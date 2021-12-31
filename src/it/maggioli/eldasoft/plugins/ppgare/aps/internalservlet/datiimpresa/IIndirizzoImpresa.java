package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.io.Serializable;

/**
 * Interfaccia per la gestione degli indirizzi di un'impresa, in
 * fase di registrazione o aggiornamento anagrafica
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public interface IIndirizzoImpresa extends Serializable {

    /**
     * @return the tipoIndirizzo
     */
    public String getTipoIndirizzo();

    /**
     * @param tipoIndirizzo
     *            the tipoIndirizzo to set
     */
    public void setTipoIndirizzo(String tipoIndirizzo);

    /**
     * @return the indirizzo
     */
    public String getIndirizzo();

    /**
     * @param indirizzo
     *            the indirizzo to set
     */
    public void setIndirizzo(String indirizzo);

    /**
     * @return the numCivico
     */
    public String getNumCivico();

    /**
     * @param numCivico
     *            the numCivico to set
     */
    public void setNumCivico(String numCivico);

    /**
     * @return the cap
     */
    public String getCap();

    /**
     * @param cap
     *            the cap to set
     */
    public void setCap(String cap);

    /**
     * @return the comune
     */
    public String getComune();

    /**
     * @param comune
     *            the comune to set
     */
    public void setComune(String comune);

    /**
     * @return the provincia
     */
    public String getProvincia();

    /**
     * @param provincia
     *            the provincia to set
     */
    public void setProvincia(String provincia);

    /**
     * @return the nazione
     */
    public String getNazione();

    /**
     * @param nazione
     *            the nazione to set
     */
    public void setNazione(String nazione);

    /**
     * @return the telefono
     */
    public String getTelefono();

    /**
     * @param telefono
     *            the telefono to set
     */
    public void setTelefono(String telefono);

    /**
     * @return the fax
     */
    public String getFax();

    /**
     * @param fax
     *            the fax to set
     */
    public void setFax(String fax);

}
