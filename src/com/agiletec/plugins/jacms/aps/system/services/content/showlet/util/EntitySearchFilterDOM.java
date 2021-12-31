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
package com.agiletec.plugins.jacms.aps.system.services.content.showlet.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListFilterBean;

/**
 * 
 * @author E.Santoboni
 */
public class EntitySearchFilterDOM {
	
	public static List<Properties> getPropertiesFilters(String showletParam) {
		if (null == showletParam) return new ArrayList<Properties>();
		String[] filterStrings = showletParam.split("\\+");
		List<Properties> properties = new ArrayList<Properties>(filterStrings.length);
		for (int i=0; i<filterStrings.length; i++) {
			String fullFilterString = filterStrings[i];
			String filterString = fullFilterString.substring(1, fullFilterString.length()-1);
			Properties props = getProperties(filterString);
			properties.add(props);
		}
		return properties;
	}
	
	public EntitySearchFilter[] getFilters(String contentType, String showletParam, IContentManager contentManager, String langCode) {
		if (null == contentType) return null;
		IApsEntity prototype = contentManager.createContentType(contentType);
		if (null == prototype) return null;
		List<Properties> properties = getPropertiesFilters(showletParam);
		EntitySearchFilter[] filters = new EntitySearchFilter[properties.size()];
		for (int i=0; i<properties.size(); i++) {
			Properties props = properties.get(i);
			filters[i] = this.createFilter(prototype, props, langCode);
		}
		return filters;
	}
	
	public EntitySearchFilter getFilter(String contentType, IContentListFilterBean bean, IContentManager contentManager, String langCode) {
		IApsEntity prototype = contentManager.createContentType(contentType);
		Properties props = new Properties();
		props.setProperty(EntitySearchFilter.KEY_PARAM, bean.getKey());
		props.setProperty(EntitySearchFilter.FILTER_TYPE_PARAM, String.valueOf(bean.isAttributeFilter()));
		props.setProperty(EntitySearchFilter.LIKE_OPTION_PARAM, String.valueOf(bean.getLikeOption()));
		if (null != bean.getValue()) props.setProperty(EntitySearchFilter.VALUE_PARAM, bean.getValue());
		if (null != bean.getStart()) props.setProperty(EntitySearchFilter.START_PARAM, bean.getStart());
		if (null != bean.getEnd()) props.setProperty(EntitySearchFilter.END_PARAM, bean.getEnd());
		if (null != bean.getOrder()) props.setProperty(EntitySearchFilter.ORDER_PARAM, bean.getOrder());
		return this.createFilter(prototype, props, langCode);
	}
	
	private static Properties getProperties(String filterString) {
		Properties props = new Properties();
		String[] params = filterString.split(EntitySearchFilter.SEPARATOR);
		for (int i=0; i<params.length; i++) {
			String[] elements = params[i].split("=");
			if (elements.length != 2) continue;
			props.setProperty(elements[0], elements[1]);
		}
		return props;
	}
	
