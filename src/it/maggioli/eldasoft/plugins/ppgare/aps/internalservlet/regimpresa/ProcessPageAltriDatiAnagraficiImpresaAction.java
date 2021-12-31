package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina degli dati anagrafici
 * dell'impresa nel wizard di registrazione di un libero professionista.
 * 
 * @author Stefano.Sabbadin
 * @since 1.6
 */
public class ProcessPageAltriDatiAnagraficiImpresaAction
		extends
		it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ProcessPageAltriDatiAnagraficiImpresaAction {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -8975413233247654707L;

	/**
	 * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
	 * 
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		return helper;
	}

}
