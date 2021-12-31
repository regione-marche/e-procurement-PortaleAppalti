package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazione_PortType;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazione_ServiceLocator;

/**
 * Servizio di gestione delle configurazioni della verticalizzazione definita
 * sul sistema.<br/>
 * Non viene gestita la sincronizzazione sull'istanza nel metodo
 * getConfigurationValue e nel blocco this.checkJNDI(list) dentro il metodo
 * loadAppParams per non peggiorare le performance dato che le configurazioni
 * più critiche avverranno solo in fase di aggiornamento dell'applicativo.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public class AppParamManager extends AbstractService implements
		IAppParamManager {
	
	// variabili JNDI
	private static final String JNDI_ENV_PROTOCOLLAZIONE_TIPO = "tipoProtocollazione";
	private static final String JNDI_ENV_PROTOCOLLAZIONE_MAIL_DESTINATARI = "mailUfficioProtocollo";
	private static final String JNDI_ENV_PROTOCOLLAZIONE_MAIL_ALLEGA_FILE = "allegaDocMailUfficioProtocollo";
	
	// parametri configurati a DB e memorizzati in cache
	public static final String LAYOUT_STYLE = "layoutStyle";
	public static final String NTP_SERVER = "ntpServer";
	public static final String NTP_TIMEOUT = "ntpTimeout";
	public static final String WS_OPERAZIONI_GENERALI = "wsOperazioniGenerali";
	public static final String WS_GARE_APPALTO = "wsGareAppalto";
	public static final String WS_ASTE = "wsAste";
	public static final String WS_BANDI_ESITI_AVVISI = "wsBandiEsitiAvvisi";
	public static final String WS_REPORT = "wsReport";
	public static final String PUBBLICAZIONE_NUM_ANNI = "pubblicazione.numAnni";
	public static final String PROTOCOLLAZIONE_TIPO = "protocollazione.tipo";
	public static final String PROTOCOLLAZIONE_MAIL_DESTINATARI = "protocollazione.mail.destinatari";
	public static final String PROTOCOLLAZIONE_MAIL_ALLEGA_FILE = "protocollazione.mail.allegaFile";
	public static final String PROTOCOLLAZIONE_WSDM_URL = "protocollazione.wsdm.url";
	public static final String PROTOCOLLAZIONE_WSDM_URL_CONFIG = "wsdmconfigurazione.fascicoloprotocollo.url";
	public static final String PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA = "protocollazione.wsdm.codiceSistema";
	public static final String PROTOCOLLAZIONE_WSDM_USERNAME = "protocollazione.wsdm.username";
	public static final String PROTOCOLLAZIONE_WSDM_PASSWORD = "protocollazione.wsdm.password";
	public static final String PROTOCOLLAZIONE_WSDM_RUOLO = "protocollazione.wsdm.ruolo";
	public static final String PROTOCOLLAZIONE_WSDM_COGNOME = "protocollazione.wsdm.cognome";
	public static final String PROTOCOLLAZIONE_WSDM_NOME = "protocollazione.wsdm.nome";
	public static final String PROTOCOLLAZIONE_WSDM_CODICEUO = "protocollazione.wsdm.codiceUO";
	public static final String PROTOCOLLAZIONE_WSDM_STRUTTURA = "protocollazione.wsdm.struttura";
	public static final String PROTOCOLLAZIONE_WSDM_TIPOASSEGNAZIONE = "protocollazione.wsdm.tipoAssegnazione";	
	public static final String PROTOCOLLAZIONE_WSDM_MEZZO = "protocollazione.wsdm.mezzo";
	public static final String PROTOCOLLAZIONE_WSDM_SUPPORTO = "protocollazione.wsdm.supporto";
	public static final String PROTOCOLLAZIONE_WSDM_MITTENTE_INTERNO = "protocollazione.wsdm.mittenteInterno";
	public static final String PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE = "protocollazione.wsdm.cfMittente";
	public static final String PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE = "protocollazione.wsdm.channelCode";
	public static final String PROTOCOLLAZIONE_WSDM_IDUTENTE = "protocollazione.wsdm.idUtente";
	public static final String PROTOCOLLAZIONE_WSDM_IDUNITA_OPERATIVA = "protocollazione.wsdm.idUnitaOperativa";
	public static final String PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA = "protocollazione.wsdm.idUnitaOperativaDestinataria";
	public static final String PROTOCOLLAZIONE_WSDM_SOTTOTIPO_GARA = "protocollazione.wsdm.sottotipoGara";
	public static final String PROTOCOLLAZIONE_WSDM_SOTTOTIPO_COMUNICAZIONE = "protocollazione.wsdm.sottotipoComunicazione";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA = "protocollazione.wsdm.gare.classifica";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO = "protocollazione.wsdm.gare.tipoDocumento";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO_PREQUALIFICA = "protocollazione.wsdm.gare.tipoDocumento.prequalifica";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO_INVIA_COMUNICAZIONE = "protocollazione.wsdm.tipoDocumento.inviaComunicazione";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_CODICE_REGISTRO = "protocollazione.wsdm.gare.registro";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_INDICE = "protocollazione.wsdm.gare.indice";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_TITOLAZIONE = "protocollazione.wsdm.gare.titolazione";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_LIVELLO_RISERVATEZZA = "protocollazione.wsdm.gare.livelloRiservatezza";
	public static final String PROTOCOLLAZIONE_WSDM_GARE_STRUTTURA = "protocollazione.wsdm.gare.struttura";	
	public static final String PROTOCOLLAZIONE_WSDM_MEPA_CLASSIFICA = "protocollazione.wsdm.mepa.classifica";
	public static final String PROTOCOLLAZIONE_WSDM_MEPA_TIPO_DOCUMENTO = "protocollazione.wsdm.mepa.tipoDocumento";
	public static final String PROTOCOLLAZIONE_WSDM_MEPA_CODICE_REGISTRO = "protocollazione.wsdm.mepa.registro";
	public static final String PROTOCOLLAZIONE_WSDM_MEPA_INDICE = "protocollazione.wsdm.mepa.indice";
	public static final String PROTOCOLLAZIONE_WSDM_MEPA_TITOLAZIONE = "protocollazione.wsdm.mepa.titolazione";
	public static final String PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CLASSIFICA = "protocollazione.wsdm.iscrizione.classifica";
	public static final String PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TIPO_DOCUMENTO = "protocollazione.wsdm.iscrizione.tipoDocumento";
	public static final String PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CODICE_REGISTRO = "protocollazione.wsdm.iscrizione.registro";
	public static final String PROTOCOLLAZIONE_WSDM_ISCRIZIONE_INDICE = "protocollazione.wsdm.iscrizione.indice";
	public static final String PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TITOLAZIONE = "protocollazione.wsdm.iscrizione.titolazione";
	public static final String PROTOCOLLAZIONE_WSDM_AVVISI_CLASSIFICA = "protocollazione.wsdm.avvisi.classifica";
	public static final String PROTOCOLLAZIONE_WSDM_AVVISI_TIPO_DOCUMENTO = "protocollazione.wsdm.avvisi.tipoDocumento";
	public static final String PROTOCOLLAZIONE_WSDM_AVVISI_CODICE_REGISTRO = "protocollazione.wsdm.avvisi.registro";
	public static final String PROTOCOLLAZIONE_WSDM_AVVISI_INDICE = "protocollazione.wsdm.avvisi.indice";
	public static final String PROTOCOLLAZIONE_WSDM_AVVISI_TITOLAZIONE = "protocollazione.wsdm.avvisi.titolazione";
	public static final String PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE = "protocollazione.wsdm.posizioneAllegatoComunicazione";
	public static final String STAZIONE_APPALTANTE_PROTOCOLLANTE = "protocollazione.stazioneAppaltante";
	public static final String WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN = "wsBandiEsitiAvvisiAuthenticationToken";
	public static final String LIMITE_UPLOAD_FILE = "limiteUploadFile";
	public static final String LIMITE_TOTALE_UPLOAD_FILE = "limiteTotaleUploadDocIscrizione";
	public static final String COMUNICAZIONI_LIMITE_UPLOAD_FILE = "comunicazioni.limiteUploadFile";
	public static final String COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE = "comunicazioni.limiteTotaleUpload";
	public static final String ESTENSIONI_AMMESSE_DOC = "estensioniAmmesseDocIscrizione";
	public static final String ESTENSIONI_AMMESSE_IMMAGINE = "estensioniAmmesseImmagineProdotto";
	public static final String SUBFOLDER_TEMPLATE_JASPER = "subfolderTemplateJasper";
	public static final String MAIL_AMMINISTRATORE_SISTEMA = "mailAmministratoreSistema";
	public static final String TIPOLOGIE_ASSISTENZA = "assistenza.tipologie";
	public static final String MODO_ASSISTENZA = "assistenza.modo";
	public static final String MAIL_ASSISTENZA = "assistenza.mail";
	public static final String TELEFONO_ASSISTENZA = "assistenza.telefono";
	public static final String NOME_CLIENTE = "nomeCliente";
	public static final String URL_SERVIZIO_ASSISTENZA = "assistenza.hda.url";
	public static final String USER_SERVIZIO_ASSISTENZA = "assistenza.hda.username";
	public static final String PASSWORD_SERVIZIO_ASSISTENZA = "assistenza.hda.password";
	public static final String IDPRODOTTO_SERVIZIO_ASSISTENZA = "assistenza.hda.productId";
	public static final String TABELLATI_REFRESH_INTERVAL = "tabellatiRefreshInterval";
	public static final String SPID_WS_AUTHSERVICESPID_URL = "sso.spid.wsAuthServiceSPID.url";	
	public static final String SPID_SERVICEPROVIDER = "sso.spid.serviceprovider";
	public static final String SPID_SERVICEINDEX = "sso.spid.serviceindex";
	public static final String SPID_AUTHLEVEL = "sso.spid.authlevel";
	public static final String NSO_URL = "nso.baseUrl";


	/**
	 * UID
	 */
	private static final long serialVersionUID = -8431943128046563070L;

	/** Mappa delle variabili JNDI se definite per l'applicativo. */
	private Map<String, Object> jndiEnvironments;

	/** Reference al DAO per recuperare le configurazioni nel DB. */
	private IAppParamDAO appParamDAO;

	private Map<String, AppParam> map;
	
	private WSDMAppParams wsdmParams;
	
	private boolean configSAPresente; 


	public void setJndiEnvironments(Map<String, Object> jndiEnvironments) {
		this.jndiEnvironments = jndiEnvironments;
	}

	public void setAppParamDAO(IAppParamDAO appParamDAO) {
		this.appParamDAO = appParamDAO;
	}
	
	public void setWsdmParams(WSDMAppParams wsdmParams) {
		this.wsdmParams = wsdmParams;
	}
	
	public boolean isConfigStazioneAppaltantePresente() {
		return configSAPresente;
	}

	@Override
	public void init() throws Exception {
		InitialContext ctx = new InitialContext();
		this.jndiEnvironments = new HashMap<String, Object>();
		this.getJndiEnvironment(ctx, LAYOUT_STYLE, null);
		this.getJndiEnvironment(ctx, NTP_SERVER, null);
		this.getJndiEnvironment(ctx, NTP_TIMEOUT, null);
		this.getJndiEnvironment(ctx, WS_OPERAZIONI_GENERALI, null);
		this.getJndiEnvironment(ctx, WS_GARE_APPALTO, null);
		this.getJndiEnvironment(ctx, WS_BANDI_ESITI_AVVISI, null);
		this.getJndiEnvironment(ctx, WS_REPORT, null);
		this.getJndiEnvironment(ctx, WS_ASTE, null);
		this.getJndiEnvironment(ctx, PROTOCOLLAZIONE_TIPO, JNDI_ENV_PROTOCOLLAZIONE_TIPO);
		this.getJndiEnvironment(ctx, PROTOCOLLAZIONE_MAIL_DESTINATARI, JNDI_ENV_PROTOCOLLAZIONE_MAIL_DESTINATARI);
		this.getJndiEnvironment(ctx, PROTOCOLLAZIONE_MAIL_ALLEGA_FILE, JNDI_ENV_PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
		this.getJndiEnvironment(ctx, WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN, null);
		this.getJndiEnvironment(ctx, LIMITE_UPLOAD_FILE, null);
		this.getJndiEnvironment(ctx, LIMITE_TOTALE_UPLOAD_FILE, null);
		this.getJndiEnvironment(ctx, ESTENSIONI_AMMESSE_DOC, null);
		this.getJndiEnvironment(ctx, ESTENSIONI_AMMESSE_IMMAGINE, null);
		this.getJndiEnvironment(ctx, SUBFOLDER_TEMPLATE_JASPER, null);
		this.getJndiEnvironment(ctx, MAIL_AMMINISTRATORE_SISTEMA, null);
		this.getJndiEnvironment(ctx, TIPOLOGIE_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, MODO_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, MAIL_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, NOME_CLIENTE, null);
		this.getJndiEnvironment(ctx, URL_SERVIZIO_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, USER_SERVIZIO_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, PASSWORD_SERVIZIO_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, IDPRODOTTO_SERVIZIO_ASSISTENZA, null);
		this.getJndiEnvironment(ctx, NSO_URL, null);
		this.loadAppParams();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}
	
	/**
	 * Estrae l'eventuale variabile JNDI definita nel contesto dell'applicativo.
	 * Di default le variabili non dovrebbero essere presenti, per cui si
	 * dovrebbe generare l'eccezione NameNotFoundException.
	 * 
	 * @param ctx
	 *            context jndi
	 * @param param
	 *            parametro corrispondente di memorizzazione
	 * @param env
	 *            variabile jndi da leggere, se nullo vale param
	 */
	private void getJndiEnvironment(InitialContext ctx, String param, String env)
			throws NamingException {
		if (env == null) {
			env = param;
		}
		try {
			this.jndiEnvironments.put(param, ctx.lookup("java:comp/env/" + env));
			ApsSystemUtils.getLogger().info(
					"JNDI \"" + env + "\" definito in META-INF/context.xml");
		} catch (NameNotFoundException e) {
		} catch (NamingException e) {
		}
	}

	/**
	 * Caricamento da db del catalogo delle customizzazioni.
	 * 
	 * @throws ApsSystemException
	 *             In caso di errori di lettura da db.
	 */
	private void loadAppParams() throws ApsSystemException {
		try {
			List<AppParam> list = this.appParamDAO.loadAppParams();
			map = this.checkJNDI(list);
		} catch (Throwable t) {
			throw new ApsSystemException(
					"Errore durante la lettura delle configurazioni dell'applicativo in ppcommon_properties",
					t);
		}
	}

	/**
	 * Controlla la lista in input e verifica se sono presenti configurazioni
	 * JNDI; genera la mappa marcando le configurazioni reperite come
	 * provenienti da JNDI ed aggiornando il valore con quanto letto come
	 * risorsa JNDI.
	 * 
	 * @param list
	 *            lista da controllare
	 */
	private Map<String, AppParam> checkJNDI(List<AppParam> list) {
		Map<String, AppParam> map = new LinkedHashMap<String, AppParam>();
		for (AppParam p : list) {
			Object value = this.jndiEnvironments.get(p.getName());
			// vince la configurazione JNDI su quella da DB
			if (value != null) {
				p.setObjectValue(value);
				p.setValue(value.toString());
				p.setJndi(true);
			} else {
				boolean isNull = (p.getValue() == null || 
						          (p.getValue() != null && StringUtils.stripToNull(p.getValue()) == null));
				if ("I".equals(p.getType())) {
					p.setObjectValue(isNull ? new Integer(0) : Integer.valueOf(p.getValue()));
				} else if ("B".equals(p.getType())) {
					p.setObjectValue(isNull ? new Boolean(false) : Boolean.valueOf(p.getValue()));
				} else {
					p.setObjectValue(p.getValue());
				}
				p.setJndi(false);
			}
			map.put(p.getName(), p);
		}
		return map;
	}
	
	@Override
	public void setStazioneAppaltanteProtocollazione(String stazioneAppaltante) {
		this.configSAPresente = false;
		if(StringUtils.isNotEmpty(stazioneAppaltante)) {
			this.wsdmParams.loadAppParams(stazioneAppaltante, this.map);
			
			//String codiceSistema = this.getCodiceSistemaProtocollo();
			
			// recupera le configurazioni della SA e di default 
			// ed utilizza la prima configurazione "valida" nel seguente ordine 
			// SA > Default > Portale > nessuna
			if(this.wsdmParams.getIdConfig() >= 0 || this.wsdmParams.getConfigPortale() == 1) {
				this.configSAPresente = true;
			}
		} else {
			// rimuovi la sessione dall'elenco delle sessioni in fase di protocollazione
			this.wsdmParams.endProtocollazione();
		}
	}
	
	@Override
	public Object getConfigurationValue(String name) {
		Object value = null;
	
		// verifica se esiste il parametro relativo alla configurazione wsdm 
		// la configurazione del Portale viene utilizzata solo se non
		// esistono in BO configurazioni per la stazione appaltante e 
		// di default
		boolean portaleConfig = true;
		String sa = this.wsdmParams.getCurrentSA();

		// CASO PARTICOLARE: codiceSistema del protocollo
		if(PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA.equals(name)) {
			value = this.getCodiceSistemaProtocollo();
			portaleConfig = false;
		} else {
			if(this.wsdmParams.existsConfiguration(name, sa)) {
				// verifica se esiste una configurazione per la stazione appaltante 
				if(this.wsdmParams.activeConfiguration(sa)) {
					value = this.wsdmParams.getConfigurationValue(name);
					portaleConfig = false;
				} else {
					// NON utilizzare la configurazione del portale
					portaleConfig = false;
				}
			} else if(this.wsdmParams.existsConfiguration(name, null)) {
				// verifica se esiste la configurazione di default
				if(this.wsdmParams.activeConfiguration(null)) {
					value = this.wsdmParams.getConfigurationValue(name);
					portaleConfig = false;
				} else {
					// NON utilizzare la configurazione del portale
					portaleConfig = false;
				}
			} else {
				// se non esistono configurazioni per la stazione appaltate e di default
				// utilizza la configurazione del portale
				portaleConfig = true;
			}
		}
		if(portaleConfig) {
			if (this.map.get(name) != null) {
				value = this.map.get(name).getObjectValue();
			}
		}
		
		// CASO PARTICOLARE: tipo protocollazione 
		// se l'url del wsdm e' vuoto, non si utilizza la protocollazione
		if(PROTOCOLLAZIONE_TIPO.equals(name)) {
			if(value != null && (Integer)value == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) {
				String url = (String)this.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_URL);
				if(StringUtils.isEmpty(url)) {
					value = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				}
			}
		}
	
		return value;
	}
	
	@Override
	public List<AppParam> getAppParams() throws ApsSystemException {
		List<AppParam> lista = null;
		try {
			lista = this.appParamDAO.loadAppParams();
			this.checkJNDI(lista);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getAppParams");
			throw new ApsSystemException(
					"Errore durante la lettura delle configurazioni dell'applicativo in ppcommon_properties",
					t);
		}
		return lista;
	}

	@Override
	public void updateAppParams(List<AppParam> configs)
			throws ApsSystemException {
		if(configs != null) {
			try {
				this.appParamDAO.updateAppParams(configs);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "updateAppParams");
				throw new ApsSystemException(
						"Errore durante l'aggiornamento delle configurazioni dell'appplicativo in ppcommon_properties",
						t);
			}
			this.loadAppParams();
		}
	}

	@Override
	public void updateDefaultAppParams(String[] names)
			throws ApsSystemException {
		try {
			this.appParamDAO.updateDefaultAppParams(names);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateDefaultAppParams");
			throw new ApsSystemException(
					"Errore durante il ripristino delle configurazioni dell'appplicativo in ppcommon_properties",
					t);
		}
		this.loadAppParams();
	}
	
	/**
	 * Recupera la configurazione "codiceSistema", che nella nuova
	 * modalit&agrave; basata sulla lettura dei dati da backoffice, si basa
	 * sulla lettura del valore usando il servizio WSDM di configurazione,
	 * mentre nella versione legacy il valore si recupera direttamente nella
	 * configurazione nel backend di amministrazione del portale.
	 * 
	 * @return codiceSistema (in formato String)
	 */
	private Object getCodiceSistemaProtocollo() {
		Object codice = null;
		
		String endpoint = StringUtils.stripToNull((String)this.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_URL_CONFIG));
		WSDMConfigurazione_PortType port = null;
		if (endpoint != null) {
			// nuova modalita' con configurazione in backoffice e codice sistema reperibile mediante chiamata al servizio di configurazione WSDM
			synchronized (this) {
				if (port == null) {
				    // Encode della password
					String errorMsg = "Servizio WSDM di configurazione non raggiungibile o non attivo";
				    try {
					    WSDMConfigurazione_ServiceLocator wsdmprotocolloLocator = new WSDMConfigurazione_ServiceLocator();
					    wsdmprotocolloLocator.setWSDMConfigurazioneImplPortEndpointAddress(endpoint);
					    Remote remote = wsdmprotocolloLocator.getPort(WSDMConfigurazione_PortType.class);
					    Stub axisPort = (Stub) remote;
					    port = (WSDMConfigurazione_PortType) axisPort;
					    
						// recupera il codice sistema dal servizio
					    WSDMConfigurazioneOutType conf = port.WSDMConfigurazioneLeggi();	
					    codice = conf.getRemotewsdm();
					    
				    } catch (ServiceException e) {
				    	ApsSystemUtils.getLogger().error(errorMsg);
				    	throw new RuntimeException(errorMsg, e); 
					} catch (RemoteException e) {
						ApsSystemUtils.getLogger().error(errorMsg);
						throw new RuntimeException(errorMsg, e);
					} catch (Throwable e) {
						ApsSystemUtils.getLogger().error(errorMsg, e);
						throw new RuntimeException(errorMsg, e);
					}
				}
			}
		} else {
			// vecchia modalita' con utilizzo del dato estratto nel backend di amministrazione del portale
			codice = this.map.get(PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA).getObjectValue();
		}
		
		return codice;
	}

}
