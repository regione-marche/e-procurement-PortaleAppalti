package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class OpenPageRiepilogoNuovoSoccorsoAction extends OpenPageRiepilogoNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2458123963361782728L;

	/**
	 * costruttore
	 */
	public OpenPageRiepilogoNuovoSoccorsoAction() {
		super(new WizardSoccorsoIstruttorioHelper());
	}

//	/**
//	 * ...
//	 */
//	@Override
//	public String openPage() {
//		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE);
//		return SUCCESS;
//	}

//	/**
//	 * ...
//	 */
//	@Override
//	public String openPageAfterError() {
//		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE);
//		return SUCCESS;
//	}

}
