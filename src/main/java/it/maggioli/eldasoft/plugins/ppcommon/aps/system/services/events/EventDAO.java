package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events;


import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Data Access Object per la tracciatura eventi.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.2
 */
public class EventDAO extends AbstractDAO implements IEventDAO {

	private final String EVENT_INSERT = "INSERT INTO ppcommon_events (eventlevel, username, destination, eventtype, message, detailMessage, ipaddress, sessionid, eventtime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private final String EVENT_SEARCH = "SELECT id, eventtime, eventlevel, username, destination, eventtype, message, detailmessage, ipaddress, sessionid FROM ppcommon_events where ";
	private final String EVENT_BY_ID  = "SELECT id, eventtime, eventlevel, username, destination, eventtype, message, detailmessage, ipaddress, sessionid FROM ppcommon_events where id = ? "; 
	private final String COUNT_EVENTS = "SELECT COUNT(*) FROM ppcommon_events where ";
	
	// traccia gli eventi su file di log
	private static final boolean XMLFORMAT = false;
	private final DateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	private final String CSV_SEPARATOR 	= ";";
	private final String CSV_VALUE_BEGIN	= "\"";
	private final String CSV_VALUE_END	= "\"";
	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("events");
	

	/**
	 * Inserisce un nuovo evento
	 */
	@Override
	public void insertEvent(Event event) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			// update event date with current local date...
			event.setEventDate( new java.util.Date() );
			
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(EVENT_INSERT, new String [] {"id"}); 
			stat.setInt(1, event.getLevel().getLevel());
			stat.setString(2, event.getUsername());
			stat.setString(3, event.getDestination());
			stat.setString(4, event.getEventType());
			stat.setString(5, event.getMessage());
			stat.setString(6, event.getDetailMessage());
			stat.setString(7, event.getIpAddress());
			stat.setString(8, event.getSessionId());
			stat.setTimestamp(9, new java.sql.Timestamp(event.getEventDate().getTime())); 
			stat.executeUpdate();
			ResultSet rs = stat.getGeneratedKeys();
			long id = (rs.next() ? rs.getLong(1) : -1);
			conn.commit();

