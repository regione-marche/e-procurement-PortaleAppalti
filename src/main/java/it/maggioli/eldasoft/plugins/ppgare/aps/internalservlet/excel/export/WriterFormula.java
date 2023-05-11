package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export;

import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.utility.UtilityExcel;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class WriterFormula extends BaseExcelWriter {

    public WriterFormula(Object value, String style) {
        super(value, style);
    }

    @Override
    public void write(HSSFSheet sheet, int numColonna, int numRiga, DizionarioStili stili) {
        UtilityExcel.scriviFormulaCella(sheet, numColonna, numRiga, value.toString(), stili.getStile(style));
    }

}
