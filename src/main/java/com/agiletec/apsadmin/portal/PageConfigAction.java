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
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.apsadmin.portal.util.ShowletTypeSelectItem;

/**
 * Main action class for the pages configuration.
 * @author E.Santoboni
 */
public class PageConfigAction extends AbstractPortalAction implements IPageConfigAction {
	
	@Override
	public String configure() {
		String pageCode = (this.getSelectedNode() != null ? this.getSelectedNode() : this.getPageCode());
		this.setPageCode(pageCode);
		String check = this.checkSelectedNode(pageCode);
		if (null != check) return check;
		return SUCCESS;
	}
	
	@Override
	public String editFrame() {
		try {
			String result = this.checkBaseParams();
			if (null != result) return result;
			Showlet showlet = this.getCurrentPage().getShowlets()[this.getFrame()];// può essere null
			this.setShowlet(showlet);
			if (showlet != null) {
				ShowletType showletType = showlet.getType();
				ApsSystemUtils.getLogger().trace("pageCode=" + this.getPageCode() 
						+ ", frame=" + this.getFrame() + ", showletCode=" + showletType.getCode());
				this.setShowletAction(showletType.getAction());
				if (null == showletType.getConfig() && null != this.getShowletAction()) {
					return "configureSpecialShowlet";
				}
			} else {
				ApsSystemUtils.getLogger().trace("pageCode=" + this.getPageCode() 
						+ ", frame=" + this.getFrame() + ", Showlet vuota da configurare.");
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "editFrame");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String joinShowlet() {
		Logger log = ApsSystemUtils.getLogger();
		try {
			String result = this.checkBaseParams();
			if (null != result) return result;
			if (null != this.getShowletTypeCode() && this.getShowletTypeCode().length() == 0) {
				this.addActionError(this.getText("Message.Pages.showletTypeCodeUnknown"));
				return INPUT;
			}
			log.trace("code=" + this.getShowletTypeCode() + ", pageCode=" 
					+ this.getPageCode() + ", frame=" + this.getFrame());
			ShowletType showletType = this.getShowletTypeManager().getShowletType(this.getShowletTypeCode());
			if (null == showletType) {
				this.addActionError(this.getText("Message.Pages.showletTypeCodeUnknown"));
				return INPUT;
			}
			if (null == showletType.getConfig() && null != showletType.getAction()) {
				this.setShowletAction(showletType.getAction());
				//continua con la configurazione di showlet
				return "configureSpecialShowlet";
			}
			Showlet showlet = new Showlet();
			showlet.setType(showletType);
			this.getPageManager().joinShowlet(this.getPageCode(), showlet, this.getFrame());
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "joinShowlet");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String removeShowlet() {
		try {
			String result = this.checkBaseParams();
			if (null != result) return result;
			this.getPageManager().removeShowlet(this.getPageCode(), this.getFrame());
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "removeShowlet");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	//TODO METODO COMUNE ALLA CONFIG SPECIAL SHOWLET
	protected String checkBaseParams() {
		Logger log = ApsSystemUtils.getLogger();
		IPage page = this.getPage(this.getPageCode());
		if (!this.isUserAllowed(page)) {
			log.info("Utente corrente non abilitato all'editazione della pagina richiesta");
			this.addActionError(this.getText("Message.userNotAllowed"));
			return "pageTree";
		}
		if (null == page) {
			log.info("Codice della pagina nullo");
			this.addActionError(this.getText("Message.Pages.invalidPageCode"));
			return "pageTree";
		}
		if (this.getFrame() == -1 || this.getFrame() >= page.getShowlets().length) {
			log.info("Identificativo frame richiesto '" + this.getFrame() + "' non corretto");
			this.addActionError(this.getText("Message.Pages.invalidPageFrame"));
			return "pageTree";
		}
		return null;
	}
	
	public List<ShowletType> getShowletTypes() {
		return this.getShowletTypeManager().getShowletTypes();
	}
	
	public List<ShowletTypeSelectItem> getOrderedShowletTypeItems() {
		List<ShowletType> types = this.getShowletTypeManager().getShowletTypes();
		List<ShowletTypeSelectItem> items = new ArrayList<ShowletTypeSelectItem>(types.size());
		for (int i = 0; i < types.size(); i++) {
			ShowletType type = types.get(i);
			String title = super.getTitle(type.getCode(), type.getTitles());
			ShowletTypeSelectItem item = new ShowletTypeSelectItem(type.getCode(), title, type.getPluginCode());
			items.add(item);
		}
		BeanComparator comparator = new BeanComparator("value");
		Collections.sort(items, comparator);
		return items;
	}
	
	public IPage getCurrentPage() {
		return this.getPage(this.getPageCode());
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public int getFrame() {
		return _frame;
	}
	public void setFrame(int frame) {
		this._frame = frame;
	}
	
	public String getShowletAction() {
		return _showletAction;
	}
	public void setShowletAction(String showletAction) {
		this._showletAction = showletAction;
	}
	
	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	public Showlet getShowlet() {
		return _showlet;
	}
	public void setShowlet(Showlet showlet) {
		this._showlet = showlet;
	}
	
	private String _pageCode;
	private int _frame = -1;
	private String _showletAction;
	private String _showletTypeCode;
	
	private IShowletTypeManager _showletTypeManager;
	private Showlet _showlet;
	
}
