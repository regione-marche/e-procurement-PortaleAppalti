package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.itextpdf.text.pdf.PdfReader;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dell'updload di un pdf per 
 * l'offerta di un'asta.
 *
 * @author ...
 */
public class ProcessPageUploadPdfAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

//	public static final int CHECKFILEFIRMATO_OK = 1;
//	public static final int CHECKFILEFIRMATO_ERRORE = 2;
//	public static final int CHECKFILEFIRMATO_HASH_ERRATO = 3;

	private static final String FUNZIONE = "Asta";
		
	private IAppParamManager appParamManager;
	private ICustomConfigManager customConfigManager;
	private IEventManager eventManager;
		
	private File docUlteriore;
	private String docUlterioreContentType;
	private String docUlterioreFileName;
	private String docUlterioreDesc; 		// per l'upload in modo che venga preso con il nome corretto
	private String filename; 				// per download  in modo che venga scaricato  con il nome corretta	
//	private int dimensioneAttualeFileCaricati;	
	private boolean deleteAllegato;			
	private String id;
	private String idAllegato;
	private InputStream inputStream;
	private String nextResultAction;
//	private String from;
	

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
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
		this.docUlterioreDesc = docUlterioreDesc;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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

	/**
	 * ...
	 */
	public ProcessPageUploadPdfAction() {
		 super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA, 
			   WizardOffertaAstaHelper.STEP_UPLOAD_PDF, 
			   true);
	} 

	/**
	 * ...
	 */
	@Override
	public String next() {
		String target = this.helperNext();
		
		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if(helper.getDocumenti().getDocUlterioriDesc().size() != 1) {
				this.addActionError(this.getText("Errors.mancaAllegatoOffertaAsta"));
				target = "backToUpload";
			}			
		}
	
		return target;
	}
	
	/**
	 * ...
	 */
	@Override
	public String back() {
		String target = this.helperBack();
		return (SUCCESS.equalsIgnoreCase(target) ? "back" : target);
	}

	/**
	 * Aggiunge un allegato all'elenco dei documenti.
	 * Dato che è concesso inviare 1 sola offerta d'asta (1 allegato) 
	 * aggiungere un nuovo documento sostituisce quello esistente.
	 */
	public String addUltDoc() {
		String target = "backToUpload";
		try{
			WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
			if(helper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				DocumentiComunicazioneHelper documenti = helper.getDocumenti();
				
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
				
				// traccia l'evento di upload di un file...
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(helper.getAsta().getCodice());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(FUNZIONE + ": conferma rilancio asta"
								  + ", file=" + this.docUlterioreFileName
								  + ", dimensione=" + dimensioneDocumento + "KB");
	
				// consenti di caricare un solo allegato per volta...
				if(documenti.getDocUlterioriDesc().size() > 0) {
					this.removeDocUlteriore(0);
				}			
		
				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				boolean controlliOk = true;
				controlliOk = controlliOk && this.checkFileSize(this.docUlteriore, this.docUlterioreFileName, getActualTotalSize(documenti.getDocUlterioriSize()), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docUlterioreFileName, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.docUlteriore, this.docUlterioreFileName, PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO, evento, onlyP7m);
				//controlliOk = controlliOk && this.checkFileExtension(this.docUlterioreFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento);
				//controlliOk = controlliOk && this.checkFileDescription(this.docUlterioreDesc, evento);
				
				if (controlliOk) {			
					// si inseriscono i documenti in sessione
					if(documenti.getDocUlterioriDesc().contains(this.docUlterioreDesc)) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
					} else {
						// verifica se l'hash del documento caricato corrisponde 
						// a quello inserito nella comunicazione FS13					 
						if(!this.checkFileFirmato(this.docUlteriore, this.docUlterioreFileName, helper.getPdfUUID(), evento)) {
							controlliOk = false;
						} 
						
						if(controlliOk) {
							// il documento ulteriore corrente è un qualsiasi altro documento
							//documenti.getDocUlterioriDesc().add(this.docUlterioreDesc);
							//documenti.getDocUlterioriSize().add(dimensioneDocumento);
							//documenti.getDocUlteriori().add(this.docUlteriore);
							//documenti.getDocUlterioriContentType().add(this.docUlterioreContentType);
							//documenti.getDocUlterioriFileName().add(this.docUlterioreFileName);
							documenti.addDocUlteriore(this.docUlterioreDesc, this.docUlteriore, this.docUlterioreContentType, this.docUlterioreFileName, evento);
							helper.putToSession();
						}
					}						
				}
				this.eventManager.insertEvent(evento);			
			}
		
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addUltDoc");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
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
		String target = "backToUpload";
		
		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
				
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			removeDocUlteriore(Integer.parseInt(this.id));
		}
		
		return target;
	}
	
	/**
	 * rimuovi un allegato ulteriore dai documenti dell'helper offerta asta
	 */
	private void removeDocUlteriore(int id) {
		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();

		// traccia la cancellazione di in allegato...
		Event evento = new Event();
		evento.setUsername(this.getCurrentUser().getUsername());
		evento.setDestination(helper.getAsta().getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.DELETE_FILE);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setMessage(FUNZIONE
						  + ": cancellazione conferma rilancio asta, file="
						  + helper.getDocumenti().getDocUlterioriFileName().get(id) 
						  + ", dimensione=" + helper.getDocumenti().getDocUlterioriSize().get(id) + "KB");
		
		//documenti.getDocUlterioriDesc().remove(id);
		//File file = documenti.getDocUlteriori().remove(id);
		//if (file.exists()) {
		//	file.delete();
		//}
		//documenti.getDocUlterioriContentType().remove(id);
		//documenti.getDocUlterioriFileName().remove(id);
		//documenti.getDocUlterioriSize().remove(id);
		
		helper.getDocumenti().removeDocUlteriore(id);
		helper.putToSession();
		
		this.eventManager.insertEvent(evento);
	}

	/**
	 * esegui il download dell'allegato 
	 */
	public String downloadAllegato() {
		String target = SUCCESS;
		// DA FARE!!!
		return target;
	}
		
	/**
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	private int getActualTotalSize(List<Integer> allegati) {
		int total = 0;
		for(int i = 0; i < allegati.size(); i++){
			total += allegati.get(i);
		}
		return total;
	}
	
	public Integer getLimiteTotaleUpload() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}

	public Integer getLimiteTotaleUploadDocIscrizione() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
	}
	
	/**
	 * Estrae il file presente nel file firmato digitalmente e verifica 
	 * se il contenuto &egrave; il medesimo di quello generato dall'applicativo.
	 * 
	 * @param documentoFirmato documento firmato da aprire
	 * @param pdfUUID UUID di verifica del file contenuto una volta estratta la firma
	 * @param action action che richiede la verifica
	 * @param customConfigManager 
	 * 
	 * @return true se il file firmato digitalmente possiede l'UUID in input 
	 *         oppure se il controllo non deve essere effettuato da configurazione, 
	 *         false altrimenti
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
						String msg = this.getText("Errors.offertaAsta.hashContenutoFileFirmato");
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

}
