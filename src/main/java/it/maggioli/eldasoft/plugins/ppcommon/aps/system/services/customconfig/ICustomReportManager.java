package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia base per i servizi di gestione delle personalizzazioni di report
 * jasper.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.1
 */
public interface ICustomReportManager {

	/**
	 * Legge un'eventuale definizione di un report custom attivo per il cliente,
	 * su una determinata entit&agrave; e tipologia di report.
	 * 
	 * @param code
	 *            chiave dell'entit&agrave; (gara, elenco, catalogo, ...)
	 * @param operation
	 *            tipologia di report
	 *            <ul>
	 *            <li>1 = iscrizione</li>
	 *            <li>2=rinnovo</li>
	 *            </ul>
	 * @return nome dell'eventuale report customizzato se esiste, null
	 *         altrimenti
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	String getActiveReport(String code, int operation)
			throws ApsSystemException;

	/**
	 * Estrae la lista di report PDF customizzati definiti ed attivi per il
	 * cliente.
	 * 
	 * @param jasperFolder
	 *            cartella jasperreport relativa al cliente
	 * @return lista dei report custom definiti e diversi rispetto a quelli
	 *         definiti come standard per il cliente
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	List<CustomReport> getActiveReports(String jasperFolder)
			throws ApsSystemException;

	/**
	 * Estrae la lista di report PDF custom usabili per il cliente.
	 * 
	 * @param jasperFolder
	 *            cartella jasperreport relativa al cliente
	 * @return lista dei report custom usabili e diversi rispetto a quelli
	 *         definiti come standard per il cliente
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	List<CustomReport> getCustomReports(String jasperFolder)
			throws ApsSystemException;

	/**
	 * Inserisce il report associato ad un'entit&agrave;.
	 * 
	 * @param entityCode
	 *            codice entit&agrave;
	 * @param reportId
	 *            id del report
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	void insertActiveReport(String entityCode, int reportId)
			throws ApsSystemException;

	/**
	 * Elimina l'associazione report con entit&agrave;.
	 * 
	 * @param entityCode
	 *            codice entit&agrave;
	 * @param reportId
	 *            id del report
	 * @throws ApsSystemException
	 *             In caso di errore nell'accesso al db.
	 */
	void deleteActiveReport(String entityCode, int reportId)
			throws ApsSystemException;
}
