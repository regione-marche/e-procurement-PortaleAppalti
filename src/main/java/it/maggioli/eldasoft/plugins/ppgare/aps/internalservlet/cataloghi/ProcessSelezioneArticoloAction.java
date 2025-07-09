package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardArticoloHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;

/**
 * Action di gestione delle operazioni nella pagina di selezione dell'articolo
 * del wizar di inserimento prodotto
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class ProcessSelezioneArticoloAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3541528394427260908L;

	private ICataloghiManager cataloghiManager;

	@Validate(EParamValidation.GENERIC)
	private String articoloSelezionato;
	@Validate(EParamValidation.GENERIC)
	private String catalogo;


	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public String getArticoloSelezionato() {
		return articoloSelezionato;
	}

	public void setArticoloSelezionato(String articoloSelezionato) {
		this.articoloSelezionato = articoloSelezionato;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = "back";
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public void validate() {
		super.validate();

		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		CarrelloProdottiSessione carrello = (CarrelloProdottiSessione) this.session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);

		if (prodottoHelper != null && carrello != null && this.articoloSelezionato != null) {
			// verifico che sia stato selezionato un articolo per cui sia ancora 
			try {
				Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(new Long(this.articoloSelezionato), this.getCurrentUser().getUsername());
				long numeroProdottiOECarrello = carrello.calculateProdottiCaricatiOE(new Long(this.articoloSelezionato));
				Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
				if (prodottiCaricati >= prodottoHelper.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo()) {
	 				this.addActionError(this.getText("Errors.limiteInserimentoProdottiPerArticolo",
										new String[] { prodottoHelper.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo() + "" }));
				}
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "validate");
				ExceptionUtils.manageExceptionError(e, this);
			}
		}
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);

		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {

			if (this.articoloSelezionato == null) {
				this.addActionError(this.getText("Errors.articoloNotSet"));
				target = INPUT;
			}

			// solo se i controlli sono andati a buon fine ed ho categorie nella
			// pagina aggiorno i dati in sessione
			if (SUCCESS.equals(target)) {
				target = this.update(prodottoHelper);
			}
		}
		return target;
	}

	/**
	 * ... 
	 */
	private String update(WizardProdottoHelper prodottoHelper) {
		String target = SUCCESS;
		
		if (this.articoloSelezionato != null) {
			WizardArticoloHelper articoloHelper = prodottoHelper.getArticolo();
			if (articoloHelper == null) {
				articoloHelper = new WizardArticoloHelper();
			}
			try {
				articoloHelper.setIdArticolo(new Long(this.articoloSelezionato));
				articoloHelper.setDettaglioArticolo(this.cataloghiManager.getArticolo(new Long(this.articoloSelezionato)));
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "update");
				ExceptionUtils.manageExceptionError(e, this);
			}

			prodottoHelper.setArticolo(articoloHelper);

		}
		return target;
	}
	
}
