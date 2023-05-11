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

	public String getTitolo();

	public void setTitolo(String titolo);

	public String getNome();

	public void setNome(String nome);
	
	public String getCognome();

	public void setCognome(String cognome);
	
	public String getDataNascita();

	public void setDataNascita(String dataNascita);

	public String getComuneNascita();

	public void setComuneNascita(String comuneNascita);

	public String getProvinciaNascita();

	public void setProvinciaNascita(String provinciaNascita);

	public String getSesso();

	public void setSesso(String sesso);

	public String getTipologiaAlboProf();

	public void setTipologiaAlboProf(String tipologiaAlboProf);

	public String getNumIscrizioneAlboProf();

	public void setNumIscrizioneAlboProf(String numIscrizioneAlboProf);

	public String getDataIscrizioneAlboProf();

	public void setDataIscrizioneAlboProf(String dataIscrizioneAlboProf);

	public String getProvinciaIscrizioneAlboProf();

	public void setProvinciaIscrizioneAlboProf(String provinciaIscrizioneAlboProf);

	public String getTipologiaCassaPrevidenza();

	public void setTipologiaCassaPrevidenza(String tipologiaCassaPrevidenza);

	public String getNumMatricolaCassaPrevidenza();

	public void setNumMatricolaCassaPrevidenza(String numMatricolaCassaPrevidenza);

}