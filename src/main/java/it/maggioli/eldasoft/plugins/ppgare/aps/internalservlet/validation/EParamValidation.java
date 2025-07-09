package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import java.util.function.Function;

/**
 * Lista di enum che definiscono i vari tipi di validazione.
 * <br/>
 * Il parametro \u00E8 il metodo che effettuer\u00E0 la validazione.
 * <br/>
 * NB: Per ora sono supportate solo la validazione di stringhe in quanto
 * è l'unico oggetto che può causare problemi di cross site scripting.
 *
 * La variabile: Function<String, String> validator; corrisponde al validatore da richiamare per validare
 * l'enum corrispondente
 *
 * L'enum NONE è stato creato per rappresentare gli oggetti non primitivi o wrapper, quindi,
 * si esorta a non utilizzarlo su oggetti primiti o wrapper.
 */
public enum EParamValidation {

    ABI(ParamValidator::isEmptyOrLowerOrEqualTo5),
    ACTION(ParamValidator::isActionValid),
    ALBO(ParamValidator::isEmptyOrLowerOrEqualTo80),
    ALIQUOTA(ParamValidator::isFloatPercentValid),
    PERCENTUALE(ParamValidator::isFloatPercentValid),
    ALTRE_CERT_ATTESTAZ(ParamValidator::isUnlimitedTextValid),
    ALTRI_ISTITUTI_PREVIDENZIALI(ParamValidator::isUnlimitedTextValid),
    AMBITO_TERRITORIALE(ParamValidator::isAmbitoTerritorialeValid),
    AUTENTICAZIONI(ParamValidator::isAutenticazioniValid),
    BICC(ParamValidator::isCodiceBICCCValid),
    CAB(ParamValidator::isEmptyOrLowerOrEqualTo5),
    CAP(ParamValidator::isCapValid),
    CASSA_PREVIDENZIALE(ParamValidator::isCassaPrevidenzialeValid),
    CIG(ParamValidator::isCIGValid),
    CLASSE_DIMENSIONE(ParamValidator::isClasseDimensioneValid),
    CODICE_CASSA_EDILE(ParamValidator::isCodiceCassaEdileValid),
    CODICE_CONTRATTO(ParamValidator::isCodiceContrattoValid),
    CODICE_FISCALE(ParamValidator::isCodiceFiscaleValid),
    CODICE(ParamValidator::isCodiceGaraValid),
    CODICE_CATEGORIA(ParamValidator::isCodiceCategoriaValid),
    CODICE_STIPULA(ParamValidator::isCodiceStipulaValid),
    CODICE_IMPRESA(ParamValidator::isCodiceImpresaValid),
    COGNOME(ParamValidator::isCognomeNomeValid),
    COMUNE(ParamValidator::isComuneValid),
    DATE_DDMMYYYY(ParamValidator::isDateInDDMMYYYYValid),
    DATE_YYYYMMDD(ParamValidator::isDateYYYYMMDDValid),
    DAT_CASSA_NATURA(ParamValidator::isDatCassaNatura),
    DAT_CASSA_RITENUTA(ParamValidator::isDatCassaRitenutaValid),
    DAT_CASSA_TIPO_CASSA(ParamValidator::isDatCassaTipoCassa),
    DIGIT(ParamValidator::isDigit),
    EMAIL(ParamValidator::isEmailValid),
    DOMAIN_USER(ParamValidator::isDomainUserValid),
    ESITO_GARA(ParamValidator::isEsitoGaraValid),
    FAX(ParamValidator::isFaxValid),
    FILE_NAME(ParamValidator::isFileNameValid),
    GENDER(ParamValidator::isGenderValid),
    GENERIC(ParamValidator::isUnlimitedTextValid),
    IBAN(ParamValidator::isIBANValid),
    ID_COMUNICAZIONE(ParamValidator::isIdComunicazioneValid),
    IMPONIBILE(ParamValidator::isFloatValid),
    IMPORTO(ParamValidator::isFloatValid),
    INDIRIZZO(ParamValidator::isIndirizzoValid),
    ISTITUTO_FINANZIARIO(ParamValidator::isEmptyOrLowerOrEqualTo80),
    IUV(ParamValidator::isEmptyOrLowerOrEqualTo50),
    LOCALITA(ParamValidator::isLocalitaValid),
    MODULE_NAME(ParamValidator::isModuleNameValid),
    MOTIVO_RIFIUTO(ParamValidator::isMotivoRifiutoValid),
    NATURA_GIURIDICA(ParamValidator::isNaturaGiuridicaValid),
    NAZIONE(ParamValidator::isNazioneValid),
    NOME(ParamValidator::isCognomeNomeValid),
    NOTE(ParamValidator::isUnlimitedTextValid),
    NUMERO_DIPENDENTI(ParamValidator::isNumeroDipendentiValid),
    NUMERO_ISCR_ALBO(ParamValidator::isEmptyOrLowerOrEqualTo50),
    NUM_CASSA_PREVIDENZIALE(ParamValidator::isEmptyOrLowerOrEqualTo16),
    NUM_CIVICO(ParamValidator::isNumCivicoValid),
    NUM_ISCR_CASSA_EDILE(ParamValidator::isEmptyOrLowerOrEqualTo16),
    NUM_ISCR_CCIAA(ParamValidator::isEmptyOrLowerOrEqualTo16),
    NUM_ISCR_INAIL(ParamValidator::isEmptyOrLowerOrEqualTo16),
    NUM_ISCR_INPS(ParamValidator::isEmptyOrLowerOrEqualTo16),
    NUM_ISCR_ISO(ParamValidator::isEmptyOrLowerOrEqualTo16),
    NUM_ISCR_SOA(ParamValidator::isNumIscrSOA),
    NUM_REGISTRO_DITTE(ParamValidator::isEmptyOrLowerOrEqualTo16),
    OGGETO_SOCIALE(ParamValidator::isUnlimitedTextValid),
    ORGANISMO_CERT_ISO(ParamValidator::isOrganismoCertificatoreISO),
    ORGANISMO_CERT_SOA(ParamValidator::isOrganismoCertificatoreSOA),
    PARTITA_IVA(ParamValidator::isPartitaIVAValid),
    POSIZ_ASSIC_INAIL(ParamValidator::isPosizAssicInailValid),
    POSIZ_CONTRIB_INPS(ParamValidator::isPosizContribInpsValid),
    PROGRESSIVO_INVIO(ParamValidator::isProgressivoInvioValid),
    PROVINCIA(ParamValidator::isProvinciaValid),
    QUALIFICA(ParamValidator::isDigit),
    RAGIONE_SOCIALE(ParamValidator::isRagioneSocialeValid),
    RATING_LEGALITA(ParamValidator::isRatingLegalitaValid),
    REFERENTE(ParamValidator::isEmptyOrLowerOrEqualTo50),
    REGIME_FISCALE(ParamValidator::isRegimeFiscaleValid),
    RIFERIMENTO_PROCEDURA(ParamValidator::isCodiceGaraValid),
    SEDE_PREFETT_W_ANTIM(ParamValidator::isEmptyOrLowerOrEqualTo50),
    SETTORE_ATTIVITA_ECONOMICA(ParamValidator::isSettoreAttivitaEconomica),
    SETTORE_PRODUTTIVO(ParamValidator::isSettoreProduttivoValid),
    SITO_WEB(ParamValidator::isEmptyOrLowerOrEqualTo60),
    SI_NO(ParamValidator::isSiNoValid),
    SOGG_ABILITATI_CC(ParamValidator::isUnlimitedTextValid),
    SPEC_SOGG_QUALIFICA(ParamValidator::isSpecSogQualificaValid),
    STATO(ParamValidator::isStatoValid),
    STATO_PRODOTTO(ParamValidator::isStatoProdottoValid),
    STAZIONE_APPALTANTE(ParamValidator::isRagioneSocialeValid),
    TELEFONO(ParamValidator::isTelefonoValid),
    TIPO_ALTRI_SOGGETTI(ParamValidator::isTipoAltriSoggettiValid),
    TIPO_APPALTO(ParamValidator::isTipoAppaltoValid),
    TIPO_COMUNICAZIONE(ParamValidator::isTipoComunicazioneValid),
    TIPO_IMPORT(ParamValidator::isTipoImportValid),
    TIPO_IMPRESA(ParamValidator::isTipoImpresaValid),
    TIPO_INDIRIZZO(ParamValidator::isAddressTypeValid),
    TIPO_RICHIESTA(ParamValidator::isTipoRichiestaValid),
    TITOLO_GARA(ParamValidator::isTitoloGaraValid),
    TITOLO_TECNICO(ParamValidator::isTechnicalTitleValid),
    TOKEN(ParamValidator::isTokenValid),
    ULTERIORI_DICHIARAZIONI(ParamValidator::isUnlimitedTextValid),
    UNLIMITED_TEXT(ParamValidator::isUnlimitedTextValid),
    URL(ParamValidator::isUrlValid),
    USERNAME(ParamValidator::isUsernameValid),
    WHITE_LIST_ANTI_MAFIA(ParamValidator::isDigit),
    CLASSIFICAZIONI(ParamValidator::isClasseValid),
    UUID(ParamValidator::isUUIDDValid),
    ENTITA(ParamValidator::isEntitaValid),
    OGGETTO(ParamValidator::isEmptyOrLowerOrEqualTo50),
    OGGETTO_COMUNICAZIONE(ParamValidator::isEmptyOrLowerOrEqualTo300),
    OGGETTO_BANDI(ParamValidator::isEmptyOrLowerOrEqualTo2000),
    PASS_OE(ParamValidator::isPassOEValid),
    SERIAL_NUMBER(ParamValidator::isSerialNumberValid),
    MARCA_PRODOTTO(ParamValidator::isEmptyOrLowerOrEqualTo60),
    CODICE_PRODOTTO(ParamValidator::isCodiceProdottoValid),
    NOME_COMMERCIALE(ParamValidator::isNomeCommercialeValid),
    DESCRIZIONE_AGGIUNTIVA(ParamValidator::isEmptyOrLowerOrEqualTo50),
    DIMENSIONI(ParamValidator::isEmptyOrLowerOrEqualTo60),
    QUANTITA(ParamValidator::isQuantitaValid),
    CONTENT_TYPE(ParamValidator::isContentType),
    FIRMATARIO(ParamValidator::isFirmatario),
    ALFANUMERICO(ParamValidator::isAlphaNumeric),
    INTERO(ParamValidator::isInteger),
    ERRORE(ParamValidator::isAlphaNumeric),
    MOTIVAZIONE(ParamValidator::isEmptyOrLowerOrEqualTo2000),
    DENOMINAZIONE_RTI(ParamValidator::isAlphaNumeric),
    SHA(ParamValidator::isAlphaNumeric),
    DESCRIZIONE(ParamValidator::isUnlimitedTextValid),
    TIPO_AVVISO(ParamValidator::isTipoAvvisoValid),
    TIPO_AVVISO_GENERALI(ParamValidator::isTipoAvvisoGeneraliValid),
    TIPO_PROCEDURA(ParamValidator::isTipoProceduraValid),
    PASSWORD(ParamValidator::isPasswordValid),
    TIPO_SOCIETA_COOPERATIVA(ParamValidator::isTipoSocietaCooperativaValid),
    CODICE_FISCALE_O_IDENTIFICATIVO(ParamValidator::isCodFiscOIdentificativo),
    CRITERI_DI_ORDINAMENTO(ParamValidator::isValidOrderCriteria),
    NUMERO_CONTO(ParamValidator::isValidNumeroConto),
    CODICE_CNEL(ParamValidator::isCNELValid),
    ACTION_PATH(ParamValidator::isActionPathValid),
    /**
     * Represents an orderable identifier.<br/>
     * <br/>
     * An orderable identifier is a value that can be used for ordering or sorting.<br/>
     * This identifier can be used to determine the column that we need to sort.<br/>
     * <br/>
     * This variable is defined using a validation function that determines
     * whether a given value is a valid orderable identifier.<br/>
     *
     * @see ParamValidator#isOrderableIdentifierValid(String)
     */
    ORDERABLE_IDENTIFIER(ParamValidator::isOrderableIdentifierValid),
    NONE(null);
	
	/**
	 * validatore associato all'enum (Function<"function param type", "return type">)
	 */
    private final Function<String, ParamValidationResult> validator;

    /**
     * contruttore di enum
     */
    EParamValidation(Function<String, ParamValidationResult> validator) {
        this.validator = validator;
    }

    /**
     *
     * @param toValidate la stringa da validare
     * @return Porzione della strina in input non valida.
     */
    public ParamValidationResult validate(Object toValidate) {
        return validator.apply(toValidate != null ? toValidate.toString() : null);
    }

}
