package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import it.maggioli.eldasoft.nso.client.model.Filter;
import it.maggioli.eldasoft.nso.client.model.Filter.OrderStatusEnum;
import it.maggioli.eldasoft.nso.client.model.NsoWsOrder;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.IOrdiniManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista ordini.
 * 
 * @version 
 * @author 
 */
public class OrdiniFinderAction extends EncodedDataAction 
	implements SessionAware, ModelDriven<OrdiniSearchBean> 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1385998498636329425L;

	private static final String FROM_PAGE_OWNER			= "searchordini"; 
	private static final String LIST_ORDINI_DAVALUTARE 	= "listordinidavalutare";
	private static final String LIST_ORDINI_CONFERMATI 	= "listordiniconfermati";
	private static final String LIST_ORDINI_TUTTI	 	= "listordinitutti";
	
	private static final Map<String, String> SESSIONID_MAP;
	static
    {
		// crea l'associazione lista/ricerca con il proprio session id...
		SESSIONID_MAP = new HashMap<String, String>();
		SESSIONID_MAP.put(LIST_ORDINI_DAVALUTARE, PortGareSystemConstants.SESSION_ID_LIST_NSO_DAVALUTARE);
		SESSIONID_MAP.put(LIST_ORDINI_CONFERMATI, PortGareSystemConstants.SESSION_ID_LIST_NSO_CONFERMATI);
		SESSIONID_MAP.put(LIST_ORDINI_TUTTI, PortGareSystemConstants.SESSION_ID_LIST_NSO_TUTTI);
    }
	
	private IOrdiniManager ordiniManager;
	private IBandiManager bandiManager;

    private Map<String, Object> session;
    
    private OrdiniSearchBean model = new OrdiniSearchBean();

    private String last;
     
    private SearchResult<NsoWsOrder> listaOrdini = null;
    
    private Date dataUltimoAggiornamento;
    
    private String stazioneAppaltante;
    
    
	public void setOrdiniManager(IOrdiniManager ordiniManager) {
		this.ordiniManager = ordiniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public OrdiniSearchBean getModel() {
		return this.model;
	}
	
	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public SearchResult<NsoWsOrder> getListaOrdini() {
		return listaOrdini;
	}	

	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}
	
