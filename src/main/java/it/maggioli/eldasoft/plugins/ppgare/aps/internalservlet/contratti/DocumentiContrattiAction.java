package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.contratti;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.DelegateUser;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.sil.WSStipule.DocumentazioneStipulaContrattiType;
import it.eldasoft.www.sil.WSStipule.StipulaContrattoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageProtocollazioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IStipuleManager;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Azione per la gestione delle operazioni a livello di stipula contratto.
 * 
 * @author michele.dinapoli
 * @since 1.11.5
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.STIPULA })
public class DocumentiContrattiAction extends ProcessPageProtocollazioneAction 
	implements SessionAware, ServletResponseAware, IDownloadAction 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5477854445026886811L;

	private static final FileNameMap MIMETYPES = URLConnection.getFileNameMap();

	private static final String TIPO_DOCUMENTO_FIRMATO 					= ".P7M";
	private static final String TIPO_DOCUMENTO_MARCATO_TEMPORALMENTE 	= ".TSD";
	
	private static final String ENTITA_STIPULE 							= "G1STIPULA";
	private static final String NOME_OPERAZIONE 						= "Stipula Contratti";
	
	// memorizza in sessione lo stato del processo di invio dei documenti 
	// (nessuno -> invia FS12 -> elimina info protocollazione documenti -> aggiorna stato documenti)
	private static final String SESSION_ID_STATO_INVIO					= "statoInvioStipula";
	private int STATO_NESSUNO											= 0;
	private int STATO_INVIA_FS12 										= 1;
	private int STATO_ELIMINA_WSALLEGATI								= 2;
	private int STATO_AGGIORNA_STATO_DOCUMENTI 							= 3;
	
	private Map<String, Object> session;
	protected IContrattiManager contrattiManager;
	protected IDocumentiDigitaliManager documentiDigitaliManager;
	protected IStipuleManager stipuleManager;
	protected IURLManager urlManager;
	protected IPageManager pageManager;
	
	protected HttpServletResponse response;

	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate(EParamValidation.GENERIC)
	protected String docId;
	protected InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	protected String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	protected String contentType;
	protected Integer formato;
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;
	protected DocumentazioneStipulaContrattiType[] documenti;
	protected List<DocumentazioneStipulaContrattiType> documentiReadOnly;
	protected List<DocumentazioneStipulaContrattiType> documentiUpload;
	protected List<String> fasiDocReadonly;
	protected List<String> fasiDocUpload;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	@Validate(EParamValidation.CODICE)
	protected String codiceStipula;
	@Validate(EParamValidation.GENERIC)
	protected String noteDoc;
	protected File docToUpload;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docToUploadContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docToUploadFileName;
	protected String statoStipula;
	protected boolean deleteDoc = false;
	protected boolean invioStipula = false;
	protected boolean mailSent;
	private String msgErroreMail;
	@Validate(EParamValidation.GENERIC)
	protected String iddocdig;
	
	protected boolean esisteFileConFirmaNonVerificata = Boolean.FALSE;
	
	// info sul dettaglio della stipula
	private StipulaContrattoType dettaglioStipula;
	
	// elenco dei documenti da aggiungere alla protocollazione
	private List<DocumentazioneStipulaContrattiType> documentiDaAggiornare;

	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setContrattiManager(IContrattiManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}

	public void setDocumentiDigitaliManager(IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
	}

	public void setStipuleManager(IStipuleManager stipuleManager) {
		this.stipuleManager = stipuleManager;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}
	
	public String getIddocdig() {
		return iddocdig;
	}

	public void setIddocdig(String iddocdig) {
		this.iddocdig = iddocdig;
	}

	public String getMsgErroreMail() {
		return msgErroreMail;
	}

	public void setMsgErroreMail(String msgErroreMail) {
		this.msgErroreMail = msgErroreMail;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public DocumentazioneStipulaContrattiType[] getDocumenti() {
		return documenti;
	}

	public void setDocumenti(DocumentazioneStipulaContrattiType[] documenti) {
		this.documenti = documenti;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public Integer getFormato() {
		return formato;
	}

	public void setFormato(Integer formato) {
		this.formato = formato;
	}

	public List<DocumentazioneStipulaContrattiType> getDocumentiReadOnly() {
		return documentiReadOnly;
	}

	public void setDocumentiReadOnly(List<DocumentazioneStipulaContrattiType> documentiReadOnly) {
		this.documentiReadOnly = documentiReadOnly;
	}

	public List<DocumentazioneStipulaContrattiType> getDocumentiUpload() {
		return documentiUpload;
	}

	public void setDocumentiUpload(List<DocumentazioneStipulaContrattiType> documentiUpload) {
		this.documentiUpload = documentiUpload;
	}

	public List<String> getFasiDocReadonly() {
		return fasiDocReadonly;
	}

	public void setFasiDocReadonly(List<String> fasiDocReadonly) {
		this.fasiDocReadonly = fasiDocReadonly;
	}

	public List<String> getFasiDocUpload() {
		return fasiDocUpload;
	}

	public void setFasiDocUpload(List<String> fasiDocUpload) {
		this.fasiDocUpload = fasiDocUpload;
	}
	
	public String getNoteDoc() {
		return noteDoc;
	}

	public void setNoteDoc(String noteDoc) {
		this.noteDoc = StringUtilities.fixEncodingMultipartField(noteDoc);
	}

	public File getDocToUpload() {
		return docToUpload;
	}

	public void setDocToUpload(File docToUpload) {
		this.docToUpload = docToUpload;
	}
	
	public String getDocToUploadContentType() {
		return docToUploadContentType;
	}

	public void setDocToUploadContentType(String docToUploadContentType) {
		this.docToUploadContentType = docToUploadContentType;
	}

	public String getDocToUploadFileName() {
		return docToUploadFileName;
	}

	public void setDocToUploadFileName(String docToUploadFileName) {
		this.docToUploadFileName = docToUploadFileName;
	}

	public String getCodiceStipula() {
		return codiceStipula;
	}

	public void setCodiceStipula(String codiceStipula) {
		this.codiceStipula = codiceStipula;
	}
	
	public String getStatoStipula() {
		return statoStipula;
	}

	public void setStatoStipula(String statoStipula) {
		this.statoStipula = statoStipula;
	}
	
	public boolean isDeleteDoc() {
		return deleteDoc;
	}

	public void setDeleteDoc(boolean deleteDoc) {
		this.deleteDoc = deleteDoc;
	}

	public boolean isInvioStipula() {
		return invioStipula;
	}

	public void setInvioStipula(boolean invioStipula) {
		this.invioStipula = invioStipula;
	}

	public boolean isMailSent() {
		return mailSent;
	}

	public void setMailSent(boolean mailSent) {
		this.mailSent = mailSent;
	}
	
	public boolean isEsisteFileConFirmaNonVerificata() {
		return esisteFileConFirmaNonVerificata;
	}
	
	@Override
	public void setUrlPage(String urlPage) {
	}

	public String getCurrentFrame() {
		return currentFrame;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	/**
	 * ...
	 */
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);

		PageURL url = this.urlManager.createURL(reqCtx);

		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}

	/**
	 * ...
	 */
	public String confirmInvio() {
		String target = SUCCESS;
		
		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return "reopen";
		}

		try {
			this.documenti = this.stipuleManager.getDocumentiRichiestiStipulaContratto(this.codice);
			boolean canConfirm = true;
			if(this.documenti != null) {
				for(DocumentazioneStipulaContrattiType allegato : this.documenti) {
					if(allegato.getVisibilita() == 3 && allegato.getStato() == 3 && allegato.getIddocdig() == null && "1".equals(allegato.getObbligatorio())) {
						canConfirm = false;
					}
				}
			}
			if( !canConfirm ) {
				this.addActionError(this.getText("Errors.stipule.confirm", new String[] { NOME_OPERAZIONE }));
				prepareView();
			} else {
				this.invioStipula = true;
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "marcaturaTemporale");
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
		}
		return target;
	}
	
	/**
	 * annulla la richiesta di invio della stipula
	 */
	public String cancelInvio() {
		String target = SUCCESS;
		this.invioStipula = false;
		return target;
	}
	
	/**
	 * conferma la richiesta di eliminazione di un documento allegato
	 */
	public String confirmDelete() {
		String target = SUCCESS;
		this.deleteDoc = true;
		return target;
	}
	
	/**
	 * annulla la richiesta di eliminazione di un documento allegato 
	 */
	public String cancelDelete() {
		String target = SUCCESS;
		this.deleteDoc = false;
		return target;
	}
	
	/**
	 * ...
	 */
	public String open() {
		String target = SUCCESS;
		
		// verifica il profilo di accesso ed esegui un LOCK alla funzione
		if( !lockAccessoFunzione(EFlussiAccessiDistinti.STIPULA, this.codiceStipula) ) {
			return INPUT;
		}
		
		try {
			this.session.remove(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
			prepareView();
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
			ApsSystemUtils.logThrowable(e, null, "open");
		}
		return target;
	}

	/**
	 * carica il dettaglio dei documenti della stipula
	 */
	private void prepareView() throws ApsException {
		this.fasiDocReadonly = new ArrayList<String>();
		this.fasiDocUpload = new ArrayList<String>();
		this.documentiUpload = new ArrayList<DocumentazioneStipulaContrattiType>();
		this.documentiReadOnly = new ArrayList<DocumentazioneStipulaContrattiType>();
		
		// se lo stato della stipula e' "annullata" (6 in A1185) il dettaglio dei documenti dve essere in sola consultazione 
		this.dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(
				this.codiceStipula, 
				this.getCurrentUser().getUsername(), 
				true);
		boolean annullata = (this.dettaglioStipula != null && "6".equals(this.dettaglioStipula.getStato()));
		
		this.documenti = this.stipuleManager.getDocumentiRichiestiStipulaContratto(this.codice);
		if(this.documenti != null) {
			for(DocumentazioneStipulaContrattiType allegato : documenti) {
				// se la stipula e' "annullata" allora tutti i documenti rimangono in sola consultazione (readolny)
				if( !annullata && (allegato.getVisibilita() == 3 && allegato.getStato() == 3) ) {
					if(!this.fasiDocUpload.contains(allegato.getFase() + "")) {
						this.fasiDocUpload.add(allegato.getFase() + "");
					}
					ApsSystemUtils.getLogger().debug("allegato.getFirmacheck(): {}",(allegato.getFirmacheck()));
					this.documentiUpload.add(allegato);
					if(!esisteFileConFirmaNonVerificata && "2".equalsIgnoreCase(allegato.getFirmacheck())) {
						esisteFileConFirmaNonVerificata = Boolean.TRUE;
					}
				} else {
					if(!this.fasiDocReadonly.contains(allegato.getFase() + "")) {
						this.fasiDocReadonly.add(allegato.getFase() + "");
					}
					this.documentiReadOnly.add(allegato);
				}
			}
		}
		
		this.invioStipula = false;
		this.deleteDoc = false;
	}

	/**
	 * scarica l'allegato della stipula
	 */
	public String downloadAllegatoStipula() {
		String target = SUCCESS;
		try {
			AllegatoComunicazioneType allegato = this.stipuleManager.getDocumentoStipula(this.docId);
			this.id = this.iddocdig;
			if (isDocumentoFirmato(allegato.getNomeFile())) {
				return "successFileFirmato";
			}
			this.filename = allegato.getNomeFile();
			this.contentType = MIMETYPES.getContentTypeFor(this.filename);
			this.inputStream = new ByteArrayInputStream(allegato.getFile());
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
			ApsSystemUtils.logThrowable(e, null, "downloadAllegatostipula");
		}
		return target;
	}
	
	/**
	 * restituisce se un documento prevede la firma digitale
	 */
	private boolean isDocumentoFirmato(String filename) {
		if(filename != null) {
			return filename.toUpperCase().endsWith(TIPO_DOCUMENTO_FIRMATO) || 
			   	   filename.toUpperCase().endsWith(TIPO_DOCUMENTO_MARCATO_TEMPORALMENTE);
		} else {
			return false;
		}
	}
	
	/**
	 * elimina un documento allegato
	 */
	public String deleteAllegatoStipula() {
		String target = SUCCESS;
		Event evento = new Event();
		try {
			evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(this.codiceStipula);
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Stipula contratto: cancellazione documento richiesto, stipula=" + this.docId);
			this.stipuleManager.deleteDocumentoStipula(this.docId);
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
			ApsSystemUtils.logThrowable(e, this, "deleteAllegatoStipula");
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
	 * allega un nuovo documento allegato  
	 */
	public String addDocRich() {
		String target = SUCCESS;
		Event evento = null;
		try {
			int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docToUpload);
			evento = getUploadValidator().getEvento();
			
			// valida l'upload del documento...
			getUploadValidator()
					.setDocumento(docToUpload)
					.setDocumentoFileName(docToUploadFileName)
					.setDocumentoFormato(formato)
					.setOnlyP7m( customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M") )
					.setCheckFileSignature(true)
					.setEventoDestinazione(codiceStipula)
					.setEventoMessaggio("Stipula contratto: documento richiesto" 
							  			+ " con id=" + this.docId
							  			+ ", file=" + this.docToUploadFileName
							  			+ ", dimensione=" + dimensioneDocumento + "KB");
			
			if ( getUploadValidator().validate() ) {
				stipuleManager.insertAllegato(
						docToUploadFileName,
						Long.valueOf(docId),
						FileUtils.readFileToByteArray(docToUpload),
						noteDoc,
						getUploadValidator().getCheckFirma(),
						DocumentiAllegatiHelper.validateContentType(null, getUploadValidator().getCheckFirma(), docToUploadFileName)
				);
			}
		} catch (NumberFormatException | ApsException | IOException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { "Stipula Contratti" }));
			ApsSystemUtils.logThrowable(e, this, "upload");
			if (evento != null)
				evento.setError(e);
		} catch (Exception e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
			ApsSystemUtils.logThrowable(e, this, "upload");
			if (evento != null)
				evento.setError(e);
		} finally {
			if (evento != null)
				eventManager.insertEvent(evento);
		}
		return target;
	}

	/**
	 * conferma una stipula ed invia la comunicazione
	 */	
	public String confermaStipula() {
		String target = SUCCESS;
		
		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return this.getTarget();
		}

		Integer statoInvio = new Integer(STATO_NESSUNO);
		this.mailSent = true;
		try {
			// recupera lo stato dell'eventuale precedente invio andato in errore... 
			statoInvio = (Integer)this.session.get(SESSION_ID_STATO_INVIO);
			if(statoInvio == null || (statoInvio != null && statoInvio < 0)) {
				statoInvio = new Integer(STATO_NESSUNO);
			}
			
			// carica i dati aggiornati dell'impresa...
			this.impresa = ImpresaAction.getLatestDatiImpresa(
					this.getCurrentUser().getUsername(), 
					this);

			// prima di aggiornare la stipula/contratto e i documenti associati
			// recupera l'elenco dei documenti che sono in stato=3 (e visibilita'=3)
			// che dopo l'aggiornamento passeranno in stato=4...
			this.documentiDaAggiornare = null;
			DocumentazioneStipulaContrattiType[] documenti = this.stipuleManager.getDocumentiRichiestiStipulaContratto(codice);
			if(documenti != null) {
				this.documentiDaAggiornare = new ArrayList<DocumentazioneStipulaContrattiType>();
				for(DocumentazioneStipulaContrattiType doc : documenti) {
					if(doc.getVisibilita() == 3 && doc.getStato() == 3) {
						this.documentiDaAggiornare.add(doc);
					}
				}
			}

			// elimina da WSALLAGATI i documenti rettificati...
			if(statoInvio <= STATO_ELIMINA_WSALLEGATI && 
			   this.documentiDaAggiornare != null) 
			{
				for(DocumentazioneStipulaContrattiType doc : this.documentiDaAggiornare) {
					long id = (doc.getIddocdig() != null ? doc.getIddocdig().longValue() : -1);
					if(id > 0) {
						this.comunicazioniManager.deleteAllegatoProtocollo(
								"W_DOCDIG", 
								CommonSystemConstants.ID_APPLICATIVO_GARE, 
								id);
					}
				}
			}
			this.session.put(SESSION_ID_STATO_INVIO, STATO_ELIMINA_WSALLEGATI);


			// invia la comunicazione FS12 e l'eventuale protocollazione...
			if(statoInvio <= STATO_INVIA_FS12) {
				// se la protocollazione va a buon fine lo stato della comunicazione FS12 va portato a INVIATA (3)
				this.statoComunicazioneDaProcessare	= CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA;

				target = send( 
						PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE,
						null,						// se esiste gia' la comunicazione in stato BOZZA questo e' l'id relativo alla comunicazione 
						this.impresa, 				// dati dell'impresa
						null,						// allegati per la FS12 da protocollare
						null,						// documenti richiesti 
						this.getCodiceStipula(), 
						null,						// dataScadenzaRichiesta
						null,						// stazioneAppaltanteProcedura
						ENTITA_STIPULE);			// entita' da associare alla comunicazione e WSDOCUMENTO
				
//				msgErroreMail = this.getMsgErrore();
//				mailSent = (StringUtils.isEmpty(this.getMsgErrore()));
			}
			this.session.put(SESSION_ID_STATO_INVIO, STATO_INVIA_FS12);

			
			if(SUCCESS.equalsIgnoreCase(target)) {
				// aggiorna la stipula/contratto e i documenti associati (stato=3->4)...
				if(statoInvio <= STATO_AGGIORNA_STATO_DOCUMENTI) {
					this.stipuleManager.updateStipula(this.codice, this.getCurrentUser().getUsername());
				}
				this.session.put(SESSION_ID_STATO_INVIO, STATO_AGGIORNA_STATO_DOCUMENTI);
			}
			
			this.session.remove(SESSION_ID_STATO_INVIO);
			
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
			ApsSystemUtils.logThrowable(e, this, "confermaStipula");
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Exception e) {
			if(this.msgErroreMail == null) { 
				this.addActionError(this.getText("Errors.stipule.configuration", new String[] { NOME_OPERAZIONE }));
				ApsSystemUtils.logThrowable(e, this, "confermaStipula");
			}
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		unlockAccessoFunzione();
		
		return target;
	}	
	
//	/**
//	 * prepara l'allegato "comunicazione.pdf"
//	 */
//	@Override
//	protected byte[] addComunicazionePdf(AllegatoComunicazioneType[] allegati) throws Exception {
//		return super.addComunicazionePdf(allegati);
//	}
	
	/**
	 * prepara il documento per la protocollazione specifico per le stipule
	 */	
	@Override
	protected WSDocumentoType initWSDocumentoProtocollo(String entita, String key1, String oggetto, WSDMProtocolloDocumentoType ris) {
		// inizializza il documento per l'invio della protocollazione (aggiornamento di WSDOCUMENTO)
		Long idStipula = (this.documentiDaAggiornare != null ? this.documentiDaAggiornare.get(0).getIdStipula() : new Long(-1));		
		WSDocumentoType documento = super.initWSDocumentoProtocollo(
				ENTITA_STIPULE,
				idStipula.toString(), 
				oggetto, 
				ris);
		return documento;
	}
	
	/**
	 * crea l'elenco degli allegati da inviare al WSDM 
	 * @throws ApsException 
	 */
	@Override
	protected WSDMProtocolloAllegatoType[] createAttachmentsWSDM(
			ComunicazioneType comunicazione, 
			boolean inTesta)
		throws ApsException
	{
		// cerca nella comunicazione l'allegato relativo a "comunicazione.pdf"
		AllegatoComunicazioneType comunicazionePdf = null;
		if(comunicazione != null) {
			for(AllegatoComunicazioneType a : comunicazione.getAllegato()) {
				if(a.getNomeFile().equals(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_PDF) || 
				   a.getNomeFile().equals(PortGareSystemConstants.NOME_FILE_RIEPILOGO_COMUNICAZIONE_TSD))
				{
					comunicazionePdf = a;
					break;
				}
			}
		}
		
		// calcola la dimensione per "allegatiAggiuntiviDaProtocollare"
		AllegatoComunicazioneType[] allegatiWSDM = null;
		int n = (this.documentiDaAggiornare != null ? this.documentiDaAggiornare.size() : 0)
			    + (comunicazionePdf != null ? 1 : 0);
		
		if(n > 0) {
			allegatiWSDM = new AllegatoComunicazioneType[n];
			Long idStipula = (this.documentiDaAggiornare != null && !this.documentiDaAggiornare.isEmpty()
							  ? this.documentiDaAggiornare.get(0).getIdStipula() 
							  : null);
			
			// converti i documenti della stipula in allegati per la protocollazione
			// questi allegati vengono poi aggiunti all'elenco WSDMProtocolloAllegatoType[]
			// utilizzato per compilare la lista dei documenti da inviare al protocollo
			// i campi necessari da compilare per la protocollazione sono
			// 		- WSDMProtocolloAllegatoType.titolo
			//	 	- WSDMProtocolloAllegatoType.contenuto
			int i = 0;
			if(this.documentiDaAggiornare != null) {
				for(DocumentazioneStipulaContrattiType d : this.documentiDaAggiornare) {
					// scarica lo stream binario del documento della stipula...
					AllegatoComunicazioneType a = this.stipuleManager.getDocumentoStipula(Long.toString(d.getId()));
					if(a != null) {
						allegatiWSDM[i] = new AllegatoComunicazioneType();
						// La descrizione nelle stipule non è sempre obbligatoria
						// quindi è stato scelto di passare il titolo dell'allegato
						allegatiWSDM[i].setDescrizione(d.getTitolo());		// titolo
						allegatiWSDM[i].setNomeFile(d.getNomeFile());		// nome
						allegatiWSDM[i].setFile(a.getFile());				// contenuto
						allegatiWSDM[i].setId(a.getId());					// id dell'documento caricato, utilizzato per inserire in WSALLEGATI.KEY2 	
					} else {
						logger.error("createAttachmentsWSDM() allegato non trovato per la stipula " + d.getNomeFile() + ", idStipula=" + idStipula);
					}
					i++;
				}
			}
			
			// aggiungi l'allegato "comunicazione.pdf" agli allegati da protocollare (WSALLEGATI)
			// l'id in W_DOCDIG di riferimento per il "comunicazione.pdf" corrisponde a quello 
			// registrato con la FS12
			// da poter utilizzare 
			if(comunicazionePdf != null) {
				allegatiWSDM[i] = new AllegatoComunicazioneType();
				allegatiWSDM[i].setDescrizione(comunicazionePdf.getDescrizione());	// titolo
				allegatiWSDM[i].setNomeFile(comunicazionePdf.getNomeFile());		// nome
				allegatiWSDM[i].setFile(comunicazionePdf.getFile());				// contenuto
				allegatiWSDM[i].setId(comunicazionePdf.getId());					// id dell'documento caricato, utilizzato per inserire in WSALLEGATI.KEY2
			}
		}
		
		this.allegatiAggiuntiviProtocollazioneComunicazione = allegatiWSDM;
		
		// crea l'elenco dei documenti da inviare al protocollo/documentale 
		return super.createAttachmentsWSDM(comunicazione, inTesta);
	}

	/**
	 * costruisci il testo della comunicazione FS12
	 */
	private String getTesto() throws ApsException, XmlException {
		StringBuilder sb = new StringBuilder();
	
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		
		StipulaContrattoType dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(
				this.codiceStipula, 
				this.getCurrentUser().getUsername(), 
				true);
		
		ApsSystemUtils.getLogger().debug("dettaglioStipula: {}",dettaglioStipula);
		String testo = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("LABEL_WSDM_TESTO_STIPULA_CONTRATTO"),
				new Object[] {ragioneSociale, this.getCodiceStipula(),dettaglioStipula.getOggetto()});
		
		sb.append(testo);
		sb.append("\n\n");
		boolean hasAllegati = false;
		this.documenti = this.stipuleManager.getDocumentiRichiestiStipulaContratto(this.codice);
		if(this.documenti != null) {
			for(DocumentazioneStipulaContrattiType allegato : this.documenti) {
				if(3 == allegato.getVisibilita()) {
					sb.append(allegato.getTitolo());
					sb.append(" - ");
					if(StringUtils.isNotEmpty(allegato.getNomeFile())) {
						sb.append(allegato.getNomeFile());
						sb.append("\n\n");
						hasAllegati = true;
					} else {
						sb.append("non allegato");
						sb.append("\n\n");
					}
				}
			}
		} if(!hasAllegati) {
			sb.append("Nessun allegato inserito");
			sb.append("\n\n");
		}
		return sb.toString();
	}
		
	/**
	 * generazione del contenuto del documento di riepilogo "COMUNICAZIONE.PDF/TSD"
	 */
	@Override
	protected String getComunicazionePdf() {
		StringBuilder contenuto = new StringBuilder();
		try {
			StipulaContrattoType dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(
					this.getCodiceStipula(), 
					this.getCurrentUser().getUsername(), 
					false);
						
			DettaglioStazioneAppaltanteType stazione = this.bandiManager.getDettaglioStazioneAppaltante(
					dettaglioStipula.getCodiceSa());

			this.impresa = ImpresaAction.getLatestDatiImpresa(
					this.getCurrentUser().getUsername(), 
					this);
			
			String descGenere = "stipula";
			String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			String nomeCliente = (String)this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE);
			String titolo = (dettaglioStipula != null ? dettaglioStipula.getOggetto() : "");
			String testo = getTesto();
			String oggetto = MessageFormat.format(
					this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_STIPULA_CONTRATTO"), 
					this.getCodiceStipula());
			
			// prepare il testo di "comunicazione.pdf"
			contenuto.append(StringUtils.isNotEmpty(nomeCliente) ? nomeCliente + "\n\n" : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getDenominazione()) ? stazione.getDenominazione() + "\n" : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getIndirizzo()) ? stazione.getIndirizzo() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getNumCivico()) ? stazione.getNumCivico() + ", " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getCap()) ? stazione.getCap() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getComune()) ? stazione.getComune() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(stazione.getProvincia()) ? "(" + stazione.getProvincia() + ")" : "");
			contenuto.append("\n\n");
			contenuto.append("Oggetto ").append(descGenere).append(": ").append(StringUtils.isNotEmpty(titolo) ? titolo : "");
			contenuto.append(" - Codice ").append(descGenere).append(": ").append((StringUtils.isNotEmpty(this.codiceStipula) ? this.codiceStipula : ""));
			contenuto.append("\n\n");
			contenuto.append("Oggetto comunicazione: ").append(oggetto).append("\n");
			contenuto.append("\n");
			contenuto.append(testo).append("\n");
			contenuto.append("\n");
			contenuto.append("Operatore economico: ").append(ragioneSociale).append("\n");
			contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " " : "");		
			contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale() + ", " : "");
			contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale()) ? this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale() + " " : "");
			contenuto.append(StringUtils.isNotEmpty(this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale()) ? "(" + this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")" : "");

		} catch (XmlException e) {
			ApsSystemUtils.logThrowable(e, this, "getComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "getComunicazionePdf");
			ExceptionUtils.manageExceptionError(e, this);
		}
		return contenuto.toString();
	}
	
	@Override
	protected String getDescrizioneFunzione() {
		return "stipula";
	}

	@Override
	protected String getOggettoNuovaComunicazione() {
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_STIPULA_CONTRATTO"), 
				this.getCodiceStipula());
		return oggetto;
	}

	@Override
	protected String getTestoNuovaComunicazione() {
		String testo = null;
		try {
			testo = this.getTesto();
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "getTestoNuovaComunicazione");
			ExceptionUtils.manageExceptionError(e, this);
		}
		return testo;
	}

	@Override
	protected String getOggettoMailUfficioProtocollo() {
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_STIPULA_CONTRATTO"), 
				this.getCodiceStipula());
		return oggetto;

	}

	@Override
	protected String getTestoMailUfficioProtocollo() {
		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("LABEL_WSDM_TESTO_STIPULA_CONTRATTO"), 
				this.getCodiceStipula());
		
		String testo = null;
		try {
			testo = this.getTesto();
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "getTestoMailUfficioProtocollo");
		}
		
