package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class RedirectSummaryEnvelopeAction extends EncodedDataAction {

    @Validate(EParamValidation.CODICE)
    private String codiceGara;
    @Validate(EParamValidation.CODICE)
    private String codice;
    private int tipoBusta;
    private int operazione;
    private boolean offertaTelematica;
    @Validate(EParamValidation.ACTION_PATH)
    private String backActionPath;

    public String doReset() {
        return "reset-envelope";
    }

    public String doModify() {
        String target;

        if (offertaTelematica)
            target = "off-tel";
        else
            target = "page-doc";

        return target;
    }


    public String getCodice() {
        return codice;
    }
    public void setCodice(String codice) {
        this.codice = codice;
    }
    public int getTipoBusta() {
        return tipoBusta;
    }
    public void setTipoBusta(int tipoBusta) {
        this.tipoBusta = tipoBusta;
    }
    public int getOperazione() {
        return operazione;
    }
    public void setOperazione(int operazione) {
        this.operazione = operazione;
    }
    public boolean isOffertaTelematica() {
        return offertaTelematica;
    }
    public void setOffertaTelematica(boolean offertaTelematica) {
        this.offertaTelematica = offertaTelematica;
    }
    public String getBackActionPath() {
        return backActionPath;
    }
    public void setBackActionPath(String backActionPath) {
        this.backActionPath = backActionPath;
    }
    public String getCodiceGara() {
        return codiceGara;
    }
    public void setCodiceGara(String codiceGara) {
        this.codiceGara = codiceGara;
    }
}
