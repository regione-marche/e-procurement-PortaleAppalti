package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'apertura della pagina degli indirizzi dell'impresa
 * nel wizard di registrazione impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class OpenPageIndirizziImpresaAction
	extends
	it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.OpenPageIndirizziImpresaAction {

    /**
     * UID
     */
    private static final long serialVersionUID = -9003055277590742518L;

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
