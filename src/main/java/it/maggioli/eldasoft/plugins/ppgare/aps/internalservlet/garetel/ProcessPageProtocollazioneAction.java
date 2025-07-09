package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSAllegatoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.maggioli.eldasoft.digitaltimestamp.model.ITimeStampResult;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.JRPdfExporterEldasoft;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.MarcaturaTemporaleFileUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * classe generalizzata per la gestione di un flusso con eventuale protocollazione
 * 
 * ...
 */
public abstract class ProcessPageProtocollazioneAction extends AbstractProcessPageAction  
	implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5758134301929192821L;
	
	protected Map<String, Object> session;
	protected INtpManager ntpManager;
	protected IComunicazioniManager comunicazioniManager;
	protected IBandiManager bandiManager;
	protected IMailManager mailManager;
	protected IWSDMManager wsdmManager;
	protected IAppParamManager appParamManager;
	protected IEventManager eventManager;
	protected ICustomConfigManager customConfigManager;
	
	@Validate(EParamValidation.ERRORE)
	protected String msgErrore;
	protected Integer tipoProtocollazione;
	@Validate(EParamValidation.DIGIT)
	protected String numeroProtocollo;
	protected Long annoProtocollo;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	protected String dataProtocollo;
	@Validate(EParamValidation.GENERIC)
	private String codiceSistema;
	@Validate(EParamValidation.EMAIL)
	protected String mailUfficioProtocollo;
	protected boolean allegaDocMailUfficioProtocollo;
	@Validate(EParamValidation.CODICE)
	protected String stazioneAppaltanteProtocollante;
	@Validate(EParamValidation.CODICE)
	protected String codiceSA;
	protected DettaglioStazioneAppaltanteType dettaglioSA;
	protected Date dataInvio;
	protected Long idComunicazione;
	@Validate(EParamValidation.USERNAME)
	protected String username;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	protected WizardDatiImpresaHelper impresa;
	protected DocumentiAllegatiHelper allegatiNuovaComunicazione;
	// eventuali documenti aggiuntivi non presenti nella comunicazione che vanno aggiunti alla protocollazione (WSALLEGATI)
	protected AllegatoComunicazioneType[] allegatiAggiuntiviProtocollazioneComunicazione;
	protected boolean esisteFascicolo;
	@Validate(EParamValidation.CODICE)
	protected String codiceCig;
	protected StazioneAppaltanteBandoType stazioneAppaltante;
	protected List<DocumentazioneRichiestaType> documentiRichiesti;
//	@Validate(EParamValidation.GENERIC)
//	protected String wsDocumentoEntita = "GARE";	// NON SERVE PIU'
	@Validate(EParamValidation.GENERIC)
	protected String statoComunicazioneDaProtocollare = CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE;
	@Validate(EParamValidation.GENERIC)
	protected String statoComunicazioneDaProcessare	= CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE; 	
	private DettaglioGaraType dettaglioGara;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	protected String titoloPdfRiepilogoComunicazione;
	protected String descrizioneGenerePdfRiepilogoComunicazione;
		
	private static final String DESCRIZIONE_GENERE_GARA 				= "gara";
	private static final String DESCRIZIONE_GENERE_ELENCO 				= "elenco";
	private static final String DESCRIZIONE_GENERE_CATALOGO 			= "catalogo";
	private static final String DESCRIZIONE_GENERE_CONTRATTO 			= "contratto";
	private static final String DESCRIZIONE_GENERE_AVVISO				= "avviso";	
	
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setId(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}	
	
	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(String dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}
	
	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}
	
	public String getStazioneAppaltanteProtocollante() {
		return stazioneAppaltanteProtocollante;
	}

	public void setStazioneAppaltanteProtocollante(String stazioneAppaltanteProtocollante) {
		this.stazioneAppaltanteProtocollante = stazioneAppaltanteProtocollante;
	}
	
	public String getMsgErrore() {
		return msgErrore;
	}

	public void setMsgErrore(String msgErrore) {
		this.msgErrore = msgErrore;
	}
	
	public String getCodiceSistema() {
		return codiceSistema;
	}
	
	/**
	 * restituisce la label LABEL_RICHIESTA_CON_ID associata a LAPIS 
	 */
	public String getLABEL_RICHIESTA_CON_ID() {
		return MessageFormat.format(this.getI18nLabel("LABEL_RICHIESTA_CON_ID"), new Object[] {dataInvio, numeroProtocollo});
	}

	@Override
	public String next() {
		return null;
	}

	/**
	 * personalizzazione delle fasi di invio comunicazione/i e protocollazione
	 */ 
	//protected void setInfoPerProtocollazione(String codice) {...}								// metodo personalizabile
	//protected byte[] addComunicazionePdf(AllegatoComunicazioneType[] allegati) {...}			// metodo personalizabile
	//protected void fase1AggiornamentoComunicazione(ComunicazioneType comunicazione) {...}		// metodo personalizabile
	//protected void fase4SessionReset() {...}													// metodo personalizabile

	// i.e. return "soccorso istruttorio"
	protected abstract String getDescrizioneFunzione();
	
	/**
	 * personalizzazione comunicazione da inviare
	 */
	// metodo personalizabile, di default restituisce la ragione seociale dell'impresa
	//protected String getMittenteNuovaComunicazione() {...}
	
	// i.e. MessageFormat.format(this.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_OGGETTO"),
	//	 new Object[] { iscrizioneHelper.getIdBando() });
	protected abstract String getOggettoNuovaComunicazione();
	
	// i.e. MessageFormat.format(this.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_TESTO"),
	//	 new Object[] { StringUtils.left(ragioneSociale, 200), StringUtils.left(descBando, 200) });
	protected abstract String getTestoNuovaComunicazione();

	/**
	 * personalizzazione mail ufficio protocollo 
	 */
	// i.e. MessageFormat.format(this.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_OGGETTO"),
	//	 new Object[] { iscrizioneHelper.getIdBando() });
	protected abstract String getOggettoMailUfficioProtocollo();
	
	// i.e. MessageFormat.format(this.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_TESTO"),
	//	 new Object[] { StringUtils.left(ragioneSociale, 200), StringUtils.left(descBando, 200) });
	protected abstract String getTestoMailUfficioProtocollo();
	
	protected abstract void addAllegatiMailUfficioProtocollo(Map<String, byte[]> allegatiMail);
	
	/**
	 * personalizzazione mail conferma impresa
	 */
	// i.e. MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_INVCOM_RICEVUTA_OGGETTO"),
	//	 new Object[] { iscrizioneHelper.getIdBando() });
	protected abstract String getOggettoMailConfermaImpresa();
	
	// i.e. MessageFormat.format(...)
	protected abstract String getTestoMailConfermaImpresa();
 
	/**
	 * personalizzazione parametri per la creazione dell'oggetto WSDM
	 */
	protected abstract String getWSDMClassificaFascicolo();
	protected abstract String getWSDMTipoDocumento();
	protected abstract String getWSDMCodiceRegistro();
	protected abstract String getWSDMIdIndice();
	protected abstract String getWSDMIdTitolazione();
