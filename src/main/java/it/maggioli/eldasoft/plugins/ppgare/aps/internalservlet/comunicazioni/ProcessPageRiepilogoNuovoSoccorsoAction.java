package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class ProcessPageRiepilogoNuovoSoccorsoAction extends ProcessPageRiepilogoNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3029856854443180535L;

	/**
	 * costruttore
	 */
	public ProcessPageRiepilogoNuovoSoccorsoAction() {
		super(new WizardSoccorsoIstruttorioHelper());
	}
	
//	@Override
//	public String back() {
//		String target = super.back();
//
//		if(SUCCESS.equals(target)) {
//			WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper) session
//				.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
//			
//			this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
//					helper.getPreviousStepNavigazione(WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE)));
//		}
//		
//		return target;
//	}

}
