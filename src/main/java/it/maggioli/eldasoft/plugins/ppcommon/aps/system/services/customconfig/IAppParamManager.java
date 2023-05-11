package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia base per i servizi di gestione delle configurazioni della
 * verticalizzazione.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public interface IAppParamManager {

	/**
	 * Legge le configurazioni definite nel DB.
	 * 
	 * @return elenco delle configurazioni definite
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public List<AppParam> getAppParams() throws ApsSystemException;
	
	
	/**
	 * Legge le configurazioni definite nel DB filtrate per categoria.
	 * 
	 * @param category
	 * 
	 * @return elenco delle configurazioni definite
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public List<AppParam> getAppParamsByCategory(String category) throws ApsSystemException;
	/**
	 * Legge le configurazioni definite nel DB filtrate per categoria impostate come mappa chiave / valore
	 * 
	 * @param category
	 * 
	 * @return elenco delle configurazioni definite
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public Map<String,AppParam> getMapAppParamsByCategory(String category) throws ApsSystemException;

	/**
	 * Aggiorna l'elenco delle configurazioni e ricarica i dati in memoria.
	 * 
	 * @param configs
	 *            configurazioni da aggiornare
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public void updateAppParams(List<AppParam> configs)
			throws ApsSystemException;

	/**
	 * Aggiorna l'elenco delle configurazioni ai valori di default e ricarica i
	 * dati in memoria.
	 * 
	 * @param names
	 *            elenco delle chiavi delle configurazioni da ripristinare al
	 *            valore di default
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public void updateDefaultAppParams(String[] names)
			throws ApsSystemException;

	/**
	 * Ritorna il valore di una configurazione.
	 * 
	 * @param name
	 *            identificativo oggetto
	 * @return valore della configurazione se esistente, null altrimenti
	 */
	public Object getConfigurationValue(String name);
	public Integer getConfigurationValueIntDef(String name, Integer defValue);
	public Long getConfigurationValueLong(String name, Long defValue);
	public Boolean getConfigurationValueBoolean(String name, Boolean defValue);

	/**
	 * Imposta la stazione appaltante per la configurazione multi WSDM
	 * Da richiamare in fase iniziale di una protocollazione e nella fase conclusiva di una protocollazione
	 * 
	 * @param stazioneAppaltante 
	 *				<> NULL apre la fase di protocollazione per la stazione appaltante impostata
	 *				= NULL conclude la fase di protocollazione
	 */	
	public void setStazioneAppaltanteProtocollazione(String stazioneAppaltante);

	/**
	 * restituisce il tipo protocollazione attivo 
	 * @param stazioneAppaltante 
	 * 		se <>NULL, in caso di protocollazione con WSDM, verifica se "stazioneAppaltante" 
	 * 		corrisponde alla stazione appaltante protocollante    
	 */
	public Integer getTipoProtocollazione(String stazioneAppaltante);
	
	/**
	 * restituisce il tipo protocollazione attivo
	 */
	public Integer getTipoProtocollazione() throws Exception;
		
	/**
	 * indica se la configurazione WSDM associata alla stazione appaltante 
	 * e' presente o meno 
	 */
	public boolean isConfigStazioneAppaltantePresente();

	/**
	 * indica se non esite una configurazione di default o se e' stata selezionata una configurazione di default vuota 
	 * per la protocollazione con  WSDM
	 */	
	public boolean isConfigWSDMNonDisponibile();

	/**
	 * ritorna le categorie dei parametri da usare per i filtri
	 * @throws ApsSystemException 
	 */
	public List<String> retrieveCategories() throws ApsSystemException;
	
	/**
	 * ritorna le autenticazioni abilitate per l'accesso al portale.
	 * @throws ApsSystemException 
	 */
	public List<String> loadEnabledAuthentications() throws ApsSystemException;
	
	/**
	 * ritorna le autenticazioni abilitate per l'accesso al portale.
	 * @throws ApsSystemException 
	 */
	public List<AppParam> loadEnabledAuthenticationsPositions(List<String> authentications) throws ApsSystemException;
	
}
