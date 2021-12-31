package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'apertura della pagina dell'impresa del wizard
 * d'iscrizione all'albo
 * 
 * @author Stefano.Sabbadin
 */
public class OpenPageImpresaAction extends AbstractOpenPageAction {
	/**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    
    /** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	
	public String getSTEP_IMPRESA() {
		return WizardIscrizioneHelper.STEP_IMPRESA;
	}

	public String getSTEP_DENOMINAZIONE_RTI() {
		return WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI;
	}

	public String getSTEP_DETTAGLI_RTI() {
		return WizardIscrizioneHelper.STEP_DETTAGLI_RTI;
	}

	public String getSTEP_SELEZIONE_CATEGORIE() {
		return WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE;
	}
	
	public String getSTEP_RIEPILOGO_CAGTEGORIE() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE;
	}
    
	public String getSTEP_SCARICA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE;
	}
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA() {
		return WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA;
	}
	
	public String getSTEP_PRESENTA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE;
	}
    
	/**
	 * ... 
	 */
    public String openPage() {
	    this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardIscrizioneHelper.STEP_IMPRESA);
	    this.checkDatiImpresaHelper();
	    return this.getTarget();
    }

	/**
	 * ... 
	 */
    private void checkDatiImpresaHelper() {
	    // Attenzione!!! 
	    //   dal wizard di gestione delle buste di una domanda di partecipazione 
	    //   o di invio offerta, se dallo step dei dati dell'impresa si va in 
	    //   modifica (pulsate "modifica") e successivamente si annulla 
	    //   l'operazione, l'action 'CancelAggiornamentoAction' rimuove sempre 
	    //   dalla sessione l'helper dei dati impresa utilizzato dal wizard 
	    //   che alla conferma dei dati della domanda di partecipazione o di 
	    //   invio offerta va in eccezione con "nullPointer"
	    // Fix:
	    //   quando apro lo step dei dati dell'impresa, verifico se lo helper
	    //   esiste ancora in sessione ed eventualmente lo rimetto in sessione
		try {
			WizardDatiImpresaHelper helper = 
				(WizardDatiImpresaHelper)this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);			
			if(helper == null) {
				helper = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(), 
						this, 
						this.appParamManager);				
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, 
						 		 helper);
			} 

			WizardPartecipazioneHelper partecipazioneHelper = 
				(WizardPartecipazioneHelper)this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			if(partecipazioneHelper != null) {
				if(partecipazioneHelper.getImpresa() == null) {
					partecipazioneHelper.setImpresa(helper); 
				}
				partecipazioneHelper.abilitaStepNavigazione(
					WizardPartecipazioneHelper.STEP_COMPONENTI, 
					partecipazioneHelper.isStepComponentiAbilitato());
			}
			
		} catch (XmlException t) {
			ApsSystemUtils.logThrowable(t, this, "checkDatiImpresaHelper");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkDatiImpresaHelper");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} 
    }

    /**
     * ...
     */
    public String clear() {
    	this.setTarget(SUCCESS);
	    return this.getTarget();
    }
    
}
