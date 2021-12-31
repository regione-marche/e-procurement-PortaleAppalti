package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid;

import it.cedaf.authservice.service.AuthData;
import java.rmi.RemoteException;
import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore delle autenticazioni SPID.
 * 
 * @version 1.0
 * @author ...
 */
public class AuthServiceSPIDManager extends AbstractService implements IAuthServiceSPIDManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6294869146274333670L;
	
	/** Riferimento al wrapper per il web service  */
	private WSAuthServiceSPIDWrapper wsAuthServiceSPID;


	/**
	 * Imposta il web service SPID
	 * 
	 * @param proxyWsAuthServiceSPID
	 * 
	 */
	public void setwsAuthServiceSPID(WSAuthServiceSPIDWrapper proxyWsAuthServiceSPID) {
		this.wsAuthServiceSPID = proxyWsAuthServiceSPID;
	}

	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}
		
	/**
	 * Restituisce un token di sicurezza
	 * 
	 * @throws ApsException 
	 */
	@Override
	public String getAuthId() throws ApsException {
		String authId = null;
		String retWS = null;
		try {
			retWS = (String) wsAuthServiceSPID.getProxyWSAuthService()
				.getAuthId();

			if (StringUtils.isNotEmpty(retWS)) {
				authId = retWS;
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la richiesta del token");
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la richiesta del token",
					t);
		}
		return authId;
	};

	/**
	 * Restituisce i dettagli delle fasi di un'asta
	 * 
	 * @throws ApsException  
	 */
	
	@Override
	public AuthData retrieveUserData(String authId) 
		throws ApsException {
		AuthData userData = null;
		AuthData retWS = null;
		try {
			retWS = (AuthData)wsAuthServiceSPID.getProxyWSAuthService()
				.retrieveUserData(authId);

			if (retWS != null) {
				userData = retWS;
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei dati utente");
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura dei dati utente",
					t);
		}
		return userData;
	}

	
	/**
	 * Restituisce i dettagli di un'asta
	 * 
	 * @throws ApsException  
	 */
	@Override
	public boolean revalidateUserData(AuthData authData) 
		throws ApsException {
		boolean response = false;
		boolean retWS = false;
		try {
			retWS = (boolean)wsAuthServiceSPID.getProxyWSAuthService()
				.revalidateUserData(authData);

			response = retWS;
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la validazione dei dati utente",
					t);
		}
		return response;		
	}

	@Override
	public String singleSignOut(String authId) 
		throws ApsException {
		String response = null;
		String retWS = null;
		try {
			retWS = (String)wsAuthServiceSPID.getProxyWSAuthService()
				.singleSignOut(authId);

			if (StringUtils.isNotEmpty(retWS)) {
				response = retWS;
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del singleSignOut per il token " + authId);
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante la lettura del singleSignOut",
					t);
		}
		return response;
	}

}