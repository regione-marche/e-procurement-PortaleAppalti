package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

/**
 * Bean di ricerca operatori economici non attivati.
 * 
 * @author Eleonora.Favaro
 */
public class OperatoriEconomiciNonAttiviSearchBean implements Serializable {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 8646001318521961885L;

	/** Username utente o sua porzione, non sensibile a maiuscole o minuscole. */
	@Validate(EParamValidation.USERNAME)
	private String utente;
	/** Password utente o sua porzione, non sensibile a maiuscole o minuscole. */
	@Validate(EParamValidation.EMAIL)
	private String email;
	/** Data registrazione utente a partire da. */
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataRegistrazioneDa;
	/** Data registrazione utente fino a. */
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataRegistrazioneA;
	
	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDataRegistrazioneDa() {
		return dataRegistrazioneDa;
	}

	public void setDataRegistrazioneDa(String dataRegistrazioneDa) {
		this.dataRegistrazioneDa = dataRegistrazioneDa;
	}

	public String getDataRegistrazioneA() {
		return dataRegistrazioneA;
	}

	public void setDataRegistrazioneA(String dataRegistrazioneA) {
		this.dataRegistrazioneA = dataRegistrazioneA;
	}
	
}
