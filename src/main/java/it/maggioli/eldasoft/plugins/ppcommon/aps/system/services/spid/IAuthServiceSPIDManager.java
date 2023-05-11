package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid;

import com.agiletec.aps.system.exception.ApsException;
import it.cedaf.authservice.service.AuthData;
import it.cedaf.authservice.service.AuthDataV2;


/**
 * Interfaccia base per il servizio di gestione di autenticazione SPID.
 * 
 * @version 1.0
 * @author 
 */
public interface IAuthServiceSPIDManager {

	/**
	 * @throws ApsException 
	 */
	public String getAuthId() throws ApsException;
	
	/**
	 * @throws ApsException 
	 */
	public AuthData retrieveUserData(
			String authId) throws ApsException;
	
	/**
	 * @throws ApsException 
	 */
	public boolean revalidateUserData(
			AuthData authData) throws ApsException;

	/**
	 * @throws ApsException 
	 */
	public String singleSignOut(
			String authId) throws ApsException;

	public AuthDataV2 retrieveUserDataV2(String authId) throws ApsException;

}
