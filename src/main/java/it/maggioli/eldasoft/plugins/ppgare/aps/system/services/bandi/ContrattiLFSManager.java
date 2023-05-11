package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;


import it.eldasoft.www.sil.WSLfs.ContrattoLFSType;
import it.eldasoft.www.sil.WSLfs.ElencoContrattiLFSOutType;
import it.eldasoft.www.sil.WSLfs.GetDettaglioContrattoLFSOutType;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore dei contratti LFS.
 * 
 * @version ...
 * @author ...
 */
public class ContrattiLFSManager extends AbstractService implements IContrattiLFSManager {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1115189429466244426L;
	
	private WSLfsWrapper wsLfs;

	public void setWsLfs(WSLfsWrapper proxyWsLfs) {
		this.wsLfs = proxyWsLfs;
	}

	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public ElencoContrattiLFSOutType searchContrattiLFS(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String gara,
			String username,
			String codice,
			int indicePrimoRecord,
	        int maxNumRecord) throws ApsException 
	{
		ElencoContrattiLFSOutType retWS = null;
		try {
			retWS = wsLfs.getProxyWSlfs().searchContrattiLFS(
					stazioneAppaltante, 
					oggetto, 
					cig, 
					gara, 
					codice,
					username,
					indicePrimoRecord,
			        maxNumRecord);
			if (retWS.getErrore() == null) {
				return retWS;
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
	}

	@Override
	public ContrattoLFSType getDettaglioContrattoLFS(String tokenRichiedente, String codice, String nappal)
			throws ApsException 
	{
		ContrattoLFSType dettaglio = null;
		GetDettaglioContrattoLFSOutType retWS = null;
		try {
			retWS = wsLfs.getProxyWSlfs().getDettaglioContrattoLFS(tokenRichiedente, codice, nappal);
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