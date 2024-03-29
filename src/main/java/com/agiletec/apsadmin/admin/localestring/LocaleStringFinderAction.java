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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * This action class implements all the needed methods to handle the Localization Strings
 * @author E.Santoboni
 */
public class LocaleStringFinderAction extends BaseAction implements ILocaleStringFinderAction {
	
	public List<String> getLocaleStrings() {
		List<String> labelKeys = new ArrayList<String>();
		try {
			II18nManager i18nManager = this.getI18nManager();
			String text = this.getText();
			String searchOption = this.getSearchOption();
			boolean customized = ("1".equals(this.getCustomized()));
			if ((null==text || text.trim().length()==0) && !customized) {
				labelKeys.addAll(i18nManager.getLabelGroups().keySet());
			} else if (searchOption==null || searchOption.length()==0 || searchOption.equals("all")) {
				labelKeys = i18nManager.searchLabelsKey(text, false, false, null, customized);
			} else if (searchOption.equals("labelkey")) {
				labelKeys = i18nManager.searchLabelsKey(text, true, false, null, customized);
			} else {
				labelKeys = i18nManager.searchLabelsKey(text, false, true, searchOption, customized);
			}
			if (null != labelKeys) {
				Collections.sort(labelKeys);
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "getLocaleStrings");
		}
		return labelKeys;
	}
	
	public List<Lang> getSystemLangs() {
		return this.getLangManager().getLangs();
	}
	
	public Map<String, ApsProperties> getLabels() {
		return this.getI18nManager().getLabelGroups();
	}
	
	public String getText() {
		return _text;
	}
	public void setText(String text) {
		this._text = text;
	}
	
	public String getSearchOption() {
		return _searchOption;
	}
	public void setSearchOption(String searchOption) {
		this._searchOption = searchOption;
	}

	public String getCustomized() {
		return _customized;
	}
	public void setCustomized(String customized) {
		this._customized = customized;
	}

	private String _text;
	private String _searchOption;
	private String _customized;
	
}