package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model.ITempRegImpresaProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.SendFailedException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserDAO;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegDAO;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.UserRegManager;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.UserRegBean;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.apache.commons.lang.StringUtils;

/**
 * Servizio gestore della registrazione dell'impresa.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class RegistrazioneImpresaManager extends UserRegManager implements
	IRegistrazioneImpresaManager {

    /**
     * UID
     */
    private static final long serialVersionUID = -7549332403939160480L;
    
    private IEventManager eventManager;
    private IAuthenticationProviderManager _authenticationProvider;

    /**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setAuthenticationProvider(IAuthenticationProviderManager _authenticationProvider) {
		this._authenticationProvider = _authenticationProvider;
	}
		
    @Override
    public void init() throws Exception {
    	super.init();
    	ApsSystemUtils.getLogger().debug(
    			this.getClass().getName() + ": inizializzato ");
    }

    public EsitoOutType registerImpresa(String username, String mailRiferimento,
			String codiceFiscale, String partitaIVA, String codiceFiscaleDelegato) {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);

		// si utilizza il profilo impresa (PTRI) definito custom per i dati
		// temporanei
		ITempRegImpresaProfile regImpresaProfile = this
				._regImpresaProfileManager.getProfileType("PTRI");
		this.updateEntityAttributes(regImpresaProfile, codiceFiscale,
				partitaIVA, mailRiferimento, codiceFiscaleDelegato);

		try {
			if (this.existsUser(username)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("DUPL-USER");
			}
			if (risultato.isEsitoOk() && this._datiImpresaChecker.existsEmail(mailRiferimento)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("DUPL-EMAIL");
			}
			if (risultato.isEsitoOk() && this._datiImpresaChecker.existsCodFiscale(codiceFiscale)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("DUPL-CF");
			}
			if (risultato.isEsitoOk() && partitaIVA != null && this._datiImpresaChecker.existsPartitaIVA(partitaIVA)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("DUPL-PI");
			}

			if (risultato.isEsitoOk()) {
				User user = new User();
				user.setDisabled(true);
				user.setUsername(username);
				user.setProfile(regImpresaProfile);
				user.setPassword("");
				user.setDelegateUser(codiceFiscaleDelegato);
				this._userDao.addUser(user);
				this._regImpresaProfileManager.addProfile(username,
						regImpresaProfile);
			}

		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "registerImpresa");
			risultato.setEsitoOk(false);
			risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		}

		return risultato;
	}

    
	public EsitoOutType insertImpresa(String username, String email, String pec,
			String denominazione, String codiceFiscale, String partitaIVA) {
    	EsitoOutType risultato = new EsitoOutType();
    	risultato.setEsitoOk(true);
    	risultato.setCodiceErrore(null);

		String mailRiferimento = DatiImpresaChecker.getEmailRiferimento(pec, email);
    	
    	// si utilizza il profilo impresa (PFI) definito custom
    	IUserProfile userProfile = this.getUserProfileManager().getProfileType(
    		"PFI");
    	RegistrazioneImpresaManager.updateEntityAttributes(userProfile, denominazione, mailRiferimento);

    	try {
    	    if (this.existsUser(username)) {
    		risultato.setEsitoOk(false);
    		risultato.setCodiceErrore("DUPL-USER");
    	    }
    	    if (risultato.isEsitoOk() && StringUtils.isEmpty(mailRiferimento)) {
        		risultato.setEsitoOk(false);
        		risultato.setCodiceErrore("EMPTY-EMAIL");
        	}
    	    if (risultato.isEsitoOk() && this._datiImpresaChecker.existsEmail(mailRiferimento)) {
    		risultato.setEsitoOk(false);
    		risultato.setCodiceErrore("DUPL-EMAIL");
    	    }
			if (risultato.isEsitoOk() && this._datiImpresaChecker.existsCodFiscale(codiceFiscale)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("DUPL-CF");
			}
			if (risultato.isEsitoOk() && partitaIVA != null && this._datiImpresaChecker.existsPartitaIVA(partitaIVA)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("DUPL-PI");
			}

    	    if (risultato.isEsitoOk()) {
    		UserRegBean regAccountBean = new UserRegBean();
    		regAccountBean.setUsername(username);
    		regAccountBean.setEMail(mailRiferimento);
    		regAccountBean.setLang("it");
    		regAccountBean.setUserProfile(userProfile);
    		this.regAccount(regAccountBean);
    	    }

    	} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "insertImpresa");
			risultato.setEsitoOk(false);
			if (e instanceof ApsSystemException
					&& e.getCause() != null
					&& e.getCause() instanceof ApsSystemException
					&& e.getCause().getCause() != null
					&& e.getCause().getCause() instanceof SendFailedException
					&& ((SendFailedException) e.getCause().getCause())
							.getNextException() instanceof SMTPAddressFailedException)
				risultato.setCodiceErrore("INVALID-EMAIL");
			else
				risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
    	}

    	return risultato;
        }

    @Override
    public EsitoOutType deleteImpresa(String username) {
	EsitoOutType risultato = new EsitoOutType();
	risultato.setEsitoOk(true);
	risultato.setCodiceErrore(null);

	try {
	    if (!this.existsUser(username)) {
		risultato.setEsitoOk(false);
		risultato.setCodiceErrore("UNKNOWN-USER");
	    }

	    if (risultato.isEsitoOk()) {
		this.getUserManager().removeUser(username);
	    }

	} catch (Exception e) {
	    ApsSystemUtils.logThrowable(e, this, "deleteImpresa");
	    risultato.setEsitoOk(false);
	    risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
	}

	return risultato;
    }

    @Override
	public EsitoOutType updateImpresa(String username, String email, String pec,
			String denominazione, String codiceFiscale, String partitaIVA) {
	EsitoOutType risultato = new EsitoOutType();
	risultato.setEsitoOk(true);
	risultato.setCodiceErrore(null);

	String mailRiferimento = DatiImpresaChecker.getEmailRiferimento(pec, email);
	
	try {
	    if (!this.existsUser(username)) {
		risultato.setEsitoOk(false);
		risultato.setCodiceErrore("UNKNOWN-USER");
	    }
	    if (risultato.isEsitoOk() && StringUtils.isEmpty(mailRiferimento)) {
    		risultato.setEsitoOk(false);
    		risultato.setCodiceErrore("EMPTY-EMAIL");
    	}
	    if (risultato.isEsitoOk()) {
		List<String> users = this._datiImpresaChecker.searchUsersFromAttrib(this
    			.getUserRegConfig().getProfileEMailAttr(), mailRiferimento);
		// se l'email in input esiste, deve essere quella dell'utente
		// stesso in quanto non aggiorna l'email, ma non puo' essere di
		// un altro utente
		if (users.size() == 1 && !users.get(0).equals(username)) {
		    risultato.setEsitoOk(false);
		    risultato.setCodiceErrore("DUPL-EMAIL");
		}
	    }
		if (risultato.isEsitoOk() && this._datiImpresaChecker.existsCodFiscale(codiceFiscale)) {
			risultato.setEsitoOk(false);
			risultato.setCodiceErrore("DUPL-CF");
		}
		if (risultato.isEsitoOk() && partitaIVA != null && this._datiImpresaChecker.existsPartitaIVA(partitaIVA)) {
			risultato.setEsitoOk(false);
			risultato.setCodiceErrore("DUPL-PI");
		}

	    if (risultato.isEsitoOk()) {
			IUserProfile userProfile = this.getUserProfileManager().getProfile(username);
			RegistrazioneImpresaManager.updateEntityAttributes(userProfile, denominazione, mailRiferimento);
			this.getUserProfileManager().updateProfile(username, userProfile);
	    }

	} catch (Exception e) {
	    ApsSystemUtils.logThrowable(e, this, "updateImpresa");
	    risultato.setEsitoOk(false);
	    risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
	}

	return risultato;
    }

	@Override
	public EsitoOutType activateImpresa(String username, String denominazione) {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);
		Event evento = new Event();
		evento.setUsername(username);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.GENERAZIONE_TOKEN);
		evento.setMessage("Attivazione impresa"); //Non possiedo ancora il token, imposto un primo messaggio
		try {
		    if (!this.existsUser(username)) {
			risultato.setEsitoOk(false);
			risultato.setCodiceErrore("UNKNOWN-USER");
			evento.setLevel(Event.Level.ERROR);
			evento.setDetailMessage(risultato.getCodiceErrore());
		    }

		    if (risultato.isEsitoOk()) {
		    	ITempRegImpresaProfile tempProfile = this._regImpresaProfileManager.getProfile(username);
				// si utilizza il profilo impresa (PFI) definito custom
				IUserProfile userProfile = this.getUserProfileManager().getProfileType(
					"PFI");
				RegistrazioneImpresaManager.updateEntityAttributes(userProfile, denominazione,
						(String) tempProfile.getValue(tempProfile
								.getMailAttributeName()));
				
				String token = null;
				UserDetails utenza = this._userDao.loadUser(username);
				/* Il token deve essere generato unicamente in caso di registrazione "standard" di un'impresa a portale 
				 * Se la registrazione viene effettuata per conto dell'impresa da un soggetto terzo (soggetto fisico segnalato 
				 * come utente delegato per l'impresa) il token non dovrà essere generato
				 * */
				if(utenza.getDelegateUser() == null){
					token = this.createToken(username);
					evento.setMessage("Attivazione impresa con token " + token );
					this.sendAlertRegProfile(username, (IUserProfile) userProfile, token);
				}else{
					//Attivazione impresa per registrazione da utenza SSO
					evento.setMessage("Attivazione impresa senza token" );
					this._authenticationProvider.sendEmailUserReactivated(username, userProfile, userProfile.getValue(userProfile.getMailAttributeName()).toString());
				}

		    	this._regImpresaProfileManager.deleteProfile(username);
				this.getUserProfileManager().addProfile(username, userProfile);
				if(token != null){
					this.getUserRegDAO().addActivationToken(username, token, new Date(), IUserRegDAO.ACTIVATION_TOKEN_TYPE);
				}else{
					utenza.setDisabled(false);
					this._userDao.updateUser(utenza);
					//Associazione dell'utenza al gruppo gare
					this.loadUserDefaultRoles((User)utenza);
					this.loadUserDefaultGroups((User)utenza);
				}
		    }
		} catch (Exception e) {
			evento.setError(e);
		    ApsSystemUtils.logThrowable(e, this, "activateImpresa");
		    risultato.setEsitoOk(false);
			if (e instanceof ApsSystemException
					&& e.getCause() != null
					&& e.getCause() instanceof SendFailedException
					&& ((SendFailedException) e.getCause()).getNextException() instanceof SMTPAddressFailedException)
				risultato.setCodiceErrore("INVALID-EMAIL");
			else
				risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		} finally {
			eventManager.insertEvent(evento);
		}
		
		return risultato;
	}

	@Override
	public EsitoOutType sendActivationMailImpresa(String username, String email) {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);
		Event evento = new Event();
		evento.setUsername(username);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.GENERAZIONE_TOKEN);
		evento.setMessage("Attivazione impresa"); //Non possiedo ancora il token, imposto un primo messaggio
		try {
			if (!this.existsUser(username)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("UNKNOWN-USER");
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage(risultato.getCodiceErrore());
			}

			if (risultato.isEsitoOk()) {
				UserDetails user = this.getUserManager().getUser(username);
				if (!user.isDisabled()) {
					risultato.setEsitoOk(false);
					risultato.setCodiceErrore("ACTIVE-USER");
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(risultato.getCodiceErrore());
				}
			}

			if (risultato.isEsitoOk()) {
				
				/* Se sono in gestione SSO la mail di attivazione deve essere notificata al soggetto fisico e non all'impresa che viene registrata (NO TOKEN) */
				UserDetails utenza = this._userDao.loadUser(username);
	
				IUserProfile userProfile = null;
				String token = null;
				
				/* Si differenziano le mail da inviare a seconda che 
				 * 	- un'impresa si registri autonomamente al portale (non avrà un delegateUser associato) => verrà generato il token di attivazione da inserire all'interno della mail di registrazione
				 * 	- un soggetto fisico operi per conto di un operatore economico => non verrà generato alcuno token e si andrà a sfruttare l'operazione di riattivazione
				 * */
				
				userProfile = this.getUserProfileManager().getProfile(username);
				((MonoTextAttribute)userProfile.getAttributeMap().get(userProfile.getMailAttributeName())).setText(email);	
				if(utenza.getDelegateUser() == null){
					// si genera il nuovo token
					token = this.createToken(username);
					evento.setMessage("Attivazione impresa con token " + token);
					// lo si invia all'utente di compentenza
					this.sendAlertRegProfile(username, (IUserProfile) userProfile,
							token);
					// si aggiorna la tabella dei token sostituendo il vecchio con
					// quello appena aggiornato
					this.getUserRegDAO().clearTokenByUsername(username);
					this.getUserRegDAO().addActivationToken(username, token,
						new Date(), IUserRegDAO.ACTIVATION_TOKEN_TYPE);
				}else{
					// lo si invia all'utente di competenza
					this._authenticationProvider.sendEmailUserReactivated(username, userProfile, userProfile.getValue(userProfile.getMailAttributeName()).toString());
				}
			}

		} catch (Exception e) {
			evento.setError(e);
			ApsSystemUtils.logThrowable(e, this, "sendActivationMailImpresa");
			risultato.setEsitoOk(false);
			if (e instanceof ApsSystemException
					&& e.getCause() != null
					&& e.getCause() instanceof SendFailedException
					&& ((SendFailedException) e.getCause()).getNextException() instanceof SMTPAddressFailedException)
				risultato.setCodiceErrore("INVALID-EMAIL");
			else
				risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		} finally {
			eventManager.insertEvent(evento);
		}

		return risultato;
	}

	@Override
	public boolean esisteImpresa(String username) {
		try {
			return this.existsUser(username);
		} catch (ApsSystemException e) {
			throw new RuntimeException("UNEXP-ERR " + e.getMessage(), e);
		}
	}

    /**
     * check if user exist
     * 
     * @param username
     * @return true if exist a user with this username, false if user not exist.
     * @throws Throwable
     *             In error case.
     */
    protected boolean existsUser(String username) throws ApsSystemException {
	boolean exists = (username != null && username.trim().length() >= 0 && this
		.getUserManager().getUser(username) != null);
	return exists;
    }

	private void updateEntityAttributes(ITempRegImpresaProfile currentEntity,
			String codiceFiscale, String partitaIVA, String email, String codiceFiscaleDelegato) {
		List<AttributeInterface> attributes = currentEntity.getAttributeList();
		for (int i = 0; i < attributes.size(); i++) {
			MonoTextAttribute attribute = (MonoTextAttribute) attributes.get(i);
			if (attribute.isActive()) {
				if ("codFiscale".equals(attribute.getName()))
					attribute.setText(codiceFiscale);
				if ("partitaIVA".equals(attribute.getName()))
					attribute.setText(partitaIVA);
				if ("email".equals(attribute.getName()))
					attribute.setText(email);
				if("codiceFiscaleDelegato".equals(attribute.getName()))
					attribute.setText(codiceFiscaleDelegato);
			}
		}
	}

    public static void updateEntityAttributes(IApsEntity currentEntity,
	    String denominazione, String email) {
	List<AttributeInterface> attributes = currentEntity.getAttributeList();
	for (int i = 0; i < attributes.size(); i++) {
	    MonoTextAttribute attribute = (MonoTextAttribute) attributes.get(i);
	    if (attribute.isActive()) {
		if ("Nome".equals(attribute.getName()))
		    attribute.setText(denominazione);
		if ("email".equals(attribute.getName()))
		    attribute.setText(email);
	    }
	}
    }

	public void setDatiImpresaChecker(DatiImpresaChecker datiImpresaChecker) {
		this._datiImpresaChecker = datiImpresaChecker;
	}

    public void setUserDAO(IUserDAO userDao) {
		this._userDao = userDao;
	}

	public void setRegImpresaProfileManager(
			IRegImpresaProfileManager regImpresaProfileManager) {
		this._regImpresaProfileManager = regImpresaProfileManager;
	}

	private DatiImpresaChecker _datiImpresaChecker;
	private IUserDAO _userDao;
	private IRegImpresaProfileManager _regImpresaProfileManager;

	@Override
	public List<UserDetails> getImpreseAssociate(String codiceFiscaleDelegato) {

		List<UserDetails> listaImpreseCollegate = new ArrayList<UserDetails>();
		listaImpreseCollegate.addAll(this._userDao.searchUsersFromDelegateUser(codiceFiscaleDelegato));

		return listaImpreseCollegate;
	}

	@Override
	public RisultatoStringaOutType getUtenteDelegatoImpresa(String username) {
		RisultatoStringaOutType risultato = new RisultatoStringaOutType();
		risultato.setRisultato(""); //settato a stringa vuota in quanto non accetta il valore null
		risultato.setCodiceErrore(null);
		try {
		    if (!this.existsUser(username)) {
		    	risultato.setCodiceErrore("UNKNOWN-USER");
		    }
		    
		    if(StringUtils.isEmpty(risultato.getCodiceErrore())){
		    	UserDetails utente = (UserDetails)this._userDao.loadUser(username);
		    	if(utente.getDelegateUser()!=null){
		    		risultato.setRisultato(utente.getDelegateUser());
				}
		    }
		    
		} catch (Exception e) {
		    ApsSystemUtils.logThrowable(e, this, "getUtenteDelegatoImpresa");
		    risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		}
		return risultato;
	}

	@Override
	public EsitoOutType aggiornaUtenteDelegatoImpresa(String username, String delegato) {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);
		
		try {
			
		    if (!this.existsUser(username)) {
				risultato.setEsitoOk(false);
				risultato.setCodiceErrore("UNKNOWN-USER");
		    }
		    
		    if(risultato.isEsitoOk()){
		    	if(StringUtils.isEmpty(delegato)){
		    		risultato.setEsitoOk(false);
		    		risultato.setCodiceErrore("EMPTY-DELEGATE");
		    	}
		    }
		    
		    if(risultato.isEsitoOk()){
		    	this._userDao.updateDelegateUserImpresa(username,delegato.toUpperCase());
		    }

		} catch (Exception e) {
		    ApsSystemUtils.logThrowable(e, this, "aggiornaUtenteDelegatoImpresa");
		    risultato.setEsitoOk(false);
		    risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		}
		return risultato;
	}
}