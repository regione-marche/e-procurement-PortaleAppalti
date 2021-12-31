package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events;

import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio di gestione eventi.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.2
 */
public class EventManager extends AbstractService implements IEventManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -737904821601262980L;

	/** Reference al DAO di gestione delle operazioni su DB. */
	private IEventDAO eventDAO;

	/**
	 * @param eventDAO
	 *            the eventDAO to set
	 */
	public void setEventDAO(IEventDAO eventDAO) {
		this.eventDAO = eventDAO;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public void insertEvent(Event event) {
		this.eventDAO.insertEvent(event);
	}
	
	@Override
	public int countEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message){
		return this.eventDAO.countEvents(dateFrom, dateTo, username, destination, type, level, message);
	}
	
	@Override
	public List<Event> searchEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, final int startRow, final int pageSize){
		return this.eventDAO.searchEvents(dateFrom, dateTo,  username, destination,  type,  level, message, startRow, pageSize);
	}
	
	@Override
	public Event getEvent(Long eventId){
		return this.eventDAO.getEvent(eventId);
	}

	
//	public int countAllEvents(){
//		return this.eventDAO.countAllEvents();
//	}
}
