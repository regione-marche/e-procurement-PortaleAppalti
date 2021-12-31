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
package com.agiletec.aps.system.common.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.util.DateConverter;

/**
 * This class implements a filter to search among entities.
 * @author E.Santoboni
 */
public class EntitySearchFilter implements Serializable {
	
	/**
	 * Filter constructor.
	 * This constructor is used when checking the presence of a value contained
	 * either in the attribute field or in the entity metadata.
	 * @param key The key of the filtering element; it may be a content attribute of type {@link AttributeInterface}
	 * or the ID of metadata).
	 * @param isAttributeFilter Decide whether the filter is applied to an Entity Attribute (true) or
	 * to a metadata (false).
	 */
	public EntitySearchFilter(String key, boolean isAttributeFilter) {
		this.setKey(key);
		this._attributeFilter = isAttributeFilter;
	}
	
	/**
	 * Filter constructor.
	 * This constructor must be used to filter the attribute values or entity metadata
	 * looking for a specific value.
	 * @param key The key of the filtering element; it may be a content attribute of type {@link AttributeInterface}
	 * or the ID of metadata).
	 * @param isAttributeFilter Decide whether the filter is applied to an Entity Attribute (true) or
	 * to a metadata (false).
	 * @param value The value to look for. If null, the filter checks if the attribute (or metadata)
	 * has been valued.
	 * @param useLikeOption When true the database search will be performed using the "LIKE" clause.
	 * This option can be used to filter by the value of a string attribute (or metadata). It can be
	 * used only with string and with not null values.
	 */
	public EntitySearchFilter(String key, boolean isAttributeFilter, Object value, boolean useLikeOption) {
		this.setKey(key);
		this._attributeFilter = isAttributeFilter;
		this._value = value;
		if ((!isAttributeFilter && useLikeOption) && (null == value || !(value instanceof String))) {
			throw new RuntimeException("Error: The 'like' clause cannot be applied on a null value of type string");
		}
		this._likeOption = useLikeOption;
	}
	
	/**
	 * Filter constructor.
	 * This constructor is used when filtering by a range of values; this can applied to both
	 * Entity Attributes and metadata).
	 * @param key The key of the filtering element; it may be a content attribute of type {@link AttributeInterface}
	 * or the ID of metadata).
	 * @param isAttributeFilter Decide whether the filter is applied to an Entity Attribute (true) or
	 * to a metadata (false).
	 * @param start The starting value of the interval. It can be an object of type
	 * "String", "Date", "BigDecimal", "Boolean" o null.
	 * @param end The ending value of the interval. It can be an object of type 
	 * "String", "Date", "BigDecimal", "Boolean" o null.
	 */
	public EntitySearchFilter(String key, boolean isAttributeFilter, Object start, Object end) {
		this.setKey(key);
		this._attributeFilter = isAttributeFilter;
		this._start = start;
		this._end = end;
	}
	
	private void setKey(String key) {
		if (null == key || key.trim().length()==0) {
			throw new RuntimeException("Errore: Chiave obbligatoria");
		}
		this._key = key;
	}
	
	/**
	 * Return the key of the filter object.
	 * @return The key of the filter object.
	 */
	public String getKey() {
		return _key;
	}
	
	/**
	 * Return the value of the key of the entity to look for.
	 * @return The value to look for.
	 */
	public Object getValue() {
		return _value;
	}
	
	/**
	 * Return the starting value of the interval. It can be an object of type
	 * "String", "Date", "BigDecimal", "Boolean" o null.
	 * @return The starting value of the interval.
	 */
	public Object getStart() {
		return _start;
	}
	
	/**
	 * Return the ending date of the interval. It can be an object of type
	 * "String", "Date", "BigDecimal", "Boolean" o null.
	 * @return L'end dell'intervallo;
	 */
	public Object getEnd() {
		return _end;
	}
	
	/**
	 * Restituisce l'ordine da attribuire alle entità in funzione dell'attributo specificato dal filtro.
	 * Può assumere i valori "ASC" o "DESC".
	 * Return the order to use to sort entities found when the filter is applied. It can assume the values
	 * "ASC" or "DESC" and depends on the attribute specified in the filter.
	 * @return The order used to sort entities found applying the filter.
	 * L'ordine da attribuire alle entità in funzione dell'attributo specificato.
	 */
	public String getOrder() {
		return _order;
	}
	
