package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;


import it.eldasoft.www.sil.WSLfs.ContrattoLFSType;
import it.eldasoft.www.sil.WSLfs.ElencoContrattiLFSOutType;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione dei contratti LFS.
 * 
 * @version ...
 * @author ...
 */
public interface IContrattiLFSManager {

	/**
	 * Restituisce la lista dei contratti filtrando per i parametri di input.
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param stato
	 *            stato della gara
	 * @param username
	 *            username dell'impresa
	 * 
	 * @return lista contratti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public ElencoContrattiLFSOutType searchContrattiLFS(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String gara,
			String username,
			String codice,
			int indicePrimoRecord,
	        int maxNumRecord) throws ApsException;
	
	/**
	 * Ritorna il dettaglio di un contratto a partire dalla sua chiave.
	 * @param tokenRichiedente
	 * 			  indica lo username 			  	
	 * @param codice
	 *            chiave del contratto
	 * @param nappal
	 * 			  numero dell'appalto
	 * @return dettaglio del contratto
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public ContrattoLFSType getDettaglioContrattoLFS(String tokenRichiedente, String codice, String nappal)
			throws ApsException;
	
}
