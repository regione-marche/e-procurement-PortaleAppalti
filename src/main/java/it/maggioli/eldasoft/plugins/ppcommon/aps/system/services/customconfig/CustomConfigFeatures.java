package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import org.apache.commons.lang3.tuple.Pair;

public final class CustomConfigFeatures {

    public static final Pair<String, String> MULTILINGUA  = Pair.of("MULTILINGUA", "TABELLATI");
    public static final Pair<String, String> PREFISSO_TEL = Pair.of("IMPRESA-RECAPITITEL", "FORMATOSIMAP");
    public static final Pair<String, String> IBAN_MANDATORY = Pair.of("IMPRESA-DATIULT-ALTRIDATI", "IBAN");

    private CustomConfigFeatures() { }

}
