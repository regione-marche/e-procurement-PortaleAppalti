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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.HypertextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SmallContentType;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.LinkAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.ResourceAttributeInterface;

/**
 * @version 1.0
 * @author M. Morini - E.Santoboni
 */
public class TestContentManager extends BaseTestCase {
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testSearchContents() throws Throwable {
		List<String> contentIds = this._contentManager.searchId(null);
		assertNotNull(contentIds);
    	assertEquals(23, contentIds.size());
    	
    	EntitySearchFilter creationOrder = new EntitySearchFilter(IContentManager.CONTENT_CREATION_DATE_FILTER_KEY, false);
    	creationOrder.setOrder(EntitySearchFilter.ASC_ORDER);
    	EntitySearchFilter descrFilter = new EntitySearchFilter(IContentManager.CONTENT_DESCR_FILTER_KEY, false, "Cont", true);
    	EntitySearchFilter[] filters1 = {creationOrder, descrFilter};
    	contentIds = this._contentManager.searchId(filters1);
		assertNotNull(contentIds);
    	String[] expected1 = {"RAH101", "ART102", "EVN103", 
    			"ART104", "ART111", "ART120", "ART121", "ART122"};
    	assertEquals(expected1.length, contentIds.size());
    	this.verifyOrder(contentIds, expected1);
    	
    	EntitySearchFilter lastEditorFilter = new EntitySearchFilter(IContentManager.CONTENT_LAST_EDITOR_FILTER_KEY, false, "admin", true);
    	EntitySearchFilter[] filters2 = {creationOrder, descrFilter, lastEditorFilter};
    	contentIds = this._contentManager.searchId(filters2);
		assertNotNull(contentIds);
    	assertEquals(expected1.length, contentIds.size());
    	this.verifyOrder(contentIds, expected1);
    	
    	EntitySearchFilter versionFilter = new EntitySearchFilter(IContentManager.CONTENT_CURRENT_VERSION_FILTER_KEY, false, "0.", true);
    	EntitySearchFilter[] filters3 = {versionFilter};
    	contentIds = this._contentManager.searchId(filters3);
		assertNotNull(contentIds);
		String[] expected2 = {"ART179"};
    	assertEquals(expected2.length, contentIds.size());
    	this.verifyOrder(contentIds, expected2);
    	
    	versionFilter = new EntitySearchFilter(IContentManager.CONTENT_CURRENT_VERSION_FILTER_KEY, false, ".0", true);
    	EntitySearchFilter[] filters4 = {versionFilter};
    	contentIds = this._contentManager.searchId(filters4);
		assertNotNull(contentIds);
    	assertEquals(22, contentIds.size());
	}
	
