package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import java.io.Serializable;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

public class ContrattiAnticorruzioneSearchBean implements Serializable{
	
	private static final long serialVersionUID = 199846384792440201L;
	
	@Validate(EParamValidation.CIG)
	private String cig;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.OGGETTO)
	private String oggetto;
	@Validate(EParamValidation.TIPO_APPALTO)
	private String tipoAppalto;
	@Validate(EParamValidation.GENERIC)
	private String partecipante;
	@Validate(EParamValidation.GENERIC)
	private String aggiudicatario;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataA;
	
	public String getCig() {
		return cig;
	}

	public void setCig(String cig) {
		this.cig = cig;
	}

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
	
	public String getTipoAppalto() {
		return tipoAppalto;
	}

	public void setTipoAppalto(String tipoAppalto) {
		this.tipoAppalto = tipoAppalto;
	}

	public String getPartecipante() {
		return partecipante;
	}

	public void setPartecipante(String partecipante) {
		this.partecipante = partecipante;
	}

	public String getAggiudicatario() {
		return aggiudicatario;
	}

	public void setAggiudicatario(String aggiudicatario) {
		this.aggiudicatario = aggiudicatario;
	}

	public String getDataDa() {
		return dataDa;
	}

	public void setDataDa(String dataDa) {
		this.dataDa = dataDa;
	}

	public String getDataA() {
		return dataA;
	}

	public void setDataA(String dataA) {
		this.dataA = dataA;
	}
	
}

