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
package com.agiletec.apsadmin.user.group;

import java.util.List;

import com.agiletec.aps.system.services.group.Group;

/**
 * Interfaccia base per le classi action della lista Gruppi.
 * @version 1.0
 * @author E.Santoboni
 */
public interface IGroupFinderAction {
	
	/**
	 * Restituisce la lista dei gruppi che deve essere erogata dall'interfaccia di 
	 * visualizzazione dei gruppi.
	 * @return La lista di gruppi.
	 */
	public List<Group> getGroups();
	
}