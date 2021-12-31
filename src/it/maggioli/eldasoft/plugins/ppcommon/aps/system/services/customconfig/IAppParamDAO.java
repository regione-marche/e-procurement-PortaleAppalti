package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;

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
}
