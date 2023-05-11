package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * ... 
 */
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

	public void setDocumentiRichiesti(
			List<DocumentazioneRichiestaType> documentiRichiesti) {
		this.documentiRichiesti = documentiRichiesti;
	}

	// "costanti" per la pagina jsp
	@Override
	public String getSTEP_TESTO_COMUNICAZIONE() {
		return WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE;
	}

	@Override
	public String getSTEP_DOCUMENTI() {
		return WizardSoccorsoIstruttorioHelper.STEP_DOCUMENTI;
	}

	@Override
	public String getSTEP_INVIO_COMUNICAZIONE() {
		return WizardSoccorsoIstruttorioHelper.STEP_INVIO_COMUNICAZIONE;
	}
	
	/**
	 * @return the esisteFileConFirmaNonVerificata
	 */
	public boolean isEsisteFileConFirmaNonVerificata() {
		return esisteFileConFirmaNonVerificata;
	}

	/**
	 * ...
	 */
	@Override
	public String openPage() {
		WizardSoccorsoIstruttorioHelper helper = (WizardSoccorsoIstruttorioHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);

		int dimensioneAttualeFileCaricati = 0;
		if (helper != null) {
			DocumentiComunicazioneHelper documentiHelper = helper.getDocumenti();
			logger.debug("esisteFileConFirmaNonVerificata: {}",esisteFileConFirmaNonVerificata);
			if (CollectionUtils.isNotEmpty(documentiHelper.getRequiredDocs())) {
//				esisteFileConFirmaNonVerificata = documentiHelper
				dimensioneAttualeFileCaricati += Attachment.sumSize(documentiHelper.getRequiredDocs());
				esisteFileConFirmaNonVerificata =
						documentiHelper.getRequiredDocs()
								.stream()
								.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
			}
			logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
			if (CollectionUtils.isNotEmpty(documentiHelper.getAdditionalDocs())) {
				dimensioneAttualeFileCaricati += Attachment.sumSize(documentiHelper.getRequiredDocs());
				esisteFileConFirmaNonVerificata |=
						documentiHelper.getRequiredDocs()
								.stream()
								.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
				logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
			}
			// documentazione richiesta per tutte le buste
			this.documentiRichiesti = helper.getDocumentiRichiesti();
		}
		this.setDimensioneAttualeFileCaricati(dimensioneAttualeFileCaricati); 
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_DOCUMENTI);

		return SUCCESS;
	}

}
