package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders;

import it.maggioli.eldasoft.nso.client.invoker.ApiException;
import it.maggioli.eldasoft.nso.client.model.Filter;
import it.maggioli.eldasoft.nso.client.model.IncomingResponseNso;
import it.maggioli.eldasoft.nso.client.model.NsoWsOrder;
import it.maggioli.eldasoft.nso.client.model.OrderRequestToNso;
import it.maggioli.eldasoft.nso.client.model.PageNsoWsOrder;
import it.maggioli.eldasoft.nso.client.model.ResultCountNsoWsOrderBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractService;


/**
 * Servizio gestore degli ordini (CEF).
 * 
 * @version 
 * @author 
 */
public class OrdiniManager extends AbstractService implements IOrdiniManager {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2322170404325198346L;
	
	private WSOrdiniNSOWrapper wsOrdiniNSO;

	public void setWsOrdiniNSO(WSOrdiniNSOWrapper proxyWsOrdiniNSO) {
		this.wsOrdiniNSO = proxyWsOrdiniNSO;
	}
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	/**
	 * This method allow to cancel an order
	 * @throws ApiException 
	 */
	@Override
	public void cancelOrderFO(
			String codimp, 
			String endpoint, 
			String orderCode, 
			String orderDate, 
			OrderRequestToNso orderRequest) throws ApiException 
	{		
		if(StringUtils.isNotEmpty(codimp) && StringUtils.isNotEmpty(endpoint) &&
		   StringUtils.isNotEmpty(orderCode) && StringUtils.isNotEmpty(orderDate) &&
		   orderRequest != null)
		{
			wsOrdiniNSO.getNso().cancelOrderFOUsingPOST(codimp, endpoint, orderCode, orderDate, orderRequest);
 		}
	}
	
	/**
	 * This method allow to confirm an order
	 */
	@Override
	public void confirmOrderFO(
			String codimp, 
			String endpoint, 
			String orderCode, 
			String orderDate, 
			OrderRequestToNso orderRequest) throws ApiException
	{
		if(StringUtils.isNotEmpty(codimp) && StringUtils.isNotEmpty(endpoint) &&
		   StringUtils.isNotEmpty(orderCode) && StringUtils.isNotEmpty(orderDate) &&
		   orderRequest != null)
		{
			wsOrdiniNSO.getNso().confirmOrderFOUsingPOST(codimp, endpoint, orderCode, orderDate, orderRequest);
		}
	}
	
	/**
	 * This method allow to get the full list of Orders without paging 
	 */
	@Override
	public SearchResult<NsoWsOrder> getFullOrderListFO(String codimp) throws ApiException {
		SearchResult<NsoWsOrder> ordini = new SearchResult<NsoWsOrder>(); 
			
		if(StringUtils.isNotEmpty(codimp)) {
			List<NsoWsOrder> list = wsOrdiniNSO.getNso()
				.getFullOrderListFOUsingGET(codimp);
			if (list != null) {
				ordini.setDati(list);
				ordini.setNumTotaleRecord(list.size());
				ordini.setNumTotaleRecordFiltrati(list.size());
			} else {
				ordini.setDati( new ArrayList<NsoWsOrder>() );
			}
		}
		
		return ordini;
	}
	
	/**
	 * This method allow to get the full list of Orders in a paged manners
	 */
	@Override
	public SearchResult<NsoWsOrder> getFullOrderListWithParingFO(String codimp, Integer page, Integer pageSize) throws ApiException {
		SearchResult<NsoWsOrder> ordini = new SearchResult<NsoWsOrder>(); 
		
		if(StringUtils.isNotEmpty(codimp) && page >= 0 && pageSize >= 0) {
			List<NsoWsOrder> list = wsOrdiniNSO.getNso()
				.getFullOrderListWithPagingFOUsingGET(codimp, page, pageSize);
			if (list != null) {
				ordini.setDati(list);
				ordini.setNumTotaleRecord(list.size());
				ordini.setNumTotaleRecordFiltrati(list.size());
			} else {
				ordini.setDati( new ArrayList<NsoWsOrder>() );
			}
		}
	
		return ordini;
	}
	
	/**
	 * This method allow to get number of orders to be confirmed by FO
	 */
	@Override
	public List<ResultCountNsoWsOrderBean> getGroupedOrderListForFO(String codimp) throws ApiException {
		List<ResultCountNsoWsOrderBean> lista = null;
		
		if(StringUtils.isNotEmpty(codimp)) {
			lista = wsOrdiniNSO.getNso().getGroupedOrderListForFOUsingGET(codimp);
		}
		
		return lista;
	}
	
