package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.AbilitazioniGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.InvitoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.MandanteRTIType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.OffertaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

/**
 * ... 
 */
public class OpenGestioneListaOfferteAction extends EncodedDataAction implements SessionAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2075463826254940330L;
	
	private INtpManager ntpManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;	
	private IEventManager eventManager;
	
	private Map<String, Object> session;
	
	@Validate(EParamValidation.CODICE)
	private String codice;							// codice gara
	@Validate(EParamValidation.CODICE)
	private String codiceGara;						// codice lotto
	private Integer operazione;
	private List<OffertaBean> listaOfferte;			// lista dei plichi definiti per la gara
	private Boolean presentazionePartecipazione; 
	private DettaglioGaraType dettaglioGara;
	private AbilitazioniGaraType abilitazioniGara;
	private Boolean esistePrequalificaSingola;
	private Boolean abilitaOffertaSingola;
	
	// serve per la navigazione tra le pagine
	private Long idComunicazione;

	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
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

	public Integer getOperazione() {
		return operazione;
	}

	public void setOperazione(Integer operazione) {
		this.operazione = operazione;
	}

	public List<OffertaBean> getListaOfferte() {
		return listaOfferte;
	}	
	
	public Boolean getPresentazionePartecipazione() {
		return presentazionePartecipazione;
	}
	
	public DettaglioGaraType getDettaglioGara() {
		return dettaglioGara;
	}

	public void setDettaglioGara(DettaglioGaraType dettaglioGara) {
		this.dettaglioGara = dettaglioGara;
	}
	
	public AbilitazioniGaraType getAbilitazioniGara() {
		return abilitazioniGara;
	}

	public void setAbilitazioniGara(AbilitazioniGaraType abilitazioniGara) {
		this.abilitazioniGara = abilitazioniGara;
	}
	
	public Boolean getAbilitaOffertaSingola() {
		return abilitaOffertaSingola;
	}

	public void setAbilitaOffertaSingola(Boolean abilitaOffertaSingola) {
		this.abilitaOffertaSingola = abilitaOffertaSingola;
	}
	
	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	/**
	 * costanti per la pagina JSP
	 */
	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}
	
	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}
	
	/**
	 * Apertura pagina di gestione della lista dei plichi definiti per la gara
	 *
	 * @return
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		// ATTENZIONE: 
		// come workaraound si fa pulizia della sessione per evitare che la jsp prenda i dati 
		// dalla sessione piuttosto che dalla action come invece dovrebbe!!!
		// NB: 
		// probabilemente la sessione va pulita nella gestione dei lotti della busta economica/tecnica!!!
		this.session.remove("codice");
		this.session.remove("codiceGara");
		
		this.listaOfferte = null;
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				boolean continua = true;
				
				this.esistePrequalificaSingola = false;
				this.abilitaOffertaSingola = true;
				
				String codGara = (StringUtils.isEmpty(this.codice) ? this.codiceGara : this.codice);
				
				DettaglioGaraType gara = this.bandiManager.getDettaglioGara(codGara);
				LottoGaraType[] lottiGara = bandiManager.getLottiGara(codGara);
				AbilitazioniGaraType abilitazioni = this.bandiManager
					.getAbilitazioniGara(this.getCurrentUser().getUsername(), codGara);
				
				this.setDettaglioGara(gara);
				this.setAbilitazioniGara(abilitazioni);
				
				boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
				boolean invioOfferta = !domandaPartecipazione;
				this.presentazionePartecipazione = false;
		
				boolean ristretta = WizardPartecipazioneHelper.isGaraRistretta(gara.getDatiGeneraliGara().getIterGara());
				boolean negoziata = WizardPartecipazioneHelper.isGaraNegoziata(gara.getDatiGeneraliGara().getIterGara());
				
				Date dataAttuale = null;
				try {
					dataAttuale = this.ntpManager.getNtpDate();
				} catch (Exception e) {
					// non si fa niente, si usano i dati ricevuti dal
					// servizio e quindi i test effettuati nel dbms server
				}
				
				if (dataAttuale != null && 
					gara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda() != null) {
					try {
						if (dataAttuale.compareTo(InitIscrizioneAction.calcolaDataOra(
									gara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
									gara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(), true)) > 0) {
							this.abilitazioniGara.setRichPartecipazione(false);
						}
					} catch (Exception e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}
				
				if (dataAttuale != null && 
					gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null) {
					try {
						if (dataAttuale.compareTo(InitIscrizioneAction.calcolaDataOra(
									gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
									gara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),	true)) > 0) {
							this.abilitazioniGara.setRichInvioOfferta(false);
						}
					} catch (Exception e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}

				// per le gare RISTRETTE verifica se sono gia' presenti le comunicazioni FS11
				// altrimenti recupera le informazioni dalle comunicazioni della prequalifica FS10
				// per precompilare la lista delle offerte...
				if(ristretta && invioOfferta) {
					this.generaOffertePerRistrette(codGara, gara);
				} 
				
				// per le gare NEGOZIATE verifica se e' gia' presente l'offerta #1 (singola o rti)
				// altrimenti recupera i dati di partecipazione dell'invito e 
				// compila una FS11 con progressivo offerta #1 con i partecipanti dell'invito
				if(negoziata && invioOfferta) {
					this.generaOffertaPerNegoziata(codGara, gara);
				}
				
				HashMap<String, String> warningLotti = new HashMap<String, String>();
				
				// cerca tutte le FS11/FS10 relative alla gara
			    String RICHIESTA_TIPO = (domandaPartecipazione)
					? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT
					: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
			    
				DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
				filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				filtri.setChiave1(this.getCurrentUser().getUsername());
				filtri.setChiave2(codGara);
				filtri.setTipoComunicazione(RICHIESTA_TIPO);
				
				List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager
						.getElencoComunicazioni(filtri);
				
				// prepara la lista delle offerte...
				List<OffertaBean> lista = new ArrayList<OffertaBean>();
				for(int i = 0; i < comunicazioni.size(); i++) {
					
					// recupera i dati della forma di partecipazione relativi all'offerta...
					ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
							CommonSystemConstants.ID_APPLICATIVO,
							comunicazioni.get(i).getId()
					);
					TipoPartecipazioneDocument doc = WizardDocumentiBustaHelper
							.getDocumentoPartecipazione(comunicazione);
					TipoPartecipazioneType partecipazione = doc.getTipoPartecipazione();
					
					String ragioneSociale = "";
					DatiImpresaDocument impresa = this.bandiManager.getDatiImpresa(comunicazioni.get(i).getChiave1(), new Date());
					if(impresa != null && impresa.getDatiImpresa() != null && impresa.getDatiImpresa().getImpresa() != null) {
						ragioneSociale = impresa.getDatiImpresa().getImpresa().getRagioneSociale();
					}
					
					long progOfferta = str2intDef(comunicazioni.get(i).getChiave3(), 0L);
					long prog = i + 1;
					
					String concorrente = (partecipazione.getRti() 
							? partecipazione.getDenominazioneRti() 
							: ragioneSociale);
					
					// recupera l'elenco delle mandanti dell'offerta...
					List<String> mandanti = null;
					if(partecipazione.getRti()) {
						mandanti = new ArrayList<String>();
						//mandanti.add(ragioneSociale);
						if(partecipazione.getPartecipantiRaggruppamento() != null) {
							PartecipanteRaggruppamentoType[] partecipanti = partecipazione.getPartecipantiRaggruppamento().getPartecipanteArray();							
							for(int j = 0; j < partecipanti.length; j++) {
								mandanti.add( partecipanti[j].getRagioneSociale() );
							}
						}
					}
					
					// recupera l'elenco dei lotti dell'offerta...
					List<LottoGaraType> lotti = null;
					if(partecipazione.getCodiceLottoArray() != null) {
						String[] lottiPart = partecipazione.getCodiceLottoArray();
						lotti = new ArrayList<LottoGaraType>();
						for(int j = 0; j < lottiPart.length; j++) {
							for(int k = 0; k < lottiGara.length; k++) {
								if(lottiPart[j].equals(lottiGara[k].getCodiceLotto())) {
									lotti.add( lottiGara[k] );
									
									// aggiorna le coppie (lotto, offerte)...
									String lst = warningLotti.get(lottiGara[k].getCodiceInterno());
									lst = (lst != null && lst.length() > 0 ? lst + "," : "") + "#" + Long.toString(prog);
									warningLotti.put(lottiGara[k].getCodiceInterno(), lst);
									break;
								}
							}
						}
					}
					
					// tipo di annullamento per l'offerta/domanda (reset, annullamento, rettifica)
					int tipoAnnullamento = this.recuperaTipoAnnullamento(
							gara, 
							comunicazioni.get(i).getChiave3());
					
					// aggiungi l'offerta alla lista... 
					OffertaBean plico = new OffertaBean();
					plico.setProgressivoOfferta(progOfferta);
					plico.setConcorrente(concorrente);
					plico.setTipoPartecipazione((partecipazione.getRti() ? "1" : "0"));
					plico.setMandanti(mandanti);
					plico.setLotti(lotti);
					plico.setStato(comunicazioni.get(i).getStato());
					plico.setAnnullamento(tipoAnnullamento == 1);
					plico.setEliminazione(tipoAnnullamento == 2);
					plico.setRettifica(tipoAnnullamento == 3);
					
					lista.add(plico);
				}
				this.listaOfferte = lista;
				
				// prepara l'elenco dei messaggi delle incongruenze per lotti sovrapposti
				for (Map.Entry<String, String> item : warningLotti.entrySet()) {
				    String[] offerte = item.getValue().split(",");
				    if(offerte != null && offerte.length > 1) {
				    	String keyMsg = (domandaPartecipazione 
				    					 ? "Errors.lottiDomandeSovrapposti"
				    					 : "Errors.lottiOfferteSovrapposti");
						this.addActionMessage(this.getText(keyMsg, 
											  new String[] {item.getKey(), item.getValue()}));
				    }
				}
				
				
				// abilita il pulsante "presenta singola"...
				// in caso di ristretta in fase di offerta...
				// se NON esiste una domanda di prequalifica come singola (FS10)
				// allora la partecipazione corrente puo' avere solo offerta in rti
				if(ristretta && invioOfferta) {
					if(!this.esistePrequalificaSingola) {
						 this.abilitaOffertaSingola = false;
					} 
					
					// NB: se la ditta non ha partecipato alla prequalifica
					// ma e' stata invitata da BO alla fase di offerta 
					// allora puo' presentare anche come singola...
					List<InvitoGaraType> inviti = this.bandiManager.getElencoInvitiGara(
							this.getCurrentUser().getUsername(), 
							this.codiceGara);
					if(inviti != null && inviti.size() > 0) {
						this.abilitaOffertaSingola = true;
					}
				}
				
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "open");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "open");
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
	 * String to long  
	 */
	private Long str2intDef(String value, long defValue) {
		try {
			return Long.parseLong(value);
		} catch(Exception e) {
			return defValue;
		}
	}
	
