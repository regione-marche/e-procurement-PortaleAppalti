package it.maggioli.eldasoft.plugins.ppgare.aps.system;

/**
 * Classe con le costanti degli eventi da tracciare.
 * 
 * @author Stefano.Sabbadin
 */
public class PortGareEventsConstants {

	/** Operazione di salvataggio comunicazione pre invio. */
	public static final String SALVATAGGIO_COMUNICAZIONE = "SAVECOM";

	/** Operazione di inserimento comunicazione da processare. */
	public static final String INVIO_COMUNICAZIONE_DA_PROCESSARE = "INVCOM";

	/**
	 * Operazione di inserimento comunicazione con richiesta di protocollazione.
	 */
	public static final String INVIO_COMUNICAZIONE_DA_PROTOCOLLARE = "INVCOMDAPROT";

	/** Operazione di protocollazione. */
	public static final String PROTOCOLLAZIONE = "PROT";

	/** Operazione di annullamento invio comunicazione. */
	public static final String ABORT_INVIO_COMUNICAZIONE = "ABORTCOM";

	/** Operazione di protocollazione comunicazione da processare. */
	public static final String PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE = "PROTCOM";

	/** Richiesta di completamento manuale protocollazione comunicazione. */
	public static final String COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE = "COMPLMANPROTCOM";

	/** Operazione di cambio stato comunicazione. */
	public static final String CAMBIO_STATO_COMUNICAZIONE = "STATCOM";

	/** Invio mail di conferma ricevuta. */
	public static final String INVIO_MAIL_CONFERMA_RICEVUTA = "MAIL";
	
	/** Operazione di login. */
	public static final String LOGIN = "LOGIN";

	/** Operazione di logout. */
	public static final String LOGOUT = "LOGOUT";

	/** Utente bloccato per raggiunto numero di tentativi di accesso falliti. */
	public static final String BLOCCO_UTENTE = "USERBLOCK";

	/** IP bloccato per raggiunto numero di tentativi di accesso falliti. */
	public static final String BLOCCO_IP = "IPBLOCK";

	/** IP bloccato per tentativo di accesso all'honeypot */
	public static final String BLOCCO_BOT = "BOTBLOCK";

//	/** Sblocco utente. */
//	public static final String SBLOCCO_UTENTE = "USERUNLOCK";

	/** Generazione del token da comunicare all'utente. */
	public static final String GENERAZIONE_TOKEN = "GENTOKEN";

	/** Utilizza il token ricevuto per attivare o riattivare l'utente. */
	public static final String PROCESSA_TOKEN = "PROCTOKEN";

	/** Inserimento di un nuovo account. */
	public static final String CREAZIONE_ACCOUNT = "CREATEACCOUNT";

	/** Recupero password per mail o username. */
	public static final String RECUPERO_PASSWORD = "PASSWORDRECOVERY";

	/** Cambio password utente. */
	public static final String CAMBIO_PASSWORD = "CHANGEPASSWORD";

	/** Accesso ad una funzionalit&agrave; applicativa. */
	public static final String ACCESSO_FUNZIONE = "ACCESSOFUNZ";

	/** Operazione di rilancio d'asta. */
	public static final String RILANCIO_ASTA = "RILANCIO";

	/** Download documento riservato. */
	public static final String DOWNLOAD_DOCUMENTO_RISERVATO = "DOWNLOADRESERVED";
	
	/** Upload di un file. */
	public static final String UPLOAD_FILE = "UPLOADFILE";
	
	/** Eliminazione di un file precedentemente caricato. */
	public static final String DELETE_FILE = "DELETEFILE";
	
	/** Login tramite sistema di Single Sign On (SSO) **/
	public static final String LOGIN_SSO = "LOGINSSO";
	
	/** Registrazione di un nuovo account a portale dopo login a sistema esterno SSO **/
	public static final String CREAZIONE_ACCOUNT_SSO = "CREATEACCOUNTSSO";
	
	/** Login di un soggetto fisico come soggetto economico **/
	public static final String LOGIN_AS = "LOGINAS";

	public static final String LOGOUT_SSO = "LOGOUTSSO";

	/** Generazione di un token da parte dell'amministrazione per attivare un operatore economico non ancora attivato (CRUSCOTTO MONITORAGGIO) **/
	public static final String GENERAZIONE_TOKEN_ADMIN = "GENTOKENADMIN";
	

	/** Messaggio di dettaglio errore che individua una richiesta fuori tempo massimo. */ 
	public static final String DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO = "Richiesta fuori tempo massimo";
	
	/** Invio richiesta di assistenza **/
	public static final String HELPDESK = "HELPDESK";

	/** Verifica pre invio offerta. */
	public static final String CHECK_INVIO = "CHECKINVIO";
	
	/** Operazione consultazione eventi. */
	public static final String CONSULTA_EVENTI = "CONSULTEVENTS";
	
	/** Soggetto con delega (delegateuser). */
	public static final String DELEGATEUSEER_CHANGE = "DELEGATEUSERCHANGE";
	
	/** Accesso consultazione espletamento gara. */
	public static final String ACCESSO_FASI_GARA = "ACCESSOFASIGARA";

}
