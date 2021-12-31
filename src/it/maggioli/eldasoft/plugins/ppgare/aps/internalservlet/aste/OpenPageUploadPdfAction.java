package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di apertura della pagina di gestione ...
 *
 * @author ...
 */
public class OpenPageUploadPdfAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	private IAppParamManager appParamManager;
	
	private int dimensioneAttualeFileCaricati;
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	private String page;

	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
    public void setPage(String page) {
    	this.page = page;
    }
    
	public int getDimensioneAttualeFileCaricati() {
		return dimensioneAttualeFileCaricati;
	}

	public void setDimensioneAttualeFileCaricati(int dimensioneAttualeFileCaricati) {
		this.dimensioneAttualeFileCaricati = dimensioneAttualeFileCaricati;
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}
	
	public Integer getLimiteTotaleUpload() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
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
				
				// si aggiorna il totale dei file finora caricati
				DocumentiComunicazioneHelper documenti = helper.getDocumenti();
				if(documenti.getDocUlterioriDesc().size() > 0) {				
					for (Integer s : documenti.getDocUlterioriSize()) {
						this.dimensioneAttualeFileCaricati += s;
					}
				}
				
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 		 WizardOffertaAstaHelper.STEP_UPLOAD_PDF);				
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return target;
	}
	
}
