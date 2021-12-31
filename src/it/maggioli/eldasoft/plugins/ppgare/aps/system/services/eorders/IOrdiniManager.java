package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders;

import it.maggioli.eldasoft.nso.client.invoker.ApiException;
import it.maggioli.eldasoft.nso.client.model.Filter;
import it.maggioli.eldasoft.nso.client.model.IncomingResponseNso;
import it.maggioli.eldasoft.nso.client.model.NsoWsOrder;
import it.maggioli.eldasoft.nso.client.model.OrderRequestToNso;
import it.maggioli.eldasoft.nso.client.model.ResultCountNsoWsOrderBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.io.File;
import java.util.List;

/**
 * Interfaccia base per il servizio di gestione degli ordini (CEF).
 * 
 * @version 
 * @author 
 */
public interface IOrdiniManager {

	public void cancelOrderFO(
			String codimp, 
			String endpoint, 
			String orderCode, 
			String orderDate, 
			OrderRequestToNso orderRequest) throws ApiException; 
	
	public void confirmOrderFO(
			String codimp, 
			String endpoint, 
			String orderCode, 
			String orderDate, 
			OrderRequestToNso orderRequest) throws ApiException;
	
	public SearchResult<NsoWsOrder> getFullOrderListFO(String codimp) throws ApiException;	
	
	public SearchResult<NsoWsOrder> getFullOrderListWithParingFO(String codimp, Integer page, Integer pageSize) throws ApiException;
		
	public List<ResultCountNsoWsOrderBean> getGroupedOrderListForFO(String codimp) throws ApiException;
	
	public NsoWsOrder getOrderByIdt(String codimp, String idt) throws ApiException;
	
	public NsoWsOrder getOrderById(String codimp, Long orderId) throws ApiException;
	
	public NsoWsOrder getOrderByTriplet(String codimp, String endpoint, String orderCode, String orderDate) throws ApiException;

	public String getOrderXmlByFileName(String codimp, String fileName) throws ApiException;

	public SearchResult<NsoWsOrder> getPagedOrderListFO(String codimp, Integer page, Integer pageSize, Filter filter) throws ApiException;
	
	public void processOrderForNSO(
			String codimp, 
			String endpoint, 
			File file, 
			String fileName,
			String ngara,
			String hasAttachment, 
			String linkedOrderCode, 
			String linkedOrderId, 
			String orderCode, 
			String orderDate, 
			String orderExpiryDate, 
			String orderId, 
			String rootOrderCode, 
			String rootOrderId,
			String receiver, 
			String sender,
			String totalPriceWithVat,
			String uffint,
			String cig) throws ApiException;
	
	public void validateOrderForNSO(File file, String orderId) throws ApiException;
	
	public void receiveResponseFromNso(IncomingResponseNso ir) throws ApiException;

}
