package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.eldasoft.utils.excel.ExcelResultBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.GenericImportExcel;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari.PrezziUnitariFields.PrezziUnitariFieldsBuilder;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.eldasoft.utils.utility.UtilityExcel.*;
import static it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari.GenExcelPrezziUnitariAction.RIGA_INIZIALE;


/**
 * Consente la generazione del PDF di riepilogo delle modifiche ai prodotti.
 *
 */
public class ImportPrezziUnitariAction extends GenericImportExcel {

    private static final Logger log = LoggerFactory.getLogger(ImportPrezziUnitariAction.class);


    private int importCase = 0;
    private final ImportPrezziUnitariResultBean result = new ImportPrezziUnitariResultBean();

    @Override
    public void init() {
        log.trace("Start - init");

        importCase = GenExcelPrezziUnitariAction.getExportCase();
        log.debug("The current unitprice on session is of the type {}", importCase);

        log.trace("END - init");
    }

    @Override
    public boolean isCurrentSessionExpired() {
        return GestioneBuste.getBustaEconomicaFromSession() == null || GestioneBuste.getBustaEconomicaFromSession().getHelper() == null;
    }

    @Override
    protected Object importRow(Object sheet, int index) {
        Object toReturn = null;

        log.trace("START - importRow");
        if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_SEMPLICE)
            toReturn = importSemplice(sheet, index);
        else if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RIBASSO)
            toReturn = importRibasso(sheet, index);
        else if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RICERCA_MERCATO)
            toReturn = importRicercaMercato(sheet, index);
        log.trace("END - importRow");

        return toReturn;
    }

    @Override
    protected boolean isValidRow(Object importedRow) {
        boolean toReturn = false;

        log.trace("START - isValidRow");

        PrezziUnitariFields fields = (PrezziUnitariFields) importedRow;
        if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_SEMPLICE)
            toReturn = validazioneSemplice(fields);
        else if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RIBASSO)
            toReturn = validazioneRibasso(fields);
        else if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RICERCA_MERCATO)
            toReturn = validazioneRicercaMercato(fields);

        log.trace("END - isValidRow");

        return toReturn;
    }

    @Override
    protected void saveRows(List<?> importedRow) {
        List<PrezziUnitariFields> fields = (List<PrezziUnitariFields>) importedRow;
        PrezziUnitariFields.save(fields, importCase);
    }

    @Override
    protected Map<String, String> getTextAndElementAndHtmlID() {
        Map<String, String> toReturn = new HashMap<>();

        toReturn.put(getText("Errors.fileNotSet"), "allegato");
        toReturn.put(getText("Errors.emptyFile"), "allegato");
        toReturn.put(getText("Errors.overflowFileSize"), "allegato");
        toReturn.put(getText("Errors.overflowTotalFileSize"), "allegato");

        return toReturn;
    }

    @Override
    protected String getPageName() {
        return PortGareSystemConstants.SHEET_PREZZI_UNITARI;
    }

    @Override
    public ExcelResultBean getResult() {
        return result;
    }
    @Override
    protected int getStartingRowIndex() {
        return RIGA_INIZIALE;
    }
    @Override
    protected void doOnSuccesfullyCompleted() {
        try {
            log.debug("START - Sending coumincation for the unit price with the code: " + getCodice());

            BustaEconomica busta = GestioneBuste.getBustaEconomicaFromSession();
            busta.getHelper().deleteDocumentoOffertaEconomica(this, eventManager);
            busta.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

            log.debug("END - Sending coumincation for the unit price with the code: " + getCodice());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Object importRicercaMercato(Object sheet, int rowIndex) {
        int columnIndex = 0;  //Aggiunto per rendere più agevole l'aggiunta/rimozione/spostamento delle colonne
        return new PrezziUnitariFieldsBuilder()
                .setOfferta(StringUtils.equalsIgnoreCase("si", readString(sheet, rowIndex, columnIndex++, "Offerta?", rowErrors)))
                .setVoce(readString(sheet, rowIndex, columnIndex++, "Voce", rowErrors))
                .setDescrizione(readString(sheet, rowIndex, columnIndex++, "Descrizione", rowErrors))
                .setDescrizioneEstesa(readString(sheet, rowIndex, columnIndex++, "Descrizione estesa", rowErrors))
                .setDataConsegnaRichiesta(leggiData(sheet, rowIndex, columnIndex++, "Data consegna richiesta", rowErrors))
                .setTipoFromText(readString(sheet, rowIndex, columnIndex++, "Tipo", rowErrors))
                .setNote(readString(sheet, rowIndex, columnIndex++, "Note", rowErrors))
                .setUnitaDiMisura(readString(sheet, rowIndex, columnIndex++, "Un. Di misura", rowErrors))
                .setQuantita(readDouble(sheet, rowIndex, columnIndex++, "Quantita", 5, rowErrors))
                .setPrezzoBase(readDouble(sheet, rowIndex, columnIndex++, "Prezzo base", 5, rowErrors))
                .setPrezzo(readDouble(sheet, rowIndex, columnIndex++, "Prezzo", 5, rowErrors))
                .setImportoFromFormula(importCase)
            .build();
    }

    private Object importRibasso(Object sheet, int rowIndex) {
        int columnIndex = 0;  //Aggiunto per rendere più agevole l'aggiunta/rimozione/spostamento delle colonne
        return new PrezziUnitariFieldsBuilder()
                .setVoce(readString(sheet, rowIndex, columnIndex++, "Voce", rowErrors))
                .setDescrizione(readString(sheet, rowIndex, columnIndex++, "Descrizione", rowErrors))
                .setDescrizioneEstesa(readString(sheet, rowIndex, columnIndex++, "Descrizione estesa", rowErrors))
                .setUnitaDiMisura(readString(sheet, rowIndex, columnIndex++, "Un. Di misura", rowErrors))
                .setQuantita(readDouble(sheet, rowIndex, columnIndex++, "Quantita", 5, rowErrors))
                .setPrezzoBase(readDouble(sheet, rowIndex, columnIndex++, "Prezzo base", 5, rowErrors))
                .setPeso(readDouble(sheet, rowIndex, columnIndex++, "Peso", 5, rowErrors))
                .setDecimaliRibasso(GestioneBuste.getBustaEconomicaFromSession().getHelper().getNumDecimaliRibasso())
                .setRibasso(readDouble(sheet, rowIndex, columnIndex++, "Ribasso", 5, rowErrors))
                .setRibassoPesatoFromFormula(importCase)
                .setPrezzoFromFormula(importCase)
                .setImportoFromFormula(importCase)
            .build();
    }

    private Object importSemplice(Object sheet, int rowIndex) {
        int columnIndex = 0;  //Aggiunto per rendere più agevole l'aggiunta/rimozione/spostamento delle colonne
        return new PrezziUnitariFieldsBuilder()
                .setVoce(readString(sheet, rowIndex, columnIndex++, "Voce", rowErrors))
                .setDescrizione(readString(sheet, rowIndex, columnIndex++, "Descrizione", rowErrors))
                .setDescrizioneEstesa(readString(sheet, rowIndex, columnIndex++, "Descrizione estesa", rowErrors))
                .setUnitaDiMisura(readString(sheet, rowIndex, columnIndex++, "Un. Di misura", rowErrors))
                .setQuantita(readDouble(sheet, rowIndex, columnIndex++, "Quantita", 5, rowErrors))
                .setPrezzoBase(readDouble(sheet, rowIndex, columnIndex++, "Prezzo base", 5, rowErrors))
                .setPrezzo(readDouble(sheet, rowIndex, columnIndex++, "Prezzo", 5, rowErrors))
                .setImportoFromFormula(importCase)
            .build();
    }

    private boolean validazioneRicercaMercato(PrezziUnitariFields fields) {
        int previousErrorsNumber = rowErrors.size();

        boolean hasReadErrors = columnNameWithError.contains("Prezzo") || columnNameWithError.contains("Tipo");

        if (fields.getOfferta()) {  //Se fields == false, vuol dire non è stata fatta alcuna offerta per quel record.
            if (!columnNameWithError.contains("Prezzo")) {
                if (fields.getPrezzo() == null || fields.getPrezzo() == 0.0d)
                    rowErrors.add(getText("Errors.sheet.requiredFiled", new String[]{"Prezzo"}));
                else if (fields.getPrezzo() < 0.0d)
                    rowErrors.add(getText("Errors.mustBeAbove", new String[]{"Prezzo", "0"}));
                else if (fields.getPrezzoBase() != null && fields.getPrezzo() > fields.getPrezzoBase())
                    rowErrors.add(getText("Errors.mustBeUnder", new String[]{"Prezzo", fields.getPrezzoBase().toString()}));
            }
            if (!columnNameWithError.contains("Tipo") && StringUtils.isEmpty(fields.getTipo()))
                rowErrors.add(getText("Errors.sheet.requiredFiled", new String[] { "Tipo" }));
        }

        return !hasReadErrors && previousErrorsNumber == rowErrors.size();
    }

    private boolean validazioneRibasso(PrezziUnitariFields fields) {
        int previousErrorsNumber = rowErrors.size();

        boolean hasReadErrors = columnNameWithError.contains("Ribasso");

        if (!hasReadErrors) {
            if (fields.getRibasso() == null)
                rowErrors.add(getText("Errors.sheet.requiredFiled", new String[]{"Ribasso"}));
            else if (fields.getRibasso() < 0.0d || fields.getRibasso() > 100)
                rowErrors.add(getText("Errors.mustBeBetween", new String[]{"Ribasso", "0", "100"}));
        }

        return !hasReadErrors && previousErrorsNumber == rowErrors.size();
    }

    private boolean validazioneSemplice(PrezziUnitariFields fields) {
        int previousErrorsNumber = rowErrors.size();

        boolean hasReadErrors = columnNameWithError.contains("Prezzo");

        if (!hasReadErrors) {
            if (fields.getPrezzo() == null || fields.getPrezzo() == 0.0d)
                rowErrors.add(getText("Errors.sheet.requiredFiled", new String[]{"Prezzo"}));
            else if (fields.getPrezzo() < 0.0d)
                rowErrors.add(getText("Errors.mustBeAbove", new String[]{"Prezzo", "0"}));
            else if (fields.getPrezzoBase() != null && fields.getPrezzo() > fields.getPrezzoBase())
                rowErrors.add(getText("Errors.mustBeUnder", new String[]{"Prezzo", fields.getPrezzoBase().toString()}));
        }


        return !hasReadErrors && previousErrorsNumber == rowErrors.size();
    }

    public String cancel() {
        return "cancel";
    }

}
