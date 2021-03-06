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
package test.com.agiletec.plugins.jacms.apsadmin.content.attribute;

import java.util.List;

import javax.servlet.http.HttpSession;

import test.com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.resource.ExtendedResourceAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.resource.ResourceAttributeActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestExtendedResourceAction extends AbstractBaseTestContentAction {
	
	public void testNewImageResource_1() throws Throwable {
		this.executeEdit("ART1", "admin");//Contenuto FREE
		
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ResourceAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, "Foto");
		session.setAttribute(ResourceAttributeActionHelper.RESOURCE_TYPE_CODE_SESSION_PARAM, "Image");
		session.setAttribute(ResourceAttributeActionHelper.RESOURCE_LANG_CODE_SESSION_PARAM, "it");
		
		this.initAction("/do/jacms/Content/Resource", "new");
		this.addParameter("resourceTypeCode", "Image");//per replicare il chain in occasione dei chooseResource da edit Contenuto.
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		ExtendedResourceAction action = (ExtendedResourceAction) this.getAction();
		List<Group> allowedGroup = action.getAllowedGroups();
		assertEquals(1, allowedGroup.size());
	}
	
	public void testNewImageResource_2() throws Throwable {
		this.executeEdit("ART102", "admin");//Contenuto customers
		
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ResourceAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, "Foto");
		session.setAttribute(ResourceAttributeActionHelper.RESOURCE_TYPE_CODE_SESSION_PARAM, "Image");
		session.setAttribute(ResourceAttributeActionHelper.RESOURCE_LANG_CODE_SESSION_PARAM, "it");
		
		this.initAction("/do/jacms/Content/Resource", "new");
		this.addParameter("resourceTypeCode", "Image");//per replicare il chain in occasione dei chooseResource da edit Contenuto.
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		ExtendedResourceAction action = (ExtendedResourceAction) this.getAction();
		List<Group> allowedGroup = action.getAllowedGroups();
		assertEquals(2, allowedGroup.size());
	}
	
}