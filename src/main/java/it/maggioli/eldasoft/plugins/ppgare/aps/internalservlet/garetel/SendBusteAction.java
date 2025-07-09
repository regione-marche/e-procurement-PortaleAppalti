package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.*;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.*;
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
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.utils.ProtocolsUtils;
import it.maggioli.eldasoft.ws.dm.*;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Action di gestione dell'invio buste.
 * 
 * @author Marco.Perazzetta
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class SendBusteAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7547283479159496469L;
	
	// target per la action
	private static final String MODULISTICA_CAMBIATA = "modulisticaCambiata";

	private Map<String, Object> session;

	private INtpManager ntpManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;
	private IMailManager mailManager;
	private IWSDMManager wsdmManager;
	private IEventManager eventManager;
	protected ICustomConfigManager customConfigManager;
	
	private Integer tipoProtocollazione;
	@Validate(EParamValidation.EMAIL)
	private String mailUfficioProtocollo;

	private Boolean allegaDocMailUfficioProtocollo;
	@Validate(EParamValidation.DIGIT)
	private String numeroProtocollo;
	private Long annoProtocollo;
	@Validate(EParamValidation.CODICE)
	private String stazioneAppaltanteProtocollante;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataProtocollo;
	@Validate(EParamValidation.GENERIC)
	private String codiceSistema;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	private Date dataInvio;

	private InputStream inputStream;
	@Validate(EParamValidation.ERRORE)
	private String msgErrore;
	@Validate(EParamValidation.GENERIC)
	private String multipartSaveDir;
	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.EMAIL)
	private String usernameEmail;
	
	@Validate(EParamValidation.ID_COMUNICAZIONE)
	private String idComunicazione;
	
	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public INtpManager getNtpManager() {
		return this.ntpManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public IAppParamManager getAppParamManager() {
		return this.appParamManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public IBandiManager getBandiManager() {
		return this.bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return this.comunicazioniManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public IWSDMManager getWsdmManager() {
		return this.wsdmManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public IEventManager getEventManager() {
		return this.eventManager;
	}

	public IMailManager getMailManager() {
		return this.mailManager;
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

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public int getOperazione() {
		return operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public Integer getTipoProtocollazione() {
		return tipoProtocollazione;
	}

	public void setTipoProtocollazione(Integer tipoProtocollazione) {
		this.tipoProtocollazione = tipoProtocollazione;
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

	public String getMsgErrore() {
		return msgErrore;
	}

	public void setMsgErrore(String msgErrore) {
		this.msgErrore = msgErrore;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMailUfficioProtocollo(String mailUfficioProtocollo){
		this.mailUfficioProtocollo = mailUfficioProtocollo;
	}
	
	public String getMailUfficioProtocollo() {
		return mailUfficioProtocollo;
	}

	public Boolean getAllegaDocMailUfficioProtocollo() {
		return this.allegaDocMailUfficioProtocollo;
	}

	public void setAllegaDocMailUfficioProtocollo(boolean allegaDocMailUfficioProtocollo){
		this.allegaDocMailUfficioProtocollo = allegaDocMailUfficioProtocollo;
	}
 	
	public String getStazioneAppaltanteProtocollante() {
		return stazioneAppaltanteProtocollante;
	}

	public void setStazioneAppaltanteProtocollante(String stazioneAppaltanteProtocollante) {
		this.stazioneAppaltanteProtocollante = stazioneAppaltanteProtocollante;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public String getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(String idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public String getPartecip() {
		return PortGareSystemConstants.NOME_EVENTO_PARTECIPA_GARA;
	}

	public String getInvio() {
		return PortGareSystemConstants.NOME_EVENTO_INVIA_OFFERTA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}
	
	public int getPartecipaGara() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
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
	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	/**
	 * ... 
	 */
	public boolean isPresentiDatiProtocollazione() {
		return (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) 
				&& this.annoProtocollo != null && this.numeroProtocollo != null;
	}
	
	/**
	 * ... 
	 */
	public String getTitle() {
		String title;
		if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			title = this.getI18nLabel("LABEL_GARETEL_INVIO_OFFERTA");
		} else if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
			title = this.getI18nLabel("LABEL_GARETEL_PARTECIPAZIONE_GARA");
		} else {
			title = this.getI18nLabel("LABEL_GARETEL_COMPROVA_REQUISITI");
		}
		return Character.toUpperCase(title.charAt(0)) + title.substring(1);
	}

	/**
	 * restituisce la descrizione del messaggio del log eventi del portale 
	 */
	protected String getDescTipoEvento() {
		return (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
				? "Presenta offerta" 
				: "Presenta domanda di partecipazione");
	}

	/**
	 * ... 
	 */
	protected String getDescrizioneBusta(int tipoBusta) { 
		String desc = null;
		if (tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
			desc = this.getI18nLabel("LABEL_BUSTA_PREQUALIFICA");
		} else if (tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			desc = this.getI18nLabel("LABEL_BUSTA_AMMINISTRATIVA");
		} else if (tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
			desc = this.getI18nLabel("LABEL_BUSTA_TECNICA");
		} else if (tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
			desc = this.getI18nLabel("LABEL_BUSTA_ECONOMICA");
		}
		return desc;  
	}
	
	/**
	 * conferma ed invia  
	 */
	public String confirmInvio() {
		this.setTarget("reopen");

		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return this.getTarget();
		}

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
				
				GestioneBuste buste = GestioneBuste.getFromSession();
				BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
				BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
				BustaTecnica bustaTecnica = buste.getBustaTecnica();
				BustaEconomica bustaEconomica = buste.getBustaEconomica();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
				DettaglioGaraType dettGara = buste.getDettaglioGara(true);
				//WizardDatiImpresaHelper impresa = buste.getImpresa();

				boolean handleBustaEconomica = bustaEconomica != null || (!buste.isConcorsoProgettazioneRiservato() && !buste.isConcorsoProgettazionePubblico());
				
				boolean costoFisso = (dettGara.getDatiGeneraliGara() != null && dettGara.getDatiGeneraliGara().getCostoFisso() != null
									  ? dettGara.getDatiGeneraliGara().getCostoFisso() == 1 
									  : false);
				
				List<CriterioValutazioneOffertaType> criteri = null;
				if (dettGara.getListaCriteriValutazione() != null) {
					criteri = Arrays.asList(dettGara.getListaCriteriValutazione()); 
				}
				
				boolean apriOEPVBustaTecnica = WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(
						PortGareSystemConstants.CRITERIO_TECNICO, 
						dettGara.getDatiGeneraliGara(),
						criteri);
					
				this.dataInvio = retrieveDataInvio(this.ntpManager, this, nomeOperazione);
				if (this.dataInvio != null) {

					boolean invioOfferta = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
					boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);		

					String tipoComunicazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) 
							? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
							: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;

					DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities
							.retrieveComunicazione(
									this.comunicazioniManager,
									this.getCurrentUser().getUsername(),
									this.codice,
									this.progressivoOfferta,
									CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
									tipoComunicazione);
			
					boolean reinvioProtocollazioneMail = false;
					if(dettComunicazione != null) {
						reinvioProtocollazioneMail = this.getComunicazioniManager().getComunicazione(
								CommonSystemConstants.ID_APPLICATIVO, 
								dettComunicazione.getId()) != null;
					}

					boolean controlliOk = true;
					
					//-----------------------------------------------------------------------
					// traccia l'evento di preinvio 
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.CHECK_INVIO);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage("Controlli preinvio dati " + (domandaPartecipazione
							? "domanda di partecipazione" 
							: "richiesta di presentazione offerta"));
					
					// traccia i tentativi di accesso alle funzionalita' fuori tempo massimo
					if(controlliOk && isFuoriTempoMassimo(dettGara)) {
						controlliOk = false;
					}
					
					// verifica se la gara e' sospesa...
					if(controlliOk && isGaraSospesa(dettGara, evento)) {
						controlliOk = false;
					}
					
					// verifica se codice CNEL e' valorizzato
					if(controlliOk && isCodiceCNELMancante(buste.getBustaPartecipazione(), evento)) {
						controlliOk = false; 
					}

					if (controlliOk) {	
						if(domandaPartecipazione) {
							WizardDocumentiBustaHelper wizardBustaPreq = bustaPrequalifica.getHelperDocumenti();
							if (wizardBustaPreq != null && wizardBustaPreq.isDatiModificati()) {
								controlliOk = false;
								this.addActionError(this.getText("Errors.invioBuste.changesNotSent", 
																 new String[] { PortGareSystemConstants.BUSTA_PRE }));
							}
						} 

						if(invioOfferta) {
							if( !reinvioProtocollazioneMail ) {
								WizardDocumentiBustaHelper wizardBustaAmm = bustaAmministrativa.getHelperDocumenti();
								if (wizardBustaAmm != null && wizardBustaAmm.isDatiModificati()) {
									controlliOk = false;
									this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
																     new String[] { PortGareSystemConstants.BUSTA_AMM }));
								}
								
								WizardDocumentiBustaHelper wizardBustaTec = bustaTecnica.getHelperDocumenti();
								if (wizardBustaTec != null && wizardBustaTec.isDatiModificati()) {
									controlliOk = false;
									this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
																	 new String[] { PortGareSystemConstants.BUSTA_TEC }));
								}

								if (handleBustaEconomica) {
									if (dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
										// si usa il getInstance per ovviare al problema di
										// accesso al Presenta offerta per andare
										// direttamente alla funzione di Invia senza passare
										// per l'edit della busta economica gestita
										// telematicamente
										WizardOffertaEconomicaHelper wizardBustaEco = bustaEconomica.getHelper();
										if (wizardBustaEco != null && wizardBustaEco.isDatiModificati()) {
											controlliOk = false;
											this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
													new String[]{PortGareSystemConstants.BUSTA_ECO}));
										}
									} else {
										WizardDocumentiBustaHelper wizardBustaEco = bustaEconomica.getHelperDocumenti();
										if (wizardBustaEco != null && wizardBustaEco.isDatiModificati()) {
											controlliOk = false;
											this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
													new String[]{PortGareSystemConstants.BUSTA_ECO}));
										}
									}
								}
							}
						}
					}

					BustaRiepilogo riepilogo = buste.getBustaRiepilogo();
					// tramite servizio controllo che non siano stati inseriti 
					// nuovi documenti obbligatori comuni per le buste tecniche via BO
					riepilogo.integraBusteFromBO();

					// controlla se esistono documenti senza contenuto nelle buste...
					// o se esistono documenti nelle buste, con dimensione del contenuto inviato 
					// diversa dall'originale ...
					if (controlliOk) {
						VerificaDocumentiCorrotti validazioneDocumenti = new VerificaDocumentiCorrotti(evento);
						
						// verifica presa vissione, documenti obbligatori, documenti "corrotti" e QForm...						
						controlliOk = controlliOk && validazioneDocumenti.validate();
						
						if(validazioneDocumenti.isErroriPresenti()) {
							validazioneDocumenti.addActionErrors(this, evento);
						}
					}
					
					// inserisci l'evento di verifica preinvio
					this.getEventManager().insertEvent(evento);
					
					// se tutti i controlli sono andati bene procedi al passo successivo
					if (controlliOk) {
						this.setTarget(SUCCESS);
					}
				}
			} catch (Exception e) {
				ApsSystemUtils.logThrowable(e, this, "confirm");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "confirm");
				ExceptionUtils.manageExceptionError(t, this);
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String invio() {
		this.setTarget("reopen");

		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return this.getTarget();
		}

		this.codiceSistema = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
		this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
		this.allegaDocMailUfficioProtocollo = (Boolean) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
		this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
		String stazioneAppaltanteProcedura = null;
		byte[] fileRiepilogo = null;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			this.username = this.getCurrentUser().getUsername();
			String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
			
			GestioneBuste buste = GestioneBuste.getFromSession();
			BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
			BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
			BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
			BustaTecnica bustaTecnica = buste.getBustaTecnica();
			BustaEconomica bustaEconomica = buste.getBustaEconomica();

			boolean handleBustaEconomica = bustaEconomica != null || (!buste.isConcorsoProgettazioneRiservato() && !buste.isConcorsoProgettazionePubblico());

			WizardDatiImpresaHelper datiImpresa = null;
			DettaglioGaraType dettGara = null;
			
			boolean domandaPartecipazione = buste.isDomandaPartecipazione();
			
			boolean controlliOk = true;
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			
			// FASE 1: invio delle comunicazioni
			try {
				dettGara = buste.getDettaglioGara(true);
				datiImpresa = buste.getImpresa();
				
				// inizializza la protocollazione impostando subito la SA  
				stazioneAppaltanteProcedura = dettGara.getStazioneAppaltante().getCodice();
				this.appParamManager.setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);

				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!! 
				this.tipoProtocollazione = this.appParamManager.getTipoProtocollazione(stazioneAppaltanteProcedura);
				
				// per i concorsi di progettazione la protocollazione NON e' prevista e viene disabilitata
				if (buste.isConcorsoProgettazionePubblico() || buste.isConcorsoProgettazioneRiservato())
					tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);

				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if (this.appParamManager.isConfigWSDMNonDisponibile()) {
					controlliOk = false;
					String msgerr = this.getText("Errors.wsdm.configNotAvailable");
					this.addActionError(msgerr);
					Event evento = new Event();
					evento.setUsername(this.username);
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.ERROR);
					evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getCurrentUser().getSessionId());
					evento.setMessage("Configurazione non disponibile o vuota");
					evento.setDetailMessage(msgerr);
					this.eventManager.insertEvent(evento);
				}

				// calcola la data di invio e verifica se fuori tempo massimo...
				if(controlliOk && isAccessoFuoriTempoMassimo(dettGara)) {
					controlliOk = false;
					this.addActionError(getText("Errors.invioRichiestaFuoriTempoMassimo", new String[] { nomeOperazione }));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
				
				if (controlliOk) {
					// verifica se la gara e' sospesa...
					boolean sospesa = ("4".equals(dettGara.getDatiGeneraliGara().getStato())); 
					if(sospesa) {
						controlliOk = false;
						Event evento = new Event();
						evento.setUsername(this.getCurrentUser().getUsername());
						evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
						evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
						evento.setIpAddress(this.getCurrentUser().getIpAddress());
						evento.setSessionId(this.getRequest().getSession().getId());
						evento.setMessage("Accesso alla funzione " + this.getDescTipoEvento());
						evento.setDetailMessage(this.getText("Errors.invioBuste.proceduraSospesa"));
						evento.setLevel(Event.Level.ERROR);
						this.getEventManager().insertEvent(evento);
						this.addActionError(this.getText("Errors.invioBuste.proceduraSospesa"));
						this.setTarget(INPUT);
					}
					
					if(controlliOk) {
						if(domandaPartecipazione) {
							// domanda di partecipazione
							if(bustaPrequalifica.getId() > 0) {
								controlliOk = controlliOk && bustaPrequalifica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							}
						} else {
							// invio offerta
							if( !dettGara.getDatiGeneraliGara().isNoBustaAmministrativa() ) {
								if(bustaAmministrativa.getId() > 0) {
									controlliOk = controlliOk && bustaAmministrativa.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
							}
							
							if (this.bandiManager.isGaraConOffertaTecnica(this.codice)) {
								if(bustaTecnica.getId() > 0) {
									controlliOk = controlliOk && bustaTecnica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
							}
	
							if(handleBustaEconomica && bustaEconomica.getId() > 0) {
								controlliOk = controlliOk && bustaEconomica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							}
						}
					
						bustaPartecipazione.get();
						if(controlliOk && bustaPartecipazione.getId() > 0) {
							bustaPartecipazione.getHelper().setDataPresentazione(this.dataInvio);
							inviataComunicazione = bustaPartecipazione.sendConfirm(new FileInputStream(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH)));
						}
					}

					// se qualcosa va in errore imposta il target a CommonSystemConstants.PORTAL_ERROR
					// senza un messaggio di errore viene restituita ad una pagina vuota...
					if(!controlliOk) {
						this.addActionError(this.getText("Errors.unexpected"));
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					}
				} //if (this.dataInvio != null)
				
			} catch (ApsException t) {
				ApsSystemUtils.getLogger().error(
						"Per errori durante la connessione al server di posta, non e'' stato possibile inviare all''impresa {} la ricevuta della richiesta di {}.",
						new Object[] { this.getCurrentUser().getUsername(), nomeOperazione });
				this.msgErrore = this.getText("Errors.invioBuste.sendMailError", new String[] { nomeOperazione });
				ApsSystemUtils.logThrowable(t, this, "invio");
				ExceptionUtils.manageExceptionError(t, this);
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "invio");
				this.addActionError(this.getText("Errors.send.outOfMemory"));
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "invio");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "invio");
				ExceptionUtils.manageExceptionError(e, this);
			}

			ComunicazioneType comunicazione = bustaPartecipazione.getComunicazioneFlusso().getComunicazione();
			ComunicazioneType comunicazioneBustaPrequalifica = (bustaPrequalifica != null ? bustaPrequalifica.getComunicazioneFlusso().getComunicazione() : null);  
			String tipoComunicazione = bustaPartecipazione.getComunicazioneFlusso().getTipoComunicazione();

			// FASE 2: ove previsto, si invia alla protocollazione
			if (inviataComunicazione) {
				Event evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				switch (this.tipoProtocollazione) {
				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
					// --- PROTOCOLLAZIONE MAIL ---
					evento.setMessage("Protocollazione via mail a " + this.mailUfficioProtocollo);
					
					boolean mailProtocollazioneInviata = false;
					try {
						String tipoRichiesta = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) 
								? " " + this.getI18nLabelFromDefaultLocale("LABEL_OFFERTA") + " " 
								: " " + this.getI18nLabelFromDefaultLocale("LABEL_PARTECIPAZIONE");

						// si invia la richiesta di protocollazione via mail
						this.setMailRichiestaUfficioProtocollo(
								datiImpresa, 
								tipoComunicazione,
								tipoRichiesta, 
								dettGara.getDatiGeneraliGara().getOggetto(), 
								UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						mailProtocollazioneInviata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a 5 tutte le comunicazioni relativa alle buste
						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + comunicazione.getDettaglioComunicazione().getId() + 
								" allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.getComunicazioniManager().updateStatoComunicazioni(
								new DettaglioComunicazioneType[] {comunicazione.getDettaglioComunicazione()}, 
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.getEventManager().insertEvent(evento);
						this.setDataProtocollo(UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						
					} catch (XmlException e) {
						ApsSystemUtils.logThrowable(e, this, "send", "Per errori durante la composizione della mail, non e' stato possibile inviare la richiesta all'ufficio protocollo");
						this.addActionError(this.getText("Per errori durante la composizione della mail, non e' stato possibile inviare la richiesta all'ufficio protocollo"));
						ExceptionUtils.manageExceptionError(e, this);
						this.setTarget(INPUT);
						evento.setError(e);
						this.eventManager.insertEvent(evento);
						
					} catch (Throwable t) {
						if (mailProtocollazioneInviata) {
							evento.setError(t);
							this.getEventManager().insertEvent(evento);

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							ComunicazioneType comunicazioneDaProtocollare = comunicazione;
							if (comunicazioneDaProtocollare == null) {
								comunicazioneDaProtocollare = comunicazioneBustaPrequalifica;
							}
							
							this.setMsgErrore(this.getText("Errors.updateStatoComunicazioneDaProcessare",
											  new String[] {comunicazioneDaProtocollare.getDettaglioComunicazione().getId().toString()}));
							ApsSystemUtils.logThrowable(t, this, "send", this.getMsgErrore());
							this.addActionError(this.getMsgErrore());

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + comunicazioneDaProtocollare.getDettaglioComunicazione().getId());							
							this.getEventManager().insertEvent(evento);
						} else {
							evento.setError(t);
							this.eventManager.insertEvent(evento);
							ApsSystemUtils.logThrowable(t, this, "send", "Per errori durante la connessione al server di posta, non e' stato possibile inviare la richiesta all''ufficio protocollo");
							ExceptionUtils.manageExceptionError(t, this);
							this.addActionError(this.getText("Per errori durante la connessione al server di posta, non e' stato possibile inviare la richiesta all''ufficio protocollo"));
							this.setTarget(INPUT);
							protocollazioneOk = false;
						}
					}
					break;
				
				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
					// --- PROTOCOLLAZIONE WSDM ---
					evento.setMessage("Protocollazione via WSDM");

					boolean protocollazioneInoltrata = false;
					WSDocumentoType documento = null;
					WSDMProtocolloDocumentoType ris = null;
					long id = comunicazione.getDettaglioComunicazione().getId();
					try {
						BustaRiepilogo riepilogo = GestioneBuste.getBustaRiepilogoFromSession();
						RiepilogoBusteHelper bustaRiepilogativa = riepilogo.getHelper();
						
						Map<String,RiepilogoBustaBean> riepilogoBuste = new HashMap<String, RiepilogoBustaBean>();
						riepilogoBuste.put("Busta prequalifica", bustaRiepilogativa.getBustaPrequalifica());
						riepilogoBuste.put("Busta amministrativa", bustaRiepilogativa.getBustaAmministrativa());
						riepilogoBuste.put("Busta tecnica", bustaRiepilogativa.getBustaTecnica());
						riepilogoBuste.put("Busta economica", bustaRiepilogativa.getBustaEconomica());						
						
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
								datiImpresa, 
								dettGara, 
								bustaRiepilogativa,
								comunicazione,
								nomeOperazione,
								fascicoloBackOffice,
								fileRiepilogo);

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

						// si aggiorna lo stato a 5 aggiornando inoltre anche i
						// dati di protocollazione
						documento = new WSDocumentoType();
						documento.setEntita("GARE");
						documento.setChiave1(this.codice);
						documento.setNumeroDocumento(ris.getNumeroDocumento());
						documento.setAnnoProtocollo(ris.getAnnoProtocollo());
						documento.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + id
								+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
								+ ", protocollata con anno " + ris.getAnnoProtocollo() 
								+ " e numero " + ris.getNumeroProtocollo());
						
						// LAPISOPERA: inserisci l'ID richiesta restituito dal protocollo nella W_INVCOM e aggiorna il messaggio dell'evento
						ProcessPageProtocollazioneAction.lapisAggiornaComunicazione(
								ris,
								documento,
								comunicazione.getDettaglioComunicazione().getId(),
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
							this.msgErrore = this.getText("Errors.updateStatoComunicazioneDaProcessare",
											 new String[] { String.valueOf(id) });
							ApsSystemUtils.logThrowable(t, this, "invio", this.msgErrore);
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage(
									"Aggiornare manualmente la comunicazione con id " + id
									+ " ed inserire inoltre un documento in ingresso per entita'  " + documento.getEntita()
									+ ", chiave1 " + documento.getChiave1()
									+ ", oggetto " + documento.getOggetto()
									+ ", numero documento " + documento.getNumeroDocumento()
									+ ", anno protocollo " + documento.getAnnoProtocollo()
									+ " e numero protocollo " + documento.getNumeroProtocollo());
							ProcessPageProtocollazioneAction.lapisAggiornaMessaggioEventoCompletamentoManuale(
									ris,
									documento,
									comunicazione.getDettaglioComunicazione().getId(),
									this.appParamManager,
									evento);
							evento.resetDetailMessage();
							this.eventManager.insertEvent(evento);
						} else {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, "invio",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							this.msgErrore = this.getText("Errors.service.wsdmHandshake");
							ExceptionUtils.manageWSDMExceptionError(t, this);
							this.setTarget(INPUT);
							// protocollare riponendo la comunicazione in bozza
							this.annullaComunicazioneInviata(comunicazione);
							protocollazioneOk = false;
						}
					}
					break;
				default:
					// qualsiasi altro caso: non si protocolla nulla altrimenti
					// si darebbe comunicazione di chi ha presentato offerta
					break;
				}
			}

			// FASE 3: se gli step precedenti sono andati a buon fine, si invia
			// la ricevuta all'impresa
			if (inviataComunicazione && protocollazioneOk) {
				Event evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage(
						"Invio mail ricevuta di conferma trasmissione comunicazione " + comunicazione.getDettaglioComunicazione().getTipoComunicazione()
						+ " a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				try {
					this.sendMailConfermaImpresa(
							datiImpresa, 
							dettGara.getDatiGeneraliGara().getOggetto());
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di invio offerta per la gara {}.",
							new Object[] {this.getCurrentUser().getUsername(), this.codice });
					this.msgErrore = this.getText("Errors.invioBuste.sendMailError", new String[] { nomeOperazione });
					ApsSystemUtils.logThrowable(t, this, "invio");
					this.setTarget("successPage");
					evento.setError(t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}

			// FASE 4: se invio e protocollazione sono andate a buon fine, 
			// anche se la ricevuta all'impresa non e' stata inviata, 
			// si procede con la pulizia della sessione
			if (inviataComunicazione && protocollazioneOk) {
				// pulizia e impostazione navigazione futura
				// se tutto e' andato a buon fine si eliminano
				// le informazioni dalla sessione ...
				this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				buste.resetSession();
				this.setTarget("successPage");
			}

		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		// concludi la protocollazione
		this.appParamManager.setStazioneAppaltanteProtocollazione(null);

		unlockAccessoFunzione();
		
		return this.getTarget();
	}

	/**
	 * Crea e popola il contenitore per richiedere la protocollazione mediante WSDM
	 * 
	 * @param datiImpresaHelper
	 *            dati dell'impresa
	 * @param dettGara dati della gara
	 * @param bustaRiepilogativa
	 *            busta riepilogativa
	 * @param comunicazionePartecipazione
	 *            richiesta di partecipazione
	 * @return contenitore popolato
	 * @throws Exception 
	 */
	protected WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardDatiImpresaHelper datiImpresaHelper,
			DettaglioGaraType dettGara,
			RiepilogoBusteHelper bustaRiepilogativa,
			ComunicazioneType comunicazionePartecipazione,
			String nomeOperazione,
			FascicoloProtocolloType fascicoloBackOffice,
			byte[] fileRiepilogo) throws Exception 
	{
		// ATTENZIONE:
		// L'UTILIZZO DEL CAST A INTEGER SUL METODO appParamManager.getConfigurationValue(...)
		// E' DEPRECATO, IN QUANTO NON E' SEMPRE VERO CHE IN PPCOMMON_PROPERTIES ESISTA
		// LA DEFINIZIONE DEL TIPO DI DATO DA RESTITUIRE (INT, BOOLEAN, STRING)
		// TUTTI I NUOVI PARAMETRI ANDREBBERO CONSIDERATI SEMPRE COME STRINGHE

		// recupera il sistema di protocollazione
		String codiceSistema = (String) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);

		boolean esisteFascicolo = (fascicoloBackOffice != null);
		String codiceFascicolo = null;
		Long annoFascicolo = null;
		String numeroFascicolo = null;
		String classificaFascicolo = null;
		String idUnitaOperDestinataria = null;
		String idIndice = null;
		String idTitolazione = null;
		boolean riservatezzaFascicolo = false;
		Integer cfMittente = (Integer) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE);
		boolean usaCodiceFiscaleMittente = (cfMittente != null ? 1 == cfMittente : true);
		String channelCode = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE);
		String codiceRegistro = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CODICE_REGISTRO);
		String sottoTipo = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SOTTOTIPO_GARA);
		String rup = (dettGara.getStazioneAppaltante() != null ? dettGara.getStazioneAppaltante().getRup() : null);
		String acronimoRup = ProcessPageProtocollazioneAction.getAcronimoSoggetto(rup);

		if (esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null 
						  	 ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			classificaFascicolo = fascicoloBackOffice.getClassifica();
			riservatezzaFascicolo = (fascicoloBackOffice.getRiservatezza() != null 
									 ? fascicoloBackOffice.getRiservatezza() : false);
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
			// si legge la classifica presa dalla configurazione 
			// solo se non esiste il fascicolo
			classificaFascicolo = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);
			
			// GARE, ODA
			idTitolazione = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TITOLAZIONE);
		}
		
		idUnitaOperDestinataria = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA e' vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}
		
		// GARE, ODA
		idIndice = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);

		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);
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

		// INTERVENTO ARCHIFLOW FA (SE BUSTA OPREQUALIFICA PERNDO VALORE DALLA CONFIG)
		String tipoDocumentoPrequalifica = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO_PREQUALIFICA);
		String tipoDocumento = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO);
		
		boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		if(domandaPartecipazione && 
		   !(tipoDocumentoPrequalifica == null || "".equals(tipoDocumentoPrequalifica))) {
			tipoDocumento = tipoDocumentoPrequalifica;
		}
		wsdmProtocolloDocumentoIn.setTipoDocumento(tipoDocumento);
	    
		Calendar data = new GregorianCalendar();
		data.setTimeInMillis(this.dataInvio.getTime());
		wsdmProtocolloDocumentoIn.setData(data);
		
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String oggetto = null;
		if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			oggetto = MessageFormat.format(this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_OFFERTA"), 
					   new Object[] {ragioneSociale200, codiceFiscale, dettGara.getDatiGeneraliGara().getOggetto(), dettGara.getDatiGeneraliGara().getCodice()});
		} else if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
			oggetto = MessageFormat.format(this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_PREQUALIFICA"), 
					   new Object[] {ragioneSociale200, codiceFiscale, dettGara.getDatiGeneraliGara().getOggetto(), dettGara.getDatiGeneraliGara().getCodice()});
		}
		
		// serve per FOLIUM e solo in caso di offerta/partecipazione
		if (IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			oggetto = StringUtils.left(oggetto, 250);
		} 
		
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.codice + " - " + oggetto;
		}
		
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}
		
		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		wsdmProtocolloDocumentoIn.setDescrizione(comunicazionePartecipazione.getDettaglioComunicazione().getTesto());
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceRegistro);
		
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId());
		
		// servono per Archiflow/FOLIUM
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(dettGara.getStazioneAppaltante().getCodice());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + dettGara.getDatiGeneraliGara().getCodice());
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(dettGara.getDatiGeneraliGara().getCodice());
		}
		wsdmProtocolloDocumentoIn.setCig(bandiManager.getCigBando(dettGara.getDatiGeneraliGara().getCodice()));
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(dettGara.getStazioneAppaltante().getCodice());
		wsdmProtocolloDocumentoIn.setMezzo((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));
		
		// serve per JIride*
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
			// oggettoFascicolo serve per Titulus
		    fascicolo.setOggettoFascicolo(dettGara.getDatiGeneraliGara().getOggetto());
		    if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
		    	fascicolo.setGenericS11(acronimoRup);
		    	fascicolo.setGenericS12(rup);
				if("CORR_SOC".equals(wsdmProtocolloDocumentoIn.getTipoDocumento())) {
					String r = ProcessPageProtocollazioneAction.getInvertiCognomeNome(rup);
					wsdmProtocolloDocumentoIn.setGenericS11(ProcessPageProtocollazioneAction.getAcronimoSoggetto(r));
					wsdmProtocolloDocumentoIn.setGenericS12(r);
				}
		    }
			if(IWSDMManager.CODICE_SISTEMA_DOCER.equals(codiceSistema)) {
				wsdmProtocolloDocumentoIn.setGenericS11( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPO_FIRMA) );
				wsdmProtocolloDocumentoIn.setGenericS31( (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_DESC_UNITA_OPERATIVA_DESTINATARIA) );
				wsdmProtocolloDocumentoIn.setGenericS32(idUnitaOperDestinataria);
			}

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
	    
		WSDMProtocolloAllegatoType[] allegati = createAttachments(
				datiImpresaHelper
				, dettGara
				, bustaRiepilogativa
				, comunicazionePartecipazione
				, nomeOperazione
				, fileRiepilogo
				, ragioneSociale
				, codiceFiscale
				, indirizzo
				, inTesta
		);		
		wsdmProtocolloDocumentoIn.setAllegati(allegati);
				
		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setOggetto(
					dettGara.getDatiGeneraliGara().getCodice() + 
					"-" + wsdmProtocolloDocumentoIn.getOggetto() + 
					"-" + dettGara.getDatiGeneraliGara().getOggetto());
		}
	    Calendar dataArrivo = Calendar.getInstance();
	    dataArrivo.setTimeInMillis(System.currentTimeMillis());
	    wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(allegati.length - 1));
	    wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
	    wsdmProtocolloDocumentoIn.setSupporto((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));

	    // solo per le procedure di gara, se specificata in configurazione
	    // uso PROTOCOLLAZIONE_WSDM_GARE_STRUTTURA (JPROTOCOL) altrimenti 
	    // uso PROTOCOLLAZIONE_WSDM_STRUTTURA
	    String struttura = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_STRUTTURA);
		if (StringUtils.isEmpty(struttura)) {
			struttura = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STRUTTURA);
		}
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

	private WSDMProtocolloAllegatoType[] createAttachments(
			WizardDatiImpresaHelper datiImpresaHelper
			, DettaglioGaraType dettGara
			, RiepilogoBusteHelper bustaRiepilogativa
			, ComunicazioneType comunicazionePartecipazione
			, String nomeOperazione
			, byte[] fileRiepilogo
			, String ragioneSociale
			, String codiceFiscale
			, String indirizzo
			, boolean inTesta
	) throws Exception {
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[2];

		String titolo = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_OGGETTO"), 
											 new Object[] {this.getCodice(), nomeOperazione});
		// PDF-A
		boolean isActiveFunctionPdfA; 
		try {
			isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A");
		} catch (Exception ex) {
			throw new ApsException(ex.getMessage(),ex);
		}
		
		InputStream iccFilePath = new FileInputStream(getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
		
		// prepara il testo per "comunicazione.pdf"
		String comunicazioneTxt = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_PROTOCOLLO_TESTOCONALLEGATI"),
				new Object[] {
					ragioneSociale,
					codiceFiscale,
					datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA(),
					this.usernameEmail,
					indirizzo,
					UtilityDate.convertiData(getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
					(operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
							? getI18nLabelFromDefaultLocale("LABEL_OFFERTA") 
							: getI18nLabelFromDefaultLocale("LABEL_PARTECIPAZIONE")),
					dettGara.getDatiGeneraliGara().getOggetto() }
		);
		
		byte[] comunicazionePdf = JRPdfExporterEldasoft.textToPdf(
				comunicazioneTxt
				, "Riepilogo comunicazione"
				, this
		);
		
		// aggiungi l'allegato "comunicazione.pdf"
		//
		int n2 = allegati.length - 1;
		if(inTesta) {
			n2 = 0;
		}
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setTitolo(titolo);
		allegati[n2].setTipo(isActiveFunctionPdfA ? "pdf/a" : "pdf");
		allegati[n2].setNome("comunicazione.pdf");
		allegati[n2].setContenuto(comunicazionePdf);
		// serve per Titulus
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId() + "/" + n2);
		int i = 0;
		if(inTesta)
			i = 1;
		
		// aggiungi il "riepilogo_buste.pdf"
		//
		boolean hasMarcaturaTemporale = false;
		try {
			hasMarcaturaTemporale = this.customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE");
		} catch (Exception e) {
			throw new ApsException("Non e' stato possibile leggere la configurazione ACT per l'objectId INVIOFLUSSI feature MARCATEMPORALE");
		}
		
		// recupera il pdf di riepilogo delle buste (pdf/tsd) ed aggiungilo agli allegati del WSDM...
		if(fileRiepilogo == null) {
			BustaRiepilogo bustaRiepilogo = GestioneBuste.getBustaRiepilogoFromSession();
			fileRiepilogo = bustaRiepilogo.getPdfRiepilogoBuste(iccFilePath);
		}

		allegati[i] = new WSDMProtocolloAllegatoType();
		allegati[i].setContenuto(fileRiepilogo);
		if(hasMarcaturaTemporale) {
			allegati[i].setTitolo(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE);
			allegati[i].setTipo("tsd");
			allegati[i].setNome(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE);
		} else {
			allegati[i].setTitolo(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE);
			allegati[i].setTipo("pdf");
			allegati[i].setNome(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE);
		}
		// serve per Titulus
		allegati[i].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId() + "/" + i);

		ProtocolsUtils.setFieldsForNumix(allegati);
		
		return allegati;
	}

	/**
	 * Annulla gli effetti della comunicazione inviata risalvandola come bozza.
	 * 
	 * @param comunicazione
	 *            comunicazione a cui annullare l'invio
	 */
	protected void annullaComunicazioneInviata(ComunicazioneType comunicazione) {
		// traccia l'evento
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setDestination(this.codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Annullamento comunicazione con id " + comunicazione.getDettaglioComunicazione().getId());
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());

		// In caso di buste non cifrate metti in stato "bozza" le comunicazioni,
		// mentrre in caso di cifratura delle busta, elimina tutte le 
		// comunicazioni relative all'offerta
		try {
			this.comunicazioniManager.updateStatoComunicazioni(
					new DettaglioComunicazioneType[] {comunicazione.getDettaglioComunicazione()},
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "invio",
					"In fase di annullamento delle modifiche apportate si e' verificato un errore, " +
					"si consiglia una rimozione manuale delle comunicazioni");
			ExceptionUtils.manageExceptionError(e, this);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * ... 
	 */
	public static Date getDataTermine(DettaglioGaraType dettGara, int operazione) {
		Date dataTermine = null;
		if (operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			dataTermine = InitIscrizioneAction.calcolaDataOra(
					dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(), 
					dettGara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(), 
					true);
		} else if (operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
			dataTermine = InitIscrizioneAction.calcolaDataOra(
					dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(), 
					dettGara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(), 
					true);
		} else {
			// comprova requisiti ???
		}
		return dataTermine;
	}

	/**
	 * ... 
	 */
	public static Date retrieveDataInvio(INtpManager ntpManager, EncodedDataAction action, String nomeOperazione) {
		Date data = null;
		try {
			data = ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", 
					action.getTextFromDefaultLocale("Errors.ntpTimeout", nomeOperazione));
			action.addActionError(action.getText("Errors.ntpTimeout", new String[] { nomeOperazione }));
			action.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (UnknownHostException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", 
					action.getTextFromDefaultLocale("Errors.ntpUnknownHost", nomeOperazione));
			action.addActionError(action.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione }));
			action.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", 
					action.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazione));
			action.addActionError(action.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
			action.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, action, "retrieveDataInvio", 
					action.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazione));
			action.addActionError(action.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
			action.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return data;
	}
	
	/**
	 * invia la mail della ricevuta  
	 * @throws ApsException 
	 */
	protected void sendMailConfermaImpresa(
			WizardDatiImpresaHelper impresa,
			String bandoGara) throws ApsException 
	{
		String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
		
		String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		
		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		
		String text = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_TESTO"), 
				new Object[] {ragioneSociale, nomeOperazione, bandoGara, data});
		if (this.isPresentiDatiProtocollazione()) {
			text = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_TESTOCONPROTOCOLLO"),
					new Object[] {ragioneSociale, nomeOperazione, bandoGara, data, this.annoProtocollo.toString(), this.numeroProtocollo});					
		}
		
		String subject = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_OGGETTO"),
				new Object[] { this.getCodice(), nomeOperazione });
		
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { (String)((IUserProfile) this.getCurrentUser().getProfile()).getValue("email") },
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}
	
	/**
	 * ... 
	 */
	private void setMailRichiestaUfficioProtocollo(
			WizardDatiImpresaHelper datiImpresa,
			String tipoComunicazione, 
			String tipoRichiesta, 
			String descBando, 
			String data) throws ApsSystemException, ApsException, XmlException 
	{
		if (1 == this.tipoProtocollazione) {
			if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
				throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}
			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio
			Map<String, byte[]> p = new HashMap<String, byte[]>();

			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.allegaDocMailUfficioProtocollo) {
				BustaRiepilogo riepilogo = GestioneBuste.getBustaRiepilogoFromSession();
				RiepilogoBusteHelper bustaRiepilogativa = riepilogo.getHelper();

				// Allegati: un file per ogni busta, contenente gli SHA1 di ogni file
				// caricato nella busta
				//if(bustaRiepilogativa!=null){
				String bustaPreQualifica = riepilogo.getDigestFileBusta(bustaRiepilogativa.getBustaPrequalifica());
				String bustaAmministrativa = riepilogo.getDigestFileBusta(bustaRiepilogativa.getBustaAmministrativa());
				String bustaTecnica = riepilogo.getDigestFileBusta(bustaRiepilogativa.getBustaTecnica());
				String bustaEconomica = riepilogo.getDigestFileBusta(bustaRiepilogativa.getBustaEconomica());

				if(bustaPreQualifica != null)
					p.put("busta_prequalifica.txt", bustaPreQualifica.getBytes());
				if(bustaAmministrativa != null)
					p.put("busta_amministrativa.txt", bustaAmministrativa.getBytes());
				if(bustaTecnica != null)
					p.put("busta_tecnica.txt", bustaTecnica.getBytes());
				if(bustaEconomica != null)
					p.put("busta_economica.txt", bustaEconomica.getBytes());
				//}
			}

			String ragioneSociale = datiImpresa.getDatiPrincipaliImpresa().getRagioneSociale();
			String codFiscale = datiImpresa.getDatiPrincipaliImpresa().getCodiceFiscale();
			String partitaIVA = datiImpresa.getDatiPrincipaliImpresa().getPartitaIVA();
			String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
			String sede = datiImpresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
					+ " " + datiImpresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()
					+ ", " + datiImpresa.getDatiPrincipaliImpresa().getCapSedeLegale()
					+ " " + datiImpresa.getDatiPrincipaliImpresa().getComuneSedeLegale()
					+ " ("+ datiImpresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")";

			String[] destinatari = this.mailUfficioProtocollo.split(",");
			
			String subject = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_OGGETTO"),
					new Object[] {this.getCodice(), tipoRichiesta});
			
			String tipoOperazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
				? this.getI18nLabel("LABEL_OFFERTA") 
				: this.getI18nLabel("LABEL_PARTECIPAZIONE") 
			);
			
			String text = null;
			if (this.allegaDocMailUfficioProtocollo && !p.isEmpty()) {
				// -- allegati
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_PROTOCOLLO_TESTOCONALLEGATI"),
						new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, tipoOperazione, descBando });
			} else {
				// -- notifica
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_TESTO"),
						new Object[] { ragioneSociale, tipoRichiesta, descBando, data });
			}
			
			if (this.isPresentiDatiProtocollazione()) {
				if(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT.equals(tipoComunicazione)) {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_TESTOCONPROTOCOLLO"),
							new Object[] {ragioneSociale, descBando, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo()});
				} else {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_TESTOCONPROTOCOLLO"),
							new Object[] {ragioneSociale, descBando, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo()});
				}
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
	 * correggi eventuali invii senza protocollazione che prevedevano la protocollazione  
	 */
	public String fixProtocollazione() {
		this.setTarget(SUCCESS);
		
		if( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError("Funzione disponibile solo per utente con ruolo di amministratore di sistema");
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			return this.getTarget(); 
		}
		
		ApsSystemUtils.getLogger().debug("fixProtocollazione BEGIN id=" + this.idComunicazione);

		ComunicazioneType comunicazione = null;
		String statoComunicazione = null;
		boolean continua = true;
		try {
			// recupera la FS11/FS10 in base all'id comunicazione
			// in stato PROCESSATA o DA PROCESSARE e non protocollata
			comunicazione = this.comunicazioniManager
				.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, 
						          Long.parseLong(this.idComunicazione));
		
			if(comunicazione == null || (comunicazione != null && comunicazione.getDettaglioComunicazione() == null)) {
				continua = false;
				String msg = "Comunicazione inesistente";
				ApsSystemUtils.getLogger().error(msg);
				this.addActionError(msg);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} 
			
			if(continua) {
				statoComunicazione = comunicazione.getDettaglioComunicazione().getStato();
				String t = comunicazione.getDettaglioComunicazione().getTipoComunicazione();
				if( !PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT.equals(t) && 
				    !PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT.equals(t) ) 
				{
					continua = false;
					String msg = "Tipologia comunicazione non valida";
					ApsSystemUtils.getLogger().error(msg);
					this.addActionError(msg);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			}
			
			if(continua) {
				statoComunicazione = comunicazione.getDettaglioComunicazione().getStato();
				String t = comunicazione.getDettaglioComunicazione().getTipoComunicazione();
				if( StringUtils.isNotEmpty(comunicazione.getDettaglioComunicazione().getNumeroProtocollo()) &&
					(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA.equals(statoComunicazione) ||
					 CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(statoComunicazione)) ) 
				{
					continua = false;
					String msg = "Comunicazione " + this.idComunicazione + " gia' protocollata con n." + comunicazione.getDettaglioComunicazione().getNumeroProtocollo() + 
								 " del " + comunicazione.getDettaglioComunicazione().getDataProtocollo(); 
					this.addActionError(msg);
					ApsSystemUtils.getLogger().error(msg);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			}
			
			
			if(continua) {
				if( !CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA.equals(statoComunicazione) &&
					!CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(statoComunicazione) ) 
				{
					continua = false;
					String msg = "Comunicazione in stato non previsto"; 
					this.addActionError(msg);
					ApsSystemUtils.getLogger().error(msg);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			}

			// riprotocolla la comunicazione FS11/FS10...
			boolean protocollazioneOk = false;
			if(continua) {
				// recupera i dati dalla comunicazione...
				IUserManager userManager = (IUserManager) ApsWebApplicationUtils
					.getBean(SystemConstants.USER_MANAGER, ServletActionContext.getRequest());
				
				this.username = comunicazione.getDettaglioComunicazione().getChiave1();
				UserDetails user = userManager.getUser(this.username);
				this.usernameEmail = (String) ((IUserProfile)user.getProfile()).getValue("email");
				this.codice = comunicazione.getDettaglioComunicazione().getChiave2();
				this.progressivoOfferta = comunicazione.getDettaglioComunicazione().getChiave3();
				String tipoComunicazione = comunicazione.getDettaglioComunicazione().getTipoComunicazione();
				
				this.operazione = 0;
				if(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT.equals(tipoComunicazione)) {
					this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
				} else if(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT.equals(tipoComunicazione)) {
					this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
				}
				
				boolean invioOfferta = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
				String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
				this.dataInvio = retrieveDataInvio(this.ntpManager, this, nomeOperazione);
				
				GestioneBuste buste = new GestioneBuste(
						this.username,
						this.codice,
						this.progressivoOfferta,
						this.operazione);
				buste.get();
				
				DettaglioGaraType dettGara = buste.getDettaglioGara();
				WizardDatiImpresaHelper impresa = buste.getImpresa();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
					
				// recupera tra gli allegati della comunicazione il file di riepilogo.pdf/riepilogo.pdf.tsd...
				byte[] fileRiepilogo = null;
				for (int i = 0; i < comunicazione.getAllegato().length; i++) {
					if(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE.equalsIgnoreCase(comunicazione.getAllegato()[i].getNomeFile())
					   || PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE.equalsIgnoreCase(comunicazione.getAllegato()[i].getNomeFile())) 
					{
						fileRiepilogo = comunicazione.getAllegato()[i].getFile();
					}
				}
				
				// recupera la SA per la configurazione di protocollazione... 
				this.appParamManager.setStazioneAppaltanteProtocollazione( dettGara.getStazioneAppaltante().getCodice() );

				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!! 
				this.tipoProtocollazione = (Integer) this.appParamManager.getTipoProtocollazione(dettGara.getStazioneAppaltante().getCodice());
				
				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.appParamManager.isConfigWSDMNonDisponibile()) {
					continua = false;
					this.setTarget(INPUT);
					String msgerr = this.getText("Errors.wsdm.configNotAvailable");
					this.addActionError(msgerr);
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
					evento.setLevel(Event.Level.ERROR);
					evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage("Configurazione non disponibile o vuota");
					evento.setDetailMessage(msgerr);
					this.getEventManager().insertEvent(evento);
				}

				if(continua) {
					if(this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA ||
					   this.tipoProtocollazione == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL) 
					{
						continua = false;
						String msg = "Protocollazione non prevista"; 
						this.addActionError(msg);
						ApsSystemUtils.getLogger().error(msg);
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					} 
				}
				
				if(continua) {
					// continua con la protocollazione
					Event evento = new Event();
					evento.setUsername(this.username);
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					
					switch (this.tipoProtocollazione) {
					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
						// ************************************************************
						// NB: LA RI-PROTOCOLLAZIONE VIA MAIL, PER ORA NON E' PREVISTA!
						// ************************************************************
						break;
					
					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
						// PROTOCOLLAZIONE WSDM
						evento.setMessage("Recupero protocollazione via WSDM per operatore " + this.username);
						
						boolean protocollazioneInoltrata = false;
						WSDocumentoType documento = null;
						WSDMProtocolloDocumentoType ris = null;
						long id = comunicazione.getDettaglioComunicazione().getId();
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
									impresa, 
									dettGara, 
									bustaRiepilogo.getHelper(),
									comunicazione,
									nomeOperazione,
									fascicoloBackOffice,
									fileRiepilogo);
			
							ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
							this.annoProtocollo = ris.getAnnoProtocollo();
							this.numeroProtocollo = ris.getNumeroProtocollo();
							this.dataProtocollo = null;
							if (ris.getDataProtocollo() != null) {
								this.dataProtocollo = UtilityDate.convertiData(ris.getDataProtocollo().getTime(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
							}
							protocollazioneInoltrata = true;
							
							this.eventManager.insertEvent(evento);
			
							// si aggiorna lo stato a 5 aggiornando inoltre anche i
							// dati di protocollazione
							documento = new WSDocumentoType();
							documento.setEntita("GARE");
							documento.setChiave1(this.codice);
							documento.setNumeroDocumento(ris.getNumeroDocumento());
							documento.setAnnoProtocollo(ris.getAnnoProtocollo());
							documento.setNumeroProtocollo(ris.getNumeroProtocollo());
							documento.setVerso(WSDocumentoTypeVerso.IN);
							documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());
			
							evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
							evento.setMessage(
									"Aggiornamento comunicazione con id " + id
									+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
									+ ", protocollata con anno " + ris.getAnnoProtocollo() 
									+ " e numero " + ris.getNumeroProtocollo());
							
							// LAPISOPERA: inserisci l'ID richiesta restituito dal protocollo nella W_INVCOM e aggiorna il messaggio dell'evento
							ProcessPageProtocollazioneAction.lapisAggiornaComunicazione(
									ris,
									documento,
									comunicazione.getDettaglioComunicazione().getId(),
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
								this.msgErrore = this.getText("Errors.updateStatoComunicazioneDaProcessare",
												 new String[] { String.valueOf(id) });
								ApsSystemUtils.logThrowable(t, this, "invio", this.msgErrore);
								this.addActionError(this.msgErrore);
								this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			
								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage(
										"Aggiornare manualmente la comunicazione con id " + id
										+ " ed inserire inoltre un documento in ingresso per entita'  " + documento.getEntita()
										+ ", chiave1 " + documento.getChiave1()
										+ ", oggetto " + documento.getOggetto()
										+ ", numero documento " + documento.getNumeroDocumento()
										+ ", anno protocollo " + documento.getAnnoProtocollo()
										+ " e numero protocollo " + documento.getNumeroProtocollo());
								ProcessPageProtocollazioneAction.lapisAggiornaMessaggioEventoCompletamentoManuale(
										ris,
										documento,
										comunicazione.getDettaglioComunicazione().getId(),
										this.appParamManager,
										evento);
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
							} else {
								evento.setError(t);
								this.eventManager.insertEvent(evento);
								ApsSystemUtils.logThrowable(t, this, "invio",
										"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");								
								this.msgErrore = this.getText("Errors.service.wsdmHandshake");
								ExceptionUtils.manageWSDMExceptionError(t, this);
//								if(t instanceof java.lang.RuntimeException && t.getCause().getMessage().contains("404")) {
//									// Servizio WSDM di configurazione non raggiungibile o non attivo
//									this.addActionError(t.getMessage());
//								}
								this.setTarget(CommonSystemConstants.PORTAL_ERROR);
								// protocollare riponendo la comunicazione in bozza
								this.annullaComunicazioneInviata(comunicazione);
								protocollazioneOk = false;
							}
						}
						break;
					default:
						// qualsiasi altro caso: non si protocolla nulla altrimenti
						// si darebbe comunicazione di chi ha presentato offerta
						break;
					}
				}
			}
			
			// concludi la protocollazione
			this.appParamManager.setStazioneAppaltanteProtocollazione(null);
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("fixProtocollazione", t);
			this.addActionError(this.getText("Errors.unexpected"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		// reimposta lo stato iniziale della comunicazione...
		if(continua) {
			if(comunicazione != null && StringUtils.isNotEmpty(statoComunicazione)) {
				try {
					DettaglioComunicazioneType[] comunicazioni = new DettaglioComunicazioneType[1];
					comunicazioni[0] = comunicazione.getDettaglioComunicazione();
					this.comunicazioniManager.updateStatoComunicazioni(comunicazioni, statoComunicazione);
					
					ApsSystemUtils.getLogger().debug("comunicazione id=" + comunicazione.getDettaglioComunicazione().getId() + " protocollata.");					
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error("fixProtocollazione", t);
					this.addActionError(this.getText("Errors.unexpected"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			}
		}
		
		ApsSystemUtils.getLogger().debug("fixProtocollazione END id=" + this.idComunicazione);
		
		return this.getTarget();
	}
	
	/**
	 * verifica se l'invio dell'offerta e' fuori tempo massimo oppure in tempo  
	 */
	protected boolean isAccessoFuoriTempoMassimo(DettaglioGaraType dettGara) {
		boolean fuoriTempo = true;
		
		Date dtInvio = retrieveDataInvio(
				this.getNtpManager(), 
				this, 
				GestioneBuste.getNomeOperazione(this.getOperazione()));
		this.setDataInvio(dtInvio);
		
		if(dtInvio != null) {
			Date dataTermine = getDataTermine(dettGara, this.operazione);
			boolean invioScaduto = (dataTermine != null && dtInvio.compareTo(dataTermine) > 0);
			if(invioScaduto) {
				fuoriTempo = true;
				// traccia solo i tentativi di accesso alle funzionalita' fuori tempo massimo
				Event evento = new Event();
				evento.setUsername(getCurrentUser().getUsername());
				evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
				evento.setLevel(Event.Level.ERROR);
				evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
				evento.setIpAddress(getCurrentUser().getIpAddress());
				evento.setSessionId(getRequest().getSession().getId());
				evento.setMessage("Accesso alla funzione " + getDescTipoEvento());
				evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + 
							" (" + UtilityDate.convertiData(dtInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
				eventManager.insertEvent(evento);
			} else {
				fuoriTempo = false;
				// traccia la data/ora di inizio di invio dell'offerta
				Event evento = new Event();
				evento.setUsername(getCurrentUser().getUsername());
				evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
				evento.setIpAddress(getCurrentUser().getIpAddress());
				evento.setSessionId(getRequest().getSession().getId());
				evento.setMessage("Accesso alla funzione " + getDescTipoEvento());
				evento.setDetailMessage("Invio in data " + 
							UtilityDate.convertiData(dtInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
				eventManager.insertEvent(evento);
			}
		}
		return fuoriTempo;
	}

	/**
	 * verifica se la richiesta di invio e' fuori tempo massimo 
	 */
	protected boolean isFuoriTempoMassimo(DettaglioGaraType dettGara) {
		boolean fuoriTempo = false;
		Date dataTermine = getDataTermine(dettGara, operazione);
		if (dataTermine != null && dataInvio.compareTo(dataTermine) > 0) {
			fuoriTempo = true;
			setTarget(CommonSystemConstants.PORTAL_ERROR);
			addActionError(getText("Errors.invioRichiestaFuoriTempoMassimo", 
											 new String[] { GestioneBuste.getNomeOperazione(operazione) }));
			
			Event evento = new Event();
			evento.setUsername(getCurrentUser().getUsername());
			evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
			evento.setLevel(Event.Level.ERROR);
			evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
			evento.setIpAddress(getCurrentUser().getIpAddress());
			evento.setSessionId(getRequest().getSession().getId());
			evento.setMessage("Accesso alla funzione " + getDescTipoEvento());
			evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO 
									+ " (" + UtilityDate.convertiData(dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
			this.getEventManager().insertEvent(evento);
		}	
		return fuoriTempo;
	}
	
	/**
	 * verifica se la gara e' sospesa 
	 */
	protected boolean isGaraSospesa(DettaglioGaraType dettGara, Event evento) {
		boolean sospesa = ("4".equals(dettGara.getDatiGeneraliGara().getStato()));
		if(sospesa) {
			evento.setDetailMessage(this.getText("Errors.invioBuste.proceduraSospesa"));
			evento.setLevel(Event.Level.ERROR);
			this.addActionError(this.getText("Errors.invioBuste.proceduraSospesa"));
		}
		return sospesa;
	}

	/**
	 * verifica se il codice CNEL della partecipazione e' valorizzato (solo in fase di offerta) 
	 */
	protected boolean isCodiceCNELMancante(BustaPartecipazione partecipazione, Event evento) {
		boolean CNELMancante = false;
		if(this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			CNELMancante = StringUtils.isEmpty(partecipazione.getHelper().getCodiceCNEL());
			if(CNELMancante) {
				evento.setDetailMessage(this.getText("Errors.invioBuste.CNELMancante"));
				evento.setLevel(Event.Level.ERROR);
				this.addActionError(this.getText("Errors.invioBuste.CNELMancante"));
			}
		}
		return CNELMancante;
	}
	

}
