/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system.services.user;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia base per Data Access Object degli oggetti User (User).
 *
 * @version 1.0
 * @author M.Diana - E.Santoboni
 */
public interface IUserDAO {

	/**
	 * Carica e restituisce la lista completa di utenti.
	 *
	 * @return La lista completa di utenti.
	 */
	public List<UserDetails> loadUsers();

	/**
	 * Restituisce la lista di utenti ricavata dalla ricerca sulla username (o
	 * porzione di essa).
	 *
	 * @param text Il testo tramite il quale effettuare la ricerca sulla username.
	 * @return La lista di utenti ricavati.
	 */
	public List<UserDetails> searchUsers(String text);


	/**
	 * Restituisce la lista di utenti ricavata dalla ricerca sul utente delegato (o
	 * porzione di essa).
	 *
	 * @param text Il testo tramite il quale effettuare la ricerca sulla delegateuser.
	 * @return La lista di utenti ricavati.
	 */
	public List<UserDetails> searchUsersFromDelegateUser(String delegateUser);
	
	/**
	 * Carica un'utente corrispondente alla userName e password immessa. null se
	 * non vi è nessun utente corrispondente.
	 *
	 * @param username Nome utente dell'utente cercato
	 * @param password password dell'utente cercato
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi è nessun utente corrispondente.
	 */
	public UserDetails loadUser(String username, String password);

	/**
	 * Carica un'utente corrispondente alla userName immessa. null se non vi è
	 * nessun utente corrispondente.
	 *
	 * @param username Nome utente dell'utente cercato.
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi è nessun utente corrispondente.
	 */
	public UserDetails loadUser(String username);

	/**
	 * Carica un'utente corrispondente alla userName immessa. null se non vi è
	 * nessun utente corrispondente.
	 *
	 * @param username Nome utente dell'utente cercato.
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi è nessun utente corrispondente.
	 */
	public UserDetails loadUser(UserDetails user);

	/**
	 * Cancella l'utente.
	 *
	 * @param user L'oggetto di tipo User relativo all'utente da cancellare.
	 */
	public void deleteUser(UserDetails user);

	/**
	 * Cancella l'utente corrispondente alla userName immessa.
	 *
	 * @param username Il nome identificatore dell'utente.
	 */
	public void deleteUser(String username);

	/**
	 * Aggiunge un nuovo utente.
	 *
	 * @param user Oggetto di tipo User relativo all'utente da aggiungere.
	 */
	public void addUser(UserDetails user);

	/**
	 * Aggiorna un utente già presente con nuovi valori (tranne la username che è
	 * fissa).
	 *
	 * @param user Oggetto di tipo User relativo all'utente da aggiornare.
	 */
	public void updateUser(UserDetails user);

	/**
	 * Carica gli utenti membri di un gruppo.
	 *
	 * @param groupName Il nome del grupo tramite il quale cercare gli utenti.
	 * @return La lista degli utenti (oggetti User) membri del gruppo specificato.
	 * @deprecated USE loadUsernamesForGroup and load single users from current
	 * UserManager.
	 */
	public List<UserDetails> loadUsersForGroup(String groupName);

	/**
	 * Carica la lista di usernames correlati con il gruppo specificato.
	 *
	 * @param groupName Il nome del gruppo tramite il quale cercare i nomi utenti.
	 * @return La lista di usernames correlati con il gruppo specificato.
	 */
	public List<String> loadUsernamesForGroup(String groupName);

	/**
	 * Effettua l'aggiornamento della password di un'utente.
	 *
	 * @param username La username dell'utente a cui aggiornare la password.
	 * @param password La nuova password.
	 */
	public void changePassword(String username, String password, String crc);

	/**
	 * Restituisce la lista di utenti ricavata dalla ricerca username, ragione sociale, email, attivo
	 * ATTENZIONE: searchUsers(a, b, c, d) filtra i campi "ragione sociale" ed "email"
	 * nel campo "xml" percio' in caso di ricerca con filtri su questi due campi
	 * il risultato non e' attendibile
	 *
	 * @param username
	 * @param ragioneSociale
	 * @param email
	 * @param attivo
	 * @return La lista di utenti ricavati.
	 */
	public List<UserDetails> searchUsers(String username, String ragioneSociale, String email, String attivo) throws ApsSystemException;

	public void updateLastAccess(String username);
	
	/**
	 * restituisce l'elenco degli utenti loggati in forma di n-uple {id, username, log in time, log out time, ip, id session}
	 */	
	public List<String[]> getLoggedUsers() throws ApsSystemException;

	/**
	 * Registra il login di un utente
	 *  
	 * @return 0=ok, -1=login gia' effettuato per lo username da una postazione diversa (pc/nodo cluster) 
	 */
	public int logLogin(String username, String delegate, String ipAddress, String sessionId);

	public boolean logLogout(String username, String delegate, String ipAddress, String sessionId);

	public void logWrongAccess(String username, String ipAddress, String sessionId);

	public void logChangePassword(String username, String password);

	public boolean isPasswordNew(String username, String password);

	public boolean tooManyWrongPasswordAccessAttempts(String username, int maxAttempts, int inhibitionIpTimeInMinutes);

	public void clearWrongAccessAttempts(String username, String ipAddress, String sessionId);

	public void clearAllWrongAccessAttemptsByUsername(String username);

	public boolean tooManyWrongIpAccessAttempts(String ipAddress, int maxAttempts, int inhibitionIpTimeInMinutes);

	public void clearAllWrongAccessAttemptsByIp(String ipaddress);

	public void updateDelegateUserImpresa(String username, String delegato);

	public void setUserCrc(String username, String crc);

	public void setAcceptanceVersion(String username, Integer acceptanceVersion);

	/**
	 * gestione dei soggetti impresa associati ad un operatore economico con account SSO
	 */
	public List<DelegateUser> loadProfiliSSO(String username, String delegate);
	
	public DelegateUser loadProfiloSSO(String username, String delegate);
	
	public void deleteProfiloSSO(String username, String delegate);
	
	public void addProfiloSSO(DelegateUser delegateUser);
	
	public void updateProfiloSSO(DelegateUser delegateUser);
	
	public boolean lockProfiloSSOAccess(String username, String delegate, String functionId);
	
	public boolean unlockProfiloSSOAccess(String username, String delegate);
	
	public DelegateUser loadProfiloSSOAccess(String username, String delegate);
	
	public List<DelegateUser> loadProfiliSSOAccesses(String username);
	
}
