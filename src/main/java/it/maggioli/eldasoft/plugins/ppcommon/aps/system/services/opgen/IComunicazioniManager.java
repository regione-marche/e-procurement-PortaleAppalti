package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen;

import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSAllegatoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;

import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia per la gestione dell'interfacciamento con il servizio per le
 * operazioni di gestione delle comunicazioni da e verso il backoffice.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public interface IComunicazioniManager {

    /**
     * Estrae l'elenco delle comunicazioni presenti nel backoffice che
     * rispettano i criteri di ricerca
     * 
     * @param criteriRicerca
     *            criteri di discriminazione delle comunicazioni
     * @return elenco di oggetti che identificano le testate delle comunicazioni
     *         estratte
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    List<DettaglioComunicazioneType> getElencoComunicazioni(
	    DettaglioComunicazioneType criteriRicerca) throws ApsException;

    /**
     * Estrae i dati relativi ad una comunicazione a partire dalla sua
     * chiave.
     * Se idDocumento e' nullo vengono recuperati tutti i dati della 
     * comunicazione e tutti gli allegati, mentre se idDocumento rappresenta 
     * il nome del file di una busta xml o l'UUID di un allegato della busta,
     * recupera solo l'allegato identificato da idDocumento 
     * 
     * @param applicativo
     *            identificativo dell'applicativo
     * @param id
     *            identificativo della comunicazione per l'applicativo
     * @param idDocumento
     *            id della busta xml o UUID del allegato allegato da scaricare
     * @return oggetto comunicazione con i dati di testata e tutti i file
     *         allegati
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public ComunicazioneType getComunicazione(String applicativo, long id, String idDocumento)
		throws ApsException;

    /**
     * Estrae tutti i dati relativi ad una comunicazione a partire dalla sua
     * chiave
     * 
     * @param applicativo
     *            identificativo dell'applicativo
     * @param id
     *            identificativo della comunicazione per l'applicativo
     * @return oggetto comunicazione con i dati di testata e tutti i file
     *         allegati
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    ComunicazioneType getComunicazione(String applicativo, long id)
	    throws ApsException;

	/**
	 * Invia una comunicazione al backoffice.
	 * 
	 * @param comunicazione
	 *            comunicazione da inviare
	 * @return id comunicazione aggiornata
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    Long sendComunicazione(ComunicazioneType comunicazione)
	    throws ApsException;

	/**
	 * Aggiorna lo stato delle comunicazioni in input.
	 * 
	 * @param comunicazioni
	 *            comunicazioni da aggiornare
	 * @param stato
	 *            nuovo stato da impostare
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	void updateStatoComunicazioni(
			DettaglioComunicazioneType[] comunicazioni, String stato)
			throws ApsException;
	
	/**
	 * Aggiorna la data lettura di una comunicazione per il destinatario in
	 * input.
	 * 
	 * @param applicativo
	 *            identificativo dell'applicativo
	 * @param idComunicazione
	 *            identificativo della comunicazione per l'applicativo
	 * @param idDestinatario
	 *            identificativo del destinatario della comunicazione
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	void updateDataLetturaDestinatarioComunicazione(String applicativo,
			long idComunicazione, long idDestinatario) throws ApsException;

	/**
     * Elimina una comunicazione e tutti i suoi allegati.
     * 
     * @param applicativo
     *            identificativo dell'applicativo
     * @param id
     *            identificativo della comunicazione per l'applicativo
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    void deleteComunicazione(String applicativo, long id)
	    throws ApsException;
    
	/**
     * Verifica se una comunicazione &egrave; stata processata lato backoffice
     * per cui non &egrave; pi&ugrave; possibile procedere ad aggiornamenti
     * della stessa.
     * 
     * @param applicativo
     *            identificativo dell'applicativo
     * @param id
     *            identificativo della comunicazione per l'applicativo
     * @return true se la comunicazione &egrave; stata processata, false
     *         altrimenti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    Boolean isComunicazioneProcessata(String applicativo, long id)
	    throws ApsException;
    
	/**
	 * Aggiorna i dati di protocollazione, eventualmente variando lo stato se
	 * specificato in input, ed associando un documento se indicato.
	 * 
	 * @param applicativo
	 *            id applicativo
	 * @param id
	 *            id comunicazione
	 * @param numeroProtocollo
	 *            numero di protocollo (obbligatorio) da attribuire
	 * @param dataProtocollo
	 *            data protocollo (obbligatoria) da attribuire
	 * @param stato
	 *            eventuale nuovo stato della comunicazione, da valorizzare solo
	 *            se si intende variare
	 * @param documento
	 *            eventuali coordinate del documento da associare al protocollo,
	 *            se necessario
	 */
    void protocollaComunicazione(String applicativo, long id,
			String numeroProtocollo, Date dataProtocollo, String stato,
			WSDocumentoType documento) throws ApsException;
    
	/**
	 * Aggiorna i dati di protocollazione, eventualmente variando lo stato se
	 * specificato in input, ed associando un documento se indicato.
	 * 
	 * @param applicativo
	 *            id applicativo
	 * @param id
	 *            id comunicazione
	 * @param numeroProtocollo
	 *            numero di protocollo (obbligatorio) da attribuire
	 * @param dataProtocollo
	 *            data protocollo (obbligatoria) da attribuire
	 * @param stato
	 *            eventuale nuovo stato della comunicazione, da valorizzare solo
	 *            se si intende variare
	 * @param documento
	 *            eventuali coordinate del documento da associare al protocollo,
	 *            se necessario
	 * @param allegati
	 *            eventuali allegati da associare all'elemento documentale,
	 *            se necessario
	 */
    void protocollaComunicazione(String applicativo, long id,
			String numeroProtocollo, Date dataProtocollo, String stato,
			WSDocumentoType documento,
			WSAllegatoType[] allegati) throws ApsException;
    
	/**
	 * Aggiorna la chiave di sessione di una comunicazione, eventualmente
	 * variando lo stato se specificato in input.
	 * 
	 * @param applicativo
	 *            id applicativo
	 * @param id
	 *            id comunicazione
	 * 
	 * @param sessionKey
	 *            chiave di sessione di cifratura da settare nella comunicazione
	 * @param stato
	 *            eventuale nuovo stato della comunicazione, da valorizzare solo
	 *            se si intende variare
	 */
	void updateSessionKeyComunicazione(String applicativo, long id,
			String sessionKey, String stato) throws ApsException;

	/**
	 * Elimina un allegato dei dati di protocollazione.
	 * 	 
	 * @param entita
	 *            entita a cui e' associato il documento
	 * @param applicativo
	 *            id applicativo
	 * @param id
	 *            id del documento
	 */
    void deleteAllegatoProtocollo(String entita, String applicativo, long id) throws ApsException;

}