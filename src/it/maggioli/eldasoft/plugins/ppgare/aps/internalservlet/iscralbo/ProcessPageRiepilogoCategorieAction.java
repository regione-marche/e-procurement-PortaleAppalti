package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'avanzamento a partire dalla pagina di riepilogo delle
 * categorie. Di fatto non fa nulla, viene presentato semplicemente il riepilogo
 * ed i bottoni per andare avanti, indietro ed annullare.
 * 
 * @author Stefano.Sabbadin
 */
public class ProcessPageRiepilogoCategorieAction extends AbstractProcessPageAction 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2996460471368061927L;
	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;
	
	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.nextResultAction = helper.getPreviousAction(WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE);
		}
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			
		this.nextResultAction = helper.getNextAction(WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE);
		
		return target;
	}

}