	public void testSearchWorkContents() throws Throwable {
    	List<String> contents = this._contentManager.loadWorkContentsId(null, null);
    	assertNotNull(contents);
    	assertEquals(0, contents.size());
    	
    	EntitySearchFilter creationOrder = new EntitySearchFilter(IContentManager.CONTENT_CREATION_DATE_FILTER_KEY, false);
    	creationOrder.setOrder(EntitySearchFilter.DESC_ORDER);
    	EntitySearchFilter typeFilter = new EntitySearchFilter(IContentManager.ENTITY_TYPE_CODE_FILTER_KEY, false, "ART", false);
    	EntitySearchFilter[] filters1 = {creationOrder, typeFilter};
    	contents = this._contentManager.loadWorkContentsId(filters1, null);
    	assertEquals(0, contents.size());
    	
    	List<String> groupCodes = new ArrayList<String>();
    	groupCodes.add("customers");
    	contents = this._contentManager.loadWorkContentsId(filters1, groupCodes);
    	String[] order1 = {"ART102"};
    	assertEquals(order1.length, contents.size());
    	this.verifyOrder(contents, order1);
    	
    	groupCodes.add(Group.FREE_GROUP_NAME);
    	EntitySearchFilter statusFilter = new EntitySearchFilter(IContentManager.CONTENT_STATUS_FILTER_KEY, false, Content.STATUS_DRAFT, false);
    	EntitySearchFilter[] filters2 = {creationOrder, typeFilter, statusFilter};
    	contents = this._contentManager.loadWorkContentsId(filters2, groupCodes);
    	String[] order2 = {"ART102", "ART187", "ART179", "ART1"};
    	assertEquals(order2.length, contents.size());
    	this.verifyOrder(contents, order2);
    	
    	EntitySearchFilter onlineFilter = new EntitySearchFilter(IContentManager.CONTENT_ONLINE_FILTER_KEY, false);
    	EntitySearchFilter[] filters3 = {creationOrder, typeFilter, onlineFilter};
    	contents = this._contentManager.loadWorkContentsId(filters3, groupCodes);
    	String[] order3 = {"ART102", "ART187", "ART180", "ART1"};
    	assertEquals(order3.length, contents.size());
    	this.verifyOrder(contents, order3);
    	
    	onlineFilter.setNullOption(true);
    	contents = this._contentManager.loadWorkContentsId(filters3, groupCodes);
    	String[] order4 = {"ART179"};
    	assertEquals(order4.length, contents.size());
    	this.verifyOrder(contents, order4);
    	
    	onlineFilter.setNullOption(false);
    	EntitySearchFilter descrFilter = new EntitySearchFilter(IContentManager.CONTENT_DESCR_FILTER_KEY, false, "scr", true);
    	EntitySearchFilter[] filters5 = {creationOrder, typeFilter, onlineFilter, descrFilter};
    	contents = this._contentManager.loadWorkContentsId(filters5, groupCodes);
    	String[] order5 = {"ART187", "ART180"};
    	assertEquals(order5.length, contents.size());
    	this.verifyOrder(contents, order5);
    	
    	groupCodes.clear();
    	groupCodes.add(Group.ADMINS_GROUP_NAME);
    	contents = this._contentManager.loadWorkContentsId(null, groupCodes);
    	assertNotNull(contents);
    	assertEquals(23, contents.size());
    }
    
    private void verifyOrder(List<String> contents, String[] order) {
    	for (int i=0; i<contents.size(); i++) {
    		assertEquals(order[i], contents.get(i));
    	}
	}
    
    public void testLoadContent() throws Throwable {
    	Content content = this._contentManager.loadContent("ART111", false);
    	assertEquals(Content.STATUS_DRAFT, content.getStatus());
    	assertEquals("coach", content.getMainGroup());
    	assertEquals(2, content.getGroups().size());
    	assertTrue(content.getGroups().contains("customers"));
    	assertTrue(content.getGroups().contains("helpdesk"));
    	
    	Map<String, AttributeInterface> attributes = content.getAttributeMap();
    	assertEquals(7, attributes.size());
    	
    	TextAttribute title = (TextAttribute) attributes.get("Titolo");
    	assertEquals("Titolo Contenuto 3 Coach", title.getTextForLang("it"));
    	assertNull(title.getTextForLang("en"));
    	
    	MonoListAttribute authors = (MonoListAttribute) attributes.get("Autori");
    	assertEquals(4, authors.getAttributes().size());
    	
    	LinkAttribute link = (LinkAttribute) attributes.get("VediAnche");
    	assertNull(link.getSymbolicLink());
    	
    	HypertextAttribute hypertext = (HypertextAttribute) attributes.get("CorpoTesto");
    	assertEquals("<p>Corpo Testo Contenuto 3 Coach</p>", hypertext.getTextForLang("it"));
    	assertNull(hypertext.getTextForLang("en"));
    	
    	ResourceAttributeInterface image = (ResourceAttributeInterface) attributes.get("Foto");
    	assertNull(image.getResource());
    	
    	DateAttribute date = (DateAttribute) attributes.get("Data");
    	assertEquals("13/12/2006", DateConverter.getFormattedDate(date.getDate(), "dd/MM/yyyy"));
    }
    
    public void testGetContentTypes() {
    	Map<String, SmallContentType> smallContentTypes = _contentManager.getSmallContentTypesMap();
    	assertEquals(3, smallContentTypes.size());
    }
    
    public void testCreateContent() {
    	Content contentType = _contentManager.createContentType("ART");
        assertNotNull(contentType);
    }
    
    public void testCreateContentWithViewPage() {
    	Content content = _contentManager.createContentType("ART");
    	String viewPage = content.getViewPage();
    	assertEquals(viewPage, "contentview");
    }
    
    public void testCreateContentWithDefaultModel() {
    	Content content = _contentManager.createContentType("ART");
    	String defaultModel = content.getDefaultModel();
    	assertEquals(defaultModel,"1");
    }
    
