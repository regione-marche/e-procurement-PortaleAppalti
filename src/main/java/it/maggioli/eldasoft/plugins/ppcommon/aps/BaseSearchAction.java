package it.maggioli.eldasoft.plugins.ppcommon.aps;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Classe base per la gestione delle ricerca e visualizzazione delle liste (bandi, avvisi, esiti, ...)
 * 
 * @version ...
 * @author ...
 */
public class BaseSearchAction<M extends BaseSearchBean, L> extends EncodedDataAction implements SessionAware, ModelDriven<M> {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6391089082835925102L;
	
	/**
	 * from page onwer delle varie ricerche  
	 */
	protected static final String FROM_PAGE_OWNER_BANDI			= "bandi";
	protected static final String FROM_PAGE_OWNER_ESITI			= "esiti";
	protected static final String FROM_PAGE_OWNER_AVVISI		= "avvisi";
	
		
	protected Map<String, Object> session;	
	
	// "last=1" indica che e' stata aperta una ricerca e si sta navigando tra i dettagli
	// e quindi ritornando alla ricerca vanno ripresentati i filtri memorizzati in sessione
	// viceversa si apre una nuova ricerca senza recuperare stati precedenti
	@Validate(EParamValidation.DIGIT)
	protected String last;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
    protected String stazioneAppaltante;
	
	protected Date dataUltimoAggiornamento;

	@Validate
	protected M model = newModel();									//newPropertyInstance(this, "model");
	
	protected SearchResult<L> list = null;							// lista dei risultati della ricerca

	// MEMBRI NON ESPOSTI
	protected String fromPage;										// indica il tipo di pagina richiesta
	protected boolean fromSearch;									// indica se ... (dipende da getFromPageOwner())
	protected boolean multipleSearchInRequest;						// indica se ci sono piu form di ricerca nella stessa pagina
	protected M lastModel;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}
	
	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}
	
	@Override
	public M getModel() {
		return model;
	}	
	
