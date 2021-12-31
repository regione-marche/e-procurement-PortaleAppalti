package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.contratti;

import java.io.Serializable;

/**
 * Bean per la raccolta dei criteri di filtro nella form di ricerca contratti.
 * 
 * @author Stefano.Sabbadin
 * @since 1.9.1
 */
public class ContrattiSearchBean implements Serializable {
    /**
	 * UID
	 */
	private static final long serialVersionUID = 920515680446379840L;

	private String stazioneAppaltante;
    private String oggetto;
    private String cig;
    private String stato;
    
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	public String getCig() {
		return cig;
	}
	
	public void setCig(String cig) {
		this.cig = cig;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
	
}
