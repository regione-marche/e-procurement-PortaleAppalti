package it.maggioli.eldasoft.plugins.pplfs.aps.internalservlet.contratti;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

public class ContrattiLFSSearchBean extends BaseSearchBean  implements Serializable{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 5750041700206069318L;

	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String stazioneAppaltante;
	@Validate(EParamValidation.OGGETTO_BANDI)
    private String oggetto;
	@Validate(EParamValidation.CODICE_CONTRATTO)
    private String codice;
	@Validate(EParamValidation.CIG)
    private String cig;
	@Validate(EParamValidation.RIFERIMENTO_PROCEDURA)
    private String gara;
    
    
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
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getCig() {
		return cig;
	}
	public void setCig(String cig) {
		this.cig = cig;
	}
	public String getGara() {
		return gara;
	}
	public void setGara(String gara) {
		this.gara = gara;
	}

}
