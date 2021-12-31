package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Action per la gestione dell'eliminazione dei documenti allegati alla busta
 * selezionata
 *
 * @version 1.0
 * @author Marco.Perazzetta
 */
public class DocumentoBustaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2479103888236560071L;

	private Map<String, Object> session;

	private IEventManager eventManager;
	
	protected int id;

	private boolean deleteDocRichiesto;
	private boolean deleteDocUlteriore;

	private InputStream inputStream;
	private String filename;
	private String contentType;

	private int tipoBusta;	
//	private String codiceGara;
//	private String codice;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
		
	public IEventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public boolean isDeleteDocRichiesto() {
		return deleteDocRichiesto;
	}

	public boolean isDeleteDocUlteriore() {
		return deleteDocUlteriore;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

//	public String getCodiceGara() {
//		return codiceGara;
//	}
//
//	public void setCodiceGara(String codiceGara) {
//		this.codiceGara = codiceGara;
//	}
//
//	public String getCodice() {
//		return codice;
//	}
//
//	public void setCodice(String codice) {
//		this.codice = codice;
//	}

	/**
	 * restituisce l'helper Documenti Busta
	 */
	protected WizardDocumentiBustaHelper getWizardDocumentiBustaHelper() throws ApsException {
		return WizardDocumentiBustaHelper.getFromSession(this.session, this.tipoBusta);
	}

	/**
	 * Chiede la conferma per l'eliminazione di un allegato richiesto
	 */
	public String confirmDeleteAllegatoRichiesto() {
		String target = SUCCESS;
		
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				this.deleteDocRichiesto = true;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "confirmDeleteAllegatoRichiesto");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}

	/**
     * Elimina un documento allegato richiesto
     */
	public String deleteAllegatoRichiesto() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// traccia l'evento di eliminazione di un file...
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(documentiBustaHelper.getCodice());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.DELETE_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(documentiBustaHelper.getDescTipoBusta()
						+ ": cancellazione documento richiesto, file=" + documentiBustaHelper.getDocRichiestiFileName().get(this.id)
						+ ", dimensione=" + documentiBustaHelper.getDocRichiestiSize().get(this.id) + "KB");

				try {
					documentiBustaHelper.removeDocRichiesto(this.id);
				} catch (Exception ex) {
					//ho rilanciato di nuovo la stessa azione con un refresh di pagina
					ApsSystemUtils.logThrowable(ex, this, "deleteAllegatoRichiesto");
				} finally {
					if(evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}
				//documentiBustaHelper.setDatiModificati(true);
				
				if( !this.aggiornaAllegato() ) {
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteAllegatoRichiesto");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * Annulla la procedura di eliminazione di un allegato richiesto
	 */
	public String cancelDeleteAllegatoRichiesto() {
		return SUCCESS;
	}

	/**
	 * Chiede la conferma per l'eliminazione di un allegato ulteriore
	 */
	public String confirmDeleteAllegatoUlteriore() {
		String target = SUCCESS;
		this.deleteDocUlteriore = true;
		return target;
	}

    /**
     * Elimina un documento allegato inserito dall'utente ma non richiesto
     */
	public String deleteAllegatoUlteriore() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// traccia l'evento di eliminazione di un file...	
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(documentiBustaHelper.getCodice());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.DELETE_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(documentiBustaHelper.getDescTipoBusta()
						+ ": cancellazione documento ulteriore, file=" + documentiBustaHelper.getDocUlterioriFileName().get(this.id)
						+ ", dimensione=" + documentiBustaHelper.getDocUlterioriSize().get(this.id) + "KB");

				try {
					documentiBustaHelper.removeDocUlteriore(this.id);
				} catch (Exception ex) {
					//ho rilanciato di nuovo la stessa azione con un refresh di pagina
					ApsSystemUtils.logThrowable(ex, this, "deleteAllegatoUlteriore");
				} finally {
					if(evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}
				//documentiBustaHelper.setDatiModificati(true);
				
				if( !this.aggiornaAllegato() ) {
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteAllegatoUlteriore");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * Annulla la procedura di eliminazione di un allegato ulteriore
	 */
	public String cancelDeleteAllegatoUlteriore() {
		return SUCCESS;
	}

	/**
	 * ... 
	 */
	public String downloadAllegatoRichiesto() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = ERROR;
			} else {
				this.contentType = documentiBustaHelper.getDocRichiestiContentType().get(this.id);
				this.filename = documentiBustaHelper.getDocRichiestiFileName().get(this.id);
				this.inputStream = documentiBustaHelper.downloadDocRichiesto(this.id);
			}
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto", "Errore durante la decifratura dell'allegato richiesto " + this.filename);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto", "Errore durante la decifratura dell'allegato richiesto " + this.filename);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} 
		return target;
	}

	/**
	 * ... 
	 */
	public String downloadAllegatoUlteriore() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = ERROR;
			} else {
				this.contentType = documentiBustaHelper.getDocUlterioriContentType().get(this.id);
				this.filename = documentiBustaHelper.getDocUlterioriFileName().get(this.id);
				this.inputStream = documentiBustaHelper.downloadDocUlteriore(this.id);
			}
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoUlteriore", "Errore durante la decifratura dell'allegato ulteriore " + this.filename);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoUlteriore", "Errore durante la decifratura dell'allegato ulteriore " + this.filename);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "downloadAllegatoUlteriore");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione 
	 * @throws ApsException 
	 */
	private boolean aggiornaAllegato() throws ApsException {
		String target = SUCCESS;
		
		WizardDocumentiBustaHelper helper = getWizardDocumentiBustaHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {				
			// NB: ProcessPageDocumentiBustaAction.saveDocumenti() termina
			// rimuovedo dalla sessione l'helper... in questo modo sembra 
			// che la prima cancellazione di un allegato funzioni mentre 
			// la successiva vada in errore per la mancanza dell'helper 
			// in sessione...
			// Come misura correttiva temporanea si reinserisce l'helper 
			// in sessione qui!
			this.session.put(helper.getNomeIdSessione(), helper);
			
			target = ProcessPageDocumentiBustaAction.saveDocumenti(
					helper.getCodiceGara(),
					helper.getCodice(), 
					helper.getOperazione(), 
					this.tipoBusta, 
					this.session, 
					this);
		}
		
		return (SUCCESS.equalsIgnoreCase(target));
	}

}
