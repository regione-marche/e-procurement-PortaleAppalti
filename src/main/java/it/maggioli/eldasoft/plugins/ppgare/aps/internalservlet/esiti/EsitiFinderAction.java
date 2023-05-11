package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.EsitoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IEsitiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista esiti.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class EsitiFinderAction extends EncodedDataAction implements
	SessionAware, ModelDriven<EsitiSearchBean> {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -218499105868023233L;

	private static final String FROM_PAGE_OWNER			= "esiti";
	private static final String LIST_ALL_IN_CORSO		= "listAllInCorso";
	private static final String LIST_ALL_AFFIDAMENTI	= "listAllAffidamenti";
	private static final String SEARCH 					= "search";

	private IEsitiManager esitiManager;
    private Map<String, Object> session;

	@Validate
    private EsitiSearchBean model = new EsitiSearchBean();

	@Validate(EParamValidation.DIGIT)
    private String last;

    private SearchResult<EsitoType> listaEsiti = null;
    
    private Date dataUltimoAggiornamento;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltante;

    public void setEsitiManager(IEsitiManager esitiManager) {
		this.esitiManager = esitiManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	@Override
	public EsitiSearchBean getModel() {
		return this.model;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public SearchResult<EsitoType> getListaEsiti() {
		return listaEsiti;
	}
	
	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}
	
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	/**
	 * calcola la MAX data di ultimo aggiormento nell'elenco esiti  
	 */
	private Date getMaxDataUltimoAggiornamento(List<EsitoType> lista) {
		Date dta = null;
		if(lista != null) {			
			for(int i = 0; i < lista.size(); i++) {
				if( dta == null ) {
					dta = lista.get(i).getDataUltimoAggiornamento();
				} 
				if( lista.get(i).getDataUltimoAggiornamento() != null && 
					lista.get(i).getDataUltimoAggiornamento().compareTo(dta) > 0 ) {
					dta = lista.get(i).getDataUltimoAggiornamento();					
				}				
			}
		}		
		return dta;		
	}

	/**
	 * ... 
	 */
	public String listAllInCorso() {
		this.setTarget( this.getListaEsiti(LIST_ALL_IN_CORSO) );
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti gli esiti relativi ad operatori economici.
	 */
	public String listAllAffidamenti() {
		this.setTarget( this.getListaEsiti(LIST_ALL_AFFIDAMENTI) );
		return this.getTarget();
	}

	/**
	 * Apre la form di ricerca degli esiti.
	 */
	public String openSearch() {
		this.setTarget(SUCCESS);
		
		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_TYPE_SEARCH_BANDI,
						 PortGareSystemConstants.VALUE_TYPE_SEARCH_ESITI);
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	private String getListaEsiti(String fromPage) {
		this.setTarget(SUCCESS);
		
		this.listaEsiti = null;
		
		String sessionId = null;
		boolean fromSearch = false;
		if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO)) {
			sessionId = PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_ESITI;
		}
		if(fromPage.equalsIgnoreCase(LIST_ALL_AFFIDAMENTI)) {
			sessionId = PortGareSystemConstants.SESSION_ID_LIST_ALL_AFFIDAMENTI_ESITI;
		}
		if(fromPage.equalsIgnoreCase(SEARCH)) {
			sessionId = PortGareSystemConstants.SESSION_ID_SEARCH_ESITI;
		}
		
		EsitiSearchBean finder = (sessionId != null 
				? (EsitiSearchBean) this.session.get(sessionId) : null);
		
		// se viene riaperta una ricerca salvata in precedenza, 
		// si ricaricano i filtri con i valori della sessione...
		if(finder != null && fromPage != null) {
			String lastFrom = (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER) +
            (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
			if(!(FROM_PAGE_OWNER + fromPage).equalsIgnoreCase(lastFrom)) {
				this.model.restoreFrom(finder);
			}
		}

		if ("1".equals(this.last)) {
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
		boolean dateOk = model.checkDataPubblicazioneDa(this, fromPage)
							& model.checkDataPubblicazioneA(this, fromPage);
		
		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			try {
				// estrazione dell'elenco degli esiti
				if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO)) {
					this.listaEsiti = this.esitiManager.getElencoEsiti(model);
				} else if(fromPage.equalsIgnoreCase(LIST_ALL_AFFIDAMENTI)) {
					this.listaEsiti = this.esitiManager.getElencoEsitiAffidamenti(model);
				} else if(fromPage.equalsIgnoreCase(SEARCH)) {
					this.listaEsiti = this.esitiManager.searchEsiti(model);
				}

				this.model.processResult(this.listaEsiti.getNumTotaleRecord(), 
		 				 				 this.listaEsiti.getNumTotaleRecordFiltrati());
				
				this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(this.listaEsiti.getDati());
				
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				if(sessionId != null) {
					this.session.put(sessionId, this.model);
				}
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, fromSearch);
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, fromPage);
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER, FROM_PAGE_OWNER);
				
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, fromPage);
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}

		return this.getTarget();
	}

	
	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	public String find() {
		this.setTarget( this.getListaEsiti(SEARCH) );
		return this.getTarget();
	}
	
}
