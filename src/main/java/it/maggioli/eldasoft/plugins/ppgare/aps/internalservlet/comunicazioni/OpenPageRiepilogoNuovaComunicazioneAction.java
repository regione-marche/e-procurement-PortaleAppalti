package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE, EFlussiAccessiDistinti.COMUNICAZIONE_STIPULA })
public class OpenPageRiepilogoNuovaComunicazioneAction extends AbstractOpenPageComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6170151936523110668L;
	
	@Validate(EParamValidation.ACTION)
	private String from;
	
	public String getFrom() {
		return from;
	}

	/**
	 * costruttore
	 */
	public OpenPageRiepilogoNuovaComunicazioneAction() {
		this(new WizardNuovaComunicazioneHelper());
	}
	
	public OpenPageRiepilogoNuovaComunicazioneAction(WizardNuovaComunicazioneHelper helper) {
		super(helper, PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
	}

	/**
	 * ...
	 */
	@Override
	public String openPage() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_INVIO_COMUNICAZIONE);
		this.from = (String)this.session.get(ComunicazioniConstants.SESSION_ID_FROM);
		return SUCCESS;
	}

	/**
	 * ...
	 */
	public String openPageAfterError() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_INVIO_COMUNICAZIONE);
		return SUCCESS;
	}
	
}
