package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import com.agiletec.aps.system.services.user.UserManager;
import it.eldasoft.www.sil.WSGareAppalto.OrderCriteria;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiCassaPrevidenzialeType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.XSSRequestPatterns;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.OpenPageImportImpresaAction;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Metodi per validare i vari parametri.
 *
 * I metodi devono ritornare la porzione di codice errata in caso di Pattern
 * o l'intera stringa in input in caso di FullMatch
 */
public final class ParamValidator {

	// ********************************************************************************    
    //START - Pattern
	// ********************************************************************************
    private static final Pattern ONLY_DIGIT         = Pattern.compile("\\d+");    
    private static final Pattern ALPHA_AND_DIGIT    = Pattern.compile("[A-Za-z\\d]+");
    private static final Pattern CIVIC_NUMBER       = Pattern.compile("[\\da-zA-Z\\/\\-]+");
//    private static final Pattern ONLY_ALPHA         = Pattern.compile("[A-Za-z]+");
    private static final Pattern NUMERO_DIPENDENTI  = Pattern.compile("[0-9]{0,5}");
//    private static final Pattern CODICE_BICCC       = Pattern.compile("^[a-zA-Z0-9]+$");  //Regex a codice
    private static final Pattern INTEGER 			= Pattern.compile("^[-+]?\\d+$");
    private static final Pattern FIRMATARIO			= Pattern.compile("[a-zA-Z0-9_-]+");		//es: LEGALI_RAPPRESENTANTI-0
    private static final Pattern COGNOME_NOME		= Pattern.compile("[\\p{IsLatin}'\\s]+");
    private static final Pattern COMUNE				= Pattern.compile("[\\p{IsLatin}'\\s]+");
    private static final Pattern INDIRIZZO			= Pattern.compile("[\\p{IsLatin}'\\s]+");
    
    /**
     * Regex presa direttamente dall'xsd di FatturaPA
     */
    private static final Pattern CODICE_BICCC       = Pattern.compile("^[a-zA-Z0-9]+$");

    private static final Pattern SPEC_SOG_QUALIFICA = Pattern.compile("\\d+-\\d+");
    private static final Pattern FILE_NAME          = Pattern.compile("^[^<>:;,?\"*|/']+$");
    private static final Pattern USERNAME           = Pattern.compile("[a-zA-Z0-9.]+");
    private static final Pattern TOKEN              = ALPHA_AND_DIGIT;
    /**
     * Regex presa direttamente dall'xsd di FatturaPA
     */
    private static final Pattern IBAN               = Pattern.compile("[a-zA-Z0-9\\d-]+");
//    private static final Pattern FISCAL_CODE        = Pattern.compile("[A-Za-z0-9]{11,16}");
    //I caratteri: . e - ; Sono per gli identificativi esteri.
    private static final Pattern FISCAL_CODE        = Pattern.compile("[A-Za-z0-9.-]+");
    private static final Pattern UUID               = Pattern.compile("^[A-Za-z0-9\\-_.]+$");
    private static final Pattern SERIAL_NUMBER      = Pattern.compile("(?i)[a-z.\\d]+");
    private static final Pattern QUANTITA           = Pattern.compile("^\\d+(?:\\.\\d{0,5})?$");
    private static final Pattern ALPHA              = Pattern.compile("[A-Za-z]+");
    private static final Pattern TITOLO_GARA        = Pattern.compile("[A-Za-z\\d\\-_.,;'\"/\\s:]+");
    private static final Pattern AUTENTICAZIONE     = Pattern.compile("[A-Za-z\\d.]+");
    private static final Pattern CODICE             = Pattern.compile("\\$?[A-Za-z\\d._/\\-\\s]+");
    private static final Pattern CODICE_CATEGORIA   = Pattern.compile("\\$?[A-Za-z\\d._/\\-\\s]+");
    private static final Pattern PASSWORD           = Pattern.compile("^[a-zA-Z0-9" + UserManager.SPECIAL_CHARS + "]+$");
    private static final Pattern TELEPHONE          = Pattern.compile("[\\d+\\-_/\\s]*"); //Controllo non preciso, in modo da non cancellare perforza il contenuto
    private static final Pattern ENTITA             = Pattern.compile("[A-Za-z\\s\\-\\_\\d]+");
    private static final Pattern STATO_PRODOTTO     = Pattern.compile("[A-Za-z\\s_\\d]+");
    private static final Pattern NUMERO_CONTO       = Pattern.compile("[A-Za-z\\d]+");
    private static final Pattern CODICE_CNEL        = Pattern.compile("[A-Za-z\\d]+|n.a.");

