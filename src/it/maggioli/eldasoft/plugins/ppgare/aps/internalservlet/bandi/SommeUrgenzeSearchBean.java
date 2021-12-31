package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;

import java.io.Serializable;

public class SommeUrgenzeSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6738965901696590657L;
	
	private String stazioneAppaltante;
	private String codice;	
	private String oggetto;
	private String tipoAppalto;
	private String cig;
	private String dataPubblicazioneDa;
	private String dataPubblicazioneA;

	
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}
		
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
	
	public String getTipoAppalto() {
		return tipoAppalto;
	}
	
	public void setTipoAppalto(String tipoAppalto) {
		this.tipoAppalto = tipoAppalto;
	}
	
	public String getCig() {
		return cig;
	}
	
	public void setCig(String cig) {
		this.cig = cig;
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

}
