package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.rssbandi;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

/**
 * Interfaccia base per il servizio di gestione dell'upload dei file xml per i
 * feed rss di bandi e bandi scaduti.
 * 
 * @version 1.11.4
 * @author Eleonora.Favaro
 */
public interface IRSSBandiManager {

	EsitoOutType uploadRssBandi(byte[] datasetCompresso);

}
