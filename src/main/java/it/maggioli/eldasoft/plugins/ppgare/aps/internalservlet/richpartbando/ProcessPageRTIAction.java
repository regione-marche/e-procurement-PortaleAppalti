package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.SystemConstants;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

/**
 * Action di gestione delle operazioni nella pagina della RTI del wizard di
 * partecipazione ad una gara.
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageRTIAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private Integer rti;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String denominazioneRTI;
//	private String tipoRaggruppamento;

	public Integer getRti() {
		return rti;
	}

	public void setRti(Integer rti) {
		this.rti = rti;
	}

	public String getDenominazioneRTI() {
		return denominazioneRTI;
	}

	public void setDenominazioneRTI(String denominazioneRTI) {
		this.denominazioneRTI = denominazioneRTI;
	}
	
//	public String getTipoRaggruppamento() {
//		return tipoRaggruppamento;
//	}
//
//	public void setTipoRaggruppamento(String tipoRaggruppamento) {
//		this.tipoRaggruppamento = tipoRaggruppamento;
//	}

	
	@Override
	public void validate() {
		super.validate();
		if ((this.rti == 1 && this.denominazioneRTI.length() == 0)
			 || (this.rti == 0 && this.denominazioneRTI.length() > 0)) 
		{
			this.addFieldError("denominazioneRTI", this.getText("Errors.wrongDenominazioneRTI"));
		}
		
//		if ((this.rti == 1 && this.tipoRaggruppamento.length() == 0)
//			 || (this.rti == 0 && this.tipoRaggruppamento.length() > 0)) 
//		{
//			this.addFieldError("tipoRaggruppamento", this.getText("Errors.wrongTipoRaggruppamento"));
//		}
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		IRaggruppamenti helper = GestioneBuste.getPartecipazioneFromSession().getHelper();
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)helper;
		
		if (helper != null &&
			(null != this.getCurrentUser() 
			 && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) {

			// la sessione non e' scaduta, per cui proseguo regolarmente
			helper.setRti(this.rti == 1);
			if (helper.isRti()) {
				partecipazioneHelper.setDenominazioneRTI(this.denominazioneRTI);
//				partecipazioneHelper.setTipoRaggruppamento(this.tipoRaggruppamento);
			} else {
				partecipazioneHelper.setDenominazioneRTI(null);
//				partecipazioneHelper.setTipoRaggruppamento(null);
				partecipazioneHelper.setQuotaRTI(null);
				for (IComponente componente : helper.getComponenti()) {
					componente.setQuota(null);
				}
			}
			
			// NB: se RTI=False (Forma di partecipazione RTI=No)
			//     a) per le imprese indica che non ci sono compontenti nel 
			//     raggruppamento, quindi WizardPartecipazioneHelper.componenti[] 
			//     deve essere vuota.
			//     b) per i consorzi che non hanno lo step delle consorziate 
			//     abilitato, WizardPartecipazioneHelper.componenti[] deve
			//     essere vuota.
			if( !partecipazioneHelper.isRti() && partecipazioneHelper.getComponenti().size() > 0 ) { 
				if( (!partecipazioneHelper.getImpresa().isConsorzio()) || 		// caso a) 
					 (partecipazioneHelper.getImpresa().isConsorzio() &&		// caso b) 
					  !partecipazioneHelper.isStepComponentiAbilitato()) )
				{
					// svuota la lista dei componenti...
					partecipazioneHelper.getComponenti().clear();
				}
			}

			// lo step STEP_COMPONENTI dipende dinamicamente dalla pagina JSP
			// abilita sempre lo step dei componenti in "OpenPageRTIAction"  
			// mentre in "ProcessPageRTIAction" viene deciso se lo step componenti
			// è abilitato nella navigazione del wizard
			// NB: gestito con javascript in "datiRTISection"
			partecipazioneHelper.abilitaStepNavigazione(
					WizardPartecipazioneHelper.STEP_COMPONENTI, 
					partecipazioneHelper.isStepComponentiAbilitato());
			
			// passa al prossimo step...
			String newTarget = partecipazioneHelper.getNextStepTarget(WizardPartecipazioneHelper.STEP_RTI);
			target = (newTarget != null ? newTarget : target);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

}
