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
package com.agiletec.apsadmin.portal.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.common.tree.TreeNode;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.PageUtilizer;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.portal.AbstractPortalAction;
import com.agiletec.apsadmin.system.TreeNodeBaseActionHelper;

/**
 * Classe Helper per la gestione pagine.
 * @version 1.0
 * @author E.Santoboni
 */
public class PageActionHelper extends TreeNodeBaseActionHelper implements IPageActionHelper {
	
	public List<Group> getAllowedGroups(UserDetails currentUser) {
		return super.getAllowedGroups(currentUser);
	}
	
	public Map getReferencingObjects(IPage page, HttpServletRequest request) throws ApsSystemException {
    	Map<String, List> references = new HashMap<String, List>();
    	try {
    		String[] defNames = ApsWebApplicationUtils.getWebApplicationContext(request).getBeanNamesForType(PageUtilizer.class);
			for (int i=0; i<defNames.length; i++) {
				Object service = null;
				try {
					service = ApsWebApplicationUtils.getWebApplicationContext(request).getBean(defNames[i]);
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "hasReferencingObjects");
					service = null;
				}
				if (service != null) {
					PageUtilizer pageUtilizer = (PageUtilizer) service;
					List utilizers = pageUtilizer.getPageUtilizers(page.getCode());
					if (utilizers != null && !utilizers.isEmpty()) {
						references.put(pageUtilizer.getName()+"Utilizers", utilizers);
					}
				}
			}
    	} catch (Throwable t) {
    		throw new ApsSystemException("Errore in hasReferencingObjects", t);
    	}
    	return references;
    }
	
	/**
	 * Verifica se nella pagina di inserimento nuova pagina viene 
	 * richiesto la specifica (facoltativa) del codice della pagina.
	 * La richiesta o meno del codice pagina è un parametro di configurazione.
	 * @return true se deve essere richiesto il codice pagina (facoltativo), 
	 * false in caso contrario.
	 */
	public boolean isRequiredPageCode() {
		String requirePageCode = this.getConfigService().getParam(SystemConstants.CONFIG_PARAM_REQUIRE_PAGE_CODE);
		boolean isRequirePageCodeEnabled = (null != requirePageCode && requirePageCode.equalsIgnoreCase("true"));
		return isRequirePageCodeEnabled;
	}
	
	@Override
	public ITreeNode getAllowedTreeRoot(UserDetails user) throws ApsSystemException {
		List<String> groupCodes = new ArrayList<String>();
		List<Group> groups = this.getAllowedGroups(user);
		for (int i=0; i<groups.size(); i++) {
			groupCodes.add(groups.get(i).getName());
		}
		return this.getAllowedTreeRoot(groupCodes);
	}
	
	@Override
	public ITreeNode getAllowedTreeRoot(Collection<String> groupCodes) throws ApsSystemException {
		ITreeNode root = null;
		if (groupCodes.contains(Group.ADMINS_GROUP_NAME)) {
			root = this.getRoot();
		} else {
			IPage pageRoot = (IPage) this.getRoot();
			if (groupCodes.contains(Group.FREE_GROUP_NAME)) {
				root = new TreeNode();
				this.fillTreeNode((TreeNode) root, null, this.getRoot());
			} else {
				root = this.getVirtualRoot();
			}
			this.addTreeWrapper((TreeNode) root, null, pageRoot, groupCodes);
		}
		return root;
	}
	
	private void addTreeWrapper(TreeNode currentNode, TreeNode parent, IPage currentTreeNode, Collection<String> groupCodes) {
		IPage[] children = currentTreeNode.getChildren();
		for (int i=0; i<children.length; i++) {
			IPage newCurrentTreeNode = children[i];
			if (this.isUserAllowed(newCurrentTreeNode, groupCodes)) {
				TreeNode newNode = new TreeNode();
				this.fillTreeNode(newNode, currentNode, newCurrentTreeNode);
				currentNode.addChild(newNode);
				this.addTreeWrapper(newNode, currentNode, newCurrentTreeNode, groupCodes);
			} else {
				this.addTreeWrapper(currentNode, currentNode, newCurrentTreeNode, groupCodes);
			}
		}
	}
	
	/**
	 * Metodo a servizio della costruzione dell'albero delle pagine. 
	 * Nel caso che l'utente corrente non sia abilitato alla visualizzazione del nodo 
	 * root, fornisce un nodo "virtuale" nel quale inserire gli eventuali nodi visibili.
	 * @return Il nodo root virtuale.
	 */
	private TreeNode getVirtualRoot() {
		TreeNode virtualRoot = new TreeNode();
		virtualRoot.setCode(AbstractPortalAction.VIRTUAL_ROOT_CODE);
		List<Lang> langs = this.getLangManager().getLangs();
		for (int i=0; i<langs.size(); i++) {
			Lang lang = langs.get(i);
			virtualRoot.setTitle(lang.getCode(), "ROOT");
		}
		return virtualRoot;
	}
	
	private boolean isUserAllowed(IPage page, Collection<String> groupCodes) {
		if (page == null) return false;
		String pageGroup = page.getGroup();
		boolean isAuth = groupCodes.contains(pageGroup) || groupCodes.contains(Group.ADMINS_GROUP_NAME);
		return isAuth;
	}
	
	@Override
	protected ITreeNode getTreeNode(String code) {
		return this.getPageManager().getPage(code);
	}
	
	@Override
	protected ITreeNode getRoot() {
		return (ITreeNode) this.getPageManager().getRoot();
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected ConfigInterface getConfigService() {
		return _configService;
	}
	public void setConfigService(ConfigInterface configService) {
		this._configService = configService;
	}
	
	private IPageManager _pageManager;
	private ConfigInterface _configService;
	
}