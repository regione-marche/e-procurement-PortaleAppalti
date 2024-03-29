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

import java.util.List;

import com.agiletec.aps.system.common.entity.IEntityDAO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;

/**
 * Basic interface for the Data Access Objects for the 'content' objects. 
 * @author M.Diana - E.Santoboni - S.Didaci
 */
public interface IContentDAO extends IEntityDAO {
	
	/**
	 * Return a 'ContentRecordVO' (shortly: VO) containing the all content informations
	 * stored in the DB. 
	 * @param id The id of the requested content. 
	 * @return The VO object corresponding to the wanted content.
	 * @deprecated From jAPS 2.0 version 2.0.9, use loadEntityRecord
	 */
	public ContentRecordVO loadContentVO(String id);
	
	/**
	 * Add the record a the given content in the DB.
	 * @param content The content to insert.
	 * @deprecated From jAPS 2.0 version 2.0.9, use addEntity
	 */
	public void addContent(Content content);
	
	/**
	 * Updates the given content in the DB.
	 * @param content The content to update.
	 * @deprecated From jAPS 2.0 version 2.0.9, use updateEntity
	 */
	public void updateContent(Content content);
	
	/**
	 * Publish a content.
	 * @param content The content to publish.
	 */
	public void insertOnLineContent(Content content);
	
	/**
	 * Reload the references of a published content.
	 * @param content The published content.
	 */
	public void reloadPublicContentReferences(Content content);
	
	/**
	 * Reload the references of a content.
	 * @param content The content.
	 */
	public void reloadWorkContentReferences(Content content);
	
	/**
	 * Unpublish a content, preventing it from being displayed in the portal. Obviously
	 * the content itself is not deleted.
	 * @param content the content to unpublish.
	 */
	public void removeOnLineContent(Content content);
	
	/**
	 * Deletes a content from the DB.
	 * @param content The content to delete.
	 * @deprecated From jAPS 2.0 version 2.0.9, use deleteEntity
	 */
	public void deleteContent(Content content);
	
	/**
	 * Return the list of the contents IDs referenced by the specified group.
	 * @param groupName The name of the group.
	 * @return The list of the contents IDs referenced by the given group.
	 */
	public List<String> getGroupUtilizers(String groupName);
	
	/**
	 * Return the list of IDs of the contents which reference the specified page.
	 * @param pageCode The code of the page.
	 * @return The list of IDs of the contents which reference the specified page.
	 */
	public List<String> getPageUtilizers(String pageCode);
	
	/**
	 * Return the list of the content IDs which reference the specified content. 
	 * @param contentId The ID of the content.
	 * @return the list of the content IDs which reference the specified content.
	 */
	public List<String> getContentUtilizers(String contentId);
	
	/**
	 * Return the list of content IDs which reference the specified resource. 
	 * @param resourceId The id of the resource.
	 * @return The list of content IDs which reference the specified resource.
	 */
	public List<String> getResourceUtilizers(String resourceId);
	
	/**
	 * Return the list of the content ID which reference the specified category. 
	 * @param categoryCode The category code.
	 * @return The list of the content ID which reference the specified category.
	 */
	public List<String> getCategoryUtilizers(String categoryCode);
	
}
