package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.SystemConstants;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class ProcessPageDocumentiNuovaRettificaAction extends ProcessPageDocumentiNuovaComunicazioneAction {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2339028454441808712L;

	/**
	 * costruttore
	 */
	public ProcessPageDocumentiNuovaRettificaAction() {
		super(new WizardRettificaHelper());
	}
	
	@Override
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardRettificaHelper helper = (WizardRettificaHelper) getWizardFromSession();

			if(helper.getDocumenti().getDocsCount() > 0) {
				// se ci sono degli allegati, procedi con l'"Avanti >" standard
				target = super.next();
			} else {
				addActionError(getText("Errors.attachAtLeastOneDocument"));
				target = INPUT;
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}
	
}
