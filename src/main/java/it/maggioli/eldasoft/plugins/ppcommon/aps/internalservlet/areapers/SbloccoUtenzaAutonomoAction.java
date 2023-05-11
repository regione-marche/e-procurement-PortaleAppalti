package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.Date;

/**
 * Azione per lo sblocco in autonomia di un utente che risulta 
 * bloccato causa utenza e password scadute, per cui &egrave;
 * attivo.
 * 
 * @author 
 */
public class SbloccoUtenzaAutonomoAction extends BaseAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -2997243366877024701L;

	protected IUserManager userManager;
//	protected IUserRegManager userRegManager;
//	private IEventManager eventManager;

	@Validate(EParamValidation.USERNAME)
	private String username;
	
	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Predispone l'apertura della pagina previo controllo autorizzazione
	 * 
	 * @return target struts di esito dell'operazione
	 */
	@SkipValidation
	public String openSbloccoUtenzaAutonomo() {
		String target = SUCCESS;		 
		if (this.getCurrentUser() == null) {
			this.addActionError(this.getText("Errors.function.notEnabled"));			
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.username = (String)(String)this.getRequest().getSession().getAttribute(SystemConstants.SESSION_SBLOCCO_UTENZA_AUTONOMO);
		}		
		return target;
	}
	
	/**
	 * Metodo che mi permette di riportare alla normalit&agrave; un utente la
	 * cui utenza o la cui password potrebbe essere scaduta. In sostanza
	 * aggiorna le date in modo da renderlo di nuovo operativo.
	 */
	public String sbloccoUtenzaAutonomo() {
		String target = SUCCESS;

//		Event evento = new Event();
//		evento.setLevel(Event.Level.INFO);
//		evento.setEventType(PortGareEventsConstants.BLOCCO_UTENTE);
//		evento.setMessage("Ripristino alla normalità per l'utente " + this.id);
//		evento.setUsername(username);
//		evento.setSessionId(sessionId);
//		evento.setIpAddress(ipAddress);
		boolean removeFromSession = false;
		try {
			this.username = (String)this.getRequest().getSession().getAttribute(SystemConstants.SESSION_SBLOCCO_UTENZA_AUTONOMO);
			
			if (this.getCurrentUser() == null) {
				this.addActionError(this.getText("Errors.function.notEnabled"));
				target = CommonSystemConstants.PORTAL_ERROR;
//				evento.setLevel(Event.Level.ERROR);
//				evento.setDetailMessage("Tentato utilizzo di funzione non abilitata");
			} else {
				/* --- recupero dell'utenza che necessita di sblocco --- */
				User user = (User) this.userManager.getUser(this.username);

				if (user != null) {
					user.setDisabled(false);
					user.setLastAccess(new Date());
					user.setLastPasswordChange(new Date());
					this.userManager.updateUser(user);
					
					removeFromSession = true;
					
					// si definisce un esito da inviare alla pagina
					String msgInfo = this.getText("Info.sbloccaAccount.change",
							new String[] { this.getUsername() });
					this.addActionMessage(msgInfo);
				} else {
//					// se l'utente non esiste allora si genera un errore
//					evento.setLevel(Event.Level.ERROR);
//					evento.setDetailMessage(this
//							.getTextFromDefaultLocale("Errors.sbloccaAccount.notFound"));
					this.addActionError(this.getText("Errors.sbloccaAccount.notFound"));
					target = INPUT;
				}				
			}			
		} catch (ApsSystemException e1) {
//			evento.setLevel(Level.ERROR);
//			evento.setError(e1);
			this.addActionError(this.getText("Errors.sbloccaAccount.notFound"));
			target = INPUT;
//		} finally {
//			eventManager.insertEvent(evento);
		}
		
		if(removeFromSession) {
			this.getRequest().getSession().removeAttribute(SystemConstants.SESSION_SBLOCCO_UTENZA_AUTONOMO);			
		}

		return target;
	}

}
