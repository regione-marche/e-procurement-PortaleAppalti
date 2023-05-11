package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
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

	@Validate(EParamValidation.CODICE)
	protected String codice;
	protected int operazione;
	@Validate(EParamValidation.USERNAME)
	protected String username;
	@Validate(EParamValidation.DIGIT)
	protected String progressivoOfferta;
	protected Date dataInvio;
	protected Date dataOfferta;
	@Validate(EParamValidation.ERRORE)
	protected String msgErrore;
	@Validate(EParamValidation.INTERO)
	protected String fromListaOfferte;        // "1" = true altrimenti false

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
				String nomeFunzioneEvento = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA)
						? "Rettifica domanda di partecipazione"
						: "Rettifica offerta";

				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						this.operazione);
				buste.putToSession();
				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();

				DettaglioGaraType dettGara = buste.getDettaglioGara();
				this.dataInvio = retrieveDataInvio(nomeOperazione);
				this.progressivoLista = this.findProgressivoLista();
				boolean daListaOfferte = ("1".equals(this.fromListaOfferte));
				this.recuperaTipoAnnullamento(buste);

				if (this.dataInvio != null) {

					Date dataTermineInvioOfferta = SendBusteAction.getDataTermine(dettGara, this.operazione);
					boolean controlliOk = true;

					if (dataTermineInvioOfferta != null && this.dataInvio.compareTo(dataTermineInvioOfferta) > 0) {
						controlliOk = false;
						this.addActionError(this.getText("Errors.rettificaRichiestaFuoriTempoMassimo", new String[] {nomeOperazione}));

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

						bustaPartecipazione.get(stati);

						if(bustaPartecipazione.getId() <= 0) {
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
			try {
				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						this.operazione);

				buste.get();

				this.username = buste.getUsername();
				nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
				boolean domandaPartecipazione = buste.isDomandaPartecipazione();

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
					DettaglioGaraType dettGara = buste.getDettaglioGara();

					this.dataInvio = retrieveDataInvio(nomeOperazione);
					buste.getBustaPartecipazione().getHelper().setDataPresentazione(this.dataInvio);

					this.recuperaTipoAnnullamento(buste);

					invioMail = (this.rettifica);

					if (this.dataInvio != null) {
						Date dataTermine = SendBusteAction.getDataTermine(dettGara, this.operazione);
						boolean controlliOk = true;

						if (dataTermine != null && this.dataInvio.compareTo(dataTermine) > 0) {
							controlliOk = false;
							richiestafuoriTempo = true;
							this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo", new String[] {nomeOperazione}));
							evento.setLevel(Event.Level.ERROR);
						}

						// recupera le buste negli stati possibili...
						if (controlliOk) {
							List<String> stati = new ArrayList<String>();
							stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
							stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
							stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

							controlliOk = controlliOk && buste.get(stati);
						}

						// ...elimina/aggiorna le comunicazioni della gara
						if (controlliOk) {
							buste.putToSession();
							BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
							BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
							BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
							BustaTecnica bustaTecnica = buste.getBustaTecnica();
							BustaEconomica bustaEconomica = buste.getBustaEconomica();
							BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

							// prepara 2 liste di comunicazioni (da eliminare e da aggiornare)
							List<DettaglioComunicazioneType> comunicazioniDaAggiornare = new ArrayList<DettaglioComunicazioneType>();
							List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

							if (bustaPartecipazione != null) {

								this.setDataOfferta(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataPubblicazione());

								if (bustaPrequalifica != null && bustaPrequalifica.getId() > 0) {
									if (bustaPrequalifica.getChiavePubblica() != null) {
										comunicazioniDaEliminare.add(bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione());
									} else {
										comunicazioniDaAggiornare.add(bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione());
									}
								}
								if (bustaAmministrativa != null && bustaAmministrativa.getId() > 0) {
									if (bustaAmministrativa.getChiavePubblica() != null) {
										comunicazioniDaEliminare.add(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione());
									} else {
										comunicazioniDaAggiornare.add(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione());
									}
								}
								if (bustaTecnica != null && bustaTecnica.getId() > 0) {
									if (bustaTecnica.getChiavePubblica() != null) {
										comunicazioniDaEliminare.add(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
									} else {
										comunicazioniDaAggiornare.add(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
									}
								}
								if (bustaEconomica != null && bustaEconomica.getId() > 0) {
									if (bustaEconomica.getChiavePubblica() != null) {
										comunicazioniDaEliminare.add(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
									} else {
										comunicazioniDaAggiornare.add(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
									}
								}

								if(bustaRiepilogo.getId() > 0) {
									// se ci sono comunicazioni da eliminare ovvero comunicazioni con cifratura,
									// allora possono essere: FS11A, FS11B, FS11C, FS10A
									boolean eliminaRiepilogo = (comunicazioniDaEliminare.size() > 0);
									if (eliminaRiepilogo) {
										comunicazioniDaEliminare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());
									} else {
										comunicazioniDaAggiornare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());
									}
								}

								// aggiorna la comunicazione FS11/FS10...
								// per le domande di partecipazione FS10,
								// elimina la comunicazione solo se e' in compilazione (stato = 1)
								// altrimenti aggiornala
								if(domandaPartecipazione) {
									if(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(bustaPartecipazione.getComunicazioneFlusso().getStato())) {
										comunicazioniDaEliminare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
									} else {
										comunicazioniDaAggiornare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
									}
								} else {
									comunicazioniDaAggiornare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
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


								// elimina dalla busta della forma di partecipazione il pdf di riepilogo...
								this.removeRiepilogoAllegati(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());

								// elimina le comunicazioni da eliminare...
								if (comunicazioniDaEliminare.size() > 0) {
									for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
										this.comunicazioniManager.deleteComunicazione(
												comunicazione.getApplicativo(),
												comunicazione.getId());
									}
								}

								// ...ed aggiorna le comunicazioni da aggiornare
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
						Date data = buste.getBustaPartecipazione().getComunicazioneFlusso().getDettaglioComunicazione().getDataAggiornamentoStato().getTime();
						this.sendMailConfermaImpresa(data);
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

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "rettifica");
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
		String tipoRichiesta = this.getI18nLabel("LABEL_PARTECIPAZIONE");    //"della partecipazione";
		String richiestaTesto = this.getI18nLabel("LABEL_PARTECIPAZIONE");    //"la partecipazione";
		if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			tipoRichiesta = this.getI18nLabel("LABEL_OFFERTA");                //"dell'offerta ";
			richiestaTesto = this.getI18nLabel("LABEL_OFFERTA");            //"l'offerta ";
		}

		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA);

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
	 * restituisce il messaggio di annullamento domanda/offerta localizzato
	 */
	public String getSuccessMessage() {
		String key = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
				? "LABEL_GARETEL_ANNULLA_PARTECIPAZIONE"
				: "LABEL_GARETEL_ANNULLA_OFFERTA");
		return MessageFormat.format(this.getI18nLabel(key), new Object[] {"#"+this.progressivoLista});
	}

	/**
	 * imposta i parametri del tipo di annullamento/rettifica di una gara
	 * - reset
	 * - annulmento
	 * - rettifica
	 */
	protected void recuperaTipoAnnullamento(GestioneBuste buste) {
		int tipoAnnullamento = 0;
		this.annullamento = false;
		this.eliminazione = false;
		this.rettifica = false;
		try {
			String nomeOperazione = this.getNomeOperazione(this.operazione);
			this.setDataInvio(retrieveDataInvio(nomeOperazione));
			this.setUsername(this.getCurrentUser().getUsername());

			BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
			BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
			BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
			BustaTecnica bustaTecnica = buste.getBustaTecnica();
			BustaEconomica bustaEconomica = buste.getBustaEconomica();

			// verifica se e' presente la cifratura delle buste
			boolean cifraturaAbilitata = false;
			if(buste.isInvioOfferta()) {
				cifraturaAbilitata = (bustaAmministrativa.getChiavePubblica() != null
						|| bustaTecnica.getChiavePubblica() != null
						|| bustaEconomica.getChiavePubblica() != null);
			} else {
				cifraturaAbilitata = (bustaPrequalifica.getChiavePubblica() != null);
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
			filtri.setChiave2(this.codice);
			filtri.setChiave3(this.progressivoOfferta);

			List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(filtri);

			if(comunicazioni != null) {
				String tipoComunicazione = bustaPartecipazione.getComunicazioneFlusso().getTipoComunicazione();

				for(int i = 0; i < comunicazioni.size(); i++) {
					if( comunicazioni.get(i).getTipoComunicazione().contains(tipoComunicazione)) {
						// verifica lo stato solo delle comunicazioni (FS11, FS11A, FS11B, FS11C, ... / FS10, FS10A, FS10R)
						if( !CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazioni.get(i).getStato()) ) {
							// offerta/domanda inviata
							inCompilazione = false;
						}
					} else if( PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT.equals(comunicazioni.get(i).getTipoComunicazione()) ) {
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
					buste.isNegoziata(),
					buste.isRistretta(),
					buste.isInvioOfferta(),
					presenteDomanda,
					inCompilazione,
					this.progressivoOfferta);

		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "recuperaTipoAnnullamento");
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
	 * verifica se la richiesta dell'operatore e' fuori tempo massimo
	 */
	private boolean isRichiestaFuoriTempo(
			GestioneBuste buste,
			Event evento) {
		this.richiestafuoriTempo = false;

		Date dataTermine = SendBusteAction.getDataTermine(buste.getDettaglioGara(), this.operazione);
		if (dataTermine != null && this.getDataInvio().compareTo(dataTermine) > 0) {
			this.richiestafuoriTempo = true;
			this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo",
					new String[] {this.getNomeOperazione(this.operazione)}));
			evento.setLevel(Event.Level.ERROR);
		}

		return this.richiestafuoriTempo;
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

	/**
	 *******************************************************************************
	 * gestione dei tipi di annullamento (reset, annullamento, rettifica)
	 * per domanda/offerta
	 *******************************************************************************
	 */

	/**
	 * reset di un'offerta relativa ad una domanda di prequalifica
	 * @throws Throwable
	 */
	protected void annullamentoOfferta(GestioneBuste buste)
			throws Throwable {
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
			if( !this.isRichiestaFuoriTempo(buste, evento) ) {
				boolean controlliOk = true;

				StringBuilder ids = new StringBuilder(" con id");

				buste.get( this.getListaStatiComunicazioni() );

				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

				String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
				bustaPartecipazione.getHelper().setDataPresentazione(this.retrieveDataInvio(nomeOperazione));

				if(bustaPartecipazione.getId() < 0 || bustaRiepilogo.getId() < 0) {
					controlliOk = false;
				}

				if(controlliOk) {
					List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

					// recupera la lista delle buste da eliminare (senza FS11/FS10 e FS11R/FS10R)...
					comunicazioniDaEliminare = buste.getListaComunicazioni(this.getListaStatiComunicazioni());

					bustaPartecipazione.getHelper().setDataPresentazione(new Date());

					// resetta i lotti nella partecipazione...
					bustaPartecipazione.getHelper().setLotti(null);

					// resetta la busta di riepilogo...
					bustaRiepilogo.getHelper().setBustaAmministrativa( new RiepilogoBustaBean() );
					bustaRiepilogo.getHelper().setListaCompletaLotti( new ArrayList<String>() );    // o null ???

					// elimina le comunicazioni FS11A, FS11B*, FS11C*
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}

					// invia FS11/FS10 e FS11R/FS10R aggiornate in stato BOZZA...
					bustaPartecipazione.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
					bustaRiepilogo.send(null);

					// completo il messaggio per la gestione eventi
					ids.append(bustaPartecipazione.getId())
							.append(" ")
							.append(bustaRiepilogo.getId());

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
	protected void eliminazioneOfferta(GestioneBuste buste)
			throws Throwable {
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
			if( !this.isRichiestaFuoriTempo(buste, evento) ) {
				boolean controlliOk = true;

				StringBuilder ids = new StringBuilder();

				buste.get( this.getListaStatiComunicazioni() );

				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

				String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
				bustaPartecipazione.getHelper().setDataPresentazione(this.retrieveDataInvio(nomeOperazione));

				if(bustaPartecipazione.getId() < 0 || bustaRiepilogo.getId() < 0) {
					controlliOk = false;
				}

				if(controlliOk) {
					List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

					// recupera l'elenco delle comunicazioni da eliminare...
					comunicazioniDaEliminare = buste.getListaComunicazioni(this.getListaStatiComunicazioni());
					comunicazioniDaEliminare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
					comunicazioniDaEliminare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());

					// elimina le comunicazioni...
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}

					// completo il messaggio per la gestione eventi...
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
	protected void rettificaOfferta(GestioneBuste buste)
			throws Throwable {
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

			// lo uso per regolare l'eliminazione della FS11R per le sole cifrate,
			// in quanto a db non viene memorizzata nessuna chiave di cifratura e resta sempre in stato 1
			boolean controlliOk = true;

			this.richiestafuoriTempo = this.isRichiestaFuoriTempo(buste, evento);
			if(this.richiestafuoriTempo) {
				controlliOk = false;
			}

			boolean isComunicazioniCifrate = false;

			buste.get(this.getListaStatiComunicazioni());

			BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
			BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
			BustaTecnica bustaTecnica = buste.getBustaTecnica();
			BustaEconomica bustaEconomica = buste.getBustaEconomica();
			BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

			String nomeOperazione = GestioneBuste.getNomeOperazione(this.operazione);
			bustaPartecipazione.getHelper().setDataPresentazione(this.retrieveDataInvio(nomeOperazione));

			List<String> listaLotti = bustaRiepilogo.getHelper().getListaCompletaLotti();

			// liste delle comunicazioni da eliminare/aggiornare
			List<DettaglioComunicazioneType> comunicazioniDaAggiornare = new ArrayList<DettaglioComunicazioneType>();
			List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

			if (controlliOk) {
				if (bustaPartecipazione.getId() < 0) {
					this.addActionMessage(this.getText("Errors.rettificaOfferta.alreadyDone"));
				} else {
					// buste tecniche
					for(int i = 0; i < listaLotti.size(); i++) {
						bustaTecnica.get(this.getListaStatiComunicazioni(), listaLotti.get(i));

						if (bustaTecnica.getId() > 0) {
							byte[] chiavePubblica = bustaTecnica.getChiavePubblica();
							boolean daEliminare = (chiavePubblica != null);
							if (daEliminare) {
								comunicazioniDaEliminare.add(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
								if(chiavePubblica != null) {
									isComunicazioniCifrate = true;
								}
							} else {
								comunicazioniDaAggiornare.add(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione());
							}
						}
					}

					// buste economiche
					for(int i = 0; i < listaLotti.size(); i++) {
						bustaEconomica.get(this.getListaStatiComunicazioni(), listaLotti.get(i));

						if (bustaEconomica.getId() > 0) {
							byte[] chiavePubblica = bustaEconomica.getChiavePubblica();
							boolean daEliminare = (chiavePubblica != null);
							if (daEliminare) {
								comunicazioniDaEliminare.add(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
								if(chiavePubblica != null) {
									isComunicazioniCifrate = true;
								}
							} else {
								comunicazioniDaAggiornare.add(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione());
							}
						}

					}
//					this.session.remove(PortGareSystemConstants.SESSION_ID_RIEPILOGO_BUSTE);
				}
			}

			if (controlliOk) {
				if (bustaPartecipazione.getId() > 0) {

					this.setDataOfferta(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataPubblicazione());
					if(this.getDataOfferta() == null &&
							bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataInserimento() != null) {
						this.setDataOfferta( bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataInserimento().getTime() );
					}

					// busta amministrativa
					if (bustaAmministrativa.getId() > 0) {
						byte[] chiavePubblica = bustaAmministrativa.getChiavePubblica();
						boolean daEliminare = (chiavePubblica != null);
						if (daEliminare) {
							comunicazioniDaEliminare.add(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione());
						} else {
							comunicazioniDaAggiornare.add(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione());
						}
					}

					// se ci sono comunicazioni cifrate allora posson solo essere eliminate...
					boolean daEliminare = (isComunicazioniCifrate);

					// in caso di comunicazioni cifrate elimina il riepilogo FS11R
					if(bustaRiepilogo.getId() > 0) {
						if (daEliminare) {
							comunicazioniDaEliminare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());
//							this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
						}else{
							comunicazioniDaAggiornare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());
						}
					}

					// in caso di comunicazione cifrata si elimina anche la comunicazione FS11
					if(daEliminare) {
						comunicazioniDaEliminare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
					} else {
						comunicazioniDaAggiornare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
					}

					// rimuovi dalla comunicazione FS10/FS11 il pdf di riepilogo
                    this.removeRiepilogoAllegati(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());

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

					// completa il messaggio per la gestione eventi
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
     * Rimuove il riepilogo allegati in formato PDF o PDF.TSD dalla busta FS10 (o FS11) 
     * visto che, rettificando, non ha senso mantenere il riepilogo allegati generato 
     * all'atto del precedente invio ora rettificato.
     * 
     * @param dettComunicazionePartecipazione
     * @throws ApsException
     */
	private void removeRiepilogoAllegati(DettaglioComunicazioneType dettComunicazionePartecipazione) throws ApsException {
		ComunicazioneType comunicazioneTestata = this.comunicazioniManager.getComunicazione(
				dettComunicazionePartecipazione.getApplicativo(),
				dettComunicazionePartecipazione.getId());
		AllegatoComunicazioneType[] allegati = comunicazioneTestata.getAllegato();
		boolean rimossoRiepilogo = false;
		List<AllegatoComunicazioneType> nuovaListaAllegati = new ArrayList<AllegatoComunicazioneType>();
		for (int i = 0; i < allegati.length; i++) {
			if (PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE.equalsIgnoreCase(allegati[i].getNomeFile())
					|| PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE.equalsIgnoreCase(allegati[i].getNomeFile())) {
				rimossoRiepilogo = true;
			} else {
				nuovaListaAllegati.add(allegati[i]);
			}
		}
		if (rimossoRiepilogo) {
			// si invia la nuova lista di allegati dopo aver rimosso il riepilogo allegati
			comunicazioneTestata.setAllegato(nuovaListaAllegati.toArray(new AllegatoComunicazioneType[] {}));
			this.comunicazioniManager.sendComunicazione(comunicazioneTestata);
		}
	}

	/**
	 * eliminazione domanda di partecipazione
	 * @throws Throwable
	 */
	protected void eliminazioneDomanda(GestioneBuste buste)
			throws Throwable {
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
			if( !this.isRichiestaFuoriTempo(buste, evento) ) {
				boolean controlliOk = true;

				StringBuilder ids = new StringBuilder();

				buste.get(this.getListaStatiComunicazioni());

				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

				if (bustaPartecipazione.getId() < 0 || bustaRiepilogo.getId() < 0) {
					controlliOk = false;
				}

				if(controlliOk) {
					List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

					comunicazioniDaEliminare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
					comunicazioniDaEliminare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());

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
	protected void rettificaDomanda(GestioneBuste buste)
			throws Throwable {
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

			// lo uso per regolare l'eliminazione della FS11R per le sole cifrate,
			// in quanto a db non viene memorizzata nessuna chiave di cifratura e resta sempre in stato 1
			boolean controlliOk = true;

			this.richiestafuoriTempo = this.isRichiestaFuoriTempo(buste, evento);
			if(this.richiestafuoriTempo) {
				controlliOk = false;
			}

			buste.get(this.getListaStatiComunicazioni());

			BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
			BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
			BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

			boolean isComunicazioniCifrate = false;

			// liste delle comunicazioni da aggiornare/eliminare
			List<DettaglioComunicazioneType> comunicazioniDaAggiornare = new ArrayList<DettaglioComunicazioneType>();
			List<DettaglioComunicazioneType> comunicazioniDaEliminare = new ArrayList<DettaglioComunicazioneType>();

			if (controlliOk) {
				if (bustaPartecipazione.getId() > 0) {
					this.setDataOfferta(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataPubblicazione());
					if(this.getDataOfferta() == null &&
							bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataInserimento() != null) {
						this.setDataOfferta( bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione().getDataInserimento().getTime() );
					}

					// busta prequalifica FS10A
					if (bustaPrequalifica.getId() > 0) {
						byte[] chiavePubblica = bustaPrequalifica.getChiavePubblica();
						boolean daEliminare = (chiavePubblica != null);
						if (daEliminare) {
							comunicazioniDaEliminare.add(bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione());
						} else {
							comunicazioniDaAggiornare.add(bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione());
						}
					}

					boolean daEliminare = (isComunicazioniCifrate);

					// in caso di comunicazioni cifrate elimina anche il riepilogo FS10R
					if(bustaRiepilogo.getId() > 0) {
						if (daEliminare) {
							comunicazioniDaEliminare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());
						}else{
							comunicazioniDaAggiornare.add(bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione());
						}
					}

					// in caso di comunicazione cifrata si elimina anche la comunicazione FS10
					if(daEliminare) {
						comunicazioniDaEliminare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
					} else {
						comunicazioniDaAggiornare.add(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
					}

					// elimina le comunicazioni cifrate...
					if (comunicazioniDaEliminare.size() > 0) {
						for (DettaglioComunicazioneType comunicazione : comunicazioniDaEliminare) {
							this.comunicazioniManager.deleteComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
						}
					}

					// aggiornare le altre comunicazioni...
					if(!comunicazioniDaAggiornare.isEmpty()) {
						this.comunicazioniManager.updateStatoComunicazioni(
								comunicazioniDaAggiornare.toArray(new DettaglioComunicazioneType[] {}),
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
					}

					// completo il messaggio per la gestione eventi...
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
