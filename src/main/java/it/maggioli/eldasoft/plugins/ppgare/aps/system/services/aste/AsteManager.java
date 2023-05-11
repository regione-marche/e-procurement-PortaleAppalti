package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.eldasoft.www.sil.WSAste.DettaglioFaseAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.GetClassificaOutType;
import it.eldasoft.www.sil.WSAste.GetDettaglioAstaOutType;
import it.eldasoft.www.sil.WSAste.GetElencoRilanciOutType;
import it.eldasoft.www.sil.WSAste.GetElencoTerminiAstaOutType;
import it.eldasoft.www.sil.WSAste.GetElencoTipiClassificaOutType;
import it.eldasoft.www.sil.WSAste.GetFasiAstaOutType;
import it.eldasoft.www.sil.WSAste.GetPrezziUnitariOutType;
import it.eldasoft.www.sil.WSAste.InsertRilancioAstaOutType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore delle aste.
 * 
 * @version 1.0
 * @author ...
 */
public class AsteManager extends AbstractService implements IAsteManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 7039912668423322712L;

	/** Riferimento al wrapper per il web service Gare Appalto */
	private WSAsteWrapper wsAste;

	
	/**
	 * Imposta il web service gare
	 * 
	 * @param proxyWsGare
	 *            web service gare.
	 */
	public void setwsAste(WSAsteWrapper proxyWsGare) {
		this.wsAste = proxyWsGare;
	}

	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}
	
	/**
	 * Restituisce l'elenco della classifica di un'asta
	 * 
	 * @throws ApsException 
	 */
	@Override
	public List<DettaglioClassificaType> getClassifica(
			java.lang.Integer tipoClassifica,
			java.lang.String codice,
			java.lang.String codiceLotto,
			java.lang.String username,
			java.lang.String faseAsta) throws ApsException {
		List<DettaglioClassificaType> dettaglio = null;
		GetClassificaOutType retWS = null;
		try {
			retWS = (GetClassificaOutType) wsAste.getProxyWSAste()
				.getClassifica(tipoClassifica, 
							   codice, codiceLotto, 
							   username, 
							   faseAsta);

			if (retWS.getErrore() == null) {
				if(retWS.getDettaglio() != null) {
					dettaglio = Arrays.asList(retWS.getDettaglio());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio dell'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio dell'asta",
					t);
		}
		return dettaglio;
	};

	/**
	 * Restituisce i dettagli delle fasi di un'asta
	 * 
	 * @throws ApsException  
	 */
	@Override
	public List<DettaglioFaseAstaType> getFasiAsta(String codice)
			throws ApsException {
		List<DettaglioFaseAstaType> dettaglio = null;
		GetFasiAstaOutType retWS = null;
		try {
			retWS = (GetFasiAstaOutType)wsAste.getProxyWSAste()
				.getFasiAsta(codice);

			if (retWS.getErrore() == null) {
				if(retWS.getDettaglio() != null) {
					dettaglio = Arrays.asList(retWS.getDettaglio());
				}				
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle fasi dell'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura delle fasi dell'asta",
					t);
		}
		return dettaglio;
	}

	/**
	 * Restituisce i dettagli di un'asta
	 * 
	 * @throws ApsException  
	 */
	@Override
	public DettaglioAstaType getDettaglioAsta(
			java.lang.String codice,
			java.lang.String username) throws ApsException {
		DettaglioAstaType dettaglio = null;
		GetDettaglioAstaOutType retWS = null;
		try {
			retWS = (GetDettaglioAstaOutType)wsAste.getProxyWSAste()
				.getDettaglioAsta(codice, username);

			if (retWS.getErrore() == null) {
				if(retWS.getDettaglio() != null) {
					dettaglio = retWS.getDettaglio();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio dell'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del dettaglio dell'asta",
					t);
		}
		return dettaglio;		
	}

	@Override
	public List<DettaglioRilancioType> getElencoRilanci(
			String codice,
			String codiceLotto, 
			String username,
			String numeroFase) throws ApsException {
		List<DettaglioRilancioType> dettaglio = null;
		GetElencoRilanciOutType retWS = null;
		try {
			retWS = (GetElencoRilanciOutType)wsAste.getProxyWSAste()
				.getElencoRilanci(codice, codiceLotto, username, numeroFase);

			if (retWS.getErrore() == null) {
				if(retWS.getDettaglio() != null) {
					dettaglio = Arrays.asList(retWS.getDettaglio());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco rilanci dell'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco rilanci dell'asta",
					t);
		}
		return dettaglio;
	}

	@Override
	public Long insertRilancioAsta(
			String codice, 
			String codiceLotto,
			String username, 
			Double offerta,
			List<VoceDettaglioAstaType> prezziUnitari) throws ApsException {
		Long risultato = null;
		InsertRilancioAstaOutType retWS = null;
		try {
			VoceDettaglioAstaType[] prezzi = null;
			if(prezziUnitari != null) {
				prezzi = (VoceDettaglioAstaType[]) prezziUnitari.toArray(new VoceDettaglioAstaType[]{});
//				prezzi = new VoceDettaglioAstaType[prezziUnitari.size()];
//				for(int i = 0; i < prezziUnitari.size(); i++) {
//					prezzi[i] = prezziUnitari.get(i);
//				}
			}
						  
			retWS = (InsertRilancioAstaOutType) wsAste.getProxyWSAste()
				.insertRilancioAsta(codice, codiceLotto, username, offerta,	prezzi);
			
			if (retWS.getErrore() == null)
				risultato = (retWS.getIdRilancio() != null ? retWS.getIdRilancio() : null); 
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante l'invio del rilancio dell'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato l'invio del rilancio dell'asta",
					t);
		}
		return risultato;
	}

	@Override
	public Map<String, String> getElencoTerminiAsta() throws ApsException {
		LinkedHashMap<String, String> elenco = null;
		GetElencoTerminiAstaOutType retWS = null;
		try {
			retWS = (GetElencoTerminiAstaOutType)wsAste.getProxyWSAste()
				.getElencoTerminiAsta();

			if (retWS.getErrore() == null) {
				if(retWS.getElenco() != null) {
					elenco = new LinkedHashMap<String, String>();
					for(int i = 0; i < retWS.getElenco().length; i++) {
						elenco.put(retWS.getElenco()[i].getKey(), 
									retWS.getElenco()[i].getValue());
					}
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco dei termini d'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco dei termini d'asta",
					t);
		}
		return elenco;
	}

	@Override
	public Map<String, String> getElencoTipiClassifica() throws ApsException {
		LinkedHashMap<String, String> elenco = null;
		GetElencoTipiClassificaOutType retWS = null;
		try {
			retWS = (GetElencoTipiClassificaOutType)wsAste.getProxyWSAste()
				.getElencoTipiClassifica();

			if (retWS.getErrore() == null) {
				if(retWS.getElenco() != null) {
					elenco = new LinkedHashMap<String, String>();
					for(int i = 0; i < retWS.getElenco().length; i++) {
						elenco.put(retWS.getElenco()[i].getKey(), 
									retWS.getElenco()[i].getValue());
					}
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dell'elenco dei tipi classifica d'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dell'elenco dei tipi classifica d'asta",
					t);
		}
		return elenco;
	}
	
	@Override
	public List<VoceDettaglioAstaType> getPrezziUnitariPrimoRilancio(
			String codice, 
			String codiceLotto, 
			String username) throws ApsException {
		List<VoceDettaglioAstaType>  dettaglio = null;
		GetPrezziUnitariOutType retWS = null;
		try {
			retWS = (GetPrezziUnitariOutType)wsAste.getProxyWSAste()
				.getPrezziUnitariPrimoRilancio(codice, codiceLotto, username);

			if (retWS.getErrore() == null) {
				if(retWS.getDettaglio() != null) {				
					dettaglio = Arrays.asList(retWS.getDettaglio());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei prezzi unitari dell'offerta d'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dei prezzi unitari dell'offerta d'asta: ",
					t);
		}
		return dettaglio;
	}

	@Override
	public List<VoceDettaglioAstaType> getPrezziUnitariRilancio(
			String codice, 
			String codiceLotto, 
			String username, 
			Long idRilancio) throws ApsException {
		List<VoceDettaglioAstaType>  dettaglio = null;
		GetPrezziUnitariOutType retWS = null;
		try {
			retWS = (GetPrezziUnitariOutType)wsAste.getProxyWSAste()
				.getPrezziUnitariRilancio(codice, codiceLotto, username, idRilancio);

			if (retWS.getErrore() == null) {
				if(retWS.getDettaglio() != null) {				
					dettaglio = Arrays.asList(retWS.getDettaglio());
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei prezzi unitari dell'offerta d'asta: "
						+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dei prezzi unitari dell'offerta d'asta: ",
					t);
		}
		return dettaglio;
	}

}