package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazione_PortType;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazione_ServiceLocator;
import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Servizio di gestione delle configurazioni della verticalizzazione definita
 * sul sistema.<br/>
 * Non viene gestita la sincronizzazione sull'istanza nel metodo
 * getConfigurationValue e nel blocco this.checkJNDI(list) dentro il metodo
 * loadAppParams per non peggiorare le performance dato che le configurazioni
 * piu' critiche avverranno solo in fase di aggiornamento dell'applicativo.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public class AppParamManager extends AbstractService implements
		IAppParamManager {
	
	private final Logger logger = ApsSystemUtils.getLogger();
	
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
	public static final String WS_STIPULE_APPALTO = "wsStipuleAppalto";
	public static final String WS_LFS_APPALTO = "wsLfsAppalto";
	public static final String WS_ASTE = "wsAste";
	public static final String WS_BANDI_ESITI_AVVISI = "wsBandiEsitiAvvisi";
	public static final String WS_REPORT = "wsReport";
	public static final String WS_PAGO_PA = PortGareSystemConstants.WS_PAGO_PA;
	public static final String PUBBLICAZIONE_NUM_ANNI = "pubblicazione.numAnni";
	public static final String PROTOCOLLAZIONE_TIPO = "protocollazione.tipo";
	public static final String PROTOCOLLAZIONE_MAIL_DESTINATARI = "protocollazione.mail.destinatari";
	public static final String PROTOCOLLAZIONE_MAIL_ALLEGA_FILE = "protocollazione.mail.allegaFile";
	public static final String PROTOCOLLAZIONE_WSDM_FASCICOLOPROTOCOLLO_URL = "wsdm.fascicoloprotocollo.url";
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
	public static final String PROTOCOLLAZIONE_WSDM_DESC_UNITA_OPERATIVA_DESTINATARIA = "protocollazione.wsdm.descUnitaOperativaDestinataria";
	public static final String PROTOCOLLAZIONE_WSDM_TIPO_FIRMA = "protocollazione.wsdm.tipoFirma";
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
	public static final String PROTOCOLLAZIONE_WSDM_STIPULE_CLASSIFICA = "protocollazione.wsdm.stipule.classifica";
	public static final String PROTOCOLLAZIONE_WSDM_STIPULE_TIPO_DOCUMENTO = "protocollazione.wsdm.stipule.tipoDocumento";
	public static final String PROTOCOLLAZIONE_WSDM_STIPULE_CODICE_REGISTRO = "protocollazione.wsdm.stipule.registro";
	public static final String PROTOCOLLAZIONE_WSDM_STIPULE_INDICE = "protocollazione.wsdm.stipule.indice";
	public static final String PROTOCOLLAZIONE_WSDM_STIPULE_TITOLAZIONE = "protocollazione.wsdm.stipule.titolazione";
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
	public static final String TIPOLOGIE_COMUNICAZIONI = "comunicazioni.tipologie";
	public static final String MODO_ASSISTENZA = "assistenza.modo";
	public static final String MAIL_ASSISTENZA = "assistenza.mail";
	public static final String TELEFONO_ASSISTENZA = "assistenza.telefono";
	public static final String NOME_CLIENTE = "nomeCliente";
	public static final String URL_SERVIZIO_ASSISTENZA = "assistenza.hda.url";
	public static final String USER_SERVIZIO_ASSISTENZA = "assistenza.hda.username";
	public static final String PASSWORD_SERVIZIO_ASSISTENZA = "assistenza.hda.password";
	public static final String IDPRODOTTO_SERVIZIO_ASSISTENZA = "assistenza.hda.productId";
	public static final String TABELLATI_REFRESH_INTERVAL = "tabellatiRefreshInterval";
	public static final String SPID_WS_AUTHSERVICESPID_URL = "auth.sso.spid.wsAuthServiceSPID.url";	
	public static final String SPID_SERVICEPROVIDER = "auth.sso.spid.serviceprovider";
	public static final String SPID_SERVICEINDEX = "auth.sso.spid.serviceindex";
	public static final String SPID_AUTHLEVEL = "auth.sso.spid.authlevel";
	public static final String CIE_WS_AUTHSERVICESPID_URL = "auth.sso.cie.wsAuthServiceCIE.url";	
	public static final String CIE_SERVICEPROVIDER = "auth.sso.cie.serviceprovider";
	public static final String CIE_SERVICEINDEX = "auth.sso.cie.serviceindex";
	public static final String CIE_AUTHLEVEL = "auth.sso.cie.authlevel";
	public static final String CRS_WS_AUTHSERVICECRS_URL = "auth.sso.crs.wsAuthServiceCRS.url";	
	public static final String CNS_WS_AUTHSERVICESCNS_URL = "auth.sso.cns.wsAuthServiceCNS.url";
	public static final String MYID_WS_AUTHSERVICEMYID_URL = "auth.sso.myid.wsAuthServiceMyId.url";
	public static final String MYID_SERVICEMYID_URL = "auth.sso.myid.serviceMyId.url";
	public static final String GEL_WS_AUTHSERVICEGEL_URL = "auth.sso.gel.wsAuthServiceGEL.url";
	public static final String GEL_SERVICEPROVIDER = "auth.sso.gel.serviceprovider";
	public static final String FEDERA_WS_AUTHSERVICEFEDERA_URL = "auth.sso.federa.wsAuthServiceFedera.url";
	public static final String FEDERA_SERVICEPROVIDER = "auth.sso.federa.serviceprovider";
	public static final String SPIDBUSINESS_AUTHPURPOSE = "auth.sso.spidbusiness.authpurpose"; 
	public static final String CLAUSOLEPIATTAFORMA_VERSIONE = "clausolePiattaforma.versione";
	public static final String IMPORT_UTENTI_DATA_TERMINE = "importMassivoUtenti.dataTermine";
	public static final String URL_ALBO_FORNITORI_ESTERNI = "urlAlboFornitoriEsterni";
	
	public static final String NSO_URL = "nso.baseUrl";
	public static final String NSO_FATT_URL = "nso.baseUrlFatt";
	public static final String NSO_FATT_AUTH = "nso.fatt.auth";

	public static final String DENOMINAZIONE_STAZIONE_APPALTANTE_UNICA = "denominazioneStazioneAppaltanteUnica";
	
	public static final String CATEGORY_PRIVACY = "privacy";
	
	public static final String SHOW_DEFAULT_TERM_OF_USE 			= "privacy.pubblicaCondizioniUsoStd";
	public static final String NOME_TITOLARE 						= "privacy.nomeTitolare";
	public static final String SEDE_TITOLARE 						= "privacy.sedeTitolare";
	public static final String MAIL_TITOLARE 						= "privacy.mailTitolare";
	public static final String MAIL_DPO 							= "privacy.mailDPO";
	public static final String NOME_GESTORE  						= "privacy.nomeGestore";
	public static final String SEDE_GESTORE 						= "privacy.sedeGestore";
	public static final String MAIL_GESTORE  						= "privacy.mailGestore";
	public static final String DURATA_LOG_NAVIGAZIONE 				= "privacy.durataLogNavigazione";
	public static final String DURATA_CONSERVAZIONE_CONTATTI_MAIL 	= "privacy.durataConservazioneContattiMail";
	public static final String DURATA_DATI_ELENCO 					= "privacy.durataDatiElenco";
	public static final String DURATA_DATI_GARE 					= "privacy.durataDatiGare";
	public static final String DURATA_DATI_NON_TRASMESSI 			= "privacy.durataDatiNonTrasmessi";
	public static final String DATA_INIZIO_VALIDITA_POLICY 			= "privacy.dataInizioValiditaPolicy";
	public static final String NOME_PIATTAFORMA 					= "privacy.nomePiattaforma";
	public static final String LOCALITA_FORO 						= "privacy.localitaForo";

	public static final String CATEGORY_AUTH_GATEWAY 				= "autenticazione-gateway";

	public static final String GATEWAY 								= "auth.sso.gateway";
	public static final String GATEWAY_POSITION 					= "auth.sso.gateway.position";
	public static final String GATEWAY_URL		 					= "auth.sso.gateway.ws.url";
	public static final String GATEWAY_ENDPOINT 					= "auth.sso.gateway.endpoint";
	public static final String GATEWAY_CLIENT_ID 					= "auth.sso.gateway.clientId";
//	public static final String GATEWAY_AUTH_TYPE 					= "auth.sso.gateway.authtype";
	public static final String GATEWAY_PASSPHRASE 					= "auth.sso.gateway.passphrase";

	public static final String HOSTS_ALLOWED 						= "httpHeader.host.allowed";

	public static final String SPIDBUSINESS_WS_AUTHSERVICESPID_URL = "auth.sso.spidbusiness.wsAuthServiceSPID.url";
	public static final String SPIDBUSINESS_SERVICEPROVIDER = "auth.sso.spidbusiness.serviceprovider";
	public static final String SPIDBUSINESS_SERVICEINDEX = "auth.sso.spidbusiness.serviceindex";
	public static final String SPIDBUSINESS_AUTHLEVEL = "auth.sso.spidbusiness.authlevel";


	/**
	 * UID
	 */
	private static final long serialVersionUID = -8431943128046563070L;

	/** Mappa delle variabili JNDI se definite per l'applicativo. */
	private Map<String, Object> jndiEnvironments;

	/** Reference al DAO per recuperare le configurazioni nel DB. */
	private IAppParamDAO appParamDAO;

	private final Map<String, AppParam> map = new LinkedHashMap<>();
	
	private WSDMAppParams wsdmParams;
	
	private boolean configSAPresente; 
	private final boolean useNewConfiguration = Boolean.FALSE;
	
	/* indica se e' stata impostata una configurazione di default vuota (o inesistente) per la protocollazione con WSDM */
	private boolean configWSDMNonDisponibile;


	public void setJndiEnvironments(Map<String, Object> jndiEnvironments) {
		this.jndiEnvironments = jndiEnvironments;
	}

	public void setAppParamDAO(IAppParamDAO appParamDAO) {
		this.appParamDAO = appParamDAO;
	}
	
	public void setWsdmParams(WSDMAppParams wsdmParams) {
		this.wsdmParams = wsdmParams;
	}
	
	@Override
	public boolean isConfigStazioneAppaltantePresente() {
		return configSAPresente;
	}
	
	@Override
	public boolean isConfigWSDMNonDisponibile() {
		return configWSDMNonDisponibile;
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
		this.getJndiEnvironment(ctx, WS_STIPULE_APPALTO, null);
		this.getJndiEnvironment(ctx, WS_PAGO_PA, null);
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
		this.getJndiEnvironment(ctx, NSO_FATT_URL, null);
		this.getJndiEnvironment(ctx, NSO_FATT_AUTH, null);
		
		this.loadAppParams();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": inizializzato ");
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
			this.map.putAll(this.checkJNDI(list,useNewConfiguration));
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
		Map<String, AppParam> map = new LinkedHashMap<String, AppParam>(list.size());
		if(list!=null) {
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
						p.setObjectValue(isNull ? Integer.valueOf(0) : Integer.valueOf(p.getValue()));
					} else if ("B".equals(p.getType())) {
						p.setObjectValue(isNull ? Boolean.FALSE : Boolean.valueOf(p.getValue()));
					} else {
						p.setObjectValue(p.getValue());
					}
					p.setJndi(false);
				}
				map.put(p.getName(), p);
			}
		}
		return map;
	}
	
	/**
	 * Metodo che utilizza Java8 per ricostruire la mappa degli AppParam
	 * Il comportamento e' stato modificato in modo comunque da inserire i dati dell'ENV 
	 * nella mappa anche se non presenti a DB
	 * @param list la lista di AppParam da verificare
	 * @param newAlg true se si vuole usare java8, false altrimenti
	 * @return la mappa creata
	 */
	private Map<String, AppParam> checkJNDI(List<AppParam> list, boolean newAlg){
		if(!newAlg) return checkJNDI(list);
		//TODO STEP 1 trasformo gli oggetti JNDI in oggetti AppParam
		//TODO trasformo in una mappa attraverso Collectors.toMap
//		Map<String, AppParam> map = this.jndiEnvironments.entrySet().parallelStream().filter(el->Objects.nonNull(el.getValue())).map(el->{
//			AppParam p = new AppParam();
//			p.setJndi(true);
//			p.setName(el.getKey());
////			ApsSystemUtils.getLogger().trace("classFor -> {}:{}",el.getKey(),el.getValue().getClass().getSimpleName());
//			p.setObjectValue(el.getValue());
//			if(el.getValue()!=null && Integer.class.getSimpleName().equals(el.getValue().getClass().getSimpleName())) {
//				p.setObjectValue(Integer.valueOf(el.getValue().toString()));
//			} else if(el.getValue()!=null && Boolean.class.getSimpleName().equals(el.getValue().getClass().getSimpleName())) {
//				p.setObjectValue(Boolean.valueOf(el.getValue().toString()));
//			}
//			p.setValue(el.getValue().toString());
//			return p;
//		}).collect(Collectors.toMap(AppParam::getName,Function.identity() ));
		
		// STEP 1 trasformo gli oggetti JNDI in oggetti AppParam
		Map<String, AppParam> map = new LinkedHashMap<String, AppParam>();
		for(Entry<String, Object> el : this.jndiEnvironments.entrySet() ) {
			if(el.getValue()!=null) {
				AppParam p = new AppParam();
				p.setJndi(true);
				p.setName(el.getKey());
				p.setObjectValue(el.getValue());
				if(el.getValue()!=null && Integer.class.getSimpleName().equals(el.getValue().getClass().getSimpleName())) {
					p.setObjectValue(Integer.valueOf(el.getValue().toString()));
				} else if(el.getValue()!=null && Boolean.class.getSimpleName().equals(el.getValue().getClass().getSimpleName())) {
					p.setObjectValue(Boolean.valueOf(el.getValue().toString()));
				}
				p.setValue(el.getValue().toString());
				map.put(p.getName(), p);
			}
		}
		
		//TODO step 2 faccio merge dei dati dove ovviamente hanno precedenza i JNDI
//		list.parallelStream().forEach(el->{
//			if ("I".equals(el.getType())) {
//				el.setObjectValue(StringUtils.isEmpty(el.getValue()) ? new Integer(0) : Integer.valueOf(el.getValue()));
//			} else if ("B".equals(el.getType())) {
//				el.setObjectValue(StringUtils.isEmpty(el.getValue()) ? new Boolean(false) : Boolean.valueOf(el.getValue()));
//			} else {
//				el.setObjectValue(el.getValue());
//			}
//			map.putIfAbsent(el.getName(), el);
//			});
		for(AppParam el : list) {
			if(!map.containsKey(el.getName())) {
				if ("I".equals(el.getType())) {
					el.setObjectValue(StringUtils.isEmpty(el.getValue()) ? Integer.valueOf(0) : Integer.valueOf(el.getValue()));
				} else if ("B".equals(el.getType())) {
					el.setObjectValue(StringUtils.isEmpty(el.getValue()) ? Boolean.valueOf(false) : Boolean.valueOf(el.getValue()));
				} else {
					el.setObjectValue(el.getValue());
				}
				map.put(el.getName(), el);
			}
		}
		ApsSystemUtils.getLogger().info("Maps.size: {}",map.size());
		return map;
	}
	
	@Override
	public void setStazioneAppaltanteProtocollazione(String stazioneAppaltante) {
		this.configSAPresente = false;
		this.configWSDMNonDisponibile = false;
		if(StringUtils.isNotEmpty(stazioneAppaltante)) {
			// apri la fase di protocollazione...
			this.wsdmParams.loadAppParams(stazioneAppaltante, this.map);
			
			//String codiceSistema = this.getCodiceSistemaProtocollo();
			
			// recupera le configurazioni della SA e di default 
			// ed utilizza la prima configurazione "valida" nel seguente ordine 
			// SA > Default > (Portale DEPRECATA) > nessuna
			//if(this.wsdmParams.getIdConfig() >= 0 || this.wsdmParams.getConfigPortale() == 1) {
			if(this.wsdmParams.getIdConfig() >= 0) {
				this.configSAPresente = true;
			}
		} else {
			// concludi la fase di protocollazione...
			// rimuovi la sessione dall'elenco delle sessioni in fase di protocollazione
			this.wsdmParams.endProtocollazione();
		}
	}

	@Override
	public Integer getTipoProtocollazione(String stazioneAppaltante) {
		// NB: 
		// il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!!
		// per capire se e' stata impostata la protocollazione con WSDM e' necessario leggere "protocollazione.tipo" 
		// dalla configurazione del Portale
		Integer tipoProtocollazione = (Integer)this.map.get(AppParamManager.PROTOCOLLAZIONE_TIPO).getObjectValue();
		if(tipoProtocollazione == null) {
			tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		}
		
		// in caso di protocollazione.tipo = 2 (WSDM) verifica se: 
		// non e' stata selezionata una configurazione di default
		// o se e' stata selezionata una configurazione di default vuota
		// e mostra il messaggio "Attenzione: non e' possibile completare l'invio per indisponibilità del sistema di protocollazione"
		if(tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM && this.wsdmParams.isDefaultConfigEmpty() ) {
			this.configWSDMNonDisponibile = true;
			tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		}

		// se la configurazione WSDM della stazione appaltante non e' presente, 
		// la protocollazione non e' prevista
		if(tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM &&
		   !this.isConfigStazioneAppaltantePresente()) 
		{
			tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		}
		
		// verifica se e' prevista la protocollazione per la SA protocollante
		if(StringUtils.isNotEmpty(stazioneAppaltante)) {
			String saProtocollante = (String) this.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
			
			boolean isProtocollazionePrevista = 
					(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA != tipoProtocollazione) &&
					 ( (StringUtils.stripToNull(saProtocollante) == null) || (saProtocollante.equals(stazioneAppaltante)) );
			
			if( !isProtocollazionePrevista ) {
				tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
		}

		return tipoProtocollazione;
	}
	
	@Override
	public Integer getTipoProtocollazione() throws Exception {
		return this.getTipoProtocollazione(null);
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
			if(value == null) {
				// imposta un valore di default
				value = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
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
	public Integer getConfigurationValueIntDef(String name, Integer defValue) {
		try {
			return (Integer) getConfigurationValue(name);
		} catch(Exception e) {
			return defValue;
		}
	}
	
	@Override
	public Long getConfigurationValueLong(String name, Long defValue) {
		try {
			return (Long) getConfigurationValue(name);
		} catch(Exception e) {
			return defValue;
		}
	}
	
	@Override
	public Boolean getConfigurationValueBoolean(String name, Boolean defValue) {
		try {
			return (Boolean) getConfigurationValue(name);
		} catch(Exception e) {
			return defValue;
		}
	}
	
	@Override
	public List<AppParam> getAppParams() throws ApsSystemException {
		List<AppParam> lista = null;
		try {
			lista = this.appParamDAO.loadAppParams();
			this.checkJNDI(lista,useNewConfiguration);
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
		
		// controlla se e' attiva la protocollazione via WSDM
		Integer tipoProtocollazione = null;
		if (this.map.get(AppParamManager.PROTOCOLLAZIONE_TIPO) != null) {
			tipoProtocollazione = (Integer) this.map.get(AppParamManager.PROTOCOLLAZIONE_TIPO).getObjectValue();
		}
		if (tipoProtocollazione == null) { 
			tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		}
		
		if (endpoint != null && tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) {
			// nuova modalita' con configurazione in backoffice e 
			// codice sistema reperibile mediante chiamata al servizio di configurazione WSDM
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

	@Override
	public List<String> retrieveCategories() 
		throws ApsSystemException {
			try {
				return this.appParamDAO.retrieveCategories();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "getCategorie");
				throw new ApsSystemException(
						"Errore durante la ricerca delle categorie in ppcommon_properties",
						t);
			}
	}
	
	@Override
	public List<AppParam> getAppParamsByCategory(String category) throws ApsSystemException {
		List<AppParam> lista = null;
		try {
			lista = this.appParamDAO.loadAppParamsByCategoory(category);
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
	public Map<String,AppParam> getMapAppParamsByCategory(String category) throws ApsSystemException {
		try {
			return this.checkJNDI(this.appParamDAO.loadAppParamsByCategoory(category));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getAppParams");
			throw new ApsSystemException(
					"Errore durante la lettura delle configurazioni dell'applicativo in ppcommon_properties",
					t);
		}
	}
	
	@Override
	public List<String> loadEnabledAuthentications()throws ApsSystemException {
		try {
			return this.appParamDAO.loadEnabledAuthentications();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadEnabledAuthentications");
			throw new ApsSystemException(
					"Errore durante la ricerca dei sitemi di autenticazione in ppcommon_properties",
					t);
		}
	}
	
	@Override
	public List<AppParam> loadEnabledAuthenticationsPositions(List<String> authentications) throws ApsSystemException {
		try {
			return this.appParamDAO.loadEnabledAuthenticationsPositions(authentications);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadEnabledAuthenticationsPositions");
			throw new ApsSystemException(
					"Errore durante la ricerca delle posizioni dei sitemi di autenticazione in ppcommon_properties",
					t);
		}
	}
	
}
