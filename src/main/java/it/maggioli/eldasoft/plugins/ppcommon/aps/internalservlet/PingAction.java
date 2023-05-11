package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import com.agiletec.apsadmin.system.BaseAction;

public class PingAction extends BaseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1390179299929316302L;
	
	public String ping() {
		return SUCCESS;
	}
		
}
