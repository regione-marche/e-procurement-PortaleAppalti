package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.UploadValidator;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.IProcessPageQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QuestionariUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario.AnomalieQuestionarioInfo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageQuestionarioBustaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.utils.PdfUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Action di gestione delle operazioni nella pagina dei documenti
 *
 * @author
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class ProcessPageQuestionarioAction extends ProcessPageDocumentiAction 
	implements IProcessPageQuestionario 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 120231716581736610L;
	private static final Logger logger = ApsSystemUtils.getLogger();
	
    private IComunicazioniManager comunicazioniManager;

    private static final String NEXT = "next";
    private static final String BACK = "back";

    @Validate(EParamValidation.GENERIC)
    private String multipartSaveDir;
    @Validate(EParamValidation.DIGIT)
    private String progressivoOfferta;        // attributo ad uso privato
    @Validate(EParamValidation.ACTION)
    private String nextResultAction;        // openGestioneBuste | openGestioneBusteDistinte

    // parametri in ingresso per le action associate a addDocumento(), deleteAllegato(), downloadAllegato()
    private Long idCom;                        // id comunicazione
    @Validate(EParamValidation.GENERIC)
    private String description;                // descrizione dell'allegato
    @Validate(EParamValidation.FILE_NAME)
    private String attachmentFileName;        // nome file dell'allegato
    private File attachmentData;            // contenuto binario dell'allegato
    @Validate(EParamValidation.GENERIC)
    private String attachmentOrder;            // chiave di ordinamneto dell'allegato nel questionario (SSSZ00GR00CA00)
    @Validate(EParamValidation.UUID)
    private String uuid;                    // UUID dell'allegato
    @Validate(EParamValidation.UUID)
    private String[] uuids;                    // lista di UUID degli allegati
    private boolean signed;                    // true/false indica se un file in upload e' firmato digitalmente
    private boolean summary;                // true/false indica se un file in upload e' il riepilogo questionario
    @Validate(EParamValidation.GENERIC)
    private String form;                    // json del questionario

    // parametri per la risposta json
    @Validate(EParamValidation.FILE_NAME)
    private String filename;
    @Validate(EParamValidation.CONTENT_TYPE)
    private String contentType;
    @Validate(EParamValidation.CODICE)
    private String codice;
    private boolean isIscrizione;
    private boolean isAggiornamentoIscrizione;
    private boolean isAggiornamentoSoloDocumenti;
    private boolean isRinnovoIscrizione;

    public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
        this.comunicazioniManager = comunicazioniManager;
    }

    @Override
    public Long getIdCom() {
        return idCom;
    }

    @Override
    public void setIdCom(Long idCom) {
        this.idCom = idCom;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = StringUtilities.fixEncodingMultipartField(description);
    }

    @Override
    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    @Override
    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    @Override
    public File getAttachmentData() {
        return attachmentData;
    }

    @Override
    public void setAttachmentData(File attachmentData) {
        this.attachmentData = attachmentData;
    }

    @Override
    public String getAttachmentOrder() {
        return this.attachmentOrder;
    }

    @Override
    public void setAttachmentOrder(String value) {
        this.attachmentOrder = value;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String[] getUuids() {
        return uuids;
    }

    @Override
    public void setUuids(String[] uuids) {
        this.uuids = uuids;
    }

    @Override
    public boolean getSigned() {
        return this.signed;
    }

    @Override
    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    @Override
    public boolean getSummary() {
        return this.summary;
    }

    @Override
    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    @Override
    public String getForm() {
        return form;
    }

    @Override
    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
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
     * @param multipartSaveDir the multipartSaveDir to set
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String multipartSaveDir) {
        this.multipartSaveDir = multipartSaveDir;
    }

    /**
     * ...
     */
    @Override
    public String cancel() {
        return "cancel";
    }

    /**
     * ...
     */
    @Override
    public String back() {
        String target = BACK;
        this.nextResultAction = "openPageIscrAlboRiepilogoCategorie";
        WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
                .get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
        this.nextResultAction = helper.getPreviousAction(WizardIscrizioneHelper.STEP_QUESTIONARIO);
        return target;
    }

    /**
     * ...
     */
    @Override
    public String next() {
        String target = NEXT;
        //this.nextResultAction = "???";
        logger.debug("ProcessPageQuestionarioAction - next(): {} ", target);
        return target;
    }

    /**
     * (QCompiler) inizializza il questionario
     */
    @Override
    public String initQuestionario() {
        // SPOSTATA IN InitIscrizioneAction !!!
        return SUCCESS;
    }


    /**
     * prepara i parametri per il riutilizzo del metodo "aggiornaAllegato()"
     *
     * @throws ApsException
     */
    private void getParametriFromComunicazione() throws ApsException {
        this.progressivoOfferta = "";
        this.codice = null;
        this.isIscrizione = false;
        this.isAggiornamentoIscrizione = false;
        this.isAggiornamentoSoloDocumenti = false;
        this.isRinnovoIscrizione = false;

        ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
                CommonSystemConstants.ID_APPLICATIVO,
                this.idCom);

        if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO)) {
            this.isIscrizione = true;
        } else if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO)) {
            this.isAggiornamentoIscrizione = true;
        }

        //this.progressivoOfferta = comunicazione.getDettaglioComunicazione().getChiave3();
        this.codice = comunicazione.getDettaglioComunicazione().getChiave2();
    }
    
    /**
     * invia l'aggiornamento della comunicazione, con helper documenti e questionario.json verificati e sincronizzati
     * 
     * @throws ApsException 
     * @throws GeneralSecurityException 
     * @throws IOException 
     */
    private boolean aggiornaComunicazione(
    		WizardIscrizioneHelper helper,		
            String questionario,				// json del QFORM inviato dal client
            boolean isEncoded64,				// true se il json e' codificato in base64
            Event evento
    ) throws ApsException, IOException, GeneralSecurityException
    {
    	boolean controlliOk = false;
    	
    	WizardDocumentiHelper documenti = (helper != null ? helper.getDocumenti() : null);
    	if(documenti != null) {
    		ApsSystemUtils.getLogger().debug("aggiornaComunicazione() BEGIN {} {}", helper.getIdBando(), evento.getEventType());
    		documenti.getRequiredDocs().stream().forEach(a -> ApsSystemUtils.getLogger().debug("  doc ric: {} {}", a.getUuid(), a.getFileName()));
    		documenti.getAdditionalDocs().stream().forEach(a -> ApsSystemUtils.getLogger().debug("  doc ult: {} {}", a.getUuid(), a.getFileName()));

    		if (StringUtils.isNotEmpty(form)) {
    			controlliOk = true;
	    		// 1) aggiorna il questionario...
	    		//
	    		if (StringUtils.isNotEmpty(this.form)) {
	    	        // aggiorna il questionario della busta...
	    	        if (isEncoded64) {
	    	            questionario = QCQuestionario.decodeFormParameter(questionario);
	    	        }
	    	        documenti.updateQuestionario(questionario);
	    		}
    		}
    		
    		// 2) verifica se questionario.json e helper documenti sono allineati
    		//
    		if(ProcessPageQuestionarioBustaAction.ABILITA_VERIFICA_JSON_XML) {
	    		if(controlliOk) {
	    	    	List<AnomalieQuestionarioInfo> anomalie = null;
	    	    	if(documenti != null) 
	    	    		anomalie = documenti.validaQuestionarioElenchi();
	    			
	    		    if(anomalie != null && anomalie.size() > 0) {
	    		    	controlliOk = false;
	    		    	
	    				addActionError(getText("invalid.qform.status"));	// messaggio da mostrare all'utente (DA RIVEDERE!!!)
	    				
	    				String message = "QForm e busta non allineati (questionario.json diverso da XML)\n";
	    				for(AnomalieQuestionarioInfo d : anomalie) 
	    			    	message += "L''allegato " + d.getFilename() + " risulta corrotto (UUID=" + d.getUuid() + ").\n";
	    			    	
	    				if(evento != null) {
	    				    evento.setLevel(Level.ERROR);
	    				    evento.setDetailMessage(message);
	    				}
	    			    
	    			    ApsSystemUtils.getLogger().debug("ERR: {}", message);
	    		    }
	    		}
    		}
    		
    		// 3) invia la comunicazione al servizio
    		//
    		if (controlliOk) {
    			controlliOk = this.aggiornaAllegato();
    		}
    		
    		ApsSystemUtils.getLogger().debug("aggiornaComunicazione() END {} {}", helper.getIdBando(), evento.getEventType());
		}
    	return controlliOk;
    }
    
    /**
     * (QCompiler) richiede un nuovo UUID da utilizsare per l'operazione di addDocumento
     */
    @Override
    public String getNewUuid() {
        String target = SUCCESS;

        this.contentType = null;
        String uuid = DocumentiAllegatiHelper.getUuid();
        if (StringUtils.isNotEmpty(this.attachmentOrder)) {
            // uuid generato (nel formato SSSZ00GR00QU00 + YYMMDDhhmmssNNNNN)
            uuid = this.attachmentOrder + uuid;
        }
        String message = "generated uuid=" + uuid;

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseNewUuid(
                this,
                target,
                message,
                uuid
        );

        return target;
    }

    /**
     * (QCompiler) aggiungi un allegato all'elenco
     * IN
     * idCom				id della comunicazione
     * description			descrizione dell'allegato
     * attachmentFileName	nome file dell'allegato
     * attachmentData		contenuto binario dell'allegato
     * uuid				uuid del nuovo allegato
     * signed				True se il file e' firmato digitalmente
     * summary				True se il file e' il pdf firmato del riepilogo questionario
     * form				json del questionario (in formato base64)
     */
    @Override
    public String addDocumento() {
        String target = SUCCESS;

        this.contentType = null;
        String message = null;
        String warning = null;
        boolean pdfRiepilogoAutoGenerato = false;
        Event evento = null;
        try {
            // recupera i parametri dalla comunicazione
            this.getParametriFromComunicazione();

            WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
                    .get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
            WizardDocumentiHelper documenti = (iscrizioneHelper != null ? iscrizioneHelper.getDocumenti() : null);

            if (documenti == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
                        CommonSystemConstants.ID_APPLICATIVO,
                        this.idCom
                );
                if (comunicazione.getDettaglioComunicazione().getStato().equals(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA)) {

                    String codice = iscrizioneHelper.getIdBando();

                    // recupera lo stream dei ...
                    byte[] data = FileUtils.readFileToByteArray(this.attachmentData);
                    int dimensioneDocumento = (int) Math.ceil(data.length / 1024.0);

                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(codice);
                    evento.setLevel(Event.Level.INFO);
                    evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(
                            String.format(
                                    "%s: documento ulteriore con file=%s, dimensione=%sKB, uuid=%s"
                                    , iscrizioneHelper.getDescrizioneFunzione()
                                    , attachmentFileName
                                    , dimensioneDocumento
                                    , uuid
                            )
                    );

                    // verifica se l'allegato e' valido (con descrizione, filename corretto, estesione corretta e dimensione nei limiti)
                    boolean controlliOk =
                            checkFileDescription(this.description, evento)
                            && checkFileName(this.attachmentFileName, evento)
                            && checkFileExtension(this.attachmentFileName, evento)
                            && checkFileSize(dimensioneDocumento, this.attachmentFileName, documenti.getTotalSize(), evento);
                    		//&& this.checkFileFormat(this.attachment, this.attachmentFileName, null, evento, false);
                    DocumentiAllegatiFirmaBean checkFirma = null;
                    if (this.signed) {
                        // verifica se il file e' firmato digitalmente...
                        controlliOk = controlliOk && this.checkFileFormat(
                                this.attachmentData,
                                this.attachmentFileName,
                                PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO,
                                false,
                                evento);

                        if (controlliOk) {
                            Date checkDate = Date.from(Instant.now());
                            checkFirma = this.checkFileSignature(
                            		this.attachmentData,
                                    this.attachmentFileName,
                                    PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO,
                                    checkDate,
                                    Boolean.FALSE,
                                    evento);

                            // se presenti recupera i messaggi della action per comporre il warning sui documenti firmati...
                            if (!this.verificaDocumentiNonFirmati(documenti) || (checkFirma == null || !checkFirma.getFirmacheck())) {
                                if (this.getActionMessages().size() > 0) {
                                    StringBuilder msg = new StringBuilder();
                                    Iterator<String> errors = this.getActionMessages().iterator();
                                    while (errors.hasNext()) {
                                        msg.append(errors.next()).append("\n");
                                    }
                                    warning = msg.toString();
                                }
                                logger.debug("this.getActionErrors().size() : {}", this.getActionErrors().size());
                                if (this.getActionErrors().size() > 0) {
                                    StringBuilder msg = new StringBuilder();
                                    Iterator<String> errors = this.getActionErrors().iterator();
                                    while (errors.hasNext()) {
                                        msg.append(errors.next()).append("\n");
                                    }
                                    warning = msg.toString();
                                }
                            }
                        }
                    } else {
                        // verifica se il file e' firmato digitalmente...
                        controlliOk = controlliOk && this.checkFileFormat(
                                this.attachmentData,
                                this.attachmentFileName,
                                null,
                                false,
                                evento);

                        if (controlliOk) {
                            Date checkDate = Date.from(Instant.now());
                            checkFirma = this.checkFileSignature(
                            		this.attachmentData,
                                    this.attachmentFileName,
                                    null,
                                    checkDate,
                                    Boolean.FALSE,
                                    evento);

                            // se presenti recupera i messaggi della action per comporre il warning sui documenti firmati...
                            if (!this.verificaDocumentiNonFirmati(documenti) || (checkFirma == null || !checkFirma.getFirmacheck())) {
                                if (this.getActionMessages().size() > 0) {
                                    StringBuilder msg = new StringBuilder();
                                    Iterator<String> errors = this.getActionMessages().iterator();
                                    while (errors.hasNext()) {
                                        msg.append(errors.next()).append("\n");
                                    }
                                    warning = msg.toString();
                                }
                                logger.debug("this.getActionErrors().size() : {}", this.getActionErrors().size());
                                if (this.getActionErrors().size() > 0) {
                                    StringBuilder msg = new StringBuilder();
                                    Iterator<String> errors = this.getActionErrors().iterator();
                                    while (errors.hasNext()) {
                                        msg.append(errors.next()).append("\n");
                                    }
                                    warning = msg.toString();
                                }
                            }
                        }
                    }

                    if (this.summary) {
                        // il file caricato e' il riepilogo del questionario...
                        // verifica se l'hash del file corrisponde a quello generato (presente nell'xml)
//					String pdfUuid = documenti.getPdfUuid();
//					controlliOk = controlliOk && this.checkFileRiepilogo(
//							this.attachmentData, 
//							this.attachmentFileName, 
//							pdfUuid, 
//							evento);

                        // nel caso di upload del PDF di riepilogo e' necessario
                        // discriminare il tipo di evento nel caso di
                        // PDF generato manualmente od autogenerato...
                        pdfRiepilogoAutoGenerato = false;
                        if (StringUtils.isNotEmpty(this.form)) {
                            String q = QCQuestionario.decodeFormParameter(this.form);
                            // se nel json del questionario e' presente il tag "printpdf"
                            // il riepilogo e' generato manualmente
                            pdfRiepilogoAutoGenerato = (q.toLowerCase().indexOf("printpdf") <= 0);
                        }
                        if (pdfRiepilogoAutoGenerato) {
                            evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                            evento.setMessage(
                                    String.format(
                                            "%s: PDF di riepilogo QFORM generato automaticamente file=%s, dimensione=%s, uuid=%s"
                                            , iscrizioneHelper.getDescrizioneFunzione()
                                            , attachmentFileName
                                            , dimensioneDocumento
                                            , uuid
                                    )
                            );
                        }
                    }

                    if (!controlliOk) {
                        target = ERROR;
                        message = "error bad file description or file name or file format or hash";
                        // se presenti recupera i messaggi di errore dalla action...
                        if (this.getActionErrors().size() > 0) {
                            StringBuilder msg = new StringBuilder();
                            Iterator<String> errors = this.getActionErrors().iterator();
                            while (errors.hasNext()) {
                                msg.append(errors.next()).append("\n");
                            }
                            message = msg.toString();
                        }
                    } else {
                    	boolean aggiornato = false;
                    	
                        // aggiungi l'allegato all'hhelper dei documenti ed invia la comunicazione...
                    	synchronized (documenti) {
                            int last = documenti.getAdditionalDocs().size();

                        	// NB in caso di "pdf riepilogo" si forza sempre in italiano la descrizione 
                        	// dell'allegato nell'xml della busta di riepilogo 
                        	// (SOLO per il riepilogo generato automaticamente) 
                        	if (this.summary) {
                        		if(pdfRiepilogoAutoGenerato) 
                        			this.description = DocumentiAllegatiHelper.QUESTIONARIO_PDFRIEPILOGO;
                        	}

                            // aggiungi l'allegato...
                            documenti.addDocUlteriore(
                                    this.description,
                                    data,
                                    this.contentType,
                                    this.attachmentFileName,
                                    this.uuid,
                                    evento, checkFirma);
                            
                            if (documenti.getAdditionalDocs().size() > last) {
                            	aggiornato = aggiornaComunicazione(iscrizioneHelper, this.form, true, evento);
    	                    }
						}

                        if (aggiornato && StringUtils.isNotEmpty(this.uuid)) {
                            message = "attachment uuid=" + this.uuid;
                            target = SUCCESS;
                        } else {
                            documenti.questionarioFixAttachments();
                            message = "error reading attachments";
                            evento.setMessage(message);
                            evento.setLevel(Level.ERROR);
                            target = ERROR;
                        }
                    }
                } else {
                    message = getText("invalid.qform.status");
                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(iscrizioneHelper.getIdBando());
                    evento.setLevel(Level.ERROR);
                    evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(iscrizioneHelper.getDescrizioneFunzione() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME
                    );
                    evento.setDetailMessage(message);
                    target = ERROR;
                }
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "addDocumento");
            ExceptionUtils.manageExceptionError(e, this);
            message = e.getMessage();
            target = ERROR;
        } finally {
        	if(evento != null) 
        		eventManager.insertEvent(evento);
        }

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseAddDoc(
                this,
                target,
                message,
                warning,
                (StringUtils.isNotEmpty(this.uuid) ? this.uuid : null));

        return target;
    }

    /**
     * (QCompiler) elimina un documento allegato
     * IN
     * idCom				id della comunicazione
     * uuids				lista di UUID degli allegati comunicazione da eliminare
     * form				json del questionario (in chiaro)
     */
    @Override
    public String deleteAllegato() {
        String target = SUCCESS;
        String message = null;
        Event evento = null;
        try {
            // recupera i parametri dalla comunicazione
            this.getParametriFromComunicazione();

            WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
                    .get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
            WizardDocumentiHelper documenti = iscrizioneHelper.getDocumenti();

            if (documenti == null) {
                // la sessione e' scaduta, occorre riconnettersi
                message = getText("Errors.sessionExpired");
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
                        CommonSystemConstants.ID_APPLICATIVO,
                        this.idCom
                );
                if (comunicazione.getDettaglioComunicazione().getStato().equals(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA)) {
                    //this.codice = helper.getCodice();
                    //this.codiceGara = helper.getCodiceGara();

                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(iscrizioneHelper.getIdBando());
                    evento.setLevel(Event.Level.INFO);
                    evento.setEventType(PortGareEventsConstants.DELETE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(iscrizioneHelper.getDescrizioneFunzione() + ": cancellazione documento");
                    try {
                        // verifica se il parametro "UUIDS" con la lista degli "uuid"
                        // da eliminare e' presente, altrimenti crea una lista
                        // con un unico elemento "uuid"
                        if (this.uuids == null) {
                            int n = (StringUtils.isNotEmpty(this.uuid) ? 1 : 0);
                            this.uuids = new String[n];
                            if (n > 0)
                                this.uuids[0] = this.uuid;
                        }

                        // elimina la lista di allegati...
                        synchronized (documenti) {
                            for (String uuid : this.uuids) {
                                int id = Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getUuid, uuid);
                                if (id >= 0) {
                                    Attachment attachment = documenti.getAdditionalDocs().get(id);

                                    evento.setMessage(evento.getMessage() + 
                                		String.format(
                                            "%s: cancellazione documento, file=%s, dimensione=%s, uuid=%s"
                                            , iscrizioneHelper.getDescrizioneFunzione()
                                            , attachment.getFileName()
                                            , attachment.getSize()
                                            , uuid
                                		) + "\n"
                                    );
                                    
                                    // rimuovi il documento i-esimo...
                                    documenti.removeDocUlteriore(id);
                                }
                            }
                            message = String.format("attachment uuids=%s removed", StringUtils.join(uuids, ", "));

                            // aggiorna la comunicazione...
                            if ( !aggiornaComunicazione(iscrizioneHelper, this.form, true, evento) ) {
                                target = CommonSystemConstants.PORTAL_ERROR;
                            }
						}
                    } catch (Exception ex) {
                        // ho rilanciato di nuovo la stessa azione con un refresh di pagina
                        ApsSystemUtils.logThrowable(ex, this, "deleteAllegato");
                    }
                } else {
                    message = getText("invalid.qform.status");
                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(iscrizioneHelper.getIdBando());
                    evento.setLevel(Level.ERROR);
                    evento.setEventType(PortGareEventsConstants.DELETE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(iscrizioneHelper.getDescrizioneFunzione() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME
                    );
                    evento.setDetailMessage(message);
                    target = ERROR;
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "deleteAllegato");
            ExceptionUtils.manageExceptionError(t, this);
            target = CommonSystemConstants.PORTAL_ERROR;
        } finally {
        	if(evento != null)
        		eventManager.insertEvent(evento);
		}

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseDel(
                this,
                target,
                message);

        return target;
    }

    /**
     * (QCompiler) scarica un documento allegato in una busta
     * IN
     * idCom				id della comunicazione
     * uuid				UUID dell'allegato comunicazione da scaricare
     * OUT
     * filename			nome del file
     * filedata			contenuto binario dell'allegato in formato base64
     */
    @Override
    public String downloadAllegato() {
        String target = SUCCESS;

        this.description = null;
        this.attachmentFileName = null;
        String data = null;
        this.contentType = null;
        String message = null;

        Event evento = null;
        try {
            // recupera i parametri dalla comunicazione
            this.getParametriFromComunicazione();

            // recupera l'allegato della comunicazione...
            // esegui il download dell'allegato
            WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
                    .get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
            WizardDocumentiHelper documenti = iscrizioneHelper.getDocumenti();

            if (documenti == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                int id = Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getUuid, uuid);

                InputStream is = documenti.downloadDocUlteriore(id);

                if (is != null) {
                    // codifica i dati binari in base64
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int next = is.read();
                    while (next > -1) {
                        bos.write(next);
                        next = is.read();
                    }
                    bos.flush();
                    data = Base64.encodeBase64String(bos.toByteArray());
                    bos.close();
                    bos = null;
                }

                if (data != null) {
                    Attachment attachment = documenti.getAdditionalDocs().get(id);
                    this.description = attachment.getDesc();
                    this.attachmentFileName = attachment.getFileName();
                    message = "attachment uuid=" + this.uuid + " OK";
                    target = SUCCESS;
                } else {
                    message = "attachment uuid=" + this.uuid + " NOT FOUND";
                    target = ERROR;
                }
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "downloadAllegato");
            ExceptionUtils.manageExceptionError(e, this);
            data = null;
            message = e.getMessage();
            target = ERROR;
        }

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseDownload(
                this,
                target,
                message,
                this.attachmentFileName,
                data);

        return target;
    }

    /**
     * (QCompiler) recuper l'allegato del questionario in una busta
     * IN
     * idCom				id della comunicazione
     * OUT
     * filename			nome del file
     * filedata			contenuto binario dell'allegato
     */
    @Override
    public String loadQuestionario() {
        String target = SUCCESS;

        this.description = null;
        this.attachmentFileName = null;
        String data = null;
        this.contentType = null;
        String message = null;

//		Event evento = null;
        try {
            // recupera i parametri dalla comunicazione
            this.getParametriFromComunicazione();

            WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
                    .get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
            WizardDocumentiHelper documenti = iscrizioneHelper.getDocumenti();

            if (documenti == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                // recupera l'allegato della comunicazione...
                // esegui il download dell'allegato
                int id = Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getFileName, DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME);

                // esegui il download del "quesionario.json"...
                QCQuestionario q = documenti.getQuestionarioElenchi();

                if (q != null) {
                    // recupera l'id del questionario (QFORM.ID)...
                    String idQuestionario = null;
                    if (documenti.getQuestionarioAssociato() != null) {
                        idQuestionario = Long.toString(documenti.getQuestionarioAssociato().getId());
                    }

                    // aggiorna la sezione "sysVariables" del documento json...
                    q.addSysVariablesElenco(
                            iscrizioneHelper,
                            idQuestionario
                    );

                    data = q.getQuestionario();

                    this.description = documenti.getAdditionalDocs().get(id).getDesc();
                    this.attachmentFileName = documenti.getAdditionalDocs().get(id).getFileName();
                    message = "attachment form OK";
                    //data = this.aggiornaParametriQuestionario(data);
                    target = SUCCESS;
                } else {
                    message = "attachment form NOT FOUND";
                    target = ERROR;
                }
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "loadQuestionario");
            ExceptionUtils.manageExceptionError(e, this);
            data = null;
            message = e.getMessage();
            target = ERROR;
        }

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseLoad(
                this,
                target,
                message,
                data);

        return target;
    }

    /**
     * (QCompiler) aggiungi un allegato in una busta
     * IN
     * idCom				id della comunicazione
     * form				json del questionario (in chiaro)
     */
    @Override
    public String saveQuestionario() {
        String target = SUCCESS;

        this.contentType = null;
        String message = null;
        AllegatoComunicazioneType allegato = null;
        Event evento = null;
        try {
            // recupera i parametri dalla comunicazione
            this.getParametriFromComunicazione();

            WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
                    .get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

            if (iscrizioneHelper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                message = getText("Errors.sessionExpired");
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
                        CommonSystemConstants.ID_APPLICATIVO,
                        this.idCom
                );
                if (comunicazione.getDettaglioComunicazione().getStato().equals(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA)) {
                    WizardDocumentiHelper documenti = iscrizioneHelper.getDocumenti();
                    this.description = DocumentiAllegatiHelper.QUESTIONARIO_DESCR;
                    this.attachmentFileName = DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME;

                    // recupera i dati del questionario...
                    // calcola la dimensione di "questionario.json" in chiaro
                    String q = QCQuestionario.decodeFormParameter(this.form);
                    int dimensioneDocumento = (int) Math.ceil(q.length() / 1024.0);

                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(iscrizioneHelper.getIdBando());
                    evento.setLevel(Event.Level.INFO);
                    evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(iscrizioneHelper.getDescrizioneFunzione() + ": documento ulteriore"
                            + " con file=" + this.attachmentFileName
                            + ", dimensione=" + dimensioneDocumento + "KB");

                    boolean controlliOk = true;
                    controlliOk = controlliOk && this.checkFileDescription(this.description, evento);
                    controlliOk = controlliOk && this.checkFileName(this.attachmentFileName, evento);
//					controlliOk = controlliOk && this.checkFileExtension(this.attachmentFileName, evento);
                    controlliOk = controlliOk && this.checkFileSize(dimensioneDocumento, this.attachmentFileName, documenti.getTotalSize(), evento);
//					//controlliOk = controlliOk && this.checkFileFormat(this.attachment, this.attachmentFileName, null, false, evento);

                    if (!controlliOk) {
                        message = "error bad file description or file name or file format";
                        evento.setMessage(message);
                        evento.setLevel(Level.ERROR);
                        target = ERROR;
                    } else {
                    	// invia la comunicazione con il nuovo allegato...
                    	synchronized (documenti) {
                        	if(controlliOk) {
                        		controlliOk = aggiornaComunicazione(iscrizioneHelper, this.form, true, evento);
                        	}
						}
                    	
                    	// ...e recupera l'UUID dell'allegato inserito...
                        if(controlliOk) {
                            comunicazione = this.comunicazioniManager.getComunicazione(
                                    CommonSystemConstants.ID_APPLICATIVO,
                                    this.idCom);

                            for (AllegatoComunicazioneType doc : comunicazione.getAllegato()) {
                                if (this.attachmentFileName.equalsIgnoreCase(doc.getNomeFile())) {
                                    allegato = doc;
                                    break;
                                }
                            }
                        }

                        if (allegato != null) {
                            message = "attachment form uuid=" + allegato.getUuid();
                            target = SUCCESS;
                        } else {
                            message = "error reading attachments";
                            evento.setMessage(message);
                            evento.setLevel(Level.ERROR);
                            target = ERROR;
                        }
                    }

                } else {
                    message = getText("invalid.qform.status");
                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(iscrizioneHelper.getIdBando());
                    evento.setLevel(Level.ERROR);
                    evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(iscrizioneHelper.getDescrizioneFunzione() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME
                    );
                    evento.setDetailMessage(message);
                    target = ERROR;
                }
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "saveQuestionario");
            ExceptionUtils.manageExceptionError(e, this);
            message = e.getMessage();
            target = ERROR;
        } finally {
        	if(evento != null)
        		eventManager.insertEvent(evento);
		}

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseSave(
                this,
                target,
                message
        );

        return target;
    }

    /**
     * prepara il messagio di warning in caso di mancata verifica della firma di un documento
     */
    private boolean verificaDocumentiNonFirmati(WizardDocumentiHelper helper) {
        boolean controlloOk = false;

        for (Attachment attachment : helper.getAdditionalDocs()) {
            if (UploadValidator.isFileExtension(attachment.getFileName(), ".P7M,.TSD")) {
                controlloOk = true;
            }
        }
        for (Attachment attachment : helper.getRequiredDocs()) {
            if (UploadValidator.isFileExtension(attachment.getFileName(), ".P7M,.TSD")) {
                controlloOk = true;
            }
        }
        if (!controlloOk) {
            // aggiungi un messaggio di WARNING alla action
            // NB: non servono altri messaggi, viene gia' aggiunto il messaggio
            // "Il sistema non e' stato in grado di eseguire le verifiche
            //  di validita' della firma. Eseguire la verifica utilizzando
            //  strumenti alternativi esterni all'applicativo. "
        }

        return controlloOk;
    }

    /**
     * Estrae il file presente nel file firmato digitalmente
     * e verifica se il contenuto &egrave; il medesimo di quello generato dall'applicativo.
     *
     * @param documento         documento firmato da aprire
     * @param documentoFilename nome del file associato al documento firmato da aprire
     * @param pdfUUID           UUID di verifica del file contenuto una volta estratta la firma
     * @return true se il file firmato digitalmente possiede l'UUID in input oppure
     * se il controllo non deve essere effettuato da configurazione,
     * false altrimenti
     * @throws Exception
     */
    private boolean checkFileRiepilogo(File documento, String documentoFilename, String pdfUUID, Event evento) throws Exception {
        boolean controlliOk = true;
        if (this.customConfigManager.isActiveFunction("GARE-DOCUMOFFERTA", "UPLOADPDFCOERENTEDATI")) {
            FileInputStream fis = null;
            try {
                // estrai il contenuto incapsulato nel documento (PDF, P7M, TSD)
                byte[] contenuto = this.getContenutoDocumentoFirmato(documento, documentoFilename, evento);
                String contentoFilename = this.getFilenameDocumentoFirmato(documento, documentoFilename, evento);
                boolean isPdf = contentoFilename.toUpperCase().endsWith(".PDF");
                boolean isP7m = contentoFilename.toUpperCase().endsWith(".P7M");

                // verifica se nel file e' presente un UUID e se corrisponde a quello generato...
                if (StringUtils.isNotEmpty(pdfUUID) && contenuto != null && contentoFilename != null) {
                    if (!(isPdf || isP7m)) {
                        // documenti firmati digitalmente che NON sono PDF, P7M...
                        String msg = this.getText("Errors.offertaTelematica.contenutoP7MNonValido");
                        this.addActionError(msg);
                        controlliOk = false;
                        if (evento != null) {
                            evento.setLevel(Event.Level.ERROR);
                            evento.setDetailMessage(msg);
                        }
                    } else {
                        // --- PDF, P7M ---
                        // verifica se il documento contiene l'UUID generato
                        if (!(PdfUtils.hasCorrectHash(contenuto, pdfUUID))) {
                            String msg = this.getText("Errors.offertaTelematica.hashContenutoFileFirmato");
                            this.addActionError(msg);
                            controlliOk = false;
                            if (evento != null) {
                                evento.setLevel(Event.Level.ERROR);
                                evento.setDetailMessage(msg);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                String msg = this.getText("Errors.offertaTelematica.contenutoFileFirmato");
                this.addActionError(msg);
                controlliOk = false;
                if (evento != null) {
                    evento.setLevel(Event.Level.ERROR);
                    evento.setDetailMessage(t);
                }
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return controlliOk;
    }

    @Override
    public String redirectToDGUE() {
        // NON PREVISTO PER GLI ELENCHI OPERATORE
        return ERROR;
    }

}
