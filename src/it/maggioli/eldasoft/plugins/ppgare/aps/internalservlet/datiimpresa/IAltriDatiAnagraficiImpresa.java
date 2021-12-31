package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.io.Serializable;

/**
 * Interfaccia per la gestione degli ulteriori dati anagrafici di un libero
 * professionista, in fase di registrazione o aggiornamento anagrafica.
 * 
 * @author Stefano.Sabbadin
 * @since 1.6
 */
public interface IAltriDatiAnagraficiImpresa extends Serializable {

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
	 * @return the nome
	 */
	public String getNome();

	/**
	 * @param nome
	 *            the nome to set
	 */
	public void setNome(String nome);
	
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
	 * @return the dataNascita
	 */
	public String getDataNascita();

	/**
	 * @param dataNascita
	 *            the dataNascita to set
	 */
	public void setDataNascita(String dataNascita);

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
	 * @return the sesso
	 */
	public String getSesso();

	/**
	 * @param sesso
	 *            the sesso to set
	 */
	public void setSesso(String sesso);

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

}