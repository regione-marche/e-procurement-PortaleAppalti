package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
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
import it.eldasoft.www.sil.WSLfs.ContrattoLFSType;
import it.eldasoft.www.sil.WSStipule.StipulaContrattoType;
import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampResult;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.MarcaturaTemporaleFileUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageProtocollazioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.SaveWizardIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IStipuleManager;
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
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


public class ProcessPageRiepilogoNuovaComunicazioneAction extends AbstractProcessPageComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3653063448358992202L;

	protected INtpManager ntpManager;
	protected IComunicazioniManager comunicazioniManager;
	protected IMailManager mailManager;
	protected IWSDMManager wsdmManager;
	protected IEventManager eventManager;
	protected IStipuleManager stipuleManager;

	@Validate(EParamValidation.ACTION)
	protected String nextResultAction;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	@Validate(EParamValidation.CODICE)
	protected String codice2;
	@Validate(EParamValidation.USERNAME)
	protected String username;
	protected Date dataInvio;
	protected String target;
	@Validate(EParamValidation.ACTION)
	protected String from;
	protected Long genere;
	@Validate(EParamValidation.ENTITA)
	protected String entita;
		
	private InputStream inputStream;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String msgErrore;
	@Validate(EParamValidation.EMAIL)
	private String mailUfficioProtocollo;
	private Boolean allegaDocMailUfficioProtocollo;
	
	private Integer tipoProtocollazione;
	private String numeroProtocollo;
	private Long annoProtocollo;
	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltanteProtocollante;
	private String dataProtocollo;
	@Validate(EParamValidation.GENERIC)
	private String codiceSistema;

	@Validate(EParamValidation.UNLIMITED_TEXT)
    private String applicativo;
	private Long idComunicazione;
	private Long idDestinatario;
	private int pagina;
	@Validate(EParamValidation.GENERIC)
	private String tipo;
	private boolean dettaglioPresente;
	private boolean soccorsoIstruttorio;
	
	
	private final static String NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF = "comunicazione.pdf";
	private final static String NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD = "comunicazione.pdf.tsd";
	
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

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}
	
	public void setStipuleManager(IStipuleManager stipuleManager) {
		this.stipuleManager = stipuleManager;
	}

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
    public String getApplicativo() {
      return applicativo;
    }

    public void setApplicativo(String applicativo) {
      this.applicativo = applicativo;
    }

    public Long getIdComunicazione() {
      return idComunicazione;
    }

	public void setId(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}
	
	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getCodice2() {
		return codice2;
	}

	public void setCodice2(String codice2) {
		this.codice2 = codice2;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}
		
	public boolean isPresentiDatiProtocollazione() {
		return (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) &&
				this.annoProtocollo != null && 
				this.numeroProtocollo != null;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}

	public String getCodiceSistema() {
		return codiceSistema;
	}

	public String getCodice() {
		return codice;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public String getMsgErrore() {
		return msgErrore;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public void setNextResultAction(String nextResultAction) {
		this.nextResultAction = nextResultAction;
	}

	public String getFrom() {
		return from;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Boolean getDettaglioPresente() {
		return dettaglioPresente;
	}

	public void setDettaglioPresente(Boolean dettaglioPresente) {
		this.dettaglioPresente = dettaglioPresente;
	}
	
	public boolean getSoccorsoIstruttorio() {
		return soccorsoIstruttorio;
	}

	public void setSoccorsoIstruttorio(boolean soccorsoIstruttorio) {
		this.soccorsoIstruttorio = soccorsoIstruttorio;
	}

	public String getStazioneAppaltanteProtocollante() {
		return stazioneAppaltanteProtocollante;
	}

	public void setStazioneAppaltanteProtocollante(
			String stazioneAppaltanteProtocollante) {
		this.stazioneAppaltanteProtocollante = stazioneAppaltanteProtocollante;
	}
	
	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(String dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}


	/**
	 * restituisce la label LABEL_RICHIESTA_CON_ID associata a LAPIS 
	 */
	public String getLABEL_RICHIESTA_CON_ID() {
		return MessageFormat.format(this.getI18nLabel("LABEL_RICHIESTA_CON_ID"), new Object[] {dataInvio, numeroProtocollo});
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		return null;
	}
	
	/**
	 * ... 
	 */
	public String back() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) session
					.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
			
			this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
					helper.getPreviousStepNavigazione(WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE)));

		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	/**
	 * ... 
	 */
	public String cancel() {
		return "cancel";
	}
	
	/**
	 * ... 
	 */
	public String send() throws ApsException {
		this.setTarget(SUCCESS);
		
		this.dettaglioPresente = ((Boolean) this.session.get(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE)).booleanValue();		
		this.pagina = ((Integer) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA)).intValue();
		this.from = (String)this.session.get(ComunicazioniConstants.SESSION_ID_FROM);
		this.tipo = StringUtils.stripToNull((String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO));				
		this.codiceSistema = (String) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);									
		this.mailUfficioProtocollo = (String) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
		this.allegaDocMailUfficioProtocollo = (Boolean) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
		this.stazioneAppaltanteProtocollante = (String) this.getAppParamManager().getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
		this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA); 
		this.codice2 = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA);
		
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		
		ComunicazioneType nuovaComunicazione = new ComunicazioneType();
		byte[] pdfRiepilogo = null;
		this.soccorsoIstruttorio = false;
		
		if(helper != null && PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equals(helper.getEntita())) {
			this.codice2 = helper.getCodice2();
		}
		
		// calcola la data di invio
		boolean continua = false;
		this.dataInvio = ProcessPageRiepilogoNuovaComunicazioneAction.retrieveDataNTP(
				this.ntpManager, 
				this, 
				"LABEL_COMUNICAZIONI_INVIA_COMUNICAZIONE");
		if (this.dataInvio == null) {
			this.setTarget(INPUT);
		} else if (helper == null) { 
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			continua = true;
		}
		
		// verifica se la richiesta e' oltre la data di scadenza
		if(continua && 
		   helper.getModello() != null && helper.getModello() > 0 && helper.getDataScadenza() != null) 
		{
			continua = this.dataInvio.before(helper.getDataScadenza());
			if(!continua) {
				//this.addActionError(this.getText("Errors.oltreDataScadenza"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
				
				// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(helper.getCodice()); 
				evento.setLevel(Event.Level.ERROR);
				evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Accesso alla funzione soccorso istruttorio (comunicazione con id " + helper.getComunicazioneId() + ")");
				evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + 
										" (" + UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
				this.eventManager.insertEvent(evento);
			}
		}
			
		// inizia la fase di invio ed eventuale protocollazione
		boolean inviataComunicazione = false;
		boolean protocollazioneOk = true;
		DettaglioGaraType dettaglioGara = null;
		StipulaContrattoType dettaglioStipula = null;
		Event evento = null;
		
		if(continua) {
			boolean contrattiLFS = false;
			boolean stipule = false;
			try {
				this.applicativo = helper.getComunicazioneApplicativo();
				this.idComunicazione = helper.getComunicazioneId();
				this.idDestinatario = helper.getIdDestinatario();
				this.username = this.getCurrentUser().getUsername();
				this.codice = StringUtils.stripToNull((helper.getCodice()));
				this.soccorsoIstruttorio = (helper.getModello() != null && helper.getModello() > 0);
				contrattiLFS = (PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(helper.getEntita()));				
				stipule = (PortGareSystemConstants.ENTITA_STIPULA.equalsIgnoreCase(helper.getEntita()));
				
				dettaglioGara = this.getBandiManager().getDettaglioGara(helper.getCodice());
				
				// se necessario, inizializza le info per la protocollazione (codiceSA, stazioneAppaltante, codiceCig)
				this.setInfoPerProtocollazione(helper);
				
				// imposta la stazione appaltante per recuperare i parametri dalla configurazione corretta
				this.getAppParamManager().setStazioneAppaltanteProtocollazione(this.getCodiceSA());
	
				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!! 
				this.tipoProtocollazione = this.getAppParamManager().getTipoProtocollazione();
				
				// recupera l'info dell'"entita" dalla sessione
				// e verifico se la comunicazione si riferisce ad una "stipula" od un "contratto LFS"
				this.genere = (Long)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
				this.entita = (String)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
				
				// se trovo una gestione per un "contratto LFS" allora recupero le info relative 
				if(contrattiLFS) {
					this.genere = null;
					this.entita = helper.getEntita();
				}

				// NB: in caso "contratto LFS",
				// 	   la protocollazione non e' prevista
				if(PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(this.entita)) {
					this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				}

				// se trovo una gestione per una "stipula" allora recupero le info relative 
				if(stipule) {
					this.entita = helper.getEntita();
				}
	
				// se l'"entita" e' una stipula o e' ancora vuota, 
				// verifica se esiste un contratto di stipula per il "codice" e recupera il dettaglio della stipula
				if(PortGareSystemConstants.ENTITA_STIPULA.equalsIgnoreCase(this.entita) || this.entita == null) {
					// se non trovo in sessione l'"entita", verifico se la comunicazione e' associata ad una stipula...
					dettaglioStipula = this.stipuleManager.dettaglioStipulacontratto(this.codice, this.getCurrentUser().getUsername(), false);
					if(dettaglioStipula != null) {
						stipule = true;
						this.entita = PortGareSystemConstants.ENTITA_STIPULA;
					}
				}
				
				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.getAppParamManager().isConfigWSDMNonDisponibile()) {
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
					event.setDetailMessage(msgerr);
					event.setMessage("Configurazione non disponibile o vuota");
					this.eventManager.insertEvent(event);
				}

				if(continua) {
					// recupera il "mittente" della comunicazione  
					String mittente = helper.getDatiImpresaHelper().getDatiPrincipaliImpresa().getRagioneSociale();
					if(StringUtils.isNotEmpty(helper.getDestinatario())) {
						mittente = helper.getDestinatario();
					}
					helper.setMittente(mittente);
					
					// prepara l'oggetto della comunicazione  
					String oggetto = helper.getOggetto();
					if(StringUtils.isNotEmpty(helper.getTipoRichiesta())) { 
						LinkedHashMap<String, String> listaTipiRichiesta =  InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPOLOGIE_COMUNICAZIONI);
						if(listaTipiRichiesta != null && listaTipiRichiesta.size() > 0) {
							oggetto = listaTipiRichiesta.get(helper.getTipoRichiesta()) + " - " + helper.getOggetto();
						}
						if(oggetto.length() > 300) {
							oggetto = oggetto.substring(300); 
						}
					}
					
					// prepara i dati della nuova comunicazione da inviare al servizio
					DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities.createDettaglioComunicazione(
							null, 
							this.username, 
							this.codice, 
							helper.getCodice2(),
							helper.getMittente(), 
							CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA, 
							oggetto, 
							helper.getTesto(), 
							PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE, 
							this.getDataInvio());
					
					// in caso di "stipula" o "contratto LFS" aggiorna l'"entita" della nuova comunicazione...
					if(StringUtils.isNotEmpty(this.entita)) {
						dettComunicazione.setEntita(this.entita);
					}
					
					// recupera la comunicazione originaria BO 
					// e recupera i dati della comunicazione di risposta (idprgris e idcomris)
					if(helper.getComunicazioneId() != null && helper.getComunicazioneId() > 0) {
						dettComunicazione.setIdRisposta(helper.getComunicazioneId());
						dettComunicazione.setApplicativoRisposta(helper.getComunicazioneApplicativo());
					}
		
					// per le comunicazioni standard "commodello" dovrebbe sempre essere NULL
					// mentre per le comunicazioni di soccorso istruttorio hanno un valore
					// che viene copiato dalla comunicazione di partenza
					dettComunicazione.setModello(helper.getModello());
					dettComunicazione.setTipoBusta(helper.getTipoBusta());
					
					dettComunicazione = this.integraCreateDettaglioComunicazione(dettComunicazione);
					
					nuovaComunicazione.setDettaglioComunicazione(dettComunicazione);
		
					// FASE 1: invio della comunicazione
					if (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA != this.tipoProtocollazione
						&& this.getCodiceSA() == null) 
					{
						// aggiorno lo stato per creare l'evento con l'indicazione
						// corretta dello stato, il cui evento conterra' il
						// messaggio di dettaglio con l'errore emesso nell'eccezione
						dettComunicazione.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
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
								"Bloccato invio comunicazione per operatore "
										+ this.username
										+ " e risposta alla comunicazione con id "
										+ this.idComunicazione
										+ " causa stazione appaltante non impostata o procedura non pubblicata sul portale e protocollazione comunicazione richiesta");
					} else if (!SaveWizardIscrizioneAction.isProtocollazionePrevista(
							this.tipoProtocollazione,
							this.stazioneAppaltanteProtocollante,
							this.getCodiceSA())) 
					{
						// se la protocollazione non e' prevista, si resetta il tipo protocollazione cosi' in seguito 
						// il codice testa su un'unica condizione
						this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
					}
	
					dettComunicazione.setStato(
							this.tipoProtocollazione.intValue() != PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA 
							? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE
							: CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
	
					// creazione evento di invio della comunicazione
					evento = ComunicazioniUtilities.createEventSendComunicazione(
							this.username, 
							this.codice, 
							nuovaComunicazione,
							this.dataInvio,
							this.getCurrentUser().getIpAddress(),
							this.getRequest().getSession().getId());
					
					// caso area personale -> lista ricevute -> rispondi a comunicazione 
					if(null == this.genere && null == this.entita) {
						this.genere = this.getBandiManager().getGenere(this.codice);
						if(dettaglioStipula != null) {
							dettComunicazione.setEntita(PortGareSystemConstants.ENTITA_STIPULA);
						}
					}
					
					DocumentiComunicazioneHelper documentiComunicazioneHelper = helper.getDocumenti();
					// Aggiunta degli allegati alla nuova comunicazione da inviare 
					pdfRiepilogo = this.setAllegatoComunicazione(
								nuovaComunicazione, 
								helper);
					
					// fino ad ora si utilizza this.codice (che dovrebbe sempre arrivare valorizzato), 
					// ma se arriva non valorizzato, una volta inizializzati gli oggetti
					// per l'invio si procede al controllo e lo si blocca, mentre la tracciatura dell'evento risulta completa di dati
					if (this.codice == null) {
						throw new ApsException("Bloccato invio comunicazione per operatore " + this.username + 
											   " e risposta alla comunicazione con id " + this.idComunicazione + 
											   " per codice procedura vuoto");
					}
					
					nuovaComunicazione.getDettaglioComunicazione().setId(this.comunicazioniManager.sendComunicazione(nuovaComunicazione));
					
					// si rigenera l'evento perche' nel caso di esito positivo si
					// arricchisce il messaggio con l'id della comunicazione (prima
					// dell'invio e' null)
					evento = ComunicazioniUtilities.createEventSendComunicazione(
							this.username, 
							this.codice, 
							nuovaComunicazione,
							this.dataInvio,
							this.getCurrentUser().getIpAddress(),
							this.getRequest().getSession().getId());
					// forzatura perche' lo stato di inserimento e' 3 e non 5 come avviene solitamente
					if (this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
						evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
					}
	
					if(documentiComunicazioneHelper != null) {
						documentiComunicazioneHelper.resetStatiInvio(nuovaComunicazione);
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

			// FASE 2: ove previsto, si invia alla protocollazione
			if (inviataComunicazione) {
				//recupero l'id della comunicazione

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
						this.sendMailUfficioProtocollo(helper, helper.getDatiImpresaHelper());
						mailProtocollazioneInviata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a 5
						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() + " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
						this.comunicazioniManager.updateStatoComunicazioni(
								new DettaglioComunicazioneType[] {nuovaComunicazione.getDettaglioComunicazione()}, 
								CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
						this.eventManager.insertEvent(evento);
						this.dataProtocollo = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
					} catch (Throwable t) {
						evento.setError(t);
						this.eventManager.insertEvent(evento);
						if (mailProtocollazioneInviata) {
							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							this.msgErrore = this.getTextFromDefaultLocale(
									"Errors.updateStatoComunicazione",
									nuovaComunicazione.getDettaglioComunicazione().getId().toString());
							ApsSystemUtils.logThrowable(t, this, "send", this.msgErrore);
							this.msgErrore = this.getText("Errors.updateStatoComunicazione",
									new String[]{nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
							evento.resetDetailMessage();
							this.eventManager.insertEvent(evento);
							this.dataProtocollo = UtilityDate.convertiData(
									this.dataInvio, 
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
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
					long idComunicazione = nuovaComunicazione.getDettaglioComunicazione().getId();

					try {
						String codiceProcedura = this.codice;
						if(stipule) {
							codiceProcedura = Long.toString(dettaglioStipula.getId());
						}
						
						FascicoloProtocolloType fascicoloBackOffice = this.getBandiManager().getFascicoloProtocollo(codiceProcedura);
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

						WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = creaInputProtocollazioneWSDM(
								helper.getDatiImpresaHelper(), 
								nuovaComunicazione, 
								fascicoloBackOffice,
								dettaglioGara,
								pdfRiepilogo);

						ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						this.annoProtocollo = ris.getAnnoProtocollo();
						this.numeroProtocollo = ris.getNumeroProtocollo();
						if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
							this.numeroProtocollo = ris.getGenericS11();
						}
						if (ris.getDataProtocollo() != null) {
							this.setDataProtocollo(UtilityDate.convertiData(
									ris.getDataProtocollo().getTime(), 
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						}
						
						protocollazioneInoltrata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a 3 aggiornando inoltre anche i dati di protocollazione (WSDOCUMENTO)						
						documento = new WSDocumentoType();
						documento.setEntita("GARE");			// entita di default e' GARE...
						documento.setChiave1(this.codice);
						if(stipule) {
							documento.setEntita(PortGareSystemConstants.ENTITA_STIPULA);
							documento.setChiave1(Long.toString(dettaglioStipula.getId()));
						}
						documento.setNumeroDocumento(ris.getNumeroDocumento());
						documento.setAnnoProtocollo(ris.getAnnoProtocollo());
						documento.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());
						
						// si rilegge la comunicazione in modo da ottenere gli id degli allegati
						nuovaComunicazione = this.comunicazioniManager.getComunicazione(
								nuovaComunicazione.getDettaglioComunicazione().getApplicativo(), 
								nuovaComunicazione.getDettaglioComunicazione().getId());

						// calcola il numero di allegati da gestire...
						int n = nuovaComunicazione.getAllegato().length;
						WSAllegatoType[] allegati = new WSAllegatoType[1+n];
						allegati[0] = new WSAllegatoType();
						allegati[0].setEntita("W_INVCOM");
						allegati[0].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
						allegati[0].setChiave2(nuovaComunicazione.getDettaglioComunicazione().getId().toString());
						for (int i = 0; i < n; i++) {
							allegati[i+1] = new WSAllegatoType();
							allegati[i+1].setEntita("W_DOCDIG");
							allegati[i+1].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
							allegati[i+1].setChiave2(nuovaComunicazione.getAllegato(i).getId().toString());
						}
						
						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);						
						evento.setMessage("Aggiornamento comunicazione con id " + idComunicazione
								+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA
								+ ", protocollata con anno " + ris.getAnnoProtocollo() 
								+ " e numero " + ris.getNumeroProtocollo());
						
						// LAPISOPERA: inserisci l'ID richiesta restituito dal protocollo nella W_INVCOM e aggiorna il messaggio dell'evento
						ProcessPageProtocollazioneAction.lapisAggiornaComunicazione(
								ris,
								documento,
								nuovaComunicazione.getDettaglioComunicazione().getId(),
								this.getAppParamManager(),
								evento);
						
						this.comunicazioniManager.protocollaComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								idComunicazione,
								ris.getNumeroProtocollo(),
								(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataInvio),
								CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA,
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
									"Errors.updateStatoComunicazione",
									String.valueOf(idComunicazione));
							ApsSystemUtils.logThrowable(t, this, "invio", this.msgErrore);
							this.msgErrore = this.getText(
									"Errors.updateStatoComunicazione",
									new String[] { String.valueOf(idComunicazione) });
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + idComunicazione
									+ " ed inserire inoltre un documento in ingresso per entita'  " + documento.getEntita()
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
									this.getAppParamManager(),
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
							// si deve annullare l'invio che si e' tentato di
							// protocollare
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
					this.sendMailConfermaImpresa(
							helper.getDatiImpresaHelper(), 
							helper);
				} catch (Throwable t) {
					ApsSystemUtils.getLogger()
						.error("Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di invio offerta per la gara {}.",
							new Object[] {
							this.getCurrentUser().getUsername(),
							this.codice});
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
				this.setTarget("successPage");
				this.session.remove(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
				this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
			}
			
			// concludi la protocollazione
			this.getAppParamManager().setStazioneAppaltanteProtocollazione(null);
		}
		return this.getTarget();//this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA, "APPA")
	}
	
	/**
	 * Annulla gli effetti della comunicazione inviata eliminandola.
	 * 
	 * @param comunicazione
	 *            comunicazione da eliminare
	 */
	private void eliminaComunicazioneInviata(String idApplicativo, Long idComunicazione) {

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
			msgErrore = this.getTextFromDefaultLocale(
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
	 * @param comunicazioneHelper
	 *            helper della comunicazione
	 * @param datiImpresaHelper
	 *            dati dell'impresa
	 * @throws ApsException
	 */
	private void sendMailUfficioProtocollo(
			WizardNuovaComunicazioneHelper comunicazioneHelper, 
			WizardDatiImpresaHelper datiImpresaHelper) throws ApsException 
	{
		DocumentiComunicazioneHelper documentiHelper = comunicazioneHelper.getDocumenti(); 
		if (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL == this.tipoProtocollazione) {
			if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
				throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}

			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio

			// si crea un file temporaneo con il testo della comunicazione
			String nomeFile = FileUploadUtilities.generateFileName() + ".txt";
			File file = new File(StrutsUtilities.getTempDir(
					this.getRequest().getSession().getServletContext())	.getAbsolutePath()
					+ File.separatorChar + nomeFile);
			PrintWriter fileWriter;
			try {
				fileWriter = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				throw new ApsException("Errore durante la creazione del file temporaneo contenente il testo della comunicazione da inviare", e);
			} catch (Throwable e) {
				throw new ApsException("Errore durante la creazione del file temporaneo contenente il testo della comunicazione da inviare", e);
			}
			fileWriter.write(comunicazioneHelper.getTesto());
			fileWriter.close();
			documentiHelper.getFileGenerati().add(file);
			
			// si predispongono gli allegati da inserire
			Properties p = new Properties();
			p.put("comunicazione.txt", file.getAbsolutePath());
			
			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.allegaDocMailUfficioProtocollo)
				documentiHelper.getAdditionalDocs().forEach(attachment -> {
					p.put(attachment.getFileName(), attachment.getFile().getAbsolutePath());
				});

			String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
			String codFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
			String partitaIVA = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
			String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
			String sede = datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
					+ " " + datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()
					+ ", " + datiImpresaHelper.getDatiPrincipaliImpresa().getCapSedeLegale()
					+ " " + datiImpresaHelper.getDatiPrincipaliImpresa().getComuneSedeLegale()
					+ " (" + datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")";
			String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
			String subject = comunicazioneHelper.getOggetto();
			String text = MessageFormat.format(this.getI18nLabel(
					"MAIL_INVCOM_PROTOCOLLO_TESTOCONALLEGATI"),
					new Object[] { ragioneSociale, codFiscale, partitaIVA,
							mail, sede, data, this.codice });
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
	}
	
	/**
	 * Crea e popola il contenitore per richiedere la protocollazione mediante
	 * WSDM.
	 * 
	 * @param datiImpresaHelper
	 *            dati dell'impresa
	 * @param comunicazione
	 *            comunicazione da inviare
	 * @return contenitore popolato
	 * @throws Exception 
	 */
	private WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardDatiImpresaHelper datiImpresaHelper,
			ComunicazioneType comunicazione,
			FascicoloProtocolloType fascicoloBackOffice,
			DettaglioGaraType dettaglioGara,
			byte[] contenutoPdf) throws Exception 
	{
		// ATTENZIONE:
		// L'UTILIZZO DEL CAST A INTEGER SUL METODO appParamManager.getConfigurationValue(...)
		// E' DEPRECATO, IN QUANTO NON E' SEMPRE VERO CHE IN PPCOMMON_PROPERTIES ESISTA
		// LA DEFINIZIONE DEL TIPO DI DATO DA RESTITUIRE (INT, BOOLEAN, STRING)
		// TUTTI I NUOVI PARAMETRI ANDREBBERO CONSIDERATI SEMPRE COME STRINGHE
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);

		String codiceSistema = (String) this.getAppParamManager()
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
		boolean riservatezzaFascicolo = false;
		Integer cfMittente = (Integer) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE);
		boolean usaCodiceFiscaleMittente = (cfMittente != null ? 1 == cfMittente : true);
		String channelCode = (String) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE);
		String sottoTipo = (String)this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SOTTOTIPO_COMUNICAZIONE);
		String rup = (this.getStazioneAppaltante() != null ? this.getStazioneAppaltante().getRup() : null);
		String acronimoRup = ProcessPageProtocollazioneAction.getAcronimoSoggetto(rup);
		
		boolean isStipula = (this.genere == null && PortGareSystemConstants.ENTITA_STIPULA.equals(this.entita));
		//boolean isElenco = (this.genere != null && this.genere == 10);
		//boolean isCatalogo = (this.genere != null && this.genere == 20);
		//boolean isAvvisi = (this.genere != null && this.genere == 11);
		//boolean isGareOda = (this.genere != null); // && this.genere == 10);
		if(isStipula) {
			// STIPULA
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_CLASSIFICA);
			tipoDocumento = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_CODICE_REGISTRO);
			idIndice = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_INDICE);			
		} else if (this.genere == 10) {
			// ELENCO
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CLASSIFICA);
			tipoDocumento = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CODICE_REGISTRO);
			idIndice = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_INDICE);
		} else if (this.genere == 20) {
			// CATALOGO
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_CLASSIFICA);
			tipoDocumento = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_CODICE_REGISTRO);
			idIndice = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_INDICE);
		} else if (this.genere == 11) {
			// AVVISI
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_CLASSIFICA);
			tipoDocumento = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_CODICE_REGISTRO);
			idIndice = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_INDICE);
		} else {
			// GARE, ODA
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);
			tipoDocumento = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO);
			codiceRegistro = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CODICE_REGISTRO);
			idIndice = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);
		}
		
		if (esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null 
					         ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			// si sovrascrive la classifica, quindi si da' priorita' a quella
			// indicata nel fascicolo
			classificaFascicolo = fascicoloBackOffice.getClassifica();
			riservatezzaFascicolo = (fascicoloBackOffice.getRiservatezza() != null 
									 ? fascicoloBackOffice.getRiservatezza() : false);
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
			// si legge la classifica presa dalla configurazione 
			// solo se non esiste il fascicolo
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);

			if(isStipula) {
				// STIPULA
				idTitolazione = (String)this.getAppParamManager()
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_TITOLAZIONE);
			} else if (this.genere == 10) {
				// ELENCO
				idTitolazione = (String)this.getAppParamManager()
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TITOLAZIONE);
			} else if (this.genere == 20) {
				// CATALOGO
				idTitolazione = (String)this.getAppParamManager()
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_TITOLAZIONE);
			} else if (this.genere == 11) {
				// AVVISI
				idTitolazione = (String)this.getAppParamManager()
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_AVVISI_TITOLAZIONE);
			} else {
				// GARE, ODA
				idTitolazione = (String)this.getAppParamManager()
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TITOLAZIONE);
			}
		}

		idUnitaOperDestinataria = (String)this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA e' vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}
		
		if (isStipula || this.genere == 10 || this.genere == 20 || this.genere == 11) {
			// ELENCO, CATALOGO, AVVISI, STIPULA
		} else {
			// GARE, ODA
			idIndice = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);
		}
		
		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);
		wsdmProtocolloDocumentoIn.setChannelCode(channelCode);
		
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS11(sottoTipo);
			wsdmProtocolloDocumentoIn.setGenericS12(rup);
		}
		if(IWSDMManager.CODICE_SISTEMA_DOCER.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS11( (String)this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPO_FIRMA) );
			wsdmProtocolloDocumentoIn.setGenericS31( (String)this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_DESC_UNITA_OPERATIVA_DESTINATARIA) );
			wsdmProtocolloDocumentoIn.setGenericS32(idUnitaOperDestinataria);
		}
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			if(esisteFascicolo) {
				wsdmProtocolloDocumentoIn.setGenericS31(fascicoloBackOffice.getCodiceUfficio());
			}
		}
		
		String tipoDocInviaComunicazione = (String)this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO_INVIA_COMUNICAZIONE);

		if(tipoDocInviaComunicazione != null && !"".equals(tipoDocInviaComunicazione)){
			tipoDocumento = tipoDocInviaComunicazione;
		}
		wsdmProtocolloDocumentoIn.setTipoDocumento(tipoDocumento);
		
		Calendar data = new GregorianCalendar();
		data.setTimeInMillis(this.dataInvio.getTime());
		wsdmProtocolloDocumentoIn.setData(data);
		
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String indirizzo = datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
				+ " " + datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale();
		String descrizione = MessageFormat.format(this.getI18nLabelFromDefaultLocale(
				"MAIL_INVCOM_PROTOCOLLO_TESTOCONALLEGATI"),
				new Object[] {
						ragioneSociale,
						codiceFiscale,
						partitaIva,
						(String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"),
						indirizzo,
						UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
						this.codice });
		String oggetto = helper.getOggetto();
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.codice + " - " + oggetto;
		}
		
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}

		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		wsdmProtocolloDocumentoIn.setDescrizione(descrizione);
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceRegistro);
		
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId());
		
		// servono per Archiflow / FOLIUM
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(this.getCodiceSA());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + this.codice);
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(this.codice);
		}
		wsdmProtocolloDocumentoIn.setCig(this.getCodiceCig());
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(this.getCodiceSA());
		wsdmProtocolloDocumentoIn.setMezzo((String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));
		
		// serve per JIride*
		// NB: per le FS12 servono i parametri della "riservatezza" ?
		if(riservatezzaFascicolo) {
			// BO: riservatezza = SI
			String livelloRiservatezza = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_LIVELLO_RISERVATEZZA);
			
			if (StringUtils.isNotEmpty(livelloRiservatezza)) {
				// se ISRISERVA == SI
				// allora si invia la property nel campo gia'  valorizzato 
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
		
		// Mittente
		wsdmProtocolloDocumentoIn.setMittenteInterno((String) this.getAppParamManager()
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
		// JPROTOCOL: "Cognomeointestazione" accetta al massimo 100 char 
		if(IWSDMManager.CODICE_SISTEMA_JPROTOCOL.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 100));
		} if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 200));
		} else {
			mittenti[0].setCognomeointestazione(ragioneSociale);
		}
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
//			if("6".equals(impresa.getTipoImpresa())) {
//		    	mittenti[0].setTipoVoceRubrica(WSDMTipoVoceRubricaType.PERSONA);
//		    } else {
//		    	mittenti[0].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);
//		    }
			mittenti[0].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);
		}
		if (usaCodiceFiscaleMittente) {
			mittenti[0].setCodiceFiscale(codiceFiscale);
		} else {
			mittenti[0].setCodiceFiscale("");
		}
		mittenti[0].setPartitaIVA(partitaIva);
		mittenti[0].setIndirizzoResidenza(indirizzo);
		mittenti[0].setLocalitaResidenza(impresa.getComuneSedeLegale());
		mittenti[0].setComuneResidenza(impresa.getComuneSedeLegale());
		mittenti[0].setNazionalita(impresa.getNazioneSedeLegale());
	    mittenti[0].setMezzo((String) this.getAppParamManager()
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
		boolean inTesta = false;		// default, inserisci in coda
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			String v = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
			if("1".equals(v)) {
				inTesta = true;
			}
		}
	
		WSDMProtocolloAllegatoType[] allegati = createAttachments(comunicazione, contenutoPdf, helper, inTesta);

		// aggiorna gli allegati 
		wsdmProtocolloDocumentoIn.setAllegati(allegati);
		
		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)){
			wsdmProtocolloDocumentoIn.setOggetto(this.codice + "-" + wsdmProtocolloDocumentoIn.getOggetto() + "-" + 
					(dettaglioGara == null ? "" : dettaglioGara.getDatiGeneraliGara().getOggetto()) );
		}
	    Calendar dataArrivo = Calendar.getInstance();
	    dataArrivo.setTimeInMillis(System.currentTimeMillis());
	    wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(allegati.length -1));
	    wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
	    wsdmProtocolloDocumentoIn.setSupporto((String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));
	    
		String struttura = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STRUTTURA);
		// se specificata in configurazione la uso, altrimenti riutilizzo la
		// configurazione presente nel fascicolo di backoffice (cosi' avveniva
		// per ARCHIFLOW)
		if (StringUtils.isNotEmpty(struttura)) {
			wsdmProtocolloDocumentoIn.setStruttura(struttura);
		} else if (esisteFascicolo) {
	    	wsdmProtocolloDocumentoIn.setStruttura(fascicoloBackOffice.getStrutturaCompetente());
	    }
		
		String tipoAssegnazione = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPOASSEGNAZIONE);
		// se specificata in configurazione la uso (JPROTOCOL)
		if (StringUtils.isNotEmpty(tipoAssegnazione)) {
			wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoAssegnazione);
		}
		
		return wsdmProtocolloDocumentoIn;
	}

	private WSDMProtocolloAllegatoType[] createAttachments(ComunicazioneType comunicazione, byte[] contenutoPdf,
			WizardNuovaComunicazioneHelper helper, boolean inTesta)
			throws Exception {
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[1 + comunicazione.getAllegato().length];
		int n = 0;
		if(inTesta) {
			n++;
		}

		// ...eventuali allegati della comunicazione. Non allego il file comunicazione. Lo faccio in seguito perche' deve rispettare l'eventuale ordine voluto dal protocollo (in testa o in coda)
		for (int i = 0; i < comunicazione.getAllegato().length; i++) {
			if(!comunicazione.getAllegato(i).getNomeFile().equals(NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF) && 
					!comunicazione.getAllegato(i).getNomeFile().equals(NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD)){
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
		
		//... inserisci "comunicazione.pdf" nella lista degli allegati (in testa/in coda)
		//... prepara l'allegato "comunicazione.pdf"
		int n2 = n;
		if(inTesta) {
			n2 = 0;
		}
		
		byte[] pdfRiepilogo = null;
		if(contenutoPdf == null){
			contenutoPdf = getPdfRiepilogoComunicazione(helper).getBytes();
		}
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
		.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
				ServletActionContext.getRequest());
		
		IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
		.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
				ServletActionContext.getRequest());
		String tipo = null;
		String nome = null;
		
		// Allego file riepilogo comunicazione
		if(customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE")){
			if(contenutoPdf == null){
				DigitalTimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(contenutoPdf, appParamManager);
				if(resultMarcatura.getResult() == false){
					ApsSystemUtils.getLogger().error(
							"Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage="+ resultMarcatura.getErrorMessage(),
							new Object[] { this.getCurrentUser().getUsername(), "marcaturaTemporale" });
					throw new ApsException("Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage="+ resultMarcatura.getErrorMessage());
				} else {
					pdfRiepilogo = resultMarcatura.getFile();
				}
			} else {
				pdfRiepilogo = contenutoPdf;
			}
			tipo = "tsd";
			nome = NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD;
		} else {
			if(contenutoPdf == null){
				pdfRiepilogo = getPdfRiepilogoComunicazione(helper).getBytes();
			}
			pdfRiepilogo = contenutoPdf;
			tipo = "pdf";
			nome = NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF;
		}	
		
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setTitolo("Testo della comunicazione");
		allegati[n2].setTipo(tipo);
		allegati[n2].setNome(nome);
		allegati[n2].setContenuto(pdfRiepilogo);
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId() + "/" + n2);
		
		ProtocolsUtils.setFieldsForNumix(allegati);
		
		return allegati;
	}

	/**
	 * crea il riepilogo pdf 
	 */
	private String getPdfRiepilogoComunicazione(
			WizardNuovaComunicazioneHelper helper) throws ApsException {
		
		DettaglioGaraType dettaglioGara = this.getBandiManager().getDettaglioGara(helper.getCodice());
		DettaglioStazioneAppaltanteType stazione = new DettaglioStazioneAppaltanteType();
		String titolo = null;
		String descGenere = null;
		
		if(this.entita != null && PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equals(this.entita)) {
			// CONTRATTO LFS
			ContrattoLFSType dettaglioContrattoLFS = this.contrattiLFSManager.getDettaglioContrattoLFS(
					this.getCurrentUser().getUsername(),
					helper.getCodice(), 
					helper.getCodice2());
			if(dettaglioContrattoLFS != null) {
				titolo = dettaglioContrattoLFS.getOggetto();
				if(dettaglioContrattoLFS.getCodiceSA() != null){
					stazione = this.getBandiManager().getDettaglioStazioneAppaltante(dettaglioContrattoLFS.getCodiceSA());
				}
				descGenere = "contratto LFS";
			}	
		} else if(this.entita != null && PortGareSystemConstants.ENTITA_STIPULA.equals(this.entita)) {
			// STIPULA CONTRATTO
			StipulaContrattoType dettaglioStipula = this.stipuleManager.dettaglioStipulacontratto(helper.getCodice(), this.getCurrentUser().getUsername(), false);
			titolo = (dettaglioStipula != null ? dettaglioStipula.getOggetto() : "");
			stazione = this.getBandiManager().getDettaglioStazioneAppaltante(dettaglioStipula.getCodiceSa());
			descGenere = "stipula";
		} else {
			if(this.genere == 10) {
				// ELENCO
				DettaglioBandoIscrizioneType elenco = this.getBandiManager().getDettaglioBandoIscrizione(helper.getCodice());
				titolo = (elenco != null ? elenco.getDatiGeneraliBandoIscrizione().getOggetto() : "");
				descGenere = "elenco";
			} else if(this.genere == 20) {
				// CATALOGO
				DettaglioBandoIscrizioneType catalogo = this.getBandiManager().getDettaglioBandoIscrizione(helper.getCodice());
				titolo = (catalogo != null ? catalogo.getDatiGeneraliBandoIscrizione().getOggetto() : "");
				descGenere = "catalogo";
			} else if(this.genere == 4) {
				// ODA
				ContrattoType contratto = this.getContrattiManager().getDettaglioContratto(helper.getCodice());
				titolo = (contratto != null ? contratto.getOggetto() : "");
				descGenere = "contratto";
			} else if(this.genere == 11) {
				// AVVISI
				DettaglioAvvisoType avviso = this.getAvvisiManager().getDettaglioAvviso(helper.getCodice());
				titolo = (avviso != null ? avviso.getDatiGenerali().getOggetto() : "");
				descGenere = "avviso";
			} else {
				titolo = (dettaglioGara != null ? dettaglioGara.getDatiGeneraliGara().getOggetto() : "");
				descGenere = "gara";
			}
		}
		String nomeCliente = (String)this.getAppParamManager().getConfigurationValue(AppParamManager.NOME_CLIENTE);
		boolean isGara = false;
		if (this.genere!= null && (this.genere == 10 || this.genere == 20 || this.genere == 11 )) {
		} else if(this.entita == null){
			isGara = true;
		}
		if(StringUtils.isNotEmpty(this.getCodiceSA())) {
			stazione = this.getBandiManager().getDettaglioStazioneAppaltante(this.getCodiceSA());
		}
		WizardDatiImpresaHelper datiImpresaHelper = helper.getDatiImpresaHelper();
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		// prepare il testo di "comunicazione.pdf"
		StringBuilder contenuto = new StringBuilder();//stazione= new DettaglioStazioneAppaltanteType()
		contenuto.append(StringUtils.isNotEmpty(nomeCliente) ? nomeCliente + "\n\n" : "");
		contenuto.append(StringUtils.isNotEmpty(stazione.getDenominazione()) ? stazione.getDenominazione() + "\n" : "");
		contenuto.append(StringUtils.isNotEmpty(stazione.getIndirizzo()) ? stazione.getIndirizzo() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(stazione.getNumCivico()) ? stazione.getNumCivico() + ", " : "");
		contenuto.append(StringUtils.isNotEmpty(stazione.getCap()) ? stazione.getCap() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(stazione.getComune()) ? stazione.getComune() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(stazione.getProvincia()) ? "(" + stazione.getProvincia() + ")" : "");
		contenuto.append("\n\n");
		
		contenuto.append("Oggetto ").append(descGenere).append(": ").append(StringUtils.isNotEmpty(titolo) ? titolo : "");
		contenuto.append(" - Codice ").append(descGenere).append(": ").append((StringUtils.isNotEmpty(this.codice) ? this.codice : ""));
		if(isGara && StringUtils.isNotEmpty(this.getCodiceCig())) {
			contenuto.append(" - CIG: ").append(this.getCodiceCig());
		}
		contenuto.append("\n\n");
		contenuto.append("Oggetto comunicazione: ").append(helper.getOggetto()).append("\n");
		contenuto.append("\n");
		contenuto.append(helper.getTesto()).append("\n");
		contenuto.append("\n");
		contenuto.append("Allegati");
		contenuto.append("\n");
		contenuto.append(getPdfRiepilogo(helper.getDocumenti()));
		contenuto.append("\n");
		contenuto.append("Operatore economico: ").append(ragioneSociale).append("\n");
		contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " " : "");		
		contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", " : "");
		contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getCapSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getCapSedeLegale() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getComuneSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getComuneSedeLegale() + " " : "");
		contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale()) ? "(" + datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")" : "");
		return contenuto.toString();
	}

    /**
	 * Invia la mail di ricevuta all'operatore economico, indicando
	 * eventualmente i riferimenti della protocollazione ove prevista.
	 * 
	 * @param impresa
	 *            dati impresa
	 * @param comunicazione
	 *            dati della comunicazione
	 * @throws ApsSystemException
	 */
	private void sendMailConfermaImpresa(WizardDatiImpresaHelper impresa, WizardNuovaComunicazioneHelper comunicazione) throws ApsSystemException {
		String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String destinatario = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
		String text = MessageFormat.format(
				this.getI18nLabel("MAIL_INVCOM_RICEVUTA_TESTO"), 
				new Object[] {ragioneSociale, data});
		if (this.isPresentiDatiProtocollazione()) {
			text = MessageFormat.format(
					this.getI18nLabel("MAIL_INVCOM_RICEVUTA_TESTOCONPROTOCOLLO"),
					new Object[] { ragioneSociale, data, this.annoProtocollo.toString(), this.numeroProtocollo });
		}
		
		String subject = MessageFormat.format(this.getI18nLabel(
				"MAIL_INVCOM_RICEVUTA_OGGETTO"),
				new Object[] { comunicazione.getOggetto(), comunicazione.getCodice() });
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { destinatario },
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}
	
	/**
	 * imposta l'allegato della comunicazione 
	 */
	private byte[] setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			WizardNuovaComunicazioneHelper helper) 
	{
		DocumentiComunicazioneHelper documenti = helper.getDocumenti();
		
		int n = documenti.getAdditionalDocs().size() +1;
		if(documenti.getRequiredDocs() != null) {
			n = n + documenti.getRequiredDocs().size();
		}
		
		AllegatoComunicazioneType[] allegati = new AllegatoComunicazioneType[n];
		int i = 0;
		
		// documenti ulteriori
		for(Attachment attachment : documenti.getAdditionalDocs()) {
			allegati[i] = new AllegatoComunicazioneType();
			allegati[i].setDescrizione(attachment.getDesc());
			allegati[i].setNomeFile(attachment.getFileName());
			//allegati[i].setTipo(helper.getDocUlterioriContentType().get(j));
			DocumentiAllegatiFirmaBean firmaBean = attachment.getFirmaBean();
			ApsSystemUtils.getLogger().debug("ProcessPageRiepilogoNuovaComunicazioneAction.setAllegatoComunicazione {},{},{}",attachment.getFileName(), attachment.getDesc(), firmaBean);
			if (firmaBean!=null) {
				allegati[i].setFirmacheck(firmaBean.getFirmacheck() ? "1" : "2");//TODO
				Calendar inst = Calendar.getInstance();
				inst.setTime(firmaBean.getFirmacheckts());
				allegati[i].setFirmacheckts(inst);//TODO
			}
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
		
		// documenti richiesti 
		if(documenti.getRequiredDocs() != null) {
			for(Attachment attachment : documenti.getRequiredDocs()) {
				// cerca la descrizione dell'allegato richiesto...
				String descrizione = null;
				for (DocumentazioneRichiestaType item : helper.getDocumentiRichiesti()) {
					if (attachment.getId() == item.getId()) {
						descrizione = item.getNome();
						break;
					}
				}
				
				allegati[i] = new AllegatoComunicazioneType();
				allegati[i].setId(attachment.getId());
				allegati[i].setNomeFile(attachment.getFileName());
				allegati[i].setDescrizione( descrizione );
				DocumentiAllegatiFirmaBean firmaBean = attachment.getFirmaBean();
				ApsSystemUtils.getLogger().debug("ProcessPageRiepilogoNuovaComunicazioneAction.setAllegatoComunicazione {},{},{}",attachment.getFileName(), attachment.getId(), firmaBean);
				if (firmaBean!=null) {
					allegati[i].setFirmacheck(firmaBean.getFirmacheck() ? "1" : "2");//TODO
					Calendar inst = Calendar.getInstance();
					inst.setTime(firmaBean.getFirmacheckts());
					allegati[i].setFirmacheckts(inst);//TODO
				}
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
		byte[] pdfRiepilogo = null;
		try {
			String contenuto = getPdfRiepilogoComunicazione(helper);
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
							ServletActionContext.getRequest());
			boolean isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A");
			if(isActiveFunctionPdfA) {
				try {
					InputStream iccFilePath = new FileInputStream(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
					ApsSystemUtils.getLogger().info("Trasformazione contenuto in PDF-A");
					pdfRiepilogo = UtilityStringhe.string2PdfA(contenuto,iccFilePath);
				} catch (com.itextpdf.text.DocumentException e) {
					DocumentException de = new DocumentException("Impossibile creare il contenuto in PDF-A.");
					de.initCause(e);
					throw de;
				}
			} else {
				pdfRiepilogo = UtilityStringhe.string2Pdf(contenuto);
			}
			
			
			if(customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE")){
				IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
						.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
								ServletActionContext.getRequest());
				try {
					DigitalTimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(pdfRiepilogo, appParamManager);
					if(resultMarcatura.getResult() == false){
						ApsSystemUtils.getLogger().error(
								"Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage="+ resultMarcatura.getErrorMessage(),
								new Object[] { this.getCurrentUser().getUsername(), "marcaturaTemporale" });
						throw new ApsException("Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage="+ resultMarcatura.getErrorMessage());
					} else {
						pdfRiepilogo = resultMarcatura.getFile();
					}
					AllegatoComunicazioneType pdfMarcatoTemporale = new AllegatoComunicazioneType();
					pdfMarcatoTemporale.setFile(pdfRiepilogo);
					pdfMarcatoTemporale.setNomeFile(NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD);
					pdfMarcatoTemporale.setTipo("tsd");
					pdfMarcatoTemporale.setDescrizione("File di riepilogo comunicazione con marcatura temporale");
					// Aggiungo il file agli allegati della comunicazione
					allegati[allegati.length -1] = pdfMarcatoTemporale;
				}catch(Exception e){
					ApsSystemUtils.logThrowable(e, this, "marcaturaTemporale");
					this.addActionError(this.getText("Errors.marcatureTemporale.generic")); 
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					throw e;
				}
			} else {
				AllegatoComunicazioneType pdfRiepilogoNonMarcato = new AllegatoComunicazioneType();
				pdfRiepilogoNonMarcato.setFile(pdfRiepilogo);
				pdfRiepilogoNonMarcato.setNomeFile(NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF);
				pdfRiepilogoNonMarcato.setTipo("pdf");
				pdfRiepilogoNonMarcato.setDescrizione("File di riepilogo comunicazione");
				// Aggiungo il file agli allegati della comunicazione
				allegati[allegati.length -1] =  pdfRiepilogoNonMarcato;
			}	
		} catch (DocumentException e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (XmlException e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		comunicazione.setAllegato(allegati);
		return pdfRiepilogo;
	}
	
	private static Date retrieveDataNTP(INtpManager ntpManager, BaseAction action, String keyNomeOperazione) {
		Date data = null;
		String nomeOperazioneLog = action.getI18nLabelFromDefaultLocale(keyNomeOperazione).toLowerCase();
		String nomeOperazione = action.getI18nLabel(keyNomeOperazione).toLowerCase();
		try {
			data = ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataNTP", action
					.getTextFromDefaultLocale("Errors.ntpTimeout", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpTimeout",
					new String[] { nomeOperazione }));
		} catch (UnknownHostException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataNTP", action
					.getTextFromDefaultLocale("Errors.ntpUnknownHost", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpUnknownHost",
					new String[] { nomeOperazione }));
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataNTP", action
					.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpUnexpectedError",
					new String[] { nomeOperazione }));
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataNTP", action
					.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpUnexpectedError",
					new String[] { nomeOperazione }));
		}
		return data;
	}
	
	/**
	 * aggiorna altri dati della comunicazione dopo la creazione standard della FS12
	 * NB: da ridefinire nelle classi che ereditano !!!
	 */
	protected DettaglioComunicazioneType integraCreateDettaglioComunicazione(DettaglioComunicazioneType dettComunicazione) {		
		return dettComunicazione;
	}

	private static String getPdfRiepilogo(DocumentiAllegatiHelper documenti) {
		StringBuilder sb = new StringBuilder();

		sb.append(Attachment.toPdfRiepilogo(documenti.getRequiredDocs()));
		sb.append(Attachment.toPdfRiepilogo(documenti.getAdditionalDocs()));

		return sb.length() > 0
			   ? sb.toString()
			   : "Nessun documento allegato";
	}

}
