package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;
import it.eldasoft.www.sil.WSGareAppalto.AvvisiTypeSearch;
import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoOutType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.ElencoAvvisiOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi.AvvisiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Servizio gestore degli avvisi di gara.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class AvvisiManager extends AbstractService implements IAvvisiManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 7959141699219931093L;

	/** Riferimento al wrapper per il web service Gare Appalto */
	private WSGareAppaltoWrapper wsGareAppalto;

	/**
	 * Imposta il web service gare
	 * 
	 * @param proxyWsGare
	 *            web service gare.
	 */
	public void setWsGareAppalto(WSGareAppaltoWrapper proxyWsGare) {
		this.wsGareAppalto = proxyWsGare;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public SearchResult<AvvisoType> searchAvvisi(AvvisiSearchBean model) throws ApsException {
		SearchResult<AvvisoType> avvisi = new SearchResult<AvvisoType>();
		ElencoAvvisiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, model.getStazioneAppaltante());

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			AvvisiTypeSearch filtri = new AvvisiTypeSearch();
			filtri.setNumAnniPubblicazione(numAnniPubblicazione);
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(model.getOggetto());
			filtri.setTipoAvviso(model.getTipoAvviso());
			filtri.setDataPubblicazioneDa(model.convertDate(model.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(model.convertDate(model.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(model.convertDate(model.getDataScadenzaDa()));
			filtri.setDataScadenzaA(model.convertDate(model.getDataScadenzaA()));
			filtri.setAltriSoggetti(model.getAltriSoggetti());
			filtri.setIsGreen(model.getIsGreen());
			filtri.setIsRecycle(model.getIsRecycle());
			filtri.setIsPnrr(model.getIsPnrr());
			filtri.setIndicePrimoRecord(model.getIndicePrimoRecord());
			filtri.setMaxNumRecord(model.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().searchAvvisi(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoAvvisi() != null)
					avvisi.setDati(Arrays.asList(retWS.getElencoAvvisi()));
				else
					avvisi.setDati(new ArrayList<>());
				avvisi.setNumTotaleRecord(retWS.getNumAvvisi());
				avvisi.setNumTotaleRecordFiltrati(retWS.getNumAvvisi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca avvisi: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca avvisi",
					t);
		}

		return avvisi;
	}

	@Override
	public SearchResult<AvvisoType> getElencoAvvisi(AvvisiSearchBean model) throws ApsException	{
		SearchResult<AvvisoType> avvisi = new SearchResult<>();
		ElencoAvvisiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, model.getStazioneAppaltante());

//			Integer numAnniPubblicazione = (Integer) wsGareAppalto
//				.getAppParamManager().getConfigurationValue(
//						AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			AvvisiTypeSearch filtri = new AvvisiTypeSearch();
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(model.getOggetto());
			filtri.setTipoAvviso(model.getTipoAvviso());
			filtri.setDataPubblicazioneDa(model.convertDate(model.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(model.convertDate(model.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(model.convertDate(model.getDataScadenzaDa()));
			filtri.setDataScadenzaA(model.convertDate(model.getDataScadenzaA()));
			filtri.setAltriSoggetti(model.getAltriSoggetti());
			filtri.setIsGreen(model.getIsGreen());
			filtri.setIsRecycle(model.getIsRecycle());
			filtri.setIsPnrr(model.getIsPnrr());
			filtri.setIndicePrimoRecord(model.getIndicePrimoRecord());
			filtri.setMaxNumRecord(model.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoAvvisi(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoAvvisi() != null)
					avvisi.setDati(Arrays.asList(retWS.getElencoAvvisi()));
				else
					avvisi.setDati(new ArrayList<>());
				avvisi.setNumTotaleRecord(retWS.getNumAvvisi());
				avvisi.setNumTotaleRecordFiltrati(retWS.getNumAvvisi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco avvisi: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco avvisi",
					t);
		}

		return avvisi;
	}

	@Override
	public SearchResult<AvvisoType> getElencoAvvisiScaduti(AvvisiSearchBean model) throws ApsException
	{
		SearchResult<AvvisoType> avvisi = new SearchResult<>();
		ElencoAvvisiOutType retWS = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, model.getStazioneAppaltante());

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			AvvisiTypeSearch filtri = new AvvisiTypeSearch();
			filtri.setNumAnniPubblicazione(numAnniPubblicazione);
			filtri.setStazioneAppaltante(stazioneAppaltante);
			filtri.setOggetto(model.getOggetto());
			filtri.setTipoAvviso(model.getTipoAvviso());
			filtri.setDataPubblicazioneDa(model.convertDate(model.getDataPubblicazioneDa()));
			filtri.setDataPubblicazioneA(model.convertDate(model.getDataPubblicazioneA()));
			filtri.setDataScadenzaDa(model.convertDate(model.getDataScadenzaDa()));
			filtri.setDataScadenzaA(model.convertDate(model.getDataScadenzaA()));
			filtri.setAltriSoggetti(model.getAltriSoggetti());
			filtri.setIsGreen(model.getIsGreen());
			filtri.setIsRecycle(model.getIsRecycle());
			filtri.setIsPnrr(model.getIsPnrr());
			filtri.setIndicePrimoRecord(model.getIndicePrimoRecord());
			filtri.setMaxNumRecord(model.getiDisplayLength());

			retWS = wsGareAppalto.getProxyWSGare().getElencoAvvisiScaduti(filtri);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoAvvisi() != null)
					avvisi.setDati(Arrays.asList(retWS.getElencoAvvisi()));
				else
					avvisi.setDati(new ArrayList<>());
				avvisi.setNumTotaleRecord(retWS.getNumAvvisi());
				avvisi.setNumTotaleRecordFiltrati(retWS.getNumAvvisi());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco avvisi scaduti: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco avvisi scaduti",
					t);
		}

		return avvisi;
	}

	@Override
	public DettaglioAvvisoType getDettaglioAvviso(String codiceGara)
			throws ApsException {
		DettaglioAvvisoType dettaglio = null;
		DettaglioAvvisoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDettaglioAvviso(codiceGara);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getAvviso();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio avviso di gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio avviso di gara",
					t);
		}

		return dettaglio;
	}
	
}