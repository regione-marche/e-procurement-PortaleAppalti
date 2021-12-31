package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.commons.lang.StringUtils;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard
 * d'inserimento prodotto
 *
 * @author Marco.Perazzetta
 */
public class OpenPageRiepilogoProdottoAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -999008947632964207L;

	private ICataloghiManager cataloghiManager;

	private boolean hasImmagine;
	private boolean comunicazioneExists;
	private int numeroCertificazioniRichieste;
	private int numeroSchedeTecniche;
	private boolean verificaStazioneApp;

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public boolean isHasImmagine() {
		return hasImmagine;
	}

	public void setHasImmagine(boolean hasImmagine) {
		this.hasImmagine = hasImmagine;
	}

	public int getNumeroCertificazioniRichieste() {
		return numeroCertificazioniRichieste;
	}

	public void setNumeroCertificazioniRichieste(int numeroCertificazioniRichieste) {
		this.numeroCertificazioniRichieste = numeroCertificazioniRichieste;
	}

	public int getNumeroSchedeTecniche() {
		return numeroSchedeTecniche;
	}

	public void setNumeroSchedeTecniche(int numeroSchedeTecniche) {
		this.numeroSchedeTecniche = numeroSchedeTecniche;
	}

	public boolean isComunicazioneExists() {
		return comunicazioneExists;
	}

	public void setComunicazioneExists(boolean comunicazioneExists) {
		this.comunicazioneExists = comunicazioneExists;
	}

	public boolean isVerificaStazioneApp() {
		return verificaStazioneApp;
	}

	public void setVerificaStazioneApp(boolean verificaStazioneApp) {
		this.verificaStazioneApp = verificaStazioneApp;
	}

	public String getBOZZA() {
		return CataloghiConstants.BOZZA;
	}

	public Integer getTIPO_PRODOTTO_SERVIZIO() {
		return CataloghiConstants.TIPO_PRODOTTO_SERVIZIO;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String openPage() {

		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}
		
		// se si proviene dall'EncodedDataAction di ProcessPageRiepilogo con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if (INPUT.equals(this.getTarget()) || "errorWS".equals(this.getTarget())) {
			this.setTarget(SUCCESS);
		}
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_RIEPILOGO);
			this.hasImmagine = (prodottoHelper.getDocumenti().getImmagine() != null);
			this.numeroCertificazioniRichieste = prodottoHelper.getDocumenti().getCertificazioniRichieste().size();
			this.numeroSchedeTecniche = prodottoHelper.getDocumenti().getSchedeTecniche().size();
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			this.comunicazioneExists = (carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo()).getIdComunicazioneModifiche() != null);
			/*
			 In caso di modifica di un prodotto soggetto a verifica vanno intercettate le modifiche a parametri qualitativi 
			 (file, descrizioni, e comunque tutti i dati non quantitativi) per presentare, nella pagina di riepilogo, 
			 un messaggio indicante "Il prodotto, prima di essere nuovamente disponibile nel catalogo, sar√† 
			 soggetto a verifica da parte della Stazione Appaltante".
			 */
			if (prodottoHelper.getArticolo().getDettaglioArticolo().isProdottoDaVerificare()) {
				try {
					ProdottoType prodottoASistema = this.cataloghiManager.getProdotto(prodottoHelper.getDettaglioProdotto().getId());
					if (!StringUtils.stripToEmpty(prodottoASistema.getMarcaProdottoProduttore())
						.equals(prodottoHelper.getDettaglioProdotto().getMarcaProdottoProduttore())) {
						this.verificaStazioneApp = true;
					} else if (!StringUtils.stripToEmpty(prodottoASistema.getCodiceProdottoProduttore())
								.equals(prodottoHelper.getDettaglioProdotto().getCodiceProdottoProduttore())) {
						this.verificaStazioneApp = true;
					} else if (!StringUtils.stripToEmpty(prodottoASistema.getNomeCommerciale())
								.equals(prodottoHelper.getDettaglioProdotto().getNomeCommerciale())) {
						this.verificaStazioneApp = true;
					} else if (!StringUtils.stripToEmpty(prodottoASistema.getCodiceProdottoFornitore())
								.equals(prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore())) {
						this.verificaStazioneApp = true;
					} else if (!StringUtils.stripToEmpty(prodottoASistema.getDescrizioneAggiuntiva())
								.equals(prodottoHelper.getDettaglioProdotto().getDescrizioneAggiuntiva())) {
						this.verificaStazioneApp = true;
					} else if (!StringUtils.stripToEmpty(prodottoASistema.getDimensioni())
								.equals(prodottoHelper.getDettaglioProdotto().getDimensioni())) {
						this.verificaStazioneApp = true;
					} else if (prodottoHelper.isAggiornatoDocumenti()) {
						this.verificaStazioneApp = true;
					}
				} catch (Throwable ex) {
					// ho convertito una bozza in un prodotto inserito, quindi non c'Ë a db, 
					// setto la verifica manualmente
					this.verificaStazioneApp = true;
				}
			}
			prodottoHelper.getDettaglioProdotto().setStato(this.verificaStazioneApp
							? CataloghiConstants.STATO_PRODOTTO_IN_ATTESA
							: CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO);
		}
		return this.getTarget();
	}
	
}
