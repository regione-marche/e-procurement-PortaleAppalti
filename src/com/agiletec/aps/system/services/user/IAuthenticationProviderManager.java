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

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;

/**
 * Interfaccia base dell'oggetto Authentication Provider. L'Authentication
 * Provider è l'oggetto delegato alla restituzione di un'utenza (comprensiva
 * delle sue autorizzazioni) in occasione di una richiesta di autenticazione
 * utente.
 *
 * @version 1.0
 * @author E.Santoboni
 */
public interface IAuthenticationProviderManager {

	/**
	 * Restituisce un'utente (comprensivo delle autorizzazioni) in base ad
	 * username e password.
	 *
	 * @param username La Username dell'utente da restituire.
	 * @param password La password dell'utente da restituire.
	 * @return L'utente cercato o null se non vi è nessun utente corrispondente ai
	 * parametri immessi.
	 * @throws ApsSystemException In caso di errore.
	 */
	public UserDetails getUser(String username, String password) throws ApsSystemException;

	/**
	 * Restituisce un'utente (comprensivo delle autorizzazioni) in base allo
	 * username.
	 * <b>UTILIZZARE ESCLUSIVAMENTE NELLA CLASSE Authenticator!</b>
	 *
	 * @param username La Username dell'utente da restituire.
	 * @return L'utente cercato o null se non vi è nessun utente corrispondente ai
	 * parametri immessi.
	 * @throws ApsSystemException In caso di errore.
	 */
	public UserDetails getUser(String username) throws ApsSystemException;

	/**
	 * Ad ogni accesso di un utente al portale, inserisce un record di log nella
	 * tabella ppcommon_accesses, settando logintime, username e ip.
	 * Contestualmente elimina i tentativi errati di accesso per quell'utenza e
	 * quell'ip.
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
	 * Ad ogni tentativo di accesso fallito, inserisce un record di log nella
	 * tabella ppcommon_wrongaccesses.Nel caso fosse abilitato il modulo privacy,
	 * verifica che non sia stato superato il numero di tentativi di login per la
	 * coppia username-ipaddress. In questo caso disabilita l'utenza e manda una
	 * notifica all'amministratore di sistema e all'utente relativo.
	 *
	 * @param user Lo user relativo alla username inserita.
	 * @param username La username con cui si e' tentato di accedere.
	 * @param ipAddress L'ip address da cui e' stato effettuato il tentativo.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void logWrongAccess(UserDetails user, String username, String ipAddress, String currentLang) throws ApsSystemException;

	/**
	 * Dato lo username che ha effettuato correttamente un accesso, resetta tutti
	 * i tentativi di login errati.
	 *
	 * @param username La username con cui si è stato effettuato l'accesso
	 * @throws ApsSystemException In caso di errore.
	 */
	public void clearAllWrongAccessAttempts(String username) throws ApsSystemException;

	/**
	 * Valuta se sono stati effettuati troppi accessi errati da uno stesso ip ed
	 * inibisce per x minuti l'accesso da quell'ip.
	 *
	 * @param ipAddress ipAddress da cio si è tentato l'accesso.
	 * @param numMaxWrongAttemptsBySameIp numero massimo di tentativi falliti
	 * permessi.
	 * @return true se si è superato il limite, false altrimenti.
	 */
	public boolean tooManyWrongIpAccessAttempts(String ipAddress) throws ApsSystemException;

	/**
	 * traccia i tentativi di accesso all'honeypot (do/adminfun) 
	 */
	public boolean honeypotAttempt(String ipAddress) throws ApsSystemException;
	
	/**
	 * Ritorna il numero di minuti di inibizione all'accesso da parte dell'ip
	 * specificato. Questa proprietà viene settata dalla sezione del modulo
	 * privacy nel backend dell'applicativo.
	 *
	 * @return i minuti di inibizione dall'accesso.
	 */
	public int getInhibitionTimeAccess() throws ApsSystemException;

	
	public void sendEmailUserReactivated(String username, IUserProfile userProfile, String email)  throws ApsSystemException;

}
