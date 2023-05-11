package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;


public class OpenPageRinnovoImpresaAction extends OpenPageImpresaAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 7263566260303736980L;
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO() {
		return WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO;
	}
	
	/**
	 * ...
	 */
	public String openPage() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardRinnovoHelper.STEP_IMPRESA);
		return this.getTarget();
	}
	
}
