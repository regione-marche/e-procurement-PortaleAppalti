package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;

import com.agiletec.apsadmin.system.BaseAction;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 *  
 */
public class SSOLogoutResponseAction extends BaseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6400306168072330961L;

	private IAppParamManager appParamManager;
	private WSAuthServiceSPIDWrapper wsAuthServiceSPID;
	
	private String urlHome;

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setWsAuthServiceSPID(WSAuthServiceSPIDWrapper wsAuthServiceSPID) {
		this.wsAuthServiceSPID = wsAuthServiceSPID;
	}

	public String getUrlHome() {
		return urlHome;
	}

	public void setUrlHome(String urlHome) {
		this.urlHome = urlHome;
	}

	/**
	 * ... 
	 */
	public String logout() {
		String target = SUCCESS;

		HttpSession session = this.getRequest().getSession();
		AccountSSO account = (AccountSSO)session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		
		// verifica se invalidare la sessione...
		boolean validLogout = false;
		boolean invalidaSessione = false;
		if(account != null) {
			if (PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO.equals(account.getTipologiaLogin())
				|| PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(account.getTipologiaLogin())
				|| PortGareSystemConstants.TIPOLOGIA_LOGIN_GATEWAY_SSO.equals(account.getTipologiaLogin()))
			{
				validLogout = true;
				invalidaSessione = true;
			} else { 
				// verifica tra gli altri sistemi SSO se l'url per il logout e' valida
				String url = null;
				if(PortGareSystemConstants.TIPOLOGIA_LOGIN_SHIBBOLETH_SSO.equals(account.getTipologiaLogin())) {
					url = (String)appParamManager.getConfigurationValue("auth.sso.shibboleth.logout.url");
				}
				if(PortGareSystemConstants.TIPOLOGIA_LOGIN_COHESION_SSO.equals(account.getTipologiaLogin())) {
					url = (String)appParamManager.getConfigurationValue("auth.sso.cohesion.logout.url");
				}
				if(StringUtils.isEmpty(url)) {
					LOG.fatal("Property 'sso.logout.url' non valorizzata, disconnessione impossibile dal sistema di SSO");
				} else {
					validLogout = true;
					if(((Integer)appParamManager.getConfigurationValue("auth.sso.sessionInvalidate")).intValue() > 0) {
						invalidaSessione = true;
					}
				}
			}
		}
		
		// configura la url per il logout dal servizio di autenticazione remoto...
		if(validLogout) {
			target = doAuthServiceLogout(account);
		}
		
		// se necessario, invalida la sessione utente
		if(invalidaSessione) {
			session.removeAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
			session.invalidate();
		}

		return target;
	}

	/**
	 * invia la richiesta di logout al servizio di autenticazione remoto (SPID, SPID Business, ...)  
	 */
	private String doAuthServiceLogout(AccountSSO accountSSO) {
		String target = "homepage";
		
		// logout per Cie, Cns, Crs, Federa, Gel, MyId, Spid, Spid Business
		String authId = BaseResponseAction.getAuthId();
		if(StringUtils.isNotEmpty(authId)) {
			try {
				// esegui il logout sul sistema remoto di autenticazione con il token "authID"
				String url = wsAuthServiceSPID.getProxyWSAuthService().singleSignOut(authId) + 
							"&backUrl=" +
							getRequest().getScheme() + "://" +
							getRequest().getServerName() + ":" +
							getRequest().getServerPort() +
							getRequest().getContextPath();
				this.setUrlHome(url);
				target = SUCCESS;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "doAuthServiceLogout");
			}
		}
		
		// logout per Cohesion
		if(accountSSO != null && PortGareSystemConstants.TIPOLOGIA_LOGIN_COHESION_SSO.equals(accountSSO.getTipologiaLogin())) {
			String url = (String)appParamManager.getConfigurationValue("auth.sso.cohesion.logout.url");
			this.setUrlHome(url);
			target = SUCCESS;
		}

		// logout per Shibboleth
		if(accountSSO != null && PortGareSystemConstants.TIPOLOGIA_LOGIN_SHIBBOLETH_SSO.equals(accountSSO.getTipologiaLogin())) {
			String url = (String)appParamManager.getConfigurationValue("auth.sso.shibboleth.logout.url");
			this.setUrlHome(url);
			target = SUCCESS;
		}
		
		// logout per Gateway
		if(accountSSO != null && PortGareSystemConstants.TIPOLOGIA_LOGIN_GATEWAY_SSO.equals(accountSSO.getTipologiaLogin())) {
			// non fare nulla e procedi con il logout standard
			target = "homepage";
		}
		
		return target;
	}

}
