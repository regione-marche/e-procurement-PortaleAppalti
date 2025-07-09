package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.DelegateUser;
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
import it.maggioli.eldasoft.digitaltimestamp.model.ITimeStampResult;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.JRPdfExporterEldasoft;
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
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * ...
 *
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE, EFlussiAccessiDistinti.COMUNICAZIONE_STIPULA })
public class ProcessPageRiepilogoNuovaComunicazioneAction extends AbstractProcessPageComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3653063448358992202L;

	protected static final String TARGET_REOPEN 		= "reopen";
	protected static final String TARGET_SUCCESS_PAGE 	= "successPage";
	
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
	@Validate(EParamValidation.ACTION)
	protected String from;
	protected Long genere;
	@Validate(EParamValidation.ENTITA)
	protected String entita;

	protected InputStream inputStream;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	protected String msgErrore;
	@Validate(EParamValidation.EMAIL)
	protected String mailUfficioProtocollo;
	protected Boolean allegaDocMailUfficioProtocollo;

	protected Integer tipoProtocollazione;
	protected String numeroProtocollo;
	protected Long annoProtocollo;
	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	protected String stazioneAppaltanteProtocollante;
	protected String dataProtocollo;
	@Validate(EParamValidation.GENERIC)
	protected String codiceSistema;

	@Validate(EParamValidation.UNLIMITED_TEXT)
    protected String applicativo;
	protected Long idComunicazione;
	protected Long idDestinatario;
	protected int pagina;
	@Validate(EParamValidation.GENERIC)
	protected String tipo;
	protected boolean dettaglioPresente;
	protected boolean soccorsoIstruttorio;
	protected boolean rettifica;
	protected Long tipoBusta;
	protected int operazione;
	protected String progressivoOfferta;
	
	
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

	public void setStazioneAppaltanteProtocollante(String stazioneAppaltanteProtocollante) {
		this.stazioneAppaltanteProtocollante = stazioneAppaltanteProtocollante;
	}

	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(String dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}
	
	public Long getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(Long tipoBusta) {
		this.tipoBusta = tipoBusta;
	}
	
	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}
	
	public boolean isRettifica() {
		return rettifica;
	}

	public void setRettifica(boolean rettifica) {
		this.rettifica = rettifica;
	}
	
	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	/**
	 * restituisce la label LABEL_RICHIESTA_CON_ID associata a LAPIS
	 */
	public String getLABEL_RICHIESTA_CON_ID() {
		return MessageFormat.format(this.getI18nLabel("LABEL_RICHIESTA_CON_ID"), new Object[] {dataInvio, numeroProtocollo});
	}

	/**
	 * costruttore 
	 */
	public ProcessPageRiepilogoNuovaComunicazioneAction() {
		this(new WizardNuovaComunicazioneHelper());
	}

	public ProcessPageRiepilogoNuovaComunicazioneAction(WizardNuovaComunicazioneHelper helper) {
		super(helper, PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
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
		setTarget(SUCCESS);

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			helper = (WizardNuovaComunicazioneHelper) getWizardFromSession();

			this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
					helper.getPreviousStepNavigazione(helper.STEP_INVIO_COMUNICAZIONE)));
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return getTarget();
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
		setTarget(SUCCESS);

		if (!hasPermessiInvioFlusso()) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return getTarget();
		}

		boolean isRettifica = (getWizardFromSession() instanceof WizardRettificaHelper);
		
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) getWizardFromSession();

		ComunicazioneType nuovaComunicazione = new ComunicazioneType();
		byte[] pdfRiepilogo = null;
		this.soccorsoIstruttorio = false;

		if (helper != null && PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equals(helper.getEntita())) {
			this.codice2 = helper.getCodice2();
		}

		// calcola la data di invio
		boolean continua = false;
		dataInvio = ProcessPageRiepilogoNuovaComunicazioneAction.retrieveDataNTP(
				ntpManager,
				this,
				"LABEL_COMUNICAZIONI_INVIA_COMUNICAZIONE"
		);
		if (dataInvio == null) {
			setTarget(INPUT);
		} else if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			addActionError(this.getText("Errors.sessionExpired"));
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			continua = true;
		}

		// verifica se la richiesta e' oltre la data di scadenza
		if (continua &&
		   helper.getModello() != null && helper.getModello() > 0 && helper.getDataScadenza() != null)
		{
			continua = dataInvio.before(helper.getDataScadenza());
			if (!continua) {
				//this.addActionError(this.getText("Errors.oltreDataScadenza"));
				setTarget(CommonSystemConstants.PORTAL_ERROR);
				addActionError(getText("Errors.richiestaFuoriTempoMassimo"));

				// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
				Event evento = new Event();
				evento.setUsername(getCurrentUser().getUsername());
				evento.setDestination(helper.getCodice());
				evento.setLevel(Event.Level.ERROR);
				evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
				evento.setIpAddress(getCurrentUser().getIpAddress());
				evento.setSessionId(getRequest().getSession().getId());
				evento.setMessage("Accesso alla funzione soccorso istruttorio (comunicazione con id " + helper.getComunicazioneId() + ")");
				evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO +
										" (" + UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
				eventManager.insertEvent(evento);
			}
		}

		// inizia la fase di invio ed eventuale protocollazione
		boolean inviataComunicazione = false;
		boolean protocollazioneOk = true;
		DettaglioGaraType dettaglioGara = null;
		StipulaContrattoType dettaglioStipula = null;
		Event evento = null;

		if (continua) {
			boolean isContrattoLFS = false;
			boolean isStipula = false;
			boolean isGenericaRiservata = false;
			try {
				dettaglioPresente = ((Boolean) session.get(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE)).booleanValue();
				pagina = ((Integer) session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA)).intValue();
				from = (String) session.get(ComunicazioniConstants.SESSION_ID_FROM);
				tipo = StringUtils.stripToNull((String) session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO));
				codiceSistema = (String) getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
				mailUfficioProtocollo = (String) getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
				allegaDocMailUfficioProtocollo = (Boolean) getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
				stazioneAppaltanteProtocollante = (String) getAppParamManager().getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
				tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				codice2 = (String) session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA);

				applicativo = helper.getComunicazioneApplicativo();
				idComunicazione = helper.getComunicazioneId();
				idDestinatario = helper.getIdDestinatario();
				username = getCurrentUser().getUsername();
				codice = StringUtils.stripToNull((helper.getCodice()));
				soccorsoIstruttorio = helper.isModelloSoccorsoItruttorio();
				rettifica = helper.isModelloRettifica();
				
				isContrattoLFS = PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(helper.getEntita());
				isStipula = PortGareSystemConstants.ENTITA_STIPULA.equalsIgnoreCase(helper.getEntita());
				isGenericaRiservata = PortGareSystemConstants.ENTITA_GENERICA_RISERVATA.equalsIgnoreCase(helper.getEntita());

				dettaglioGara = getBandiManager().getDettaglioGara(helper.getCodice());

				// se necessario, inizializza le info per la protocollazione (codiceSA, stazioneAppaltante, codiceCig)
				setInfoPerProtocollazione(helper);

				// imposta la stazione appaltante per recuperare i parametri dalla configurazione corretta
				getAppParamManager().setStazioneAppaltanteProtocollazione(getCodiceSA());

				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!!
				tipoProtocollazione = getAppParamManager().getTipoProtocollazione();

				// recupera l'info dell'"entita" dalla sessione
				// e verifico se la comunicazione si riferisce ad una "stipula" od un "contratto LFS"
				genere = (Long) session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
				entita = (String) session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);

				// CONTRATTO LFS
				//
				// se trovo una gestione per un "contratto LFS" allora recupero le info relative
				if (isContrattoLFS) {
					genere = null;
					entita = helper.getEntita();
				}

				// NB: in caso "contratto LFS", la protocollazione non e' prevista
				if (PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(entita)) {
					tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				}

				// STIPULA
				//
				// se trovo una gestione per una "stipula" allora recupero le info relative
				if (isStipula) {
					entita = helper.getEntita();
				}

				// se l'"entita" e' una stipula o e' ancora vuota,
				// verifica se esiste un contratto di stipula per il "codice" e recupera il dettaglio della stipula
				if (StringUtils.isNotEmpty(codice)
				    && (PortGareSystemConstants.ENTITA_STIPULA.equalsIgnoreCase(entita) || entita == null))
				{
					// se manca "entita" in sessione, verifico se la comunicazione e' associata ad una STIPULA...
					dettaglioStipula = stipuleManager.getDettaglioStipulaContratto(
							codice,
							getCurrentUser().getUsername(),
							false
					);
					if(dettaglioStipula != null) {
						isStipula = true;
						entita = PortGareSystemConstants.ENTITA_STIPULA;
					}
				}

				// GENERICA RISERVATA
				// NB: in caso di comunicazione generica riservata (w_invcom.comtent='HOMEPG')
				//     la protocollazione non e' prevista!!!
				if (isGenericaRiservata) {
					tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
					entita = PortGareSystemConstants.ENTITA_GENERICA_RISERVATA;
				}
				
				// Concorsi di progettazione
				// NB: in caso di concorsi di progettazione ancora in fase anonima (torn.iterga=9 e torn.fineconc = null) 
				//     la protocollazione NON e' prevista!!!
				if (helper.isConcorsoDiProgettazione() && !helper.isConcEnded())
					tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);

				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if (getAppParamManager().isConfigWSDMNonDisponibile()) {
					continua = false;
					setTarget(INPUT);
					String msgerr = getText("Errors.wsdm.configNotAvailable");
					addActionError(msgerr);
					Event event = new Event();
					event.setUsername(username);
					event.setDestination(codice);
					event.setLevel(Event.Level.ERROR);
					event.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					event.setIpAddress(getCurrentUser().getIpAddress());
					event.setSessionId(getCurrentUser().getSessionId());
					event.setDetailMessage(msgerr);
					event.setMessage("Configurazione non disponibile o vuota");
					eventManager.insertEvent(event);
				}

				if (continua) {
					// recupera il "mittente" della comunicazione
                    // In caso di concorso di progettazione devo recuperare la ragione sociale anonima
                    String mittente = !helper.isConcorsoDiProgettazioneCrypted()
                    		? helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale()
                            : retrieveAnonymousSender(username, codice);
                    
                    // ???????????????????????????????????????????????????????????????
					// Caso: Concorso di progettazione, ma, busta non ancora acquisita
					if (mittente == null && helper.isConcorsoDiProgettazione()) 
						getBandiManager().getIdImpresa(getCurrentUser().getUsername());
					// ???????????????????????????????????????????????????????????????

					if(StringUtils.isNotEmpty(helper.getDestinatario()))
						mittente = helper.getDestinatario();
					
					helper.setMittente(mittente);

					// prepara l'oggetto della comunicazione
					String oggetto = helper.getOggetto();
					if (StringUtils.isNotEmpty(helper.getTipoRichiesta())) {
						LinkedHashMap<String, String> listaTipiRichiesta =  InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPOLOGIE_COMUNICAZIONI);
						if (listaTipiRichiesta != null && !listaTipiRichiesta.isEmpty())
							oggetto = listaTipiRichiesta.get(helper.getTipoRichiesta()) + " - " + helper.getOggetto();
						if (oggetto.length() > 300)
							oggetto = oggetto.substring(300);
					}

					// prepara i dati della nuova comunicazione da inviare al servizio
					DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities.createDettaglioComunicazione(
							null,
							username,
							codice,
							(StringUtils.isNotEmpty(helper.getCodice2()) ? helper.getCodice2() : null),
							helper.getMittente(),
							CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA,
							oggetto,
							helper.getTesto(),
							PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE,
							getDataInvio()
					);

					// in caso di "stipula" o "contratto LFS" aggiorna l'"entita" della nuova comunicazione...
					if (StringUtils.isNotEmpty(entita)) {
						dettComunicazione.setEntita(entita);
					}

					// recupera la comunicazione originaria BO
					// e recupera i dati della comunicazione di risposta (idprgris e idcomris)
					if (helper.getComunicazioneId() != null && helper.getComunicazioneId() > 0) {
						dettComunicazione.setIdRisposta(helper.getComunicazioneId());
						dettComunicazione.setApplicativoRisposta(helper.getComunicazioneApplicativo());
					}

					// per le comunicazioni standard "commodello" dovrebbe sempre essere NULL
					// mentre per le comunicazioni di soccorso istruttorio o rettifica 
					// hanno un valore che viene copiato dalla comunicazione di partenza
					dettComunicazione.setModello(helper.getModello());
					dettComunicazione.setTipoBusta(helper.getTipoBusta());
					if(rettifica) {
						// la cifratura si usa solo in caso di invio rettifica (chiavecifratura != null e chiave sessione)
						dettComunicazione.setSessionKey(helper.getDocumenti()
								.getEncriptedSessionKey(
										codice
										, codice2
										, (helper.getTipoBusta() != null ? helper.getTipoBusta().intValue() : 0)));
					}

					// "comunicazioni generiche" imposta il campo IDCFG.W_INVCOM
					if(isGenericaRiservata)
						dettComunicazione.setStazioneAppaltante(getCodiceSA());

					dettComunicazione = integraCreateDettaglioComunicazione(dettComunicazione);

					nuovaComunicazione.setDettaglioComunicazione(dettComunicazione);

					// FASE 1: invio della comunicazione
					if (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA != tipoProtocollazione
						&& getCodiceSA() == null
					) {
						// aggiorno lo stato per creare l'evento con l'indicazione
						// corretta dello stato, il cui evento conterra' il
						// messaggio di dettaglio con l'errore emesso nell'eccezione
						dettComunicazione.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
						evento = ComunicazioniUtilities.createEventSendComunicazione(
								username,
								codice,
								nuovaComunicazione,
								dataInvio,
								getCurrentUser().getIpAddress(),
								getRequest().getSession().getId()
						);

						// questa eccezione blocca il caso di affidamenti diretti non
						// pubblicati, per i quali e' prevista la protocollazione: vanno
						// per forza pubblicati sul portale altrimenti non si estrae il
						// dettaglio della gara e non e' possibile verificare se per la
						// stazione appaltante va applicata o meno la protocollazione
						throw new ApsException(
								"Bloccato invio comunicazione per operatore " + this.username
								+ " e risposta alla comunicazione con id " + this.idComunicazione
								+ " causa stazione appaltante non impostata o procedura non pubblicata sul portale e protocollazione comunicazione richiesta");
					} else if (!SaveWizardIscrizioneAction.isProtocollazionePrevista(
							tipoProtocollazione,
							stazioneAppaltanteProtocollante,
							getCodiceSA()
					)) {
						// se la protocollazione non e' prevista, si resetta il tipo protocollazione cosi' in seguito
						// il codice testa su un'unica condizione
						tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
					}

					dettComunicazione.setStato(
							tipoProtocollazione.intValue() != PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA
							? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE
							: CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);

					// creazione evento di invio della comunicazione
					evento = ComunicazioniUtilities.createEventSendComunicazione(
							username,
							codice,
							nuovaComunicazione,
							dataInvio,
							getCurrentUser().getIpAddress(),
							getRequest().getSession().getId()
					);

					// caso area personale -> lista ricevute -> rispondi a comunicazione
					if (StringUtils.isNotEmpty(codice) && (null == genere && null == entita)) {
						genere = getBandiManager().getGenere(codice);
						if (dettaglioStipula != null) {
							dettComunicazione.setEntita(PortGareSystemConstants.ENTITA_STIPULA);
						}
					}

					DocumentiComunicazioneHelper documentiComunicazioneHelper = helper.getDocumenti();

					// Aggiunta degli allegati alla nuova comunicazione da inviare
					pdfRiepilogo = setAllegatoComunicazione(
								nuovaComunicazione,
								helper
					);

					// fino ad ora si utilizza this.codice (che dovrebbe sempre arrivare valorizzato),
					// ma se arriva non valorizzato, una volta inizializzati gli oggetti
					// per l'invio si procede al controllo e lo si blocca, mentre la tracciatura dell'evento risulta completa di dati
					if (!isGenericaRiservata && codice == null) {
						throw new ApsException("Bloccato invio comunicazione per operatore " + username +
											   " e risposta alla comunicazione con id " + idComunicazione +
											   " per codice procedura vuoto");
					}

					// invia la comunicazione
					nuovaComunicazione.getDettaglioComunicazione().setId(comunicazioniManager.sendComunicazione(nuovaComunicazione));

					// si rigenera l'evento perche' nel caso di esito positivo si
					// arricchisce il messaggio con l'id della comunicazione (prima
					// dell'invio e' null)
					evento = ComunicazioniUtilities.createEventSendComunicazione(
							username,
							codice,
							nuovaComunicazione,
							dataInvio,
							getCurrentUser().getIpAddress(),
							getRequest().getSession().getId()
					);
					// forzatura perche' lo stato di inserimento e' 3 e non 5 come avviene solitamente
					if (tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
						evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
					}

					if (documentiComunicazioneHelper != null)
						documentiComunicazioneHelper.resetStatiInvio(nuovaComunicazione);

					inviataComunicazione = true;
				}
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "send");
				ExceptionUtils.manageExceptionError(e, this);
				setTarget(CommonSystemConstants.PORTAL_ERROR);
				if(evento != null) evento.setError(e);
			} finally {
				if (evento != null)
					eventManager.insertEvent(evento);
			}

			// FASE 2: ove previsto, si invia alla protocollazione
			if (inviataComunicazione) {
				//recupero l'id della comunicazione
				evento = new Event();
				evento.setUsername(username);
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(getCurrentUser().getIpAddress());
				evento.setSessionId(getRequest().getSession().getId());

				switch (tipoProtocollazione) {
				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
					evento.setMessage("Protocollazione via mail a " + mailUfficioProtocollo);

					boolean mailProtocollazioneInviata = false;
					try {
						// si invia la richiesta di protocollazione via mail
						sendMailUfficioProtocollo(helper, helper.getImpresa());
						mailProtocollazioneInviata = true;
						eventManager.insertEvent(evento);

						// si aggiorna lo stato a 5
						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() + " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
						comunicazioniManager.updateStatoComunicazioni(
								new DettaglioComunicazioneType[] {nuovaComunicazione.getDettaglioComunicazione()},
								CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
						eventManager.insertEvent(evento);
						dataProtocollo = UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
					} catch (Throwable t) {
						evento.setError(t);
						eventManager.insertEvent(evento);
						if (mailProtocollazioneInviata) {
							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							msgErrore = getTextFromDefaultLocale(
									"Errors.updateStatoComunicazione",
									nuovaComunicazione.getDettaglioComunicazione().getId().toString()
							);
							ApsSystemUtils.logThrowable(t, this, "send", msgErrore);
							msgErrore = getText("Errors.updateStatoComunicazione",
									new String[]{nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
							addActionError(msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
							evento.resetDetailMessage();
							eventManager.insertEvent(evento);
							dataProtocollo = UtilityDate.convertiData(
									dataInvio,
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS
							);
						} else {
							ApsSystemUtils.logThrowable(t, this, "send", getText("Errors.sendMailError"));
							ExceptionUtils.manageExceptionError(t, this);
							setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di protocollare
							eliminaComunicazioneInviata(
									CommonSystemConstants.ID_APPLICATIVO,
									nuovaComunicazione.getDettaglioComunicazione().getId()
							);
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
						String codiceProcedura = codice;
						String codiceEntita = PortGareSystemConstants.ENTITA_GARE;	// l'entita di default e' GARE...
						if (isStipula) {
							codiceProcedura = Long.toString(dettaglioStipula.getId());
							codiceEntita = PortGareSystemConstants.ENTITA_STIPULA;
						}

						FascicoloProtocolloType fascicoloBackOffice = getBandiManager().getFascicoloProtocollo(codiceProcedura);
						WSDMLoginAttrType loginAttr = wsdmManager.getLoginAttr();
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
								helper.getImpresa(),
								nuovaComunicazione,
								fascicoloBackOffice,
								dettaglioGara,
								pdfRiepilogo
						);

						ris = wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						annoProtocollo = ris.getAnnoProtocollo();
						numeroProtocollo = ris.getNumeroProtocollo();
						if (IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
							numeroProtocollo = ris.getGenericS11();
						}
						if (ris.getDataProtocollo() != null) {
							setDataProtocollo(UtilityDate.convertiData(
									ris.getDataProtocollo().getTime(),
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						}

						protocollazioneInoltrata = true;
						eventManager.insertEvent(evento);

						// si aggiorna lo stato a 3 aggiornando inoltre anche i dati di protocollazione (WSDOCUMENTO, WSALLEGATI)
						// recupera la comunicazione in modo da ottenere gli id degli allegati
						nuovaComunicazione = comunicazioniManager.getComunicazione(
								nuovaComunicazione.getDettaglioComunicazione().getApplicativo(),
								nuovaComunicazione.getDettaglioComunicazione().getId());

						documento = new WSDocumentoType();
						documento.setEntita(codiceEntita);
						documento.setChiave1(codiceProcedura);
						documento.setNumeroDocumento(ris.getNumeroDocumento());
						documento.setAnnoProtocollo(ris.getAnnoProtocollo());
						documento.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

						// calcola il numero di allegati da gestire...
						// NB: per riservatezza i documenti di una rettifica non vanno mai inviati al protocollo
						int n = (isRettifica 
								? getAllegatiNonCifratiCount(nuovaComunicazione) 
								: nuovaComunicazione.getAllegato().length
						); 
						
						WSAllegatoType[] allegati = new WSAllegatoType[1 + n];
						allegati[0] = new WSAllegatoType();
						allegati[0].setEntita("W_INVCOM");
						allegati[0].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
						allegati[0].setChiave2(nuovaComunicazione.getDettaglioComunicazione().getId().toString());
						int i = 1;
						for (AllegatoComunicazioneType a : nuovaComunicazione.getAllegato()) {
							if(StringUtils.isEmpty(a.getCifrato())) {
								allegati[i] = new WSAllegatoType();
								allegati[i].setEntita("W_DOCDIG");
								allegati[i].setChiave1(nuovaComunicazione.getDettaglioComunicazione().getApplicativo());
								allegati[i].setChiave2(a.getId().toString());
								i++;
							}
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
								getAppParamManager(),
								evento);

						comunicazioniManager.protocollaComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								idComunicazione,
								ris.getNumeroProtocollo(),
								(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataInvio),
								CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA,
								documento,
								allegati
						);

						eventManager.insertEvent(evento);
					} catch (Throwable t) {
						evento.setError(t);
						eventManager.insertEvent(evento);
						if (protocollazioneInoltrata) {

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							msgErrore = getTextFromDefaultLocale(
									"Errors.updateStatoComunicazione",
									String.valueOf(idComunicazione));
							ApsSystemUtils.logThrowable(t, this, "invio", msgErrore);
							msgErrore = getText(
									"Errors.updateStatoComunicazione",
									new String[] { String.valueOf(idComunicazione) }
							);
							addActionError(msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + idComunicazione
									+ " ed inserire inoltre un documento in ingresso per entita' " + documento.getEntita()
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
									getAppParamManager(),
									evento
							);
							evento.resetDetailMessage();
							eventManager.insertEvent(evento);
						} else {
							ApsSystemUtils.logThrowable(
									t,
									this,
									"invio",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							msgErrore = getText("Errors.service.wsdmHandshake");
							ExceptionUtils.manageWSDMExceptionError(t, this);
							setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di
							// protocollare
							eliminaComunicazioneInviata(
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
				evento.setUsername(username);
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage("Invio mail ricevuta di conferma trasmissione comunicazione "
						+ nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione()
						+ " a " + (String) ((IUserProfile) getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(getCurrentUser().getIpAddress());
				evento.setSessionId(getRequest().getSession().getId());

				try {
					sendMailConfermaImpresa(
							helper.getImpresa(),
							helper
					);
				} catch (Throwable t) {
					ApsSystemUtils.getLogger()
						.error("Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di invio offerta per la gara {}.",
							new Object[] {
							getCurrentUser().getUsername(),
							codice}
						);
					msgErrore = getText("Errors.sendMailError");
					ApsSystemUtils.logThrowable(t, this, "invio");
					evento.setError(t);
				} finally {
					eventManager.insertEvent(evento);
				}
			}

			// FASE 4: se invio e protocollazione sono andate a buon fine, anche
			// se la ricevuta all'impresa non e' stata inviata, si procede
			// con la pulizia della sessione
			if (inviataComunicazione && protocollazioneOk) {
				setTarget(TARGET_SUCCESS_PAGE);
				session.remove(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
				session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
			}
			
			// aggiorna i parametri per la navigazione delle jsp
			codice = helper.getCodice();
			codice2 = helper.getCodice2();
			progressivoOfferta = helper.getProgressivoOfferta();
			tipoBusta = helper.getTipoBusta();
			operazione = helper.getOperazione();

			// concludi la protocollazione
			getAppParamManager().setStazioneAppaltanteProtocollazione(null);
		}

		unlockAccessoFunzione();

//		this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA, "APPA")
		return getTarget();
	}

	private String retrieveAnonymousSender(String username, String codice) throws ApsException {
		return getBandiManager().getRagioneSocialeAnonima(
				username
				, codice
		);
	}

	/**
	 * Annulla gli effetti della comunicazione inviata eliminandola.
	 *
	 * @param idComunicazione
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

		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOW.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS12(rup);
			wsdmProtocolloDocumentoIn.setGenericS42( (String)this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_DIVISIONE) );
		}
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
				// allora si invia la property nel campo gia'� valorizzato
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
		WSDMProtocolloAllegatoType[] allegati = createAttachments(comunicazione, contenutoPdf, helper);
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

	/**
	 * crea l'elenco degli allegati da inviare al protocollo 
	 */
	private WSDMProtocolloAllegatoType[] createAttachments(
			ComunicazioneType comunicazione
			, byte[] contenutoPdf
			, WizardNuovaComunicazioneHelper helper
	) throws Exception {
		String v = (String) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
		boolean inTesta = (v != null && "1".equals(v));
		
		// NB: per riservatezza i documenti di una rettifica non vanno mai inviati al protocollo
		boolean isRettifica = (helper instanceof WizardRettificaHelper);
		
		// calcola il numero di allegati da inviare al WSDM
		// NB: in caso di rettifica non si inviano gli allegati cifrati (w_docdig.digkey4 = "crypt")
		int size = (isRettifica ? 0 : comunicazione.getAllegato().length);
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[1 + size];	// <= comunicazione.pdf + X allegati
		
		int n = 0;
		if(inTesta) {
			n++;
		}
		
		if( !isRettifica ) {
			// ...eventuali allegati della comunicazione. 
			// Non allego il file comunicazione. 
			// Lo faccio in seguito perche' deve rispettare l'eventuale ordine voluto dal protocollo (in testa o in coda)
			for (int i = 0; i < comunicazione.getAllegato().length; i++) {
				// ignora "comunicazione.pdf" e tutti gli allegati "crypt"
				if(!comunicazione.getAllegato(i).getNomeFile().equals(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF) 
				   && !comunicazione.getAllegato(i).getNomeFile().equals(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD))
				{
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
		}

		//... inserisci "comunicazione.pdf" nella lista degli allegati (in testa/in coda)
		//... prepara l'allegato "comunicazione.pdf"
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
				.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
						 ServletActionContext.getRequest());

		IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
			.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
					 ServletActionContext.getRequest());
		
		byte[] riepilogoPdf = getRiepilogoPdf(helper);
		
		int n2 = n;
		if(inTesta) {
			n2 = 0;
		}
		
		String tipo = null;
		String nome = null;
		if(customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE")){
			if(contenutoPdf == null){
				ITimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(contenutoPdf, appParamManager);
				if(resultMarcatura.getResult() == false) {
					String errMsg = "Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() 
									+ " ErrorMessage="+ resultMarcatura.getErrorMessage();
					ApsSystemUtils.getLogger().error(errMsg, new Object[] { this.getCurrentUser().getUsername(), "marcaturaTemporale" });
					throw new ApsException(errMsg);
				} else {
					riepilogoPdf = resultMarcatura.getFile();
				}
			} else {
				riepilogoPdf = contenutoPdf;
			}
			tipo = "tsd";
			nome = PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD;
		} else {
			if(contenutoPdf == null) {
				contenutoPdf = getRiepilogoPdf(helper);
			}
			riepilogoPdf = contenutoPdf;
			tipo = "pdf";
			nome = PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF;
		}

		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setTitolo("Testo della comunicazione");
		allegati[n2].setTipo(tipo);
		allegati[n2].setNome(nome);
		allegati[n2].setContenuto(riepilogoPdf);
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId() + "/" + n2);

		ProtocolsUtils.setFieldsForNumix(allegati);

		return allegati;
	}

	/**
	 * crea l'allegato di riepilogo
	 */
	private byte[] getRiepilogoPdf(WizardNuovaComunicazioneHelper helper) throws Exception {
		DettaglioGaraType dettaglioGara = this.getBandiManager().getDettaglioGara(helper.getCodice());
		String titolo = null;
		String codSA = null;
		
		String descGenere = null;
		boolean isGenericaRiservata = (PortGareSystemConstants.ENTITA_GENERICA_RISERVATA.equals(this.entita));

		if (PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equals(this.entita)) {
			// CONTRATTO LFS
			ContrattoLFSType dettaglioContrattoLFS = this.contrattiLFSManager.getDettaglioContrattoLFS(
					this.getCurrentUser().getUsername(),
					helper.getCodice(),
					helper.getCodice2());
			titolo = (dettaglioContrattoLFS != null ? dettaglioContrattoLFS.getOggetto() : "");
			codSA = (dettaglioContrattoLFS != null ? dettaglioContrattoLFS.getCodiceSA() : "");
			descGenere = "contratto LFS";

		} else if (PortGareSystemConstants.ENTITA_STIPULA.equals(this.entita)) {
			// STIPULA CONTRATTO
			StipulaContrattoType dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(
					helper.getCodice(),
					this.getCurrentUser().getUsername(),
					false);
			titolo = (dettaglioStipula != null ? dettaglioStipula.getOggetto() : "");
			codSA = (dettaglioStipula != null ? dettaglioStipula.getCodiceSa() : "");
			descGenere = "stipula";

		} else if (isGenericaRiservata) {
			// GENERICHE RISERVATE
			titolo = helper.getOggetto();
			codSA = this.getCodiceSA();        // W_INVCOM.IDCFG che arriva da BO con la comunicazione ricevuta
			descGenere = "generica";

		} else {
			// GARE, ELENCHI, ETC.
			if (this.genere == 10) {
				// ELENCO
				DettaglioBandoIscrizioneType elenco = this.getBandiManager().getDettaglioBandoIscrizione(helper.getCodice());
				titolo = (elenco != null ? elenco.getDatiGeneraliBandoIscrizione().getOggetto() : "");
				descGenere = "elenco";
			} else if (this.genere == 20) {
				// CATALOGO
				DettaglioBandoIscrizioneType catalogo = this.getBandiManager().getDettaglioBandoIscrizione(helper.getCodice());
				titolo = (catalogo != null ? catalogo.getDatiGeneraliBandoIscrizione().getOggetto() : "");
				descGenere = "catalogo";
			} else if (this.genere == 4) {
				// ODA
				ContrattoType contratto = this.getContrattiManager().getDettaglioContratto(helper.getCodice());
				titolo = (contratto != null ? contratto.getOggetto() : "");
				descGenere = "contratto";
			} else if (this.genere == 11) {
				// AVVISI
				DettaglioAvvisoType avviso = this.getAvvisiManager().getDettaglioAvviso(helper.getCodice());
				titolo = (avviso != null ? avviso.getDatiGenerali().getOggetto() : "");
				descGenere = "avviso";
			} else {
				titolo = (dettaglioGara != null ? dettaglioGara.getDatiGeneraliGara().getOggetto() : "");
				descGenere = "gara";
			}
			codSA = this.getCodiceSA();
		}

		String nomeCliente = (String) this.getAppParamManager().getConfigurationValue(AppParamManager.NOME_CLIENTE);

		boolean isGara = false;
		if (this.genere != null && (this.genere == 10 || this.genere == 20 || this.genere == 11)) {
			// NON SERVE FARE NULLA
		} else if (this.entita == null) {
			isGara = true;
		}

		// recupera la stazione appaltante
		DettaglioStazioneAppaltanteType stazione = null;
		if (StringUtils.isNotEmpty(codSA)) {
			stazione = this.getBandiManager().getDettaglioStazioneAppaltante(codSA);
		}

		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();

		// prepare il testo di "comunicazione.pdf"
		StringBuilder contenuto = new StringBuilder();

		contenuto.append("Riepilogo comunicazione generato il ");
		Date dataOdierna = new Date();
		SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormattata = formatoData.format(dataOdierna);
		SimpleDateFormat formatoOra = new SimpleDateFormat("HH:mm:ss");
		String oraFormattata = formatoOra.format(dataOdierna);
		contenuto.append(dataFormattata).append(" alle ore ").append(oraFormattata).append("\n\n");

		contenuto.append(StringUtils.isNotEmpty(nomeCliente) ? nomeCliente + "\n\n" : "");
		if (stazione != null) {
			contenuto.append(StringUtils.isNotEmpty(stazione.getDenominazione()) ? stazione.getDenominazione() + "\n" : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getIndirizzo()) ? stazione.getIndirizzo() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getNumCivico()) ? stazione.getNumCivico() + ", " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getCap()) ? stazione.getCap() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getComune()) ? stazione.getComune() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getProvincia()) ? "(" + stazione.getProvincia() + ")" : "");
		}
		contenuto.append("\n\n");

		if (!isGenericaRiservata) {
			// per le comunicazioni generiche non e' previsto un codice o un titolo
			contenuto.append("Oggetto ").append(descGenere).append(": ").append(StringUtils.isNotEmpty(titolo) ? titolo : "");
			contenuto.append(" - Codice ").append(descGenere).append(": ").append((StringUtils.isNotEmpty(this.codice) ? this.codice : ""));
		}
		if (isGara && StringUtils.isNotEmpty(this.getCodiceCig())) {
			contenuto.append(" - CIG: ").append(this.getCodiceCig());
		}
		contenuto.append("\n\n");
		contenuto.append("Oggetto comunicazione: ").append(helper.getOggetto()).append("\n");
		contenuto.append("\n");
		contenuto.append(helper.getTesto()).append("\n");
		contenuto.append("\n");
		contenuto.append("Allegati");
		contenuto.append("\n");
		
		//contenuto.append(getAllegatiPdfRiepilogo(helper.getDocumenti()));
		StringBuilder sb = new StringBuilder();
		sb.append(Attachment.toPdfRiepilogo(helper.getDocumenti().getRequiredDocs()));
		sb.append(Attachment.toPdfRiepilogo(helper.getDocumenti().getAdditionalDocs()));
		contenuto.append( (sb.length() > 0 ? sb.toString() : "Nessun documento allegato") );
		contenuto.append("\n");
		
		// mostra le info dell'OE solo se non e' un concorso di progettazione oppure se e' un consorso di progettazione non piu' anonimo !!
		boolean mostraOE = (dettaglioGara == null) 
							|| (("9".equals(dettaglioGara.getDatiGeneraliGara().getIterGara()) && dettaglioGara.getDatiGeneraliGara().isFineConc())
						    || !"9".equals(dettaglioGara.getDatiGeneraliGara().getIterGara()));
		if (mostraOE) {
			contenuto.append("Operatore economico: ").append(ragioneSociale).append("\n");
			contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", " : "");
			contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getCapSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getCapSedeLegale() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getComuneSedeLegale()) ? datiImpresaHelper.getDatiPrincipaliImpresa().getComuneSedeLegale() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale()) ? "(" + datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")" : "");
		}
		
		// prepara il contenuto di "comunicazione.pdf"
		byte[] riepilogoPdf = JRPdfExporterEldasoft.textToPdf(
				contenuto.toString()
				, "Riepilogo comunicazione"
				, this
		);

		return riepilogoPdf;
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
			ComunicazioneType comunicazione
			, WizardNuovaComunicazioneHelper helper
	) {
		DocumentiComunicazioneHelper documenti = helper.getDocumenti();

		// calcola la dimensione della lista di allegati da inviare
		int n = 1 	// per il pdf riepilogo 
				+ (documenti.getAdditionalDocs() != null ? documenti.getAdditionalDocs().size() : 0)
				+ (documenti.getRequiredDocs() != null ? documenti.getRequiredDocs().size() : 0);

		AllegatoComunicazioneType[] allegati = new AllegatoComunicazioneType[n];
		int i = 0;

		// documenti ulteriori
		for(Attachment attachment : documenti.getAdditionalDocs()) {
			try {
				allegati[i] = attachmentToAllegatoComunicazione(attachment, helper, false);
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
				try {
					allegati[i] = attachmentToAllegatoComunicazione(attachment, helper, true);
				} catch (Throwable ex) {
					ApsSystemUtils.logThrowable(ex, this, "setAllegatoComunicazione");
					ExceptionUtils.manageExceptionError(ex, this);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
				i++;
			}
		}

		// aggiungi l'allegato del riepilogo della comunicazione ("comunicazione.pdf")
		byte[] pdfRiepilogo = null; 
		try {
			pdfRiepilogo = addAllegatoPdfRiepilogo(allegati);
		} catch (DocumentException e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "setAllegatoComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		// aggiorna gli allegati della comunicazione
		comunicazione.setAllegato(allegati);
		
		return pdfRiepilogo;
	}
	
	/**
	 * recupera la data ora corrente dal server NTP 
	 */
	protected static Date retrieveDataNTP(INtpManager ntpManager, BaseAction action, String keyNomeOperazione) {
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

	/**
	 * crea ed aggiunge l'allegato per il PDF di riepilogo  
	 */
	private byte[] addAllegatoPdfRiepilogo(AllegatoComunicazioneType[] allegati) throws Exception { 
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
				.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
						ServletActionContext.getRequest());

		boolean isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A", false);
		
		byte[] riepilogoPdf = getRiepilogoPdf(helper);
		
		// aggiungi il pdf di riepilogo con l'eventuale marcatura, all'elenco degli allegati della comunicazione
		if(customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE")){
			IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
							ServletActionContext.getRequest());
			try {
				ITimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(riepilogoPdf, appParamManager);
				if(resultMarcatura.getResult() == false) {
					String errMsg = "Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() 
									+ " ErrorMessage="+ resultMarcatura.getErrorMessage();
					ApsSystemUtils.getLogger().error(errMsg, new Object[] { this.getCurrentUser().getUsername(), "marcaturaTemporale" });
					throw new ApsException(errMsg);
				} else {
					riepilogoPdf = resultMarcatura.getFile();
				}
				AllegatoComunicazioneType pdfMarcatoTemporale = new AllegatoComunicazioneType();
				pdfMarcatoTemporale.setFile(riepilogoPdf);
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
			pdfRiepilogoNonMarcato.setFile(riepilogoPdf);
			pdfRiepilogoNonMarcato.setNomeFile(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF);
			pdfRiepilogoNonMarcato.setTipo(isActiveFunctionPdfA ? "pdf/a" : "pdf");
			pdfRiepilogoNonMarcato.setDescrizione("File di riepilogo comunicazione");
			// Aggiungo il file agli allegati della comunicazione
			allegati[allegati.length - 1] =  pdfRiepilogoNonMarcato;
		}
		
		return riepilogoPdf;
	}

	/**
	 * converte un Attachment in un AllegatoComunicazioneType
	 * @throws Throwable 
	 */
	private AllegatoComunicazioneType attachmentToAllegatoComunicazione(
			Attachment attachment
			, WizardNuovaComunicazioneHelper helper
			, boolean isDocRichiesto
	) throws Throwable {
		// cerca la descrizione dell'allegato richiesto definita in BO...
		String descrizione = attachment.getDesc();
		if(isDocRichiesto)
			descrizione = helper.getDocumentiRichiesti().stream()
				.filter(d -> d.getId() == attachment.getId().longValue())
				.map(DocumentazioneRichiestaType::getNome)
				.findFirst()
				.orElse(null);
		
		ApsSystemUtils.getLogger().debug("ProcessPageRiepilogoNuovaComunicazioneAction.attachmentToAllegatoComunicazione {},{},{}"
										 , attachment.getFileName()
										 , descrizione
										 , attachment.getFirmaBean());
		
		AllegatoComunicazioneType allegato = new AllegatoComunicazioneType();
		allegato.setNomeFile(attachment.getFileName());
		allegato.setTipo(attachment.getContentType());
		allegato.setDescrizione(descrizione);
		if(isDocRichiesto)
			allegato.setId(attachment.getId());
		
		// firmaCheck e firmaCheckTs  
		DocumentiAllegatiFirmaBean firmaBean = attachment.getFirmaBean();
		if (firmaBean != null) {
			allegato.setFirmacheck(firmaBean.getFirmacheck() ? "1" : "2"); 	//TODO
			Calendar c = Calendar.getInstance();
			c.setTime(firmaBean.getFirmacheckts());
			allegato.setFirmacheckts(c); 									//TODO
		}
		
		// recupera lo stream dell'allegato
		// in caso di cifratura recupera lo stream cifrato
		File f = attachment.getFile();
		if(helper.getDocumenti().getChiaveSessione() != null) {
			// cifratura attiva
			// NB: per si gestisce la cifratura solo per le rettifiche
			if(helper instanceof WizardRettificaHelper) {
				f = attachment.getFileCifrati();
				allegato.setCifrato("crypt");
			}
		}
		allegato.setFile(FileUtils.readFileToByteArray(f));
		
		return allegato;
	}

	/**
	 * retstituisce il totale degli allegati NON "cifrati" presenti in una comunicazione
	 */
	private int getAllegatiNonCifratiCount(ComunicazioneType comunicazione) {
		int n = 0;
		for (AllegatoComunicazioneType a : comunicazione.getAllegato())
			if(StringUtils.isEmpty(a.getCifrato()))		// w_docdig.digkey4 != 'crypt'
				n++;
		return n;
	}

}
