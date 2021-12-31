package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.dsappalti;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

/**
 * Interfaccia per la gestione dell'upload dei dataset appalti per la legge
 * 190/2012.
 * 
 * @author Stefano.Sabbadin
 */
public interface IDatasetAppaltiManager {
	
	EsitoOutType uploadDatasetAppalti(int anno, String codFiscaleStazAppaltante, byte[] datasetCompresso);

}
