package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class OpenPageDocumentiNuovaComunicazioneAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8461069422850390065L;
	
	protected IAppParamManager appParamManager;
	protected IEventManager eventManager;
	
	private File docUlteriore;
	private String docUlterioreContentType;
	private String docUlterioreFileName;
	private String docUlterioreDesc;

	protected int dimensioneAttualeFileCaricati;
	protected boolean deleteAllegato;
	protected String id;
	protected String idAllegato;
	protected InputStream inputStream;
	protected String oggetto;
	protected String testo;
	protected String from;

	public Map<String, Object> getSession() {
		return session;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
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
		this.docUlterioreDesc = docUlterioreDesc;
	}

	public int getDimensioneAttualeFileCaricati() {
		return dimensioneAttualeFileCaricati;
	}

	public void setDimensioneAttualeFileCaricati(int dimensioneAttualeFileCaricati) {
		this.dimensioneAttualeFileCaricati = dimensioneAttualeFileCaricati;
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

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	// "costanti" per la pagina jsp
	public String getSTEP_TESTO_COMUNICAZIONE() {
		return WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE;
	}

	public String getSTEP_DOCUMENTI() {
		return WizardNuovaComunicazioneHelper.STEP_DOCUMENTI;
	}

	public String getSTEP_INVIO_COMUNICAZIONE() {
		return WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE;
	}
	
	/**
	 * ...
	 */
	public Integer getLimiteTotaleUpload() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(this.appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE);
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_UPLOAD_FILE);
	}

//	// VIENE ANCORA UTILIZZATO ?
//	public Integer getLimiteTotaleUploadDocIscrizione() {
//		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
//	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);

		for(int i = 0; i < helper.getDocumenti().getDocUlterioriSize().size(); i++) {
			this.dimensioneAttualeFileCaricati += helper.getDocumenti().getDocUlterioriSize().get(i);
		}
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardNuovaComunicazioneHelper.STEP_DOCUMENTI);

		return SUCCESS;
	}

}
