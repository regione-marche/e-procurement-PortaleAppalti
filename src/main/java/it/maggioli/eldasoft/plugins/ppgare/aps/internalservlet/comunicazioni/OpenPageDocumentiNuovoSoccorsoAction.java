package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class OpenPageDocumentiNuovoSoccorsoAction extends OpenPageDocumentiNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5812345614463282879L;
	
	private File docRichiesto;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String docRichiestoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docRichiestoFileName;
	@Validate(EParamValidation.DIGIT)
	private String docRichiestoId;
	private Integer formato;
	
	private List<DocumentazioneRichiestaType> documentiRichiesti;
	
	private boolean esisteFileConFirmaNonVerificata = Boolean.FALSE;


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
	
	public String getDocRichiestoId() {
		return docRichiestoId;
	}

	public void setDocRichiestoId(String docRichiestoId) {
		this.docRichiestoId = docRichiestoId;
	}
	
	public Integer getFormato() {
		return formato;
	}

	public void setFormato(Integer formato) {
		this.formato = formato;
	}

	public List<DocumentazioneRichiestaType> getDocumentiRichiesti() {
		return documentiRichiesti;
	}

	public void setDocumentiRichiesti(List<DocumentazioneRichiestaType> documentiRichiesti) {
		this.documentiRichiesti = documentiRichiesti;
	}

	public boolean isEsisteFileConFirmaNonVerificata() {
		return esisteFileConFirmaNonVerificata;
	}

	/**
	 * costruttore
	 */
	public OpenPageDocumentiNuovoSoccorsoAction() {
		super(new WizardSoccorsoIstruttorioHelper());
	}

	/**
	 * ...
	 */
	@Override
	public String openPage() {
		helper = (WizardSoccorsoIstruttorioHelper) getWizardFromSession();

		dimensioneAttualeFileCaricati = 0;
		if (helper != null) {
			getUploadValidator()
				.setHelper(helper.getDocumenti())
				.setLimiteUploadFile( FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_UPLOAD_FILE) )
				.setLimiteTotaleUploadFile( FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager, AppParamManager.COMUNICAZIONI_LIMITE_TOTALE_UPLOAD_FILE) );

			DocumentiComunicazioneHelper documentiHelper = helper.getDocumenti();
			
			dimensioneAttualeFileCaricati += documentiHelper.getTotalSize();
			logger.debug("esisteFileConFirmaNonVerificata: {}",esisteFileConFirmaNonVerificata);
			if (CollectionUtils.isNotEmpty(documentiHelper.getRequiredDocs())) {
				esisteFileConFirmaNonVerificata =
						documentiHelper.getRequiredDocs()
								.stream()
								.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
			}
			
			logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
			if (CollectionUtils.isNotEmpty(documentiHelper.getAdditionalDocs())) {
				esisteFileConFirmaNonVerificata |=
						documentiHelper.getAdditionalDocs()
								.stream()
								.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
				logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
			}
			
			// documentazione richiesta per tutte le buste
			this.documentiRichiesti = helper.getDocumentiRichiesti();
		}
		this.setDimensioneAttualeFileCaricati(dimensioneAttualeFileCaricati); 
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_DOCUMENTI);

		return SUCCESS;
	}

}
