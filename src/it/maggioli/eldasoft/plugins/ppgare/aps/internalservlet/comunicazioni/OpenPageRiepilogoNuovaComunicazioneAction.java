package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class OpenPageRiepilogoNuovaComunicazioneAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6170151936523110668L;

	private String from;
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	// "costanti" per la pagina jsp
	public String getSTEP_TESTO_COMUNICAZIONE() {
		return WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE;
	}

	public String getSTEP_DOCUMENTI() {
		return WizardNuovaComunicazioneHelper.STEP_DOCUMENTI;
	}

	public String getSTEP_INVIO_COMUNICAZIONE() {
		return WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE;
	}
	
	/**
	 * ...
	 */
	public String openPage() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE);
		return SUCCESS;
	}

	/**
	 * ...
	 */
	public String openPageAfterError() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE);
		return SUCCESS;
	}
	
}
