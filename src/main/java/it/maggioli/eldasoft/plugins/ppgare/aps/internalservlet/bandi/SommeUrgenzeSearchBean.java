package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

public class SommeUrgenzeSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6738965901696590657L;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.OGGETTO_COMUNICAZIONE)
	private String oggetto;
	@Validate(EParamValidation.TIPO_APPALTO)
	private String tipoAppalto;
	@Validate(EParamValidation.CIG)
	private String cig;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataPubblicazioneDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
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
