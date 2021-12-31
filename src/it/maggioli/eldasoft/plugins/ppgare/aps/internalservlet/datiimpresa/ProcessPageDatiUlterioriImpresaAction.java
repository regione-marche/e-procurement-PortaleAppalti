package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import org.apache.commons.lang.StringUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei dati ulteriori
 * dell'impresa nel wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageDatiUlterioriImpresaAction extends
				AbstractProcessPageAction implements IDatiUlterioriImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 23179258092441689L;
	

	private String iscrittoCCIAA;
	private String numRegistroDitteCCIAA;
	private String dataDomandaIscrizioneCCIAA;
	private String numIscrizioneCCIAA;
	private String dataIscrizioneCCIAA;
	private String provinciaIscrizioneCCIAA;
	private String dataNullaOstaAntimafiaCCIAA;
	private String soggettoNormativeDURC;
	private String settoreProduttivoDURC;
	private String numIscrizioneINPS;
	private String dataIscrizioneINPS;
	private String localitaIscrizioneINPS;
	private String posizContributivaIndividualeINPS;
	private String numIscrizioneINAIL;
	private String dataIscrizioneINAIL;
	private String localitaIscrizioneINAIL;
	private String posizAssicurativaINAIL;
	private String codiceCassaEdile;
	private String numIscrizioneCassaEdile;
	private String dataIscrizioneCassaEdile;
	private String localitaIscrizioneCassaEdile;
	private String altriIstitutiPrevidenziali;
	private String numIscrizioneSOA;
	private String dataIscrizioneSOA;
	private String dataUltimaRichiestaIscrizioneSOA;
	private String dataScadenzaTriennaleSOA;
	private String dataVerificaTriennaleSOA;
	private String dataScadenzaIntermediaSOA;
	private String dataScadenzaQuinquennaleSOA;
	private String organismoCertificatoreSOA;
	private String numIscrizioneISO;
	private String dataScadenzaISO;
	private String organismoCertificatoreISO;
	private String iscrittoWhitelistAntimafia;
	private String sedePrefetturaWhitelistAntimafia;
	private String[] sezioniIscrizioneWhitelistAntimafia;
	private String dataIscrizioneWhitelistAntimafia;
	private String dataScadenzaIscrizioneWhitelistAntimafia;
	private String aggiornamentoWhitelistAntimafia;
	private String iscrittoAnagrafeAntimafiaEsecutori;
	private String dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori;
	private String rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori;
	private String iscrittoElencoSpecialeProfessionisti;
	private String possiedeRatingLegalita;
	private String ratingLegalita;
	private String dataScadenzaPossessoRatingLegalita;
	private String aggiornamentoRatingLegalita;
	private String altreCertificazioniAttestazioni;
