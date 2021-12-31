package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoType;
import it.eldasoft.www.sil.WSGareAppalto.EsitoType;
import it.eldasoft.www.sil.WSGareAppalto.LottoEsitoType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia base per il servizio di gestione degli esiti di gara.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public interface IEsitiManager {

	/**
	 * Restituisce la lista degli esiti filtrando per i parametri di input.
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * 
	 * @return lista esiti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public SearchResult<EsitoType> searchEsiti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Boolean proceduraTelematica,
			String altriSoggetti,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;

	/**
	 * Ritorna l'elenco degli esiti di gara.
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 *  
	 * @return lista esiti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public SearchResult<EsitoType> getElencoEsiti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Boolean proceduraTelematica,
			String altriSoggetti,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;
		
	/**
	 * Ritorna l'elenco degli esiti collegati ad elenco operatori economici.
	 * 
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 *   
	 * @return lista esiti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public SearchResult<EsitoType> getElencoEsitiAffidamenti(
			String stazioneAppaltante,
			String oggetto, 
			String cig, 
			String tipoAppalto,
			Date dataPubblicazioneDa, Date dataPubblicazioneA,
			Boolean proceduraTelematica,
			String altriSoggetti,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;
	
    /**
     * Restituisce il dettaglio di un esito a partire dalla sua chiave.
     * 
     * @param codiceGara
     *            codice univoco della gara
     * 
     * @return dettaglio dell'esito, costituito dai suoi dati generali, quelli
     *         della stazione appaltante, ed i documenti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
	public DettaglioEsitoType getDettaglioEsito(String codiceGara)
	    throws ApsException;

    /**
     * Restituisce i lotti di un esito a partire dalla sua chiave
     * 
     * @param codiceGara
     *            codice univoco della gara
     * 
     * @return dati dei lotti e dati comuni tra i lotti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public LottoEsitoType[] getLottiEsito(String codiceGara) throws ApsException;
	
}


