package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AdempimentoAnticorruzioneType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AppaltoAggiudicatoAnticorruzioneType;

import java.util.List;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione dei dati definiti nell'articolo
 * 1 della legge 190/2012 (Legge anticorruzione) sulle procedure di acquisizione
 * per gare e contratti.
 * 
 * @version 1.8.0
 * @author Stefano.Sabbadin
 */
public interface ILeggeAnticorruzioneManager {

	/**
	 * Restituisce la lista di esiti che compongono il prospetto procedure di
	 * acquisizione di gare e contratti nel rispetto della Legge 190/2012.
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param anno
	 *            anno di riferimento (campo obbligatorio)
	 * @param cig
	 *            eventuale filtro sul cig
	 * @param proponente
	 *            eventuale filtro sul codice fiscale o denominazione della
	 *            stazione appaltante
	 * @param oggetto
	 *            eventuale filtro sull'oggetto
	 * @param partecipante
	 *            eventuale filtro sul codice fiscale o denominazione di un
	 *            partecipante
	 * @param aggiudicatario
	 *            eventuale filtro sul codice fiscale o denominazione di un
	 *            aggiudicatario
	 * @return lista di esiti che rispettano i filtri impostati
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	List<AppaltoAggiudicatoAnticorruzioneType> getProspettoGareContrattiAnticorruzione(
			String token, int anno, String cig, String proponente,
			String oggetto, 
			String partecipante, String aggiudicatario) throws ApsException;
	
	List<AdempimentoAnticorruzioneType> getAdempimentiAnticorruzione(String token, Integer anno) throws ApsException;
}
