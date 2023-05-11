/**
 * 
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.report;

import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.report.datatypes.DefinizioneReportType;
import it.eldasoft.report.datatypes.RisultatoReportType;
import it.eldasoft.www.Report.ValParametroType;

/**
 * Interfaccia per l'estrazione di report dal backoffice mediante web service.
 * 
 * @author Stefano.Sabbadin
 */
public interface IReportManager {

	/**
	 * Estrae la definizione di un report.
	 * 
	 * @param codice
	 *            codice del report pubblicato nel backoffice
	 * @return definizione del report
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public DefinizioneReportType getDefinizioneReport(String codice)
			throws ApsException;

	/**
	 * Produce un report gestendo anche la paginazione.
	 * 
	 * @param codice
	 *            codice del report pubblicato nel backoffice da eseguire
	 * @param parametri
	 *            elenco dei valori degli eventuali parametri
	 * @param pagina
	 *            eventuale pagina da visualizzare, nel caso di report a griglia
	 *            di dati
	 * @param maxDimensionePagina
	 *            numero massimo di record da estrarre per una pagina di
	 *            risultato, nel caso di report a griglia di dati
	 * 
	 * @return report estratto, o porzione di report estratto nel caso di report
	 *         a griglia con paginazione dei dati
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public RisultatoReportType getRisultatoReport(String codice,
			ValParametroType[] parametri, Integer pagina,
			Integer maxDimensionePagina) throws ApsException;

	/**
	 * Produce un report.
	 * 
	 * @param codice
	 *            codice del report pubblicato nel backoffice da eseguire
	 * @param parametri
	 *            elenco dei valori degli eventuali parametri
	 * @return report estratto, o porzione di report estratto nel caso di report
	 *         a griglia con paginazione dei dati
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public RisultatoReportType getRisultatoReport(String codice,
			ValParametroType[] parametri) throws ApsException;
}
