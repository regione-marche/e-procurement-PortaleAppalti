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
package com.agiletec.aps.system.services.page;

import com.agiletec.aps.system.common.tree.TreeNode;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.pagemodel.PageModel;

/**
 * This is the representation of a portal page
 * @author
 */
public class Page extends TreeNode implements IPage {

	/**
	 * Set the position of the page with regard to its sisters
	 * @param position the position of the page.
	 */
	protected void setPosition(int position) {
		super.setPosition(position);
	}

	/**
	 * Return the related model of page
	 * @return the page model
	 */
	public PageModel getModel() {
		return _model;
	}

	/**
	 * WARNING: This method is for the page manager service only exclusive use
	 * Assign the given page model to the current object
	 * @param pageModel the model of the page to assign
	 */
	public void setModel(PageModel pageModel) {
		this._model = pageModel;
	}

	/**
	 * Return the authorization group this page belongs to
	 * @return the authorization group
	 */
	public String getGroup() {
		return _group;
	}

	/**
	 * Set the authorisation group of this page
	 * @param the group to assign this page to
	 */
	public void setGroup(String group) {
		this._group = group;
	}

	@Override
	public IPage getParent() {
		return (IPage) super.getParent();
	}

	@Override
	public IPage[] getChildren() {
		IPage[] children = new IPage[super.getChildren().length];
		for (int i=0; i<super.getChildren().length; i++) {
			children[i] = (IPage) super.getChildren()[i];
		}
		return children;
	}

	/**
	 * WARING: this method is reserved to the page manager service only.
	 * Return the code of the father of this page. This methods exists only to
	 * simplify the loading of the pages structure, it cannot be used in any other 
	 * circumstance.
	 * @return the code of the higher level page
	 */
	public String getParentCode() {
		return _parentCode;
	}

	/**
	 * WARING: this method is reserved to the page manager service only.
	 * Set the code of the father of this page. This methods exists only to
	 * simplify the loading of the pages structure, it cannot be used in any other 
	 * circumstance.
	 * @param parentCode the code of the higher level page
	 */
	public void setParentCode(String parentCode) {
		this._parentCode = parentCode;
	}

	/**
	 * WARING: this method is reserved to the page manager service only.
	 * This returns a boolean values indicating whether the page is
	 * displayed in the menus or similar.
	 * @return true if the page must be shown in the menu, false otherwise. 
	 */
	public boolean isShowable() {
		return _showable;
	}

	/**
	 * WARING: this method is reserved to the page manager service only.
	 * Toggle the visibility of the current page in the menu or similar.
	 * @param showable a boolean which toggles the visibility on when true, off otherwise.
	 */
	public void setShowable(boolean showable) {
		this._showable = showable;
	}

	/**
	 * Metodo riservato al servizio di gestione pagine.
	 * Imposta un titolo alla pagina
	 * @param lang La lingua del titolo
	 * @param title Il titolo da impostare
	 * @deprecated Use setTitle(String, String)
	 */
	public void setTitle(Lang lang, String title){
		this.setTitle(lang.getCode(), title);
	}

	/**
	 * Restituisce il titolo della pagina nella lingua specificata
	 * @param lang La lingua
	 * @return il titolo della pagina
	 * @deprecated Use getTitle(String)
	 */
	public String getTitle(Lang lang) {
		return this.getTitle(lang.getCode());
	}

	/**
	 * Return the showlets configured in this page.
	 * @return all the showlets of the current page
	 */
	public Showlet[] getShowlets() {
		return _showlets;
	}

	/**
	 * Assign a set of showlets to the current page.
	 * @param the showlets to assign.
	 */
	public void setShowlets(Showlet[] showlets) {
		this._showlets = showlets;
	}

	public String toString() {
		return "Page: " + this.getCode();
	}

	/**
	 * The code of the higher level page
	 */
	private String _parentCode; 

	/**
	 * The authorization group this page belongs to 
	 */
	private String _group;

	/**
	 * The page model associate to the current object
	 */
	private PageModel _model;

	/**
	 * Toggle menu visibility on and off
	 */
	private boolean _showable = false;

	/**
	 * The showlet of the current page
	 */
	private Showlet[] _showlets;

}
