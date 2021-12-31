package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
 * Action per le operazioni sui cataloghi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version 1.0
 * @author Stefano.Sabbadin
 *
 */
public class InsertProdottoChoicesAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1695787306909078618L;

	private ICataloghiManager cataloghiManager;
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
	 * la gestione del wizard di inserimento del prodotto e memorizzo tali dati in
	 * sessione.
	 *
	 * @return il target a cui passare il flusso applicativo
	 */
	public String open() {
		if (null == this.getCurrentUser() 
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}