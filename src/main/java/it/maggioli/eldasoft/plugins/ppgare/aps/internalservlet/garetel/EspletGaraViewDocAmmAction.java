package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

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
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewDocAmmAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2111781795777072131L;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
	private IComunicazioniManager comunicazionManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	private Long faseGara;
	private List<EspletGaraOperatoreType> elencoOperatori;
	private Boolean proceduraInversa;
		
	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setComunicazionManager(IComunicazioniManager comunicazionManager) {
		this.comunicazionManager = comunicazionManager;
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

	/**
	 * ...
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME) 
			&& EspletGaraFasiAction.isAperturaDocAmministrativaAbilitata(this.session, this.codice)) 
		{
			try {
				this.proceduraInversa = false;
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				if(dettGara != null) {
					this.proceduraInversa = dettGara.getDatiGeneraliGara().isProceduraInversa();
				}
				
				this.elencoOperatori = this.bandiManager
					.getEspletamentoGaraDocAmmElencoOperatori(this.codice, null);
				
				// NB: 
				//	per le gare plico unico offerta unica
				//  l'informazione del soccorso istruttorio puo' essere 
				//  nella riga della lotto fittizio o nella riga del lotto
				if(dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
					List<EspletGaraOperatoreType> soccorsi = this.bandiManager
						.getEspletamentoGaraSoccorsiElencoOperatori(this.codice, null, "2", null);
				
					EspletGaraViewDocAmmAction.updateSoccorsoIstruttorioList(this.elencoOperatori, soccorsi);					
				}

				this.faseGara = this.bandiManager.getFaseGara(this.codice);

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
	 * aggiorna lo stato del soccorso istruttorio per la lista degli operatori 
	 */
	public static void updateSoccorsoIstruttorioList(
			List<EspletGaraOperatoreType> elencoOperatori, 
			List<EspletGaraOperatoreType> soccorsi) 
	{
		// per ogni operatore verifica se esiste un soccorso istruttorio
		// per la gara o in un lotto...
		if(soccorsi != null && elencoOperatori != null) {
			for(EspletGaraOperatoreType oper: elencoOperatori) {
				for(EspletGaraOperatoreType soccorso: soccorsi) {
					if(oper.getCodiceOperatore().equals(soccorso.getCodiceOperatore())) {
						if("10".equals(soccorso.getCodiceAmmissione())) {
							// esiste un soccorso istruttorio per l'operatore e per la fase
							oper.setCodiceAmmissione(soccorso.getCodiceAmmissione());
						}
						break;
					}
				}	
			}		
		}
	}
	
}
