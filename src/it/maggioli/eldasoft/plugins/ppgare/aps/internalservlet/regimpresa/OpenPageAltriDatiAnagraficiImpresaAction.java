package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'apertura della pagina degli altri dati anagrafici per
 * il libero professionista (al posto della pagina dei soggetti) nel wizard di
 * registrazione impresa.
 * 
 * @author Stefano.Sabbadin
 * @since 1.6
 */
public class OpenPageAltriDatiAnagraficiImpresaAction
		extends
		it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.OpenPageAltriDatiAnagraficiImpresaAction {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 6633300446096137070L;

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
