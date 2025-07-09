package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraLottoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewValTecLottiAction extends EncodedDataAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 3834840682004537635L;

	private IBandiManager bandiManager;	
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	private List<EspletGaraLottoType> lotti;
		
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public List<EspletGaraLottoType> getLotti() {
		return lotti;
	}

	public void setLotti(List<EspletGaraLottoType> lotti) {
		this.lotti = lotti;
	}

	/**
	 * ... 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				
				// estrai i lotti abilitati per la gara...
				List<EspletGaraLottoType> lotti = this.getLottiAbilitatiGara(dettGara);
				this.setLotti(lotti);
				
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
	 * recupera l'elenco dei lotti abilitati per la fase di valutazione tecnica 
	 */
	private List<EspletGaraLottoType> getLottiAbilitatiGara(DettaglioGaraType dettaglio) throws ApsException {
		List<EspletGaraLottoType> lotti = null;
		
		if(dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
		   dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
			// prepara la lista dei lotti abilitati per la gara...
			lotti = EspletGaraViewOffEcoLottiAction.getListaLottiAbilitati(
					BustaGara.BUSTA_TECNICA
					, dettaglio
					, getCurrentUser().getUsername()
			); 
		}
		
		return lotti;
	}
	
}
