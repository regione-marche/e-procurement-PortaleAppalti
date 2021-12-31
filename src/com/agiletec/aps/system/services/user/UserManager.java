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

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Servizio di gestione degli utenti.
 *
 * @author M.Diana - E.Santoboni
 */
public class UserManager extends AbstractService implements IUserManager, GroupUtilizer {

	private static final int ADMIN_MIN_PASSWORD_LEN	 	= 14;
	private static final int ADMIN_MAX_PASSWORD_LEN		= 20;
	private static final int USER_MIN_PASSWORD_LEN	 	= 8;
	private static final int USER_MAX_PASSWORD_LEN	 	= 20;
	// caratteri speciali ammessi (alcuni caratteri necessitano di escape  (" diventa \")
	// nb: per le regular expression esistono poi dei caratteri speciali che necessitano di escape 
	//	   < > ( )	[ ] { }	\ ^	- =	$ !	| ? * +	.
	//     e quindi il set	~#"$%&'()*+,-./:;<=>?!@[]^_\
	//	   (per java) 		~#\"$%&'()*+,-./:;<=>?!@[]^_\\
	//	   (per regex)		~#\"\\$%&'\\(\\)\\*\\+,\\-\\./:;\\<\\=\\>\\?\\!@\\[\\]\\^_\\\\
	//private static final String SPECIAL_CHARS 			= "_.&$@!-";
	private static final String SPECIAL_CHARS			= "~#\"\\$%&'\\(\\)\\*\\+,\\-\\./:;\\<\\=\\>\\?\\!@\\[\\]\\^_\\\\";
			
	private String error;								// ie. "wrongCharactersAdmin", "wrongSintaxAdmin", ...
    private int countUpperChars;						// conteggio dei caratteri maiuscoli presenti nella password
    private int countLowerChars;						// conteggio dei caratteri minuscoli presenti nella password
    private int countNumberChars;						// conteggio delle cifre presenti nella password
    private int countSpecialChars;						// conteggio dei caratteri speciali
    private int countMaxConsecutiveEqualChars;			// conteggio del massimo numero di carattere uguali

    
    private IAuthorizationManager authorizationManager;
    private IUserProfileManager userProfileManager;
	private IUserDAO _userDao;
	private ConfigInterface _configManager;
	private boolean _enabledPrivacyModule;

	protected ConfigInterface getConfigManager() {
		return _configManager;
	}

	public void setConfigManager(ConfigInterface configManager) {
		this._configManager = configManager;
	}

	protected IUserDAO getUserDAO() {
		return _userDao;
	}

	public void setUserDAO(IUserDAO userDao) {
		this._userDao = userDao;
	}
	
