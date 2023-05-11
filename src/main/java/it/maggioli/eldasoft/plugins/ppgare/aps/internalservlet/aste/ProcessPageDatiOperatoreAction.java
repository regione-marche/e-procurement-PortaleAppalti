package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dell'impresa del wizard di
 * offerta di un'asta
 *
 * @author ...
 */
public class ProcessPageDatiOperatoreAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7893486273804482022L;
	
	private int idFirmatarioSelezionatoInLista;

	public int getIdFirmatarioSelezionatoInLista() {
		return idFirmatarioSelezionatoInLista;
	}

	public void setIdFirmatarioSelezionatoInLista(int idFirmatarioSelezionatoInLista) {
		this.idFirmatarioSelezionatoInLista = idFirmatarioSelezionatoInLista;
	}

	/**
	 * ... 
	 */
	public ProcessPageDatiOperatoreAction() {
		 super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA,
			   WizardOffertaAstaHelper.STEP_DATI_OPERATORE, 
			   true);
	} 

	/**
	 * ... 
	 */
	@Override
	public String next() {
		return this.helperNext();
	}
		
}
