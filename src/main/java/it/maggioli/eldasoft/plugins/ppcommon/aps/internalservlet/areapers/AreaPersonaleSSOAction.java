package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AreaPersonaleSSOAction extends BaseAction implements SessionAware, ServletContextAware, ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -383181983769081718L;

	private IURLManager urlManager;
	private IRegistrazioneImpresaManager _registrazioneImpresaManager;
	private IUserProfileManager userProfileManager;
	private IPageManager pageManager;
	private IEventManager eventManager;

	private Map<String, Object> session;
	private ServletContext servletContext;
	
	private HttpServletResponse response;
	@Validate(EParamValidation.URL)
	private String urlRegistraNuovoSoggetto;
	private List<UserDetails> impreseAssociate;
	private List<String> ragioniSocialiImprese;
	private int impreseInAttesa;
	
	/**
	 * Valorizzato esclusivamente quando il soggetto autenticatosi con single
	 * sign on corrisponde ad un operatore economico non gestito da se stesso ma
	 * da un soggetto delegato diverso.
	 */
	private boolean soggettoBloccato;

	/**
	 * @return the impreseAssociate
	 */
	public List<UserDetails> getImpreseAssociate() {
		return impreseAssociate;
	}

	/**
	 * @param impreseAssociate the impreseAssociate to set
	 */
	public void setImpreseAssociate(List<UserDetails> impreseAssociate) {
		this.impreseAssociate = impreseAssociate;
	}

	public void setUrlManager(IURLManager urlManager){
		this.urlManager = urlManager;
	}


	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}
	
	/**
	 * @return the urlRegistraNuovoSoggetto
	 */
	public String getUrlRegistraNuovoSoggetto() {
		return urlRegistraNuovoSoggetto;
	}
	/**
	 * @param urlRegistraNuovoSoggetto the urlRegistraNuovoSoggetto to set
	 */
	public void setUrlRegistraNuovoSoggetto(String urlRegistraNuovoSoggetto) {
		this.urlRegistraNuovoSoggetto = urlRegistraNuovoSoggetto;
	}

	/**
	 * @param registrazioneImpresaManager the registrazioneImpresaManager to set
	 */
	public void setRegistrazioneImpresaManager(
			IRegistrazioneImpresaManager registrazioneImpresaManager) {
		this._registrazioneImpresaManager = registrazioneImpresaManager;
	}

	public void setUserProfileManager(IUserProfileManager userProfileManager){
		this.userProfileManager = userProfileManager;
	}

	public void setEventManager(IEventManager eventManager){
		this.eventManager = eventManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * @return the ragioniSocialiImprese
	 */
	public List<String> getRagioniSocialiImprese() {
		return ragioniSocialiImprese;
	}

	/**
	 * @param ragioniSocialiImprese the ragioniSocialiImprese to set
	 */
	public void setRagioniSocialiImprese(List<String> ragioniSocialiImprese) {
		this.ragioniSocialiImprese = ragioniSocialiImprese;
	}

	/**
	 * @return the impreseInAttesa
	 */
	public int getImpreseInAttesa() {
		return impreseInAttesa;
	}

	/**
	 * @param impreseInAttesa the impreseInAttesa to set
	 */
	public void setImpreseInAttesa(int impreseInAttesa) {
		this.impreseInAttesa = impreseInAttesa;
	}
	
	/**
	 * @return the delegateUserSoggetto
	 */
	public boolean isSoggettoBloccato() {
		return this.soggettoBloccato;
	}

	public String view(){
		String target = SUCCESS;

		AccountSSO soggettoSSO = (AccountSSO)this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);


		if(soggettoSSO != null){
			
			AreaPersonaleAction.checkAltriAccessiUtente(this.servletContext, this);

			EsitoOutType risultato = new EsitoOutType();
			risultato.setEsitoOk(true);
			risultato.setCodiceErrore(null);
			
			// verifico se il soggetto risulta censito tra le imprese ma delegato ad un soggetto diverso da se stesso
			RisultatoStringaOutType risultatoUtenteDelegato = this._registrazioneImpresaManager.getUtenteDelegatoImpresa(soggettoSSO.getLogin());
			String utenteDelegato = StringUtils.stripToNull(risultatoUtenteDelegato.getRisultato());
			if (utenteDelegato != null  && !(soggettoSSO.getLogin().equals(utenteDelegato))) {
				String key = "Errors.sso.anotherDelegateUser";
				this.addActionError(this.getText(key, new String[] {utenteDelegato}));
				Event evento = new Event();
				evento.setLevel(Level.ERROR);
				evento.setEventType(PortGareEventsConstants.LOGIN_AS);
				evento.setUsername(soggettoSSO.getLogin());
				evento.setIpAddress(soggettoSSO.getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(this.getTextFromDefaultLocale(key, utenteDelegato));
				eventManager.insertEvent(evento);
				this.soggettoBloccato = true;
			} else {
				/* --- Recupero lista delle imprese associate a questo soggetto fisico (aggiornamento lista dopo fine wizard registrazione nuova impresa) --- */
				this.impreseAssociate = new ArrayList<UserDetails>();
				this.ragioniSocialiImprese = new ArrayList<String>();

				impreseAssociate.addAll(this._registrazioneImpresaManager.getImpreseAssociate(soggettoSSO.getLogin()));

				List<UserDetails> impreseAttive = new ArrayList<UserDetails>();

				for(int i = 0; i < this.impreseAssociate.size();i++){
					if(!this.impreseAssociate.get(i).isDisabled()){
						impreseAttive.add(this.impreseAssociate.get(i));
					}else{
						impreseInAttesa++;
					}
				}

				impreseAssociate.clear();
				impreseAssociate.addAll(impreseAttive);

				for(int i = 0; i < this.impreseAssociate.size(); i++){
					IUserProfile profiloImpresa;
					try {
						profiloImpresa = userProfileManager.getProfile(this.impreseAssociate.get(i).getUsername());
						this.ragioniSocialiImprese.add((String) profiloImpresa.getValue("Nome"));
					} catch (ApsSystemException e) {
						ApsSystemUtils.logThrowable(e, this, "view");
						ExceptionUtils.manageExceptionError(e, this);
						this.addActionError(this.getText("Errors.profile.notFound", new String[] {this.impreseAssociate.get(i).getUsername()}));
						this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
						target = INPUT;
					}
				}

				/* --- costruzione url per lanciare wizard registrazione impresa --- */
				RequestContext reqCtx = (RequestContext) this.getRequest().getAttribute(RequestContext.REQCTX);
				String url = (this.urlManager.createURL(reqCtx)).getURL();
				this.setUrlRegistraNuovoSoggetto(url + "?actionPath=/ExtStr2/do/FrontEnd/RegistrImpr/initSSO.action&currentFrame=7");
			}
		}

		return target;
	}
	
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest(); 
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		
		PageURL url = this.urlManager.createURL(reqCtx);
	
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}

}
