package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
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
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Action di gestione delle operazioni nella pagina dei documenti delle buste
 * economica, tecnica, amministrativa
 *
 * @author Marco.Perazzetta
 */
public class ProcessPageDocumentiBustaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 647490520354773711L;
	private static final Logger logger = ApsSystemUtils.getLogger();
	
	private static final String CANCEL 							= "cancel";
	private static final String BACK							= "back";
	private static final String BACK_TO_DOCUMENTI				= "backToDocumenti";
	private static final String BACK_TO_GESTIONE_BUSTE_DISTINTE = "backToGestioneBusteDistinte";
	private static final String BACK_TO_BUSTE_TECNICHE_LOTTI 	= "backToBusteTecnicheLotti";
	private static final String BACK_TO_BUSTE_ECONOMICHE_LOTTI 	= "backToBusteEconomicheLotti";
	
	protected ICustomConfigManager customConfigManager;
	protected IAppParamManager appParamManager;
	protected IEventManager eventManager;
	protected IComunicazioniManager comunicazioniManager;
	
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
	protected Integer formato;

	protected InputStream inputStream;

	protected int tipoBusta;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	@Validate(EParamValidation.CODICE)
	protected String codiceGara;
	protected int operazione;
	protected Date dataPresentazione;
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

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
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

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}
	
	public String getNextResultAction() {
		return nextResultAction;
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
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	protected int getActualTotalSize(WizardDocumentiBustaHelper helper) {
		return Attachment.sumSize(helper.getRequiredDocs()) + Attachment.sumSize(helper.getRequiredDocs());
	}

	/**
	 * aggiungi un documento richiesto alla busta 
	 */
	public String addDocRich() {
		String target = BACK_TO_DOCUMENTI;
		
		Event evento = null;
		boolean controlliOk = false;
		try {
			BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
			WizardDocumentiBustaHelper documentiBustaHelper = busta.getHelperDocumenti();
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
				// la sessione non e' scaduta, per cui proseguo regolarmente
			} else {
				String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
				if(StringUtils.isEmpty(codice)) {
					codice = this.codiceGara;
				}
				
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docRichiesto);
				
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(busta.getDescrizioneBusta() + ": documento richiesto" 
						+ " con id=" + this.docRichiestoId
						+ ", file=" + this.docRichiestoFileName
						+ ", dimensione=" + dimensioneDocumento + "KB");
				
				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				controlliOk =
							checkFileSize(docRichiesto, docRichiestoFileName, getActualTotalSize(documentiBustaHelper), appParamManager, evento)
							&& checkFileName(docRichiestoFileName, evento)
							&& checkFileFormat(docRichiesto, docRichiestoFileName, formato, evento, onlyP7m);
				logger.info("Controlli per i file prima del ws: {}",controlliOk);
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
					logger.info("ProcessPageDocumentiBustaAction -> checkFirma: {}",checkFirma);
					logger.info("Controlli per i file dopo ws: {}",controlliOk);
					if (Attachment.indexOf(documentiBustaHelper.getRequiredDocs(), Attachment::getId, docRichiestoId) == -1) {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						documentiBustaHelper.addDocRichiesto(
								docRichiestoId,
								docRichiesto,
								docRichiestoContentType,
								docRichiestoFileName,
								evento,
								checkFirma
						);
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
					}
				} else {
					target = INPUT;
				}
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "addDocRich");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(t);
			}
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(e);
			}
		} catch (Throwable e) {
			if (controlliOk) {
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
			} else {
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la verifica del formato dell'allegato richiesto " + this.docRichiestoFileName);
			}
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(e);
			}
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}

	/**
	 * aggiungi un documento ulteriore alla busta 
	 */
	public String addUltDoc() {
		String target = BACK_TO_DOCUMENTI;		
		try {
			BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
			WizardDocumentiBustaHelper documentiBustaHelper = busta.getHelperDocumenti();
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;				
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente
				String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
				if(StringUtils.isEmpty(codice)) {
					codice = this.codiceGara;
				}
				
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
				
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(busta.getDescrizioneBusta() + ": documento ulteriore"
								  + " con file=" + this.docUlterioreFileName
								  + ", dimensione=" + dimensioneDocumento + "KB");

				boolean controlliOk =
							checkFileDescription(docUlterioreDesc, evento)
							&& checkFileSize(docUlteriore, docUlterioreFileName, getActualTotalSize(documentiBustaHelper), appParamManager, evento)
							&& checkFileName(docUlterioreFileName, evento)
							&& checkFileExtension(docUlterioreFileName, appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento)
							&& checkFileFormat(docUlteriore, docUlterioreFileName, null, evento, false);
				
				logger.info("Controlli per i file prima del ws: {}", controlliOk);
				if (controlliOk) {
					Date checkDate = Date.from(Instant.now());
					DocumentiAllegatiFirmaBean checkFirma = checkFileSignature(
							docUlteriore
							, docUlterioreFileName
							, formato
							, checkDate
							, evento
							, Boolean.FALSE
							, appParamManager
							, customConfigManager
					);
					logger.info("ProcessPageDocumentiBustaAction -> checkFirma: {}", checkFirma);
					logger.info("Controlli per i file dopo ws: {}", controlliOk);
				
//				if (controlliOk) {
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
								evento,checkFirma);
						
						this.docUlterioreDesc = null;
						documentiBustaHelper.setDatiModificati(true);
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
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
	@Override
	public String cancel() {
		return CANCEL;
	}	
	
	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = BACK;
		
		WizardPartecipazioneHelper partecipazione = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if(partecipazione.isPlicoUnicoOfferteDistinte()) {
			if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA || 
			   this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) 
			{
				target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
			} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
				target = BACK_TO_BUSTE_TECNICHE_LOTTI;
			} else {
				target = BACK_TO_BUSTE_ECONOMICHE_LOTTI;
			}
		}
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		return null;
	}
	
	/**
	 * salva la busta ed invia tutti gli allegati 
	 * (usato fino alla v 1.14.5)  
	 */
	public String save() {
		String target = SUCCESS;
		
		if( this.aggiornaAllegato() ) {
			WizardPartecipazioneHelper partecipazione = GestioneBuste.getPartecipazioneFromSession().getHelper(); 
			
			if(partecipazione.isPlicoUnicoOfferteDistinte()) {
				if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA || 
				   this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) 
				{
					target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
				} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
					target = BACK_TO_BUSTE_TECNICHE_LOTTI;
				} else {
					target = BACK_TO_BUSTE_ECONOMICHE_LOTTI;
				}
			}
		} else {
			target = INPUT;
		}
		
		return target;
	}
	
	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione 
	 */
	protected boolean aggiornaAllegato() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = GestioneBuste.getDocumentiBustaHelperFromSession(this.tipoBusta);
			
			String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
			if(StringUtils.isEmpty(codice)) {
				codice = this.codiceGara;
			}
			
			target = saveDocumenti(
					documentiBustaHelper.getCodiceGara(),
					codice,
					this.operazione,
					this.tipoBusta,
					this.session,
					this);
		} catch (Throwable e) {
			target = INPUT;
		}
 
		return SUCCESS.equalsIgnoreCase(target);
	}
	
	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione 
	 */
	public static String saveDocumenti(
			String codiceGara,
			String codice,
			int operazione,
			int tipoBusta,
			Map<String, Object> session,
			BaseAction action) 
	{
		String target = SUCCESS;

		// recupera l'helper dei documenti dalla sessione
		GestioneBuste buste = GestioneBuste.getFromSession();
		BustaDocumenti busta = null;
		WizardDocumentiBustaHelper documentiBustaHelper = null;			
		try {			
			busta = buste.getBusta(tipoBusta);
			documentiBustaHelper = busta.getHelperDocumenti();
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, action, "save");
			ExceptionUtils.manageExceptionError(ex, action);
			target = ERROR;
		}
		
		if (documentiBustaHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			action.addActionError(action.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {			
			// verifica se action e dati in sessione sono sincronizzati...
			if(!documentiBustaHelper.isSynchronizedToAction(codice, action)) {
				return CommonSystemConstants.PORTAL_ERROR;
			}
						
			// per le gare a lotti, aggiorna il target in base alla busta
			if(buste.getBustaPartecipazione().getHelper().isPlicoUnicoOfferteDistinte()) {
				// gara a lotti
				documentiBustaHelper.setCodice(codice); // <= SERVE ???
				
				if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
					target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
				} else if(tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
					target = BACK_TO_BUSTE_ECONOMICHE_LOTTI;
				}
			}

			// ----- INVIO COMUNICAZIONI -----
			try {
				// invia la comunicazione della busta (e il riepilogo) in stato BOZZA
				logger.info("ProcessPageDocumentiBustaAction->saveDocumenti->send");
				busta.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				
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
		}
		
		// NB: 
		//  - fino alla 1.14.5 il "salva documenti" salva tutti i documenti 
		//    allegati in un'unica soluzione e ritorna al wizard principale... 
		//  - dalla 1.15.0 per ogni documento allegato si salva si resta nella 
		//    pagina corrente...
		if(ERROR.equalsIgnoreCase(target) ||
		   INPUT.equalsIgnoreCase(target) ||
		   CommonSystemConstants.PORTAL_ERROR.equalsIgnoreCase(target)) 
		{
			// restiruisci come target l'errore... 
		} else {
			// altrimenti restiruisci il target richiesto...
			target = SUCCESS;
		}
		
		return target;
	}
	
	/**
	 * rettifica/elimina i documenti di una singola busta
	 */
	public String eliminaDocumentiBusta() {
		String target = SUCCESS;
		
		this.nextResultAction = "openGestioneBuste";
		
		Event evento = null;		
		try {
			BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
			WizardDocumentiBustaHelper documentiHelper = busta.getHelperDocumenti();			

			if (documentiHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
				if(StringUtils.isEmpty(codice)) {
					codice = this.codiceGara;
				}
				
				boolean garaLotti = false;
				if(StringUtils.isNotEmpty(documentiHelper.getCodice()) && StringUtils.isNotEmpty(documentiHelper.getCodiceGara())) {
					garaLotti = !documentiHelper.getCodice().equals(documentiHelper.getCodiceGara());
				}

				// crea l'evento...
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(busta.getDescrizioneBusta() + ": rettifica busta");

				// elimina i documenti dall'helper ed aggiorna il riepilogo...
				for(int i = documentiHelper.getRequiredDocs().size() - 1; i >= 0; i--)
					documentiHelper.removeDocRichiesto(i);
				for(int i = documentiHelper.getAdditionalDocs().size() - 1; i >= 0; i--)
					documentiHelper.removeDocUlteriore(i);
				documentiHelper.setQuestionarioAssociato(null);

				// elimina la comunicazione della busta
				busta.delete();

				if( !garaLotti ) {
					this.nextResultAction = "openGestioneBuste";
				} else {					
					this.nextResultAction = "openGestioneBusteDistinte";
					this.setCodiceGara(busta.getCodiceGara());
				}

				target = SUCCESS;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "eliminaDocumentiBusta", "Errore durante l'eliminazione della busta " + this.tipoBusta);
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(t);
			}
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}
	
}
