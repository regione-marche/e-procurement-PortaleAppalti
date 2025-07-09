package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.services.AbstractService;

import it.maggioli.eldasoft.nso.client.api.NsoIntegrationControllerV11Api;
import it.maggioli.eldasoft.nso.client.invoker.ApiException;
import it.maggioli.eldasoft.nso.client.invoker.auth.ApiKeyAuth;
import it.maggioli.eldasoft.nso.client.model.DraftData;
import it.maggioli.eldasoft.nso.client.model.InvoiceData;
import it.maggioli.eldasoft.nso.client.model.InvoiceDraftKeeper;
import it.maggioli.eldasoft.nso.client.model.PageOfNsoWsInvSdi;
import it.maggioli.eldasoft.nso.invoice.client.api.V10Api;
import it.maggioli.eldasoft.nso.invoice.client.model.BaseResponse;
import it.maggioli.eldasoft.nso.invoice.client.model.CreateFatturaRequest;
import it.maggioli.eldasoft.nso.invoice.client.model.CreateFatturaResponse;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiBeniServiziType;
import it.maggioli.eldasoft.nso.invoice.client.model.FatturaElettronicaType;
import it.maggioli.eldasoft.nso.invoice.client.model.GetDatiGeneraliFatturaResponse;
import it.maggioli.eldasoft.nso.invoice.client.model.GetDatiRigheFatturaResponse;
import it.maggioli.eldasoft.nso.invoice.client.model.NsoWsFatture;
import it.maggioli.eldasoft.nso.invoice.client.model.NsoWsFattureResponse;
import it.maggioli.eldasoft.nso.invoice.client.model.UpdateDatiGeneraliFatturaRequest;
import it.maggioli.eldasoft.nso.invoice.client.model.UpdateDatiRigheFatturaRequest;

public class FattureManager extends AbstractService {
	private static final long serialVersionUID = -3114693152536497231L;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private WSOrdiniNSOWrapper wsOrdiniNSO;
	private NsoIntegrationControllerV11Api nso;
	private V10Api fattApi;

	public void setWsOrdiniNSO(WSOrdiniNSOWrapper proxyWsOrdiniNSO) {
		this.wsOrdiniNSO = proxyWsOrdiniNSO;
	}
	
	@Override
	public void init() throws Exception {
		logger.debug("init");
		logger.debug("this.wsOrdiniNSO: {}",this.wsOrdiniNSO.toString());
		this.nso = wsOrdiniNSO.getNsoV11();
		this.fattApi = wsOrdiniNSO.getApiFatture();
	}

	public InvoiceDraftKeeper getLastDataForOrder(Long idOrdine) throws ApiException {
		try {
			return this.nso.getLatestDraftUsingGET(idOrdine);
		} catch(ApiException ex) {
			logger.warn("Error during call to getLatestDraft response code: {}",ex.getCode());
			if(404 != ex.getCode()) throw ex;
			return null;
		}
	}
	
	public long createFattura(Long idOrdine, String username) throws ApiException {
		CreateFatturaRequest createFatturaRequest = new CreateFatturaRequest();
//		createFatturaRequest.setIduser(518l);
		createFatturaRequest.setUsernome(username);
		createFatturaRequest.setNsoWsOrderId(idOrdine);
		logger.debug("this.fattApi.auth {}",this.fattApi.getApiClient().getAuthentication("Bearer"));
		logger.debug("this.fattApi.auth {}",((ApiKeyAuth)this.fattApi.getApiClient().getAuthentication("Bearer")).getApiKey());
		CreateFatturaResponse resp = this.fattApi.createFatturaUsingPOST(createFatturaRequest );
		if(resp.isEsito()) return resp.getNsoWsFattureId();
		throw new ApiException(resp.getErrorData());
	}
	
	public void createDraft(Long idOrdine, Long idFattura, String codOrd) throws ApiException {
		DraftData dd = new DraftData();
		dd.setCodOrd(codOrd);
		dd.setIdFattura(idFattura);
		dd.setIdOrdine(idOrdine);
		this.nso.createDraftUsingPUT(dd);
	}

