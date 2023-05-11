package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.dgue;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.BaseDgueAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.JwtTokenUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Gestione link DGUE da visualizzare nelle buste amministrativa, prequalifica, qform
 *   
 */
public class DgueBuilder {
	
	private static final String DGUE 					= "dgue";
	private static final String DGUE_ENC_DATA 			= "enc-data";
	private static final String DGUE_URL_MDGUE 			= "dgue-url-mdgue";	
	private static final String DGUE_JWTKEY_EXPIRATION 	= "dgue-jwtkey-expiration";
	private static final String DGUE_JWTKEY 			= "dgue-jwtkey";
	private static final String DGUE_SYMKEY 			= "dgue-symkey";
	private static final int DGUE_JWT_SECRET_LENGTH 	= 64;
	private static final Long MINUTES_TO_MILLIS 		= 60000l;
	
	// action target for redirect to DGUE  
	private static final String REDIRECT_TO_DGUE		= "redirectToDGUE";
		
	private ConfigInterface configManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;

	private String codiceGara;
	private String codiceLotto;
	private String dgueLinkActionRti;		// i.e. /do/FrontEnd/GareTel/dgueRti.action
	private String dgueLinkAction;			// i.e. /do/FrontEnd/GareTel/dgue.action
	private GestioneBuste gestioneBuste;
	private int tipoBusta;
	
	// info calcolate e restituite come risultato
	private Long iddocdig;
	private String dgueLinkSecondHref;		// url di accesso a DGUE con token
	private String dgueLinkThirdHref;		// url di accesso a DBUG senza token
	private String dgueLinkSecond;			// <a href=[dgueLinkSecondHref] ...
	private String dgueLinkThird;			// <a href=[dgueLinkThirdHref] ...
	private String url;						// url di redirect to DGUE
	private String target;
	
	// parametri Dgue per la generazione del jwtToken
	private AppParam paramDgueSymKey;
	private AppParam paramDgueJwtKey;
	private AppParam paramDgueJwtKeyExpiration;
	private AppParam paramDgueUrlMDgue;

	public Long getIddocdig() {
		return iddocdig;
	}

	public String getDgueLinkSecondHref() {
		return dgueLinkSecondHref;
	}

	public String getDgueLinkThirdHref() {
		return dgueLinkThirdHref;
	}

	public String getDgueLinkSecond() {
		return dgueLinkSecond;
	}

	public String getDgueLinkThird() {
		return dgueLinkThird;
	}

	public String getUrl() {
		return url;
	}
	
	public String getTarget() {
		return target;
	}

