package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa;

import java.util.List;

import com.agiletec.aps.system.services.user.UserDetails;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType;

/**
 * Interfaccia base per il servizio di gestione della registrazione
 * dell'impresa.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public interface IRegistrazioneImpresaManager {

	EsitoOutType registerImpresa(String username, String mailRiferimento,
			String codiceFiscale, String partitaIVA, String codiceFiscaleDelegato);
	
	EsitoOutType insertImpresa(String username, String email, String pec,
			String denominazione, String codiceFiscale, String partitaIVA);

	EsitoOutType deleteImpresa(String username);

	EsitoOutType updateImpresa(String username, String email, String pec,
			String denominazione, String codiceFiscale, String partitaIVA);

	EsitoOutType activateImpresa(String username, String denominazione);

	EsitoOutType sendActivationMailImpresa(String username, String email);

	boolean esisteImpresa(String username);

	List<UserDetails>  getImpreseAssociate(String login);

	RisultatoStringaOutType getUtenteDelegatoImpresa(String username);

	EsitoOutType aggiornaUtenteDelegatoImpresa(String username, String delegato);

}
