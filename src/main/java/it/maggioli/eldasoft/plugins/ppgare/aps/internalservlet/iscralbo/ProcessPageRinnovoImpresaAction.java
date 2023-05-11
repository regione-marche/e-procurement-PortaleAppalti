package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class ProcessPageRinnovoImpresaAction extends ProcessPageImpresaAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 6747525355850166607L;
	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;

	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		if (rinnovoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			if (!rinnovoHelper.getImpresa().validate()) {
				this.addActionError(this.getText("Errors.datiImpresaIncompleti"));
				target = INPUT;
			} else {
				this.nextResultAction = rinnovoHelper.getNextAction(WizardRinnovoHelper.STEP_IMPRESA);
			}
		}
		return target;
	}

}
