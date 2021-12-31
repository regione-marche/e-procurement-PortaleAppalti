package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import java.sql.Date;
import java.util.Map;

import it.eldasoft.www.sil.WSGareAppalto.DeliberaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;

/**
 * ... 
 */
public class OpenSearchDelibereAction extends EncodedDataAction 
	implements ModelDriven<DelibereSearchBean>, SessionAware 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7120555760616028115L;

	private IBandiManager bandiManager;
	private IAppParamManager appParamManager;
	private Map<String, Object> session;
	
	private DelibereSearchBean model = new DelibereSearchBean();
	private SearchResult<DeliberaType> listaDelibere;
	private String stazioneAppaltante; 

	@Override
	public DelibereSearchBean getModel() {
		return this.model;
	}
	
	public void setModel(DelibereSearchBean model) {
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

	public SearchResult<DeliberaType> getListaDelibere() {
		return listaDelibere;
	}

	public void setListaDelibere(SearchResult<DeliberaType> listaDelibere) {
		this.listaDelibere = listaDelibere;
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
		
		this.listaDelibere = null;
		
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}
		
		return this.getTarget();
	}
	
	/**
	 * Restituisce la lista delle delibere a contrarre
	 */
    public String listDelibere() {
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
		
		// validazione del filtro somma urgenza (se valorizzate)
		Boolean sommaUrgenza = null;
		if (StringUtils.isNotEmpty(this.model.getSommaUrgenza())) {
			sommaUrgenza = "1".equals(model.getSommaUrgenza());
		}

		// estrazione dell'elenco delle delibere...
		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			try {
				int startIndex = 0;
				if(this.model.getCurrentPage() > 0){
					startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
				}
				
				this.setListaDelibere(this.bandiManager.getDelibere(
						StringUtils.stripToNull(this.model.getStazioneAppaltante()), 
						StringUtils.stripToNull(this.model.getOggetto()), 
						StringUtils.stripToNull(this.model.getTipoAppalto()),	
						StringUtils.stripToNull(this.model.getCig()),
						dtPubblicazioneDa,
						dtPubblicazioneA,
						sommaUrgenza, 		
						startIndex, 
						this.model.getiDisplayLength()));
											 
				this.model.processResult(this.getListaDelibere().getNumTotaleRecord(), 
						                 this.getListaDelibere().getNumTotaleRecordFiltrati());
				
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "listDelibere");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}		
				
		return this.getTarget();
	}    

}
