package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import java.io.Serializable;

/**
 * Bean di ricerca operatori economici.
 * 
 * @author 
 */
public class OperatoriEconomiciSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4707331672445902250L;
	
	private String username;
	private String ragioneSociale;
	private String email;
	private String attivo;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}
	
	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getAttivo() {
		return attivo;
	}

	public void setAttivo(String attivo) {
		this.attivo = attivo;
	}
	
}
