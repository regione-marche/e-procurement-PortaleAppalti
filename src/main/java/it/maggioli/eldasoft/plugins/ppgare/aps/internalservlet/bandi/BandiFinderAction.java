package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista bandi.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class BandiFinderAction extends EncodedDataAction implements
	SessionAware, ModelDriven<BandiSearchBean> {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -3463997215453176670L;
		
	private static final String FROM_PAGE_OWNER					= "bandi"; 
	
	// inizializzazione delle costanti per le liste e ricerche...
	// NB: il valore delle costanti corrisponde alla action!!!
	private static final String LIST_ALL_IN_CORSO 				= "listAllInCorso";
	private static final String LIST_ALL_RICHIESTE_OFFERTA 		= "listAllRichiesteOfferta";
	private static final String LIST_ALL_RICHIESTE_DOCUMENTI	= "listAllRichiesteDocumenti";
	private static final String LIST_ALL_ASTE_IN_CORSO 			= "listAllAsteInCorso";
	private static final String LIST_ALL_SCADUTI 				= "listAllScaduti";
	private static final String SEARCH 							= "search";
	private static final String SEARCH_PROCEDURE 				= "searchProcedure";
	private static final String SEARCH_BANDI_CON_ESITO		    = "searchBandiConEsito";
	// FNM gare privatistiche acquisto/vendita
	private static final String LIST_ALL_ACQ_SCADUTI			= "listAllAcqRegimePrivScaduti";
	private static final String LIST_ALL_ACQ_RICHIESTE_OFFERTA	= "listAllRichiesteOffertaAcqRegimePriv";
	private static final String SEARCH_PROCEDURE_ACQ 			= "searchProcedureAcqRegimePriv";
	private static final String LIST_ALL_ACQ_IN_CORSO 			= "listAllAcqRegimePrivInCorso";
	private static final String LIST_ALL_VEND_SCADUTI			= "listAllVendRegimePrivScaduti";
	private static final String LIST_ALL_VEND_RICHIESTE_OFFERTA	= "listAllRichiesteOffertaVendRegimePriv";
	private static final String SEARCH_PROCEDURE_VEND 			= "searchProcedureVendRegimePriv";
	private static final String LIST_ALL_VEND_IN_CORSO 			= "listAllVendRegimePrivInCorso";
		
	private static final Map<String, String> SESSIONID_MAP;
	static {
		// crea l'associazione lista/ricerca con il proprio session id...
		SESSIONID_MAP = new HashMap<String, String>();
		SESSIONID_MAP.put(LIST_ALL_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_BANDI);
		SESSIONID_MAP.put(LIST_ALL_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_RICHIESTE_OFFERTA_BANDI);
		SESSIONID_MAP.put(LIST_ALL_RICHIESTE_DOCUMENTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_RICHIESTE_DOCUMENTI_BANDI);
		SESSIONID_MAP.put(LIST_ALL_ASTE_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_ASTE_IN_CORSO_BANDI);
		SESSIONID_MAP.put(LIST_ALL_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_SCADUTI_BANDI);
		SESSIONID_MAP.put(SEARCH, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI);
		SESSIONID_MAP.put(SEARCH_PROCEDURE, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_PROC_AGG);
		SESSIONID_MAP.put(SEARCH_BANDI_CON_ESITO, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_CON_ESITO);		
		SESSIONID_MAP.put(LIST_ALL_ACQ_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_SCADUTI_BANDI);
		SESSIONID_MAP.put(LIST_ALL_ACQ_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_RICHIESTE_OFFERTA_BANDI);
		SESSIONID_MAP.put(SEARCH_PROCEDURE_ACQ, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_ACQ);
		SESSIONID_MAP.put(LIST_ALL_ACQ_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_IN_CORSO_BANDI);
		SESSIONID_MAP.put(LIST_ALL_VEND_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_SCADUTI_BANDI);
		SESSIONID_MAP.put(LIST_ALL_VEND_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_RICHIESTE_OFFERTA_BANDI);
		SESSIONID_MAP.put(SEARCH_PROCEDURE_VEND, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_VEND);
		SESSIONID_MAP.put(LIST_ALL_VEND_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_IN_CORSO_BANDI);
    }
		
	
	private IBandiManager bandiManager;

	private Map<String, Object> session;

	@Validate
    private BandiSearchBean model = new BandiSearchBean();

	@Validate(EParamValidation.DIGIT)
    private String last;

    private SearchResult<GaraType> listaBandi = null;

	@Validate(EParamValidation.DATE_DDMMYYYY)
    private Date dataUltimoAggiornamento;
	@Validate(EParamValidation.STAZIONE_APPALTANTE)
    private String stazioneAppaltante;
    
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public BandiSearchBean getModel() {
		return this.model;
	}
	
	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public SearchResult<GaraType> getListaBandi() {
		return listaBandi;
	}

	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}
	
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	/**
	 * calcola la MAX data di ultimo aggiormento nell'elenco bandi  
	 */
	private Date getMaxDataUltimoAggiornamento(List<GaraType> lista) {
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
	 * Restituisce la lista di tutti i bandi in corso
	 */
	public String listAllInCorso() {
		this.setTarget( this.getListaBandi(LIST_ALL_IN_CORSO) );
		return this.getTarget();
	}

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta
     */
    public String listAllRichiesteOfferta() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(LIST_ALL_RICHIESTE_OFFERTA) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di documenti per la comprova dei requisiti
     */
    public String listAllRichiesteDocumenti() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(LIST_ALL_RICHIESTE_DOCUMENTI) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta
     */
    public String listAllAsteInCorso() {
    	UserDetails userDetails = this.getCurrentUser();
    	if (null != userDetails
    		&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
    		this.setTarget( this.getListaBandi(LIST_ALL_ASTE_IN_CORSO) );
    	} else {
    		this.addActionError(this.getText("Errors.sessionExpired"));
    		this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    	}
		return this.getTarget();
    }

	/**
	 * Restituisce la lista di tutti i bandi scaduti.
	 */
	public String listAllScaduti() {
		this.setTarget( this.getListaBandi(LIST_ALL_SCADUTI) );
		 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi in corso per gare privatistiche di acquisto
	 */
	public String listAllAcqRegimePrivInCorso() {
		this.setTarget( this.getListaBandi(LIST_ALL_ACQ_IN_CORSO) );
		return this.getTarget();
	}

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta per gare privatistiche di acquisto
     */
    public String listAllRichiesteOffertaAcqRegimePriv() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(LIST_ALL_ACQ_RICHIESTE_OFFERTA) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }

	/**
	 * Restituisce la lista di tutti i bandi scaduti per gare privatistiche di acquisto
	 */
	public String listAllAcqRegimePrivScaduti() {
		this.setTarget( this.getListaBandi(LIST_ALL_ACQ_SCADUTI) );
		 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi in corso per gare privatistiche di vendita
	 */
	public String listAllVendRegimePrivInCorso() {
		this.setTarget( this.getListaBandi(LIST_ALL_VEND_IN_CORSO) );
		return this.getTarget();
	}

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta per gare privatistiche di vendita
     */
    public String listAllRichiesteOffertaVendRegimePriv() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(LIST_ALL_VEND_RICHIESTE_OFFERTA) );
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
    }

	/**
	 * Restituisce la lista di tutti i bandi scaduti per gare privatistiche di vendita
	 */
	public String listAllVendRegimePrivScaduti() {
		this.setTarget( this.getListaBandi(LIST_ALL_VEND_SCADUTI) );
		 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

    
	/**
	 * Verifica che tipologia di ricerca è impostata e quindi demanda l'apertura
	 * della form corrispondente.
	 */
	public String init() {
		Integer tipoRicerca = (Integer) this.session
				.get(PortGareSystemConstants.SESSION_ID_TYPE_SEARCH_BANDI);
		if (tipoRicerca == null)
			tipoRicerca = 1;
		switch (tipoRicerca) {
		case PortGareSystemConstants.VALUE_TYPE_SEARCH_BANDI:
			this.setTarget("openSearchBandi");
			break;
		case PortGareSystemConstants.VALUE_TYPE_SEARCH_ESITI:
			this.setTarget("openSearchEsiti");
			break;
		case PortGareSystemConstants.VALUE_TYPE_SEARCH_AVVISI:
			this.setTarget("openSearchAvvisi");
			break;
		}
		return this.getTarget();
	}

	/**
	 * Apre la form di ricerca dei bandi
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
						 PortGareSystemConstants.VALUE_TYPE_SEARCH_BANDI);
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	private String getListaBandi(String fromPage) {
		this.setTarget(SUCCESS);

		this.listaBandi = null;
		
		String sessionId = SESSIONID_MAP.get(fromPage);
		boolean fromSearch = (fromPage.equalsIgnoreCase(SEARCH) ||
				      		  fromPage.equalsIgnoreCase(SEARCH_PROCEDURE) ||
				      		  fromPage.equalsIgnoreCase(SEARCH_BANDI_CON_ESITO));

		BandiSearchBean finder = (sessionId != null 
			? (BandiSearchBean) this.session.get(sessionId) : null);
		
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
		stazioneAppaltante = getCodiceStazioneAppaltante();
		if(stazioneAppaltante != null) {
			model.setStazioneAppaltante(stazioneAppaltante);
		}

		// validazione delle date (se valorizzate)
		boolean dateOk = model.checkDataPubblicazioneDa(this, fromPage)
							& model.checkDataPubblicazioneA(this, fromPage)
							& model.checkScadenzaDa(this, fromPage)
							& model.checkScadenzaA(this, fromPage);

//		// filtro CUC
//		if (StringUtils.isNotEmpty(this.model.getAltriSoggetti())) {
//			this.model.setAltriSoggetti(null);
//		}

		// gare privatistiche: 1=vendita, 2=acquisto
		Integer garaPrivatistica = null;
		if(fromPage.equalsIgnoreCase(LIST_ALL_VEND_SCADUTI) ||
		   fromPage.equalsIgnoreCase(LIST_ALL_VEND_RICHIESTE_OFFERTA) ||
		   fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_VEND) ||
		   fromPage.equalsIgnoreCase(LIST_ALL_VEND_IN_CORSO)) {
			model.setGaraPrivatistica(1);
		} else if(fromPage.equalsIgnoreCase(LIST_ALL_ACQ_SCADUTI) ||
	       fromPage.equalsIgnoreCase(LIST_ALL_ACQ_RICHIESTE_OFFERTA) ||
	       fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_ACQ) ||
	       fromPage.equalsIgnoreCase(LIST_ALL_ACQ_IN_CORSO)) {
			model.setGaraPrivatistica(2);
		}
		
		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			// estrazione dell'elenco dei bandi...
			model.setTokenRichiedente(getCurrentUser().getUsername());
			try {

				//Da java 12 gli switch possono ritornare i valori, quindi, non servirebbe fare l'assegnazione ogni volta
				if (fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO))
					listaBandi = bandiManager.getElencoBandi(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_RICHIESTE_OFFERTA))
					listaBandi = bandiManager.getElencoBandiPerRichiesteOfferta(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_RICHIESTE_DOCUMENTI))
					listaBandi = bandiManager.getElencoBandiPerRichiesteCheckDocumentazione(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_ASTE_IN_CORSO))
	    			listaBandi = bandiManager.getElencoBandiPerAsteInCorso(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_SCADUTI))
					listaBandi = bandiManager.getElencoBandiScaduti(model);
				else if (fromPage.equalsIgnoreCase(SEARCH))
					listaBandi = bandiManager.searchBandi(model);
				else if (fromPage.equalsIgnoreCase(SEARCH_PROCEDURE))
					listaBandi = bandiManager.searchBandiPerProcInAggiudicazione(model);
				else if (fromPage.equalsIgnoreCase(SEARCH_BANDI_CON_ESITO))
					listaBandi = bandiManager.searchBandiConEsito(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_ACQ_SCADUTI))
					listaBandi = bandiManager.getElencoBandiScadutiPrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_ACQ_RICHIESTE_OFFERTA))
					listaBandi = bandiManager.getElencoBandiPerRichiesteOffertaPrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_ACQ))
					listaBandi = bandiManager.searchBandiPerProcInAggiudicazionePrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_ACQ_IN_CORSO))
					listaBandi = bandiManager.getElencoBandiPrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_VEND_SCADUTI))
					listaBandi = bandiManager.getElencoBandiScadutiPrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_VEND_RICHIESTE_OFFERTA))
					listaBandi = bandiManager.getElencoBandiPerRichiesteOffertaPrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_VEND))
					listaBandi = bandiManager.searchBandiPerProcInAggiudicazionePrivatistiche(model);
				else if (fromPage.equalsIgnoreCase(LIST_ALL_VEND_IN_CORSO))
					listaBandi = bandiManager.getElencoBandiPrivatistiche(model);

				
				this.model.processResult(this.listaBandi.getNumTotaleRecord(), 
		                 				 this.listaBandi.getNumTotaleRecordFiltrati());

				this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(
						this.listaBandi.getDati());
				
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form...
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
		this.setTarget( this.getListaBandi(SEARCH) );
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi relativi a procedure in
	 * aggiudicazione o concluse
	 */
	public String findProcedure() {
		this.setTarget(SUCCESS);
	
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(SEARCH_PROCEDURE) );
			 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
			this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi 
	 */
	public String findBandiConEsito() {
		this.setTarget( this.getListaBandi(SEARCH_BANDI_CON_ESITO) );
//		// si rimuove lo stato "in corso" in quanto non ha senso in questo caso
//		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

	/**
	 * Restituisce la lista delle procedure di aggiudicazione o concluse di acquisto
	 * relative alle gare privatistiche
	 */
	public String searchProcedureAcq() {
		this.setTarget(SUCCESS);
	
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(SEARCH_PROCEDURE_ACQ) );
//			// si rimuove lo stato "in corso" in quanto non ha senso in questo caso
//			this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista delle procedure di aggiudicazione o concluse di vendita
	 * relative alle gare privatistiche
	 */
	public String searchProcedureVend() {
		this.setTarget(SUCCESS);
	
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( this.getListaBandi(SEARCH_PROCEDURE_VEND) );
//		    // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
//			this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

}
