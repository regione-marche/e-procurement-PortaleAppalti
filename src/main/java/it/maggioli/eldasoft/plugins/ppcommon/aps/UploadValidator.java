package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.sign.DigitalSignatureException;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.digital.signature.DigitalSignatureCheckClient;
import it.maggioli.eldasoft.digital.signature.ProviderEnum;
import it.maggioli.eldasoft.digital.signature.model.ResponseCheckSignature;
import it.maggioli.eldasoft.digital.signature.providers.Provider;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.pdfa.PdfAUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParam;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardRinnovoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.keycloakclient.invoker.ApiException;


/**
 * Validatore del processo di upload di un file
 * Valida nome del file, descrizione, dimensione, estensione, formato etc 
 * e prepara la tracciatura dell'evento 
 */
public class UploadValidator {
	
	private final Logger LOG = ApsSystemUtils.getLogger();
	
	private static final SimpleDateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private IAppParamManager appParamManager;
	private ICustomConfigManager customConfigManager;
	
	// input
	private BaseAction action;
	private int actualTotalSize;
	//private Long documentoId;						// ??? SERVE ??? (SOLO per documento richiesto)
	private String documentoDescrizione;			// SOLO in caso di documento ulteriore	
	private File documento;							//
	private int documentoDimensione;				//
	private String documentoFileName;				//
	//private String documentoContentType;			// ??? SERVE ??? 
	private Integer documentoFormato;				// 
	private boolean onlyP7m;						// DOCUM-FIRMATO.ACCETTASOLOP7M.ACTIVE=1	
	private boolean checkFileSignature;				// il default e' False => non verificare la firma digitale del file 
	private String estensioniAmmesse;				// es: AppParamManager(AppParamManager.ESTENSIONI_AMMESSE_DOC)
	//private String pdfUUID;						// ??? verifica l'HASH del documentp PDF	
	private String eventoDestinazione;				// codice gara,elenco,etc associato all'evento di UPLOAD
	private String eventoMessaggio;					// descrizione associata all'evento di UPLOAD
	private Boolean isCatalogo;						// 

	// output
	// evento per la gestire il flusso di upload
	private Event evento;
	private DocumentiAllegatiFirmaBean checkFirma;	// firma digitale calcolata	
	
	// parametri del portale pe le info relative ai limiti di upload
	private Integer limiteUploadFile;
	private Integer limiteTotaleUploadFile;

	// tipi di documenti definiti in BO   
	private List<DocumentazioneRichiestaType> documentiRichiestiBO; 
	
	
	/**
	 * elenco di file name non validi 
	 * In casi particolari, per i documenti ulteriori alcuni nomi file non sono ammessi ("Offerta tecnica", "Offerta economica", ...)
	 */
	private class FileNameNonValido {
		String filenameNonValido;
		String formField;
		String fieldError;
		
		// costruttore
		public FileNameNonValido(String filenameNonValido, String formField, String fieldError) {
			this.filenameNonValido = filenameNonValido;
			this.formField = formField;
			this.fieldError = fieldError;
		}		
	}
	
	private List<FileNameNonValido> fileNameNonValidi;
	 

	/**
	 * costruttore 
	 */
	public UploadValidator(BaseAction action) {
		this.action = action;
		//documentoId = null;
		documentoDescrizione = null;
		documentoFileName = null;
		documentoDimensione = 0;
		//documentoContentType = null;
		documentoFormato = null;
		onlyP7m = false;
		checkFileSignature = false;		
		//pdfUUID = null;
		eventoMessaggio = null;
		eventoDestinazione = null;		
		estensioniAmmesse = null;
		limiteUploadFile = new Integer(0);
		limiteTotaleUploadFile = new Integer(0);
		documentiRichiestiBO = null;
		isCatalogo = false;
		evento = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);
			customConfigManager = (ICustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
			
			// default: utilizza la customizzazione DOCUM-FIRMATO.ACCETTASOLOP7M
			onlyP7m = customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");

			// default: utilizza il parametro delle estensioni di default 
			estensioniAmmesse = (String) appParamManager.getConfigurationValue(AppParamManager.ESTENSIONI_AMMESSE_DOC);
					
			// recupera i parametri dei limiti di upload di default
			limiteUploadFile = new Integer(FileUploadUtilities.getLimiteUploadFile(appParamManager));
			limiteTotaleUploadFile = new Integer(FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager));

