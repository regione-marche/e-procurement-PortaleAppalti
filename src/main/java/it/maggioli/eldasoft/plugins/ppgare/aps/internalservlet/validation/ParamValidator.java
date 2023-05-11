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
import java.util.regex.Pattern;

/**
 * Metodi per validare i vari parametri.
 */
public final class ParamValidator {

    //START - Pattern
    private static final Pattern ONLY_DIGIT         = Pattern.compile("\\d+");    
    private static final Pattern ALPHA_AND_DIGIT    = Pattern.compile("[A-Za-z\\d]+");
    private static final Pattern CIVIC_NUMBER       = Pattern.compile("[\\da-zA-Z\\/\\-]+");
//    private static final Pattern ONLY_ALPHA         = Pattern.compile("[A-Za-z]+");
    private static final Pattern NUMERO_DIPENDENTI  = Pattern.compile("[0-9]{0,5}");
//    private static final Pattern CODICE_BICCC       = Pattern.compile("^[a-zA-Z0-9]+$");  //Regex a codice
    private static final Pattern INTEGER 			= Pattern.compile("^[-+]?\\d+$");
    private static final Pattern FIRMATARIO			= Pattern.compile("[a-zA-Z0-9_-]+");		//es: LEGALI_RAPPRESENTANTI-0
    
    /**
     * Regex presa direttamente dall'xsd di FatturaPA
     */
    private static final Pattern CODICE_BICCC       = Pattern.compile("[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?");

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
    private static final Pattern UUID               = Pattern.compile("^[A-Za-z0-9\\-]+$");
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
    private static final Pattern STATO_PRODOTTO             = Pattern.compile("[A-Za-z\\s_\\d]+");

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

    //END - Pattern


    private ParamValidator() { }    //Classe statica, quindi, tolgo la possibilit� di istanziarla

    //START - Validatori

