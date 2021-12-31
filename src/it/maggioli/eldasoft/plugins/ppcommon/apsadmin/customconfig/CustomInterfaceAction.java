package it.maggioli.eldasoft.plugins.ppcommon.apsadmin.customconfig;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfig;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di gestione della visualizzazione e aggiornamento delle configurazioni
 * di interfaccia dell'applicativo.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public class CustomInterfaceAction extends BaseAction {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -7837120627157546083L;

	/** Manager per la gestione delle customizzazioni dell'interfaccia. */
	private ICustomConfigManager customConfigManager;

	/** Lista delle configurazioni gestite. */
	private List<CustomConfig> customConfigs;
	
	/* dati popolati in fase di salvataggio e ricevuti dal form. */
	private String[] objectId;
	private String[] attrib;
	private String[] feature;
	private Integer[] configValue;

	/**
	 * @param customConfigManager
	 *            the customConfigManager to set
	 */
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	/**
	 * @return the customConfigs
	 */
	public List<CustomConfig> getCustomConfigs() {
		return customConfigs;
	}
	
	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(String[] objectId) {
		this.objectId = objectId;
	}

	/**
	 * @param attrib the attrib to set
	 */
	public void setAttrib(String[] attrib) {
		this.attrib = attrib;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(String[] feature) {
		this.feature = feature;
	}

	/**
	 * @param configValue the configValue to set
	 */
	public void setConfigValue(Integer[] configValue) {
		this.configValue = configValue;
	}

	/** Apre la pagina in edit. */
	public String edit() {
		try {
			this.customConfigs = this.customConfigManager.getCustomConfigs();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}

	/** Salva e riapre la pagina in edit. */
	public String save() {
		List<CustomConfig> dati = new ArrayList<CustomConfig>(); 
		for (int i = 0; i < this.objectId.length; i++) {
			CustomConfig cfg = new CustomConfig();
			cfg.setObjectId(this.objectId[i]);
			cfg.setAttrib(this.attrib[i]);
			cfg.setFeature(this.feature[i]);
			cfg.setConfigValue(this.configValue[i] == 1 ? true : false);
			dati.add(cfg);
		}
		try {
			this.customConfigManager.updateCustomConfigs(dati);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
}
