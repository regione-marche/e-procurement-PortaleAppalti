package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.UserDetails;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.sign.DigitalSignatureException;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.JwtTokenUtilities;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//Anche la classe OpenpageDocumentiBustaAction dovrebbe estendere questa classe perch� crea url per l'mdgue e invia il token
public abstract class BaseDgueAction extends EncodedDataAction implements ServletResponseAware {

    protected final Logger log = LoggerFactory.getLogger(BaseDgueAction.class);

    /**
     * DGUE
     */
    //I seguenti percorsi rest non devono iniziare per "/"
    public static final String REST_URL_VISUALIZATION   = "quadro-generale-appalti";
    public static final String REST_URL_MODIFICATION    = "dgue-home-portale";

    public static final String TOKEN_SUBJECT          = "PortaleAppalti";
    protected static final String DGUE_ENC_DATA          = "enc-data";
    protected static final String DGUE_URL_MDGUE         = "dgue-url-mdgue";
    protected static final String DGUE                   = "dgue";
    //Minuti che il token jwt rimane attivo, in caso non fosse valorizzato il default attuale � 5 minuti 8DEFAULT_EXPIRATION_MINUTES)
    protected static final String DGUE_JWTKEY_EXPIRATION = "dgue-jwtkey-expiration";
    //Property contente la chiave jwt
    //Durante la fase di generazione, in caso la property fosse vuota, viene valorizzata.
    protected static final String DGUE_JWTKEY            = "dgue-jwtkey";
    //Property contenente la chiave di crittografia simmetrica dei dati
    //Durante la fase di generazione, in caso la property fosse vuota, viene valorizzata.
    protected static final String DGUE_SYMKEY            = "dgue-symkey";
    protected static final int    DGUE_JWT_SECRET_LENGTH = 64;
    protected static final String JSON_LOTTI                 = "lotti";
    //Nodo che contiene l'anagraficaOE
    protected static final String JSON_INFO_OE               = "infoOE";
    //Contenuto del file da passare al dgue
    public static final String JSON_DGUE_REQUEST          = "dgueRequest";
    public static final String JSON_CODICE                = "codice";
    public static final String JSON_PROGRESSIVO_OFFERTA = "progressivoOfferta";
    //Utente che ha effettuato la richiesta
    public static final String JSON_USER                = "usr";
    protected static final String JSON_ENC_DATA            = "enc-data";
    //Nodo del json contenente il json con file e tutte le informazioni da inviare al dgue
    protected static final String JSON_DATA                  = "data";
    //Id del documento da passare al dgue
    public static final String JSON_IDDOCDIG              = "iddocdig";
    //idprg del documento da passare al dgue
    public static final String JSON_IDPRG                 = "idprg";
    //Url della action da chiamare dopo l'invio del token da parte del Portale
    protected static final String JSON_URL_SERVIZION         = "urlServizio";
    //Categoria dgue nel database
    protected static final String DGUE_CATEGORY              = "dgue";
    protected static final long   DEFAULT_EXPIRATION_MINUTES = 5;

    //Classe che effettua lo sbustamento in case di richieste firmate
    private static final DigitalSignatureChecker signatureChecker = new DigitalSignatureChecker();

    //Utilizzata per impostare una risposta quando è il dgue a inviare una request al portale
    protected HttpServletResponse response;

    //Utilizzata per recuperarsi le property di categoria dgue dal db
    protected IAppParamManager               appParamManager;
    //Utilizzata in alcuni casi, quando � necessario essere loggati
    protected IAuthenticationProviderManager authenticationProvider;
    //Manager per recuperarmi il file della richiesta
    protected IDocumentiDigitaliManager documentiDigitaliManager;
    //Manager che serve a far ritornare l'url del servizio corrente
    protected ConfigInterface           configManager;

    /**
     * Se non presente l'autenticazione ritorna errore.
     * @param decodedData
     * @throws Exception
     */
    protected String checkAuthentication(JSONObject decodedData) throws Exception {
        String username = decodedData.getString(JSON_USER);
        UserDetails ud = authenticationProvider.getUser(username);
        if (ud == null) {
            log.warn("Utente non identificato ha cercato di accedere ai dati DGUE. Username: {}", username);
            throw new Exception("Utente non identificato ha cercato di accedere ai dati DGUE.");
        }
        return username;
    }

