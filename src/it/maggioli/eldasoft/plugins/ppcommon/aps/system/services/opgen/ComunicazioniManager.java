package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen;

import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ErroreOutType;
import it.eldasoft.www.WSOperazioniGenerali.GetComunicazioneOutType;
import it.eldasoft.www.WSOperazioniGenerali.GetElencoComunicazioniOutType;
import it.eldasoft.www.WSOperazioniGenerali.IsComunicazioneProcessataOutType;
import it.eldasoft.www.WSOperazioniGenerali.ProtocollaComunicazioneOutType;
import it.eldasoft.www.WSOperazioniGenerali.SendComunicazioneOutType;
import it.eldasoft.www.WSOperazioniGenerali.WSAllegatoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore delle comunicazioni con il backoffice
 * 
 * @author Stefano.Sabbadin
 */
public class ComunicazioniManager extends AbstractService implements
	IComunicazioniManager {

    /**
     * UID
     */
    private static final long serialVersionUID = -7494511498344917005L;
    
	/** Riferimento al wrapper per il web service delle operazioni generali. */
	private WSOperazioniGeneraliWrapper wsOpGenerali;
    
    /**
	 * @param wsOpGenerali the wsOpGenerali to set
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
    public List<DettaglioComunicazioneType> getElencoComunicazioni(
	    DettaglioComunicazioneType criteriRicerca) throws ApsException {
	List<DettaglioComunicazioneType> risultato = null;
	try {
	    GetElencoComunicazioniOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
		    .getElencoComunicazioni(criteriRicerca);
	    if (retWS.getErrore() == null) {
		if (retWS.getComunicazione() != null)
		    risultato = Arrays.asList(retWS.getComunicazione());
		else
		    risultato = new ArrayList<DettaglioComunicazioneType>();
	    } else {
		// se si verifica un errore durante l'estrazione dei dati con il
		// servizio, allora si ritorna un'eccezione che contiene il
		// messaggio di errore
		throw new ApsException(
			"Errore durante la ricerca di comunicazioni: "
				+ retWS.getErrore());
	    }
	} catch (RemoteException e) {
	    throw new ApsException(
		    "Errore inaspettato durante la ricerca di comunicazioni", e);
	}
	return risultato;
    }

    @Override
	public ComunicazioneType getComunicazione(String applicativo, long id, String idDocumento)
		throws ApsException 
	{
		ComunicazioneType risultato = null;
		try {
		    GetComunicazioneOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
					.getComunicazione(applicativo, id, idDocumento);
		    if (retWS.getErrore() == null) {
		    	risultato = retWS.getComunicazione();
		    } else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
					"Errore durante la lettura di una comunicazione: "
						+ retWS.getErrore());
		    }
								
			//////////////////////////////////////////////
			// Workaraound per il rilascio della memoria  
			retWS = null;
	
		} catch (RemoteException e) {
		    throw new ApsException(
			    "Errore inaspettato durante la lettura di una comunicazione",
			    e);
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError(
					"Memoria temporaneanete esaurita durante la lettura di una comunicazione");
		}
		return risultato;
    }

    @Override
	public ComunicazioneType getComunicazione(String applicativo, long id)
		throws ApsException 
	{
		return getComunicazione(applicativo, id, null);
	}

	@Override
	public Long sendComunicazione(ComunicazioneType comunicazione)
			throws ApsException {
		Long risultato = null;
		try {
			// Verifica se ci sono piu' allegati e quali di questi vanno 
			// inviati. 
            // Gli allegati con "uuid" valorizzato rappresentano file da 
			// gestire in modalità 2.0.0; generalmente questo tipo di 
			// allegati rappresenta documenti allegati di un documento (busta
			// amministrativa, economica, tecnica, etc) allegato alla 
			// comunicazione.			
			// Crea una copia della comunicazione con solo gli allegati 
			// modificati/eliminati da inviare al servizio...
			ComunicazioneType comunicazioneDaInviare = new ComunicazioneType(
					comunicazione.getDettaglioComunicazione(), null);
			
			List<AllegatoComunicazioneType> allegatiDaInviare = 
				new ArrayList<AllegatoComunicazioneType>();
			
			if(comunicazione.getAllegato() != null) {
				for(int i = 0; i < comunicazione.getAllegato().length; i++) {
					if( !StringUtils.isEmpty(comunicazione.getAllegato(i).getUuid()) ) {
						// gestione degli allegati all'esterno della busta xml (dalla 2.0.0)
						
						// per sicurezza, evita di spedire un allegato "modificato" 
						// senza contenuto !!!
						if(comunicazione.getAllegato(i).getModificato() == PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO &&
						   (comunicazione.getAllegato(i).getFile() == null ||
						    (comunicazione.getAllegato(i).getFile() != null && comunicazione.getAllegato(i).getFile().length <= 0)) ) {						
							// NON dovrebbe mai succedere
							comunicazione.getAllegato(i).setModificato( PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO );
							ApsSystemUtils.getLogger().warn(
									"sendComunicazione(): tentativo di invio di un allegato 'modificato' senza contenuto. " +
									"Id=" + comunicazione.getDettaglioComunicazione().getId() + ", " + 
									"IdOperatore=" + comunicazione.getDettaglioComunicazione().getIdOperatore() + ", " +  
									"allegato.Id=" + comunicazione.getAllegato(i).getId() + ", " +
									"allegato.uuid=" + comunicazione.getAllegato(i).getUuid() 
							);
						}
						
						if(comunicazione.getAllegato(i).getModificato() == PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO) {
							// allegato modificato
							allegatiDaInviare.add( comunicazione.getAllegato(i) );
						} else {
							// allegato NON modificato/eliminato
							// e non andrebbe spedito al servizio...
							// si spedisce solo il riferimento del file per mantenere
							// coerente la lista degli allegati, ma non si spedisce 
							// il contenuto :D !!!
							AllegatoComunicazioneType doc = new AllegatoComunicazioneType(
									comunicazione.getAllegato(i).getId(),
									comunicazione.getAllegato(i).getTipo(),
									comunicazione.getAllegato(i).getNomeFile(),
									comunicazione.getAllegato(i).getDescrizione(),
									new byte[0],	// <= il servizio non accetta null !!!
									comunicazione.getAllegato(i).getUuid(),
									comunicazione.getAllegato(i).getModificato(),
									comunicazione.getAllegato(i).getDimensione()); 
							allegatiDaInviare.add(doc);
						}
					} else {
						// gestione degli allegati all'interno della busta xml (fino a 1.14.x)
						allegatiDaInviare.add( comunicazione.getAllegato(i) );
					}
				}
			}
			
			comunicazioneDaInviare.setAllegato( 
					allegatiDaInviare.toArray(
							new AllegatoComunicazioneType[allegatiDaInviare.size()]) );
			
			// invia al servizio la comunicazione...
			//
			SendComunicazioneOutType retWS = this.wsOpGenerali
				.getProxyWSOPGenerali().sendComunicazione(comunicazioneDaInviare);
			if (retWS.getErrore() != null) {
				throw new ApsException(
						"Errore durante l'invio della comunicazione: " + retWS.getErrore());
			} 
			risultato = retWS.getIdComunicazione();
						
			// NB: se tutto e' andato bene marca come gestito lo stato degli allegati
			if(comunicazione.getAllegato() != null) {
				for(int i = 0; i < comunicazione.getAllegato().length; i++) {
					comunicazione.getAllegato(i).setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
				}
			}
		} catch (RemoteException e) {
			throw new ApsException(
					"Errore inaspettato durante l'invio della comunicazione", e);
		}
		return risultato;
	}

	@Override
	public void updateStatoComunicazioni(
			DettaglioComunicazioneType[] comunicazioni, String stato)
			throws ApsException {
		try {
			ErroreOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
					.updateStatoComunicazioni(comunicazioni, stato);
			if (retWS.getErrore() != null)
				throw new ApsException(
						"Errore durante l'aggiornamento dello stato delle comunicazioni: "
								+ retWS.getErrore());
		} catch (RemoteException e) {
			throw new ApsException(
					"Errore inaspettato durante l'aggiornamento dello stato delle comunicazioni",
					e);
		}
	}
	
	@Override
	public void updateDataLetturaDestinatarioComunicazione(String applicativo,
			long idComunicazione, long idDestinatario) throws ApsException {
		try {
			ErroreOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
					.updateDataLetturaDestinatarioComunicazione(applicativo,
							idComunicazione, idDestinatario);
			if (retWS.getErrore() != null)
				throw new ApsException(
						"Errore durante l'aggiornamento della data lettura della comunicazione "
								+ idComunicazione + " per l'applicativo "
								+ applicativo + " al destinatario "
								+ idDestinatario + ": " + retWS.getErrore());
		} catch (RemoteException e) {
			throw new ApsException(
					"Errore inaspettato durante l'aggiornamento data lettura della comunicazione "
							+ idComunicazione + " per l'applicativo "
							+ applicativo + " al destinatario "
							+ idDestinatario, e);
		}
	}

    @Override
	public void deleteComunicazione(String applicativo, long id)
			throws ApsException {
		try {
			ErroreOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
					.deleteComunicazione(applicativo, id);
			if (retWS.getErrore() == null) {
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante l'eliminazione di una comunicazione ("
								+ applicativo + "," + id + "): "
								+ retWS.getErrore());
			}
		} catch (RemoteException e) {
			throw new ApsException(
					"Errore inaspettato durante l'eliminazione di una comunicazione ("
							+ applicativo + "," + id + ")", e);
		}
	}

    @Override
    public Boolean isComunicazioneProcessata(String applicativo, long id)
	    throws ApsException {
	Boolean risultato = null;
	try {
	    IsComunicazioneProcessataOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
		    .isComunicazioneProcessata(applicativo, id);
	    if (retWS.getErrore() == null) {
		risultato = retWS.getComunicazioneProcessata();
	    } else {
		// se si verifica un errore durante l'estrazione dei dati con il
		// servizio, allora si ritorna un'eccezione che contiene il
		// messaggio di errore
		throw new ApsException(
			"Errore durante la verifica sullo stato di avanzamento di una comunicazione: "
				+ retWS.getErrore());
	    }
	} catch (RemoteException e) {
	    throw new ApsException(
		    "Errore inaspettato durante la sullo stato di avanzamento di una comunicazione",
		    e);
	}
	return risultato;
    }

	@Override
	public void protocollaComunicazione(String applicativo, long id,
			String numeroProtocollo, Date dataProtocollo, String stato,
			WSDocumentoType documento) throws ApsException {
		
		this.protocollaComunicazione(applicativo, id, numeroProtocollo,
				dataProtocollo, stato, documento, null);
		
	}

	@Override
	public void protocollaComunicazione(String applicativo, long id,
			String numeroProtocollo, Date dataProtocollo, String stato,
			WSDocumentoType documento, WSAllegatoType[] allegati) throws ApsException {
		try {
			Calendar calDataProtocollo = DateUtils.toCalendar(dataProtocollo);
			ProtocollaComunicazioneOutType retWS = this.wsOpGenerali
					.getProxyWSOPGenerali().protocollaComunicazione(
							applicativo, id, numeroProtocollo, calDataProtocollo,
							stato, documento, allegati);
			if (retWS.getErrore() != null)
				throw new ApsException(
						"Errore durante l'aggiornamento dei dati di protocollazione di una comunicazione ("
								+ applicativo
								+ ","
								+ id
								+ "): "
								+ retWS.getErrore());
		} catch (RemoteException e) {
			throw new ApsException(
					"Errore inaspettato durante l'aggiornamento dei dati di protocollazione di una comunicazione ("
							+ applicativo + "," + id + ")", e);
		}
	}

	@Override
	public void updateSessionKeyComunicazione(String applicativo, long id,
			String sessionKey, String stato) throws ApsException {
		try {
			ErroreOutType retWS = this.wsOpGenerali.getProxyWSOPGenerali()
					.updateSessionKeyComunicazione(applicativo, id, sessionKey,
							stato);
			if (retWS.getErrore() != null)
				throw new ApsException(
						"Errore durante l'aggiornamento della chiave di sessione di una comunicazione ("
								+ applicativo
								+ ","
								+ id
								+ "): "
								+ retWS.getErrore());
		} catch (RemoteException e) {
			throw new ApsException(
					"Errore inaspettato durante l'aggiornamento della chiave di sessione di una comunicazione ("
							+ applicativo + "," + id + ")", e);
		}
	}

}
