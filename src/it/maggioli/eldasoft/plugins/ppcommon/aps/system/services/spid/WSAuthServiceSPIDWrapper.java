package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid;

import it.cedaf.authservice.service.AuthServiceProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Wrapper per il client del web service dei servizi di autenticazione SPID, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author ...
 */
@Aspect
public class WSAuthServiceSPIDWrapper {

	/** Riferimento al web service SPID. */	
	private AuthServiceProxy proxyWSAuthService; 
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/**
	 * @return the proxyWSAste
	 */
	public AuthServiceProxy getProxyWSAuthService() {
		return proxyWSAuthService;
	}

	/**
	 * @param proxyWSAst the proxyWSAste to set
	 */
	public void setProxyWSAuthService(AuthServiceProxy proxyWSAuthService) {
		this.proxyWSAuthService = proxyWSAuthService;
	}

	/**
	 * @return the appParamManager
	 */
	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.SPID_WS_AUTHSERVICESPID_URL);
		this.proxyWSAuthService.setEndpoint(endpoint);
	}

}
