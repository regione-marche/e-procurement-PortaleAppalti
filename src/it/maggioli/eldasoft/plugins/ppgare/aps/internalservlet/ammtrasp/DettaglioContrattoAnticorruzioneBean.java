package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.OperatoreInvitatoType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DettaglioContrattoAnticorruzioneBean implements Serializable{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	private String cig;
	private String codicefiscaleProponente;	
	private String denominazioneProponente;
	private String oggetto;
	private String sceltaContraente;
	private String codiceAggiudicataria;
	private String[] ragioneSocialeAggiudicataria;
	private String[] codiceFiscaleAggiudicataria;
	private String[] pivaAggiudicataria;
	private OperatoreInvitatoType[] rtiAggiudicataria;
	private List<OperatoreInvitatoType> elencoOperatoriInvitati = new ArrayList<OperatoreInvitatoType>();
	private Double importoAggiudicazione;
	private Double importoContratto;
	private Date dataInizio;
	private Date dataUltimazione;
	private Double importoSommeLiquidate;
	
	public String getCig() {
		return cig;
	}

	public void setCig(String cig) {
		this.cig = cig;
	}

	public String getCodicefiscaleProponente() {
		return codicefiscaleProponente;
	}

	public void setCodicefiscaleProponente(String codicefiscaleProponente) {
		this.codicefiscaleProponente = codicefiscaleProponente;
	}
	
	public String getDenominazioneProponente() {
		return denominazioneProponente;
	}
	
	public void setDenominazioneProponente(String denominazioneProponente) {
		this.denominazioneProponente = denominazioneProponente;
	}
	
	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	public String getSceltaContraente() {
		return sceltaContraente;
	}
	
	public void setSceltaContraente(String sceltaContraente) {
		this.sceltaContraente = sceltaContraente;
	}
	
	public String getCodiceAggiudicataria() {
		return codiceAggiudicataria;
	}
	
	public void setCodiceAggiudicataria(String codiceAggiudicataria) {
		this.codiceAggiudicataria = codiceAggiudicataria;
	}

	public String[] getRagioneSocialeAggiudicataria() {
		return ragioneSocialeAggiudicataria;
	}

	public void setRagioneSocialeAggiudicataria(
			String[] ragioneSocialeAggiudicataria) {
		this.ragioneSocialeAggiudicataria = ragioneSocialeAggiudicataria;
	}
	
	public String[] getCodiceFiscaleAggiudicataria() {
		return codiceFiscaleAggiudicataria;
	}
	
	public void setCodiceFiscaleAggiudicataria(
			String[] codiceFiscaleAggiudicataria) {
		this.codiceFiscaleAggiudicataria = codiceFiscaleAggiudicataria;
	}
	
	public String[] getPivaAggiudicataria() {
		return pivaAggiudicataria;
	}
	
	public void setPivaAggiudicataria(String[] pivaAggiudicataria) {
		this.pivaAggiudicataria = pivaAggiudicataria;
	}
	
	public Double getImportoAggiudicazione() {
		return importoAggiudicazione;
	}
	
	public void setImportoAggiudicazione(Double importoAggiudicazione) {
		this.importoAggiudicazione = importoAggiudicazione;
	}
	
	public Double getImportoContratto() {
		return importoContratto;
	}

	public void setImportoContratto(Double importoContratto) {
		this.importoContratto = importoContratto;
	}

	public Date getDataInizio() {
		return dataInizio;
	}
	
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	
	public Date getDataUltimazione() {
		return dataUltimazione;
	}
	
	public void setDataUltimazione(Date dataUltimazione) {
		this.dataUltimazione = dataUltimazione;
	}
	
	public Double getImportoSommeLiquidate() {
		return importoSommeLiquidate;
	}

	public void setImportoSommeLiquidate(Double importoSommeLiquidate) {
		this.importoSommeLiquidate = importoSommeLiquidate;
	}

	public OperatoreInvitatoType[] getRtiAggiudicataria() {
		return rtiAggiudicataria;
	}

	public void setRtiAggiudicataria(OperatoreInvitatoType[] operatoreInvitatoTypes) {
		this.rtiAggiudicataria = operatoreInvitatoTypes;
	}

	public List<OperatoreInvitatoType> getElencoOperatoriInvitati() {
		return elencoOperatoriInvitati;
	}
	
	public void setElencoOperatoriInvitati(List<OperatoreInvitatoType> elencoOperatoriInvitati) {
		this.elencoOperatoriInvitati = elencoOperatoriInvitati;
	}

}
