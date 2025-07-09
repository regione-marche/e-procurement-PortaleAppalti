package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Action di gestione dell'apertura della pagina dei documentiRichiesti del
 * wizard d'iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageDocumentiAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -9152265474737463671L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;

	private int dimensioneAttualeFileCaricati;

	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private List<DocumentazioneRichiestaType> documentiInseriti;
	private List<DocumentazioneRichiestaType> documentiMancanti;

	private Long docRichiestoId;
	@Validate(EParamValidation.GENERIC)
	private String docUlterioreDesc;
	
	private boolean esisteFileConFirmaNonVerificata = Boolean.FALSE;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	/**
	 * @return the esisteFileConFirmaNonVerificata
	 */
	public boolean isEsisteFileConFirmaNonVerificata() {
		return esisteFileConFirmaNonVerificata;
	}

	public int getDimensioneAttualeFileCaricati() {
		return dimensioneAttualeFileCaricati;
	}

	public List<DocumentazioneRichiestaType> getDocumentiRichiesti() {
		return documentiRichiesti;
	}

	public void setDocumentiRichiesti(List<DocumentazioneRichiestaType> documentiRichiesti) {
		this.documentiRichiesti = documentiRichiesti;
	}

	public List<DocumentazioneRichiestaType> getDocumentiInseriti() {
		return documentiInseriti;
	}

	public void setDocumentiInseriti(
					List<DocumentazioneRichiestaType> documentiInseriti) {
		this.documentiInseriti = documentiInseriti;
	}

	public List<DocumentazioneRichiestaType> getDocumentiMancanti() {
		return documentiMancanti;
	}

	public void setDocumentiMancanti(List<DocumentazioneRichiestaType> documentiMancanti) {
		this.documentiMancanti = documentiMancanti;
	}

	public Long getDocRichiestoId() {
		return docRichiestoId;
	}

	public void setDocRichiestoId(Long docRichiestoId) {
		this.docRichiestoId = docRichiestoId;
	}

	public String getDocUlterioreDesc() {
		return docUlterioreDesc;
	}

	public void setDocUlterioreDesc(String docUlterioreDesc) {
		this.docUlterioreDesc = docUlterioreDesc;
	}

	/**
	 * espone le costanti alla JSP 
	 */
	public String getSTEP_IMPRESA() { return WizardIscrizioneHelper.STEP_IMPRESA; }
	public String getSTEP_DENOMINAZIONE_RTI() { return WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI; }
	public String getSTEP_DETTAGLI_RTI() { return WizardIscrizioneHelper.STEP_DETTAGLI_RTI; }
	public String getSTEP_SELEZIONE_CATEGORIE() { return WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE; }
	public String getSTEP_RIEPILOGO_CAGTEGORIE() { return WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE; }
	public String getSTEP_SCARICA_ISCRIZIONE() { return WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE; }
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA() { return WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA; }	
	public String getSTEP_PRESENTA_ISCRIZIONE() { return WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE; }
	public String getSTEP_QUESTIONARIO() { return WizardIscrizioneHelper.STEP_QUESTIONARIO; }
	public String getSTEP_RIEPILOGO_QUESTIONARIO() { return WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO; }
	public int getDOCUMENTO_FORMATO_FIRMATO() { return PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO; }
	public int getDOCUMENTO_FORMATO_PDF() { return PortGareSystemConstants.DOCUMENTO_FORMATO_PDF; }
	public int getDOCUMENTO_FORMATO_EXCEL() { return PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL; }

	
	/**
	 * ...
	 */
	public String openPage() {
		return openPageCommon();
	}
	
	/**
	 * ...
	 */
	public String openPageClear() {
		String target = openPageCommon();
		if (SUCCESS.equals(target)) {
			this.docRichiestoId = null;
			this.docUlterioreDesc = null;
		}
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
						 WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA);
		return target;
	}

	/**
	 * ...
	 */
	private String openPageCommon() {
		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}

		String target = SUCCESS;
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			getUploadValidator().setHelper(iscrizioneHelper);
			
			try {
				List<DocumentazioneRichiestaType> documentiRichiesti = iscrizioneHelper.getDocumentiRichiestiBO();

				// con l'elenco dei documentiRichiesti richiesti si creano 2
				// liste, una
				// con gli elementi inseriti e una con quelli mancanti
				WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();
				List<DocumentazioneRichiestaType> documentiMancanti = new ArrayList<DocumentazioneRichiestaType>();
				List<DocumentazioneRichiestaType> documentiInseriti = new ArrayList<DocumentazioneRichiestaType>();
				for (int i = 0; i < documentiRichiesti.size(); i++) {
					DocumentazioneRichiestaType elem = documentiRichiesti.get(i);

					if (Attachment.indexOf(documentiHelper.getRequiredDocs(), Attachment::getId, elem.getId()) == -1) {
						// si clona l'elemento in quanto oggetto di modifiche
						DocumentazioneRichiestaType docDaInserire = new DocumentazioneRichiestaType();
						// si accorcia il nome in modo da non creare una
						// combobox troppo estesa
						docDaInserire.setNome(elem.getNome().substring(0, Math.min(40, elem.getNome().length())));
						docDaInserire.setId(elem.getId());
						docDaInserire.setIdfacsimile(elem.getIdfacsimile());
						docDaInserire.setObbligatorio(elem.isObbligatorio());
						documentiMancanti.add(docDaInserire);
					} else {
						documentiInseriti.add(elem);
					}
				}
				this.setDocumentiMancanti(documentiMancanti);
				this.setDocumentiInseriti(documentiInseriti);
				this.setDocumentiRichiesti(documentiRichiesti);

				// si aggiorna il totale dei file finora caricati
//				for (Integer s : documentiHelper.getDocRichiestiSize()) {
//					this.dimensioneAttualeFileCaricati += s;
//				}
//				for (Integer s : documentiHelper.getDocUlterioriSize()) {
//					this.dimensioneAttualeFileCaricati += s;
//				}

				dimensioneAttualeFileCaricati += documentiHelper.getTotalSize();
				if (CollectionUtils.isNotEmpty(documentiHelper.getRequiredDocs())) {
					esisteFileConFirmaNonVerificata =
							documentiHelper.getRequiredDocs()
									.stream()
									.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
					logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
				}
				if (CollectionUtils.isNotEmpty(documentiHelper.getAdditionalDocs())) {
					esisteFileConFirmaNonVerificata =
							documentiHelper.getAdditionalDocs()
									.stream()
									.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
					logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
							 WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA);
		}
		return target;
	}
	
}
