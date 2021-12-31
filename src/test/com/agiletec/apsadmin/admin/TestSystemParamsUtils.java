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
package test.com.agiletec.apsadmin.admin;

import java.util.HashMap;
import java.util.Map;

import test.com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.admin.SystemParamsUtils;

/**
 * @version 1.0
 * @author E.Santoboni
 */
public class TestSystemParamsUtils extends ApsAdminBaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testUpdateXmlItemParams() throws Throwable {
		String xmlParams = this._configManager.getConfigItem("params");
		assertNotNull(xmlParams);
		Map<String, String> params = SystemParamsUtils.getParams(xmlParams);
		assertTrue(params.size()>=6);
		assertEquals("homepage", params.get("homePageCode"));
		assertEquals("errorpage", params.get("errorPageCode"));
		
		Map<String, String> newParams = new HashMap<String, String>();
		newParams.put("homePageCode", "home");
		newParams.put("wrongNewParam", "value");
		String newXml = SystemParamsUtils.getNewXmlParams(xmlParams, newParams);
		Map<String, String> newExtractedParams = SystemParamsUtils.getParams(newXml);
		assertTrue(newExtractedParams.size()>=6);
		assertEquals("home", newExtractedParams.get("homePageCode"));
		assertEquals("errorpage", newExtractedParams.get("errorPageCode"));
		assertNull(newExtractedParams.get("wrongNewParam"));
	}
	
	private void init() {
		this._configManager = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
	}
	
	private ConfigInterface _configManager;
	
}