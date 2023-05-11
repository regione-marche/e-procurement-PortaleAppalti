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
package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model.ITempRegImpresaProfile;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interface for Manager of TempRegImpresaProfile Object.
 * 
 * @author Stefano.Sabbadin
 */
public interface IRegImpresaProfileManager {

	/**
	 * Return a UserProfile Prototype. The prototype contains all Attribute
	 * (empty) of the Profile.
	 * 
	 * @param typeCode
	 *            The type of required profile.
	 * @return A UserProfile Prototype.
	 */
	public ITempRegImpresaProfile getProfileType(String typeCode);

	/**
	 * Return a TempRegImpresaProfile by username.
	 * 
	 * @param username
	 *            The username of the profile to return.
	 * @return The UserProfile required.
	 * @throws ApsSystemException
	 *             In case of Exception.
	 */
	public ITempRegImpresaProfile getProfile(String username)
			throws ApsSystemException;

	/**
	 * Add a TempRegImpresaProfile.
	 * 
	 * @param username
	 *            The username of the Profile owner.
	 * @param profile
	 *            The UserProfile to add.
	 * @throws ApsSystemException
	 *             In case of Exception.
	 */
	public void addProfile(String username, ITempRegImpresaProfile profile)
			throws ApsSystemException;

	/**
	 * Delete a TempRegImpresaProfile by username.
	 * 
	 * @param username
	 *            The username of the Profile owner that you must delete the
	 *            profile.
	 * @throws ApsSystemException
	 *             In case of Exception.
	 */
	public void deleteProfile(String username) throws ApsSystemException;

	public static final String PUBLIC_PROFILE_FILTER_KEY = "publicprofile";

}
