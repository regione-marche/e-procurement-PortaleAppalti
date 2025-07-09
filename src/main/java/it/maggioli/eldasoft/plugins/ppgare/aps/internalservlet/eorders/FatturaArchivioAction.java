package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.maggioli.eldasoft.nso.client.invoker.ApiException;
import it.maggioli.eldasoft.nso.client.model.PageOfNsoWsInvSdi;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.FattureManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Azione per la gestione delle operazioni a livello di dettaglio ordine
 * 
 * @author 
 * @since 
 */
public class FatturaArchivioAction extends EncodedDataAction implements SessionAware, ModelDriven<PageOfNsoWsInvSdi> {
	
	private static final long serialVersionUID = -8564702344327853135L;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Long id;
	private Long idFatt;
	@Validate(EParamValidation.GENERIC)
	private String orderCode;
	private Map<String, Object> session; 
	private PageOfNsoWsInvSdi model;
	private FattureManager fattManager;
	private IBandiManager bandiManager;
	
	public FattureManager getFattManager() {
		return fattManager;
	}
	public void setFattManager(FattureManager fattManager) {
		this.fattManager = fattManager;
	}
	public IBandiManager getBandiManager() {
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
	public Long getIdFatt() {
		return idFatt;
	}
	public void setIdFatt(Long idFatt) {
		this.idFatt = idFatt;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String openPage() {
		this.setTarget(SUCCESS);
		logger.debug("openPage called");
		int page = 0;
		int pageSize = 10;
		try {
			this.model = this.fattManager.getListaFattura(this.getIdImpresa(),this.getOrderCode(),page,pageSize);
		} catch (ApiException e) {
			logger.error("Error on api {}",e);
			this.model = new PageOfNsoWsInvSdi();
			this.model.setTotalElements(0l);
		} catch (Exception e) {
			logger.error("Error on openpage",e);
			this.model = new PageOfNsoWsInvSdi();
			this.model.setTotalElements(0l);
		}
		logger.debug("openPage finished");
		return this.getTarget();
	}
	
	public String cancel() {
		logger.debug("Called FatturaArchivioAction.cancel");
		return "cancelToOrder";
	}
	
	
	@Override
	public PageOfNsoWsInvSdi getModel() {
		return model;
	}
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	/**
	 * recupera il codice impresa associato all'utente
	 * @throws ApsException 
	 */
	private String getIdImpresa() throws ApsException {
		String codimp = (String) this.session.get(PortGareSystemConstants.SESSION_ID_IMPRESA);
		if(StringUtils.isEmpty(codimp)) {
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				codimp = this.bandiManager.getIdImpresa(userDetails.getUsername());
				this.session.put(PortGareSystemConstants.SESSION_ID_IMPRESA, codimp);	
			}
		}	
		return codimp;
	}
	
	
	
}
