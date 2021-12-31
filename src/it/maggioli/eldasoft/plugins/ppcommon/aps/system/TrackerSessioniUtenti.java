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

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Pattern "Singleton" per la gestione dell'elenco delle sessioni collegate
 * all'applicativo.
 *
 * @author Stefano.Sabbadin
 *
 */
public class TrackerSessioniUtenti {

	public static final String UTENTI_CONNESSI = "utentiConnessi";

	/**
	 * Hash contenente gli identificativi delle sessioni degli utenti connessi
	 * all'applicativo
	 */
	private HashMap<String, String[]> datiSessioniUtentiConnessi;     

	/**
	 * Lista delle sessioni degli utenti connessi all'applicativo
	 */
	private HashMap<String, HttpSession> sessioniUtentiConnessi;  

//  /**
//   * Singleton
//   */
//  private static TrackerSessioniUtenti instance;

  /**
   * Costruttore privato del singleton che inizializza l'oggetto con le
   * variabili per le connessioni al valore massimo letto dal file di
   * configurazione, mentre il set di id sessione è inizialmente vuoto
   */
  public TrackerSessioniUtenti() {
    this.datiSessioniUtentiConnessi = new HashMap<String, String[]>();
    this.sessioniUtentiConnessi = new HashMap<String, HttpSession>();
  }

//  /**
//   * Metodo statico per ottenere l'unica referenza al dizionario. Viene creato
//   * l'oggetto solo la prima volta, le altre volte l'oggetto viene semplicemente
//   * restituito
//   *
//   * @return oggetto limitatore delle connessioni
//   */
//  public static TrackerSessioniUtenti getInstance() {
//    if (instance != null) return instance;
//
//    synchronized (TrackerSessioniUtenti.class) {
//      if (instance == null) {
//        instance = new TrackerSessioniUtenti();
//      }
//    }
//    return instance;
//  }
  
	/**
	 * Ritorna una referenza al tracker delle sessioni utenti connessi.
	 * 
	 * @param context
	 *            contesto dell'applicativo
	 * @return tracker delle sessioni
	 */
	public static TrackerSessioniUtenti getInstance(ServletContext context) {
		return (TrackerSessioniUtenti) context
				.getAttribute(TrackerSessioniUtenti.UTENTI_CONNESSI);
	}

	/**
	 * Referenzia la sessione utente con i dati caratterizzanti
	 * l'autenticazione.
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
  public synchronized void putSessioneUtente(HttpSession session, String ip, String login,
	      String loginDateTime) {
     String[] info = new String[3];
     info[0] = ip;
     info[1] = login;
     info[2] = loginDateTime;
     this.datiSessioniUtentiConnessi.put(session.getId(), info);     
     this.sessioniUtentiConnessi.put(session.getId(), session);
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
    }
  }

  /**
   * @return Ritorna datiSessioniUtentiConnessi.
   */
  public HashMap<String, String[]> getDatiSessioniUtentiConnessi() {
	  return datiSessioniUtentiConnessi;
  }
  
  /**
   * 
   */
  public HashMap<String, HttpSession> getSessioniUtentiConnessi() {
	  return sessioniUtentiConnessi;
  }
  
  /**
   * 
   */
  public Integer getNumeroUtentiConnessi() {	  
	  return datiSessioniUtentiConnessi.size();
  }
  
	/**
	 * Verifica se un utente risulta o meno gi&agrave; connesso.
	 * 
	 * @param login
	 *            login utente da verificare
	 * @param sessionId
	 *            id sessione dell'utente che sta effettuando l'accesso
	 * 
	 * @return true se l'utente in input risulta gi&agrave; connesso, false altrimenti
	 */
	public synchronized boolean isUtenteConnesso(String login, String sessionId) {
		boolean utenteConnesso = false;
		for (String elementSessionId : datiSessioniUtentiConnessi.keySet()) {
			String[] datiUtente = datiSessioniUtentiConnessi.get(elementSessionId);
			if (datiUtente[1].equals(login) && !elementSessionId.equals(sessionId)) {
				utenteConnesso = true;
				break;
			}
		}
		return utenteConnesso;
	}

	/**
	 * Verifica se un utente risulta o meno gi&agrave; connesso e ritorna l'eventuale session id dell'utente correntemente connesso.
	 * 
	 * @param login
	 *            login utente da verificare
	 * @param sessionId
	 *            id sessione dell'utente che sta effettuando l'accesso
	 * 
	 * @return identificativo di sessione utente gi&agrave; connesso, null se non esiste alcun accesso simultaneo con lo stesso utente
	 */
	public synchronized String getSessionIdUtenteConnesso(String login, String sessionId) {
		String sessionIdUserLogged = null;
		for (String elementSessionId : datiSessioniUtentiConnessi.keySet()) {
			String[] datiUtente = datiSessioniUtentiConnessi.get(elementSessionId);
			if (datiUtente[1].equals(login) && !elementSessionId.equals(sessionId)) {
				sessionIdUserLogged = elementSessionId;
				break;
			}
		}
		return sessionIdUserLogged;
	}
	
}
