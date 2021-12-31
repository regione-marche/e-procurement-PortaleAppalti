package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.io.Serializable;

/**
 * Interfaccia per la gestione dei soggetti di un'impresa, in
 * fase di registrazione o aggiornamento anagrafica
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public interface ISoggettoImpresa extends Serializable {
    
    /**
     * @return the esistente
     */
    public boolean isEsistente();

    /**
     * @param esistente the esistente to set
     */
    public void setEsistente(boolean esistente);

	/**
	 * Campo usato per la sola interfaccia WEB e necessario per proporre le
	 * diverse sottotipologie di soggetti previsti per le altre cariche ed i
	 * collaboratori oltre che le due tipologie legale rappresentante e
	 * direttore tecnico.
	 * 
	 * @return campo composto tipoSoggetto + "-" + qualifica
	 */
    public String getSoggettoQualifica();

	/**
	 * Imposta il campo usato nell'interfaccia WEB. Tale metodo deve settare i
	 * campi tipoSoggetto e qualifica.
	 * 
	 * @param soggettoQualifica
	 *            valore impostato
	 */
	public void setSoggettoQualifica(String soggettoQualifica);

	/**
     * @return the tipoSoggetto
     */
    public String getTipoSoggetto();

    /**
     * @param tipoIncarico the tipoSoggetto to set
     */
    public void setTipoSoggetto(String tipoSoggetto);

    /**
     * @return the dataInizioIncarico
     */
    public String getDataInizioIncarico();

    /**
     * @param dataInizioIncarico the dataInizioIncarico to set
     */
    public void setDataInizioIncarico(String dataInizioIncarico);

    /**
     * @return the dataFineIncarico
     */
    public String getDataFineIncarico();

    /**
     * @param dataFineIncarico the dataFineIncarico to set
     */
    public void setDataFineIncarico(String dataFineIncarico);
    
    /**
     * @return the qualifica
     */
    public String getQualifica();

    /**
     * @param qualifica
     *            the qualifica to set
     */
    public void setQualifica(String qualifica);
    
    /**
     * @return the responsabileDichiarazioni
     */
    public String getResponsabileDichiarazioni();
    
    /**
     * @param responsabileDichiarazioni
     *            the responsabileDichiarazioni to set
     */
    public void setResponsabileDichiarazioni(String responsabileDichiarazioni);

    /**
     * @return the cognome
     */
    public String getCognome();

    /**
     * @param cognome
     *            the cognome to set
     */
    public void setCognome(String cognome);

    /**
     * @return the nome
     */
    public String getNome();

    /**
     * @param nome
     *            the nome to set
     */
    public void setNome(String nome);

    /**
     * @return the titolo
     */
    public String getTitolo();

    /**
     * @param titolo
     *            the titolo to set
     */
    public void setTitolo(String titolo);

    /**
     * @return the codiceFiscale
     */
    public String getCodiceFiscale();

    /**
     * @param codiceFiscale
     *            the codiceFiscale to set
     */
    public void setCodiceFiscale(String codiceFiscale);

    /**
     * @return the sesso
     */
    public String getSesso();

    /**
     * @param sesso
     *            the sesso to set
     */
    public void setSesso(String sesso);

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
     * @return the comuneNascita
     */
    public String getComuneNascita();

    /**
     * @param comuneNascita
     *            the comuneNascita to set
     */
    public void setComuneNascita(String comuneNascita);

    /**
     * @return the provinciaNascita
     */
    public String getProvinciaNascita();

    /**
     * @param provinciaNascita
     *            the provinciaNascita to set
     */
    public void setProvinciaNascita(String provinciaNascita);

    /**
     * @return the dataNascita
     */
    public String getDataNascita();

    /**
     * @param dataNascita
     *            the dataNascita to set
     */
    public void setDataNascita(String dataNascita);

	/**
	 * @return the tipologiaAlboProf
	 */
	public String getTipologiaAlboProf();

	/**
	 * @param tipologiaAlboProf
	 *            the tipologiaAlboProf to set
	 */
	public void setTipologiaAlboProf(String tipologiaAlboProf);

	/**
	 * @return the numIscrizioneAlboProf
	 */
	public String getNumIscrizioneAlboProf();

	/**
	 * @param numIscrizioneAlboProf
	 *            the numIscrizioneAlboProf to set
	 */
	public void setNumIscrizioneAlboProf(String numIscrizioneAlboProf);

	/**
	 * @return the dataIscrizioneAlboProf
	 */
	public String getDataIscrizioneAlboProf();

	/**
	 * @param dataIscrizioneAlboProf
	 *            the dataIscrizioneAlboProf to set
	 */
	public void setDataIscrizioneAlboProf(String dataIscrizioneAlboProf);

	/**
	 * @return the provinciaIscrizioneAlboProf
	 */
	public String getProvinciaIscrizioneAlboProf();

	/**
	 * @param provinciaIscrizioneAlboProf
	 *            the provinciaIscrizioneAlboProf to set
	 */
	public void setProvinciaIscrizioneAlboProf(
			String provinciaIscrizioneAlboProf);

	/**
	 * @return the tipologiaCassaPrevidenza
	 */
	public String getTipologiaCassaPrevidenza();

	/**
	 * @param tipologiaCassaPrevidenza
	 *            the tipologiaCassaPrevidenza to set
	 */
	public void setTipologiaCassaPrevidenza(
			String tipologiaCassaPrevidenza);

	/**
	 * @return the numMatricolaCassaPrevidenza
	 */
	public String getNumMatricolaCassaPrevidenza();

	/**
	 * @param numMatricolaCassaPrevidenza
	 *            the numMatricolaCassaPrevidenza to set
	 */
	public void setNumMatricolaCassaPrevidenza(
			String numMatricolaCassaPrevidenza);

	/**
     * @return the note
     */
    public String getNote();

    /**
     * @param note
     *            the note to set
     */
    public void setNote(String note);

	public void setSolaLettura(boolean solaLettura);
	
	public boolean isSolaLettura();

}