    private static final Pattern URL_RESTRICTED     = Pattern.compile("(?i)https?://(www.)?[a-z.\\d/:_-]+\\??(&?[a-z\\d]+=[a-z/\\.\\d]*)*");
    private static final Pattern EMAIL_ADDRESS		= Pattern.compile("^[\\w-.]+@[\\w-.]+");
    private static final Pattern DOMAIN_USER         = Pattern.compile("^[\\w.-]+$");

    private static final Pattern ACTION_PATH     	= Pattern.compile("(?i)[a-z./\\d]+");


    /**
     * 10.5 (Valido)
     * 10,5 (Valido)
     * 10 (Valido)
     * 10,5% (Valido)
     * 10% (Valido)
     * 10.5% (Valido)
     * .5% (Non valido)
     * .5 (Non valido)
     */
    private static final Pattern FLOAT_PERCENT      = Pattern.compile("^[\\-+]?\\d+(?:[.,]\\d+)?\\s*%?$");
    /**
     * Come FLOAT_PERCENT ma senza il percento
     */
    private static final Pattern FLOAT              = Pattern.compile("^[\\-+]?\\d+(?:[.,]\\d+)?\\s*$");

    private static SimpleDateFormat sdfISO8601 = new SimpleDateFormat("yyyy-MM-dd");
    
    // ********************************************************************************
    //END - Pattern
    // ********************************************************************************
    
    
    /**
     * Classe statica, quindi, elimina la possibilita' di istanziarla
     */
    private ParamValidator() { }

    // ********************************************************************************
    //START - Validatori
    // ********************************************************************************
    
    public static ParamValidationResult isRagioneSocialeValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 2000)
                , () -> xssRegexCheck(toValidate));
    }
    
    public static ParamValidationResult isNaturaGiuridicaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_NATURA_GIURIDICA);
    }
    
    public static ParamValidationResult isTipoImpresaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO);
    }
    
    public static ParamValidationResult isAmbitoTerritorialeValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_AMBITO_TERRITORIALE);
    }
    
    public static ParamValidationResult isCodiceFiscaleValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, FISCAL_CODE, 16);
    }
    
    public static ParamValidationResult isPartitaIVAValid(String toValidate) {
        //return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 16);
    	// adeguato com per CF per le nazioni extra EU (cina)
        return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 30);	
    }
    
    public static ParamValidationResult isSiNoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SINO);
    }
    
    public static ParamValidationResult isIndirizzoValid(String toValidate) {
    	return isEmptyOrMatchingAndInSize(toValidate, INDIRIZZO, 100);
    }
    
    public static ParamValidationResult isNumCivicoValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CIVIC_NUMBER, 10);
    }
    
    public static ParamValidationResult isCapValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ONLY_DIGIT, 5);
    }
    
    public static ParamValidationResult isComuneValid(String toValidate) {
    	return isEmptyOrMatchingAndInSize(toValidate, COMUNE, 100);
    }
    
    public static ParamValidationResult isProvinciaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_PROVINCE);
    }
    
    public static ParamValidationResult isNazioneValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_NAZIONI);
    }
    
    public static ParamValidationResult isTelefonoValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, TELEPHONE, 50);
    }
    
    public static ParamValidationResult isFaxValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, TELEPHONE, 20);
    }
    
    public static ParamValidationResult isDateInDDMMYYYYValid(String toValidate) {
        return isValidDateFromDateFormat(toValidate, WizardDatiImpresaHelper.DDMMYYYY);
    }
    
    public static ParamValidationResult isGenderValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SESSI);
    }
    
    public static ParamValidationResult isAddressTypeValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_INDIRIZZO);
    }
    
    public static ParamValidationResult isCassaPrevidenzialeValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_CASSA_PREVIDENZA);
    }
    
    public static ParamValidationResult isTechnicalTitleValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO);
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo16(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 16)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo50(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 50)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isCodiceGaraValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE, 20);
    }
    
    public static ParamValidationResult isCodiceCategoriaValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE_CATEGORIA, 30);
    }
    
    public static ParamValidationResult isIBANValid(String toValidate) {
        return isEmptyOrMatching(toValidate, IBAN);
    }
    
    public static ParamValidationResult isCognomeNomeValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, COGNOME_NOME, 80);
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo80(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 80)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo100(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 100)
                , () ->  xssRegexCheck(toValidate)
        );
    }    
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo300(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 300)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo2000(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 2000)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isNumeroDipendentiValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, NUMERO_DIPENDENTI, 5);
    }
    
    public static ParamValidationResult isCodiceBICCCValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE_BICCC, 11);
    }
    
    public static ParamValidationResult isSettoreProduttivoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SETTORI_PRODUTTIVI);
    }
    
    public static ParamValidationResult isLocalitaValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 100)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isPosizContribInpsValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 10)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isPosizAssicInailValid(String toValidate) {
        return concatIfValid(isEmptyOrLowerEqualOf(toValidate, 9)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isCodiceCassaEdileValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 4)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isNumIscrSOA(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 20)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isOrganismoCertificatoreSOA(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_CERTIFICATORI_SOA);
    }
    
    public static ParamValidationResult isOrganismoCertificatoreISO(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_CERTIFICATORI_ISO);
    }
    
    public static ParamValidationResult isRatingLegalitaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_RATING_LEGALE);
    }
    
    public static ParamValidationResult isRegimeFiscaleValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_REGIME_FISCALE);
    }
    
    public static ParamValidationResult isSettoreAttivitaEconomica(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SETTORE_ATTIVITA_ECONOMICA);
    }
    
    public static ParamValidationResult isClasseDimensioneValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_CLASSI_DIMENSIONE);
    }
    
    public static ParamValidationResult isAlphaNumeric(String toValidate) {
    	return isEmptyOrMatching(toValidate, ALPHA_AND_DIGIT);
    }
    
    public static ParamValidationResult isDigit(String toValidate) {
        return isEmptyOrMatching(toValidate, ONLY_DIGIT);
    }
    
    public static ParamValidationResult isInteger(String toValidate) {
        return isEmptyOrMatching(toValidate, INTEGER);
    }
    
