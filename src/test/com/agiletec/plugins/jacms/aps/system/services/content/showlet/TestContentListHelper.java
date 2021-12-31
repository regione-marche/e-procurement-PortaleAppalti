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
package test.com.agiletec.plugins.jacms.aps.system.services.content.showlet;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListHelper;

/**
 * @version 1.0
 * @author E.Santoboni
 */
public class TestContentListHelper extends BaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
    
    public void testGetFilters() throws Throwable {
    	String filtersShowletParam = "(key=DataInizio;attributeFilter=true;start=21/10/2007;order=DESC)+(key=Titolo;attributeFilter=true;order=ASC)";
    	EntitySearchFilter[] filters = this._helper.getFilters("EVN", filtersShowletParam, this.getRequestContext());
    	assertEquals(2, filters.length);
    	EntitySearchFilter filter = filters[0];
    	assertEquals("DataInizio", filter.getKey());
    	assertEquals(DateConverter.parseDate("21/10/2007", "dd/MM/yyyy"), filter.getStart());
    	assertNull(filter.getEnd());
    	assertNull(filter.getValue());
    	assertEquals("DESC", filter.getOrder());
    }
	
    private void init() throws Exception {
        try {
            Lang lang = new Lang();
            lang.setCode("it");
            lang.setDescr("italiano");
            this.getRequestContext().addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, lang);
            
            IPageManager pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
            IPage page = pageManager.getRoot();
            this.getRequestContext().addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, page);
            this.getRequestContext().addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, new Integer(1));
            
            Showlet showlet = new Showlet();
            IShowletTypeManager showletTypeMan = 
            	(IShowletTypeManager) this.getService(SystemConstants.SHOWLET_TYPE_MANAGER);
            ShowletType showletType = showletTypeMan.getShowletType("content_viewer");
            showlet.setType(showletType);
            showlet.setConfig(new ApsProperties());
            this.getRequestContext().addExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET, showlet);
            
            this._helper = (IContentListHelper) this.getApplicationContext().getBean("jacmsContentListHelper");
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }
	
	private IContentListHelper _helper;
	
}