//	/**
//	 * crea ed inizializza lo helper di partecipazione
//	 * @throws XmlException 
//	 * 
//	 */
//	private WizardPartecipazioneHelper createHelperPartecipazione(
//			String codiceGara, 
//			DettaglioGaraType gara,
//			ComunicazioneType prequalifica,
//			String progressivoOfferta,
//			WizardDatiImpresaHelper impresa,
//			boolean consorziateEsecutriciPresenti,
//			LottoGaraType[] lottiGara) throws XmlException 
//	{
//		// NB: Attenzione, in questa sezione si "replica" una creazione dell'helper
//		//     di partecipazione come nel riepilogo del wizard di partecipazione delle
//		//     offerte, quindi eventuali modifiche al wizard di partecipazione offerte
//		//     vanno portate anche in questa sezione 
//		WizardPartecipazioneHelper partecipazione;
//		if(prequalifica != null) {
//			partecipazione = new WizardPartecipazioneHelper(prequalifica);
//		} else {
//			partecipazione = new WizardPartecipazioneHelper();
//		}
//		partecipazione.setIdBando(codiceGara);
//		partecipazione.setDescBando(gara.getDatiGeneraliGara().getOggetto());
//		partecipazione.setTipoEvento(this.operazione);
//		partecipazione.setGaraTelematica(gara.getDatiGeneraliGara().isProceduraTelematica());
//		partecipazione.setTipoProcedura(Integer.parseInt(gara.getDatiGeneraliGara().getTipoProcedura()));
//		partecipazione.setIterGara(Integer.parseInt(gara.getDatiGeneraliGara().getIterGara()));
//		partecipazione.setProgressivoOfferta(progressivoOfferta);
//		partecipazione.setImpresa(impresa);
//		partecipazione.setPlicoUnicoOfferteDistinte(true);	//gara.getDatiGeneraliGara().getTipologia()==PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO 
//		partecipazione.setConsorziateEsecutriciPresenti(consorziateEsecutriciPresenti);
//		
//		// recupera da BO l'elenco dei lotti ammessi in offerta...
//		if(lottiGara != null) { 
//			if(partecipazione.isPlicoUnicoOfferteDistinte()) {
//				// non e' ancora stata compilata l'offerta ed
//				// quindi proponi tutti i lotti di gara selezionati
//				List<String> lottiAttivi = new ArrayList<String>();
//				List<String> oggettiLotti = new ArrayList<String>();
//				partecipazione.getLotti().clear();
//				for(int j = 0; j < lottiGara.length; j++) {
//					if(progressivoOfferta.equals(lottiGara[j].getProgressivoOfferta())) {
//						lottiAttivi.add(lottiGara[j].getCodiceLotto());
//						oggettiLotti.add(lottiGara[j].getOggetto());
//						if(!partecipazione.getLotti().contains(lottiGara[j].getCodiceLotto())) {
//							partecipazione.getLotti().add(lottiGara[j].getCodiceLotto());
//						}
//					}
//				}
//				partecipazione.setLottiAmmessiInOfferta(lottiAttivi);
//				partecipazione.setOggettiLotti(oggettiLotti);
//			}
//		}	
//		return partecipazione;
//	}
	
	/**
	 * genera l'elenco delle offerte in base alle domande di partecipazione
	 * @throws Throwable 
	 */
	private void generaOffertePerRistrette(
			String codiceGara, 
			DettaglioGaraType gara) 
		throws Throwable 
	{	
		boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		boolean invioOfferta = !domandaPartecipazione;
		if(!invioOfferta) {
			return;
		}
		
		// recupera l'elenco delle domande di partecipazione (o prequalifica (FS10))
		// NB: al 25/01/2021 si assume che il "progressivoOfferta = 1" 
		//     indichi sempre la domanda come "singola"!!!
		DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
		filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filtri.setChiave1(this.getCurrentUser().getUsername());
		filtri.setChiave2(codiceGara);
		filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
		filtri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		
		List<DettaglioComunicazioneType> tmp = this.comunicazioniManager
				.getElencoComunicazioni(filtri);
		List<DettaglioComunicazioneType> comunicazioni = new ArrayList<DettaglioComunicazioneType>();
				
		// elimina dalla lista delle comunicazioni quelle con non hanno un invito 
		// con lo stesso progressivoOfferta
		List<InvitoGaraType> inviti = this.bandiManager.getElencoInvitiGara(
				this.getCurrentUser().getUsername(), 
				codiceGara);

		if(tmp != null && inviti != null) {
			for(int i = 0; i < tmp.size(); i++) {
				for(int j = 0; j < inviti.size(); j++) {
					if(tmp.get(i).getChiave3().equals(inviti.get(j).getProgressivoOfferta())) {
						comunicazioni.add(tmp.get(i));
						break;
					}
				}
			}
		}
		
		this.esistePrequalificaSingola = false;
		for(int i = 0; i < comunicazioni.size(); i++) {
			if("1".equals(comunicazioni.get(i).getChiave3())) {
				this.esistePrequalificaSingola = true;
				break;
			}
		}
	
		// verifica se esistono gia' le FS11 relative alla prequalifica FS10
		filtri = new DettaglioComunicazioneType();
		filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filtri.setChiave1(this.getCurrentUser().getUsername());
		filtri.setChiave2(codiceGara);
		filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT);
		
		List<DettaglioComunicazioneType> offerte = this.comunicazioniManager
				.getElencoComunicazioni(filtri);
