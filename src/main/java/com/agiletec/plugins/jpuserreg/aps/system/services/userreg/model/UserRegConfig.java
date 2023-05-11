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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.agiletec.aps.system.services.lang.Lang;

/**
 * Container of plugin config
 *
 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 */
public class UserRegConfig implements IUserRegConfig {

	@Override
	public long getTokenValidityMinutes() {
		return _tokenValidityMinutes;
	}

	@Override
	public void setTokenValidityMinutes(long tokenValidityMinutes) {
		this._tokenValidityMinutes = tokenValidityMinutes;
	}

	@Override
	public String getProfileEMailAttr() {
		return _profileEMailAttr;
	}

	@Override
	public void setProfileEMailAttr(String profileEMailAttr) {
		this._profileEMailAttr = profileEMailAttr;
	}

	@Override
	public String getProfileNameAttr() {
		return _profileNameAttr;
	}

	@Override
	public void setProfileNameAttr(String profileNameAttr) {
		this._profileNameAttr = profileNameAttr;
	}

	@Override
	public String getProfileSurnameAttr() {
		return _profileSurnameAttr;
	}

	@Override
	public void setProfileSurnameAttr(String profileSurnameAttr) {
		this._profileSurnameAttr = profileSurnameAttr;
	}

	@Override
	public String getEMailSenderCode() {
		return _eMailSenderCode;
	}

	@Override
	public void setEMailSenderCode(String mailSenderCode) {
		_eMailSenderCode = mailSenderCode;
	}

	@Override
	public String getActivationPageCode() {
		return _activationPageCode;
	}

	@Override
	public void setActivationPageCode(String activationPageCode) {
		this._activationPageCode = activationPageCode;
	}

	@Override
	public String getActivationMailSubject(String langCode) {
		String subject = this._activationMailSubject.get(langCode);
		if (null == subject || subject.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			subject = this._activationMailSubject.get(defaultLangCode);
		}
		return subject;
	}

	@Override
	public void addActivationMailSubject(String langCode, String activationMailSubject) {
		this._activationMailSubject.put(langCode, activationMailSubject);
	}

	@Override
	public String getActivationMailBody(String langCode) {
		String body = this._activationMailBody.get(langCode);
		if (null == body || body.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			body = this._activationMailBody.get(defaultLangCode);
		}
		return body;
	}

	@Override
	public void addActivationMailBody(String langCode, String activationMailBody) {
		this._activationMailBody.put(langCode, activationMailBody);
	}

	@Override
	public String getReactivationPageCode() {
		return _reactivationPageCode;
	}

	@Override
	public void setReactivationPageCode(String reactivationPageCode) {
		this._reactivationPageCode = reactivationPageCode;
	}

	@Override
	public String getReactivationMailSubject(String langCode) {
		String subject = this._reactivationMailSubject.get(langCode);
		if (null == subject || subject.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			subject = this._reactivationMailSubject.get(defaultLangCode);
		}
		return subject;
	}

	@Override
	public void addReactivationMailSubject(String langCode, String reactivationMailSubject) {
		this._reactivationMailSubject.put(langCode, reactivationMailSubject);
	}

	@Override
	public String getReactivationMailBody(String langCode) {
		String body = this._reactivationMailBody.get(langCode);
		if (null == body || body.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			body = this._reactivationMailBody.get(defaultLangCode);
		}
		return body;
	}

	@Override
	public void addReactivationMailBody(String langCode, String reactivationMailBody) {
		this._reactivationMailBody.put(langCode, reactivationMailBody);
	}

	@Override
	public void addRole(String role) {
		if (null == this._roles) {
			this._roles = new HashSet<String>();
		}
		this._roles.add(role);
	}

	@Override
	public void setRoles(Set<String> roles) {
		this._roles = roles;
	}

	@Override
	public Set<String> getRoles() {
		return _roles;
	}

	@Override
	public void addGroup(String group) {
		if (null == this._groups) {
			this._groups = new HashSet<String>();
		}
		this._groups.add(group);
	}

	@Override
	public void setGroups(Set<String> groups) {
		this._groups = groups;
	}

	@Override
	public Set<String> getGroups() {
		return _groups;
	}

	@Override
	public void setProfileLangAttr(String profileLangAttr) {
		this._profileLangAttr = profileLangAttr;
	}

	@Override
	public String getProfileLangAttr() {
		return _profileLangAttr;
	}

	@Override
	public void setDefaultLang(Lang defaultLang) {
		this._defaultLang = defaultLang;
	}

	@Override
	public Lang getDefaultLang() {
		return _defaultLang;
	}

	@Override
	public String getUserReactivatedPageCode() {
		return _userReactivatedPageCode;
	}

	@Override
	public void setUserReactivatedPageCode(String userReactivatedPageCode) {
		_userReactivatedPageCode = userReactivatedPageCode;
	}

	@Override
	public String getUserReactivatedMailSubject(String langCode) {
		String subject = this._userReactivatedMailSubject.get(langCode);
		if (null == subject || subject.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			subject = this._userReactivatedMailSubject.get(defaultLangCode);
		}
		return subject;
	}

	@Override
	public void addUserReactivatedMailSubject(String langCode, String userReactivatedMailSubject) {
		this._userReactivatedMailSubject.put(langCode, userReactivatedMailSubject);
	}

	@Override
	public String getUserReactivatedMailBody(String langCode) {
		String body = this._userReactivatedMailBody.get(langCode);
		if (null == body || body.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			body = this._userReactivatedMailBody.get(defaultLangCode);
		}
		return body;
	}

	@Override
	public void addUserReactivatedMailBody(String langCode, String userReactivatedMailBody) {
		this._userReactivatedMailBody.put(langCode, userReactivatedMailBody);
	}

	private long _tokenValidityMinutes;
	private String _profileEMailAttr;
	private String _profileNameAttr;
	private String _profileSurnameAttr;
	private String _profileLangAttr;
	private String _eMailSenderCode;
	private String _activationPageCode;
	private String _reactivationPageCode;
	private String _userReactivatedPageCode;
	private Set<String> _roles;
	private Set<String> _groups;
	private Lang _defaultLang;
	private Map<String, String> _activationMailSubject = new HashMap<String, String>();
	private Map<String, String> _activationMailBody = new HashMap<String, String>();
	private Map<String, String> _reactivationMailSubject = new HashMap<String, String>();
	private Map<String, String> _reactivationMailBody = new HashMap<String, String>();
	private Map<String, String> _userReactivatedMailSubject = new HashMap<String, String>();
	private Map<String, String> _userReactivatedMailBody = new HashMap<String, String>();

}
