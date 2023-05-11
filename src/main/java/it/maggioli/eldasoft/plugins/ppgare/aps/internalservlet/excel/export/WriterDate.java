package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export;

import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityExcel;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.Date;

public class WriterDate extends BaseExcelWriter {

    private short formato;

    public WriterDate(Object value, String style, short formato) {
        super(value, style);
        this.formato = formato;
    }

    @Override
    public void write(HSSFSheet sheet, int numColonna, int numRiga, DizionarioStili stili) {
        Object toWrite = UtilityDate.convertiData((Date) value, formato);
        UtilityExcel.scriviCella(sheet, numColonna, numRiga, toWrite, stili.getStile(style));
    }

}
