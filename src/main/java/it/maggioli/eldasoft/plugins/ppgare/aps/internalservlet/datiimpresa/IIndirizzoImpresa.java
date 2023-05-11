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

    public String getTipoIndirizzo();

    public void setTipoIndirizzo(String tipoIndirizzo);

    public String getIndirizzo();

    public void setIndirizzo(String indirizzo);

    public String getNumCivico();

    public void setNumCivico(String numCivico);

    public String getCap();

    public void setCap(String cap);

    public String getComune();

    public void setComune(String comune);

    public String getProvincia();

    public void setProvincia(String provincia);

    public String getNazione();

    public void setNazione(String nazione);

    public String getTelefono();

    public void setTelefono(String telefono);

    public String getFax();

    public void setFax(String fax);

}
