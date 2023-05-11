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

    String getRagioneSociale();

    void setRagioneSociale(String ragioneSociale);

    String getNaturaGiuridica();

    void setNaturaGiuridica(String naturaGiuridica);

    String getTipoImpresa();

    void setTipoImpresa(String tipoImpresa);

    String getAmbitoTerritoriale();					// 1=operatore eco italiano, 2=operatore eco UE o extra UE

    void setAmbitoTerritoriale(String tipoImpresa);

    String getCodiceFiscale();

    void setCodiceFiscale(String codiceFiscale);

    String getPartitaIVA();

    void setPartitaIVA(String partitaIVA);

    String getMicroPiccolaMediaImpresa();
    
    void setMicroPiccolaMediaImpresa(String microPiccolaMediaImpresa);

    String getIndirizzoSedeLegale();

    void setIndirizzoSedeLegale(String indirizzoSedeLegale);

    String getNumCivicoSedeLegale();

    void setNumCivicoSedeLegale(String numCivicoSedeLegale);

    String getCapSedeLegale();

    void setCapSedeLegale(String capSedeLegale);

    String getComuneSedeLegale();

    void setComuneSedeLegale(String comuneSedeLegale);

    String getProvinciaSedeLegale();

    void setProvinciaSedeLegale(String provinciaSedeLegale);

    String getNazioneSedeLegale();

    void setNazioneSedeLegale(String nazioneSedeLegale);

    String getSitoWeb();
    
    void setSitoWeb(String sitoWeb);

    String getTelefonoRecapito();

    void setTelefonoRecapito(String telefonoRecapito);

    String getFaxRecapito();

    void setFaxRecapito(String faxRecapito);

    String getCellulareRecapito();

    void setCellulareRecapito(String cellulareRecapito);

    String getEmailRecapito();

    void setEmailRecapito(String emailRecapito);

    String getEmailPECRecapito();

    void setEmailPECRecapito(String emailPECRecapito);

    String getEmailRecapitoConferma();
    
    void setEmailRecapitoConferma(String value);

    String getEmailPECRecapitoConferma();
    
    void setEmailPECRecapitoConferma(String value);
    
    void setVatGroup(String vatGroup);
    
    String getVatGroup();

    String getTipoSocietaCooperativa();
    void setTipoSocietaCooperativa(String tipoSocietaCooperativa);

}