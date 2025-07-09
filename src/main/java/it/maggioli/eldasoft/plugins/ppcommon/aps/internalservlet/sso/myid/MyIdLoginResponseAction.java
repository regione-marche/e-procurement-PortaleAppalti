package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.myid;

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

public class MyIdLoginResponseAction extends BaseResponseAction {
	private static final long serialVersionUID = -508800390968455151L;


	/**
	 * inizializzazione dell'operazione di login al servizio SPID di Maggioli
	 */
	public String prepareLogin() {
		String target = SUCCESS;
		this.urlLogin = prepareCallbackLogin(
				"/do/SSO/MyIdLoginResponse.action",
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
					.getConfigurationValue(AppParamManager.MYID_WS_AUTHSERVICEMYID_URL);
			String authSystem = MYID_AUTHSYSTEM_DEFAULT;
			String urlServizio = (String) appParamManager
					.getConfigurationValue(AppParamManager.MYID_SERVICEMYID_URL);
			
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

				urlLogin = urlServizio 
						+ "?backUrl=" + backUrl
						+ "&authSystem=" + authSystem 
						+ "&authId=" + authId
						+ "&authServicetoken=" + authId;
				
				target = SUCCESS;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli MYID" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}

		return urlLogin;
	}


}
