package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AbstractMonoInstanceResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc.DataUsageParam;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc.TermOfUseParam;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA })
public class OpenPrivacyInfoAction extends BaseAction implements SessionAware {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -4004152821580458080L;
	
	private Map<String, Object> session;
	private IAppParamManager appParamManager;

	@ValidationNotRequired
	private String resourceURL;
	@Validate(EParamValidation.DIGIT)
	private String resourceID;
	
	private DataUsageParam dataUsageParam;
	private TermOfUseParam termOfUseParam;
		
	public String openDataUsageInfo() {
		String status = SUCCESS;
		
		try {
			dataUsageParam = new DataUsageParam();
			ConfigInterface configService = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, getRequest());
			String urlPlatform = configService.getParam(SystemConstants.PAR_APPL_BASE_URL);
			dataUsageParam.setFieldsForMultiLang(getI18nManager(), appParamManager, getCurrentLang().getCode());
			dataUsageParam.setUrlPiattaforma(urlPlatform);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, getCallerMethodName());
			status = ERROR;
		}
		
		return status;
	}
	
	public String openTermOfUse() {
		String status = SUCCESS;
		
		try {
			Object showTermOfUse = appParamManager.getConfigurationValue(AppParamManager.SHOW_DEFAULT_TERM_OF_USE);
			if (showTermOfUse == null || StringUtils.equals(showTermOfUse.toString(), "1")) {
				termOfUseParam = new TermOfUseParam();
				termOfUseParam.setFieldsForMultiLang(getI18nManager(), appParamManager, getCurrentLang().getCode());
			} else {
				if ("en".equals(getCurrentLang().getCode())) {
					resourceURL = "/resources/cms/documents/EN_Regole_utilizzo_piattaforma_telematica.pdf";					
				} else if ("de".equals(getCurrentLang().getCode())) {
					resourceURL = "/resources/cms/documents/DE_Regole_utilizzo_piattaforma_telematica.pdf";					
				} else {
					resourceURL = "/resources/cms/documents/Regole_utilizzo_piattaforma_telematica.pdf";
				}
			}
			
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, getCallerMethodName());
			status = ERROR;
		}
		
		return status;
	}
	
	public String openPrivacyLinks() {
		return SUCCESS;
	}
	
	public String openResource() {
		String status = SUCCESS;

		try {
			String baseUrl = "/resources/";
			String fileName = getResourcesPath();
			resourceURL = baseUrl + fileName;
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, getCallerMethodName());
			status = ERROR;
		}
		return status;
	}

	private String getResourcesPath() throws ApsSystemException {
		final IResourceManager resourceManager
			= (IResourceManager) ApsWebApplicationUtils.getBean(JacmsSystemConstants.RESOURCE_MANAGER, getRequest());
		
		ResourceInterface loadResource = resourceManager.loadResource(resourceID);
		String folder = loadResource.getFolder();
		String fileName = ((AbstractMonoInstanceResource) loadResource).getInstance().getFileName();
		
		return folder + fileName;
	}

	private String getCallerMethodName() {
		return Thread.currentThread()
			      .getStackTrace()[2].getMethodName();
	}
	
	@Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	
	public String getResourceURL() {
		return resourceURL;
	}

	public String getResourceID() {
		return resourceID;
	}

	public DataUsageParam getDataUsageParam() {
		return dataUsageParam;
	}

	public TermOfUseParam getTermOfUseParam() {
		return termOfUseParam;
	}

}
