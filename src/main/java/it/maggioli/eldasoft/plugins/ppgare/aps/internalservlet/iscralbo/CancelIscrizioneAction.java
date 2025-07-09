package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Action di gestione dell'annullamento di un'iscrizione
 * 
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class CancelIscrizioneAction extends BaseAction implements SessionAware {

    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;
    private static final Logger logger = ApsSystemUtils.getLogger();

    private Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
    private String codice;
    private Boolean fromQFormChanged;
    private IComunicazioniManager comunicazioniManager;
    private IEventManager eventManager;
    
    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    public String getCodice() {
    	return codice;
    }

    /**
     * @return the fromQFormChanged
     */
    public Boolean getFromQFormChanged() {
    	return fromQFormChanged;
    }
    
    /**
     * @param fromQFormChanged the fromQFormChanged to set
     */
    public void setFromQFormChanged(Boolean fromQFormChanged) {
    	this.fromQFormChanged = fromQFormChanged;
    }

    
    /**
	 * @param comunicazioniManager the comunicazioniManager to set
	 */
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	/**
     * ...
     */
	public String questionCancelIscrizione() {
		String target = SUCCESS;
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	
		if (iscrizioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
		    this.codice = iscrizioneHelper.getIdBando();
		    if (iscrizioneHelper.isAggiornamentoIscrizione())
		    	target += "CancAggIscrizione";
		}
		return target;
    }

    /**
     * ...
     */
	public String cancelIscrizione() {
		String target = SUCCESS;
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		if (iscrizioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (iscrizioneHelper != null
				&& iscrizioneHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO) {
				target = "successCatalogo";
			}
			
			Long comunicazioneDaElminare = iscrizioneHelper.getIdComunicazione() != null ? iscrizioneHelper.getIdComunicazione() : iscrizioneHelper.getIdComunicazioneBozza();
			if(comunicazioneDaElminare != null) {
				logger.debug("CancelIscrizioneAction - eliminazione comunicazione con id: {}",comunicazioneDaElminare);
				Event evento = null;
				try {
					// crea l'evento...
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage(iscrizioneHelper.getDescrizioneFunzione() + ": rettifica elenco");
					
					this.comunicazioniManager.deleteComunicazione(
							CommonSystemConstants.ID_APPLICATIVO, 
							comunicazioneDaElminare);
				} catch (ApsException e) {
					ApsSystemUtils.logThrowable(e, this, "eliminaDocumentiElenco", "Errore durante l'eliminazione della richiesta per l''elenco");
					this.addActionError(this.getText("Errors.sessionExpired"));
					target = CommonSystemConstants.PORTAL_ERROR;
					if(evento != null) {
						evento.setError(e);
					}
				} finally {
					if(evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}
			}
		}
		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);		
		return target;
	}

	
}
