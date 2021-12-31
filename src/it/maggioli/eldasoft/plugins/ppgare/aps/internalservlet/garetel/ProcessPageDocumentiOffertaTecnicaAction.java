package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfReader;


public class ProcessPageDocumentiOffertaTecnicaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6235887945333953499L;
	
	private static final String SUCCESS_BACK_TO_DOCUMENTI = "backToDocumenti";
	
	/** Memorizza la prossima dispatchAction da eseguire nel wizard */
	private String nextResultAction;
	
	public String getNextResultAction() {
		return nextResultAction;
	}

	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;

	private Long docRichiestoId;
	private File docRichiesto;
	private String docRichiestoContentType;
	private String docRichiestoFileName;
	private String docUlterioreDesc;
	private File docUlteriore;
	private String docUlterioreContentType;
	private String docUlterioreFileName;
	private Integer formato;
	private boolean docOffertaPresente;
	private InputStream inputStream;

	private int tipoBusta;
	private String codice;
	private int operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	private Date dataPresentazione;
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public Long getDocRichiestoId() {
		return docRichiestoId;
	}

	public void setDocRichiestoId(Long docRichiestoId) {
		this.docRichiestoId = docRichiestoId;
	}

	public File getDocRichiesto() {
		return docRichiesto;
	}

	public void setDocRichiesto(File docRichiesto) {
		this.docRichiesto = docRichiesto;
	}

	public String getDocRichiestoContentType() {
		return docRichiestoContentType;
	}

	public void setDocRichiestoContentType(String docRichiestoContentType) {
		this.docRichiestoContentType = docRichiestoContentType;
	}

	public String getDocRichiestoFileName() {
		return docRichiestoFileName;
	}

	public void setDocRichiestoFileName(String docRichiestoFileName) {
		this.docRichiestoFileName = docRichiestoFileName;
	}

	public String getDocUlterioreDesc() {
		return docUlterioreDesc;
	}

	public void setDocUlterioreDesc(String docUlterioreDesc) {
		this.docUlterioreDesc = docUlterioreDesc;
	}

	public File getDocUlteriore() {
		return docUlteriore;
	}

	public void setDocUlteriore(File docUlteriore) {
		this.docUlteriore = docUlteriore;
	}

	public String getDocUlterioreContentType() {
		return docUlterioreContentType;
	}

	public void setDocUlterioreContentType(String docUlterioreContentType) {
		this.docUlterioreContentType = docUlterioreContentType;
	}

	public String getDocUlterioreFileName() {
		return docUlterioreFileName;
	}

	public void setDocUlterioreFileName(String docUlterioreFileName) {
		this.docUlterioreFileName = docUlterioreFileName;
	}

	public Integer getFormato() {
		return formato;
	}

	public void setFormato(Integer formato) {
		this.formato = formato;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getOperazione() {
		return operazione;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}
	
	public boolean isDocOffertaPresente(){
		return docOffertaPresente;
	}
	
	public void setDocOffertaPresente(boolean docOffertaPresente){
		this.docOffertaPresente = docOffertaPresente;
	}

	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }

	public int getBUSTA_TECNICA() {	return PortGareSystemConstants.BUSTA_TECNICA; }

	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }


	/**
	 * costruttore 
	 */
	public ProcessPageDocumentiOffertaTecnicaAction() {
		super(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA, 
			  WizardOffertaTecnicaHelper.STEP_DOCUMENTI); 
	} 

	/**
	 * ... 
	 */
	@SkipValidation
	public String next() {
		// NON ESISTE LO STEP SUCCESSIVO!!!
		return SUCCESS;
	}
	
	/**
	 * ... 
	 */
	@SkipValidation
	public String back() {
		String target = SUCCESS; //= this.helperBack();

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) session
					.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
			
			this.nextResultAction = helper.getPreviousAction(WizardOffertaTecnicaHelper.STEP_DOCUMENTI);
			
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}
	
	/**
	 * ...
	 */
	public String addDocRich() {
		String target = SUCCESS_BACK_TO_DOCUMENTI;
				
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		WizardDocumentiBustaHelper documentiBustaHelper = helper.getDocumenti();

		if (documentiBustaHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			Event evento = null;
			try {
				String codice = ( !StringUtils.isEmpty(helper.getCodice()) 
						? helper.getCodice() 
						: helper.getGara().getCodice() );
				
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docRichiesto);

				// traccia l'evento di upload di un file...
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(documentiBustaHelper.getDescTipoBusta() + ": documento richiesto"
						+ " con id="+this.docRichiestoId
						+ ", file="+this.docRichiestoFileName
						+ ", dimensione="+dimensioneDocumento+"KB");
				
				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				boolean controlliOk = true;
				controlliOk = controlliOk && this.checkFileSize(this.docRichiesto, this.docRichiestoFileName, getActualTotalSize(documentiBustaHelper), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docRichiestoFileName, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.docRichiesto, this.docRichiestoFileName, this.formato, evento, onlyP7m);

				if (this.docRichiestoId.longValue() == helper.getIdOfferta().longValue()) {
					// se si carica l'offerta tecnica allora si controlla, se necessario, la hash del file firmato
					controlliOk = controlliOk && this.checkFileFirmato(this.docRichiesto, this.docRichiestoFileName, helper.getPdfUUID(), evento);
				}
				
				if (controlliOk) {
					if (!documentiBustaHelper.getDocRichiestiId().contains(this.docRichiestoId)) {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						documentiBustaHelper.addDocRichiesto(
								this.docRichiestoId,
								this.docRichiesto,
								this.docRichiestoContentType,
								this.docRichiestoFileName,
								evento);
						
						if(this.docRichiestoId.longValue() == helper.getIdOfferta().longValue()){
							documentiBustaHelper.setDocOffertaPresente(true);
						}
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
					}
				} else {
					target = INPUT;
				}
			
			} catch (InvalidPdfException e) {
				evento.setError(e);
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la lettura dell'allegato richiesto " + this.docRichiestoFileName);
				this.addActionError(this.getText("Errors.offertaTelematica.documento.fileNonLeggibile"));
				target = INPUT;
			} catch (IOException e) {
				evento.setError(e);
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} catch (GeneralSecurityException e) {
				evento.setError(e);
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} catch (Throwable t) {
				evento.setError(t);
				ApsSystemUtils.logThrowable(t, this, "addDocRich");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} finally {
				if(evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}
		}
		return target;
	}
	
	/**
	 * ...
	 */
	public String addUltDoc() {
		String target = SUCCESS_BACK_TO_DOCUMENTI;
		
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper)session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		WizardDocumentiBustaHelper documentiBustaHelper = helper.getDocumenti();

		if (documentiBustaHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			try {
				String codice = ( !StringUtils.isEmpty(helper.getCodice()) 
						? helper.getCodice() 
						: helper.getGara().getCodice() );

				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
				
				// traccia l'evento di upload di un file...
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(documentiBustaHelper.getDescTipoBusta() + ": documento ulteriore" 
						+ " con file="+this.docUlterioreFileName
						+ ", dimensione="+dimensioneDocumento+"KB");

				boolean controlliOk = true;
				if(StringUtils.isEmpty(this.docUlterioreFileName)) {
					this.addFieldError("docUlteriore", this.getText("Errors.offertaTelematica.docUlteriore.nomeNonValido"));
					controlliOk = false;
				}
				if(controlliOk){
					String nome = (this.docUlterioreFileName).substring(0, this.docUlterioreFileName.lastIndexOf('.')).toUpperCase().trim();
					if(nome.equals((PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_TECNICA).toUpperCase())){
						this.addFieldError("docUlteriore", this.getText("Errors.offertaTelematica.docUlteriore.nomeNonValido"));
						controlliOk = false;
					}
				}
				
				controlliOk = controlliOk && this.checkFileDescription(this.docUlterioreDesc, evento);
				controlliOk = controlliOk && this.checkFileSize(this.docUlteriore, this.docUlterioreFileName, getActualTotalSize(documentiBustaHelper), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docUlterioreFileName, evento);
				controlliOk = controlliOk && this.checkFileExtension(this.docUlterioreFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.docUlteriore, this.docUlterioreFileName, null, evento, false);

				if (controlliOk) {
					// si inseriscono i documenti in sessione	
					if (documentiBustaHelper.getDocUlterioriDesc().contains(this.docUlterioreDesc)) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
					} else {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						documentiBustaHelper.addDocUlteriore(
								this.docUlterioreDesc, 
								this.docUlteriore, 
								this.docUlterioreContentType, 
								this.docUlterioreFileName,
								evento);
						
						this.docUlterioreDesc = null;
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
					}
				} else {
					target = INPUT;
				}
				
				this.eventManager.insertEvent(evento);
				
			} catch (GeneralSecurityException e) {
				ApsSystemUtils.logThrowable(e, this, "addDocUlt", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "addDocUlt", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}

		return target;
	}
	
	/**
	 * "Torna al menu"
	 */
	public String quit() {
		String target = "quit";
		
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
			target = "quitToGestionBusteDistinte";
		}
		
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;			
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		}
		
		return target;
	}
		
	/**
	 * Estrae il file presente nel file firmato digitalmente per l'offerta tecnica 
	 * e verifica se il contenuto &egrave; il medesimo di quello generato dall'applicativo.
	 * 
	 * @param documento documento firmato da aprire
	 * @param documentoFilename nome del file associato al documento firmato da aprire
	 * @param pdfUUID UUID di verifica del file contenuto una volta estratta la firma
	 * 
	 * @return true se il file firmato digitalmente possiede l'UUID in input oppure 
	 *  	   se il controllo non deve essere effettuato da configurazione, 
	 *  	   false altrimenti
	 * @throws Exception 
	 */
	private boolean checkFileFirmato(File documento, String documentoFilename, String pdfUUID, Event evento) throws Exception {
		boolean esito = true;
		
		if (this.customConfigManager.isActiveFunction("GARE-DOCUMOFFERTA", "UPLOADPDFCOERENTEDATI")) {
			byte[] contenuto = null;
			String contentoFilename = null;

			// se arrivo qui ho gia' verificato in precedenza con checkFileFormat(...)
			// e non e' necessario ricontrollare...
//			if(this.checkFileFormat(
//					documento, documentoFilename, PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO, null, evento)) {				
				contenuto = this.getContenutoDocumentoFirmato(
						documento, documentoFilename, evento);
				contentoFilename = this.getFilenameDocumentoFirmato(
						documento, documentoFilename, evento);				
//			}					
			
			// in caso di PDF verifica se UUID e' quello atteso...
			if(contenuto != null && contentoFilename != null) {
				boolean isPdf = contentoFilename.toUpperCase().endsWith(".PDF");
				if(isPdf) {									
					PdfReader readInputPDF = new PdfReader(contenuto);
					HashMap<String, String> hMap = readInputPDF.getInfo();
					String testoPdf = StringUtilities.getPdfContentAsString(readInputPDF);
					String digestTestoPdf = StringUtilities.getSha256(testoPdf);
					if (!(pdfUUID.equals(hMap.get("Keywords")) && pdfUUID.equals(digestTestoPdf))) {
						String msg = this.getText("Errors.offertaTelematicaTecnica.hashContenutoFileFirmato");
						this.addActionError(msg);
						esito = false;
						if (evento != null) {
							evento.setLevel(Event.Level.ERROR);
							evento.setDetailMessage(msg);
						}
					}
				}
			} else {
				//...
			}
		}

		return esito;
	}
	
	/**
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	private int getActualTotalSize(WizardDocumentiBustaHelper helper) {
		int total = 0;
		for (Integer s : helper.getDocRichiestiSize()) {
			total += s;
		}
		for (Integer s : helper.getDocUlterioriSize()) {
			total += s;
		}
		return total;
	}
	
	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public String save() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);		

			if( !this.aggiornaAllegato() ) {
				target = INPUT;
			}
			
			this.nextResultAction = helper.getCurrentAction(WizardOffertaTecnicaHelper.STEP_DOCUMENTI);
		}
		return target;
	}
	
	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione
	 */
	protected boolean aggiornaAllegato() {
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		
		String target = ProcessPageDocumentiOffertaAction.saveDocumenti(
				this.codice,
				helper,
				this.session,
				this);
		
		return SUCCESS.equalsIgnoreCase(target);
	}
	
}
