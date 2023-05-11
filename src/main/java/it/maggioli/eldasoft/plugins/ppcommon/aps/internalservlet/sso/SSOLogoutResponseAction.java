package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.struts2.interceptor.ServletResponseAware;

//import com.agiletec.aps.system.RequestContext;
import com.agiletec.apsadmin.system.BaseAction;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class SSOLogoutResponseAction extends BaseAction { //implements ServletResponseAware{

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6400306168072330961L;

	private IAppParamManager appParamManager;

	//private HttpServletResponse _response;
	private String urlHome;

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @return the urlHome
	 */
	public String getUrlHome() {
		return urlHome;
	}

	/**
	 * @param urlHome the urlHome to set
	 */
	public void setUrlHome(String urlHome) {
		this.urlHome = urlHome;
	}

	public String logout(){
		String target = SUCCESS;

		HttpSession session = this.getRequest().getSession();
		boolean invalidaSessione = false;
		AccountSSO account = (AccountSSO)session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		
		if(account != null && 
			(PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO.equals(account.getTipologiaLogin())
			 || PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(account.getTipologiaLogin())
			 || PortGareSystemConstants.TIPOLOGIA_LOGIN_GATEWAY_SSO.equals(account.getTipologiaLogin())))
		{
			invalidaSessione = true;
			target = "homepage";
		} else {
			// CONTROLLO TRA GLI ALTRI SISTEMI DI SSO PER LA LOGOUT
			String propDefURL = null;
			if(account != null && PortGareSystemConstants.TIPOLOGIA_LOGIN_SHIBBOLETH_SSO.equals(account.getTipologiaLogin())){
				propDefURL = (String)appParamManager.getConfigurationValue("auth.sso.shibboleth.logout.url");
			}
			if(account != null && PortGareSystemConstants.TIPOLOGIA_LOGIN_COHESION_SSO.equals(account.getTipologiaLogin())){
				propDefURL = (String)appParamManager.getConfigurationValue("auth.sso.cohesion.logout.url");
			}
			if (propDefURL == null) {
				LOG.fatal("Property 'sso.logout.url' non valorizzata, disconnessione impossibile dal sistema di SSO");
			} else {
				String url = propDefURL;
				this.setUrlHome(url);
				if(((Integer)appParamManager.getConfigurationValue("auth.sso.sessionInvalidate")).intValue() > 0) {
					invalidaSessione = true;
					target = "homepage";
				}
			}
		}
		
		// se necessario, invalida la sessione utente
		if(invalidaSessione) {
			session.invalidate();
		}

		return target;
	}


}
