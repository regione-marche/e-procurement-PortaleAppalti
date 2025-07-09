package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;

import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.poi.ss.formula.functions.ImReal;

import java.io.Serializable;
import java.util.*;

public class WizardNuovaComunicazioneHelper implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7046861513120551063L;
		
	// step del wizard 
	public String STEP_TESTO_COMUNICAZIONE		= "testoComunicazione";
	public String STEP_DOCUMENTI				= "documenti";
	public String STEP_INVIO_COMUNICAZIONE		= "inviaComunicazione";
	
	/** Stack delle pagine navigate. */
	Stack<String> stepNavigazione;

	private String codice;
	private String codice2;
	private String tipoRichiesta;
	private String oggetto;
	private String testo;
	private DocumentiComunicazioneHelper documenti;
	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private WizardDatiImpresaHelper impresa; 
	private Date dataInvio;
    private String comunicazioneApplicativo;
	private Long comunicazioneId;
	private Long idDestinatario;
	private String stazioneAppaltante;
	private String destinatario;
	private String mittente;
	private Long modello;
	private Long tipoBusta;
	private int operazione;
	private Date dataScadenza;
	private String entita;
	private boolean isConcorsoDiProgettazione;
	private boolean isConcorsoDiProgettazioneCrypted;
	private boolean isConcEnded;
	private String progressivoOfferta;
	private String from;
	

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice2() {
		return codice2;
	}

	public void setCodice2(String codice2) {
		this.codice2 = codice2;
	}

	public String getTipoRichiesta() {
		return tipoRichiesta;
	}

	public void setTipoRichiesta(String tipoRichiesta) {
		this.tipoRichiesta = tipoRichiesta;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public DocumentiComunicazioneHelper getDocumenti() {
		return documenti;
	}

	public void setDocumenti(DocumentiComunicazioneHelper documenti) {
		this.documenti = documenti;
	}
	
	public List<DocumentazioneRichiestaType> getDocumentiRichiesti() {
		return documentiRichiesti;
	}

	public void setDocumentiRichiesti(List<DocumentazioneRichiestaType> documentiRichiesti) {
		this.documentiRichiesti = documentiRichiesti;
	}

    public Stack<String> getStepNavigazione() {
    	return stepNavigazione;
    }

	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	public void setImpresa(WizardDatiImpresaHelper impresa) {
		this.impresa = impresa;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}
	
    public String getComunicazioneApplicativo() {
      return comunicazioneApplicativo;
    }

    public void setComunicazioneApplicativo(String comunicazioneApplicativo) {
      this.comunicazioneApplicativo = comunicazioneApplicativo;
    }

    public Long getComunicazioneId() {
      return comunicazioneId;
    }

	public void setComunicazioneId(Long comunicazioneId) {
		this.comunicazioneId = comunicazioneId;
	}
		
	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}
	
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	
	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	
	public Long getModello() {
		return modello;
	}

	public void setModello(Long modello) {
		this.modello = modello;
	}

	public Long getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(Long tipoBusta) {
		this.tipoBusta = tipoBusta;
	}
	
	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getEntita() {
		return entita;
	}

	public void setEntita(String entita) {
		this.entita = entita;
	}
	
	public boolean isConcorsoDiProgettazione() {
		return isConcorsoDiProgettazione;
	}
	
	public void setConcorsoDiProgettazione(boolean concorsoDiProgettazione) {
		isConcorsoDiProgettazione = concorsoDiProgettazione;
	}
	
	public void setConcorsoDiProgettazioneCrypted(boolean isConcorsoDiProgettazioneCrypted) {
		this.isConcorsoDiProgettazioneCrypted = isConcorsoDiProgettazioneCrypted;
	}

	public boolean isConcorsoDiProgettazioneCrypted() {
		return isConcorsoDiProgettazioneCrypted;
	}

	public void setConcEnded(boolean isConcEnded) {
		this.isConcEnded = isConcEnded;
	}
	
	public boolean isConcEnded() {
		return isConcEnded;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * costruttore 
	 */
    public WizardNuovaComunicazioneHelper() {
    	// inizializza gli step del wizard!!!
    	this.documenti = new DocumentiComunicazioneHelper();
    	this.stepNavigazione = new Stack<String>();
    	this.tipoRichiesta = null;
    	this.oggetto = null;
    	this.testo = null;
    	this.comunicazioneApplicativo = null;	// id comunicazione di BO/risposta 
    	this.comunicazioneId = null;			// applicativo della comunicazione di BO/risposta
    	this.idDestinatario = null;
    	this.destinatario = null;
    	this.mittente = null;
    	//this.dataInvio = null;
    	this.modello = null;
    	this.dataScadenza = null;
    	this.documentiRichiesti = new ArrayList<DocumentazioneRichiestaType>();
    	this.modello = null;
    	this.tipoBusta = null;
    	this.operazione = 0;
    	this.entita	= null;
    	this.progressivoOfferta = null;
    	this.from = null;
	}
    
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		this.documenti.valueBound(event);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		this.documenti.valueUnbound(event);
	}

	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione.
	 */
	public void fillStepsNavigazione() {
		this.stepNavigazione.push(STEP_TESTO_COMUNICAZIONE);
		this.stepNavigazione.push(STEP_DOCUMENTI);
		this.stepNavigazione.push(STEP_INVIO_COMUNICAZIONE);
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
		int posizione = this.stepNavigazione.indexOf(stepAttuale);
		if (posizione != -1 && posizione + 1 < this.stepNavigazione.size()) {
			step = this.stepNavigazione.elementAt(posizione + 1);

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
		int posizione = this.stepNavigazione.indexOf(stepAttuale);
		if (posizione != -1 && posizione - 1 >= 0) {
			step = this.stepNavigazione.elementAt(posizione - 1);
		}
		return step;
	}

	/**
	 * dato una data ed un'ora calcola la corrispondente data e ora
	 */
	public static Date calcolaDataOra(Calendar data, String ora) {
		Date risultato = null;
		if (data != null && data.getTime() != null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(data.getTime());
			int ore = 24;
			int minuti = 0;
			if (ora != null && !"".equals(ora)) {
				ore = Integer.parseInt(ora.substring(0, ora.indexOf(':')));
				minuti = Integer.parseInt(ora.substring(ora.indexOf(':') + 1));
			}
			gc.set(Calendar.HOUR_OF_DAY, ore);
			gc.set(Calendar.MINUTE, minuti);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			risultato = gc.getTime();
		}
		return risultato;
	}

    public boolean isModelloSoccorsoItruttorio() {
    	return isModelloSoccorsoItruttorio(modello);
    }
    
	public boolean isModelloRettifica() {
    	return isModelloRettifica(modello);
	}
	
    /**
     * restituire true se il modello della comunicazione e' di un soccorso istruttorio 
     */
    public static boolean isModelloSoccorsoItruttorio(Long modello) {
    	boolean soccorso = false; 
    	if(modello != null && modello.longValue() > 0) {
    		soccorso = !isModelloRettifica(modello);
        }
    	return soccorso;
    }

	/**
	 * restituire true se il modello della comunicazione e' di una rettifica
	 */
    public static boolean isModelloRettifica(Long modello) {
    	boolean rettifica = false; 
    	if(modello != null) {
    		rettifica = modello.longValue() == PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_TEC  
    					|| modello.longValue() == PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_TEC
    					|| modello.longValue() == PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_TEC
    					|| modello.longValue() == PortGareSystemConstants.RETTIFICA_BUSTA_TEC
    					|| modello.longValue() == PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_ECO
    					|| modello.longValue() == PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_ECO
    					|| modello.longValue() == PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_ECO
    					|| modello.longValue() == PortGareSystemConstants.RETTIFICA_BUSTA_ECO;
        }
    	return rettifica;
    }
    
}
