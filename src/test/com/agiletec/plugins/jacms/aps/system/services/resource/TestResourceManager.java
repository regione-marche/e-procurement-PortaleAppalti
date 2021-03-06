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
package test.com.agiletec.plugins.jacms.aps.system.services.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.com.agiletec.aps.BaseTestCase;
import test.com.agiletec.plugins.jacms.aps.system.services.resource.mock.MockResourceDataBean;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AttachResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ImageResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceDataBean;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInstance;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * @author W.Ambu - E.Santoboni
 */
public class TestResourceManager extends BaseTestCase {
	
    protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
    
    public void testLoadResource() throws Throwable {
    	try {
			ResourceInterface resource = this._resourceManager.loadResource("44");
			assertTrue(resource instanceof ImageResource);
			assertTrue(resource.isMultiInstance());
			assertEquals(resource.getDescr(), "logo");
			assertEquals(resource.getCategories().size(), 1);
    		resource = this._resourceManager.loadResource("7");
			assertTrue(resource instanceof AttachResource);
			assertFalse(resource.isMultiInstance());
			assertEquals(resource.getDescr(), "configurazione");
			assertEquals(resource.getCategories().size(), 0);
		} catch (Throwable t) {
			throw t;
		}
    }
    
    public void testUpdateResource() throws Throwable {
    	String oldDescr = null;
    	List<Category> oldCategories = null;
    	try {
			ResourceInterface resource = this._resourceManager.loadResource("44");
			assertTrue(resource instanceof ImageResource);
			assertEquals(resource.getDescr(), "logo");
			assertEquals(resource.getCategories().size(), 1);
			assertTrue(resource.isMultiInstance());
			oldCategories = resource.getCategories();
			oldDescr = resource.getDescr();
			String newDescr = "Descrizione modificata";
			resource.setDescr(newDescr);
			resource.setCategories(new ArrayList<Category>());
			this._resourceManager.updateResource(resource);
			resource = this._resourceManager.loadResource("44");
			assertEquals(resource.getDescr(), newDescr);
			assertEquals(resource.getCategories().size(), 0);
		} catch (Throwable t) {
			throw t;
		} finally {
			if (oldCategories != null && oldDescr != null) {
				ResourceInterface resource = this._resourceManager.loadResource("44");
				resource.setCategories(oldCategories);
				resource.setDescr(oldDescr);
				this._resourceManager.updateResource(resource);
			}
		}
    }
    
    public void testSearchResources() throws Throwable {
    	List<String> resourceIds = _resourceManager.searchResourcesId("Image", "", null, null);
		assertEquals(3, resourceIds.size());
		
		List<String> allowedGroups = new ArrayList<String>();
		allowedGroups.add("customers");
		resourceIds = _resourceManager.searchResourcesId("Image", "", null, allowedGroups);
		assertEquals(1, resourceIds.size());
		
		resourceIds = _resourceManager.searchResourcesId("Image", "Wrong descr", null, null);
		assertEquals(0, resourceIds.size());
    }
    
    public void testSearchResourcesForCategory() throws Throwable {
    	List<String> resourceIds = _resourceManager.searchResourcesId("Image", null, "resCat1", null);
		assertEquals(1, resourceIds.size());
		
		resourceIds = _resourceManager.searchResourcesId("Image", null, "wrongCat", null);
		assertEquals(0, resourceIds.size());
		
		List<String> allowedGroups = new ArrayList<String>();
		allowedGroups.add("customers");
		resourceIds = _resourceManager.searchResourcesId("Image", "", "resCat1", allowedGroups);
		assertEquals(0, resourceIds.size());
    }
    
    public void testAddRemoveImageResource() throws Throwable {
    	this.testAddRemoveImageResource(Group.FREE_GROUP_NAME);
    	this.testAddRemoveImageResource(Group.ADMINS_GROUP_NAME);
    }
    
