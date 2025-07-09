package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import com.agiletec.aps.system.SystemConstants;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;


/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class ProcessPageNuovaRettificaAction extends ProcessPageNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2001516413003422834L;

	@Validate(EParamValidation.GENERIC)
	private String tipoBusta;
	@Validate(EParamValidation.CODICE)
	private String lotto;
	
	public String getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(String tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getLotto() {
		return lotto;
	}

	public void setLotto(String lotto) {
		this.lotto = lotto;
	}

	/**
	 * costruttore
	 */
	public ProcessPageNuovaRettificaAction() {
		super(new WizardRettificaHelper());
	}

	@Override
	public String next() {
		String target = super.next();
		
		if( SUCCESS.equals(target) ) { 			
			WizardRettificaHelper rettifica = (WizardRettificaHelper) getWizardFromSession();;
			
			if (null != this.getCurrentUser()
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
			{
				rettifica.setTipoBusta( stringToLong(this.tipoBusta, 0L) );
				rettifica.setCodice2(lotto);
				
				this.nextResultAction = InitNuovaComunicazioneAction.setNextResultAction(
						helper.getNextStepNavigazione(helper.STEP_TESTO_COMUNICAZIONE));			
			} else {
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}
	
	/**
	 * ...
	 */
	private Long stringToLong(String value, Long defaultValue) {
		try {
			return Long.parseLong(value);
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
}
