package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * classe base per la gestione dell'apertura di uno step del wizard della comunicazione, soccorso istruttorio, rettifica...
 *  
 */
public abstract class AbstractOpenPageComunicazioneAction extends AbstractOpenPageAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -5405693850915046651L;

	protected WizardNuovaComunicazioneHelper helper;
	protected String helperSessionId; 

	
	/**
	 * costruttore generico
	 */
	public AbstractOpenPageComunicazioneAction(WizardNuovaComunicazioneHelper helper, String helperSessionId) {
		super();
		this.helper = helper;
		this.helperSessionId = helperSessionId;
	}
	
	public AbstractOpenPageComunicazioneAction() {
		this(null, null);
	}
	
	protected Object getWizardFromSession() {
		helper = (WizardNuovaComunicazioneHelper) session.get(helperSessionId);
		return helper;
	}
	
	protected void putWizardToSession() {
		session.put(helperSessionId, helper);
	}			

	@Override
	public String openPage() {
		ApsSystemUtils.getLogger().error("Implementazione di openPage mancante in " + this.getClass().getSimpleName());
		return CommonSystemConstants.PORTAL_ERROR;
	}
	
	/**
	 * espone i nomi degli step del wizard alle pagine jsp
	 */
	public String getSTEP_TESTO_COMUNICAZIONE() { 
		return helper.STEP_TESTO_COMUNICAZIONE; 
	}
	
	public String getSTEP_DOCUMENTI() { 
		return helper.STEP_DOCUMENTI; 
	}
	
	public String getSTEP_INVIO_COMUNICAZIONE() { 
		return helper.STEP_INVIO_COMUNICAZIONE; 
	}
	
}
