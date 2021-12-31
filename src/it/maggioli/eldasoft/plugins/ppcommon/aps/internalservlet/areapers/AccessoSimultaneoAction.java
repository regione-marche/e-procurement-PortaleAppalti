package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;

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
	 * UID.
	 */
	private static final long serialVersionUID = -6258772164285601659L;

	private Map<String, Object> session;
	private HttpServletResponse response;
	
	private IPageManager pageManager;
	private IURLManager urlManager;
	private IEventManager eventManager;

	private String[] sessioneSimultanea;
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
	
	/**
	 * @return the urlRedirect
	 */
	public String getUrlRedirect() {
		return urlRedirect;
	}

	public String[] getSessioneSimultanea() {
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
		this.sessioneSimultanea = TrackerSessioniUtenti.getInstance(this.getRequest().getSession().getServletContext()).getDatiSessioniUtentiConnessi().get(sessionIdUserLogged);
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
	
	public String force() {
		String target = "redirect";
		// si rimuovono i dati inseriti in sessione per la gestione della concorrenza
		AccessoSimultaneoBean accessoSimultaneo = (AccessoSimultaneoBean)session.remove(AccessoSimultaneoBean.SESSION_CUNCURRENT_OBJECT_ID);
		String sessionIdUserLogged = accessoSimultaneo.getSessionIdUtenteConnesso();
		
		// si invalida la sessione correntemente operativa
		ServletContext context = this.getRequest().getSession().getServletContext();
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(context);
		HttpSession httpSession = tracker.getSessioniUtentiConnessi().get(sessionIdUserLogged);
		String[] dati = tracker.getDatiSessioniUtentiConnessi().get(sessionIdUserLogged);
		if (httpSession != null && dati != null) {
			try {
				httpSession.invalidate();
				ApsSystemUtils.getLogger().info(
						"Forzata disconnessione per utente " + dati[1]);
			} catch (IllegalStateException e) {
				// si sono verificati dei casi con SSO per cui rimangono i
				// riferimenti ad una sessione "morta" di un precedente accesso
				// mediante SSO: in tal caso blocca l'accesso fintantochè non si
				// ferma il tomcat e non si svuota la work!
				// la soluzione pertanto è di intercettare l'errore in fase di
				// invalidazione della sessione in quanto gia' rimossa dal
				// context applicativo e ripulire i dati della cache applicativa
				// per la gestione delle sessioni
				tracker.removeSessioneUtente(sessionIdUserLogged);
				ApsSystemUtils.getLogger().warn(
						"Rimossa dalla cache applicativa la sessione " + sessionIdUserLogged + " gia' invalidata in precedenza ma erroneamente rimasta nella cache per l'utente " + dati[1]);
			}
		}

		Event evento = new Event();
		evento.setLevel(Level.WARNING);
		evento.setEventType(PortGareEventsConstants.LOGIN_AS);
		switch (accessoSimultaneo.getTipoAutenticazione()) {
		case DB:
			UserDetails user = accessoSimultaneo.getUtenteCandidatoPortale();
			String username = user.getUsername();
			String password = user.getPassword();
			String ipAddress = user.getIpAddress();

			// si predispongono i dati per l'accesso
			session.put("username", username);
			session.put("password", password);
			this.urlRedirect = getPageURL("ppcommon_area_personale");
			
			// tracciatura dell'evento di disconnessione forzata per far accedere il nuovo utente connesso
			evento.setUsername(username);
			evento.setIpAddress(ipAddress);
			evento.setSessionId(this.getRequest().getSession().getId());

			break;
		case SINGLE_SIGN_ON:
			AccountSSO accountSSO = accessoSimultaneo.getUtenteCandidatoSingleSignOn();
			// si reinserisce l'oggetto nella request per poi ripassare per la action di accesso mediante SSO
			this.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountSSO);
			target = "loginSSO";
			break;
		}
		evento.setMessage("Forzata la disconnessione della sessione di lavoro aperta da un'altra postazione per l'utente " + dati[1] + " con id sessione " + sessionIdUserLogged + " a favore della sessione corrente");
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
		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}
}