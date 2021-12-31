package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.sysparams;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

/**
 * Interfaccia per la configurazione di parametri di sistema imposti dai plugin fruiti.
 *  
 * @author Stefano.Sabbadin
 */
public interface IConfigParamSistemaManager {

	public EsitoOutType syncConfigurazioneMail(String host, Integer porta,
			String protocollo, Integer timeout, boolean debug, String username, String password, String mail);

	public EsitoOutType syncUnitaMisura();

}
