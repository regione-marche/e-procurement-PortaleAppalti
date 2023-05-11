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
package com.agiletec.aps.system.services.showlettype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio di gestione dei tipi di showlet (ShowletType) definiti
 * nel sistema. (Questo servizio non riguarda la configurazione delle
 * istanze di showlet nelle pagine)
 * @author 
 */
public class ShowletTypeManager extends AbstractService implements IShowletTypeManager {

	public void init() throws Exception {
		this.loadShowletTypes();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized " + _showletTypes.size() + " showlet types");
	}

	/**
	 * Caricamento da db del catalogo dei tipi di showlet.
	 * @throws ApsSystemException In caso di errori di lettura da db.
	 */
	private void loadShowletTypes() throws ApsSystemException{
		try {
			this._showletTypes = this.getShowletTypeDAO().loadShowletTypes();
			Iterator<ShowletType> iter = this._showletTypes.values().iterator();
			while (iter.hasNext()) {
				ShowletType type = iter.next();
				String mainTypeCode = type.getParentTypeCode();
				if (null != mainTypeCode) {
					type.setParentType(this._showletTypes.get(mainTypeCode));
				}
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading showlets types", t);
		}
	}

	@Override
	public ShowletType getShowletType(String code) {
		return (ShowletType) _showletTypes.get(code);
	}

	@Override
	public List<ShowletType> getShowletTypes() {
		List<ShowletType> types = new ArrayList<ShowletType>();
		types.addAll(this._showletTypes.values());
		return types;
	}

	protected IShowletTypeDAO getShowletTypeDAO() {
		return _showletTypeDao;
	}
	public void setShowletTypeDAO(IShowletTypeDAO showletTypeDAO) {
		this._showletTypeDao = showletTypeDAO;
	}

	private Map<String, ShowletType> _showletTypes;

	private IShowletTypeDAO _showletTypeDao;

}
