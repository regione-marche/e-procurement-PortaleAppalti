package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Action di apertura della pagina di gestione ...
 *
 * @author ...
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.ASTA })
public class OpenPageUploadPdfAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1131733542666426889L;
	
	private IAppParamManager appParamManager;
	
	private int dimensioneAttualeFileCaricati;
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	@Validate(EParamValidation.GENERIC)
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

//	public Integer getLimiteUploadFile() {
//		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
//	}
//	
//	public Integer getLimiteTotaleUpload() {
//		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
//	}

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
				// la sessione e' attiva...
				getUploadValidator().setHelper(helper);
				
				// si aggiorna il totale dei file finora caricati
				WizardDocumentiBustaHelper documenti = helper.getDocumenti();
//				if (CollectionUtils.isNotEmpty(documenti.getAdditionalDocs()))
//					dimensioneAttualeFileCaricati += Attachment.sumSize(documenti.getAdditionalDocs());				
				dimensioneAttualeFileCaricati = documenti.getTotalSize();

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