//	/**
//	 * calcola la MAX data di ultimo aggiormento nell'elenco bandi  
//	 */
//	private Date getMaxDataUltimoAggiornamento(List<ContrattoType> lista) {
//		Date dta = null;
//		if(lista != null) {			
//			for(int i = 0; i < lista.size(); i++) {
//				if( dta == null ) {
//					dta = lista.get(i).getDataUltimoAggiornamento();
//				} 
//				if( lista.get(i).getDataUltimoAggiornamento() != null && 
//					lista.get(i).getDataUltimoAggiornamento().compareTo(dta) > 0 ) {
//					dta = lista.get(i).getDataUltimoAggiornamento();					
//				}				
//			}
//		}		
//		return dta;		
//	}

	/**
     * Restituisce la lista degli ordini ricevuti NSO
     */
    public String listOrdiniDaValutare() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaOrdini(LIST_ORDINI_DAVALUTARE) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }
    
    /**
     * Restituisce la lista dei documenti di trasporto NSO
     */
    public String listOrdiniConfermati() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaOrdini(LIST_ORDINI_CONFERMATI) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }
    
    /**
     * Restituisce la lista dei documenti di fatrurazione relativi agli ordini NSO
     */
    public String listOrdiniTutti() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaOrdini(LIST_ORDINI_TUTTI) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }
    
	/**
	 * Restituisce la lista degli ordini NSO
	 */
	private String getListaOrdini(String fromPage) {
		
		String sessionId = SESSIONID_MAP.get(fromPage);
		
		OrdiniSearchBean finder = (sessionId != null 
				? (OrdiniSearchBean) this.session.get(sessionId) : null);
			
		// se viene riaperta una ricerca salvata in precedenza, 
		// si ricaricano i filtri con i valori della sessione...
		if(finder != null && fromPage != null) {
			String lastFrom = (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER) +
			                  (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
			if(!(FROM_PAGE_OWNER + fromPage).equalsIgnoreCase(lastFrom)) {
				this.model.restoreFrom(finder);
			}
		}
			
		if ("1".equals(this.last) && finder != null) {
			// se si richiede il rilancio dell'ultima estrazione effettuata,
			// allora si prendono dalla sessione i filtri applicati e si
			// caricano nel presente oggetto
			this.model.restoreFrom(finder);
		}
		
		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}

		// validazione delle date (se valorizzate)
		boolean dateOk = true;

		Date dtPubblicazioneDa = null;
		try {
			dtPubblicazioneDa = (Date) LocaleConvertUtils.convert(StringUtils.stripToNull(this.model.getDataPubblicazioneDa()),
					java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_BANDO", DA_DATA, this.model.getDataPubblicazioneDa());
			this.model.setDataPubblicazioneDa(null);
			dateOk = false;
		}

		Date dtPubblicazioneA = null;
		try {
			dtPubblicazioneA = (Date) LocaleConvertUtils.convert(StringUtils.stripToNull(this.model.getDataPubblicazioneA()),
					java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_BANDO", A_DATA, this.model.getDataPubblicazioneA());
			this.model.setDataPubblicazioneA(null);
			dateOk = false;
		}
		
		Date dtScadenzaDa = null;		
		try {
			dtScadenzaDa = (Date) LocaleConvertUtils.convert(StringUtils.stripToNull(this.model.getDataScadenzaDa()), 
					java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_SCADENZA_BANDO", DA_DATA, this.model.getDataScadenzaDa());
			this.model.setDataScadenzaDa(null);
			dateOk = false;
		}
		
		Date dtScadenzaA = null;
		try {
			dtScadenzaA = (Date) LocaleConvertUtils.convert(StringUtils.stripToNull(this.model.getDataScadenzaA()),
					java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_SCADENZA_BANDO", A_DATA, this.model.getDataScadenzaA());
			this.model.setDataScadenzaA(null);
			dateOk = false;
		}
	
		Integer statoOrdine = null;
		if(StringUtils.isNotEmpty(this.model.getStato())) {
			statoOrdine = Integer.parseInt(this.model.getStato());
		}

		// prepara i filtri per della ricerca
		Filter filtri = new Filter();
		filtri.setCig(this.model.getCig());
		//filtri.setExpiryDateFrom(dtScadenzaA);		// DA RIVEDERE
		//filtri.setExpiryDateTo(dtScadenzaA);			// DA RIVEDERE
		//filtri.setOrderDateFrom(dtPubblicazioneA);	// DA RIVEDERE
		//filtri.setOrderDateTo(dtPubblicazioneA);		// DA RIVEDERE
		filtri.setTender(this.model.getGara());
		filtri.setOrderStatus(statoOrdine);

			
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// recupera il codice impresa associato all'utente
				String codimp = (String) this.session.get(PortGareSystemConstants.SESSION_ID_IMPRESA);
				if(StringUtils.isEmpty(codimp)) {	
					codimp = this.bandiManager.getIdImpresa(userDetails.getUsername());
					this.session.put(PortGareSystemConstants.SESSION_ID_IMPRESA, codimp);
				}
				
//				int startIndex = 0;
//				if(this.model.getCurrentPage() <= 0 ) {
//					this.model.setCurrentPage(1); 
//					startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
//				}
				
				if(fromPage.equalsIgnoreCase(LIST_ORDINI_DAVALUTARE)) {
					filtri.setOrderStatus(OrderStatusEnum.IN_ATTESA_CONFERMA.getValue());
					this.listaOrdini = this.ordiniManager.getPagedOrderListFO(
								codimp, 
								this.model.getCurrentPage(), 
								this.model.getiDisplayLength(),
								filtri);
				}				
				if(fromPage.equalsIgnoreCase(LIST_ORDINI_CONFERMATI)) {					
					filtri.setOrderStatus(OrderStatusEnum.ACCETTATO.getValue());
					this.listaOrdini = this.ordiniManager.getPagedOrderListFO(
							codimp, 
							this.model.getCurrentPage(), 
							this.model.getiDisplayLength(),
							filtri);
					
					filtri.setOrderStatus(OrderStatusEnum.ACCETTATO_AUTOMATICAMENTE.getValue());
					SearchResult<NsoWsOrder> list = this.ordiniManager.getPagedOrderListFO(
							codimp, 
							this.model.getCurrentPage(), 
							this.model.getiDisplayLength(),
							filtri);
										
					this.listaOrdini.getDati().addAll(list.getDati());
					this.listaOrdini.setNumTotaleRecord(this.listaOrdini.getNumTotaleRecord() + 
														list.getNumTotaleRecord());
					this.listaOrdini.setNumTotaleRecordFiltrati(this.listaOrdini.getNumTotaleRecordFiltrati() + 
														list.getNumTotaleRecordFiltrati());
				}  				
				if(fromPage.equalsIgnoreCase(LIST_ORDINI_TUTTI)) {
					this.listaOrdini = this.ordiniManager.getPagedOrderListFO(
							codimp, 
							this.model.getCurrentPage(), 
							this.model.getiDisplayLength(),
							filtri);
				}  
				
				this.model.processResult(this.listaOrdini.getNumTotaleRecord(), 
        				 				 this.listaOrdini.getNumTotaleRecordFiltrati());

//				this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(
//						this.listaOrdini);
				
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form...
				if(sessionId != null) {
					this.session.put(sessionId, this.model);
				}
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, fromPage);
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER, FROM_PAGE_OWNER);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "getListaOrdini");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}
	
	
	
}
