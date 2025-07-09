package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.eldasoft.www.sil.WSGareAppalto.VendorRatingType;
import it.eldasoft.www.sil.WSPagoPASoap.StatisticaPagamentiOutType;
import it.eldasoft.www.sil.WSPagoPASoap.StatisticaPagamentiOutTypeStatisticaEntry;
import it.maggioli.eldasoft.nso.client.model.Filter;
import it.maggioli.eldasoft.nso.client.model.Filter.OrderStatusEnum;
import it.maggioli.eldasoft.nso.client.model.NsoWsOrder;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti.LoggedUserInfo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.BandiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa.ws.WsPagoPATableWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.IOrdiniManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Esegue le preoperazioni necessarie all'apertura della pagina dell'area
 * personale.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.6
 */
public class AreaPersonaleAction extends BaseAction implements SessionAware, ServletContextAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2541550486944052191L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, Object> session;
	private ServletContext servletContext;
	
	private IPageManager pageManager;
	private IBandiManager bandiManager;
	private ICataloghiManager cataloghiManager;
	private IUserManager userManager;
	private IAppParamManager appParamManager;
	private IOrdiniManager ordiniManager;
	private WsPagoPATableWrapper wsPagoPATableWrapper;

	private boolean showRichiestaAssistenza;
	private boolean showBandi;
	private boolean showRichOfferta;
	private boolean showComprovaRequisiti;	
	private boolean showProcAggiudicazione;
	private boolean showAsteInCorso;
	private boolean showElenchi;
	private boolean showIscrizioneElenco;			// mostra il link "iscriviti a elenco operatori"
	private boolean showIscrivitiElenco;			// mostra il link "Attenzione: non sei ancora iscritto ad alcun elenco operatori. Iscriviti"
	private String singoloCodiceElenco;	
	private List<BandoIscrizioneType> elenchi;
	private boolean showMonitoraggio;
	private boolean showSbloccaAccount;
	private boolean showCataloghi;
	private boolean showIscrizioneCatalogo;			// mostra il link "iscriviti a catalogo"
	private boolean showIscrivitiCatalogo;			// mostra il link "Attenzione: non sei ancora iscritto ad alcun catalogo. Iscriviti"
	private String singoloCodiceCatalogo;			
	private List<BandoIscrizioneType> cataloghi;
	private boolean showEOrders;
	private boolean showBandiAcqPriv;
	private boolean showBandiVenPriv;
	private boolean showAbilitaAccessoCon;
	private boolean showEliminaLinkAccountSSo;
	private boolean showContratti;
	private boolean showContrattiLFS;
	
	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numSoccorsiIstruttori;
	private int numSoccorsiIstruttoriDaLeggere;
	private int numComunicazioniInviate;
	private int numAsteInCorso;
	private int numEOrdersDaValutare;
	private int numEOrdersConfermati;

	//PagoPA
	private boolean showPagoPA;
	private Long numPagoPAFatti;
	private Long numPagoPADaFare;
	private boolean showVendorRating;
	private  VendorRatingType vendorRating;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}
	
	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setOrdiniManager(IOrdiniManager ordiniManager) {
		this.ordiniManager = ordiniManager;
	}

	public boolean isShowRichiestaAssistenza() {
		return showRichiestaAssistenza;
	}

	public boolean isShowBandi() {
		return showBandi;
	}

	public boolean isShowRichOfferta() {
		return showRichOfferta;
	}

	public boolean isShowComprovaRequisiti() {
		return showComprovaRequisiti;
	}
	
	public boolean isShowAsteInCorso() {
		return showAsteInCorso;
	}
	
	public boolean isShowMonitoraggio() {
		return showMonitoraggio;
	}
	
	public boolean isShowSbloccaAccount() {
		return showSbloccaAccount;
	}
	
	public boolean isShowAbilitaAccessoCon() {
		return showAbilitaAccessoCon;
	}

	public boolean isShowContratti() {
		return showContratti;
	}

	public void setShowContratti(boolean showContratti) {
		this.showContratti = showContratti;
	}
	
	public boolean isShowContrattiLFS() {
		return showContrattiLFS;
	}

	public void setShowContrattiLFS(boolean showContrattiLFS) {
		this.showContrattiLFS = showContrattiLFS;
	}

	public boolean isShowEliminaLinkAccountSSo() {
		return showEliminaLinkAccountSSo;
	}

	public void setShowEliminaLinkAccountSSo(boolean showEliminaLinkAccountSSo) {
		this.showEliminaLinkAccountSSo = showEliminaLinkAccountSSo;
	}

	public int getNumAsteInCorso() {
		return numAsteInCorso;
	}

	public void setNumAsteInCorso(int numAsteInCorso) {
		this.numAsteInCorso = numAsteInCorso;
	}

	public boolean isShowProcAggiudicazione() {
		return showProcAggiudicazione;
	}

	public boolean isShowElenchi() {
		return showElenchi;
	}
		
	public boolean isShowIscrizioneElenco() {
		return showIscrizioneElenco;
	}

	public void setShowIscrizioneElenco(boolean showIscrizioneElenco) {
		this.showIscrizioneElenco = showIscrizioneElenco;
	}
	
	public boolean isShowIscrivitiElenco() {
		return showIscrivitiElenco;
	}

	public void setShowIscrivitiElenco(boolean showIscrivitiElenco) {
		this.showIscrivitiElenco = showIscrivitiElenco;
	}

	public String getSingoloCodiceElenco() {
		return singoloCodiceElenco;
	}

	public void setSingoloCodiceElenco(String singoloCodiceElenco) {
		this.singoloCodiceElenco = singoloCodiceElenco;
	}

	public List<BandoIscrizioneType> getElenchi() {
		return elenchi;
	}

	public boolean isShowCataloghi() {
		return showCataloghi;
	}
	
	public boolean isShowIscrizioneCatalogo() {
		return showIscrizioneCatalogo;
	}

	public void setShowIscrizioneCatalogo(boolean showIscrizioneCatalogo) {
		this.showIscrizioneCatalogo = showIscrizioneCatalogo;
	}

	public boolean isShowIscrivitiCatalogo() {
		return showIscrivitiCatalogo;
	}

	public void setShowIscrivitiCatalogo(boolean showIscrivitiCatalogo) {
		this.showIscrivitiCatalogo = showIscrivitiCatalogo;
	}

	public String getSingoloCodiceCatalogo() {
		return singoloCodiceCatalogo;
	}

	public void setSingoloCodiceCatalogo(String singoloCodiceCatalogo) {
		this.singoloCodiceCatalogo = singoloCodiceCatalogo;
	}

	public List<BandoIscrizioneType> getCataloghi() {
		return cataloghi;
	}
	
	public boolean isShowBandiAcqPriv() {
		return showBandiAcqPriv;
	}

	public void setShowBandiAcqPriv(boolean showBandiAcqPriv) {
		this.showBandiAcqPriv = showBandiAcqPriv;
	}

	public boolean isShowBandiVenPriv() {
		return showBandiVenPriv;
	}

	public void setShowBandiVenPriv(boolean showBandiVenPriv) {
		this.showBandiVenPriv = showBandiVenPriv;
	}

	public int getNumComunicazioniRicevute() {
		return numComunicazioniRicevute;
	}

	public void setNumComunicazioniRicevute(int numComunicazioniRicevute) {
		this.numComunicazioniRicevute = numComunicazioniRicevute;
	}

	public int getNumComunicazioniRicevuteDaLeggere() {
		return numComunicazioniRicevuteDaLeggere;
	}

	public void setNumComunicazioniRicevuteDaLeggere(int numComunicazioniRicevuteDaLeggere) {
		this.numComunicazioniRicevuteDaLeggere = numComunicazioniRicevuteDaLeggere;
	}

	public int getNumComunicazioniArchiviate() {
		return numComunicazioniArchiviate;
	}

	public void setNumComunicazioniArchiviate(int numComunicazioniArchiviate) {
		this.numComunicazioniArchiviate = numComunicazioniArchiviate;
	}

	public int getNumComunicazioniArchiviateDaLeggere() {
		return numComunicazioniArchiviateDaLeggere;
	}

	public void setNumComunicazioniArchiviateDaLeggere(int numComunicazioniArchiviateDaLeggere) {
		this.numComunicazioniArchiviateDaLeggere = numComunicazioniArchiviateDaLeggere;
	}
	
	public int getNumSoccorsiIstruttori() {
		return numSoccorsiIstruttori;
	}

	public void setNumSoccorsiIstruttori(int numSoccorsiIstruttori) {
		this.numSoccorsiIstruttori = numSoccorsiIstruttori;
	}
	
	public int getNumSoccorsiIstruttoriDaLeggere() {
		return numSoccorsiIstruttoriDaLeggere;
	}

	public void setNumSoccorsiIstruttoriDaLeggere(int numSoccorsiIstruttoriDaLeggere) {
		this.numSoccorsiIstruttoriDaLeggere = numSoccorsiIstruttoriDaLeggere;
	}

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}

	public void setNumComunicazioniInviate(int numComunicazioniInviate) {
		this.numComunicazioniInviate = numComunicazioniInviate;
	}
	
	public void setWsPagoPATableWrapper(WsPagoPATableWrapper wsPagoPATableWrapper) {
		this.wsPagoPATableWrapper = wsPagoPATableWrapper;
	}

	public boolean isShowEOrders() {
		return showEOrders;
	}

	public int getNumEOrdersDaValutare() {
		return numEOrdersDaValutare;
	}

	public void setNumEOrdersDaValutare(int numEOrdersDaValutare) {
		this.numEOrdersDaValutare = numEOrdersDaValutare;
	}

	public int getNumEOrdersConfermati() {
		return numEOrdersConfermati;
	}

	public void setNumEOrdersConfermati(int numEOrdersConfermati) {
		this.numEOrdersConfermati = numEOrdersConfermati;
	}

	public boolean isShowPagoPA() {
		return showPagoPA;
	}

	public Long getNumPagoPAFatti() {
		return numPagoPAFatti;
	}

	public Long getNumPagoPADaFare() {
		return numPagoPADaFare;
	}

	public boolean isShowVendorRating() {
		return showVendorRating;
	}

	public void setShowVendorRating(boolean showVendorRating) {
		this.showVendorRating = showVendorRating;
	}

	public VendorRatingType getVendorRating() {
		return vendorRating;
	}

	public void setVendorRating(VendorRatingType vendorRating) {
		this.vendorRating = vendorRating;
	}
	
	/**
	 * visualizza le sezioni dell'area personale (Profilo, Procedure di interesse, 
	 * Mercato elettronico, Ordini, Servizi, Elenchi operatori economici, ...) 
	 */
	public String view() {
		String target = SUCCESS;

		// funzionalita' disponibile solo per utenti loggati
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{	
			AreaPersonaleAction.checkAltriAccessiUtente(this.servletContext, this);

			try {
				boolean isUserAdmin = this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE);
				
				// verifica quali pagine/sezioni sono visibili/disponibili in area personale...
				
				//********************************************************************************
				// richiesta di assistenza
				IPage pgRichiestaAssistenza = this.pageManager.getPage("ppgare_doc_assistenza_tecnica");
				this.showRichiestaAssistenza = pgRichiestaAssistenza.isShowable();

				//********************************************************************************
				// gestione dei bandi
				IPage pgBandiGara = this.pageManager.getPage("ppgare_bandi_lista");
				this.showBandi = pgBandiGara.isShowable();
				
				//********************************************************************************
				// procedure di acq/ven di interesse (ppgare_acq_reg_priv_scaduti, ppgare_vend_reg_priv_scaduti)
				IPage pgBandiGaraAcqRegPriv = this.pageManager.getPage("ppgare_acq_reg_priv");
				this.showBandiAcqPriv = pgBandiGaraAcqRegPriv.isShowable();
				
				IPage pgBandiGaraVenRegPriv = this.pageManager.getPage("ppgare_vend_reg_priv");
				this.showBandiVenPriv = pgBandiGaraVenRegPriv.isShowable();

				//********************************************************************************
				// richieste d'offerta
				IPage pgRichiesteOfferta = this.pageManager.getPage("ppgare_vai_a_rich_offerta");
				this.showRichOfferta = pgRichiesteOfferta.isShowable();

				//********************************************************************************
				// generazione dei barcode
				IPage pgComprovaRequisiti = this.pageManager.getPage("ppgare_vai_a_rich_documenti");
				this.showComprovaRequisiti = pgComprovaRequisiti.isShowable();
				
				//********************************************************************************
				// aste elettroniche in corso
				IPage pgAsteInCorso = this.pageManager.getPage("ppgare_vai_a_aste_corso");
				this.showAsteInCorso = pgAsteInCorso.isShowable();
				
				int asteInCorso = 0;
				if (this.showAsteInCorso) {
					BandiSearchBean search = new BandiSearchBean();
					search.setTokenRichiedente(getCurrentUser().getUsername());
					asteInCorso = bandiManager.countBandiPerAsteInCorso(search);
					if (asteInCorso <= 0) {
						this.showAsteInCorso = false;
					}
				}
				this.setNumAsteInCorso(asteInCorso);
				
				//********************************************************************************
				// monitoraggio
				this.showMonitoraggio = this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE);

				//********************************************************************************
				// SSO
				AccountSSO soggettoSSO = (AccountSSO)this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
				List<String> autenticazioni = this.appParamManager.loadEnabledAuthentications();
				
				boolean ssoEnabled = false;
				for (String autenticazione : autenticazioni) {
					if(!PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_FORM.equals(autenticazione)){
						ssoEnabled = true;
					}
				}
				
				boolean sso = soggettoSSO != null;
				this.showSbloccaAccount = this.showMonitoraggio && this.userManager.isEnabledPrivacyModule() && !sso;
				this.showAbilitaAccessoCon = ssoEnabled  
						&& StringUtils.isEmpty(this.getCurrentUser().getDelegateUser()) 
						&& !sso 
						&& !this.showMonitoraggio;
				
				this.showEliminaLinkAccountSSo = !StringUtils.isEmpty(this.getCurrentUser().getDelegateUser()) 
						&& !sso 
						&& !this.showMonitoraggio;
				
				//********************************************************************************
				// procedure di aggiudicazione
				IPage pgProcAggiudicazione = this.pageManager.getPage("ppgare_vai_a_proc_aggiudicaz");
				this.showProcAggiudicazione = pgProcAggiudicazione.isShowable();
				
				//********************************************************************************
				// elenchi operatore
				IPage pgElenchi = this.pageManager.getPage("ppgare_oper_economici");
				this.showElenchi = pgElenchi.isShowable();
				showIscrizioneElenco = false;
				showIscrivitiElenco = false;
				singoloCodiceElenco = null;
				if (this.showElenchi) {
					// se esiste la gestione degli elenchi, si reperiscono gli
					// elenchi per i quali l'utente risulta iscritto
					elenchi = bandiManager.searchBandiIscrizione(this.getCurrentUser().getUsername(), null, true);
					int n = (elenchi != null ? elenchi.size() : 0); 
					if (n <= 0) {
						// si visualizza il link "Attenzione: non sei ancora iscritto ad alcun elenco operatori. Iscriviti"
						List<BandoIscrizioneType> listElenchi = bandiManager.getElencoBandiIscrizione(true);
						if(listElenchi != null) {
							showIscrivitiElenco = true;
							if(listElenchi.size() == 1) {
								// se esiste 1 solo elenco allora rendi disponibile il codice
								singoloCodiceElenco = listElenchi.get(0).getCodice();
							} else {
								// 0 o >1 elenchi, link alla lista degli elenchi
								elenchi = listElenchi;
							}
						}
					} else {
	                    // prepara il link "iscriviti a elenco operatori"
	                    int numIscrizioni = 0;
	                    for(int i = 0; i < elenchi.size(); i++) {
	                        if(elenchi.get(i).getFase() == 1 &&
	                           !"1".equals(elenchi.get(i).getStato()) && !"2".equals(elenchi.get(i).getStato())) 
	                        {
	                            numIscrizioni++; 
	                        }
	                    }
	                    showIscrizioneElenco = (numIscrizioni == 0);
					}
				}
				
				//********************************************************************************
				// cataloghi mercato elettronico
				IPage pgCataloghi = this.pageManager.getPage("ppgare_cataloghi");
				this.showCataloghi = pgCataloghi.isShowable();
				showIscrizioneCatalogo = false;
				showIscrivitiCatalogo = false;
				singoloCodiceCatalogo = null;
				if (this.showCataloghi) {
					// se esiste la gestione dei cataloghi, si reperiscono i
					// cataloghi per i quali l'utente risulta iscritto
					cataloghi = cataloghiManager.searchCataloghi(this.getCurrentUser().getUsername(), null, true);
					int n = (cataloghi != null ? cataloghi.size() : 0); 
					if (n <= 0) {
						// si visualizza il link "Attenzione: non sei ancora iscritto ad alcun catalogo. Iscriviti"
						List<BandoIscrizioneType> listCataloghi = cataloghiManager.getElencoCataloghi(true);
						if(listCataloghi != null) {
							showIscrivitiCatalogo = true;
							if(listCataloghi.size() == 1) {
								// se esiste 1 solo elenco allora rendi disponibile il codice
								singoloCodiceCatalogo = listCataloghi.get(0).getCodice();
							} else {
								// 0 o >1 elenchi, link alla lista degli elenchi
								cataloghi = listCataloghi;
							}
						}
					} else {
	                    // prepara il link "iscriviti a catalogo"
	                    int numIscrizioni = 0;
	                    for(int i = 0; i < cataloghi.size(); i++) {
	                        if(cataloghi.get(i).getFase() == 1 &&
	                           !"1".equals(cataloghi.get(i).getStato()) && !"2".equals(cataloghi.get(i).getStato())) 
	                        {
	                            numIscrizioni++; 
	                        }
	                    }
	                    showIscrizioneCatalogo = (numIscrizioni == 0);	                    
					}
				}
				
				//********************************************************************************
				// contratti
				IPage pgContratti = this.pageManager.getPage("ppcommon_contracts");
				this.showContratti = pgContratti.isShowable();
				
				//********************************************************************************
				// contratti LFS
				IPage pgContrattiLFS = this.pageManager.getPage("ppgare_contratti_lfs_lista");
				this.showContrattiLFS = pgContrattiLFS.isShowable();
				
				//********************************************************************************
				// eOrders (CEF EInvoicing PORTAPPALT-63)
				String codimp = (String) this.session.get(PortGareSystemConstants.SESSION_ID_IMPRESA);
				try {
					if(StringUtils.isEmpty(codimp)) {
						codimp = this.bandiManager.getIdImpresa(this.getCurrentUser().getUsername());
						this.session.put(PortGareSystemConstants.SESSION_ID_IMPRESA, codimp);
					}
				} catch(Exception e) {
					// in case di user "admin" id impresa non esiste  
					codimp = null;
				}
				
				IPage pgEOrders = this.pageManager.getPage("ppgare_eorders");
				this.showEOrders = (pgEOrders != null ? pgEOrders.isShowable() : false);
				
				if (this.showEOrders && codimp != null && !isUserAdmin) {
					this.numEOrdersDaValutare = 0;
					this.numEOrdersConfermati = 0;

					Filter filtri = new Filter();
					SearchResult<NsoWsOrder> eorders;
	
					// calcola gli ordini "da valutare" come IN_ATTESA_CONFERMA(2)
//					filtri.setOrderStatus(OrderStatusEnum.IN_ATTESA_CONFERMA.getValue());
					filtri.setOrderStatus(OrderStatusEnum.NUMBER_2);
					eorders = this.ordiniManager.getPagedOrderListFO(codimp, 1, 1, filtri);
					if (eorders != null) {
						this.numEOrdersDaValutare += eorders.getNumTotaleRecord();
					}
	
					// calcola gli ordini "confermati" come ACCETTATI(6) +
					// ACCETTATI_AUTOMATICAMENTE(7)
//					filtri.setOrderStatus(OrderStatusEnum.ACCETTATO.getValue());
					filtri.setOrderStatus(OrderStatusEnum.NUMBER_6);
					eorders = this.ordiniManager.getPagedOrderListFO(codimp, 1, 1, filtri);
					if (eorders != null) {
						this.numEOrdersConfermati += eorders.getNumTotaleRecord();
					}
//					filtri.setOrderStatus(OrderStatusEnum.ACCETTATO_AUTOMATICAMENTE.getValue());
					filtri.setOrderStatus(OrderStatusEnum.NUMBER_7);
					eorders = this.ordiniManager.getPagedOrderListFO(codimp, 1, 1, filtri);
					if (eorders != null) {
						this.numEOrdersConfermati += eorders.getNumTotaleRecord();
					}
				}
				
				//********************************************************************************
				// Pago PA
				IPage pgPagoPa = this.pageManager.getPage("ppgare_pagopa");
				this.showPagoPA = (pgPagoPa != null ? pgPagoPa.isShowable() : false);
				this.numPagoPAFatti = 0L;
				this.numPagoPADaFare = 0L;
				if(this.showPagoPA && !isUserAdmin) {
					try {
						logger.debug("Chiamare WSAppalti per ottenere i dati dalle tabelle.");
						StatisticaPagamentiOutType outStat = wsPagoPATableWrapper.getProxyWsPagoPa().getStatisticaPagamenti(codimp);
						// ottenere i tabellati corrispondenti
						/*
						 * A1190	1	Bozza
						 * A1190	2	Da effettuare
						 * A1190	3	Effettuato
						 */
						if(outStat!=null) {
							// avere i dati dalle statistiche (i dati in bozza sono esclusi)
							for(StatisticaPagamentiOutTypeStatisticaEntry entry : outStat.getStatistica()) {
								if(entry.getKey().intValue()==2) {
									this.numPagoPADaFare = entry.getValue();
								} else if(entry.getKey().intValue()==3) {
									this.numPagoPAFatti = entry.getValue();
								}
							}
						}
					} catch (Exception e) {
						logger.error("Errore con PagoPA.",e);
					}
				}
				logger.debug("showPagoPA: {}, numPagoPAFatti: {}, numPagoPADaFare: {}",showPagoPA,numPagoPAFatti,numPagoPADaFare);

				//********************************************************************************
				// comunicazioni (inviate, ricevute, archiviate)
				StatisticheComunicazioniPersonaliType stat = this.bandiManager.getStatisticheComunicazioniPersonali(
						this.getCurrentUser().getUsername(), 
						null,
						null,
						null);
				this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
				this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
				this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
				this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
				this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
				this.setNumSoccorsiIstruttoriDaLeggere(stat.getNumSoccorsiIstruttoriDaLeggere());
				this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO);

				//********************************************************************************
				// vendor rating
				if(!isUserAdmin) {
					VendorRatingType vendorRating = bandiManager.getVendorRating(getCurrentUser().getUsername(), new Date());
					this.setShowVendorRating(vendorRating != null ? true : false);
				}

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "view");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}

		return target;
	}

	/**
	 * Controlla quanti accessi utilizzando la login dell'impresa ci sono in contemporanea nel portale.
	 */
	static void checkAltriAccessiUtente(ServletContext servletContext, BaseAction action) {
		int contaAccessiMiaUtenza = 0;
		Map<String, LoggedUserInfo> utentiConnessi = TrackerSessioniUtenti.getInstance(servletContext).getDatiSessioniUtentiConnessi();
		AccountSSO mioSoggettoFisico = (AccountSSO) action.getRequest().getSession().getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		for (String sessionId : utentiConnessi.keySet()) {
			LoggedUserInfo info = utentiConnessi.get(sessionId);
			// controllo se l'utente estratto e' l'utente autenticatosi mediante single sign on oppure con login/password 
			if ((mioSoggettoFisico != null && mioSoggettoFisico.getLogin().equals(info.getLogin())) 
				|| action.getCurrentUser().getUsername().equals(info.getLogin()))
				contaAccessiMiaUtenza++;
		}
		if (action.getCurrentUser().getUsername().equals(SystemConstants.ADMIN_USER_NAME) && contaAccessiMiaUtenza > 1) {
			// si limita la segnalazione ai soli utenti amministratori
			action.addActionMessage(action.getText("Warnings.accessiConcorrentiUtente", new String[] {
								Integer.toString(contaAccessiMiaUtenza-1) }));
		}
	}
	
}