package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.io.Serializable;

/**
 * Rappresentazione di una customizzazione report per un cliente.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.1
 */
public class CustomReport implements Serializable {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -8768793459726186383L;

	/** Chiave entit&agrave;. */
	private String entityCode;
	/** Id del report. */
	private int reportId;
	/** Tipo di operazione per cui viene definito il report. */
	private int reportOperation;
	/** Nome del file contenente il report Jasper. */
	private String reportName;
	/** Descrizione attribuita al report Jasper. */
	private String reportDescription;

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
	 * @return the reportId
	 */
	public int getReportId() {
		return reportId;
	}

	/**
	 * @param reportId
	 *            the reportId to set
	 */
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	/**
	 * @return the reportOperation
	 */
	public int getReportOperation() {
		return reportOperation;
	}

	/**
	 * @param reportOperation
	 *            the reportOperation to set
	 */
	public void setReportOperation(int reportOperation) {
		this.reportOperation = reportOperation;
	}

	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName
	 *            the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @return the reportDescription
	 */
	public String getReportDescription() {
		return reportDescription;
	}

	/**
	 * @param reportDescription
	 *            the reportDescription to set
	 */
	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}

}
