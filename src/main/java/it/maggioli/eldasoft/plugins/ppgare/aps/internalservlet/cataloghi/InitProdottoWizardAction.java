package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Action per le operazioni sui cataloghi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version 1.0
 * @author Stefano.Sabbadin
 *
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class InitProdottoWizardAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1695787306909078618L;

	private ICataloghiManager cataloghiManager;
	@Validate(EParamValidation.CODICE)
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
	public String initWizard() {

		// verifica il profilo di accesso ed esegui un LOCK alla funzione 
		if( !lockAccessoFunzione(EFlussiAccessiDistinti.PRODOTTI, this.getCatalogo()) ) {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			return this.getTarget();
		}

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.catalogo, 
						this.cataloghiManager);
				WizardProdottoHelper prodottoHelper = WizardProdottoHelper.getInstance(
						this.session, 
						this.cataloghiManager, 
						carrelloProdotti,
						this.getCurrentUser().getUsername(), 
						carrelloProdotti.getCurrentCatalogo(), 
						null, 
						null, 
						null, 
						null, 
						null, 
						CataloghiConstants.PAGINA_GESTIONE_PRODOTTI);
				session.put(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO, prodottoHelper);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "initWizard");
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
