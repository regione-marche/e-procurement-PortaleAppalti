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
package test.com.agiletec.apsadmin.portal;

import java.util.Collection;

import test.com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.apsadmin.portal.IPageTreeAction;
import com.agiletec.apsadmin.portal.PageTreeAction;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestPageTreeAction extends ApsAdminBaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testViewTree() throws Throwable {
		this.initAction("/do/Page", "viewTree");
		this.setUserOnSession("admin");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		IPage root = ((IPageTreeAction)this.getAction()).getRoot();
		assertNotNull(root);
		assertEquals("homepage", root.getCode());
	}
	
	public void testMoveHome() throws Throwable {
		this.initAction("/do/Page", "moveUp");
		this.setUserOnSession("admin");
		this.addParameter("selectedNode", "homepage");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		Collection<String> errors = this.getAction().getActionErrors();
		assertEquals(1, errors.size());
	}
	
	public void testMoveForAdminUser() throws Throwable {
		String pageToMoveCode = "pagina_12";
		String sisterPageCode = "pagina_11";
		IPage pageToMove = _pageManager.getPage(pageToMoveCode);
		IPage sisterPage = _pageManager.getPage(sisterPageCode);
		assertNotNull(pageToMove);
		assertEquals(pageToMove.getPosition(), 2);
		assertNotNull(sisterPage);
		assertEquals(sisterPage.getPosition(), 1);
		
		this.initAction("/do/Page", "moveUp");
		this.setUserOnSession("admin");
		this.addParameter("selectedNode", pageToMoveCode);
		String result = this.executeAction();
		
		assertEquals(Action.SUCCESS, result);
		Collection<String> messages = this.getAction().getActionMessages();
		assertEquals(0, messages.size());
		
		pageToMove = this._pageManager.getPage(pageToMoveCode);
		assertEquals(pageToMove.getPosition(), 1);
		sisterPage = this._pageManager.getPage(sisterPageCode);
		assertEquals(sisterPage.getPosition(), 2);
		
		this.initAction("/do/Page", "moveDown");
		this.setUserOnSession("admin");
		this.addParameter("selectedNode", pageToMoveCode);
		result = this.executeAction();
		
		assertEquals(Action.SUCCESS, result);
		messages = this.getAction().getActionMessages();
		assertEquals(0, messages.size());
		
		pageToMove = _pageManager.getPage(pageToMoveCode);
		assertEquals(pageToMove.getPosition(), 2);
		sisterPage = _pageManager.getPage(sisterPageCode);
		assertEquals(sisterPage.getPosition(), 1);
	}
	
	public void testMoveForCoachUser() throws Throwable {
		String pageToMoveCode = "pagina_12";
		this.setUserOnSession("pageManagerCoach");
		this.initAction("/do/Page", "moveDown");
		this.addParameter("selectedNode", pageToMoveCode);
		String result = this.executeAction();
		assertEquals("pageTree", result);
		Collection<String> errors = this.getAction().getActionErrors();
		assertEquals(1, errors.size());
	}
	
	public void testMovementNotAllowed() throws Throwable {
		String pageToMoveCode = "primapagina";
		this.setUserOnSession("admin");
		this.initAction("/do/Page", "moveUp");
		this.addParameter("selectedNode", pageToMoveCode);
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		Collection<String> errors = this.getAction().getActionErrors();
		assertEquals(1, errors.size());
		
		pageToMoveCode = "errorpage";
		this.initAction("/do/Page", "moveDown");
		this.addParameter("selectedNode", pageToMoveCode);
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		errors = this.getAction().getActionErrors();
		assertEquals(1, errors.size());
	}
	
	public void testCopyForAdminUser() throws Throwable {
		String pageToCopy = this._pageManager.getRoot().getCode();
		this.executeCopyPage(pageToCopy, "admin");
		
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		String copyingPageCode = ((PageTreeAction)this.getAction()).getCopyingPageCode();
		assertEquals(pageToCopy, copyingPageCode);
		
		this.executeCopyPage("wrongPageCode", "admin");
		result = this.executeAction();
		assertEquals("pageTree", result);
		PageTreeAction action = (PageTreeAction)this.getAction();
		copyingPageCode = action.getCopyingPageCode();
		assertNull(copyingPageCode);
		assertEquals(1, action.getActionErrors().size());
	}
	
	public void testCopyForCoachUser() throws Throwable {
		String pageToCopy = this._pageManager.getRoot().getCode();
		this.executeCopyPage(pageToCopy, "pageManagerCoach");
		String result = this.executeAction();
		assertEquals("pageTree", result);
		assertEquals(1, this.getAction().getActionErrors().size());
		
		IPage customers_page = _pageManager.getPage("customers_page");
		this.executeCopyPage(customers_page.getCode(), "pageManagerCoach");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		String copyingPageCode = ((PageTreeAction)this.getAction()).getCopyingPageCode();
		assertEquals(customers_page.getCode(), copyingPageCode);
	}
	
	private void executeCopyPage(String pageCodeToCopy, String userName) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/Page", "copy");
		this.addParameter("selectedNode", pageCodeToCopy);
	}
	
	private void init() throws Exception {
    	try {
    		this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
    private IPageManager _pageManager = null;
	
}
