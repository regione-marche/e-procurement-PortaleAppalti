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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;

/**
 * @author E.Santoboni
 */
public class DefaultApsEntityFinderAction extends AbstractApsEntityFinderAction implements BeanFactoryAware {
	
	public String changeEntityType() {
		this.setEntityTypeCode(null);
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		// Operation Not Supported
		return FAILURE;
	}
	
	@Override
	public String trash() {
		// Operation Not Supported
		return FAILURE;
	}
	
	@Override
	protected void deleteEntity(String entityId) throws Throwable {
		// Operation Not Supported
	}
	
	@Override
	protected IApsEntity getEntity(String entityId) {
		IApsEntity entity = null;
		try {
			IEntityManager entityManager = this.getEntityManager();
			entity = entityManager.getEntity(entityId);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getEntity");
			throw new RuntimeException("Error while extracting entity", t);
		}
		return entity;
	}
	
	public List<IApsEntity> getEntityPrototypes() {
		List<IApsEntity> entityPrototypes = null;
		try {
			Map<String, IApsEntity> modelMap = this.getEntityManager().getEntityPrototypes();
			entityPrototypes = new ArrayList<IApsEntity>(modelMap.values());
			BeanComparator comparator = new BeanComparator("typeDescr");
			Collections.sort(entityPrototypes, comparator);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getEntityPrototypes");
			throw new RuntimeException("Error while extracting entity prototypes", t);
		}
		return entityPrototypes;
	}
	
	@Override
	public IApsEntity getEntityPrototype() {
		IEntityManager entityManager = this.getEntityManager();
		return entityManager.getEntityPrototype(this.getEntityTypeCode());
	}
	
	@Override
	public List<String> getSearchResult() {
		List<String> result = null;
		try {
			IEntityManager entityManager = this.getEntityManager();
			result = entityManager.searchId(this.getFilters());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getSearchResult");
			throw new RuntimeException("Error while searching entity Ids", t);
		}
		return result;
	}
	
	protected IEntityManager getEntityManager() {
		String entityManagerName = this.getEntityManagerName();
		return (IEntityManager) this.getBeanFactory().getBean(entityManagerName);
	}
	
	public String getEntityManagerName() {
		String sessionValue = (String) this.getRequest().getSession().getAttribute(ApsEntityActionConstants.ENTITY_TYPE_MANAGER_SESSION_PARAM);
		if (null != sessionValue) return sessionValue;
		return _entityManagerName;
	}
	public void setEntityManagerName(String entityManagerName) {
		this.getRequest().getSession().setAttribute(ApsEntityActionConstants.ENTITY_TYPE_MANAGER_SESSION_PARAM, entityManagerName);
		this._entityManagerName = entityManagerName;
	}
	
	public String getEntityTypeCode() {
		return _entityTypeCode;
	}
	public void setEntityTypeCode(String entityTypeCode) {
		this._entityTypeCode = entityTypeCode;
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private String _entityManagerName;
	private String _entityTypeCode;
	
	private BeanFactory _beanFactory;
	
}