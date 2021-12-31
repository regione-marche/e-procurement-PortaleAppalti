package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardArticoloHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiDocument;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

/**
 * Action per le operazioni sui cataloghi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version 1.0
 * @author Stefano.Sabbadin
 *
 */
public class GestioneProdottiAction extends EncodedDataAction implements SessionAware, ModelDriven<ProdottiSearchBean> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1695787506907078618L;
	
	private static final int MIN_SEARCH_CHARS = 3;

	private ICataloghiManager cataloghiManager;
	private IComunicazioniManager comunicazioniManager;
	private String catalogo;
	private Map<String, Object> session;
	private ProdottiSearchBean model = new ProdottiSearchBean();
	List<WizardProdottoHelper> listaProdotti = null;
	private String last;
	private boolean modifichePresenti;
	private boolean variazioneOfferta;
	private DettaglioComunicazioneType dettaglioComunicazione;
	private DettaglioComunicazioneType dettaglioComunicazioneVariazioneOfferta;
	private InputStream inputStream;
	private String filename;
	private String statoProdotto;
	private boolean deleted;
	private int inseriti;
	private int modificati;
	private int archiviati;
	private int bozze;

	/**
	 * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
	 */
	private String multipartSaveDir;

	/**
	 * Id dell'articolo per cui si vuole inserire un nuovo prodotto
	 */
	private Long articoloId;

	
	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public ProdottiSearchBean getModel() {
		return this.model;
	}

	public List<WizardProdottoHelper> getListaProdotti() {
		return listaProdotti;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public void setArticoloId(Long articoloId) {
		this.articoloId = articoloId;
	}

	public Long getArticoloId() {
		return articoloId;
	}
	
	public boolean isModifichePresenti() {
		return modifichePresenti;
	}

	public void setModifichePresenti(boolean modifichePresenti) {
		this.modifichePresenti = modifichePresenti;
	}

	public DettaglioComunicazioneType getDettaglioComunicazione() {
		return dettaglioComunicazione;
	}

	public void setDettaglioComunicazione(DettaglioComunicazioneType dettaglioComunicazione) {
		this.dettaglioComunicazione = dettaglioComunicazione;
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

	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	public String getStatoProdotto() {
		return statoProdotto;
	}

	public void setStatoProdotto(String statoProdotto) {
		this.statoProdotto = statoProdotto;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getInseriti() {
		return inseriti;
	}

	public void setInseriti(int inseriti) {
		this.inseriti = inseriti;
	}

	public int getModificati() {
		return modificati;
	}

	public void setModificati(int modificati) {
		this.modificati = modificati;
	}

	public int getArchiviati() {
		return archiviati;
	}

	public void setArchiviati(int archiviati) {
		this.archiviati = archiviati;
	}

	public int getBozze() {
		return bozze;
	}

	public void setBozze(int bozze) {
		this.bozze = bozze;
	}

	public boolean isVariazioneOfferta() {
		return variazioneOfferta;
	}

	public void setVariazioneOfferta(boolean variazioneOfferta) {
		this.variazioneOfferta = variazioneOfferta;
	}

	public DettaglioComunicazioneType getDettaglioComunicazioneVariazioneOfferta() {
		return dettaglioComunicazioneVariazioneOfferta;
	}

	public void setDettaglioComunicazioneVariazioneOfferta(DettaglioComunicazioneType dettaglioComunicazioneVariazioneOfferta) {
		this.dettaglioComunicazioneVariazioneOfferta = dettaglioComunicazioneVariazioneOfferta;
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

	public String getSTATO_PRODOTTO_ARCHIVIATO() {
		return CataloghiConstants.STATO_PRODOTTO_ARCHIVIATO;
	}

	public String getBOZZA() {
		return CataloghiConstants.BOZZA;
	}

	/**
	 * Visualizza i prodotti a sistema e ne consente la modifica
	 *
	 * @return il target desiderato
	 */
	public String viewProdottiSistema() {

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.model = new ProdottiSearchBean();
			this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_PRODOTTI, this.model);
			try {
				populateListaProdottiASistema();
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openGestioneProdotti");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Visualizza i prodotti a sistema filtrati e ne consente la modifica
	 *
	 * @return il target desiderato
	 */
	public String searchProdottiSistema() {

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			if ("1".equals(this.last)) {
				ProdottiSearchBean finder = (ProdottiSearchBean) this.session.get(PortGareSystemConstants.SESSION_ID_SEARCH_PRODOTTI);
				this.model = finder;
			}
			try {
				this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_PRODOTTI, this.model);
				populateListaProdottiASistema();
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openGestioneProdotti");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Visualizza le modifiche ai prodotti salvate nel carrello
	 *
	 * @return
	 */
	public String viewProdottiCarrello() {
		this.setTarget("prodottiCarrello");
		
		this.listaProdotti = new ArrayList<WizardProdottoHelper>();
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				CarrelloProdottiSessione carrelloProdotti = initCarrello();
				if (!populateListaProdottiDallaComunicazioneProdottiInviati(carrelloProdotti)) {
					readVariazioneOffertaInviata(carrelloProdotti);
				}
				ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				populateListaProdottiDelCarrello(prodottiInCarrello);
				this.variazioneOfferta = prodottiInCarrello.isVariazioneOfferta();
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openGestioneProdotti");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Apertura pagina di gestione dei prodotti
	 *
	 * @return
	 */
	public String openGestioneProdotti() {
		this.setTarget(SUCCESS);
		
		this.listaProdotti = new ArrayList<WizardProdottoHelper>();
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				CarrelloProdottiSessione carrelloProdotti = initCarrello();
				if (!populateListaProdottiDallaComunicazioneProdottiInviati(carrelloProdotti)) {
					readVariazioneOffertaInviata(carrelloProdotti);
				}
				ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				this.modifichePresenti = prodottiInCarrello.hasModifiche();
				this.inseriti = prodottiInCarrello.getNumeroProdottiInseriti();
				this.modificati = prodottiInCarrello.getNumeroProdottiModificati();
				this.archiviati = prodottiInCarrello.getNumeroProdottiEliminati();
				this.bozze = prodottiInCarrello.getNumeroProdottiBozza();
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				this.variazioneOfferta = prodottiInCarrello.isVariazioneOfferta();
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openGestioneProdotti");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openGestioneProdotti");
				this.addActionError(this.getText("Errors.gestioneProdotti.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	private CarrelloProdottiSessione initCarrello() throws ApsException, XmlException, IOException, Exception {
		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		
		if (carrelloProdotti == null 
			|| (this.catalogo != null && !carrelloProdotti.getCurrentCatalogo().equals(this.catalogo))) {
			carrelloProdotti = CarrelloProdottiSessione.getInstance(this.session, this.catalogo, this.cataloghiManager);
			readComunicazioneBozze(carrelloProdotti);
		}
		if (this.catalogo == null) {
			this.catalogo = carrelloProdotti.getCurrentCatalogo();
		}
		return carrelloProdotti;
	}

	private void populateListaProdottiASistema() throws ApsException, XmlException, IOException, Exception {
		this.setTarget("prodottiSistema");
		
		if (this.model == null) {
			this.model = new ProdottiSearchBean();
		}
		
		this.listaProdotti = new ArrayList<WizardProdottoHelper>();
		CarrelloProdottiSessione carrelloProdotti = initCarrello();
		populateListaProdottiDallaComunicazioneProdottiInviati(carrelloProdotti);
		SearchResult<ProdottoType> prodotti = new SearchResult<ProdottoType>();
		
		if (this.model.isAdvancedSearch()) {
			prodotti = cataloghiManager.searchProdotti(
					carrelloProdotti.getCurrentCatalogo(), 
					null, 
					this.getCurrentUser().getUsername(), 
					this.model);
		} else {
			if (isValidGoogleLikeSearchField()) {
				prodotti = cataloghiManager.searchProdotti(
						carrelloProdotti.getCurrentCatalogo(), 
						null, 
						this.getCurrentUser().getUsername(),
						this.model.getGoogleLike(), 
						this.model.getStartIndexNumber(), 
						this.model.getiDisplayLength());
			} else {
				this.addActionError(this.getText("minSearchChars"));
			}
		}
		
		ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
		for (ProdottoType prodotto : prodotti.getDati()) {
			WizardProdottoHelper prodottoHelper = new WizardProdottoHelper();
			prodottoHelper.setDettaglioProdotto(prodotto);
			WizardArticoloHelper articoloHelper = new WizardArticoloHelper();
			articoloHelper.setIdArticolo(prodotto.getIdArticolo());
			articoloHelper.setDettaglioArticolo(this.cataloghiManager.getArticolo(prodotto.getIdArticolo()));
			prodottoHelper.setArticolo(articoloHelper);
			prodottoHelper.setModificato(prodottiInCarrello.prodottoGiaModificato(prodotto.getId()));
			prodottoHelper.setArchiviato(prodottiInCarrello.prodottoGiaEliminato(prodotto.getId()));
			this.listaProdotti.add(prodottoHelper);
		}
		this.variazioneOfferta = prodottiInCarrello.isVariazioneOfferta();
		this.dettaglioComunicazioneVariazioneOfferta = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				this.catalogo,
				null,
				CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
				PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO);
		this.model.processResult(prodotti.getNumTotaleRecord(), prodotti.getNumTotaleRecordFiltrati());
	}

	/**
	 * ... 
	 */
	private void populateListaProdottiDelCarrello(ProdottiCatalogoSessione prodottiInCarrello) throws ApsException, XmlException, IOException, Exception {
		this.listaProdotti.addAll(prodottiInCarrello.getProdottiInseriti());
		this.listaProdotti.addAll(prodottiInCarrello.getProdottiModificati());
		this.listaProdotti.addAll(prodottiInCarrello.getProdottiEliminati());
		this.listaProdotti.addAll(prodottiInCarrello.getBozze());
	}

	/**
	 * ... 
	 */
	private void readComunicazioneBozze(CarrelloProdottiSessione carrelloProdotti) throws ApsException, XmlException, IOException, Exception {
		//scaricare l'eventuale comunicazione di tipo 'FS7' in stato '1' relativa al medesimo catalogo
		DettaglioComunicazioneType prodottiSalvati = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				this.catalogo,
				null,
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
				PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO);
		if (prodottiSalvati != null) {
			populateListaProdottiDallaComunicazioneBozze(carrelloProdotti, prodottiSalvati);
		}
	}

	/**
	 * ... 
	 */
	private void populateListaProdottiDallaComunicazioneBozze(CarrelloProdottiSessione carrelloProdotti, DettaglioComunicazioneType dettaglioComunicazione)
			throws ApsException, XmlException, Exception 
	{
		ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
				CommonSystemConstants.ID_APPLICATIVO, 
				dettaglioComunicazione.getId());
		int i = 0;
		AllegatoComunicazioneType allegato = null;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && allegato == null) {
			if (PortGareSystemConstants.NOME_FILE_PRODOTTI_BOZZE.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegato = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}
		if (allegato == null) {
			this.addActionError(this.getText("Errors.gestioneProdotti.xmlBozzeNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// si interpreta l'xml ricevuto
			GestioneProdottiDocument documento = GestioneProdottiDocument.Factory.parse(new String(allegato.getFile()));
			DettaglioBandoIscrizioneType dettagliocatalogo = this.cataloghiManager.getDettaglioCatalogo(this.catalogo);
			String saveDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), this.multipartSaveDir).getAbsolutePath();
			carrelloProdotti.populateListaProdottiDaDocumento(
					comunicazione, 
					dettagliocatalogo, 
					documento, 
					this.cataloghiManager, 
					saveDir);
		}
	}

	/**
	 * ... 
	 */
	private boolean populateListaProdottiDallaComunicazioneProdottiInviati(CarrelloProdottiSessione carrelloProdotti) throws ApsException {
		boolean comunicazionePresente = false;
		
		this.dettaglioComunicazione = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				this.catalogo,
				null,
				CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
				PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO);
		
		ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo()
			.get(carrelloProdotti.getCurrentCatalogo());
		
		if (this.dettaglioComunicazione != null) {
			comunicazionePresente = true;
			// se il riepilogo e' stato generato in una precedente sessione di lavoro 
			// allora lo si rilegge dalla comunicazione
			if (prodottiInCarrello.getRiepilogo() == null) {
				populateRiepilogoProdottiInviati(
						carrelloProdotti, 
						prodottiInCarrello, 
						this.dettaglioComunicazione);
			}
			prodottiInCarrello.setIdComunicazioneModifiche(this.dettaglioComunicazione.getId());
		} else {
			// se non esiste o e' stata processata, si sbianca l'id comunicazione e il pdf di riepilogo
			prodottiInCarrello.setIdComunicazioneModifiche(null);
			prodottiInCarrello.cleanRiepilogo();
		}
		return comunicazionePresente;
	}

	/**
	 * ... 
	 */
	private boolean readVariazioneOffertaInviata(CarrelloProdottiSessione carrelloProdotti) throws ApsException {
		boolean comunicazionePresente = false;
		
		this.dettaglioComunicazioneVariazioneOfferta = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				this.catalogo,
				null,
				CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
				PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO);
		
		ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo()
			.get(carrelloProdotti.getCurrentCatalogo());
		
		if (this.dettaglioComunicazioneVariazioneOfferta != null) {
			comunicazionePresente = true;
			if (prodottiInCarrello.getRiepilogo() == null) {
				readRiepilogoVariazioneOfferta(
						carrelloProdotti, 
						prodottiInCarrello, 
						this.dettaglioComunicazioneVariazioneOfferta);
			}
			prodottiInCarrello.setIdComunicazioneVariazioneOfferta(this.dettaglioComunicazioneVariazioneOfferta.getId());
		} else {
			prodottiInCarrello.setIdComunicazioneVariazioneOfferta(null);
			prodottiInCarrello.cleanRiepilogo();
		}
		return comunicazionePresente;
	}

	/**
	 * Ricavo i dati utili alla generazione del carrello prodotti per consentire
	 * la gestione del wizard di inserimento del prodotto e memorizzo tali dati in
	 * sessione.
	 *
	 * @return il target a cui passare il flusso applicativo
	 */
	public String initWizard() {

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.catalogo, 
						this.cataloghiManager);
				WizardProdottoHelper prodottoHelper = WizardProdottoHelper.getInstance(
						this.session, 
						this.cataloghiManager, 
						carrelloProdotti,
						this.getCurrentUser().getUsername(), 
						carrelloProdotti.getCurrentCatalogo(), 
						null, 
						null, 
						null, 
						null, 
						null, 
						CataloghiConstants.PAGINA_GESTIONE_PRODOTTI);
				session.put(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO, prodottoHelper);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "initWizard");
				ExceptionUtils.manageExceptionError(t, this);
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
	private void populateRiepilogoProdottiInviati(
			CarrelloProdottiSessione carrelloProdotti,
			ProdottiCatalogoSessione prodottiInCarrello, 
			DettaglioComunicazioneType dettaglioComunicazione) throws ApsException 
	{
		ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
				CommonSystemConstants.ID_APPLICATIVO,
				dettaglioComunicazione.getId());
		int i = 0;
		AllegatoComunicazioneType allegatoRiepilogo = null;
		AllegatoComunicazioneType allegatoProdotti = null;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && (allegatoRiepilogo == null || allegatoProdotti == null)) {
			if (PortGareSystemConstants.NOME_FILE_GESTIONE_PRODOTTI.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoProdotti = comunicazione.getAllegato()[i];
			} else {
				allegatoRiepilogo = comunicazione.getAllegato()[i];
			}
			i++;
		}
		if (allegatoRiepilogo == null) {
			this.addActionError(this.getText("Errors.gestioneProdotti.RiepilogoProdottiFirmatoNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			this.readPdfRiepilogo(
					allegatoRiepilogo, 
					carrelloProdotti,
					prodottiInCarrello, 
					"Riepilogo_modifiche_prodotti_firmato_");
		}
		if (allegatoProdotti == null) {
			this.addActionError(this.getText("Errors.gestioneProdotti.xmlGestioneProdottiNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			this.readXmlRiepilogo(
					allegatoProdotti, 
					carrelloProdotti, 
					prodottiInCarrello);
		}
	}

	/**
	 * ... 
	 */
	private void readRiepilogoVariazioneOfferta(
			CarrelloProdottiSessione carrelloProdotti,
			ProdottiCatalogoSessione prodottiInCarrello, 
			DettaglioComunicazioneType dettaglioComunicazione) throws ApsException 
	{
		ComunicazioneType comunicazione = this.comunicazioniManager.getComunicazione(
				CommonSystemConstants.ID_APPLICATIVO,
				dettaglioComunicazione.getId());
		int i = 0;
		AllegatoComunicazioneType allegatoRiepilogo = null;
		AllegatoComunicazioneType allegatoVariazioni = null;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && (allegatoRiepilogo == null || allegatoVariazioni == null)) {
			if (PortGareSystemConstants.NOME_FILE_VARIAZIONE_OFFERTA.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoVariazioni = comunicazione.getAllegato()[i];
			} else {
				allegatoRiepilogo = comunicazione.getAllegato()[i];
			}
			i++;
		}
		if (allegatoRiepilogo == null) {
			this.addActionError(this.getText("Errors.gestioneProdotti.RiepilogoVariazioniOffertaFirmatoNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			this.readPdfRiepilogo(
					allegatoRiepilogo, 
					carrelloProdotti,
					prodottiInCarrello, 
					"Riepilogo_variazioni_prodotti_firmato_");
		}
		if (allegatoVariazioni == null) {
			this.addActionError(this.getText("Errors.gestioneProdotti.xmlVariazioneOffertaNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			this.readXmlRiepilogo(
					allegatoVariazioni, 
					carrelloProdotti, 
					prodottiInCarrello);
		}
	}

	/**
	 * ... 
	 */
	private void readPdfRiepilogo(
			AllegatoComunicazioneType allegatoRiepilogo, 
			CarrelloProdottiSessione carrelloProdotti,
			ProdottiCatalogoSessione prodottiInCarrello, 
			String filePrefix) 
	{
		String tempDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(),
						this.multipartSaveDir).getAbsolutePath();
		File f = new File(tempDir + File.separatorChar + FileUploadUtilities.generateFileName());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(allegatoRiepilogo.getFile());
		} catch (FileNotFoundException ex) {
			ApsSystemUtils.logThrowable(ex, this, "readPdfRiepilogo");
		} catch (IOException ex) {
			ApsSystemUtils.logThrowable(ex, this, "readPdfRiepilogo");
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, this, "readPdfRiepilogo");
		} finally {
			if (fos != null) {
				try {
					fos.close();
					prodottiInCarrello.setRiepilogo(f);
					prodottiInCarrello.setRiepilogoSize((int) Math.ceil(f.length() / 1024.0));
					String dataPubb = null;
					if (this.dettaglioComunicazione != null) {
						dataPubb = UtilityDate.convertiData(
								dettaglioComunicazione.getDataPubblicazione(),
								UtilityDate.FORMATO_AAAAMMGG);
					} else if (this.dettaglioComunicazioneVariazioneOfferta != null) {
						dataPubb = UtilityDate.convertiData(
								dettaglioComunicazioneVariazioneOfferta.getDataPubblicazione(),
								UtilityDate.FORMATO_AAAAMMGG);
					}
					prodottiInCarrello.setRiepilogoFileName(filePrefix + dataPubb + ".pdf.p7m");
					prodottiInCarrello.setRiepilogoContentType("application/pkcs7-mime");
					carrelloProdotti.addFileReference(f);
				} catch (IOException ex) {
					ApsSystemUtils.logThrowable(ex, this, "readPdfRiepilogo");
				} catch (Throwable ex) {
					ApsSystemUtils.logThrowable(ex, this, "readPdfRiepilogo");
				}				
			}
		}
	}

	/**
	 * ... 
	 */
	private void readXmlRiepilogo(
			AllegatoComunicazioneType allegatoProdotti, 
			CarrelloProdottiSessione carrelloProdotti,
			ProdottiCatalogoSessione prodottiInCarrello) 
	{
		String tempDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), 
				this.multipartSaveDir).getAbsolutePath();
		File f = new File(tempDir + File.separatorChar + FileUploadUtilities.generateFileName());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(allegatoProdotti.getFile());
		} catch (FileNotFoundException ex) {
			ApsSystemUtils.logThrowable(ex, this, "readXmlRiepilogo");
		} catch (IOException ex) {
			ApsSystemUtils.logThrowable(ex, this, "readXmlRiepilogo");
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, this, "readXmlRiepilogo");
		} finally {
			if (fos != null) {
				try {
					fos.close();
					prodottiInCarrello.setXmlDati(f);
					carrelloProdotti.addFileReference(f);
				} catch (IOException ex) {
					ApsSystemUtils.logThrowable(ex, this, "readXmlRiepilogo");
				}
			}
		}
	}

	/**
	 * ... 
	 */
	private boolean isValidGoogleLikeSearchField() {
		if (StringUtils.isBlank(this.model.getGoogleLike())) {
			return true;
		}
		String[] words = StringUtils.split(this.model.getGoogleLike(), " ");
		boolean found = false;
		for (String word : words) {
			if (word.trim().length() < MIN_SEARCH_CHARS) {
				found = true;
				break;
			}
		}
		return !found;
	}
	
}