// NB: 
//	il controllo viene disabilitato al fine di ricostruire le eventuali 
//	righe mancanti in seguito ad un bug sulla getElencoInvitiGara 
//		if(offerte != null && offerte.size() > 0) {
//			// offerte gia' generate...
//			return;
//		}

//		// recupera i dati impresa... 
//		WizardDatiImpresaHelper impresa = ImpresaAction.getLatestDatiImpresa(
//				this.getCurrentUser().getUsername(),
//				this, 
//				this.appParamManager);
		
//		// recupera i lotti ammessi in offerta...
//		LottoGaraType[] lottiGara = null;
//		if(PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO == gara.getDatiGeneraliGara().getTipologia()) {
//			lottiGara = this.bandiManager.getLottiGaraPlicoUnicoPerRichiesteOfferta(
//							this.getCurrentUser().getUsername(), 
//							codiceGara,
//							null);
//		} else {
//			lottiGara = this.bandiManager.getLottiGaraPerRichiesteOfferta(
//							this.getCurrentUser().getUsername(), 
//							codiceGara,
//							null);
//		}

//		// recupera se sono presenti delle consorziate esecutrici
//		boolean consorziateEsecutriciPresenti = false;
//		if (impresa.isConsorzio() && this.bandiManager.isConsorziateEsecutriciPresenti(this.getCurrentUser().getUsername(), codiceGara)) {
//			consorziateEsecutriciPresenti = true;
//		}

		// resetta i filtri...
		filtri = new DettaglioComunicazioneType();
		
