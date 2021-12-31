package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.error;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionSupport;

public class PortalErrorPageAction extends BaseAction implements SessionAware, ServletResponseAware {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6600306442633875276L;
	private Map<String, Object> session;
	private HttpServletResponse response;
	private IPageManager pageManager;
	private IURLManager urlManager;

	private String urlRedirect;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setUrlManager(IURLManager urlManager){
		this.urlManager = urlManager;
	}

	/**
	 * @return the urlRedirect
	 */
	public String getUrlRedirect() {
		return urlRedirect;
	}

	public String openErrorPage(){
		ActionSupport azione = (ActionSupport)this.session.get("ACTION_OBJECT");

		this.setActionErrors(azione.getActionErrors());
		this.setActionMessages(azione.getActionMessages());
		this.setFieldErrors( azione.getFieldErrors());

		this.session.remove("ACTION_OBJECT");
		return "error";
	}

	public String openHomepage(){
		this.urlRedirect = this.getPageURL("homepage");
		return "redirect";
	}
	
	/**
	 * Genera la url per il redirezionamento ad una specifica pagina.
	 * @param page codice pagina
	 * @return url per la redirect alla pagina
	 */
	private String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

}