    /**
     * Ritorno il token jwt decodificato e convertito in json object
     * @return
     * @throws Exception
     */
    protected JSONObject getDecodedData(boolean forceUpdate) throws Exception {
        //Ritorno il token jwt contenuto nell'header
        String jwt = getJwtFromHeader();
        //inutile effettuare la query a db per le properties nel caso in cui manchi Authorization Header o che questi
        // sia presente ma senza valori
        Map<String, String> properties = getValidDgueProperties(forceUpdate);
        //Se non esistono le chiavi di decifratura a db, ritorno errore
        if (properties.get(DGUE_SYMKEY) == null || properties.get(DGUE_JWTKEY) == null)
            throw new Exception("Missing params with category " + DGUE_CATEGORY);

        //Estraggo il contenuto del jwt token in un json
        JSONObject body = getJsonFromJwtToken(properties, jwt);
        //Il nodo root del json � JSON_DATA
        JSONObject dataFromInput = (JSONObject) body.get(JSON_DATA);
        //mi faccio ritornate il json criptato
        String encodedData = dataFromInput.getString(JSON_ENC_DATA);
        log.trace("encData: {}", encodedData);
        //Decripto il json
        JSONObject decodedData = decodeData(properties, encodedData);
        log.trace("decoData: {}", decodedData);

        return decodedData;
    }

    /**
     * Recupero il json contenente il token jwt
     *
     * @param properties
     * @param jwt
     * @return
     */
    private static JSONObject getJsonFromJwtToken(Map<String, String> properties, String jwt) {
        return JSONObject.fromObject(
                JwtTokenUtilities.getBodyFromJwt(
                        JwtTokenUtilities.parseJwt(jwt, properties.get(DGUE_JWTKEY))
                )
        );
    }

    /**
     * Questo metodo serve a fare il decoupling della decifratura, in modo da intervenire qui se cambiasse il modo di
     * cifrare / decifrare i dati sensibili nel token
     *
     * @param dataToBeDecoded la stringa in base64 che deve essere decifrata
     * @return
     * @throws Exception
     */
    protected JSONObject decodeData(Map<String, String> properties, String dataToBeDecoded) throws Exception {
        Cipher cipher = SymmetricEncryptionUtils.getDecoder(Base64.decode(properties.get(DGUE_SYMKEY)));
        return JSONObject.fromObject(new String(cipher.doFinal(Base64.decode(dataToBeDecoded.getBytes()))));
    }

    /**
     * Encoding data with the given symmetric key (DGUE_SYMKEY)
     * @param properties
     * @param toEncode
     * @return
     * @throws Exception
     */
    protected byte[] encode(Map<String, String> properties, String toEncode) throws Exception {
        byte[] symmetricKey = Base64.decode(properties.get(DGUE_SYMKEY));
        Cipher cipher = SymmetricEncryptionUtils.getEncoder(symmetricKey);
        return cipher.doFinal(toEncode.getBytes());
    }

    /**
     * Estraggo il token jwt che viene inserito sull'header Authorization, dopo il Bearer.
     * @return
     * @throws Exception
     */
    protected String getJwtFromHeader() throws Exception {
        String jwt = this.getRequest().getHeader("Authorization");
        if (jwt == null) throw new IllegalArgumentException("Missing Authorization header parameter.");
        jwt = StringUtils.substringAfterLast(jwt, "Bearer ");
        if (StringUtils.isBlank(jwt)) throw new Exception("Missing Bearer value into Authorization header parameter.");
        return jwt;
    }

    /**
     * Utilizzato per specificare al browser che tipo di chiamate http sono accettate.
     */
    protected void addResponseHeader() {
        response.addHeader("Access-Control-Allow-Origin", "*");//TODO non molto sicuro sarebbe meglio avere un
        // mapping degli url chiamanti
        response.addHeader("Vary", "Origin");//per dare modo al chiamante di sapere che la origin potrebbe variare
        response.addHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
        response.addHeader("Access-Control-Allow-Methods", HttpMethod.OPTIONS + "," + HttpMethod.GET);
    }

