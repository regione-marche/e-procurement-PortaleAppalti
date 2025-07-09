package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSAllegatoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageProtocollazioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardDocumentiHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.utils.ProtocolsUtils;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;
import it.maggioli.eldasoft.ws.dm.WSDMTipoVoceRubricaType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Classe di utilit&agrave; per la creazione di comunicazioni da inviare al
 * backoffice
 *
 * @version 1.1
 * @author Stefano.Sabbadin
 */
public class ComunicazioniUtilities {

	/**
	 * Crea una comunicazione inizializzandone la testata
	 *
	 * @param idComunicazione id comunicazione, diversa da null se si intende
	 * procedere con un aggiornamento
	 * @param chiave1 chiave 1
	 * @param chiave2 chiave 2
	 * @param chiave3 chiave 3
	 * @param mittente mittente
	 * @param stato stato della comunicazione
	 * @param oggetto oggetto della comunicazione
	 * @param testo testo
	 * @param tipoComunicazione codice della comunicazione
	 * @param dataPubblicazione la data di creazione della comunicazione
	 * @return comunicazione con dati di testata inizializzati
	 */
	public static DettaglioComunicazioneType createDettaglioComunicazione(
			Long idComunicazione, 
			String chiave1, 
			String chiave2,
			String chiave3,
			String mittente, 
			String stato, 
			String oggetto, 
			String testo,
			String tipoComunicazione, 
			Date dataPubblicazione) 
	{
		DettaglioComunicazioneType dettaglioComunicazione = new DettaglioComunicazioneType();
		dettaglioComunicazione.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		if (idComunicazione != null) {
			dettaglioComunicazione.setId(idComunicazione);
		}
		dettaglioComunicazione.setChiave1(chiave1);
		dettaglioComunicazione.setChiave2(chiave2);
		dettaglioComunicazione.setChiave3(chiave3);
		if (mittente != null && mittente.length() > 60) {
			mittente = mittente.substring(0, 60);
		}
		dettaglioComunicazione.setMittente(mittente);
		dettaglioComunicazione.setStato(stato);
		dettaglioComunicazione.setOggetto(oggetto);
		dettaglioComunicazione.setTesto(testo);
		dettaglioComunicazione.setTipoComunicazione(tipoComunicazione);
		dettaglioComunicazione.setComunicazionePubblica(false);
		if (dataPubblicazione != null) {
			dettaglioComunicazione.setDataPubblicazione(dataPubblicazione);
		}
		return dettaglioComunicazione;
	}

	/**
	 * Crea un allegato per la comunicazione
	 *
	 * @param nomeFile nome del file
	 * @param descrizione descrizione del file allegato
	 * @param file contenuto del file
	 * @return allegato
	 */
	public static AllegatoComunicazioneType createAllegatoComunicazione(
					String nomeFile, String descrizione, byte[] file) {
		AllegatoComunicazioneType allegato = new AllegatoComunicazioneType();
		allegato.setTipo(CommonSystemConstants.TIPO_ALLEGATO_XML);
		allegato.setNomeFile(nomeFile);
		allegato.setDescrizione(descrizione);
		allegato.setFile(file);
		return allegato;
	}

	/**
	 * ...	 
	 */
	public static DettaglioComunicazioneType retrieveComunicazione(
			IComunicazioniManager comunicazioniManager,
			String username, 
			String codice,
			String progOfferta,
			String statoComunicazione, 
			String tipoComunicazione) throws ApsException 
	{
		DettaglioComunicazioneType dettaglio = null;
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setChiave3(progOfferta);
		criteriRicerca.setStato(statoComunicazione);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		if (!comunicazioni.isEmpty()) {
			if (comunicazioni.size() > 1) {
				throw new ApsException("retrieveComunicazione.notUnique");
			}
			dettaglio = comunicazioni.get(0);
		}
		return dettaglio;
	}

	/**
	 * recupera una comunicazione in base utente, codice gara, tipo comunicazione 
	 * ed un elenco di stati 
	 */
	public static DettaglioComunicazioneType retrieveComunicazioneConStati(
			IComunicazioniManager comunicazioniManager,
			String username, 
			String codice, 
			String progOfferta,
			String tipoComunicazione, 
			List<String> stati) throws ApsException 
	{
		DettaglioComunicazioneType dettaglio = null;
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setChiave3(progOfferta);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		if (!comunicazioni.isEmpty()) {
			Long maxId = (long) -1;
			for (DettaglioComunicazioneType comunicazione : comunicazioni) {
				if (stati.contains(comunicazione.getStato()) && comunicazione.getId() > maxId) {
					dettaglio = comunicazione;
				}
			}
		}
		return dettaglio;
	}
	
	/**
	 * recupera l'elenco delle comunicazioni in base utente, codice gara, tipo comunicazione 
	 * ed un elenco di stati 
	 */
	public static List<DettaglioComunicazioneType> retrieveComunicazioniConStati(
			IComunicazioniManager comunicazioniManager,
			String username, 
			String codice, 
			String tipoComunicazione, 
			List<String> stati) throws ApsException 
	{
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		List<DettaglioComunicazioneType> lista = new ArrayList<DettaglioComunicazioneType>();
		if (!comunicazioni.isEmpty()) {
			for (DettaglioComunicazioneType comunicazione : comunicazioni) {
				if (stati.contains(comunicazione.getStato())) {
					lista.add(comunicazione);
				}
			}
		} 
		return (lista.size() > 0 ? lista : null);
	}

