package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

/**
 * Interfaccia per la gestione dei dati relativi ad un'impresa, in fase di
 * registrazione o iscrizione albo
 * 
 * @author Stefano.Sabbadin
 */
public interface IDatiImpresa {

    /**
     * @return the ragioneSociale
     */
    public String getRagioneSociale();

    /**
     * @param ragioneSociale
     *            the ragioneSociale to set
     */
    public void setRagioneSociale(String ragioneSociale);

    /**
     * @return the naturaGiuridica
     */
    public String getNaturaGiuridica();

    /**
     * @param naturaGiuridica
     *            the naturaGiuridica to set
     */
    public void setNaturaGiuridica(String naturaGiuridica);

    /**
     * @return the tipoImpresa
     */
    public String getTipoImpresa();

    /**
     * @param tipoImpresa
     *            the tipoImpresa to set
     */
    public void setTipoImpresa(String tipoImpresa);

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
     * @return the partitaIVA
     */
    public String getPartitaIVA();

    /**
     * @param partitaIVA
     *            the partitaIVA to set
     */
    public void setPartitaIVA(String partitaIVA);

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

//    /**
//     * @return the modalitaComunicazioneRecapito
//     */
//    public String getModalitaComunicazioneRecapito();
//
//    /**
//     * @param modalitaComunicazioneRecapito
//     *            the modalitaComunicazioneRecapito to set
//     */
//    public void setModalitaComunicazioneRecapito(
//	    String modalitaComunicazioneRecapito);

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

    /**
     * @return the numRegistroDitteCCIAA
     */
    public String getNumRegistroDitteCCIAA();

    /**
     * @param numRegistroDitteCCIAA
     *            the numRegistroDitteCCIAA to set
     */
    public void setNumRegistroDitteCCIAA(String numRegistroDitteCCIAA);

    /**
     * @return the dataDomandaIscrizioneCCIAA
     */
    public String getDataDomandaIscrizioneCCIAA();

    /**
     * @param dataDomandaIscrizioneCCIAA
     *            the dataDomandaIscrizioneCCIAA to set
     */
    public void setDataDomandaIscrizioneCCIAA(String dataDomandaIscrizioneCCIAA);

    /**
     * @return the numIscrizioneCCIAA
     */
    public String getNumIscrizioneCCIAA();

    /**
     * @param numIscrizioneCCIAA
     *            the numIscrizioneCCIAA to set
     */
    public void setNumIscrizioneCCIAA(String numIscrizioneCCIAA);

    /**
     * @return the dataIscrizioneCCIAA
     */
    public String getDataIscrizioneCCIAA();

    /**
     * @param dataIscrizioneCCIAA
     *            the dataIscrizioneCCIAA to set
     */
    public void setDataIscrizioneCCIAA(String dataIscrizioneCCIAA);

    /**
     * @return the provinciaIscrizioneCCIAA
     */
    public String getProvinciaIscrizioneCCIAA();

    /**
     * @param provinciaIscrizioneCCIAA
     *            the provinciaIscrizioneCCIAA to set
     */
    public void setProvinciaIscrizioneCCIAA(String provinciaIscrizioneCCIAA);
    
    /**
     * @return the dataNullaOstaAntimafiaCCIAA
     */
    public String getDataNullaOstaAntimafiaCCIAA();

    /**
     * @param dataNullaOstaAntimafiaCCIAA
     *            the dataNullaOstaAntimafiaCCIAA to set
     */
    public void setDataNullaOstaAntimafiaCCIAA(String dataNullaOstaAntimafiaCCIAA);

    /**
     * @return the numIscrizioneSOA
     */
    public String getNumIscrizioneSOA();

    /**
     * @param numIscrizioneSOA
     *            the numIscrizioneSOA to set
     */
    public void setNumIscrizioneSOA(String numIscrizioneSOA);

    /**
     * @return the dataIscrizioneSOA
     */
    public String getDataIscrizioneSOA();

    /**
     * @param dataIscrizioneSOA
     *            the dataIscrizioneSOA to set
     */
    public void setDataIscrizioneSOA(String dataIscrizioneSOA);

    /**
     * @return the dataScadenzaSOA
     */
    public String getDataScadenzaSOA();

    /**
     * @param dataScadenzaSOA
     *            the dataScadenzaSOA to set
     */
    public void setDataScadenzaSOA(String dataScadenzaSOA);

