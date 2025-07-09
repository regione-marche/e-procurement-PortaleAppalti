package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class OpenPageNuovaRettificaAction extends OpenPageNuovaComunicazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4023420473435492090L;

	private IBandiManager bandiManager;
	
	private DettaglioGaraType gara;
	private Boolean garaLotti;	
	private List<String> lotti;	
	@Validate(EParamValidation.GENERIC)
	private String tipoBusta;
	@Validate(EParamValidation.CODICE)
	private String lotto;	
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}	
	
	public DettaglioGaraType getGara() {
		return gara;
	}

	public Boolean getGaraLotti() {
		return garaLotti;
	}

	public void setGaraLotti(Boolean garaLotti) {
		this.garaLotti = garaLotti;
	}
	
	public List<String> getLotti() {
		return lotti;
	}

	public String getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(String tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getLotto() {
		return lotto;
	}

	public void setLotto(String lotto) {
		this.lotto = lotto;
	}
	
	
	/**
	 * costruttore
	 */
	public OpenPageNuovaRettificaAction() {		
		super(new WizardRettificaHelper());
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		String target = SUCCESS;
		try {
			helper = (WizardNuovaComunicazioneHelper) getWizardFromSession();
			
			if(helper != null) {
	            // recupera i dati della gara
	         	gara = bandiManager.getDettaglioGara(helper.getCodice());
				         	
	            garaLotti = (gara != null && gara.getLotto() != null 
	            		? gara.getLotto().length > 1 
	            		: false
	            );
	            
	            lotti = new ArrayList<String>();
	            if(garaLotti) {
	            	for(int i = 1; i < gara.getLotto().length; i++) 
	            		lotti.add(gara.getLotto()[i].getCodiceLotto());
	            }
	
				this.setOggetto(helper.getOggetto());
				this.setTesto(helper.getTesto());			
				this.setTipoBusta(longToString(helper.getTipoBusta()));
				this.setLotto(helper.getCodice2());
			}
	
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, helper.STEP_TESTO_COMUNICAZIONE);
			
		} catch (ApsException ex) {
			ApsSystemUtils.logThrowable(ex, this, "openPage");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return target;
	}
	
	/**
	 * ...
	 */
	private static String longToString(Long value) {
		return (value != null ? value.toString() : null);
	}

//	/**
//	 * ... 
//	 */
//	@Override
//	public String openPageAfterError() {
//		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardSoccorsoIstruttorioHelper.STEP_TESTO_COMUNICAZIONE);
//		return this.getTarget();
//	}
	
}
