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
 * Interfaccia base per i servizi di gestione utenti.
 *
 * @author M.Diana - E.Santoboni
 */
public interface IUserManager {

	/**
	 * Restituisce la lista completa degli utenti (in oggetti User).
	 *
	 * @return La lista completa degli utenti (in oggetti User).
	 * @throws ApsSystemException In caso di errore.
	 */
	public List<UserDetails> getUsers() throws ApsSystemException;

	/**
	 * Restituisce la lista di utenti ricavata dalla ricerca sulla username (o
	 * porzione di essa).
	 *
	 * @param text Il testo tramite il quale effettuare la ricerca sulla username.
	 * @return La lista di utenti ricavati.
	 * @throws ApsSystemException In caso di errore.
	 */
	public List<UserDetails> searchUsers(String text) throws ApsSystemException;

	/**
	 * Restituisce la lista di utenti ricavata dalla ricerca sulla username (o
	 * porzione di essa). 
	 */
	public List<UserDetails> searchUsers(
			String username, 
			String ragioneSociale, 
			String email, 
			String attivo) throws ApsSystemException;
	/**
	 * Elimina un utente.
	 *
	 * @param user L'utente da eliminare dal db.
	 * @throws ApsSystemException in caso di errore.
	 */
	public void removeUser(UserDetails user) throws ApsSystemException;

	/**
	 * Elimina un utente.
	 *
	 * @param username La username dell'utente da eliminare.
	 * @throws ApsSystemException in caso di errore.
	 */
	public void removeUser(String username) throws ApsSystemException;

	/**
	 * Aggiorna un utente.
	 *
	 * @param user L'utente da aggiornare.
	 * @throws ApsSystemException in caso di errore.
	 */
	public void updateUser(UserDetails user) throws ApsSystemException;

	/**
	 * Aggiorna la data (a quella odierna) di ultimo accesso dell'utente
	 * specificato.
	 *
	 * @param user L'utente a cui aggiornare la data di ultimo accesso.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void updateLastAccess(UserDetails user) throws ApsSystemException;

	/**
	 * Effettua l'operazione di cambio password.
	 *
	 * @param username Lo username al quale cambiare la password.
	 * @param password La nuova password.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void changePassword(String username, String password) throws Exception;

	/**
	 * Aggiunge un utente.
	 *
	 * @param user L'utente da aggiungere.
	 * @throws ApsSystemException in caso di errore.
	 */
	public void addUser(UserDetails user) throws ApsSystemException;

	/**
	 * Restituisce un utente. Se la userName non corrisponde ad un utente
	 * restituisce null.
	 *
	 * @param username Lo username dell'utente da restituire.
	 * @return L'utente cercato, null se non vi è nessun utente corrispondente
	 * alla username immessa.
	 * @throws ApsSystemException in caso di errore.
	 */
	public UserDetails getUser(String username) throws ApsSystemException;

	/**
	 * Restituisce un utente. Se userName e password non corrispondono ad un
	 * utente, restituisce null.
	 *
	 * @param username Lo username dell'utente da restituire.
	 * @param password La password dell'utente da restituire.
	 * @return L'utente cercato, null se non vi è nessun utente corrispondente
	 * alla username e password immessa.
	 * @throws ApsSystemException in caso di errore.
	 */
	public UserDetails getUser(String username, String password) throws ApsSystemException;

	/**
	 * Restituisce l'utente di default di sistema. L'utente di default rappresenta
	 * un utente "ospite" senza nessuna autorizzazione di accesso ad elementi non
	 * "liberi" e senza nessuna autorizzazione ad eseguire qualunque azione sugli
	 * elementi del sistema.
	 *
	 * @return L'utente di default di sistema.
	 */
	public UserDetails getGuestUser();

	/**
	 * Logga il cambiamento della password di un utente.
	 *
	 * @param username Lo username che chiede il cambio password.
	 * @param password La nuova password.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void logChangePassword(String username, String password) throws ApsSystemException;

	/**
	 * Verifica a db, nella tabella ppcommon_passwords, non esista gia'
	 * un'occorrenza della medesima password per il medesimo utente
	 *
	 * @param username Lo username che chiede il cambio password.
	 * @param password La nuova password.
	 * @throws ApsSystemException In caso di errore.
	 */
	public boolean isPasswordNew(String username, String password) throws ApsSystemException;

