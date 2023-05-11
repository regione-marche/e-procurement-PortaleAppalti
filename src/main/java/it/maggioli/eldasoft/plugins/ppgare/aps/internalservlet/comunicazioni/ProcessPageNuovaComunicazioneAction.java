package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;

/**
 * ... 
 * 
 */
public class ProcessPageNuovaComunicazioneAction extends AbstractProcessPageComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8667835724591043008L;

	@Validate(EParamValidation.GENERIC)
	protected String tipoRichiesta;
	@Validate(EParamValidation.OGGETTO_COMUNICAZIONE)
	protected String oggetto;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	protected String testo;
	@Validate(EParamValidation.ACTION)
	protected String nextResultAction;
	@Validate(EParamValidation.OGGETTO)
	protected String from;
	
	public String getTipoRichiesta() {
		return tipoRichiesta;
	}

	public void setTipoRichiesta(String tipoRichiesta) {
		this.tipoRichiesta = tipoRichiesta;
	}

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
	
	/**
	 * ...
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			helper.setTipoRichiesta(this.getTipoRichiesta());
			helper.setOggetto(this.getOggetto());
			helper.setTesto(this.getTesto());

			this.nextResultAction = InitNuovaComunicazioneAction.setNextResultAction(
					helper.getNextStepNavigazione(WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE));

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
	public void validate() {
		super.validate();

		try {
			WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);

			// se "tipo richiesta" e' visibile allora e' obbligatorio (solo per contratti LFS)...
			if(PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(helper.getEntita())) { 
				LinkedHashMap<String, String> listaTipiRichiesta =  InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPOLOGIE_COMUNICAZIONI);
				if(listaTipiRichiesta != null && listaTipiRichiesta.size() > 0) {
					if(StringUtils.isEmpty(this.getTipoRichiesta())) {
						this.addFieldError("tipoRichiesta", 
										   this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("tipoRichiesta") }));
						return;
					}
				}
			}

			if(PortGareSystemConstants.ENTITA_STIPULA.equalsIgnoreCase(helper.getEntita()) ||
			   PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(helper.getEntita())) 
			{
				// SE CONTRATTO LFS O STIPULA PER ORA NON SI PROTOCOLLA !!!
				return;
			}
			
			try {
				this.setInfoPerProtocollazione(helper);
				
				// imposta la stazione appaltante per recuperare i parametri
				this.getAppParamManager().setStazioneAppaltanteProtocollazione(this.getCodiceSA());
				
				//Integer tipoProtocollazione = (Integer) this.getAppParamManager().getTipoProtocollazione(this.getCodiceSA());  ?????
				Integer tipoProtocollazione = (Integer) this.getAppParamManager().getTipoProtocollazione(this.getCodiceSA());
				
				// in caso di protocollazione WSDM se non esiste una configurazione segnala un errore
				if(this.getAppParamManager().isConfigWSDMNonDisponibile()) {
					//this.addMActionError(this.getText("Errors.wsdm.configNotAvailable"));
					//return;
					this.addActionMessage(this.getText("Errors.wsdm.configNotAvailable"));
				}
	
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
					helper.setOggetto(this.oggetto);
					helper.setTesto(this.testo);
				}
		
			} catch (Throwable t) {
				ExceptionUtils.manageWSDMExceptionError(t, this);
				ApsSystemUtils.getLogger().error("validate", t);
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
