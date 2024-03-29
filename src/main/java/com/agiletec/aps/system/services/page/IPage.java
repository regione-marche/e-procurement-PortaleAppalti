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

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.services.pagemodel.PageModel;

/**
 * This class describes a pages of the portal.
 * @author
 */
public interface IPage extends ITreeNode {

	/**
	 * Return the model of the associated page
	 * @return The page model
	 */
	public PageModel getModel();

	/**
	 * Return the authorization group this page belongs to
	 * @return the authorization group
	 */
	public String getGroup();

	/**
	 * WARING: this method is reserved to the page manager service only.
	 * Return the code of the father of this page. This methods exists only to
	 * simplify the loading of the pages structure, it cannot be used in any other 
	 * circumstance.
	 * @return the code of the higher level page
	 */
	public String getParentCode();

	/**
	 * Return the sorted group of the children of the current page, that is the 
	 * pages belonging to the lower level
	 * @return L'array di pagine 
	 */
	public IPage[] getChildren();

	/**
	 * Return the parent of the current page. 
	 * If the current page is the root, the root page itself is returned
	 * @return The father of the current page
	 */
	public IPage getParent();

	/**
	 * WARING: this method is reserved to the page manager service only.
	 * This returns a boolean values indicating whether the page is
	 * displayed in the menus or similar.
	 * @return true if the page must be shown in the menu, false otherwise. 
	 */
	public boolean isShowable();

	/**
	 * Return the showlets configured in this page.
	 * @return all the showlets of the current page
	 */
	public Showlet[] getShowlets();

}
