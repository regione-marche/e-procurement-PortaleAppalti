package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste;

import it.eldasoft.www.sil.WSAste.WSAsteSoapProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Wrapper per il client del web service delle gare d'appalto, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author Stefano.Sabbadin
 */
@Aspect
public class WSAsteWrapper {

	/** Riferimento al web service Aste . */
	private WSAsteSoapProxy proxyWSAste;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/**
	 * @return the proxyWSAste
	 */
	public WSAsteSoapProxy getProxyWSAste() {
		return proxyWSAste;
	}

	/**
	 * @param proxyWSAst the proxyWSAste to set
	 */
	public void setProxyWSAste(WSAsteSoapProxy proxyWSAste) {
		this.proxyWSAste = proxyWSAste;
	}

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_ASTE);
		this.proxyWSAste.setEndpoint(endpoint);
	}
	
}
