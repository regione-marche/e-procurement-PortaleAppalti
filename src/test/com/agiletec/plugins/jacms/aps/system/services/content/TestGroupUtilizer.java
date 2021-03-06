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
package test.com.agiletec.plugins.jacms.aps.system.services.content;

import java.util.List;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;

public class TestGroupUtilizer extends BaseTestCase {
	public void testGetGroupsUtilizers() throws Throwable {
    	String[] names = this.getApplicationContext().getBeanNamesForType(GroupUtilizer.class);
    	assertTrue(names.length>=4);
    	for (int i=0; i<names.length; i++) {
			GroupUtilizer service = (GroupUtilizer) this.getApplicationContext().getBean(names[i]);
			List utilizers = service.getGroupUtilizers("coach");
			if (names[i].equals(JacmsSystemConstants.CONTENT_MANAGER)) {
				assertEquals(5, utilizers.size());
			} else if (names[i].equals(JacmsSystemConstants.RESOURCE_MANAGER)) {
				assertEquals(0, utilizers.size());
			}
		}
    }
}
