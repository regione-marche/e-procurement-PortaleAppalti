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
package com.agiletec.aps.system.services.user;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BotFilter;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.authorization.IApsAuthority;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.authorization.authorizator.IApsAuthorityManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.user.jpmail_wrongaccess_config.IWrongAccessConfig;
import com.agiletec.aps.system.services.user.jpmail_wrongaccess_config.WrongAccessConfigDOM;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegDAO;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.parse.UserRegConfigDOM;
import com.opensymphony.xwork2.ActionContext;

/**
 * Implementazione concreta dell'oggetto Authentication Provider di default del
 * sistema. L'Authentication Provider e' l'oggetto delegato alla restituzione di
 * un'utenza (comprensiva delle sue autorizzazioni) in occasione di una
 * richiesta di autenticazione utente; questo oggetto non ha visibilita' ai
 * singoli sistemi (concreti) delegati alla gestione delle autorizzazioni, ma
 * possiede una referenza alla lista (astratta) di "Authorizators" e al Gestore
 * Utenti (per il recupero dell'utenza base).
 *
 * @author E.Santoboni
 */
public class AuthenticationProviderManager extends AbstractService implements IAuthenticationProviderManager {

	@Override
	public void init() throws Exception {
		int maxLoginAttemptsWrongPassword = this.getUserManager().extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_MAX_LOGIN_ATTEMPTS_WRONG_PASSWORD, 5);
		this.setMaxLoginAttemptsWrongPassword(maxLoginAttemptsWrongPassword);
		int maxLoginAttemptsSameIp = this.getUserManager().extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_MAX_LOGIN_ATTEMPTS_FROM_SAME_IP, 10);
		this.setMaxLoginAttemptsSameIp(maxLoginAttemptsSameIp);
		int inhibitionIpTimeInMinutes = this.getUserManager().extractNumberParamValue(SystemConstants.CONFIG_PARAM_PM_MM_INHIBITION_IP_TIME_IN_MINUTES, 10);
		this.setInhibitionIpTimeInMinutes(inhibitionIpTimeInMinutes);
		String enabledPrivacyModuleParValue = this.getConfigManager().getParam(SystemConstants.CONFIG_PARAM_PM_ENABLED);
		this.setEnabledPrivacyModule(Boolean.parseBoolean(enabledPrivacyModuleParValue));
		this.loadConfigs();
		ApsSystemUtils.getLogger().debug("{}: initialized", this.getClass().getName());
	}

	private void loadConfigs() throws ApsSystemException {
		try {
			ConfigInterface configManager = this.getConfigManager();
			Lang defaultLang = this.getLangManager().getDefaultLang();
			String xml = configManager.getConfigItem("jpauthenticator_Config");
			if (xml == null) {
				throw new ApsSystemException("Configuration Item not found: jpmail_wrongAccess_Config");
			}
			WrongAccessConfigDOM wrongAccessConfigDOM = new WrongAccessConfigDOM(xml);
			IWrongAccessConfig waConfig = wrongAccessConfigDOM.getConfig();
			waConfig.setDefaultLang(defaultLang);
			this.setWrongAccessConfig(waConfig);
			xml = configManager.getConfigItem("jpuserreg_Config");
			if (xml == null) {
				throw new ApsSystemException("Configuration Item not found: jpmail_wrongAccess_Config");
			}
			UserRegConfigDOM userRegConfigDOM = new UserRegConfigDOM(xml);
			IUserRegConfig urConfig = userRegConfigDOM.getConfig();
			urConfig.setDefaultLang(defaultLang);
			this.setUserRegConfig(urConfig);
		} catch (ApsSystemException t) {
			ApsSystemUtils.logThrowable(t, this, "loadConfigs");
			throw new ApsSystemException("Error in init", t);
		}
	}

	@Override
	public UserDetails getUser(String username, String password) throws ApsSystemException {

		UserDetails user = null;
		try {
			user = this.getUserManager().getUser(username, password);
			if (null == user) {
				return null;
			}
			if (user.isDisabled()) {
				ApsSystemUtils.getLogger().info("USER ACCOUNT ''{}'' DISABLED", user.getUsername());
			}
			if (!user.isAccountNotExpired()) {
				ApsSystemUtils.getLogger().info("USER ACCOUNT ''{}'' EXPIRED", user.getUsername());
			}
			if (!user.isCredentialsNotExpired()) {
				ApsSystemUtils.getLogger().info("USER ''{}'' credentials EXPIRED", user.getUsername());
			}
			if (user.getUsername().equals(SystemConstants.ADMIN_USER_NAME)
					|| user.getUsername().equals(SystemConstants.ENTE_ADMIN_USER_NAME)
					|| user.getUsername().equals(SystemConstants.SERVICE_USER_NAME)
					|| user.isAccountNotExpired()) {
				this.getUserManager().updateLastAccess(user);
				if (user.getUsername().equals(SystemConstants.ADMIN_USER_NAME) 
					|| user.getUsername().equals(SystemConstants.ENTE_ADMIN_USER_NAME)
					|| user.getUsername().equals(SystemConstants.SERVICE_USER_NAME)
					|| user.isCredentialsNotExpired()) {
					this.addUserAuthorizations(user);
				}
			}
			
			String crc = getUserManager().calculateUserCrc(user.getUsername(), user.getPassword());
			if(user.getCrc() == null){
				this.getUserManager().setUserCrc(user.getUsername(), crc);
			} else {
				if(!crc.equals(user.getCrc())){
					return null;
				}
			}

		} catch (ApsSystemException t) {
			throw new ApsSystemException("Error detected during the authentication of the user " + username, t);
		}
		return user;
	}

	private boolean isAdmin(UserDetails user){
		IAuthorizationManager authManager = this.getAuthorizationManager();
		return authManager.isAuthOnGroup(user, Group.ADMINS_GROUP_NAME);
	}
	
	@Override
	public UserDetails getUser(String username) throws ApsSystemException {

		UserDetails user = null;
		try {
			user = this.getUserManager().getUser(username);
			if (null == user) {
				return null;
			}
			if (user.isDisabled()) {
				ApsSystemUtils.getLogger().info("USER ACCOUNT ''{}'' DISABLED", user.getUsername());
			}
			if (!user.isAccountNotExpired()) {
				ApsSystemUtils.getLogger().info("USER ACCOUNT ''{}'' EXPIRED", user.getUsername());
			}
			if (!user.isCredentialsNotExpired()) {
				ApsSystemUtils.getLogger().info("USER ''{}'' credentials EXPIRED", user.getUsername());
			}
			// nel caso di accesso per assistenza non si cambia la data di ultimo accesso e si impostano le autorizzazioni in modo incondizionato
			//if (user.getUsername().equals(SystemConstants.ADMIN_USER_NAME) || user.isAccountNotExpired()) {
				//this.getUserManager().updateLastAccess(user);
				//if (user.getUsername().equals(SystemConstants.ADMIN_USER_NAME) || user.isCredentialsNotExpired()) {
					this.addUserAuthorizations(user);
				//}
			//}
		} catch (ApsSystemException t) {
			throw new ApsSystemException("Error detected during the authentication of the user " + username, t);
		}
		return user;
	}
	
	@Override
	public UserDetails getUser(UserDetails user) throws ApsSystemException {
		UserDetails u = null;
		try {
			u = this.getUserManager().getUser(user);
			if (null == u) {
				return null;
			}
			// nel caso di accesso per assistenza non si cambia la data di ultimo accesso e si impostano le autorizzazioni in modo incondizionato
			//if (user.getUsername().equals(SystemConstants.ADMIN_USER_NAME) || user.isAccountNotExpired()) {
				//this.getUserManager().updateLastAccess(user);
				//if (user.getUsername().equals(SystemConstants.ADMIN_USER_NAME) || user.isCredentialsNotExpired()) {
					this.addUserAuthorizations(user);
				//}
			//}
		} catch (ApsSystemException t) {
			throw new ApsSystemException("Error detected during the authentication of the user " + user.getUsername(), t);
		}
		return user;
	}

	protected void addUserAuthorizations(UserDetails user) throws ApsSystemException {

		if (null == user) {
			return;
		}
		//setta autorizzazioni "interne"
		for (int i = 0; i < this.getAuthorizators().size(); i++) {
			IApsAuthorityManager authorizator = this.getAuthorizators().get(i);
			List<IApsAuthority> auths = authorizator.getAuthorizationsByUser(user);
			user.addAutorities(auths);
			/*
			 Nel caso che le autorizzazioni vengano settate all'esterno dell'applicazione, 
			 bisogna prevedere un elemento di aggancio alle autorizzazioni remote.
			 L'oggetto non puo' essere in questo caso generalizzabile, in quanto presuppone l'interazione diretta con il sistema esterno e 
			 con la logica di assegnazione delle autorizzazioni che man mano vengono stabilite caso per caso nel sistema centrale.
			 */
		}
	}

	@Override
	public List<UserDetails> getUsers() throws ApsSystemException {
		return this.getUserManager().getUsers();
	}
	
	@Override
	public List<String[]> getLoggedUsers() throws ApsSystemException {
		return this.getUserManager().getLoggedUsers();
	}
		
	@Override
	public int logLogin(String username, String delegate, String ipAddress, String sessionId) throws ApsSystemException {
		int exitCode = 0;
		if (StringUtils.isNotBlank(username) && (StringUtils.isNotBlank(ipAddress) || StringUtils.isNotBlank(sessionId))) {
			exitCode = this.getUserManager().logLogin(username, delegate, ipAddress, sessionId);
			this.getUserManager().clearWrongAccessAttempts(username, ipAddress, sessionId);
		}
		return exitCode;
	}

	@Override
	public boolean logLogout(String username, String delegate, String ipAddress, String sessionId) throws ApsSystemException {
		boolean logout = false;
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(ipAddress)) {
			logout = this.getUserManager().logLogout(username, delegate, ipAddress, sessionId);
		}
		return logout;
	}

	@Override
	public void logWrongAccess(
			UserDetails user, 
			String username, 
			String ipAddress, 
			String sessionId, 
			String currentLang) throws ApsSystemException 
	{
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(ipAddress)) {
			this.getUserManager().logWrongAccess(user != null ? user.getUsername() : username, ipAddress, sessionId);
			
			// se manca "user" non viene registrato l'evento e non viene inviata la mail ad admin 
			if (this.isEnabledPrivacyModule() && user != null
				&& this.getUserManager().tooManyWrongPasswordAccessAttempts(user.getUsername(), this.getMaxLoginAttemptsWrongPassword())) 
			{
				Event evento = new Event();
				evento.setUsername(username);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.BLOCCO_UTENTE);
				evento.setIpAddress(ipAddress);
				evento.setMessage("Blocco utenza e IP per superamento limite (" + this.getMaxLoginAttemptsWrongPassword() + ") di tentativi di login falliti");
				
				// prepara l'evento per la generazione del token di riattivazione allegato alla mail inviata all'utente 
				Event eventoToken = new Event();
				eventoToken.setLevel(Level.INFO);
				eventoToken.setEventType(PortGareEventsConstants.GENERAZIONE_TOKEN);
				eventoToken.setMessage("Generazione del token di riattivazione per l'utente " + username);
				eventoToken.setUsername(username);
				eventoToken.setIpAddress(ipAddress);
				//eventoToken.setSessionId(???);

				try {
					// errata password per piu' di X volte
					((User) user).setDisabled(true);
					this.getUserManager().updateUser(user);
					
					// invio mail all'utente
					try {
						sendEmailUserDisabledTooManyWrongAttempts(user.getUsername(), USER_RECEIVER, currentLang);
					} catch (Throwable ex) {
						eventoToken.setError(ex);
						ApsSystemUtils.logThrowable(ex, this, "logWrongAccess");
					}
					
					// invio mail ad amministratore
					try {
						String defLang = null;
						Lang defaultLang = this.getLangManager().getDefaultLang();
						if(defaultLang != null) {
							defLang = defaultLang.getCode();
						}
						if (StringUtils.isNotBlank((String) this.getAppParamManager()
								.getConfigurationValue(AppParamManager.MAIL_AMMINISTRATORE_SISTEMA))) {
							sendEmailUserDisabledTooManyWrongAttempts(user.getUsername(), ADMIN_RECEIVER, defLang);
						}
					} catch (Throwable ex) {
						ApsSystemUtils.logThrowable(ex, this, "logWrongAccess");
					}
				} catch (ApsSystemException t) {
					evento.setError(t);
					ApsSystemUtils.logThrowable(t, this, "logWrongAccess");
					throw new ApsSystemException("Error while logging the log attempt of the User " + (user != null ? user.getUsername() : ""), t);
				} finally {
					this.eventManager.insertEvent(evento);
					this.eventManager.insertEvent(eventoToken);
				}
			}
		}
	}

	private void sendEmailUserDisabledTooManyWrongAttempts(String username, String receiver, String currentLang) throws ApsSystemException {
		try {
			// genera il nuovo token di riattivazione solo per l'operatore economico e non per "admin"
			String token = (receiver.equals(ADMIN_RECEIVER)
							? null 
							: this.userRegManager.getReactivationToken(username));
			
			IUserProfile userProfile = this.getUserProfileManager().getProfile(username);
			Map<String, String> params = this.prepareProfileParamsUserDisabledMail(username, userProfile, token);
			String[] eMail = {receiver.equals(ADMIN_RECEIVER) 
							  	? (String) this.getAppParamManager().getConfigurationValue(AppParamManager.MAIL_AMMINISTRATORE_SISTEMA)
							  	: (String) userProfile.getValue(this.getWrongAccessConfig().getProfileEMailAttr())};
			String langCode = currentLang;		//getProfileLangAttr(userProfile);
			String subject = receiver.equals(ADMIN_RECEIVER) 
								? this.getWrongAccessConfig().getUserDisabledAdminMailSubject(langCode)
								: this.getWrongAccessConfig().getUserDisabledUserMailSubject(langCode);
			String text = this.prepareBody(receiver.equals(ADMIN_RECEIVER) 
								? this.getWrongAccessConfig().getUserDisabledAdminMailBody(langCode)
								: this.getWrongAccessConfig().getUserDisabledUserMailBody(langCode), 
								params);
			this.getMailManager().sendMail(text, subject, eMail, null, null, this.getWrongAccessConfig().getEMailSenderCode());
		} catch (ApsSystemException t) {
			ApsSystemUtils.logThrowable(t, this, "sendEmailUserDisabledTooManyWrongAttempts");
			throw new ApsSystemException("Error while sending email to admin", t);
		}
	}

	private Map<String, String> prepareProfileParamsUserDisabledMail(String userName, IUserProfile userProfile, String token) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", (String) userProfile.getValue(this.getWrongAccessConfig().getProfileNameAttr()));
		params.put("surname", (String) userProfile.getValue(this.getWrongAccessConfig().getProfileSurnameAttr()));
		params.put("userName", userName);
		params.put("attempts", this.getMaxLoginAttemptsWrongPassword() + "");
		params.put("applicationBaseUrl", this.getConfigManager().getParam(SystemConstants.PAR_APPL_BASE_URL));
		if(StringUtils.isNotEmpty(token)) {
			String reactivationPageCode = this.getUserRegConfig().getReactivationPageCode();
			String profileLangAttr = this.getLangForLinkCreation(userProfile);
			if(token != null){
				String link = this.createLink(reactivationPageCode, userName, token, profileLangAttr);
				params.put("link", link);
			}
		}
		return params;
	}

	private Map<String, String> prepareProfileParamsIpInhibiteddMail(String ipAddress) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("attempts", this.getMaxLoginAttemptsSameIp() + "");
		params.put("ipAddress", ipAddress);
		params.put("minutes", this.getInhibitionIpTimeInMinutes() + "");
		params.put("applicationBaseUrl", this.getConfigManager().getParam(SystemConstants.PAR_APPL_BASE_URL));
		return params;
	}

	private String getProfileLangAttr(IUserProfile userProfile) {
		String profileLangAttr = (String) userProfile.getValue(this.getWrongAccessConfig().getProfileLangAttr());
		if (null == profileLangAttr || profileLangAttr.length() == 0) {
			Lang defaultLang = this.getLangManager().getDefaultLang();
			profileLangAttr = defaultLang.getCode();
		}
		return profileLangAttr;
	}
	
	private String getLangForLinkCreation(IUserProfile profile) {
		String profileLangAttr = (String) profile.getValue(this.getUserRegConfig().getProfileLangAttr());
		if (null == profileLangAttr || profileLangAttr.length() == 0 ) {
			Lang defaultLang = this.getLangManager().getDefaultLang();
			profileLangAttr = defaultLang.getCode();
		}
		return profileLangAttr;
	}
	
	/**
	 * Create link for email confirmation
	 */
	protected String createLink(String pageCode, String userName, String token, String langcode) {
		StringBuffer link = new StringBuffer(this.getConfigManager().getParam(SystemConstants.PAR_APPL_BASE_URL)); // this.getCompleteApplbaseUrl());
		link.append(langcode);
		link.append("/");
		link.append(pageCode);
		link.append(".wp");
		link.append("?token=");
		link.append(token);
		return link.toString();
	}

	private String prepareBody(String defaultText, Map params) {
		String body = defaultText;
		StringBuffer strBuff;
		Iterator it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String field = "{" + pairs.getKey() + "}";
			int start = body.indexOf(field);
			if (start > 0) {
				int end = start + field.length();
				strBuff = new StringBuffer();
				strBuff.append(body.substring(0, start));
				strBuff.append(pairs.getValue());
				strBuff.append(body.substring(end));
				body = strBuff.toString();
			}
		}
		return body;
	}

	@Override
	public void clearAllWrongAccessAttempts(String username) throws ApsSystemException {
		try {
			this.getUserManager().clearAllWrongAccessAttemptsByUsername(username);
			//invio mail all'utente
			sendEmailUserReactivated(username);
		} catch (ApsSystemException t) {
			ApsSystemUtils.logThrowable(t, this, "clearAllWrongAccessAttempts");
			throw new ApsSystemException("Error cleaning all wrong access attempts ", t);
		}
	}

	/**
	 * Si espone un metodo che invia una mail ad un destinatario diverso da
	 * quello individuato dallo username abilitato.
	 * 
	 * @param username
	 *            username da abilitare
	 * @param mail
	 *            indirizzo mail del destinatario da informare (se nullo si
	 *            invia all'indirizzo mail contenuto nel profilo individuato
	 *            dallo username)
	 * @throws ApsSystemException
	 */
	public void sendEmailUserReactivated(String username, IUserProfile userProfile, String mail) throws ApsSystemException {
		try {
			Map<String, String> params = this.prepareProfileParamsUserDisabledMail(username, userProfile, null);
	
			String[] eMail = {(String) userProfile.getValue(this.getUserRegConfig().getProfileEMailAttr())};
			if (mail != null) {
				eMail = new String[] {mail};
			}
			String langCode = getProfileLangAttr(userProfile);
			String subject = this.getUserRegConfig().getUserReactivatedMailSubject(langCode);
			String text = this.prepareBody(this.getUserRegConfig().getUserReactivatedMailBody(langCode), params);
			this.getMailManager().sendMail(text, subject, eMail, null, null, this.getUserRegConfig().getEMailSenderCode());
		} catch (ApsSystemException t) {
			ApsSystemUtils.logThrowable(t, this, "sendEmailUserReactivated");
			throw new ApsSystemException("Error while sending email to user", t);
		}
	}

	private void sendEmailUserReactivated(String username) throws ApsSystemException {
		// spostata l'intera logica dentro il metodo pubblico, per garantire la retrocompatibilita' si passa
		// il nuovo (secondo) parametro a null
		IUserProfile userProfile = this.getUserProfileManager().getProfile(username);
		this.sendEmailUserReactivated(username, userProfile, null);
	}
		
	private void sendEmailIpInhibited(String ipAddress) throws ApsSystemException {
		try {
			if (StringUtils.isNotBlank((String) this.getAppParamManager().getConfigurationValue(AppParamManager.MAIL_AMMINISTRATORE_SISTEMA))) {
				Map<String, String> params = this.prepareProfileParamsIpInhibiteddMail(ipAddress);
				String[] eMail = {(String) this.getAppParamManager().getConfigurationValue(AppParamManager.MAIL_AMMINISTRATORE_SISTEMA)};
				String langCode = this.getLangManager().getDefaultLang().getCode();
				String subject = this.getWrongAccessConfig().getIpSuspendedAdminMailSubject(langCode);
				String text = this.prepareBody(this.getWrongAccessConfig().getIpSuspendedAdminMailBody(langCode), params);
				this.getMailManager().sendMail(text, subject, eMail, null, null, this.getWrongAccessConfig().getEMailSenderCode());
			}
		} catch (ApsSystemException t) {
			ApsSystemUtils.logThrowable(t, this, "sendEmailIpInhibited");
			throw new ApsSystemException("Error while sending email to admin", t);
		}
	}

	@Override
	public boolean tooManyWrongIpAccessAttempts(String ipAddress) throws ApsSystemException {
		boolean esito = false;
		if (this.isEnabledPrivacyModule() && this.getUserManager().tooManyWrongIpAccessAttempts(ipAddress, this.getMaxLoginAttemptsSameIp())) {
			Event evento = new Event();
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.BLOCCO_IP);
			evento.setIpAddress(ipAddress);
			evento.setMessage("Blocco IP per superamento limite (" + this.getMaxLoginAttemptsSameIp() + ") di tentativi falliti di login");
			try {
				sendEmailIpInhibited(ipAddress); 
				esito = true;
			} catch (ApsSystemException t) {
				evento.setError(t);
				ApsSystemUtils.logThrowable(t, this, "tooManyWrongIpAccessAttempts");
				throw new ApsSystemException("Error while checking ip access attempts", t);
			} finally {
				eventManager.insertEvent(evento);
			}
		}
		return esito;
	}

	@Override
	public boolean honeypotAttempt(String ipAddress) throws ApsSystemException {
		boolean esito = false;
		
		if (this.isEnabledPrivacyModule()) {
			Event evento = new Event();
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.BLOCCO_BOT);
			evento.setIpAddress(ipAddress);
			evento.setMessage("Inizio blocco IP per tentativo di accesso a honeypot");
			try {
				// aggiungi l'ip alla blacklist degli ip da bloccare...
//				Map<String, Object> application = ActionContext.getContext().getApplication();
				
//				Map<String, Date> blacklist = (Map<String, Date>)application.get(BotFilter.HONEYPOT_BLACK_LIST);
				Map<String, Date> blacklist = BotFilter.getBlackList();
				if(blacklist == null) {
					blacklist = new HashMap<String, Date>();
				}
				blacklist.put(ipAddress, new Date());
				
//				application.put(BotFilter.HONEYPOT_BLACK_LIST, blacklist);
				
				esito = true;
			} catch (Throwable t) {
				evento.setError(t);
				ApsSystemUtils.logThrowable(t, this, "honeypotAttempt");
				throw new ApsSystemException("Error while checking ip access attempts", t);
			} finally {
				this.eventManager.insertEvent(evento);
			}
		} else {
			// se il modulo privacy e' disabilitato, si svuota la blacklist
//			Map<String, Object> application = ActionContext.getContext().getApplication();
			
//			Map<String, Date> blacklist = (Map<String, Date>)application.get(BotFilter.HONEYPOT_BLACK_LIST);
			Map<String, Date> blacklist = BotFilter.getBlackList();
			if(blacklist != null) {
				blacklist.clear();
//				application.put(BotFilter.HONEYPOT_BLACK_LIST, blacklist);
			}
		}
		return esito;
	}

	@Override
	public int getInhibitionTimeAccess() throws ApsSystemException {
		return this.getInhibitionIpTimeInMinutes();
	}

	protected IMailManager getMailManager() {
		return _mailManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this._mailManager = mailManager;
	}

	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}

	protected ILangManager getLangManager() {
		return _langManager;
	}

	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this._userProfileManager = userProfileManager;
	}

	protected IUserProfileManager getUserProfileManager() {
		return _userProfileManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this._appParamManager = appParamManager;
	}

	protected IAppParamManager getAppParamManager() {
		return _appParamManager;
	}

	protected IWrongAccessConfig getWrongAccessConfig() {
		return _wrongAccessConfig;
	}

	public void setWrongAccessConfig(IWrongAccessConfig wrongAccessConfig) {
		this._wrongAccessConfig = wrongAccessConfig;
	}

	protected IUserRegConfig getUserRegConfig() {
		return _userRegConfig;
	}

	public void setUserRegConfig(IUserRegConfig userRegConfig) {
		this._userRegConfig = userRegConfig;
	}

	public int getMaxLoginAttemptsWrongPassword() {
		return _maxLoginAttemptsWrongPassword;
	}

	public void setMaxLoginAttemptsWrongPassword(int maxLoginAttemptsWrongPassword) {
		this._maxLoginAttemptsWrongPassword = maxLoginAttemptsWrongPassword;
	}

	public int getMaxLoginAttemptsSameIp() {
		return _maxLoginAttemptsSameIp;
	}

	public void setMaxLoginAttemptsSameIp(int maxLoginAttemptsSameIp) {
		this._maxLoginAttemptsSameIp = maxLoginAttemptsSameIp;
	}

	public void setInhibitionIpTimeInMinutes(int inhibitionIpTimeInMinutes) {
		this._inhibitionIpTimeInMinutes = inhibitionIpTimeInMinutes;
	}

	public int getInhibitionIpTimeInMinutes() {
		return _inhibitionIpTimeInMinutes;
	}

	protected IUserManager getUserManager() {
		return _userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}

	protected ConfigInterface getConfigManager() {
		return _configManager;
	}

	public void setConfigManager(ConfigInterface configManager) {
		this._configManager = configManager;
	}

	protected List<IApsAuthorityManager> getAuthorizators() {
		return _authorizators;
	}

	public void setAuthorizators(List<IApsAuthorityManager> authorizators) {
		this._authorizators = authorizators;
	}

	public boolean isEnabledPrivacyModule() {
		return _enabledPrivacyModule;
	}

	public void setEnabledPrivacyModule(boolean _enabledPrivacyModule) {
		this._enabledPrivacyModule = _enabledPrivacyModule;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	protected IAuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	
	protected IUserRegManager getUserRegManager() {
		return userRegManager;
	}

	public void setUserRegManager(IUserRegManager userRegManager) {
		this.userRegManager = userRegManager;
	}


	private boolean _enabledPrivacyModule;
	private List<IApsAuthorityManager> _authorizators;
	private int _maxLoginAttemptsWrongPassword = -1;
	private int _maxLoginAttemptsSameIp = -1;
	private int _inhibitionIpTimeInMinutes;
	private static final String ADMIN_RECEIVER = "ADMIN";
	private static final String USER_RECEIVER = "USER";

	private ConfigInterface _configManager;
	private IUserManager _userManager;
	private IMailManager _mailManager;
	private IWrongAccessConfig _wrongAccessConfig;
	private IUserRegConfig _userRegConfig;
	private ILangManager _langManager;
	private IUserProfileManager _userProfileManager;
	private IAppParamManager _appParamManager;
	
	private IEventManager eventManager;	
	private IAuthorizationManager authorizationManager;
	private IUserRegManager userRegManager;

}
