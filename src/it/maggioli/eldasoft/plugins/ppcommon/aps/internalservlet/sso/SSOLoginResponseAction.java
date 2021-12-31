package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers.AccessoSimultaneoBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.controller.control.Authenticator;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.DefaultApsEncrypter;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;

public class SSOLoginResponseAction extends BaseAction implements SessionAware, ServletResponseAware{

	/**
	 * UID
	 */
	private static final long serialVersionUID = 4055333962063641658L;

	private Map<String, Object> session;
	private HttpServletResponse _response;

	private IRegistrazioneImpresaManager _registrazioneImpresaManager;
	private IUserManager userManager;
	private IPageManager pageManager;
	private IURLManager urlManager;
	private IEventManager eventManager;
	private IUserProfileManager userProfileManager;

	private String urlRedirect;
	private String id; 

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this._response = response;
	}

	/**
	 * @param registrazioneImpresaManager the registrazioneImpresaManager to set
	 */
	public void setRegistrazioneImpresaManager(
			IRegistrazioneImpresaManager registrazioneImpresaManager) {
		this._registrazioneImpresaManager = registrazioneImpresaManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * @param pageManager the pageManager to set
	 */
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setUrlManager(IURLManager urlManager){
		this.urlManager = urlManager;
	}

	public void setEventManager(IEventManager eventManager){
		this.eventManager = eventManager;
	}

	public void setUserProfileManager(IUserProfileManager userProfileManager){
		this.userProfileManager = userProfileManager;
	}

	public String getUrlRedirect() {
		return this.urlRedirect ;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String login(){

		String target = SUCCESS;

		String paginaDestinazione = "ppcommon_area_soggetto_sso";

		Event evento = new Event();
		evento.setLevel(Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGIN_SSO);
		try{

			AccountSSO accountSSO = (AccountSSO) this.getRequest().getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);

			evento.setUsername(accountSSO.getLogin());
			evento.setIpAddress(accountSSO.getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			String message = "Autenticazione mediante single sign on esterna soggetto " + accountSSO.getDenominazione();
			if(accountSSO.getEmail() != null){
				message += " con email " + accountSSO.getEmail();
			}
			evento.setMessage(message);
			
			ServletContext context = this.getRequest().getSession().getServletContext();
			TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(context);
			String sessionIdUtenteConnesso = tracker.getSessionIdUtenteConnesso(accountSSO.getLogin(), this.getRequest().getSession().getId());
			
			if (sessionIdUtenteConnesso != null) {
				// esiste gia' l'utente connesso in un'altra sessione di lavoro, devo accedere ad una pagina di domanda su come procedere
				AccessoSimultaneoBean accessoSimultaneo = new AccessoSimultaneoBean();
				accessoSimultaneo.setTipoAutenticazione(AccessoSimultaneoBean.TipoAutenticazione.SINGLE_SIGN_ON);
				accessoSimultaneo.setUtenteCandidatoSingleSignOn(accountSSO);
				accessoSimultaneo.setSessionIdUtenteConnesso(sessionIdUtenteConnesso);
				session.put(AccessoSimultaneoBean.SESSION_CUNCURRENT_OBJECT_ID, accessoSimultaneo);
				evento.setLevel(Event.Level.WARNING);
				evento.setMessage("Accesso mediante single sign on esterna simultaneo da 2 postazioni");
				// si cambia la destinazione, si va alla pagina di accesso simultaneo
				paginaDestinazione = "ppcommon_accesso_simultaneo";
			} else {
				// prima connessione dell'utente
				
				/* --- Recupero lista delle imprese associate a questo soggetto fisico --- */
				List<UserDetails> impreseAssociate  = new ArrayList<UserDetails>();

				impreseAssociate.addAll(this._registrazioneImpresaManager.getImpreseAssociate(accountSSO.getLogin()));

				this.session.put(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountSSO);

				/* ----------					UNICA IMPRESA ASSOCIATA					 ---------- */
				if(isUnicaImpresaAttiva(impreseAssociate)){

					User user = getUnicaImpresaAttiva(impreseAssociate);

					this.getRequest().getSession().setAttribute("username", user.getUsername());
					this.getRequest().getSession().setAttribute("password", DefaultApsEncrypter.decrypt(user.getPassword()));

					/* --- registrazione dell'evento di login come impresa da parte del soggetto fisico --- */
					evento.setEventType(PortGareEventsConstants.LOGIN_AS);
					evento.setMessage("Accesso del soggetto " + accountSSO.getLogin() +
							" autenticato mediante single sign on come " +
							impreseAssociate.get(0).getUsername());
				}
			}
			this.urlRedirect = getPageURL(paginaDestinazione);
		}catch(Exception e){
			evento.setLevel(Level.ERROR);
			evento.setError(e);
			target = INPUT;
			ApsSystemUtils.logThrowable(e, this, "login");
		}finally{
			eventManager.insertEvent(evento);
		}

		return target;
	}

	/** Metodo che mi permette di effettuare il login "nei panni" di un operatore economico. */
	public String loginAs(){

		String target = SUCCESS;

		Event evento = new Event();
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGIN_AS);

		AccountSSO delegateUser = (AccountSSO)this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);

		try {
			evento.setIpAddress(delegateUser.getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());

			/* --- recupero dell'utenza per conto della quale ci si deve loggare --- */
			User user = (User)userManager.getUser(this.id);

			if (user != null) {
				// nel caso di autenticazione mediante single sign on si
				// demandano al sistema esterno di autenticazione la gestione
				// della scadenza dell'utente per inattivita' e della durata
				// della password
				user.setCheckCredentials(false);

				IUserProfile profiloImpresa = (IUserProfile)this.userProfileManager.getProfile(user.getUsername());

				evento.setUsername(user.getUsername());
				evento.setMessage("Accesso del soggetto " + delegateUser.getLogin() +
						" come " +
						(String) profiloImpresa.getValue("Nome"));
				session.put("username", user.getUsername());
				session.put("password", Authenticator.PASSE_PARTOUT);
				this.urlRedirect = getPageURL("ppcommon_area_personale");
			}else{
				evento.setMessage("Nessun operatore economico associato al soggetto " + delegateUser.getLogin());
				target=INPUT;
			}
		} catch (ApsSystemException e1) {
			evento.setLevel(Level.ERROR);
			evento.setError(e1);
			evento.setMessage("Autenticazione del soggetto " + delegateUser.getLogin() +
						" come operatore economico");
			target=INPUT;
		} finally{
			eventManager.insertEvent(evento);	
		}

		return target;
	}

	/**
	 * Genera la url per il redirezionamento ad una specifica pagina.
	 * @param page codice pagina
	 * @return url per la redirect alla pagina
	 */
	private String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = this.pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this._response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

	protected String getParameter(String name, RequestContext reqCtx){
		String param = reqCtx.getRequest().getParameter(name);
		return param;
	}

	private boolean isUnicaImpresaAttiva(List<UserDetails> impreseAssociate) {
		int countImpreseAttive = 0;
		for(int i = 0; i < impreseAssociate.size() && countImpreseAttive <=1;i++) {
			if(!impreseAssociate.get(i).isDisabled()){
				countImpreseAttive++;
			}
		}
		return countImpreseAttive == 1;
	}

	private User getUnicaImpresaAttiva(List<UserDetails> impreseAssociate) {
		for(int i = 0; i < impreseAssociate.size();i++){
			User impresaCorrente =(User)impreseAssociate.get(i); 
			if(!impresaCorrente.isDisabled()){
				return impresaCorrente;
			}
		}
		return null;
	}
}
