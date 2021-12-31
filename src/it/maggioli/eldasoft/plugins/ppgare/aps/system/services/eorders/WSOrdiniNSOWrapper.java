package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.maggioli.eldasoft.nso.client.api.NsoIntegrationControllerApi;
import it.maggioli.eldasoft.nso.client.api.NsoIntegrationResponseControllerApi;
import it.maggioli.eldasoft.nso.client.invoker.ApiClient;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

/**
 * Wrapper per il client del web service delle gare d'appalto, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author 
 */
@Aspect
public class WSOrdiniNSOWrapper {

	// DA RIVEDERE E MODIFICARE
	private static final String basicAuthenticaton = "YWRtaW46OUI1ZHJOZW0=";	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private IAppParamManager appParamManager;

	private Map<String, Object> session;

	// NSO client
	private String endpoint;
	private ApiClient client;
	private NsoIntegrationControllerApi nso;
	private NsoIntegrationResponseControllerApi nsoResponse;
	
	
	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * metodo utilizzato per la reiniezione dell'endpoint del client 
	 * dopo un aggiornamento dei parametri dell'applicazione  
	 */
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	private void _setEndpoint() {
		this.endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_URL);		
		this.initClient();
	}

	/**
	 * ...
	 */
	private void initClient() {
		this.client = new ApiClient();
		this.endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_URL);
		logger.info("this.endpoint: {}",this.endpoint);
		String auth = new String(Base64.decodeBase64(basicAuthenticaton.getBytes()));			
		if(StringUtils.isNotEmpty(auth)) {
			String v[] = auth.split(":");
			this.client.setUsername(v[0]);			
			this.client.setPassword(v[1]);
			this.client.setBasePath(this.endpoint);
		}

		this.nso = new NsoIntegrationControllerApi(this.client);
		this.nsoResponse = new NsoIntegrationResponseControllerApi(this.client);
	}
	
	/**
	 * NSO 
	 */
	public NsoIntegrationControllerApi getNso() {
		if(this.client == null) {
			this.initClient();
		}		
		return nso;
	}
	
	/**
	 * NSO response
	 */
	public NsoIntegrationResponseControllerApi getNsoResponse() {
		if(this.client == null) {
			this.initClient();
		}
		return nsoResponse;
	}
	
}
