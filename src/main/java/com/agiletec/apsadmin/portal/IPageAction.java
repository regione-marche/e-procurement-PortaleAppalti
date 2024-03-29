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

/**
 * Base interface for those action which handle the pages.
 * @author E.Santoboni
 */
public interface IPageAction {
	
	/**
	 * Create a new page
	 * @return The code describing the result of the operation.
	 */
	public String newPage();
	
	/**
	 * Edit a page.
	 * @return The result code.
	 */
	public String edit();
	
	public String detail();
	
	/**
	 * Paste a page previously copied. 
	 * @return The code describing the result of the operation.
	 */
	public String paste();
	
	/**
	 * Save a page.
	 * @return The code describing the result of the operation.
	 */
	public String save();
	
	/**
	 * Start the deletion operations for the given page.
	 * @return The result code.
	 */
	public String trash();
	
	/**
	 * Delete a page from the system.
	 * @return The result code.
	 */
	public String delete();
	
}