	/**
	 * This method allow to get the order specified by the parameters 
	 */
	@Override
	public NsoWsOrder getOrderByIdt(String codimp, String idt) throws ApiException {
		NsoWsOrder order = null;
		
		if(StringUtils.isNotEmpty(codimp) && StringUtils.isNotEmpty(idt)) {
			order = wsOrdiniNSO.getNso().getOrderByIdtUsingGET(codimp, idt);
		}
	
		return order;
	}

	/**
	 * This method allow to get the order specified by the parameters 
	 */
	@Override
	public NsoWsOrder getOrderById(String codimp, Long orderId) throws ApiException {
		NsoWsOrder order = null;
		
		if(StringUtils.isNotEmpty(codimp) && orderId >= 0) {
			order = wsOrdiniNSO.getNso().getOrderByIdUsingGET(codimp, orderId);
		}
	
		return order;
	}
	
	/**
	 * This method allow to get the order specified by the parameters
	 */
	@Override
	public NsoWsOrder getOrderByTriplet(String codimp, String endpoint, String orderCode, String orderDate) throws ApiException {
		NsoWsOrder order = null;
		
		if(StringUtils.isNotEmpty(codimp) && StringUtils.isNotEmpty(endpoint) && 
		   StringUtils.isNotEmpty(orderCode) && StringUtils.isNotEmpty(orderDate)) 
		{
			order = wsOrdiniNSO.getNso().getOrderByTripletUsingGET(codimp, endpoint, orderCode, orderDate);
		}
		
		return order;
	}

	/**
	 * This method allow to get content of the xml file of the order
	 */
	@Override
	public String getOrderXmlByFileName(String codimp, String fileName) throws ApiException {
		String order = null;
		
		if(StringUtils.isNotEmpty(codimp) && StringUtils.isNotEmpty(fileName)) {
			order = wsOrdiniNSO.getNso().getOrderXmlByFileNameUsingGET(codimp, fileName);
		}
	
		return order;
	}

	/**
	 * This method allow to get a page object containing the data
	 */
	@Override
	public SearchResult<NsoWsOrder> getPagedOrderListFO(String codimp, Integer page, Integer pageSize, Filter filter) throws ApiException {
		SearchResult<NsoWsOrder> ordini = new SearchResult<NsoWsOrder>(); 
		
		if(StringUtils.isNotEmpty(codimp) && page >= 0 && pageSize >= 0) {
			if(filter != null) {
				GregorianCalendar d = new GregorianCalendar();
				
				// OrderDate è obbligatorio, se manca imposta a 1/1/1900 
				if(filter.getOrderDate() == null) {
					d.set(1, 1, 1900);
					filter.setOrderDate(d.getTime()); 
				}
			}
				
			PageNsoWsOrder list = wsOrdiniNSO.getNso()
				.getPagedOrderListFOUsingPOST(codimp, page, pageSize, filter);
			if (list != null) {
				ordini.setDati(list.getContent());
				ordini.setNumTotaleRecord(list.getTotalElements());
				ordini.setNumTotaleRecordFiltrati(list.getTotalElements());
			} else {
				ordini.setDati( new ArrayList<NsoWsOrder>() );
			}
		}
	
		return ordini;
	}
		
	/**
	 * This method allow to process the XML order 
	 */
	@Override
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
			String cig) throws ApiException
	{
		if(StringUtils.isNotEmpty(codimp) &&
		   StringUtils.isNotEmpty(endpoint) &&
		   file != null &&
		   StringUtils.isNotEmpty(fileName) &&
		   StringUtils.isNotEmpty(orderDate) &&
		   StringUtils.isNotEmpty(orderDate) &&
		   StringUtils.isNotEmpty(orderExpiryDate) &&
		   StringUtils.isNotEmpty(orderId)) 
		{
			wsOrdiniNSO.getNso().processOrderForNSOUsingPOST(
					codimp, 
					endpoint, 
					file, 
					fileName, 
					ngara, 
					orderCode, 
					orderDate, 
					orderExpiryDate, 
					rootOrderId, 
					receiver, 
					sender, 
					totalPriceWithVat, 
					uffint, 
					cig, 
					hasAttachment, 
					linkedOrderCode, 
					linkedOrderId, 
					rootOrderCode, 
					rootOrderId);
		}
	}

	@Override
	public void validateOrderForNSO(File file, String orderId) throws ApiException {
		if(file != null && StringUtils.isNotEmpty(orderId)) {
			wsOrdiniNSO.getNso().validateOrderForNSOUsingPOST(file, orderId);
		}
	}
	
	@Override
	public void receiveResponseFromNso(IncomingResponseNso ir) throws ApiException {
		if(ir != null) {
			wsOrdiniNSO.getNsoResponse().receiveResponseFromNsoUsingPOST(ir);
		}
	}

}