//		SERVE ???
//		if (this.isPresentiDatiProtocollazione()) {
//			testo = MessageFormat.format(
//					this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RINUNCIA_RICEVUTA_TESTOCONPROTOCOLLO"),
//					new Object[] {ragioneSociale, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo(), 
//								  this.codice, oggetto});
//		}
		
		return testo;
	}

	@Override
	protected void addAllegatiMailUfficioProtocollo(Map<String, byte[]> allegatiMail) {
		// ??? CI SONO DOCUMENTI DA ALLEGARE ALLA MAIL ???
	}

	@Override
	protected String getOggettoMailConfermaImpresa() {
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("LABEL_WSDM_OGGETTO_STIPULA_CONTRATTO"), 
				this.getCodiceStipula());
 		return oggetto;
	}

	@Override
	protected String getTestoMailConfermaImpresa() {
		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("LABEL_WSDM_TESTO_STIPULA_CONTRATTO"), 
				this.getCodiceStipula());
		
		String testo = null;
		try {
			testo = this.getTesto();
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "getTestoMailConfermaImpresa");
		}

//		SERVE ???
//		if (this.isPresentiDatiProtocollazione()) {
//			testo = MessageFormat.format(
//					this.getI18nLabel("MAIL_GARETEL_RINUNCIA_RICEVUTA_TESTOCONPROTOCOLLO"),
//					new Object[] {ragioneSociale, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo(),
//								  this.codice, oggetto});
//		}

		return testo;
	}

	@Override
	protected String getWSDMClassificaFascicolo() {
		String classificaFascicolo = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_CLASSIFICA);
		return classificaFascicolo;
	}

	@Override
	protected String getWSDMTipoDocumento() {
		String tipoDocumento = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_TIPO_DOCUMENTO);
		return tipoDocumento;
	}

	@Override
	protected String getWSDMCodiceRegistro() {
		String codiceRegistro = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_CODICE_REGISTRO);
		return codiceRegistro;
	}

	@Override
	protected String getWSDMIdIndice() {
		String idIndice = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_INDICE);
		return idIndice;
	}

	@Override
	protected String getWSDMIdTitolazione() {
		String idTitolazione = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STIPULE_TITOLAZIONE);
		return idTitolazione;
	}
	
	@Override
	protected void setInfoPerProtocollazione(String codice) throws ApsException {
		// recupera le info standard...
		super.setInfoPerProtocollazione(codice);
		
		if(this.codiceSA == null) {
			// recupera il dettaglio della stipula/contratto
			StipulaContrattoType dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(
					this.codiceStipula, 
					this.getCurrentUser().getUsername(), 
					true);
			if(dettaglioStipula != null) {
				//this.stazioneAppaltante = ???
				this.codiceSA = dettaglioStipula.getCodiceSa();
				//this.codiceCig = null;
				this.titoloPdfRiepilogoComunicazione = (dettaglioStipula != null ? dettaglioStipula.getOggetto() : "");
				this.descrizioneGenerePdfRiepilogoComunicazione = "stipula";
			}
		}
	}

	/**
	 * recupera il fascicolo associato alla comunicazione della stipula
	 */
	@Override
	protected FascicoloProtocolloType getFascicoloProtocollo() throws ApsException {
		// NB: il fascicolo della stipula va recuperato in base all'id della stipula e non al codice del bando
		// recupera il dettaglio della stipula/contratto 
		StipulaContrattoType dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(
				this.codiceStipula, 
				this.getCurrentUser().getUsername(), 
				true);
		
		// recupera il fascicolo associata all'id della stipula
		FascicoloProtocolloType fascicolo = this.bandiManager.getFascicoloProtocollo(Long.toString(dettaglioStipula.getId()));
		
		return fascicolo;
	}


}
