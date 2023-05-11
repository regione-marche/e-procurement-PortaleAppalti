package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.www.sil.WSGareAppalto.*;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.BandiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Servizio gestore dei bandi.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class BandiManager extends AbstractService implements IBandiManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 7039912668423322712L;

	/** Riferimento al wrapper per il web service Gare Appalto */
	private WSGareAppaltoWrapper wsGareAppalto;

	private static final OrderCriteria DEFAULT_GARE_IN_CORSO = OrderCriteria.DATA_SCADENZA_ASC;
	private static final OrderCriteria DEFAULT_GARE_SCADUTE = OrderCriteria.DATA_SCADENZA_DESC;

	/**
	 * Imposta il web service gare
	 * 
	 * @param proxyWsGare
	 *            web service gare
	 */
	public void setWsGareAppalto(WSGareAppaltoWrapper proxyWsGare) {
		this.wsGareAppalto = proxyWsGare;
	}

	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}
	
	/**
	 * 
	 */	
	public static String getApplicationFilter(String SESSION_ID, String defaultValue) {
		String value = StringUtils.stripToNull((String)ServletActionContext.getRequest().getSession()
			.getAttribute(SESSION_ID));
		if(value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	/**
	 * normalizza il valore del ribassoOfferto
	 *   abs(ribasso-0.0000) > 1/10000 => OK altrimenti 0
	 */
	// 
	private Double normalizzaRibasso(Double v) {
		if(v==null) return null;
		// se il valore e' troppo basso normalizzalo a 0
		// quindi => abs(ribasso-0.0000) > 1/10000 => OK
		double x = 0.0;
		if(v != null && !Double.isNaN(v.doubleValue())) {
			x = v.doubleValue();
			if(x < 0) x = Math.abs(x);
			if(x < 0.00001) x = 0.0;
		}		
		return Double.valueOf(x);
	}

	/**
	 * 
	 */
	public String getVersion() throws ApsException {
		String version = null;
		VersionOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getVersion();
			if (retWS.getErrore() == null) {
				version = (retWS.getVersion() != null ? retWS.getVersion() : "No version found");
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della versione di WSAppalti: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della versione di WSAppalti",
					t);
		}

		return version;
	}
	
	/**
	 * 
	 */
	public String getAppaltiVersion() throws ApsException {
		String version = null;
		VersionOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getAppaltiVersion();
			if (retWS.getErrore() == null) {
				version = (retWS.getVersion() != null ? retWS.getVersion() : "No version found");
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della versione del BackOffice: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della versione del BackOffice",
					t);
		}

		return version;
	}

	private OrderCriteria getOrderCriteriaOrDefault(String searchOrderCriteria, OrderCriteria defaultOrderCriteria){
		return StringUtils.isNotEmpty(searchOrderCriteria) ? OrderCriteria.fromString(searchOrderCriteria) : defaultOrderCriteria;
	}

	/**
	 * ...
	 */
	public SearchResult<GaraType> searchBandi(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());
			
			Integer numAnniPubblicazione = (Integer) wsGareAppalto
					.getAppParamManager().getConfigurationValue(
							AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setNumAnniPubblicazione(numAnniPubblicazione);
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().searchBandi(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca bandi: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca bandi",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> getElencoBandi(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());


			retWS = wsGareAppalto.getProxyWSGare().getElencoBandi(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> getElencoBandiScaduti(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
					.getAppParamManager().getConfigurationValue(
							AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			// gestione customizzazione GARE-SCADUTE|NEGOZIATE|VIA
			boolean visNegoziateScadute = true;
			try {
				ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
					.getBean("CustomConfigManager", ServletActionContext.getRequest());
				visNegoziateScadute = customConfigManager.isVisible("GARE-SCADUTE", "NEGOZIATE");
			} catch (Exception ex) {
			  visNegoziateScadute = true;
			}

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setNumAnniPubblicazione(numAnniPubblicazione);
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setVisualizzaNegoziate(visNegoziateScadute);
			filtri.setEsito(search.convertedEsito());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_SCADUTE));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiScaduti(filtri);
			
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null) {
					List<GaraType> lista = Arrays.asList(retWS.getElencoBandi());
					bandi.setDati(lista);
				} else {
					bandi.setDati( new ArrayList<GaraType>() );
				}
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi scaduti: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi scaduti",
					t);
		}

		return bandi;
	}
	
	@Override
	public String getCigBando(String codiceGara) throws ApsException {
		String risultato = null;
		RisultatoStringaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getCigBando(codiceGara);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei CIG per la procedura con codice "
								+ codiceGara + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei CIG per la procedura con codice "
							+ codiceGara, t);
		}
		return risultato;
	}

	@Override
	public DettaglioGaraType getDettaglioGara(String codiceGara) throws ApsException {
		DettaglioGaraType dettaglio = null;
		try {
			DettaglioGaraOutType retWS = wsGareAppalto.getProxyWSGare().getDettaglioGara(codiceGara);
			if (retWS.getErrore() == null) {
				dettaglio = retWS.getGara();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio bando di gara: "
						+ retWS.getErrore());
			}
			
			//////////////////////////////////////////////
			// Workaraound per il rilascio della memoria  
			retWS = null;

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio bando di gara",
					t);
		}

		return dettaglio;
	}

	@Override
	public DettaglioGaraType getDettaglioGaraFromLotto(String codiceLotto)
	throws ApsException {
		DettaglioGaraType dettaglio = null;
		DettaglioGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDettaglioGaraFromLotto(codiceLotto);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getGara();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio bando di gara a partire da un lotto: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio bando di gara a partire da un lotto",
					t);
		}

		return dettaglio;
	}

	@Override
	public LottoGaraType[] getLottiGara(String codiceGara) throws ApsException {
		LottoGaraType[] dettaglio = null;
		LottiGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getLottiGara(codiceGara);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getLotto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei lotti di gara: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei lotti di gara",
					t);
		}

		return dettaglio;
	}

	@Override
	public LottoGaraType[] getLottiGaraPerDomandePartecipazione(
			String codiceGara, 
			String progOfferta) throws ApsException 
	{
		LottoGaraType[] dettaglio = null;
		LottiGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getLottiGaraPerDomandePartecipazione(codiceGara, progOfferta);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getLotto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei lotti di gara per la domanda di partecipazione: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei lotti di gara per la domanda di partecipazione",
					t);
		}

		return dettaglio;
	}

	@Override
	public LottoGaraType[] getLottiGaraPerRichiesteOfferta(
			String username,
			String codiceGara, 
			String progOfferta) throws ApsException 
	{
		LottoGaraType[] dettaglio = null;
		LottiGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getLottiGaraPerRichiesteOfferta(
					username,
					codiceGara,
					progOfferta);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getLotto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei lotti di gara per la richiesta di offerta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei lotti di gara per la richiesta di offerta",
					t);
		}

		return dettaglio;
	}

	@Override
	public LottoGaraType[] getLottiGaraPlicoUnicoPerRichiesteOfferta(
			String username,
			String codiceGara,
			String progOfferta) throws ApsException 
	{
		LottoGaraType[] dettaglio = null;
		LottiGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getLottiGaraPlicoUnicoPerRichiesteOfferta(
					username,
					codiceGara,
					progOfferta);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getLotto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei lotti di gara a plico unico per la richiesta di offerta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei lotti di gara a plico unico per la richiesta di offerta",
					t);
		}

		return dettaglio;
	}

	@Override
	public LottoGaraType[] getLottiGaraPerRichiesteCheckDocumentazione(String username,
			String codiceGara) throws ApsException {
		LottoGaraType[] dettaglio = null;
		LottiGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getLottiGaraPerRichiesteCheckDocumentazione(username,
					codiceGara);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getLotto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei lotti di gara per la comprova dei requisiti: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei lotti di gara per la comprova dei requisiti",
					t);
		}

		return dettaglio;
	}

	@Override
	public Boolean isGaraConOffertaTecnica(String codiceGara) throws ApsException {
		Boolean esito = null;
		EsitoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().isGaraConOffertaTecnica(codiceGara);
			if (retWS.getErrore() == null) {
				esito = retWS.getEsito();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la verifica gara con offerta tecnica (gara="
						+ codiceGara + "): " + retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la verifica gara con offerta tecnica (gara="
					+ codiceGara + ")", t);
		}

		return esito;
	}

	@Override
	public List<BandoIscrizioneType> searchBandiIscrizione(String username,
														   Integer stato, boolean isAttivo) throws ApsException {
		List<BandoIscrizioneType> bandi = null;
		ElencoBandiIscrizioneOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare().searchBandiIscrizione(
					username, stato, stazioneAppaltante, isAttivo);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandiIscrizione() != null)
					bandi = Arrays.asList(retWS.getElencoBandiIscrizione());
				else
					bandi = new ArrayList<BandoIscrizioneType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la ricerca bandi d'iscrizione per l'impresa "
						+ username + ": " + retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei bandi d'iscrizione per l'impresa "
					+ username, t);
		}

		return bandi;
	}

	@Override
	public List<BandoIscrizioneType> getElencoBandiIscrizione(boolean isAttivo) throws ApsException {
		List<BandoIscrizioneType> bandi = null;
		ElencoBandiIscrizioneOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiIscrizione(stazioneAppaltante, isAttivo);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandiIscrizione() != null)
					bandi = Arrays.asList(retWS.getElencoBandiIscrizione());
				else
					bandi = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi d'iscrizione: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi d'iscrizione",
					t);
		}

		return bandi;
	}

	@Override
	public DettaglioBandoIscrizioneType getDettaglioBandoIscrizione(
			String codice) throws ApsException {
		DettaglioBandoIscrizioneType dettaglio = null;
		DettaglioBandoIscrizioneOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDettaglioBandoIscrizione(codice, null);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getBandoIscrizione();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio bando d'iscrizione: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio bando d'iscrizione",
					t);
		}

		return dettaglio;
	}

	@Override
	public CategoriaBandoIscrizioneType[] getElencoCategorieBandoIscrizione(String codice, String filtroCategorie)
	throws ApsException {
		CategoriaBandoIscrizioneType[] categorie = null;
		CategorieBandoIscrizioneOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getCategorieBandoIscrizione(codice, filtroCategorie);
			if (retWS.getErrore() == null) {
				categorie = retWS.getCategoria();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco categorie del bando d'iscrizione: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco categorie del bando d'iscrizione",
					t);
		}

		return categorie;

	}

	@Override
	public StatisticheComunicazioniPersonaliType getStatisticheComunicazioniPersonali(
			String username, 
			String codice,
			String codice2,
			String entita) throws ApsException {
		StatisticheComunicazioniPersonaliType dettaglio = null;
		GetStatisticheComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getStatisticheComunicazioniPersonali(
						username, 
						codice,
						codice2,
						entita,
						stazioneAppaltante);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getStatistiche();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle statistiche comunicazioni per il richiedente "
						+ username
						+ " per la procedura con codice " + codice
						+ ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle statistiche comunicazioni per il richiedente "
					+ username
					+ " per la procedura con codice " + codice, t);
		}

		return dettaglio;
	}
	
	@Override
	public StatisticheComunicazioniPersonaliType getStatisticheComunicazioniPersonaliGaraLotti(
			String username, 
			String codiceGara,
			String entita) throws ApsException 
	{
		StatisticheComunicazioniPersonaliType dettaglio = null;
		GetStatisticheComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getStatisticheComunicazioniPersonaliGaraLotti(
						username, 
						codiceGara,
						entita,
						stazioneAppaltante);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getStatistiche();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle statistiche comunicazioni per il richiedente "
						+ username
						+ " per la procedura a lotti con codice " + codiceGara
						+ ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle statistiche comunicazioni per il richiedente "
					+ username
					+ " per la procedura a lotti con codice " + codiceGara, t);
		}

		return dettaglio;
	}

	@Override
	public SearchResult<ComunicazioneType> getComunicazioniPersonaliRicevute(
			String username, 
			String codice, 
			String codice2, 
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException 
	{
		SearchResult<ComunicazioneType> risultato = new SearchResult<ComunicazioneType>();
		List<ComunicazioneType> comunicazioni = null;
		ComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);
			
			retWS = wsGareAppalto.getProxyWSGare()
				.getComunicazioniPersonaliRicevute(
						username,
						codice,
						codice2,
						soccorsoIstruttorio,
						entita,
						indicePrimoRecord, maxNumRecord, 
						stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getComunicazione() != null) {
					comunicazioni = Arrays.asList(retWS.getComunicazione());
				} else {
					comunicazioni = new ArrayList<ComunicazioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle comunicazioni personali ricevute per il richiedente "
						+ username
						+ " per la procedura con codice "
						+ codice
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(comunicazioni);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle comunicazioni personali ricevute per il richiedente "
					+ username
					+ " per la procedura con codice " + codice, t);
		}

		return risultato;
	}

	@Override
	public SearchResult<ComunicazioneType> getComunicazioniPersonaliRicevuteGaraLotti(
			String username, 
			String codiceGara,
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException 
	{
		SearchResult<ComunicazioneType> risultato = new SearchResult<ComunicazioneType>();
		List<ComunicazioneType> comunicazioni = null;
		ComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getComunicazioniPersonaliRicevuteGaraLotti(
						username,
						codiceGara,
						soccorsoIstruttorio,
						entita,
						indicePrimoRecord, maxNumRecord,
						stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getComunicazione() != null) {
					comunicazioni = Arrays.asList(retWS.getComunicazione());
				} else {
					comunicazioni = new ArrayList<ComunicazioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle comunicazioni personali ricevute per il richiedente "
						+ username
						+ " per la procedura a lotti con codice "
						+ codiceGara
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(comunicazioni);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle comunicazioni personali ricevute per il richiedente "
					+ username
					+ " per la procedura a lotti con codice " + codiceGara, t);
		}

		return risultato;
	}

	@Override
	public SearchResult<ComunicazioneType> getComunicazioniPersonaliArchiviate(
			String username, String codice, String codice2,
			boolean soccorsoIstruttorio, String entita, int indicePrimoRecord,
			int maxNumRecord) throws ApsException {
		SearchResult<ComunicazioneType> risultato = new SearchResult<ComunicazioneType>();
		List<ComunicazioneType> comunicazioni = null;
		ComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getComunicazioniPersonaliArchiviate(
						username,
						codice,
						codice2,
						soccorsoIstruttorio,
						entita,
						indicePrimoRecord, maxNumRecord,
						stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getComunicazione() != null) {
					comunicazioni = Arrays.asList(retWS.getComunicazione());
				} else {
					comunicazioni = new ArrayList<ComunicazioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle comunicazioni personali archiviate per il richiedente "
						+ username
						+ " per la procedura con codice "
						+ codice
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(comunicazioni);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle comunicazioni personali archiviate per il richiedente "
					+ username
					+ " per la procedura con codice " + codice, t);
		}

		return risultato;
	}
	
	@Override
	public SearchResult<ComunicazioneType> getComunicazioniPersonaliArchiviateGaraLotti(
			String username, 
			String codiceGara, 
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException 
	{
		SearchResult<ComunicazioneType> risultato = new SearchResult<ComunicazioneType>();
		List<ComunicazioneType> comunicazioni = null;
		ComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getComunicazioniPersonaliArchiviateGaraLotti(
						username,
						codiceGara,
						soccorsoIstruttorio,
						entita,
						indicePrimoRecord, maxNumRecord,
						stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getComunicazione() != null) {
					comunicazioni = Arrays.asList(retWS.getComunicazione());
				} else {
					comunicazioni = new ArrayList<ComunicazioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle comunicazioni personali archiviate per il richiedente "
						+ username
						+ " per la procedura a lotti con codice "
						+ codiceGara
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(comunicazioni);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle comunicazioni personali archiviate per il richiedente "
					+ username
					+ " per la procedura a lotti con codice " + codiceGara, t);
		}

		return risultato;
	}

	@Override
	public SearchResult<ComunicazioneType> getComunicazioniPersonaliInviate(
			String username, 
			String codice,
			String codice2,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException 
	{
		SearchResult<ComunicazioneType> risultato = new SearchResult<ComunicazioneType>();
		List<ComunicazioneType> comunicazioni = null;
		ComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getComunicazioniPersonaliInviate(
						username, 
						codice,
						codice2,
						entita,
						indicePrimoRecord, maxNumRecord, 
						stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getComunicazione() != null) {
					comunicazioni = Arrays.asList(retWS.getComunicazione());
				} else {
					comunicazioni = new ArrayList<ComunicazioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle comunicazioni personali inviate per il richiedente "
						+ username
						+ " per la procedura con codice "
						+ codice
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(comunicazioni);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle comunicazioni personali inviate per il richiedente "
					+ username
					+ " per la procedura con codice " + codice, t);
		}

		return risultato;
	}

	@Override
	public ComunicazioneType getComunicazioneRicevuta(
			String tokenRichiedente,
			long idComunicazione,
			long idDestinatario,
			String idprg) throws ApsException 
	{
		ComunicazioneType dettaglio = null;
		ComunicazionePersonaleOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
				.getComunicazionePersonaleRicevuta(
						tokenRichiedente,
						idComunicazione,
						idDestinatario,
						idprg);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getComunicazione();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della comunicazione ricevuta con id "
						+ idComunicazione + " e progressivo " + idDestinatario +
						" per il richiedente " + tokenRichiedente + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della comunicazione ricevuta con id "
					+ idComunicazione + " e progressivo " + idDestinatario +
					" per il richiedente " + tokenRichiedente, t);
		}

		return dettaglio;
	}

	@Override
	public ComunicazioneType getComunicazioneInviata(String username,
			long idComunicazione, String idprg) throws ApsException {
		ComunicazioneType dettaglio = null;
		ComunicazionePersonaleOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
			.getComunicazionePersonaleInviata(username,
					idComunicazione, idprg);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getComunicazione();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della comunicazione inviata con id "
						+ idComunicazione + " per il richiedente "
						+ username + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della comunicazione inviata con id "
					+ idComunicazione + " per il richiedente "
					+ username, t);
		}

		return dettaglio;
	}

	@Override
	public DatiImpresaDocument getDatiImpresa(String username, Date dataPartenzaCessati)
	throws ApsException {

		DatiImpresaDocument dettaglio = null;
		GetDatiImpresaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDatiImpresa(
					username, 
					(dataPartenzaCessati != null ? DateUtils.toCalendar(dataPartenzaCessati) : null));

			if (retWS.getErrore() == null) {
				String xml = retWS.getDatiImpresa();
				dettaglio = DatiImpresaDocument.Factory.parse(xml);
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore nel servizio per la lettura dei dati dell'impresa: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei dati dell'impresa",
					t);
		} catch (XmlException t) {
			throw new ApsException(
					"Errore inaspettato durante l'interpretazione dei dati dell'impresa",
					t);
		}

		return dettaglio;
	}

	@Override
	public List<DocumentazioneRichiestaType> getDocumentiRichiestiBandoGara(
			String codiceGara, String codiceLotto, String tipoImpresa,
			boolean rti, String busta) throws ApsException {
		List<DocumentazioneRichiestaType> dettaglio = null;
		GetDocumentiRichiestiBandoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
			.getDocumentiRichiestiBandoGara(codiceGara, codiceLotto,
					tipoImpresa, rti, busta);
			if (retWS.getErrore() == null) {
				if (retWS.getDocumento() != null)
					dettaglio = Arrays.asList(retWS.getDocumento());
				else
					dettaglio = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti richiesti per l'invio di una busta telematica (gara="
						+ codiceGara
						+ ", lotto="
						+ codiceLotto
						+ ", tipoimpresa="
						+ tipoImpresa
						+ ", rti="
						+ rti
						+ ", busta="
						+ busta
						+ "): "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei documenti richiesti per l'invio di una busta telematica (gara="
					+ codiceGara
					+ ", lotto="
					+ codiceLotto
					+ ", tipoimpresa="
					+ tipoImpresa
					+ ", rti="
					+ rti
					+ ", busta=" + busta + ")", t);
		}

		return dettaglio;
	}

	@Override
	public DocumentazioneRichiestaGaraPlicoUnico getDocumentiRichiestiBandoGaraPlicoUnico(
			String codiceGara, String tipoImpresa, boolean rti)
	throws ApsException {
		DocumentazioneRichiestaGaraPlicoUnico documentazioneComplessiva = null;
		GetDocumentiRichiestiBandoGaraPlicoUnicoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
			.getDocumentiRichiestiBandoGaraPlicoUnico(codiceGara,
					tipoImpresa, rti);
			if (retWS.getErrore() == null) {
				documentazioneComplessiva = new DocumentazioneRichiestaGaraPlicoUnico();
				documentazioneComplessiva.setDocumentiBustaAmministrativa(retWS.getDocumentoBustaAmministrativa());
				documentazioneComplessiva.setLotti(retWS.getLotti());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti richiesti per l'invio di una busta telematica a plico unico ed offerte distinte (gara="
						+ codiceGara
						+ ", tipoimpresa="
						+ tipoImpresa
						+ ", rti=" + rti + "): " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei documenti richiesti per l'invio di una busta telematica a plico unico ed offerte distinte (gara="
					+ codiceGara
					+ ", tipoimpresa="
					+ tipoImpresa
					+ ", rti=" + rti + ")", t);
		}

		return documentazioneComplessiva;
	}

	@Override
	public List<DocumentazioneRichiestaType> getDocumentiRichiestiBandoIscrizione(
			String codice, String tipoImpresa, boolean rti) throws ApsException {
		List<DocumentazioneRichiestaType> dettaglio = null;
		GetDocumentiRichiestiBandoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
			.getDocumentiRichiestiBandoIscrizione(codice, tipoImpresa,
					rti);
			if (retWS.getErrore() == null) {
				if (retWS.getDocumento() != null)
					dettaglio = Arrays.asList(retWS.getDocumento());
				else
					dettaglio = new ArrayList<DocumentazioneRichiestaType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti richiesti per l'iscrizione all'elenco operatori economici (bando="
						+ codice
						+ ", tipoimpresa="
						+ tipoImpresa
						+ ", rti=" + rti + "): " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei documenti richiesti per l'iscrizione all'elenco operatori economici (bando="
					+ codice
					+ ", tipoimpresa="
					+ tipoImpresa
					+ ", rti=" + rti + ")", t);
		}

		return dettaglio;
	}

	@Override
	public List<DocumentazioneRichiestaType> getDocumentiRichiestiRinnovoIscrizione(
			String codice, String tipoImpresa, boolean rti) throws ApsException {
		List<DocumentazioneRichiestaType> dettaglio = null;
		GetDocumentiRichiestiBandoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
			.getDocumentiRichiestiRinnovoIscrizione(codice,
					tipoImpresa, rti);
			if (retWS.getErrore() == null) {
				if (retWS.getDocumento() != null)
					dettaglio = Arrays.asList(retWS.getDocumento());
				else
					dettaglio = new ArrayList<DocumentazioneRichiestaType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti richiesti per il rinnovo iscrizione all'elenco operatori economici (bando="
						+ codice
						+ ", tipoimpresa="
						+ tipoImpresa
						+ ", rti=" + rti + "): " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei documenti richiesti per il rinnovo iscrizione all'elenco operatori economici (bando="
					+ codice
					+ ", tipoimpresa="
					+ tipoImpresa
					+ ", rti=" + rti + ")", t);
		}

		return dettaglio;
	}

	@Override
    public List<DocumentazioneRichiestaType> getDocumentiRichiestiComunicazione(
    	    String idApplicativo, long idComunicazione) throws ApsException 
    {
		List<DocumentazioneRichiestaType> dettaglio = null;
		GetDocumentiRichiestiBandoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
				.getDocumentiRichiestiComunicazione(idApplicativo, idComunicazione);
			if (retWS.getErrore() == null) {
				if (retWS.getDocumento() != null)
					dettaglio = Arrays.asList(retWS.getDocumento());
				else
					dettaglio = new ArrayList<DocumentazioneRichiestaType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti richiesti per il rinnovo iscrizione all'elenco operatori economici ("
						+ idApplicativo + ", " + idComunicazione + "): " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dei documenti richiesti per il rinnovo iscrizione all'elenco operatori economici ("
					+ idApplicativo + ", " + idComunicazione + ")", t);
		}

		return dettaglio;
	}
    
	@Override
	public Integer getStatoIscrizioneABandoIscrizione(String username,
			String codice) throws ApsException {
		Integer stato = null;
		StatoIscrizioneABandoIscrizioneOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getStatoIscrizioneABandoIscrizione(username,	codice);
			if (retWS.getErrore() == null)
				stato = retWS.getStato();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante l'estrazione dello stato dell'eventuale iscrizione al bando per l'impresa: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione dello stato dell'eventuale iscrizione al bando per l'impresa",
					t);
		}

		return stato;
	}

	@Override
	public AbilitazioniGaraType getAbilitazioniGara(String username,
			String codice) throws ApsException {
		AbilitazioniGaraType dettaglio = null;
		GetAbilitazioniGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getAbilitazioniGara(username, codice);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getAbilitazioni();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle abilitazioni dell'impresa al bando di gara: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante lettura delle abilitazioni dell'impresa al bando di gara",
					t);
		}

		return dettaglio;
	}
	
	@Override
	public SearchResult<GaraType> getElencoBandiPerRichiesteOfferta(BandiSearchBean search) throws ApsException
	{
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiPerRichiesteOfferta(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi abilitati all'inserimento di richieste di invio offerta: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi abilitati all'inserimento di richieste di invio offerta",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> getElencoBandiPerRichiesteCheckDocumentazione(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiPerRichiesteCheckDocumentazione(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi abilitati all'inserimento di richieste di invio documentazione: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi abilitati all'inserimento di richieste di invio documentazione",
					t);
		}

		return bandi;
	}
	
	@Override
	public SearchResult<GaraType> getElencoBandiPerAsteInCorso(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiPerAsteInCorso(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi abilitati ad aste elettroniche: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi abilitati ad aste elettroniche",
					t);
		}

		return bandi;
	}

	@Override
	public int countBandiPerAsteInCorso(BandiSearchBean search) throws ApsException {
		int numBandi = 0;
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setCodice(search.getCodice());

			retWS = wsGareAppalto.getProxyWSGare().countBandiPerAsteInCorso(filtri);
			if (retWS.getErrore() == null) {
				numBandi = (retWS.getNumBandi() != null ? retWS.getNumBandi() : 0);
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante il conteggio dei bandi abilitati ad aste elettroniche: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante il conteggio dei bandi abilitati ad aste elettroniche",
					t);
		}
		
		return numBandi;
	}

	@Override
	public TipoPartecipazioneType getTipoPartecipazioneImpresa(
			String username, String codice, String progOfferta) 
		throws ApsException {
		TipoPartecipazioneType dettaglio = null;
		TipoPartecipazioneImpresaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getTipoPartecipazioneImpresa(username, codice, progOfferta);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getTipoPartecipazione();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del tipo di partecipazione dell'impresa al bando di gara: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante lettura del tipo di partecipazione  dell'impresa al bando di gara",
					t);
		}

		return dettaglio;
	}

	@Override
	public List<CategoriaImpresaType> getCategorieImpresaPerIscrizione(
			String username, String codice) throws ApsException {
		List<CategoriaImpresaType> categorie = null;
		CategorieImpresaPerIscrizioneOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getCategorieImpresaPerIscrizione(username,
					codice);
			if (retWS.getErrore() == null) {
				if (retWS.getCategorie() != null)
					categorie = Arrays.asList(retWS.getCategorie());
				else
					categorie = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco categorie iscritte per l'impresa all'albo: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco categorie iscritte per l'impresa all'albo",
					t);
		}

		return categorie;
	}

	@Override
    public ImpresaIscrizioneType getImpresaIscrizione(
    	    String tokenRichiedente, String codice) throws ApsException
    {
		ImpresaIscrizioneType impresa = null;
		try {
			ImpresaIscrizioneOutType retWS = wsGareAppalto.getProxyWSGare().getImpresaIscrizione(
					tokenRichiedente, codice);
			if (retWS.getErrore() == null) {
				impresa = retWS.getImpresa();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei dati per l'iscrizione dell'impresa all'albo: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei dati per l'iscrizione dell'impresa all'albo",
					t);
		}

		return impresa;
	}

	@Override
	public LinkedHashMap<String, String> getElencoStazioniAppaltantiPerIscrizione(
			String codice) throws ApsException {
		LinkedHashMap<String, String> lista = null;
		try {
			lista = InterceptorEncodedData.parseXml(wsGareAppalto.getProxyWSGare()
					.getElencoStazioniAppaltantiPerIscrizione(codice));
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco delle stazioni appaltanti a cui e' permessa l'iscrizione all'albo",
					t);
		} catch (XmlException e) {
			throw new ApsException(
					"Errore inaspettato durante l'interpretazione della richiesta elenco delle stazioni appaltanti a cui e' permessa l'iscrizione all'albo",
					e);
		}
		return lista;
	}

	@Override
	public Boolean isImpresaRegistrata(String codiceFiscale, String partitaIva, boolean isGruppoIva)
	throws ApsException {
		Boolean esito = null;
		EsitoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().isImpresaRegistrata(codiceFiscale, partitaIva, isGruppoIva);
			if (retWS.getErrore() == null) {
				esito = retWS.getEsito();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la verifica dell'esistenza di un'impresa cercandola per codice fiscale e/o partita iva: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la verifica dell'esistenza di un'impresa cercandola per codice fiscale e/o partita iva",
					t);
		}

		return esito;
	}

    @Override
	public String getImpresaRegistrata(String codiceFiscale, String partitaIva, String email, String pec)
		throws ApsException 
	{
		String username = null;
		RisultatoStringaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getImpresaRegistrata(codiceFiscale, partitaIva, email, pec);
			if (retWS.getErrore() == null) {
				username = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante il recupero dell'esistenza di un'impresa cercandola per codice fiscale e/o partita iva: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante il recupero dell'esistenza di un'impresa cercandola per codice fiscale e/o partita iva",
					t);
		}
		return username;
	}
    
	@Override
	public SearchResult<GaraType> searchBandiPerProcInAggiudicazione(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_SCADUTE));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().searchBandiPerProcInAggiudicazione(filtri);
			
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca bandi per procedure in aggiudicazione: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca bandi per procedure in aggiudicazione",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> searchBandiConEsito(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());
			
			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
						AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setNumAnniPubblicazione(numAnniPubblicazione);
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setEsito(search.convertedEsito());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_SCADUTE));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().searchBandiConEsito(filtri);
			
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca generale bandi per procedure : "
						+ retWS.getErrore());
			}
	
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca generale bandi per procedure",
					t);
		}
	
		return bandi;
	}

	@Override
	public Boolean isImpresaAbilitataRinnovo(String codice, String username)
			throws ApsException {

		Boolean risultato = null;
		try {
			EsitoOutType retWS = wsGareAppalto.getProxyWSGare()
					.isImpresaAbilitataRinnovo(codice, username);
			if (retWS.getErrore() == null) {
				risultato = retWS.getEsito();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la verifica se l'impresa " + username
						+ " risulta abilitata al rinnovo per l'elenco "
						+ codice + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la verifica se l'impresa "
					+ username
					+ " risulta abilitata al rinnovo per l'elenco "
					+ codice, t);
		}
		return risultato;
	}

	@Override
	public List<VoceDettaglioOffertaType> getVociDettaglioOfferta(String codice)
			throws ApsException {
		List<VoceDettaglioOffertaType> voci = null;
		GetVociDettaglioOffertaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getVociDettaglioOfferta(
					codice);
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					voci = Arrays.asList(retWS.getElenco());
				else
					voci = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle voci di dettaglio offerta prezzi unitari per la gara "
						+ codice
						+ " con offerta telematica: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle voci di dettaglio offerta prezzi unitari per la gara "
					+ codice + " con offerta telematica", t);
		}

		return voci;
	}

	@Override
	public List<VoceDettaglioOffertaType> getVociDettaglioOffertaNoRibasso(String codice)
			throws ApsException {
		List<VoceDettaglioOffertaType> voci = null;
		GetVociDettaglioOffertaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getVociDettaglioOffertaNoRibasso(
					codice);
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					voci = Arrays.asList(retWS.getElenco());
				else
					voci = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle voci di dettaglio offerta prezzi unitari non soggette a ribasso per la gara "
						+ codice
						+ " con offerta telematica: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle voci di dettaglio offerta prezzi unitari non soggette a ribasso per la gara "
					+ codice + " con offerta telematica", t);
		}

		return voci;
	}

	@Override
	public List<AttributoAggiuntivoOffertaType> getAttributiAggiuntiviOfferta(
			String codice) throws ApsException {
		List<AttributoAggiuntivoOffertaType> attributi = null;
		GetAttributiAggiuntiviOffertaOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getAttributiAggiuntiviOfferta(codice);
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					attributi = Arrays.asList(retWS.getElenco());
				else
					attributi = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura degli attributi aggiuntivi per il dettaglio offerta prezzi unitari per la gara "
						+ codice
						+ " con offerta telematica: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura degli attributi aggiuntivi per il dettaglio offerta prezzi unitari per la gara "
					+ codice + " con offerta telematica", t);
		}

		return attributi;
	}

	@Override
	public DettaglioStazioneAppaltanteType getDettaglioStazioneAppaltante(
			String codice) throws ApsException {
		DettaglioStazioneAppaltanteType dettaglio = null;
		GetDettaglioStazioneAppaltanteOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDettaglioStazioneAppaltante(codice);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getStazioneAppaltante();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della stazione appaltante con codice "
						+ codice + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della stazione appaltante con codice "
					+ codice, t);
		}

		return dettaglio;
	}

	@Override
	public List<DettaglioStazioneAppaltanteType> getStazioniAppaltanti() throws ApsException {
		List<DettaglioStazioneAppaltanteType> elenco = null;
		GetStazioniAppaltantiOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getStazioniAppaltanti();
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					elenco = Arrays.asList(retWS.getElenco());
				else
					elenco = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle stazioni appaltanti "
						+ ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle stazioni appaltanti "
					, t);
		}

		return elenco;
	}

	@Override
	public Long getNumeroDecimaliRibasso() throws ApsException {
		Long risultato = null;
		try {
			RisultatoNumericoOutType retWS = wsGareAppalto.getProxyWSGare().getNumDecimaliRibasso();
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del numero massimo di decimali per esprimere un ribasso: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del numero massimo di decimali per esprimere un ribasso",
					t);
		}
		return risultato;
	}

	@Override
	public List<MandanteRTIType> getMandantiRTI(
			String codiceProcedura,
			String username,
			String progOfferta) throws ApsException 
	{
		List<MandanteRTIType> mandanti = null;
		GetMandantiRTIOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getMandantiRTI(codiceProcedura, username, progOfferta);
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					mandanti = Arrays.asList(retWS.getElenco());
				else
					mandanti = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle mandanti RTI per la procedura "
						+ codiceProcedura
						+ "  con mandataria l'impresa con username "
						+ username + ": " + retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle mandanti RTI per la procedura "
					+ codiceProcedura
					+ "  con mandataria l'impresa con username "
					+ username, t);
		}

		return mandanti;
	}

	@Override
	public FascicoloProtocolloType getFascicoloProtocollo(String codiceProcedura)
			throws ApsException {
		FascicoloProtocolloType dettaglio = null;
		GetFascicoloProtocolloOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getFascicoloProtocollo(codiceProcedura);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getFascicolo();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del fascicolo per la procedura con codice "
						+ codiceProcedura + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del fascicolo per la procedura con codice "
					+ codiceProcedura, t);
		}

		return dettaglio;
	}

	@Override
	public Long getGenere(String codice) throws ApsException {
		Long risultato = null;
		try {
			RisultatoNumericoOutType retWS = wsGareAppalto.getProxyWSGare()
					.getGenere(codice);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del genere per per la procedura con codice "
						+ codice + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del genere per per la procedura con codice "
					+ codice, t);
		}
		return risultato;
	}

	@Override
	public byte[] getChiavePubblica(String codice, String tipoBusta)
			throws ApsException {
		ApsSystemUtils.getLogger().debug(
				"Classe: " + this.getClass().getName() + " - Metodo: getChiavePubblica \n" +  
				"Messaggio: inizio metodo " + 
				"\ncodice=" + codice + ", tipoBusta=" + tipoBusta);
		byte[] risultato = null;
		try {
			RisultatoStringaOutType retWS = wsGareAppalto.getProxyWSGare().getChiavePubblica(codice, tipoBusta);
			if (retWS.getErrore() == null) {
				String bytearrayCodificato = retWS.getValore();
				if (bytearrayCodificato != null)
					risultato = bytearrayCodificato.getBytes();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della chiave pubblica per per la procedura con codice " + codice 
						+ " e busta " + tipoBusta
						+ ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del genere per per la procedura con codice "
					+ codice + " e busta " + tipoBusta, t);
		}
		ApsSystemUtils.getLogger().debug(
				"Classe: " + this.getClass().getName() + " - Metodo: getChiavePubblica \n" +
				"Messaggio: risultato=[" + (risultato != null ? new String(risultato) : "non trovata" ) + "]"); 
		ApsSystemUtils.getLogger().debug(
				"Classe: " + this.getClass().getName() + " - Metodo: getChiavePubblica \n" +  
				"Messaggio: fine metodo");
		return risultato;
	}

	@Override
	public boolean isConsorziateEsecutriciPresenti(String username, String codiceGara)
			throws ApsException {
		Boolean esito = null;
		EsitoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().isConsorziateEsecutriciPresenti(username, codiceGara);
			if (retWS.getErrore() == null) {
				esito = retWS.getEsito();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la verifica dell'esistenza delle consorziate esecutrici nella gara " + codiceGara + " per l'impresa attiva nel portale con login "
						+ username + " : " + retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la verifica dell'esistenza delle consorziate esecutrici nella gara " + codiceGara + " per l'impresa attiva nel portale con login "
					+ username, t);
		}

		return esito;
	}

	@Override
	public List<InvioType> getElencoInvii(String username, String codice,
			String[] tipiComunicazione) throws ApsException {
		List<InvioType> attributi = null;
		ElencoInviiOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getElencoInvii(username,
					codice, tipiComunicazione);
			if (retWS.getErrore() == null) {
				if (retWS.getInvio() != null)
					attributi = Arrays.asList(retWS.getInvio());
				else
					attributi = new ArrayList<>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura degli invii effettuati per l'utente "
						+ username + " sulla procedura " + codice
						+ " per le comunicazioni con tipo " + "["
						+ StringUtils.join(tipiComunicazione, ',')
						+ "]:" + retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura degli invii effettuati per l'utente "
					+ username + " sulla procedura " + codice
					+ " per le comunicazioni con tipo " + "["
					+ StringUtils.join(tipiComunicazione, ',')
					+ "]", t);
		}

		return attributi;
	}

	@Override
	public DocumentoAllegatoType[] getAttiDocumentiBandoGara(String codiceGara) throws ApsException {
		DocumentoAllegatoType[] listaAttiDocumentiBandoGara = null;
		GetAttiDocumentiBandoGaraOutType retWS = null;

		try {
			retWS = wsGareAppalto.getProxyWSGare().getAttiDocumentiBandoGara(codiceGara);
			if (retWS.getErrore() == null) {
				if (retWS.getAttiDocumenti()!=null)
					listaAttiDocumentiBandoGara = retWS.getAttiDocumenti();
			} else{
				throw new ApsException(
						"Errore inaspettato durante la lettura degli atti e documenti  sulla procedura " + codiceGara);
			}
		} catch (RemoteException e) {
			throw new ApsException(	"Errore inaspettato durante la lettura degli atti e documenti  sulla procedura " + codiceGara, e);
		}

		return listaAttiDocumentiBandoGara;
	}
	
	@Override
	public DocumentoAllegatoType[] getDocumentiInvitoAsta(String codiceGara, String codiceLotto) throws ApsException {
		DocumentoAllegatoType[] listaDocumentiAsta = null;
		GetDocumentiInvitoAstaLottoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDocumentiInvitoAstaLotto(codiceGara, codiceLotto);
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					listaDocumentiAsta = retWS.getElenco();
			} else{
				throw new ApsException(
						"Errore inaspettato durante la lettura dei documenti sull'asta " + codiceGara);
			}
		} catch (RemoteException e) {
			throw new ApsException(	"Errore inaspettato durante la lettura dei documenti sull'asta " + codiceGara, e);
		}

		return listaDocumentiAsta;
	}

	@Override
	public Boolean isInvitataAsta(String codice, String username) throws ApsException {
		Boolean risultato = false;
		try {
			risultato = false;
//			if(gara != null) {
//				if(gara.getLotto() != null && gara.getLotto().length > 0) {
//					// gara a lotti...					 
//					for(int i = 0; i < gara.getLotto().length; i++) {
//						if( wsGareAppalto.getProxyWSGare().isInvitataAsta(
//								gara.getLotto(i).getCodiceLotto(), 
//								username) ) {
//							risultato = true; 
//						}
//					}
//				} else {
//					// gara senza lotti...
//					risultato = wsGareAppalto.getProxyWSGare().isInvitataAsta(
//							gara.getDatiGeneraliGara().getCodice(), 
//							username);
//				}
//			} else {
//				Boolean retWS = (Boolean) wsGareAppalto.getProxyWSGare()
//					.isInvitataAsta(codice, username);
//				
//				risultato = (retWS != null ? retWS : null);
//			}			
			Boolean retWS = wsGareAppalto.getProxyWSGare().isInvitataAsta(codice, username);
			risultato = (retWS);
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato la lettura dell'invito/ammissione all'asta",
					t);
		}
		return risultato;
	}

	@Override
	public Long checkAggiornamentoIscrizionePosticipata(
    	    String tokenRichiedente, String codiceGara) throws ApsException {
		
		Long risultato = null;
		try {
			CheckAggiornamentoIscrizionePosticipataOutType retWS = wsGareAppalto.getProxyWSGare()
				.checkAggiornamentoIscrizionePosticipata(
						tokenRichiedente, codiceGara);
			if (retWS.getErrore() == null) {
				risultato = retWS.getRisultato();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la ricerca della comunicazione di aggiornamento iscrizione posticipata: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la ricerca della comunicazione di aggiornamento iscrizione posticipata",
							t);
		}
		return risultato;
	}
	
	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraSoccorsiElencoOperatori(
			String codiceGara, String codiceLotto, String faseGara, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraSoccorsiElencoOperatori(
						codiceGara,
						codiceLotto,
						faseGara,
						tokenRichiedente);

			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null){
					elenco = Arrays.asList(retWS.getElenco());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la documentazione dei soccorsi istruttori della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la documentazione dei soccorsi istruttori della gara",
							t);
		}
		return elenco;
	}
	
	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraDocAmmElencoOperatori(
			String codiceGara, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraDocAmmElencoOperatori(
						codiceGara, tokenRichiedente);

			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					elenco = Arrays.asList(retWS.getElenco());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la documentazione amministrativa della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la documentazione amministrativa della gara",
							t);
		}
		return elenco;
	}
	
	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraValTecElencoOperatori(
			String codice, String codiceLotto, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraValTecElencoOperatori(
						codice, codiceLotto, tokenRichiedente);

			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null){
					elenco = Arrays.asList(retWS.getElenco());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la valutazione tecnica della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la valutazione tecnica della gara",
							t);
		}
		return elenco;
	}

	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraOffEcoElencoOperatori(
			String codice, String codiceLotto, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraOffEcoElencoOperatori(
						codice, codiceLotto, tokenRichiedente);
			
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null) {
					elenco = Arrays.asList(retWS.getElenco());
					
					// normalizza il valore di alcuni campi Double (ribassoOfferto)
					for(int i = 0; i < elenco.size(); i++) {
						elenco.get(i).setRibassoOfferto( normalizzaRibasso(elenco.get(i).getRibassoOfferto()) );
					}
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per l'offerta economica della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per l'offerta economica della gara",
							t);
		}
		return elenco;
	}
	
	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraValTecElencoOperatoriLotto(
			String codice, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraValTecElencoOperatoriLotto(
						codice, tokenRichiedente);

			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null){
					elenco = Arrays.asList(retWS.getElenco());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la valutazione tecnica della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la valutazione tecnica della gara",
							t);
		}
		return elenco;
	}

	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraOffEcoElencoOperatoriLotto(
			String codice, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraOffEcoElencoOperatoriLotto(
						codice, tokenRichiedente);

			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null){
					elenco = Arrays.asList(retWS.getElenco());
					
					// normalizza il valore di alcuni campi Double (ribassoOfferto)
					for(int i = 0; i < elenco.size(); i++) {
						elenco.get(i).setRibassoOfferto( normalizzaRibasso(elenco.get(i).getRibassoOfferto()) );
					}
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per l'offerta economica della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per l'offerta economica della gara",
							t);
		}
		return elenco;
	}
	
	@Override
	public List<EspletGaraOperatoreType> getEspletamentoGaraGraduatoriaElencoOperatori(
			String codiceGara, String tokenRichiedente) throws ApsException {
		List<EspletGaraOperatoreType> elenco = null;
		try {
			GetEspletamentoGaraElencoOperatoriOutType retWS = wsGareAppalto.getProxyWSGare()
				.getEspletamentoGaraGraduatoriaElencoOperatori(
						codiceGara, tokenRichiedente);

			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null){
					elenco = Arrays.asList(retWS.getElenco());
					// normalizza il valore di alcuni campi Double (ribassoOfferto)
					for(int i = 0; i < elenco.size(); i++) {
						elenco.get(i).setRibassoOfferto( normalizzaRibasso(elenco.get(i).getRibassoOfferto()) );
					}
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la graduatoria della gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore durante la lettura dell'elenco degli operatori economici per l'espletamento per la graduatoria della gara",
							t);
		}
		return elenco;
	}
	

	public Long getFaseGara(String codice) throws ApsException {
		Long risultato = null;
		try {
			RisultatoNumericoOutType retWS = wsGareAppalto.getProxyWSGare()
					.getFaseGara(codice);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della fase per la procedura con codice "
						+ codice + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della fase per la procedura con codice "
					+ codice, t);
		}
		return risultato;	
	}

	@Override
	public SearchResult<ComunicazioneType> getNews(
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException {
		SearchResult<ComunicazioneType> risultato = new SearchResult<ComunicazioneType>();
		List<ComunicazioneType> comunicazioni = null;
		ComunicazioniPersonaliOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			retWS = wsGareAppalto.getProxyWSGare()
				.getNews(indicePrimoRecord, maxNumRecord, stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getComunicazione() != null) {
					comunicazioni = Arrays.asList(retWS.getComunicazione());
				} else {
					comunicazioni = new ArrayList<>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle news: " + retWS.getErrore());
			}
			risultato.setDati(comunicazioni);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle news ", t);
		}

		return risultato;
	}

