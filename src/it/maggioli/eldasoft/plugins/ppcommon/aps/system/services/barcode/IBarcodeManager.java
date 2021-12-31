package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.barcode;

import java.io.InputStream;
import java.io.OutputStream;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia per la gestione della generazione di codici a barre
 * 
 * @version 1.1
 * @author Stefano.Sabbadin
 */
public interface IBarcodeManager {

    /**
     * Genera il timbro digitale (codice a barre o altro formato di codifica
     * digitale) relativo ad un messaggio testuale, a partire da un file di
     * configurazione contenente le impostazioni da utilizzare per la
     * generazione.
     * 
     * @param configFile
     *            stream di input per il file di configurazione
     * @param out
     *            stream di output per l'immagine contenente l'output
     *            dell'elaborazione
     * @param message
     *            messaggio da inserire nel timbro
     */
    public void generateBarcode(InputStream configFile, OutputStream out,
	    String message) throws ApsException;

}
