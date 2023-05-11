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
 * Data Access Object per le customizzazioni dei clienti
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class CustomConfigDAO extends AbstractDAO implements ICustomConfigDAO {

	private final String ALL_CUSTOM_CONFIGS = "SELECT objectid, attrib, feature, configvalue FROM ppcommon_customizations ORDER BY objectid, attrib";
	private final String UPDATE_CONFIG = "UPDATE ppcommon_customizations SET configvalue = ? WHERE objectid = ? AND attrib = ? AND feature = ?";

	@Override
	public List<CustomConfig> loadCustomConfigs() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		List<CustomConfig> customConfigs = new ArrayList<CustomConfig>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_CUSTOM_CONFIGS);
			while (res.next()) {
				CustomConfig customConfig = this
						.getCustomConfigFromResultSet(res);
				customConfigs.add(customConfig);
			}
		} catch (Throwable t) {
			processDaoException(t,
					"Errore durante la lettura di ppcommon_customizations",
					"loadCustomConfigs");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return customConfigs;
	}

	/**
	 * Costruisce e restituisce una personalizzazione leggendo una riga di
	 * recordset.
	 * 
	 * @param res
	 *            Il resultset da leggere.
	 * @return La customizzazione generata.
	 * @throws ApsSystemException
	 *             In caso di errore
	 */
	private CustomConfig getCustomConfigFromResultSet(ResultSet res)
			throws ApsSystemException {
		CustomConfig customConfig = new CustomConfig();
		String objectid = null;
		String attrib = null;
		String feature = null;
		try {
			objectid = res.getString(1);
			attrib = res.getString(2);
			feature = res.getString(3);

			customConfig.setObjectId(objectid);
			customConfig.setAttrib(attrib);
			customConfig.setFeature(feature);
			customConfig.setConfigValue(res.getBoolean(4));
		} catch (Throwable t) {
			throw new ApsSystemException(
					"Errore durante l'interpretazione in ppcommon_customization del record ('"
							+ objectid + "','" + attrib + "','" + feature
							+ "')", t);
		}

		return customConfig;
	}

	@Override
	public void updateCustomConfigs(List<CustomConfig> configs) {
    	Connection conn = null;
		PreparedStatement stat = null;
        try {
        	conn = this.getConnection();
        	conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_CONFIG);
			for (CustomConfig config : configs) {
	    		stat.setInt(1, config.isConfigValue() ? 1 : 0);
	    		stat.setString(2, config.getObjectId());
	    		stat.setString(3, config.getAttrib());
	    		stat.setString(4, config.getFeature());
	    		stat.executeUpdate();
			}
    		conn.commit();
    	} catch (Throwable t) {
    		this.executeRollback(conn);
			processDaoException(t, "Errore rilevato durante l'aggiornamento di un record in ppcommon_customization", 
    				"updateCustomConfigs");
    	} finally {
    		closeDaoResources(null, stat, conn);
    	}
	}

}
