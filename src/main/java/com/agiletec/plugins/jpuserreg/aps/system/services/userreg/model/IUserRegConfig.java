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

import java.util.Set;

import com.agiletec.aps.system.services.lang.Lang;

/**
 * TODO
 *
 */
public interface IUserRegConfig {

	public abstract long getTokenValidityMinutes();

	public abstract void setTokenValidityMinutes(long tokenValidityMinutes);

	public abstract String getProfileEMailAttr();

	public abstract void setProfileEMailAttr(String profileEMailAttr);

	public abstract String getProfileNameAttr();

	public abstract void setProfileNameAttr(String profileNameAttr);

	public abstract String getProfileSurnameAttr();

	public abstract void setProfileSurnameAttr(String profileSurnameAttr);

	public abstract String getEMailSenderCode();

	public abstract void setEMailSenderCode(String mailSenderCode);

	public abstract String getActivationPageCode();

	public abstract void setActivationPageCode(String activationPageCode);

	public abstract String getActivationMailSubject(String langCode);

	public abstract void addActivationMailSubject(String langCode,
					String activationMailSubject);

	public abstract String getActivationMailBody(String langCode);

	public abstract void addActivationMailBody(String langCode,
					String activationMailBody);

	public abstract String getReactivationPageCode();

	public abstract void setReactivationPageCode(String reactivationPageCode);

	public abstract String getReactivationMailSubject(String langCode);

	public abstract void addReactivationMailSubject(String langCode,
					String reactivationMailSubject);

	public abstract String getReactivationMailBody(String langCode);

	public abstract void addReactivationMailBody(String langCode,
					String reactivationMailBody);

	public abstract void addRole(String role);

	public abstract void setRoles(Set<String> roles);

	public abstract Set<String> getRoles();

	public abstract void addGroup(String group);

	public abstract void setGroups(Set<String> groups);

	public abstract Set<String> getGroups();

	public abstract void setProfileLangAttr(String profileLangAttr);

	public abstract String getProfileLangAttr();

	public abstract void setDefaultLang(Lang defaultLang);

	public abstract Lang getDefaultLang();

	public abstract String getUserReactivatedPageCode();

	public abstract void setUserReactivatedPageCode(String pageCode);

	public abstract String getUserReactivatedMailSubject(String langCode);

	public abstract void addUserReactivatedMailSubject(String langCode, String mailSubject);

	public abstract String getUserReactivatedMailBody(String langCode);

	public abstract void addUserReactivatedMailBody(String langCode, String mailBody);

}
