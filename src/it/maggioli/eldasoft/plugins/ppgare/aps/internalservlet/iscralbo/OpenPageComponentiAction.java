package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;


import org.apache.commons.lang.StringUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IRaggruppamenti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ProcessPageComponentiAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina della RTI del wizard di
 * iscrizione ad un elenco operatori o catalogo elettronico.
 *
 * @author Marco.Perazzetta
 */
public class OpenPageComponentiAction extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.OpenPageComponentiAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159987491L;
	
	
	private boolean liberoProfessionista;

	
	public boolean isLiberoProfessionista() {
		return liberoProfessionista;
	}

	public void setLiberoProfessionista(boolean liberoProfessionista) {
		this.liberoProfessionista = liberoProfessionista;
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

	/**
	 * ...
	 */
	public String openPage() {
		this.setNazione("Italia");
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
		return this.getTarget();
	}
	
	/**
	 * Apre la pagina svuotando i dati del form di inserimento/modifica
	 *
	 * @return target
	 */
	public String openPageClear() {
		IRaggruppamenti helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente.
			// svuota i dati inseriti nella form in modo da riaprire la
			// form pulita.
			ProcessPageComponentiAction.resetComponente(this);
			this.setLiberoProfessionista(false);
			this.setQuotaRTI(helper.getQuotaRTI());
			this.setId(null);
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PAGINA_COMPONENTI);
		}
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
		return this.getTarget();
	}

	public String openPageModify() {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente.
			// popola nel bean i dati presenti nell'elemento in sessione
			// individuato da id.
			IComponente componente = null;
			if(!helper.isRti()) {
				componente = helper.getComponenti().get(
							Integer.parseInt(this.getId()));
			} else {
				componente = helper.getComponentiRTI().get(
						Integer.parseInt(this.getId()));
			}
			ProcessPageComponentiAction.synchronizeComponente(componente, this);
			if (StringUtils.isNotBlank(this.getTipoImpresa())) {
				this.setLiberoProfessionista(this.getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
								.containsKey(this.getTipoImpresa()));
			}
			this.setQuotaRTI(helper.getQuotaRTI());
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
		}
		return this.getTarget();
	}
	
	/**
	 * Ritorna l'helper in sessione utilizzato per la memorizzazione dei dati
	 * sulla partecipazione in RTI.
	 * 
	 * @return helper contenente i dati per la gestione di RTI e componenti
	 */
	protected IRaggruppamenti getSessionHelper() {
		return (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	}
	
}
