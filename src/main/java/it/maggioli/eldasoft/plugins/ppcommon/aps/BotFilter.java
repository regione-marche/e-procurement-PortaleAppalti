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
import org.apache.commons.lang3.StringUtils;
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

/**
 *  
 */
public class BotFilter implements Filter {
	
	// id di sessione per black, white list 
	public static final String HONEYPOT_BLACK_LIST 			= "honeypotblacklist";
	public static final String USER_WHITE_LIST 				= "userwhitelist";
	public static final String COOKIE_USER_ACCESS			= "USERACCESSCOOKIE";
	public static final String CREATE_USER_ACCESS_COOKIE 	= "createUserAccessCookie";
	
	private static final String USER_ACCESS_COUNT			= "USERACCESSCOUNT";
	private static final String USER_WHITE_LIST_FILE		= "bot-allowed.txt";

	private static final int COOKIE_ALLOW_USER_DURATION		= 30;	// in days
	
	private IEventManager eventManager;
	private IUserManager userManager;
	private ConfigInterface configManager;
	private CustomConfigManager customConfigManager;

	private Map<String, Date> whitelist = null;
	private Map<String, Date> blacklist = null;
	
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// inizializza i manager necessari
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());		
		this.eventManager = (IEventManager) ctx.getBean("EventManager");
		this.userManager = (IUserManager) ctx.getBean("UserManager");
		this.configManager = (ConfigInterface) ctx.getBean("BaseConfigManager");
		this.customConfigManager = (CustomConfigManager) ctx.getBean("CustomConfigManager");
	}
	
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
		Event evento = null;
		try {
			req.setCharacterEncoding("UTF-8");
			
			HttpServletResponse response = (HttpServletResponse) resp;
			HttpServletRequest request = (HttpServletRequest) req;
			HttpSession session = request.getSession();
			ServletContext ctx = session.getServletContext();

			String ipClient = request.getHeader("X-Forwarded-For");
			if(ipClient == null) {
				ipClient = request.getRemoteAddr();
			}
			
			String userAgent = request.getHeader("USER-AGENT");

			// recupera dal modulo privacy alcuni parametri 
			boolean enabledPrivacyModule = Boolean.parseBoolean(this.configManager.getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED));
			int inhibitionIpTimeInMinutes = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
			boolean abilitaCookies = this.customConfigManager.isActiveFunction("BOTFILTER", "ABILITACOOKIES");

			// inizializza whitelist e blacklist degli accessi...
			this.whitelist = getWhiteList(request);
			this.blacklist = getBlackList(request);
			
			// se la customizzazione per l'accesso con cookies e' attiva 
			// controlla se l'utente e' valido o un bot
			boolean invalidBot = false; 
			if(abilitaCookies) {
				invalidBot = this.isInvalidBot(request, response, ipClient, userAgent);
				if(invalidBot && this.blacklist.get(ipClient) == null) {
					this.blacklist.put(ipClient, new Date());
				}
			}
			
			// verifica se l'ip client della richiesta appartiene alla blacklist...
			boolean block = false;
			
			if(ctx != null && this.blacklist != null) {
				Date dt = this.blacklist.get(ipClient);
				
				if(dt != null) {
					Date now = new Date();
					String url = request.getRequestURL().toString();
					
					evento = new Event();
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
					evento.setIpAddress(ipClient);
					
					if(enabledPrivacyModule || abilitaCookies) {
						if((now.getTime() - dt.getTime()) / 60000 > inhibitionIpTimeInMinutes) {
							// se e' passato il tempo necessario rimuovi l'ip dalla lista
							this.blacklist.remove(ipClient);
							session.setAttribute(USER_ACCESS_COUNT, null);
							//ctx.setAttribute(HONEYPOT_BLACK_LIST, this.blacklist);
							evento.setMessage("Termine blocco IP, consentito accesso a " + url);
						} else {
							// blocca la richiesta...
							block = true;
							evento.setMessage("Blocco IP per tentativo di accesso a " + url);
						}
					} else {
						// modulo privacy disattivato, svuota la blacklist
						this.blacklist.clear();
						ctx.setAttribute(HONEYPOT_BLACK_LIST, this.blacklist);
						evento.setMessage("Termine blocco IP, consentito accesso a " + url);
					}
				}
			}	
			
			// ...restituisci la gestione all'applicazione...
			if( !block ) {
				chain.doFilter(req, resp);
			} else {
				// 403 !!!
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				
				// metti in pausa per N secondi
				int numSecondiBlocco = 10;
				Thread.sleep(numSecondiBlocco*1000);
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("bloccaAccesso", t);
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
	}
	
	/**
	 * verifica se la sessione corrente e' di un utente valido o di un bot 
	 * @throws Exception 
	 */
	private boolean isInvalidBot(
			HttpServletRequest request,
			HttpServletResponse response,
			String ipClient, 
			String userAgent) throws Exception
	{
		boolean invalidBot = false;
		
		ApsSystemUtils.getLogger().debug("Richiesta di accesso da " + ipClient);
		
		HttpSession session = request.getSession();
		//boolean enabledPrivacyModule = Boolean.parseBoolean(this.configManager.getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED));
		//int inhibitionIpTimeInMinutes = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
		int maxLoginAttemptsSameIp = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_MAX_LOGIN_ATTEMPTS_FROM_SAME_IP, 10);

		// se viene premuto il pulsante "accetta" nel dialog "In questo sito si utilizzano solo cookie ..." 
		// si crea il cookie per la navigazione dell'utente valido
		if("1".equals(session.getAttribute(CREATE_USER_ACCESS_COOKIE))) {
			createUserAccessCookie(request, response);
			session.removeAttribute(CREATE_USER_ACCESS_COOKIE);
			ApsSystemUtils.getLogger().debug("Richiesta di accesso da utente validato " + ipClient);
		} else {
			// se l'utente non e' sospetto (bot) allora 
			// aggiorna il contatore dei tentativi di accesso
			Long n = null;
			try {
				n = (Long)session.getAttribute(USER_ACCESS_COUNT);
			} catch (Throwable t) {
			}
			n = (n == null ? new Long(0) : n) + 1;
			
			Cookie cookie = getUserAccessCookie(request);
			
			if(n < maxLoginAttemptsSameIp || cookie != null) {
				// aggiorna il numero di tentativi di accesso per la sessione corrente
				session.setAttribute(USER_ACCESS_COUNT, n);
			} else {
				// verifica se il bot e' nella whitelist ed e' autorizzato
				
				// controlla per IP
				boolean ipOk = (this.whitelist.get(ipClient) != null);
				
				// controlla per USER-AGENT
				boolean userAgentOk = false;
				if( !ipOk && StringUtils.isNotEmpty(userAgent) ) {
					for (Map.Entry<String, Date> item : this.whitelist.entrySet()) {
						if( item.getValue() != null && item.getKey().contains(userAgent) ) {
							userAgentOk = true;
							break;
						}
					}
				}
				
				if(ipOk || userAgentOk) {
					// bot OK 
					this.blacklist.remove(ipClient);
					session.setAttribute(USER_ACCESS_COUNT, null);
					ApsSystemUtils.getLogger().debug("Richiesta di accesso da bot in whitelist " + ipClient);
				} else {
					// bot NON autorizzato
					invalidBot = true;
					ApsSystemUtils.getLogger().debug("Richiesta di accesso da bot in blacklist " + ipClient);
				}
			}
		}
		
		return invalidBot;
	}
	
	/**
	 * restituisce la black list degli accessi utente memorizzata nell'application context 
	 * @throws ApsSystemException 
	 */
	public static Map<String, Date> getBlackList(HttpServletRequest request) throws ApsSystemException {
		Map<String, Date> list = null;
		try {
			HttpSession session = request.getSession();
			ServletContext ctx = session.getServletContext();
			list = (Map<String, Date>)ctx.getAttribute(HONEYPOT_BLACK_LIST);
			if(list == null) {
				list = new HashMap<String, Date>();
			}
			ctx.setAttribute(HONEYPOT_BLACK_LIST, list);
			
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, "BotFilter", "getBlackList");
			throw new ApsSystemException("Error while reading black list users", t);
		}
		return list; 
	}
	
	/**
	 * restituisce la white list degli accessi utente memorizzata nell'application context 
	 * @throws ApsSystemException 
	 */
	public static Map<String, Date> getWhiteList(HttpServletRequest request) throws ApsSystemException {
		Map<String, Date> list = null;
		try {
			HttpSession session = request.getSession();
			ServletContext ctx = session.getServletContext();
			list = (Map<String, Date>)ctx.getAttribute(USER_WHITE_LIST);
			if(list == null) {
				list = new HashMap<String, Date>();
				
				// se la whitelist viene creata per la prima volta allora 
				// carica l'elenco degli ip validi da file
				loadWhiteListFile(request, list);
			}
			ctx.setAttribute(USER_WHITE_LIST, list);
			
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, "BotFilter", "getWhiteList");
			throw new ApsSystemException("Error while reading white list users", t);
		}
		return list; 
	}
	
	/**
	 * carica la configurazione dei bot/utente/ip ammessi alla navigazione sul portale 
	 */
	private static void loadWhiteListFile(HttpServletRequest request, Map<String, Date> list) {
		ApsSystemUtils.getLogger().debug("Caricamento whitelist per bot abilitati alla navigazione");
		try {
			InputStream is = request.getSession().getServletContext()
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
	 * se esiste, restituisce il cookie di accesso dell'utente 
	 */
	private Cookie getUserAccessCookie(HttpServletRequest request) {
		Cookie cookie = null;
		if(request.getCookies() != null) {
			for(Cookie item : request.getCookies()) {
				if(COOKIE_USER_ACCESS.equals(item.getName())) {
					cookie = item;
					break;
				}
			}
		}
		return cookie;
	}

	/**
	 * crea un cookie di accesso per l'utente legittimo 
	 */
	private void createUserAccessCookie(HttpServletRequest request, ServletResponse response) {
		String dataAccesso = null;

		// verifica se esiste gia' il cookie per l'utente...
		Cookie cookie = getUserAccessCookie(request);
		if(cookie != null) {
			dataAccesso = cookie.getValue();
		}
		
		// aggiorna/crea il cookie...
		if(cookie == null) {
			DateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");
			dataAccesso = YYYYMMDD_HHMMSS.format(new Date());
			
			// il valore del cookie non deve avere " " 
			cookie = new PortalCookie(COOKIE_USER_ACCESS, dataAccesso, request);
			cookie.setValue(dataAccesso);
			cookie.setMaxAge(COOKIE_ALLOW_USER_DURATION * 24 * 60 * 60);	// 30gg di validita'
		}	
		
		// aggiungi il cookie alla response...
		((HttpServletResponse) response).addCookie(cookie);
	}
	
}
