/*
*
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
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
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jpuserreg.aps.system.services.userreg;

import java.security.NoSuchAlgorithmException;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.UserRegBean;

/**
 * Basic interface that provides the Account Registration functionalities
 * 
 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 */
public interface IUserRegManager {
	
	/**
	 * Inserting an user inactive waiting for complete registration 
	 * 
	 * @param regAccountBean bean with base information on the user.
	 * @throws ApsSystemException
	 * @throws Exception 
	 */
	public void regAccount(UserRegBean regAccountBean) throws Exception;
	
	/**
	 * Activate user identified by username with password provided
	 * 
	 * @param username
	 * @param password
	 * @param token 
	 * @throws ApsSystemException
	 */
	public void activateUser(String username, String password, String token) throws Exception;
	
	/**
	 * Reactivate user identified by username with password provided
	 * 
	 * @param username
	 * @param password
	 * @param token 
	 * @throws ApsSystemException
	 */
	public void reactivateUser(String username, String password, String token) throws Exception;
	
	/**
	 * Load username from associated ticket if exist
	 * 
	 * @param token
	 * */
	public String getUsernameFromToken(String token);
	
	/**
	 * Manage reactivation request using user id
	 * 
	 * @param username
	 * */
	public void reactivationByUserName(String username) throws ApsSystemException;
	
	/**
	 * Manage reactivation request using user email
	 * 
	 * @param email 
	 * */
	public void reactivationByEmail(String email) throws ApsSystemException;
	
	/**
	 * Deactivate user
	 * 
	 * @param user
	 * */
	public void deactivateUser(UserDetails user) throws ApsSystemException;
	
	public IUserRegConfig getUserRegConfig();
	
	/**
	 * Remove tickets and associated disabled accounts if expired
	 * */
	void clearOldAccountRequests() throws ApsSystemException;
	
	/**
	 * create a reactivation token for a usename
	 * 
	 * @param usenname
	 * */
	public String getReactivationToken(String username) throws ApsSystemException;
	
}