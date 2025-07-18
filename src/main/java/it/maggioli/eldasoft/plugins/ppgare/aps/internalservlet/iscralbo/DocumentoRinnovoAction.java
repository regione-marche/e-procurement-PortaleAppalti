package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Action per la gestione dei documenti di rinnovo iscrizione a un catalogo o
 * elenco
 *
 * @version 1.0
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class DocumentoRinnovoAction extends EncodedDataAction 
	implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2477103888236590071L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;


	private List<DocumentazioneRichiestaType> documentiRichiesti;

	private int id;

	private boolean deleteDocRichiesto;
	private boolean deleteDocUlteriore;

	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public List<DocumentazioneRichiestaType> getDocumentiRichiesti() {
		return documentiRichiesti;
	}

	public void setDocumentiRichiesti(List<DocumentazioneRichiestaType> documentiRichiesti) {
		this.documentiRichiesti = documentiRichiesti;
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

	/**
	 * conferma la cancellazione di un documento allegato richiesto 
	 */
	public String confirmDeleteDocRichiesto() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if (helper != null) {
			try {
				this.deleteDocRichiesto = true;
				
				this.documentiRichiesti = helper.getDocumentiRichiestiBO();
				
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "confirmDeleteDocRichiesto");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * cancella un documento allegato richiesto
	 */
	public String deleteDocRichiesto() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		if (helper != null) {

		    Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(helper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Rinnovo iscrizione: cancellazione documento richiesto con id="
					+ this.id + ", file="
					+ helper.getDocumenti().getRequiredDocs().get(this.id).getFileName()
					+ ", dimensione=" + helper.getDocumenti().getRequiredDocs().get(this.id).getSize() + "KB");

			try {
				helper.getDocumenti().removeDocRichiesto(this.id);
				if( !aggiornaAllegato() ) {
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			} catch (Exception ex) {
				// ho rilanciato di nuovo la stessa azione con un refresh di pagina
				evento.setError(ex);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "deleteDocRichiesto");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} finally {
				this.eventManager.insertEvent(evento);
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * nega la cancellazione di un documento allegato richiesto
	 */
	public String cancelDeleteDocRichiesto() {
		return SUCCESS;
	}

	/**
	 * conferma la cancellazione di un documento allegato ulteriore
	 */
	public String confirmDeleteDocUlteriore() {
		String target = SUCCESS;
		this.deleteDocUlteriore = true;
		return target;
	}

	/**
	 * cancella un documento allegato ulteriore
	 */
	public String deleteDocUlteriore() {

		String target = SUCCESS;

		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if (helper != null) {
			
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(helper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Rinnovo iscrizione: cancellazione documento ulteriore, file="
					+ helper.getDocumenti().getAdditionalDocs().get(this.id).getFileName()
					+ ", dimensione=" + helper.getDocumenti().getAdditionalDocs().get(this.id).getSize() + "KB");

			try {
				helper.getDocumenti().removeDocUlteriore(this.id);
				
				if( !aggiornaAllegato() ) {
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			} catch (Exception ex) {
				//ho rilanciato di nuovo la stessa azione con un refresh di pagina
				evento.setError(ex);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "deleteDocUlteriore");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} finally {
				this.eventManager.insertEvent(evento);
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * nega la cancellazione un documento allegato ulteriore
	 */
	public String cancelDeleteDocUlteriore() {
		return SUCCESS;
	}

	/**
	 * scarica un documento allegato richiesto 
	 */
	public String downloadDocRichiesto() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		if (helper != null) {
			try {
				Attachment attachment = helper.getDocumenti().getRequiredDocs().get(id);
				contentType = attachment.getContentType();
				filename = attachment.getFileName();
				inputStream = helper.getDocumenti().downloadDocRichiesto(id);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadDocRichiesto");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * scarica un documento allegato ulteriore
	 */
	public String downloadDocUlteriore() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if (helper != null) {
			try {
				Attachment attachment = helper.getDocumenti().getAdditionalDocs().get(id);
				contentType = attachment.getContentType();
				filename = attachment.getFileName();
				inputStream = helper.getDocumenti().downloadDocUlteriore(id);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadDocUlteriore");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
	/**
	 * aggiorna un documento allegato per una domanda di rinnovo 
	 */
	private boolean aggiornaAllegato() throws ApsException {		
		String target = SUCCESS;

		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			target = RinnovoAction.saveDocumenti(
					helper
					, session 
					, this
					, new Date()
			);
		}
		
		return (SUCCESS.equalsIgnoreCase(target));
	}

}