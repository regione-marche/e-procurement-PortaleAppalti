package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.regsoggsso;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

/**
 * Interfaccia base per il servizio di gestione della registrazione
 * di un soggetto autenticato tramite sistema SSO
 * 
 * @version 1.0
 * @author Eleonora.Favaro
 */

public interface IRegistrazioneSoggettoSSOManager {
	boolean esisteSoggettoSSO(String username);
	EsitoOutType insertSoggettoSSO(String username, String email, String nome, String cognome);
	EsitoOutType updateSoggettoSSO(String username);
}
