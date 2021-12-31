package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.Allegato;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ZipUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Action per la pagina di riepilogo dell'offerta
 *
 * @version 1.0
 * @author Marco.Perazzetta
 *
 */
public class RiepilogoOffertaAction extends EncodedDataAction implements SessionAware, IDownloadAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8793287506907078618L;

	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;
	private INtpManager ntpManager;
	private IAppParamManager appParamManager;

	private Map<String, Object> session;
	
	private String fromListaOfferte;	// "1" = true altrimenti false
	
	private String codice;
	private int operazione;
	private String progressivoOfferta;
	private boolean offertaTecnica;
	private boolean rti;
	private String denominazioneRti;
	private DettaglioGaraType dettGara;
	private WizardDatiImpresaHelper datiImpresa;
	private Long idBustaPreq;
	private Long idBustaAmm;
	private Long idBustaTec;
	private Long idBustaEco;
	private int idBusta;
	private int tipoBusta;
	private InputStream inputStream;
	private String filename;
	private String contentType;
	private boolean abilitaRettifica;
	private boolean bustaAmministrativaCifrata;
	private boolean bustaTecnicaCifrata;
	private boolean bustaEconomicaCifrata;
	private boolean bustaPrequalificaCifrata;
	private RiepilogoBusteHelper bustaRiepilogativa;

	
	/**
	 * Riferimento alla url della pagina per visualizzare eventuali problemi emersi.
	 */
	private String urlPage;

	/**
	 * Riferimento al frame della pagina in cui visualizzare il contenuto della risposta.
	 */
	private String currentFrame;

	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return this.comunicazioniManager;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public IBandiManager getBandiManager() {
		return this.bandiManager;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}
	
	public INtpManager getNtpManager() {
		return this.ntpManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public IAppParamManager getAppParamManager(){
		return this.appParamManager;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map<String, Object> getSession() {
		return this.session;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public boolean isOffertaTecnica() {
		return offertaTecnica;
	}

	public void setOffertaTecnica(boolean offertaTecnica) {
		this.offertaTecnica = offertaTecnica;
	}

	public boolean isRti() {
		return rti;
	}

	public void setRti(boolean rti) {
		this.rti = rti;
	}

	public String getDenominazioneRti() {
		return denominazioneRti;
	}

	public void setDenominazioneRti(String denominazioneRti) {
		this.denominazioneRti = denominazioneRti;
	}

	public DettaglioGaraType getDettGara() {
		return dettGara;
	}

	public void setDettGara(DettaglioGaraType dettGara) {
		this.dettGara = dettGara;
	}

	public WizardDatiImpresaHelper getDatiImpresa() {
		return datiImpresa;
	}
	
	public void setDatiImpresa(WizardDatiImpresaHelper datiImpresa) {
		this.datiImpresa = datiImpresa;
	}

	public Long getIdBustaPreq() {
		return idBustaPreq;
	}

	public void setIdBustaPreq(Long idBustaPreq) {
		this.idBustaPreq = idBustaPreq;
	}

	public Long getIdBustaAmm() {
		return idBustaAmm;
	}

	public void setIdBustaAmm(Long idBustaAmm) {
		this.idBustaAmm = idBustaAmm;
	}

	public Long getIdBustaTec() {
		return idBustaTec;
	}

	public void setIdBustaTec(Long idBustaTec) {
		this.idBustaTec = idBustaTec;
	}

	public Long getIdBustaEco() {
		return idBustaEco;
	}

	public void setIdBustaEco(Long idBustaEco) {
		this.idBustaEco = idBustaEco;
	}

	public int getIdBusta() {
		return idBusta;
	}

	public void setIdBusta(int idBusta) {
		this.idBusta = idBusta;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isAbilitaRettifica() {
		return abilitaRettifica;
	}

	public void setAbilitaRettifica(boolean abilitaRettifica) {
		this.abilitaRettifica = abilitaRettifica;
	}

	@Override
	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}

	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			"?last=1&actionPath=/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferta.action" +
			"&currentFrame=" + this.currentFrame + 
			"&codice=" + this.codice + 
			"&operazione=" + this.operazione;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public boolean isBustaAmministrativaCifrata() {
		return bustaAmministrativaCifrata;
	}

	public void setBustaAmministrativaCifrata(boolean bustaAmministrativaCifrata) {
		this.bustaAmministrativaCifrata = bustaAmministrativaCifrata;
	}

	public boolean isBustaTecnicaCifrata() {
		return bustaTecnicaCifrata;
	}

	public void setBustaTecnicaCifrata(boolean bustaTecnicaCifrata) {
		this.bustaTecnicaCifrata = bustaTecnicaCifrata;
	}

	public boolean isBustaEconomicaCifrata() {
		return bustaEconomicaCifrata;
	}

	public void setBustaEconomicaCifrata(boolean bustaEconomicaCifrata) {
		this.bustaEconomicaCifrata = bustaEconomicaCifrata;
	}

	public RiepilogoBusteHelper getBustaRiepilogativa() {
		return bustaRiepilogativa;
	}

	public void setBustaRiepilogativa(RiepilogoBusteHelper bustaRiepilogativa) {
		this.bustaRiepilogativa = bustaRiepilogativa;
	}

	public boolean isBustaPrequalificaCifrata() {
		return bustaPrequalificaCifrata;
	}

	public void setBustaPrequalificaCifrata(boolean bustaPrequalificaCifrata) {
		this.bustaPrequalificaCifrata = bustaPrequalificaCifrata;
	}

	public String getFromListaOfferte() {
		return fromListaOfferte;
	}

	public void setFromListaOfferte(String fromListaOfferte) {
		this.fromListaOfferte = fromListaOfferte;
	}

	/**
	 * Esponi le costanti 
	 * 		PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
	 * 		PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
	 * 		PortGareSystemConstants.BUSTA_AMMINISTRATIVA
	 * 		PortGareSystemConstants.BUSTA_TECNICA
	 * 		PortGareSystemConstants.BUSTA_ECONOMICA 
	 * 		PortGareSystemConstants.BUSTA_PRE_QUALIFICA
	 * alle pagine JSP
	 */
	public int getPresentaPartecipazione() { return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA; }
	
	public int getInviaOfferta() { return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA; }
	
	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }

	public int getBUSTA_TECNICA() { return PortGareSystemConstants.BUSTA_TECNICA; }

	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }
	
	public int getBUSTA_PRE_QUALIFICA() { return PortGareSystemConstants.BUSTA_PRE_QUALIFICA; }
	

	/**
	 * Apertura pagina di gestione dei prodotti
	 *
	 * @return
	 */
	public String openPage() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
				
				String tipoComunicazione = (domandaPartecipazione)
					? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT
					: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT; 
				
				String RICHIESTA_TIPO_RIEPILOGO = (domandaPartecipazione)
					? PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO 
					: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO;

				boolean daListaOfferte = "1".equals(this.fromListaOfferte);

				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_SCARTATA);
				if(daListaOfferte) {
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				}
			
				DettaglioComunicazioneType comunicazioneOfferta = ComunicazioniUtilities.retrieveComunicazioneConStati(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(), 
						this.codice, 
						this.progressivoOfferta,
						tipoComunicazione, 
						stati);

				if (comunicazioneOfferta == null) {
					this.setTarget(INPUT);
					this.addActionError(this.getText("Errors.riepilogoOfferta.notFound"));
				} else {
					retrieveDatiRTI(comunicazioneOfferta.getId());
					this.dettGara = this.bandiManager.getDettaglioGara(this.codice);
					this.datiImpresa = (WizardDatiImpresaHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
					if (this.datiImpresa == null) {
						// --- CESSATI --- 
						this.datiImpresa = ImpresaAction.getLatestDatiImpresa(this.getCurrentUser().getUsername(), this, this.getAppParamManager()); 
					}
					this.offertaTecnica = (boolean) this.bandiManager.isGaraConOffertaTecnica(this.codice);
				}
					
				DettaglioComunicazioneType comunicazioneBustaRie = ComunicazioniUtilities.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codice, 
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						RICHIESTA_TIPO_RIEPILOGO);

				// nella gara a lotto unico i documenti stanno nel lotto, 
				// nella gara ad offerta unica stanno nella gara
				String codiceLotto = null;
				if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
					codiceLotto = dettGara.getDatiGeneraliGara().getCodice();
				}
				ComunicazioneType comunicazioneSalvata = null;

				if(comunicazioneBustaRie != null) {
					comunicazioneSalvata = comunicazioniManager
							.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, comunicazioneBustaRie.getId());
					
					this.setBustaRiepilogativa(new RiepilogoBusteHelper(
							bandiManager, 
							this.codice, 
							codiceLotto,
							datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
							this.isRti(), 
							false,
							tipoComunicazione,
							this.getCurrentUser().getUsername(),
							this.progressivoOfferta));
					
					// Popolamento dell'helper di riepilogo gli ultimi dati 
					// presenti all'interno dell'xml
					if(this.bustaRiepilogativa != null) {
						if(domandaPartecipazione) {
							// PARTECIPAZIONE
							RiepilogoBustePartecipazioneDocument documento = 
								getBustePartecipazioneDocument(comunicazioneSalvata);
							
							if(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica() != null) {
								bustaRiepilogativa.getBustaPrequalifica().popolaBusta(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica());
							}
						} else {
							// OFFERTA
							RiepilogoBusteOffertaDocument documento = 
								getBusteOffertaDocument(comunicazioneSalvata);
						
							if(documento.getRiepilogoBusteOfferta().getBustaAmministrativa() != null) {
								bustaRiepilogativa.getBustaAmministrativa().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaAmministrativa());
							}
							if(documento.getRiepilogoBusteOfferta().getBustaTecnica() != null) {
								bustaRiepilogativa.getBustaTecnica().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaTecnica());
							}
							if(documento.getRiepilogoBusteOfferta().getBustaEconomica() != null) {
								bustaRiepilogativa.getBustaEconomica().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaEconomica());
							}
						}
						//} else {
						//   ??? PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI
						//}
					}
				}

				if(domandaPartecipazione) {
					// provo a ricavare la comunicazione per la busta di prequalifica
					DettaglioComunicazioneType comunicazioneBustaPreq = ComunicazioniUtilities.retrieveComunicazioneConStati(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(), 
							this.codice, 
							this.progressivoOfferta,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA, 
							stati);

					if (comunicazioneBustaPreq != null) {
						this.setBustaPrequalificaCifrata(comunicazioneBustaPreq.getSessionKey()!=null);
						this.idBustaPreq = comunicazioneBustaPreq.getId();
					}
					
				} else {
					// provo a ricavare la comunicazione per la busta amministrativa
					DettaglioComunicazioneType comunicazioneBustaAmm = ComunicazioniUtilities.retrieveComunicazioneConStati(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(), 
							this.codice, 
							this.progressivoOfferta,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA, 
							stati);

					if (comunicazioneBustaAmm != null) {
						this.setBustaAmministrativaCifrata(comunicazioneBustaAmm.getSessionKey()!=null);
						this.idBustaAmm = comunicazioneBustaAmm.getId();
					}

					if (this.offertaTecnica) {
						// provo a ricavare la comunicazione per la busta tecnica
						DettaglioComunicazioneType comunicazioneBustaTec = ComunicazioniUtilities.retrieveComunicazioneConStati(
								this.comunicazioniManager,
								this.getCurrentUser().getUsername(), 
								this.codice, 
								this.progressivoOfferta,
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
								stati);

						if (comunicazioneBustaTec != null) {
							this.setBustaTecnicaCifrata(comunicazioneBustaTec.getSessionKey()!=null);
							this.idBustaTec = comunicazioneBustaTec.getId();
						}
					}
					
					// provo a ricavare la comunicazione per la busta economica
					DettaglioComunicazioneType comunicazioneBustaEco = ComunicazioniUtilities.retrieveComunicazioneConStati(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(), 
							this.codice, 
							this.progressivoOfferta,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
							stati);
					if (comunicazioneBustaEco != null) {
						this.setBustaEconomicaCifrata(comunicazioneBustaEco.getSessionKey()!=null);
						this.idBustaEco = comunicazioneBustaEco.getId();
					}
				}

				Date dataAttuale = null;
				this.abilitaRettifica = false;

				if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
					&& this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneDomanda() != null) {
					try {
						dataAttuale = this.ntpManager.getNtpDate();
						Date dataTerminePresentazione = InitIscrizioneAction.calcolaDataOra(
								this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
								this.getDettGara().getDatiGeneraliGara().getOraTerminePresentazioneDomanda(),
								true); 
						if (dataAttuale.compareTo(dataTerminePresentazione) <= 0) {
							this.abilitaRettifica = true;
						}
					} catch (Exception e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}
				
				if (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
					&& this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null) {
					try {
						if (dataAttuale == null) {
							dataAttuale = this.ntpManager.getNtpDate();
						}
						Date dataTerminePresentazione = InitIscrizioneAction.calcolaDataOra(
								this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
								this.getDettGara().getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),
								true);
						if (dataAttuale.compareTo(dataTerminePresentazione) <= 0) {
							this.abilitaRettifica = true;
						}
					} catch (Throwable e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}

			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openPage");
				this.addActionError(this.getText("Errors.invioBuste.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String download() {
		String target = SUCCESS;
		
		Set<String> nomiFilePresenti = new HashSet<String>();
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				this.dettGara = this.bandiManager.getDettaglioGara(this.codice);

				ComunicazioneType comunicazione = comunicazioniManager.getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO, this.idBusta);
				ListaDocumentiType documenti = null;
				if (this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA && this.dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
					// la busta economica per l'offerta telematica ha un tracciato XML diverso dalle altre
					BustaEconomicaDocument bustaEconomicaTelematicaDocument = InitOffertaEconomicaAction.getBustaEconomicaDocument(comunicazione);
					documenti = bustaEconomicaTelematicaDocument.getBustaEconomica().getDocumenti();
				} else if (this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA && this.dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
					// per semplificare la gestione in caso di OEPV con offerta
					// tecnica a wizard, e quindi busta xml con tracciato
					// differente, si tenta di fare il parse con i 2 tracciati
					// previsti
					String contenutoFile = InitOffertaTecnicaAction.getBustaTecnicaDocumentXml(comunicazione);
					try {
						DocumentazioneBustaDocument doc = DocumentazioneBustaDocument.Factory.parse(contenutoFile);
						documenti = doc.getDocumentazioneBusta().getDocumenti();
					} catch (Exception e) {
						// eccezione, allora si tratta di un'offerta telematica, quindi si usa un'altra busta
						BustaTecnicaDocument doc = BustaTecnicaDocument.Factory.parse(contenutoFile);
						documenti = doc.getBustaTecnica().getDocumenti();
					}
					
				} else {
					DocumentazioneBustaDocument document = WizardDocumentiBustaHelper.getDocumentoBusta(comunicazione);
					documenti = document.getDocumentazioneBusta().getDocumenti();
				}
				
				List<Allegato> files = new ArrayList<Allegato>();

				for (int i = 0; i < documenti.sizeOfDocumentoArray(); i++) {
					DocumentoType documento = documenti.getDocumentoArray(i);
					
					/* --- in caso di file aventi lo stesso nome si aggiunge (x) alla fine del nome file --- */ 
					/* --- questo previene il problema del download dello zip (duplicate entry)          --- */
					if(nomiFilePresenti.contains(documento.getNomeFile())){
						String nuovoNomeFile = "Copia di " + documento.getNomeFile(); //windows-like rename automatico file con stesso nome in cartella 
						documento.setNomeFile(nuovoNomeFile);
					}
					nomiFilePresenti.add(documento.getNomeFile());
					
					// se l'allegato è esterno alla busta XML...
					// recupera l'allegato dal servizio
					byte[] contenuto = null;
					if(StringUtils.isNotEmpty(documento.getUuid())) {
						contenuto = ComunicazioniUtilities.getAllegatoComunicazione(this.idBusta, documento.getUuid());
					} else {
						contenuto = documento.getFile();
					}
					files.add(new Allegato(documento.getNomeFile(), contenuto));
				}

				byte[] zipFile = ZipUtilities.getZip(files);
				this.inputStream = new ByteArrayInputStream(zipFile);

				String nomeBusta = "";
				switch (this.tipoBusta) {
				case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
					nomeBusta = "BUSTA_AMMINISTRATIVA";
					break;
				case PortGareSystemConstants.BUSTA_ECONOMICA:
					nomeBusta = "BUSTA_ECONOMICA";
					break;
				case PortGareSystemConstants.BUSTA_TECNICA:
					nomeBusta = "BUSTA_TECNICA";
					break;
				default:
					break;
				}
				this.filename = this.codice + "_" + nomeBusta + ".zip";

			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
				ExceptionUtils.manageExceptionError(e, this);
				session.put(ERROR_DOWNLOAD, this.getActionErrors().toArray()[0]);
				target = INPUT;
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		}
		return target;
	}

	/**
	 * Si estraggono i dati dall'ultima comunicazione di partecipazione in stato
	 * da processare.
	 * 
	 * @param idCom
	 *            idcomunicazione da estrarre
	 * @throws ApsException
	 * @throws XmlException
	 */
	protected void retrieveDatiRTI(long idCom) throws ApsException, XmlException {

		ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, idCom);

		AllegatoComunicazioneType allegatoTipoPartecipazione = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
				&& i < comunicazione.getAllegato().length
				&& allegatoTipoPartecipazione == null) {
			// si cerca l'xml con i dati del tipo partecipazione tra
			// tutti gli allegati
			if (PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoTipoPartecipazione = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}
		if (allegatoTipoPartecipazione == null) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
			this.addActionError(this.getText("Errors.tipoPartecipazione.xmlTipoPartecipazioneNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			TipoPartecipazioneDocument documento = TipoPartecipazioneDocument.Factory
					.parse(new String(allegatoTipoPartecipazione.getFile()));
			this.setRti(documento.getTipoPartecipazione().getRti());
			if (documento.getTipoPartecipazione().getRti()) {
				this.setDenominazioneRti(documento.getTipoPartecipazione().getDenominazioneRti());
			}
		}
	}

	/**
	 * Estrae la busta riepilogatica dalla comunicazione  
	 * 
	 * @param comunicazione
	 *            comunicazione contentente la busta riepilogativa 
	 * 
	 * @throws ApsException
	 * @throws XmlException
	 */
	private static String getBusteDocument(ComunicazioneType comunicazione) 
		throws ApsException, XmlException {
		String xml = null;
		AllegatoComunicazioneType allegatoIscrizione = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
				&& i < comunicazione.getAllegato().length
				&& allegatoIscrizione == null) {
			// si cerca l'xml con i dati dell'iscrizione tra
			// tutti gli allegati
			if (PortGareSystemConstants.NOME_FILE_BUSTA_RIEPILOGATIVA.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoIscrizione = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}
		if (allegatoIscrizione == null) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
			xml = null;
		} else {
			// si estra e l'xml in allegato
			xml = new String(allegatoIscrizione.getFile());
		}		
		return xml;
	}	

	/**
	 * Si estrae l'allegato xml di una partecipazione dalla comunicazione  
	 * 
	 * @param comunicazione
	 *            comunicazione contentente l'allegato xml
	 * 
	 * @throws ApsException
	 * @throws XmlException
	 */
	public static RiepilogoBustePartecipazioneDocument getBustePartecipazioneDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
		RiepilogoBustePartecipazioneDocument documento = null;		
		 
		String buste = getBusteDocument(comunicazione);
		if(buste == null) {
			throw new ApsException("Errors.partecipazioneTelematica.xmlPartecipazioneNotFound");
		} else {
			// si interpreta l'xml ricevuto
			documento = RiepilogoBustePartecipazioneDocument.Factory.parse(buste);
		}		
		return documento;
	}

	/**
	 * Si estrae l'allegato xml di un'offerta dalla comunicazione  
	 * 
	 * @param comunicazione
	 *            comunicazione contentente l'allegato xml
	 * 
	 * @throws ApsException
	 * @throws XmlException
	 */
	public static RiepilogoBusteOffertaDocument getBusteOffertaDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
		RiepilogoBusteOffertaDocument documento = null;		
		
		String buste = getBusteDocument(comunicazione);
		if(buste == null) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
			throw new ApsException("Errors.offertaTelematica.xmlOffertaNotFound");
		} else {
			// si interpreta l'xml ricevuto
			documento = RiepilogoBusteOffertaDocument.Factory.parse(buste);
		}		
		return documento;
	}	

}