    private void testAddRemoveImageResource(String mainGroup) throws Throwable {
    	ResourceInterface res = null;
    	String resDescrToAdd = "Logo jAPS";
    	String resourceType = "Image";
    	String categoryCodeToAdd = "resCat1";
    	ResourceDataBean bean = this.getMockResource(resourceType, mainGroup, resDescrToAdd, categoryCodeToAdd);
    	try {
    		List<String> resources = _resourceManager.searchResourcesId(resourceType, null, categoryCodeToAdd, null);
			assertEquals(1, resources.size());
    		
			this._resourceManager.addResource(bean);
			resources = _resourceManager.searchResourcesId(resourceType, resDescrToAdd, null, null);
			assertEquals(resources.size(), 1);
			resources = _resourceManager.searchResourcesId(resourceType, resDescrToAdd, categoryCodeToAdd, null);
			assertEquals(resources.size(), 1);
			res = this._resourceManager.loadResource(resources.get(0));
			assertTrue(res instanceof ImageResource);
			assertEquals(res.getCategories().size(), 1);
			assertEquals(res.getDescr(), resDescrToAdd);
			
			ResourceInstance instance0 = ((ImageResource) res).getInstance(0, null);
			assertEquals("jAPS_logo_d0.jpg", instance0.getFileName());
			assertEquals("image/jpeg", instance0.getMimeType());
			
			resources = _resourceManager.searchResourcesId(resourceType, null, categoryCodeToAdd, null);
			assertEquals(resources.size(), 2);
		} catch (Throwable t) {
			throw t;
		} finally {
			if (res != null) {
				this._resourceManager.deleteResource(res);
				List<String> resources = _resourceManager.searchResourcesId(resourceType, resDescrToAdd, null, null);
				assertEquals(resources.size(), 0);
				
				resources = _resourceManager.searchResourcesId(resourceType, null, categoryCodeToAdd, null);
				assertEquals(resources.size(), 1);
			}
		}
    }
    
    private ResourceDataBean getMockResource(String resourceType, 
    		String mainGroup, String resDescrToAdd, String categoryCodeToAdd) {
    	File file = new File("admin/test/jAPS_logo.jpg");
    	MockResourceDataBean bean = new MockResourceDataBean();
    	bean.setFile(file);
    	bean.setDescr(resDescrToAdd);
    	bean.setMainGroup(mainGroup);
    	bean.setResourceType(resourceType);
    	bean.setMimeType("image/jpeg");
    	List<Category> categories = new ArrayList<Category>();
    	ICategoryManager catManager = 
    		(ICategoryManager) this.getService(SystemConstants.CATEGORY_MANAGER);
    	Category cat = catManager.getCategory(categoryCodeToAdd);
    	categories.add(cat);
    	bean.setCategories(categories);
    	return bean;
    }
    
    public void testGetResourceType() {
    	ResourceInterface imageResource = _resourceManager.createResourceType("Image");
        assertEquals("", imageResource.getDescr());
        assertEquals("", imageResource.getId());
        assertEquals("Image", imageResource.getType());
    }
    
    public void testCreateResourceType() {
    	ResourceInterface imageResource =_resourceManager.createResourceType("Image");
        assertNotNull(imageResource);
        assertEquals("", imageResource.getDescr());
        assertEquals("", imageResource.getId());
        assertEquals("Image", imageResource.getType());
        assertNotSame("", imageResource.getXML());
    }
    
    public void testGetGroupUtilizers() throws Throwable {
    	assertTrue(this._resourceManager instanceof GroupUtilizer);
    	List utilizers = ((GroupUtilizer) this._resourceManager).getGroupUtilizers("free");
    	assertEquals(3, utilizers.size());
    	
    	utilizers = ((GroupUtilizer) this._resourceManager).getGroupUtilizers("customers");
    	assertEquals(1, utilizers.size());
    	ResourceInterface resource = (ResourceInterface)  utilizers.get(0);
    	assertEquals("jAPS", resource.getDescr());
    }
    
    private void init() throws Exception {
    	try {
    		_resourceManager = (IResourceManager) this.getService(JacmsSystemConstants.RESOURCE_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
    private IResourceManager _resourceManager = null;

}
