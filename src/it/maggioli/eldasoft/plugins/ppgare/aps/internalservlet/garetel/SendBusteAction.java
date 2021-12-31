package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoAllegatoBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.SaveWizardIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.security.PGPEncryptionUtils;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;
import com.opensymphony.xwork2.inject.Inject;

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

	private Map<String, Object> session;

	private INtpManager ntpManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;
	private IMailManager mailManager;
	private IWSDMManager wsdmManager;
	private IEventManager eventManager;

	private Integer tipoProtocollazione;
	private String mailUfficioProtocollo;

	private Boolean allegaDocMailUfficioProtocollo;
	private String numeroProtocollo;
	private Long annoProtocollo;
	private String stazioneAppaltanteProtocollante;
	private String dataProtocollo;

	private String codice;
	private int operazione;
	private String progressivoOfferta;
	private Date dataInvio;

	private InputStream inputStream;
	private String msgErrore;
	private String multipartSaveDir;
	private String username;
	
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
	 * ... 
	 */
	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	/**
	 * Ritorna la directory per i file temporanei, prendendola da
	 * struts.multipart.saveDir (struts.properties) se valorizzata
	 * correttamente, altrimenti da javax.servlet.context.tempdir
	 * 
	 * @return path alla directory per i file temporanei
	 */
	private File getTempDir() {
		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), this.multipartSaveDir);
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
	public String invio() {
		this.setTarget("reopen");

		this.tipoProtocollazione = (Integer) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_TIPO);
		if (this.tipoProtocollazione == null) {
			this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		}
		this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
		this.allegaDocMailUfficioProtocollo = (Boolean) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
		this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);
		String stazioneAppaltanteProcedura = null;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			this.username = this.getCurrentUser().getUsername();

			WizardDatiImpresaHelper datiImpresa = null;
			DettaglioGaraType dettGara = null;
			String nomeOperazione = "";
			boolean controlliOk = true;
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			ComunicazioneType comunicazioneBustaAmministrativa = null;
			ComunicazioneType comunicazioneBustaTecnica = null;
			ComunicazioneType comunicazioneBustaEconomica = null;
			ComunicazioneType comunicazioneBustaPrequalifica = null;
			ComunicazioneType comunicazione = null;

			boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);

			String tipoComunicazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
					? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
					: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;

			// FASE 1: invio delle comunicazioni
			try {
				dettGara = this.bandiManager.getDettaglioGara(this.codice);
				stazioneAppaltanteProcedura = dettGara.getStazioneAppaltante().getCodice();
				this.appParamManager.setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
				
				if(!this.appParamManager.isConfigStazioneAppaltantePresente()) {
					// se la configurazione WSDM della stazione appaltante 
					// non e' presente, resetta il tipo protocollazione
					this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				}
			
				if (!SaveWizardIscrizioneAction.isProtocollazionePrevista(
						this.tipoProtocollazione, 
						this.stazioneAppaltanteProtocollante, 
						stazioneAppaltanteProcedura)) 
				{
					// se la protocollazione non e' prevista, si resetta il tipo protocollazione cosi' in seguito il codice testa su un'unica condizione
					this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
				}
				nomeOperazione = WizardDocumentiBustaHelper.retrieveNomeOperazione(this.operazione);
				this.dataInvio = retrieveDataInvio(this.ntpManager, this, nomeOperazione);

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
					
					datiImpresa = (WizardDatiImpresaHelper) this.session
							.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
					
					if (controlliOk) {
						if(domandaPartecipazione) {
							// verifico che ci siano i documenti richiesti per la
							// busta di prequalifica
							DettaglioComunicazioneType dettComunicazioneBustaPreq = ComunicazioniUtilities
									.retrieveComunicazione(
											this.comunicazioniManager,
											this.getCurrentUser().getUsername(),
											this.codice,
											this.progressivoOfferta,
											CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA);

							if (dettComunicazioneBustaPreq != null) {
								byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA);
								comunicazioneBustaPrequalifica = this.comunicazioniManager
										.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
														  dettComunicazioneBustaPreq.getId());

								controlliOk = controlliOk
										&& aggiornaComunicazioneBusta(
												comunicazioneBustaPrequalifica,
												dettComunicazioneBustaPreq,
												PortGareSystemConstants.BUSTA_PRE,
												chiavePubblica);
							}
						} else {
							// verifico che ci siano i documenti richiesti per la
							// busta amministrativa
							DettaglioComunicazioneType dettComunicazioneBustaAmm = ComunicazioniUtilities
									.retrieveComunicazione(
											this.comunicazioniManager,
											this.getCurrentUser().getUsername(),
											this.codice,
											this.progressivoOfferta,
											CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA);

							if (dettComunicazioneBustaAmm != null) {
								byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA);
								comunicazioneBustaAmministrativa = this.comunicazioniManager
										.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
														  dettComunicazioneBustaAmm.getId());

								controlliOk = controlliOk
										&& aggiornaComunicazioneBusta(
												comunicazioneBustaAmministrativa,
												dettComunicazioneBustaAmm,
												PortGareSystemConstants.BUSTA_AMM,
												chiavePubblica);
							}

							if (this.bandiManager.isGaraConOffertaTecnica(this.codice)) {
								// verifico che ci siano i documenti richiesti per
								// la busta tecnica
								DettaglioComunicazioneType dettComunicazioneBustaTec = ComunicazioniUtilities
										.retrieveComunicazione(
												this.comunicazioniManager,
												this.getCurrentUser().getUsername(),
												this.codice,
												this.progressivoOfferta,
												CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
												PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);

								if (dettComunicazioneBustaTec != null) {
									byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);

									comunicazioneBustaTecnica = this.comunicazioniManager
											.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
															  dettComunicazioneBustaTec.getId());

									controlliOk = controlliOk
											&& aggiornaComunicazioneBusta(
													comunicazioneBustaTecnica,
													dettComunicazioneBustaTec,
													PortGareSystemConstants.BUSTA_TEC,
													chiavePubblica);
								}
							}

							// verifico che ci siano i documenti richiesti per la
							// busta economica
							DettaglioComunicazioneType dettComunicazioneBustaEco = ComunicazioniUtilities
									.retrieveComunicazione(
											this.comunicazioniManager,
											this.getCurrentUser().getUsername(),
											this.codice,
											this.progressivoOfferta,
											CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);

							if (dettComunicazioneBustaEco != null) {
								byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);

								comunicazioneBustaEconomica = this.comunicazioniManager
										.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
														  dettComunicazioneBustaEco.getId());
								if (dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
									controlliOk = controlliOk
											&& aggiornaComunicazioneBustaEconomica(
													comunicazioneBustaEconomica,
													dettComunicazioneBustaEco,
													chiavePubblica);
								} else {
									controlliOk = controlliOk
											&& aggiornaComunicazioneBusta(
													comunicazioneBustaEconomica,
													dettComunicazioneBustaEco,
													PortGareSystemConstants.BUSTA_ECO,
													chiavePubblica);
								}
							}
						}  //if(domandaPartecipazione)
					} //if (controlliOk)


					if (controlliOk) {
						Event evento = new Event();

						if (comunicazioneBustaPrequalifica != null) {
							evento = ComunicazioniUtilities.createEventAggiornaStatoComunicazione(
									this.username, 
									this.codice,
									comunicazioneBustaPrequalifica,
									this.getCurrentUser().getIpAddress(),
									this.getRequest().getSession().getId());
							try {
								if(comunicazioneBustaPrequalifica.getDettaglioComunicazione().getSessionKey() != null) {
									this.comunicazioniManager.updateSessionKeyComunicazione(
											comunicazioneBustaPrequalifica.getDettaglioComunicazione().getApplicativo(), 
											comunicazioneBustaPrequalifica.getDettaglioComunicazione().getId(), 
											comunicazioneBustaPrequalifica.getDettaglioComunicazione().getSessionKey(), 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								} else {
									this.comunicazioniManager.updateStatoComunicazioni(
											new DettaglioComunicazioneType[] {comunicazioneBustaPrequalifica.getDettaglioComunicazione()}, 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
								this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);
							} catch (Throwable e) {
								evento.setError(e);
								throw e;
							} finally {
								evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
								this.eventManager.insertEvent(evento);
							}
						}

						if (comunicazioneBustaAmministrativa != null) {
							evento = ComunicazioniUtilities.createEventAggiornaStatoComunicazione(
									this.username, 
									this.codice,
									comunicazioneBustaAmministrativa,
									this.getCurrentUser().getIpAddress(),
									this.getRequest().getSession().getId());
							try {
								if(comunicazioneBustaAmministrativa.getDettaglioComunicazione().getSessionKey() != null) {
									this.comunicazioniManager.updateSessionKeyComunicazione(
											comunicazioneBustaAmministrativa.getDettaglioComunicazione().getApplicativo(), 
											comunicazioneBustaAmministrativa.getDettaglioComunicazione().getId(), 
											comunicazioneBustaAmministrativa.getDettaglioComunicazione().getSessionKey(), 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								} else {
									this.comunicazioniManager.updateStatoComunicazioni(
											new DettaglioComunicazioneType[] {comunicazioneBustaAmministrativa.getDettaglioComunicazione()}, 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
								this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
							} catch (Throwable e) {
								evento.setError(e);
								throw e;
							} finally {
								evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
								this.eventManager.insertEvent(evento);
							}
						}

						if (comunicazioneBustaTecnica != null) {
							evento = ComunicazioniUtilities.createEventAggiornaStatoComunicazione(
									this.username, 
									this.codice,
									comunicazioneBustaTecnica,
									this.getCurrentUser().getIpAddress(),
									this.getRequest().getSession().getId());
							try {
								if(comunicazioneBustaTecnica.getDettaglioComunicazione().getSessionKey() != null) {
									this.comunicazioniManager.updateSessionKeyComunicazione(
											comunicazioneBustaTecnica.getDettaglioComunicazione().getApplicativo(), 
											comunicazioneBustaTecnica.getDettaglioComunicazione().getId(), 
											comunicazioneBustaTecnica.getDettaglioComunicazione().getSessionKey(), 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								} else {
									this.comunicazioniManager.updateStatoComunicazioni(
											new DettaglioComunicazioneType[] {comunicazioneBustaTecnica.getDettaglioComunicazione()}, 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
								this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
							} catch (Throwable e) {
								evento.setError(e);
								throw e;
							} finally {
								evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
								this.eventManager.insertEvent(evento);
							}
						}

						if (comunicazioneBustaEconomica != null) {
							evento = ComunicazioniUtilities.createEventAggiornaStatoComunicazione(
									this.username, 
									this.codice,
									comunicazioneBustaEconomica,
									this.getCurrentUser().getIpAddress(),
									this.getRequest().getSession().getId());
							try {
								if(comunicazioneBustaEconomica.getDettaglioComunicazione().getSessionKey() != null) {
									this.comunicazioniManager.updateSessionKeyComunicazione(
											comunicazioneBustaEconomica.getDettaglioComunicazione().getApplicativo(), 
											comunicazioneBustaEconomica.getDettaglioComunicazione().getId(), 
											comunicazioneBustaEconomica.getDettaglioComunicazione().getSessionKey(), 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								} else {
									this.comunicazioniManager.updateStatoComunicazioni(
											new DettaglioComunicazioneType[] {comunicazioneBustaEconomica.getDettaglioComunicazione()}, 
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
								this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
							} catch (Throwable e) {
								evento.setError(e);
								throw e;
							} finally {
								evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
								this.eventManager.insertEvent(evento);
							}
						}

						DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities
								.retrieveComunicazione(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice,
										this.progressivoOfferta,
										CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
										tipoComunicazione);

						if (dettComunicazione == null) {
							dettComunicazione = ComunicazioniUtilities
									.retrieveComunicazione(
											this.comunicazioniManager,
											this.getCurrentUser().getUsername(),
											this.codice,
											this.progressivoOfferta,
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
											tipoComunicazione);
						}

						if (dettComunicazione != null) {
							dettComunicazione.setDataPubblicazione(this.dataInvio);
							comunicazione = this.comunicazioniManager
									.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
													  dettComunicazione.getId());
							aggiornaComunicazionePartecipazione(
									comunicazione,
									dettComunicazione);
							// l'aggiornamento stato avviene in questa posizione
							// altrimenti con aggiornaComunicazionePartecipazione 
							// sovrascrivo lo stato con quello della comunicazione
							boolean daProtocollare = (this.getTipoProtocollazione().intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM
													  || (this.getTipoProtocollazione().intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL));						
							dettComunicazione.setStato( daProtocollare
									? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE
									: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE );
							
							evento = ComunicazioniUtilities.createEventSendComunicazione(
									this.username, 
									this.codice,
									comunicazione,
									this.dataInvio,
									this.getCurrentUser().getIpAddress(),
									this.getRequest().getSession().getId());
							try {
								this.comunicazioniManager.sendComunicazione(comunicazione);
								inviataComunicazione = true;
							} catch (Throwable e) {
								evento.setError(e);
								throw e;
							} finally {
								this.eventManager.insertEvent(evento);
							}
						}
					}

					// se qualcosa va in errore imposta il target a CommonSystemConstants.PORTAL_ERROR
					// senza unn messaggio di errore viene restituita ad una pagina vuota...
					if(!controlliOk && !invioScaduto) {
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
					long id = comunicazione.getDettaglioComunicazione().getId();
					try {
						RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper)this.getSession()
							.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);

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
								fascicoloBackOffice);

						WSDMProtocolloDocumentoType ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						this.annoProtocollo = ris.getAnnoProtocollo();
						this.numeroProtocollo = ris.getNumeroProtocollo();
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
									+ " ed inserire inoltre un documento in ingresso per entità " + documento.getEntita()
									+ ", chiave1 " + documento.getChiave1()
									+ ", oggetto " + documento.getOggetto()
									+ ", numero documento " + documento.getNumeroDocumento()
									+ ", anno protocollo " + documento.getAnnoProtocollo()
									+ " e numero protocollo " + documento.getNumeroProtocollo());
							evento.resetDetailMessage();
							this.eventManager.insertEvent(evento);
						} else {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, "invio",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							ExceptionUtils.manageExceptionError(t, this);
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
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				// rimosso in modo tale che alla riapertura dopo annullamento integra/rettifica 
				// non si trovino informazioni già valorizzate (es. importi prezzi unitari)
				this.session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA); 	
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
	 * Crea e popola il contenitore per richiedere la protocollazione mediante
	 * WSDM.
	 * 
	 * @param datiImpresaHelper
	 *            dati dell'impresa
	 * @param dettGara dati della gara
	 * @param bustaRiepilogativa
	 *            busta riepilogativa
	 * @param comunicazionePartecipazione
	 *            richiesta di partecipazione
	 * @return contenitore popolato
	 * @throws IOException
	 * @throws ApsException
	 * @throws XmlException 
	 * @throws DocumentException 
	 */
	protected WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardDatiImpresaHelper datiImpresaHelper,
			DettaglioGaraType dettGara,
			RiepilogoBusteHelper bustaRiepilogativa,
			ComunicazioneType comunicazionePartecipazione,
			String nomeOperazione,
			FascicoloProtocolloType fascicoloBackOffice) throws IOException, ApsException, XmlException, DocumentException 
	{
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
		String acronimoRup = ""; 
		if(rup != null) {
			String[] s = rup.toUpperCase().split(" ");
			for(int i = 0; i < s.length; i++) acronimoRup += s[i].substring(0, 1);  
		}

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
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA è vuoto utilizza WSFASCICOLO.struttura)
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
				// allora si invia la property nel campo già valorizzato 
				// e non si valorizza più la data 
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
		    }
			wsdmProtocolloDocumentoIn.setFascicolo(fascicolo);
		} else {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
		}

		// Allegati: un file per ogni busta, contenente gli SHA1 di ogni file
		// caricato nella busta
		byte[] bustaPreQualifica = getDigestFileBusta(bustaRiepilogativa.getBustaPrequalifica());
		byte[] bustaAmministrativa = getDigestFileBusta(bustaRiepilogativa.getBustaAmministrativa());
		byte[] bustaTecnica = getDigestFileBusta(bustaRiepilogativa.getBustaTecnica());
		byte[] bustaEconomica = getDigestFileBusta(bustaRiepilogativa.getBustaEconomica());

		int numBusteValorizzate = 
			(bustaPreQualifica != null ? 1 : 0) + 
			(bustaAmministrativa != null ? 1 : 0) +
			(bustaTecnica != null ? 1 : 0) +
			(bustaEconomica != null ? 1 : 0);

		// prepara 1+N allegati...
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// ...verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		boolean inTesta = false;		// default, inserisci in coda
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			Integer v = (Integer) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
			if(v != null && v == 1) {
				inTesta = true;
			}
		}
		
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[numBusteValorizzate + 1];

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
					(String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"),
					indirizzo,
					UtilityDate.convertiData(this.getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
					(this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
							? this.getI18nLabelFromDefaultLocale("LABEL_OFFERTA") 
							: this.getI18nLabelFromDefaultLocale("LABEL_PARTECIPAZIONE")),
					dettGara.getDatiGeneraliGara().getOggetto() });
		byte[] contenutoPdf = UtilityStringhe.string2Pdf(contenuto);
		allegati[n2].setContenuto(contenutoPdf);
		
		// serve per Titulus
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId() + "/" + n2);

		// busta di prequalifica
		int i = 0;
		if(inTesta) {
			i = 1;
		}
		if (bustaPreQualifica != null) {
			allegati[i] = new WSDMProtocolloAllegatoType();
			allegati[i].setTitolo("Busta di prequalifica");
			allegati[i].setTipo("txt");
			allegati[i].setNome("busta_prequalifica.sha1.txt");
			allegati[i].setContenuto(bustaPreQualifica);
			// serve per Titulus
			allegati[i].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazionePartecipazione.getDettaglioComunicazione().getId()
					+ "/" + i);
			i++;
		}
		
		// busta di amministrativa 
		if (bustaAmministrativa != null) {
			allegati[i] = new WSDMProtocolloAllegatoType();
			allegati[i].setTitolo("Busta amministrativa");
			allegati[i].setTipo("txt");
			allegati[i].setNome("busta_amministrativa.sha1.txt");
			allegati[i].setContenuto(bustaAmministrativa);
			// serve per Titulus
			allegati[i].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazionePartecipazione.getDettaglioComunicazione().getId()
					+ "/" + i);
			i++;
		}
		
		// busta di tecnica
		if (bustaTecnica != null) {
			allegati[i] = new WSDMProtocolloAllegatoType();
			allegati[i].setTitolo("Busta tecnica");
			allegati[i].setTipo("txt");
			allegati[i].setNome("busta_tecnica.sha1.txt");
			allegati[i].setContenuto(bustaTecnica);
			// serve per Titulus
			allegati[i].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazionePartecipazione.getDettaglioComunicazione().getId()
					+ "/" + i);
			i++;
		}
		
		// busta di economica
		if (bustaEconomica != null) {
			allegati[i] = new WSDMProtocolloAllegatoType();
			allegati[i].setTitolo("Busta economica");
			allegati[i].setTipo("txt");
			allegati[i].setNome("busta_economica.sha1.txt");
			allegati[i].setContenuto(bustaEconomica);
			// serve per Titulus
			allegati[i].setIdAllegato("W_INVCOM/"
					+ CommonSystemConstants.ID_APPLICATIVO + "/"
					+ comunicazionePartecipazione.getDettaglioComunicazione().getId()
					+ "/" + i);
		}
		
		wsdmProtocolloDocumentoIn.setAllegati(allegati);
				
		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)){
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
					"In fase di annullamento delle modifiche apportate si è verificato un errore, " +
					"si consiglia una rimozione manuale delle comunicazioni");
			ExceptionUtils.manageExceptionError(e, this);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * conferma ed invia  
	 */
	public String confirmInvio() {
		this.setTarget("reopen");

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				
				String nomeOperazione = WizardDocumentiBustaHelper.retrieveNomeOperazione(this.operazione);
				
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
					
					WizardDatiImpresaHelper datiImpresa = (WizardDatiImpresaHelper) this.session
							.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);

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

					if (controlliOk) {	
						if(domandaPartecipazione) {
							WizardDocumentiBustaHelper wizardBustaPreq = WizardDocumentiBustaHelper
									.getFromSession(this.session, PortGareSystemConstants.BUSTA_PRE_QUALIFICA);
							if (wizardBustaPreq != null && wizardBustaPreq.isDatiModificati()) {
								controlliOk = false;
								this.addActionError(this.getText("Errors.invioBuste.changesNotSent", 
																 new String[] { PortGareSystemConstants.BUSTA_PRE }));
							}
						} 

						if(invioOfferta) {
							if(!reinvioProtocollazioneMail){
								WizardDocumentiBustaHelper wizardBustaAmm = WizardDocumentiBustaHelper
									.getFromSession(this.session, PortGareSystemConstants.BUSTA_AMMINISTRATIVA);
								if (wizardBustaAmm != null && wizardBustaAmm.isDatiModificati()) {
									controlliOk = false;
									this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
																     new String[] { PortGareSystemConstants.BUSTA_AMM }));
								}
								WizardDocumentiBustaHelper wizardBustaTec = WizardDocumentiBustaHelper
										.getFromSession(this.session, PortGareSystemConstants.BUSTA_TECNICA);
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
									WizardOffertaEconomicaHelper wizardBustaEco = InitOffertaEconomicaAction
											.getInstance(session, 
														 this.getCurrentUser().getUsername(), 
														 this.codice, 
														 null,
														 this.operazione,
														 bandiManager, 
														 comunicazioniManager,
														 this.getTempDir());
									if (wizardBustaEco != null && wizardBustaEco.isDatiModificati()) {
										controlliOk = false;
										this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
																		 new String[] { PortGareSystemConstants.BUSTA_ECO }));
									}
								} else {
									WizardDocumentiBustaHelper wizardBustaEco = WizardDocumentiBustaHelper
											.getFromSession(this.session, PortGareSystemConstants.BUSTA_ECONOMICA);
									if (wizardBustaEco != null && wizardBustaEco.isDatiModificati()) {
										controlliOk = false;
										this.addActionError(this.getText("Errors.invioBuste.changesNotSent",
																		 new String[] { PortGareSystemConstants.BUSTA_ECO }));
									}
								}
							}
						} //if(invioOfferta)
					}

					RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper)this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
					WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)this.getSession()
						.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

					// tramite servizio controllo che non siano stati inseriti 
					// nuovi documenti obbligatori comuni per le buste tecniche via BO 
					if(domandaPartecipazione) {
						if(bustaRiepilogativa != null){
							// --- ricalcolo gli eventuali documenti mancanti --- 
							bustaRiepilogativa.integraBustaPrequalificaFromBO(
									this.codice, 
									this.codice, 
									this.getBandiManager(), 
									datiImpresa, 
									partecipazioneHelper);
						}
					} 

					if(invioOfferta) {
						// --- ricalcolo gli eventuali documenti mancanti --- 
						bustaRiepilogativa.integraBustaAmministrativaFromBO(
								this.codice, 
								this.codice, 
								this.getBandiManager(), 
								datiImpresa, 
								partecipazioneHelper);

						// --- per ogni lotto controllo se esiste la busta tecnica ---
						RiepilogoBustaBean bustaTecnica = bustaRiepilogativa.getBustaTecnica();						
						if(bustaTecnica != null) {
							bustaRiepilogativa.integraBustaTecnicaFromBO(
									bustaTecnica, 
									this.bandiManager, 
									this.codice, 
									this.codice, 
									datiImpresa, 
									partecipazioneHelper);
						}

						RiepilogoBustaBean bustaEconomica = bustaRiepilogativa.getBustaEconomica();
						if(bustaEconomica != null) {
							bustaRiepilogativa.integraBustaEconomicaFromBO(
									bustaEconomica, 
									this.bandiManager, 
									this.codice, 
									this.codice, 
									datiImpresa, 
									partecipazioneHelper);
						}
					}

					if (controlliOk) {	
						if(domandaPartecipazione) {
							controlliOk = checkBusta(
									datiImpresa,
									dettGara,
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA,
									PortGareSystemConstants.BUSTA_PRE,
									PortGareSystemConstants.BUSTA_PRE_QUALIFICA);							
						}  

						if(invioOfferta) {
							// busta amministrativa
							controlliOk = checkBusta(
									datiImpresa,
									dettGara,
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
									PortGareSystemConstants.BUSTA_AMM,
									PortGareSystemConstants.BUSTA_AMMINISTRATIVA);

							// busta tecnica
							if (this.bandiManager.isGaraConOffertaTecnica(this.codice)) {
								if (apriOEPVBustaTecnica) {
									controlliOk = controlliOk && checkBustaTecnica(
											dettGara,
											datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(),
											PortGareSystemConstants.BUSTA_TEC);
								} else {
									controlliOk = controlliOk && checkBusta(
											datiImpresa,
											dettGara,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
											PortGareSystemConstants.BUSTA_TEC,
											PortGareSystemConstants.BUSTA_TECNICA);
								}
							}

							// busta economica
							// in caso di OEPV a costo fisso non va controllata
							// la parte economica...
							if(!costoFisso) {
								if (dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
									controlliOk = controlliOk && checkBustaEconomica(
											dettGara,
											datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(),
											PortGareSystemConstants.BUSTA_ECO);
								} else {
									controlliOk = controlliOk && checkBusta(
											datiImpresa,
											dettGara,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
											PortGareSystemConstants.BUSTA_ECO,
											PortGareSystemConstants.BUSTA_ECONOMICA);
								}
							}
						}
					}
					
					if(!controlliOk) {
						String documentiMancanti = this.getListaDocumentiMancanti(bustaRiepilogativa, domandaPartecipazione);
						if(StringUtils.isNotEmpty(documentiMancanti)) {
							evento.setDetailMessage(documentiMancanti);
							evento.setLevel(Event.Level.ERROR);
						}
					}
					
					// per gare ristrette in fase di domanda di partecipazione
					// controlla se i lotti dell'offerta sono utilizzati anche in altre offerte...
					if(controlliOk && domandaPartecipazione) {
						boolean lottiInAltreOfferte = this.isLottiPresentiInAltreOfferte(
								partecipazioneHelper, 
								dettGara); 
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
					VerificaDocumentiCorrotti documentiCorrotti = new VerificaDocumentiCorrotti(bustaRiepilogativa, this.bandiManager); 
					if (controlliOk) {
						if(domandaPartecipazione) {
							controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
									this.codice, 
									this.getCurrentUser().getUsername(),
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA,
									bustaRiepilogativa.getBustaPrequalifica());
						} else {
							controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
									this.codice, 
									this.getCurrentUser().getUsername(),
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
									bustaRiepilogativa.getBustaAmministrativa());
							
							controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
									this.codice, 
									this.getCurrentUser().getUsername(),
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
									bustaRiepilogativa.getBustaTecnica());
							
							controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
									this.codice, 
									this.getCurrentUser().getUsername(),
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
									bustaRiepilogativa.getBustaEconomica());
						}
						
						if(documentiCorrotti.size() > 0) {
							// visualizza la lista dei documenti nulli o corrotti
							documentiCorrotti.addActionErrors(this);
							evento.setDetailMessage(documentiCorrotti.getEventDetailMessage());
							evento.setLevel(Event.Level.ERROR);
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
	 * imposta gli allegati della comunicazione e restituisce l'elenco degli 
	 * allegati della comunicazione
	 * 
	 * @param comunicazione
	 * 			la comunicazione alla quale impostare la lista degli allegati
	 * @param sender
	 * 			Helper o XmlObject da cui estrarre le informazioni sugli allegati 
	 * @param nomeFile
	 * 			nomefile della documento xml (vedere PortGareSystemConstants. ...)
	 * @param descrizioneFile
	 * 			descrizione del documento xml
	 * @return
	 * 			un elenco di AllegatoComunicazioneType
	 */
	public static AllegatoComunicazioneType[] setAllegatoComunicazione(
			ComunicazioneType comunicazione,
			Object sender,	
			String nomeFile, 
			String descrizioneFile) throws ApsException  
	{
		AllegatoComunicazioneType[] allegati = null;
		
		// crea la lista degli allegati della comunicazione gli allegati 
		// della comunicazione...
		HashMap<String, String> suggestedPrefixes;
		ByteArrayOutputStream baos;
		XmlOptions opts;
		XmlObject xmlDocument;
		try {
			if(sender instanceof TipoPartecipazioneDocument) {
				//
				// TipoPartecipazioneDocument
				//
				xmlDocument = (TipoPartecipazioneDocument)sender;
				baos = new ByteArrayOutputStream();
				xmlDocument.save(baos);
				
				AllegatoComunicazioneType allegato = ComunicazioniUtilities
					.createAllegatoComunicazione(
							nomeFile, 
							descrizioneFile, 
							baos.toString("UTF-8").getBytes());
				
				allegati = new AllegatoComunicazioneType[] { allegato };
				
			} else if(sender instanceof RiepilogoBusteHelper) {
				//
				// RiepilogoBusteHelper
				//
				RiepilogoBusteHelper h = (RiepilogoBusteHelper)sender;
			    
				suggestedPrefixes = new HashMap<String, String>();
				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");			
				opts = new XmlOptions();
				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
				
				xmlDocument = h.getXmlDocument();
				baos = new ByteArrayOutputStream();
				xmlDocument.save(baos, opts);
				
				AllegatoComunicazioneType allegato = ComunicazioniUtilities
					.createAllegatoComunicazione(
							nomeFile, 
							descrizioneFile, 
							baos.toString("UTF-8").getBytes());
				
				allegati = new AllegatoComunicazioneType[] { allegato };
				
			} else if(sender instanceof WizardDocumentiBustaHelper) {
				//
				// WizardDocumentiBustaHelper
				//
				WizardDocumentiBustaHelper h = (WizardDocumentiBustaHelper)sender;			
				
				suggestedPrefixes = new HashMap<String, String>();
				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
				opts = new XmlOptions();
				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
				baos = new ByteArrayOutputStream();
				
				// inserisci il documento xml come I allegato della comunicazione 
				// e tutti i documenti dell'xml come allegati della comunicazione
				List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();
	
				AllegatoComunicazioneType allegato = ComunicazioniUtilities
					.createAllegatoComunicazione(
							nomeFile, 
							descrizioneFile, 
							null);
				allegatiXml.add(allegato);
				
				h.documentiToAllegatiComunicazione(allegatiXml);
				
			    // qui l'helper e' aggiornato e si puo' generare l'xml del documento 
			    xmlDocument = h.getXmlDocument();
			    xmlDocument.save(baos, opts);
			    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
			    
				allegati = allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]);

			} else if(sender instanceof WizardOffertaHelper) {
				//
				// WizardOffertaEconomicaHelper, WizardOffertaTecnicaHelper 
				// ereditano WizardOffertaHelper
				//
				WizardOffertaHelper h = (WizardOffertaHelper)sender;
				
				suggestedPrefixes = new HashMap<String, String>();
				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
				opts = new XmlOptions();
				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
				baos = new ByteArrayOutputStream();
				
				// inserisci il documento xml come I allegato della comunicazione 
				// e tutti i documenti dell'xml come allegati della comunicazione
				List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();
	
				AllegatoComunicazioneType allegato = ComunicazioniUtilities
					.createAllegatoComunicazione(
							nomeFile, 
							descrizioneFile, 
							null);
				allegatiXml.add(allegato);
				
				h.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
				
			    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
				xmlDocument = null;
				if(sender instanceof WizardOffertaEconomicaHelper) { 
					xmlDocument = h.getXmlDocument(BustaEconomicaDocument.Factory.newInstance(), true, false);
				} else if(sender instanceof WizardOffertaTecnicaHelper) {
					xmlDocument = h.getXmlDocument(BustaTecnicaDocument.Factory.newInstance(), true, false);
				}
				xmlDocument.save(baos, opts);
				allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
				    
				allegati = allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]);
				
