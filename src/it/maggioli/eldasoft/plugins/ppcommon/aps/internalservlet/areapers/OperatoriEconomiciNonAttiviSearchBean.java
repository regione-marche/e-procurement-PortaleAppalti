package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import java.io.Serializable;

/**
 * Bean di ricerca operatori economici non attivati.
 * 
 * @author Eleonora.Favaro
 */
public class OperatoriEconomiciNonAttiviSearchBean implements Serializable{

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 8646001318521961885L;

	/** Username utente o sua porzione, non sensibile a maiuscole o minuscole. */
	private String utente;
	/** Password utente o sua porzione, non sensibile a maiuscole o minuscole. */
	private String email;
	/** Data registrazione utente a partire da. */
	private String dataRegistrazioneDa;
	/** Data registrazione utente fino a. */
	private String dataRegistrazioneA;
	
	/**
	 * @return the utente
	 */
	public String getUtente() {
		return utente;
	}
	/**
	 * @param utente the utente to set
	 */
	public void setUtente(String utente) {
		this.utente = utente;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the dataRegistrazioneDa
	 */
	public String getDataRegistrazioneDa() {
		return dataRegistrazioneDa;
	}
	/**
	 * @param dataRegistrazioneDa the dataRegistrazioneDa to set
	 */
	public void setDataRegistrazioneDa(String dataRegistrazioneDa) {
		this.dataRegistrazioneDa = dataRegistrazioneDa;
	}
	/**
	 * @return the dataRegistrazioneA
	 */
	public String getDataRegistrazioneA() {
		return dataRegistrazioneA;
	}
	/**
	 * @param dataRegistrazioneA the dataRegistrazioneA to set
	 */
	public void setDataRegistrazioneA(String dataRegistrazioneA) {
		this.dataRegistrazioneA = dataRegistrazioneA;
	}
	
}
