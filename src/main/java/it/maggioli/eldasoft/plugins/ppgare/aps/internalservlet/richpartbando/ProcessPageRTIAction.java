package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import org.apache.commons.lang3.StringUtils;

import com.agiletec.aps.system.SystemConstants;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

/**
 * Action di gestione delle operazioni nella pagina della RTI del wizard di
 * partecipazione ad una gara.
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageRTIAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private Integer rti;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String denominazioneRTI;
//	private String tipoRaggruppamento;
	@Validate(EParamValidation.CODICE_CNEL)
	private String codiceCNEL;

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
	
	public String getCodiceCNEL() {
		return codiceCNEL;
	}

	public void setCodiceCNEL(String codiceCNEL) {
		this.codiceCNEL = codiceCNEL;
		if (!StringUtils.equals(this.codiceCNEL, "n.a.")) {
			this.codiceCNEL = StringUtils.upperCase(codiceCNEL);
		}
	}

	/**
	 * ... 
	 */
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

		// valida CNEL solo per le gare, per gli elenchi operatore non e' previsto
		GestioneBuste buste = GestioneBuste.getFromSession();
		if (buste != null && buste.isInvioOfferta()) {
			// codiceCNEL deve essere di 4 caratteri
			if (StringUtils.isEmpty(this.codiceCNEL)) {
				this.addFieldError("codiceCNEL", this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("codiceCNEL") }));
			} else if (this.codiceCNEL.length() != 4) {
				this.addFieldError("codiceCNEL", this.getText("Errors.stringlength", new String[] { this.getTextFromDB("codiceCNEL"), "4" }));
			}
		}
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		IRaggruppamenti helper = (buste != null ? buste.getBustaPartecipazione().getHelper() : null);
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

			if(buste.isInvioOfferta()) {
				partecipazioneHelper.setCodiceCNEL(codiceCNEL);
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
