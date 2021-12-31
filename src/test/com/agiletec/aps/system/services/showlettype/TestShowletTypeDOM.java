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

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.showlettype.ShowletTypeDOM;

/**
 * @version 1.0
 * @author M.Diana
 */
public class TestShowletTypeDOM extends BaseTestCase {

    public void testGetAction() throws ApsSystemException {
		String framesXml = "<config>" +
							"<parameter name=\"contentType\">" +
							"Tipo di contenuto (obbligatorio)" +
							"</parameter>" +
							"<parameter name=\"modelId\">" +
							"Modello di contenuto (obbligatorio)" +
							"</parameter>" +
							"<parameter name=\"attributeName\" />" +
							"<parameter name=\"order\" />" +
							"<parameter name=\"start\" />" +
							"<parameter name=\"end\" />" +
							"<action name=\"listViewerConfig\"/>" +
							"</config>";
		ShowletTypeDOM showletTypeDOM = new ShowletTypeDOM(framesXml);
        String action = showletTypeDOM.getAction();
        assertTrue(action.equals("listViewerConfig"));
	}   
			
}
