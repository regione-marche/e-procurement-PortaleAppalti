package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.SystemConstants;

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
public abstract class AbstractProcessPageAction extends EncodedDataAction
	implements SessionAware 
{
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
	
	/**
	 * Passa allo step successivo della procedura guidata utilizzando la mappa di navigazione definita dal wizard.
	 *
	 * @return the next step target
	 */
	protected String helperNext() {
		return this.helperNavigationAction(true);
	}

	/**
	 * Torna allo step precedente della procedura guidata utilizzando la mappa di navigazione definita dal wizard.
	 *
	 * @return the back step target
	 */
	protected String helperBack() {
		return this.helperNavigationAction(false);
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
	private int tipoBusta;	
	 
	public AbstractProcessPageAction(
			String sessionKeyHelperId, 
			String currentStep,
			boolean doValidate,
			String validateErrorMessageId) 
	{
		this.sessionKeyHelperId = sessionKeyHelperId;
		this.currentStep = currentStep;
		this.doValidate = doValidate;
		this.validateErrorMessageId = validateErrorMessageId;
		this.tipoBusta = -1;
	}

	public AbstractProcessPageAction(
			String sessionKeyHelperId, 
			String currentStep,
			boolean doValidate) 
	{
		this(sessionKeyHelperId, currentStep, doValidate, null);
	}
	
	public AbstractProcessPageAction(
			String sessionKeyHelperId, 
			String currentStep) 
	{
		this(sessionKeyHelperId, currentStep, false, null);
	}

	public AbstractProcessPageAction() {
		this(null, null, false, null);
	}
	
	public AbstractProcessPageAction(
			int tipoBusta, 
			String currentStep) 
	{
		this(null, currentStep, false, null);
		this.tipoBusta = tipoBusta;
	}

	private AbstractWizardHelper getHelper() {
		if(this.tipoBusta > 0) {
			GestioneBuste buste = GestioneBuste.getFromSession();
			if(this.tipoBusta == BustaGara.BUSTA_PRE_QUALIFICA) {
				return null; 	//buste.getBusta(this.tipoBusta).getHelperDocumenti();
			} else if(this.tipoBusta == BustaGara.BUSTA_AMMINISTRATIVA) {
				return null; 	//buste.getBusta(this.tipoBusta).getHelperDocumenti();
			} else if(this.tipoBusta == BustaGara.BUSTA_TECNICA) {
				return buste.getBustaEconomica().getHelper();
			} else if(this.tipoBusta == BustaGara.BUSTA_ECONOMICA) {
				return buste.getBustaTecnica().getHelper();
			}
			return null;
		} else {
			return (AbstractWizardHelper) this.session.get(this.sessionKeyHelperId);	
		}
	}
	
	private String helperNavigationAction(boolean direction) {
		String target = (direction ? SUCCESS : "back"); 
		
		this.validated = true;
				
		this.helper = this.getHelper();
		
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
					String newTarget = (direction 
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
	
}
