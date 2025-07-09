package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.controller.control.Authenticator;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers.AccessoSimultaneoBean.TipoAutenticazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti.LoggedUserInfo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.bouncycastle.asn1.cms.AuthenticatedData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Effettua la segnalazione all'utente dell'accesso simultaneo ad un'altra
 * sessione di lavoro aperta in precedenza permettendo di scegliere se ottenere
 * in modo esclusivo o rinunciare all'accesso.
 * 
 * @author Stefano.Sabbadin
 * @since 1.14.7
 */
public class AccessoSimultaneoAction extends BaseAction implements SessionAware, ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6258772164285601659L;

	private Map<String, Object> session;
	private HttpServletResponse response;
	
	private IPageManager pageManager;
	private IURLManager urlManager;
	private IEventManager eventManager;
	private IUserManager userManager;
	private IAuthenticationProviderManager authenticationProvider;

	private LoggedUserInfo sessioneSimultanea;
	@ValidationNotRequired
	private String urlRedirect;
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setUrlManager(IURLManager urlManager){
		this.urlManager = urlManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public IUserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}
	
	public void setAuthenticationProvider(IAuthenticationProviderManager authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	public String getUrlRedirect() {
		return urlRedirect;
	}

	public LoggedUserInfo getSessioneSimultanea() {
		return this.sessioneSimultanea;
	}

	/**
	 * Apre la pagina per la consultazione dei dati relativi alla sessione di
	 * lavoro correntemente aperta da un altro soggetto con la medesima utenza e
	 * la domanda su come proseguire.
	 * 
	 * @return
	 */
	public String view() {
		AccessoSimultaneoBean accessoSimultaneo = (AccessoSimultaneoBean)session.get(AccessoSimultaneoBean.SESSION_CUNCURRENT_OBJECT_ID);
		String sessionIdUserLogged = accessoSimultaneo.getSessionIdUtenteConnesso();
		this.sessioneSimultanea = TrackerSessioniUtenti.getInstance(this.getRequest().getSession().getServletContext())
				.getDatiSessioniUtentiConnessi().get(sessionIdUserLogged);
		return SUCCESS;
	}
	
	/**
	 * Rinuncia all'accesso ritornando alla homepage e lasciando l'utente
	 * correntemente connesso ad utilizzare l'applicativo.
	 * 
	 * @return
	 */
	public String cancel() {
		// si rimuovono i dati inseriti in sessione per la gestione della concorrenza
		AccessoSimultaneoBean accessoSimultaneo = (AccessoSimultaneoBean)session.remove(AccessoSimultaneoBean.SESSION_CUNCURRENT_OBJECT_ID);
		String username = null;
		String ipAddress = null;
		switch (accessoSimultaneo.getTipoAutenticazione()) {
		case DB:
			UserDetails user = accessoSimultaneo.getUtenteCandidatoPortale();
			username = user.getUsername();
			ipAddress = user.getIpAddress();
			break;
		case SINGLE_SIGN_ON:
			AccountSSO accountSSO = accessoSimultaneo.getUtenteCandidatoSingleSignOn();
			username = accountSSO.getLogin();
			ipAddress = accountSSO.getIpAddress();
			break;
		}

		// generazione url di redirezionamento alla homepage
		this.urlRedirect = getPageURL("homepage");
		
		// tracciatura dell'evento di rinuncia ad accedere lasciando connesso l'utente corrente
		Event evento = new Event();
		evento.setLevel(Level.WARNING);
		evento.setEventType(PortGareEventsConstants.LOGOUT);
		evento.setUsername(username);
		evento.setIpAddress(ipAddress);
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setMessage("Rinuncia all'accesso per preservare la sessione di lavoro aperta in precedenza da un'altra postazione");
		eventManager.insertEvent(evento);

		return "redirect";
	}
	
	/**
	 * forza l'accesso ed invalida la sessione dell'utente precedentemente loggato in un'altra postazione 
	 */
	public String force() {
		String target = "redirect";
		
		// recupera e rimuovi i dati inseriti in sessione per la gestione della concorrenza
		AccessoSimultaneoBean accessoSimultaneo = (AccessoSimultaneoBean)session.remove(AccessoSimultaneoBean.SESSION_CUNCURRENT_OBJECT_ID);
		
		// si invalida la sessione attualmente attiva (su un altro browse/pc/nodo)
		ServletContext context = this.getRequest().getSession().getServletContext();
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(context);
		AccountSSO soggettoImpresa = null;
		
		// recupera i dati dell'utente attualmente connesso, che andra' invalidato...  
		String sessionIdDaInvalidare = accessoSimultaneo.getSessionIdUtenteConnesso();
		HttpSession sessionDaInvalidare = null;
		LoggedUserInfo infoDaInvalidare = null;
		if(StringUtils.isNotEmpty(sessionIdDaInvalidare)) {
			sessionDaInvalidare = tracker.getSessioniUtentiConnessi().get(sessionIdDaInvalidare);
			infoDaInvalidare = tracker.getDatiSessioniUtentiConnessi().get(sessionIdDaInvalidare); 
		}
		
		// invalida la sessione associata all'utente che attualmente ha effettuato il login...
		if (infoDaInvalidare != null) {
			try {
				// in caso di CLUSTER l'oggetto sessione non esite se risiede su un altro nodo
				if(sessionDaInvalidare != null) {
					//lastUserId = sessionDaInvalidare.getId();
					sessionDaInvalidare.invalidate();
				}
				
				// chiudi la sessione dell'utente su db...
				soggettoImpresa = AccountSSO.getFromSession();
				String username = infoDaInvalidare.getLogin();
				String delegateUser = null;
				// se esiste recupera il delegate user (per l'utente SSO)
				if(soggettoImpresa != null) {
					username = (StringUtils.isNotEmpty(infoDaInvalidare.getUsername()) ? infoDaInvalidare.getUsername() : username);
					delegateUser = (soggettoImpresa != null ? soggettoImpresa.getLogin() : null);
				}
				
				// esegui il logout dell'utente per poter forzare il nuovo login
				// ed invalida la sessione precedente
				authenticationProvider.logLogout(username, delegateUser, "*", "*");
			    if(sessionIdDaInvalidare != null) {
			    	tracker.removeSessioneUtente(sessionIdDaInvalidare);
			    }
				
				ApsSystemUtils.getLogger().debug("Forzata disconnessione per utente " + infoDaInvalidare.getLogin());
			} catch (Exception e) {
				// si sono verificati dei casi con SSO per cui rimangono i
				// riferimenti ad una sessione "morta" di un precedente accesso
				// mediante SSO: in tal caso blocca l'accesso fintantochè non si
				// ferma il tomcat e non si svuota la work!
				// la soluzione pertanto è di intercettare l'errore in fase di
				// invalidazione della sessione in quanto gia' rimossa dal
				// context applicativo e ripulire i dati della cache applicativa
				// per la gestione delle sessioni
				tracker.removeSessioneUtente(sessionIdDaInvalidare);
				ApsSystemUtils.getLogger().warn("Rimossa dalla cache applicativa la sessione " + sessionIdDaInvalidare + 
												" gia' invalidata in precedenza ma erroneamente rimasta nella cache per l'utente " + infoDaInvalidare.getLogin());
			}
		}

		// tracciatura dell'evento di disconnessione forzata per far accedere il nuovo utente connesso
		Event evento = new Event();
		evento.setLevel(Level.WARNING);
		evento.setEventType(PortGareEventsConstants.LOGIN_AS);
		evento.setSessionId(getRequest().getSession().getId());

		// segnala alla classe Authenticator che e' in corso un login forzato !!!
		session.put("forceLogin", "1");

		String username = null;
		String password = null;
		String ipAddress = null;
		switch (accessoSimultaneo.getTipoAutenticazione()) {
		case DB:
			// NB: in caso di login con SSO (SPID, Cohesion, etc.) l'autenticazione fine garantita
			// dal sistema di autenticazione remoto, perciò quando si seleziona l'impresa per
			// cui operare, il login diventa un loginAs e quindi la password associata 
			// all'account dell'impresa non e' necessaria per effettuare il login
			// si utilizza l'account dell'impresa ma con la password PASSE_PARTOUT!!!
			UserDetails user = accessoSimultaneo.getUtenteCandidatoPortale();
			username = user.getUsername();
			password = (soggettoImpresa != null ? Authenticator.PASSE_PARTOUT : user.getPassword());
			ipAddress = user.getIpAddress();

			// si predispongono i dati per l'accesso
			session.put("username", username);
			session.put("password", password);
			
			evento.setUsername(username);
			evento.setIpAddress(ipAddress);

			this.urlRedirect = getPageURL("ppcommon_area_personale");
			break;
		case SINGLE_SIGN_ON:
			AccountSSO accountSSO = accessoSimultaneo.getUtenteCandidatoSingleSignOn();
			// si reinserisce l'oggetto nella request per poi ripassare per la action di accesso mediante SSO
			this.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountSSO);
			target = "loginSSO";
			break;
		}
		
		String msg = "Forzata la disconnessione della sessione di lavoro aperta da un'altra postazione per l'utente " 
					 + (sessionDaInvalidare != null && infoDaInvalidare != null ? infoDaInvalidare.getLogin() : username)
					 + " a favore della sessione corrente";
		evento.setMessage(msg);
		eventManager.insertEvent(evento);
		
		return target;
	}
	
	/**
	 * Genera la url per il redirezionamento ad una specifica pagina.
	 * @param page codice pagina
	 * @return url per la redirect alla pagina
	 */
	private String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
//		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

}