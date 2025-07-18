package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.JwtTokenUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.utils.Base64;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * ... 
 */
public class DgueRequestAction extends BaseDgueAction {
    /**
	 * UID
	 */
	private static final long serialVersionUID = 4642930160647511277L;

    private static final String BACK_URL = "do/FrontEnd/DGUE/requestXmlFile.action";

    //Url di redirect utilizzato sull'invio del token
    @ValidationNotRequired
    private String url;

    @Validate(EParamValidation.INTERO)
    private String iddocdig;

    @ValidationNotRequired
    protected String      json;
    // Contenuto della richiesta inviato all'm-dgue
    protected InputStream inputStream;

    /**
     * Ritorna l'xml del documento (idprg e iddocdig contenuti nel token)
     * @return
     */
    public String sendXmlToOpenOnDgue() {
        String target = SUCCESS;

        try {
            addResponseHeader();
            //La prima richiesta inviata dal browser � di tipo OPTION
            //Questa richiesta serve al browser per capire che tipo di richieste sono accettate, in questo caso
            //GET e OPTION (come specificato nell'addResponseHeader)
            if (HttpMethod.OPTIONS.equals(getRequest().getMethod()))
                return SUCCESS;

            JSONObject infoOut = new JSONObject();
            JSONObject decodedData = getDecodedData(false);  //Estraggo il token dall'header e decodifico i dati
//            checkAuthentication(decodedData);
            //Recupero il file da inviare all'm-dgue
            FileType requestFileToSend = getDgueRequestDocument(decodedData);
            //Inserisco il file sbustato nel json
            infoOut.put(JSON_DGUE_REQUEST, Base64.encode(getUnpackedFileBytes(requestFileToSend)));

            JSONObject jsonData = new JSONObject();
            jsonData.put(JSON_DATA, infoOut);
            json = jsonData.toString();

        } catch (SignatureException | MalformedJwtException | ExpiredJwtException e) {
            logger.error("RequestError", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("GeneralError", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (inputStream == null
                && StringUtils.isNotEmpty(json))
                inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            else
                inputStream = new InputStream() {
                    @Override
                    public int read() {
                        return -1;
                    }
                };
        }

        return target;
    }

    /**
     * Invio il token al dgue con idprg=PA, iddocdig passato dalla jsp e backurl = il metodo sendXmlToOpenOnDgue
     * @return
     */
    public String sendTokenToDgue() {
        String target = "redirectToDGUE";

        logger.debug("sendTokenToDgue called");
        try {
            addResponseHeader();

            Map<String, String> properties = getValidDgueProperties(true);
            
            Map<String, String> toInsertInToken = new HashMap<>();
            toInsertInToken.put(JSON_URL_SERVIZION, retrieveServiceUrl(BACK_URL));
            toInsertInToken.put(
                    DGUE_ENC_DATA
                    , Base64.encode( encode(properties, getJsonStringWithTenderInfo(this.getCurrentUser().getUsername())) )
            );

            String jwtToken = JwtTokenUtilities.generateToken(
                    TOKEN_SUBJECT
                    , getExpirationFromProperties(properties)
                    , properties.get(DGUE_JWTKEY)
                    , toInsertInToken
            );
            url = buildDgueURL(properties, REST_URL_VISUALIZATION, jwtToken);
            logger.trace("url: {}", url);

        } catch (Exception e) {
            logger.error("sendTokenToDgue(): ", e);
            target = CommonSystemConstants.PORTAL_ERROR;
        }

        return target;
    }

    private String getJsonStringWithTenderInfo(String username) {
        JSONObject jsonWithInfo = new JSONObject();
        if(StringUtils.isNotEmpty(username)) 
        	jsonWithInfo.put(JSON_USER, username);
        jsonWithInfo.put(JSON_CODICE, codice);
        jsonWithInfo.put(JSON_IDPRG, CommonSystemConstants.ID_APPLICATIVO_GARE);
        jsonWithInfo.put(JSON_IDDOCDIG, iddocdig);
        return jsonWithInfo.toString();
    }

    public String getUrl() {
        return url;
    }
    
    public void setIddocdig(String iddocdig) {
        this.iddocdig = iddocdig;
    }
    
    public String getJson() {
        return json;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
}
