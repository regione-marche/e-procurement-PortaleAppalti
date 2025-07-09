package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;

import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.CsvExport;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista bandi.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class BandiFinderAction extends BaseSearchAction<BandiSearchBean, GaraType> {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -3463997215453176670L;
		
	private static final String STATO_IN_CORSO					= "1";
	private static final String STATO_IN_AGGIUDICAZIONE			= "2";
	private static final String STATO_CONCLUSE					= "3";
	
	private static final int GARE_PRIVATISTICHE_VENDITA			= 1;
	private static final int GARE_PRIVATISTICHE_ACQUISTO		= 2;
		
			
	//*****************************************************************************************************************
	// inizializzazione delle costanti per le liste e ricerche...
	// NB: il valore delle costanti corrisponde alla action!!!
	public static final String LIST_ALL_BANDI					= "listAllBandi";
	public static final String LIST_ALL_IN_CORSO 				= "listAllInCorso";
	public static final String LIST_ALL_RICHIESTE_OFFERTA 		= "listAllRichiesteOfferta";
	public static final String LIST_ALL_RICHIESTE_DOCUMENTI		= "listAllRichiesteDocumenti";
	public static final String LIST_ALL_ASTE_IN_CORSO 			= "listAllAsteInCorso";
	public static final String LIST_ALL_SCADUTI 				= "listAllScaduti";
	public static final String SEARCH 							= "search";
	public static final String SEARCH_PROCEDURE 				= "searchProcedure";
	public static final String SEARCH_BANDI_CON_ESITO		    = "searchBandiConEsito";
	// FNM gare privatistiche acquisto/vendita
	private static final String LIST_ALL_ACQ_SCADUTI			= "listAllAcqRegimePrivScaduti";
	private static final String LIST_ALL_ACQ_RICHIESTE_OFFERTA	= "listAllRichiesteOffertaAcqRegimePriv";
	private static final String SEARCH_PROCEDURE_ACQ 			= "searchProcedureAcqRegimePriv";
	private static final String LIST_ALL_ACQ_IN_CORSO 			= "listAllAcqRegimePrivInCorso";
	private static final String LIST_ALL_VEND_SCADUTI			= "listAllVendRegimePrivScaduti";
	private static final String LIST_ALL_VEND_RICHIESTE_OFFERTA	= "listAllRichiesteOffertaVendRegimePriv";
	private static final String SEARCH_PROCEDURE_VEND 			= "searchProcedureVendRegimePriv";
	private static final String LIST_ALL_VEND_IN_CORSO 			= "listAllVendRegimePrivInCorso";
		
	// crea l'associazione lista/ricerca con il proprio session id...
	private static final Map<String, String> SESSIONID_MAP = new HashMap<String, String>() {{
	    put(LIST_ALL_BANDI, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI);
		put(LIST_ALL_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_BANDI);
		put(LIST_ALL_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_RICHIESTE_OFFERTA_BANDI);
		put(LIST_ALL_RICHIESTE_DOCUMENTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_RICHIESTE_DOCUMENTI_BANDI);
		put(LIST_ALL_ASTE_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_ASTE_IN_CORSO_BANDI);
		put(LIST_ALL_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_SCADUTI_BANDI);
		put(SEARCH, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI);
		put(SEARCH_PROCEDURE, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_PROC_AGG);
		put(SEARCH_BANDI_CON_ESITO, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_CON_ESITO);
		// FNM gare privatistiche acquisto/vendita
		put(LIST_ALL_ACQ_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_SCADUTI_BANDI);
		put(LIST_ALL_ACQ_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_RICHIESTE_OFFERTA_BANDI);
		put(SEARCH_PROCEDURE_ACQ, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_ACQ);
		put(LIST_ALL_ACQ_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_IN_CORSO_BANDI);
		put(LIST_ALL_VEND_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_SCADUTI_BANDI);
		put(LIST_ALL_VEND_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_RICHIESTE_OFFERTA_BANDI);
		put(SEARCH_PROCEDURE_VEND, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_VEND);
		put(LIST_ALL_VEND_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_IN_CORSO_BANDI);
	}};
	//*****************************************************************************************************************

	
	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
		
	private String statoInApertura; 				// MEMBRO NON ESPOSTO: parametro "ricercaBandi.statoApertura"
	private InputStream inputStream;
		
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public SearchResult<GaraType> getListaBandi() {
		return list;
	}

	/**
	 * ...
	 */
	@Override
	protected BandiSearchBean newModel() {
		return new BandiSearchBean();
	}

	/**
	 * ...
	 */
	@Override
	protected String getFromPageOwner() {
		return FROM_PAGE_OWNER_BANDI;
	}
	
	/**
	 * ...
	 */
	@Override
	protected String getModelSessionId() {
		return SESSIONID_MAP.get(fromPage);
	}

	/**
	 * ... 
	 */
	private boolean isRicercaProcedure(String fromPage) {
		return fromPage.equalsIgnoreCase(LIST_ALL_BANDI)
			   || fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO)
			   || fromPage.equalsIgnoreCase(LIST_ALL_RICHIESTE_OFFERTA)
			   || fromPage.equalsIgnoreCase(LIST_ALL_RICHIESTE_DOCUMENTI)
			   || fromPage.equalsIgnoreCase(LIST_ALL_ASTE_IN_CORSO)
			   || fromPage.equalsIgnoreCase(LIST_ALL_SCADUTI)
			   || fromPage.equalsIgnoreCase(SEARCH)
			   || fromPage.equalsIgnoreCase(SEARCH_PROCEDURE)
			   || fromPage.equalsIgnoreCase(SEARCH_BANDI_CON_ESITO);
	}

	/**
	 * ... 
	 */
	private boolean isRicercaProcedurePrivatisticheVendita(String fromPage) {
		return fromPage.equalsIgnoreCase(LIST_ALL_VEND_SCADUTI) 
				|| fromPage.equalsIgnoreCase(LIST_ALL_VEND_RICHIESTE_OFFERTA) 
				|| fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_VEND) 
				|| fromPage.equalsIgnoreCase(LIST_ALL_VEND_IN_CORSO);
	}
	
	/**
	 * ... 
	 */
	private boolean isRicercaProcedurePrivatisticheAcquisto(String fromPage) {
		return fromPage.equalsIgnoreCase(LIST_ALL_ACQ_SCADUTI)
			    || fromPage.equalsIgnoreCase(LIST_ALL_ACQ_RICHIESTE_OFFERTA)
			    || fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_ACQ)
			    || fromPage.equalsIgnoreCase(LIST_ALL_ACQ_IN_CORSO);
	}

	/**
	 * ... 
	 */
	private boolean isRicercaProcedurePrivatistiche(String fromPage) {
		return isRicercaProcedurePrivatisticheVendita(fromPage) || isRicercaProcedurePrivatisticheAcquisto(fromPage);
	}

	/**
	 * recupeara la configurazione "ricercaBandi.statoApertura"
	 * (Valori ammessi: 0=nessun filtro, 1=In corso [DEFAULT], 2=In aggiudicazione, 3=Conclusa)
	 */
	private String getParamStatoInApertura() {
		String value = null;
		Integer paramValue = (Integer) appParamManager.getConfigurationValue(AppParamManager.RICERCA_BANDI_STATO_APERTURA);
		if(paramValue != null) 
			value = 
				0 == paramValue.intValue() ? null
				: 1 == paramValue.intValue() ? STATO_IN_CORSO
				: 2 == paramValue.intValue() ? STATO_IN_AGGIUDICAZIONE
				: 3 == paramValue.intValue() ? STATO_CONCLUSE
				: null;
		return value;
	}
	
	/**
	 * Restituisce la lista di bandi in base ai filtr\ impostati
	 */
	@Override
	protected SearchResult<GaraType> prepareResultList(BandiSearchBean model) throws ApsException {
		// valida gli input...
		//
		list = null;
		
		statoInApertura = getParamStatoInApertura();
		
		// se e' la prima volta che viene aperta la pagina di ricerca imposta "stato" e "ordinamento"
		if(model.isFirstSearch()) {
			if(fromPage.equalsIgnoreCase(LIST_ALL_BANDI))
				// apertura di default sui  bandi in corso
				model.setStato(STATO_IN_CORSO);
			else if(StringUtils.isNotEmpty(statoInApertura))
				// apertura di default in base alla configurazione dello stato in apertura
				model.setStato(statoInApertura);
			setDefaultOrderCriteria(model.getStato());
		}
		
		// se cambia lo il filtro "stato" reimposta l'ordinamento di default associato al tipo di lista
		if(fromPage.equalsIgnoreCase(LIST_ALL_BANDI)) {
			if(lastModel != null && model.getStato() != null && !model.getStato().equals(lastModel.getStato())) {
				setDefaultOrderCriteria(model.getStato());
			}
		}

		// se e' stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		stazioneAppaltante = getCodiceStazioneAppaltante();
		if(stazioneAppaltante != null) {
			model.setStazioneAppaltante(stazioneAppaltante);
		}

		// validazione delle date (se valorizzate)
		boolean dateOk = model.checkDataPubblicazioneDa(this, fromPage)
						 && model.checkDataPubblicazioneA(this, fromPage)
						 && model.checkScadenzaDa(this, fromPage)
						 && model.checkScadenzaA(this, fromPage);

//		// filtro CUC
//		if (StringUtils.isNotEmpty(this.model.getAltriSoggetti())) {
//			this.model.setAltriSoggetti(null);
//		}

		// gare privatistiche 
		if(isRicercaProcedurePrivatisticheVendita(fromPage))
			model.setGaraPrivatistica(GARE_PRIVATISTICHE_VENDITA);
		else if(isRicercaProcedurePrivatisticheAcquisto(fromPage))
			model.setGaraPrivatistica(GARE_PRIVATISTICHE_ACQUISTO);

		// esegui la ricerca ed estrai l'elenco dei bandi...
		//
		if (dateOk) {
			model.setTokenRichiedente(getCurrentUser().getUsername());
			// "stato" indica quali tipi di bandi cercare
			if (fromPage.equalsIgnoreCase(LIST_ALL_BANDI))
				list = bandiManager.searchBandi(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO))
				list = bandiManager.getElencoBandi(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_RICHIESTE_OFFERTA))
				list = bandiManager.getElencoBandiPerRichiesteOfferta(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_RICHIESTE_DOCUMENTI))
				list = bandiManager.getElencoBandiPerRichiesteCheckDocumentazione(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_ASTE_IN_CORSO))
				list = bandiManager.getElencoBandiPerAsteInCorso(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_SCADUTI))
				list = bandiManager.getElencoBandiScaduti(model);
			else if (fromPage.equalsIgnoreCase(SEARCH))
				list = bandiManager.searchBandi(model);
			else if (fromPage.equalsIgnoreCase(SEARCH_PROCEDURE))
				list = bandiManager.searchBandiPerProcInAggiudicazione(model);
			else if (fromPage.equalsIgnoreCase(SEARCH_BANDI_CON_ESITO))
				list = bandiManager.searchBandiConEsito(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_ACQ_SCADUTI))
				list = bandiManager.getElencoBandiScadutiPrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_ACQ_RICHIESTE_OFFERTA))
				list = bandiManager.getElencoBandiPerRichiesteOffertaPrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_ACQ))
				list = bandiManager.searchBandiPerProcInAggiudicazionePrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_ACQ_IN_CORSO))
				list = bandiManager.getElencoBandiPrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_VEND_SCADUTI))
				list = bandiManager.getElencoBandiScadutiPrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_VEND_RICHIESTE_OFFERTA))
				list = bandiManager.getElencoBandiPerRichiesteOffertaPrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(SEARCH_PROCEDURE_VEND))
				list = bandiManager.searchBandiPerProcInAggiudicazionePrivatistiche(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_VEND_IN_CORSO))
				list = bandiManager.getElencoBandiPrivatistiche(model);
			//else if (fromPage.equalsIgnoreCase(LIST_ALL_...))
			//	listaBandi = bandiManager.getElenco...(model);
			
			dataUltimoAggiornamento = getMaxDataUltimoAggiornamento(list.getDati(), GaraType::getDataUltimoAggiornamento);
		}
		
		return list;
	}

	/**
	 * Restituisce la lista di tutti i bandi in corso/scaduti
	 */
	public String listAllBandi() {
		this.setTarget( getList(LIST_ALL_BANDI) );
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi in corso
	 */
	public String listAllInCorso() {
		this.setTarget( getList(LIST_ALL_IN_CORSO) );
		return this.getTarget();
	}

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta
     */
    public String listAllRichiesteOfferta() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( getList(LIST_ALL_RICHIESTE_OFFERTA) );
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
		if (null != userDetails && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( getList(LIST_ALL_RICHIESTE_DOCUMENTI) );
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
    	if (null != userDetails && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
    		this.setTarget( getList(LIST_ALL_ASTE_IN_CORSO) );
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
		this.setTarget( getList(LIST_ALL_SCADUTI) );
		 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi in corso per gare privatistiche di acquisto
	 */
	public String listAllAcqRegimePrivInCorso() {
		this.setTarget( getList(LIST_ALL_ACQ_IN_CORSO) );
		return this.getTarget();
	}

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta per gare privatistiche di acquisto
     */
    public String listAllRichiesteOffertaAcqRegimePriv() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( getList(LIST_ALL_ACQ_RICHIESTE_OFFERTA) );
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
		this.setTarget( getList(LIST_ALL_ACQ_SCADUTI) );
		 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti i bandi in corso per gare privatistiche di vendita
	 */
	public String listAllVendRegimePrivInCorso() {
		this.setTarget( getList(LIST_ALL_VEND_IN_CORSO) );
		return this.getTarget();
	}

    /**
     * Restituisce la lista di tutti i bandi per cui e' possibile inviare
     * richieste di invio offerta per gare privatistiche di vendita
     */
    public String listAllRichiesteOffertaVendRegimePriv() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.setTarget( getList(LIST_ALL_VEND_RICHIESTE_OFFERTA) );
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
		this.setTarget( getList(LIST_ALL_VEND_SCADUTI) );
		 // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
		this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		return this.getTarget();
	}

	/**
	 * predisponi il tipo di ordinamento della lista in base allo stato selezionato 
	 */
	private void setDefaultOrderCriteria(String stato) {
		if(STATO_IN_CORSO.equals(model.getStato())) { 
			model.setOrderCriteria(InterceptorEncodedData.ORDER_CRITERIA_DATA_SCADENZA_ASC);
		} else if(STATO_IN_AGGIUDICAZIONE.equals(model.getStato())) { 
			model.setOrderCriteria(InterceptorEncodedData.ORDER_CRITERIA_DATA_SCADENZA_DESC);
		} else if(STATO_CONCLUSE.equals(model.getStato())) { 
			model.setOrderCriteria(InterceptorEncodedData.ORDER_CRITERIA_DATA_PUBBLICAZIONE_ASC);
		}
	}

	/**
	 * Verifica che tipologia di ricerca � impostata e quindi demanda l'apertura
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
		
		// se e' stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			model.setStazioneAppaltante(this.stazioneAppaltante);
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_TYPE_SEARCH_BANDI,
						 PortGareSystemConstants.VALUE_TYPE_SEARCH_BANDI);
		
		return this.getTarget();
	}
	
	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	public String find() {
		this.setTarget( getList(SEARCH) );
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
			this.setTarget( getList(SEARCH_PROCEDURE) );
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
		this.setTarget( getList(SEARCH_BANDI_CON_ESITO) );
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
			this.setTarget( getList(SEARCH_PROCEDURE_ACQ) );
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
			this.setTarget( getList(SEARCH_PROCEDURE_VEND) );
//		    // si rimuove lo stato "in corso" in quanto non ha senso in questo caso
//			this.getMaps().get(InterceptorEncodedData.LISTA_STATI_GARA).remove("1");
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}
		
	/**
	 * esporta la lista 
	 */
	public String export() {
		this.setTarget("export");
		
		try {
			// recupera i filtri dell'ultima ricerca
			fromPage = (String)session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
	    	model = ("1".equals(this.last)
	    	    	// se si richiede il rilancio dell'ultima estrazione effettuata,
	    	        // allora si prendono dalla sessione i filtri applicati e si caricano nel presente oggetto
	    			? (BandiSearchBean) session.get(getModelSessionId())
	        		: getModel()
	        );
	    	model.setiDisplayLength(Integer.MAX_VALUE);
			
	    	// recupera la lista dei risultati
	    	SearchResult<GaraType> list = prepareResultList(model);

	    	// prepare l'export CSV
	    	CsvExport csv = new CsvExport();
	    	
			// intestazione...
	        csv.writeLine(
	        		getI18nLabel("LABEL_STAZIONE_APPALTANTE")
	        		, getI18nLabel("LABEL_TITOLO")
	        		, getI18nLabel("LABEL_TIPO_APPALTO")
	        		, getI18nLabel("LABEL_CIG")
	        		, getI18nLabel("LABEL_IMPORTO_BANDO")
	        		, getI18nLabel("LABEL_DATA_PUBBLICAZIONE_BANDO")
	        		, getI18nLabel("LABEL_DATA_SCADENZA_BANDO")
	        		, getI18nLabel("LABEL_RIFERIMENTO_PROCEDURA")
	        		, getI18nLabel("LABEL_STATO_GARA")
	        		, getI18nLabel("LABEL_IS_PNRR")
	        		, getI18nLabel("LABEL_IS_GREEN")
	        		, getI18nLabel("LABEL_IS_RECYCLE")
	        		, "Url"
	        );
	        
			// ...dati
	        list.getDati().forEach(bando ->
	    			csv.writeLine(
	    					bando.getStazioneAppaltante()
	    					, bando.getOggetto()
	    					, bando.getTipoAppalto()
	    					, (SEARCH_BANDI_CON_ESITO.equalsIgnoreCase(fromPage) ? csv.wrapForExcel(bando.getCig()) : "")
	    					, (!"7".equals(bando.getIterGara()) && bando.getNascondiImportoBaseGara() != 1
	    							? csv.moneyToString(bando.getImporto()) 
	    							: ""
	    					)
	    					, csv.dateToString(bando.getDataPubblicazione())
	    					, csv.dateToString(bando.getDataTermine()) + (bando.getOraTermine() != null 
	    							? " " + getI18nLabel("LABEL_ENTRO_LE_ORE") + " " + bando.getOraTermine() 
	    							: ""
	    					)
	    					, csv.wrapForExcel(bando.getCodice())
	    					, (bando.getStato() != null ? bando.getStato() + (StringUtils.isNotEmpty(bando.getEsito()) ? " - " + bando.getEsito() : ""): "")
	    					, ("1".equals(bando.getIsPnrr()) ? getI18nLabel("YES") : "")
	    					, ("1".equals(bando.getIsGreen()) ? getI18nLabel("YES") : "")
	    					, ("1".equals(bando.getIsRecycle()) ? getI18nLabel("YES") : "")
	    					, csv.getLinkTo("it/ppgare_bandi_lista.wp?actionPath=/ExtStr2/do/FrontEnd/Bandi/view.action&currentFrame=7&codice=" + bando.getCodice())
					)
	        );
        
			this.inputStream = csv.getStream();
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
        return this.getTarget();
	}
	
}