	/**
	 * Set up the order to use to sort entities found when the filter is applied. It can assume the values
	 * "ASC" or "DESC" and depends on the attribute specified in the filter.
	 * @param order The order used to sort entities found applying the filter.
	 */
	public void setOrder(String order) {
		if (null != order && !(order.equals(ASC_ORDER) || order.equals(DESC_ORDER))) {
			throw new RuntimeException("Error: the 'order' clause cannot be null and must be comparable using the constants ASC_ORDER or DESC_ORDER");
		}
		this._order = order;
	}
	
	/**
	 * Check whether the string must be performed using the "LIKE" clause or not.
	 * This option can be used to filter by a specific value in this attributes containing text strings.
	 * @return True if the search process can have the "LIKE" clause enabled, false otherwise. 
	 */
	public boolean isLikeOption() {
		return _likeOption;
	}
	
	/**
	 * This method shows if the filter must be applied on a Entity Attribute or
	 * a metadata.
	 * @return true if the filter is to be applied to an attribute entity or a 
	 * to a metadata of the an entity.
	 */
	public boolean isAttributeFilter() {
		return _attributeFilter;
	}
	
	public boolean isNullOption() {
		return _nullOption;
	}
	
	public void setNullOption(boolean nullOption) {
		if (nullOption && (this.isAttributeFilter() || null != this.getValue() || null != this.getStart() || null != this.getEnd())) {
			throw new RuntimeException("Error: the NULL cluase may be used only in conjunction with null metadata fields");
		}
		this._nullOption = nullOption;
	}
	
	public String getLangCode() {
		return this._langCode;
	}
	
	public void setLangCode(String langCode) {
		this.setLangCode(this.isAttributeFilter(), langCode);
	}
	
	private void setLangCode(boolean isAttributeFilter, String langCode) {
		if (null != langCode && !isAttributeFilter) {
			throw new RuntimeException("Error: the language can be only specified on attribute filters");
		}
		this._langCode = langCode;
	}
	
	public String toString() {
		StringBuffer param = new StringBuffer();
		param.append(KEY_PARAM).append("=").append(this.getKey()).append(SEPARATOR);
		param.append(FILTER_TYPE_PARAM).append("=").append(Boolean.toString(this.isAttributeFilter()));
		this.appendObjectValue(param, this.getValue(), VALUE_PARAM);
		if (this.getValue() instanceof String) {
			param.append(SEPARATOR).append(LIKE_OPTION_PARAM).append("=").append(Boolean.toString(this.isLikeOption()));
		}
		this.appendObjectValue(param, this.getStart(), START_PARAM);
		this.appendObjectValue(param, this.getEnd(), END_PARAM);
		if (null != this.getOrder()) {
			param.append(SEPARATOR).append(ORDER_PARAM).append("=").append(this.getOrder());
		}
		return param.toString();
	}
	
	private void appendObjectValue(StringBuffer param, Object value, String paramValue) {
		if (null != value) {
			param.append(SEPARATOR).append(paramValue).append("=");
			if (value instanceof String) {
				param.append(value);
			} else if (value instanceof Date) {
				param.append(DateConverter.getFormattedDate((Date)value, DATE_PATTERN));
			} else if (value instanceof BigDecimal) {
				param.append(((BigDecimal)value).toString());
			} else if (value instanceof Boolean) {
				param.append(((Boolean)value).toString());
			}
		}
	}
	
	private String _key;
	
	private boolean _attributeFilter;
	
	private Object _value;
	
	private String _order;
	private Object _start;
	private Object _end;
	
	private boolean _likeOption;
	private boolean _nullOption;
	
	private String _langCode;
	
	public static final String ASC_ORDER = "ASC";
	public static final String DESC_ORDER = "DESC";
	
	public static final String SEPARATOR = ";";
	public static final String KEY_PARAM = "key";
	public static final String FILTER_TYPE_PARAM = "attributeFilter";
	public static final String VALUE_PARAM = "value";
	public static final String LIKE_OPTION_PARAM = "likeOption";
	public static final String START_PARAM = "start";
	public static final String END_PARAM = "end";
	public static final String ORDER_PARAM = "order";
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
}
