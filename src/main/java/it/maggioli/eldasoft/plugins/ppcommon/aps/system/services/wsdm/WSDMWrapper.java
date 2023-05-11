package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.ws.dm.WSDM_PortType;
import it.maggioli.eldasoft.ws.dm.WSDM_ServiceLocator;
import it.maggioli.eldasoft.wssec.PasswordCallback;

import java.rmi.Remote;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Wrapper per il client del web service WSDM, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author Stefano.Sabbadin
 */
@Aspect
public class WSDMWrapper {
	
//	/** Riferimento al web service WSDM. */
//	private WSDM_PortType portType;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;
	
//    /** URL servizio WSDM. */
//	private String endpoint;
//	/** URL servizio WSDMConfigurazione. */
//	private String endpointConfig;	
//    /** Username di autenticazione al servizio. */
//	private String username;
//	/** Password di autenticazione al servizio. */
//    private String password;
//    /** Cognome utente. */
//	private String cognome;
//    /** Nome utente. */
//	private String nome;
//	/** Ruolo di autenticazione al servizio. */
//	private String ruolo;
//    /** codice unit&agrave; organizzativa. */
//	private String codiceUO;
//	/** id utente. */
//	private String idUtente;
//	/** id unit&agrave operativa. */
//	private String idUnitaOperativa;
	
	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
//	/**
//	 * @param endpoint the endpoint to set
//	 */
//	public void setEndpoint(String endpoint) {
//		this.endpoint = endpoint;
//	}
//	
//	public void setEndpointConfig(String endpointConfig) {
//		this.endpointConfig = endpointConfig;
//	}
//
//	/**
//	 * @param username the username to set
//	 */
//	public void setUsername(String username) {
//		this.username = StringUtils.stripToEmpty(username);
//	}
//
//	/**
//	 * @param password the password to set
//	 */
//	public void setPassword(String password) {
//		this.password = StringUtils.stripToEmpty(password);
//	}
//	/**
//	 * @param cognome the cognome to set
//	 */
//	public void setCognome(String cognome) {
//		this.cognome = cognome;
//	}
//	/**
//	 * @param nome the nome to set
//	 */
//	public void setNome(String nome) {
//		this.nome = nome;
//	}
//	/**
//	 * @param ruolo the ruolo to set
//	 */
//	public void setRuolo(String ruolo) {
//		this.ruolo = ruolo;
//	}
//	/**
//	 * @param codiceUO the codiceUO to set
//	 */
//	public void setCodiceUO(String codiceUO) {
//		this.codiceUO = codiceUO;
//	}
//	/**
//	 * @param idUtente the idUtente to set
//	 */
//	public void setIdUtente(String idUtente) {
//		this.idUtente = idUtente;
//	}
//	/**
//	 * @param idUnitaOperativa the idUnitaOperativa to set
//	 */
//	public void setIdUnitaOperativa(String idUnitaOperativa) {
//		this.idUnitaOperativa = idUnitaOperativa;
//	}
	
	/**
	 * @return the cognome
	 */
	public String getCognome() {
		//return cognome;
		return StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_COGNOME));
	}
	
	/**
	 * @return the nome
	 */
	public String getNome() {
		return StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_NOME));
	}
	
	/**
	 * @return the ruolo
	 */
	public String getRuolo() {
		return StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_RUOLO));
	}

	/**
	 * @return the codiceUO
	 */
	public String getCodiceUO() {
		return StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICEUO));
	}

	/**
	 * @return the idUtente
	 */
	public String getIdUtente() {
		return StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_IDUTENTE));
	}

	/**
	 * @return the idUnitaOperativa
	 */
	public String getIdUnitaOperativa() {
		return StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_IDUNITA_OPERATIVA));
	}


	public WSDM_PortType getWSDM_PortType() throws ApsException {
		WSDM_PortType port = null;
				
		if (port == null) {
			synchronized (this) {
				if (port == null) {
					// solo una volta acceduto al blocco sincronizzato, e se
					// sono rimasto in attesa non c'e' stato un altro che ha
					// inizializzato l'oggetto un attimo prima di me, lo faccio
					// io
					String endpoint = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_URL));
					String username = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_USERNAME));
					String password = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_PASSWORD));
					//String cognome = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_COGNOME));
					//String nome = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_NOME));
					//String ruolo = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_RUOLO));
					//String codiceUO = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICEUO));
					//String idUtente = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_IDUTENTE));
					//String idUnitaOperativa = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_IDUNITA_OPERATIVA));

				    // Chiave per AES 128bit
				    byte[] key = "T/Yer@#2983273&d".getBytes();

				    // Encode della password
				    try {
					    Cipher c = Cipher.getInstance("AES");
					    SecretKeySpec k = new SecretKeySpec(key, "AES");
					    c.init(Cipher.ENCRYPT_MODE, k);
					    byte[] passwordEncoded = c.doFinal(password.getBytes());
	
					    // Configurazione client WS per l'utilizzo di WS-Security
					    EngineConfiguration config = new FileProvider("client_wssec.wsdd");
	
					    WSDM_ServiceLocator wsdmprotocolloLocator = new WSDM_ServiceLocator(config);
					    wsdmprotocolloLocator.setWSDMImplPortEndpointAddress(endpoint);
					    Remote remote = wsdmprotocolloLocator.getPort(WSDM_PortType.class);
					    Stub axisPort = (Stub) remote;
					    axisPort._setProperty(WSHandlerConstants.USER, username);
					    PasswordCallback passwordCallback = new PasswordCallback();
					    passwordCallback.setAliasPassword(username, org.apache.axis.encoding.Base64.encode(passwordEncoded));
					    axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_REF, passwordCallback);
					    port = (WSDM_PortType) axisPort;
				    } catch (ServiceException e) {
					    throw new ApsException(
							    "Impossibile ottenere il riferimento al servizio remoto WSDM",
							    e);
					} catch (GeneralSecurityException e) {
					    throw new ApsException(
							    "Errore durante la cifratura dei dati di autenticazione per l'utilizzo di WSDM con ws-security",
							    e);
					}
				}
			}
		}
		return port;
	}
	
	@After ("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setCredentials(JoinPoint joinPoint) {
//		this.endpoint = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_URL));
//		this.endpointConfig = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CONFIG_URL));
//		this.username = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_USERNAME));
//		this.password = StringUtils.stripToEmpty((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_PASSWORD));
//		this.cognome = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_COGNOME));
//		this.nome = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_NOME));
//		this.ruolo = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_RUOLO));
//		this.codiceUO = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICEUO));
//		this.idUtente = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_IDUTENTE));
//		this.idUnitaOperativa = StringUtils.stripToNull((String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_IDUNITA_OPERATIVA));
//		synchronized (this) {
//			this.portType = null;
//			this.portTypeConfig = null;
//		}
	}
	
	/**
	 * aggangiato all'esecuzione del metodo IAppParamManager.setStazioneAppaltanteProtocollazione(...)
	 * ed utilizzato per for
	 * zare la rilettura dei parametri relativi al WSMD multi config
	 */
	@After ("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.setStazioneAppaltanteProtocollazione(..))")
	private void setStazioneAppaltanteProtocollazione() {
		this.setCredentials(null);
	}

}
