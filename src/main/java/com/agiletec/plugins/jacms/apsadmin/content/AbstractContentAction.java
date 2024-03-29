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
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.util.SelectItem;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SmallContentType;
import com.agiletec.plugins.jacms.apsadmin.content.helper.IContentActionHelper;

/**
 * Action Astratta Base per la gestione contenuti.
 * @author E.Santoboni
 */
public abstract class AbstractContentAction extends BaseAction {
	
	/**
	 * Restituisce il contenuto vo in base all'identificativo.
	 * Metodo a servizio dell'interfaccia di gestione 
	 * contenuti singoli ed in lista.
	 * @param contentId L'identificativo del contenuto.
	 * @return Il contenuto vo cercato.
	 */
	public ContentRecordVO getContentVo(String contentId) {
		ContentRecordVO contentVo = null;
		try {
			contentVo = this.getContentManager().loadContentVO(contentId);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContentVo");
			throw new RuntimeException("Errore in caricamento contenuto vo", t);
		}
		return contentVo;
	}
	
	/**
	 * Verifica se l'utente corrente è abilitato all'accesso 
	 * del contenuto specificato.
	 * @param content Il contenuto su cui verificare il permesso di accesso.
	 * @return True se l'utente corrente è abilitato all'eccesso al contenuto,
	 * false in caso contrario.
	 */
	protected boolean isUserAllowed(Content content) {
		String group = content.getMainGroup();
		return this.isCurrentUserMemberOf(group);
	}
	
	/**
	 * Restituisce il contenuto in sesione.
	 * @return Il contenuto in sesione.
	 */
	public Content getContent() {
		return (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
	}
	
	protected Content updateContentOnSession() {
		Content content = this.getContent();
		this.getContentActionHelper().updateEntity(content, this.getRequest());
		return content;
	}
	
	/**
	 * Restituisce la lista di contenuti (in forma small) definiti nel sistema.
	 * Il metodo è a servizio delle jsp che richiedono questo dato per fornire 
	 * una corretta visualizzazione della pagina.
	 * @return La lista di tipi di contenuto (in forma small) definiti nel sistema.
	 */
	public List<SmallContentType> getContentTypes() {
		return this.getContentManager().getSmallContentTypes();
	}
	
	/**
	 * Restituisce la lista di stati di contenuto definiti nel sistema.
	 * Il metodo è a servizio delle jsp che richiedono questo dato per fornire 
	 * una corretta visualizzazione della pagina.
	 * @return La lista di stati di contenuto definiti nel sistema.
	 * @deprecated use getAvalaibleStatus()
	 */
	public String[] getStatesList() {
		return Content.AVAILABLE_STATUS;
	}
	
	/**
	 * Restituisce la lista di stati di contenuto definiti nel sistema, come insieme di chiave e valore
	 * Il metodo è a servizio delle jsp che richiedono questo dato per fornire 
	 * una corretta visualizzazione della pagina.
	 * @return La lista di stati di contenuto definiti nel sistema.
	 */
	public List<SelectItem> getAvalaibleStatus() {
		String[] status = Content.AVAILABLE_STATUS;
		List<SelectItem> items = new ArrayList<SelectItem>(status.length);
		for (int i = 0; i < status.length; i++) {
			//TODO DA VALIDARE LABEL
			SelectItem item = new SelectItem(status[i], "name.contentStatus." + status[i]);
			items.add(item);
		}
		return items;
	}
	
	/**
	 * Restituisce la lista di lingue definite nel sistema.
	 * Il metodo è a servizio delle jsp che richiedono questo dato per fornire 
	 * una corretta visualizzazione della pagina.
	 * @return La lista di lingue definite nel sistema.
	 */
	public List<Lang> getLangs() {
		return this.getLangManager().getLangs();
	}
	
	/**
	 * Restituisce il manager gestore delle operazioni sui contenuti.
	 * @return Il manager gestore delle operazioni sui contenuti.
	 */
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	
	/**
	 * Setta il manager gestore delle operazioni sui contenuti.
	 * @param contentManager Il manager gestore delle operazioni sui contenuti.
	 */
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	/**
	 * Restituisce la classe helper della gestione contenuti.
	 * @return La classe helper della gestione contenuti.
	 */
	protected IContentActionHelper getContentActionHelper() {
		return _contentActionHelper;
	}
	
	/**
	 * Setta la classe helper della gestione contenuti.
	 * @param contentActionHelper La classe helper della gestione contenuti.
	 */
	public void setContentActionHelper(IContentActionHelper contentActionHelper) {
		this._contentActionHelper = contentActionHelper;
	}
	
	/**
	 * Restituisce la mappa dei gruppi presenti nel sistema. 
	 * La mappa è indicizzata in base al nome del gruppo.
	 * @return La mappa dei gruppi presenti nel sistema.
	 * @deprecated
	 */
	public Map<String, Group> getGroupsMap() {
		return this.getGroupManager().getGroupsMap();
	}
	
	/**
	 * Restituisce un gruppo in base al nome.
	 * @param groupName Il nome del gruppo da restituire.
	 * @return Il gruppo cercato.
	 */
	public Group getGroup(String groupName) {
		return this.getGroupManager().getGroup(groupName);
	}
	
	/**
	 * Restituisce un tipo di contenuto in forma small.
	 * @param typeCode Il codice del tipo di contenuto.
	 * @return Il tipo di contenuto (in forma small) cercato.
	 */
	public SmallContentType getSmallContentType(String typeCode) {
		Map<String, SmallContentType> smallContentTypes = this.getContentManager().getSmallContentTypesMap();
		return (SmallContentType) smallContentTypes.get(typeCode);
	}
	
	public List<Group> getAllowedGroups() {
		return this.getContentActionHelper().getAllowedGroups(this.getCurrentUser());
	}
	
	/**
	 * Restituisce la lista ordinata dei gruppi presenti nel sistema.
	 * @return La lista dei gruppi presenti nel sistema.
	 */
	public List<Group> getGroups() {
		return this.getGroupManager().getGroups();
	}
	
	/**
	 * Restituisce il manager dei gruppi.
	 * @return Il manager dei gruppi.
	 */
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	
	/**
	 * Setta il manager dei gruppi.
	 * @param groupManager Il manager dei gruppi.
	 */
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	
	private IContentManager _contentManager;
	private IContentActionHelper _contentActionHelper;
	
	private IGroupManager _groupManager;
	
}
