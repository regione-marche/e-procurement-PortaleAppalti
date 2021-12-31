package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Bean per la raccolta dei criteri di filtro nelle form di ricerca bandi.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.0
 */
public class BandiSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6872864439830197455L;

    private String stazioneAppaltante;
    private String oggetto;
    private String cig;
    private String tipoAppalto;
    private String dataPubblicazioneDa;
    private String dataPubblicazioneA;
    private String dataScadenzaDa;
    private String dataScadenzaA;
    private String stato;
    private String esito;
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

	public String getDataScadenzaDa() {
		return dataScadenzaDa;
	}

	public void setDataScadenzaDa(String dataScadenzaDa) {
		if (!"".equals(dataScadenzaDa))
			this.dataScadenzaDa = dataScadenzaDa;
	}

	public String getDataScadenzaA() {
		return dataScadenzaA;
	}

	public void setDataScadenzaA(String dataScadenzaA) {
		if (!"".equals(dataScadenzaA))
			this.dataScadenzaA = dataScadenzaA;
	}

	public String getProceduraTelematica() {
		return proceduraTelematica;
	}

	public void setProceduraTelematica(String proceduraTelematica) {
		this.proceduraTelematica = proceduraTelematica;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
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
