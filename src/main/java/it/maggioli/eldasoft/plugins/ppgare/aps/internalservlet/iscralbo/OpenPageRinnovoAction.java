package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Action di apertura della pagina per la modifica dei documenti per il rinnovo
 * dell'iscrizione ad un elenco
 *
 * @author Marco.Perazzetta
 */
public class OpenPageRinnovoAction extends AbstractOpenPageAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8730931987831179769L;
	private final Logger logger = ApsSystemUtils.getLogger();

	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;

	/**
	 * Codice del bando per elenco operatori economici per il quale gestire
	 * un'iscrizione
	 */
	@Validate(EParamValidation.CODICE)
	private String codice;

	/**
	 * Tipologia di elenco per cui si sta richiedendo il rinnovo iscrizione
	 */
	private int tipoElenco;

	private int dimensioneAttualeFileCaricati;

	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private List<DocumentazioneRichiestaType> documentiInseriti;
	private List<DocumentazioneRichiestaType> documentiMancanti;

	private Long docRichiestoId;
	@Validate(EParamValidation.GENERIC)
	private String docUlterioreDesc;
	
	private boolean esisteFileConFirmaNonVerificata = Boolean.FALSE;


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

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
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

	public void setDocumentiInseriti(List<DocumentazioneRichiestaType> documentiInseriti) {
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

	public int getTipoElenco() {
		return tipoElenco;
	}

	public void setTipoElenco(int tipoElenco) {
		this.tipoElenco = tipoElenco;
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}

	public Integer getLimiteTotaleUploadDocIscrizione() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
	}

	public String getSTEP_IMPRESA() {
		return WizardRinnovoHelper.STEP_IMPRESA;
	}
	
	public String getSTEP_SCARICA_ISCRIZIONE() {
		return WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE;
	}
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO() {
		return WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO;
	}

	public String getSTEP_PRESENTA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE;
	}
	
	public int getTIPOLOGIA_ELENCO_CATALOGO() {
		return PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO;
	}

	public int getTIPOLOGIA_ELENCO_STANDARD() {
		return PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD;
	}

	public int getDOCUMENTO_FORMATO_FIRMATO() {
		return PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO;
	}

	public int getDOCUMENTO_FORMATO_PDF() {
		return PortGareSystemConstants.DOCUMENTO_FORMATO_PDF;
	}

	public int getDOCUMENTO_FORMATO_EXCEL() {
		return PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL;
	}

	/**
	 * Funzione di lettura dati dei documenti in base alla tipologia di busta
	 * selezionata.
	 *
	 * @return il target desiderato
	 */
	@Override
	public String openPage() {
		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}
		this.setTarget(SUCCESS);
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				this.tipoElenco = rinnovoHelper.getTipologia();
				/* --- CESSATI --- */
				WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(),
						this);
				
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, 
								 datiImpresaHelper);
				
				TipoPartecipazioneType tipoPartecipazione = this.bandiManager
					.getTipoPartecipazioneImpresa(
							this.getCurrentUser().getUsername(),
							rinnovoHelper.getIdBando(),
							null);
				
				rinnovoHelper.setCodice(rinnovoHelper.getIdBando());
				
				List<DocumentazioneRichiestaType> documentiRichiestiDB = this.bandiManager
					.getDocumentiRichiestiRinnovoIscrizione(
							rinnovoHelper.getCodice(), 
							datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
							tipoPartecipazione.isRti());

				// con l'elenco dei documentiRichiestiDB richiesti si creano 2
				// liste, una con gli elementi inseriti e una con quelli mancanti
				List<DocumentazioneRichiestaType> documentiMancantiDB = new ArrayList<DocumentazioneRichiestaType>();
				List<DocumentazioneRichiestaType> documentiInseritiDB = new ArrayList<DocumentazioneRichiestaType>();

				for (int i = 0; i < documentiRichiestiDB.size(); i++) {
					DocumentazioneRichiestaType elem = documentiRichiestiDB.get(i);

					if (Attachment.indexOf(rinnovoHelper.getDocumenti().getRequiredDocs(), Attachment::getId, elem.getId()) == -1) {
						// si clona l'elemento in quanto oggetto di modifiche
						DocumentazioneRichiestaType docDaInserire = new DocumentazioneRichiestaType();
						// si accorcia il nome in modo da non creare una
						// combobox troppo estesa
						docDaInserire.setNome(elem.getNome().substring(0,
										Math.min(40, elem.getNome().length())));
						docDaInserire.setId(elem.getId());
						docDaInserire.setIdfacsimile(elem.getIdfacsimile());
						docDaInserire.setObbligatorio(elem.isObbligatorio());
						documentiMancantiDB.add(docDaInserire);
					} else {
						documentiInseritiDB.add(elem);
					}
				}
				this.setDocumentiMancanti(documentiMancantiDB);
				this.setDocumentiInseriti(documentiInseritiDB);
				this.setDocumentiRichiesti(documentiRichiestiDB);

				// si aggiorna il totale dei file finora caricati
//				for (Integer s : rinnovoHelper.getDocumenti().getDocRichiestiSize()) {
//					this.dimensioneAttualeFileCaricati += s;
//				}
//				for (Integer s : rinnovoHelper.getDocumenti().getDocUlterioriSize()) {
//					this.dimensioneAttualeFileCaricati += s;
//				}
				WizardDocumentiHelper wizardDocumentiHelper = rinnovoHelper.getDocumenti();
				if (CollectionUtils.isNotEmpty(wizardDocumentiHelper.getRequiredDocs())) {
					dimensioneAttualeFileCaricati += Attachment.sumSize(wizardDocumentiHelper.getRequiredDocs());
					esisteFileConFirmaNonVerificata =
							wizardDocumentiHelper.getRequiredDocs()
									.stream()
									.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
					logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
				}
				if (CollectionUtils.isNotEmpty(wizardDocumentiHelper.getAdditionalDocs())) {
					dimensioneAttualeFileCaricati += Attachment.sumSize(wizardDocumentiHelper.getAdditionalDocs());
					esisteFileConFirmaNonVerificata =
							wizardDocumentiHelper.getAdditionalDocs()
									.stream()
									.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
					logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
				}
				// ...aggiorna l'helper dei documenti in sessione...
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
								 WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO);
				
			} catch (XmlException ex) {
				ApsSystemUtils.logThrowable(ex, this, "openPage");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	public String openPageClear() {
		String target = openPage();
		if (SUCCESS.equals(target)) {
			this.docRichiestoId = null;
			this.docUlterioreDesc = null;
		}
		return target;
	}
	
}