//			} else if(sender instanceof WizardOffertaEconomicaHelper) {
//				//
//				// WizardOffertaEconomicaHelper
//				//
//				WizardOffertaEconomicaHelper h = (WizardOffertaEconomicaHelper)sender;
//				
//				suggestedPrefixes = new HashMap<String, String>();
//				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
//				opts = new XmlOptions();
//				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
//				baos = new ByteArrayOutputStream();
//				
//				// inserisci il documento xml come I allegato della comunicazione 
//				// e tutti i documenti dell'xml come allegati della comunicazione
//				List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();
//	
//				AllegatoComunicazioneType allegato = ComunicazioniUtilities
//					.createAllegatoComunicazione(
//							nomeFile, 
//							descrizioneFile, 
//							null);
//				allegatiXml.add(allegato);
//				
//				h.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
//				
//			    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
//			    xmlDocument = h.getXmlDocument(BustaEconomicaDocument.Factory.newInstance(), true, false);
//			    xmlDocument.save(baos, opts);
//			    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
//			    
//			    allegati = allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]);
//
//			} else if(sender instanceof WizardOffertaTecnicaHelper) {
//				//
//				// WizardOffertaTecnicaHelper
//				//
//				WizardOffertaTecnicaHelper h = (WizardOffertaTecnicaHelper)sender;
//				
//				suggestedPrefixes = new HashMap<String, String>();
//				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
//				opts = new XmlOptions();
//				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
//				baos = new ByteArrayOutputStream();
//				
//				// inserisci il documento xml come I allegato della comunicazione 
//				// e tutti i documenti dell'xml come allegati della comunicazione
//				List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();
//	
//				AllegatoComunicazioneType allegato = ComunicazioniUtilities
//					.createAllegatoComunicazione(
//							nomeFile, 
//							descrizioneFile, 
//							null);
//				allegatiXml.add(allegato);
//				
//				h.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
//				
//			    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
//			    xmlDocument = h.getXmlDocument(BustaTecnicaDocument.Factory.newInstance(), true, false);
//			    xmlDocument.save(baos, opts);
//			    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
//			    
//			    allegati = allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]);

			} else if(sender instanceof WizardOffertaAstaHelper) {
				//
				// WizardOffertaAstaHelper
				//
				WizardOffertaAstaHelper h = (WizardOffertaAstaHelper)sender;
				
				suggestedPrefixes = new HashMap<String, String>();
				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
				opts = new XmlOptions();
				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
				baos = new ByteArrayOutputStream();
				
				// inserisci il documento xml come I allegato della comunicazione 
				// e tutti i documenti dell'xml come allegati della comunicazione
				List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();
	
				AllegatoComunicazioneType allegato = ComunicazioniUtilities
					.createAllegatoComunicazione(
							nomeFile, 
							descrizioneFile, 
							null);
				allegatiXml.add(allegato);
				
				h.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
				
			    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
			    xmlDocument = h.getXmlDocument(BustaEconomicaDocument.Factory.newInstance(), true, false);
			    xmlDocument.save(baos, opts);
			    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
			    
			    allegati = allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]);

			} else {
				throw new ApsException("La classe del parametro 'sender' non gestita (" + sender.getClass().toString() + ")");
			}
		} catch (Throwable ex) {
			throw new ApsException(ex.getMessage(), ex); 
		}

		// se esiste la comunicazione imposta l'elenco degli allegati
		if( comunicazione != null ) {
			comunicazione.setAllegato(allegati);
		}
		
		return allegati;
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
	private void setDataPresentazioneDocumentoPartecipazione(TipoPartecipazioneDocument document) {
		GregorianCalendar dataUfficiale = new GregorianCalendar();
		if (this.dataInvio != null) {
			dataUfficiale.setTime(this.dataInvio);
		}
		document.getTipoPartecipazione().setDataPresentazione(dataUfficiale);
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
			bustaDocument = WizardDocumentiBustaHelper.getDocumentoBusta(comunicazioneBusta);
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
			bustaDocument = InitOffertaEconomicaAction.getBustaEconomicaDocument(comunicazioneBusta);
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
			bustaDocument = InitOffertaTecnicaAction.getBustaTecnicaDocument(comunicazioneBusta);
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
	 * ... 
	 */
	protected boolean aggiornaComunicazioneBusta(
			ComunicazioneType comunicazioneBusta,
			DettaglioComunicazioneType dettComunicazioneBusta, 
			String nomeBusta, 
			byte[] chiavePubblica) throws XmlException, IOException 
	{
		boolean aggiornata = true;
		try {
			dettComunicazioneBusta.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			if (chiavePubblica != null) {
				// viene prevista la cifratura, pertanto si ricava la chiave di
				// sessione e la si cifra in modo irreversibile con la chiave
				// pubblica
				byte[] sessionKey = EncryptionUtils.decodeSessionKey(
						dettComunicazioneBusta.getSessionKey(), 
						this.getCurrentUser().getUsername());
				dettComunicazioneBusta.setSessionKey(Base64.encodeBase64String(
						PGPEncryptionUtils.encrypt(new ByteArrayInputStream(chiavePubblica), sessionKey)));
			}
			comunicazioneBusta.setDettaglioComunicazione(dettComunicazioneBusta);

		} catch (Throwable ex) {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			aggiornata = false;
		}
		return aggiornata;
	}

	/**
	 * ... 
	 */
	private boolean aggiornaComunicazioneBustaEconomica(
			ComunicazioneType comunicazioneBusta,
			DettaglioComunicazioneType dettComunicazioneBusta, 
			byte[] chiavePubblica) throws XmlException, IOException 
	{
		boolean aggiornata = true;
		try {
			dettComunicazioneBusta.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			if (chiavePubblica != null) {
				// viene prevista la cifratura, pertanto si ricava la chiave di
				// sessione e la si cifra in modo irreversibile con la chiave
				// pubblica
				byte[] sessionKey = EncryptionUtils.decodeSessionKey(
						dettComunicazioneBusta.getSessionKey(), this.getCurrentUser().getUsername());
				dettComunicazioneBusta.setSessionKey(Base64.encodeBase64String(
						PGPEncryptionUtils.encrypt(new ByteArrayInputStream(chiavePubblica), sessionKey)));
			}
			comunicazioneBusta.setDettaglioComunicazione(dettComunicazioneBusta);
			
		} catch (Throwable ex) {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			aggiornata = false;
		}
		return aggiornata;
	}

	/**
	 * ... 
	 */
	protected boolean aggiornaComunicazionePartecipazione(
			ComunicazioneType comunicazionePartecipazione,
			DettaglioComunicazioneType dettComunicazionePartecipazione)
			throws XmlException, IOException 
	{
		boolean aggiornata = true;
		try {
			TipoPartecipazioneDocument document = WizardDocumentiBustaHelper
					.getDocumentoPartecipazione(comunicazionePartecipazione);
			setDataPresentazioneDocumentoPartecipazione(document);
			
			SendBusteAction.setAllegatoComunicazione(
					comunicazionePartecipazione,
					document,
					PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE,
					this.getText("label.tipoPartecipazione.allegato.descrizione"));
			
			dettComunicazionePartecipazione.setDataPubblicazione(this.dataInvio);
			dettComunicazionePartecipazione.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			comunicazionePartecipazione.setDettaglioComunicazione(dettComunicazionePartecipazione);
		} catch (Throwable ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlPartecipazioneNotFound")) {
				this.addActionError(this.getText("Errors.invioBuste.xmlPartecipazioneNotFound"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			aggiornata = false;
		}
		return aggiornata;
	}

	/**
	 * invia la mail della ricevuta  
	 * @throws ApsException 
	 */
	protected void sendMailConfermaImpresa(
			WizardDatiImpresaHelper impresa,
			String bandoGara) throws ApsException 
	{
		String nomeOperazione = WizardDocumentiBustaHelper.retrieveNomeOperazione(this.operazione, this);
		
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
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

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
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

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
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

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
				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.getSession()
					.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);

				// Allegati: un file per ogni busta, contenente gli SHA1 di ogni file
				// caricato nella busta
				//if(bustaRiepilogativa!=null){
				byte[] bustaPreQualifica = getDigestFileBusta(bustaRiepilogativa.getBustaPrequalifica());
				byte[] bustaAmministrativa = getDigestFileBusta(bustaRiepilogativa.getBustaAmministrativa());
				byte[] bustaTecnica = getDigestFileBusta(bustaRiepilogativa.getBustaTecnica());
				byte[] bustaEconomica = getDigestFileBusta(bustaRiepilogativa.getBustaEconomica());

				if(bustaPreQualifica != null)
					p.put("busta_prequalifica.txt", bustaPreQualifica);
				if(bustaAmministrativa != null)
					p.put("busta_amministrativa.txt", bustaAmministrativa);
				if(bustaTecnica != null)
					p.put("busta_tecnica.txt", bustaTecnica);
				if(bustaEconomica != null)
					p.put("busta_economica.txt", bustaEconomica);
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
	 * ... 
	 */
	protected byte[] getDigestFileBusta(RiepilogoBustaBean busta) throws ApsException, XmlException {
		byte[] contenuto = null;
		StringBuilder sb = new StringBuilder();
		if(busta!= null){
			for (DocumentoAllegatoBean allegato : busta.getDocumentiInseriti()) {
				sb.append(allegato.getSha1());
				sb.append(" *");
				sb.append(allegato.getNomeFile());
				sb.append("\n");
			}
			if (sb.length() > 0) {
				// 04/04/2018: si genera il testo solo quando esistono documenti allegati
				contenuto = sb.toString().getBytes();
			}
		}
		return contenuto;
	}

	/**
	 * per le gare a lotti, verifica se i lotti dell'offerta sono utilizzati in altre offerte 
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	protected boolean isLottiPresentiInAltreOfferte(
			WizardPartecipazioneHelper partecipazione,
			DettaglioGaraType gara) 
		throws ApsException, XmlException 
	{		 
		boolean lottiDuplicati = false;
		
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
				if(this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
					ComunicazioneType comunicazione = this.getComunicazioniManager()
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
						  	  	  		  partecipazione.getIdComunicazioneTipoPartecipazione());			
					partecipazione = new WizardPartecipazioneHelper(comunicazione);
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

}

