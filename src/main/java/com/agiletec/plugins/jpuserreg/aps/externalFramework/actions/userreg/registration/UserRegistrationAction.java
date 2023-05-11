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
package com.agiletec.plugins.jpuserreg.aps.externalFramework.actions.userreg.registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.system.entity.IEntityActionHelper;
import com.agiletec.plugins.jpmail.aps.services.mail.util.EmailAddressValidator;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.UserRegBean;

/**
 * Action to manage User Account Registration Requests
 * 
 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 * */
public class UserRegistrationAction extends BaseAction implements IUserRegistrationAction {
	
	@Override
	public void validate() {
		IUserProfile userProfile = this.getUserProfileManager().getDefaultProfileType();
		this.getEntityActionHelper().updateEntityAttributes(userProfile, this.getRequest());
		this.setUserProfile(userProfile);
		super.validate();
		this.loadMapNamedAttributes();
		try {
			if (this.existsUser(this.getUsername())) {
				this.addFieldError("username", this.getText("Errors.duplicateUser"));
			}
			if (!this.isPrivacyPolicyAgreement()) {
				this.addFieldError("privacyPolicyAgreement", this.getText("Errors.privacyPolicyAgreement.required"));
			}
			String name = this.getName();
			if (null==name || name.length() == 0 ) {
				this.addFieldError("name", this.getText("Errors.name.required"));
			}
			String surname = this.getSurname();
			if (null==surname || surname.length() == 0 ) {
				this.addFieldError("surname", this.getText("Errors.surname.required"));
			}
			String email = this.getEmail();
			if (null==email || email.length() == 0 ) {
				this.addFieldError("email", this.getText("Errors.email.required"));
			} else if (!email.equals(this.getEmailConfirm())) {
				this.addFieldError("email", this.getText("Errors.email.confirm.msg"));
			} else if (!EmailAddressValidator.isValidEmailAddress(this.getEmail())) {
				this.addFieldError("email", this.getText("Errors.email.unvalid"));
			} else if(this.getEmail() != null && this.verifyEmailAlreadyPresent(this.getEmail())){
				this.addFieldError("email", this.getText("jpregprofile.mail.alreadypresent"));
			}
			if (null == this.getLanguage() || this.getLanguage().length() == 0 ) {
				this.addFieldError("language", this.getText("Errors.langcode.required"));
			}
		} catch (Throwable t) {
			throw new RuntimeException("Error validation of request for account activation" + this.getUsername(), t);
		}
	}
	
	protected void loadMapNamedAttributes() {
		String nameAttr = this.getUserRegConfig().getProfileNameAttr();
		String surnameAttr = this.getUserRegConfig().getProfileSurnameAttr();
		String emailAttr = this.getUserRegConfig().getProfileEMailAttr();
		_attributeValues.put(nameAttr, this.getRequest().getParameter(nameAttr));
		_attributeValues.put(surnameAttr, this.getRequest().getParameter(surnameAttr));
		_attributeValues.put(emailAttr, this.getRequest().getParameter(emailAttr));
	}
	
	public IUserProfile getUserProfile() {
		return (IUserProfile) this.getRequest().getSession().getAttribute(UserRegistrationAction.SESSION_PARAM_NAME_REQ_PROFILE);
	}
	public void setUserProfile(IUserProfile userProfile) {
		this.getRequest().getSession().setAttribute(UserRegistrationAction.SESSION_PARAM_NAME_REQ_PROFILE, userProfile);
	}
	
	/**
	 * check if user exist
	 * @param username
	 * @return true if exist a user with this username, false if user not exist.
	 * @throws Throwable In error case.
	 */
	protected boolean existsUser(String username) throws Throwable {
		boolean exists = (username!=null && username.trim().length()>=0 && this.getUserManager().getUser(username)!=null);
		return exists;
	}
	
	/**
	 * It Adds user account and profile in to the system, 
	 * keeping disabled status until the end of registration process
	 * */
	public String save() {
		try {
			UserRegBean regAccountBean = new UserRegBean();
			regAccountBean.setUsername(_username);
			regAccountBean.setName(_name);
			regAccountBean.setSurname(_surname);
			regAccountBean.setEMail(_email);
			regAccountBean.setLang(_language);
			regAccountBean.setUserProfile(this.getUserProfile());
			this._userRegManager.regAccount(regAccountBean);
		} catch (Throwable t) {
			this.addActionError(this.getText("Errors.userRegistration.genericError"));
			ApsSystemUtils.logThrowable(t, this, "reqAccount");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public IUserRegConfig getUserRegConfig(){
		return this.getUserRegManager().getUserRegConfig();
	}
	
	public List<Lang> getLangs() {
		return this.getLangManager().getLangs();
	}
	
	/**
	 * Verify if email already exist
	 * @param email
	 * @return
	 * @throws ApsSystemException
	 */
	private boolean verifyEmailAlreadyPresent(String email) throws ApsSystemException {
		try {
			EntitySearchFilter[] filters = 
				{ new EntitySearchFilter(this.getUserRegConfig().getProfileEMailAttr(), true, email, false) };
			List<String> usernames = ((ApsEntityManager)this.getUserProfileManager()).searchId(filters);
			if (usernames.size()>0) {
				return true;
			}
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "verifyEmailAlreadyPresent");
			throw e;
		}
		return false;
	}
	
	public void setUsername(String username) {
		this._username = username;
	}
	public String getUsername() {
		return _username;
	}
	
	public void setLanguage(String language) {
		this._language = language;
	}
	public String getLanguage() {
		return _language;
	}
	
	public String getName() {
		String nameAttr = this.getUserRegConfig().getProfileNameAttr();
		return (String) this._attributeValues.get(nameAttr);
	}
	public String getSurname() {
		String surnameAttr = this.getUserRegConfig().getProfileSurnameAttr();
		return (String) this._attributeValues.get(surnameAttr);
	}
	public String getEmail() {
		String emailAttr = this.getUserRegConfig().getProfileEMailAttr();
		return (String) this._attributeValues.get(emailAttr);
	}
	
	public void setEmailConfirm(String emailConfirm) {
		this._emailConfirm = emailConfirm;
	}
	public String getEmailConfirm() {
		return _emailConfirm;
	}
	
	public void setPrivacyPolicyAgreement(boolean privacyPolicyAgreement) {
		this._privacyPolicyAgreement = privacyPolicyAgreement;
	}
	public boolean isPrivacyPolicyAgreement() {
		return _privacyPolicyAgreement;
	}
	
	public void setUserRegManager(IUserRegManager userRegManager) {
		this._userRegManager = userRegManager;
	}
	protected IUserRegManager getUserRegManager() {
		return _userRegManager;
	}
	
	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this._userProfileManager = userProfileManager;
	}
	protected IUserProfileManager getUserProfileManager() {
		return _userProfileManager;
	}
	
	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}
	protected IUserManager getUserManager() {
		return _userManager;
	}
	
	protected IEntityActionHelper getEntityActionHelper() {
		return _entityActionHelper;
	}
	public void setEntityActionHelper(IEntityActionHelper entityActionHelper) {
		this._entityActionHelper = entityActionHelper;
	}
	
	private String _username;
	private String _language;
	private String _name;
	private String _surname;
	private String _email;
	private String _emailConfirm;
	private boolean _privacyPolicyAgreement = false;
	private IUserRegManager _userRegManager;
	private IUserManager _userManager;
	private IUserProfileManager _userProfileManager;
	private IEntityActionHelper _entityActionHelper;
	protected Map<String, String> _attributeValues = new HashMap<String, String>();
	
}