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
package com.agiletec.aps.system.services.user.jpmail_wrongaccess_config;

import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.services.lang.Lang;

/**
 * @author Marco Perazzetta
 */
public class WrongAccessConfig implements IWrongAccessConfig {

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
	public void setProfileLangAttr(String profileLangAttr) {
		this._profileLangAttr = profileLangAttr;
	}

	@Override
	public String getProfileLangAttr() {
		return _profileLangAttr;
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
	public void setDefaultLang(Lang defaultLang) {
		this._defaultLang = defaultLang;
	}

	@Override
	public Lang getDefaultLang() {
		return _defaultLang;
	}

	@Override
	public String getUserDisabledAdminPageCode() {
		return _userDisabledAdminPageCode;
	}

	@Override
	public void setUserDisabledAdminPageCode(String userDisabledAdminPageCode) {
		_userDisabledAdminPageCode = userDisabledAdminPageCode;
	}

	@Override
	public String getUserDisabledAdminMailSubject(String langCode) {
		String subject = this._userDisabledAdminMailSubject.get(langCode);
		if (null == subject || subject.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			subject = this._userDisabledAdminMailSubject.get(defaultLangCode);
		}
		return subject;
	}

	@Override
	public void addUserDisabledAdminMailSubject(String langCode, String userDisabledAdminMailSubject) {
		this._userDisabledAdminMailSubject.put(langCode, userDisabledAdminMailSubject);
	}

	@Override
	public String getUserDisabledAdminMailBody(String langCode) {
		String body = this._userDisabledAdminMailBody.get(langCode);
		if (null == body || body.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			body = this._userDisabledAdminMailBody.get(defaultLangCode);
		}
		return body;
	}

	@Override
	public void addUserDisabledAdminMailBody(String langCode, String userDisabledAdminMailBody) {
		this._userDisabledAdminMailBody.put(langCode, userDisabledAdminMailBody);
	}

	@Override
	public String getUserDisabledUserPageCode() {
		return _userDisabledUserPageCode;
	}

	@Override
	public void setUserDisabledUserPageCode(String userDisabledUserPageCode) {
		_userDisabledUserPageCode = userDisabledUserPageCode;
	}

	@Override
	public String getUserDisabledUserMailSubject(String langCode) {
		String subject = this._userDisabledUserMailSubject.get(langCode);
		if (null == subject || subject.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			subject = this._userDisabledUserMailSubject.get(defaultLangCode);
		}
		return subject;
	}

	@Override
	public void addUserDisabledUserMailSubject(String langCode, String userDisabledUserMailSubject) {
		this._userDisabledUserMailSubject.put(langCode, userDisabledUserMailSubject);
	}

	@Override
	public String getUserDisabledUserMailBody(String langCode) {
		String body = this._userDisabledUserMailBody.get(langCode);
		if (null == body || body.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			body = this._userDisabledUserMailBody.get(defaultLangCode);
		}
		return body;
	}

	@Override
	public void addUserDisabledUserMailBody(String langCode, String userDisabledUserMailBody) {
		this._userDisabledUserMailBody.put(langCode, userDisabledUserMailBody);
	}

	@Override
	public String getIpSuspendedAdminPageCode() {
		return _ipSuspendedAdminPageCode;
	}

	@Override
	public void setIpSuspendedAdminPageCode(String ipSuspendedAdminPageCode) {
		_ipSuspendedAdminPageCode = ipSuspendedAdminPageCode;
	}

	@Override
	public String getIpSuspendedAdminMailSubject(String langCode) {
		String subject = this._ipSuspendedAdminMailSubject.get(langCode);
		if (null == subject || subject.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			subject = this._ipSuspendedAdminMailSubject.get(defaultLangCode);
		}
		return subject;
	}

	@Override
	public void addIpSuspendedAdminMailSubject(String langCode, String ipSuspendedAdminMailSubject) {
		this._ipSuspendedAdminMailSubject.put(langCode, ipSuspendedAdminMailSubject);
	}

	@Override
	public String getIpSuspendedAdminMailBody(String langCode) {
		String body = this._ipSuspendedAdminMailBody.get(langCode);
		if (null == body || body.length() == 0) {
			String defaultLangCode = this.getDefaultLang().getCode();
			body = this._ipSuspendedAdminMailBody.get(defaultLangCode);
		}
		return body;
	}

	@Override
	public void addIpSuspendedAdminMailBody(String langCode, String ipSuspendedAdminMailBody) {
		this._ipSuspendedAdminMailBody.put(langCode, ipSuspendedAdminMailBody);
	}

	private String _profileEMailAttr;
	private String _profileNameAttr;
	private String _profileSurnameAttr;
	private String _profileLangAttr;
	private String _eMailSenderCode;
	private Lang _defaultLang;
	private String _userDisabledAdminPageCode;
	private String _userDisabledUserPageCode;
	private String _ipSuspendedAdminPageCode;
	private Map<String, String> _userDisabledAdminMailSubject = new HashMap<String, String>();
	private Map<String, String> _userDisabledAdminMailBody = new HashMap<String, String>();
	private Map<String, String> _userDisabledUserMailSubject = new HashMap<String, String>();
	private Map<String, String> _userDisabledUserMailBody = new HashMap<String, String>();
	private Map<String, String> _ipSuspendedAdminMailSubject = new HashMap<String, String>();
	private Map<String, String> _ipSuspendedAdminMailBody = new HashMap<String, String>();

}
