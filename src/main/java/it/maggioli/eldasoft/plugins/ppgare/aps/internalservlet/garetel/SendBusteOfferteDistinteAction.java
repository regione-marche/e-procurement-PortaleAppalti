package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.*;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.JRPdfExporterEldasoft;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.utils.ProtocolsUtils;
import it.maggioli.eldasoft.ws.dm.*;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class SendBusteOfferteDistinteAction extends SendBusteAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3432218166978564415L;

	/**
	 * restituisce la descrizione del messaggio del log eventi del portale 
	 */
	protected String getDescTipoEvento() {
		return (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
				? "Presenta offerta buste distinte" 
				: "Presenta domanda di partecipazione buste distinte");
	}
	
	/**
	 * conferma ed invio
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
			this.setUsername(this.getCurrentUser().getUsername());

			GestioneBuste buste = GestioneBuste.getFromSession();
			BustaEconomica bustaEco = buste.getBustaEconomica();
			BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
			RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper();
			
			boolean invioOfferta = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
			boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);

			try {
				String nomeOperazione = GestioneBuste.getNomeOperazione(this.getOperazione());
				this.setDataInvio(retrieveDataInvio(this.getNtpManager(), this, nomeOperazione));
				boolean controlliOk = true;

				if (this.getDataInvio() != null) {
					DettaglioGaraType dettGara = buste.getDettaglioGara(true);
					
					//-----------------------------------------------------------------------
					// traccia l'evento di preinvio 
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(dettGara.getDatiGeneraliGara().getCodice());
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.CHECK_INVIO);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					//evento.setMessage("Controlli preinvio dati " + (domandaPartecipazione ? "domanda di partecipazione" : "richiesta di presentazione offerta"));
					evento.setMessage("Controlli preinvio dati " + "richiesta di presentazione offerta");
					
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
					
					boolean integrazioneEffettuata = false;
					if(controlliOk) {
						// TEST SU EVENTUALE INTEGRAZIONE DOCUMENTI DA BO
						integrazioneEffettuata = bustaRiepilogo.integraBusteFromBO();
					}

					// in caso di integrazione di documenti inseriti da BO => aggiorno la FS11R 
					if(integrazioneEffettuata) {
						bustaRiepilogo.send(bustaEco);
					}
					
					// controlla se esistono documenti senza contenuto nelle buste...
					// o se esistono documenti nelle buste, con dimensione del contenuto inviato 
					// diversa dall'originale...
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
					if(controlliOk) {
						this.setTarget(SUCCESS);
					}
				}

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "confirm");
				ExceptionUtils.manageExceptionError(t, this);
			} 
		}else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}	

		return this.getTarget();
	}
		
	/**
	 * invio  
	 */
	public String invio() {
		this.setTarget("reopen");

		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return this.getTarget();
		}

		this.setCodiceSistema((String) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA));		
		this.setTipoProtocollazione(new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA));
		this.setMailUfficioProtocollo((String)this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI));
		this.setAllegaDocMailUfficioProtocollo((Boolean) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE));
		this.setStazioneAppaltanteProtocollante((String) this.getAppParamManager().getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE));
		String stazioneAppaltanteProcedura = null;
		byte[] fileRiepilogo = null;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			this.setUsername(this.getCurrentUser().getUsername());

			Event evento = new Event();

			WizardDatiImpresaHelper datiImpresa = null;
			DettaglioGaraType dettGara = null;
			String nomeOperazione = GestioneBuste.getNomeOperazione(this.getOperazione());
			boolean controlliOk = true;
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			
			GestioneBuste buste = GestioneBuste.getFromSession();
			BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
			BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
			BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
			BustaTecnica bustaTecnica = buste.getBustaTecnica();
			BustaEconomica bustaEconomica = buste.getBustaEconomica();
			WizardPartecipazioneHelper wizardPartecipazione = bustaPartecipazione.getHelper();
			
			boolean invioOfferta = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
			boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
			
			List<String> lottiAttivi = new ArrayList<String>();
			Iterator<String> lottiAttiviIterator = wizardPartecipazione.getLotti().iterator();
			while(lottiAttiviIterator.hasNext()){
				lottiAttivi.add(lottiAttiviIterator.next());
			}

			// FASE 1: invio delle comunicazioni
			try {
				dettGara = buste.getDettaglioGara(true);
				datiImpresa = buste.getImpresa();
				
				stazioneAppaltanteProcedura = dettGara.getStazioneAppaltante().getCodice();
				
				this.getAppParamManager().setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
				
				// NB: il tipoProtocollazione va inizializzato dopo la chiamata a setStazioneAppaltanteProtocollazione() !!! 
				this.setTipoProtocollazione( this.getAppParamManager().getTipoProtocollazione(stazioneAppaltanteProcedura) );
				
				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.getAppParamManager().isConfigWSDMNonDisponibile()) {
					controlliOk = false;
					this.setTarget(INPUT);
					String msgerr = this.getText("Errors.wsdm.configNotAvailable");
					this.addActionError(msgerr);
					Event event = new Event();
					event.setUsername(this.getCurrentUser().getUsername());
					event.setDestination(super.getCodice());
					event.setLevel(Event.Level.ERROR);
					event.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					event.setIpAddress(this.getCurrentUser().getIpAddress());
					event.setSessionId(this.getCurrentUser().getSessionId());
					event.setMessage("Configurazione non disponibile o vuota");
					event.setDetailMessage(msgerr);
					this.getEventManager().insertEvent(event);
				}
				
				// calcola la data di invio e verifica se fuori tempo massimo...
				if(controlliOk && isAccessoFuoriTempoMassimo(dettGara)) {
					controlliOk = false;
					this.addActionError(getText("Errors.invioRichiestaFuoriTempoMassimo", new String[] { nomeOperazione }));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}

				// verifica se la gara e' sospesa...
				if(controlliOk) {
					boolean sospesa = "4".equals(dettGara.getDatiGeneraliGara().getStato()); 
					if(sospesa) {
						controlliOk = false;
						evento.setDetailMessage(this.getText("Errors.invioBuste.proceduraSospesa"));
						evento.setLevel(Event.Level.ERROR);
						this.addActionError(this.getText("Errors.invioBuste.proceduraSospesa"));
						this.setTarget(INPUT);
					}
				}

				if(controlliOk) {
					if (this.getDataInvio() != null) {
					
						if(domandaPartecipazione) {
							// domanda di partecipazione
							if(bustaPrequalifica.getId() > 0) {
								controlliOk = controlliOk && bustaPrequalifica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							}
						}
						
						if( !dettGara.getDatiGeneraliGara().isNoBustaAmministrativa() ) {
							if(bustaAmministrativa != null) {
								if(bustaAmministrativa.getId() > 0) {
									bustaAmministrativa.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
							}
						}

						for(int i = 0; i < lottiAttivi.size(); i++) {
							if(bustaTecnica != null) {
								bustaTecnica.get(null, lottiAttivi.get(i));
								if(bustaTecnica.getId() > 0) {
									controlliOk = controlliOk && bustaTecnica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
							}

							if(bustaEconomica != null) {
								bustaEconomica.get(null, lottiAttivi.get(i));
								if(bustaEconomica.getId() > 0) {
									controlliOk = controlliOk && bustaEconomica.sendConfirm(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
								}
							}
						}
						
						bustaPartecipazione.get();
						if(controlliOk && bustaPartecipazione.getId() > 0) {
							bustaPartecipazione.getHelper().setDataPresentazione(this.getDataInvio());
							inviataComunicazione = bustaPartecipazione.sendConfirm(new FileInputStream(this.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH)));
						}
					}
				}
			} catch (ApsException e) {
				ApsSystemUtils.getLogger().error(
						"Per errori durante la connessione al server di posta, non e'' stato possibile inviare all''impresa {} la ricevuta della richiesta di {}.",
						new Object[] { this.getCurrentUser().getUsername(), nomeOperazione });
				this.setMsgErrore(this.getText("Errors.invioBuste.sendMailError",
								     		   new String[] { nomeOperazione }));
				ApsSystemUtils.logThrowable(e, this, "invio");
				ExceptionUtils.manageExceptionError(e, this);
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
			
			ComunicazioneType comunicazionePartecipazione = bustaPartecipazione.getComunicazioneFlusso().getComunicazione();
			String tipoComunicazione = bustaPartecipazione.getComunicazioneFlusso().getTipoComunicazione();

			// FASE 2: ove previsto, si invia alla protocollazione
			if (inviataComunicazione) {
				evento = new Event();
				evento.setUsername(this.getUsername());
				evento.setDestination(super.getCodice());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				switch (this.getTipoProtocollazione()) {

				// --- PROTOCOLLAZIONE MAIL ---
				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
					evento.setMessage("Protocollazione via mail a " + this.getMailUfficioProtocollo());

					boolean mailProtocollazioneInviata = false;

					try {
						String tipoRichiesta = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) 
								? " " + this.getI18nLabel("LABEL_OFFERTA") + " " 
							    : " " + this.getI18nLabel("LABEL_PARTECIPAZIONE");
								
						// si invia la richiesta di protocollazione via mail
						this.setMailRichiestaUfficioProtocollo(
								datiImpresa, 
								tipoComunicazione,
								tipoRichiesta, 
								dettGara.getDatiGeneraliGara().getOggetto(),
								UtilityDate.convertiData(this.getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						mailProtocollazioneInviata = true;
						this.getEventManager().insertEvent(evento);

						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + comunicazionePartecipazione.getDettaglioComunicazione().getId() + 
								" allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.getComunicazioniManager().updateStatoComunicazioni(
								new DettaglioComunicazioneType[] {comunicazionePartecipazione.getDettaglioComunicazione()}, 
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.getEventManager().insertEvent(evento);
						this.setDataProtocollo(UtilityDate.convertiData(this.getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						
					} catch (XmlException e) {
						ApsSystemUtils.logThrowable(e, this, "send", this.getText("Errors.sendProtocolloMailError"));
						this.addActionError(this.getText("Errors.sendProtocolloMailError"));
						ExceptionUtils.manageExceptionError(e, this);
						this.setTarget(INPUT);
						evento.setError(e);
						this.getEventManager().insertEvent(evento);
						
					} catch (Throwable t) {
						if (mailProtocollazioneInviata) {
							evento.setError(t);
							this.getEventManager().insertEvent(evento);

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							this.setMsgErrore(
									this.getText("Errors.updateStatoComunicazioneDaProcessare",
												 new String[] {comunicazionePartecipazione.getDettaglioComunicazione().getId().toString()}));
							ApsSystemUtils.logThrowable(t, this, "send", this.getMsgErrore());
							this.addActionError(this.getMsgErrore());

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage(
									"Aggiornare manualmente la comunicazione con id " + comunicazionePartecipazione.getDettaglioComunicazione().getId());
							this.getEventManager().insertEvent(evento);
						} else {
							evento.setError(t);
							this.getEventManager().insertEvent(evento);
							ApsSystemUtils.logThrowable(t, this, "send", this.getText("Errors.sendProtocolloConnectionError"));
							this.addActionError(this.getText("Errors.sendProtocolloConnectionError"));
							ExceptionUtils.manageExceptionError(t, this);
							this.setTarget(INPUT);
							protocollazioneOk = false;
						}
					}
					break;

				// --- PROTOCOLLAZIONE WSDM
				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
					evento.setMessage("Protocollazione via WSDM");

					boolean protocollazioneInoltrata = false;
					WSDocumentoType documento = null;
					WSDMProtocolloDocumentoType ris = null;
					long id = comunicazionePartecipazione.getDettaglioComunicazione().getId();
					try {
						BustaRiepilogo riepilogo = GestioneBuste.getBustaRiepilogoFromSession();
						RiepilogoBusteHelper bustaRiepilogativa = riepilogo.getHelper();

						FascicoloProtocolloType fascicoloBackOffice = this.getBandiManager().getFascicoloProtocollo(super.getCodice());
						WSDMLoginAttrType loginAttr = this.getWsdmManager().getLoginAttr();
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
								comunicazionePartecipazione,
								nomeOperazione,
								fascicoloBackOffice,
								fileRiepilogo);

						ris = this.getWsdmManager().inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						this.setAnnoProtocollo(ris.getAnnoProtocollo());
						this.setNumeroProtocollo(ris.getNumeroProtocollo());
						if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(this.getCodiceSistema())) {
							this.setNumeroProtocollo(ris.getGenericS11());
						}
						if (ris.getDataProtocollo() != null) {
							this.setDataProtocollo(UtilityDate.convertiData(ris.getDataProtocollo().getTime(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						}
						protocollazioneInoltrata = true;
						this.getEventManager().insertEvent(evento);

						// si aggiorna lo stato a 5 aggiornando inoltre anche i
						// dati di protocollazione
						documento = new WSDocumentoType();
						documento.setEntita("GARE");
						documento.setChiave1(super.getCodice());
						documento.setNumeroDocumento(ris.getNumeroDocumento());
						documento.setAnnoProtocollo(ris.getAnnoProtocollo());
						documento.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + id +
								" allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE +
								", protocollata con anno " + ris.getAnnoProtocollo() + 
								" e numero " + ris.getNumeroProtocollo());
						
						// LAPISOPERA: inserisci l'ID richiesta restituito dal protocollo nella W_INVCOM e aggiorna il messaggio dell'evento
						ProcessPageProtocollazioneAction.lapisAggiornaComunicazione(
								ris,
								documento,
								comunicazionePartecipazione.getDettaglioComunicazione().getId(),
								this.getAppParamManager(),
								evento);

						this.getComunicazioniManager().protocollaComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								id,
								ris.getNumeroProtocollo(),
								(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.getDataInvio()),
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
								documento);
						
						this.getEventManager().insertEvent(evento);
					} catch (Throwable t) {
						if (protocollazioneInoltrata) {
							evento.setError(t);
							this.getEventManager().insertEvent(evento);

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							this.setMsgErrore(this.getText("Errors.updateStatoComunicazioneDaProcessare",
														   new String[] { String.valueOf(id) }));
							ApsSystemUtils.logThrowable(t, this, "invio", this.getMsgErrore());
							this.addActionError(this.getMsgErrore());

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage(
									"Aggiornare manualmente la comunicazione con id " + id +
									" ed inserire inoltre un documento in ingresso per entità " + documento.getEntita() +
									", chiave1 " + documento.getChiave1() +
									", oggetto " + documento.getOggetto() +
									", numero documento " + documento.getNumeroDocumento() + 
									", anno protocollo " + documento.getAnnoProtocollo() +
									" e numero protocollo " + documento.getNumeroProtocollo());
							ProcessPageProtocollazioneAction.lapisAggiornaMessaggioEventoCompletamentoManuale(
									ris,
									documento,
									comunicazionePartecipazione.getDettaglioComunicazione().getId(),
									this.getAppParamManager(),
									evento);
							evento.resetDetailMessage();
							this.getEventManager().insertEvent(evento);
						} else {
							evento.setError(t);
							this.getEventManager().insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, 
									"invio",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							this.setMsgErrore(this.getText("Errors.service.wsdmHandshake"));
							ExceptionUtils.manageWSDMExceptionError(t, this);
							this.setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di
							// protocollare riponendo la comunicazione in bozza
							this.annullaComunicazioneInviata(comunicazionePartecipazione);
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
				evento = new Event();
				evento.setUsername(this.getUsername());
				evento.setDestination(super.getCodice());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage(
						"Invio mail ricevuta di conferma trasmissione comunicazione " + 
						comunicazionePartecipazione.getDettaglioComunicazione().getTipoComunicazione() +
						" a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				try {
					this.sendMailConfermaImpresa(
							datiImpresa, 
							dettGara.getDatiGeneraliGara().getOggetto());
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di invio offerta per la gara {}.",
							new Object[] { this.getCurrentUser().getUsername(), super.getCodice() });
					this.setMsgErrore(this.getText("Errors.invioBuste.sendMailError", new String[] { nomeOperazione }));
					ApsSystemUtils.logThrowable(t, this, "invio");
					evento.setError(t);
				} finally {
					this.getEventManager().insertEvent(evento);
				}
			}

			// FASE 4: se invio e protocollazione sono andate a buon fine, anche
			// se la ricevuta all'impresa non e' stata inviata, 
			// si procede con la pulizia della sessione
			if (inviataComunicazione && protocollazioneOk) {
				// pulizia e impostazione navigazione futura
				// se tutto e' andato a buon fine si eliminano
				// le informazioni dalla sessione ...
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				buste.resetSession();
				this.setTarget("successPage");
			}

		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		// concludi la protocollazione
		this.getAppParamManager().setStazioneAppaltanteProtocollazione(null);

		unlockAccessoFunzione();
		
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
		Integer cfMittente = (Integer) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE);
		boolean usaCodiceFiscaleMittente = (cfMittente != null ? 1 == cfMittente : true);
		String channelCode = (String) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE);
		String sottoTipo = (String)this.getAppParamManager()
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
			// si legge la classifica presa dalla configurazione solo se non
			// esiste il fascicolo
			classificaFascicolo = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);
						
			// GARE, ODA
			idTitolazione = (String)this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TITOLAZIONE);
		}
		
		idUnitaOperDestinataria = (String)this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA è vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}

		// GARE, ODA
		idIndice = (String)this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);

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

		boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		String tipoDocumentoPrequalifica = (String) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO_PREQUALIFICA);
		String tipoDocumento = (String) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO);
		if(domandaPartecipazione && !(tipoDocumentoPrequalifica == null || "".equals(tipoDocumentoPrequalifica))){
			tipoDocumento = tipoDocumentoPrequalifica;
		}

		wsdmProtocolloDocumentoIn.setTipoDocumento(tipoDocumento);
		Calendar data = new GregorianCalendar();
		data.setTimeInMillis(this.getDataInvio().getTime());
		wsdmProtocolloDocumentoIn.setData(data);
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String oggetto = null;
		if (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			oggetto = MessageFormat.format(this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_OFFERTA"),
					   new Object[] {ragioneSociale200, codiceFiscale, dettGara.getDatiGeneraliGara().getOggetto(), dettGara.getDatiGeneraliGara().getCodice()});
		} else if (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
			oggetto = MessageFormat.format(this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_PREQUALIFICA"),
					   new Object[] {ragioneSociale200, codiceFiscale, dettGara.getDatiGeneraliGara().getOggetto(), dettGara.getDatiGeneraliGara().getCodice()});
		}
		
		// serve per FOLIUM e solo in caso di offerta/partecipazione
		if (IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			oggetto = StringUtils.left(oggetto, 250);
		} 
		
		if(IWSDMManager.CODICE_SISTEMA_ENGINEERINGDOC.equals(codiceSistema)) {
			oggetto = this.getCodice() + " - " + oggetto;
		}
		
		if(IWSDMManager.CODICE_SISTEMA_LAPIS.equals(codiceSistema)) {
			ragioneSociale = StringUtils.left(ragioneSociale, 100); 
		}
		
		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		wsdmProtocolloDocumentoIn.setDescrizione(comunicazionePartecipazione.getDettaglioComunicazione().getTesto());
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		String codiceRegistro = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CODICE_REGISTRO);
		wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceRegistro);
		
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId());
		
		// servono per Archiflow / FOLIUM
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(dettGara.getStazioneAppaltante().getCodice());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + dettGara.getDatiGeneraliGara().getCodice());
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(dettGara.getDatiGeneraliGara().getCodice());
		}
		wsdmProtocolloDocumentoIn.setCig(this.getBandiManager().getCigBando(dettGara.getDatiGeneraliGara().getCodice()));
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(dettGara.getStazioneAppaltante().getCodice());
		wsdmProtocolloDocumentoIn.setMezzo((String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));
		
		// serve per JIride*
		if(riservatezzaFascicolo) {
			// BO: riservatezza = SI
			String livelloRiservatezza = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_LIVELLO_RISERVATEZZA);
			
			if (StringUtils.isNotEmpty(livelloRiservatezza)) {
				// se ISRISERVA == SI
				// allora si invia la property nel campo gia' valorizzato 
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
			mittenti[0].setCognomeointestazione(ragioneSociale200);
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

			wsdmProtocolloDocumentoIn.setFascicolo(fascicolo);
		} else {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
		}
		
		// Allegati: un file per ogni busta, contenente gli SHA1 di ogni file
		// caricato nella busta

		// prepara 1+N allegati...
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// ...verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		String v = (String) this.getAppParamManager()
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
		boolean inTesta = (v != null && "1".equals(v));
		
		WSDMProtocolloAllegatoType[] allegati = createAttachments(datiImpresaHelper, dettGara, bustaRiepilogativa,
				comunicazionePartecipazione, nomeOperazione, fileRiepilogo, ragioneSociale, codiceFiscale, indirizzo,
				inTesta);
		
		wsdmProtocolloDocumentoIn.setAllegati(allegati);
		
		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)){
			wsdmProtocolloDocumentoIn.setOggetto(
					dettGara.getDatiGeneraliGara().getCodice() 
					+ "-" + wsdmProtocolloDocumentoIn.getOggetto()
					+ "-" + dettGara.getDatiGeneraliGara().getOggetto());
		}
	    Calendar dataArrivo = Calendar.getInstance();
	    dataArrivo.setTimeInMillis(System.currentTimeMillis());
	    wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(2));		
	    wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
	    wsdmProtocolloDocumentoIn.setSupporto((String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));
	    
	    // solo per le procedure di gara, se specificata in configurazione
	    // uso PROTOCOLLAZIONE_WSDM_GARE_STRUTTURA (JPROTOCOL) altrimenti 
	    // uso PROTOCOLLAZIONE_WSDM_STRUTTURA
	    String struttura = (String) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_STRUTTURA);
		if (StringUtils.isEmpty(struttura)) {
			struttura = (String) this.getAppParamManager()
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
		
		String tipoAssegnazione = (String) this.getAppParamManager()
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

		String titolo = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_OGGETTO"),
				this.getCodice(), nomeOperazione
		);
		
		// PDF-A
		boolean isActiveFunctionPdfA; 
		try {
			isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A");
		} catch (Exception ex) {
			throw new ApsException(ex.getMessage(),ex);
		}
		
		// PDF-A ICC path
		InputStream iccFilePath = new FileInputStream(getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
		
		// prepara il testo per "comunicazione.pdf"
		String comunicazioneTxt = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_PROTOCOLLO_TESTOCONALLEGATI"),
				ragioneSociale,
				codiceFiscale,
				datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA(),
				(String) ((IUserProfile) getCurrentUser().getProfile()).getValue("email"),
				indirizzo,
				UtilityDate.convertiData(getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
				(getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
						? getI18nLabelFromDefaultLocale("LABEL_OFFERTA")
						: getI18nLabelFromDefaultLocale("LABEL_PARTECIPAZIONE")),
				dettGara.getDatiGeneraliGara().getOggetto()
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
		if(inTesta) {
			i = 1;
		}

		// aggiungi l'allegato "Riepilogo_buste.pdf"
		//
		boolean hasMarcaturaTemporale = false;
		try{
			hasMarcaturaTemporale = this.customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE");
		}catch (Exception e) {
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
	 * invia la mail di notifica all'ufficio del protocollo
	 */
	private void setMailRichiestaUfficioProtocollo(
			WizardDatiImpresaHelper datiImpresa,
			String tipoComunicazione, 
			String tipoRichiesta, 
			String descBando, 
			String data) 
		throws ApsSystemException, ApsException, XmlException
	{
		if (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL == this.getTipoProtocollazione()) {
			if (StringUtils.isBlank(this.getMailUfficioProtocollo())) {
				throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}
			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio
			Map<String, byte[]> p = new HashMap<>();

			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.getAllegaDocMailUfficioProtocollo()) {
				BustaRiepilogo riepilogo = GestioneBuste.getBustaRiepilogoFromSession();
				RiepilogoBusteHelper bustaRiepilogativa = riepilogo.getHelper();

				String bustaPreQualifica = riepilogo.getDigestFileBusta(bustaRiepilogativa.getBustaPrequalifica());
				if(bustaPreQualifica != null) {
					p.put("busta_prequalifica.sha1.txt", bustaPreQualifica.getBytes());
				}

				String bustaAmministrativa = riepilogo.getDigestFileBusta(bustaRiepilogativa.getBustaAmministrativa());
				if(bustaAmministrativa != null) {
					p.put("busta_amministrativa.sha1.txt", bustaAmministrativa.getBytes());
				}
				
				if(bustaRiepilogativa.getBusteTecnicheLotti() != null) {
					HashMap<String, RiepilogoBustaBean> busteTecniche = bustaRiepilogativa.getBusteTecnicheLotti();
					for(String codiceLotto : bustaRiepilogativa.getListaCompletaLotti()) {
						RiepilogoBustaBean bustaTecnicaLotto = busteTecniche.get(codiceLotto);
						if(bustaTecnicaLotto != null) {
							if(!bustaTecnicaLotto.getDocumentiInseriti().isEmpty()) {
								String bustaTecnicaLottoDigest = riepilogo.getDigestFileBusta(bustaTecnicaLotto);
								if(bustaTecnicaLottoDigest != null)
									p.put("busta_tecnica_lotto_" + codiceLotto + ".sha1.txt", bustaTecnicaLottoDigest.getBytes());
							}
						}
					}
				}

				if(bustaRiepilogativa.getBusteEconomicheLotti() != null) {
					HashMap<String, RiepilogoBustaBean> busteEconomiche = bustaRiepilogativa.getBusteEconomicheLotti();
					for(String codiceLotto : bustaRiepilogativa.getListaCompletaLotti()) {
						RiepilogoBustaBean bustaEconomicaLotto = busteEconomiche.get(codiceLotto);
						if(bustaEconomicaLotto != null) {
							if(!bustaEconomicaLotto.getDocumentiInseriti().isEmpty()) {
								String bustaEconomicaLottoDigest = riepilogo.getDigestFileBusta(bustaEconomicaLotto);
								if(bustaEconomicaLottoDigest != null)
									p.put("busta_economica_lotto_" + codiceLotto + ".sha1.txt", bustaEconomicaLottoDigest.getBytes());
							}
						}
					}
				}
			}

			String ragioneSociale = datiImpresa.getDatiPrincipaliImpresa().getRagioneSociale();
			String codFiscale = datiImpresa.getDatiPrincipaliImpresa().getCodiceFiscale();
			String partitaIVA = datiImpresa.getDatiPrincipaliImpresa().getPartitaIVA();
			String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
			String sede = datiImpresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
					+ " "
					+ datiImpresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()
					+ ", "
					+ datiImpresa.getDatiPrincipaliImpresa().getCapSedeLegale()
					+ " "
					+ datiImpresa.getDatiPrincipaliImpresa().getComuneSedeLegale()
					+ " ("
					+ datiImpresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")";

			String[] destinatari = this.getMailUfficioProtocollo().split(",");

			String tipoOperazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
				? "offerta"
				: "partecipazione"
			);
			
			String subject = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_OGGETTO"),
						new Object[] {this.getCodice(), tipoRichiesta});
			
			String text = null;
			if (this.getAllegaDocMailUfficioProtocollo() && !p.isEmpty()) {
				// -- allegati
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_PROTOCOLLO_TESTOCONALLEGATI"),
						new Object[] {ragioneSociale, codFiscale, partitaIVA, mail, sede, data,
									  tipoOperazione, descBando});
			} else {
				// -- notifica
				text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_TESTO"),
						new Object[] {ragioneSociale, tipoRichiesta, descBando, data});
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

			this.getMailManager().sendMail(
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
	
}
