package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Interfaccia comune per le action dotate della gestione di dati tipizzati
 * (codice/tipo/stato, descrizione) quali ad esempio le form di ricerca o le
 * schede
 *
 * @author Stefano.Sabbadin
 */
public interface IEncodedDataAction {

	public Map<String, LinkedHashMap<String, String>> getMaps();

	public void setTarget(String target);

	public String getTarget();
}
