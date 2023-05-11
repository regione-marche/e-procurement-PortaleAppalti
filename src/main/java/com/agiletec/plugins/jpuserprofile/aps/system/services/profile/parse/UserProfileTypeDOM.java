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
package com.agiletec.plugins.jpuserprofile.aps.system.services.profile.parse;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.parse.EntityTypeDOM;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile;

/**
 * @author E.Santoboni
 */
public class UserProfileTypeDOM extends EntityTypeDOM {
	
	@Override
	protected IApsEntity createEntityType(Element contentElem, Class entityClass) throws ApsSystemException {
		UserProfile userProfile = (UserProfile) super.createEntityType(contentElem, entityClass);
		String firstNameAttributeName = this.extractXmlAttribute(contentElem, "firstNameAttributeName", false);
		if (null != firstNameAttributeName && !firstNameAttributeName.equals(NULL_VALUE)) {
			userProfile.setFirstNameAttributeName(firstNameAttributeName);
		}
		String surnameAttributeName = this.extractXmlAttribute(contentElem, "surnameAttributeName", false);
		if (null != surnameAttributeName && !surnameAttributeName.equals(NULL_VALUE)) {
			userProfile.setSurnameAttributeName(surnameAttributeName);
		}
		String mailAttributeName = this.extractXmlAttribute(contentElem, "mailAttributeName", false);
		if (null != mailAttributeName && !mailAttributeName.equals(NULL_VALUE)) {
			userProfile.setMailAttributeName(mailAttributeName);
		}
		return userProfile;
	}
	
	@Override
	protected Element createRootTypeElement(IApsEntity currentEntityType) {
		Element typeElement = super.createRootTypeElement(currentEntityType);
		UserProfile userProfile = (UserProfile) currentEntityType;
		this.setXmlAttribute(typeElement, "firstNameAttributeName", userProfile.getFirstNameAttributeName());
		this.setXmlAttribute(typeElement, "surnameAttributeName", userProfile.getSurnameAttributeName());
		this.setXmlAttribute(typeElement, "mailAttributeName", userProfile.getMailAttributeName());
		return typeElement;
	}
	
	private void setXmlAttribute(Element element, String name, String value) {
		String valueToSet = value;
		if (null == value || value.trim().length() == 0) {
			valueToSet = NULL_VALUE;
		}
		element.setAttribute(name, valueToSet);
	}
	
	@Override
	protected String getEntityTypeRootElementName() {
		return "profiletype";
	}
	
	@Override
	protected String getEntityTypesRootElementName() {
		return "profiletypes";
	}
	
	private static final String NULL_VALUE = "**NULL**";
	
}