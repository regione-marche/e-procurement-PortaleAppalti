package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComunicazioneFlusso;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.Allegato;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ZipUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
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
	
	@Validate(EParamValidation.INTERO)
	private String fromListaOfferte;	// indica se si arriva dalla openGestioneListaOfferta, "1" = true altrimenti false
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	private boolean offertaTecnica;
	private boolean rti;
	@Validate(EParamValidation.DENOMINAZIONE_RTI)
	private String denominazioneRti;
	private DettaglioGaraType dettGara;
	private WizardDatiImpresaHelper datiImpresa;
	private Long idBustaPreq;
	private Long idBustaAmm;
	private Long idBustaTec;
	private Long idBustaEco;
	private Long idBustaRiepilogo;
	private int idBusta;
	private int tipoBusta;
	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;
	private boolean abilitaRettifica;
	private boolean bustaAmministrativaCifrata;
	private boolean bustaTecnicaCifrata;
	private boolean bustaEconomicaCifrata;
	private boolean bustaPrequalificaCifrata;
	private RiepilogoBusteHelper bustaRiepilogativa;
	private boolean hasFileRiepilogoAllegati;
	private boolean riepilogoAllegatiFirmato;
	
	/**
	 * Riferimento alla url della pagina per visualizzare eventuali problemi emersi
	 */
	private String urlPage;

	/**
	 * Riferimento al frame della pagina in cui visualizzare il contenuto della risposta
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

	
	public Long getIdBustaRiepilogo() {
		return idBustaRiepilogo;
	}

	public void setIdBustaRiepilogo(Long idBustaRiepilogo) {
		this.idBustaRiepilogo = idBustaRiepilogo;
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

	public boolean isHasFileRiepilogoAllegati() {
		return hasFileRiepilogoAllegati;
	}

	public void setHasFileRiepilogoAllegati(boolean hasFileRiepilogoAllegati) {
		this.hasFileRiepilogoAllegati = hasFileRiepilogoAllegati;
	}
	
	public boolean isRiepilogoAllegatiFirmato() {
		return riepilogoAllegatiFirmato;
	}

	public void setRiepilogoAllegatiFirmato(boolean riepilogoAllegatiFirmato) {
		this.riepilogoAllegatiFirmato = riepilogoAllegatiFirmato;
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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						this.codice, 
						this.progressivoOfferta,
						this.operazione);
				
				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
				BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
				BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
				BustaEconomica bustaEconomica = buste.getBustaEconomica();
				BustaTecnica bustaTecnica = buste.getBustaTecnica();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
				
				boolean domandaPartecipazione = buste.isDomandaPartecipazione();
				boolean invioOfferta = buste.isInvioOfferta();

				boolean daListaOfferte = "1".equals(this.fromListaOfferte);

				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_SCARTATA);
				if(daListaOfferte) {
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				}
			
				bustaPartecipazione.get(stati);
				
				if (bustaPartecipazione.getId() <= 0) {
					this.setTarget(INPUT);
					this.addActionError(this.getText("Errors.riepilogoOfferta.notFound"));
				} else {
					this.dettGara = buste.getDettaglioGara();
					
					this.datiImpresa = buste.getImpresa();
					if (this.datiImpresa == null) {
						// --- CESSATI --- 
						this.datiImpresa = buste.getLastestDatiImpresa(); 
					}
					
					this.offertaTecnica = buste.isGaraConOffertaTecnica();
					
					this.setHasFileRiepilogoAllegati(bustaPartecipazione.isHasFileRiepilogoAllegati());
					this.setRiepilogoAllegatiFirmato(bustaPartecipazione.isRiepilogoAllegatiFirmato());	
					this.setIdBustaRiepilogo(bustaPartecipazione.getIdFileRiepilogoAllegati());
					if(bustaPartecipazione.getHelper() != null) {
						this.rti = bustaPartecipazione.getHelper().isRti();
						this.denominazioneRti = bustaPartecipazione.getHelper().getDenominazioneRTI();
					}
				}
				
				bustaRiepilogo.get();
				this.setBustaRiepilogativa( bustaRiepilogo.getHelper() );
				
				if(domandaPartecipazione) {
					// provo a ricavare la comunicazione per la busta di prequalifica
					bustaPrequalifica.get(stati);
					
					if(bustaPrequalifica.getId() > 0) {
						this.setBustaPrequalificaCifrata(bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null);
						this.idBustaPreq = bustaPrequalifica.getId();
					}
				} 
				
				if(invioOfferta) {
					// provo a ricavare la comunicazione per la busta amministrativa
					bustaAmministrativa.get(stati);
					
					if(bustaAmministrativa.getId() > 0) {
						this.setBustaAmministrativaCifrata(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null);
						this.idBustaAmm = bustaAmministrativa.getId();
					}

					// provo a ricavare la comunicazione per la busta tecnica
					if (this.offertaTecnica) {					
						bustaTecnica.get(stati);
						
						if(bustaTecnica.getId() > 0) {
							this.setBustaTecnicaCifrata(bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null);
							this.idBustaTec = bustaTecnica.getId();
						}
					}
					
					// provo a ricavare la comunicazione per la busta economica
					bustaEconomica.get(stati);
					
					if(bustaEconomica.getId() > 0) {
						this.setBustaEconomicaCifrata(bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null);
						this.idBustaEco = bustaEconomica.getId();
					}
				}
				
				// verifica per le domande di prequalifica/offerte 
				// se l'operazione e' ancora possibile rettificare				
				this.abilitaRettifica = this.rettificaAbilitata();

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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				GestioneBuste buste = GestioneBuste.getFromSession();
				BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
				BustaTecnica bustaTec = GestioneBuste.getBustaTecnicaFromSession();
				this.dettGara = buste.getDettaglioGara();

				// recupera l'xml dalla comunicazione
				ComunicazioneType comunicazione = comunicazioniManager.getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO, 
						this.idBusta);
				String xml = ComunicazioneFlusso.getXmlDoc(comunicazione);
				
				ListaDocumentiType documenti = null;
				if (this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA && this.dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
					// la busta economica per l'offerta telematica ha un tracciato XML diverso dalle altre
					BustaEconomicaDocument bustaEconomicaDocument = BustaEconomicaDocument.Factory.parse(xml);
					documenti = bustaEconomicaDocument.getBustaEconomica().getDocumenti();
					
				} else if (this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA && this.dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
					// per semplificare la gestione in caso di OEPV con offerta
					// tecnica a wizard, e quindi busta xml con tracciato
					// differente, si tenta di fare il parse con i 2 tracciati
					// previsti
					try {
						DocumentazioneBustaDocument doc = DocumentazioneBustaDocument.Factory.parse(xml);
						documenti = doc.getDocumentazioneBusta().getDocumenti();
					} catch (Exception e) {
						// eccezione, allora si tratta di un'offerta telematica, quindi si usa un'altra busta
						BustaTecnicaDocument doc = BustaTecnicaDocument.Factory.parse(xml);
						documenti = doc.getBustaTecnica().getDocumenti();
					}
					
				} else {
					DocumentazioneBustaDocument document = BustaDocumenti.getBustaDocument(comunicazione);
					documenti = document.getDocumentazioneBusta().getDocumenti();
				}
				
				List<Allegato> files = new ArrayList<Allegato>();

				for (int i = 0; i < documenti.sizeOfDocumentoArray(); i++) {
					DocumentoType documento = documenti.getDocumentoArray(i);
					
					// --- in caso di file aventi lo stesso nome si aggiunge (x) alla fine del nome file --- 
					// --- questo previene il problema del download dello zip (duplicate entry)          ---
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

				String descrizioneBusta = BustaGara.getDescrizioneBusta(this.tipoBusta);
				this.filename = this.codice + "_" + descrizioneBusta + ".zip";

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
	 * imposta l'abilitazione della rettifica  
	 */
	protected boolean rettificaAbilitata() {
		boolean abilita = false;
		
		boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA); 
		boolean invioOfferta = !domandaPartecipazione; //(operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);

		if (domandaPartecipazione 
		    && this.dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda() != null) {
			try {
				Date dataAttuale = this.ntpManager.getNtpDate();
				Date dataTermine = InitIscrizioneAction.calcolaDataOra(
						this.dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
						this.dettGara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(),
						true);
				if (dataAttuale.compareTo(dataTermine) <= 0) {
					abilita = true;
				}
			} catch (Throwable e) {
				// non si fa niente, si usano i dati ricevuti dal
				// servizio e quindi i test effettuati nel dbms server
			}
		}
		
		if (invioOfferta
			&& this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null) {
			try {
				//if (dataAttuale == null) {						 ???
				//	dataAttuale = this.getNtpManager().getNtpDate(); ???
				//}													 ???
				Date dataAttuale = this.ntpManager.getNtpDate();
				Date dataTermine = InitIscrizioneAction.calcolaDataOra(
						this.dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
						this.dettGara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),
						true);
				if (dataAttuale.compareTo(dataTermine) <= 0) {
					abilita = true;
				}
			} catch (Throwable e) {
				// non si fa niente, si usano i dati ricevuti dal
				// servizio e quindi i test effettuati nel dbms server
			}
		}
		return abilita;
	}

}
