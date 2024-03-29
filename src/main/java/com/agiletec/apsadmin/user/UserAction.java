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
package com.agiletec.apsadmin.user;

import java.util.Date;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action base delegata alla gestione utenze. L'azione gestisce le operazioni di
 * aggiunta, modifica e salvataggio degli oggetti utente, senza interagire con
 * le autorizzazioni, la cui gestione e' delegata alle funzionalita' apposite.
 *
 * @version 1.0
 * @author E.Santoboni
 */
public class UserAction extends BaseAction implements IUserAction {

	@Override
	public void validate() {
		super.validate();
		if (this.getStrutsAction() == ApsAdminSystemConstants.ADD) {
			this.checkDuplicatedUser();
		} else {
			try {
				if (this.hasActionErrors() || this.hasErrors() || this.hasFieldErrors()) {
					String username = this.getUsername();
					UserDetails user = this.getUserManager().getUser(username);
					this.setUser(user);
				}
			} catch (Throwable t) {
				throw new RuntimeException("Errore in estrazione utente " + this.getUsername(), t);
			}
		}
	}

	/**
	 * Esegue in fase di aggiunta la verifica sulla duplicazione dell'utente.<br
	 * />
	 * Nel caso la verifica risulti negativa aggiunge un fieldError.
	 */
	protected void checkDuplicatedUser() {
		String username = this.getUsername();
		try {
			if (this.existsUser(username)) {
				String[] args = {username};
				//	TODO Label da revisionare
				this.addFieldError("username", this.getText("Message.duplicateUser", args));
			}
		} catch (Throwable e) {
			ApsSystemUtils.getLogger().error("Eccezione in controllo duplicazione UserName " + username);
		}
	}

	@Override
	public String newUser() {
		this.setStrutsAction(ApsAdminSystemConstants.ADD);
		return SUCCESS;
	}

