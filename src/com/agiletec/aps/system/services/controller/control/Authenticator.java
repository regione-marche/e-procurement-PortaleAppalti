/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system.services.controller.control;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers.AccessoSimultaneoBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;


/**
 * Sottoservizio di controllo esecutore dell'autenticazione.
 *
 * @author
 */
public class Authenticator extends AbstractControlService implements ApplicationContextAware {

	public final static long SECOND_MILLIS = 1000;
	public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
	
	/**
	 * Chiave da inserire in sessione quando da un'autenticazione effettuata in
	 * precedenza (ad esempio un'autenticazione forte mediante single sign on)
	 * si vuole accedere come operatore economico tra quelli in delega. Con
	 * questa chiave memorizzata in sessione si cerca l'utente per login e non
	 * per la coppia login e password in modo da essere compatibili con
	 * algoritmi di cifratura non reversibili (stile message digest).
	 */
	public final static String PASSE_PARTOUT = "QTk4MjM0ZGtvXz/DrCRkcy5mc2tsXzNsw7I0a3NkZiEjNDU0MzUzQHdldmdmUzNEYnZ3MzUhZGZsa2pz"; 

	//public static final String UTENTI_CONNESSI = "utentiConnessi";

	//private ServletContext servletContext;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager _appParamManager;
	
	private IEventManager _eventManager;
	private IUserManager _userManager;
	private IAuthorizationManager _authorizationManager;
	private IAuthenticationProviderManager _authenticationProvider;
	ApplicationContext _applicationContext;
	private boolean formLoginVisible;	

	protected IUserManager getUserManager() {
		return _userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}

	protected IAuthenticationProviderManager getAuthenticationProvider() {
		return _authenticationProvider;
	}

