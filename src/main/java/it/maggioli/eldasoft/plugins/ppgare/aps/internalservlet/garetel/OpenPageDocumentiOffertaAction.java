package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.List;


@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageDocumentiOffertaAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8419705084593383421L;

	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	
	private int dimensioneAttualeFileCaricati;
	private Long docRichiestoId;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String docUlterioreDesc;
	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private List<DocumentazioneRichiestaType> documentiInseriti;
	private List<DocumentazioneRichiestaType> documentiMancanti;
	private boolean docOffertaPresente;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;

	private boolean esisteFileConFirmaNonVerificata = Boolean.FALSE;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	/**
	 * @return the esisteFileConFirmaNonVerificata
	 */
	public boolean isEsisteFileConFirmaNonVerificata() {
		return esisteFileConFirmaNonVerificata;
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

//	public Integer getLimiteUploadFile() {
//		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
//	}
//
//	public Integer getLimiteTotaleUploadDocBusta() {
//		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
//	}

	public boolean isDocOffertaPresente(){
		return docOffertaPresente;
	}

	public void setDocOffertaPresente(boolean docOffertaPresente){
		this.docOffertaPresente = docOffertaPresente;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	/**
	 * Espone le costanti alle pagine JSP  
	 */
	public String getSTEP_PREZZI_UNITARI() { return WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI; }
	public String getSTEP_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_OFFERTA; }
	public String getSTEP_SCARICA_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA; }
	public String getSTEP_DOCUMENTI() { return WizardOffertaEconomicaHelper.STEP_DOCUMENTI; }
	public int getDOCUMENTO_FORMATO_FIRMATO() { return PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO; }
	public int getDOCUMENTO_FORMATO_PDF() {	return PortGareSystemConstants.DOCUMENTO_FORMATO_PDF; }
	public int getDOCUMENTO_FORMATO_EXCEL() { return PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL; }
	public String getDESCRIZIONE_DOCUMENTO_OFFERTA_ECONOMICA() { return PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_ECONOMICA; }

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		GestioneBuste buste = GestioneBuste.getFromSession();
		BustaEconomica bustaEco = buste.getBustaEconomica();

		WizardOffertaEconomicaHelper helper = buste.getBustaEconomica().getHelper();
		WizardPartecipazioneHelper wizardPartecipazione = buste.getBustaPartecipazione().getHelper();
		WizardDatiImpresaHelper datiImpresaHelper = buste.getImpresa();
		WizardDocumentiBustaHelper documentiBustaHelper = helper.getDocumenti();
		BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
		RiepilogoBusteHelper bustaRiepilogativa = bustaRiepilogo.getHelper();
		getUploadValidator().setHelper(bustaEco);
		
		this.codice = (StringUtils.isEmpty(this.codice) ? (String)this.session.get("codice") : this.codice);
		this.codiceGara = (StringUtils.isEmpty(this.codiceGara) ? (String)this.session.get("codiceGara") : this.codiceGara);

		try {
			// nella gara a lotto unico i documenti stanno nel lotto,
			// nella gara ad offerta unica stanno nella gara
			String codiceLotto = null;
			if (helper.getGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
				codiceLotto = helper.getGara().getCodice();
			}
			List<DocumentazioneRichiestaType> documentiRichiestiDB = this.bandiManager
					.getDocumentiRichiestiBandoGara(
							helper.getGara().getCodice(),
							codiceLotto,
							datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(),
							wizardPartecipazione.isRti(),
							String.valueOf(PortGareSystemConstants.BUSTA_ECONOMICA));

			List<DocumentazioneRichiestaType> tuttiAllegatiRichiesti = new ArrayList<DocumentazioneRichiestaType>();
			tuttiAllegatiRichiesti.addAll(documentiRichiestiDB);
			
			if (wizardPartecipazione.isPlicoUnicoOfferteDistinte()) {
				
				if(!bustaRiepilogativa.getPrimoAccessoEconomicheEffettuato().get(helper.getCodice())) {
					bustaRiepilogativa.getPrimoAccessoEconomicheEffettuato().put(helper.getCodice(), true);
					// aggiornamento FS11R per marcare primo accesso a documentazione lotto
					// ed invia comunicazione FS11R
					bustaRiepilogo.send(bustaEco);
				}
				
				// cerco i documenti specifici del lotto
				List<DocumentazioneRichiestaType> documentiRichiestiDBLotto = null;
				documentiRichiestiDBLotto = this.bandiManager
						.getDocumentiRichiestiBandoGara(
								helper.getGara().getCodice(),
								helper.getCodice(),
								datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
								wizardPartecipazione.isRti(), 
								PortGareSystemConstants.BUSTA_ECONOMICA + "");
				for (DocumentazioneRichiestaType docLotto : documentiRichiestiDBLotto) {
					tuttiAllegatiRichiesti.add(docLotto); //unsupportedOperationException
				}
			} else {
				// aggiornamento FS11R per marcare primo accesso a documentazione lotto
				// ed invia comunicazione FS11R
				bustaRiepilogativa.setPrimoAccessoEconomicaEffettuato(true);
				bustaRiepilogo.send(bustaEco);
			}
			
			// con l'elenco dei documentiRichiestiDB richiesti si creano 2
			// liste, una con gli elementi inseriti e una con quelli mancanti
			List<DocumentazioneRichiestaType> documentiMancantiDB = new ArrayList<DocumentazioneRichiestaType>();
			List<DocumentazioneRichiestaType> documentiInseritiDB = new ArrayList<DocumentazioneRichiestaType>();

			for (int i = 0; i < tuttiAllegatiRichiesti.size(); i++) {
				DocumentazioneRichiestaType elem = tuttiAllegatiRichiesti.get(i);
				if (Attachment.indexOf(documentiBustaHelper.getRequiredDocs(), Attachment::getId, elem.getId()) == -1) {
					// si clona l'elemento in quanto oggetto di modifiche
					DocumentazioneRichiestaType docDaInserire = new DocumentazioneRichiestaType();
					// si accorcia il nome in modo da non creare una
					// combobox troppo estesa
					docDaInserire.setNome(elem.getNome().substring(0, Math.min(40, elem.getNome().length())));
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
			this.setDocumentiRichiesti(tuttiAllegatiRichiesti);
			this.setDocOffertaPresente(documentiBustaHelper.isDocOffertaPresente());
			// si aggiorna il totale dei file finora caricati
//			for (Integer s : documentiBustaHelper.getDocRichiestiSize()) {
//				this.dimensioneAttualeFileCaricati += s;
//			}
//			for (Integer s : documentiBustaHelper.getDocUlterioriSize()) {
//				this.dimensioneAttualeFileCaricati += s;
//			}

			dimensioneAttualeFileCaricati += documentiBustaHelper.getTotalSize();
			if (CollectionUtils.isNotEmpty(documentiBustaHelper.getRequiredDocs())) {
				esisteFileConFirmaNonVerificata =
						documentiBustaHelper.getRequiredDocs()
								.stream()
								.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
				logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
			}
			if (CollectionUtils.isNotEmpty(documentiBustaHelper.getAdditionalDocs())) {
				esisteFileConFirmaNonVerificata =
						documentiBustaHelper.getAdditionalDocs()
								.stream()
								.anyMatch(attachment -> attachment.getFirmaBean() != null && !attachment.getFirmaBean().getFirmacheck());
				logger.debug("esisteFileConFirmaNonVerificata: {}", esisteFileConFirmaNonVerificata);
			}

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 WizardOffertaEconomicaHelper.STEP_DOCUMENTI);

		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String openPageClear() {
		String target = openPage();
		if (SUCCESS.equals(target)) {
			this.docRichiestoId = null;
			this.docUlterioreDesc = null;
		}
		return target;
	}

}
