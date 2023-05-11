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

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
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
public class GestioneBuste implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1109032304924164653L;

	
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
	private boolean ristretta;
	private boolean negoziata; 
	private boolean indagineMercato;
	private boolean domandaPartecipazione;
	private boolean invioOfferta;
	private boolean garaLotti;
	
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

	public boolean isRistretta() {
		return ristretta;
	}

	public boolean isNegoziata() {
		return negoziata;
	}
		
	public boolean isIndagineMercato() {
		return indagineMercato;
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
		return (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
	}
	
	public static Map<String, Object> getSession() {
		return ActionContext.getContext().getSession();
	}
	
	/**
	 * tracciature di un messaggio di debug
	 */
	public static void traceLog(String msg) {
		if(ApsSystemUtils.getLogger().isDebugEnabled()) {
			ApsSystemUtils.getLogger().debug(msg);
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
		this.domandaPartecipazione = (operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA); 
		this.invioOfferta = !this.domandaPartecipazione; //(operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
		this.garaLotti = false; 
		this.impresa = null;
		this.dettaglioGara = null;
		this.negoziata = false;
		this.ristretta = false;
		this.indagineMercato = false;
		this.bustaPartecipazione = null;
		this.bustaPrequalifica = null;
		this.bustaAmministrativa = null;
		this.bustaTecnica = null;
		this.bustaEconomica = null;
		this.bustaRiepilogo = null;

		// recupera action ed i manager necessari
		this.tempDir = (File)GestioneBuste.getAction().getRequest().getSession().getServletContext().getAttribute("javax.servlet.context.tempdir");
		this.initManagerBeans();
		
		// inizializza le buste
		try {
			// recupera i dati dell'impresa e della gara
			this.impresa = this.getLastestDatiImpresa();
			this.refreshDettaglioGara();
			
			this.garaLotti = false;
			if(this.dettaglioGara.getLotto() != null && this.dettaglioGara.getLotto().length > 0)	{
				this.garaLotti = true;
				if(this.dettaglioGara.getLotto().length == 1 && 
				   this.codiceGara.equals(this.dettaglioGara.getLotto()[0].getCodiceLotto())) 
				{
					this.garaLotti = false;
				}
			} 
			
			// inizializza le buste 
			if(this.domandaPartecipazione) {
				// inizializza riepilogo e buste per la domanda di partecipazione (prequalifica)
				this.bustaRiepilogo = new BustaRiepilogo(this, BustaGara.BUSTA_RIEPILOGO_PRE);
				this.bustaPartecipazione = new BustaPartecipazione(this, BustaGara.BUSTA_PARTECIPAZIONE);				
				this.bustaPrequalifica = new BustaPrequalifica(this);
			} else {
				// inizializza riepilogo e buste per l'invio offerta  
				this.bustaRiepilogo = new BustaRiepilogo(this, BustaGara.BUSTA_RIEPILOGO);
				this.bustaPartecipazione = new BustaPartecipazione(this, BustaGara.BUSTA_INVIO_OFFERTA);
				this.bustaAmministrativa = new BustaAmministrativa(this);
				this.bustaTecnica = new BustaTecnica(this);
				this.bustaEconomica = new BustaEconomica(this);
			}
			
			// salva in sessione la gestione buste
			this.putToSession();
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("GestioneBuste", "GestioneBuste", t);
		}
	}
	
	/**
	 * inizializza i bean dei manager
	 */
	private void initManagerBeans() {
		this.bandiManager = null;
		this.comunicazioniManager = null;
		this.appParamManager = null;
		this.eventManager = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			this.bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
			this.comunicazioniManager = (IComunicazioniManager) ctx.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER);
			this.appParamManager = (IAppParamManager) ctx.getBean(PortGareSystemConstants.APPPARAM_MANAGER);
			this.eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().equals(t);
		}
		
		if(this.bustaPartecipazione != null) this.bustaPartecipazione.gestioneBuste = this;
		if(this.bustaPrequalifica != null) this.bustaPrequalifica.gestioneBuste = this;
		if(this.bustaAmministrativa != null) this.bustaAmministrativa.gestioneBuste = this;
		if(this.bustaTecnica != null) this.bustaTecnica.gestioneBuste = this;
		if(this.bustaEconomica != null) this.bustaEconomica.gestioneBuste = this;
		if(this.bustaRiepilogo != null) this.bustaRiepilogo.gestioneBuste = this;
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
			this.resetSession();

//			// se la lista degli stati e' vuota, usa gli stati standard...
//			if(stati == null) {
//				//stati = new ArrayList<String>();
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
//				//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
//			}
			
//			// recupera i dati dell'impresa e della gara...
//			this.impresa = this.getLastestDatiImpresa();
			this.refreshDettaglioGara();
		 
			if(this.domandaPartecipazione) {
				// inizializza riepilogo e buste per la domanda di partecipazione (prequalifica)...
				continua = continua && this.bustaPartecipazione.get(stati);
				continua = continua && this.bustaPrequalifica.get(stati);
			} else {
				// inizializza riepilogo e buste per l'invio offerta...
				continua = continua && this.bustaPartecipazione.get(stati);
				continua = continua && this.bustaAmministrativa.get(stati);
				continua = continua && this.bustaTecnica.get(stati);
				continua = continua && this.bustaEconomica.get(stati);
			}
			continua = continua && this.bustaRiepilogo.get(stati);
	
		} catch (Throwable t) {
			continua = false;
			ApsSystemUtils.getLogger().error("GestioneBuste", "get", t);
		}

		// riallinea l'helper del riepilogo alle buste...
		if(continua) {
			if(this.bustaPartecipazione.getComunicazioneFlusso().getId() > 0 &&
			   this.bustaRiepilogo.getComunicazioneFlusso().getId() <= 0) 
			{
				// la comunicazione del riepilogo NON esiste
				// aggiorna il riepilogo ed invia la comunicazione FS10R/FS11R
				continua = continua && this.bustaRiepilogo.riallineaDocumenti(this.bustaPrequalifica);
				continua = continua && this.bustaRiepilogo.riallineaDocumenti(this.bustaAmministrativa);
				continua = continua && this.bustaRiepilogo.riallineaDocumenti(this.bustaTecnica);
				continua = continua && this.bustaRiepilogo.riallineaDocumenti(this.bustaEconomica);
				continua = continua && this.bustaRiepilogo.send(null);
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
		return this.get(null); 
	}
	
	/**
	 * invia la busta al servizio con la descrizione dell'operazione
	 * 
	 * @throws ApsException 
	 */
	protected boolean send() throws Exception {
		GestioneBuste.traceLog("GestioneBuste.send not implemented");
		return false;
	}

	/**
	 * recupera i dati impresa piu' recenti
	 */
	public WizardDatiImpresaHelper getLastestDatiImpresa() throws ApsException, XmlException {
		GestioneBuste.traceLog("GestioneBuste.getLastestDatiImpresa");
		return ImpresaAction.getLatestDatiImpresa(
				this.username, 
				(EncodedDataAction)GestioneBuste.getAction());
	}

	/**
	 * rilegge il dettaglio dei dati della gara 
	 * @throws ApsException 
	 */
	private boolean refreshDettaglioGara() {
		try {
			this.dettaglioGara = this.bandiManager.getDettaglioGara(this.codiceGara);
			this.ristretta = WizardPartecipazioneHelper.isGaraRistretta(this.dettaglioGara.getDatiGeneraliGara().getIterGara());
			this.negoziata = WizardPartecipazioneHelper.isGaraNegoziata(this.dettaglioGara.getDatiGeneraliGara().getIterGara());
			this.indagineMercato = "7".equals(this.dettaglioGara.getDatiGeneraliGara().getIterGara());
		} catch(Throwable t) {
			// NON DOVREBBE MAI ACCADERE!!!
			this.dettaglioGara = null;
			ApsSystemUtils.getLogger().error("GestioneBuste", "refreshDettaglioGara", t);
		}
		return (this.dettaglioGara != null);
	}

	/**
	 * ricava la comunicazione della busta  
	 * @throws ApsException 
	 */
	protected DettaglioComunicazioneType retriveComunicazioneBusta(BustaGara busta) throws ApsException {
		DettaglioComunicazioneType dettCom = busta.getComunicazioneFlusso().find(
				this.username, 
				busta.getCodiceGara(), 
				busta.getProgressivoOfferta(), 
				busta.getComunicazioneFlusso().getTipoComunicazione(), 
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA); 
		if (dettCom == null) {
			dettCom  = busta.getComunicazioneFlusso().find(
					this.username, 
					busta.getCodiceGara(), 
					busta.getProgressivoOfferta(), 
					busta.getComunicazioneFlusso().getTipoComunicazione(), 
					CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		}
		return dettCom;
	}

	/**
	 * restituisce se e' prevista la gestione della busta amministrativa
	 */
	public boolean isBustaAmministrativaPresente() {
		boolean noBustaAmministrativa = false;
		if(this.dettaglioGara != null &&
		   this.dettaglioGara.getDatiGeneraliGara() != null)  
		{
			noBustaAmministrativa = this.dettaglioGara.getDatiGeneraliGara().isNoBustaAmministrativa();
		}
		return noBustaAmministrativa;
	}
	
	/**
	 * restituisce se e' prevista la gestione della busta amministrativa
	 * @throws ApsException 
	 */
	public boolean isGaraConOffertaTecnica() throws ApsException {
		return (boolean) this.bandiManager.isGaraConOffertaTecnica(this.codiceGara);
	}
	
	/**
	 * restituisce la busta in base al tipo busta 
	 */
	public BustaDocumenti getBusta(int tipoBusta) throws Exception {
		BustaDocumenti busta = null;
		if( tipoBusta == BustaGara.BUSTA_PRE_QUALIFICA) {
			busta = this.getBustaPrequalifica();
		} else if(tipoBusta == BustaGara.BUSTA_AMMINISTRATIVA) {
			busta = this.getBustaAmministrativa();
		} else if( tipoBusta == BustaGara.BUSTA_TECNICA) {
			busta = this.getBustaTecnica();
		} else if( tipoBusta == BustaGara.BUSTA_ECONOMICA) {
			busta = this.getBustaEconomica();
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
		BustaDocumenti busta = this.getBusta(tipoBusta);
		if(busta != null) {
			helper = busta.getHelperDocumenti();
		} else {
			throw new Exception("getDocumentiBustaHelper() Tipologia di busta non valida");
		}
		return helper;
	}

	/**
	 * reset di tutti i dati in sessione 
	 */
	public void resetSession() {
		GestioneBuste.traceLog("GestioneBuste.resetSession");
		HttpSession session = GestioneBuste.getAction().getRequest().getSession();
		session.removeAttribute(SESSION_ID_OFFERTA_GARA);
		
		// dati in sessione per retrocompatibilita' con le JSP!!!
		session.removeAttribute(SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
//		session.removeAttribute(SESSION_ID_DETT_PART_GARA);				// helper partecipazione
//		session.removeAttribute(SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);	// helper busta preq
//		session.removeAttribute(SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);	// helper busta amm
//		session.removeAttribute(SESSION_ID_DETT_BUSTA_TECNICA);			// helper busta tec
//		session.removeAttribute(SESSION_ID_DETT_BUSTA_ECONOMICA);		// helper busta eco
//		session.removeAttribute(SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);	// helper busta di riepilogo
	}
	
	/**
	 * recupera dalla sessione l'oggetto per codice lotto 
	 */
	public static GestioneBuste getFromSession(String codiceLotto) {
		GestioneBuste.traceLog("GestioneBuste.getFromSession");
		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		GestioneBuste buste = (GestioneBuste)action.getRequest().getSession().getAttribute(SESSION_ID_OFFERTA_GARA);
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
		
//		// forza il garbage collector esplicitamente
//		System.gc();
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
		this.initManagerBeans();
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
		if(this.dettaglioGara.getDatiGeneraliGara() != null && 
		   this.dettaglioGara.getDatiGeneraliGara().getNumDecimaliRibasso() != null) 
		{
			numDecimaliRibasso = this.dettaglioGara.getDatiGeneraliGara().getNumDecimaliRibasso();
		}
		if(numDecimaliRibasso == null) {
			numDecimaliRibasso = this.bandiManager.getNumeroDecimaliRibasso();
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
				.getBean(PortGareSystemConstants.NTP_MANAGER,
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
		
		this.bustaAmministrativa.get(stati);	// <= forse questa non serve
		if(this.bustaAmministrativa != null && this.bustaAmministrativa.getComunicazioneFlusso().getId() > 0) {			
			lista.add(this.bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione());
		}
		
		List<String> lotti = this.bustaRiepilogo.getHelper().getListaCompletaLotti();
		if(lotti != null && lotti.size() > 0) {
			// GARA A LOTTI
			for(String lotto : lotti) {
				if(this.bustaTecnica != null) {
					this.bustaTecnica.get(stati, lotto);
					if(this.bustaTecnica.getComunicazioneFlusso().getId() > 0) {
						lista.add(this.bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
					}
				}	
				if(this.bustaEconomica != null) {
					this.bustaEconomica.get(stati, lotto);
					if(this.bustaEconomica.getComunicazioneFlusso().getId() > 0) {
						lista.add(this.bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
					}
				}
			}
		} else {
			// GARA SENZA LOTTI
			if(this.bustaTecnica != null) {
				this.bustaTecnica.get(stati);	// <= forse questa non serve
				if(this.bustaTecnica.getComunicazioneFlusso().getId() > 0) {
					lista.add(this.bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
				}
			}
			if(this.bustaEconomica != null) {
				this.bustaEconomica.get(stati);	// <= forse questa non serve
				if(this.bustaEconomica.getComunicazioneFlusso().getId() > 0) {
					lista.add(this.bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
				}
			}
		}
		
		return lista;
	}

	/**
	 * verifica la correttezza della modalita' di aggiudicazione di una gara
	 */
	public boolean isModalitaAggiudicazioneValida() {
		return (this.dettaglioGara.getDatiGeneraliGara().getModalitaAggiudicazione() != null);
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
		// eventualmente cerca nel lotto fittizzio o nei lotti...
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
		return id;
	}

}
