package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Map;


/**
 * Action per la gestione del download di documenti digitali presenti in backoffice.
 * 
 * @version 1.8.6
 * @author Stefano.Sabbadin
 */
public class DownloadAllegatoAction extends BaseAction 
	implements SessionAware, IDownloadAction, ServletResponseAware
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6362151029936624402L;
	
	private static final String SUCCESS_DOC_FIRMATO_RISERVATO			= "successFirmaDigitaleRiservato"; 
	private static final String SUCCESS_DOC_FIRMATO_PUBBLICO			= "successFirmaDigitalePubblico";
	private static final String SUCCESS_DOC_LARGEFILE					= "successLargefile";
	
	private static final String TIPO_DOCUMENTO_FIRMATO 					= ".P7M";
	private static final String TIPO_DOCUMENTO_MARCATO_TEMPORALMENTE 	= ".TSD";
	
	private Map<String, Object> session;
	private HttpServletResponse response;
	private IDocumentiDigitaliManager documentiDigitaliManager;
	private IEventManager eventManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	
	private long id;
	@Validate(EParamValidation.GENERIC)
	private String idprg;
	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.GENERIC)
	private String url;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setUrlPage(String urlPage) {}

	@Override
	public void setCurrentFrame(String currentFrame) {}

	public void setDocumentiDigitaliManager(
			IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}
	
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public IPageManager getPageManager() {
		return this.pageManager;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdprg() {
		return idprg;
	}

	public void setIdprg(String idprg) {
		this.idprg = idprg;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Restituisce lo stream contenente un documento digitale pubblico.
	 */
	public String downloadDocumentoPubblico() {
		String target = SUCCESS;
		
		try {
			//FileType file = this.documentiDigitaliManager.getDocumentoPubblico(this.idprg, this.getId());
			FileType file = getDownloadDocumentoPubblico(idprg, id, this);
			
			target = this.download(file, false);
			if( ERROR.equals(target) ) {
				// problemi nel recuperare il file per documento pubblico
				this.addActionError(this.getText("Errors.fileDownload.noFile"));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				target = INPUT;
			}
		} catch (Exception t) {
			ApsSystemUtils.logThrowable(t, this, "downloadDocumentoPubblico");
			ExceptionUtils.manageExceptionError(t, this);
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
	    return target;
	}
		
	/**
	 * Restituisce lo stream contenente un documento digitale riservato.
	 */
	public String downloadDocumentoRiservato() {
		String target = INPUT;
		
		UserDetails user = this.getCurrentUser();
		if (null != user && !user.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			target = this.downloadRiservatoAction(user.getUsername(), "downloadDocumentoRiservato");
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		
		return target;
	}
	
	/**
	 * 
	 */
	public String viewInvito() {
		return "successFirmaDigitaleInvito"; 
	}
	
	/**
	 * Restituisce lo stream contenente un documento digitale riservato.
	 */
	public String downloadDocumentoInvito() {
		String target = INPUT;

		// recupera l'utente loggato
		String username = this.getCurrentUser().getUsername();
		boolean isAdmin = SystemConstants.ADMIN_USER_NAME.equalsIgnoreCase(username)
				  		  || SystemConstants.SERVICE_USER_NAME.equalsIgnoreCase(username);
		if(isAdmin) {
			this.addActionError(this.getText("Errors.fileDownload.noRights"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			return INPUT;
		}

		try {
			if(StringUtils.isNotEmpty(codice)) {
				// se NON e' un utente di sistema (admin, service, guest, etc) allora scarica il documento di invito
				boolean isRiservato = isDownloadInvitoRiservato(idprg, id, username, codice);
				target = (isRiservato
						? downloadRiservatoAction(username, "downloadDocumentoInvito")
						: downloadDocumentoPubblico()
				);
			} else {
				this.addActionError(this.getText("Errors.fileDownload.noRights"));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				target = INPUT;
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumentoInvito");
			ExceptionUtils.manageExceptionError(e, this);
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		
	    return target;
	}
	
	/**
	 * restituisce il file di un documento riservato/pubblico
	 * @throws ApsException 
	 */
	private static FileType getDownloadDocumento(
			String idprg,
			long iddoc,
			String username,
			BaseAction action) throws ApsException
	{
		FileType file = null;
		
		// in caso di usente "admin" non si permette il donwload
		boolean isAdmin = SystemConstants.ADMIN_USER_NAME.equalsIgnoreCase(username)
				  		  || SystemConstants.SERVICE_USER_NAME.equalsIgnoreCase(username);
		if(isAdmin) 
			return file;
		
		// se lo user e' GUEST allora il download deve sempre essere pubblico
		if(SystemConstants.GUEST_USER_NAME.equalsIgnoreCase(username)) {
			username = null;
		}

		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		IDocumentiDigitaliManager documentiDigitaliManager = (IDocumentiDigitaliManager) ctx
				.getBean(PortGareSystemConstants.DOCUMENTI_DIGITALI_MANAGER);
		
		idprg = (StringUtils.isEmpty(idprg) ? CommonSystemConstants.ID_APPLICATIVO_GARE : idprg);
		
		if( !StringUtils.isEmpty(username) ) {
			file = documentiDigitaliManager.getDocumentoRiservato(idprg, iddoc, username);
		} else {
			file = documentiDigitaliManager.getDocumentoPubblico(idprg, iddoc);
		}
		
		return file;
	}

	/**
	 * restituisce il file di un documento pubblico
	 * @throws ApsException 
	 */
	public static FileType getDownloadDocumentoPubblico(
			String idprg,
			long iddoc,
			BaseAction action) throws ApsException
	{
		return getDownloadDocumento(idprg, iddoc, null, action); 
	}
	
	/**
	 * restituisce il file di un documento riservato
	 * @throws ApsException 
	 */
	public static FileType getDownloadDocumentoRiservato(
			String idprg,
			long iddoc,
			String username,
			BaseAction action) throws ApsException
	{
		return getDownloadDocumento(idprg, iddoc, username, action); 
	}

	/**
	 * esegue il download di un documento di invito
	 * @throws ApsException 
	 */
	public static FileType getDownloadDocumentoInvito(
			String idprg,
			long iddoc,
			String username,
			String codice,
			BaseAction action) throws ApsException
	{
		boolean isRiservato = isDownloadInvitoRiservato(idprg, iddoc, username, codice);
		return (isRiservato 
				? getDownloadDocumentoRiservato(idprg, iddoc, username, action)
				: getDownloadDocumentoPubblico(idprg, iddoc, action)
		);
	}
	
	/**
	 * verifica se il documento di invito da scaricare e' pubblico o riservato
	 */
	private static boolean isDownloadInvitoRiservato(
			String idprg,
			long iddoc,
			String username,
			String codice)
	{
		boolean riservato = false;
		
		// in caso di usente "admin" non si permette il donwload
		boolean isAdmin = SystemConstants.ADMIN_USER_NAME.equalsIgnoreCase(username)
				  		  || SystemConstants.SERVICE_USER_NAME.equalsIgnoreCase(username);
		if(isAdmin) 
			return riservato;
		
		try {
			// recupera i manager necessari
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ctx
				.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
			IBandiManager bandiManager = (IBandiManager) ctx
				.getBean(PortGareSystemConstants.BANDI_MANAGER);
			
			// verifica se l'opzione DOCINVITOPUBBLICA e' attiva...
			if( !customConfigManager.isVisible("GARE", "DOCINVITOPUBBLICA") ) {
				riservato = true;
			} else {
				if(SystemConstants.GUEST_USER_NAME.equalsIgnoreCase(username)) {
					username = null;
				}
				
				if(StringUtils.isEmpty(username)) {
					// nessuno user indica sempre un download pubblico
					riservato = false;
				} else {
					// prima di scaricare il documento, in caso i oggetto di tipo "GARA", 
					// e' necessario verificare lo stato della gara
					// se la gara e' in corso allora il documento puo' essere scaricato solo dall'utente loggato
					// se la gara e scaduta chiunque puo' scaricare il documento (quindi il codice seguente si puo' utilizzare)
					DettaglioGaraType gara = (StringUtils.isNotEmpty(codice)
							? bandiManager.getDettaglioGara(codice)
							: null
					);
					Date now = new Date();
					boolean inCorso = (gara != null) && now.before(gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta());
					boolean scaduta = (gara != null) && now.after(gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta());
					
					if(inCorso)
						riservato = true;
					if(scaduta) 
						riservato = false;
				}
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, null, "isDownloadInvitoRiservato");
		}
		return riservato;
	}
	
	/**
	 * download generalizzata di un documento per documenti riservati
	 */
	private String downloadRiservatoAction(String username, String sender) {
		String target = SUCCESS;
		
		// recupera il file relativo alla richiesta di download...
		FileType file = null;
		try {
			file = getDownloadDocumentoRiservato(idprg, id, username, this);
			if(file == null) {
				this.addActionError(this.getText("Errors.fileDownload.noRights"));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				target = INPUT;
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, sender);
			ExceptionUtils.manageExceptionError(e, this);
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}			

		////////////////////////////////////////////////////
		// Gestione del download dei file firma digitale... 
		// ...demanda la gestione a InfoFirmaDigitaleAction 
		if (SUCCESS.equals(target)) {
			if (isDocumentoFirmato(file)) {
				return this.download(file, true);
			}
		}
		
		////////////////////////////////////////////////////
		// Gestione standard del download di un file...
		if (SUCCESS.equals(target)) {
			Event evento = new Event();
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DOWNLOAD_DOCUMENTO_RISERVATO);
			String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}
			evento.setIpAddress(ipAddress);
			evento.setSessionId(this.getRequest().getSession().getId());
	
			try {
				evento.setDestination(this.idprg + "," + this.id);
				evento.setMessage("Download documento riservato (applicativo " + this.idprg + ", id " + this.id + ")");
	
				if( !StringUtils.isEmpty(username) ) {
					evento.setUsername(username);
					target = this.download(file, true);
				}
				
				if (ERROR.equals(target)) {
					/* --- utente cerca di scaricare un documento che non gli appartiene --- */
					this.addActionError(this.getText("Errors.fileDownload.noRights"));
					session.put("ACTION_OBJECT", this);
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage("Documento inesistente o non visibile");
					target = INPUT;
				}
			} catch (Exception e) { 	//(ApsException t) {
				evento.setError(e);
				ApsSystemUtils.logThrowable(e, this, sender);
				ExceptionUtils.manageExceptionError(e, this);
				session.put("ACTION_OBJECT", this);
				target = INPUT;
			} finally{
				this.eventManager.insertEvent(evento);
			}
		}
	    return target;
	}
	
	/**
	 * download dello stream i un allegato, pubblico o riserva, con o senza firma  
	 */
	private String download(FileType file, boolean documentoRiservato) {
		String target = SUCCESS;
		if(file == null) {
			target = ERROR;
		} else {
			if(isDocumentoFirmato(file)) {
				// documento firmato o marcato temporalmente (.P7M, .TSD)
				target = (documentoRiservato 
						  ? SUCCESS_DOC_FIRMATO_RISERVATO 
					  	  : SUCCESS_DOC_FIRMATO_PUBBLICO
				);
			} else {
				// documento NON firmato
				if(StringUtils.isNotEmpty(file.getUrl())) {
					// scarica un LARGEFILE dal servizio https://apache-dev.maggioli.it/M-LargeFile
					// NB: per LARGEFILE si permette solo il download su tab del browser separato (<a ... target=_blank/>)
					this.url = file.getUrl();
					target = SUCCESS_DOC_LARGEFILE;
				} else {
					this.filename = file.getNome();
					this.contentType = "application/octet-stream";
					this.inputStream = new ByteArrayInputStream(file.getFile());
					target = SUCCESS;
				}
			}
		}
		return target;
	}

	/**
	 * ... 
	 */
	private boolean isDocumentoFirmato(FileType file) {
		if(file != null) {
			String filename = file.getNome().toUpperCase();
			return filename.endsWith(TIPO_DOCUMENTO_FIRMATO) || 
			   	   filename.endsWith(TIPO_DOCUMENTO_MARCATO_TEMPORALMENTE);
		} else {
			return false;
		}
	}

	@Override
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest(); 
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPageManager pageManager = this.getPageManager();
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		
		PageURL url = this.urlManager.createURL(reqCtx);
	
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}
	
}
