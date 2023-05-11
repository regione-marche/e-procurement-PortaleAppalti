package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;

import com.agiletec.aps.system.SystemConstants;

/**
 * Action di gestione delle operazioni nella pagina di selezione degli articoli
 * da esportare al termine del wizard.
 *
 * @author Marco.Perazzetta
 */
public class ProcessSelezioneArticoliAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159776491L;

	private ICataloghiManager cataloghiManager;

	@Validate(EParamValidation.GENERIC)
	private String[] codiceArticoli;
	@Validate(EParamValidation.GENERIC)
	private String[] articoliSelezionati;
	@Validate(EParamValidation.GENERIC)
	private String catalogo;

	
	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public String[] getCodiceArticoli() {
		return codiceArticoli;
	}

	public void setCodiceArticoli(String[] codiceArticoli) {
		this.codiceArticoli = codiceArticoli;
	}

	public String[] getArticoliSelezionati() {
		return articoliSelezionati;
	}

	public void setArticoliSelezionati(String[] articoliSelezionati) {
		this.articoliSelezionati = articoliSelezionati;
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
	public String next() {
		String target = SUCCESS;

		if (null == this.getCurrentUser() 
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else if (this.articoliSelezionati == null) {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.addActionError(this.getText("Errors.articoliNotSet"));
			target = INPUT;
		}
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = "back";
		return target;
	}
	
}