    public static boolean isRagioneSocialeValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 2000) && xssRegexCheck(toValidate);
    }
    public static boolean isNaturaGiuridicaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_NATURA_GIURIDICA);
    }
    public static boolean isTipoImpresaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO);
    }
    public static boolean isAmbitoTerritorialeValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_AMBITO_TERRITORIALE);
    }
    public static boolean isCodiceFiscaleValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, FISCAL_CODE, 16);
    }
    public static boolean isPartitaIVAValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 16);
    }
    public static boolean isSiNoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SINO);
    }
    public static boolean isNumCivicoValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CIVIC_NUMBER, 10);
    }
    public static boolean isCapValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ONLY_DIGIT, 5);
    }
    public static boolean isComuneValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 100) && xssRegexCheck(toValidate);
    }
    public static boolean isProvinciaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_PROVINCE);
    }
    public static boolean isNazioneValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_NAZIONI);
    }
    public static boolean isTelefonoValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, TELEPHONE, 50);
    }
    public static boolean isFaxValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, TELEPHONE, 20);
    }
    public static boolean isDateInDDMMYYYYValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || (toValidate.length() <= 10
                && isValidDateFromDateFormat(toValidate, WizardDatiImpresaHelper.DDMMYYYY));
    }
    public static boolean isGenderValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SESSI);
    }
    public static boolean isAddressTypeValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_INDIRIZZO);
    }
    public static boolean isCassaPrevidenzialeValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_CASSA_PREVIDENZA);
    }
    public static boolean isTechnicalTitleValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO);
    }
    public static boolean isEmptyOrLowerOrEqualTo16(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 16) && xssRegexCheck(toValidate);
    }
    public static boolean isEmptyOrLowerOrEqualTo50(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 50) && xssRegexCheck(toValidate);
    }
    public static boolean isCodiceGaraValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE, 20);
    }
    public static boolean isCodiceCategoriaValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE_CATEGORIA, 20);
    }    
    public static boolean isIBANValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || IBAN.matcher(toValidate).matches();
    }
    public static boolean isEmptyOrLowerOrEqualTo80(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 80) && xssRegexCheck(toValidate);
    }
    public static Boolean isEmptyOrLowerOrEqualTo100(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 100) && xssRegexCheck(toValidate);
    }
    public static boolean isEmptyOrLowerOrEqualTo300(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 300) && xssRegexCheck(toValidate);
    }
    public static boolean isEmptyOrLowerOrEqualTo2000(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 2000) && xssRegexCheck(toValidate);
    }
    public static boolean isNumeroDipendentiValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, NUMERO_DIPENDENTI, 5);
    }
    public static boolean isCodiceBICCCValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, CODICE_BICCC, 11);
    }
    public static boolean isSettoreProduttivoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SETTORI_PRODUTTIVI);
    }
    public static boolean isLocalitaValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 100) && xssRegexCheck(toValidate);
    }
    public static boolean isPosizContribInpsValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 10) && xssRegexCheck(toValidate);
    }
    public static boolean isPosizAssicInailValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 9) && xssRegexCheck(toValidate);
    }
    public static boolean isCodiceCassaEdileValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 4) && xssRegexCheck(toValidate);
    }
    public static boolean isNumIscrSOA(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 20) && xssRegexCheck(toValidate);
    }
    public static boolean isOrganismoCertificatoreSOA(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_CERTIFICATORI_SOA);
    }
    public static boolean isOrganismoCertificatoreISO(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_CERTIFICATORI_ISO);
    }
    public static boolean isRatingLegalitaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_RATING_LEGALE);
    }
    public static boolean isRegimeFiscaleValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_REGIME_FISCALE);
    }
    public static boolean isSettoreAttivitaEconomica(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_SETTORE_ATTIVITA_ECONOMICA);
    }
    public static boolean isClasseDimensioneValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_CLASSI_DIMENSIONE);
    }    
    public static boolean isAlphaNumeric(String toValidate) {
    	return StringUtils.isEmpty(toValidate) || ALPHA_AND_DIGIT.matcher(toValidate).matches();
    }
    public static boolean isDigit(String toValidate) {
        return StringUtils.isEmpty(toValidate) || ONLY_DIGIT.matcher(toValidate).matches();
    }
    public static boolean isInteger(String toValidate) {
        return StringUtils.isEmpty(toValidate) || INTEGER.matcher(toValidate).matches();
    }
    public static boolean isQualificaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
                || isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
                || isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_COLLABORAZIONE);
    }
    public static boolean isSpecSogQualificaValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || SPEC_SOG_QUALIFICA.matcher(toValidate).matches();
    }
    public static Boolean isTipoImportValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || OpenPageImportImpresaAction.XML_IMPORT_PORTALE.equals(toValidate)
                || OpenPageImportImpresaAction.XML_IMPORT_DGUE.equals(toValidate);
    }
    public static Boolean isFileNameValid(String toValidate) {
        return StringUtils.isEmpty(toValidate) || FILE_NAME.matcher(toValidate).matches();
    }
    public static Boolean isContentType(String toValidate) {
    	return isUnlimitedTextValid(toValidate);
    }    
    public static Boolean isUsernameValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, USERNAME, 20);
    }
    public static Boolean isTokenValid(String toValidate) {
        return StringUtils.isEmpty(toValidate) || TOKEN.matcher(toValidate).matches();
    }
    public static Boolean isProgressivoInvioValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 10) && xssRegexCheck(toValidate);
    }
    public static Boolean isDateYYYYMMDDValid(String toValidate) {
        return isValidDateFromDateFormat(toValidate, sdfISO8601);
    }
    public static Boolean isEmptyOrLowerOrEqualTo5(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 5) && xssRegexCheck(toValidate);
    }
    public static Boolean isFloatPercentValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || FLOAT_PERCENT.matcher(toValidate).matches();
    }
    public static Boolean isFloatValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || FLOAT.matcher(toValidate).matches();
    }
    public static Boolean isDatCassaRitenutaValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || Arrays.stream(DatiCassaPrevidenzialeType.RitenutaEnum.values())
                    .anyMatch(it -> it.getValue().equals(toValidate));
    }
    public static Boolean isDatCassaNatura(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || Arrays.stream(DatiCassaPrevidenzialeType.NaturaEnum.values())
                    .anyMatch(it -> it.getValue().equals(toValidate));

    }

    public static Boolean isDatCassaTipoCassa(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || Arrays.stream(DatiCassaPrevidenzialeType.TipoCassaEnum.values())
                    .anyMatch(it -> it.getValue().equals(toValidate));
    }
    public static Boolean isCIGValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 10);
    }
    public static Boolean isMotivoRifiutoValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 2000) && xssRegexCheck(toValidate);
    }
    public static Boolean isModuleNameValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || ALPHA_AND_DIGIT.matcher(toValidate).matches();
    }
    public static Boolean isTipoRichiestaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPOLOGIE_ASSISTENZA);
    }
    public static Boolean isTipoAppaltoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_APPALTO);
    }
    public static Boolean isTipoAltriSoggettiValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPO_ALTRI_SOGGETTI);
    }
    //Le classifica hanno sempre valore numerico (positivo o negativo). Fanno parte di pi� tabellati.
    public static Boolean isClasseValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || INTEGER.matcher(toValidate).matches();
    }

    public static Boolean isUUIDDValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || UUID.matcher(toValidate).matches();
    }
    public static Boolean isPassOEValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 30)
                && xssRegexCheck(toValidate);
    }
    public static Boolean isSerialNumberValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || SERIAL_NUMBER.matcher(toValidate).matches();
    }

    public static Boolean isCodiceProdottoValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 30)
                && xssRegexCheck(toValidate);
    }
    public static Boolean isNomeCommercialeValid(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 250);
    }
    public static boolean isEmptyOrLowerOrEqualTo60(String toValidate) {
        return isEmptyOrLowerEqualOf(toValidate, 60)
                && xssRegexCheck(toValidate);
    }
    public static boolean isQuantitaValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || QUANTITA.matcher(toValidate).matches();
    }
    public static Boolean isTipoComunicazioneValid(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, ALPHA_AND_DIGIT, 5);
    }   //Alphanumerico
    public static Boolean isCodiceImpresaValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || ALPHA_AND_DIGIT.matcher(toValidate).matches();
    }   //Alphanumerico
    public static Boolean isUrlValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    public static Boolean isAutenticazioniValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || AUTENTICAZIONE.matcher(toValidate).matches();
    }   //alfanumerico e punti
    public static Boolean isIdComunicazioneValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || ONLY_DIGIT.matcher(toValidate).matches();
    }
