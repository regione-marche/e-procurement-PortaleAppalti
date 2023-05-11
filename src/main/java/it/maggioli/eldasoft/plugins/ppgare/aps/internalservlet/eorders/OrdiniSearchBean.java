package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;

import java.io.Serializable;

/**
 * Bean per la raccolta dei criteri di filtro nella form di ricerca ordini.
 * 
 * @author 
 * @since 
 */
public class OrdiniSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2404704154014775548L;

	private String stazioneAppaltante;
	private String cig;
	private String gara;
	private String oggetto;
	private String dataPubblicazioneDa;
	private String dataPubblicazioneA;
	private String dataScadenzaDa;
	private String dataScadenzaA;
	private String stato;
	
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}

	public String getCig() {
		return cig;
	}
	
	public void setCig(String cig) {
		this.cig = cig;
	}
	
	public String getGara() {
		return gara;
	}
	
	public void setGara(String gara) {
		this.gara = gara;
	}
	
	public String getOggetto() {
		return oggetto;
	}
	
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	public String getDataPubblicazioneDa() {
		return dataPubblicazioneDa;
	}
	
	public void setDataPubblicazioneDa(String dataPubblicazioneDa) {
		this.dataPubblicazioneDa = dataPubblicazioneDa;
	}
	
	public String getDataPubblicazioneA() {
		return dataPubblicazioneA;
	}
	
	public void setDataPubblicazioneA(String dataPubblicazioneA) {
		this.dataPubblicazioneA = dataPubblicazioneA;
	}
	
	public String getDataScadenzaDa() {
		return dataScadenzaDa;
	}
	
	public void setDataScadenzaDa(String dataScadenzaDa) {
		this.dataScadenzaDa = dataScadenzaDa;
	}
	
	public String getDataScadenzaA() {
		return dataScadenzaA;
	}
	
	public void setDataScadenzaA(String dataScadenzaA) {
		this.dataScadenzaA = dataScadenzaA;
	}

	public String getStato() {
		return stato;
	}
	
	public void setStato(String stato) {
		this.stato = stato;
	}
	
}