	@Override
	public String edit() {
		this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		try {
			String result = this.checkUserForEdit();
			if (null != result) {
				return result;
			}
			String username = this.getUsername();
			UserDetails user = this.getUserManager().getUser(username);
			if (!user.isJapsUser()) {
				//TODO REVISIONARE LABEL E CREARE REDIRECT GIUSTO
				this.addActionError(this.getText("MESSAGGIO_UTENTE_NON_LOCALE DI JAPS"));
				return "userList";
			}
			this.setActive(!user.isDisabled());
			this.setUser(user);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String save() {
		User user = null;
		boolean userReactivated = false;
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.ADD) {
				user = new User();
				user.setUsername(this.getUsername());
				user.setPassword(this.getPassword());
			} else if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) {
				user = (User) this.getUserManager().getUser(this.getUsername());
				if (null != this.getPassword() && this.getPassword().trim().length() > 0) {
					user.setPassword(this.getPassword());
				}
			}
			userReactivated = user.isDisabled() && this.isActive();
			user.setDisabled(!this.isActive());
			if (this.isReset()) {
				user.setLastAccess(new Date());
				user.setLastPasswordChange(new Date());
			}
			if (this.getStrutsAction() == ApsAdminSystemConstants.ADD) {
				this.getUserManager().addUser(user);
			} else if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) {
				if (null != this.getPassword() && this.getPassword().trim().length() > 0) {
					this.getUserManager().changePassword(this.getUsername(), this.getPassword());
				}
				
				// rileggi la pwd criptata
				User tmp = (User) this.getUserManager().getUser(this.getUsername());
				user.setPassword(tmp.getPassword());
				
				// aggiorna le info dell'utente (con la pwd criptata)
				this.getUserManager().updateUser(user);
			}
			if (userReactivated) {
				this.getUserManager().clearAllWrongAccessAttemptsByUsername(this.getUsername());
			}
		} catch (Exception t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return INPUT;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String trash() {
		try {
			String result = this.checkUserForDelete();
			if (null != result) {
				return result;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String delete() {
		try {
			String result = this.checkUserForDelete();
			if (null != result) {
				return result;
			}
			this.getUserManager().removeUser(this.getUsername());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}

	protected boolean isCurrentUser() {
		UserDetails currentUser = this.getCurrentUser();
		return currentUser.getUsername().equals(this.getUsername());
	}

	/**
	 * Verifica l'esistenza dell'utente.
	 *
	 * @param username Lo username dell'utente da verificare.
	 * @return true in caso positivo, false nel caso l'utente non esista.
	 * @throws Throwable In caso di errore.
	 */
	protected boolean existsUser(String username) throws Throwable {
		boolean exists = (username != null && username.trim().length() >= 0 && this.getUserManager().getUser(username) != null);
		return exists;
	}

	/**
	 * Verifica se l'utente e' un utente locale di jAPS
	 *
	 * @param username Lo username dell'utente da verificare.
	 * @return true in caso positivo, false nel caso contrario.
	 * @throws Throwable In caso di errore.
	 */
	protected boolean isJapsUser(String username) throws Throwable {
		UserDetails user = this.getUserManager().getUser(username);
		return (null != user && user.isJapsUser());
	}

	/**
	 * Esegue i controlli necessari per la modifica di un utente ed imposta gli
	 * eventuali messaggi di errore.
	 *
	 * @return Il codice del risultato in funzione dell'eventuale errore
	 * riscontrato, null se non viene riscontrato nessun errore.
	 * @throws Throwable In caso di errore.
	 */
	protected String checkUserForEdit() throws Throwable {
		if (!this.existsUser(this.getUsername())) {
			//	TODO Label da revisionare
			this.addActionError(this.getText("MESSAGGIO_UTENTE_NON_ESISTENTE"));
			return "userList";
		}
		if (!this.isJapsUser(this.getUsername())) {
			//	TODO Label da revisionare
			this.addActionError(this.getText("MESSAGGIO_UTENTE_NON_LOCALE DI JAPS"));
			return "userList";
		}
		return null;
	}

	/**
	 * Esegue i controlli necessari per la cancellazione di un utente ed imposta
	 * gli opportuni messaggi di errore.
	 *
	 * @return Il codice del risultato in funzione dell'eventuale errore
	 * riscontrato, null se non viene riscontrato nessun errore.
	 * @throws Throwable In caso di errore.
	 */
	protected String checkUserForDelete() throws Throwable {
		if (!this.existsUser(this.getUsername())) {
			//	TODO Label da revisionare
			this.addActionError(this.getText("MESSAGGIO_UTENTE_NON_ESISTENTE"));
			return "userList";
		} else if (SystemConstants.ADMIN_USER_NAME.equals(this.getUsername())) {
			//	TODO Label da revisionare
			this.addActionError(this.getText("MESSAGGIO_RIMOZIONE_UTENTE_ADMIN"));
			return "userList";
		} else if (this.isCurrentUser()) {
			//	TODO Label da revisionare
			this.addActionError(this.getText("MESSAGGIO_RIMOZIONE_UTENTE_CORRENTE"));
			return "userList";
		} else if (!this.isJapsUser(this.getUsername())) {
			//	TODO Label da revisionare
			this.addActionError(this.getText("MESSAGGIO_UTENTE_NON_LOCALE DI JAPS"));
			return "userList";
		}
		return null;
	}

	public int getStrutsAction() {
		return _strutsAction;
	}

	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}

	public String getUsername() {
		return _username;
	}

	public void setUsername(String username) {
		this._username = username;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	public String getPasswordConfirm() {
		return _passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this._passwordConfirm = passwordConfirm;
	}

	public boolean isActive() {
		return _active;
	}

	public void setActive(boolean active) {
		this._active = active;
	}

	public boolean isReset() {
		return _reset;
	}

	public void setReset(boolean reset) {
		this._reset = reset;
	}

	public UserDetails getUser() {
		return _user;
	}

	public void setUser(UserDetails user) {
		this._user = user;
	}

	protected IUserManager getUserManager() {
		return _userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}

	private IUserManager _userManager;

	private int _strutsAction;
	private String _username;
	private String _password;
	private String _passwordConfirm;

	private boolean _active = false;
	private boolean _reset;

	private UserDetails _user;

}
