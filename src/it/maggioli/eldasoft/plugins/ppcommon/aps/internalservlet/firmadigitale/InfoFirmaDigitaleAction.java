package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.firmadigitale;

import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.sign.DigitalSignatureException;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import com.agiletec.apsadmin.system.TokenInterceptor;

/**
 * Action per la gestione delle info sulla firma digitale e per il download 
 * dei documenti digitali presenti in backoffice.
 * 
 * @version x.y.z
 * @author cristiano.crescenti
 */
public class InfoFirmaDigitaleAction extends EncodedDataAction 
	implements SessionAware, IDownloadAction, ServletResponseAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private static final int DOWNLOAD_RISERVATO = 1;
	private static final int DOWNLOAD_PUBBLICO  = 2;
	private static final int DOWNLOAD_FILE 		= 3;
	private static final int DOWNLOAD_INVITO	= 4;
	
	//private static final String SESSION_EXPIRED = 
//		"Sessione scaduta, effettuare nuovamente l''accesso";
	
//	private static final String DOWNLOAD_NOT_ALLOWED = 
//		"Impossibile scaricare un documento inesistente oppure di cui non si possiede la visibilità";

	public int getDOWNLOAD_RISERVATO() 	{ return DOWNLOAD_RISERVATO; }
	public int getDOWNLOAD_PUBBLICO() 	{ return DOWNLOAD_PUBBLICO; }
	public int getDOWNLOAD_FILE() 		{ return DOWNLOAD_FILE; }
	public int getDOWNLOAD_INVITO() 	{ return DOWNLOAD_INVITO; }

	//**************************************************************************
	
	private Map<String, Object> session;
	private HttpServletResponse response;
	private IDocumentiDigitaliManager documentiDigitaliManager;
	private IEventManager eventManager;
	private IPageManager pageManager;
	private IURLManager urlManager;
	private ICustomConfigManager customConfigManager;
	
	private Map<String, String> hiddenParams;
	private String from;	
	private String currentFrame;
	private long id;			// id del documento in DOCDIG
	private String idprg;		// PA, PG, ...
	private String pos;			// posizione contenuto da estrarre da un documento marcato/firmato
	private Boolean isfile;		// indica se presente in sessione il nome di un file fisico (SESSION_ID_DOWNLOAD_WORK_FILE_FIRMA_DIGITALE)
	private InputStream inputStream;
	private String filename;	
	private String contentType;	
	
	// informazioni relative alla firma digitale 
	private String verificaFirmaDigitaleXML;	
	private String verificaMarcheTemporaliXML;
	private String dignomdoc_p7m;
	private String dignomdoc_doc;
	private String state;
	private String message;
	private int riservato;
	
	// download relativi all'allegato e al contenuto nell'allegato  
	private List<String> downloadAllegati;	
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setUrlPage(String urlPage) {
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public IPageManager getPageManager() {
		return this.pageManager;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}	
	
	public Map<String, String> getHiddenParams() {
		return hiddenParams;
	}

	public void setHiddenParams(Map<String, String> hiddenParams) {
		this.hiddenParams = hiddenParams;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCurrentFrame() {
		return currentFrame;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {	
		this.currentFrame = currentFrame;
	}

	public void setDocumentiDigitaliManager(
			IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
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
	
	public Boolean getIsfile() {
		return isfile;
	}

	public void setIsfile(Boolean isfile) {
		this.isfile = isfile;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
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

	public String getVerificaFirmaDigitaleXML() {
		return verificaFirmaDigitaleXML;
	}

	public String getVerificaMarcheTemporaliXML() {
		return verificaMarcheTemporaliXML;
	}

	public String getDignomdoc_p7m() {
		return dignomdoc_p7m;
	}

	public void setDignomdoc_p7m(String dignomdoc_p7m) {
		this.dignomdoc_p7m = dignomdoc_p7m;
	}

	public String getDignomdoc_doc() {
		return dignomdoc_doc;
	}

	public void setDignomdoc_doc(String dignomdoc_doc) {
		this.dignomdoc_doc = dignomdoc_doc;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

	public int getRiservato() {
		return riservato;
	}

	public List<String> getDownloadAllegati() {
		return downloadAllegati;
	}

	public void setDownloadAllegati(List<String> downloadAllegati) {
		this.downloadAllegati = downloadAllegati;
	}
	
	/**
	 * ...
	 */
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
	
	/**
	 * apre la form con le informazioni sulla firma digitale del documento 
	 */
	private String view(int riservato, String username) {
		this.setTarget(SUCCESS);
	
		this.from = null;
		this.riservato = riservato;
		this.dignomdoc_p7m = null;
		this.dignomdoc_doc = null;
		this.state = null;
		this.message = null;
		this.hiddenParams = null;
		
		this.downloadAllegati = null;
		
		try {
			// costruisci il link per ritornare alla pagina precedente
			String back = (String)this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_FIRMA_DIGITALE);
			//this.getSession()
			//	.remove(PortGareSystemConstants.SESSION_ID_FROM_PAGE_FIRMA_DIGITALE);
			
			if(back != null) {
				// converti l'URL prima con tutti "&" come separatori dei parametri 
				// e poi riconverti tutti i separatori in "&amp;"
				this.from = back.replaceAll("&amp;", "&").replaceAll("&", "&amp;");		
				
				// NB: 
				// elimina dall'url il parametro CSRF Token...
				// viene rigenerato dalla pagina ed inserito esplicitamente nella pagina
				int i1 = this.from.indexOf("&amp;" + TokenInterceptor.getStrutsTokenName());
				if(i1 > 0) {
					int i2 = this.from.indexOf("&amp;", i1 + 1);
					i2 = (i2 > 0 ? i2 : this.from.length() - 1);
					this.from = this.from.substring(0, i1) + this.from.substring(i2, this.from.length() - 1);
				}
				
				// N.B.
				// Alcune pagine jsp passano dei parametri nascosti (codice, ext, ...)
				// per recuperare i parametri nascosti e configurare correttamente 
				// il back link ('Torna alla pagine precendente') è necessario
				// - recuperare tutti i parametri della request
				// - tenere solo quei parametri che non sono presenti ne 
				//   nella query string ne nell'indirizzo della pagina chiamante
				this.hiddenParams = new HashMap<String, String>();				
				String qry = this.from.toLowerCase() + ";" +						 
						     this.getRequest().getQueryString().toLowerCase();	 
				Map<String, String> params = this.getRequest().getParameterMap();
				for (Map.Entry<String, String> item : params.entrySet()) {
					// ignora il parametro CSRF Token,
					// viene rigenerato dalla pagina ed inserito esplicitamente nella pagina
					if( item.getKey().toLowerCase().indexOf(TokenInterceptor.getStrutsTokenName()) >= 0 ) {
						continue;
					}
					// aggiungi il parametro agli input hidden della pagina
					if( qry.indexOf( item.getKey().toLowerCase() + "=" ) < 0 ) {
						this.hiddenParams.put(item.getKey(), item.getValue());
					}
				}				
			}
			
			// recupera le informazioni sulla firma del documento 
			FileType doc = this.getDocumento(riservato, username);
			
			this.downloadAllegati = this.getListaDocumenti(doc);
						
			if(doc != null) {
				this.dignomdoc_p7m = doc.getNome();
				
				int i = doc.getNome().lastIndexOf(".");
				//String fileext = (i < 0 ? "" : doc.getNome().substring(i).toLowerCase());			
				String filename = (i < 0 ? "" : doc.getNome().substring(0, i));
				
				this.dignomdoc_doc = filename;
			}
				
			if(doc != null && doc.getFile() != null) {
				DigitalSignatureChecker checker = new DigitalSignatureChecker();
				
				// recupera le informazioni sulla marcatura temporale (.tsd)
				byte[] content = null;
				try {				
					content = checker.getContentTimeStamp(doc.getFile());		
					if (content != null) {
						byte[] xml = checker.getXMLTimeStamps(doc.getFile());
						this.verificaMarcheTemporaliXML = new String(xml);
					}					 
				} catch(Exception ex) {					
					this.verificaMarcheTemporaliXML = null;
				}
				
				// recupera le informazioni sulla firma del documento (.p7m)				
				try {				
					Date ckDate = null;
					byte[] xml = checker.getXMLSignature(
							(content != null ? content : doc.getFile()), 
							ckDate);
					this.verificaFirmaDigitaleXML = new String(xml);									
				} catch(Exception ex) {					
					this.verificaFirmaDigitaleXML = null;
				}
				
			} else {
				this.state = "NO-DATA-FOUND";
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}			
										
//			} catch(ParseException t) {
//				this.state = "DATE-PARSE-EXCEPTION";
		} catch (Exception e) {			
			this.state = "ERROR";
			this.message = e.getMessage();
			if (e.getCause() != null) message = message + " (" + e.getCause().toString() + ")";
			
			ApsSystemUtils.logThrowable(e, this, "view");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		return this.getTarget();
	}

	/**
	 * apre la form con le informazioni sulla firma digitale di un documento pubblico 
	 */
	public String viewPubblico() {	
		this.setTarget( this.view(DOWNLOAD_PUBBLICO, null) );
		return this.getTarget();		
	}

	/**
	 * apre la form con le informazioni sulla firma digitale di un documento riservato 
	 */
	public String viewRiservato() {	
		this.setTarget(SUCCESS);
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// session ok, si può continuare...
			this.setTarget( this.view(DOWNLOAD_RISERVATO, userDetails.getUsername()) );
		} else {
			// sessione scaduta
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);			
		}		
		return this.getTarget();
	}
		
	/**
	 * apre la form con le informazioni sulla firma digitale di un documento riservato 
	 */
	public String viewFile() {	
		this.setTarget(SUCCESS);
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// session ok, si può continuare...
			this.setTarget( this.view(DOWNLOAD_FILE, null) );
		} else {
			// sessione scaduta
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);			
		}		
		return this.getTarget();
	}

	/**
	 * apre la form con le informazioni sulla firma digitale di un documento riservato
	 * in forma anonima (documentazione di invito della gara) 
	 */
	public String viewInvito() {
		this.setTarget(SUCCESS);
		try {
			if(StringUtils.isEmpty(this.idprg)){
				this.idprg = CommonSystemConstants.ID_APPLICATIVO_GARE;
			}
			
			// verifica se l'opzione DOCINVITOPUBBLICA e' attiva...
			if( !this.customConfigManager.isVisible("GARE", "DOCINVITOPUBBLICA") ) {
				this.addActionError(this.getText("Errors.fileDownload.noRights"));							
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else { 
				String username = this.documentiDigitaliManager.getUsernameDocumentoRiservato(this.idprg, this.id);
				if(!StringUtils.isEmpty(username)) {
					this.setTarget( this.view(DOWNLOAD_INVITO, username) );
				} else {
					this.addActionError(this.getText("Errors.fileDownload.noRights"));	
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "viewInvito");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "viewInvito");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		return this.getTarget();
	}
	

	/**
	 * Esegue il download del contenuto del documento firmato 
	 */
	private String download(int riservato, String username) {
		this.setTarget(SUCCESS);
		
		Event evento = null;
		try {
			this.riservato = riservato;
			
			String fn = null; 
			if(this.isfile != null && this.isfile) {
				fn = (String)this.session
					.get(PortGareSystemConstants.SESSION_ID_DOWNLOAD_WORK_FILE_FIRMA_DIGITALE);
			}
			
			if(StringUtils.isEmpty(this.idprg)){
				this.idprg = CommonSystemConstants.ID_APPLICATIVO_GARE;
			}

			// tracciatura eventi...
			if(riservato == DOWNLOAD_RISERVATO || 
			   riservato == DOWNLOAD_FILE || 
			   riservato == DOWNLOAD_INVITO) {
				evento = new Event();
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.DOWNLOAD_DOCUMENTO_RISERVATO);
			
				String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
				if (ipAddress == null) {
					ipAddress = this.getRequest().getRemoteAddr();
				}
				evento.setIpAddress(ipAddress);
				evento.setSessionId(this.getRequest().getSession().getId());
				if(this.isfile != null && this.isfile) {
					evento.setDestination(fn);
				} else {
					evento.setDestination(this.idprg + "," + this.id);
				}
			}

			FileType allegato = this.getDocumento(riservato, username);
						
			if(allegato == null) {
				if(evento != null) {				
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage("Documento inesistente o non visibile");
				}
			} else {
				// recupera il contenuto del documento (firmato/marcato)...				
				List<String> listaFile = this.getListaDocumenti(allegato);
				int i = (StringUtils.isEmpty(this.pos) ? 0 : Integer.parseInt(this.pos));
				String filename = listaFile.get(i);
				boolean isContenuto = (allegato.getNome() != null && 
						               !allegato.getNome().equalsIgnoreCase(filename));
				byte[] content = null;
					
				if(!isContenuto) {
					// download del documento digitale 
					if(evento != null) {
						evento.setMessage("Download documento digitale (applicativo " + this.idprg + ", id " + this.id + ")");
						evento.setUsername(this.getCurrentUser().getUsername());
					}					
					content = allegato.getFile();
				} else {
					// download del contenuto nel documento digitale 
					if(evento != null) {
						evento.setMessage("Download contenuto all'interno di un documento digitale riservato (applicativo " + this.idprg + ", id " + this.id + ")");
						evento.setUsername(this.getCurrentUser().getUsername());
					}
					content = this.getContenutoDocumento(allegato, filename);
				}
							
				if(content == null) {
					// ERRORE?!?
					//this.addActionError(DOWNLOAD_NOT_ALLOWED);
					//this.setTarget(INPUT);
				} else {
					this.filename = filename;
					this.contentType = "application/octet-stream";
					this.inputStream = new ByteArrayInputStream(content);
					this.setTarget(SUCCESS);
				} 
			}
		} catch(Exception t) {
			if(evento != null) {
				evento.setError(t);
			}
			ApsSystemUtils.logThrowable(t, this, "download");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		
		return this.getTarget();
	}

	/**
	 * Restituisce un allegato pubblico o riservato scaricato dal db  
	 */
	private FileType getDocumento(int riservato, String username) {
		FileType doc = null;
		String filename = "";
		try {
			if(StringUtils.isEmpty(this.idprg)) {
				this.idprg = CommonSystemConstants.ID_APPLICATIVO_GARE;
			}

			if(this.isfile != null && this.isfile /*&& DOWNLOAD_FILE*/) {
				// recupera un file presente nella work... 
				filename = (String)this.session
					.get(PortGareSystemConstants.SESSION_ID_DOWNLOAD_WORK_FILE_FIRMA_DIGITALE);
				File f = new File(filename);				
				byte[] content = FileUtils.readFileToByteArray(f);				
				doc = new FileType(f.getName(), content);
			} else {
				// recupera un file presente in documentiDigitaliManager...
				if(riservato == DOWNLOAD_RISERVATO || riservato == DOWNLOAD_INVITO) {
					doc = this.documentiDigitaliManager.getDocumentoRiservato(
								this.idprg, 
								this.getId(), 
								username); 				
				} else if(riservato == DOWNLOAD_PUBBLICO) {
					doc = this.documentiDigitaliManager.getDocumentoPubblico(
								this.idprg, 
								this.getId());
				}
			}
		} catch(Exception e) {
			doc = null;
			ApsSystemUtils.logThrowable(e, this, "getDocumento", 
					"Non e' stato possibile trovare il documento identificato da " + 
					"idprg=" + this.idprg + ", id=" + this.id + ", filename=" + filename);
		}
		return doc;
	}
		
	/**
	 * Restituisce lo stream contenente un documento digitale pubblico.
	 */
	public String downloadDocumentoPubblico() {	
		this.setTarget( this.download(DOWNLOAD_PUBBLICO, null) );
		return this.getTarget();
	}
	
	/**
	 * Restituisce lo stream contenente un documento digitale riservato
	 */
	public String downloadDocumentoRiservato() {	
		this.setTarget(INPUT);
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// session ok, si può continuare...
			this.setTarget( this.download(DOWNLOAD_RISERVATO, userDetails.getUsername()) );
		} else {
			// sessione scaduta
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);			
		}		
		return this.getTarget();
	}	
	
	/**
	 * Restituisce lo stream contenente un documento digitale riservato
	 */
	public String downloadDocumentoFile() {	
		this.setTarget(INPUT);
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// session ok, si può continuare...
			this.setTarget( this.download(DOWNLOAD_FILE, null) );
		} else {
			// sessione scaduta
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);			
		}		
		return this.getTarget();
	}	

	/**
	 * Restituisce lo stream contenente un documento digitale riservato
	 * in forma anonima (documentazione di invito della gara) 
	 */
	public String downloadDocumentoInvito() {
		this.setTarget(INPUT);
		try {
			if(StringUtils.isEmpty(this.idprg)){
				this.idprg = CommonSystemConstants.ID_APPLICATIVO_GARE;
			}
			
			// verifica se l'opzione DOCINVITOPUBBLICA e' attiva...
			if( !this.customConfigManager.isVisible("GARE", "DOCINVITOPUBBLICA") ) {
				this.addActionError(this.getText("Errors.fileDownload.noRights"));							
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {			
				String username = this.documentiDigitaliManager.getUsernameDocumentoRiservato(this.idprg, this.id);
				if(!StringUtils.isEmpty(username)) {
					this.setTarget( this.download(DOWNLOAD_INVITO, username) );
				} else {
					// --- utente cerca di scaricare un documento che non gli appartiene ---
					this.addActionError(this.getText("Errors.fileDownload.noRights"));
					session.put("ACTION_OBJECT", this);
				}	
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumentoInvito");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumentoInvito");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		return this.getTarget();
	}

	
	/**
	 * esplode un allegato e ne estrae tutti i livelli contenuti
	 * 
	 * @return elenco dei livelli (download) presenti nell'allegato (List<String>)
	 *         il contenuto in byte di un livello dell'allegato
	 *          
	 * @throws DigitalSignatureException 
	 * @throws IOException 
	 */
	private Object esplodiAllegato(
			FileType allegato, boolean creaLista, String downloadFilename) 
		throws DigitalSignatureException, IOException {
	
		List<String> lista = null;
		byte[] content = null;
		
		if(allegato != null) {
			try {
				DigitalSignatureChecker checker = new DigitalSignatureChecker();
				String filename = allegato.getNome();
				content = allegato.getFile();
	
				if(creaLista) {
					lista = new ArrayList<String>();
					// il I download è sempre l'allegato... 
					lista.add(filename);
				} 
				
				boolean termina = false;
				while (!termina && content != null) {
					
					int i = filename.lastIndexOf(".");
					String fileext = (i < 0 ? "" : filename.substring(i).toLowerCase());
					filename = (i < 0 ? filename : filename.substring(0, i));
						
					if(i < 0) {
						// nessun contenuto da estrarre...
						termina = true;
					} else {
						if(".tsd".equals(fileext)) {
							// recupera il contenuto del documento .tsd
							byte[] tmp = checker.getContentTimeStamp(content);							
							if(tmp != null) {
								tmp = checker.getContent(tmp);								
								if(creaLista) {
									lista.add(filename);
								}
							}
							content = tmp;
							
						} else if(".p7m".equals(fileext)) {
							// recupera il contenuto del documento .p7m
							byte[] tmp = null;
							try {
								tmp = checker.getContent(content);
							} catch(DigitalSignatureException e) {
								// se non riesci a verificare la firma,
								// restituisci comunque il contenuto...
								tmp = content;
								ApsSystemUtils.logThrowable(e, this, "esplodiAllegato", 
										"Non e' possibile verificare il contenuto del " + 
										"file " + filename + " nell'allegato " + allegato.getNome());
								ExceptionUtils.manageExceptionError(e, this);								
							} 		
							if(tmp != null) {
								if(creaLista) {
									lista.add(filename);
								}
							}
							content = tmp;
							
						} else if(".pdf".equals(fileext)) {
							//PdfReader readInputPDF = new PdfReader(content);
							//...
							//...
							//...
							termina = true;
							
						} else if(".xml".equals(fileext)) {
							//...
							//...
							//...
							termina = true;				
						} 
					}
						
					// in caso di richiesta di un download specifico,
					// restituisce l'array di byte corrispendenti al 
					// download richiesto richiesto...
					if(filename.equalsIgnoreCase(downloadFilename)) {
						// restituisce il "content" trovato...
						termina = true;
					}
				}			
			} catch(DigitalSignatureException e) {
				ApsSystemUtils.logThrowable(e, this, "esplodiAllegato");
				ExceptionUtils.manageExceptionError(e, this);
//				throw e;				
			} 							
		}
		
		return (creaLista ? lista : content);
	}
	
	/**
	 * estra la lista dei download di un allegato firmato/marcato
	 */
	@SuppressWarnings("unchecked")
	private List<String> getListaDocumenti(FileType allegato) 
		throws DigitalSignatureException, IOException {		
		return (List<String>)this.esplodiAllegato(allegato, true, null);
	}

	/**
	 * estrae da un allegato firmato/marcato il file principale o il contenuto  
	 */
	private byte[] getContenutoDocumento(FileType allegato, String downloadFilename) 
		throws DigitalSignatureException, IOException {		
		return (byte[])this.esplodiAllegato(allegato, false, downloadFilename);
	}
	
}

