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

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;
import com.agiletec.aps.system.services.user.DelegateUser.DelegateUserBuilder;
import com.agiletec.aps.util.IApsEncrypter;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object per gli oggetti Utente.
 *
 * @author M.Diana - E.Santoboni
 */
public class UserDAO extends AbstractDAO implements IUserDAO {

	private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

	private ICustomConfigManager customConfigManager;

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	/**
	 * Carica e restituisce la lista completa di utenti presenti nel db.
	 *
	 * @return La lista completa di utenti (oggetti User)
	 */
	public List<UserDetails> loadUsers() {
		Connection conn = null;
		List<UserDetails> users = null;
		Statement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(LOAD_USERS);
			users = this.loadUsers(res);
		} catch (Throwable t) {
			processDaoException(t, "Error loading the users list", "loadUsers");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return users;
	}

	public List<UserDetails> searchUsers(String text) {
		if (null == text) {
			text = "";
		}
		Connection conn = null;
		List<UserDetails> users = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(SEARCH_USERS_BY_TEXT);
			stat.setString(1, "%" + text.toUpperCase() + "%");
			res = stat.executeQuery();
			users = this.loadUsers(res);
		} catch (Throwable t) {
			processDaoException(t, "Error while searching users", "searchUsers");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return users;
	}
	
	public List<UserDetails> searchUsersFromDelegateUser(String delegateUser){
		List<UserDetails> users = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(SEARCH_USERS_BY_DELEGATE_USER);
			stat.setString(1, delegateUser);
			res = stat.executeQuery();
			users = this.loadUsers(res);
		} catch (Throwable t) {
			processDaoException(t, "Error while searching users for delegate user", "searchUsersFromDelegateUser");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		
		return users;
	}

	/**
	 * ATTENZIONE: searchUsers(...) filtra i campi "ragione sociale" ed "email"
	 * nel campo "xml" percio' in caso di ricerca con filtri su questi due campi
	 * il risultato non e' attendibile
	 */
	@Override
	public List<UserDetails> searchUsers(String username, String ragioneSociale, String email, String attivo) throws ApsSystemException {
		List<UserDetails> users = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		try {
			String filters = "";
			if(StringUtils.isNotEmpty(username)) {
				filters = filters + (filters.length() > 0 ? " AND " : "") +
						"upper(a.username) LIKE '%" + username.toUpperCase() + "%'"; 
			}
			if(StringUtils.isNotEmpty(ragioneSociale)) {
				filters = filters + (filters.length() > 0 ? " AND " : "") +
						"upper(xml) LIKE '%" + ragioneSociale.toUpperCase() + "%'"; 
			}
			if(StringUtils.isNotEmpty(email)) {
				filters = filters + (filters.length() > 0 ? " AND " : "") +
						"upper(xml) LIKE '%" + email.toUpperCase() + "%'"; 
			}
			if(StringUtils.isNotEmpty(attivo)) {
				filters = filters + (filters.length() > 0 ? " AND " : "") +
						"b.active = " + attivo; 
			}
			conn = this.getConnection();
			stat = conn.prepareStatement(
					SEARCH_USERS + (filters.length() > 0 ? "WHERE " + filters : "") + " ORDER BY a.username");
			res = stat.executeQuery();
			
			users = new ArrayList<UserDetails>();
			while (res.next()) {
				User user = new User();
				user.setUsername(res.getString(1));
				user.setDisabled( !"1".equals(res.getString(2)) );
				users.add(user);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error while searching users", "searchUsers");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		
		return users;
	}

	protected List<UserDetails> loadUsers(ResultSet result) throws SQLException {
		List<UserDetails> users = new ArrayList<UserDetails>();
		User user = null;
		while (result.next()) {
			String userName = result.getString(1);
			user = new User();
			user.setUsername(userName);
			user.setPassword(result.getString(2));
			user.setCreationDate(result.getDate(3));
			user.setLastAccess(result.getDate(4));
			user.setLastPasswordChange(result.getDate(5));
			int activeId = result.getInt(6);
			user.setDisabled(activeId != 1);
			user.setDelegateUser(result.getString(7));
			user.setCrc(result.getString(8));
			user.setAcceptanceVersion(result.getInt(9));
			users.add(user);
		}
		return users;
	}

	/**
	 * Carica un'utente corrispondente alla userName e password immessa. null se
	 * non vi e' nessun utente corrispondente.
	 *
	 * @param username Nome utente dell'utente cercato
	 * @param password password dell'utente cercato
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi e' nessun utente corrispondente.
	 */
	public UserDetails loadUser(String username, String password) {
		UserDetails user = null;
		try {
			String encrypdedPassword = this.getEncryptedPassword(password);
			user = this.executeLoadingUser(username, encrypdedPassword);
			// PERSONALIZZAZIONE ELDASOFT: si elimina il tentativo di reperire
			// l'utente usando password in chiaro in quanto se uno si copia la
			// password cifrata presente in authusers riuscirebbe ad entrare!
			//if (null == user) {
			//	user = this.executeLoadingUser(username, password);
			//}
			if (user == null) {
				user = this.executeLoadingUser(username, StringUtilities.getSha256(password));
				if (user != null && StringUtils.isEmpty(user.getCrc()))
					setUserCrc(user.getUsername(), calculateCRC(user.getUsername(), password));
			} else {
				String unreadable = getUnreadablePassword(password);
				if (!StringUtils.equals(unreadable, user.getPassword()))
					doChangePassword(user.getUsername(), unreadable, calculateCRC(user.getUsername(), password));
			}
			if (user != null && user instanceof AbstractUser) {
				((AbstractUser) user).setPassword(password);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the user " + username, "loadUser");
		}
		return user;
	}

	private UserDetails executeLoadingUser(String username, String password) {
		Connection conn = null;
		UserDetails user = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USER);
			stat.setString(1, username.toUpperCase());
			stat.setString(2, password);
			res = stat.executeQuery();
			user = this.createUserFromRecord(res);
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the user " + username, "executeLoadingUser");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return user;
	}

	/**
	 * Carica un'utente corrispondente alla userName immessa. null se non vi e'
	 * nessun utente corrispondente.
	 *
	 * @param username Nome utente dell'utente cercato.
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi e' nessun utente corrispondente.
	 */
	public UserDetails loadUser(String username) {
		User user = null;

		Connection conn = null;
		try {
			conn = this.getConnection();
			user = loadUser(conn, username);
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the user " + username, "loadUser");
		} finally {
			closeConnection(conn);
		}

		return user;
	}
	/**
	 * La connection deve essere chiusa nel chiamante
	 *
	 * Carica un'utente corrispondente alla userName immessa. null se non vi e'
	 * nessun utente corrispondente.
	 *
	 * @param username Nome utente dell'utente cercato.
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi e' nessun utente corrispondente.
	 */
	private User loadUser(Connection conn, String username) {
		User user = null;

		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			stat = conn.prepareStatement(LOAD_USER_FROM_USERNAME);
			stat.setString(1, username.toUpperCase());
			res = stat.executeQuery();
			user = this.createUserFromRecord(res);
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the user " + username, "loadUser");
		} finally {
			closeDaoResources(res, stat);
		}

		return user;
	}
	
	/**
	 * Carica un'utente corrispondente alla userName immessa. null se non vi e'
	 * nessun utente corrispondente.
	 *
	 * @return L'oggetto utente corrispondente ai parametri richiesti, oppure null
	 * se non vi e' nessun utente corrispondente.
	 */
	public UserDetails loadUser(UserDetails user) {
		UserDetails u = null;

		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			// carica le informazioni di login dell'utente (login time, logout time, ip, session id, ...)
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USER_LOGIN);
			stat.setString(1, user.getUsername());
			stat.setString(2, user.getSessionId());
			res = stat.executeQuery();
			if (res.next()) {
				String logout = res.getString(4);
				String sessionid = res.getString(6);
				//Chiudo i resource e i stat per fare altre query sul sotto metodo "loadUser"
				closeDaoResources(res, stat);

				u = loadUser(conn, user.getUsername());
				u.setLogoutTime(logout);
				u.setSessionId(sessionid);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the user " + user.getUsername(), "loadUser");
		} finally {
			closeDaoResources(res, stat, conn);
		}

		return u;
	}


