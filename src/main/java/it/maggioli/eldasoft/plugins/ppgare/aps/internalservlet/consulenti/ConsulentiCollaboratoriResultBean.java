package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.consulenti;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

/**
 *
 * @author ...
 */
public class ConsulentiCollaboratoriResultBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7337309254881513943L;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String data;
	private Double compensoPrevisto;
	@Validate(EParamValidation.GENERIC)
	private String parteVariabile;
	@Validate(EParamValidation.GENERIC)
	private String soggettoPercettore;
	@Validate(EParamValidation.GENERIC)
	private String protocollo;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataA;
	@Validate(EParamValidation.GENERIC)
	private String ragioneIncarico;
	@Validate(EParamValidation.IMPORTO)
	private String compensoPrevistoDa;
	@Validate(EParamValidation.IMPORTO)
	private String compensoPrevistoA;
	@Validate(EParamValidation.GENERIC)
	private String oggettoPrestazione;
	@Validate(EParamValidation.TIPO_PROCEDURA)
	private String tipoProcedura;
	@Validate(EParamValidation.INTERO)
	private String numeroPartecipanti;
	@Validate(EParamValidation.GENERIC)
	private String iddocdg;
	@Validate(EParamValidation.GENERIC)
	private String idprg;
	
	public ConsulentiCollaboratoriResultBean() {
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}
 
	public String getSoggettoPercettore() {
		return soggettoPercettore;
	}

	public void setSoggettoPercettore(String soggettoPercettore) {
		this.soggettoPercettore = soggettoPercettore;
	}

	public String getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getRagioneIncarico() {
		return ragioneIncarico;
	}

	public void setRagioneIncarico(String ragioneIncarico) {
		this.ragioneIncarico = ragioneIncarico;
	}

	public Double getCompensoPrevisto() {
		return compensoPrevisto;
	}

	public void setCompensoPrevisto(Double compensoPrevisto) {
		this.compensoPrevisto = compensoPrevisto;
	}

	public String getParteVariabile() {
		return parteVariabile;
	}

	public void setParteVariabile(String parteVariabile) {
		this.parteVariabile = parteVariabile;
	}

	public String getIddocdg() {
		return iddocdg;
	}

	public void setIddocdg(String iddocdg) {
		this.iddocdg = iddocdg;
	}

	public String getIdprg() {
		return idprg;
	}

	public void setIdprg(String idprg) {
		this.idprg = idprg;
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

	public String getCompensoPrevistoA() {
		return compensoPrevistoA;
	}

	public void setCompensoPrevistoA(String compensoPrevistoA) {
		this.compensoPrevistoA = compensoPrevistoA;
	}

	public String getCompensoPrevistoDa() {
		return compensoPrevistoDa;
	}

	public void setCompensoPrevistoDa(String compensoPrevistoDa) {
		this.compensoPrevistoDa = compensoPrevistoDa;
	}

	public String getOggettoPrestazione() {
		return oggettoPrestazione;
	}

	public void setOggettoPrestazione(String oggettoPrestazione) {
		this.oggettoPrestazione = oggettoPrestazione;
	}

	public String getTipoProcedura() {
		return tipoProcedura;
	}

	public void setTipoProcedura(String tipoProcedura) {
		this.tipoProcedura = tipoProcedura;
	}

	public String getNumeroPartecipanti() {
		return numeroPartecipanti;
	}

	public void setNumeroPartecipanti(String numeroPartecipanti) {
		this.numeroPartecipanti = numeroPartecipanti;
	}
	
}