//	private String estremiCCDedicato;
	private String codiceIBANCCDedicato;
	private String codiceBICCCDedicato;
	private String soggettiAbilitatiCCDedicato;	
	private String socioUnico;
	private String regimeFiscale;
	private String settoreAttivitaEconomica;
	private String dataScadenzaAbilitPreventiva;
	private String dataRichRinnovoAbilitPreventiva;
	private String rinnovoAbilitPreventiva;
	private String[] zoneAttivita;
	private String assunzioniObbligate;
	private int[] anni;
	private Integer[] numDipendenti;
	private String[] strNumDipendenti;
	private String classeDimensioneDipendenti;
	private String ulterioriDichiarazioni;
	private String oggettoSociale;
	private boolean obblSezCCIAA;
	private boolean obblDataNullaOstaAntimafiaCCIAA;
	private boolean visSezAbilitPreventiva;
	private boolean obblDataScadenzaAbilitPreventiva;
	private boolean obblZoneAttivita;
	private Boolean obblSoggettoNormativeDURC;
	private Boolean obblAssunzioniObbligate;
	private Boolean obblDipendentiTriennio;
	private Boolean obblClasseDimensione;
	private boolean obblSettoreAttivitaEconomica;
	private boolean obblIscrittoAnagrafeAntimafiaEsecutori;
	private boolean obblPossiedeRatingLegalita;
	private boolean obblOggettoSociale;
	private boolean liberoProfessionista;
	private String tipoImpresa;


	@Override
	public String getIscrittoCCIAA() {
		return this.iscrittoCCIAA;
	}

	@Override
	public void setIscrittoCCIAA(String iscrittoCCIAA) {
		this.iscrittoCCIAA = iscrittoCCIAA;
	}
	
	@Override
	public String getNumRegistroDitteCCIAA() {
		return numRegistroDitteCCIAA;
	}

	@Override
	public void setNumRegistroDitteCCIAA(String numRegistroDitteCCIAA) {
		this.numRegistroDitteCCIAA = numRegistroDitteCCIAA.trim();
	}

	@Override
	public String getDataDomandaIscrizioneCCIAA() {
		return dataDomandaIscrizioneCCIAA;
	}

	@Override
	public void setDataDomandaIscrizioneCCIAA(String dataDomandaIscrizioneCCIAA) {
		this.dataDomandaIscrizioneCCIAA = dataDomandaIscrizioneCCIAA;
	}

	@Override
	public String getNumIscrizioneCCIAA() {
		return numIscrizioneCCIAA;
	}

	@Override
	public void setNumIscrizioneCCIAA(String numIscrizioneCCIAA) {
		this.numIscrizioneCCIAA = numIscrizioneCCIAA.trim();
	}

	@Override
	public String getDataIscrizioneCCIAA() {
		return dataIscrizioneCCIAA;
	}

	@Override
	public void setDataIscrizioneCCIAA(String dataIscrizioneCCIAA) {
		this.dataIscrizioneCCIAA = dataIscrizioneCCIAA;
	}

	@Override
	public String getProvinciaIscrizioneCCIAA() {
		return provinciaIscrizioneCCIAA;
	}

	@Override
	public void setProvinciaIscrizioneCCIAA(String provinciaIscrizioneCCIAA) {
		this.provinciaIscrizioneCCIAA = provinciaIscrizioneCCIAA;
	}

	@Override
	public String getDataNullaOstaAntimafiaCCIAA() {
		return dataNullaOstaAntimafiaCCIAA;
	}

	@Override
	public void setDataNullaOstaAntimafiaCCIAA(String dataNullaOstaAntimafiaCCIAA) {
		this.dataNullaOstaAntimafiaCCIAA = dataNullaOstaAntimafiaCCIAA;
	}

	@Override
	public String getSoggettoNormativeDURC() {
		return soggettoNormativeDURC;
	}

	@Override
	public void setSoggettoNormativeDURC(String soggettoDURC) {
		this.soggettoNormativeDURC = soggettoDURC;
	}

	@Override
	public String getSettoreProduttivoDURC() {
		return settoreProduttivoDURC;
	}

	@Override
	public void setSettoreProduttivoDURC(String settoreProduttivo) {
		this.settoreProduttivoDURC = settoreProduttivo;
	}

	@Override
	public String getNumIscrizioneINPS() {
		return numIscrizioneINPS;
	}

	@Override
	public void setNumIscrizioneINPS(String numIscrizioneINPS) {
		this.numIscrizioneINPS = numIscrizioneINPS;
	}

	@Override
	public String getDataIscrizioneINPS() {
		return dataIscrizioneINPS;
	}

	@Override
	public void setDataIscrizioneINPS(String dataIscrizioneINPS) {
		this.dataIscrizioneINPS = dataIscrizioneINPS;
	}

	@Override
	public String getLocalitaIscrizioneINPS() {
		return localitaIscrizioneINPS;
	}

	@Override
	public void setLocalitaIscrizioneINPS(String localitaIscrizioneINPS) {
		this.localitaIscrizioneINPS = localitaIscrizioneINPS;
	}

	@Override
	public String getPosizContributivaIndividualeINPS() {
		return posizContributivaIndividualeINPS;
	}

	@Override
	public void setPosizContributivaIndividualeINPS(String posizContributivaIndividualeINPS) {
		this.posizContributivaIndividualeINPS = posizContributivaIndividualeINPS;
	}

	@Override
	public String getNumIscrizioneINAIL() {
		return numIscrizioneINAIL;
	}

	@Override
	public void setNumIscrizioneINAIL(String numIscrizioneINAIL) {
		this.numIscrizioneINAIL = numIscrizioneINAIL;
	}

	@Override
	public String getDataIscrizioneINAIL() {
		return dataIscrizioneINAIL;
	}
	
	@Override
	public void setDataIscrizioneINAIL(String dataIscrizioneINAIL) {
		this.dataIscrizioneINAIL = dataIscrizioneINAIL;
	}

	@Override
	public String getLocalitaIscrizioneINAIL() {
		return localitaIscrizioneINAIL;
	}

	@Override
	public void setLocalitaIscrizioneINAIL(String localitaIscrizioneINAIL) {
		this.localitaIscrizioneINAIL = localitaIscrizioneINAIL;
	}

	@Override
	public String getPosizAssicurativaINAIL() {
		return posizAssicurativaINAIL;
	}

	@Override
	public void setPosizAssicurativaINAIL(String posizAssicurativaINAIL) {
		this.posizAssicurativaINAIL = posizAssicurativaINAIL;
	}

	@Override
	public String getCodiceCassaEdile() {
		return codiceCassaEdile;
	}

	@Override
	public void setCodiceCassaEdile(String codice) {
		this.codiceCassaEdile = codice;
	}

	@Override
	public String getNumIscrizioneCassaEdile() {
		return numIscrizioneCassaEdile;
	}

	@Override
	public void setNumIscrizioneCassaEdile(String numIscrizioneCassaEdile) {
		this.numIscrizioneCassaEdile = numIscrizioneCassaEdile;
	}

	@Override
	public String getDataIscrizioneCassaEdile() {
		return dataIscrizioneCassaEdile;
	}

	@Override
	public void setDataIscrizioneCassaEdile(String dataIscrizioneCassaEdile) {
		this.dataIscrizioneCassaEdile = dataIscrizioneCassaEdile;
	}

	@Override
	public String getLocalitaIscrizioneCassaEdile() {
		return localitaIscrizioneCassaEdile;
	}

	@Override
	public void setLocalitaIscrizioneCassaEdile(String localitaIscrizioneCassaEdile) {
		this.localitaIscrizioneCassaEdile = localitaIscrizioneCassaEdile;
	}

	@Override
	public String getAltriIstitutiPrevidenziali() {
		return this.altriIstitutiPrevidenziali;
	}

	@Override
	public void setAltriIstitutiPrevidenziali(String altriIstitutiPrevidenziali) {
		this.altriIstitutiPrevidenziali = altriIstitutiPrevidenziali;
	}

	@Override
	public String getNumIscrizioneSOA() {
		return numIscrizioneSOA;
	}

	@Override
	public void setNumIscrizioneSOA(String numIscrizioneSOA) {
		this.numIscrizioneSOA = numIscrizioneSOA.trim();
	}

	@Override
	public String getDataIscrizioneSOA() {
		return dataIscrizioneSOA;
	}

	@Override
	public void setDataIscrizioneSOA(String dataIscrizioneSOA) {
		this.dataIscrizioneSOA = dataIscrizioneSOA;
	}

	@Override
	public String getDataUltimaRichiestaIscrizioneSOA() {
		return dataUltimaRichiestaIscrizioneSOA;
	}

	@Override
	public void setDataUltimaRichiestaIscrizioneSOA(String dataUltimaRichiestaIscrizioneSOA) {
		this.dataUltimaRichiestaIscrizioneSOA = dataUltimaRichiestaIscrizioneSOA;
	}

	@Override
	public String getDataScadenzaTriennaleSOA() {
		return dataScadenzaTriennaleSOA;
	}

	@Override
	public void setDataScadenzaTriennaleSOA(String dataScadenzaTriennaleSOA) {
		this.dataScadenzaTriennaleSOA = dataScadenzaTriennaleSOA;
	}
	
	@Override
	public String getDataVerificaTriennaleSOA() {
		return dataVerificaTriennaleSOA;
	}

	@Override
	public void setDataVerificaTriennaleSOA(String dataVerificaTriennaleSOA) {
		this.dataVerificaTriennaleSOA = dataVerificaTriennaleSOA;
	}
	
	@Override
	public String getDataScadenzaIntermediaSOA() {
		return dataScadenzaIntermediaSOA;
	}

	@Override
	public void setDataScadenzaIntermediaSOA(String dataScadenzaIntermediaSOA) {
		this.dataScadenzaIntermediaSOA = dataScadenzaIntermediaSOA;
	}

	@Override
	public String getDataScadenzaQuinquennaleSOA() {
		return dataScadenzaQuinquennaleSOA;
	}

	@Override
	public void setDataScadenzaQuinquennaleSOA(String dataScadenzaQuinquennaleSOA) {
		this.dataScadenzaQuinquennaleSOA = dataScadenzaQuinquennaleSOA;
	}

	@Override
	public String getOrganismoCertificatoreSOA() {
		return organismoCertificatoreSOA;
	}

	@Override
	public void setOrganismoCertificatoreSOA(String organismoCertificatoreSOA) {
		this.organismoCertificatoreSOA = organismoCertificatoreSOA;
	}

	@Override
	public String getNumIscrizioneISO() {
		return numIscrizioneISO;
	}

	@Override
	public void setNumIscrizioneISO(String numIscrizioneISO) {
		this.numIscrizioneISO = numIscrizioneISO.trim();
	}

	@Override
	public String getDataScadenzaISO() {
		return dataScadenzaISO;
	}
	
	@Override
	public void setDataScadenzaISO(String dataScadenzaISO) {
		this.dataScadenzaISO = dataScadenzaISO;
	}

	@Override
	public String getOrganismoCertificatoreISO() {
		return organismoCertificatoreISO;
	}

	@Override
	public void setOrganismoCertificatoreISO(String organismoCertificatoreISO) {
		this.organismoCertificatoreISO = organismoCertificatoreISO;
	}
	
	@Override
	public String getIscrittoWhitelistAntimafia() {
		return iscrittoWhitelistAntimafia;
	}

	@Override
	public void setIscrittoWhitelistAntimafia(String iscritto) {
		this.iscrittoWhitelistAntimafia = iscritto;
	}

	@Override
	public String getSedePrefetturaWhitelistAntimafia() {
		return sedePrefetturaWhitelistAntimafia;
	}

	@Override
	public void setSedePrefetturaWhitelistAntimafia(String sedePrefettura) {
		this.sedePrefetturaWhitelistAntimafia = sedePrefettura;	
	}

	@Override
	public String[] getSezioniIscrizioneWhitelistAntimafia() {
		return sezioniIscrizioneWhitelistAntimafia;
	}

	@Override
	public void setSezioniIscrizioneWhitelistAntimafia(String[] sezioniIscrizione) {
		this.sezioniIscrizioneWhitelistAntimafia = sezioniIscrizione;
	}

	@Override
	public String getDataIscrizioneWhitelistAntimafia() {
		return dataIscrizioneWhitelistAntimafia;
	}

	@Override
	public void setDataIscrizioneWhitelistAntimafia(String dataIscrizione) {
		this.dataIscrizioneWhitelistAntimafia = dataIscrizione;	
	}

	@Override
	public String getDataScadenzaIscrizioneWhitelistAntimafia() {
		return dataScadenzaIscrizioneWhitelistAntimafia;
	}

	@Override
	public void setDataScadenzaIscrizioneWhitelistAntimafia(String dataScadenza) {
		this.dataScadenzaIscrizioneWhitelistAntimafia = dataScadenza;
	}

	@Override
	public String getAggiornamentoWhitelistAntimafia() {
		return aggiornamentoWhitelistAntimafia;
	}

	@Override
	public void setAggiornamentoWhitelistAntimafia(String aggiornamento) {
		this.aggiornamentoWhitelistAntimafia = aggiornamento;
	}
	
	@Override
	public String getIscrittoAnagrafeAntimafiaEsecutori() {
		return this.iscrittoAnagrafeAntimafiaEsecutori;
	}

	@Override
	public void setIscrittoAnagrafeAntimafiaEsecutori(String value) {
		this.iscrittoAnagrafeAntimafiaEsecutori = value;
	}

	@Override
	public String getDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori() {
		return this.dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori;
	}

	@Override
	public void setDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori(String value) {
		this.dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori = value;
	}

	@Override
	public String getRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori() {
		return this.rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori;
	}
	
	@Override
	public void setRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori(String value) {
		this.rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori = value;
	}

	@Override
	public String getIscrittoElencoSpecialeProfessionisti() {
		return this.iscrittoElencoSpecialeProfessionisti;
	}

	@Override
	public void setIscrittoElencoSpecialeProfessionisti(String value) {
		this.iscrittoElencoSpecialeProfessionisti = value;
	}

	@Override
	public String getPossiedeRatingLegalita() {
		return this.possiedeRatingLegalita;
	}

	@Override
	public void setPossiedeRatingLegalita(String value) {
		this.possiedeRatingLegalita = value;
	}

	@Override
	public String getRatingLegalita() {
		return this.ratingLegalita;
	}

	@Override
	public void setRatingLegalita(String value) {
		this.ratingLegalita = value;
	}

	@Override
	public String getDataScadenzaPossessoRatingLegalita() {
		return this.dataScadenzaPossessoRatingLegalita;
	}

	@Override
	public void setDataScadenzaPossessoRatingLegalita(String value) {
		this.dataScadenzaPossessoRatingLegalita = value;
	}

	@Override
	public String getAggiornamentoRatingLegalita() {
		return this.aggiornamentoRatingLegalita;
	}

	@Override
	public void setAggiornamentoRatingLegalita(String value) {
		this.aggiornamentoRatingLegalita = value;
	}

	@Override
	public String getAltreCertificazioniAttestazioni() {
		return altreCertificazioniAttestazioni;
	}

	@Override
	public void setAltreCertificazioniAttestazioni(String altreCertificazioniAttestazioni) {
		this.altreCertificazioniAttestazioni = altreCertificazioniAttestazioni;
	}

	@Override
	public String getCodiceIBANCCDedicato() {
		return codiceIBANCCDedicato;
	}

	@Override
	public void setCodiceIBANCCDedicato(String codiceIBANCCDedicato) {
		this.codiceIBANCCDedicato = (codiceIBANCCDedicato != null ? codiceIBANCCDedicato.toUpperCase() : codiceIBANCCDedicato);
	}

	@Override
	public String getCodiceBICCCDedicato() {
		return codiceBICCCDedicato;
	}

	@Override
	public void setCodiceBICCCDedicato(String codiceBICCCDedicato) {
		this.codiceBICCCDedicato = (codiceBICCCDedicato != null ? codiceBICCCDedicato.toUpperCase() : codiceBICCCDedicato);
	}

	@Override
	public String getSoggettiAbilitatiCCDedicato() {
		return soggettiAbilitatiCCDedicato;
	}
	
	@Override
	public void setSoggettiAbilitatiCCDedicato(String soggettiAbilitatiCCDedicato) {
		this.soggettiAbilitatiCCDedicato = soggettiAbilitatiCCDedicato;
	}
	
	@Override
	public String getSocioUnico() {
		return socioUnico;
	}

	@Override
	public void setSocioUnico(String socioUnico) {
		this.socioUnico = socioUnico;
	}

	@Override
	public String getRegimeFiscale() {
		return regimeFiscale;
	}

	@Override
	public void setRegimeFiscale(String regimeFiscale) {
		this.regimeFiscale = regimeFiscale;
	}
	
	@Override
	public String getSettoreAttivitaEconomica() {
		return this.settoreAttivitaEconomica;
	}

	@Override
	public void setSettoreAttivitaEconomica(String settoreAttivitaEconomica) {
		this.settoreAttivitaEconomica = settoreAttivitaEconomica;
	}	

	@Override
	public String getDataScadenzaAbilitPreventiva() {
		return dataScadenzaAbilitPreventiva;
	}

	@Override
	public void setDataScadenzaAbilitPreventiva(String dataScadenzaAbilitPreventiva) {
		this.dataScadenzaAbilitPreventiva = dataScadenzaAbilitPreventiva;
	}

	@Override
	public String getDataRichRinnovoAbilitPreventiva() {
		return dataRichRinnovoAbilitPreventiva;
	}

	@Override
	public void setDataRichRinnovoAbilitPreventiva(String dataRichRinnovoAbilitPreventiva) {
		this.dataRichRinnovoAbilitPreventiva = dataRichRinnovoAbilitPreventiva;
	}

	@Override
	public String getRinnovoAbilitPreventiva() {
		return rinnovoAbilitPreventiva;
	}

	@Override
	public void setRinnovoAbilitPreventiva(String rinnovoAbilitPreventiva) {
		this.rinnovoAbilitPreventiva = rinnovoAbilitPreventiva;
	}
	
	@Override
	public String[] getZoneAttivita() {
		return zoneAttivita;
	}

	@Override
	public void setZoneAttivita(String[] zoneAttivita) {
		this.zoneAttivita = zoneAttivita;
	}

	@Override
	public String getAssunzioniObbligate() {
		return assunzioniObbligate;
	}

	@Override
	public void setAssunzioniObbligate(String assunzioniObbligate) {
		this.assunzioniObbligate = assunzioniObbligate;
	}

	@Override
	public int[] getAnni() {
		return anni;
	}

	@Override
	public void setAnni(int[] anni) {
		this.anni = anni;
	}

	@Override
	public Integer[] getNumDipendenti() {
		return numDipendenti;
	}

	@Override
	public void setNumDipendenti(Integer[] numDipendenti) {
		this.numDipendenti = numDipendenti;
	}

	public String[] getStrNumDipendenti() {
		return strNumDipendenti;
	}

	public void setStrNumDipendenti(String[] strNumDipendenti) {
		this.strNumDipendenti = strNumDipendenti;
	}
	
	@Override
	public String getClasseDimensioneDipendenti() {
		return classeDimensioneDipendenti;
	}

	@Override
	public void setClasseDimensioneDipendenti(String classeDimensioneDipendenti) {
		this.classeDimensioneDipendenti = classeDimensioneDipendenti;
	}

	@Override
	public String getUlterioriDichiarazioni() {
		return ulterioriDichiarazioni;
	}

	@Override
	public void setUlterioriDichiarazioni(String ulterioriDichiarazioni) {
		this.ulterioriDichiarazioni = ulterioriDichiarazioni;
	}

	public boolean isLiberoProfessionista() {
		return liberoProfessionista;
	}

	public void setLiberoProfessionista(boolean liberoProfessionista) {
		this.liberoProfessionista = liberoProfessionista;
	}
	
	public boolean isObblOggettoSociale() {
		return obblOggettoSociale;
	}

	public void setObblOggettoSociale(boolean obblOggettoSociale) {
		this.obblOggettoSociale = obblOggettoSociale;
	}
	
	public String getOggettoSociale() {
		return oggettoSociale;
	}

	public void setOggettoSociale(String oggettoSociale) {
		this.oggettoSociale = oggettoSociale;
	}
	
	public boolean isObblSezCCIAA() {
		return obblSezCCIAA;
	}

	public void setObblSezCCIAA(boolean obblSezCCIAA) {
		this.obblSezCCIAA = obblSezCCIAA;
	}

	public boolean isObblDataNullaOstaAntimafiaCCIAA() {
		return obblDataNullaOstaAntimafiaCCIAA;
	}

	public void setObblDataNullaOstaAntimafiaCCIAA(boolean obblDataNullaOstaAntimafiaCCIAA) {
		this.obblDataNullaOstaAntimafiaCCIAA = obblDataNullaOstaAntimafiaCCIAA;
	}

	public boolean isVisSezAbilitPreventiva() {
		return visSezAbilitPreventiva;
	}

	public void setVisSezAbilitPreventiva(boolean visSezAbilitPreventiva) {
		this.visSezAbilitPreventiva = visSezAbilitPreventiva;
	}

	public boolean isObblDataScadenzaAbilitPreventiva() {
		return obblDataScadenzaAbilitPreventiva;
	}

	public void setObblDataScadenzaAbilitPreventiva(boolean obblDataScadenzaAbilitPreventiva) {
		this.obblDataScadenzaAbilitPreventiva = obblDataScadenzaAbilitPreventiva;
	}

	public boolean isObblZoneAttivita() {
		return obblZoneAttivita;
	}

	public void setObblZoneAttivita(boolean obblZoneAttivita) {
		this.obblZoneAttivita = obblZoneAttivita;
	}

	public Boolean getObblSoggettoNormativeDURC() {
		return obblSoggettoNormativeDURC;
	}

	public void setObblSoggettoNormativeDURC(Boolean obblSoggettoNormativeDURC) {
		this.obblSoggettoNormativeDURC = obblSoggettoNormativeDURC;
	}

	public Boolean getObblAssunzioniObbligate() {
		return obblAssunzioniObbligate;
	}

	public void setObblAssunzioniObbligate(Boolean obblAssunzioniObbligate) {
		this.obblAssunzioniObbligate = obblAssunzioniObbligate;
	}

	public Boolean getObblDipendentiTriennio() {
		return obblDipendentiTriennio;
	}

	public void setObblDipendentiTriennio(Boolean obblDipendentiTriennio) {
		this.obblDipendentiTriennio = obblDipendentiTriennio;
	}

	public Boolean getObblClasseDimensione() {
		return obblClasseDimensione;
	}

	public void setObblClasseDimensione(Boolean obblClasseDimensione) {
		this.obblClasseDimensione = obblClasseDimensione;
	}

	public boolean isObblSettoreAttivitaEconomica() {
		return obblSettoreAttivitaEconomica;
	}

	public void setObblSettoreAttivitaEconomica(boolean obblSettoreAttivitaEconomica) {
		this.obblSettoreAttivitaEconomica = obblSettoreAttivitaEconomica;
	}
	
	public boolean isObblIscrittoAnagrafeAntimafiaEsecutori() {
		return obblIscrittoAnagrafeAntimafiaEsecutori;
	}

	public void setObblIscrittoAnagrafeAntimafiaEsecutori(boolean obblIscrittoAnagrafeAntimafiaEsecutori) {
		this.obblIscrittoAnagrafeAntimafiaEsecutori = obblIscrittoAnagrafeAntimafiaEsecutori;
	}

	public boolean isObblPossiedeRatingLegalita() {
		return obblPossiedeRatingLegalita;
	}

	public void setObblPossiedeRatingLegalita(boolean obblPossiedeRatingLegalita) {
		this.obblPossiedeRatingLegalita = obblPossiedeRatingLegalita;
	}
	
	public String getTipoImpresa() {
		return tipoImpresa;
	}

	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	/**
	 * Si verifica che l'eventuale modifica degli indirizzi email non vada in
	 * conflitto con gli indirizzi email usati come riferimento per altre imprese.
	 */
	@Override
	public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		try {
			if("1".equals(this.iscrittoWhitelistAntimafia)) {
				
				// verifica che sia presente almeno 1 sezione d'iscrizione...
				int selezionati = 0;
				if(this.sezioniIscrizioneWhitelistAntimafia != null) {
					for(int i = 0; i < this.sezioniIscrizioneWhitelistAntimafia.length; i++) {
						if(StringUtils.isNotEmpty(this.sezioniIscrizioneWhitelistAntimafia[i])) {
							selezionati++;
						}
					}
				}
				if(selezionati <= 0) {
					this.addFieldError("sezioniIscrizioneWhitelistAntimafia", this.getText("Errors.datiSezioneWhitelistAntimafiaIncompleti"));
				}
			}
			if("1".equals(this.iscrittoCCIAA)) {
				if (this.obblOggettoSociale
						&& !this.liberoProfessionista
						&& StringUtils.isBlank(this.oggettoSociale)) {
					this.addFieldError("oggettoSociale", this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("oggettoSociale") }));
				}
			}
