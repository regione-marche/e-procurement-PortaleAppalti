package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaCatalogoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Action di gestione dell'apertura della pagina per la selezione degli articoli
 * nel mini wizard di generazione excel per inserimento massivo
 *
 * @author Marco.Perazzetta
 */
public class OpenPageSelezioneArticoliAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7585283479159496491L;

	private ICataloghiManager cataloghiManager;

	private List<CategoriaCatalogoType> categorie;

	@Validate(EParamValidation.CODICE)
	private String catalogo;

	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	@Validate(EParamValidation.GENERIC)
	private String page;
	@Validate(EParamValidation.DIGIT)
	private String[] articoliSelezionati;
	//private String[] codiceArticoli;
	private boolean[] checkArticoli;

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

	public String[] getArticoliSelezionati() {
		return articoliSelezionati;
	}

	public void setArticoloSelezionato(String[] articoliSelezionati) {
		this.setArticoliSelezionati(articoliSelezionati);
	}

	public void setArticoliSelezionati(String[] articoliSelezionati) {
		this.articoliSelezionati = articoliSelezionati;
	}

	public boolean[] getCheckArticoli() {
		return checkArticoli;
	}

	public void setCheckArticoli(boolean[] checkArticoli) {
		this.checkArticoli = checkArticoli;
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

		if (null == this.getCurrentUser() 
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_SELEZIONE_ARTICOLI);

			boolean processOK = false;
			try {
				List<CategoriaCatalogoType> listaCategorie = this.cataloghiManager.getCategorieArticoliOE(
						this.catalogo, 
						this.getCurrentUser().getUsername());

				if (listaCategorie != null) {
					this.setCategorie(listaCategorie);
				} else {
					this.setCategorie(new ArrayList<CategoriaCatalogoType>());
				}
				processOK = true;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}

			if (processOK) {
				//List<String> tmpIdArticoli = new ArrayList<String>();
				List<Boolean> tmpCheckArticoli = new ArrayList<Boolean>();
				Set<String> setArticoliSelezionati = new HashSet<String>();
				if (this.articoliSelezionati != null) {
					setArticoliSelezionati.addAll(Arrays.asList(this.articoliSelezionati));
					for (CategoriaCatalogoType categoria : this.categorie) {
						if (categoria.getArticoli() != null) {
							for (ArticoloType articolo : categoria.getArticoli()) {
								//tmpIdArticoli.add(articolo.getId() + "");
								tmpCheckArticoli.add(setArticoliSelezionati.contains(articolo.getId() + ""));
							}
						}
					}
				}
				if (!tmpCheckArticoli.isEmpty()) {
					this.checkArticoli = new boolean[tmpCheckArticoli.size()];
					for (int i = 0; i < tmpCheckArticoli.size(); i++) {
						this.checkArticoli[i] = tmpCheckArticoli.get(i);
					}
				}
			}
		}
		return this.getTarget();
	}
	
}
