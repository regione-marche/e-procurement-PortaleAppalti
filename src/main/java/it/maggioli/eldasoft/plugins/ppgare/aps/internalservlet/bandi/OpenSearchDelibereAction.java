package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.sil.WSGareAppalto.DeliberaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.SystemConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.util.*;

/**
 * ...
 */
public class OpenSearchDelibereAction extends EncodedDataAction
        implements ModelDriven<DelibereSearchBean>, SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -7120555760616028115L;

    private IBandiManager bandiManager;
    private ConfigInterface configManager;
    private IAppParamManager appParamManager;
    private Map<String, Object> session;

    @Validate
    private DelibereSearchBean model = new DelibereSearchBean();
    private SearchResult<DeliberaType> listaDelibere;
    @Validate(EParamValidation.STAZIONE_APPALTANTE)
    private String stazioneAppaltante;
    private String last;
    private InputStream inputStream;

    @Override
    public DelibereSearchBean getModel() {
        return this.model;
    }

    public void setModel(DelibereSearchBean model) {
        this.model = model;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setBandiManager(IBandiManager bandiManager) {
        this.bandiManager = bandiManager;
    }

    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }

    public SearchResult<DeliberaType> getListaDelibere() {
        return listaDelibere;
    }

    public void setListaDelibere(SearchResult<DeliberaType> listaDelibere) {
        this.listaDelibere = listaDelibere;
    }

    public String getStazioneAppaltante() {
        return stazioneAppaltante;
    }

    public void setStazioneAppaltante(String stazioneAppaltante) {
        this.stazioneAppaltante = stazioneAppaltante;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setConfigManager(ConfigInterface configManager) {
        this.configManager = configManager;
    }


    /**
     * ...
     */
    public String openPage() {
        this.setTarget(SUCCESS);

        this.model.setiDisplayLength(20);    // 20 default max item per page

        this.listaDelibere = null;

        this.stazioneAppaltante = this.getCodiceStazioneAppaltante();
        if (this.stazioneAppaltante != null) {
            this.model.setStazioneAppaltante(this.stazioneAppaltante);
        }

        return this.getTarget();
    }

    /**
     * Restituisce la lista delle delibere a contrarre
     */
    public String listDelibere() {
        this.setTarget(SUCCESS);
        search(whichModel(), model.getCurrentPage() > 0 ?  model.getiDisplayLength() * (model.getCurrentPage() - 1) : 0 );
        return this.getTarget();
    }

    public String export() {
        DelibereSearchBean exportModel = whichModel();
        exportModel.setiDisplayLength(Integer.MAX_VALUE);
        search(exportModel, 0);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(stream);

		//intestazione
       new ArrayList<>(Arrays.asList(
                        "LABEL_STAZIONE_APPALTANTE",
                        "LABEL_TITOLO",
                        "LABEL_CIG",
                        "LABEL_RIFERIMENTO_PROCEDURA",
                        "LABEL_DATA_PUBBLICAZIONE_BANDO",
                        "LABEL_DATA_ATTO",
                        "LABEL_NUMERO_ATTO",
                        "LABEL_DESCRIZIONE_DOCUMENTO"
                )).forEach(campo -> {
            writer.print(this.getI18nLabel(campo));
            writer.print(StringUtilities.CSV_DELIMITER);
        });
        writer.println();

		//dati
        listaDelibere.getDati().forEach(deliberaType -> {
            new ArrayList<>(Arrays.asList(
                    stringForCsv(deliberaType.getStazioneAppaltante()),
                    stringForCsv(deliberaType.getOggetto()),
                    stringForCsv(wrapForExcel(deliberaType.getCig())),
                    stringForCsv(wrapForExcel(deliberaType.getCodice())),
                    dateForCsv(deliberaType.getDataPubblicazione()),
                    dateForCsv(deliberaType.getDataAtto()),
                    stringForCsv(wrapForExcel(deliberaType.getNumeroAtto())),
                    stringForCsv(wichUrlDoc(deliberaType))
            )).forEach(s -> writeElement(writer, s));
            writer.println();
        });

        this.inputStream = new ByteArrayInputStream(stream.toByteArray());

        return "export";
    }

    private void writeElement(PrintStream writer, String element) {
        writer.print(element);
        writer.print(StringUtilities.CSV_DELIMITER);
    }

    private void search(DelibereSearchBean model, int startIndex) {

        // se è stata impostata una stazione appaltante nei parametri del portale
        // allora i dati vanno sempre filtrati per questa stazione appaltante...
        this.stazioneAppaltante = this.getCodiceStazioneAppaltante();
        if (this.stazioneAppaltante != null) {
            model.setStazioneAppaltante(stazioneAppaltante);
        }

        // validazione del filtro date (se valorizzate)
        boolean dateOk = true;

        Date dtPubblicazioneDa = null;
        if (StringUtils.isNotEmpty(model.getDataPubblicazioneDa())) {
            try {
                dtPubblicazioneDa = (Date) LocaleConvertUtils.convert(model.getDataPubblicazioneDa(),
                        java.sql.Date.class, "dd/MM/yyyy");
            } catch (ConversionException e) {
                ApsSystemUtils.logThrowable(e, this, "listDelibere");
                this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_BANDO", DA_DATA, model.getDataPubblicazioneDa());
                model.setDataPubblicazioneDa(null);
                dateOk = false;
            }
        }

        Date dtPubblicazioneA = null;
        if (StringUtils.isNotEmpty(model.getDataPubblicazioneA())) {
            try {
                dtPubblicazioneA = (Date) LocaleConvertUtils.convert(model.getDataPubblicazioneA(),
                        java.sql.Date.class, "dd/MM/yyyy");
            } catch (ConversionException e) {
                ApsSystemUtils.logThrowable(e, this, "listDelibere");
                this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_BANDO", A_DATA, model.getDataPubblicazioneA());
                model.setDataPubblicazioneA(null);
                dateOk = false;
            }
        }

        // validazione del filtro somma urgenza (se valorizzate)
        Boolean sommaUrgenza = null;
        if (StringUtils.isNotEmpty(model.getSommaUrgenza())) {
            sommaUrgenza = "1".equals(model.getSommaUrgenza());
        }

        // estrazione dell'elenco delle delibere...
        if (SUCCESS.equals(this.getTarget()) && dateOk) {
            try {
                this.setListaDelibere(this.bandiManager.getDelibere(
                        StringUtils.stripToNull(model.getStazioneAppaltante()),
                        StringUtils.stripToNull(model.getOggetto()),
                        StringUtils.stripToNull(model.getTipoAppalto()),
                        StringUtils.stripToNull(model.getCig()),
                        dtPubblicazioneDa,
                        dtPubblicazioneA,
                        sommaUrgenza,
                        startIndex,
                        model.getiDisplayLength()));

                model.processResult(this.getListaDelibere().getNumTotaleRecord(),
                        this.getListaDelibere().getNumTotaleRecordFiltrati());
                this.session.put("formSearchDelibere", model);

            } catch (ApsException e) {
                ApsSystemUtils.logThrowable(e, this, "listDelibere");
                ExceptionUtils.manageExceptionError(e, this);
                this.setTarget(CommonSystemConstants.PORTAL_ERROR);
            }
        }
    }

    DelibereSearchBean whichModel() {
        if ("1".equals(this.last)) {
            // se si richiede il rilancio dell'ultima estrazione effettuata,
            // allora si prendono dalla sessione i filtri applicati e si
            // caricano nel presente oggetto
            return (DelibereSearchBean) this.session
                    .get("formSearchDelibere");
        } else {
            return getModel();
        }
    }

    String stringForCsv(String string) {
        return StringUtilities.escapeCsv(StringUtils.defaultString(string));
    }

    String dateForCsv(Calendar cal) {
        return cal != null ?
                UtilityDate.convertiData(Date.from(cal.toInstant()), UtilityDate.FORMATO_GG_MM_AAAA)
                : "";
    }

    String wichUrlDoc(DeliberaType deliberaType) {
		String baseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
        return !StringUtils.isBlank(deliberaType.getUrlDoc()) ?
                deliberaType.getUrlDoc() :
                deliberaType.getFileDoc().toUpperCase().endsWith(".P7M") || deliberaType.getFileDoc().toUpperCase().endsWith(".TSD") ?
						baseUrl + session.get("WW_TRANS_I18N_LOCALE").toString() + "/ppgare_delibere_contrarre.wp?actionPath=/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action&currentFrame=7&id=" + deliberaType.getIdDoc() + "&idprg="
						: baseUrl + "do/FrontEnd/DocDig/downloadDocumentoPubblico.action?id=" + deliberaType.getIdDoc();

    }

    String wrapForExcel(String s) {
        return !StringUtils.isEmpty(s) ? "\"" + s + "\"" : "";
    }
}
