package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.io.InputStream;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE, EFlussiAccessiDistinti.COMUNICAZIONE_STIPULA })
public class OpenPageDocumentiNuovaComunicazioneAction extends AbstractOpenPageComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8461069422850390065L;
	
	protected IAppParamManager appParamManager;
	protected IEventManager eventManager;
	
	private File docUlteriore;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docUlterioreContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docUlterioreFileName;
	@Validate(EParamValidation.GENERIC)
	private String docUlterioreDesc;

	protected int dimensioneAttualeFileCaricati;
	protected boolean deleteAllegato;
	@Validate(EParamValidation.DIGIT)
	protected String id;
	@Validate(EParamValidation.DIGIT)
	protected String idAllegato;
	protected InputStream inputStream;
	@Validate(EParamValidation.OGGETTO_COMUNICAZIONE)
	protected String oggetto;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	protected String testo;
	@Validate(EParamValidation.ACTION)
	protected String from;
	
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


	/**
	 * costruttore
	 */
	public OpenPageDocumentiNuovaComunicazioneAction() {
		this(new WizardNuovaComunicazioneHelper());
	}
	
	public OpenPageDocumentiNuovaComunicazioneAction(WizardNuovaComunicazioneHelper helper) {
		super(helper, PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		helper = (WizardNuovaComunicazioneHelper) getWizardFromSession();
		
		getUploadValidator()
			.setHelper(helper.getDocumenti())
			.setLimiteUploadFile( FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_UPLOAD_FILE) )
			.setLimiteTotaleUploadFile( FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE) );

		from = (String)session.get(ComunicazioniConstants.SESSION_ID_FROM);

		dimensioneAttualeFileCaricati = helper.getDocumenti().getTotalSize();
		
		session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_DOCUMENTI);

		return SUCCESS;
	}

}
