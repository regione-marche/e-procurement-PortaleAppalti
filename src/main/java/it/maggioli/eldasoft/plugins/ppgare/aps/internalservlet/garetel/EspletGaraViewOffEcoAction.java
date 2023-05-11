package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewOffEcoAction extends EncodedDataAction implements SessionAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5311787704219394626L;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;	
	private Long faseGara;
	private Boolean lottiDistinti;
	private List<EspletGaraOperatoreType> elencoOperatori;
	@Validate(EParamValidation.CODICE)
	private String lotto;
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
	
	public String getLotto() {
		return lotto;
	}

	public void setLotto(String lotto) {
		this.lotto = lotto;
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
			&& EspletGaraFasiAction.isOffertaEconomicaAbilitata(this.session, this.codice)) 
		{
			try {
				this.lottiDistinti = (!StringUtils.isEmpty(codiceLotto));
				String codiceGara = (this.lottiDistinti ? this.codiceLotto : this.codice);
				
				this.elencoOperatori = this.bandiManager
					.getEspletamentoGaraOffEcoElencoOperatori(this.codice, this.codiceLotto, null);

				// NB: 
				//	per le gare plico unico offerta unica
				//  l'informazione del soccorso istruttorio puo' essere 
				//  nella riga della lotto fittizio o nella riga del lotto
				List<EspletGaraOperatoreType> soccorsi = this.bandiManager
					.getEspletamentoGaraSoccorsiElencoOperatori(this.codice, this.codiceLotto, "6", null);
			
				EspletGaraViewDocAmmAction.updateSoccorsoIstruttorioList(this.elencoOperatori, soccorsi);
				
				int tipologiaGara = EspletGaraFasiAction.getTipologiaGara(this.session, this.codice);
				if(tipologiaGara == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
					 codiceGara = this.codice;
				}
				this.faseGara = this.bandiManager.getFaseGara(codiceGara);
				
				// recupera le info sul lotto selezionato
				this.lotto = null;
				if(this.lottiDistinti) { 
					this.lotto = EspletGaraViewOffEcoAction.getCodiceInternoLotto(this.codiceLotto, this.bandiManager, this.codice); 					
				}
				
				this.dettaglioGara = this.bandiManager.getDettaglioGara(codiceGara);
				nascondiValoriEspletamento = this.dettaglioGara.getDatiGeneraliGara().getNascondiValoriEspletamento();
				this.tipoOffertaTelematica = EspletGaraFasiAction.getTipoOffertaTelematica(this.session, codiceGara);
				
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

	
	/**
	 * recupera il codice interno del lotto  
	 * @throws ApsException 
	 */
	public static String getCodiceInternoLotto(String codiceLotto, IBandiManager bandiManager, String codiceGara) throws ApsException {
		String lotto = null;
		DettaglioGaraType dettGara = bandiManager.getDettaglioGara(codiceGara);
		if(dettGara.getLotto() != null) {
			for(int i = 0; i < dettGara.getLotto().length; i++) {
				if(codiceLotto.equals(dettGara.getLotto()[i].getCodiceLotto())) {
					lotto = dettGara.getLotto()[i].getCodiceInterno();
					break;
				}
			}
		}
		return lotto;
	}
	
}
