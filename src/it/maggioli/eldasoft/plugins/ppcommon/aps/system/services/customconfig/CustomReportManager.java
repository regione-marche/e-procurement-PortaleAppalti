package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio di gestione delle delle personalizzazioni di report jasper.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.1
 */
public class CustomReportManager extends AbstractService implements
		ICustomReportManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -7596404386021864057L;

	/** Reference al manager per la lettura delle properties. */
	private IAppParamManager appParamManager;

	/** Reference al DAO per la lettura delle personalizzazioni di report. */
	private ICustomReportDAO customReportDAO;

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @param customReportDAO
	 *            the customReportDAO to set
	 */
	public void setCustomReportDAO(ICustomReportDAO customReportDAO) {
		this.customReportDAO = customReportDAO;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public String getActiveReport(String code, int operation)
			throws ApsSystemException {
		String reportName = null;
		String jasperFolder = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.SUBFOLDER_TEMPLATE_JASPER);
		if (jasperFolder != null) {
			try {
				reportName = this.customReportDAO.getActiveReport(code,
						operation, jasperFolder);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "getActiveReport");
				throw new ApsSystemException(
						"Errore durante la ricerca di una personalizzazione report in ppcommon_activereports",
						t);
			}

		}
		return reportName;
	}

	@Override
	public List<CustomReport> getActiveReports(String jasperFolder) throws ApsSystemException {
		List<CustomReport> lista = null;
		try {
			lista = this.customReportDAO.getActiveReports(jasperFolder);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActiveReports");
			throw new ApsSystemException(
					"Errore durante la lettura delle personalizzazioni dei report in ppcommon_activereports",
					t);
		}
		return lista;
	}

	@Override
	public List<CustomReport> getCustomReports(String jasperFolder)
			throws ApsSystemException {
		List<CustomReport> lista = null;
		try {
			lista = this.customReportDAO.getCustomReports(jasperFolder);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getCustomReports");
			throw new ApsSystemException(
					"Errore durante la lettura delle personalizzazioni dei report in ppcommon_customreports",
					t);
		}
		return lista;
	}

	@Override
	public void insertActiveReport(String entityCode, int reportId)
			throws ApsSystemException {
		try {
			this.customReportDAO.insertActiveReport(entityCode, reportId);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "insertActiveReport");
			throw new ApsSystemException(
					"Errore durante l'inserimento di una personalizzazione report in ppcommon_activereports",
					t);
		}
	}

	@Override
	public void deleteActiveReport(String entityCode, int reportId)
			throws ApsSystemException {
		try {
			this.customReportDAO.deleteActiveReport(entityCode, reportId);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteActiveReport");
			throw new ApsSystemException(
					"Errore durante l'eliminazione di una personalizzazione report in ppcommon_activereports",
					t);
		}
	}

}
