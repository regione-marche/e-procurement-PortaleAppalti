package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.ElencoOperatoriAbilitatiElenchiOutType;
import it.eldasoft.www.sil.WSGareAppalto.ElencoOperatoriAbilitatiElenco;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.EAppParamCodiceClienti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.filter.ElencoMeritoFilter;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OpenPageElencoMeritoAction extends AbstractOpenPageAction implements ModelDriven<ElencoMeritoFilter> {

    private static final Logger log = LoggerFactory.getLogger(OpenPageElencoMeritoAction.class);

    private IBandiManager bandiManager;
    private IAppParamManager appParamManager;

    private SearchResult<ElencoOperatoriAbilitatiElenco> listaElenchi = new SearchResult<>();
    @Validate
    private final ElencoMeritoFilter model = new ElencoMeritoFilter();
    public static final String SESSION_FILTER = "ELENCO_FILTER_RER";

    private boolean isRer = false;

    @Override
    public String openPage() {
        String target = SUCCESS;

        try {
            isRer = EAppParamCodiceClienti.RER.name().equalsIgnoreCase(
                    (String) appParamManager.getConfigurationValue(AppParamManager.CODICE_CLIENTE)
            );
            session.put(SESSION_FILTER, model);

            recuperaElenchi();
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "openPage");
            ExceptionUtils.manageExceptionError(e, this);
//            addActionError("Errore durante il recupero dei dati");
//            this.setTarget(CommonSystemConstants.PORTAL_ERROR);
        }

        return target;
    }

    private void recuperaElenchi() throws ApsException {
        ElencoOperatoriAbilitatiElenchiOutType elenchi = bandiManager.getElencoOperatoriAbilitatiElenco(model.toSearch(), isRer);
        if (elenchi != null && elenchi.getResult() != null) {
            listaElenchi.setDati(Arrays.stream(elenchi.getResult()).collect(Collectors.toList()));
            model.processResult(
                    listaElenchi.getDati().size()
                    , (int) elenchi.getTotaleOPAbilitati()
            );
        } else
            model.processResult(0, 0);
    }

    public void setBandiManager(IBandiManager bandiManager) {
        this.bandiManager = bandiManager;
    }
    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }

    public SearchResult<ElencoOperatoriAbilitatiElenco> getListaElenchi() {
        return listaElenchi;
    }

    public boolean getIsRer() {
        return isRer;
    }

    @Override
    public ElencoMeritoFilter getModel() {
        return model;
    }

}
