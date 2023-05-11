package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiFilterInType;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiOutType;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiPaginationType;
import it.eldasoft.www.sil.WSPagoPASoap.PagoPaException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

/**
 * Questa classe gestisce le azioni per il pagamento tramite PagoPA
 * @author gabriele.nencini
 *
 */
public class PagoPaActionLista extends EncodedDataAction implements SessionAware,ModelDriven<PagoPASearchBean> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1817941217117411133L;
	
	private final Logger logger = ApsSystemUtils.getLogger();
	private Map<String, Object> session;
	private IBandiManager bandiManager;
	private PagoPaManager pagoPaManager;

	@Validate
	private PagoPASearchBean model;
	private SearchResult<PagamentiOutType> listaPagamenti;
	
	//data objetcs
	private Map<Integer,String> tipiCausalePagamento;
	private Map<Integer,String> tipiStatoPagamento;
	@Validate(EParamValidation.GENERIC)
	private String errorDownload;

	/**
	 * @param pagoPaManager the pagoPaManager to set
	 */
	public void setPagoPaManager(PagoPaManager pagoPaManager) {
		this.pagoPaManager = pagoPaManager;
	}
	
	/**
	 * @return the tipiCausalePagamento
	 */
	public Map<Integer, String> getTipiCausalePagamento() {
		return tipiCausalePagamento;
	}

	public PagoPaActionLista() {
		logger.info("PagoPaAction()");
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
		logger.info("setSession()");
	}
	
	
	public String listaEffettuati() {
		logger.info("listaEffettuati");
		try {
			Integer stato = Integer.valueOf(3);
			loadPagamenti(stato);
			if(StringUtils.isNotEmpty(errorDownload)) {
				this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_DOWNLOAD"));
			}
		} catch (Exception e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(e.getMessage());
			return SUCCESS;
		}
		return SUCCESS;
	}

	
	public String listaDaEffettuare() {
		logger.info("listaDaEffettuare");
		try {
			Integer stato = Integer.valueOf(2);
			loadPagamenti(stato);
		} catch (Exception e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(e.getMessage());
			return SUCCESS;
		}
		return SUCCESS;
	}
	
	private void loadPagamenti(Integer stato) throws ApsException, PagoPaException, RemoteException, Exception {
		String codimp = (String)this.session.get("codimp");
		if(StringUtils.isEmpty(codimp)) {
			codimp = this.bandiManager.getIdImpresa(this.getCurrentUser().getUsername());
			this.session.put("codimp", codimp);
		}
		PagamentiFilterInType filtro = new PagamentiFilterInType();
		filtro.setStato(new Integer[]{stato});
		filtro.setCodiceimpresa(codimp);
		
		if(model==null) {
			this.model = new PagoPASearchBean();
			this.model.setCurrentPage(0);
			this.model.setiDisplayLength(10);
		}
		populateFilterFromModel(filtro,this.model);
		
		listaPagamenti = new SearchResult<PagamentiOutType>();
		PagamentiPaginationType pag = this.pagoPaManager.ricercaPagamenti(filtro);
		listaPagamenti.setNumTotaleRecord(pag.getTotalelements().intValue());
		listaPagamenti.setNumTotaleRecordFiltrati(pag.getTotalelements().intValue());
		this.model.setiDisplayLength(pag.getPagesize().intValue());
		this.model.processResult(pag.getPagesize().intValue(),listaPagamenti.getNumTotaleRecord());
//		listaPagamenti.setDati(Collections.emptyList());
		if(pag.getContent()!=null) {
			listaPagamenti.setDati(Arrays.asList(pag.getContent()));
		}
		this.tipiCausalePagamento = this.pagoPaManager.getElencoTipiCausalePagamento();
		this.tipiStatoPagamento = this.pagoPaManager.getElencoStatiPagamento();
	}
	
	public String reload() {
		logger.info("reload");
		try {
			this.tipiCausalePagamento = this.pagoPaManager.getElencoTipiCausalePagamento();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public void setModel(PagoPASearchBean model) {
		this.model = model;
	}

	@Override
	public PagoPASearchBean getModel() {
		return this.model;
	}

	/**
	 * @return the listaPagamenti
	 */
	public SearchResult<PagamentiOutType> getListaPagamenti() {
		return listaPagamenti;
	}

	/**
	 * @param bandiManager the bandiManager to set
	 */
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	private void populateFilterFromModel(PagamentiFilterInType filtro, PagoPASearchBean model) {
		SimpleDateFormat sdf = null;
		SimpleDateFormat sdfFrom = null;
		if(model.getCausale()!=null && model.getCausale()!=-1) {
			filtro.setCausale(new Integer[] {model.getCausale()});
		}
		if(StringUtils.isNotEmpty(model.getDataScadenzaA())) {
			sdfFrom = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				filtro.setDataScadenzaA(sdf.format(sdfFrom.parse(model.getDataScadenzaA()+" 23:59:59")));
			} catch (ParseException e) {
				logger.error("Errore nella conversione delle date.",e);
				this.addActionError("Errore nella conversione della DataScadenzaA.");
			}//TODO make conversion
		}
		if(StringUtils.isNotEmpty(model.getDataScadenzaDa())) {
			sdfFrom = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				filtro.setDataScadenzaDa(sdf.format(sdfFrom.parse(model.getDataScadenzaDa()+" 00:00:00")));
			} catch (ParseException e) {
				logger.error("Errore nella conversione delle date.",e);
				this.addActionError("Errore nella conversione della DataScadenzaDa.");
			}//TODO make conversion
		}
		if(StringUtils.isNotEmpty(model.getCodiceGara())) {
			filtro.setCodicegara(new String[] {model.getCodiceGara()});
		}
//		filtro.setStato(new Integer[] {model.getStato()});
		filtro.setPage(model.getCurrentPage());
		if(filtro.getPage()>0) {
			filtro.setPage(filtro.getPage()-1);
		}
		filtro.setPagesize(model.getiDisplayLength());
		
		logger.info("model: {}",model);
		
	}

	/**
	 * @return the tipiStatoPagamento
	 */
	public Map<Integer,String> getTipiStatoPagamento() {
		return tipiStatoPagamento;
	}

	/**
	 * @return the errorDownload
	 */
	public String getErrorDownload() {
		return errorDownload;
	}

	/**
	 * @param errorDownload the errorDownload to set
	 */
	public void setErrorDownload(String errorDownload) {
		this.errorDownload = errorDownload;
	}

}
