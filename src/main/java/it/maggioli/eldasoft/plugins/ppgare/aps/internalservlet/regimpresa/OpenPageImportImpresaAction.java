package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

/**
 * Action di gestione dell'apertura della pagina di importazione dei dati
 * impresa oppure di editing dei dati principali dell'impresa nel wizard di
 * registrazione impresa.
 * 
 * @author
 * @since 2.3.0
 */
public class OpenPageImportImpresaAction extends EncodedDataAction {
	/**
     * UID
     */
	private static final long serialVersionUID = 7436548250185664099L;
	 
    /**
     * Tipo di formato del file XML da importare: file XML esportato da un Portale Appalti.
     */
	public static final String XML_IMPORT_PORTALE	= "PORTALE";

	/**
     * Tipo di formato del file XML da importare: file XML di un modello DGUE.
     */
	public static final String XML_IMPORT_DGUE		= "DGUE";
	
    /** Memorizza il tipo di importazione selezionata. */
	@Validate(EParamValidation.TIPO_IMPORT)
	private String tipoImport;

    public String getTipoImport() {
		return tipoImport;
	}

	public void setTipoImport(String tipoImport) {
		this.tipoImport = tipoImport;
	}

    /**
     * Apre la pagina per la selezione del file M-XML Portale da importare.
     */
    public String openPageFromPortale() {
       	this.tipoImport = XML_IMPORT_PORTALE;
    	return SUCCESS;
    }
    
    /**
     * Apre la pagina per la selezione del file XML DGUE da importare.
     */
    public String openPageFromDGUE() {
    	this.tipoImport = XML_IMPORT_DGUE;
    	return SUCCESS;    	
    }
    
    /**
     * Apre la pagina di registrazione dei dati principali dell'impresa.
     */
    public String openPageOnline() {
    	this.tipoImport = null;
    	return "successOnline";    	
    }
           
}
