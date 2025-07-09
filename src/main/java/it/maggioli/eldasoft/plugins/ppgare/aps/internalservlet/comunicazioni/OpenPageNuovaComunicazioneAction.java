package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang3.StringUtils;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE, EFlussiAccessiDistinti.COMUNICAZIONE_STIPULA })
public class OpenPageNuovaComunicazioneAction extends AbstractOpenPageComunicazioneAction {
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

	/**
	 * costruttore
	 */
	public OpenPageNuovaComunicazioneAction() {
		this(new WizardNuovaComunicazioneHelper());
	}
	
	public OpenPageNuovaComunicazioneAction(WizardNuovaComunicazioneHelper helper) {
		super(helper, PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
	}
	
	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		helper = (WizardNuovaComunicazioneHelper) getWizardFromSession();
		
		if(helper != null) {
			boolean rettifica = (helper instanceof WizardRettificaHelper);
			boolean soccorsoIstruttorio = (helper instanceof WizardSoccorsoIstruttorioHelper);
			boolean comunicazione = (!rettifica && ! soccorsoIstruttorio);
			boolean contrattoLFS = (PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equals(helper.getEntita()));
			
			this.setOggetto(helper.getOggetto());
			this.setTesto(helper.getTesto());
			
			// solo per le comunizioni standard e solo in caso di contratti LFS
			if(comunicazione && contrattoLFS && StringUtils.isNotEmpty(helper.getTipoRichiesta())) {
				this.setTipoRichiesta(helper.getTipoRichiesta());
			}
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_TESTO_COMUNICAZIONE);
		
		return SUCCESS;
	}

	/**
	 * ... 
	 */
	public String openPageAfterError() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_TESTO_COMUNICAZIONE);
		return this.getTarget();
	}

}