	public void setAuthenticationProvider(IAuthenticationProviderManager authenticationProvider) {
		this._authenticationProvider = authenticationProvider;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this._applicationContext = ac;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this._appParamManager = appParamManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this._eventManager = eventManager;
	}

	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}
	
	public IAuthorizationManager getAuthorizationManager() {
		return this._authorizationManager;
	}
	
	/**
	 * @return the formLoginVisible
	 */
	public boolean isFormLoginVisible() {
		return formLoginVisible;
	}

	/**
	 * @param formLoginVisible the formLoginVisible to set
	 */
	public void setFormLoginVisible(boolean formLoginVisible) {
		this.formLoginVisible = formLoginVisible;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this._log.debug("{}: initialized", this.getClass().getName());
	}

	/**
	 * Esecuzione. Il metodo service esegue le seguenti operazioni (nell'ordine
	 * indicato): 
	 * 1) se nella richiesta sono presenti dei parametri user e password, 
	 *    viene caricato l'utente relativo; se l'utente restituito e' non nullo, 
	 *    lo si mette in sessione; se l'utente restituito e' nullo, non si fa
	 *    nulla. 
	 * 2) si controlla l'esistenza di un utente in sessione; se non esiste,
	 *    si richiede un utente di default e lo si mette in sessione.
	 *
	 * @param reqCtx Il contesto di richiesta
	 * @param status Lo stato di uscita del servizio precedente
	 * @return Lo stato di uscita
	 */
	@Override
	public int service(RequestContext reqCtx, int status) {
		reqCtx.getRequest().setAttribute(SystemConstants.EXTRAPAR_CURRENT_LANG, 
										 reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG));
		int retStatus = doAuthentication(
				reqCtx.getRequest(),
				status, 
				this._authenticationProvider, 
				this._userManager, 
				this._appParamManager, 
				this._applicationContext,
				this._eventManager,
				this._log,
				this);
		if(retStatus == ControllerManager.REDIRECT) {
			HttpSession session = reqCtx.getRequest().getSession();
			UserDetails currentUser = ((UserDetails)session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER));			
			if(StringUtils.isNotEmpty((String)session.getAttribute(SystemConstants.SESSION_SBLOCCO_UTENZA_AUTONOMO))) {
				this.redirect("ppcommon_sblocco_autonomo", reqCtx);
			} else {
				if(session.getAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER) != null){
					this.redirect("ppcommon_admin_access", reqCtx);
				} else if (currentUser != null && !currentUser.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
					if(currentUser.isCredentialsNotExpired()) {
						this.redirect("ppcommon_area_personale", reqCtx);
					} else {
						this.redirect("ppcommon_cambia_password", reqCtx);
					}
				} else {
					this.redirect("ppcommon_accesso_simultaneo", reqCtx);
				}
			}
		}
		return retStatus;
	}

	/**
	 * Esegue una richiesta e traccia l'evento nel log applicativo... 
	 */
	public static int doAuthentication(
			HttpServletRequest req,
			int status,
			IAuthenticationProviderManager authenticationProvider,
			IUserManager userManager,
			IAppParamManager appParamManager,
			ApplicationContext applicationContext,
			IEventManager eventManager,
			org.slf4j.Logger log,
			Object sender)
	{
		if (log.isTraceEnabled()) {
			log.trace("Invocata {}", sender.getClass().getName());
		}
		int retStatus = ControllerManager.CONTINUE; //INVALID_STATUS;
		if (status == ControllerManager.ERROR) {
			return status;
		}
		Event evento = null;
		try {
			boolean isSetPassePartout = false;
			String userName = req.getParameter("username");
			String password = req.getParameter("password");
			String currentLang = null;
			UserDetails user = null;
			UserDetails userPwd = null;
			
			Lang lang = (Lang)req.getAttribute(SystemConstants.EXTRAPAR_CURRENT_LANG);
			if(lang != null) {
				currentLang = lang.getCode();
			}
			
			// sentinella per comprendere se sono nel form di attivazione/riattivazione 
			// utente, in tal caso non devo tracciare nulla
			String passwordConfirm = req.getParameter("passwordConfirm");
			HttpSession session = req.getSession();
			
			/* SSO: In questo caso vado a controllare se sono presenti i 
			 * parametri username e password in sessione a seguito di un'autenticazione 
			 * da parte di un soggetto fisico nei panni di un suo operatore 
			 * economico collegato          
			 */
			if(StringUtils.isEmpty(userName) && StringUtils.isEmpty(password)) {
				String usernameFromSession = (String)session.getAttribute("username");
				String passwordFromSession = (String)session.getAttribute("password");
				if(!SystemConstants.GUEST_USER_NAME.equals(usernameFromSession) && usernameFromSession != null && passwordFromSession != null){ //test su guest
					userName = usernameFromSession;
					password = passwordFromSession;
					session.removeAttribute("username");
					session.removeAttribute("password");
					if (PASSE_PARTOUT.equals(password)) {
						// l'autenticazione mediante passepartout puo' verificarsi ESCLUSIVAMENTE previa autenticazione precedente e quindi come loginAs con passaggio delle credenziali in sessione 
						isSetPassePartout = true;
					}
				}
			}
			
			// imposta l'IPAddress per la richiesta corrente
			String ipAddress = req.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = req.getRemoteAddr();
			}
			
			// Verifico se l'ip usato e' stato inibito o meno
			boolean isSuspendedIp = checkIpSuspended(
					req, 
					ipAddress, 
					applicationContext,
					userManager,
					authenticationProvider);
					
			if (!isSuspendedIp && userName != null && password != null) {
				log.trace("user {} - password ******** ", userName);
		
				user = null;
				if (!isSetPassePartout) {
					user = authenticationProvider.getUser(userName, password); 
					userPwd = user;
				} else {
					user = authenticationProvider.getUser(userName);
				}
				
				boolean canAccess = false;

				if (user != null) {
					// si corregge immediatamente in caso di match case insensitive
					userName = user.getUsername();

					String denominazione = "";
					if (user.getProfile() != null && ((IUserProfile) user.getProfile()).getValue("Nome") != null) {
						denominazione = " con denominazione " + (String) ((IUserProfile) user.getProfile()).getValue("Nome");
					}

					// user.ipAddress = ipAddress 
					user.setIpAddress(ipAddress);

					evento = new Event();
					evento.setLevel(Event.Level.INFO);
					evento.setUsername(userName);
					evento.setIpAddress(ipAddress);
					evento.setSessionId(session.getId());
					evento.setEventType(PortGareEventsConstants.LOGIN);
					evento.setMessage("Autenticazione utente" + denominazione);
					if (user.isDisabled()) {
						req.setAttribute("accountDisabled", true);
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage("Utente disabilitato");
					//} else if (!user.isAccountNotExpired()) {
					} else if (session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO) == null && !user.isAccountNotExpired()) {
						// il controllo sulla scadenza account deve avvenire per utenti che non accedono mediante single sign on
						req.setAttribute("accountExpired", true);
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage("Utente scaduto per superamento tempo massimo di inattivita'");
					//} else if (!user.isCredentialsNotExpired()) {
					} else if (!user.isCredentialsNotExpired()) {
						if (session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO) == null) {
							// il controllo sulla password deve avvenire per utenti che non accedono mediante single sign on
							req.setAttribute("credentialsExpired", true);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Autenticazione utente con credenziali di accesso scadute, modificare la password");
						} else {
							// nel caso di autenticazione mediante single sign on si
							// demandano al sistema esterno di autenticazione la gestione
							// della durata della password
							((User)user).setCheckCredentials(false);
						}
						canAccess = true;
					} else {
						log.trace("New user: {}", user.getUsername());
						canAccess = true;
					}
					
					TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(req.getSession().getServletContext());
					if (canAccess) { 
						
						UserDetails userLoggato = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER); 
						boolean isAdminInAssistenza = userLoggato != null && userLoggato.getUsername().equals(SystemConstants.ADMIN_USER_NAME);
						String sessionIdUtenteConnesso = null;
						if (!isAdminInAssistenza) {
							// la verifica dell'utente gia' loggato avviene solo
							// se non ci si logga da admin per assistenza (admin
							// puo' quindi aprire una sessione di lavoro nuova
							// parallela a quella dell'utente assistito)
							sessionIdUtenteConnesso = tracker.getSessionIdUtenteConnesso(userName, session.getId());
						}
						if((user.getUsername().equals(SystemConstants.ADMIN_USER_NAME)||user.getUsername().equals(SystemConstants.SERVICE_USER_NAME)) && 
								session.getAttribute(SystemConstants.ADMIN_LOGGED)== null){
							Map<String,String> adminAttributes = new HashMap<String, String>();
							adminAttributes.put("username", user.getUsername());
							adminAttributes.put("password", user.getPassword());
							adminAttributes.put("ip", ipAddress);
							// Metto in sessione gli attributi dello user admin, da riutilizzare
							// per la login dopo il riconoscimento e' personale tramite SPID o portoken.
							// Uso una mappa anziche' l'oggetto USER per evitare la tracciatura degli eventi 
							session.setAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER, adminAttributes);
							retStatus = ControllerManager.REDIRECT;
							//Blocco la tracciatura dell'evento in quanto per tracciare la login di admin e' necessario il riconoscimento del soggetto fisico
							evento = null;
//							evento.setLevel(Event.Level.WARNING);
//							evento.setMessage("Accesso da admin anonimo in attesa di riconoscimento persona fisica");
						} else {
							session.removeAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER);
							session.removeAttribute(SystemConstants.ADMIN_LOGGED);
							if (sessionIdUtenteConnesso != null && !user.getUsername().equals(SystemConstants.ADMIN_USER_NAME)) {
								// esiste gia' l'utente connesso in un'altra sessione di lavoro, devo accedere ad una pagina di domanda su come procedere
								retStatus = ControllerManager.REDIRECT;
								AccessoSimultaneoBean accessoSimultaneo = new AccessoSimultaneoBean();
								accessoSimultaneo.setTipoAutenticazione(AccessoSimultaneoBean.TipoAutenticazione.DB);
								accessoSimultaneo.setUtenteCandidatoPortale(user);
								accessoSimultaneo.setSessionIdUtenteConnesso(sessionIdUtenteConnesso);
								session.setAttribute(AccessoSimultaneoBean.SESSION_CUNCURRENT_OBJECT_ID, accessoSimultaneo);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Accesso simultaneo da 2 postazioni");
							} else {
								// primo ed unico accesso per l'utente, setto l'utente in sessione e proseguo il regolare
								// flusso con tracciatura accessi e inserimento utente nei dati di controllo sessioni del
								// contesto applicativo
	
								session.setAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER, user);
								authenticationProvider.logLogin(userName, ipAddress);
								retStatus = ControllerManager.REDIRECT;
							
								// 31/03/2017: si aggiunge l'utente autenticatosi nella 
								//   lista contenuta nell'applicativo (deve essere 
								//   autenticazione diretta, non SSO)
								if (session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO) == null) {
									tracker.putSessioneUtente(session, ipAddress, userName, DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
								} else {
									// alla selezione dell'utente si imposta l'indirizzo 
									// ip nell'oggetto di single sign on e nei dati dal 
									// tracker sessioni
									AccountSSO delegateUser = (AccountSSO)session.getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
									delegateUser.setIpAddress(ipAddress);
									String[] info = (String[]) tracker.getDatiSessioniUtentiConnessi().get(session.getId());
									info[0] = ipAddress;
								}
							}
						}
						
					}
				} else {
					if (passwordConfirm == null) {
						req.setAttribute("wrongAccountCredential", true);
						if (user == null) {
							// se arrivo qui causa autenticazione fallita, lo user non ce l'ho, 
							// provo a vedere quindi se l'utente esiste prendendolo senza password
							user = userManager.getUser(userName);
						}
						
						String denominazione = " con denominazione " + userName;
						
						authenticationProvider.logWrongAccess(user, userName, ipAddress, currentLang);
						evento = new Event();
						evento.setUsername(userName);
						evento.setIpAddress(ipAddress);
						evento.setEventType(PortGareEventsConstants.LOGIN);
						evento.setMessage("Autenticazione utente" + denominazione);
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage("Credenziali errate");
						if (user != null && user.isDisabled()) {
							req.setAttribute("accountDisabled", true);
							evento.setLevel(Event.Level.ERROR);
							evento.setDetailMessage("Utente disabilitato");
						}
					}
				}
			}
			
			// PORTAPPALT-329: sblocco utenza autonomo dell'utente
			// se un utente disabilitato effettua un login corretto e il modulo privacy e' attivo 
			// ridireziona alla pagina per la riattivazione del login da parte dell'OE
			boolean sbloccoUtenza = false;
			if (userManager.isEnabledPrivacyModule()) {
				if(userPwd != null && !user.isDisabled() && !userPwd.isAccountNotExpired()) {
					// redirect a pagina di sblocco utente con pulsante di "ripristina"
					session.setAttribute(SystemConstants.SESSION_SBLOCCO_UTENZA_AUTONOMO, userPwd.getUsername());
					retStatus = ControllerManager.REDIRECT;
					sbloccoUtenza = true;
				}
			} else {
				session.removeAttribute(SystemConstants.SESSION_SBLOCCO_UTENZA_AUTONOMO);
			}
			
			if( !sbloccoUtenza ) {
				if (session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER) == null) {
					UserDetails guestUser = userManager.getGuestUser();
					session.setAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER, guestUser);
				}
				
				/* --- Attivazione SSO --- */
				
				int ssoProtocollo = 0; //di default l'utilizzo di sso e' disabilitato
				Integer configurazioneSSOProtocollo = (Integer)appParamManager.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO);
				if(configurazioneSSOProtocollo != null) {
					if(configurazioneSSOProtocollo.intValue() >= PortGareSystemConstants.MIN_NUM_SSO_PROTOCOLLO && 
					   configurazioneSSOProtocollo <= PortGareSystemConstants.MAX_NUM_SSO_PROTOCOLLO) {
						ssoProtocollo = configurazioneSSOProtocollo.intValue();
					}
				}
				
				req.setAttribute("ssoProtocollo", ssoProtocollo);
				// Integro il servizio SPID a prescindere dal ssoProtocollo, in quanto utilizzato nella pagina di login da amministratore
				String ssoLoginUrl = (String)appParamManager.getConfigurationValue("sso.login.url");
				if(ssoProtocollo == PortGareSystemConstants.SSO_PROTOCOLLO_SPID_MAGGIOLI) {
					// integrazione servizio SPID di Maggioli...
					ssoLoginUrl = "SpidLogin";
				}	
				req.setAttribute("urlLoginSSO", ssoLoginUrl);
				req.setAttribute("urlLogoutSSO", (String)appParamManager.getConfigurationValue("sso.logout.url"));
	
				if(isSuspendedIp && userName != null && StringUtils.isNotEmpty(userName)) {
					// non sono ancora trascorsi i minuti di blocco, pertanto...
			        // ...si rallenta l'esecuzione per evitare che sia un eventuale 
					// attacco di brute force...
					int numSecondiDelayLoginBloccata = 10;
					Thread.sleep(numSecondiDelayLoginBloccata*1000);
				}
				
				// Spid Validator
				Integer spidValidator = (Integer)appParamManager.getConfigurationValue("sso.spid.validator");
				if(spidValidator == null) {
					spidValidator = new Integer(0);
				}
				req.setAttribute("spidValidatorVisible", spidValidator);
			}
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, sender, "service", "Error, could not fulfill the request");
			retStatus = ControllerManager.ERROR;
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, sender, "service", "Error, could not fulfill the request");
			retStatus = ControllerManager.ERROR;
		} finally {
			if (evento != null) {
				eventManager.insertEvent(evento);
			}
		}
		return retStatus;
	}

	/**
	 * Get the minutes difference
	 *
	 * @param earlierDate la data precendente
	 * @param laterDate la data successiva
	 * @return la differenza in minuti
	 *
	 */
	private static int minutesDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null) {
			return 0;
		}
		return (int) ((laterDate.getTime() / MINUTE_MILLIS) - (earlierDate.getTime() / MINUTE_MILLIS));
	}

	/**
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean checkIpSuspended(
			HttpServletRequest req, 
			String ipAddress,
			ApplicationContext applicationContext,			
			IUserManager userManager,
			IAuthenticationProviderManager authenticationProvider
			) throws ApsSystemException {

		Map suspendedIPList = (HashMap) applicationContext.getBean("SuspendedIP");
		GregorianCalendar now = new GregorianCalendar();
		boolean suspended = false;
		String message = ResourceBundle.getBundle("com.agiletec.apsadmin.common.package", req.getLocale()).getString("suspendedIpMessage");

		if (suspendedIPList.containsKey(ipAddress) && now.after((GregorianCalendar) suspendedIPList.get(ipAddress))) {

			suspended = false;
			userManager.clearAllWrongAccessAttemptsByIp(ipAddress);
			suspendedIPList.remove(ipAddress);

		} else if (suspendedIPList.containsKey(ipAddress) && now.before((GregorianCalendar) suspendedIPList.get(ipAddress))) {

			suspended = true;
			req.setAttribute("suspendedIp", true);
			message = message.replace("{0}", minutesDiff(now.getTime(), ((GregorianCalendar) suspendedIPList.get(ipAddress)).getTime()) > 0
							? (minutesDiff(now.getTime(), ((GregorianCalendar) suspendedIPList.get(ipAddress)).getTime()) + "")
							: ResourceBundle.getBundle("com.agiletec.apsadmin.common.package", req.getLocale()).getString("lessThanOne"));
			message = message + " " + (minutesDiff(now.getTime(), ((GregorianCalendar) suspendedIPList.get(ipAddress)).getTime()) > 1
							? ResourceBundle.getBundle("com.agiletec.apsadmin.common.package", req.getLocale()).getString("minutes")
							: ResourceBundle.getBundle("com.agiletec.apsadmin.common.package", req.getLocale()).getString("minute"));
			req.setAttribute("suspendedIpMessage", message);

		} else if (authenticationProvider.tooManyWrongIpAccessAttempts(ipAddress)) {

			GregorianCalendar ipInhibitionEndTime = new GregorianCalendar();
			ipInhibitionEndTime.add(GregorianCalendar.MINUTE, authenticationProvider.getInhibitionTimeAccess());
			((HashMap) applicationContext.getBean("SuspendedIP")).put(ipAddress, ipInhibitionEndTime);
			suspended = true;
			req.setAttribute("suspendedIp", true);
			message = message.replace("{0}", authenticationProvider.getInhibitionTimeAccess() + "");
			message = message + " " + (authenticationProvider.getInhibitionTimeAccess() > 1
							? ResourceBundle.getBundle("com.agiletec.apsadmin.common.package", req.getLocale()).getString("minutes")
							: ResourceBundle.getBundle("com.agiletec.apsadmin.common.package", req.getLocale()).getString("minute"));
			req.setAttribute("suspendedIpMessage", message);

		}
		return suspended;
	}

}
