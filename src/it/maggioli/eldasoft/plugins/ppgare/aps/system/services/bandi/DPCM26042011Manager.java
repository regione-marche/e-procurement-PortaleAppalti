package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioBandoOutType;

import java.rmi.RemoteException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore dei bandi esiti ed avvisi nel rispetto del D.P.C.M. del
 * 26/04/2011.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */

public class DPCM26042011Manager extends AbstractService implements
		IDPCM26042011Manager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -1703037109558786055L;

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

	public DettaglioBandoOutType getDettaglioBando(String token, String codice,
			String tipo) throws ApsException {
		DettaglioBandoOutType retWS = null;
		try {
			retWS = this.wsBandiEsitiAvvisi.getProxyWSBandiEsitiAvvisi()
					.getDettaglioBando(token, codice, tipo);
			if (retWS.getErrore() != null) {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
						"Errore durante la lettura del dettaglio bando/esito/avviso di gara per il DPCM: "
								+ retWS.getErrore());
			}

		} catch (RemoteException t) {
			throw new ApsException(
					"Errore inaspettato durante l'estrazione del dettaglio bando/esito/avviso di gara per la produzione della tabella di indicizzazione secondo il DPCM 26/04/2011",
					t);
		}
		return retWS;
	}
}
