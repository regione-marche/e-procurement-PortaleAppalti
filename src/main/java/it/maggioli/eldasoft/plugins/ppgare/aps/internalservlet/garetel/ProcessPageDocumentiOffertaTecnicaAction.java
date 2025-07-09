package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.itextpdf.text.exceptions.InvalidPdfException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
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
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.utils.PdfUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageDocumentiOffertaTecnicaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6235887945333953499L;
	
	private static final String SUCCESS_BACK_TO_DOCUMENTI = "backToDocumenti";
	
	/** Memorizza la prossima dispatchAction da eseguire nel wizard */
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

	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }

	public int getBUSTA_TECNICA() {	return PortGareSystemConstants.BUSTA_TECNICA; }

	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }


	/**
	 * costruttore 
	 */
	public ProcessPageDocumentiOffertaTecnicaAction() {
		super(BustaGara.BUSTA_TECNICA, //PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA, 
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
		String target = SUCCESS; 

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
			
			this.nextResultAction = helper.getPreviousAction(WizardOffertaTecnicaHelper.STEP_DOCUMENTI);
			
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
			BustaTecnica bustaTec = GestioneBuste.getBustaTecnicaFromSession();
			String codiceGara = bustaTec.getCodiceGara();
			String codiceLotto = (StringUtils.isNotEmpty(codice) ? codice : codiceGara);
			
			evento = VerificaDocumentiCorrotti.createNewEvent(bustaTec);
			
			VerificaDocumentiCorrotti validazione = new VerificaDocumentiCorrotti(evento);
			validazione.validate(bustaTec, codiceLotto);
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
		
		BustaTecnica bustaTec = GestioneBuste.getBustaTecnicaFromSession();
		WizardOffertaTecnicaHelper helper = bustaTec.getHelper();
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
				
				evento = getUploadValidator().getEvento();
				
				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(bustaTec)
						.setDocumento(docRichiesto)
						.setDocumentoFileName(docRichiestoFileName)
						.setDocumentoFormato(formato)
						.setCheckFileSignature(true)
						.setEventoDestinazione(codice)
						.setEventoMessaggio(bustaTec.getDescrizioneBusta() + ": documento richiesto"
											+ " con id=" + this.docRichiestoId
											+ ", file=" + this.docRichiestoFileName
											+ ", dimensione=" + FileUploadUtilities.getFileSize(this.docRichiesto) + "KB");				
				
				if ( getUploadValidator().validate() ) {
					boolean controlliOk = true;
					
					// se si carica l'offerta tecnica allora si controlla, se necessario, la hash del file firmato
					if (this.docRichiestoId.longValue() == helper.getIdOfferta().longValue()) {
						controlliOk = controlliOk && this.checkFileFirmato(
								this.docRichiesto, 
								this.docRichiestoFileName, 
								helper.getPdfUUID(), 
								getUploadValidator().getEvento());
					}

					if (controlliOk &&
						Attachment.indexOf(documentiBustaHelper.getRequiredDocs(), Attachment::getId, docRichiestoId) == -1) 
					{
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
								getUploadValidator().getCheckFirma()
						);
						
						if(this.docRichiestoId.longValue() == helper.getIdOfferta().longValue()){
							documentiBustaHelper.setDocOffertaPresente(true);
						}
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
					}
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
		
		BustaTecnica bustaTec = GestioneBuste.getBustaTecnicaFromSession();
		WizardOffertaTecnicaHelper helper = bustaTec.getHelper();
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

				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(bustaTec)
						.setDocumentoDescrizione(docUlterioreDesc)
						.setDocumento(docUlteriore)
						.setDocumentoFileName(docUlterioreFileName)
						.setOnlyP7m(false)
						.setCheckFileSignature(true)
						.setEventoDestinazione(codice)
						.setEventoMessaggio(bustaTec.getDescrizioneBusta() + ": documento ulteriore" 
								 			+ " con file=" + this.docUlterioreFileName
								 			+ ", dimensione=" + FileUploadUtilities.getFileSize(this.docUlteriore) + "KB")
					.addFilenameNonValido(PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_TECNICA,
										  "docUlteriore", 
										  "Errors.offertaTelematica.docUlteriore.nomeNonValido");

				if ( getUploadValidator().validate() ) {
					// si inseriscono i documenti in sessione
					if (Attachment.indexOf(documentiBustaHelper.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) != -1) {
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
								getUploadValidator().getEvento(),
								getUploadValidator().getCheckFirma());
						
						this.docUlterioreDesc = null;
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
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
	 * "Torna al menu"
	 */
	public String quit() {
		String target = "quit";
		
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();			
		
		if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
			target = "quitToGestionBusteDistinte";
		}
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;			
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
//			this.session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
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
					try {
						if (!(PdfUtils.hasCorrectHash(contenuto, pdfUUID))) {
							String msg = this.getText("Errors.offertaTelematicaTecnica.hashContenutoFileFirmato");
							this.addActionError(msg);
							esito = false;
							if (evento != null) {
								evento.setLevel(Event.Level.ERROR);
								evento.setDetailMessage(msg);
							}
						}
					} catch(Exception ex) {
						this.addActionError(this.getText("Errors.offertaTelematicaTecnica.contenutoFileFirmato"));
						esito = false;
						if (evento != null) {
							evento.setError(ex);
						}
					}
				} else {
					String msg = this.getText("Errors.offertaTelematica.contenutoP7MNonValido");
					this.addActionError(msg);
					esito = false;
					if (evento != null) {
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage(msg);
					}
				}
			} else {
				//...
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
	
	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public String save() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();			

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
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
		
		String target = ProcessPageDocumentiOffertaAction.saveDocumenti(
				this.codice,
				helper,
				this.session,
				this);
		
		return SUCCESS.equalsIgnoreCase(target);
	}
	
}
