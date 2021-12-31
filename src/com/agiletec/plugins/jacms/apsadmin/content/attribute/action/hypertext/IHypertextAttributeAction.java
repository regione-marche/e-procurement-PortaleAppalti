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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.action.hypertext;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.plugins.jacms.apsadmin.content.IContentFinderAction;

/**
 * Interfaccia delle classi action delegate alla gestione dei jAPSLinks (link interni al testo degli attributi Hypertext).
 * @author E.Santoboni
 */
public interface IHypertextAttributeAction extends IContentFinderAction {
	
	/**
	 * Restituisce il nodo root dell'albero delle pagine.
	 * @return La root dell'albero.
	 */
	public ITreeNode getTreeRootNode();
	
}