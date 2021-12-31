package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;

/**
 * Interfaccia base per Data Access Object delle personalizzazioni di report
 * jasper.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.1
 */
public interface ICustomReportDAO {

	/**
	 * Legge un'eventuale definizione di un report custom attivo per un cliente,
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
	 * @param jasperFolder
	 *            codice cartella dei report jasper personalizzati per il
	 *            cliente
	 * @return nome dell'eventuale report customizzato se esiste, null
	 *         altrimenti
	 */
	String getActiveReport(String code, int operation, String jasperFolder);

	/**
	 * Legge la lista delle personalizzazioni di report definite ed attive per
	 * il cliente.
	 * 
	 * @param jasperFolder
	 *            sottocartella che identifica i report specifici del cliente
	 * @return lista di report diversi da quelli standard definiti per il
	 *         cliente
	 */
	List<CustomReport> getActiveReports(String jasperFolder);

	/**
	 * Legge la lista delle personalizzazioni di report usabili per il cliente.
	 * 
	 * @param jasperFolder
	 *            sottocartella che identifica i report specifici del cliente
	 * @return lista di report diversi da quelli standard usabili per il cliente
	 */
	List<CustomReport> getCustomReports(String jasperFolder);

	/**
	 * Inserisce il report associato ad un'entit&agrave;.
	 * 
	 * @param entityCode
	 *            codice entit&agrave;
	 * @param reportId
	 *            id del report
	 */
	void insertActiveReport(String entityCode, int reportId);

	/**
	 * Elimina il report associato ad un'entit&agrave;.
	 * 
	 * @param entityCode
	 *            codice entit&agrave;
	 * @param reportId
	 *            id del report
	 */
	void deleteActiveReport(String entityCode, int reportId);
}