			// scrivi su log separato una copia dell'evento (PORTAPPALTI-60)
			if(XMLFORMAT) {
//				// XML format
//				StringWriter value = new StringWriter();
//				JAXB.marshal(event, value);
//				this.logger.info(value.toString());
			} else {
				// CSV format
				StringBuilder value = new StringBuilder();
				value.append(CSV_VALUE_BEGIN).append(id).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append(YYYYMMDD_HHMMSS.format(event.getEventDate().getTime())).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append(event.getLevel().getLevel()).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append((event.getUsername() != null ? event.getUsername() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append((event.getDestination() != null ? event.getDestination() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append((event.getEventType() != null ? event.getEventType() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append((event.getMessage() != null ? event.getMessage() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append(event.getDetailMessage() != null ? event.getDetailMessage() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append(event.getIpAddress() != null ? event.getIpAddress() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR)
					 .append(CSV_VALUE_BEGIN).append(event.getSessionId() != null ? event.getSessionId() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR);
				this.logger.info(value);
			}  
		} catch (Throwable t) {
			this.executeRollback(conn);
			ApsSystemUtils.logThrowable(t, this, "insertEvent",
					"Errore rilevato durante l'inserimento del record in ppcommon_events ("
							+ "eventlevel=" + event.getLevel().getLevel()
							+ ",username=" + event.getUsername()
							+ ",destination=" + event.getDestination()
							+ ",eventtype=" + event.getEventType()
							+ ",message=" + event.getMessage() 
							+ ",detailMessage=" + event.getDetailMessage()
							+ ",ipAddress=" + event.getIpAddress() 
							+ ",sessionId=" + event.getSessionId() + ")");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	/**
	 * Restituisce il numero di eventi relativo ai filtri impostati
	 */
	public int countEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message){
		
		StringBuilder s = new StringBuilder(COUNT_EVENTS);
		ArrayList<Object> listaParametri = new ArrayList<Object>();
		int numRis = -1;
		buildWhereCondition(dateFrom, dateTo, username, destination, type, level, message, listaParametri, s);

		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			numRis = (Integer) jt.query(s.toString(),
            (Object[])listaParametri.toArray(),
            new ResultSetExtractor() {
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                	rs.next();
                	return rs.getInt(1);
                }
			});
		} catch (Throwable t) {
			processDaoException(t, "Error while searching events", "searchEvents");
		}

		return numRis;
	}
	
	/**
	 * inserisce un evento in ppcommon_events
	 */
	class EventRowMapper implements ParameterizedRowMapper<Event> {
		@Override
		public Event mapRow(ResultSet rs, int i) throws SQLException {
			Event event = new Event();
		
			event.setId(rs.getLong(1));
			event.setEventDate(rs.getTimestamp(2));
			
			String level = rs.getBigDecimal(3).toString(); 
			if("1".equals(level)){
				event.setLevel(Event.Level.INFO);
			}else if("2".equals(level)){
				event.setLevel(Event.Level.WARNING);
			}else{
				event.setLevel(Event.Level.ERROR);
			}
			event.setUsername(rs.getString(4));
			event.setDestination(rs.getString(5));
			event.setEventType(rs.getString(6));
			event.setMessage(rs.getString(7));
			if(StringUtils.isEmpty(rs.getString(8))){
				event.setDetailMessage("");
			}else{
				event.setDetailMessage(rs.getString(8));
			}
			event.setIpAddress(rs.getString(9));
			event.setSessionId(rs.getString(10));
			return event;
		}
		
	}
	
	/**
	 * Ricerca paginata degli eventi presenti a db.
	 * @param String condizioni di filtro impostate tramite maschera di input
	 * @return La lista completa di eventi (oggetti Event)
	 */
	public List<Event> searchEvents(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, final int startRow, final int pageSize) {
		StringBuilder s = new StringBuilder(EVENT_SEARCH);
		ArrayList<Object> listaParametri = new ArrayList<Object>();
		
		buildWhereCondition(dateFrom, dateTo, username, destination, type, level, message, listaParametri, s);
		
		final List<Event> events = new ArrayList<Event>();
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			s.append(" ORDER BY id");
			jt.query(s.toString(),
            (Object[])listaParametri.toArray(),
            new ResultSetExtractor() {
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                	EventRowMapper rowMapper = new EventRowMapper();
                    int currentRow = 0;
                    while (rs.next() && currentRow < startRow + pageSize) {
                        if (currentRow >= startRow) {
                            events.add(rowMapper.mapRow(rs, currentRow));
                        }
                        currentRow++;
                    }
                    return events;
                }
			});
		} catch (Throwable t) {
			processDaoException(t, "Error while searching events", "searchEvents");
		}
		return events;
	}
	
	/**
	 * Ricerca di un evento specifico dato il suo id.
	 * @param int id dell'evento da rivercare
	 * @return evento letto da DB (oggetto di tipo Event)
	 */
	public Event getEvent(Long eventId){
		
		StringBuilder s = new StringBuilder(EVENT_BY_ID);
		Object[] listaParametri = {eventId};
	
		Event event = new Event();
		
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			event = (Event) jt.query(s.toString(),
            (Object[])listaParametri,
            new ResultSetExtractor() {
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                	EventRowMapper rowMapper = new EventRowMapper();
                		rs.next();
                        return rowMapper.mapRow(rs, 0);
                     
                }
			});
		} catch (Throwable t) {
			processDaoException(t, "Error while searching events", "searchEvents");
		}
		return event;
	
	}
	
	/**
	 * Metodo che mi permette di costruire la condizione di filtro per la ricerca in base ai parametri che passo
	 *  
	 */
	private void buildWhereCondition(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, ArrayList<Object> listaParametri, StringBuilder s){
	
		if(dateFrom != null && dateTo != null){
			s.append("eventtime >= ? and eventtime <= ? ");
			listaParametri.add(dateFrom);
			listaParametri.add(dateTo);
		}else if(dateFrom != null && dateTo == null) {
			s.append("eventtime >= ?");
			listaParametri.add(dateFrom);
		}else if (dateTo != null && dateFrom == null){
			s.append("eventtime <= ?");
			listaParametri.add(dateTo);
		}

		if(StringUtils.isNotEmpty(username)){
			if(listaParametri.isEmpty()){
				s.append("UPPER(username) like ? ");
			}else{
				s.append(" and UPPER(username) like ? ");;
			}
			listaParametri.add("%"+username.toUpperCase()+"%");
		}
		
		if(StringUtils.isNotEmpty(destination)){
			if(listaParametri.isEmpty()){
				s.append("UPPER(destination) like ? ");
			}else{
				s.append(" and UPPER(destination) like ? ");
			}
			listaParametri.add("%"+destination.toUpperCase()+"%");
		}
		
		if(StringUtils.isNotEmpty(type)){
			if(listaParametri.isEmpty()){
				s.append("eventtype = ? ");
			}else{
				s.append(" and eventtype = ? ");
			}
			listaParametri.add(type);
		}
		
		if(StringUtils.isNotEmpty(level)){
			if(listaParametri.isEmpty()){
				s.append("eventlevel = ? ");
			}else{
				s.append(" and eventlevel = ? ");
			}
			listaParametri.add(Integer.valueOf(level));
		}
		
		if(StringUtils.isNotEmpty(message)){
			if(listaParametri.isEmpty()){
				s.append("UPPER(message) like ? ");
			}else{
				s.append(" and UPPER(message) like ? ");;
			}
			listaParametri.add("%"+message.toUpperCase()+"%");
		}

		if(listaParametri.isEmpty()){
			s.delete(s.lastIndexOf("where"), s.length());
		}
	}
	
}
