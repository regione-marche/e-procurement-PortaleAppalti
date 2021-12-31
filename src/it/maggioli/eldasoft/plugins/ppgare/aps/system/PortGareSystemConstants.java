package it.maggioli.eldasoft.plugins.ppgare.aps.system;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

/**
 * Interfaccia con le principali costanti della parte del portale gare.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public interface PortGareSystemConstants {

    /**
     * Nome del bean proxy del servizio web per la gestione dei bandi.
     */
    public static final String WS_GARE_APPALTO = "WSGareAppalto";

    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione dei bandi.
     */
    public static final String BANDI_MANAGER = "BandiManager";

    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione delle comunicazioni di gara.
     */
    public static final String COMUNICAZIONI_MANAGER = "ComunicazioniManager";

    
    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione degli eventi.
     */
    public static final String EVENTI_MANAGER = "EventManager";

    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione dei cataloghi.
     */
    public static final String CATALOGHI_MANAGER = "CataloghiManager";
    
    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione dei parametri applicativi.
     */
    public static final String NTP_MANAGER = "NtpManager";
    
    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione dei parametri applicativi.
     */
    public static final String APPPARAM_MANAGER = "AppParamManager";

    
    /**
     * Nome del bean proxy del servizio web per la gestione delle aste.
     */
    public static final String WS_ASTE = "WSAste";

    /**
     * Nome del manager che gestisce l'interfacciamento con il servizio web per
     * la gestione delle aste.
     */
    public static final String ASTE_MANAGER = "AsteManager";
    
    /**
     * Nome del bean proxy del servizio web per la gestione degli ordini NSO (CEF eInvoicing).
     */
    public static final String WS_ORDINI_NSO = "WSOrdiniNSO";

    /**
     * Identificativo dell'attributo in sessione contenente la tipologia di
     * ricerca sui bandi (1=Bandi, 2=Esiti, 3=Avvisi).
     */
    public static final String SESSION_ID_TYPE_SEARCH_BANDI = "typeSearchBandi";

    /**
     * Ricerca bandi.
     */
    public static final int VALUE_TYPE_SEARCH_BANDI = 1;
    /**
     * Ricerca esiti.
     */
    public static final int VALUE_TYPE_SEARCH_ESITI = 2;
    /**
     * Ricerca avvisi.
     */
    public static final int VALUE_TYPE_SEARCH_AVVISI = 3;

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca bandi
     */
    public static final String SESSION_ID_SEARCH_BANDI = "formSearchBandi";

    public static final String SESSION_ID_LIST_ALL_IN_CORSO_BANDI = "formListAllInCorsoBandi";
    
    public static final String SESSION_ID_LIST_ALL_SCADUTI_BANDI = "formListAllScadutiBandi";
    
    public static final String SESSION_ID_LIST_ALL_RICHIESTE_OFFERTA_BANDI = "formListAllRichiesteOffertaBandi";
    
    public static final String SESSION_ID_LIST_ALL_RICHIESTE_DOCUMENTI_BANDI = "formListAllRichiesteDocumentiBandi";
    
    public static final String SESSION_ID_LIST_ALL_ASTE_IN_CORSO_BANDI = "formListAllAsteInCorsoBandi";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca gare privatistiche acquisto/vendita
     */	
    public static final String SESSION_ID_SEARCH_BANDI_ACQ 						= "formSearchBandiProcAggAcq";
    public static final String SESSION_ID_LIST_ALL_ACQ_IN_CORSO_BANDI 			= "formListAllAcqInCorsoBandi";    
    public static final String SESSION_ID_LIST_ALL_ACQ_SCADUTI_BANDI 			= "formListAllAcqScadutiBandi";    
    public static final String SESSION_ID_LIST_ALL_ACQ_RICHIESTE_OFFERTA_BANDI 	= "formListAllAcqRichiesteOffertaBandi";    
    public static final String SESSION_ID_SEARCH_BANDI_VEND 					= "formSearchBandiProcAggVend";
    public static final String SESSION_ID_LIST_ALL_VEND_IN_CORSO_BANDI 			= "formListAllVendInCorsoBandi";    
    public static final String SESSION_ID_LIST_ALL_VEND_SCADUTI_BANDI 			= "formListAllVendScadutiBandi";    
    public static final String SESSION_ID_LIST_ALL_VEND_RICHIESTE_OFFERTA_BANDI = "formListAllVendRichiesteOffertaBandi";    

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca bandi per procedure in aggiudicazione
     */
    public static final String SESSION_ID_SEARCH_BANDI_PROC_AGG = "formSearchBandiProcAgg";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca bandi di iscrizione
     */
    public static final String SESSION_ID_LIST_ALL_BANDI_ISCRIZIONE = "formListAllBandiIscrizione";
    
    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca bandi con esito 
     */
    public static final String SESSION_ID_SEARCH_BANDI_CON_ESITO = "formSearchBandiConEsito";
    
    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca esiti
     */
    public static final String SESSION_ID_SEARCH_ESITI = "formSearchEsiti";

    public static final String SESSION_ID_LIST_ALL_IN_CORSO_ESITI = "formListAllInCorsoEsiti";
    
    public static final String SESSION_ID_LIST_ALL_AFFIDAMENTI_ESITI = "formListAllAffidamentiEsiti";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca avvisi
     */
    public static final String SESSION_ID_SEARCH_AVVISI = "formSearchAvvisi";

    public static final String SESSION_ID_LIST_ALL_IN_CORSO_AVVISI = "formListAllInCorsoAvvisi";
    
    public static final String SESSION_ID_LIST_ALL_SCADUTI_AVVISI = "formListAllScadutiAvvisi";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca contratti
     */
    public static final String SESSION_ID_SEARCH_CONTRATTI = "formSearchContratti";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca prospetto amministrazione aperta.
     */
    public static final String SESSION_ID_SEARCH_AMM_APERTA = "formSearchAmmAperta";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca prospetto anticorruzione bandi di gara e contratti.
     */
    public static final String SESSION_ID_SEARCH_ANTICORRUZIONE = "formSearchAnticorruzione";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca della lista articoli.
     */
    public static final String SESSION_ID_SEARCH_ARTICOLI = "formSearchArticoli";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca dei prodotti.
     */
    public static final String SESSION_ID_SEARCH_PRODOTTI = "formSearchProdotti";

    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca ordini (CEF)
     */
    public static final String SESSION_ID_SEARCH_ORDINI = "formSearchOrdini";

    /**
     * Identificativo dell'attributo in sessione contenente il flag che indica
     * se si proviene da un form di ricerca oppure no
     */
    public static final String SESSION_ID_FROM_SEARCH = "fromSearch";

    /**
     * Identificativo dell'attributo in sessione contenente l'identificativo
     * della pagina da cui parte una richiesta
     */
    public static final String SESSION_ID_FROM_PAGE = "fromPage";
    
    /**
     * Identificativo dell'attributo in sessione contenente l'identificativo
     * della proprietario della pagina da cui parte una richiesta (bandi, esiti, avvisi, ...)
     */
    public static final String SESSION_ID_FROM_PAGE_OWNER = "fromPageOwner";

    /**
     * Identificativo dell'attributo in sessione contenente l'identificativo
     * della pagina da cui parte una richiesta
     */
    public static final String SESSION_ID_FROM_PAGE_FIRMA_DIGITALE = "fromPageFirmaDigitale";
    
    /**
     * Identificativo dell'attributo in sessione contentente il nome del file
     * fisico (es: presente nella work) da gestire in InfoFirmaDigitaleAction 
     */
    public static final String SESSION_ID_DOWNLOAD_WORK_FILE_FIRMA_DIGITALE = "downloadWorkFileFirmaDigitale";
    
    /**
     * Variabile di sessione per individuare una sottopagina di dettaglio di una
     * funzionalita' (vedi ad esempio l'inserimento di un'iscrizione ad un albo
     * fornitori)
     */
    public static final String SESSION_ID_PAGINA = "page";

    // VARIABILI DI SESSIONE CONTENITORE DI WIZARD
    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio registrazione
     */
    public static final String SESSION_ID_DETT_REGISTRAZIONE_IMPRESA = "dettRegistrImpresa";

    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio impresa per la visualizzazione o modifica
     */
    public static final String SESSION_ID_DETT_ANAGRAFICA_IMPRESA = "dettAnagrImpresa";

    /**
     * Variabile di sessione per individuare la url di ritorno in seguito ad un
     * aggiornamento anagrafica impresa
     */
    public static final String SESSION_ID_URL_RITORNO_AGG_ANAGRAFICA_IMPRESA = "returnUrlAggAnagrImpresa";

    /**
     * Variabile di sessione per individuare il namespace della action di
     * ritorno in seguito ad un aggiornamento anagrafica impresa.
     */
    public static final String SESSION_ID_MODULO_RITORNO_AGG_ANAGRAFICA_IMPRESA = "returnModuleAggAnagrImpresa";

    /**
     * Variabile di sessione per individuare la action di ritorno in seguito ad
     * un aggiornamento anagrafica impresa.
     */
    public static final String SESSION_ID_ACTION_RITORNO_AGG_ANAGRAFICA_IMPRESA = "returnActionAggAnagrImpresa";

    /**
     * Variabile di sessione per individuare il frame di ritorno in seguito ad
     * un aggiornamento anagrafica impresa.
     */
    public static final String SESSION_ID_FRAME_RITORNO_AGG_ANAGRAFICA_IMPRESA = "returnFrameAggAnagrImpresa";

    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio partecipazione ad una gara
     */
    public static final String SESSION_ID_DETT_GARA = "dettGara";

    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio iscrizione all'albo fornitori
     */
    public static final String SESSION_ID_DETT_ISCR_ALBO = "dettIscrAlbo";

    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio rinnovo all'albo fornitori
     */
    public static final String SESSION_ID_DETT_RINN_ALBO = "dettRinnAlbo";

    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio partecipazione ad una gara
     */
    public static final String SESSION_ID_DETT_PART_GARA = "dettPartecipGara";
    
    /**
     * Variabile di sessione per individuare il contenitore con i dati del
     * dettaglio prodotto da inserire
     */
    public static final String SESSION_ID_DETT_PRODOTTO = "dettProdotto";

    /**
     * Variabile di sessione per individuare il contenitore con i dati dei
     * documenti della busta di prequalifica
     */
    public static final String SESSION_ID_DETT_BUSTA_PRE_QUALIFICA = "dettBustaPrequalifica";

    /**
     * Variabile di sessione per individuare il contenitore con i dati dei
     * documenti della busta amministrativa
     */
    public static final String SESSION_ID_DETT_BUSTA_AMMINISTRATIVA = "dettBustaAmministrativa";

    /**
     * Variabile di sessione per individuare il contenitore con i dati dei
     * documenti della busta economica
     */
    public static final String SESSION_ID_DETT_BUSTA_ECONOMICA = "dettBustaEconomica";

    /**
     * Variabile di sessione per individuare il contenitore con i dati dei
     * documenti della busta tecnica
     */
    public static final String SESSION_ID_DETT_BUSTA_TECNICA = "dettBustaTecnica";
        
    /**
     * Variabile di sessione per individuare il contenitore con i dati della busta
     * riepilogativa
     */
	public static final String SESSION_ID_DETT_BUSTA_RIEPILOGATIVA = "riepilogoBuste";
	

    /**
     * Variabile di sessione per individuare il contenitore con i dati
     * dell'offerta economica per una gara telematica.
     */
    public static final String SESSION_ID_OFFERTA_ECONOMICA = "offertaEconomica";
    
    /**
     * Variabile di sessione per individuare il contenitore con i dati
     * dell'offerta tecnica per una gara telematica.
     */
    public static final String SESSION_ID_OFFERTA_TECNICA = "offertaTecnica";

    /**
     * Variabile di sessione per individuare il carrello prodotti
     */
    public static final String SESSION_ID_CARRELLO_PRODOTTI = "carrelloProdotti";

