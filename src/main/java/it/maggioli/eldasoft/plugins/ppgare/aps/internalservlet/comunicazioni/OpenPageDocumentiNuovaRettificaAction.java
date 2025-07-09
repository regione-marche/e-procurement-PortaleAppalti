package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class OpenPageDocumentiNuovaRettificaAction extends OpenPageDocumentiNuovaComunicazioneAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 2327538705630930812L;

	/**
	 * costruttore
	 */
	public OpenPageDocumentiNuovaRettificaAction() {
		super(new WizardRettificaHelper());		
	}

}
