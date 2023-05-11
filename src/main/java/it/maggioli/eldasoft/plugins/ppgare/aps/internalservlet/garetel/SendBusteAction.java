package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;
import com.opensymphony.xwork2.inject.Inject;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
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
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
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

/**
 * Action di gestione dell'invio buste.
 * 
 * @author Marco.Perazzetta
 * @author Stefano.Sabbadin
 */
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

//	/**
//	 * Ritorna la directory per i file temporanei, prendendola da
//	 * struts.multipart.saveDir (struts.properties) se valorizzata
//	 * correttamente, altrimenti da javax.servlet.context.tempdir
//	 * 
//	 * @return path alla directory per i file temporanei
//	 */
//	private File getTempDir() {
//		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), this.multipartSaveDir);
//	}

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
	 * conferma ed invia  
	 */
	public String confirmInvio() {
		this.setTarget("reopen");

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
				WizardDatiImpresaHelper impresa = buste.getImpresa();
				
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

					Date dataTermine = getDataTermine(dettGara, this.operazione);
					
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
					
					//-----------------------------------------------------------------------
					// traccia i tentativi di accesso alle funzionalita' fuori tempo massimo
					if (dataTermine != null && this.dataInvio.compareTo(dataTermine) > 0) {
						controlliOk = false;
						this.addActionError(this.getText("Errors.invioRichiestaFuoriTempoMassimo", new String[] { nomeOperazione }));
						
						Event fuoriTempo = new Event();
						fuoriTempo.setUsername(this.getCurrentUser().getUsername());
						fuoriTempo.setDestination(dettGara.getDatiGeneraliGara().getCodice());
						fuoriTempo.setLevel(Event.Level.ERROR);
						fuoriTempo.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
						fuoriTempo.setIpAddress(this.getCurrentUser().getIpAddress());
						fuoriTempo.setSessionId(this.getRequest().getSession().getId());
						fuoriTempo.setMessage("Accesso alla funzione " + this.getDescTipoEvento());
						fuoriTempo.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						this.getEventManager().insertEvent(fuoriTempo);
					}  

					// verifica se la gara e' sospesa...
					boolean sospesa = ("4".equals(dettGara.getDatiGeneraliGara().getStato()));
					if(sospesa) {
						controlliOk = false;
						evento.setDetailMessage(this.getText("Errors.invioBuste.proceduraSospesa"));
						evento.setLevel(Event.Level.ERROR);
						this.addActionError(this.getText("Errors.invioBuste.proceduraSospesa"));
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
																		 new String[] { PortGareSystemConstants.BUSTA_ECO }));
									}
								} else {
									WizardDocumentiBustaHelper wizardBustaEco = bustaEconomica.getHelperDocumenti();
									if (wizardBustaEco != null && wizardBustaEco.isDatiModificati()) {
										controlliOk = false;
										this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
																		 new String[] { PortGareSystemConstants.BUSTA_ECO }));
									}
								}
							}
						}
					}

					BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
					WizardPartecipazioneHelper partecipazioneHelper = bustaPartecipazione.getHelper();
					BustaRiepilogo riepilogo = buste.getBustaRiepilogo();
					RiepilogoBusteHelper bustaRiepilogativa = riepilogo.getHelper();
					// tramite servizio controllo che non siano stati inseriti 
					// nuovi documenti obbligatori comuni per le buste tecniche via BO
					riepilogo.integraBusteFromBO();
					
					if (controlliOk) {	
						if(domandaPartecipazione) {
							// busta prequalifica
							controlliOk = checkBusta(
									impresa,
									dettGara,
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA,
									PortGareSystemConstants.BUSTA_PRE,
									PortGareSystemConstants.BUSTA_PRE_QUALIFICA);
							
							// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
							if( controlliOk && !bustaRiepilogativa.getPrimoAccessoPrequalificaEffettuato() ) {
								this.addActionError(this.getText("Errors.invioBuste.dataNotSent",
																 new String[] { "Busta prequalifica" }));
								controlliOk = false;
							}
						}  

						if(invioOfferta) {
							// busta amministrativa
							if( !dettGara.getDatiGeneraliGara().isNoBustaAmministrativa() ) {
								controlliOk = checkBusta(
										impresa,
										dettGara,
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
										PortGareSystemConstants.BUSTA_AMM,
										PortGareSystemConstants.BUSTA_AMMINISTRATIVA);
								
								// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
								if( controlliOk && !bustaRiepilogativa.getPrimoAccessoAmministrativaEffettuato() ) {
									this.addActionError(this.getText("Errors.invioBuste.dataNotSent",
																	 new String[] { "Busta amministrativa" }));
									controlliOk = false;
								}
							}

							// busta tecnica
							if (this.bandiManager.isGaraConOffertaTecnica(this.codice)) {
								// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
								if( !bustaRiepilogativa.getPrimoAccessoTecnicaEffettuato() ) {
									this.addActionError(this.getText("Errors.invioBuste.dataNotSent",
																	 new String[] { "Busta tecnica" }));
									controlliOk = false;
								}
								
								if (controlliOk) {
									if (apriOEPVBustaTecnica) {
										controlliOk = controlliOk && checkBustaTecnica(
												dettGara,
												impresa.getDatiPrincipaliImpresa().getTipoImpresa(),
												PortGareSystemConstants.BUSTA_TEC);
									} else {
										controlliOk = controlliOk && checkBusta(
												impresa,
												dettGara,
												PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
												PortGareSystemConstants.BUSTA_TEC,
												PortGareSystemConstants.BUSTA_TECNICA);
									}
								}
							}

							// busta economica
							// in caso di OEPV a costo fisso non va controllata
							// la parte economica...
							if(!costoFisso) {
								// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
								if( !bustaRiepilogativa.getPrimoAccessoEconomicaEffettuato() ) {
									this.addActionError(this.getText("Errors.invioBuste.dataNotSent",
																	 new String[] { "Busta economica" }));
									controlliOk = false;
								}
								
								if (controlliOk) {
									if (dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
										controlliOk = controlliOk && checkBustaEconomica(
												dettGara,
												impresa.getDatiPrincipaliImpresa().getTipoImpresa(),
												PortGareSystemConstants.BUSTA_ECO);
									} else {
										controlliOk = controlliOk && checkBusta(
												impresa,
												dettGara,
												PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
												PortGareSystemConstants.BUSTA_ECO,
												PortGareSystemConstants.BUSTA_ECONOMICA);
									}
								}
							}
						}
					}
					
					if( !controlliOk && !sospesa ) {
						String documentiMancanti = this.getListaDocumentiMancanti(bustaRiepilogativa, domandaPartecipazione);
						if(StringUtils.isNotEmpty(documentiMancanti)) {
							evento.setDetailMessage(documentiMancanti);
							evento.setLevel(Event.Level.ERROR);
						}
					}
					
					// per gare ristrette in fase di domanda di partecipazione
					// controlla se i lotti dell'offerta sono utilizzati anche in altre offerte...
					if(controlliOk && domandaPartecipazione) {
						boolean lottiInAltreOfferte = this.isLottiPresentiInAltreOfferte(bustaPartecipazione); 
						if (lottiInAltreOfferte) {
							// alcuni lotti dell'offerta sono presenti in altre offerte...
							evento.setDetailMessage("Lotti presenti in altre offerte");
							evento.setLevel(Event.Level.ERROR);
							controlliOk = false;
						}
					}

					// controlla se esistono documenti senza contenuto nelle buste...
					// o se esistono documenti nelle buste, con dimensione del contenuto inviato 
					// diversa dall'originale ...
					VerificaDocumentiCorrotti documentiCorrotti = new VerificaDocumentiCorrotti(bustaRiepilogo); 
					if (controlliOk) {
						controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti();
						
						if(documentiCorrotti.size() > 0) {
							// visualizza la lista dei documenti nulli o corrotti
							documentiCorrotti.addActionErrors(this);
							evento.setDetailMessage(documentiCorrotti.getEventDetailMessage());
							evento.setLevel(Event.Level.ERROR);
						}
					}
					
					// (Questionari - QFORM) 
					// 1) controlla se e' presente un questionario e se lo stato e' "completato"
					// 2) verifica se e' cambiata la modulistica dei questionari 
					if (controlliOk) {
						if(domandaPartecipazione) {
							if(bustaRiepilogativa.getBustaPrequalifica() != null) {
								controlliOk = controlliOk & this.verificaQuestionario( 
										this.codice,
										null,
										null,
										PortGareSystemConstants.BUSTA_PRE_QUALIFICA, 
										bustaRiepilogativa.getBustaPrequalifica(),
										evento);
							}	
						} else {
							if(bustaRiepilogativa.getBustaAmministrativa() != null) {
								controlliOk = controlliOk & this.verificaQuestionario(
										this.codice,
										null,
										null,
										PortGareSystemConstants.BUSTA_AMMINISTRATIVA, 
										bustaRiepilogativa.getBustaAmministrativa(),
										evento);
							}
							if(bustaRiepilogativa.getBustaTecnica() != null) {
								controlliOk = controlliOk & this.verificaQuestionario(
										this.codice,
										null,
										null,
										PortGareSystemConstants.BUSTA_TECNICA, 
										bustaRiepilogativa.getBustaTecnica(),
										evento);
							}
							if(bustaRiepilogativa.getBustaEconomica() != null) {
								controlliOk = controlliOk & this.verificaQuestionario(
										this.codice,
										null,
										null,
										PortGareSystemConstants.BUSTA_ECONOMICA, 
										bustaRiepilogativa.getBustaEconomica(),
										evento);
							}
						}
					}
					
					// inserisci l'evento di verifica preinvio
					this.getEventManager().insertEvent(evento);
					
					// se tutti i controlli sono andati bene procedi al passo successivo
					if (controlliOk) {
						this.setTarget(SUCCESS);
					}
				}
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "confirm");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "confirm");
				ExceptionUtils.manageExceptionError(e, this);
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

				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.appParamManager.isConfigWSDMNonDisponibile()) {
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

				this.dataInvio = retrieveDataInvio(
						this.ntpManager, 
						this, 
						GestioneBuste.getNomeOperazione(this.operazione));

				if (this.dataInvio != null) {
					Date dataTermine = getDataTermine(dettGara, this.operazione);
					boolean invioScaduto = (dataTermine != null && this.dataInvio.compareTo(dataTermine) > 0);					
					if(invioScaduto) {
						controlliOk = false;
						this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
						
						// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
						Event evento = new Event();
						evento.setUsername(this.getCurrentUser().getUsername());
						evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
						evento.setLevel(Event.Level.ERROR);
						evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
						evento.setIpAddress(this.getCurrentUser().getIpAddress());
						evento.setSessionId(this.getRequest().getSession().getId());
						evento.setMessage("Accesso alla funzione " + this.getDescTipoEvento());
						evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + 
									" (" + UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						this.getEventManager().insertEvent(evento);
					}
					
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
	
							if(bustaEconomica.getId() > 0) {
								controlliOk = controlliOk && bustaEconomica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							}
						}
					
						bustaPartecipazione.get();
						if(controlliOk && bustaPartecipazione.getId() > 0) {
							bustaPartecipazione.getHelper().setDataPresentazione(this.dataInvio);
							inviataComunicazione = bustaPartecipazione.sendConfirm();
						}
					}

					// se qualcosa va in errore imposta il target a CommonSystemConstants.PORTAL_ERROR
					// senza un messaggio di errore viene restituita ad una pagina vuota...
					if(!controlliOk && !invioScaduto && !sospesa) {
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
		boolean inTesta = false;		// default, inserisci in coda
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			String v = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
			if("1".equals(v)) {
				inTesta = true;
			}
		}
	    
		WSDMProtocolloAllegatoType[] allegati = createAttachents(datiImpresaHelper, dettGara, bustaRiepilogativa,
				comunicazionePartecipazione, nomeOperazione, fileRiepilogo, ragioneSociale, codiceFiscale, indirizzo,
				inTesta);
		
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

	private WSDMProtocolloAllegatoType[] createAttachents(WizardDatiImpresaHelper datiImpresaHelper,
			DettaglioGaraType dettGara, RiepilogoBusteHelper bustaRiepilogativa,
			ComunicazioneType comunicazionePartecipazione, String nomeOperazione, byte[] fileRiepilogo,
			String ragioneSociale, String codiceFiscale, String indirizzo, boolean inTesta)
			throws Exception 
	{
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[2];

		int n2 = allegati.length - 1;
		if(inTesta) {
			n2 = 0;
		}
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setTitolo(MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_OGGETTO"), 
												   new Object[] {this.getCodice(), nomeOperazione}));
		allegati[n2].setTipo("pdf");
		allegati[n2].setNome("comunicazione.pdf");

		String contenuto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_PROTOCOLLO_TESTOCONALLEGATI"),
				new Object[] {
					ragioneSociale,
					codiceFiscale,
					datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA(),
					this.usernameEmail,
					indirizzo,
					UtilityDate.convertiData(this.getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
					(this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
							? this.getI18nLabelFromDefaultLocale("LABEL_OFFERTA") 
							: this.getI18nLabelFromDefaultLocale("LABEL_PARTECIPAZIONE")),
					dettGara.getDatiGeneraliGara().getOggetto() });
		
		boolean isActiveFunctionPdfA;
		try {
			isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A");
		} catch (Exception e1) {
			throw new ApsException(e1.getMessage(),e1);
		}
		
		byte[] contenutoPdf = null;
		if(isActiveFunctionPdfA) {
			try {
				ApsSystemUtils.getLogger().info("Trasformazione contenuto in PDF-A");
				InputStream iccFilePath = new FileInputStream(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
				contenutoPdf = UtilityStringhe.string2PdfA(contenuto, iccFilePath);
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
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId() + "/" + n2);

		int i = 0;
		if(inTesta)
			i = 1;
				
		// aggiungi il "riepilogo_buste.pdf"
		boolean hasMarcaturaTemporale = false;
		try {
			hasMarcaturaTemporale = this.customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE");
		} catch (Exception e) {
			throw new ApsException("Non e' stato possibile leggere la configurazione ACT per l'objectId INVIOFLUSSI feature MARCATEMPORALE");
		}				
		
		// recupera il pdf di riepilogo delle buste (pdf/tsd) ed aggiungilo agli allegati del WSDM...		
		if(fileRiepilogo == null) {
			BustaRiepilogo bustaRiepilogo = GestioneBuste.getBustaRiepilogoFromSession();
			fileRiepilogo = bustaRiepilogo.getPdfRiepilogoBuste();
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
	 * @param datiImpresaHelper
	 *            dati dell'impresa
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
	 * verifica se esistono documenti richiesti mancanti in una busta
	 */
	private String getCountDocumentiMancanti(RiepilogoBustaBean busta) {
		String msg = null;
		String ids = "";
		int n = 0;
		if(busta != null && busta.getDocumentiMancanti() != null && busta.getDocRichiesti() != null) {
			for(int j = 0; j < busta.getDocRichiesti().size(); j++) {
				boolean trovato = false;
				for(int i = 0; i < busta.getDocumentiMancanti().size() && !trovato; i++) {
					trovato = (busta.getDocRichiesti().get(j).isObbligatorio() &&
							   busta.getDocumentiMancanti().get(i).getId() == busta.getDocRichiesti().get(j).getId());
				}
				if( trovato ) {
					n++;
					ids = ids + (StringUtils.isNotEmpty(ids) ? ", " : "") + busta.getDocRichiesti().get(j).getId();
				}
			}
		}
		if(n > 0) {
			msg = n + " (id's: " + ids + ")";
		}
		return msg;
	}
		
	/**
	 * restituisce la lista dei documenti presenti in una richiesta di partecipazione/offerta
	 */
	protected String getListaDocumentiMancanti(
			RiepilogoBusteHelper riepilogo,
			boolean domandaPartecipazione) 
	{
		String documenti = "";
		String s;
		
		if(domandaPartecipazione) {
			s = getCountDocumentiMancanti(riepilogo.getBustaPrequalifica());
			if(s != null) {
				documenti += "- prequalifica: " + s + "\n";
			}
		} else {
			s = getCountDocumentiMancanti(riepilogo.getBustaAmministrativa());
			if(s != null) {
				documenti += "- amministrativa: " + s + "\n";
			}
			
			boolean garaLotti = riepilogo.getListaCompletaLotti() != null && 
			                    riepilogo.getListaCompletaLotti().size() > 0;
			if(garaLotti) {
				// GARA A LOTTI
				for(int i = 0; i < riepilogo.getListaCompletaLotti().size(); i++){
					String lotto = riepilogo.getListaCompletaLotti().get(i);
					
					s = getCountDocumentiMancanti(riepilogo.getBusteTecnicheLotti().get(lotto));
					if(s != null) {
						documenti += "- lotto " + lotto + " tecnica: " + s + "\n";
					}
					s = getCountDocumentiMancanti(riepilogo.getBusteEconomicheLotti().get(lotto));
					if(s != null) {
						documenti += "- lotto " + lotto + " economica: " + s + "\n";
					}
				}
			} else {
				// GARA LOTTO UNICO
				s = getCountDocumentiMancanti(riepilogo.getBustaTecnica());
				if(s != null) {
					documenti += "- tecnica: " + s + "\n";
				}
				
				s = getCountDocumentiMancanti(riepilogo.getBustaEconomica());
				if(s != null) {
					documenti += "- economica: " + s + "\n";
				}
			}
		}	
		
		if(StringUtils.isNotEmpty(documenti)) {
			documenti = "Alcuni documenti richiesti risultano mancanti nelle seguenti buste: \n" + documenti;
		}
		
		return documenti;
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
	 * ... 
	 */
	private boolean checkDocumentiBusta(
			String nomeBusta,
			ListaDocumentiType documenti,
			List<DocumentazioneRichiestaType> documentiRichiesti)
		throws ApsException 
	{
		boolean controlliOk = true;

		DocumentoType documento;
		boolean docFound;

		for (int i = 0; i < documentiRichiesti.size(); i++) {
			docFound = false;
			if (!controlliOk) {
				break;
			}
			if (documentiRichiesti.get(i).isObbligatorio()) {
				for (int j = 0; j < documenti.sizeOfDocumentoArray(); j++) {
					documento = documenti.getDocumentoArray(j);
					if (documento.getId() != 0 && documento.getId() == documentiRichiesti.get(i).getId()) {
						docFound = true;
						break;
					}
				}
				if (!docFound) {
					controlliOk = false;
					this.addActionError(this.getText("Errors.docRichiestoObbligatorioNotFound",
										new String[] { nomeBusta, documentiRichiesti.get(i).getNome() }));
				}
			}
		}

		for (int j = 0; j < documenti.sizeOfDocumentoArray(); j++) {
			documento = documenti.getDocumentoArray(j);
			if (documento.getNomeFile().length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				controlliOk = false;
				if (documento.getId() != 0) {
					this.addActionError(this.getText("Errors.docRichiestoOverflowFileNameLength",
										new String[] { nomeBusta, documento.getNomeFile() }));
				} else {
					this.addActionError(this.getText("Errors.docUlterioreOverflowFileNameLength",
										new String[] { nomeBusta, documento.getNomeFile() }));
				}
			}
		}
		return controlliOk;
	}

	/**
	 * ... 
	 */
	private boolean checkDocumenti(
			ComunicazioneType comunicazioneBusta,
			String nomeBusta,
			List<DocumentazioneRichiestaType> documentiRichiesti) throws XmlException, IOException 
	{
		boolean controlliOk = true;
		DocumentazioneBustaDocument bustaDocument;
		try {						
			bustaDocument = BustaDocumenti.getBustaDocument(comunicazioneBusta);
			ListaDocumentiType documenti = bustaDocument.getDocumentazioneBusta().getDocumenti();
			controlliOk = checkDocumentiBusta(
					nomeBusta, 
					documenti, 
					documentiRichiesti);
		} catch (Throwable ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
				this.addActionError(this.getText("Errors.invioBuste.xmlBustaNotFound", new String[] { nomeBusta }));
			} else {
				this.addActionError(ex.getMessage());
			}
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			controlliOk = false;
		}
		return controlliOk;
	}
	
	/**
	 * ... 
	 */
	private boolean checkDocumentiOfferta(
			ComunicazioneType comunicazioneBusta,
			String nomeBusta,
			List<DocumentazioneRichiestaType> documentiRichiesti) 
		throws XmlException, IOException 
	{
		boolean controlliOk = true;
		BustaEconomicaDocument bustaDocument;
		try {			
			BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
			bustaDocument = (BustaEconomicaDocument)bustaEco.getBustaDocument(); //getBustaEconomicaDocument();
			ListaDocumentiType documenti = bustaDocument.getBustaEconomica().getDocumenti();
		
			controlliOk = controlliOk && 
				checkDocumentiBusta(nomeBusta, documenti, documentiRichiesti);
		
		} catch (Throwable ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
				this.addActionError(this.getText("Errors.invioBuste.xmlBustaNotFound", new String[] { nomeBusta }));
			} else {
				this.addActionError(ex.getMessage());
			}
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			controlliOk = false;
		}
		return controlliOk;
	}
	
	/**
	 * ... 
	 */
	private boolean checkDocumentiOffertaTecnica(
			ComunicazioneType comunicazioneBusta,
			String nomeBusta,
			List<DocumentazioneRichiestaType> documentiRichiesti) throws XmlException, IOException 
	{
		boolean controlliOk = true;
		BustaTecnicaDocument bustaDocument;
		try {			
			BustaTecnica bustaTec = GestioneBuste.getBustaTecnicaFromSession();
			bustaDocument = (BustaTecnicaDocument)bustaTec.getBustaDocument();
			ListaDocumentiType documenti = bustaDocument.getBustaTecnica().getDocumenti();
			
			controlliOk = controlliOk && 
				checkDocumentiBusta(nomeBusta, documenti, documentiRichiesti);
			
		} catch (Throwable ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
				this.addActionError(this.getText("Errors.invioBuste.xmlBustaNotFound", new String[] { nomeBusta }));
			} else {
				this.addActionError(ex.getMessage());
			}
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			controlliOk = false;
		}
		return controlliOk;
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
	private static boolean hasDocObbligatori(List<DocumentazioneRichiestaType> documentiRichiesti) {
		boolean found = false;
		for (DocumentazioneRichiestaType documentoRichiesto : documentiRichiesti) {
			if (documentoRichiesto.isObbligatorio()) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * ... 
	 */
	public boolean checkBusta(
			WizardDatiImpresaHelper datiImpresa,
			DettaglioGaraType dettGara, 
			String tipoBustaRichiesta,
			String nomeBusta, 
			int codiceBusta) 
		throws ApsException, XmlException, IOException 
	{
		boolean bustaOk = true;
		List<DocumentazioneRichiestaType> documentiRichiesti;
		DettaglioComunicazioneType dettComunicazioneBustaInviata = ComunicazioniUtilities
				.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						tipoBustaRichiesta);

		if (dettComunicazioneBustaInviata != null) {
			this.addActionMessage(this.getText("Errors.send.alreadySent", new String[] { nomeBusta }));
		} else {
			// verifico se l'ente ha specificato dei documenti obbligatori
			// da inserire nella busta, altrimenti bypasso il check su
			// questa busta
			WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

			String codiceLotto = null;
			// nella gara a lotto unico i documenti stanno nel lotto, nella gara
			// ad offerta unica stanno nella gara
			if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
				codiceLotto = dettGara.getDatiGeneraliGara().getCodice();
			}
			documentiRichiesti = this.bandiManager
					.getDocumentiRichiestiBandoGara(
							this.codice, 
							codiceLotto,
							datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
							partecipazioneHelper.isRti(), 
							codiceBusta + "");

			if (hasDocObbligatori(documentiRichiesti)) {

				// verifico che ci siano i documenti richiesti per la busta
				DettaglioComunicazioneType dettComunicazioneBusta = ComunicazioniUtilities
						.retrieveComunicazione(
								this.comunicazioniManager,
								this.getCurrentUser().getUsername(),
								this.codice,
								this.progressivoOfferta,
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
								tipoBustaRichiesta);

				if (dettComunicazioneBusta != null) {
					ComunicazioneType comunicazioneBusta = this.comunicazioniManager
							.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
											  dettComunicazioneBusta.getId());
					bustaOk = checkDocumenti(comunicazioneBusta, nomeBusta, documentiRichiesti);
				} else {
					bustaOk = false;
					this.addActionError(this.getText("Errors.invioBuste.dataNotSent",
													 new String[] { nomeBusta }));
				}
			}
		}
		return bustaOk;
	}

	/**
	 * ... 
	 */
	public boolean checkBustaEconomica(
			DettaglioGaraType dettGara,
			String tipoImpresa, 
			String nomeBusta) throws ApsException, XmlException, IOException 
	{
		boolean bustaOk = true;

		String tipoBustaRichiesta = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;
		int codiceBusta = PortGareSystemConstants.BUSTA_ECONOMICA;

		List<DocumentazioneRichiestaType> documentiRichiesti;
		DettaglioComunicazioneType dettComunicazioneBustaInviata = ComunicazioniUtilities
				.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						tipoBustaRichiesta);

		if (dettComunicazioneBustaInviata != null) {
			this.addActionMessage(this.getText("Errors.send.alreadySent", new String[] { nomeBusta }));
		} else {

			// verifico se l'ente ha specificato dei documenti obbligatori
			// da inserire nella busta, altrimenti bypasso il check su
			// questa busta
			WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

			String codiceLotto = null;
			// nella gara a lotto unico i documenti stanno nel lotto, nella gara
			// ad offerta unica stanno nella gara
			if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
				codiceLotto = dettGara.getDatiGeneraliGara().getCodice();
			}
			documentiRichiesti = this.bandiManager.getDocumentiRichiestiBandoGara(
					this.codice, 
					codiceLotto,
					tipoImpresa, 
					partecipazioneHelper.isRti(),
					codiceBusta + "");

			// verifico che ci siano i documenti richiesti per la busta
			DettaglioComunicazioneType dettComunicazioneBusta = ComunicazioniUtilities
					.retrieveComunicazione(
							this.comunicazioniManager, 
							this.getCurrentUser().getUsername(), 
							this.codice,
							this.progressivoOfferta,
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							tipoBustaRichiesta);

			if (dettComunicazioneBusta != null) {
				ComunicazioneType comunicazioneBusta = this.comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
										  dettComunicazioneBusta.getId());
				bustaOk = checkDocumentiOfferta(
						comunicazioneBusta, 
						nomeBusta,
						documentiRichiesti);
			} else {
				bustaOk = false;
				this.addActionError(this.getText("Errors.invioBuste.dataNotSent", new String[] { nomeBusta }));
			}
		}
		return bustaOk;
	}
	
	/**
	 * ... 
	 */
	public boolean checkBustaTecnica(
			DettaglioGaraType dettGara,
			String tipoImpresa, 
			String nomeBusta) throws ApsException, XmlException, IOException 
	{
		boolean bustaOk = true;

		String tipoBustaRichiesta = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA;
		int codiceBusta = PortGareSystemConstants.BUSTA_TECNICA;

		List<DocumentazioneRichiestaType> documentiRichiesti;
		DettaglioComunicazioneType dettComunicazioneBustaInviata = ComunicazioniUtilities
				.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						tipoBustaRichiesta);

		if (dettComunicazioneBustaInviata != null) {
			this.addActionMessage(this.getText("Errors.send.alreadySent", new String[] { nomeBusta }));
		} else {

			// verifico se l'ente ha specificato dei documenti obbligatori
			// da inserire nella busta, altrimenti bypasso il check su
			// questa busta
			WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

			String codiceLotto = null;
			// nella gara a lotto unico i documenti stanno nel lotto, nella gara
			// ad offerta unica stanno nella gara
			if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
				codiceLotto = dettGara.getDatiGeneraliGara().getCodice();
			}
			documentiRichiesti = this.bandiManager.getDocumentiRichiestiBandoGara(
					this.codice, 
					codiceLotto,
					tipoImpresa, 
					partecipazioneHelper.isRti(),
					codiceBusta + "");

			// verifico che ci siano i documenti richiesti per la busta
			DettaglioComunicazioneType dettComunicazioneBusta = ComunicazioniUtilities
					.retrieveComunicazione(
							this.comunicazioniManager, 
							this.getCurrentUser().getUsername(), 
							this.codice,
							this.progressivoOfferta,
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							tipoBustaRichiesta);

			if (dettComunicazioneBusta != null) {

				ComunicazioneType comunicazioneBusta = this.comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
										  dettComunicazioneBusta.getId());

				bustaOk = checkDocumentiOffertaTecnica(
						comunicazioneBusta, 
						nomeBusta,
						documentiRichiesti);
			} else {
				bustaOk = false;
				this.addActionError(this.getText("Errors.invioBuste.dataNotSent", new String[] { nomeBusta }));
			}
		}
		return bustaOk;
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
	 * per le gare a lotti, verifica se i lotti dell'offerta sono utilizzati in altre offerte 
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	protected boolean isLottiPresentiInAltreOfferte(BustaPartecipazione bustaPartecipazione) 
		throws ApsException, XmlException 
	{		 
		boolean lottiDuplicati = false;
		
		WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper();
		DettaglioGaraType gara = bustaPartecipazione.getGestioneBuste().getDettaglioGara();
		
		if(partecipazione.getLotti() != null) {
			// recupera l'elenco dei lotti dalla gara per evitare la rilettura dal servizio...
			//LottoDettaglioGaraType[] listaLotti = gara.getLotto();
			HashMap<String, LottoDettaglioGaraType> listaLotti = new HashMap<String, LottoDettaglioGaraType>(); 
			for(int i = 0; i < gara.getLotto().length; i++) {
				listaLotti.put(gara.getLotto()[i].getCodiceLotto(), gara.getLotto()[i]);
			}
			
			boolean invioOfferta = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
				 
			String tipoComunicazione = (invioOfferta
				? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
				: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
	
			String progressivoOfferta = (partecipazione.getProgressivoOfferta() != null ? partecipazione.getProgressivoOfferta() : "");
	 
			// recupera l'elenco delle comunicazioni FS11/FS10...
			DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
			criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
			criteriRicerca.setChiave2(partecipazione.getIdBando());
			//criteriRicerca.setChiave3(*);
			//criteriRicerca.setStato(*);
			criteriRicerca.setTipoComunicazione(tipoComunicazione);
			List<DettaglioComunicazioneType> comunicazioni = this.getComunicazioniManager().getElencoComunicazioni(criteriRicerca);
			
			if(comunicazioni != null && comunicazioni.size() > 0) {
				
				// NB: in caso di gara ristretta in fase di domanda di partecipazione
				// recupera i dati della partecipazione dalla comunicazione...
				if(bustaPartecipazione.getGestioneBuste().isRistretta() &&
				   bustaPartecipazione.getGestioneBuste().isDomandaPartecipazione()) 
				{
					ApsSystemUtils.getLogger().debug("Gara ristretta in fase di prequalifica, " + 
													 "recupera partecipazione (IdComunicazioneTipoPartecipazione=?)... UNDEFINED");					
				}
				
				// controlla se ci sono lotti duplicati nelle altre domande/offerte...
				for(int i = 0; i < comunicazioni.size(); i++) {
					String progOfferta = comunicazioni.get(i).getChiave3();
					String prog = Integer.toString(i + 1);
					
					if( !progressivoOfferta.equals(progOfferta) ) {
					
						ComunicazioneType comunicazione = this.getComunicazioniManager()
							.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
										  	  comunicazioni.get(i).getId());
						
						WizardPartecipazioneHelper p = new WizardPartecipazioneHelper(comunicazione);
						if(p != null && p.getLotti() != null) {
							// verifica se ci sono lotti presenti in un'altra offerta
							Iterator<String> lotti = p.getLotti().iterator();
							while(lotti.hasNext()) {
								String codLotto = lotti.next();
								if(partecipazione.getLotti().contains(codLotto)) {
									lottiDuplicati = true;
									
									// trova le informazioni del lotto... 
									// NB: il lotto dovrebbe SEMPRE esistere nella lista !!!
									LottoDettaglioGaraType info = listaLotti.get(codLotto);
									String lotto = (info != null ? info.getCodiceInterno() : "");
									String keyMsg = (invioOfferta
													 ? "Errors.invioBusteLotti.lottoDuplicatoInAltraOfferta"
													 : "Errors.invioBusteLotti.lottoDuplicatoInAltraDomanda");
									this.addActionError(this.getText(keyMsg, new String[] { lotto, "#" + prog }));
								}
							}
						}
					}
				}
			}
		}
		
		return lottiDuplicati;
	}

	/**
	 * verifica se una data busta ha un questionario associato e completato
	 * e se la modulistica e' stata cambiata dall'ultima compilazione
	 */
	protected boolean verificaQuestionario(
			String codiceGara,
			String codiceLotto,
			String codiceInternoLotto,
			int tipoBusta, 
			RiepilogoBustaBean busta, 
			Event evento) 
	{
		boolean controlliOk = true;
		boolean modulisticaCambiata = false;
		if(busta != null) {
			try {
              String descBusta = null;
              if (tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
                descBusta = this.getI18nLabel("LABEL_BUSTA_PREQUALIFICA");
              } else if (tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
                descBusta = this.getI18nLabel("LABEL_BUSTA_AMMINISTRATIVA");
              } else if (tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
                descBusta = this.getI18nLabel("LABEL_BUSTA_TECNICA");
                if (StringUtils.isNotEmpty(codiceInternoLotto)) {
                  // si esplicita il lotto non completo
                  descBusta += " (" + this.getI18nLabel("LABEL_LOTTO") + " " + codiceInternoLotto + ")";
                }
              } else if (tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
                descBusta = this.getI18nLabel("LABEL_BUSTA_ECONOMICA");
                if (StringUtils.isNotEmpty(codiceInternoLotto)) {
                  // si esplicita il lotto non completo
                  descBusta += " (" + this.getI18nLabel("LABEL_LOTTO") + " " + codiceInternoLotto + ")";
                }
              }

				codiceLotto = (StringUtils.isNotEmpty(codiceLotto) ? codiceLotto : codiceGara);
				
				// verifica se e' attiva la gestione del questionario per la gara
				QuestionarioType q = WizardDocumentiBustaHelper.getQuestionarioAssociatoBO(
						codiceLotto,
						codiceGara, 
						null, 
						tipoBusta);
				
				if(busta.isQuestionarioPresente()) {
					// verifica se e' cambiata la modulistica del questionario...
					long idModello = (q != null ? q.getId() : 0);
					long idBusta = busta.getQuestionarioId();
					if( idModello != idBusta ) {
						modulisticaCambiata = true;
					}
					
					// verifica se il questionario non e' variato ed e' completo...
					if(!modulisticaCambiata && busta.isQuestionarioCompletato()) {
						// questionario completato
						controlliOk = true;
					} else {
						// questionario incompleto o modulistica cambiata...
						controlliOk = false;
						String msg = this.getText("Errors.invioBuste.nullSurvey", new String[] {descBusta});
						this.addActionError(msg);
						if(StringUtils.isNotEmpty(evento.getDetailMessage())) {
							msg = evento.getDetailMessage() + "\n" + msg;
						}
						evento.setDetailMessage(msg);
						evento.setLevel(Event.Level.ERROR);
					}
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "isQuestionarioCompleto");
				evento.setLevel(Event.Level.ERROR);
				evento.setError(t);
			}
		}
		
		//if(modulisticaCambiata) {
		//	//...
		//}
		
		return controlliOk;
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
		
		ApsSystemUtils.getLogger().info("fixProtocollazione BEGIN id=" + this.idComunicazione);

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
						// NB: LA RI-PROTOCOLLAZIONE VIA MAIL, PER ORA NON E' PREVISTA!
//						// PROTOCOLLAZIONE MAIL
//						evento.setMessage("Protocollazione via mail a " + this.mailUfficioProtocollo);
//						
//						try {
//							// si aggiorna lo stato a 5 tutte le comunicazioni relativa alle buste (FS11/FS10)
//							evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
//							evento.setMessage(
//									"Aggiornamento comunicazione con id " + comunicazione.getDettaglioComunicazione().getId() + 
//									" allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
//							this.getComunicazioniManager().updateStatoComunicazioni(
//									new DettaglioComunicazioneType[] {comunicazione.getDettaglioComunicazione()}, 
//									CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
//							this.eventManager.insertEvent(evento);
//							this.dataProtocollo = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
//							
//							protocollazioneOk = true;
//							
//						} catch (Throwable t) {
//							evento.setError(t);
//							this.eventManager.insertEvent(evento);
//							ApsSystemUtils.logThrowable(t, this, "send", "Per errori durante la connessione al server di posta, non e' stato possibile inviare la richiesta all''ufficio protocollo");
//							ExceptionUtils.manageExceptionError(t, this);
//							this.addActionError(this.getText("Per errori durante la connessione al server di posta, non e' stato possibile inviare la richiesta all''ufficio protocollo"));
//							this.setTarget(CommonSystemConstants.PORTAL_ERROR);
//							protocollazioneOk = false;
//						}
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
					
					ApsSystemUtils.getLogger().info("comunicazione id=" + comunicazione.getDettaglioComunicazione().getId() + " protocollata.");					
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error("fixProtocollazione", t);
					this.addActionError(this.getText("Errors.unexpected"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			}
		}
		
		ApsSystemUtils.getLogger().info("fixProtocollazione END id=" + this.idComunicazione);
		
		return this.getTarget();
	}
	
}
