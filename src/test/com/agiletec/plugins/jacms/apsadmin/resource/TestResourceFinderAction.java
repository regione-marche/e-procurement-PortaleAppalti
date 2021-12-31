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
package test.com.agiletec.plugins.jacms.apsadmin.resource;

import java.util.List;

import test.com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.plugins.jacms.apsadmin.resource.ResourceFinderAction;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestResourceFinderAction extends ApsAdminBaseTestCase {
	
	public void testViewImageResources() throws Throwable {
		String result = this.executeShowList("admin", "Image");
		assertEquals(Action.SUCCESS, result);
		ResourceFinderAction action = (ResourceFinderAction) this.getAction();
		String resourceTypeCode = action.getResourceTypeCode();
		assertNotNull(resourceTypeCode);
		assertEquals("Image", resourceTypeCode);
		List<String> resources = action.getResources();
		assertNotNull(resources);
		assertEquals(3, resources.size());
		Category root = ((ResourceFinderAction) action).getCategoryRoot();
		assertNotNull(root);
		assertEquals("Home", root.getTitle());
		assertEquals(3, root.getChildren().length);
	}
	
	public void testViewAttachResources() throws Throwable {
		String result = this.executeShowList("admin", "Attach");
		assertEquals(Action.SUCCESS, result);
		ResourceFinderAction action = (ResourceFinderAction) this.getAction();
		String resourceTypeCode = action.getResourceTypeCode();
		assertNotNull(resourceTypeCode);
		assertEquals("Attach", resourceTypeCode);
		List<String> resources = action.getResources();
		assertNotNull(resources);
		assertEquals(1, resources.size());
		Category root = ((ResourceFinderAction) action).getCategoryRoot();
		assertNotNull(root);
		assertEquals("Home", root.getTitle());
		assertEquals(3, root.getChildren().length);
	}
	
	public void testViewImageResourcesForCustomerUser() throws Throwable {
		String result = this.executeShowList("editorCustomers", "Image");
		assertEquals(Action.SUCCESS, result);
		ResourceFinderAction action = (ResourceFinderAction) this.getAction();
		String resourceTypeCode = action.getResourceTypeCode();
		assertNotNull(resourceTypeCode);
		assertEquals("Image", resourceTypeCode);
		List<String> resources = action.getResources();
		assertNotNull(resources);
		assertEquals(1, resources.size());
		Category root = ((ResourceFinderAction) action).getCategoryRoot();
		assertNotNull(root);
		assertEquals("Home", root.getTitle());
		assertEquals(3, root.getChildren().length);
	}
	
	public void testViewImagesWithUserNotAllowed() throws Throwable {
		String result = this.executeShowList("pageManagerCustomers", "Image");
		assertEquals("userNotAllowed", result);
	}
	
	private String executeShowList(String userName, String resourceTypeCode) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/jacms/Resource", "list");
		this.addParameter("resourceTypeCode", resourceTypeCode);
		String result = this.executeAction();
		return result;
	}
	
	public void testSearchWithWrongParameter() throws Throwable {
		String result = this.executeSearchResource("admin", "Attach", "WrongDescription", "");
		assertEquals(Action.SUCCESS, result);
		ResourceFinderAction action = (ResourceFinderAction) this.getAction();
		List<String> resources = action.getResources();
		assertNotNull(resources);
		assertTrue(resources.isEmpty());
		assertEquals("WrongDescription", action.getText());
		assertEquals("", action.getCategoryCode());
	}
	
	public void testSearchWithNoParameter() throws Throwable {
		String result = this.executeSearchResource("admin", "Attach", "", "");
		assertEquals(Action.SUCCESS, result);
		ResourceFinderAction action = (ResourceFinderAction) this.getAction();
		List<String> resources = action.getResources();
		assertNotNull(resources);
		assertEquals(1, action.getResources().size());
		assertEquals("", action.getText());
		assertEquals("", action.getCategoryCode());
	}
	
	public void testSearchForCategory() throws Throwable {
		String result = this.executeSearchResource("admin", "Image", "", "resCat1");
		assertEquals(Action.SUCCESS, result);
		ResourceFinderAction action = (ResourceFinderAction) this.getAction();
		List<String> resources = action.getResources();
		assertNotNull(resources);
		assertEquals(1, action.getResources().size());
		assertEquals("", action.getText());
		assertEquals("resCat1", action.getCategoryCode());
		
		result = this.executeSearchResource("admin", "Image", "log", "resCat1");
		assertEquals(Action.SUCCESS, result);
		action = (ResourceFinderAction) this.getAction();
		resources = action.getResources();
		assertNotNull(resources);
		assertEquals(1, action.getResources().size());
		
		result = this.executeSearchResource("admin", "Image", "japs", "resCat1");
		assertEquals(Action.SUCCESS, result);
		action = (ResourceFinderAction) this.getAction();
		resources = action.getResources();
		assertNotNull(resources);
		assertTrue(action.getResources().isEmpty());
	}
	
	private String executeSearchResource(String userName, String resourceTypeCode, 
			String text, String categoryCode) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/jacms/Resource", "search");
		this.addParameter("resourceTypeCode", resourceTypeCode);
		this.addParameter("text", text);
		this.addParameter("categoryCode", categoryCode);
		String result = this.executeAction();
		return result;
	}
	
}
