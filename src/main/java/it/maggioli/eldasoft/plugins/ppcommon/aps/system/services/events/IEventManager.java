package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events;

import java.util.Date;
import java.util.List;

/**
 * Interfaccia base per la gestione degli eventi.<br/>
 * Si preferisce una implementazione specifica piuttosto che l'utilizzo di un
 * appender log4j da reindirizzare a DB perch&egrave; in questo modo risulta
 * pi&ugrave; semplice dividere alcune informazioni chiave per eventuali
 * estrazioni di dati dalla tabella definita piuttosto di lavorare su
 * sottostringhe del messaggio.
 * 
 * @author Stefano.Sabbadin
 * @version 1.11.2
 */
public interface IEventManager {
	
	/**
	 * Inserisce un evento.
	 * 
	 * @param event
	 *            evento da inserire
	 */
	void insertEvent(Event event);
	
	/**
	 * Numero di eventi trovati a db in base ai filtri di ricerca impostati
	 * 
	 * @param dateFrom 
	 * 			data da cui far partire la ricerca
	 * @param dateTo
	 * 			data fino a cui effettuare la ricerca
	 * @param username
	 * 			username per cui filtrare la ricerca
	 * @param destination
	 * 			destinazione per cui filtrare la ricerca
	 * @param type
	 * 			tipo per cui filtrare la ricerca
	 * @param level
	 * 			livello per cui filtrare la ricerca
	 * @param message
	 * 			messaggio per cui filtrare la ricerca
	 * 
	 * @return int numero di eventi trovati a db in base ai filtri di ricerca impostati
	 */
	int countEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message);
	
	/**
	 * Ricerca paginata degli eventi presenti a db.
	 * 
	 * @param dateFrom 
	 * 			data da cui far partire la ricerca
	 * @param dateTo
	 * 			data fino a cui effettuare la ricerca
	 * @param username
	 * 			username per cui filtrare la ricerca
	 * @param destination
	 * 			destinazione per cui filtrare la ricerca
	 * @param type
	 * 			tipo per cui filtrare la ricerca
	 * @param level
	 * 			livello per cui filtrare la ricerca
	 * @param message
	 * 			messaggio per cui filtrare la ricerca
	 * @param startRow
	 * 			riga iniziale
	 * @param pageSize
	 * 			dimensione della pagina
	 * 
	 * @return List<Event> lista degli eventi per la pagina selezionata.
	 */
	List<Event> searchEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, final int startRow, final int pageSize);
	
	
	/**
	 * Estrae il dettaglio di un evento.

	 * @param eventId
	 * 			id evento
	 * 
	 * @return dettaglio evento
	 */
	Event getEvent(Long eventId);
}
