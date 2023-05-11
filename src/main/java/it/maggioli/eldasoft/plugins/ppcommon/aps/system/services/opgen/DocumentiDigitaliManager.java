package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.www.WSOperazioniGenerali.DocumentoOutType;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore del download dei documenti digitali dal backoffice.
 * 
 * @version 1.8.6
 * @author Stefano.Sabbadin
 */
public class DocumentiDigitaliManager extends AbstractService implements
		IDocumentiDigitaliManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 95816721344509L;

	/** Riferimento al wrapper per il web service delle operazioni generali. */
	private WSOperazioniGeneraliWrapper wsOpGenerali;

	/**
	 * @param wsOpGenerali
	 *            the wsOpGenerali to set
	 */
	public void setWsOpGenerali(WSOperazioniGeneraliWrapper proxyWSOPGenerali) {
		this.wsOpGenerali = proxyWSOPGenerali;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public FileType getDocumentoPubblico(String idProgramma, long idDocumento)
			throws ApsException {
		FileType dettaglio = null;
		DocumentoOutType retWS = null;
		try {
			idProgramma = (StringUtils.isEmpty(idProgramma)  
					   ? CommonSystemConstants.ID_APPLICATIVO_GARE 
					   : idProgramma);

			retWS = wsOpGenerali.getProxyWSOPGenerali().getDocumento(
					idProgramma, idDocumento, null);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getFile();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante il download del documento digitale: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante il download del documento digitale",
					t);
		}

		return dettaglio;
	}
	
	
	public FileType getDocumentoRiservato(String idProgramma, long idDocumento, String username)
			throws ApsException {
		FileType dettaglio = null;
		DocumentoOutType retWS = null;
		try {
			idProgramma = (StringUtils.isEmpty(idProgramma)  
					   ? CommonSystemConstants.ID_APPLICATIVO_GARE 
					   : idProgramma);

			retWS = wsOpGenerali.getProxyWSOPGenerali().getDocumento(
					idProgramma, idDocumento, username);
			if (retWS.getErrore() == null)
				dettaglio = retWS.getFile();
			else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante il download del documento digitale: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante il download del documento digitale",
					t);
		}

		return dettaglio;
	}
	
	
	public String getUsernameDocumentoRiservato(String idProgramma, long idDocumento)
		throws ApsException {
		String dettaglio = null;
		String retWS = null;
		try {
			idProgramma = (StringUtils.isEmpty(idProgramma)  
						   ? CommonSystemConstants.ID_APPLICATIVO_GARE 
						   : idProgramma);
			
			retWS = wsOpGenerali.getProxyWSOPGenerali().getUsernameDocumentoRiservato(
					idProgramma, idDocumento);
			if (retWS != null) {
				dettaglio = retWS;
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante il download riservato anonimo del documento digitale",
					t);
		}

		return dettaglio;
	}

}
