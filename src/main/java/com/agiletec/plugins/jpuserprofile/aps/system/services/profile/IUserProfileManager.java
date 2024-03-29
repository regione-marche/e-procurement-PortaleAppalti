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
package com.agiletec.plugins.jpuserprofile.aps.system.services.profile;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile;

/**
 * Interface for Manager of UserProfile Object. 
 * @author E.Santoboni
 */
public interface IUserProfileManager {
	
	/**
	 * Return a UserProfile Prototype. 
	 * The prototype contains all Attribute (empty) of the Profile.
	 * @return A UserProfile Prototype.
	 * @deprecated use getDefaultProfileType()
	 */
	public UserProfile getProfileType();
	
	/**
	 * Return a default UserProfile Prototype. 
	 * The prototype contains all Attribute (empty) of the Profile.
	 * @return A UserProfile Prototype.
	 */
	public IUserProfile getDefaultProfileType();
	
	/**
	 * Return a UserProfile Prototype. 
	 * The prototype contains all Attribute (empty) of the Profile.
	 * @param typeCode The type of required profile.
	 * @return A UserProfile Prototype.
	 */
	public IUserProfile getProfileType(String typeCode);
	
	/**
	 * Return a UserProfile by username.
	 * @param username The username of the profile to return.
	 * @return The UserProfile required.
	 * @throws ApsSystemException In case of Exception.
	 */
	public IUserProfile getProfile(String username)	throws ApsSystemException;
	
	/**
	 * Add a UserProfile.
	 * @param username The username of the Profile owner.
	 * @param profile The UserProfile to add.
	 * @throws ApsSystemException In case of Exception.
	 * @deprecated use addProfile(String, IUserProfile)
	 */
	public void addProfile(String username, UserProfile profile) throws ApsSystemException;
	
	/**
	 * Add a UserProfile.
	 * @param username The username of the Profile owner.
	 * @param profile The UserProfile to add.
	 * @throws ApsSystemException In case of Exception.
	 */
	public void addProfile(String username, IUserProfile profile) throws ApsSystemException;
	
	/**
	 * Delete a UserProfile by username.
	 * @param username The username of the Profile owner that you must delete the profile.
	 * @throws ApsSystemException In case of Exception.
	 */
	public void deleteProfile(String username) throws ApsSystemException;
	
	/**
	 * Update a UserProfile.
	 * @param username The username of the user that you must update the profile.
	 * @param profile The profile to update.
	 * @throws ApsSystemException In case of Exception.
	 * @deprecated use updateProfile(String, IUserProfile)
	 */
	public void updateProfile(String username, UserProfile profile) throws ApsSystemException;
	
	/**
	 * Update a UserProfile.
	 * @param username The username of the user that you must update the profile.
	 * @param profile The profile to update.
	 * @throws ApsSystemException In case of Exception.
	 */
	public void updateProfile(String username, IUserProfile profile) throws ApsSystemException;
	
	public static final String PUBLIC_PROFILE_FILTER_KEY = "publicprofile";
	
}