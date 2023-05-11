package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.excel.export;

import it.eldasoft.utils.excel.DizionarioStili;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public interface IWriteExcel {

    public void write(HSSFSheet sheet, int numColonna, int numRiga, DizionarioStili stili);

}
