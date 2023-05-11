package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

public class DelibereSearchBean extends BaseSearchBean implements Serializable {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 863995630406603198L;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.OGGETTO_BANDI)
	private String oggetto;
	@Validate(EParamValidation.TIPO_APPALTO)
	private String tipoAppalto;
	@Validate(EParamValidation.CIG)
	private String cig;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataPubblicazioneDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataPubblicazioneA;
	@Validate(EParamValidation.SI_NO)
	private String sommaUrgenza;
//	private String dataAtto;
//	private String numeroAtto;
//	private String descrizioneDoc;
//	private String urlDoc;	
//	private String fileDoc;
	
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

	public String getSommaUrgenza() {
		return sommaUrgenza;
	}
	
	public void setSommaUrgenza(String sommaUrgenza) {
		this.sommaUrgenza = sommaUrgenza;
	}
	
//	public String getDataAtto() {
//		return dataAtto;
//	}
//	
//	public void setDataAtto(String dataAtto) {
//		this.dataAtto = dataAtto;
//	}
//	
//	public String getNumeroAtto() {
//		return numeroAtto;
//	}
//	
//	public void setNumeroAtto(String numeroAtto) {
//		this.numeroAtto = numeroAtto;
//	}
//	
//	public String getDescrizioneDoc() {
//		return descrizioneDoc;
//	}
//	
//	public void setDescrizioneDoc(String descrizioneDoc) {
//		this.descrizioneDoc = descrizioneDoc;
//	}
//	
//	public String getUrlDoc() {
//		return urlDoc;
//	}
//	
//	public void setUrlDoc(String urlDoc) {
//		this.urlDoc = urlDoc;
//	}
//	
//	public String getFileDoc() {
//		return fileDoc;
//	}
//	
//	public void setFileDoc(String fileDoc) {
//		this.fileDoc = fileDoc;
//	}
	
}
