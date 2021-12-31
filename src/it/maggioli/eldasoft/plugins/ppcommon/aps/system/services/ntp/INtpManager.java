package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia per la gestione dell'interfacciamento con un server NTP per
 * l'ottenimento dell'ora ufficiale
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public interface INtpManager {
	
	/**
	 * Restituisce l'ora ufficiale ottenuta dal server NTP configurato nell'applicativo
	 * @throws UnknownHostException In caso di rete non raggiungibile.
	 * @throws SocketTimeoutException In caso di risposta troppo lenta.
	 * @throws ApsSystemException In caso di altri errori in fase di interrogazione del server NTP.
	 */
	public Date getNtpDate() throws UnknownHostException, SocketTimeoutException, ApsSystemException;


}
