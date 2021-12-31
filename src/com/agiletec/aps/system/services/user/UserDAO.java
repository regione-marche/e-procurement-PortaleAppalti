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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;
import com.agiletec.aps.util.IApsEncrypter;

/**
 * Data Access Object per gli oggetti Utente.
 *
 * @author M.Diana - E.Santoboni
 */
public class UserDAO extends AbstractDAO implements IUserDAO {

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
			processDaoException(t, "Error loading the users list", "loadUsersList");
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
			
			if (null != user && user instanceof AbstractUser) {
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
		Connection conn = null;
		User user = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USER_FROM_USERNAME);
			stat.setString(1, username.toUpperCase());
			res = stat.executeQuery();
			user = this.createUserFromRecord(res);
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the user " + username, "loadUser");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return user;
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
			String encrypdedPassword = this.getEncryptedPassword(user.getPassword());
			stat.setString(2, encrypdedPassword);
			stat.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			if (!user.isDisabled()) {
				stat.setInt(4, 1);
			} else {
				stat.setInt(4, 0);
			}
			stat.setString(5, user.getDelegateUser());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error adding a new user", "addUser");
		} finally {
			closeDaoResources(null, stat, conn);
		}
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

			stat.setString(5, user.getUsername());
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
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(CHANGE_PASSWORD);
			String encrypdedPassword = this.getEncryptedPassword(password);
			stat.setString(1, encrypdedPassword);
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
			String encrypdedPassword = this.getEncryptedPassword(password);
			stat.setString(1, username);
			stat.setString(2, encrypdedPassword);
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
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		boolean found = false;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(IS_PASSWORD_NEW_TO_LOG);
			String encrypdedPassword = this.getEncryptedPassword(password);
			stat.setString(1, username);
			stat.setString(2, encrypdedPassword);
			result = stat.executeQuery();
			if (result != null) {
				found = result.next();
			}
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

	@Override
	public void logLogin(String username, String ipAddress) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		boolean found = false;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(IS_NEW_USER_LOGIN);
			stat.setString(1, username);
			stat.setString(2, ipAddress);
			result = stat.executeQuery();
			if (result != null) {
				found = result.next();
			}
			if (!found) {
				stat = conn.prepareStatement(LOG_USER_LOGIN);
				stat.setString(1, username);
				stat.setString(2, ipAddress);
				stat.executeUpdate();
				conn.commit();
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the access for the user " + username, "logLogin");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void logLogout(String username, String ipAddress) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOG_USER_LOGOUT);
			stat.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
			stat.setString(2, username);
			stat.setString(3, ipAddress);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error loggin the access for the user " + username, "logLogout");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void logWrongAccess(String username, String ipAddress) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(LOG_WRONG_ACCESS_ATTEMP);
			stat.setString(1, username);
			stat.setString(2, ipAddress);
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
	public void clearWrongAccessAttempts(String username, String ipAddress) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_WRONG_ACCESS_ATTEMPTS);
			stat.setString(1, username);
			stat.setString(2, ipAddress);
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
	
	protected String getEncryptedPassword(String password) throws ApsSystemException {
		String encrypted = password;
		if (null != this.getEncrypter()) {
			encrypted = this.getEncrypter().encrypt(password);
		}
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
					+ "authusers.lastaccess, authusers.lastpasswordchange, authusers.active, authusers.delegateuser, authusers.crc  FROM authusers ";

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
					= "INSERT INTO authusers (username, passwd, registrationdate, active, delegateuser) VALUES ( ? , ? , ? , ?, ? )";

	private final String CHANGE_PASSWORD
					= "UPDATE authusers SET passwd = ? , lastpasswordchange = ?, crc = ? WHERE username = ? ";

	private final String UPDATE_USER
					= "UPDATE authusers SET passwd = ? , lastaccess = ? , lastpasswordchange = ? , active = ? WHERE username = ? ";

	private final String UPDATE_LAST_ACCESS
					= "UPDATE authusers SET lastaccess = ? WHERE username = ? ";

	private final String UPDATE_DELEGATE_USER_IMPRESA
					= "UPDATE authusers SET delegateuser = ? WHERE username = ? ";
	
	private final String UPDATE_CRC_USER
					= "UPDATE authusers SET crc = ? WHERE username = ? ";

	private final String LOG_USER_LOGIN
					= "INSERT INTO ppcommon_accesses (username, ipaddress) VALUES (?, ?) ";

	private final String LOG_USER_LOGOUT
					= "UPDATE ppcommon_accesses SET logouttime = ? WHERE username = ? AND ipaddress = ? AND logouttime IS NULL";

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
					= "SELECT 1 FROM ppcommon_accesses WHERE username = ? AND ipaddress = ? AND logouttime IS NULL ";

	private final String TOO_MANY_WRONG_PASSWORD_ACCESS_ATTEMPTS
					= "SELECT COUNT(*) FROM ppcommon_wrongaccesses WHERE username = ? AND logintime >= ?";

	private final String DELETE_WRONG_ACCESS_ATTEMPTS
					= "DELETE FROM ppcommon_wrongaccesses WHERE username = ? AND ipaddress = ? ";

	private final String DELETE_ALL_WRONG_ACCESS_ATTEMPTS_BY_USERNAME
					= "DELETE FROM ppcommon_wrongaccesses WHERE username = ? ";

	private final String TOO_MANY_WRONG_IP_ACCESS_ATTEMPTS
					= "SELECT COUNT(*) FROM ppcommon_wrongaccesses WHERE ipaddress = ? AND logintime >= ?";

	private final String DELETE_ALL_WRONG_ACCESS_ATTEMPTS_BY_IP
					= "DELETE FROM ppcommon_wrongaccesses WHERE ipaddress = ? ";

	private final String SEARCH_USERS
					= "SELECT a.username, b.active FROM jpuserprofile_authuserprofiles a JOIN authusers b ON a.username=b.username ";

}
