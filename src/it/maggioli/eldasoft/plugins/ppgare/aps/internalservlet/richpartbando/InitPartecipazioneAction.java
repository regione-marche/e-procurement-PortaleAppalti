package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Action di inizializzazione apertura wizard partecipazione al bando di gara.
 * Questa classe estende EncodedDataAction esclusivamente per poter usufruire
 * del metodo statico ImpresaAction.getLatestDatiImpresa
 *
 * @author Stefano.Sabbadin
 */
public class InitPartecipazioneAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8730921987830449769L;

	private INtpManager ntpManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;

	private Map<String, Object> session;

	/**
	 * Codice del bando per elenco operatori economici per il quale gestire un'iscrizione
	 */
	private String codice;
	private String operazione;
	private String progressivoOfferta;

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di partecipazione ad una gara
	 */
	public String newPartecipazione() {
		return this.newWizard(PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di invio offerta
	 */
	public String newInvioOfferta() {
		return this.newWizard(PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di comprova requisiti ex articolo 48
	 */
	public String newComprovaRequisiti() {
		return this.newWizard(PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa mediante una comunicazione o
	 * l'accesso ai dati dell'impresa per consentire la gestione del wizard di
	 * partecipazione ad una gara/invio offerta/comprova requisiti e memorizza
	 * tali dati in sessione
	 */
	private String newWizard(int tipoEvento) {
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// si estraggono dal B.O. i dati del bando di gara a cui
				// partecipare
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);

				// il controllo sulla data non si effettua per la comprova
				// requisiti
				Date dataTermine = null;
				String oraTermine = null;
				if (tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
					dataTermine = dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda();
					oraTermine = dettGara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda();
				} else if (tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
					dataTermine = dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta();
					oraTermine = dettGara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta();
				}
				
				String nomeOperazione = null;
				switch (tipoEvento) {
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
					nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_PARTECIPAZIONE");
					break;
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
					nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_OFFERTA");
					break;
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
					nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_GARA_COMPROVA_REQUISITI");
					break;
				}
				
				if (dataTermine != null) {
					// si prosegue solo se la data termine e' valorizzata

					// si calcola il timestamp ntp
					Date dataAttuale = null;
					try {
						dataAttuale = this.ntpManager.getNtpDate();
					} catch (SocketTimeoutException e) {
						this.addActionError(this.getText("Errors.ntpTimeout", new String[] { nomeOperazione }));
						this.setTarget("block");
					} catch (UnknownHostException e) {
						this.addActionError(this.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione }));
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					} catch (ApsSystemException e) {
						this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
						this.setTarget("block");
					}

					if (dataAttuale != null) {
						if (dataAttuale.compareTo(InitIscrizioneAction.calcolaDataOra(dataTermine, oraTermine, true)) > 0) {
							this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
							this.setTarget("block");
						}
					}
				}

				WizardPartecipazioneHelper partecipazioneHelper = null;
				if (SUCCESS.equals(this.getTarget())) {
					partecipazioneHelper = InitPartecipazioneAction.createHelper(
							tipoEvento, 
							dettGara, 
							this.codice, 
							this.getCurrentUser().getUsername(),
							this.progressivoOfferta);
					this.setDatiImpresaAndRTI(partecipazioneHelper);
				}

				if (SUCCESS.equals(this.getTarget())) {
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA, partecipazioneHelper);
				}

			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "newWizard - " + tipoEvento);
				this.addActionError(this.getText("Errors.aggiornamentoAnagrafica.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "newWizard - " + tipoEvento);
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

	/**
	 * Si crea il contenitore da porre in sessione
	 *
	 * @param tipoEvento tipologia di evento
	 * @param dettGara dettaglio della gara estratta dal backoffice
	 * @param codiceGara codice della gara estratta dal backoffice
//	 * @param progressivoOfferta progressivo dell'offerta estratta dal backoffice
	 * 
	 * @return helper da memorizzare in sessione
	 * @throws ApsException 
	 */
	public static WizardPartecipazioneHelper createHelper(
			int tipoEvento,
			DettaglioGaraType dettGara,
			String codiceGara,
			String username,
			String progressivoOfferta
		) throws ApsException 
	{
		// NB: attualmente questo helper viene completato con l'ausilio
		//     di setDatiImpresaAndRTI()
		//     probabilmente si può prima verificare se esiste l'helper 
		//     in sessione e restituire quello della sessione... 
		//     viceversa si può creare un helper nuovo come segue...
		if(StringUtils.isEmpty(progressivoOfferta)) {
			progressivoOfferta = "1";
		}
		
		WizardPartecipazioneHelper partecipazioneHelper = new WizardPartecipazioneHelper();
		partecipazioneHelper.setTipoEvento(tipoEvento);
		switch (tipoEvento) {
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
				partecipazioneHelper.setDataScadenza(InitIscrizioneAction
								.calcolaDataOra(dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(), 
												dettGara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(), 
												true));
				break;
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
				partecipazioneHelper.setDataScadenza(InitIscrizioneAction
								.calcolaDataOra(dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(), 
												dettGara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(), 
												true));
				break;
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
				// per la comprova dei requisiti non va fatto il controllo della data
				partecipazioneHelper.setDataScadenza(null);
				break;
		}
		
		partecipazioneHelper.setIdBando(codiceGara);
		partecipazioneHelper.setDescBando(dettGara.getDatiGeneraliGara().getOggetto());
		partecipazioneHelper.setGenere(dettGara.getDatiGeneraliGara().getTipologia());
		partecipazioneHelper.setProgressivoOfferta(progressivoOfferta);

		if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE) {
			partecipazioneHelper.setLottiDistinti(true);
			
		} else if(dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO 
				  && dettGara.getDatiGeneraliGara().getBusteDistinte()) 
		{
			partecipazioneHelper.setPlicoUnicoOfferteDistinte(true);
			if(!partecipazioneHelper.isGaraTelematica()) {
				partecipazioneHelper.getLotti().add(codiceGara);
			}
			
		} else {
			// nel caso di lotto unico o piu' lotti con offerte
			// distinte si inserisce direttamente come codice lotto
			// il codice del bando stesso
			partecipazioneHelper.getLotti().add(codiceGara);
		}
		partecipazioneHelper.setGaraTelematica(dettGara.getDatiGeneraliGara().isProceduraTelematica());
		
		partecipazioneHelper.setIterGara( Integer.parseInt(dettGara.getDatiGeneraliGara().getIterGara()) );
		
		partecipazioneHelper.setGaraRilancio(dettGara.getDatiGeneraliGara().isGaraRilancio());
		
//		partecipazioneHelper.setGaraRilancio( StringUtils.isNotEmpty(dettGara.getDatiGeneraliGara().getGaraRilancioPrecedente()) );
//		
//		// verifica se è una gara di rilancio
//		// NB: codice commentato perchè non dovrebbe essere necessario recuperare i dati rti e consorziate !!!
//		if(garaRilancio) {
//			if(partecipazioneHelper.isGaraRilancio()) {
//				// recupera i dati della gara precedente...
//				IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
//					.getBean(PortGareSystemConstants.BANDI_MANAGER,
//							 ServletActionContext.getRequest());
//	
//				DettaglioGaraType dettGaraPreced = bandiManager.getDettaglioGara(
//						dettGara.getDatiGeneraliGara().getGaraRilancioPrecedente());
//				
//				WizardPartecipazioneHelper garaPrecedente = createHelper(
//						tipoEvento, 
//						dettGaraPreced, 
//						dettGaraPreced.getDatiGeneraliGara().getCodice(),
//						false);
//				
//				partecipazioneHelper.copyRtiFrom(garaPrecedente);
//			}
//		}
		
		return partecipazioneHelper;
	}
	
	/**
	 * Si estraggono i dati dell'impresa ricercandoli prima nella comunicazione di
	 * aggiornamento anagrafica piu' recente, e quindi in assenza di
	 * comunicazioni, direttamente dal backoffice. I dati estratti vengono posti
	 * nell'helper del wizard. Inoltre si estraggono da eventuali comunicazioni
	 * con generazione del barcode i dati della RTI, ed in assenza di
	 * comunicazioni si estraggono le informazioni su eventuali partecipazioni
	 * dell'impresa alla stessa gara.
	 *
	 * @param partecipazioneHelper helper da porre in sessione
	 * @throws ApsException
	 * @throws XmlException
	 */
	private void setDatiImpresaAndRTI(WizardPartecipazioneHelper partecipazioneHelper)
			throws ApsException, XmlException 
	{
		// si estraggono dal B.O. i dati delle richieste di
		// aggiornamento inviate e non ancora processate; se ne trovo
		// almeno una, prendo l'ultima per poter rileggere i dati
		// dell'impresa da proporre nel form
		/* --- CESSATI --- */
		WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
				this.getCurrentUser().getUsername(), 
				this, 
				this.appParamManager);
		
		partecipazioneHelper.setImpresa(datiImpresaHelper);
		if (datiImpresaHelper.isConsorzio() 
			&& this.bandiManager.isConsorziateEsecutriciPresenti(this.getCurrentUser().getUsername(), this.codice)) 
		{
			partecipazioneHelper.setConsorziateEsecutriciPresenti(true);
		}

		// recupera l'helper in sessione 
		// (salvato da OpenGestioneBusteAction/OpenGestioneBusteDistinteAction)
		WizardPartecipazioneHelper helper = (WizardPartecipazioneHelper)this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

		if (SUCCESS.equals(this.getTarget())) {
			// si cercano le comunicazioni di tipo partecipazione relative 
			// al plico, all'utente e alla gara in input
			String tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE;
			
			if (partecipazioneHelper.isGaraTelematica()) {
				tipoComunicazione = (partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
								? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
								: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
			}

			DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
			criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
			criteriRicerca.setChiave2(this.codice);
			criteriRicerca.setChiave3(this.progressivoOfferta);
			if (partecipazioneHelper.isGaraTelematica()) {
				criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			} else {
				criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			}
			criteriRicerca.setTipoComunicazione(tipoComunicazione);

			// si cerca la comunicazione inviata in precedenza
			List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteriRicerca);

			// si cerca l'eventuale tipo di partecipazione se gia' presente in backoffice
			// se l'"helper" era già in sessione allora il tipo partecipazione 
			// è già stato recuperato dal backoffice 
			// e non è necessario rileggere i dati
			TipoPartecipazioneType tipoPartecipazione = null;
			if(helper == null) {  
				tipoPartecipazione = this.bandiManager.getTipoPartecipazioneImpresa(
						this.getCurrentUser().getUsername(), 
						this.codice, 
						partecipazioneHelper.getProgressivoOfferta());
			}
			
			if (comunicazioni == null || comunicazioni.isEmpty()) {
				if(helper == null) {
					// in caso di comunicazione non reperita si utilizzano gli eventuali dati della
					// partecipazione presenti in backoffice
					if (tipoPartecipazione != null) {
						// ristrette e negoziate
						partecipazioneHelper.setRti(tipoPartecipazione.isRti());
						partecipazioneHelper.setDenominazioneRTI(tipoPartecipazione.getDenominazioneRti());
						
						if (!tipoPartecipazione.isRti()) {
							// permetto l'editing dei dati se sono invitato come singola impresa
							partecipazioneHelper.setEditRTI(true);
						}
					} else {
						// aperte
						partecipazioneHelper.setEditRTI(true);
					}
				}
			} else {
				// si individua l'ultima comunicazione in stato da
				// processare (quella con id massimo, dato che e' un
				// contatore)
				Long maxId = (long) -1;
				for (int i = 0; i < comunicazioni.size(); i++) {
					if (comunicazioni.get(i).getId() > maxId) {
						maxId = comunicazioni.get(i).getId();
					}
				}

				// si estraggono i dati dall'ultima comunicazione in stato
				// da processare
				ComunicazioneType comunicazione = this.comunicazioniManager
								.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, maxId);

//				AllegatoComunicazioneType allegatoTipoPartecipazione = null;
//				int i = 0;
//				while (comunicazione.getAllegato() != null
//								&& i < comunicazione.getAllegato().length
//								&& allegatoTipoPartecipazione == null) {
//					// si cerca l'xml con i dati del tipo partecipazione tra
//					// tutti gli allegati
//					if (PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE
//									.equals(comunicazione.getAllegato()[i]
//													.getNomeFile())
//									|| PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE
//									.equals(comunicazione.getAllegato()[i]
//													.getNomeFile())) {
//						allegatoTipoPartecipazione = comunicazione
//										.getAllegato()[i];
//					} else {
//						i++;
//					}
//				}
				
				String xml = partecipazioneHelper.getAllegatoXml(
						comunicazione, 
						PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE);
				
//				if (allegatoTipoPartecipazione == null) {
				if (xml == null) {
					// non dovrebbe succedere mai...si inserisce questo
					// controllo per blindare il codice da eventuali
					// comportamenti anomali
					this.addActionError(this.getText("Errors.tipoPartecipazione.xmlTipoPartecipazioneNotFound"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				} else {
					partecipazioneHelper.setIdComunicazioneTipoPartecipazione(maxId);
//					TipoPartecipazioneDocument documento = TipoPartecipazioneDocument.Factory
//									.parse(new String(allegatoTipoPartecipazione
//																	.getFile()));
					TipoPartecipazioneDocument documento = TipoPartecipazioneDocument.Factory.parse(xml);
					partecipazioneHelper.fillFromXml(documento);
					
					if(helper == null) {
						if (tipoPartecipazione == null || !tipoPartecipazione.isRti()) {
							// rendo editabile la sezione solo se la comunicazione rappresenta dati raccolti
							// per una procedura aperta (non prevede dati sulla partecipazione in backoffice)
							// oppure nel caso di invito (ristretta e negoziata) come impresa singola
							partecipazioneHelper.setEditRTI(true);
						}
					}
				}
			}
		}

		// sincronizza helper corrente con l'helper in sessione...
		// l'helper in sessione contiene dati più aggiornati!!!
		if(helper != null) {
			partecipazioneHelper.setTipoProcedura(helper.getTipoProcedura());
			partecipazioneHelper.setIterGara(helper.getIterGara());
			partecipazioneHelper.copyRtiFrom(helper);
		}
		
		// configura gli step del wizard
		partecipazioneHelper.fillStepsNavigazione();
	}
	
}
