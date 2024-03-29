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
package com.agiletec.plugins.jpuserprofile.aps.system.services.profile.event;

import com.agiletec.aps.system.services.IManager;
import com.agiletec.aps.system.services.notify.ApsEvent;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;

/**
 * @author E.Santoboni
 */
public class ProfileChangedEvent extends ApsEvent {
	
	@Override
	public void notify(IManager srv) {
		((ProfileChangedObserver) srv).updateFromProfileChanged(this);
	}
	
	public Class getObserverInterface() {
		return ProfileChangedObserver.class;
	}

	public IUserProfile getProfile() {
		return _profile;
	}
	public void setProfile(IUserProfile profile) {
		this._profile = profile;
	}
	
	public int getOperationCode() {
		return _operationCode;
	}
	public void setOperationCode(int operationCode) {
		this._operationCode = operationCode;
	}
	
	private IUserProfile _profile;
	
	private int _operationCode;
	
	public static final int INSERT_OPERATION_CODE = 1;
	
	public static final int REMOVE_OPERATION_CODE = 2;
	
	public static final int UPDATE_OPERATION_CODE = 3;
	
}
