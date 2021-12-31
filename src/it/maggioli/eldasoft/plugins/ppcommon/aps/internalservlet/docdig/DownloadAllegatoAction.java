package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig;

import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;


/**
 * Action per la gestione del download di documenti digitali presenti in backoffice.
 * 
 * @version 1.8.6
 * @author Stefano.Sabbadin
 */
public class DownloadAllegatoAction extends BaseAction implements SessionAware,IDownloadAction,ServletResponseAware{
	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6362151029936624402L;
	
	private Map<String, Object> session;
	private HttpServletResponse response;
	private IDocumentiDigitaliManager documentiDigitaliManager;
	private IEventManager eventManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	private ICustomConfigManager customConfigManager;
	
	private long id;
	private String idprg;
	private InputStream inputStream;
	private String filename;	
	private String contentType;
	
	private static final String TIPO_DOCUMENTO_FIRMATO = ".P7M";
	private static final String TIPO_DOCUMENTO_MARCATO_TEMPORALMENTE = ".TSD";
	
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

	/**
	 * @param documentiDigitaliManager the documentiDigitaliManager to set
	 */
	public void setDocumentiDigitaliManager(
			IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
	}
	
	/**
	 * @param eventManager the eventManager to set
	 */
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
	
	/**
	 * @param customConfigManager the customConfigManager to set
	 */
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the idprg
	 */
	public String getIdprg() {
		return idprg;
	}

	/**
	 * @param idprg the idprg to set
	 */
	public void setIdprg(String idprg) {
		this.idprg = idprg;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * Restituisce lo stream contenente un documento digitale pubblico.
	 */
	public String downloadDocumentoPubblico() {
		String target = SUCCESS;
		
		try {
			FileType file = this.documentiDigitaliManager.getDocumentoPubblico(
					this.idprg, this.getId());
			
			target = this.download(file, false);
			if( ERROR.equals(target) ){
				/* --- problemi nel recuperare il file per documento pubblico --- */
				this.addActionError(this.getText("Errors.fileDownload.noFile"));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				target = INPUT;
			}
		} catch (ApsException t) {
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
		
		try {
		// verifica se l'opzione DOCINVITOPUBBLICA e' attiva...
			if( !this.customConfigManager.isVisible("GARE", "DOCINVITOPUBBLICA") ) {
				this.addActionError(this.getText("Errors.function.notEnabled"));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);							
				target = INPUT;
			} else { 			
				String username = this.documentiDigitaliManager.getUsernameDocumentoRiservato(
						this.idprg, this.id);
				if(!StringUtils.isEmpty(username)) {
					target = this.downloadRiservatoAction(username, "downloadDocumentoInvito");
				}					
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumentoInvito");
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumentoInvito");
			ExceptionUtils.manageExceptionError(e, this);
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		
	    return target;
	}
		
	/**
	 * download generalizzata di un documento per documenti riservati
	 */
	private String downloadRiservatoAction(String username, String sender) {
		String target = SUCCESS;
		
		// recupera il nome del file relativo alla richiesta di download...
		FileType file = null;
		try {	
			if(StringUtils.isEmpty(this.idprg)){
				this.idprg = CommonSystemConstants.ID_APPLICATIVO_GARE;
			}
			if( !StringUtils.isEmpty(username) ) {			
				file = this.documentiDigitaliManager.getDocumentoRiservato(
						this.idprg, this.id, username);
				if(file == null) {
					this.addActionError(this.getText("Errors.fileDownload.noRights"));
					this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
					target = INPUT;
				}
			}
		} catch (Exception e) { //(ApsException e) {
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
					evento.setUsername(this.getCurrentUser().getUsername());
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
	
	@Override
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest(); 
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		IPageManager pageManager = this.getPageManager();
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		
		PageURL url = this.urlManager.createURL(reqCtx);
	
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}

	
	private String download(FileType file, boolean documentoRiservato){
		if(file != null){
			if(isDocumentoFirmato(file)){
				// documento firmato o marcato temporalmente (.P7M, .TSD)
				return (documentoRiservato 
						? "successFirmaDigitaleRiservato" 
						: "successFirmaDigitalePubblico");
			}else{
				// documento NON firmato 
				this.filename = file.getNome();
				this.contentType = "application/octet-stream";
				this.inputStream = new ByteArrayInputStream(file.getFile());
				return SUCCESS;
			}
		}else{
			return ERROR;
		}
	}

	
	private boolean isDocumentoFirmato(FileType file) {
		if(file != null) {
			String filename = file.getNome().toUpperCase();			
			return filename.endsWith(TIPO_DOCUMENTO_FIRMATO) || 
			   	   filename.endsWith(TIPO_DOCUMENTO_MARCATO_TEMPORALMENTE);
		} else {
			return false;
		}
	}

}