	/**
	 * Cancella l'utente.
	 *
	 * @param user L'oggetto di tipo User relativo all'utente da cancellare.
	 */
	public void deleteUser(UserDetails user) {
		this.deleteUser(user.getUsername());
	}

	/**
	 * Cancella l'utente corrispondente alla userName immessa.
	 *
	 * @param username Il nome identificatore dell'utente.
	 */
	public void deleteUser(String username) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.removeUserGroups(username, conn);
			this.removeUserRoles(username, conn);
			stat = conn.prepareStatement(DELETE_USER);
			stat.setString(1, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error deleting a user", "deleteUser");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	/**
	 * Aggiunge un nuovo utente.
	 *
	 * @param user Oggetto di tipo User relativo all'utente da aggiungere.
	 */
	public void addUser(UserDetails user) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_USER);
			stat.setString(1, user.getUsername());
			stat.setString(2, getUnreadablePassword(user.getPassword()));
			stat.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			if (!user.isDisabled()) {
				stat.setInt(4, 1);
			} else {
				stat.setInt(4, 0);
			}
			stat.setString(5, user.getDelegateUser());
			if (user.getAcceptanceVersion() != null)
			  stat.setInt(6, user.getAcceptanceVersion());
			else
			  stat.setNull(6, Types.NUMERIC);
			calculateCRC(user.getUsername(), user.getPassword());
			stat.setString(7, user.getCrc());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error adding a new user", "addUser");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	private String calculateCRC(String username, String password) {
		String toReturn = null;

		try {
			if (customConfigManager.isActiveFunction("PASSWORD-ALGORITMO", "INDECIFRABILE"))
				toReturn = StringUtilities.getSha256(username + SystemConstants.CRC_USER_DELIMITER_CHAR + password);
		} catch (Exception ignored) {}

