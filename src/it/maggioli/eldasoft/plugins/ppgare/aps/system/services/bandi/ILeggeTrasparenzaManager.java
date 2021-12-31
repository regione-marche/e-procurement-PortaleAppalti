package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ConsulenteCollaboratoreType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioContrattoType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.EsitoProspettoBeneficiariType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.FileType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ProspettoContrattoType;

import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione dei dati definiti nell'articolo
 * 18 della legge 134/2012 sulla trasparenza, valutazione, merito per
 * l'estrazione del prospetto affidamenti.
 * 
 * @version 1.7.1
 * @author Stefano.Sabbadin
 */
public interface ILeggeTrasparenzaManager {

	/**
	 * Restituisce la lista di esiti che compongono il prospetto affidamenti nel
	 * rispetto della Legge 134/2012.
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param dataAffidamentoDa
	 *            data affidamento a partire da
	 * @param dataAffidamentoA
	 *            data affidamento fino a
	 * @return lista di esiti che rispettano i filtri impostati
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	List<EsitoProspettoBeneficiariType> getProspettoBeneficiariArt18DLgs83_2012(
			String token, Date dataAffidamentoDa, Date dataAffidamentoA)
			throws ApsException;

	/**
	 * Estrae l'elenco dei documenti allegati per la trasparenza per l'esito di
	 * gara in input.
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param codice
	 *            codice dell'esito di gara
	 * @param codiceBeneficiario
	 *            codice del beneficiario/aggiudicatario
	 * @return elenco dei file allegati per la trasparenza
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	List<FileType> getDocumentiBeneficiario(String token, String codice, String codiceBeneficiario) throws ApsException;
	
	/**
	 * Estrae l'elenco ...
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param cig
	 * @param strutturaProponente
	 * @param oggetto
	 * @param tipoAppalto
	 * @param dataDa
	 * @param dataA
	 * 
	 * @return elenco ...
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	List<ProspettoContrattoType> getProspettoContratti(String token, String cig, String strutturaProponente, String oggetto, String tipoAppalto, String partecipante, String aggiudicatario, Date dataDa, Date dataA) throws ApsException;

	/**
	 * Estrae l'elenco ...
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param cig
	 * @param codice
	 * 
	 * @return ...
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	DettaglioContrattoType getDettaglioContratto(String token, String codice) throws ApsException;
	
	/**
	 * Estrae l'elenco dei consulenti e collaboratori di una gara in input.
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param codice
	 *            codice della gara
	 * @return elenco dei consulenti e collaboratori
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public List<ConsulenteCollaboratoreType> getConsulentiCollaboratori(
			String token,
			String stazioneAppaltante,
			String soggettoPercettore, 
			Date dataDa, 
			Date dataA, 
			String ragioneIncarico, 
			Double compensoPrevistoDa, 
			Double compensoPrevistoA) throws ApsException;
	
	/**
	 * Esegue il download dei documenti di un consulente/collaboratore
	 * 
	 * @param token
	 *            token di autenticazione del client, null altrimenti
	 * @param codice
	 *            codice della gara
	 * @param codiceSoggetto
	 *            codice del soggetto
	 * @return elenco dei documenti del consulente/collaboratore
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public List<FileType> getDocumentiConsulentiCollaboratori(String token, String codice, String codiceSoggetto) throws ApsException;
	
}
