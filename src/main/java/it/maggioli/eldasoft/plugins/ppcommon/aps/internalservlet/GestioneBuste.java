package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;


/**
 * gestore delle buste di una domanda di partecipazione/invio offerta 
 */
public class GestioneBuste implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1109032304924164653L;

	private static final Logger LOGGER = ApsSystemUtils.getLogger();
	
	/**
	 * id di sessione per memorizzare l'offerta o la domanda prequalifica  
	 */
	public static final String SESSION_ID_OFFERTA_GARA 					= "dettaglioOffertaGara";
	// id sessione per retro compatibilita' con le JSP
	// (come primo step si modificano solo le classi e non lo JSP)
	private static final String SESSION_ID_DETT_ANAGRAFICA_IMPRESA		= "dettAnagrImpresa";

	/**
	 * tipologie di evento legate al barcode
	 * 
	 * - Identificativo della tipologia di evento di partecipazione ad una gara
	 * - Identificativo della tipologia di evento invia offerta per una gara
	 * - Identificativo della tipologia di evento per l'invio documentazione al
	 *   fine di comprovare i requisiti
	 * - Identificativo della tipologia di evento invia offerte distinte per una gara  
	 */
	public static final int TIPOLOGIA_EVENTO_PARTECIPA_GARA 			= 1;
	public static final int TIPOLOGIA_EVENTO_INVIA_OFFERTA 				= 2;
	public static final int TIPOLOGIA_EVENTO_COMPROVA_REQUISITI 		= 3;
	public static final int TIPOLOGIA_EVENTO_INVIA_OFFERTE_DISTINTE 	= 4;

	/**
	 * il documento "dgue request" e' l'allegato marcato idstampa='dgue' 
	 */
	public static final String DGUEREQUEST								= "dgue";
		

	/**
	 * manager e attributi
	 */
	private transient IBandiManager bandiManager;
	private transient IComunicazioniManager comunicazioniManager;
	private transient IAppParamManager appParamManager;
	private transient IEventManager eventManager;

	private String username;
	private String codiceGara;
	private String progressivoOfferta;
	private int operazione;
	private boolean domandaPartecipazione;
	private boolean invioOfferta;
	private boolean garaLotti;
	private boolean ristretta;							// itergara = 2|4
	private boolean negoziata; 							// itergara = 3|5|6|8
	private boolean indagineMercato;					// itergara = 7
	private boolean concorsoProgettazionePubblico;		// itergara = 9
	private boolean concorsoProgettazioneRiservato;		// itergara = 10
	
	private BustaPartecipazione bustaPartecipazione;	// FS10 / FS11
	private BustaPrequalifica bustaPrequalifica;		// FS10A
	private BustaAmministrativa bustaAmministrativa;	// FS11A
	private BustaTecnica bustaTecnica;					// FS11B
	private BustaEconomica bustaEconomica;				// FS11C
	private BustaRiepilogo bustaRiepilogo;				// FS10R / FS11R
	
	private WizardDatiImpresaHelper impresa;
	private DettaglioGaraType dettaglioGara;
	private File tempDir; 	

	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return comunicazioniManager;
	}
	
	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public IEventManager getEventManager() {
		return eventManager;
	}

	public String getUsername() {
		return username;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public int getOperazione() {
		return operazione;
	}

	public boolean isDomandaPartecipazione() {
		return domandaPartecipazione;
	}

	public boolean isInvioOfferta() {
		return invioOfferta;
	}
	
	public boolean isGaraLotti() {
		return garaLotti;
	}

	public boolean isRistretta() {
		return ristretta;
	}

	public boolean isNegoziata() {
		return negoziata;
	}
		
	public boolean isIndagineMercato() {
		return indagineMercato;
	}

	public boolean isConcorsoProgettazionePubblico() {
		return concorsoProgettazionePubblico;
	}

	public boolean isConcorsoProgettazioneRiservato() {
		return concorsoProgettazioneRiservato;
	}
	
	public BustaPartecipazione getBustaPartecipazione() {
		return bustaPartecipazione;
	}

	public BustaPrequalifica getBustaPrequalifica() {
		return bustaPrequalifica;
	}

	public BustaAmministrativa getBustaAmministrativa() {
		return bustaAmministrativa;
	}

	public BustaTecnica getBustaTecnica() {
		return bustaTecnica;
	}

	public BustaEconomica getBustaEconomica() {
		return bustaEconomica;
	}

	public BustaRiepilogo getBustaRiepilogo() {
		return bustaRiepilogo;
	}

	public DettaglioGaraType getDettaglioGara(boolean reload) {
		if(reload) { 
			this.refreshDettaglioGara();
		}
		return dettaglioGara;
	}
	
	public DettaglioGaraType getDettaglioGara() {
		return this.getDettaglioGara(false);
	}
	
	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}
	
	public File getTempDir() {
		return tempDir;
	}

	public static BaseAction getAction() {
		return (BaseAction) (ActionContext.getContext() != null && ActionContext.getContext().getActionInvocation() != null
				? ActionContext.getContext().getActionInvocation().getAction()
				: null
		);
	}
	
	public static Map<String, Object> getSession() {
		return ActionContext.getContext().getSession();
	}
	
	/**
	 * tracciature di un messaggio di debug
	 */
	public static void traceLog(String msg) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(msg);
		}
	}


	/**
	 * costruttore
	 */
	public GestioneBuste() {
	}
	
	/**
	 * costruttore
	 */
	public GestioneBuste(
			String username,
			String codiceGara,
			String progressivoOfferta,
			int operazione)
	{
		GestioneBuste.traceLog("GestioneBuste constructor");
		this.username = username;
		this.codiceGara = codiceGara;
		this.progressivoOfferta = (StringUtils.isEmpty(progressivoOfferta) ? "1" : progressivoOfferta);
		this.operazione = operazione;
		domandaPartecipazione = (operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA); 
		invioOfferta = !this.domandaPartecipazione; //(operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
		garaLotti = false; 
		impresa = null;
		dettaglioGara = null;
		negoziata = false;
		ristretta = false;
		indagineMercato = false;
		bustaPartecipazione = null;
		bustaPrequalifica = null;
		bustaAmministrativa = null;
		bustaTecnica = null;
		bustaEconomica = null;
		bustaRiepilogo = null;

		// recupera action ed i manager necessari
		tempDir = (File)GestioneBuste.getAction().getRequest().getSession().getServletContext().getAttribute("javax.servlet.context.tempdir");
		initManagerBeans();
		
		// inizializza le buste
		try {
			// recupera i dati dell'impresa e della gara
			impresa = this.getLastestDatiImpresa();
			refreshDettaglioGara();
			
			garaLotti = false;
			if(dettaglioGara.getLotto() != null && dettaglioGara.getLotto().length > 0)	{
				garaLotti = true;
				if(dettaglioGara.getLotto().length == 1 && 
				   codiceGara.equals(dettaglioGara.getLotto()[0].getCodiceLotto())) 
				{
					garaLotti = false;
				}
			}
//			if(!isGaraLottiDistinti())	// SERVE ??? <= gara a lotti plico unico offerta unica !!!
//				garaLotti = false;
			
			// inizializza le buste 
			if(domandaPartecipazione) {
				// inizializza riepilogo e buste per la domanda di partecipazione (prequalifica)
				bustaRiepilogo = new BustaRiepilogo(this, BustaGara.BUSTA_RIEPILOGO_PRE);
				bustaPartecipazione = new BustaPartecipazione(this, BustaGara.BUSTA_PARTECIPAZIONE);
				bustaPrequalifica = new BustaPrequalifica(this);
			} else {
				// inizializza riepilogo e buste per l'invio offerta  
				bustaRiepilogo = new BustaRiepilogo(this, BustaGara.BUSTA_RIEPILOGO);
				bustaPartecipazione = new BustaPartecipazione(this, BustaGara.BUSTA_INVIO_OFFERTA);
				bustaAmministrativa = new BustaAmministrativa(this);
				bustaTecnica = new BustaTecnica(this);
				if(!concorsoProgettazionePubblico && !concorsoProgettazionePubblico) {
					bustaEconomica = new BustaEconomica(this);
				}
			}
			
			// salva in sessione la gestione buste
			this.putToSession();
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("GestioneBuste", "GestioneBuste", t);
		}
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		// propaga l'evento di rimozione dalla sessione agli helper delle buste
		if(bustaPrequalifica != null) bustaPrequalifica.valueUnbound(event);
		if(bustaAmministrativa != null) bustaAmministrativa.valueUnbound(event);
		if(bustaTecnica != null) bustaTecnica.valueUnbound(event);
		if(bustaEconomica != null) bustaEconomica.valueUnbound(event);
	}

	/**
	 * inizializza i bean dei manager
	 */
	private void initManagerBeans() {
		bandiManager = null;
		comunicazioniManager = null;
		appParamManager = null;
		eventManager = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
			comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
			appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);
			eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("initManagerBeans", t);
		}
		
		if(bustaPartecipazione != null) bustaPartecipazione.gestioneBuste = this;
		if(bustaPrequalifica != null) bustaPrequalifica.gestioneBuste = this;
		if(bustaAmministrativa != null) bustaAmministrativa.gestioneBuste = this;
		if(bustaTecnica != null) bustaTecnica.gestioneBuste = this;
		if(bustaEconomica != null) bustaEconomica.gestioneBuste = this;
		if(bustaRiepilogo != null) bustaRiepilogo.gestioneBuste = this;
	}

	/**
	 * recupera le buste dal servizio
	 * @throws Exception 
	 */	
	public boolean get(List<String> stati) throws Throwable {
		GestioneBuste.traceLog("GestioneBuste.get");
		boolean continua = true;
		
		// leggi dalle comunicazioni i dati delle buste
		try {
			resetSession();

//			// se la lista degli stati e' vuota, usa gli stati standard...
//			if(stati == null) {
//				//stati = new ArrayList<String>();
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
//			}
			
			// recupera i dati dell'impresa e della gara...
			//impresa = getLastestDatiImpresa();
			refreshDettaglioGara();
		 
			continua = continua && bustaPartecipazione.get(stati);
			if(domandaPartecipazione) {
				// inizializza riepilogo e buste per la domanda di partecipazione (prequalifica)...
				continua = continua && bustaPrequalifica.get(stati);
			} else {
				// inizializza riepilogo e buste per l'invio offerta...
				continua = continua && bustaAmministrativa.get(stati);
				continua = continua && bustaTecnica.get(stati);
				if(bustaEconomica != null) {
					continua = continua && bustaEconomica.get(stati);
				}
			}
			continua = continua && bustaRiepilogo.get(stati);
	
		} catch (Throwable t) {
			continua = false;
			ApsSystemUtils.getLogger().error("GestioneBuste", "get", t);
		}

		// riallinea l'helper del riepilogo alle buste...
		if(continua) {
			if(bustaPartecipazione.getComunicazioneFlusso().getId() > 0 &&
			   bustaRiepilogo.getComunicazioneFlusso().getId() <= 0) 
			{
				// la comunicazione del riepilogo NON esiste
				// aggiorna il riepilogo ed invia la comunicazione FS10R/FS11R
				continua = continua && bustaRiepilogo.riallineaDocumenti(bustaPrequalifica);
				continua = continua && bustaRiepilogo.riallineaDocumenti(bustaAmministrativa);
				continua = continua && bustaRiepilogo.riallineaDocumenti(bustaTecnica);
				continua = continua && bustaRiepilogo.riallineaDocumenti(bustaEconomica);
				continua = continua && bustaRiepilogo.send(null);
			}
		}
		
		// salva in sessione la gestione buste
		if(continua) {
			this.putToSession();
		}
		
		return continua;
	}
	
	/**
	 * recupera le buste dal servizio
	 * @throws Exception 
	 */	
	public boolean get() throws Throwable {
		return get(null); 
	}
	
	/**
	 * invia la busta al servizio con la descrizione dell'operazione
	 * 
	 * @throws ApsException 
	 */
	protected boolean send() throws Exception {
		GestioneBuste.traceLog("GestioneBuste.send not implemented");
		// MIGLIORIA: implementare qui il "Conferma ed invia"
//		if(this.domandaPartecipazione) {
//			continua = continua && this.bustaPrequalifica.sendConfirm(...)
//		} else {
//			continua = continua && this.bustaAmministrativa.sendConfirm(...);
//			continua = continua && this.bustaTecnica.sendConfirm(...);
//			continua = continua && this.bustaEconomica.sendConfirm(...);
//		}
//		boolean continua = this.bustaPartecipazione.sendConfirm();
		return false;
	}

	/**
	 * recupera i dati impresa piu' recenti
	 */
	public WizardDatiImpresaHelper getLastestDatiImpresa() throws ApsException, XmlException {
		GestioneBuste.traceLog("GestioneBuste.getLastestDatiImpresa");
		return ImpresaAction.getLatestDatiImpresa(
				username, 
				(EncodedDataAction)GestioneBuste.getAction());
	}

	/**
	 * rilegge il dettaglio dei dati della gara 
	 * @throws ApsException 
	 */
	private boolean refreshDettaglioGara() {
		try {
			dettaglioGara = bandiManager.getDettaglioGara(this.codiceGara);
			ristretta = WizardPartecipazioneHelper.isGaraRistretta(dettaglioGara.getDatiGeneraliGara().getIterGara());
			negoziata = WizardPartecipazioneHelper.isGaraNegoziata(dettaglioGara.getDatiGeneraliGara().getIterGara());
			indagineMercato = "7".equals(dettaglioGara.getDatiGeneraliGara().getIterGara());			
			concorsoProgettazionePubblico = "9".equals(dettaglioGara.getDatiGeneraliGara().getIterGara());
			concorsoProgettazioneRiservato= "10".equals(dettaglioGara.getDatiGeneraliGara().getIterGara());
		} catch(Throwable t) {
			dettaglioGara = null;
			ApsSystemUtils.getLogger().error("GestioneBuste", "refreshDettaglioGara", t);
		}
		return (dettaglioGara != null);
	}

	/**
	 * restituisce se e' prevista la gestione della busta amministrativa
	 */
	public boolean isBustaAmministrativaPresente() {
		boolean noBustaAmministrativa = false;
		if(dettaglioGara != null && dettaglioGara.getDatiGeneraliGara() != null) {
			noBustaAmministrativa = dettaglioGara.getDatiGeneraliGara().isNoBustaAmministrativa();
		}
		return !noBustaAmministrativa;
	}
	
	/**
	 * restituisce se e' prevista la gestione della busta amministrativa
	 * @throws ApsException 
	 */
	public boolean isGaraConOffertaTecnica() throws ApsException {
		return (boolean) bandiManager.isGaraConOffertaTecnica(codiceGara);
	}
	
	/**
	 * restituisce la busta in base al tipo busta 
	 */
	public BustaDocumenti getBusta(int tipoBusta) throws Exception {
		BustaDocumenti busta = null;
		if( tipoBusta == BustaGara.BUSTA_PRE_QUALIFICA) {
			busta = getBustaPrequalifica();
		} else if(tipoBusta == BustaGara.BUSTA_AMMINISTRATIVA) {
			busta = getBustaAmministrativa();
		} else if( tipoBusta == BustaGara.BUSTA_TECNICA) {
			busta = getBustaTecnica();
		} else if( tipoBusta == BustaGara.BUSTA_ECONOMICA) {
			busta = getBustaEconomica();
		} else {
			throw new Exception("getBusta() Tipologia di busta non valida");
		}
		return busta;
	}
	
	/**
	 * restituisce l'helper dei documenti in base al tipo busta 
	 */
	public WizardDocumentiBustaHelper getDocumentiBustaHelper(int tipoBusta) throws Exception {
		WizardDocumentiBustaHelper helper = null;
		BustaDocumenti busta = getBusta(tipoBusta);
		if(busta != null) {
			helper = busta.getHelperDocumenti();
		} else {
			throw new Exception("getDocumentiBustaHelper() Tipologia di busta non valida");
		}
		return helper;
	}

	/**
	 * recupera dalla sessione l'oggetto per codice lotto 
	 */
	public static GestioneBuste getFromSession(String codiceLotto) {
		GestioneBuste.traceLog("GestioneBuste.getFromSession");
		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		GestioneBuste buste = (GestioneBuste)action.getRequest().getSession().getAttribute(SESSION_ID_OFFERTA_GARA);
		
		// se e' una gara a lotti, inizializza il codice lotto nell'helper dei documenti per la busta TEC/ECO
		if(buste != null && StringUtils.isNotEmpty(codiceLotto)) {
			if(buste.getBustaEconomica() != null) {
				buste.getBustaEconomica().codiceLotto = codiceLotto;
				WizardDocumentiBustaHelper documenti = null;
				if(buste.getBustaEconomica().getHelper() != null) {
					documenti = buste.getBustaEconomica().getHelper().getDocumenti();
				} else {
					documenti = buste.getBustaEconomica().helperDocumenti;
				}
				if(documenti != null) {
					documenti.setCodice(codiceLotto);
				}
			}
				
			if(buste.getBustaTecnica() != null) {
				buste.getBustaTecnica().codiceLotto = codiceLotto;
				WizardDocumentiBustaHelper documenti = null;
				if(buste.getBustaTecnica().getHelper() != null) {
					documenti = buste.getBustaTecnica().getHelper().getDocumenti();
				} else {
					documenti = buste.getBustaTecnica().helperDocumenti;
				}
				if(documenti != null) {
					documenti.setCodice(codiceLotto);
				}
			}
		}
		
		return buste;
	}
	
	/**
	 * recupera dalla sessione l'oggetto 
	 */
	public static GestioneBuste getFromSession() {
		return GestioneBuste.getFromSession(null);
	}
	
	/**
	 * salva in sessione l'oggetto
	 */
	public void putToSession() {
		GestioneBuste.traceLog("GestioneBuste.putToSession");
		HttpSession session = GestioneBuste.getAction().getRequest().getSession();
		session.setAttribute(SESSION_ID_OFFERTA_GARA, this);
		// si salvano i seguenti dati in sessione per retrocompatibilita' con le JSP!!!
		session.setAttribute(SESSION_ID_DETT_ANAGRAFICA_IMPRESA, this.getImpresa());
	}	
	
	/**
	 * reset di tutti i dati in sessione 
	 */
	public static void resetSession() {
		GestioneBuste.traceLog("GestioneBuste.resetSession");
		HttpSession session = GestioneBuste.getAction().getRequest().getSession();
		session.removeAttribute(SESSION_ID_OFFERTA_GARA);
		// dati in sessione per retrocompatibilita' con le JSP!!!
		session.removeAttribute(SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
	}
	
	/**
	 * deserializzazione dell'oggetto  
	 * In ambiente cluster (Redis) le istanze dei manager non possono essere serializzate/deserializzate 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
		GestioneBuste.traceLog("GestioneBuste.readObject -> deserialization");
		// recupera i bean dei manager e poi deserializza le buste...
		initManagerBeans();
		// deserializza l'oggetto... 
	    is.defaultReadObject();	    
	}
	
	/**
	 * restituisce la busta in sessione in base al tipo busta 
	 */
	public static BustaDocumenti getBustaFromSession(int tipoBusta) throws Exception {
		GestioneBuste.traceLog("GestioneBuste.getFromSession");
		BustaDocumenti busta = null;
		GestioneBuste buste = GestioneBuste.getFromSession();
		if(buste != null) {
			busta = buste.getBusta(tipoBusta);
		}
		return busta;
	}

	/**
	 * restituisce l'helper dei documenti in sessione in base al tipo busta 
	 */
	public static WizardDocumentiBustaHelper getDocumentiBustaHelperFromSession(int tipoBusta) throws Exception {
		WizardDocumentiBustaHelper documenti = null;
		BustaDocumenti busta = GestioneBuste.getBustaFromSession(tipoBusta);
		if(busta != null) {
			documenti = busta.getHelperDocumenti();
		}
		return documenti;
	}

	/**
	 * restituisce la busta di partecipazione in sessione 
	 */
	public static BustaPartecipazione getPartecipazioneFromSession() {
		BustaPartecipazione busta = null;
		GestioneBuste buste = GestioneBuste.getFromSession();
		if(buste != null) {
			busta = buste.getBustaPartecipazione();
		}
		return busta;
	}

	/**
	 * restituisce la busta tecnica in sessione 
	 */
	public static BustaTecnica getBustaTecnicaFromSession() {
		BustaTecnica busta = null;
		GestioneBuste buste = GestioneBuste.getFromSession();
		if(buste != null) {
			busta = buste.getBustaTecnica();
		}
		return busta;
	}

	/**
	 * restituisce la busta economica in sessione 
	 */
	public static BustaEconomica getBustaEconomicaFromSession() {
		BustaEconomica busta = null;
		GestioneBuste buste = GestioneBuste.getFromSession();
		if(buste != null) {
			busta = buste.getBustaEconomica();
		}
		return busta;
	}

	/**
	 * restituisce la busta di riepilogo in sessione 
	 */
	public static BustaRiepilogo getBustaRiepilogoFromSession() {
		BustaRiepilogo busta = null;
		GestioneBuste buste = GestioneBuste.getFromSession();
		if(buste != null) {
			busta = buste.getBustaRiepilogo();
		}
		return busta;
	}

	/**
	 * recupera il numero dei decimali per il ribasso
	 * prima verifica se per la gara esiste TORN.PRERIB 
	 * e se non esiste un valore utilizza quello che 
	 * si trova nel tabellato "A1028" 
	 * @throws ApsException 
	 */
	public Long getNumeroDecimaliRibasso() throws ApsException {
		GestioneBuste.traceLog("GestioneBuste.getNumeroDecimaliRibasso");
		Long numDecimaliRibasso = null;
		if(dettaglioGara.getDatiGeneraliGara() != null && 
		   dettaglioGara.getDatiGeneraliGara().getNumDecimaliRibasso() != null) 
		{
			numDecimaliRibasso = dettaglioGara.getDatiGeneraliGara().getNumDecimaliRibasso();
		}
		if(numDecimaliRibasso == null) {
			numDecimaliRibasso = bandiManager.getNumeroDecimaliRibasso();
		}
		return numDecimaliRibasso;
	}

	/**
	 * calcola la data NTP
	 */
	public Date getNTPDate() { 
		Date dta = null;
		EncodedDataAction action = (EncodedDataAction)GestioneBuste.getAction();
		try {
			INtpManager ntpManager = (INtpManager) ApsWebApplicationUtils
				.getBean(CommonSystemConstants.NTP_MANAGER,
						 ServletActionContext.getRequest());
			dta = ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			action.getText("Errors.ntpTimeout", new String[] { getNomeOperazione(this.operazione) });
			action.setTarget(BaseAction.INPUT);
		} catch (UnknownHostException e) {
			action.getText("Errors.ntpUnknownHost", new String[] { getNomeOperazione(this.operazione) });
			action.setTarget(BaseAction.INPUT);
		} catch (ApsSystemException e) {
			action.getText("Errors.ntpUnexpectedError", new String[] { getNomeOperazione(this.operazione) });
			action.setTarget(BaseAction.INPUT);
		}
		return dta;
	}
	
	/**
	 * restituisce la descrizione dell'operazione/tipo evento di gara 
	 */
	public static String getNomeOperazione(int tipoEvento) {
		String nomeOperazione = null;
		switch (tipoEvento) {
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
				nomeOperazione = GestioneBuste.getAction().getI18nLabel("LABEL_GARETEL_PARTECIPAZIONE_GARA");
				break;
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
				nomeOperazione = GestioneBuste.getAction().getI18nLabel("LABEL_GARETEL_INVIO_OFFERTA");
				break;
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
				nomeOperazione = GestioneBuste.getAction().getI18nLabel("LABEL_GARETEL_COMPROVA_REQUISITI");
				break;
			default:
				//throw new ApsException("Tipologia di operazione non valida");
				ApsSystemUtils.getLogger().error("GestioneBuste.getNomeOperazione()", "Tipologia di operazione non valida");
				break;
		}
		return nomeOperazione;
	}
	
	/**
	 * restituisce l'elenco dei DettagliComuncazione delle buste/lotti di gara 
	 * NB: partecipazione (FS10/FS11) e riepilogo (FS10R/FS11R) non vengono inserite nella lista delle buste di gara
	 *  
	 * @throws Throwable 
	 */
	public List<DettaglioComunicazioneType> getListaComunicazioni(List<String> stati) throws Throwable {
		List<DettaglioComunicazioneType> lista = new ArrayList<DettaglioComunicazioneType>();
		
		bustaAmministrativa.get(stati);	// <= forse questa non serve
		if(bustaAmministrativa != null && bustaAmministrativa.getComunicazioneFlusso().getId() > 0) {			
			lista.add(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione());
		}
		
		List<String> lotti = bustaRiepilogo.getHelper().getListaCompletaLotti();
		if(lotti != null && lotti.size() > 0) {
			// GARA A LOTTI
			for(String lotto : lotti) {
				if(bustaTecnica != null) {
					bustaTecnica.get(stati, lotto);
					if(bustaTecnica.getComunicazioneFlusso().getId() > 0) {
						lista.add(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
					}
				}	
				if(bustaEconomica != null) {
					bustaEconomica.get(stati, lotto);
					if(bustaEconomica.getComunicazioneFlusso().getId() > 0) {
						lista.add(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
					}
				}
			}
		} else {
			// GARA SENZA LOTTI
			if(bustaTecnica != null) {
				bustaTecnica.get(stati);	// <= forse questa non serve
				if(bustaTecnica.getComunicazioneFlusso().getId() > 0) {
					lista.add(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
				}
			}
			if(bustaEconomica != null) {
				bustaEconomica.get(stati);	// <= forse questa non serve
				if(bustaEconomica.getComunicazioneFlusso().getId() > 0) {
					lista.add(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
				}
			}
		}
		
		return lista;
	}

	/**
	 * verifica la correttezza della modalita' di aggiudicazione di una gara
	 */
	public boolean isModalitaAggiudicazioneValida() {
		return (dettaglioGara.getDatiGeneraliGara().getModalitaAggiudicazione() != null);
	}

	/**
	 * verifica se esiste un documento "dgue request" tra i documenti della gara (idstampa='dgue')  
	 */
	public long getIdDgueRequestDocument() {
		long id = -1; 
		
		// cerca un documento marcato idstampa="dgue" tra i documenti della gara...
		if (dettaglioGara.getDocumento() != null) {
			for (DocumentoAllegatoType doc : dettaglioGara.getDocumento()) {
				ApsSystemUtils.getLogger().debug("id: {}, idfacsimile: {}, idstampa(): {}", doc.getId(), doc.getId(), doc.getIdStampa());
				if (DGUEREQUEST.equalsIgnoreCase(doc.getIdStampa())) {
					id = doc.getId();        // "iddoc" equivale al facsimile
					break;
				}
			}
		}
		
		// se non si trova nella documentazione di gara, cerca nel lotto fittizio o nei lotti...
		if (id <= 0) {
			if (dettaglioGara.getLotto() != null) {
				for (LottoDettaglioGaraType lotto : dettaglioGara.getLotto()) {
					if (lotto.getDocumento() != null) {
						for (DocumentoAllegatoType doc : lotto.getDocumento()) {
							ApsSystemUtils.getLogger().debug("id: {}, idfacsimile: {}, idstampa(): {}", doc.getId(), doc.getId(), doc.getIdStampa());
							if (DGUEREQUEST.equalsIgnoreCase(doc.getIdStampa())) {
								id = doc.getId();        // "iddoc" equivale al facsimile
								break;
							}
						}
						if (id >= 0) {
							break;
						}
					}
				}
			}
		}
		
		// se non si trova nel lotto fittizio/lotti, cerca nella documentazione di invito (per le negoziate)...
		if (id <= 0) {
			if (dettaglioGara.getInvito() != null) {
				for (DocumentoAllegatoType doc : dettaglioGara.getInvito()) {
					ApsSystemUtils.getLogger().debug("id: {}, idfacsimile: {}, idstampa(): {}", doc.getId(), doc.getId(), doc.getIdStampa());
					if (DGUEREQUEST.equalsIgnoreCase(doc.getIdStampa())) {
						id = doc.getId();        // "iddoc" equivale al facsimile
						break;
					}
				}
			}
		}
		
		return id;
	}

	/**
	 * indica se la busta di prequalifica e' prevista per la gara 
	 */
	public boolean isBustaPrequalificaPrevista() {
		return (bustaPrequalifica != null && domandaPartecipazione);
	}
	
	/**
	 * indica se la busta amministrativa e' prevista per la gara 
	 */
	public boolean isBustaAmministrativaPrevista() {
		return (bustaAmministrativa != null && isBustaAmministrativaPresente());
	}
	
	/**
	 * indica se la busta(lotto) tecnica e' prevista per la gara 
	 */
	public boolean isBustaTecnicaPrevista() {
		boolean prevista = true;
		try {
			prevista = isGaraConOffertaTecnica();
		} catch (ApsException e) {
			//...
		}
		return (prevista || bustaRiepilogo.isAlmenoUnaBustaTecnica());
	}
	
	/**
	 * indica se la busta(lotto) economica e' prevista per la gara 
	 */
	public boolean isBustaEconomicaPrevista() {
		boolean costoFisso = (dettaglioGara.getDatiGeneraliGara().getCostoFisso() == 1);
		return (!costoFisso || bustaRiepilogo.isAlmenoUnaBustaEconomica());
	}
	
	/**
	 * restituisce True se e' una gara a lotti distinti e buste distinte 
	 */
	public boolean isGaraLottiDistinti() {
		boolean garaLottiDistinti = false;
		
		// ATTENZIONE 
		// datiGeneraliGara.Tipologia e' sempre 3 sia per 
		// sia per le gare a lotti, plico unico, offerte distinte  
		// sia per le gare a lotti, plico unico, offerta unica
		// per sapere se e' una gara a offerte distine o offerta unica va usato datiGeneraliGara.BusteDistinte !!!
		boolean busteDistinte = (dettaglioGara.getDatiGeneraliGara().getBusteDistinte() != null
								 && dettaglioGara.getDatiGeneraliGara().getBusteDistinte().booleanValue());		
		if (dettaglioGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE) {
			garaLottiDistinti = true;
		} else if(dettaglioGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO && busteDistinte) {
			garaLottiDistinti = true;
		} else {
			// SERVE ??? 
		}
		
		return garaLottiDistinti;
	}
	
}
