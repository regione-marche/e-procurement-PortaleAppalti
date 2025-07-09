package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import com.itextpdf.text.exceptions.InvalidPdfException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.UploadValidator;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.utils.PdfUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Map;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageDocumentiOffertaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5132202495276748L;
	
	private final Logger logger = ApsSystemUtils.getLogger();
	
	private static final String SUCCESS_BACK_TO_DOCUMENTI = "backToDocumenti";

	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
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
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docRichiestoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docRichiestoFileName;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String docUlterioreDesc;
	private File docUlteriore;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docUlterioreContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docUlterioreFileName;
	private Integer formato;
	private boolean docOffertaPresente;
	private InputStream inputStream;

	private int tipoBusta;
	@Validate(EParamValidation.CODICE)
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
	
	public int getBUSTA_AMMINISTRATIVA() { 
		return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; 
	}
	
	public int getBUSTA_TECNICA() { 
		return PortGareSystemConstants.BUSTA_TECNICA; 
	}
	
	public int getBUSTA_ECONOMICA() { 
		return PortGareSystemConstants.BUSTA_ECONOMICA; 
	}
	
	/**
	 * ...
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
			
			this.nextResultAction = helper.getNextAction(WizardOffertaEconomicaHelper.STEP_DOCUMENTI);
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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
			
			this.nextResultAction = helper.getPreviousAction(WizardOffertaEconomicaHelper.STEP_DOCUMENTI);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}
	
	/**
	 * verifica l'integrita' della busta 
	 */
	public String check() {
		String target = SUCCESS_BACK_TO_DOCUMENTI;
		
		Event evento = null;
		try {
			BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
			String codiceGara = bustaEco.getCodiceGara();
			String codiceLotto = (StringUtils.isNotEmpty(codice) ? codice : codiceGara);
			
			evento = VerificaDocumentiCorrotti.createNewEvent(bustaEco);
			
			VerificaDocumentiCorrotti validazione = new VerificaDocumentiCorrotti(evento);
			validazione.validate(bustaEco, codiceLotto);
			if( !validazione.isErroriPresenti() ) {
				this.addActionMessage(this.getText("Envelope.prontaInvio"));
			} else {
				validazione.addActionErrors(this, evento);
				target = INPUT;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "check");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(t);
			}
		} finally {
			if(evento != null) {
				eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}

	/**
	 * ...
	 */
	public String addDocRich() {
		String target = SUCCESS_BACK_TO_DOCUMENTI;
		
		boolean controlliOk = false;
		
		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();			
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();			
		WizardDocumentiBustaHelper documentiBustaHelper = helper.getDocumenti();

		if (documentiBustaHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			
		} else if( SUCCESS_BACK_TO_DOCUMENTI.equals(target) ) {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			Event evento = null;
			try {
				String codice = ( !StringUtils.isEmpty(helper.getCodice()) 
						? helper.getCodice() 
						: helper.getGara().getCodice() );
				
				evento = getUploadValidator().getEvento();
				
				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(bustaEco)
						.setDocumento(docRichiesto)
						.setDocumentoFileName(docRichiestoFileName)
						.setDocumentoFormato(formato)
						//.setOnlyP7m( customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M") )
						.setCheckFileSignature(true)
						.setEventoDestinazione(codice)
						.setEventoMessaggio(bustaEco.getDescrizioneBusta() + ": documento richiesto"
											+ " con id=" + this.docRichiestoId
											+ ", file=" + this.docRichiestoFileName
											+ ", dimensione=" + FileUploadUtilities.getFileSize(this.docRichiesto) + "KB");					
				
				if ( getUploadValidator().validate() ) {
					controlliOk = true;
					
					// se si carica l'offerta economica allora si controlla, se necessario, la hash del file firmato
					if (docRichiestoId.longValue() == helper.getIdOfferta().longValue())
						controlliOk = checkFileFirmato(docRichiesto, docRichiestoFileName, helper.getPdfUUID(), evento);

					if (controlliOk &&
							Attachment.indexOf(documentiBustaHelper.getRequiredDocs(), Attachment::getId, docRichiestoId) == -1) {
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
								getUploadValidator().getEvento(),
								getUploadValidator().getCheckFirma());
						
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
				if (controlliOk) {
					ApsSystemUtils.logThrowable(t, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
				} else {
					ApsSystemUtils.logThrowable(t, this, "addDocRich", "Errore durante la verifica del formato dell'allegato richiesto " + this.docRichiestoFileName);
				}
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
		
		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
		WizardDocumentiBustaHelper documentiBustaHelper = helper.getDocumenti();

		if (documentiBustaHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			
		} else if( SUCCESS_BACK_TO_DOCUMENTI.equals(target) ) {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			try {
				String codice = ( !StringUtils.isEmpty(helper.getCodice()) 
						? helper.getCodice() 
						: helper.getGara().getCodice() );

				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(bustaEco)
						.setDocumentoDescrizione(docUlterioreDesc)
						.setDocumento(docUlteriore)
						.setDocumentoFileName(docUlterioreFileName)
						.setCheckFileSignature(true)
						.setEventoDestinazione(codice)
						.setEventoMessaggio(bustaEco.getDescrizioneBusta() + ": documento ulteriore" 
											+ " con file=" + this.docUlterioreFileName
											+ ", dimensione=" + FileUploadUtilities.getFileSize(this.docUlteriore) + "KB")
					.addFilenameNonValido(PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_ECONOMICA,
										  "docUlteriore", 
										  "Errors.offertaTelematica.docUlteriore.nomeNonValido");
  					
				if ( getUploadValidator().validate() ) {
					// si inseriscono i documenti in sessione	
					if (Attachment.indexOf(documentiBustaHelper.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) != -1) {
						addActionError(getText("Errors.docUlteriorePresent"));
					} else {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						documentiBustaHelper.addDocUlteriore(
							docUlterioreDesc
							, docUlteriore
							, docUlterioreContentType
							, docUlterioreFileName
							, getUploadValidator().getEvento()
							, getUploadValidator().getCheckFirma()
						);
						
						docUlterioreDesc = null;

						if (!this.aggiornaAllegato())
							target = INPUT;
					}
				} else {
					target = INPUT;
				}
				
				this.eventManager.insertEvent(getUploadValidator().getEvento());
				
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
	 * ...
	 */
	public String quit() {
		String target = "quit";
		
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		
		if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
			target = "quitToGestionBusteDistinte";
		}
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;			
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
//			session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		}
		return target;
	}
	
	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public String save() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
			
			if( !this.aggiornaAllegato() ) {
				target = INPUT;
			} 
			
			this.nextResultAction = helper.getNextAction(WizardOffertaEconomicaHelper.STEP_DOCUMENTI);
		}
		
		return target;
	}
	
	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione
	 */
	protected boolean aggiornaAllegato() {
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		
		String target = saveDocumenti(
				this.codice,
				helper,
				this.session,
				this);
		
		return SUCCESS.equalsIgnoreCase(target);
	}
	
	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public static String saveDocumenti(
			String codice,
			Object helperOfferta,
			Map<String, Object> session,
			BaseAction action) 
	{
		String target = SUCCESS;

		if( !(helperOfferta instanceof WizardOffertaEconomicaHelper
			  || helperOfferta instanceof WizardOffertaTecnicaHelper) ) {  
			// non gestito... esci
			return ERROR;
		}
		
		WizardOffertaHelper helper = (WizardOffertaHelper) helperOfferta;  
		WizardDocumentiBustaHelper documentiBustaHelper = helper.getDocumenti();

		if (null != action.getCurrentUser()
			&& !action.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME) 
			&& documentiBustaHelper != null 
			&& SUCCESS.equals(target)) 
		{
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(codice, action)) {
				return CommonSystemConstants.PORTAL_ERROR;
			}
			
			GestioneBuste buste = GestioneBuste.getFromSession();
			BustaEconomica bustaEco = buste.getBustaEconomica();
			BustaTecnica bustaTec = buste.getBustaTecnica();
			
			try {
				// Invio della busta tecnica FS11B / economica FS11C
				if(helperOfferta instanceof WizardOffertaEconomicaHelper) {					
					bustaEco.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				} else {
					bustaTec.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				}
				
				documentiBustaHelper.setDatiModificati(false);
				helper.setDatiModificati(false);
				
				buste.putToSession();
				
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				ExceptionUtils.manageExceptionError(e, action);
				target = ERROR;
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				action.addActionError(action.getText("Errors.cannotLoadAttachments"));
				target = ERROR;
			} catch (GeneralSecurityException e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				action.addActionError(action.getText("Errors.cannotLoadAttachments"));
				target = ERROR;
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				action.addActionError(action.getText("Errors.save.outOfMemory"));
				target = INPUT;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				ExceptionUtils.manageExceptionError(e, action);
				target = ERROR;
			}
		} else {
			action.addActionError(action.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}
	
	/**
	 * Estrae il file presente nel file firmato digitalmente per l'offerta economica 
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
				contenuto = this.getContenutoDocumentoFirmato(documento, documentoFilename, evento);
				contentoFilename = this.getFilenameDocumentoFirmato(documento, documentoFilename, evento);				
//			}
			
			// in caso di PDF verifica se UUID e' quello atteso...
			if(contenuto != null && contentoFilename != null) {
				boolean isPdf = contentoFilename.toUpperCase().endsWith(".PDF");
				if(isPdf) {
					try {
						if (StringUtils.isNotEmpty(pdfUUID) && !(PdfUtils.hasCorrectHash(contenuto, pdfUUID))) {
							String msg = this.getText("Errors.offertaTelematica.hashContenutoFileFirmato");
							this.addActionError(msg);
							esito = false;
							if (evento != null) {
								evento.setLevel(Event.Level.ERROR);
								evento.setDetailMessage(msg);
							}
						}
					} catch(Exception ex) {
						this.addActionError(this.getText("Errors.offertaTelematica.contenutoFileFirmato"));
						esito = false;
						if (evento != null) {
							evento.setError(ex);
						}	
					}
				} else {
					// documenti firmati digitalmente che NON sono PDF...
					String msg = this.getText("Errors.offertaTelematica.contenutoP7MNonValido");
					this.addActionError(msg);
					esito = false;
					if (evento != null) {
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage(msg);
					}
				}
			} else {
				// ERRORE/MESSAGGIO ???
				String msg = this.getText("Errors.offertaTelematica.contenutoP7MNonValido");
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
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	private int getActualTotalSize(WizardDocumentiBustaHelper helper) {
		return helper.getTotalSize();
	}

}
