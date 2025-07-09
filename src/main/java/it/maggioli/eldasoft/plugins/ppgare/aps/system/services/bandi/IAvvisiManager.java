package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.AvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi.AvvisiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

/**
 * Interfaccia base per il servizio di gestione degli avvisi di gara.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public interface IAvvisiManager {

	/**
	 * Restituisce la lista degli avvisi di carattere generale di gara filtrando per i parametri di input
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
	public SearchResult<AvvisoType> searchAvvisiGenerali(AvvisiSearchBean model) throws ApsException;
	
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
	public SearchResult<AvvisoType> searchAvvisi(AvvisiSearchBean model) throws ApsException;

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
	public SearchResult<AvvisoType> getElencoAvvisi(AvvisiSearchBean model) throws ApsException;

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
	public SearchResult<AvvisoType> getElencoAvvisiScaduti(AvvisiSearchBean model) throws ApsException;
	
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

    /**
     * Restituisce il dettaglio di un avviso di gara a partire dal cig
     * 
     * @param cig
     *            cig associato alla gara
     * 
     * @return dettaglio dell'avviso, costituito dai suoi dati generali, quelli
     *         della stazione appaltante, ed i documenti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public DettaglioAvvisoType getDettaglioAvvisoByCig(String cig)
	    throws ApsException;
    
}
