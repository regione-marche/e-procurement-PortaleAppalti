package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IRaggruppamenti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina della RTI del wizard di
 * iscrizione ad un elenco-catalogo
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageRTIAction extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.OpenPageRTIAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5939974240229775029L;


	public String getSTEP_IMPRESA() {
		return WizardIscrizioneHelper.STEP_IMPRESA;
	}

	public String getSTEP_DENOMINAZIONE_RTI() {
		return WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI;
	}

	public String getSTEP_DETTAGLI_RTI() {
		return WizardIscrizioneHelper.STEP_DETTAGLI_RTI;
	}

	public String getSTEP_SELEZIONE_CATEGORIE() {
		return WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE;
	}

	public String getSTEP_RIEPILOGO_CAGTEGORIE() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE;
	}

	public String getSTEP_SCARICA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE;
	}

	public String getSTEP_DOCUMENTAZIONE_RICHIESTA() {
		return WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA;
	}

	public String getSTEP_PRESENTA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE;
	}
		
	public String getSTEP_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_QUESTIONARIO;
	}
	
	public String getSTEP_RIEPILOGO_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO;
	}
	
		
	@Override
	protected IRaggruppamenti getSessionHelper() {
		return (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	}
	
	/**
	 * ... 
	 */
	public String openPage() {
		String target = SUCCESS;
		IRaggruppamenti helper = this.getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PAGINA_RTI);

			if (!PortGareSystemConstants.WIZARD_PAGINA_RTI.equals(this.getPage())) {
				this.setRti(helper.isRti() ? 1 : 0);
				this.setDenominazioneRTI(helper.getDenominazioneRTI());
			}
		}
		
	    this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI);
	    
		return target;
	}

}
