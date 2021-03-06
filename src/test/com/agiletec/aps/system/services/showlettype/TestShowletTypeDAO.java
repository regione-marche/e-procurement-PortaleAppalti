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

import java.util.Map;

import javax.sql.DataSource;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.system.services.showlettype.ShowletTypeDAO;

/**
 * @version 1.0
 * @author M.Diana
 */
public class TestShowletTypeDAO extends BaseTestCase {
	
    public void testLoadShowletTypes() throws Throwable {
    	DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
    	ShowletTypeDAO showletTypeDao = new ShowletTypeDAO();
    	showletTypeDao.setDataSource(dataSource);
    	Map<String, ShowletType> types = null;
		try {
			types = showletTypeDao.loadShowletTypes();
		} catch (Throwable t) {
            throw t;
        }
		ShowletType showletType = (ShowletType) types.get("content_viewer");
		assertNotNull(showletType);
		showletType = (ShowletType) types.get("content_viewer_list");
		assertNotNull(showletType);
	}    
	
}
