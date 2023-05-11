package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export;

import it.eldasoft.utils.excel.DizionarioStili;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public abstract class BaseExcelWriter implements IWriteExcel {

    protected Object value;
    protected String style;

    public BaseExcelWriter(Object value, String style) {
        this.value = value;
        this.style = style;
    }

    @Override
    public abstract void write(HSSFSheet sheet, int numColonna, int numRiga, DizionarioStili stili);

}
