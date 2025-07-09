/*
 * Created on 20-lug-2007
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps.system;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.UserDetails;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pattern "Singleton" per la gestione dell'elenco delle sessioni collegate
 * all'applicativo.
 *
 * @author Stefano.Sabbadin
 *
 */
public class TrackerSessioniUtenti {

	/** 
	 * ******************************************************************
	 * Classe per gestire le info di sessione in caso di cluster di nodi 
	 * per la pagina di delle sessioni attive (ViewSessioniAttive.action)
	 * ******************************************************************   
	 */
	private class HttpSessionWrapper implements HttpSession, Serializable {
		/**
		 * UID
		 */
		private static final long serialVersionUID = -6941885785174366111L;

		// NB: HttpSession non e' serializzabile 
		private transient HttpSession session;
		
		private long creationTime;
		private String id;
		private long lastAccessedTime;
		private int maxInactiveInterval;
		
		public HttpSessionWrapper(HttpSession session) {
			this.session = session;
			updateInfo();
		}
		
		private void updateInfo() {
			if(this.session != null) {
				creationTime = this.session.getCreationTime();
				id = this.session.getId();
				lastAccessedTime = this.session.getLastAccessedTime();
				maxInactiveInterval = this.session.getMaxInactiveInterval();
			}
		}
		
		@Override
		public long getCreationTime() {
			updateInfo();
			return this.creationTime;
		}

		@Override
		public String getId() {
			updateInfo();
			return this.id;
		}

		@Override
		public long getLastAccessedTime() {
			updateInfo();
			return this.lastAccessedTime;
		}

		@Override
		public ServletContext getServletContext() {
			if(this.session != null) {
				return this.session.getServletContext();
			}
			return null;
		}

		@Override
		public void setMaxInactiveInterval(int interval) {
			this.maxInactiveInterval = interval;
		}

		@Override
		public int getMaxInactiveInterval() {
			updateInfo();
			return this.maxInactiveInterval;
		}

		@Override
		public HttpSessionContext getSessionContext() {
			if(this.session != null) {
				return this.session.getSessionContext();
			}
			return null;
		}

		@Override
		public Object getAttribute(String name) {
			if(this.session != null) {
				return this.session.getAttribute(name);
			}
			return null;
		}

		@Override
		public Object getValue(String name) {
			if(this.session != null) {
				return this.session.getValue(name);
			}
			return null;
		}

		@Override
		public Enumeration getAttributeNames() {
			if(this.session != null) {
				return this.session.getAttributeNames();
			}
			return null;
		}

		@Override
		public String[] getValueNames() {
			if(this.session != null) {
				return this.session.getValueNames();
			}
			return null;
		}

		@Override
		public void setAttribute(String name, Object value) { 
			if(this.session != null) {
				this.session.setAttribute(name, value);
			}
		}

		@Override
		public void putValue(String name, Object value) { 
			if(this.session != null) {
				this.session.putValue(name, value);
			}
		}

		@Override
		public void removeAttribute(String name) {
			if(this.session != null) {
				this.session.removeAttribute(name);
			}
		}
			
		@Override
		public void removeValue(String name) {
			if(this.session != null) {
				this.session.removeValue(name);
			}
		}

		@Override
		public void invalidate() {
			if(this.session != null) {
				this.session.invalidate(); 
			}
		}

		@Override
		public boolean isNew() {
			if(this.session != null) {
				return this.session.isNew(); 
			} 
			return false;
		}
		
	}
		
	/** 
	 * ******************************************************************
	 * Info degli utenti loggati 
	 * ******************************************************************   
	 */
	public class LoggedUserInfo {
		
		private String ip;
		private String login;
		private String loginTime;
		private String logoutTime;
		private String lastAccess;
		private String username;
		private String sessionId;
		
		public LoggedUserInfo(String ip, String login, String loginDateTime, String logoutDateTime, String username, String sessionId) {
			this.ip = ip;
			this.login = login;
			this.loginTime = loginDateTime;
			this.logoutTime = logoutDateTime;
			this.username = username;
			this.sessionId = sessionId;
		}

		public String getIp() { return ip; }
		public void setIp(String ip) { this.ip = ip; }

