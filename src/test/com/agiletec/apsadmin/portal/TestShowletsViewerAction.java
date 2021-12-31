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

import java.util.List;

import test.com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.portal.AbstractPortalAction;
import com.agiletec.apsadmin.portal.util.ShowletTypeSelectItem;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestShowletsViewerAction extends ApsAdminBaseTestCase {
	
	public void testViewShowlets() throws Throwable {
		String result = this.executeViewShowlets("admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeViewShowlets("pageManagerCustomers");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeViewShowlets("editorCustomers");
		assertEquals("userNotAllowed", result);
		
		result = this.executeViewShowlets(null);
		assertEquals("apslogin", result);
	}
	
	public void testGetShowletFlavours() throws Throwable {
		String result = this.executeViewShowlets("admin");
		assertEquals(Action.SUCCESS, result);
		AbstractPortalAction action = (AbstractPortalAction) this.getAction();
		List<List<ShowletTypeSelectItem>> showletFlavours = action.getShowletFlavours();
		assertNotNull(showletFlavours);
		assertTrue(showletFlavours.size()>=3);
		Lang currentLang = action.getCurrentLang();
		
		List<ShowletTypeSelectItem> stockShowlets = showletFlavours.get(0);
		assertEquals(3, stockShowlets.size());
		ShowletTypeSelectItem stockType = stockShowlets.get(2);
		assertEquals(AbstractPortalAction.STOCK_SHOWLETS_CODE, stockType.getGroupCode());
		if (currentLang.getCode().equals("it")) {
			assertEquals("login_form", stockType.getKey());
			assertEquals("Showlet di Login", stockType.getValue());
		} else {
			assertEquals("messages_system", stockType.getKey());
			assertEquals("System Messages", stockType.getValue());
		}
		
		List<ShowletTypeSelectItem> customShowlets = showletFlavours.get(1);
		assertEquals(1, customShowlets.size());
		ShowletTypeSelectItem customType = customShowlets.get(0);
		assertEquals(AbstractPortalAction.CUSTOM_SHOWLETS_CODE, customType.getGroupCode());
		if (currentLang.getCode().equals("it")) {
			assertEquals("leftmenu", customType.getKey());
			assertEquals("Menu di navigazione verticale", customType.getValue());
		} else {
			assertEquals("leftmenu", customType.getKey());
			assertEquals("Vertical Navigation Menu", customType.getValue());
		}
		
		List<ShowletTypeSelectItem> userShowlets = showletFlavours.get(2);
		assertEquals(2, userShowlets.size());
		ShowletTypeSelectItem userType = userShowlets.get(1);
		assertEquals(AbstractPortalAction.USER_SHOWLETS_CODE, userType.getGroupCode());
		if (currentLang.getCode().equals("it")) {
			assertEquals("logic_type", userType.getKey());
			assertEquals("Tipo logico per test", userType.getValue());
		} else {
			assertEquals("logic_type", userType.getKey());
			assertEquals("Logic type for test", userType.getValue());
		}
	}
	
	private String executeViewShowlets(String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/Showlets", "viewShowlets");
		String result = this.executeAction();
		return result;
	}
	
}