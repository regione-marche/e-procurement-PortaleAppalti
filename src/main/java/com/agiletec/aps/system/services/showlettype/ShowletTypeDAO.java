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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;
import com.agiletec.aps.util.ApsProperties;

/**
 * Data Access Object per i tipi di showlet (ShowletType).
 * @author 
 */
public class ShowletTypeDAO extends AbstractDAO implements IShowletTypeDAO {

	/**
	 * Carica e restituisce il Map dei tipi di showlet.
	 * @return Il map dei tipi di showlet
	 */
	public Map<String, ShowletType> loadShowletTypes() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		Map<String, ShowletType> showletTypes = new HashMap<String, ShowletType>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_SHOWLET_TYPES);
			while (res.next()) {
				ShowletType showletType = this.showletTypeFromResultSet(res);
				showletTypes.put(showletType.getCode(), showletType);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading showlets", "loadShowletTypes");
		} finally{
			closeDaoResources(res, stat, conn);
		}
		return showletTypes;
	}

	/**
	 * Costruisce e restituisce un tipo di showlet leggendo una riga di recordset.
	 * @param res Il resultset da leggere.
	 * @return Il tipo di showlet generato.
	 * @throws ApsSystemException In caso di errore
	 */
	protected ShowletType showletTypeFromResultSet(ResultSet res) throws ApsSystemException {
		ShowletType showletType = new ShowletType();
		String code = null;
		try {
			code = res.getString(1);
			showletType.setCode(code);

			String xmlTitles = res.getString(2);
			ApsProperties titles = new ApsProperties();
			titles.loadFromXml(xmlTitles);
			showletType.setTitles(titles);

			String xml = res.getString(3);
			if (null != xml && xml.trim().length() > 0) {
				ShowletTypeDOM showletTypeDom = new ShowletTypeDOM(xml);
				showletType.setTypeParameters(showletTypeDom.getParameters());
				showletType.setAction(showletTypeDom.getAction());
			}
			showletType.setPluginCode(res.getString(4));

			showletType.setParentTypeCode(res.getString(5));

			String config = res.getString(6);
			if (null != config && config.trim().length() > 0) {
				ApsProperties defaultConfig = new ApsProperties();
				defaultConfig.loadFromXml(config);
				showletType.setConfig(defaultConfig);
			}

			if ((null != showletType.getConfig() && null == showletType.getParentTypeCode())) {
				throw new ApsSystemException("Default configuration found in the type '" +
						code + "' with no parent type assigned");
			}

			int isLocked = res.getInt(7);
			showletType.setLocked(isLocked == 1);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "showletTypeFromResultSet",
					"Error parsing the Showlet Type '" + code + "'");
			throw new ApsSystemException("Error in the parsing in the Showlet Type '" + code + "'", t);
		}
		return showletType;
	}

	private final String ALL_SHOWLET_TYPES =
		"SELECT code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked FROM showletcatalog";

}
