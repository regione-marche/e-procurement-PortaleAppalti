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
package test.com.agiletec.aps.system.services.showlettype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.system.services.showlettype.ShowletTypeParameter;

/**
 * @author M.Diana
 */
public class TestShowletTypeManager extends BaseTestCase {
	
    protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testGetShowletTypes() throws ApsSystemException {
		List<ShowletType> list = _showletTypeManager.getShowletTypes();
		Iterator<ShowletType> iter = list.iterator();
		Map<String, String> showletTypes = new HashMap<String, String>();
		while (iter.hasNext()) {
			ShowletType showletType = iter.next();
			showletTypes.put(showletType.getCode(), showletType.getTitles().getProperty("it"));
		}
		boolean containsKey = showletTypes.containsKey("content_viewer_list");
		boolean containsValue = showletTypes.containsValue("Contenuti - Pubblica una Lista di Contenuti");
		assertTrue(containsKey);
		assertTrue(containsValue);
		containsKey = showletTypes.containsKey("content_viewer");
		containsValue = showletTypes.containsValue("Contenuti - Pubblica un Contenuto");
		assertTrue(containsKey);
		assertTrue(containsValue);		
	}
    
    public void testGetShowletType_1() throws ApsSystemException {
    	ShowletType showletType = _showletTypeManager.getShowletType("content_viewer");
		assertEquals("content_viewer", showletType.getCode());
		assertEquals("Contenuti - Pubblica un Contenuto", showletType.getTitles().get("it"));
		assertTrue(showletType.isLocked());
		assertFalse(showletType.isLogic());
		assertFalse(showletType.isUserType());
		assertNull(showletType.getParentType());
		assertNull(showletType.getConfig());
		String action = showletType.getAction();
		assertEquals(action, "viewerConfig");
		List<ShowletTypeParameter> list = showletType.getTypeParameters();
		Iterator<ShowletTypeParameter> iter = list.iterator();
		Map<String, String> parameters = new HashMap<String, String>();
		while (iter.hasNext()) {
			ShowletTypeParameter parameter = (ShowletTypeParameter) iter.next();
			parameters.put(parameter.getName(), parameter.getDescr());
		}
		boolean containsKey = parameters.containsKey("contentId");
		boolean containsValue = parameters.containsValue("Identificativo del Contenuto");
		assertEquals(containsKey, true);
		assertEquals(containsValue, true);
		containsKey = parameters.containsKey("modelId");
		containsValue = parameters.containsValue("Identificativo del Modello di Contenuto");
		assertEquals(containsKey, true);
		assertEquals(containsValue, true);				
	}
    
    public void testGetShowletType_2() throws ApsSystemException {
    	ShowletType showletType = _showletTypeManager.getShowletType("90_events");
		assertEquals("90_events", showletType.getCode());
		assertEquals("Lista contenuti anni '90", showletType.getTitles().get("it"));
		assertFalse(showletType.isLocked());
		assertTrue(showletType.isLogic());
		assertTrue(showletType.isUserType());
		assertNull(showletType.getAction());
		assertNull(showletType.getTypeParameters());
		assertNotNull(showletType.getParentType());
		assertEquals("content_viewer_list", showletType.getParentType().getCode());
		assertNotNull(showletType.getConfig());
		String contentTypeParam = showletType.getConfig().getProperty("contentType");
		assertEquals("EVN", contentTypeParam);
		String filtersParam = showletType.getConfig().getProperty("filters");
		assertTrue(filtersParam.contains("start=01/01/1990"));			
	}
	
    private void init() throws Exception {
		try {
			this._showletTypeManager = (IShowletTypeManager) this.getService(SystemConstants.SHOWLET_TYPE_MANAGER);
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}
    
    private IShowletTypeManager _showletTypeManager = null;
    
}