//    public static ParamValidationResult isQualificaValid(String toValidate) {
//    	return concatIfValid(
//    			isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
//    			, () -> isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
//    		    , () -> isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_COLLABORAZIONE)
//    			);
//    }
    
    public static ParamValidationResult isSpecSogQualificaValid(String toValidate) {
        //return isEmptyOrMatching(toValidate, SPEC_SOG_QUALIFICA);
		return firstNull(
			() -> isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_SOGGETTO)			// (1-, 2-)
			, () -> isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)		// (3-1, 3-2, ...)
		    , () -> isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_COLLABORAZIONE)	// (4-1, 4-2, ...)
			);
    }
    
    public static ParamValidationResult isTipoImportValid(String toValidate) {
        String errorIn = StringUtils.isEmpty(toValidate)
                || OpenPageImportImpresaAction.XML_IMPORT_PORTALE.equals(toValidate)
                || OpenPageImportImpresaAction.XML_IMPORT_DGUE.equals(toValidate)
                ? null
                : toValidate;
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
    			.build();
    }
    
    public static ParamValidationResult isFileNameValid(String toValidate) {
        return isEmptyOrMatching(toValidate, FILE_NAME);
    }
    
    public static ParamValidationResult isContentType(String toValidate) {
    	return isUnlimitedTextValid(toValidate);
    }
    
    public static ParamValidationResult isUsernameValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, USERNAME, 20);
    }
    
    public static ParamValidationResult isTokenValid(String toValidate) {
        return isEmptyOrMatching(toValidate, TOKEN);
    }
    
    public static ParamValidationResult isProgressivoInvioValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 10)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isDateYYYYMMDDValid(String toValidate) {
        return isValidDateFromDateFormat(toValidate, sdfISO8601);
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo5(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 5)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isFloatPercentValid(String toValidate) {
        return isEmptyOrMatching(toValidate, FLOAT_PERCENT);
    }
    
    public static ParamValidationResult isFloatValid(String toValidate) {
        return isEmptyOrMatching(toValidate, FLOAT);
    }
    
    public static ParamValidationResult isDatCassaRitenutaValid(String toValidate) {
        String errorIn = StringUtils.isEmpty(toValidate)
                || Arrays.stream(DatiCassaPrevidenzialeType.RitenutaEnum.values())
                    .anyMatch(it -> it.getValue().equals(toValidate))
                ? null
                : toValidate;
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
    			.build();
    }
    
    public static ParamValidationResult isDatCassaNatura(String toValidate) {
    	String errorIn = StringUtils.isEmpty(toValidate)
                || Arrays.stream(DatiCassaPrevidenzialeType.NaturaEnum.values())
                    .anyMatch(it -> it.getValue().equals(toValidate))
                ? null
                : toValidate;
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
    			.build();
    }

    public static ParamValidationResult isDatCassaTipoCassa(String toValidate) {
    	String errorIn = StringUtils.isEmpty(toValidate)
                || Arrays.stream(DatiCassaPrevidenzialeType.TipoCassaEnum.values())
                    .anyMatch(it -> it.getValue().equals(toValidate))
                ? null
                : toValidate;
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
    			.build();
    }
    
    public static ParamValidationResult isCIGValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 10);
    }
    
    public static ParamValidationResult isMotivoRifiutoValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 2000)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isModuleNameValid(String toValidate) {
        return isEmptyOrMatching(toValidate, ALPHA_AND_DIGIT);
    }
    
    public static ParamValidationResult isTipoRichiestaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPOLOGIE_ASSISTENZA);
    }
    
    public static ParamValidationResult isTipoAppaltoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_APPALTO);
    }
    
    public static ParamValidationResult isTipoAltriSoggettiValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPO_ALTRI_SOGGETTI);
    }
    
    //Le classifica hanno sempre valore numerico (positivo o negativo). Fanno parte di pi� tabellati.
    public static ParamValidationResult isClasseValid(String toValidate) {
        return isEmptyOrMatching(toValidate, INTEGER);
    }

    public static ParamValidationResult isUUIDDValid(String toValidate) {
        return isEmptyOrMatching(toValidate, UUID);
    }
    
    public static ParamValidationResult isPassOEValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 30)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isSerialNumberValid(String toValidate) {
        return isEmptyOrMatching(toValidate, SERIAL_NUMBER);
    }

    public static ParamValidationResult isCodiceProdottoValid(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 30)
                ,() -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isNomeCommercialeValid(String toValidate) {
    	return isEmptyOrLowerEqualOf(toValidate, 250);
    }
    
    public static ParamValidationResult isEmptyOrLowerOrEqualTo60(String toValidate) {
        return concatIfValid(
                isEmptyOrLowerEqualOf(toValidate, 60)
                , () -> xssRegexCheck(toValidate)
        );
    }
    
    public static ParamValidationResult isQuantitaValid(String toValidate) {
        return isEmptyOrMatching(toValidate, QUANTITA);
    }
    
    public static ParamValidationResult isTipoComunicazioneValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 5);
    }   //Alphanumerico
    
    public static ParamValidationResult isCodiceImpresaValid(String toValidate) {
        return isEmptyOrMatching(toValidate, ALPHA_AND_DIGIT);
    }   //Alphanumerico
    
    public static ParamValidationResult isUrlValid(String toValidate) {
        return isEmptyOrMatching(toValidate, URL_RESTRICTED);
    }
    
    public static ParamValidationResult isEmailValid(String toValidate) {
        return isEmptyOrMatching(toValidate, EMAIL_ADDRESS);
    }
    
    public static ParamValidationResult isDomainUserValid(String toValidate) {
        return isEmptyOrMatching(toValidate, DOMAIN_USER);
    }
    
    public static ParamValidationResult isAutenticazioniValid(String toValidate) {
        return isEmptyOrMatching(toValidate, AUTENTICAZIONE);
    }   //alfanumerico e punti
    
    public static ParamValidationResult isIdComunicazioneValid(String toValidate) {
        return isEmptyOrMatching(toValidate, ONLY_DIGIT);
    }
    