//	protected abstract String getWSDMDescrizione();		SERVE??? FORSE NO
	
	/**
	 * crea il contenuto dell'allegato "comunicazione.pdf" 
	 */ 
	protected String getComunicazionePdf() {
		return getComunicazionePdfDefault();
	}
		
	/**
	 * invia la richiesta di protocollazione
	 * 
	 * @param tipoComunicazione
	 * 			tipo della comunicazione da inviare (FS1, FS2, ..., FS11, FS12, ...)
	 * @param impresa
	 * 			dati dell'impresa
	 * @param documenti
	 * 			elenco dei documenti da inviare alla protocollazione
	 * @param documentiRichiesti
	 * 			(opzionale) elenco degli eventuali documenti richiesti definiti in BO
	 * @param codice
	 * 			codice della gara, elenco, catalogo, ...
	 * @param dataScadenzaRichiesta
	 * 			(opzionale) termine massimo per accettare l'invio della protocollazione
	 * @param stazioneAppaltanteProcedura
	 *			(opzionale) codice della stazione appaltante di default
	 */
	public String send(
			String tipoComunicazione,
			Long idComunicazioneBozza,
			WizardDatiImpresaHelper impresa,
			DocumentiAllegatiHelper documenti,
			List<DocumentazioneRichiestaType> documentiRichiesti,
			String codice,
			Date dataScadenzaRichiesta,
			String stazioneAppaltanteProcedura,
			String entita) 
		throws ApsException 
	{
		this.setTarget(SUCCESS);
		
		boolean continua = true;
		
		this.codiceSistema = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);									
		this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
		this.allegaDocMailUfficioProtocollo = (Boolean) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
		this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
		this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		
		this.idComunicazione = idComunicazioneBozza;
		this.impresa = impresa;
		this.allegatiNuovaComunicazione = documenti;
		this.codiceSA = null;
		this.dettaglioSA = null; 
		this.codice = codice;
		this.username = this.getCurrentUser().getUsername();
		this.documentiRichiesti = documentiRichiesti;
		this.allegatiAggiuntiviProtocollazioneComunicazione = null;
		this.esisteFascicolo = false;
		entita = (StringUtils.isEmpty(entita) ? "GARE": entita);	// il default e' GARE
		
		// calcola la data e ora corrente...
		this.dataInvio = this.retrieveDataNTP();
		if (this.dataInvio == null) {
			continua = false;
			this.setTarget(INPUT);
		}
		
		// verifica se la richiesta e' oltre la data di scadenza
		if(dataScadenzaRichiesta != null && this.dataInvio.after(dataScadenzaRichiesta) ) {
			// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
			continua = false;
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
			
			Event evento = new Event();
			evento.setUsername(this.username);
			evento.setLevel(Event.Level.ERROR);
			evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + 
									" (" + UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
			evento.setDestination(this.codice);
			evento.setMessage("Accesso alla funzione " + this.getDescrizioneFunzione() + " (comunicazione con id " + this.idComunicazione + ")");
			this.eventManager.insertEvent(evento);
		}
		
		// lo stato di un flusso di protocollazione e' generalmente di 2 tipi:
		// a) 1 -> 5		senza protocollazione
		// b) 1 -> 9 -> 5	in caso di protocollazione (via mail o via WSDM)  
		ComunicazioneType nuovaComunicazione = new ComunicazioneType();
		Event evento = null;
			
		if(continua) {
			this.setInfoPerProtocollazione(this.codice);
			
			this.dettaglioSA = null;
			if(StringUtils.isNotEmpty(this.codiceSA)) {
				this.dettaglioSA = this.bandiManager.getDettaglioStazioneAppaltante(this.codiceSA);
			}
			
			// imposta la stazione appaltante per recuperare i parametri
			this.appParamManager.setStazioneAppaltanteProtocollazione(this.codiceSA);
			
			this.tipoProtocollazione =
					dettaglioGara != null
					&& dettaglioGara.getDatiGeneraliGara() != null
					&& "10".equals(dettaglioGara.getDatiGeneraliGara().getIterGara())
						? new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA)
						: this.appParamManager.getTipoProtocollazione(this.codiceSA);

			// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
			if(this.appParamManager.isConfigWSDMNonDisponibile()) {
				continua = false;
				this.setTarget(INPUT);
				String msgerr = this.getText("Errors.wsdm.configNotAvailable");
				this.addActionError(msgerr);
				Event event = new Event();
				event.setUsername(this.username);
				event.setDestination(this.codice);
				event.setLevel(Event.Level.ERROR);
				event.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				event.setIpAddress(this.getCurrentUser().getIpAddress());
				event.setSessionId(this.getCurrentUser().getSessionId());
				event.setMessage("Configurazione non disponibile o vuota");
				event.setDetailMessage(msgerr);
				this.eventManager.insertEvent(event);
			}
			
			String oggettoComunicazione = this.getOggettoNuovaComunicazione();
			if(oggettoComunicazione.length() > 300) {
				oggettoComunicazione = StringUtils.left(oggettoComunicazione, 300);
			}
			
			// crea la comunicazione per la gestione del flusso   
			DettaglioComunicazioneType dettComunicazione = null;
			if(this.idComunicazione != null && this.idComunicazione.longValue() > 0) {
				// se idComunicazione e' valorizzato esiste gia' la comunicazione
				// in stato BOZZA per il flusso...
				nuovaComunicazione = this.comunicazioniManager.getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO,
						this.idComunicazione);
				dettComunicazione = nuovaComunicazione.getDettaglioComunicazione();
			} else {
				// crea una nuova comunicazione in stato BOZZA...
				dettComunicazione = ComunicazioniUtilities.createDettaglioComunicazione(
					null, 
					this.username, 
					this.codice, 
					null,
					this.getMittenteNuovaComunicazione(),
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
					oggettoComunicazione,
					this.getTestoNuovaComunicazione(),
					tipoComunicazione,
					this.dataInvio);
				if(StringUtils.isNotEmpty(entita)) {
					dettComunicazione.setEntita(entita);
				}
			}
			
			nuovaComunicazione.setDettaglioComunicazione(dettComunicazione);

			evento = ComunicazioniUtilities.createEventSendComunicazione(
					this.username, 
					this.codice, 
					nuovaComunicazione,
					this.dataInvio,
					this.getCurrentUser().getIpAddress(),
					this.getRequest().getSession().getId());

			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			
			if(continua) {
				evento = ComunicazioniUtilities.createEventSendComunicazione(
						this.username, 
						this.codice, 
						nuovaComunicazione,
						this.dataInvio,
						this.getCurrentUser().getIpAddress(),
						this.getRequest().getSession().getId());
				
				// FASE 1: invio della comunicazione 
				//
				try {
					synchronized (this)	{
						if(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA != this.tipoProtocollazione.intValue() 
						   && this.codiceSA == null) 
						{
							// aggiorno lo stato per creare l'evento con l'indicazione
							// corretta dello stato, il cui evento conterra' il
							// messaggio di dettaglio con l'errore emesso nell'eccezione
							dettComunicazione.setStato(this.statoComunicazioneDaProtocollare);
							evento = ComunicazioniUtilities.createEventSendComunicazione(
									this.username, 
									this.codice, 
									nuovaComunicazione,
									this.dataInvio,
									this.getCurrentUser().getIpAddress(),
									this.getRequest().getSession().getId());
							
							// questa eccezione blocca il caso di affidamenti diretti non
							// pubblicati, per i quali e' prevista la protocollazione: vanno
							// per forza pubblicati sul portale altrimenti non si estrae il
							// dettaglio della gara e non e' possibile verificare se per la
							// stazione appaltante va applicata o meno la protocollazione
							throw new ApsException(
									"Bloccato invio comunicazione per operatore " + this.username
									+ " e risposta alla comunicazione con id " + this.idComunicazione
									+ " causa stazione appaltante non impostata o procedura non pubblicata sul portale e protocollazione comunicazione richiesta");
						} 
//						else if ( !this.isProtocollazionePrevista(this.stazioneAppaltanteProtocollante, this.codiceSA) ) 
//						{
//							// se la protocollazione non e' prevista, 
//							// si resetta il tipo protocollazione cosi' in seguito il codice testa su un'unica condizione
//							this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
//						}
	
						// aggiorna lo stato del flusso (1 -> 9 oppure 1 -> 5)
						if(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA != this.tipoProtocollazione.intValue()) {
							// via mail o via WSDM
							dettComunicazione.setStato(this.statoComunicazioneDaProtocollare);
						} else {
							// nessuna protocollazione
							dettComunicazione.setStato(this.statoComunicazioneDaProcessare);
						}
	
						// Creazione evento di invio della comunicazione in base al nuovo stato del flusso...
						evento = ComunicazioniUtilities.createEventSendComunicazione(
								this.username, 
								this.codice, 
								nuovaComunicazione,
								this.dataInvio,
								this.getCurrentUser().getIpAddress(),
								this.getRequest().getSession().getId());
	
						this.setAllegatoComunicazione(nuovaComunicazione);
						
						// eventuali operazioni aggiuntive come ad esempio comunicazioni associate 
						// (ie. per la FS11 -> gestione stati delle comunicazioni per buste FS11A,B,C, ...)
						this.fase1AggiornamentoComunicazione(nuovaComunicazione);
						
						// fino ad ora si utilizza this.codice (che dovrebbe sempre arrivare valorizzato), 
						// ma se arriva non valorizzato, una volta inizializzati gli oggetti
						// per l'invio si procede al controllo e lo si blocca, 
						// mentre la tracciatura dell'evento risulta completa di dati
						if (this.codice == null) {
							throw new ApsException(
									"Bloccato invio comunicazione per operatore " + this.username + 
									" e risposta alla comunicazione con id " + this.idComunicazione + 
									" per codice procedura vuoto");
						}

						nuovaComunicazione.getDettaglioComunicazione().setId(this.comunicazioniManager.sendComunicazione(nuovaComunicazione));
						this.idComunicazione = nuovaComunicazione.getDettaglioComunicazione().getId();
						
						// si rigenera l'evento perche' nel caso di esito positivo si
						// arricchisce il messaggio con l'id della comunicazione 
						// (prima dell'invio e' null)
						
						// resetta lo stato dei documenti inviati con la comunicazione...
						if(this.allegatiNuovaComunicazione != null) {
							this.allegatiNuovaComunicazione.resetStatiInvio(nuovaComunicazione);
						}
						
						inviataComunicazione = true;
					}
				} catch (Throwable e) {
					ApsSystemUtils.logThrowable(e, this, "send");
					ExceptionUtils.manageExceptionError(e, this);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					evento.setError(e);
				} finally {
					if(evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}
			}
			
			// FASE 2: ove previsto, si invia alla protocollazione
			//
			if (inviataComunicazione) {
				// recupero l'id della comunicazione
				evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				switch (this.tipoProtocollazione) {

				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
					evento.setMessage("Protocollazione via mail a " + this.mailUfficioProtocollo);

					boolean mailProtocollazioneInviata = false;
					try {
						// si invia la richiesta di protocollazione via mail
						this.sendMailUfficioProtocollo(); 
						
						mailProtocollazioneInviata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a "5"
						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() 
										  + " allo stato " + this.statoComunicazioneDaProcessare);
						this.comunicazioniManager.updateStatoComunicazioni(
								new DettaglioComunicazioneType[] { nuovaComunicazione.getDettaglioComunicazione() }, 
								this.statoComunicazioneDaProcessare);
						this.eventManager.insertEvent(evento);
						
						this.dataProtocollo = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
						
					} catch (Throwable t) {
						evento.setError(t);
						this.eventManager.insertEvent(evento);
						if (mailProtocollazioneInviata) {
							// segnalo l'errore, 
							// comunque considero la protocollazione andata a buon fine 
							// e segnalo nel log e a video che va aggiornato a mano lo stato
							// della comunicazione
							this.msgErrore = this.getTextFromDefaultLocale(
									"Errors.updateStatoComunicazioneDaProcessare",
									nuovaComunicazione.getDettaglioComunicazione().getId().toString());
							ApsSystemUtils.logThrowable(t, this, "send", this.msgErrore);
							
							this.msgErrore = this.getText("Errors.updateStatoComunicazioneDaProcessare",
									new String[] { nuovaComunicazione.getDettaglioComunicazione().getId().toString() });
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
							evento.resetDetailMessage();
							this.eventManager.insertEvent(evento);
							
							this.dataProtocollo = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
							
						} else {
							ApsSystemUtils.logThrowable(t, this, "send", this.getText("Errors.sendMailError"));
							ExceptionUtils.manageExceptionError(t, this);
							this.setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di protocollare
							this.eliminaComunicazioneInviata(
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
					this.idComunicazione = nuovaComunicazione.getDettaglioComunicazione().getId().longValue();

					try {
						WSDMLoginAttrType loginAttr = this.wsdmManager.getLoginAttr();
						FascicoloProtocolloType fascicoloBackOffice = this.getFascicoloProtocollo();
						
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
						
						// rilegge la comunicazione in modo da ottenere gli id degli allegati
						// per poterli inserire nel documento da protocollare
						nuovaComunicazione = this.comunicazioniManager.getComunicazione(
								nuovaComunicazione.getDettaglioComunicazione().getApplicativo(), 
								nuovaComunicazione.getDettaglioComunicazione().getId());

						// crea il documento di protocollo da inviare al servizio WSDM
						WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = this.creaInputProtocollazioneWSDM(
								nuovaComunicazione, 
								fascicoloBackOffice);
								
						ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						
						// recupera la risposta del servizio WSDM
						this.annoProtocollo = ris.getAnnoProtocollo();
						this.numeroProtocollo = ris.getNumeroProtocollo();
						if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
							this.numeroProtocollo = ris.getGenericS11();
						}
						if (ris.getDataProtocollo() != null) {
							this.setDataProtocollo(
									UtilityDate.convertiData(ris.getDataProtocollo().getTime(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						}
						protocollazioneInoltrata = true;
						
						this.eventManager.insertEvent(evento);
						
						// si aggiorna lo stato a 3 aggiornando inoltre anche i dati di protocollazione						//
						this.idComunicazione = nuovaComunicazione.getDettaglioComunicazione().getId().longValue();

						// prepara il documento per protocollare la comunicazione (WSDOCUMENTO)
						documento = this.initWSDocumentoProtocollo(
								entita, 
								this.codice,
								wsdmProtocolloDocumentoIn.getOggetto(),
								ris);
						
						// calcola il numero di allegati da inviare alla protocollazione (WSALLEGATI)
						// conteggia solo i documenti allegati alla comunicazione 
						// ed ignora il riepilogo "comunicazione.pdf"...
						int n = 0;
						if(this.allegatiNuovaComunicazione != null) {
							n = n + (this.allegatiNuovaComunicazione.getAdditionalDocs() != null 
								     ? this.allegatiNuovaComunicazione.getAdditionalDocs().size()
								     : 0)
								  + (this.allegatiNuovaComunicazione.getRequiredDocs() != null 
								     ? this.allegatiNuovaComunicazione.getRequiredDocs().size()
								     : 0);
						}
						
						// conteggia eventuali documenti non presenti tra gli allegati della comunicazione 
						// che vanno comunque protocollati   
						// 	   aggiungili all'elenco di quelli da protocollare (WSDOCUMENTI, WSALLEGATI)...
						int m = (this.allegatiAggiuntiviProtocollazioneComunicazione != null 
								 ? this.allegatiAggiuntiviProtocollazioneComunicazione.length 
								 : 0);
						
						// prepara l'elenco degli allegati per il protocollo...
						WSAllegatoType[] allegati = new WSAllegatoType[1 + n + m];
						allegati[0] = new WSAllegatoType();
						allegati[0].setEntita("W_INVCOM");
						allegati[0].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
						allegati[0].setChiave2(nuovaComunicazione.getDettaglioComunicazione().getId().toString());
						
						// aggiungi alla protocollazione gli allegati della comunicazione...
						int i = 1;
						for (int j = 0; j < n; j++) {
							allegati[i] = new WSAllegatoType();
							allegati[i].setEntita("W_DOCDIG");
							allegati[i].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
							allegati[i].setChiave2(nuovaComunicazione.getAllegato(j).getId().toString());
							i++;
						}
						
						// aggiungi alla protocollazione gli allegati NON presenti nella comunicazione (WSALLEGATI)...
						if(this.allegatiAggiuntiviProtocollazioneComunicazione != null) {
							for (int j = 0; j < m; j++) {
								String applicativo = (isRiepilogoComunicazione(this.allegatiAggiuntiviProtocollazioneComunicazione[j].getNomeFile())
											   		  ? nuovaComunicazione.getDettaglioComunicazione().getApplicativo()
											   		  : CommonSystemConstants.ID_APPLICATIVO_GARE);
								allegati[i] = new WSAllegatoType();
								allegati[i].setEntita("W_DOCDIG");
								allegati[i].setChiave1(applicativo);
								allegati[i].setChiave2(this.allegatiAggiuntiviProtocollazioneComunicazione[j].getId().toString());
								i++;
							}
						}
						
						// aggiorna l'evento 
						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + this.idComunicazione
								+ " allo stato " + this.statoComunicazioneDaProcessare
								+ ", protocollata con anno " + ris.getAnnoProtocollo() 
								+ " e numero " + ris.getNumeroProtocollo());
						
						// LAPISOPERA: inserisci l'ID richiesta restituito dal protocollo nella W_INVCOM e aggiorna il messaggio dell'evento
						lapisAggiornaComunicazione(
								ris,
								documento,
								nuovaComunicazione.getDettaglioComunicazione().getId(),
								this.appParamManager,
								evento);

						this.comunicazioniManager.protocollaComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								this.idComunicazione,
								ris.getNumeroProtocollo(),
								(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataInvio),
								this.statoComunicazioneDaProcessare,
								documento,
								allegati);
						
						this.eventManager.insertEvent(evento);
					} catch (Throwable t) {
						evento.setError(t);
						this.eventManager.insertEvent(evento);
						if (protocollazioneInoltrata) {

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							this.msgErrore = this.getTextFromDefaultLocale(
									"Errors.updateStatoComunicazioneDaProcessare",
									this.idComunicazione);
							ApsSystemUtils.logThrowable(t, this, "invio", this.msgErrore);
							this.msgErrore = this.getText(
									"Errors.updateStatoComunicazioneDaProcessare",
									new String[] { this.idComunicazione.toString() });
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + this.idComunicazione
											  + " ed inserire inoltre un documento in ingresso per entita' " + documento.getEntita()
											  + ", chiave1 " + documento.getChiave1()
											  + ", oggetto " + documento.getOggetto()
											  + ", numero documento " + documento.getNumeroDocumento()
											  + ", anno protocollo " + documento.getAnnoProtocollo()
											  + " e numero protocollo " + documento.getNumeroProtocollo()
											  + " ed un allegato per la comunicazione ed uno per ogni documento effettivamente allegato");
							lapisAggiornaMessaggioEventoCompletamentoManuale(
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
									this,
									"invio",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							this.msgErrore = this.getText("Errors.service.wsdmHandshake");
							ExceptionUtils.manageWSDMExceptionError(t, this);
							this.setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di protocollare
							this.eliminaComunicazioneInviata(
									CommonSystemConstants.ID_APPLICATIVO, 
									nuovaComunicazione.getDettaglioComunicazione().getId());
							protocollazioneOk = false;
						}
					}
					break;
				default:
					// qualsiasi altro caso: non si protocolla nulla
					break;
				}
			}

			// FASE 3: se gli step precedenti sono andati a buon fine, si invia
			// la ricevuta all'impresa
			if (inviataComunicazione && protocollazioneOk) {
				evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage("Invio mail ricevuta di conferma trasmissione comunicazione " 
								  + nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione()
								  + " a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				try {
					this.sendMailConfermaImpresa();
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, " +
							"non e' stato possibile inviare all'impresa " + this.impresa.getDatiPrincipaliImpresa().getRagioneSociale() +
							" la ricevuta della richiesta di per il codice " + this.codice + ".",
							new Object[] { this.getCurrentUser().getUsername(), this.codice});
					this.msgErrore = this.getText("Errors.sendMailError");
					ApsSystemUtils.logThrowable(t, this, "invio");
					evento.setError(t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}

			// FASE 4: se invio e protocollazione sono andate a buon fine, anche
			// se la ricevuta
			// all'impresa non e' stata inviata, si procede con la pulizia della
			// sessione
			if (inviataComunicazione && protocollazioneOk) {
				this.setTarget(SUCCESS);
				this.fase4SessionReset();
			}
			
			// concludi la protocollazione
			this.appParamManager.setStazioneAppaltanteProtocollazione(null);
		}
		return this.getTarget();
	}

	private boolean isRiepilogoComunicazione(String filename) { 
		return PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF.equalsIgnoreCase(filename) ||
				PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD.equalsIgnoreCase(filename);
	}

	/**
	 * metodo vuoto utilizzabile per la definizione di azioni aggiuntive
	 * prima dell'invio della nuova comunicazione 
	 */
	protected void fase1AggiornamentoComunicazione(ComunicazioneType comunicazione) {
		// metodo personalizabile
		// il comportamento di default e' vuoto
	}
	
	/**
	 * metodo vuoto utilizzabile per la pulizia della sessione al termine 
	 * dell'invio e della protocollazione del flusso
	 */
	protected void fase4SessionReset() {
		// metodo personalizabile
		// il comportamento di default e' vuoto
	}
	
	/**
	 * Annulla gli effetti della comunicazione inviata eliminandola
	 * 
	 * @param idComunicazione
	 *            comunicazione da eliminare
	 */
	private void eliminaComunicazioneInviata(
			String idApplicativo, 
			Long idComunicazione) 
	{
		Event evento = new Event();
		evento.setUsername(this.getCurrentUser().getUsername());
		evento.setDestination(this.codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Annullamento comunicazione con id " + idComunicazione);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		
		try {
			this.comunicazioniManager.deleteComunicazione(idApplicativo, idComunicazione);
		} catch (Throwable e) {
			this.msgErrore = this.getTextFromDefaultLocale(
					"Errors.deleteComunicazione",
					idComunicazione.toString());
			ApsSystemUtils.logThrowable(e, this, "delete", this.msgErrore);
			ExceptionUtils.manageExceptionError(e, this);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * Invia la mail di notifica all'ufficio protocollo, contenente un allegato
	 * con il testo della comunicazione inviata e gli allegati effettivamente
	 * inseriti da interfaccia.<br/>
	 * <b>Attenzione: in questa casistica si allegano sempre i documenti
	 * indipendentemente dalla configurazione (una comunicazione non ha senso se
	 * non si forniscono assieme gli allegati alla stessa)</b>
	 * 
	 * @throws ApsException
	 * 
	 */
	private void sendMailUfficioProtocollo() throws Exception {
		try {
			if (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL == this.tipoProtocollazione) {
				if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
					throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
				}
	
				// nel caso di email dell'ufficio protocollo, si
				// procede con l'invio della notifica a tale ufficio
				
				// si predispongono gli allegati da inserire
				Map<String, byte[]> p = new HashMap<String, byte[]>();
				
				String testoComunicazione = this.getTestoNuovaComunicazione();
				p.put("comunicazione.txt", testoComunicazione.getBytes());
				
				// solo se indicato da configurazione si allegano i doc inseriti nella domanda
				if (this.allegaDocMailUfficioProtocollo) {
					this.addAllegatiMailUfficioProtocollo(p);
				}
				
//				String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
//				String codFiscale = this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale();
//				String partitaIVA = this.impresa.getDatiPrincipaliImpresa().getPartitaIVA();
//				String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
//				String sede = this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
//						+ " " + this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()
//						+ ", " + this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale()
//						+ " " + this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale()
//						+ " (" + this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")";
//				String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
				
				String subject = this.getOggettoMailUfficioProtocollo();
				String text = this.getTestoMailUfficioProtocollo(); 
				String[] destinatari = this.mailUfficioProtocollo.split(",");
	
				this.mailManager.sendMail(
						text, 
						subject,
						IMailManager.CONTENTTYPE_TEXT_PLAIN, 
						p, 
						destinatari, 
						null,
						null, 
						CommonSystemConstants.SENDER_CODE);
			}
		} catch (Exception e) {
			throw new ApsSystemException(e.getMessage());
		} finally {
			//...
		}
	}

	/**
	 * Invia la mail di ricevuta all'operatore economico, indicando
	 * eventualmente i riferimenti della protocollazione ove prevista.
	 * 
	 * @throws ApsSystemException
	 */
	private void sendMailConfermaImpresa() throws ApsSystemException {
		String destinatario = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
		String text = this.getTestoMailConfermaImpresa();
		String subject = this.getOggettoMailConfermaImpresa();
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { destinatario },
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}
	
	/**
	 * Crea e popola il contenitore per richiedere la protocollazione mediante WSDM
	 * 
	 * @throws Exception 
	 */
	private WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			ComunicazioneType comunicazione,
			FascicoloProtocolloType fascicoloBackOffice) throws Exception 
	{
		// ATTENZIONE:
		// L'UTILIZZO DEL CAST A (INTEGER) SUL METODO appParamManager.getConfigurationValue(...)
		// E' DEPRECATO, IN QUANTO NON E' SEMPRE VERO CHE IN PPCOMMON_PROPERTIES ESISTA
		// LA DEFINIZIONE DEL TIPO DI DATO DA RESTITUIRE (INT, BOOLEAN, STRING)
		// TUTTI I NUOVI PARAMETRI ANDREBBERO CONSIDERATI SEMPRE COME STRINGHE
		this.esisteFascicolo = (fascicoloBackOffice != null);
		this.codiceSistema = (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
		Integer cfMittente = (Integer)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE);
		boolean usaCodiceFiscaleMittente = (cfMittente != null ? 1 == cfMittente : true);
		String channelCode = (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE);
		String sottoTipo = (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SOTTOTIPO_COMUNICAZIONE);
		String mezzo = (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO);
		String rup = (this.stazioneAppaltante != null ? this.stazioneAppaltante.getRup() : null);
		String acronimoRup = ProcessPageProtocollazioneAction.getAcronimoSoggetto(rup);

		String classificaFascicolo = this.getWSDMClassificaFascicolo();
		String tipoDocumento = this.getWSDMTipoDocumento();
		String codiceRegistro = this.getWSDMCodiceRegistro();
		String idIndice = this.getWSDMIdIndice();
		
		String codiceFascicolo = null;
		Long annoFascicolo = null;
		String numeroFascicolo = null;
		String idUnitaOperDestinataria = null;
		String idTitolazione = null;
		boolean riservatezzaFascicolo = false;
		
		if (this.esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			// si sovrascrive la classifica, quindi si da' priorita' a quella
			// indicata nel fascicolo
			classificaFascicolo = fascicoloBackOffice.getClassifica();
			riservatezzaFascicolo = (fascicoloBackOffice.getRiservatezza() != null ? fascicoloBackOffice.getRiservatezza() : false);
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
//			// si legge la classifica presa dalla configurazione 
//			// solo se non esiste il fascicolo
//			classificaFascicolo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);
			idTitolazione = this.getWSDMIdTitolazione();
		}

		idUnitaOperDestinataria = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA e' vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}
		
//		if (this.genere == 10 || this.genere == 20 || this.genere == 11) {
//			// ELENCO, CATALOGO, AVVISI
//		} else {
//			// GARE, ODA
//			idIndice = (String)this.appParamManager
//				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);
//		}
		
		Calendar data = new GregorianCalendar();
		data.setTimeInMillis(this.dataInvio.getTime());
		Calendar dataArrivo = Calendar.getInstance();
	    dataArrivo.setTimeInMillis(System.currentTimeMillis());
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String codiceFiscale = this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIva = this.impresa.getDatiPrincipaliImpresa().getPartitaIVA();
		String indirizzo = this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
						   + " " + this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale();
		
		String tipoDocInviaComunicazione = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO_INVIA_COMUNICAZIONE);
		if(tipoDocInviaComunicazione != null && !"".equals(tipoDocInviaComunicazione)){
			tipoDocumento = tipoDocInviaComunicazione;
		}
		
		String oggetto = this.getOggettoMailUfficioProtocollo();  //comunicazione.getDettaglioComunicazione().getOggetto());
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.codice + " - " + oggetto;
		}
		
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}

		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);
		wsdmProtocolloDocumentoIn.setChannelCode(channelCode);
		wsdmProtocolloDocumentoIn.setTipoDocumento(tipoDocumento);
		wsdmProtocolloDocumentoIn.setData(data);
		wsdmProtocolloDocumentoIn.setOggetto(oggetto); 
		wsdmProtocolloDocumentoIn.setDescrizione(this.getTestoMailUfficioProtocollo()); //comunicazione.getDettaglioComunicazione().getTesto());
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceRegistro);
		wsdmProtocolloDocumentoIn.setCig(this.codiceCig);
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(this.codiceSA);
		wsdmProtocolloDocumentoIn.setMezzo(mezzo);

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
	
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId());
		
		// servono per Archiflow / FOLIUM
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(this.codiceSA);
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + this.codice);
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(this.codice);
		}
		
		
		// serve per JIride*
		// NB: per le FS12 servono i parametri della "riservatezza" ?
		if(riservatezzaFascicolo) {
			// BO: riservatezza = SI
			String livelloRiservatezza = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_LIVELLO_RISERVATEZZA);
			
			if (StringUtils.isNotEmpty(livelloRiservatezza)) {
				// se ISRISERVA == SI
				// allora si invia la property nel campo gia' valorizzato 
				// e non si valorizza piu' la data 
				wsdmProtocolloDocumentoIn.setLivelloRiservatezza(livelloRiservatezza);
//				Date dataTermine = getDataTermine(dettGara, this.operazione);
//				Calendar calDataTermineRiservatezza = Calendar.getInstance();
//				calDataTermineRiservatezza.setTime(dataTermine);
//				wsdmProtocolloDocumentoIn.setDataFineRiservatezza(calDataTermineRiservatezza);
			} else {
				throw new ApsSystemException("Valorizzare la configurazione " + 
						  AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_LIVELLO_RISERVATEZZA);
			}
		} else {
			// BO: riservatezza = NO
			// nessuna info da aggiungere alla richiesta inviata al WSDM ! 
		}
		
		// inserisci il Mittente 
		wsdmProtocolloDocumentoIn.setMittenteInterno((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MITTENTE_INTERNO));
		if (IWSDMManager.CODICE_SISTEMA_JIRIDE.equals(codiceSistema)
			&& esisteFascicolo
			&& StringUtils.isNotEmpty(fascicoloBackOffice.getStrutturaCompetente())) 
		{
			// solo per JIRIDE valorizzo il mittente interno con la struttura del fascicolo, se valorizzata
			wsdmProtocolloDocumentoIn.setMittenteInterno(fascicoloBackOffice.getStrutturaCompetente());
		}

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
		mittenti[0].setIndirizzoResidenza(indirizzo);
		mittenti[0].setLocalitaResidenza(this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
		mittenti[0].setComuneResidenza(this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
		mittenti[0].setNazionalita(this.impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
	    mittenti[0].setMezzo(mezzo);	    
	    String email = DatiImpresaChecker.getEmailRiferimento(this.impresa.getDatiPrincipaliImpresa().getEmailPECRecapito(), 
	    													  this.impresa.getDatiPrincipaliImpresa().getEmailRecapito());
	    mittenti[0].setEmail(email);
	    mittenti[0].setProvinciaResidenza(this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale());
	    mittenti[0].setCapResidenza(this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale());
		wsdmProtocolloDocumentoIn.setMittenti(mittenti);
		
		// inserimento in fascicolo
		if (this.esisteFascicolo) {
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

		// prepara N + 1 allegati da inviare al protocollo
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		// Default: inserisci in coda
		String v = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
		boolean inTesta = (v != null && "1".equals(v));
	
		WSDMProtocolloAllegatoType[] allegati = createAttachmentsWSDM(comunicazione, inTesta);
		
		// inserisci gli allegati nella richiesta di protocollazione... 
		wsdmProtocolloDocumentoIn.setAllegati(allegati);
		wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(allegati.length - 1));
		
		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)){
			wsdmProtocolloDocumentoIn.setOggetto(
					this.codice + "-" + 
					wsdmProtocolloDocumentoIn.getOggetto() + "-" + 
					(this.dettaglioGara == null ? "" : this.dettaglioGara.getDatiGeneraliGara().getOggetto()) );
		}
			    
	    wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
	    wsdmProtocolloDocumentoIn.setSupporto((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));
	    		
		// se specificata in configurazione la uso, altrimenti riutilizzo la
		// configurazione presente nel fascicolo di backoffice (cos? avveniva
		// per ARCHIFLOW)
	    String struttura = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STRUTTURA);
		if (StringUtils.isNotEmpty(struttura)) {
			wsdmProtocolloDocumentoIn.setStruttura(struttura);
		} else if (this.esisteFascicolo) {
	    	wsdmProtocolloDocumentoIn.setStruttura(fascicoloBackOffice.getStrutturaCompetente());
	    }
		
		// se specificata in configurazione la uso (JPROTOCOL)
		String tipoAssegnazione = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPOASSEGNAZIONE);
		if (StringUtils.isNotEmpty(tipoAssegnazione)) {
			wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoAssegnazione);
		}
		
		return wsdmProtocolloDocumentoIn;
	}

	/**
	 * crea l'elenco degli allegati da inviare al WSDM
	 */
	protected WSDMProtocolloAllegatoType[] createAttachmentsWSDM(
			ComunicazioneType comunicazione, 
			boolean inTesta)
		throws ApsException 
	{
		// crea l'elenco degli allegati per la protocollazione
		int n1 = comunicazione.getAllegato().length;
		int n2 = (this.allegatiAggiuntiviProtocollazioneComunicazione != null 
				  ? this.allegatiAggiuntiviProtocollazioneComunicazione.length 
				  : 0);
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[n1 + n2];
		
		int n = 0;
		int posComunicazionePdf = allegati.length - 1;
		if(inTesta) {
			n++;
			posComunicazionePdf = 0;
		}

		// ...eventuali allegati della comunicazione
		// l'allegato con il testo della comunicazione "comunicazione.pdf" 
		// viene inserito in seguito perche' deve rispettare 
		// l'eventuale ordine voluto dal protocollo (in testa o in coda)
		int iComunicazionePdf = -1;
		for (int i = 0; i < comunicazione.getAllegato().length; i++) {
			if(isRiepilogoComunicazione(comunicazione.getAllegato(i).getNomeFile())) {
				// trovato l'allegato "comunicazione.pdf" o "comunicazione.pdf.tsd"
				iComunicazionePdf = i;
			} else {
				allegati[n] = new WSDMProtocolloAllegatoType();
				allegati[n].setTitolo(comunicazione.getAllegato(i).getDescrizione());
				allegati[n].setNome(comunicazione.getAllegato(i).getNomeFile());
				allegati[n].setTipo(StringUtils.substringAfterLast(comunicazione.getAllegato(i).getNomeFile(), "."));
				allegati[n].setContenuto(comunicazione.getAllegato(i).getFile());
				// serve per Titulus
				allegati[n].setIdAllegato("W_INVCOM/"
						+ CommonSystemConstants.ID_APPLICATIVO + "/"
						+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
				n++;
			}
		}
		
		// se sono presenti dei documenti che non fanno parte della comunicazione
		// aggiungili all'elenco degli allegati da protocollare...
		if(this.allegatiAggiuntiviProtocollazioneComunicazione != null) {
			logger.debug("ProcessPageProtocollazioneAction.createAttachmentsWSDM() aggiungi allegati aggiuntivi per protocollazione");
			for (int i = 0; i < this.allegatiAggiuntiviProtocollazioneComunicazione.length; i++) {
				// NB: in fase di registrazione al protocollo (protocollaComunicazione), 
				//     e' presente tra gli allegati della comunicazione un allegato "comunicazione.pdf" (aggiungo qui sotto)
				//     e se risulta presente anche in "allegatiAggiuntiviProtocollazioneComunicazione" verrebbe aggiunto 2 volte, 
				//     ecco perche' in questo ciclo non deve essere considerato 
				if(iComunicazionePdf >= 0 &&
				   isRiepilogoComunicazione(this.allegatiAggiuntiviProtocollazioneComunicazione[i].getNomeFile())) 
				{
					logger.warn("ProcessPageProtocollazioneAction.createAttachmentsWSDM() '" + this.allegatiAggiuntiviProtocollazioneComunicazione[i].getNomeFile() + "' " +
								"ignorato in questa fase; verra' riutilizzato in fase di protocollazione comunicazione");
					continue;
				}
				
				allegati[n] = new WSDMProtocolloAllegatoType();
				allegati[n].setTitolo(this.allegatiAggiuntiviProtocollazioneComunicazione[i].getDescrizione());
				allegati[n].setNome(this.allegatiAggiuntiviProtocollazioneComunicazione[i].getNomeFile());
				allegati[n].setTipo(StringUtils.substringAfterLast(this.allegatiAggiuntiviProtocollazioneComunicazione[i].getNomeFile(), "."));
				allegati[n].setContenuto(this.allegatiAggiuntiviProtocollazioneComunicazione[i].getFile());
				// serve per Titulus
				allegati[n].setIdAllegato("W_INVCOM/"
						+ CommonSystemConstants.ID_APPLICATIVO + "/"
						+ this.allegatiAggiuntiviProtocollazioneComunicazione[i].getId() + "/" + n);
				n++;
			}
		}		
		
		// aggiungi l'allegato "comunicazione.pdf" o "comunicazione.pdf.tsd"
		// agli allegati da inviare al protocollo
		if(iComunicazionePdf < 0) {
			// NON DOVREBBE MAI ACCADERE !!!
			throw new ApsSystemException("Allegato \"Comunicazione.pdf\" o \"Comunicazione.pdf.tsd\" non trovato.");
		} else {
			String tipo = (PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD
					.equalsIgnoreCase(comunicazione.getAllegato(iComunicazionePdf).getNomeFile())
					? "tsd" 
					: "pdf"
			);
			allegati[posComunicazionePdf] = new WSDMProtocolloAllegatoType();
			allegati[posComunicazionePdf].setTitolo(this.getOggettoMailUfficioProtocollo());	// "Testo della comunicazione"
			allegati[posComunicazionePdf].setNome(comunicazione.getAllegato(iComunicazionePdf).getNomeFile());
			allegati[posComunicazionePdf].setTipo(tipo);
			allegati[posComunicazionePdf].setContenuto(comunicazione.getAllegato(iComunicazionePdf).getFile());
			// serve per Titulus
			allegati[posComunicazionePdf].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazione.getDettaglioComunicazione().getId() + "/" + posComunicazionePdf);
		}
		
		ProtocolsUtils.setFieldsForNumix(allegati);

		return allegati;
	}
	
	/**
	 * Definisce un acronimo basato sulle iniziali del soggetto. 
	 * @param denominazione denominazione del soggetto
	 * @return acronimo costituito dalle iniziali in maiuscolo, stringa vuota altrimenti
	 */
	public static String getAcronimoSoggetto(String denominazione) {
		String acronimo = "";
		if (denominazione != null) {
			String denominazioneUpper = denominazione.toUpperCase();
			boolean precedenteSpazio = true;
			// si cicla carattere per carattere prendendo il primo carattere dopo uno spazio
			for (int i = 0; i < denominazioneUpper.length(); i++) {
				char carattere = denominazioneUpper.charAt(i); 
				if (carattere == ' ') {
					precedenteSpazio = true;
				} else {
					if (precedenteSpazio) {
						acronimo += carattere;
					}
					precedenteSpazio = false;
				}
			}
		}
		return acronimo;
	}
    
	/**
	 * Restituisce una stringa con cognome e nome invertiti. 
	 * @param denominazione cognome e nome 
	 * @return cognome e nome invertiti
	 */
	public static String getInvertiCognomeNome(String denominazione) {
		String cn = denominazione;
		int i = cn.indexOf(" ");
		if(i >= 0) {
			cn = StringUtils.right(denominazione, denominazione.length() - i - 1) + " " + 
				 StringUtils.left(denominazione, i);  
		}
		return cn;
	}    

	/**
	 * prepara gli allegati della comunicazione da inviare
	 * 
	 * @throws Exception 
	 */
	private void setAllegatoComunicazione(ComunicazioneType comunicazione) throws Exception {
		// calcola la dimensione degli allegati...
		int n = 0;
		if(this.allegatiNuovaComunicazione != null) {
			n = n + (this.allegatiNuovaComunicazione.getAdditionalDocs() != null
					 ? this.allegatiNuovaComunicazione.getAdditionalDocs().size() 
					 : 0)
				  + (this.allegatiNuovaComunicazione.getRequiredDocs() != null
					 ? this.allegatiNuovaComunicazione.getRequiredDocs().size()
					 : 0);
		}
		
		// considera sempre almeno l'allegato della "comunicazione.pdf"...
		n = n + 1;
			
		// prepara gli allegati della nuova comunicazione
		AllegatoComunicazioneType[] allegati = new AllegatoComunicazioneType[n];
		
		if(this.allegatiNuovaComunicazione != null) {
			int i = 0;
		
			// documenti ulteriori...
			for(Attachment attachment : allegatiNuovaComunicazione.getAdditionalDocs()) {
				allegati[i] = new AllegatoComunicazioneType();
				allegati[i].setDescrizione(attachment.getDesc());
				allegati[i].setNomeFile(attachment.getFileName());
				//allegati[i].setTipo(helper.getDocUlterioriContentType().get(j));
				try {
					allegati[i].setFile(FileUtils.readFileToByteArray(attachment.getFile()));
				} catch (IOException ex) {
					ApsSystemUtils.logThrowable(ex, this, "setAllegatoComunicazione");
					ExceptionUtils.manageExceptionError(ex, this);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				} catch (Throwable ex) {
					ApsSystemUtils.logThrowable(ex, this, "setAllegatoComunicazione");
					ExceptionUtils.manageExceptionError(ex, this);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
				i++;
			}
		
			// documenti richiesti...
			if(this.documentiRichiesti != null) {
				if(this.allegatiNuovaComunicazione.getRequiredDocs() != null) {
					for(Attachment attachment : allegatiNuovaComunicazione.getRequiredDocs()) {
						// cerca la descrizione dell'allegato richiesto...
						String descrizione = null;
						for (DocumentazioneRichiestaType item : this.documentiRichiesti) {
							if (attachment.getId() == item.getId()) {
								descrizione = item.getNome();
								break;
							}
						}
						
						allegati[i] = new AllegatoComunicazioneType();
						allegati[i].setId(attachment.getId());
						allegati[i].setNomeFile(attachment.getFileName());
						allegati[i].setDescrizione(descrizione);
						//allegati[i].setTipo( documenti.getDocRichiestiContentType().get(j) );
						try {
							allegati[i].setFile(FileUtils.readFileToByteArray(attachment.getFile()));
						} catch (IOException ex) {
							ApsSystemUtils.logThrowable(ex, this, "setAllegatoComunicazione");
							ExceptionUtils.manageExceptionError(ex, this);
							this.setTarget(CommonSystemConstants.PORTAL_ERROR);
						} catch (Throwable ex) {
							ApsSystemUtils.logThrowable(ex, this, "setAllegatoComunicazione");
							ExceptionUtils.manageExceptionError(ex, this);
							this.setTarget(CommonSystemConstants.PORTAL_ERROR);
						}	
						i++;
					}
				}
			}
		}
		
		// aggiungi l'allegato "comunicazione.pdf"... 
		// NB: questo metodo puo' essere ridefinito per aggiungere altri
		//     allegati specifici (ie. per le offerte il riepilogo delle buste...)
		this.addComunicazionePdf(allegati);
		
		// aggiorna gli allegati della comunicazione...
		comunicazione.setAllegato(allegati);
	}
		
	/**
	 * prepara l'allegato "comunicazione.pdf"
	 * se e' attiva la "INVIOFLUSSI.MARCATEMPORALE" allora marca temportalmente l'allegato
	 *  
	 */
	protected byte[] addComunicazionePdf(AllegatoComunicazioneType[] allegati) throws Exception {
		// prepara l'allegato in formato PDF o TSD...
		byte[] pdfRiepilogo = null;
		try {
			boolean isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A", false);
			
			pdfRiepilogo = JRPdfExporterEldasoft.textToPdf(
					this.getComunicazionePdf()
					, "Riepilogo comunicazione"
					, this
			);
					
			if(this.customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE")) {
				try {
					// prepara il riepilogo comprensivo di marcatura temporale...
					ITimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(pdfRiepilogo, this.appParamManager);
					if(resultMarcatura.getResult() == false){
						ApsSystemUtils.getLogger().error(
								"Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + 
								" ErrorMessage=" + resultMarcatura.getErrorMessage(),
								new Object[] { this.getCurrentUser().getUsername(), "marcaturaTemporale" });
						throw new ApsException(
								"Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + 
								" ErrorMessage=" + resultMarcatura.getErrorMessage());
					} else {
						pdfRiepilogo = resultMarcatura.getFile();
					}
					AllegatoComunicazioneType pdfMarcatoTemporale = new AllegatoComunicazioneType();
					pdfMarcatoTemporale.setFile(pdfRiepilogo);
					pdfMarcatoTemporale.setNomeFile(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD);
					pdfMarcatoTemporale.setTipo("tsd");
					pdfMarcatoTemporale.setDescrizione("File di riepilogo comunicazione con marcatura temporale");
					// Aggiungo il file agli allegati della comunicazione
					allegati[allegati.length - 1] = pdfMarcatoTemporale;
				} catch(Exception e) {
					ApsSystemUtils.logThrowable(e, this, "marcaturaTemporale");
					this.addActionError(this.getText("Errors.marcatureTemporale.generic")); 
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					throw e;
				}
			} else {
				AllegatoComunicazioneType pdfRiepilogoNonMarcato = new AllegatoComunicazioneType();
				pdfRiepilogoNonMarcato.setFile(pdfRiepilogo);
				pdfRiepilogoNonMarcato.setNomeFile(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF);
				pdfRiepilogoNonMarcato.setTipo(isActiveFunctionPdfA ? "pdf/a" : "pdf");
				pdfRiepilogoNonMarcato.setDescrizione("File di riepilogo comunicazione");
				// Aggiungo il file agli allegati della comunicazione
				allegati[allegati.length - 1] =  pdfRiepilogoNonMarcato;
			}	
			
		} catch (DocumentException e) {
			ApsSystemUtils.logThrowable(e, this, "addComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
			throw new Exception(e);
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "addComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
			throw new Exception(e);
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "addComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
			throw new Exception(e);
		} catch (XmlException e) {
			ApsSystemUtils.logThrowable(e, this, "addComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
			throw new Exception(e);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "addComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
			throw new Exception(e);
		}
		
		return pdfRiepilogo;
	}
		
	/**
	 * calcola la data ora corrente dal server NTP 
	 */	
	protected Date retrieveDataNTP() {
		Date data = null;
		//String lbl = this.getLabelNomeOperazione();	// SERVE ???
		//String nomeOperazioneLog = lbl; this.getI18nLabelFromDefaultLocale(lbl).toLowerCase();
		//String nomeOperazione = lbl;this.getI18nLabel(lbl).toLowerCase();
		String nomeOperazioneLog = this.getDescrizioneFunzione().toLowerCase();
		String nomeOperazione = this.getDescrizioneFunzione().toLowerCase();
		try {
			data = ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataNTP", this.getTextFromDefaultLocale("Errors.ntpTimeout", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpTimeout", new String[] { nomeOperazione }));
		} catch (UnknownHostException e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataNTP", this.getTextFromDefaultLocale("Errors.ntpUnknownHost", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione }));
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataNTP", this.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataNTP", this
					.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
		}
		return data;
	}

	/**
	 * Metodo che centralizza ed estrae alcune informazioni utili per procedere
	 * alla successiva eventuale protocollazione.
	 * 
	 * @throws ApsException
	 */
	protected void setInfoPerProtocollazione(String codice) throws ApsException {
		this.stazioneAppaltante = null;
		this.codiceSA = null;
		this.codiceCig = null;
		this.titoloPdfRiepilogoComunicazione = null;
		this.descrizioneGenerePdfRiepilogoComunicazione = null;
		this.dettaglioGara = null;
	
		DettaglioGaraType dettaglioGara = this.bandiManager.getDettaglioGara(codice);
		if(dettaglioGara != null) {
			this.stazioneAppaltante = dettaglioGara.getStazioneAppaltante();
			this.codiceSA = dettaglioGara.getStazioneAppaltante().getCodice();
			this.codiceCig = this.bandiManager.getCigBando(codice);
			this.titoloPdfRiepilogoComunicazione = (dettaglioGara != null ? dettaglioGara.getDatiGeneraliGara().getOggetto() : "");
			this.descrizioneGenerePdfRiepilogoComunicazione = DESCRIZIONE_GENERE_GARA;
			this.dettaglioGara = dettaglioGara;

		} else {
			DettaglioBandoIscrizioneType dettaglioIscrizione = this.bandiManager.getDettaglioBandoIscrizione(codice);			
			if(dettaglioIscrizione != null) {
				this.stazioneAppaltante = dettaglioIscrizione.getStazioneAppaltante();
				this.codiceSA = dettaglioIscrizione.getStazioneAppaltante().getCodice();
				if(Integer.toString(PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD).equals(dettaglioIscrizione.getDatiGeneraliBandoIscrizione().getTipoElenco())) {
					// elenco (genere = 10) 
					this.titoloPdfRiepilogoComunicazione = (dettaglioIscrizione != null ? dettaglioIscrizione.getDatiGeneraliBandoIscrizione().getOggetto() : "");
					this.descrizioneGenerePdfRiepilogoComunicazione = DESCRIZIONE_GENERE_ELENCO;
				} else {
					// catalogo (genere = 20)
					this.titoloPdfRiepilogoComunicazione = (dettaglioIscrizione != null ? dettaglioIscrizione.getDatiGeneraliBandoIscrizione().getOggetto() : "");
					this.descrizioneGenerePdfRiepilogoComunicazione = DESCRIZIONE_GENERE_CATALOGO;
				}
				
			} else {
				IContrattiManager contrattiManager = (IContrattiManager) ApsWebApplicationUtils
					.getBean("ContrattiManager", ServletActionContext.getRequest());
				
				ContrattoType dettaglioContratto = contrattiManager.getDettaglioContratto(codice);
				if(dettaglioContratto != null) {
					// TODO: 
					// al momento, per come e' implementata, 
					// questa funzione di nicchia non recupera la StazioneAppaltanteBandoType ma solo la denominazione 
					this.codiceSA = dettaglioContratto.getStazioneAppaltante();
					this.codiceCig = dettaglioContratto.getCig();
					this.titoloPdfRiepilogoComunicazione = (dettaglioContratto != null ? dettaglioContratto.getOggetto() : "");
					this.descrizioneGenerePdfRiepilogoComunicazione = DESCRIZIONE_GENERE_CONTRATTO;
					
				} else {
					IAvvisiManager avvisiManager = (IAvvisiManager) ApsWebApplicationUtils
						.getBean("AvvisiManager", ServletActionContext.getRequest());
				
					DettaglioAvvisoType dettaglioAvviso = avvisiManager.getDettaglioAvviso(codice);
					if(dettaglioAvviso != null && dettaglioAvviso.getStazioneAppaltante() != null) {
						this.stazioneAppaltante = dettaglioAvviso.getStazioneAppaltante();
						this.codiceSA = dettaglioAvviso.getStazioneAppaltante().getCodice();
						this.titoloPdfRiepilogoComunicazione = (dettaglioAvviso != null ? dettaglioAvviso.getDatiGenerali().getOggetto() : "");
						this.descrizioneGenerePdfRiepilogoComunicazione = DESCRIZIONE_GENERE_AVVISO;

					} else {
						// se non trovo ne un dettaglioGara, ne un dettaglioIscrizione,
						// ne un dettaglioContratto... allora cerco nella gara
						// anche se non e' pubblicata sul PortaleAppalti !!!
						// NB: 
						//   in caso di protocollazione non e' possibile rispondere
						// 	 ad una comunicazione inviata dall'ente su una gara 
						//   non ancora pubblicata!!!
						// FIX: estraggo allora il dato dell'eventuale
						// comunicazione a cui rispondo visto che non ho
						// reperito il dettaglio corrispondente
						
						// ???
//						this.codiceSA = ??? (helper.getStazioneAppaltante());
						// ???
					}
				}
			}
		}
	}
	
	/**
	 * restiruisce true se la protocollazione ha generato un protocollo
	 */
	public boolean isPresentiDatiProtocollazione() {
		return (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) &&
				this.annoProtocollo != null && 
				this.numeroProtocollo != null;
	}
	
	/**
	 * Verifica se la protocollazione va attivata.
	 * (tipologia di protocollazione (2=WSDM, 1=Mail, 0=non prevista))
	 * 
	 * @param stazioneAppaltanteProtocollante
	 *            eventuale unica stazione appaltante protocollante, da
	 *            configurazione
	 * @param stazioneAppaltanteProcedura
	 *            stazione appaltante della procedura per cui si richiede la
	 *            protocollazione
	 * @return true se la protocollazione va applicata, false altrimenti
	 */
	public boolean isProtocollazionePrevista(
			String stazioneAppaltanteProtocollante, 
			String stazioneAppaltanteProcedura) 
	{
		return (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA != this.tipoProtocollazione.intValue()) &&
			   	( (StringUtils.stripToNull(stazioneAppaltanteProtocollante) == null)
			   	   || (stazioneAppaltanteProtocollante.equals(stazioneAppaltanteProcedura)));
	}

	/**
	 * restituisce il mittente per la comunicazione da inviare
	 */
	protected String getMittenteNuovaComunicazione() {
		if(this.impresa != null) {
			return this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		}
		return "";
	}
	
	/**
	 * crea il riepilogo degli allegati... 
	 */
	private String getPdfRiepilogo(DocumentiAllegatiHelper documenti) {
		StringBuilder sb = new StringBuilder();

		if(documenti != null) {
			sb.append(Attachment.toPdfRiepilogo(documenti.getRequiredDocs()));
			sb.append(Attachment.toPdfRiepilogo(documenti.getAdditionalDocs()));
		}

		return sb.length() > 0
			   ? sb.toString()
			   : "Nessun documento allegato";
	}

	/**
	 * crea il contenuto per l'allegato "comunicazione.pdf" di default
	 */	
	private String getComunicazionePdfDefault() {
		boolean isGara = (DESCRIZIONE_GENERE_GARA.equalsIgnoreCase(this.descrizioneGenerePdfRiepilogoComunicazione));
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String nomeCliente = (String)this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE);
		
		// prepare il testo di "comunicazione.pdf"
		StringBuilder contenuto = new StringBuilder();
		contenuto.append(StringUtils.isNotEmpty(nomeCliente) ? nomeCliente + "\n\n" : "");
		contenuto.append(StringUtils.isNotEmpty(this.dettaglioSA.getDenominazione()) ? this.dettaglioSA.getDenominazione() + "\n" : "");
		contenuto.append(StringUtils.isNotEmpty(this.dettaglioSA.getIndirizzo()) ? this.dettaglioSA.getIndirizzo() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(this.dettaglioSA.getNumCivico()) ? this.dettaglioSA.getNumCivico() + ", " : "");
		contenuto.append(StringUtils.isNotEmpty(this.dettaglioSA.getCap()) ? this.dettaglioSA.getCap() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(this.dettaglioSA.getComune()) ? this.dettaglioSA.getComune() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(this.dettaglioSA.getProvincia()) ? "(" + this.dettaglioSA.getProvincia() + ")" : "");
		contenuto.append("\n\n");
		contenuto.append("Oggetto ").append(this.descrizioneGenerePdfRiepilogoComunicazione).append(": ")
					.append(StringUtils.isNotEmpty(this.titoloPdfRiepilogoComunicazione) ? this.titoloPdfRiepilogoComunicazione : "");
		contenuto.append(" - Codice ").append(this.descrizioneGenerePdfRiepilogoComunicazione).append(": ")
					.append((StringUtils.isNotEmpty(this.codice) ? this.codice : ""));
		if(isGara && StringUtils.isNotEmpty(this.codiceCig)) {
			contenuto.append(" - CIG: ").append(this.codiceCig);
		}
		contenuto.append("\n");
		contenuto.append("\n");
		contenuto.append("Oggetto comunicazione: ").append(this.getOggettoNuovaComunicazione());
		contenuto.append("\n");
		contenuto.append("\n");
		contenuto.append(this.getTestoNuovaComunicazione());
		contenuto.append("\n");
		contenuto.append("\n");
		contenuto.append("Allegati");
		contenuto.append("\n");
		contenuto.append(getPdfRiepilogo(allegatiNuovaComunicazione));
		contenuto.append("\n");
		contenuto.append("Operatore economico: ").append(ragioneSociale);
		contenuto.append("\n");
		contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " " : "");		
		contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", " : "");
		contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale()) ? "(" + this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")" : "");
		
		return contenuto.toString();
	}
	
	/**
	 * prepara il documento per la protocollazione (WSDOCUMENTO)
	 * 
	 * @return restituisce il documento per la protocollazione
	 */
	protected WSDocumentoType initWSDocumentoProtocollo(String entita, String key1, String oggetto, WSDMProtocolloDocumentoType ris) {
		WSDocumentoType documento = new WSDocumentoType();
		documento.setEntita(entita);
		documento.setChiave1(key1);
		documento.setNumeroDocumento(ris.getNumeroDocumento());
		documento.setAnnoProtocollo(ris.getAnnoProtocollo());
		documento.setNumeroProtocollo(ris.getNumeroProtocollo());
		documento.setVerso(WSDocumentoTypeVerso.IN);
		documento.setOggetto(oggetto);
		return documento;
	}
	
	/**
	 * recupera il fascicolo associato alla fase di protocollazione
	 * Questo metoto puo' essere ridefinito nelle classi figlie
	 * 
	 * @return restituisce il fascicolo associato al codice gara/avviso/esito/elenco/catalogo/comunicazione/stipula
	 * @throws ApsException 
	 */	
	protected FascicoloProtocolloType getFascicoloProtocollo() throws ApsException {
		return this.bandiManager.getFascicoloProtocollo(this.codice);
	}

	/**
	 * (LAPIS) aggiorna le informazioni prima di eseguire la protocollaComunicazione() 
	 * con l'id richiesta restituito dal protocollo LAPIS  
	 */
	public static void lapisAggiornaComunicazione(
			WSDMProtocolloDocumentoType protocollo,
			WSDocumentoType documento,
			Long idComunicazione,
			IAppParamManager appParamManager,
			Event evento) 
	{
		String codiceSistema = (String) appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equalsIgnoreCase(codiceSistema)) {
			try {
				// NB: 
				// successivamente IComunicazioniManager.protocollaComunicazione() aggiornera' W_INVCOM.COMNUMPROT
				// con quello che viene trovato in protocollo.genericS11 !!!
				protocollo.setNumeroProtocollo("ID_" + protocollo.getGenericS11());
				
				documento.setNumeroProtocollo(protocollo.getNumeroProtocollo());
				
				evento.setMessage("Aggiornamento comunicazione con id " + idComunicazione
						+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA
						+ ", con ID per richiesta di protocollazione " + protocollo.getGenericS11()); 
				
			} catch(Throwable t) {
				ApsSystemUtils.getLogger().error("aggiornaComunicazioneLapis", t);
			}
		}
	}
	
	/**
	 * (LAPIS) aggiorna il messaggio dell'evento in caso di completamneto manuale della protocollazione  
	 */
	public static void lapisAggiornaMessaggioEventoCompletamentoManuale(
			WSDMProtocolloDocumentoType protocollo,
			WSDocumentoType documento,
			Long idComunicazione,
			IAppParamManager appParamManager,
			Event evento) 
	{
		String codiceSistema = (String) appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equalsIgnoreCase(codiceSistema)) {
			evento.setMessage("Aggiornare manualmente la comunicazione con id " + idComunicazione
					  + " ed inserire inoltre un documento in ingresso per entita' " + documento.getEntita()
					  + ", chiave1 " + documento.getChiave1()
					  + ", oggetto " + documento.getOggetto()
					  + ", id richiesta protocollazione " + protocollo.getGenericS11()
					  + " ed un allegato per la comunicazione ed uno per ogni documento effettivamente allegato");
		}
	}

}
