package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei dati riepilogativi 
 * dell'ultima offerta del wizard di offerta ad un'asta.
 *
 * @author ...
 */
public class ProcessPageDatiOffertaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * ... 
	 */
	public ProcessPageDatiOffertaAction() {
		super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA,
			  WizardOffertaAstaHelper.STEP_DATI_OFFERTA,
			  true);
	} 

	/**
	 * ... 
	 */
	@Override
	public String next() {
		return this.helperNext();
	}
	
	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = this.helperBack();
		return (SUCCESS.equalsIgnoreCase(target) ? "back" : target);
	}
	
}
