package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.dgue.DgueBuilder;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;

import java.util.List;


/**
 * Action di apertura delle pagine "busta di prequalifica", "busta amministrativa", "busta economica", "busta tecnica".
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageDocumentiBustaAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8730921987831149769L;
	
	/**
	 * Questa action serve ai componenti la RTI o subappaltanti
	 */
	private static final String DGUE_LINK_ACTION_RTI 	= "/do/FrontEnd/GareTel/dgueRti.action";
	private static final String DGUE_LINK_ACTION 		= "/do/FrontEnd/GareTel/dgue.action";
//	private final Logger logger = ApsSystemUtils.getLogger();		// esiste gia' in BaseAction ed e' accessibile
	
	protected static final String SUCCESS_BUSTATECNICA 	= "successBustaTecnica";
	protected static final String SUCCESS_QUESTIONARIO 	= "successQuestionario";
	
	private IBandiManager bandiManager;						// SERVE ANCORA ???
	private IComunicazioniManager comunicazioniManager;		// SERVE ANCORA ???
	private IAppParamManager appParamManager;
	private ConfigInterface configManager;

	@Validate(EParamValidation.CODICE)
	private String codice;				// codice lotto o codice gara
	@Validate(EParamValidation.CODICE)
	private String codiceGara;			// codice gara
	private int operazione;
	private Long docRichiestoId;
	@Validate(EParamValidation.GENERIC)
	private String docUlterioreDesc;
	private int tipoBusta;
	@Validate(EParamValidation.GENERIC)
	private String multipartSaveDir;
	private Long iddocdig;
	@Validate(EParamValidation.GENERIC)
	private String dgueLinkSecond;
	@Validate(EParamValidation.GENERIC)
	private String dgueLinkThird;

	@ValidationNotRequired
	private String url;

	private boolean esisteFileConFirmaNonVerificata = Boolean.FALSE;

	private int dimensioneAttualeFileCaricati;

	private List<DocumentazioneRichiestaType> documentiRichiesti;
	private List<DocumentazioneRichiestaType> documentiInseriti;
	private List<DocumentazioneRichiestaType> documentiMancanti;


	public String getUrl() {
		return url;
	}

	public String getDgueLinkSecond() {
		return dgueLinkSecond;
	}

	public String getDgueLinkThird() {
		return dgueLinkThird;
	}
	
	public Long getIddocdig() {
		return iddocdig;
	}
	
	public void setIddocdig(Long iddocdig) {
		this.iddocdig = iddocdig;
	}

	public boolean isEsisteFileConFirmaNonVerificata() {
		return esisteFileConFirmaNonVerificata;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getCodice() {
		return codice;
	}
	
	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
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
	
	/**
	 * Espone la costante per le pagine JSP 
	 * 		PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO
	 * 		PortGareSystemConstants.DOCUMENTO_FORMATO_PDF
	 *  	PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL
	 *  	PortGareSystemConstants.BUSTA_AMMINISTRATIVA 
	 *  	PortGareSystemConstants.BUSTA_TECNICA 
	 *  	PortGareSystemConstants.BUSTA_ECONOMICA 
	 *  	PortGareSystemConstants.BUSTA_PRE_QUALIFICA 
	 */ 
	public int getDOCUMENTO_FORMATO_FIRMATO() { return PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO; }	
	public int getDOCUMENTO_FORMATO_PDF() { return PortGareSystemConstants.DOCUMENTO_FORMATO_PDF; }
	public int getDOCUMENTO_FORMATO_EXCEL() { return PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL; }	
	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }	
	public int getBUSTA_TECNICA() { return PortGareSystemConstants.BUSTA_TECNICA; }	
	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }
	public int getBUSTA_PRE_QUALIFICA() { return PortGareSystemConstants.BUSTA_PRE_QUALIFICA; }
	
	/**
	 * Setta la pagina del wizard in base alla busta
	 */
	private void setPage() {
		switch (this.tipoBusta) {
			case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_PREQ);
				break;
			case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_AMM);
				break;
			case PortGareSystemConstants.BUSTA_TECNICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_TEC);
				break;
			case PortGareSystemConstants.BUSTA_ECONOMICA:
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DOC_BUSTA_ECO);
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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				String codGara = (StringUtils.isNotEmpty(this.codiceGara) ? this.codiceGara : this.codice);
				String cod = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
				
				// inizializza la gestione delle buste in base al codice del lotto
				GestioneBuste gestioneBuste = GestioneBuste.getFromSession(cod);
				if(gestioneBuste == null) {
					gestioneBuste = new GestioneBuste(
							this.getCurrentUser().getUsername(),
							codGara, 
							null,
							this.operazione);
					gestioneBuste.get();
				}
				
				DettaglioGaraType dettGara = gestioneBuste.getDettaglioGara();
				boolean noBustaAmministrativa = dettGara.getDatiGeneraliGara().isNoBustaAmministrativa();
				
				// recupera la busta dei documenti
				WizardPartecipazioneHelper partecipazione = gestioneBuste.getBustaPartecipazione().getHelper();
				RiepilogoBusteHelper riepilogo = gestioneBuste.getBustaRiepilogo().getHelper();
				BustaDocumenti busta = gestioneBuste.getBusta(this.tipoBusta);
				getUploadValidator().setHelper(busta);
				
				busta.get();
				
				WizardDocumentiBustaHelper documentiBustaHelper = busta.getHelperDocumenti();
				documentiBustaHelper.setCodice(cod);
				
				// OEPV: in caso di busta tecnica con 
				//	- gara telematica
				//  - gara con offerta telematica 
				//  - gara con modalita' aggiudicazione = 6
				//	- criteri di valutazione
				// va visualizzato il nuovo wizard per la gestione dell'offerta tecnica
				boolean apriOEPVBustaTecnica = busta.getOEPV();
				
				// se la gara non prevede la busta amministrativa 
				// non deve essere consentita l'aperture del wizard...
				if(noBustaAmministrativa && this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
					// busta amminstrativa per gara senza busta amministrativa
					ApsSystemUtils.getLogger().error("Apertura busta amministrativa per gara senza busta amministrativa");
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				} else {
					if(apriOEPVBustaTecnica) {
						// Gestione OEPV della busta tecnica con nuovo wizard
						this.setTarget("successBustaTecnica");
						
					} else {
						// Gestione standard della busta: solo upload documenti
						this.setPage();
						
						// QUESTIONARI 
						// verifica se esiste gia' la comunicazione con il questionario allegato
						if( SUCCESS.equals(this.getTarget()) ) {
							if(documentiBustaHelper.isGestioneQuestionario()) {
								// l'allegato della comunicazione esiste gia'
								// oppure va aggiunto alla comunicazione
								this.setTarget(SUCCESS_QUESTIONARIO);
							}
						}
					
						if(SUCCESS.equals(this.getTarget()) || 
						   SUCCESS_QUESTIONARIO.equals(this.getTarget())) 
						{ 
							// verifica ed integra la busta di riepilogo FS11R/FS10R...
							gestioneBuste.getBustaRiepilogo().verificaIntegraDocumenti(busta);
						
							this.setDocumentiMancanti(busta.getDocumentiMancantiDB());
							this.setDocumentiInseriti(busta.getDocumentiInseritiDB());
							this.setDocumentiRichiesti(busta.getDocumentiRichiestiDB());
							
							if(SUCCESS.equals(this.getTarget()) || 
							   SUCCESS_QUESTIONARIO.equals(this.getTarget())) 
							{
//								this.setHelper(documentiBustaHelper);
								
								if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
									if( !riepilogo.getPrimoAccessoPrequalificaEffettuato() ) {
										riepilogo.setPrimoAccessoPrequalificaEffettuato(true);
										gestioneBuste.getBustaRiepilogo().send(busta);
									}
								}
								if(this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
									if( !riepilogo.getPrimoAccessoAmministrativaEffettuato() ) {
										riepilogo.setPrimoAccessoAmministrativaEffettuato(true);
										gestioneBuste.getBustaRiepilogo().send(busta);
									}
								}
								
								if (partecipazione.isPlicoUnicoOfferteDistinte()) {
									// gara a lotti
									if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
										if(riepilogo.getPrimoAccessoEconomicheEffettuato().get(cod) != null) {
											riepilogo.getPrimoAccessoEconomicheEffettuato().put(cod, true);
										}
									}
									if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
										if(riepilogo.getPrimoAccessoTecnicheEffettuato().get(cod) != null) {
											riepilogo.getPrimoAccessoTecnicheEffettuato().put(cod, true);
										}
									}
								} else {
									// gara NO lotti
									if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
										if( !riepilogo.getPrimoAccessoEconomicaEffettuato() ) {
											riepilogo.setPrimoAccessoEconomicaEffettuato(true);
											gestioneBuste.getBustaRiepilogo().send(busta);
										}
									}
									if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
										if( !riepilogo.getPrimoAccessoTecnicaEffettuato() ) {
											riepilogo.setPrimoAccessoTecnicaEffettuato(true);
											gestioneBuste.getBustaRiepilogo().send(busta);
										}
									}
								}
							}
							
							// DGUE
							// se un documento richiesto appartiene al DGUE abilita la gestione
							// genera i link 2 e 3 per la gestione del DGUE
							DgueBuilder dgue = new DgueBuilder()
									.setCodiceGara(this.codiceGara)
									.setCodiceLotto(this.codice)
									.setDgueLinkActionRti(DGUE_LINK_ACTION_RTI)
									.setDgueLinkAction(DGUE_LINK_ACTION)
									.setGestioneBuste(gestioneBuste)
									.setTipoBusta(this.tipoBusta)
									.initLink23();
							if (dgue.getIddocdig() != null) {
								this.iddocdig = dgue.getIddocdig();
								this.dgueLinkSecond = dgue.getDgueLinkSecond();
								this.dgueLinkThird = dgue.getDgueLinkThird();
							} else {
								// solo in caso di QForm e in presenza di errori recupera recupera lo stato del DGUE
								if( !SUCCESS.equals(dgue.getTarget()) && documentiBustaHelper.isGestioneQuestionario() ) {
									this.setTarget(dgue.getTarget());
								}
							}
							
							// verifica se esiste un allegato firmato non verificato
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
	 * DGUE 
	 */
	public String redirectToDGUE() {
		String target = "redirectToDGUE";
		
		UserDetails ud = this.getCurrentUser();
		if (null != ud && !ud.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				String codGara = (StringUtils.isNotEmpty(this.codiceGara) ? this.codiceGara : this.codice);
				String cod = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
			
				// DGUE - esegui il redirect per il DGUE
				DgueBuilder dgue = new DgueBuilder()
						.setCodiceGara(this.codiceGara)
						.setCodiceLotto(this.codice)
						.setDgueLinkActionRti(DGUE_LINK_ACTION_RTI)
						.setDgueLinkAction(DGUE_LINK_ACTION)
						.setGestioneBuste(GestioneBuste.getFromSession(cod))
						.setIddocdig(this.iddocdig)
						.redirectToDGUE();
				this.url = dgue.getUrl();
				target = dgue.getTarget();
				
			} catch (Exception e) {
				logger.error("Errore nella cifratura dei dati nel jwt",e);
				ApsSystemUtils.logThrowable(e, this, "redirectToDGUE");
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		
		} else {
			logger.warn("Un utente non autorizzato ha cercato di attivare la funzione DGUE, probabile sessione scaduta. UserDetail: {}",ud);
			target = CommonSystemConstants.PORTAL_ERROR;
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
	
}
