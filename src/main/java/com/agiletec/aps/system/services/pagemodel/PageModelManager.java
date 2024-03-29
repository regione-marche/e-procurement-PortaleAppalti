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
package com.agiletec.aps.system.services.pagemodel;

import java.util.Collection;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio di gestione dei modelli di pagina.
 * @author 
 */
public class PageModelManager extends AbstractService implements IPageModelManager {

	public void init() throws Exception {
		this.loadPageModels();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized " 
				+ this._models.size() + " page models");
	}

	private void loadPageModels() throws ApsSystemException {
		try {
			this._models = this.getPageModelDAO().loadModels();
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading page models", t);
		}
	}

	/**
	 * Restituisce il modello di pagina con il codice dato
	 * @param name Il nome del modelo di pagina
	 * @return Il modello di pagina richiesto
	 */
	public PageModel getPageModel(String name) {
		return (PageModel) _models.get(name);
	}

	/**
	 * Restituisce la Collection completa di modelli.
	 * @return la collection completa dei modelli disponibili in oggetti PageModel.
	 */
	public Collection<PageModel> getPageModels() {
		return _models.values();
	}

	protected IPageModelDAO getPageModelDAO() {
		return _pageModelDao;
	}
	public void setPageModelDAO(IPageModelDAO pageModelDAO) {
		this._pageModelDao = pageModelDAO;
	}

	/**
	 * Map dei modelli di pagina configurati nel sistema
	 */
	private Map<String, PageModel> _models;

	private IPageModelDAO _pageModelDao;

}
