package com.agiletec.aps.system.services.user;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Soggetto impresa abilitato all'accesso dei dati relativi di un'impresa con accesso tramite SSO 
 * @author ...
 */
public class DelegateUser implements Serializable {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4278761747932413450L;

	/*
	 * "accesso in sola lettura" 
	 * l'utente puo' consultare tutto cio' che e' visibile in area personale e a partire da qualsiasi elenco o 
	 * gara in cui l'impresa ha inviato flussi, ma non puo' modificare, annullare o inserire alcun dato
	 * 
	 * "accesso compilazione"
	 * il soggetto può operare su flussi da inviare (flussi sugli elenchi, cataloghi e gare) 
	 * ma non puo' procedere all'invio; puo' altresi' lavorare sugli aggiornamenti dati anagrafici e 
	 * sulle comunicazioni (FS12) indipendentemente che siano chiarimenti o invii legati a 
	 * soccorso istruttorio o stipule contratti. Questo perche' l'utente che accede per inviare questi flussi 
	 * e' un utente amministrativo che integra la documentazione e la invia in autonomia
	 * 
	 * "accesso controllo completo"
	 * l'utente puo' manipolare i dati, inviare i flussi, ed eventualmente annullarli 
	 * (annulla presentazione offerta, rinuncia offerta)
	 */
	public enum Accesso {
		NESSUNO
		, READONLY				// SOLA_LETTURA
		, EDIT					// COMPIPLAZIONE
		, EDIT_SEND				// CONTROLLO_COMPLETO
	}
	
	public static Accesso valueOfDefault(String value, Accesso defValue) {
		Accesso v = Accesso.READONLY;
		try {
			v = StringUtils.isNotEmpty(value) ? Accesso.valueOf(value) : Accesso.READONLY;
		} catch (Exception e) {
			// se non si riesce a decodificare il valore si usa come default READONLY
			v = defValue;
		}
		return v;
	}

	private boolean owner;
	private String username;		// impresa
	private String delegate;		// delegate user associato all'impresa
	private Accesso rolename;
	private String description;
	private Date lastAccess;
	private String email;
	private String flusso;			// flusso per la quale si ha ottenuto un lock (offerta, inscrizione/rinnovo elenco, ...)
	private Date loginTime;			// log in time del lock
	private Date logoutTime;		// log out time del lock
	private String UO;				// Unita' Organizzativa
			
	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDelegate() {
		return delegate;
	}

	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}

	public Accesso getRolename() {
		return rolename;
	}

	public void setRolename(Accesso rolename) {
		this.rolename = rolename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastAccess() {
		return lastAccess;
	}
	
	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFlusso() {
		return flusso;
	}
	
	public void setFlusso(String flusso) {
		this.flusso = flusso;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}
	
	public String getUO() {
		return UO;
	}

	public void setUO(String uO) {
		UO = uO;
	}

	public void copyFrom(DelegateUser from) {
		if(from == null) return; 
		owner = from.isOwner();
		username = from.getUsername();
		delegate = from.getDelegate();
		rolename = from.getRolename();
		description = from.getDescription();
		lastAccess = from.getLastAccess();
		email = from.getEmail();
		flusso = from.getFlusso();
		loginTime = from.getLoginTime();
		logoutTime = from.getLogoutTime();
		UO = from.getUO();
	}
		
	public boolean isLockOpen() {
		boolean locked = (StringUtils.isNotEmpty(flusso) && loginTime != null && logoutTime == null);
		return locked;
	}
	
	
	/**
	 * DelegateUser builder 
	 */
	public static final class DelegateUserBuilder {
		private final DelegateUser u;

        private DelegateUserBuilder() { u = new DelegateUser(); }
        public static DelegateUserBuilder init() { return new DelegateUserBuilder(); }
        
		public DelegateUserBuilder setOwner(boolean owner) { 
			u.owner = owner;
			return this;
		}
		public DelegateUserBuilder setUsername(String username) {
			u.username = username;
			return this;
		}
		public DelegateUserBuilder setDelegate(String delegate) {
			u.delegate = delegate;
			return this;
		}
		public DelegateUserBuilder setRolename(Accesso rolename) {
			u.rolename = rolename;
			return this;
		}
		public DelegateUserBuilder setDescription(String description) {
			u.description = description;
			return this;
		}
		public DelegateUserBuilder setLastAccess(Date lastAccess) {
			u.lastAccess = lastAccess;
			return this;
		}
		public DelegateUserBuilder setEmail(String email) {
			u.email = email;
			return this;
		}
		public DelegateUserBuilder setFlusso(String flusso) {
			u.flusso = flusso;
			return this;
		}
		public DelegateUserBuilder setLoginTime(Date loginTime) {
			u.loginTime = loginTime;
			return this;
		}
		public DelegateUserBuilder setLogoutTime(Date logoutTime) {
			u.logoutTime = logoutTime;
			return this;
		}
		public DelegateUserBuilder setUO(String uo) {
			u.UO = uo;
			return this;
		}
		public DelegateUser build() { return u; }
	}

}
