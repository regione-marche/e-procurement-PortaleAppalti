package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;

/**
 * Data Access Object per le configurazioni alla verticalizzazione.
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class AppParamDAO extends AbstractDAO implements IAppParamDAO {

	private final String ALL_CUSTOM_CONFIGS = "SELECT name, type, description, value FROM ppcommon_properties order by ordprog asc";
	private final String UPDATE_CONFIG = "UPDATE ppcommon_properties SET value = ? WHERE name = ?";
	private final String UPDATE_DEFAULT_CONFIG = "UPDATE ppcommon_properties SET value = defvalue WHERE name = ?";

	@Override
	public List<AppParam> loadAppParams() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		List<AppParam> customConfigs = new ArrayList<AppParam>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_CUSTOM_CONFIGS);
			while (res.next()) {
				AppParam customConfig = this.getCustomConfigFromResultSet(res);
				customConfigs.add(customConfig);
			}
		} catch (Throwable t) {
			processDaoException(t,
					"Errore durante la lettura di ppcommon_properties",
					"loadAppParams");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return customConfigs;
	}

	/**
	 * Costruisce e restituisce una configurazione leggendo una riga di
	 * recordset.
	 * 
	 * @param res
	 *            Il resultset da leggere.
	 * @return La customizzazione generata.
	 * @throws ApsSystemException
	 *             In caso di errore
	 */
	private AppParam getCustomConfigFromResultSet(ResultSet res)
			throws ApsSystemException {
		AppParam customConfig = new AppParam();
		String name = null;
		try {
			name = res.getString(1);
			String type = res.getString(2);
			String description = res.getString(3);
			String value = res.getString(4);

			customConfig.setName(name);
			customConfig.setType(type);
			customConfig.setDescription(description);
			customConfig.setValue(value);
		} catch (Throwable t) {
			throw new ApsSystemException(
					"Errore durante l'interpretazione in ppcommon_properties del record con id "
							+ name, t);
		}

		return customConfig;
	}

	@Override
	public void updateAppParams(List<AppParam> configs) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_CONFIG);
			for (AppParam config : configs) {
				stat.setString(1, config.getValue());
				stat.setString(2, config.getName());
				stat.executeUpdate();
			}
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(
					t,
					"Error rilevato durante l'aggiornamento di un record in ppcommon_properties",
					"updateAppParams");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void updateDefaultAppParams(String[] names) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_DEFAULT_CONFIG);
			for (String name : names) {
				stat.setString(1, name);
				stat.executeUpdate();
			}
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(
					t,
					"Error rilevato durante l'aggiornamento di un record in ppcommon_properties",
					"updateDefaultAppParams");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

}
