package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.consulenti;

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
	
	private String stazioneAppaltante;
	private String data;
	private Double compensoPrevisto;
	private String parteVariabile;
	private String soggettoPercettore;
	private String protocollo;
	private String dataDa;
	private String dataA;
	private String ragioneIncarico;
	private String compensoPrevistoDa;
	private String compensoPrevistoA;
	private String oggettoPrestazione; 
	private String tipoProcedura;      
	private String numeroPartecipanti; 
	private String iddocdg;
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