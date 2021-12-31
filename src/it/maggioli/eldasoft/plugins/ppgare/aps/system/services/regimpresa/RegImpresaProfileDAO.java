package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model.ITempRegImpresaProfile;

import java.sql.PreparedStatement;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.UserProfileDAO;

/**
 * DAO derivato da UserProfileDAO, di cui si ridefinisce l'unico metodo
 * effettivamente utilizzato (quello per l'inserimento).
 */
public class RegImpresaProfileDAO extends UserProfileDAO {

	@Override
	protected void buildAddEntityStatement(IApsEntity entity,
			PreparedStatement stat) throws Throwable {
		ITempRegImpresaProfile profile = (ITempRegImpresaProfile) entity;
		stat.setString(1, profile.getUsername());
		stat.setString(2, profile.getTypeCode());
		stat.setString(3, profile.getXML());
		if (profile.isPublicProfile()) {
			stat.setInt(4, 1);
		} else {
			stat.setInt(4, 0);
		}
	}
}
