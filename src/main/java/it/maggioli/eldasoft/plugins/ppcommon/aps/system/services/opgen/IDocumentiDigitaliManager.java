package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen;

import it.eldasoft.www.WSOperazioniGenerali.FileType;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia per la gestione dell'interfacciamento con il servizio per
 * l'estrazione dei documenti digitali dal backoffice.
 * 
 * @version 1.8.6
 * @author Stefano.Sabbadin
 */
public interface IDocumentiDigitaliManager {

    /**
     * Restituisce un documento di gara a partire dalla sua chiave
     * 
     * @param id
     *            identificativo univoco del documento
     * 
     * @return documento di gara
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */

	public FileType getDocumentoPubblico(String idProgramma, long idDocumento)
			throws ApsException;
			
	public FileType getDocumentoRiservato(String idProgramma, long idDocumento, String username)
			throws ApsException;
    	
	public String getUsernameDocumentoRiservato(String idProgramma, long idDocumento)
			throws ApsException;

}
