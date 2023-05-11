package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model;

import com.agiletec.aps.system.common.entity.model.IApsEntity;

/**
 * Interfaccia per il profilo temporaneo di registrazione impresa.
 * 
 * @author Stefano.Sabbadin
 */
public interface ITempRegImpresaProfile extends IApsEntity {

	/**
	 * Return of the username that is associated with the profile.
	 * 
	 * @return The username.
	 */
	public String getUsername();

	/**
	 * Returns the value of an attribute identified by his key. The value can be
	 * of any type.
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @return The value of the attribute.
	 */
	public Object getValue(String key);

	public String getCodiceFiscaleAttributeName();

	public String getPartitaIVAAttributeName();

	public String getMailAttributeName();

	public boolean isPublicProfile();

	public void setPublicProfile(boolean isPublic);

}