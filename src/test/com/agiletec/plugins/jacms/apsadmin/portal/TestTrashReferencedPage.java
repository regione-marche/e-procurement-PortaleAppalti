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
package test.com.agiletec.plugins.jacms.apsadmin.portal;

import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.apsadmin.portal.PageAction;

import test.com.agiletec.apsadmin.ApsAdminBaseTestCase;

public class TestTrashReferencedPage extends ApsAdminBaseTestCase {

	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testTrashReferencedPage() throws Throwable {
		String result = this.executeTrashPage("pagina_11", "admin");
		assertEquals("references", result);
		PageAction action = (PageAction) this.getAction();
		Map utilizers = action.getReferences();
		assertEquals(1, utilizers.size());
		List contentUtilizers = (List) utilizers.get("jacmsContentManagerUtilizers");
		assertEquals(2, contentUtilizers.size());
	}
	
	private String executeTrashPage(String selectedPageCode, String userName) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/Page", "trash");
		this.addParameter("selectedNode", selectedPageCode);
		String result = this.executeAction();
		return result;
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
