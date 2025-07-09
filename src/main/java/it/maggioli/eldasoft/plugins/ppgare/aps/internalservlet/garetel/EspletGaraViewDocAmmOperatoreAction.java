package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.util.EspletamentoUtil;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.List;
import java.util.Map;

public class EspletGaraViewDocAmmOperatoreAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -2172294812073609912L;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
		
	@Validate(EParamValidation.CODICE)
	private String codice;
	private Long faseGara;
	@Validate(EParamValidation.USERNAME)
	private String codiceOper;
	private EspletGaraOperatoreType operatoreEconomico;
	private boolean hideFiscalCode = false;

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}	
	
	public Long getFaseGara() {
		return faseGara;
	}

	public String getCodiceOper() {
		return codiceOper;
	}

	public void setCodiceOper(String codiceOper) {
		this.codiceOper = codiceOper;
	}

	public EspletGaraOperatoreType getOperatoreEconomico() {
		return operatoreEconomico;
	}

	public void setOperatoreEconomico(EspletGaraOperatoreType operatoreEconomico) {
		this.operatoreEconomico = operatoreEconomico;
	}
	
	/**
	 * ...
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& this.codiceOper != null
			&& EspletGaraFasiAction.isAperturaDocAmministrativaAbilitata(this.session, this.codice))
		{
			try {
				// recupera i dati dell'operatore economico...
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				hideFiscalCode = EspletamentoUtil.hasToHideFiscalCode(dettGara);
				EspletamentoElencoOperatoriSearch search = new EspletamentoElencoOperatoriSearch();
				search.setCodice(codice);
				search.setCodiceOperatore(codiceOper);
				search.setUsername(getCurrentUser().getUsername());
				List<EspletGaraOperatoreType> operatori = bandiManager.getEspletamentoGaraDocAmmElencoOperatori(search);
				
				this.faseGara = this.bandiManager.getFaseGara(this.codice);
				
				EspletGaraOperatoreType operatore = (operatori != null && operatori.size() == 1 
					 ? operatori.get(0) 
					 : null);
					
				if(operatore == null) {
					// l'operatore economico non è stato trovato...
					this.addActionError(this.getText("Errors.espletamento.nonDisponibile"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				} else {
					// imposto i dati dell'operatore economico... 
					this.setOperatoreEconomico(operatore);
					
					this.setTarget(SUCCESS);
				}				
			} catch(Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "viewOperatore");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);	
		}
		
		return this.getTarget();
	}

	public boolean getHideFiscalCode() {
		return hideFiscalCode;
	}
}


