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
package com.agiletec.apsadmin.admin.lang;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * This action class implements the default operations on System Languages.
 * @author E.Santoboni
 */
public class LangAction extends BaseAction implements ILangAction {
	
	@Override
	public String add() {
		try {
			Lang langToAdd = this.getLangManager().getLang(this.getLangCode());
			if (null != langToAdd) {
				//TODO CHIEDERE CHIAVE MESSAGGIO CORRETTO
				String[] args = {this.getLangCode()};
				this.addActionError(this.getText("message.langIsDuplicate", args));
				return INPUT;
			}
			this.getLangManager().addLang(this.getLangCode());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "add");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String remove() {
		try {
			Lang langToRemove = this.getLangManager().getLang(this.getLangCode());
			if (null == langToRemove || langToRemove.isDefault()) {
				//TODO CHIEDERE CHIAVE MESSAGGIO CORRETTO
				this.addActionError(this.getText("message.removingDefaultLang"));
				return INPUT;
			}
			this.getLangManager().removeLang(this.getLangCode());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "remove");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String getLangCode() {
		return _langCode;
	}
	public void setLangCode(String langCode) {
		this._langCode = langCode;
	}
	
	private String _langCode;
	
}