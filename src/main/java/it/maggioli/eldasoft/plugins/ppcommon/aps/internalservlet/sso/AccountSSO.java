package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.DelegateUser;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.system.services.user.DelegateUser.Accesso;
import com.opensymphony.xwork2.ActionContext;

/**
 * ...
 */
public class AccountSSO implements HttpSessionBindingListener, HttpSessionActivationListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -301895878764308084L;

	private String login;
	private String authId;
	private String nome;
	private String cognome;
	private String email;
	private String tipoAutenticazione;
	private String ipAddress;
	private String tipologiaLogin;
	private String ragioneSociale;
	private DelegateUser profilo;
	//private List<UserDetails> impreseCollegate;

	@Override
	public void valueBound(HttpSessionBindingEvent hsbe) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent hsbe) {
		// recupera i manager necessari (eventi, utenti)
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		IEventManager eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
		
		Event evento = new Event();
		evento.setLevel(Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGOUT_SSO);
		evento.setMessage("Disconnessione soggetto " + nome + " " + cognome + " con email " + email + " autenticato mediante single sign on esterna");
		evento.setUsername(getLogin());
		evento.setIpAddress(getIpAddress());
		evento.setSessionId(hsbe.getSession().getId());
		eventManager.insertEvent(evento);
		
		//31/03/2017: si rimuove l'utente disconnesso dalla lista contenuta nell'applicativo
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(hsbe.getSession().getServletContext());
		tracker.removeSessioneUtente(hsbe.getSession().getId());
		
		// esegui il logout del soggetto impresa SSO
		logoutProfiloSSO();
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(event.getSession().getServletContext());
    	tracker.putSessioneUtente(
    			event.getSession()
    			, getIpAddress()
    			, getLogin()
    			, DateFormatUtils.format(new Date(event.getSession().getCreationTime()), "dd/MM/yyyy HH:mm:ss")
    	);
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent event) {
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(event.getSession().getServletContext());
    	tracker.removeSessioneUtente(event.getSession().getId());
	}

	/**
	 * restituisce l'oggetto in sessione relativo al login con SSO 
	 */
	public static AccountSSO getFromSession() {
		AccountSSO sso = null; 
		try {
			sso = (AccountSSO)ActionContext.getContext().getSession().get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().warn(t.getMessage());
		}
		return sso;
	}

	/**
	 * aggiorna l'oggetto in sessione relativo al login con SSO 
	 */
	public void putToSession() {
		ActionContext.getContext().getSession().put(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, this);
	}

	/**
	 * restituisce se l'account SSO prevede la profilazione di piu' utenti per la stessa impresa
	 */
	public static boolean isAccessiDistinti(HttpSession session) {
		boolean accessiDistinti = false;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
			if(customConfigManager.isActiveFunction("LOGIN", "PROFILAUTENTISSO")) {
				AccountSSO sso = (AccountSSO) (session != null
						? session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO)
						: ActionContext.getContext().getSession().get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO));
				accessiDistinti = (sso != null && sso.getProfilo() != null);
			}
		} catch (Throwable t) {
			// NON DOVREBBRE SUCCEDERE!!!
			ApsSystemUtils.getLogger().error("isAccessiDistinti", "Non è possibile recuperare l'accesso SSO dalla sessione", t);
		}
		return accessiDistinti;
	}
	
	public static boolean isAccessiDistinti() {
		return isAccessiDistinti(null);
	}

	/**
	 * restituisce il flusso aperto da un soggetto impresa
	 */
	public static String getFlussoAccessiDistinti(AccountSSO sso) {
		String flusso = "";
		if(sso != null && sso.getProfilo() != null && sso.getProfilo().getFlusso() != null) {
			int i = sso.getProfilo().getFlusso().indexOf("-");
			flusso = (i > 0 ? sso.getProfilo().getFlusso().substring(0, i) : "");
		}
		return flusso;
	}
	
	/**
	 * restituisce il codice del flusso aperto da un soggetto impresa
	 */
	public static String getCodiceAccessiDistinti(AccountSSO sso) {
		String codice = "";
		if(sso != null && sso.getProfilo() != null && sso.getProfilo().getFlusso() != null) {
			int i = sso.getProfilo().getFlusso().indexOf("-");
			codice = (i > 0 ? sso.getProfilo().getFlusso().substring(i + 1) : "");
		}
		return codice;
	}

	/**
	 * restituisce il ruolo di un soggetto impresa (sola lettura, compilazione, accesso completo)
	 */
	public static DelegateUser.Accesso getRuoloAccessiDistinti(AccountSSO sso) {
		
		DelegateUser.Accesso ruolo = (isAccessiDistinti()
				? DelegateUser.Accesso.READONLY		// in caso di accessi distinti con SSO, l'accesso di partenza e' sempre sola lettura
				: DelegateUser.Accesso.EDIT_SEND	// in caso di accesso standard senza SSO, l'accesso e' sempre completo
		);
		if(sso != null) {
			// in caso ci sia un profilo specifico per il soggetto impresa autorizzato
			// utilizza il profilo definito dall'OWNER, 
			// altrimenti si concede solo l'accesso in SOLA LETTURA
			ruolo = (sso.getProfilo() != null 
					 ? sso.getProfilo().getRolename()
					 : DelegateUser.Accesso.READONLY);
		}
		return ruolo;
	}
	
	/**
	 * esegui il logout del profilo del soggetto impresa 
	 */
	private void logoutProfiloSSO() {
		if(getProfilo() != null) {
			try {
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
				IUserManager userManager = (IUserManager) ctx.getBean(SystemConstants.USER_MANAGER);
				
				// unlock...
				UserDetails user = userManager.getUser(getProfilo().getUsername());
				userManager.unlockProfiloSSOAccess(user, getProfilo().getDelegate());
				
				// logout... 
				profilo = userManager.getProfiloSSO(getProfilo().getUsername(), getProfilo().getDelegate());
				profilo.setLogoutTime(new Date());
				userManager.updateProfiloSSO(profilo);
				
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().error("logoutProfiloSSO", this, t);
			}
		}
	}

	/**
	 * effettua il login del soggetto impresa e carica il profilo di accesso
	 */
	public DelegateUser loadProfilo(String username) {
		profilo = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IUserManager userManager = (IUserManager) ctx.getBean(SystemConstants.USER_MANAGER);
			UserDetails user = userManager.getUser(username);
			
			if(user != null 
			   && !SystemConstants.GUEST_USER_NAME.equalsIgnoreCase(user.getUsername()) 
			   && !SystemConstants.ADMIN_USER_NAME.equalsIgnoreCase(user.getUsername())) 
			{
				// prepara il profilo di accesso associato al soggetto impresa (owner o invitato)
				// ricarica i dati da DB...
				profilo = userManager.getProfiloSSO(user.getUsername(), login);
				
				// se e' l'utente OWNER reimposta il ruolo e i permessi da OWNER
				if(StringUtils.isNotEmpty(login) && login.equalsIgnoreCase(user.getDelegateUser())) {
					// owner
					profilo.setOwner(true);
					profilo.setRolename(Accesso.EDIT_SEND);
				}
				
				// aggiorna l'oggetto in sessione
				this.putToSession();
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("loadProfilo", this, t);
		}
		return this.profilo;
	}
	
	/**
	 * restituisce l'elenco delle imprese (attive) associate ad un utente 
	 */
	public List<UserDetails> getImpresaAssociateAttive() {
		List<UserDetails> impreseAssociate = new ArrayList<UserDetails>();
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IRegistrazioneImpresaManager registrazioneImpresaManager = (IRegistrazioneImpresaManager) ctx.getBean("RegistrazioneImpresaManager");
			IUserManager userManager = (IUserManager) ctx.getBean(SystemConstants.USER_MANAGER);

			// aggiungi solo le impresa abilitate...
			List<UserDetails> imprese = registrazioneImpresaManager.getImpreseAssociate(login);
			for(int i = 0; i < imprese.size(); i++) {
				if( !imprese.get(i).isDisabled() ) {
					impreseAssociate.add(imprese.get(i));
				}
			}
		
			// Profili SSO - aggiungi anche le ditte per le quali il soggetto impresa e' stato invitato dall'OWNER delle ditte 
			List<DelegateUser> profili = userManager.getProfiliSSO(null, login);
			for(int i = 0; i < profili.size(); i++) {
				UserDetails ditta = userManager.getUser(profili.get(i).getUsername());
				if(ditta != null) {
					impreseAssociate.add(ditta);
				}
			}
			
		} catch(Exception ex) {
			ApsSystemUtils.logThrowable(ex, null, "getImpresaAssociateAttive");
		}	
		return impreseAssociate;
	}


	public void setTipoAutenticazione(String tipoAutenticazione) {
		this.tipoAutenticazione = tipoAutenticazione;
	}

	public String getTipoAutenticazione() {
		return tipoAutenticazione;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return Estrae la denominazione dell'account, valorizzata con nome e
	 *         cognome nel caso di persona fisica, ragione sociale nel caso di
	 *         persona giuridica.
	 */
	public String getDenominazione() {
		StringBuilder denominazione = new StringBuilder();
		if (StringUtils.isNotBlank(nome)) {
			denominazione.append(nome);
		}
		if (StringUtils.isNotBlank(cognome)) {
			if (denominazione.length() > 0) {
				denominazione.append(" ");
			}
			denominazione.append(cognome);
		}
		return denominazione.toString();
	}

	public String getTipologiaLogin() {
		return tipologiaLogin;
	}

	public void setTipologiaLogin(String tipologiaLogin) {
		this.tipologiaLogin = tipologiaLogin;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public DelegateUser getProfilo() {
		return profilo;
	}

//	/**
//	 * @return the impreseCollegate
//	 */
//	public List<UserDetails> getImpreseCollegate() {
//		return impreseCollegate;
//	}
//
//	/**
//	 * @param impreseCollegate the impreseCollegate to set
//	 */
//	public void setImpreseCollegate(List<UserDetails> impreseCollegate) {
//		this.impreseCollegate = new ArrayList<UserDetails>();
//	}

}