		return toReturn;
	}

	private String getUnreadablePassword(String password) {
		String toReturn = null;
		try {
			if (customConfigManager.isActiveFunction("PASSWORD-ALGORITMO", "INDECIFRABILE"))
				toReturn = StringUtilities.getSha256(password);
			else
				toReturn = getEncryptedPassword(password);
		} catch (Exception ignored) {

		} finally {
			if (StringUtils.isEmpty(toReturn))
				toReturn = StringUtilities.getSha256(password);
		}
		return toReturn;
	}

	/**
	 * Aggiorna un utente gia' presente con nuovi valori (tranne la username che e'
	 * fissa).
	 *
	 * @param user Oggetto di tipo User relativo all'utente da aggiornare.
	 */
	public void updateUser(UserDetails user) {
		User japsUser = ((user instanceof User) ? (User) user : null);
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_USER);
			stat.setString(1, user.getPassword());
			if (null != japsUser && null != japsUser.getLastAccess()) {
				stat.setDate(2, new java.sql.Date(japsUser.getLastAccess().getTime()));
			} else {
				stat.setNull(2, Types.DATE);
			}
			if (null != japsUser && null != japsUser.getLastPasswordChange()) {
				stat.setDate(3, new java.sql.Date(japsUser.getLastPasswordChange().getTime()));
			} else {
				stat.setNull(3, Types.DATE);
			}
			if (null != japsUser) {
				if (!japsUser.isDisabled()) {
					stat.setInt(4, 1);
				} else {
					stat.setInt(4, 0);
				}
			} else {
				stat.setNull(4, Types.NUMERIC);
			}
			if (null != japsUser && user.getAcceptanceVersion() != null) {
				stat.setInt(5, user.getAcceptanceVersion());
			} else {
				stat.setNull(5, Types.NUMERIC);
			}
			
			// where param
			stat.setString(6, user.getUsername());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error while updating the user " + user.getUsername(), "updateUser");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void changePassword(String username, String password, String crc) {
		doChangePassword(username, getUnreadablePassword(password), crc);
	}

	private void doChangePassword(String username, String password, String crc) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(CHANGE_PASSWORD);
			stat.setString(1, password);
			stat.setDate(2, new java.sql.Date(new Date().getTime()));
			stat.setString(3, crc);
			stat.setString(4, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error updating the password for the user " + username, "changePassword");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void logChangePassword(String username, String password) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOG_CHANGE_PASSWORD);
			stat.setString(1, username);
			stat.setString(2, getUnreadablePassword(password));
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the password changing for the user '" + username + "'", "logChangePassword");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public boolean isPasswordNew(String username, String password) {
		boolean toReturn = false;
		try {
			toReturn = checkPasswordNew(username, StringUtilities.getSha256(password)) && checkPasswordNew(username, getEncryptedPassword(password));
		} catch(Exception e) {
			log.error("Errore durante il controllo se la password è già stata utilizzata", e);
		}
		return toReturn;
	}

	private boolean checkPasswordNew(String username, String password) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		boolean found = false;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(IS_PASSWORD_NEW_TO_LOG);
			stat.setString(1, username);
			stat.setString(2, password);
			result = stat.executeQuery();
			if (result != null)
				found = result.next();
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the password changing for the user '" + username + "'", "isPasswordNew");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return !found;
	}

	@Override
	public void updateLastAccess(String username) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_LAST_ACCESS);
			stat.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
			stat.setString(2, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error updating the password for the user " + username, "updateLastAccess");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	public List<String[]> getLoggedUsers() throws ApsSystemException {
		List<String[]> users = null;
		
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		try {
			conn = this.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOAD_USER_LOGIN_LIST);
			result = stat.executeQuery();
			if (result != null) {
				users = new ArrayList<String[]>();
				while (result.next()) {
					String[] login = new String[6];
					login[0] = (StringUtils.isNotEmpty(result.getString(1)) ? result.getString(1) : "");	// id
					login[1] = (StringUtils.isNotEmpty(result.getString(2)) ? result.getString(2) : "");	// username
					login[2] = (StringUtils.isNotEmpty(result.getString(3)) ? result.getString(3) : "");	// log in time
					login[3] = (StringUtils.isNotEmpty(result.getString(4)) ? result.getString(4) : "");	// log out time (sempre vuoto) 					
					login[4] = (StringUtils.isNotEmpty(result.getString(5)) ? result.getString(5) : "");	// ip address 
					login[5] = (StringUtils.isNotEmpty(result.getString(6)) ? result.getString(6) : "");	// id session					
					users.add(login);
				}
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading logged user list", "getLoggedUsers");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		
		return users;
	}
	
	/**
	 * verifica se l'utente ha un login attivo sul pc da cui viene effettuata la chiamata
	 * Se l'utente ha un login su un pc diverso da quello della chiamata 
	 * (ip, sessionId o nodo cluster diverso) restituisce False altrimenti True
	 * 
	 * @return 0 se l'utente non ha un login
	 *        -1 se l'utente ha gia' un login 
	 *        -2 se l'utente ha gia' un login da un altro nodo (cluster)
	 */
	private int isNewUserLogin(String username, String delegate, String ipAddress, String sessionId) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		int exitCode = 0;
		boolean found = false;
		try {
			boolean isCluster = (StringUtils.isNotEmpty(sessionId) ? sessionId.indexOf(".") > 0 : false);
			
			conn = this.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			// SELECT ipaddress, sessionid FROM ppcommon_accesses WHERE username = ? AND logouttime IS NULL
			stat = conn.prepareStatement(IS_NEW_USER_LOGIN);
			stat.setString(1, username);
			//stat.setString(2, ipAddress);
			//stat.setString(3, sessionId);
			result = stat.executeQuery();
			
			if (result != null) {
				boolean isLoginAccessiDistinti = StringUtils.isNotEmpty(delegate);
				boolean execLoginAccessiDistinti = false;
				
				// verifica se in PPCOMMON_ACCESSES esiste gia' un login per "username" (impresa)
				boolean empty = !result.next();
				if( empty ) {
					found = false;
					execLoginAccessiDistinti = isLoginAccessiDistinti;
				} else {
					String dbIp = (StringUtils.isNotEmpty(result.getString(1)) ? result.getString(1) : "");
					String dbSessionId = (StringUtils.isNotEmpty(result.getString(2)) ? result.getString(2) : "");
					
					if(isLoginAccessiDistinti) {
						// LOGIN CON ACCESSI DISTINTI PER OE (username, delegate)
						// NB: 
						// se viene effettuato un accesso come soggetto impresa (delegate presente)
						// si permette l'accesso simultaneo per la stessa ditta/OE
						// quindi
						// e possibile avere in PPCOMMON_ACCESES piu' righe per lo stesso username, ma con session id diversi 
						// ovvero uno per ogni soggetto impresa che si autentica per la ditta (username, delegate) 
						if( !"*".equals(sessionId) ) {
							if( StringUtils.isNotEmpty(dbSessionId) && !dbSessionId.equals(sessionId) ) {
								// verifica se in PPCOMMON_DELEGATE_ACCESSES esiste gia' un login (username, delegate)
								boolean exists = existsLoginProfiloSSO(username, delegate);
								if( !exists ) {
									// effettua un nuovo login del soggetto impresa (username, delegate)
									execLoginAccessiDistinti = true;
								} else {
									// esiste gia' un login per il soggetto impresa (username, delegate) 
								}
							} else {
								// stessa sessione, quindi lo stesso identico utente dalla stessa postazione
								// effettua un nuovo login del soggetto impresa (username, delegate)
								execLoginAccessiDistinti = true;
							}
						}
						
					} else  {
						// LOGIN STANDARD PER OE (username) 
						if( !isCluster ) {
							// c'e' un altro login dallo stesso pc per l'utente
							//if( !ip.equals(ipAddress) ) {
								exitCode = -1;
							//}
						} else {
							if(StringUtils.isNotEmpty(dbSessionId)) {
								// se l'id sessione e' diverso allora l'utente tenta l'autenticazione 
								// da un client diverso o da un nodo-cluster diverso 
								// che e' gia' loggato
								if( !dbSessionId.equals(sessionId) ) {
									exitCode = (sessionId.indexOf(".") > 0
												? -2		// login da nodo-cluster diverso
												: -1		// login dallo stesso pc
									);
								}
							}
						}
					}
				}
				
				// LOGIN CON ACCESSI DISTINTI PER OE (username, delegate)
				// se necessario esegui il login del soggetto impresa
				if(execLoginAccessiDistinti) {
					boolean loginOk = loginProfiloSSOAccess(username, delegate);
					if( !loginOk ) {
						// lo stesso (utente, delegate) ha gia' effettuando il login su un'altra postazione...
						exitCode = (isCluster ? -2 : -1);  
					}
				}
			}
		} catch (Throwable t) {
			processDaoException(t, "Error checkin the access for the user " + username, "isNewUserLogin");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return exitCode;
	}

	@Override
	public int logLogin(String username, String delegate, String ipAddress, String sessionId) {
		Connection conn = null;
		PreparedStatement stat = null;
		int exitCode = 0;
		boolean found = false;
		try {
			delegate = (StringUtils.isNotEmpty(delegate) ? delegate : null);	// se non c'e' si normalizza a NULL
			
			// chiudi tutti i login aperti e non chiusi da oltre 24h
			Date now = new java.util.Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			cal.add(Calendar.DATE, -1);
			Date now_24 = cal.getTime();
			
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOG_USER_LOGOUT_24H);
			stat.setTimestamp(1, new java.sql.Timestamp(now.getTime()));
			stat.setString(2, username);
			stat.setTimestamp(3, new java.sql.Timestamp(now_24.getTime())); 	// now - 24h
			stat.executeUpdate();
			conn.commit();
			stat.close();
			conn.close();
			
			// effettua il login
			exitCode = this.isNewUserLogin(username, delegate, ipAddress, sessionId);
			found = (exitCode != 0);
			
			conn = this.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			
			if ( !found ) {
				stat = conn.prepareStatement(LOG_USER_LOGIN);
				stat.setString(1, username);
				stat.setString(2, ipAddress);
				stat.setString(3, sessionId);
				stat.executeUpdate();
				conn.commit();
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the access for the user " + username, "logLogin");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return exitCode;
	}

	@Override
	public boolean logLogout(String username, String delegate, String ipAddress, String sessionId) {
		Connection conn = null;
		PreparedStatement stat = null;
		boolean logout = false;
		try {
			int exitCode = this.isNewUserLogin(username, delegate, ipAddress, sessionId);
			boolean isCluster = (exitCode == -2);
			if( !isCluster ) {
				String sql = LOG_USER_LOGOUT;
				if( !"*".equals(ipAddress) ) {
					sql = sql + " AND (ipaddress = '" + ipAddress + "')";
				}
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(sql);
				stat.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
				stat.setString(2, username);
				//stat.setString(3, ipAddress);
				//stat.setString(4, sessionId);
				stat.executeUpdate();
				conn.commit();
				logout = true;
			}
			
			logoutProfiloSSOAccess(username, delegate);
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the access for the user " + username, "logLogout");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return logout;
	}

	@Override
	public void logWrongAccess(String username, String ipAddress, String sessionId) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			String ip = ipAddress;
			if(StringUtils.isNotEmpty(sessionId) && sessionId.indexOf(".") > 0) {
				ip = sessionId.substring(sessionId.indexOf(".") + 1);
			} 

			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOG_WRONG_ACCESS_ATTEMP);
			stat.setString(1, username);
			stat.setString(2, ip);
			//stat.setString(3, sessionId);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the login attempt for the user " + username, "logWrongAccess");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public boolean tooManyWrongPasswordAccessAttempts(String username, int maxAttempts, int inhibitionIpTimeInMinutes) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		long numRows = 0;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(TOO_MANY_WRONG_PASSWORD_ACCESS_ATTEMPTS);
			stat.setString(1, username);
			
			Calendar now = Calendar.getInstance();
		    now.add(Calendar.MINUTE, -inhibitionIpTimeInMinutes);
		    stat.setTimestamp(2, new java.sql.Timestamp(now.getTime().getTime()));
			
			result = stat.executeQuery();
			if (result != null && result.next()) {
				numRows = result.getLong(1);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error checking wrong attempts for the user " + username, "tooManyWrongPasswordAccessAttempts");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return numRows >= maxAttempts;
	}

	@Override
	public boolean tooManyWrongIpAccessAttempts(String ipAddress, int maxAttempts, int inhibitionIpTimeInMinutes) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		long numRows = 0;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(TOO_MANY_WRONG_IP_ACCESS_ATTEMPTS);
			stat.setString(1, ipAddress);
			
			Calendar now = Calendar.getInstance();
		    now.add(Calendar.MINUTE, -inhibitionIpTimeInMinutes);
		    stat.setTimestamp(2, new java.sql.Timestamp(now.getTime().getTime()));
			
			result = stat.executeQuery();
			if (result != null && result.next()) {
				numRows = result.getLong(1);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error checking wrong attempts for the ipaddress " + ipAddress, "tooManyWrongIpAccessAttempts");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return numRows >= maxAttempts;
	}

	@Override
	public void clearWrongAccessAttempts(String username, String ipAddress, String sessionId) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			String ip = ipAddress;
			if(StringUtils.isNotEmpty(sessionId) && sessionId.indexOf(".") > 0) {
				ip = sessionId.substring(sessionId.indexOf(".") + 1);
			} 

			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_WRONG_ACCESS_ATTEMPTS);
			stat.setString(1, username);
			stat.setString(2, ip);
			//stat.setString(3, sessionId);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error clearing wrong access attempts for " + username + " from ip address " + ipAddress, "clearWrongAccessAttempts");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void clearAllWrongAccessAttemptsByUsername(String username) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_ALL_WRONG_ACCESS_ATTEMPTS_BY_USERNAME);
			stat.setString(1, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error clearing wrong access attempts for username " + username, "clearAllWrongAccessAttemptsByUsername");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void clearAllWrongAccessAttemptsByIp(String ipaddress) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_ALL_WRONG_ACCESS_ATTEMPTS_BY_IP);
			stat.setString(1, ipaddress);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error clearing wrong access attempts for ipaddress " + ipaddress, "clearAllWrongAccessAttemptsByIp");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	private void removeUserRoles(String username, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_USER_ROLE);
			stat.setString(1, username);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error deleting the association between the user and his roles", "removeUserRoles");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Crea un utente leggendo i valori dal record corrente del ResultSet passato.
	 * Attenzione: la query di origine del ResultSet deve avere nella select list
	 * i campi esattamente in questo numero e ordine: 1=username, 2=passwd
	 *
	 * @param res Il ResultSet da cui leggere i valori
	 * @return L'oggetto user popolato.
	 * @throws SQLException
	 */
	protected User createUserFromRecord(ResultSet res) throws SQLException {
		User user = null;
		if (res.next()) {
			user = new User();
			user.setUsername(res.getString(1));
			user.setPassword(res.getString(2));
			user.setCreationDate(res.getDate(3));
			user.setLastAccess(res.getDate(4));
			user.setLastPasswordChange(res.getDate(5));
			int activeId = res.getInt(6);
			user.setDisabled(activeId != 1);
			user.setDelegateUser(res.getString(7));
			user.setCrc(res.getString(8));
			user.setAcceptanceVersion(res.getInt(9));
		}
		return user;
	}

	private void removeUserGroups(String username, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_USER_GROUP);
			stat.setString(1, username);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error deleting the association between the user and his roles",
							"removeUserGroups");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	@Override
	public List<String> loadUsernamesForGroup(String groupName) {
		Connection conn = null;
		List<String> usernames = new ArrayList<String>();
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USERNAMES_FROM_GROUP);
			stat.setString(1, groupName);
			res = stat.executeQuery();
			while (res.next()) {
				usernames.add(res.getString(1));
			}
		} catch (Throwable t) {
			processDaoException(t, "Error getting the usernames sharing the same group",
							"loadUsernamesForGroup");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return usernames;
	}

	/**
	 * Carica gli utenti membri di un gruppo.
	 *
	 * @param groupName Il nome del grupo tramite il quale cercare gli utenti.
	 * @return La lista degli utenti (oggetti User) membri del gruppo specificato.
	 */
	public List<UserDetails> loadUsersForGroup(String groupName) {
		Connection conn = null;
		List<UserDetails> users = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USERS_FROM_GROUP);
			stat.setString(1, groupName);
			res = stat.executeQuery();
			users = this.loadUsers(res);
		} catch (Throwable t) {
			processDaoException(t, "Error loading the list of users members of the group",
							"loadUsersForGroup");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return users;
	}

	@Override
	public void updateDelegateUserImpresa(String username, String delegato) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_DELEGATE_USER_IMPRESA);
			stat.setString(1, delegato);
			stat.setString(2, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error updating the delegate user for the user " + username, "updateDelegateUserImpresa");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public void setUserCrc(String username, String crc) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_CRC_USER);
			stat.setString(1, crc);
			stat.setString(2, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error updating the crc for the user " + username, "setUserCrc");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public void setAcceptanceVersion(String username, Integer acceptanceVersion) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_ACCEPTANCE_VERSION);
			if (acceptanceVersion != null) {
			  stat.setInt(1, acceptanceVersion);
            } else {
              stat.setNull(1, Types.NUMERIC);
            }
			stat.setString(2, username);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error updating the acceptance_version for the user " + username, "setAcceptanceVersion");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	/**
	 * Carica e restituisce la lista completa dei soggetti impresa abilitati 
	 * all'accesso dei dati di un operatore economico.
	 *
	 * @return La lista completa deri soggetti impresa
	 */
	@Override
	public List<DelegateUser> loadProfiliSSO(String username, String delegate) {
		List<DelegateUser> delegateUsers = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			UserDetails user = null;
			if(StringUtils.isNotEmpty(username)) {
				user = this.loadUser(username);
			}
			
			// prepara la quiri ed esegui l'sql...
			String sql = LOAD_IMPRESE_DELEGATES +
				(StringUtils.isNotEmpty(username) ? " AND upper(username) = '" + username.toUpperCase() + "'" : "") + 
				(StringUtils.isNotEmpty(delegate) ? " AND upper(delegate) = '" + delegate.toUpperCase() + "'" : "");
			sql = sql + " ORDER BY username, delegate";
			
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(sql);
			res = stat.executeQuery();
			
			delegateUsers = new ArrayList<DelegateUser>();
			while (res.next()) {
				DelegateUser du = createDelegateUserSSOFromRecord(res, user);
				delegateUsers.add(du);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading the OE users list", "loadProfiliSSO");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return delegateUsers;
	}

	/**
	 * Restituisce il soggetto impresa associato ad un'impresa.
	 *
	 * @param delegateUser il delegate user associato all'impresa
	 */
	@Override
	public DelegateUser loadProfiloSSO(String username, String delegate) {
		DelegateUser du = null;
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			UserDetails user = null;
			if(StringUtils.isNotEmpty(username)) {
				user = this.loadUser(username);
			}

			// NB: l'OWNER non viene definito nella "authusers_delegates"
			//     se un soggetto e' OWNER significa che il user.delegateuser 
			//     della ditta corrisponde all'id utente del soggetto
			if(StringUtils.isNotEmpty(user.getDelegateUser()) && user.getDelegateUser().equalsIgnoreCase(delegate)) {
				// OWNER della ditta
				du = createDelegateUser(user.getUsername(), delegate, null, true, DelegateUser.Accesso.EDIT_SEND, null);
			} else {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(LOAD_IMPRESA_DELEGATE);
				stat.setString(1, username);
				stat.setString(2, delegate);
				ResultSet res = stat.executeQuery();
				if(res.next()) {
					du = createDelegateUserSSOFromRecord(res, user);
				}
				closeDaoResources(null, stat, conn);
			}
			
			// carica le info sull'ultimo lock attivo del soggetto impresa...
			if(du != null) {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(LOAD_IMPRESA_DELEGATE_ACCESS);
				stat.setString(1, username);
				stat.setString(2, delegate);
				ResultSet res = stat.executeQuery();
				if(res.next()) {
					DelegateUser access = createDelegateUserSSOAccessFromRecord(res);
					du.setFlusso(access.getFlusso());
					du.setLoginTime(access.getLoginTime());
					du.setLogoutTime(access.getLogoutTime());
				}
			}

		} catch (Throwable t) {
			processDaoException(t, "Error while loading the OE user " + username + "," + delegate, "loadProfiloSSO");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return du;
	}	
	
	/**
	 * restituisce un delegate user dato un record di authusers_delegate 
	 * @throws SQLException 
	 */
	private DelegateUser createDelegateUserSSOFromRecord(ResultSet res, UserDetails user) throws SQLException {
		DelegateUser du = null;
		if (res != null) {
			// SELECT username, delegate, description, rolename, email
			boolean isOwner = (user != null 
							   && StringUtils.isNotEmpty(user.getDelegateUser()) 
							   && user.getDelegateUser().equalsIgnoreCase(res.getString(2)));
			du = createDelegateUser(
					res.getString(1)		// username
					, res.getString(2)		// delegate
					, res.getString(3)		// description 
					, isOwner
					, DelegateUser.valueOfDefault(res.getString(4), DelegateUser.Accesso.READONLY)
					, res.getString(5));	// email
		}
		return du;
	}
	
	private DelegateUser createDelegateUser(
			String username
			, String delegate
			, String description
			, boolean isOwner
			, DelegateUser.Accesso role
			, String email)
	{
		return DelegateUserBuilder.init()
			.setUsername(username)
			.setDelegate(delegate)
			.setDescription(description)
			.setOwner(isOwner)
			.setRolename(isOwner ? DelegateUser.Accesso.EDIT_SEND : role)
			.setEmail(email)
			.build();
	}
	
	/**
	 * Cancella il soggetto impresa associato ad un operatore economico.
	 * 
	 * @param impresa Lo username dell'impresa
	 * @param codiceFiscale Il codice fiscale del soggetto associato all'impresa(o username)
	 */
	@Override
	public void deleteProfiloSSO(String username, String delegate) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			DelegateUser u = this.loadProfiloSSO(username, delegate);
			if( u != null && !u.isOwner() ) {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(DELETE_IMPRESA_DELEGATE);
				// where params
				stat.setString(1, username);
				stat.setString(2, delegate);
				stat.executeUpdate();
				conn.commit();
			}
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error deleting a OE user", "deleteProfiloSSO");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	/**
	 * Aggiunge un nuovo soggetto impresa associato ad un'impresa.
	 *
	 * @param delegateUser il delegate user associato all'impresa
	 */
	@Override
	public void addProfiloSSO(DelegateUser delegateUser) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			if( !delegateUser.isOwner() ) {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(ADD_IMPRESA_DELEGATE);
				stat.setString(1, delegateUser.getUsername());
				stat.setString(2, delegateUser.getDelegate());
				stat.setString(3, delegateUser.getDescription());
				stat.setString(4, delegateUser.getRolename().name());
				stat.setString(5, delegateUser.getEmail());
				stat.executeUpdate();
				conn.commit();
			}
		} catch (Throwable t) {
			processDaoException(t, "Error adding a new OE user", "addProfiloSSO");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	/**
	 * Aggiorna un soggetto impresa gia' presente 
	 *
	 * @param delegateUser il delegate user associato all'impresa
	 */
	@Override
	public void updateProfiloSSO(DelegateUser delegateUser) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			if( !delegateUser.isOwner() ) {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(UPDATE_IMPRESA_DELEGATE);
				stat.setString(1, delegateUser.getDescription());
				stat.setString(2, delegateUser.getRolename().name());
				stat.setString(3, delegateUser.getEmail());
				// where params
				stat.setString(4, delegateUser.getUsername());
				stat.setString(5, delegateUser.getDelegate());
				stat.executeUpdate();
				conn.commit();
			}
		} catch (Throwable t) {
			processDaoException(t, "Error while updating the OE user " + delegateUser.getUsername() + "," + delegateUser.getDelegate(), "updateProfiloSSO");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	/**
	 * verifica se esiste un LOGIN per il soggetto impresa
	 * 
	 * @return true se il login viene effettuato con successo, false viceversa
	 */
	private boolean existsLoginProfiloSSO(String username, String delegate) {
		boolean exists = false;
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			String functionId = "LOGIN";

			conn = this.getConnection();
			conn.setAutoCommit(false);
			// SELECT * FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) AND functionid = ?
			stat = conn.prepareStatement(SELECT_IMPRESA_DELEGATES_ACCESSES);
			stat.setString(1, username);
			stat.setString(2, delegate);
			stat.setString(3, "LOGIN");
			ResultSet res = stat.executeQuery();
			if(res != null && res.next())
				exists = true;
		} catch (Throwable t) {
			exists = false;
			processDaoException(t, "Error while login OE user " + username + "," + delegate, "existsLoginProfiloSSO");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return exists;
	}

	/**
	 * effettua il LOGIN per il soggetto impresa
	 * 
	 * @return true se il login viene effettuato con successo, false viceversa
	 */
	private boolean loginProfiloSSOAccess(String username, String delegate) {
		boolean login = false;
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			String functionId = "LOGIN";
			
			conn = this.getConnection();
			conn.setAutoCommit(false);
			// DELETE FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) AND functionid = ?
			stat = conn.prepareStatement(DELETE_IMPRESA_DELEGATES_ACCESSES);
			stat.setString(1, username);
			stat.setString(2, delegate);
			stat.setString(3, functionId);
			stat.executeUpdate();
			conn.commit();
			closeDaoResources(null, stat, conn);
			
			conn = this.getConnection();
			conn.setAutoCommit(false);
			// INSERT INTO ppcommon_delegate_accesses (username, delegate, functionid, logintime, logouttime) VALUES ( ? , ? , ? , ? , ? )
			stat = conn.prepareStatement(INSERT_IMPRESA_DELEGATES_ACCESSES);
			stat.setString(1, username);
			stat.setString(2, delegate);
			stat.setString(3, functionId);
			stat.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
			stat.setTimestamp(5, null);
			stat.executeUpdate();
			conn.commit();
			
			login = true;
		} catch (Throwable t) {
			login = false;
			processDaoException(t, "Error while login OE user " + username + "," + delegate, "loginProfiloSSOAccess");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return login;
	}
	
	/**
	 * effettua il LOGOUT per il soggetto impresa
	 * 
	 * @return true se il login viene effettuato con successo, false viceversa
	 */
	private boolean logoutProfiloSSOAccess(String username, String delegate) {
		boolean logout = true;
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			// rimuovi tutti gli eventuali lock ancora aperti
			unlockProfiloSSOAccess(username, delegate);

			// esegui il LOGOUT
			// ed elimina tutte le occorrenze relative alle funzioni utilizzate
			conn = this.getConnection();
			conn.setAutoCommit(false);
//			// DELETE FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) AND functionid = ?
//			stat = conn.prepareStatement(DELETE_IMPRESA_DELEGATES_ACCESSES);
			// DELETE FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) 
			stat = conn.prepareStatement(DELETE_IMPRESA_ALL_DELEGATES_ACCESSES);
			stat.setString(1, username);
			stat.setString(2, delegate);
//			stat.setString(3, "LOGIN");
			stat.executeUpdate();
			conn.commit();
			
			logout = true;
		} catch (Throwable t) {
			logout = false;
			processDaoException(t, "Error while logout OE user " + username + "," + delegate, "logoutProfiloSSOAccess");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return logout;
	}

	/**
	 * effettua un LOCK sulla risorsa "functionId" per il soggetto impresa
	 * 
	 * @return true se il lock viene portato a termine, false viceversa
	 */
	@Override
	public boolean lockProfiloSSOAccess(String username, String delegate, String functionId) {
		boolean locked = false;
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			if(StringUtils.isEmpty(delegate)) {
				// acceso standard solo per "username"
				// si simula un lock andato a buon fine senza inserire nulla
				locked = true;
			} else {
				// NB: 
				// ogni metodo di un DAO viene sempre eseguito in modo atomico perche' 
				// e' l'oggetto manager che gestisce la concorrenza tra le varie request
				boolean canLock = true;
				boolean update = false;
				
				// carica tutti i lock attivi per l'impresa (username, funzione) 
				// SELECT username, delegate, functionid, logintime, logouttime FROM ppcommon_delegate_accesses 
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(LOAD_IMPRESE_DELEGATES_LOCKS);
				stat.setString(1, username);
				stat.setString(2, functionId);
				//stat.setString(3, delegate);
				ResultSet res = stat.executeQuery();
				while (res.next()) {
					if(res.getString(2).equalsIgnoreCase(delegate)) {
						// esiste gia' un LOCK per il soggetto corrente 
						update = true;
					} else if(res.getString(5) == null) {
						// esiste gia' un LOCK di un altro soggetto impresa
						canLock = false;
					}
				}
				closeDaoResources(null, stat, conn);
			
				if(canLock) {
					conn = this.getConnection();
					conn.setAutoCommit(false);
					if(update) {
						// UPDATE ppcommon_delegate_accesses SET functionid = ? AND logintime = ? AND logouttime = ? WHERE upper(username) = ? AND upper(delegate) = ?
						stat = conn.prepareStatement(UPDATE_IMPRESA_DELEGATES_ACCESSES);
						stat.setString(1, functionId);
						stat.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
						stat.setTimestamp(3, null);
						// where params
						stat.setString(4, username);
						stat.setString(5, delegate);
						stat.setString(6, functionId);
					} else {
						// INSERT INTO ppcommon_delegate_accesses (username, delegate, functionid, logintime, logouttime) VALUES ( ? , ? , ? , ? , ? )
						stat = conn.prepareStatement(INSERT_IMPRESA_DELEGATES_ACCESSES);
						stat.setString(1, username);
						stat.setString(2, delegate);
						stat.setString(3, functionId);
						stat.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
						stat.setTimestamp(5, null);
					}
					stat.executeUpdate();
					conn.commit();
					locked = true;
				}
			}
		} catch (Throwable t) {
			locked = false;
			processDaoException(t, "Error while locking OE user " + username + "," + delegate + " on " + functionId, "lockProfiloSSOAccess");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return locked;
	}
	
	/**
	 * effettua un UNLOCK sulla risorsa "functionId" per il soggetto impresa
	 * 
	 * @return true se l'unlock viene portato a termine, false viceversa
	 */
	@Override
	public boolean unlockProfiloSSOAccess(String username, String delegate) {
		boolean unlocked = false; 
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			if(StringUtils.isNotEmpty(delegate)) {
				// UPDATE ppcommon_delegate_accesses SET logouttime = ? WHERE logouttime IS NULL AND upper(username) = upper(?) AND upper(delegate) = upper(?)
				String sql = UNLOCK_IMPRESA_DELEGATES_ACCESSES + " AND functionid <> 'LOGIN'";
				conn = this.getConnection();
				conn.setAutoCommit(false);
				stat = conn.prepareStatement(sql);
				stat.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
				// where param
				stat.setString(2, username);
				stat.setString(3, delegate);
				stat.executeUpdate();
				conn.commit();
			}
			unlocked = true;
		} catch (Throwable t) {
			processDaoException(t, "Error while unlocking OE user " + username + "," + delegate, "unlockProfiloSSOAccess");
		} finally {
			closeDaoResources(null, stat, conn);
		}
		return unlocked;
	}

	/**
	 * Restituisce il lock del soggetto impresa di una ditta/OE 
	 *
	 * @return lock del soggetto impresa
	 */
	@Override
	public DelegateUser loadProfiloSSOAccess(String username, String delegate) {
		DelegateUser access = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOAD_IMPRESA_DELEGATE_ACCESS); 
			stat.setString(1, username);
			stat.setString(2, delegate);
			
			res = stat.executeQuery();
			if(res.next()) {
				access = createDelegateUserSSOAccessFromRecord(res);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading accesses for OE user", "loadProfiloSSOAccess");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return access;
	}
	
	/**
	 * @throws SQLException 
	 */
	private DelegateUser createDelegateUserSSOAccessFromRecord(ResultSet res) throws SQLException {
		DelegateUser access = null;
		if (res != null) {
			// SELECT username, delegate, functionid, login, logout
			access = new DelegateUser();
			access.setUsername(res.getString(1));
			access.setDelegate(res.getString(2));
			access.setFlusso(res.getString(3));
			access.setLoginTime(res.getTimestamp(4));
			access.setLogoutTime(res.getTimestamp(5));
			access.setDescription(res.getString(6));
		}
		return access;
	}
	
	/**
	 * Restituisce la lista degli accessi dei soggetti impresa di una ditta/OE 
	 *
	 * @return La lista completa deri soggetti impresa
	 */
	@Override
	public List<DelegateUser> loadProfiliSSOAccesses(String username) {
		List<DelegateUser> accesses = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			// prepara la quiri ed esegui l'sql...
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOAD_IMPRESA_DELEGATES_ACCESSES);
			stat.setString(1, username);
			res = stat.executeQuery();
			
			accesses = new ArrayList<DelegateUser>();
			while (res.next()) {
				DelegateUser a = createDelegateUserSSOAccessFromRecord(res);
				accesses.add(a);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading accesses for OE user", "loadProfiliSSOAccesses");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return accesses;
	}

	/**
	 * restituisce la password decifrata   
	 */
	protected String getEncryptedPassword(String password) throws ApsSystemException {
		String encrypted = password;
		if (null != this.getEncrypter())
			encrypted = this.getEncrypter().encrypt(password);
		return encrypted;
	}

	protected IApsEncrypter getEncrypter() {
		return _encrypter;
	}

	public void setEncrypter(IApsEncrypter encrypter) {
		this._encrypter = encrypter;
	}

	private IApsEncrypter _encrypter;

	private final String PREFIX_LOAD_USERS
					= "SELECT authusers.username, authusers.passwd, authusers.registrationdate, "
					+ "authusers.lastaccess, authusers.lastpasswordchange, authusers.active, authusers.delegateuser, authusers.crc, "
					+ "authusers.acceptance_version "
					+ "FROM authusers ";

	private final String LOAD_USERS
					= PREFIX_LOAD_USERS + "ORDER BY authusers.username";

	private final String SEARCH_USERS_BY_TEXT
					= PREFIX_LOAD_USERS + " WHERE upper(authusers.username) LIKE ? ORDER BY authusers.username";
	
	private final String SEARCH_USERS_BY_DELEGATE_USER
					= PREFIX_LOAD_USERS + " WHERE authusers.delegateuser = ? ORDER BY authusers.username";

	private final String LOAD_USER
					= PREFIX_LOAD_USERS + "WHERE upper(authusers.username) = ? AND authusers.passwd = ? ";

	private final String LOAD_USER_FROM_USERNAME
					= PREFIX_LOAD_USERS + "WHERE upper(authusers.username) = ? ";

	private final String LOAD_USERS_FROM_GROUP
					= PREFIX_LOAD_USERS + " LEFT JOIN authusergroups ON authusers.username = authusergroups.username "
					+ "WHERE authusergroups.groupname = ? ORDER BY authusers.username";

	private final String LOAD_USERNAMES_FROM_GROUP
					= "SELECT authusergroups.username FROM authusergroups WHERE authusergroups.groupname = ? ORDER BY authusergroups.username";

	private final String DELETE_USER
					= "DELETE FROM authusers WHERE username = ? ";

	private final String ADD_USER
					= "INSERT INTO authusers (username, passwd, registrationdate, active, delegateuser, acceptance_version, crc) VALUES ( ? , ? , ? , ? , ? , ? , ?)";

	private final String CHANGE_PASSWORD
					= "UPDATE authusers SET passwd = ? , lastpasswordchange = ?, crc = ? WHERE username = ? ";

	private final String UPDATE_USER
					= "UPDATE authusers SET passwd = ? , lastaccess = ? , lastpasswordchange = ? , active = ? , acceptance_version = ? WHERE username = ? ";

	private final String UPDATE_LAST_ACCESS
					= "UPDATE authusers SET lastaccess = ? WHERE username = ? ";

	private final String UPDATE_DELEGATE_USER_IMPRESA
					= "UPDATE authusers SET delegateuser = ? WHERE username = ? ";
	
	private final String UPDATE_CRC_USER
					= "UPDATE authusers SET crc = ? WHERE username = ? ";

	private final String UPDATE_ACCEPTANCE_VERSION
					= "UPDATE authusers SET acceptance_version = ? WHERE username = ? ";

	private final String LOG_USER_LOGIN
					= "INSERT INTO ppcommon_accesses (username, ipaddress, sessionid) VALUES (?, ?, ?) ";

	private final String LOG_USER_LOGOUT
					= "UPDATE ppcommon_accesses SET logouttime = ? WHERE username = ? AND logouttime IS NULL ";
					  //+ " AND (ipaddress = ?)"; 

	private final String LOG_USER_LOGOUT_24H
					= "UPDATE ppcommon_accesses SET logouttime = ? WHERE username = ? AND logouttime IS NULL AND (logintime < ?) ";
	
	private final String LOAD_USER_LOGIN_LIST
					= "SELECT id, username, logintime, logouttime, ipaddress, sessionid FROM ppcommon_accesses WHERE logouttime IS NULL ";	

	private final String LOAD_USER_LOGIN
					= "SELECT id, username, logintime, logouttime, ipaddress, sessionid FROM ppcommon_accesses WHERE logouttime IS NULL AND username = ? AND sessionid = ? ";

	private final String LOG_WRONG_ACCESS_ATTEMP
					= "INSERT INTO ppcommon_wrongaccesses (username, ipaddress) VALUES (?, ?) ";

	private final String DELETE_USER_GROUP
					= "DELETE FROM authusergroups WHERE username = ? ";

	private final String DELETE_USER_ROLE
					= "DELETE FROM authuserroles WHERE username = ? ";

	private final String LOG_CHANGE_PASSWORD
					= "INSERT INTO ppcommon_passwords (username, passwd) VALUES (?, ?) ";

	private final String IS_PASSWORD_NEW_TO_LOG
					= "SELECT 1 FROM ppcommon_passwords WHERE username = ? AND passwd = ? ";

	private final String IS_NEW_USER_LOGIN
					= "SELECT ipaddress, sessionid FROM ppcommon_accesses WHERE username = ? AND logouttime IS NULL ";

	private final String TOO_MANY_WRONG_PASSWORD_ACCESS_ATTEMPTS
					= "SELECT COUNT(*) FROM ppcommon_wrongaccesses WHERE username = ? AND logintime >= ?";

	private final String DELETE_WRONG_ACCESS_ATTEMPTS
					= "DELETE FROM ppcommon_wrongaccesses WHERE username = ? AND (ipaddress = ?)";

	private final String DELETE_ALL_WRONG_ACCESS_ATTEMPTS_BY_USERNAME
					= "DELETE FROM ppcommon_wrongaccesses WHERE username = ? ";

	private final String TOO_MANY_WRONG_IP_ACCESS_ATTEMPTS
					= "SELECT COUNT(*) FROM ppcommon_wrongaccesses WHERE ipaddress = ? AND logintime >= ?";

	private final String DELETE_ALL_WRONG_ACCESS_ATTEMPTS_BY_IP
					= "DELETE FROM ppcommon_wrongaccesses WHERE ipaddress = ? ";

	private final String SEARCH_USERS
					= "SELECT a.username, b.active FROM jpuserprofile_authuserprofiles a JOIN authusers b ON a.username=b.username ";
	
	// profilazione accessi per ditta con account SSO (delegate users)
	//
	private final String PREFIX_LOAD_IMPRESE_DELEGATES
					= "SELECT username, delegate, description, rolename, email "
					+ "FROM authusers_delegates ";
	
	private final String LOAD_IMPRESE_DELEGATES 
					= PREFIX_LOAD_IMPRESE_DELEGATES + "WHERE 1=1 ";
	
	private final String LOAD_IMPRESA_DELEGATE
					= PREFIX_LOAD_IMPRESE_DELEGATES + "WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) ";
	
	private final String DELETE_IMPRESA_DELEGATE
					= "DELETE FROM authusers_delegates WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) ";

	private final String ADD_IMPRESA_DELEGATE
					= "INSERT INTO authusers_delegates (username, delegate, description, rolename, email) VALUES ( ? , ? , ? , ? , ? )";
	
	private final String UPDATE_IMPRESA_DELEGATE
					= "UPDATE authusers_delegates SET description = ?, rolename = ?, email = ? WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) ";

	private final String PREFIX_LOAD_IMPRESE_DELEGATES_ACCESSES 
					= "SELECT a.username, a.delegate, a.functionid, a.logintime, a.logouttime, d.description "
					+ "FROM ppcommon_delegate_accesses a LEFT JOIN authusers_delegates d ON a.username=d.username AND a.delegate=d.delegate ";

	private final String LOAD_IMPRESE_DELEGATES_LOCKS 
					= "SELECT username, delegate, functionid, logintime, logouttime "
					+ "FROM ppcommon_delegate_accesses " 
					+ "WHERE upper(username) = upper(?) AND functionid = ? "; //AND upper(delegate) <> upper(?) AND logouttime IS NULL  

	private final String INSERT_IMPRESA_DELEGATES_ACCESSES
					= "INSERT INTO ppcommon_delegate_accesses (username, delegate, functionid, logintime, logouttime) VALUES ( ? , ? , ? , ? , ? )";

	private final String UPDATE_IMPRESA_DELEGATES_ACCESSES
					= "UPDATE ppcommon_delegate_accesses SET functionid = ?, logintime = ?, logouttime = ? WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) AND functionid = ? ";

	private final String SELECT_IMPRESA_DELEGATES_ACCESSES
					= "SELECT username, delegate, functionid, logintime, logouttime "
					+ "FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) AND functionid = ? ";
	
	private final String DELETE_IMPRESA_DELEGATES_ACCESSES
					= "DELETE FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) AND functionid = ? ";
	
	private final String DELETE_IMPRESA_ALL_DELEGATES_ACCESSES
					= "DELETE FROM ppcommon_delegate_accesses WHERE upper(username) = upper(?) AND upper(delegate) = upper(?) ";

	private final String UNLOCK_IMPRESA_DELEGATES_ACCESSES 
					= "UPDATE ppcommon_delegate_accesses SET logouttime = ? WHERE logouttime IS NULL AND upper(username) = upper(?) AND upper(delegate) = upper(?) ";

	private final String LOAD_IMPRESA_DELEGATE_ACCESS 
					= PREFIX_LOAD_IMPRESE_DELEGATES_ACCESSES
					+ "WHERE logouttime IS NULL AND upper(a.username) = upper(?) AND upper(a.delegate) = upper(?) "
					+ "ORDER BY logintime DESC ";
	
	private final String LOAD_IMPRESA_DELEGATES_ACCESSES 
					= PREFIX_LOAD_IMPRESE_DELEGATES_ACCESSES 
					+ "WHERE a.logouttime IS NULL AND upper(a.username) = upper(?) "
					+ "ORDER BY a.username, a.delegate ";

}
