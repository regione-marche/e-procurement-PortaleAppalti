package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;


public class WizardSoccorsoIstruttorioHelper extends WizardNuovaComunicazioneHelper {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2123191234424549946L;

	public static final String STEP_TESTO_COMUNICAZIONE = "testoSoccorso";
	public static final String STEP_DOCUMENTI 			= "documentiSoccorso";
	public static final String STEP_INVIO_COMUNICAZIONE = "inviaSoccorso";
	/**
	 * costruttore 
	 */
    public WizardSoccorsoIstruttorioHelper() {
    	super();
    	this.setDocumenti( new DocumentiComunicazioneHelper(true, true) );
	}
	
	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione.
	 */
	public void fillStepsNavigazione() {
		this.stepNavigazione.clear();
		this.stepNavigazione.push(WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE);
		this.stepNavigazione.push(WizardSoccorsoIstruttorioHelper.STEP_DOCUMENTI);
		this.stepNavigazione.push(WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE);
	}
	
}
