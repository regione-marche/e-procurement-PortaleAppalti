package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei soggetti dell'impresa
 * nel wizard di registrazione di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageSoggettiImpresaAction
	extends
	it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ProcessPageSoggettiImpresaAction {

    /**
     * UID
     */
    private static final long serialVersionUID = 1192839295359192770L;

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
