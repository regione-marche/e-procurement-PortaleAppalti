package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import java.util.List;
import java.util.Map;

/**
 * Action per la gestione dell'eliminazione dei documenti allegati alla domanda
 * d'iscrizione all'albo.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class DeleteAllegatoIscrizioneAction extends BaseAction 
	implements ServletRequestAware, SessionAware 
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2479103800236560071L;

    private Map<String, Object> session;

    private IBandiManager bandiManager;
	private IEventManager eventManager;

    private List<DocumentazioneRichiestaType> documentiRichiesti;

	private int id;

    private boolean deleteDocRichiesto;
    private boolean deleteDocUlteriore;
    
    
	@Override
    public void setSession(Map<String, Object> session) {
		this.session = session;
    }
    
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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

	public boolean isDeleteDocRichiesto() {
		return deleteDocRichiesto;
	}

	public boolean isDeleteDocUlteriore() {
		return deleteDocUlteriore;
	}

	/**
	 * Etichetta per la generazione del messaggio nel log eventi del portale
	 */
	private String getFunzione(WizardIscrizioneHelper iscrizioneHelper) {
		String funzione = "Iscrizione";
		if (iscrizioneHelper.isAggiornamentoIscrizione()) {
			if (iscrizioneHelper.isAggiornamentoSoloDocumenti()) {
				funzione = "Aggiornamento documenti";
			} else {
				funzione = "Aggiornamento dati/documenti";
			}
		}
		return funzione;
	}

	/**
	 * ...
	 */
	public String confirmDeleteAllegatoRichiesto() {
		String target = SUCCESS;

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.deleteDocRichiesto = true;

			try {
				this.documentiRichiesti = this.bandiManager.getDocumentiRichiestiBandoIscrizione(
						iscrizioneHelper.getIdBando(), 
						iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
						iscrizioneHelper.isRti());
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "confirmDeleteAllegatoRichiesto");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} 
		}

		return target;
	}

	/**
     * Elimina un documento allegato richiesto per l'iscrizione ad un elenco
     * fornitori
     */
    public String deleteAllegatoRichiesto() {
		String target = SUCCESS;
	
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	
		if (iscrizioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			Event evento = null;
			try {
			    // la sessione non e' scaduta, per cui proseguo regolarmente
			    WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();
			    
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(iscrizioneHelper.getIdBando());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.DELETE_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(this.getFunzione(iscrizioneHelper)
						+ ": cancellazione documento richiesto, file="
						+ documentiHelper.getRequiredDocs().get(this.id).getFileName()
						+ ", dimensione=" + documentiHelper.getRequiredDocs().get(this.id).getSize() + "KB");
		
			    documentiHelper.removeDocRichiesto(this.id);
			    
				if( !this.aggiornaAllegato() ) {
					target = CommonSystemConstants.PORTAL_ERROR;
			    }
			    
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "deleteAllegatoRichiesto");
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} finally {
				if(evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}				
		}
	
		return target;
    }

    /**
     * ... 
     */
	public String cancelDeleteAllegatoRichiesto() {
		return SUCCESS;
	}

	/**
     * ... 
     */
	public String confirmDeleteAllegatoUlteriore() {
		String target = SUCCESS;
		this.deleteDocUlteriore = true;
		return target;
	}

    /**
     * Elimina un documento allegato inserito dall'utente ma non richiesto per
     * l'iscrizione ad un elenco fornitori
     */
    public String deleteAllegatoUlteriore() {
		String target = SUCCESS;
	
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	
		if (iscrizioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			Event evento = null;			
			try {
			    // la sessione non e' scaduta, per cui proseguo regolarmente
			    WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();
			    
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(iscrizioneHelper.getIdBando());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.DELETE_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(this.getFunzione(iscrizioneHelper)
						+ ": cancellazione documento ulteriore, file="
						+ documentiHelper.getAdditionalDocs().get(this.id).getFileName()
						+ ", dimensione=" + documentiHelper.getAdditionalDocs().get(this.id).getSize() + "KB");
		
				documentiHelper.removeDocUlteriore(this.id);
					
				if( !this.aggiornaAllegato() ) {
					target = CommonSystemConstants.PORTAL_ERROR;
			    }
		
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "deleteAllegatoUlteriore");
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} finally {
				if(evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}				
		}
	
		return target;
    }

    /**
     * ...
     */
	public String cancelDeleteAllegatoUlteriore() {
		return SUCCESS;
	}

	/**
	 * aggiorna gli allegati della comunicazione   
	 */
	private boolean aggiornaAllegato() throws ApsException {
		String target = SUCCESS;

		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {			
			target = SaveWizardIscrizioneAction.saveDocumenti(
					helper,
					this.session, 
					this);
		}
		return (SUCCESS.equalsIgnoreCase(target));
	}

}