			// crea un nuovo evento per tracciare l'upload
			evento = new Event();
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("UploadValidator()", t);
		}		
	}
	
	public UploadValidator setActualTotalSize(int actualTotalSize) {
		this.actualTotalSize = actualTotalSize;
		return this;
	}

	public UploadValidator setDocumentoDescrizione(String documentoDescrizione) {
		this.documentoDescrizione = documentoDescrizione;
		return this;
	}

	public UploadValidator setDocumento(File documento) {
		this.documento = documento;
		return this;
	}
	
	public UploadValidator setDocumentoDimensione(int documentoDimensione) {
		this.documentoDimensione = documentoDimensione;
		return this;
	}

	public UploadValidator setDocumentoFileName(String documentoFileName) {
		this.documentoFileName = documentoFileName;
		return this;
	}
	
	public UploadValidator setDocumentoFormato(Integer documentoFormato) {
		this.documentoFormato = documentoFormato;
		return this;
	}
	
	public UploadValidator setEstensioniAmmesse(String estensioniAmmesse) {		
		this.estensioniAmmesse = estensioniAmmesse;		
		return this;
	}

	public UploadValidator setLimiteUploadFile(int limiteUploadFile) {
		this.limiteUploadFile = limiteUploadFile;
		return this;
	}

	public UploadValidator setLimiteTotaleUploadFile(int limiteTotaleUploadFile) {
		this.limiteTotaleUploadFile = limiteTotaleUploadFile;
		return this;
	}

	public UploadValidator setHelper(BustaDocumenti busta) {
		documentiRichiestiBO = busta.getDocumentiRichiestiDB();
		actualTotalSize = busta.getHelperDocumenti().getTotalSize();
		return this;
	}

	public UploadValidator setHelper(WizardIscrizioneHelper helper) {
		recuperaDocumentiRichiestiBOElenchi(helper);
		actualTotalSize = helper.getDocumenti().getTotalSize();
		return this;
	}

	public UploadValidator setHelper(DocumentiAllegatiHelper helper) {
		//documentiRichiestiBO = null;
		actualTotalSize = helper.getTotalSize();
		return this;
	}

	public UploadValidator setHelper(WizardProdottoHelper helper) {
		//documentiRichiestiBO = ???;
		actualTotalSize = helper.getDocumenti().getActualTotalSize();
		isCatalogo = true;
		return this;
	}

	public UploadValidator setHelper(WizardOffertaAstaHelper helper) {
		//this.documentiRichiestiBO = ???;
		this.actualTotalSize = helper.getDocumenti().getTotalSize();
		return this;
	}

	public UploadValidator setOnlyP7m(boolean onlyP7m) {
		this.onlyP7m = onlyP7m;
		return this;
	}

	public UploadValidator setCheckFileSignature(boolean checkFileSignature) {
		this.checkFileSignature = checkFileSignature;
		return this;
	}

	public UploadValidator setEventoDestinazione(String eventoDestinazione) {
		this.eventoDestinazione = eventoDestinazione;
		return this;
	}

	public UploadValidator setEventoMessaggio(String eventoMessaggio) {
		this.eventoMessaggio = eventoMessaggio;
		return this;
	}

	public Event getEvento() {
		return evento;
	}	
	
	public DocumentiAllegatiFirmaBean getCheckFirma() {
		return checkFirma;
	}
	
	public Integer getLimiteUploadFile() {
		return limiteUploadFile;
	}

	public Integer getLimiteTotaleUploadFile() {
		return limiteTotaleUploadFile;
	}

	/**
	 * recupera la documentazione richiesta definita in BO per gli elenchi operatore 
	 */
	private void recuperaDocumentiRichiestiBOElenchi(WizardIscrizioneHelper helper) {
		documentiRichiestiBO = null;		
		if(helper != null) {
			try {
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
				IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
				
				if(helper instanceof WizardRinnovoHelper)
					documentiRichiestiBO = bandiManager
						.getDocumentiRichiestiRinnovoIscrizione(
								helper.getIdBando(), 
								helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
								helper.isRti());
				else 
					documentiRichiestiBO = bandiManager
						.getDocumentiRichiestiBandoIscrizione(
								helper.getIdBando(), 
								helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
								helper.isRti());
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().error("recuperaDocumentiRichiestiBOElenchi(WizardIscrizioneHelper helper)", t);
			}
		}
	}

	/**
	 * crea la lista delle note informative da inserire dopo le info dei limiti di upload dei file nelle jsp di upload 
	 */
	public List<String> getInfoEstensioniAmmesse() {
		List<String> msg = new ArrayList<String>();
			
		boolean formatoFirmato = false;	
		boolean formatoExcel = false;
		boolean formatoPdf = false;
		if(documentiRichiestiBO != null) {
			List<Integer> formati = documentiRichiestiBO.stream()
					.map(DocumentazioneRichiestaType::getFormato)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());			
			formatoFirmato = (formati.indexOf(PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO) >= 0);
			formatoExcel = (formati.indexOf(PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL) >= 0);
			formatoPdf = (formati.indexOf(PortGareSystemConstants.DOCUMENTO_FORMATO_PDF) >= 0);
		}
		
		if(formatoFirmato || formatoExcel || formatoPdf || StringUtils.isNotEmpty(estensioniAmmesse)) {
			if(formatoFirmato) {
				if(onlyP7m)
					msg.add( action.getText("info.upload.ammessiSoloP7m") );
				else {
					msg.add( action.getText("info.upload.ammessiFileFirmati") );
				}
			}
			
//			if(formatoPdf) {
//				msg.add( action.getText("info.upload.ammessi???") );
//			}
			
			if(formatoExcel) {
				msg.add( action.getText("info.upload.ammessiFileExcel") );
			}
			
			if(StringUtils.isNotEmpty(estensioniAmmesse)) {
				String estensioni = estensioniAmmesse.replace(",", ", ").replace(".", "");
				if(isCatalogo)
					// in caso di gestione prodotto si aggiunge anche la riga per tutti i formati
					msg.add( action.getText("info.upload.ammesseImmagini", new String[] {estensioni}) );
				else
					msg.add( action.getText("info.upload.estensioniAmmesse", new String[] {estensioni}) );
			}
		} else {
			msg.add( action.getText("info.upload.ammessiTutti") );
		}		
		
		// in caso di gestione prodotto si aggiunge anche la riga per tutti i formati
		if(isCatalogo && StringUtils.isNotEmpty(estensioniAmmesse)) {
			msg.add( action.getText("info.upload.ammessiTutti") );
		}
		
		
		return msg;
	}
	
	/**
	 * aggiungi la descrizione di un file che non e' permesso durante l'upload ("Offerta economica", "Offerta tecnica", ...)  
	 */
	public UploadValidator addFilenameNonValido(
			String filenameNonAmmesso,
			String formField,
			String fileError) 
	{	
		if(fileNameNonValidi == null) {
			fileNameNonValidi = new ArrayList<FileNameNonValido>();
		}
		fileNameNonValidi.add( new FileNameNonValido(filenameNonAmmesso, formField, fileError) );
		return this;
	}
	
	/**
	 * valida l'operazione di upload
	 *  
	 * @throws ApsSystemException 
	 */
	public boolean validate() {
		boolean controlliOk = true;
		
		LOG.debug("Inizio validazione per l'upload del documento {}", documentoFileName);
		checkFirma = null;
		try {
			// 1) inizializza l'evento di UPLOAD
			evento.setUsername(action.getCurrentUser().getUsername());
			evento.setDestination(eventoDestinazione);
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
			evento.setIpAddress(action.getCurrentUser().getIpAddress());
			evento.setSessionId(action.getRequest().getSession().getId());
			evento.setMessage(eventoMessaggio);

			// calcola la dimensione del file
			if(documento != null) {
				documentoDimensione = FileUploadUtilities.getFileSize(documento);
			}
			
			// recupera l'estensione del file (TXT, PDF, XLS, ...) 
			String documentoFileExt = (StringUtils.isNotEmpty(documentoFileName) 
					? FilenameUtils.getExtension(documentoFileName).toUpperCase() 
					: ""
			);
			
			// 2) valida il nome del file
			if(StringUtils.isEmpty(documentoFileName)) {
				action.addActionError(action.getText("Errors.nomeNonValido"));
				controlliOk = false;
			}
			
			// 3) in casi particolari, per i documenti ulteriori alcuni nomi file non sono ammessi ("Offerta tecnica", "Offerta economica", ...) 
			if(controlliOk) {
				if(fileNameNonValidi != null) {
					for(FileNameNonValido f : fileNameNonValidi) {
						String nome = documentoFileName.substring(0, documentoFileName.lastIndexOf('.')).toUpperCase().trim();
						if(nome.equals((f.filenameNonValido).toUpperCase())) {
							action.addFieldError(f.formField, action.getText(f.fieldError));
							controlliOk = false;
						}
					}
				}
			}
			
			// 4) valida descrizione, dimensione, nome file, estensione, formato, ...
			controlliOk = controlliOk && checkFileSize(
					documentoDimensione, 
					documentoFileName, 
					actualTotalSize, 
					limiteUploadFile, 
					limiteTotaleUploadFile, 
					evento
			);
			
			controlliOk = controlliOk && checkFileName(
					documentoFileName, 
					evento
			);
			
			if(documento != null) {
				controlliOk = controlliOk && checkFileFormat(
						documento, 
						documentoFileName, 
						documentoFormato, 
						onlyP7m, 
						evento
				);
			}
			
			if(StringUtils.isNotEmpty(documentoDescrizione)) {
				controlliOk = controlliOk && checkFileDescription(
						documentoDescrizione, 
						evento
				);
			}
			
			if(StringUtils.isNotEmpty(estensioniAmmesse)) {
				controlliOk = controlliOk && checkFileExtension(
						documentoFileName, 
						estensioniAmmesse, 
						evento
				);
			}
			
//			// 5) verifica se l'HASH del documento caricato corrisponde 
//			// a quello inserito nella comunicazione FS13
//			if( !action.checkFileFirmato(documento, documentoFileName, pdfUUID, evento) ) {
//				controlliOk = false; 
//			}		
	
			// 6) verifica la firma digitale del documento, servira' successivamente al DocumentiAllegatiHelper...
			if(controlliOk) {
				if(documento != null && checkFileSignature) {
					try {
						checkFirma = checkFileSignature(
								documento
								, documentoFileName
								, documentoFormato
								, Date.from(Instant.now())
								, onlyP7m
								, evento
						);
					} catch (ApsSystemException ex) {
						LOG.error("Errore nella verifica della firma digitale.", ex);
						action.addActionError(action.getText("Errors.cannotVerifySign"));
					}
					LOG.debug(action.getClass().getName() + " -> checkFirma: {}", checkFirma);
				}
			}

			// 7) verifica del contenuto con Tika
			if(controlliOk) {
				if(documento != null) {
					if( !isEstensioneFileAmmessa(documentoFileName, estensioniAmmesse, evento) ) {
						controlliOk = false;
					}
				}
			}

			// 8) verifica del contenuto con Tika
			if(controlliOk) {
				if(documento != null) {
					if( !checkTikaFileContent(documento, documentoFileName, evento) ) {
						controlliOk = false;
					}
				}
			}
			
			// 9) verifica contenuti malevoli nei documenti PDF
			if(controlliOk) {
				//if(documento != null && isFileExtension(documentoFileName, "p7m,pdf,tsd")) {
				if(documento != null && isFileExtension(documentoFileName, "pdf")) {
					if(customConfigManager.isActiveFunction("UPLOADFILE", "CHECKPDFVULNERABILE")) {
						byte[] contenuto = FileUtils.readFileToByteArray(documento);
						if(isMaliciousPdf(contenuto, documentoFileName, evento)) {
							controlliOk = false;
						}
					}
				}
			}
			
		} catch (Exception ex) {
			controlliOk = false;
			LOG.error("UploadValidator.validate()", ex);
		}

		LOG.debug("validazione per l'upload del file {} : {}", documentoFileName, controlliOk);
		return controlliOk; 
	}


	/**
	 * Verifica la dimensione del file di input.
	 *
	 * @param filesize dimensione in byte del file allegato
	 * @param filename nome del file allegato
	 * @param actualTotalSize la dimensione totale dei file finora caricati
	 * @param limiteDimensioneSingoloFile la dimensione massima per singolo file
	 * @param limiteDimensioneTotaliFile la dimensione massima per la busta
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	private boolean checkFileSize(
			int documentSize,
			String documentFileName, 
			int actualTotalSize,
			int limiteDimensioneSingoloFile,
			int limiteDimensioneTotaliFile,
			Event event) 
	{
		boolean controlliOk = true;
		if (documentSize < 0) {
			action.addActionError(action.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("File da caricare non selezionato");
			}
			controlliOk = false;
		} else {
			if (documentSize == 0) {
				action.addActionError(action.getText("Errors.emptyFile"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("File " + documentFileName + " vuoto");
				}
				controlliOk = false;
			} else {
				// se e' stato allegato un file si controlla che non superi la dimensione massima
				//int limiteDimensioneSingoloFile = FileUploadUtilities.getLimiteUploadFile(appParamManager);
				if (documentSize > limiteDimensioneSingoloFile) {
					action.addActionError(action.getText("Errors.overflowFileSize"));
					if (event != null) {
						event.setLevel(Event.Level.ERROR);
						event.setDetailMessage("Upload del file " + documentFileName
								+ " bloccato in quanto la sua dimensione (" + documentSize
								+ " KB) supera il limite per singolo file di " + limiteDimensioneSingoloFile
								+ " KB definito in configurazione");
					}
					controlliOk = false;
				}
				//int limiteDimensioneTotaliFile = FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
				if (controlliOk && (documentSize + actualTotalSize > limiteDimensioneTotaliFile)) {
					action.addActionError(action.getText("Errors.overflowTotalFileSize"));
					if (event != null) {
						event.setLevel(Event.Level.ERROR);
						event.setDetailMessage("Upload del file " + documentFileName
								+ " bloccato in quanto la sua dimensione (" + documentSize
								+ " KB) sommata ai file caricati in precedenza (" + actualTotalSize 
								+ " KB) supera il limite di " + limiteDimensioneTotaliFile
								+ " KB definito in configurazione");
					}
					controlliOk = false;
				}
			}
		}
		return controlliOk;
	}

	/**
	 * Verifica la dimensione del file di input.
	 *
	 * @param filesize dimensione del documento
	 * @param filename nome del file allegato
	 * @param actualTotalSize la dimensione totale dei file caricati finora
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	public boolean checkFileSize(
			int documentSize,
			String documentFileName, 
			int actualTotalSize,
			Event event) 
	{
		// usa i parametri di default per i limiti di upload ("limiteUploadFile", "limiteTotaleUploadDocIscrizione")
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		IAppParamManager appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);				
		return checkFileSize(
				documentSize, 
				documentFileName, 
				actualTotalSize,
				FileUploadUtilities.getLimiteUploadFile(appParamManager),
				FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager),
				event);
	}

	/**
	 * Verifica la dimensione del file di input.
	 *
	 * @param filesize dimensione del documento
	 * @param filename nome del file allegato
	 * @param actualTotalSize la dimensione totale dei file caricati finora
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	public boolean checkFileSize(
			File document,
			String documentFileName, 
			int actualTotalSize,
			Event event) 
	{
		return checkFileSize(
				(document != null ? FileUploadUtilities.getFileSize(document) : -1), 
				documentFileName, 
				actualTotalSize,
				event);
	}
	
	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentFileName nome del file allegato
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	public boolean checkFileName(String documentFileName, Event event) {
		boolean controlliOk = true;
		if (documentFileName != null) {
			if (documentFileName.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				action.addActionError(action.getText("Errors.overflowFileNameLength"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il nome file " + documentFileName
							+ " supera il limite di " + FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE
							+ " caratteri");
				}
				controlliOk = false;
			}
			//matcher to find if there is any special character in string
			Pattern regex = Pattern.compile(FileUploadUtilities.INVALID_FILE_NAME_REGEX);
			Matcher matcher = regex.matcher(documentFileName);
			if (matcher.find()) {
				action.addActionError(action.getText("Errors.invalidFileName"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il nome file " + documentFileName
							+ " contiene caratteri non ammessi");
				}
				controlliOk = false;
			}
		}
		return controlliOk;
	}
	
	/**
	 * Check if the inputted filename end with the given extension
	 * NB: The check is case-insensitive
	 * @param fileName
	 * @param extensions indica l'estensione (pdf, .pdf, PDF, .PDF) o l'elenco delle estensioni separate da "," (P7M,XLS,PDF,...) 
	 * @return
	 */
	public static boolean isFileExtension(String fileName, String extensions) {
		boolean match = false;
		
		if(StringUtils.isNotEmpty(extensions)) {
			// estrai l'estensione del file (es: prova.txt => TXT)
			String ext = (StringUtils.isNotEmpty(fileName) 
						  ? FilenameUtils.getExtension(fileName).toUpperCase()		 
						  : "");
			
			// verifica se l'estensione "abc" o ".abc" e' valida...
			if(extensions.indexOf(",") >= 0) {
				// txt,pdf,p7m => [TXT, PDF, P7M]
				List<String> list = Arrays.asList(extensions.toUpperCase().split(","));
				match = list.stream()
						.anyMatch(e -> e.equalsIgnoreCase("." + ext) || e.equalsIgnoreCase(ext));
			} else { 
				match = (extensions.equalsIgnoreCase("." + ext) || extensions.equalsIgnoreCase(ext));
			}
		} else {
			// in caso di lista di estensioni vuota, si accetta qualunque tipo di file
			match = true;
		}
	
		return match;
	}
	
	public static boolean isP7m(String fileName) {
		return isFileExtension(fileName, ".p7m");
	}
	
	public static boolean isPdf(String fileName) {
		return isFileExtension(fileName, ".pdf");
	}
	
	public static boolean isTsd(String fileName) {
		return isFileExtension(fileName, ".tsd");
	}
	
	public static boolean isXml(String fileName) {
		return isFileExtension(fileName, ".xml");
	}

	/**
	 * Verifica il formato del file in input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param checkDate data di verifica della validita' della firma
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @param onlyP7m
	 * @param signRequested indica se il file prevede una firma digitale da verificare
	 *
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 * @throws Exception 
	 */
	private boolean checkFileFormat(
			File document,
			String documentFileName,
			Integer format,
			Date checkDate,
			boolean onlyP7m,
			boolean signRequested,
			Event event) 
	{
		boolean controlliOk = true;
		
		if (documentFileName != null
			&& (format == null || format == PortGareSystemConstants.DOCUMENTO_FORMATO_QUALSIASI)
			&& documentFileName.toUpperCase().endsWith(".P7M")) 
		{
			format = PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO;
		}
		if (documentFileName != null) {
			boolean doCheckDocument = false;
			boolean isP7m = isP7m(documentFileName);
			boolean isTsd = isTsd(documentFileName);
			boolean isPdf = isPdf(documentFileName);
			boolean isXml = isXml(documentFileName);
			if (format == null) {
				// NB: nel caso non sia specificato un formato si verifica in base all'estensione del file
				//     e forza il controllo solo ai PDF (PORTAPPALT-920) 
				doCheckDocument = true;
				isP7m = false;
				isTsd = false;
				isXml = false;
			} else {
				switch (format) {
					case PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO:
//						// come file firmati si considerano .p7m, .tsd, .pdf, .xml
//						// NB: per .p7m e .tsd e' possibible verificare la firma digitale
//						//     mentre per .pdf, .xml si visualizza a video un messaggio
//						//     di notifica per "l'accettazione di un documento" che
//						//     non ha firma digitale
						if (onlyP7m && !isP7m) {
							action.addActionError(action.getText("Errors.onlyP7m"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file .p7m");
							}
							controlliOk = false;
						} else if (!isP7m && !isTsd && !isPdf && !isXml) {
							action.addActionError(action.getText("Errors.p7mRequired"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file .p7m, .p7m.tsd, .pdf, .xml");
							}
							controlliOk = false;
						} else if (document != null) {
							//Causa una duplicazione del controllo firma
							doCheckDocument = true;
						}
						break;
	
					case PortGareSystemConstants.DOCUMENTO_FORMATO_PDF:
						if (!isPdf) {
							action.addActionError(action.getText("Errors.pdfRequired"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file PDF");
							}
							controlliOk = false;
						}
						if( !checkDocumentPdf(document, documentFileName, event, null) ) {
							controlliOk = false;
						} 
						break;
	
					case PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL:
						if (!isFileExtension(documentFileName, ".XLS,.XLSX,.ODS")) {
							action.addActionError(action.getText("Errors.excelRequired"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file in formato foglio elettronico (XLS, XLSX, ODS)");
							}
							controlliOk = false;
						}
						break;
	
					default:
						break;
				}
			}
			if(doCheckDocument) {
				if (isP7m)
					controlliOk = checkDocumentP7m(document, documentFileName, event, Boolean.TRUE, null);
				else if (isTsd)
					controlliOk = checkDocumentTsd(document, documentFileName, checkDate, event, Boolean.TRUE, true);
				else if(isPdf) {
					controlliOk = checkDocumentPdf(document, documentFileName, event, null);
				} 
//				else if(isXml) {
//					controlliOk = checkDocumentXml(document, documentFileName, event);
//				}
			}
		}
		return controlliOk;
	}

	/**
	 * Verifica il formato del file in input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @param onlyP7m ...
	 * 
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 * @throws Exception 
	 */
	public boolean checkFileFormat(
			File document, 
			String documentFileName,
			Integer format,
			boolean onlyP7m,
			Event event) 
	{
		return checkFileFormat(document, documentFileName, format, new Date(), onlyP7m, false, event);
	}

	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentDescription descrizione del file allegato
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	public boolean checkFileDescription(String documentDescription, Event event) {
		boolean controlliOk = true;
		if (StringUtils.isEmpty(documentDescription)) {
			action.addActionError(action.getText("Errors.fileDescriptionMissing"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Descrizione obbligatoria mancante per il file caricato");
			}
			controlliOk = false;
		}
		return controlliOk;
	}
	
	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentFileName nome del file allegato
	 * @param appParamManager l'appParamManager per ricavare le costanti di check
	 * @param estensioniParamName il parametro che indica quali estensioni sono
	 * accettate
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	public boolean checkFileExtension(String documentFileName, String estensioniAmmesse, Event event) {
		boolean controlliOk = true;
		
		// l'elenco delle estensioni ammesse per default si trova nel parametro "estensioniAmmesseDocIscrizione"
		if(StringUtils.isEmpty(estensioniAmmesse)) {
			// carica le estensioni di default
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IAppParamManager appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);
			estensioniAmmesse = StringUtils.stripToNull((String) appParamManager
					.getConfigurationValue(AppParamManager.ESTENSIONI_AMMESSE_DOC));
		}
		
		controlliOk = isFileExtension(documentFileName, estensioniAmmesse);
		if ( !controlliOk ) {
			addActionErrorExtensionNotSupported(documentFileName, estensioniAmmesse, event);
		}
		
		return controlliOk;
	}
	
	/**
	 * ...
	 */
	private void addActionErrorExtensionNotSupported(String fileName, String estensioniAmmesse, Event event) {
		estensioniAmmesse = (StringUtils.isNotEmpty(estensioniAmmesse) 
				 ? estensioniAmmesse.replace(",", ", ").replace(".", "")
				 : "");
		action.addActionError(action.getText("Errors.fileNameExtensionNotSupported", new String[] {estensioniAmmesse}));
		if (event != null) {
			event.setLevel(Event.Level.ERROR);
			event.setDetailMessage("Il file " + fileName + " non utilizza una delle estensioni ammesse (" + estensioniAmmesse + ")");
		}
	}

	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentFileName nome del file allegato
	 * @param appParamManager l'appParamManager per ricavare le costanti di check
	 * @param estensioniParamName il parametro che indica quali estensioni sono accettate
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	public boolean checkFileExtension(String documentFileName, Event event) {
		return checkFileExtension(documentFileName, null, event);
	}

	/**
	 * Verifica la firma del file se viene richiesto i formato di file {@link PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO}
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param checkDate data di verifica della validita' della firma 
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @param onlyP7m
	 * @return {@link DocumentiAllegatiFirmaBean} se si verifica un file firmato, {@code null} altrimenti
	 * @throws ApsSystemException nel caso ci siano errori
	 */
	public DocumentiAllegatiFirmaBean checkFileSignature(
			File document,
			String documentFileName,
			Integer format,
			Date checkDate,
			boolean onlyP7m,
			Event event) throws ApsSystemException
	{
		DocumentiAllegatiFirmaBean checkFirma = null;

		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		IAppParamManager appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);
		//ICustomConfigManager customConfigManager = (ICustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
		
		boolean isP7m = isP7m(documentFileName);
		boolean isTsd = isTsd(documentFileName);
		boolean isPdf = isPdf(documentFileName);
		boolean isXml = isXml(documentFileName);
		boolean signed = (format != null && PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO == format);
		
		LOG.debug("Verifico che il formato del documento risulti un documento firmato per capire se ecluderlo o meno");
		if(signed && !isP7m && !isTsd && !isPdf && !isXml) {
			action.addActionError(action.getText("Errors.p7mRequired"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("File " + documentFileName + " scartato, richiesto file .p7m, .p7m.tsd, .pdf, .xml");
			}
			throw new ApsSystemException(action.getText("Errors.p7mRequired"));
		}
		
		if (signed && onlyP7m && !isP7m) {
			action.addActionError(action.getText("Errors.onlyP7m"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("File " + documentFileName + " scartato, richiesto file .p7m");
			}
			throw new ApsSystemException(action.getText("Errors.onlyP7m"));
		}
		
		LOG.debug("Verifico la firma");
		// E' inutile fare il controllo visto che se non e' uno dei formati: p7m, tsd, pdf, xml; 
		// viene fatto un throws e si deve fare la verifica della firma su tutti e 4 i formati
		try {
			if (isP7m || isTsd || isXml || isPdf) {
				Integer provider = (Integer) appParamManager.getConfigurationValue("digital-signature-checker-provider");				
				if (provider == null || provider == 0) {
					// verifica della firma con BouncyCastle
					if ((!isXml && !isPdf) || signed)
						checkFirma = standardCheckSignatureMethod(
										document
										, documentFileName
										, checkDate
										, onlyP7m
										, signed
										, event
								);
				} else if (provider == 1 || provider == 2)
					// verifica tramite Maggioli o Infocert
					checkFirma = checkDocumentSignatureWithNewService(
									appParamManager
									, document
									, documentFileName
									, checkDate
									, provider == 1 ? ProviderEnum.MAGGIOLI : ProviderEnum.INFOCERT
									, event
							);
				else
					throw new RuntimeException("The Provider" + provider + " is not supported yet");
				
				// verifica la validita' del formato PDF/A e PDF/UA
				checkFirma = checkPdfACompliance(
						document, 
						documentFileName, 
						checkDate,
						checkFirma,
						event);
			}			
		} catch (Exception e) {
			LOG.warn("Impossibile verificare la firma del documento.",e);
			throw new ApsSystemException(e.getMessage(), e);
		}

		return checkFirma;
	}

	/**
	 * Metodo standard per la verifica delle firme
	 *
	 * @param document
	 * @param documentFileName
	 * @param checkDate
	 * @param event
	 * @param onlyP7m
	 * @param b
	 * @return
	 */
	private DocumentiAllegatiFirmaBean standardCheckSignatureMethod(
			File document,
			String documentFileName,
			Date checkDate,
			boolean onlyP7m, 
			boolean signRequested,
			Event event) 
	{
		LOG.debug("Verifico la firma con il metodo standard.");
		DocumentiAllegatiFirmaBean controlliOk = null;
		// come file firmati si considerano .p7m, .tsd, .pdf, .xml
		// NB: per .p7m e .tsd e' possibible verificare la firma digitale
		//     mentre per .pdf, .xml si visualizza a video un messaggio
		//     di notifica per "l'accettazione di un documento" che
		//     non ha firma digitale
		boolean isP7m = isP7m(documentFileName);
		boolean isTsd = isTsd(documentFileName);
		boolean isPdf = isPdf(documentFileName);
		boolean isXml = isXml(documentFileName);

		if (document != null) {
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheckts(checkDate);
			if (isP7m)
				controlliOk.firmacheck(checkDocumentP7m(document, documentFileName, event, Date.from(Instant.now())));
			else if (isTsd)
				controlliOk.firmacheck(checkDocumentTsd(document, documentFileName, checkDate, event, signRequested));
			else if (isPdf)
				controlliOk.firmacheck(checkDocumentPdf(document, documentFileName, event, Date.from(Instant.now())));
			else if (isXml)
				controlliOk.firmacheck(checkDocumentXml(document, documentFileName, event));
		}
		if (controlliOk != null && Boolean.FALSE.equals(controlliOk.getFirmacheck())) {
			action.addActionMessage(action.getText("Warning.signValidationNotSupported"));
		}
		return controlliOk;
	}

	/**
	 * verifica della firma digitale di un documento per mezzo di servizio esterno 
	 */
	private DocumentiAllegatiFirmaBean checkDocumentSignatureWithNewService(
			IAppParamManager appParamManager
			, File document
			, String documentFileName
			, Date checkDate
			, ProviderEnum providerEnum
			, Event evento
	) {
		LOG.debug("Chiamo il servizio esterno con il provider {} per verificare la firma elettronica.", providerEnum.name());

		Date firmacheckts = checkDate != null ? checkDate : Date.from(Instant.now());
		
		DocumentiAllegatiFirmaBean controlliOk = null;
		IEventManager eventManager = null;
		FileInputStream fis = null;
		try {
			Map<String, AppParam> kongParams = appParamManager.getMapAppParamsByCategory("kong-client");
			String authUrl = kongParams.get(AppParamManager.KONG_AUTH_URL).getValue();
			//String authUsername = kongParams.get(AppParamManager.KONG_AUTH_USERNAME).getValue();
			//String authSecret = kongParams.get(AppParamManager.KONG_AUTH_PASSWORD).getValue();
			String authUsername = kongParams.get(AppParamManager.KONG_AUTH_CLIENT_ID).getValue();
			String authSecret = kongParams.get(AppParamManager.KONG_AUTH_CLIENT_SECRET).getValue();
			
			Map<String, AppParam> parameters = appParamManager.getMapAppParamsByCategory("digital-signature-check");
			String providerUrl = parameters.get("digital-signature-check-url").getValue();

			Provider provider =
					DigitalSignatureCheckClient
							.getInstance(providerEnum)
//							.withConfiguration(providerUrl);
							.withConfiguration(authUrl, authUsername, authSecret, providerUrl);

			// crea l'evento per la verifica della firma digitale tramite servizio esterno
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
			
			// inserisci l'evento di uscita verso il servizio di verifica della firma...  
			Event eventoCheckFirma = new Event();
			eventoCheckFirma.setUsername(evento.getUsername());
			eventoCheckFirma.setIpAddress(evento.getIpAddress());
			eventoCheckFirma.setSessionId(evento.getSessionId());
			eventoCheckFirma.setDestination(evento.getDestination());
			eventoCheckFirma.setEventType(PortGareEventsConstants.CHECKFIRMA);
			eventoCheckFirma.setLevel(Event.Level.INFO);
			eventoCheckFirma.setMessage("Verifica firma digitale per il file " + documentFileName + 
					  		  			" presso il servizio " + providerUrl + ", " + authUrl + "");
			eventManager.insertEvent(eventoCheckFirma);
			
			LOG.debug("verifica firma alla data: {}", YYYYMMDD_HHMMSS.format(firmacheckts));
			fis = new FileInputStream(document);
			ResponseCheckSignature rcs = null;
			if (providerEnum == ProviderEnum.MAGGIOLI)
				rcs = provider.checkDigitalSignature(
						firmacheckts,
						fis,
						documentFileName
				);
			else
				rcs = provider.checkDigitalSignature(
						fis,
						documentFileName
				);

			LOG.debug("Ricevuta risposta: {}",rcs.getJsonbody());
			
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(rcs.getVerified()).firmacheckts(firmacheckts);
			
		} catch (ApiException e) {
			LOG.error("Errore nella verifica della firma digitale.",e);
//				this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (evento != null) {
				evento.setLevel(Event.Level.WARNING);
				evento.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.FALSE).firmacheckts(firmacheckts);
		} catch (FileNotFoundException e) {
			LOG.error("Errore nella lettura del file per la verifica della firma digitale.",e);
//				this.addActionError(this.getText("Errors.fileNotSet"));
			if (evento != null) {
				evento.setError(e);
			}
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.FALSE).firmacheckts(firmacheckts);
		} catch (Exception e) {
			final String ERROR_MESSAGE = "Errore nella verifica della firma digitale, controllare che il servizio per il controllo della firma sia operativo e che le configurazioni impostate siano corrette.";
			LOG.error(ERROR_MESSAGE);
			if (evento != null) {
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage(ERROR_MESSAGE);
			}
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.FALSE).firmacheckts(firmacheckts);
		}
				
		// chiudi lo il filestream anche in caso di errori e rilascia il file... 
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
			}
		}
		
		if(controlliOk != null && Boolean.FALSE.equals(controlliOk.getFirmacheck())) {
			action.addActionMessage(action.getText("Warning.signValidationNotSupported"));
//				action.addActionError(this.getText("Errors.invalidSign"));
			if (evento != null) {
				evento.setLevel(Event.Level.WARNING);
				evento.setDetailMessage("Il file " + documentFileName + " non contiene una firma digitale valida");
			}
		}
		
		return controlliOk;
	}

	/**
	 * verifica la validita' del formato PDF/A, PDF/UA
	 * @throws Exception 
	 */
	private DocumentiAllegatiFirmaBean checkPdfACompliance(
			File document,
			String documentFileName,
			Date checkDate,
			DocumentiAllegatiFirmaBean checkFirma,
			Event event) throws Exception 
	{
		// verifica solo quando e' attiva la customizzazione UPLOAD|UPLOADPDF-A.ACTIVE = 1
		
		boolean uploadPdfA = customConfigManager.isActiveFunction("PDF", "UPLOADPDF-A");
		if(uploadPdfA) {
			boolean isPdfA = false;
			
			if (isP7m(documentFileName) || isTsd(documentFileName)) {
				// recupera il contentuto "pdf"
				byte[] pdf = (byte[]) action.estraiContenutoDocumentoFirmato(document, documentFileName, true, event);					
				String pdfFilename = (String) action.estraiContenutoDocumentoFirmato(document, documentFileName, false, event);		
				while (pdf != null && (isP7m(pdfFilename) || isTsd(pdfFilename))) {
					pdf = (byte[]) action.estraiContenutoDocumentoFirmato(pdf, pdfFilename, true, event);
					pdfFilename = (String) action.estraiContenutoDocumentoFirmato(pdf, pdfFilename, false, event);
				}
				if(pdf != null) {
					isPdfA = PdfAUtils.checkIsPdfACompliant(pdf, pdfFilename, event, action);
				}
				pdf = null;
				
			} else if (isPdf(documentFileName)) {
				isPdfA = PdfAUtils.checkIsPdfACompliant(document, documentFileName, event, action);
			}
			
			// marca l'allegato come PDF-A  
			if(checkFirma == null) {
				checkFirma = new DocumentiAllegatiFirmaBean().firmacheckts(checkDate).firmacheck(false);
			}
			checkFirma.setPdfaCompliant(isPdfA);
		}
		
		return checkFirma;
	}
	
	/**
	 * verifica la validita' di un documento in formato .pdf ed addizionalmente anche se e' in formato PDF-A
	 * 
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentPdf(
			File document, 
			String documentFileName,
			Event event,
			Date currentTime)
	{
		LOG.info("checkDocumentPdf");
		boolean controlliOk = true;

		// verifica la firma solo se necessario...
		if(currentTime != null) {
			controlliOk = checkDocumentSign(
					document, 
					documentFileName, 
					event, 
					Boolean.TRUE, 
					currentTime); 	
		}
		
		return controlliOk;
	}

	/**
	 * Controllo di validita' della firma di un file utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param showActionError In caso di errore se a true inserisce una action error.
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentSign(
			File document, 
			String documentFileName, 
			Event event, 
			boolean showActionError, 
			Date currentTime) 
	{
		boolean controlliOk = true;

		try (FileInputStream file = new FileInputStream(document)) {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			boolean signVerified = checker.verifySignature(IOUtils.toByteArray(file), currentTime);
			if (!signVerified) {
				if(showActionError) 
					action.addActionError(action.getText("Errors.invalidSign"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il file " + documentFileName
							+ " non contiene una firma digitale valida");
				}
				controlliOk = false;
			}
		} catch (IOException e) {
			if(showActionError) 
				action.addActionError(action.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			controlliOk = false;
		} catch (DigitalSignatureException e) {
			if(showActionError) 
				action.addActionError(action.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.WARNING);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = false;
		}

		return controlliOk;
	}
	
	/**
	 * verifica la validita' di un documento in formato .xml
	 */
	private boolean checkDocumentXml(File document, String documentFileName, Event event) {
		LOG.info("checkDocumentXml");
		boolean controlliOk = false;
		
		return controlliOk;
	}
	
	/**
	 * Controllo di validita'  utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentP7m(
			File document, 
			String documentFileName, 
			Event event, 
			Date currentTime) 
	{
		return checkDocumentP7m(document, documentFileName, event, Boolean.FALSE, currentTime);
	}

	/**
	 * Controllo di validita'  utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param showActionError In caso di errore se a true inserisce una action error.
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentP7m(
			File document, 
			String documentFileName, 
			Event event, 
			boolean showActionError, 
			Date currentTime) 
	{
		return checkDocumentSign(
				document, 
				documentFileName, 
				event, 
				showActionError, 
				currentTime);
	}

	/**
	 * verifica la validita' di un documento .tsd 
	 */
	private boolean checkDocumentTsd(
			File document,
			String documentFileName,
			Date checkDate,
			Event event, 
			boolean signRequested) 
	{
		return checkDocumentTsd(
				document, 
				documentFileName, 
				checkDate, 
				event, 
				Boolean.FALSE, 
				signRequested);
	}
	
	/**
	 * verifica la validita' di un documento in formato .tsd
	 */
	private boolean checkDocumentTsd(
			File document, 
			String documentFileName, 
			Date checkDate, 
			Event event, 
			boolean showActionError, 
			boolean signedRequested) 
	{
		LOG.info("checkDocumentTsd");
		boolean controlliOk = true;
		try (FileInputStream file = new FileInputStream(document)) {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			byte[] stream = IOUtils.toByteArray(file);
			
//			boolean signVerified = checker.verifySignature(stream);
//			if (!signVerified) {
//				this.addActionError(this.getText("Errors.invalidSign"));
//				if (event != null) {
//					event.setLevel(Event.Level.ERROR);
//					event.setDetailMessage("Il file " + documentFileName
//							+ " non contiene una firma digitale valida");
//				}
//				controlliOk = false;
//			}
			
			// recupera la marcatura temporale...
			String verificaMarcheTemporaliXML = null;
			byte[] content = null;
			try {
				content = checker.getContentTimeStamp(stream);
			} catch (Exception e) {
				if (event != null) {
					event.setLevel(Event.Level.WARNING);
					event.setDetailMessage("Impossibile verificare la firma digitale per il file "
												   + documentFileName);
				}
				action.addActionError(action.getText("Errors.cannotVerifyTimestamp"));
				controlliOk = false;
			}
			if (controlliOk) {
				if (content != null) {
					byte[] xmlTimeStamps = null;
					try {
						xmlTimeStamps = checker.getXMLTimeStamps(content);
						//content = checker.getContent(content);	// recupera lo stream del file contenuto per estrarre l'estensione del file
					} catch (Exception e) {
						xmlTimeStamps = checker.getXMLTimeStamps(stream);
						//content = checker.getContent(content);	// recupera lo stream del file contenuto per estrarre l'estensione del file
					}
					verificaMarcheTemporaliXML = new String(xmlTimeStamps);
				} else {
					content = stream;
				}

				// recupera l'estensione del file contenuto...
				// (NB: per ora recupera l'estesione dal documentfilename anziche' leggere il contenuto binario)
				String fn = documentFileName.substring(0, documentFileName.length() - 4);   // prova.pdf.p7m => prova.pdf 
				boolean isP7m = isP7m(fn);
//				boolean isTsd = isTsd(fn); //UNUSED
				boolean isPdf = isPdf(fn);
				boolean isXml = isXml(fn);

				// recupera la firma digitale...
				boolean verificaFirma = false;
				String verificaFirmaDigitaleXML = null;
				if (isP7m) {
					byte[] xml = checker.getXMLSignature(content, checkDate);
					verificaFirmaDigitaleXML = new String(xml);
					verificaFirma = true;
				} else if (isPdf || isXml) {
					if (showActionError) action.addActionMessage(action.getText("Warning.signValidationNotSupported"));
					controlliOk = false;
				} else {
					if (signedRequested) {
						if (showActionError) action.addActionMessage(action.getText("Warning.signValidationNotSupported"));
//					if (showActionError) this.addActionError(this.getText("Errors.cannotVerifySign"));
//					if (event != null) {
//						event.setLevel(Event.Level.WARNING);
//						event.setDetailMessage("Impossibile verificare la firma digitale per il file " + documentFileName);
						controlliOk = false;
					}
				}

				// verifica la firma digitale...
				if (verificaFirma && StringUtils.isEmpty(verificaFirmaDigitaleXML)) {
					if (showActionError) action.addActionError(action.getText("Errors.invalidSign"));
					if (event != null) {
						event.setLevel(Event.Level.WARNING);
						event.setDetailMessage("Il file " + documentFileName
													   + " non contiene una firma digitale valida");
					}
					controlliOk = false;
				}

				// verifica la marcatura temporale...
				if ((verificaMarcheTemporaliXML == null)
						|| (verificaMarcheTemporaliXML != null && verificaMarcheTemporaliXML.isEmpty())) {
//	        	this.addActionError(this.getText("Errors.invalid???"));
//				if (event != null) {
//					event.setLevel(Event.Level.ERROR);
//					event.setDetailMessage("Il file " + documentFileName
//							+ " non contiene una marcatura temporale valida");
//				}
				}
			}

		} catch (DigitalSignatureException e) {
			if(showActionError) action.addActionError(action.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.WARNING);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = false;
		} catch (IOException e) {
			if (showActionError) action.addActionError(action.getText("Errors.fileNotSet"));
			if (event != null) event.setError(e);
			controlliOk = false;
		}

		return controlliOk;
	}	
	
    /**
     * Apache Tika file content validation (PORTAPPALT-1376)
     */
	public boolean checkTikaFileContent(File file, String filename, Event event) {
		return checkTikaFileContent(file, filename, action, event);
	}
	
	/**
	 * Apache Tika file content validation (PORTAPPALT-1376)
	 */
    public static boolean checkTikaFileContent(File file, String filename, BaseAction action, Event event) {
    	boolean validated = false;
    	
    	boolean isActive = false;
    	try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
	    	isActive = customConfigManager.isActiveFunction("UPLOADFILE", "CHECKCONTENUTO");
    	} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("checkTikaFileContent(..., " + filename + ")", t);
    	}
    	
	    if( !isActive ) {
    		validated = true;
    	} else {
    		// verifica il contenuto del file...
    		try {
	    		Tika tika = new Tika();
	    		
	    		// metodo A
	    	    byte[] contenuto = Files.readAllBytes(file.toPath());
	    	    String mediaTypeByContent = tika.detect(contenuto, filename);
	    	    String mediaTypeByExtension = tika.detect(filename);
	
//	    		// metodo B
//	    	    String mediaTypeByExtension = Files.probeContentType(file.toPath());
//	    		//String mediaTypeByExtension = URLConnection.guessContentTypeFromName(file.getName());
//	    		//FileNameMap fileNameMap = URLConnection.getFileNameMap();
//	    	    //String mediaTypeByExtension = fileNameMap.getContentTypeFor(file.getName());
//	    	 
//	    	    Magic magic = new Magic();
//	    	    MagicMatch match = magic.getMagicMatch(file, false);
//	    	    String mediaTypeByContent = (match != null ? match.getMimeType() : "");
	    	    
	    	    validated = checkTikaMediaType(mediaTypeByExtension, mediaTypeByContent);
    	    
	    	    ApsSystemUtils.getLogger().debug(validated + " =>" + mediaTypeByExtension + " , " + mediaTypeByContent + " FILE " + filename);
	    	} catch (Throwable t) {
				ApsSystemUtils.getLogger().error("checkTikaFileContent(..., " + filename + ")", t);
	    	}
	
	    	if( !validated ) {
		    	if(event != null) {
			    	action.addActionError(action.getText("Errors.contenutoNonValido"));
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il contenuto del file " + filename + " non sembra essere del tipo associato all'estensione");
				}
	    	}
    	}
    	
    	return validated;
    }
    
    private static boolean checkTikaMediaType(String mediaTypeByExtension, String mediaTypeByContent) {
		boolean esito = false;
		
		// eseguire i controlli
		if (mediaTypeByExtension.equals(mediaTypeByContent)) {
			// c'e' corrispondenza esatta tra contenuto del file e la sua estensione riportata nel nome
			esito = true;
		} else if (mediaTypeByExtension.startsWith("text/") && mediaTypeByContent.startsWith("text/")) {
			// alcuni file testuali, se analizzati a partire dal path del file stesso, vengono ritornati sempre come text/plain e non con il mime
			// type text/xxx legato all'estensione del file, ma bypasso la mancanza del match e considero il file comunque buono
			esito = true;
		} else if ("application/msword".equals(mediaTypeByExtension) && "application/x-tika-msoffice".equals(mediaTypeByContent)) {
			// i file .DOC vengono mappati come application/msword mentre analizzando il contenuto Tika li classifica come
			// application/x-tika-msoffice
			esito = true;
		} else if (mediaTypeByExtension.startsWith("application/vnd.openxmlformats-officedocument.")
				&& "application/x-tika-ooxml".equals(mediaTypeByContent)) {
			// i file .DOCX vengono mappati come application/vnd.openxmlformats-officedocument.wordprocessingml.document mentre analizzando il
			// contenuto Tika li classifica come application/x-tika-ooxml
			// i file .XLSX vengono mappati come application/vnd.openxmlformats-officedocument.spreadsheetml.sheet mentre analizzando il contenuto
			// Tika li classifica come application/x-tika-ooxml
			esito = true;
		} else if ("application/pkcs7-mime".equals(mediaTypeByExtension)
				&& ("application/pkcs7-signature".equals(mediaTypeByContent) || "application/x-dbf".equals(mediaTypeByContent))) {
			// i file .P7M vengono mappati in diversi modi distinti (prevalentemente sono application/pkcs7-signature) 
			esito = true;
		} else if ("application/vnd.ms-outlook".equals(mediaTypeByExtension) && "application/x-tika-msoffice".equals(mediaTypeByContent)) {
			// i file .MSG vengono mappati come application/vnd.ms-outlook mentre analizzando il contenuto Tika li classifica come
			// application/x-tika-msoffice
			esito = true;
		} else if ("application/vnd.ms-excel.sheet.macroenabled.12".equals(mediaTypeByExtension)
				&& "application/x-tika-ooxml".equals(mediaTypeByContent)) {
			// i file .XLSM vengono mappati come application/vnd.ms-excel.sheet.macroenabled.12 mentre analizzando il contenuto Tika li classifica
			// come application/x-tika-ooxml
			esito = true;
		} else if ("image/vnd.dxf".equals(mediaTypeByExtension) && mediaTypeByContent.startsWith("image/vnd.dxf")) {
			// i file .DXF vengono mappati come image/vnd.dxf postponendo pero' il suffisso "; format=ascii"
			esito = true;
		}
		
		return esito;
	}
	
    /**
     * Inserisce nella response il file allegato individuato dai parametri in input, effettuando la tracciatura sulla W_LOGEVENTI
     * @param nomeFile nome del documento
     * @param idProgramma
     *        identificativo del programma
     * @param idDocumento
     * @param codApp
     * @param codProfilo
     * @param idUtente
     * @param response
     * @param ip
     * @throws IOException
     * Verifica la presenza di codice malevolo all'interno di un file PDF.
     * Il metodo esegue diversi controlli di sicurezza sul documento PDF:
     * <ul>
     * <li>Verifica la presenza di JavaScript potenzialmente dannoso</li>
     * <li>Controlla le annotazioni per individuare link o azioni sospette</li>
     * <li>Analizza i file incorporati nel PDF</li>
     * <li>Cerca pattern malevoli nel testo estratto</li>
     *</ul>
     * I controlli vengono eseguiti solo se abilitati tramite il parametro di configurazione "uploadFile.checkPdfVulnerabile"
     *
     * @param stream array di byte contenente il documento PDF da analizzare
     * @param filename nome del file, utilizzato per le tracciature su log
     * @return true se viene rilevato contenuto potenzialmente malevolo, false altrimenti
     * @throws RuntimeException in caso di errori durante l'analisi del documento
     */
    private boolean isMaliciousPdf(byte[] stream, String filename, Event event) {
    	boolean malicious = false;
    	
		PDDocument document = null;
		try {
			document = PDDocument.load(stream);
			
			// test javascript malevolo
			PDDestinationOrAction action = document.getDocumentCatalog().getOpenAction();
			if (action != null && action instanceof PDActionJavaScript) {
				PDActionJavaScript jsAction = (PDActionJavaScript) action;
				String script = jsAction.getAction();
				malicious = hasPdfMaliciousJavaScript(script, filename);
			}
			
			// malicious e' la somma di tutti i controlli, la or com il booleano viene posta a destra in modo da lanciare prima tutte le funzioni
			malicious = hasMaliciousAnnotation(document, filename) || malicious;
			malicious = hasMaliciousEmbeddedFiles(document, filename) || malicious;

			if (!malicious) {
				// si aggiunge questo controllo sotto condizione in quanto la getText potrebbe causare una eccezione, allora
				// preferisco ritornare un esito con tutti i controlli errati loggati in precedenza anziche' andare a generare una eccezione
				PDFTextStripper stripper = new PDFTextStripper();
				String text = stripper.getText(document);
				malicious = hasMaliciousPattern(text, filename);
			}

			// al bisogno aggiungere altri controlli
			// ...
			// ...
			// ...

		} catch (IOException e) {
			ApsSystemUtils.getLogger().error("Impossibile analizzare il contenuto del file " + filename + " per rilevare eventuale codice malevolo", e);
			throw new RuntimeException(e);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					ApsSystemUtils.getLogger().error("Impossibile chiudere il documento " + filename, e);
				}
			}
		}
    	
		if(malicious) {
	    	if(event != null) {
		    	action.addActionError(action.getText("Errors.unsafePdfContent"));
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Il file " + filename + " sembra codificare dei contenuti non sicuri (malware)");
			}
    	}
		
    	return malicious;
    }

    /**
     * ...
     */
    public boolean isMaliciousPdf(File documento, String filename, Event event) {
    	boolean malicious = false;
        if(isFileExtension(filename, "pdf")) {
        	try {
    			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    			customConfigManager = (ICustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
    			if(customConfigManager.isActiveFunction("UPLOADFILE", "CHECKPDFVULNERABILE")) {
        			byte[] contenuto = FileUtils.readFileToByteArray(documento);
        			malicious =  isMaliciousPdf(contenuto, documentoFileName, event);
        		}
        	} catch (Exception ex) {
        		//action.addActionError(action.getText("Errors.unsafePdfContent"));
        		LOG.error("UploadValidator.validate()", ex);
			}
    	}
        return malicious;
    }
        
    /**
     * Testa alcuni pattern tipici che si possono rilevare all'interno di un pdf e sono considerati codice javascript malevolo.
     * @param script codice javascript rilevato nel pdf
     * @param filename nome del file, usato per le tracciature su log
     * @return true se il codice risulta malevolo, false altrimenti
     */
    private static boolean hasPdfMaliciousJavaScript(String script, String filename) {
    	boolean malicious = false;

    	// List of suspicious functions or patterns
    	String[] suspiciousPatterns = {"eval(", "setTimeout(", "setInterval(", "this.submitForm", "app.launchURL", "app.openDoc", "alert", "confirm", "prompt", "http://", "https://", "on\\w+\\s*="};
    	for (String pattern : suspiciousPatterns) {
    		if (script.contains(pattern)) {
    			ApsSystemUtils.getLogger().error("File " + filename + ", rilevato pattern malevolo " + pattern + " all'interno del file nella seguente sequenza: " + script);
    			malicious = true;
    		}
    	}

    	// Check for obfuscated code (e.g., long hex strings)
    	if (script.matches(".*([0-9A-Fa-f]{50,}).*")) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevato codice offuscato all'interno del file nella seguente sequenza: " + script);
    		malicious = true;
    	}

    	return malicious;
    }

    /**
     * Verifica la presenza di pattern malevoli all'interno del testo estratto da un file PDF.
     * Il metodo cerca pattern specifici come URL sospetti, domini di phishing e URL basati su IP.
     *
     * @param pdfText testo estratto dal file PDF da analizzare
     * @param filename nome del file, utilizzato per le tracciature su log
     * @return true se viene rilevato almeno un pattern malevolo, false altrimenti
     */
    private static boolean hasMaliciousPattern(String pdfText, String filename) {
    	boolean malicious = false;

    	// Ricerca di URL sospetti o malevoli (e.g., phishing)
    	// Check for suspicious URL patterns
    	Pattern maliciousPattern = Pattern.compile("(?i)(evil|hack|malware|phish|spam|attack|exploit)\\.\\w+");
    	Matcher maliciousMatcher = maliciousPattern.matcher(pdfText);
    	while (maliciousMatcher.find()) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevata URL potenzialmente malevola: " + maliciousMatcher.group());
    		malicious = true;
    	}

    	// Check for common phishing domains
    	Pattern phishingPattern = Pattern.compile("(?i)(paypa1|google-security|bank-verify|secure-login)\\.\\w+");
    	Matcher phishingMatcher = phishingPattern.matcher(pdfText);
    	while (phishingMatcher.find()) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", trovato possibile dominio di phishing: " + phishingMatcher.group());
    		malicious = true;
    	}

    	// Check for IP-based URLs
    	Pattern ipPattern = Pattern.compile("http[s]?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    	Matcher ipMatcher = ipPattern.matcher(pdfText);
    	while (ipMatcher.find()) {
    		ApsSystemUtils.getLogger().error("File " + filename + ",  trovato URL basato su IP: " + ipMatcher.group());
    		malicious = true;
    	}

    	return malicious;
    }

    /**
     * Verifica la presenza di annotazioni potenzialmente malevole all'interno di un documento PDF.
     * Il metodo analizza tutte le annotazioni presenti nel documento controllando:
     * <ul>
     * <li>link che contengono URI potenzialmente pericolosi</li>
     * <li>annotazioni contenenti JavaScript</li>
     * <li>contenuto sospetto nei dizionari delle annotazioni</li>
     *</ul>
     * @param document documento PDF da analizzare
     * @param filename nome del file PDF, utilizzato per le tracciature su log
     * @return true se viene rilevata almeno un'annotazione potenzialmente malevola, false altrimenti
     */
    private static boolean hasMaliciousAnnotation(PDDocument document, String filename) {
    	boolean malicious = false;

    	int pageCount = document.getNumberOfPages();
    	for (int i = 0; i < pageCount; i++) {
    		try {
    			List<PDAnnotation> annotations = document.getPage(i).getAnnotations();
    			for (PDAnnotation annotation : annotations) {
    				if (annotation instanceof PDAnnotationLink) {
    					PDAnnotationLink link = (PDAnnotationLink) annotation;
    					PDAction action = link.getAction();
    					if (action instanceof PDActionURI) {
    						PDActionURI uri = (PDActionURI) action;
    						String uriString = uri.getURI();
    						malicious = checkURIForMaliciousContent(uriString, filename) || malicious;
    					}
    					if (action instanceof PDActionJavaScript) {
    						PDActionJavaScript jsAction = (PDActionJavaScript) action;
    						String script = jsAction.getAction();
    						malicious =  hasPdfMaliciousJavaScript(script, filename) || malicious;
    					}
    				}

    				// Check the annotation's dictionary for suspicious content
    				COSDictionary annotDict = annotation.getCOSObject();
    				// Check the /V (value) entry
    				if (annotDict.containsKey("V")) {
    					Object value = annotDict.getDictionaryObject("V");
    					if (value instanceof COSString) {
    						String stringValue = ((COSString) value).getString();
    						malicious = checkURIForMaliciousContent(stringValue, filename) || malicious;
    					}
    				}

    				String contents = annotation.getContents();
    				malicious = checkURIForMaliciousContent(contents, filename) || malicious;
    			}
    		} catch (IOException e) {
    			ApsSystemUtils.getLogger().error("Impossibile analizzare il contenuto del file " + filename + " per rilevare eventuale codice malevolo", e);
    			throw new RuntimeException(e);
    		}
    	}
    	return malicious;
    }

    /**
     * Verifica la presenza di file embedded all'interno di un documento PDF che potrebbero contenere codice malevolo.
     * Il metodo analizza i file incorporati nel PDF verificando:
     * <ul>
     * <li>estensioni potenzialmente pericolose (exe, dll, bat, etc.)</li>
     * <li>contenuto sospetto nei file embedded</li>
     * <li>header di file eseguibili</li>
     * </ul>
     * @param document documento PDF da analizzare
     * @param filename nome del file PDF, utilizzato per le tracciature su log
     * @return true se viene rilevato almeno un file embedded potenzialmente malevolo, false altrimenti
     */
    private static boolean hasMaliciousEmbeddedFiles(PDDocument document, String filename) {
    	boolean malicious = false;
    	if (document.getDocumentCatalog().getNames() != null) {
    		PDEmbeddedFilesNameTreeNode embeddedFiles = document.getDocumentCatalog().getNames().getEmbeddedFiles();
    		try {
    			if (embeddedFiles != null && embeddedFiles.getNames() != null) {
    				for (Map.Entry<String, PDComplexFileSpecification> entry : embeddedFiles.getNames().entrySet()) {
    					PDComplexFileSpecification fileSpec = entry.getValue();
    					// Analyze each embedded file
    					malicious = malicious || analyzeEmbeddedFile(fileSpec.getEmbeddedFile(), fileSpec.getFile());
    				}
    			}
    		} catch (IOException e) {
    			ApsSystemUtils.getLogger().error("File " + filename + ", impossibile analizzare i file embedded", e);
    			throw new RuntimeException(e);
    		}
    	}

    	return malicious;
    }

    /**
     * Analizza il contenuto di un file embedded in un PDF per verificare la presenza di contenuti potenzialmente malevoli.
     * Il metodo controlla:
     * <ul>
     * <li>se il file ha un'estensione potenzialmente pericolosa (exe, dll, bat, etc.)</li>
     * <li>se il contenuto del file contiene pattern sospetti come header di file eseguibili</li>
     * </ul>
     * @param embeddedFile file embedded da analizzare
     * @param filename nome del file embedded
     * @return true se viene rilevato contenuto potenzialmente malevolo, false altrimenti
     * @throws IOException in caso di errori durante la lettura del file
     */
    private static boolean analyzeEmbeddedFile(PDEmbeddedFile embeddedFile, String filename) throws IOException {
    	boolean malicious = false;
    	// Check file extension
    	if (hasExecutableExtension(filename)) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevata estensione malevola");
    		malicious = true;
    	}

    	// Check file content (first few bytes)
    	byte[] content = embeddedFile.toByteArray();
    	if (containsSuspiciousContent(content)) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevato sospetto contenuto malevolo");
    		malicious = true;
    	}

    	return malicious;
    }

    /**
     * Verifica se il file ha un'estensione che potrebbe essere pericolosa.
     * Le estensioni considerate pericolose sono quelle tipicamente associate a file eseguibili
     * o script, come: .exe, .dll, .bat, .cmd, .vbs, .js, .sh, .jar
     *
     * @param filename nome del file da controllare
     * @return true se l'estensione del file è considerata pericolosa, false altrimenti
     */
    private static boolean hasExecutableExtension(String filename) {
    	String[] suspiciousExtensions = {".exe", ".dll", ".bat", ".cmd", ".vbs", ".js", ".sh", ".jar"};
    	for (String ext : suspiciousExtensions) {
    		if (filename.toLowerCase().endsWith(ext)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Verifica se il contenuto di un file embedded in un PDF contiene pattern sospetti o malevoli.
     * Il metodo controlla la presenza di header tipici di file eseguibili (es. MZ header per .exe)
     * e altri pattern potenzialmente pericolosi nel contenuto del file.
     *
     * @param content array di byte contenente il contenuto del file da analizzare
     * @return true se viene rilevato contenuto sospetto, false altrimenti
     */
    private static boolean containsSuspiciousContent(byte[] content) {
    	// Check for suspicious byte patterns, e.g., executable headers
    	byte[] exeHeader = {0x4D, 0x5A}; // MZ header for executables
    	if (content.length >= 2 && content[0] == exeHeader[0] && content[1] == exeHeader[1]) {
    		return true;
    	}
    	// Add more checks as needed
    	return false;
    }

    /**
     * Verifica se una URI all'interno di un pdf contiene contenuto potenzialmente malevolo.
     * Il metodo controlla la presenza di JavaScript, caratteri HTML, JavaScript codificato
     * e altri pattern sospetti all'interno dell'URI.
     *
     * @param uri stringa contenente l'URI da analizzare
     * @param filename nome del file, utilizzato per le tracciature su log
     * @return true se viene rilevato contenuto malevolo, false altrimenti
     */
    private static boolean checkURIForMaliciousContent(String uri, String filename) {
    	boolean malicious = false;
    	if (uri == null) return malicious;
    	// Check for JavaScript
    	if (uri.toLowerCase().contains("javascript:")) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevato JavaScript all'interno di una URI : " + uri);
    		malicious = true;
    	}
    	// Check for encoded JavaScript
    	if (uri.toLowerCase().contains("%3cscript") || uri.toLowerCase().contains("script%3e")) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevato encoded JavaScript all'interno di una URI : " + uri);
    		malicious = true;
    	}

    	// Check for HTML characters
    	if (uri.contains("<") || uri.contains(">")) {
    		ApsSystemUtils.getLogger().error("File " + filename + ", rilevato HTML all'interno di una URI : " + uri);
    		malicious = true;
    	}

    	// Add more checks as needed
    	// For example, check for other suspicious patterns or encodings

    	return malicious;
    }
    
    /**
     * Utility per il controllo della validit&agrave; dell'estensione di un file secondo la configurazione impostata da applicativo. Si
     * controllano tutte le estensioni associate al file (es: pdf.p7m.psd).
     *
     * @param nomeFile
     *        nome del file da controllare
     *
     * @return true se la configurazione non contiene valori e quindi qualsiasi estensione del file &egrave; ammessa, oppure l'estensione del
     *         file in input rientra in una delle estensioni ammesse, false altrimenti
     */
    private boolean isEstensioneFileAmmessa(String nomeFile, String estensioniAmmesse, Event event) {
      boolean esito = true;
      
      if (estensioniAmmesse != null) {
        // si controlla se il nome del file caricato rientra in una delle estensioni ammesse da configurazione
        if (nomeFile == null || nomeFile.indexOf('.') <= 0 || (nomeFile.indexOf('.') == (nomeFile.length()-1))) {
          // devono esistere file con un nome reale ed una suddivisione nome+"."+estensione
          esito = false;
        } else {
          Set<String> setEstensioni = new HashSet<String>(Arrays.asList(StringUtils.split(estensioniAmmesse.toUpperCase(), ',')));
          String nomeFileConEstensioni = nomeFile;
          while (nomeFileConEstensioni.contains(".")) {
            String estensione = StringUtils.substringAfterLast(nomeFileConEstensioni, ".");
            if (estensione != null) {
              if (!setEstensioni.contains(estensione.toUpperCase())) {
                // l'estensione non risulta tra quelle ammesse, quindi fallisco il controllo
                esito = false;
                break;
              }
            }
            // si itera sulla eventuale estensione precedente
            nomeFileConEstensioni = StringUtils.substringBeforeLast(nomeFileConEstensioni, ".");
          }
        }
      }
      if(!esito) {
    	  addActionErrorExtensionNotSupported(nomeFile, estensioniAmmesse, event);
      }
      
      return esito;
    }

    
}