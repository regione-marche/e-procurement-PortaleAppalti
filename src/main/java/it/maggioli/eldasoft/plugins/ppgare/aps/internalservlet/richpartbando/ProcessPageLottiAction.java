package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Action di gestione delle operazioni nella pagina della selezione lotti del
 * wizard di partecipazione ad una gara
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageLottiAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	@Validate(EParamValidation.GENERIC)
	private String[] lottoSelezionato;

	public String[] getLottoSelezionato() {
		return lottoSelezionato;
	}

	public void setLottoSelezionato(String[] lottoSelezionato) {
		this.lottoSelezionato = lottoSelezionato;
	}

	/**
	 * ... 
	 */
	@Override
	public void validate() {
		super.validate();
		if (this.lottoSelezionato == null) {
			this.addFieldError("lottoSelezionato", this.getText("Errors.lottoSelezionatoNotSet"));
		}
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
			
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();
	
		if (partecipazioneHelper != null 
			&& (null != this.getCurrentUser() 
			    && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{
			TreeSet<String> setLotti = new TreeSet<String>();
			List<String> lottiSelezionati = new ArrayList<String>();
			if (this.lottoSelezionato != null) {
				lottiSelezionati = Arrays.asList(this.lottoSelezionato);
				setLotti = new TreeSet<String>(lottiSelezionati);
			}
			partecipazioneHelper.setLotti(setLotti);
			partecipazioneHelper.setLottiAmmessiInOfferta(lottiSelezionati);

			String newTarget = partecipazioneHelper.getNextStepTarget(WizardPartecipazioneHelper.STEP_LOTTI);
			target = (newTarget != null ? newTarget : target);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String cancel() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = "back";

		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if (partecipazioneHelper != null 
			&& (null != this.getCurrentUser()
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{	
			String newTarget = partecipazioneHelper.getPreviousStepTarget(WizardPartecipazioneHelper.STEP_LOTTI);
			target = (newTarget != null ? newTarget : target);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
}
