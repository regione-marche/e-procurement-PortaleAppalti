package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.eidas;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.BaseResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

public class EidasLoginResponseAction extends BaseResponseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8928288167778218304L;

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}
	
	/**
	 * inizializzazione dell'operazione di login al servizio SPID di Maggioli
	 */
	public String prepareLogin() {
		String target = SUCCESS;
		this.urlLogin = prepareCallbackLogin(
				"/do/SSO/EidasLoginResponse.action",
				this.idp,
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
	 * Creazione URL per accedere al servizio SPID di Maggioli, comprendente la backUrl di ritorno sul portale
	 */
	public static String prepareCallbackLogin(
			String callbackAction,
			String idProvider,
			ConfigInterface configManager,
			IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager,
			WSAuthServiceSPIDWrapper wsAuthServiceSPID) 
	{
		String target = SUCCESS;
		String urlLogin = null; 

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try {
			String url = (String) appParamManager.getConfigurationValue(AppParamManager.EIDAS_WS_AUTHSERVICEEIDAS_URL);			
			String authSystem = EIDAS_AUTHSYSTEM_DEFAULT;
			String authLevel = (String) appParamManager.getConfigurationValue(AppParamManager.EIDAS_AUTHLEVEL);
			Integer serviceIndex = (Integer) appParamManager.getConfigurationValue(AppParamManager.EIDAS_SERVICEINDEX);
			String serviceProvider = (String) appParamManager.getConfigurationValue(AppParamManager.EIDAS_SERVICEPROVIDER);			
			
			//String saServiceProvider = getEidasServiceProviderPerSA(appParamManager);
			//serviceProvider = (StringUtils.isNotEmpty(saServiceProvider) ? saServiceProvider : serviceProvider);
			
			// normalizza "callbackAction"
			if(StringUtils.isNotEmpty(callbackAction))
				if(callbackAction.startsWith("/"))
					callbackAction = callbackAction.substring(1); 
			
			// valida i parametri...
			if (StringUtils.isEmpty(url)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.EIDAS_WS_AUTHSERVICEEIDAS_URL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.EIDAS_WS_AUTHSERVICEEIDAS_URL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(serviceProvider)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.EIDAS_SERVICEPROVIDER }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.EIDAS_SERVICEPROVIDER));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(authLevel)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.EIDAS_AUTHLEVEL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.EIDAS_AUTHLEVEL));
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
						+ "&authLevel=" + SPID_AUTHLEVEL_URL + authLevel
						+ "&serviceIndex=" + serviceIndex
						+ "&serviceProvider=" + serviceProvider; 
				
				target = SUCCESS;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli EIDAS" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}

		return urlLogin;
	}

}