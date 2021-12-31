package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ConsulenteCollaboratoreType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ConsulentiCollaboratoriOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioContrattoOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioContrattoType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DocumentiBeneficiarioOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DocumentiConsulentiCollaboratoriOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.EsitoProspettoBeneficiariType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.FileType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ProspettoBeneficiariOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ProspettoContrattiOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ProspettoContrattoType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore degli esiti nel rispetto dell'articolo 18 della Legge
 * 134/2012 e Decreto legislativo 83/2012.
 * 
 * @version 1.7.1
 * @author Stefano.Sabbadin
 */

public class LeggeTrasparenzaManager extends AbstractService implements ILeggeTrasparenzaManager {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2338513986554144426L;

	/** Riferimento al wrapper per il web service Bandi Esiti Avvisi */
	private WSBandiEsitiAvvisiWrapper wsBandiEsitiAvvisi;

	public void setWsBandiEsitiAvvisi(WSBandiEsitiAvvisiWrapper wsBandiEsitiAvvisi) {
		this.wsBandiEsitiAvvisi = wsBandiEsitiAvvisi;
	}
	

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public List<EsitoProspettoBeneficiariType> getProspettoBeneficiariArt18DLgs83_2012(
			String token, Date dataAffidamentoDa, Date dataAffidamentoA)
			throws ApsException 
	{
		List<EsitoProspettoBeneficiariType> esiti = null;
		ProspettoBeneficiariOutType retWS = null;
		try {
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi()
					.getProspettoBeneficiari(token, dataAffidamentoDa, dataAffidamentoA);
			if (retWS.getErrore() == null) {
				if (retWS.getEsito() != null)
					esiti = Arrays.asList(retWS.getEsito());
				else
					esiti = new ArrayList<EsitoProspettoBeneficiariType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del prospetto beneficiari: "
								+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione della lista degli esiti per la produzione del prospetto beneficiari",
					t);
		}
		return esiti;
	}

	@Override
	public List<FileType> getDocumentiBeneficiario(String token, String codice, String codiceBeneficiario)
			throws ApsException 
	{
		List<FileType> file = null;
		DocumentiBeneficiarioOutType retWS = null;
		try {
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi()
					.getDocumentiBeneficiario(token, codice, codiceBeneficiario);
			if (retWS.getErrore() == null) {
				if (retWS.getDocumento() != null)
					file = Arrays.asList(retWS.getDocumento());
				else
					file = new ArrayList<FileType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura dei documenti allegati del prospetto beneficiari: "
								+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione dei documenti allegati del prospetto beneficiari",
					t);
		}
		return file;
	}

	@Override
	public List<ProspettoContrattoType> getProspettoContratti(
			String token,
			String cig, 
			String strutturaProponente, 
			String oggetto,
			String tipoAppalto,
			String partecipante,
			String aggiudicatario,
			Date dataDa, Date dataA) throws ApsException 
	{	
		List<ProspettoContrattoType> prospettoContratti = null;
		ProspettoContrattiOutType retWS = null;
		
		try{
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi().getProspettoContratti(
					token, 
					cig, 
					strutturaProponente, 
					oggetto, 
					tipoAppalto,
					partecipante,
					aggiudicatario,
					dataDa, dataA);
			if(retWS.getErrore() == null) {
				if(retWS.getContratto() != null) {
					prospettoContratti = Arrays.asList(retWS.getContratto());
				} else {
					prospettoContratti = new ArrayList<ProspettoContrattoType>();
				}
			}else{
				throw new ApsException(
						"Errore inaspettato durante l'estrazione del prospetto per i contratti: "
								+ retWS.getErrore());
			}
		}catch(RemoteException t){
			throw new ApsException(
					"Errore inaspettato durante l'estrazione del prospetto per i contratti ",
					t);
		}
		
		return prospettoContratti;
	}

	@Override
	public DettaglioContrattoType getDettaglioContratto(String token, String codice) throws ApsException {
		
		DettaglioContrattoType dettaglioContratto = null;
		DettaglioContrattoOutType retWS = null;
			
		try {
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi().getDettaglioContratto(token, codice);
			if(retWS.getErrore() == null) {
				if(retWS.getDettaglioContratto() != null) {
					dettaglioContratto = retWS.getDettaglioContratto();
				} else {
					dettaglioContratto = new DettaglioContrattoType();
				}
			} else {
				throw new ApsException(
						"Errore inaspettato durante l'estrazione del dettaglio per il contratto relativo alla gara " 
								+ retWS.getErrore());
			}
		} catch(RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione del dettaglio per il contratto relativo al codice " + codice,t);
		}
		
		return dettaglioContratto;
	}

	@Override
	public List<ConsulenteCollaboratoreType> getConsulentiCollaboratori(
			String token,
			String stazioneAppaltante,
			String soggettoPercettore, 
			Date dataDa, 
			Date dataA, 
			String ragioneIncarico, 
			Double compensoPrevistoDa, 
			Double compensoPrevistoA) throws ApsException 
	{	
		List<ConsulenteCollaboratoreType> elenco = null;
		ConsulentiCollaboratoriOutType retWS = null;
			
		try {
			stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, stazioneAppaltante);

			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi().getConsulentiCollaboratori(
					token, 
					stazioneAppaltante,
					soggettoPercettore, 
					dataDa, 
					dataA, 
					ragioneIncarico, 
					compensoPrevistoDa, 
					compensoPrevistoA);
			if(retWS.getErrore() == null) {
				if(retWS.getConsulentiCollaboratori() != null) {
					elenco = Arrays.asList(retWS.getConsulentiCollaboratori());
				} else {
					elenco = new ArrayList<ConsulenteCollaboratoreType>();
				}
			} else {
				throw new ApsException(
						"Errore inaspettato durante l'estrazione dei consulenti e collaboratori " 
								+ retWS.getErrore());
			}
		} catch(RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione dei consulenti e collaboratori ", t);
		}
		
		return elenco;
	}

	@Override
	public List<FileType> getDocumentiConsulentiCollaboratori(
			String token, 
			String codice, 
			String codiceSoggetto) throws ApsException 
	{
		List<FileType> files = null;
		DocumentiConsulentiCollaboratoriOutType retWS = null;
		try {
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi()
				.getDocumentiConsulentiCollaboratori(token, codice, codiceSoggetto);
			if(retWS.getErrore() == null) {
				if(retWS.getDocumento() != null) {
					files = Arrays.asList(retWS.getDocumento());
				} else {
					files = new ArrayList<FileType>();
				}
			} else {
				throw new ApsException(
						"Errore inaspettato durante l'estrazione dei documenti per consulenti e collaboratori " 
								+ retWS.getErrore());
			}
		} catch(RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione dei documenti per consulenti e collaboratori ", t);
		}
		
		return files;
	}
	
}
