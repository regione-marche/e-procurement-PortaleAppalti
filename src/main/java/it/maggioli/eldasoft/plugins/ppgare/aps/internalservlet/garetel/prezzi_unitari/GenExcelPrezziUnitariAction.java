package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export.GenericExportExcel;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export.IWriteExcel;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export.WriterDate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export.WriterFormula;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export.WriterValue;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Consente la generazione dei pdf dei prezzi unitari
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class GenExcelPrezziUnitariAction extends GenericExportExcel {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -6663223791079272337L;

	private static final Logger log = LoggerFactory.getLogger(GenExcelPrezziUnitariAction.class);

    public static Integer RIGA_INIZIALE = 1;

    public static final int PREZZI_UNITARI_SEMPLICE = 1;
    public static final int PREZZI_UNITARI_RIBASSO = 2;
    public static final int PREZZI_UNITARI_RICERCA_MERCATO = 3;

    private int exportCase = 0;
    private String templateName = null;
    private Collection<String> tipologia = null;

    @Override
    public void init() {
        log.trace("START - Init");

        exportCase = getExportCase();
        templateName = getTemplateName();

        log.trace("END - Init");
    }

    @Override
    protected List getDataToPutOnTheExcel() {
        WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
        return PrezziUnitariFields.buildFromBustaEconomicaSession(helper);
    }

    @Override
    protected List<IWriteExcel> convertDTOToValueAndStyle(int numRow, Object row) {
        List<IWriteExcel> toReturn = null;


        PrezziUnitariFields base = (PrezziUnitariFields) row;

        switch (exportCase) {
            case PREZZI_UNITARI_SEMPLICE:
                toReturn = createColumnsPrezziUnitariSemplice(numRow, base);
                break;
            case PREZZI_UNITARI_RIBASSO:
                toReturn = createColumnsPrezziUnitariRibasso(numRow, base);
                break;
            case PREZZI_UNITARI_RICERCA_MERCATO:
                toReturn = createColumnsPrezziUnitariRicercaMercato(numRow, base);
                break;
            default:
                toReturn = createColumnsPrezziUnitariSemplice(numRow, base);
                break;
        }

        return toReturn;
    }

    private String getTemplateName() {
        String toReturn = null;

        switch(exportCase) {
            case PREZZI_UNITARI_SEMPLICE:
                toReturn = PortGareSystemConstants.MODELLO_EXCEL_VARIAZIONE_PREZZI_UNITARI_SEMPLICE;
                break;
            case PREZZI_UNITARI_RIBASSO:
                toReturn = PortGareSystemConstants.MODELLO_EXCEL_VARIAZIONE_PREZZI_UNITARI_RIBASSO;
                break;
            case PREZZI_UNITARI_RICERCA_MERCATO:
                toReturn = PortGareSystemConstants.MODELLO_EXCEL_VARIAZIONE_PREZZI_UNITARI_RICERCA_MERCATO;
                break;
            default:
                toReturn = PortGareSystemConstants.MODELLO_EXCEL_VARIAZIONE_PREZZI_UNITARI_SEMPLICE;
                break;
        }

        log.debug("Using template name: {}", toReturn);

        return toReturn;
    }

    @Override
    protected void addValidation(HSSFWorkbook wb, int numberOfRecords) {
        HSSFSheet mainSheet = wb.getSheet(getPageName());

        log.debug("START - Adding validation row {}", numberOfRecords);

        if (exportCase == PREZZI_UNITARI_RICERCA_MERCATO) {

            addValidationEqualIN(
                    mainSheet
                    , getStartingRowIndex()
                    , getStartingRowIndex() + numberOfRecords
                    , 0
                    , 0
                    , new String[] { "si", "no" }
                    , "Il campo deve assumere i valori si o no"
            );
            if (CollectionUtils.isNotEmpty(tipologia)) {
                String[] loweredTypes = tipologia.stream().map(String::toLowerCase).toArray(String[]::new);
                addValidationEqualIN(
                        mainSheet
                        , getStartingRowIndex()
                        , getStartingRowIndex() + numberOfRecords
                        , 5
                        , 5
                        , loweredTypes
                        , String.format("Il campo deve assumere i valori [%s]", StringUtils.join(loweredTypes, ", "))
                );
            }
        } else
            log.trace("Only market search needs validations");

        log.debug("END - Validating row {}", numberOfRecords);
    }

    public static int getExportCase() {
        int toReturn = PREZZI_UNITARI_SEMPLICE;

        WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
        Integer tipoRibasso = helper.getGara().getTipoRibasso();
        if (StringUtils.equals(helper.getGara().getIterGara(), "8"))
            toReturn = PREZZI_UNITARI_RICERCA_MERCATO;
        else if (tipoRibasso == 2)   //
            toReturn = PREZZI_UNITARI_SEMPLICE;
        else if (tipoRibasso == 3)
            toReturn = PREZZI_UNITARI_RIBASSO;

        log.debug("Found export case: {}", toReturn);

        return toReturn;
    }

    private List<IWriteExcel> createColumnsPrezziUnitariSemplice(int numRow, PrezziUnitariFields base) {
        List<IWriteExcel> toWrite = new ArrayList<>();

        toWrite.add(new WriterValue(base.getVoce(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getDescrizione(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getDescrizioneEstesa(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getUnitaDiMisura(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getQuantita(), INTEGER_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getPrezzoBase(), DECIMAL_5_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getPrezzo(), DECIMAL_5_UNLOCKED_REQ_STYLE));

        toWrite.add(new WriterFormula("E" + numRow + "*G" + numRow, DECIMAL_5_LOCKED_STYLE));   //Import
        //        String col5 = CellReference.convertNumToColString(5);

        return toWrite;
    }

    private List<IWriteExcel> createColumnsPrezziUnitariRibasso(int numRow, PrezziUnitariFields base) {
        List<IWriteExcel> toWrite = new ArrayList<>();

        toWrite.add(new WriterValue(base.getVoce(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getDescrizione(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getDescrizioneEstesa(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getUnitaDiMisura(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getQuantita(), INTEGER_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getPrezzoBase(), DECIMAL_5_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getPeso(), DECIMAL_5_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getRibasso(), DECIMAL_5_UNLOCKED_REQ_STYLE));

        String ribasso = "H" + numRow;
        String peso = "G" + numRow;
        String prezzoBase = "F" + numRow;
        String prezzo = "J" + numRow;
        String quantita = "E" + numRow;

        String condition = String.format("IF(%s=0,0,", ribasso);

        toWrite.add(new WriterFormula(condition + peso + " / 100 * " + ribasso + ")", DECIMAL_5_LOCKED_STYLE));    //Ribasso pesato
        toWrite.add(new WriterFormula(condition + prezzoBase + " * (1 - " + ribasso + " / 100))", DECIMAL_5_LOCKED_STYLE)); //Prezzo
        toWrite.add(new WriterFormula(condition + prezzo + " * " + quantita + ")", DECIMAL_5_LOCKED_STYLE)); //Importo

        return toWrite;
    }
    private List<IWriteExcel> createColumnsPrezziUnitariRicercaMercato(int numRow, PrezziUnitariFields base) {
        List<IWriteExcel> toWrite = new ArrayList<>();

        if (Boolean.TRUE.equals(base.getOfferta()))
            toWrite.add(new WriterValue("si", STRING_UNLOCKED_REQ_STYLE));
        else
            toWrite.add(new WriterValue("no", STRING_UNLOCKED_REQ_STYLE));

        toWrite.add(new WriterValue(base.getVoce(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getDescrizione(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getDescrizioneEstesa(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterDate(base.getDataConsegnaRichiesta(), DATE_LOCKED_STYLE, UtilityDate.FORMATO_GG_MM_AAAA));
        toWrite.add(new WriterValue(StringUtils.lowerCase(base.getTipoTextFromValue()), STRING_UNLOCKED_REQ_STYLE));
        toWrite.add(new WriterValue(base.getNote(), STRING_UNLOCKED_STYLE));

        toWrite.add(new WriterValue(base.getUnitaDiMisura(), STRING_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getQuantita(), INTEGER_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getPrezzoBase(), DECIMAL_5_LOCKED_STYLE));
        toWrite.add(new WriterValue(base.getPrezzo(), DECIMAL_5_UNLOCKED_REQ_STYLE));

        String prezzo = "K" + numRow;
        String condition = String.format("IF(%s=0,0,", prezzo);
        toWrite.add(new WriterFormula(condition + prezzo + " * I" + numRow + ")", DECIMAL_5_LOCKED_STYLE)); //Importo

        tipologia = base.getTipologie();

        return toWrite;
    }

    @Override
    protected List<List<IWriteExcel>> getLegendValueAndStyle() {
        List<List<IWriteExcel>> toWrite = new ArrayList<>();

        toWrite.add(Collections.singletonList(new WriterValue("LEGENDA", STRING_LOCKED_STYLE)));
        toWrite.add(Collections.singletonList(new WriterValue("cella editabile", STRING_UNLOCKED_STYLE)));
        toWrite.add(Collections.singletonList(new WriterValue("cella editabile obbligatoria", STRING_UNLOCKED_REQ_STYLE)));

        return toWrite;
    }

    @Override
    protected void createCustomStyles(HSSFWorkbook wb, DizionarioStili stili) {
        defaultStyles(wb, stili);
    }

    @Override
    protected String getModelloExcelPath() {
        return templateName;
    }

    @Override
    protected int getStartingRowIndex() {
        return RIGA_INIZIALE;
    }

    @Override
    protected String getPageName() {
        return PortGareSystemConstants.SHEET_PREZZI_UNITARI;
    }

    @Override
    public String getBaseFileName() {
        return PortGareSystemConstants.NOME_MODELLO_EXCEL_VARIAZIONE_PREZZI_UNITARI;
    }

    @Override
    public String getUrlErrori() {
        return this.getUrlPage() +
                "?actionPath=/ExtStr2/do/FrontEnd/GareTel/variazionePrezziUnitari.action" +
                "&currentFrame=" + this.getCurrentFrame();
    }

}
