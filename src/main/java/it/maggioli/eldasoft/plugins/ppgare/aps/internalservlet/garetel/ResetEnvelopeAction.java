package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.apsadmin.system.TokenInterceptor;
import com.opensymphony.xwork2.ActionContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ResetEnvelopeAction extends EncodedDataAction {

    private IEventManager eventManager;

    /**
     * Codice gara (solo in caso di gara a lotti)
     */
    @Validate(EParamValidation.CODICE)
    private String codiceGara;
    /**
     * Codige gara o lotto in caso di gara a lotti
     */
    @Validate(EParamValidation.CODICE)
    private String codice;
    private int tipoBusta;
    private int operazione;
    private boolean offertaTelematica;
    /**
     * Action utilizzata da askResetEnvelop.jsp per costruire l'urlRedirect
     */
    @Validate(EParamValidation.ACTION_PATH)
    private String backActionPath;
    /**
     * Url per aprire la pagina di riepilogo
     */
    @Validate(EParamValidation.URL)
    private String urlRedirect;

    public String success() { return SUCCESS; }

    /**
     * Resetta la busta mettendo la comunicazione attuale in stato "99".
     *
     * @return : go-to-riepilogo = torna al riepilogo; : ERROR = Errore durante il reset della busta
     */
    public String resetEnvelope() {
        setTarget(ERROR);
        try {
            GestioneBuste gestioneBuste = GestioneBuste.getFromSession();
            if (gestioneBuste != null) {
                BustaDocumenti busta = gestioneBuste.getBusta(tipoBusta);
                // In caso di lotto mi recupero la busta del lotto
                if (codiceGara != null && StringUtils.isNotEmpty(codice) && !StringUtils.equals(codice, codiceGara) && !busta.get(null, codice)) {
                    addErrorAndLog(String.format(getText("Errors.load.lotto"), codice, codiceGara));
                    return getTarget();
                }

                if (!busta.send("99"))
                    addErrorAndLog(getText("Errors.communication.update"));
                else {
                    gestioneBuste.getBustaRiepilogo()
                        .getHelper()
                        .getRiepilogoBusta(tipoBusta, codiceGara != null && StringUtils.isNotEmpty(codice) && !StringUtils.equals(codice, codiceGara) ? codice : null)
                        .setQuestionarioCompletato(false);
                    putRedirectParameters();
                    setTarget("go-to-riepilogo");
                    addMessageAndLog(String.format(getText("Envelope.reset"), busta.getDescrizioneBusta()));
                }
            } else
                addErrorAndLog(getText("Errors.sessionExpired"));
        } catch (Throwable e) {
            addErrorAndLog(getText("Errors.generic.reset.envelope"));
        }

        logger.debug("END - resetEnvelope");

        return getTarget();
    }

    /**
     * Aggiungo il token csrf per la redirect + i parametri che servono alla chiamata
     */
    private void putRedirectParameters() {
        ActionContext.getContext()
            .getParameters()
        .put(
            TokenInterceptor.getStrutsTokenName()
            , new String[] { TokenInterceptor.saveSessionToken(this.getRequest()) }
        );
        TokenInterceptor.redirectToken();

        // Aggiungo manualmente all'url del redirect i parametri necessari

        if (!urlRedirect.contains("?"))
            urlRedirect += "?";
        else if (!urlRedirect.endsWith("?"))
            urlRedirect += "&";

        urlRedirect +=
            Stream.of(
                Pair.of("codice", codice)
                , Pair.of("operazione", operazione)
                , Pair.of("tipoBusta", tipoBusta)
                , Pair.of("offertaTelematica", offertaTelematica)
                , Pair.of("codiceGara", codiceGara) // Valorizzato solo x lotti
            )
                .map(it -> String.format("%s=%s", it.getKey(), it.getValue() != null ? it.getValue().toString() : ""))
            .collect(Collectors.joining("&"));
    }

//    private void sendResetEvent(Long idComunicazione, String descrizioneBusta) {
//        Event evento = new Event();
//        evento.setUsername(getCurrentUser().getUsername());
//        evento.setLevel(Event.Level.INFO);
//        evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
//        evento.setIpAddress(getCurrentUser().getIpAddress());
//        evento.setDestination(codiceGara != null ? codiceGara : codice);
//        evento.setSessionId(getRequest().getSession().getId());
//        evento.setEventDate(new Date());
//        evento.setMessage(
//            String.format(
//                getText("Event.reset.envelope")
//                , descrizioneBusta
//                , idComunicazione
//            )
//        );
//        evento.setDetailMessage(getText("Event.reset.envelope.detail"));
//
//        eventManager.insertEvent(evento);
//    }

    public IEventManager getEventManager() {
        return eventManager;
    }
    public void setEventManager(IEventManager eventManager) {
        this.eventManager = eventManager;
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
    public String getUrlRedirect() {
        return urlRedirect;
    }
    public void setUrlRedirect(String urlRedirect) {
        this.urlRedirect = urlRedirect;
    }
    public String getCodiceGara() {
        return codiceGara;
    }
    public void setCodiceGara(String codiceGara) {
        this.codiceGara = codiceGara;
    }

    /**
     * Richiamato da askResetEnvelope.jsp
     * @return
     * @throws Exception
     */
    public String retrieveEnvelopeName() throws Exception {
        return GestioneBuste.getFromSession().getBusta(tipoBusta).getDescrizioneBusta();
    }

}
