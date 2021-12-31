package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

//import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.struts2.interceptor.ServletResponseAware;

//import com.agiletec.aps.system.RequestContext;
import com.agiletec.apsadmin.system.BaseAction;

public class SSOLogoutResponseAction extends BaseAction { //implements ServletResponseAware{

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6400306168072330961L;

	private IAppParamManager appParamManager;

	//private HttpServletResponse _response;
	private String urlHome;

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

//	@Override
//	public void setServletResponse(HttpServletResponse response) {
//		this._response = response;
//	}

	/**
	 * @return the urlHome
	 */
	public String getUrlHome() {
		return urlHome;
	}

	/**
	 * @param urlHome the urlHome to set
	 */
	public void setUrlHome(String urlHome) {
		this.urlHome = urlHome;
	}

	public String logout(){
		String target = SUCCESS;

		HttpSession session = this.getRequest().getSession();
		//RequestContext reqCtx = new RequestContext();

		Integer protocollo = (Integer)appParamManager.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO);
		if(protocollo != null && protocollo == PortGareSystemConstants.SSO_PROTOCOLLO_SPID_MAGGIOLI) {
			// 3 = SPID Maggioli
			session.invalidate();
			target = "homepage";
			
		} else {		
			//this.getRequest().setAttribute(RequestContext.REQCTX, reqCtx);
			//reqCtx.setRequest(this.getRequest());
			//reqCtx.setResponse(this._response);
			
			String propDefURL = (String)appParamManager.getConfigurationValue("sso.logout.url");
			if (propDefURL == null) {
				LOG.fatal("Property 'sso.logout.url' non valorizzata, disconnessione impossibile dal sistema di SSO");
			} else {
				String url = propDefURL;
				this.setUrlHome(url);
				session.invalidate();
			}
		}

		return target;
	}


}
