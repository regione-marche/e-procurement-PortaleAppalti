package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;

import java.util.List;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaCatalogoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import java.util.ArrayList;

/**
 * Action di gestione dell'apertura della pagina per la selezione dell'articolo
 * nel wizard di inserimento di un nuovo prodotto a catalogo
 *
 * @author Marco.Perazzetta
 */
public class OpenPageSelezioneArticoloAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private ICataloghiManager cataloghiManager;

	private List<CategoriaCatalogoType> categorie;

	/**
	 * Recupero l'ultima categoria del catalogo in cui c'e' stata una modifica
	 */
	private String ultimaCategoria = null;

	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	private String page;

	// dati che, se popolati, dipendono dal fatto che si vuole ricaricare la
	// pagina in seguito a dei controlli falliti
	private Long articoloSelezionato;


	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<CategoriaCatalogoType> getCategorie() {
		return categorie;
	}

	public void setCategorie(List<CategoriaCatalogoType> categorie) {
		this.categorie = categorie;
	}

	public String getPage() {
		return page;
	}

	public Long getArticoloSelezionato() {
		return articoloSelezionato;
	}

	public void setArticoloSelezionato(Long articoloSelezionato) {
		this.articoloSelezionato = articoloSelezionato;
	}

	public String getUltimaCategoria() {
		return ultimaCategoria;
	}

	public void setUltimaCategoria(String ultimaCategoria) {
		this.ultimaCategoria = ultimaCategoria;
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {

		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);

		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_SELEZIONE_ARTICOLO);

			boolean processOK = false;
			try {
				List<CategoriaCatalogoType> listaCategorie = this.cataloghiManager.getCategorieArticoliOE(
						prodottoHelper.getCodiceCatalogo(), 
						this.getCurrentUser().getUsername());

				if (listaCategorie != null) {
					this.setCategorie(listaCategorie);
				} else {
					this.setCategorie(new ArrayList<CategoriaCatalogoType>());
				}
				CarrelloProdottiSessione carrello = CarrelloProdottiSessione.getInstance(
						this.session, 
						prodottoHelper.getCodiceCatalogo(), 
						this.cataloghiManager);
				this.setUltimaCategoria(carrello.getListaProdottiPerCatalogo().get(prodottoHelper.getCodiceCatalogo()).getUltimaCategoria());
				processOK = true;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			
			if ((processOK) && (!PortGareSystemConstants.WIZARD_PAGINA_SELEZIONE_ARTICOLO.equals(this.page))) {
				this.setArticoloSelezionato(prodottoHelper.getArticolo().getIdArticolo());
			}
		}
		return this.getTarget();
	}
	
}
