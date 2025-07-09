package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Action di apertura delle pagine del questionario.
 *
 * @author 
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageQuestionarioAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3626185012529007654L;
	private static final Logger logger = ApsSystemUtils.getLogger();

	private Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
	private String codice;
	
    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }
    	
	public String getSTEP_IMPRESA() {
		return WizardIscrizioneHelper.STEP_IMPRESA;
	}

	public String getSTEP_DENOMINAZIONE_RTI() {
		return WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI;
	}

	public String getSTEP_DETTAGLI_RTI() {
		return WizardIscrizioneHelper.STEP_DETTAGLI_RTI;
	}

	public String getSTEP_SELEZIONE_CATEGORIE() {
		return WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE;
	}
	
	public String getSTEP_RIEPILOGO_CAGTEGORIE() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE;
	}
    
	public String getSTEP_SCARICA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE;
	}
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA() {
		return WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA;
	}
	
	public String getSTEP_PRESENTA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE;
	}
	
	public String getSTEP_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_QUESTIONARIO;
	}
	
	public String getSTEP_RIEPILOGO_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO;
	}


	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	/**
	 * restituisce l'helper di iscrizione/rinnovo in sessione  
	 */
	private WizardIscrizioneHelper getHelperFromSession() {
		WizardIscrizioneHelper a = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		WizardRinnovoHelper b = (WizardRinnovoHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		if(a != null & b == null) {
			return a;
		} else if(b != null) {
			return b;
		}
		return null;
	}
	
	/**
	 * (QCompiler) apri la pagina del questionario 
	 */
	public String openPage() {
		String target = SUCCESS;
		logger.debug("openPage");
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 		 WizardIscrizioneHelper.STEP_QUESTIONARIO);

				WizardIscrizioneHelper helper = this.getHelperFromSession();
				
				this.codice = helper.getIdBando();
				WizardDocumentiHelper documenti = helper.getDocumenti();
				
				// carica questionario.json ed aggiorna la sezione "serverFilesUuids" per il client angular
				QCQuestionario questionario = documenti.getQuestionarioElenchi();
				if(questionario != null) {
					questionario.addServerFilesUuids(documenti);
					logger.debug("Impostati i serverFiles");
					String json = questionario.getQuestionario();
					documenti.updateQuestionario(json);
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			} 
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		logger.debug("openPage - END");
		return target;
	}

}
