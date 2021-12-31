package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import java.io.Serializable;

/**
 * Bean per la raccolta dei criteri di filtro nelle form di ricerca esiti.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.0
 */
public class EsitiSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5202673970729673878L;

    private String stazioneAppaltante;
    private String oggetto;
    private String cig;
    private String tipoAppalto;
    private String dataPubblicazioneDa;
    private String dataPubblicazioneA;
    private String proceduraTelematica;
    private String altriSoggetti;
    private String sommaUrgenza;
    

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

	public String getTipoAppalto() {
		return tipoAppalto;
	}

	public void setTipoAppalto(String tipoAppalto) {
		this.tipoAppalto = tipoAppalto;
	}
	
	public String getDataPubblicazioneDa() {
		return dataPubblicazioneDa;
	}

	public void setDataPubblicazioneDa(String dataTermineDa) {
		if (!"".equals(dataTermineDa))
			this.dataPubblicazioneDa = dataTermineDa;
	}

	public String getDataPubblicazioneA() {
		return dataPubblicazioneA;
	}

	public void setDataPubblicazioneA(String dataTermineA) {
		if (!"".equals(dataTermineA))
			this.dataPubblicazioneA = dataTermineA;
	}

	public String getProceduraTelematica() {
		return proceduraTelematica;
	}

	public void setProceduraTelematica(String proceduraTelematica) {
		this.proceduraTelematica = proceduraTelematica;
	}

	public String getAltriSoggetti() {
		return altriSoggetti;
	}

	public void setAltriSoggetti(String altriSoggetti) {
		this.altriSoggetti = altriSoggetti;
	}

	public String getSommaUrgenza() {
		return sommaUrgenza;
	}

	public void setSommaUrgenza(String sommaUrgenza) {
		this.sommaUrgenza = sommaUrgenza;
	}
	
}
