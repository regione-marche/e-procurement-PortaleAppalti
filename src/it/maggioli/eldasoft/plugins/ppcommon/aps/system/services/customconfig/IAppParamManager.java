package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.WSDMWrapper;

import java.util.List;

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
	
	/**
	 * Imposta la stazione appaltante per la configurazione multi WSDM
	 * Da richiamare in fase iniziale di una protocollazione 
	 * e nella fase conclusiva di una protocollazione
	 */
	public void setStazioneAppaltanteProtocollazione(String stazioneAppaltante);

	/**
	 * indica se la configurazione WSDM associata alla stazione appaltante 
	 * e' presente o meno 
	 */
	public boolean isConfigStazioneAppaltantePresente();
	
}
