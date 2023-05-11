package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

public class OpenPageRinnovoRiepilogoAction extends OpenPageRiepilogoAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2713913220268770899L;
	
	public String getSTEP_IMPRESA() {
		return WizardRinnovoHelper.STEP_IMPRESA;
	}
	
	public String getSTEP_SCARICA_ISCRIZIONE() {
		return WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE;
	}
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO() {
		return WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO;
	}

	public String getSTEP_PRESENTA_ISCRIZIONE() {
		return WizardRinnovoHelper.STEP_PRESENTA_ISCRIZIONE;
	}
	
	/**
	 * ... 
	 */
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
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		if (rinnovoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			try {
				this.documentiRichiesti = this.bandiManager
						.getDocumentiRichiestiRinnovoIscrizione(
								rinnovoHelper.getIdBando(), 
								rinnovoHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
								rinnovoHelper.isRti());
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
							 WizardRinnovoHelper.STEP_PRESENTA_ISCRIZIONE);
		}
		return this.getTarget();
	}
	
}
