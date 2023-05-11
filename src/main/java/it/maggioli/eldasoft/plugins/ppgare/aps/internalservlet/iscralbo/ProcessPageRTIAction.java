package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IRaggruppamenti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina della RTI del wizard di
 * iscrizione ad un elenco/catalogo.
 *
 * @author Marco.Perazzetta
 */
public class ProcessPageRTIAction extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ProcessPageRTIAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4130491742260687479L;
	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;
	
	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * ... 
	 */
	protected IRaggruppamenti getSessionHelper() {
		return (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.nextResultAction = iscrizioneHelper.getPreviousAction(WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI);
		}
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		if(helper.getImpresa().isConsorzio() || this.getRti() == 1) {
			if(!helper.getStepNavigazione().contains(WizardIscrizioneHelper.STEP_DETTAGLI_RTI)) {
				helper.getStepNavigazione().add(2, WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
			}
			if(this.getRti() == 1) {
				helper.setRti(true);				
				if(helper.getComponentiRTI().size() <= 0) {
					// aggiungi la mandataria all'elenco dei componenti RTI...
					ComponenteHelper c = new ComponenteHelper();
					c.setCodiceFiscale(helper.getImpresa().getDatiPrincipaliImpresa().getCodiceFiscale());
					c.setNazione(helper.getImpresa().getDatiPrincipaliImpresa().getNazioneSedeLegale());
					c.setPartitaIVA(helper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA());
					c.setRagioneSociale(helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
					c.setTipoImpresa(helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa());
					if(helper.getComponentiRTI().isEmpty()) {
						helper.getComponentiRTI().add(0, c);
					}
				}					
				helper.setDenominazioneRTI(this.getDenominazioneRTI());
			} else {
				helper.setRti(false);
				if(!helper.getImpresa().isConsorzio()) {
					helper.getStepNavigazione().remove(WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
				}
			}
		} else {
			helper.setRti(false);
			helper.getStepNavigazione().remove(WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
		}

		this.nextResultAction = helper.getNextAction(WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI);
		
		if(helper.isRti() || helper.getImpresa().isConsorzio()) {
			this.nextResultAction = this.nextResultAction + "Clear"; 
		}
		
		return target;
	}
	
}
