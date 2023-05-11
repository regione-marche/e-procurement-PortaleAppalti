package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class ProcessPageRinnovoDocumentiAction extends ProcessPageDocumentiAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8229032804443673207L;
	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;
	
	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * ... 
	 */
	public String next() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		this.nextResultAction = helper.getNextAction(WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO);
	
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		// nel caso di assenza di categorie si deve saltare alla pagina
		// precedente (a sua volta, se esiste un'unica stazione appaltante
		// si deve saltare direttamente alla pagina precedente)
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		this.nextResultAction = helper.getPreviousAction(WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO);
		
		return target;
	}
	
}
