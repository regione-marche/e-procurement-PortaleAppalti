package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio;

import java.util.Date;
import java.util.List;

/**
 * Interfaccia DAO per estrarre i dati dal DB necessari al cruscotto di monitoraggio.
 * 
 * @author Eleonora.Favaro
 */
public interface IMonitoraggioDAO {

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
	List<OperatoreEconomicoKO> searchOperatoriEconomiciKO(String utente,
			String email, Date dataRegistrazioneDa, Date dataRegistrazioneA);

	/**
	 * Estra la mail di riferimento di un dato utente in input.
	 * 
	 * @param utenteSelezionato
	 *            username utente
	 * @return indirizzo mail di riferimento per l'utente
	 */
	String getEmailOperatore(String utenteSelezionato);

}
