package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.report;

import it.eldasoft.report.datatypes.DefinizioneReportType;
import it.eldasoft.report.datatypes.GetDefinizioneReportResponseDocument;
import it.eldasoft.report.datatypes.GetRisultatoReportResponseDocument;
import it.eldasoft.report.datatypes.RisultatoReportType;
import it.eldasoft.www.Report.ValParametroType;

import java.rmi.RemoteException;

import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Classe che realizza il servizio di business per l'interfacciamento con il web
 * service Report lato backoffice.
 * 
 * @author Stefano.Sabbadin
 */
public class ReportManager extends AbstractService implements IReportManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 5599506255708193929L;

	/** Riferimento al wrapper per il web service Report. */
	private WSReportWrapper wsReport;

	/**
	 * Imposta il web service report.
	 * 
	 * @param wsReport
	 *            web service report.
	 */
	public void setWsReport(WSReportWrapper wsReportWrapper) {
		this.wsReport = wsReportWrapper;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public DefinizioneReportType getDefinizioneReport(String codice)
			throws ApsException {
		DefinizioneReportType def = null;
		String retWS = null;
		try {
			retWS = wsReport.getProxyWSReport().getDefinizioneReport(
					codice);
			GetDefinizioneReportResponseDocument document = GetDefinizioneReportResponseDocument.Factory
					.parse(retWS);
			if (document.getGetDefinizioneReportResponse().isSetDefinizione()) {
				def = document.getGetDefinizioneReportResponse()
						.getDefinizione();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della definizione di un report: "
								+ document.getGetDefinizioneReportResponse()
										.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura della definizione di un report",
					t);
		} catch (XmlException e) {
			throw new ApsException(
					"Errore inaspettato durante l'interpretazione della definizione di un report",
					e);
		}

		return def;
	}

	@Override
	public RisultatoReportType getRisultatoReport(String codice,
			ValParametroType[] parametri, Integer pagina,
			Integer maxDimensionePagina) throws ApsException {
		RisultatoReportType ris = null;
		String retWS = null;
		try {
			retWS = wsReport.getProxyWSReport().getRisultatoReport(codice,
					parametri, pagina, maxDimensionePagina);
			GetRisultatoReportResponseDocument document = GetRisultatoReportResponseDocument.Factory
					.parse(retWS);
			if (document.getGetRisultatoReportResponse().isSetRisultatoReport()) {
				ris = document.getGetRisultatoReportResponse()
						.getRisultatoReport();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante l'estrazione di un report: "
								+ document.getGetRisultatoReportResponse()
										.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione di un report", t);
		} catch (XmlException e) {
			throw new ApsException(
					"Errore inaspettato durante l'interpretazione del risultato di un report",
					e);
		}

		return ris;
	}

	@Override
	public RisultatoReportType getRisultatoReport(String codice,
			ValParametroType[] parametri) throws ApsException {
		return getRisultatoReport(codice, parametri, null, null);
	}

}
