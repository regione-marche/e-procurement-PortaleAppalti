package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia base per i servizi di gestione delle customizzazioni clienti
 * sulle funzionalit&agrave;.
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public interface ICustomConfigManager {

    /**
     * Testa se una determinata configurazione deve risultare visibile al
     * cliente.
     * 
     * @param objectId
     *            oggetto padre
     * @param attrib
     *            attributo dell'oggetto
     * @return true se l'attributo risulta visibile, false altrimenti
     * @throws Exception
     *             eccezione ritornata quanto l'oggetto o l'attributo non sono
     *             presenti nella configurazione caricata
     */
    public boolean isVisible(String objectId, String attrib)
	    throws Exception;

    /**
     * Testa se una determinata configurazione deve risultare obbligatoria per
     * il cliente.
     * 
     * @param objectId
     *            oggetto padre
     * @param attrib
     *            attributo dell'oggetto
     * @return true se l'attributo risulta obbligatorio, false altrimenti
     * @throws Exception
     *             eccezione ritornata quando l'oggetto o l'attributo non sono
     *             presenti nella configurazione caricata
     */
    public boolean isMandatory(String objectId, String attrib)
	    throws Exception;
    
	/**
	 * Testa se una determinata configurazione (attivazione di una
	 * funzionalit&agrave; o controllo) deve risultare disponibile per il
	 * cliente.
	 * 
	 * @param objectId
	 *            oggetto padre
	 * @param attrib
	 *            attributo dell'oggetto
	 * @return true se la funzione risulta attiva, false altrimenti
	 * @throws Exception
	 *             eccezione ritornata quando l'oggetto o l'attributo non sono
	 *             presenti nella configurazione caricata
	 */
	public boolean isActiveFunction(String objectId, String attrib)
			throws Exception;

	/**
	 * Legge le configurazioni definite nel DB.
	 * 
	 * @return elenco delle configurazioni definite
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public List<CustomConfig> getCustomConfigs() throws ApsSystemException;
    
	/**
	 * Aggiorna l'elenco delle configurazioni e ricarica i dati in memoria.
	 * 
	 * @param configs
	 *            configurazioni da aggiornare
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public void updateCustomConfigs(List<CustomConfig> configs)
			throws ApsSystemException;

}
