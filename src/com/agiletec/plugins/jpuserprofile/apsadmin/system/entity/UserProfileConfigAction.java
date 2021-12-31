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
package com.agiletec.plugins.jpuserprofile.apsadmin.system.entity;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.apsadmin.system.entity.type.EntityTypeConfigAction;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile;

/**
 * @author E.Santoboni
 */
public class UserProfileConfigAction extends EntityTypeConfigAction {
	
	@Override
	protected IApsEntity updateEntityOnSession() {
		UserProfile userProfileType = (UserProfile) super.updateEntityOnSession();
		userProfileType.setFirstNameAttributeName(this.getFirstNameAttributeName());
		userProfileType.setSurnameAttributeName(this.getSurnameAttributeName());
		userProfileType.setMailAttributeName(this.getMailAttributeName());
		return userProfileType;
	}
	
	public List<AttributeInterface> getAllowedDefaultAttributes() {
		List<AttributeInterface> attributes = new ArrayList<AttributeInterface>();
		for (int i = 0; i < this.getEntityType().getAttributeList().size(); i++) {
			AttributeInterface attribute = this.getEntityType().getAttributeList().get(i);
			if (attribute instanceof MonoTextAttribute && attribute.isRequired() && attribute.isSearcheable()) {
				attributes.add(attribute);
			}
		}
		return attributes;
	}
	
	public String getFirstNameAttributeName() {
		return _firstNameAttributeName;
	}
	public void setFirstNameAttributeName(String firstNameAttributeName) {
		this._firstNameAttributeName = firstNameAttributeName;
	}
	
	public String getSurnameAttributeName() {
		return _surnameAttributeName;
	}
	public void setSurnameAttributeName(String surnameAttributeName) {
		this._surnameAttributeName = surnameAttributeName;
	}
	
	public String getMailAttributeName() {
		return _mailAttributeName;
	}
	public void setMailAttributeName(String mailAttributeName) {
		this._mailAttributeName = mailAttributeName;
	}
	
	private String _firstNameAttributeName;
	private String _surnameAttributeName;
	private String _mailAttributeName;
	
}