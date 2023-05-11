package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractService;

import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.www.sil.WSPagoPASoap.ElencoStatiPagamentoOutType;
import it.eldasoft.www.sil.WSPagoPASoap.ElencoStatiPagamentoOutTypeTipologiaEntry;
import it.eldasoft.www.sil.WSPagoPASoap.ElencoTabulatoDueCodificatoOutType;
import it.eldasoft.www.sil.WSPagoPASoap.ElencoTabulatoDueCodificatoOutTypeTipologiaEntry;
import it.eldasoft.www.sil.WSPagoPASoap.ElencoTipiCausalePagamentoOutType;
import it.eldasoft.www.sil.WSPagoPASoap.ElencoTipiCausalePagamentoOutTypeTipologiaEntry;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiFilterInType;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiOutType;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiPaginationType;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentoInType;
import it.eldasoft.www.sil.WSPagoPASoap.PagoPaException;
import it.eldasoft.www.sil.WSPagoPASoap.PagopaRicevutaInType;
import it.eldasoft.www.sil.WSPagoPASoap.PagopaRicevutaOutType;
import it.eldasoft.www.sil.WSPagoPASoap.RiferimentoFilterInType;
import it.eldasoft.www.sil.WSPagoPASoap.RiferimentoOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa.ws.WsPagoPATableWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.sispi.PagoPAClient;
import it.maggioli.eldasoft.sispi.PagoPAIntegration;
import it.maggioli.eldasoft.sispi.PagoPAResponseUrl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Manager per le invocazioni ai vari servizi
 * @author gabriele.nencini
 *
 */
@Aspect
public class PagoPaManager extends AbstractService {
	/**
	 * UID
	 */	
	private static final long serialVersionUID = 5694578697664051975L;
	
	private final Logger logger = ApsSystemUtils.getLogger();
	private SimpleDateFormat sdf;
	private PagoPAIntegration pagopaIntgration;
	
	
	private WsPagoPATableWrapper wsPagoPATableWrapper;
	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
	
	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @param bandiManager the bandiManager to set
	 */
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	/**
	 * @param wsPagoPATableWrapper the wsPagoPATableWrapper to set
	 */
	public void setWsPagoPATableWrapper(WsPagoPATableWrapper wsPagoPATableWrapper) {
		this.wsPagoPATableWrapper = wsPagoPATableWrapper;
	}

	public PagoPaManager(){
		logger.debug("PagoPaManager Constructed");
	}

	@Override
	public void init() throws Exception {
		logger.info("PagoPaManager init");
		this.reloadConfiguration();
		logger.info("PagoPaManager init done");
	}
	
	@After ("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void reloadConfiguration() throws Exception {
		Map<String,AppParam> params = appParamManager.getMapAppParamsByCategory("pagopa");
		String serviceKey=params.get("pagopa-configuration").getValue();
		try {
			Map<String,String> hashParams = new HashMap<String,String>();
			for(Entry<String,AppParam> e : params.entrySet()) {
				hashParams.put(e.getKey(), e.getValue().getValue());
			}
//			hashParams.putAll(getMappaturaJppa());
			pagopaIntgration = PagoPAIntegration
					.getInstance(PagoPAClient.valueOf(serviceKey))
					.initService(hashParams);
			logger.info("PagoPaManager Configuration loaded: {}",serviceKey);
		} catch (Exception e) {
			logger.error("PagoPaManager Configuration not existing: {}",serviceKey,e);
			pagopaIntgration = null;
		}
		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}
	
	private Map<String,String> getMappaturaJppa() throws Exception {
		ElencoTabulatoDueCodificatoOutType out = wsPagoPATableWrapper.getProxyWsPagoPa().getMappaturaJppa();
		ElencoTabulatoDueCodificatoOutTypeTipologiaEntry[] entrySet = out.getTipologia();
		Map<String,String> mappaturaJppa = new HashMap<String,String>();
		if(entrySet!=null && entrySet.length>0) {
			Map<Integer,String> map = getElencoTipiCausalePagamento();
			for(ElencoTabulatoDueCodificatoOutTypeTipologiaEntry entry : entrySet) {
				String[] keys = StringUtils.split(entry.getKey(),',');
				for(String k: keys) {
					Integer ki = Integer.valueOf(k);
					if(map.containsKey(ki)){
						mappaturaJppa.put(map.get(ki), entry.getValue());
					}
				}
			}
		}
		return mappaturaJppa;
	}
	
