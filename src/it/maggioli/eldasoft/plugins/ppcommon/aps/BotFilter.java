package it.maggioli.eldasoft.plugins.ppcommon.aps;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.IUserManager;

/**
 *  
 */
public class BotFilter implements Filter {
	
	public static final String HONEYPOT_BLACK_LIST = "honeypotblacklist";

	private IEventManager eventManager;
	private IUserManager userManager;
	private ConfigInterface configManager;
	
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());		
		this.eventManager = (IEventManager) ctx.getBean("EventManager");
		this.userManager = (IUserManager) ctx.getBean("UserManager");
		this.configManager = (ConfigInterface) ctx.getBean("BaseConfigManager");
	}
	
	@Override
	public void destroy() {
	}

	/**
	 * Gestione dei filtri della web application
	 *       
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
		throws IOException, ServletException {

		Event evento = null;
		try {
			req.setCharacterEncoding("UTF-8");
			
			HttpServletRequest request = (HttpServletRequest) req;
			
			// verifica se l'ip client della richiesta appartiene alla blacklist...
			boolean block = false;
			
			String ipClient = request.getHeader("X-Forwarded-For");
			if(ipClient == null) {
				ipClient = request.getRemoteAddr();
			}
			
			ServletContext ctx = request.getSession().getServletContext();
			if(ctx == null) {
				// NON DOVREBBE MAI SUCCEDERE !!!
				//block = true;
			} else {
				Map<String, Date> blacklist = (Map<String, Date>)ctx.getAttribute(BotFilter.HONEYPOT_BLACK_LIST);
				
				if(blacklist != null) {
					Date dt = blacklist.get(ipClient);
					
					if(dt != null) {
						Date now = new Date();
						String url = request.getRequestURL().toString();
						
						boolean enabledPrivacyModule = Boolean.parseBoolean(this.configManager.getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED));
						int inhibitionIpTimeInMinutes = this.userManager.extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
						
						if(enabledPrivacyModule) {
							if((now.getTime() - dt.getTime()) / 60000 > inhibitionIpTimeInMinutes) {
								// se e' passato il tempo necessario rimuovi l'ip dalla lista
								blacklist.remove(ipClient);
								ctx.setAttribute(HONEYPOT_BLACK_LIST, blacklist);
								
								evento = new Event();
								evento.setLevel(Event.Level.INFO);
								evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
								evento.setIpAddress(ipClient);
								evento.setMessage("Termine blocco IP, consentito accesso a " + url);
							} else {
								// blocca la richiesta...
								block = true;
								
								evento = new Event();
								evento.setLevel(Event.Level.INFO);
								evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
								evento.setIpAddress(ipClient);
								evento.setMessage("Blocco IP per tentativo di accesso a " + url);
							}
						} else {
							// modulo privacy disattivato, svuota la blacklist
							blacklist.clear();
							ctx.setAttribute(HONEYPOT_BLACK_LIST, blacklist);
							
							evento = new Event();
							evento.setLevel(Event.Level.INFO);
							evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
							evento.setIpAddress(ipClient);
							evento.setMessage("Termine blocco IP, consentito accesso a " + url);
						}
					}
				}	
			}
		
			// ...restituisci la gestione all'applicazione...
			if(!block) {
				chain.doFilter(req, resp);
			} else {
				// 403 !!!
				HttpServletResponse response = (HttpServletResponse) resp;
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
	
}
