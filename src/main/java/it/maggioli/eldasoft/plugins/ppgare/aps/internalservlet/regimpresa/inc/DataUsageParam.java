package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.itextpdf.awt.geom.misc.Messages;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

public class DataUsageParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8229771707392055454L;
	
	private String urlPiattaforma;
	
	private II18nManager ii18nManager;
	private IAppParamManager appParamManager;
	private String language;
	
	public void setFieldsForMultiLang(II18nManager ii18nManager, IAppParamManager appParamManager, String language) throws ApsSystemException {
		this.ii18nManager = ii18nManager;
		this.appParamManager = appParamManager;
		this.language = language;
	}
	
	public String getDataUsages() throws ApsSystemException {
		return Messages.format(ii18nManager.getLabel("LABEL_DATA_USAGES", language), new Object[] { urlPiattaforma });
	}
	public String getTreatment() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_TREATMENT", 
				AppParamManager.NOME_TITOLARE, 
				AppParamManager.SEDE_TITOLARE, 
				AppParamManager.MAIL_TITOLARE);
	}
	public String getDpoEmail() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_DPO", 
				AppParamManager.MAIL_DPO);
	}
	public String getHandler() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_HANDLER", 
				AppParamManager.NOME_GESTORE, 
				AppParamManager.SEDE_GESTORE, 
				AppParamManager.MAIL_GESTORE);
	}
	public String getNavigation() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_NAVIGATION", 
				AppParamManager.DURATA_LOG_NAVIGAZIONE);
	}
	public String getPreservation() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_PRESERVATION", 
				AppParamManager.DURATA_LOG_NAVIGAZIONE, 
				AppParamManager.DURATA_CONSERVAZIONE_CONTATTI_MAIL, 
				AppParamManager.DURATA_DATI_ELENCO,  
				AppParamManager.DURATA_DATI_GARE, 
				AppParamManager.DURATA_DATI_NON_TRASMESSI);
	}
	public String getRights() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_RIGHTS", 
				AppParamManager.MAIL_TITOLARE,
				AppParamManager.SEDE_TITOLARE);
	}
	public String getUpdates() throws ApsSystemException {
		return getLabelWithParams("LABEL_DATA_USAGES_UPDATES", 
				AppParamManager.DATA_INIZIO_VALIDITA_POLICY);
	}
	
	public String getMailDPO() {
		return (String) appParamManager.getConfigurationValue(AppParamManager.MAIL_DPO);
	}
	public String getNomeGestore() {
		return (String) appParamManager.getConfigurationValue(AppParamManager.NOME_GESTORE);
	}
	private String getLabelWithParams(String lebelKey, String ...paramsKey) throws ApsSystemException {
		List<String> params = new ArrayList<String>(paramsKey.length);
		for (String paramKey : paramsKey) {
			String value = (String) appParamManager.getConfigurationValue(paramKey);
			params.add(value);
		}
		return Messages.format(ii18nManager.getLabel(lebelKey, language), params.toArray() );
	}
	
	public String getUrlPiattaforma() {
		return urlPiattaforma;
	}
	public void setUrlPiattaforma(String urlPiattaforma) {
		this.urlPiattaforma = urlPiattaforma;
	}
	
}
