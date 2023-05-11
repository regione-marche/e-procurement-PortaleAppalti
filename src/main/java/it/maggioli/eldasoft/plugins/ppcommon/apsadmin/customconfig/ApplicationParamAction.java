package it.maggioli.eldasoft.plugins.ppcommon.apsadmin.customconfig;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.util.ArrayList;
import java.util.List;

public class ApplicationParamAction extends BaseAction {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6966434358415565624L;

	/** Manager per la gestione delle configurazioni dell'applicativo. */
	private IAppParamManager appParamManager;

	/** Elenco delle configurazioni previste dalla verticalizzazione. */
	private List<AppParam> appParams;

	/* dati popolati in fase di salvataggio/reset e ricevuti dal form. */
	@Validate(EParamValidation.GENERIC)
	private String[] reset;
	@Validate(EParamValidation.GENERIC)
	private String[] name;
	@Validate(EParamValidation.GENERIC)
	private String[] value;
	private List<String> categories;
	@Validate(EParamValidation.GENERIC)
	private String category;
	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @return the appParams
	 */
	public List<AppParam> getAppParams() {
		return appParams;
	}

	/**
	 * @return the reset
	 */
	public String[] getReset() {
		return reset;
	}

	/**
	 * @param reset
	 *            the reset to set
	 */
	public void setReset(String[] reset) {
		this.reset = reset;
	}

	/**
	 * @return the name
	 */
	public String[] getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String[] name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String[] getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String[] value) {
		this.value = value;
	}


	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	/** Apre la pagina in edit. */
	public String edit() {
		try {
			this.appParams = this.appParamManager.getAppParams();
			this.categories = this.appParamManager.retrieveCategories();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/** Salva e riapre la pagina in edit. */
	public String save() {
		List<AppParam> dati = new ArrayList<AppParam>(); 
		for (int i = 0; i < this.name.length; i++) {
			AppParam cfg = new AppParam();
			cfg.setName(this.name[i]);
			cfg.setValue(this.value[i]);
			dati.add(cfg);
		}
		try {
			this.appParamManager.updateAppParams(dati);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String filtra() {
		
		try {
			if(this.category == null || "".equals(this.category)){
				this.appParams = this.appParamManager.getAppParams();
			} else {
				this.appParams = this.appParamManager.getAppParamsByCategory(this.getCategory());
			}
			this.categories = this.appParamManager.retrieveCategories();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "filtra");
			return FAILURE;
		}
		return INPUT;
	}
	
	
	/** Salva e riapre la pagina in edit. */
	public String setDefault() {
		if (this.reset == null) {
			this.addActionError(this.getText("ppcommon.systemParams.noElementSelected"));
			return "noCheck";
		} else {
			try {
				this.appParamManager.updateDefaultAppParams(this.reset);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "setDefault");
				return FAILURE;
			}
		}
		return SUCCESS;
	}

}
