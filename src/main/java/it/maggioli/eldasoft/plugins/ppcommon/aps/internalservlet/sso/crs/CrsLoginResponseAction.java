package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.crs;

import org.apache.commons.lang.StringUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.BaseResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

public class CrsLoginResponseAction extends BaseResponseAction {
	private static final long serialVersionUID = -508800390968455151L;


	/**
	 * inizializzazione dell'operazione di login al servizio SPID di Maggioli
	 */
	public String prepareLogin() {
		String target = SUCCESS;
		this.urlLogin = prepareCallbackLogin(
				"/do/SSO/CrsLoginResponse.action",
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
					.getConfigurationValue(AppParamManager.CRS_WS_AUTHSERVICECRS_URL);
			String authSystem = CRS_AUTHSYSTEM_DEFAULT;
			
			// valida i parametri...
			if (StringUtils.isEmpty(url)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.CRS_WS_AUTHSERVICECRS_URL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.CRS_WS_AUTHSERVICECRS_URL));
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
						+ "&authId=" + authId;
				target = SUCCESS;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli CRS" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}
		return urlLogin;
	}


}
