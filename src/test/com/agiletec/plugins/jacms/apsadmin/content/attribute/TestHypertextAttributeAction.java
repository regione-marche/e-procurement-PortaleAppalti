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
package test.com.agiletec.plugins.jacms.apsadmin.content.attribute;

import java.util.List;

import test.com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.hypertext.IHypertextAttributeAction;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestHypertextAttributeAction extends AbstractBaseTestContentAction {
	
	public void testFindContent_1() throws Throwable {
		this.initTest("admin", "ART1");//Contenuto del gruppo Free
		
		IHypertextAttributeAction action = (IHypertextAttributeAction) this.getAction();
		List<String> contentIds = action.getContents();
		assertEquals(14, contentIds.size());//Contenuti pubblici liberi o non liberi con free gruppo extra
		assertTrue(contentIds.contains("EVN25"));//Contenuto coach abilitato al gruppo free
		assertTrue(contentIds.contains("ART121"));//Contenuto del gruppo "administrators" abilitato al gruppo free
	}
	
	public void testFindContent_2() throws Throwable {
		this.initTest("admin", "ART120");//Contenuto del gruppo degli amministratori
		
		IHypertextAttributeAction action = (IHypertextAttributeAction) this.getAction();
		List<String> contentIds = action.getContents();
		assertEquals(22, contentIds.size());//Tutti i contenuti pubblici
	}
	
	public void testFindContent_3() throws Throwable {
		this.initTest("editorCustomers", "ART102");//Contenuto del gruppo customers
		
		IHypertextAttributeAction action = (IHypertextAttributeAction) this.getAction();
		List<String> contentIds = action.getContents();
		assertEquals(18, contentIds.size());// Contenuti pubblici liberi, o del gruppo customers o altri con customers gruppo extra
		assertTrue(contentIds.contains("ART122"));//Contenuto del gruppo "administrators" abilitato al gruppo customers
		assertTrue(contentIds.contains("ART121"));//Contenuto del gruppo "administrators" abilitato al gruppo free
		assertTrue(contentIds.contains("EVN25"));//Contenuto del gruppo "coach" abilitato al gruppo free
		assertTrue(contentIds.contains("ART111"));//Contenuto del gruppo "coach" abilitato al gruppo customers
	}
	
	public void testFindContent_4() throws Throwable {
		this.initTest("admin", "EVN25");//Contenuto del gruppo coach
		
		IHypertextAttributeAction action = (IHypertextAttributeAction) this.getAction();
		List<String> contentIds = action.getContents();
		assertEquals(18, contentIds.size());// Contenuti pubblici liberi, o del gruppo coach o altri con coach gruppo extra
		assertTrue(contentIds.contains("ART121"));//Contenuto del gruppo "administrators" abilitato al gruppo coach
		assertTrue(contentIds.contains("ART121"));//Contenuto del gruppo "administrators" abilitato al gruppo free
		assertTrue(contentIds.contains("EVN25"));//Contenuto del gruppo "coach" abilitato al gruppo free
		assertTrue(contentIds.contains("ART111"));//Contenuto del gruppo "coach" abilitato al gruppo customers
	}
	
	private void initTest(String currentUserName, String contentId) throws Throwable {
		this.executeEdit(contentId, currentUserName);
		this.initAction("/do/jacms/Content/Hypertext", "configInternalLink");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
	}
	
}