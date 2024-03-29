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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.apsadmin.portal.util.ShowletTypeSelectItem;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * This abstract class contains both the methods and the signatures to handle the portal configuration
 * @author E.Santoboni
 */
public abstract class AbstractPortalAction extends BaseAction {
	
	public List<List<ShowletTypeSelectItem>> getShowletFlavours() {
		List<String> pluginCodes = new ArrayList<String>();
		Map<String, List<ShowletTypeSelectItem>> mapping = this.getShowletFlavoursMapping(pluginCodes);
		List<List<ShowletTypeSelectItem>> group = new ArrayList<List<ShowletTypeSelectItem>>();
		try {
			this.addGroup(STOCK_SHOWLETS_CODE, mapping, group);
			this.addGroup(CUSTOM_SHOWLETS_CODE, mapping, group);
			this.addGroup(USER_SHOWLETS_CODE, mapping, group);
			for (int i = 0; i < pluginCodes.size(); i++) {
				String pluginCode = pluginCodes.get(i);
				this.addGroup(pluginCode, mapping, group);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getShowletFlavours");
			throw new RuntimeException("Errore in estrazione gruppi showlet", t);
		}
		return group;
	}
	
	private Map<String, List<ShowletTypeSelectItem>> getShowletFlavoursMapping(List<String> pluginCodes) {
		Map<String, List<ShowletTypeSelectItem>> mapping = new HashMap<String, List<ShowletTypeSelectItem>>();
		List<ShowletType> types = this.getShowletTypeManager().getShowletTypes();
		for (int i = 0; i < types.size(); i++) {
			ShowletType type = types.get(i);
			String pluginCode = type.getPluginCode();
			if (null != pluginCode && pluginCode.trim().length() > 0) {
				//is a plugin's showlet
				if (!pluginCodes.contains(pluginCode)) {
					pluginCodes.add(pluginCode);
				}
				this.addShowletType(pluginCode, type, mapping);
			} else if (type.isUserType()) {
				//is a user showlet
				this.addShowletType(USER_SHOWLETS_CODE, type, mapping);
			} else {
				//is a core showlet
				if (this.getStockShowletCodes().contains(type.getCode())) {
					this.addShowletType(STOCK_SHOWLETS_CODE, type, mapping);
				} else {
					this.addShowletType(CUSTOM_SHOWLETS_CODE, type, mapping);
				}
			}
		}
		Collections.sort(pluginCodes);
		return mapping;
	}
	
	private void addShowletType(String mapCode, ShowletType type, Map<String, List<ShowletTypeSelectItem>> mapping) {
		List<ShowletTypeSelectItem> showletTypes = mapping.get(mapCode);
		if (null == showletTypes) {
			showletTypes = new ArrayList<ShowletTypeSelectItem>();
			mapping.put(mapCode, showletTypes);
		}
		String title = super.getTitle(type.getCode(), type.getTitles());
		ShowletTypeSelectItem item = new ShowletTypeSelectItem(type.getCode(), title, mapCode);
		showletTypes.add(item);
	}
	
	private void addGroup(String code, Map<String, List<ShowletTypeSelectItem>> mapping, List<List<ShowletTypeSelectItem>> group) {
		List<ShowletTypeSelectItem> singleGroup = mapping.get(code);
		if (null != singleGroup) {
			BeanComparator comparator = new BeanComparator("value");
			Collections.sort(singleGroup, comparator);
			group.add(singleGroup);
		}
	}
	
	/**
	 * Returns the 'bread crumbs' path, with titles in the current language, 
	 * between the root and the given page.
	 * @param pageCode The code of the page being represented in the bread crumbs path.
	 * @param separator The separator used to separate the pages.
	 * @return The bread crumbs path requested.
	 */
	public String getBreadCrumbs(String pageCode, String separator) {
		IPage page = this.getPageManager().getPage(pageCode);
		if (null == page) return "NULL PAGE";
		Lang lang = this.getCurrentLang();
		String breadCrumbs = this.getPageManager().getRoot().getTitle(lang.getCode());
		if (!page.isRoot()) {
			breadCrumbs += separator + page.getFullTitle(lang.getCode(), separator);
		}
		return breadCrumbs;
	}
	
	/**
	 * Returns the 'bread crumbs' targets.
	 * @param pageCode The code of the page being represented in the bread crumbs path.
	 * @return The bread crumbs targets requested.
	 */
	public List<IPage> getBreadCrumbsTargets(String pageCode) {
		IPage page = this.getPageManager().getPage(pageCode);
		if (null == page) return null;
		List<IPage> pages = new ArrayList<IPage>();
		this.getSubBreadCrumbsTargets(pages, page);
		return pages;
	}
	
	private void getSubBreadCrumbsTargets(List<IPage> pages, IPage current) {
		pages.add(0, current);
		IPage parent = current.getParent();
		if (parent != null && !parent.getCode().equals(current.getCode())) {
			this.getSubBreadCrumbsTargets(pages, parent);
		}
	}
	
	/**
	 * Check if the current user can access the specified page.
	 * @param page The page to check against the current user.
	 * @return True if the user has can access the given page, false otherwise.
	 */
	protected boolean isUserAllowed(IPage page) {
		if (page == null) return false;
		String pageGroup = page.getGroup();
		return this.isCurrentUserMemberOf(pageGroup);
	}
	
	protected String checkSelectedNode(String selectedNode) {
		if (null == selectedNode || selectedNode.trim().length() == 0) {
			this.addActionError(this.getText("Message.Pages.noSelection"));
			return "pageTree";
		}
		if (VIRTUAL_ROOT_CODE.equals(selectedNode)) {
			this.addActionError(this.getText("Message.Pages.virtualRootSelected"));
			return "pageTree";
		}
		IPage selectedPage = this.getPageManager().getPage(selectedNode);
		if (null == selectedPage) {
			this.addActionError(this.getText("Message.Pages.selectedPage.null"));
			return "pageTree";
		}
		if (!this.isUserAllowed(selectedPage)) {
			this.addActionError(this.getText("Message.Pages.userNotAllowed"));
			return "pageTree";
		}
		return null;
	}
	
	/**
	 * Return the page given its code.
	 * @param pageCode The code of the requested page.
	 * @return The page associated to the given code, null if the code is unknown.
	 */
	public IPage getPage(String pageCode) {
		return this.getPageManager().getPage(pageCode);
	}
	
	/**
	 * Return the list of the system languages. The default language is placed first.
	 * @return The list of the system languages.
	 */
	public List<Lang> getLangs() {
		return this.getLangManager().getLangs();
	}
	
	/** 
	 * Return the map of the system groups. The map is indexed by the group name.
	 * @return The map containing the system groups.
	 */
	public Map<String, Group> getSystemGroups() {
		return this.getGroupManager().getGroupsMap();
	}
	
	/**
	 * Return the node selected in the tree of pages.
	 * @return The node selected in the tree of pages.
	 */
	public String getSelectedNode() {
		return _selectedNode;
	}
	
	/**
	 * Set a given node in the tree of pages.
	 * @param selectedNode The node selected in the tree of pages.
	 */
	public void setSelectedNode(String selectedNode) {
		this._selectedNode = selectedNode;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	
	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}
	
	protected String getStockShowletCodes() {
		return _stockShowletCodes;
	}
	public void setStockShowletCodes(String stockShowletCodes) {
		this._stockShowletCodes = stockShowletCodes;
	}
	
	private IPageManager _pageManager;
	private IGroupManager _groupManager;
	
	private IShowletTypeManager _showletTypeManager;
	
	private String _stockShowletCodes;
	
	private String _selectedNode;
	
	/**
	 * This is the code of an abstract page which identifies a 'virtual' container of all
	 * the pages which can be viewed by the current page administrator.
	 */
	public static final String VIRTUAL_ROOT_CODE = "VIRTUAL_PAGE_ROOT";
	
	public static final String STOCK_SHOWLETS_CODE = "stockShowletCode";
	public static final String CUSTOM_SHOWLETS_CODE = "customShowletCode";
	public static final String USER_SHOWLETS_CODE = "userShowletCode";
	
}
