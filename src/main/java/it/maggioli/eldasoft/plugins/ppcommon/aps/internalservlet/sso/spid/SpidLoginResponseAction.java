package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.spid;

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

/**
 * ... 
 */
public class SpidLoginResponseAction extends BaseResponseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -508800390968455151L;


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
				"/do/SSO/SpidLoginResponse.action",
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
	 * Inizializzazione dell'operazione di login al servizio SPID di Maggioli per accesso da amministratori
	 */
	public String prepareAdminLogin() {
		// controllo motivazione, se è presente la inserisco in sessione per tracciarla in seguito nell'evento di login,
		// in caso contrario scateno un errore
		if(motivazione == null || "".equals(motivazione)){
			this.addActionError(this.getText("Errors.sso.motivazioneMandatory",	new String[0]));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			return INPUT;
		} else {
			@SuppressWarnings("unchecked")
			Map<String,String> adminAttributes = (Map<String,String>)this.getRequest().getSession().getAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER);
			adminAttributes.put("motivazione", motivazione);
			String target = SUCCESS;
			this.urlLogin = prepareCallbackLogin(
					"/do/SSO/SpidLoginAdminResponse.action",
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
			String url = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPID_WS_AUTHSERVICESPID_URL);
			String authSystem = SPID_AUTHSYSTEM_DEFAULT;
			String serviceProvider = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPID_SERVICEPROVIDER);
			Integer serviceIndex = (Integer) appParamManager
			.getConfigurationValue(AppParamManager.SPID_SERVICEINDEX);
			String authLevel = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPID_AUTHLEVEL);
			//String idProvider = this.idp;

			// valida i parametri...
			if (StringUtils.isEmpty(url)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.SPID_WS_AUTHSERVICESPID_URL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.SPID_WS_AUTHSERVICESPID_URL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(serviceProvider)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.SPID_SERVICEPROVIDER }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.SPID_SERVICEPROVIDER));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(authLevel)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.SPID_AUTHLEVEL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.SPID_AUTHLEVEL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(idProvider)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { "IDP" }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										"IDP"));
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

				action.getRequest().getSession().setAttribute(SESSION_ID_SSO_AUTHID, authId);

				// invia la richiesta di login al servizio SPID...
				int i = url.indexOf("/services/");
				url = (i > 0 ? url.substring(0, i) : url);

				urlLogin = url + "/auth.jsp" 
						+ "?backUrl=" + backUrl
						+ "&authSystem=" + authSystem 
						+ "&authId=" + authId
						+ "&serviceProvider=" + serviceProvider 
						+ "&serviceIndex=" + serviceIndex
						+ "&authLevel=" + SPID_AUTHLEVEL_URL + authLevel 
						+ "&idp=" + idProvider;
				
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
