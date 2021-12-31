package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;

/**
 * Classe di utilit&agrave; nonch&egrave; di controlli attivabili sui dati
 * dell'impresa, condivisi in pi&ugrave; punti della logica del plugin di gare.
 * 
 * @author Stefano.Sabbadin
 */
public class DatiImpresaChecker {

	private IUserRegManager _userRegManager;
	private IUserProfileManager _userProfileManager;

	public void setUserRegManager(IUserRegManager _userRegManager) {
		this._userRegManager = _userRegManager;
	}

	public void setUserProfileManager(IUserProfileManager _userProfileManager) {
		this._userProfileManager = _userProfileManager;
	}

	public IUserRegConfig getUserRegConfig() {
		return this._userRegManager.getUserRegConfig();
	}

	/**
	 * Ritorna l'email di riferimento, dando priorit&agrave; alla pec se
	 * esistente, altrimenti utilizza l'indirizzo email tradizionale. Se viene
	 * configurato l'utilizzo della sola mail tradizionale, allora ritorna tale
	 * informazione in modo incondizionato.
	 * 
	 * @param pec
	 *            mail pec
	 * @param mail
	 *            mail tradizionale
	 * @return pec se valorizzata altrimenti mail tradizionale, oppure mail
	 *         tradizionale se configurata come mail da utilizzare
	 */
	public static String getEmailRiferimento(String pec, String mail) {
		String mailRiferimento = null;
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		ICustomConfigManager manager = (ICustomConfigManager) ctx
				.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
		try {
			if (manager.isActiveFunction("IMPRESA-DATIPRINC-RECAPITI", "MAILRIF")) {
				mailRiferimento = mail;
			} else {
				mailRiferimento = StringUtils.defaultIfEmpty(pec, mail); 
			}
		} catch (Exception e) {
			// non accadra' mai in quanto sto referenziando una customizzazione esistente
		}
		
		return mailRiferimento;
	}

	/**
	 * Verifica se l'email esiste in un qualche profilo.
	 * 
	 * @param mail
	 *            mail da ricercare
	 * @return true se esiste, false altrimenti
	 * @throws ApsSystemException
	 */
	public boolean existsEmail(String mail) throws ApsSystemException {
		List<String> usernames = searchUsersFromAttrib(this.getUserRegConfig()
				.getProfileEMailAttr(), mail);
		if (usernames.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se l'email esiste in un profilo diverso da quello dell'eventuale
	 * utente.
	 * 
	 * @param username
	 *            username correlato all'utente
	 * @param mail
	 *            mail da ricercare
	 * @return true se esiste, false altrimenti
	 * @throws ApsSystemException
	 */
	public boolean existsEmail(String username, String mail)
			throws ApsSystemException {
		List<String> usernames = searchUsersFromAttrib(this.getUserRegConfig()
				.getProfileEMailAttr(), mail);
		if (usernames.size() > 0) {
			// si controlla se nell'elenco esiste solo l'utente stesso
			// oppure altri: in quest'ultimo caso allora vuol dire che per
			// l'utente e' stato specificato un indirizzo email usato da
			// altri, pertanto risulta gia' presente l'indirizzo email
			for (Iterator<String> iterator = usernames.iterator(); iterator
					.hasNext();) {
				String user = (String) iterator.next();
				if (!username.equals(user))
					return true;
			}
		}
		return false;
	}

	/**
	 * Verifica se il codice fiscale esiste in un qualche profilo.
	 * 
	 * @param codiceFiscale
	 *            codice fiscale da ricercare
	 * @return true se esiste, false altrimenti
	 * @throws ApsSystemException
	 */
	public boolean existsCodFiscale(String codiceFiscale)
			throws ApsSystemException {
		List<String> usernames = searchUsersFromAttrib("codFiscale",
				codiceFiscale);
		if (usernames.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se la partita iva esiste in un qualche profilo.
	 * 
	 * @param partitaIVA
	 *            partita iva da ricercare
	 * @return true se esiste, false altrimenti
	 * @throws ApsSystemException
	 */
	public boolean existsPartitaIVA(String partitaIVA)
			throws ApsSystemException {
		List<String> usernames = searchUsersFromAttrib("partitaIVA", partitaIVA);
		if (usernames.size() > 0) {
			return true;
		}
		return false;
	}

	public List<String> searchUsersFromAttrib(String key, String value)
			throws ApsSystemException {
		EntitySearchFilter[] filters = { new EntitySearchFilter(key, true,
				value, false) };
		List<String> usernames = ((ApsEntityManager) this._userProfileManager)
				.searchId(filters);
		return usernames;
	}

}
