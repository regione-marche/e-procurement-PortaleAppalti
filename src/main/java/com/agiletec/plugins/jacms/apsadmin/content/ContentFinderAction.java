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
package com.agiletec.plugins.jacms.apsadmin.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * Action per la ricerca contenuti.
 * @author E.Santoboni
 */
public class ContentFinderAction extends AbstractContentAction implements IContentFinderAction {
	
	@Override
	public List<String> getContents() {
		List<String> result = null;
		try {
			List<String> allowedGroups = this.getContentGroupCodes();
			result = this.getContentManager().loadWorkContentsId(this.createFilters(), allowedGroups);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContents");
			throw new RuntimeException("Errore in ricerca contenuti", t);
		}
		return result;
	}
	
	/**
	 * Restituisce la lista di gruppi (codici) dei contenuti che devono essere visualizzati in lista.
	 * La lista viene ricavata in base alle autorizzazioni dall'utente corrente.
	 * @return La lista di gruppi cercata.
	 */
	protected List<String> getContentGroupCodes() {
		List<String> allowedGroups = new ArrayList<String>();
		List<Group> userGroups = this.getContentActionHelper().getAllowedGroups(this.getCurrentUser());
		Iterator<Group> iter = userGroups.iterator();
    	while (iter.hasNext()) {
    		Group group = iter.next();
    		allowedGroups.add(group.getName());
    	}
    	return allowedGroups;
	}
	
	/**
	 * Restitusce i filtri per la selezione e l'ordinamento dei contenuti erogati nell'interfaccia.
	 * @return Il filtri di selezione ed ordinamento dei contenuti.
	 */
	protected EntitySearchFilter[] createFilters() {
		EntitySearchFilter[] filters = new EntitySearchFilter[0];
		if (null != this.getContentType() && this.getContentType().trim().length()>0) {
			EntitySearchFilter filterToAdd = new EntitySearchFilter(IContentManager.ENTITY_TYPE_CODE_FILTER_KEY, false, this.getContentType(), false);
			filters = this.addFilter(filters, filterToAdd);
		}
		if (null != this.getState() && this.getState().trim().length()>0) {
			EntitySearchFilter filterToAdd = new EntitySearchFilter(IContentManager.CONTENT_STATUS_FILTER_KEY, false, this.getState(), false);
			filters = this.addFilter(filters, filterToAdd);
		}
		if (null != this.getText() && this.getText().trim().length()>0) {
			EntitySearchFilter filterToAdd = new EntitySearchFilter(IContentManager.CONTENT_DESCR_FILTER_KEY, false, this.getText(), true);
			filters = this.addFilter(filters, filterToAdd);
		}
		if (null != this.getOnLineState() && this.getOnLineState().trim().length()>0) {
			EntitySearchFilter filterToAdd = new EntitySearchFilter(IContentManager.CONTENT_ONLINE_FILTER_KEY, false);
			filterToAdd.setNullOption(this.getOnLineState().trim().equals("no"));
			filters = this.addFilter(filters, filterToAdd);
		}
		if (null != this.getContentIdToken() && this.getContentIdToken().trim().length()>0) {
			EntitySearchFilter filterToAdd = new EntitySearchFilter(IContentManager.ENTITY_ID_FILTER_KEY, false, this.getContentIdToken(), true);
			filters = this.addFilter(filters, filterToAdd);
		}
		EntitySearchFilter orderFilter = this.getContentActionHelper().getOrderFilter(this.getLastGroupBy(), this.getLastOrder());
		filters = this.addFilter(filters, orderFilter);
		return filters;
	}
	
