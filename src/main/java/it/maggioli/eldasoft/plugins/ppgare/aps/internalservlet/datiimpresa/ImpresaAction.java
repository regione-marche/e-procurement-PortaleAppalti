package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.TokenInterceptor;
import com.opensymphony.xwork2.ActionContext;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaAggiornabileType;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Action per la gestione delle azioni sul dettaglio di un'impresa
 * 
 * @author stefano.sabbadin
 * @since 1.2
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.MODIFICA_IMPRESA, EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA,
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ImpresaAction extends EncodedDataAction implements SessionAware, IDownloadAction {
    /**
     * UID
     */
    private static final long serialVersionUID = 7423895356757257349L;

    private Map<String, Object> session;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	private IEventManager eventManager;

	/** url di ritorno al modulo chiamante l'aggiornamento anagrafica */
	@Validate(EParamValidation.URL)
	private String fromUrl;
    /** modulo chiamante l'aggiornamento anagrafica */
	@Validate(EParamValidation.MODULE_NAME)
	private String fromModule;
    /** action chiamante l'aggiornamento anagrafica */
	@Validate(EParamValidation.ACTION)
    private String fromAction;
    /**
     * indice del frame di esecuzione del modulo chiamante l'aggiornamento
     * anagrafica
     */
	@Validate(EParamValidation.DIGIT)
    private String fromFrame;

    // gestione del download del file xml
	private InputStream inputStream;        // stream associato al pdf generato e restituito dalla action
	@Validate(EParamValidation.FILE_NAME)
	private String filename;                // filename del pdf generato
	@Validate(EParamValidation.URL)
	private String urlPage;                    // ...
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;            // ...
    
    @Override
    public void setSession(Map<String, Object> session) {
		this.session = session;
    }

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

    public String getFromUrl() {
		return fromUrl;
    }

    public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
    }

    public String getFromModule() {
		return fromModule;
    }

    public void setFromModule(String fromModule) {
		this.fromModule = fromModule;
    }

	public String getFromAction() {
		return fromAction;
	}

	public void setFromAction(String fromAction) {
		this.fromAction = fromAction;
	}

    public String getFromFrame() {
		return fromFrame;
    }

    public void setFromFrame(String fromFrame) {
		this.fromFrame = fromFrame;
    }

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	@Override
	public String getUrlErrori() {
		// Example:
		//return this.urlPage +
		//	   "?actionPath=" + "/ExtStr2/do/FrontEnd/Cataloghi/variazionePrezziScadenzeChoices.action" +
		//	   "&currentFrame=" + this.currentFrame;
		return this.urlPage +
				"?actionPath=" + "/" +
				"&currentFrame=" + this.currentFrame;
	}

	/**
	 * Richiede al backoffice i dati dell'impresa mediante una comunicazione o
	 * l'accesso ai dati dell'impresa per consentire la memorizzazione di tali
	 * dati in sessione
	 */
	public String get() {
		setTarget(SUCCESS);
		
		String username = (null != this.getCurrentUser() ? this.getCurrentUser().getUsername() : null); 
		
		// NB: 
		// caso particolare per i dati impresa, visualizzazione e modifica usano la stessa action ma showlet diverse !!!
		String currentPageUrl = getCurrentPageUrl();
		boolean isModificaDati = (currentPageUrl != null && currentPageUrl.toLowerCase().contains("ppgare_impr_aggdati"));
		boolean isRichiestaVariazioneDati = (currentPageUrl != null && currentPageUrl.toLowerCase().contains("ppgare_impr_vardati"));

		// NB:
		// in caso di richiesta variazione dati, se il profilo utente e' SOLA LETTURA si nega l'accesso 
		if(isRichiestaVariazioneDati) {
			if(hasPermessiSolaLettura()) {
				addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
				setTarget(INPUT);
				return getTarget();
			}
		}

		// NB: 
		// solo per l'apertura dei dati impresa e' necessario verificare se eseguire lock oppure unlock del flusso
		// dato che le annotazioni del flusso sono comuni all'apertura in visualizzazione e all'apertura in modifica!!!
		if(isModificaDati) {
			// verifica il profilo di accesso ed esegui un LOCK alla funzione
			if( !lockAccessoFunzione(EFlussiAccessiDistinti.MODIFICA_IMPRESA, username) ) {
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				return this.getTarget();
			}
		} else {
			// forza l'UNLOCK della funzione 
			unlockAccessoFunzione();
		}

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			// si carica un eventuale errore parcheggiato in sessione, ad
			// esempio in caso di errori durante il download
			String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
			if (errore != null) {
				this.addActionError(errore);
			}

			try {
				WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(),
						this);

				if (datiImpresaHelper != null) {
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA,
							datiImpresaHelper);
					this.session.put(PortGareSystemConstants.SESSION_ID_URL_RITORNO_AGG_ANAGRAFICA_IMPRESA,
							this.fromUrl);
					this.session.put(PortGareSystemConstants.SESSION_ID_MODULO_RITORNO_AGG_ANAGRAFICA_IMPRESA,
							this.fromModule);
					this.session.put(PortGareSystemConstants.SESSION_ID_ACTION_RITORNO_AGG_ANAGRAFICA_IMPRESA,
							this.fromAction);
					this.session.put(PortGareSystemConstants.SESSION_ID_FRAME_RITORNO_AGG_ANAGRAFICA_IMPRESA,
							this.fromFrame);
				}

				// NB: quando c'e' un redirect da Authenticator,
				// e' necessario forzare il salvataggio esplicito di un nuovo token CSRF
				// per poter continuare l'esecuzione della action
				if(this.getCurrentUser().getUsername().equals(session.get(SystemConstants.SESSION_DATI_IMPRESA))) {
					String token = TokenInterceptor.saveSessionToken(this.getRequest());
					ActionContext.getContext().getParameters().put(TokenInterceptor.getStrutsTokenName(), new String[] {token});
					TokenInterceptor.redirectToken();
					this.addActionError(this.getText("Errors.datiImpresaIncompleti"));
					session.remove(SystemConstants.SESSION_DATI_IMPRESA);
				}

			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "get");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "get");
				this.addActionError(this.getText("Errors.aggiornamentoAnagrafica.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

	/**
     * visualizza una pagina di errore
     */
    public String getAfterError() {
		return SUCCESS;
    }
    
	/**
	 * Si estraggono i dati aggiornati dell'impresa ricercandoli prima nella
	 * comunicazione di aggiornamento anagrafica piu' recente, e quindi in
	 * assenza di comunicazioni, direttamente dalle tabelle del backoffice.
	 * I dati estratti vengono posti nell'helper in uscita.<br/>
	 * Per l'action chiamante devono essere definite nel package.properties i 2
	 * messaggi
	 * <ul>
	 * <li>Errors.aggiornamentoAnagrafica.xmlAggiornamentoNotFound</li>
	 * <li>Errors.aggiornamentoAnagrafica.parseXml</li>
	 * </ul>
	 *
	 * @param username
	 *            username dell'impresa
	 * @param action
	 *            action chiamante la presente funzione
	 * @return helper contenente i dati dell'impresa estratti dal backoffice
	 * @throws ApsException
	 * @throws XmlException
	 */
	public static WizardRegistrazioneImpresaHelper getLatestDatiImpresa(
			String username,
			EncodedDataAction action,
			HttpServletRequest request) 
		throws ApsException, XmlException 
	{
		WizardRegistrazioneImpresaHelper helper = null;

		if (SystemConstants.SERVICE_USER_NAME.equals(username)) {
			helper = wizardDatiImpresaForServiceUser();
		} else {
			// inizializza i manager necessari
			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER,
							request);
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.BANDI_MANAGER,
							request);
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
							request);
			IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
							request);

			// si estraggono dal B.O. i dati delle richieste di
			// aggiornamento inviate e non ancora processate; se ne trovo
			// almeno una, prendo l'ultima per poter rileggere i dati
			// dell'impresa da proporre nel form
			DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
			criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteriRicerca.setChiave1(username);
			criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ANAGRAFICA);
			List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager
					.getElencoComunicazioni(criteriRicerca);

			if (comunicazioni == null || comunicazioni.size() == 0) {
				// si estraggono direttamente dal B.O. i dati dell'impresa
				int anniPrecedentiPerCessati = 0;
				if (((Integer) appParamManager.getConfigurationValue("incarichiCessati.estraiNumAnniPrecedenti")) != null) {
					anniPrecedentiPerCessati = ((Integer) appParamManager.getConfigurationValue("incarichiCessati.estraiNumAnniPrecedenti")).intValue();
				}
				Calendar now = Calendar.getInstance();
				int year = now.get(Calendar.YEAR);

				DatiImpresaDocument datiImpresa = null;
				if (anniPrecedentiPerCessati >= 0) {
					// gestione dei cessati
					datiImpresa = bandiManager.getDatiImpresa(username, new GregorianCalendar(year - anniPrecedentiPerCessati - 1, GregorianCalendar.DECEMBER, 31).getTime());
				} else {
					datiImpresa = bandiManager.getDatiImpresa(username, null);
				}

				if (datiImpresa.getDatiImpresa().sizeOfLegaleRappresentanteArray() > 0) {
					for (ReferenteImpresaAggiornabileType refImpr : datiImpresa.getDatiImpresa().getLegaleRappresentanteArray()) {
						refImpr.setSolaLettura(refImpr.getDataFineIncarico() != null);
					}
				}

				if (datiImpresa.getDatiImpresa().sizeOfDirettoreTecnicoArray() > 0) {
					for (ReferenteImpresaAggiornabileType refImpr : datiImpresa.getDatiImpresa().getDirettoreTecnicoArray()) {
						refImpr.setSolaLettura(refImpr.getDataFineIncarico() != null);
					}
				}

				if (datiImpresa.getDatiImpresa().sizeOfAltraCaricaArray() > 0) {
					for (ReferenteImpresaAggiornabileType refImpr : datiImpresa.getDatiImpresa().getAltraCaricaArray()) {
						refImpr.setSolaLettura(refImpr.getDataFineIncarico() != null);
					}
				}

				if (datiImpresa.getDatiImpresa().sizeOfCollaboratoreArray() > 0) {
					for (ReferenteImpresaAggiornabileType refImpr : datiImpresa.getDatiImpresa().getCollaboratoreArray()) {
						refImpr.setSolaLettura(refImpr.getDataFineIncarico() != null);
					}
				}

				helper = ImpresaAction.popolaFromTabsBackOffice(datiImpresa);
			} else {
				// si individua l'ultima comunicazione in stato da
				// processare (quella con id massimo, dato che e' un
				// contatore)
				Long maxId = new Long(-1);
				for (int i = 0; i < comunicazioni.size(); i++) {
					if (comunicazioni.get(i).getId() > maxId) {
						maxId = comunicazioni.get(i).getId();
					}
				}

				// si estraggono i dati dall'ultima comunicazione in stato
				// da processare
				ComunicazioneType comunicazione = comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, maxId);

				AllegatoComunicazioneType allegatoAggiornamentoAnagrafica = null;
				int i = 0;
				while (comunicazione.getAllegato() != null
						&& i < comunicazione.getAllegato().length
						&& allegatoAggiornamentoAnagrafica == null) {
					// si cerca l'xml con i dati dell'aggiornamento
					// anagrafica tra tutti gli allegati
					if (PortGareSystemConstants.NOME_FILE_AGG_ANAGRAFICA
							.equals(comunicazione.getAllegato()[i].getNomeFile())) {
						allegatoAggiornamentoAnagrafica = comunicazione
								.getAllegato()[i];
					} else
						i++;
				}
				if (allegatoAggiornamentoAnagrafica == null) {
					// non dovrebbe succedere mai...si inserisce questo
					// controllo per blindare il codice da eventuali
					// comportamenti anomali
					if (action != null) {
						action.addActionError(action.getText("Errors.aggiornamentoAnagrafica.xmlAggiornamentoNotFound"));
						action.setTarget(CommonSystemConstants.PORTAL_ERROR);
					} else {
						//...
					}
				} else {
					AggiornamentoAnagraficaImpresaDocument documento = AggiornamentoAnagraficaImpresaDocument.Factory
							.parse(new String(allegatoAggiornamentoAnagrafica
									.getFile()));
					helper = ImpresaAction.popolaFromComunicazione(documento,
							comunicazione.getDettaglioComunicazione().getId());
				}
			}

			if (helper != null) {
				try {
					helper.setDittaIndividuale(
							InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE)
									.containsKey(helper.getDatiPrincipaliImpresa().getTipoImpresa()));
					helper.setLiberoProfessionista(
							InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
									.containsKey(helper.getDatiPrincipaliImpresa().getTipoImpresa()));
					helper.setConsorzio(
							InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_CONSORZIO)
									.containsKey(helper.getDatiPrincipaliImpresa().getTipoImpresa()));
				} catch (Exception ex) {
					ApsSystemUtils.getLogger().error("WizardRegistrazioneImpresaHelper.getLatestDatiImpresa", ex.getMessage());
				}
				
				helper.setMailUtentePrecedente(
						DatiImpresaChecker.getEmailRiferimento(
								helper.getDatiPrincipaliImpresa().getEmailPECRecapito(),
								helper.getDatiPrincipaliImpresa().getEmailRecapito()));
				helper.setMailUtenteImpostata(helper.getMailUtentePrecedente());

				try {
					if (!customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP")) {
						helper.setIndirizziImpresa(new ArrayList<IIndirizzoImpresa>());
					}
				} catch (Exception e) {
					ApsSystemUtils.getLogger().error("WizardRegistrazioneImpresaHelper.getLatestDatiImpresa", e.getMessage());
				}
			}
		}

		return helper;
	}

	private static WizardRegistrazioneImpresaHelper wizardDatiImpresaForServiceUser() {
		WizardRegistrazioneImpresaHelper toReturn = new WizardRegistrazioneImpresaHelper();

		toReturn.setDatiPrincipaliImpresa(wizardDatiPrincipaliForServiceUser());
		toReturn.getLegaliRappresentantiImpresa().add(legaleRappresentanteForServiceUser());
		toReturn.setDatiUlterioriImpresa(wizardDatiUlterioriForServiceUser());

		return toReturn;
	}

	private static IDatiUlterioriImpresa wizardDatiUlterioriForServiceUser() {
		WizardDatiUlterioriImpresaHelper toReturn = new WizardDatiUlterioriImpresaHelper();

		toReturn.setIscrittoCCIAA("0");
		toReturn.setSoggettoNormativeDURC("0");
		toReturn.setIscrittoWhitelistAntimafia("0");
		toReturn.setCodiceIBANCCDedicato("NL94ABNA3882854936");
		toReturn.setDataScadenzaAbilitPreventiva("01/01/2099");
		toReturn.setZoneAttivita(
				IntStream.range(0, 20).mapToObj(it -> "1").toArray(String[]::new)
		);
		toReturn.setAssunzioniObbligate("0");
		toReturn.setNumDipendenti(
				IntStream.range(0, 3).map(it -> it+1).boxed().toArray(Integer[]::new)
		);
		toReturn.setClasseDimensioneDipendenti("2");
		toReturn.setIscrittoAnagrafeAntimafiaEsecutori("0");
		toReturn.setPossiedeRatingLegalita("0");

		return toReturn;
	}

	private static ISoggettoImpresa legaleRappresentanteForServiceUser() {
		SoggettoImpresaHelper toReturn = new SoggettoImpresaHelper();

		toReturn.setTipoSoggetto("1");
		toReturn.setSoggettoQualifica("1-");
		toReturn.setDataInizioIncarico("22/03/2000");
		toReturn.setResponsabileDichiarazioni("1");

		toReturn.setCognome("Rossi");
		toReturn.setNome("Mario");
		toReturn.setCodiceFiscale("RSSMRA80C22L407W");
		toReturn.setSesso("M");
		toReturn.setComune("Treviso");
		toReturn.setIndirizzo("Via Garibaldi");
		toReturn.setNumCivico("13");
		toReturn.setCap("95586");
		toReturn.setProvincia("TV");
		toReturn.setNazione("Italia");

		toReturn.setComuneNascita("Treviso");
		toReturn.setDataNascita("22/03/1980");
		toReturn.setProvinciaNascita("TV");

		return toReturn;
	}

	private static IDatiPrincipaliImpresa wizardDatiPrincipaliForServiceUser() {
		WizardDatiPrincipaliImpresaHelper toReturn = new WizardDatiPrincipaliImpresaHelper();

		toReturn.setRagioneSociale("Service Appalti");
		toReturn.setNaturaGiuridica("1");
		toReturn.setTipoImpresa("1");
		toReturn.setAmbitoTerritoriale("1");	//Italia
		toReturn.setCodiceFiscale("DNLCCS80C22I480C");
		toReturn.setPartitaIVA("DNLCCS80C22I480C");
		toReturn.setCapSedeLegale("98898");
		toReturn.setComuneSedeLegale("Salerno");
		toReturn.setNumCivicoSedeLegale("45");
		toReturn.setEmailRecapito("helpdesk.adadvice@gmail.com");
		toReturn.setEmailRecapitoConferma("helpdesk.adadvice@gmail.com");
		toReturn.setEmailPECRecapito("helpdesk.adadvice@gmail.com");
		toReturn.setEmailPECRecapitoConferma("helpdesk.adadvice@gmail.com");
		toReturn.setTelefonoRecapito("+39 1111111111");
		toReturn.setCellulareRecapito("+39 1111111111");
		toReturn.setFaxRecapito("+39 111111");
		toReturn.setIndirizzoSedeLegale("Via Noalese");
		toReturn.setProvinciaSedeLegale("SA");

		return toReturn;
	}

	/**
	 * Si estraggono i dati aggiornati dell'impresa ricercandoli prima nella
	 * comunicazione di aggiornamento anagrafica piu' recente, e quindi in
	 * assenza di comunicazioni, direttamente dalle tabelle del backoffice.
	 */
	public static WizardRegistrazioneImpresaHelper getLatestDatiImpresa(
			String username,
			EncodedDataAction action)
		throws ApsException, XmlException
	{
		//EncodedDataAction action = (EncodedDataAction)ActionContext.getContext().getActionInvocation().getAction();
		HttpServletRequest request = (action != null ? action.getRequest() : ServletActionContext.getRequest());
		return ImpresaAction.getLatestDatiImpresa(username, action, request);
	}

	/**
	 * Si estraggono i dati aggiornati dell'impresa ricercandoli prima nella
	 * comunicazione di aggiornamento anagrafica piu' recente, e quindi in
	 * assenza di comunicazioni, direttamente dalle tabelle del backoffice.
	 */
	public static WizardRegistrazioneImpresaHelper getLatestDatiImpresa(
			String username,
			HttpServletRequest request)
		throws ApsException, XmlException 
	{
		EncodedDataAction action = (EncodedDataAction)ActionContext.getContext().getActionInvocation().getAction();
		return ImpresaAction.getLatestDatiImpresa(username, action, request);
	}

	/**
     * Popola il contenitore da porre in sessione a partire dai dati letti
     * direttamente dalle tabelle del backoffice, potenzialmente incompleti o
     * vuoti eccetto i dati obbligatori minimi (codice fiscale, partita iva,
     * ragione sociale ed email)
     * 
     * @param datiImpresa
     *            contenitore con i dati ricevuti dal backoffice
     * @return helper popolato
     */
    private static WizardRegistrazioneImpresaHelper popolaFromTabsBackOffice(
			DatiImpresaDocument datiImpresa) {
		return new WizardRegistrazioneImpresaHelper(datiImpresa.getDatiImpresa());
    }

    /**
     * Popola il contenitore da porre in sessione a partire dai dati letti
     * dall'ultima comunicazione di aggiornamento anagrafica per l'impresa
     * 
     * @param documento
     *            allegato xml con i dati dell'aggiornamento anagrafica presente
     *            nella comunicazione
     * @return helper popolato con i dati e con l'id della comunicazione
     */
    private static WizardRegistrazioneImpresaHelper popolaFromComunicazione(
			AggiornamentoAnagraficaImpresaDocument documento, Long id) {
		WizardRegistrazioneImpresaHelper helper = new WizardRegistrazioneImpresaHelper(documento
				.getAggiornamentoAnagraficaImpresa().getDatiImpresa());
		helper.setIdComunicazione(id);
		return helper;
    }
    
    /**
     * Export dei dati impresa su file XML 
     */
    public String exportXml() {
		String target = SUCCESS;

		this.filename = null;
		this.inputStream = null;

		try {
			if (null != this.getCurrentUser() &&
					!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

				// recupera i dati dell'impresa...
				WizardRegistrazioneImpresaHelper helper = getLatestDatiImpresa(
						this.getCurrentUser().getUsername(),
						this);

				if(helper == null) {
					// la sessione e' scaduta, occorre riconnettersi
					this.addActionError(this.getText("Errors.sessionExpired"));
					target = CommonSystemConstants.PORTAL_ERROR;
				} else {
					// traccia l'evento di esportazione dei dati impresa...
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination("");
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.DOWNLOAD_DOCUMENTO_RISERVATO);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage("Esportazione dati impresa in formato M-XML per la ditta " +
							helper.getDatiPrincipaliImpresa().getRagioneSociale());

					// preimposta parametri per la registrazione...
					helper.setUsername(null);
					helper.setPrivacy(null);
					helper.setUtilizzoPiattaforma(null);
					helper.setSoggettoRichiedente(null);

					// esporta il i dati dell'impresa...
					this.filename = this.getCurrentUser().getUsername() + ".xml";
					ByteArrayOutputStream bos = new ByteArrayOutputStream();

					try {
						helper.exportToXmlPortale(bos, filename);
						this.inputStream = new ByteArrayInputStream(bos.toByteArray());
					} catch (Exception ex) {
						session.put(ERROR_DOWNLOAD, this.getText("Errors.importExportMXML") + ex.getMessage());
						evento.setError(ex);
						this.addActionError( ex.getMessage() );
						target = INPUT;
					}

					// registra l'evento...
					this.eventManager.insertEvent(evento);
				}
			} else {
				//this.addActionError(this.getText("Errors.sessionExpired"));
				this.addActionMessage(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (Exception ex) {
			session.put(ERROR_DOWNLOAD, ex.getMessage());
			ApsSystemUtils.getLogger().error(ex.getMessage());
			target = INPUT;
		}

		return target;
    }

}
