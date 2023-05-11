package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import java.util.Map;

import it.eldasoft.www.sil.WSGareAppalto.WSGareAppaltoSoapProxy;
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
public class WSGareAppaltoWrapper {

	/** Riferimento al web service Gare Appalto. */
	private WSGareAppaltoSoapProxy proxyWSGare;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	private Map<String, Object> session;
	
	/**
	 * @return the proxyWSGare
	 */
	public WSGareAppaltoSoapProxy getProxyWSGare() {
		return proxyWSGare;
	}

	/**
	 * @param proxyWSGare the proxyWSGare to set
	 */
	public void setProxyWSGare(WSGareAppaltoSoapProxy proxyWSGare) {
		this.proxyWSGare = proxyWSGare;
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
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_GARE_APPALTO);
		this.proxyWSGare.setEndpoint(endpoint);
	}

//	private void setStazioneAppaltante(String stazioneAppaltante) {
//		if(stazioneAppaltante != null) {
//			this.session.put(
//					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, 
//					stazioneAppaltante);
//		}
//	}

}