	private EntitySearchFilter[] addFilter(EntitySearchFilter[] filters, EntitySearchFilter filterToAdd){
		int len = filters.length;
		EntitySearchFilter[] newFilters = new EntitySearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = filters[i];
		}
		newFilters[len] = filterToAdd;
		return newFilters;
	}
	
	public String changeOrder() {
		try {
			if (null == this.getGroupBy()) return SUCCESS;
			if (this.getGroupBy().equals(this.getLastGroupBy())) {
				boolean condition = (null == this.getLastOrder() || this.getLastOrder().equals(EntitySearchFilter.ASC_ORDER));
				String order = (condition ? EntitySearchFilter.DESC_ORDER : EntitySearchFilter.ASC_ORDER);
				this.setLastOrder(order);
			} else {
				this.setLastOrder(EntitySearchFilter.DESC_ORDER);
			}
			this.setLastGroupBy(this.getGroupBy());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "changeOrder");
			throw new RuntimeException("Errore in cambiamento ordinamento", t);
		}
		return SUCCESS;
	}
	
	public String insertOnLine() {
		try {
			if (null == this.getContentIds()) return SUCCESS;
			Iterator<String> iter = this.getContentIds().iterator();
			List<Content> publishedContents = new ArrayList<Content>();
			while (iter.hasNext()) {
				String contentId = (String) iter.next();
				Content contentToPublish = this.getContentManager().loadContent(contentId, false);
				String[] msgArg = new String[1];
				if (null == contentToPublish) {
					//TODO RIVISITARE LABEL
					msgArg[0] = contentId;
					this.addActionError(this.getText("Message.Content.nullContent", msgArg));
					continue;
				}
				msgArg[0] = contentToPublish.getDescr();
				if (!this.isUserAllowed(contentToPublish)) {
					//TODO RIVISITARE LABEL
					this.addActionError(this.getText("Message.Content.userNotAllowedToPublishContent", msgArg));
					continue;
				}
				this.getContentActionHelper().scanEntity(contentToPublish, this);
				if (this.getFieldErrors().size()>0) {
					//TODO RIVISITARE LABEL
					this.addActionError(this.getText("Message.Content.publishingContentWithErrors", msgArg));
					continue;
				}
				this.getContentManager().insertOnLineContent(contentToPublish);
				ApsSystemUtils.getLogger().info("Pubblicato contenuto " + contentToPublish.getId() 
						+ " da Utente " + this.getCurrentUser().getUsername());
				publishedContents.add(contentToPublish);
			}
			//TODO RIVISITARE LABEL e LOGICA DI COSTRUZIONE LABEL
			this.addConfirmMessage("Message.Content.publishedContents", publishedContents);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "insertOnLine");
			throw new RuntimeException("Errore in inserimento contenuti online", t);
		}
		return SUCCESS;
	}
	
	public String removeOnLine() {
		try {
			if (null == this.getContentIds()) return SUCCESS;
			Iterator<String> contentsIdsItr = this.getContentIds().iterator();
			List<Content> removedContents = new ArrayList<Content>();
			while (contentsIdsItr.hasNext()) {
			String contentId = (String) contentsIdsItr.next();
				Content contentToSuspend = this.getContentManager().loadContent(contentId, false);
				String[] msgArg = new String[1];					
				if (null == contentToSuspend) {
					//TODO RIVISITARE LABEL
					msgArg[0] = contentId;
					this.addActionError(this.getText("Message.Content.nullContent", msgArg));
					continue;
				}
				msgArg[0] = contentToSuspend.getDescr();
				if (!this.isUserAllowed(contentToSuspend)) {
					//TODO RIVISITARE LABEL
					this.addActionError(this.getText("Message.Content.userNotAllowedToSuspendContent", msgArg));
					continue;
				}
				Map references = this.getContentActionHelper().getReferencingObjects(contentToSuspend, this.getRequest());
				if (references.size()>0) {
					//TODO RIVISITARE LABEL
					this.addActionError(this.getText("Message.Content.suspendingContentWithReferences", msgArg));
					continue;
				}
				this.getContentManager().removeOnLineContent(contentToSuspend);
				ApsSystemUtils.getLogger().info("Sospeso contenuto " + contentToSuspend.getId() 
						+ " da Utente " + this.getCurrentUser().getUsername());
				removedContents.add(contentToSuspend);
			}
			//TODO RIVISITARE LABEL e LOGICA DI COSTRUZIONE LABEL
			this.addConfirmMessage("Message.Content.suspendedContents", removedContents);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeOnLine");
			throw new RuntimeException("Errore in sospensione contenuti online", t);
		}
		return SUCCESS;
	}
	
	
	/**
	 * We've moved to deletion check here in the 'trash' action so to have errors notified immediately. Be design we
	 * share all the messages with the 'delete' action.
	 * @return the result code of the action: "success" if all the contents can be deleted, "cannotProceed" if blocking errors are detected
	 */
	@Override
	public String trash() {
		// TODO STABILIRE SE DOVREBBE COMPARIRE MESSAGGIO D'ERRORE, PER ADESSO MANTENIAMO INALTERATO IL COMPORTAMENTO PRECEDENTE
		if (null == this.getContentIds()) {
			//TODO METTERE MESSAGGIO
			return SUCCESS;
		}
		try {
			Iterator<String> itr = this.getContentIds().iterator();
			while (itr.hasNext()) {
				String currentContentId = itr.next();
				String msgArg[] = new String[1];
				Content contentToTrash = this.getContentManager().loadContent(currentContentId, false);
				if (null == contentToTrash) {
					msgArg[0] = currentContentId;
					this.addActionError(this.getText("Message.Content.contentToDeleteNull", msgArg));
					continue;
				} 
				msgArg[0] = contentToTrash.getDescr();			
				if (!this.isUserAllowed(contentToTrash)) {
					this.addActionError(this.getText("Message.Content.userNotAllowedToContentToDelete", msgArg));
					continue;
				}
				if (contentToTrash.isOnLine()) {
					this.addActionError(this.getText("Message.Content.notAllowedToDeleteOnlineContent", msgArg));
					continue;
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			throw new RuntimeException("Errore in cancellazione contenuti", t);
		}
		if (this.getActionErrors().isEmpty()) return SUCCESS;
		return "cannotProceed";
	}
	
	public String delete() {
		try {
			if (null == this.getContentIds()) return SUCCESS;
			Iterator<String> iter = this.getContentIds().iterator();
			List<Content> deletedContents = new ArrayList<Content>();
			while (iter.hasNext()) {
				String contentId = (String) iter.next();
				Content contentToDelete = this.getContentManager().loadContent(contentId, false);
				String[] msgArg = new String[1];
				if (null == contentToDelete) {
					//TODO RIVISITARE LABEL
					msgArg[0] = contentId;
					this.addActionError(this.getText("Message.Content.contentToDeleteNull", msgArg));
					continue;
				} 
				msgArg[0] = contentToDelete.getDescr();
				if (!this.isUserAllowed(contentToDelete)) {
					//TODO RIVISITARE LABEL
					this.addActionError(this.getText("Message.Content.userNotAllowedToContentToDelete", msgArg));
					continue;
				}
				if (contentToDelete.isOnLine()) {
					//TODO RIVISITARE LABEL
					this.addActionError(this.getText("Message.Content.notAllowedToDeleteOnlineContent", msgArg));
					continue;
				}
				this.getContentManager().deleteContent(contentToDelete);
				ApsSystemUtils.getLogger().info("Cancellato contenuto " + contentToDelete.getId() 
						+ " da Utente " + this.getCurrentUser().getUsername());
				deletedContents.add(contentToDelete);
			}
			//TODO RIVISITARE LABEL e LOGICA DI COSTRUZIONE LABEL
			this.addConfirmMessage("Message.Content.deletedContents", deletedContents);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			throw new RuntimeException("Errore in cancellazione contenuti", t);
		}
		return SUCCESS;
	}
	
	private void addConfirmMessage(String key, List<Content> deletedContents) {
		if (deletedContents.size()>0) {
			//TODO RIVISITARE LOGICA DI COSTRUZIONE MESSAGGIO
			String confirm = this.getText(key);
			for (int i=0; i<deletedContents.size(); i++) {
				Content content = deletedContents.get(i);
				if (i>0) confirm += " - ";
				confirm += " '" + content.getDescr() + "'";
			}
			this.addActionMessage(confirm);
		}
	}
	
	public String getContentType() {
		return _contentType;
	}
	public void setContentType(String contentType) {
		this._contentType = contentType;
	}
	
	public String getOnLineState() {
		return _onLineState;
	}
	public void setOnLineState(String onLineState) {
		this._onLineState = onLineState;
	}
	
	public String getState() {
		return _state;
	}
	public void setState(String state) {
		this._state = state;
	}
	
	public String getText() {
		return _text;
	}
	public void setText(String text) {
		this._text = text;
	}
	
	public String getLastOrder() {
		return _lastOrder;
	}
	public void setLastOrder(String order) {
		this._lastOrder = order;
	}
	
	public String getLastGroupBy() {
		return _lastGroupBy;
	}
	public void setLastGroupBy(String lastGroupBy) {
		this._lastGroupBy = lastGroupBy;
	}
	
	public String getGroupBy() {
		return _groupBy;
	}
	public void setGroupBy(String groupBy) {
		this._groupBy = groupBy;
	}
	
	public boolean isViewCode() {
		return _viewCode;
	}
	public void setViewCode(boolean viewCode) {
		this._viewCode = viewCode;
	}
	
	public boolean isViewStatus() {
		return _viewStatus;
	}
	public void setViewStatus(boolean viewStatus) {
		this._viewStatus = viewStatus;
	}
	
	public boolean isViewCreationDate() {
		return _viewCreationDate;
	}
	public void setViewCreationDate(boolean viewCreationDate) {
		this._viewCreationDate = viewCreationDate;
	}
	
	public boolean getViewGroup() {
		return _viewGroup;
	}
	public void setViewGroup(boolean viewGroup) {
		this._viewGroup = viewGroup;
	}
	
	public boolean getViewTypeDescr() {
		return _viewTypeDescr;
	}
	public void setViewTypeDescr(boolean viewTypeDescr) {
		this._viewTypeDescr = viewTypeDescr;
	}
	
	public Set<String> getContentIds() {
		return _contentIds;
	}
	public void setContentIds(Set<String> contentIds) {
		this._contentIds = contentIds;
	}

	public void setContentIdToken(String contentIdToken) {
		this._contentIdToken = contentIdToken;
	}
	public String getContentIdToken() {
		return _contentIdToken;
	}


	private String _contentType = "";
	private String _state = "";
	private String _text = "";
	private String _onLineState = "";
	private String _contentIdToken = "";
	
	private String _lastOrder;
	private String _lastGroupBy;
	private String _groupBy;
	
	private boolean _viewCode;
	private boolean _viewGroup;
	private boolean _viewStatus;
	private boolean _viewTypeDescr;
	private boolean _viewCreationDate;
	
	private Set<String> _contentIds;
	
}