	public Map<Integer,String> getElencoTipiCausalePagamento() throws Exception {
		try {
			ElencoTipiCausalePagamentoOutType out = wsPagoPATableWrapper.getProxyWsPagoPa().getElencoTipiCausalePagamento();
			Map<Integer,String> tipiCausalePagamento = new TreeMap<Integer,String>();
			for(ElencoTipiCausalePagamentoOutTypeTipologiaEntry e : out.getTipologia()) {
				tipiCausalePagamento.put(e.getKey().intValue(), e.getValue());
			}
			return tipiCausalePagamento;
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public Map<Integer,String> getElencoStatiPagamento() throws Exception {
		try {
			ElencoStatiPagamentoOutType out = wsPagoPATableWrapper.getProxyWsPagoPa().getElencoStatiPagamento();
			Map<Integer,String> tipiCausalePagamento = new TreeMap<Integer,String>();
			for(ElencoStatiPagamentoOutTypeTipologiaEntry e : out.getTipologia()) {
				tipiCausalePagamento.put(e.getKey().intValue(), e.getValue());
			}
			return tipiCausalePagamento;
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	
	public void creaBozzaPagamento(PagoPaPagamentoModel model) throws PagoPaException, RemoteException {
		PagamentoInType pagamento = new PagamentoInType();
		pagamento.setCausaletip(model.getCausale());
		pagamento.setCodicegara(model.getCodiceGara());
		pagamento.setCodiceimpresa(model.getCodiceImpresa());
		if(model.getDataFineValidita()!=null) {
			pagamento.setDatafinevalidita(sdf.format(model.getDataFineValidita()));
		}
		if(model.getDataInizioValidita()!=null) {
			pagamento.setDatainiziovalidita(sdf.format(model.getDataInizioValidita()));
		}
		if(model.getDataScadenza()!=null) {
			pagamento.setDatascadenza(sdf.format(model.getDataScadenza()));
		}
		pagamento.setDcreazione(sdf.format(new Date()));
		pagamento.setIdrata(UUID.randomUUID().toString()); //NEW -> UUID
		pagamento.setIddebito(model.getCodiceGara()+"-"+pagamento.getIdrata());//NEW codgar+idrata
		pagamento.setImporto(BigDecimal.valueOf(model.getImporto())); //NEW
		pagamento.setServizio("UR");//->per adesso obbligatorio in questo modo
		pagamento.setStatotip(1);
		pagamento.setUsername(model.getUsername());
		logger.info("Datafinevalidita: {}",pagamento.getDatafinevalidita());
		logger.info("Datainiziovalidita: {}",pagamento.getDatainiziovalidita());
		logger.info("Datascadenza: {}",pagamento.getDatascadenza());
		model.setId(wsPagoPATableWrapper.getProxyWsPagoPa().saveOrUpdatePagamento(pagamento ));
	}

	public String eseguiPagamento(PagoPaPagamentoModel model, String applBaseUrl) throws Exception {
		PagamentiOutType po = wsPagoPATableWrapper.getProxyWsPagoPa().getPagamentoById(model.getId());
		String iuv = po.getIuv();
		logger.info("Using iuv {}",iuv);
		populateModel(model,po);
		pagopaIntgration.addConfigurations(getMappaturaJppa());
		logger.info("model populated from db");
		
		// creare gli url di callback
		String id = po.getId().toString();
		String urlCancel = applBaseUrl+"/do/FrontEnd/PagoPA/actionCancel.action?id="+id;
		String urlKo = applBaseUrl+"/do/FrontEnd/PagoPA/actionKo.action?id="+id;
		String urlOk = applBaseUrl+"/do/FrontEnd/PagoPA/actionOk.action?id="+id;
		String urlS2S = applBaseUrl+"/do/FrontEnd/PagoPA/actionS2S.action?id="+id;
		// ottenere la url di callback
		String tipoFiscaleDebitore = model.getTipoIdFiscaleDebitore();
		String idFiscaleDebitore = model.getIdFiscaleDebitore();
		Date dataFineValidita = model.getDataFineValidita();
		Date dataInizioValidita = model.getDataInizioValidita();
		Date dataScadenza = model.getDataScadenza();
		String idDebito = model.getIdDebito();
		String idRata = model.getIdRata();
		Double importo = model.getImporto();
		String causale = getElencoTipiCausalePagamento().get(model.getCausale()); 
//		String iuv = model.getIuv();
		PagoPAResponseUrl res = pagopaIntgration.generaPagamento(tipoFiscaleDebitore, idFiscaleDebitore, dataFineValidita, dataInizioValidita, dataScadenza, idDebito, idRata, importo, causale, urlCancel, urlKo, urlOk, urlS2S, iuv);
//		if(StringUtils.isEmpty(iuv)){		
		logger.info("Generate iuv");
		PagamentoInType pagamento = new PagamentoInType();
		pagamento.setId(po.getId());
		pagamento.setIuv(res.getIuv());
		pagamento.setStatotip(2);
		model.setId(wsPagoPATableWrapper.getProxyWsPagoPa().saveOrUpdatePagamento(pagamento));
//		}
		return res.getUrl();
	}
	
	public void impostaPagamentoEffettuato(Long id)  throws Exception {
		PagamentoInType pagamento = new PagamentoInType();
		pagamento.setId(id);
		pagamento.setStatotip(3);
		pagamento.setDatapagamento(sdf.format(new Date()));
		wsPagoPATableWrapper.getProxyWsPagoPa().saveOrUpdatePagamento(pagamento);
	}
	
	private void populateModel(PagoPaPagamentoModel model,PagamentiOutType po ) throws Exception {
		model.setIdDebito(po.getIddebito());
		model.setCausale(po.getCausaletip());
		model.setCodiceGara(po.getCodicegara());
		model.setDataFineValidita(sdf.parse(po.getDatafinevalidita()));
		model.setDataInizioValidita(sdf.parse(po.getDatainiziovalidita()));
		model.setDataScadenza(sdf.parse(po.getDatascadenza()));
		model.setIdDebito(po.getIddebito());
		model.setIdRata(po.getIdrata());
		model.setImporto(po.getImporto().doubleValue());
		model.setUsername(po.getUsername());
		DatiImpresaDocument datiImpresa = bandiManager.getDatiImpresa(po.getUsername(),null);
		String idFiscaleDebitore = datiImpresa.getDatiImpresa().getImpresa().getPartitaIVA();
		if(StringUtils.isEmpty(idFiscaleDebitore)) {
			idFiscaleDebitore = datiImpresa.getDatiImpresa().getImpresa().getCodiceFiscale();
		}
		model.setIdFiscaleDebitore(idFiscaleDebitore); //ottenere lo idFiscaleDebitore
		model.setTipoIdFiscaleDebitore("G");
		if(datiImpresa.getDatiImpresa().getImpresa().getNaturaGiuridica()==10) {
			model.setTipoIdFiscaleDebitore("F");
		}
	}
	
	public PagamentiPaginationType ricercaPagamenti(PagamentiFilterInType filtro) throws PagoPaException, RemoteException{
		return wsPagoPATableWrapper.getProxyWsPagoPa().getListPagamentiByFilter(filtro);
	}
	
	public PagamentiOutType getPagamentoById(Long id) throws PagoPaException, RemoteException{
		return wsPagoPATableWrapper.getProxyWsPagoPa().getPagamentoById(id);
	}

	public InputStream getRicevuta(PagamentiOutType po) throws Exception{
		PagopaRicevutaOutType out = null;
		try {
			out = wsPagoPATableWrapper.getProxyWsPagoPa().getRicevutaById(po.getId());
		} catch(Exception e) {
			logger.warn("Ricevuta con id {} non presente a DB.",po.getId());
		}
		if(out!=null) {
			logger.info("Ricevuta con id {} presente a DB.",po.getId());
			return new ByteArrayInputStream(out.getRicevuta());
		}
		
		String ricevuta = null;
		try {
			logger.info("calling service.getRicevuta ");
			ricevuta = pagopaIntgration.getRicevuta(po.getIddebito(), po.getIdrata(), getElencoTipiCausalePagamento().get(po.getCausaletip()), po.getIuv());
			if(StringUtils.isNotBlank(ricevuta)) {
				logger.info("Ottenuta ricevuta, salvo in appalti ");
				try {
					PagopaRicevutaInType r = new PagopaRicevutaInType();
					r.setId(po.getId());
					r.setRicevuta(ricevuta.getBytes());
					wsPagoPATableWrapper.getProxyWsPagoPa().insertRicevutaPagamento(r);
				} catch(Exception e) {
					logger.warn("Impossibile salvare la ricevuta a DB, ma si continua lo stesso nelle operazioni.",e);
				}
			}
		} catch(Exception e) {
			logger.warn("Impossibile salvare o ottenere la ricevuta per pagamento con id {}.",po.getId(),e);
			throw e;
		} finally {
			if(ricevuta!=null) {
				return new ByteArrayInputStream(ricevuta.getBytes());
			} 
		}
		logger.info("return null");
		if(StringUtils.isEmpty(ricevuta)) {
			throw new PagoPaException("Impossibile restituire la ricevuta.");
		}
		throw new PagoPaException("Impossibile ottenere il file.");
	}

	public RiferimentoOutType getRiferimento(PagoPaRiferimentoFilterModel model) throws Exception{
		RiferimentoFilterInType filtro = new RiferimentoFilterInType();
		BeanUtils.copyProperties(filtro, model);
		return wsPagoPATableWrapper.getProxyWsPagoPa().getRiferimentoProcedura(filtro);
	}
	public InputStream getElencoRiferimenti(PagoPaRiferimentoFilterModel model) throws Exception{
		try {
			logger.info("getElencoRiferimenti");
			RiferimentoFilterInType filtro = new RiferimentoFilterInType();
			BeanUtils.copyProperties(filtro, model);
			RiferimentoOutType[] refs = wsPagoPATableWrapper.getProxyWsPagoPa().getElencoRiferimenti(filtro );
			JSONArray jarr = new JSONArray();
			JSONObject obj = new JSONObject();
			for(RiferimentoOutType out : refs) {
				jarr.add(JSONObject.fromObject(out));
			}
			obj.put("data", jarr);
			logger.info("getElencoRiferimenti - finished");
			return new ByteArrayInputStream(obj.toString().getBytes());
		} catch (Exception e) {
			logger.error("Errore in getElencoRiferimenti",e);
			throw e;
		}
	}
}
