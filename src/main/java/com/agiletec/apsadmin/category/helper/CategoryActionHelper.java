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
package com.agiletec.apsadmin.category.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.CategoryUtilizer;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.TreeNodeBaseActionHelper;

/**
 * This Helper class provides support for categories management.
 * @author E.Santoboni
 */
public class CategoryActionHelper extends TreeNodeBaseActionHelper implements ICategoryActionHelper {
	
	@Override
	public Map getReferencingObjects(Category category, HttpServletRequest request) throws ApsSystemException {
    	Map<String, List> references = new HashMap<String, List>();
    	try {
    		String[] defNames = ApsWebApplicationUtils.getWebApplicationContext(request).getBeanNamesForType(CategoryUtilizer.class);
			for (int i=0; i<defNames.length; i++) {
				Object service = null;
				try {
					service = ApsWebApplicationUtils.getWebApplicationContext(request).getBean(defNames[i]);
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "hasReferencingObjects");
					service = null;
				}
				if (service != null) {
					CategoryUtilizer categoryUtilizer = (CategoryUtilizer) service;
					List utilizers = categoryUtilizer.getCategoryUtilizers(category.getCode());
					if (utilizers != null && !utilizers.isEmpty()) {
						references.put(categoryUtilizer.getName()+"Utilizers", utilizers);
					}
				}
			}
    	} catch (Throwable t) {
    		throw new ApsSystemException("Errore in hasReferencingObjects", t);
    	}
    	return references;
    }
	
	@Override
	public Category buildNewCategory(String code, String parentCode, ApsProperties titles) throws ApsSystemException {
		Category category = new Category();
		try {
			category.setParentCode(parentCode);
			category.setTitles(titles);
			//ricava il codice
			String newCategoryCode = code;
			if (null != newCategoryCode && newCategoryCode.trim().length() > 0) {
				if (newCategoryCode.length() > 0) {
					category.setCode(newCategoryCode);
				}
			}
			if (null == category.getCode()) {
				String defaultTitle = category.getTitles().getProperty(this.getLangManager().getDefaultLang().getCode());
				String categoryCode = this.buildCode(defaultTitle, "category", 25);
				category.setCode(categoryCode);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "buildNewCategory");
			throw new ApsSystemException("Errore in creazione nuova categoria", t);
		}
		return category;
	}
	
	@Override
	public ITreeNode getAllowedTreeRoot(UserDetails user) throws ApsSystemException {
		return this.getRoot();
	}
	
	@Override
	protected ITreeNode getTreeNode(String code) {
		return this.getCategoryManager().getCategory(code);
	}
	
	@Override
	protected ITreeNode getRoot() {
		return (ITreeNode) this.getCategoryManager().getRoot();
	}
	
	protected ICategoryManager getCategoryManager() {
		return _categoryManager;
	}
	public void setCategoryManager(ICategoryManager categoryManager) {
		this._categoryManager = categoryManager;
	}
	
	private ICategoryManager _categoryManager;
	
}