    /**
     * Ritorno le properties del dgue. Se passato true verranno generate le chiavi per cryptare in caso non presenti.
     * @param forceUpdateKeys
     * @return
     * @throws Exception
     */
    protected Map<String, String> getValidDgueProperties(boolean forceUpdateKeys) throws Exception {
        List<AppParam> appParamList = appParamManager.getAppParamsByCategory(DGUE);
        return CollectionUtils.isNotEmpty(appParamList)
               ? appParamList.stream()
                       .peek(param ->  {
                           if (forceUpdateKeys) checkAndUpdateProperties(param);
                       }).collect(Collectors.toMap(AppParam::getName, AppParam::getValue))
               : null;
    }

    /**
     * Controlla se la property � un chiave di cifratura, se presente e se vuote le valorizza.
     * @param param
     */
    protected void checkAndUpdateProperties(AppParam param) {
        try {
            boolean needsToUpdate =
                    updateIfEmpty(param, DGUE_SYMKEY, Base64.encode(EncryptionUtils.generateAesKey()))
                            || updateIfEmpty(param, DGUE_JWTKEY, RandomStringUtils.random(DGUE_JWT_SECRET_LENGTH,
                                                                                          true, true));
            if (needsToUpdate) {
                log.info("Param {} dgue-security not found, update it", param.getName());
                appParamManager.updateAppParams(Collections.singletonList(param));
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'update delle properties del dgue");
        }
    }

    /**
     * Se la property � vuota la aggiorno con il valore specificato
     * @param param
     * @param propName
     * @param value
     * @return
     */
    private boolean updateIfEmpty(AppParam param, String propName, String value) {
        boolean toReturn = false;

        if (StringUtils.equals(param.getName(), propName)
                && StringUtils.isEmpty(param.getValue())) {
            param.setValue(value);
            toReturn = true;
        }

        return toReturn;
    }

    /**
     * Ritorna l'expiration date del token, ovvero il tempo specificato sulle properties o il DEFAULT_EXPIRATION_MINUTES se vuoto
     * (viene ritornata la data di fine validit� in millisecondi)
     * @param properties
     * @return
     */
    protected long getExpirationFromProperties(Map<String, String> properties) {
        String expirationMinute = properties.get(DGUE_JWTKEY_EXPIRATION);
        return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(
                StringUtils.isNotEmpty(DGUE_JWTKEY_EXPIRATION)
                    ? Long.parseLong(expirationMinute)
                    : DEFAULT_EXPIRATION_MINUTES
        );
    }

    /**
     * Recupero i byte del documento richiesto
     * @param decodedData
     * @return
     * @throws ApsException
     */
    protected FileType getDgueRequestDocument(JSONObject decodedData) throws ApsException {
        return documentiDigitaliManager.getDocumentoPubblico(
                decodedData.get(JSON_IDPRG).toString()
                , decodedData.getLong(JSON_IDDOCDIG)
        );
    }

    /**
     * Sbusta il file in caso di file firmato
     * @param requestFileToSend
     * @return
     * @throws DigitalSignatureException
     */
    protected byte[] getUnpackedFileBytes(FileType requestFileToSend) throws DigitalSignatureException {
        return StringUtils.endsWithIgnoreCase(requestFileToSend.getNome(), ".p7m")
                       || StringUtils.containsIgnoreCase(requestFileToSend.getNome(), ".p7m")
               ? signatureChecker.getContent(requestFileToSend.getFile())
               : requestFileToSend.getFile();
    }

    /**
     * Recupero l'url del servizio attuale (Url del Portale)
     * @param actionUrl
     * @return
     */
    public String retrieveServiceUrl(String actionUrl) {
        String baseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
        return StringUtils.endsWith(baseUrl, "/")
               ? baseUrl + actionUrl
               : "/" + baseUrl + actionUrl;
    }

    protected String buildDgueURL(Map<String, String> properties, String restUrlVisualization, String jwtToken) {
        String baseUrl = properties.get(DGUE_URL_MDGUE).endsWith("/")
                         ? properties.get(DGUE_URL_MDGUE)
                         : properties.get(DGUE_URL_MDGUE) + "/";
        return baseUrl + restUrlVisualization + "?t=" + jwtToken;
    }

    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }
    public void setAuthenticationProvider(IAuthenticationProviderManager authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
    public void setDocumentiDigitaliManager(IDocumentiDigitaliManager documentiDigitaliManager) {
        this.documentiDigitaliManager = documentiDigitaliManager;
    }
    public void setConfigManager(ConfigInterface configManager) {
        this.configManager = configManager;
    }


    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

}