	/**
	 * @ Inserisce un record di log nella tabella ppcommon_accesses, settando
	 * logintime, username e ip.
	 *
	 * @param username Lo username che ha effettuato l'accesso.
	 * @param ipAddress L'ip address da cui e' stato effettuato l'accesso.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void logLogin(String username, String ipAddress) throws ApsSystemException;

	/**
	 * Ad ogni disconnessione di un utente dal portale o scadenza della propria
	 * sessione corrente, aggiorna il record di log nella tabella
	 * ppcommon_accesses, settando il logouttime.
	 *
	 * @param username Lo username che ha effettuato la disconnessione o per cui
	 * e' scaduta la sessione.
	 * @param ipAddress L'ip address da cui e' stato effettuato l'accesso in
	 * precedenza.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void logLogout(String username, String ipAddress) throws ApsSystemException;

	/**
	 * Inserisce un record di log nella tabella ppcommon_wrongaccesses
	 *
	 * @param username La username con cui si e' tentato di accedere.
	 * @param ipAddress L'ip address da cui e' stato effettuato il tentativo.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void logWrongAccess(String username, String ipAddress) throws ApsSystemException;

	/**
	 * Pulisce i tutti i tentativi di accesso sbagliati per quest'utenza.
	 *
	 * @param username Lo username da riabilitare.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void clearAllWrongAccessAttemptsByUsername(String username) throws ApsSystemException;

	/**
	 * Pulisce i tutti i tentativi di accesso sbagliati data una username e un'ip.
	 * Usato nella procedura di autenticazione utente.
	 *
	 * @param username Lo username con cui si effettua il login.
	 * @param ipAddress L'ip address da cui si effettua il login.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void clearWrongAccessAttempts(String username, String ipAddress) throws ApsSystemException;

	/**
	 * Ritorna il valore di una proprietà dato il suo codice.
	 *
	 * @param paramName nome del parametro.
	 * @param defaultValue valore di default da assegnare.
	 * @return il valore numero della proprietà.
	 */
	public int extractNumberParamValue(String paramName, int defaultValue);

	/**
	 * Valuta se sono stati effettuati troppi accessi errati interrogando 
	 * la tabella ppcommon_wrongaccesses.
	 *
	 * @param username username con cui si è tentato l'accesso.
	 * @param numMaxLoginAttemptsWrongPassword numero massimo di tentativi
	 * falliti permessi
	 * @return true se si è superato il limite, false altrimenti.
	 */
	public boolean tooManyWrongPasswordAccessAttempts(String username, int numMaxLoginAttemptsWrongPassword) throws ApsSystemException;
	
	/**
	 * Valuta se sono stati effettuati troppi accessi errati da uno stesso ip 
	 * guardando la tabella ppcommon_wrongaccesses.
	 *
	 * @param ipAddress ipAddress da cio si è tentato l'accesso.
	 * @param numMaxWrongAttemptsBySameIp numero massimo di tentativi falliti 
	 * permessi.
	 * @return true se si è superato il limite, false altrimenti.
	 */
	public boolean tooManyWrongIpAccessAttempts(String ipAddress, int numMaxWrongAttemptsBySameIp) throws ApsSystemException;
	
	/**
	 * Pulisce i tutti i tentativi di accesso sbagliati di questo indirizzo ip.
	 *
	 * @param ipaddress L'ip da cui si effettua l'accesso.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void clearAllWrongAccessAttemptsByIp(String ipaddress) throws ApsSystemException;
	
	/**
	 * Inserisce il crc nella tabella authusers per lo username passato.
	 *
	 * @param username dell'utente da modificare.
 	 * @param crc calcolato dell'utente.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void setUserCrc(String username, String crc) throws ApsSystemException;

	public String calculateUserCrc(String username, String password) throws ApsSystemException;

	boolean isEnabledPrivacyModule();
	
	/**
	 * aggiorna il DELEGATEUSER nella tabella authusers per lo username passato.
	 *
	 * @param username dell'utente da modificare.
 	 * @param delegateUser da associare all'utente.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void setDelegateUser(String username, String delegateUser) throws ApsSystemException;

	public String validatePassword(UserDetails user, String password) throws ApsSystemException;
	
}