	/**
	 * Genera l'evento per l'invio di una comunicazione.
	 * 
	 * @param username
	 *            username impresa richiedente
	 * @param destination
	 *            codice della gara/procedura/elenco interessata
	 * @param comunicazione
	 *            comunicazione inviata
	 * @param dataInvio
	 *            data di invio
	 * @return evento
	 */
	public static Event createEventSendComunicazione(String username, String destination, ComunicazioneType comunicazione, Date dataInvio, String ipAddress, String sessionId) {
		Event evento = new Event();
		evento.setUsername(username);
		evento.setDestination(destination);
		evento.setMessage(
				"Invio comunicazione " + comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				+ " con id " + comunicazione.getDettaglioComunicazione().getId()
				+ " in stato " + comunicazione.getDettaglioComunicazione().getStato()
				+ " e timestamp ntp " + UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
		evento.setLevel(Event.Level.INFO);
		evento.setIpAddress(ipAddress);
		evento.setSessionId(sessionId);
		if (CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(comunicazione.getDettaglioComunicazione().getStato())) {
			// evento INVCOM
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		} else if (CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE.equals(comunicazione.getDettaglioComunicazione().getStato())) {
			// evento INVCOMDAPROT
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROTOCOLLARE);			
		} else if (CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazione.getDettaglioComunicazione().getStato())) {
			// evento SAVECOM
			evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);			
		} else {
			// evento INVCOM
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		}
		return evento;
	}
	
	/**
	 * ...	 
	 */
	public static Event createEventAggiornaStatoComunicazione(String username, String destination, ComunicazioneType comunicazione, String ipAddress, String sessionId) {
		Event evento = new Event();
		evento.setUsername(username);
		evento.setDestination(destination);
		evento.setMessage("Aggiornamento comunicazione con id "
			+ comunicazione.getDettaglioComunicazione().getId()
			+ " allo stato "
			+ CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setLevel(Event.Level.INFO);
		evento.setIpAddress(ipAddress);
		evento.setSessionId(sessionId);
		return evento;
	}
	
	//**************************************************************************
	//**************************************************************************
	//**************************************************************************
	
	// proprieta' passate dal costruttore
	private String target;
	private Object action;                     //EncodedDataAction | BaseAction
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private INtpManager ntpManager;
	private IMailManager mailManager;
	private IAppParamManager appParamManager;
	private IWSDMManager wsdmManager;
	private IEventManager eventManager;	
	ICustomConfigManager customConfigManager;
	
	// proprieta' interne
	private String codice;								// codice di gara
	private Long genere;
	private UserDetails currentUser;
	private String sessionId;
	private Integer tipoProtocollazione;
	private String mailUfficioProtocollo;				// destinatario ente per notifiche di invio delle comunicazioni da parte delle ditte 
	private Boolean allegaDocMailUfficioProtocollo;
	private String stazioneAppaltanteProtocollante;
	private String stazioneAppaltanteProcedura;

	// info sulla protocollazione restituite al chiamante
	private String msgErrore;
	private Long annoProtocollo;
	private String numeroProtocollo;
	private String dataProtocollo;
	private String dataInvio;
	private Date dataPresentazione;
	private String codiceSistema;
	
	// proprieta' per "oggetto" e "testo" per le comunicazioni inviate e le mail
	private String dettaglioComunicazioneSubject;		// oggetto della comunicazione 
	private String dettaglioComunicazioneText;			// testo della comunicazione 
	private String mailRicevutaImpresaSubject;
	private String mailRicevutaImpresaText;
	private String mailUffProtocolloSubject;			// oggetto mail notifica ufficio protocollo
	private String mailUffProtocolloText;				// testo mail notifica ufficio protocollo
	private String WsdmProtocolloSubject;				// oggetto inviato a WSDM
	private String WsdmProtocolloText;					// testo inviato a WSDM

	/**
	 * @return the msgErrore
	 */
	public String getMsgErrore() {
		return msgErrore;
	}

	public void setMsgErrore(String msgErrore) {
		this.msgErrore = msgErrore;
	}
	
	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}
	
	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public String getDataInvio() {
		return dataInvio;
	}
	
	public String getCodiceSistema() {
		return codiceSistema;
	}

	/**
	 * costruttore/inizializzazione 
	 * @throws Exception 
	 */
	public ComunicazioniUtilities(
			Object action,
			String sessionId) throws Exception 
	{
		try {
			this.action = action;
			
		    this.currentUser = (UserDetails) ServletActionContext
	    		.getContext().getSession().get(SystemConstants.SESSIONPARAM_CURRENT_USER);
		    
		    this.sessionId = sessionId;
			
	    	this.appParamManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER, ServletActionContext.getRequest());
			this.bandiManager = (IBandiManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());
			this.comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER, ServletActionContext.getRequest());
			this.eventManager = (IEventManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.EVENTI_MANAGER, ServletActionContext.getRequest());
			this.ntpManager = (INtpManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.NTP_MANAGER, ServletActionContext.getRequest());
			this.mailManager = (IMailManager) ApsWebApplicationUtils
					.getBean("jpmailMailManager", ServletActionContext.getRequest());
			this.wsdmManager = (IWSDMManager) ApsWebApplicationUtils
					.getBean("WSDMManager", ServletActionContext.getRequest());
			this.customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER, ServletActionContext.getRequest());
			
			//this.session = ServletActionContext.getContext().getSession();
			
			this.tipoProtocollazione = (Integer) this.appParamManager.getConfigurationValueIntDef(
					AppParamManager.PROTOCOLLAZIONE_TIPO,
					PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		} catch(Exception e) {
			throw new Exception("ComunicazioniUtilities " + e.getMessage());
		} 
	}


	//*************************************************************************
	// INVIO DI COMUNICAZIONE CON FASE DI PROTOCOLLAZIONE  
	//*************************************************************************	

	/**
	 * Invio di una comunicazione con gestione della fase di protocollazione
	 * 
	 * @param idCom 
	 * 			NULL se si inserisce una nuova comunicazione, >0 se si aggiorna una comunicazione esistente 
	 * @param tipoComunicazione 
	 * 			tipo di comunicazione (FS12, ...) 
	 * @param statoComunicazione 
	 * 			stato della comunicazione (BOZZA, INVIATA, PROCESSATA, ...)
	 * @param entitaComunicazione 
	 * 			entita' associata alla comunicazione 
	 * @param codice 
	 * 			codice della gara
	 * @param codiceLotto 
	 * 			codice lotto della gara
	 * @param impresa 
	 * 			dati dell'impresa
	 * @param allegatiComunicazione 
	 * 			documenti da allegare alla comunicazione
	 *          Es: FS12 per un'offerta d'asta corrisponde al pdf generato per l'offerta
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 * 
	 * @return SUCCESS se l'invio e' andato a buon fine
	 * 		   INPUT se sono necessari altri dati per l'invio
	 *         CommonSystemConstants.PORTAL_ERROR in caso di errore 
	 * @throws Exception 
	 */
	private Long sendComunicazione(
			Long idCom,
			String tipoComunicazione,
			String statoComunicazione,
			String entitaComunicazione,
			String codice, 
			String codiceLotto,
			WizardDatiImpresaHelper impresa,
			AllegatoComunicazioneType[] allegatiComunicazione,
			Date dataInvio, 
			boolean isActiveFunctionPdfA, 
			InputStream iccFilePath) throws Exception 
	{	
		Long idComunicazione = null;
				
		this.target = BaseAction.SUCCESS; 

		String codiceGara = (codiceLotto != null && !codiceLotto.isEmpty()
				       		 ? codiceLotto : codice);
		
		if(codiceGara != null && !codiceGara.isEmpty()) {
			Event evento = new Event();
			
			this.dataPresentazione = dataInvio;
			
			if(dataInvio != null) { 
				this.dataInvio = UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
			}
			
			String username = this.currentUser.getUsername();
			this.codice = StringUtils.stripToNull(codiceGara);
			
			// prepara gli allegati della comunicazione...
			//
			// NB: qui allegatiComunicazioni[] deve contere tutti gli allegati
			//     della comunicazione; in caso di comunicazioni che prevedono 
			//     documenti xml, questi devono gia' essere presenti nell'elenco 


			// prepara i documenti allegati per l'invio delle mail...
			// crea l'elenco dei documenti dagli allegatiComunicazione[]...
			// al termine della protocollazione i file temporanei vanno eliminati...
			boolean eliminaTemp = false;
			WizardDocumentiHelper allegatiMail = new WizardDocumentiHelper();
			try {
				if(allegatiComunicazione != null) {
					eliminaTemp = true;
					for (AllegatoComunicazioneType allegato : allegatiComunicazione) {
						allegatiMail.addAdditionalDoc(
								Attachment.AttachmentBuilder.init()
										.withFile(bytesToFile(allegato.getFile(), allegato.getNomeFile()))
										.withFileName(allegato.getNomeFile())
										.withDesc(allegato.getDescrizione())
									.build()
						);
					}
				}
			} catch(Exception e) {
				// NON dovrebbe succedere!!!
				ApsSystemUtils.getLogger().warn("ComunicazioniUtilies.sendComunicazione", e);
			}
			
			
			//******************************************************************
			// FASE 1: 
			// invio della comunicazione
			//******************************************************************
			DettaglioGaraType gara = bandiManager.getDettaglioGara(this.codice);
			
			this.codiceSistema = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
			this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			this.allegaDocMailUfficioProtocollo = (Boolean) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
			this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);			
			this.stazioneAppaltanteProcedura = gara.getStazioneAppaltante().getCodice();
			
			// inizializza la protocollazione impostando subito la SA
			this.appParamManager.setStazioneAppaltanteProtocollazione(this.stazioneAppaltanteProcedura);

			this.tipoProtocollazione = this.appParamManager.getTipoProtocollazione(this.stazioneAppaltanteProcedura);
			
			// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
			if(this.appParamManager.isConfigWSDMNonDisponibile()) {
				this.target = BaseAction.INPUT;
				String msgerr = this.getText("Errors.wsdm.configNotAvailable");
				this.addActionError(msgerr);
				evento = new Event();
				evento.setUsername(username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.ERROR);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(this.currentUser.getIpAddress());
				evento.setSessionId(this.sessionId);
				evento.setMessage("Configurazione non disponibile o vuota");
				evento.setDetailMessage(msgerr);
				this.eventManager.insertEvent(evento);
			}
			
			ComunicazioneType nuovaComunicazione = new ComunicazioneType();
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			
			if(BaseAction.SUCCESS.equals(this.target)) {
				try {
					this.genere = this.bandiManager.getGenere(this.codice);
					
					DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities
						.createDettaglioComunicazione( 
								idCom, 
								username, 
								this.codice,
								null,
								impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
								statoComunicazione,  
								this.dettaglioComunicazioneSubject,
								this.dettaglioComunicazioneText,
								tipoComunicazione,
								dataInvio);
					
					if(statoComunicazione != CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA) {
						if(this.tipoProtocollazione != PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
							dettComunicazione.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
						} else {
							dettComunicazione.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
						}
					}
	
					// recupera l'elenco degli allegati da inviare con la comunicazione...
					nuovaComunicazione.setAllegato(allegatiComunicazione);
					nuovaComunicazione.setDettaglioComunicazione(dettComunicazione);
	
					// invio della comunicazione
					evento = ComunicazioniUtilities.createEventSendComunicazione(
							username, 
							this.codice, 
							nuovaComunicazione,
							dataInvio,
							this.currentUser.getIpAddress(),
							this.sessionId);
							
					idComunicazione = this.comunicazioniManager.sendComunicazione(nuovaComunicazione);
					nuovaComunicazione.getDettaglioComunicazione().setId(idComunicazione);
							
					// si rigenera l'evento perche' nel caso di esito positivo si
					// arricchisce il messaggio con l'id della comunicazione 
					// (prima dell'invio e' null)
					evento = ComunicazioniUtilities.createEventSendComunicazione(
							username, 
							this.codice, 
							nuovaComunicazione,
							dataInvio,
							this.currentUser.getIpAddress(),
							this.sessionId);
					// forzatura perche' lo stato di inserimento e' 3 e non 5 come avviene solitamente
					if (this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
						evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
					}
	
					inviataComunicazione = true;
				} catch (ApsException e) {
					ApsSystemUtils.logThrowable(e, this, "sendComunicazione");
					manageExceptionError(e, this.action);
					this.target = CommonSystemConstants.PORTAL_ERROR;
					// aggiorna l'evento con le info dell'errore
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage("Invio della comunicazione " + tipoComunicazione + " non riuscito");
					evento.setError(e);
				}
			}
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
			
			//******************************************************************
			// FASE 2: 
			// ove previsto, si invia alla protocollazione
			//******************************************************************
//			this.dataInvio = (this.dataInvio != null && !this.dataInvio.isEmpty() 
//					? UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS)
//			 		: null);
			this.numeroProtocollo = null;
			this.annoProtocollo = null;
			this.dataProtocollo = null;
			Exception protocollazioneException = null;
			
			// prepara oggetto e testo della mail di ricevuta da inviare 
			// all'impresa in caso di NESSUNA PROTOCOLLAZIONE... 
			if(tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
				this.mailRicevutaImpresaSubject = MessageFormat.format(this.mailRicevutaImpresaSubject, 
						new Object[] {this.codice});
				this.mailRicevutaImpresaText = MessageFormat.format(this.mailRicevutaImpresaText, new Object[] {
						impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
						UtilityDate.convertiData(this.dataPresentazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS)});				
			} 
			
			if (statoComunicazione != CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA &&
				inviataComunicazione &&
				isProtocollazionePrevista(this.tipoProtocollazione, this.stazioneAppaltanteProtocollante, this.stazioneAppaltanteProcedura)) 
			{
				// recupero l'id della comunicazione
				evento = new Event();
				evento.setUsername(username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(this.currentUser.getIpAddress());
				evento.setSessionId(this.sessionId);

				switch (tipoProtocollazione) {
					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
						evento.setMessage("Protocollazione via mail a " + this.mailUfficioProtocollo);
	
						boolean mailProtocollazioneInviata = false;
						try {
							// si invia la richiesta di protocollazione via mail
							// all'ente...
							this.sendMailRichiestaUfficioProtocollo(allegatiMail, impresa);
							mailProtocollazioneInviata = true;
							this.eventManager.insertEvent(evento);
	
							// si aggiorna lo stato a 5
							evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
							evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() + 
									" allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
							this.comunicazioniManager.updateStatoComunicazioni(
									new DettaglioComunicazioneType[] {nuovaComunicazione.getDettaglioComunicazione()}, 
									CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
							this.eventManager.insertEvent(evento);
							
							this.dataProtocollo = UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);

							// prepara il testo della mail di ricevuta da 
							// inviare all'impresa...
							this.mailRicevutaImpresaSubject = MessageFormat.format(this.mailRicevutaImpresaSubject, 
									new Object[] {this.codice});
							this.mailRicevutaImpresaText = MessageFormat.format(this.mailRicevutaImpresaText, 
									new Object[] {impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
												  this.dataProtocollo});

						} catch (ApsException t) {
							protocollazioneException = t;
							evento.setError(t);
							this.eventManager.insertEvent(evento);
							if (mailProtocollazioneInviata) {
								// segnalo l'errore, comunque considero la
								// protocollazione andata a buon fine e segnalo nel
								// log e a video che va aggiornato a mano lo stato
								// della comunicazione
								this.msgErrore = getText(
										"Errors.updateStatoComunicazioneDaProcessare",
										new String[] {nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
								ApsSystemUtils.logThrowable(t, this.action, "sendComunicazione", this.msgErrore);
								addActionError(this.msgErrore);
	
								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
								this.dataProtocollo = UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
							} else {
								ApsSystemUtils.logThrowable(t, this, "sendComunicazione", "Per errori durante la connessione al server di posta, non e' stato possibile inviare la richiesta all'ufficio protocollo");
								manageExceptionError(t, this.action);
								this.target = BaseAction.INPUT;
								// si deve annullare l'invio che si e' tentato di protocollare
								this.annullaComunicazioneInviata(
										CommonSystemConstants.ID_APPLICATIVO, 
										nuovaComunicazione.getDettaglioComunicazione().getId());
								protocollazioneOk = false;
							}
						}
						break;
					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
						evento.setMessage("Protocollazione via WSDM");
	
						boolean protocollazioneInoltrata = false;
						WSDocumentoType documento = null;
						WSDMProtocolloDocumentoType ris = null;
						idComunicazione = nuovaComunicazione.getDettaglioComunicazione().getId();

						try {
							// in caso di protocollazione tramite WSDM si invia 
							// lo stesso testo della mail che si invierebbe 
							// all'ufficio di protocollazione...
							this.WsdmProtocolloSubject = this.mailUffProtocolloSubject;
							this.WsdmProtocolloText = this.mailUffProtocolloText;
							
							FascicoloProtocolloType fascicoloBackOffice = this.bandiManager.getFascicoloProtocollo(this.codice);
							WSDMLoginAttrType loginAttr = this.wsdmManager.getLoginAttr();
							if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceAoo() != null) {
								// nel caso di protocollazione Titulus il codiceAoo
								// risulta valorizzato nel fascicolo a deve essere
								// letto per essere usato in protocollazione in
								// ingresso
								loginAttr.getLoginTitAttr().setCodiceAmministrazioneAoo(fascicoloBackOffice.getCodiceAoo());
							}
							if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceUfficio() != null) {
								// nel caso di protocollazione Titulus il codiceUfficio
								// risulta valorizzato nel fascicolo a deve essere
								// letto per essere usato in protocollazione in
								// ingresso
								loginAttr.getLoginTitAttr().setCodiceUfficio(fascicoloBackOffice.getCodiceUfficio());
							}
							WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn =
								creaInputProtocollazioneWSDM(
										allegatiMail, 
										impresa, 
										fascicoloBackOffice, 
										nuovaComunicazione, 
										gara, 
										isActiveFunctionPdfA,
										iccFilePath);
							
							ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
							this.annoProtocollo = ris.getAnnoProtocollo();
							this.numeroProtocollo = ris.getNumeroProtocollo();
							if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
								this.numeroProtocollo = ris.getGenericS11();
							}
							if(ris.getDataProtocollo() != null) {
								this.dataProtocollo = UtilityDate.convertiData(ris.getDataProtocollo().getTime(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
							} else { 
								this.dataProtocollo = UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
							}
							this.dataInvio = UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);

							// prepara il testo della mail di ricevuta da 
							// inviare all'impresa...
							this.mailRicevutaImpresaSubject = MessageFormat.format(this.mailRicevutaImpresaSubject, 
									new Object[] {this.codice});

							this.mailRicevutaImpresaText = MessageFormat.format(this.mailRicevutaImpresaText, 
									new Object[] {impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
												  this.dataProtocollo,
												  (this.annoProtocollo != null ? this.annoProtocollo.toString() : ""), 
												  (StringUtils.isNotEmpty(this.numeroProtocollo) ? this.numeroProtocollo : "")});

							if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
								this.mailRicevutaImpresaText = MessageFormat.format(this.mailRicevutaImpresaText, 
										new Object[] {impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
													  ris.getGenericS11()}); 
							}

							protocollazioneInoltrata = true;
							this.eventManager.insertEvent(evento);
	
							// si aggiorna lo stato a 3 aggiornando inoltre anche i
							// dati di protocollazione
							documento = new WSDocumentoType();
							documento.setEntita(entitaComunicazione);
							documento.setChiave1(this.codice);
							documento.setNumeroDocumento(ris.getNumeroDocumento());
							documento.setAnnoProtocollo(ris.getAnnoProtocollo());
							documento.setNumeroProtocollo(ris.getNumeroProtocollo());
							documento.setVerso(WSDocumentoTypeVerso.IN);
							documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());
							
							// si rilegge la comunicazione in modo da ottenere gli id degli allegati
							nuovaComunicazione = this.comunicazioniManager.getComunicazione(nuovaComunicazione.getDettaglioComunicazione().getApplicativo(), nuovaComunicazione.getDettaglioComunicazione().getId());
	
							WSAllegatoType[] allegati = new WSAllegatoType[1 + nuovaComunicazione.getAllegato().length];
							allegati[0] = new WSAllegatoType();
							allegati[0].setEntita("W_INVCOM");
							allegati[0].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
							allegati[0].setChiave2(nuovaComunicazione.getDettaglioComunicazione().getId().toString());
							for (int i = 0; i < nuovaComunicazione.getAllegato().length; i++) {
								allegati[i+1] = new WSAllegatoType();
								allegati[i+1].setEntita("W_DOCDIG");
								allegati[i+1].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
								allegati[i+1].setChiave2(nuovaComunicazione.getAllegato(i).getId().toString());
							}
	
							evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);							
							evento.setMessage(
									"Aggiornamento comunicazione con id " + idComunicazione
									+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA
									+ ", protocollata con anno " + ris.getAnnoProtocollo() 
									+ " e numero " + ris.getNumeroProtocollo());
							
							// LAPISOPERA: inserisci l'ID richiesta restituito dal protocollo nella W_INVCOM e aggiorna il messaggio dell'evento
							ProcessPageProtocollazioneAction.lapisAggiornaComunicazione(
									ris,
									documento,
									nuovaComunicazione.getDettaglioComunicazione().getId(),
									this.appParamManager,
									evento);

							this.comunicazioniManager.protocollaComunicazione(
									CommonSystemConstants.ID_APPLICATIVO,
									idComunicazione,
									ris.getNumeroProtocollo(),
									(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : dataInvio),
									CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA,
									documento,
									allegati);
							
							this.eventManager.insertEvent(evento);
						} catch (Exception t) {
							protocollazioneException = t;
							evento.setError(t);
							this.eventManager.insertEvent(evento);
							if (protocollazioneInoltrata) {
	
								// segnalo l'errore, comunque considero la
								// protocollazione andata a buon fine e segnalo nel
								// log e a video che va aggiornato a mano lo stato
								// della comunicazione
								this.msgErrore = getText(
										"Errors.updateStatoComunicazioneDaProcessare",
										new String[] { String.valueOf(idComunicazione) });
								ApsSystemUtils.logThrowable(t, this, "sendComunicazione", this.msgErrore);
								addActionError(this.msgErrore);
	
								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Aggiornare manualmente la comunicazione con id " + idComunicazione
										+ " ed inserire inoltre un documento in ingresso per entita' " + documento.getEntita()
										+ ", chiave1 " + documento.getChiave1()
										+ ", oggetto " + documento.getOggetto()
										+ ", numero documento " + documento.getNumeroDocumento()
										+ ", anno protocollo " + documento.getAnnoProtocollo()
										+ " e numero protocollo " + documento.getNumeroProtocollo()
										+ " ed un allegato per la comunicazione ed uno per ogni documento effettivamente allegato");
								ProcessPageProtocollazioneAction.lapisAggiornaMessaggioEventoCompletamentoManuale(
										ris,
										documento,
										nuovaComunicazione.getDettaglioComunicazione().getId(),
										this.appParamManager,
										evento);
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
							} else {
								ApsSystemUtils.logThrowable(
										t,
										this.action,
										"sendComunicazione",
										"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
								this.msgErrore = getText("Errors.service.wsdmHandshake");
								manageWSDMExceptionError(t, this.action);
								this.target = BaseAction.INPUT;
								// si deve annullare l'invio che si e' tentato di
								// protocollare
								this.annullaComunicazioneInviata(
										CommonSystemConstants.ID_APPLICATIVO, 
										nuovaComunicazione.getDettaglioComunicazione().getId());
								protocollazioneOk = false;
							}
						}
						
						break;
					default:
						// qualsiasi altro caso: non si protocolla nulla!!!
						break;
				}
			}

			// elimina eventuali file temporanei...
			// ovvero gli allegati della mail di protocollazione...
			if(eliminaTemp)
				allegatiMail.getAdditionalDocs().forEach(attachment -> deleteTempFile(attachment.getFileName()));


			//******************************************************************
			// FASE 3: 
			// se gli step precedenti sono andati a buon fine, 
			// si invia la ricevuta all'impresa
			//******************************************************************
			if (statoComunicazione != CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA &&
				inviataComunicazione && protocollazioneOk) 
			{
				evento = new Event();
				evento.setUsername(username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage(
						"Invio mail ricevuta di conferma trasmissione comunicazione " + nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione()
						+ " a " + (String) ((IUserProfile) this.currentUser.getProfile()).getValue("email"));
				evento.setIpAddress(this.currentUser.getIpAddress());
				evento.setSessionId(this.sessionId);

				try {
					this.sendMailConfermaImpresa();
				} catch (ApsException t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di invio offerta per la gara {}.",
							new Object[] { username, this.codice });
					this.msgErrore = getText("Errors.sendMailError");
					ApsSystemUtils.logThrowable(t, this.action, "sendComunicazione");
					evento.setError(t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}

			// FASE 4: se invio e protocollazione sono andate a buon fine, 
			// anche se la ricevuta all'impresa non e' stata inviata, 
			// si procede con la pulizia della sessione
			if (inviataComunicazione && protocollazioneOk) {
				this.target = BaseAction.SUCCESS;
			}
			
			// se la protocollazione non e' andata a buon fine 
			// genera un'eccezione per il chiamante...
			if(protocollazioneException != null) {
				throw protocollazioneException;
			}

		} else {
			// la sessione e' scaduta, occorre riconnettersi
			addActionError(getText("Errors.sessionExpired"));
			this.target = CommonSystemConstants.PORTAL_ERROR;
		}
				
		// imposta il target della action...
		if(this.action != null) {
			if(this.action instanceof EncodedDataAction) {
				((EncodedDataAction)this.action).setTarget(target);
			}
//			if(this.action instanceof BaseAction) {
//				((BaseAction)this.action). ...
//			}
		}		
		
		return idComunicazione;
	}

	private String getText(String aTextName) {
		String txt = "";
		if(this.action != null) {
			if(this.action instanceof BaseAction) {
				txt = ((BaseAction)this.action).getText(aTextName);
			}
		}
		return txt; 
	} 

	private String getText(String key, String[] args) {
		String txt = "";
		if(this.action != null) {
			if(this.action instanceof BaseAction) {
				txt = ((BaseAction)this.action).getText(key, args);
			}		
			if(txt != null && args != null) {
				for(int i = 0; i < args.length; i++) {
					txt = txt.replaceAll("\\{" + i + "\\}", args[i]);
				}
			}
		}
		return txt; 
	} 

	private String getI18nLabelFromDefaultLocale(String key) {
		String txt = "";
		if(this.action != null) {
			if(this.action instanceof BaseAction) {
				txt = ((BaseAction)this.action).getI18nLabelFromDefaultLocale(key);
			}		
		}
		return txt; 
	} 

	private void addActionError(String anErrorMessage) {
		if(this.action != null) {
			if(this.action instanceof BaseAction) {
				((BaseAction)this.action).addActionError(anErrorMessage);
			}
		}
	}
	
	private void manageExceptionError(Exception ex, Object action) {
		if(this.action != null) {
			if(this.action instanceof EncodedDataAction) {
				ExceptionUtils.manageExceptionError(ex, (EncodedDataAction)this.action);
			}
		}
	}
	
	private void manageWSDMExceptionError(Exception ex, Object action) {
		if(this.action != null) {
			if(this.action instanceof EncodedDataAction) {
				ExceptionUtils.manageWSDMExceptionError(ex, (EncodedDataAction)this.action);
			}
		}
	}

	/**
	 * converte un file in un array di byte
	 */
	private byte[] fileToBytes(File src) {
		byte[] bytes = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(src);
	        bytes = IOUtils.toByteArray(fis);
		} catch (FileNotFoundException e) {
			ApsSystemUtils.logThrowable(e, this, "fileToBytes");
			manageExceptionError(e, this.action);
			this.target = CommonSystemConstants.PORTAL_ERROR;
			bytes = null;
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "fileToBytes");
			manageExceptionError(e, this.action);
			this.target = CommonSystemConstants.PORTAL_ERROR;
			bytes = null;
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch(Exception e) {
				} 
			}
		}
		return bytes;
	}
	
	/**
	 * converte un byte array in un file 
	 */
	private File bytesToFile(byte[] src, String filename) {
		File file = null;
		try {
			file = new File(
					StrutsUtilities.getTempDir(ServletActionContext.getServletContext()).getAbsolutePath() 
					+ File.separatorChar + filename);
			FileUtils.writeByteArrayToFile(file, src);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "bytesToFile");
			manageExceptionError(e, this.action);
			this.target = CommonSystemConstants.PORTAL_ERROR;
			file = null;
		} 
		return file;
	}
	
	/**
	 * elimina un file temporaneo
	 */
	private void deleteTempFile(String filename) {
		try {
			File file = new File(
					StrutsUtilities.getTempDir(ServletActionContext.getServletContext()).getAbsolutePath() 
					+ File.separatorChar + filename);
			file.delete();
		} catch (Exception e) {
			//ApsSystemUtils.logThrowable(e, this, "deleteTempFile");
			//ExceptionUtils.manageExceptionError(e, this.action);
		} 
	}	
	
	/**
	 * isProtocollazionePrevista()
	 * 
	 * @param tipoProtocollazione
	 * @param stazioneAppaltanteProtocollante
	 * @param stazioneAppaltanteProcedura
	 * @return
	 */
	private static boolean isProtocollazionePrevista(
			int tipoProtocollazione, 
			String stazioneAppaltanteProtocollante, 
			String stazioneAppaltanteProcedura) {
		return ( new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) != tipoProtocollazione) &&
			     ((StringUtils.stripToNull(stazioneAppaltanteProtocollante) == null)
			       ||(stazioneAppaltanteProcedura.equals(stazioneAppaltanteProtocollante)) );
	}

	/**
	 * Invia all'impresa, la mail di ricevuta di invio comunicazione 
	 */
	private void sendMailConfermaImpresa() throws ApsSystemException {
		this.mailManager.sendMail(
				this.mailRicevutaImpresaText,
				this.mailRicevutaImpresaSubject,
				new String[] {(String)((IUserProfile) this.currentUser.getProfile()).getValue("email")},
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}

	/**
	 * Invia la mail di notifica all'ufficio protocolli
	 */ 
	private void sendMailRichiestaUfficioProtocollo(
			WizardDocumentiHelper documenti, 
			WizardDatiImpresaHelper impresa) throws ApsSystemException {
 
		if (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL == this.tipoProtocollazione) {
			if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
				throw new ApsSystemException("Valorizzare la configurazione " + 
						  AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}
			
			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio
			Properties p = null;

			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.allegaDocMailUfficioProtocollo && documenti != null) {
				// si predispongono gli allegati da inserire
				if (documenti.getRequiredDocs().size() + documenti.getAdditionalDocs().size() > 0) {
					p = new Properties();
					for (Attachment attachment : documenti.getRequiredDocs())
						p.put(attachment.getFileName(),
							  attachment.getFile().getAbsolutePath());
					for (Attachment attachment : documenti.getAdditionalDocs())
						p.put(attachment.getFileName(),
							  attachment.getFile().getAbsolutePath());
				}
				
				this.mailUffProtocolloText += 
					"\n\n" + this.getText("label.invioComunicazione.mailProtocollo.allegatoTestoDocumento");
			}

			String[] destinatari = this.mailUfficioProtocollo.split(",");
			
			this.mailManager.sendMail(
					this.mailUffProtocolloText,
					this.mailUffProtocolloSubject,
					IMailManager.CONTENTTYPE_TEXT_PLAIN, 
					p, 
					destinatari, 
					null,
					null, 
					CommonSystemConstants.SENDER_CODE);
		}
	}

	/**
	 * creaInputProtocollazioneWSDM()
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 * @throws DocumentException 
	 */
	private WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardDocumentiHelper documentiHelper, 
			WizardDatiImpresaHelper datiImpresaHelper,
			FascicoloProtocolloType fascicoloBackOffice,
			ComunicazioneType comunicazione,
			DettaglioGaraType gara, 
			boolean isActiveFunctionPdfA, 
			InputStream iccFilePath) throws IOException, DocumentException 
	{
		// ATTENZIONE:
		// L'UTILIZZO DEL CAST A INTEGER SUL METODO appParamManager.getConfigurationValue(...)
		// E' DEPRECATO, IN QUANTO NON E' SEMPRE VERO CHE IN PPCOMMON_PROPERTIES ESISTA
		// LA DEFINIZIONE DEL TIPO DI DATO DA RESTITUIRE (INT, BOOLEAN, STRING)
		// TUTTI I NUOVI PARAMETRI ANDREBBERO CONSIDERATI SEMPRE COME STRINGHE
		String codiceSistema = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
		
		boolean esisteFascicolo = (fascicoloBackOffice != null); 
		
		String codiceFascicolo = null;
		Long annoFascicolo = null;
		String numeroFascicolo = null;
		String classificaFascicolo = null;
		String tipoDocumento = null;
		String codiceRegistro = null;
		String idUnitaOperDestinataria = null;
		String idIndice = null;
		String idTitolazione = null;
		Integer cfMittente = (Integer) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE);
		boolean usaCodiceFiscaleMittente = (cfMittente != null ? 1 == cfMittente : true);
		String channelCode = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE);
		String sottoTipo = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SOTTOTIPO_COMUNICAZIONE);
		String rup = (gara.getStazioneAppaltante() != null ? gara.getStazioneAppaltante().getRup() : null);
		String acronimoRup = ProcessPageProtocollazioneAction.getAcronimoSoggetto(rup);

		if (this.genere == 10) {
			// ELENCO OPERATORI
			classificaFascicolo = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CLASSIFICA);
			tipoDocumento = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CODICE_REGISTRO);
			idIndice = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_INDICE);
		} else if (this.genere == 20) {
			// CATALOGO
			classificaFascicolo = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_CLASSIFICA);
			tipoDocumento = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_CODICE_REGISTRO);
			idIndice = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_INDICE);
		} else if (this.genere == 11) {
			// AVVISI
			classificaFascicolo = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_CLASSIFICA);
			tipoDocumento = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_CODICE_REGISTRO);
			idIndice = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_INDICE);
		} else {
			// GARE, ODA
			classificaFascicolo = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);
			tipoDocumento = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CODICE_REGISTRO);
			idIndice = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);
		}
		
		if (esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			classificaFascicolo = fascicoloBackOffice.getClassifica();
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
			if (this.genere == 10L) {
				// ELENCO
				idTitolazione = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TITOLAZIONE);
			} else if (this.genere == 20L) {
				// CATALOGO
				idTitolazione = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_TITOLAZIONE);
			} if (this.genere == 11L) {
				// AVVISI
				idTitolazione = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_TITOLAZIONE);				
			} else {
				// GARE, ODA
				idTitolazione = (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TITOLAZIONE);
			}
		}
		
		idUnitaOperDestinataria = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		
		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);
		wsdmProtocolloDocumentoIn.setTipoDocumento(tipoDocumento);
		wsdmProtocolloDocumentoIn.setChannelCode(channelCode);

		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOW.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS12(rup);
			wsdmProtocolloDocumentoIn.setGenericS42( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_DIVISIONE) );
		}
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS11(sottoTipo);
			wsdmProtocolloDocumentoIn.setGenericS12(rup);
		}
		if(IWSDMManager.CODICE_SISTEMA_DOCER.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS11( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPO_FIRMA) );
			wsdmProtocolloDocumentoIn.setGenericS31( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_DESC_UNITA_OPERATIVA_DESTINATARIA) );
			wsdmProtocolloDocumentoIn.setGenericS32(idUnitaOperDestinataria);
		}		
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			if(esisteFascicolo) {
				wsdmProtocolloDocumentoIn.setGenericS31(fascicoloBackOffice.getCodiceUfficio());
			} 
		}

		Calendar data = new GregorianCalendar();
		data.setTimeInMillis(this.dataPresentazione.getTime());	
		wsdmProtocolloDocumentoIn.setData(data);
		
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String oggetto = this.WsdmProtocolloSubject;
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.codice + " - " + oggetto;
		}
		
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}
		
		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		wsdmProtocolloDocumentoIn.setDescrizione(this.WsdmProtocolloText);
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceRegistro);
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId());
		// servono per Archiflow
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(gara.getStazioneAppaltante().getCodice());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + gara.getDatiGeneraliGara().getCodice());
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(gara.getDatiGeneraliGara().getCodice());
		}
		wsdmProtocolloDocumentoIn.setCig(gara.getDatiGeneraliGara().getCig());
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(gara.getStazioneAppaltante().getCodice());
		wsdmProtocolloDocumentoIn.setMezzo((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));

		// Mittente
		wsdmProtocolloDocumentoIn.setMittenteInterno((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MITTENTE_INTERNO));
		if (IWSDMManager.CODICE_SISTEMA_JIRIDE.equals(codiceSistema)
				&& esisteFascicolo
				&& StringUtils.isNotEmpty(fascicoloBackOffice
						.getStrutturaCompetente())) {
			// solo per JIRIDE valorizzo il mittente interno con la struttura del fascicolo, se valorizzata
			wsdmProtocolloDocumentoIn.setMittenteInterno(fascicoloBackOffice
					.getStrutturaCompetente());
		}
		
		IDatiPrincipaliImpresa impresa = datiImpresaHelper.getDatiPrincipaliImpresa();
		
		WSDMProtocolloAnagraficaType[] mittenti = new WSDMProtocolloAnagraficaType[1];
		mittenti[0] = new WSDMProtocolloAnagraficaType();
		mittenti[0].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);
		// JPROTOCOL: "Cognomeointestazione" accetta al massimo 100 char 
		if(IWSDMManager.CODICE_SISTEMA_JPROTOCOL.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 100));
		} else if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 200));
		} else {
			mittenti[0].setCognomeointestazione(ragioneSociale);
		}
		if (usaCodiceFiscaleMittente) {
			mittenti[0].setCodiceFiscale(codiceFiscale);
		} else {
			mittenti[0].setCodiceFiscale("");
		}
	    mittenti[0].setPartitaIVA(partitaIva);
		String indirizzo = impresa.getIndirizzoSedeLegale() + " " + impresa.getNumCivicoSedeLegale(); 
		mittenti[0].setIndirizzoResidenza(indirizzo);
		mittenti[0].setLocalitaResidenza(impresa.getComuneSedeLegale());
		mittenti[0].setComuneResidenza(impresa.getComuneSedeLegale());
		mittenti[0].setNazionalita(impresa.getNazioneSedeLegale());
	    mittenti[0].setMezzo((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));
	    String email = DatiImpresaChecker.getEmailRiferimento(impresa.getEmailPECRecapito(), impresa.getEmailRecapito());
	    mittenti[0].setEmail(email);
	    mittenti[0].setProvinciaResidenza(impresa.getProvinciaSedeLegale());
	    mittenti[0].setCapResidenza(impresa.getCapSedeLegale());
		wsdmProtocolloDocumentoIn.setMittenti(mittenti);
		
		// Inserimento in fascicolo
		if (esisteFascicolo) {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
			WSDMFascicoloType fascicolo = new WSDMFascicoloType();
			fascicolo.setClassificaFascicolo(classificaFascicolo);
			fascicolo.setCodiceFascicolo(codiceFascicolo);
			fascicolo.setAnnoFascicolo(annoFascicolo);
			fascicolo.setNumeroFascicolo(numeroFascicolo);
			// oggettoFascicolo serve per Titulus: solitamente si invia
			// l'oggetto della gara, elenco, ... a cui ci si riferisce, ma
			// siccome il dato non e' recuperabile agevolmente ed inoltre il
			// fascicolo e' stato creato da backoffice, si scrive una stringa
			// fittizia
		    fascicolo.setOggettoFascicolo("PORTALE");
		    if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
		    	fascicolo.setGenericS11(acronimoRup);
		    	fascicolo.setGenericS12(rup);
				if("CORR_SOC".equals(wsdmProtocolloDocumentoIn.getTipoDocumento())) {
					String r = ProcessPageProtocollazioneAction.getInvertiCognomeNome(rup);
					wsdmProtocolloDocumentoIn.setGenericS11(ProcessPageProtocollazioneAction.getAcronimoSoggetto(r));
					wsdmProtocolloDocumentoIn.setGenericS12(r);
				}
		    }
