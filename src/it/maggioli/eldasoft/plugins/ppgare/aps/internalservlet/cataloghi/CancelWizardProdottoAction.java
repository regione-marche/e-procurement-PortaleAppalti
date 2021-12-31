package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di gestione dell'annullamento di un'iscrizione
 *
 * @author Stefano.Sabbadin
 */
public class CancelWizardProdottoAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527227479159496491L;

	private Map<String, Object> session;

	private String catalogo;
	private String prodotto;
	private long prodottoId;
	private boolean inCarrello;
	private String statoProdotto;
	private long articoloId;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public String getTAB_PRODOTTI_NEL_CARRELLO() {
		return CataloghiConstants.TAB_PRODOTTI_NEL_CARRELLO;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public String getProdotto() {
		return prodotto;
	}

	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}

	public long getProdottoId() {
		return prodottoId;
	}

	public void setProdottoId(long prodottoId) {
		this.prodottoId = prodottoId;
	}

	public boolean isInCarrello() {
		return inCarrello;
	}

	public void setInCarrello(boolean inCarrello) {
		this.inCarrello = inCarrello;
	}

	public String getStatoProdotto() {
		return statoProdotto;
	}

	public void setStatoProdotto(String statoProdotto) {
		this.statoProdotto = statoProdotto;
	}

	public long getArticoloId() {
		return articoloId;
	}

	public void setArticoloId(long articoloId) {
		this.articoloId = articoloId;
	}
	
	public String getPAGINA_DETTAGLIO_ARTICOLO() {
		return CataloghiConstants.PAGINA_DETTAGLIO_ARTICOLO;
	}

	/**
	 * ...
	 */
	public String question() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (prodottoHelper.isAggiornamento()) {
				target += "CancAggProdotto";
				this.setProdotto(prodottoHelper.getDettaglioProdotto().getCodiceProdottoProduttore());
				this.setProdottoId(prodottoHelper.isInCarrello() ? prodottoHelper.getIndex() : prodottoHelper.getDettaglioProdotto().getId());
				this.setInCarrello(prodottoHelper.isInCarrello());
			}
		}
		return target;
	}

	/**
	 * ...
	 */
	public String cancel() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			/*
			if (prodottoHelper.isAggiornamento()) {
				target = "successProdotto";
				this.setProdottoId(prodottoHelper.isInCarrello() ? prodottoHelper.getIndex() : prodottoHelper.getDettaglioProdotto().getId());
				this.setInCarrello(prodottoHelper.isInCarrello());
				this.setStatoProdotto(prodottoHelper.getStatoProdotto());
			} else if (CataloghiConstants.PAGINA_DETTAGLIO_ARTICOLO.equals(prodottoHelper.getWizardSourcePage())) {
				target = "successArticolo";
				this.setArticoloId(prodottoHelper.getArticolo().getIdArticolo());
			}
			*/
			this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
			this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		}
		return target;
	}
	
}
