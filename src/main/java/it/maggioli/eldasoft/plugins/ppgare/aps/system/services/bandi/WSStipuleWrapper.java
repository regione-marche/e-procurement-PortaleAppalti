package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSStipule.WSStipuleSoapProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import java.util.Map;

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
public class WSStipuleWrapper {

	/** Riferimento al web service Gare Appalto. */
	private WSStipuleSoapProxy proxyWSstipule;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	private Map<String, Object> session;
	
	public WSStipuleSoapProxy getProxyWSstipule() {
		return proxyWSstipule;
	}

	public void setProxyWSstipule(WSStipuleSoapProxy proxyWSstipule) {
		this.proxyWSstipule = proxyWSstipule;
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
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_STIPULE_APPALTO);
		this.proxyWSstipule.setEndpoint(endpoint);
	}

}