    /**
     * @return the dataUltimaRichiestaIscrizioneSOA
     */
    public String getDataUltimaRichiestaIscrizioneSOA();

    /**
     * @param dataUltimaRichiestaIscrizioneSOA
     *            the dataUltimaRichiestaIscrizioneSOA to set
     */
    public void setDataUltimaRichiestaIscrizioneSOA(
	    String dataUltimaRichiestaIscrizioneSOA);
    
    /**
     * @return the organismoCertificatoreSOA
     */
    public String getOrganismoCertificatoreSOA();

    /**
     * @param organismoCertificatoreSOA
     *            the organismoCertificatoreSOA to set
     */
    public void setOrganismoCertificatoreSOA(
	    String organismoCertificatoreSOA);
    
    /**
     * @return the numIscrizioneISO
     */
    public String getNumIscrizioneISO();

    /**
     * @param numIscrizioneISO
     *            the numIscrizioneISO to set
     */
    public void setNumIscrizioneISO(String numIscrizioneISO);

    /**
     * @return the dataScadenzaISO
     */
    public String getDataScadenzaISO();

    /**
     * @param dataScadenzaISO
     *            the dataScadenzaISO to set
     */
    public void setDataScadenzaISO(String dataScadenzaISO);

    /**
     * @return the organismoCertificatoreISO
     */
    public String getOrganismoCertificatoreISO();

    /**
     * @param organismoCertificatoreISO
     *            the organismoCertificatoreISO to set
     */
    public void setOrganismoCertificatoreISO(
	    String organismoCertificatoreISO);

    /**
     * @return the dataScadAbilitPreventiva
     */
    public String getDataScadenzaAbilitPreventiva();

    /**
     * @param dataScadAbilitPreventiva
     *            the dataScadAbilitPreventiva to set
     */
    public void setDataScadenzaAbilitPreventiva(String dataScadAbilitPreventiva);

    /**
     * @return the rinnovoAbilitPreventiva
     */
    public String getRinnovoAbilitPreventiva();

    /**
     * @param rinnovoAbilitPreventiva
     *            the rinnovoAbilitPreventiva to set
     */
    public void setRinnovoAbilitPreventiva(String rinnovoAbilitPreventiva);

    /**
     * @return the dataRichRinnovoAbilitPreventiva
     */
    public String getDataRichRinnovoAbilitPreventiva();

    /**
     * @param dataRichRinnovoAbilitPreventiva
     *            the dataRichRinnovoAbilitPreventiva to set
     */
    public void setDataRichRinnovoAbilitPreventiva(String dataRichRinnovoAbilitPreventiva);

    /**
     * @return the volumeAffari
     */
    public String getVolumeAffari();

    /**
     * @param volumeAffari
     *            the volumeAffari to set
     */
    public void setVolumeAffari(String volumeAffari);

    /**
     * @return the zoneAttivita
     */
    public String[] getZoneAttivita();

    /**
     * @param zoneAttivita
     *            the zoneAttivita to set
     */
    public void setZoneAttivita(String[] zoneAttivita);

    /**
     * @return the cognomeLR
     */
    public String getCognomeLR();

    /**
     * @param cognomeLR
     *            the cognomeLR to set
     */
    public void setCognomeLR(String cognomeLR);

    /**
     * @return the nomeLR
     */
    public String getNomeLR();

    /**
     * @param nomeLR
     *            the nomeLR to set
     */
    public void setNomeLR(String nomeLR);

    /**
     * @return the codiceFiscaleLR
     */
    public String getCodiceFiscaleLR();

    /**
     * @param codiceFiscaleLR
     *            the codiceFiscaleLR to set
     */
    public void setCodiceFiscaleLR(String codiceFiscaleLR);

    /**
     * @return the partitaIVALR
     */
    public String getPartitaIVALR();

    /**
     * @param partitaIVALR
     *            the partitaIVALR to set
     */
    public void setPartitaIVALR(String partitaIVALR);

    /**
     * @return the sessoLR
     */
    public String getSessoLR();

    /**
     * @param sessoLR
     *            the sessoLR to set
     */
    public void setSessoLR(String sessoLR);

    /**
     * @return the indirizzoLR
     */
    public String getIndirizzoLR();

    /**
     * @param indirizzoLR
     *            the indirizzoLR to set
     */
    public void setIndirizzoLR(String indirizzoLR);

