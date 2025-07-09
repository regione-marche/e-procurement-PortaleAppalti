package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.sil.WSStipule.*;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;

import java.rmi.RemoteException;
import java.util.Calendar;


public class StipuleManager extends AbstractService implements IStipuleManager {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7039912668423322712L;

	/** Riferimento al wrapper per il web service Gare Appalto */
	private WSStipuleWrapper wsStipule;

	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	public void setWsStipule(WSStipuleWrapper wsStipule) {
		this.wsStipule = wsStipule;
	}

	@Override
	public ElencoStipuleContrattiOutType searchStipuleContratti(
			String codiceStipula,
			String oggetto, 
			Integer stato,
			String stazioneAppaltante,
			String usernome,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException  
	{
		try {
			ElencoStipuleContrattiOutType retWS = wsStipule.getProxyWSstipule()
					.searchStipuleContratti(codiceStipula, oggetto, stato, stazioneAppaltante, usernome, indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				return retWS;
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura delle stipule contratto: " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura delle stipule contratto", t);
		}
	}
	
	@Override
	public StipulaContrattoType getDettaglioStipulaContratto(
			String id, String username, boolean pubblicata) throws ApsException  
	{
		try {
			ApsSystemUtils.getLogger().debug("Request stipula with id: {}, username: {}, pubblicata: {}",id,username,pubblicata);
			GetDettaglioStipulaContrattoOutType retWS = wsStipule.getProxyWSstipule()
					.getDettaglioStipulaContratto(id, username, pubblicata);
			if (retWS.getErrore() == null) {
				return retWS.getStipulaContratto();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura della stipula contratto: " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura della stipula contratto", t);
		}
	}
	
	
	@Override
	public DocumentazioneStipulaContrattiType[] getDocumentiRichiestiStipulaContratto(
			String id) throws ApsException  
	{
		try {
			GetDocumentiRichiestiStipulaContrattoOutType  retWS = wsStipule.getProxyWSstipule()
					.getDocumentiRichiestiStipulaContratto(id);
			if (retWS.getErrore() == null) {
				return retWS.getDocumento();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti della stipula contratto: " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura dei documenti della stipula contratto", t);
		}
	}

	@Override
	public AllegatoComunicazioneType getDocumentoStipula(String docId) throws ApsException {
		try {
			GetAllegatoStipulaContrattoOutType 	retWS = wsStipule.getProxyWSstipule().getDocumentoStipula(docId);
			if (retWS.getErrore() == null) {
				return retWS.getDocumento();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del documento della stipula contratto: " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura del documento della stipula contratto", t);
		}
	}

	@Override
	public void deleteDocumentoStipula(String docId) throws ApsException {
		try {
			String error = wsStipule.getProxyWSstipule().deleteDocumentoStipula(docId);
			if (error != null){
				throw new ApsException(
						"Errore durante la cancellazione del documento della stipula contratto: " + error);
			}
			
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura del documento della stipula contratto", t);
		}
	}
	
	@Override
	public void insertAllegato(
			String nomeFile
			, Long idDocStipula
			, byte[] allegato
			, String note
			, DocumentiAllegatiFirmaBean checkFirma
			, String contentType
	) throws ApsException {
		try {
			String error = null;
			ApsSystemUtils.getLogger().debug("checkFirma is null? {}",(checkFirma==null));
			AllegatoComunicazioneType attachment = new AllegatoComunicazioneType();
			attachment.setNomeFile(nomeFile);
			attachment.setTipo(contentType);
			attachment.setFile(allegato);
			if (checkFirma != null) {
				Calendar firmacheckts = Calendar.getInstance();
				firmacheckts.setTimeInMillis(checkFirma.getFirmacheckts().getTime());

				attachment.setFirmacheck(checkFirma.getFirmacheck() ? "1" : "2");
				attachment.setFirmacheckts(firmacheckts);
			}

			wsStipule.getProxyWSstipule().insertAllegato(idDocStipula, attachment, note);

			if (error != null)
				throw new ApsException(
						"Errore durante la lettura del documento della stipula contratto: " + error);
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la lettura del documento della stipula contratto", t);
		}
	}

	@Override
	public void updateStipula(String codiceStipula, String username) throws ApsException {
		try {
			String error = wsStipule.getProxyWSstipule().updateStipula(codiceStipula, username);
			if (error != null){
				throw new ApsException(
						"Errore durante la conferma della stipula contratto: " + error);
			}
		} catch (RemoteException t) {
			throw new ApsException(
					"Errore durante la conferma della stipula contratto", t);
		}
	}


}