//    /**
//     * Identificativo dell'attributo in sessione contenente i documenti per il
//     * rinnovo dell'iscrizione al catalogo/elenco
//     */
//    public static final String SESSION_ID_DOCUMENTI_RINNOVO = "dettDocumentiRinnovo";

    /**
     * Variabile di sessione per individuare il contenitore con i dati
     * del riepilogo buste di un'offerta
     */
    public static final String SESSION_ID_RIEPILOGO_BUSTE = "documentoRiepilogativa";

    /**
     * Variabile di sessione per individuare i dati dell'offerta d'asta
     */
    public static final String SESSION_ID_DETT_OFFERTA_ASTA = "dettOffertaAsta";

    /**
     * Variabile di sessione per individuare i dati dell'offerta d'asta
     */
    public static final String SESSION_ID_RILANCIO_PREZZI_UNITARI = "dettAstaPrezziUnitari";
    public static final String SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE = "dettAstaPrezziUnitariSoggette";
    public static final String SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE = "dettAstaPrezziUnitariNonSoggette";

    /**
     * Identificativo dell'attributo in sessione contenente la stazione appaltante 
     * da utilizzare per partizionare i dati del portale
     */
    public static final String SESSION_ID_STAZIONE_APPALTANTE_CF = "codiceFiscaleSA";
    public static final String SESSION_ID_STAZIONE_APPALTANTE_DESC = "denominazioneSA";
    public static final String SESSION_ID_STAZIONE_APPALTANTE = "codiceSA";
    public static final String SESSION_ID_STAZIONE_APPALTANTE_LAYOUT = "layoutStyle";
    
	/**
	 * Sentinella il cui nome non deve avere un significato chiaro ed evidente,
	 * e serve per la marcatura delle sessioni in cui si l'amministratore si
	 * logga come un operatore per dargli assistenza.
	 */
    public static final String SESSION_ID_SENTINELLA_LOGIN_AS = "alaoe";

    // IDENTIFICATIVI PAGINE DI WIZARD
    /**
     * Costante generica che individua la pagina finale di riepilogo in
     * qualsiasi wizard
     */
    public static final String WIZARD_PAGINA_RIEPILOGO = "fine";

    /**
     * Costante generica che individua la pagina con dati dell'impresa
     */
    public static final String WIZARD_PAGINA_DATI_IMPRESA = "impr";

    /**
     * Costante che individua la pagina degli indirizzi in un wizard che li
     * visualizza (registrazione o modifica anagrafica)
     */
    public static final String WIZARD_AGGIMPRESA_PAGINA_INDIRIZZI = "indir";

    /**
     * Costante che individua la pagina degli altri dati anagrafici per i liberi
     * professionisti.
     */
    public static final String WIZARD_AGGIMPRESA_PAGINA_ALTRI_DATI_ANAGR = "altri-dati";

    /**
     * Costante che individua la pagina dei soggetti in un wizard che li
     * visualizza (registrazione o modifica anagrafica)
     */
    public static final String WIZARD_AGGIMPRESA_PAGINA_SOGGETTI = "sogg";

    /**
     * Costante che individua la pagina dei dati ulteriori impresa in un wizard
     * che li visualizza (registrazione o modifica anagrafica)
     */
    public static final String WIZARD_AGGIMPRESA_PAGINA_DATI_ULT_IMPRESA = "impr-ult";

    /**
     * Costante che individua la pagina dei dati account e privacy nel wizard
     * registrazione impresa
     */
    public static final String WIZARD_REGIMPRESA_PAGINA_ACCOUNT_PRIVACY = "user";

    /**
     * Costante che individua la pagina di selezione della stazione appaltante
     * nel wizard iscrizione albo fornitori
     */
    public static final String WIZARD_ISCRALBO_PAGINA_SA = "sa";

    /**
     * Costante che individua la pagina di inserimento delle categorie nel
     * wizard iscrizione albo fornitori
     */
    public static final String WIZARD_ISCRALBO_PAGINA_CATEGORIE = "cate";

    /**
     * Costante che individua la pagina di riepilogo delle categorie nel wizard
     * iscrizione albo fornitori
     */
    public static final String WIZARD_ISCRALBO_PAGINA_RIEPILOGO_CATEGORIE = "riepcate";

    /**
     * Costante che individua la pagina di inserimento dei documenti nel wizard
     * iscrizione albo fornitori
     */
    public static final String WIZARD_ISCRALBO_PAGINA_DOCUMENTI = "doc";

    /**
     * Costante che individua la pagina di indicazione se RTI nel wizard
     * partecipazione ad una gara (domanda partecipazione o invio offerta) e nel
     * wizard di iscrizione ad un elenco/catalogo
     */
    public static final String WIZARD_PAGINA_RTI = "rti";

    /**
     * Costante che individua la pagina di indicazione componenti nel wizard
     * partecipazione ad una gara.
     */
    public static final String WIZARD_PAGINA_COMPONENTI = "componenti";

    /**
     * Costante che individua la pagina di inserimento dei lotti nel wizard
     * partecipazione ad una gara (domanda partecipazione o invio offerta) e nel
     * wizard di iscrizione ad un elenco/catalogo
     */
    public static final String WIZARD_PARTGARA_PAGINA_LOTTI = "lotti";

    /**
     * Costante che individua la pagina di selezione articolo nel wizard di
     * inserimento di un prodotto
     */
    public static final String WIZARD_PAGINA_SELEZIONE_ARTICOLO = "articolo";

    /**
     * Costante che individua la pagina di selezione articolo nel wizard di
     * inserimento di un prodotto
     */
    public static final String WIZARD_PAGINA_DEFINIZIONE_PRODOTTO = "prodotto";

    /**
     * Costante generica che individua la pagina con i dati documenti busta
     * amministrativa
     */
    public static final String WIZARD_PAGINA_DOC_BUSTA_AMM = "dcoBustaAmm";

    /**
     * Costante generica che individua la pagina con i dati documenti busta
     * economica
     */
    public static final String WIZARD_PAGINA_DOC_BUSTA_ECO = "dcoBustaEco";

    /**
     * Costante generica che individua la pagina con i dati documenti busta
     * amministrativa
     */
    public static final String WIZARD_PAGINA_DOC_BUSTA_TEC = "dcoBustaTec";

    /**
     * Costante generica che individua la pagina con i dati documenti busta
     * di prequalifica
     */
    public static final String WIZARD_PAGINA_DOC_BUSTA_PREQ = "dcoBustaPreq";
    
    /**
     * Costante che individua la pagina di selezione articoli nel wizard di
     * inserimento prodotti tramite excel
     */
    public static final String WIZARD_PAGINA_SELEZIONE_ARTICOLI = "articoli";

    // FILE ALLEGATI DELLE COMUNICAZIONI
    /**
     * Nome del file xml contenente la registrazione
     */
    public static final String NOME_FILE_REGISTRAZIONE = "dati_reg.xml";

    /**
     * Nome del file xml contenente l'iscrizione
     */
    public static final String NOME_FILE_ISCRIZIONE = "dati_iscele.xml";

    /**
     * Nome del file xml contenente l'aggiornamento dati dell'iscrizione
     */
    public static final String NOME_FILE_AGG_ISCRIZIONE = "dati_aggisc.xml";

    /**
     * Nome del file xml contenente l'aggiornamento dati dell'anagrafica impresa
     */
    public static final String NOME_FILE_AGG_ANAGRAFICA = "dati_agganag.xml";

    /**
     * Nome del file xml contenente il rinnovo iscrizione ad elenco.
     */
    public static final String NOME_FILE_RINNOVO_ISCRIZIONE = "dati_rin.xml";

    /**
     * Nome del file xml contenente una domanda di variazione
     */
    public static final String NOME_FILE_DOMANDA_VARIAZIONE = "dati_domvar.xml";

    /**
     * Nome del file xml contenente i dati di partecipazione come rti
     */
    public static final String NOME_FILE_TIPO_PARTECIPAZIONE = "dati_partrti.xml";

    /**
     * Nome del file xml contenente i dati di gestione dei prodotti in catalogo
     */
    public static final String NOME_FILE_GESTIONE_PRODOTTI = "dati_prodotti.xml";

    /**
     * Nome del file xml contenente i dati di gestione delle bozze nel carrello
     */
    public static final String NOME_FILE_PRODOTTI_BOZZE = "dati_bozze.xml";

    /**
     * Nome del file xml contenente i dati di variazione offerta prodotti
     */
    public static final String NOME_FILE_VARIAZIONE_OFFERTA = "dati_varprodotti.xml";

    /**
     * Nome del file xml contenente i dati di gestione delle bozze nel carrello
     */
    public static final String NOME_FILE_BUSTA = "busta.xml";
    
    /**
     * Nome del file xml contenente i dati di riepilogo per le buste di un'offerta
     */
    public static final String NOME_FILE_BUSTA_RIEPILOGATIVA = "riepilogo_buste.xml";


    // TIPI COMUNICAZIONE
    /**
     * Tipo di comunicazione per la richiesta di registrazione al portale.
     */
    public static final String RICHIESTA_REGISTRAZIONE_PORTALE = "FS1";
    /**
     * Tipo di comunicazione per la richiesta di iscrizione
     */
    public static final String RICHIESTA_ISCRIZIONE_ALBO = "FS2";

    /**
     * Tipo di comunicazione per l'invio di un rinnovo iscrizione a
     * elenco/catalogo
     */
    public static final String RICHIESTA_RINNOVO_ISCRIZIONE = "FS3";

    /**
     * Tipo di comunicazione per la richiesta di aggiornamento dati/documenti di
     * una iscrizione
     */
    public static final String RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO = "FS4";

    /**
     * Tipo di comunicazione per la richiesta di aggiornamento anagrafica
     */
    public static final String RICHIESTA_AGGIORNAMENTO_ANAGRAFICA = "FS5";

    /**
     * Tipo di comunicazione per la richiesta di variazione dati anagrafici
     */
    public static final String RICHIESTA_VARIAZIONE_DATI_ANAGRAFICI = "FS6";

    /**
     * Tipo di comunicazione per la richiesta di variazione prodotti del
     * catalogo
     */
    public static final String RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO = "FS7";

    /**
     * Tipo di comunicazione per la richiesta di variazione prezzi e scadenze di
     * prodotti del catalogo
     */
    public static final String RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO = "FS8";

    /**
     * Tipo di comunicazione per la richiesta di partecipazione o meno di
     * un'impresa come RTI
     */
    public static final String RICHIESTA_TIPO_PARTECIPAZIONE = "FS9";

    /**
     * Tipo di comunicazione per la richiesta di partecipazione di un'impresa
     * con gara telematica.
     */
    public static final String RICHIESTA_TIPO_PARTECIPAZIONE_GT = "FS10";

    /**
     * Tipo di comunicazione per l'invio di un'offerta di un'impresa con gara
     * telematica.
     */
    public static final String RICHIESTA_TIPO_INVIO_OFFERTA_GT = "FS11";

    /**
     * Tipo di comunicazione per l'invio di della busta di prequalifica
     */
    public static final String RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA = "FS10A";

    /**
     * Tipo di comunicazione per l'invio di della busta amministrativa.
     */
    public static final String RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA = "FS11A";

    /**
     * Tipo di comunicazione per l'invio di della busta tecnica.
     */
    public static final String RICHIESTA_TIPO_BUSTA_TECNICA = "FS11B";

    /**
     * Tipo di comunicazione per l'invio di della busta economica.
     */
    public static final String RICHIESTA_TIPO_BUSTA_ECONOMICA = "FS11C";
    
    /**
     * Tipo di comunicazione per la busta prequalifica riepilogativa.
     */
    public static final String RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO = "FS10R";
    
    /**
     * Tipo di comunicazione per la busta riepilogativa.
     */
    public static final String RICHIESTA_TIPO_BUSTA_RIEPILOGO = "FS11R";

