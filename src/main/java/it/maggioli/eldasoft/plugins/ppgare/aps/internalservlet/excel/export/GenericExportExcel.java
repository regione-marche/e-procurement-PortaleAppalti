package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.TokenInterceptor;
import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityExcel;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Classe generica per generare excel sulla base dei dati specificati.
 * Supporta solo il formato .xls
 */
public abstract class GenericExportExcel extends EncodedDataAction implements SessionAware, IDownloadAction {

    private static final Logger log = LoggerFactory.getLogger(GenericExportExcel.class);

    private String currentFrame;
    private String urlPage;
    private String filename = null;
    private InputStream inputStream;
    private Map<String, Object> session;

    protected static final String STRING_UNLOCKED_STYLE = "STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED";
    protected static final String STRING_UNLOCKED_REQ_STYLE = "STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED";

    protected static final String STRING_LOCKED_STYLE = "STRINGA_LEFT_CALIBRI_NORMAL_LOCKED";
    protected static final String DATE_LOCKED_STYLE = "DATA_LEFT_CALIBRI_NORMAL_LOCKED";

    protected static final String DECIMAL_5_UNLOCKED_REQ_STYLE = "DECIMALE_5_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED";
    protected static final String DECIMAL_5_LOCKED_STYLE = "DECIMALE_5_CENTER_CALIBRI_NORMAL_LOCKED";
    protected static final String DECIMAL_2_UNLOCKED_STYLE = "DECIMALE_2_CENTER_CALIBRI_NORMAL_UNLOCKED";
    protected static final String INTEGER_LOCKED_STYLE = "INTERO_CENTER_CALIBRI_NORMAL_LOCKED";

    /**
     * Viene richiamato come prima cosa, sovrascrivelo in caso si voglia inizializzare delle variabili
     */
    public abstract void init();

