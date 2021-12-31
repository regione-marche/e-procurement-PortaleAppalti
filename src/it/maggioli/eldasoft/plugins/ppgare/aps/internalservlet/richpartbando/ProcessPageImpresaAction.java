package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.SystemConstants;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dell'impresa del wizard di
 * partecipazione ad una gara
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageImpresaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7617871592321998786L;

	/**
	 * ... 
	 */
	@Override
	public String next() {	
		String target = SUCCESS;
	
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

		if(partecipazioneHelper != null 
		   && (null != this.getCurrentUser()
			   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{
			// si effettuano i controlli sui dati anagrafici: se sono completi
			// si consente l'avanzamento del wizard, altrimenti si blocca
			// l'avanzamento e si informa l'utente che deve attivare la funzione
			// di aggiornamento dati anagrafici
			if (!partecipazioneHelper.getImpresa().validate()) {
				this.addActionError(this.getText("Errors.datiImpresaIncompleti"));
				target = INPUT;
			} else {
				// lo step STEP_COMPONENTI dipende dinamicamente dalla pagina JSP
				// abilita sempre lo step dei componenti in "OpenPageRTIAction"  
				// mentre in "ProcessPageRTIAction" viene deciso se lo step componenti
				// è abilitato nella navigazione del wizard 
				partecipazioneHelper.abilitaStepNavigazione(
						WizardPartecipazioneHelper.STEP_COMPONENTI, 
						partecipazioneHelper.isStepComponentiAbilitato());
				
				String newTarget = partecipazioneHelper.getNextStepTarget(WizardPartecipazioneHelper.STEP_IMPRESA);
				target = (newTarget != null ? newTarget : target);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
}
