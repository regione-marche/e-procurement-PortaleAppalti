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
import java.util.Map;

import javax.servlet.http.HttpSession;

import test.com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.LinkAttribute;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.link.helper.ILinkAttributeActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestPageLinkAction extends AbstractBaseTestContentAction {

	public void testFailureJoinPageLink_1() throws Throwable {
		this.initJoinLinkTest("ART1", "VediAnche", "it");
		
		this.initAction("/do/jacms/Content/Link", "joinPageLink");
		this.addParameter("linkType", String.valueOf(SymbolicLink.PAGE_TYPE));
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> typeFieldErrors = fieldErrors.get("selectedNode");
		assertEquals(1, typeFieldErrors.size());
	}

	public void testFailureJoinPageLink_2() throws Throwable {
		this.initJoinLinkTest("ART1", "VediAnche", "it");
		
		this.initAction("/do/jacms/Content/Link", "joinPageLink");
		this.addParameter("linkType", String.valueOf(SymbolicLink.PAGE_TYPE));
		this.addParameter("selectedNode", "");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> typeFieldErrors = fieldErrors.get("selectedNode");
		assertEquals(1, typeFieldErrors.size());
	}
	
	public void testFailureJoinPageLink_3() throws Throwable {
		this.initJoinLinkTest("ART1", "VediAnche", "it");
		
		this.initAction("/do/jacms/Content/Link", "joinPageLink");
		this.addParameter("linkType", String.valueOf(SymbolicLink.PAGE_TYPE));
		this.addParameter("selectedNode", "wrongPageCode");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> typeFieldErrors = fieldErrors.get("selectedNode");
		assertEquals(1, typeFieldErrors.size());
	}
	
	public void testJoinPageLink_1() throws Throwable {
		this.initJoinLinkTest("ART1", "VediAnche", "it");
		
		this.initAction("/do/jacms/Content/Link", "joinPageLink");
		this.addParameter("linkType", String.valueOf(SymbolicLink.PAGE_TYPE));
		this.addParameter("selectedNode", "pagina_11");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		Content content = this.getContentOnEdit();
		LinkAttribute attribute = (LinkAttribute) content.getAttribute("VediAnche");
		SymbolicLink symbolicLink = attribute.getSymbolicLink();
		assertNotNull(symbolicLink);
		assertEquals("pagina_11", symbolicLink.getPageDest());
	}
	
	private void initJoinLinkTest(String contentId, String simpleLinkAttributeName, String langCode) throws Throwable {
		this.executeEdit(contentId, "admin");
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, simpleLinkAttributeName);
		session.setAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM, langCode);
	}
	
}