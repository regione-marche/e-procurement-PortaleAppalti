package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AdempimentoAnticorruzioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeAnticorruzioneManager;

public class AnticorruzioneListaAdempimentiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 643349001078627279L;
	
	private ILeggeAnticorruzioneManager leggeAnticorruzioneManager;
	
	List<AdempimentoAnticorruzioneType> listaAdempimenti = null;
	List<String> listaAnni = null;

	public void setLeggeAnticorruzioneManager(ILeggeAnticorruzioneManager leggeAnticorruzioneManager) {
		this.leggeAnticorruzioneManager = leggeAnticorruzioneManager;
	}
	
	public List<AdempimentoAnticorruzioneType> getListaAdempimenti(){
		return this.listaAdempimenti;
	}
	
	public List<String> getListaAnni(){
		return this.listaAnni;
	}
	
	/**
	 * ...
	 */
	public String view() {
		this.setTarget(SUCCESS);
		try {
			 listaAdempimenti = this.leggeAnticorruzioneManager.getAdempimentiAnticorruzione(null, null);
			 
			 listaAnni = new ArrayList<String>();
			 if(listaAdempimenti != null) {
				 for(int i = 0; i < listaAdempimenti.size(); i++) {
					 String annoRiferimento = (new Integer (listaAdempimenti.get(i).getAnnoRiferimento()).toString()); 
					 if(!listaAnni.contains(annoRiferimento)) {
						 listaAnni.add(annoRiferimento);
					 }
				 }
			 }
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "view");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

}
