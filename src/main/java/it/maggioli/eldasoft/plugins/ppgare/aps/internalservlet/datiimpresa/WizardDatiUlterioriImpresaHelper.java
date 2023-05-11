package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.util.Calendar;
import java.util.GregorianCalendar;

import it.eldasoft.sil.portgare.datatypes.ImpresaAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati ulteriori dell'impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class WizardDatiUlterioriImpresaHelper implements IDatiUlterioriImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4854288468947815845L;

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
	private String dataScadenzaTriennaleSOA;
	private String dataVerificaTriennaleSOA;
	private String dataScadenzaIntermediaSOA;
	private String dataScadenzaQuinquennaleSOA;
	private String organismoCertificatoreSOA;
	private String dataUltimaRichiestaIscrizioneSOA;
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
	private String classeDimensioneDipendenti;
	private String ulterioriDichiarazioni;
    private String oggettoSociale;

	public WizardDatiUlterioriImpresaHelper() {
		this.iscrittoCCIAA = null;
		this.numRegistroDitteCCIAA = null;
		this.dataDomandaIscrizioneCCIAA = null;
		this.numIscrizioneCCIAA = null;
		this.dataIscrizioneCCIAA = null;
		this.provinciaIscrizioneCCIAA = null;
		this.dataNullaOstaAntimafiaCCIAA = null;
		this.soggettoNormativeDURC = null;
		this.settoreProduttivoDURC = null;
		this.numIscrizioneINPS = null;
		this.dataIscrizioneINPS = null;
		this.localitaIscrizioneINPS = null;
		this.posizContributivaIndividualeINPS = null;
		this.numIscrizioneINAIL = null;
		this.dataIscrizioneINAIL = null;
		this.localitaIscrizioneINAIL = null;
		this.posizAssicurativaINAIL = null;
		this.numIscrizioneCassaEdile = null;
		this.codiceCassaEdile = null;
		this.dataIscrizioneCassaEdile = null;
		this.localitaIscrizioneCassaEdile = null;
		this.altriIstitutiPrevidenziali = null;
		this.numIscrizioneSOA = null;
		this.dataIscrizioneSOA = null;
		this.dataScadenzaTriennaleSOA = null;
		this.dataVerificaTriennaleSOA = null;
		this.dataScadenzaIntermediaSOA = null;
		this.dataScadenzaQuinquennaleSOA = null;
		this.organismoCertificatoreSOA = null;
		this.dataUltimaRichiestaIscrizioneSOA = null;
		this.numIscrizioneISO = null;
		this.dataScadenzaISO = null;
		this.organismoCertificatoreISO = null;
		this.iscrittoWhitelistAntimafia = null;
		this.sedePrefetturaWhitelistAntimafia = null;
		this.sezioniIscrizioneWhitelistAntimafia = null;
		this.dataIscrizioneWhitelistAntimafia = null;
		this.dataScadenzaIscrizioneWhitelistAntimafia = null;
		this.aggiornamentoWhitelistAntimafia = null;
		this.iscrittoAnagrafeAntimafiaEsecutori = null;
		this.dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori = null;
		this.rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori = null;
		this.iscrittoElencoSpecialeProfessionisti = null;
		this.possiedeRatingLegalita = null;
		this.ratingLegalita = null;
		this.dataScadenzaPossessoRatingLegalita = null;
		this.aggiornamentoRatingLegalita = null;
		this.altreCertificazioniAttestazioni = null;
		this.codiceIBANCCDedicato = null;
		this.codiceBICCCDedicato = null;
		this.soggettiAbilitatiCCDedicato = null;
		this.socioUnico = null;
		this.regimeFiscale = null;
		this.settoreAttivitaEconomica = null;
		this.dataScadenzaAbilitPreventiva = null;
		this.dataRichRinnovoAbilitPreventiva = null;
		this.rinnovoAbilitPreventiva = null;
		this.zoneAttivita = null;
		this.assunzioniObbligate = null;
		Calendar c = new GregorianCalendar();
		int anno = c.get(Calendar.YEAR);
		this.anni = new int[]{anno - 1, anno - 2, anno - 3};
		this.numDipendenti = new Integer[]{null, null, null};
		this.classeDimensioneDipendenti = null;
		this.ulterioriDichiarazioni = null;
		this.oggettoSociale = null;
	}

	public WizardDatiUlterioriImpresaHelper(ImpresaAggiornabileType impresa) {
		
		this.iscrittoCCIAA = impresa.getCciaa().getIscritto();
		this.reimpostaIscrittoCCIAA(impresa);
		this.oggettoSociale = impresa.getOggettoSociale();
		this.numRegistroDitteCCIAA = impresa.getCciaa().getNumRegistroDitte();
		if (impresa.getCciaa().getDataDomandaIscrizione() != null) {
			this.dataDomandaIscrizioneCCIAA = DateFormatUtils.format(impresa
							.getCciaa().getDataDomandaIscrizione().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		this.numIscrizioneCCIAA = impresa.getCciaa().getNumIscrizione();
		if (impresa.getCciaa().getDataIscrizione() != null) {
			this.dataIscrizioneCCIAA = DateFormatUtils.format(impresa
							.getCciaa().getDataIscrizione().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		this.provinciaIscrizioneCCIAA = impresa.getCciaa()
						.getProvinciaIscrizione();
		if (impresa.getCciaa().getDataNullaOstaAntimafia() != null) {
			this.dataNullaOstaAntimafiaCCIAA = DateFormatUtils.format(impresa
							.getCciaa().getDataNullaOstaAntimafia().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}

		this.soggettoNormativeDURC = impresa.getSoggettoDURC();
		this.settoreProduttivoDURC = impresa.getSettoreProduttivo();

		if (impresa.isSetInps()) {
			this.numIscrizioneINPS = impresa.getInps().getNumIscrizione();
			if (impresa.getInps().getDataIscrizione() != null) {
				this.dataIscrizioneINPS = DateFormatUtils.format(impresa
								.getInps().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
			}
			this.localitaIscrizioneINPS = impresa.getInps()
							.getLocalitaIscrizione();
			this.posizContributivaIndividualeINPS = impresa.getInps()
							.getPosizContributivaIndividuale();
		}
		if (impresa.isSetInail()) {
			this.numIscrizioneINAIL = impresa.getInail().getNumIscrizione();
			if (impresa.getInail().getDataIscrizione() != null) {
				this.dataIscrizioneINAIL = DateFormatUtils.format(impresa
								.getInail().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
			}
			this.localitaIscrizioneINAIL = impresa.getInail()
							.getLocalitaIscrizione();
			this.posizAssicurativaINAIL = impresa.getInail().getPosizAssicurativa();
		}
		if (impresa.isSetCassaEdile()) {
			this.numIscrizioneCassaEdile = impresa.getCassaEdile().getNumIscrizione();
			if (impresa.getCassaEdile().getDataIscrizione() != null) {
				this.dataIscrizioneCassaEdile = DateFormatUtils.format(impresa
								.getCassaEdile().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
			}
			this.localitaIscrizioneCassaEdile = impresa.getCassaEdile()
							.getLocalitaIscrizione();
			this.codiceCassaEdile = impresa.getCassaEdile().getCodice();
		}

		this.altriIstitutiPrevidenziali = impresa.getAltriIstitutiPrevidenziali();

		this.numIscrizioneSOA = impresa.getSoa().getNumIscrizione();
		if (impresa.getSoa().getDataIscrizione() != null) {
			this.dataIscrizioneSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataIscrizione().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataScadenzaTriennale() != null) {
			this.dataScadenzaTriennaleSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataScadenzaTriennale().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataVerificaTriennale() != null) {
			this.dataVerificaTriennaleSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataVerificaTriennale().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataScadenzaIntermedia() != null) {
			this.dataScadenzaIntermediaSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataScadenzaIntermedia().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataScadenza() != null) {
			this.dataScadenzaQuinquennaleSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataScadenza().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		this.organismoCertificatoreSOA = impresa.getSoa()
						.getOrganismoCertificatore();
		if (impresa.getSoa().getDataUltimaRichiestaIscrizione() != null) {
			this.dataUltimaRichiestaIscrizioneSOA = DateFormatUtils
							.format(impresa.getSoa().getDataUltimaRichiestaIscrizione()
											.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
		}
		this.numIscrizioneISO = impresa.getIso9001().getNumIscrizione();
		if (impresa.getIso9001().getDataScadenza() != null) {
			this.dataScadenzaISO = DateFormatUtils.format(impresa.getIso9001()
							.getDataScadenza().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		this.organismoCertificatoreISO = impresa.getIso9001()
						.getOrganismoCertificatore();
		
		this.iscrittoWhitelistAntimafia = null;
		if (impresa.getIscrizioneWhitelistAntimafia().isSetIscritto()) {
			this.iscrittoWhitelistAntimafia = impresa.getIscrizioneWhitelistAntimafia()
				.getIscritto();
			this.sedePrefetturaWhitelistAntimafia = impresa.getIscrizioneWhitelistAntimafia()
				.getSedePrefetturaCompetente();
			this.sezioniIscrizioneWhitelistAntimafia = WizardDatiUlterioriImpresaHelper
				.getSezioniIscrizioneFromBO(impresa.getIscrizioneWhitelistAntimafia()
						.getSezioniIscrizione());
			if (impresa.getIscrizioneWhitelistAntimafia().getDataIscrizione() != null) {
				this.dataIscrizioneWhitelistAntimafia = DateFormatUtils.format(
						impresa.getIscrizioneWhitelistAntimafia().getDataIscrizione().getTimeInMillis(),
						EntitySearchFilter.DATE_PATTERN);
			}
			if (impresa.getIscrizioneWhitelistAntimafia().getDataScadenzaIscrizione() != null) {
				this.dataScadenzaIscrizioneWhitelistAntimafia = DateFormatUtils.format(
						impresa.getIscrizioneWhitelistAntimafia().getDataScadenzaIscrizione().getTimeInMillis(),
						EntitySearchFilter.DATE_PATTERN);
			}
		}
		this.aggiornamentoWhitelistAntimafia = impresa.getIscrizioneWhitelistAntimafia()
			.getAggiornamento();
	
		// Iscrizione elenchi ricostruzione (DL 189/2016)
		this.iscrittoAnagrafeAntimafiaEsecutori = null;
		if (impresa.getIscrizioneElenchiRicostruzione().isSetIscrittoAnagrafeAntimafiaEsecutori()) {
			this.iscrittoAnagrafeAntimafiaEsecutori = impresa.getIscrizioneElenchiRicostruzione()
				.getIscrittoAnagrafeAntimafiaEsecutori();
			if (impresa.getIscrizioneElenchiRicostruzione().getDataScadenza() != null) {
				this.dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori = DateFormatUtils.format(
						impresa.getIscrizioneElenchiRicostruzione().getDataScadenza().getTimeInMillis(),
						EntitySearchFilter.DATE_PATTERN);
			}
			this.rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori = impresa.getIscrizioneElenchiRicostruzione()
				.getRinnovoIscrizioneInCorso();
			this.iscrittoElencoSpecialeProfessionisti = impresa.getIscrizioneElenchiRicostruzione()
				.getIscrittoElencoSpecialeProfessionisti();
		}
		
		// Rating di legalità (DL 1/2012)
		this.possiedeRatingLegalita = null;
		if (impresa.getRatingLegalita().isSetPossiedeRating()) {
			this.possiedeRatingLegalita = impresa.getRatingLegalita()
				.getPossiedeRating();
			this.ratingLegalita = impresa.getRatingLegalita()
				.getRating();
			if (impresa.getRatingLegalita().getDataScadenza() != null) {
				this.dataScadenzaPossessoRatingLegalita = DateFormatUtils.format(
						impresa.getRatingLegalita().getDataScadenza().getTimeInMillis(),
						EntitySearchFilter.DATE_PATTERN);
			}
			this.aggiornamentoRatingLegalita = impresa.getRatingLegalita()
				.getAggiornamentoRatingInCorso();
		}
		
		this.altreCertificazioniAttestazioni = impresa.getAltreCertificazioniAttestazioni();

		if (impresa.isSetContoCorrente()) {
			this.codiceIBANCCDedicato = impresa.getContoCorrente().getEstremi();
			this.codiceBICCCDedicato = impresa.getContoCorrente().getBic();
			this.soggettiAbilitatiCCDedicato = impresa.getContoCorrente().getSoggettiAbilitati();
		}
		
		this.socioUnico = impresa.getSocioUnico();
		
		this.regimeFiscale = impresa.getRegimeFiscale();

		this.settoreAttivitaEconomica = impresa.getSettoreAttivitaEconomica();
		
		if (impresa.getAbilitazionePreventiva().getDataScadenzaRinnovo() != null) {
			this.dataScadenzaAbilitPreventiva = DateFormatUtils.format(impresa
							.getAbilitazionePreventiva().getDataScadenzaRinnovo()
							.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
		}
		if (impresa.getAbilitazionePreventiva().getDataRichiestaRinnovo() != null) {
			this.dataRichRinnovoAbilitPreventiva = DateFormatUtils.format(
							impresa.getAbilitazionePreventiva()
							.getDataRichiestaRinnovo().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		if (impresa.getAbilitazionePreventiva().isSetFaseRinnovo()) {
			this.rinnovoAbilitPreventiva = impresa.getAbilitazionePreventiva()
							.getFaseRinnovo();
		}
		this.zoneAttivita = WizardDatiUlterioriImpresaHelper
						.getZoneAttivitaFromBO(impresa.getZoneAttivita());

		this.assunzioniObbligate = impresa.getAssunzioniObbligate();
		this.anni = new int[impresa.getDatoAnnuoArray().length];
		this.numDipendenti = new Integer[impresa.getDatoAnnuoArray().length];
		for (int i = 0; i < impresa.getDatoAnnuoArray().length; i++) {
			this.anni[i] = impresa.getDatoAnnuoArray()[i].getAnno();
			if (!impresa.getDatoAnnuoArray()[i].isNilDipendenti()) {
				this.numDipendenti[i] = impresa.getDatoAnnuoArray()[i].getDipendenti();
			}
		}
		
		this.classeDimensioneDipendenti = impresa.getClasseDimensione();
		this.ulterioriDichiarazioni = impresa.getUlterioriDichiarazioni();
	}

	public WizardDatiUlterioriImpresaHelper(ImpresaType impresa) {
		
		this.iscrittoCCIAA = impresa.getCciaa().getIscritto();
		this.reimpostaIscrittoCCIAA(impresa);
		
		this.numRegistroDitteCCIAA = impresa.getCciaa().getNumRegistroDitte();
		if (impresa.getCciaa().getDataDomandaIscrizione() != null) {
			this.dataDomandaIscrizioneCCIAA = DateFormatUtils.format(impresa
							.getCciaa().getDataDomandaIscrizione().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		this.numIscrizioneCCIAA = impresa.getCciaa().getNumIscrizione();
		if (impresa.getCciaa().getDataIscrizione() != null) {
			this.dataIscrizioneCCIAA = DateFormatUtils.format(impresa
							.getCciaa().getDataIscrizione().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		this.provinciaIscrizioneCCIAA = impresa.getCciaa()
						.getProvinciaIscrizione();
		if (impresa.getCciaa().getDataNullaOstaAntimafia() != null) {
			this.dataNullaOstaAntimafiaCCIAA = DateFormatUtils.format(impresa
							.getCciaa().getDataNullaOstaAntimafia().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		this.oggettoSociale = impresa.getOggettoSociale();
		
		this.soggettoNormativeDURC = impresa.getSoggettoDURC();
		this.settoreProduttivoDURC = impresa.getSettoreProduttivo();

		if (impresa.isSetInps()) {
			this.numIscrizioneINPS = impresa.getInps().getNumIscrizione();
			if (impresa.getInps().getDataIscrizione() != null) {
				this.dataIscrizioneINPS = DateFormatUtils.format(impresa
								.getInps().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
			}
			this.localitaIscrizioneINPS = impresa.getInps()
							.getLocalitaIscrizione();
			this.posizContributivaIndividualeINPS = impresa.getInps()
							.getPosizContributivaIndividuale();
		}
		if (impresa.isSetInail()) {
			this.numIscrizioneINAIL = impresa.getInail().getNumIscrizione();
			if (impresa.getInail().getDataIscrizione() != null) {
				this.dataIscrizioneINAIL = DateFormatUtils.format(impresa
								.getInail().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
			}
			this.localitaIscrizioneINAIL = impresa.getInail()
							.getLocalitaIscrizione();
			this.posizAssicurativaINAIL = impresa.getInail().getPosizAssicurativa();
		}
		if (impresa.isSetCassaEdile()) {
			this.numIscrizioneCassaEdile = impresa.getCassaEdile().getNumIscrizione();
			if (impresa.getCassaEdile().getDataIscrizione() != null) {
				this.dataIscrizioneCassaEdile = DateFormatUtils.format(impresa
								.getCassaEdile().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
			}
			this.localitaIscrizioneCassaEdile = impresa.getCassaEdile()
							.getLocalitaIscrizione();
			this.codiceCassaEdile = impresa.getCassaEdile().getCodice();
		}

		this.altriIstitutiPrevidenziali = impresa.getAltriIstitutiPrevidenziali();

		this.numIscrizioneSOA = impresa.getSoa().getNumIscrizione();
		if (impresa.getSoa().getDataIscrizione() != null) {
			this.dataIscrizioneSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataIscrizione().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		if (impresa.getSoa().getDataScadenza() != null) {
			this.dataScadenzaQuinquennaleSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataScadenza().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataScadenzaTriennale() != null) {
			this.dataScadenzaTriennaleSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataScadenzaTriennale().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataVerificaTriennale() != null) {
			this.dataVerificaTriennaleSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataVerificaTriennale().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		if (impresa.getSoa().getDataScadenzaIntermedia() != null) {
			this.dataScadenzaIntermediaSOA = DateFormatUtils.format(impresa.getSoa()
							.getDataScadenzaIntermedia().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		
		this.organismoCertificatoreSOA = impresa.getSoa()
						.getOrganismoCertificatore();
		if (impresa.getSoa().getDataUltimaRichiestaIscrizione() != null) {
			this.dataUltimaRichiestaIscrizioneSOA = DateFormatUtils
							.format(impresa.getSoa().getDataUltimaRichiestaIscrizione()
											.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
		}
		this.numIscrizioneISO = impresa.getIso9001().getNumIscrizione();
		if (impresa.getIso9001().getDataScadenza() != null) {
			this.dataScadenzaISO = DateFormatUtils.format(impresa.getIso9001()
							.getDataScadenza().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		this.organismoCertificatoreISO = impresa.getIso9001()
						.getOrganismoCertificatore();

		this.iscrittoWhitelistAntimafia = null;
		if (impresa.getIscrizioneWhitelistAntimafia() != null) {
			if(impresa.getIscrizioneWhitelistAntimafia().isSetIscritto()) {
				this.iscrittoWhitelistAntimafia = impresa.getIscrizioneWhitelistAntimafia()
					.getIscritto();
			
				if("1".equals(impresa.getIscrizioneWhitelistAntimafia().getIscritto())) {
					this.sedePrefetturaWhitelistAntimafia = impresa.getIscrizioneWhitelistAntimafia()
						.getSedePrefetturaCompetente();
					this.sezioniIscrizioneWhitelistAntimafia = WizardDatiUlterioriImpresaHelper
						.getSezioniIscrizioneFromBO(impresa.getIscrizioneWhitelistAntimafia()
								.getSezioniIscrizione());
					if (impresa.getIscrizioneWhitelistAntimafia().getDataIscrizione() != null) {
						this.dataIscrizioneWhitelistAntimafia = DateFormatUtils.format(
								impresa.getIscrizioneWhitelistAntimafia().getDataIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
					}
					if (impresa.getIscrizioneWhitelistAntimafia().getDataScadenzaIscrizione() != null) {
						this.dataScadenzaIscrizioneWhitelistAntimafia = DateFormatUtils.format(
								impresa.getIscrizioneWhitelistAntimafia().getDataScadenzaIscrizione().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
					}
					this.aggiornamentoWhitelistAntimafia = impresa.getIscrizioneWhitelistAntimafia()
						.getAggiornamento();
				}
			}
		}
		
		// Iscrizione elenchi ricostruzione (DL 189/2016)
		this.iscrittoAnagrafeAntimafiaEsecutori = null;
		if (impresa.getIscrizioneElenchiRicostruzione() != null) {
			if (impresa.getIscrizioneElenchiRicostruzione().isSetIscrittoAnagrafeAntimafiaEsecutori()) {
				this.iscrittoAnagrafeAntimafiaEsecutori = impresa.getIscrizioneElenchiRicostruzione()
					.getIscrittoAnagrafeAntimafiaEsecutori();
				
				if("1".equals(impresa.getIscrizioneElenchiRicostruzione().getIscrittoAnagrafeAntimafiaEsecutori())) {
					if (impresa.getIscrizioneElenchiRicostruzione().getDataScadenza() != null) {
						this.dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori = DateFormatUtils.format(
								impresa.getIscrizioneElenchiRicostruzione().getDataScadenza().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
					}
					this.rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori = impresa.getIscrizioneElenchiRicostruzione()
						.getRinnovoIscrizioneInCorso();	
				}
				
				this.iscrittoElencoSpecialeProfessionisti = impresa.getIscrizioneElenchiRicostruzione()
					.getIscrittoElencoSpecialeProfessionisti();
			}
		}
		
		// Rating di legalità (DL 1/2012)
		this.possiedeRatingLegalita = null;
		if (impresa.getRatingLegalita() != null) {
			if (impresa.getRatingLegalita().isSetPossiedeRating()) {
				this.possiedeRatingLegalita = impresa.getRatingLegalita()
					.getPossiedeRating();
			
				if("1".equals(impresa.getRatingLegalita().getPossiedeRating())) {
					this.ratingLegalita = impresa.getRatingLegalita()
						.getRating();
					if (impresa.getRatingLegalita().getDataScadenza() != null) {
						this.dataScadenzaPossessoRatingLegalita = DateFormatUtils.format(
								impresa.getRatingLegalita().getDataScadenza().getTimeInMillis(),
								EntitySearchFilter.DATE_PATTERN);
					}
					this.aggiornamentoRatingLegalita = impresa.getRatingLegalita()
						.getAggiornamentoRatingInCorso();
				}
			}
		}
		
		this.altreCertificazioniAttestazioni = impresa.getAltreCertificazioniAttestazioni();

		if (impresa.isSetContoCorrente()) {
//			this.estremiCCDedicato = impresa.getContoCorrente().getEstremi();
			this.codiceIBANCCDedicato = impresa.getContoCorrente().getEstremi();
			this.codiceBICCCDedicato = impresa.getContoCorrente().getBic();
			this.soggettiAbilitatiCCDedicato = impresa.getContoCorrente().getSoggettiAbilitati();
		}
		
		this.socioUnico = impresa.getSocioUnico();
		
		this.regimeFiscale = impresa.getRegimeFiscale();

		this.settoreAttivitaEconomica = impresa.getSettoreAttivitaEconomica();
		
		if (impresa.getAbilitazionePreventiva().getDataScadenzaRinnovo() != null) {
			this.dataScadenzaAbilitPreventiva = DateFormatUtils.format(impresa
							.getAbilitazionePreventiva().getDataScadenzaRinnovo()
							.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
		}
		if (impresa.getAbilitazionePreventiva().getDataRichiestaRinnovo() != null) {
			this.dataRichRinnovoAbilitPreventiva = DateFormatUtils.format(
							impresa.getAbilitazionePreventiva()
							.getDataRichiestaRinnovo().getTimeInMillis(),
							EntitySearchFilter.DATE_PATTERN);
		}
		if (impresa.getAbilitazionePreventiva().isSetFaseRinnovo()) {
			this.rinnovoAbilitPreventiva = impresa.getAbilitazionePreventiva()
							.getFaseRinnovo();
		}
		this.zoneAttivita = WizardDatiUlterioriImpresaHelper
						.getZoneAttivitaFromBO(impresa.getZoneAttivita());

		this.assunzioniObbligate = impresa.getAssunzioniObbligate();
		if (impresa.getDatoAnnuoArray().length != 0) {
			this.anni = new int[impresa.getDatoAnnuoArray().length];
			this.numDipendenti = new Integer[impresa.getDatoAnnuoArray().length];
			for (int i = 0; i < impresa.getDatoAnnuoArray().length; i++) {
				this.anni[i] = impresa.getDatoAnnuoArray()[i].getAnno();
				if (impresa.getDatoAnnuoArray()[i].isSetDipendenti()) {
					this.numDipendenti[i] = impresa.getDatoAnnuoArray()[i]
									.getDipendenti();
				}
			}
		} else {
			// se nella comunicazione non si trova nulla si lascia l'elenco vuoto con i soli 3 anni indicati
		}
		
		this.classeDimensioneDipendenti = impresa.getClasseDimensione();
		this.ulterioriDichiarazioni = impresa.getUlterioriDichiarazioni();
	}

	private void reimpostaIscrittoCCIAA(Object impresa) {
//		if(StringUtils.isEmpty(this.iscrittoCCIAA)) {
//			boolean reimposta = false;
//			
//			if(impresa instanceof ImpresaType) {
//				ImpresaType imp = (ImpresaType)impresa;
//				reimposta = (!StringUtils.isEmpty(imp.getCciaa().getNumRegistroDitte()) ||
//							 !StringUtils.isEmpty(imp.getCciaa().getNumIscrizione()) ||
//							 !StringUtils.isEmpty(imp.getCciaa().getProvinciaIscrizione()) ||
//							 imp.getCciaa().getDataDomandaIscrizione() != null ||
//							 imp.getCciaa().getDataIscrizione() != null ||
//							 imp.getCciaa().getDataNullaOstaAntimafia() != null);
//				
//			} else if(impresa instanceof ImpresaAggiornabileType) {
//				ImpresaAggiornabileType imp = (ImpresaAggiornabileType)impresa;
//				reimposta = (!StringUtils.isEmpty(imp.getCciaa().getNumRegistroDitte()) ||
//						 	 !StringUtils.isEmpty(imp.getCciaa().getNumIscrizione()) ||
//						 	 !StringUtils.isEmpty(imp.getCciaa().getProvinciaIscrizione()) ||
//						 	 imp.getCciaa().getDataDomandaIscrizione() != null ||
//						 	 imp.getCciaa().getDataIscrizione() != null ||
//						 	 imp.getCciaa().getDataNullaOstaAntimafia() != null);
//			}
//			
//			if(reimposta) {
//				this.iscrittoCCIAA = "1";
//			}
//		}
	}
	
	@Override
	public String getIscrittoCCIAA() {
		return iscrittoCCIAA;
	}

	@Override
	public void setIscrittoCCIAA(String iscrittoCCIAA) {
		this.iscrittoCCIAA = iscrittoCCIAA;
		
		// se "Iscritto a Camera di Commercio='No'" resetta tutti i dati CCIAA
		if( "0".equals(this.iscrittoCCIAA) ) {
			this.numIscrizioneCCIAA = null;
			this.dataIscrizioneCCIAA = null;
			this.numRegistroDitteCCIAA = null;
			this.dataDomandaIscrizioneCCIAA = null;
			this.provinciaIscrizioneCCIAA = null;
			this.dataNullaOstaAntimafiaCCIAA = null;
			this.oggettoSociale= null;
		}
	}

	@Override
	public String getNumRegistroDitteCCIAA() {
		return numRegistroDitteCCIAA;
	}

	@Override
	public void setNumRegistroDitteCCIAA(String numRegistroDitteCCIAA) {
		this.numRegistroDitteCCIAA = numRegistroDitteCCIAA;
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
		this.numIscrizioneCCIAA = numIscrizioneCCIAA;
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
		return this.dataNullaOstaAntimafiaCCIAA;
	}

	@Override
	public void setDataNullaOstaAntimafiaCCIAA(String dataNullaOstaAntimafiaCCIAA) {
		this.dataNullaOstaAntimafiaCCIAA = dataNullaOstaAntimafiaCCIAA;
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
	public String getCodiceCassaEdile() {
		return codiceCassaEdile;
	}

	@Override
	public void setCodiceCassaEdile(String codice) {
		this.codiceCassaEdile = codice;
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
		this.numIscrizioneSOA = numIscrizioneSOA;
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
		return this.organismoCertificatoreSOA;
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
		this.numIscrizioneISO = numIscrizioneISO;
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
		return this.organismoCertificatoreISO;
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
	public String getOggettoSociale() {
		return oggettoSociale;
	}

    @Override
	public void setOggettoSociale(String oggettoSociale) {
		this.oggettoSociale = oggettoSociale;
	}

	@Override
	public void setIscrittoWhitelistAntimafia(String iscritto) {
		this.iscrittoWhitelistAntimafia = iscritto;
		
		// se "Iscritto!='Si'" resetta i dati iscrizione whitelist antimafia
		if( !"1".equals(this.iscrittoWhitelistAntimafia) ) {
			this.sedePrefetturaWhitelistAntimafia = null;
			this.sezioniIscrizioneWhitelistAntimafia = null;
			this.dataIscrizioneWhitelistAntimafia = null;
			this.dataScadenzaIscrizioneWhitelistAntimafia = null;
		}
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
		
		// se "Iscritto!='Si'" resetta i dati
		if( !"1".equals(this.iscrittoAnagrafeAntimafiaEsecutori) ) {
			this.dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori = null;
			this.rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori = null;
		}
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
		
		// se "Iscritto!='Si'" resetta i dati
		if( !"1".equals(this.possiedeRatingLegalita) ) {
			this.ratingLegalita = null;
			this.dataScadenzaPossessoRatingLegalita = null;
			this.aggiornamentoRatingLegalita = null;
		}
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
		return this.dataScadenzaAbilitPreventiva;
	}

	@Override
	public void setDataScadenzaAbilitPreventiva(String dataScadAbilitPreventiva) {
		this.dataScadenzaAbilitPreventiva = dataScadAbilitPreventiva;
	}

	@Override
	public String getRinnovoAbilitPreventiva() {
		return this.rinnovoAbilitPreventiva;
	}

	@Override
	public void setRinnovoAbilitPreventiva(String rinnovoAbilitPreventiva) {
		this.rinnovoAbilitPreventiva = rinnovoAbilitPreventiva;
	}

	@Override
	public String getDataRichRinnovoAbilitPreventiva() {
		return this.dataRichRinnovoAbilitPreventiva;
	}

	@Override
	public void setDataRichRinnovoAbilitPreventiva(String dataRichRinnovoAbilitPreventiva) {
		this.dataRichRinnovoAbilitPreventiva = dataRichRinnovoAbilitPreventiva;
	}

	@Override
	public String[] getZoneAttivita() {
		return this.zoneAttivita;
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

	@Override
	public String getSoggettoNormativeDURC() {
		return this.soggettoNormativeDURC;
	}

	/**
	 * Genera la stringa da inviare al backoffice e contenente la codifica delle
	 * zone di attivit&agrave;, regione per regione, in cui:
	 * <ul>
	 * <li>0 indica non definito</li>
	 * <li>1 indica selezionata</li>
	 * <li>2 indica non selezionata</li>
	 * </ul>
	 *
	 * @param zoneAttivita elenco di venti cifre che codifica lo stato delle
	 * attivit&agrave; regione per regione, oppure null se le zone non sono
	 * gestite nel portale
	 * @return stringa di 20 cifre (0/1/2) oppure null se l'input non corrisponde
	 * all'elenco dei valori per le 20 regioni
	 */
	public static String getZoneAttivitaForBO(String[] zoneAttivita) {
		String risultato = null;

		if (zoneAttivita != null && zoneAttivita.length == 20) {
			StringBuilder appoggio = new StringBuilder("");
			for (int i = 0; i < 20; i++) {
				if ("".equals(zoneAttivita[i])) {
					appoggio.append("0");
				} else if ("1".equals(zoneAttivita[i])) {
					appoggio.append("1");
				} else if ("0".equals(zoneAttivita[i])) {
					appoggio.append("2");
				}
			}
			risultato = appoggio.toString();
		}
		return risultato;
	}

	/**
	 * Genera della stringa proveniente dal backoffice la codifica delle zone di
	 * attivit&agrave;, regione per regione, in cui:
	 * <ul>
	 * <li>"" indica non definito</li>
	 * <li>"1" indica selezionata</li>
	 * <li>"0" indica non selezionata</li>
	 * </ul>
	 *
	 * @param zoneAttivita stringa di venti cifre che codifica lo stato delle
	 * attivit&agrave; regione per regione, oppure null se le zone non sono
	 * gestite nel portale
	 * @return elenco di 20 cifre (0/1/2) oppure null se l'input non corrisponde
	 * all'elenco dei valori per le 20 regioni
	 */
	public static String[] getZoneAttivitaFromBO(String zoneAttivita) {
		String risultato[] = null;

		if (zoneAttivita != null && zoneAttivita.length() == 20) {
			risultato = new String[20];
			for (int i = 0; i < 20; i++) {
				if (zoneAttivita.charAt(i) == '0') {
					risultato[i] = "";
				} else if (zoneAttivita.charAt(i) == '1') {
					risultato[i] = "1";
				} else if (zoneAttivita.charAt(i) == '2') {
					risultato[i] = "0";
				}
			}
		}
		return risultato;
	}
	
	/**
	 * Genera la stringa da inviare al backoffice e contenente la codifica delle
	 * sezioni d'iscrizione della whitelist antimafia
	 */
	public static String getSezioniIscrizioneForBO(String[] sezioniIscrizione) {
		String risultato = null;
		if (sezioniIscrizione != null ) {
			StringBuilder appoggio = new StringBuilder("");
			for (int i = 0; i < sezioniIscrizione.length; i++) {
				if("".equals(sezioniIscrizione[i])) {
					// ignora
				} else {
					if(appoggio.length() > 0) {
						appoggio.append("-");
					}
					appoggio.append(sezioniIscrizione[i]);
				}
			}
			// verifica se la colonna nel db puo' contenere il valore
			if(appoggio.length() <= 20) {   
				risultato = appoggio.toString();
			}
		}
		return risultato;
	}

	/**
	 * Genera della stringa proveniente dal backoffice la codifica delle 
	 * sezioni d'iscrizione della whitelist antimafia
	 */
	public static String[] getSezioniIscrizioneFromBO(String sezioniIscrizione) {
		String risultato[] = null;
		if (sezioniIscrizione != null) {
			risultato = sezioniIscrizione.split("-");
		}
		return risultato;
	}

}
