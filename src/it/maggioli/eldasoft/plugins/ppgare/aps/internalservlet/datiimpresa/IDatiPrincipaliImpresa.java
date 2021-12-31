package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.io.Serializable;

/**
 * Interfaccia per la gestione dei dati principali relativi ad un'impresa, in
 * fase di registrazione o aggiornamento anagrafica
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public interface IDatiPrincipaliImpresa extends Serializable {

    public String getRagioneSociale();

    public void setRagioneSociale(String ragioneSociale);

    public String getNaturaGiuridica();

    public void setNaturaGiuridica(String naturaGiuridica);

    public String getTipoImpresa();

    public void setTipoImpresa(String tipoImpresa);

    public String getAmbitoTerritoriale();					// 1=operatore eco italiano, 2=operatore eco UE, 3=operatore eco extra UE

    public void setAmbitoTerritoriale(String tipoImpresa);

    public String getCodiceFiscale();

    public void setCodiceFiscale(String codiceFiscale);

    public String getPartitaIVA();

    public void setPartitaIVA(String partitaIVA);

    /**
     * @return microPiccolaMediaImpresa
     *            the microPiccolaMediaImpresa to set
     */
    public String getMicroPiccolaMediaImpresa();
    
    /**
     * @param microPiccolaMediaImpresa
     *            the microPiccolaMediaImpresa to set
     */
    public void setMicroPiccolaMediaImpresa(String microPiccolaMediaImpresa);

    /**
     * @return the indirizzoSedeLegale
     */
    public String getIndirizzoSedeLegale();

    /**
     * @param indirizzoSedeLegale
     *            the indirizzoSedeLegale to set
     */
    public void setIndirizzoSedeLegale(String indirizzoSedeLegale);

    /**
     * @return the numCivicoSedeLegale
     */
    public String getNumCivicoSedeLegale();

    /**
     * @param numCivicoSedeLegale
     *            the numCivicoSedeLegale to set
     */
    public void setNumCivicoSedeLegale(String numCivicoSedeLegale);

    /**
     * @return the capSedeLegale
     */
    public String getCapSedeLegale();

    /**
     * @param capSedeLegale
     *            the capSedeLegale to set
     */
    public void setCapSedeLegale(String capSedeLegale);

    /**
     * @return the comuneSedeLegale
     */
    public String getComuneSedeLegale();

    /**
     * @param comuneSedeLegale
     *            the comuneSedeLegale to set
     */
    public void setComuneSedeLegale(String comuneSedeLegale);

    /**
     * @return the provinciaSedeLegale
     */
    public String getProvinciaSedeLegale();

    /**
     * @param provinciaSedeLegale
     *            the provinciaSedeLegale to set
     */
    public void setProvinciaSedeLegale(String provinciaSedeLegale);

    /**
     * @return the nazioneSedeLegale
     */
    public String getNazioneSedeLegale();

    /**
     * @param nazioneSedeLegale
     *            the nazioneSedeLegale to set
     */
    public void setNazioneSedeLegale(String nazioneSedeLegale);

    /**
     * @return sitoWeb
     *            the sitoWeb to set
     */
    public String getSitoWeb();
    
    /**
     * @param sitoWeb
     *            the sitoWeb to set
     */
    public void setSitoWeb(String sitoWeb);

    /**
     * @return the telefonoRecapito
     */
    public String getTelefonoRecapito();

    /**
     * @param telefonoRecapito
     *            the telefonoRecapito to set
     */
    public void setTelefonoRecapito(String telefonoRecapito);

    /**
     * @return the faxRecapito
     */
    public String getFaxRecapito();

    /**
     * @param faxRecapito
     *            the faxRecapito to set
     */
    public void setFaxRecapito(String faxRecapito);

    /**
     * @return the cellulareRecapito
     */
    public String getCellulareRecapito();

    /**
     * @param cellulareRecapito
     *            the cellulareRecapito to set
     */
    public void setCellulareRecapito(String cellulareRecapito);

    /**
     * @return the emailRecapito
     */
    public String getEmailRecapito();

    /**
     * @param emailRecapito
     *            the emailRecapito to set
     */
    public void setEmailRecapito(String emailRecapito);

    /**
     * @return the emailRecapito
     */
    public String getEmailPECRecapito();

    /**
     * @param emailPECRecapito
     *            the emailPECRecapito to set
     */
    public void setEmailPECRecapito(String emailPECRecapito);

    public String getEmailRecapitoConferma();
    
    public void setEmailRecapitoConferma(String value);

    public String getEmailPECRecapitoConferma();
    
    public void setEmailPECRecapitoConferma(String value);
    
}