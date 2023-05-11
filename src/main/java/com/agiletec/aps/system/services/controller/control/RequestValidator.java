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
package com.agiletec.aps.system.services.controller.control;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.PageUtils;

/**
 * Implementazione del un sottoservizio di controllo che
 * verifica la validità della richiesta del client.
 * Viene verificata la correttezza formale della richiesta tramite la
 * corrispondenza con una maschera. La richiesta deve essere nella forma:<br>
 * <code>/lingua/pagina.wp</code> oppure <code>/lingua/path_pagina/</code><br>
 * dove lingua è un codice lingua configurato, pagina una pagina del portale e 
 * path_pagina il path (stile breadcrumbs) della pagina.
 * Se la richiesta è valida, l'oggetto lingua e l'oggetto pagina
 * corrispondenti alla richiesta sono inseriti nel contesto di richiesta
 * sotto forma di extra parametri, con i nomi "currentLang" e "currentPage",
 * ed il metodo service restituisce Controller.CONTINUE.
 * Se la richiesta non è valida, viene restituito lo stato di errore.
 * @author 
 */
public class RequestValidator implements ControlServiceInterface {
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized");
	}
	
	/**
	 * Esecuzione. Le operazioni sono descritte nella documentazione della classe.
	 * @param reqCtx Il contesto di richiesta
	 * @param status Lo stato di uscita del servizio precedente
	 * @return Lo stato di uscita
	 */
	@Override
	public int service(RequestContext reqCtx, int status) {
		if (ApsSystemUtils.getLogger().isTraceEnabled()) {
			ApsSystemUtils.getLogger().trace(this.getClass().getName()+" invoked");
		}
		int retStatus = ControllerManager.INVALID_STATUS;
		// Se si è verificato un errore in un altro sottoservizio, termina subito
		if(status == ControllerManager.ERROR){
			return status;
		}
		try { // non devono essere rilanciate eccezioni
			boolean ok = this.isRightPath(reqCtx);
			if (ok) {
				retStatus = ControllerManager.CONTINUE;
			} else {
				retStatus = ControllerManager.ERROR;
				reqCtx.addExtraParam("errorType", "user");
				reqCtx.addExtraParam("errorCode", "invalidRequest");
			}
		} catch (Throwable e) {
			retStatus = ControllerManager.SYS_ERROR;
			ApsSystemUtils.logThrowable(e, this, "service", "Error while validating the client request");
		}
		return retStatus;
	}
	
	private boolean isRightPath(RequestContext reqCtx) {
		boolean ok = false;
		String resourcePath;
		Matcher matcher;
		Lang lang = null;
		IPage page = null;
		if (this.getResourcePath(reqCtx).equals("/pages")) {
			resourcePath = getFullResourcePath(reqCtx);
			matcher = this._patternFullPath.matcher(resourcePath);
			if (matcher.lookingAt()) {
				String sect1 = matcher.group(1);
				lang = getLangManager().getLang(sect1);
				page = this.getPage(matcher);
			}
		} else {
			resourcePath = getResourcePath(reqCtx);
			matcher = this._pattern.matcher(resourcePath);
			if (matcher.lookingAt()) {
				String sect1 = matcher.group(1);
				String sect2 = matcher.group(2);
				lang = getLangManager().getLang(sect1);
				page = this.getPageManager().getPage(sect2);
			}
		}
		if (lang != null && page != null) {
			reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, lang);
			reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, page);
			ok = true;
		}
		return ok;
	}
	
	/**
	 * Qualora si usasse il mapping /pages/*
	 * restituisce un'oggetto IPage solo nel caso
	 * in cui il path completo della pagina risulti corretto.
	 * Qualora il path sia di lunghezza pari a zero
	 * verrà restituita l'homepage.
	 * @param Matcher il matcher valorizzato come segue<br>
	 * matcher.group(1) -> lang_code<br>
	 * matcher.group(2) -> /paginaX/paginaY<br>
	 * matcher.group(3) -> /paginaY<br>
	 * @return un oggetto Page oppure null
	 */
	private IPage getPage(Matcher matcher) {
		IPage page = null;
		String rootCode = this.getPageManager().getRoot().getCode();
		String path = matcher.group(2);
		//Se il path è di tipo /it o /it/ o /it/homepage
		if(path.trim().length() == 0  || path.substring(1).equals(rootCode) ){
			return this.getPageManager().getRoot();
		}
		String pageCode = matcher.group(3).substring(1);
		IPage tempPage = this.getPageManager().getPage(pageCode);
		if (null != tempPage) {
			//la pagina esiste ed è di livello 1
			//if(tempPage.getParentCode().equals(rootCode)) return tempPage;
			//la pagina è di livello superiore al primo e il path è corretto
			String fullPath = matcher.group(2).substring(1).trim();
			String createdlFullPath = PageUtils.getFullPath(tempPage, "/").toString();
			if (null != tempPage && createdlFullPath.equals(fullPath) ){
				page = tempPage;
			}
		}
		return page;
	}

	/**
	 * Recupera il ServletPath richiesto dal client.
	 * @param reqCtx Il contesto di richiesta
	 * @return Il ServletPath
	 */
	protected String getResourcePath(RequestContext reqCtx) {
		String servletPath = reqCtx.getRequest().getServletPath();
		return servletPath;
	}
	
	protected String getFullResourcePath(RequestContext reqCtx) {
		String servletPath = this.getResourcePath(reqCtx) + reqCtx.getRequest().getPathInfo();
		return servletPath;
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	private ILangManager _langManager;
	private IPageManager _pageManager;
	
	protected Pattern _pattern = Pattern.compile("^/(\\w+)/(\\w+)\\Q.wp\\E");
	
	protected Pattern _patternFullPath = Pattern.compile("^/pages/(\\w+)((/\\w+)*)");
	
}
