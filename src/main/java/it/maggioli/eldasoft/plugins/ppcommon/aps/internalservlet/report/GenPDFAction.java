package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.TokenInterceptor;
import it.eldasoft.sil.portgare.datatypes.AltriDatiAnagraficiType;
import it.eldasoft.sil.portgare.datatypes.CameraCommercioType;
import it.eldasoft.sil.portgare.datatypes.CategoriaType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaType;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.INAILType;
import it.eldasoft.sil.portgare.datatypes.INPSType;
import it.eldasoft.sil.portgare.datatypes.ISO9001Type;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoEstesoType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneWhitelistAntimafiaType;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.ListaStazioniAppaltantiType;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaType;
import it.eldasoft.sil.portgare.datatypes.SOAType;
import it.eldasoft.sil.portgare.datatypes.StazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomReportManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiUlterioriImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiUlterioriImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Classe generica per la generazione di un report PDF
 * Fornisce i seguenti metodi astratti per l'inizializzazione, parametrizzazione 
 * e completamento del report, da definire (anche vuoi) nella classe figlia :   
 *
 * 		reportInit()
 * 			valida ed inizializza la creazione del report
 * 
 *		reportParametersInit(HashMap<String, Object> params)
 *			inizializza i parametri del report
 *
 *		reportCompleted(File pdf)
 *			completa la generazione del report fornendo il file prodotto e 
 *          l'uuid ad esso associato
 *          
 *		XmlObject generaXmlConDatiTipizzati()
 *			decodifica in chiaro con la corrispondente descrizione, il valore 
 *			dei campi 
 *
 * Il file di configurazione xml della action che eredita questa classe, deve fare 
 * riferimento al metodo 
 * 
 *		String createPdf()
 * 
 * che verrà associato alla generazione del pdf.
 * 
 * @author 
 * @since 
 */
