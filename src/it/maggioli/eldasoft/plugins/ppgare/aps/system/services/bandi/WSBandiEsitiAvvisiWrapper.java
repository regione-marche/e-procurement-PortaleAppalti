package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.WSBandiEsitiAvvisiProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Wrapper per il client del web service dei bandi esiti ed avvisi, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author Stefano.Sabbadin
 */
@Aspect
public class WSBandiEsitiAvvisiWrapper {

	/** Riferimento al web service Bandi Esiti Avvisi. */
	private WSBandiEsitiAvvisiProxy proxyWSBandiEsitiAvvisi;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/**
	 * @return the proxyWSBandiEsitiAvvisi
	 */
	public WSBandiEsitiAvvisiProxy getProxyWSBandiEsitiAvvisi() {
		return proxyWSBandiEsitiAvvisi;
	}

	/**
	 * @param proxyWSBandiEsitiAvvisi
	 *            the proxyWSBandiEsitiAvvisi to set
	 */
	public void setProxyWSBandiEsitiAvvisi(
			WSBandiEsitiAvvisiProxy proxyWSBandiEsitiAvvisi) {
		this.proxyWSBandiEsitiAvvisi = proxyWSBandiEsitiAvvisi;
	}

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI);
		this.proxyWSBandiEsitiAvvisi.setEndpoint(endpoint);
	}

}
