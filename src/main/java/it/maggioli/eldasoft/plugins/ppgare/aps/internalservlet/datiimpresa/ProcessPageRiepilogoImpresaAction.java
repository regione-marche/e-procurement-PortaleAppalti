package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.RegistrazioneImpresaManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Action di salvataggio dell'impresa che richiede la modifica dei dati
 * anagrafici
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageRiepilogoImpresaAction extends BaseAction implements SessionAware {
	
	 /**
     * UID
     */
    private static final long serialVersionUID = -8933279873028742963L;

	private CustomConfigManager customConfigManager;
	
	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
    
	private IEventManager eventManager;
	
	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	/**
	 * Si verifica che l'eventuale modifica degli indirizzi email non vada in
	 * conflitto con gli indirizzi email usati come riferimento per altre imprese.
	 */
	@Override
	public void validate() {
		super.validate();
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		if (registrazioneHelper != null) {
			try {
				String email = DatiImpresaChecker.getEmailRiferimento(
						registrazioneHelper.getDatiPrincipaliImpresa()
								.getEmailPECRecapito(), registrazioneHelper
								.getDatiPrincipaliImpresa().getEmailRecapito());
				if (email != null
						&& this._datiImpresaChecker.existsEmail(
								this.getCurrentUser().getUsername(), email)) {
					List<Object> arg = new ArrayList<Object>();
					arg.add(email);
					this.addFieldError("emailRecapito",
							this.getText("Errors.mailAlreadyPresent", arg));
				}
			} catch (Throwable t) {
				throw new RuntimeException(
						"Errore durante la verifica dei dati richiesti per l'impresa "
								+ registrazioneHelper
										.getDatiPrincipaliImpresa()
										.getRagioneSociale(), t);
			}
		}
	}

	/**
	 * Invia la richiesta di registrazione al backoffice
	 */
	public String send() {
		String target = SUCCESS;

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (helper.isDatiInviati()) {
				// l'aggiornamento anagrafica e' gia' stato completato, pertanto
				// si segnala l'errore e non si ripete l'aggiornamento
				this.addActionError(this.getText("Errors.aggiornamentoAnagraficaCompleted"));
				target = CommonSystemConstants.PORTAL_ERROR;
				// si elimina pertanto l'oggetto dalla sessione in modo da
				// evitare il ripetersi
				this.session
						.remove(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
			} else {
			    XmlOptions validationOptions = new XmlOptions();
			    ArrayList<XmlValidationError> validationErrors =
			        new ArrayList<XmlValidationError>();
			    validationOptions.setErrorListener(validationErrors);
			
			    if (!helper.getXmlDocumentAggiornamentoAnagrafica().validate(validationOptions)) {
			    	this.addActionError(this.getText("Errors.checkDatiObbligatori"));
			    	String errmsg = "";
			    	for(int i = 0; i < validationErrors.size(); i++) {
			    		errmsg += validationErrors.get(i).getMessage() + "\n";
			    	}
			    	ApsSystemUtils.getLogger().error(this.getText("Errors.checkDatiObbligatori") + "\n" + errmsg); 
					target = "errorAgg";
				} else {
					EsitoOutType esito = null;
					try {
						String email = DatiImpresaChecker.getEmailRiferimento(
								helper.getDatiPrincipaliImpresa()
										.getEmailPECRecapito(), helper
										.getDatiPrincipaliImpresa()
										.getEmailRecapito());
						esito = this._registrazioneImpresaManager
								.updateImpresa(this.getCurrentUser().getUsername(),
										helper.getDatiPrincipaliImpresa().getEmailRecapito(), 
										helper.getDatiPrincipaliImpresa().getEmailPECRecapito(), 
										helper.getDatiPrincipaliImpresa().getRagioneSociale(), 
										helper.getDatiPrincipaliImpresa().getCodiceFiscale(), 
										helper.getDatiPrincipaliImpresa().getPartitaIVA());
						
						if (esito.isEsitoOk()) {
							// se l'aggiornamento e' andato a buon fine, si
							// aggiorna l'email nel profilo in sessione
							IUserProfile userProfile = (IUserProfile)this.getCurrentUser().getProfile();
							RegistrazioneImpresaManager.updateEntityAttributes(
									userProfile,
									(String) userProfile.getValue(userProfile
											.getFirstNameAttributeName()),
									email);
							if (helper.getIdComunicazione() != null
									&& this._comunicazioniManager
											.isComunicazioneProcessata(
													CommonSystemConstants.ID_APPLICATIVO,
													helper.getIdComunicazione())) {
								// si sbianca l'id comunicazione in quanto si deve
								// procedere con una nuova comunicazione dato che
								// quella che si deve aggiornare nel frattempo e'
								// stata processata
								helper.setIdComunicazione(null);
							}
							this.sendComunicazione(helper);
							this.session
									.remove(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
							session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
						} else {
							this.addActionError(this.getText("Errors.aggiornamentoAnagraficaPortaleUnexpectedError", 
															 new String[] {esito.getCodiceErrore()}));
							target = "errorAgg";
						}
					} catch (ApsException t) {
						ApsSystemUtils.logThrowable(t, this, "save");
						ExceptionUtils.manageExceptionError(t, this);
						target = "errorAgg";
					}
				}
			}
		}
		return target;
	}

    public String cancel() {
	return "cancel";
    }

    public String back() {
    	String target = "back";
    	
		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
	    	boolean hasDatiUlteriori = true;
	    	try {
				if (!customConfigManager.isVisible("IMPRESA-DATIULT", "STEP")) {
					hasDatiUlteriori = false;
				}
			} catch (Exception e) {
				// Configurazione sbagliata
				ApsSystemUtils.logThrowable(e, this, "next",
						"Errore durante la ricerca delle proprietà di visualizzazione dello step dati ulteriori");
			}
			// nel caso di libero professionista la pagina precedente ï¿½ il
			// dettaglio con altri dati del libero professionista e non la
			// pagina dei soggetti
			if (helper.isLiberoProfessionista()) {
				target = hasDatiUlteriori ? "back" : "backNoDatiUlterioriLiberoProf";
			} else if (!hasDatiUlteriori) {
				target = "backNoDatiUlteriori";
			}
		}
		return target;
    }

    @SkipValidation
    public String createPdf() {
	String target = "successCreatePdf";

	WizardDatiImpresaHelper helper = getSessionHelper();
	if (helper == null) {
	    // la sessione e' scaduta, occorre riconnettersi
	    this.addActionError(this.getText("Errors.sessionExpired"));
	    target = CommonSystemConstants.PORTAL_ERROR;
	} else {
	    // la sessione non e' scaduta, per cui proseguo regolarmente

	    // si determina il nome del file di destinazione nell'area
	    // temporanea
	    String nomePdf = FileUploadUtilities.generateFileName() + ".pdf";
	    File filePdf = new File(this.getTempDir().getAbsolutePath()
		    + File.separatorChar + nomePdf);

	    StringBuilder xml = new StringBuilder();
	    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	    // qui si deve creare la stringa XML da passare al report

	    try {
		// si carica il report jasper, lo si filla con i dati e si
		// genera il pdf
		InputStream isJasper = this.getRequest().getSession()
			.getServletContext().getResourceAsStream(
				PortGareSystemConstants.GARE_JASPER_FOLDER
					+ "AnagraficaImpresa.jasper");
		JRXmlDataSource jrxmlds = new JRXmlDataSource(
			new ByteArrayInputStream(xml.toString().getBytes()),
			"/rich_partecipazione/lotto");
		JasperPrint print = JasperFillManager.fillReport(isJasper,
			new HashMap<String, Object>(), jrxmlds);
		JRExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
			filePdf.getAbsolutePath());
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
		exporter.exportReport();

		// generato il report con JasperReport, si memorizza lo stream
		// per il download e si inserisce una referenza nel bean in
		// sessione per la rimozione alla rimozione del bean dalla
		// sessione
		this.inputStream = new FileInputStream(filePdf);
		helper.getTempFiles().add(filePdf);

	    } catch (JRException e) {
		ApsSystemUtils.logThrowable(e, this, "createPdf");
		ExceptionUtils.manageExceptionError(e, this);
		// il download ï¿½ una url indipendente e non dentro una porzione
		// di pagina
		target = ERROR;
	    } catch (IOException e) {
		ApsSystemUtils.logThrowable(e, this, "createPdf");
		ExceptionUtils.manageExceptionError(e, this);
		// il download ï¿½ una url indipendente e non dentro una porzione
		// di pagina
		target = ERROR;
	    }
	}

	return target;
    }

    /**
     * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
     * 
     * @return helper contenente i dati dell'impresa
     */
    protected WizardDatiImpresaHelper getSessionHelper() {
	WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
		.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
	return helper;
    }

    /**
     * Ritorna la directory per i file temporanei prendendola sempre da
     * javax.servlet.context.tempdir (cartella di default temporanea per il
     * singolo contesto)
     * 
     * @return path alla directory per i file temporanei
     */
    private File getTempDir() {
	return StrutsUtilities.getTempDir(this.getRequest().getSession()
		.getServletContext());
    }

    /**
     * Compone i dati da inviare nella comunicazione ed invia la richiesta al
     * backoffice
     * 
     * @throws ApsException
     */
	private void sendComunicazione(WizardDatiImpresaHelper helper)
			throws ApsException {
		ComunicazioneType comunicazione = null;

		// FASE 1: estrazione dei parametri necessari per i dati di testata
		// della comunicazione

		String username = this.getCurrentUser().getUsername();
		String ragioneSociale = helper.getDatiPrincipaliImpresa()
				.getRagioneSociale();

		Event evento = new Event();
		evento.setUsername(username);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Invio comunicazione "
				+ PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ANAGRAFICA
				+ " in stato "
				+ CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);

		// Invio della comunicazione
		try {
			// FASE 2: costruzione del contenitore della comunicazione e
			// popolamento
			// della testata
			comunicazione = new ComunicazioneType();

			DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.createDettaglioComunicazione(
							helper.getIdComunicazione(),
							username,
							null,
							null,
							ragioneSociale,
							CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
							this.getTextFromDefaultLocale(
									"label.aggiornamentoAnagrafica.oggetto",
									username),
							this.getTextFromDefaultLocale(
									"label.aggiornamentoAnagrafica.testo",
									StringUtils.left(ragioneSociale, 200)),
							PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ANAGRAFICA,
							null);
			comunicazione.setDettaglioComunicazione(dettaglioComunicazione);

			// FASE 3: creazione e popolamento dell'allegato con l'xml dei dati
			// da
			// inviare
			this.setAllegatoComunicazione(
					comunicazione,
					helper.getXmlDocumentAggiornamentoAnagrafica(),
					PortGareSystemConstants.NOME_FILE_AGG_ANAGRAFICA,
					this.getTextFromDefaultLocale("label.aggiornamentoAnagrafica.allegato.descrizione"));

			// FASE 4: invio comunicazione
			comunicazione.getDettaglioComunicazione()
					.setId(this._comunicazioniManager
							.sendComunicazione(comunicazione));

			evento.setMessage("Invio comunicazione "
					+ PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ANAGRAFICA
					+ " con id "
					+ comunicazione.getDettaglioComunicazione().getId()
					+ " in stato "
					+ comunicazione.getDettaglioComunicazione().getStato());
			
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} catch (IOException e) {
			evento.setError(e);
			throw new ApsException(e.getMessage());
		} finally {
			this.eventManager.insertEvent(evento);
		}

		// FASE 5: settaggio della protezione in modo da bloccare eventuali
		// nuovi invii con gli stessi dati
		helper.setDatiInviati(true);
	}

	/**
	 * imposta gli allegati della comunicazione 
	 */
	private void setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			XmlObject xmlDocument,
			String nomeFile, 
			String descrizioneFile) throws IOException {
		
		AllegatoComunicazioneType allegatoAggAnagr = ComunicazioniUtilities
			.createAllegatoComunicazione(
					nomeFile,
					descrizioneFile,
					xmlDocument.toString().getBytes());
		comunicazione.setAllegato(new AllegatoComunicazioneType[] { allegatoAggAnagr });
		
	}
	
    private Map<String, Object> session;
	private DatiImpresaChecker _datiImpresaChecker;
	private IComunicazioniManager _comunicazioniManager;
	private IRegistrazioneImpresaManager _registrazioneImpresaManager;

    private InputStream inputStream;

    @Override
    public void setSession(Map<String, Object> session) {
	this.session = session;
    }

    /**
     * @return the session
     */
    public Map<String, Object> getSession() {
	return session;
    }

	/**
	 * @param datiImpresaChecker the datiImpresaChecker to set
	 */
	public void setDatiImpresaChecker(DatiImpresaChecker datiImpresaChecker) {
		this._datiImpresaChecker = datiImpresaChecker;
	}

    /**
     * @param manager
     *            the _comunicazioniManager to set
     */
	public void setComunicazioniManager(IComunicazioniManager manager) {
		this._comunicazioniManager = manager;
	}
    
    /**
	 * @param manager the manager to set
	 */
	public void setRegistrazioneImpresaManager(
			IRegistrazioneImpresaManager manager) {
		this._registrazioneImpresaManager = manager;
	}

	/**
     * @return the inputStream
     */
    public InputStream getInputStream() {
	return inputStream;
    }

}