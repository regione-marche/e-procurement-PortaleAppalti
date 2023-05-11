package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Bean per la raccolta dei criteri di filtro nelle form di ricerca bandi.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.0
 */
public class BandiSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2641453754459460335L;

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
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataScadenzaDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataScadenzaA;
	@Validate(EParamValidation.STATO)
    private String stato;
	@Validate(EParamValidation.ESITO_GARA)
    private String esito;
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
	@Validate(EParamValidation.CRITERI_DI_ORDINAMENTO)
	private String orderCriteria;
	@Validate(EParamValidation.RIFERIMENTO_PROCEDURA)
	private String codice;

	//START - Non ricercabili

	private Integer garaPrivatistica;
	private String tokenRichiedente;

	//END - Non ricercabili

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

	public Integer getGaraPrivatistica() {
		return garaPrivatistica;
	}

	public void setGaraPrivatistica(Integer garaPrivatistica) {
		this.garaPrivatistica = garaPrivatistica;
	}

	public String getTokenRichiedente() {
		return tokenRichiedente;
	}

	public void setTokenRichiedente(String tokenRichiedente) {
		this.tokenRichiedente = tokenRichiedente;
	}

	public String getOrderCriteria() {
		return orderCriteria;
	}

	public void setOrderCriteria(String orderCriteria) {
		this.orderCriteria = orderCriteria;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	//START - GETTER CUSTOM che convertono eventuali field stringa nei corrispettivi boolean, int...
	public Boolean convertedProceduraTelematica() {
		return StringUtils.isEmpty(proceduraTelematica) ? null : "1".equals(proceduraTelematica);
	}
	public Boolean convertedSommaUrgenza() {
		return StringUtils.isEmpty(sommaUrgenza) ? null : "1".equals(sommaUrgenza);
	}
	public String convertedEsito() {
		return !"3".equals(stato) ? null : esito;
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
	public boolean checkScadenzaDa(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_SCADENZA_BANDO", BandiAction.DA_DATA, dataScadenzaDa);
		if (!isValid)
			dataScadenzaDa = null;
		return isValid;
	}
	public boolean checkScadenzaA(BaseAction action, String fromPage) {
		boolean isValid = checkConversiosionError(action, fromPage, "LABEL_DATA_SCADENZA_BANDO", BandiAction.A_DATA, dataScadenzaA);
		if (!isValid)
			dataScadenzaA = null;
		return isValid;
	}
	//END - GETTER CUSTOM

}
