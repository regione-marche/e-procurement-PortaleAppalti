package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Action per aprire la pagina di scelta della modifica prezzi e scadenze.
 *
 * @version 1.0
 * @author Marco.Perazzetta
 *
 */
public class VariazionePrezziScadenzeChoicesAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1692287388907778618L;

	private ICataloghiManager cataloghiManager;
	@Validate(EParamValidation.GENERIC)
	private String catalogo;
	private Map<String, Object> session;

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public String getCatalogo() {
		return catalogo;
	}

	/**
	 * Ricavo i dati utili alla generazione del carrello prodotti per consentire
	 * la gestione dell'export catalogo prodotti e caricamente documento
	 * variazione prezzi e scadenze
	 *
	 * @return il target a cui passare il flusso applicativo
	 */
	public String open() {

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

		if (null == this.getCurrentUser()
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
