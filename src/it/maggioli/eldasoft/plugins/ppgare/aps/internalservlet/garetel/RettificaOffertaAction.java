package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoLottoBustaType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;

/**
 * Action di gestione dell'invio buste
 *
 * @author Marco.Perazzetta
 */
public class RettificaOffertaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7547283479159496469L;

	protected INtpManager ntpManager;
	protected IComunicazioniManager comunicazioniManager;
	protected IBandiManager bandiManager;
	protected IMailManager mailManager;
	protected IEventManager eventManager;
	protected IAppParamManager appParamManager;

	protected Map<String, Object> session;
	
	protected String codice;
	protected int operazione;
	protected String username;
	protected String progressivoOfferta;
	protected Date dataInvio;
	protected Date dataOfferta;
	protected String msgErrore;
	protected String fromListaOfferte;		// "1" = true altrimenti false
	
	protected boolean annullamento;
	protected boolean eliminazione;
	protected boolean rettifica;
	protected boolean offerteDistinte;
	
	// attributi ad uso interno 
	protected int progressivoLista;
	protected boolean inviataComunicazione;
	protected boolean richiestafuoriTempo;
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
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

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}
	
	public String getMsgErrore() {
		return msgErrore;
	}

	public void setMsgErrore(String msgErrore) {
		this.msgErrore = msgErrore;
	}

	public Date getDataOfferta() {
		return dataOfferta;
	}

	public void setDataOfferta(Date dataOfferta) {
		this.dataOfferta = dataOfferta;
	}
	
	public String getFromListaOfferte() {
		return fromListaOfferte;
	}

	public void setFromListaOfferte(String fromListaOfferte) {
		this.fromListaOfferte = fromListaOfferte;
	}
	
	public int getProgressivoLista() {
		return progressivoLista;
	}

	public void setProgressivoLista(int progressivoLista) {
		this.progressivoLista = progressivoLista;
	}
	
	public boolean isAnnullamento() {
		return annullamento;
	}

	public void setAnnullamento(boolean annullamento) {
		this.annullamento = annullamento;
	}

	public boolean isEliminazione() {
		return eliminazione;
	}

	public void setEliminazione(boolean eliminazione) {
		this.eliminazione = eliminazione;
	}

	public boolean isRettifica() {
		return rettifica;
	}

	public void setRettifica(boolean rettifica) {
		this.rettifica = rettifica;
	}
	
	public boolean isOfferteDistinte() {
		return offerteDistinte;
	}

	public void setOfferteDistinte(boolean offerteDistinte) {
		this.offerteDistinte = offerteDistinte;
	}

	/**
	 * Esporta la costante per le pagine JSP 
	 * 		PortGareSystemConstants.NOME_EVENTO_PARTECIPA_GARA
	 * 		PortGareSystemConstants.NOME_EVENTO_INVIA_OFFERTA
	 * 		PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA 
	 * 		PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
	 */
	public String getPartecip() {
		return PortGareSystemConstants.NOME_EVENTO_PARTECIPA_GARA;
	}

	public String getInvio() {
		return PortGareSystemConstants.NOME_EVENTO_INVIA_OFFERTA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}
	
	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}

	/**
	 * ... 
	 */
	public String confirmRettifica() {
		this.setTarget("reopen");

		this.offerteDistinte = false;
		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				String nomeOperazione = this.getNomeOperazione(this.operazione);
				this.dataInvio = retrieveDataInvio(nomeOperazione);
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				
				boolean daListaOfferte = ("1".equals(fromListaOfferte));
				
				this.recuperaTipoAnnullamento(dettGara);
				
				this.progressivoLista = this.findProgressivoLista();
				
				String tipoComunicazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA)
						? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT
						: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
				
				String nomeFunzioneEvento = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA)
						? "Rettifica domanda di partecipazione"
						: "Rettifica offerta";

				if (this.dataInvio != null) {

					Date dataTermineInvioOfferta = SendBusteAction.getDataTermine(dettGara, this.operazione);
					boolean controlliOk = true;

					if (dataTermineInvioOfferta != null && this.dataInvio.compareTo(dataTermineInvioOfferta) > 0) {
						controlliOk = false;
						this.addActionError(this.getText("Errors.rettificaRichiestaFuoriTempoMassimo", new String[]{nomeOperazione}));
						
						// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
						Event evento = new Event();
						evento.setUsername(this.getCurrentUser().getUsername());
						evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
						evento.setLevel(Event.Level.ERROR);
						evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
						evento.setIpAddress(this.getCurrentUser().getIpAddress());
						evento.setSessionId(this.getRequest().getSession().getId());
						evento.setMessage("Accesso alla funzione " + nomeFunzioneEvento);
						evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						this.eventManager.insertEvent(evento);
					}

					if (controlliOk) {
						List<String> stati = new ArrayList<String>();
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
						if(daListaOfferte) {
							stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
						}
						
						DettaglioComunicazioneType dettComunicazione = null;

						dettComunicazione = ComunicazioniUtilities.retrieveComunicazioneConStati(
								this.comunicazioniManager,
								this.getCurrentUser().getUsername(),
								this.codice, 
								this.progressivoOfferta,
								tipoComunicazione, 
								stati);

						if (dettComunicazione == null) {
							this.addActionMessage(this.getText("Errors.rettificaOfferta.alreadyDone"));
						} else {
							this.setTarget(SUCCESS);
						}
					}
				}
			} catch (ApsSystemException t) {
				ApsSystemUtils.logThrowable(t, this, "confirmRettifica");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "confirmRettifica");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "confirmRettifica");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
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
	public String rettifica() {
		this.setTarget("reopen");

		boolean inviataComunicazione = false;
		String nomeOperazione = null;
		boolean richiestafuoriTempo = false;
		StringBuilder ids = new StringBuilder(" con id");

		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);			

			this.username = this.getCurrentUser().getUsername();
			
			Event evento = new Event();
			evento.setUsername(this.username);
			evento.setDestination(this.codice);
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
			evento.setMessage("Annullamento comunicazioni");
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());

			// FASE 1: annullamento comunicazioni
			boolean invioMail = true;
			try {
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				nomeOperazione = this.getNomeOperazione(this.operazione);
				this.dataInvio = retrieveDataInvio(nomeOperazione);

				this.recuperaTipoAnnullamento(dettGara);
				
				invioMail = (this.rettifica);
				
				DettaglioComunicazioneType dettComunicazioneBustaAmm = null;
				DettaglioComunicazioneType dettComunicazioneBustaTec = null;
				DettaglioComunicazioneType dettComunicazioneBustaEco = null;
				DettaglioComunicazioneType dettComunicazioneBustaRie = null;
				DettaglioComunicazioneType dettComunicazioneBustaPreq = null;

				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

				String tipoComunicazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
						? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
						: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;

				String RICHIESTA_TIPO_RIEPILOGO = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
						? PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO
						: PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO;
				
				if (this.dataInvio != null) {

					Date dataTermine = SendBusteAction.getDataTermine(dettGara, this.operazione);
					boolean controlliOk = true;

					if (dataTermine != null && this.dataInvio.compareTo(dataTermine) > 0) {
						controlliOk = false;
						richiestafuoriTempo = true;
						this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo", new String[] {nomeOperazione}));
						evento.setLevel(Event.Level.ERROR);
					}

					DettaglioComunicazioneType dettComunicazione = null;

					if (controlliOk) {

						// recupera i dettagli della comunicazione FS11/FS10
						dettComunicazione = ComunicazioniUtilities.retrieveComunicazioneConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice,
										this.progressivoOfferta,
										tipoComunicazione,
										stati);

						if (dettComunicazione == null) {
							this.addActionMessage(this.getText("Errors.rettificaOfferta.alreadyDone"));
						} else {
							if(domandaPartecipazione) {
								// verifico che ci siano i documenti richiesti 
								// per la busta di prequalifica FS10A
								dettComunicazioneBustaPreq = ComunicazioniUtilities.retrieveComunicazioneConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice,
										this.progressivoOfferta,
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA,
										stati);
							} else {
								// verifico che ci siano i documenti richiesti 
								// per la busta amministrativa FS11A
								dettComunicazioneBustaAmm = ComunicazioniUtilities.retrieveComunicazioneConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice,
										this.progressivoOfferta,
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
										stati);
	
								if (this.bandiManager.isGaraConOffertaTecnica(this.codice)) {
									// verifico che ci siano i documenti richiesti 
									// per la busta tecnica FS11B
									dettComunicazioneBustaTec = ComunicazioniUtilities.retrieveComunicazioneConStati(
											this.comunicazioniManager,
											this.getCurrentUser().getUsername(),
											this.codice,
											this.progressivoOfferta,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
											stati);
								}
	
								// verifico che ci siano i documenti richiesti 
								// per la busta economica FS11C
								dettComunicazioneBustaEco = ComunicazioniUtilities.retrieveComunicazioneConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice,
										this.progressivoOfferta,
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
										stati);
							}
							
							// recupera i dettagli della comunicazione di riepilogo FS11R/FS10R 
							dettComunicazioneBustaRie = ComunicazioniUtilities.retrieveComunicazioneConStati(
									this.comunicazioniManager,
									this.getCurrentUser().getUsername(),
									this.codice,
									this.progressivoOfferta,
									RICHIESTA_TIPO_RIEPILOGO,
									stati);
						}
					}

					if (controlliOk) {

						// prepara 2 liste di comunicazioni (da eliminare e da aggiornare)
						List<DettaglioComunicazioneType> comunicazioniDaAggiornare = new ArrayList<DettaglioComunicazioneType>();
						List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

						if (dettComunicazione != null) {
							
							this.setDataOfferta(dettComunicazione.getDataPubblicazione());	
							
							if (dettComunicazioneBustaPreq != null) {
								if (this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA) != null) {
									comunicazioniDaEliminare.add(dettComunicazioneBustaPreq);
								} else {
									comunicazioniDaAggiornare.add(dettComunicazioneBustaPreq);
								}
							}
							if (dettComunicazioneBustaAmm != null) {
								if (this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA) != null) {
									comunicazioniDaEliminare.add(dettComunicazioneBustaAmm);
								} else {
									comunicazioniDaAggiornare.add(dettComunicazioneBustaAmm);
								}
							}
							if (dettComunicazioneBustaTec != null) {
								if (this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA) != null) {
									comunicazioniDaEliminare.add(dettComunicazioneBustaTec);
								} else {
									comunicazioniDaAggiornare.add(dettComunicazioneBustaTec);
								}
							}
							if (dettComunicazioneBustaEco != null) {
								if (this.bandiManager.getChiavePubblica(this.codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA) != null) {
									comunicazioniDaEliminare.add(dettComunicazioneBustaEco);
								} else {
									comunicazioniDaAggiornare.add(dettComunicazioneBustaEco);
								}
							}	

							if(dettComunicazioneBustaRie != null) {
								// se ci sono comunicazioni da eliminare ovvero comunicazioni con cifratura, 
								// allora possono essere: FS11A, FS11B, FS11C, FS10A
								boolean eliminaRiepilogo = (comunicazioniDaEliminare.size() > 0);

								//if(this.bandiManager.getChiavePubblica(this.codice, RICHIESTA_TIPO_RIEPILOGO) != null) {
								if (eliminaRiepilogo) {  	
									comunicazioniDaEliminare.add(dettComunicazioneBustaRie);
								} else {
									comunicazioniDaAggiornare.add(dettComunicazioneBustaRie);
								}
							}
							
							// aggiorna la comunicazione FS11/FS10...
							// per le domande di partecipazione FS10, 
							// elimina la comunicazione solo se e' in compilazione (stato = 1)
							// altrimenti aggiornala
							if(domandaPartecipazione) {
								if("1".equals(dettComunicazione.getStato())) {
									comunicazioniDaEliminare.add(dettComunicazione);
								} else {
									comunicazioniDaAggiornare.add(dettComunicazione);
								}
							} else {
								comunicazioniDaAggiornare.add(dettComunicazione);
							}
							
							// completo il messaggio per la gestione eventi
							for (DettaglioComunicazioneType comunicazione : comunicazioniDaAggiornare) {
								ids.append(" ").append(comunicazione.getId());
							}
							if (comunicazioniDaEliminare.size() > 0) {
								ids.append(" ed eliminazione comunicazioni con id");
								for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
									ids.append(" ").append(comunicazione.getId());
								}
							}
							evento.setMessage(evento.getMessage() + ids.toString());
							
							// elimina le comunicazioni da eliminare 
							// ed aggiorna le comunicazioni da aggiornare
							if (comunicazioniDaEliminare.size() > 0) {
								for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
									this.comunicazioniManager.deleteComunicazione(
											comunicazione.getApplicativo(), 
											comunicazione.getId());
								}
							}
							
							if(comunicazioniDaAggiornare.size() > 0) {
								this.comunicazioniManager.updateStatoComunicazioni(
										comunicazioniDaAggiornare.toArray(new DettaglioComunicazioneType[]{}),
										CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
							}
							inviataComunicazione = true;
						}
					}
				}
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "rettifica");
				ExceptionUtils.manageExceptionError(t, this);
				evento.setError(t);
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "rettifica");
				this.addActionError(this.getText("Errors.send.outOfMemory"));
				evento.setError(e);
			} catch (IndexOutOfBoundsException e) {
				ApsSystemUtils.logThrowable(e, this, "rettifica");
				ExceptionUtils.manageExceptionError(e, this);
				evento.setError(e);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "rettifica");
				ExceptionUtils.manageExceptionError(t, this);
				evento.setError(t);
			} finally {
				this.eventManager.insertEvent(evento);
			}
			
			// FASE 2: invio mail
			if (inviataComunicazione && invioMail) {
				evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage("Invio mail ricevuta di conferma annullamento comunicazioni a "
						+ (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				
				try {
					this.sendMailConfermaImpresa(this.getDataOfferta());
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, non e'' stato possibile inviare all''impresa {} "
							+ "la ricevuta della richiesta di rettifica dell'offerta.",
							new Object[] {this.username});
					this.msgErrore = this.getText("Errors.rettificaBuste.sendMailError", new String[] {nomeOperazione});
					ApsSystemUtils.logThrowable(t, this, "rettifica");
					ExceptionUtils.manageExceptionError(t, this);
					evento.setError(t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}
			
			// FASE 3: chiusura stato operazione
			if (inviataComunicazione) {
				this.setTarget("successPage");
			} else if (!richiestafuoriTempo) {
				this.addActionError(this.getText("Errors.rettificaOfferta.somethingWentWrong"));
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
	protected Date retrieveDataInvio(String nomeOperazione) {
		Date data = null;
		try {
			data = ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
					this.getTextFromDefaultLocale("Errors.ntpTimeout", nomeOperazione));
			this.addActionError(this.getText("Errors.ntpTimeout", new String[]{nomeOperazione}));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (UnknownHostException e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
					this.getTextFromDefaultLocale("Errors.ntpUnknownHost", nomeOperazione));
			this.addActionError(this.getText("Errors.ntpUnknownHost", new String[]{nomeOperazione}));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
					this.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazione));
			this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[]{nomeOperazione}));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "retrieveDataInvio", 
					this.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazione));
			this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[]{nomeOperazione}));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return data;
	}

	/**
	 * ... 
	 */
	protected void sendMailConfermaImpresa(Date dataPubblicazione) throws ApsSystemException {
		String tipoRichiesta = this.getI18nLabel("LABEL_PARTECIPAZIONE");	//"della partecipazione";
		String richiestaTesto = this.getI18nLabel("LABEL_PARTECIPAZIONE");	//"la partecipazione";
		if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			tipoRichiesta = this.getI18nLabel("LABEL_OFFERTA");				//"dell'offerta "; 
			richiestaTesto = this.getI18nLabel("LABEL_OFFERTA");			//"l'offerta "; 
		} 
		
		String data = UtilityDate.convertiData(dataPubblicazione, UtilityDate.FORMATO_GG_MM_AAAA);
		
		String text = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_RETTIFICA_TESTO"), 
				new Object[] {richiestaTesto, data, tipoRichiesta});
		
		String subject = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_RETTIFICA_OGGETTO"), 
				new Object[] {this.codice, tipoRichiesta});
		
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { (String)((IUserProfile) this.getCurrentUser().getProfile()).getValue("email") }, 
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}
	
	/**
	 * Ritorna il nome dell'operazione selezionata nella lingua utilizzata
	 * dall'utente da interfaccia.
	 * 
	 * @param operazione
	 *            tipo di operazione
	 * @return stringa nella lingua dell'utente
	 */
	protected String getNomeOperazione(int operazione) {
		// rimuovere le stringhe fisse sottostanti ed utilizzare la label del bottone 
		// che consente di annullare e ripresentare, presente nel riepilogo
		String nomeOperazione = null;
		switch (this.getOperazione()) {
		case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
			nomeOperazione = "Annulla e ripresenta domanda di partecipazione"; //this.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_PARTECIPAZIONE");
			break;
		case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
			nomeOperazione = "Annulla e ripresenta offerta"; //this.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_OFFERTA");
			break;
		}
		return nomeOperazione;
	}

	/**
	 * restituisce il messaggio di annullamento domanda/offerta localizzato 
	 */
	public String getSuccessMessage() {
		String key = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
			? "LABEL_GARETEL_ANNULLA_PARTECIPAZIONE"
			: "LABEL_GARETEL_ANNULLA_OFFERTA");
		return MessageFormat.format(this.getI18nLabel(key), new Object[] {"#"+this.progressivoLista});
	}
	
	/**
	 * imposta i parametri di gara 
	 * - reset, annulmento, rettifica
	 */
	protected void recuperaTipoAnnullamento(DettaglioGaraType dettGara) {
		int tipoAnnullamento = 0; 
		this.annullamento = false;
		this.eliminazione = false;
		this.rettifica = false;
		try {
			String nomeOperazione = this.getNomeOperazione(this.getOperazione());
			this.setDataInvio(retrieveDataInvio(nomeOperazione));
			this.setUsername(this.getCurrentUser().getUsername());
			
			boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA); 
			boolean invioOfferta = !domandaPartecipazione; //(this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
			boolean ristretta = WizardPartecipazioneHelper.isGaraRistretta(dettGara.getDatiGeneraliGara().getIterGara());
			boolean negoziata = WizardPartecipazioneHelper.isGaraNegoziata(dettGara.getDatiGeneraliGara().getIterGara());
			
			// verifica se e' presente la cifratura delle buste
			boolean cifraturaAbilitata = false;
			if(invioOfferta) {
				cifraturaAbilitata = 
					(this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA) != null
					 || this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA) != null
					 || this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA) != null); 
			} else {
				cifraturaAbilitata = (this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA) != null);
			}
			
			// per le ristrette recupera il progressivo max 
			// delle domande di prequalifica...
			// se esiste una comunicazione FS11*/FS10* inviata (stato <> 1) 
			// allora l'offerta risulta inviata
			boolean presenteDomanda = false; 
			boolean inCompilazione = true;

			DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			filtri.setChiave1(this.getCurrentUser().getUsername());
			filtri.setChiave2(this.getCodice());
			filtri.setChiave3(this.getProgressivoOfferta());

			List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(filtri);
			
			if(comunicazioni != null) {
				String tipoComunicazione = (invioOfferta 
						? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
						: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
				
				for(int i = 0; i < comunicazioni.size(); i++) {
					if( comunicazioni.get(i).getTipoComunicazione().contains(tipoComunicazione)) {
						// verifica lo stato solo delle comunicazioni (FS11, FS11A, FS11B, FS11C, ... / FS10, FS10A, FS10R)
						if( !CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazioni.get(i).getStato()) ) {
							// offerta/domanda inviata
							inCompilazione = false;
						}
					} else if( PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT.equals(comunicazioni.get(i).getTipoComunicazione())) {
						// NB: questo serve solo nel caso delle offerte
						// verifica se esiste la domanda di partecipazione FS10...
						presenteDomanda = true;
					}
				}
			}
			
			// verifica il metodo quale metodo utilizzare tra
			// - annullamento  mmetodo per resettare FS11/FS11R e ripristinare un'offerta derivante da una domanda di prequalifica
			// - eliminazione  metodo per eliminare anche FS11/FS11R di un'offerta non ancora "inviata"
			// - rettifica     metodo gia' esistente per la rettifica di un'offerta
			tipoAnnullamento = OpenGestioneListaOfferteAction.getTipoAnnullamento(
					cifraturaAbilitata, 
					negoziata, 
					ristretta, 
					invioOfferta, 
					presenteDomanda, 
					inCompilazione, 
					this.progressivoOfferta);
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "getParametriGara");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		// imposta i flag per il tipo di annullamento
		this.annullamento = (tipoAnnullamento == 1);
		this.eliminazione = (tipoAnnullamento == 2);
		this.rettifica = (tipoAnnullamento == 3);
	}

	/**
	 * restituisce la lista degli stati per la ricerca delle comunicazioni 
	 */
	private List<String> getListaStatiComunicazioni() {
		List<String> stati = new ArrayList<String>();
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		return stati;
	}
	
	
	/**
	 * ... 
	 */
	private DettaglioComunicazioneType retriveComunicazione(
			String codice, 
			String tipoComunicazione, 
			List<String> stati) 
		throws ApsException 
	{
		return ComunicazioniUtilities.retrieveComunicazioneConStati(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				codice,
				this.getProgressivoOfferta(),
				tipoComunicazione,
				stati);	
	}
	
	/**
	 * aggiunge ad una lista tutte le comunicazioni FS11A, FS11B*, FS11C* relative alla gara 
	 * @throws ApsException 
	 */
	private void addBusteAmmTecEcoToList(List<DettaglioComunicazioneType> lista, RiepilogoBusteOffertaType riepilogo) 
		throws ApsException 
	{
		List<String> stati = this.getListaStatiComunicazioni();
		
		// busta amministrativa
		DettaglioComunicazioneType dettComunicazioneBustaAmm = this.retriveComunicazione(
				this.getCodice(), 
				PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA, 
				stati); 
	
		if(dettComunicazioneBustaAmm != null) {
			lista.add(dettComunicazioneBustaAmm);
		}
		
		RiepilogoLottoBustaType[] lotti = riepilogo.getLottoArray();
		if(lotti != null) {
			// GARA A LOTTI
			for(RiepilogoLottoBustaType lotto : lotti) {
				// busta tecnica
				DettaglioComunicazioneType dettComunicazioneBustaTec = this.retriveComunicazione(
						lotto.getCodiceLotto(), 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
						stati); 
				if(dettComunicazioneBustaTec != null) {
					lista.add(dettComunicazioneBustaTec);
				}
				
				// busta economica
				DettaglioComunicazioneType dettComunicazioneBustaEco = this.retriveComunicazione(
						lotto.getCodiceLotto(), 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
						stati); 
				if(dettComunicazioneBustaEco != null) {
					lista.add(dettComunicazioneBustaEco);
				}
			}
		} else {
			// GARA SEMPLICE
			// busta tecnica
			DettaglioComunicazioneType dettComunicazioneBustaTec = this.retriveComunicazione(
					this.getCodice(), 
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
					stati); 
			if(dettComunicazioneBustaTec != null) {
				lista.add(dettComunicazioneBustaTec);
			}
			
			// busta economica
			DettaglioComunicazioneType dettComunicazioneBustaEco = this.retriveComunicazione(
					this.getCodice(), 
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
					stati); 
			if(dettComunicazioneBustaEco != null) {
				lista.add(dettComunicazioneBustaEco);
			}
		}
	}
	
	/**
	 * ... 
	 */
	private boolean isRichiestafuoriTempo(
			DettaglioGaraType dettGara, 
			Event evento) 
	{
		this.richiestafuoriTempo = false;
		
		Date dataTermine = SendBusteAction.getDataTermine(dettGara, this.getOperazione());	
		if (dataTermine != null && this.getDataInvio().compareTo(dataTermine) > 0) {
			this.richiestafuoriTempo = true;
			this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo", new String[] {this.getNomeOperazione(this.getOperazione())}));
			evento.setLevel(Event.Level.ERROR);
		}
		
		return this.richiestafuoriTempo;
	}
	
	/**
	 * ...
	 */
	private int indexOfAllegato(ComunicazioneType comunicazione, String nomefile) {
		int j = -1;
		int i = 0;
		while (comunicazione.getAllegato() != null && i < comunicazione.getAllegato().length) {
			if (nomefile.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				j = i;
				break;
			}
			i++;
		}
		return j;
	}
	
	/**
	 * recupera il documento XML della busta di riepilogo
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	private TipoPartecipazioneDocument getTipoPartecipazione(Long idComunicazione) 
		throws ApsException, XmlException 
	{
		ComunicazioneType comunicazione = this.comunicazioniManager
			.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, idComunicazione);
		
		int i = this.indexOfAllegato(comunicazione, PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE);
		
		String xml = new String(comunicazione.getAllegato()[i].getFile());
		TipoPartecipazioneDocument doc = TipoPartecipazioneDocument.Factory.parse(xml);
		return doc;
	}
	
	/**
	 * recupera il documento XML della busta di riepilogo
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	private RiepilogoBusteOffertaDocument getRiepilogoBusteOfferta(Long idComunicazione) 
		throws ApsException, XmlException 
	{
		ComunicazioneType comunicazione = this.comunicazioniManager
			.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, idComunicazione);
		
		int i = this.indexOfAllegato(comunicazione, PortGareSystemConstants.NOME_FILE_BUSTA_RIEPILOGATIVA);
		
		String xml = new String(comunicazione.getAllegato()[i].getFile());
		RiepilogoBusteOffertaDocument doc = RiepilogoBusteOffertaDocument.Factory.parse(xml);
		return doc;
	}

	/**
	 * ... 
	 * @throws IOException 
	 * @throws ApsException 
	 */
	private ComunicazioneType xmlToComunicazione(
			XmlObject xmlDocument,
			Long idComunicazione, 
			String nomefile) 
		throws IOException, ApsException 
	{
		ComunicazioneType comunicazione = this.comunicazioniManager
			.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, idComunicazione);
		
		int i = this.indexOfAllegato(comunicazione, nomefile);
		
		//HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
		//XmlOptions opts = new XmlOptions();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		//suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
		//opts.setSaveSuggestedPrefixes(suggestedPrefixes);
		//xmlDocument.save(baos, opts);
		xmlDocument.save(baos);
		comunicazione.getAllegato()[i].setFile( baos.toString("UTF-8").getBytes() );
		
		return comunicazione;
	}
	
	/**
	 * recupera la posizione del progressivo offerta nella lista delle offerte
	 */
	protected int findProgressivoLista() {
		int progressivo = 0;
		
		String tipoComunicazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
			? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT
			: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT);
			
		try {
			// recupera l'elenco delle comunicazioni per individuare la posizione di
			// domanda/offerta nella lista
			DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			filtri.setChiave1(this.getCurrentUser().getUsername());
			filtri.setChiave2(this.codice);
			//filtri.setChiave3(this.progressivoOfferta);
			filtri.setTipoComunicazione(tipoComunicazione);
			
			List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(filtri);			
			for(int i = 0; i < comunicazioni.size(); i++) {
				if(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(comunicazioni.get(i).getStato()) ||
				   CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA.equals(comunicazioni.get(i).getStato()) ||
				   CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE.equals(comunicazioni.get(i).getStato()) ||
				   CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazioni.get(i).getStato())) {
					progressivo++;
					if(this.progressivoOfferta.equals(comunicazioni.get(i).getChiave3())) {
						// trovata la posizione domanda/offerta 
						break;
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "findProgressivoLista");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return progressivo;
	}
	
	/********************************************************************************
	 * gestione dei tipi di annullamento (reset, annullamento, rettifica) 
	 * per domanda/offerta 
	 ********************************************************************************/
	
	/**
	 * reset di un'offerta relativa ad una domanda di prequalifica  
	 * @throws Throwable 
	 */
	protected void annullamentoOfferta(DettaglioGaraType dettGara) 
		throws Throwable 
	{	
		this.richiestafuoriTempo = false;
		this.inviataComunicazione = false;
		
		Event evento = new Event();
		evento.setUsername(this.getUsername());
		evento.setDestination(this.getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Reset comunicazioni");
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		
		try {
			if( !this.isRichiestafuoriTempo(dettGara, evento) ) {
				boolean controlliOk = true;
				
				StringBuilder ids = new StringBuilder(" con id");
				
				List<String> stati = this.getListaStatiComunicazioni();
				
				String tipoComunicazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
							? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
							: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
				
				// recupera la busta di riepilogo FS11
				DettaglioComunicazioneType dettComunicazionePartecipazione = this.retriveComunicazione(
						this.getCodice(), 
						tipoComunicazione, 
						stati); 
				
				// recupera la busta di riepilogo FS11R
				DettaglioComunicazioneType dettComunicazioneBustaRie = this.retriveComunicazione(
						this.getCodice(), 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO, 
						stati); 
				
				if (dettComunicazionePartecipazione == null || dettComunicazioneBustaRie == null) {
					controlliOk = false;
				}
				
				if(controlliOk) {
					List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();
					
					// recupera la busta FS11 e resetta il lotti
					TipoPartecipazioneDocument tipoPartecipazione = this.getTipoPartecipazione(dettComunicazionePartecipazione.getId());
					
					tipoPartecipazione.getTipoPartecipazione().setCodiceLottoArray(null);
					
					// recupera la busta di riepilogo FS11R e resettala
					RiepilogoBusteOffertaDocument riepilogo = this.getRiepilogoBusteOfferta(dettComunicazioneBustaRie.getId());
					
					// prima aggiorna la lista delle buste da eliminare...
					this.addBusteAmmTecEcoToList(comunicazioniDaEliminare, riepilogo.getRiepilogoBusteOfferta());
					
					// ...e poi resetta la busta di riepilogo
					RiepilogoBustaType bustaAmm = riepilogo.getRiepilogoBusteOfferta().addNewBustaAmministrativa();
					riepilogo.getRiepilogoBusteOfferta().setBustaAmministrativa(bustaAmm);
					riepilogo.getRiepilogoBusteOfferta().setLottoArray(null);
					
					// elimina le comunicazioni FS11A, FS11B*, FS11C* 
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}
					
					ComunicazioneType comPartecipazione = this.xmlToComunicazione(
							tipoPartecipazione, 
							dettComunicazionePartecipazione.getId(),
							PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE);
					comPartecipazione.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
					this.comunicazioniManager.sendComunicazione(comPartecipazione);
					
					ComunicazioneType comRiepilogo = this.xmlToComunicazione(
							riepilogo, 
							dettComunicazioneBustaRie.getId(),
							PortGareSystemConstants.NOME_FILE_BUSTA_RIEPILOGATIVA);
					comRiepilogo.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
					this.comunicazioniManager.sendComunicazione(comRiepilogo);
							
					// completo il messaggio per la gestione eventi
					ids.append(comPartecipazione.getDettaglioComunicazione().getId())
					   .append(" ")
					   .append(comRiepilogo.getDettaglioComunicazione().getId());
					if (comunicazioniDaEliminare.size() > 0) {
						ids.append(" ed eliminazione comunicazioni con id");
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							ids.append(" ").append(comunicazione.getId());
						}
					}
					evento.setMessage(evento.getMessage() + ids.toString());
					
					this.inviataComunicazione = true;
				}
			}
		} catch (Exception e) {
			evento.setError(e);
			throw e;
		} catch (Throwable t) {
			evento.setError(t);
			throw t;
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}
	
	/**
	 * eliminazione  
	 * @throws Throwable 
	 */
	protected void eliminazioneOfferta(DettaglioGaraType dettGara) 
		throws Throwable 
	{	
		this.richiestafuoriTempo = false;
		this.inviataComunicazione = false;

		Event evento = new Event();
		evento.setUsername(this.getUsername());
		evento.setDestination(this.getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Annullamento comunicazioni");
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		
		try {
			if( !this.isRichiestafuoriTempo(dettGara, evento) ) {
				boolean controlliOk = true;
				
				StringBuilder ids = new StringBuilder();
				
				List<String> stati = this.getListaStatiComunicazioni();
				
				String tipoComunicazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
							? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
							: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
				
				// recupera la busta di riepilogo FS11
				DettaglioComunicazioneType dettComunicazionePartecipazione = this.retriveComunicazione(
						this.getCodice(), 
						tipoComunicazione, 
						stati); 
				
				// recupera la busta di riepilogo FS11R
				DettaglioComunicazioneType dettComunicazioneBustaRie = this.retriveComunicazione(
						this.getCodice(), 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO, 
						stati); 
				
				if (dettComunicazionePartecipazione == null || dettComunicazioneBustaRie == null) {
					controlliOk = false;
				}
				
				if(controlliOk) {
					List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();
					
					comunicazioniDaEliminare.add(dettComunicazionePartecipazione);
					comunicazioniDaEliminare.add(dettComunicazioneBustaRie);
					
					// recupera la busta di riepilogo FS11R
					RiepilogoBusteOffertaDocument riepilogo = this.getRiepilogoBusteOfferta(dettComunicazioneBustaRie.getId());
					
					this.addBusteAmmTecEcoToList(comunicazioniDaEliminare, riepilogo.getRiepilogoBusteOfferta());
					
					// elimina le comunicazioni 
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}
					
					// completo il messaggio per la gestione eventi
					if (comunicazioniDaEliminare.size() > 0) {
						ids.append(" ed eliminazione comunicazioni con id");
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							ids.append(" ").append(comunicazione.getId());
						}
					}
					evento.setMessage(evento.getMessage() + ids.toString());
					
					this.inviataComunicazione = true;
				}
			}
		} catch (Exception e) {
			evento.setError(e);
			throw e;
		} catch (Throwable t) {
			evento.setError(t);
			throw t;
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}
	
	/**
	 * rettifica le comunicazioni della domanda/offerta
	 * @throws Throwable 
	 */
	protected void rettificaOfferta(DettaglioGaraType dettGara) 
		throws Throwable 
	{
		this.richiestafuoriTempo = false;
		this.inviataComunicazione = false;
		
		Event evento = new Event();
		evento.setUsername(this.getUsername());
		evento.setDestination(this.getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Rettifica comunicazioni");
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());

		try {
			StringBuilder ids = new StringBuilder(" con id");
			//String nomeOperazione = this.getNomeOperazione(this.getOperazione());
			
			DettaglioComunicazioneType dettComunicazioneBustaAmm = null;
			DettaglioComunicazioneType dettComunicazioneBustaTec = null;
			DettaglioComunicazioneType dettComunicazioneBustaEco = null;
			DettaglioComunicazioneType dettComunicazioneBustaRie = null;

			List<String> stati = this.getListaStatiComunicazioni();

			String tipoComunicazionePartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
				? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
				: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
			
			// lo uso per regolare l'eliminazione della FS11R per le sole cifrate, 
			// in quanto a db non viene memorizzata nessuna chiave di cifratura e resta sempre in stato 1			  
			boolean controlliOk = true;
			
			this.richiestafuoriTempo = this.isRichiestafuoriTempo(dettGara, evento);
			if(this.richiestafuoriTempo) {
				controlliOk = false;
			}
			
			boolean isComunicazioniCifrate = false;

			DettaglioComunicazioneType dettComunicazionePartecipazione = null;
			
			List<DettaglioComunicazioneType> comunicazioniDaAggiornare = new ArrayList<DettaglioComunicazioneType>();
			List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();
			
			if (controlliOk) {
				dettComunicazionePartecipazione = this.retriveComunicazione(
						this.getCodice(), 
						tipoComunicazionePartecipazione, 
						stati); 
				
				if (dettComunicazionePartecipazione == null) {
					this.addActionMessage(this.getText("Errors.rettificaOfferta.alreadyDone"));
				} else {
					// recupera il dettaqlio della comunicazione di riepilogo FS11R
					dettComunicazioneBustaRie = this.retriveComunicazione(
							this.getCodice(), 
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO, 
							stati); 
					
					RiepilogoBusteOffertaDocument documento = (RiepilogoBusteOffertaDocument)this.session
							.get(PortGareSystemConstants.SESSION_ID_RIEPILOGO_BUSTE);
					
					RiepilogoLottoBustaType[] listaLotti = documento.getRiepilogoBusteOfferta().getLottoArray();
						
					// verifico che ci siano i documenti richiesti per la busta amministrativa
					dettComunicazioneBustaAmm = this.retriveComunicazione(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA, stati);
					
					// buste tecniche
					for(int i = 0; i < listaLotti.length; i++) {
						dettComunicazioneBustaTec = this.retriveComunicazione(
								listaLotti[i].getCodiceLotto(), 
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
								stati);
						if (dettComunicazioneBustaTec != null) {
							byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA); 
							boolean daEliminare = (chiavePubblica != null);
							if (daEliminare) {
								comunicazioniDaEliminare.add(dettComunicazioneBustaTec);
								if(chiavePubblica != null) {
									isComunicazioniCifrate = true;
								}
							} else {
								comunicazioniDaAggiornare.add(dettComunicazioneBustaTec);
							}
						}
					}
					
					// buste economiche
					for(int i = 0; i < listaLotti.length; i++){
						dettComunicazioneBustaEco = this.retriveComunicazione(
								listaLotti[i].getCodiceLotto(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
								stati);
						if (dettComunicazioneBustaEco != null) {
							byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA); 
							boolean daEliminare = (chiavePubblica != null);
							if (daEliminare) {
								comunicazioniDaEliminare.add(dettComunicazioneBustaEco);
								if(chiavePubblica != null) {
									isComunicazioniCifrate = true;
								}
							} else {
								comunicazioniDaAggiornare.add(dettComunicazioneBustaEco);
							}
						}
					}
					this.session.remove(PortGareSystemConstants.SESSION_ID_RIEPILOGO_BUSTE);
				}
			}

			if (controlliOk) {
				if (dettComunicazionePartecipazione != null) {
					this.setDataOfferta(dettComunicazionePartecipazione.getDataPubblicazione());
					if(this.getDataOfferta() == null && dettComunicazionePartecipazione.getDataInserimento() != null) {
						this.setDataOfferta( dettComunicazionePartecipazione.getDataInserimento().getTime() );
					}
					
					// busta amministrativa
					if (dettComunicazioneBustaAmm != null) {
						byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA); 
						boolean daEliminare = (chiavePubblica != null);
						if (daEliminare) {
							comunicazioniDaEliminare.add(dettComunicazioneBustaAmm);
						} else {
							comunicazioniDaAggiornare.add(dettComunicazioneBustaAmm);
						}
					}
					
					boolean daEliminare = (isComunicazioniCifrate);
					
					// in caso di comunicazioni cifrate elimina il riepilogo FS11R
					if(dettComunicazioneBustaRie != null) {
						if (daEliminare) {
							comunicazioniDaEliminare.add(dettComunicazioneBustaRie);
							this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
						}else{
							comunicazioniDaAggiornare.add(dettComunicazioneBustaRie);
						}
					}
					
					// in caso di comunicazione cifrata si elimina anche la comunicazione FS11
					if(daEliminare) {
						comunicazioniDaEliminare.add(dettComunicazionePartecipazione);
					} else {
						comunicazioniDaAggiornare.add(dettComunicazionePartecipazione);	
					}
					
					// elimina le comunicazioni
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}
					
					// aggiorna le comunicazioni in stato BOZZA
					if(!comunicazioniDaAggiornare.isEmpty()) {
						this.comunicazioniManager.updateStatoComunicazioni(
								comunicazioniDaAggiornare.toArray(new DettaglioComunicazioneType[] {}),
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
					}
					
					// completo il messaggio per la gestione eventi
					for (DettaglioComunicazioneType comunicazione : comunicazioniDaAggiornare) {
						ids.append(" ").append(comunicazione.getId());
					}
					if (comunicazioniDaEliminare.size() > 0) {
						ids.append(" ed eliminazione comunicazioni con id");
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							ids.append(" ").append(comunicazione.getId());
						}
					}
					evento.setMessage(evento.getMessage() + ids.toString());

					this.inviataComunicazione = true;
				}
			}
		} catch (ApsException e) {
			//ApsSystemUtils.logThrowable(t, this, "annullaComunicazioni");
			//ExceptionUtils.manageExceptionError(t, this);
			evento.setError(e);
			throw e;
		} catch (OutOfMemoryError e) {
			evento.setError(e);
			throw e;
		} catch (IndexOutOfBoundsException e) {
			evento.setError(e);
			throw e;
		} catch (Throwable t) {
			evento.setError(t);
			throw t;
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * eliminazione domanda di partecipazione
	 * @throws Throwable 
	 */
	protected void eliminazioneDomanda(DettaglioGaraType dettGara) 
		throws Throwable 
	{	
		this.richiestafuoriTempo = false;
		this.inviataComunicazione = false;

		Event evento = new Event();
		evento.setUsername(this.getUsername());
		evento.setDestination(this.getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Annullamento comunicazioni");
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		
		try {
			if( !this.isRichiestafuoriTempo(dettGara, evento) ) {
				boolean controlliOk = true;
				
				StringBuilder ids = new StringBuilder();
				
				List<String> stati = this.getListaStatiComunicazioni();
				
				// recupera la busta di riepilogo FS10
				DettaglioComunicazioneType dettComunicazionePartecipazione = this.retriveComunicazione(
						this.getCodice(), 
						PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT, 
						stati); 
				
				// recupera la busta di riepilogo FS10R
				DettaglioComunicazioneType dettComunicazioneBustaRie = this.retriveComunicazione(
						this.getCodice(), 
						PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO, 
						stati); 
				
				if (dettComunicazionePartecipazione == null || dettComunicazioneBustaRie == null) {
					controlliOk = false;
				}
				
				if(controlliOk) {
					List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();
					
					comunicazioniDaEliminare.add(dettComunicazionePartecipazione);
					comunicazioniDaEliminare.add(dettComunicazioneBustaRie);
				
					// elimina le comunicazioni 
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}
					
					// completo il messaggio per la gestione eventi
					if (comunicazioniDaEliminare.size() > 0) {
						ids.append(" ed eliminazione comunicazioni con id");
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							ids.append(" ").append(comunicazione.getId());
						}
					}
					evento.setMessage(evento.getMessage() + ids.toString());
					
					this.inviataComunicazione = true;
				}
			}
		} catch (Exception e) {
			evento.setError(e);
			throw e;
		} catch (Throwable t) {
			evento.setError(t);
			throw t;
		} finally {
			this.eventManager.insertEvent(evento);
		}	
	}
		
	/**
	 * rettifica le comunicazioni della domanda/offerta
	 * @throws Throwable 
	 */
	protected void rettificaDomanda(DettaglioGaraType dettGara) 
		throws Throwable 
	{
		this.richiestafuoriTempo = false;
		this.inviataComunicazione = false;
		
		Event evento = new Event();
		evento.setUsername(this.getUsername());
		evento.setDestination(this.getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Rettifica comunicazioni");
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());

		try {
			StringBuilder ids = new StringBuilder(" con id");
			//String nomeOperazione = this.getNomeOperazione(this.getOperazione());
			
			DettaglioComunicazioneType dettComunicazioneBustaPreq = null;
			DettaglioComunicazioneType dettComunicazioneBustaRie = null;

			List<String> stati = this.getListaStatiComunicazioni();
	
			// lo uso per regolare l'eliminazione della FS11R per le sole cifrate, 
			// in quanto a db non viene memorizzata nessuna chiave di cifratura e resta sempre in stato 1			  
			boolean controlliOk = true;
			
			this.richiestafuoriTempo = this.isRichiestafuoriTempo(dettGara, evento);
			if(this.richiestafuoriTempo) {
				controlliOk = false;
			}
			
			boolean isComunicazioniCifrate = false;

			DettaglioComunicazioneType dettComunicazionePartecipazione = null;
			
			List<DettaglioComunicazioneType> comunicazioniDaAggiornare = new ArrayList<DettaglioComunicazioneType>();
			List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();
			
			if (controlliOk) {
				dettComunicazionePartecipazione = this.retriveComunicazione(
						this.getCodice(), 
						PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT, 
						stati); 
				
				if (dettComunicazionePartecipazione == null) {
					this.addActionMessage(this.getText("Errors.rettificaOfferta.alreadyDone"));
				} else {
					// recupera il dettaqlio della comunicazione di riepilogo FS11R
					dettComunicazioneBustaRie = this.retriveComunicazione(
							this.getCodice(), 
							PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO, 
							stati); 
						
					// verifico che ci siano i documenti richiesti per la busta di prequalifica
					dettComunicazioneBustaPreq = this.retriveComunicazione(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA, stati);
				}
			}

			if (controlliOk) {
				if (dettComunicazionePartecipazione != null) {
					this.setDataOfferta(dettComunicazionePartecipazione.getDataPubblicazione());
					if(this.getDataOfferta() == null && dettComunicazionePartecipazione.getDataInserimento() != null) {
						this.setDataOfferta( dettComunicazionePartecipazione.getDataInserimento().getTime() );
					}
					
					// busta prequalifica FS10A
					if (dettComunicazioneBustaPreq != null) {
						byte[] chiavePubblica = this.bandiManager.getChiavePubblica(this.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA); 
						boolean daEliminare = (chiavePubblica != null);
						if (daEliminare) {
							comunicazioniDaEliminare.add(dettComunicazioneBustaPreq);
						} else {
							comunicazioniDaAggiornare.add(dettComunicazioneBustaPreq);
						}
					}
					
					boolean daEliminare = (isComunicazioniCifrate);
					
					// in caso di comunicazioni cifrate elimina il riepilogo FS10R
					if(dettComunicazioneBustaRie != null) {
						if (daEliminare) {
							comunicazioniDaEliminare.add(dettComunicazioneBustaRie);
							this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
						}else{
							comunicazioniDaAggiornare.add(dettComunicazioneBustaRie);
						}
					}
					
					// in caso di comunicazione cifrata si elimina anche la comunicazione FS10
					if(daEliminare) {
						comunicazioniDaEliminare.add(dettComunicazionePartecipazione);
					} else {
						comunicazioniDaAggiornare.add(dettComunicazionePartecipazione);	
					}
					
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}
					
					if(!comunicazioniDaAggiornare.isEmpty()) {
						this.comunicazioniManager.updateStatoComunicazioni(
								comunicazioniDaAggiornare.toArray(new DettaglioComunicazioneType[] {}),
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
					}

					// completo il messaggio per la gestione eventi
					for (DettaglioComunicazioneType comunicazione : comunicazioniDaAggiornare) {
						ids.append(" ").append(comunicazione.getId());
					}
					if (comunicazioniDaEliminare.size() > 0) {
						ids.append(" ed eliminazione comunicazioni con id");
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							ids.append(" ").append(comunicazione.getId());
						}
					}
					evento.setMessage(evento.getMessage() + ids.toString());
					
					this.inviataComunicazione = true;
				}
			}
		} catch (ApsException e) {
			//ApsSystemUtils.logThrowable(t, this, "annullaDomanda");
			//ExceptionUtils.manageExceptionError(t, this);
			evento.setError(e);
			throw e;
		} catch (OutOfMemoryError e) {
			evento.setError(e);
			throw e;
		} catch (IndexOutOfBoundsException e) {
			evento.setError(e);
			throw e;
		} catch (Throwable t) {
			evento.setError(t);
			throw t;
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

}
