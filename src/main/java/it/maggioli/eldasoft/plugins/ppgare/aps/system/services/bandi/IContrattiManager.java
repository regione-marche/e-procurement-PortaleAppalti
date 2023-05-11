package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;

import java.util.List;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione dei contratti.
 * 
 * @version 1.9.1
 * @author Stefano.Sabbadin
 */
public interface IContrattiManager {

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
	public List<ContrattoType> searchContratti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String stato, 
			String username) throws ApsException;
	
	/**
	 * Ritorna il dettaglio di un contratto a partire dalla sua chiave.
	 * 
	 * @param codice
	 *            chiave del contratto
	 * @return dettaglio del contratto
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public ContrattoType getDettaglioContratto(String codice)
			throws ApsException;
}
