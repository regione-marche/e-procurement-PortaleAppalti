package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;


public class ComunicazioneFlusso implements Serializable {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 4565909867408478612L;
	private final Logger logger = ApsSystemUtils.getLogger();
	
    /**
     * Identificativi del tipo di di applicativo (PA, PG, PL, ...)
     */
    public static final String ID_APPLICATIVO 									= "PA";
    public static final String ID_APPLICATIVO_GARE 								= "PG";
    public static final String ID_APPLICATIVO_LAVORI							= "PL";

    /**
     * Tipo di comunicazione (W_INVCOM)
     * - FS1   richiesta di registrazione al portale
     * - FS2   richiesta di iscrizione
     * - FS3   invio di un rinnovo iscrizione a elenco/catalogo
     * - FS4   richiesta di aggiornamento dati/documenti di una iscrizione
     * - FS5   richiesta di aggiornamento anagrafica
     * - FS6   richiesta di variazione dati anagrafici
     * - FS7   richiesta di variazione prodotti del catalogo
     * - FS8   richiesta di variazione prezzi e scadenze di prodotti del catalogo
     * - FS9   richiesta di partecipazione o meno di un'impresa come RTI
     * - FS10  richiesta di partecipazione di un'impresa con gara telematica
     * - FS10A invio di della busta di prequalifica
     * - FS10R busta prequalifica riepilogativa
     * - FS11  invio di un'offerta di un'impresa con gara telematica
     * - FS11A invio di della busta amministrativa
     * - FS11B invio di della busta tecnica
     * - FS11C invio di della busta economica
     * - FS11R busta riepilogativa
     * - FS12  invio di comunicazioni da parte di un'impresa
     * - FS13  registrazione delle offerte d'asta
     * - FS14  richiesta di rinuncia di partecipazione ad una gara
     */
    public static final String RICHIESTA_REGISTRAZIONE_PORTALE 					= "FS1";
    public static final String RICHIESTA_ISCRIZIONE_ALBO 						= "FS2";
    public static final String RICHIESTA_RINNOVO_ISCRIZIONE 					= "FS3";
    public static final String RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO 			= "FS4";
    public static final String RICHIESTA_AGGIORNAMENTO_ANAGRAFICA 				= "FS5";
    public static final String RICHIESTA_VARIAZIONE_DATI_ANAGRAFICI 			= "FS6";
    public static final String RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO 			= "FS7";
    public static final String RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO 	= "FS8";
    public static final String RICHIESTA_TIPO_PARTECIPAZIONE 					= "FS9";
    public static final String RICHIESTA_TIPO_PARTECIPAZIONE_GT 				= "FS10";
    public static final String RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA 				= "FS10A";
    public static final String RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO 			= "FS10R";
    public static final String RICHIESTA_TIPO_INVIO_OFFERTA_GT 					= "FS11";
    public static final String RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA 				= "FS11A";
    public static final String RICHIESTA_TIPO_BUSTA_TECNICA 					= "FS11B";
    public static final String RICHIESTA_TIPO_BUSTA_ECONOMICA 					= "FS11C";
    public static final String RICHIESTA_TIPO_BUSTA_RIEPILOGO 					= "FS11R";
    public static final String RICHIESTA_INVIO_COMUNICAZIONE 					= "FS12";
    public static final String RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA 				= "FS13";
    public static final String RICHIESTA_RINUNCIA_PARTECIPAZIONE_OFFERTA 		= "FS14";
    