		public String getLogin() { return login; }
		public void setLogin(String login) { this.login = login; }

		public String getLoginTime() { return loginTime; }
		public void setLoginTime(String loginTime) { this.loginTime = loginTime; }

		public String getLogoutTime() { return logoutTime; }
		public void setLogoutTime(String logoutTime) { this.logoutTime = logoutTime; }

		public String getUsername() { return username; }
		public void setUsername(String username) { this.username = username; }

		public String getSessionId() { return sessionId; }
		public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	}

	
	public static final String UTENTI_CONNESSI = "utentiConnessi";
	
	private static final DateFormat DDMMYYYY_HHMMSS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final DateFormat YYYYMMDD_HHMMSS_SSSSSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
	
	/**
	 * Hash contenente gli identificativi delle sessioni degli utenti connessi all'applicativo
	 */
	private HashMap<String, LoggedUserInfo> datiSessioniUtentiConnessi;

	/**
	 * Lista delle sessioni degli utenti connessi all'applicativo
	 */
	private HashMap<String, HttpSession> sessioniUtentiConnessi;  

	
	/**
	 * Costruttore privato del singleton che inizializza l'oggetto con le
	 * variabili per le connessioni al valore massimo letto dal file di
	 * configurazione, mentre il set di id sessione è inizialmente vuoto
	 */
	public TrackerSessioniUtenti() {
		this.datiSessioniUtentiConnessi = new HashMap<String, LoggedUserInfo>();
		this.sessioniUtentiConnessi = new HashMap<String, HttpSession>();
	}

	/**
	 * Ritorna una referenza al tracker delle sessioni utenti connessi.
	 * 
	 * @param context
	 *            contesto dell'applicativo
	 * @return tracker delle sessioni
	 */
	public static TrackerSessioniUtenti getInstance(ServletContext context) {
		TrackerSessioniUtenti tracker = (TrackerSessioniUtenti) context.getAttribute(TrackerSessioniUtenti.UTENTI_CONNESSI);
		
		UserDetails currentUser = ((UserDetails) context.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER));
		boolean isCluster = (currentUser != null ? currentUser.getSessionId().indexOf(".") > 0 : false);
		
		// in caso di CLUSTER recupera la lista delle sessioni attive da DB
//		if(isCluster) {
			try {
				// recupera dal db l'elenco dei login aperti
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
				IAuthenticationProviderManager authenticationProvider = (IAuthenticationProviderManager) ctx
						.getBean("AuthenticationProviderManager");

//				List<UserDetails> users = authenticationProvider.getUsers();
				List<String[]> loggedUsers = authenticationProvider.getLoggedUsers();
				
				// aggiorna la lista delle sessioni attive...
				if(loggedUsers != null) {
					for(String[] item : loggedUsers) {
						// {id, username, log in time, log out time, ip, session id}
						String username = item[1];
						String loginTime = (StringUtils.isNotEmpty(item[2]) ? formatDate(item[2]) : "");
						String logoutTime = (StringUtils.isNotEmpty(item[3]) ? formatDate(item[3]) : "");
						String ip = item[4];
						String sessionId = item[5];
						String login = null;
						
//						// verifica se esiste un delegate user per l'utente...
//						if(users != null) {
//							UserDetails userDetail = null;
//							for(UserDetails u : users) {
//								if(u.getUsername().equalsIgnoreCase(username)) {
//									userDetail = u;
//									break;
//								}
//							}
//							if(userDetail != null) {
//								username = userDetail.getDelegateUser();
//								login = userDetail.getUsername();
//							}
//						}
						
						// verifica se l'utente esiste gia' nel tracker delle sessioni utente... 
						boolean trovato = false;
						for (Map.Entry<String, LoggedUserInfo> dati : tracker.getDatiSessioniUtentiConnessi().entrySet()) {
							LoggedUserInfo info = dati.getValue();
							if(info.getLogin().equals(username)) {
								trovato = true;
								break;
							}
						}
						if( !trovato ) {
							tracker.putSessioneUtente(null, sessionId, ip, username, loginTime, logoutTime, login);
						}
					}
				}

				debugTrackerList(tracker);
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().error("TrackerSessioniUtenti", "getInstance", t);
			}
//		}
		