    public void testGetXML() throws Throwable {
    	Content content = this._contentManager.createContentType("ART");
    	content.setId("ART1");
    	content.setTypeCode("Articolo");
    	content.setTypeDescr("Articolo");
    	content.setDescr("descrizione");
    	content.setStatus(Content.STATUS_DRAFT);
    	content.setMainGroup("free");
    	Category cat13 = new Category();
    	cat13.setCode("13");
    	content.addCategory(cat13);
    	Category cat19 = new Category();
    	cat19.setCode("19");
    	content.addCategory(cat19);
    	String xml = content.getXML();
    	assertNotNull(xml); 
    	assertTrue(xml.indexOf("<content id=\"ART1\" typecode=\"Articolo\" typedescr=\"Articolo\">")!= -1);
    	assertTrue(xml.indexOf("<descr>descrizione</descr>")!= -1);
    	assertTrue(xml.indexOf("<status>" + Content.STATUS_DRAFT +"</status>")!= -1); 
    	assertTrue(xml.indexOf("<category id=\"13\" />")!= -1);
    	assertTrue(xml.indexOf("<category id=\"19\" />")!= -1);
    }
    
    public void testLoadPublicEvents_1() throws ApsSystemException {
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, null, null);
		String[] expectedFreeContentsId = {"EVN194", "EVN193", 
				"EVN24", "EVN23", "EVN25", "EVN20", "EVN21", "EVN192", "EVN191"};
		assertEquals(expectedFreeContentsId.length, contents.size());
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertTrue(contents.contains(expectedFreeContentsId[i]));
		}
		assertFalse(contents.contains("EVN103"));
		
		List<String> groups = new ArrayList<String>();
		groups.add("coach");
		contents = _contentManager.loadPublicContentsId("EVN", null, null, groups);
		assertEquals(expectedFreeContentsId.length+2, contents.size());
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertTrue(contents.contains(expectedFreeContentsId[i]));
		}
		assertTrue(contents.contains("EVN103"));
		assertTrue(contents.contains("EVN41"));
    }
    
    public void testLoadPublicEvents_2() throws ApsSystemException {
    	List<String> groups = new ArrayList<String>();
		groups.add("coach");
		groups.add(Group.ADMINS_GROUP_NAME);
		Date start = DateConverter.parseDate("1997-06-10", "yyyy-MM-dd");
		Date end = DateConverter.parseDate("2020-09-19", "yyyy-MM-dd");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, start, end);
		EntitySearchFilter filter2 = new EntitySearchFilter(IContentManager.CONTENT_DESCR_FILTER_KEY, false, "even", true);
		filter2.setOrder(EntitySearchFilter.DESC_ORDER);
		EntitySearchFilter[] filters = {filter, filter2};
		
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, groups);
		assertEquals(2, contents.size());
		assertEquals("EVN193", contents.get(0));
		assertEquals("EVN192", contents.get(1));
		
		filter2 = new EntitySearchFilter(IContentManager.CONTENT_DESCR_FILTER_KEY, false, null, false);
		filter2.setOrder(EntitySearchFilter.DESC_ORDER);
		
		EntitySearchFilter[] filters2 = {filter, filter2};
		contents = _contentManager.loadPublicContentsId("EVN", null, filters2, groups);
		
		String[] expectedOrderedContentsId = {"EVN25", "EVN21", "EVN20", "EVN41", "EVN193", 
				"EVN192", "EVN103", "EVN23", "EVN24"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
		
		contents = _contentManager.loadPublicContentsId("EVN", null, filters2, null);
		String[] expectedFreeOrderedContentsId = {"EVN25", "EVN21", "EVN20", "EVN193", 
				"EVN192", "EVN23", "EVN24"};
		assertEquals(expectedFreeOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedFreeOrderedContentsId.length; i++) {
			assertEquals(expectedFreeOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadPublicEvents_3() throws ApsSystemException {
    	List<String> groups = new ArrayList<String>();
		groups.add(Group.ADMINS_GROUP_NAME);
		Date value = DateConverter.parseDate("1999-04-14", "yyyy-MM-dd");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, value, false);
		EntitySearchFilter[] filters = {filter};
		
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, groups);
		assertEquals(1, contents.size());
		assertEquals("EVN192", contents.get(0));
    }
    
    public void testLoadPublicEvents_4() throws ApsSystemException {
    	List<String> groups = new ArrayList<String>();
		groups.add(Group.ADMINS_GROUP_NAME);
		
		EntitySearchFilter filter1 = new EntitySearchFilter("Titolo", true, "Ce", "TF");
		filter1.setLangCode("it");
		filter1.setOrder(EntitySearchFilter.DESC_ORDER);
		
		EntitySearchFilter[] filters1 = {filter1};
		
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters1, groups);
		
		String[] expectedOrderedContentsId = {"EVN25", "EVN41", "EVN20", "EVN21", "EVN23"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
		
		filter1 = new EntitySearchFilter("Titolo", true, null, "TF");
		filter1.setLangCode("it");
		filter1.setOrder(EntitySearchFilter.DESC_ORDER);
		
		EntitySearchFilter[] filters2 = {filter1};
		
		contents = _contentManager.loadPublicContentsId("EVN", null, filters2, groups);
		
		String[] expectedOrderedContentsId2 = {"EVN25", "EVN41", "EVN20", "EVN21", "EVN23", "EVN24"};
		assertEquals(expectedOrderedContentsId2.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId2.length; i++) {
			assertEquals(expectedOrderedContentsId2[i], contents.get(i));
		}
    }
    
    public void testLoadOrderedPublicEvents_1() throws ApsSystemException {
    	EntitySearchFilter filterForDescr = new EntitySearchFilter(IContentManager.CONTENT_DESCR_FILTER_KEY, false, null, false);
    	filterForDescr.setOrder(EntitySearchFilter.ASC_ORDER);
    	EntitySearchFilter[] filters = {filterForDescr};
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		
		String[] expectedFreeContentsId = {"EVN24", "EVN23", "EVN191", 
				"EVN192", "EVN193", "EVN194", "EVN20", "EVN21", "EVN25"};
		assertEquals(expectedFreeContentsId.length, contents.size());
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertEquals(expectedFreeContentsId[i], contents.get(i));
		}
		
		filterForDescr.setOrder(EntitySearchFilter.DESC_ORDER);
		contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		
		assertEquals(expectedFreeContentsId.length, contents.size());
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertEquals(expectedFreeContentsId[expectedFreeContentsId.length - i - 1], contents.get(i));
		}
    }
    
    public void testLoadOrderedPublicEvents_2() throws ApsSystemException {
    	EntitySearchFilter filterForCreation = new EntitySearchFilter(IContentManager.CONTENT_CREATION_DATE_FILTER_KEY, false, null, false);
    	filterForCreation.setOrder(EntitySearchFilter.ASC_ORDER);
    	EntitySearchFilter[] filters = {filterForCreation};
    	
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		String[] expectedFreeOrderedContentsId = {"EVN191", "EVN192", "EVN193", "EVN194", 
				"EVN20", "EVN23", "EVN24", "EVN25", "EVN21"};
		assertEquals(expectedFreeOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedFreeOrderedContentsId.length; i++) {
			assertEquals(expectedFreeOrderedContentsId[i], contents.get(i));
		}
		
		filterForCreation.setOrder(EntitySearchFilter.DESC_ORDER);
		contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		assertEquals(expectedFreeOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedFreeOrderedContentsId.length; i++) {
			assertEquals(expectedFreeOrderedContentsId[expectedFreeOrderedContentsId.length - i - 1], contents.get(i));
		}
    }
    
    public void testLoadOrderedPublicEvents_3() throws ApsSystemException {
    	EntitySearchFilter filterForCreation = new EntitySearchFilter(IContentManager.CONTENT_CREATION_DATE_FILTER_KEY, false, null, false);
    	filterForCreation.setOrder(EntitySearchFilter.DESC_ORDER);
    	EntitySearchFilter filterForDate = new EntitySearchFilter("DataInizio", true, null, false);
    	filterForDate.setOrder(EntitySearchFilter.DESC_ORDER);
    	EntitySearchFilter[] filters = {filterForCreation, filterForDate};
    	
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		String[] expectedFreeOrderedContentsId = {"EVN21", "EVN25", "EVN24", "EVN23", 
				"EVN20", "EVN194", "EVN193", "EVN192", "EVN191"};
		assertEquals(expectedFreeOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedFreeOrderedContentsId.length; i++) {
			assertEquals(expectedFreeOrderedContentsId[i], contents.get(i));
		}
    	
		EntitySearchFilter[] filters2 = {filterForDate, filterForCreation};
		
		List<String> contents2 = _contentManager.loadPublicContentsId("EVN", null, filters2, null);
		String[] expectedFreeOrderedContentsId2 = {"EVN194", "EVN193", "EVN24", 
				"EVN23", "EVN25", "EVN20", "EVN21", "EVN192", "EVN191"};
		assertEquals(expectedFreeOrderedContentsId2.length, contents2.size());
		for (int i=0; i<expectedFreeOrderedContentsId2.length; i++) {
			assertEquals(expectedFreeOrderedContentsId2[i], contents2.get(i));
		}
    }
    
    public void testLoadOrderedPublicEvents_4() throws Throwable {
    	Content masterContent = this._contentManager.loadContent("EVN193", true);
    	masterContent.setId(null);
    	DateAttribute dateAttribute = (DateAttribute) masterContent.getAttribute("DataInizio");
    	dateAttribute.setDate(DateConverter.parseDate("17/06/2019", "dd/MM/yyyy"));
    	try {
    		this._contentManager.saveContent(masterContent);
    		this._contentManager.insertOnLineContent(masterContent);
			this.waitNotifyingThread();
			
			EntitySearchFilter filterForDate = new EntitySearchFilter("DataInizio", true, null, false);
			filterForDate.setOrder(EntitySearchFilter.DESC_ORDER);
			EntitySearchFilter[] filters = {filterForDate};
			
			List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
			String[] expectedFreeOrderedContentsId = {"EVN194", masterContent.getId(), "EVN193", "EVN24", 
					"EVN23", "EVN25", "EVN20", "EVN21", "EVN192", "EVN191"};
			assertEquals(expectedFreeOrderedContentsId.length, contents.size());
			for (int i=0; i<expectedFreeOrderedContentsId.length; i++) {
				assertEquals(expectedFreeOrderedContentsId[i], contents.get(i));
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != masterContent.getId() && !"EVN193".equals(masterContent.getId())) {
				this._contentManager.removeOnLineContent(masterContent);
				this._contentManager.deleteContent(masterContent);
			}
		}
    }
    
    public void testLoadFutureEvents_1() throws ApsSystemException {
		Date today =  DateConverter.parseDate("2005-01-01", "yyyy-MM-dd");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, today, null);
		filter.setOrder(EntitySearchFilter.ASC_ORDER);
		EntitySearchFilter[] filters = {filter};
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		String[] expectedOrderedContentsId = {"EVN21", "EVN20", "EVN25", "EVN23", 
				"EVN24", "EVN193", "EVN194"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadFutureEvents_2() throws ApsSystemException {
		Date today = DateConverter.parseDate("2005-01-01", "yyyy-MM-dd");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, today, null);
		filter.setOrder(EntitySearchFilter.DESC_ORDER);
		EntitySearchFilter[] filters = {filter};
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		String[] expectedOrderedContentsId = {"EVN194", "EVN193", "EVN24", 
				"EVN23", "EVN25", "EVN20", "EVN21"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadFutureEvents_3() throws ApsSystemException {
		Date today = DateConverter.parseDate("2005-01-01", "yyyy-MM-dd");
		List<String> groups = new ArrayList<String>();
		groups.add("coach");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, today, null);
		filter.setOrder(EntitySearchFilter.DESC_ORDER);
		EntitySearchFilter[] filters = {filter};
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, groups);
		String[] expectedOrderedContentsId = {"EVN194", "EVN193", "EVN24", 
				"EVN23", "EVN41", "EVN25", "EVN20", "EVN21"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadPastEvents_1() throws ApsSystemException {
    	Date today = DateConverter.parseDate("2008-10-01", "yyyy-MM-dd");
    	
    	EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, null, today);
		filter.setOrder(EntitySearchFilter.ASC_ORDER);
		EntitySearchFilter[] filters = {filter};
		
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		String[] expectedOrderedContentsId = {"EVN191", "EVN192", 
				"EVN21", "EVN20", "EVN25", "EVN23"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadPastEvents_2() throws ApsSystemException {
    	Date today = DateConverter.parseDate("2008-10-01", "yyyy-MM-dd");
		
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, null, today);
		filter.setOrder(EntitySearchFilter.DESC_ORDER);
		EntitySearchFilter[] filters = {filter};
		
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, null);
		String[] expectedOrderedContentsId = {"EVN23", "EVN25", 
				"EVN20", "EVN21", "EVN192", "EVN191"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadPastEvents_3() throws ApsSystemException {
		Date today = DateConverter.parseDate("2008-02-13", "yyyy-MM-dd");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, null, today);
		filter.setOrder(EntitySearchFilter.ASC_ORDER);
		EntitySearchFilter[] filters = {filter};
		
		List<String> groups = new ArrayList<String>();
		groups.add("coach");
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, filters, groups);
		String[] expectedOrderedContentsId = {"EVN191", "EVN192", "EVN103", 
				"EVN21", "EVN20", "EVN25", "EVN41", "EVN23"};
		assertEquals(expectedOrderedContentsId.length, contents.size());
		for (int i=0; i<expectedOrderedContentsId.length; i++) {
			assertEquals(expectedOrderedContentsId[i], contents.get(i));
		}
    }
    
    public void testLoadContentsForCategory() throws ApsSystemException {
    	String[] categories1 = {"evento"};
		List<String> contents = _contentManager.loadPublicContentsId(categories1, null, null);
		assertEquals(2, contents.size());
		assertTrue(contents.contains("EVN192"));
		assertTrue(contents.contains("EVN193"));
		
		String[] categories2 = {"cat1"};
		contents = _contentManager.loadPublicContentsId(categories2, null, null);
		assertEquals(1, contents.size());
		assertTrue(contents.contains("ART180"));
    }
    
    
    public void testLoadEventsForCategory() throws ApsSystemException {
    	String[] categories = {"evento"};
		List<String> contents = _contentManager.loadPublicContentsId("EVN", categories, null, null);
		assertEquals(2, contents.size());
		assertTrue(contents.contains("EVN192"));
		assertTrue(contents.contains("EVN193"));
		
		Date today = DateConverter.parseDate("2005-02-13", "yyyy-MM-dd");
		EntitySearchFilter filter = new EntitySearchFilter("DataInizio", true, null, today);
		filter.setOrder(EntitySearchFilter.ASC_ORDER);
		EntitySearchFilter[] filters = {filter};
		contents = _contentManager.loadPublicContentsId("EVN", categories, filters, null);
		assertEquals(1, contents.size());
		assertTrue(contents.contains("EVN192"));
    }
    
    public void testLoadEventsForGroup() throws ApsSystemException {
		List<String> contents = _contentManager.loadPublicContentsId("EVN", null, null, null);
		String[] expectedFreeContentsId = {"EVN191", "EVN192", "EVN193", "EVN194", 
				"EVN20", "EVN23", "EVN21", "EVN24", "EVN25"};
		assertEquals(expectedFreeContentsId.length, contents.size());
		
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertTrue(contents.contains(expectedFreeContentsId[i]));
		}
		
		Collection<String> allowedGroup = new HashSet<String>();
		allowedGroup.add(Group.FREE_GROUP_NAME);
		allowedGroup.add("customers");
		
		contents = _contentManager.loadPublicContentsId("EVN", null, null, allowedGroup);
		assertEquals(9, contents.size());
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertTrue(contents.contains(expectedFreeContentsId[i]));
		}
		assertFalse(contents.contains("EVN103"));//evento coach
		
		allowedGroup.remove("customers");
		allowedGroup.remove(Group.FREE_GROUP_NAME);
		allowedGroup.add(Group.ADMINS_GROUP_NAME);
		
		contents = _contentManager.loadPublicContentsId("EVN", null, null, allowedGroup);
		assertEquals(11, contents.size());
		for (int i=0; i<expectedFreeContentsId.length; i++) {
			assertTrue(contents.contains(expectedFreeContentsId[i]));
		}
		assertTrue(contents.contains("EVN103"));
		assertTrue(contents.contains("EVN41"));
    }
    
    private void init() throws Exception {
    	try {
    		this._contentManager = (IContentManager) this.getService(JacmsSystemConstants.CONTENT_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
    private IContentManager _contentManager = null;
    
}