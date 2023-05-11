package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
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
 * Action di salvataggio dell'impresa che richiede la registrazione
 * 
 * @author Stefano.Sabbadin
 */
public class RegistrazioneImpresaAction extends BaseAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -8933279873028742963L;
    
    private Map<String, Object> session;
    private IComunicazioniManager comunicazioniManager;
    private IRegistrazioneImpresaManager registrazioneImpresaManager;
	private IEventManager eventManager;
	private IAppParamManager appParamManager;
	private ICustomConfigManager customConfigManager;
    
    private InputStream inputStream;
	@Validate(EParamValidation.EMAIL)
    private String mail;
    
    @Override
    public void setSession(Map<String, Object> session) {
	   this.session = session;
    }

    public Map<String, Object> getSession() {
    	return session;
    }

    public void setComunicazioniManager(IComunicazioniManager manager) {
    	comunicazioniManager = manager;
    }

    public void setRegistrazioneImpresaManager(IRegistrazioneImpresaManager registrazioneImpresaManager) {
		this.registrazioneImpresaManager = registrazioneImpresaManager;
	}
    
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

    public InputStream getInputStream() {
        return inputStream;
    }

	public String getMail() {
		return mail;
	}
	
	
	/**
	 * Invia la richiesta di registrazione al backoffice
	 */
	public String send() {
		String target = SUCCESS;

		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);

		if (registrazioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			Integer version = (Integer) this.appParamManager.getConfigurationValue(AppParamManager.CLAUSOLEPIATTAFORMA_VERSIONE);
			
			XmlOptions validationOptions = new XmlOptions();
		    ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
		    validationOptions.setErrorListener(validationErrors);
		    
		    boolean esitoCheckSintassi = registrazioneHelper.getXmlDocumentRegistrazione(this.getI18nManager(), this.getLocale()).validate(validationOptions);
			
			if (!esitoCheckSintassi) {
				this.addActionError(this.getText("Errors.checkDatiObbligatori"));
		    	String errmsg = "";
		    	for(int i = 0; i < validationErrors.size(); i++) {
		    		errmsg += validationErrors.get(i).getMessage() + "\n";
		    	}
		    	ApsSystemUtils.getLogger().error(this.getText("Errors.checkDatiObbligatori") + "\n" + errmsg); 
				target = "errorReg";
			} else {
				// se la registrazione impresa e' in attesa di validazione 
				// (quindi con comunicazione gia' presente in stato BOZZA)
				// allore vanno eliminati eventuali dati di tentativi di 
				// registrazione precepresenti
				if(registrazioneHelper.isAttesaValidazione()) {
					this.registrazioneImpresaManager.deleteImpresa(registrazioneHelper.getUsername());
				}
				
				EsitoOutType esito = null;
				Event evento = new Event();
				evento.setUsername(registrazioneHelper.getUsername());
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setEventType(PortGareEventsConstants.CREAZIONE_ACCOUNT);
				evento.setLevel(Event.Level.INFO);
				String message = "Registrazione account per impresa con mail di riferimento "
						+ registrazioneHelper.getMailUtenteImpostata()
						+ " e ragione sociale "	+ registrazioneHelper.getDatiPrincipaliImpresa().getRagioneSociale()
						+ ", richiesta da "	+ registrazioneHelper.getSoggettoRichiedente();
				if(version != null) {
					String ver = " (versione consensi n. " + version + ")";
					message = StringUtils.substring(message, 0, 500 - ver.length()) + ver;
				}
				evento.setMessage(StringUtils.substring(message, 0, 500));
				
				try {
					this.mail = DatiImpresaChecker.getEmailRiferimento(
							registrazioneHelper.getDatiPrincipaliImpresa().getEmailPECRecapito(), 
							registrazioneHelper.getDatiPrincipaliImpresa().getEmailRecapito());
					
					String codiceFiscaleDelegato = null;
					AccountSSO accountSSO = (AccountSSO)this.getSession().get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
					
					if (accountSSO != null) {
						codiceFiscaleDelegato = accountSSO.getLogin();
					}
					if(StringUtils.isEmpty(codiceFiscaleDelegato ) && 
							PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(registrazioneHelper.getTipologiaRegistrazione())){
						codiceFiscaleDelegato = registrazioneHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
					}
					boolean isPivaGroup = WizardDatiImpresaHelper.isVatGroup(registrazioneHelper.getDatiPrincipaliImpresa().getVatGroup());
					esito = this.registrazioneImpresaManager.registerImpresa(
							registrazioneHelper.getUsername(), 
							this.mail,
							registrazioneHelper.getDatiPrincipaliImpresa().getCodiceFiscale(),
							registrazioneHelper.getDatiPrincipaliImpresa().getPartitaIVA(),
							codiceFiscaleDelegato,
							isPivaGroup);
					if (esito.isEsitoOk()) {
						this.sendComunicazione(registrazioneHelper);
					} else {
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage("Codice errore: " + esito.getCodiceErrore());
						this.addActionError(this.getText(
								"Errors.userRegistration.unexpectedError",
								new String[] { esito.getCodiceErrore() }));
						target = "errorReg";
					}
				} catch (Throwable t) {
					// se fallisce l'interfacciamento con il WS, allora elimino
					// l'utenza creata
					if (esito != null && esito.isEsitoOk()) {
						this.registrazioneImpresaManager
								.deleteImpresa(registrazioneHelper.getUsername());
					}
					ApsSystemUtils.logThrowable(t, this, "save");
					ExceptionUtils.manageExceptionError(t, this);
					target = "errorReg";
					evento.setError(t);
				} finally{
					eventManager.insertEvent(evento);
				}
			}
		}
		return target;
	}

    public String cancel() {
    	return "cancel";
    }

    public String back() {
    	return "back";
    }

    public String createPdf() {
		String target = "successCreatePdf";
	
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
	
		if (registrazioneHelper == null) {
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
		    	registrazioneHelper.getTempFiles().add(filePdf);

		    } catch (JRException e) {
		    	ApsSystemUtils.logThrowable(e, this, "createPdf");
		    	ExceptionUtils.manageExceptionError(e, this);
		    	// il download e' una url indipendente e non dentro una porzione
		    	// di pagina
		    	target = ERROR;
		    } catch (IOException e) {
		    	ApsSystemUtils.logThrowable(e, this, "createPdf");
		    	ExceptionUtils.manageExceptionError(e, this);
		    	// il download e' una url indipendente e non dentro una porzione
		    	// di pagina
		    	target = ERROR;
		    }
		}
	
		return target;
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
     * @throws Exception 
     */
	private void sendComunicazione(
			WizardRegistrazioneImpresaHelper registrazioneHelper)
			throws Exception 
	{
		ComunicazioneType comunicazione = null;
				
		// FASE 1: 
		// estrazione dei parametri necessari per i dati di testata della comunicazione
		boolean verificaManuale = this.customConfigManager.isActiveFunction("REG-IMPRESA", "VERIFICAMANUALE");
		
		String statoComunicazione = (verificaManuale
			? CommonSystemConstants.STATO_COMUNICAZIONE_ATTESA_VERIFICA
			: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		
		String username = registrazioneHelper.getUsername();
		String ragioneSociale = registrazioneHelper.getDatiPrincipaliImpresa()
				.getRagioneSociale();

		Event evento = new Event();
		evento.setUsername(username);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Invio comunicazione "
				+ PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE
				+ " in stato " + statoComunicazione);

		// Invio della comunicazione
		try {
			// FASE 2: 
			// costruzione del contenitore della comunicazione e popolamento della testata
			if(verificaManuale) {
				// verifica se esiste gi� la comunicazione di registrazione in stato BOZZA
				DettaglioComunicazioneType criteri = new DettaglioComunicazioneType();
				criteri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				criteri.setChiave1(username);
				criteri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE);
				criteri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				
				List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteri);
				if(comunicazioni != null) {
					if(comunicazioni.size() == 1) {
						// comunicazione in stato BOZZA trovata...
						comunicazione = this.comunicazioniManager.getComunicazione(
								CommonSystemConstants.ID_APPLICATIVO, 
								comunicazioni.get(0).getId());
						comunicazione.getDettaglioComunicazione().setStato(statoComunicazione);
					} else if(comunicazioni.size() > 1) {
						// ERRORE: pi� comunicazioni trovate o comunicazione non trovata
						throw new Exception("piu' comunicazioni trovate o comunicazione non trovata");
					}
				}
			}
			
			// crea un nuova comunicazione per la registrazione
			if(comunicazione == null) {
				comunicazione = new ComunicazioneType();
				DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
						.createDettaglioComunicazione(
								null,
								username,
								null,
								null,
								ragioneSociale,
								statoComunicazione,
								this.getTextFromDefaultLocale("label.registrazione.oggetto"),
								this.getTextFromDefaultLocale("label.registrazione.testo", StringUtils.left(ragioneSociale, 200)),
								PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE,
								null);
				comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
			}
			
			// FASE 3: 
			// creazione e popolamento dell'allegato con l'xml dei dati da inviare
			this.setAllegatoComunicazione(
					comunicazione,
					registrazioneHelper.getXmlDocumentRegistrazione(this.getI18nManager(), this.getLocale()),
					PortGareSystemConstants.NOME_FILE_REGISTRAZIONE,
					this.getTextFromDefaultLocale("label.registrazione.allegatoRegistrazione.descrizione"));

			// FASE 4: 
			// invio comunicazione
			comunicazione.getDettaglioComunicazione()
				.setId(this.comunicazioniManager.sendComunicazione(comunicazione));
			
			// FASE 5: 
			// se tutto e' andato a buon fine si eliminano le informazioni
			this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
			this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);

			evento.setMessage("Invio comunicazione "
					+ PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE
					+ " con id " + comunicazione.getDettaglioComunicazione().getId()
					+ " in stato " + comunicazione.getDettaglioComunicazione().getStato());
			
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} catch (Throwable e) {
			evento.setError(e);
			throw new ApsException("Errore inaspettato in registrazione impresa", e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * imposta gli allegati della comunicazione
	 */
	private void setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			XmlObject xmlDocument,
			String nomeFile, String descrizioneFile) 
	{	
		AllegatoComunicazioneType allegatoReg = ComunicazioniUtilities
				.createAllegatoComunicazione(
						nomeFile,
						descrizioneFile,
						xmlDocument.toString().getBytes());
		comunicazione.setAllegato(new AllegatoComunicazioneType[] { allegatoReg });
	}

}