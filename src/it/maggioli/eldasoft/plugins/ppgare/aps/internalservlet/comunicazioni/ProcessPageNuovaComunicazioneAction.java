package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

public class ProcessPageNuovaComunicazioneAction extends AbstractProcessPageComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8667835724591043008L;

	protected String oggetto;
	protected String testo;
	protected String nextResultAction;
	protected String from;
	
	
	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getNextResultAction() {
		return nextResultAction;
	}
	
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			helper.setOggetto(this.getOggetto());
			helper.setTesto(this.getTesto());
			this.from = (String)this.session.get(ComunicazioniConstants.SESSION_ID_FROM);

			this.nextResultAction = InitNuovaComunicazioneAction.setNextResultAction(
					helper.getNextStepNavigazione(WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE));

		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
	@Override
	public void validate() {
		super.validate();

		try {
			WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
			
			this.setInfoPerProtocollazione(helper);
			// imposta la stazione appaltante per recuperare i parametri
			this.getAppParamManager().setStazioneAppaltanteProtocollazione(this.getCodiceSA());
			
			Integer tipoProtocollazione = (Integer) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_TIPO);
			String codiceSistemaProtocollazione = (String) this.getAppParamManager().getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
	
			if ((PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM == tipoProtocollazione)
				 && IWSDMManager.CODICE_SISTEMA_TITULUS.equals(codiceSistemaProtocollazione)
				 && this.oggetto.length() < CommonSystemConstants.PROTOCOLLAZIONE_OGGETTO_MIN_LENGTH) 
			{
				// Titulus: vincola l'invio di un oggetto di dimensione pari almeno
				// a 30 caratteri
				this.addFieldError("oggetto",
								   this.getText("Errors.minStringLength", new String[] {
										   this.getTextFromDB("oggetto"),
										   String.valueOf(CommonSystemConstants.PROTOCOLLAZIONE_OGGETTO_MIN_LENGTH) }));
			} else {
				//WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
				//	.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
				this.from = (String)this.session.get(ComunicazioniConstants.SESSION_ID_FROM);
				helper.setOggetto(this.oggetto);
				helper.setTesto(this.testo);
			}
		} catch (Exception e) {
			this.addActionError(this.getText("Errors.unexpected"));
			ApsSystemUtils.getLogger().error("validate", e);
		}
		// resetta la stazione appaltante per recuperare i parametri
		this.getAppParamManager().setStazioneAppaltanteProtocollazione(null);
		
		if (this.getFieldErrors().size() > 0) {
			return;
		}
	}
	
}