    /**
     * Nome del file xml relativo al tipo di comunicazione allegato alla comunicazione (W_DOCDIG)
     */
    public static final String NOME_FILE_REGISTRAZIONE 							= "dati_reg.xml";
    public static final String NOME_FILE_ISCRIZIONE 							= "dati_iscele.xml";
    public static final String NOME_FILE_AGG_ISCRIZIONE 						= "dati_aggisc.xml";
    public static final String NOME_FILE_AGG_ANAGRAFICA 						= "dati_agganag.xml";
    public static final String NOME_FILE_RINNOVO_ISCRIZIONE 					= "dati_rin.xml";
    public static final String NOME_FILE_DOMANDA_VARIAZIONE 					= "dati_domvar.xml";    
    public static final String NOME_FILE_GESTIONE_PRODOTTI 						= "dati_prodotti.xml";
    public static final String NOME_FILE_PRODOTTI_BOZZE 						= "dati_bozze.xml";
    public static final String NOME_FILE_VARIAZIONE_OFFERTA 					= "dati_varprodotti.xml";
    public static final String NOME_FILE_TIPO_PARTECIPAZIONE 					= "dati_partrti.xml";
    public static final String NOME_FILE_BUSTA 									= "busta.xml";
    public static final String NOME_FILE_BUSTA_RIEPILOGATIVA 					= "riepilogo_buste.xml";


