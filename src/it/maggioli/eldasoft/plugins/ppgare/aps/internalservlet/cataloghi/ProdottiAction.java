package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.eldasoft.sil.portgare.datatypes.GestioneProdottiDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Action per le operazioni sui cataloghi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @author Marco Perazzetta
 * @since 1.8.6
 */
public class ProdottiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8145031459928348024L;

	private ICataloghiManager cataloghiManager;
	private IBandiManager bandiManager;
	private IDocumentiDigitaliManager documentiDigitaliManager;
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;

	/**
	 * nel caso di prodotto a sistema, "prodottoId" e' l'id del prodotto nel db,
	 * nel caso di prodotto in carrello e' la posizione relativa nella lista
	 * coerente con il suo stato
	 */
	private Long prodottoId;
	private WizardProdottoHelper prodotto;
	private boolean inCarrello;
	private Map<String, Object> session;
	private String statoProdotto;
	private boolean undo;
	private boolean delete;
	private boolean reinsert;
	private String catalogo;
	private String descrizioneArticolo;
	private String nomeProdotto;
	private Integer tipoProdotto;
	private boolean deleted;
	private boolean fromDetail;
	private boolean modificheInviate;
	private boolean undoAll;

	private String multipartSaveDir;

	private boolean variazioneOfferta;

	
	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
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

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setProdottoId(Long prodottoId) {
		this.prodottoId = prodottoId;
	}

	public Long getProdottoId() {
		return prodottoId;
	}

	public WizardProdottoHelper getProdotto() {
		return prodotto;
	}

	public void setProdotto(WizardProdottoHelper prodotto) {
		this.prodotto = prodotto;
	}

	public boolean getInCarrello() {
		return inCarrello;
	}

	public void setInCarrello(boolean inCarrello) {
		this.inCarrello = inCarrello;
	}

	public String getStatoProdotto() {
		return statoProdotto;
	}

	public void setStatoProdotto(String statoProdotto) {
		this.statoProdotto = statoProdotto;
	}
	
	public boolean isUndo() {
		return undo;
	}

	public void setUndo(boolean undo) {
		this.undo = undo;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public String getDescrizioneArticolo() {
		return descrizioneArticolo;
	}

	public void setDescrizioneArticolo(String descrizioneArticolo) {
		this.descrizioneArticolo = descrizioneArticolo;
	}

	public String getNomeProdotto() {
		return nomeProdotto;
	}

	public void setNomeProdotto(String nomeProdotto) {
		this.nomeProdotto = nomeProdotto;
	}

	public Integer getTipoProdotto() {
		return tipoProdotto;
	}

	public void setTipoProdotto(Integer tipoProdotto) {
		this.tipoProdotto = tipoProdotto;
	}

	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	public boolean isReinsert() {
		return reinsert;
	}

	public void setReinsert(boolean reinsert) {
		this.reinsert = reinsert;
	}

	public void setDocumentiDigitaliManager(IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isFromDetail() {
		return fromDetail;
	}

	public void setFromDetail(boolean fromDetail) {
		this.fromDetail = fromDetail;
	}

	public boolean isModificheInviate() {
		return modificheInviate;
	}

	public void setModificheInviate(boolean modificheInviate) {
		this.modificheInviate = modificheInviate;
	}

	public boolean isVariazioneOfferta() {
		return variazioneOfferta;
	}

	public void setVariazioneOfferta(boolean variazioneOfferta) {
		this.variazioneOfferta = variazioneOfferta;
	}

	public boolean isUndoAll() {
		return undoAll;
	}

	public void setUndoAll(boolean undoAll) {
		this.undoAll = undoAll;
	}

	
	public String getTAB_PRODOTTI_A_SISTEMA() {
		return CataloghiConstants.TAB_PRODOTTI_A_SISTEMA;
	}

	public String getTAB_PRODOTTI_NEL_CARRELLO() {
		return CataloghiConstants.TAB_PRODOTTI_NEL_CARRELLO;
	}

	public String getSTATO_PRODOTTO_IN_CATALOGO() {
		return CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO;
	}

	public String getPRODOTTO_INSERITO() {
		return CataloghiConstants.PRODOTTO_INSERITO;
	}

	public String getPRODOTTO_MODIFICATO() {
		return CataloghiConstants.PRODOTTO_MODIFICATO;
	}

	public String getPRODOTTO_ELIMINATO() {
		return CataloghiConstants.PRODOTTO_ELIMINATO;
	}

	public String getBOZZA() {
		return CataloghiConstants.BOZZA;
	}

	public Integer getTIPO_PRODOTTO_BENE() {
		return CataloghiConstants.TIPO_PRODOTTO_BENE;
	}

	public String getTIPO_PREZZO_UNITA_DI_MISURA() {
		return CataloghiConstants.TIPO_PREZZO_UNITA_DI_MISURA;
	}

	public String getTIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UM() {
		return CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA;
	}

	public String getSTATO_PRODOTTO_ARCHIVIATO() {
		return CataloghiConstants.STATO_PRODOTTO_ARCHIVIATO;
	}

	public Integer DEFAULT_NUM_MESI_GARANZIA() {
		return CataloghiConstants.DEFAULT_NUM_MESI_GARANZIA;
	}

	
	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio di
	 * prodotto presente a sistema o in carrello
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String viewProdotto() {
		this.setTarget(SUCCESS);
		
		try {
			if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.catalogo, 
						this.cataloghiManager);
				this.prodotto = WizardProdottoHelper.getInstance(
						this.session, 
						this.cataloghiManager, 
						carrelloProdotti, 
						this.getCurrentUser().getUsername(),
						carrelloProdotti.getCurrentCatalogo(), 
						this.statoProdotto, 
						this.inCarrello, 
						null, 
						this.prodottoId, 
						null, 
						CataloghiConstants.PAGINA_DETTAGLIO_PRODOTTO);
				ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				this.modificheInviate = prodotti.getIdComunicazioneModifiche() != null;
				this.variazioneOfferta = prodotti.isVariazioneOfferta(); // || prodotti.getIdComunicazioneVariazioneOfferta() != null;
			} else {
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Gestisce la richiesta di annullamento modifica di un prodotto
	 *
	 * @return il target a cui andare
	 */
	public String confirmUndoProdotto() {

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			
			this.catalogo = carrelloProdotti.getCurrentCatalogo();
			
			WizardProdottoHelper prodottoDaEliminare = null;
			ProdottiCatalogoSessione prodottiSessione = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
			
			if (CataloghiConstants.BOZZA.equals(this.statoProdotto)) {
				prodottoDaEliminare = prodottiSessione.getBozze().get(this.prodottoId.intValue());
			} else if (CataloghiConstants.PRODOTTO_ELIMINATO.equals(this.statoProdotto)) {
				prodottoDaEliminare = prodottiSessione.getProdottiEliminati().get(this.prodottoId.intValue());
			} else if (CataloghiConstants.PRODOTTO_INSERITO.equals(this.statoProdotto)) {
				prodottoDaEliminare = prodottiSessione.getProdottiInseriti().get(this.prodottoId.intValue());
			} else {
				prodottoDaEliminare = prodottiSessione.getProdottiModificati().get(this.prodottoId.intValue());
			}
			if (CataloghiConstants.TIPO_PRODOTTO_BENE.toString().equals(prodottoDaEliminare.getArticolo().getDettaglioArticolo().getTipo())) {
				this.nomeProdotto = prodottoDaEliminare.getDettaglioProdotto().getNomeCommerciale();
			} else {
				this.descrizioneArticolo = prodottoDaEliminare.getArticolo().getDettaglioArticolo().getDescrizione();
			}
			this.tipoProdotto = new Integer(prodottoDaEliminare.getArticolo().getDettaglioArticolo().getTipo());
			this.undo = true;
			
			if (this.fromDetail) {
				this.setTarget("dettaglioProdotto");
			} else {
				this.setTarget("prodottiCarrello");
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Gestisce la richiesta di elimazione di un prodotto in catalogo
	 *
	 * @return il target a cui andare
	 */
	public String confirmDeleteProdotto() {
		try {
			if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				ProdottoType prodottoDaEliminare = this.cataloghiManager.getProdotto(this.prodottoId);
				
				ArticoloType articolo = this.cataloghiManager.getArticolo(prodottoDaEliminare.getIdArticolo());
				
				if (CataloghiConstants.TIPO_PRODOTTO_BENE.toString().equals(articolo.getTipo())) {
					this.nomeProdotto = prodottoDaEliminare.getNomeCommerciale();
				} else {
					this.descrizioneArticolo = articolo.getDescrizione();
				}
				this.tipoProdotto = new Integer(articolo.getTipo());
				this.delete = true;
				
				this.setTarget("prodottiSistema");
			} else {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "confirmDeleteProdotto");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Esegue l'operazione di richiesta annullamento operazione su un prodotto in
	 * carrello
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String undoProdotto() {
		boolean procedi = true;
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			
			ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
			
			try {
				if (CataloghiConstants.PRODOTTO_INSERITO.equals(this.statoProdotto)) {
					prodotti.getProdottiInseriti().remove(this.prodottoId.intValue());
				} else if (CataloghiConstants.PRODOTTO_MODIFICATO.equals(this.statoProdotto)) {
					prodotti.getProdottiModificati().remove(this.prodottoId.intValue());
				} else if (CataloghiConstants.PRODOTTO_ELIMINATO.equals(this.statoProdotto)) {
					WizardProdottoHelper prodottoHelper = prodotti.getProdottiEliminati().get(this.prodottoId.intValue());
					Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(
							prodottoHelper.getDettaglioProdotto().getIdArticolo(),
							this.getCurrentUser().getUsername());
					long numeroProdottiOECarrello = carrelloProdotti.calculateProdottiCaricatiOE(prodottoHelper.getDettaglioProdotto().getIdArticolo());
					Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
					DettaglioBandoIscrizioneType catalogoBando = this.bandiManager.getDettaglioBandoIscrizione(carrelloProdotti.getCurrentCatalogo());
					//non si deve superare il numero massimo di prodotti inseribili per l'articolo individuato 
					if (prodottiCaricati >= catalogoBando.getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo()) {
						this.addActionError(this.getText("Errors.limiteInserimentoProdottiPerArticolo",
														 new String[] {catalogoBando.getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo() + ""}));
						procedi = false;
					} else {
						prodotti.getProdottiEliminati().remove(this.prodottoId.intValue());
					}
				} else {
					prodotti.getBozze().remove(this.prodottoId.intValue());
				}
				
				if (procedi) {
					prodotti.reindexProducts(this.statoProdotto);
					if (!prodotti.hasModifiche()) {
						prodotti.setVariazioneOfferta(false);
						//nel caso ci sia gia' una comunicazione FS7 in bozza la elimino
						if (prodotti.getIdComunicazioneBozza() != null) {
							this.comunicazioniManager.deleteComunicazione(
									CommonSystemConstants.ID_APPLICATIVO,
									prodotti.getIdComunicazioneBozza());
							prodotti.setIdComunicazioneBozza(null);
						}
					} else {
						saveProdotti(
								this.comunicazioniManager, 
								this.eventManager, 
								this.appParamManager, 
								prodotti, 
								this);
					}
				}
				this.setDeleted(true);
				this.setTarget("gestioneProdotti");
				
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "undoProdotto");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		this.undo = false;
		
		return this.getTarget();
	}

	/**
	 * Esegue l'operazione di richiesta eliminazione di un prodotto in catalogo
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String deleteProdotto() {
		try {
			if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.catalogo, 
						this.cataloghiManager);
				this.prodotto = WizardProdottoHelper.getInstance(
						this.session, 
						this.cataloghiManager, 
						carrelloProdotti, 
						this.getCurrentUser().getUsername(),
						carrelloProdotti.getCurrentCatalogo(), 
						this.statoProdotto, 
						this.inCarrello, 
						null, 
						this.prodottoId, 
						null, 
						null);
				this.prodotto.setStatoProdotto(CataloghiConstants.PRODOTTO_ELIMINATO);
				//Verifico che il prodotto non sia gia' presente nella lista delle eliminazioni
				ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				if (prodotti.prodottoGiaEliminato(this.prodottoId)) {
					this.addActionError(this.getText("Errors.prodottoAlreadyDeleted"));
				} else {
					prodotti.getProdottiEliminati().add(this.prodotto);
					prodotti.reindexProducts(CataloghiConstants.PRODOTTO_ELIMINATO);
					saveProdotti(this.comunicazioniManager, this.eventManager, this.appParamManager, prodotti, this);
					this.setDeleted(true);
				}
				this.setTarget("gestioneProdotti");
			} else {
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "deleteProdotto");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (IOException t) {
			ApsSystemUtils.logThrowable(t, this, "send");
			this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (OutOfMemoryError e) {
			ApsSystemUtils.logThrowable(e, this, "send");
			this.addActionError(this.getText("Errors.send.outOfMemory"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (XmlException t) {
			ApsSystemUtils.logThrowable(t, this, "send");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "send");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		this.delete = false;
		
		return this.getTarget();
	}

	/**
	 * ...
	 */
	public static void saveProdotti(
			IComunicazioniManager comunicazioniManager,
			IEventManager eventManager,
			IAppParamManager appParamManager,
			ProdottiCatalogoSessione prodottiDaInviare, 
			EncodedDataAction action) throws ApsException, IOException, OutOfMemoryError, XmlException 
	{	
		// --- CESSATI ---
		WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
				action.getCurrentUser().getUsername(), 
				action, 
				appParamManager);
		
		ComunicazioneType com = ProdottiAction.sendComunicazioneSaveProdotti(
				comunicazioniManager, 
				eventManager,
				prodottiDaInviare, 
				datiImpresaHelper, 
				action);
		
		if (prodottiDaInviare.getIdComunicazioneBozza() == null) {
			DettaglioComunicazioneType comunicazioneProdottiBozza = ComunicazioniUtilities.retrieveComunicazione(
					comunicazioniManager,
					action.getCurrentUser().getUsername(),
					prodottiDaInviare.getCodiceCatalogo(),
					null,
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
					PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO);
			prodottiDaInviare.setIdComunicazioneBozza(comunicazioneProdottiBozza.getId());					
		}
	}

	/**
	 * Effettua l'invio della comunicazione per il salvataggio delle sole bozze e
	 * poi ripulisce la sessione.
	 *
	 * @param datiModificheProdotti le modifiche ai prodotti
	 * @param impresa i dettagli dell'impresa
	 * @return 
	 * @throws IOException
	 * @throws ApsException
	 */
	private static ComunicazioneType sendComunicazioneSaveProdotti(
			IComunicazioniManager comunicazioniManager,
			IEventManager eventManager,
			ProdottiCatalogoSessione datiModificheProdotti, 
			WizardDatiImpresaHelper impresa,
			EncodedDataAction action) throws IOException, ApsException 
	{
		Event evento = new Event();
		evento.setUsername(action.getCurrentUser().getUsername());
		evento.setDestination(datiModificheProdotti.getCodiceCatalogo());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
		evento.setIpAddress(action.getCurrentUser().getIpAddress());
		evento.setSessionId(action.getRequest().getSession().getId());
		evento.setMessage("Salvataggio comunicazione "
						  + PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO
						  + " con id " + datiModificheProdotti.getIdComunicazioneBozza()
						  + " in stato " + CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

		ComunicazioneType comunicazione = null;
		try {
			String username = action.getCurrentUser().getUsername();
			String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
			String oggetto = action.formatI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SAVE_OGGETTO", new Object[] {ragioneSociale});
			String testo = action.formatI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SAVE_TESTO", new Object[] {ragioneSociale200, datiModificheProdotti.getCodiceCatalogo()});
			String descrizioneFile = action.getI18nLabelFromDefaultLocale("NOTIFICA_CATALOGHI_SAVE_ALLEGATO");
	
			comunicazione = ProdottiAction.sendComunicazione(
					comunicazioniManager,
					datiModificheProdotti,
					username, 
					ragioneSociale,
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
					PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO,
					oggetto, 
					testo, 
					PortGareSystemConstants.NOME_FILE_PRODOTTI_BOZZE,
					descrizioneFile, 
					null, 
					true, 
					new Date(),
					CataloghiConstants.COMUNICAZIONE_SALVATAGGIO_PRODOTTI);
			
			// visto l'esito con successo si arricchisce il messaggio di dettagli
			evento.setMessage(
					"Salvataggio comunicazione " + PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO
					+ " con id " + comunicazione.getDettaglioComunicazione().getId()
					+ " in stato " + comunicazione.getDettaglioComunicazione().getStato());

		} catch (IOException e) {
			evento.setError(e);
			throw e;
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} finally {
			if(evento != null ) {
				eventManager.insertEvent(evento);
			}
		}
		
		return comunicazione;			
	}

	/**
	 * Gestisce la richiesta di annullamento elimazione dell'immagine di un
	 * prodotto
	 *
	 * @return il target a cui andare
	 */
	public String cancelUndoProdotto() {
		this.undo = false;
		if (this.fromDetail) {
			this.setTarget("dettaglioProdotto");
		} else {
			this.setTarget("prodottiCarrello");
		}
		return this.getTarget();
	}

	/**
	 * Gestisce la richiesta di annullamento elimazione di un prodotto in catalogo
	 *
	 * @return il target a cui andare
	 */
	public String cancelDeleteProdotto() {
		this.delete = false;
		this.setTarget("prodottiSistema");
		return this.getTarget();
	}

	/**
	 * Gestisce la richiesta di reinseramento prodotto a catalogo
	 *
	 * @return il target a cui andare
	 */
	public String confirmReinsertProdotto() {
		this.setTarget("dettaglioProdotto");
		try {
			if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				this.reinsert = true;
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.catalogo, 
						this.cataloghiManager);
				this.catalogo = carrelloProdotti.getCurrentCatalogo();
			} else {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "confirmReinsertProdotto");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Esegue l'operazione di richiesta reinserimento prodotto nel catalogo
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String reinsertProdotto() {
		try {
			boolean controlliOk = true;
			if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				
				WizardProdottoHelper oldProdottoHelper = WizardProdottoHelper.getInstance(
						this.session, 
						this.cataloghiManager,
						carrelloProdotti, 
						this.getCurrentUser().getUsername(), 
						carrelloProdotti.getCurrentCatalogo(), 
						this.statoProdotto,
						this.inCarrello, 
						null, 
						this.prodottoId, 
						null, 
						null);
				
				String tempDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir).getAbsolutePath();
				WizardProdottoHelper prodottoHelper = oldProdottoHelper.clone(
						this.documentiDigitaliManager, 
						tempDir, 
						true, 
						carrelloProdotti,
						this.getCurrentUser().getUsername());
				
				Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(
						prodottoHelper.getDettaglioProdotto().getIdArticolo(),
						this.getCurrentUser().getUsername());
				long numeroProdottiOECarrello = carrelloProdotti.calculateProdottiCaricatiOE(prodottoHelper.getDettaglioProdotto().getIdArticolo());
				Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
				DettaglioBandoIscrizioneType catalogoBando = this.bandiManager.getDettaglioBandoIscrizione(carrelloProdotti.getCurrentCatalogo());
				//non si deve superare il numero massimo di prodotti inseribili per l'articolo individuato 
				if (prodottiCaricati >= catalogoBando.getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo()) {
					this.addActionError(this.getText("Errors.limiteInserimentoProdottiPerArticolo",
													 new String[] {catalogoBando.getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo() + ""}));
					controlliOk = false;
				}
				ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				
				// Verifico che il prodotto non sia gia' presente nel carrello delle modifiche
				if (prodotti.prodottoGiaModificato(this.prodottoId)) {
					this.addActionError(this.getText("Errors.prodottoAlreadyModified"));
					controlliOk = false;
				}
				if (controlliOk) {
					carrelloProdotti.aggiungiAModificati(prodottoHelper);
					saveProdotti(
							this.comunicazioniManager, 
							this.eventManager, 
							this.appParamManager, 
							prodotti, 
							this);
				}
				this.undo = false;
				this.setTarget("gestioneProdotti");
			} else {
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "reinsertProdotto");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (IOException t) {
			ApsSystemUtils.logThrowable(t, this, "reinsertProdotto");
			this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (CloneNotSupportedException ex) {
			ApsSystemUtils.logThrowable(ex, this, "reinsertProdotto");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (OutOfMemoryError ex) {
			ApsSystemUtils.logThrowable(ex, this, "reinsertProdotto");
			this.addActionError(this.getText("Errors.send.outOfMemory"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (XmlException ex) {
			ApsSystemUtils.logThrowable(ex, this, "reinsertProdotto");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, this, "reinsertProdotto");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Gestisce la richiesta di annullamento reinserimento di un prodotto
	 * archiviato in catalogo
	 *
	 * @return il target a cui andare
	 */
	public String cancelReinsertProdotto() {
		this.reinsert = false;
		this.setTarget("dettaglioProdotto");
		return this.getTarget();
	}

	/**
	 * Gestisce la richiesta di annullamento di tutte le modifiche al catalogo
	 * presenti nel carrello
	 *
	 * @return il target a cui andare
	 */
	public String confirmUndoAllProdotto() {
		this.setTarget("prodottiCarrello");
		
		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& carrelloProdotti != null) {
			this.catalogo = carrelloProdotti.getCurrentCatalogo();
			this.setUndoAll(true);
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Esegue l'operazione di di annullamento di tutte le modifiche al catalogo
	 * presenti nel carrello
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String undoAllProdotto() {
		this.setTarget("gestioneProdotti");
		
		Event evento = null;
		
		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& carrelloProdotti != null) {

			ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
			try {
				prodotti.setProdottiEliminati(new ArrayList<WizardProdottoHelper>());
				prodotti.setProdottiInseriti(new ArrayList<WizardProdottoHelper>());
				prodotti.setProdottiModificati(new ArrayList<WizardProdottoHelper>());
				prodotti.setBozze(new ArrayList<WizardProdottoHelper>());
				
				//nel caso ci sia gia' una comunicazione FS7 in bozza la elimino
				if (prodotti.getIdComunicazioneBozza() != null) {
					this.comunicazioniManager.deleteComunicazione(
							CommonSystemConstants.ID_APPLICATIVO,
							prodotti.getIdComunicazioneBozza());
					prodotti.setIdComunicazioneBozza(null);
				}
				prodotti.setVariazioneOfferta(false);
				this.setDeleted(true);
				
//				// DELETECOM ?! DA RIVEDERE...
//				evento = new Event();
//				evento.setUsername(this.getCurrentUser().getUsername());
//				evento.setDestination(carrelloProdotti.getCurrentCatalogo());
//				evento.setLevel(Event.Level.INFO);
//				evento.setEventType(PortGareEventsConstants.???);
//				evento.setIpAddress(this.getCurrentUser().getIpAddress());
//				evento.setSessionId(this.getRequest().getSession().getId()); 				
				
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "undoAllProdotto");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} finally {
//				if(evento != null) {
//					this.eventManager.insertEvent(evento);
//				}				
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		this.setUndoAll(false);
		return this.getTarget();
	}

	/**
	 * Scarta la richiesta di annullamento modifiche del catalogo presenti nel
	 * carrello
	 *
	 * @return il target a cui andare
	 */
	public String cancelUndoAllProdotto() {
		this.undoAll = false;
		this.setTarget("prodottiCarrello");
		return this.getTarget();
	}

	/**
	 * Effettua l'invio della comunicazione al backoffice costituita da una
	 * testata e da un allegato contenente al suo interno l'XML con le modifiche
	 * ai prodotti del catalogo (compreso il file di riepilogo firmato)
	 *
	 * @param comunicazioniManager
	 * @param datiModificheProdotti 
	 * 			contenitore di tutte le modifiche ai prodotti del catalogo selezionato
	 * @param username 
	 * 			username dell'impresa inviante la comunicazione
	 * @param mittente 
	 * 			mittente della comunicazione (ragione sociale)
	 * @param stato 
	 * 			stato della comunicazione (1=bozza, 5=da processare)
	 * @param tipoComunicazione 
	 * 			tipo di comunicazione (FS7=Richiesta aggiornamento prodotti in catalogo)
	 * @param oggetto 
	 * 			titolo della comunicazione
	 * @param testo 
	 * 			testo della comunicazione
	 * @param nomeFile 
	 * 			nome del file xml contenente i dati
	 * @param descrizioneFile 
	 * 			descrizione del contenuto del file xml con i prodotti
	 * @param descrizioneFileRiepilogoFirmato 
	 * 			descrizione del contenuto del file di riepilogo firmato
	 * @param usaIdComunicazioneBozza 
	 * 			indica se generare una nuova comunicazione
	 * oppure sovrascrive 
	 * 			quella delle bozze usata in precedenza
	 * @param dataPresentazione 
	 * 			la data di invio comunicazione
	 * @param tipoOperazione 
	 * 			indica la tipologia di operazione che sto facendo
	 * (invio modifiche prodotti, salvataggio prodotti, salvataggio bozze)
	 * 
	 * @throws IOException
	 * @throws ApsException
	 */
	public static ComunicazioneType sendComunicazione(
			IComunicazioniManager comunicazioniManager,
			ProdottiCatalogoSessione datiModificheProdotti,
			String username, 
			String mittente, 
			String stato,
			String tipoComunicazione, 
			String oggetto, 
			String testo,
			String nomeFile,
			String descrizioneFile,
			String descrizioneFileRiepilogoFirmato,
			boolean usaIdComunicazioneBozza,
			Date dataPresentazione,
			String tipoOperazione) throws IOException, ApsException 
	{
		// FASE 1: costruzione del contenitore della comunicazione
		ComunicazioneType comunicazione = new ComunicazioneType();

		// FASE 2: popolamento della testata
		DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities.createDettaglioComunicazione(
				(usaIdComunicazioneBozza ? datiModificheProdotti.getIdComunicazioneBozza() : null),
				username, 
				datiModificheProdotti.getCodiceCatalogo(), 
				null,
				mittente,
				stato, 
				oggetto, 
				testo, 
				tipoComunicazione, 
				dataPresentazione);
		comunicazione.setDettaglioComunicazione(dettaglioComunicazione);

		// FASE 3: popolamento dell'allegato con l'xml dei dati da inviare
		// se la comunicazione è in stato DA_PROCESSARE o DA PROTOCOLLARE
		// si allega il riepilogo alla comunicazione...
		File riepilogoFirmato = null;
		if (datiModificheProdotti.getRiepilogo() != null && 
			( CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(stato) || 
			  CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE.equals(stato)) ) {
			riepilogoFirmato = datiModificheProdotti.getRiepilogo();
		} 	
		
		ProdottiAction.setAllegatoComunicazione(
				comunicazione, 
				datiModificheProdotti, 
				nomeFile, 
				descrizioneFile, 				
				riepilogoFirmato, 
				descrizioneFileRiepilogoFirmato, 
				tipoOperazione);		

		// FASE 4: invio comunicazione
		comunicazione.getDettaglioComunicazione().setId(comunicazioniManager.sendComunicazione(comunicazione));
		
		return comunicazione;
	}
	
	/**
	 * imposta l'allegato della comunicazione 
	 */
	private static void setAllegatoComunicazione(
			ComunicazioneType comunicazione,
			ProdottiCatalogoSessione datiModificheProdotti,
			String nomeFile,
			String descrizioneFile,
			File riepilogoFirmato,
			String descrizioneFileRiepilogoFirmato,
			String tipoOperazione) throws IOException 
	{
		AllegatoComunicazioneType[] allegati = null;
		AllegatoComunicazioneType allegatoXml;
		AllegatoComunicazioneType allegatoPdf; 
			
		HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
		suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmlObject xmlDocument = datiModificheProdotti.getXmlDocument(
				GestioneProdottiDocument.Factory.newInstance(), 
				null, 
				tipoOperazione);
		xmlDocument.save(baos, opts);
		
		// crea l'allegato del documento xml...
		allegatoXml = ComunicazioniUtilities.createAllegatoComunicazione(
				nomeFile, 
				descrizioneFile, 
				baos.toByteArray());
 
		// aggiungi l'allego del pdf firmato...
		if(riepilogoFirmato != null) {
			allegatoPdf = ComunicazioniUtilities.createAllegatoComunicazione(
					datiModificheProdotti.getRiepilogoFileName(),
					descrizioneFileRiepilogoFirmato,
					FileUtils.readFileToByteArray(riepilogoFirmato));
			allegati = new AllegatoComunicazioneType[] { allegatoXml, allegatoPdf };
		} else {
			allegati = new AllegatoComunicazioneType[] { allegatoXml };
		}

		comunicazione.setAllegato(allegati);		
	}

}
