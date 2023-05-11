package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;
import it.eldasoft.www.sil.WSGareAppalto.ElencoContrattiOutType;
import it.eldasoft.www.sil.WSGareAppalto.GetDettaglioContrattoOutType;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore dei contratti.
 * 
 * @version 1.9.1
 * @author Stefano.Sabbadin
 */
public class ContrattiManager extends AbstractService implements IContrattiManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -75994598141793498L;
	
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

	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public List<ContrattoType> searchContratti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String stato,
			String username) throws ApsException 
	{
		List<ContrattoType> lista = null;
		ElencoContrattiOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare().searchContratti(
					stazioneAppaltante, 
					oggetto, 
					cig, 
					stato, 
					username);
			if (retWS.getErrore() == null) {
				if (retWS.getElenco() != null)
					lista = Arrays.asList(retWS.getElenco());
				else
					lista = new ArrayList<ContrattoType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della ricerca contratti: "
								+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della ricerca contratti",
					t);
		}

		return lista;
	}

	@Override
	public ContrattoType getDettaglioContratto(String codice)
			throws ApsException {
		ContrattoType dettaglio = null;
		GetDettaglioContrattoOutType retWS = null;
		try {
			retWS = wsGareAppalto.getProxyWSGare()
					.getDettaglioContratto(codice);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getContratto();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del contratto con codice "
								+ codice + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del contratto con codice "
							+ codice, t);
		}

		return dettaglio;
	}

}