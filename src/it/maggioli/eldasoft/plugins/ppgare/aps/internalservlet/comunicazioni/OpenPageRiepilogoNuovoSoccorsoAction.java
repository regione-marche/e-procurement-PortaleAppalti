package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ... 
 * 
 */
public class OpenPageRiepilogoNuovoSoccorsoAction extends OpenPageRiepilogoNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2458123963361782728L;

	
	// "costanti" per la pagina jsp
	@Override
	public String getSTEP_TESTO_COMUNICAZIONE() {
		return WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE;
	}

	@Override
	public String getSTEP_DOCUMENTI() {
		return WizardSoccorsoIstruttorioHelper.STEP_DOCUMENTI;
	}

	@Override
	public String getSTEP_INVIO_COMUNICAZIONE() {
		return WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE;
	}

	/**
	 * ...
	 */
	@Override
	public String openPage() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE);
		return SUCCESS;
	}

	/**
	 * ...
	 */
	@Override
	public String openPageAfterError() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE);
		return SUCCESS;
	}
	
}
