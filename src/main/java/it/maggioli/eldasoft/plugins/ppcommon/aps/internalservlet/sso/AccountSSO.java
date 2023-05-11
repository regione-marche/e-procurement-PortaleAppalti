package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AccountSSO implements HttpSessionBindingListener, HttpSessionActivationListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -301895878764308084L;
	
	private String login;
	private String nome;
	private String cognome;
	private String email;
	private String tipoAutenticazione;
	private String ipAddress;
	//private List<UserDetails> impreseCollegate;
	private String tipologiaLogin;
	private String ragioneSociale;

	@Override
	public void valueBound(HttpSessionBindingEvent hsbe) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent hsbe) {
		Event evento = new Event();
		evento.setLevel(Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGOUT_SSO);
		evento.setMessage("Disconnessione soggetto " + nome + " " + cognome + " con email " + email + " autenticato mediante single sign on esterna");
		evento.setUsername(this.getLogin());
		evento.setIpAddress(this.getIpAddress());
		evento.setSessionId(hsbe.getSession().getId());
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		IEventManager eventManager = (IEventManager) ctx.getBean("EventManager");
		eventManager.insertEvent(evento);
		
		//31/03/2017: si rimuove l'utente disconnesso dalla lista contenuta nell'applicativo
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(hsbe.getSession().getServletContext());
		tracker.removeSessioneUtente(hsbe.getSession().getId());
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(event.getSession().getServletContext());
    	tracker.putSessioneUtente(event.getSession(), this.getIpAddress(), this.getLogin(), DateFormatUtils.format(new Date(event.getSession().getCreationTime()), "dd/MM/yyyy HH:mm:ss"));
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent event) {
		TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(event.getSession().getServletContext());
    	tracker.removeSessioneUtente(event.getSession().getId());
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

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
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
		if (StringUtils.isNotBlank(this.nome)) {
			denominazione.append(this.nome);
		}
		if (StringUtils.isNotBlank(this.cognome)) {
			if (denominazione.length() > 0) {
				denominazione.append(" ");
			}
			denominazione.append(this.cognome);
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
