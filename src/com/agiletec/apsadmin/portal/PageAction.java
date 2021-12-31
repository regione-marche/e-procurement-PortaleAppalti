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
package com.agiletec.apsadmin.portal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.portal.helper.IPageActionHelper;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseActionHelper;

/**
 * Main action for pages handling
 * @author E.Santoboni
 */
public class PageAction extends AbstractPortalAction implements IPageAction {
	
	@Override
	public void validate() {
		super.validate();
		this.checkCode();
		this.checkTitles();
	}
	
	private void checkTitles() {
		Iterator<Lang> langsIter = this.getLangManager().getLangs().iterator();
		while (langsIter.hasNext()) {
			Lang lang = langsIter.next();
			String titleKey = "lang"+lang.getCode();
			String title = this.getRequest().getParameter(titleKey);
			if (null != title) {
				this.getTitles().put(lang.getCode(), title.trim());
			}
			if (null == title || title.trim().length() == 0) {
				String[] args = {lang.getDescr()};
				this.addFieldError(titleKey, this.getText("Pages.entryPage.insertTitle.maskmsg", args));
			}
		}
	}
	
	private void checkCode() {
		String code = this.getPageCode();
		if ((this.getStrutsAction() == ApsAdminSystemConstants.ADD || 
				this.getStrutsAction() == ApsAdminSystemConstants.PASTE) 
				&& null != code && code.trim().length() > 0) {
			String currectCode = BaseActionHelper.purgeString(code.trim());
			if (currectCode.length() > 0 && null != this.getPageManager().getPage(currectCode)) {
				String[] args = {currectCode};
				this.addFieldError("pageCode", this.getText("Pages.entryPage.duplicateCode.maskmsg", args));
			}
			this.setPageCode(currectCode);
		}
	}
	
