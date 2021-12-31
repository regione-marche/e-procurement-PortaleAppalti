package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.SaveWizardIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;

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
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			this.setUsername(this.getCurrentUser().getUsername());

			WizardDatiImpresaHelper datiImpresa = (WizardDatiImpresaHelper) this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper)this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);

			try {
				String nomeOperazione = WizardDocumentiBustaHelper
						.retrieveNomeOperazione(this.getOperazione());
				this.setDataInvio(retrieveDataInvio(this.getNtpManager(), this,
						nomeOperazione));
				boolean controlliOk = true;

				if (this.getDataInvio() != null) {
					DettaglioGaraType dettGara = (DettaglioGaraType)this.getSession().get("dettaglioGara");

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
					
					Date dataTermine = getDataTermine(dettGara, this.getOperazione());

					//-----------------------------------------------------------------------
					// traccia i tentativi di accesso alle funzionalita' fuori tempo massimo
					if (dataTermine != null && this.getDataInvio().compareTo(dataTermine) > 0) {
						controlliOk = false;
						this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo",
											new String[] { nomeOperazione }));
						
						Event fuoriTempo = new Event();
						fuoriTempo.setUsername(this.getCurrentUser().getUsername());
						fuoriTempo.setDestination(dettGara.getDatiGeneraliGara().getCodice());
						fuoriTempo.setLevel(Event.Level.ERROR);
						fuoriTempo.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
						fuoriTempo.setIpAddress(this.getCurrentUser().getIpAddress());
						fuoriTempo.setSessionId(this.getRequest().getSession().getId());
						fuoriTempo.setMessage("Accesso alla funzione " + this.getDescTipoEvento());
						fuoriTempo.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(this.getDataInvio(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						this.getEventManager().insertEvent(fuoriTempo);
					}

					boolean integrazioneEffettuata = false;

					if(controlliOk){
						// TEST SU EVENTUALE INTEGRAZIONE DOCUMENTI DA BO 

						boolean integrazioneBustaAmministrativa = bustaRiepilogativa.integraBustaAmministrativaFromBO(super.getCodice(), null, this.getBandiManager(), datiImpresa, partecipazioneHelper);
						integrazioneEffettuata = integrazioneEffettuata || integrazioneBustaAmministrativa;

						for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size();i++){
							String lottoCorrente = bustaRiepilogativa.getListaCompletaLotti().get(i);
							
							// BUSTE TECNICHE
							RiepilogoBustaBean bustaTecnicaLotto = bustaRiepilogativa.getBusteTecnicheLotti().get(lottoCorrente);

							// per ogni lotto controllo se esiste la busta tecnica
							if(bustaTecnicaLotto!= null){
								integrazioneEffettuata = integrazioneEffettuata || bustaRiepilogativa.integraBustaTecnicaFromBO(bustaTecnicaLotto, this.getBandiManager(),super.getCodice(), lottoCorrente, datiImpresa, partecipazioneHelper);
							}

							// BUSTE ECONOMICHE 
							RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(lottoCorrente);
							
							// ricalcolo gli eventuali documenti mancanti
							if(bustaEconomicaLotto != null) {
								integrazioneEffettuata = integrazioneEffettuata || bustaRiepilogativa.integraBustaEconomicaFromBO(bustaEconomicaLotto, this.getBandiManager(), super.getCodice(), lottoCorrente , datiImpresa, partecipazioneHelper);
							}
						}
					}

					// CONTROLLO DOCUMENTAZIONE LOTTI
					boolean documentiAmministrativaMancanti = false;
					for(int i = 0; i < bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().size() && !documentiAmministrativaMancanti;i++){
						documentiAmministrativaMancanti = documentiAmministrativaMancanti || bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().get(i).isObbligatorio();
					}

					// LOTTI BUSTE TECNICHE
					boolean documentiTecnicheMancanti = false;
					for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size() && !documentiTecnicheMancanti; i++) {
						String lottoCorrente = bustaRiepilogativa.getListaCompletaLotti().get(i);
						RiepilogoBustaBean bustaTecnicaLotto = bustaRiepilogativa.getBusteTecnicheLotti().get(lottoCorrente);
						if(bustaTecnicaLotto != null) {
							for(int j = 0; j < bustaTecnicaLotto.getDocumentiMancanti().size(); j++){
								documentiTecnicheMancanti = (documentiTecnicheMancanti || bustaTecnicaLotto.getDocumentiMancanti().get(j).isObbligatorio());
								if(!bustaRiepilogativa.getPrimoAccessoTecnicheEffettuato().get(lottoCorrente)){
									documentiTecnicheMancanti = true;
								}
							}
						}
					}

					// LOTTI BUSTE ECONOMICHE
					boolean documentiEconomicheMancanti = false;
					for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size() && !documentiEconomicheMancanti; i++) {
						String lottoCorrente = bustaRiepilogativa.getListaCompletaLotti().get(i);
						RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(lottoCorrente);
						if(bustaEconomicaLotto != null) {
							for(int j = 0; j < bustaEconomicaLotto.getDocumentiMancanti().size(); j++) {
								documentiEconomicheMancanti = (documentiEconomicheMancanti || bustaEconomicaLotto.getDocumentiMancanti().get(j).isObbligatorio()); 
								if(! bustaRiepilogativa.getPrimoAccessoEconomicheEffettuato().get(lottoCorrente)){
									documentiEconomicheMancanti = true;
								}
							}
						}
					}

					// in caso di integrazione di documenti inseriti da BO => aggiorno la FS11R 
					if(integrazioneEffettuata) {
						bustaRiepilogativa.sendComunicazioneBusta(
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
								datiImpresa, 
								this.getCurrentUser().getUsername(),  
								super.getCodice(),
								datiImpresa.getDatiPrincipaliImpresa().getRagioneSociale(), 
								this.getComunicazioniManager(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO,
								this);
					}

					if(documentiAmministrativaMancanti) {
						this.addActionError(this.getText("Errors.invioBuste.dataNotSent",
											new String[] { PortGareSystemConstants.BUSTA_AMM }));
						controlliOk = false;
					}else if(documentiTecnicheMancanti) {
						this.addActionError(this.getText("Errors.invioBusteLotti.dataNotSent",
											new String[] { "Buste tecniche" }));
						controlliOk = false;
					}else if(documentiEconomicheMancanti) {
						this.addActionError(this.getText("Errors.invioBusteLotti.dataNotSent",
											new String[] { "Buste economiche" }));
						controlliOk = false;
					}
					
					if(!controlliOk) {
						String documentiMancanti = this.getListaDocumentiMancanti(bustaRiepilogativa, false);
						if(StringUtils.isNotEmpty(documentiMancanti)) {
							evento.setDetailMessage(documentiMancanti);
							evento.setLevel(Event.Level.ERROR);
						}
					}

					// controlla se i lotti dell'offerta sono utilizzati anche in altre offerte...
					if(controlliOk) {
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
					VerificaDocumentiCorrotti documentiCorrotti = new VerificaDocumentiCorrotti(bustaRiepilogativa, this.getBandiManager());
					if (controlliOk) {
//						if(domandaPartecipazione) {
//						} else {
						controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
								dettGara.getDatiGeneraliGara().getCodice(), 
								this.getCurrentUser().getUsername(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
								bustaRiepilogativa.getBustaAmministrativa());
						
						controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
								dettGara.getDatiGeneraliGara().getCodice(), 
								this.getCurrentUser().getUsername(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
								bustaRiepilogativa.getBusteTecnicheLotti());
						
						controlliOk = controlliOk & !documentiCorrotti.isDocumentiCorrottiPresenti(
								dettGara.getDatiGeneraliGara().getCodice(), 
								this.getCurrentUser().getUsername(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
								bustaRiepilogativa.getBusteEconomicheLotti());

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
					if(controlliOk){
						this.setTarget(SUCCESS);
					}
				}

			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "confirm");
				ExceptionUtils.manageExceptionError(e, this);
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

		this.setTipoProtocollazione((Integer) this.getAppParamManager()
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_TIPO));
		if (this.getTipoProtocollazione() == null) {
			this.setTipoProtocollazione(new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA));
		}
		this.setMailUfficioProtocollo((String)this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI));
		this.setAllegaDocMailUfficioProtocollo((Boolean) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE));
		this.setStazioneAppaltanteProtocollante((String) this.getAppParamManager().getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE));
		String stazioneAppaltanteProcedura = null;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			this.setUsername(this.getCurrentUser().getUsername());

			Event evento = new Event();

			WizardDatiImpresaHelper datiImpresa = null;
			DettaglioGaraType dettGara = null;
			String nomeOperazione = "";
			boolean controlliOk = true;
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			ComunicazioneType comunicazioneBustaAmministrativa = null;
			DettaglioComunicazioneType comunicazioniTec = null;
			DettaglioComunicazioneType comunicazioniEco = null;

			ComunicazioneType comunicazioneBustaTecnica = null;
			ComunicazioneType comunicazioneBustaEconomica = null;

			ComunicazioneType comunicazionePartecipazione = null;
			WizardPartecipazioneHelper wizardPartecipazione = (WizardPartecipazioneHelper)this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			List<String> lottiAttivi = new ArrayList<String>();
			Iterator<String> lottiAttiviIterator = wizardPartecipazione.getLotti().iterator();
			while(lottiAttiviIterator.hasNext()){
				lottiAttivi.add(lottiAttiviIterator.next());
			}

			String tipoComunicazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) 
					? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
					: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;

			// FASE 1: invio delle comunicazioni
			try {
				dettGara = (DettaglioGaraType) this.getSession().get("dettaglioGara");
				stazioneAppaltanteProcedura = dettGara.getStazioneAppaltante().getCodice();
				this.getAppParamManager().setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
				
				if(!this.getAppParamManager().isConfigStazioneAppaltantePresente()) {
					// se la configurazione WSDM della stazione appaltante 
					// non e' presente, resetta il tipo protocollazione
					this.setTipoProtocollazione( new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) );
				}
				
				if (!SaveWizardIscrizioneAction.isProtocollazionePrevista(
						this.getTipoProtocollazione(), 
						this.getStazioneAppaltanteProtocollante(), 
						stazioneAppaltanteProcedura)) {
					// se la protocollazione non e' prevista, si resetta il tipo protocollazione cosi' in seguito il codice testa su un'unica condizione
					this.setTipoProtocollazione(new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA));
				}
				nomeOperazione = WizardDocumentiBustaHelper.retrieveNomeOperazione(this.getOperazione());
				this.setDataInvio(retrieveDataInvio(this.getNtpManager(), this, nomeOperazione));
				
				if (this.getDataInvio() != null) {

					datiImpresa = (WizardDatiImpresaHelper) this.getSession()
							.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);

					if (controlliOk) {

						// verifico che ci siano i documenti richiesti per la
						// busta amministrativa
						DettaglioComunicazioneType dettComunicazioneBustaAmm = ComunicazioniUtilities
								.retrieveComunicazione(
										this.getComunicazioniManager(),
										this.getCurrentUser().getUsername(),
										super.getCodice(),
										this.getProgressivoOfferta(),
										CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA);

						if (dettComunicazioneBustaAmm != null) {

							byte[] chiavePubblica = this.getBandiManager().getChiavePubblica(super.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA);
							comunicazioneBustaAmministrativa = this.getComunicazioniManager()
									.getComunicazione(
											CommonSystemConstants.ID_APPLICATIVO,
											dettComunicazioneBustaAmm.getId());

							controlliOk = controlliOk
									&& aggiornaComunicazioneBusta(
											comunicazioneBustaAmministrativa,
											dettComunicazioneBustaAmm,
											PortGareSystemConstants.BUSTA_AMM,
											chiavePubblica);

							if(controlliOk){
								if (comunicazioneBustaAmministrativa != null) {
									evento = ComunicazioniUtilities
											.createEventAggiornaStatoComunicazione(
													this.getUsername(), super.getCodice(),
													comunicazioneBustaAmministrativa,
													this.getCurrentUser().getIpAddress(),
													this.getRequest().getSession().getId());
									try {
										if(comunicazioneBustaAmministrativa.getDettaglioComunicazione().getSessionKey()!=null){
											this.getComunicazioniManager().updateSessionKeyComunicazione(comunicazioneBustaAmministrativa.getDettaglioComunicazione().getApplicativo(), comunicazioneBustaAmministrativa.getDettaglioComunicazione().getId(), comunicazioneBustaAmministrativa.getDettaglioComunicazione().getSessionKey(), CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
										}else{
											this.getComunicazioniManager().updateStatoComunicazioni(new DettaglioComunicazioneType[] {comunicazioneBustaAmministrativa.getDettaglioComunicazione()}, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
										}
										this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
									} catch (ApsException e) {
										evento.setError(e);
										throw e;
									} finally {
										evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
										this.getEventManager().insertEvent(evento);
									}
								}
							}

						}

						for(int i = 0; i < lottiAttivi.size();i++){
							// reset variabili a inizio ciclo
							comunicazioneBustaTecnica = null;
							comunicazioneBustaEconomica = null;

							// ---------- BUSTE TECNICHE ----------

							comunicazioniTec = ComunicazioniUtilities.retrieveComunicazione(
									this.getComunicazioniManager(),
									this.getCurrentUser().getUsername(),
									lottiAttivi.get(i),
									this.getProgressivoOfferta(),
									CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);

							if (comunicazioniTec != null) {
								byte[] chiavePubblica = this.getBandiManager().getChiavePubblica(super.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
								comunicazioneBustaTecnica = this.getComunicazioniManager()
										.getComunicazione(
												CommonSystemConstants.ID_APPLICATIVO,
												comunicazioniTec.getId());
								controlliOk = controlliOk
										&& aggiornaComunicazioneBusta(
												comunicazioneBustaTecnica,
												comunicazioniTec,
												PortGareSystemConstants.BUSTA_TEC,
												chiavePubblica);
							}

							// ---------- BUSTE ECONOMICHE ---------- 

							comunicazioniEco = ComunicazioniUtilities.retrieveComunicazione(
									this.getComunicazioniManager(),
									this.getCurrentUser().getUsername(),
									lottiAttivi.get(i),
									this.getProgressivoOfferta(),
									CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);

							if (comunicazioniEco != null) {
								byte[] chiavePubblica = this.getBandiManager().getChiavePubblica(super.getCodice(), PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
								comunicazioneBustaEconomica = this.getComunicazioniManager()
										.getComunicazione(
												CommonSystemConstants.ID_APPLICATIVO,
												comunicazioniEco.getId());
								controlliOk = controlliOk
										&& aggiornaComunicazioneBusta(
												comunicazioneBustaEconomica,
												comunicazioniEco,
												PortGareSystemConstants.BUSTA_ECO,
												chiavePubblica);
							}


							if (controlliOk) {

								if (comunicazioneBustaTecnica != null) {
									evento = ComunicazioniUtilities
											.createEventAggiornaStatoComunicazione(
													this.getUsername(), lottiAttivi.get(i),
													comunicazioneBustaTecnica,
													this.getCurrentUser().getIpAddress(),
													this.getRequest().getSession().getId());
									try {
										if(comunicazioneBustaTecnica.getDettaglioComunicazione().getSessionKey()!=null){
											this.getComunicazioniManager().updateSessionKeyComunicazione(comunicazioneBustaTecnica.getDettaglioComunicazione().getApplicativo(), comunicazioneBustaTecnica.getDettaglioComunicazione().getId(), comunicazioneBustaTecnica.getDettaglioComunicazione().getSessionKey(), CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
										}else{
											this.getComunicazioniManager().updateStatoComunicazioni(new DettaglioComunicazioneType[] {comunicazioneBustaTecnica.getDettaglioComunicazione()}, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
										}
										this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
									} catch (ApsException e) {
										evento.setError(e);
										throw e;
									} finally {
										evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
										this.getEventManager().insertEvent(evento);
									}
								}
								if (comunicazioneBustaEconomica != null) {
									evento = ComunicazioniUtilities
											.createEventAggiornaStatoComunicazione(
													this.getUsername(), 
													lottiAttivi.get(i),
													comunicazioneBustaEconomica,
													this.getCurrentUser().getIpAddress(),
													this.getRequest().getSession().getId());
									try {
										if(comunicazioneBustaEconomica.getDettaglioComunicazione().getSessionKey()!=null){
											this.getComunicazioniManager().updateSessionKeyComunicazione(comunicazioneBustaEconomica.getDettaglioComunicazione().getApplicativo(), comunicazioneBustaEconomica.getDettaglioComunicazione().getId(), comunicazioneBustaEconomica.getDettaglioComunicazione().getSessionKey(), CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
										}else{
											this.getComunicazioniManager().updateStatoComunicazioni(new DettaglioComunicazioneType[] {comunicazioneBustaEconomica.getDettaglioComunicazione()}, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
										}
										this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
									} catch (ApsException e) {
										evento.setError(e);
										throw e;
									} finally {
										evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
										this.getEventManager().insertEvent(evento);
									}
								}	
							}
						}


						String tipoComunicazionePartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) 
								? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
								: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
						
						DettaglioComunicazioneType dettComunicazionePartecipazione = ComunicazioniUtilities
								.retrieveComunicazione(
										this.getComunicazioniManager(),
										this.getCurrentUser().getUsername(),
										super.getCodice(),
										super.getProgressivoOfferta(),
										CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
										tipoComunicazionePartecipazione);
						if (dettComunicazionePartecipazione == null) {
							dettComunicazionePartecipazione = ComunicazioniUtilities
									.retrieveComunicazione(
											this.getComunicazioniManager(),
											this.getCurrentUser().getUsername(),
											super.getCodice(),
											super.getProgressivoOfferta(),
											CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
											tipoComunicazionePartecipazione);
						}

						if (dettComunicazionePartecipazione != null) {
							dettComunicazionePartecipazione
							.setDataPubblicazione(this.getDataInvio());
							comunicazionePartecipazione = this.getComunicazioniManager()
									.getComunicazione(
											CommonSystemConstants.ID_APPLICATIVO,
											dettComunicazionePartecipazione
											.getId());
							aggiornaComunicazionePartecipazione(
									comunicazionePartecipazione,
									dettComunicazionePartecipazione);
							// l'aggiornamento stato avviene in questa posizione
							// altrimenti con aggiornaComunicazionePartecipazione sovrascrivo
							// lo stato con quello della comunicazione

							dettComunicazionePartecipazione
							.setStato((this.getTipoProtocollazione().intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) || 
									  (this.getTipoProtocollazione().intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL)  
									  ? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE
									  : CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);

							evento = ComunicazioniUtilities
									.createEventSendComunicazione(
											this.getUsername(), 
											super.getCodice(),
											comunicazionePartecipazione,
											this.getDataInvio(),
											this.getCurrentUser().getIpAddress(),
											this.getRequest().getSession().getId());
							try {
								this.getComunicazioniManager().sendComunicazione(comunicazionePartecipazione);
								inviataComunicazione = true;
							} catch (Throwable e) {
								evento.setError(e);
								throw e;
							} finally {
								this.getEventManager().insertEvent(evento);
							}
						}
					}
				}
			} catch (ApsException e) {
				ApsSystemUtils.getLogger().error(
						"Per errori durante la connessione al server di posta, non e'' stato possibile inviare all''impresa {} la ricevuta della richiesta di {}.",
						new Object[] { this.getCurrentUser().getUsername(), nomeOperazione });
				this.setMsgErrore(
						this.getText("Errors.invioBuste.sendMailError",
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
								tipoComunicazione,tipoRichiesta, 
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
					long id = comunicazionePartecipazione.getDettaglioComunicazione().getId();
					try {
						RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.getSession()
							.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);

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
								fascicoloBackOffice);

						WSDMProtocolloDocumentoType ris = this.getWsdmManager()
								.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						this.setAnnoProtocollo(ris.getAnnoProtocollo());
						this.setNumeroProtocollo(ris.getNumeroProtocollo());
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
						documento
						.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn
								.getOggetto());

						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
						evento.setMessage(
								"Aggiornamento comunicazione con id " + id +
								" allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE +
								", protocollata con anno " + ris.getAnnoProtocollo() + 
								" e numero " + ris.getNumeroProtocollo());
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
							evento.resetDetailMessage();
							this.getEventManager().insertEvent(evento);
						} else {
							evento.setError(t);
							this.getEventManager().insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, 
									"invio",
									"Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							ExceptionUtils.manageExceptionError(t, this);
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
			// se la ricevuta
			// all'impresa non e' stata inviata, si procede con la pulizia della
			// sessione
			if (inviataComunicazione && protocollazioneOk) {
				// pulizia e impostazione navigazione futura
				// se tutto e' andato a buon fine si eliminano
				// le informazioni dalla sessione ...
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);				
				// rimosso dalla sessione SESSION_ID_OFFERTA_ECONOMICA in modo tale che 
				// alla riapertura dopo annullamento integra/rettifica non mi trovi informazioni 
				// già valorizzate (es. importi prezzi unitari)
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
				
				this.setTarget("successPage");
			}

		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		// concludi la protocollazione
		this.getAppParamManager().setStazioneAppaltanteProtocollazione(null);

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
		
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS11(sottoTipo);
			wsdmProtocolloDocumentoIn.setGenericS12(rup);
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
		    }
			wsdmProtocolloDocumentoIn.setFascicolo(fascicolo);
		} else {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
		}
		
		// Allegati: un file per ogni busta, contenente gli SHA1 di ogni file
		// caricato nella busta
		byte[] bustaPreQualifica = getDigestFileBusta(bustaRiepilogativa.getBustaPrequalifica());
		byte[] bustaAmministrativa = getDigestFileBusta(bustaRiepilogativa.getBustaAmministrativa());
		List<byte[]> digestBusteTecniche = new ArrayList<byte[]>();
		List<byte[]> digestBusteEconomiche = new ArrayList<byte[]>();
		
		int numBusteValorizzate = 
			(bustaPreQualifica != null ? 1 : 0) +
			(bustaAmministrativa != null ? 1 : 0);

		/* ----- digest delle buste tecniche ----- */
		for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size(); i++) {
			RiepilogoBustaBean bustaTecnicaLotto = bustaRiepilogativa.getBusteTecnicheLotti().get(bustaRiepilogativa.getListaCompletaLotti().get(i)); 
			if (bustaTecnicaLotto != null) {
				byte[] bustaTecnica = getDigestFileBusta(bustaTecnicaLotto);
				if (bustaTecnica != null) {
					digestBusteTecniche.add(bustaTecnica);
					numBusteValorizzate++;
				} else {
					digestBusteTecniche.add(null); //mantenimento allineamento per indice
				}
			} else {
				digestBusteTecniche.add(null); //mantenimento allineamento per indice
			}
		}

		/* ----- digest delle buste economiche ----- */
		for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size(); i++) {
			RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(bustaRiepilogativa.getListaCompletaLotti().get(i));

			if (bustaEconomicaLotto != null) {
				byte[] bustaEconomica = getDigestFileBusta(bustaEconomicaLotto);
				if (bustaEconomica != null) {
					digestBusteEconomiche.add(bustaEconomica);
					numBusteValorizzate++;
				} else {
					digestBusteEconomiche.add(null); //mantenimento allineamento per indice
				}
			}else{
				digestBusteEconomiche.add(null); //mantenimento allineamento per indice
			}
		}

		// prepara 1+N allegati...
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// ...verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		boolean inTesta = false;		// default, inserisci in coda
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			Integer v = (Integer) this.getAppParamManager()
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
		allegati[n2].setTitolo(MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RICEVUTA_OGGETTO"),
				new Object[] { this.getCodice(), nomeOperazione }));
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
						(this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
								? this.getI18nLabelFromDefaultLocale("LABEL_OFFERTA") 
								: this.getI18nLabelFromDefaultLocale("LABEL_PARTECIPAZIONE")),
						dettGara.getDatiGeneraliGara().getOggetto() 
				});
		byte[] contenutoPdf = UtilityStringhe.string2Pdf(contenuto);
		allegati[n2].setContenuto(contenutoPdf);
		// serve per Titulus
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazionePartecipazione.getDettaglioComunicazione().getId() + "/" + n2);

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

		// ----- protocollazione buste tecniche -----
		for(int j = 0; j < bustaRiepilogativa.getListaCompletaLotti().size(); j++) {
			String codiceLotto = bustaRiepilogativa.getListaCompletaLotti().get(j);
			byte[] bustaTecnica = digestBusteTecniche.get(j);
			if (bustaTecnica != null) {
				allegati[i] = new WSDMProtocolloAllegatoType();
				allegati[i].setTitolo("Busta tecnica per il lotto " + bustaRiepilogativa.getListaCodiciInterniLotti().get(codiceLotto));
				allegati[i].setTipo("txt");
				allegati[i].setNome("busta_tecnica_lotto_"+ bustaRiepilogativa.getListaCodiciInterniLotti().get(codiceLotto)+".sha1.txt");
				allegati[i].setContenuto(bustaTecnica);
				// serve per Titulus
				allegati[i].setIdAllegato("W_INVCOM/"
						+ CommonSystemConstants.ID_APPLICATIVO + "/"
						+ comunicazionePartecipazione.getDettaglioComunicazione().getId()
						+ "/" + i);
				i++;
			}
		}

		// ----- protocollazione buste economiche ----- 
		for(int j = 0; j < bustaRiepilogativa.getListaCompletaLotti().size(); j++) {
			String codiceLotto = bustaRiepilogativa.getListaCompletaLotti().get(j);
			byte[] bustaEconomica = digestBusteEconomiche.get(j);
			if (bustaEconomica != null) {
				allegati[i] = new WSDMProtocolloAllegatoType();
				allegati[i].setTitolo("Busta economica per il lotto "+  bustaRiepilogativa.getListaCodiciInterniLotti().get(codiceLotto));
				allegati[i].setTipo("txt");
				allegati[i].setNome("busta_economica_lotto_"+ bustaRiepilogativa.getListaCodiciInterniLotti().get(codiceLotto)+".sha1.txt");
				allegati[i].setContenuto(bustaEconomica);
				// serve per Titulus
				allegati[i].setIdAllegato("W_INVCOM/"
						+ CommonSystemConstants.ID_APPLICATIVO + "/"
						+ comunicazionePartecipazione.getDettaglioComunicazione().getId()
						+ "/" + i);
				i++;
			}
		}
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
	    wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(numBusteValorizzate));		
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

	private void setMailRichiestaUfficioProtocollo(WizardDatiImpresaHelper datiImpresa,String tipoComunicazione, String tipoRichiesta, String descBando, String data) throws ApsSystemException, ApsException, XmlException {
		if (1 == this.getTipoProtocollazione()) {
			if (StringUtils.isBlank(this.getMailUfficioProtocollo())) {
				throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}
			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio
			Map<String, byte[]> p = new HashMap<String, byte[]>();

			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.getAllegaDocMailUfficioProtocollo()) {
				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.getSession().get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);

				byte[] bustaPreQualifica = getDigestFileBusta(bustaRiepilogativa.getBustaPrequalifica());
				if(bustaPreQualifica != null)
					p.put("busta_prequalifica.sha1.txt", bustaPreQualifica);

				byte[] bustaAmministrativa = getDigestFileBusta(bustaRiepilogativa.getBustaAmministrativa());
				if(bustaAmministrativa != null)
					p.put("busta_amministrativa.sha1.txt", bustaAmministrativa);

				if(bustaRiepilogativa.getBusteTecnicheLotti() != null){
					HashMap<String, RiepilogoBustaBean> busteTecniche = bustaRiepilogativa.getBusteTecnicheLotti();
					for(String codiceLotto : bustaRiepilogativa.getListaCompletaLotti()){
						RiepilogoBustaBean bustaTecnicaLotto = busteTecniche.get(codiceLotto);
						if(bustaTecnicaLotto!=null){
							if(!bustaTecnicaLotto.getDocumentiInseriti().isEmpty()){
								byte[] bustaTecnicaLottoDigest = getDigestFileBusta(bustaTecnicaLotto);
								if(bustaTecnicaLottoDigest != null)
									p.put("busta_tecnica_lotto_"+codiceLotto+".sha1.txt", bustaTecnicaLottoDigest);
							}
						}
					}
				}

				if(bustaRiepilogativa.getBusteEconomicheLotti() != null){
					HashMap<String, RiepilogoBustaBean> busteEconomiche = bustaRiepilogativa.getBusteEconomicheLotti();
					for(String codiceLotto : bustaRiepilogativa.getListaCompletaLotti()){
						RiepilogoBustaBean bustaEconomicaLotto = busteEconomiche.get(codiceLotto);
						if(bustaEconomicaLotto!=null){
							if(!bustaEconomicaLotto.getDocumentiInseriti().isEmpty()){
								byte[] bustaEconomicaLottoDigest = getDigestFileBusta(bustaEconomicaLotto);
								if(bustaEconomicaLottoDigest != null)
									p.put("busta_economica_lotto_"+codiceLotto+".sha1.txt", bustaEconomicaLottoDigest);
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
