package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model.ITempRegImpresaProfile;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.IEntityDAO;
import com.agiletec.aps.system.common.entity.IEntitySearcherDAO;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileDAO;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfileRecord;

/**
 * Classe per la gestione dell'estrazione del profilo temporaneo registrazione
 * impresa.
 * 
 * @author Stefano.Sabbadin
 */
public class RegImpresaProfileManager extends ApsEntityManager implements
		IRegImpresaProfileManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -9068415423647413871L;

	@Override
	public IApsEntity getEntity(String entityId) throws ApsSystemException {
		return this.getProfile(entityId);
	}

	@Override
	public ITempRegImpresaProfile getProfile(String username)
			throws ApsSystemException {
		ITempRegImpresaProfile profile = null;
		try {
			UserProfileRecord profileVO = (UserProfileRecord) this
					.getProfileDAO().loadEntityRecord(username);
			if (profileVO != null) {
				profile = (ITempRegImpresaProfile) this.createEntityFromXml(
						profileVO.getTypeCode(), profileVO.getXml());
				profile.setPublicProfile(profileVO.isPublicProfile());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getProfile");
			throw new ApsSystemException("Errore recupero profileVO", t);
		}
		return profile;
	}

	@Override
	protected ICategoryManager getCategoryManager() {
		return null;
	}

	@Override
	protected IEntityDAO getEntityDao() {
		return (IEntityDAO) this.getProfileDAO();
	}

	@Override
	protected IEntitySearcherDAO getEntitySearcherDao() {
		return _entitySearcherDAO;
	}

	@Override
	public ITempRegImpresaProfile getProfileType(String typeCode) {
		return (ITempRegImpresaProfile) super.getEntityPrototype(typeCode);
	}

	@Override
	public void addProfile(String username, ITempRegImpresaProfile profile)
			throws ApsSystemException {
		try {
			profile.setId(username);
			this.getProfileDAO().addEntity(profile);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addProfile");
			throw new ApsSystemException("Errore salvataggio profilo", t);
		}
	}

	@Override
	public void deleteProfile(String username) throws ApsSystemException {
		try {
			ITempRegImpresaProfile profileToDelete = this.getProfile(username);
			if (null == profileToDelete)
				return;
			this.getProfileDAO().deleteEntity(username);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteProfile");
			throw new ApsSystemException(
					"Errore eliminazione profile per utente", t);
		}
	}

	protected IUserProfileDAO getProfileDAO() {
		return _profileDAO;
	}

	public void setProfileDAO(IUserProfileDAO profileDAO) {
		this._profileDAO = profileDAO;
	}

	protected IEntitySearcherDAO getEntitySearcherDAO() {
		return _entitySearcherDAO;
	}

	public void setEntitySearcherDAO(IEntitySearcherDAO entitySearcherDAO) {
		this._entitySearcherDAO = entitySearcherDAO;
	}

	private IUserProfileDAO _profileDAO;
	private IEntitySearcherDAO _entitySearcherDAO;

}
