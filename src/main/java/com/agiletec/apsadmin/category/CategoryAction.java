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
package com.agiletec.apsadmin.category;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.category.helper.ICategoryActionHelper;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.system.BaseActionHelper;

/**
 * Action class which handles categories. 
 * @author E.Santoboni - G.Cocco
 */
public class CategoryAction extends BaseAction implements ICategoryAction, ICategoryTreeAction {
	
	//FIXME CODICE DUPLICATO IN VALIDAZIONE PAGINA
	public void validate() {
		super.validate();
		this.checkCode();
		this.checkTitles();
	}
	
	private void checkTitles() {
		Iterator<Lang> langsIter = this.getLangManager().getLangs().iterator();
		while (langsIter.hasNext()) {
			Lang lang = (Lang) langsIter.next();
			String titleKey = "lang"+lang.getCode();
			String title = this.getRequest().getParameter(titleKey);
			if (null != title) {
				this.getTitles().put(lang.getCode(), title.trim());
			}
			if (null == title || title.trim().length() == 0) {
				String[] args = {lang.getDescr()};
				this.addFieldError(titleKey, this.getText("Categories.entryCategory.insertTitle.maskmsg", args));
			}
		}
	}
	
	private void checkCode() {
		String code = this.getCategoryCode();
		if ((this.getStrutsAction() == ApsAdminSystemConstants.ADD || 
				this.getStrutsAction() == ApsAdminSystemConstants.PASTE) 
				&& null != code && code.trim().length() > 0) {
			String currectCode = BaseActionHelper.purgeString(code.trim());
			if (currectCode.length() > 0 && null != this.getCategoryManager().getCategory(currectCode)) {
				String[] args = {currectCode};
				//TODO FARE LABEL DECENTE
				this.addFieldError("categoryCode", this.getText("Categories.entryCategory.duplicateCode.maskmsg", args));
			}
			this.setCategoryCode(currectCode);
		}
	}
	
	@Override
	public String add() {
		String selectedNode = this.getSelectedNode();
		try {
			Category category = this.getCategory(selectedNode);
			if (null == category) {
				ApsSystemUtils.getLogger().info("E' necessario selezionare un nodo");
				//TODO FARE LABEL DECENTE
				this.addActionError(this.getText("Message.Categories.selectCategory"));
				return "categoryTree";
			}
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
			this.setParentCategoryCode(selectedNode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "add");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String edit() {
		String selectedNode = this.getSelectedNode();
		try {
			Category category = this.getCategory(selectedNode);
			if (null == category) {
				ApsSystemUtils.getLogger().info("E' necessario selezionare un nodo");
				//TODO VERIFICARE LABEL
				this.addActionError(this.getText("Message.Categories.selectCategory"));
				return "categoryTree";
			}
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
			this.setParentCategoryCode(category.getParentCode());
			this.setCategoryCode(category.getCode());
			this.setTitles(category.getTitles());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String trash() {
		String selectedNode = this.getSelectedNode();
		try {
			String check = this.chechDelete();
			if (null != check) return check;
			Category categoryToDelete = this.getCategoryManager().getCategory(selectedNode);
			Map references = this.getHelper().getReferencingObjects(categoryToDelete, this.getRequest());
			if (references.size() > 0) {
				this.setReferences(references);
		        return "references";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		String selectedNode = this.getSelectedNode();
		try {
			String check = this.chechDelete();
			if (null != check) return check;
			Category currentCategory = this.getCategory(selectedNode);
			this.getCategoryManager().deleteCategory(selectedNode);
			this.setSelectedNode(currentCategory.getParent().getCode());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * Perform all the needed checks before deleting a category.
	 * When errors are detected a new actionMessaged, containing the appropriate error code and messaged, is created.
	 * @return null if the deletion operation is successful, otherwise the error code
	 */
	protected String chechDelete() {
		Category currentCategory = this.getCategory(this.getSelectedNode());
		if (null == currentCategory) {
			ApsSystemUtils.getLogger().info("E' necessario selezionare un nodo");
			//TODO VERIFICARE LABEL
			this.addActionError(this.getText("Message.Categories.selectCategory"));
			return "categoryTree";
		}
		if (currentCategory.getCode().equals(currentCategory.getParentCode())) {
			ApsSystemUtils.getLogger().info("Non è possibile eliminare la categoria Home");
			//TODO VERIFICARE LABEL
			this.addActionError(this.getText("Message.Categories.homeDelete.notAllowed"));
			return "categoryTree";
		}
		if (currentCategory.getChildren().length != 0) {
			ApsSystemUtils.getLogger().info("Non è possibile eliminare una categoria con figlie");
			//TODO VERIFICARE LABEL
			this.addActionError(this.getText("Message.Categories.deleteWithChildren.notAllowed"));
			return "categoryTree";
        }
		return null;
	}
	
	@Override
	public String save() {
		Logger log = ApsSystemUtils.getLogger();
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) {
				Category category = this.getCategory(this.getCategoryCode());
				category.setTitles(this.getTitles());
				this.getCategoryManager().updateCategory(category);
				log.trace("Aggiornamento categoria " + category.getCode());
			} else {
				Category category = this.getHelper().buildNewCategory(this.getCategoryCode(), this.getParentCategoryCode(), this.getTitles());
				this.getCategoryManager().addCategory(category);
				log.trace("Aggiunta nuova categoria");
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public Category getCategory(String categoryCode) {
		return this.getCategoryManager().getCategory(categoryCode);
	}
	
	@Override
	@Deprecated
	public Category getRoot() {
		return this.getCategoryManager().getRoot();
	}
	
	@Override
	public ITreeNode getTreeRootNode() {
		ITreeNode node = null;
		try {
			node = this.getHelper().getAllowedTreeRoot(this.getCurrentUser());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getTreeRootNode");
		}
		return node;
	}
	
	public List<Lang> getLangs() {
		return this.getLangManager().getLangs();
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	public String getCategoryCode() {
		return _categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this._categoryCode = categoryCode;
	}
	
	public String getParentCategoryCode() {
		return _parentCategoryCode;
	}
	public void setParentCategoryCode(String parentCategoryCode) {
		this._parentCategoryCode = parentCategoryCode;
	}
	
	public ApsProperties getTitles() {
		return _titles;
	}
	public void setTitles(ApsProperties titles) {
		this._titles = titles;
	}
	
	protected ICategoryManager getCategoryManager() {
		return _categoryManager;
	}
	
	public void setCategoryManager(ICategoryManager categoryManager) {
		this._categoryManager = categoryManager;
	}
	
	protected ICategoryActionHelper getHelper() {
		return _helper;
	}
	public void setHelper(ICategoryActionHelper helper) {
		this._helper = helper;
	}
	
	public Map getReferences() {
		return _references;
	}
	protected void setReferences(Map references) {
		this._references = references;
	}
	
	public String getSelectedNode() {
		return _selectedNode;
	}
	public void setSelectedNode(String selectedNode) {
		this._selectedNode = selectedNode;
	}
	
	private int _strutsAction;
	private String _categoryCode;
	private String _parentCategoryCode;
	private String _selectedNode;
	private ApsProperties _titles = new ApsProperties();
	
	private ICategoryManager _categoryManager;
	private ICategoryActionHelper _helper;
	
	private Map _references;
	
}