//			if("1".equals(this.iscrittoAnagrafeAntimafiaEsecutori)) {
//				// ...
//			}
//			
//			if("1".equals(this.possiedeRatingLegalita)) {
//				// ...
//			}
		
		} catch (Exception ex) {
			throw new RuntimeException(
					"Errore durante la verifica dei dati richiesti per l'impresa "
					, ex);
		}
	}

	@Override
	public String next() {

		String target = SUCCESS;

		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// genero i dati sui dipendenti opportunamente tipizzati
			this.numDipendenti = new Integer[this.strNumDipendenti.length];
			for (int i = 0; i < this.strNumDipendenti.length; i++) {
				if (StringUtils.isNotBlank(this.strNumDipendenti[i])) {
					this.numDipendenti[i] = Integer
									.parseInt(this.strNumDipendenti[i]);
				}
			}
			// aggiorna i dati in sessione
			OpenPageDatiUlterioriImpresaAction.synchronizeDatiUlterioriImpresa(
							this, helper.getDatiUlterioriImpresa());
		}
		return target;
	}

	@Override
	public String back() {

		String target = "back";

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// nel caso di libero professionista la pagina precedente � il
			// dettaglio con altri dati del libero professionista e non la
			// pagina dei soggetti
			if ("back".equals(target) && helper.isLiberoProfessionista()) {
				target = "backLiberoProf";
			}
		}
		return target;
	}

	/**
	 * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
	 *
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		return helper;
	}

}