    public String createExcel() {
        log.debug("START - Creating excel for the exportation");

        init();
        List<T> foundData = getDataToPutOnTheExcel();
        if (dataAreValid(foundData)) {
            log.trace("Found: {} record to put on the excel", foundData.size());
            try {
                ServletContext ctx = this.getRequest().getSession().getServletContext();

                if (ctx.getResource(getModelloExcelPath()) == null) {
                    session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.TEMPLATE_NOT_FOUND.name()));
                    this.setTarget(INPUT);
                } else {
                    try {
                        String percorsoModelliExcel = ctx.getRealPath(getModelloExcelPath());
                        HSSFWorkbook wb = UtilityExcel.leggiModelloExcel(percorsoModelliExcel);
                        populateExcel(wb, foundData);
                        setTarget(SUCCESS);
                    } catch (ExcelException e) {
                        error(ERROR_DOWNLOAD, getText(e.getCodiceErrore()), e);
                    } catch (Throwable e) {
                        error(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()), e);
                    }
                }
            } catch (Throwable e) {
                error(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()), e);
            }

            if(!SUCCESS.equals(this.getTarget()))
                TokenInterceptor.redirectToken();

        } else
            error(ERROR_DOWNLOAD, "Errors.sheet.noResultToExport");

        log.debug("END - Creating excel for the exportation");

        return getTarget();
    }

    protected void addValidationEqualIN(HSSFSheet mainSheet, int rowFrom, int rowTO, int columnFrom, int columnTO, String[] equalsIN, String errorMessage) {
        log.trace("START - Adding validation <from,to> row: <{},{}>; and <from,to> column>: " +
                "<{},{}>. The cells has to be equals to one of the following values [{}]"
                , rowFrom, rowTO, columnFrom, columnTO, StringUtils.join(equalsIN, ", "));

        HSSFDataValidationHelper validationHelper = new HSSFDataValidationHelper(mainSheet);
        DataValidationConstraint listConstraint = validationHelper.createExplicitListConstraint(equalsIN);
        CellRangeAddressList range =
                new CellRangeAddressList(rowFrom, rowTO, columnFrom, columnTO);

        DataValidation dataValidation = validationHelper.createValidation(listConstraint, range);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setEmptyCellAllowed(false);
        dataValidation.createErrorBox("Errore", errorMessage);
        dataValidation.setShowErrorBox(true);
        mainSheet.addValidationData(dataValidation);

        log.trace("END - Adding validation");
    }

    private void populateExcel(HSSFWorkbook wb, List<?> data) throws ExcelException {
        // si determina il nome del file di destinazione nell'area
        filename = getBaseFileName() + FileUploadUtilities.generateFileName() + ".xls";

        DizionarioStili stili = new DizionarioStili(wb);
        createCustomStyles(wb, stili);
        populatePage(wb, data, stili);
        addValidation(wb, data.size());
        protectExcel(wb);

        excelObjectToInputStream(wb);
    }

    private void populatePage(HSSFWorkbook wb, List<?> data, DizionarioStili stili) throws ExcelException {
        HSSFSheet sheet = wb.getSheetAt(0);

        int indiceRigaCorrente = getStartingRowIndex();
        indiceRigaCorrente = writeContent(data, stili, sheet, indiceRigaCorrente);
        writeLegend(sheet, indiceRigaCorrente + 3, stili);
    }

    private int writeContent(List<?> data, DizionarioStili stili, HSSFSheet sheet, int indiceRigaCorrente) throws ExcelException {
        for (Object dataRow : data)
            indiceRigaCorrente = elaborateRow(dataRow, indiceRigaCorrente, sheet, stili);

        return indiceRigaCorrente;
    }

    private void excelObjectToInputStream(HSSFWorkbook wb) throws ExcelException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            wb.write(os);
            inputStream = new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), e);
        }
    }

    private void protectExcel(HSSFWorkbook wb) throws ExcelException {
        log.trace("START - Protecting sheet");
        UtilityExcel.protectSheet(wb, getPageName());
        UtilityExcel.protectVersionSheet(wb);
        log.trace("END - Protecting sheet");
    }

    private int elaborateRow(Object prodotto, int indiceRigaCorrente, HSSFSheet sheet, DizionarioStili stili) throws ExcelException {
        //Se sto inserendo più di una riga copio quella precedente per avere
        //le formattazioni corrette
        if (indiceRigaCorrente > getStartingRowIndex())
            UtilityExcel.copiaRiga(sheet, indiceRigaCorrente - 1, indiceRigaCorrente, stili.getStili());
        try {
            log.debug("START - Converting object to excel row");
            List<IWriteExcel> columnsValueAndStyle = convertDTOToValueAndStyle(++indiceRigaCorrente, prodotto);
            fillRow(sheet, indiceRigaCorrente, columnsValueAndStyle, stili);
            log.debug("END - Converting object to excel row");
        } catch (Throwable aps) {
            throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), aps);
        }
        return indiceRigaCorrente;
    }

    private void writeLegend(HSSFSheet sheet, final int numRiga, DizionarioStili stili) {
        List<List<IWriteExcel>> legendValueAndStyle = getLegendValueAndStyle();
        if (CollectionUtils.isNotEmpty(legendValueAndStyle))
            IntStream.range(0, legendValueAndStyle.size())
                .forEach(numRow ->  {
                    List<IWriteExcel> row = legendValueAndStyle.get(numRow);
                    IntStream.range(0, row.size())
                        .forEach(numCol -> row.get(numCol).write(sheet, numCol + 1, numRiga + numRow, stili));
                });
    }

    private void fillRow(HSSFSheet sheet, int numRiga, List<IWriteExcel> columnsValueAndStyle, DizionarioStili stili) {
        if (CollectionUtils.isNotEmpty(columnsValueAndStyle))
            IntStream.range(0, columnsValueAndStyle.size())
                .forEach(numColonna -> columnsValueAndStyle.get(numColonna).write(sheet, numColonna+1, numRiga, stili));
    }

    protected void defaultStyles(HSSFWorkbook wb, DizionarioStili stili) {
        List<HSSFCellStyle> stiliRequired = new ArrayList<>();

        for (String styleKey : stili.getStili().keySet()) {
            if (styleKey.contains(DizionarioStili.CellaBloccata.UNLOCKED.name())) {
                HSSFCellStyle style = stili.getStile(styleKey);
                style.setFillForegroundColor(HSSFColor.YELLOW.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                style.setLocked(false);
                stili.aggiungiCustomStyle(style);
                stiliRequired.add(style);
            }
        }
        for (HSSFCellStyle stile : stiliRequired) {
            HSSFCellStyle newStileRequired = wb.createCellStyle();
            newStileRequired.cloneStyleFrom(stile);
            newStileRequired.setFillForegroundColor(HSSFColor.ORANGE.index);
            newStileRequired.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            newStileRequired.setUserStyleName(stile.getUserStyleName() + DizionarioStili.SEPARATORE_CHIAVE + "REQUIRED");
            stili.aggiungiCustomStyle(newStileRequired);
        }
        HSSFCellStyle stileNascosto = wb.createCellStyle();
        stileNascosto.cloneStyleFrom(stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_LOCKED"));
        stileNascosto.setHidden(true);
        stileNascosto.setUserStyleName("STRINGA_CENTER_CALIBRI_NORMAL_LOCKED_HIDDEN");
        HSSFFont fontNascosto = wb.createFont();
        fontNascosto.setFontName("CALIBRI_NORMAL_TRANSPARENT");
        fontNascosto.setColor(HSSFColor.WHITE.index);
        stili.aggiungiCustomFont("CALIBRI_NORMAL_TRANSPARENT", fontNascosto);
        stileNascosto.setFont(fontNascosto);
        stili.aggiungiCustomStyle(stileNascosto);
    }

    protected void error(String sessioneKey, String sessionMessage) {
        error(sessioneKey, sessionMessage, null);
    }
    protected void error(String sessioneKey, String sessionMessage, Throwable error) {
        if (error != null)
            ApsSystemUtils.logThrowable(error, this, "createExcel");
        session.put(sessioneKey, sessionMessage);
        this.setTarget(INPUT);
    }

    private boolean dataAreValid(List<T> data) {
        return CollectionUtils.isNotEmpty(data);
    }


    /**
     * Opzionale, si può anche fare un semplice return null
     * @return
     */
    protected abstract List<List<IWriteExcel>> getLegendValueAndStyle();

    /**
     * Convertire il dto in una lista di valori e stili ordinata in base a come la si vuole sull'excel
     * @param row
     * @return
     */
    protected abstract List<IWriteExcel> convertDTOToValueAndStyle(int numRow, Object row);

    /**
     * Creare stili curstom,richiamare defaultStyle() se non si vuole implementare
     * @param wb
     * @param stili
     */
    protected abstract void createCustomStyles(HSSFWorkbook wb, DizionarioStili stili);

    /**
     * Percorso al modello /WEB-INF/..../*.xls
     * @return
     */
    protected abstract String getModelloExcelPath();

    /**
     * Dati grezzi da inserire sull'excel (SearchResult{@literal <}T extends DTO{@literal >})
     * @return
     */
    protected abstract List<T> getDataToPutOnTheExcel();

    protected abstract void addValidation(HSSFWorkbook wb, int numberOfRecords);

    /**
     * Numero di righe da saltare
     * @return
     */
    protected abstract int getStartingRowIndex();
    protected abstract String getPageName();
    @Override
    public void setUrlPage(String urlPage) {
        this.urlPage = urlPage;
    }
    @Override
    public abstract String getUrlErrori();
    public abstract String getBaseFileName();

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void setCurrentFrame(String currentFrame) {
        this.currentFrame = currentFrame;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getUrlPage() {
        return urlPage;
    }
    public String getCurrentFrame() {
        return currentFrame;
    }


}
