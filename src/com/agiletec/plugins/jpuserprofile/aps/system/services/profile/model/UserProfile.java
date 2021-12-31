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
package com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model;

import java.util.List;

import com.agiletec.aps.system.common.entity.model.ApsEntity;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AbstractComplexAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;

/**
 * A IUserProfile implementation. 
 * It contains a set of attributes specified in the configuration of ProfileManager.
 * @author E.Santoboni
 */
public class UserProfile extends ApsEntity implements IUserProfile {
	
	@Override
	public String getUsername() {
		return this.getId();
	}
	
	@Override
	public Object getValue(String key) {
		AttributeInterface attribute = (AttributeInterface) this.getAttribute(key);
		if (null != attribute) {
			return this.getValue(attribute);
		}
		return null;
	}
	
	private Object getValue(AttributeInterface attribute) {
		if (null == attribute) return "";
		if (attribute.isTextAttribute()) {
			return ((ITextAttribute) attribute).getText();
		} else if (attribute instanceof NumberAttribute) {
			return ((NumberAttribute) attribute).getValue();
		} else if (attribute instanceof BooleanAttribute) {
			return ((BooleanAttribute) attribute).getValue();
		} else if (attribute instanceof DateAttribute) {
			return ((DateAttribute) attribute).getDate();
		} else if (!attribute.isSimple()) {
			String text = "";
			List<AttributeInterface> attributes = ((AbstractComplexAttribute) attribute).getAttributes();
			for (int i=0; i<attributes.size(); i++) {
				if (i>0) text += ",";
				AttributeInterface attributeElem = attributes.get(i);
				text += this.getValue(attributeElem);
			}
			return text;
		}
		return null;
	}
	
	@Override
	public IApsEntity getEntityPrototype() {
		UserProfile prototype = (UserProfile) super.getEntityPrototype();
		prototype.setFirstNameAttributeName(this.getFirstNameAttributeName());
		prototype.setSurnameAttributeName(this.getSurnameAttributeName());
		prototype.setMailAttributeName(this.getMailAttributeName());
		return prototype;
	}
	
	@Override
	public boolean isPublicProfile() {
		return _publicProfile;
	}
	@Override
	public void setPublicProfile(boolean publicProfile) {
		this._publicProfile = publicProfile;
	}
	
	@Override
	public String getFirstNameAttributeName() {
		return _firstNameAttributeName;
	}
	public void setFirstNameAttributeName(String firstNameAttributeName) {
		this._firstNameAttributeName = firstNameAttributeName;
	}
	
	@Override
	public String getSurnameAttributeName() {
		return _surnameAttributeName;
	}
	public void setSurnameAttributeName(String surnameAttributeName) {
		this._surnameAttributeName = surnameAttributeName;
	}
	
	@Override
	public String getMailAttributeName() {
		return _mailAttributeName;
	}
	public void setMailAttributeName(String mailAttributeName) {
		this._mailAttributeName = mailAttributeName;
	}
	
	private boolean _publicProfile;
	
	private String _firstNameAttributeName;
	
	private String _surnameAttributeName;
	
	private String _mailAttributeName;
	
}