	public FatturaElettronicaType getDatiGeneraliFattura(Long idFattura) throws ApiException {
		logger.debug("Getting dati testata for fattura {}",idFattura);
		logger.debug("this.fattApi.getBasePath {}",this.fattApi.getApiClient().getBasePath());
		logger.debug("this.fattApi.auth {}",this.fattApi.getApiClient().getAuthentication("Bearer"));
		logger.debug("this.fattApi.auth {}",((ApiKeyAuth)this.fattApi.getApiClient().getAuthentication("Bearer")).getApiKey());
		GetDatiGeneraliFatturaResponse resp = this.fattApi.getDatiGeneraliFatturaUsingGET(idFattura);
		logger.debug("Resp {}",resp);
		if(!resp.isEsito()) return null;
		return resp.getFatturaElettronica();
	}
	
	public void updateDatiGeneraliFattura(Long idOrdine, FatturaElettronicaType fattura) throws ApiException {
		logger.debug("Called saveDatiTestataFattura");
		UpdateDatiGeneraliFatturaRequest req = new UpdateDatiGeneraliFatturaRequest();
		req.setFatturaElettronica(fattura);
		req.setNsoWsFattureId(idOrdine);
		try {
			BaseResponse br = this.fattApi.updateDatiGeneraliFatturaUsingPOST(req );
			logger.debug("{}",br);
		} catch(ApiException ex) {
			logger.error("Received response: {}",ex.getCode());
			throw ex;
		}		
	}
	
	public DatiBeniServiziType getDatiLinee(Long idFattura) throws ApiException {
		try {
			logger.debug("getDatiLinee");
			GetDatiRigheFatturaResponse resp = this.fattApi.getInvoiceLinesDataUsingGET(idFattura);
			logger.debug("getDatiLinee responses {}",resp);
			if(!resp.isEsito()) return null;
			logger.debug("getDatiLinee responses is esito true");
			return resp.getDatiBeniServizi();
		} catch(ApiException e) {
			logger.error("Error getting linee fatture {}",e.getMessage(),e);
			throw e;
		}
	}
	
	public void updateLinee(Long idFattura, DatiBeniServiziType datiBeniServizi) throws ApiException {
		UpdateDatiRigheFatturaRequest req = new UpdateDatiRigheFatturaRequest();
		req.setNsoWsFattureId(idFattura);
		req.setDatiBeniServizi(datiBeniServizi);
		logger.debug("{}",req);
		BaseResponse resp = this.fattApi.updateDatiRigheFatturaUsingPOST(req );
		logger.debug("updateLinee {}",resp);
	}

	public NsoWsFatture getDatiFatturaById(Long idFatt, String type) throws ApiException {
		NsoWsFattureResponse resp = this.fattApi.getNsoWsFattureByIdUsingGET(idFatt, type);
		logger.debug("getDatiFatturaById: {}",resp);
		if(resp.isEsito()) {
			return resp.getFattura();
		}
		return null;
	}
	
	public void checkFattura(String orderCode, Long orderId, String progInvio) throws ApiException {
		try {
			this.nso.checkInvoiceUsingGET(orderCode, orderId, progInvio);
		} catch(ApiException e) {
			logger.warn("checkInvoiceUsingGET: {}",e.getCode());
			throw e;
		}
	}
	
	public void inviaFattura(InvoiceData invoiceData) throws ApiException {
		this.nso.processInvoicingUsingPOST(invoiceData);
	}

	public PageOfNsoWsInvSdi getListaFattura(String codimp, String orderCode, int page, int pageSize) throws ApiException {
		try {
			return this.nso.getListOfInvoiceSentUsingGET1(codimp,orderCode, page, pageSize);
		} catch (ApiException e) {
			logger.error("Errore nel trovare i dati",e);
			throw e;
		}
	}

	public void createDraftDownload(Long idOrdine, Long idFattura, String codOrd, String progInvio) throws ApiException {
		DraftData dd = new DraftData();
		dd.setCodOrd(codOrd);
		dd.setIdFattura(idFattura);
		dd.setIdOrdine(idOrdine);
		dd.setProgInvio(progInvio);
		InvoiceDraftKeeper resp = this.nso.createDownloadUsingPUT(dd);
		logger.debug("Create Draft {}",resp);
	}
}
