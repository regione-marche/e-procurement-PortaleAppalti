package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Action di gestione delle operazioni nella pagina dei documenti di rinnovo
 * iscrizione ad un catologo/elenco
 *
 * @author Marco.Perazzetta
 */
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
	private String docRichiestoContentType;
	private String docRichiestoFileName;
	private String docUlterioreDesc;
	private File docUlteriore;
	private String docUlterioreContentType;
	private String docUlterioreFileName;
	private Integer formato;

	private InputStream inputStream;

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
		this.docUlterioreDesc = docUlterioreDesc;
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
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	private int getActualTotalSize(WizardDocumentiHelper helper) {
		int total = 0;
		for (Integer s : helper.getDocRichiestiSize()) {
			total += s;
		}
		for (Integer s : helper.getDocUlterioriSize()) {
			total += s;
		}
		return total;
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
				boolean controlliOk = true;
				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				int dimensioneDocumento = FileUploadUtilities.getFileSize(docRichiesto);
	
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(rinnovoHelper.getIdBando());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(FUNZIONE + ": documento richiesto"
								  	+ " con id="+this.docRichiestoId
									+ ", file="+this.docRichiestoFileName
									+ ", dimensione="+dimensioneDocumento + "KB");
	
				controlliOk = controlliOk && this.checkFileSize(this.docRichiesto, this.docRichiestoFileName, getActualTotalSize(rinnovoHelper.getDocumenti()), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docRichiestoFileName, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.docRichiesto, this.docRichiestoFileName, this.formato, evento, onlyP7m);
				
				if (controlliOk) {	
					if (!rinnovoHelper.getDocumenti().getDocRichiestiId().contains(this.docRichiestoId)) {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza

						rinnovoHelper.getDocumenti().addDocRichiesto(
								this.docRichiestoId, 
								this.docRichiesto, 
								this.docRichiestoContentType, 
								this.docRichiestoFileName,
								evento);
						
						if( !aggiornaAllegato() ) {
							target = CommonSystemConstants.PORTAL_ERROR;
						}
					}
				} else {
					target = INPUT;
				}
				this.eventManager.insertEvent(evento);
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
				boolean controlliOk = true;

				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
	
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(rinnovoHelper.getIdBando());
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(FUNZIONE + ": documento ulteriore"
						+ ", file="+this.docUlterioreFileName
						+ ", dimensione="+dimensioneDocumento + "KB");
	
				controlliOk = controlliOk && this.checkFileDescription(this.docUlterioreDesc, evento);
				controlliOk = controlliOk && this.checkFileSize(this.docUlteriore, this.docUlterioreFileName, getActualTotalSize(rinnovoHelper.getDocumenti()), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docUlterioreFileName, evento);
				controlliOk = controlliOk && this.checkFileExtension(this.docUlterioreFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.docUlteriore, this.docUlterioreFileName, null, evento, false);
				
				if (controlliOk) {
					if (rinnovoHelper.getDocumenti().getDocUlterioriDesc().contains(this.docUlterioreDesc)) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage("Il file "
								+ this.docUlterioreFileName
								+ " viene scartato in quanto esiste già un documento ulteriore caricato con la stessa descrizione ("
								+ this.docUlterioreDesc + ")");
					} else {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						evento.setMessage(FUNZIONE + ": documento ulteriore, " + 
										  "file=" + this.docUlterioreFileName + ", " + 
										  "dimensione=" + dimensioneDocumento + "KB");
						
						rinnovoHelper.getDocumenti().addDocUlteriore(
								this.docUlterioreDesc, 
								this.docUlteriore, 
								this.docUlterioreContentType, 
								this.docUlterioreFileName,
								evento);
						
						this.docUlterioreDesc = null;
						
						if( !aggiornaAllegato() ) {
							target = CommonSystemConstants.PORTAL_ERROR;
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
	 * aggiorna un documento allegato per una domanda di rinnovo 
	 */
	private boolean aggiornaAllegato() throws ApsException {
		String target = SUCCESS;

		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			target = RinnovoAction.saveDocumenti(
					helper,
					session, 
					this,
					new Date());
		}
		
		return (SUCCESS.equalsIgnoreCase(target));
	}

	
}
