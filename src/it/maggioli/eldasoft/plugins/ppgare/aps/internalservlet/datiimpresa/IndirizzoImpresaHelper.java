package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.IndirizzoEstesoAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoEstesoType;

/**
 * Bean di memorizzazione di un singolo indirizzo di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class IndirizzoImpresaHelper implements IIndirizzoImpresa {

    /**
	 * UID
	 */
	private static final long serialVersionUID = -3844548637225530054L;

	private String tipoIndirizzo;
    private String indirizzo;
    private String numCivico;
    private String cap;
    private String comune;
    private String provincia;
    private String nazione;
    private String telefono;
    private String fax;

    public IndirizzoImpresaHelper() {
	this.tipoIndirizzo = null;
	this.indirizzo = null;
	this.numCivico = null;
	this.cap = null;
	this.comune = null;
	this.provincia = null;
	this.nazione = "Italia";
	this.telefono = null;
	this.fax = null;
    }

    public IndirizzoImpresaHelper(IndirizzoEstesoAggiornabileType indirizzo) {
	this.tipoIndirizzo = indirizzo.getTipoIndirizzo();
	this.indirizzo = indirizzo.getIndirizzo();
	this.numCivico = indirizzo.getNumCivico();
	this.cap = indirizzo.getCap();
	this.comune = indirizzo.getComune();
	this.provincia = indirizzo.getProvincia();
	this.nazione = indirizzo.getNazione();
	this.telefono = indirizzo.getTelefono();
	this.fax = indirizzo.getFax();
    }

    public IndirizzoImpresaHelper(IndirizzoEstesoType indirizzo) {
	this.tipoIndirizzo = indirizzo.getTipoIndirizzo();
	this.indirizzo = indirizzo.getIndirizzo();
	this.numCivico = indirizzo.getNumCivico();
	this.cap = indirizzo.getCap();
	this.comune = indirizzo.getComune();
	this.provincia = indirizzo.getProvincia();
	this.nazione = indirizzo.getNazione();
	this.telefono = indirizzo.getTelefono();
	this.fax = indirizzo.getFax();
    }

    /**
     * @return the tipoIndirizzo
     */
    public String getTipoIndirizzo() {
	return tipoIndirizzo;
    }

    /**
     * @param tipoIndirizzo
     *            the tipoIndirizzo to set
     */
    public void setTipoIndirizzo(String tipoIndirizzo) {
	this.tipoIndirizzo = tipoIndirizzo;
    }

    /**
     * @return the indirizzo
     */
    public String getIndirizzo() {
	return indirizzo;
    }

    /**
     * @param indirizzo
     *            the indirizzo to set
     */
    public void setIndirizzo(String indirizzo) {
	this.indirizzo = indirizzo;
    }

    /**
     * @return the numCivico
     */
    public String getNumCivico() {
	return numCivico;
    }

    /**
     * @param numCivico
     *            the numCivico to set
     */
    public void setNumCivico(String numCivico) {
	this.numCivico = numCivico;
    }

    /**
     * @return the cap
     */
    public String getCap() {
	return cap;
    }

    /**
     * @param cap
     *            the cap to set
     */
    public void setCap(String cap) {
	this.cap = cap;
    }

    /**
     * @return the comune
     */
    public String getComune() {
	return comune;
    }

    /**
     * @param comune
     *            the comune to set
     */
    public void setComune(String comune) {
	this.comune = comune;
    }

    /**
     * @return the provincia
     */
    public String getProvincia() {
	return provincia;
    }

    /**
     * @param provincia
     *            the provincia to set
     */
    public void setProvincia(String provincia) {
	this.provincia = provincia;
    }

    /**
     * @return the nazione
     */
    public String getNazione() {
	return nazione;
    }

    /**
     * @param nazione
     *            the nazione to set
     */
    public void setNazione(String nazione) {
	this.nazione = nazione;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
	return telefono;
    }

    /**
     * @param telefono
     *            the telefono to set
     */
    public void setTelefono(String telefono) {
	this.telefono = telefono;
    }

    /**
     * @return the fax
     */
    public String getFax() {
	return fax;
    }

    /**
     * @param fax
     *            the fax to set
     */
    public void setFax(String fax) {
	this.fax = fax;
    }

}
