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
    
    public boolean isEsistente();

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

    public String getTipoSoggetto();

    public void setTipoSoggetto(String tipoSoggetto);

    public String getDataInizioIncarico();

    public void setDataInizioIncarico(String dataInizioIncarico);

    public String getDataFineIncarico();

    public void setDataFineIncarico(String dataFineIncarico);
    
    public String getQualifica();

    public void setQualifica(String qualifica);
    
    public String getResponsabileDichiarazioni();
    
    public void setResponsabileDichiarazioni(String responsabileDichiarazioni);

    public String getCognome();

    public void setCognome(String cognome);

    public String getNome();

    public void setNome(String nome);

    public String getTitolo();

    public void setTitolo(String titolo);

    public String getCodiceFiscale();

    public void setCodiceFiscale(String codiceFiscale);

    public String getSesso();

    public void setSesso(String sesso);

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

    public String getComuneNascita();

    public void setComuneNascita(String comuneNascita);

    public String getProvinciaNascita();

    public void setProvinciaNascita(String provinciaNascita);

    public String getDataNascita();

    public void setDataNascita(String dataNascita);

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

    public String getNote();

    public void setNote(String note);

	public void setSolaLettura(boolean solaLettura);
	
	public boolean isSolaLettura();

}
