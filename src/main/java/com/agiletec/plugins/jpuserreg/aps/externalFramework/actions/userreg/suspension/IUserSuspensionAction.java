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
package com.agiletec.plugins.jpuserreg.aps.externalFramework.actions.userreg.suspension;

/**
 * Interface for Struts Action to manage account suspension, user disabled.
 * @author G.Cocco
 */
public interface IUserSuspensionAction {
	
	public String initSuspension();
	
	public String suspend();
	
}
