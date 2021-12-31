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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.apsadmin.portal.helper.IPageActionHelper;

/**
 * Action principale per la gestione dell'albero delle pagine.
 * @version 1.0
 * @author E.Santoboni
 */
public class PageTreeAction extends AbstractPortalAction implements IPageTreeAction {
	
	@Override
	public String moveUp() {
		return this.movePage(true);
	}
	
	@Override
	public String moveDown() {
		return this.movePage(false);
	}
	
	protected String movePage(boolean moveUp) {
		String selectedNode = this.getSelectedNode();
		try {
			String check = this.checkSelectedNode(selectedNode);
			if (null != check) return check;
			IPage currentPage = this.getPageManager().getPage(selectedNode);
			if (!isUserAllowed(currentPage.getParent())) {
				ApsSystemUtils.getLogger().info("Utente corrente non abilitato a spostare la pagina selezionata");
				this.addActionError(this.getText("Message.Pages.userNotAllowed"));
				return SUCCESS;
			}
			if (this.getPageManager().getRoot().getCode().equals(currentPage.getCode())) {
				this.addActionError(this.getText("Message.Pages.movementHome.notAllowed"));
				return SUCCESS;
			}
			boolean result = this.getPageManager().movePage(selectedNode, moveUp);
			if (!result) {
				if (moveUp) {
					this.addActionError(this.getText("Message.Pages.movementUp.notAllowed"));
				} else {
					this.addActionError(this.getText("Message.Pages.movementDown.notAllowed"));
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "movePage");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String copy() {
		String selectedNode = this.getSelectedNode();
		String check = this.checkSelectedNode(selectedNode);
		if (null != check) return check;
		//FIXME RICORDARSI DELL'AREA DI NOTIFICA QUANDO PASSEREMO ALL'ULTIMO LAYER... se è possibile mettere un'indicazione della pagina copiata 
		this.setCopyingPageCode(selectedNode);
		return SUCCESS;
	}
	
	@Override
	@Deprecated
	public IPage getRoot() {
		return this.getPageManager().getRoot();
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
	
	public String getCopyingPageCode() {
		return _copyingPageCode;
	}
	public void setCopyingPageCode(String copyingPageCode) {
		this._copyingPageCode = copyingPageCode;
	}
	
	protected IPageActionHelper getHelper() {
		return _helper;
	}
	public void setHelper(IPageActionHelper helper) {
		this._helper = helper;
	}
	
	private String _copyingPageCode;
	
	private IPageActionHelper _helper;
	
}
