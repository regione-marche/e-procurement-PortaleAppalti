package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi;

import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaCatalogoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;

import java.util.List;

import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiSearchBean;

/**
 * Interfaccia base per il servizio di gestione dei cataloghi.
 *
 * @version 1.8.5
 * @author Stefano.Sabbadin
 */
public interface ICataloghiManager {

	/**
	 * Restituisce la lista dei cataloghi filtrati dai parametri in input
	 *
	 * @param username username dell'impresa
	 * @param stato stato dell'iscrizione dell'impresa
	 *
	 * @return lista bandi
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public List<BandoIscrizioneType> searchCataloghi(String username, Integer stato)
					throws ApsException;

	/**
	 * Ritorna l'elenco dei cataloghi in corso di validit&agrave;.
	 *
	 * @return lista cataloghi
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public List<BandoIscrizioneType> getElencoCataloghi() throws ApsException;

	/**
	 * Restituisce il dettaglio di un catalogo a partire dalla sua chiave.
	 *
	 * @param codiceCatalogo il codice del catalogo
	 * @return dettaglio del catalogo, costituito dai suoi dati generali, quelli
	 * della stazione appaltante, i documenti, chiarimenti e le comunicazioni
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public DettaglioBandoIscrizioneType getDettaglioCatalogo(
					String codiceCatalogo) throws ApsException;

	/**
	 * Restituisce la lista degli articoli filtrando per i parametri di input.
	 *
	 * @param codiceCatalogo codice del catalogo
	 * @param codiceCategoria eventuale codice categoria
	 * @param codice eventuale codice articolo
	 * @param tipo eventuale tipo articolo
	 * @param descrizione eventuale descrizione
	 * @param colore eventuale colore
	 * @param username eventuale username dell'impresa
	 * @param indicePrimoRecord indice del primo record da considerare, a partire
	 * da 0
	 * @param maxNumRecord numero massimo di record da estrarre, 0 per estrarli
	 * tutti
	 *
	 * @return lista articoli, di tipo ArticoloType
	 *
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public SearchResult<ArticoloType> searchArticoli(String codiceCatalogo,
					String codiceCategoria, String codice, String tipo,
					String descrizione, String colore, String username, 
					int indicePrimoRecord, int maxNumRecord) throws ApsException;

	/**
	 * Restituisce il dettaglio di un articolo a partire dalla sua chiave.
	 *
	 * @param id id articolo
	 *
	 * @return dettaglio
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public ArticoloType getArticolo(Long id) throws ApsException;

	/**
	 * Restituisce il numero di prodotti inseriti in stato "In catalogo" o "In
	 * attesa di verifica" da parte dell'impresa in input su un determinato
	 * articolo in catalogo.
	 *
	 * @param idArticolo id articolo
	 * @param username username dell'impresa
	 * @return numero di prodotti inseriti dall'impresa per l'articolo
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public Long getNumProdottiOEInArticolo(long idArticolo, String username)
					throws ApsException;

	/**
	 * Restituisce il numero di prodotti inseriti in stato "In catalogo" da parte
	 * di tutti gli altri operatori economici diversi dall'impresa in input.
	 *
	 * @param idArticolo id articolo
	 * @param username username dell'impresa
	 * @return numero di prodotti inseriti dalle altre imprese per l'articolo
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public Long getNumProdottiAltriOEInArticolo(long idArticolo, String username)
					throws ApsException;

	/**
	 * Fornisce il prezzo migliore offerto per un determinato articolo
	 * considerando tutti i prodotti inseriti in catalogo.
	 *
	 * @param idArticolo id articolo
	 * @return prezzo migliore, null se non presente alcun prodotto
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public Double getPrezzoMiglioreArticolo(long idArticolo)
					throws ApsException;

	/**
	 * Fornisce l'indicazione se l'impresa in input risulta abilitata nel catalogo
	 * indicato.
	 *
	 * @param codiceCatalogo codice del catalogo
	 * @param username username dell'impresa
	 * @return true se l'impresa e' abilitata, false altrimenti
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public Boolean isImpresaAbilitataCatalogo(String codiceCatalogo,
					String username) throws ApsException;

	/**
	 * Ritorna l'elenco dei prodotti filtrati in base ai parametri in input.
	 *
	 * @param codiceCatalogo codice del catalogo
	 * @param idArticolo id univoco dell'articolo
	 * @param username username dell'impresa
	 * @param terminiRicerca i termini di ricerca google like
	 * @param indicePrimoRecord indice del primo record da considerare, a partire
	 * da 0
	 * @param maxNumRecord numero massimo di record da estrarre, 0 per estrarli
	 * tutti
	 *
	 * @return lista prodotti, di tipo ProdottoType
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public SearchResult<ProdottoType> searchProdotti(String codiceCatalogo,
					Long idArticolo, String username, String terminiRicerca,
					int indicePrimoRecord, int maxNumRecord) throws ApsException;

	/**
	 * Ritorna l'elenco dei prodotti filtrati in base ai parametri in input.
	 *
	 * @param codiceCatalogo codice del catalogo
	 * @param idArticolo id univoco dell'articolo
	 * @param username username dell'impresa
	 * @param parametri oggetto contenente i parametri di ricerca avanzata
	 *
	 * @return lista prodotti, di tipo ProdottoType
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public SearchResult<ProdottoType> searchProdotti(String codiceCatalogo,
					Long idArticolo, String username, ProdottiSearchBean parametri)
					throws ApsException;

	/**
	 * Estrae il dettaglio di un prodotto.
	 *
	 * @param id id prodotto
	 * @return dettaglio
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
	public ProdottoType getProdotto(long id) throws ApsException;

	/**
	 * Restituisce, a partire da un catalogo e da una impresa, l'elenco delle
	 * categorie per cui un'impresa risulta iscritta al catalogo, e per ogni
	 * categoria ritorna i prodotti ad essa associati.<br/>
	 * Vengono ritornate le sole categorie contenenti articoli, oppure categorie
	 * aventi categorie figlie contenenti articoli.
	 *
	 * @param codiceCatalogo codice del catalogo
	 * @param username username dell'impresa
	 * @return categorie su cui risulta iscritta l'impresa e contenenti articoli
	 * @throws ApsException
	 */
	public List<CategoriaCatalogoType> getCategorieArticoliOE(
					String codiceCatalogo, String username) throws ApsException;
}
