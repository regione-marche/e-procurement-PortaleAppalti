package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.botfilter;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BotFilter;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Accettazione cookies da homepage 
 * 
 * @author ...
 */
public class AllowCookiesAction extends BaseAction implements SessionAware, ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1431322390770191135L;	  
	
	private IAuthenticationProviderManager authenticationProvider;
	private IPageManager pageManager;
	private IURLManager urlManager;
	private IEventManager eventManager;
	
	private Map<String, Object> session;
	private HttpServletResponse response;

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

	public void setAuthenticationProvider(IAuthenticationProviderManager authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
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
	
	public String getUrlRedirect() {
		return urlRedirect;
	}
	
	/**
	 * abilita l'accesso all'utente 
	 */
	public String allowCookies() {
		String target = SUCCESS;
		try {
			HttpServletRequest request = (HttpServletRequest) this.getRequest();
			
			String ipAddress = request.getHeader("X-Forwarded-For");
			if(ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}

			// recupera la whitelist accessi degli utenti autorizzati
			Map<String, Date> whitelist = BotFilter.getWhiteList();
			
			// registra l'ip nella white list come utente reale
			if(whitelist != null) {			
				Event evento = new Event();
				evento.setLevel(Event.Level.INFO);
				evento.setEventType("USERVALID"); //PortGareEventsConstants.VALID_USER);
				evento.setIpAddress(ipAddress);
				evento.setMessage("Accesso utente valido");
				evento.setSessionId(this.getRequest().getSession().getId());
				
				try {
					// aggiungi l'ip alla lista degli utenti validi 
					whitelist.put(ipAddress, new Date());
					
					this.urlRedirect = getPageURL("homepage");
					target = SUCCESS;
					
				} catch (Throwable t) {
					target = INPUT;
					evento.setError(t);
					ApsSystemUtils.logThrowable(t, this, "allowCookies");
					throw new ApsSystemException("Error while checking ip access attempts", t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("allowCookies", t);
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * nega l'accesso all'utente ed aggiungilo alla black list degli accessi futuri da invalidare
	 */
	public String cancelCookies() {
		String target = SUCCESS;
		try {
			HttpServletRequest request = (HttpServletRequest) this.getRequest();
			
			String ipAddress = request.getHeader("X-Forwarded-For");
			if(ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}
	
			Map<String, Date> blacklist = BotFilter.getBlackList();

			// registra l'ip nella black list come utente non valido
			if(blacklist != null) {
				Event evento = new Event();
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
				evento.setIpAddress(ipAddress);
				evento.setMessage("Inizio blocco IP per tentativo di accesso a honeypot");
				evento.setSessionId(this.getRequest().getSession().getId());

				try {
					// aggiungi l'ip alla blacklist degli ip da bloccare...
					blacklist.put(ipAddress, new Date());
					
					// 403 !!!
					response.sendError(HttpServletResponse.SC_FORBIDDEN);

					target = INPUT;
					
				} catch (Throwable t) {
					evento.setError(t);
					ApsSystemUtils.logThrowable(t, this, "cancelCookies");
					throw new ApsSystemException("Error while checking ip access attempts", t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}

		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("cancelCookies", t);
			target = INPUT;
		}

		return target;
	}

	/**
	 * abilita l'accesso all'utente con Captcha
	 */
	public String allowCaptcha() {
		String target = SUCCESS;
		try {
			HttpServletRequest req = (HttpServletRequest) this.getRequest();
			HttpServletResponse resp = (HttpServletResponse)this.response;
			
			String url = req.getHeader("referer");
			
			boolean success = CaptchaUtils.validate(req);
			if(success) {
				// genera il cookie con un nuovo captcha token
				Cookie cookie = BotFilter.generateUserAccessCookie(req);
				target = allowCookies(); 
				if(SUCCESS.equals(target)) {
					this.urlRedirect = url;
					if(cookie != null)
						resp.addCookie(cookie);
				}
			} else {
				target = cancelCookies();
			}
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("allowCaptcha", t);
			target = INPUT;
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
