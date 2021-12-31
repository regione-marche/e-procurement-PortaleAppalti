package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ricaricaconfig;

import com.agiletec.apsadmin.admin.BaseAdminAction;

/**
 * ricarica la configurazione del portale senza il bisogno di effettuare login come admin
 *  
 */
public class RicaricaConfigurazioneAction extends BaseAdminAction {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -208317357153700753L;

	
	/**
	 * ricarica la configurazione del portale con "do/ricaricaConfigurazione.action"
	 */
	public String reload() {
		return this.reloadConfig();
	}
	
}