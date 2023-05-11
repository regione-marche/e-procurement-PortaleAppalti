package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AdempimentoAnticorruzioneOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AdempimentoAnticorruzioneType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AppaltoAggiudicatoAnticorruzioneType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ProspettoGareContrattiAnticorruzioneOutType;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore degli esiti nel rispetto dell'articolo 1 della Legge
 * 190/2012.
 * 
 * @version 1.8.0
 * @author Stefano.Sabbadin
 */

public class LeggeAnticorruzioneManager extends AbstractService implements
		ILeggeAnticorruzioneManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 2338513986554144426L;

	/** Riferimento al wrapper per il web service Bandi Esiti Avvisi */
	private WSBandiEsitiAvvisiWrapper wsBandiEsitiAvvisi;

	/**
	 * @param wsBandiEsitiAvvisi
	 *            the wsBandiEsitiAvvisi to set
	 */
	public void setWsBandiEsitiAvvisi(
			WSBandiEsitiAvvisiWrapper wsBandiEsitiAvvisi) {
		this.wsBandiEsitiAvvisi = wsBandiEsitiAvvisi;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public List<AppaltoAggiudicatoAnticorruzioneType> getProspettoGareContrattiAnticorruzione(
			String token, int anno, String cig, String proponente,
			String oggetto, 
			String partecipante, String aggiudicatario)
			throws ApsException {
		List<AppaltoAggiudicatoAnticorruzioneType> appalti = null;
		ProspettoGareContrattiAnticorruzioneOutType retWS = null;
		try {
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi()
					.getProspettoGareContrattiAnticorruzione(token,
							anno, cig, proponente, oggetto, partecipante, aggiudicatario);
			if (retWS.getErrore() == null) {
				if (retWS.getAppalto() != null)
					appalti = Arrays.asList(retWS.getAppalto());
				else
					appalti = new ArrayList<AppaltoAggiudicatoAnticorruzioneType>();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del prospetto gare e contratti anticorruzione secondo la Legge 190/2012: "
								+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione della lista dei record per la produzione del prospetto gare e contratti anticorruzione secondo la Legge 190/2012",
					t);
		}
		return appalti;
	}
	
	@Override
	public List<AdempimentoAnticorruzioneType> getAdempimentiAnticorruzione(String token, Integer anno) throws ApsException{
		List<AdempimentoAnticorruzioneType> adempimenti = null;
		AdempimentoAnticorruzioneOutType retWS = null;
		
		try{
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi().getAdempimentiAnticorruzione(token, anno);
			
			if(retWS.getErrore() == null){
				if(retWS.getAdempimento() != null){
					adempimenti = Arrays.asList(retWS.getAdempimento());
				}else{
					adempimenti = new ArrayList<AdempimentoAnticorruzioneType>();
				}
			}else{
				
			}
		}catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione della lista dei record per il recupero delle infromazioni sugli adempimenti anticorruzione secondo la Legge 190/2012",
					t);
		}
		
		return adempimenti;
	}


}
