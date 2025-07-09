package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista avvisi comunicazioni atti di carattere generale.
 * 
 * @version 
 * @author 
 */
public class AvvisiComunicazioniAttiFinderAction extends EncodedDataAction implements
		SessionAware, ModelDriven<AvvisiSearchBean> 
{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -7137866211556774476L;
	
	private static final String FROM_PAGE_OWNER					= "avvisiComunicazioniAtti"; 
	private static final String LIST_AVVISI_COMUNICAZIONI_ATTI	= "listAvvisiComunicazioniAtti";
	
	private static final String STATO_IN_CORSO					= "1";
	private static final String STATO_SCADUTO					= "2";
	
	private IAvvisiManager avvisiManager;
	private Map<String, Object> session;

	@Validate
	private AvvisiSearchBean model = new AvvisiSearchBean();

	@Validate(EParamValidation.DIGIT)
	private String last;

	private SearchResult<AvvisoType> listaAvvisi = null;	
	private Date dataUltimoAggiornamento;
	private String stazioneAppaltante;
	

	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public AvvisiSearchBean getModel() {
		return this.model;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public SearchResult<AvvisoType> getListaAvvisi() {
		return listaAvvisi;
	}
	
	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	/**
	 * calcola la MAX data di ultimo aggiormento nell'elenco  
	 */
	private Date getMaxDataUltimoAggiornamento(List<AvvisoType> lista) {
		Date dta = null;
		if(lista != null) {
			for(int i = 0; i < lista.size(); i++) {
				if( dta == null )
					dta = lista.get(i).getDataUltimoAggiornamento();
				if( lista.get(i).getDataUltimoAggiornamento() != null &&
					lista.get(i).getDataUltimoAggiornamento().compareTo(dta) > 0 ) {
					dta = lista.get(i).getDataUltimoAggiornamento();
				}
			}
		}
		return dta;
	}

	/**
	 * Apre la form di ricerca degli avvisi
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
		this.setTarget( getLista() );
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti gli avvisi
	 */
	public String listAvvisi() {
		this.setTarget( getLista() );
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	private String getLista() {
		String target = SUCCESS;

		boolean multipleSearchInRequest = isMultipleSearchInRequest();

		listaAvvisi = null;
		
		String fromPage = LIST_AVVISI_COMUNICAZIONI_ATTI; 
		boolean fromSearch = false;
		AvvisiSearchBean finder = (AvvisiSearchBean) session.get(PortGareSystemConstants.SESSION_ID_SEARCH_AVVISI_COMUNICAZIONI_ATTI);
		
		// se viene riaperta una ricerca salvata in precedenza, 
		// si ricaricano i filtri con i valori della sessione...
		if (!multipleSearchInRequest && finder != null && fromPage != null) {
			String lastFrom = session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER)
							  + (String) session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
			if (!(FROM_PAGE_OWNER + fromPage).equalsIgnoreCase(lastFrom))
				model.restoreFrom(finder);
		}

		// se si richiede il rilancio dell'ultima estrazione effettuata,
		// allora si prendono dalla sessione i filtri applicati e si
		// caricano nel presente oggetto
		boolean restoreFiltriUltimaRicerca = ("1".equals(this.last));
		if (restoreFiltriUltimaRicerca) {
			if(finder != null)
				model.restoreFrom(finder);
		} else {
			// imposto la ricerca per avvisi "in corso" come default
			if(finder == null && StringUtils.isEmpty(model.getStato())) {
				model.setStato(STATO_IN_CORSO);	
			}
		}

		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		stazioneAppaltante = getCodiceStazioneAppaltante();
		if (stazioneAppaltante != null)
			model.setStazioneAppaltante(stazioneAppaltante);

		// conversione e validazione delle date (se valorizzate)
		boolean dateOk = model.checkDataPubblicazioneDa(this, fromPage)
						 && model.checkDataPubblicazioneA(this, fromPage)
						 && model.checkScadenzaDa(this, fromPage)
						 && model.checkScadenzaA(this, fromPage);

		if (SUCCESS.equals(getTarget()) && dateOk) {
			try {
				// estrazione dell'elenco degli avvisi
				listaAvvisi = avvisiManager.searchAvvisiGenerali(model);

				model.processResult(listaAvvisi.getNumTotaleRecord(), listaAvvisi.getNumTotaleRecordFiltrati());
				
				dataUltimoAggiornamento = getMaxDataUltimoAggiornamento(listaAvvisi.getDati());
				
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				session.put(PortGareSystemConstants.SESSION_ID_SEARCH_AVVISI_COMUNICAZIONI_ATTI, model);
				if (!multipleSearchInRequest) {
					session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, fromSearch);
					session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, fromPage);
					session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER, FROM_PAGE_OWNER);
				}
				
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, fromPage);
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}

		return target;
	}

}
