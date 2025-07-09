package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti;

import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.EsitoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.CsvExport;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IEsitiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista esiti.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class EsitiFinderAction extends BaseSearchAction<EsitiSearchBean, EsitoType> {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -218499105868023233L;

	//*****************************************************************************************************************
	// inizializzazione delle costanti per le liste e ricerche...
	// NB: il valore delle costanti corrisponde alla action!!!
	public static final String LIST_ALL_IN_CORSO		= "listAllInCorso";
	public static final String LIST_ALL_AFFIDAMENTI		= "listAllAffidamenti";
	public static final String SEARCH 					= "search";
	//*****************************************************************************************************************

	private IEsitiManager esitiManager;
	private InputStream inputStream;

    public void setEsitiManager(IEsitiManager esitiManager) {
		this.esitiManager = esitiManager;
	}

	public SearchResult<EsitoType> getListaEsiti() {
		return list;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * ...
	 */
	@Override
	protected EsitiSearchBean newModel() {
		return new EsitiSearchBean();
	}

	/**
	 * ...
	 */
	@Override
	protected String getFromPageOwner() {
		return FROM_PAGE_OWNER_ESITI;
	}	
	
	/**
	 * ...
	 */
	@Override
	protected String getModelSessionId() {
		String sessionId = null;
		if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO))
			sessionId = PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_ESITI;
		if(fromPage.equalsIgnoreCase(LIST_ALL_AFFIDAMENTI))
			sessionId = PortGareSystemConstants.SESSION_ID_LIST_ALL_AFFIDAMENTI_ESITI;
		if(fromPage.equalsIgnoreCase(SEARCH))
			sessionId = PortGareSystemConstants.SESSION_ID_SEARCH_ESITI;
		//return SESSIONID_MAP.get(fromPage);
		return sessionId;
	}

	/**
	 * Restituisce la lista degli esiti in base ai filtri impostati
	 */
	@Override
	protected SearchResult<EsitoType> prepareResultList(EsitiSearchBean model) throws ApsException {
		// valida gli input...
		//
		list = null;
		fromSearch = false;
		
//		EsitiSearchBean finder = (sessionId != null ? (EsitiSearchBean) session.get(sessionId) : null);
//		
//		// se viene riaperta una ricerca salvata in precedenza, 
//		// si ricaricano i filtri con i valori della sessione...
//		if(!multipleSearchInRequest && finder != null && fromPage != null) {
//			String lastFrom = (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER) +
//            (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
//			if(!(FROM_PAGE_OWNER + fromPage).equalsIgnoreCase(lastFrom)) {
//				this.model.restoreFrom(finder);
//			}
//		}
//
//		if ("1".equals(this.last)) {
//			// se si richiede il rilancio dell'ultima estrazione effettuata,
//			// allora si prendono dalla sessione i filtri applicati e si
//			// caricano nel presente oggetto
//			this.model.restoreFrom(finder);
//		}

		// se e' la prima volta che viene aperta la pagina di ricerca imposta "stato" e "ordinamento"
		if(model.isFirstSearch()) {
//			if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO))
//				// apertura di default sugli  avvisi in corso
//				model.setStato(STATO_IN_CORSO);
//			setDefaultOrderCriteria(model.getStato());
		}
				
//		// imposta l'ordinamento di default associato al tipo di lista
//		// se cambia il filtro "stato" (lo stato indica il tipo di lista) 
//		if(fromPage.equalsIgnoreCase(LIST_ALL_AVVISI)) {
//			if(lastModel != null && model.getStato() != null && !model.getStato().equals(lastModel.getStato())) {
//				setDefaultOrderCriteria(model.getStato());
//			}
//		}


		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			model.setStazioneAppaltante(this.stazioneAppaltante);
		}

		// validazione delle date (se valorizzate)
		boolean dateOk = model.checkDataPubblicazioneDa(this, fromPage)
						 && model.checkDataPubblicazioneA(this, fromPage);
		
		if (dateOk) {
			// estrazione dell'elenco degli esiti
			if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO)) {
				list = this.esitiManager.getElencoEsiti(model);
			} else if(fromPage.equalsIgnoreCase(LIST_ALL_AFFIDAMENTI)) {
				list = this.esitiManager.getElencoEsitiAffidamenti(model);
			} else if(fromPage.equalsIgnoreCase(SEARCH)) {
				list = this.esitiManager.searchEsiti(model);
			}

			dataUltimoAggiornamento = getMaxDataUltimoAggiornamento(list.getDati(), EsitoType::getDataUltimoAggiornamento);
		}
		
		return list;
	}
	
	/**
	 * ... 
	 */
	public String listAllInCorso() {
		this.setTarget(getList(LIST_ALL_IN_CORSO));
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti gli esiti relativi ad operatori economici.
	 */
	public String listAllAffidamenti() {
		this.setTarget(getList(LIST_ALL_AFFIDAMENTI));
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
	public String find() {
		this.setTarget(getList(SEARCH));
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
	    			? (EsitiSearchBean) session.get(getModelSessionId())
	        		: getModel()
	        );
	    	model.setiDisplayLength(Integer.MAX_VALUE);
			
	    	// recupera la lista dei risultati
	    	SearchResult<EsitoType> list = prepareResultList(model);
			
	    	// prepare l'export CSV
	    	CsvExport csv = new CsvExport();
	    	
			// intestazione...
	        csv.writeLine(
	        		getI18nLabel("LABEL_STAZIONE_APPALTANTE")
	        		, getI18nLabel("LABEL_TITOLO")
	        		, getI18nLabel("LABEL_TIPO_APPALTO")
	        		, getI18nLabel("LABEL_CIG")
	        		, getI18nLabel("LABEL_DATA_PUBBLICAZIONE_ESITO")
	        		, getI18nLabel("LABEL_RIFERIMENTO_PROCEDURA")
	        		, getI18nLabel("LABEL_STATO_GARA")
	        		, getI18nLabel("LABEL_IS_PNRR")
	        		, getI18nLabel("LABEL_IS_GREEN")
	        		, getI18nLabel("LABEL_IS_RECYCLE")
	        		,"Url"
	        );
	        
	        // ...dati
	        list.getDati().forEach(bando ->
	    			csv.writeLine(
	    					bando.getStazioneAppaltante()
	    					, bando.getOggetto()
	    					, bando.getTipoAppalto()
	    					, (bando.getCig() != null ? csv.wrapForExcel(bando.getCig()) : "")
	    					, csv.dateToString(bando.getDataPubblicazione())
	    					, csv.wrapForExcel(bando.getCodice())
	    					, (bando.getStato() != null ? bando.getStato() + (StringUtils.isNotEmpty(bando.getEsito()) ? " - " + bando.getEsito() : ""): "")
	    					, ("1".equals(bando.getIsPnrr()) ? getI18nLabel("YES") : "")
	    					, ("1".equals(bando.getIsGreen()) ? getI18nLabel("YES") : "")
	    					, ("1".equals(bando.getIsRecycle()) ? getI18nLabel("YES") : "")
	    					, csv.getLinkTo("it/ppgare_esiti_lista.wp?actionPath=/ExtStr2/do/FrontEnd/Esiti/view.action&currentFrame=7&codice=" + bando.getCodice())
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
