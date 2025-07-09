package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;


/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class ProcessPageNuovoSoccorsoAction extends ProcessPageNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6709960936656849031L;

	/**
	 * costruttore
	 */
	public ProcessPageNuovoSoccorsoAction() {
		super(new WizardSoccorsoIstruttorioHelper());
	}

//	@Override
//	public String next() {
//		String target = super.next();
//		
//		WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper) this.session
//			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
//		
//		if(SUCCESS.equals(target)) {
//			this.nextResultAction = InitNuovaComunicazioneAction.setNextResultAction(
//					helper.getNextStepNavigazione(WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE));
//		}
//		
//		return target;
//		
//	}
	
}
