package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dell'impresa del wizard
 * d'iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({
	EFlussiAccessiDistinti.OFFERTA_GARA,
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class ProcessPageImpresaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;

	
	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			if (!iscrizioneHelper.getImpresa().validate()) {
				this.addActionError(this.getText("Errors.datiImpresaIncompleti"));
				target = INPUT;
			} else {
				this.nextResultAction = iscrizioneHelper.getNextAction(WizardIscrizioneHelper.STEP_IMPRESA);
			}
		}
		return target;
	}
	
}
