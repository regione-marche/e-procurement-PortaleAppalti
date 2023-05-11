package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

public class PagoPaRiferimentoFilterModel implements Serializable {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7683426089727279376L;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String oggetto;
	@Validate(EParamValidation.GENERIC)
	private String sa;
	@Validate(EParamValidation.CIG)
	private String cig;
	@Validate(EParamValidation.USERNAME)
	private String usernome;
	/**
	 * @return the codice
	 */
	public String getCodice() {
		return codice;
	}
	/**
	 * @param codice the codice to set
	 */
	public void setCodice(String codice) {
		this.codice = codice;
	}
	/**
	 * @param codice the codice to set
	 */
	public PagoPaRiferimentoFilterModel codice(String codice) {
		this.codice = codice;
		return this;
	}
	/**
	 * @return the oggetto
	 */
	public String getOggetto() {
		return oggetto;
	}
	/**
	 * @param oggetto the oggetto to set
	 */
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	/**
	 * @return the sa
	 */
	public String getSa() {
		return sa;
	}
	/**
	 * @param sa the sa to set
	 */
	public void setSa(String sa) {
		this.sa = sa;
	}
	/**
	 * @return the cig
	 */
	public String getCig() {
		return cig;
	}
	/**
	 * @param cig the cig to set
	 */
	public void setCig(String cig) {
		this.cig = cig;
	}
	/**
	 * @return the usernome
	 */
	public String getUsernome() {
		return usernome;
	}
	/**
	 * @param usernome the usernome to set
	 */
	public void setUsernome(String usernome) {
		this.usernome = usernome;
	}
	/**
	 * @param usernome the usernome to set
	 */
	public PagoPaRiferimentoFilterModel usernome(String usernome) {
		this.usernome = usernome;
		return this;
	}
	
	
}
