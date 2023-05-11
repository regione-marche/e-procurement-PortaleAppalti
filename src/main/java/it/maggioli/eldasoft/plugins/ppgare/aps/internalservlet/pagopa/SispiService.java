package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.sispi.PagoPAClient;
import it.maggioli.eldasoft.sispi.PagoPAIntegration;
import it.maggioli.eldasoft.sispi.client.api.AuthenticationApi;
import it.maggioli.eldasoft.sispi.client.api.PagamentoApi;
import it.maggioli.eldasoft.sispi.client.invoker.ApiClient;
import it.maggioli.eldasoft.sispi.client.invoker.ApiException;
import it.maggioli.eldasoft.sispi.client.model.KeycloakTokenResponse;
import it.maggioli.eldasoft.sispi.client.model.UrlpagamentoRequest;
import it.maggioli.eldasoft.sispi.client.model.UrlpagamentoResponse;

public class SispiService extends AbstractService implements PagoPaService {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6752360750148363732L;
	
	private final String clientId = "pagopa-sispi-clientid";
	private final String clientSecret = "pagopa-sispi-client-secret";
	private final String grantType = "pagopa-sispi-grant-type";
	private final String scopeApp = "pagopa-sispi-scope";
	private final String keycloakUsername = "pagopa-sispi-keycloak-username";
	private final String keycloakPassword = "pagopa-sispi-keycloak-password";
	private final String keycloakUrl = "pagopa-sispi-keycloak-url";
	private final String sispiBaseurl = "pagopa-sispi-url-pagamento";

	private final String sispiRealm = "pagopa-sispi-realm";
	private final String sispiServicePmpay = "pagopa-sispi-service-pmpay";
	private final String sispiUserid = "pagopa-sispi-userid";
	private final String sispiCodiceEnte = "pagopa-sispi-codice-ente";
	
	private final Logger logger = ApsSystemUtils.getLogger();
	private AuthenticationApi authApi;
	private PagamentoApi pagamentoApi;
	private IAppParamManager appParamManager;
	
	private PagoPAIntegration pagopaIntgration;
	
	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public SispiService() {
		logger.info("SispiService created");
	}
	
	@Override
	public String getIuv(PagoPaPagamentoModel model, Map<Integer, String> map) throws Exception {
		try {
			logger.info("Calling external WS.");
			logger.info("{}",model);
			String tipoFiscaleDebitore = model.getTipoIdFiscaleDebitore();
			String idFiscaleDebitore = model.getIdFiscaleDebitore();
			Date dataFineValidita = model.getDataFineValidita();
			Date dataInizioValidita = model.getDataInizioValidita();
			Date dataScadenza = model.getDataScadenza();
			String idDebito = model.getIdDebito();
			String idRata = model.getIdRata();
			Double importo = model.getImporto();
			String causale = map.get(model.getCausale());
			String iuv = model.getIuv();
			logger.info("Calling out element");
			
//			PagoPAResponseUrl res = pagopaIntgration.generaPagamento(tipoFiscaleDebitore, idFiscaleDebitore, dataFineValidita, dataInizioValidita, dataScadenza, idDebito, idRata, importo, causale, tipoFiscaleDebitore, idFiscaleDebitore, idDebito, idRata, iuv);
//			logger.info("{} - {}",res.getIuv(), res.getUrl());
			return pagopaIntgration.getIuvFromClient(tipoFiscaleDebitore, idFiscaleDebitore, dataFineValidita, dataInizioValidita,dataScadenza, idDebito, idRata, importo, causale);
		} catch (Exception e) {
			logger.error("Errore nella comunicazione.",e);
			throw e;
		}
	}

	@Override
	public String invioBozzaPagamento() throws ApiException {
		return UUID.randomUUID().toString();
	}

	@Override
	public String confermaPagamento(String urlCancel, String urlKo, String urlOk, String urlS2S, String iuv) throws ApiException, ApsSystemException {
		Map<String,AppParam> params = appParamManager.getMapAppParamsByCategory("pagopa");
		String client_id = params.get(clientId).getValue();
		String client_secret = params.get(clientSecret).getValue();
		String grant_type = params.get(grantType).getValue();
		String scope = params.get(scopeApp).getValue();

		authApi.getApiClient().setUsername(params.get(keycloakUsername).getValue());
		authApi.getApiClient().setPassword(params.get(keycloakPassword).getValue());
		authApi.getApiClient().setBasePath(params.get(keycloakUrl).getValue());
		
		logger.info("Calling  SispiService getToken: {}",authApi.getApiClient().getBasePath());
		KeycloakTokenResponse resp = authApi.getToken(client_id,client_secret,grant_type,scope);
		UrlpagamentoRequest body = new UrlpagamentoRequest()
										.idRichiesta(null)
										.realm(params.get(sispiRealm).getValue())
										.service(params.get(sispiServicePmpay).getValue())
										.urlCancel(urlCancel)
										.urlKo(urlKo)
										.urlOk(urlOk)
										.urlS2S(urlS2S);
		pagamentoApi.getApiClient().setBasePath(params.get(sispiBaseurl).getValue());
		pagamentoApi.getApiClient().setApiKey(resp.getAccessToken());
		List<UrlpagamentoResponse> respl = pagamentoApi.urlpagamento(body , params.get(sispiCodiceEnte).getValue(), iuv);
		if(respl==null || respl.isEmpty()) throw new ApiException("Restituita lista vuota di URL");
		return respl.get(0).getLocation();
	}

	@Override
	public void init() throws Exception {
		logger.info("SispiService init");
		ApiClient apiClientForToken = new ApiClient();

		authApi = new AuthenticationApi(apiClientForToken);
		
		ApiClient apiClientForPagoPa = new ApiClient();
		apiClientForPagoPa.setApiKeyPrefix("Bearer");
		
		pagamentoApi = new PagamentoApi(apiClientForPagoPa);
		
		Map<String,AppParam> params = appParamManager.getMapAppParamsByCategory("pagopa");
		Map<String,String> hashParams = new HashMap<String,String>();
		for(Entry<String,AppParam> e : params.entrySet()) {
			hashParams.put(e.getKey(), e.getValue().getValue());
		}
		//TODO !!!
		pagopaIntgration = PagoPAIntegration
									.getInstance(PagoPAClient.SISPI)
									.initService(hashParams);
		logger.info("SispiService initialized");
	}

	@Override
	public String toString() {
		return "SispiService []";
	}

	@Override
	public String getRicevutaByIuv(String iuv) throws Exception {
		String str = pagopaIntgration.getRicevutaByIuv(iuv);
		logger.info("ricevutatelematica: {}",str);
		return str;
	}
	
}