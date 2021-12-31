package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Classe astratta da utilizzare come base per ogni action di process di una
 * pagina di un wizard.<br/>
 * Sostanzialmente estende BaseAction in modo da usufruire delle facilities
 * standard previste per le action jAPS, ed implementa l'interfaccia in modo da
 * usufruire della sessione.
 *
 * @author stefano.sabbadin
 * @since 1.2
 */
public abstract class AbstractProcessPageAction extends BaseAction 
	implements SessionAware {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 5464618686086907166L;

	protected Map<String, Object> session;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * Annulla la procedura guidata.
	 *
	 * @return the cancel target
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * Passa allo step successivo della procedura guidata.
	 *
	 * @return the next step target
	 */
	public abstract String next();

		/**
	 * Torna allo step precedente della procedura guidata.
	 *
	 * @return the back step target
	 */
	public String back() {
		return "back";
	}
	
	
	/**************************************************************************
	 * Gestione ausiliaria per AbstractWizardHelper con relativa navigazione 
	 * per "next", "back", "cancel"
	 * 
	 * Nella classe derivata attivare la gestione semi automatizzata della 
	 * navigazione, configurata dall'helper derivato da AbstractWizardHelper,  
	 * come segue:
	 * 
	 * 	public <costruttore>Action() {
	 * 		super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA, 
	 *			  WizardOffertaAstaHelper.STEP_DATI_OPERATORE
	 *			  [, <esegui validazione>] 			
	 *			  [, "Error.validazione"] );
	 *	} 
	 *
	 *	@Override
	 *	public String next() {
	 *		return this.helperNext();
	 *	}
	 *	
	 *	@Override
	 *	public String back() {
	 *		return this.helperBack();
	 *	}
	 *	
	 *	@Override
	 *	public void validate() {
	 *		this.validated = true;		
	 *	}
     *
	 */
	
	/**
	 * Inizializza l'helper dell'action e la navigazione "next", "back"
	 */
	protected AbstractWizardHelper helper;	// helper associato al wizard
	protected String sessionKeyHelperId;	// id di sessione associato all'helper		
	protected String currentStep;			// step corrente del wizard
	protected boolean doValidate;			// indica se eseguire il metodo "validate()"
	protected boolean validated;			// indica il risultato del medoto "validate()"
	protected String validateErrorMessageId;
	 
	public AbstractProcessPageAction(
			String sessionKeyHelperId, 
			String currentStep,
			boolean doValidate,
			String validateErrorMessageId) {
		this.sessionKeyHelperId = sessionKeyHelperId;
		this.currentStep = currentStep;
		this.doValidate = doValidate;
		this.validateErrorMessageId = validateErrorMessageId;
	}

	public AbstractProcessPageAction(
			String sessionKeyHelperId, 
			String currentStep,
			boolean doValidate) {
		this(sessionKeyHelperId, currentStep, doValidate, null);
	}
	
	public AbstractProcessPageAction(
			String sessionKeyHelperId, 
			String currentStep) {
		this(sessionKeyHelperId, currentStep, false, null);
	}

	public AbstractProcessPageAction() {
		this(null, null, false, null);
	}
	
	private String helperNavigationAction(boolean next) {
		String target = (next ? SUCCESS : "back"); 
		
		this.validated = true;
		
		this.helper = (AbstractWizardHelper) this.session.get(this.sessionKeyHelperId);
		
		if(null != this.getCurrentUser() 
		   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
						
			if(this.helper != null && this.currentStep != null) {
				if(this.doValidate) {
				this.validate();			
				} else {
					this.validated = true;
				}
				if(!this.validated) {
					if(this.validateErrorMessageId != null) {
						this.addActionError(this.getText(this.validateErrorMessageId));
					}
					target = INPUT;
				} else {
					String newTarget = (next 
						? this.helper.getNextStepTarget(this.currentStep)
						: this.helper.getPreviousStepTarget(this.currentStep));					
					if(newTarget != null) {
						target = newTarget;
					}		
				}
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}

	public String helperNext() {
		return this.helperNavigationAction(true);
	}

	public String helperBack() {
		return this.helperNavigationAction(false);
	}

}