//    /**
//     * Tipo di comunicazione per l'invio di della busta con offerta economica
//     * interamente telematica.
//     * <p>
//     * <b>NB: potremmo differenziarla in FS11D</b>
//     * </p>
//     */
//    public static final String RICHIESTA_TIPO_BUSTA_ECONOMICA_OFFERTA_TELEMATICA = "FS11C";

    /**
     * Tipo di comunicazione per l'invio di comunicazioni da parte di un'impresa.
     */
    public static final String RICHIESTA_INVIO_COMUNICAZIONE = "FS12";

    /**
     * Tipo di comunicazione per la registrazione delle offerte d'asta
     */
    public static final String RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA = "FS13";

    /**
     * Oggetto per le comunicazioni di invio offerta d'asta
     */
    public static final String RICHIESTA_INVIO_COMUNICAZIONE_OFFERTA_ASTA_OGGETTO = "Conferma offerta finale asta elettronica";
    
    // IDENTIFICATIVI TIPOLOGIE DI GARA
    /**
     * Identificativo della gara a piu' lotti con offerte distinte
     */
    public static final int TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE = 1;

    /**
     * Identificativo della gara a lotto unico
     */
    public static final int TIPOLOGIA_GARA_LOTTO_UNICO = 2;

    /**
     * Identificativo della gara a piu' lotti con offerta unica
     */
    public static final int TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO = 3;

    // TIPOLOGIE DI ELENCO
    /**
     * Identificativo della tipologia di elenco per operatori economici,
     * utilizzato per gestire gare d'appalto.
     */
    public static final int TIPOLOGIA_ELENCO_STANDARD = 1;

    /**
     * Identificativo della tipologia di elenco per catalogo.
     */
    public static final int TIPOLOGIA_ELENCO_CATALOGO = 2;

    
    // TIPOLOGIE DI CLASSIFICA PER LE CATEGORIE BANDI ISCRIZIONE E CATALOGHI
    public static final int TIPO_CLASSIFICA_INTERVALLO = 1;
    
    public static final int TIPO_CLASSIFICA_MASSIMA = 2;
    
    public static final int TIPO_CLASSIFICA_NESSUNA = 3;

    
    // TIPOLOGIE DI EVENTO LEGATE AL BARCODE
    /**
     * Identificativo della tipologia di evento di partecipazione ad una gara
     */
    public static final int TIPOLOGIA_EVENTO_PARTECIPA_GARA = 1;

    /**
     * Identificativo della tipologia di evento invia offerta per una gara
     */
    public static final int TIPOLOGIA_EVENTO_INVIA_OFFERTA = 2;

    /**
     * Identificativo della tipologia di evento per l'invio documentazione al
     * fine di comprovare i requisiti
     */
    public static final int TIPOLOGIA_EVENTO_COMPROVA_REQUISITI = 3;

    /**
     * Identificativo della tipologia di evento invia offerte distinte per una gara
     */
    public static final int TIPOLOGIA_EVENTO_INVIA_OFFERTE_DISTINTE = 4;
    
    /**
     * Cartella in cui salvare i modelli excel per l'esportazione
     */
    public static final String GARE_SHEET_FOLDER = CommonSystemConstants.WEBINF_FOLDER
	    + "plugins/ppgare/aps/sheet/";

    /**
     * Nome modello excel per importazione massiva prodotti
     */
    public static final String NOME_MODELLO_EXCEL_IMPORT_PRODOTTI = "prodotti";

    /**
     * Percorso modello excel per importazione massiva prodotti
     */
    public static final String MODELLO_EXCEL_IMPORT_PRODOTTI = GARE_SHEET_FOLDER
	    + "/" + NOME_MODELLO_EXCEL_IMPORT_PRODOTTI + ".xls";

    /**
     * Nome modello excel per aggiornamento prezzi e scadenze prodotti a
     * catalogo
     */
    public static final String NOME_MODELLO_EXCEL_VARIAZIONE_PREZZI_SCADENZE = "variazione_prezzi_scadenze";

    /**
     * Percorso modello excel per aggiornamento prezzi e scadenze prodotti a
     * catalogo
     */
    public static final String MODELLO_EXCEL_VARIAZIONE_PREZZI_SCADENZE = GARE_SHEET_FOLDER
	    + "/" + NOME_MODELLO_EXCEL_VARIAZIONE_PREZZI_SCADENZE + ".xls";

    /**
     * Nome foglio excel per aggiornamento prezzi e scadenze prodotti a catalogo
     */
    public static final String NOME_FOGLIO_EXCEL_VARIAZIONE_PREZZI_SCADENZE = "PRODOTTI";

    /**
     * Nome file excel export articoli
     */
    public static final String NOME_MODELLO_EXCEL_EXPORT_ARTICOLI = "ARTICOLI";

    /**
     * Estensione dei file contenenti i modelli Jasper compilati da lanciare
     * nell'applicativo.
     */
    public static final String ESTENSIONE_JASPER_REPORT = ".jasper";

    /**
     * Cartella in cui salvare i Jasper Report della verticalizzazione di gare
     */
    public static final String GARE_JASPER_FOLDER = CommonSystemConstants.WEBINF_FOLDER
	    + "plugins/ppgare/aps/jasper/";

    /**
     * Cartella in cui salvare i subreport Jasper della verticalizzazione di
     * gare
     */
    public static final String GARE_JASPER__SUBREPORT_FOLDER = PortGareSystemConstants.GARE_JASPER_FOLDER
	    + "subreports/";

    /**
     * Parametro standard per indicare il path di subreport jasper
     */
    public static final String JASPER__SUBREPORT_DIR = "SUBREPORT_DIR";

    /**
     * Parametro standard per indicare il path di images
     */
    public static final String IMAGES_DIR = "IMAGES_DIR";

    /**
     * Cartella in cui salvare le immagini della verticalizzazione di gare
     */
    public static final String GARE_JASPER__IMAGES_FOLDER = PortGareSystemConstants.GARE_JASPER_FOLDER
	    + "images/";

    /** Tipologia di report per l'iscrizione ad elenco o catalogo. */
    public static final int JASPER_REPORT_ISCRIZIONE = 1;

    /** Tipologia di report per il rinnovo ad elenco o catalogo. */
    public static final int JASPER_REPORT_RINNOVO = 2;

    /** Tipologia di report per l'aggiornamento iscrizione ad elenco o catalogo. */
    public static final int JASPER_REPORT_AGG_ISCRIZIONE = 3;

    /**
     * Tipologia busta amministrativa
     */
    public static final int BUSTA_AMMINISTRATIVA = 1;

    /**
     * Tipologia busta tecnica
     */
    public static final int BUSTA_TECNICA = 2;

    /**
     * Tipologia busta economica
     */
    public static final int BUSTA_ECONOMICA = 3;

    /**
     * Tipologia busta di prequalifica.
     */
    public static final int BUSTA_PRE_QUALIFICA = 4;

    /**
     * Tipologia busta amministrativa
     */
    public static final String BUSTA_AMM = "Busta amministrativa";

    /**
     * Tipologia busta tecnica
     */
    public static final String BUSTA_TEC = "Busta tecnica";

    /**
     * Tipologia busta economica
     */
    public static final String BUSTA_ECO = "Busta economica";
    
    /**
     * Tipologia busta economica
     */
    public static final String BUSTA_PRE = "Busta di prequalifica";

    /**
     * Descrizione per il documento di offerta economica
     */
    public static final String DESCRIZIONE_DOCUMENTO_OFFERTA_ECONOMICA = "Offerta economica";

    /**
     * Descrizione per il documento di offerta tecnica
     */
    public static final String DESCRIZIONE_DOCUMENTO_OFFERTA_TECNICA = "Offerta tecnica";
    
    public static final String NOME_EVENTO_PARTECIPA_GARA = "partecipazione gara";

    public static final String NOME_EVENTO_INVIA_OFFERTA = "invio offerta";

    public static final String NOME_EVENTO_COMPROVA_REQUISITI = "comprova requisiti";

    // FORMATI DEI DOCUMENTI ALLEGABILI NEL PORTALE
    /**
     * Documento allegato con firma digitale (estensione p7m).
     */
    public static final int DOCUMENTO_FORMATO_FIRMATO = 1;

    /**
     * Documento allegato PDF (estensione pdf).
     */
    public static final int DOCUMENTO_FORMATO_PDF = 2;

    /**
     * Documento in qualsiasi formato.
     */
    public static final int DOCUMENTO_FORMATO_QUALSIASI = 3;

    /**
     * Documento allegato in formato Excel (estensioni xls, xlsx, ods).
     */
    public static final int DOCUMENTO_FORMATO_EXCEL = 4;

    /**
     * Stati dei file allegati ad una busta xml presente in una comunicazione  
     * I file allegati possono essere all'esterno della busta e quindi possono
     * essere inviati singolarmente anziche' in con unico invio al termine della
     * preparazione della busta
     */
    /**
     * Documento busta inalterato   
     */
    public static final int STATO_DOCUMENTO_BUSTA_NESSUNO		= 0;
    
    /**
     * Documento busta modificato  
     */
    public static final int STATO_DOCUMENTO_BUSTA_MODIFICATO 	= 1;
    
    /**
     * Documento busta eliminato  
     */
    public static final int STATO_DOCUMENTO_BUSTA_ELIMINATO		= -1;
    
    /**
     * Variabile che memorizza gli articoli selezionati per esportazione excel
     * inserimnto prodotti.
     */
    public static final String ARTICOLI_X_EXPORT_IMPORT = "ARTICOLI_X_EXPORT_IMPORT";

    public static final String ARTICOLI_ERRATI_X_EXPORT_IMPORT = "ARTICOLI_ERRATI_X_EXPORT_IMPORT";

    public static final String CATALOGO = "CATALOGO";

    /** Protocollazione delle comunicazioni non prevista da configurazione. */
    public static final int TIPO_PROTOCOLLAZIONE_NON_PREVISTA = 0;

    /**
     * Protocollazione delle comunicazioni mediante inoltro di mail agli
     * indirizzi esplicitati in configurazione.
     */
    public static final int TIPO_PROTOCOLLAZIONE_MAIL = 1;

    /**
     * Protocollazione delle comunicazioni mediante integrazione con sistemi di
     * protocollazione esterni all'applicativo (Iride, Jiride, ...).
     */
    public static final int TIPO_PROTOCOLLAZIONE_WSDM = 2;

    /**
     * Tipo di classifica per un'asta
     */
    public static final int TIPO_CLASSIFICA_PROPRIA = 1;
    
    public static final int TIPO_CLASSIFICA_PROPRIA_E_MIGLIOR_OFFERTA = 2;
    
    public static final int TIPO_CLASSIFICA_GENERALE = 3;

    /**
     * Tipologia validatori attributi aggiuntivi compilazione offerta economica
     */
    public static final int TIPO_VALIDATORE_DATA = 1;

    public static final int TIPO_VALIDATORE_IMPORTO = 2;

    public static final int TIPO_VALIDATORE_TABELLATO = 3;

    public static final int TIPO_VALIDATORE_NOTE = 4;

    public static final int TIPO_VALIDATORE_NUMERO = 5;

    public static final int TIPO_VALIDATORE_FLAG = 6;

    public static final int TIPO_VALIDATORE_STRINGA = 7;
	
    
    /**
     * Tipi di critari di valutazione
     */
    public static final int CRITERIO_TECNICO	= 1;
    public static final int CRITERIO_ECONOMICO 	= 2;

    public static final int CRITERIO_VALUTAZIONE_DATA = 1;
    
    public static final int CRITERIO_VALUTAZIONE_IMPORTO = 2;
    
    public static final int CRITERIO_VALUTAZIONE_LISTA_VALORI = 3;
    
    public static final int CRITERIO_VALUTAZIONE_TESTO = 4;
    
    public static final int CRITERIO_VALUTAZIONE_INTERO = 5;
    
    public static final int CRITERIO_VALUTAZIONE_DECIMALE = 6;
    
	public static final int CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO = 50;
	
	public static final int CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO = 51;
	
	public static final int CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI = 52;
	
	// Ordini NSO
    /**
     * Identificativo dell'attributo in sessione contenente i dati del form di
     * ricerca Ordini NSO
     */
	public static final String SESSION_ID_IMPRESA = "idImpresa";
	
    public static final String SESSION_ID_SEARCH_NSO = "formSearchNSO";

    public static final String SESSION_ID_LIST_NSO_DAVALUTARE = "formListNSODaValutare";
    
    public static final String SESSION_ID_LIST_NSO_CONFERMATI = "formListNSOConfermati";
    
    public static final String SESSION_ID_LIST_NSO_TUTTI = "formListNSOAll";
    
    /*
     * field per la messa in sessione dell'identificativo del file per la impresa
     */
    public static final String SESSION_ID_NSO_FILE_NAME_IMPR = "nsoImprFileName";

	
	/** Campi di ricerca per gli eventi **/
	public static final String SESSION_ID_SEARCH_EVENTS_FIELD_DATE_FROM = "eventtime-f";
	
	public static final String SESSION_ID_SEARCH_EVENTS_FIELD_DATE_TO = "eventtime-t";
	
	public static final String SESSION_ID_SEARCH_EVENTS_FIELD_USERNAME = "username";
	
	public static final String SESSION_ID_SEARCH_EVENTS_FIELD_DESTINATION = "destination";
	
	public static final String SESSION_ID_SEARCH_EVENTS_FIELD_TYPE ="eventtype";
	
	public static final String SESSION_ID_SEARCH_EVENTS_FIELD_LEVEL = "level";
	
	public static final String SESSION_ID_SEARCH_EVENTS_PAGE_NUMBER ="pageNum";
	
	public static final String SESSION_ID_LOADED_EVENTS ="loadedEvents";
	
	public static final String SESSION_ID_LOADED_EVENTS_RESULT ="eventSearchResult";
	
	/** Costanti per gestione invio/ricezione comunicazioni **/
	public static final String NOME_EVENTO_INVIO_NUOVA_COMUNICAZIONE = "invio nuova comunicazione";
    public static final String SESSION_ID_NUOVA_COMUNICAZIONE= "nuovaComunicazione";

	public static final String SESSION_ID_SEARCH_CONSULENTI_COLLABORATORI = "consulentiCollaboratori";

	/** Estremi protocollo SSO **/
	public static final String TIPO_PROTOCOLLO_SSO = "sso.protocollo";
	public static final int MIN_NUM_SSO_PROTOCOLLO = 0;
	public static final int MAX_NUM_SSO_PROTOCOLLO = 3;
	public static final int SSO_PROTOCOLLO_SHIBBOLETH = 1;
	public static final int SSO_PROTOCOLLO_COHESION = 2;
	public static final int SSO_PROTOCOLLO_SPID_MAGGIOLI = 3;
	
}
