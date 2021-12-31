package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Action di apertura delle pagine "busta amministrativa", "busta economica",
 * "busta tecninca".
 *
 * @author Marco.Perazzetta
 */
public class OpenPageDocumentiBustaAction extends AbstractOpenPageAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8730921987831149769L;

	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;

	/**
	 * Codice del bando per elenco operatori economici per il quale gestire un'iscrizione
	 */
	private String codice;
	private String codiceGara;
	private int operazione;

	private int dimensioneAttualeFileCaricati;

	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private List<DocumentazioneRichiestaType> documentiInseriti;
	private List<DocumentazioneRichiestaType> documentiMancanti;

	private Long docRichiestoId;
	private String docUlterioreDesc;
	private int tipoBusta;

	/**
	 * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
	 */
	private String multipartSaveDir;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
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

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}

	public Integer getLimiteTotaleUploadDocBusta() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
	}
	
	
	/**
	 * Espone la costante 
	 * 		PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO
	 * 		PortGareSystemConstants.DOCUMENTO_FORMATO_PDF
	 *  	PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL
	 *  	PortGareSystemConstants.BUSTA_AMMINISTRATIVA 
	 *  	PortGareSystemConstants.BUSTA_TECNICA 
	 *  	PortGareSystemConstants.BUSTA_ECONOMICA 
	 *  	PortGareSystemConstants.BUSTA_PRE_QUALIFICA 
	 * alle pagine JSP  
	 */ 
	public int getDOCUMENTO_FORMATO_FIRMATO() {
		return PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO;
	}
	
	public int getDOCUMENTO_FORMATO_PDF() {
		return PortGareSystemConstants.DOCUMENTO_FORMATO_PDF;
	}

	public int getDOCUMENTO_FORMATO_EXCEL() {
		return PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL;
	}
	
	public int getBUSTA_AMMINISTRATIVA() {
		return PortGareSystemConstants.BUSTA_AMMINISTRATIVA;
	}
	
	public int getBUSTA_TECNICA() {
		return PortGareSystemConstants.BUSTA_TECNICA;
	}
	
	public int getBUSTA_ECONOMICA() {
		return PortGareSystemConstants.BUSTA_ECONOMICA;
	}

	public int getBUSTA_PRE_QUALIFICA() {
		return PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
	}

	
	/**
	 * Setta la pagina del wizard in base alla busta
	 */
	private void setPage() {
		switch (this.tipoBusta) {
			case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_AMM);
				break;
			case PortGareSystemConstants.BUSTA_TECNICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_TEC);
				break;
			case PortGareSystemConstants.BUSTA_ECONOMICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_ECO);
				break;
			case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_PREQ);
				break;
			default:
				break;
		}
	}

	/**
	 * Setta la pagina del wizard in base alla busta
	 */
	private void setHelper(WizardDocumentiBustaHelper documentiBustaHelper) {
		switch (this.tipoBusta) {
			case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA, documentiBustaHelper);
				break;
			case PortGareSystemConstants.BUSTA_TECNICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA, documentiBustaHelper);
				break;
			case PortGareSystemConstants.BUSTA_ECONOMICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA, documentiBustaHelper);
				break;
			case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA, documentiBustaHelper);
				break;
			default:
				break;
		}
	}

	/**
	 * Funzione di lettura dati dei documenti in base alla tipologia di busta
	 * selezionata.
	 *
	 * @return il target desiderato
	 */
	@Override
	public String openPage() {
		ApsSystemUtils.getLogger().debug(
				"Classe: " + this.getClass().getName() + " - Metodo: openPage \n" +  
				"Messaggio: inizio metodo" + 
				"\ncodiceGara=" + this.codiceGara + ", codice=" + this.codice + ", tipoBusta=" + this.tipoBusta);
		
		// si carica un eventuale errore parcheggiato in sessione, ad esempio 
		// in caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}
		
		this.setTarget(SUCCESS);

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				String codice = this.codiceGara;
				if(StringUtils.isEmpty(this.codiceGara)) {
					codice = this.codice;
				}
				
				WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);							
				WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper)this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				bustaRiepilogativa.setModified(false);

				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(codice);
				
				String RICHIESTA_TIPO_BUSTA_RIEPILOGO = (PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA.equals(this.tipoBusta)
						? PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO
						: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO);
				
				// OEPV: in caso di busta tecnica con 
				//	- gara telematica
				//  - gara con offerta telematica 
				//  - gara con modalita' aggiudicazione = 6  				
				//	- criteri di valutazione
				// va visualizzato il nuovo wizard per la gestione dell'offerta tecnica
				boolean apriOEPVBustaTecnica = false;
				if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA ||
				   tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
					
					// scegli il tipo di criteri da verificare in base al tipo di busta
					int tipoCriteri = -1;
					if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
						tipoCriteri = PortGareSystemConstants.CRITERIO_TECNICO;
					} else if(tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
						tipoCriteri = PortGareSystemConstants.CRITERIO_ECONOMICO;
					}
				
					if(dettGara.getListaCriteriValutazione() != null) {
						List<CriterioValutazioneOffertaType> criteri =
							Arrays.asList(dettGara.getListaCriteriValutazione());						
						apriOEPVBustaTecnica = WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(									
										tipoCriteri, 
										dettGara.getDatiGeneraliGara(),
										criteri);							
					}
					
					// verifico se nel lotto da visualizzare ci sono criteri di valutazione...
					if(dettGara != null && 
					   dettGara.getLotto() != null && 
					   dettGara.getLotto().length > 0) {
						for (int i = 0; i < dettGara.getLotto().length; i++) {
							if(dettGara.getLotto()[i].getCodiceLotto().equals(this.codice)) {
								if(dettGara.getLotto()[i].getListaCriteriValutazione() != null) {
									List<CriterioValutazioneOffertaType> criteri =
										Arrays.asList(dettGara.getLotto()[i].getListaCriteriValutazione());						
									apriOEPVBustaTecnica = WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(									
													tipoCriteri, 
													dettGara.getDatiGeneraliGara(),
													criteri);
								}
								break;  
							}
						}
					}
				}
		
				if(apriOEPVBustaTecnica) {
					// Gestione OEPV della busta tecnica con nuovo wizard
					this.setTarget("successBustaTecnica");
					
				} else {
					// Gestione standard della busta: solo upload documenti
					this.setPage();
				
					WizardDocumentiBustaHelper documentiBustaHelper = null;
					if(!partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
						documentiBustaHelper = WizardDocumentiBustaHelper.getInstance(
								this.session, 
								this.codiceGara,
								this.codice, 
								this.tipoBusta, 
								this.operazione,
								partecipazioneHelper.getProgressivoOfferta());
					} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {  //&& partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
						documentiBustaHelper = WizardDocumentiBustaHelper.getInstance(
								this.session, 
								this.codiceGara,
								codice, //this.codiceGara, 
								this.tipoBusta, 
								this.operazione,
								partecipazioneHelper.getProgressivoOfferta());
					} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA && partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
						documentiBustaHelper = WizardDocumentiBustaHelper.getInstance(
								this.session, 
								this.codiceGara,
								this.codiceGara,
								this.tipoBusta, 
								this.operazione,
								partecipazioneHelper.getProgressivoOfferta());
					} else {
						documentiBustaHelper = WizardDocumentiBustaHelper.getInstance(
								this.session,
								this.codiceGara,
								this.codice, 
								this.tipoBusta, 
								this.operazione,
								partecipazioneHelper.getProgressivoOfferta());
						
						if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
							if(!bustaRiepilogativa.getPrimoAccessoTecnicheEffettuato().get(this.codice)) {
								bustaRiepilogativa.getPrimoAccessoTecnicheEffettuato().put(this.codice, true);
							}
						} else {
							if(!bustaRiepilogativa.getPrimoAccessoEconomicheEffettuato().get(this.codice)) {
								bustaRiepilogativa.getPrimoAccessoEconomicheEffettuato().put(this.codice, true);
							}
						}
						
						// aggiornamento FS11R /FS10R per marcare primo accesso 
						// a documentazione lotto
						bustaRiepilogativa.setModified(true);
					}
					
					// inizializza i documenti dell'helper documenti...
					if (!documentiBustaHelper.isAlreadyLoaded()) {
						try {
							// NB: spostato in WizardDocumentiBustaHelper.getInstance() !!!
							//if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
							//	documentiBustaHelper.setChiaveCifratura(this.bandiManager.getChiavePubblica(this.codiceGara, documentiBustaHelper.getTipoComunicazioneBusta()));
							//} else {
							//	documentiBustaHelper.setChiaveCifratura(this.bandiManager.getChiavePubblica(this.codice, documentiBustaHelper.getTipoComunicazioneBusta()));
							//}
	
							documentiBustaHelper.setDatiDocumenti(
									this.comunicazioniManager,
									this.getCurrentUser().getUsername(), 
									dettGara, 
									datiImpresaHelper,
									this.getTempDir().getAbsolutePath());
							
						} catch (Throwable ex) {
							if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
								this.addActionError(this.getText("Errors.invioBuste.xmlBustaNotFound", 
																 new String[] {documentiBustaHelper.getNomeBusta()}));
								this.setTarget(CommonSystemConstants.PORTAL_ERROR);
							}
						}
					}
					
					if( SUCCESS.equals(this.getTarget()) ) { 
						// verifica ed integra la busta di riepilogo FS10R/FS10R...
						bustaRiepilogativa.verificaIntegraDatiDocumenti(documentiBustaHelper);
						
						// aggiorna FS11R /FS10R se necessario... 
						if(bustaRiepilogativa.isModified()) {
							bustaRiepilogativa.sendComunicazioneBusta(
									CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
									datiImpresaHelper, 
									this.getCurrentUser().getUsername(),  
									codice,		// si usa "codice" (variabile locale) e non "this.codice"
									datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(), 
									this.comunicazioniManager,
									RICHIESTA_TIPO_BUSTA_RIEPILOGO,
									this);
						}
		
						
						String codiceLotto = null;
						// nella gara a lotto unico i documenti stanno nel lotto, 
						// nella gara ad offerta unica stanno nella gara
						if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
							codiceLotto = dettGara.getDatiGeneraliGara().getCodice();
						}
						
						List<DocumentazioneRichiestaType> tuttiAllegatiRichiesti = new ArrayList<DocumentazioneRichiestaType>();
						// documentazione richiesta per tutte le buste
						List<DocumentazioneRichiestaType> documentiRichiestiDB = this.bandiManager
								.getDocumentiRichiestiBandoGara(
										codice,
										codiceLotto,
										datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
										partecipazioneHelper.isRti(), 
										this.tipoBusta + "");
						tuttiAllegatiRichiesti.addAll(documentiRichiestiDB);
						
						if (partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
							// cerco i documenti specifici del lotto
							List<DocumentazioneRichiestaType> documentiRichiestiDBLotto = null;
							if(StringUtils.stripToNull(this.codice) != null){
								documentiRichiestiDBLotto = this.bandiManager
										.getDocumentiRichiestiBandoGara(
												codice,
												this.codice,
												datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
												partecipazioneHelper.isRti(), 
												this.tipoBusta + "");
								for (DocumentazioneRichiestaType docLotto : documentiRichiestiDBLotto) {
									tuttiAllegatiRichiesti.add(docLotto); //unsupportedOperationException
								}
							}
						}
						
						// Documenti specifici unici per quel lotto
						// con l'elenco dei documentiRichiestiDB richiesti si creano 2
						// liste, una con gli elementi inseriti e una con quelli mancanti
						List<DocumentazioneRichiestaType> documentiMancantiDB = new ArrayList<DocumentazioneRichiestaType>();
						List<DocumentazioneRichiestaType> documentiInseritiDB = new ArrayList<DocumentazioneRichiestaType>();
		
						// --- DOCUMENTI COMUNI A TUTTI I LOTTI + SPECIFICI 
						// NEL CASO DI PLICO UNICO OFFERTE DISTINTE
						for (int i = 0; i < tuttiAllegatiRichiesti.size(); i++) {
							DocumentazioneRichiestaType elem = tuttiAllegatiRichiesti.get(i);
							if (!documentiBustaHelper.getDocRichiestiId().contains(elem.getId())) {
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
						this.setDocumentiRichiesti(tuttiAllegatiRichiesti);
		
						// si aggiorna il totale dei file finora caricati
						for (Integer s : documentiBustaHelper.getDocRichiestiSize()) {
							this.dimensioneAttualeFileCaricati += s;
						}
						for (Integer s : documentiBustaHelper.getDocUlterioriSize()) {
							this.dimensioneAttualeFileCaricati += s;
						}
						if (SUCCESS.equals(this.getTarget())) {
							this.setHelper(documentiBustaHelper);
						}
					}
				}
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
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
		
		ApsSystemUtils.getLogger().debug(
				"Classe: " + this.getClass().getName() + " - Metodo: openPage \n" +  
				"Messaggio: fine metodo");
		
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

	/**
	 * @param multipartSaveDir the multipartSaveDir to set
	 */
	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	/**
	 * Ritorna la directory per i file temporanei, prendendola da
	 * struts.multipart.saveDir (struts.properties) se valorizzata correttamente,
	 * altrimenti da javax.servlet.context.tempdir
	 *
	 * @return path alla directory per i file temporanei
	 */
	private File getTempDir() {
		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), this.multipartSaveDir);
	}
	
}
