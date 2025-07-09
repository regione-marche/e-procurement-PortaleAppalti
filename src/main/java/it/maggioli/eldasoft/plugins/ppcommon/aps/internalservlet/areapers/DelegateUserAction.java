package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.DelegateUser;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.Date;
import java.util.List;

/**
 * Azione che gestisce il DELEGATEUSER associato ad un'impresa
 * 
 * @author 
 */
public class DelegateUserAction extends BaseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1955366184501233452L;
	
	private static final int OP_INSERT = 1;
	private static final int OP_MODIFY = 2;
	private static final int OP_REMOVE = 3;
	
	
	private IUserManager userManager;
	private IEventManager eventManager;

	@Validate(EParamValidation.DIGIT)
	private String tipoOperazione;
	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String olddelegateuser;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String newdelegateuser;
	private List<DelegateUser> impresaDelegates; 
		
	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getTipoOperazione() {
		return tipoOperazione;
	}

	public void setTipoOperazione(String tipoOperazione) {
		this.tipoOperazione = tipoOperazione;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = StringUtils.stripToNull(username);
	}

	public String getOlddelegateuser() {
		return olddelegateuser;
	}

	public void setOlddelegateuser(String olddelegateuser) {
		this.olddelegateuser = StringUtils.stripToNull(olddelegateuser.toUpperCase());
	}

	public String getNewdelegateuser() {
		return newdelegateuser;
	}

	public void setNewdelegateuser(String newdelegateuser) {
		this.newdelegateuser = StringUtils.stripToNull(newdelegateuser.toUpperCase());
	}

	public List<DelegateUser> getImpresaDelegates() {
		return impresaDelegates;
	}
	
	/**
	 * validate 
	 */
	@Override
	public void validate() {
		// funzionalità accessibile sono da amministratore!!!
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			redirectToHome();
			return;
		} 
		
		super.validate();
	}

	/**
	 * Predispone l'apertura della pagina previo controllo autorizzazione (si
	 * deve essere un utente amministratore).
	 * 
	 * @return target struts di esito dell'operazione
	 */
	@SkipValidation
	public String openDelegateUser() {
		String target = SUCCESS;

		// funzionalita' disponibile solo per l'utente con il ruolo di amministratore
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 *  Inserimento di un nuovo delgateuser
	 */
	public String newDelegateUser() {
		String target = SUCCESS;
		try {
			User user = (User) this.userManager.getUser(this.username);
			if(user != null && user.getDelegateUser() != null) {
				this.addActionError(this.getText("Errors.delegateUser.alreadyExists"));
				target = INPUT;
			} else {
				target = updateDelegateUser(OP_INSERT);
			}
		} catch (ApsSystemException e) {
			this.addActionError(this.getText("Errors.user.notFound", new String[] { this.username }));
			target = INPUT;
		}		
		return target;
	}
	
	/**
	 * Modifica di un delegateuser esistente
	 */
	public String editDelegateUser() {
		String target = SUCCESS;
		try {
			User user = (User) userManager.getUser(this.username);
			if(user != null && 
			   StringUtils.isNotEmpty(this.olddelegateuser) && StringUtils.isNotEmpty(this.newdelegateuser))
			{
				if(user.getDelegateUser() == null) {
					this.addActionError(this.getText("Errors.delegateUser.notSet"));
					target = INPUT;
				} else if( !user.getDelegateUser().equalsIgnoreCase(this.olddelegateuser) ) {
					this.addActionError(this.getText("Errors.delegateUser.notEqual"));
					target = INPUT;
				} else {
					target = updateDelegateUser(OP_MODIFY);
				}
			}
		} catch (ApsSystemException e) {
			this.addActionError(this.getText("Errors.user.notFound", new String[] { this.username }));
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * Rimuovi un delegateuser esistente (vedi diritto all'oblio)
	 */ 
	public String removeDelegateUser() {
		String target = SUCCESS;
		try {
			User user = (User) this.userManager.getUser(this.username);
			if(user != null && 
			   StringUtils.isNotEmpty(this.olddelegateuser))
			{
				if( !user.getDelegateUser().equalsIgnoreCase(this.olddelegateuser) ) {
					this.addActionError(this.getText("Errors.delegateUser.notEqual"));
					target = INPUT;
				} else {
					target = updateDelegateUser(OP_REMOVE);
				}
			}
		} catch (ApsSystemException e) {
			this.addActionError(this.getText("Errors.user.notFound", new String[] { this.username }));
			target = INPUT;
		}		
		return target;
	}
	
	/**
	 * ...
	 */
	private String updateDelegateUser(int tipoOper) {
		String target = SUCCESS;

		Event evento = null;
		try {
			evento = new Event();
			evento.setLevel(Event.Level.INFO); 
			evento.setEventType(PortGareEventsConstants.DELEGATEUSEER_CHANGE);
			if(tipoOper == OP_INSERT) {
				evento.setMessage("Inserimento soggetto con delega " + this.newdelegateuser + 
								  " per l'utente " + this.username);
			} else if(tipoOper == OP_MODIFY) {
				evento.setMessage("Modifica soggetto con delega " + this.olddelegateuser +
								  " in " + this.newdelegateuser + 
								  " per l'utente " + this.username);
			} else if(tipoOper == OP_REMOVE) {
				evento.setMessage("Rimozione soggetto con delega " +  this.olddelegateuser + 
								  " per l'utente " + this.username);
				this.newdelegateuser = null;
			}
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			
			if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
				this.addActionError(this.getText("Errors.function.notEnabled"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// recupero dell'utenza corrente
				User user = (User) this.userManager.getUser(this.username);
				if (user != null) {
					user.setLastAccess(new Date());
					user.setDelegateUser(this.newdelegateuser);
					this.userManager.setDelegateUser(user.getUsername(), this.newdelegateuser);
					
					// si definisce un esito da inviare alla pagina
					String msgInfo = this.getText("Info.delegateUser.change",
												  new String[] { user.getUsername(), this.newdelegateuser });
					this.addActionMessage(msgInfo);
				} else {
					// se l'utente non esiste allora si genera un errore
					evento.setLevel(Level.ERROR);
					evento.setDetailMessage("Utente " + this.username + " non trovato");
					this.addActionError(this.getText("Errors.user.notFound", new String[] { this.username }));
					target = INPUT;
				}
			}
		} catch (Throwable e) {
			evento.setLevel(Level.ERROR);
			evento.setError(e);
			this.addActionError(this.getText("Errors.user.notFound", new String[] { this.username }));
			target = INPUT;
		} finally {
			eventManager.insertEvent(evento);
		}

		return target;
	}

}
