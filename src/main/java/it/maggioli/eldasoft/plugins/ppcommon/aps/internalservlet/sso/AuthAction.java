package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AuthAction  extends BaseAction   implements ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1403260265973778908L;

	private List<String> sistemiAutenticazione;
	
	private String urlLoginCohesion;
	
	private String urlLoginShibboleth;
	
	private IAppParamManager appParamManager;
	
	private IURLManager urlManager;
	
	private IPageManager pageManager;
	
	private HttpServletResponse response;
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public List<String> getSistemiAutenticazione() {
		return sistemiAutenticazione;
	}

	public void setSistemiAutenticazione(List<String> sistemiAutenticazione) {
		this.sistemiAutenticazione = sistemiAutenticazione;
	}
	
	public String getUrlLoginCohesion() {
		return urlLoginCohesion;
	}

	public void setUrlLoginCohesion(String urlLoginCohesion) {
		this.urlLoginCohesion = urlLoginCohesion;
	}

	public String getUrlLoginShibboleth() {
		return urlLoginShibboleth;
	}

	public void setUrlLoginShibboleth(String urlLoginShibboleth) {
		this.urlLoginShibboleth = urlLoginShibboleth;
	}

	public String view() {
		String target = SUCCESS;
		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try{
			Map<String,String> ordineAutenticazioni = new TreeMap<String, String>();
			// Carico la lista delle autenticazioni abilitate
			List<String> autenticazioniAbilitate = appParamManager.loadEnabledAuthentications();
			List<AppParam> parametriAutenticazione = appParamManager.loadEnabledAuthenticationsPositions(autenticazioniAbilitate);

			//Per ogni sistema di autenticazione abilitato guardo la posizione configurata nelle customizzazioni. Se non è configurata uso le posizioni di default.
			//La posizione è poi inserita in una TreeMap, che ha la peculiarità di ordinare le chiavi. Per evitare che le chiavi siano uguali e che quindi si sovrascrivano
			//i valori nella mappa la chiave viene composta da: "Posizione impostata sul portale" + "_" + "Posizione default" + "Sistema autenticazione".
			for(AppParam sistemaAuth : parametriAutenticazione){
				String value = sistemaAuth.getValue() == null || "".equals(sistemaAuth.getValue())  ? sistemaAuth.getDefaultValue().toString() : sistemaAuth.getValue().toString();
				String defaultValue = sistemaAuth.getDefaultValue().toString();
				//Levo dal nome del parametro la posizione, ottunuta dalla query
				String name = sistemaAuth.getName().replace(".position", "");
				ordineAutenticazioni.put(String.format("%03d", new Integer(value)) + "_" + String.format("%03d", new Integer(defaultValue)) + "_" + name , name);
			}
			
			this.urlLoginShibboleth = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.login.url");
			this.urlLoginCohesion = (String) appParamManager.getConfigurationValue("auth.sso.cohesion.login.url");
			
			// Prendo la lista di autenticazioni abilitate ordinate per posizione
			this.sistemiAutenticazione =  new ArrayList<String>(ordineAutenticazioni.values()); ;
		}catch (Exception e) {
			action.addActionError(action.getText("Errors.auth.generic", new String[] { "Maggioli Auth" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "Auth.jsp");
			target = INPUT;
		}
	
		return target;
	}
	
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);

		PageURL url = this.urlManager.createURL(reqCtx);

		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}

}
