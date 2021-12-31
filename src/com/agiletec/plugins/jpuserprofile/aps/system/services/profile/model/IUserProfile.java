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
package com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model;

import com.agiletec.aps.system.common.entity.model.IApsEntity;

/**
 * Provides core UserProfile information.
 * @author E.Santoboni
 */
public interface IUserProfile extends IApsEntity {
	
	/**
	 * Return of the username that is associated with the profile.
	 * @return The username.
	 */
	public String getUsername();
	
	/**
	 * Returns the value of an attribute identified by his key. 
	 * The value can be of any type.
	 * @param key The key of the attribute.
	 * @return The value of the attribute.
	 */
	public Object getValue(String key);
	
	public String getFirstNameAttributeName();
	
	public String getSurnameAttributeName();
	
	public String getMailAttributeName();
	
	public boolean isPublicProfile();
	
	public void setPublicProfile(boolean isPublic);
	
}