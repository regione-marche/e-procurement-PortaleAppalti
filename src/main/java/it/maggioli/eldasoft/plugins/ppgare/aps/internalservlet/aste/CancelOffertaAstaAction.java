package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

/**
 * Action di gestione dell'annullamento di un'offerta df'asta.
 * 
 * @author ...
 */
public class CancelOffertaAstaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 1965795852167320822L;
	

	protected Map<String, Object> session;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
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
	public String questionCancel() {
		String target = SUCCESS;
		
		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			//target = (helper.isLottiDistinti() ? "successLottiDistinti" : SUCCESS);
		}
		
		return target;
	}
	
	/**
	 * ... 
	 */
	public String cancel() {
		String target = SUCCESS;
		
		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.codice = helper.getAsta().getCodice();
			this.codiceLotto = helper.getAsta().getCodiceLotto();
			
			target = (helper.isLottiDistinti() ? "successLottiDistinti" : SUCCESS);
			
			// rimuovi dalla sessione l'helper del wizard...
//			this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
			helper.removeFromSession();
		}
		
		return target;
	}
	
}

