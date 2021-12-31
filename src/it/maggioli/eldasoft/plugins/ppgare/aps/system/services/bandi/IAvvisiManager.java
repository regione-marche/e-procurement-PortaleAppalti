package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.util.Date;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione degli avvisi di gara.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public interface IAvvisiManager {

	/**
	 * Restituisce la lista degli avvisi di gara filtrando per i parametri di input
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * 
	 * @return lista avvisi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public SearchResult<AvvisoType> searchAvvisi(
			String stazioneAppaltante,
			String oggetto, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Date dataScadenzaDa, Date dataScadenzaA,
			String altriSoggetti,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;

	/**
	 * Ritorna l'elenco degli avvisi di gara in corso di validit&agrave;.
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 *             
	 * @return lista avvisi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public SearchResult<AvvisoType> getElencoAvvisi(
			String stazioneAppaltante,
			String oggetto, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Date dataScadenzaDa, Date dataScadenzaA,
			String altriSoggetti,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;

	/**
	 * Ritorna l'elenco degli avvisi di gara scaduti.
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 *             
	 * @return lista avvisi scaduti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public SearchResult<AvvisoType> getElencoAvvisiScaduti(
			String stazioneAppaltante,
			String oggetto, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Date dataScadenzaDa, Date dataScadenzaA,
			String altriSoggetti,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;
	
    /**
     * Restituisce il dettaglio di un avviso di gara a partire dalla sua chiave
     * 
     * @param codiceGara
     *            codice univoco della gara
     * 
     * @return dettaglio dell'avviso, costituito dai suoi dati generali, quelli
     *         della stazione appaltante, ed i documenti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public DettaglioAvvisoType getDettaglioAvviso(String codiceGara)
	    throws ApsException;

}