//	/**
//	 * recupera l'elenco di tutte le properties dell'oggetto 
//	 */
//	private static List<Field> getAllFields(Class<?> cls) {
//        List<Field> fields = new ArrayList<Field>();
//        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
//            fields.addAll(Arrays.asList(c.getDeclaredFields()));
//        }
//        return fields;
//    }
//	
//	/**
//	 * se "property" non e' stata inizializzata, crea nuova nuovca istanza 
//	 */
//	private static Object newPropertyInstance(Object obj, String property) {
//		Object prop = null;
//		//Field[] fields = cls.getDeclaredFields();
//		final List<Field> allFields = getAllFields(obj.getClass());
//		for (Field field : allFields) 
//	    	try {
//    			if(property.equalsIgnoreCase(field.getName())) {
//System.out.println("newPropertyInstance(...) => "
//	+ "\n name=" + field.getName() 
//	+ "\n class=" + field.getType().getClass()
//	+ "\n ComponentType=" + field.getType().getComponentType()
//	+ "\n GenericSuperclass=" + field.getType().getGenericSuperclass()
//	+ "\n getGenericType=" + field.getGenericType()
//	+ "\n getGenericType.Name=" + field.getGenericType().getTypeName()
//	+ "\n getGenericType.class=" + field.getGenericType().getClass()
//);    				
//    				
//    				prop = field.getType().getClass().newInstance();
//    				break;
//    			}
//			} catch (Exception e) {
//				//...
//			}
//	    
//System.out.println("initProperty(...) => " + property + "=" + prop);
//	    return prop;
//	}

	/**
	 * restituisce il session ID associato a "model"
	 */
	protected M newModel() {
		// DA RIDEFINIRE NELLA CLASSE DERIVATA!!!
		//i.e. return new BandiSearchBean();
		return null;
	};

	/**
	 * restituisce il session ID associato a "model"
	 */
	protected String getModelSessionId() {
		// DA RIDEFINIRE NELLA CLASSE DERIVATA!!!
		//i.e. return SESSIONID_MAP.get(fromPage); 
		return null;
	};

	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	protected String getFromPageOwner() {
		// DA RIDEFINIRE NELLA CLASSE DERIVATA!!!
		//i.e. return FROM_PAGE_OWNER_BANDI;
		return null;
	}
	
	/**
	 * recupera i filtri dell'ultima ricerca dalla sessione
	 */
	private M getModelFromSession() {
		String sessionId = getModelSessionId();
		M obj = (M)(StringUtils.isNotEmpty(sessionId) ? session.get(sessionId) : null); 
		return obj;
	}

	/**
	 * prepara la struttura della lista lista dei risultati, inviando la chiamata al servizio
	 */
	protected SearchResult<L> prepareResultList(M model) throws ApsException {
		// DA RIDEFINIRE NELLA CLASSE DERIVATA!!!
		// List<...> list = bandiManagar.getElenco.....(model);
		return null; 
	}

	/**
	 * chiamata standard da utilizzare nella action derivata per eseguire una ricerca in base ai filtri impostati
	 */
	protected String getList(String from) {
		setTarget(SUCCESS);
		try {
			// 1) prepara i parametri per gestire la ricerca
			//
			//fromSearch = ???											// dipende da fromPageOwner	
			fromPage = from;
			multipleSearchInRequest = isMultipleSearchInRequest();		// ci sono piu' form di ricerca nella stessa pagina ?
			lastModel = getModelFromSession();
			model = getModel();
			
			// 2) aggiorna e sincronizza lo stato di "model" verificando i dati memorizzati in sessione
			//
			if(model != null) {
				String fromPageId = getFromPageOwner() + fromPage;
				String lastFromPageId = (String)session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER) +
		           						(String)session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
				boolean restoreLastModel = false;
				
				// CASO PARTICOLARE: 
				// se la stessa ricerca viene riaperta dopo aver navigato altri package diversi 
				// da quello della ricerca, verifica se e' necessario recuperare dalla sessone
				// il precedente stato...
				if(lastModel != null)
					if(fromPageId.equalsIgnoreCase(lastFromPageId) && model.isInitialState())
						restoreLastModel = true;
				
				// se viene riaperta una ricerca salvata in precedenza, 
				// si ricaricano i filtri con i valori della sessione...
				if(!multipleSearchInRequest && lastModel != null && fromPage != null)
					if( !fromPageId.equalsIgnoreCase(lastFromPageId) )
						restoreLastModel = true; 
				
				// "last=1" indica che e' stata aperta una ricerca e si sta navigando tra i dettagli
				// e quindi ritornando alla ricerca vanno ripresentati i filtri memorizzati in sessione
				// viceversa si apre una nuova ricerca senza recuperare stati precedenti 
				boolean restoreFiltriUltimaRicerca = ("1".equals(last));
				if(restoreFiltriUltimaRicerca && lastModel != null)
					restoreLastModel = true;
				
				// se necessario ripristina il modello presente in sessione
				if(restoreLastModel && lastModel != null) {
					((BaseSearchBean) model).restoreFrom(lastModel);
				}
	
				// aggiorna il contatore delle ricerche effettuate
				long n = (lastModel != null ? ((BaseSearchBean) lastModel).getSearchCount() : 0);
				((BaseSearchBean) model).setSearchCount( (n < 1 ? 1 : n) );
			}
			
			// 3) prepara la lista dei risultati...
			//
			list = prepareResultList(model);
			
			// aggiorna i risultati per il paginatore 
			((BaseSearchBean)model).processResult(list.getNumTotaleRecord(), list.getNumTotaleRecordFiltrati());
			
			// 4) salvataggio dei criteri di ricerca in sessione per la prossima riapertura della form...
			//
			String sessionId = getModelSessionId();
			if(StringUtils.isNotEmpty(sessionId)) {
				// aggiorna il contatore di ricerche effettuate quando la pagina e' aperta
				((BaseSearchBean) model).setSearchCount(((BaseSearchBean) model).getSearchCount() + 1);
				session.put(sessionId, model);
			}
			
			// salva il tipo di ricerca in sessione 
			if (!multipleSearchInRequest) {
				session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, fromSearch);
				session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER, getFromPageOwner());
				session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, fromPage);
			}
			
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, fromPage);
			ExceptionUtils.manageExceptionError(t, this);
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		return getTarget();
	}

	/**
	 * restituisce il max date della lista 
	 */
	protected Date getMaxDataUltimoAggiornamento(List<L> lista, Function<? super L, Date> getDate) {
		Date dta = null;
		if(lista != null) 
			dta = lista.stream()
				.map(getDate)	// GaraType::getDataUltimoAggiornamento
				.filter(d -> d != null)
				.max(Date::compareTo)
				.orElse(null);
		return dta;
	}

}