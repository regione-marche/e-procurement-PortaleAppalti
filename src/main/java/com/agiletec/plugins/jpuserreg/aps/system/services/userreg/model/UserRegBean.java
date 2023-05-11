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
package com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model;

import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;

/**
 * Transfer Object from Struts action to Manager
 * 
 * @author G.Cocco
 * */
public class UserRegBean {
	
	public void setUsername(String username) {
		this._username = username;
	}
	public String getUsername() {
		return _username;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	public String getName() {
		return _name;
	}
	
	public void setSurname(String surname) {
		this._surname = surname;
	}
	public String getSurname() {
		return _surname;
	}
	
	public void setEMail(String eMail) {
		this._eMail = eMail;
	}
	public String getEMail() {
		return _eMail;
	}
	
	public void setUserProfile(IUserProfile userProfile) {
		this._userProfile = userProfile;
	}
	public IUserProfile getUserProfile() {
		return _userProfile;
	}
	
	public void setLang(String lang) {
		this._lang = lang;
	}
	public String getLang() {
		return _lang;
	}
	
	private String _username;
	private String _name;
	private String _surname;
	private String _eMail;
	private String _lang;
	private IUserProfile _userProfile;
	
}