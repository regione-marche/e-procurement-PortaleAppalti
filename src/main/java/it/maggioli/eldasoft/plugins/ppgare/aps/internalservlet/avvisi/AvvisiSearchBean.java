package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

/**
 * Bean per la raccolta dei criteri di filtro nelle form di ricerca avvisi.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.0
 */
public class AvvisiSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8602794282923863971L;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.OGGETTO_BANDI)
	private String oggetto;	
	@Validate(EParamValidation.TIPO_AVVISO)
	private String tipoAvviso;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataPubblicazioneDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataPubblicazioneA;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataScadenzaDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataScadenzaA;
	@Validate(EParamValidation.STATO)
    private String stato;
	@Validate(EParamValidation.SI_NO)
	private String altriSoggetti;
	@Validate(EParamValidation.SI_NO)
	private String sommaUrgenza;
	@Validate(EParamValidation.SI_NO)
	private String isGreen;
	@Validate(EParamValidation.SI_NO)
	private String isRecycle;
	@Validate(EParamValidation.SI_NO)
	private String isPnrr;
	@Validate(EParamValidation.TIPO_AVVISO_GENERALI)
	private String tipoAvvisoGenerale;

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

	public String getTipoAvviso() {
		return tipoAvviso;
	}

	public void setTipoAvviso(String tipoAvviso) {
		this.tipoAvviso = tipoAvviso;
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
	
	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
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

	public String getTipoAvvisoGenerale() {
		return tipoAvvisoGenerale;
	}

	public void setTipoAvvisoGenerale(String tipoAvvisoGenerale) {
		this.tipoAvvisoGenerale = tipoAvvisoGenerale;
	}
	
	// START - Getter custom

	public boolean checkDataPubblicazioneDa(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_PUBBLICAZIONE_AVVISO", AvvisiAction.DA_DATA, dataPubblicazioneDa);
		if (!isValid)
			dataPubblicazioneDa = null;
		return isValid;
	}
	public boolean checkDataPubblicazioneA(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_PUBBLICAZIONE_AVVISO", AvvisiAction.A_DATA, dataPubblicazioneA);
		if (!isValid)
			dataPubblicazioneA = null;
		return isValid;
	}
	public boolean checkScadenzaDa(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_SCADENZA_AVVISO", AvvisiAction.DA_DATA, dataScadenzaDa);
		if (!isValid)
			dataScadenzaDa = null;
		return isValid;
	}
	public boolean checkScadenzaA(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_SCADENZA_AVVISO", AvvisiAction.A_DATA, dataScadenzaA);
		if (!isValid)
			dataScadenzaA = null;
		return isValid;
	}

	//END - Getter custom

	
}
