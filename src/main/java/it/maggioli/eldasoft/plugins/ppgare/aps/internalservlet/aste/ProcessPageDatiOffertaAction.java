package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei dati riepilogativi 
 * dell'ultima offerta del wizard di offerta ad un'asta.
 *
 * @author ...
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.ASTA })
public class ProcessPageDatiOffertaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2795797394648795485L;
	
	// "codice" e "codiceLotto" servono quando si ritorna al primo step del wizard
	// altrimenti lo step "Dati operatore" non riesce a caricare l'helper dell'asta
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}

	/**
	 * ... 
	 */
	public ProcessPageDatiOffertaAction() {
		super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA,
			  WizardOffertaAstaHelper.STEP_DATI_OFFERTA,
			  true);
	} 

	/**
	 * ... 
	 */
	@Override
	public String next() {
		return this.helperNext();
	}
	
	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = this.helperBack();
		return (SUCCESS.equalsIgnoreCase(target) ? "back" : target);
	}
	
}
