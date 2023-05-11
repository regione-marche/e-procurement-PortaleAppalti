package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa.ws;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;

import it.eldasoft.www.sil.WSPagoPASoap.WSPagoPASoapProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

/**
 *  Wrapper per il client del web service per leggere le tabelle dedicate a PagoPA, definito per
 * 	iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author gabriele.nencini
 *
 */
public class WsPagoPATableWrapper {
	private final Logger logger = ApsSystemUtils.getLogger();
	private IAppParamManager appParamManager;
	private WSPagoPASoapProxy proxyWsPagoPa;
	
	public WsPagoPATableWrapper() {
		logger.info("WsPagoPATableWrapper created.");
	}
	
	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	/**
	 * @param proxyWsPagoPa the proxyWsPagoPa to set
	 */
	public void setProxyWsPagoPa(WSPagoPASoapProxy proxyWsPagoPa) {
		logger.info("Impostato il proxyWsPagoPa {}",proxyWsPagoPa);
		this.proxyWsPagoPa = proxyWsPagoPa;
		logger.info("Impostato il proxyWsPagoPa con endpoint: {}",proxyWsPagoPa.getEndpoint());
	}
	
	/**
	 * @return the proxyWsPagoPa
	 */
	public WSPagoPASoapProxy getProxyWsPagoPa() {
		return proxyWsPagoPa;
	}
	
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_PAGO_PA);
		this.proxyWsPagoPa.setEndpoint(endpoint);
	}
}
