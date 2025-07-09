package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.util;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import org.apache.commons.lang.StringUtils;

public final class EspletamentoUtil {

    private EspletamentoUtil() { }

    public static boolean hasToHideFiscalCode(DettaglioGaraType dettGara) {
        return dettGara.getDatiGeneraliGara().isIsConcProg()
                || (
                (
                        StringUtils.equals("9", dettGara.getDatiGeneraliGara().getIterGara())
                                || StringUtils.equals("10", dettGara.getDatiGeneraliGara().getIterGara())
                ) && !dettGara.getDatiGeneraliGara().isFineConc()
        );
    }

}
