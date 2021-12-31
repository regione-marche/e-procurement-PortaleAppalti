package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IRaggruppamenti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei componenti di una RTI o
 * di un consorzio.
 *
 * @author Marco.Perazzetta
 */
public class ProcessPageComponentiAction extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ProcessPageComponentiAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2219453004709516212L;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;

	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * ... 
	 */
	@Override
	protected IRaggruppamenti getSessionHelper() {
		return (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	}

	
	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.nextResultAction = helper.getPreviousAction(WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
		}
		return target;
	}
	
	/**
	 * ... 
	 */
	public String next() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session		
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.getRagioneSociale().length() > 0) {
				// sono stati inseriti dei dati, si procede al salvataggio
				if (StringUtils.isNotBlank(this.getId())) {
					// aggiorna i dati in sessione (codice preso da modify)
					IComponente componente = null;
					int id = Integer.parseInt(this.getId());
					
					if(helper.getImpresa().isConsorzio()) {
						componente = helper.getComponenti().get(id);
					} else {
						componente = helper.getComponentiRTI().get(id);						
					}
					
					ProcessPageComponentiAction.synchronizeComponente(this, componente);
				} else {
					// aggiunge i dati in sessione (codice preso da insert)
					ComponenteHelper componente = new ComponenteHelper();
					ProcessPageComponentiAction.synchronizeComponente(this, componente);
					
					if(helper.isRti()) {
						helper.getComponentiRTI().add(componente);
					} else {
						helper.getComponenti().add(componente);
					}
				}
			}

			if (helper.checkQuota()) {
				// controlli solo per gare telematiche e non per iscrizioni albo/catalogo
				if (helper.isRti() && StringUtils.isBlank(this.getStrQuotaRTI())) {
					this.addActionError(this.getText("Errors.requiredstring",
										new String[] {this.getTextFromDB("quotaRTI")}));
					target = "refresh";
				} else if (!helper.isRti() && (StringUtils.isNotBlank(this.getStrQuotaRTI()))) {
					this.addActionError(this.getText("Errors.wrongQuotaRTI"));
					target = "refresh";
				} else if (helper.isRti() && StringUtils.isNotBlank(this.getStrQuotaRTI())) {
					try {
						this.setQuotaRTI(Double.parseDouble(this.getStrQuotaRTI()));
					} catch (NumberFormatException ex) {
						this.setQuotaRTI(0.0);
					}
					helper.setQuotaRTI(this.getQuotaRTI());
					if (this.getQuotaRTI() == 0) {
						this.addActionError(this.getTextFromDB("quotarangeRTI"));
						target = "refresh";
					}
				}
				
				// verifica il numero massimo di decimali ammessi
				if( !checkNumeroDecimali(this.getStrQuotaRTI(), QUOTA_MAX_DECIMALS) ) {
					this.addFieldError(this.getTextFromDB("quotarangeRTI"), this.getText("quotamaxdecimals", 
							new String[] {Integer.toString(QUOTA_MAX_DECIMALS), this.getStrQuotaRTI()}));
					target = "refresh";
				}
			}

			if (helper.isRti() && helper.getComponentiRTI().size() <= 1) {
				this.addActionError(this.getText("Errors.numeroMandantiNonSufficiente"));
				target = "refresh";
			} else if (helper.getImpresa().isConsorzio() && helper.getComponenti().size() < 1 && !helper.isRti()) {
				try {
					if (this.getCustomConfigManager().isMandatory("GARE-CONSORZIATE", "ESECUTRICI")) {
						this.addActionError(this.getText("Errors.numeroConsorziateNonSufficiente"));
						target = "refresh";
					} else {
						// caso 0 consorziate esecutrici, configurazione senza obbligo
						if (StringUtils.isEmpty(this.getNoConsorziate())) {
							// entro in questo ramo se ho dato la conferma a proseguire
							this.setConfirmNoConsorziate(true);
							target = "refresh";
						}
					}
				} catch (Exception e) {
					this.addActionError("Missing configuration GARE-CONSORZIATE.ESECUTRICI with feature MAN");
					// il fatto che si faccia il check su una configurazione scritta
					// male deve provocare il fallimento della validazione in modo
					// da accorgermene
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			}
						
			double sommaQuote = helper.getQuotaRTI() != null ? helper.getQuotaRTI() : 0;
			
			// siccome vado avanti, procedo con la verifica dei record in lista
			for (int i = 0; i < helper.getComponenti().size(); i++) {

				IComponente componente = helper.getComponenti().get(i);
				if (StringUtils.isBlank(componente.getRagioneSociale())
					|| StringUtils.isBlank(componente.getNazione())
					|| (StringUtils.isBlank(componente.getPartitaIVA())
					    && StringUtils.isBlank(componente.getCodiceFiscale()))
					|| (helper.checkQuota() && helper.isRti() && componente.getQuota() == null)
					|| StringUtils.isBlank(componente.getTipoImpresa())) {

					if (helper.isRti()) {
						this.addActionError(this.getText("Errors.datiMandanteIncompleti",
														 new String[] {i + 1 + ""}));
					} else {
						this.addActionError(this.getText("Errors.datiConsorziataIncompleti",
														 new String[] {i + 1 + ""}));
					}					
					target = "refresh";
				} else if (helper.isRti() && componente.getQuota() != null) {										
					sommaQuote += componente.getQuota();
				}
			}
			BigDecimal bd = new BigDecimal(sommaQuote).setScale(5, BigDecimal.ROUND_HALF_UP);
			sommaQuote = bd.doubleValue();

			// Controllo QUOTA > 100%
			if (helper.checkQuota() && helper.isRti()) {				
				// somma < 100% 	=> errore
				// somma 100% 		=> OK
				// somma > 100% 	=> warning e proseguo
				if(sommaQuote < 100) {
					this.addActionError(this.getText("Errors.quotaComponentiNonRaggiunta"));
					target = "refresh";
				} else if(sommaQuote > 100) {
					this.addActionMessage(this.getText("Warnings.quotaComponentiSuperiore100"));
				}
			}
		}
		
		this.nextResultAction = helper.getNextAction(WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
	
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	@Override
	public String delete() {
		String target = "refresh";
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper)getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			if (this.getIdDelete() != null) {
				if(helper.isRti()) {
					helper.getComponentiRTI().remove(Integer.parseInt(this.getIdDelete()));
				} else {	
					helper.getComponenti().remove(Integer.parseInt(this.getIdDelete()));
				}
			}
		}
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String insert() {
		String target = "refresh";
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			if (this.getRagioneSociale().length() > 0) {
				ComponenteHelper componente = new ComponenteHelper();
				ProcessPageComponentiAction.synchronizeComponente(this, componente);
				
				if(!helper.isRti()) {
					helper.getComponenti().add(componente);
				}else{
					helper.getComponentiRTI().add(componente);
				}
			}
		}
		
		return target;
	}
	
}
