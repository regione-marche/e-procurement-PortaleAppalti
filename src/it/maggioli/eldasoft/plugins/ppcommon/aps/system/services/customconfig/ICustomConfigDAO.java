package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;

/**
 * Interfaccia base per Data Access Object delle customizzazioni clienti
 * (CustomConfig).
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public interface ICustomConfigDAO {

    /**
     * Carica e restituisce la lista di tutte le personalizzazioni.
     * 
     * @return lista delle personalizzazioni
     */
    public List<CustomConfig> loadCustomConfigs();

    /**
     * Aggiorna le personalizzazioni.
     * 
     * @param configs lista delle personalizzazioni da aggiornare 
     */
    public void updateCustomConfigs(List<CustomConfig> configs);
}
