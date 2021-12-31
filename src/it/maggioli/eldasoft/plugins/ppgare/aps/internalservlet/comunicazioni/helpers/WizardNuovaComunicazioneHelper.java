package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;

import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class WizardNuovaComunicazioneHelper implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7046861513120551063L;

	public static final String STEP_TESTO_COMUNICAZIONE = "testoComunicazione";
	public static final String STEP_DOCUMENTI 			= "documenti";
	public static final String STEP_INVIO_COMUNICAZIONE = "inviaComunicazione";

	/** Stack delle pagine navigate. */
	Stack<String> stepNavigazione;

	private String codice;
	private String oggetto;
	private String testo;
	private DocumentiComunicazioneHelper documenti;
	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private WizardDatiImpresaHelper datiImpresaHelper; 
	private Date dataInvio;
	private Long comunicazioneId;
	private Long idDestinatario;
	private String stazioneAppaltante;
	private String destinatario;
	private String mittente;
	private Long modello;
	private Long tipoBusta;
	private Date dataScadenza;
	

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
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

	public WizardDatiImpresaHelper getDatiImpresaHelper() {
		return datiImpresaHelper;
	}

	public void setDatiImpresaHelper(WizardDatiImpresaHelper datiImpresaHelper) {
		this.datiImpresaHelper = datiImpresaHelper;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
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

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	/**
	 * costruttore 
	 */
    public WizardNuovaComunicazioneHelper() {
    	this.documenti = new DocumentiComunicazioneHelper();
    	this.stepNavigazione = new Stack<String>();
    	this.oggetto = null;
    	this.testo = null;
    	this.comunicazioneId = null;
    	this.idDestinatario = null;
    	this.destinatario = null;
    	this.mittente = null;
    	//this.dataInvio = null;
    	this.modello = null;
    	this.dataScadenza = null;
    	this.documentiRichiesti = new ArrayList<DocumentazioneRichiestaType>();
    	this.modello = null;
    	this.tipoBusta = null;
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
		this.stepNavigazione.push(WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE);
		this.stepNavigazione.push(WizardNuovaComunicazioneHelper.STEP_DOCUMENTI);
		this.stepNavigazione.push(WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE);
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

}
