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

import com.agiletec.aps.system.services.lang.Lang;

/**
 * @author Marco Perazzetta
 */
public interface IWrongAccessConfig {

	public abstract String getProfileEMailAttr();

	public abstract void setProfileEMailAttr(String profileEMailAttr);

	public abstract String getEMailSenderCode();

	public abstract void setEMailSenderCode(String mailSenderCode);

	public abstract void setDefaultLang(Lang defaultLang);

	public abstract Lang getDefaultLang();

	public abstract String getUserDisabledAdminPageCode();

	public abstract void setUserDisabledAdminPageCode(String pageCode);

	public abstract String getUserDisabledAdminMailSubject(String langCode);

	public abstract void addUserDisabledAdminMailSubject(String langCode, String mailSubject);

	public abstract String getUserDisabledAdminMailBody(String langCode);

	public abstract void addUserDisabledAdminMailBody(String langCode, String mailBody);

	public abstract String getUserDisabledUserPageCode();

	public abstract void setUserDisabledUserPageCode(String pageCode);

	public abstract String getUserDisabledUserMailSubject(String langCode);

	public abstract void addUserDisabledUserMailSubject(String langCode, String mailSubject);

	public abstract String getUserDisabledUserMailBody(String langCode);

	public abstract void addUserDisabledUserMailBody(String langCode, String mailBody);

	public abstract String getIpSuspendedAdminPageCode();

	public abstract void setIpSuspendedAdminPageCode(String pageCode);

	public abstract String getIpSuspendedAdminMailSubject(String langCode);

	public abstract void addIpSuspendedAdminMailSubject(String langCode, String mailSubject);

	public abstract String getIpSuspendedAdminMailBody(String langCode);

	public abstract void addIpSuspendedAdminMailBody(String langCode, String mailBody);

	public abstract String getProfileNameAttr();

	public abstract void setProfileNameAttr(String profileNameAttr);

	public abstract String getProfileSurnameAttr();

	public abstract void setProfileSurnameAttr(String profileSurnameAttr);

	public abstract void setProfileLangAttr(String profileLangAttr);

	public abstract String getProfileLangAttr();

}
