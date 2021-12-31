package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi;

import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista avvisi.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class AvvisiFinderAction extends EncodedDataAction implements
		SessionAware, ModelDriven<AvvisiSearchBean> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6503418819280258333L;
	
	private static final String FROM_PAGE_OWNER		= "avvisi"; 
	private static final String LIST_ALL_IN_CORSO	= "listAllInCorso";
	private static final String LIST_ALL_SCADUTI 	= "listAllScaduti";
	private static final String SEARCH 				= "search";

	private IAvvisiManager avvisiManager;
	private Map<String, Object> session;
	
	private AvvisiSearchBean model = new AvvisiSearchBean();

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
	 * calcola la MAX data di ultimo aggiormento nell'elenco avvisi  
	 */
	private Date getMaxDataUltimoAggiornamento(List<AvvisoType> lista) {
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
	 * Restituisce la lista di tutti gli avvisi in corso
	 */
	public String listAllInCorso() {
		this.setTarget( this.getListaAvvisi(LIST_ALL_IN_CORSO) );
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di tutti gli avvisi scaduti
	 */
	public String listAllScaduti() {
		this.setTarget( this.getListaAvvisi(LIST_ALL_SCADUTI) );
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

		this.session.put(
				PortGareSystemConstants.SESSION_ID_TYPE_SEARCH_BANDI,
				PortGareSystemConstants.VALUE_TYPE_SEARCH_AVVISI);
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di bandi in base ai filtri impostati
	 */
	public String getListaAvvisi(String fromPage) {
		this.setTarget(SUCCESS);
		
		this.listaAvvisi = null;
		
		String sessionId = null;
		boolean fromSearch = false;		  
		if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO)) {
			sessionId = PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_AVVISI;
		}
		if(fromPage.equalsIgnoreCase(LIST_ALL_SCADUTI)) {
			sessionId = PortGareSystemConstants.SESSION_ID_LIST_ALL_SCADUTI_AVVISI;
		}
		if(fromPage.equalsIgnoreCase(SEARCH)) {
			sessionId = PortGareSystemConstants.SESSION_ID_SEARCH_AVVISI;
			fromSearch = true;
		}

		AvvisiSearchBean finder = (sessionId != null 
				? (AvvisiSearchBean) this.session.get(sessionId) : null);
		
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

		// conversione delle date se valorizzate
		boolean dateOk = true;

		Date dtPubblicazioneDa = null;
		try {
			dtPubblicazioneDa = (Date) LocaleConvertUtils
					.convert(this.model.getDataPubblicazioneDa(), java.sql.Date.class,
							"dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_AVVISO", DA_DATA, this.model.getDataPubblicazioneDa());
			this.model.setDataPubblicazioneDa(null);
			dateOk = false;
		}

		Date dtPubblicazioneA = null;
		try {
			dtPubblicazioneA = (Date) LocaleConvertUtils.convert(
					this.model.getDataPubblicazioneA(), java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_AVVISO", A_DATA, this.model.getDataPubblicazioneA());
			this.model.setDataPubblicazioneA(null);
			dateOk = false;
		}

		Date dtScadenzaDa = null;
		try {
			dtScadenzaDa = (Date) LocaleConvertUtils.convert(
					this.model.getDataScadenzaDa(), java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_SCADENZA_AVVISO", DA_DATA, this.model.getDataScadenzaDa());
			this.model.setDataScadenzaDa(null);
			dateOk = false;
		}

		Date dtScadenzaA = null;
		try {
			dtScadenzaA = (Date) LocaleConvertUtils.convert(this.model.getDataScadenzaA(),
					java.sql.Date.class, "dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, fromPage);
			this.addActionErrorDateInvalid("LABEL_DATA_SCADENZA_AVVISO", A_DATA, this.model.getDataScadenzaA());
			this.model.setDataScadenzaA(null);
			dateOk = false;
		}

		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			try {
				int startIndex = 0;
				if(this.model.getCurrentPage() > 0){
					startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
				}
				
				// estrazione dell'elenco degli avvisi
				if(fromPage.equalsIgnoreCase(LIST_ALL_IN_CORSO)) {
					this.listaAvvisi = this.avvisiManager.getElencoAvvisi(
							this.model.getStazioneAppaltante(), 
							this.model.getOggetto(), 
							this.model.getTipoAvviso(),
							dtPubblicazioneDa, dtPubblicazioneA, 
							dtScadenzaDa, dtScadenzaA,
							this.model.getAltriSoggetti(),
							startIndex, this.model.getiDisplayLength());
				}  
				if(fromPage.equalsIgnoreCase(LIST_ALL_SCADUTI)) {
					this.listaAvvisi = this.avvisiManager.getElencoAvvisiScaduti(
							this.model.getStazioneAppaltante(), 
							this.model.getOggetto(), 
							this.model.getTipoAvviso(),
							dtPubblicazioneDa, dtPubblicazioneA, 
							dtScadenzaDa, dtScadenzaA,
							this.model.getAltriSoggetti(),
							startIndex, this.model.getiDisplayLength());
				}
				if(fromPage.equalsIgnoreCase(SEARCH)) {
					this.listaAvvisi = this.avvisiManager.searchAvvisi(
							this.model.getStazioneAppaltante(), 
							this.model.getOggetto(), 
							this.model.getTipoAvviso(),
							dtPubblicazioneDa, dtPubblicazioneA, 
							dtScadenzaDa, dtScadenzaA,
							this.model.getAltriSoggetti(),
							startIndex, this.model.getiDisplayLength());
				}
							
				this.model.processResult(this.listaAvvisi.getNumTotaleRecord(), 
        				 				 this.listaAvvisi.getNumTotaleRecordFiltrati());
				
				this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(
						this.listaAvvisi.getDati());
				
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
		this.setTarget( this.getListaAvvisi(SEARCH) );
		return this.getTarget();
	}

}
