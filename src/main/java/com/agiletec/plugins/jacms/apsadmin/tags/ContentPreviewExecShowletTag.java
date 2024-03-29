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
package com.agiletec.plugins.jacms.apsadmin.tags;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.tags.ExecShowletTag;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;

/**
 * Tag per l'esecuzione preliminare delle showlet per la funzione di preview da redazione contenuti.
 * La classe tag deriva direttamente dalla classe utilizzata nel front-end di portale {@link ExecShowletTag} 
 * per le funzioni di costruzione pagine portale.
 * @author E.Santoboni
 */
public class ContentPreviewExecShowletTag extends ExecShowletTag {
	
	@Override
	protected void includeShowlet(RequestContext reqCtx, Showlet showlet) throws Throwable {
		Content contentOnSession = (Content) reqCtx.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
		if (contentOnSession!=null && showlet != null 
				&& "viewerConfig".equals(showlet.getType().getAction())) {
			IPage currentPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			if ((currentPage.getCode().equals(contentOnSession.getViewPage()) && (showlet.getConfig() == null || showlet.getConfig().size() == 0)) 
					|| (showlet.getPublishedContent() == null || showlet.getPublishedContent().equals(contentOnSession.getId()))) {
				String path = CONTENT_VIEWER_JSP;
				reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET, showlet);
				this.pageContext.include(path.toString());
				return;
			}
		}
		super.includeShowlet(reqCtx, showlet);
	}
	
	private final String CONTENT_VIEWER_JSP="/WEB-INF/plugins/jacms/apsadmin/jsp/content/preview/content_viewer.jsp";
	
}