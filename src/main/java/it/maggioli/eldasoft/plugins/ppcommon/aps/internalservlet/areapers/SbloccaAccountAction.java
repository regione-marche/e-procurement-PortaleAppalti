package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.Date;

/**
 * Azione che gestisce il ripristino alle condizioni di normalit&agrave; di un
 * utente che risulta bloccato causa utenza e password scadute, per cui &egrave;
 * attivo.
 * 
 * @author stefano.sabbadin
 */
public class SbloccaAccountAction extends BaseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7771480382310387102L;

	private IUserManager userManager;
	private IUserRegManager userRegManager;

	/** Login/username utente per il quale procedere con l'assistenza. */
	@Validate(EParamValidation.USERNAME)
	private String username;

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}
	
	public void setUserRegManager(IUserRegManager userRegManager) {
		this.userRegManager = userRegManager;
	}

	public void setUsername(String username) {
		this.username = StringUtils.stripToNull(username);
	}
	
	public String getUsername() {
		return username;
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
	public String openSbloccaAccount() {
		String target = SUCCESS;

		// funzionalita' disponibile solo per l'utente con il ruolo di
		// amministratore
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}

	/**
	 * Metodo che mi permette di riportare alla normalit&agrave; un utente la
	 * cui utenza o la cui password potrebbe essere scaduta. In sostanza
	 * aggiorna le date in modo da renderlo di nuovo operativo.
	 */
	public String sbloccaAccount() {
		String target = SUCCESS;
		
		try {
			if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
				this.addActionError(this.getText("Errors.function.notEnabled"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				/* --- recupero dell'utenza che necessita di sblocco --- */
				User user = (User) userManager.getUser(this.username);
				if (user != null) {
					user.setDisabled(false);
					user.setLastAccess(new Date());
					user.setLastPasswordChange(new Date());
					userManager.updateUser(user);
					
					this.userRegManager.reactivationByUserName(this.username);
					
					// si definisce un esito da inviare alla pagina
					String msgInfo = this.getText("Info.sbloccaAccount.change",
												  new String[] { this.username });
					this.addActionMessage(msgInfo);
				} else {
//					// se l'utente non esiste allora si genera un errore
					this.addActionError(this.getText("Errors.sbloccaAccount.notFound"));
					target = INPUT;
				}
			}
		} catch (ApsSystemException ex) {
			this.addActionError(this.getText("Errors.sbloccaAccount.notFound"));
			target = INPUT;
		}
		
		return target;
	}

}
