package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class OpenPageNuovoSoccorsoAction extends OpenPageNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4888684312235453401L;

	/**
	 * costruttore
	 */
	public OpenPageNuovoSoccorsoAction() {
		super(new WizardSoccorsoIstruttorioHelper());
	}

//	/**
//	 * ... 
//	 */
//	@Override
//	public String openPage() {
//		super.openPage();
//		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE);
//		return SUCCESS;
//	}

//	/**
//	 * ... 
//	 */
//	@Override
//	public String openPageAfterError() {
//		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE);
//		return this.getTarget();
//	}
	
}
