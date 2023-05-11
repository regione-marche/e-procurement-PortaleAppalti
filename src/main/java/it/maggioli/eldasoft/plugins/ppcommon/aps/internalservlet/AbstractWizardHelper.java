package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlObject;


public abstract class AbstractWizardHelper implements Serializable {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 7730192512438617259L;

	
	////////////////////////////////////////////////////////////////////////////
	// Gestione dello stack del wizard
	////////////////////////////////////////////////////////////////////////////
	private class StepItem implements Serializable {
		/**
		 * UID 
		 */
		private static final long serialVersionUID = -7542578352512572355L;
		
		String step;
		String nextTarget;
		String prevTarget;
		public StepItem(String step, String nextTarget, String prevTarget) {
			this.step = step;
			this.nextTarget = nextTarget;
			this.prevTarget = prevTarget;
		}
	}
	
	private class IntStack<E> extends Stack<E> {
		/**
		 * UID 
		 */
		private static final long serialVersionUID = 8405721681579621380L;

		public IntStack() {
		}

		public synchronized boolean add(E e) {
			 // sincronizza stepNavigazioneAbilitato con stepNavigazione per 
			 // compatibilità con quei wizard che utilizzano stepNavigazione 
			 // come mezzo per gestire gli step del wizard...
			stepNavigazioneAbilitato.put(e.toString(), true);
			return super.add(e);
		};
	}
	////////////////////////////////////////////////////////////////////////////

	/* ID della chiave in sessione assaciato all'helper */
	protected String sessionKeyHelperId; 
	
	/* mappa utilizzabile dalle pagine JSP per verificare l'abilitazione di uno step */
	private List<StepItem> mappaStepNavigazione;
	
	/* info di configurazione di uno step di navigazione */
	private Map<String, Boolean> stepNavigazioneAbilitato;
	
	/* elenco degli step presenti in un wizard
	 * NB: mantenuto per compatibilità con i wizard già esistenti
	 */
	protected Stack<String> stepNavigazione;
		
	/* mantieni il comportamento per "next" e "prev" del vecchio wizard 
	 * convertito al nuovo modello. Il valore di default e' True*/
	private boolean legacyPrevNext;
	
	
	/**
	 * inizializzazione del wizard helper
	 */
	public AbstractWizardHelper(String sessionKeyHelperId, boolean legacyPrevNext) {
		this.legacyPrevNext = legacyPrevNext;
		this.sessionKeyHelperId = sessionKeyHelperId; 
		this.clearMappaStepNavigazione();
	}
	
	public AbstractWizardHelper() {
		this(null, true);
	}
	
	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione.
	 */	
	abstract public void fillStepsNavigazione();
	
	/**
	 * Restituisce un documento XML da utilizzare per gli allegati xml
	 * delle comunicazioni 
	 */
	abstract public XmlObject getXmlDocument(
			XmlObject document, 
			boolean attachFileContents, 
			boolean report) throws Exception;

	/**
	 * Restituisce un documento XML da utilizzare per gli allegati xml
	 * delle comunicazioni 
	 */
	public XmlObject getXmlDocument(XmlObject document) {
		return null;
	}

	/**
     * Resetta gli step di navigazione del wizard
     */
	public void clearMappaStepNavigazione() {
		this.mappaStepNavigazione = new ArrayList<StepItem>();
		this.stepNavigazioneAbilitato = new HashMap<String, Boolean>();
		this.stepNavigazione = new IntStack<String>();
	}
	
	/**
     * Aggiunge una configurazione alla mappa degli step/target del wizard
     * La mappa deve contenere l'elenco completo degli step del wizard 
     * compresi quelli disabilitati (o che non saranno visualizzati)
     * Ogni riga della mappa definisce una n-upla del tipo  
     * 
     * NB: Gli step vengono creati per default non abilitati, 
     * 	   vanno abilitati nella definizione del metodo "fillStepsNavigazione()" 
     *     utilizzare il metodo abilitaStepNavigazione(...)
     * 
     * 		( step,  next target,  previuos target )
     * 
     * NB: gli step del wizard sono disabilitati per default
     *     vanno abilitati nella definizione del metodo "fillStepsNavigazione()"
     *     		
     * @param step
     * 			step del wizard 
     * @param nextTarget
     * 			target successivo per l'operazione di "Avanti"
     * @param prevTarget
     * 			target precedente per l'operazione di "Indietro"
     */
	public void addMappaStepNavigazione(String step, String nextTarget, String prevTarget) {
		this.mappaStepNavigazione.add(new StepItem(step, nextTarget, prevTarget));
		this.stepNavigazioneAbilitato.put(step, false);	 
	}
	
	/**
	 * @return the stepNavigazioneAbilitato
	 */
	public Map<String, Boolean> getStepNavigazioneAbilitato() {
		return stepNavigazioneAbilitato;
	}

	/**
	 * Abilita/disabilita lo stato di uno step
	 */
	public void abilitaStepNavigazione(String step, boolean abilita) {
		this.stepNavigazioneAbilitato.put(step, abilita);
	}
	
	/**
     * Ritorna lo step di navigazione successivo allo step in input
     * 
     * @param stepAttuale
     *            step attuale
     * @return step successivo previsto dopo l'attuale, null se non previsto
     */
	public String getNextStepNavigazione(String stepAttuale) {
		String step = null;
		int posizione = this.getStep(stepAttuale, true);
		if (0 <= posizione && posizione < this.mappaStepNavigazione.size()) {
			step = this.mappaStepNavigazione.get(posizione).step;
		}
		return step;
	}

