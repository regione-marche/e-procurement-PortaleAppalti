package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;
import java.util.Date;

public class ComunicazioniSearchBean extends BaseSearchBean implements Serializable {
	private static final long serialVersionUID = -8117325934295933836L;
	
	private Date dataRicezione;
	@Validate(EParamValidation.OGGETTO_COMUNICAZIONE)
	private String oggetto;
	@Validate(EParamValidation.STATO)
	private String stato;
	
	public Date getDataRicezione() {
		return dataRicezione;
	}

	public void setDataRicezione(Date dataRicezione) {
		this.dataRicezione = dataRicezione;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

}
