package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.io.Serializable;

/**
 * Interfaccia per la gestione dei dati relativi ulteriori (pi&ugrave; tecnici)
 * di un'impresa, in fase di registrazione o aggiornamento
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public interface IDatiUlterioriImpresa extends Serializable {
	
    public String getOggettoSociale();
    public void setOggettoSociale(String oggettoSociale);

	public String getIscrittoCCIAA();
	public void setIscrittoCCIAA(String iscrittoCCIAA);
	
	public String getNumRegistroDitteCCIAA();
	public void setNumRegistroDitteCCIAA(String numRegistroDitteCCIAA);

	public String getDataDomandaIscrizioneCCIAA();
	public void setDataDomandaIscrizioneCCIAA(String dataDomandaIscrizioneCCIAA);

	public String getNumIscrizioneCCIAA();
	public void setNumIscrizioneCCIAA(String numIscrizioneCCIAA);

	public String getDataIscrizioneCCIAA();
	public void setDataIscrizioneCCIAA(String dataIscrizioneCCIAA);

	public String getProvinciaIscrizioneCCIAA();
	public void setProvinciaIscrizioneCCIAA(String provinciaIscrizioneCCIAA);

	public String getDataNullaOstaAntimafiaCCIAA();
	public void setDataNullaOstaAntimafiaCCIAA(String dataNullaOstaAntimafiaCCIAA);

	public String getSoggettoNormativeDURC();
	public void setSoggettoNormativeDURC(String soggettoDURC);

	public String getSettoreProduttivoDURC();
	public void setSettoreProduttivoDURC(String settoreProduttivo);
	
	public String getNumIscrizioneINAIL();
	public void setNumIscrizioneINAIL(String numIscrizione);

	public String getDataIscrizioneINAIL();
	public void setDataIscrizioneINAIL(String dataIscrizione);

	public String getLocalitaIscrizioneINAIL();
	public void setLocalitaIscrizioneINAIL(String localitaIscrizione);

	public String getPosizAssicurativaINAIL();
	public void setPosizAssicurativaINAIL(String posizAssicurativaINAIL);

	public String getCodiceCNEL();
	public void setCodiceCNEL(String codice);
	
	public String getNumIscrizioneINPS();
	public void setNumIscrizioneINPS(String numIscrizione);

	public String getDataIscrizioneINPS();
	public void setDataIscrizioneINPS(String dataIscrizione);

	public String getLocalitaIscrizioneINPS();
	public void setLocalitaIscrizioneINPS(String localitaIscrizione);

	public String getPosizContributivaIndividualeINPS();
	public void setPosizContributivaIndividualeINPS(String posizContributivaIndividualeINPS);

	public String getNumIscrizioneCassaEdile();
	public void setNumIscrizioneCassaEdile(String numIscrizione);

	public String getDataIscrizioneCassaEdile();
	public void setDataIscrizioneCassaEdile(String dataIscrizione);

	public String getLocalitaIscrizioneCassaEdile();
	public void setLocalitaIscrizioneCassaEdile(String localitaIscrizione);

	public String getCodiceCassaEdile();
	public void setCodiceCassaEdile(String codice);

	public String getAltriIstitutiPrevidenziali();
	public void setAltriIstitutiPrevidenziali(String altriIstitutiPrevidenziali);

	public String getNumIscrizioneSOA();
	public void setNumIscrizioneSOA(String numIscrizioneSOA);

	public String getDataIscrizioneSOA();
	public void setDataIscrizioneSOA(String dataIscrizioneSOA);

	public String getDataUltimaRichiestaIscrizioneSOA();
	public void setDataUltimaRichiestaIscrizioneSOA(String dataUltimaRichiestaIscrizioneSOA);

	public String getDataScadenzaTriennaleSOA();
	public void setDataScadenzaTriennaleSOA(String dataScadenzaTriennaleSOA);

	public String getDataVerificaTriennaleSOA();
	public void setDataVerificaTriennaleSOA(String dataVerificaTriennaleSOA);

	public String getDataScadenzaIntermediaSOA();
	public void setDataScadenzaIntermediaSOA(String dataScadenzaIntermediaSOA);
	
	public String getDataScadenzaQuinquennaleSOA();
	public void setDataScadenzaQuinquennaleSOA(String dataScadenzaSOA);

	public String getOrganismoCertificatoreSOA();
	public void setOrganismoCertificatoreSOA(String organismoCertificatoreSOA);

	public String getNumIscrizioneISO();
	public void setNumIscrizioneISO(String numIscrizioneISO);

	public String getDataScadenzaISO();
	public void setDataScadenzaISO(String dataScadenzaISO);

	public String getOrganismoCertificatoreISO();
	public void setOrganismoCertificatoreISO(String organismoCertificatoreISO);

	public String getIscrittoWhitelistAntimafia();
	public void setIscrittoWhitelistAntimafia(String iscrito);

	public String getSedePrefetturaWhitelistAntimafia();
	public void setSedePrefetturaWhitelistAntimafia(String sedePrefettura);

	public String[] getSezioniIscrizioneWhitelistAntimafia();
	public void setSezioniIscrizioneWhitelistAntimafia(String[] sezioniIscrizione);
	
	public String getDataIscrizioneWhitelistAntimafia();
	public void setDataIscrizioneWhitelistAntimafia(String dataIscrizione);
	
	public String getDataScadenzaIscrizioneWhitelistAntimafia();
	public void setDataScadenzaIscrizioneWhitelistAntimafia(String dataScadenza);

	public String getAggiornamentoWhitelistAntimafia();
	public void setAggiornamentoWhitelistAntimafia(String aggiornamento);
	
	/**
	 * Iscrizione elenchi ricostruzione (DL 189/2016)
	 */
	public String getIscrittoAnagrafeAntimafiaEsecutori();
	public void setIscrittoAnagrafeAntimafiaEsecutori(String value);
	
	public String getDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori();
	public void setDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori(String value);
	
	public String getRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori();
	public void setRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori(String value);
	
	public String getIscrittoElencoSpecialeProfessionisti();
	public void setIscrittoElencoSpecialeProfessionisti(String value);
	
	/**
	 * Rating di legalità (DL 1/2012)
	 */
	public String getPossiedeRatingLegalita();
	public void setPossiedeRatingLegalita(String value);
	
	public String getRatingLegalita();
	public void setRatingLegalita(String value);
	
	public String getDataScadenzaPossessoRatingLegalita();
	public void setDataScadenzaPossessoRatingLegalita(String value);
	
	public String getAggiornamentoRatingLegalita();
	public void setAggiornamentoRatingLegalita(String value);
	
	public String getAltreCertificazioniAttestazioni();
	public void setAltreCertificazioniAttestazioni(String altreCertificazioniAttestazioni);

	/**
	 * Altri dati
	 */
	public String getCodiceIBANCCDedicato();
	public void setCodiceIBANCCDedicato(String codiceIBANCCDedicato);
	
	String getWithIBAN();
	void setWithIBAN(String withIBAN);
	
	String getNumeroConto();
	void setNumeroConto(String numeroConto);

	public String getCodiceBICCCDedicato();
	public void setCodiceBICCCDedicato(String codiceBICCCDedicato);

	public String getSoggettiAbilitatiCCDedicato();
	public void setSoggettiAbilitatiCCDedicato(String soggettiAbilitatiCCDedicato);
	
	public String getSocioUnico();
	public void setSocioUnico(String socioUnico);
	
	public String getRegimeFiscale();
	public void setRegimeFiscale(String regimeFiscale);	
	
	public String getSettoreAttivitaEconomica();
	public void setSettoreAttivitaEconomica(String settoreAttivitaEconomica);
	
	public String getDataScadenzaAbilitPreventiva();
	public void setDataScadenzaAbilitPreventiva(String dataScadAbilitPreventiva);

	public String getRinnovoAbilitPreventiva();
	public void setRinnovoAbilitPreventiva(String rinnovoAbilitPreventiva);

	public String getDataRichRinnovoAbilitPreventiva();
	public void setDataRichRinnovoAbilitPreventiva(String dataRichRinnovoAbilitPreventiva);

	public String[] getZoneAttivita();
	public void setZoneAttivita(String[] zoneAttivita);

	public String getAssunzioniObbligate();
	public void setAssunzioniObbligate(String assunzioniObbligate);

	public int[] getAnni();
	public void setAnni(int[] anni);

	public Integer[] getNumDipendenti();
	public void setNumDipendenti(Integer[] numDipendenti);
	
	public String getClasseDimensioneDipendenti();
	public void setClasseDimensioneDipendenti(String classeDimensioneDipendenti);
	
	public String getUlterioriDichiarazioni();
	public void setUlterioriDichiarazioni(String classeDimensioneDipendenti);
	
}
