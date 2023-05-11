package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;

public class OpenPrezziUnitariImportExportAction extends AbstractImportOpenPage {

    @Override
    protected boolean isCurrentSessionExpired() {
        return GestioneBuste.getBustaEconomicaFromSession().getHelper() == null;
    }

}