	/**
	 * costruttore 
	 */
	public DgueBuilder() {
		//ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		//configManager = (ConfigInterface) ctx.getBean(SystemConstants.BASE_CONFIG_MANAGER);
		//appParamManager = (IAppParamManager) ctx.getBean(PortGareSystemConstants.APPPARAM_MANAGER);
		configManager = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, ServletActionContext.getRequest());
		appParamManager = (IAppParamManager) ApsWebApplicationUtils.getBean(PortGareSystemConstants.APPPARAM_MANAGER, ServletActionContext.getRequest());
		eventManager = (IEventManager) ApsWebApplicationUtils.getBean(PortGareSystemConstants.EVENTI_MANAGER, ServletActionContext.getRequest());
	}
	
	public DgueBuilder setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
		return this;
	}
	
	public DgueBuilder setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
		return this;
	}
	
	public DgueBuilder setDgueLinkActionRti(String value) {
		this.dgueLinkActionRti = value;
		return this;
	}
	
	public DgueBuilder setDgueLinkAction(String value) {
		this.dgueLinkAction = value;
		return this;
	}

	public DgueBuilder setGestioneBuste(GestioneBuste buste) {
		this.gestioneBuste = buste;
		return this;
	}
	
	public DgueBuilder setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
		return this;
	}
	
	public DgueBuilder setIddocdig(Long iddocdig) {
		this.iddocdig = iddocdig;
		return this;
	}

	/**
	 * genera i link "second" e "third" per la gestione del DGUE 
	 */
	public DgueBuilder initLink23() throws ApsException, Throwable {
		target = BaseAction.SUCCESS;
		
		BustaDocumenti busta = gestioneBuste.getBusta(tipoBusta);
		WizardPartecipazioneHelper partecipazione = gestioneBuste.getBustaPartecipazione().getHelper();
		String codice = (StringUtils.isNotEmpty(codiceLotto) ? codiceLotto : codiceGara);
		DettaglioGaraType dettGara = gestioneBuste.getDettaglioGara();
		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		
		iddocdig = new Long(findIdDocDgue());
		
		// genera i link "second" e "third" per la gestione del DGUE
		String baseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
		String urlServizio = baseUrl + (baseUrl.endsWith("/") ? dgueLinkActionRti.substring(1) : dgueLinkActionRti);
		ApsSystemUtils.getLogger().debug("urlServizio: {}", urlServizio);
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("urlServizio", urlServizio);
		
		updateDgueParams();
		
		byte[] chiaveSimmetrica = Base64.decode(paramDgueSymKey.getValue());
		
		Date dt = null;
		if(busta.getTipoBusta() == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			dt = InitIscrizioneAction.calcolaDataOra(
					dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
					dettGara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),
					true);
		} else {
			dt = InitIscrizioneAction.calcolaDataOra(
					dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
					dettGara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(),
					true);
		}
		
		ApsSystemUtils.getLogger().debug("DataTermine per token: {}", dt);
		
		// prepare il json per JwtToken
		JSONObject json = new JSONObject();
		json.put(BaseDgueAction.JSON_CODICE, codice);
		json.put(BaseDgueAction.JSON_PROGRESSIVO_OFFERTA, partecipazione.getProgressivoOfferta());
		json.put(BaseDgueAction.JSON_IDPRG, CommonSystemConstants.ID_APPLICATIVO_GARE);
		if(iddocdig != null) {
			// se esiste il documento del DGUE "abilito" il DGUE...
			json.put(BaseDgueAction.JSON_IDDOCDIG, iddocdig);
			
			Cipher cipher = SymmetricEncryptionUtils.getEncoder(chiaveSimmetrica);
			data.put(DGUE_ENC_DATA, Base64.encode(cipher.doFinal(json.toString().getBytes())));
			
			String clickHere = action.getI18nLabel("LABEL_DGUE_LINK_CLICK");
			long expirationInMillis = System.currentTimeMillis() + 5 * MINUTES_TO_MILLIS;
			if(dt != null) {
				expirationInMillis = dt.getTime();
			}
	
			// genera i link "second" e "third" per il DGUE
			String jwtToken = JwtTokenUtilities.generateToken(BaseDgueAction.TOKEN_SUBJECT, expirationInMillis, paramDgueJwtKey.getValue(),data);
			String urlWithToken = buildDgueURL(paramDgueUrlMDgue.getValue(), BaseDgueAction.REST_URL_MODIFICATION, jwtToken);
			String urlWithoutToken = urlWithToken.substring(0, urlWithToken.indexOf("?"));
			this.dgueLinkSecondHref = urlWithToken;
			this.dgueLinkThirdHref = urlWithoutToken;
			this.dgueLinkSecond = "<a href='" + urlWithToken + "' target='blank'>" + clickHere + "</a>";
			this.dgueLinkThird = "<a href='" + urlWithoutToken + "' target='blank'>" + clickHere + "</a>";		
		} else {
			// documento marcato "dgue" non trovato
			// traccia solo in caso di QForm
//			if(busta.getHelperDocumenti().isGestioneQuestionario()) {
//				target = traceDgueRequestMancante();
//			}
		}	
		
		return this;
	}
	
	/**
	 * redirect to DGUE application 
	 * 
	 * @return "redirectToDGUE" se il metodo termina con successo e in "url" viene restituito il link del DGUE
	 * 		   "portalError"    se il metodo incontra qualche errore
	 */
	public DgueBuilder redirectToDGUE() {
		target = BaseAction.SUCCESS;
		
		ApsSystemUtils.getLogger().debug("redirectToDGUE called");
		long start = System.currentTimeMillis();

		WizardPartecipazioneHelper partecipazione = gestioneBuste.getBustaPartecipazione().getHelper();
		String codice = (StringUtils.isEmpty(this.codiceGara) ? this.codiceLotto : this.codiceGara);
		
		String baseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
		String urlServizio = baseUrl + (baseUrl.endsWith("/") ? dgueLinkAction.substring(1) : dgueLinkAction);
		ApsSystemUtils.getLogger().debug("urlServizio: {}",urlServizio);
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("urlServizio", urlServizio);
		
		byte[] chiaveSimmetrica = null;
		Long durationOfTicket = Long.valueOf(5); // parameter
		try {
			updateDgueParams();
			
			chiaveSimmetrica = Base64.decode(paramDgueSymKey.getValue());
			durationOfTicket = Long.valueOf(paramDgueJwtKeyExpiration.getValue() != null ? paramDgueJwtKeyExpiration.getValue() : "5") * MINUTES_TO_MILLIS;
		} catch (NullPointerException e) {
			ApsSystemUtils.getLogger().error("Errore nella apertura della pagina.",e);
			ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (NoSuchAlgorithmException e) {
			ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (NoSuchProviderException e) {
			ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Base64DecodingException e) {
			ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		// prepara il JSON per il JwtToken per produre l'url del DGUE
		JSONObject json = new JSONObject();
		json.put(BaseDgueAction.JSON_USER, gestioneBuste.getUsername());
		json.put(BaseDgueAction.JSON_CODICE, codice);
		json.put(BaseDgueAction.JSON_PROGRESSIVO_OFFERTA, partecipazione.getProgressivoOfferta());
		json.put(BaseDgueAction.JSON_IDPRG, CommonSystemConstants.ID_APPLICATIVO_GARE);
		if(iddocdig != null) {
			json.put(BaseDgueAction.JSON_IDDOCDIG, iddocdig);
			
			try {
				long expiration = System.currentTimeMillis() + durationOfTicket;
				Cipher cipher = SymmetricEncryptionUtils.getEncoder(chiaveSimmetrica);
				data.put(DGUE_ENC_DATA,Base64.encode(cipher.doFinal(json.toString().getBytes())));
				String jwtToken = JwtTokenUtilities.generateToken(BaseDgueAction.TOKEN_SUBJECT, expiration, paramDgueJwtKey.getValue(), data);
				this.url = buildDgueURL(paramDgueUrlMDgue.getValue(), BaseDgueAction.REST_URL_MODIFICATION, jwtToken);
				ApsSystemUtils.getLogger().trace("url: {}", this.url);
				target = REDIRECT_TO_DGUE;
				
			} catch (Exception e) {
				ApsSystemUtils.getLogger().error("Errore nella cifratura dei dati nel jwt", e);
				ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} else {
			// documnto marcato "dgue" non trovato
			target = traceDgueRequestMancante();
		}
		
		ApsSystemUtils.getLogger().debug("redirectToDGUE - Time execution: {}ms", (System.currentTimeMillis() - start));
		
		return this;
	}

	/**
	 * ... 
	 */
	private String buildDgueURL(String url, String restUrlVisualization, String jwtToken) {
		String baseUrl = url.endsWith("/") ? url : url + "/";
		return baseUrl + restUrlVisualization + "?t=" + jwtToken;
	}

	/**
	 * recupera i parametri del Dgue 
	 * @throws ApsSystemException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	private void updateDgueParams() throws ApsSystemException, NoSuchAlgorithmException, NoSuchProviderException {		
		paramDgueSymKey = null;
		paramDgueJwtKey = null;
		paramDgueJwtKeyExpiration = null;
		paramDgueUrlMDgue = null;
		List<AppParam> appParamList = appParamManager.getAppParamsByCategory(DGUE);
		for(AppParam param : appParamList) {
			if(DGUE_SYMKEY.equals(param.getName())) {
				paramDgueSymKey = param;
			} else if(DGUE_JWTKEY.equals(param.getName())) {
				paramDgueJwtKey = param;
			} else if(DGUE_JWTKEY_EXPIRATION.equals(param.getName())) {
				paramDgueJwtKeyExpiration = param;
			} else if(DGUE_URL_MDGUE.equals(param.getName())) {
				paramDgueUrlMDgue = param;
			}
		}
		
		if(StringUtils.isBlank(paramDgueSymKey.getValue())) {
			ApsSystemUtils.getLogger().debug("Param dgue-symkey dgue-security not found, update it");
			paramDgueSymKey.setValue(Base64.encode(EncryptionUtils.generateAesKey()));
		}
		if(StringUtils.isBlank(paramDgueJwtKey.getValue())) {
			ApsSystemUtils.getLogger().debug("Param dgue-jwtkey dgue-security not found, update it");
			paramDgueJwtKey.setValue(RandomStringUtils.random(DGUE_JWT_SECRET_LENGTH,true,true));
		}
		
		// update datum
		appParamManager.updateAppParams(appParamList);
	}		

	/**
	 * trova il documento marcato con idStampa="DGUE" 
	 * @throws Exception 
	 */
	private long findIdDocDgue() throws Exception {
		BustaDocumenti busta = gestioneBuste.getBusta(tipoBusta);
		
		// NB: 
		// solo per il documento DGUE non va verificata l'esistenza tra i docuemnti di gara
		// il documento richiesto DGUE per la gara e' necessario lato BO 
		// per poter poi inserire nella busta amministrativa il documento DGUE!
		long id = -1;
		for (DocumentazioneRichiestaType doc : busta.getDocumentiRichiestiDB()) {
			ApsSystemUtils.getLogger().debug("id: {}, idfacsimile: {}, idstampa(): {}", doc.getId(), doc.getIdfacsimile(), doc.getIdstampa());
			if(DGUE.equalsIgnoreCase(doc.getIdstampa())) {
				id = doc.getIdfacsimile();
				break;
			}
		}

		// ...se non si trova nella busta si cerca tra i documenti della gara...
		if (busta.getHelperDocumenti().isGestioneQuestionario()
				&& (tipoBusta == BustaGara.BUSTA_AMMINISTRATIVA || tipoBusta == BustaGara.BUSTA_PRE_QUALIFICA))
			id = gestioneBuste.getIdDgueRequestDocument();

		return id;
	}

	
	/**
	 * traccia un messaggio di errore per i QForm in caso non sia stato allegato ai documenti il "DGUE Request" 
	 */
	private String traceDgueRequestMancante() {
		target = CommonSystemConstants.PORTAL_ERROR;
		ActionSupport action = (ActionSupport)ActionContext.getContext().getActionInvocation().getAction();
		String msg = action.getText("Errors.dgue.DgueRequestNotFound");
		action.addActionError(msg);
		ApsSystemUtils.getLogger().error("DgueBuilder: " + msg);
		Event evento = null;
		try {
			evento = new Event();
			evento.setUsername(gestioneBuste.getUsername());
			evento.setDestination(codiceGara);
			evento.setLevel(Event.Level.ERROR);
			evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
			evento.setIpAddress(null);
			evento.setSessionId(null);
			evento.setMessage("Accesso alla funzione Dgue: (busta = " + tipoBusta + ")");
			evento.setDetailMessage(msg);
		} catch (Exception e) {
			//...
		} finally {
			if(evento != null) {
				eventManager.insertEvent(evento);
			}
		}
		return target;
	}
	
}
