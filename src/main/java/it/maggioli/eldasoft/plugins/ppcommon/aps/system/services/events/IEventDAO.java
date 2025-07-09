package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events;

import java.util.Date;
import java.util.List;

/**
 * Interfaccia base per Data Access Object per la tracciatura di eventi.<br/>
 * La tracciatura eventi non causa eccezioni applicative nemmeno di tipo
 * unchecked, pertanto nel caso di anomalie viene tracciato nel log il problema
 * rilevato.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.2
 */
public interface IEventDAO {

	/**
	 * Inserisce un evento.
	 * 
	 * @param event
	 *            evento da inserire
	 */
	void insertEvent(Event event);

//	/**
//	 * Carica la lista di tutti gli eventi presenti a db.
//	 * 
//	 * @param String
//	 *            condizione di filtro costruita partendo dai dati forniti tramite la maschera di input.
//	 *            
//	 * @return List<Event> lista degli eventi che ripondono alla condizione di filtro.
//	 */
//	List<Event> loadEvents(String condition);
//	
	
	int countEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, String delegate);
	
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
	 * @param delegate
	 * 			in caso di SSO e' il soggetto impresa per cui filtrare la ricerca
	 * @param startRow
	 * 			riga iniziale
	 * @param pageSize
	 * 			dimensione della pagina
	 * 
	 * @return List<Event> lista degli eventi per la pagina selezionata.
	 */
	List<Event> searchEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, String delegate, final int startRow, final int pageSize);
	
	Event getEvent(Long eventId);

//	int countAllEvents();
	
}
