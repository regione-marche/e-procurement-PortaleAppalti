package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.cie;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.BaseResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

public class CieLoginResponseAction extends BaseResponseAction implements ServletResponseAware {

	private static final long serialVersionUID = -508800390968455151L;

	/**
	 * inizializzazione dell'operazione di login al servizio SPID di Maggioli
	 */
	public String prepareLogin() {
		String target = SUCCESS;
		this.urlLogin = prepareCallbackLogin(
				"/do/SSO/CieLoginResponse.action",
				this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		if(this.urlLogin == null) {
			target = INPUT;
		}
		return target;
	}

	/**
	 * ...
	 * @param configManager 
	 */
	public static String prepareCallbackLogin(
			String callbackAction,
			ConfigInterface configManager, 
			IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager,
			WSAuthServiceSPIDWrapper wsAuthServiceSPID) 
	{
		String target = SUCCESS;
		String urlLogin = null; 

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try {
			String url = (String) appParamManager
					.getConfigurationValue(AppParamManager.CIE_WS_AUTHSERVICESPID_URL);
			String authSystem = CIE_AUTHSYSTEM_DEFAULT;
			String serviceProvider = (String) appParamManager
					.getConfigurationValue(AppParamManager.CIE_SERVICEPROVIDER);
			Integer serviceIndex = (Integer) appParamManager
					.getConfigurationValue(AppParamManager.CIE_SERVICEINDEX);
			String authLevel = (String) appParamManager
					.getConfigurationValue(AppParamManager.CIE_AUTHLEVEL);
			
			// valida i parametri...
			if (StringUtils.isEmpty(url)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.CIE_WS_AUTHSERVICESPID_URL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.CIE_WS_AUTHSERVICESPID_URL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(serviceProvider)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.CIE_SERVICEPROVIDER }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.CIE_SERVICEPROVIDER));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(authLevel)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.CIE_AUTHLEVEL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.CIE_AUTHLEVEL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}

			
			if (SUCCESS.equals(target)) {
				// imposta dinamicamente l'endpoint del proxy prima di
				// utilizzarne i servizi...
				wsAuthServiceSPID.getProxyWSAuthService().setEndpoint(url);

				String backUrl = getBackUrl(callbackAction, configManager);

				// richiedi il token temporaneo al sevizio SPID...
				// e salvalo in sessione per il login...
				String authId = authServiceSPIDManager.getAuthId();
				setAuthId(authId);

				// invia la richiesta di login al servizio SPID...
				int i = url.indexOf("/services/");
				url = (i > 0 ? url.substring(0, i) : url);

				urlLogin = url + "/auth.jsp" 
						+ "?backUrl=" + backUrl
						+ "&authSystem=" + authSystem 
						+ "&authId=" + authId
						+ "&serviceProvider=" + serviceProvider 
						+ "&serviceIndex=" + serviceIndex
						+ "&authLevel=" + SPID_AUTHLEVEL_URL + authLevel;
				
				target = SUCCESS;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli SPID" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}

		return urlLogin;
	}



}
