package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen;

import it.eldasoft.www.WSOperazioniGenerali.WSOperazioniGeneraliSoapProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Wrapper per il client del web service delle operazioni generali, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author Stefano.Sabbadin
 */
@Aspect
public class WSOperazioniGeneraliWrapper {

	/** Riferimento al web service Operazioni Generali. */
	private WSOperazioniGeneraliSoapProxy proxyWSOPGenerali;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/**
	 * @param generali
	 *            the _proxyWSOPGenerali to set
	 */
	public void setProxyWSOPGenerali(WSOperazioniGeneraliSoapProxy generali) {
		proxyWSOPGenerali = generali;
	}

	/**
	 * @return the wsOpGenerali
	 */
	public WSOperazioniGeneraliSoapProxy getProxyWSOPGenerali() {
		return proxyWSOPGenerali;
	}

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	@After ("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_OPERAZIONI_GENERALI);
		this.proxyWSOPGenerali.setEndpoint(endpoint);
	}
	
}
