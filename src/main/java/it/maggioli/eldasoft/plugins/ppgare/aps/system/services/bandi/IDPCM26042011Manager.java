package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioBandoOutType;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione dei dati definiti nel D.P.C.M.
 * del 24/04/2011 sui bandi, esiti ed avvisi.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public interface IDPCM26042011Manager {

	/**
	 * Restituisce i dati di dettaglio di un bando, un esito o un avviso nel
	 * rispetto del D.P.C.M. del 26/04/2011.<br/>
	 * I dati sono memorizzati in un array in quanto viene fornito il record di
	 * dettaglio del bando ed i record per i singoli lotti, strutturati tutti
	 * nello stesso modo.
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param codice
	 *            codice del bando/esito/avviso
	 * @param tipo
	 *            tipologia, valori ammessi: "Bando", "Esito", "Avviso"
	 * @return dettaglio dell'oggetto richiesto
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	DettaglioBandoOutType getDettaglioBando(String token, String codice,
			String tipo) throws ApsException;
	
}
