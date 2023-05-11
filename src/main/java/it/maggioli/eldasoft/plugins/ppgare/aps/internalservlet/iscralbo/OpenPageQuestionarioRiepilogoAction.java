package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard
 * d'iscrizione all'albo
 * 
 * @author ...
 */
public class OpenPageQuestionarioRiepilogoAction extends AbstractOpenPageAction {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -359113269098520465L;
	private static final Logger logger = ApsSystemUtils.getLogger();
    
    protected IBandiManager bandiManager;
    protected IComunicazioniManager comunicazioniManager;
    protected IAppParamManager appParamManager;


    private List<String> categorieSelezionate;
    protected List<DocumentazioneRichiestaType> documentiRichiesti;
    private Boolean rettifica = Boolean.FALSE;
    
    
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
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
	 * restituisce l'helper di iscrizione/rinnovo in sessione  
	 */
	private WizardIscrizioneHelper getHelperFromSession() {
		WizardIscrizioneHelper a = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		WizardRinnovoHelper b = (WizardRinnovoHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		if(a != null & b == null) {
			return a;
		} else if(b != null) {
			return b;
		}
		return null;
	}

	/**
	 * ... 
	 */
	public String openPage() {
		logger.debug("OpenPageQuestionarioRiepilogoAction - openPage");
		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}

		// se si proviene dall'EncodedDataAction di ProcessPageRiepilogo con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		logger.debug("OpenPageQuestionarioRiepilogoAction - openPage - this.getTarget():{}",this.getTarget());
		if (INPUT.equals(this.getTarget()) || "errorWS".equals(this.getTarget())) {
			this.setTarget(SUCCESS);
		}
		
		WizardIscrizioneHelper helper = this.getHelperFromSession();
		logger.debug("OpenPageQuestionarioRiepilogoAction - openPage - iscrizioneHelper.isnull? {}",(helper==null));

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			try {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			
			// si devono visualizzare solo le foglie
			this.categorieSelezionate = new ArrayList<String>();
			for (String cat : helper.getCategorie().getCategorieSelezionate()) {
				if (helper.getCategorie().getFoglie().contains(cat)) {
					this.categorieSelezionate.add(cat);
				}
			}
			logger.debug("OpenPageQuestionarioRiepilogoAction - openPage - this.categorieSelezionate {}",(this.categorieSelezionate));

			try {
				this.documentiRichiesti  = this.bandiManager
						.getDocumentiRichiestiBandoIscrizione(
								helper.getIdBando(), 
								helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
								helper.isRti());
				logger.debug("documentiRichiesti {}",(this.documentiRichiesti));
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
	 		 		 		 WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO);
			}catch(Exception e) {
				logger.error("OpenPageQuestionarioRiepilogoAction",e);
			}
		}
		logger.debug("OpenPageQuestionarioRiepilogoAction - openPage - {}",this.getTarget());
		return this.getTarget();
	}

	public String openPageToViewOnly() {
		rettifica = Boolean.TRUE;
		return this.openPage();
	}
	/**
	 * @return the rettifica
	 */
	public Boolean getRettifica() {
		return rettifica;
	}
	
	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}
	
	public Integer getLimiteTotaleUploadDocIscrizione() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
	}

}
