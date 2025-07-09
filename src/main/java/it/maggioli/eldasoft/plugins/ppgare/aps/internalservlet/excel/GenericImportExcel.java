package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.excel.ExcelResultBean;
import it.eldasoft.utils.utility.UtilityExcel;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.UploadValidator;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.eldasoft.utils.utility.UtilityExcel.leggiNumero;
import static it.eldasoft.utils.utility.UtilityExcel.leggiStringa;

public abstract class GenericImportExcel extends EncodedDataAction implements SessionAware {

    private static final Logger log = LoggerFactory.getLogger(GenericImportExcel.class);

    private IAppParamManager appParamManager;
    protected IEventManager eventManager;

    @Validate(EParamValidation.CODICE)
    private String codice;
    private File allegato;
    @Validate(EParamValidation.CONTENT_TYPE)
    private String allegatoContentType;
    @Validate(EParamValidation.FILE_NAME)
    private String allegatoFileName;

    protected Map<String, Object> session;

    private int rowIndex = getStartingRowIndex();  //Indice per lambda
    private Object row = null;

    protected List<Object> rows;
    protected List<String> rowErrors = new ArrayList<>();
    protected List<String> columnNameWithError = new ArrayList<>();

    public String load() {
        try {
            if (!isCurrentSessionExpired()) {
                int allegatoSize = FileUploadUtilities.getFileSize(allegato);

				// valida l'upload del documento...
    			getUploadValidator()
						.setDocumento(allegato)
						.setDocumentoFileName(allegatoFileName)
						.setDocumentoFormato(PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL)
						.setOnlyP7m(false)
						.setEventoDestinazione(codice)
						.setEventoMessaggio("Import variazione prezzi scadenze: file=" + allegatoFileName 
								 			+ ", dimensione=" + allegatoSize + "KB");
				
				if ( getUploadValidator().validate() ) {
					init();
                    try {
                        Object sheet = UtilityExcel.leggiFoglioExcelPerImportazione(
                                allegatoFileName,
                                getPageName(),
                                allegato,
                                CataloghiConstants.VERSIONE_MODELLO_EXCEL_IMPORT_PRODOTTI);
                        if (sheet == null)
                            throw new ExcelException(String.format("Cannot find the sheet %s in the file %s", getPageName(), allegatoFileName));
                        log.debug("Imported sheet {} from the attachment {}", getPageName(), allegatoFileName);
                        importPage(sheet);
//                        if (getResult().getSuccessRows() > 0)
//                            ProdottiAction.saveProdotti(prodotti);
                    } catch (ExcelException e) {
                        error(UtilityExcel.errore(e), INPUT, e);
                    } catch (Throwable e) {
                        error(UtilityExcel.errore(ExcelException.Errore.IMPORT_UNEXPECTED_ERROR.name()), INPUT, e);
                    }
                } else {
                    actionErrorToFieldError();
                    setTarget(INPUT);
                }
				
                eventManager.insertEvent(getUploadValidator().getEvento());
            } else {
                // la sessione e' scaduta, occorre riconnettersi
                error(getText("Errors.sessionExpired"), CommonSystemConstants.PORTAL_ERROR);
            }

        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "importData");
            ExceptionUtils.manageExceptionError(t, this);
        }

