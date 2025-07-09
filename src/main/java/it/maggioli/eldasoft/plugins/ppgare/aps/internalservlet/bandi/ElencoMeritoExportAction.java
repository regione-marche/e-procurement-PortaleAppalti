package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.CategoryForOperator;
import it.eldasoft.www.sil.WSGareAppalto.ElencoOperatoriAbilitatiElenco;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.EAppParamCodiceClienti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.filter.ElencoMeritoFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities.escapeCsv;
import static it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.OpenPageElencoMeritoAction.SESSION_FILTER;
import static java.util.stream.Collectors.collectingAndThen;

public class ElencoMeritoExportAction extends EncodedDataAction implements SessionAware, IDownloadAction {

    private static final Logger logger = LoggerFactory.getLogger(ElencoMeritoFilter.class);

    private static final String[] HEADER = new String[] {
            "LABEL_RAGIONE_SOCIALE"
            , "LABEL_CODICE_FISCALE"
            , "LABEL_COMUNE"
            , "LABEL_PROVINCIA"
            , "LABEL_ELENCO"
            , "LABEL_STATO"
            , "LABEL_CATEGORIA"
            , "LABEL_CLASSIFICA"
    };
    private static final String[] HEADER_RER = new String[] {
            "LABEL_RAGIONE_SOCIALE"
            , "LABEL_CODICE_FISCALE"
            , "LABEL_COMUNE"
            , "LABEL_PROVINCIA"
            , "LABEL_OPERATORI_ISCRITTI_RATING"
            , "LABEL_STATO"
            , "LABEL_CATEGORIA"
            , "LABEL_CLASSIFICA"
    };

    private Map<String, Object> session;
    private IBandiManager bandiManager;
    private IAppParamManager appParamManager;

    private InputStream inputStream;
    private String currentFrame;

    private boolean isRer;

    public String exportCSV() {
        String target = SUCCESS;

        try {
            isRer = EAppParamCodiceClienti.RER.name().equalsIgnoreCase(
                    (String) appParamManager.getConfigurationValue(AppParamManager.CODICE_CLIENTE)
            );

            ElencoOperatoriAbilitatiElenco[] elenchi = retrieveElenchi();
            if (elenchi != null && elenchi.length > 0)
                inputStream = Arrays.stream(elenchi)
                        .map(this::toRow)
                    .collect(toCsvInputStream(getHeaderWithI18n(isRer ? HEADER_RER : HEADER)));
        } catch (Exception e) {
            logger.error("Errore generico", e);
        }

        return target;
    }

    private String getHeaderWithI18n(String[] header) {
        return Arrays.stream(header)
                .map(this::getI18nLabel)
                .map(StringUtils::capitalize)
                .collect(
                    Collectors.joining(""+StringUtilities.CSV_DELIMITER)
                );
    }

    private ElencoOperatoriAbilitatiElenco[] retrieveElenchi() throws ApsException {
        ElencoOperatoriAbilitatiElenco[] toReturn = null;

        Object filterObject = session.get(SESSION_FILTER);
        if (filterObject != null) {
            ElencoMeritoFilter filter = (ElencoMeritoFilter) filterObject;
            Integer start = filter.getiDisplayStart();
            Integer end = filter.getiDisplayLength();

            filter.setiDisplayStart(0);
            filter.setiDisplayLength(Integer.MAX_VALUE);

            toReturn = bandiManager.getElencoOperatoriAbilitatiElenco(filter.toSearch(), isRer).getResult();
            filter.setiDisplayLength(end);
            filter.setiDisplayStart(start);
        }

        return toReturn;
    }
    public static Collector<CharSequence, ?, ByteArrayInputStream> toCsvInputStream(String header) {
        return collectingAndThen(
                    collectingAndThen(Collectors.joining("\n"), it -> (header + "\n" + it).getBytes())
                    , ByteArrayInputStream::new
        );
    }

    private String toRow(ElencoOperatoriAbilitatiElenco it) {
        return it.getCategories() != null && it.getCategories().length > 0
                ?
                    Arrays.stream(it.getCategories())
                            .map(category -> toRow(it, category))
                    .collect(Collectors.joining("\n"))
                : toRow(it, null);
    }

    private String toRow(ElencoOperatoriAbilitatiElenco it, CategoryForOperator category) {
        try {
            return !isRer
                    ?
                        StringUtils.joinWith(
                                ""+StringUtilities.CSV_DELIMITER
                                , escapeCsv(it.getBusinessName())
                                , escapeCsv("\"" + StringUtils.defaultIfBlank(it.getFiscalCode(), "") + "\"")
                                , escapeCsv(StringUtils.defaultIfBlank(it.getCity(), ""))
                                , escapeCsv(StringUtils.defaultIfBlank(it.getProvince(), ""))
                                , escapeCsv(it.getCodElenco())
                                , InterceptorEncodedData.get(InterceptorEncodedData.LISTA_STATI_ELENCO).get(it.getStato() != null && it.getStato() ? "1" : "0")
                                , escapeCsv(category != null ? category.getCategory() : "")
                                , escapeCsv(category != null ? StringUtils.defaultIfBlank(category.getClassifica(), "") : "")
                        )
                    :
                        StringUtils.joinWith(
                                ""+StringUtilities.CSV_DELIMITER
                                , escapeCsv(it.getBusinessName())
                                , escapeCsv("\"" + StringUtils.defaultIfBlank(it.getFiscalCode(), "") + "\"")
                                , escapeCsv(StringUtils.defaultIfBlank(it.getCity(), ""))
                                , escapeCsv(StringUtils.defaultIfBlank(it.getProvince(), ""))
                                , escapeCsv(StringUtils.defaultIfBlank(it.getRating(), ""))
                                , InterceptorEncodedData.get(InterceptorEncodedData.LISTA_STATI_ELENCO).get(it.getStato() != null && it.getStato() ? "1" : "0")
                                , escapeCsv(category != null ? category.getCategory() : "")
                                , escapeCsv(category != null ? StringUtils.defaultIfBlank(category.getClassifica(), "") : "")
                        );
        } catch (Exception e) {
            logger.error("Errore durante la creazione della riga csv", e);
            return "";
        }
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getFilename() {
        return "report.csv";
    }

    @Override
    public void setUrlPage(String urlPage) { }
    public void setBandiManager(IBandiManager bandiManager) {
        this.bandiManager = bandiManager;
    }
    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }

    @Override
    public void setCurrentFrame(String currentFrame) {
        this.currentFrame = currentFrame;
    }

    @Override
    public String getUrlErrori() {
        return null;
    }

}
