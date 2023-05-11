package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
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
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class ProcessPageDocumentiNuovaComunicazioneAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6262926399547520939L;
	
	protected IAppParamManager appParamManager;
	protected IEventManager eventManager;
	protected ICustomConfigManager customConfigManager;
	
	private File docUlteriore;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docUlterioreContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docUlterioreFileName;
	@Validate(EParamValidation.GENERIC)
	private String docUlterioreDesc; 	// per l'upload in modo che venga preso con il nome corretto
	
	protected int dimensioneAttualeFileCaricati;
	protected boolean deleteAllegato;
	@Validate(EParamValidation.DIGIT)
	protected String id;
	@Validate(EParamValidation.DIGIT)
	protected String idAllegato;
	@Validate(EParamValidation.FILE_NAME)
	protected String filename; 			// per download in modo che venga scaricato con il nome corretto
	protected InputStream inputStream;
	@Validate(EParamValidation.ACTION)
	protected String nextResultAction;
	@Validate(EParamValidation.ACTION)
	protected String from;

	public Map<String, Object> getSession() {
		return session;
	}

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	/**
	 * @param customConfigManager the customConfigManager to set
	 */
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
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

	public String getDocUlterioreDesc() {
		return docUlterioreDesc;
	}

	public void setDocUlterioreDesc(String docUlterioreDesc) {
		this.docUlterioreDesc = StringUtilities.fixEncodingMultipartField(docUlterioreDesc);
	}

	public int getDimensioneAttualeFileCaricati() {
		return dimensioneAttualeFileCaricati;
	}

	public void setDimensioneAttualeFileCaricati(int dimensioneAttualeFileCaricati) {
		this.dimensioneAttualeFileCaricati = dimensioneAttualeFileCaricati;
	}

	public boolean isDeleteAllegato() {
		return deleteAllegato;
	}

	public void setDeleteAllegato(boolean deleteAllegato) {
		this.deleteAllegato = deleteAllegato;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdAllegato() {
		return idAllegato;
	}

	public void setIdAllegato(String idAllegato) {
		this.idAllegato = idAllegato;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public void setNextResultAction(String nextResultAction) {
		this.nextResultAction = nextResultAction;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}	
	
	/**
	 * aggiungi un allegato ulteriore alla comunicazione 
	 */
	public String addUltDoc() {
		String target = "backToDocumenti";

		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		
		Event evento = null;
		try {
			DocumentiComunicazioneHelper documenti = helper.getDocumenti();
	
			int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
	
			// traccia l'evento di upload di un file...
			evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(helper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Invia nuova comunicazione: " + 
					"allegato file=" + this.docUlterioreFileName + ", " + 
					"dimensione=" + dimensioneDocumento + "KB");
	

			//???
			//In caso il file fosse troppo grande, si va in nullPointerException nel recuperare il nome del file
			//String nome = (this.docUlterioreFileName).substring(0, this.docUlterioreFileName.lastIndexOf('.')).toUpperCase().trim();
			boolean controlliOk =
					checkFileDescription(docUlterioreDesc, evento)
					&& checkFileSize(
							docUlteriore
							, docUlterioreFileName
							, Attachment.sumSize(documenti.getAdditionalDocs())
							, FileUploadUtilities.getLimiteUploadFile(appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_UPLOAD_FILE)
							, FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE)
							, evento
					) && checkFileName(docUlterioreFileName, evento)
					&& checkFileExtension(docUlterioreFileName, appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento)
					&& checkFileFormat(docUlteriore, docUlterioreFileName, null, evento, false);
	
			if (controlliOk) {
				Date checkDate = Date.from(Instant.now());
				DocumentiAllegatiFirmaBean checkFirma =  this.checkFileSignature(this.docUlteriore, this.docUlterioreFileName, null, checkDate, evento, Boolean.FALSE, this.appParamManager, this.customConfigManager);
				// si inseriscono i documenti in sessione
				if (Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) != -1) {
					this.addActionError(this.getText("Errors.docUlteriorePresent"));
				} else {
					documenti.addDocUlteriore(
							docUlterioreDesc
							, docUlteriore
							, docUlterioreContentType
							, docUlterioreFileName
							, evento
							, checkFirma
					);
	
					//if( !this.aggiornaAllegato() ) {
					//	target = CommonSystemConstants.PORTAL_ERROR; 
					//}
	
					//this.session.put("documenti", this.documenti);
				}
			}
		
		} catch (GeneralSecurityException e) {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable e) {
			this.addActionError(this.getText("Errors.unexpected"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}

	/**
	 * ...
	 */
	public String downloadAllegato() {
		String target = SUCCESS;
		
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		DocumentiComunicazioneHelper documentiHelper = helper.getDocumenti();
		
		if (documentiHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		} else {
			try {
				int index = Integer.parseInt(id);
				Attachment attachment = documentiHelper.getAdditionalDocs().get(index);
				docUlterioreContentType = attachment.getContentType();
				//this.docUlterioreFileName = documentiHelper.getDocUlterioriFileName().get(Integer.parseInt(this.id));
				setFilename(attachment.getFileName());
				inputStream = documentiHelper.downloadDocUlteriore(index);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegato");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		}
		return target;
	}

	/**
	 * Chiede la conferma per l'eliminazione di un allegato.
	 */
	public String confirmDeleteAllegato() {
		String target = SUCCESS;
		this.setDeleteAllegato(true);
		return target;
	}	

	/**
	 * Elimina un documento allegato inserito dall'utente.
	 */
	public String deleteAllegato() {
		String target = "backToDocumenti";
		
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			DocumentiComunicazioneHelper documentiHelper = helper.getDocumenti();
			int id = Integer.parseInt(this.id);
			
			// traccia l'evento di rimozione di un allegato...
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(helper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Invia nuova comunicazione: " +
					"cancellazione file=" + documentiHelper.getAdditionalDocs().get(id).getFileName() + ", " +
					"dimensione=" + documentiHelper.getAdditionalDocs().get(id).getSize() + "KB");
			
			documentiHelper.removeDocUlteriore(id);
			
			//if( !this.aggiornaAllegato() ) {
			//	target = CommonSystemConstants.PORTAL_ERROR; 
			//}
			
			this.eventManager.insertEvent(evento);
		}
		return target;
	}

	/**
	 * ... 
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) session
					.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
			
			this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
					helper.getNextStepNavigazione(WizardNuovaComunicazioneHelper.STEP_DOCUMENTI)));
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}
	
	/**
	 * ...
	 */
	public String back() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) session
					.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
			
			this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
					helper.getPreviousStepNavigazione(WizardNuovaComunicazioneHelper.STEP_DOCUMENTI)));

		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}
	
	/**
	 * ... 
	 */
	public Integer getLimiteTotaleUpload() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(this.appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE);
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_UPLOAD_FILE);
	}

//	// VIENE ANCORA UTILIZZATO ?
//	public Integer getLimiteTotaleUploadDocIscrizione() {
//		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
//	}

//	/**
//	 * salva/rimuovi un allegato ed invia la relativa comunicazione
//	 */
//	private boolean aggiornaAllegato() {
//		String target = SUCCESS;
//		//...
//		// DA FARE ??? SERVE ???
//		//...
//		return SUCCESS.equalsIgnoreCase(target);
//	}
	
}
