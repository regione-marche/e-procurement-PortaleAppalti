package it.maggioli.eldasoft.plugins.ppcommon.apsadmin.customconfig;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomReport;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomReportManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Action di gestione della visualizzazione e aggiornamento delle configurazioni
 * di PDF specifici da generare.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.1
 */
public class CustomPdfAction extends BaseAction {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 2792235328155868941L;

	/**
	 * Manager per la gestione delle personalizzazioni/properties di
	 * configurazione.
	 */
	private IAppParamManager appParamManager;

	/**
	 * Manager per la gestione delle customizzazioni dei report PDF da generare.
	 */
	private ICustomReportManager customReportManager;

	/**
	 * Folder in cui contenere le personalizzazioni dei PDF per il cliente in
	 * uso.
	 */
	@Validate(EParamValidation.GENERIC)
	private String folder;

	/** Report custom definiti per il cliente. */
	private List<CustomReport> customReports;

	/**
	 * Campo per l'inserimento codice entit&agrave; di una nuova
	 * personalizzazione.
	 */
	@Validate(EParamValidation.GENERIC)
	private String entityCode;

	/** Campo per l'inserimento dell'id report di una nuova personalizzazione. */
	@Validate(EParamValidation.GENERIC)
	private String idReport;

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @param customReportManager
	 *            the customReportManager to set
	 */
	public void setCustomReportManager(ICustomReportManager customReportManager) {
		this.customReportManager = customReportManager;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @return the customReports
	 */
	public List<CustomReport> getCustomReports() {
		return customReports;
	}

	public LinkedHashMap<Integer, String> getOperations() {
		LinkedHashMap<Integer, String> lista = new LinkedHashMap<Integer, String>();
		lista.put(1, "ppcommon.pdf.operation.1");
		lista.put(2, "ppcommon.pdf.operation.2");
		lista.put(3, "ppcommon.pdf.operation.3");
		return lista;
	}

	/**
	 * @param customReports
	 *            the customReports to set
	 */
	public void setCustomReports(List<CustomReport> customReports) {
		this.customReports = customReports;
	}

	/**
	 * @return the entityCode
	 */
	public String getEntityCode() {
		return entityCode;
	}

	/**
	 * @param entityCode
	 *            the entityCode to set
	 */
	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	/**
	 * @return the idReport
	 */
	public String getIdReport() {
		return idReport;
	}

	/**
	 * @param idReport
	 *            the idReport to set
	 */
	public void setIdReport(String idReport) {
		this.idReport = idReport;
	}

	/** Apre la pagina con la lista report. */
	@SkipValidation
	public String list() {
		this.folder = StringUtils.stripToNull((String) this.appParamManager
				.getConfigurationValue(AppParamManager.SUBFOLDER_TEMPLATE_JASPER));

		if (this.folder != null) {
			// si estraggono le configurazioni definite per tale cliente
			try {
				this.customReports = this.customReportManager
						.getActiveReports(this.folder);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "list");
				return FAILURE;
			}
		} else {
			this.addActionError(this.getText("ppcommon.pdf.emptyFolder"));
		}
		return SUCCESS;
	}

	@SkipValidation
	public String newCustomPdf() {
		this.folder = StringUtils.stripToNull((String) this.appParamManager
				.getConfigurationValue(AppParamManager.SUBFOLDER_TEMPLATE_JASPER));

		if (this.folder != null) {
			// si estraggono le configurazioni definite per tale cliente
			try {
				this.customReports = this.customReportManager
						.getCustomReports(this.folder);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "newCustomPdf");
				return FAILURE;
			}
		}
		return SUCCESS;
	}

	public String saveCustomPdf() {
		try {
			this.customReportManager.insertActiveReport(this.entityCode,
					Integer.parseInt(this.idReport));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveCustomPdf");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@SkipValidation
	public String trashCustomPdf() {
		return SUCCESS;
	}

	@SkipValidation
	public String deleteCustomPdf() {
		try {
			this.customReportManager.deleteActiveReport(this.entityCode,
					Integer.parseInt(this.idReport));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteCustomPdf");
			return FAILURE;
		}
		return SUCCESS;
	}
	
}
