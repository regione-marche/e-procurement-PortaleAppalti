package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.contratti;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

public class StipuleSearchBean extends BaseSearchBean  implements Serializable {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 6611846943553184973L;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.OGGETTO_BANDI)
    private String oggetto;
	@Validate(EParamValidation.CODICE_STIPULA)
    private String codstipula;
	@Validate(EParamValidation.STATO)
    private String stato;
    
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
	
	public String getCodstipula() {
		return codstipula;
	}
	
	public void setCodstipula(String codstipula) {
		this.codstipula = codstipula;
	}
	
	public String getStato() {
		return stato;
	}
	
	public void setStato(String stato) {
		this.stato = stato;
	}
    
}
