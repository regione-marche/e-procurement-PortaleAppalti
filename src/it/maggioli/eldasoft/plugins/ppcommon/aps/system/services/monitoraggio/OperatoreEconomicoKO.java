package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio;

import java.util.Date;


/**
 * Helper contenente i dati necessari nella visualizzazione del singolo utente non attivato.
 * 
 * @author Eleonora.Favaro
 */
public class OperatoreEconomicoKO {

	/** Username utente. */
	private String utente;
	/** Email di riferimento. */
	private String email;
	/** Data di registrazione al portale. */
	private Date dataRegistrazione;
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
	 * @return the dataRegistrazione
	 */
	public Date getDataRegistrazione() {
		return dataRegistrazione;
	}
	/**
	 * @param dataRegistrazione the dataRegistrazione to set
	 */
	public void setDataRegistrazione(Date dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}
	
}
