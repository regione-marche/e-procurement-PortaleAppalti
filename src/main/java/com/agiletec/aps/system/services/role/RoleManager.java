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
package com.agiletec.aps.system.services.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IApsAuthority;
import com.agiletec.aps.system.services.authorization.authorizator.AbstractApsAutorityManager;
import com.agiletec.aps.system.services.authorization.authorizator.IApsAuthorityDAO;

/**
 * Servizio di gestione dei ruoli.
 * @author 
 */
public class RoleManager extends AbstractApsAutorityManager implements IRoleManager {

	public void init() throws Exception {
		this.loadAuthConfiguration();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": initialized " + this._roles.size() + " roles and "
				+ this._permissions.size() + " permissions.");
	}

	private void loadAuthConfiguration() throws ApsSystemException {
		try {
			this._roles = this.getRoleDAO().loadRoles();
			this._permissions = this.getPermissionDAO().loadPermissions();
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading roles and permissions", t);
		}
	}

	/**
	 * Restituisce la lista dei ruoli esistenti.
	 * @return La lista dei ruoli esistenti.
	 */
	public List<Role> getRoles() {
		List<Role> roles = new ArrayList<Role>(this._roles.values());
		return roles;
	}

	/**
	 * Restituisce un ruolo in base al nome.
	 * @param roleName Il nome del ruolo richesto.
	 * @return Il ruolo cercato.
	 */
	public Role getRole(String roleName) {
		return (Role) this._roles.get(roleName);
	}

	/**
	 * Rimuove un ruolo dal db e dalla mappa dei ruoli.
	 * @param role Oggetto di tipo Role relativo al ruolo da rimuovere.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public void removeRole(Role role) throws ApsSystemException {
		try {
			this.getRoleDAO().deleteRole(role);
			_roles.remove(role.getName());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeRole");
			throw new ApsSystemException("Error while removing a role", t);
		}
	}

	/**
	 * Aggiorna un ruolo sul db ed sulla mappa dei ruoli.
	 * @param role Il ruolo da aggiornare.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public void updateRole(Role role) throws ApsSystemException {
		try {
			this.getRoleDAO().updateRole(role);
			_roles.put(role.getName(), role);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateRole");
			throw new ApsSystemException("Error while updating a role", t);
		}
	}


	/**
	 * Aggiunge un ruolo al db ed alla mappa dei ruoli.
	 * @param role Oggetto di tipo Role relativo al ruolo da aggiungere.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public void addRole(Role role) throws ApsSystemException {
		try {
			this.getRoleDAO().addRole(role);
			this._roles.put(role.getName(), role);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addRole");
			throw new ApsSystemException("Error while adding a role", t);
		}
	}

	/**
	 * Restituisce la lista ordinata (secondo il nome) dei permessi di autorizzazione.
	 * @return La lista ordinata dei permessi
	 */
	public List<Permission> getPermissions() {
		List<Permission> permissions = new ArrayList<Permission>(this._permissions.values());
		Collections.sort(permissions);
		return permissions;
	}

	@Override
	public Permission getPermission(String permissionName) {
		return this._permissions.get(permissionName);
	}

	/**
	 * Rimuove il permesso specificato dal db e dai ruoli.
	 * @param permissionName Il permesso da rimuovere dal ruolo.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public void removePermission(String permissionName) throws ApsSystemException {
		try {
			this.getPermissionDAO().deletePermission(permissionName);
			this._permissions.remove(permissionName);
			Iterator<Role> roleIt = _roles.values().iterator();
			while (roleIt.hasNext()) {
				Role role = (Role) roleIt.next();
				role.removePermission(permissionName);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removePermission");
			throw new ApsSystemException("Error while deleting a permission", t);
		}
	}

	/**
	 * Aggiorna un permesso di autorizzazione nel db.
	 * @param permission Il permesso da aggiornare nel db.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public void updatePermission(Permission permission) throws ApsSystemException {
		try {
			this.getPermissionDAO().updatePermission(permission);
			this._permissions.put(permission.getName(), permission);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updatePermission");
			throw new ApsSystemException("Error while updating a role", t);
		}
	}

	/**
	 * Aggiunge un permesso di autorizzazione nel db.
	 * @param permission Il permesso da aggiungere nel db.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public void addPermission(Permission permission) throws ApsSystemException {
		try {
			this.getPermissionDAO().addPermission(permission);
			this._permissions.put(permission.getName(), permission);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addPermission");
			throw new ApsSystemException("Error while adding a permission", t);
		}
	}

	/**
	 * Restituisce il numero di utenti che utilizzano il ruolo immesso.
	 * @param role Il ruolo di cui trovate il numero di utenti che lo utilizzano.
	 * @return Il numero di utenti che utilizzano quel ruolo.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	public int getRoleUses(Role role) throws ApsSystemException {
		int number = 0;
		try {
			number = this.getRoleDAO().getRoleUses(role);
		} catch (Throwable t) {
			throw new ApsSystemException("Error in the number of the role users", t);
		}
		return number;
	}

	public List<Role> getRolesWithPermission(String permissionName) {
		List<Role> rolesWithPerm = new ArrayList<Role>();
		Iterator<Role> iter = this.getRoles().iterator();
		while (iter.hasNext()) {
			Role role = (Role) iter.next();
			if (role.hasPermission(permissionName)) {
				rolesWithPerm.add(role);
			}
		}
		return rolesWithPerm;
	}

	protected IPermissionDAO getPermissionDAO() {
		return permissionDao;
	}
	public void setPermissionDAO(IPermissionDAO permissionDao) {
		this.permissionDao = permissionDao;
	}

	protected IRoleDAO getRoleDAO() {
		return roleDao;
	}
	public void setRoleDAO(IRoleDAO roleDao) {
		this.roleDao = roleDao;
	}

	@Override
	public IApsAuthority getAuthority(String authName) {
		return this.getRole(authName);
	}

	@Override
	protected IApsAuthorityDAO getAuthorizatorDAO() {
		return this.getRoleDAO();
	}

	private Map<String, Role> _roles;
	private Map<String, Permission> _permissions;

	private IRoleDAO roleDao;
	private IPermissionDAO permissionDao;

}
