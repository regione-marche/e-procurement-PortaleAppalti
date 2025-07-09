package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;


public class WizardSoccorsoIstruttorioHelper extends WizardNuovaComunicazioneHelper {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2123191234424549946L;

	
    public WizardSoccorsoIstruttorioHelper() {
		super();
		
		// ridefinisci gli step del wizard
		STEP_TESTO_COMUNICAZIONE	= "testoSoccorso";
		STEP_DOCUMENTI				= "documentiSoccorso";
		STEP_INVIO_COMUNICAZIONE	= "inviaSoccorso";
		
    	this.setDocumenti( new DocumentiComunicazioneHelper(true, true) );
	}
	
}
