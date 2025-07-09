package it.maggioli.eldasoft.plugins.ppcommon.aps.system;

/**
 * Interfaccia con le principali costanti della parte comune.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public interface CommonSystemConstants {


    public static final String STIPULE_MANAGER = "WSStipule";

	/**
	 * Codice del sender da utilizzare in chiamata al plugin jpmail per l'invio mail.
	 */
	public static final String SENDER_CODE = "PORTALICE1";

	/**
     * Nome del bean proxy del servizio web per la gestione di operazioni
     * generali.
     */
    public static final String WS_OPERAZIONI_GENERALI = "WSOperazioniGenerali";

    /**
     * Nome del servizio che gestisce la lettura e l'invio di comunicazioni al backoffice.
     */
    public static final String COMUNICAZIONI_MANAGER = "ComunicazioniManager";

    /**
     * Nome del servizio che gestisce la lettura dell'ora ufficiale.
     */
    public static final String NTP_MANAGER = "NtpManager";

    /**
     * Nome del servizio che gestisce la lettura delle personalizzazioni per i clienti.
     */
    public static final String CUSTOM_CONFIG_MANAGER = "CustomConfigManager";

    /**
     * Nome del servizio che gestisce la lettura delle configurazioni applicative della verticalizzazione.
     */
    public static final String APP_PARAM_MANAGER = "AppParamManager";

    /**
     * Nome del bean contenente l'oggetto di cache delle codifiche ricevute dai
     * servizi, in modo da richiederli una sola volta
     */
    public static final String CACHE_CODIFICHE = "CodificheManager";

    /**
     * Nome del result custom definito per gestire la pagina di errore relativa
     * alla verticalizzazione sviluppata sul portale
     */
    public static final String PORTAL_ERROR = "portalError";

    /**
     * Identificativo della lista settata nelle azioni per contenere l'elenco
     * delle stazioni appaltanti
     */
    public static final String LISTA_STAZIONI_APPALTANTI = "stazioniAppaltanti";

    /**
     * Identificativo di applicativo per le comunicazioni verso il backoffice e relativi documenti.
     */
    public static final String ID_APPLICATIVO = "PA";

    /**
     * Identificativo di applicativo per comunicazioni da e documenti nel backoffice.
     */
    public static final String ID_APPLICATIVO_GARE = "PG";
    
    /**
     * Stato della comunicazione a bozza
     */
    public static final String STATO_COMUNICAZIONE_BOZZA = "1";

    /**
     * Stato della comunicazione a inviata.
     */
    public static final String STATO_COMUNICAZIONE_INVIATA = "3";

    /**
     * Stato della comunicazione a da processare
     */
    public static final String STATO_COMUNICAZIONE_DA_PROCESSARE = "5";

    /**
     * Stato della comunicazione a processata con successo
     */
    public static final String STATO_COMUNICAZIONE_PROCESSATA = "6";
		
	/**
     * Stato della comunicazione a processata con errore
     */
    public static final String STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE = "7";

	/**
     * Stato della comunicazione scartata
     */
    public static final String STATO_COMUNICAZIONE_SCARTATA = "8";

	/**
     * Stato della comunicazione a processata con errore
     */
    public static final String STATO_COMUNICAZIONE_DA_PROTOCOLLARE = "9";

    /**
     * Stato della comunicazione a in attesa di verifica
     */
    public static final String STATO_COMUNICAZIONE_ATTESA_VERIFICA = "18";
    
    /**
     * Stato delle comunicazioni FS1 per accettazione dei consensi 
     */
    public static final String STATO_COMUNICAZIONE_ACCETTAZIONE_CONSENSI = "19";

    /**
     * Tipo di allegato delle comunicazioni verso il backoffice
     */
    public static final String TIPO_ALLEGATO_XML = "xml";

    /**
     * Path della cartella WEB-INF
     */
    public static final String WEBINF_FOLDER = "/WEB-INF/";

    /**
     * Chiave che individua la property da utilizzare per bypassare l'attivazione
     * del software ed utilizzare il solo genep cifrato con la chiave standard
     */
    public static final String PROP_NON_ATTIVARE_APPLICATIVO       = "it.eldasoft.skipAttivazione";
    
    /**
     * Codice interno attribuito al tipo soggetto impresa legale rappresentante 
     */
    public static final String TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE = "1"; 

    /**
     * Codice interno attribuito al tipo soggetto impresa direttore tecnico
     */
    public static final String TIPO_SOGGETTO_DIRETTORE_TECNICO = "2"; 

    /**
     * Codice interno attribuito al tipo soggetto impresa altra carica o qualifica
     */
    public static final String TIPO_SOGGETTO_ALTRA_CARICA = "3"; 

    /**
     * Codice interno attribuito al tipo soggetto impresa collaboratore
     */
    public static final String TIPO_SOGGETTO_COLLABORATORE = "4";
    
    public static final String SEPARATORE_TABELLATI_CONCATENATI = "-";
    
    /** Identificativo della pagina dell'elenco operatori economici. */
    public static final String CODICE_PAGINA_ELENCO_OP = "ppgare_oper_ec_bandi_avvisi";

	/** Identificativo in sessione del bean che contiene i dati di autenticazione single sign on. */
	public static final String SESSION_OBJECT_ACCOUNT_SSO = "accountSSO";
	
	/** Identificativo in sessione del bean che contiene i dati di autenticazione single sign on. */
	public static final String SESSION_OBJECT_ACCOUNT_SSO_TOKEN = "accountSSOToken";

    /** Identificativo in sessione del bean che contiene i dati di autenticazione single sign on. */
    public static final String SESSION_OBJECT_ACTION = "ACTION_OBJECT";

    /**
	 * Lunghezza minima del campo oggetto (necessario per Titulus).
	 */
	public static final int PROTOCOLLAZIONE_OGGETTO_MIN_LENGTH = 30;
}
