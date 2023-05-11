package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang3.StringUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class OpenPageNuovaComunicazioneAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6339166723605760212L;

	@Validate(EParamValidation.GENERIC)
	protected String tipoRichiesta;
	@Validate(EParamValidation.OGGETTO_COMUNICAZIONE)
	private String oggetto;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String testo;
	@Validate(EParamValidation.ACTION)
	private String from;
	@Validate(EParamValidation.DIGIT)
	private String id;

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
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	// "costanti" per la pagina jsp
	public String getSTEP_TESTO_COMUNICAZIONE() {
		return WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE;
	}

	public String getSTEP_DOCUMENTI() {
		return WizardNuovaComunicazioneHelper.STEP_DOCUMENTI;
	}

	public String getSTEP_INVIO_COMUNICAZIONE() {
		return WizardNuovaComunicazioneHelper.STEP_INVIO_COMUNICAZIONE;
	}
	
	/**
	 * ... 
	 */
	public String openPage() {
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		if(helper != null) {
			
			// solo in caso di contratti LFS
			boolean contrattoLFS = (PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equals(helper.getEntita()));			
			if(contrattoLFS && StringUtils.isNotEmpty(helper.getTipoRichiesta())) {
				this.setTipoRichiesta(helper.getTipoRichiesta());
			}
			this.setOggetto(helper.getOggetto());
			this.setTesto(helper.getTesto());
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE);
		return SUCCESS;
	}

	/**
	 * ... 
	 */
	public String openPageAfterError() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardNuovaComunicazioneHelper.STEP_TESTO_COMUNICAZIONE);
		return this.getTarget();
	}

}
