package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;

import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia Manager per estrarre i dati necessari al cruscotto di monitoraggio.
 * 
 * @author Eleonora.Favaro
 */
public interface IMonitoraggioManager {
	
	/**
	 * Estrae la lista degli operatori economici non ancora attivati in base ai
	 * filtri in input.
	 * 
	 * @param utente
	 *            username utente o sua porzione (non sensibile a
	 *            maiuscole/minuscole)
	 * @param email
	 *            email utente o sua porzione (non sensibile a
	 *            maiuscole/minuscole)
	 * @param dataRegistrazioneDa
	 *            data registrazione utente a partire da
	 * @param dataRegistrazioneA
	 *            data registrazione utente fino a
	 * @return lista utenti di portale registrati, non attivi e che non hanno
	 *         mai impostato la password (e quindi non hanno mai fatto alcun
	 *         accesso).
	 */
	List<OperatoreEconomicoKO> getOperatoriEconomiciKO(String utente, String email, Date dataRegistrazioneDa, Date dataRegistrazioneA);

	/**
	 * Estra la mail di riferimento di un dato utente in input.
	 * 
	 * @param utenteSelezionato
	 *            username utente
	 * @return indirizzo mail di riferimento per l'utente
	 */
	String getEmailOperatore(String utenteSelezionato);

	/**
	 * Estrae la lista dei flussi processati con errori.
	 * 
	 * @param utente
	 *            eventuale utente avente inviato il flusso
	 * @param mittente
	 *            eventuale mittente/ragione sociale dell'utente avente inviato il flusso
	 * @param tipoComunicazione
	 *            eventuale tipologia di comunicazione (FS*)
	 * @param riferimentoProcedura
	 *            eventuale riferimento a procedura
	 * @return lista delle comunicazioni processate con errori filtrate rispetto
	 *         ai criteri in input, ordinate dalla pi&ugrave; recente alla
	 *         pi&ugrave; vecchia
	 * 
	 * @throws ApsException
	 */
	List<FlussiInviatiKO> getInviiFlussiConErrori(String utente, String mittente, String tipoComunicazione,
			String riferimentoProcedura) throws ApsException;

	/**
	 * Estrae una comunicazione data la sua chiave.
	 * 
	 * @param flussoSelezionato
	 *            id comunicazione
	 * @return dettaglio comunicazione
	 * @throws ApsException
	 */
	DettaglioComunicazioneType getFlusso(Long flussoSelezionato) throws ApsException;
	
	/**
	 * Aggiorna lo stato della comunicazione in input a "Da processare" (5).
	 * 
	 * @param comunicazione
	 *            dettaglio della comunicazione
	 * @throws ApsException
	 */
	void rielaboraFlusso(DettaglioComunicazioneType comunicazione) throws ApsException;

}
