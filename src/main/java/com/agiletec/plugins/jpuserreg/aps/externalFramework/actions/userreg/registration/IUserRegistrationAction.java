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
package com.agiletec.plugins.jpuserreg.aps.externalFramework.actions.userreg.registration;

/**
 * Interface for action to manage User Account Registration Requests
 * 
 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 * */
public interface IUserRegistrationAction {
	
	/**
	 * It Adds user account and profile in to the system, 
	 * keeping disabled status until the end of registration process
	 * */
	public String save();
	
	public static final String SESSION_PARAM_NAME_REQ_PROFILE = "jpuserreg_userRegSessionParam";
	
}