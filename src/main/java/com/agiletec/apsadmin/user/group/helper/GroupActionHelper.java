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
package com.agiletec.apsadmin.user.group.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseActionHelper;

/**
 * Classe action helper della gestione Gruppi.
 * @version 1.0
 * @author E.Santoboni
 */
public class GroupActionHelper extends BaseActionHelper implements IGroupActionHelper {
	
	@Override
	public Map<String, List<Object>> getReferencingObjects(Group group, HttpServletRequest request) throws ApsSystemException {
		Map<String, List<Object>> references = new HashMap<String, List<Object>>();
    	try {
    		String[] defNames = ApsWebApplicationUtils.getWebApplicationContext(request).getBeanNamesForType(GroupUtilizer.class);
			for (int i=0; i<defNames.length; i++) {
				Object service = null;
				try {
					service = ApsWebApplicationUtils.getWebApplicationContext(request).getBean(defNames[i]);
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "hasReferencingObjects");
					service = null;
				}
				if (service != null) {
					GroupUtilizer groupUtilizer = (GroupUtilizer) service;
					List<Object> utilizers = groupUtilizer.getGroupUtilizers(group.getName());
					if (utilizers != null && !utilizers.isEmpty()) {
						//FIXME UNIFORMARE E STUDIARE CHIAVE IDONEA
						references.put(groupUtilizer.getName()+"Utilizers", utilizers);
					}
				}
			}
    	} catch (Throwable t) {
    		throw new ApsSystemException("Errore in getReferencingObjects", t);
    	}
    	return references;
	}
	
}
