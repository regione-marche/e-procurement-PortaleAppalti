package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoOutType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoType;
import it.eldasoft.www.sil.WSGareAppalto.ElencoEsitiOutType;
import it.eldasoft.www.sil.WSGareAppalto.EsitoType;
import it.eldasoft.www.sil.WSGareAppalto.LottiEsitoOutType;
import it.eldasoft.www.sil.WSGareAppalto.LottoEsitoType;
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
 * Servizio gestore degli esiti.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class EsitiManager extends AbstractService implements IEsitiManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 4901521573783291085L;

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
	public SearchResult<EsitoType> searchEsiti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Boolean proceduraTelematica,
			String altriSoggetti,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<EsitoType> esiti = new SearchResult<EsitoType>();
		ElencoEsitiOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);
			
			retWS = wsGareAppalto.getProxyWSGare().searchEsiti(
					stazioneAppaltante, oggetto, cig, tipoAppalto,
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null), 
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					proceduraTelematica, 
					altriSoggetti,
					sommaUrgenza,
					numAnniPubblicazione,
					indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoEsiti() != null)
					esiti.setDati( Arrays.asList(retWS.getElencoEsiti()) );
				else
					esiti.setDati( new ArrayList<EsitoType>() );
				esiti.setNumTotaleRecord(retWS.getNumEsiti());
				esiti.setNumTotaleRecordFiltrati(retWS.getNumEsiti());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca esiti: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca esiti",
					t);
		}

		return esiti;
	}

	@Override
	public SearchResult<EsitoType> getElencoEsiti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Boolean proceduraTelematica,
			String altriSoggetti,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<EsitoType> esiti = new SearchResult<EsitoType>();
		ElencoEsitiOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);
			
			retWS = wsGareAppalto.getProxyWSGare().getElencoEsiti(
					stazioneAppaltante, oggetto, cig, tipoAppalto,
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null), 
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					proceduraTelematica, 
					altriSoggetti,
					sommaUrgenza,
					numAnniPubblicazione,
					indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoEsiti() != null)
					esiti.setDati( Arrays.asList(retWS.getElencoEsiti()) );
				else
					esiti.setDati( new ArrayList<EsitoType>() );
				esiti.setNumTotaleRecord(retWS.getNumEsiti());
				esiti.setNumTotaleRecordFiltrati(retWS.getNumEsiti());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco esiti: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco esiti",
					t);
		}

		return esiti;
	}
	
	@Override
	public SearchResult<EsitoType> getElencoEsitiAffidamenti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Boolean proceduraTelematica,
			String altriSoggetti,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException 
	{
		SearchResult<EsitoType> esiti = new SearchResult<EsitoType>();
		ElencoEsitiOutType retWS = null;
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			Integer numAnniPubblicazione = (Integer) wsGareAppalto
				.getAppParamManager().getConfigurationValue(
					AppParamManager.PUBBLICAZIONE_NUM_ANNI);
			
			retWS = wsGareAppalto.getProxyWSGare().getElencoEsitiAffidamenti(
					stazioneAppaltante, oggetto, cig, tipoAppalto,
					(dataPubblicazioneDa != null ? DateUtils.toCalendar(dataPubblicazioneDa) : null), 
					(dataPubblicazioneA != null ? DateUtils.toCalendar(dataPubblicazioneA) : null),
					proceduraTelematica, 
					altriSoggetti,
					sommaUrgenza,
					numAnniPubblicazione,
					indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoEsiti() != null)
					esiti.setDati( Arrays.asList(retWS.getElencoEsiti()) );
				else
					esiti.setDati( new ArrayList<EsitoType>() );
				esiti.setNumTotaleRecord(retWS.getNumEsiti());
				esiti.setNumTotaleRecordFiltrati(retWS.getNumEsiti());
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco esiti affidamenti: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco esiti affidamenti",
					t);
		}

		return esiti;
	}

	@Override
	public DettaglioEsitoType getDettaglioEsito(String codiceGara)
			throws ApsException {
		DettaglioEsitoType dettaglio = null;
		DettaglioEsitoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getDettaglioEsito(codiceGara);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getEsito();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio esito di gara: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio esito di gara",
					t);
		}

		return dettaglio;
	}

	@Override
	public LottoEsitoType[] getLottiEsito(String codiceGara)
			throws ApsException {
		LottoEsitoType[] dettaglio = null;
		LottiEsitoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().getLottiEsito(codiceGara);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getLotto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei lotti di un esito: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei lotti di un esito",
					t);
		}

		return dettaglio;
	}

}