//	@Override	 
//	public List<SoggettoAderenteType> getSoggettiAderenti(
//    		String codice) throws ApsException {
//		List<SoggettoAderenteType> elenco = new ArrayList<SoggettoAderenteType>();
//		SoggettiAderentiOutType retWS = null;
//		try {
//			retWS = wsGareAppalto.getProxyWSGare().getSoggettiAderenti(codice);
//			if (retWS.getErrore() == null) {
//				if (retWS.getSoggetti() != null) {
//					elenco = Arrays.asList(retWS.getSoggetti());
//				} else {
//					elenco = new ArrayList<SoggettoAderenteType>();
//				}
//			} else {
//				// se si verifica un errore durante l'estrazione dei dati con il
//				// servizio, allora si ritorna un'eccezione che contiene il
//				// messaggio di errore
//				throw new ApsException(
//						"Errore durante la lettura dei soggetti aderenti: " + retWS.getErrore());
//			}
//			
//		} catch (RemoteException t) {
//			throw new ApsException(
//					"Errore inaspettato durante la lettura dei soggetti aderenti ", t);
//		}
//		return elenco;
//	}

	@Override	 
	public List<OperatoreIscrittoType> getOperatoriIscritti(
			String codice) throws ApsException {
		List<OperatoreIscrittoType> operatori = null;
		OperatoriIscrittiOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getOperatoriIscritti(codice);
			if (retWS.getErrore() == null) {
				if (retWS.getOperatori() != null) {
					operatori = Arrays.asList(retWS.getOperatori());
				} else {
					operatori = new ArrayList<OperatoreIscrittoType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura degli operatori iscritti : " + retWS.getErrore());
			}
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura degli operatori iscritti ", t);
		}
		return operatori;
	}

	@Override	 
	public List<CategoriaOperatoreIscrittoType> getElencoCategorieOperatoriIscritti(
    		String codice) throws ApsException {
		List<CategoriaOperatoreIscrittoType> categorie = null;
		CategorieOperatoriIscrittiOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getElencoCategorieOperatoriIscritti(codice);
			if (retWS.getErrore() == null) {
				if (retWS.getCategorie() != null) {
					categorie = Arrays.asList(retWS.getCategorie());
				} else {
					categorie = new ArrayList<CategoriaOperatoreIscrittoType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura degli operatori iscritti : " + retWS.getErrore());
			}
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura degli operatori iscritti ", t);
		}
		return categorie;
	}

	@Override	 
	public List<DocumentoAllegatoLotto> checkDocumentiNulli(
    		String codiceGara, String username, String tipoBusta) throws ApsException {
		List<DocumentoAllegatoLotto> documenti = null;
		CheckDocumentiNulliOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().checkDocumentiNulli(codiceGara, username, tipoBusta);
			if (retWS.getErrore() == null) {
				if (retWS.getRisultato() != null) {
					documenti = Arrays.asList(retWS.getRisultato());
				} 
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante il controllo dei documenti vuoti : " + retWS.getErrore());
			}
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante il controllo dei documenti vuoti ", t);
		}
		return documenti;
	}

	@Override	 
	public List<DocumentoAllegatoLotto> checkDimensioneDocumenti(
    		String codiceGara, String username, String tipoBusta) throws ApsException {
		List<DocumentoAllegatoLotto> documenti = null;
		CheckDimensioneDocumentiOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().checkDimensioneDocumenti(codiceGara, username, tipoBusta);
			if (retWS.getErrore() == null) {
				if (retWS.getRisultato() != null) {
					documenti = Arrays.asList(retWS.getRisultato());
				} 
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante il controllo della dimensione dei documenti : " + retWS.getErrore());
			}
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante il controllo della dimensione dei documenti ", t);
		}
		return documenti;
	}

	
	@Override
	public SearchResult<DeliberaType> getDelibere(
			String stazioneAppaltante, 
			String oggetto, 
			String tipoAppalto,
			String cig,
			Date dataPubblicazioneDa, 
			Date dataPubblicazioneA,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<DeliberaType> risultato = new SearchResult<DeliberaType>();
		List<DeliberaType> delibere = null;
		DelibereOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);
			
			Integer annoInizio = Calendar.getInstance().get(Calendar.YEAR) - 
								 (Integer) wsGareAppalto.getAppParamManager().getConfigurationValue(
										 	AppParamManager.PUBBLICAZIONE_NUM_ANNI);
			 
			retWS = wsGareAppalto.getProxyWSGare().getDelibere(
					annoInizio,					
					stazioneAppaltante, 
					oggetto, 
					tipoAppalto, 
					cig, 
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null),
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					sommaUrgenza,
					indicePrimoRecord, maxNumRecord);

			if (retWS.getErrore() == null) {
				if (retWS.getDelibere() != null) {
					delibere = Arrays.asList(retWS.getDelibere());
				} else {
					delibere = new ArrayList<DeliberaType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle delibere a contrarre "
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(delibere);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura delle delibere a contrarre "
					, t);
		}

		return risultato;
	}
	
	@Override
	public SearchResult<SommaUrgenzaType> getElencoSommaUrgenza(
			String stazioneAppaltante, 
			String oggetto, 
			String tipoAppalto,
			String cig,
			Date dataPubblicazioneDa, 
			Date dataPubblicazioneA,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<SommaUrgenzaType> risultato = new SearchResult<SommaUrgenzaType>();
		List<SommaUrgenzaType> sommaUrgenza = null;
		SommaUrgenzaOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			retWS = wsGareAppalto.getProxyWSGare().getElencoSommaUrgenza(
					stazioneAppaltante, 
					oggetto, 
					tipoAppalto, 
					cig, 
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null),
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					indicePrimoRecord, maxNumRecord);

			if (retWS.getErrore() == null) {
				if (retWS.getSommaUrgenza() != null) {
					sommaUrgenza = Arrays.asList(retWS.getSommaUrgenza());
				} else {
					sommaUrgenza = new ArrayList<SommaUrgenzaType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco somma urgenza "
						+ ": " + retWS.getErrore());
			}
			risultato.setDati(sommaUrgenza);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotali());
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dell'elenco somma urgenza "
					, t);
		}

		return risultato;
	}

	
	@Override
	public List<WSDMConfigType> getWSDMConfig(
			String stazioneAppaltante) throws ApsException 
	{
		List<WSDMConfigType> wsdmconfig = new ArrayList<WSDMConfigType>();
		WSDMConfigOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getWSDMConfig(stazioneAppaltante);
			if (retWS.getErrore() == null) {
				if (retWS.getWSDMConfig() != null) {
					wsdmconfig = Arrays.asList(retWS.getWSDMConfig());
				} else {
					wsdmconfig = new ArrayList<WSDMConfigType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della configurazione WSDM "
						+ ": " + retWS.getErrore());
			}
			
		} catch (RemoteException t) {
			wsdmconfig = null;
			throw new ApsException(
					"Errore durante la lettura della configurazione WSDM "
					, t);
		}
	
		return wsdmconfig;
	}

	@Override
	public String getIdImpresa(String username) throws ApsException {
		String codice = null;
		try {
			codice = wsGareAppalto.getProxyWSGare().getIdImpresa(username);
			if (codice == null) {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della codice impresa ");
			}
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura del codice impresa "
					, t);
		}
	
		return codice;
	}
	  
    /**
     * FNM gare privatistiche acquisto/vendita
     */ 
	@Override
	public SearchResult<GaraType> getElencoBandiScadutiPrivatistiche(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
					.getAppParamManager().getConfigurationValue(
							AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setNumAnniPubblicazione(numAnniPubblicazione);
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setEsito(search.convertedEsito());
			filtri.setGaraPrivatistica(search.getGaraPrivatistica());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_SCADUTE));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiScadutiPrivatistiche(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi scaduti: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi scaduti",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> getElencoBandiPerRichiesteOffertaPrivatistiche(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setGaraPrivatistica(search.getGaraPrivatistica());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiPerRichiesteOffertaPrivatistiche(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati( new ArrayList<GaraType>() );
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi abilitati all'inserimento di richieste di invio offerta: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi abilitati all'inserimento di richieste di invio offerta",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> getElencoBandiPrivatistiche(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setGaraPrivatistica(search.getGaraPrivatistica());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_IN_CORSO));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoBandiPrivatistiche(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati(Arrays.asList(retWS.getElencoBandi()));
				else
					bandi.setDati(new ArrayList<>());
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco bandi: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco bandi",
					t);
		}

		return bandi;
	}

	@Override
	public SearchResult<GaraType> searchBandiPerProcInAggiudicazionePrivatistiche(BandiSearchBean search) throws ApsException {
		SearchResult<GaraType> bandi = new SearchResult<>();
		ElencoBandiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, search.getStazioneAppaltante());

			GaraTypeSearch filtri = new GaraTypeSearch();
			filtri.setTokenRichiedente(search.getTokenRichiedente());
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(search.getOggetto());
			filtri.setCig(search.getCig());
			filtri.setTipoAppalto(search.getTipoAppalto());
			filtri.setDataPubblicazioneDa(search.convertDate(search.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(search.convertDate(search.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(search.convertDate(search.getDataScadenzaDa()));
			filtri.setDataScadenzaA(search.convertDate(search.getDataScadenzaA()));
			filtri.setProceduraTelematica(search.convertedProceduraTelematica());
			filtri.setStato(search.getStato());
			filtri.setAltriSoggetti(search.getAltriSoggetti());
			filtri.setSommaUrgenza(search.convertedSommaUrgenza());
			filtri.setIsGreen(search.getIsGreen());
			filtri.setIsRecycle(search.getIsRecycle());
			filtri.setIsPnrr(search.getIsPnrr());
			filtri.setOrderCriteria(getOrderCriteriaOrDefault(search.getOrderCriteria(), DEFAULT_GARE_SCADUTE));
			filtri.setCodice(search.getCodice());
			filtri.setIndicePrimoRecord(search.getIndicePrimoRecord());
			filtri.setMaxNumRecord(search.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().searchBandiPerProcInAggiudicazionePrivatistiche(filtri);
			
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandi() != null)
					bandi.setDati( Arrays.asList(retWS.getElencoBandi()) );
				else
					bandi.setDati( new ArrayList<GaraType>() );
				bandi.setNumTotaleRecord(retWS.getNumBandi());
				bandi.setNumTotaleRecordFiltrati(retWS.getNumBandi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca bandi per procedure in aggiudicazione: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca bandi per procedure in aggiudicazione",
					t);
		}

		return bandi;
	}
	
	@Override
	public List<InvitoGaraType> getElencoInvitiGara(
				String username,
				String codiceGara) throws ApsException {
		List<InvitoGaraType> inviti = new ArrayList<>();
		InvitiGaraOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getElencoInvitiGara(username, codiceGara);
			
			if (retWS.getErrore() == null) {
				if (retWS.getElencoInvitiGara() != null)
					inviti = Arrays.asList(retWS.getElencoInvitiGara());
				else
					inviti = new ArrayList<InvitoGaraType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura degli inviti di gara: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura degli inviti di gara: ",
					t);
		}

		return inviti;
	}

	public Long getNumeroOrdineInvito(String username, String codice) throws ApsException {
		Long risultato = null;
		try {
			RisultatoNumericoOutType retWS = wsGareAppalto.getProxyWSGare()
					.getNumeroOrdineInvito(username, codice);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del numero ordine invito per la procedura con codice "
						+ codice + " e utente " + username + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della fase per la procedura con codice "
					+ codice + " e utente " + username, t);
		}
		return risultato;
	}
	@Override
	public List<QuestionarioType> getQuestionari(
			String codice, 
			String tipologia, 
			String busta) throws ApsException {
		List<QuestionarioType> questionari = null;
		QuestionarioOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getQuestionari(codice, tipologia, busta);
			
			if (retWS.getErrore() == null) {
				if (retWS.getQuestionari() != null) {
					questionari = Arrays.asList(retWS.getQuestionari());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei questionari: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dei questionari: ",
					t);
		}

		return questionari;
	}
	
	@Override
	public List<ParametroQuestionarioType> getParametriQuestionario() throws ApsException {
		List<ParametroQuestionarioType> parametri = new ArrayList<>();
		ParametriQuestionarioOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getParametriQuestionario();
			
			if (retWS.getErrore() == null) {
				if (retWS.getParametri() != null) {
					parametri = Arrays.asList(retWS.getParametri());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei parametri del questionario: "
						+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dei parametri del questionario: ",
					t);
		}

		return parametri;
	}
	@Override
	public VendorRatingType getVendorRating(String username, Date date) throws ApsException {
		GetVendorRatingOutType vendorRatingOutType = null;
		try {
			vendorRatingOutType = wsGareAppalto.getProxyWSGare().getVendorRating(username, DateUtils.toCalendar(date));
		} catch (RemoteException e) {
			throw new ApsException("Errore durante il recupero del vendor rating per username: " + username, e);
		}
		return vendorRatingOutType.getVendorRatingType();
	}

}