	/**
	 * restituisce il nome del documento XML relativo alla comunicazione (W_INVCOM) 
	 */
	private static String getXmlFilename(String tipoComunicazione) {
		if(RICHIESTA_REGISTRAZIONE_PORTALE.equals(tipoComunicazione)) return NOME_FILE_REGISTRAZIONE;
		else if(RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunicazione)) return NOME_FILE_ISCRIZIONE;
		else if(RICHIESTA_RINNOVO_ISCRIZIONE.equals(tipoComunicazione)) return NOME_FILE_RINNOVO_ISCRIZIONE;
		else if(RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO.equals(tipoComunicazione)) return NOME_FILE_AGG_ISCRIZIONE;
		else if(RICHIESTA_AGGIORNAMENTO_ANAGRAFICA.equals(tipoComunicazione)) return NOME_FILE_AGG_ANAGRAFICA;
		else if(RICHIESTA_VARIAZIONE_DATI_ANAGRAFICI.equals(tipoComunicazione)) return NOME_FILE_DOMANDA_VARIAZIONE;
		else if(RICHIESTA_REGISTRAZIONE_PORTALE.equals(tipoComunicazione)) return NOME_FILE_GESTIONE_PRODOTTI;
		else if(RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO.equals(tipoComunicazione)) return NOME_FILE_PRODOTTI_BOZZE;
		else if(RICHIESTA_TIPO_PARTECIPAZIONE.equals(tipoComunicazione)) return NOME_FILE_TIPO_PARTECIPAZIONE;
		else if(RICHIESTA_TIPO_PARTECIPAZIONE_GT.equals(tipoComunicazione)) return NOME_FILE_TIPO_PARTECIPAZIONE;
		else if(RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA.equals(tipoComunicazione)) return NOME_FILE_BUSTA;
		else if(RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO.equals(tipoComunicazione)) return NOME_FILE_BUSTA_RIEPILOGATIVA;
	    else if(RICHIESTA_TIPO_INVIO_OFFERTA_GT.equals(tipoComunicazione)) return NOME_FILE_TIPO_PARTECIPAZIONE;
		else if(RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA.equals(tipoComunicazione)) return NOME_FILE_BUSTA;
		else if(RICHIESTA_TIPO_BUSTA_TECNICA.equals(tipoComunicazione)) return NOME_FILE_BUSTA;
		else if(RICHIESTA_TIPO_BUSTA_ECONOMICA.equals(tipoComunicazione)) return NOME_FILE_BUSTA;
		else if(RICHIESTA_TIPO_BUSTA_RIEPILOGO.equals(tipoComunicazione)) return NOME_FILE_BUSTA_RIEPILOGATIVA;
	    else if(RICHIESTA_INVIO_COMUNICAZIONE.equals(tipoComunicazione)) return null;					// non esiste un xml per le FS12
		else if(RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA.equals(tipoComunicazione)) return NOME_FILE_BUSTA;
		else if(RICHIESTA_RINUNCIA_PARTECIPAZIONE_OFFERTA.equals(tipoComunicazione)) return null;		// non esiste un xml per le FS14
	    return null;
	}

    
    /**
     * proprieta della classe 
     */
    private transient IComunicazioniManager comunicazioniManager;
	private ComunicazioneType comunicazione;
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public DettaglioComunicazioneType getDettaglioComunicazione() {
		if(this.comunicazione != null) {
			return this.comunicazione.getDettaglioComunicazione();
		}
		return null;
	}
	
	/**
	 * restituisce l'id della comunicazione
	 */
	public long getId() {
		return (this.comunicazione != null &&
				this.comunicazione.getDettaglioComunicazione() != null && 
				this.comunicazione.getDettaglioComunicazione().getId() != null
				? this.comunicazione.getDettaglioComunicazione().getId().longValue()
				: -1L);
	}
	
	/**
	 * restituisce il tipo della comunicazione (FS1, FS2, ...)
	 */
	public String getTipoComunicazione() {
		return (this.comunicazione != null && 
				this.comunicazione.getDettaglioComunicazione() != null
				? this.comunicazione.getDettaglioComunicazione().getTipoComunicazione() 
				: "");
	}
	
	/**
	 * restituisce lo stato della comunicazione
	 */
	public String getStato() {
		return (this.comunicazione != null && 
				this.comunicazione.getDettaglioComunicazione() != null
				? this.comunicazione.getDettaglioComunicazione().getStato() 
				: null);
	}
	
	/**
	 * restituisce la comunicazione
	 */
	public ComunicazioneType getComunicazione() {
		return this.comunicazione;
	}
	
	public void setComunicazione(ComunicazioneType comunicazione) {
		this.comunicazione = comunicazione;
	}
		
	
	/**
	 * costruttore
	 */
	public ComunicazioneFlusso(
			String applicativo, 
			Long id) 
	{
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("ComunicazioneFlusso", t);
		}
		
		this.comunicazione = null;
		
		if(id != null) {
			try {
				this.get(applicativo, id);
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().equals(t);
			}
		}
		
		// se non riesco a trovare la comunicazione, la creo vuota...
		if(this.comunicazione == null) {
			this.comunicazione = new ComunicazioneType();
			DettaglioComunicazioneType dettaglioComunicazione = new DettaglioComunicazioneType();
			this.comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
		}
	}
	
	/**
	 * costruttore
	 */	
	public ComunicazioneFlusso(String tipoComunicazione) {
		this(null, null);
		this.comunicazione.getDettaglioComunicazione().setTipoComunicazione(tipoComunicazione);
	}

	/**
	 * costruttore  
	 */
	public ComunicazioneFlusso() {
		this(null, null);
	}

	/**
	 * resetta il dettaglio comunicazione   
	 */
	public void reset() { 
		DettaglioComunicazioneType dettCom = new DettaglioComunicazioneType();
		dettCom.setApplicativo(null);
		dettCom.setId(null);	
		dettCom.setEntita(null);
		dettCom.setChiave1(this.comunicazione.getDettaglioComunicazione().getChiave1());
		dettCom.setChiave2(this.comunicazione.getDettaglioComunicazione().getChiave2());
		dettCom.setChiave3(this.comunicazione.getDettaglioComunicazione().getChiave3());
		dettCom.setChiave4(this.comunicazione.getDettaglioComunicazione().getChiave4());
		dettCom.setChiave5(this.comunicazione.getDettaglioComunicazione().getChiave5());
		dettCom.setIdOperatore(null);
		dettCom.setMittente(null);
		dettCom.setStato(null);
		dettCom.setOggetto(null);
		dettCom.setTesto(null);
		dettCom.setTipoComunicazione(this.comunicazione.getDettaglioComunicazione().getTipoComunicazione());
		dettCom.setComunicazionePubblica(null);
		dettCom.setDataPubblicazione(null);
		dettCom.setDataInserimento(null);
		dettCom.setDataAggiornamentoStato(null);
		dettCom.setDataLettura(null);
		dettCom.setDataProtocollo(null);
		dettCom.setNumeroProtocollo(null);
		dettCom.setSessionKey(null);
		dettCom.setApplicativoRisposta(null);
		dettCom.setIdRisposta(null);
		dettCom.setModello(null);
		dettCom.setTipoBusta(this.comunicazione.getDettaglioComunicazione().getTipoBusta());
		this.comunicazione.setDettaglioComunicazione(dettCom);
	}
	
	/**
	 * recupera la comunicazione dal servizio
	 * 
	 * @throws ApsException 
	 */	
	public boolean get(
			String applicativo, 
			Long id) throws ApsException 
	{		
		this.comunicazione = this.comunicazioniManager.getComunicazione(applicativo, id);
		if(this.comunicazione != null) {
			// aggiorna eventuali proprieta' collegate alla comunicazione...
			//...
			//...
			//...
		}
		return (this.comunicazione != null);
	}

	/**
	 * invia una comunicazione al servizio
	 *  
	 * @param comunicazione
	 * 			(opzionale) comunicazione da inviare al servizio
	 * @param xmlDocument	
	 * 			(opzionale) documento XML associato al flusso della comunicazione (FS1, FS2, ...)
	 * @param xmlDescription
	 * 			(opzionale) descrizione dell'allegato XML 
	 * @param attachments
	 * 			(opzionale) eventuali documenti da allegare alla comunicazione
	 * 
	 * @throws ApsException 
	 * @throws IOException 
	 */
	private Long send(
			ComunicazioneType comunicazione,
			XmlObject xmlDocument, 
			String xmlDescription,
			List<AllegatoComunicazioneType> attachments) throws ApsException, IOException 
	{
		Long idCom = -1L;
		
		if(comunicazione != null) {
			this.comunicazione = comunicazione;
		}
		
		if(this.comunicazione != null) {
			// prepara l'eventuale allegato XML associato alla comunicazione
			AllegatoComunicazioneType allegatoXml = null;
			if(xmlDocument != null) {
				HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
				suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");
				
				XmlOptions opts = new XmlOptions();
				opts.setSaveSuggestedPrefixes(suggestedPrefixes);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				xmlDocument.save(baos, opts);
				
				String xmlFilename = ComunicazioneFlusso.getXmlFilename(
						this.comunicazione.getDettaglioComunicazione().getTipoComunicazione());
			
				allegatoXml = new AllegatoComunicazioneType();
				allegatoXml.setTipo(CommonSystemConstants.TIPO_ALLEGATO_XML);	// W_DOCDIG.DIGTIPDOC
				allegatoXml.setNomeFile(xmlFilename);							// W_DOCDIG.DIGNOMDOC
				allegatoXml.setDescrizione(xmlDescription);						// W_DOCDIG.DIGDESDOC
				allegatoXml.setFile(baos.toString("UTF-8").getBytes());			// W_DOCDIG.DIGOGG
			}
				
			// prepara l'eventuale allegato (xml + documenti) della communicazione...
			if(allegatoXml != null || attachments != null) {
				int n = (allegatoXml != null ? 1 : 0) + 
					    (attachments != null ? attachments.size() : 0);
				AllegatoComunicazioneType[] allegati = new AllegatoComunicazioneType[n];
				int j = 0;
				
				// allega il documento XML
				if(allegatoXml != null) {
					allegati[j++] = allegatoXml;
				}
					
				// allega eventuali documenti 
				if(attachments != null) {
					for(int i = 0; i < attachments.size(); i++) {
						allegati[j++] = attachments.get(i);
					}
				}
				logger.debug("Imposto allegati alla comunicazione");
				this.comunicazione.setAllegato(allegati);
			}
			
			// invia la comunicazione al servizio e recupera l'id della comunicazione inviata
			if(this.getId() <= 0) {
				// NB: correggi "id" se è stato impostato come <= 0 per indicare una nuova comunicazione...
				this.comunicazione.getDettaglioComunicazione().setId(null);
			}
			idCom = this.comunicazioniManager.sendComunicazione(this.comunicazione);
			this.comunicazione.getDettaglioComunicazione().setId(idCom);
		}
		
		return idCom;
	}

	/**
	 * invia una comunicazione al servizio
	 */
	public Long send(
			XmlObject xmlDocument, 
			String xmlDescription,
			List<AllegatoComunicazioneType> attachments) throws ApsException, IOException 
	{		
		return this.send(
				this.comunicazione, 
				xmlDocument, 
				xmlDescription, 
				attachments);
	}
	
	/**
	 * invia una comunicazione al servizio
	 */
	public Long send(List<AllegatoComunicazioneType> attachments) throws ApsException, IOException {
		return this.send(
				this.comunicazione, 
				null, 
				null, 
				attachments);
	}
	
	/**
	 * invia una comunicazione al servizio
	 */
	public Long send() throws ApsException, IOException {
		return this.send(
				this.comunicazione, 
				null, 
				null, 
				null);
	}
	
	/**
	 * recupera una comunicazione in base a utente, codice, 
	 * progressivo offerta, tipo comunicazione e uno stato
	 * 
  	 * @param username
	 * 			username associato alla comunicazione
	 * @param codice
	 * 			codice gara, codice lotto di gara, codice elenco iscrizione, codice catalogo, ...
	 * @param progOfferta
	 * 			(solo per gare) progressivo dell'offerta di gara 
	 * @param tipoComunicazione
	 * 			tipo di comunicazione/flusso (FS1, FS2, ...)
	 * @param stato
	 * 			stato della comunicazione 
	 * 
	 * @throws ApsException 
	 */	
	public DettaglioComunicazioneType find(
			String username, 
			String codice,
			String progOfferta,
			String tipoComunicazione,
			String statoComunicazione) throws ApsException
	{		
		DettaglioComunicazioneType dettaglio = null;
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setChiave3(progOfferta);
		criteriRicerca.setStato(statoComunicazione);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		if (!comunicazioni.isEmpty()) {
			if (comunicazioni.size() > 1) {
				throw new ApsException("retrieveComunicazione.notUnique");
			}
			dettaglio = comunicazioni.get(0);
		}
		return dettaglio;
	}	
	
	/**
 	 * recupera una comunicazione in base a utente, codice, 
	 * progressivo offerta, tipo comunicazione ed una lista di stati 
	 * 
	 * @param username
	 * 			username associato alla comunicazione
	 * @param codice
	 * 			codice gara, codice lotto di gara, codice elenco iscrizione, codice catalogo, ...
	 * @param progOfferta
	 * 			(solo per gare) progressivo dell'offerta di gara 
	 * @param tipoComunicazione
	 * 			tipo di comunicazione/flusso (FS1, FS2, ...)
	 * @param stati
	 * 			elenco di stati della comunicazione
	 * @param findInOrder
	 * 			indica se la ricerca della comunicazione segue l'ordine definito dagli "stati";
	 * 			se True allora la comunicazione viene cercata in base all'ordine degli "stati"   
	 * @throws ApsException 
	 */	
	public DettaglioComunicazioneType find(
			String username, 
			String codice, 
			String progOfferta, 
			String tipoComunicazione,
			List<String> stati,
			boolean findInOrder) throws ApsException 
	{
		String x  = "";
		if(stati != null) for(String s : stati) x += s + (findInOrder ? ">" : " ");	// info di DEBUG
		
		GestioneBuste.traceLog("ComunicazioneFlusso.find " + x);
		
		DettaglioComunicazioneType dettaglio = null;
		
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setChiave3(progOfferta);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteriRicerca);		
		if (!comunicazioni.isEmpty()) {
			if(findInOrder) {
				// utilizza la lista degli stati per cercare in modo condizionato 
				// in base all'ordine degli stati nella lista
				// iniziando dal primo e proseguendo con i successivi
				// fino a che non si trova un dettaglio comunicazione
				// cerca la comunicazione per stato in base all'ordine di priorita della lista stati
				for(String stato : stati) {
					long maxId = -1;
					for (DettaglioComunicazioneType comunicazione : comunicazioni) {
						if(stato.equals(comunicazione.getStato()) && comunicazione.getId().longValue() > maxId) {
							maxId = comunicazione.getId().longValue();
							dettaglio = comunicazione;
						}
					}
					if(dettaglio != null) {
						break;
					}
				}
			} else {
				long maxId = -1;
				for (DettaglioComunicazioneType comunicazione : comunicazioni) {
					if(stati.contains(comunicazione.getStato()) && comunicazione.getId().longValue() > maxId) {
						maxId = comunicazione.getId().longValue();
						dettaglio = comunicazione;
					}
				}
			}
		}
		return dettaglio;
	}

	/**
 	 * recupera una comunicazione in base a utente, codice, 
	 * progressivo offerta, tipo comunicazione ed una lista di stati 
	 * 
	 * @param username
	 * 			username associato alla comunicazione
	 * @param codice
	 * 			codice gara, codice lotto di gara, codice elenco iscrizione, codice catalogo, ...
	 * @param progOfferta
	 * 			(solo per gare) progressivo dell'offerta di gara 
	 * @param tipoComunicazione
	 * 			tipo di comunicazione/flusso (FS1, FS2, ...)
	 * @param stati
	 * 			elenco di stati della comunicazione
	 * 
	 * @throws ApsException 
	 */	
	public DettaglioComunicazioneType find(
			String username, 
			String codice, 
			String progOfferta, 
			String tipoComunicazione,
			List<String> stati) throws ApsException 
	{
		return this.find(username, codice, progOfferta, tipoComunicazione, stati, false);
	}
	
	/**
	 * recupera la comunicazione dal servizio in base a dei filtri
	 * 
	 * @param username
	 * 			username associato alla comunicazione
	 * @param codice
	 * 			codice gara, codice elenco iscrizione, codice catalogo, ...
	 * @param tipoComunicazione
	 * 			tipo di comunicazione/flusso (FS1, FS2, ...)
	 * @param stati
	 * 			elenco degli stati della comunicazione da reperire
	 */
	public DettaglioComunicazioneType find(
			String username, 
			String codice, 
			String tipoComunicazione,
			List<String> stati) throws ApsException 
	{
		return this.find(username, codice, null, tipoComunicazione, stati);
	}
	
	/**
	 * elimina la comunicazione dal servizio
	 * 
	 * @throws ApsException 
	 */	
		
	public boolean delete(Long idCom) throws ApsException {
		this.comunicazioniManager.deleteComunicazione(ID_APPLICATIVO, idCom);
		return true;
	}

	/**
	 * estrai il documento XML associato alla comunicazione
	 */
	public static String getXmlDoc(ComunicazioneType comunicazione) {
		long id = -1;
		if(comunicazione != null && 
		   comunicazione.getDettaglioComunicazione() != null && 
		   comunicazione.getDettaglioComunicazione().getId() != null) 
		{	
			id = comunicazione.getDettaglioComunicazione().getId();
		}
		
		String xmlNomeFile = ComunicazioneFlusso.getXmlFilename(comunicazione.getDettaglioComunicazione().getTipoComunicazione());		
		String xml = null;
		if(id > 0) {
			int i = 0;
			while (comunicazione.getAllegato() != null && i < comunicazione.getAllegato().length) {
				if (xmlNomeFile.equals(comunicazione.getAllegato()[i].getNomeFile())) {
					xml = new String(comunicazione.getAllegato()[i].getFile());
					break;
				}
				i++;
			}
		}
		return xml;
	}

	/**
	 * estrai il documento XML associato alla comunicazione
	 */
	public String getXmlDoc() { 
		return ComunicazioneFlusso.getXmlDoc(this.comunicazione);
	}
	
}
