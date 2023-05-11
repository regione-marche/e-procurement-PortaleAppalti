package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Implementazione del servizio per l'ottenimento della data ufficiale da un
 * server NTP
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class NtpManager extends AbstractService implements INtpManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -3889750148326994459L;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	@Override
    public void init() throws Exception {
	ApsSystemUtils.getLogger().debug(
		this.getClass().getName() + ": inizializzato ");
    }

    @Override
	public Date getNtpDate() throws UnknownHostException,
			SocketTimeoutException, ApsSystemException {
		Date dataNtp = null;
		Exception exOut = null;

		String ntpServer = StringUtils
				.stripToNull((String) this.appParamManager
						.getConfigurationValue(AppParamManager.NTP_SERVER));
		Integer ntpTimeout = (Integer) this.appParamManager
				.getConfigurationValue(AppParamManager.NTP_TIMEOUT);
		if (ntpTimeout == null) {
			ntpTimeout = 5000;
		}

		if (ntpServer != null) {
			String[] ntpServers = ntpServer.split(",");
			for (int i = 0; i < ntpServers.length && dataNtp == null; i++) {
				NTPUDPClient client = new NTPUDPClient();
				// We want to timeout if a response takes longer than 10 seconds
				try {
					client.setDefaultTimeout(ntpTimeout);
					client.open();
					InetAddress hostAddr = InetAddress.getByName(ntpServers[i]
							.trim());
					TimeInfo info = client.getTime(hostAddr);
					dataNtp = new Date(info.getMessage().getReceiveTimeStamp()
							.getTime());
					// nel qual caso si riesca a calcolare la data ntp in un
					// tentativo successivo al primo, l'esito complessivo
					// dell'operazione e' positivo e pertanto l'eccezione
					// memorizzata nello step precedente viene eliminata (tanto
					// rimane tracciata nel log) in modo da non ritornarla al
					// chiamante
					exOut = null;
				} catch (SocketTimeoutException e) {
					ApsSystemUtils.getLogger().error("Scaduto il timeout di attesa per la ricezione della data ufficiale dal server " 
							+ ntpServers[i] + ": " + e.getMessage());
					exOut = e;
				} catch (UnknownHostException e) {
					ApsSystemUtils
							.logThrowable(
									e,
									this,
									"Data ufficiale non rilevata per problemi di rete durante la connessione al server "
											+ ntpServers[i]);
					exOut = e;
				} catch (Throwable e) {
					ApsSystemUtils.logThrowable(e, this,
							"Errore durante la richiesta della data/ora ufficiale al server "
									+ ntpServers[i]);
					exOut = new ApsSystemException(
							"Errore durante la richiesta della data/ora ufficiale al server "
									+ ntpServers[i], e);
				} finally {
					client.close();
				}
			}

			// nel qual caso si siano generate eccezioni durante il ciclo di
			// tentativi, l'ultima eccezione generata viene ritornata all'utente
			if (exOut != null) {
				if (exOut instanceof SocketTimeoutException) {
					throw (SocketTimeoutException) exOut;
				} else if (exOut instanceof UnknownHostException) {
					throw (UnknownHostException) exOut;
				} else {
					throw (ApsSystemException) exOut;
				}
			}

		} else {
			// SOLO PER LE DEMO, si puo' essere disconnessi dalla rete e
			// pertanto la data NTP non va richiesta ad un server NTP in rete
			ApsSystemUtils
					.getLogger()
					.debug(
							"Server NTP non configurato: si utilizza la data attuale del server");
			dataNtp = new Date();
		}

		return dataNtp;
	}

}