        return getTarget();
    }

    protected void error(String sessionMessage, String target) {
        error(sessionMessage, target,null);
    }
    protected void error(String sessionMessage, String target, Throwable error) {
        if (error != null)
            ApsSystemUtils.logThrowable(error, this, "load");
        addActionError(sessionMessage);
        setTarget(target);
    }

    private void importPage(Object sheet) throws ExcelException {   //Sistemare valore letto dalle formule.
        int numberOfRows = UtilityExcel.ricavaNumeroRigheFoglio(sheet);
        /*
          Semplifico il loop incapsulando il richiamo del metodo in un interfaccia funzionale e la
          gestione dell'eccezione in un metodo a parte.
         */
        ThrowingSupplier<Object> readRow = () -> UtilityExcel.leggiRiga(sheet, rowIndex);
        ThrowingSupplier<Boolean> isEmptyRow = () -> UtilityExcel.rigaVuota(row);

        rows = new ArrayList<>(numberOfRows);  //Non ci possono essere più di <numberOfRows> valori da aggiungere all'array
        
        /*
          Con questo doWithoutThrow richiamo il metodo leggiRiga/rigaVuota senza dover gestire il try catch
          (lo gestisco nel doWithoutThrow)
         */
        ExcelResultBean result = getResult();
        while (rowIndex < numberOfRows
                && (((row = doWithoutThrow(rowErrors, readRow)) != null || rowErrors.size() > 0)
                && Boolean.FALSE.equals(doWithoutThrow(rowErrors, isEmptyRow))
            )
        ) {   //Condizione da sistemare

            result.setInputRows(result.getInputRows() + 1);
            if (row != null) {
                Object importedRow = null;
                importedRow = tryImportRow(sheet);
                if (importedRow != null && isValidRow(importedRow)) {
                    rows.add(importedRow);
                    result.setSuccessRows(result.getSuccessRows() + 1);
                    log.debug("Row number {} is importable", rowIndex);
                }
            }
            if (CollectionUtils.isNotEmpty(rowErrors))
                result.getErrorRows().put(rowIndex, rowErrors);

            rowErrors = new ArrayList<>(); //Messo alla fine così non rischio di perdere gli errori su leggiriga e rigavuota
            columnNameWithError = new ArrayList<>();

            rowIndex++;    //Utilizzato troppe volte, meglio specificarlo sul fondo del loop
        }
        if (result.getSuccessRows() + result.getSkippedRows().size() == getResult().getInputRows()) {
            saveRows(rows);
            doOnSuccesfullyCompleted();
            log.debug("Rows successfully imported");
        }
            //Salva...
    }

    protected Double readDouble(Object sheet, int rowIndex, int columnIndex, String fieldName, int numDecimals, List<String> errors) {
        int initialSize = errors.size();
        Number value = leggiNumero(sheet, rowIndex, columnIndex, fieldName, numDecimals, errors);
        if (initialSize != errors.size()) columnNameWithError.add(fieldName);
        return value != null ? value.doubleValue() : null;
    }
    protected String readString(Object sheet, int rowIndex, int columnIndex, String fieldName, List<String> errors) {
        int initialSize = errors.size();
        String value = leggiStringa(sheet, rowIndex, columnIndex, fieldName, errors);
        if (initialSize != errors.size()) columnNameWithError.add(fieldName);
        return value;
    }

    private Object tryImportRow(Object sheet) {
        Object importedRow = null;

        try {
            importedRow = importRow(sheet, rowIndex);
        } catch (Exception e) {
            log.error("C'è stato un errore bloccante durante la lettura della riga {}" +
                    ", probabilmente è stato utilizzato un excel non originale.", rowIndex);
            rowErrors.add("Errore bloccante durante la lettura della riga");
        }

        return importedRow;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws ExcelException;
    }

    /**
     * In caso di errore durante il richiamo della funzione,
     * Aggiungo agli errori
     * @param erroriRiga
     * @param throwingSupplier
     * @return
     * @param <T>
     */
    private <T> T doWithoutThrow(
            List<String> erroriRiga
            , ThrowingSupplier<T> throwingSupplier) {
        T toReturn = null;

        try {
            toReturn = throwingSupplier.get();
        } catch (ExcelException ex) {
            erroriRiga.add(UtilityExcel.errore(ex));
        }

        return toReturn;
    }

    /**
     * Viene richiamato quando tutte le righe sono importate e sono state importate tramite il metodo saveRow
     */
    protected abstract void doOnSuccesfullyCompleted();

    /**
     * Ritorna l'excelResultBean che contiene la lista di errori, righe completate, righe elaborate...
     * @return
     */
    public abstract ExcelResultBean getResult();

    /**
     * Indica la riga da dove comincia il contenuto dell'excel
     * @return
     */
    protected abstract int getStartingRowIndex();

    /**
     * Nome della pagina da elaborare
     * @return
     */
    protected abstract String getPageName();

    /**
     * Controlla se la sessione è scaduta
     * @return
     */
    protected abstract boolean isCurrentSessionExpired();

    /**
     * Se tutte le righe sono importabili, per ogni riga viene richiamato questo metodo, contenete la riga importata.
     * @param importedRow
     */
    protected abstract void saveRows(List<?> importedRow);

    /**
     * Importa la riga da sheet excel a Oggetto java
     * @param sheet
     * @param index
     * @return
     */
    protected abstract Object importRow(Object sheet, int index);

    /**
     * Controlla se la riga importata è valida
     * @param data
     * @return
     */
    protected abstract boolean isValidRow(Object data);

    /**
     * Inizializzazione in caso si debbano calcolare alcuni parametri prima che inizi l'import.
     */
    protected abstract void init();

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setAllegato(File allegato) {
        this.allegato = allegato;
    }
    
    public void setAllegatoContentType(String allegatoContentType) {
        this.allegatoContentType = allegatoContentType;
    }
    public void setAllegatoFileName(String allegatoFileName) {
        this.allegatoFileName = allegatoFileName;
    }

    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }
    public void setEventManager(IEventManager eventManager) {
        this.eventManager = eventManager;
    }

    public String getCodice() {
        return codice;
    }
    public void setCodice(String codice) {
        this.codice = codice;
    }

}
