package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageImportPrezziUnitariAction extends AbstractImportOpenPage {
    /**
	 * UID
	 */
	private static final long serialVersionUID = 4754095480598861043L;

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

    @Override
    protected boolean isCurrentSessionExpired() {
        return GestioneBuste.getBustaEconomicaFromSession().getHelper() == null;
    }

}
