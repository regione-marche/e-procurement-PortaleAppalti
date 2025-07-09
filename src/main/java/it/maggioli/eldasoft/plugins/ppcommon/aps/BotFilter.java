package it.maggioli.eldasoft.plugins.ppcommon.aps;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.IUserManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * ... 
 */
public class BotFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger(BotFilter.class);	
	
	private static final String HONEYPOT_BLACK_LIST 		= "honeypotblacklist";		// id contesto applicativo per black list
	private static final String USER_WHITE_LIST 			= "userwhitelist";			// id contesto applicativo per white list
	private static final String USER_ACCESS_COOKIE_LIST 	= "useraccesstokenlist";	// id contesto applicativo per la lista utenti/captcha
	private static final String USER_ACCESS_COUNT			= "USERACCESSCOUNT";		// contatore tentativi di accesso di un client
	private static final String ID_COOKIE_USER_ACCESS		= "USERACCESSCOOKIE";		// session ID del nome del cookie
	private static final String USER_WHITE_LIST_FILE		= "bot-allowed.txt";
	private static final int COOKIE_USER_DAYS_VALIDITY		= 30;
	
	//***********************************************************************************************************
	/**
	 * User access cookie info
	 */
	public class UserAccessInfo {
		String sessionId;			// id di sessione
		String cookieValue;  		// cookie.value = "[data accesso]|[token]"
		Cookie cookie;
		boolean sessionCreated; 
		boolean sessionDestroyed;
		
		public String getSessionId() { return sessionId; }
		public void setSessionId(String sessionId) { this.sessionId = sessionId; }
		public String getCookieValue() { return cookieValue; }
		public void setCookieValue(String cookieValue) { this.cookieValue = cookieValue; }
		public Cookie getCookie() { return cookie; }
		public void setCookie(Cookie cookie) { this.cookie = cookie; }
		public boolean isSessionCreated() { return sessionCreated; }
		public void setSessionCreated(boolean sessionCreated) { this.sessionCreated = sessionCreated; }
		public boolean isSessionDestroyed() { return sessionDestroyed; }
		public void setSessionDestroyed(boolean sessionDestroyed) { this.sessionDestroyed = sessionDestroyed; }
	}
	//***********************************************************************************************************

	private IEventManager eventManager;
	private IUserManager userManager;
	private ConfigInterface configManager;
	private CustomConfigManager customConfigManager;
	private Map<String, Date> whiteList = null;
	private Map<String, Date> blackList = null;
	private Map<String, UserAccessInfo> userAccessCookieList = null;
	
	
	/**
	 * init 
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// inizializza i manager necessari
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());		
		this.eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
		this.userManager = (IUserManager) ctx.getBean(SystemConstants.USER_MANAGER);
		this.configManager = (ConfigInterface) ctx.getBean(SystemConstants.BASE_CONFIG_MANAGER);
		this.customConfigManager = (CustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
	}
	
	/**
	 * ... 
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Gestione dei filtri della web application
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
		throws IOException, ServletException 
	{
		LOG.debug("BotFilter.doFilter() BEGIN");
		Event evento = null;
		try {
			req.setCharacterEncoding("UTF-8");
			
			HttpServletResponse response = (HttpServletResponse) resp;
			HttpServletRequest request = (HttpServletRequest) req;
			HttpSession session = request.getSession();
			ServletContext ctx = session.getServletContext();

			String ipClient = (request.getHeader("X-Forwarded-For") == null 
					? request.getRemoteAddr() 
					: request.getHeader("X-Forwarded-For")
			);
			String userAgent = request.getHeader("USER-AGENT");
			String url = request.getRequestURL().toString();

			// recupera dal modulo privacy alcuni parametri 
			boolean enabledPrivacyModule = Boolean.parseBoolean(this.configManager.getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED));
			int inhibitionIpTimeInMinutes = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
			boolean abilitaCookies = this.customConfigManager.isActiveFunction("BOTFILTER", "ABILITACOOKIES");

			// inizializza whitelist e blacklist degli accessi...
			whiteList = getWhiteList();
			blackList = getBlackList();
			userAccessCookieList = getUserAccessCookieList();
			
			// se la customizzazione per l'accesso con cookies e' attiva 
			boolean invalidBot = false; 
			if(abilitaCookies) {
				// verifica se la request proviene da un client validato 
				invalidBot = this.isInvalidRequest(request, response, ipClient);
				if(invalidBot && blackList.get(ipClient) == null) {
					blackList.put(ipClient, new Date());
				}
			}
			
			// verifica se l'ip client della richiesta appartiene alla blacklist...
			boolean block = false;
			
			if(ctx != null && blackList != null) {
				Date dt = blackList.get(ipClient);
				if(dt != null) {
					evento = new Event();
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
					evento.setIpAddress(ipClient);
					
					if(enabledPrivacyModule || abilitaCookies) {
						Date now = new Date();
						if((now.getTime() - dt.getTime()) / 60000 > inhibitionIpTimeInMinutes) {
							// se e' passato il tempo necessario rimuovi l'ip dalla lista
							blackList.remove(ipClient);
							session.setAttribute(USER_ACCESS_COUNT, null);
							evento.setMessage("Termine blocco IP, consentito accesso a " + url);
						} else {
							// blocca la richiesta...
							block = true;
							evento.setMessage("Blocco IP per tentativo di accesso a " + url);
							evento.setDetailMessage("UserAgent " + userAgent + "\n");
						}
					} else {
						// modulo privacy disattivato, svuota la blacklist
						blackList.clear();
						evento.setMessage("Termine blocco IP, consentito accesso a " + url);
					}
				}
			}	
			
			// ...restituisci la gestione all'applicazione...
			if( !block ) {
				chain.doFilter(req, resp);
			} else {
				//**************************************************
				// 403 FORBIDDEN !!! + pausa di 30"
				//**************************************************
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				
				// metti in pausa per N secondi
				int numSecondiBlocco = 10;
				Thread.sleep(numSecondiBlocco*1000);
			}
		} catch (Throwable t) {
			LOG.error("bloccaAccesso", t);
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		LOG.debug("BotFilter.doFilter() END");
	}
	
	/**
	 * verifica se la sessione corrente e' di un utente valido o di un bot 
	 * @throws Exception 
	 */
	private boolean isInvalidRequest(
			HttpServletRequest request,
			HttpServletResponse response,
			String ipClient) throws Exception
	{
		boolean invalidBot = false;
		
		LOG.debug("Richiesta di accesso da " + ipClient);
		
		//boolean enabledPrivacyModule = Boolean.parseBoolean(this.configManager.getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED));
		//int inhibitionIpTimeInMinutes = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
		int maxLoginAttemptsSameIp = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_MAX_LOGIN_ATTEMPTS_FROM_SAME_IP, 10);

		HttpSession session = request.getSession();
		
		String userAgent = request.getHeader("USER-AGENT");
		
		// recupera il cookie inviato dal client...
		Cookie cookie = getUserAccessCookieFromRequest(request);
		UserAccessInfo userAccessInfo = userAccessCookieList.get(session.getId());
		boolean sessionReset = (userAccessInfo != null && userAccessInfo.isSessionCreated());
		boolean cookieValido = (cookie != null || sessionReset);
		
		// verifica se e' stato generato un nuovo friendly captcha token
		if(cookieValido) {
			// se viene premuto il pulsante "clicca per iniziare" nel dialog "In questo sito si utilizzano solo cookie ..." 
			// si crea il cookie per la navigazione dell'utente valido
			// e lo si aggiunge alla response...
			if(cookie != null)
				response.addCookie(cookie);
			
			// quando il nuovo cookie e' stato validato/utilizzato, eliminalo dalla lista
			if(userAccessInfo != null) {
				userAccessInfo.setCookie(null);
				userAccessInfo.setSessionCreated(false);
			}
			
			LOG.debug("Richiesta di accesso da utente validato " + ipClient);
			
		} else {
			// verifica se la richiesta ha uno user access cookie valido !!!
			boolean cookieOk = isValidCookie(request, response);
			
			if(cookieOk) {
				// la richiesta proviente da un "utente" che "ha accetto le condizioni d'uso"
				// e quindi ha uno user access cookie valido  
			} else {
				boolean ipOk = (blackList.get(ipClient) == null);
				if(ipOk) {
					// la richiesta potrebbe arrivare da un bot che deve essere validato !!!
					// aggiorna il contatore dei tentativi di accesso per la sessione corrente
					Long accessCount = getUserAccessCount(request);
			
					if(accessCount < maxLoginAttemptsSameIp) {
						session.setAttribute(USER_ACCESS_COUNT, accessCount);
					} else {
						// verifica se il bot e' nella whitelist ed e' autorizzato
						// controlla per IP
						ipOk = (whiteList.get(ipClient) != null);
						
						// controlla per USER-AGENT
						boolean userAgentOk = false;
						if( !ipOk && StringUtils.isNotEmpty(userAgent) ) {
							for (Map.Entry<String, Date> item : whiteList.entrySet()) {
								if( item.getValue() != null && userAgent.contains(item.getKey()) ) {
									userAgentOk = true;
									break;
								}
							}
						}
				
						if(ipOk || userAgentOk) {
							// bot OK 
							blackList.remove(ipClient);
							session.setAttribute(USER_ACCESS_COUNT, null);
							LOG.debug("Richiesta di accesso da bot in whitelist " + ipClient);
						} else {
							// bot NON autorizzato
							invalidBot = true;
							LOG.debug("Richiesta di accesso da bot in blacklist " + ipClient);
						}
					}
				}
			}
		}
		
		return invalidBot;
	}
	
	/**
	 * restituisce una lista di elementi di una data classe
	 * @throws ApsSystemException 
	 */
	private static synchronized <K, V> Map<V, K> getGlobalList(String id) throws ApsSystemException {
		Map<K, V> list = null;
		try {
			ServletContext ctx = SpringAppContext.getServletContext();
			list = (Map<K, V>)ctx.getAttribute(id);
			if(list == null) 
				list = new HashMap<K, V>();
			ctx.setAttribute(id, list);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, "BotFilter", "getGlobalList");
			throw new ApsSystemException("Error while reading list [" + id + "]", t);
		}

