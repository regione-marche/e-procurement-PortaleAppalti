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
package com.agiletec.plugins.jacms.aps.system.services.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.IEntityDAO;
import com.agiletec.aps.system.common.entity.IEntitySearcherDAO;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.CategoryUtilizer;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.aps.system.services.keygenerator.IKeyGeneratorManager;
import com.agiletec.aps.system.services.page.PageUtilizer;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SmallContentType;
import com.agiletec.plugins.jacms.aps.system.services.resource.ResourceUtilizer;

/**
 * Contents manager. This implements all the methods needed to create and manage the contents.
 * @author M.Diana - E.Santoboni
 */
public class ContentManager extends ApsEntityManager 
		implements IContentManager, GroupUtilizer, PageUtilizer, ContentUtilizer, ResourceUtilizer, CategoryUtilizer {
	
	@Override
	public void init() throws Exception {
		super.init();
		this.createSmallContentTypes();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": inizializzati " + 
				super.getEntityTypes().size() + " tipi di contenuto");
	}
	
	private void createSmallContentTypes() {
		this._smallContentTypes = new HashMap<String, SmallContentType>(this.getEntityTypes().size());
		List<IApsEntity> types = new ArrayList<IApsEntity>(this.getEntityTypes().values());
		for (int i=0; i<types.size(); i++) {
			Content contentPrototype = (Content) types.get(i);
			SmallContentType smallContentType = new SmallContentType();
			smallContentType.setCode(contentPrototype.getTypeCode());
			smallContentType.setDescr(contentPrototype.getTypeDescr());
			this._smallContentTypes.put(smallContentType.getCode(), smallContentType);
		}
	}
	
	@Override
	public void updateEntityPrototype(IApsEntity entityType) throws ApsSystemException {
		super.updateEntityPrototype(entityType);
		this.createSmallContentTypes();
	}
	
	/**
	 * Create a new instance of the requested content. The new content is forked (or cloned) 
	 * from the corresponding prototype, and it's returned empty.
	 * @param typeCode The code of the requested (proto)type, as declared in the configuration.
	 * @return The new content.
	 */
	@Override
	public Content createContentType(String typeCode) {
		Content content = (Content) super.getEntityPrototype(typeCode);
		return content;
	}
	
	/**
	 * Return a list of the of the content types in a 'small form'. 'Small form' mans that
	 * the contents returned are purged from all unnecessary information (eg. attributes).
	 * @return The list of the types in a (small form).
	 */
	@Override
	public List<SmallContentType> getSmallContentTypes() {
		List<SmallContentType> smallContentTypes = new ArrayList<SmallContentType>();
		smallContentTypes.addAll(this._smallContentTypes.values());
		Collections.sort(smallContentTypes);
		return smallContentTypes;
	}
	
	/**
	 * Return the map of the prototypes of the contents types.
	 * Return a map, index by the code of the type, of the prototypes of the available content types. 
	 * @return The map of the prototypes of the content types in a 'SmallContentType' objects.
	 */
	@Override
	public Map<String, SmallContentType> getSmallContentTypesMap() {
		return this._smallContentTypes;
	}
	
	/**
	 * Return the code of the default page used to display the given content.
	 * The default page is defined at content type level; the type is extrapolated
	 * from the code built following the conventions. 
	 * @param contentId The content ID
	 * @return The page code.
	 */
	@Override
	public String getViewPage(String contentId) {
		Content type = this.getTypeById(contentId);
		String pageCode = type.getViewPage();
		return pageCode;
	}
	
	/**
	 * Return the code of the default model of content.
	 * @param contentId The content code
	 * @return Il requested model code
	 */
	@Override
	public String getDefaultModel(String contentId) {
		Content type = this.getTypeById(contentId);
		String defaultModel = type.getDefaultModel();
		return defaultModel;
	}
	
	/**
	 * Return the code of the model to be used when the content is rendered in list
	 * @param contentId The code of the content
	 * @return The code of the model
	 */
	@Override
	public String getListModel(String contentId) {
		Content type = this.getTypeById(contentId);
		String defaultListModel = type.getListModel();
		return defaultListModel;
	}
	
	/**
	 * Return a complete content given its ID; it is possible to choose to return the published 
	 * -unmodifiable!- content or the working copy. It also returns the data in the form of XML.
	 * @param id The ID of the content
	 * @param onLine Specifies the type of the content to return: 'true' references the published
	 * content, 'false' the freely modifiable one. 
	 * @return The requested content.
	 * @throws ApsSystemException In case of error.
	 */
	@Override
	public Content loadContent(String id, boolean onLine) throws ApsSystemException {
		Content content = null;
		try {
			ContentRecordVO contentVo = this.loadContentVO(id);
			if (contentVo != null) {
				String xmlData;
				if (onLine) {
					xmlData = contentVo.getXmlOnLine();
				} else {
					xmlData = contentVo.getXmlWork();
				}
				if (xmlData != null) {
					content = (Content) this.createEntityFromXml(contentVo.getTypeCode(), xmlData);
					content.setId(contentVo.getId());
					content.setTypeCode(contentVo.getTypeCode());
					content.setDescr(contentVo.getDescr());
					content.setOnLine(contentVo.isOnLine());
					content.setMainGroup(contentVo.getMainGroupCode());
					content.setVersion(contentVo.getVersion());
					content.setLastEditor(contentVo.getLastEditor());
					content.setStatus(contentVo.getStatus());
				}
			}
		} catch (ApsSystemException e) {
			throw e;
		}
		return content;
	}
	
	/**
	 * Return a {@link ContentRecordVO} (shortly: VO) containing the all content informations
	 * stored in the DB. 
	 * @param id The id of the requested content. 
	 * @return The VO object corresponding to the wanted content.
	 * @throws ApsSystemException in case of error.
	 */
	@Override
	public ContentRecordVO loadContentVO(String id) throws ApsSystemException {
		ContentRecordVO contentVo = null;
		try {
			contentVo = (ContentRecordVO) this.getContentDAO().loadEntityRecord(id);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadContentVO");
			throw new ApsSystemException("Errore caricamento contenuto vo con id " + id, t);
		}
		return contentVo;
	}
	
	/**
	 * Save a content in the DB.
	 * This moethod is invoked when saving a new content (whose ID will be null)
	 * or when updating an existing content (the ID is not null)
	 * @param content The content to add or modify.
	 * @throws ApsSystemException in case of error.
	 */	
	@Override
	public void saveContent(Content content) throws ApsSystemException {
		try {
			content.incrementVersion(false);
			if (null == content.getId()) {
				IKeyGeneratorManager keyGenerator = (IKeyGeneratorManager) this.getService(SystemConstants.KEY_GENERATOR_MANAGER);
				int key = keyGenerator.getUniqueKeyCurrentValue();
				String id = content.getTypeCode() + key;
				content.setId(id);
				this.getContentDAO().addEntity(content);
			} else {
				this.getContentDAO().updateEntity(content);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveContent");
			throw new ApsSystemException("Errore salvataggio contenuto", t);
		}
	}
	
	/**
	 * Publish a content.
	 * @param content The ID associated to the content to be displayed in the portal.
	 * @throws ApsSystemException in case of error.
	 */
	@Override
	public void insertOnLineContent(Content content) throws ApsSystemException {
		try {
			if (null == content.getId()) {
				this.saveContent(content);
			}
			content.incrementVersion(true);
			this.getContentDAO().insertOnLineContent(content);
			int operationEventCode = -1;
			if (content.isOnLine()) {
				operationEventCode = PublicContentChangedEvent.UPDATE_OPERATION_CODE;
			} else {
				operationEventCode = PublicContentChangedEvent.INSERT_OPERATION_CODE;
			}
			this.notifyPublicContentChanging(content, operationEventCode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "insertOnLineContent");
			throw new ApsSystemException("Errore inserimento contenuto onLine", t);
		}
	}
	
	/**
	 * Return the unique key used to reference a rendered content which is going to be cached. 
	 * @param contentId The id of the rendered content.
	 * @param modelId The ID of the model used to render the content.
	 * @param langCode The rendering language.
	 * @return The unique key used by the caching engine.
	 */
	public static String getRenderedContentCacheKey(String contentId, long modelId, String langCode) {
		String key = contentId + "_" + modelId + "_" + langCode;
		return key;
	}
	
	/**
	 * Return the unique key used to cache and retrieve the access authorization 
	 * of a content (stored in the 'ContentAuthorizationInfo' object).
	 * @param contentId L'identificativo del contenuto.
	 * @return The unique key used to cache and retrieve the authorizations of a content.
	 */
	public static String getContentAuthInfoCacheKey(String contentId) {
		return contentId + "_AUTH_INFO";
	}
	
	/**
	 * Return the list of all the content IDs. 
	 * @return The list of all the content IDs. 
	 * @throws ApsSystemException In case of error
	 * @deprecated From jAPS 2.0 version 2.0.9, use searchId(EntitySearchFilter[]) method 
	 */
	@Override
	public List<String> getAllContentsId() throws ApsSystemException {
		return super.getAllEntityId();
	}
	
	@Override
	public void reloadEntityReferences(String entityId) {
		try {
			Content content = this.loadContent(entityId, true);
			if (content != null) {
				this.getContentDAO().reloadPublicContentReferences(content);
			}
			Content workcontent = this.loadContent(entityId, false);
			if (workcontent != null) {
				this.getContentDAO().reloadWorkContentReferences(workcontent);
			}
			ApsSystemUtils.getLogger().info("Reloaded content references for content " + entityId);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadEntityReferences", 
					"Error on reloading content references for content " + entityId);
		}
	}
	
	/**
	 * Unpublish a content, preventing it from being displayed in the portal. Obviously
	 * the content itself is not deleted.
	 * @param content the content to unpublish.
	 * @throws ApsSystemException in case of error
	 */	
	@Override
	public void removeOnLineContent(Content content) throws ApsSystemException {
		try {
			content.incrementVersion(false);
			this.getContentDAO().removeOnLineContent(content);
			this.notifyPublicContentChanging(content, PublicContentChangedEvent.REMOVE_OPERATION_CODE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeOnLineContent");
			throw new ApsSystemException("Errore in rimozione contenuto onLine", t);
		}
	}
	
	/**
	 * Notify the modification of a published content.
	 * @param content The modified content.
	 * @param operationCode the operation code to notify.
	 * @exception ApsSystemException in caso of error. 
	 */
	private void notifyPublicContentChanging(Content content, int operationCode) throws ApsSystemException {
		PublicContentChangedEvent event = new PublicContentChangedEvent();
		event.setContent(content);
		event.setOperationCode(operationCode);
		this.notifyEvent(event);
	}
	
	/**
	 * Return the content type from the given ID code.
	 * The code is extracted following the coding conventions: the first three characters
	 * are the type of the code.
	 * @param contentId the content ID whose content type is extracted.
	 * @return The content type requested
	 */
	private Content getTypeById(String contentId) {
		String typeCode = contentId.substring(0, 3);
		Content type = (Content) super.getEntityTypes().get(typeCode);
		return type;
	}
	
	/**
	 * Deletes a content from the DB.
	 * @param content The content to delete.
	 * @throws ApsSystemException in case of error.
	 */
	@Override
	public void deleteContent(Content content) throws ApsSystemException {
		try {
			this.getContentDAO().deleteEntity(content.getId());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteContent");
			throw new ApsSystemException("Errore in cancellazione contenuto " + content.getId(), t);
		}
	}
	
	@Override
	public List<String> loadPublicContentsId(String contentType, String[] categories, EntitySearchFilter[] filters, 
			Collection<String> userGroupCodes) throws ApsSystemException {
		List<String> contentsId = null;
		try {
			contentsId = this.getPublicContentSearcherDAO().loadPublicContentsId(contentType, categories, filters, userGroupCodes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadContentsId");
			throw new ApsSystemException("Errore caricamento id contenuti", t);
		}
		return contentsId;
	}
	
	@Override
	public List<String> loadPublicContentsId(String[] categories, EntitySearchFilter[] filters, 
			Collection<String> userGroupCodes) throws ApsSystemException {
		List<String> contentsId = null;
		try {
			contentsId = this.getPublicContentSearcherDAO().loadPublicContentsId(categories, filters, userGroupCodes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadContentsId");
			throw new ApsSystemException("Errore caricamento id contenuti", t);
		}
		return contentsId;
	}
	
	/**
	 * @deprecated From jAPS 2.0 version 2.0.9. Use loadWorkContentsId or loadPublicContentsId
	 */
	@Override
	public List<String> loadContentsId(String[] categories, EntitySearchFilter[] filters, 
			Collection<String> userGroupCodes, boolean onlyOwner) throws ApsSystemException {
		throw new ApsSystemException("'loadContentsId' method deprecated From jAPS 2.0 version 2.0.9.");
	}
	
	@Override
	public List<String> loadWorkContentsId(EntitySearchFilter[] filters, Collection<String> userGroupCodes) throws ApsSystemException {
		List<String> contentsId = null;
		try {
			contentsId = this.getWorkContentSearcherDAO().loadContentsId(filters, userGroupCodes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadContentsId");
			throw new ApsSystemException("Errore caricamento id contenuti", t);
		}
		return contentsId;
	}
	
	@Override
	public List getPageUtilizers(String pageCode) throws ApsSystemException {
		List<ContentRecordVO> contentsVo = null;
    	try {
    		List<String> contentIds = this.getContentDAO().getPageUtilizers(pageCode);
			contentsVo = this.getContentsRecord(contentIds);
    	} catch (Throwable t) {
            throw new ApsSystemException("Errore in caricamento Contenuti referenziati con pagina " + pageCode, t);
    	}
    	return contentsVo;
	}
	
	@Override
	public List getContentUtilizers(String contentId) throws ApsSystemException {
		List<ContentRecordVO> contentsVo = null;
    	try {
    		List<String> contentIds = this.getContentDAO().getContentUtilizers(contentId);
			contentsVo = this.getContentsRecord(contentIds);
    	} catch (Throwable t) {
            throw new ApsSystemException("Errore in caricamento Contenuti referenziati con contenuto " + contentId, t);
    	}
    	return contentsVo;
	}
	
	@Override
	public List getGroupUtilizers(String groupName) throws ApsSystemException {
		List<ContentRecordVO> contentsVo = null;
		try {
			List<String> contentIds = this.getContentDAO().getGroupUtilizers(groupName);
			contentsVo = this.getContentsRecord(contentIds);
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in caricamento Contenuti referenziati con gruppo " + groupName, t);
		}
		return contentsVo;
	}
	
	@Override
	public List getResourceUtilizers(String resourceId) throws ApsSystemException {
		List<ContentRecordVO> contentsVo = null;
		try {
			List<String> contentIds = this.getContentDAO().getResourceUtilizers(resourceId);
			contentsVo = this.getContentsRecord(contentIds);
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in caricamento Contenuti referenziati con risorsa " + resourceId, t);
		}
		return contentsVo;
	}
	
	@Override
	public List getCategoryUtilizers(String resourceId) throws ApsSystemException {
		List<ContentRecordVO> contentsVo = null;
		try {
			List<String> contentIds = this.getContentDAO().getCategoryUtilizers(resourceId);
			contentsVo = this.getContentsRecord(contentIds);
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in caricamento Contenuti referenziati con risorsa " + resourceId, t);
		}
		return contentsVo;
	}
	
	protected List<ContentRecordVO> getContentsRecord(List<String> contentIds) throws Throwable {
		List<ContentRecordVO> contentsVo = new ArrayList<ContentRecordVO>();
		Iterator<String> iter = contentIds.iterator();
		while (iter.hasNext()) {
			String id = (String) iter.next();
			ContentRecordVO contentVo = this.loadContentVO(id);
			if (contentVo != null) contentsVo.add(contentVo);
		}
		return contentsVo;
	}
	
	@Override
	public boolean isSearchEngineUser() {
		return true;
	}
	
    /**
     * Return the DAO which handles all the operations on the contents.
     * @return The DAO managing the contents.
     */
    protected IContentDAO getContentDAO() {
		return _contentDao;
	}
    
    /**
     * Set the DAO which handles the operations on the contents. 
     * @param contentDao The DAO managing the contents.
     */
	public void setContentDAO(IContentDAO contentDao) {
		this._contentDao = contentDao;
	}
    
	@Override
	protected IEntitySearcherDAO getEntitySearcherDao() {
		return this.getWorkContentSearcherDAO();
	}
	
	@Override
	protected IEntityDAO getEntityDao() {
		return this.getContentDAO();
	}
	
	protected IWorkContentSearcherDAO getWorkContentSearcherDAO() {
		return _workContentSearcherDAO;
	}
	public void setWorkContentSearcherDAO(IWorkContentSearcherDAO workContentSearcherDAO) {
		this._workContentSearcherDAO = workContentSearcherDAO;
	}
	
	public IPublicContentSearcherDAO getPublicContentSearcherDAO() {
		return _publicContentSearcherDAO;
	}
	public void setPublicContentSearcherDAO(IPublicContentSearcherDAO publicContentSearcherDAO) {
		this._publicContentSearcherDAO = publicContentSearcherDAO;
	}
	
	@Override
	protected ICategoryManager getCategoryManager() {
		return (ICategoryManager) this.getService(SystemConstants.CATEGORY_MANAGER);
	}
	
	@Override
	public IApsEntity getEntity(String entityId) throws ApsSystemException {
		return this.loadContent(entityId, false);
	}
    
	/**
	 * @deprecated From jAPS 2.0 version 2.0.9, use getStatus()
	 */
    @Override
	public int getState() {
		return super.getStatus();
	}
    
	/**
	 * Map of the prototypes of the content types in the so called 'small form', indexed by
	 * the type code.
	 */
	private Map<String, SmallContentType> _smallContentTypes;
	
	private IContentDAO _contentDao;
	
	private IWorkContentSearcherDAO _workContentSearcherDAO;
	
	private IPublicContentSearcherDAO _publicContentSearcherDAO;
	
}
