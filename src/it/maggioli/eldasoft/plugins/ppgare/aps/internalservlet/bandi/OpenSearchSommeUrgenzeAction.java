package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import java.sql.Date;
import java.util.Map;

import it.eldasoft.www.sil.WSGareAppalto.DeliberaType;
import it.eldasoft.www.sil.WSGareAppalto.SommaUrgenzaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;

/**
 * ... 
 */
public class OpenSearchSommeUrgenzeAction extends EncodedDataAction 
	implements ModelDriven<SommeUrgenzeSearchBean>, SessionAware 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3531196301388576572L;
	
	private static final String LIST_SOMME_URGENZE	 			= "listSommeUrgenze";
	
	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
	private Map<String, Object> session;
	
	private SommeUrgenzeSearchBean model = new SommeUrgenzeSearchBean();
	private SearchResult<SommaUrgenzaType> listaSommeUrgenze;
	private String stazioneAppaltante; 

	@Override
	public SommeUrgenzeSearchBean getModel() {
		return this.model;
	}
	
	public void setModel(SommeUrgenzeSearchBean model) {
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
	
	public SearchResult<SommaUrgenzaType> getListaSommeUrgenze() {
		return listaSommeUrgenze;
	}

	public void setListaSommeUrgenze(SearchResult<SommaUrgenzaType> listaSommeUrgenze) {
		this.listaSommeUrgenze = listaSommeUrgenze;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}

	/**
	 * ...
	 */
	public String openPage() {
		this.setTarget(SUCCESS);
		
		this.model.setiDisplayLength(20);	// 20 default max item per page
		
		this.listaSommeUrgenze = null;
		
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}
		
		return this.getTarget();
	}
	
	/**
	 * Restituisce la lista delle delibere a contrarre
	 */
    public String listSommeUrgenze() {
		this.setTarget(SUCCESS);

		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(stazioneAppaltante);
		}

		// validazione del filtro date (se valorizzate)		
		boolean dateOk = true;
		
		Date dtPubblicazioneDa = null;
		if(StringUtils.isNotEmpty(this.model.getDataPubblicazioneDa())) {
			try {
				dtPubblicazioneDa = (Date) LocaleConvertUtils.convert(this.model.getDataPubblicazioneDa(),
						java.sql.Date.class, "dd/MM/yyyy");
			} catch (ConversionException e) {
				ApsSystemUtils.logThrowable(e, this, "listDelibere");
				this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_BANDO", DA_DATA, this.model.getDataPubblicazioneDa());
				this.model.setDataPubblicazioneDa(null);
				dateOk = false;
			}
		}

		Date dtPubblicazioneA = null;
		if(StringUtils.isNotEmpty(this.model.getDataPubblicazioneA())) {
			try {
				dtPubblicazioneA = (Date) LocaleConvertUtils.convert(this.model.getDataPubblicazioneA(),
						java.sql.Date.class, "dd/MM/yyyy");
			} catch (ConversionException e) {
				ApsSystemUtils.logThrowable(e, this, "listDelibere");
				this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_BANDO", A_DATA, this.model.getDataPubblicazioneA());
				this.model.setDataPubblicazioneA(null);
				dateOk = false;
			}
		}

		// estrazione dell'elenco delle delibere...
		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			try {
				int startIndex = 0;
				if(this.model.getCurrentPage() > 0){
					startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
				}
				
				this.setListaSommeUrgenze(this.bandiManager.getElencoSommaUrgenza(
						StringUtils.stripToNull(this.model.getStazioneAppaltante()), 
						StringUtils.stripToNull(this.model.getOggetto()), 
						StringUtils.stripToNull(this.model.getTipoAppalto()),	
						StringUtils.stripToNull(this.model.getCig()),
						dtPubblicazioneDa,
						dtPubblicazioneA,
						startIndex, 
						this.model.getiDisplayLength()));
											 
				this.model.processResult(this.getListaSommeUrgenze().getNumTotaleRecord(), 
						                 this.getListaSommeUrgenze().getNumTotaleRecordFiltrati());
				
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, LIST_SOMME_URGENZE);
				
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "listSommeUrgenze");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}		
				
		return this.getTarget();
	}    

}
