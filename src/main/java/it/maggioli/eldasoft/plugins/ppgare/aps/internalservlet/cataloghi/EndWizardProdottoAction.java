package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.inject.Inject;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Action di gestione dell'apertura delle pagine del wizard
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class EndWizardProdottoAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2969901481272472654L;

	private Map<String, Object> session;

	private ICataloghiManager cataloghiManager;
	private IDocumentiDigitaliManager documentiDigitaliManager;
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;

	private Date dataOperazione;
	@Validate(EParamValidation.GENERIC)
	private String msgErroreInvioEmail;
	private boolean bozza;
	private boolean aggiornamento;
	private String catalogo;

	/**
	 * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
	 */
	@Validate(EParamValidation.GENERIC)
	private String multipartSaveDir;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setDocumentiDigitaliManager(IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getTAB_PRODOTTI_NEL_CARRELLO() {
		return CataloghiConstants.TAB_PRODOTTI_NEL_CARRELLO;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public Date getDataOperazione() {
		return dataOperazione;
	}

	public void setDataOperazione(Date dataOperazione) {
		this.dataOperazione = dataOperazione;
	}

	public String getMsgErroreInvioEmail() {
		return msgErroreInvioEmail;
	}

	public boolean isBozza() {
		return bozza;
	}

	public void setBozza(boolean bozza) {
		this.bozza = bozza;
	}

	public boolean isAggiornamento() {
		return aggiornamento;
	}

	public void setAggiornamento(boolean aggiornamento) {
		this.aggiornamento = aggiornamento;
	}
	
	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	/**
	 * Inserisco il prodotto nel carrello in stato "In catalogo" o "In attesa di
	 * verifica"
	 *
	 * @return il target a cui andare
	 */
	public String addSave() {
		try {
			WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
			
			if (prodottoHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				boolean controlliOk = true;
				
				catalogo = prodottoHelper.getCodiceCatalogo();
				
				// la sessione non e' scaduta, per cui proseguo regolarmente
				if (prodottoHelper.getArticolo().getDettaglioArticolo().isObbligoImmagine() && prodottoHelper.getDocumenti().getImmagine() == null) {
					this.addActionError(this.getText("Errors.immagineMancante"));
					controlliOk = false;
				}
				if (prodottoHelper.getArticolo().getDettaglioArticolo().isObbligoCertificazioni() && prodottoHelper.getDocumenti().getCertificazioniRichieste().isEmpty()) {
					this.addActionError(this.getText("Errors.certificazioneMancante"));
					controlliOk = false;
				}
				if (prodottoHelper.getArticolo().getDettaglioArticolo().isObbligoSchedaTecnica() && prodottoHelper.getDocumenti().getSchedeTecniche().isEmpty()) {
					this.addActionError(this.getText("Errors.schedaTecnicaMancante"));
					controlliOk = false;
				}
				
				CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(
						prodottoHelper.getArticolo().getIdArticolo(),
						this.getCurrentUser().getUsername());
				long numeroProdottiOECarrello = carrelloProdotti.calculateProdottiCaricatiOE(prodottoHelper.getArticolo().getIdArticolo());
				Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
				if ((!prodottoHelper.isAggiornamento() || CataloghiConstants.BOZZA.equals(prodottoHelper.getStatoProdotto()))
					&& prodottiCaricati >= prodottoHelper.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo()) {
					this.addActionError(this.getText(
							"Errors.limiteInserimentoProdottiPerArticolo",
							new String[] {prodottoHelper.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo() + ""}));
					controlliOk = false;
				}

				if (controlliOk) {
					String tempDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir).getAbsolutePath();
					WizardProdottoHelper prodotto = prodottoHelper.clone(
							this.documentiDigitaliManager, 
							tempDir, 
							false, 
							carrelloProdotti, 
							this.getCurrentUser().getUsername());
					prodotto.setDataOperazione(new Date());
					if (CataloghiConstants.BOZZA.equals(prodotto.getStatoProdotto())) {
						fromBozzaToProdottoInserito(prodotto, prodottoHelper, carrelloProdotti);
					} else if (prodotto.isAggiornamento()) {
						modifyProdotto(prodotto, prodottoHelper, carrelloProdotti);
					} else {
						addProdotto(prodotto, carrelloProdotti);
					}
					ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
					ProdottiAction.saveProdotti(prodotti);
					this.dataOperazione = prodotto.getDataOperazione();
					this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
					prodotti.setUltimaCategoria(prodotto.getArticolo().getDettaglioArticolo().getCodiceCategoria());
				} else {
					this.setTarget(INPUT);
				}
			}
		} catch (CloneNotSupportedException ex) {
			ApsSystemUtils.logThrowable(ex, this, "addSave");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (IOException t) {
			ApsSystemUtils.logThrowable(t, this, "addSave");
			this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (OutOfMemoryError t) {
			ApsSystemUtils.logThrowable(t, this, "addSave");
			this.addActionError(this.getText("Errors.send.outOfMemory"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (XmlException t) {
			ApsSystemUtils.logThrowable(t, this, "addSave");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addSave");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Funzione di utilita' che implementa l'aggiunta del prodotto nel carrello
	 * nella lista degli inserimenti
	 *
	 * @param prodottoDaSalvare il prodotto da aggiungere come nuovo
	 * @param carrelloProdotti il carrello dei prodotti in sessione
	 */
	private void addProdotto(
			WizardProdottoHelper prodottoDaSalvare, 
			CarrelloProdottiSessione carrelloProdotti) 
	{
		this.aggiornamento = false;
		carrelloProdotti.aggiungiAInseriti(prodottoDaSalvare);
	}

	/**
	 * Funzione di utilita' che implementa l'aggiunta del prodotto nel carrello
	 * nella lista delle modifiche
	 *
	 * @param prodottoModificato il prodotto da aggiungere come modificato
	 * @param carrelloProdotti il carrello dei prodotti in sessione
	 * @param oldIndex indice del prodotto in modifica nel carrello prima del
	 * salvataggio
	 */
	private void modifyProdotto(
			WizardProdottoHelper prodottoModificato, 
			WizardProdottoHelper prodottoDaModificare, 
			CarrelloProdottiSessione carrelloProdotti) 
	{
		this.aggiornamento = true;
		if (prodottoModificato.isInCarrello()) {
			if (prodottoModificato.getStatoProdotto().equals(CataloghiConstants.PRODOTTO_MODIFICATO)) {
				carrelloProdotti.rimuoviDaModificati(prodottoDaModificare);
			} else {
				carrelloProdotti.rimuoviDaInseriti(prodottoDaModificare);
			}
		}
		if (!prodottoModificato.isInCarrello() || prodottoModificato.getStatoProdotto().equals(CataloghiConstants.PRODOTTO_MODIFICATO)) {
			carrelloProdotti.aggiungiAModificati(prodottoModificato);
		} else {
			carrelloProdotti.aggiungiAInseriti(prodottoModificato);
		}
	}

	/**
	 * Funzione di utilita' che implementa l'aggiunta della bozza nel carrello
	 * nella lista degli inserimenti
	 *
	 * @param prodotto la bozza da aggiungere come nuovo prodotto
	 * @param carrelloProdotti il carrello dei prodotti in sessione
	 */
	private void fromBozzaToProdottoInserito(
			WizardProdottoHelper prodotto, 
			WizardProdottoHelper bozzaDaInserire, 
			CarrelloProdottiSessione carrelloProdotti) 
	{
		this.aggiornamento = false;
		prodotto.setStatoProdotto(CataloghiConstants.PRODOTTO_INSERITO);
		carrelloProdotti.rimuoviDaBozze(bozzaDaInserire);
		carrelloProdotti.aggiungiAInseriti(prodotto);
	}

	/**
	 * Inserisco il prodotto nel carrello in stato "Bozza"
	 *
	 * @return il target a cui andare
	 */
	public String addSaveBozza() {
		try {
			WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
			
			if (prodottoHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				catalogo = prodottoHelper.getCodiceCatalogo();
				
				prodottoHelper.setDataOperazione(new Date());
				CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				String tempDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir).getAbsolutePath();
				WizardProdottoHelper bozzaDaSalvare = prodottoHelper.clone(
						this.documentiDigitaliManager, 
						tempDir, 
						false, 
						carrelloProdotti,
						this.getCurrentUser().getUsername());
				if (prodottoHelper.isAggiornamento()) {
					carrelloProdotti.rimuoviDaBozze(prodottoHelper);
				}
				carrelloProdotti.aggiungiABozze(bozzaDaSalvare);
				ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				ProdottiAction.saveProdotti(prodotti);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				this.bozza = true;
			}
		} catch (CloneNotSupportedException ex) {
			ApsSystemUtils.logThrowable(ex, this, "addSaveBozza");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (IOException t) {
			ApsSystemUtils.logThrowable(t, this, "addSaveBozza");
			this.addActionError(this.getText("Errors.cannotLoadAttachments"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (OutOfMemoryError t) {
			ApsSystemUtils.logThrowable(t, this, "addSaveBozza");
			this.addActionError(this.getText("Errors.send.outOfMemory"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (XmlException t) {
			ApsSystemUtils.logThrowable(t, this, "addSaveBozza");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addSaveBozza");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * ...
	 */
	public String back() {
		return "back";
	}
	
}