		return tracker;
	}
	
	private static String formatDate(String date) throws ParseException {
		return DDMMYYYY_HHMMSS.format(YYYYMMDD_HHMMSS_SSSSSS.parse(date));
	}
	
	/**
	 * ... 
	 */
	private static void debugTrackerList(TrackerSessioniUtenti tracker) {
		if(ApsSystemUtils.getLogger().isDebugEnabled()) {
//System.out.println("TrackerSessioniUtenti BEGIN");
			ApsSystemUtils.getLogger().debug("TrackerSessioniUtenti BEGIN");
			for (String sessionId : tracker.getDatiSessioniUtentiConnessi().keySet()) {
				LoggedUserInfo info = tracker.getDatiSessioniUtentiConnessi().get(sessionId);
				ApsSystemUtils.getLogger().debug("TrackerSessioniUtenti [" + sessionId + "]=(" + info.getLogin() + ", " + info.getUsername() + ", " + info.getLoginTime() + ", " + info.getLogoutTime() + ")");
//System.out.println("TrackerSessioniUtenti [" + sessionId + "]=(" + info.getLogin() + ", " + info.getUsername() + ", " + info.getLoginTime() + ", " + info.getLogoutTime() + ", " + info.getIp() + ")");
			}
			ApsSystemUtils.getLogger().debug("TrackerSessioniUtenti END");
//System.out.println("TrackerSessioniUtenti END");
		}
	}

	/**
	 * Referenzia la sessione utente con i dati caratterizzanti l'autenticazione.
	 */
    public synchronized void putSessioneUtente(
    		HttpSession session, 
    		String sessionId, 
    		String ip, 
    		String login, 
    		String loginDateTime, 
    		String logoutDateTime, 
    		String username) 
    {
       if(session != null && StringUtils.isEmpty(sessionId)) {
    	   sessionId = session.getId();
       }
       if(StringUtils.isNotEmpty(sessionId)) {
    	   HttpSessionWrapper cs = (session != null && session instanceof HttpSessionWrapper
					? (HttpSessionWrapper)session 
					: new HttpSessionWrapper(session));
    	   
    	   LoggedUserInfo info = new LoggedUserInfo(
    			   ip,
    			   login, 
    			   loginDateTime, 
    			   logoutDateTime, 
    			   username, 
    			   sessionId);
    	   
		   this.datiSessioniUtentiConnessi.put(sessionId, info);
		   this.sessioniUtentiConnessi.put(sessionId, cs);
		   
		   if(ApsSystemUtils.getLogger().isDebugEnabled()) {
		      ApsSystemUtils.getLogger().debug("putSessioneUtente(" + login + ", " + username + ", " + sessionId + ", " + ip + ")");
		      debugTrackerList(this);
		   }
       }
    }

	/**
	 * Referenzia la sessione utente con i dati caratterizzanti l'autenticazione.
	 * 
	 * @param session
	 *            sessione utente autenticatosi
	 * @param ip
	 *            indirizzo ip del client
	 * @param login
	 *            login utilizzata per l'accesso
	 * @param loginDateTime
	 *            data ora in formato stringa GG/MM/AAAA HH:MI:SS di
	 *            autenticazione al sistema
	 */
    public synchronized void putSessioneUtente(HttpSession session, String ip, String login, String loginDateTime) {
    	putSessioneUtente(session, session.getId(), ip, login, loginDateTime, null, null);
    }

    /**
     * aggiorna i dati sessione di un utente
     */
    public synchronized void updateSessioneUtente(String sessionId) {
    	if(StringUtils.isNotEmpty(sessionId)) {
    		try {
    			HttpSessionWrapper cs = (HttpSessionWrapper) this.sessioniUtentiConnessi.get(sessionId);
        		// ATTENNZIONE: 
        		// il logoutTime viene aggiornato con la data attuale da UserCluesterIterceptor 
        		// per mantenere aggiornata la data ora di ultimo accesso 
    			LoggedUserInfo info = this.datiSessioniUtentiConnessi.get(sessionId);
    			info.setLogoutTime(DateFormatUtils.format(cs.getLastAccessedTime(), "dd/MM/yyyy HH:mm:ss"));
    		} catch(Exception ex) {
    			ApsSystemUtils.getLogger().warn("TrackerSessioniUtenti", "updateSessioneUtente", ex);
    		}
    	}
    }
  
    /**
     * aggiorna i dati sessione utente solo per l'utente specificato, senza ricaricare da DB i dati di tutti gli utenti loggati...
     */
    public static void updateSessioneUtente(HttpSession session, UserDetails currentUser) {	
    	if(session != null && currentUser != null) {
    		TrackerSessioniUtenti tracker = (TrackerSessioniUtenti) session.getServletContext().getAttribute(TrackerSessioniUtenti.UTENTI_CONNESSI);
    		if(tracker != null) {
    			tracker.updateSessioneUtente(currentUser.getSessionId());
    		}
    	}
    }

    /**
     * Esegue la deallocazione della connessione per la sessione in input. Si
     * esegue un test preventivo che la sessione sia tra quelle che han ottenuto
     * con successo l'allocazione in precedenza.
     *
     * @param sessionId
     *        sessione richiedente la deallocazione della connessione
     */
    public synchronized void removeSessioneUtente(String sessionId) {
    	if (this.datiSessioniUtentiConnessi.containsKey(sessionId)) {
    		this.datiSessioniUtentiConnessi.remove(sessionId);
    		this.sessioniUtentiConnessi.remove(sessionId);
    		debugTrackerList(this);
    	}
    }

    
    /**
     * @return Ritorna datiSessioniUtentiConnessi.
     */
    public HashMap<String, LoggedUserInfo> getDatiSessioniUtentiConnessi() {
    	return datiSessioniUtentiConnessi;
    }
  
    /**
     * ...
     */
    public HashMap<String, HttpSession> getSessioniUtentiConnessi() {
    	return sessioniUtentiConnessi;
    }
  
    /**
     * ...
     */
    public Integer getNumeroUtentiConnessi() {
    	return datiSessioniUtentiConnessi.size();
    }
  
    /**
     * recupera la prima sessione di un utente diversa dalla sessione corrente
     * @param login 
     * 			username dell'utente da verificare
     * @param currentSessionId 
     * 			id sessione dell'utente da verificare
     */
    private synchronized String findUserIdSession(String login, String currentSessionId) {
    	String sessionId = null;
    	for (String id : datiSessioniUtentiConnessi.keySet()) {
    		// ignora la sessione corrente...
    		// ATTENNZIONE: 
    		// il logoutTime viene aggiornato con la data attuale da UserCluesterIterceptor 
    		// per mantenere aggiornata la data ora di ultimo accesso 
    		if( !currentSessionId.equals(id) ) {
    			LoggedUserInfo info = datiSessioniUtentiConnessi.get(id);
	    		if(login.equals(info.getLogin())) {
	    			sessionId = id;
	    			break;
	    		}
    		}
    	}
    	return sessionId;
    }

    /**
     * Verifica se un utente risulta o meno gi&agrave; connesso.
     * @param login
     *            login utente da verificare
     * @param sessionId
     *            id sessione dell'utente che sta effettuando l'accesso
     * 
     * @return true se l'utente in input risulta gi&agrave; connesso, false altrimenti
     */
    public synchronized boolean isUtenteConnesso(String login, String loginSessionId) {
    	LoggedUserInfo info = null;
    	String sessionId = findUserIdSession(login, loginSessionId);
    	if(StringUtils.isNotEmpty(sessionId)) {
    		info = datiSessioniUtentiConnessi.get(sessionId);
    	}
    	return (info != null);
    }

    /**
     * Verifica se un utente risulta gi&agrave; connesso e ritorna l'eventuale session id dell'utente gi&agrave; connesso.
     * 
     * @param login
     *            login utente da verificare
     * @param sessionId
     *            id sessione dell'utente corrente che sta tentanto l'accesso
     * 
     * @return identificativo di sessione utente gi&agrave; connesso, null se non esiste alcun accesso simultaneo con lo stesso utente
     */
    public synchronized String getSessionIdUtenteConnesso(String login, String loginSessionId) {
    	return findUserIdSession(login, loginSessionId);
    }

}
