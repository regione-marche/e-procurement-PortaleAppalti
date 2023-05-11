package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DownloadDatiImpresaAction extends EncodedDataAction implements SessionAware {

    private static final Logger log = LoggerFactory.getLogger(DownloadDatiImpresaAction.class);

    private static final String DO_CREATE_PDF = "successCreate";

    @Validate(EParamValidation.USERNAME)
    private String username;
    private String urlPdf;  //Non necessita di validazione
    private WizardRegistrazioneImpresaHelper datiImpresaHelper;
    private Map<String, Object> session;

    public String createPdf() {
        setTarget(DO_CREATE_PDF);
        if (StringUtils.isEmpty(username)) {
            String error = getText("Errors.opKO.noUserSelected");
            addActionError(error);
            setTarget(ERROR);
            log.debug("Error: {}", error);
        } else {
            try {
                datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
                        username
                        , this
                );
                urlPdf = "/do/FrontEnd/DatiImpr/createPdf.action"
                        + "?urlPage=/it/ppcommon_admin_searchoe.wp"
                        + "&amp;currentFrame=";

                session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, datiImpresaHelper);
            } catch (ApsException | XmlException e) {
                setTarget(ERROR);
                log.error("Error: ", e);
            }
        }

        return getTarget();
    }

    public String getUrlPdf() {
        return urlPdf;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
