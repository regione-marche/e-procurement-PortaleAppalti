package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.BandiAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;

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

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
    private String stazioneAppaltante;
	@Validate(EParamValidation.OGGETTO_BANDI)
    private String oggetto;
	@Validate(EParamValidation.CIG)
    private String cig;
	@Validate(EParamValidation.TIPO_APPALTO)
    private String tipoAppalto;
	@Validate(EParamValidation.DATE_DDMMYYYY)
    private String dataPubblicazioneDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
    private String dataPubblicazioneA;
	@Validate(EParamValidation.SI_NO)
    private String proceduraTelematica;
	@Validate(EParamValidation.TIPO_ALTRI_SOGGETTI)
    private String altriSoggetti;
	@Validate(EParamValidation.SI_NO)
    private String sommaUrgenza;
	@Validate(EParamValidation.SI_NO)
	private String isGreen;
	@Validate(EParamValidation.SI_NO)
	private String isRecycle;
	@Validate(EParamValidation.SI_NO)
	private String isPnrr;

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

	public String getIsGreen() {
		return isGreen;
	}

	public void setIsGreen(String isGreen) {
		this.isGreen = isGreen;
	}

	public String getIsRecycle() {
		return isRecycle;
	}

	public void setIsRecycle(String isRecycle) {
		this.isRecycle = isRecycle;
	}

	public String getIsPnrr() {
		return isPnrr;
	}

	public void setIsPnrr(String isPnrr) {
		this.isPnrr = isPnrr;
	}

	//START - GETTER CUSTOM che convertono eventuali field stringa nei corrispettivi boolean, int...
	public Boolean convertedProceduraTelematica() {
		return StringUtils.isEmpty(proceduraTelematica) ? null : "1".equals(proceduraTelematica);
	}
	public Boolean convertedSommaUrgenza() {
		return StringUtils.isEmpty(sommaUrgenza) ? null : "1".equals(sommaUrgenza);
	}
	public boolean checkDataPubblicazioneDa(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_PUBBLICAZIONE_BANDO", BandiAction.DA_DATA, dataPubblicazioneDa);
		if (!isValid)
			dataPubblicazioneDa = null;
		return isValid;
	}
	public boolean checkDataPubblicazioneA(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_PUBBLICAZIONE_BANDO", BandiAction.A_DATA, dataPubblicazioneA);
		if (!isValid)
			dataPubblicazioneA = null;
		return isValid;
	}
	//END - GETTER CUSTOM
}
