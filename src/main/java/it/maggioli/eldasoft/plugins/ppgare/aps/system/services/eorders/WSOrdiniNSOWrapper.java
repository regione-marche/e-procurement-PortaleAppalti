package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.maggioli.eldasoft.nso.client.api.NsoIntegrationControllerApi;
import it.maggioli.eldasoft.nso.client.api.NsoIntegrationControllerV11Api;
import it.maggioli.eldasoft.nso.client.api.NsoIntegrationResponseControllerApi;
import it.maggioli.eldasoft.nso.client.invoker.ApiClient;
import it.maggioli.eldasoft.nso.invoice.client.api.V10Api;
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

	// NSO client
	private String endpoint;
	private String endpointFatt;
	private String endpointFattAuth;
	private ApiClient client;
	private ApiClient clientFatt;
	private NsoIntegrationControllerApi nso;
	private NsoIntegrationControllerV11Api nsov11;
	private V10Api nsoFattApi;
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

	public String getEndpointFatt() {
		return endpointFatt;
	}

	public void setEndpointFatt(String endpointFatt) {
		this.endpointFatt = endpointFatt;
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
	 * metodo utilizzato per la reiniezione dell'endpoint del client 
	 * dopo un aggiornamento dei parametri dell'applicazione  
	 */
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	private void _setEndpointFatt() {
		this.endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_FATT_URL);		
		this.initClientFatt();
	}
	
	/**
	 * metodo utilizzato per la reiniezione dell'endpoint del client 
	 * dopo un aggiornamento dei parametri dell'applicazione  
	 */
	@After("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	private void _setEndpointFattAuth() {
		this.endpointFattAuth = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_FATT_AUTH);
		logger.debug("this.endpointFattAuth: {}",this.endpointFattAuth);
		this.initClientFatt();
	}

	private void initClientFatt() {
		this.endpointFatt = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_FATT_URL);
		this.endpointFattAuth = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_FATT_AUTH);
		this.clientFatt = new ApiClient();
		logger.debug("this.endpointFatt: {}",this.endpointFatt);
		this.clientFatt.setBasePath(this.endpointFatt);
		this.clientFatt.setApiKey(this.endpointFattAuth);
		logger.debug("Set bearer");
		
		this.nsoFattApi = new V10Api(this.clientFatt);
		logger.debug("this.endpointFatt: {}",this.nsoFattApi.getApiClient().getBasePath());
	}

	/**
	 * ...
	 */
	private void initClient() {
		this.client = new ApiClient();
		this.endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.NSO_URL);
		logger.debug("this.endpoint: {}",this.endpoint);
		String auth = new String(Base64.decodeBase64(basicAuthenticaton.getBytes()));
		if(StringUtils.isNotEmpty(auth)) {
			String v[] = auth.split(":");
			this.client.setUsername(v[0]);
			this.client.setPassword(v[1]);
			this.client.setBasePath(this.endpoint);
		}

		this.nso = new NsoIntegrationControllerApi(this.client);
		this.nsov11 = new NsoIntegrationControllerV11Api(this.client);
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
	 * NSO 
	 */
	public NsoIntegrationControllerV11Api getNsoV11() {
		if(this.client == null) {
			this.initClient();
		}
		return nsov11;
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
	
	/**
	 * Fatture API
	 */
	public V10Api getApiFatture() {
		logger.debug("Called getApiFatture");
		if(this.clientFatt==null) this.initClientFatt();
		return this.nsoFattApi;
	}

	public String getEndpointFattAuth() {
		return endpointFattAuth;
	}

	public void setEndpointFattAuth(String endpointFattAuth) {
		this.endpointFattAuth = endpointFattAuth;
	}
}
