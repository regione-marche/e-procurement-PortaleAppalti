package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

/**
 * ...
 * 
 */
public class ProcessPageDocumentiNuovoSoccorsoAction extends ProcessPageDocumentiNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4050331248422517220L;
	
	private ICustomConfigManager customConfigManager;
	
	private File docRichiesto;
	private String docRichiestoContentType;
	private String docRichiestoFileName;
	private Long docRichiestoId;
	private Integer formato;
	
	private boolean deleteAllegatoRichiesto;
		
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
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

	public Long getDocRichiestoId() {
		return docRichiestoId;
	}

	public void setDocRichiestoId(Long docRichiestoId) {
		this.docRichiestoId = docRichiestoId;
	}

	public boolean isDeleteAllegatoRichiesto() {
		return deleteAllegatoRichiesto;
	}

	public void setDeleteAllegatoRichiesto(boolean deleteAllegatoRichiesto) {
		this.deleteAllegatoRichiesto = deleteAllegatoRichiesto;
	}
	
	public Integer getFormato() {
		return formato;
	}

	public void setFormato(Integer formato) {
		this.formato = formato;
	}

	/**
	 * aggiungi un allegato ulteriore allacomunicazione 
	 */
	@Override
	public String addUltDoc() {
		return super.addUltDoc();
	}
	
	/**
	 * aggiungi un allegato richiesto alla comunicazione 
	 */
	public String addDocRich() {
		String target = "backToDocumenti";

		WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		
		Event evento = null;
		try {
			DocumentiComunicazioneHelper documenti = helper.getDocumenti();
	
			int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docRichiesto);
	
			// traccia l'evento di upload di un file...
			evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(helper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Invia nuova comunicazione: " 
							  + "allegato file=" + this.docRichiestoFileName + ", "
							  + "dimensione=" + dimensioneDocumento + "KB");
				
			boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
			boolean controlliOk = true;
			controlliOk = controlliOk && this.checkFileSize(
					this.docRichiesto, 
					this.docRichiestoFileName, 
					this.getActualTotalSize(documenti.getDocRichiestiSize()),
					FileUploadUtilities.getLimiteUploadFile(this.appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_UPLOAD_FILE),
					FileUploadUtilities.getLimiteTotaleUploadFile(this.appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE),
					evento);
			controlliOk = controlliOk && this.checkFileName(this.docRichiestoFileName, evento);
			controlliOk = controlliOk && this.checkFileFormat(this.docRichiesto, this.docRichiestoFileName, this.formato, evento, onlyP7m);
			//controlliOk = controlliOk && this.checkFileExtension(this.docRichiestoFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento);

			if (controlliOk) {
				// si inseriscono i documenti in sessione
				if (!documenti.getDocRichiestiId().contains(this.docRichiestoId)) {
					documenti.addDocRichiesto(
							this.docRichiestoId,
							this.docRichiesto,
							this.docRichiestoContentType,
							this.docRichiestoFileName,
							evento);
	
					//if( !this.aggiornaAllegato() ) {
					//	target = CommonSystemConstants.PORTAL_ERROR; 
					//}
				}
			}
		
		} catch (GeneralSecurityException e) {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable e) {
			this.addActionError(this.getText("Errors.unexpected"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}

	/**
	 * ...
	 */
	public String downloadAllegatoRichiesto() {
		String target = SUCCESS;
		
		WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		DocumentiComunicazioneHelper documenti = helper.getDocumenti();
		
		if (documenti == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		} else {
			try {
				this.docRichiestoContentType = documenti.getDocRichiestiContentType().get(Integer.parseInt(this.id));
				this.setFilename(documenti.getDocRichiestiFileName().get(Integer.parseInt(this.id)));
				this.inputStream = documenti.downloadDocRichiesto(Integer.parseInt(this.id));
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		}
		return target;
	}

	/**
	 * Chiede la conferma per l'eliminazione di un allegato richiesto
	 */
	public String confirmDeleteAllegatoRichiesto() {
		String target = SUCCESS;
		this.setDeleteAllegatoRichiesto(true);
		return target;
	}

	/**
     * Elimina un documento allegato richiesto
     */
	public String deleteAllegatoRichiesto() {
		String target = "backToDocumenti";
		
		WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			DocumentiComunicazioneHelper documenti = helper.getDocumenti();
			int id = Integer.parseInt(this.id);
			
			// traccia l'evento di rimozione di un allegato...
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(helper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Invia nuova comunicazione: " +
					"cancellazione file=" + documenti.getDocRichiestiFileName().get(id) + ", " +
					"dimensione=" + documenti.getDocRichiestiSize().get(id) + "KB");
			
			documenti.removeDocRichiesto(id);
			
			//if( !this.aggiornaAllegato() ) {
			//	target = CommonSystemConstants.PORTAL_ERROR; 
			//}
			
			this.eventManager.insertEvent(evento);
		}
		return target;
	}

	/**
	 * Annulla la procedura di eliminazione di un allegato richiesto
	 */
	public String cancelRichiesto() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		try {
			if (null != this.getCurrentUser()
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
	
				WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper) session
						.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
				DocumentiComunicazioneHelper documenti = helper.getDocumenti();
				
				// verifica la presenza degli allegati richiesti
				boolean continua = this.checkDocumenti(helper.getDocumenti(), helper.getDocumentiRichiesti());
				
				if(continua) {
					this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
							helper.getNextStepNavigazione(WizardSoccorsoIstruttorioHelper.STEP_DOCUMENTI)));
				} else {
					target = INPUT;
				}
				
				this.from = (String)this.session.get(ComunicazioniConstants.SESSION_ID_FROM);
			} else {
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "next");
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}
	
	/**
	 * ...
	 */
	@Override
	public String back() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper) session
					.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
			
			this.from = (String)this.session.get(ComunicazioniConstants.SESSION_ID_FROM);
			this.setNextResultAction(InitNuovaComunicazioneAction.setNextResultAction(
					helper.getPreviousStepNavigazione(WizardSoccorsoIstruttorioHelper.STEP_DOCUMENTI)));

		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	
