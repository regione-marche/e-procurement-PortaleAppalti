package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard
 * d'iscrizione all'albo
 * 
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageRiepilogoAction extends AbstractOpenPageAction {
    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    protected IBandiManager bandiManager;

    private List<String> categorieSelezionate;
    protected List<DocumentazioneRichiestaType> documentiRichiesti;
    
    
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public List<String> getCategorieSelezionate() {
		return categorieSelezionate;
	}

	public List<DocumentazioneRichiestaType> getDocumentiRichiesti() {
		return documentiRichiesti;
	}

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
		
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PAGINA_RIEPILOGO);
			
			// si devono visualizzare solo le foglie
			this.categorieSelezionate = new ArrayList<String>();
			for (String cat : iscrizioneHelper.getCategorie().getCategorieSelezionate()) {
				if (iscrizioneHelper.getCategorie().getFoglie().contains(cat)) {
					this.categorieSelezionate.add(cat);
				}
			}

			try {
				this.documentiRichiesti  = iscrizioneHelper.getDocumentiRichiestiBO();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
							 WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE);
		}
		return this.getTarget();
	}

}
