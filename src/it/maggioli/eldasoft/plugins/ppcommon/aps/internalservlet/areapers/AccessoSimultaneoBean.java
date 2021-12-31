package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;

import java.io.Serializable;

import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Contenitore dei dati per la gestione dell'accesso simultaneo tra due utenti aventi le stesse credenziali.
 * 
 * @author stefano.sabbadin
 */
public class AccessoSimultaneoBean implements Serializable {

	public enum TipoAutenticazione {
		DB, SINGLE_SIGN_ON
	}

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4876958996610702946L;
	
	public static final String SESSION_CUNCURRENT_OBJECT_ID = "accessoSimultaneo";
	
	private TipoAutenticazione tipoAutenticazione;
	private UserDetails utenteCandidatoPortale;
	private AccountSSO utenteCandidatoSingleSignOn;
	private String sessionIdUtenteConnesso;
	/**
	 * @return the tipoAutenticazione
	 */
	public TipoAutenticazione getTipoAutenticazione() {
		return tipoAutenticazione;
	}
	/**
	 * @param tipoAutenticazione the tipoAutenticazione to set
	 */
	public void setTipoAutenticazione(TipoAutenticazione tipoAutenticazione) {
		this.tipoAutenticazione = tipoAutenticazione;
	}
	/**
	 * @return the utenteCandidatoPortale
	 */
	public UserDetails getUtenteCandidatoPortale() {
		return utenteCandidatoPortale;
	}
	/**
	 * @param utenteCandidatoPortale the utenteCandidatoPortale to set
	 */
	public void setUtenteCandidatoPortale(UserDetails utenteCandidatoPortale) {
		this.utenteCandidatoPortale = utenteCandidatoPortale;
	}
	/**
	 * @return the utenteCandidatoSingleSignOn
	 */
	public AccountSSO getUtenteCandidatoSingleSignOn() {
		return utenteCandidatoSingleSignOn;
	}
	/**
	 * @param utenteCandidatoSingleSignOn the utenteCandidatoSingleSignOn to set
	 */
	public void setUtenteCandidatoSingleSignOn(
			AccountSSO utenteCandidatoSingleSignOn) {
		this.utenteCandidatoSingleSignOn = utenteCandidatoSingleSignOn;
	}
	/**
	 * @return the sessionIdUtenteConnesso
	 */
	public String getSessionIdUtenteConnesso() {
		return sessionIdUtenteConnesso;
	}
	/**
	 * @param sessionIdUtenteConnesso the sessionIdUtenteConnesso to set
	 */
	public void setSessionIdUtenteConnesso(String sessionIdUtenteConnesso) {
		this.sessionIdUtenteConnesso = sessionIdUtenteConnesso;
	}
}