	@Override
	public String newPage() {
		String selectedNode = this.getSelectedNode();
		try {
			String check = this.checkSelectedNode(this.getSelectedNode());
			if (null != check) return check;
			IPage parentPage = this.getPageManager().getPage(selectedNode);
			this.valueFormForNew(parentPage);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "newPage");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	protected void valueFormForNew(IPage parentPage) {
		this.setStrutsAction(ApsAdminSystemConstants.ADD);
		this.setParentPageCode(parentPage.getCode());
		this.setGroup(parentPage.getGroup());
		boolean isParentFree = parentPage.getGroup().equals(Group.FREE_GROUP_NAME);
		this.setGroupSelectLock(!(this.isCurrentUserMemberOf(Group.ADMINS_GROUP_NAME) && isParentFree));
		this.setDefaultShowlet(true);
		this.setRequirePageCode(this.getHelper().isRequiredPageCode());
	}
	
	@Override
	public String edit() {
		String pageCode = this.getSelectedNode();
		try {
			String check = this.checkSelectedNode(pageCode);
			if (null != check) return check;
			IPage page = this.getPageManager().getPage(pageCode);
			this.valueFormForEdit(page);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String detail() {
		String pageCode = this.getSelectedNode();
		try {
			String check = this.checkSelectedNode(pageCode);
			if (null != check) return check;
			IPage page = this.getPageManager().getPage(pageCode);
			this.valueFormForEdit(page);
			this.setViewerCondition(pageCode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "detail");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * We must ensure that in the main frame of the current page there is the so called viever showlet with no
	 * configuration at all.
	 */
	private void setViewerCondition(String pageCode) {
		IPage page = this.getPageManager().getPage(pageCode);
		int mainFrame = page.getModel().getMainFrame();
		if (null != page && mainFrame > -1) {
			Showlet viewer = page.getShowlets()[mainFrame];
			if (null != viewer && viewer.getType().getCode().equals(this.getViewerShowletCode())) {
				if (null == viewer.getConfig() || viewer.getConfig().isEmpty()) {
					this.setViewerPage(true);
					return;
				} 
			}
		}
		this.setViewerPage(false);
	}
	
	protected void valueFormForEdit(IPage pageToEdit) {
		this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		this.setParentPageCode(pageToEdit.getParent().getCode());
		this.setPageCode(pageToEdit.getCode());
		this.setTitles(pageToEdit.getTitles());
		this.setGroup(pageToEdit.getGroup());
		this.setGroupSelectLock(true);
		this.setModel(pageToEdit.getModel().getCode());
		this.setShowable(pageToEdit.isShowable());
	}
	
	@Override
	public String paste() {
		String selectedNode = this.getSelectedNode();
		String copyingPageCode = this.getRequest().getParameter("copyingPageCode");
		try {
			String check = this.checkSelectedNode(selectedNode);
			if (null != check) return check;
			if ("".equals(copyingPageCode) || null == this.getPageManager().getPage(copyingPageCode)) {
				this.addActionError(this.getText("Message.Pages.selectPageToCopy"));
				return "pageTree";
			}
			IPage selectedPage = this.getPageManager().getPage(selectedNode);
			IPage copiedPage = this.getPageManager().getPage(copyingPageCode);
			this.setStrutsAction(ApsAdminSystemConstants.PASTE);
			this.setCopyPageCode(copyingPageCode);
			this.setRequirePageCode(this.getHelper().isRequiredPageCode());
			this.setGroup(selectedPage.getGroup());
			this.setModel(copiedPage.getModel().getCode());
			this.setShowable(copiedPage.isShowable());
			this.setParentPageCode(selectedNode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "paste");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String save() {
		Logger log = ApsSystemUtils.getLogger();
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) {
				IPage page = this.getUpdatedPage();
				this.checkViewerPage(page);
				this.getPageManager().updatePage(page);
				log.trace("Aggiornamento pagina " + page.getCode());
			} else {
				IPage page = this.buildNewPage();
				this.checkViewerPage(page);
				this.getPageManager().addPage(page);
				log.trace("Aggiunta nuova pagina");
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private void checkViewerPage(IPage page) {
		int mainFrame = page.getModel().getMainFrame();
		if (this.isViewerPage() && mainFrame>-1) {
			IShowletTypeManager showletTypeManager = (IShowletTypeManager) ApsWebApplicationUtils.getBean(SystemConstants.SHOWLET_TYPE_MANAGER, this.getRequest());
			Showlet viewer = new Showlet();
			viewer.setConfig(new ApsProperties());
			ShowletType type = showletTypeManager.getShowletType(this.getViewerShowletCode());
			if (null == type) {
				throw new RuntimeException("Showlet 'Contenuto Singolo' assente o non valida : Codice " + this.getViewerShowletCode());
			}
			viewer.setType(type);
			Showlet[] showlets = page.getShowlets();
			showlets[mainFrame] = viewer;
		}
	}
	
	protected IPage buildNewPage() throws ApsSystemException {
		Page page = new Page();
		try {
			page.setParent(this.getPageManager().getPage(this.getParentPageCode()));
			page.setGroup(this.getGroup());
			page.setShowable(this.isShowable());
			PageModel pageModel = this.getPageModelManager().getPageModel(this.getModel());
			page.setModel(pageModel);
			if (this.getStrutsAction() == ApsAdminSystemConstants.PASTE) {
				IPage copyPage = this.getPageManager().getPage(this.getCopyPageCode());
				page.setShowlets(copyPage.getShowlets());
			} else {
				if (this.isDefaultShowlet()) {
					this.setDefaultShowlets(page);
				} else {
					page.setShowlets(new Showlet[pageModel.getFrames().length]);
				}
			}
			page.setTitles(this.getTitles());
			
			//ricava il codice
			String newPageCode = this.getPageCode();
			if (null != newPageCode && newPageCode.trim().length() > 0) {
				if (newPageCode.length() > 0) {
					page.setCode(newPageCode);
				}
			}
			if (null == page.getCode()) {
				String pageCode = 
					this.getHelper().buildCode(page.getTitle(this.getLangManager().getDefaultLang().getCode()), "page", 25);
				page.setCode(pageCode);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "buildNewPage");
			throw new ApsSystemException("Errore in costruzione nuova pagina", t);
		}
		return page;
	}
	
	protected IPage getUpdatedPage() throws ApsSystemException {
		Page page = null;
		try {
			page = (Page) this.getPageManager().getPage(this.getPageCode());
			page.setGroup(this.getGroup());
			page.setShowable(this.isShowable());
			if (!page.getModel().getCode().equals(this.getModel())) {
				//Ho cambiato modello e allora cancello tutte le showlets Precedenti
				PageModel model = this.getPageModelManager().getPageModel(this.getModel());
				page.setModel(model);
				page.setShowlets(new Showlet[model.getFrames().length]);
			}
			if (this.isDefaultShowlet()) {
				this.setDefaultShowlets(page);
			}
			page.setTitles(this.getTitles());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUpdatedPage");
			throw new ApsSystemException("Errore in aggiornamento pagina", t);
		}
		return page;
	}
	
	protected void setDefaultShowlets(Page page) {
		Showlet[] defaultShowlets = page.getModel().getDefaultShowlet();
		Showlet[] showlets = new Showlet[defaultShowlets.length];
		for (int i=0; i<defaultShowlets.length; i++) {
			showlets[i] = defaultShowlets[i];
		}
		page.setShowlets(showlets);
	}
	
	@Override
	public String trash() {
		String selectedNode = this.getSelectedNode();
		try {
			String check = this.checkDelete(selectedNode);
			if (null != check) return check;
			IPage currentPage = this.getPageManager().getPage(selectedNode);
			Map references = this.getHelper().getReferencingObjects(currentPage, this.getRequest());
			if (references.size()>0) {
				this.setReferences(references);
				return "references";
			}
			this.setNodeToBeDelete(selectedNode);
			this.setSelectedNode(currentPage.getParent().getCode());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		try {
			String check = this.checkDelete(this.getNodeToBeDelete());
			if (null != check) return check;
			this.getPageManager().deletePage(this.getNodeToBeDelete());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	protected String checkDelete(String selectedNode) {
		String check = this.checkSelectedNode(selectedNode);
		if (null != check) return check;
		IPage currentPage = this.getPageManager().getPage(selectedNode);
		if (this.getPageManager().getRoot().getCode().equals(currentPage.getCode())) {
			this.addActionError(this.getText("Message.Pages.removeHome.notAllowed"));
			return "pageTree";
		} else if (!isUserAllowed(currentPage) || !isUserAllowed(currentPage.getParent())) {
			this.addActionError(this.getText("Message.Pages.remove.notAllowed"));
			return "pageTree";
		} else if (currentPage.getChildren().length != 0) {
			this.addActionError(this.getText("Message.Pages.remove.notAllowed2"));
			return "pageTree";
        }
		return null;
	}
	
	public String getCopyPageCode() {
		return _copyPageCode;
	}
	public void setCopyPageCode(String copyPageCode) {
		this._copyPageCode = copyPageCode;
	}
	public boolean isDefaultShowlet() {
		return _defaultShowlet;
	}
	public void setDefaultShowlet(boolean defaultShowlet) {
		this._defaultShowlet = defaultShowlet;
	}
	public String getGroup() {
		return _group;
	}
	public void setGroup(String group) {
		this._group = group;
	}
	public boolean isGroupSelectLock() {
		return _groupSelectLock;
	}
	public void setGroupSelectLock(boolean groupSelectLock) {
		this._groupSelectLock = groupSelectLock;
	}
	public String getModel() {
		return _model;
	}
	public void setModel(String model) {
		this._model = model;
	}
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	public String getParentPageCode() {
		return _parentPageCode;
	}
	public void setParentPageCode(String parentPageCode) {
		this._parentPageCode = parentPageCode;
	}
	public boolean isRequirePageCode() {
		return _requirePageCode;
	}
	public void setRequirePageCode(boolean requirePageCode) {
		this._requirePageCode = requirePageCode;
	}
	public boolean isShowable() {
		return _showable;
	}
	public void setShowable(boolean showable) {
		this._showable = showable;
	}
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	public ApsProperties getTitles() {
		return _titles;
	}
	public void setTitles(ApsProperties titles) {
		this._titles = titles;
	}
	
	public boolean isViewerPage() {
		return _viewerPage;
	}
	public void setViewerPage(boolean viewerPage) {
		this._viewerPage = viewerPage;
	}
	
	public List<Group> getGroups() { // TODO RINOMINARE IN ALLOWEDGROUPS
		return this.getHelper().getAllowedGroups(this.getCurrentUser());
	}
	
	public List<PageModel> getPageModels() {
		List<PageModel> models = new ArrayList<PageModel>(this.getPageModelManager().getPageModels());
		return models;
	}
	
	public PageModel getPageModel(String code) {
		return this.getPageModelManager().getPageModel(code);
	}
	
	protected String getViewerShowletCode() {
		return _viewerShowletCode;
	}
	public void setViewerShowletCode(String viewerShowletCode) {
		this._viewerShowletCode = viewerShowletCode;
	}
	
	protected IPageActionHelper getHelper() {
		return _helper;
	}
	public void setHelper(IPageActionHelper helper) {
		this._helper = helper;
	}
	
	protected IPageModelManager getPageModelManager() {
		return _pageModelManager;
	}
	public void setPageModelManager(IPageModelManager pageModelManager) {
		this._pageModelManager = pageModelManager;
	}
	
	public Map getReferences() {
		return _references;
	}
	protected void setReferences(Map references) {
		this._references = references;
	}
	
	public String getNodeToBeDelete() {
		return _nodeToBeDelete;
	}
	public void setNodeToBeDelete(String nodeToBeDelete) {
		this._nodeToBeDelete = nodeToBeDelete;
	}
	

	private boolean _requirePageCode;
	private String _pageCode;
	private String _parentPageCode;
	private String _copyPageCode;
	private String _group;
	private boolean _groupSelectLock;
	private String _model;
	private boolean _defaultShowlet = false;
	private ApsProperties _titles = new ApsProperties();
	private boolean _showable = false;
	private int _strutsAction;
	private boolean _viewerPage;
	
	private String _viewerShowletCode;
	
	private IPageModelManager _pageModelManager;
	private IPageActionHelper _helper;
	
	private Map _references;
	
	private String _nodeToBeDelete;
	
}
