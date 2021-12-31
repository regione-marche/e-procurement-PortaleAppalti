package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoOutType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.ElencoAvvisiOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

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
	public SearchResult<AvvisoType> searchAvvisi(
			String stazioneAppaltante,
			String oggetto, 
			String tipoAppalto, 
			Date dataPubblicazioneDa, Date dataPubblicazioneA, 
			Date dataScadenzaDa, Date dataScadenzaA,
			String altriSoggetti,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<AvvisoType> avvisi = new SearchResult<AvvisoType>();
		ElencoAvvisiOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);

			retWS = wsGareAppalto.getProxyWSGare().searchAvvisi(
					stazioneAppaltante, oggetto, tipoAppalto, 
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null), 
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					(dataScadenzaDa != null ? DateUtils.toCalendar(dataScadenzaDa) : null), 
					(dataScadenzaA != null ? DateUtils.toCalendar(dataScadenzaA) : null),
					altriSoggetti,
					numAnniPubblicazione,
					indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoAvvisi() != null)
					avvisi.setDati(Arrays.asList(retWS.getElencoAvvisi()));
				else
					avvisi.setDati(new ArrayList<AvvisoType>());
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
	public SearchResult<AvvisoType> getElencoAvvisi(
			String stazioneAppaltante,
			String oggetto, 
			String tipoAppalto, 
			Date dataPubblicazioneDa, Date dataPubblicazioneA, 
			Date dataScadenzaDa, Date dataScadenzaA,
			String altriSoggetti,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<AvvisoType> avvisi = new SearchResult<AvvisoType>();
		ElencoAvvisiOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

//			Integer numAnniPubblicazione = (Integer) wsGareAppalto
//				.getAppParamManager().getConfigurationValue(
//						AppParamManager.PUBBLICAZIONE_NUM_ANNI);
			
			retWS = wsGareAppalto.getProxyWSGare().getElencoAvvisi(
					stazioneAppaltante, oggetto, tipoAppalto, 
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null), 
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					(dataScadenzaDa != null ? DateUtils.toCalendar(dataScadenzaDa) : null), 
					(dataScadenzaA != null ? DateUtils.toCalendar(dataScadenzaA) : null),
					altriSoggetti,
					indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoAvvisi() != null)
					avvisi.setDati(Arrays.asList(retWS.getElencoAvvisi()));
				else
					avvisi.setDati(new ArrayList<AvvisoType>());
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
	public SearchResult<AvvisoType> getElencoAvvisiScaduti(
			String stazioneAppaltante,
			String oggetto, 
			String tipoAppalto, 
			Date dataPubblicazioneDa, Date dataPubblicazioneA, 
			Date dataScadenzaDa, Date dataScadenzaA,
			String altriSoggetti,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<AvvisoType> avvisi = new SearchResult<AvvisoType>();
		ElencoAvvisiOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);
			
			retWS = wsGareAppalto.getProxyWSGare().getElencoAvvisiScaduti(
					stazioneAppaltante, 
					oggetto, 
					tipoAppalto, 
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null), 
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					(dataScadenzaDa != null ? DateUtils.toCalendar(dataScadenzaDa) : null), 
					(dataScadenzaA != null ? DateUtils.toCalendar(dataScadenzaA) : null),
					altriSoggetti,
					numAnniPubblicazione,
					indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoAvvisi() != null)
					avvisi.setDati(Arrays.asList(retWS.getElencoAvvisi()));
				else
					avvisi.setDati(new ArrayList<AvvisoType>());
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