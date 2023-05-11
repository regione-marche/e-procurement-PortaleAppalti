package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.itextpdf.awt.geom.misc.Messages;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

public class TermOfUseParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6262501386331613098L;
	
	private II18nManager ii18nManager;
	private IAppParamManager appParamManager;
	private String language;
	
	public void setFieldsForMultiLang(II18nManager ii18nManager, IAppParamManager appParamManager, String language) throws ApsSystemException {
		this.ii18nManager = ii18nManager;
		this.appParamManager = appParamManager;
		this.language = language;
	}
	
	public String getTermOfUse() throws ApsSystemException {
		return getLabelWithParams("LABEL_TERM_OF_USE_TERMS", AppParamManager.NOME_PIATTAFORMA);
	}
	public String getAcronimi() throws ApsSystemException {
		return getLabelWithParams("LABEL_TERM_OF_USE_ACRONIMI", AppParamManager.NOME_PIATTAFORMA);
	}
	public String getLaw() throws ApsSystemException {
		return getLabelWithParams("LABEL_TERM_OF_USE_LAW", AppParamManager.LOCALITA_FORO);
	}
	
	private String getLabelWithParams(String lebelKey, String ...paramsKey) throws ApsSystemException {
		List<String> params = new ArrayList<String>(paramsKey.length);
		for (String paramKey : paramsKey) {
			String value = (String) appParamManager.getConfigurationValue(paramKey);
			params.add(value);
		}
		return Messages.format(ii18nManager.getLabel(lebelKey, language), params.toArray() );
	}
}