    /**
     * Ritorna lo step di navigazione precedente lo step in input
     * 
     * @param stepAttuale
     *            step attuale
     * @return step precedente previsto dopo l'attuale, null se non previsto
     */
	public String getPreviousStepNavigazione(String stepAttuale) {
		String step = null;
		int posizione = this.getStep(stepAttuale, false);
		if (0 <= posizione && posizione < this.mappaStepNavigazione.size()) {
			step = this.mappaStepNavigazione.get(posizione).step;
		}
		return step;
	}

	/**
     * Ritorna il target dello step di navigazione successivo allo step in input
     * 
     * @param stepAttuale
     *            step attuale
     * @return target associato allo step successivo all'attuale, null se non previsto
     */
	public String getNextStepTarget(String stepAttuale) {
		String target = null;
		int posizione = this.getStep(stepAttuale, true);
		if (0 < posizione && posizione < this.mappaStepNavigazione.size()) {
			target = this.mappaStepNavigazione.get(posizione - 1).nextTarget;
		}
		return target;
	}
	
	/**
     * Ritorna il target dello step di navigazione precedente allo step in input
     * 
     * @param stepAttuale
     *            step attuale
     * @return target associato allo step precedente all'attuale, null se non previsto
     */
	public String getPreviousStepTarget(String stepAttuale) {
		String target = null;
		int posizione = this.getStep(stepAttuale, false);
		if (0 <= posizione && posizione < this.mappaStepNavigazione.size() - 1) {
			target = this.mappaStepNavigazione.get(posizione + 1).prevTarget;
		}
		return target;
	}
		
	/**
     * trova lo step di navigazione successivo/precedente allo step in input
     * 
     * @param stepAttuale
     *            step attuale
     *            
     * @return target associato allo step precedente all'attuale, null se non previsto
     */
	private int getStep(String stepAttuale, boolean avanti) {
		int indexStepNuovo = -1;
		int indexStepAttuale = -1;
		if(this.mappaStepNavigazione != null && this.stepNavigazioneAbilitato != null) {
			// trova lo step attuale
			for(int k = 0; k < this.mappaStepNavigazione.size(); k++) {
				if(this.mappaStepNavigazione.get(k).step.equals(stepAttuale)) {
					indexStepAttuale = k;
					break;
				}	
			}
			if(indexStepAttuale >= 0) {
				// trova il prossimo step di navigazione abilitato 
				if(avanti) {		// NEXT >
					for(int k = indexStepAttuale+1; k < this.mappaStepNavigazione.size(); k++) {
						if(this.stepNavigazioneAbilitato.get(this.mappaStepNavigazione.get(k).step)) {
							indexStepNuovo = k;
							break;
						}	
					}
				} else {			// < BACK
					for(int k = indexStepAttuale-1; k >= 0; k--) {
						if(this.stepNavigazioneAbilitato.get(this.mappaStepNavigazione.get(k).step)) {
							indexStepNuovo = k;
							break;
						}
					}
				}
				
				if(indexStepNuovo >= 0) {
					// NB: i vecchi Wizard elper riadattati partendo da questa 
					//     classe base, potrebbero gestire in modo non standard 
					//     il target restituito nelle action a loro associati. 
					//     Per mantenere il comportamento di tali wizard la 
					//     proprieta' 'legacyPrevNext' indica se preservare il 
					//     comporamento originario o meno.
					if(this.legacyPrevNext) {
						if(Math.abs(indexStepAttuale-indexStepNuovo) == 1) {
							// se non ci sono step disabilitati e quindi nessun
							// salto di step...
							// (Es: "success" oppure "back")
							indexStepNuovo = -1;
						} else {
							// se ci sono salti di step (quindi step disabilitati)
							// restituisce il nuovo step "indexStepNuovo"
							// (Es: "successSkip..." oppure "backSkip...")
						}
					}
				}
			}
		}
		return indexStepNuovo;
	}

	/**
	 * Rimuove dalla sessione i dati per gestire l'helper 
	 */
	public Object removeFromSession() {
		if(this.sessionKeyHelperId != null) {
			return ServletActionContext.getContext().getSession().remove(this.sessionKeyHelperId);
		}
		return null;
	}

	/**
	 * Recupera dalla sessione i dati per gestire l'helper 
	 */	
	public Object getFromSession() {
		if(this.sessionKeyHelperId != null) {
			return ServletActionContext.getContext().getSession().get(this.sessionKeyHelperId);
		}
		return null;
	}

	/**
	 * Salva in sessione i dati per gestire l'helper
	 */
	public void putToSession() {
		if(this.sessionKeyHelperId != null) {
			ServletActionContext.getContext().getSession().put(this.sessionKeyHelperId, this);
		}
	}
	
	/**
	 * recupera il documento XML tra gli allegati della comunicazione  
	 */
	public String getAllegatoXml(ComunicazioneType comunicazione, String nomeFileXml) {
		String doc = null;
		AllegatoComunicazioneType allegatoXml = null;
		if(comunicazione.getAllegato() != null && StringUtils.isNotEmpty(nomeFileXml)) {
			// si cerca l'xml con i dati del documento tra tutti gli allegati
			int i = 0;
			while (i < comunicazione.getAllegato().length && allegatoXml == null) {
				if (nomeFileXml.equals(comunicazione.getAllegato()[i].getNomeFile())) {
					allegatoXml = comunicazione.getAllegato()[i];
				}
				i++;
			}
			if (allegatoXml == null) {
				// non dovrebbe succedere mai...si inserisce questo
				// controllo per blindare il codice da eventuali
				// comportamenti anomali
				// gestisci tracciatura e messaggio di errore nel chiamante... 
				doc = null;
			} else {
				doc = new String(allegatoXml.getFile());
			}
		}
		return doc;
	}

}
