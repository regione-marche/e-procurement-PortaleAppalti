package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate ;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Azione che gestisce la ricerca degli operatori economici
 * 
 * @author 
 */
public class SearchOEAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8666154787112920638L;
	
	private IUserProfileManager userProfileManager;	
	private IUserManager userManager;
	private IEventManager eventManager;
	
	private Map<String, Object> session;

	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.EMAIL)
	private String email;
	@Validate(EParamValidation.SI_NO)
	private String attivo;

	@Validate
	private List<OperatoriEconomiciSearchBean> listaOperatori;	
		
	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}

	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this.userProfileManager = userProfileManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAttivo() {
		return attivo;
	}

	public void setAttivo(String attivo) {
		this.attivo = attivo;
	}

	public List<OperatoriEconomiciSearchBean> getListaOperatori() {
		return listaOperatori;
	}

	/**
	 * Predispone l'apertura della pagina previo controllo autorizzazione 
	 * (si deve essere un utente amministratore).
	 * 
	 * @return target struts di esito dell'operazione
	 */
	@SkipValidation
	public String openSearch() {
		String target = SUCCESS;

		// funzionalita' disponibile solo per l'utente con il ruolo di
		// amministratore
		if (this.getCurrentUser() == null
				|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// alla prima apertura della pagina in automatico si scatena una ricerca senza filtri
			target = this.search();
		}
		return target;
	}

	/**
	 * Estrae la lista degli utenti non attivati sulla base dei criteri di
	 * filtro impostati nella form.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	public String search() {
		String target = SUCCESS;
		
		this.listaOperatori = new ArrayList<OperatoriEconomiciSearchBean>();

		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null || !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				List<UserDetails> users = this.userManager.searchUsers(
						this.username, 
						this.ragioneSociale, 
						this.email,
						this.attivo);
				 
				for (UserDetails item : users) {
					// recupera il profilo dell'utente...
					IUserProfile p = this.userProfileManager.getProfile(item.getUsername());
					String denominazione = null;
					String email = null;
					if(p != null) {
						denominazione = (String)p.getValue("Nome");
						email = (String)p.getValue("email");
					}
					
					// aggiungi l'operatore economico alla lista dei risultati...
					OperatoriEconomiciSearchBean oe = new OperatoriEconomiciSearchBean();
					oe.setUsername(item.getUsername());
					oe.setRagioneSociale(denominazione);
					oe.setEmail(email);
					oe.setAttivo((item.isDisabled() ? "0" : "1"));
					this.listaOperatori.add(oe);
				}
			} catch (ApsSystemException e) {
				this.addActionError(this.getText("Errors.user.notFound", new String[] { this.username }));
				target = INPUT;
			}
		}

		return target;
	}
	
}
