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
package com.agiletec.plugins.jacms.aps.system.services.searchengine;

import java.io.File;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;

/**
 * Classe factory degli elementi ad uso del SearchEngine.
 * @author E.Santoboni
 */
public class SearchEngineDAOFactory implements ISearchEngineDAOFactory, BeanFactoryAware {
	
	@Override
	public void init() throws Exception {
		this._subDirectory = this.getConfigManager().getConfigItem(SUB_INDEX_DIR_ITEM_NAME);
		if (_subDirectory == null) {
			throw new ApsSystemException("Item configurazione assente: " + SUB_INDEX_DIR_ITEM_NAME);
		}
	}
	
	@Override
	public IIndexerDAO getIndexer(boolean newIndex) throws ApsSystemException {
		return this.getIndexer(newIndex, this._subDirectory);
	}
	
	@Override
	public ISearcherDAO getSearcher() throws ApsSystemException {
		return this.getSearcher(this._subDirectory);
	}
	
	@Override
	public IIndexerDAO getIndexer(boolean newIndex, String subDir) throws ApsSystemException {
		IIndexerDAO indexerDao = (IIndexerDAO) this._beanFactory.getBean("jacmsSearchEngineIndexerDao");
		indexerDao.init(this.getDirectory(subDir), newIndex);
		return indexerDao;
	}
	
	@Override
	public ISearcherDAO getSearcher(String subDir) throws ApsSystemException {
		ISearcherDAO searcherDao = (ISearcherDAO) this._beanFactory.getBean("jacmsSearchEngineSearcherDao");
		searcherDao.init(this.getDirectory(subDir));
		return searcherDao;
	}
	
	@Override
	public void updateSubDir(String newSubDirectory) throws ApsSystemException {
		this.getConfigManager().updateConfigItem(SUB_INDEX_DIR_ITEM_NAME, newSubDirectory);
		String oldDir = _subDirectory;
		this._subDirectory = newSubDirectory;
		this.deleteSubDirectory(oldDir);
	}
	
	private File getDirectory(String subDirectory) throws ApsSystemException {
		String dirName = this.getIndexDiskRootFolder();
		if (!dirName.endsWith("/")) dirName += "/";
		dirName += subDirectory;
		ApsSystemUtils.getLogger().debug("Directory indici: " + dirName);
		File dir = new File(dirName);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			ApsSystemUtils.getLogger().debug("Creata Directory indici");
		}
		if (!dir.canRead() || !dir.canWrite()) {
			throw new ApsSystemException(dirName + " does not have r/w rights");
		}
		return dir;
	}
    
	@Override
	public void deleteSubDirectory(String subDirectory) {
		String dirName = this.getIndexDiskRootFolder();
		if (!dirName.endsWith("/") || !dirName.endsWith(File.separator)) 
			dirName += File.separator;
		dirName += subDirectory;
		File dir = new File(dirName);
		if (dir.exists() || dir.isDirectory()) {
			String[] filesName = dir.list();
			for (int i=0; i<filesName.length; i++) {
				File fileToDelete = new File(dirName + File.separator + filesName[i]);
				fileToDelete.delete();
			}
			dir.delete();
			ApsSystemUtils.getLogger().debug("Cancellata SottoDirectory indici " + subDirectory);
		}
	}
	
	protected String getIndexDiskRootFolder() {
		return _indexDiskRootFolder;
	}
	public void setIndexDiskRootFolder(String indexDiskRootFolder) {
		this._indexDiskRootFolder = indexDiskRootFolder;
	}
	
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configService) {
		this._configManager = configService;
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private BeanFactory _beanFactory;
	
	private String _indexDiskRootFolder;
	private String _subDirectory;
	
	private ConfigInterface _configManager;
	
	private final String SUB_INDEX_DIR_ITEM_NAME = "subIndexDir";
	
}