public abstract class GenPDFAction extends EncodedDataAction implements SessionAware, IDownloadAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2988587434268058443L;

	protected Map<String, Object> session;
	protected IAppParamManager appParamManager;
	protected ICustomConfigManager customConfigManager;	
	protected ICustomReportManager customReportManager;
	protected IEventManager eventManager;

	@Validate(EParamValidation.FILE_NAME)
	protected String reportName;			// nome del report o del custom report
	@Validate(EParamValidation.GENERIC)
	protected String standardReportName; 	// nome dello standard report
	@Validate(EParamValidation.GENERIC)
	protected String reportHeaderName;		// nome del report header o del custom report header
	@Validate(EParamValidation.GENERIC)
	protected String reportFooterName;		// nome del report footer o del custom report footer
	@Validate(EParamValidation.GENERIC)
	protected String xmlRootNode;			// nodo radice del documento xml contenente i dati del report
				
	protected InputStream inputStream;		// stream associato al pdf generato e restituito dalla action
	@Validate(EParamValidation.FILE_NAME)
	protected String filename;				// filename del pdf generato
	@Validate(EParamValidation.UUID)
	protected String uuid;					// UUID associato al pdf generato
	@Validate(EParamValidation.URL)
	protected String urlPage;				// ...
	@Validate(EParamValidation.DIGIT)
	protected String currentFrame;			// ...
	@Validate(EParamValidation.GENERIC)
	protected String jsonSource;			// dati del report in formato JSON
	@ValidationNotRequired
	private String subFolderTemplate; 	// SUBFOLDER_TEMPLATE_JASPER
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setCustomReportManager(ICustomReportManager customReportManager) {
		this.customReportManager = customReportManager;
	}
		
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
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
		
	public String getUuid() {
		return uuid;
	}
	
	public String getJsonSource() {
		return jsonSource;
	}

	public void setJsonSource(String jsonSource) {
		this.jsonSource = jsonSource;
	}

	/**
	 * costruttore generico da utilizzare nelle classi derivate
	 */
	public GenPDFAction(
			String reportName,
			String reportHeaderName,
			String reportFooterName,
			String xmlRootNode) 
	{
		this.reportName = reportName;
		this.reportHeaderName = reportHeaderName;
		this.reportFooterName = reportFooterName;
		this.xmlRootNode = xmlRootNode;
		this.standardReportName = reportName;
		this.subFolderTemplate = null;
		this.filename = null;	 // usa reportNome come nome standard per il file pdf
		this.uuid = null;
		this.jsonSource = null;
	}
	
	public GenPDFAction(
			String reportName,
			String xmlRootNode) 
	{
		this(reportName, null, null, xmlRootNode);
	}

	public GenPDFAction() {
		this(null, null, null, null);
	}

	/**
	 * Validazione prima di poter generare il report  
	 */
	protected abstract boolean reportInit();
	
	/**
	 * Inizializza i parametri del report prima della generazione
	 */
	protected abstract void reportParametersInit(HashMap<String, Object> params) throws Exception;

	/**
	 * Eseguito al completamento della generazione del report  
	 */
	protected abstract void reportCompleted();

	/**
	 * Crea il documento xml da collegare alla generazione del report pdf
	 * Qui vanno inserite del decodifiche in chiaro dei valori corrispondenti a 
	 * codici.
	 */
	protected abstract XmlObject generaXmlConDatiTipizzati() throws Exception;

	/**
	 * Generazione del report PDF
	 * 
	 * In caso di report customizzato, inserire nel metodo della classe figlia 
	 * l'inizializzazione di "reportName" con il nome del report custom.
	 *   
	 */
	public String createPdf() {
		this.setTarget(SUCCESS);
		
		if(reportInit()) {
			
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.subFolderTemplate = StringUtils
				.stripToNull((String) this.appParamManager
					.getConfigurationValue(AppParamManager.SUBFOLDER_TEMPLATE_JASPER));
			
			// se subFolderTemplate � NULL o vuoto, 
			// allora si utilizza il folder standard plugins/ppgare/aps/jasper/
			if(this.subFolderTemplate == null) {
				this.subFolderTemplate = "";
			}
			
			try {
				// in caso di customizzato se reportName non viene valorizzato
				// allora si utilizza il report standard
				String rptPath = PortGareSystemConstants.GARE_JASPER_FOLDER;
				
				if(this.reportName == null) {
					// utilizza il report standard...
					this.reportName = this.standardReportName;
				} else {
				// utilizza il report standard o il report custom...
				// se presente utilizza SUBFOLDER_TEMPLATE_JASPER 
				if (StringUtils.isNotEmpty(this.subFolderTemplate)) {
					rptPath = PortGareSystemConstants.GARE_JASPER_FOLDER + 
							this.subFolderTemplate + "/";  
				}	
				}

				// se il report configurato non esiste si utilizza il report standard...
				if (this.getRequest().getSession().getServletContext().getResource(
						rptPath + this.reportName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT) == null) {
					// imposta il report di default...
					rptPath = PortGareSystemConstants.GARE_JASPER_FOLDER;
				}

				if (this.getRequest().getSession().getServletContext().getResource(
						rptPath + this.reportName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT) == null) {
					session.put(ERROR_DOWNLOAD,
								//this.reportName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT + " not found in " + rptPath);
								this.reportName + " report not found");
					this.setTarget(INPUT);
				} else {
					// crea un nuovo UUID e genera su file il nuovo report... 
					//this.uuid = UUID.randomUUID().toString();
					this.generatePdf(this.reportName, rptPath);
				}
			} catch (MalformedURLException e) {
				ApsSystemUtils.logThrowable(e, this, "createPdf");
				session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));
				this.setTarget(INPUT);
			} catch (Exception e) {
				ApsSystemUtils.logThrowable(e, this, "createPdf");
				session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "createPdf");
				session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { t.getMessage()}));
			}
		}
		
		// in caso di errore ed eventuale "redirect" e' necessario
		// effettuare il redirect del token...
		if( !SUCCESS.equals(this.getTarget()) ) {
			TokenInterceptor.redirectToken();
		}

		return this.getTarget();
	}

	/**
	 * Genera fisicamente il PDF a partire da un documento XML generato da 
	 * generaXmlConDatiTipizzati()
	 * 
	 * @param reportName
	 *            nome del report da lanciare
	 * @param reportPath
	 * 			  path dove si trova il template del report (.jasper) 
	 */
	private void generatePdf(
			String reportName, 
			String reportPath) 
	{
		//File filePdf = null;
		try {
			// FASE 1: si genera il documento XML/JSON
			XmlObject xmlSource = null;
			if(StringUtils.isNotEmpty(this.jsonSource)) {
				// sorgente dati in formato JSON
			} else {
				// sorgente dati in formato XML
				xmlSource = generaXmlConDatiTipizzati();
			}
			
			// FASE 2: si genera il report in area temporanea
			javax.servlet.ServletContext ctx = this.getRequest().getSession().getServletContext();
			
			// si determina il nome del file di destinazione nell'area temporanea
//			String nomePdf = FileUploadUtilities.generateFileName() + ".pdf";
//			filePdf = new File(
//					StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext()).getAbsolutePath() +
//					File.separatorChar + nomePdf);
			
			if(StringUtils.isEmpty(this.filename)) {
				this.filename = this.reportName + ".pdf";
			} else {
				this.filename = this.filename + ".pdf";
			} 
			
			String selectExpression = this.xmlRootNode;
			
			// si carica il report jasper, lo si filla con i dati e si genera il pdf...
			// Controllo se esistono subreport specifici
			// verifica header e footer...
			if(this.reportHeaderName == null || StringUtils.isEmpty(this.reportHeaderName)) {
				this.reportHeaderName = this.reportName + "Header";
			}
			if(this.reportFooterName == null || StringUtils.isEmpty(this.reportFooterName)) {
				this.reportFooterName = this.reportName + "Footer";
			}
//			boolean useSpecificHeader = false;
//			boolean useSpecificFooter = false; 

			if(this.subFolderTemplate == null || (this.subFolderTemplate != null && "".equals(this.subFolderTemplate))) {  
				this.subFolderTemplate = "";
			} else {
				this.subFolderTemplate += File.separator;
			}
			
			String specificHeaderFile = ctx.getRealPath(
							PortGareSystemConstants.GARE_JASPER_FOLDER +
							this.subFolderTemplate + "subreports" + File.separator + 
							this.reportHeaderName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT);
			String specificFooterFile = ctx.getRealPath(
							PortGareSystemConstants.GARE_JASPER_FOLDER +
							this.subFolderTemplate + "subreports" + File.separator + 
							this.reportFooterName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT);
			
			File subreportHeaderFile = new File(specificHeaderFile);
			if (subreportHeaderFile.exists()) {
//				useSpecificHeader = true;
			} else {
				// usa header standard...
				specificHeaderFile = 
					ctx.getRealPath(PortGareSystemConstants.GARE_JASPER_FOLDER) + //File.separator +
					"subreports" + File.separator +
					this.reportHeaderName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT;
			}
			
			File subreportFooterFile = new File(specificFooterFile);
			if (subreportFooterFile.exists()) {
//				useSpecificFooter = true;
			} else {
				// usa footer standard...
				specificFooterFile = 
					ctx.getRealPath(PortGareSystemConstants.GARE_JASPER_FOLDER) + //File.separator +
					"subreports" + File.separator +
					this.reportFooterName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT;
			}

			// prepara i path per "report", "subreport" e "images"...
			String reportDir = reportPath; 
			InputStream isJasper = ctx.getResourceAsStream(
					reportPath + reportName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT);
			
			if(isJasper == null) {
				// utilizza il report standard
				reportDir = PortGareSystemConstants.GARE_JASPER_FOLDER;
				isJasper = ctx.getResourceAsStream(
						reportDir + reportName + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT);
			} 
			String subReportDir = reportDir + "subreports";
			String imagesDir = reportDir + "images";
			
			// prepara i parametri del report... 
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(PortGareSystemConstants.JASPER__SUBREPORT_DIR, ctx.getRealPath(subReportDir) + File.separator); 		
			params.put(PortGareSystemConstants.IMAGES_DIR, ctx.getRealPath(imagesDir) + File.separator);
			params.put("ROOT_TAG", selectExpression);
			params.put("SUBREPORT_DIR", ctx.getRealPath(subReportDir) + File.separator);
			params.put("REPORT_HEADER", specificHeaderFile);
			params.put("REPORT_FOOTER", specificFooterFile);
			this.reportParametersInit(params);

			// crea il report...
		    JRAbstractTextDataSource jrds = null;
		    if(StringUtils.isNotEmpty(this.jsonSource)) {
		    	// JSON SOURCE
		    	jrds = new JsonDataSource(
			    		new ByteArrayInputStream(this.jsonSource.getBytes("UTF-8")), 
			    		selectExpression
			    );
		    } else {
		    	// XML SOURCE
				jrds = new JRXmlDataSource(
						new ByteArrayInputStream(xmlSource.toString().getBytes("UTF-8")), 
						selectExpression
				);
		    }
		    jrds.setLocale(Locale.ITALIAN);
		    jrds.setDatePattern("yyyy-MM-dd");
		    JasperPrint print = JasperFillManager.fillReport(isJasper, params, jrds);
		    
		    if(print == null) {
		    	// ERRORE
		    	ApsSystemUtils.getLogger().error("Invalid or empty datasource.");
		    } else {
		    	// genera il PDF (JasperReport 6.16.0)...
		    	JRPdfExporterEldasoft exporter = JRPdfExporterEldasoft.newInstance()
		     			.setReportName(reportName)
		     			.setPdfACompliance(customConfigManager.isActiveFunction("PDF", "PDF-A", false))
		     			.setPdfUACompliance(customConfigManager.isActiveFunction("PDF", "PDF-UA", false))
		     			.setPrint(print)
		     			.setAction(this);
				exporter.exportReport();
				
		     	this.uuid = exporter.getUuid();
		     	ByteArrayOutputStream baosPdf = exporter.getOutputStream(); 		     	
				this.inputStream = new ByteArrayInputStream(baosPdf.toByteArray());
				
				this.reportCompleted();
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "createPdf");
			ExceptionUtils.manageExceptionError(e, this);
			session.put(ERROR_DOWNLOAD, this.getActionErrors().toArray()[0]);
			this.setTarget(INPUT);
	    } catch (OutOfMemoryError e) {
			ApsSystemUtils.logThrowable(e, this, "createPdf");
			session.put(ERROR_DOWNLOAD, this.getText("Errors.pdf.outOfMemory"));
			this.setTarget(INPUT);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createPdf");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(INPUT);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	// esporta un generico report come PDF 
	// (vedi ProcessPageRiepilogoImpresaAction, ProcessPafeEsitoAction, ...)
	////////////////////////////////////////////////////////////////////////////
	public static byte[] createPdf(
			InputStream jasperStream
			, String xml
			, String xmlSelectExpression
	) throws UnsupportedEncodingException, JRException {
		
		//PDFUA Accessibility Compliance
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		configuration.setTagged(true);
		configuration.setTagLanguage(Locale.ITALIAN.getLanguage());
		configuration.setMetadataTitle("Report PDF");
		//configuration.setMetadataAuthor(ragione sociale ditta);
		//configuration.setOwnerPassword(...);
		//configuration.setUserPassword(...)
		
		// genera il PDf...
		ByteArrayOutputStream baosPdf = new ByteArrayOutputStream();
		JRXmlDataSource jrxmlds = new JRXmlDataSource(new ByteArrayInputStream(xml.getBytes("UTF-8")), xmlSelectExpression);
		JasperPrint print = JasperFillManager.fillReport(jasperStream, new HashMap<String, Object>(), jrxmlds);
		
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		jasperPrintList.add(print);
		
		JRPdfExporterEldasoft exporter = new JRPdfExporterEldasoft();
		exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baosPdf));
		exporter.setConfiguration(configuration);
		exporter.exportReport();

		return baosPdf.toByteArray();
	}
	
	////////////////////////////////////////////////////////////////////////////
	// Metodi generali per la decodifica dei codici dai loro tabellati
	////////////////////////////////////////////////////////////////////////////
	/**
	 * decodifica in chiaro un firmatario
	 */
	protected void decodeFirmatario(
			FirmatarioType firmatario) 
	{
		firmatario.setSesso(getMaps()
				.get(InterceptorEncodedData.LISTA_SESSI).get(firmatario.getSesso()));
		
		if(StringUtils.isNotBlank(firmatario.getQualifica()) && 
		   !"Libero professionista".equalsIgnoreCase(firmatario.getQualifica())) {
			
			if(String.valueOf(firmatario.getQualifica().charAt(0)).equals(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO)) {
				firmatario.setQualifica(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO + 
						     CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
				
			} else if(String.valueOf(firmatario.getQualifica().charAt(0)).equals(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE)) {
				firmatario.setQualifica(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE + 
						     CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else {
				firmatario.setQualifica(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
						.get(firmatario.getQualifica()));
			}
		} else {
			firmatario.setQualifica("libero professionista");
		}
		
		if (StringUtils.isNotEmpty(firmatario.getTipoImpresa())) {
			firmatario.setTipoImpresa(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO)
					.get(firmatario.getTipoImpresa()));
		}
	}

	/**
	 * decodifica in chiaro un referente impresa
	 */
	protected void decodeReferenteImpresa(
			ReferenteImpresaType referente) 
	{
		referente.setSesso(getMaps()
				.get(InterceptorEncodedData.LISTA_SESSI).get(referente.getSesso()));

		if (referente.isSetTitolo())
			referente.setTitolo(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO)
					.get(referente.getTitolo()));

		if (referente.getAlboProfessionale().isSetTipologia())
			referente.getAlboProfessionale().setTipologia(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_ALBO_PROFESSIONALE)
					.get(referente.getAlboProfessionale().getTipologia()));
		
		if (referente.getAlboProfessionale().isSetProvinciaIscrizione())
			referente.getAlboProfessionale().setProvinciaIscrizione(getMaps()
					.get(InterceptorEncodedData.LISTA_PROVINCE)
					.get(referente.getAlboProfessionale().getProvinciaIscrizione()));

		if (referente.getCassaPrevidenza().isSetTipologia())
			referente.getCassaPrevidenza().setTipologia(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_CASSA_PREVIDENZA)
					.get(referente.getCassaPrevidenza().getTipologia()));
	}

	/**
	 * decodifica in chiaro i dati di un'impresa
	 */	
	@SuppressWarnings("unlikely-arg-type")
	protected void decodeDatiImpresa(
			DatiImpresaType datiImpresa, 
			boolean isLiberoProfessionista) 
	{
		if (datiImpresa.getImpresa().getNaturaGiuridica() != null) {
			datiImpresa.getImpresa().setNaturaGiuridica(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_NATURA_GIURIDICA)
					.get(datiImpresa.getImpresa().getNaturaGiuridica()));
		}

		if (datiImpresa.getImpresa().getTipoImpresa() != null) {
			datiImpresa.getImpresa().setTipoImpresa(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO)
					.get(datiImpresa.getImpresa().getTipoImpresa()));
		}
		
		if (StringUtils.isNotBlank(datiImpresa.getImpresa().getSettoreProduttivo())) {
			datiImpresa.getImpresa().setSettoreProduttivo(getMaps()
					.get(InterceptorEncodedData.LISTA_SETTORI_PRODUTTIVI)
					.get(datiImpresa.getImpresa().getSettoreProduttivo()));
		}

		IndirizzoEstesoType[] indirizzi = datiImpresa.getImpresa().getIndirizzoArray();
		for (int i = 0; i < indirizzi.length; i++) {
			IndirizzoEstesoType indirizzo = indirizzi[i];
			indirizzo.setTipoIndirizzo(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_INDIRIZZO)
					.get(indirizzo.getTipoIndirizzo()));
		}

		CameraCommercioType cciaa = datiImpresa.getImpresa().getCciaa();
		cciaa.setProvinciaIscrizione(getMaps()
				.get(InterceptorEncodedData.LISTA_PROVINCE)
				.get(cciaa.getProvinciaIscrizione()));

		SOAType soa = datiImpresa.getImpresa().getSoa();
		soa.setOrganismoCertificatore(getMaps()
				.get(InterceptorEncodedData.LISTA_CERTIFICATORI_SOA)
				.get(soa.getOrganismoCertificatore()));

		ISO9001Type iso9001 = datiImpresa.getImpresa().getIso9001();
		iso9001.setOrganismoCertificatore(getMaps()
				.get(InterceptorEncodedData.LISTA_CERTIFICATORI_ISO)
				.get(iso9001.getOrganismoCertificatore()));

		if (datiImpresa.getImpresa().getRegimeFiscale() != null) {
			datiImpresa.getImpresa().setRegimeFiscale(getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_REGIME_FISCALE)
					.get(datiImpresa.getImpresa().getRegimeFiscale()));	
		}
		
		if (datiImpresa.getImpresa().getSettoreAttivitaEconomica() != null) {
			datiImpresa.getImpresa().setSettoreAttivitaEconomica(getMaps()
					.get(InterceptorEncodedData.LISTA_SETTORE_ATTIVITA_ECONOMICA)
					.get(datiImpresa.getImpresa().getSettoreAttivitaEconomica()));	
		}		
		
		if (datiImpresa.getImpresa().getClasseDimensione() != null) {
			datiImpresa.getImpresa().setClasseDimensione(getMaps()
					.get(InterceptorEncodedData.LISTA_CLASSI_DIMENSIONE)
					.get(datiImpresa.getImpresa().getClasseDimensione()));
		}
		
		// whitelist antimafia 
		IscrizioneWhitelistAntimafiaType whitelist = datiImpresa.getImpresa()
			.getIscrizioneWhitelistAntimafia();

		String sezioniIscrizione = null;
		String[] sezioni = WizardDatiUlterioriImpresaHelper
			.getSezioniIscrizioneFromBO(whitelist.getSezioniIscrizione());
		if(sezioni != null) {
			sezioniIscrizione = "";
			for(int i = 0; i < sezioni.length; i++) {
				sezioniIscrizione += sezioni[i] + " - " + getMaps()
						.get(InterceptorEncodedData.LISTA_SEZIONI_WHITELIST_ANTIMAFIA)
						.get(sezioni[i]) + "\n";
			}
		}
		whitelist.setSezioniIscrizione(sezioniIscrizione);

		// Rating di legalit� (DL 1/2012) (19/03/2020- mod. vers. 26)
		if (datiImpresa.getImpresa().getRatingLegalita() != null &&
			StringUtils.isNotBlank(datiImpresa.getImpresa().getRatingLegalita().getRating()))
		{
			datiImpresa.getImpresa().getRatingLegalita().setRating(getMaps()
					.get(InterceptorEncodedData.LISTA_RATING_LEGALE)
					.get(datiImpresa.getImpresa().getRatingLegalita().getRating()));
		}	

		if (!isLiberoProfessionista) {
			// NON LIBERO PROFESSIONISTA
			
			// legali rappresentanti
			ReferenteImpresaType[] referenti = datiImpresa.getLegaleRappresentanteArray();
			for (int i = 0; i < referenti.length; i++) {
				ReferenteImpresaType ref = referenti[i];
				decodeReferenteImpresa(ref);
				ref.setQualifica(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE +
							 CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			}

			// direttori tecnici
			referenti = datiImpresa.getDirettoreTecnicoArray();
			for (int i = 0; i < referenti.length; i++) {
				ReferenteImpresaType ref = referenti[i];
				decodeReferenteImpresa(ref);
				ref.setQualifica(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
								+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			}

			// altre cariche
			referenti = datiImpresa.getAltraCaricaArray();
			for (int i = 0; i < referenti.length; i++) {
				ReferenteImpresaType ref = referenti[i];
				decodeReferenteImpresa(ref);
				if (ref.isSetQualifica())
					ref.setQualifica(getMaps()
							.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
							.get(CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA
									+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI
									+ ref.getQualifica()));
			}

			// collaboratori
			referenti = datiImpresa.getCollaboratoreArray();
			for (int i = 0; i < referenti.length; i++) {
				ReferenteImpresaType ref = referenti[i];
				decodeReferenteImpresa(ref);
				if (ref.isSetQualifica())
					ref.setQualifica(getMaps()
							.get(InterceptorEncodedData.LISTA_TIPI_COLLABORAZIONE)
							.get(CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE
									+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI
									+ ref.getQualifica()));
			}
		} else {
			// LIBERO PROFESSIONISTA
			AltriDatiAnagraficiType altriDati = datiImpresa.getImpresa().getAltriDatiAnagrafici();
			altriDati.setSesso(getMaps()
					.get(InterceptorEncodedData.LISTA_SESSI)
					.get(altriDati.getSesso()));

			if (altriDati.isSetTitolo())
				altriDati.setTitolo(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO)
						.get(altriDati.getTitolo()));

			if (altriDati.getAlboProfessionale().isSetTipologia())
				altriDati.getAlboProfessionale().setTipologia(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_ALBO_PROFESSIONALE)
						.get(altriDati.getAlboProfessionale().getTipologia()));

			if (altriDati.getAlboProfessionale().isSetProvinciaIscrizione())
				altriDati.getAlboProfessionale().setProvinciaIscrizione(getMaps()
						.get(InterceptorEncodedData.LISTA_PROVINCE)
						.get(altriDati.getAlboProfessionale().getProvinciaIscrizione()));

			if (altriDati.getCassaPrevidenza().isSetTipologia())
				altriDati.getCassaPrevidenza().setTipologia(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_CASSA_PREVIDENZA)
						.get(altriDati.getCassaPrevidenza()));
		}		
	}	

	/**
	 * decodifica in chiaro le categorie di un bando di iscrizione 
	 */
	protected void decodeCategorieBandoIscrizione(
			ListaCategorieIscrizioneType categorie, 
			CategoriaBandoIscrizioneType[] categorieBando) 
	{
		if(categorie != null) {
			for (int i = 0; i < categorie.getCategoriaArray().length; i++) {
				CategoriaType cat = categorie.getCategoriaArray()[i];
				
				// {Lavori|Forniture|Servizi} - [TITOLO]*[LIVELLO]*{0|1}*([CODICE_CATEGORIA])[DESCRIZIONE_CATEGORIA]
				String tipoAppalto = null;
				for (int j = 0; j < categorieBando.length; j++) {
					if (categorieBando[j].getCodice().equals(cat.getCategoria())) {
						tipoAppalto = categorieBando[j].getTipoAppalto();
						cat.setCategoria(getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI)
								.get(tipoAppalto) + 
								(StringUtils.stripToNull(categorieBando[j].getTitolo()) != null 
										? " - " + categorieBando[j].getTitolo() 
										: "")
								+ "*"
								+ categorieBando[j].getLivello()
								+ "*"
								+ (categorieBando[j].isFoglia() ? "1" : "0")
								+ "*"
								+ "("
								+ cat.getCategoria()
								+ ") "
								+ categorieBando[j].getDescrizione());
					}
				}
				
				// classificazione minima e massima
				String elenco = null;
				if ("1".equals(tipoAppalto)) {
					// si usa il set completo, tanto si deve associare al codice la descrizione
					elenco = InterceptorEncodedData.LISTA_CLASSIFICAZIONE_LAVORI;
				} else if ("2".equals(tipoAppalto)) {
					elenco = InterceptorEncodedData.LISTA_CLASSIFICAZIONE_FORNITURE;
				} else if ("3".equals(tipoAppalto)) {
					elenco = InterceptorEncodedData.LISTA_CLASSIFICAZIONE_SERVIZI;
				} else if ("4".equals(tipoAppalto)) {
					elenco = InterceptorEncodedData.LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA;
				} else if ("5".equals(tipoAppalto)) {
					elenco = InterceptorEncodedData.LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI;
				}
	
				if (cat.isSetClassificaMinima()) {
					cat.setClassificaMinima(getMaps().get(elenco).get(cat.getClassificaMinima()));
				}
				if (cat.isSetClassificaMassima()) {
					cat.setClassificaMassima(getMaps().get(elenco).get(cat.getClassificaMassima()));
				}
			}
		}
	}

	/**
	 * decodifica in chiaro i documenti richiesti 	 
	 */
	protected void decodeDocumentiRichiesti(
			ListaDocumentiType documenti, 
			List<DocumentazioneRichiestaType> documentiRichiesti) 
	{
		if(documenti != null && documentiRichiesti != null) {
			for (int i = 0; i < documenti.sizeOfDocumentoArray(); i++) {
				DocumentoType doc = documenti.getDocumentoArray()[i];
				if (doc.isSetId()) {
					for (int j = 0; j < documentiRichiesti.size(); j++) {
						if (documentiRichiesti.get(j).getId() == doc.getId()) {
							doc.unsetId();
							doc.setDescrizione(documentiRichiesti.get(j).getNome());
						}
					}
				}
			}
		}
	}

	/**
	 * decodifica in chiaro i partecipanti ad un raggruppamento
	 */
	protected void decodePartecipantiRaggruppamento(
			ListaPartecipantiRaggruppamentoType partecipanti) 
	{
		if(partecipanti != null) {
			PartecipanteRaggruppamentoType[] partecipantiRaggruppamento = partecipanti.getPartecipanteArray();
			for(int i = 0; i < partecipantiRaggruppamento.length; i++) {
				PartecipanteRaggruppamentoType partecipanteRaggruppamento = partecipantiRaggruppamento[i];
				partecipanteRaggruppamento.setTipoImpresa(getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO)
						.get(partecipanteRaggruppamento.getTipoImpresa()));
			}
		}	
	}
	
	/**
	 * decodifica in chiaro le stazioni appaltanti
	 */
	protected void decodeStazioniAppaltanti(ListaStazioniAppaltantiType stazioniAppaltanti) {
		for (int i = 0; i < stazioniAppaltanti.getStazioneAppaltanteArray().length; i++) {
			stazioniAppaltanti.setStazioneAppaltanteArray(
					i,
					getMaps().get(InterceptorEncodedData.LISTA_STAZIONI_APPALTANTI)
							 .get(stazioniAppaltanti.getStazioneAppaltanteArray()[i]));
		}		
	}

	/**
	 * aggiunge ad un documento xml i dati relativi all'impresa 
	 * (ad esempio l'offerta economica o l'offerta tecnica) 
	 */
	protected void addDatiImpresa(
			WizardDatiImpresaHelper datiImpresaHelper,
			DatiImpresaType datiImpresa) 
	{
		IDatiPrincipaliImpresa datiPrincipaliImpresa = datiImpresaHelper.getDatiPrincipaliImpresa();
		IDatiUlterioriImpresa datiUlterioriImpresa = datiImpresaHelper.getDatiUlterioriImpresa();
		
		ImpresaType impresa = datiImpresa.addNewImpresa();
		impresa.setRagioneSociale(datiPrincipaliImpresa.getRagioneSociale());
		impresa.setPartitaIVA(datiPrincipaliImpresa.getPartitaIVA());
		impresa.setCodiceFiscale(datiPrincipaliImpresa.getCodiceFiscale());
		
		impresa.setNaturaGiuridica(datiPrincipaliImpresa.getNaturaGiuridica());
		impresa.setTipoSocietaCooperativa(datiPrincipaliImpresa.getTipoSocietaCooperativa());
		impresa.setSettoreProduttivo(datiUlterioriImpresa.getSettoreProduttivoDURC());
		
		IndirizzoType sedeLegale = impresa.addNewSedeLegale();
		sedeLegale.setIndirizzo(datiPrincipaliImpresa.getIndirizzoSedeLegale());
		sedeLegale.setNumCivico(datiPrincipaliImpresa.getNumCivicoSedeLegale());
		sedeLegale.setCap(datiPrincipaliImpresa.getCapSedeLegale());
		sedeLegale.setComune(datiPrincipaliImpresa.getComuneSedeLegale());
		sedeLegale.setProvincia(datiPrincipaliImpresa.getProvinciaSedeLegale());
		sedeLegale.setNazione(datiPrincipaliImpresa.getNazioneSedeLegale());
		
		RecapitiType recapiti = impresa.addNewRecapiti();
		recapiti.setTelefono(datiPrincipaliImpresa.getTelefonoRecapito());
		recapiti.setFax(datiPrincipaliImpresa.getFaxRecapito());
		recapiti.setEmail(datiPrincipaliImpresa.getEmailRecapito());
		recapiti.setPec(datiPrincipaliImpresa.getEmailPECRecapito());
		
		CameraCommercioType cameraCommercio = impresa.addNewCciaa();
		cameraCommercio.setNumRegistroDitte(datiUlterioriImpresa.getNumRegistroDitteCCIAA());
		cameraCommercio.setDataDomandaIscrizione(CalendarValidator.getInstance()
				.validate(datiUlterioriImpresa.getDataDomandaIscrizioneCCIAA(), "dd/MM/yyyy"));
		cameraCommercio.setNumIscrizione(datiUlterioriImpresa.getNumIscrizioneCCIAA());
		cameraCommercio.setDataIscrizione(CalendarValidator.getInstance()
				.validate(datiUlterioriImpresa.getDataIscrizioneCCIAA(), "dd/MM/yyyy"));
		cameraCommercio.setProvinciaIscrizione(datiUlterioriImpresa.getProvinciaIscrizioneCCIAA());
		
		INAILType inail = impresa.addNewInail();
		inail.setNumIscrizione(datiUlterioriImpresa.getNumIscrizioneINAIL());
		inail.setPosizAssicurativa(datiUlterioriImpresa.getPosizAssicurativaINAIL());
		inail.setLocalitaIscrizione(datiUlterioriImpresa.getLocalitaIscrizioneINAIL());
		
		impresa.setCodiceCNEL(datiUlterioriImpresa.getCodiceCNEL());

		INPSType inps = impresa.addNewInps();
		inps.setNumIscrizione(datiUlterioriImpresa.getNumIscrizioneINPS());
		inps.setLocalitaIscrizione(datiUlterioriImpresa.getLocalitaIscrizioneINPS());
	}
	
	protected void impostaDatiStazioneAppaltante(DettaglioStazioneAppaltanteType from, StazioneAppaltanteType to) {
		String denominazioneUnica = (String) appParamManager.getConfigurationValue(AppParamManager.DENOMINAZIONE_STAZIONE_APPALTANTE_UNICA);
		if (StringUtils.isEmpty(denominazioneUnica)) {
			to.setDenominazione(from.getDenominazione());
			to.setCodiceFiscale(from.getCodiceFiscale());
			to.setIndirizzo(from.getIndirizzo());
			to.setNumCivico(from.getNumCivico());
			to.setCap(from.getCap());
			to.setComune(from.getComune());
			to.setProvincia(from.getProvincia());
			to.setEmail(from.getEmail());
			to.setPec(from.getPec());
			to.setTelefono(from.getTelefono());
			to.setFax(from.getFax());
		} else
			to.setDenominazione(denominazioneUnica);
	}

	protected String getContractingAuthorityValue(DettaglioStazioneAppaltanteType stazione) {
		String toReturn = null;
		
		toReturn = (String) appParamManager.getConfigurationValue(AppParamManager.DENOMINAZIONE_STAZIONE_APPALTANTE_UNICA);
		if (StringUtils.isEmpty(toReturn) && stazione != null)
			toReturn = stazione.getDenominazione();
		
		return toReturn;
	}

	protected boolean hasToShowSocCoop(WizardDatiImpresaHelper impresa) throws Exception {
		LinkedHashMap<String, String> socCop =
				InterceptorEncodedData.get(InterceptorEncodedData.LISTA_FORME_GIURIDICHE_COOPERATIVE);
		return StringUtils.isNotEmpty(impresa.getDatiPrincipaliImpresa().getTipoSocietaCooperativa())
		 		&& !socCop.isEmpty()
				&& socCop.containsValue(impresa.getDatiPrincipaliImpresa().getNaturaGiuridica());
	}
	protected boolean hasToShowSocCoop(WizardRegistrazioneImpresaHelper impresa) throws Exception {
		LinkedHashMap<String, String> socCop =
				InterceptorEncodedData.get(InterceptorEncodedData.LISTA_FORME_GIURIDICHE_COOPERATIVE);
		return StringUtils.isNotEmpty(impresa.getDatiPrincipaliImpresa().getTipoSocietaCooperativa())
				&& !socCop.isEmpty()
				&& socCop.containsValue(impresa.getDatiPrincipaliImpresa().getNaturaGiuridica());
	}

}