//    public static Boolean isActionValid(String toValidate) {
//        return StringUtils.isEmpty(toValidate)
//                || ALPHA_AND_DIGIT.matcher(toValidate).matches();
//    }   //Alphanumerico
    public static Boolean isActionValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    public static Boolean isTitoloGaraValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || TITOLO_GARA.matcher(toValidate).matches();
    }
    public static Boolean isStatoValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || ALPHA_AND_DIGIT.matcher(toValidate).matches();
    }   //Alphanumerico
    public static Boolean isStatoProdottoValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || STATO_PRODOTTO.matcher(toValidate).matches();
    }   //Alphanumerico

    public static Boolean isEsitoGaraValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || ALPHA_AND_DIGIT.matcher(toValidate).matches();
    }   //Come stato
    public static Boolean isCodiceStipulaValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    public static Boolean isCodiceContrattoValid(String toValidate) {
        return isUnlimitedTextValid(toValidate);
    }
    public static Boolean isEntitaValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || ENTITA.matcher(toValidate).matches();
    }
    public static Boolean isTipoAvvisoValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_AVVISO);
    }
    public static boolean isFirmatario(String toValidate) {
    	return StringUtils.isEmpty(toValidate) || FIRMATARIO.matcher(toValidate).matches();
    }    
    public static Boolean isTipoProceduraValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPI_PROCEDURA);
    }

    public static Boolean isPasswordValid(String toValidate) {
        return StringUtils.isEmpty(toValidate)
                || PASSWORD.matcher(toValidate).matches();
    }
    public static Boolean isTipoSocietaCooperativaValid(String toValidate) {
        return isEmptyOrInInterceptor(toValidate, InterceptorEncodedData.LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE);
    }
    public static Boolean isCodFiscOIdentificativo(String toValidate) {
        return isEmptyOrMatchingAndInSize(toValidate, FISCAL_CODE, 16);
    }
    //START - NON ANCORA BEN DEFINITI

    //END - NON ANCORA BEN DEFINITI

    //END - Validatori


    public static Boolean isUnlimitedTextValid(String toValidate) {
        return StringUtils.isEmpty(toValidate) || xssRegexCheck(toValidate);
    }

    /**
     * Validazione el parametro tramite le regex di validazione
     *
     * @param toValidate
     * @return
     */
    private static boolean xssRegexCheck(String toValidate) {
        return Arrays.stream(XSSRequestPatterns.XXS_PATTERNS).noneMatch(xss -> xss.matcher(toValidate).find());
    }

    /**
     * Validazione tramite regex e controllo di lunghezza massima.
     *
     * @param toValidate
     * @param regex
     * @param maxLength
     * @return
     */
    private static boolean isEmptyOrMatchingAndInSize(String toValidate, Pattern regex, int maxLength) {
        return StringUtils.isEmpty(toValidate)
                || (toValidate.length() <= maxLength
                && regex.matcher(toValidate).matches());
    }

    /**
     * Se vuoto o se la linkedhashmap ritornata dall'interceptor contiene il valore passato come parametro
     * il parametro � valido, altrimenti, se la linkedhashmap non contiene il valore, o se c'� un errore durante
     * il recupero dei dati dall'interceptor, ritorno false.
     * NB: il falseOnThrows ritorna false in caso l'espressione inserita come parametro ritorni errore.
     *
     * @param toValidate
     * @param listNameOnInterceptor
     * @return
     */
    private static boolean isEmptyOrInInterceptor(String toValidate, String listNameOnInterceptor) {
        return StringUtils.isEmpty(toValidate)
                || falseOnThrows(() -> InterceptorEncodedData.get(listNameOnInterceptor).containsKey(toValidate));
    }

    /**
     * Controllo la lunghezza massima del campo.
     *
     * @param toValidate
     * @param maxLength
     * @return
     */
    private static boolean isEmptyOrLowerEqualOf(String toValidate, int maxLength) {
        return StringUtils.isEmpty(toValidate)
                || toValidate.length() <= maxLength;
    }

    /**
     * Valido una data
     * NB: il falseOnThrows ritorna false in caso l'espressione inserita come parametro ritorni errore.
     *
     * @param toValidate
     * @param dateFormat
     * @return
     */
    private static boolean isValidDateFromDateFormat(String toValidate, SimpleDateFormat dateFormat) {
        return falseOnThrows(() -> {
            Date parsed = dateFormat.parse(toValidate);
            return parsed != null && StringUtils.equals(dateFormat.format(parsed), toValidate);
        });
    }

    public static Boolean isValidOrderCriteria(String s) {
        return StringUtils.isEmpty(s) || falseOnThrows(() -> {
            OrderCriteria.fromString(s);
            return true;
        });
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
