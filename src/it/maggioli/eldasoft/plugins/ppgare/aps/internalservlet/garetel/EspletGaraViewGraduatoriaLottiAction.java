package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraLottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewGraduatoriaLottiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private IBandiManager bandiManager;	
	
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
								
				List<EspletGaraLottoType> lotti = new ArrayList<EspletGaraLottoType>(); 
				
				for(int i = 0; i < dettGara.getLotto().length; i++) {
					if(dettGara.getLotto(i).getCodiceInterno() != null) {
						EspletGaraLottoType lotto = new EspletGaraLottoType();
						lotto.setLotto(dettGara.getLotto(i).getCodiceLotto());
						lotto.setCodiceInterno(dettGara.getLotto(i).getCodiceInterno());
						lotto.setOggetto(dettGara.getLotto(i).getOggetto());					
						lotti.add(lotto);
					}
				}
				
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

}
