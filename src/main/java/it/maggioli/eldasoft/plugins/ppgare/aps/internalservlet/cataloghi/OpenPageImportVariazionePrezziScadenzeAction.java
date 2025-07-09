package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;

/**
 * Action di gestione dell'apertura della pagina di importazione delle
 * variazioni ai prezzi e alle scadenze dei prodotti a catalogo tramite excel
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class OpenPageImportVariazionePrezziScadenzeAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3214120165136874894L;

	private IAppParamManager appParamManager;
	private ICataloghiManager cataloghiManager;
	@Validate(EParamValidation.CODICE)
	private String catalogo;

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}


	/**
	 * ... 
	 */
	@Override
	public String openPage() {

		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) this.session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}

		// se si proviene dall'EncodedDataAction di ProcessPageRiepilogo con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if (INPUT.equals(this.getTarget()) || "errorWS".equals(this.getTarget())) {
			this.setTarget(SUCCESS);
		}

		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
