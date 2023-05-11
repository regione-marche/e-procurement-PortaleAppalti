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
	private boolean cluster;

	public TipoAutenticazione getTipoAutenticazione() {
		return tipoAutenticazione;
	}
	
	public void setTipoAutenticazione(TipoAutenticazione tipoAutenticazione) {
		this.tipoAutenticazione = tipoAutenticazione;
	}
	
	public UserDetails getUtenteCandidatoPortale() {
		return utenteCandidatoPortale;
	}
	
	public void setUtenteCandidatoPortale(UserDetails utenteCandidatoPortale) {
		this.utenteCandidatoPortale = utenteCandidatoPortale;
	}
	
	public AccountSSO getUtenteCandidatoSingleSignOn() {
		return utenteCandidatoSingleSignOn;
	}
	
	public void setUtenteCandidatoSingleSignOn(
			AccountSSO utenteCandidatoSingleSignOn) {
		this.utenteCandidatoSingleSignOn = utenteCandidatoSingleSignOn;
	}

	public String getSessionIdUtenteConnesso() {
		return sessionIdUtenteConnesso;
	}
	
	public void setSessionIdUtenteConnesso(String sessionIdUtenteConnesso) {
		this.sessionIdUtenteConnesso = sessionIdUtenteConnesso;
	}
	
	public boolean isCluster() {
		return cluster;
	}
	
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
}
