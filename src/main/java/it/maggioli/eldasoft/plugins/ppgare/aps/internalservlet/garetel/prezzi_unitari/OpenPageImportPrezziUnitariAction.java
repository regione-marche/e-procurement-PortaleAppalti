package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;

public class OpenPageImportPrezziUnitariAction extends AbstractImportOpenPage {

    private IAppParamManager appParamManager;
    private String codice;

    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }

    public String getCodice() {
        return codice;
    }
    public void setCodice(String codice) {
        this.codice = codice;
    }

    public Integer getLimiteUploadFile() {
        return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
    }

    @Override
    protected boolean isCurrentSessionExpired() {
        return GestioneBuste.getBustaEconomicaFromSession().getHelper() == null;
    }
}
