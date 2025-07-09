package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Action di gestione dell'annullamento di una registrazione
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA })
public class CancelRegistrazioneAction extends BaseAction implements
	SessionAware,ServletResponseAware {

    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private Map<String, Object> session;
	private IURLManager urlManager;
	private String urlRedirect;//Ridirezione verso area personale soggetto fisico per SSO
	private HttpServletResponse _response;


    @Override
    public void setSession(Map<String, Object> session) {
	this.session = session;
    }
    
	public void setUrlManager(IURLManager urlManager){
		this.urlManager = urlManager;
	}
	
	public void setUrlRedirect(String urlRedirect){
		this.urlRedirect = urlRedirect;
	}
	
	public String getUrlRedirect() {
		return this.urlRedirect + "?actionPath=/ExtStr2/do/FrontEnd/AreaPers/viewSoggettoSSO.action&currentFrame=7";
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this._response = response;
	}

    public String questionCancelRegistrazione() {
	String target = SUCCESS;
	WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
		.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);

	if (registrazioneHelper == null) {
	    // la sessione e' scaduta, occorre riconnettersi
	    this.addActionError(this.getText("Errors.sessionExpired"));
	    target = CommonSystemConstants.PORTAL_ERROR;
	}
	return target;
    }

    public String cancelRegistrazione() {
	    String target = SUCCESS;
	    if((AccountSSO)this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO) != null){

			RequestContext reqCtx =  (RequestContext) this.getRequest().getAttribute(RequestContext.REQCTX);
			//reqCtx.setRequest(this.getRequest());
			//reqCtx.setResponse(this._response);
			PageURL url = this.urlManager.createURL(reqCtx);
			url.setPageCode("ppcommon_area_soggetto_sso");
			this.setUrlRedirect(url.getURL());
	    	target="backToSSO";
	    }
		AccountSSO sso = (AccountSSO) this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		if (sso == null || !PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(sso.getTipologiaLogin()))
			this.session
				.remove(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
		return target;
    }
}
