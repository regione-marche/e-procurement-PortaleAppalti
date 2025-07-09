 package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class RettificaOfferteDistinteAction extends RettificaOffertaAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2091501389732573775L;

	private static final String OPEN_RIEPILOGO_OFFERTE_DISTINTE = "openRiepilogoOfferteDistinte";
	private static final String OPEN_GESTIONE_LISTA_OFFERTE 	= "openGestioneListaOfferte";
	
	@Validate(EParamValidation.ACTION)
	private String nextResultAction = OPEN_RIEPILOGO_OFFERTE_DISTINTE;	// default

	
	public String getFromListaOfferte() {
		return fromListaOfferte;
	}

	public void setFromListaOfferte(String fromListaOfferte) {
		this.fromListaOfferte = fromListaOfferte;
		
		// se il chiamante e' la lista delle offerte ("forma partecipazione per lotto")
		// si riapre la lista delle offerte...
		this.nextResultAction = OPEN_RIEPILOGO_OFFERTE_DISTINTE;
		if("1".equalsIgnoreCase(this.fromListaOfferte)) {
			this.nextResultAction = OPEN_GESTIONE_LISTA_OFFERTE;
		}
	}
	
	/**
	 * restituisci la prossima action per l'action chaining
	 */
	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * visualizza la la conferma per l'operazione di rettifica/annullamento 
	 */
	public String confirmRettifica() {
		this.setTarget("reopen");
		
		// verifica il profilo di accesso ed esegui un LOCK alla funzione 
		if( !lockAccessoFunzione(EFlussiAccessiDistinti.OFFERTA_GARA, this.codice) ) {
			return this.getTarget();
		}

		this.offerteDistinte = true;
		// forzo l'operazione dato che non arriva dal submit e gestisco solo questa tipologia
		this.setOperazione(PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						this.operazione);

				DettaglioGaraType dettGara = buste.getDettaglioGara();
				String nomeOperazione = this.getNomeOperazione(this.getOperazione());
				this.setDataInvio(retrieveDataInvio(nomeOperazione));
				this.progressivoLista = this.findProgressivoLista();
				
				this.recuperaTipoAnnullamento(buste);
				
				if (this.getDataInvio() != null) {

					Date dataTermineInvioOfferta = SendBusteAction.getDataTermine(dettGara, this.getOperazione());
					boolean controlliOk = true;

					if (dataTermineInvioOfferta != null && this.getDataInvio().compareTo(dataTermineInvioOfferta) > 0) {
						controlliOk = false;
						this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo", new String[] {nomeOperazione}));
						
						// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
						Event evento = new Event();
						evento.setUsername(this.getCurrentUser().getUsername());
						evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
						evento.setLevel(Event.Level.ERROR);
						evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
						evento.setIpAddress(this.getCurrentUser().getIpAddress());
						evento.setSessionId(this.getRequest().getSession().getId());
						evento.setMessage("Accesso alla funzione " + "Rettifica offerte distinte");
						evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO 
								+ " (" + UtilityDate.convertiData(this.getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						this.eventManager.insertEvent(evento);
					}

					if (controlliOk) {
						List<String> stati = new ArrayList<String>();
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
						if("1".equals(this.fromListaOfferte)) {
							stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
						}
						
						BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
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
	 * esecuzione della conferma "rettifica" 
	 */
	public String rettifica() {
		this.setTarget("reopen");
		
		// verifica il profilo di accesso ed esegui un LOCK alla funzione 
		if( !lockAccessoFunzione(EFlussiAccessiDistinti.OFFERTA_GARA, this.codice) ) {
			return this.getTarget();
		}
		
		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						this.codice,
						this.progressivoOfferta,
						this.operazione);
			
				buste.get();

				// FASE 1: rettifica/annullamento/reset comunicazioni
				boolean invioMail = true;
				try {
	//				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.getCodice());				
					
					// imposta i parametri in baste alla gara e al tipo di partecipazione
					this.recuperaTipoAnnullamento(buste);
					
					invioMail = (this.rettifica);
			
					// verifica il metodo quale metodo utilizzare tra
					// - annullemento  mmetodo per resettare FS11/FS11R e ripristinare un'offerta derivante da una domanda di prequalifica
					// - eliminazione  metodo per eliminare anche FS11/FS11R di un'offerta non ancora "inviata"
					// - rettifica     metodo gia' esistente per la rettifica di un'offerta
					if(buste.isDomandaPartecipazione()) {
						// DOMANDE DI PARTECIPAZIONE
						if(this.eliminazione) {
							// eliminazione
							// elimina tutte FS10, FS10R, FS10A
							this.eliminazioneDomanda(buste);
							
						} else if(this.rettifica) {
							// rettifica
							this.rettificaDomanda(buste);
						}	
					} else {
						// OFFERTE
						if(this.annullamento) {
							// annullamento 
							// resetta le FS11,FS11R 
							// elimina le FS11A, FS11B*, FS11C*
							this.annullamentoOfferta(buste);
							
						} else if(this.eliminazione) {
							// eliminazione
							// elimina tutte FS11, FS11R, FS11A, FS11B*, FS11C*
							this.eliminazioneOfferta(buste);
							
						} else if(this.rettifica) {
							// rettifica
							this.rettificaOfferta(buste);
						}
					}
				} catch (Throwable e) {
					this.inviataComunicazione = false;
					ApsSystemUtils.logThrowable(e, this, "rettifica");
					ExceptionUtils.manageExceptionError(e, this);
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
				
				
				// FASE 2: invio mail
				if(this.inviataComunicazione && invioMail) {
					Event evento = new Event();
					evento.setUsername(this.getUsername());
					evento.setDestination(this.getCodice());
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
					evento.setMessage("Invio mail ricevuta di conferma annullamento comunicazioni a "
									  + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					
					String nomeOperazione = this.getNomeOperazione(this.getOperazione());
					
					try {
						Date data = buste.getBustaPartecipazione().getComunicazioneFlusso().getDettaglioComunicazione().getDataAggiornamentoStato().getTime();
						this.sendMailConfermaImpresa(data);
					} catch (Throwable t) {
						ApsSystemUtils.getLogger().error(
								"Per errori durante la connessione al server di posta, non e'' stato possibile inviare all''impresa {} "
								+ "la ricevuta della richiesta di rettifica dell'offerta.",
								new Object[] {this.getUsername()});
						this.setMsgErrore(this.getText("Errors.rettificaBuste.sendMailError", new String[] {nomeOperazione}));
						ApsSystemUtils.logThrowable(t, this, "rettifica");
						ExceptionUtils.manageExceptionError(t, this);
						evento.setError(t);
					} finally {
						this.eventManager.insertEvent(evento);
					}
				}
				
				
				// FASE 3: chiusura stato operazione
				if (this.inviataComunicazione) {
					this.setTarget("successPage");
				} else if (!this.richiestafuoriTempo) {
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
		
		unlockAccessoFunzione();
		
		return this.getTarget(); 
	}

}