package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import org.apache.commons.lang.StringUtils;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class OpenPageRiepilogoNuovaRettificaAction extends OpenPageRiepilogoNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2750667222236254175L;

	private static final String TARGET_BACK_TO_DETTAGLIO 	= "backToDettaglio";

	@Validate(EParamValidation.ACTION)
	protected String actionName;
	@Validate(EParamValidation.ACTION)
	protected String namespace;
	
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * costruttore
	 */
	public OpenPageRiepilogoNuovaRettificaAction() {
		super(new WizardRettificaHelper());
	}
	
	@Override
	public String openPageAfterError() {
		String target = super.openPageAfterError();	
		
		// gestione specifica dell'errore per le rettifiche
		WizardRettificaHelper helper = (WizardRettificaHelper)getWizardFromSession();
		if(helper != null) {
			target = TARGET_BACK_TO_DETTAGLIO;
			
			boolean fromComunicazioni = ("rispondi".equals(helper.getFrom()));
			if(fromComunicazioni) {
				// dettaglio comunicazione
				actionName = "openPageDettaglioComunicazioneRicevuta";
				namespace = "/do/FrontEnd/Comunicazioni";
			} else {
				// riepilogo offerta
				actionName = (StringUtils.isEmpty(helper.getCodice2()) ? "openRiepilogoOfferta" : "riepilogoOfferteDistinte");
				namespace = "/do/FrontEnd/GareTel";
			}
		}
		
		return target;
	}
	
}
