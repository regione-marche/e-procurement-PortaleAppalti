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
package com.agiletec.plugins.jacms.apsadmin.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AbstractMonoInstanceResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AbstractMultiInstanceResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceDataBean;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

import it.maggioli.eldasoft.plugins.ppcommon.aps.UploadValidator;

/**
 *  Class used to handle resource objects.
 *    
 * @version 1.0
 * @author E.Santoboni
 */
public class ResourceAction extends AbstractResourceAction implements IResourceAction, ResourceDataBean {
	
	@Override
	public void validate() {
		super.validate();
		if (!this.hasFieldErrors() && this.getStrutsAction() == ApsAdminSystemConstants.ADD) {
			ResourceInterface resourcePrototype = this.getResourceManager().createResourceType(this.getResourceType());
			this.checkRightFileType(resourcePrototype);
			if (this.hasFieldErrors()) return;
			this.checkFileName();
			if (this.hasFieldErrors()) return;
			this.checkDuplicateFile(resourcePrototype);
			if (this.hasFieldErrors()) return;
			this.checkContentFileType();
		}
	}
	
	private void checkContentFileType() {
		if(!UploadValidator.checkTikaFileContent(_file, _filename, null, null)) {
			this.addFieldError("upload", this.getText("Resource.file.wrongFormat"));
		}
	}
	
	private void checkRightFileType(ResourceInterface resourcePrototype) {
		if (!this.isRightType(resourcePrototype)) {
			this.addFieldError("upload", this.getText("Resource.file.wrongFormat"));
		}
	}
	
	private void checkFileName() {
		String fileName = this.getFileName();
		if (!fileName.matches("[a-zA-Z_\\.0-9]+")) {
			String[] args = {fileName};
			this.addFieldError("upload", this.getText("Resource.file.wrongFileNameFormat", args));
		}
	}
	
	private void checkDuplicateFile(ResourceInterface resourcePrototype) {
		resourcePrototype.setMainGroup(this.getMainGroup());
		String baseDiskFolder = resourcePrototype.getDiskFolder();
    	String formFileName = this._filename;
    	String fileName = null;
    	if (resourcePrototype.isMultiInstance()) {
    		fileName = ((AbstractMultiInstanceResource) resourcePrototype).getInstanceFileName(formFileName, 0, null);
    	} else {
    		fileName = ((AbstractMonoInstanceResource) resourcePrototype).getInstanceFileName(formFileName);
    	}
    	if ((new File(baseDiskFolder + fileName)).exists()) {
    		String[] args = {formFileName};
    		this.addFieldError("upload", this.getText("Resource.file.alreadyPresent", args));
		}
	}
	
	private boolean isRightType(ResourceInterface resourcePrototype) {
		boolean isRight = false;
		if (this._filename.length() > 0) {
			String fileName = _filename;
			String docType = fileName.substring(fileName.lastIndexOf('.')+1).trim();
			String[] types = resourcePrototype.getAllowedFileTypes();
			isRight = this.isValidType(docType, types);
		} else {
			isRight = true;
		}
		return isRight;
	}
	
	private boolean isValidType(String docType, String[] rightTypes) {
		boolean isValid = false;
		if (rightTypes.length > 0) {
			for (int i=0; i<rightTypes.length; i++) {
				if (docType.toLowerCase().equals(rightTypes[i])) {
					isValid = true;
					break;
				}
			}
		} else {
			isValid = true;
		}
		return isValid;
	}
	
	@Override
	public String newResource() {
		this.setStrutsAction(ApsAdminSystemConstants.ADD);
		return SUCCESS;
	}
	