//		// GENERAZIONE OFFERTA DA FS10 
//		// genera le offerte (FS11) in base alle domande di prequalifica (FS10)
//		for(int i = 0; i < comunicazioni.size(); i++) {
//			String progOfferta = comunicazioni.get(i).getChiave3();
//			
//			// recupera i dati della prequalifica...
//			ComunicazioneType prequalifica = this.comunicazioniManager.getComunicazione(
//					CommonSystemConstants.ID_APPLICATIVO,
//					comunicazioni.get(i).getId());
//			
//			// verifica se esiste gia' una FS11 relativa alla prequalifica FS10
//			// e se non esiste genera una nuova comunicazione FS11/FS11R
//			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
//			filtri.setChiave1(this.getCurrentUser().getUsername());
//			filtri.setChiave2(prequalifica.getDettaglioComunicazione().getChiave2());
//			filtri.setChiave3(progOfferta);
//			filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT);
//			
//			offerte = this.comunicazioniManager.getElencoComunicazioni(filtri);
//			
//			if(offerte == null || (offerte != null && offerte.size() <= 0)) {
//				// prepara un helper di paretcipazione per creare le comunicazioni FS11,FS11R 
//				// per inizializzare la lista delle offerte relative alla gara
//				// in base a quanto trovato in fase di prequalifica...
//				WizardPartecipazioneHelper partecipazione = this.createHelperPartecipazione(
//						codiceGara,
//						gara,
//						prequalifica,
//						progOfferta,
//						impresa,
//						consorziateEsecutriciPresenti,
//						lottiGara);
//				
//				// invia le comunicazioni di partecipazione FS11,FS11R...
//				RichiestaPartecipazione richiesta = new RichiestaPartecipazione(this, false);
//				
//				boolean inviata = richiesta.send(
//						codiceGara,
//						partecipazione, 
//						null, 
//						impresa);
//				
//				if(!inviata) {
//					this.addActionError(richiesta.getActionError());
//					this.setTarget(richiesta.getActionTarget());
//				}
//			}
//		}
		
		// GENERAZIONE OFFERTE DA INVITI 
		// CREA LE FS11 PARTENDO DAGLI INVITI 
		// per ogni invito crea una comunicazione relativa all'offerta FS11...
		List<String> stati = new ArrayList<String>();
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

		for(int j = 0; j < inviti.size(); j++) {
			// considera solo le righe relative alla partecipazione e salta le righe corrispondenti ai lotti
			String cod  = (StringUtils.isNotEmpty(inviti.get(j).getCodice()) ? inviti.get(j).getCodice() : "");
			String codLotto = (StringUtils.isNotEmpty(inviti.get(j).getCodiceLotto()) ? inviti.get(j).getCodiceLotto() : "");
			if(codLotto.equalsIgnoreCase(cod)) {
				
				String progOfferta = inviti.get(j).getProgressivoOfferta();
				
				// verifica se esiste gia' una FS11... 
				// e se non esiste genera una nuova comunicazione FS11/FS11R
				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(), 
						codiceGara, 
						progOfferta,
						this.operazione);
				buste.get(stati);
				
				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
				WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper();
				//List<String> lottiAttivi = partecipazione.getLottiAmmessiInOfferta();
					
				if(bustaPartecipazione.getId() <= 0) {
					
					// recupera i dati della prequalifica, se ci sono...
					ComunicazioneType prequalifica = null;
					for(int i = 0; i < comunicazioni.size(); i++) {
						if(comunicazioni.get(i).getChiave3().equals(progOfferta)) {
							prequalifica = this.comunicazioniManager.getComunicazione(
									CommonSystemConstants.ID_APPLICATIVO,
									comunicazioni.get(i).getId());
							break;
						}
					}
					
					if(prequalifica != null) {
						// inizializza la partecipazione in base all'invito
						// recupera la partecipazione dell'invito presente in BO
						it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType partecipazioneInvito = this.bandiManager
							.getTipoPartecipazioneImpresa(
									this.getCurrentUser().getUsername(), 
									codiceGara, 
									progOfferta);
		
						List<IComponente> componentiRTI = this.getComponentiRti(
								codiceGara,
								progOfferta,
								partecipazioneInvito,
								buste.getImpresa());
						
						partecipazione.setDataPresentazione(new Date());
						partecipazione.setRti(partecipazioneInvito.isRti());
						partecipazione.setDenominazioneRTI(partecipazioneInvito.getDenominazioneRti());
						partecipazione.setComponenti(componentiRTI);
						
						// invia le comunicazioni di partecipazione FS11 (e FS11R)...
						boolean inviata = bustaPartecipazione.send();
					}
				}
			}
		}
	}
	
	/**
	 * genera l'elenco delle offerte in base all'invito presente in BO
	 * @throws Throwable 
	 */
	private void generaOffertaPerNegoziata(
			String codiceGara, 
			DettaglioGaraType gara) 
		throws Throwable 
	{	
		String progressivoOfferta = "1";
		
		boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		boolean invioOfferta = !domandaPartecipazione;
		if(!invioOfferta) {
			return;
		}
		
		// verifica se esiste gia' una FS11 relativa al progressivo #1  
		DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
		filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filtri.setChiave1(this.getCurrentUser().getUsername());
		filtri.setChiave2(codiceGara);
		filtri.setChiave3(progressivoOfferta);
		filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT);
		
		List<DettaglioComunicazioneType> offerte = this.comunicazioniManager.getElencoComunicazioni(filtri);
		
		if(offerte != null && offerte.size() > 0) {
			return;
		}
		
		// recupera i dati impresa...
		GestioneBuste buste = new GestioneBuste(
				this.getCurrentUser().getUsername(), 
				codiceGara, 
				progressivoOfferta,
				this.operazione);
		buste.get();
		
		BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
		WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper();
		
		// recupeara la partecipazione dell'invito presente in BO
		it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType partecipazioneInvito = this.bandiManager
			.getTipoPartecipazioneImpresa(
					this.getCurrentUser().getUsername(), 
					codiceGara, 
					progressivoOfferta);
		
		List<IComponente> componentiRTI = this.getComponentiRti(
				codiceGara,
				progressivoOfferta,
				partecipazioneInvito,
				buste.getImpresa());

		// verifica se esiste gia' una FS11 relativa a #1
		// e se non esiste genera una nuova comunicazione FS11/FS11R
		if(offerte == null || (offerte != null && offerte.size() <= 0)) {
			// prepara un helper di paretcipazione per creare le comunicazioni FS11,FS11R 
			// in base all'invito presente in BO per la negoziata...
			
			// inizializza la partecipazione in base all'invito
			partecipazione.setDataPresentazione(new Date());
			partecipazione.setRti(partecipazioneInvito.isRti());
			partecipazione.setDenominazioneRTI(partecipazioneInvito.getDenominazioneRti());
			partecipazione.setComponenti(componentiRTI);

			boolean inviata = bustaPartecipazione.send();
		}
	}
	
	/**
	 * recupera i componenti RTI della partecipazione d'invito 
	 * @throws ApsException 
	 */
	private List<IComponente> getComponentiRti(
			String codiceGara,
			String progressivoOfferta,
			it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType partecipazioneInvito,
			WizardDatiImpresaHelper impresa) 
			throws ApsException 
	{
		List<IComponente> componentiRTI = new ArrayList<IComponente>();
		if(partecipazioneInvito.isRti()) {
			// *** RTI ***
			// recupera i dati del raggruppamento in BO
			List<MandanteRTIType> mandantiBO = this.bandiManager.getMandantiRTI(
					codiceGara, 
					this.getCurrentUser().getUsername(),
					progressivoOfferta);
			
			// mandataria
			IComponente mandataria = new ComponenteHelper();
			mandataria.setCodiceFiscale(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
			mandataria.setPartitaIVA(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
			mandataria.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
			mandataria.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
			mandataria.setAmbitoTerritoriale(impresa.getDatiPrincipaliImpresa().getAmbitoTerritoriale());
			if("2".equals(impresa.getDatiPrincipaliImpresa().getAmbitoTerritoriale())) { 
				mandataria.setIdFiscaleEstero(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
			} else {
				mandataria.setIdFiscaleEstero(null);
			}
			componentiRTI.add(mandataria);
			
			// mandanti
			for(int i = 0; i < mandantiBO.size();i++) {
				IComponente mandante = new ComponenteHelper();
				mandante.setCodiceFiscale(mandantiBO.get(i).getCodiceFiscale());
				mandante.setPartitaIVA(mandantiBO.get(i).getPartitaIVA());
				mandante.setRagioneSociale(mandantiBO.get(i).getRagioneSociale());
				if (mandantiBO.get(i).getTipologia() != null) {
					mandante.setTipoImpresa(mandantiBO.get(i).getTipologia().toString());
				}
				if (mandantiBO.get(i).getAmbitoTerritoriale() != null) {
					mandataria.setAmbitoTerritoriale(mandantiBO.get(i).getAmbitoTerritoriale());
				}
				if (mandantiBO.get(i).getIdFiscaleEstero() != null) {
					mandataria.setIdFiscaleEstero(mandantiBO.get(i).getIdFiscaleEstero());
				}
				componentiRTI.add(mandante);
			}
			
			// aggiungi i firmatari...
			//for(int i = 0; i < componentiRTI.size(); i++) {
			//	SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
			//	componentiRTI.addFirmatario(componentiRTI.get(i), firmatario);
			//}
		} else {
			// *** SINGOLA *** 
			// serve fare qualcosa ???
		}
		
		return componentiRTI;
	}
	
	/**
	 * restituisce il tipo di annullamento per l'offerta/domanda
	 * (1=annullamento, 2=eliminazione, 3=rettifica)
	 * @throws ApsException 
	 */
	private int recuperaTipoAnnullamento(
			DettaglioGaraType gara,
			String progressivoOfferta) throws ApsException 
	{
		int tipoAnnullamento = 0;
		
		String codice = gara.getDatiGeneraliGara().getCodice();
		
		boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		boolean invioOfferta = !domandaPartecipazione;
		boolean ristretta = WizardPartecipazioneHelper.isGaraRistretta(gara.getDatiGeneraliGara().getIterGara());
		boolean negoziata = WizardPartecipazioneHelper.isGaraNegoziata(gara.getDatiGeneraliGara().getIterGara());
		
		// verifica se e' presente la cifratura delle buste
		boolean cifraturaAbilitata = false;
		if(invioOfferta) {
			cifraturaAbilitata = 
				(this.bandiManager.getChiavePubblica(codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA) != null
				 || this.bandiManager.getChiavePubblica(codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA) != null
				 || this.bandiManager.getChiavePubblica(codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA) != null); 
		} else {
			cifraturaAbilitata = (this.bandiManager.getChiavePubblica(codice, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA) != null);
		}
		
		// per le ristrette recupera il progressivo max 
		// delle domande di prequalifica...
		// se esiste una comunicazione FS11* inviata (stato <> 1) 
		// allora l'offerta risulta inviata
		boolean presenteDomanda = false; 
		boolean inCompilazione = true;
		
		DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
		filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filtri.setChiave1(this.getCurrentUser().getUsername());
		filtri.setChiave2(codice);
		filtri.setChiave3(progressivoOfferta);
		
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(filtri);
		if(comunicazioni != null) {
			for(int i = 0; i < comunicazioni.size(); i++) {
				if( comunicazioni.get(i).getTipoComunicazione().contains(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT)) {
					// verifica lo stato solo delle comunicazioni FS11, FS11A, FS11B, FS11C,...
					if( !CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazioni.get(i).getStato()) ) {
						// offerta inviata
						inCompilazione = false;
					}
				} else if( PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT.equals(comunicazioni.get(i).getTipoComunicazione())) {
					// verifica se esiste la domanda di partecipazione FS10...
					presenteDomanda = true;
				}
			}
		}

		// verifica il metodo quale metodo utilizzare tra
		// - annullamento  mmetodo per resettare FS11/FS11R e ripristinare un'offerta derivante da una domanda di prequalifica
		// - eliminazione  metodo per eliminare anche FS11/FS11R di un'offerta non ancora "inviata"
		// - rettifica     metodo gia' esistente per la rettifica di un'offerta
		tipoAnnullamento = OpenGestioneListaOfferteAction.getTipoAnnullamento(
				cifraturaAbilitata, 
				negoziata, 
				ristretta, 
				invioOfferta, 
				presenteDomanda, 
				inCompilazione, 
				progressivoOfferta);
		
		return tipoAnnullamento;
	}
		
	/**
	 * restituisci il tipo di annullamento 
	 * (1=annullamento, 2=eliminazione, 3=rettifica)  
	 */
	public static int getTipoAnnullamento(
			boolean cifraturaAbilitata,
			boolean negoziata,
			boolean ristretta,
			boolean invioOfferta,
			boolean presenteDomandaPreq,
			boolean inCompilazione,
			String progressivoOfferta) 
	{
		int tipoAnnullamento = 0;
		
		// verifica il metodo quale metodo utilizzare tra
		// - annullamento  mmetodo per resettare FS11/FS11R e ripristinare un'offerta derivante da una domanda di prequalifica
		// - eliminazione  metodo per eliminare anche FS11/FS11R di un'offerta non ancora "inviata"
		// - rettifica     metodo gia' esistente per la rettifica di un'offerta
		if(cifraturaAbilitata) {
			// NB: per le NEGOZIATE solo l'offerta #1 deve essere resettabile e non eliminabile 
			if(negoziata && "1".equals(progressivoOfferta)) {
				// annullamento
				// resetta le FS11,FS11R 
				// elimina le FS11A, FS11B*, FS11C*
				tipoAnnullamento = 1;
			} else if(ristretta && invioOfferta && presenteDomandaPreq) {
				// annullamento
				// resetta le FS11,FS11R 
				// elimina le FS11A, FS11B*, FS11C*
				tipoAnnullamento = 1;
			} else if(inCompilazione) {
				// eliminazione
				// elimina tutte FS11, FS11R, FS11A, FS11B*, FS11C*
				tipoAnnullamento = 2;
			} else {
				// rettifica
				tipoAnnullamento = 3;
			}
		} else {
			// NB: per le NEGOZIATE solo l'offerta #1 deve essere resettabile e non eliminabile 
			if(negoziata && "1".equals(progressivoOfferta)) {
				// annullamento
				// resetta le FS11,FS11R 
				// elimina le FS11A, FS11B*, FS11C*
				tipoAnnullamento = 1;
			} else if(ristretta && invioOfferta && presenteDomandaPreq) {
				// annullamento 
				// resetta le FS11,FS11R 
				// elimina le FS11A, FS11B*, FS11C*
				tipoAnnullamento = 1;
			} else {
				// eliminazione
				// elimina tutte FS11, FS11R, FS11A, FS11B*, FS11C*
				tipoAnnullamento = 2;
			}
		}
		return tipoAnnullamento;
	}
	
}
 