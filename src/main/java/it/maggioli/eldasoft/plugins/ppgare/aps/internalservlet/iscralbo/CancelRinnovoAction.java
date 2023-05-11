package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class CancelRinnovoAction  extends BaseAction implements SessionAware{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -2313996142562674698L;

	private Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
    private String codice;
    
    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    public String getCodice() {
    	return codice;
    }

    
    /**
     * ...
     */
	public String questionCancelRinnovo() {
		String target = SUCCESS;
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		if (rinnovoHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
		    this.codice = rinnovoHelper.getIdBando();
		    if (rinnovoHelper.isAggiornamentoIscrizione()) {
		    	target += "CancAggIscrizione";
		    }
		}
		return target;
    }

    /**
     * ...
     */
	public String cancelRinnovo() {
		String target = SUCCESS;
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if (rinnovoHelper != null
			&& rinnovoHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO) {
			target = "successCatalogo";
		}
		// riga necessaria perche' per questa procedura esistono 2 macrooggetti
		// wizard in sessione, uno anche per i documenti
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
		
		return target;
	}
	
}
