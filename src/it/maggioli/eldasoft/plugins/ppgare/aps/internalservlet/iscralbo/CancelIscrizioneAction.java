package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di gestione dell'annullamento di un'iscrizione
 * 
 * @author Stefano.Sabbadin
 */
public class CancelIscrizioneAction extends BaseAction implements SessionAware {

    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private Map<String, Object> session;

    private String codice;
    
    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    public String getCodice() {
    	return codice;
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
		}
		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);		
		return target;
	}
	
}
