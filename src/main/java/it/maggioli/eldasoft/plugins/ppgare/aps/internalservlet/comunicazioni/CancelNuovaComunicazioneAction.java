package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class CancelNuovaComunicazioneAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8926614043648752196L;

	protected Map<String, Object> session;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	@Validate(EParamValidation.ACTION)
	protected String actionName;
	@Validate(EParamValidation.ACTION)
	protected String namespace;
	@Validate(EParamValidation.GENERIC)
	protected String tipo;
	@Validate(EParamValidation.GENERIC)
	protected String applicativo;
	protected Long idComunicazione;
	protected Long idDestinatario;
	protected int pagina;
	protected Long genere;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
    public String getApplicativo() {
      return applicativo;
    }

    public void setApplicativo(String applicativo) {
      this.applicativo = applicativo;
    }

    public Long getIdComunicazione() {
      return idComunicazione;
    }

	public void setId(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}
	
	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	
	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}

	/**
	 * ... 
	 */
	public String questionCancel() {
		String target = "confirm";
		WizardNuovaComunicazioneHelper helper = this.getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
	/**
	 * ... 
	 */
	public String cancel() {
		String target = SUCCESS;
		WizardNuovaComunicazioneHelper helper = this.getSessionHelper();
        this.applicativo = helper.getComunicazioneApplicativo();
		this.idComunicazione = helper.getComunicazioneId();
		this.idDestinatario = helper.getIdDestinatario();
		this.codice = StringUtils.stripToNull(helper.getCodice());
		this.actionName = StringUtils.stripToNull((String)this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME));
		this.namespace = (StringUtils.stripToNull((String)this.session.get(ComunicazioniConstants.SESSION_ID_NAMESPACE)));
		this.pagina = ((Integer)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA)).intValue();		
		String comunicazioneProcedura = (String)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
		this.clearSession();
		if (comunicazioneProcedura == null) {
			target = "backToComunicazione";
		} else {
			target = "backToDettaglio";
		}
		return target;
	}
		
	/** 
	 * Si rimuovono gli oggetti non pi&ugrave; necessari dalla sessione 
	 */
	protected void clearSession() {
		this.session.remove(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
	}
	
	/**
	 * Estrae l'helper del wizard dati della comunicazione da utilizzare
	 * 
	 * @return helper contenente i dati della comunicazione
	 */
	protected WizardNuovaComunicazioneHelper getSessionHelper() {
		WizardNuovaComunicazioneHelper helper = (WizardNuovaComunicazioneHelper) session
				.get(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
		return helper;
	}

}
