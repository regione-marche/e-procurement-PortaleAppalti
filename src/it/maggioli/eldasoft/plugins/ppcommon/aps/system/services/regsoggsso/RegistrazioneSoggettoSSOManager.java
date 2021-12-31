package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.regsoggsso;

import java.util.List;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.authorizator.IApsAuthorityManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.user.IUserDAO;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.UserRegBean;

/**
 * Servizio gestore della registrazione di un soggetto loggato troamite sistema SSO.
 * 
 * @version 1.0
 * @author Eleonora.Favaro
 */

public class RegistrazioneSoggettoSSOManager implements IRegistrazioneSoggettoSSOManager {

	private static final long serialVersionUID = -6327520017795120851L;
	
	private IGroupManager _groupManager;
	private IUserManager _userManager;
	private IUserProfileManager _userProfileManager;
	
	private IUserDAO _userDao;
	
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}
	
	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this._userProfileManager = userProfileManager;
	}
	
    public void setUserDAO(IUserDAO userDao) {
		this._userDao = userDao;
	}
    
	@Override
	public EsitoOutType insertSoggettoSSO(String username, String email,
			String nome,String cognome) {

		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);

		risultato = insertUserSSO(username,email,nome,cognome);
		
		if(risultato.isEsitoOk()){
			try {
				Group group = this._groupManager.getGroup("sso");
				((IApsAuthorityManager) this._groupManager).setUserAuthorization(username, group);
			} catch (ApsSystemException e) {
				ApsSystemUtils.logThrowable(e, this, "insertSoggettoSSO");
			}
		}

		return risultato;
	}

	@Override
	public EsitoOutType updateSoggettoSSO(String username) {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);

		this._userDao.updateLastAccess(username);
		
		return risultato;
	}

	@Override
	public boolean esisteSoggettoSSO(String username) {
		boolean exists = false;
		try {
			exists = (username != null && username.trim().length() >= 0 && this._userManager.getUser(username) != null);
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "esisteSoggettoSSO");
		}
		return exists;
	}

	private EsitoOutType insertUserSSO(String username, String email, String nome, String cognome){

		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);

		IUserProfile userProfile = this._userProfileManager.getProfileType(
				"PFL");
		userProfile.setPublicProfile(false);
		
		RegistrazioneSoggettoSSOManager.updateEntityAttributes(userProfile, email, nome, cognome);

		try {
			UserRegBean regAccountBean = new UserRegBean();
			regAccountBean.setUsername(username);
			regAccountBean.setEMail(email);
			regAccountBean.setLang("it");
			regAccountBean.setUserProfile(userProfile);
			this.regAccountSSO(regAccountBean);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "insertSoggettoSSO");
			risultato.setEsitoOk(false);
			risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
			try {
				this._userProfileManager.deleteProfile(userProfile.getUsername());
			} catch (ApsSystemException e1) {
				ApsSystemUtils.logThrowable(e1, this, "insertUserSSO");
			}
		}
		return risultato;
	}
	
	public static void updateEntityAttributes(IApsEntity currentEntity,
			String email, String nome, String cognome) {
		List<AttributeInterface> attributes = currentEntity.getAttributeList();
		for (int i = 0; i < attributes.size(); i++) {
			MonoTextAttribute attribute = (MonoTextAttribute) attributes.get(i);
			if (attribute.isActive()) {
				if ("Nome".equals(attribute.getName()))
					attribute.setText(nome);
				if ("Cognome".equals(attribute.getName()))
					attribute.setText(cognome);
				if ("email".equals(attribute.getName()))
					attribute.setText(email);
			}
		}
	}
	
	private void regAccountSSO(UserRegBean regAccountBean) throws ApsSystemException {
		try {
			User user = new User();
			user.setDisabled(false);
			user.setUsername(regAccountBean.getUsername());
			user.setProfile(regAccountBean.getUserProfile());
			user.setPassword("");
			this._userManager.addUser(user);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "regAccountSSO");
			throw new ApsSystemException("Error in Account registration for SSO user", t);
		}
	}
}
