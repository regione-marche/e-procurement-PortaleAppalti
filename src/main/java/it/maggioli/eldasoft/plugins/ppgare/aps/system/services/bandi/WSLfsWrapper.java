package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import java.util.Map;

import it.eldasoft.www.sil.WSLfs.WSLfsSoapProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Wrapper per il client del web service delle gare d'appalto, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author ...
 */
@Aspect
public class WSLfsWrapper {

	/** Riferimento al web service LFS. */
	private WSLfsSoapProxy proxyWSlfs;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;
	
	public WSLfsSoapProxy getProxyWSlfs() {
		return proxyWSlfs;
	}

	public void setProxyWSlfs(WSLfsSoapProxy proxyWSlfs) {
		this.proxyWSlfs = proxyWSlfs;
	}

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_LFS_APPALTO);
		this.proxyWSlfs.setEndpoint(endpoint);
	}

}
