package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

/**
 * Classe astratta da utilizzare come base per ogni action di apertura pagina di
 * un wizard.<br/>
 * Sostanzialmente estende EncodedDataAction in modo da usufruire
 * dell'interceptor per il caricamento delle mappe di tabellati usabili nella
 * pagina, ed implementa l'interfaccia in modo da usufruire della sessione.
 * 
 * @author stefano.sabbadin
 * @since 1.2
 */
public abstract class AbstractOpenPageAction extends EncodedDataAction
	implements SessionAware {

    /**
     * UID
     */
    private static final long serialVersionUID = -2656722187910716L;

    protected Map<String, Object> session;

    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    /**
     * Esegue controlli ed inizializzazioni necessarie all'apertura della pagina di uno step della procedura guidata.
     */
    public abstract String openPage();

}
