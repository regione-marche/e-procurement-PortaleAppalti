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
package com.agiletec.apsadmin.admin.localestring;

import java.util.Iterator;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * This base action class implements the default operations for the Localization Strings.
 * @author E.Santoboni
 */
public class LocaleStringAction extends BaseAction implements ILocaleStringAction {
	
	public void validate() {
		super.validate();
		this.checkKey();
		this.checkLabels();
	}
	
	public String newLabel() {
		this.setStrutsAction(ApsAdminSystemConstants.ADD);
		return SUCCESS;
	}
	
	public String edit() {
		try {
			String key = this.getKey();
			ApsProperties labels = (ApsProperties) this.getI18nManager().getLabelGroups().get(key);
			this.setLabels(labels);
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String save() {
		try {
			int strutsAction = this.getStrutsAction();
			if (ApsAdminSystemConstants.ADD==strutsAction) {
				this.getI18nManager().addLabelGroup(this.getKey(), this.getLabels());
			} else if (ApsAdminSystemConstants.EDIT==strutsAction) {
				this.getI18nManager().updateLabelGroup(this.getKey(), this.getLabels());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String delete() {
		try {
			this.getI18nManager().deleteLabelGroup(this.getKey());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private void checkKey() {
		String key = this.getKey();
		if (this.getStrutsAction()==ApsAdminSystemConstants.ADD && null!=key && key.trim().length()>0) {
			String currectKey = key.trim();
			this.setKey(currectKey);
			if (null != this.getI18nManager().getLabelGroups().get(currectKey)) {
				String[] args = {currectKey};
				//	TODO Etichetta chiave con nome duplicato.
				this.addFieldError("key", this.getText("message.keyIsPresent", args));
			}
			this.setKey(currectKey);
		}
	}
	
	private void checkLabels() {
		Iterator<Lang> langsIter = this.getLangManager().getLangs().iterator();
		while (langsIter.hasNext()) {
			Lang lang = langsIter.next();
			String code = lang.getCode();
			String label = this.getRequest().getParameter(code);
			if (null != label && label.trim().length()>0) {
				this.addLabel(code, label);
			} else {
				String[] args = {lang.getDescr()};
				//	TODO Etichetta lingua "X" non valorizzata.
				this.addFieldError(code, this.getText("message.needValue", args));
			}
		}
	}
	
	public List<Lang> getLangs() {
		return this.getLangManager().getLangs();
	}
	
	public String getKey() {
		return _key;
	}
	public void setKey(String key) {
		this._key = key;
	}
	
	public ApsProperties getLabels() {
		return _labels;
	}
	protected void setLabels(ApsProperties labels) {
		this._labels = labels;
	}
	protected void addLabel(String code, String label) {
		this._labels.put(code, label);
	}

	public int getCustomized(String code) {
		return _labels.getCustomized(code);
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	private String _key;
	private ApsProperties _labels = new ApsProperties();
	private int _strutsAction;
	
}