//		if(list != null) {
//			LOG.debug("getGlobalList(" + id + ") {");
//			list.forEach((k, v) -> LOG.debug("\t" + k + "=" + v) );
//			LOG.debug("} getGlobalList(" + id + ")");
//		}
		
		return (Map<V, K>) list;
	}
	
	/**
	 * restituisce la black list degli accessi utente memorizzata nell'application context 
	 * @throws ApsSystemException 
	 */
	public static synchronized Map<String, Date> getBlackList() throws ApsSystemException {
		Map<String, Date> list = getGlobalList(HONEYPOT_BLACK_LIST);
		return list; 
	}
	
	/**
	 * restituisce la white list degli accessi utente memorizzata nell'application context 
	 * @throws ApsSystemException 
	 */
	public static synchronized Map<String, Date> getWhiteList() throws ApsSystemException {
		Map<String, Date> list = getGlobalList(USER_WHITE_LIST);
		
		// se la whitelist viene creata per la prima volta allora 
		// carica l'elenco degli ip validi da file
		if(list != null && list.size() <= 0)
			loadWhiteListFile(list);
		
		return list; 
	}
	
	/**
	 * carica la configurazione dei bot/utente/ip ammessi alla navigazione sul portale 
	 */
	private static void loadWhiteListFile(Map<String, Date> list) {
		LOG.debug("Caricamento whitelist per bot abilitati alla navigazione");
		try {
			InputStream is = SpringAppContext.getServletContext()
					.getResourceAsStream(CommonSystemConstants.WEBINF_FOLDER + USER_WHITE_LIST_FILE);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
		    String ip = br.readLine();
		    while (ip != null) {
		    	if(StringUtils.isNotEmpty(ip)) {
		    		ip = ip.trim();
		    		if(ip.length() > 0 && !ip.startsWith("#")) {
		    			list.put(ip, new Date());
		    		}
		    	}
		        ip = br.readLine();
		    }
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, "BotFilter", "loadWhiteListFile");
		}
	}
	
	/**
	 * restituisce la lista degli user access cookie generati
	 */
	public static synchronized Map<String, UserAccessInfo> getUserAccessCookieList() throws ApsSystemException {
		Map<String, UserAccessInfo> list = getGlobalList(USER_ACCESS_COOKIE_LIST);
		return list; 
	}
	
	/**
	 * verifica se token captcha del cookie inviato dal client e' valido 
	 * @throws ApsSystemException 
	 */
	private boolean isValidCookie(HttpServletRequest request, HttpServletResponse response) throws ApsSystemException {
		// recupera il token captcha dal cookie (=> [Data accesso]|[token])
		Cookie cookie = getUserAccessCookieFromRequest(request);
		
		userAccessCookieList = getUserAccessCookieList();
		UserAccessInfo userAccessInfo = userAccessCookieList.get(request.getSession().getId());
		
		String cookieValue = (cookie != null && cookie.getValue() != null ? cookie.getValue() : "");
		String validationCookieValue = (userAccessInfo != null ? userAccessInfo.getCookieValue() : null);
		boolean valid = (validationCookieValue != null && cookieValue.equals(validationCookieValue));
		
		// se il client ha inviato un cookie "valido" ma non e' presente il cookie lato server  
		// si invalida il cookie del client e si richiede l'accettazione lato client...
		if(cookie != null && validationCookieValue == null) {
			if(userAccessInfo != null)
				userAccessInfo.setCookie(null);
			if(cookie != null) {
				cookie.setMaxAge(0);
				cookie.setValue("");
				response.addCookie(cookie);
			}
			valid = true;
		}

		return valid;
	}

	/**
	 * se esiste, restituisce lo user access cookie 
	 */
	private static Cookie getUserAccessCookieFromRequest(HttpServletRequest request) {
		Cookie cookie = null;
		if(request.getCookies() != null) {
			for(Cookie item : request.getCookies()) {
				if(ID_COOKIE_USER_ACCESS.equals(item.getName())) {
					cookie = item;
					break;
				}
			}
		}
		
		// se il client non ha il cookie verifica se e' appena stato generatp un nuovo cookie
		if(cookie == null) {
			try {
				Map<String, UserAccessInfo> userAccessCookieList = getUserAccessCookieList();
				UserAccessInfo userAccessInfo = userAccessCookieList.get(request.getSession().getId());
				if(userAccessInfo != null)
					cookie = userAccessInfo.getCookie();
			} catch (ApsSystemException e) {
			}
		}
		
		return cookie;
	}

	/**
	 * genera un nuovo user access cookie (data corrente + token)
	 * @throws ApsSystemException 
	 */
	public static Cookie generateUserAccessCookie(HttpServletRequest request) throws ApsSystemException {
		Cookie cookie = null;
		try {
			// genera un nuovo token da associare al cookie 
			// recupera la data corrente
			DateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");
			String dataAccesso = YYYYMMDD_HHMMSS.format(new Date());

			Random rnd = new Random();
		    String token = Long.toString(rnd.nextLong());
		
			// cookie = [data + token]
			String value = dataAccesso + "|" + token;
			
			// crea un cookie con una validità di 30gg
			// il valore del cookie non deve avere "spazi"
			cookie = new PortalCookie(ID_COOKIE_USER_ACCESS, value, request);
			cookie.setValue(value);
			cookie.setMaxAge(COOKIE_USER_DAYS_VALIDITY * 24 * 60 * 60);
			
			// aggiungi il captcha token alla lista dei token validati
			Map<String, UserAccessInfo> userAccesslist = getUserAccessCookieList();
			
			UserAccessInfo userAccessInfo = userAccesslist.get(request.getSession().getId());
			if(userAccessInfo == null) {
				BotFilter x = new BotFilter();
				userAccessInfo =  x.new UserAccessInfo();
			}
			userAccessInfo.setSessionDestroyed(false);
			userAccessInfo.setSessionId(request.getSession().getId());
			userAccessInfo.setCookieValue(cookie.getValue());
			userAccessInfo.setCookie(cookie);
			
			userAccesslist.put(request.getSession().getId(), userAccessInfo);
			
			LOG.debug("Nuovo user captcha token generato per id sessione " + request.getSession().getId());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, "BotFilter", "newUserAccessCookie");
			throw new ApsSystemException("Error while generating new user access cookie", t);
		}
	    return cookie;
	}
	
	/**
	 * ... 
	 */
	public static void doSessionCreated(HttpSession newSession) {
		String cookieValue = null;
		
		// NB: serve a limitare l'eccesso di tracciature nel log
		try {
			HttpServletRequest req = ServletActionContext.getRequest();
			Cookie cookie = (req != null ? getUserAccessCookieFromRequest(req) : null);
			cookieValue = (cookie != null ? cookie.getValue() : "");
		} catch (Throwable t) {
		}
		
		// nel caso di sessione invalidata cerca per cookie la vecchia sessione ed aggiorna le info con la nuova sessione
		if(StringUtils.isNotEmpty(cookieValue)) {
			try {
				Map<String, UserAccessInfo> userAccesslist = getUserAccessCookieList();
				for (Map.Entry<String, UserAccessInfo> item : userAccesslist.entrySet())
					if(item.getValue().isSessionDestroyed() && cookieValue.equals(item.getValue().getCookieValue())) {
						// crea una "copia" del precedente user access cookie da associare alla nuova sessione
						BotFilter x = new BotFilter();
						UserAccessInfo userAccessInfo = x.new UserAccessInfo();
						userAccessInfo.setSessionCreated(true);
						userAccessInfo.setSessionDestroyed(false);
						userAccessInfo.setSessionId(newSession.getId());
						userAccessInfo.setCookieValue(item.getValue().getCookieValue());
						userAccessInfo.setCookie(item.getValue().getCookie());
						userAccesslist.put(newSession.getId(), userAccessInfo);
						
						// rimuovi il vecchio elemento con la precedente sessione dalla lista 
						userAccesslist.remove(item.getValue().getSessionId());
						
						break;
					}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, "BotFilter", "doSessionCreated");
			}
		}
	}
	
	/**
	 * ... 
	 */
	public static void doSessionDestroyed(HttpSession oldSession) {
		try {
			Map<String, UserAccessInfo> userAccesslist = getUserAccessCookieList();
			UserAccessInfo userAccessInfo = userAccesslist.get(oldSession.getId());
			if(userAccessInfo != null) {
				userAccessInfo.setSessionDestroyed(true);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, "BotFilter", "doSessionDestroyed");
		}
	}
	
	/**
	 * restituisce i tentativi di accesso di un client
	 */
	private Long getUserAccessCount(HttpServletRequest request) {
		Long n = null;
		try {
			n = (Long)request.getSession().getAttribute(USER_ACCESS_COUNT);
		} catch (Throwable t) {
		}
		return (n == null ? new Long(0) : n) + 1;
	}
	
}
