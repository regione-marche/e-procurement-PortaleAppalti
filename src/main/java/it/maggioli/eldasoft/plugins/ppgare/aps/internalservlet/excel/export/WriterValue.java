package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export;

import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.utility.UtilityExcel;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class WriterValue extends BaseExcelWriter {


    public WriterValue(Object value, String style) {
        super(value, style);
    }

    @Override
    public void write(HSSFSheet sheet, int numColonna, int numRiga, DizionarioStili stili) {
        UtilityExcel.scriviCella(sheet, numColonna, numRiga, value, stili.getStile(style));
    }

}
