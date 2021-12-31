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
package test.com.agiletec.plugins.jacms.apsadmin.content;

import java.util.List;
import java.util.Map;

import test.com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author E.Santoboni
 */
public class TestIntroNewContentAction extends ApsAdminBaseTestCase {
	
	public void testOpenNew() throws Throwable {
		String result = this.executeOpenNew("admin");
		assertEquals(Action.SUCCESS, result);
		assertNull(this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT));
		
		result = this.executeOpenNew("pageManagerCoach");
		assertEquals("userNotAllowed", result);
	}
	
	private String executeOpenNew(String currentUserName) throws Throwable {
		this.initAction("/do/jacms/Content", "new");
		this.setUserOnSession(currentUserName);
		return this.executeAction();
	}
	
	public void testCreateNewVoid() throws Throwable {
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		String result = this.executeAction();
		assertEquals(1, this.getAction().getFieldErrors().size());
		assertEquals(Action.INPUT, result);
		
		Content content = (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
		assertNull(content);
		
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "Descrizione di prova");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		content = (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
		assertNotNull(content);
		assertEquals("ART", content.getTypeCode());
	}
	
	public void testCreateNewVoid2() throws Throwable {
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
	}
	
	public void testValidateCreateNewVoid() throws Throwable {
		// Validazione descrizione troppo lunga
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "Descrizione che supera la lunghezza massima di cento caratteri; " +
				"Ripeto, Descrizione che supera la lunghezza massima di cento caratteri");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErros = action.getFieldErrors();
		assertEquals(1, fieldErros.size());
		List<String> descrErrors = fieldErros.get("contentDescription");
		assertEquals(1, descrErrors.size());
		String message = descrErrors.get(0);
		assertTrue(message.indexOf("100")>0);
	}
	
}