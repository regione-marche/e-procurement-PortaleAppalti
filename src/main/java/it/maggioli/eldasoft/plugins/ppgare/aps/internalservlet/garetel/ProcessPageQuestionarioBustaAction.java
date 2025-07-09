package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.UploadValidator;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
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
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.dgue.DgueBuilder;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.utils.PdfUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;

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
 * Action di gestione delle operazioni nella pagina dei documenti delle buste
 * economica, tecnica, amministrativa
 *
 * @author
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageQuestionarioBustaAction extends ProcessPageDocumentiBustaAction 
	implements IProcessPageQuestionario 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3422807824712989386L;
	
	/**
	 * abilita il controllo di sincronizzazione tra gli allegati presenti nel questionario.json e helper dei documenti (xml/db) 
	 */
	public static final boolean ABILITA_VERIFICA_JSON_XML = false;
	
    private static final String CANCEL 	= "cancel";
    private static final String NEXT 	= "next";
    private static final String BACK 	= "back";

    private String multipartSaveDir;

    @Validate(EParamValidation.DIGIT)
    private String progressivoOfferta;        // attributo ad uso privato
    @Validate(EParamValidation.ACTION)
    private String nextResultAction;        // openGestioneBuste | openGestioneBusteDistinte

    // parametri in ingresso per le action associate a addDocumento(), deleteAllegato(), downloadAllegato()
    private Long idCom;                        // id comunicazione
    @Validate(EParamValidation.UNLIMITED_TEXT)
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

    private Long iddocdig;

    // parametri per la risposta json
    @Validate(EParamValidation.FILE_NAME)
    private String filename;
    @Validate(EParamValidation.CONTENT_TYPE)
    private String contentType;


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

    public Long getIddocdig() {
        return iddocdig;
    }

    public void setIddocdig(Long iddocdig) {
        this.iddocdig = iddocdig;
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
        String target = CANCEL;

        WizardPartecipazioneHelper partecipazione = GestioneBuste.getPartecipazioneFromSession().getHelper();

        if (partecipazione.isPlicoUnicoOfferteDistinte()) {
            if (this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
                this.nextResultAction = "openPageListaBusteTecnicheDistinte";
            } else if (this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
                this.nextResultAction = "openPageListaBusteEconomicheDistinte";
            } else {
                this.nextResultAction = "openGestioneBusteDistinte";
            }
        } else {
            this.nextResultAction = "openGestioneBuste";
        }

        return target;
    }

    /**
     * ...
     */
    @Override
    public String back() {
        String target = BACK;

        WizardPartecipazioneHelper partecipazione = GestioneBuste.getPartecipazioneFromSession().getHelper();

        if (partecipazione.isPlicoUnicoOfferteDistinte()) {
            if (this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
                this.nextResultAction = "openPageListaBusteTecnicheDistinte";
            } else if (this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
                this.nextResultAction = "openPageListaBusteEconomicheDistinte";
            } else {
                this.nextResultAction = "openGestioneBusteDistinte";
            }
        } else {
            this.nextResultAction = "openGestioneBuste";
        }

        return target;
    }

    /**
     * ...
     */
    @Override
    public String next() {
        String target = NEXT;

        return target;
    }

    /**
     * prepara i parametri per il riutilizzo del metodo "aggiornaAllegato()"
     *
     * @throws ApsException
     */
    private ComunicazioneType getParametriFromComunicazione() throws ApsException {
        this.codice = "";
        this.codiceGara = "";
        this.operazione = 0;
        this.tipoBusta = 0;
        this.progressivoOfferta = "";

        ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
                CommonSystemConstants.ID_APPLICATIVO,
                this.idCom);

        if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT)) {
            this.tipoBusta = PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
        } else if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA)) {
            this.tipoBusta = PortGareSystemConstants.BUSTA_AMMINISTRATIVA;
        } else if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA)) {
            this.tipoBusta = PortGareSystemConstants.BUSTA_TECNICA;
        } else if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA)) {
            this.tipoBusta = PortGareSystemConstants.BUSTA_ECONOMICA;
        }

        if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT)) {
            this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
        } else if (comunicazione.getDettaglioComunicazione().getTipoComunicazione()
                .startsWith(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT)) {
            this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
        } else {
            this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI;
        }

        this.progressivoOfferta = comunicazione.getDettaglioComunicazione().getChiave3();

        this.codice = comunicazione.getDettaglioComunicazione().getChiave2();        // viene inizializzato dopo aver recuperato l'helper dalla sessione
        this.codiceGara = comunicazione.getDettaglioComunicazione().getChiave2();    // viene inizializzato dopo aver recuperato l'helper dalla sessione
        
        return comunicazione;
    }

    /**
     * invia l'aggiornamento della comunicazione, con helper documenti e questionario.json verificati e sincronizzati
     *  
     * @throws GeneralSecurityException 
     * @throws IOException 
     */
    private boolean aggiornaComunicazione(
    		BustaDocumenti busta,		
            String questionario,				// json del QFORM inviato dal client
            boolean isEncoded64,				// true se il json e' codificato in base64
            Event evento
    ) throws IOException, GeneralSecurityException 
    {           
    	boolean controlliOk = false;
    	
    	WizardDocumentiBustaHelper documenti = (busta != null ? busta.getHelperDocumenti() : null);
    	if(documenti != null) {
    		ApsSystemUtils.getLogger().debug("aggiornaComunicazione() BEGIN {} {}", busta.getCodiceGara(), evento.getEventType());
    		documenti.getRequiredDocs().stream().forEach(a -> ApsSystemUtils.getLogger().debug("  doc ric: {} {}", a.getUuid(), a.getFileName()));
    		documenti.getAdditionalDocs().stream().forEach(a -> ApsSystemUtils.getLogger().debug("  doc ult: {} {}", a.getUuid(), a.getFileName()));

    		// 1) aggiorna il questionario.json nell'helper documenti... (OLD aggiornaHelperQuestionario(...))
    		//
    		if (StringUtils.isNotEmpty(form)) {
    			controlliOk = true;
    			
    	        // aggiorna l'allegato "questionario.json" della busta...
    	        if (isEncoded64) {
    	        	questionario = QCQuestionario.decodeFormParameter(questionario);
    	        }
    	        documenti.updateQuestionario(questionario);

    	        // aggiorna la busta di riepilogo...
    	        BustaRiepilogo riepilogo = GestioneBuste.getBustaRiepilogoFromSession();
    	        RiepilogoBusteHelper bustaRiepilogativa = riepilogo.getHelper();
    	        if (bustaRiepilogativa != null) {
    	            RiepilogoBustaBean bustaRie = bustaRiepilogativa.getRiepilogoBusta(this.tipoBusta, documenti.getCodice());
    	            if (bustaRie != null) {
    	                // estrai dal JSON la proprieta' "validationStatus"...
    	                QCQuestionario q = new QCQuestionario(this.tipoBusta, questionario);
    	                
    	                boolean completato = q.getValidationStatus() && q.getSummaryGenerated();

    	                // ed aggiorna lo stato del questionario nella busta di riepilogo...
    	                bustaRie.setQuestionarioPresente(documenti.isGestioneQuestionario());
    	                bustaRie.setQuestionarioCompletato(completato);
    	                if (documenti.getQuestionarioAssociato() != null) {
    	                	bustaRie.setQuestionarioId(documenti.getQuestionarioAssociato().getId());
    	                }
    	                bustaRie.setRiepilogoPdfAuto(q.isRiepilogoPdfAuto());
    	            } else {
    	                evento.setDetailMessage("Busta tipo=" + this.tipoBusta + " non individuata");
    	                evento.setLevel(Level.ERROR);
    	                controlliOk = false;
    	            }
    	        } else {
    	            evento.setDetailMessage("Busta di riepilogo non individuata");
    	            evento.setLevel(Level.ERROR);
    	            controlliOk = false;
    	        }
    		}
    		
    		// 2) verifica se questionario.json e helper documenti sono allineati
    		//
    		if(ABILITA_VERIFICA_JSON_XML) {
	    		if(controlliOk) {
	    	    	List<AnomalieQuestionarioInfo> anomalie = null;
	    	    	if(documenti != null) 
	    	    		anomalie = documenti.validaQuestionarioGare(busta.getTipoBusta());
	    			
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
    		
    		ApsSystemUtils.getLogger().debug("aggiornaComunicazione() END {} {}", busta.getCodiceGara(), evento.getEventType());
    	}
		return controlliOk;
    }

    /**
     * (QCompiler) inizializza il questionario
     */
    @Override
    public String initQuestionario() {
        String target = SUCCESS;

        try {
            BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
            WizardDocumentiBustaHelper documentiBustaHelper = (busta != null ? busta.getHelperDocumenti() : null);

            if (documentiBustaHelper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                // la sessione non e' scaduta, per cui proseguo regolarmente
                // verifica se il questionario esiste gia'...
                // ed eventualmente aggiungi alla comunicazione un allegato "questionario.json"
                if (!documentiBustaHelper.isQuestionarioAllegato()) {

                    // prepara gli attributi per l'aggiunta del questionario...
                    byte[] survey = null;
                    if (documentiBustaHelper.getQuestionarioAssociato().getOggetto() != null) {
                        survey = documentiBustaHelper.getQuestionarioAssociato().getOggetto().getBytes("UTF-8");
                    } else {
                        survey = new byte[0];
                    }
                    this.docUlterioreDesc = DocumentiAllegatiHelper.QUESTIONARIO_DESCR;
                    this.docUlterioreFileName = DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME;
                    this.docUlterioreContentType = "JSON";
                    int dimensioneDocumento = (int) Math.ceil(survey.length / 1024.0);

                    // aggiungi il documento...
                    String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
                    if (StringUtils.isEmpty(codice)) {
                        codice = this.codiceGara;
                    }

                    Event evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(codice);
                    evento.setLevel(Event.Level.INFO);
                    evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(busta.getDescrizioneBusta() + ": documento "
                            + " con descrizione=" + this.docUlterioreDesc
                            + ", file=" + this.docUlterioreFileName
                            + ", dimensione=" + dimensioneDocumento + "KB");

                    if (Attachment.indexOf(documentiBustaHelper.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) == -1) {
                        documentiBustaHelper.addDocUlteriore(
                                this.docUlterioreDesc,
                                survey,
                                this.docUlterioreContentType,
                                this.docUlterioreFileName,
                                null,
                                evento,
                                null);

                        if (!this.aggiornaAllegato()) {
                            target = INPUT;
                        }
                    }

                    this.eventManager.insertEvent(evento);
                }

                // Avvia il questionario...
                // Esegui la action "openQC" !!!
            }
        } catch (ApsException t) {
            ApsSystemUtils.logThrowable(t, this, "initQuestionario");
            ExceptionUtils.manageExceptionError(t, this);
            target = CommonSystemConstants.PORTAL_ERROR;
        } catch (GeneralSecurityException e) {
            ApsSystemUtils.logThrowable(e, this, "initQuestionario", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
            ExceptionUtils.manageExceptionError(e, this);
            target = CommonSystemConstants.PORTAL_ERROR;
        } catch (Throwable e) {
            ApsSystemUtils.logThrowable(e, this, "initQuestionario", "Errore durante la verifica del formato dell'allegato richiesto " + this.docRichiestoFileName);
            ExceptionUtils.manageExceptionError(e, this);
            target = CommonSystemConstants.PORTAL_ERROR;
        }

        return target;
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
                uuid);

        return target;
    }

    /**
     * (QCompiler) aggiungi un allegato in una busta
     * IN
     * idCom				id della comunicazione
     * description			descrizione dell'allegato
     * attachmentFileName	nome file dell'allegato
     * attachmentData		contenuto binario dell'allegato
     * uuid					uuid del nuovo allegato
     * signed				True se il file e' firmato digitalmente
     * summary				True se il file e' il pdf firmato del riepilogo questionario
     * form					json del questionario (in formato base64)
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
            // recupera i parametri dalla comunicazione (tipobusta, operazione, progressivoofferta, codice, codicegara)
        	ComunicazioneType comunicazione = this.getParametriFromComunicazione();

            BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
            WizardDocumentiBustaHelper helper = (busta != null ? busta.getHelperDocumenti() : null);

            if (helper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                if (comunicazione.getDettaglioComunicazione().getStato().equals(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA)) {

                    this.codice = helper.getCodice();
                    this.codiceGara = helper.getCodiceGara();

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
                                    , busta.getDescrizioneBusta()
                                    , attachmentFileName
                                    , dimensioneDocumento
                                    , uuid
                            )
                    );

                    // verifica se l'allegato e' valido (con descrizione, filename corretto, estesione corretta e dimensione nei limiti)
                    boolean controlliOk =
                            checkFileDescription(description, evento)
                            && checkFileName(attachmentFileName, evento)
                            && checkFileExtension(attachmentFileName, evento)
                            && checkFileSize(dimensioneDocumento, attachmentFileName, getActualTotalSize(helper), evento);
                    		//&& this.checkFileFormat(this.attachment, this.attachmentFileName, null, evento, false);

                    // verifica se il file e' firmato digitalmente...
                    DocumentiAllegatiFirmaBean checkFirma = null;
                    controlliOk = controlliOk && checkFileFormat(
                            attachmentData
                            , attachmentFileName
                            , signed ? PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO : null
                            , false
                            , evento
                    );
                    if (controlliOk) {
                        Date checkDate = Date.from(Instant.now());
                        checkFirma = checkFileSignature(
                                attachmentData
                                , attachmentFileName
                                , signed ? PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO : null
                                , checkDate
                                , false
                                , evento
                        );
                        // se presenti recupera i messaggi della action per comporre il warning sui documenti firmati...
                        if (!this.verificaDocumentiNonFirmati(helper) || (checkFirma == null || !checkFirma.getFirmacheck())) {
                            if (this.getActionMessages().size() > 0) {
                                StringBuilder msg = new StringBuilder();
                                Iterator<String> errors = this.getActionMessages().iterator();
                                while (errors.hasNext()) {
                                    msg.append(errors.next()).append("\n");
                                }
                                warning = msg.toString();
                            }
                        }
                    }

                    // in caso di riepilogo del questionario verifica l'allegato...
                    if (controlliOk) {
                        if (this.summary) {
                            // verifica se l'hash del file corrisponde a quello generato (presente nell'xml)
                            String pdfUuid = helper.getPdfUuid();
                            controlliOk = controlliOk && this.checkFileRiepilogo(
                                    this.attachmentData,
                                    this.attachmentFileName,
                                    pdfUuid,
                                    evento);

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
                                                , busta.getDescrizioneBusta()
                                                , attachmentFileName
                                                , dimensioneDocumento
                                                , uuid
                                        )
                                );
                            }
                        }
                    }

                    // se l'allegato e' valido aggiungilo all'helper documenti in sessione
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
                    	
                        // aggiungi l'allegato all'helper documenti ed invia la comunicazione...
                        synchronized (helper) {
                        	int last = helper.getAdditionalDocs().size();
                        	
                        	// NB in caso di "pdf riepilogo" si forza sempre in italiano la descrizione 
                        	// dell'allegato nell'xml della busta di riepilogo 
                        	// (SOLO per il riepilogo generato automaticamente) 
                        	if (this.summary) {
                        		if(pdfRiepilogoAutoGenerato) 
                        			this.description = DocumentiAllegatiHelper.QUESTIONARIO_PDFRIEPILOGO;
                        	}
                        	
	                        helper.addDocUlteriore(
	                                this.description,
	                                data,
	                                this.contentType,
	                                this.attachmentFileName,
	                                this.uuid,
	                                evento,
	                                checkFirma);
                        
	                        if (helper.getAdditionalDocs().size() > last) {
	                        	aggiornato = aggiornaComunicazione(busta, this.form, true, evento);
	                        }
						}

                        if (aggiornato && StringUtils.isNotEmpty(this.uuid)) {
                            message = "attachment uuid=" + this.uuid;
                            target = SUCCESS;
                        } else {
                            helper.questionarioFixAttachments();
                            message = "error reading attachments";
                            evento.setDetailMessage(message);
                            evento.setLevel(Level.ERROR);
                            target = ERROR;
                        }
                    }
                } else {
                    message = getText("invalid.qform.status");
                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(helper != null ? helper.getCodice() : null);
                    evento.setLevel(Level.ERROR);
                    evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(busta.getDescrizioneBusta() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME
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
     * (QCompiler) elimina un documento allegato in una busta
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

            BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
            WizardDocumentiBustaHelper helper = (busta != null ? busta.getHelperDocumenti() : null);

            if (helper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                message = getText("Errors.sessionExpired");
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
                        CommonSystemConstants.ID_APPLICATIVO,
                        this.idCom
                );
                if (comunicazione.getDettaglioComunicazione().getStato().equals(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA)) {
                	
                	boolean controlliOk = true;
                    this.codice = helper.getCodice();
                    this.codiceGara = helper.getCodiceGara();

                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(helper.getCodice());
                    evento.setLevel(Event.Level.INFO);
                    evento.setEventType(PortGareEventsConstants.DELETE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(busta.getDescrizioneBusta() + ": cancellazione documento");
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
                        synchronized (helper) {
	                        for (String uuid : this.uuids) {
	                            int id = Attachment.indexOf(helper.getAdditionalDocs(), Attachment::getUuid, uuid);
	                            if (id >= 0) {
	                                Attachment attachment = helper.getAdditionalDocs().get(id);
	
	                                evento.setMessage(evento.getMessage() +
	                                        String.format(
	                                                "%s: cancellazione documento, file=%s, dimensione=%s, uuid=%s"
	                                                , busta.getDescrizioneBusta()
	                                                , attachment.getFileName()
	                                                , attachment.getSize()
	                                                , uuid
	                                        )
	                                );
	
	                                // rimuovi il documento i-esimo...
	                                helper.removeDocUlteriore(id);
	                            }
	                        }
	                        message = String.format("attachment uuids=%s removed", StringUtils.join(uuids, ", "));
	
	                        // aggiorna la comunicazione...
	                        if( !aggiornaComunicazione(busta, this.form, true, evento) ) {
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
                    evento.setDestination(helper != null ? helper.getCodice() : null);
                    evento.setLevel(Level.ERROR);
                    evento.setEventType(PortGareEventsConstants.DELETE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(busta.getDescrizioneBusta() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME
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
            BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
            WizardDocumentiBustaHelper helper = (busta != null ? busta.getHelperDocumenti() : null);

            if (helper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                this.codice = helper.getCodice();
                this.codiceGara = helper.getCodiceGara();

                int id = Attachment.indexOf(helper.getAdditionalDocs(), Attachment::getUuid, uuid);

                InputStream is = helper.downloadDocUlteriore(id);

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
                    Attachment attachment = helper.getAdditionalDocs().get(id);
                    description = attachment.getDesc();
                    attachmentFileName = attachment.getFileName();
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

            GestioneBuste buste = GestioneBuste.getFromSession();
            BustaDocumenti busta = buste.getBustaFromSession(this.tipoBusta);
            WizardDocumentiBustaHelper helper = (busta != null ? busta.getHelperDocumenti() : null);
            BustaPartecipazione bustaPart = buste.getBustaPartecipazione();
            WizardPartecipazioneHelper partecipazione = (bustaPart != null ? bustaPart.getHelper() : null);
            WizardDatiImpresaHelper impresa = buste.getImpresa();

            if (helper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                this.addActionError(this.getText("Errors.sessionExpired"));
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                // recupera l'allegato della comunicazione...
                // esegui il download dell'allegato
                this.codice = helper.getCodice();
                this.codiceGara = helper.getCodiceGara();

                int id = Attachment.indexOf(helper.getAdditionalDocs(), Attachment::getFileName, DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME);

                // esegui il download del "questionario.json"...
                QCQuestionario q = helper.getQuestionarioGare(this.tipoBusta);

                if (q != null) {
                    // recupera l'id del questionario (QFORM.ID)...
                    String idQuestionario = null;
                    if (helper.getQuestionarioAssociato() != null) {
                        idQuestionario = Long.toString(helper.getQuestionarioAssociato().getId());
                    }

                    // verifica la presenza del "dgue request"
                    // per i QFORM si trova sempre tra i documenti della gara e non nella busta
                    //long idDgueRequest = busta.getIdDgueRequestDocument();
                    long idDgueRequest = buste.getIdDgueRequestDocument();
                    boolean dgueRequest = (idDgueRequest > 0);

                    // aggiorna la sezione "sysVariables" del documento json...
                    q.addSysVariablesBusta(
                            impresa,
                            partecipazione,
                            idQuestionario,
                            dgueRequest);

                    data = q.getQuestionario();

                    this.description = helper.getAdditionalDocs().get(id).getDesc();
                    this.attachmentFileName = helper.getAdditionalDocs().get(id).getFileName();
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
                data
        );

        return target;
    }

    /**
     * (QCompiler) aggiungi un allegato in una busta
     * IN
     * idCom			id della comunicazione
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
        	ComunicazioneType comunicazione = this.getParametriFromComunicazione();

            BustaDocumenti busta = GestioneBuste.getBustaFromSession(tipoBusta);
            WizardDocumentiBustaHelper helper = (busta != null ? busta.getHelperDocumenti() : null);

            if (helper == null) {
                // la sessione e' scaduta, occorre riconnettersi
                message = getText("Errors.sessionExpired");
                target = CommonSystemConstants.PORTAL_ERROR;
            } else {
                if (comunicazione.getDettaglioComunicazione().getStato().equals(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA)) {
                    this.codice = helper.getCodice();
                    this.codiceGara = helper.getCodiceGara();
                    this.description = DocumentiAllegatiHelper.QUESTIONARIO_DESCR;
                    this.attachmentFileName = DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME;

                    // recupera i dati del questionario...

                    // calcola la dimensione di "questionario.json" in chiaro
                    String q = QCQuestionario.decodeFormParameter(this.form);
                    int dimensioneDocumento = (int) Math.ceil(q.length() / 1024.0);

                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(codice);
                    evento.setLevel(Event.Level.INFO);
                    evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(busta.getDescrizioneBusta() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME
                    );

                    boolean controlliOk = true;
                    controlliOk = controlliOk && this.checkFileDescription(this.description, evento);
                    controlliOk = controlliOk && this.checkFileName(this.attachmentFileName, evento);
//					controlliOk = controlliOk && this.checkFileExtension(this.attachmentFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento);
                    controlliOk = controlliOk && this.checkFileSize(dimensioneDocumento, this.attachmentFileName, getActualTotalSize(helper), evento);
//					//controlliOk = controlliOk && this.checkFileFormat(this.attachment, this.attachmentFileName, null, evento, false);
                    
                    if (!controlliOk) {
                        message = "error bad file description or file name or file format";
                        evento.setDetailMessage(message);
                        evento.setLevel(Level.ERROR);
                        target = ERROR;
                    } else {
                    	
                    	// invia la comunicazione con il nuovo allegato...
                    	synchronized(helper) {
	                    	if(controlliOk) {
	                    		controlliOk = aggiornaComunicazione(busta, this.form, true, evento);
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
                            evento.setDetailMessage(message);
                            evento.setLevel(Level.ERROR);
                            target = ERROR;
                        }
                    }
                } else {
                    message = getText("invalid.qform.status");
                    evento = new Event();
                    evento.setUsername(this.getCurrentUser().getUsername());
                    evento.setDestination(helper != null ? helper.getCodice() : null);
                    evento.setLevel(Level.ERROR);
                    evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
                    evento.setIpAddress(this.getCurrentUser().getIpAddress());
                    evento.setSessionId(this.getRequest().getSession().getId());
                    evento.setMessage(busta.getDescrizioneBusta() + ": documento ulteriore"
                            + " con file=" + DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME
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
                message);

        return target;
    }

    /**
     * prepara il messagio di warning in caso di mancata verifica della firma di un documento
     */
    private boolean verificaDocumentiNonFirmati(WizardDocumentiBustaHelper helper) {
        boolean controlloOk = false;

        for (Attachment attachment : helper.getAdditionalDocs()) {
            if (UploadValidator.isFileExtension(attachment.getFileName(), ".P7M,.TSD")) {
                controlloOk = true;
            }
        }
        for (Attachment attachment : helper.getAdditionalDocs()) {
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

    /**
     * (QCompiler) restituisce l'url del DGUE
     * IN
     * idCom				id della comunicazione
     * OUT
     * form 				url di redirect del DGUE
     */
    @Override
    public String redirectToDGUE() {
        String target = "redirectToDGUE";

        this.contentType = null;
        String message = null;
        String url = null;
        try {
            // recupera i parametri dalla comunicazione
            this.getParametriFromComunicazione();

            // DGUE - esegui il redirect per il DGUE
            DgueBuilder dgue = new DgueBuilder()
                    .setCodiceGara(this.codiceGara)
                    .setCodiceLotto(this.codice)
                    .setDgueLinkActionRti("/do/FrontEnd/GareTel/dgueRti.action")
                    .setDgueLinkAction("/do/FrontEnd/GareTel/dgue.action")
                    .setGestioneBuste(GestioneBuste.getFromSession())
                    .setIddocdig(this.iddocdig)
                    .redirectToDGUE();
            url = dgue.getUrl();
            target = dgue.getTarget();
            message = "redirectToDGUE";
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
            ExceptionUtils.manageExceptionError(e, this);
            message = e.getMessage();
            target = ERROR;
        }

        // prepara la risposta in formato JSON...
        target = QuestionariUtils.jsonResponseRedirectToDGUE(
                this,
                target,
                message,
                url);

        return target;
    }

}
