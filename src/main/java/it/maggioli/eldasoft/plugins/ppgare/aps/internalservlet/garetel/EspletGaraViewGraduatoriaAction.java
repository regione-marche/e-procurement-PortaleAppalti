package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class EspletGaraViewGraduatoriaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4411517742629697477L;
	
	public static final String GRADUATORIA_PRIMA_CLASSIFICATA = "Prima ditta classificata";
	public static final String GRADUATORIA_IDONEA = "Idonea";
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;	
	private Long faseGara;
	private Boolean lottiDistinti;
	private List<EspletGaraOperatoreType> elencoOperatori;
	private Boolean proceduraInversa;
	private Integer tipoOffertaTelemantica;
	private Integer nascondiValoriEspletamento; 

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

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}
	
	public Boolean getLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(Boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}

	public List<EspletGaraOperatoreType> getElencoOperatori() {
		return elencoOperatori;
	}

	public void setElencoOperatori(List<EspletGaraOperatoreType> elencoOperatori) {
		this.elencoOperatori = elencoOperatori;
	}
	
	public Boolean getProceduraInversa() {
		return proceduraInversa;
	}

	public void setProceduraInversa(Boolean proceduraInversa) {
		this.proceduraInversa = proceduraInversa;
	}
	
	public Integer getTipoOffertaTelemantica() {
		return tipoOffertaTelemantica;
	}

	public void setTipoOffertaTelemantica(Integer tipoOffertaTelemantica) {
		this.tipoOffertaTelemantica = tipoOffertaTelemantica;
	}
	
	public Integer getNascondiValoriEspletamento() { 
		return nascondiValoriEspletamento; 
	} 
 
	public void setNascondiValoriEspletamento(Integer nascondiValoriEspletamento) { 
		this.nascondiValoriEspletamento = nascondiValoriEspletamento; 
	} 
	/**
	 * apre la pagina della graduatoria 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& EspletGaraFasiAction.isGraduatoriaAbilitata(this.session, this.codice)) 
		{
			try {
				this.lottiDistinti = (this.codiceLotto != null && !this.codiceLotto.isEmpty());
				
				String codiceGara = (this.lottiDistinti ? this.codiceLotto : this.codice);
				
				this.elencoOperatori = this.bandiManager
					.getEspletamentoGaraGraduatoriaElencoOperatori(codiceGara, null);
				
				// nel caso di accordo quadro esiste la possibilita' di avere
				// piu' aggiudicatarie, in tal caso si modifica la dicitura
				// "Prima ditta classificata" in "Idonea" visto che trattasi di
				// piu' di un operatore e non ha senso visualizzare piu' prime
				// ditte classificate
				List<EspletGaraOperatoreType> aggiudicatarie = new ArrayList<EspletGaraOperatoreType>();
				if(this.elencoOperatori != null) {
					for (EspletGaraOperatoreType operatore : this.elencoOperatori) {
						if (GRADUATORIA_PRIMA_CLASSIFICATA.equals(operatore.getGraduatoria())) {
							aggiudicatarie.add(operatore);
						}
					}
				}
				if (aggiudicatarie.size() > 1) {
					// solo nel caso di piu' aggiudicatarie si cambia la descrizione della graduatoria
					for (EspletGaraOperatoreType aggiudicataria : aggiudicatarie) {
						aggiudicataria.setGraduatoria(GRADUATORIA_IDONEA);
					}
				}
				
				int tipologiaGara = EspletGaraFasiAction.getTipologiaGara(this.session, this.codice);
				if(tipologiaGara == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
					 codiceGara = this.codice;
				}
				this.faseGara = this.bandiManager.getFaseGara(codiceGara);
				
				this.proceduraInversa = false;
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				if(dettGara != null) {
					this.proceduraInversa = dettGara.getDatiGeneraliGara().isProceduraInversa();
				}
				DettaglioGaraType dettaglioGara = bandiManager.getDettaglioGara(codiceGara); 
				nascondiValoriEspletamento = dettaglioGara.getDatiGeneraliGara().getNascondiValoriEspletamento(); 

				this.tipoOffertaTelemantica = EspletGaraFasiAction.getTipoOffertaTelematica(this.session, this.codice);				

			} catch(Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "view");
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
