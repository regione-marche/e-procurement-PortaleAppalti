package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Date;

/**
 * Action di gestione delle operazioni nella pagina dei documenti del wizard
 * d'iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageDocumentiAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 647490520354783711L;

	/**
	 * Riferimento al manager per la gestione dei parametri applicativo
	 */
	protected ICustomConfigManager customConfigManager;
	protected IAppParamManager appParamManager;
	protected IEventManager eventManager;

	protected Long docRichiestoId;
	protected File docRichiesto;
	@Validate(EParamValidation.CONTENT_TYPE)
	protected String docRichiestoContentType;
	@Validate(EParamValidation.FILE_NAME)
	protected String docRichiestoFileName;
	@Validate(EParamValidation.GENERIC)
	protected String docUlterioreDesc;
	protected File docUlteriore;
	@Validate(EParamValidation.CONTENT_TYPE)
	protected String docUlterioreContentType;
	@Validate(EParamValidation.FILE_NAME)
	protected String docUlterioreFileName;
	private Integer formato;

	protected InputStream inputStream;
	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	protected String nextResultAction;

	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public Long getDocRichiestoId() {
		return docRichiestoId;
	}

	public void setDocRichiestoId(Long idDocRichiesto) {
		this.docRichiestoId = idDocRichiesto;
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
		this.docUlterioreDesc = StringUtilities.fixEncodingMultipartField(docUlterioreDesc);
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

	/**
	 * tipologia messaggio per la tracciatura nel log eventi del portale 
	 */
	protected String getFunzione(WizardIscrizioneHelper iscrizioneHelper) {
		String funzione = "Iscrizione";
		if (iscrizioneHelper.isAggiornamentoIscrizione()) {
			if (iscrizioneHelper.isAggiornamentoSoloDocumenti()) {
				funzione = "Aggiornamento documenti";
			} else {
				funzione = "Aggiornamento dati/documenti";
			}
		} else if (iscrizioneHelper.isRinnovoIscrizione()) {
			funzione = "Rinnovo iscrizione";
		}
		return funzione;
	}

	/**
	 * ... 
	 */
	public String addDocRich() {
		String target = "backToDocumenti";
		
		boolean controlliOk = true;
		try {			
			WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

			if (iscrizioneHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docRichiesto);
	
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(iscrizioneHelper.getIdBando());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(this.getFunzione(iscrizioneHelper) + ": documento richiesto" 
						+ " con id=" + this.docRichiestoId
						+ ", file=" + this.docRichiestoFileName
						+ ", dimensione=" + dimensioneDocumento + "KB");
	
				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				controlliOk =
						checkFileSize(docRichiesto, docRichiestoFileName, getActualTotalSize(iscrizioneHelper.getDocumenti()), appParamManager, evento)
						&& checkFileName(docRichiestoFileName, evento)
						&& checkFileFormat(docRichiesto, docRichiestoFileName, formato, evento, onlyP7m);
	
				if (controlliOk) {
					Date checkDate = Date.from(Instant.now());
					DocumentiAllegatiFirmaBean checkFirma = checkFileSignature(
							docRichiesto
							, docRichiestoFileName
							, formato
							, checkDate
							, evento
							, onlyP7m
							, appParamManager
							, customConfigManager
					);
					// si inseriscono i documenti in sessione
					WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();

					if (Attachment.indexOf(documentiHelper.getRequiredDocs(), Attachment::getId, docRichiestoId) == -1) {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza

						documentiHelper.addDocRichiesto(
								this.docRichiestoId, 
								this.docRichiesto, 
								this.docRichiestoContentType, 
								this.docRichiestoFileName,
								evento,checkFirma);
							
						if( !this.aggiornaAllegato() ) {
							target = CommonSystemConstants.PORTAL_ERROR;
						}
					} else {
						target = INPUT;
					}
					this.eventManager.insertEvent(evento);
				}
			}
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich", 
					(controlliOk 
						? "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName
						: "Errore durante la verifica del formato dell'allegato richiesto " + this.docRichiestoFileName));
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich");
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * ... 
	 */
	public String addUltDoc() {
		String target = "backToDocumenti";
		try {
			WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

			if (iscrizioneHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
	
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(iscrizioneHelper.getIdBando());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(this.getFunzione(iscrizioneHelper) + ": documento ulteriore" 
								  + ", file=" + this.docUlterioreFileName
								  + ", dimensione=" + dimensioneDocumento + "KB");
	
				boolean controlliOk =
						checkFileDescription(docUlterioreDesc, evento)
						&& checkFileSize(docUlteriore, docUlterioreFileName, getActualTotalSize(iscrizioneHelper.getDocumenti()), appParamManager, evento)
						&& checkFileName(docUlterioreFileName, evento)
						&& checkFileExtension(docUlterioreFileName, appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento)
						&& checkFileFormat(docUlteriore, docUlterioreFileName, null, evento, false);


				if (controlliOk) {
//					String sha1 = null; 
//					try {
//						sha1 = this.getSha1Digest(this.docUlteriore, evento);
//					} catch (IOException e) {
//						ApsSystemUtils.logThrowable(e, this, "addUltDoc",
//								"In fase di caricamento di un documento ulteriore si è verificato un problema durante il calcolo del digest SHA1 del file per inserirlo nella tracciatura eventi");
//					}
					
					// si inseriscono i documenti in sessione
					WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();

					if (Attachment.indexOf(documentiHelper.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) != -1) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage(
								"Il file " + this.docUlterioreFileName +
								" viene scartato in quanto esiste già un documento ulteriore caricato con la stessa descrizione (" +
								this.docUlterioreDesc + ")");
					} else {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						Date checkDate = Date.from(Instant.now());
						DocumentiAllegatiFirmaBean checkFirma = this.checkFileSignature(this.docUlteriore, this.docUlterioreFileName, null,checkDate, evento, false, this.appParamManager, this.customConfigManager);
						evento.setMessage(this.getFunzione(iscrizioneHelper) + ": documento ulteriore" 
								  + ", file=" + this.docUlterioreFileName
								  + ", dimensione=" + dimensioneDocumento + "KB"
								  + ", descrizione=" + this.docUlterioreDesc);

						documentiHelper.addDocUlteriore(
								this.docUlterioreDesc, 
								this.docUlteriore, 
								this.docUlterioreContentType, 
								this.docUlterioreFileName,
								evento,checkFirma);
						
						if( !this.aggiornaAllegato() ) {
							target = CommonSystemConstants.PORTAL_ERROR;
						}
							
						this.docUlterioreDesc = null;
					}
				} else {
					target = INPUT;
				}
				this.eventManager.insertEvent(evento);
			}
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "addUltDoc", "Errore durante la cifratura dell'allegato ulteriore " + this.docUlterioreFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addUltDoc");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * ... 
	 */
	public String next() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		this.nextResultAction = helper.getNextAction(WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA);	
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		// nel caso di assenza di categorie si deve saltare alla pagina
		// precedente (a sua volta, se esiste un'unica stazione appaltante
		// si deve saltare direttamente alla pagina precedente)
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		this.nextResultAction = iscrizioneHelper.getPreviousAction(WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA);
		return target;
	}

	/**
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	protected int getActualTotalSize(WizardDocumentiHelper helper) {
		return Attachment.sumSize(helper.getRequiredDocs()) + Attachment.sumSize(helper.getRequiredDocs());
	}
	
	/**
	 * ...  
	 */
	protected boolean aggiornaAllegato() throws ApsException {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			target = SaveWizardIscrizioneAction.saveDocumenti(
					helper, 
					this.session, 
					this);
		}
		
		return (SUCCESS.equalsIgnoreCase(target));
	}

}