//    public static Boolean isActionValid(String toValidate) {
//        return StringUtils.isEmpty(toValidate)
//                || ALPHA_AND_DIGIT.matcher(toValidate).matches();
//    }   //Alphanumerico
    
    public static ParamValidationResult isActionValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    
    public static ParamValidationResult isTitoloGaraValid(String toValidate) {
        return isEmptyOrMatching(toValidate, TITOLO_GARA);
    }
    
    public static ParamValidationResult isStatoValid(String toValidate) {
        return isEmptyOrMatching(toValidate, ALPHA_AND_DIGIT);
    }   //Alphanumerico
    
    public static ParamValidationResult isStatoProdottoValid(String toValidate) {
        return isEmptyOrMatching(toValidate, STATO_PRODOTTO);
    }   //Alphanumerico

    public static ParamValidationResult isEsitoGaraValid(String toValidate) {
        return isEmptyOrMatching(toValidate, ALPHA_AND_DIGIT);
    }   //Come stato
    
    public static ParamValidationResult isCodiceStipulaValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    
    public static ParamValidationResult isCodiceContrattoValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    
    public static ParamValidationResult isEntitaValid(String toValidate) {
        return isEmptyOrMatching(toValidate, ENTITA);
    }
    
    public static ParamValidationResult isTipoAvvisoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_AVVISO);
    }
    
    public static ParamValidationResult isTipoAvvisoGeneraliValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_AVVISO_GENERALI);
    }
    
    public static ParamValidationResult isFirmatario(String toValidate) {
    	return isEmptyOrMatching(toValidate, FIRMATARIO);
    }    
    public static ParamValidationResult isTipoProceduraValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_PROCEDURA);
    }

    public static ParamValidationResult isPasswordValid(String toValidate) {
        return isEmptyOrMatching(toValidate, PASSWORD);
    }
    
    public static ParamValidationResult isTipoSocietaCooperativaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE);
    }
    
    public static ParamValidationResult isCodFiscOIdentificativo(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, FISCAL_CODE, 30);
    }
    
    public static ParamValidationResult isValidNumeroConto(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, NUMERO_CONTO, 50);
    }
    
    public static ParamValidationResult isCNELValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE_CNEL, 4);
    }
    
    public static ParamValidationResult isValidOrderCriteria(String toValidate) {
        String errorIn = StringUtils.isEmpty(toValidate) || falseOnThrows(() -> {
            OrderCriteria.fromString(toValidate);
            return true;
        }) ? null : toValidate;
        return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
    			.build();
    }

    public static ParamValidationResult isActionPathValid(String s) {
        return isEmptyOrMatching(s, ACTION_PATH);
    }

    /**
     * Determines if the given string is a valid orderable identifier.
     *
     * @param s the string to be checked
     * @return a String representing the not valid portion of the string or null if valid.
     */
    public static ParamValidationResult isOrderableIdentifierValid(String s) {
        return isEmptyOrMatching(s, ALPHA_AND_DIGIT);
    }

    public static ParamValidationResult isUnlimitedTextValid(String toValidate) {
    	return xssRegexCheck(toValidate);
    }

    // ********************************************************************************
    //END - Validatori
    // ********************************************************************************
    
    /**
     * Validazione el parametro tramite le regex di validazione
     *
     * @param toValidate
     * @return
     */
    private static ParamValidationResult xssRegexCheck(String toValidate) {
        String errorIn = null;
        if (StringUtils.isNotEmpty(toValidate))
            for (Pattern pattern : XSSRequestPatterns.XXS_PATTERNS) {
                Matcher matcher = pattern.matcher(toValidate);
                if (matcher.find()) {
                    errorIn = matcher.group(0);
                    break;
                }
            }
    	return ParamValidationResultBuilder.newValidator()
    			.setMessageError(WithError.DEFAULT_TEXT_ERROR_MALWARE)
    			.setInvalidPart(errorIn)
				.build();
    }

    /**
     * Utilizzato un concat con Supplier per rendere l'evaluation delle "condizioni" Lazy.
     *
     * @param firstCheck La prima condizione
     * @param otherChecks Evaluation lazy di ulteriori condizioni
     * @return
     */
    @SafeVarargs
	private static ParamValidationResult concatIfValid(ParamValidationResult firstCheck, Supplier<ParamValidationResult> ... otherChecks) {
        return firstCheck != null
                ? firstCheck
                : firstNotNull(otherChecks);
    }
    
	private static ParamValidationResult firstNotNull(Supplier<ParamValidationResult>[] otherChecks) {
        String errorIn = null;
        String msgError = null; 

        for (Supplier<ParamValidationResult> expression : otherChecks) {
        	ParamValidationResult current = expression.get();
        	if(current != null)
	            if (StringUtils.isNotEmpty(current.getInvalidPart())) {
	                errorIn = current.getInvalidPart();
	                msgError = current.getMessageError();
	                break;
	            }
        }

        return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
    			.setMessageError(msgError != null ? msgError : WithError.DEFAULT_TEXT_ERROR)
    			.build();
    }

    @SafeVarargs
	private static ParamValidationResult firstNull(Supplier<ParamValidationResult> ... otherChecks) {
    	ParamValidationResult errorIn = null;
        for (Supplier<ParamValidationResult> expression : otherChecks) {
        	ParamValidationResult current = expression.get();
        	if(current != null) { 
	            if (StringUtils.isEmpty(current.getInvalidPart())) {
	                errorIn = null;
	                break;
	            }
	            // traccia solo l'ultimo errore trovato
	            errorIn = current;
        	}
        }
    	return ParamValidationResultBuilder.newValidator()
			.setInvalidPart( errorIn != null ? errorIn.getInvalidPart() : null )
			.setInvalidPart( errorIn != null ? errorIn.getMessageError() : null )
			.build();
    }

    /**
     * Validazione tramite regex e controllo di lunghezza massima.
     *
     * @param toValidate
     * @param regex
     * @param maxLength
     * @return
     */
    private static ParamValidationResult isEmptyOrMatchingAndInSize(String toValidate, Pattern regex, int maxLength) {
        String errorIn = null;
        if (StringUtils.isNotEmpty(toValidate)) {
            if (toValidate.length() > maxLength)
                errorIn = toValidate.substring(maxLength);
            else {
            	ParamValidationResult res = isEmptyOrMatching(toValidate, regex);
                errorIn = (res != null ? res.getInvalidPart() : null); 
            }
        }
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
				.build();
    }

    /**
     * Se vuoto o se la linkedhashmap ritornata dall'interceptor contiene il valore passato come parametro
     * il parametro e' valido, altrimenti, se la linkedhashmap non contiene il valore, o se c'e' un errore durante
     * il recupero dei dati dall'interceptor, ritorno false.
     * NB: il falseOnThrows ritorna false in caso l'espressione inserita come parametro ritorni errore.
     *
     * @param toValidate
     * @param listNameOnInterceptor
     * @return
     */
    private static ParamValidationResult isEmptyOrInInterceptor(String toValidate, String listNameOnInterceptor) {
        String errorIn = StringUtils.isEmpty(toValidate)
                || falseOnThrows(() -> InterceptorEncodedData.get(listNameOnInterceptor).containsKey(toValidate))
                ? null				// il valore e' nella lista
                : toValidate;		// il valore NON e' nella lista
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
				.build();
    }

    /**
     * Controllo la lunghezza massima del campo.
     *
     * @param toValidate
     * @param maxLength
     * @return
     */
    private static ParamValidationResult isEmptyOrLowerEqualOf(String toValidate, int maxLength) {
        String errorIn = StringUtils.isNotEmpty(toValidate) && toValidate.length() > maxLength
                ? toValidate.substring(maxLength)
                : null;
        return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
				.build();
    }

    private static ParamValidationResult isEmptyOrMatching(String toValidate, Pattern regex) {
        String errorIn = null;
        if (StringUtils.isNotEmpty(toValidate)) {
            Matcher matcher = regex.matcher(toValidate);
            if (!matcher.matches())
                errorIn = getNotMatching(toValidate, matcher);
        }
        return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
				.build();
    }

    private static String getNotMatching(String input, Matcher matcher) {
    	return !matcher.find()
    			? input
    			: matcher.replaceAll("");
    }

    /**
     * Valido una data
     * NB: il falseOnThrows ritorna false in caso l'espressione inserita come parametro ritorni errore.
     *
     * @param toValidate
     * @param dateFormat
     * @return
     */
    private static ParamValidationResult isValidDateFromDateFormat(String toValidate, SimpleDateFormat dateFormat) {
        String errorIn = StringUtils.isEmpty(toValidate) || falseOnThrows(() -> {
            Date parsed = dateFormat.parse(toValidate);
            return parsed != null && StringUtils.equals(dateFormat.format(parsed), toValidate);
        }) ? null : toValidate;
    	return ParamValidationResultBuilder.newValidator()
    			.setInvalidPart(errorIn)
				.build();
    }

    /**
     * Interfaccia che rappresenta la lambda passata come parametro sul metodo falseOnThrows(...)
     */
    @FunctionalInterface
    private interface ThrowingSupplier {
        boolean get() throws Exception;
    }

    /**
     * un semplice metodo ghe effettua la get da una lambda e ne ritorna il boolean ritornato.
     * In caso questa lambda ritorni errore, viene ritornato false
     * @param tr
     * @return
     */
    private static boolean falseOnThrows(ThrowingSupplier tr) {
        boolean toReturn = false;

        try {
            toReturn = tr.get();
        } catch (Exception ignored) { }

        return toReturn;
    }

}