package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;


public class ComunicazioniInviateAction extends AbstractOpenPageAction implements ModelDriven<ComunicazioniSearchBean>, SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1479643031996378000L;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;
	@Validate(EParamValidation.CODICE)
	private String comunicazioniCodiceProcedura;
	@Validate(EParamValidation.CODICE)
	private String codice2;
	private SearchResult<ComunicazioneType> comunicazioni;
	@Validate(EParamValidation.TIPO_COMUNICAZIONE)
	private String tipoComunicazione;
	private int pagina;
	@Validate(EParamValidation.ACTION)
	private String actionName;
	@Validate(EParamValidation.ACTION)
	private String namespace;
	private Long genere;
	private Long id;
	@Validate(EParamValidation.ENTITA)
	private String entita;
	
	// attributi per la navigaione
	private Long idComunicazione;
	private Long idDestinatario;
	@Validate
	private ComunicazioniSearchBean model = new ComunicazioniSearchBean() ;


	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	protected IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComunicazioniCodiceProcedura() {
		return comunicazioniCodiceProcedura;
	}

	public void setComunicazioniCodiceProcedura(String comunicazioniCodiceProcedura) {
		this.comunicazioniCodiceProcedura = comunicazioniCodiceProcedura;
	}
	
	public String getCodice2() {
		return codice2;
	}

	public void setCodice2(String codice2) {
		this.codice2 = codice2;
	}

	public SearchResult<ComunicazioneType> getComunicazioni() {
		return comunicazioni;
	}

	public void setComunicazioni(SearchResult<ComunicazioneType> comunicazioni) {
		this.comunicazioni = comunicazioni;
	}

	@Override
	public ComunicazioniSearchBean getModel() {
		return this.model;
	}

	public String viewDettaglioComunicazione(){
		return SUCCESS;
	}

	public String getOperazione() {
		return tipoComunicazione;
	}

	public void setTipoComunicazione(String tipoComunicazione) {
		this.tipoComunicazione = tipoComunicazione;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
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
	
	public Long getGenere() {
		return genere;
	}

	public void setgenere(Long genere) {
		this.genere = genere;
	}
	
	public String getEntita() {
		return entita;
	}

	public void setEntita(String entita) {
		this.entita = entita;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {

		UserDetails userDetails = this.getCurrentUser();
		int startIndex = 0;
		
		// gestione del ritorno alla pagina aperta partendo dal dettaglio
		if(this.getPagina() > 0) {
			this.model.setCurrentPage(this.getPagina());
		}

		try {
			// in caso si torni dal Dettaglio Comunicazioni premendo il link "Torna alla lista"...
			// i seguenti parametri non vengono passati tra i parametri delle request e response...
			if(StringUtils.isEmpty(this.actionName) && StringUtils.isEmpty(this.namespace)) {
				this.actionName = (String) this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
				this.namespace = (String) this.session.get(ComunicazioniConstants.SESSION_ID_NAMESPACE);
			}

			if(this.model.getCurrentPage() > 0) {
				startIndex = 10 * (this.model.getCurrentPage() - 1);
			}
			this.entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			this.comunicazioniCodiceProcedura = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.codice2 = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA);
			this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
			
			this.setComunicazioni(this.getBandiManager().getComunicazioniPersonaliInviate(
					userDetails.getUsername(), 
					StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
					null,
					null,
					startIndex, 
					10));
			this.model.processResult(this.getComunicazioni().getNumTotaleRecord(), this.getComunicazioni().getNumTotaleRecordFiltrati());
			this.setTipoComunicazione("inviate");
			
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO, this.tipoComunicazione);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.model.getCurrentPage());
			this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
			this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
			if(this.genere != null) {
				this.entita = null;
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			}
	
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPage");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

}
