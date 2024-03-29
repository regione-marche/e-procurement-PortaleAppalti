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
package com.agiletec.plugins.jpuserprofile.aps.system.services.profile;

import java.sql.ResultSet;

import com.agiletec.aps.system.common.entity.AbstractEntitySearcherDAO;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.ApsEntityRecord;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfileRecord;

/**
 * Data Access Object for Search of UserProfile Object. 
 * @author E.Santoboni
 */
public class UserProfileSearcherDAO extends AbstractEntitySearcherDAO {
	
	@Override
	protected ApsEntityRecord createRecord(ResultSet result) throws Throwable {
		UserProfileRecord record = new UserProfileRecord();
		record.setId(result.getString("username"));
		record.setXml(result.getString("xml"));
		record.setTypeCode(result.getString("profiletype"));
		record.setPublicProfile(result.getInt("publicprofile") == 1);
		return record;
	}
	
	@Override
	public String getEntityMasterTableIdFieldName() {
		return "username";
	}
	
	@Override
	public String getEntityMasterTableIdTypeFieldName() {
		return "profiletype";
	}
	
	@Override
	public String getEntityMasterTableName() {
		return "jpuserprofile_authuserprofiles";
	}
	
	@Override
	public String getEntitySearchTableIdFieldName() {
		return "username";
	}
	
	@Override
	public String getEntitySearchTableName() {
		return "jpuserprofile_profilesearch";
	}
	
	@Override
	public String getTableFieldName(String metadataFieldKey) {
		if (metadataFieldKey.equals(IEntityManager.ENTITY_ID_FILTER_KEY)) {
			return this.getEntityMasterTableIdFieldName();
		} else if (metadataFieldKey.equals(IEntityManager.ENTITY_TYPE_CODE_FILTER_KEY)) {
			return this.getEntityMasterTableIdTypeFieldName();
		} else if (metadataFieldKey.equals(IUserProfileManager.PUBLIC_PROFILE_FILTER_KEY)) {
			return "publicprofile";
		} else throw new RuntimeException("Chiave di ricerca '" + metadataFieldKey + "' non riconosciuta");
	}

}
