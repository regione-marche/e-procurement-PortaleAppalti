package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;

/**
 * Data Access Object per le personalizzazioni di report jasper.
 * 
 * @author Stefano.Sabbadin
 * @since 1.11.1
 */
public class CustomReportDAO extends AbstractDAO implements ICustomReportDAO {

	private final String GET_ACTIVE_REPORT = "SELECT reportname FROM ppcommon_activereports a INNER JOIN ppcommon_customreports c ON a.idreport = c.id WHERE a.entitycode = ? AND c.operation = ? AND c.jasperfolder = ?";
	private final String GET_ACTIVE_REPORTS = "SELECT entitycode, idreport, operation, reportname, description FROM ppcommon_activereports a INNER JOIN ppcommon_customreports c ON a.idreport = c.id WHERE c.jasperfolder = ? order by entitycode, operation";
	private final String GET_CUSTOM_REPORTS = "SELECT id, operation, reportname, description FROM ppcommon_customreports WHERE jasperfolder = ? order by operation, description";
	private final String INSERT_ACTIVE_REPORT = "INSERT INTO ppcommon_activereports (entitycode, idreport) VALUES (?, ?)";
	private final String DELETE_ACTIVE_REPORT = "DELETE FROM ppcommon_activereports  WHERE entitycode = ? AND idreport = ?";

	@Override
	public String getActiveReport(String code, int operation,
			String jasperFolder) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		String reportName = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(GET_ACTIVE_REPORT);
			stat.setString(1, code);
			stat.setInt(2, operation);
			stat.setString(3, jasperFolder);
			res = stat.executeQuery();
			if (res.next()) {
				reportName = res.getString(1);
			}
		} catch (Throwable t) {
			processDaoException(t,
					"Errore durante la lettura di ppcommon_activereports",
					"getActiveReport");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return reportName;
	}

	@Override
	public List<CustomReport> getActiveReports(String jasperFolder) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		List<CustomReport> customConfigs = new ArrayList<CustomReport>();
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(GET_ACTIVE_REPORTS);
			stat.setString(1, jasperFolder);
			res = stat.executeQuery();
			while (res.next()) {
				CustomReport customConfig = this
						.getActiveReportsFromResultSet(res);
				customConfigs.add(customConfig);
			}
		} catch (Throwable t) {
			processDaoException(t,
					"Errore durante la lettura di ppcommon_activereports",
					"getActiveReports");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return customConfigs;
	}

	@Override
	public List<CustomReport> getCustomReports(String jasperFolder) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		List<CustomReport> customConfigs = new ArrayList<CustomReport>();
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(GET_CUSTOM_REPORTS);
			stat.setString(1, jasperFolder);
			res = stat.executeQuery();
			while (res.next()) {
				CustomReport customConfig = this
						.getCustomReportsFromResultSet(res);
				customConfigs.add(customConfig);
			}
		} catch (Throwable t) {
			processDaoException(t,
					"Errore durante la lettura di ppcommon_activereports",
					"getActiveReports");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return customConfigs;
	}

	private CustomReport getActiveReportsFromResultSet(ResultSet res)
			throws ApsSystemException {
		CustomReport customReport = new CustomReport();
		String entitycode = null;
		try {
			entitycode = res.getString(1);
			int idreport = res.getInt(2);
			int operation = res.getInt(3);
			String reportname = res.getString(4);
			String description = res.getString(5);

			customReport.setEntityCode(entitycode);
			customReport.setReportId(idreport);
			customReport.setReportOperation(operation);
			customReport.setReportName(reportname);
			customReport.setReportDescription(description);
		} catch (Throwable t) {
			throw new ApsSystemException(
					"Errore durante l'interpretazione in ppcommon_activereports del record con entitycode "
							+ entitycode, t);
		}

		return customReport;
	}

	private CustomReport getCustomReportsFromResultSet(ResultSet res)
			throws ApsSystemException {
		CustomReport customReport = new CustomReport();
		int idreport = 0;
		try {
			idreport = res.getInt(1);
			int operation = res.getInt(2);
			String reportname = res.getString(3);
			String description = res.getString(4);

			customReport.setReportId(idreport);
			customReport.setReportOperation(operation);
			customReport.setReportName(reportname);
			customReport.setReportDescription(description);
		} catch (Throwable t) {
			throw new ApsSystemException(
					"Errore durante l'interpretazione in ppcommon_customreports del record con id "
							+ idreport, t);
		}

		return customReport;
	}

	@Override
	public void insertActiveReport(String entityCode, int reportId) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(INSERT_ACTIVE_REPORT);
			stat.setString(1, entityCode);
			stat.setInt(2, reportId);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(
					t,
					"Errore durante l'inserimento in ppcommon_activereports del record con entitycode "
							+ entityCode + " e id report " + reportId,
					"insertActiveReport");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void deleteActiveReport(String entityCode, int reportId) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_ACTIVE_REPORT);
			stat.setString(1, entityCode);
			stat.setInt(2, reportId);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(
					t,
					"Errore durante l'eliminazione in ppcommon_activereports del record con entitycode "
							+ entityCode + " e id report " + reportId,
					"insertActiveReport");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
}
