/*
*
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
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
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jpuserreg.aps.system.services.userreg;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.authorization.authorizator.IApsAuthorityManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.i18n.I18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.role.IRoleManager;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.UserRegBean;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.parse.UserRegConfigDOM;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.util.ShaEncoder;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import com.agiletec.aps.system.services.i18n.II18nManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Manager for operations of user account registration 
 * 
 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 */
@Aspect
public class UserRegManager extends AbstractService implements IUserRegManager {

	private IEventManager eventManager;

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	@Override
	public void init() throws Exception {
		this.loadConfigs();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": inizializzato");
	}
	
	private void loadConfigs() throws ApsSystemException {
		try {
			ConfigInterface configManager = this.getConfigManager();
			String xml = configManager.getConfigItem("jpuserreg_Config");
			if (xml == null) {
				throw new ApsSystemException("Configuration Item not found: jpuserreg_Config");
			}
			UserRegConfigDOM userRegConfigDom = new UserRegConfigDOM(xml);
			Lang defaultLang = this.getLangManager().getDefaultLang();
			IUserRegConfig config = userRegConfigDom.getConfig();
			config.setDefaultLang(defaultLang);
			this.setUserRegConfig(config);
			this.setTokenValidityInMillis(config.getTokenValidityMinutes()*60000L);
			String applBaseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
			this.setCompleteApplbaseUrl(applBaseUrl);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadConfigs");
			throw new ApsSystemException("Error in init", t);
		}
	}
	
	@AfterReturning(
			pointcut="execution(* com.agiletec.aps.system.services.user.IUserManager.removeUser(..)) && args(key)")
	public void deleteUser(Object key) {
		String username = null;
		if (key instanceof String) {
			username = key.toString();
		} else if (key instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) key;
			username = userDetails.getUsername();
		}
		if (username != null) {
			try {
				this.getUserRegDAO().clearTokenByUsername(username);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "deleteUser", "Error removing username " + username);
			}
		}
	}
	
	@Override
	public void regAccount(UserRegBean regAccountBean) throws ApsSystemException {
		try {
			User user = new User();
			user.setDisabled(true);
			user.setUsername(regAccountBean.getUsername());
			user.setProfile(regAccountBean.getUserProfile());
			user.setPassword("P@sSw0rd$ToCh@nge!");
			String token = this.createToken(regAccountBean.getUsername());
			this.sendAlertRegProfile(user.getUsername(), (IUserProfile) user.getProfile(), token);
			this._userManager.addUser(user);
			this.getUserRegDAO().addActivationToken(regAccountBean.getUsername(), token, new Date(), IUserRegDAO.ACTIVATION_TOKEN_TYPE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "regAccount");
			throw new ApsSystemException("Error in Account registration", t);
		}
	}
	
	@Override
	public void reactivationByEmail(String email) throws ApsSystemException {
		try {
			EntitySearchFilter[] filters = { new EntitySearchFilter(this.getUserRegConfig().getProfileEMailAttr(), true, email, false) };
			List<String> usernames = ((ApsEntityManager)this.getUserProfileManager()).searchId(filters);
			Iterator usernamesIter = usernames.iterator();
			while (usernamesIter.hasNext()) {
				String userName = (String) usernamesIter.next();
				this.reactivationByUserName(userName);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reactivationByEmail");
			throw new ApsSystemException("Error in request for Account Reactivation", t);
		}
	}
	
	@Override
	public void activateUser(String username, String password, String token) throws Exception {
		try {
			this.clearOldAccountRequests();
			
			// FIX: rimuovi eventuali occorrenze in "authusergroups" che non dovrebbero esserci!!!
			this.clearUserDefaultGroups(username);

			User user = (User) this.getUserManager().getUser(username);
			user.setLastPasswordChange(new Date());
			user.setPassword(password);
			user.setDisabled(false);
			this.loadUserDefaultRoles(user);
			this.loadUserDefaultGroups(user);
			this.getUserManager().updateUser(user);
			this.getUserManager().changePassword(username, password);
			this.getUserRegDAO().removeConsumedToken(token);
		} catch (Exception e) {
			throw e;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "activateUser");
			throw new ApsSystemException("Error in Account activation", t);
		}
	}
	
	@Override
	public void reactivateUser(String username, String password, String token) throws Exception {
		try {
			this.clearOldAccountRequests();
			User user = (User) this.getUserManager().getUser(username);
			user.setLastPasswordChange(new Date());
//			user.setPassword(password);	//Aggiornava l'utente con la Password in chiaro
			user.setDisabled(false);
			this.getUserManager().updateUser(user);
			this.getUserManager().changePassword(username, password);
			this.getUserRegDAO().removeConsumedToken(token);
		} catch (Exception e) {
			throw e;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reactivateUser");
			throw new ApsSystemException("Error in Account activation", t);
		}
	}
	
	protected void loadUserDefaultRoles(User user) throws ApsSystemException {
		Set<String> roleNames = this.getUserRegConfig().getRoles();
		if (null != roleNames) {
			Iterator<String> it = roleNames.iterator();
			while (it.hasNext()) {
				String rolename = it.next();
				Role role = this.getRoleManager().getRole(rolename);
				((IApsAuthorityManager) this.getRoleManager()).setUserAuthorization(user.getUsername(), role);
			}
		}
	}
	
	protected void loadUserDefaultGroups(User user) throws ApsSystemException {
		Set<String> groupNames = this.getUserRegConfig().getGroups();
		if (null != groupNames) {
			Iterator it = groupNames.iterator();
			while (it.hasNext()) {
				Group group = this.getGroupManager().getGroup((String) it.next());
				((IApsAuthorityManager) this.getGroupManager()).setUserAuthorization(user.getUsername(), group);
			}
		}
	}
	
	/**
	 * FIX: attivazione utente
	 * rimuovi eventuali occorrenze in "authusergroups" che non dovrebbero esserci
	 */
	private void clearUserDefaultGroups(String username) throws ApsSystemException {
		Set<String> groupNames = this.getUserRegConfig().getGroups();
		if (null != groupNames) {
			Iterator it = groupNames.iterator();
			while (it.hasNext()) {
				Group group = this.getGroupManager().getGroup((String) it.next());
				((IApsAuthorityManager) this.getGroupManager()).removeUserAuthorization(username, group);
			}
		}
	}
	
	@Override
	public void deactivateUser(UserDetails user) throws ApsSystemException {
		try {
			((User)user).setDisabled(true);
			this.getUserManager().updateUser(user);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deactivateUser");
			throw new ApsSystemException("Error in Account deactivation", t);
		}
	}	
	
	@Override
	public String getUsernameFromToken(String token){
		return this.getUserRegDAO().getUsernameFromToken(token);
	}
	
	@Override
	public void reactivationByUserName(String username) throws ApsSystemException {
		Event evento = null;
		try {
			IUserProfile profile = this.getUserProfileManager().getProfile(username);
			if (null != profile) {
				if( !"PTRI".equalsIgnoreCase(profile.getTypeCode()) ) {
					evento = new Event();
					evento.setUsername(username);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.GENERAZIONE_TOKEN);
					String token = this.createToken(username);
					evento.setMessage("Richiesta recupero password con token " + token);
					this.getUserRegDAO().clearTokenByUsername(username);
					this.getUserRegDAO().addActivationToken(username, token, new Date(), IUserRegDAO.REACTIVATION_RECOVER_TOKEN_TYPE);
					this.sendAlertReactivateUser(username, profile, token);
				}
			}
		} catch (Throwable t) {
			evento.setError(t);
			ApsSystemUtils.logThrowable(t, this, "reactivationByUserName");
			throw new ApsSystemException("Error in request for Account Reactivation", t);
		} finally {
			if (evento != null)
				eventManager.insertEvent(evento);
		}
	}
	
	/**
	 * Create link for email confirmation
	 * */
	protected String createLink(String pageCode, String userName, String token, String langcode) {
		StringBuffer link = new StringBuffer(this.getCompleteApplbaseUrl());
		link.append(langcode);
		link.append("/");
		link.append(pageCode);
		link.append(".wp");
		link.append("?token=");
		link.append(token);
		return link.toString();
	}
	
	/**
	 * Populate email template
	 * */
	protected String prepareBody(String defaultText, Map params) {
		String body = defaultText;
		StringBuffer strBuff = null;
		Iterator it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String field = "{" + pairs.getKey() + "}";
			int start = body.indexOf(field);
			while (start > 0) {
				int end = start + field.length();
				strBuff = new StringBuffer();
				strBuff.append(body.substring(0, start));
				strBuff.append(pairs.getValue());
				strBuff.append(body.substring(end));
				body = strBuff.toString();
				start = body.indexOf(field, start + field.length() + 1);
			}
		}
		return body;
	}
	
	/**
	 * Generated random token 
	 * */
	protected String createToken(String userName) throws NoSuchAlgorithmException {
		Random random = new Random();
		StringBuffer salt = new StringBuffer();
		long rndLong = random.nextLong();
		salt.append(rndLong);
		String date = DateConverter.getFormattedDate(new Date(), "SSSmmyyyy-SSS-MM:ssddmmHHmmEEE");
		salt.append(date);
		rndLong = random.nextLong();
		salt.append(rndLong);
		// genero il token in base a username e salt
		String token = ShaEncoder.encodePassword(userName, salt.toString());
		return token;
	}
	
	@Override
	public void clearOldAccountRequests() throws ApsSystemException {
		long time = new Date().getTime()-this.getTokenValidityInMillis();
		
		Date expiration = new Date(time);
		this.getUserRegDAO().clearOldTokens(expiration);
		
		List<String> usernames = this.getUserRegDAO().oldAccountsNotActivated(expiration);
		
//		 TODO
//		Ragionarci
		Iterator it = usernames.iterator();
	
		while (it.hasNext()) {
			String current = (String) it.next();
			this.getUserManager().removeUser(current);
			this.getUserRegDAO().clearTokenByUsername(current);
		}		
	}
	
	/**
	 * Prepares the parameters to populate email's template of account request
	 * */
	protected Map<String, String> prepareRegProfileParams(String userName, IUserProfile regProfile, String token) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", (String) regProfile.getValue(this.getUserRegConfig().getProfileNameAttr()));
		params.put("surname", (String) regProfile.getValue(this.getUserRegConfig().getProfileSurnameAttr()));
		params.put("userName", userName);
		String activationPageCode = this.getUserRegConfig().getActivationPageCode();
		String profileLangAttr = this.getLangForLinkCreation(regProfile);
		if(token != null){
			//l'inclusione del toekn deve essere fatta solo in caso di registrazione di impresa semplice e non tramite soggetto fisico delegato (SSO)
			String link = 
					this.createLink(activationPageCode, regProfile.getUsername(), token, profileLangAttr);
			params.put("link", link);
		}
		return params;
	}

	/**
	 * Prepares the parameters to populate email's template of account reactivation request
	 * */
	protected Map<String, String> prepareReactivateUserParams(String userName, IUserProfile profile, String token) {
		Map<String, String> params = new HashMap<String, String>();
		IUserRegConfig config = this.getUserRegConfig();
		MonoTextAttribute nameAttr = (MonoTextAttribute) profile.getAttribute(config.getProfileNameAttr());
		params.put("name", nameAttr.getText());
		MonoTextAttribute surnameAttr = (MonoTextAttribute) profile.getAttribute(config.getProfileSurnameAttr());
		params.put("surname", surnameAttr.getText());
		params.put("userName", userName);
		String reactivationPageCode = this.getUserRegConfig().getReactivationPageCode();
		String profileLangAttr = this.getLangForLinkCreation(profile);
		if(token != null){
			String link = 
					this.createLink(reactivationPageCode, userName, token, profileLangAttr);
			params.put("link", link);
		}
		return params;
	}

	private String getLangForLinkCreation(IUserProfile profile) {
		String profileLangAttr = (String) profile.getValue(this.getUserRegConfig().getProfileLangAttr());
		if (null == profileLangAttr || profileLangAttr.length() == 0 ) {
			Lang defaultLang = this.getLangManager().getDefaultLang();
			profileLangAttr = defaultLang.getCode();
		}
		return profileLangAttr;
	}

	protected void sendAlertRegProfile(String userName, IUserProfile regProfile, String token) throws Exception {

		Map<String, String> params = this.prepareRegProfileParams(userName, regProfile, token);
		IUserRegConfig config = this.getUserRegConfig();
		String[] eMail = {(String) regProfile.getValue(config.getProfileEMailAttr()) };
		String langAttrName = this.getUserRegConfig().getProfileLangAttr();
		String langCode = (String) regProfile.getValue(langAttrName);
		String subject = config.getActivationMailSubject(langCode);
		String text = this.prepareBody(whichBodyMail(langCode, config), params);
		this.getMailManager().sendMail(text, subject, eMail, null, null, config.getEMailSenderCode());

	}
	private String whichBodyMail(String langCode, IUserRegConfig config) throws ApsSystemException {
		II18nManager m = (II18nManager)
				WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext())
						.getBean("I18nManager");
		String s = m.getLabel("MAIL_TOKEN_ATTIVAZIONE_TESTO", !StringUtils.isBlank(langCode) ? langCode : "it");
		return !(StringUtils.isBlank(s) || "#".equals(s)) ? s : config.getActivationMailBody(langCode);
	}

	protected void sendAlertReactivateUser(String userName, IUserProfile profile, String token) throws ApsSystemException {
		Map<String, String> params = this.prepareReactivateUserParams(userName, profile, token);
		IUserRegConfig config = this.getUserRegConfig();
		String eMail = (String) profile.getValue(config.getProfileEMailAttr());
		String[] eMails = {eMail};
		String langAttrName = this.getUserRegConfig().getProfileLangAttr();
		String langCode = (String) profile.getValue(langAttrName);
		String subject = config.getReactivationMailSubject(langCode);
		String text = this.prepareBody(config.getReactivationMailBody(langCode), params);
		this.getMailManager().sendMail(text, subject, eMails, null, null, config.getEMailSenderCode());
	}
	
	public String getReactivationToken(String username) throws ApsSystemException {
		String token = null;
		try {
			token = this.createToken(username);
			this.getUserRegDAO().clearTokenByUsername(username);
			this.getUserRegDAO().addActivationToken(username, token, new Date(), IUserRegDAO.REACTIVATION_RECOVER_TOKEN_TYPE);
		} catch (Exception ex) {
			ApsSystemUtils.getLogger().error("getReactivationToken", ex);
		}
		return token;
	}
	
	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}
	protected IUserManager getUserManager() {
		return _userManager;
	}
	
	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this._userProfileManager = userProfileManager;
	}
	protected IUserProfileManager getUserProfileManager() {
		return _userProfileManager;
	}
	
	public void setConfigManager(ConfigInterface baseConfigManager) {
		this._configManager = baseConfigManager;
	}
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	
	public void setUserRegConfig(IUserRegConfig regProfileConfig) {
		this._userRegConfig = regProfileConfig;
	}
	public IUserRegConfig getUserRegConfig() {
		return _userRegConfig;
	}
	
	public void setTokenValidityInMillis(long tokenValidityInMillis) {
		this._tokenValidityInMillis = tokenValidityInMillis;
	}
	public long getTokenValidityInMillis() {
		return _tokenValidityInMillis;
	}
	
	public void setCompleteApplbaseUrl(String completeApplbaseUrl) {
		this._completeApplbaseUrl = completeApplbaseUrl;
	}
	public String getCompleteApplbaseUrl() {
		return _completeApplbaseUrl;
	}
	
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	protected ILangManager getLangManager() {
		return _langManager;
	}
	
	public void setMailManager(IMailManager mailManager) {
		this._mailManager = mailManager;
	}
	protected IMailManager getMailManager() {
		return _mailManager;
	}
	
	public void setUserRegDAO(IUserRegDAO activationTocketDAO) {
		this._userRegDAO = activationTocketDAO;
	}
	protected IUserRegDAO getUserRegDAO() {
		return _userRegDAO;
	}
	
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	
	public void setRoleManager(IRoleManager roleManager) {
		this._roleManager = roleManager;
	}
	protected IRoleManager getRoleManager() {
		return _roleManager;
	}
	
	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}
	protected IAuthorizationManager getAuthorizationManager() {
		return _authorizationManager;
	}
	
	private IMailManager _mailManager;
	private ILangManager _langManager;
	private IUserManager _userManager;
	private IUserProfileManager _userProfileManager;
	private ConfigInterface _configManager;
	private IUserRegConfig _userRegConfig;
	private long _tokenValidityInMillis;
	private String _completeApplbaseUrl;
	private IUserRegDAO _userRegDAO;
	private IGroupManager _groupManager;
	private IRoleManager _roleManager;
	private IAuthorizationManager _authorizationManager;
	
}