//  NB: PER SI LAVORA ANCORA IN SESSIONE  
//	/**
//	 * aggiorna gli allegati della comunicazione   
//	 */
//	private boolean aggiornaAllegato() throws ApsException {
//		String target = SUCCESS;
//
//		WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper) session
//			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
//		if (helper == null) {
//			// la sessione e' scaduta, occorre riconnettersi
//			this.addActionError(this.getText("Errors.sessionExpired"));
//			target = CommonSystemConstants.PORTAL_ERROR;
//		} else {
////			target = ProcessPageRiepilogoNuovoSoccorsoAction.saveDocumenti(
////					helper,
////					this.session, 
////					this);
//		}
//		return (SUCCESS.equalsIgnoreCase(target));
//	}
	
	
	private boolean checkDocumenti(
			DocumentiComunicazioneHelper documenti,
			List<DocumentazioneRichiestaType> documentiRichiesti)
		throws ApsException 
	{
		boolean controlliOk = true;

		boolean docFound;

		for (int i = 0; i < documentiRichiesti.size(); i++) {
			docFound = false;
			if (!controlliOk) {
				break;
			}
			if (documentiRichiesti.get(i).isObbligatorio()) {
				for (int j = 0; j < documenti.getDocRichiesti().size(); j++) {
					Long docId = documenti.getDocRichiestiId().get(j);
					if (docId != 0 && docId == documentiRichiesti.get(i).getId()) {
						docFound = true;
						break;
					}
				}
				if (!docFound) {
					controlliOk = false;
					this.addActionError(this.getText("Errors.docRichiestoObbligatorioNotFound",
										new String[] { documentiRichiesti.get(i).getNome() }));
				}
			}
		}

		for (int j = 0; j < documenti.getDocRichiesti().size(); j++) {
			String nomefile = documenti.getDocRichiestiFileName().get(j);
			if (nomefile.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				controlliOk = false;
				this.addActionError(this.getText("Errors.docRichiestoOverflowFileNameLength", new String[] { nomefile }));
			}
		}
//		for (int j = 0; j < documenti.getDocUlteriori().size(); j++) {
//			String nomefile = documenti.getDocUlterioriFileName().get(j);
//			if (nomefile.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
//				controlliOk = false;
//				this.addActionError(this.getText("Errors.docUlterioreOverflowFileNameLength", new String[] { nomefile }));
//			}
//		}
		
		return controlliOk;
	}
	
}
