package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi;

import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista avvisi.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class AvvisiFinderAction extends BaseSearchAction<AvvisiSearchBean, AvvisoType> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6503418819280258333L;
	
	// elenco degli stati di un avviso
	private static final String STATO_IN_CORSO				= "1";
	private static final String STATO_SCADUTI 				= "2";

	//*****************************************************************************************************************
	// inizializzazione delle costanti per le liste e ricerche...
	// NB: il valore delle costanti corrisponde alla action!!!
	public static final String LIST_ALL_AVVISI				= "listAllAvvisi";
	public static final String LIST_ALL_IN_CORSO			= "listAllInCorso";
	public static final String LIST_ALL_SCADUTI 			= "listAllScaduti";
	public static final String SEARCH 						= "search";
	
	private static final Map<String, String> SESSIONID_MAP = new HashMap<String, String>() {{
	    put(LIST_ALL_AVVISI, PortGareSystemConstants.SESSION_ID_LIST_ALL_AVVISI);
		put(LIST_ALL_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_AVVISI);
		put(LIST_ALL_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_SCADUTI_AVVISI);
		put(SEARCH, PortGareSystemConstants.SESSION_ID_SEARCH_AVVISI);
	}};
	//*****************************************************************************************************************

	private IAvvisiManager avvisiManager;
	
	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}

	public SearchResult<AvvisoType> getListaAvvisi() {
		return list;
	}
	
	/**
	 * ...
	 */
	@Override
	protected AvvisiSearchBean newModel() {
		return new AvvisiSearchBean();
	}

	/**
	 * ...
	 */
	@Override
	protected String getFromPageOwner() {
		return FROM_PAGE_OWNER_AVVISI;
	}	
	
	/**
	 * ...
	 */
	@Override
	protected String getModelSessionId() {
		return SESSIONID_MAP.get(fromPage);
	}
	
	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	@Override
	protected SearchResult<AvvisoType> prepareResultList(AvvisiSearchBean model) throws ApsException {
		// valida gli input...
		//
		list = null;
		
		// se e' la prima volta che viene aperta la pagina di ricerca imposta "stato" e "ordinamento"
		if(model.isFirstSearch()) {
			if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO))
				// apertura di default sugli  avvisi in corso
				model.setStato(STATO_IN_CORSO);
//			setDefaultOrderCriteria(model.getStato());
		}
		
//		// imposta l'ordinamento di default associato al tipo di lista
//		// se cambia il filtro "stato" (lo stato indica il tipo di lista) 
//		if(fromPage.equalsIgnoreCase(LIST_ALL_AVVISI)) {
//			if(lastModel != null && model.getStato() != null && !model.getStato().equals(lastModel.getStato())) {
//				setDefaultOrderCriteria(model.getStato());
//			}
//		}


		// se e' stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		stazioneAppaltante = getCodiceStazioneAppaltante();
		if (stazioneAppaltante != null)
			model.setStazioneAppaltante(stazioneAppaltante);

		// conversione delle date se valorizzate
		// validazione delle date (se valorizzate)
		boolean dateOk = model.checkDataPubblicazioneDa(this, fromPage)
						 && model.checkDataPubblicazioneA(this, fromPage)
						 && model.checkScadenzaDa(this, fromPage)
						 && model.checkScadenzaA(this, fromPage);

		// esegui la ricerca ed estrai l'elenco dei bandi...
		//
		if (dateOk) {
			// estrazione dell'elenco degli avvisi
			if (fromPage.equalsIgnoreCase(LIST_ALL_AVVISI))
				list = avvisiManager.searchAvvisi(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO))
				list = avvisiManager.getElencoAvvisi(model);
			else if (fromPage.equalsIgnoreCase(LIST_ALL_SCADUTI))
				list = avvisiManager.getElencoAvvisiScaduti(model);
			else if (fromPage.equalsIgnoreCase(SEARCH))
				list = avvisiManager.searchAvvisi(model);

			dataUltimoAggiornamento = getMaxDataUltimoAggiornamento(list.getDati(), AvvisoType::getDataUltimoAggiornamento);
		}
		
		return list;
	}
	
//	/**
//	 * predisponi il tipo di ordinamento della lista in base allo stato selezionato 
//	 */
//	private void setDefaultOrderCriteria(String stato) {
//		if(STATO_IN_CORSO.equals(model.getStato())) { 
//			model.setOrderCriteria(InterceptorEncodedData.ORDER_CRITERIA_DATA_SCADENZA_ASC);
//		} else if(STATO_SCADUTI.equals(model.getStato())) { 
//			model.setOrderCriteria(InterceptorEncodedData.ORDER_CRITERIA_DATA_SCADENZA_DESC);
//		}
//	}
	
	/**
	 * Restituisce la lista di tutti gli avvisi in corso\scaduti
	 */
	public String listAllAvvisi() {
		this.setTarget(getList(LIST_ALL_AVVISI) );
		return this.getTarget();
	}
	
	/**
	 * Restituisce la lista di tutti gli avvisi in corso
	 */
	public String listAllInCorso() {
		this.setTarget(getList(LIST_ALL_IN_CORSO) );
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti gli avvisi scaduti
	 */
	public String listAllScaduti() {
		this.setTarget(getList(LIST_ALL_SCADUTI) );
		return this.getTarget();
	}

	/**
	 * Apre la form di ricerca degli avvisi.
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
						 PortGareSystemConstants.VALUE_TYPE_SEARCH_AVVISI);
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	public String find() {
		this.setTarget(getList(SEARCH));
		return this.getTarget();
	}

}
