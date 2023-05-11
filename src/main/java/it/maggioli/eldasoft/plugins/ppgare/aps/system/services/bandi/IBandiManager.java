package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.www.sil.WSGareAppalto.*;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.BandiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Interfaccia base per il servizio di gestione dei bandi.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public interface IBandiManager {

	/**
	 * Restituisce la lista dei bandi filtrando per i parametri di input
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
	 * 
	 * @return lista bandi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	SearchResult<GaraType> searchBandi(BandiSearchBean search) throws ApsException;

    /**
	 * Ritorna l'elenco dei bandi di gara in corso di validit&agrave;.
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
	 *             
	 * @return lista bandi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	SearchResult<GaraType> getElencoBandi(BandiSearchBean search) throws ApsException;

	/**
	 * Ritorna l'elenco dei bandi di gara scaduti.
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
	 *             
	 * @return lista bandi scaduti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	SearchResult<GaraType> getElencoBandiScaduti(BandiSearchBean search) throws ApsException;
	
	/**
	 * Restituisce una stringa contenente il singolo CIG o la lista dei CIG dei
	 * lotti per la gara in input.
	 * 
	 * @param codiceGara
	 *            codice univoco della gara
	 * @return cig
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public String getCigBando(String codiceGara) throws ApsException;

    /**
     * Restituisce il dettaglio di una gara a partire dalla sua chiave
     * 
     * @param codiceGara
     *            codice univoco della gara
     * 
     * @return dettaglio della gara, costituito dai suoi dati generali, quelli
     *         della stazione appaltante, i documenti, chiarimenti e le
     *         comunicazioni
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public DettaglioGaraType getDettaglioGara(String codiceGara)
	    throws ApsException;


    /**
     * Restituisce il dettaglio di una gara a partire da un suo lotto.
     * 
     * @param codiceLotto
     *            codice univoco del lotto
     * 
     * @return dettaglio della gara, costituito dai suoi dati generali, quelli
     *         della stazione appaltante, i documenti, chiarimenti e le
     *         comunicazioni
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public DettaglioGaraType getDettaglioGaraFromLotto(String codiceLotto)
	    throws ApsException;
    
    /**
     * Restituisce i lotti di una gara a partire dalla sua chiave
     * 
     * @param codiceGara
     *            codice univoco della gara
     * 
     * @return dati dei lotti e dati comuni tra i lotti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public LottoGaraType[] getLottiGara(String codiceGara) throws ApsException;

    /**
	 * Restituisce i lotti di una gara non conclusi a partire dalla sua chiave,
	 * per la domanda di partecipazione.
	 * 
	 * @param codiceGara
	 *            codice univoco della gara
	 * 
	 * @return dati dei lotti e dati comuni tra i lotti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public LottoGaraType[] getLottiGaraPerDomandePartecipazione(
			String codiceGara, 
			String progOfferta) throws ApsException;

	/**
	 * Restituisce i lotti di una gara abilitati per un'impresa per la richiesta
	 * di offerta
	 * 
	 * @param username
	 *            username dell'impresa
	 * @param codiceGara
	 *            codice univoco della gara
	 * 
	 * @return dati dei lotti e dati comuni tra i lotti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public LottoGaraType[] getLottiGaraPerRichiesteOfferta(
			String username,
			String codiceGara,
			String progOfferta) throws ApsException;

	/**
	 * Restituisce i lotti di una gara abilitati per un'impresa per la richiesta
	 * di offerta per una gara a lotti a plico unico con offerte distinte.
	 * 
	 * @param username
	 *            username dell'impresa
	 * @param codiceGara
	 *            codice univoco della gara
	 * 
	 * @return dati dei lotti e dati comuni tra i lotti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public LottoGaraType[] getLottiGaraPlicoUnicoPerRichiesteOfferta(
			String username,
			String codiceGara,
			String progOfferta) throws ApsException;

	/**
	 * Restituisce i lotti di una gara abilitati per un'impresa per la richiesta
	 * di comprova requisiti
	 * 
	 * @param username
	 *            username dell'impresa
	 * @param codiceGara
	 *            codice univoco della gara
	 * 
	 * @return dati dei lotti e dati comuni tra i lotti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public LottoGaraType[] getLottiGaraPerRichiesteCheckDocumentazione(String username,
			String codiceGara) throws ApsException;

	/**
	 * Verifica se una gara prevede la busta per l'offerta tecnica
	 * (perch&egrave; di tipo offerta economicamente pi&ugrave; vantaggiosa
	 * oppure perch&egrave; gestita da configurazione della gara stessa).
	 * 
	 * @param codiceGara
	 *            codice univoco della gara
	 * @return true in caso di gara busta tecnica, false altrimenti
	 * @throws ApsException
	 */
    public Boolean isGaraConOffertaTecnica(String codiceGara) throws ApsException;
    
	/**
	 * Restituisce la lista dei bandi d'iscrizione filtrati dai parametri in
	 * input
	 *
	 * @param username username dell'impresa
	 * @param stato    stato dell'iscrizione dell'impresa
	 * @param isAttivo
	 * @return lista bandi
	 * @throws ApsException In caso di errori in accesso al servizio web.
	 */
    public List<BandoIscrizioneType> searchBandiIscrizione(String username, Integer stato, boolean isAttivo)
	    throws ApsException;

    /**
     * Restituisce la lista dei bandi d'iscrizione
     * 
     * @return lista bandi
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public List<BandoIscrizioneType> getElencoBandiIscrizione(boolean isAttivo)
	    throws ApsException;

    /**
     * Restituisce il dettaglio di un bando d'iscrizione a partire dalla sua
     * chiave
     * 
     * @param codice
     *            codice univoco del bando d'iscrizione
     * 
     * @return dettaglio del bando d'iscrizione, costituito dai suoi dati
     *         generali, quelli della stazione appaltante, i documenti,
     *         chiarimenti e le comunicazioni
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public DettaglioBandoIscrizioneType getDettaglioBandoIscrizione(
	    String codice) throws ApsException;

    /**
     * Restituisce la lista delle categorie/prestazioni associate al bando
     * d'iscrizione
     * 
     * @param codice
     *            codice univoco del bando d'iscrizione
     * @param filtroCategorie eventuale filtro sulle categorie
     * @return lista categorie/prestazioni
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public CategoriaBandoIscrizioneType[] getElencoCategorieBandoIscrizione(String codice, String filtroCategorie)
	    throws ApsException;
    
	/**
	 * Restituisce le statistiche delle comunicazioni ricevute e archiviate,
	 * eventualmente da leggere, e quelle inviate, per una data entit&agrave;.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codice
	 *            codice univoco della gara/lotto/bando d'iscrizione
	 * @param entita
	 *            (opzionale) codice del tipo di entita' (TORN, GARECONT, G1STIPULA, APPA, ...) 
	 * 
	 * @return statistiche delle personali dell'utente
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    StatisticheComunicazioniPersonaliType getStatisticheComunicazioniPersonali(
			String username, 
			String codice,
			String codice2,
			String entita) throws ApsException;
    
	/**
	 * Restituisce le statistiche delle comunicazioni ricevute e archiviate,
	 * eventualmente da leggere, e quelle inviate, per una gara a lotti distinti.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codice
	 *            codice univoco della gara a lotti distinti
	 * @param entita
	 *            (opzionale) codice del tipo di entita' (TORN, GARECONT, G1STIPULA, APPA, ...) 
	 * 
	 * @return statistiche delle personali dell'utente
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    StatisticheComunicazioniPersonaliType getStatisticheComunicazioniPersonaliGaraLotti(
			String username, 
			String codiceGara,
			String entita) throws ApsException;

	/**
	 * Restituisce le comunicazioni ricevute (cio&egrave; entro gli ultimi 90
	 * giorni) da un utente per una eventuale entit&agrave;.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codice
	 *            (opzionale) codice univoco della gara/lotto/bando d'iscrizione
	 * @param soccorsoIstruttorio
	 *            (opzionale) true per le comunicazioni di soccorso istruttorio, false per tutte le altre
	 * @param entita
	 *            (opzionale) codice delle entita' da recuperare (TORN, GARECONT, G1STIPULE, APPA, etc)
	 * @param indicePrimoRecord
	 *            indice del primo record da considerare, a partire da 0
	 * @param maxNumRecord
	 *            numero massimo di record da estrarre, 0 per estrarli tutti
	 * 
	 * @return lista paginata delle comunicazioni ricevute
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    SearchResult<ComunicazioneType> getComunicazioniPersonaliRicevute(
			String username, 
			String codice, 
			String codice2,
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException;
    
	/**
	 * Restituisce le comunicazioni ricevute (cio&egrave; entro gli ultimi 90
	 * giorni) da un utente per una gara a lotti.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codiceGara
	 *            codice univoco della gara a lotti distinti
   	 * @param soccorsoIstruttorio
	 *            (opzionale) true per le comunicazioni di soccorso istruttorio, false per tutte le altre
	 * @param entita
	 *            (opzionale) codice delle entita' da recuperare (TORN, GARECONT, G1STIPULE, APPA, etc)
	 * @param indicePrimoRecord
	 *            indice del primo record da considerare, a partire da 0
	 * @param maxNumRecord
	 *            numero massimo di record da estrarre, 0 per estrarli tutti
	 * 
	 * @return lista paginata delle comunicazioni ricevute
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    SearchResult<ComunicazioneType> getComunicazioniPersonaliRicevuteGaraLotti(
			String username, 
			String codiceGara, 
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException;
    
	/**
	 * Restituisce le comunicazioni ricevute ed archiviate (cio&egrave; ricevute
	 * oltre 90 giorni fa) da un utente per una eventuale entit&agrave;.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codice
	 *            (opzionale) codice univoco della gara/lotto/bando d'iscrizione
   	 * @param entita
	 *            (opzionale) codice del tipo di entita' (TORN, GARECONT, G1STIPULA, APPA, ...) 
	 * @param indicePrimoRecord
	 *            indice del primo record da considerare, a partire da 0
	 * @param maxNumRecord
	 *            numero massimo di record da estrarre, 0 per estrarli tutti
	 * 
	 * @return lista paginata delle comunicazioni archiviate
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    SearchResult<ComunicazioneType> getComunicazioniPersonaliArchiviate(
			String username, 
			String codice, 
			String codice2,
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException;
    
	/**
	 * Restituisce le comunicazioni ricevute ed archiviate (cio&egrave; ricevute
	 * oltre 90 giorni fa) da un utente per una gara a lotti.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codiceGara
	 *            codice univoco della gara a lotti distinti
	 * @param entita
	 *            (opzionale) codice del tipo di entita' (TORN, GARECONT, G1STIPULA, APPA, ...) 
	 * @param indicePrimoRecord
	 *            indice del primo record da considerare, a partire da 0
	 * @param maxNumRecord
	 *            numero massimo di record da estrarre, 0 per estrarli tutti
	 * 
	 * @return lista paginata delle comunicazioni archiviate
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    SearchResult<ComunicazioneType> getComunicazioniPersonaliArchiviateGaraLotti(
			String username, 
			String codiceGara, 
			boolean soccorsoIstruttorio,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException;
    
    /**
	 * Restituisce le comunicazioni inviate da un utente per una eventuale
	 * entit&agrave;.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param codice
	 *            (opzionale) codice univoco della gara/lotto/bando d'iscrizione
	 * @param indicePrimoRecord
	 *            indice del primo record da considerare, a partire da 0
	 * @param maxNumRecord
	 *            numero massimo di record da estrarre, 0 per estrarli tutti
	 * 
	 * @return lista paginata delle comunicazioni inviate
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    SearchResult<ComunicazioneType> getComunicazioniPersonaliInviate(
			String username, 
			String codice, 
			String codice2,
			String entita,
			int indicePrimoRecord, int maxNumRecord) throws ApsException;
    
	/**
	 * Restituisce il dettaglio di una comunicazione ricevuta da un utente.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param idComunicazione
	 *            id della comunicazione da estrarre
	 * @param idDestinatario
	 *            progressivo del destinatario relativo alla comunicazione
	 * 
	 * @return dettaglio comunicazione ricevuta
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    ComunicazioneType getComunicazioneRicevuta(
    		String username,
			long idComunicazione,
			long idDestinatario,
			String idprg) throws ApsException;
    
	/**
	 * Restituisce il dettaglio di una comunicazione inviata da un utente.
	 * 
	 * @param username
	 *            username dell'utente
	 * @param idComunicazione
	 *            id della comunicazione da estrarre
	 * 
	 * @return dettaglio comunicazione inviata
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    ComunicazioneType getComunicazioneInviata(
    		String username,
			long idComunicazione,
			String idprg) throws ApsException;
			
    /**
     * Estrae i dati dell'impresa da presentare all'atto dell'apertura di una
     * modifica anagrafica quando si estraggono dal backoffice
     * 
     * @param username
     *            username dell'impresa
     * @return contenitore con i dati dell'impresa e dei suoi referenti
     * @throws ApsException
     */
    public DatiImpresaDocument getDatiImpresa(String username, Date dataPartenzaCessati)
	    throws ApsException;

	/**
	 * Estrae l'elenco dei documenti richiesti all'atto dell'inserimento di una
	 * domanda di partecipazione o un invio offerta in una gara telematica,
	 * dipendente anche dalla tipologia d'impresa
	 * 
	 * @param codiceGara
	 *            codice univoco della gara
	 * @param codiceLotto
	 *            codice univoco dell'eventuale lotto
	 * @param tipoImpresa
	 *            tipologia d'impresa
	 * @param rti impresa partecipa come rti
	 * @param busta
	 *            tipologia di busta
	 *            <ul>
	 *            <li><b>1</b>: amministrativa</li>
	 *            <li><b>2</b>: tecnica</li>
	 *            <li><b>3</b>: economica</li>
	 *            </ul>
	 * @return elenco dei documenti richiesti
	 * @throws ApsException
	 */
	public List<DocumentazioneRichiestaType> getDocumentiRichiestiBandoGara(
			String codiceGara, String codiceLotto, String tipoImpresa, boolean rti, String busta)
			throws ApsException;

	/**
	 * Estrae l'elenco dei documenti richiesti all'atto dell'inserimento di un invio offerta in una gara telematica a plico unico con offerte distinte.
	 * La presente operazione estrae <b>TUTTI</b> i documenti richiesti suddivisi per lotto e per busta.
	 * 
	 * @param codiceGara
	 *            codice univoco della gara
	 * @param tipoImpresa
	 *            tipologia d'impresa
	 * @param rti impresa partecipa come rti
	 * @return elenco dei documenti richiesti, suddivisi per busta e per lotto
	 * @throws ApsException
	 */
	public DocumentazioneRichiestaGaraPlicoUnico getDocumentiRichiestiBandoGaraPlicoUnico(
			String codiceGara, String tipoImpresa, boolean rti)
			throws ApsException;

    /**
     * Estrae l'elenco dei documenti richiesti all'atto dell'iscrizione all'albo
     * elenco operatori economici dipendenti anche dalla tipologia d'impresa
     * 
     * @param codice
     *            codice univoco della gara/lotto/bando d'iscrizione
     * @param tipoImpresa
     *            tipologia d'impresa
	 * @param rti impresa partecipa come rti
     * @return elenco dei documenti richiesti
     * @throws ApsException
     */
    public List<DocumentazioneRichiestaType> getDocumentiRichiestiBandoIscrizione(
	    String codice, String tipoImpresa, boolean rti) throws ApsException;

    /**
     * Estrae l'elenco dei documenti richiesti all'atto del rinnovo iscrizione all'albo
     * elenco operatori economici dipendenti anche dalla tipologia d'impresa.
     * 
     * @param codice
     *            codice univoco della gara/lotto/bando d'iscrizione
     * @param tipoImpresa
     *            tipologia d'impresa
	 * @param rti impresa partecipa come rti
     * @return elenco dei documenti richiesti per il rinnovo
     * @throws ApsException
     */
    public List<DocumentazioneRichiestaType> getDocumentiRichiestiRinnovoIscrizione(
	    String codice, String tipoImpresa, boolean rti) throws ApsException;

    /**
     * Estrae l'elenco dei documenti richiesti di una comunicazione.
     * 
     * @param idPrg
     *            "PA" o "PG"
     * @param idComunicazione
     *            id della comunicazione
	 * @return elenco dei documenti richiesti per la comunicazione
     * @throws ApsException
     */
    public List<DocumentazioneRichiestaType> getDocumentiRichiestiComunicazione(
	    String idApplicativo, long idComunicazione) throws ApsException;

    /**
     * Estrae lo stato di un'eventuale iscrizione all'elenco fornitori in input
     * per l'impresa
     * 
     * @param username
     *            username dell'impresa
     * @param codice
     *            codice univoco del bando d'iscrizione
     * @return <ul>
     *         <li>null = l'impresa non risulta iscritta</li>
     *         <li>1 = l'impresa ha un'iscrizione in corso</li>
     *         <li>2 = l'impresa ha presentato la domanda d'iscrizione</li>
     *         </ul>
     * @throws ApsException
     */
    public Integer getStatoIscrizioneABandoIscrizione(String username,
	    String codice) throws ApsException;

    /**
     * Estrae l'elenco delle abilitazioni di un'impresa ad un bando di gara
     * 
     * @param username
     *            username dell'impresa
     * @param codice
     *            codice univoco del bando di gara
     * @return abilitazioni alla gara
     * @throws ApsException
     */
    public AbilitazioniGaraType getAbilitazioniGara(String username,
	    String codice) throws ApsException;

    /**
     * Restituisce la lista dei bandi abilitati all'inserimento di richieste di
     * invio offerta per l'impresa in input
     * 
     * @param username
     *            username dell'impresa
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
     * 
     * @return lista bandi abilitati all'inserimento di richieste di invio
     *         offerta
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public SearchResult<GaraType> getElencoBandiPerRichiesteOfferta(BandiSearchBean search) throws ApsException;

    /**
     * Restituisce la lista dei bandi abilitati all'inserimento di richieste di
     * invio documentazione per la comprova dei requisiti per l'impresa in input
     * 
     * @param username
     *            username dell'impresa
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
            
     * 
     * @return lista bandi abilitati all'inserimento di richieste di invio
     *         documentazione
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    SearchResult<GaraType> getElencoBandiPerRichiesteCheckDocumentazione(BandiSearchBean search) throws ApsException;

    /**
     * Restituisce la lista dei bandi abilitati ad aste elettroniche 
     * per l'impresa in input
     * 
     * @param username
     *            username dell'impresa
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara            
     * 
     * @return lista bandi abilitati all'inserimento di richieste di invio
     *         offerta
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    SearchResult<GaraType> getElencoBandiPerAsteInCorso(BandiSearchBean search) throws ApsException;
    
    /**
     * Restituisce la lista dei bandi abilitati ad aste elettroniche 
     * per l'impresa in input
     * 
     * @param username
     *            username dell'impresa
	 * @param stazioneAppaltante
	 *            stazione appaltante
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param tipoAppalto
	 *            tipo di appalto (lavori/forniture/servizi)
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara            
     * 
     * @return lista bandi abilitati all'inserimento di richieste di invio
     *         offerta
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    int countBandiPerAsteInCorso(BandiSearchBean search) throws ApsException;

    /**
     * Restituisce il tipo di partecipazione dell'impresa al bando di gara
     * 
     * @param username
     *            username dell'impresa
     * @param codice
     *            codice della gara
     * @param progOfferta
     * 			  progressivo offerta
     * 
     * @return tipologia di partecipazione
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public TipoPartecipazioneType getTipoPartecipazioneImpresa(String username,
	    String codice, String progOfferta) throws ApsException;

    /**
     * Restituisce l'elenco delle stazioni appaltanti associate ad un bando
     * d'iscrizione
     * 
     * @param username
     *            username dell'impresa
     * @param codice
     *            codice del bando d'iscrizione
     * 
     * @return lista delle stazioni appaltanti
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public LinkedHashMap<String, String> getElencoStazioniAppaltantiPerIscrizione(
	    String codice) throws ApsException;

    /**
     * Restituisce l'elenco delle categorie per cui un'impresa risulta iscritta
     * ad un albo.
     * 
     * @param username
     *            username dell'impresa
     * @param codice
     *            codice del bando d'iscrizione
     * 
     * @return elenco delle categorie abilitate dall'impresa per l'albo
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public List<CategoriaImpresaType> getCategorieImpresaPerIscrizione(
	    String username, String codice) throws ApsException;

    /**
     * Restituisce i dati di is iscrizione percui un'impresa risulta iscritta
     * ad un albo.
     * 
     * @param username
     *            username dell'impresa
     * @param codice
     *            codice del bando d'iscrizione
     * 
     * @return dati dell'impresa per iscrizione all'albo
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
    public ImpresaIscrizioneType getImpresaIscrizione(
	    String username, String codice) throws ApsException;

    /**
     * Verifica se un'impresa &egrave; gi&agrave; registrata nel backoffice.
     * 
     * @param codiceFiscale
     *            codice fiscale dell'impresa
     * @param partitaIva
     *            partita iva dell'impresa
     * @param isGruppoIva
     *            true se azienda italiana facente parte di un gruppo iva, false altrimenti
     * @return true se esiste un'impresa che utilizza il portale ed &egrave;
     *         identificata dal codice fiscale o dalla partita iva in input
     * @throws ApsException
     */
    public Boolean isImpresaRegistrata(String codiceFiscale, String partitaIva, boolean isGruppoIva)
	    throws ApsException;

    /**
     * Restituisce lo username di un'impresa gi&agrave; registrata nel backoffice.
     * 
     * @param codiceFiscale
     *            codice fiscale dell'impresa
     * @param partitaIva
     *            partita iva dell'impresa
     * @param email
     *            email dell'impresa
     * @param pec
     *            pec dell'impresa
     * @return true se esiste un'impresa che utilizza il portale ed &egrave;
     *         identificata dal codice fiscale o dalla partita iva in input
     * @throws ApsException
     */
    public String getImpresaRegistrata(String codiceFiscale, String partitaIva,
    		String email, String pec)
	    throws ApsException;

	/**
	 * Restituisce la lista dei bandi per procedure in aggiudicazione o concluse
	 * dell'impresa filtrando per i parametri di input.
	 * 
	 * @param username
	 *            username dell'impresa
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * 
	 * @return lista bandi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	SearchResult<GaraType> searchBandiPerProcInAggiudicazione(BandiSearchBean search) throws ApsException;

	/**
	 * Restituisce la lista dei bandi per procedure in aggiudicazione o concluse
	 * dell'impresa filtrando per i parametri di input.
	 * 
	 * @param username
	 *            username dell'impresa
	 * @param oggetto
	 *            oggetto del bando di gara
	 * @param cig
	 *            codice cig del bando di gara
	 * @param dataPubblicazioneDa
	 *            data pubblicazione a partire da
	 * @param dataPubblicazioneA
	 *            data pubblicazione fino a
	 * 
	 * @return lista bandi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	SearchResult<GaraType> searchBandiConEsito(BandiSearchBean search) throws ApsException;

	/**
	 * Fornisce l'indicazione se l'impresa in input risulta abilitata al rinnovo
	 * nel bando indicato.
	 * 
	 * @param codice
	 *            codice del bando d'iscrizione
	 * @param username
	 *            username dell'impresa
	 * @return true se l'impresa e' autorizzata ad inviare un rinnovo, false
	 *         altrimenti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public Boolean isImpresaAbilitataRinnovo(String codice, String username)
			throws ApsException;

	/**
	 * Estrae la lista delle voci di dettaglio per l'inserimento dei prezzi
	 * unitari nel caso di offerta telematica.
	 * 
	 * @param codice
	 *            codice del bando di gara
	 * @return lista eventuale delle lavorazioni o forniture per l'inserimento
	 *         dei prezzi unitari
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public List<VoceDettaglioOffertaType> getVociDettaglioOfferta(String codice)
			throws ApsException;

	/**
	 * Estrae la lista delle voci di dettaglio presenti in una lista lavorazioni
	 * che non sono oggetto di ribasso nell'offerta telematica.
	 * 
	 * @param codice
	 *            codice del bando di gara
	 * @return lista eventuale delle lavorazioni o forniture non soggette a
	 *         ribasso dei prezzi unitari
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public List<VoceDettaglioOffertaType> getVociDettaglioOffertaNoRibasso(
			String codice) throws ApsException;
	
	/**
	 * Estrae la lista eventuali delle voci aggiuntive da dettagliare per ogni
	 * lavorazione e fornitura di un'offerta telematica.
	 * 
	 * @param codice
	 *            codice del bando di gara
	 * @return lista eventuale dei campi aggiuntivi da inserire nel dettaglio
	 *         prezzi unitari
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public List<AttributoAggiuntivoOffertaType> getAttributiAggiuntiviOfferta(
			String codice) throws ApsException;

	/**
	 * Ritorna i dati di dettaglio di una stazione appaltante.
	 * 
	 * @param codice
	 *            codice univoco della stazione appaltante
	 * @return dati di dettaglio
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */	
	public DettaglioStazioneAppaltanteType getDettaglioStazioneAppaltante(
			String codice) throws ApsException;

	/**
	 * Ritorna l'elenco delle stazioni appaltanti
	 * 
	 * @return elenco delle stazioni appaltanti 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */	
	public List<DettaglioStazioneAppaltanteType> getStazioniAppaltanti() throws ApsException;
		
	/**
	 * Ritorna il numero massimo di decimali utilizzabili per esprimere la
	 * percentuale di ribasso o aumento in un'offerta economica.
	 * 
	 * @return
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public Long getNumeroDecimaliRibasso() throws ApsException;

	/**
	 * Ritorna l'elenco delle mandanti della RTI relativa alla procedura ed alla
	 * mandataria in input.
	 * 
	 * @param codiceProcedura
	 *            codice della gara, oppure del bando iscrizione o del catalogo
	 *            elettronico
	 * @param username
	 *            username dell'impresa mandataria
	 * @return lista delle mandanti, vuota se non presente
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	public List<MandanteRTIType> getMandantiRTI(String codiceProcedura,
			String username, String progOfferta) throws ApsException;
	
	/**
	 * Estrae i dati attribuiti alla creazione del fascicolo.
	 * @param codiceProcedura
	 *            codice della gara, oppure del bando iscrizione o del catalogo
	 *            elettronico
	 * @return eventuali dati del fascicolo, se presenti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	FascicoloProtocolloType getFascicoloProtocollo(String codiceProcedura) throws ApsException;
	
	/**
	 * Ritorna il genere di una procedura.
	 * 
	 * @param codice
	 *            codice procedura
	 * @return <ul>
	 *         <li>1 = Gara a lotti distinti</li>
	 *         <li>2 = Gara a lotto unico</li>
	 *         <li>3 = Gara a lotti con offerta unica (plico unico)</li>
	 *         <li>4 = ODA</li>
	 *         <li>10 = Elenco</li>
	 *         <li>20 = Catalogo elettronico</li>
	 *         <li>100 = Lotto di gara a lotti distinti</li>
	 *         <li>200 = Lotto di gara di offerta unica</li>
	 *         </ul>
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
	Long getGenere(String codice) throws ApsException;

	/**
	 * Estrae la chiave pubblica di cifratura busta se prevista
	 * @param codice codice gara
	 * @param tipoBusta tipologia di busta
	 * @return chiave pubblica
	 */
	byte[] getChiavePubblica(String codice, String tipoBusta) throws ApsException;

	/**
	 * Testa se le consorziate esecutrici di un consorzio sono gi&agrave;
	 * presenti in backoffice.
	 * 
	 * @param username
	 *            identificativo impresa
	 * @param codiceGara codice gara
	 * @return true se le consorziate risultano caricate in anagrafica, false
	 *         altrimenti
	 */
	boolean isConsorziateEsecutriciPresenti(String username, String codiceGara) throws ApsException;
	
	/**
	 * Ritorna l'elenco di invii effettuati dall'utente in input su una
	 * determinata procedura (elenco, catalogo), filtrando per tipologia di
	 * comunicazione.
	 * 
	 * @param username
	 *            identificativo impresa
	 * @param codice
	 *            codice procedura (elenco, catalogo)
	 * @param tipiComunicazione
	 *            lista di tipi di comunicazione da reperire
	 * @return lista di comunicazioni inviate
	 */
	List<InvioType> getElencoInvii(String username, String codice,
			String[] tipiComunicazione) throws ApsException;

	/**
	 * 
	 */
	DocumentoAllegatoType[] getAttiDocumentiBandoGara(String codiceGara) throws ApsException;
	
    /**
     * Restituisce gli allegati di un'asta o di un lotto 
     * 
     * @param codiceGara
     *            codice univoco della gara
     * @param codiceLotto
     *            codice univoco del lotto 
     * @return documenti dell'asta o del lotto 
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
	public DocumentoAllegatoType[] getDocumentiInvitoAsta(String codiceGara, 
			String codiceLotto) throws ApsException;

	/**
     * Restituisce se un'impresa è invitata/ammesso ad una data asta  
     * 
     * @param codice
     *            codice/codiceLotto univoco dell'asta
     * @param username
     *            codice della ditta 
     *            
     * @return true se l'utente è invitato/ammesso alla'asta 
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
	public Boolean isInvitataAsta(String codice, String username) throws ApsException;

	/**
	 * Restituisce, a partire dalle comunicazioni di aggiornamento iscrizione  
	 * posticipate, la comunicazione relativa ad un utente ed una gara/catalogo
	 * 
     * @param tokenRichiedente
     *            identificativo impresa    
	 * @param codiceGara
     *            codice/codiceLotto univoco della gara
	 */
	public Long checkAggiornamentoIscrizionePosticipata(
    	    String tokenRichiedente, String codiceGara) throws ApsException;

	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo ai soccorsi istruttori
	 * 
	 * @param codicegara
     *            codice univoco della gara
     * @param codiceLotto
     *  		  codiceLotto codice del lotto
     * @param fasegara
     *            fase di gara
     * @param tokenRichiedente
     *            identificativo impresa
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraSoccorsiElencoOperatori(
			String codiceGara, String codiceLotto, String faseGara, String tokenRichiedente) throws ApsException;

	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo alla documentazione amministrativa
	 * 
	 * @param codicegara
     *            codice/codiceLotto univoco della gara
     * @param tokenRichiedente
     *            identificativo impresa    
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraDocAmmElencoOperatori(
			String codiceGara, String tokenRichiedente) throws ApsException;
	
	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo alla valutazione tecnica 
	 * 
	 * @param codice
     *            codice della gara
     * @param codiceLotto
     *            codiceLotto del lotto
     * @param tokenRichiedente
     *            identificativo impresa    
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraValTecElencoOperatori(
			String codice, String codiceLotto, String tokenRichiedente) throws ApsException;
	
	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo all'offerta economica  
	 * 
	 * @param codice
     *            codice della gara
     * @param codiceLotto
     *            codiceLotto del lotto
     * @param tokenRichiedente
     *            identificativo impresa    
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraOffEcoElencoOperatori(
			String codice, String codiceLotto, String tokenRichiedente) throws ApsException;

	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo alla valutazione tecnica 
	 * 
	 * @param codice
     *            codice della gara
     * @param tokenRichiedente
     *            identificativo impresa    
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraValTecElencoOperatoriLotto(
			String codice, String tokenRichiedente) throws ApsException;
	
	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo all'offerta economica  
	 * 
	 * @param codice
     *            codice della gara
     * @param tokenRichiedente
     *            identificativo impresa    
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraOffEcoElencoOperatoriLotto(
			String codice, String tokenRichiedente) throws ApsException;

	/**
	 * Restituisce, per l'espletamento di gara, l'elenco degli operatori economici
	 * che hanno inviato offerta per una gara (tokenRichiedente = null)
	 * o un singolo operatore economico (tokenRichiedente != null)
	 * relativo all'offerta economica  
	 * 
	 * @param codicegara
     *            codice/codiceLotto univoco della gara
     * @param tokenRichiedente
     *            identificativo impresa    
	 */
	public List<EspletGaraOperatoreType> getEspletamentoGaraGraduatoriaElencoOperatori(
			String codiceGara, String tokenRichiedente) throws ApsException;

	/**
	 * Restituisce, per l'espletamento di gara, la fase di una gara
	 * 
	 * @param codice
     *            codice/codiceLotto univoco della gara
	 */
	public Long getFaseGara(String codice) throws ApsException;

    /**
	 * Restituisce le comunicazioni pubbliche degli ultimi 60gg
	 * 
	 * @return lista paginata delle comunicazioni inviate
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
    public SearchResult<ComunicazioneType> getNews(
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;

//    /**
//	 * Restituisce l'elenco dei soggetti aderenti relativi ad bando/esito/avviso
//	 * 
//	 * @param codice
//	 * 			codice/codice lotto di bando/esito/avviso
//	 * @return lista dei soggetti aderenti
//	 * 
//	 * @throws ApsException
//	 *             In caso di errori in accesso al servizio web.
//	 */
//    List<SoggettoAderenteType> getSoggettiAderenti(
//    		String codice) throws ApsException;

   /**
	 * Restituisce l'elenco degli operatori abilitati a elenco
	 * 
	 * @param codice
	 * 			codice/codice lotto 
	 * @return lista degli operatori iscritti 
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
   public List<OperatoreIscrittoType> getOperatoriIscritti(
   			String codice) throws ApsException;

   /**
	 * Restituisce l'elenco delle categorie relative agli operatori abilitati a elenco
	 * 
	 * @param codice
	 * 			codice/codice lotto 
	 * @return lista degli operatori iscritti 
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */   
   public List<CategoriaOperatoreIscrittoType> getElencoCategorieOperatoriIscritti(
    		String codice) throws ApsException;

   /**
	 * Restituisce l'elenco dei documenti nulli relativi ad una gara o alla busta di una gara 
	 * 
	 * @param codiceGara
	 * 			codice della gara
	 * @param username (opzionale)
	 * 			nome utente/ditta
	 * @param tipoBusta (opzionale)
	 * 			tipo della busta (FS11A, FS11B, ...)  
	 * @return lista dei documenti con contenuto nullo 
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */   
   public List<DocumentoAllegatoLotto> checkDocumentiNulli(
		   String codiceGara, String username, String tipoBusta) throws ApsException;

   /**
	 * Restituisce l'elenco dei documenti relativi ad una gara o alla busta di una gara 
	 * che hanno la dimensione originale diversa da quella memorizzata dal db  
	 * 
	 * @param codiceGara
	 * 			codice della gara
	 * @param username (opzionale)
	 * 			nome utente/ditta
	 * @param tipoBusta (opzionale)
	 * 			tipo della busta (FS11A, FS11B, ...)  
	 * @return lista dei documenti con contenuto nullo 
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */   
  public List<DocumentoAllegatoLotto> checkDimensioneDocumenti(
		   String codiceGara, String username, String tipoBusta) throws ApsException;

   /**
 	 * Restituisce l'elenco delle delibere a contrarre degli ultimi "PUBBLICAZIONE_NUM_ANNI" anni
	 *
	 * @param username
	 * @param oggetto
	 * @param tipoAppalto
	 * @param cig
	 * @param datPubblicazione
	 * @param sommaUrgenza
	 * @param indicePrimoRecord
	 * @param maxNumRecord
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web. 
	 */
  public SearchResult<DeliberaType> getDelibere(
			String stazioneAppaltante, 
			String oggetto, 
			String tipoAppalto,
			String cig,
			Date dataPubblicazioneDa, 
			Date dataPubblicazioneA,
			Boolean sommaUrgenza,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;
  
   /**
	 * Restituisce l'elenco delle somma urgenza
	 *
	 * @param username
	 * @param oggetto
	 * @param tipoAppalto
	 * @param cig
	 * @param datPubblicazione
	 * @param sommaUrgenza
	 * @param indicePrimoRecord
	 * @param maxNumRecord
	 * 
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web. 
	 */
  public SearchResult<SommaUrgenzaType> getElencoSommaUrgenza(
			String stazioneAppaltante, 
			String oggetto, 
			String tipoAppalto,
			String cig,
			Date dataPubblicazioneDa, 
			Date dataPubblicazioneA,
			int indicePrimoRecord,
			int maxNumRecord) throws ApsException;
  
  /**
   * recupera i parametri di configurazione del WSDM da BO
   */
  public List<WSDMConfigType> getWSDMConfig(
			String stazioneAppaltante) throws ApsException;

  /**
   * recupera il codice impresa diun dato usename da BO
   */
  public String getIdImpresa(String username) throws ApsException;


	/**
	 * Ritorna l'elenco dei bandi di gara scaduti
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
	 * @param esito
	 * 
	 * @param altrisoggetti
	 * 
	 * @param sommaUrgenza
	 * 
	 * @param garaPrivatistica
	 * 			  2=acquisto, 1=vendita 
	 *             
	 * @return lista bandi scaduti
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
  SearchResult<GaraType> getElencoBandiScadutiPrivatistiche(BandiSearchBean search) throws ApsException;

    /**
     * Restituisce la lista dei bandi abilitati all'inserimento di richieste di
     * invio offerta per l'impresa in input
     * 
     * @param username
     *            username dell'impresa
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
	 * @param altrisoggetti
	 * 
	 * @param sommaUrgenza
	 * 
	 * @param garaPrivatistica
	 * 			  2=acquisto, 1=vendita 
	 *  
     * @return lista bandi abilitati all'inserimento di richieste di invio
     *         offerta
     * @throws ApsException
     *             In caso di errori in accesso al servizio web.
     */
  SearchResult<GaraType> getElencoBandiPerRichiesteOffertaPrivatistiche(BandiSearchBean search) throws ApsException;

  /**
	 * Ritorna l'elenco dei bandi di gara in corso di validit&agrave;.
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
	 * @param dataScadenzaDa
	 *            data scadenza a partire da
	 * @param dataScadenzaA
	 *            data scadenza fino a
	 * @param proceduraTelematica
	 *            procedura telematica
	 * @param stato
	 *            stato della gara
	 * @param altrisoggetti
	 * 
	 * @param sommaUrgenza
	 * 
	 * @param garaPrivatistica
	 * 			  2=acquisto, 1=vendita 
             
	 * @return lista bandi
	 * @throws ApsException
	 *             In caso di errori in accesso al servizio web.
	 */
  SearchResult<GaraType> getElencoBandiPrivatistiche(BandiSearchBean search) throws ApsException;

  SearchResult<GaraType> searchBandiPerProcInAggiudicazionePrivatistiche(BandiSearchBean search) throws ApsException;

  public List<InvitoGaraType> getElencoInvitiGara(
			String username,
			String codiceGara) throws ApsException;

  /**
   * Restituisce il numero di ordine dell'invito ad una gara negoziata
   * 
   * @param username
   * 			username della ditta
   * @param codice
   *            codice gara/lotto del bando 
   */
  public Long getNumeroOrdineInvito(String username, String codice) throws ApsException;
  
  public List<QuestionarioType> getQuestionari(
			String codice, 
			String tipologia, 
			String busta) throws ApsException;

  public List<ParametroQuestionarioType> getParametriQuestionario() throws ApsException;
	public VendorRatingType getVendorRating(String username, Date date) throws ApsException;

}