	private EntitySearchFilter createFilter(IApsEntity prototype, Properties props, String langCode) {
		EntitySearchFilter filter = null;
		String key = props.getProperty(EntitySearchFilter.KEY_PARAM);
		if (null == key) return null;
		boolean isAttributeFilter = Boolean.parseBoolean(props.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		boolean likeOption = false;
		String likeOptionString = props.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM);
		if (null != likeOptionString) {
			likeOption = Boolean.parseBoolean(likeOptionString);
		}
		if (!isAttributeFilter) {
			String value = props.getProperty(EntitySearchFilter.VALUE_PARAM);
			filter = new EntitySearchFilter(key, isAttributeFilter, value, likeOption);
		} else {
			AttributeInterface attr = (AttributeInterface) prototype.getAttribute(key);
			if (null != attr && null != prototype) {
				String value = props.getProperty(EntitySearchFilter.VALUE_PARAM);
				String start = props.getProperty(EntitySearchFilter.START_PARAM);
				String end = props.getProperty(EntitySearchFilter.END_PARAM);
				Object objectValue = null;
				Object objectStart = null;
				Object objectEnd = null;
				if (attr instanceof DateAttribute) {
					if (null != value) objectValue = this.buildDate(value);
					if (null != start) objectStart = this.buildDate(start);
					if (null != end) objectEnd = this.buildDate(end);
				} else if (attr instanceof ITextAttribute || attr instanceof BooleanAttribute) {
					if (null != value) objectValue = value;
					if (null != start) objectStart = start;
					if (null != end) objectEnd = end;
				} else if (attr instanceof NumberAttribute) {
					if (null != value) objectValue = this.buildNumber(value);
					if (null != start) objectStart = this.buildNumber(start);
					if (null != end) objectEnd = this.buildNumber(end);
				}
				String langForFilter = (attr.isMultilingual() ? langCode : null);
				if (objectValue != null) {
					filter = new EntitySearchFilter(key, true, objectValue, likeOption);
				} else filter = new EntitySearchFilter(key, true, objectStart, objectEnd);
				filter.setLangCode(langForFilter);
			} else new ApsSystemException("ERRORE: Contenuto tipo '" + prototype.getTypeCode() 
					+ "' e attributo '" + key + "' non Riconosciuti");
		}
		String order = props.getProperty(EntitySearchFilter.ORDER_PARAM);
		filter.setOrder(order);
		return filter;
	}
	
	protected Date buildDate(String dateString) {
		SimpleDateFormat dataF = new SimpleDateFormat(EntitySearchFilter.DATE_PATTERN);
		String today = "today, oggi, odierna";
		java.util.Date data = null;
		try {
			if (today.indexOf(dateString) != -1) {
				data = new java.util.Date();
			} else {
				data = dataF.parse(dateString);
			}
		} catch (ParseException ex) {
			ApsSystemUtils.getLogger().error( "Stringa inserita non corrispondente a data - " + dateString + " -");
		}
		return data;
	}
	
	protected BigDecimal buildNumber(String numberString) {
		BigDecimal number = null;
		try {
			number = new BigDecimal(numberString);
		} catch (NumberFormatException e) {
			ApsSystemUtils.getLogger().error( "Stringa inserita non corrispondente a formato numerico - " + numberString + " -");
		}
		return number;
	}
	
	/**
	 * Crea il parametro di configurazione della showlet, caratteristico per la rappresentazione dei filtri.
	 * Il parametro viene ricavato in base alla lista di filtri specificati.
	 * @param filters I filtri applicati.
	 * @return Il parametro di configurazione della showlet.
	 */
	public String getShowletParam(EntitySearchFilter[] filters) {
		StringBuffer param = new StringBuffer("");
		for (int i = 0; i < filters.length; i++) {
			if (i!=0) param.append("+");
			String element = filters[i].toString(); 
			param.append("(");
			param.append(element);
			param.append(")");
		}
		return param.toString();
	}
	
	/**
	 * Crea il parametro di configurazione della showlet, caratteristico per la rappresentazione dei filtri.
	 * Il parametro viene ricavato in base alla lista di properties specificata.
	 * @param properties Le properties rappresentanti ciascuna un filtro.
	 * @return Il parametro di configurazione della showlet.
	 */
	
	public static String getShowletParam(List<Properties> properties) {
		StringBuffer param = new StringBuffer("");
		for (int i = 0; i < properties.size(); i++) {
			if (i!=0) param.append("+");
			Properties props = properties.get(i);
			String element = createElement(props); 
			param.append("(");
			param.append(element);
			param.append(")");
		}
		return param.toString();
	}
	
	private static String createElement(Properties props) {
		StringBuffer param = new StringBuffer();
		Iterator<Object> keys = props.keySet().iterator();
		boolean init = true;
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (!init) param.append(EntitySearchFilter.SEPARATOR);
			param.append(key).append("=").append(props.getProperty(key));
			init = false;
		}
		return param.toString();
	}
	
}
