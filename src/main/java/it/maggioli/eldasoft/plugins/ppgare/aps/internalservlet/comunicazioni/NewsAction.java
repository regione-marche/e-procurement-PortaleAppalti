package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class NewsAction extends EncodedDataAction 
	implements ModelDriven<ComunicazioniSearchBean>, SessionAware 
{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 1926508534229004905L;

	private IBandiManager bandiManager;
	
	private Map<String, Object> session;

	private static final int ROWS_PER_PAGE 	= 10;
	
	private SearchResult<ComunicazioneType> comunicazioni;
	@Validate
	private ComunicazioniSearchBean model = new ComunicazioniSearchBean() ;
	private int pagina;
	@Validate(EParamValidation.ACTION)
	private String actionName;
	@Validate(EParamValidation.GENERIC)
	private String namespace;
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public ComunicazioniSearchBean getModel() {
		return this.model;
	}

	public SearchResult<ComunicazioneType> getComunicazioni() {
		return comunicazioni;
	}

	public void setComunicazioni(SearchResult<ComunicazioneType> comunicazioni) {
		this.comunicazioni = comunicazioni;
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

	/**
	 * 
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		try {
			// gestione del ritorno alla pagina aperta partendo dal dettaglio
			int startIndex = 0;
			if(this.getPagina() > 0) {
				this.model.setCurrentPage(this.getPagina());
			}
			if(this.model.getCurrentPage() > 0) {
				startIndex = ROWS_PER_PAGE * (this.model.getCurrentPage() - 1);
			}

			this.setComunicazioni(this.bandiManager.getNews(startIndex, ROWS_PER_PAGE));
			
			this.model.processResult(
					this.getComunicazioni().getNumTotaleRecord(), 
					this.getComunicazioni().getNumTotaleRecordFiltrati());
			
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.model.getCurrentPage());
//			this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
//			this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);			
			this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, "news");
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "open");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
