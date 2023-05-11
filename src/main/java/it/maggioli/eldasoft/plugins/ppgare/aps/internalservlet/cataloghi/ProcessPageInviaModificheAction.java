package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageProtocollazioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Consente la generazione del PDF di riepilogo delle modifiche ai prodotti.
 * 
 * @author Marco.Perazzetta
 */
public class ProcessPageInviaModificheAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7877095906255185322L;

	private static final int CATALOGHI 			= 1; 
	private static final int VARIAZIONE_PREZZI	= 2;
	
	private Map<String, Object> session;

	private ICustomConfigManager customConfigManager;
	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
	private INtpManager ntpManager;
	private IComunicazioniManager comunicazioniManager;
	private IMailManager mailManager;
	private IWSDMManager wsdmManager;
	private IEventManager eventManager;

	private Integer tipoProtocollazione;
	@Validate(EParamValidation.EMAIL)
	private String mailUfficioProtocollo;
	private Boolean allegaDocMailUfficioProtocollo;
	@Validate(EParamValidation.DIGIT)
	private String numeroProtocollo;
	private Long annoProtocollo;
	@Validate(EParamValidation.STAZIONE_APPALTANTE)
	private String stazioneAppaltanteProtocollante;

	private Date dataPresentazione;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataProtocollo;
	@Validate(EParamValidation.GENERIC)
	private String msgErrore;
	@Validate(EParamValidation.GENERIC)
	private String codiceSistema;

	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.GENERIC)
	private String catalogo;
	private boolean sendOnlyBozze;

	private File riepilogo;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String riepilogoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String riepilogoFileName;

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public File getRiepilogo() {
		return riepilogo;
	}

	public void setRiepilogo(File riepilogo) {
		this.riepilogo = riepilogo;
	}

	public String getRiepilogoContentType() {
		return riepilogoContentType;
	}

	public void setRiepilogoContentType(String riepilogoContentType) {
		this.riepilogoContentType = riepilogoContentType;
	}

	public String getRiepilogoFileName() {
		return riepilogoFileName;
	}

	public void setRiepilogoFileName(String riepilogoFileName) {
		this.riepilogoFileName = riepilogoFileName;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(String dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public String getMsgErrore() {
		return msgErrore;
	}
	
	public String getCodiceSistema() {
		return codiceSistema;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public boolean isSendOnlyBozze() {
		return sendOnlyBozze;
	}

	public void setSendOnlyBozze(boolean sendOnlyBozze) {
		this.sendOnlyBozze = sendOnlyBozze;
	}

	public String getStazioneAppaltanteProtocollante() {
		return stazioneAppaltanteProtocollante;
	}

	public void setStazioneAppaltanteProtocollante(String stazioneAppaltanteProtocollante) {
		this.stazioneAppaltanteProtocollante = stazioneAppaltanteProtocollante;
	}
	
	public boolean isPresentiDatiProtocollazione() {
		return (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM)
				&& this.annoProtocollo != null && this.numeroProtocollo != null;
	}
	
	public String getTAB_PRODOTTI_NEL_CARRELLO() {
		return CataloghiConstants.TAB_PRODOTTI_NEL_CARRELLO;
	}
	
	/**
	 * restituisce la label LABEL_RICHIESTA_CON_ID associata a LAPIS 
	 */
	public String getLABEL_RICHIESTA_CON_ID() {
		return MessageFormat.format(this.getI18nLabel("LABEL_RICHIESTA_CON_ID"), new Object[] {dataPresentazione, numeroProtocollo});
	}
	
	/**
	 * Gestisco la richiesta di allegare un immagine al prodotto.
	 * 
	 * @return il target a cui andare
	 */
	public String add() {
		String target = "addRiepilogo";

		try {

			if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = ERROR;
			} else {
				CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
						.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);

				int dimensioneRiepilogo = FileUploadUtilities.getFileSize(this.riepilogo);

				// traccia l'evento di upload di un file...
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(carrelloProdotti.getCurrentCatalogo());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Riepilogo modifiche prodotti:"
								  + " allegato con file=" + this.riepilogoFileName
								  + ", dimensione=" + dimensioneRiepilogo + "KB");

				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				boolean controlliOk =
						checkFileSize(riepilogo, riepilogoFileName, dimensioneRiepilogo, appParamManager, evento)
						&& checkFileName(riepilogoFileName, evento)
						&& checkFileFormat(riepilogo, riepilogoFileName, PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO, evento, onlyP7m);
				
				if (controlliOk) {
					Date checkDate = Date.from(Instant.now());
					DocumentiAllegatiFirmaBean checkFirma = this.checkFileSignature(this.riepilogo, this.riepilogoFileName, PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO,checkDate, evento, onlyP7m, this.appParamManager, this.customConfigManager);
					ProdottiCatalogoSessione prodottiDaInviare = carrelloProdotti
							.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());

					if (!this.riepilogoFileName.equals(prodottiDaInviare.getRiepilogoFileName())) {
						File f = new File(this.getRiepilogo().getParent()
												  + File.separatorChar
												  + FileUploadUtilities.generateFileName());
						this.getRiepilogo().renameTo(f);
						prodottiDaInviare.setRiepilogoSize(dimensioneRiepilogo);
						prodottiDaInviare.setRiepilogo(f);
						prodottiDaInviare.setRiepilogoContentType(this.getRiepilogoContentType());
						prodottiDaInviare.setRiepilogoFileName(this.getRiepilogoFileName());
					}
				} else {
					actionErrorToFieldError();
					target = INPUT;
				}

				this.eventManager.insertEvent(evento);
			}

		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "add");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * restituisce il prefisso dei messaggi di notifica all'ufficio protocollo, 
	 * all'impresa o al servizio WSDM
	 */
	private String getPrefissoLabel(int tipoInvio, int procedura) {
		if(procedura == 1) {
			// richiesta ufficio protocollo
			return (tipoInvio == CATALOGHI ? "MAIL_CATALOGHI" : "MAIL_CATALOGHI_VARPRZ");
		} else if(procedura == 2) {
			// conferma all'impresa
			return (tipoInvio == CATALOGHI ? "MAIL_CATALOGHI" : "MAIL_CATALOGHI_VARPRZ");
		} else if(procedura == 3) {
			// creazione wsdm
			return (tipoInvio == CATALOGHI ? "NOTIFICA_CATALOGHI" : "NOTIFICA_CATALOGHI_VARPRZ"); 
		}
		return "";
	}
	
	/**
	 * Creo la comunicazione con le modifiche dei prodotti in W_INVCOM con:
	 * tipologia "FS7" (Richiesta aggiornamento prodotti in catalogo), stato "5"
	 * (Da processare), oggetto "Richiesta aggiornamento prodotti nel catalogo
	 * x, testo "Il fornitore y chiede di potersi aggiornare i prodotti nel
	 * catalogo x. Cordiali saluti".
	 * 
	 * @return il target a cui andare
	 */
	public String send() {

		// I dati inseriti vengono serializzati nel rispetto del type
		// GestioneProdottiType nel file dati_prodotti.xml memorizzato
		// in W_DOCDIG in un'occ. collegata a quella in W_INVCOM. Il pdf firmato
		// digitalmente va invece inserito a parte come ulteriore allegato alla
		// medesima comunicazione. I soli prodotti in bozza vengono salvati
		// in una ulteriore comunicazione identica e senza file di riepilogo.

		this.codiceSistema = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);									
		this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
		this.allegaDocMailUfficioProtocollo = (Boolean) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
		this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
		DettaglioBandoIscrizioneType bandoIscrizione = null;
		String stazioneAppaltanteProcedura = null;

		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(INPUT);
		} else {
			this.username = this.getCurrentUser().getUsername();

			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			this.setCatalogo(carrelloProdotti.getCurrentCatalogo());
			ProdottiCatalogoSessione prodottiDaInviare = carrelloProdotti
					.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());

			WizardDatiImpresaHelper datiImpresaHelper = null;
			int tipoInvio = CATALOGHI;
			String nomeOperazioneLog = this.getI18nLabelFromDefaultLocale("BUTTON_DETTAGLIO_CATALOGO_GESTIONE_PRODOTTI").toLowerCase();
			String nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_CATALOGO_GESTIONE_PRODOTTI").toLowerCase();
			boolean controlliOk = true;
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			// boolean inviataMailConferma = false;
			ComunicazioneType nuovaComunicazione = null;

			// FASE 1: invio della comunicazione
			try {
				bandoIscrizione = bandiManager.getDettaglioBandoIscrizione(this.catalogo);
				stazioneAppaltanteProcedura = bandoIscrizione.getStazioneAppaltante().getCodice();
				
				this.appParamManager.setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
				
				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!! 
				this.tipoProtocollazione = this.appParamManager.getTipoProtocollazione(stazioneAppaltanteProcedura);			
				
				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.appParamManager.isConfigWSDMNonDisponibile()) {
					controlliOk = false;
					this.setTarget(INPUT);
					String msgerr = this.getText("Errors.wsdm.configNotAvailable");
					this.addActionError(msgerr);
					Event evento = new Event();
					evento.setUsername(this.username);
					evento.setDestination(this.catalogo);
					evento.setLevel(Event.Level.ERROR);
					evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getCurrentUser().getSessionId());
					evento.setMessage("Configurazione non disponibile o vuota");
					evento.setDetailMessage(msgerr);
					this.eventManager.insertEvent(evento);
				}
				
				// verificare che tutti i documenti richiesti ed obbligatori
				// siano presenti nella domanda
				if(controlliOk) {
					if (prodottiDaInviare.getRiepilogo() == null) {
						controlliOk = false;
						this.setTarget(INPUT);
						this.addActionError(this.getText("Errors.pdf.RiepilogoFirmatoNotFound"));
					}
				}
				
				// invia la comunicazione...
				if (controlliOk) {
					// lettura data/ora ntp
					this.setDataPresentazione(this.ntpManager.getNtpDate());
					// lettura dati impresa
					//--- CESSATI ---
					datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
							this.getCurrentUser().getUsername(), 
							this);
					
					String statoComunicazione = (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA 
							? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
							: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
							
					// invio comunicazione
					if (prodottiDaInviare.isVariazioneOfferta()) {
						tipoInvio = VARIAZIONE_PREZZI; //prefissoLabel = "MAIL_CATALOGHI_VARPRZ"; //label.variazionePrezziScadenze
						nuovaComunicazione = this.sendComunicazioneVariazionePrezziScadenze(
								prodottiDaInviare,
								datiImpresaHelper,
								statoComunicazione);
					} else {
						nuovaComunicazione = this.sendComunicazioneModificheProdotti(
								prodottiDaInviare,
								datiImpresaHelper,
								statoComunicazione);
					}
					inviataComunicazione = true;
				}
				
			} catch (SocketTimeoutException e) {
				ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
						this.getTextFromDefaultLocale("Errors.ntpTimeout", nomeOperazioneLog));
				this.addActionError(this.getText("Errors.ntpTimeout", new String[] {nomeOperazione}));
				this.setTarget(INPUT);
			} catch (UnknownHostException e) {
				ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
						this.getTextFromDefaultLocale("Errors.ntpUnknownHost", nomeOperazioneLog));
				this.addActionError(this.getText("Errors.ntpUnknownHost", new String[] {nomeOperazione}));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (ApsSystemException e) {
				ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
						this.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazioneLog));
				this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[] {nomeOperazione}));
				this.setTarget(INPUT);
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "send");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(INPUT);
			} catch (IOException t) {
				ApsSystemUtils.logThrowable(t, this, "send");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
				this.setTarget(INPUT);
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "send");
				this.addActionError(this.getText("Errors.send.outOfMemory"));
				this.setTarget(INPUT);
			} catch (XmlException t) {
				ApsSystemUtils.logThrowable(t, this, "send");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(INPUT);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "send");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(INPUT);
			}

			// FASE 2: ove previsto, si invia alla protocollazione
			if (inviataComunicazione) {
				Event evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.catalogo);
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
						this.sendMailRichiestaUfficioProtocollo(
								prodottiDaInviare, 
								datiImpresaHelper,
								tipoInvio,
								carrelloProdotti.getCurrentCatalogo());
						mailProtocollazioneInviata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a 5
						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId()
								+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.comunicazioniManager.updateStatoComunicazioni(
								new DettaglioComunicazioneType[] { nuovaComunicazione.getDettaglioComunicazione() },
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.eventManager.insertEvent(evento);
						this.dataProtocollo = UtilityDate.convertiData(
								this.dataPresentazione,
								UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
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
									new String[] { nuovaComunicazione.getDettaglioComunicazione().getId().toString() });
							ApsSystemUtils.logThrowable(t, this, "send", this.msgErrore);
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage(
									"Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
							evento.resetDetailMessage();
							this.eventManager.insertEvent(evento);
							this.dataProtocollo = UtilityDate.convertiData(
									this.dataPresentazione,
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
						} else {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, "send", this.getText("Errors.sendMailError"));
							ExceptionUtils.manageExceptionError(t, this);
							this.setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di protocollare
							this.annullaComunicazioneInviata(prodottiDaInviare, datiImpresaHelper);
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
						FascicoloProtocolloType fascicoloBackOffice = this.bandiManager.getFascicoloProtocollo(this.catalogo);
						WSDMLoginAttrType loginAttr = this.wsdmManager.getLoginAttr();
						if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceAoo() != null) {
							// nel caso di protocollazione Titulus il codiceAoo
							// risulta valorizzato nel fascicolo a deve essere
							// letto per essere usato in protocollazione in
							// ingresso
							loginAttr.getLoginTitAttr().setCodiceAmministrazioneAoo(fascicoloBackOffice.getCodiceAoo());
						}
						if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceUfficio() != null) {
							// nel caso di protocollazione Titulus il
							// codiceUfficio
							// risulta valorizzato nel fascicolo a deve essere
							// letto per essere usato in protocollazione in
							// ingresso
							loginAttr.getLoginTitAttr().setCodiceUfficio(fascicoloBackOffice.getCodiceUfficio());
						}
						
						boolean isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A");
						InputStream iccFilePath = null;
						if(isActiveFunctionPdfA) {
							iccFilePath = new FileInputStream(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
						}
						
						WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = creaInputProtocollazioneWSDM(
								datiImpresaHelper, 
								bandoIscrizione.getDatiGeneraliBandoIscrizione().getOggetto(), 
								prodottiDaInviare,
								tipoInvio, 
								fascicoloBackOffice,
								nuovaComunicazione, 
								bandoIscrizione, isActiveFunctionPdfA, iccFilePath);

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

						// si aggiorna lo stato a 5 aggiornando inoltre anche i
						// dati di protocollazione
						long id = 0;
						if (prodottiDaInviare.isVariazioneOfferta()) {
							id = prodottiDaInviare.getIdComunicazioneVariazioneOfferta();
						} else {
							id = prodottiDaInviare.getIdComunicazioneModifiche();
						}
						documento = new WSDocumentoType();
						documento.setEntita("GARE");
						documento.setChiave1(prodottiDaInviare.getCodiceCatalogo());
						documento.setNumeroDocumento(ris.getNumeroDocumento());
						documento.setAnnoProtocollo(ris.getAnnoProtocollo());
						documento.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
						evento.setMessage(
								"Aggiornamento comunicazione con id "+ nuovaComunicazione.getDettaglioComunicazione().getId()
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
								(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataPresentazione),
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
									new String[] { nuovaComunicazione.getDettaglioComunicazione().getId().toString() });
							ApsSystemUtils.logThrowable(t, this, "send", this.msgErrore);
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage(
									"Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId()
									+ " ed inserire inoltre un documento in ingresso per entità " + documento.getEntita()
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

							ApsSystemUtils.logThrowable(t, this, "send",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							this.msgErrore = this.getText("Errors.service.wsdmHandshake");
							ExceptionUtils.manageWSDMExceptionError(t, this);
							this.setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di protocollare
							this.annullaComunicazioneInviata(prodottiDaInviare, datiImpresaHelper);
							protocollazioneOk = false;
						}
					}
					break;
				default:
					break;
				}
			}

			// FASE 3: se gli step precedenti sono andati a buon fine, si invia
			// la ricevuta all'impresa
			if (inviataComunicazione && protocollazioneOk) {
				Event evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.catalogo);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage(
						"Invio mail ricevuta di conferma trasmissione comunicazione " + nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione()
						+ " a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				try {
					this.sendMailConfermaImpresa(
							carrelloProdotti, 
							datiImpresaHelper, 
							tipoInvio);
					// inviataMailConferma = true;
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di modifica dell'offerta prodotti del catalogo {}.",
							new Object[] { this.getCurrentUser().getUsername(), carrelloProdotti.getCurrentCatalogo() });
					this.msgErrore = this.getText("Errors.sendMailError");
					ApsSystemUtils.logThrowable(t, this, "send");
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
				// pulizia e impostazione navigazione futura
				// se tutto e' andato a buon fine si eliminano
				// le informazioni dalla sessione ...
				this.session.remove(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				this.setTarget(SUCCESS);
			}
			
			// concludi la protocollazione
			this.appParamManager.setStazioneAppaltanteProtocollazione(null);
		}

		return this.getTarget();
	}

	/**
	 * Annulla gli effetti della comunicazione inviata eliminandola nel caso di
	 * FS8, e risalvando un'unica comunicazione di bozza con tutti i prodotti
	 * nel caso di FS7.
	 * 
	 * @param prodottiDaInviare
	 *            carrello delle modifiche
	 * @param datiImpresaHelper
	 *            dati dell'impresa
	 */
	private void annullaComunicazioneInviata(
			ProdottiCatalogoSessione prodottiDaInviare,
			WizardDatiImpresaHelper datiImpresaHelper) 
	{
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setDestination(this.catalogo);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());

		try {
			if (prodottiDaInviare.isVariazioneOfferta()) {
				evento.setMessage(
						"Annullamento comunicazione con id " + prodottiDaInviare.getIdComunicazioneVariazioneOfferta());
				this.comunicazioniManager.deleteComunicazione(
						CommonSystemConstants.ID_APPLICATIVO,
						prodottiDaInviare.getIdComunicazioneVariazioneOfferta());
				prodottiDaInviare.setIdComunicazioneVariazioneOfferta(null);
			} else {
				evento.setMessage(
						"Annullamento comunicazione con id " + prodottiDaInviare.getIdComunicazioneModifiche());
				if (prodottiDaInviare.getNumeroProdottiBozza() > 0) {
					// se ho inviato prodotti e ho salvato una comunicazione con
					// le bozze, rimuovo i prodotti inviati
					this.comunicazioniManager.deleteComunicazione(
							CommonSystemConstants.ID_APPLICATIVO,
							prodottiDaInviare.getIdComunicazioneModifiche());
				} else {
					// se ho inviato solo prodotti e non ho bozze, allora mi
					// imposto come id della bozza l'id della comunicazione
					// inviata, e poi la aggiornero' come bozza
					prodottiDaInviare.setIdComunicazioneBozza(
							prodottiDaInviare.getIdComunicazioneModifiche());
				}
				prodottiDaInviare.setIdComunicazioneModifiche(null);

				// va salvato tutto nella comunicazione di tipo bozza
				String username = this.getCurrentUser().getUsername();
				String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
				String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);

				String oggetto = this.formatI18nLabel("NOTIFICA_CATALOGHI_SEND_OGGETTO", new Object[] { ragioneSociale });
				String testo = this.formatI18nLabel("NOTIFICA_CATALOGHI_SEND_TESTO", new Object[] { ragioneSociale200, this.catalogo });
				String descrizioneFile = this.getI18nLabel("NOTIFICA_CATALOGHI_SEND_ALLEGATO"); 
				String descrizioneFileRiepilogoFirmato = this.getI18nLabel("NOTIFICA_CATALOGHI_SEND_ALLEGATOFIRMATO");
				
				ProdottiAction.sendComunicazione(
						this.comunicazioniManager,
						prodottiDaInviare,
						username,
						ragioneSociale,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO,
						oggetto,
						testo,
						PortGareSystemConstants.NOME_FILE_PRODOTTI_BOZZE,
						descrizioneFile,
						descrizioneFileRiepilogoFirmato,
						true,
						this.dataPresentazione,
						CataloghiConstants.COMUNICAZIONE_SALVATAGGIO_PRODOTTI);
			}
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "send",
					"In fase di annullamento delle modifiche apportate si è verificato un errore, si consiglia una rimozione manuale delle comunicazioni");
			ExceptionUtils.manageExceptionError(e, this);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * Crea e popola il contenitore per richiedere la protocollazione mediante
	 * WSDM.
	 * 
	 * @param datiImpresaHelper
	 *            dati dell'impresa
	 * @param prodottiDaInviare
	 *            carrello delle modifiche
	 * @param prefissoLabel
	 *            prefisso per il reperimento di testi dal resources di package,
	 *            dipendente dall'operazione di modifica prodotti
	 * @param bandoIscrizione
	 * @param isActiveFunctionPdfA 
	 * @param iccFilePath 
	 * @return contenitore popolato
	 * @throws IOException
	 * @throws DocumentException 
	 * @throws ApsException
	 */
	private WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardDatiImpresaHelper datiImpresaHelper, 
			String oggettoFascicolo,
			ProdottiCatalogoSessione prodottiDaInviare, 
			int tipoInvio,
			FascicoloProtocolloType fascicoloBackOffice,
			ComunicazioneType comunicazione,
			DettaglioBandoIscrizioneType bandoIscrizione, 
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
		String rup = (bandoIscrizione.getStazioneAppaltante() != null ? bandoIscrizione.getStazioneAppaltante().getRup() : null);
		String acronimoRup = ProcessPageProtocollazioneAction.getAcronimoSoggetto(rup);
		
		if (esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			classificaFascicolo = fascicoloBackOffice.getClassifica();
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
			// si legge la classifica presa dalla configurazione solo se non
			// esiste il fascicolo
			classificaFascicolo = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_CLASSIFICA);

			// CATALOGO
			idTitolazione = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_TITOLAZIONE);
		}

		idUnitaOperDestinataria = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA è vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}

		// CATALOGO
		idIndice = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_INDICE);

		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);
		wsdmProtocolloDocumentoIn.setTipoDocumento((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_TIPO_DOCUMENTO));
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
		data.setTimeInMillis(this.getDataPresentazione().getTime());
		wsdmProtocolloDocumentoIn.setData(data);

		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
		String indirizzo = datiImpresaHelper.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
						   + " " + datiImpresaHelper.getDatiPrincipaliImpresa().getNumCivicoSedeLegale();
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String prefisso = getPrefissoLabel(tipoInvio, 3);
		String oggetto = this.formatI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_AGGPRODOTTI_CATALOGO",
																  new Object[] { ragioneSociale200, codiceFiscale });
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.catalogo + " - " + oggetto;
		}
		
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}
		
		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		
		wsdmProtocolloDocumentoIn.setDescrizione(this.formatI18nLabelFromDefaultLocale(prefisso + "_SEND_TESTO", //.send.testo
				new Object[] { ragioneSociale200, this.catalogo }));
		
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEPA_CODICE_REGISTRO));
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId());
		// servono per Archiflow
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(bandoIscrizione.getStazioneAppaltante().getCodice());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + bandoIscrizione.getDatiGeneraliBandoIscrizione().getCodice());
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(bandoIscrizione.getDatiGeneraliBandoIscrizione().getCodice());
		}
		// wsdmProtocolloDocumentoIn.setCig(null);
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(bandoIscrizione.getStazioneAppaltante().getCodice());
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
			fascicolo.setOggettoFascicolo(oggettoFascicolo);
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
			String v = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
			if("1".equals(v)) {
				inTesta = true;
			}
		}
	
		WSDMProtocolloAllegatoType[] allegati = createAttachments(datiImpresaHelper, prodottiDaInviare, comunicazione,
				isActiveFunctionPdfA, iccFilePath, ragioneSociale, ragioneSociale200, codiceFiscale, indirizzo,
				prefisso, inTesta);

		wsdmProtocolloDocumentoIn.setAllegati(allegati);
		
		// INTERVENTI ARCHIFLOW
		if (IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setOggetto(
					bandoIscrizione.getDatiGeneraliBandoIscrizione().getCodice()
					+ " - "
					+ wsdmProtocolloDocumentoIn.getOggetto()
					+ " - "
					+ bandoIscrizione.getDatiGeneraliBandoIscrizione().getOggetto());
		}
		Calendar dataArrivo = Calendar.getInstance();
		dataArrivo.setTimeInMillis(System.currentTimeMillis());
		wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(allegati.length - 1));
		wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
		wsdmProtocolloDocumentoIn.setSupporto((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));
		
		String struttura = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STRUTTURA);
		// se specificata in configurazione la uso, altrimenti riutilizzo la
		// configurazione presente nel fascicolo di backoffice (così avveniva
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

	private WSDMProtocolloAllegatoType[] createAttachments(WizardDatiImpresaHelper datiImpresaHelper,
			ProdottiCatalogoSessione prodottiDaInviare, ComunicazioneType comunicazione, boolean isActiveFunctionPdfA,
			InputStream iccFilePath, String ragioneSociale, String ragioneSociale200, String codiceFiscale,
			String indirizzo, String prefisso, boolean inTesta) throws IOException, DocumentException {
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[2];
		
		int n = 0;
		if(inTesta) {
			n++;
		}
		
		// Allegato del file di riepilogo modifiche
		allegati[n] = new WSDMProtocolloAllegatoType();
		allegati[n].setTitolo(this.getI18nLabelFromDefaultLocale(prefisso + "_SEND_ALLEGATOFIRMATO"));
		allegati[n].setTipo("p7m");
		allegati[n].setNome(prodottiDaInviare.getRiepilogoFileName());
		if (prodottiDaInviare.getRiepilogo() != null) {
			allegati[n].setContenuto(FileUtils.readFileToByteArray(prodottiDaInviare.getRiepilogo()));
		}
		// serve per Titulus
		allegati[n].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
		n++;
		
		//... inserisci "comunicazione.pdf" nella lista degli allegati (in testa/in coda)
		//... prepara l'allegato "comunicazione.pdf"
		int n2 = n;
		if(inTesta) {
			n2 = 0;
		}
		
		// allegato del file comunicazione.pdf
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setTitolo(this.formatI18nLabelFromDefaultLocale(prefisso + "_SEND_OGGETTO",
				new Object[] { ragioneSociale200 }));
		allegati[n2].setTipo("pdf");
		allegati[n2].setNome("comunicazione.pdf");
		String contenuto = this.formatI18nLabelFromDefaultLocale("MAIL_CATALOGHI_PROTOCOLLO_TESTOCONALLEGATO", 
				new Object[] { ragioneSociale,
				   codiceFiscale,
				   datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(),
				   (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"),
				   indirizzo,
				   UtilityDate.convertiData(this.getDataPresentazione(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
				   this.getCatalogo() });
		
		byte[] contenutoPdf = null;
		if(isActiveFunctionPdfA) {
			try {
				ApsSystemUtils.getLogger().info("Trasformazione contenuto in PDF-A");
				contenutoPdf = UtilityStringhe.string2PdfA(contenuto,iccFilePath);
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
	 * ...
	 */
	private void sendMailConfermaImpresa(
			CarrelloProdottiSessione carrelloProdotti,
			WizardDatiImpresaHelper impresa, 
			int tipoInvio) throws ApsSystemException 
	{
		String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String data = UtilityDate.convertiData(this.getDataPresentazione(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String destinatario = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
		String prefisso = getPrefissoLabel(tipoInvio, 2); 
		
		String text = this.formatI18nLabel(prefisso + "_RICEVUTA_TESTO", //.mailRicevuta.testo
				new Object[] { ragioneSociale, carrelloProdotti.getCurrentCatalogo(), data });
		if (this.isPresentiDatiProtocollazione()) {
			text = this.formatI18nLabel(prefisso + "_RICEVUTA_TESTOCONPROTOCOLLO", //.mailRicevuta.testoConProtocollo 
					new Object[] { ragioneSociale,
								   carrelloProdotti.getCurrentCatalogo(), 
								   data,
								   this.annoProtocollo.toString(),
								   this.numeroProtocollo });
		}
		String subject = this.formatI18nLabel(prefisso + "_RICEVUTA_OGGETTO", //.mailRicevuta.oggetto 
				new Object[] { carrelloProdotti.getCurrentCatalogo() });
		
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { destinatario },
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}

	/**
	 * invia la mail di richiesta all'ufficio del protocollo 
	 */
	private void sendMailRichiestaUfficioProtocollo(
			ProdottiCatalogoSessione prodottiDaInviare,
			WizardDatiImpresaHelper impresa, 
			int tipoInvio,
			String catalogo) throws ApsSystemException 
	{
		if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
			throw new ApsSystemException("Valorizzare la configurazione "
					+ AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
		}

		Properties p = null;
		if (this.allegaDocMailUfficioProtocollo) {
			if (prodottiDaInviare.getRiepilogo() != null) {
				p = new Properties();
				p.put(prodottiDaInviare.getRiepilogoFileName(),
					  prodottiDaInviare.getRiepilogo().getAbsolutePath());
			}
		}

		String text;
		String subject;
		String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String codFiscale = impresa.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIVA = impresa.getDatiPrincipaliImpresa().getPartitaIVA();
		String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
		String sede = impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
				+ " " + impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()
				+ ", " + impresa.getDatiPrincipaliImpresa().getCapSedeLegale()
				+ " " + impresa.getDatiPrincipaliImpresa().getComuneSedeLegale()
				+ " (" + impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")";
		String codiceCatalogo = prodottiDaInviare.getCodiceCatalogo();
		String data = UtilityDate.convertiData(this.dataPresentazione,
											   UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String[] destinatari = this.mailUfficioProtocollo.split(",");
		String prefisso = getPrefissoLabel(tipoInvio, 1);
		
		subject = this.formatI18nLabelFromDefaultLocale(prefisso + "_RICEVUTA_OGGETTO", //.mailRicevuta.oggetto 
				new Object[] { catalogo });

		if (this.allegaDocMailUfficioProtocollo && p != null) {
			text = this.formatI18nLabelFromDefaultLocale(prefisso + "_PROTOCOLLO_TESTOCONALLEGATI",  //.mailProtocollo.testoConAllegati 
					new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, codiceCatalogo });
		} else {
			text = this.formatI18nLabelFromDefaultLocale(prefisso + "_PROTOCOLLO_TESTO",  //.mailProtocollo.testo
					new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, codiceCatalogo });
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

	/**
	 * Effettua l'invio della comunicazione al backoffice costituita da una
	 * testata e da un allegato contenente al suo interno l'XML con le modifiche
	 * ai prodotti del catalogo (compreso il file di riepilogo firmato), e poi
	 * ripulisce la sessione.
	 * 
	 * @param prodottiDaInviare
	 *            le modifiche ai prodotti da inviare
	 * @param stato
	 *            stato della comunicazione
	 * @param tipoOperazione
	 * @throws IOException
	 * @throws ApsException
	 */
	private ComunicazioneType sendComunicazioneModificheProdotti(
			ProdottiCatalogoSessione prodottiDaInviare,
			WizardDatiImpresaHelper impresa, 
			String stato) throws IOException, ApsException 
	{
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setDestination(this.catalogo);
		evento.setMessage("Invio comunicazione " + PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO);
		evento.setLevel(Event.Level.INFO);
		if (CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(stato)) {
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		} else {
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROTOCOLLARE);
		}
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());

		String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String oggetto = this.formatI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SEND_OGGETTO", new Object[] { ragioneSociale });
		String testo = this.formatI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SEND_TESTO", new Object[] { StringUtils.left(ragioneSociale, 200), this.catalogo });
		String descrizioneFile = this.getI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SEND_ALLEGATO");  
		String descrizioneFileRiepilogoFirmato = this.getI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SEND_ALLEGATOFIRMATO"); 

		prodottiDaInviare.setDataPresentazione(this.dataPresentazione);

		ComunicazioneType comunicazione = null;
		try {
			if (prodottiDaInviare.hasModifiche()) {
				comunicazione = ProdottiAction.sendComunicazione(
						this.comunicazioniManager,
						prodottiDaInviare,
						this.username,
						ragioneSociale,
						stato,
						PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO,
						oggetto,
						testo,
						PortGareSystemConstants.NOME_FILE_GESTIONE_PRODOTTI,
						descrizioneFile,
						descrizioneFileRiepilogoFirmato,
						(prodottiDaInviare.getNumeroProdottiBozza() == 0),
						this.dataPresentazione,
						CataloghiConstants.COMUNICAZIONE_INVIO_PRODOTTI);
				
				prodottiDaInviare.setIdComunicazioneModifiche(
						comunicazione.getDettaglioComunicazione().getId());

				// visto l'esito con successo si arricchisce il messaggio di
				// dettagli
				evento.setMessage(
						"Invio comunicazione " + PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO
						+ " con id " + prodottiDaInviare.getIdComunicazioneModifiche()
						+ " in stato " + stato
						+ " e timestamp ntp " + UtilityDate.convertiData(this.getDataPresentazione(),
																		 UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			}
			if (prodottiDaInviare.getNumeroProdottiBozza() > 0) {
				ComunicazioneType comunicazioneBozza = ProdottiAction.sendComunicazione(
						this.comunicazioniManager,
						prodottiDaInviare,
						this.username,
						ragioneSociale,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO,
						oggetto,
						testo,
						PortGareSystemConstants.NOME_FILE_PRODOTTI_BOZZE,
						descrizioneFile,
						descrizioneFileRiepilogoFirmato,
						true,
						this.dataPresentazione,
						CataloghiConstants.COMUNICAZIONE_SALVATAGGIO_BOZZE);
				
				prodottiDaInviare.setIdComunicazioneBozza(
						comunicazioneBozza.getDettaglioComunicazione().getId());
			}
		} catch (IOException e) {
			evento.setError(e);
			throw e;
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} finally {
			this.eventManager.insertEvent(evento);
		}

		return comunicazione;
	}

	/**
	 * Effettua l'invio della comunicazione al backoffice costituita da una
	 * testata e da un allegato contenente al suo interno l'XML con le
	 * variazioni di prezzo e data scadenza offerta dei prodotti del catalogo
	 * (compreso il file di riepilogo firmato), e poi ripulisce la sessione.
	 * 
	 * @param prodottiDaInviare
	 *            le modifiche ai prodotti da inviare
	 * @param stato
	 *            stato della comunicazione da salvare
	 * @throws IOException
	 * @throws ApsException
	 */
	private ComunicazioneType sendComunicazioneVariazionePrezziScadenze(
			ProdottiCatalogoSessione prodottiDaInviare,
			WizardDatiImpresaHelper impresa, 
			String stato) throws IOException, ApsException 
	{
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setDestination(this.catalogo);
		evento.setMessage("Invio comunicazione " + PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO);
		evento.setLevel(Event.Level.INFO);
		if (CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(stato)) {
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		} else {
			evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROTOCOLLARE);
		}
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());

		ComunicazioneType comunicazione = null;
		try {
			String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
			
			String oggetto = this.formatI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_VARPRZ_SEND_OGGETTO",
					new Object[] { ragioneSociale });
			String testo = this.formatI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_VARPRZ_SEND_TESTO",
					new Object[] { ragioneSociale200, this.catalogo });
			String descrizioneFile = this.getI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_VARPRZ_SEND_ALLEGATO");
			String descrizioneFileRiepilogoFirmato = this.getI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_VARPRZ_SEND_ALLEGATOFIRMATO");

			prodottiDaInviare.setDataPresentazione(this.dataPresentazione);

			comunicazione = ProdottiAction.sendComunicazione(
					this.comunicazioniManager,
					prodottiDaInviare,
					this.username,
					ragioneSociale,
					stato,
					PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO,
					oggetto,
					testo,
					PortGareSystemConstants.NOME_FILE_VARIAZIONE_OFFERTA,
					descrizioneFile,
					descrizioneFileRiepilogoFirmato,
					false,
					this.dataPresentazione,
					CataloghiConstants.COMUNICAZIONE_INVIO_VARIAZIONE_OFFERTA);
			
			prodottiDaInviare.setIdComunicazioneVariazioneOfferta(comunicazione
					.getDettaglioComunicazione().getId());

			// visto l'esito con successo si arricchisce il messaggio di dettagli
			evento.setMessage(
					"Variazione prezzi catalogo " + PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO
					+ " con id " + prodottiDaInviare.getIdComunicazioneVariazioneOfferta()
					+ " in stato " + stato
					+ " e timestamp ntp " + UtilityDate.convertiData(this.getDataPresentazione(),
																	 UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
		} catch (IOException e) {
			evento.setError(e);
			throw e;
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} finally {
			this.eventManager.insertEvent(evento);
		}

		return comunicazione;
	}

	/**
	 * ...
	 */
	public String cancel() {
		String catalogo = null;
		String message = "";

		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		
		if (this.riepilogo != null) {
			if (this.riepilogo.exists()) {
				this.riepilogo.delete();
			}

			catalogo = carrelloProdotti.getCurrentCatalogo();
			message = message + "cancellazione riepilogo, file=" + this.riepilogoFileName;

			this.riepilogo = null;
			this.riepilogoContentType = null;
			this.riepilogoFileName = null;
		}
		if (carrelloProdotti != null) {
			ProdottiCatalogoSessione modificheProdottiCatalogo = carrelloProdotti.getListaProdottiPerCatalogo()
				.get(carrelloProdotti.getCurrentCatalogo());
			
			if (modificheProdottiCatalogo != null && modificheProdottiCatalogo.getRiepilogo() != null) {
				if (modificheProdottiCatalogo.getRiepilogo().exists()) {
					modificheProdottiCatalogo.getRiepilogo().delete();
				}

				catalogo = modificheProdottiCatalogo.getCodiceCatalogo();
				message = message + "cancellazione riepilogo, file=" + modificheProdottiCatalogo.getRiepilogoFileName()
						+ ", dimensione=" + modificheProdottiCatalogo.getRiepilogoSize() + "KB";

				modificheProdottiCatalogo.setRiepilogoSize(null);
				modificheProdottiCatalogo.setRiepilogo(null);
				modificheProdottiCatalogo.setRiepilogoContentType(null);
				modificheProdottiCatalogo.setRiepilogoFileName(null);
			}
		}

		// traccia l'evento di cancellazione di un file...
		if (catalogo != null && !message.isEmpty()) {
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(catalogo);
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Modifica catalogo" + ": " + message);
			this.eventManager.insertEvent(evento);
		}

		return "cancel";
	}
	
	@Override
	protected Map<String, String> getTextAndElementAndHtmlID() {
		Map<String, String> toReturn = new HashMap<String, String>();
		
		toReturn.put(getText("Errors.fileNotSet"), "riepilogo");
		toReturn.put(getText("Errors.emptyFile"), "riepilogo");
		toReturn.put(getText("Errors.overflowFileSize"), "riepilogo");
		toReturn.put(getText("Errors.overflowTotalFileSize"), "riepilogo");
		
		return toReturn;
	}
	
}
