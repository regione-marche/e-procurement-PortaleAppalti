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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.ITreeNodeBaseActionHelper;

/**
 * Interface for the helper classes handling the portal pages.
 * @author E.Santoboni
 */
public interface IPageActionHelper extends ITreeNodeBaseActionHelper {
	
	public Map getReferencingObjects(IPage page, HttpServletRequest request) throws ApsSystemException;
	
	public List<Group> getAllowedGroups(UserDetails currentUser);
	
	/**
	 * Check if the page code must by supplied by the user in the input page. This behaviour can
	 * can be easily modified changing the corresponding configuration parameter.
	 *  
	 * @return true if the page code is mandatory, false otherwise
	 */
	public boolean isRequiredPageCode();
	
	/**
	 * Return the root node of the page tree respecting the given permissions. 
	 * @param groupCodes The groups list used when building the page tree.
	 * @return The root of the page tree
	 * @throws ApsSystemException In case of error
	 */
	public ITreeNode getAllowedTreeRoot(Collection<String> groupCodes) throws ApsSystemException;
	
}