//			if(IWSDMManager.CODICE_SISTEMA_DOCER.equals(codiceSistema)) {
//				fascicolo.setGenericS11( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPO_FIRMA) );
//				fascicolo.setGenericS31( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_DESC_UNITA_OPERATIVA_DESTINATARIA) );
//				fascicolo.setGenericS32(idUnitaOperDestinataria);
//			}

			wsdmProtocolloDocumentoIn.setFascicolo(fascicolo);
		} else {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
		}

		// prepara 1+N allegati...
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// ...verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		String v = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
		boolean inTesta = (v != null && "1".equals(v));
		
		int numDocDaProtocollare = 1 +
			(documentiHelper.getRequiredDocs() == null ? 0 : documentiHelper.getRequiredDocs().size()) +
		    (documentiHelper.getAdditionalDocs() == null ? 0 : documentiHelper.getAdditionalDocs().size());

		// Se non ho allegato nessun documento e sono in fase di aggiornamento
		// Il primo documento che allego contiene il corpo della mail.
		// In questo modo l'operazione di inserisciProtocollo non fallisce.
		// (inserisciProtocollo richiede obbligatoriamente un documento da protocollare).
		String titolo = this.WsdmProtocolloSubject; 
		String contenuto = this.WsdmProtocolloText;

		// crea l'elenco degli allegati per il protocollo
		WSDMProtocolloAllegatoType[] allegati = createAttachments(documentiHelper, comunicazione, isActiveFunctionPdfA,
				iccFilePath, inTesta, numDocDaProtocollare, titolo, contenuto);
				
		wsdmProtocolloDocumentoIn.setAllegati(allegati);

		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)){
			wsdmProtocolloDocumentoIn.setOggetto(this.codice + "-" + wsdmProtocolloDocumentoIn.getOggetto()+"-" +gara.getDatiGeneraliGara().getOggetto() );
		}
		Calendar dataArrivo = Calendar.getInstance();
	    dataArrivo.setTimeInMillis(System.currentTimeMillis());
	    wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(allegati.length -1));	
	    wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
	    wsdmProtocolloDocumentoIn.setSupporto((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));
	    
    	String struttura = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STRUTTURA);
	    // se specificata in configurazione la uso, altrimenti riutilizzo la
		// configurazione presente nel fascicolo di backoffice (cosi' avveniva
		// per ARCHIFLOW)
		if (StringUtils.isNotEmpty(struttura)) {
			wsdmProtocolloDocumentoIn.setStruttura(struttura);
		} else if (esisteFascicolo) {
	    	wsdmProtocolloDocumentoIn.setStruttura(fascicoloBackOffice.getStrutturaCompetente());
	    }
		
		String tipoAssegnazione = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPOASSEGNAZIONE);
		// se specificata in configurazione la uso (JPROTOCOL)
		if (StringUtils.isNotEmpty(tipoAssegnazione)) {
			wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoAssegnazione);
		}
		
		return wsdmProtocolloDocumentoIn;
	}

	private WSDMProtocolloAllegatoType[] createAttachments(WizardDocumentiHelper documentiHelper,
			ComunicazioneType comunicazione, boolean isActiveFunctionPdfA, InputStream iccFilePath, boolean inTesta,
			int numDocDaProtocollare, String titolo, String contenuto) throws IOException, DocumentException {
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[numDocDaProtocollare];
		
		int n = 0;
		if(inTesta)
			n++;
		
		// inserisci gli eventuali allegati dei documenti richiesti ed ulteriori...
		if(documentiHelper != null && numDocDaProtocollare > 0) {

			// allega i documenti richiesti...
			if(documentiHelper.getRequiredDocs() != null && !documentiHelper.getRequiredDocs().isEmpty()) {
//				// DA FARE !!!
//				List<DocumentazioneRichiestaType> listaDocRichiestiBO = this.bandiManager.getDocumentiRichiestiBandoGara(
//				    this.codice,
//				    codiceLotto,
//				    datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(),
//				    wizardPartecipazione.isRti(),
//				    String.valueOf(PortGareSystemConstants.BUSTA_ECONOMICA));
				List<DocumentazioneRichiestaType> listaDocRichiestiBO = new ArrayList<DocumentazioneRichiestaType>(); 

				for(Attachment attachment : documentiHelper.getRequiredDocs()) {
					// cerca la descrizione dell'allegato richiesto...
					String descrizione = null;
					for (DocumentazioneRichiestaType item : listaDocRichiestiBO) {
						if (attachment.getId() == item.getId()) {
							descrizione = item.getNome();
							break;
						}
					}

					allegati[n] = new WSDMProtocolloAllegatoType();
					allegati[n].setNome(attachment.getFileName());
					allegati[n].setTipo(StringUtils.substringAfterLast(attachment.getFileName(), "."));
					allegati[n].setTitolo(descrizione);
//					allegati[n].setContenuto( FileUtils.readFileToByteArray(docRichiesti.get(j)) );
					allegati[n].setContenuto( documentiHelper.getContenutoDocRichiesto(attachment) );
					// serve per Titulus
					allegati[n].setIdAllegato("W_INVCOM/"
							+ CommonSystemConstants.ID_APPLICATIVO + "/"
							+ comunicazione.getDettaglioComunicazione().getId()	+ "/" + n);
					n++;
				}
			}

			// allega i documenti ulteriori...
			if(CollectionUtils.isNotEmpty(documentiHelper.getAdditionalDocs())) {
				for (Attachment attachment : documentiHelper.getAdditionalDocs()) {
					allegati[n] = new WSDMProtocolloAllegatoType();
					allegati[n].setNome(attachment.getFileName());
					allegati[n].setTipo(StringUtils.substringAfterLast(attachment.getFileName(), "."));
					allegati[n].setTitolo(attachment.getDesc());
//					allegati[n].setContenuto( FileUtils.readFileToByteArray(documentiHelper.getDocUlteriori().get(j)) );
					allegati[n].setContenuto(documentiHelper.getContenutoDocUlteriore(attachment));
					// serve per Titulus
					allegati[n].setIdAllegato("W_INVCOM/"
							+ CommonSystemConstants.ID_APPLICATIVO + "/"
							+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
					n++;
				}
			}
		}

		// inserisci "comunicazione.pdf" nella lista degli allegati (in testa/in coda)
		// prepara l'allegato "comunicazione.pdf"
		int n2 = n;
		if(inTesta) {
			n2 = 0;
		}
		
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setTitolo(titolo);
		allegati[n2].setNome("comunicazione.pdf");
		allegati[n2].setTipo("pdf");
		byte[] contenutoPdf = null;
		if(isActiveFunctionPdfA) {
			try {
				ApsSystemUtils.getLogger().debug("Trasformazione contenuto in PDF-A");
				contenutoPdf = UtilityStringhe.string2PdfA(contenuto,iccFilePath);
				allegati[n2].setTipo("pdf/a");
			} catch (com.itextpdf.text.DocumentException e) {
				DocumentException de = new DocumentException("Impossibile creare il contenuto in PDF-A.");
				de.initCause(e);
				throw de;
			}
		} else {
			contenutoPdf = UtilityStringhe.string2Pdf(contenuto);
		}
		allegati[n2].setContenuto(contenutoPdf);
		// serve per Titulus
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId() + "/" + n2);
		
		ProtocolsUtils.setFieldsForNumix(allegati);
		
		return allegati;
	}
	
	
	/**
	 * Annulla gli effetti della comunicazione inviata riportandola nello stato bozza
	 * 
	 * @param comunicazione
	 *            comunicazione da riportare in stato bozza
	 */
	private void annullaComunicazioneInviata(String idApplicativo, Long idComunicazione) {
		Event evento = new Event();
		evento.setUsername(this.currentUser.getUsername());
		evento.setDestination(this.codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Annullamento comunicazione con id " + idComunicazione);
		evento.setIpAddress(this.currentUser.getIpAddress());
		try {
			//this.comunicazioniManager.deleteComunicazione(idApplicativo, idComunicazione);
			ComunicazioneType comunicazione = this.comunicazioniManager
				.getComunicazione(idApplicativo, idComunicazione);
			if(comunicazione != null) {
				comunicazione.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				this.comunicazioniManager.sendComunicazione(comunicazione);
			}
		} catch (ApsException e) {
			msgErrore = getText(
					"Errors.deleteComunicazione",
					new String[] {idComunicazione.toString()});
			ApsSystemUtils.logThrowable(e, this.action, "annullaComunicazioneInviata", this.msgErrore);
			manageExceptionError(e, this.action);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

//	//*************************************************************************
//	// INVIO FS...: OFFERTA ECONOMICA
//	//*************************************************************************	
//	/**
//	 * Richiesta di invio comunicazione per un'offerta economica
//	 * 
//	 * @param statoComunicazione stato della comunicazione
//	 * @param codice codice della gara
//	 * @param codiceLotto codice lotto della gara
//	 * @param helper helper dell'offerta economica
//	 * @throws Exception 
//	 * 
//	 */
//	public Long sendRichiestaInvioComunicazioneOffertaEconomica(
//			String statoComunicazione,
//			String codice, 
//			String codiceLotto,
//			String comunicazioneSubject,
//			String comunicazioneText,
//			String mailRicevutaSubject,
//			String mailRicevutaText,
//			String mailRicevutaConProtocolloText,
//			String mailProtocolloSubject,
//			String mailProtocolloText,
//			WizardOffertaEconomicaHelper helper) throws Exception {
//
//		WizardDatiImpresaHelper impresa = helper.getImpresa();
//		Date dataInvio = new Date();
//		
//		String descrCodiceLotto = ""; 
//		if(codiceLotto != null && !codiceLotto.isEmpty()) {
//			descrCodiceLotto = " per lotto " + codiceLotto;
//		}
//		
//		// NB: per un'offerta economica esiste un allegato "xyz.xml" 
//		//     che contiene i documenti allegati...
//
//		// comunicazione
//		this.dettaglioComunicazioneSubject = getText(comunicazioneSubject, new String[]{
//				codice}) + descrCodiceLotto;
//		this.dettaglioComunicazioneText = getText(comunicazioneText, new String[]{
//				impresa.getDatiPrincipaliImpresa().getRagioneSociale(), 
//				UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS)});
//		
//		// mail ricevuta
//		this.mailRicevutaImpresaSubject = getText(mailRicevutaSubject, new String[]{});
//		if(this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA
//		   || this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL) {
//			this.mailRicevutaImpresaText = mailRicevutaText;
//		} else {
//			this.mailRicevutaImpresaText = mailRicevutaConProtocolloText;
//		}
//		
//		// mail protocollo
//		this.mailUffProtocolloSubject = getText(comunicazioneSubject, new String[]{
//				codice}) + descrCodiceLotto;
//		this.mailUffProtocolloText = getText(mailProtocolloText, new String[]{
//				impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
//				impresa.getDatiPrincipaliImpresa().getCodiceFiscale(),
//				impresa.getDatiPrincipaliImpresa().getPartitaIVA(),
//				(String) ((IUserProfile) this.currentUser.getProfile()).getValue("email"),
//				impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " "
//				+ impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", "
//				+ impresa.getDatiPrincipaliImpresa().getCapSedeLegale() + " "
//				+ impresa.getDatiPrincipaliImpresa().getComuneSedeLegale() + " ("
//				+ impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")",
//				UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
//				codice //+ descrCodiceLotto
//				});
//
//		// DA RIVEDERE!!!
//		// prepara l'helper dei documenti allegati all'offerta economica
//		WizardDocumentiHelper documenti = new WizardDocumentiHelper();
//		
//		documenti.setDocRichiesti( helper.getDocumenti().getDocRichiesti() );
//		documenti.setDocRichiestiContentType( helper.getDocumenti().getDocRichiestiContentType() );
//		documenti.setDocRichiestiFileName( helper.getDocumenti().getDocRichiestiFileName() );
//		documenti.setDocRichiestiId( helper.getDocumenti().getDocRichiestiId() );
//		documenti.setDocRichiestiSize( helper.getDocumenti().getDocRichiestiSize() );
//		
//		documenti.setDocUlteriori( helper.getDocumenti().getDocUlteriori() );
//		documenti.setDocUlterioriContentType( helper.getDocumenti().getDocUlterioriContentType() );
//		documenti.setDocUlterioriDesc( helper.getDocumenti().getDocUlterioriDesc() );
//		documenti.setDocUlterioriFileName( helper.getDocumenti().getDocUlterioriFileName() );
//		documenti.setDocUlterioriSize( helper.getDocumenti().getDocUlterioriSize() );
//
//		return sendComunicazione(
//				PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE,  
//				statoComunicazione, 
//				"GARE", 
//				codice, 
//				codiceLotto, 
//				helper.getImpresa(), 
//				documenti, 
//				null,
//				dataInvio);
//	}
	
	//*************************************************************************
	// INVIO FS12: OFFERTA D'ASTA
	//*************************************************************************
	/**
	 * Richiesta di invio comunicazione per un'offerta d'asta
	 * 
	 * @param statoComunicazione stato della comunicazione
	 * @param codice codice della gara
	 * @param codiceLotto codice lotto della gara
	 * @param statoComunicazione (1=BOZZA, 3=INVIATA)
	 * @param helper helper dell'offerta economica
	 * @throws Exception 
	 * 
	 */	
	public Long sendRichiestaInvioComunicazioneOffertaAsta(
			WizardOffertaAstaHelper helper,
			String statoComunicazione) throws Exception 
	{	
		WizardDatiImpresaHelper impresa = helper.getOffertaEconomica().getImpresa();
		Date dataInvio = new Date();
		
		String codice = helper.getAsta().getCodice();
		String codiceLotto = helper.getAsta().getCodiceLotto();

		String descrCodiceLotto = ""; 
		if(codiceLotto != null && !codiceLotto.isEmpty()) {
			descrCodiceLotto = " per lotto " + codiceLotto;
		}
		
		// prepara i riferimenti alle label da utilizzare
		//String keyComunicazioneSubject = "MAIL_ASTE_RICEVUTA_OGGETTO"; 
		//String keyComunicazioneText = "MAIL_ASTE_PROTOCOLLO_TESTO";
		//String keyMailRicevutaSubject = "MAIL_ASTE_RICEVUTA_OGGETTO";
		//String keyMailRicevutaText = "MAIL_ASTE_RICEVUTA_TESTO";
		//String keyMailRicevutaConProtocolloText = "MAIL_ASTE_RICEVUTA_TESTOCONPROTOCOLLO";
		//String keyMailProtocolloSubject = "MAIL_ASTE_RICEVUTA_OGGETTO";
		//String keyMailProtocolloText = "MAIL_ASTE_PROTOCOLLO_TESTO"; 
		
		InputStream iccFilePath = null;
		boolean isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A");
		if(isActiveFunctionPdfA) {
			iccFilePath = new FileInputStream(ServletActionContext.getRequest().getSession().getServletContext()
					.getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
		}
		
		// prepara gli oggetto e testo per comunicazioni e mail...
		this.dettaglioComunicazioneSubject =
			PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE_OFFERTA_ASTA_OGGETTO 
			+ " " + codice + descrCodiceLotto;
		this.dettaglioComunicazioneText = MessageFormat.format(
			this.getI18nLabelFromDefaultLocale("MAIL_ASTE_PROTOCOLLO_TESTO"), 
			new Object[] {
					impresa.getDatiPrincipaliImpresa().getRagioneSociale(), 
					impresa.getDatiPrincipaliImpresa().getCodiceFiscale(),
					impresa.getDatiPrincipaliImpresa().getPartitaIVA(),
					(String) ((IUserProfile) this.currentUser.getProfile()).getValue("email"),
					impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " "
					+ impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", "
					+ impresa.getDatiPrincipaliImpresa().getCapSedeLegale() + " "
					+ impresa.getDatiPrincipaliImpresa().getComuneSedeLegale() + " ("
					+ impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")",
					UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
					codice //+ descrCodiceLotto
					});
			
		// verifica l'esistenza delle label ed risolvile in chiaro  
		this.mailRicevutaImpresaSubject = this.getI18nLabelFromDefaultLocale("MAIL_ASTE_RICEVUTA_OGGETTO");
		if(this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA
		   || this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL) 
		{
			this.mailRicevutaImpresaText = this.getI18nLabelFromDefaultLocale("MAIL_ASTE_RICEVUTA_TESTO");
		} else {
			this.mailRicevutaImpresaText = this.getI18nLabelFromDefaultLocale("MAIL_ASTE_RICEVUTA_TESTOCONPROTOCOLLO");
		}
		this.mailUffProtocolloSubject = this.getI18nLabelFromDefaultLocale("MAIL_ASTE_RICEVUTA_OGGETTO");
		this.mailUffProtocolloText = this.getI18nLabelFromDefaultLocale("MAIL_ASTE_PROTOCOLLO_TESTO"); 
		
		// prepara i messaggi... 
		this.mailUffProtocolloSubject = MessageFormat.format(this.mailUffProtocolloSubject, new Object[] {
				codice + descrCodiceLotto});
		this.mailUffProtocolloText = MessageFormat.format(this.mailUffProtocolloText, new Object[] {
				impresa.getDatiPrincipaliImpresa().getRagioneSociale(),
				impresa.getDatiPrincipaliImpresa().getCodiceFiscale(),
				impresa.getDatiPrincipaliImpresa().getPartitaIVA(),
				(String) ((IUserProfile) this.currentUser.getProfile()).getValue("email"),
				impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " "
					+ impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", "
					+ impresa.getDatiPrincipaliImpresa().getCapSedeLegale() + " "
					+ impresa.getDatiPrincipaliImpresa().getComuneSedeLegale() + " ("
					+ impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")",
				UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
				codice //+ descrCodiceLotto
				});
		
		// prepara l'elenco degli allegati della comunicazione...
		// NB: FS12 e' una comunicazione senza documento xml che puo' contenere
		//     documenti come allegati
		AllegatoComunicazioneType[] allegati = null;
		
		// allega alla FS12 il PDF dell'offerta d'asta
		WizardDocumentiBustaHelper documenti = helper.getDocumenti();
		if(documenti != null && documenti.getAdditionalDocs() != null)
			allegati = documenti.getAdditionalDocs().stream().map(this::toAllegatiComunicazioneType).toArray(AllegatoComunicazioneType[]::new);

		// recupera la FS13 e verifica se e' gia presente una FS12 (FS13.COMKEY3 > 0 ==> FS12.idcom)
		List<String> stati = new ArrayList<String>(); 
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		
		DettaglioComunicazioneType dettComunicazioneFS13 = ComunicazioniUtilities.retrieveComunicazioneConStati(
				this.comunicazioniManager,
				this.currentUser.getUsername(), 
				codiceLotto, 
				null,
				PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA, 
				stati);
		
		Long idComunicazioneF12 = null;
		if(dettComunicazioneFS13 != null && StringUtils.isNotEmpty(dettComunicazioneFS13.getChiave3())) {				
			idComunicazioneF12 = new Long(Long.parseLong(dettComunicazioneFS13.getChiave3())); 
		}
					
		Long idComunicazione = sendComunicazione(
				idComunicazioneF12,
				PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE,  
				statoComunicazione, 
				"GARE", 
				codice, 
				codiceLotto, 
				impresa, 
				allegati,	   
				dataInvio, 
				isActiveFunctionPdfA, 
				iccFilePath);
		
		helper.getOffertaEconomica().setIdComunicazione(idComunicazione);
		
		return idComunicazione;
	}

	private AllegatoComunicazioneType toAllegatiComunicazioneType(Attachment attachment) {
		try {
			AllegatoComunicazioneType toReturn = new AllegatoComunicazioneType();
			//allegati[i].setId( null );
			//allegati[i].setFile( this.fileToBytes(documenti.getDocUlteriori().get(i)) );
			toReturn.setFile(FileUtils.readFileToByteArray(attachment.getFile()));
			toReturn.setDescrizione(attachment.getDesc());
			toReturn.setNomeFile(attachment.getFileName());
			toReturn.setTipo(StringUtils.substringAfterLast(attachment.getFileName(), ".") );
			//allegati[i].setModificato(...);
			//allegati[i].setUuid(...);
			return toReturn;
		} catch (IOException io) {
			throw new RuntimeException(io);
		}
	}

	//*************************************************************************
	// INVIO FS13
	//*************************************************************************	
	/**
	 * Invia una comunicazione FS13 relativa alla generazione del pdf 
	 * dell'offerta d'asta.
	 * @throws Exception 
	 */
	public Long sendComunicazioneGenerazioneOffertaAsta(
			WizardOffertaAstaHelper helper,
			boolean processata) throws Exception 
	{
		Long idComunicazione = null;
		
		if(helper != null) {
			String codice = 
				(helper.getAsta().getCodiceLotto() != null && !helper.getAsta().getCodiceLotto().isEmpty() 
				 ? helper.getAsta().getCodiceLotto() 
				 : helper.getAsta().getCodice());
			
			String descrCodiceLotto = ""; 
			if(helper.getAsta().getCodiceLotto() != null && !helper.getAsta().getCodiceLotto().isEmpty()) {
				descrCodiceLotto = " per lotto " + helper.getAsta().getCodiceLotto();
			}
			
			String username = this.currentUser.getUsername();
			
			// traccia l'evento di salvataggio...
			Event evento = new Event();
			evento.setUsername(this.currentUser.getUsername());
			evento.setDestination(codice);
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
			evento.setIpAddress(this.currentUser.getIpAddress()); 

			try {
				//File pdfFile = ...
				String pdfUUID = helper.getOffertaEconomica().getPdfUUID();
				
				// verifica l'esistenza delle label e risolvi in chiaro...
				String oggetto = this.getI18nLabelFromDefaultLocale("NOTIFICA_ASTE_OGGETTO"); 
				String testo = this.getI18nLabelFromDefaultLocale("NOTIFICA_ASTE_TESTO"); 
				String descrizioneAllegatoXml = this.getI18nLabelFromDefaultLocale("NOTIFICA_ASTE_ALLEGATO_DESCRIZIONE");
				 
				// prepara i valori delle diciture...
				String nomeBusta = "busta asta";
				String ragioneSociale = helper.getOffertaEconomica().getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
				oggetto = MessageFormat.format(oggetto, new Object[] {helper.getAsta().getCodice()}) + descrCodiceLotto;
				testo = MessageFormat.format(testo, new Object[] {StringUtils.left(ragioneSociale, 200), nomeBusta});
				descrizioneAllegatoXml = MessageFormat.format(descrizioneAllegatoXml, new Object[] {nomeBusta});
				
				// verifica se esiste la comunicazione di supporto 
				// per le offerte d'asta FS13 in stato BOZZA (o PROCESSATA)...
				List<String> stati = new ArrayList<String>(); 
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				
				DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.retrieveComunicazioneConStati(
						comunicazioniManager,
						username,
						codice,
						null,
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA,
						stati);
				
				// FASE 1: costruzione del contenitore della comunicazione
				ComunicazioneType comunicazione = null;
		
				// FASE 2: popolamento della testata
				if(dettaglioComunicazione != null) {
					// comunicazione presente...
					idComunicazione = dettaglioComunicazione.getId();
					comunicazione = comunicazioniManager.getComunicazione(
							dettaglioComunicazione.getApplicativo(), 
							dettaglioComunicazione.getId());
				} else {
					// nessuna comunicazione gia' presente...
					comunicazione = new ComunicazioneType();
					dettaglioComunicazione = ComunicazioniUtilities
						.createDettaglioComunicazione(
							null,
							username, 
							codice,  
							null,
							ragioneSociale,
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA, 
							oggetto, 
							testo, 
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA, 
							null);
				}
				
				if(helper.getChiaveSessione() != null) {
					dettaglioComunicazione.setSessionKey(
							EncryptionUtils.encodeSessionKey(helper.getChiaveSessione(), username)); 
				}
				
				comunicazione.setDettaglioComunicazione(dettaglioComunicazione);

				// collega la comunicazione FS13 alla comunicazione FS12 (FS13.chiave3 = FS12.id)
				// in modo da poter recuperare il pdf se siamo ancora in stato BOZZA...
				if(helper.getOffertaEconomica().getIdComunicazione() != null && 
				   helper.getOffertaEconomica().getIdComunicazione().longValue() > 0) 
				{
					comunicazione.getDettaglioComunicazione().setChiave3(
							helper.getOffertaEconomica().getIdComunicazione().toString());
				}
				
				if(processata) {
					// prepara la comunicazione in stato PROCESSATA...
					comunicazione.getDettaglioComunicazione().setStato(
							CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				} else {
					//helper.getOffertaEconomica().setPdfUUID(pdfUUID);
					//helper.getOffertaEconomica().getPdfGenerati().clear();
					//helper.getOffertaEconomica().getPdfGenerati().add(pdfFile);
					
					// FASE 3: creazione allegato con dati xml
					Map<String, String> suggestedPrefixes = new HashMap<String, String>();
					suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
					XmlOptions opts = new XmlOptions();
					opts.setSaveSuggestedPrefixes(suggestedPrefixes);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					
					// inserisci il documento xml come I allegato della comunicazione 
					// e tutti i documenti dell'xml come allegati della comunicazione
					List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();
		
					AllegatoComunicazioneType allegato = ComunicazioniUtilities
						.createAllegatoComunicazione(
								PortGareSystemConstants.NOME_FILE_BUSTA,
								descrizioneAllegatoXml,
								null);
					allegatiXml.add(allegato);
					
					helper.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
					
				    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
				    XmlObject xmlDocument = helper.getXmlDocument(BustaEconomicaDocument.Factory.newInstance(), true, false);
				    xmlDocument.save(baos, opts);
				    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
				    
				    AllegatoComunicazioneType[] allegati = allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]);
				    comunicazione.setAllegato(allegati);
				}
				
				// FASE 4: invio comunicazione
				Long newId = comunicazioniManager.sendComunicazione(comunicazione);
				if(idComunicazione == null) {
					idComunicazione = newId; 
				}
				comunicazione.getDettaglioComunicazione().setId(idComunicazione);

				evento.setMessage("Salvataggio comunicazione "
						+ comunicazione.getDettaglioComunicazione().getTipoComunicazione()
						+ " con id " + comunicazione.getDettaglioComunicazione().getId()
						+ " in stato "
						+ comunicazione.getDettaglioComunicazione().getStato());

				helper.setIdComunicazione(idComunicazione);
				helper.getDocumenti().resetStatiInvio(comunicazione);
				
			} catch(IOException e) {
				evento.setError(e);
				throw e;
			} catch(GeneralSecurityException e) {
				evento.setError(e);
				throw e;
			} catch(ApsException e) {
				evento.setError(e);
				throw e;
			} finally {
				if(evento != null) {
					eventManager.insertEvent(evento);
				}
			}
		}
		
		return idComunicazione;
	}
	
	//************************************************************************
	//************************************************************************
	//************************************************************************
	
	/**
	 * estrai dalla comunicazione i documenti allegati alla busta...
	 * 
	 * @param comunicazione
	 * 			comunicazione dalla quale estrarre i documenti allegati
	 * @param allegatiBusta
	 * 			lista dei documenti allegati estratti
	 */
	public static void getAllegatiBustaFromComunicazione(
			ComunicazioneType comunicazione, 
			ListaDocumentiType allegatiBusta) 
	{ 
		if(comunicazione != null && allegatiBusta != null) {
			// crea la mappa dei documenti con uuid...
			Map<String, Long> hashUuid = new HashMap<String, Long>();
			for(int i = 0; i < comunicazione.getAllegato().length; i++) {
				if(comunicazione.getAllegato()[i].getUuid() != null) {
					hashUuid.put(comunicazione.getAllegato()[i].getUuid(), new Long(i));
				}
			}
			
			// verifica quali documenti hanno uuid ed impostane la dimensione...
			for(int i = 0; i < allegatiBusta.sizeOfDocumentoArray(); i++) {
				String uuid = allegatiBusta.getDocumentoArray(i).getUuid();
				if(uuid == null) {
					// ***** fino alla 1.14.x *****

					// in questo caso allegatiBusta.getDocumentoArray(i).getFile() 
					// dovrebbe contenere lo stream binario dell'allegato
					if(allegatiBusta.getDocumentoArray(i).getDimensione() <= 0 && 
					   allegatiBusta.getDocumentoArray(i).getFile() != null) {
						// correggi eventuali bug di visualizzazione della dimensione...
						allegatiBusta.getDocumentoArray(i).setDimensione( allegatiBusta.getDocumentoArray(i).getFile().length );
					}
				} else {
					// ***** dalla 2.0.0 *****
					Long k = hashUuid.get(uuid);
					if(k != null && k >= 0) {
						allegatiBusta.getDocumentoArray(i).setDimensione(comunicazione.getAllegato(k.intValue()).getDimensione());						
//						byte[] f = null;
//						if( comunicazione.getAllegato(k.intValue()).getFile() != null ) {
//							f = Arrays.copyOf( comunicazione.getAllegato(k.intValue()).getFile(),
//										       comunicazione.getAllegato(k.intValue()).getFile().length );
//						}
//						allegatiBusta.getDocumentoArray(i).setFile(f);
					}
				}
			}
		}
	}

	/**
	 * scarica un allegato da una comunizione  
	 * 
	 * @throws ApsException 
	 */
	public static byte[] getAllegatoComunicazione(
			long idComunicazione, 
			String idDocumento) throws ApsException 
	{	
		byte[] contenuto = null;
		
		if(idComunicazione > 0 && idDocumento != null) {
			// richiedi lo stream del file al servizio...
			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils				
				.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER,
						 ServletActionContext.getRequest());
		
			ComunicazioneType comunicazione = comunicazioniManager.getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO, 
					idComunicazione, 
					idDocumento);
			
			// NB: la chiamata restituisce una comunicazione generica che 
			// ha come allegato (index=0) il documento corrispondente a 
			// idDocumento !!!
			if(comunicazione != null && 
			   comunicazione.getAllegato() != null && comunicazione.getAllegato().length > 0 &&
			   comunicazione.getAllegato(0).getFile() != null) {
				// recupera dalla comunicazione generica l'allegato del 
				// documento richiesto
				contenuto = comunicazione.getAllegato(0).getFile();
			}

			// informa esplicitamente il Garbage Collector che questa memoria 
			// non serve piu'!!!
			comunicazione = null;
			//System.gc();
		}	

		return contenuto;
	}
	
}