	@Override
	public String edit() {
		try {
			ResourceInterface resource = this.loadResource(this.getResourceId());
			this.setResourceTypeCode(resource.getType());
			this.setDescr(resource.getDescr());
			List<Category> resCategories = resource.getCategories();
			for (int i=0; i<resCategories.size(); i++) {
				Category resCat = resCategories.get(i);
				this.getCategoryCodes().add(resCat.getCode());
			}
			this.setMainGroup(resource.getMainGroup());
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String save() {
		try {
			if (ApsAdminSystemConstants.ADD == this.getStrutsAction()) {
				this.getResourceManager().addResource(this);
			} else if (ApsAdminSystemConstants.EDIT == this.getStrutsAction()) {
				ResourceInterface resource = this.loadResource(this.getResourceId());
				resource.setDescr(this.getDescr());
				resource.setCategories(this.getCategories());
				this.getResourceManager().updateResource(resource);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String trash() {
		try {
			String resourceId = this.getResourceId();
			ResourceInterface resource = this.loadResource(resourceId);
			if (resource == null) {
				return INPUT;
			}
			Map references = this.getResourceActionHelper().getReferencingObjects(resource, this.getRequest());
			if (references.size() > 0) {
				this.setReferences(references);
				return "references";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String delete() {
		try {
			ResourceInterface resource = this.loadResource(this.getResourceId());
			// Since this is a critical error we notify it 
			if (resource == null) {
				this.addActionError(this.getText("Resource.delete.error"));
				return INPUT;
			}
			this.getResourceManager().deleteResource(resource);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String joinCategory() {
		return this.joinRemoveCategory(true, this.getCategoryCode());
	}
	
	@Override
	public String removeCategory() {
		return this.joinRemoveCategory(false, this.getCategoryCode());
	}
	
	/**
	 * This method perfoms either the linking of a resource to a category or the removal of such association.
	 * NOTE: in the current implementation operations carried on invalid or unknown categories do not return error code on
	 * purpose, since the join or unlink process does not take place. 
	 * @param isJoin if 'true' associates a resource to a category, otherwise remove it
	 * @param categoryCode the string code of the category to work with.
	 * @return FAILURE if error detected, SUCCESS otherwise.
	 */
	private String joinRemoveCategory(boolean isJoin, String categoryCode) {
		try {
			Category category = this.getCategory(categoryCode);
			if (category == null) return SUCCESS;
			List<String> categories = this.getCategoryCodes();
			if (isJoin) {
				if (!categories.contains(categoryCode)) {
					categories.add(categoryCode);
				}
			} else {
				categories.remove(categoryCode);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "joinRemoveCategory");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public List<Group> getAllowedGroups() {
		return this.getResourceActionHelper().getAllowedGroups(this.getCurrentUser());
	}
	
	public Category getCategory(String categoryCode) {
		return this.getCategoryManager().getCategory(categoryCode);
	}
	
	public String getResourceId() {
		return _resourceId;
	}
	public void setResourceId(String resourceId) {
		this._resourceId = resourceId;
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	public Map getReferences() {
		return _references;
	}
	
	public void setReferences(Map references) {
		this._references = references;
	}
	
    public void setUpload(File file) {
       this._file = file;
    }
    public File getUpload() {
		return this._file;
	}

    public void setUploadContentType(String contentType) {
       this._contentType = contentType;
    }

    public void setUploadFileName(String filename) {
       this._filename = filename;
    }
	
    @Override
	public String getDescr() {
		return _descr;
	}
	public void setDescr(String descr) {
		this._descr = descr;
	}
	
	@Override
	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>(this.getCategoryCodes().size());
		Iterator<String> iter = this.getCategoryCodes().iterator();
		while (iter.hasNext()) {
			String categoryCode = iter.next();
			Category category = this.getCategoryManager().getCategory(categoryCode);
			if (null != category) categories.add(category);
		}
		return categories;
	}
	
	@Override
	public String getFileName() {
		return this._filename;
	}
	
	@Override
	public int getFileSize() {
		return (int) this._file.length()/1000;
	}
	
	@Override
	public InputStream getInputStream() throws Throwable {
		return new FileInputStream(this._file);
	}
	
	@Override
	public String getMainGroup() {
		return this._mainGroup;
	}
	public void setMainGroup(String mainGroup) {
		this._mainGroup = mainGroup;
	}
	
	public List<String> getCategoryCodes() {
		return _categoryCodes;
	}
	public void setCategoryCodes(List<String> categoryCodes) {
		this._categoryCodes = categoryCodes;
	}
	
	@Override
	public String getMimeType() {
		return this._contentType;
	}
	@Override
	public String getResourceType() {
		return this.getResourceTypeCode();
	}
	
	public String getCategoryCode() {
		return _categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this._categoryCode = categoryCode;
	}
	
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	
	private String _resourceId;
	private String _descr;
	private String _mainGroup;
	private List<String> _categoryCodes = new ArrayList<String>();
	
	private File _file;
    private String _contentType;
    private String _filename;
	
	private IGroupManager _groupManager;
    
    private int _strutsAction;
	
	private Map _references;
	
	private String _categoryCode;
	
}
