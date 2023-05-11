package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia base per Data Access Object delle configurazioni della verticalizzazione.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public interface IAppParamDAO {

    /**
     * Carica e restituisce la lista di tutte le configurazioni dell'applicativo.
     * 
     * @return lista delle configurazioni applicative
     */
    public List<AppParam> loadAppParams();

    /**
     * Aggiorna le configurazioni.
     * 
     * @param configs lista delle configurazioni da aggiornare 
     */
    public void updateAppParams(List<AppParam> configs);

    /**
     * Aggiorna le configurazioni in input con i valori di default.
     * 
     * @param names elenco delle configurazioni da ripristinare al valore di default
     */
    public void updateDefaultAppParams(String[] names);

    
	/**
	 * ritorna le categorie dei parametri da usare per i filtri
	 * @throws ApsSystemException 
	 */
	public List<String> retrieveCategories();

	/**
	 * Legge le configurazioni definite nel DB filtrate per categoria.
	 * 
	 * @param category
	 * 
	 * @return elenco delle configurazioni definite
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	public List<AppParam> loadAppParamsByCategoory(String category);
	
	/**
	 * ritorna le autenticazioni abilitate per l'accesso al portale.
	 * @throws ApsSystemException 
	 */
	public List<String> loadEnabledAuthentications();

	/**
	 * ritorna le autenticazioni abilitate per l'accesso al portale.
	 * @throws ApsSystemException 
	 */
	public List<AppParam> loadEnabledAuthenticationsPositions(List<String> authentications);
	
}
