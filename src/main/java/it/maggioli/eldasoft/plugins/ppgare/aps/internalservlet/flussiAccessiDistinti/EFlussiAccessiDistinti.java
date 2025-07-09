package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti;

import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.services.user.DelegateUser;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Lista di enum che definiscono i tipi di flusso di funzione.
 *
 */
public enum EFlussiAccessiDistinti {

	NONE,
	LOGIN,
	REGISTRAZIONE_IMPRESA,
	MODIFICA_IMPRESA,
	COMUNICAZIONE,
	COMUNICAZIONE_STIPULA,
	OFFERTA_GARA,
	ISCRIZIONE_ELENCO,
	RINNOVO_ELENCO,
	ISCRIZIONE_CATALOGO,
	RINNOVO_CATALOGO,
	PRODOTTI,
	STIPULA,
	ASTA;

	/**
	 * mappa dei permessi di accesso dei flussi e dei rispettivi ruoli
	 */
	public static final Map<String, DelegateUser.Accesso[]> PERMESSI_COMPILAZIONE = new HashMap<String, DelegateUser.Accesso[]>() {{		
	    put(REGISTRAZIONE_IMPRESA.toString(), 	new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(MODIFICA_IMPRESA.toString(), 		new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(COMUNICAZIONE.toString(), 			new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(COMUNICAZIONE_STIPULA.toString(), 	new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(OFFERTA_GARA.toString(), 			new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(ISCRIZIONE_ELENCO.toString(), 		new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(RINNOVO_ELENCO.toString(), 			new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(ISCRIZIONE_CATALOGO.toString(), 	new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(RINNOVO_CATALOGO.toString(), 		new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(PRODOTTI.toString(), 				new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(STIPULA.toString(), 				new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(ASTA.toString(), 					new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	}};

	/**
	 * mappa dei permessi di invio dei flussi e dei rispettivi ruoli
	 */
	public static final Map<String, DelegateUser.Accesso[]> PERMESSI_INVIO = new HashMap<String, DelegateUser.Accesso[]>() {{		
	    put(REGISTRAZIONE_IMPRESA.toString(), 	new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(MODIFICA_IMPRESA.toString(), 		new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(COMUNICAZIONE.toString(), 			new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(COMUNICAZIONE_STIPULA.toString(), 	new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND, DelegateUser.Accesso.EDIT});
	    put(OFFERTA_GARA.toString(), 			new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(ISCRIZIONE_ELENCO.toString(), 		new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(RINNOVO_ELENCO.toString(), 			new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(ISCRIZIONE_CATALOGO.toString(), 	new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(RINNOVO_CATALOGO.toString(), 		new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(PRODOTTI.toString(), 				new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(STIPULA.toString(), 				new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	    put(ASTA.toString(), 					new DelegateUser.Accesso[] {DelegateUser.Accesso.EDIT_SEND});
	}};

/*	
 	// le descrizione possono servire per rendere i log in tabella "piu' leggibili"
	NONE("Not defined"),
	REGISTRAZIONE_IMPRESA("Registrazione impresa"),
	COMUNICAZIONE("Comunicazione"),
	COMUNICAZIONE_STIPULA("Comunicazione stipula"),
	OFFERTA_GARA("Offerta gara"),
	ISCRIZIONE_ELENCO("Iscrizione elenco"),
	RINNOVO_ELENCO("Rinnovo elenco"),
	ISCRIZIONE_CATALOGO("Iscrizione catalogo"),
	RINNOVO_CATALOGO("Rinnovo catalogo"),
	PRODOTTI("Gestione prodotti"),
	STIPULA("Stipula"),
	ASTA("Asta");

	private String value;

	EFlussiAccessiDistinti(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static EFlussiAccessiDistinti getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(EFlussiAccessiDistinti v : values())
            if(value.equalsIgnoreCase(v.getValue())) return v;
        throw new IllegalArgumentException();
    }

*/
	
	/**
	 * tipologia di accesso previsto per accedere ad un flusso 
	 */
//	// crea l'associazione lista/ricerca con il proprio session id...
//	public static final Map<String, String> PERMESSIACCESO_PER_FLUSSO = new HashMap<String, String>() {{
//	    put(LIST_ALL_BANDI, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI);
//		put(LIST_ALL_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_IN_CORSO_BANDI);
//		put(LIST_ALL_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_RICHIESTE_OFFERTA_BANDI);
//		put(LIST_ALL_RICHIESTE_DOCUMENTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_RICHIESTE_DOCUMENTI_BANDI);
//		put(LIST_ALL_ASTE_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_ASTE_IN_CORSO_BANDI);
//		put(LIST_ALL_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_SCADUTI_BANDI);
//		put(SEARCH, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI);
//		put(SEARCH_PROCEDURE, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_PROC_AGG);
//		put(SEARCH_BANDI_CON_ESITO, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_CON_ESITO);
//		// FNM gare privatistiche acquisto/vendita
//		put(LIST_ALL_ACQ_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_SCADUTI_BANDI);
//		put(LIST_ALL_ACQ_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_RICHIESTE_OFFERTA_BANDI);
//		put(SEARCH_PROCEDURE_ACQ, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_ACQ);
//		put(LIST_ALL_ACQ_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_ACQ_IN_CORSO_BANDI);
//		put(LIST_ALL_VEND_SCADUTI, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_SCADUTI_BANDI);
//		put(LIST_ALL_VEND_RICHIESTE_OFFERTA, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_RICHIESTE_OFFERTA_BANDI);
//		put(SEARCH_PROCEDURE_VEND, PortGareSystemConstants.SESSION_ID_SEARCH_BANDI_VEND);
//		put(LIST_ALL_VEND_IN_CORSO, PortGareSystemConstants.SESSION_ID_LIST_ALL_VEND_IN_CORSO_BANDI);
//	}};
	
}

