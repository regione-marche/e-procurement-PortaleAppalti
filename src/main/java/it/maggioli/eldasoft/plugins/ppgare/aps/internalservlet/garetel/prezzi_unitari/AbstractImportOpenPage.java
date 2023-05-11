package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

public abstract class AbstractImportOpenPage extends AbstractOpenPageAction {

    /**
     * ...
     */
    @Override
    public String openPage() {

        // si carica un eventuale errore parcheggiato in sessione, ad esempio in
        // caso di errori durante il download
        String errore = (String) this.session.remove(IDownloadAction.ERROR_DOWNLOAD);
        if (errore != null)
            this.addActionError(errore);

        // se si proviene dall'EncodedDataAction di ProcessPageRiepilogo con un
        // errore, devo resettare il target tanto va riaperta la pagina stessa
        if (INPUT.equals(this.getTarget()) || "errorWS".equals(this.getTarget()))
            this.setTarget(SUCCESS);

        if (isCurrentSessionExpired()) {
            // la sessione e' scaduta, occorre riconnettersi
            this.addActionError(this.getText("Errors.sessionExpired"));
            this.setTarget(CommonSystemConstants.PORTAL_ERROR);
        }
        return this.getTarget();
    }

    protected abstract boolean isCurrentSessionExpired();

}
