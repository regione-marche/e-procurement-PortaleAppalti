package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
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
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampResult;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
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
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageProtocollazioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
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
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
 * Action di gestione delle operazioni nella pagina dei documenti di rinnovo
 * iscrizione ad un catologo/elenco
 *
 * @author Marco.Perazzetta
 */
public class RinnovoAction extends AbstractProcessPageAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5144905320354779433L;

	@Validate(EParamValidation.ACTION)
	private String nextResultAction;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IComunicazioniManager comunicazioniManager;
	private INtpManager ntpManager;
	private IBandiManager bandiManager;
	private IMailManager mailManager;
	private IAppParamManager appParamManager;
	private ICataloghiManager cataloghiManager;
	private IWSDMManager wsdmManager;
	private IEventManager eventManager;
	private ICustomConfigManager customConfigManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private Date dataInvio;
	private int tipoElenco;

	private Integer tipoProtocollazione;
	@Validate(EParamValidation.EMAIL)
    private String mailUfficioProtocollo;
    private Boolean allegaDocMailUfficioProtocollo;
	@Validate(EParamValidation.GENERIC)
    private String msgErrore;
	@Validate(EParamValidation.DIGIT)
	private String numeroProtocollo;
	private Long annoProtocollo;
	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltanteProtocollante;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataProtocollo;
	@Validate(EParamValidation.GENERIC)
	private String codiceSistema;

	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}

	public String getMsgErrore() {
		return msgErrore;
	}

	public void setMsgErrore(String msgErrore) {
		this.msgErrore = msgErrore;
	}

	public int getTipoElenco() {
		return tipoElenco;
	}

	public void setTipoElenco(int tipoElenco) {
		this.tipoElenco = tipoElenco;
	}
	
	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}

	public void setAnnoProtocollo(Long annoProtocollo) {
		this.annoProtocollo = annoProtocollo;
	}
	
	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(String dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}
	
	public String getCodiceSistema() {
		return codiceSistema;
	}

	public void setCodiceSistema(String codiceSistema) {
		this.codiceSistema = codiceSistema;
	}

	public boolean isPresentiDatiProtocollazione() {
		return (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) && 
		        this.annoProtocollo != null && this.numeroProtocollo != null;
	}

	public int getTIPOLOGIA_ELENCO_STANDARD() {
		return PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD;
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
	public String back() {
		String target = SUCCESS;

		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		this.nextResultAction = rinnovoHelper.getPreviousAction(WizardRinnovoHelper.STEP_PRESENTA_ISCRIZIONE);
		return target;
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
	public String cancel() {
		return "cancel";
	}
	
	/**
	 * ...
	 */
	public String rinnovo() {
		String target = INPUT;
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if (rinnovoHelper == null) {
//			// la sessione e' scaduta, occorre riconnettersi
//			action.addActionError(action.getText("Errors.sessionExpired"));
//			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			String prefissoLabel = "NOTIFICA_RINNOVO";	
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			DettaglioBandoIscrizioneType bando = null;
			ComunicazioneType nuovaComunicazione = null;
			String username = this.getCurrentUser().getUsername();
			String tipoRinnovoDefaultLocale = rinnovoHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
					? this.getI18nLabelFromDefaultLocale("LABEL_ALL_ELENCO") 
					: this.getI18nLabelFromDefaultLocale("LABEL_AL_MERCATO_ELETTRONICO");
			String tipoRinnovo = rinnovoHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
					? this.getI18nLabel("LABEL_ALL_ELENCO") 
					: this.getI18nLabel("LABEL_AL_MERCATO_ELETTRONICO");

			WizardDatiImpresaHelper datiImpresaHelper = rinnovoHelper.getImpresa();
			String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();

			boolean continua = true;
			try {
				// FASE 1: invio della comunicazione
				
				// per prima cosa si estrae la data NTP dell'istante in cui 
				// avviene la richiesta
				this.dataInvio = retrieveDataInvio(this.ntpManager, this, "BUTTON_ISCRALBO_RINNOVO_ISCRIZIONE");
	
				this.codiceSistema = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
				this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
				this.allegaDocMailUfficioProtocollo = (Boolean)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
				this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
				this.tipoProtocollazione = Integer.valueOf(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				
				this.codice = rinnovoHelper.getIdBando();
				
				this.setTipoElenco(rinnovoHelper.getTipologia());

				if (rinnovoHelper.getTipoElenco() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD) {
					bando = bandiManager.getDettaglioBandoIscrizione(rinnovoHelper.getCodice());
				} else {
					bando = cataloghiManager.getDettaglioCatalogo(rinnovoHelper.getCodice());
				}
				
				rinnovoHelper.setDescBando(bando.getDatiGeneraliBandoIscrizione().getOggetto());
				
				String stazioneAppaltanteProcedura = bando.getStazioneAppaltante().getCodice();
				
				this.appParamManager.setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
				
				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!! 
				this.tipoProtocollazione = (Integer) this.appParamManager.getTipoProtocollazione(stazioneAppaltanteProcedura);
				
				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.appParamManager.isConfigWSDMNonDisponibile()) {
					continua = false;
					target = INPUT;
					String msgerr = this.getText("Errors.wsdm.configNotAvailable");
					this.addActionError(msgerr);
					Event event = new Event();
					event.setUsername(this.getCurrentUser().getUsername());
					event.setDestination(bando.getStazioneAppaltante().getCodice());
					event.setLevel(Event.Level.ERROR);
					event.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					event.setIpAddress(this.getCurrentUser().getIpAddress());
					event.setSessionId(this.getCurrentUser().getSessionId());
					event.setMessage("Configurazione non disponibile o vuota");
					event.setDetailMessage(msgerr);
					this.eventManager.insertEvent(event);
				}

				boolean isActiveFunctionPdfA = false;
				InputStream iccFilePath = null;
				if(continua) {
					isActiveFunctionPdfA = false;
					try {
						isActiveFunctionPdfA = this.customConfigManager.isActiveFunction("PDF", "PDF-A");
					} catch (Exception e1) {
						throw new ApsException(e1.getMessage(), e1);
					}
					iccFilePath = null;
					if(isActiveFunctionPdfA) {
						//iccFilePath = new FileInputStream(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
						iccFilePath = new ByteArrayInputStream(FileUtils.readFileToByteArray(
								new File(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH))));
					}
					// completa l'invio della domanda di rinnovo
					nuovaComunicazione = sendComunicazione(
							this.tipoProtocollazione,	// completamento ed invio della comunicazione
							rinnovoHelper,
							this.session, 
							this,
							this.dataInvio, 
							isActiveFunctionPdfA, 
							iccFilePath);
					
					if(nuovaComunicazione != null) {
						inviataComunicazione = true;
						target = "successPage";
					}
				}
			
				// FASE 2: ove previsto, si invia alla protocollazione
				if (inviataComunicazione) {
					Event evento = new Event();
					evento.setUsername(username);
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
							this.setMailRichiestaUfficioProtocollo(rinnovoHelper, datiImpresaHelper, tipoRinnovoDefaultLocale);
							mailProtocollazioneInviata = true;
							this.eventManager.insertEvent(evento);

							// si aggiorna lo stato a 5
							evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
							evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() + " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							this.comunicazioniManager.updateStatoComunicazioni(new DettaglioComunicazioneType[] {nuovaComunicazione.getDettaglioComunicazione()}, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							this.eventManager.insertEvent(evento);
							this.setDataProtocollo(UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						} catch (Throwable t) {
							if (mailProtocollazioneInviata) {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								// segnalo l'errore, comunque considero la
								// protocollazione andata a buon fine e segnalo nel
								// log e a video che va aggiornato a mano lo stato
								// della comunicazione
								this.msgErrore = this.getText(
										"Errors.updateStatoComunicazioneDaProcessare",
										new String[]{nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
								ApsSystemUtils.logThrowable(t, this, "send", this.getTextFromDefaultLocale(
										"Errors.updateStatoComunicazioneDaProcessare",
										nuovaComunicazione.getDettaglioComunicazione().getId().toString()));
								this.addActionError(this.msgErrore);

								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
								this.setDataProtocollo(UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
							} else {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								ApsSystemUtils.logThrowable(t, this, "send", "Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
								ExceptionUtils.manageExceptionError(t, this);
								target = INPUT;
								// si deve annullare l'invio che si e' tentato di protocollare
								this.annullaComunicazioneInviata(
										CommonSystemConstants.ID_APPLICATIVO, 
										nuovaComunicazione.getDettaglioComunicazione().getId(),
										rinnovoHelper.getDescBando(),
										rinnovoHelper);
								protocollazioneOk = false;
							}
						}
						break;

					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
						evento.setMessage("Protocollazione via WSDM");

						boolean protocollazioneInoltrata = false;
						WSDocumentoType documento = null; 
						WSDMProtocolloDocumentoType ris = null;
						try {
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

							WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = creaInputProtocollazioneWSDM(
									rinnovoHelper,
									datiImpresaHelper, 
									prefissoLabel,
									fascicoloBackOffice, 
									nuovaComunicazione, 
									bando, 
									isActiveFunctionPdfA, 
									iccFilePath);

							ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
							this.annoProtocollo = ris.getAnnoProtocollo();
							this.numeroProtocollo = ris.getNumeroProtocollo();
							if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
								this.numeroProtocollo = ris.getGenericS11();
							}
							if (ris.getDataProtocollo() != null) {
								this.setDataProtocollo(UtilityDate.convertiData(ris.getDataProtocollo().getTime(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
							}
							protocollazioneInoltrata = true;
							this.eventManager.insertEvent(evento);

							// si aggiorna lo stato a 5 aggiornando inoltre anche i dati di protocollazione
							long id = nuovaComunicazione.getDettaglioComunicazione().getId();

							documento = new WSDocumentoType();
							documento.setEntita("GARE");
							documento.setChiave1(rinnovoHelper.getIdBando());
							documento.setNumeroDocumento(ris.getNumeroDocumento());
							documento.setAnnoProtocollo(ris.getAnnoProtocollo());
							documento.setNumeroProtocollo(ris.getNumeroProtocollo());
							documento.setVerso(WSDocumentoTypeVerso.IN);
							documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

							evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
							evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId()
									+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
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
									id,
									ris.getNumeroProtocollo(),
									(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataInvio),
									CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
									documento);
							
							this.eventManager.insertEvent(evento);
						} catch (Throwable t) {
							if (protocollazioneInoltrata) {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								// segnalo l'errore, comunque considero la
								// protocollazione andata a buon fine e segnalo nel
								// log e a video che va aggiornato a mano lo stato
								// della comunicazione
								this.msgErrore = this.getText(
										"Errors.updateStatoComunicazioneDaProcessare",
										new String[] {nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
								ApsSystemUtils.logThrowable(t, this, "send", this.getTextFromDefaultLocale(
										"Errors.updateStatoComunicazioneDaProcessare",
										nuovaComunicazione.getDettaglioComunicazione().getId().toString()));
								this.addActionError(this.msgErrore);

								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId()
										+ " ed inserire inoltre un documento in ingresso per entita' " + documento.getEntita()
										+ ", chiave1 " + documento.getChiave1()
										+ ", oggetto " + documento.getOggetto()
										+ ", numero documento " + documento.getNumeroDocumento()
										+ ", anno protocollo " + documento.getAnnoProtocollo()
										+ " e numero protocollo " + documento.getNumeroProtocollo());
								ProcessPageProtocollazioneAction.lapisAggiornaMessaggioEventoCompletamentoManuale(
										ris,
										documento,
										nuovaComunicazione.getDettaglioComunicazione().getId(),
										this.appParamManager,
										evento);
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
							} else {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								ApsSystemUtils.logThrowable(t, this, "send", "Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
								this.msgErrore = this.getText("Errors.service.wsdmHandshake");
								ExceptionUtils.manageWSDMExceptionError(t, this);
								target = INPUT;
								// si deve annullare l'invio che si e' tentato di protocollare
								this.annullaComunicazioneInviata(
										CommonSystemConstants.ID_APPLICATIVO, 
										nuovaComunicazione.getDettaglioComunicazione().getId(),
										rinnovoHelper.getDescBando(),
										rinnovoHelper);
								protocollazioneOk = false;
							}
						}
						break;
					default:
						break;
					}
				}

				
				// FASE 3: se gli step precedenti sono andati a buon fine, 
				//         si invia la ricevuta all'impresa
				if (inviataComunicazione && protocollazioneOk) {
					Event evento = new Event();
					evento.setUsername(username);
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
					evento.setMessage("Invio mail ricevuta di conferma trasmissione comunicazione " + nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione() + " a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
					evento.setIpAddress(this.getCurrentUser().getIpAddress());

					try {
						this.sendMailConfermaImpresa(ragioneSociale, tipoRinnovo, rinnovoHelper.getDescBando());
					} catch (Throwable t) {
						ApsSystemUtils.getLogger()
							.error("Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di iscrizione per {}.",
								new Object[] { this.getCurrentUser().getUsername(), this.codice});
						this.msgErrore = this.getText("Errors.sendMailError");
						ApsSystemUtils.logThrowable(t, this, "send");
						evento.setError(t);
					} finally {
						this.eventManager.insertEvent(evento);
					}
				}
				
				// FASE 4: se invio e protocollazione sono andate a buon fine, anche se la ricevuta
				// all'impresa non e' stata inviata, si procede con la pulizia della sessione
				if (inviataComunicazione && protocollazioneOk) {
					// pulizia e impostazione navigazione futura
					// se tutto e' andato a buon fine si eliminano
					// le informazioni dalla sessione ...
					this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
					this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				}
				
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "rinnovo"); 
				ExceptionUtils.manageExceptionError(e, this);
			}
			
			// concludi la protocollazione
			this.appParamManager.setStazioneAppaltanteProtocollazione(null);
		}
			
		return target;
	}
	
	/**
	 * salva/aggiorna un allegato relativo alla domanda di rinnovo 
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 */
	public static String saveDocumenti(
			WizardRinnovoHelper helper,
			Map<String, Object> session,
			BaseAction action,
			Date dataInvio, boolean isActiveFunctionPdfA, InputStream iccFilePath) 
	{
		ComunicazioneType comunicazione = sendComunicazione(
				null, 	// aggiornamento comunicazione per caricamento documenti 
				helper, 
				session, 
				action,
				dataInvio, isActiveFunctionPdfA, iccFilePath);
		return (comunicazione != null ? SUCCESS : INPUT);
	}
	
	/**
	 * Inserimento/aggiornamento della comunicazione di rinnovo 
	 * (aggiunta documento o completamento domanda di rinnovo) 
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 */
	private static ComunicazioneType sendComunicazione(
			Integer tipoProtocollazione,
			WizardRinnovoHelper helper,
			Map<String, Object> session,
			BaseAction action,
			Date dataInvio, 
			boolean isActiveFunctionPdfA, 
			InputStream iccFilePath) 
	{
		String target = INPUT;
		
		boolean controlliOk = true;
		ComunicazioneType nuovaComunicazione = null;
		try {
			IEventManager eventManager = (IEventManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.EVENTI_MANAGER,
						 ServletActionContext.getRequest());
			
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.BANDI_MANAGER,
						 ServletActionContext.getRequest());

			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER,
						 ServletActionContext.getRequest());

//			INtpManager ntpManager = (INtpManager) ApsWebApplicationUtils
//				.getBean(PortGareSystemConstants.NTP_MANAGER,
//						 ServletActionContext.getRequest());
//			
//			IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
//				.getBean(PortGareSystemConstants.APPPARAM_MANAGER,
//						 ServletActionContext.getRequest());
		
			String username = action.getCurrentUser().getUsername();
//			String codice = helper.getIdBando();
			String tipoRinnovoDefaultLocale = helper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
					? action.getI18nLabelFromDefaultLocale("LABEL_ALL_ELENCO") 
					: action.getI18nLabelFromDefaultLocale("LABEL_AL_MERCATO_ELETTRONICO");
			String tipoRinnovo = helper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
					? action.getI18nLabel("LABEL_ALL_ELENCO") 
					: action.getI18nLabel("LABEL_AL_MERCATO_ELETTRONICO");
			
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
			String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
			
			// memorizzo una data non significativa in quanto si tratta di bozza
			// (la data comunque serve in quanto il campo e' obbligatorio nel
			// tracciato xml da inviare)
			helper.setDataPresentazione(dataInvio);

//			DettaglioBandoIscrizioneType bando = null;
//			if (documentiRinnovoHelper.getTipoElenco() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD) {
//				bando = bandiManager.getDettaglioBandoIscrizione(documentiRinnovoHelper.getCodice());
//			} else {
//				bando = cataloghiManager.getDettaglioCatalogo(documentiRinnovoHelper.getCodice());
//			}
			
			if (!bandiManager.isImpresaAbilitataRinnovo(helper.getCodice(), username)) {
				controlliOk = false;
				action.addActionError(action.getText("Errors.invioRinnovo.impresaNonAbilitata",
									  new String[]{ragioneSociale, tipoRinnovo, helper.getCodice()}));
			}
			
			// NB: finche' non si completa il wizard il salvataggio della 
			// comunicazione deve essere in stato BOZZA,
			// questo perche' ogni volta che si aggiunge 1 documento si registra 
			// subito la comunicazione che va salvata in stato BOZZA,
			// solo al termine del wizard si aggiorna la comunicazione come 
			// DA PROCESSARE o DA PROTOCOLLARE
			boolean completaInvio = (tipoProtocollazione != null);
			
			String statoInvioRinnovo = CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA;
			if(completaInvio) {
				statoInvioRinnovo = (tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA 
									 ? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
									 : CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
			}
			
			if (controlliOk) {
				// verifica se � gi� stata inviata la comunicazione (stato DA PROCESSARE)  
				DettaglioComunicazioneType dettComunicazioneRinnovoInviata = ComunicazioniUtilities
					.retrieveComunicazione(
						comunicazioniManager,
						username,
						helper.getCodice(), 
						null,
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE);
				
				if (dettComunicazioneRinnovoInviata != null) {
					action.addActionMessage(action.getText("Errors.invioRinnovo.alreadySent",
										    new String[]{tipoRinnovo, helper.getCodice()}));
					controlliOk = false;
				}
			}

			// NB: il check dei documenti va effettuato solo al completamento del wizard
			//     non serve in fase di aggiunta o rimozione di ogni allegato 
			if (completaInvio) {
				if(controlliOk) {
					controlliOk = checkDocumenti(
							helper,
							datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(),
							action);
				}
			}
			
			if (controlliOk) {
				// lo stato della comunicazione inviata dipende da
				//  - aggiunta/rimozione di un documento    (stato = BOZZA)
				//  - conferma ed invio del rinnovo  		(stato = DA PROCESSARE o DA PROTOCOLLARE)
				nuovaComunicazione = sendComunicazioneRinnovo(
						helper, 
						username, 
						ragioneSociale, 
						tipoRinnovoDefaultLocale,
						statoInvioRinnovo,
						comunicazioniManager,
						eventManager,
						action, 
						isActiveFunctionPdfA, 
						iccFilePath);
				target = SUCCESS;
				
				// ogni volta che si invia questa comunicazione viene fatto
				// per allegare/rimuovere un documento oppure per completare ed 
				// inviare la domanda di rinnovo...
				// nel caso di allega/rimuovi un documento significa che la 
				// comunicazione � ancora in stato BOZZA e quindi si memorizza
				// l'idComunicazione nello helper... 
				if(!completaInvio && nuovaComunicazione != null) {
					helper.setIdComunicazioneBozza(nuovaComunicazione.getDettaglioComunicazione().getId());
				}
			}
		} catch (OutOfMemoryError e) {
			ApsSystemUtils.logThrowable(e, action, "sendComunicazione");
			action.addActionError(action.getText("Errors.send.outOfMemory"));
			target = INPUT;
		} catch (IndexOutOfBoundsException e) {
			ApsSystemUtils.logThrowable(e, action, "sendComunicazione");
			ExceptionUtils.manageExceptionError(e, action);
			target = INPUT;
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, action, "sendComunicazione");
			action.addActionError(action.getText("Errors.cannotLoadAttachments"));
			target = INPUT;
		} catch (ApsException e) {
			ApsSystemUtils.getLogger().error(
					"Per errori durante la connessione al server di posta, non e'' stato possibile "
					+ "inviare all''impresa {} la ricevuta della richiesta di rinnovo iscrizione.",
					new Object[]{action.getCurrentUser().getUsername()});
//			action.setMsgErrore(action.getText("Errors.sendMailError"));
			ApsSystemUtils.logThrowable(e, action, "sendComunicazione");
			ExceptionUtils.manageExceptionError(e, action);
			target = INPUT;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, action, "sendComunicazione");
			ExceptionUtils.manageExceptionError(e, action);
			target = INPUT;
		}
		
		return (SUCCESS.equals(target) ? nuovaComunicazione : null);
	}
	
	/**
	 * invio della comunicazione di rinnovo FS3 
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 * @throws Throwable 
	 */
	private static ComunicazioneType sendComunicazioneRinnovo(
			WizardRinnovoHelper rinnovoHelper,
			String username, 
			String ragioneSociale, 
			String tipoRinnovo, 
			String stato,
			IComunicazioniManager comunicazioniManager,
			IEventManager eventManager,
			BaseAction action, 
			boolean isActiveFunctionPdfA, 
			InputStream iccFilePath) 
		throws Throwable 
	{
		Event evento = new Event();
		ComunicazioneType comunicazione = null;
		evento.setUsername(username);
		evento.setDestination(rinnovoHelper.getIdBando()); //this.codice);
		evento.setMessage("Invio comunicazione " + PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE);
		evento.setLevel(Event.Level.INFO);
		evento.setIpAddress(action.getCurrentUser().getIpAddress());
		evento.setSessionId(action.getRequest().getSession().getId());
		
		if (CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE.equals(stato)) {
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROTOCOLLARE);
		} else {
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		}
		
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String oggetto = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_RINNOVO_OGGETTO"), 
						new Object[] {tipoRinnovo, rinnovoHelper.getCodice()});
		String testo = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_RINNOVO_TESTO"), 
						new Object[] {ragioneSociale200, tipoRinnovo, rinnovoHelper.getCodice()});
		String descrizioneFile = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_RINNOVO_ALLEGATO_DESCRIZIONE"), 
						new Object[] {ragioneSociale200, tipoRinnovo, rinnovoHelper.getCodice()});
				
		try {
			synchronized (rinnovoHelper) {
				Long idComunicazione = rinnovoHelper.getIdComunicazione();
				if(rinnovoHelper.getIdComunicazioneBozza() != null && rinnovoHelper.getIdComunicazioneBozza() > 0) {
					// se e' presente la comunicazione in stato BOZZA 
					// recupera l'id comunicazione corretto per l'aggiornamento
					idComunicazione = rinnovoHelper.getIdComunicazioneBozza();
				}
				
				// FASE 1: costruzione del contenitore della comunicazione
				comunicazione = new ComunicazioneType();
		
				// FASE 2: popolamento della testata
				DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.createDettaglioComunicazione(
							idComunicazione,
							username, 
							rinnovoHelper.getCodice(), 
							null,
							ragioneSociale,
							stato,
							oggetto, 
							testo, 
							PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE, 
							null);
				comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
		
				setAllegatoComunicazione(
						comunicazione, 
						rinnovoHelper,  
						PortGareSystemConstants.NOME_FILE_RINNOVO_ISCRIZIONE, 
						descrizioneFile);
				
				// Il file di riepilogo viene creato e allegato solo al moneto in cui lo stato della comunicazione e' da processare, 
				// oppure, se attivata la protocollazione in stato da protocollare (che poi verra' aggiornato in stato da processare
				// durante la protocollazione)
				if(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(stato)
				   || CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE.equals(stato))
				{
						byte[] pdfRiepilogo = trasformaStringaInPdf(getPdfRiepilogo(rinnovoHelper.getDocumenti()), isActiveFunctionPdfA, iccFilePath);
						
						ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
							.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
									 ServletActionContext.getRequest());
						
						IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
							.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
									 ServletActionContext.getRequest());
						
						AllegatoComunicazioneType[] allegati = comunicazione.getAllegato();
						if(customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE")){
							try{
								DigitalTimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(pdfRiepilogo, appParamManager);
								if(resultMarcatura.getResult() == false){
									ApsSystemUtils.getLogger().error(
											"Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage="+ resultMarcatura.getErrorMessage(),
											new Object[] { action.getCurrentUser().getUsername(), "marcaturaTemporale" });
									throw new ApsException("Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage="+ resultMarcatura.getErrorMessage());
								} else {
									pdfRiepilogo = resultMarcatura.getFile();
								}
								AllegatoComunicazioneType pdfMarcatoTemporale = new AllegatoComunicazioneType();
								pdfMarcatoTemporale.setFile(pdfRiepilogo);
								pdfMarcatoTemporale.setNomeFile(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE);
								pdfMarcatoTemporale.setTipo("tsd");
								pdfMarcatoTemporale.setDescrizione("File di riepilogo allegati con marcatura temporale");
								// Aggiungo il file agli allegati della comunicazionw
								AllegatoComunicazioneType[] newAllegati = new AllegatoComunicazioneType[allegati.length+1];
								for (int i = 0; i < allegati.length; i++) {
									newAllegati[i] = allegati[i];
								}
								newAllegati[allegati.length] = pdfMarcatoTemporale;
								comunicazione.setAllegato(newAllegati);
							}catch(Exception e){
								ApsSystemUtils.logThrowable(e, action, "marcaturaTemporale");
								action.addActionError(action.getText("Errors.marcatureTemporale.generic")); 
								throw e;
							}
						} else {
							AllegatoComunicazioneType pdfRiepilogoNonMarcato = new AllegatoComunicazioneType();
							pdfRiepilogoNonMarcato.setFile(pdfRiepilogo);
							pdfRiepilogoNonMarcato.setNomeFile(PortGareSystemConstants.FILENAME_RIEPILOGO);
							pdfRiepilogoNonMarcato.setTipo("pdf");
							pdfRiepilogoNonMarcato.setDescrizione("File di riepilogo allegati");
							// Aggiungo il file agli allegati della comunicazionw
							AllegatoComunicazioneType[] newAllegati = new AllegatoComunicazioneType[allegati.length+1];
							for (int i = 0; i < allegati.length; i++) {
								newAllegati[i] = allegati[i];
							}
							newAllegati[allegati.length] = pdfRiepilogoNonMarcato;
							comunicazione.setAllegato(newAllegati);
						}	
				}
				// FASE 4: invio comunicazione
				Long idCom = comunicazioniManager.sendComunicazione(comunicazione);
				comunicazione.getDettaglioComunicazione().setId(idCom);
				
				// visto l'esito con successo si arricchisce il messaggio di dettagli
				evento.setMessage("Invio comunicazione "
						+ PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE 
						+ " con id " + comunicazione.getDettaglioComunicazione().getId() 
						+ " in stato " + comunicazione.getDettaglioComunicazione().getStato()
//						+ " per l'impresa "
//						+ rinnovoHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale() 
						+ " e timestamp ntp "
						+ UtilityDate.convertiData(rinnovoHelper.getDataPresentazione(),
								UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));	
				
				rinnovoHelper.getDocumenti().resetStatiInvio(comunicazione);
			}			
		} catch (IOException e) { 
			evento.setError(e);
			throw e;
		} catch (Throwable e) {
			evento.setError(e);
			throw e;
		} finally {
			eventManager.insertEvent(evento);
		}
		
		return comunicazione;
	}
	
	/**
	 * imposta gli allegati della comunicazione
	 */
	private static void setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			WizardRinnovoHelper helper,
			String nomeFile, 
			String descrizioneFile) throws IOException 
	{	
		HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
		suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// inserisci il documento xml come I allegato della comunicazione 
		// e tutti i documenti dell'xml come allegati della comunicazione
		List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();

		AllegatoComunicazioneType allegato = ComunicazioniUtilities
			.createAllegatoComunicazione(nomeFile, descrizioneFile, null);
		allegatiXml.add(allegato);
		
		// aggiungi i documenti come allegati della comunicazione 
		helper.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
		
	    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
		XmlObject xmlDocument = helper.getXmlDocument(true);
	    xmlDocument.save(baos, opts);
	    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
	    
		comunicazione.setAllegato(
				allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]) );
	}
	
	/**
	 * recupera il file ed il contenuto dell'allegato in area temporanea 
	 */
	private File getStreamDocumento(DocumentiAllegatiHelper documenti, int id, boolean docRichiesto) throws ApsSystemException {
		File f = (docRichiesto 
				  ? documenti.getRequiredDocs().get(id).getFile()
				  : documenti.getAdditionalDocs().get(id).getFile());
		// se non esiste lo stream, scaricalo in area temporanea...
		try {
			if(f == null) {
				if(docRichiesto) {
					documenti.downloadDocRichiesto(id);
					f = documenti.getRequiredDocs().get(id).getFile();
				} else {
					documenti.downloadDocUlteriore(id);
					f = documenti.getAdditionalDocs().get(id).getFile();
				}
			}
		} catch (Throwable t) {
			throw new ApsSystemException(t.getMessage());
		}
		return f;
	}

	/**
	 * invio della mail di richiesta protocollazione
	 */
	private void setMailRichiestaUfficioProtocollo(
			WizardRinnovoHelper helper, 
			WizardDatiImpresaHelper datiImpresaHelper, 
			String tipoRinnovo) throws ApsSystemException 
	{
		if (1 == this.tipoProtocollazione) {
			if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
				throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}
			
			WizardDocumentiHelper documentiHelper = helper.getDocumenti();
			
			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio

			Properties p = null;

			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.allegaDocMailUfficioProtocollo) {
				// si predispongono gli allegati da inserire
				if (documentiHelper.getRequiredDocs().size()
					+ documentiHelper.getAdditionalDocs().size() > 0)
				{
					p = new Properties();
					for (Attachment attachment : documentiHelper.getRequiredDocs()) {
//						File f = getStreamDocumento(documentiHelper, i, true);
						p.put(attachment.getFileName(), attachment.getFile().getAbsolutePath());
					}
					for (Attachment attachment : documentiHelper.getAdditionalDocs()) {
						if (StringUtils.isEmpty(attachment.getNascosto()))
//						if (!documentiHelper.getDocUlterioriNascosti()
//							 .contains(documentiHelper.getDocUlterioriDesc().get(i)))
						{
//							File f = getStreamDocumento(documentiHelper, i, false);
							p.put(attachment.getFileName(), attachment.getFile().getAbsolutePath());
						}
					}
				}
			}

			String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
			String codFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
			String partitaIVA = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
			String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
			String sede = 
				datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " "
				+ datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", "
				+ datiImpresaHelper.getDatiPrincipaliImpresa().getCapSedeLegale() + " "
				+ datiImpresaHelper.getDatiPrincipaliImpresa().getComuneSedeLegale() 
				+ " (" + datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")";
			String descBando = helper.getDescBando();
			String data = UtilityDate.convertiData(
					helper.getDataPresentazione(),
					UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
			String[] destinatari = this.mailUfficioProtocollo.split(",");
			
			String subject = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_RINNOVO_RICEVUTA_OGGETTO"),
					new Object[] { tipoRinnovo, helper.getDescBando(), helper.getIdBando() });
			
			String text = null;
			if (this.allegaDocMailUfficioProtocollo && p != null) {
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_RINNOVO_PROTOCOLLO_TESTOCONALLEGATI"),
						new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, descBando, tipoRinnovo });
			} else {
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_RINNOVO_PROTOCOLLO_TESTO"),
						new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, descBando, tipoRinnovo });
			}
			if (this.isPresentiDatiProtocollazione()) {
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_RINNOVO_RICEVUTA_TESTOCONPROTOCOLLO"),
						new Object[] { ragioneSociale,descBando,data,this.getAnnoProtocollo().toString(), this.getNumeroProtocollo() });
			}
			
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
	 * invia la mail di conferma all'impresa 
	 */
	private void sendMailConfermaImpresa(
			String ragioneSociale, 
			String tipoRinnovo,
			String bandoGara) throws ApsSystemException 
	{
		String data = UtilityDate.convertiData(
				this.dataInvio,
				UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		
		String destinatario = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
		
		String text = MessageFormat.format(this.getI18nLabel("MAIL_RINNOVO_RICEVUTA_TESTO"), 
				new Object[] { ragioneSociale, tipoRinnovo, bandoGara, data });
		if (this.isPresentiDatiProtocollazione()) {
			text = MessageFormat.format(this.getI18nLabel("MAIL_RINNOVO_RICEVUTA_TESTOCONPROTOCOLLO"),
					new Object[] { ragioneSociale, this.codice, data, this.annoProtocollo.toString(), this.numeroProtocollo });
		}
		
		String subject = MessageFormat.format(this.getI18nLabel("MAIL_RINNOVO_RICEVUTA_OGGETTO"),
				new Object[] { tipoRinnovo, bandoGara, this.codice });

		this.mailManager.sendMail(
				text, 
				subject, 
				new String[]{destinatario}, 
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}

	/**
	 * ... 
	 */
	private static Date retrieveDataInvio(
			INtpManager ntpManager,
			BaseAction action,
			String keyNomeOperazione) 
	{
		Date data = null;
		String nomeOperazioneLog = action.getI18nLabelFromDefaultLocale(keyNomeOperazione).toLowerCase();
		String nomeOperazione = action.getI18nLabel(keyNomeOperazione).toLowerCase();

		try {
			data = ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", action
					.getTextFromDefaultLocale(
							"Errors.ntpTimeout", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpTimeout",
					new String[] { nomeOperazione }));
		} catch (UnknownHostException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", action
					.getTextFromDefaultLocale(
							"Errors.ntpUnknownHost", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpUnknownHost",
					new String[] { nomeOperazione }));
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", action
					.getTextFromDefaultLocale(
							"Errors.ntpUnexpectedError", nomeOperazioneLog));
			action.addActionError(action.getText("Errors.ntpUnexpectedError",
					new String[] { nomeOperazione }));
		}
		return data;
	}
	
	/**
	 * verifica i documenti allegati al rinnovo
	 */
	private static boolean checkDocumenti(
			WizardRinnovoHelper helper,
			String tipoImpresa,
			BaseAction action) 
		throws ApsException 
	{
		boolean controlliOk = true;
		
		IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
			.getBean(PortGareSystemConstants.BANDI_MANAGER,
					 ServletActionContext.getRequest());

		TipoPartecipazioneType tipoPartecipazione = bandiManager
			.getTipoPartecipazioneImpresa(
					action.getCurrentUser().getUsername(), 
					helper.getCodice(),
					null);

		List<DocumentazioneRichiestaType> documentiRichiesti = bandiManager
			.getDocumentiRichiestiRinnovoIscrizione(
					helper.getCodice(),
					tipoImpresa, 
					tipoPartecipazione.isRti());

		Long documentoId;
		String documentoName;
		boolean docFound;

		for (int i = 0; i < documentiRichiesti.size(); i++) {
			docFound = false;
			if(!controlliOk) {
				break;
			}
			if(documentiRichiesti.get(i).isObbligatorio()) {
				for (Attachment attachment : helper.getDocumenti().getRequiredDocs()) {
					documentoId = attachment.getId();
					if (documentoId != 0 && documentoId == documentiRichiesti.get(i).getId()) {
						docFound = true;
						break;
					}
				}
				if(!docFound) {
					controlliOk = false;
					action.addActionError(action.getText("Errors.invioRinnovo.docRichiestoObbligatorioNotFound",
							new String[] { documentiRichiesti.get(i).getNome() }));
				}
			}
		}

		for (Attachment attachment : helper.getDocumenti().getRequiredDocs()) {
			documentoName = attachment.getFileName();
			if (documentoName.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				controlliOk = false;
				action.addActionError(action.getText("Errors.invioRinnovo.docRichiestoOverflowFileNameLength",
						new String[] { documentoName }));
			}
		}

		for (Attachment attachment : helper.getDocumenti().getAdditionalDocs()) {
			documentoName = attachment.getFileName();
			if (documentoName.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				controlliOk = false;
				action.addActionError(action.getText("Errors.invioRinnovo.docUlterioreOverflowFileNameLength",
						new String[] { documentoName }));
			}
		}
		return controlliOk;
	}

	/**
	 * ...
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 * @throws DocumentException 
	 */
	private WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardRinnovoHelper rinnovoHelper,
			WizardDatiImpresaHelper datiImpresaHelper, 
			String prefissoLabel,
			FascicoloProtocolloType fascicoloBackOffice,
			ComunicazioneType comunicazione, 
			DettaglioBandoIscrizioneType bando, boolean isActiveFunctionPdfA, InputStream iccFilePath) throws IOException, ApsException, DocumentException 
	{
		// ATTENZIONE:
		// L'UTILIZZO DEL CAST A INTEGER SUL METODO appParamManager.getConfigurationValue(...)
		// E' DEPRECATO, IN QUANTO NON E' SEMPRE VERO CHE IN PPCOMMON_PROPERTIES ESISTA
		// LA DEFINIZIONE DEL TIPO DI DATO DA RESTITUIRE (INT, BOOLEAN, STRING)
		// TUTTI I NUOVI PARAMETRI ANDREBBERO CONSIDERATI SEMPRE COME STRINGHE
		WizardDocumentiHelper documentiHelper = rinnovoHelper.getDocumenti();
		
		String codiceSistema = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);

		boolean esisteFascicolo = (fascicoloBackOffice != null); 
		String codiceFascicolo = null;
		Long annoFascicolo = null;
		String numeroFascicolo = null;
		String classificaFascicolo = null;
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
		String rup = (bando.getStazioneAppaltante() != null ? bando.getStazioneAppaltante().getRup() : null);
		String acronimoRup = ProcessPageProtocollazioneAction.getAcronimoSoggetto(rup);

		if (esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			classificaFascicolo = fascicoloBackOffice.getClassifica();
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
			// si legge la classifica presa dalla configurazione solo se non esiste il fascicolo
			classificaFascicolo = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CLASSIFICA);
			
			// ELENCO
			idTitolazione = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TITOLAZIONE);
		}

		idUnitaOperDestinataria = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA � vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}
		
		// ELENCO
		idIndice = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_INDICE);

		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();		
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);
		wsdmProtocolloDocumentoIn.setTipoDocumento( (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TIPO_DOCUMENTO) );
		wsdmProtocolloDocumentoIn.setChannelCode(channelCode);
		
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
		data.setTimeInMillis(this.getDataInvio().getTime());
		wsdmProtocolloDocumentoIn.setData(data);
		
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();	
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String indirizzo = datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " " + 
		   				   datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale();
		String descrizione = MessageFormat.format(this.getI18nLabelFromDefaultLocale(prefissoLabel + "_TESTO"),
							 					  new Object[] { ragioneSociale200, this.codice, bando.getStazioneAppaltante().getDenominazione() });
		String oggetto = MessageFormat.format(
							this.getI18nLabelFromDefaultLocale(
									rinnovoHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD 
									? "LABEL_WSDM_OGGETTO_RINNOVO_ELENCO"
									: "LABEL_WSDM_OGGETTO_RINNOVO_CATALOGO"
							), new Object[] { ragioneSociale200, codiceFiscale });

		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.codice + " - " + oggetto;
		}

		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}

		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		wsdmProtocolloDocumentoIn.setDescrizione(descrizione);
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro( (String)this.appParamManager
					.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CODICE_REGISTRO));
		
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId());
		
		// servono per Archiflow
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(bando.getStazioneAppaltante().getCodice());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + bando.getDatiGeneraliBandoIscrizione().getCodice());
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(bando.getDatiGeneraliBandoIscrizione().getCodice());
		}
		//wsdmProtocolloDocumentoIn.setCig(null);
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(bando.getStazioneAppaltante().getCodice());		
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
		// JPROTOCOL: "Cognomeointestazione" accetta al massimo 100 char 
		if(IWSDMManager.CODICE_SISTEMA_JPROTOCOL.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 100));
		} else if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
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
			// oggettoFascicolo serve per Titulus
		    fascicolo.setOggettoFascicolo(rinnovoHelper.getDescBando());
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
		
		String partitaIVA = rinnovoHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA();
		String tipoRinnovo = (
				rinnovoHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD 
				? this.getI18nLabelFromDefaultLocale("LABEL_ALL_ELENCO") 
				: this.getI18nLabelFromDefaultLocale("LABEL_AL_MERCATO_ELETTRONICO")
		);

		// recupera gli allegati
		List<DocumentazioneRichiestaType> listaDocRichiesti = this.bandiManager
				.getDocumentiRichiestiRinnovoIscrizione(
						rinnovoHelper.getIdBando(), 
						rinnovoHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(), 
						rinnovoHelper.isRti());
		
		
		// prepara 1+N allegati...
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// ...verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		boolean inTesta = false;		// default, inserisci in coda
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			String v = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
			if("1".equals(v)) {
				inTesta = true;
			}
		}
		
		WSDMProtocolloAllegatoType[] allegati = createAttachments(rinnovoHelper, comunicazione, isActiveFunctionPdfA,
				iccFilePath, documentiHelper, ragioneSociale, codiceFiscale, indirizzo, email, partitaIVA, tipoRinnovo,
				listaDocRichiesti, inTesta);
		
		wsdmProtocolloDocumentoIn.setAllegati(allegati);
		
		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)){
			wsdmProtocolloDocumentoIn.setOggetto(this.codice + "-" + wsdmProtocolloDocumentoIn.getOggetto()+"-" + rinnovoHelper.getDescBando());
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
		// configurazione presente nel fascicolo di backoffice (cos� avveniva
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

	private WSDMProtocolloAllegatoType[] createAttachments(WizardRinnovoHelper rinnovoHelper,
			ComunicazioneType comunicazione, boolean isActiveFunctionPdfA, InputStream iccFilePath,
			WizardDocumentiHelper documentiHelper, String ragioneSociale, String codiceFiscale, String indirizzo,
			String email, String partitaIVA, String tipoRinnovo, List<DocumentazioneRichiestaType> listaDocRichiesti,
			boolean inTesta) throws ApsException, DocumentException, IOException {
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[
		    	documentiHelper.getRequiredDocs().size() + documentiHelper.getAdditionalDocs().size() + 2
			];
		
		int n = 0;
		if(inTesta) {
			n++;
		}
		
		// allega gli eventuali documenti richiesti...
		for(Attachment attachment : documentiHelper.getRequiredDocs()) {
			boolean titoloFound = false;
			allegati[n] = new WSDMProtocolloAllegatoType();
			allegati[n].setNome(attachment.getFileName());
			allegati[n].setTipo(StringUtils.substringAfterLast(attachment.getFileName(), "."));

			for (int k = 0; k < listaDocRichiesti.size() && !titoloFound; k++) {
				DocumentazioneRichiestaType elem = listaDocRichiesti.get(k);
				if (attachment.getId() == elem.getId()) {
					allegati[n].setTitolo(elem.getNome());
					titoloFound = true;
				}
			}
			//byte[] bf = FileUtils.readFileToByteArray(documentiHelper.getDocRichiesti().get(j));
			allegati[n].setContenuto(documentiHelper.getContenutoDocRichiesto(attachment));
			// serve per Titulus
			allegati[n].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
			n++;
		}
		
		// allega gli eventuali documenti ulteriori...
		for(Attachment attachment : documentiHelper.getAdditionalDocs()) {
			allegati[n] = new WSDMProtocolloAllegatoType();
			allegati[n].setNome(attachment.getFileName());
			allegati[n].setTipo(StringUtils.substringAfterLast(attachment.getFileName(), "."));
			allegati[n].setTitolo(attachment.getDesc());
			//byte[] bf = FileUtils.readFileToByteArray(documentiHelper.getDocUlteriori().get(j));
			allegati[n].setContenuto(documentiHelper.getContenutoDocUlteriore(attachment));
			// serve per Titulus
			allegati[n].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
			n++;
		}
		
		byte[] fileRiepilogo = null;
		AllegatoComunicazioneType[] allegatiComunicazione = comunicazione.getAllegato();
		// Se tra gli allegati � gi� presente il file di marcatura non lo ricreo
		for (int i = 0; i < allegatiComunicazione.length; i++) {
			if(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE.equals(allegatiComunicazione[i].getNomeFile())
					|| PortGareSystemConstants.FILENAME_RIEPILOGO.equals(allegatiComunicazione[i].getNomeFile())){
				fileRiepilogo = allegatiComunicazione[i].getFile();
			}
		}
		if(fileRiepilogo != null){
			boolean hasMarcaturaTemporale = false;
			try{
				hasMarcaturaTemporale = this.customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE");
			}catch (Exception e) {
				throw new ApsException("Non e' stato possibile leggere la configurazione ACT per l'objectId INVIOFLUSSI feature MARCATEMPORALE");
			}
			if(hasMarcaturaTemporale){
				allegati[n] = new WSDMProtocolloAllegatoType();
				allegati[n].setTitolo(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE);
				allegati[n].setTipo("tsd");
				allegati[n].setNome(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE);
				allegati[n].setContenuto(fileRiepilogo);
				// serve per Titulus
				allegati[n].setIdAllegato("W_INVCOM/"
						+ CommonSystemConstants.ID_APPLICATIVO + "/"
						+ comunicazione.getDettaglioComunicazione().getId()
						+ "/" + n);
			} else {
				allegati[n] = new WSDMProtocolloAllegatoType();
				allegati[n].setTitolo(PortGareSystemConstants.FILENAME_RIEPILOGO);
				allegati[n].setTipo("pdf");
				allegati[n].setNome(PortGareSystemConstants.FILENAME_RIEPILOGO);
				allegati[n].setContenuto(fileRiepilogo);
				// serve per Titulus
				allegati[n].setIdAllegato("W_INVCOM/"
						+ CommonSystemConstants.ID_APPLICATIVO + "/"
						+ comunicazione.getDettaglioComunicazione().getId()
						+ "/" + n);
			}
			n++;
		}
		
		// allegato del file comunicazione.pdf...
		int n2 = n;
		if(inTesta) {
			n2 = 0;
		}
		
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setNome("comunicazione.pdf");
		allegati[n2].setTipo("pdf");
		allegati[n2].setTitolo(MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("NOTIFICA_RINNOVO_OGGETTO"),
				new Object[] { tipoRinnovo, this.codice }));
		String contenuto = MessageFormat
				.format(this.getI18nLabelFromDefaultLocale("MAIL_RINNOVO_PROTOCOLLO_TESTOCONALLEGATI"),
						new Object[] {
								ragioneSociale,
								codiceFiscale,
								partitaIVA,
								email,
								indirizzo,
								UtilityDate.convertiData(this.getDataInvio(),
										UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
								rinnovoHelper.getDescBando(), tipoRinnovo });
		byte[] contenutoPdf = trasformaStringaInPdf(contenuto, isActiveFunctionPdfA, iccFilePath);
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
	 */
	private void annullaComunicazioneInviata(
			String idApplicativo, 
			Long idComunicazione, 
			String descBando,
			WizardRinnovoHelper helper) 
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
			// verifica se la comunicazione inviata era in stato BOZZA
			// prima dell'invio... 
			if(helper.getIdComunicazioneBozza() != null && 
			   helper.getIdComunicazioneBozza().longValue() == idComunicazione.longValue()) {
				// ...riporta la comunicazione in stato BOZZA
				ComunicazioneType comunicazione = this.comunicazioniManager
					.getComunicazione(idApplicativo, idComunicazione);
				comunicazione.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				this.comunicazioniManager.sendComunicazione(comunicazione);
			} else {
				this.comunicazioniManager.deleteComunicazione(idApplicativo, idComunicazione);
			}
		} catch (ApsException e) {
			msgErrore = this.getTextFromDefaultLocale("Errors.deleteComunicazione", idComunicazione.toString());
			ApsSystemUtils.logThrowable(e, this, "delete", this.msgErrore);
			ExceptionUtils.manageExceptionError(e, this);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	private static String getPdfRiepilogo(WizardDocumentiHelper documenti) {
		StringBuilder sb = new StringBuilder();

		sb.append(Attachment.toPdfRiepilogo(documenti.getRequiredDocs()));
		sb.append(Attachment.toPdfRiepilogo(documenti.getAdditionalDocs()));

		return sb.length() > 0
			   ? sb.toString()
			   : "Nessun documento allegato";
	}

	private static byte[] trasformaStringaInPdf(String contenuto, boolean isActiveFunctionPdfA, InputStream iccFilePath) throws DocumentException, IOException {
		if(isActiveFunctionPdfA) {
			try {
				ApsSystemUtils.getLogger().info("Trasformazione contenuto in PDF-A");
				iccFilePath.reset();	// e' un ByteArrayInputStream
				return UtilityStringhe.string2PdfA(contenuto, iccFilePath);
			} catch (com.itextpdf.text.DocumentException e) {
				DocumentException de = new DocumentException("Impossibile creare il contenuto in PDF-A.");
				de.initCause(e);
				throw de;
			}
		} else {
			return UtilityStringhe.string2Pdf(contenuto);
		}
	}
}
