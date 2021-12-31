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
package com.agiletec.apsadmin.system.entity;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * @author E.Santoboni
 */
public abstract class AbstractApsEntityFinderAction extends BaseAction implements IApsEntityFinderAction {
	
	@Override
	public String execute() {
		try {
			IApsEntity prototype = this.getEntityPrototype();
			if (null != prototype) {
				EntitySearchFilter filterToAdd = new EntitySearchFilter(IEntityManager.ENTITY_TYPE_CODE_FILTER_KEY, false, prototype.getTypeCode(), false);
				this.addFilter(filterToAdd);
				EntitySearchFilter[] filters = this.getEntityActionHelper().getSearchFilters(this, prototype);
				this.addFilters(filters);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "execute");
			throw new RuntimeException("Error while creating entity filters", t);
		}
		return SUCCESS;
	}
	
	protected void addFilters(EntitySearchFilter[] filters) {
		for (int i = 0; i < filters.length; i++) {
			EntitySearchFilter filterToAdd = filters[i];
			this.addFilter(filterToAdd);
		}
	}
	
	protected void addFilter(EntitySearchFilter filterToAdd) {
		EntitySearchFilter[] filters = this.getFilters();
		int len = filters.length;
		EntitySearchFilter[] newFilters = new EntitySearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = filters[i];
		}
		newFilters[len] = filterToAdd;
		this.setFilters(newFilters);
	}
	
	@Override
	public String trash() {
		try {
			String checkResult = this.checkDeletingEntity();
			if (null != checkResult) return checkResult;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			throw new RuntimeException("Error while trashing entity", t);
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		try {
			String checkResult = this.checkDeletingEntity();
			if (null != checkResult) return checkResult;
			this.deleteEntity(this.getEntityId());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			throw new RuntimeException("Error while deleting entity", t);
		}
		return SUCCESS;
	}
	
	protected abstract void deleteEntity(String entityId) throws Throwable;
	
	protected String checkDeletingEntity() throws Throwable {
		IApsEntity entity = this.getEntity(this.getEntityId());
		if (null == entity) {
			String[] args = {this.getEntityId()};
			this.addFieldError("entityId", this.getText("field.error.entity.type.null",args));
			return INPUT;
		}
		return null;
	}
	
	public abstract IApsEntity getEntityPrototype();
	
	protected abstract IApsEntity getEntity(String entityId);
	
	public String getSearchFormFieldValue(String inputFieldName) {
		return this.getRequest().getParameter(inputFieldName);
	}
	
	public List<AttributeInterface> getSearcheableAttributes() {
		List<AttributeInterface> searcheableAttributes = new ArrayList<AttributeInterface>();
		IApsEntity prototype = this.getEntityPrototype();
		if (null == prototype) return searcheableAttributes;
		List<AttributeInterface> contentAttributes = prototype.getAttributeList();
		for (int i=0; i<contentAttributes.size(); i++) {
			AttributeInterface attribute = contentAttributes.get(i);
			if (attribute.isActive() && attribute.isSearcheable()) {
				searcheableAttributes.add(attribute);
			}
		}
		return searcheableAttributes;
	}
	
	public String getEntityId() {
		return _entityId;
	}
	public void setEntityId(String entityId) {
		this._entityId = entityId;
	}
	
	protected IEntityActionHelper getEntityActionHelper() {
		return _entityActionHelper;
	}
	public void setEntityActionHelper(IEntityActionHelper entityActionHelper) {
		this._entityActionHelper = entityActionHelper;
	}
	
	protected EntitySearchFilter[] getFilters() {
		return _filters;
	}
	protected void setFilters(EntitySearchFilter[] filters) {
		this._filters = filters;
	}
	
	private String _entityId;
	
	private IEntityActionHelper _entityActionHelper;
	
	private EntitySearchFilter[] _filters = new EntitySearchFilter[0];
	
} 