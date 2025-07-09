package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Action per la gestione dell'eliminazione dei documenti allegati alla busta
 * selezionata
 *
 * @version 1.0
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class DocumentoBustaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2479103888236560071L;

	protected Map<String, Object> session;

	protected IEventManager eventManager;
	
	protected int id;

	private boolean deleteDocRichiesto;
	private boolean deleteDocUlteriore;

	protected InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	protected String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	protected String contentType;

	protected int tipoBusta;


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

	/**
	 * Chiede la conferma per l'eliminazione di un allegato richiesto
	 */
	public String confirmDeleteAllegatoRichiesto() {
		String target = SUCCESS;
		
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = GestioneBuste.getDocumentiBustaHelperFromSession(this.tipoBusta);
			
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
			BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
			WizardDocumentiBustaHelper documentiBustaHelper = busta.getHelperDocumenti();
			
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
				evento.setMessage(busta.getDescrizioneBusta()
						+ ": cancellazione documento richiesto, file=" + documentiBustaHelper.getRequiredDocs().get(this.id).getFileName()
						+ ", dimensione=" + documentiBustaHelper.getRequiredDocs().get(this.id).getSize() + "KB");

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
			BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
			WizardDocumentiBustaHelper documentiBustaHelper = busta.getHelperDocumenti();			
			
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
				evento.setMessage(busta.getDescrizioneBusta()
						+ ": cancellazione documento ulteriore, file=" + documentiBustaHelper.getAdditionalDocs().get(this.id).getFileName()
						+ ", dimensione=" + documentiBustaHelper.getAdditionalDocs().get(this.id).getSize() + "KB");

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
			WizardDocumentiBustaHelper documentiBustaHelper = GestioneBuste.getDocumentiBustaHelperFromSession(this.tipoBusta);
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = ERROR;
			} else {
				Attachment attachment = documentiBustaHelper.getRequiredDocs().get(id);
				contentType = attachment.getContentType();
				filename = attachment.getFileName();
				inputStream = documentiBustaHelper.downloadDocRichiesto(id);
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
			WizardDocumentiBustaHelper documentiBustaHelper = GestioneBuste.getDocumentiBustaHelperFromSession(this.tipoBusta);
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = ERROR;
			} else {
				Attachment attachment = documentiBustaHelper.getAdditionalDocs().get(id);
				contentType = attachment.getContentType();
				filename = attachment.getFileName();
				inputStream = documentiBustaHelper.downloadDocUlteriore(id);
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
	protected boolean aggiornaAllegato() throws ApsException {
		String target = SUCCESS;		
		try {
			WizardDocumentiBustaHelper helper = GestioneBuste.getDocumentiBustaHelperFromSession(this.tipoBusta);
			
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
//				// NB: ProcessPageDocumentiBustaAction.saveDocumenti() termina
//				// rimuovedo dalla sessione l'helper... in questo modo sembra 
//				// che la prima cancellazione di un allegato funzioni mentre 
//				// la successiva vada in errore per la mancanza dell'helper 
//				// in sessione...
//				// Come misura correttiva temporanea si reinserisce l'helper 
//				// in sessione qui!
//				this.session.put(helper.getNomeIdSessione(), helper);
				
				target = ProcessPageDocumentiBustaAction.saveDocumenti(
						helper.getCodiceGara(),
						helper.getCodice(), 
						helper.getOperazione(), 
						this.tipoBusta, 
						this.session, 
						this);
			}
		} catch (Exception e) {
			throw new ApsException(e.getMessage());
		}		
		return (SUCCESS.equalsIgnoreCase(target));
	}

}
