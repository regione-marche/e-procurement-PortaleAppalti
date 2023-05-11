package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.exception.ApsException;


public abstract class BustaGara implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7874436563842346850L;
	
    /**
     * Tipologia di busta 
     */
	public static final int BUSTA_INVIO_OFFERTA  	= 101;	// (solo ad uso locale)	
	public static final int BUSTA_PARTECIPAZIONE  	= 104;	// (solo ad uso locale)
    public static final int BUSTA_AMMINISTRATIVA 	= PortGareSystemConstants.BUSTA_AMMINISTRATIVA;	
    public static final int BUSTA_TECNICA 			= PortGareSystemConstants.BUSTA_TECNICA;
    public static final int BUSTA_ECONOMICA 		= PortGareSystemConstants.BUSTA_ECONOMICA;    
    public static final int BUSTA_PRE_QUALIFICA 	= PortGareSystemConstants.BUSTA_PRE_QUALIFICA;    
    public static final int BUSTA_RIEPILOGO 		= 501;	// (solo ad uso locale)
    public static final int BUSTA_RIEPILOGO_PRE 	= 504;	// (solo ad uso locale)
    
    /**
     * Tipologia delle descrizioni della busta  
     */
    //public static final String BUSTA_PAR 			= "Busta di parteciazopme";
    //public static final String BUSTA_OFF			= "Busta offerta";
    public static final String BUSTA_PRE 			= "Busta di prequalifica";
    public static final String BUSTA_AMM 			= "Busta amministrativa";
    public static final String BUSTA_TEC 			= "Busta tecnica";
    public static final String BUSTA_ECO 			= "Busta economica";    
    public static final String BUSTA_RIE 			= "Busta riepilogativa";
        
    /**
     * inizializazza i parametri relativi alle buste (tipo busta, descrizione tipo busta, tipo comunicazione busta)
     */    
    private static final HashMap<Integer, String[]> parametriBuste = new HashMap<Integer, String[]>();
    {
    	BustaGara.parametriBuste.put(BUSTA_INVIO_OFFERTA,	new String[] { "", ComunicazioneFlusso.RICHIESTA_TIPO_INVIO_OFFERTA_GT } );
    	BustaGara.parametriBuste.put(BUSTA_PARTECIPAZIONE,	new String[] { "", ComunicazioneFlusso.RICHIESTA_TIPO_PARTECIPAZIONE_GT });
    	BustaGara.parametriBuste.put(BUSTA_AMMINISTRATIVA,	new String[] { BUSTA_AMM, ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA });
    	BustaGara.parametriBuste.put(BUSTA_TECNICA, 		new String[] { BUSTA_TEC, ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA });
    	BustaGara.parametriBuste.put(BUSTA_ECONOMICA, 	 	new String[] { BUSTA_ECO, ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_TECNICA });
    	BustaGara.parametriBuste.put(BUSTA_PRE_QUALIFICA,	new String[] { BUSTA_PRE, ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_ECONOMICA });
    	BustaGara.parametriBuste.put(BUSTA_RIEPILOGO, 	 	new String[] { BUSTA_RIE, ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_RIEPILOGO });
    	BustaGara.parametriBuste.put(BUSTA_RIEPILOGO_PRE,	new String[] { BUSTA_RIE, ComunicazioneFlusso.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO });    	
	}

	/**
	 * restituisce la descrizione della busta
	 */
	public static String getDescrizioneBusta(int tipoBusta) {
		String[] parametri = BustaGara.parametriBuste.get(tipoBusta);
		return (parametri != null ? parametri[0] : "");
	}
	
    /**
	 * restituisce il tipo della comunicazione associato alla busta (W_INVCOM.COMTIPO)
	 */
	public static String getTipoComunicazione(int tipoBusta) {
		String[] parametri = BustaGara.parametriBuste.get(tipoBusta);
		return (parametri != null ? parametri[1] : "");
	}


    /**
     * Tipi di criteri di valutazione (tecnica/economica)
     */
    public static final int CRITERIO_TECNICO										= 1;
    public static final int CRITERIO_ECONOMICO 										= 2;
    public static final int CRITERIO_VALUTAZIONE_DATA 								= 1;
    public static final int CRITERIO_VALUTAZIONE_IMPORTO 							= 2;
    public static final int CRITERIO_VALUTAZIONE_LISTA_VALORI 						= 3;
    public static final int CRITERIO_VALUTAZIONE_TESTO 								= 4;
    public static final int CRITERIO_VALUTAZIONE_INTERO 							= 5;
    public static final int CRITERIO_VALUTAZIONE_DECIMALE 							= 6;
	public static final int CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO 			= 50;
	public static final int CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO 			= 51;
	public static final int CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI 	= 52;


	
	/**
	 * attributi 
	 */
    protected GestioneBuste gestioneBuste;
    protected String username;
	protected String codiceGara;
	protected String codiceLotto;
	protected String progressivoOfferta;
	protected int tipoBusta;
	protected ComunicazioneFlusso comunicazioneFlusso;
	protected boolean soloUploadDocumenti;
	protected boolean qForm;

	public GestioneBuste getGestioneBuste() {
		return gestioneBuste;
	}

	public String getUsername() {
		return username;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}
	
	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public String getDescrizioneBusta() {
		return BustaGara.getDescrizioneBusta(this.tipoBusta);
	}
	
	public ComunicazioneFlusso getComunicazioneFlusso() {
		return comunicazioneFlusso;
	}

	public boolean isSoloUploadDocumenti() {
		return soloUploadDocumenti;
	}
	
	public Long getId() {
		return this.comunicazioneFlusso.getId();
	}

	
	
	/**
	 * costruttore
	 */
	public BustaGara(
			GestioneBuste buste,
			String codiceLotto,
			int tipoBusta) 
	{
		this.gestioneBuste = buste;
		if(buste != null) {
			this.username = buste.getUsername();
			this.codiceGara = buste.getCodiceGara();
			this.codiceLotto = (StringUtils.isEmpty(codiceLotto) ? buste.getCodiceGara() : codiceLotto);
			this.progressivoOfferta = buste.getProgressivoOfferta();
		}
		this.tipoBusta = tipoBusta;
		this.soloUploadDocumenti = false;
		if(buste != null && !buste.getDettaglioGara().getDatiGeneraliGara().isOffertaTelematica()) {
			this.soloUploadDocumenti = true;
		}
		this.qForm = false;
		this.comunicazioneFlusso = new ComunicazioneFlusso();
		this.comunicazioneFlusso.setComunicazioniManager(this.gestioneBuste.getComunicazioniManager());		
	}
	
	/**
	 * costruttore
	 */
	public BustaGara() {
		this(null, null, -1);
	}	

	/**
	 * deserializzazione dell'oggetto  
	 * In ambiente cluster con integrazione di Redis le istanze dei manager non possono essere serializzate/deserializzate
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
		GestioneBuste.traceLog("BustaGara.readObject -> deserialization");
		// perform the default de-serialization first
	    is.defaultReadObject();
	    this.comunicazioneFlusso.setComunicazioniManager(this.gestioneBuste.getComunicazioniManager());
	}

	/**
	 * recupera la busta dal servizio
	 * in base ad una lista di stati condizionata e un lotto
	 * 
	 * @throws ApsException 
	 */	
	public boolean get(List<String> stati, boolean findInOrder, String codiceLotto, String progressivoOfferta) throws Throwable {
		GestioneBuste.traceLog("BustaGara.get " + codiceLotto );
		boolean continua = true;
		
		//this.codiceLotto = codiceLotto;  <= da verificare
		if(StringUtils.isNotEmpty(codiceLotto)) {
			this.codiceLotto = codiceLotto;
		}
		
		if(stati == null) {
			// prepara una lista standard di stati { BOZZA, DA PROCESSARE }
			stati = new ArrayList<String>();
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		}
		
		DettaglioComunicazioneType dettCom = this.comunicazioneFlusso.find(
				this.username, 
				(StringUtils.isNotEmpty(this.codiceLotto) ? this.codiceLotto : this.codiceGara),
				progressivoOfferta,
				this.comunicazioneFlusso.getTipoComunicazione(),
				stati,
				findInOrder);

		if(dettCom == null) {
			// se non e' stata trovata la comunicazione 
			// si invalida il dettaglio comunicazione per "resettare" i dati dell'oggetto...
			this.comunicazioneFlusso.reset();
		} else {
			// recupera la comunicazione associata al dettaglio...
			continua = continua && this.comunicazioneFlusso.get(
					dettCom.getApplicativo(), 
					dettCom.getId());
		}
		
		// inizializza l'helper in base ai nuovi dati...
		this.invalidateHelper();
		if(continua) {
			this.initHelper();
		}
		
		return continua;
	}

	/**
	 * recupera la busta dal servizio
	 * in base ad una lista di stati condizionata e un lotto
	 * 
	 * @throws ApsException 
	 */	
	private boolean get(List<String> stati, boolean findInOrder, String codiceLotto) throws Throwable {
		this.progressivoOfferta = (StringUtils.isEmpty(this.progressivoOfferta) ? "1" : this.progressivoOfferta);		
		return this.get(stati, findInOrder, codiceLotto, this.progressivoOfferta);
	}

	/**
	 * recupera la busta dal servizio
	 * in base ad una lista di stati condizionata e un lotto
	 * 
	 * 
	 * @throws ApsException 
	 */	
	public boolean get(List<String> stati, String codiceLotto) throws Throwable {
		return this.get(stati, (stati == null), codiceLotto);
	}

	/**
	 * recupera la busta dal servizio in base ad una lista di stati 
	 * 
	 * @throws ApsException 
	 */	
	public boolean get(List<String> stati) throws Throwable {
		return this.get(stati, (stati == null), null);
	}

	/**
	 * recupera la busta dal servizio
	 * verifica se esiste prima in stato BOZZA (1) e poi in stato DA PROCESSARE (5)
	 * 
	 * @throws ApsException 
	 */	
	public boolean get() throws Throwable {
		List<String> stati = new ArrayList<String>();
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE); 
		return this.get(stati, true, null);
	}

	/**
	 * invia la busta al servizio con la descrizione dell'operazione
	 * 
	 * @throws ApsException 
	 */
	protected boolean send() throws Throwable {
		GestioneBuste.traceLog("BustaGara.send NOT DEFINED");
		return false;
	}

	/**
	 * restituisce il documento xml associato alla busta
	 */
	public abstract XmlObject getBustaDocument() throws XmlException;

	/**
	 * invalida i dati dell'helper associato alla busta  
	 */
	protected /*??? abstract ???*/ void invalidateHelper() throws Throwable {
		// DA RIDEFINIRE NELLE CLASSI FIGLIE !!!
	}

	/**
	 * inizializza un nuovo helper associato alla busta  
	 */
	protected abstract boolean initHelper() throws Throwable;
		
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
	 * @throws ApsException 
	 */
	public DettaglioComunicazioneType find(List<String> stati) throws Throwable {
		return this.comunicazioneFlusso.find(
				this.username, 
				this.codiceGara, 
				this.comunicazioneFlusso.getTipoComunicazione(),
				stati);
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
	 * @param stato
	 * 			stato della comunicazione da reperire
	 * @throws ApsException 
	 */
	public DettaglioComunicazioneType find(String stato) throws Throwable {
		List<String> stati = new ArrayList<String>();
		stati.add(stato);
		return this.find(stati);
	}
	
//	/**
//	 * recupera la busta della sessione (SERVE ???)
//	 */
//	public static BustaGara getFromSession() {
//		GestioneBuste buste = this.gestioneBuste.getFromSession();
//		...	
//		return ???;
//	}
	
	/**
	 * salva la busta in sessione
	 */
	public void putToSession() {
		this.gestioneBuste.putToSession();
	}
	
//	/**
//	 * calcola il CRC associato all'oggetto 
//	 */
//	private String getCrc() {
//		return this.username + "|" + 
//			   this.codiceGara + "|" + 
//			   this.codiceLotto + "|" + 
//			   this.progressivoOfferta + "|" +
//			   this.tipoBusta;
//	}
	
}
