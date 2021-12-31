package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Action di gestione dell'apertura della pagina delle categorie d'iscrizione
 * del wizard d'iscrizione all'albo
 * 
 * @author Stefano.Sabbadin
 */
public class OpenPageCategorieAction extends AbstractOpenPageAction {

    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private IBandiManager bandiManager;
    
    private List<CategoriaBandoIscrizioneType> categorieBando;

    /**
     * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
     * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
     * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
     * valorizzato diversamente, vuol dire che si proviene dalla normale
     * navigazione tra le pagine del wizard
     */
    private String page;

    private String filtroCategorie;
    private String filtroCategorieNew;

    // dati che, se popolati, dipendono dal fatto che si vuole ricaricare la
    // pagina in seguito a dei controlli falliti
    private String[] codiceCategoria;
    private String[] catSelezionata;
    private String[] classeDa;
    private String[] classeA;
    private String[] nota;

    /** Array di appoggio per la pagina JSP per indicare lo stato dei checkbox */
    private boolean[] checkCategoria;
    
    
    public void setBandiManager(IBandiManager bandiManager) {
    	this.bandiManager = bandiManager;
    }

    public void setPage(String page) {
    	this.page = page;
    }

    public List<CategoriaBandoIscrizioneType> getCategorieBando() {
    	return categorieBando;
    }

    public void setCategorieBando(List<CategoriaBandoIscrizioneType> categorieBando) {
    	this.categorieBando = categorieBando;
    }
    
	public String getFiltroCategorie() {
		return filtroCategorie;
	}

	public void setFiltroCategorie(String filtroCategorie) {
		this.filtroCategorie = filtroCategorie;
	}
	
	public String getFiltroCategorieNew() {
		return filtroCategorieNew;
	}

	public void setFiltroCategorieNew(String filtroCategorieNew) {
		this.filtroCategorieNew = filtroCategorieNew;
	}

    public String[] getCodiceCategoria() {
    	return codiceCategoria;
    }

    public void setCodiceCategoria(String[] codiceCategoria) {
    	this.codiceCategoria = codiceCategoria;
    }

    public String[] getCatSelezionata() {
    	return catSelezionata;
    }

    public void setCatSelezionata(String[] catSelezionata) {
    	this.catSelezionata = catSelezionata;
    }

    public boolean[] getCheckCategoria() {
    	return checkCategoria;
    }

    public String[] getClasseDa() {
    	return classeDa;
    }

    public void setClasseDa(String[] classeDa) {
    	this.classeDa = classeDa;
    }

    public String[] getClasseA() {
    	return classeA;
    }

    public void setClasseA(String[] classeA) {
    	this.classeA = classeA;
    }
    
	public String[] getNota() {
		return nota;
	}

	public void setNota(String[] nota) {
		this.nota = nota;
	}
	
    public String getSTEP_IMPRESA() {
		return WizardIscrizioneHelper.STEP_IMPRESA;
	}

	public String getSTEP_DENOMINAZIONE_RTI() {
		return WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI;
	}

	public String getSTEP_DETTAGLI_RTI() {
		return WizardIscrizioneHelper.STEP_DETTAGLI_RTI;
	}

	public String getSTEP_SELEZIONE_CATEGORIE() {
		return WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE;
	}
	