	public IUserProfileManager getUserProfileManager() {
		return userProfileManager;
	}

	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this.userProfileManager = userProfileManager;
	}

	@Override
	public boolean isEnabledPrivacyModule() {
		return _enabledPrivacyModule;
	}

	public void setEnabledPrivacyModule(boolean _enabledPrivacyModule) {
		this._enabledPrivacyModule = _enabledPrivacyModule;
	}	

	protected IAuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	

	@Override
	public void init() throws Exception {
		String enabledPrivacyModuleParValue = this.getConfigManager().getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED);
		this.setEnabledPrivacyModule(Boolean.parseBoolean(enabledPrivacyModuleParValue));
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized");
	}

	@Override
	public List<UserDetails> getUsers() throws ApsSystemException {
		List<UserDetails> users = null;
		try {
			users = this.getUserDAO().loadUsers();
			for (int i = 0; i < users.size(); i++) {
				this.setUserCredentialCheckParams(users.get(i));
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading the list of users", t);
		}
		return users;
	}

	@Override
	public List<UserDetails> searchUsers(String text) throws ApsSystemException {
		List<UserDetails> users = null;
		try {
			users = this.getUserDAO().searchUsers(text);
			for (int i = 0; i < users.size(); i++) {
				this.setUserCredentialCheckParams(users.get(i));
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading the user list", t);
		}
		return users;
	}
	
	@Override
	public List<UserDetails> searchUsers(
			String username, 
			String ragioneSociale, 
			String email, 
			String attivo) throws ApsSystemException 
	{
		List<UserDetails> users = null;
		try {	
			// NB: 
			//  IUserDAO.searchUsers(...) filtra i campi "ragione sociale" ed "email" 
			// 	nel campo "xml" percio' in caso di ricerca con filtri su questi due campi
			//  e' necessario verificare se sono effettivamente presenti nello
			//  IUserProfile (Nome, email)
			if(StringUtils.isNotEmpty(ragioneSociale)) {
				ragioneSociale = ragioneSociale.toUpperCase();
			}
			if(StringUtils.isNotEmpty(email)) {
				email = email.toUpperCase();
			}
			
			List<UserDetails> list = this._userDao.searchUsers(username, ragioneSociale, email, attivo);
			if(list != null) {
				users = new ArrayList<UserDetails>();
			}
			
			for (UserDetails item : list) {
			
				IUserProfile p = this.userProfileManager.getProfile(item.getUsername());
				String pDenominazione = "";
				String pEmail = "";
				if(p != null) {
					pDenominazione = (p.getValue("Nome") != null ? ((String) p.getValue("Nome")).toUpperCase() : "");
					pEmail = (p.getValue("email") != null ? ((String) p.getValue("email")).toUpperCase() : "");
				}
				
				// filtra i campi "ragione sociale", "email" nel modo corretto
				boolean add = true;
				if(StringUtils.isNotEmpty(ragioneSociale)) {
					if( !pDenominazione.contains(ragioneSociale) ) {
						add = false;
					}
				}
				if(StringUtils.isNotEmpty(email)) {
					if( !pEmail.contains(email) ) {
						add = false;
					}
				}
				
				// aggiungi l'operatore economico alla lista dei risultati...
				if(add) {
					users.add(item);
				}
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading the user list", t);
		}
		return users;
	}

	@Override
	public void removeUser(UserDetails user) throws ApsSystemException {
		try {
			this.getUserDAO().deleteUser(user);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeUser");
			throw new ApsSystemException("Error deleting a user", t);
		}
	}

	@Override
	public void removeUser(String username) throws ApsSystemException {
		try {
			this.getUserDAO().deleteUser(username);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeUser");
			throw new ApsSystemException("Error deleting a user", t);
		}
	}

	@Override
	public void updateUser(UserDetails user) throws ApsSystemException {
		try {
			this.getUserDAO().updateUser(user);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateUser");
			throw new ApsSystemException("Error updating the User", t);
		}
	}

	@Override
	public void changePassword(String username, String password) throws Exception {
		try {
			String crc = null;
			UserDetails user = getUser(username);
			if(user != null && isAdmin(user)){
				crc = calculateUserCrc(username, password);
			}
			
			// applica la policy sulle password...
			if(user != null) {
				String error = this.validatePassword(user, password);
				if(StringUtils.isNotEmpty(error)) {
					ActionSupport action = (ActionSupport)ActionContext.getContext().getActionInvocation().getAction();
					String msg = action.getText(error); 
					action.addActionError(msg);
					throw new Exception(msg);
				}
			}
			
			this.getUserDAO().changePassword(username, password, crc);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "changePassword");
			throw e;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "changePassword");
			throw new ApsSystemException("Error updating the password of the User " + username, t);
		}
	}

	@Override
	public void logChangePassword(String username, String password) throws ApsSystemException {
		try {
			this.getUserDAO().logChangePassword(username, password);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "changePassword");
			throw new ApsSystemException("Error loggin the password changing of the User" + username, t);
		}
	}

	@Override
	public boolean isPasswordNew(String username, String password) throws ApsSystemException {
		try {
			return this.getUserDAO().isPasswordNew(username, password);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "isPasswordNew");
			throw new ApsSystemException("Error while checking password log before changing password of the User" + username, t);
		}
	}

	@Override
	public void updateLastAccess(UserDetails user) throws ApsSystemException {
		if (!user.isJapsUser()) {
			return;
		}
		try {
			this.getUserDAO().updateLastAccess(user.getUsername());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateLastAccess");
			throw new ApsSystemException("Error while refreshing the last access date of the User " + user.getUsername(), t);
		}
	}

	@Override
	public void logLogin(String username, String ipAddress) throws ApsSystemException {
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(ipAddress)) {
			try {
				this.getUserDAO().logLogin(username, ipAddress);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "logLogin");
				throw new ApsSystemException("Error while logging the access of the User " + username, t);
			}
		}
	}

	@Override
	public void clearWrongAccessAttempts(String username, String ipAddress) throws ApsSystemException {
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(ipAddress)) {
			try {
				this.getUserDAO().clearWrongAccessAttempts(username, ipAddress);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "logLogin");
				throw new ApsSystemException("Error while logging the access of the User " + username, t);
			}
		}
	}

	@Override
	public void logLogout(String username, String ipAddress) throws ApsSystemException {
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(ipAddress)) {
			try {
				this.getUserDAO().logLogout(username, ipAddress);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "logLogout");
				throw new ApsSystemException("Error while logging the exipation of the User " + username + " session", t);
			}
		}
	}

	@Override
	public void logWrongAccess(String username, String ipAddress) throws ApsSystemException {
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(ipAddress)) {
			try {
				this.getUserDAO().logWrongAccess(username, ipAddress);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "logWrongAccess");
				throw new ApsSystemException("Error while logging the log attempt with username " + username + " from ipaddress " + ipAddress, t);
			}
		}
	}

	@Override
	public boolean tooManyWrongPasswordAccessAttempts(String username, int numMaxLoginAttemptsWrongPassword) throws ApsSystemException {
		try {
			int inhibitionIpTimeInMinutes = this.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
			return this.getUserDAO().tooManyWrongPasswordAccessAttempts(username, numMaxLoginAttemptsWrongPassword, inhibitionIpTimeInMinutes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "tooManyWrongPasswordAccessAttempts");
			throw new ApsSystemException("Error while checking for too many wrong password attempts with username " + username, t);
		}
	}

	@Override
	public boolean tooManyWrongIpAccessAttempts(String ipAddress, int numMaxWrongAttemptsBySameIp) throws ApsSystemException {
		try {
			int inhibitionIpTimeInMinutes = this.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);			
			return this.getUserDAO().tooManyWrongIpAccessAttempts(ipAddress, numMaxWrongAttemptsBySameIp, inhibitionIpTimeInMinutes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "tooManyWrongIpAccessAttempts");
			throw new ApsSystemException("Error while checking for too many attempts from ip address " + ipAddress, t);
		}
	}

	@Override
	public void clearAllWrongAccessAttemptsByUsername(String username) throws ApsSystemException {
		try {
			this.getUserDAO().clearAllWrongAccessAttemptsByUsername(username);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "clearAllWrongAccessAttemptsByUsername");
			throw new ApsSystemException("Error cleaning all wrong access attempts ", t);
		}
	}

	@Override
	public void clearAllWrongAccessAttemptsByIp(String ipaddress) throws ApsSystemException {
		try {
			this.getUserDAO().clearAllWrongAccessAttemptsByIp(ipaddress);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "clearAllWrongAccessAttemptsByIp");
			throw new ApsSystemException("Error cleaning all wrong access attempts ", t);
		}
	}

	/**
	 * Aggiunge un utente nel db.
	 *
	 * @param user L'utente da aggiungere nel db.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	@Override
	public void addUser(UserDetails user) throws ApsSystemException {
		try {
			this.getUserDAO().addUser(user);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addUser");
			throw new ApsSystemException("Error adding a new user ", t);
		}
	}

	/**
	 * Recupera un'user caricandolo da db. Se la userName non corrisponde ad un
	 * utente restituisce null.
	 *
	 * @param username Lo username dell'utente da restituire.
	 * @return L'utente cercato, null se non vi e' nessun utente corrispondente
	 * alla username immessa.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	@Override
	public UserDetails getUser(String username) throws ApsSystemException {
		UserDetails user = null;
		try {
			user = this.getUserDAO().loadUser(username);
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading user", t);
		}
		this.setUserCredentialCheckParams(user);
		return user;
	}

	/**
	 * Recupera un'user caricandolo da db. Se userName e password non
	 * corrispondono ad un utente, restituisce null.
	 *
	 * @param username Lo username dell'utente da restituire.
	 * @param password La password dell'utente da restituire.
	 * @return L'utente cercato, null se non vi e' nessun utente corrispondente
	 * alla username e password immessa.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	@Override
	public UserDetails getUser(String username, String password) throws ApsSystemException {
		UserDetails user = null;
		try {
			user = this.getUserDAO().loadUser(username, password);
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading user", t);
		}
		this.setUserCredentialCheckParams(user);
		return user;
	}

	/**
	 * Inserisce nell'utenza le informazioni necessarie per la verifica della
	 * validita' delle credenziali. In particolare, in base allo stato del Modulo
	 * Privacy (attivo oppure no), inserisce le informazioni riguardo il numero
	 * massimo di mesi consentiti dall' ultimo accesso e il numero massimo di mesi
	 * consentiti dall'ultimo cambio password (parametri estratti dalla
	 * configurazioni di sistema).
	 *
	 * @param user L'utenza sulla quale inserire le informazioni necessarie per la
	 * verifica della validita' delle credenziali.
	 */
	protected void setUserCredentialCheckParams(UserDetails user) {
		if (null != user && user.isJapsUser()) {
			User japsUser = (User) user;
			japsUser.setCheckCredentials(this.isEnabledPrivacyModule());
			if (this.isEnabledPrivacyModule()) {
				int maxMonthsSinceLastAccess = this.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_LAST_ACCESS, 6);
				japsUser.setMaxMonthsSinceLastAccess(maxMonthsSinceLastAccess);
				int maxMonthsSinceLastPasswordChange = this.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_LAST_PASSWORD_CHANGE, 3);
				japsUser.setMaxMonthsSinceLastPasswordChange(maxMonthsSinceLastPasswordChange);
			}
		}
	}

	@Override
	public int extractNumberParamValue(String paramName, int defaultValue) {
		String parValue = this.getConfigManager().getParam(paramName);
		int value = 0;
		try {
			value = Integer.parseInt(parValue);
		} catch (NumberFormatException e) {
			value = defaultValue;
		}
		return value;
	}

	/**
	 * Restituisce l'utente di default di sistema. L'utente di default rappresenta
	 * un utente "ospite" senza nessuna autorizzazione di accesso ad elementi non
	 * "liberi" e senza nessuna autorizzazione ad eseguire qualunque azione sugli
	 * elementi del sistema.
	 *
	 * @return L'utente di default di sistema.
	 */
	@Override
	public UserDetails getGuestUser() {
		User user = new User();
		user.setUsername(SystemConstants.GUEST_USER_NAME);
		return user;
	}

	@Override
	public List<UserDetails> getGroupUtilizers(String groupName) throws ApsSystemException {
		List<String> usernames = null;
		List<UserDetails> utilizers = new ArrayList<UserDetails>();
		try {
			usernames = this.getUserDAO().loadUsernamesForGroup(groupName);
			if (usernames != null) {
				for (int i = 0; i < usernames.size(); i++) {
					String username = usernames.get(i);
					UserDetails user = this.getUser(username);
					if (null != user) {
						utilizers.add(user);
					} else {
						ApsSystemUtils.getLogger().info("Searching for the references of the group '" + groupName
										+ "' - The username '" + username + "'referenced does not correspond to any valid user."
										+ "Deleting reference!");
						this.getUserDAO().deleteUser(username);
					}
				}
			}
		} catch (ApsSystemException t) {
			throw new ApsSystemException("Error while loading the members of the group " + groupName, t);
		}
		return utilizers;
	}
	
	@Override
	public void setUserCrc(String username, String crc) throws ApsSystemException {
		try {
			this.getUserDAO().setUserCrc(username, crc);
		} catch (Throwable t) {
			throw new ApsSystemException("Error updating the user crc", t);
		}
		
	}
	
	public String calculateUserCrc(String username, String password) throws ApsSystemException{
		String text = username + SystemConstants.CRC_USER_DELIMITER_CHAR + password;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(text.getBytes());
			StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
		} catch (Exception e) {
			throw new ApsSystemException("Error in calculating crc for user" + username, e);
		}
	}
	
	private boolean isAdmin(UserDetails user) throws ApsSystemException{
		try {
			List<UserDetails> utentiAdmin = this.getGroupUtilizers(Group.ADMINS_GROUP_NAME);
			for(UserDetails u : utentiAdmin){
				if(u.getUsername().equals(user.getUsername())){
					return true;
				}
			}
			
		} catch (Exception e) {
			throw new ApsSystemException("Error in check if user is admin " + user.getUsername(), e);
		}
		return false;
	}
	
	public void setDelegateUser(String username, String delegateuser) throws ApsSystemException {
		UserDetails user = this.getUser(username);
		if (user == null || !user.isJapsUser()) {
			return;
		}
		try {
			this.getUserDAO().updateDelegateUserImpresa(user.getUsername(), delegateuser);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setDelegateUser");
			throw new ApsSystemException("Error while refreshing the delegateuser of the User " + user.getUsername(), t);
		}
	}
	
	/** 
	 * estrae le caratteristiche della password
	 * @param pwd
	 */
	private void getPasswordInfo(String pwd) {
		// Le regole tradizionali per la costruzione di una strong password 
		// secondo policy compliant gdpr, sono:
		//  - lunghezza >= 8 (suggerita >= 16/20)
		//  - nuova password != vecchia password
		//  - non usare "dictionary words"
		//  - non usare info personali (children names/pets, birth place, ...) 
		//  - almeno 1 carattere per ogni categoria (numeri, maiuscole, minuscole, speciali)
		//  - suggerito l'uso di "pass-phrase" suggested (almeno 4 parole non correlate)
		//  - (per SPID) non deve contenere piu' di 2 caratteri consecutivi uguali
		this.error = null;
	    this.countUpperChars = 0;
	    this.countLowerChars = 0;
	    this.countNumberChars = 0;
	    this.countSpecialChars = 0;
	    this.countMaxConsecutiveEqualChars = 0;
	    char last = 0;
	    int consecutive = 0;
	    for(int i = 0; i < pwd.length(); i++) {
	        char ch = pwd.charAt(i);
	        if(Character.isDigit(ch)) { 
	        	this.countNumberChars++;
	        } else if (Character.isUpperCase(ch)) {
	        	this.countUpperChars++;
	        } else if (Character.isLowerCase(ch)) {
	        	this.countLowerChars++;
	        } else if (SPECIAL_CHARS.indexOf(ch) >= 0) {
	        	this.countSpecialChars++;
	        } 
	        
	        // conteggio caratteri uguali consecutivi
	        if (ch == last) {	
	        	consecutive++;
	        	if(consecutive > this.countMaxConsecutiveEqualChars) {
	        		this.countMaxConsecutiveEqualChars = consecutive;
	        	}
	        } else {
	        	consecutive = 1;
	        }
	        last = ch;
	    }
	}
	
	/** 
	 * valida la password per un utente di tipo amministratore
	 * 
	 * policy per la password di un utente amministratore  
     *  - caratteri ammessi a-zA-Z0-9._&$@!- 
     *  - lunghezza minima 14 e massima 20
     *  - almeno 1 cifra 
	 *  - almeno 1 carattere maiuscolo
	 *  - almeno 1 carattere minuscolo
	 *  - almeno 1 carattere speciale
	 *  - non piu' di 2 carattereri uguali consecutivi
     */
	private void validateAdminPassword(String pwd) {
		this.getPasswordInfo(pwd);
		
		// [a-zA-Z0-9_.&$@!-]{14,20}
		if( !pwd.matches("^[a-zA-Z0-9" + SPECIAL_CHARS + "]+$") ) {
			this.error = "wrongCharactersAdmin"; 
		}
		// lunghezza compresa tra 14 e 20
		if(pwd.length() < ADMIN_MIN_PASSWORD_LEN || pwd.length() > ADMIN_MAX_PASSWORD_LEN) {
			this.error = "passwordlength";
		}
		
		boolean wrongSintax = false;
		// almeno 1 carattere maiuscolo
		if(this.countUpperChars < 1) {
			wrongSintax = true;
		}
		// almeno 1 carattere minuscolo
		if(this.countLowerChars < 1) {
			wrongSintax = true;
		}
		// almeno 1 cifra 
		if(this.countNumberChars < 1) {
			wrongSintax = true;
		}
		// almeno 1 carattere speciale
		if(this.countSpecialChars < 1) {
			wrongSintax = true;
		}
		// non piu' di 2 caratteri consecutivi uguali
		if(this.countMaxConsecutiveEqualChars > 2) {
			wrongSintax = true;
		}
		
		if(wrongSintax) {
			this.error = "wrongSintaxAdmin";
		}
	}
	
	/** 
	 * valida la password per un utente generico
	 * 	   
	 * policy per la password di un utente standard  
     *  - caratteri ammessi a-zA-Z0-9(caratteri speciali) 
     *  - lunghezza minima 8 e massima 20
     *  - almeno 2 cifre
	 *  - almeno 1 carattere maiuscolo
	 *  - almeno 1 carattere minuscolo
	 *  - almeno 1 carattere speciale
	 *  - non piu' di 2 carattereri uguali consecutivi
	 */
	private void validateUserPassword(String pwd) {
		this.getPasswordInfo(pwd);
		
		// [a-zA-Z0-9_.&$@!-]{8,20}
		if( !pwd.matches("^[a-zA-Z0-9" + SPECIAL_CHARS + "]+$") ) {
			this.error =  "wrongCharacters";
		}
		// lunghezza compresa tra 8 e 20
		if(pwd.length() < USER_MIN_PASSWORD_LEN || pwd.length() > USER_MAX_PASSWORD_LEN) {
			this.error = "passwordlength";
		}
		
		boolean wrongSintax = false;
		// almeno 1 carattere maiuscolo
		if(this.countUpperChars < 1) {
			wrongSintax = true;
		}
		// almeno 1 carattere minuscolo
		if(this.countLowerChars < 1) {
			wrongSintax = true;
		}
		// almeno 2 cifra (o e' sufficiente 1 ???) 
		if(this.countNumberChars < 2) {
			wrongSintax = true;
		}
		// almeno 1 carattere speciale
		if(this.countSpecialChars < 1) {
			wrongSintax = true;
		}
		// non piu' di 2 caratteri consecutivi uguali
		if(this.countMaxConsecutiveEqualChars > 2) {
			wrongSintax = true;
		}
		
		if(wrongSintax) {
			this.error = "wrongSintaxAdmin";
		}
	}

	/**
	 * valida la password in base alla policy corrente
	 * 
	 * @throws ApsSystemException 
	 */
	public String validatePassword(UserDetails user, String password) throws ApsSystemException {
		if(this.isAdmin(user)) {
			this.validateAdminPassword(password);
		} else {
			this.validateUserPassword(password);
		}
		return this.error;
	}

}
