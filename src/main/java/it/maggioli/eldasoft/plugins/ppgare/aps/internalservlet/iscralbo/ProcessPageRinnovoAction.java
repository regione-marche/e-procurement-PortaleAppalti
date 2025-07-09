package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * Action di gestione delle operazioni nella pagina dei documenti di rinnovo
 * iscrizione ad un catologo/elenco
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class ProcessPageRinnovoAction extends AbstractProcessPageAction 
	implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6474905203547737433L;

	private static final String FUNZIONE = "Rinnovo iscrizione";
	
	/**
	 * Riferimento al manager per la gestione dei parametri applicativo.
	 */
	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;

	private Long docRichiestoId;
	private File docRichiesto;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docRichiestoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docRichiestoFileName;
	@Validate(EParamValidation.GENERIC)
	private String docUlterioreDesc;
	private File docUlteriore;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docUlterioreContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docUlterioreFileName;
	private Integer formato;

	private InputStream inputStream;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private int tipoElenco;
	
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
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

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getTipoElenco() {
		return tipoElenco;
	}

	public void setTipoElenco(int tipoElenco) {
		this.tipoElenco = tipoElenco;
	}

	public Integer getFormato() {
		return formato;
	}

	public void setFormato(Integer formato) {
		this.formato = formato;
	}


	/**
	 * ... 
	 */
	@Override
	public String next() {
		return null;
	}

	/**
	 * aggiungi un documento richiesto
	 */
	public String addDocRich() {
		String target = "backToDocumenti";
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		try {
			if (rinnovoHelper != null) {
				
				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(rinnovoHelper)
						.setDocumento(docRichiesto)
						.setDocumentoFileName(docRichiestoFileName)
						.setDocumentoFormato(formato)
						.setCheckFileSignature(true)
						.setEventoDestinazione(rinnovoHelper.getIdBando())
						.setEventoMessaggio(FUNZIONE + ": documento richiesto"
							  				+ " con id=" + this.docRichiestoId
							  				+ ", file=" + this.docRichiestoFileName
							  				+ ", dimensione=" + FileUploadUtilities.getFileSize(docRichiesto) + "KB");
				
				if ( getUploadValidator().validate() ) {
					if (Attachment.indexOf(rinnovoHelper.getDocumenti().getRequiredDocs(), Attachment::getId, docRichiestoId) == -1) {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						rinnovoHelper.getDocumenti().addDocRichiesto(
								docRichiestoId,
								docRichiesto,
								docRichiestoContentType,
								docRichiestoFileName,
								getUploadValidator().getEvento(),
								getUploadValidator().getCheckFirma());
						
						if( !aggiornaAllegato() ) {
							target = CommonSystemConstants.PORTAL_ERROR;
						}
					}
				} else {
					target = INPUT;
				}
				
				this.eventManager.insertEvent(getUploadValidator().getEvento());
				
			} else {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich");
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addDocRich");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}		
		return target;
	}

	/**
	 * aggiungi un documento richiesto
	 */
	public String addUltDoc() {
		String target = "backToDocumenti";
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
	
		try {
			if (rinnovoHelper != null) {
				// valida l'upload del documento...
				getUploadValidator()
						.setHelper(rinnovoHelper)
						.setDocumentoDescrizione(docUlterioreDesc)
						.setDocumento(docUlteriore)
						.setDocumentoFileName(docUlterioreFileName)
						.setOnlyP7m(false)
						.setCheckFileSignature(true)
						.setEventoDestinazione(rinnovoHelper.getIdBando())
						.setEventoMessaggio(FUNZIONE + ": documento ulteriore"
											+ ", file=" + this.docUlterioreFileName
											+ ", dimensione=" + FileUploadUtilities.getFileSize(this.docUlteriore) + "KB");
  				
				if ( getUploadValidator().validate() ) {
					if (Attachment.indexOf(rinnovoHelper.getDocumenti().getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) != -1) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
						getUploadValidator().getEvento().setLevel(Event.Level.ERROR);
						getUploadValidator().getEvento().setDetailMessage("Il file "
								+ this.docUlterioreFileName
								+ " viene scartato in quanto esiste già un documento ulteriore caricato con la stessa descrizione ("
								+ this.docUlterioreDesc + ")");
					} else {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						rinnovoHelper.getDocumenti().addDocUlteriore(
								docUlterioreDesc,
								docUlteriore,
								docUlterioreContentType,
								docUlterioreFileName,
								getUploadValidator().getEvento(),
								getUploadValidator().getCheckFirma()
						);
						
						this.docUlterioreDesc = null;
						if( !aggiornaAllegato() ) {
							target = CommonSystemConstants.PORTAL_ERROR;
						}
					}
				} else {
					target = INPUT;
				}
				
				this.eventManager.insertEvent(getUploadValidator().getEvento());
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
	 * aggiorna un documento allegato per una domanda di rinnovo
	 *  
	 * @throws Exception 
	 */
	private boolean aggiornaAllegato() throws Exception {
		String target = SUCCESS;

		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			target = RinnovoAction.saveDocumenti(
					helper
					, session 
					, this
					, new Date()
			);
		}
		
		return (SUCCESS.equalsIgnoreCase(target));
	}

	
}
