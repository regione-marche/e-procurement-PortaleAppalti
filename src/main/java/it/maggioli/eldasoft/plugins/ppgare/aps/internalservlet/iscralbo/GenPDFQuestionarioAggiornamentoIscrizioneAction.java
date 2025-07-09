package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.HashMap;

/**
 * Consente la generazione del PDF per l'aggiornamento dell'iscrizione.
 * 
 * @author 
 * @since 
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
})
public class GenPDFQuestionarioAggiornamentoIscrizioneAction extends GenPDFQuestionarioIscrizioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 438069642907773904L;
	

	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			   "?actionPath=" + "/ExtStr2/do/FrontEnd/IscrAlbo/openQC.action" + 
			   "&currentFrame=" + this.currentFrame;
	}

	@Override
	public String createPdf() {
		// NB: in caso di report custom inserire qui l'inizializzazione di 
		// "reportName" e "xmlRootNode" relativi alla customizzazione...
		//this.reportName = "...";
		//this.xmlRootNode = "...";
		return super.createPdf();
	}
		
	@Override
	protected boolean reportInit() {
		return super.reportInit();
	}
	
	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		super.reportParametersInit(params);
	}

	@Override
	protected void reportCompleted() {
		if (SUCCESS.equals(this.getTarget())) {
			WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			if (helper != null) {
				// aggiungi il pdf generato all'elenco dei file dell'helper
				//helper.getDocumenti().getPdfGenerati().add(pdf);
			}
		}
	}

}