	public String getSTEP_RIEPILOGO_CAGTEGORIE() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE;
	}
    
	public String getSTEP_SCARICA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE;
	}
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA() {
		return WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA;
	}
	
	public String getSTEP_PRESENTA_ISCRIZIONE() {
		return WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE;
	}

	/**
	 * ... 
	 */
	public String openPage() {
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
					PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_CATEGORIE);

			boolean processOK = false;
			try {
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(
								iscrizioneHelper.getIdBando(),
								StringUtils.stripToNull(this.filtroCategorie));
				if (categorie != null)
					this.setCategorieBando(Arrays.asList(categorie));
				else
					this.setCategorieBando(new ArrayList<CategoriaBandoIscrizioneType>());
				processOK = true;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}

			if (processOK) {
				// vanno ridefiniti gli array da assegnare effettivamente agli
				// attributi in modo da visualizzare la pagina in modo coerente
				// con la lista di categorie estratte (nell'eventualita' che
				// cambiassero da un momento all'altro)
				String[] tmpCodiceCategoria = new String[this.categorieBando.size()];
				boolean[] tmpCheckCategoria = new boolean[this.categorieBando.size()];
				String[] tmpClasseDa = new String[this.categorieBando.size()];
				String[] tmpClasseA = new String[this.categorieBando.size()];
				String[] tmpNota = new String[this.categorieBando.size()];

				if (PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_CATEGORIE
						.equals(this.page)) {
					// si arriva dalla pagina stessa, quindi se la si deve
					// ricaricare vuol dire che si e' verificato qualche errore
					// in fase di controllo e pertanto vanno ripresentati i dati
					// inseriti
					Set<String> setCategorieSelezionate = new HashSet<String>();
					if (this.catSelezionata != null)
						setCategorieSelezionate.addAll(Arrays
								.asList(this.catSelezionata));

					for (int i = 0; i < this.categorieBando.size(); i++) {
						tmpCodiceCategoria[i] = this.categorieBando.get(i).getCodice();
						tmpCheckCategoria[i] = setCategorieSelezionate.contains(tmpCodiceCategoria[i]);
						for (int j = 0; j < this.codiceCategoria.length; j++) {
							if (this.codiceCategoria[j].equals(tmpCodiceCategoria[i])) {
								tmpClasseDa[i] = this.classeDa[j];
								tmpClasseA[i] = this.classeA[j];
								tmpNota[i] = this.nota[j];
								break;
							}
						}
					}
				} else {
					// si arriva da un'altra pagina e pertanto si presentano i
					// dati leggendoli dalla sessione
					for (int i = 0; i < this.categorieBando.size(); i++) {
						tmpCodiceCategoria[i] = this.categorieBando.get(i).getCodice();
						tmpCheckCategoria[i] = iscrizioneHelper.getCategorie()
								.getCategorieSelezionate().contains(tmpCodiceCategoria[i]);
						tmpClasseDa[i] = iscrizioneHelper.getCategorie().getClasseDa().get(tmpCodiceCategoria[i]);
						tmpClasseA[i] = iscrizioneHelper.getCategorie().getClasseA().get(tmpCodiceCategoria[i]);
						tmpNota[i] = iscrizioneHelper.getCategorie()
						.getNota().get(tmpCodiceCategoria[i]);
					}
				}
				this.codiceCategoria = tmpCodiceCategoria;
				this.checkCategoria = tmpCheckCategoria;
				this.classeDa = tmpClasseDa;
				this.classeA = tmpClasseA;
				this.nota = tmpNota;
			}
			
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
							 WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE);
		}

		return this.getTarget();
	}

	public String openPageFiltrato() {
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
					PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_CATEGORIE);

			boolean processOK = false;
			try {
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(
								iscrizioneHelper.getIdBando(),
								StringUtils.stripToNull(this.filtroCategorieNew));
				if (categorie != null)
					this.setCategorieBando(Arrays.asList(categorie));
				else
					this.setCategorieBando(new ArrayList<CategoriaBandoIscrizioneType>());
				processOK = true;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}

			if (processOK) {
				// vanno ridefiniti gli array da assegnare effettivamente agli
				// attributi in modo da visualizzare la pagina in modo coerente
				// con la lista di categorie estratte (nell'eventualita' che
				// cambiassero da un momento all'altro)
				String[] tmpCodiceCategoria = new String[this.categorieBando.size()];
				boolean[] tmpCheckCategoria = new boolean[this.categorieBando.size()];
				String[] tmpClasseDa = new String[this.categorieBando.size()];
				String[] tmpClasseA = new String[this.categorieBando.size()];
				String[] tmpNota = new String[this.categorieBando.size()];

				// si arriva da un'altra pagina e pertanto si presentano i
				// dati leggendoli dalla sessione
				for (int i = 0; i < this.categorieBando.size(); i++) {
					tmpCodiceCategoria[i] = this.categorieBando.get(i).getCodice();
					tmpCheckCategoria[i] = iscrizioneHelper.getCategorie()
							.getCategorieSelezionate().contains(tmpCodiceCategoria[i]);
					tmpClasseDa[i] = iscrizioneHelper.getCategorie().getClasseDa().get(tmpCodiceCategoria[i]);
					tmpClasseA[i] = iscrizioneHelper.getCategorie().getClasseA().get(tmpCodiceCategoria[i]);
					tmpNota[i] = iscrizioneHelper.getCategorie().getNota().get(tmpCodiceCategoria[i]);
				}
				this.codiceCategoria = tmpCodiceCategoria;
				this.checkCategoria = tmpCheckCategoria;
				this.classeDa = tmpClasseDa;
				this.classeA = tmpClasseA;
				this.nota = tmpNota;
				this.filtroCategorie = this.filtroCategorieNew;
			}
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
							 WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE);
		}

		return this.getTarget();
	}
	
}
