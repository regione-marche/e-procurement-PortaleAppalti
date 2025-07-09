package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;

/**
 * ... 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPrezziUnitariImportExportAction extends AbstractImportOpenPage {
    /**
	 * UID
	 */
	private static final long serialVersionUID = 6073699947793513294L;

	@Override
    protected boolean isCurrentSessionExpired() {
        return GestioneBuste.getBustaEconomicaFromSession().getHelper() == null;
    }

}
