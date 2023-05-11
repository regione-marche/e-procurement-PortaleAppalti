package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewOffEcoOperatoreAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 5991832884681689850L;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
		
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	private Long faseGara;
	@Validate(EParamValidation.USERNAME)
	private String codiceOper;
	private EspletGaraOperatoreType operatoreEconomico;
	private Integer tipoOffertaTelematica;
	private Integer nascondiValoriEspletamento; 
	private DettaglioGaraType dettaglioGara; 

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
	
	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
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
	
	public Integer getTipoOffertaTelematica() {
		return tipoOffertaTelematica;
	}

	public void setTipoOffertaTelematica(Integer tipoOffertaTelematica) {
		this.tipoOffertaTelematica = tipoOffertaTelematica;
	}
	
	public Integer getNascondiValoriEspletamento() { 
		return nascondiValoriEspletamento; 
	} 
 
	public void setNascondiValoriEspletamento(Integer nascondiValoriEspletamento) { 
		this.nascondiValoriEspletamento = nascondiValoriEspletamento; 
	}	
	
	public DettaglioGaraType getDettaglioGara() {
		return dettaglioGara;
	}

	public void setDettaglioGara(DettaglioGaraType dettaglioGara) {
		this.dettaglioGara = dettaglioGara;
	}

	/**
	 * ...
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& this.codiceOper != null
			&& EspletGaraFasiAction.isOffertaEconomicaAbilitata(this.session, this.codice))
		{
			try {
				// recupera i dati dell'operatore economico...
				boolean lottiDistinti = (!StringUtils.isEmpty(this.codiceLotto));
				String codiceGara = (lottiDistinti ? this.codiceLotto : this.codice);

				List<EspletGaraOperatoreType> operatori = this.bandiManager
					.getEspletamentoGaraOffEcoElencoOperatori(
						this.codice, this.codiceLotto, this.codiceOper);
				
				int tipologiaGara = EspletGaraFasiAction.getTipologiaGara(this.session, this.codice);
				if(tipologiaGara == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
					 codiceGara = this.codice;
				}
				this.faseGara = this.bandiManager.getFaseGara(codiceGara);
				
				EspletGaraOperatoreType operatore = (operatori != null && operatori.size() == 1 
					 ? operatori.get(0) 
					 : null);
					
				if(operatore == null) {
					// l'operatore economico non e' stato trovato...
					this.addActionError(this.getText("Errors.espletamento.nonDisponibile"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				} else {
					// imposto i dati dell'operatore economico... 
					this.setOperatoreEconomico(operatore);
				
					this.setTarget(SUCCESS);
				}
				this.dettaglioGara = this.bandiManager.getDettaglioGara(codiceGara); 
				this.nascondiValoriEspletamento = this.dettaglioGara.getDatiGeneraliGara().getNascondiValoriEspletamento(); 

				this.tipoOffertaTelematica = EspletGaraFasiAction.getTipoOffertaTelematica(this.session, codiceGara);
				
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
	
}
