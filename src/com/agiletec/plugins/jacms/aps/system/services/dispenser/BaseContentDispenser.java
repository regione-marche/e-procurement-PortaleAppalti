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
package com.agiletec.plugins.jacms.aps.system.services.dispenser;

import java.util.Iterator;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.util.EntityAttributeIterator;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.cache.ICacheManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.AbstractResourceAttribute;
import com.agiletec.plugins.jacms.aps.system.services.linkresolver.ILinkResolverManager;
import com.agiletec.plugins.jacms.aps.system.services.renderer.IContentRenderer;

/**
 * Fornisce i contenuti formattati.
 * Il compito del servizio, in fase di richiesta di un contenuto formattato, è quello di 
 * controllare preliminarmente le autorizzazzioni dell'utente corrente all'accesso al contenuto;
 * successivamente (in caso di autorizzazioni valide) restituisce il contenuto formattato.
 * @author 
 */
public class BaseContentDispenser extends AbstractService implements IContentDispenser {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": inizializzato ");
	}
	
	@Override
	public String getRenderedContent(String contentId, long modelId, String langCode, RequestContext reqCtx) {
		Content contentToRender = null;
		ContentAuthorizationInfo authInfo = this.getAuthorizationInfo(contentToRender, contentId);
		if (authInfo == null) return "";
		String renderedContent = this.getRenderedContent(authInfo, contentToRender, contentId, modelId, langCode, reqCtx);
		return renderedContent;
	}
	
	/**
	 * Carica le informazioni di autorizzazione sul contenuto.
	 * @param content Il codice del contenuto del quale restituire le informazioni di autorizzazione. 
	 * @param contentId Il codice del contenuto del quale restituire le informazioni di autorizzazione. 
	 * Il parametro viene utilizzato nel caso il parametro contenuto sia nullo.
	 * @return Le informazioni di autorizzazione sul contenuto.
	 */
	protected ContentAuthorizationInfo getAuthorizationInfo(Content content, String contentId) {
		String authorizationCacheKey = ContentManager.getContentAuthInfoCacheKey(contentId);
		ContentAuthorizationInfo authInfo = null;
		if (null != this.getCacheManager()) {
			Object authInfoTemp = this.getCacheManager().getFromCache(authorizationCacheKey);
			if (authInfoTemp instanceof ContentAuthorizationInfo) {
				authInfo = (ContentAuthorizationInfo) authInfoTemp;
			}
		}
		if (null == authInfo) {
			try {
				if (null == content) {
					content = this.getContentManager().loadContent(contentId, true);
				}
				if (content != null) {
					authInfo = this.buildContentAuthorizationInfo(content);
				}
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().error(this.getClass().getName(), "getRenderedContent", t);
			}
			if (authInfo != null) {
				if (null != this.getCacheManager()) {
					this.getCacheManager().putInCache(authorizationCacheKey, authInfo);
				}
			} else {
				ApsSystemUtils.getLogger().warn("Impossibile fornire il contenuto " + contentId);
			}
		}
		return authInfo;
	}
	
	/**
	 * Costruisce le informazioni di autorizzazione in base ai dati del contenuto.
	 * Metodo richiamato in occasione della prima richiesta del contenuto renderizzato, 
	 * indipendentemente dalla lingua e dal modello di renderizzazione.
	 * @param content Il contenuto dal quale ricavare le info sulle autorizzazioni.
	 * @return Le informazioni di autorizzazione richiesta.
	 */
	private ContentAuthorizationInfo buildContentAuthorizationInfo(Content content) {
		ContentAuthorizationInfo authInfo = new ContentAuthorizationInfo();
		String[] allowedGroups = new String[1+content.getGroups().size()];
		allowedGroups[0] = content.getMainGroup();
		int index = 1;
		Iterator<String> iterGroup = content.getGroups().iterator();
		while (iterGroup.hasNext()) {
			allowedGroups[index++] = iterGroup.next();
		}
		authInfo.setAllowedGroups(allowedGroups);
		EntityAttributeIterator attributeIter = new EntityAttributeIterator(content);
		while (attributeIter.hasNext()) {
			AttributeInterface currAttribute = (AttributeInterface) attributeIter.next();
			if (currAttribute instanceof AbstractResourceAttribute) {
				AbstractResourceAttribute abstrResAttr = (AbstractResourceAttribute) currAttribute;
				if (abstrResAttr.getResource() != null) {
					authInfo.addProtectedResourceId(abstrResAttr.getResource().getId());
				}
			}
		}
		return authInfo;
	}
	
	/**
	 * Restituisce il contenuto renderizzato.
	 * @param authInfo Le informazioni di autorizzazione sul contenuto.
	 * @param contentToRender Il contenuto da renderizzare.
	 * @param contentId L'Identificativo del contenuto da renderizzare. 
	 * Il parametro viene utilizzato nel caso il parametro contenuto sia nullo.
	 * @param modelId Identificatore del modello di contenuto.
	 * @param langCode Codice della lingua di renderizzazione richiesta.
	 * @param reqCtx Il contesto della richiesta.
	 * @return Il contenuto renderizzato.
	 */
	protected String getRenderedContent(ContentAuthorizationInfo authInfo, Content contentToRender, String contentId, long modelId, String langCode, RequestContext reqCtx) {
		String renderedContent = null;
		UserDetails currentUser = (UserDetails) reqCtx.getRequest().getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		List<Group> userGroups = this.getAuthorizationManager().getGroupsOfUser(currentUser);
		//verifica autorizzazione
		if (authInfo.isUserAllowed(userGroups)) {
			//ricerca contenuto renderizzato
			String cacheKey = ContentManager.getRenderedContentCacheKey(contentId, modelId, langCode);
			if (null != this.getCacheManager()) {
				renderedContent = (String) this.getCacheManager().getFromCache(cacheKey);
			}
			if (null == renderedContent) {
				boolean ok = false;
				try {
					if (contentToRender == null) {
						contentToRender = this.getContentManager().loadContent(contentId, true);
					}
					if (contentToRender != null) {
						renderedContent = this.getContentRender().render(contentToRender, modelId, langCode, reqCtx);
						ok = true;
					}
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error(this.getClass().getName(), "getRenderedContent", t);
				}
				if (ok) {
					if (null != this.getCacheManager()) {
						String modelGroupId = JacmsSystemConstants.CACHE_GROUP_MODEL_PREFIX + modelId;
						String[] groups = { modelGroupId };
						this.getCacheManager().putInCache(cacheKey, renderedContent, groups);
					}
				} else {
					ApsSystemUtils.getLogger().warn("Impossibile fornire il contenuto " + contentId);
					return "";
				}
			}
		} else {
			renderedContent = "Current user '" + currentUser.getUsername() + "' can't view this content";
		}
		renderedContent = _linkResolver.resolveLinks(renderedContent, reqCtx);
		return renderedContent;
	}
	
	protected ICacheManager getCacheManager() {
		return _cacheManager;
	}
	public void setCacheManager(ICacheManager manager) {
		this._cacheManager = manager;
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager manager) {
		this._contentManager = manager;
	}
	
	protected IContentRenderer getContentRender() {
		return _contentRenderer;
	}
	public void setContentRenderer(IContentRenderer renderer) {
		this._contentRenderer = renderer;
	}
	
	protected ILinkResolverManager getLinkResolverManager() {
		return _linkResolver;
	}
	public void setLinkResolver(ILinkResolverManager resolver) {
		this._linkResolver = resolver;
	}
	
	protected IAuthorizationManager getAuthorizationManager() {
		return _authorizationManager;
	}
	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}
	
	private IContentRenderer _contentRenderer;
	private IContentManager _contentManager;
	private ILinkResolverManager _linkResolver;
	private ICacheManager _cacheManager;
	private IAuthorizationManager _authorizationManager;
	
}
