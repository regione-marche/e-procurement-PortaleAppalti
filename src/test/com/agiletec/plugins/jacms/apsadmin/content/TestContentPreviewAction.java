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
package test.com.agiletec.plugins.jacms.apsadmin.content;

import java.util.List;
import java.util.Map;

import test.com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestContentPreviewAction extends AbstractBaseTestContentAction {
	
	public void testPreviewNewContent() throws Throwable {
		String insertedDescr = "XXX Prova preview XXX";
		String result = this.executeCreateNewVoid("ART", insertedDescr, Content.STATUS_DRAFT, "admin");
		assertEquals(Action.SUCCESS, result);
		
		Content content = this.getContentOnEdit();
		ITextAttribute titleAttribute = (ITextAttribute) content.getAttribute("Titolo");
		assertNull(titleAttribute.getTextForLang("it"));
		assertEquals(content.getDescr(), insertedDescr);
		
		this.initAction("/do/jacms/Content", "preview");
		this.addParameter("mainGroup", Group.FREE_GROUP_NAME);
		this.addParameter("it_Titolo", "Nuovo titolo di prova");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		content = this.getContentOnEdit();
		titleAttribute = (ITextAttribute) content.getAttribute("Titolo");
		assertEquals("Nuovo titolo di prova", titleAttribute.getTextForLang("it"));
	}
	
	public void testPreviewContent() throws Throwable {
		String result = this.executeEdit("EVN192", "admin");
		assertEquals(Action.SUCCESS, result);
		
		Content content = this.getContentOnEdit();
		ITextAttribute titleAttribute = (ITextAttribute) content.getAttribute("Titolo");
		assertEquals("Titolo B - Evento 2", titleAttribute.getTextForLang("it"));
		
		this.initAction("/do/jacms/Content", "preview");
		this.addParameter("mainGroup", Group.FREE_GROUP_NAME);
		this.addParameter("it_Titolo", "Nuovo titolo di prova");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		content = this.getContentOnEdit();
		titleAttribute = (ITextAttribute) content.getAttribute("Titolo");
		assertEquals("Nuovo titolo di prova", titleAttribute.getTextForLang("it"));
	}
	
	public void testExecutePreviewContent_1() throws Throwable {
		String result = this.executeEdit("EVN192", "admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executePreviewPage("");
		assertEquals(Action.SUCCESS, result);
		
		RequestContext reqCtx = (RequestContext) this.getRequest().getAttribute(RequestContext.REQCTX);
		assertNotNull(reqCtx);
		IPage currentPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		assertEquals("contentview", currentPage.getCode());
	}
	
	public void testExecutePreviewContent_2() throws Throwable {
		String result = this.executeEdit("ART187", "admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executePreviewPage("");
		assertEquals(Action.SUCCESS, result);
		
		RequestContext reqCtx = (RequestContext) this.getRequest().getAttribute(RequestContext.REQCTX);
		assertNotNull(reqCtx);
		IPage currentPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		assertEquals("coach_page", currentPage.getCode());
	}
	
	public void testExecutePreviewContent_3() throws Throwable {
		String result = this.executeEdit("ART187", "admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executePreviewPage("pagina_2");
		assertEquals(Action.SUCCESS, result);
		
		RequestContext reqCtx = (RequestContext) this.getRequest().getAttribute(RequestContext.REQCTX);
		assertNotNull(reqCtx);
		IPage currentPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		assertEquals("pagina_2", currentPage.getCode());
	}
	
	public void testFailureExecutePreviewContent() throws Throwable {
		String result = this.executeEdit("ART187", "admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executePreviewPage("wrongPageCode");
		assertEquals(Action.INPUT, result);
		
		RequestContext reqCtx = (RequestContext) this.getRequest().getAttribute(RequestContext.REQCTX);
		assertNull(reqCtx);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		assertEquals(1, fieldErrors.get("previewPageCode").size());
	}
	
	private String executePreviewPage(String pageDest) throws Throwable {
		this.initAction("/do/jacms/Content", "executePreview");
		this.addParameter("previewPageCode", pageDest);
		return this.executeAction();
	}
	
}
