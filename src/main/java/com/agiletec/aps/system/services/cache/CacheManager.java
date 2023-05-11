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
package com.agiletec.aps.system.services.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import com.agiletec.aps.system.services.page.events.PageChangedObserver;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Servizio gestore cache.
 * @author E.Santoboni
 */
public class CacheManager extends AbstractService implements ICacheManager, PageChangedObserver, ServletContextAware {
	
	@Override
	public void init() throws Exception {
		Properties props = new Properties();
		try {
			InputStream is = this._servletContext.getResourceAsStream(this.getCachePropertiesFilePath());
			props.load(is);
		} catch (IOException e) {
			throw new ApsSystemException("Error while reading cache configuration", e);
		}
		props.setProperty("cache.path", this.getCacheDiskRootFolder());
		this._cacheAdmin = new GeneralCacheAdministrator(props);
		this.flushAll();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": cache service initialized");
	}
	
	public void updateFromPageChanged(PageChangedEvent event) {
		IPage page = event.getPage();
		String pageCacheGroupName = SystemConstants.PAGES_CACHE_GROUP_PREFIX + page.getCode();
		this.flushGroup(pageCacheGroupName);
	}

	public void release() {
		this._cacheAdmin.destroy();
	}

	/**
	 * Flush the entire cache immediately.
	 */
	public void flushAll() {
		this._cacheAdmin.flushAll();
	}

	/**
	 * Flushes a single cache entry.
	 * @param key The key entered by the user.
	 */
	public void flushEntry(String key) {
		this._cacheAdmin.flushEntry(key);
	}
	
	/**
	 * Flushes all items that belong to the specified group.
	 * @param group The name of the group to flush.
	 */
	public void flushGroup(String group) {
		this._cacheAdmin.flushGroup(group);
	}

	/**
	 * Put an object in a cache.
	 * @param key The key entered by the user.
	 * @param obj The object to store.
	 */
	public synchronized void putInCache(String key, Object obj) {
		_cacheAdmin.putInCache(key, obj);
	}

	/**
	 * Put an object in a cache.
	 * @param key The key entered by the user.
	 * @param obj The object to store.
	 * @param groups The groups that this object belongs to.
	 */
	public synchronized void putInCache(String key, Object obj, String[] groups) {
		_cacheAdmin.putInCache(key, obj, groups);
	}
	
	/**
	 * Get an object from the cache.
	 * @param key The key entered by the user.
	 * @return The object from cache.
	 */
	public Object getFromCache(String key) {
		Object obj = null;
		try {
			obj = _cacheAdmin.getFromCache(key);
		} catch (NeedsRefreshException nre) {
			try {
				this._cacheAdmin.putInCache(key, "TEMP");
			} catch (Throwable t) {
				this._cacheAdmin.cancelUpdate(key);
			} finally {
				this._cacheAdmin.flushEntry(key);
			}
		}
		return obj;
	}

	/**
	 * Get an object from the cache.
	 * @param key The key entered by the user.
	 * @param myRefreshPeriod How long the object can stay in cache in seconds.
	 * @return The object from cache.
	 */
	public Object getFromCache(String key, int myRefreshPeriod) {
		Object obj = null;
		try {
			obj = _cacheAdmin.getFromCache(key, myRefreshPeriod);
		} catch (NeedsRefreshException nre) {
			try {
				this._cacheAdmin.putInCache(key, "TEMP");
			} catch (Throwable t) {
				this._cacheAdmin.cancelUpdate(key);
			} finally {
				this._cacheAdmin.flushEntry(key);
			}
		}
		return obj;
	}
	
	protected String getCachePropertiesFilePath() {
		return _cachePropertiesFilePath;
	}
	public void setCachePropertiesFilePath(String cachePropertiesFilePath) {
		this._cachePropertiesFilePath = cachePropertiesFilePath;
	}
	
	protected String getCacheDiskRootFolder() {
		return _cacheDiskRootFolder;
	}
	public void setCacheDiskRootFolder(String cacheDiskRootFolder) {
		this._cacheDiskRootFolder = cacheDiskRootFolder;
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this._servletContext = servletContext;
	}

	private String _cachePropertiesFilePath;
	private String _cacheDiskRootFolder;
	
	private GeneralCacheAdministrator _cacheAdmin;
	
	private ServletContext _servletContext;
	
}