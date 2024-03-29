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
package com.agiletec.plugins.jacms.aps.system.services.content.showlet;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.tags.util.HeadInfoContainer;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.IContentDispenser;

/**
 * Classe helper per la showlet di erogazione contenuti singoli.
 * @author W.Ambu - E.Santoboni
 */
public class ContentViewerHelper implements IContentViewerHelper {
    
	/**
     * Restituisce il contenuto da visualizzare nella showlet.
     * @param contentId L'identificativo del contenuto ricavato dal tag.
     * @param modelId Il modello del contenuto ricavato dal tag.
     * @param reqCtx Il contesto della richiesta.
     * @return Il contenuto da visualizzare nella showlet.
     * @throws ApsSystemException
     */
	public String getRenderedContent(String contentId, String modelId, RequestContext reqCtx) throws ApsSystemException {
		String renderedContent = "";
        try {
            Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            String langCode = currentLang.getCode();
            Showlet showlet = (Showlet) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
            ApsProperties showletConfig = showlet.getConfig();
            contentId = this.extractContentId(contentId, showletConfig, reqCtx);
            modelId = this.extractModelId(contentId, modelId, showletConfig, reqCtx);
            if (contentId != null && modelId != null) {   
 	            long longModelId = new Long(modelId).longValue();
	            this.setStylesheet(longModelId, reqCtx);
	            renderedContent = this.getContentDispenser().getRenderedContent(contentId, 
	            		longModelId, langCode, reqCtx);
            } else {
            	ApsSystemUtils.getLogger().warn("Parametri visualizzazione contenuto incompleti: " +
            			"contenuto=" + contentId + " modello=" + modelId);				
            }
        } catch (Throwable t) {
        	ApsSystemUtils.logThrowable(t, this, "getRenderedContent");
    		throw new ApsSystemException("Errore in fase di preparazione del contenuto", t);
    	}
        return renderedContent;
    }
	
	/**
	 * Metodo che determina con che ordine viene ricercato l'identificativo del contenuto.
	 * L'ordine con cui viene cercato è questo:
	 * 1) Nel parametro specificato all'interno del tag.
	 * 2) Tra i parametri di configurazione della showlet
	 * 3) Nella Request.
	 * @param contentId L'identificativo del contenuto specificato nel tag. 
	 * Può essere null o una Stringa alfanumerica.
	 * @param showletConfig I parametri di configurazione della showlet corrente.
	 * @param reqCtx Il contesto della richiesta.
	 * @return L'identificativo del contenuto da erogare.
	 */
	protected String extractContentId(String contentId, ApsProperties showletConfig, RequestContext reqCtx) {
		if (null == contentId) {
			if (null != showletConfig) {
				contentId = (String) showletConfig.get("contentId");
			}
			if (null == contentId) {
				contentId = reqCtx.getRequest().getParameter(SystemConstants.K_CONTENT_ID_PARAM);
			}
		}
		return contentId;
	}
	
	/**
	 * Restituisce l'identificativo del modello 
	 * con il quale renderizzare il contenuto.
	 * Metodo che determina con che ordine viene ricercato 
	 * l'identificativo del modello di contenuto.
	 * L'ordine con cui viene cercato è questo:
	 * 1) Nel parametro specificato all'interno del tag.
	 * 2) Tra i parametri di configurazione della showlet
	 * Nel caso non venga trovato nessun ideentificativo, viene restituito l'identificativo 
	 * del modello di default specificato nella configurazione del tipo di contenuto.
	 * @param contentId L'identificativo del contenuto da erogare.
	 * Può essere null, un numero in forma di stringa, o un'identificativo della tipologia del modello 'list' 
	 * (in tal caso viene restituito il modello per le liste definito nella configurazione del tipo di contenuto) 
	 * o 'default' (in tal caso viene restituito il modello di default definito nella configurazione del tipo di contenuto).
	 * @param modelId L'identificativo del modello specificato nel tag. Può essere null.
	 * @param showletConfig La configurazione della showlet corrente 
	 * nel qual è inserito il tag erogatore del contenuti.
	 * @param reqCtx Il contesto della richiesta.
	 * @return L'identificativo del modello 
	 * con il quale renderizzare il contenuto.
	 */
	protected String extractModelId(String contentId, String modelId, ApsProperties showletConfig, RequestContext reqCtx) {
		modelId = this.extractConfiguredModelId(contentId, modelId, showletConfig);
		if (null == modelId) {
			modelId = reqCtx.getRequest().getParameter("modelId");
		}
		if (null == modelId && null != contentId) {
			modelId = this.getContentManager().getDefaultModel(contentId);
		}
		return modelId;
	}
	
	protected String extractModelId(String contentId, String modelId, ApsProperties showletConfig) {
		modelId = this.extractConfiguredModelId(contentId, modelId, showletConfig);
		if (null == modelId && null != contentId) {
			modelId = this.getContentManager().getDefaultModel(contentId);
		}
		return modelId;
	}
	
	private String extractConfiguredModelId(String contentId, String modelId, ApsProperties showletConfig) {
		if (null != modelId && null != contentId) {
			if (modelId.equals("list")) {
				modelId = this.getContentManager().getListModel(contentId);
			}
			if (modelId.equals("default")) {
				modelId = this.getContentManager().getDefaultModel(contentId);
			}
		}
		if (null == modelId && null != showletConfig) {
			modelId = (String) showletConfig.get("modelId");
		}
		return modelId;
	}
    
	protected void setStylesheet(long modelId, RequestContext reqCtx) {
		ContentModel model = this.getContentModelManager().getContentModel(modelId);
	    if (model != null) {
	    	String stylesheet = model.getStylesheet();
	    	if (null != stylesheet && stylesheet.trim().length()>0) {
	    		HeadInfoContainer headInfo  = (HeadInfoContainer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_HEAD_INFO_CONTAINER);
	    		if (headInfo != null) {
	    			headInfo.addInfo("CSS", stylesheet);
	    		}
	    	}
	    }
    }
	
	protected IContentModelManager getContentModelManager() {
		return _contentModelManager;
	}
	public void setContentModelManager(IContentModelManager contentModelManager) {
		this._contentModelManager = contentModelManager;
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	protected IContentDispenser getContentDispenser() {
		return _contentDispenser;
	}
	public void setContentDispenser(IContentDispenser contentDispenser) {
		this._contentDispenser = contentDispenser;
	}
	
	private IContentModelManager _contentModelManager;
    private IContentManager _contentManager;
    private IContentDispenser _contentDispenser;
	
}