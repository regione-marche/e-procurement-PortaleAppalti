package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.utils.PdfUtils;
import java.io.File;
import java.io.InputStream;

/**
 * Action di gestione delle operazioni nella pagina dell'updload di un pdf per 
 * l'offerta di un'asta.
 *
 * @author ...
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.ASTA })
public class ProcessPageUploadPdfAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5388330394631666488L;


	private static final String FUNZIONE = "Asta";
		
	private IAppParamManager appParamManager;
	private ICustomConfigManager customConfigManager;
	private IEventManager eventManager;
		
	private File docUlteriore;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docUlterioreContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docUlterioreFileName;
	@Validate(EParamValidation.DESCRIZIONE)
	private String docUlterioreDesc; 		// per l'upload in modo che venga preso con il nome corretto
	@Validate(EParamValidation.FILE_NAME)
	private String filename; 				// per download  in modo che venga scaricato  con il nome corretta	
//	private int dimensioneAttualeFileCaricati;
	private boolean deleteAllegato;
	@Validate(EParamValidation.GENERIC)
	private String id;
	@Validate(EParamValidation.INTERO)
	private String idAllegato;
	private InputStream inputStream;
	@Validate(EParamValidation.ACTION)
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
		this.docUlterioreDesc = StringUtilities.fixEncodingMultipartField(docUlterioreDesc);
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
			if (helper.getDocumenti().getAdditionalDocs().size() != 1) {
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
	 * Dato che e' concesso inviare 1 sola offerta d'asta (1 allegato) 
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
				WizardDocumentiBustaHelper documenti = helper.getDocumenti();

				// consenti di caricare un solo allegato per volta...
				if(documenti.getAdditionalDocs().size() > 0) {
					this.removeDocUlteriore(0);
				}
		
				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(helper)
						.setDocumentoDescrizione(docUlterioreDesc)
						.setDocumento(docUlteriore)
						.setDocumentoFileName(docUlterioreFileName)
						.setCheckFileSignature(true)
						.setEventoDestinazione(helper.getAsta().getCodice())
						.setEventoMessaggio(FUNZIONE + ": conferma rilancio asta"
								  			+ ", file=" + this.docUlterioreFileName
								  			+ ", dimensione=" + FileUploadUtilities.getFileSize(this.docUlteriore) + "KB");

				if ( getUploadValidator().validate() ) {
					// si inseriscono i documenti in sessione
					if (Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) != -1) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
					} else {
						// verifica se l'hash del documento caricato corrisponde 
						// a quello inserito nella comunicazione FS13
						boolean controlliOk = checkFileFirmato(
								this.docUlteriore, 
								this.docUlterioreFileName, 
								helper.getPdfUUID(), 
								getUploadValidator().getEvento()
						);
						
						if(controlliOk) {
							// il documento ulteriore corrente � un qualsiasi altro documento
							documenti.addDocUlteriore(
									this.docUlterioreDesc, 
									this.docUlteriore, 
									this.docUlterioreContentType, 
									this.docUlterioreFileName, 
									getUploadValidator().getEvento(), 
									getUploadValidator().getCheckFirma()
							);
							saveAllegati(helper);
							helper.putToSession();
						}
					}
				}

				this.eventManager.insertEvent(getUploadValidator().getEvento());
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
						  + helper.getDocumenti().getAdditionalDocs().get(id).getFileName()
						  + ", dimensione=" + helper.getDocumenti().getAdditionalDocs().get(id).getSize() + "KB");
		
		helper.getDocumenti().removeDocUlteriore(id);
		saveAllegati(helper);
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
		
//	/**
//	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
//	 *
//	 * @return totale in KB dei file caricati
//	 */
//	private int getActualTotalSize(List<Attachment> attachments) {
//		return Attachment.sumSize(attachments);
//	}
	
//	public Integer getLimiteTotaleUpload() {
//		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
//	}
//
//	public Integer getLimiteUploadFile() {
//		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
//	}
//
//	public Integer getLimiteTotaleUploadDocIscrizione() {
//		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
//	}
	
	/**
	 * Estrae il file presente nel file firmato digitalmente e verifica 
	 * se il contenuto &egrave; il medesimo di quello generato dall'applicativo.
	 * 
	 * @param pdfUUID UUID di verifica del file contenuto una volta estratta la firma
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
					try {
						if (!(PdfUtils.hasCorrectHash(contenuto, pdfUUID))) {
							String msg = this.getText("Errors.offertaAsta.hashContenutoFileFirmato");
							this.addActionError(msg);
							esito = false;
							if (evento != null) {
								evento.setLevel(Event.Level.ERROR);
								evento.setDetailMessage(msg);
							}
						}
					} catch(Exception ex) {
						this.addActionError(this.getText("Errors.offertaAsta.hashContenutoFileFirmato"));
						esito = false;
						if (evento != null) {
							evento.setError(ex);
						}
					}
				} else {
					String msg = this.getText("Errors.offertaAsta.hashContenutoFileFirmato");
					this.addActionError(msg);
					esito = false;
					if (evento != null) {
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage(msg);
					}
				}
			} else {
				//...
				String msg = this.getText("Errors.offertaAsta.hashContenutoFileFirmato");
				this.addActionError(msg);
				esito = false;
				if (evento != null) {
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(msg);
				}
			}
		}

		return esito;
	}

	/**
	 * invia la comunicazione con gli allegati 
	 */
	private boolean saveAllegati(WizardOffertaAstaHelper helper) {
		boolean inviata = false;
		try {
			// Utilizza la classe "SendComunicazioni" per gestire 
			// l'invio della FS12 ed includere gli allegati della comunicazione (pdf offerta asta)...
			ComunicazioniUtilities comunicazione = new ComunicazioniUtilities(
					this,
					this.getRequest().getSession().getId());

			// invia la comunicazione FS12 con gli allegati (pdf offerta d'asta) in stato BOZZA...
			comunicazione.sendRichiestaInvioComunicazioneOffertaAsta(
					helper,
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			
			// aggiorna nella FS13 il riferimento alla FS12 (FS13.COMKEY3 = FS12.idcom)...
			comunicazione.sendComunicazioneGenerazioneOffertaAsta(
					helper,
					false);

			inviata = true;
		} catch(Throwable t) {
			inviata = false;
			ApsSystemUtils.getLogger().error("saveAllegati", t);
		}
		return inviata; 
	}
	
}
