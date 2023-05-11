package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.botfilter;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Honeypot - link trappola per bot 
 * 
 * @author ...
 */
public class AdminfunAction extends BaseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4627143319103591419L;
	
	private IAuthenticationProviderManager authenticationProvider;
	
	public void setAuthenticationProvider(IAuthenticationProviderManager authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	
	/**
	 * open 
	 */
	public String open() {
		return SUCCESS;
	}
		
	/**
	 * login
	 */
	public String login() {
		this.bloccaAccesso();
		return INPUT;
	}

	/**
	 * info
	 */
	public String info() {
		this.bloccaAccesso();
		return INPUT;
	}
	
	/**
	 * blocca l'accesso utente
	 */
	private void bloccaAccesso() {
		try {
			HttpServletRequest httpServletRequest = (HttpServletRequest) this.getRequest();
			String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
			if(ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}
			
			if(this.authenticationProvider.honeypotAttempt(ipAddress)) {
				// metti in pausa per N secondi
				int numSecondiBlocco = 10;
				Thread.sleep(numSecondiBlocco*1000);
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("bloccaAccesso", t);
		}
	}

}
