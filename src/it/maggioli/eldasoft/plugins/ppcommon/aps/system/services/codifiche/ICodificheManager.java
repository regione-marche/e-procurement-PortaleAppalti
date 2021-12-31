package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche;

import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * Interfaccia per la gestione della cache codifiche provenienti dal backoffice.
 * 
 * @author Stefano.Sabbadin
 */
public interface ICodificheManager {

	/**
	 * Ritorna la hash delle codifiche per la lettura e memorizzazione.
	 * 
	 * @return hash delle codifiche
	 */
	public Hashtable<String, LinkedHashMap<String, String>> getCodifiche();
}
