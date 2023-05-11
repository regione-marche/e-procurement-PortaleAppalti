package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Bean rappresentante un evento.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.2
 */
public class Event implements Serializable {

	/**
	 * Enumerato per i livelli di tracciatura di un evento.
	 * <ul>
	 * <li>1 = INFO</li>
	 * <li>2 = WARNING</li>
	 * <li>3 = ERROR</li>
	 * </ul>
	 * 
	 * @author Stefano.Sabbadin
	 */
	public enum Level {
		INFO(1), WARNING(2), ERROR(3);
		Level(int level) {
			this.level = level;
		}

		private int level;

		public int getLevel() {
			return this.level;
		}
	}

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -7404511895553730986L;

	/** Id della tracciatura.*/
	private Long id;
	/** Data della tracciatura */
	private Date eventDate;
	/** Livello di tracciatura. */
	private Level level;
	/** Utente loggato che causa la tracciatura. */
	private String username;
	/** Identificativo oggetto di destinazione, in base all'evento. */
	private String destination;
	/** Tipo di evento tracciato. */
	private String eventType;
	/** Messaggio evento. */
	private String message;
	/** Dettaglio del messaggio. */
	private String detailMessage;
	/** IP del richiedente. */
	private String ipAddress;
	/** ID di sessione dell'utente connesso. */
	private String sessionId;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param level
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	
	/**
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
		if (StringUtils.length(this.message) > 500) {
			this.message = StringUtils.substring(this.message, 0, 500);
		}
	}
	
	/**
	 * @return the html detailMessage
	 */
	public String getHtmlDetailMessage() {
		//String msg = StringEscapeUtils.escapeHtml(detailMessage);
		String msg = detailMessage
			.replaceAll("\0", "#0")
			.replaceAll("\1", "#1")
			.replaceAll("\2", "#2")
			.replaceAll("\3", "#3")
			.replaceAll("\4", "#4")
			.replaceAll("\5", "#5")
			.replaceAll("\6", "#6")
			.replaceAll("\7", "#7");
		return msg;
	}

	/**
	 * @return the detailMessage
	 */
	public String getDetailMessage() {
		return detailMessage;
	}

	/**
	 * @param detailMessage
	 *            the detailMessage to set
	 */
	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
		if (StringUtils.length(this.detailMessage) > 4000) {
			this.detailMessage = StringUtils.substring(this.detailMessage, 0, 4000);
		}
	}
	
	/**
	 * Imposta come messaggio di dettaglio lo stack dell'eccezione in input.
	 * 
	 * @param t
	 *            eccezione catturata e da inserire nel messaggio di dettaglio
	 */
	public void setDetailMessage(Throwable t) {
		StringBuilder cause = new StringBuilder("");
		Throwable innerException = t.getCause();
		if (innerException != null) {
			// se esistono eccezioni annidate, ne traccio classe e messaggio per
			// aumentare la comprensione del problema
			int nestedLevel = 1;
			while (innerException != null) {
				if (nestedLevel == 1) {
					cause.append("[...]\nCaused by ");
				} else {
					cause.append("\nnested exception is ");
				}
				cause.append(innerException.getClass().getName()).append(" : ")
						.append(innerException.getMessage());
				nestedLevel++;
				innerException = innerException.getCause();
			}
		}
		// si traccia parte dell'eccezione catturata, ed in coda si inseriscono
		// la tracciatura generata per le eccezioni annidate
		StringWriter errors = new StringWriter();
		t.printStackTrace(new PrintWriter(errors));
		// si controlla di non superare i 4000 caratteri in modo da non causare
		// problemi in fase di inserimento a db del messaggio
		int length = 4000 - cause.length();
		String detail = null;
		do {
			detail = StringUtils.substring(errors.toString(), 0, length--)
					+ cause;
		} while (detail.getBytes().length > 4000);
		this.detailMessage = detail;
	}
	
	/**
	 * Resetta il contenuto del messaggio di dettaglio, in modo da riusare
	 * l'evento senza riportarlo nuovamente.
	 */
	public void resetDetailMessage() {
		this.detailMessage = null;
	}
	
	/**
	 * Imposta l'evento in stato di errore in seguito alla cattura di un'eccezione.
	 * 
	 * @param t
	 *            eccezione catturata e da inserire nel messaggio di dettaglio
	 */
	public void setError(Throwable t) {
		this.level = Event.Level.ERROR;
		this.setDetailMessage(t);
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionID the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
