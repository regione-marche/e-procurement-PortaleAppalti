package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei dati ulteriori
 * dell'impresa nel wizard di registrazione di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageDatiUlterioriImpresaAction 
	extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ProcessPageDatiUlterioriImpresaAction {

    /**
     * UID
     */
    private static final long serialVersionUID = 812437802371671000L;

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
