package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.List;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard di
 * una offerta d'asta
 * 
 * @author ...
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.ASTA })
public class OpenPageRiepilogoAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6808673545103261384L;

	private List<String> tipoQualificaCodifica;
	private double ultimaOfferta; 
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	@Validate(EParamValidation.GENERIC)
	private String page;

	
	public List<String> getTipoQualificaCodifica() {
		return tipoQualificaCodifica;
	}

	public void setTipoQualificaCodifica(List<String> tipoQualificaCodifica) {
		this.tipoQualificaCodifica = tipoQualificaCodifica;
	}
	
	public double getUltimaOfferta() {
		return ultimaOfferta;
	}

	public void setUltimaOfferta(double ultimaOfferta) {
		this.ultimaOfferta = ultimaOfferta;
	}

    public void setPage(String page) {
    	this.page = page;
    }
	
    /**
     * ... 
     */
	public String openPage() {
		String target = SUCCESS;
		
		try {
			WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
			
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi...
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// la sessione è attiva...
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 		 WizardOffertaAstaHelper.STEP_RIEPILOGO);
				
				// recupera la decodifica dei tipi di firmatari...
				this.setTipoQualificaCodifica(
						helper.getOffertaEconomica().getImpresa().getTipoQualificaCodifica(
								helper.getOffertaEconomica().getListaFirmatariMandataria(),
								this.getMaps()));
				
				// imposta il valore dell'ultima offerta...
				this.setUltimaOfferta(
						helper.getAsta().getTipoOfferta() == 1
						? helper.getAsta().getRibassoUltimoRilancio()
						: helper.getAsta().getImportoUltimoRilancio()							
				);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
			
		return target;
	}
	
}