    /**
     * @return the numCivicoLR
     */
    public String getNumCivicoLR();

    /**
     * @param numCivicoLR
     *            the numCivicoLR to set
     */
    public void setNumCivicoLR(String numCivicoLR);

    /**
     * @return the capLR
     */
    public String getCapLR();

    /**
     * @param capLR
     *            the capLR to set
     */
    public void setCapLR(String capLR);

    /**
     * @return the comuneLR
     */
    public String getComuneLR();

    /**
     * @param comuneLR
     *            the comuneLR to set
     */
    public void setComuneLR(String comuneLR);

    /**
     * @return the provinciaLR
     */
    public String getProvinciaLR();

    /**
     * @param provinciaLR
     *            the provinciaLR to set
     */
    public void setProvinciaLR(String provinciaLR);

    /**
     * @return the nazioneLR
     */
    public String getNazioneLR();

    /**
     * @param nazioneLR
     *            the nazioneLR to set
     */
    public void setNazioneLR(String nazioneLR);

    /**
     * @return the comuneNascitaLR
     */
    public String getComuneNascitaLR();

    /**
     * @param comuneNascitaLR
     *            the comuneNascitaLR to set
     */
    public void setComuneNascitaLR(String comuneNascitaLR);

    /**
     * @return the dataNascitaLR
     */
    public String getDataNascitaLR();

    /**
     * @param dataNascitaLR
     *            the dataNascitaLR to set
     */
    public void setDataNascitaLR(String dataNascitaLR);

    /**
     * @return the cognomeDT
     */
    public String getCognomeDT();

    /**
     * @param cognomeDT
     *            the cognomeDT to set
     */
    public void setCognomeDT(String cognomeDT);

    /**
     * @return the nomeDT
     */
    public String getNomeDT();

    /**
     * @param nomeDT
     *            the nomeDT to set
     */
    public void setNomeDT(String nomeDT);

    /**
     * @return the codiceFiscaleDT
     */
    public String getCodiceFiscaleDT();

    /**
     * @param codiceFiscaleDT
     *            the codiceFiscaleDT to set
     */
    public void setCodiceFiscaleDT(String codiceFiscaleDT);

    /**
     * @return the partitaIVADT
     */
    public String getPartitaIVADT();

    /**
     * @param partitaIVADT
     *            the partitaIVADT to set
     */
    public void setPartitaIVADT(String partitaIVADT);

    /**
     * @return the sessoDT
     */
    public String getSessoDT();

    /**
     * @param sessoDT
     *            the sessoDT to set
     */
    public void setSessoDT(String sessoDT);

    /**
     * @return the indirizzoDT
     */
    public String getIndirizzoDT();

    /**
     * @param indirizzoDT
     *            the indirizzoDT to set
     */
    public void setIndirizzoDT(String indirizzoDT);

    /**
     * @return the numCivicoDT
     */
    public String getNumCivicoDT();

    /**
     * @param numCivicoDT
     *            the numCivicoDT to set
     */
    public void setNumCivicoDT(String numCivicoDT);

    /**
     * @return the capDT
     */
    public String getCapDT();

    /**
     * @param capDT
     *            the capDT to set
     */
    public void setCapDT(String capDT);

    /**
     * @return the comuneDT
     */
    public String getComuneDT();

    /**
     * @param comuneDT
     *            the comuneDT to set
     */
    public void setComuneDT(String comuneDT);

    /**
     * @return the provinciaDT
     */
    public String getProvinciaDT();

    /**
     * @param provinciaDT
     *            the provinciaDT to set
     */
    public void setProvinciaDT(String provinciaDT);

    /**
     * @return the nazioneDT
     */
    public String getNazioneDT();

    /**
     * @param nazioneDT
     *            the nazioneDT to set
     */
    public void setNazioneDT(String nazioneDT);

    /**
     * @return the comuneNascitaDT
     */
    public String getComuneNascitaDT();

    /**
     * @param comuneNascitaDT
     *            the comuneNascitaDT to set
     */
    public void setComuneNascitaDT(String comuneNascitaDT);

    /**
     * @return the dataNascitaDT
     */
    public String getDataNascitaDT();

    /**
     * @param dataNascitaDT
     *            the dataNascitaDT to set
     */
    public void setDataNascitaDT(String dataNascitaDT);

}