package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.nso.client.model.Filter;
import it.maggioli.eldasoft.nso.client.model.Filter.OrderStatusEnum;
import it.maggioli.eldasoft.nso.client.model.NsoWsOrder;
import it.maggioli.eldasoft.nso.client.model.ResultCountNsoWsOrderBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.IOrdiniManager;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.web.context.ServletContextAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;

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

	private Map<String, Object> session;
	private ServletContext servletContext;
	
	private IPageManager pageManager;
	private IBandiManager bandiManager;
	private ICataloghiManager cataloghiManager;
	private IUserManager userManager;
	private IAppParamManager appParamManager;
	private IOrdiniManager ordiniManager;

	private boolean showRichiestaAssistenza;
	private boolean showBandi;
	private boolean showRichOfferta;
	private boolean showComprovaRequisiti;	
	private boolean showProcAggiudicazione;
	private boolean showAsteInCorso;
	private boolean showElenchi;
	private boolean showMonitoraggio;
	private boolean showSbloccaAccount;
	private List<BandoIscrizioneType> elenchi;
	private boolean showCataloghi;
	private List<BandoIscrizioneType> cataloghi;
	private boolean showEOrders;
	private boolean showBandiAcqPriv;
	private boolean showBandiVenPriv;
	private boolean showAbilitaAccessoCon;
	
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

	public List<BandoIscrizioneType> getElenchi() {
		return elenchi;
	}

	public boolean isShowCataloghi() {
		return showCataloghi;
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
				//si verifica se la pagina di richiesta assistenza e' disponibile
				IPage pgRichiestaAssistenza = this.pageManager
						.getPage("ppgare_doc_assistenza_tecnica");
				this.showRichiestaAssistenza = pgRichiestaAssistenza.isShowable();

				// si verifica se esiste la gestione dei bandi
				IPage pgBandiGara = this.pageManager
						.getPage("ppgare_bandi_lista");
				this.showBandi = pgBandiGara.isShowable();
				
				// gestione delle funzioni per le procedure di acq/ven di interesse
				// ppgare_acq_reg_priv_scaduti, ppgare_vend_reg_priv_scaduti
				IPage pgBandiGaraAcqRegPriv = this.pageManager
						.getPage("ppgare_acq_reg_priv");
				this.showBandiAcqPriv = pgBandiGaraAcqRegPriv.isShowable();
				
				IPage pgBandiGaraVenRegPriv = this.pageManager
						.getPage("ppgare_vend_reg_priv");
				this.showBandiVenPriv = pgBandiGaraVenRegPriv.isShowable();

				// gestione delle funzioni per la generazione dei barcode o accesso a gare telematiche
				IPage pgRichiesteOfferta = this.pageManager
						.getPage("ppgare_vai_a_rich_offerta");
				this.showRichOfferta = pgRichiesteOfferta.isShowable();

				IPage pgComprovaRequisiti = this.pageManager
						.getPage("ppgare_vai_a_rich_documenti");
				this.showComprovaRequisiti = pgComprovaRequisiti
						.isShowable();
				
				// verifica se esistono aste elettroniche in corso...
				IPage pgAsteInCorso = this.pageManager
						.getPage("ppgare_vai_a_aste_corso");
				this.showAsteInCorso = pgAsteInCorso.isShowable();
				
				int asteInCorso = 0;
				if (this.showAsteInCorso) {
					asteInCorso = this.bandiManager.countBandiPerAsteInCorso(
							this.getCurrentUser().getUsername(), 
							null, null, null, null, null, null, null, null);
					if (asteInCorso <= 0) {
						this.showAsteInCorso = false;
					}
				}
				this.setNumAsteInCorso(asteInCorso);
				
				this.showMonitoraggio = this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE);
				
				Integer configurazioneSSOProtocollo = (Integer)appParamManager.getConfigurationValue("sso.protocollo");
				boolean sso = (configurazioneSSOProtocollo != null &&
						       (1==configurazioneSSOProtocollo.intValue() || 2==configurazioneSSOProtocollo.intValue()));
				this.showSbloccaAccount = this.showMonitoraggio && this.userManager.isEnabledPrivacyModule() && !sso;
				
				this.showAbilitaAccessoCon = 
					(configurazioneSSOProtocollo != null && 3==configurazioneSSOProtocollo.intValue() &&
				     StringUtils.isEmpty(this.getCurrentUser().getDelegateUser()));
				
				IPage pgProcAggiudicazione = this.pageManager
						.getPage("ppgare_vai_a_proc_aggiudicaz");
				this.showProcAggiudicazione = pgProcAggiudicazione.isShowable();

				
				// si verifica se esiste la gestione degli elenchi
				IPage pgElenchi = this.pageManager
						.getPage("ppgare_oper_economici");
				this.showElenchi = pgElenchi.isShowable();

				if (this.showElenchi) {
					// se esiste la gestione degli elenchi, si reperiscono gli
					// elenchi per i quali l'utente risulta iscritto
					this.elenchi = this.bandiManager.searchBandiIscrizione(this
							.getCurrentUser().getUsername(), null);
					if (this.elenchi.size() == 0) {
						this.showElenchi = false;
					}
				}

				// si verifica se esiste la gestione dei cataloghi
				IPage pgCataloghi = this.pageManager
						.getPage("ppgare_cataloghi");
				this.showCataloghi = pgCataloghi.isShowable();

				if (this.showCataloghi) {
					// se esiste la gestione dei cataloghi, si reperiscono i
					// cataloghi per i quali l'utente risulta iscritto
					this.cataloghi = this.cataloghiManager.searchCataloghi(this
							.getCurrentUser().getUsername(), null);
					if (this.cataloghi.size() == 0) {
						this.showCataloghi = false;
					}
				}
				
				// si verifica se esiste la gestione eOrders (CEF EInvoicing PORTAPPALT-63)
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
				
				IPage pgEOrders = this.pageManager
						.getPage("ppgare_eorders");
				this.showEOrders = (pgEOrders != null ? pgEOrders.isShowable() : false);
				
				if (this.showEOrders && codimp != null) {
					this.numEOrdersDaValutare = 0;
					this.numEOrdersConfermati = 0;
					try {
						Filter filtri = new Filter();
						SearchResult<NsoWsOrder> eorders;
						
						// calcola gli ordini "da valutare" come IN_ATTESA_CONFERMA(2)
						filtri.setOrderStatus( OrderStatusEnum.IN_ATTESA_CONFERMA.getValue() );
						eorders = this.ordiniManager.getPagedOrderListFO(codimp, 1, 1, filtri);
						if(eorders != null) {
							this.numEOrdersDaValutare += eorders.getNumTotaleRecord();
						}
				
						// calcola gli ordini "confermati" come ACCETTATI(6) + ACCETTATI_AUTOMATICAMENTE(7)
						filtri.setOrderStatus( OrderStatusEnum.ACCETTATO.getValue() );
						eorders = this.ordiniManager.getPagedOrderListFO(codimp, 1, 1, filtri);
						if(eorders != null) {
							this.numEOrdersConfermati += eorders.getNumTotaleRecord();
						}

						filtri.setOrderStatus( OrderStatusEnum.ACCETTATO_AUTOMATICAMENTE.getValue() );
						eorders = this.ordiniManager.getPagedOrderListFO(codimp, 1, 1, filtri);
						if(eorders != null) {
							this.numEOrdersConfermati += eorders.getNumTotaleRecord();
						}
					} catch(Exception e) {
					}
				}

				StatisticheComunicazioniPersonaliType stat = this.bandiManager.getStatisticheComunicazioniPersonali(
						this.getCurrentUser().getUsername(), null);
				this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
				this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
				this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
				this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
				this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
				this.setNumSoccorsiIstruttoriDaLeggere(stat.getNumSoccorsiIstruttoriDaLeggere());
				this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA);
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO);
				
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
	 * 
	 */
	static void checkAltriAccessiUtente(ServletContext servletContext, BaseAction action) {
		int contaAccessiMiaUtenza = 0;
		Map<String, String[]> utentiConnessi = TrackerSessioniUtenti.getInstance(servletContext).getDatiSessioniUtentiConnessi();
		AccountSSO mioSoggettoFisico = (AccountSSO) action.getRequest().getSession().getAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		for (String sessionId : utentiConnessi.keySet()) {
			String[] datiUtente = utentiConnessi.get(sessionId);
			// controllo se l'utente estratto e' l'utente autenticatosi mediante single sign on oppure con login/password 
			if ((mioSoggettoFisico != null && mioSoggettoFisico.getLogin().equals(datiUtente[1])) || action.getCurrentUser().getUsername().equals(datiUtente[1]))
				contaAccessiMiaUtenza++;
		}
		if (action.getCurrentUser().getUsername().equals(SystemConstants.ADMIN_USER_NAME) && contaAccessiMiaUtenza > 1) {
			// si limita la segnalazione ai soli utenti amministratori
			action.addActionMessage(action.getText("Warnings.accessiConcorrentiUtente", new String[] {
								Integer.toString(contaAccessiMiaUtenza-1) }));
		}
	}
}