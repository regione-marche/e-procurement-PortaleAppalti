package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IEncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Action di gestione delle operazioni nella pagina delle categorie del wizard
 * d'iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageCategorieAction extends AbstractProcessPageAction 
	implements IEncodedDataAction 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;
	
	private Map<String, LinkedHashMap<String, String>> maps = new HashMap<String, LinkedHashMap<String, String>>();
	private String target = SUCCESS;

	private IBandiManager bandiManager;

	private String[] codiceCategoria;
	private String[] catSelezionata;
	private String[] classeDa;
	private String[] classeA;
	private String[] nota;

//	private boolean[] obblAClassifica;		// 12/07/2019: se una classifica è visibile, allora è sempre obbligatoria!
	private boolean[] obblNota;

	private Set<String> catErrata = new HashSet<String>();

	private String filtroCategorie;
	private String filtroCategorieNew;
	
	private String requisitiCoordinatoreSicurezza;		// "Possesso requisiti Coordinatore della Sicurezza - art. 98 D.Lgs. 81/2008" 
	
	/** Memorizza la prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;

	@Override
	public Map<String, LinkedHashMap<String, String>> getMaps() {
		return maps;
	}
		
	public void setTarget(String target) {
		this.target = target;
	}
	
 	@Override
	public String getTarget() {
		return target;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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

//	/**
//	 * @return the obblAClassifica
//	 */
//	public boolean[] isObblAClassifica() {
//		return obblAClassifica;
//	}
//
//	/**
//	 * @param obblAClassifica the obblAClassifica to set
//	 */
//	public void setObblAClassifica(boolean[] obblAClassifica) {
//		this.obblAClassifica = obblAClassifica;
//	}
	
	public boolean[] getObblNota() {
		return obblNota;
	}

	public void setObblNota(boolean[] obblNota) {
		this.obblNota = obblNota;
	}

	public Set<String> getCatErrata() {
		return catErrata;
	}

	public void setFiltroCategorie(String filtroCategorie) {
		this.filtroCategorie = filtroCategorie;
	}

	public void setFiltroCategorieNew(String filtroCategorieNew) {
		this.filtroCategorieNew = filtroCategorieNew;
	}
		
	public String getRequisitiCoordinatoreSicurezza() {
		return requisitiCoordinatoreSicurezza;
	}

	public void setRequisitiCoordinatoreSicurezza(String requisitiCoordinatoreSicurezza) {
		this.requisitiCoordinatoreSicurezza = requisitiCoordinatoreSicurezza;
	}

	/**
	 * Validazione
	 */
	public void validate() {
		super.validate();

		String daClassifica = this.getI18nLabel("LABEL_DA_CLASSIFICA");
		String aClassifica = this.getI18nLabel("LABEL_A_CLASSIFICA");

		try {
			WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			
			if (iscrizioneHelper != null) {
				
				int tipoClassifica = (iscrizioneHelper.getTipoClassifica() != null 
									  ? iscrizioneHelper.getTipoClassifica()
									  : -1);
				if (tipoClassifica == PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO) {
					aClassifica = this.getI18nLabel("LABEL_CLASSIFICA");
				}
	
				// il controllo lo si effettua solo se la sessione e' ancora valida
				// e si tratta di iscrizione, tanto poi nel metodo richiamato
				// dall'azione si effettua il controllo della sessione e l'eventuale
				// forward corretto
				// si crea l'insieme delle categorie selezionate
				Set<String> setCategorieSelezionate = new HashSet<String>();
				if (this.catSelezionata != null) {
					setCategorieSelezionate.addAll(Arrays.asList(this.catSelezionata));
				}
								
				int classifFornitureCount = this.maps.get(InterceptorEncodedData.LISTA_CLASSIFICAZIONE_FORNITURE).size();
				int classifServiziCount = this.maps.get(InterceptorEncodedData.LISTA_CLASSIFICAZIONE_SERVIZI).size();
				int classifLavoriSottoSogliaCount = this.maps.get(InterceptorEncodedData.LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA).size();
				int classifServiziProfCount = this.maps.get(InterceptorEncodedData.LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI).size();
				
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(iscrizioneHelper.getIdBando(), null);
	
				// si controlla che le classificazioni impostate siano relative
				// esclusivamente a categorie selezionate
				if (this.codiceCategoria != null) {
					for (int i = 0; i < this.codiceCategoria.length; i++) {
						if (!setCategorieSelezionate.contains(this.codiceCategoria[i])
							&& this.classeDa[i].length() > 0) {
							this.addActionError(this.getText(
									"Errors.categoriaDaClassificaSet",
									new String[]{daClassifica,this.codiceCategoria[i]}));
							this.catErrata.add(this.codiceCategoria[i]);
						}
						if (!setCategorieSelezionate.contains(this.codiceCategoria[i])
							&& this.classeA[i].length() > 0) {
							this.addActionError(this.getText(
									"Errors.categoriaAClassificaSet",
									new String[]{aClassifica,this.codiceCategoria[i]}));
							this.catErrata.add(this.codiceCategoria[i]);
						}
						if (!setCategorieSelezionate.contains(this.codiceCategoria[i])
							&& this.nota[i].length() > 0) {
							this.addActionError(this.getText(
									"Errors.categoriaNotaSet",
									new String[] { this.codiceCategoria[i] }));
							this.catErrata.add(this.codiceCategoria[i]);
						}
						if (setCategorieSelezionate.contains(this.codiceCategoria[i])
							&& this.classeDa[i].length() > 0
							&& this.classeA[i].length() > 0
							&& Integer.parseInt(this.classeDa[i]) > Integer.parseInt(this.classeA[i])) {
							this.addActionError(this.getText(
									"Errors.categoriaDaClassificaSupAClassificaSet",
									new String[]{daClassifica,
												 aClassifica,
												 this.codiceCategoria[i]}));
							this.catErrata.add(this.codiceCategoria[i]);
						}
						
						// 12/07/2019: se la classifica è visibile, allora è sempre obbligatoria!
						if (setCategorieSelezionate.contains(this.codiceCategoria[i])) {
							
							boolean daClassificaVisibile = false;
							boolean aClassificaVisibile = false;
							if(categorie != null) {
								for(int j = 0; j < categorie.length; j++) {
									if(categorie[j].getCodice().equalsIgnoreCase(this.codiceCategoria[i])) {
										// vedi condizioni per la visualizzazione delle classifiche in "iscralbo\stepCategorie.jsp"
										boolean tipoAppaltoValido = 
											("1".equals(categorie[j].getTipoAppalto())) || 
											("2".equals(categorie[j].getTipoAppalto()) && classifFornitureCount > 0) ||
											("3".equals(categorie[j].getTipoAppalto()) && classifServiziCount > 0) ||
											("4".equals(categorie[j].getTipoAppalto()) && classifLavoriSottoSogliaCount > 0) ||
											("5".equals(categorie[j].getTipoAppalto()) && classifServiziProfCount > 0);
										daClassificaVisibile = (tipoClassifica == PortGareSystemConstants.TIPO_CLASSIFICA_INTERVALLO) 
															   && tipoAppaltoValido;
										aClassificaVisibile = (tipoClassifica == PortGareSystemConstants.TIPO_CLASSIFICA_INTERVALLO || 
											       			   tipoClassifica == PortGareSystemConstants.TIPO_CLASSIFICA_MASSIMA) 
											       			  && tipoAppaltoValido;
									}
								}
							}
							
							if(daClassificaVisibile && this.classeDa[i].length() == 0) {
								this.addActionError(this.getText(
										"Errors.categoriaAClassificaNotSet",
										new String[]{daClassifica, 
												     this.codiceCategoria[i]}));
								this.catErrata.add(this.codiceCategoria[i]);
							}
							
							if(aClassificaVisibile && this.classeA[i].length() == 0) {
								this.addActionError(this.getText(
										"Errors.categoriaAClassificaNotSet",
										new String[]{aClassifica, 
												     this.codiceCategoria[i]}));
								this.catErrata.add(this.codiceCategoria[i]);
							}
						}
						
						if (setCategorieSelezionate.contains(this.codiceCategoria[i])
							&& this.obblNota[i]
							&& this.nota[i].length() == 0) {
							this.addActionError(this.getText(
									"Errors.categoriaNotaNotSet",
									new String[]{this.codiceCategoria[i]}));
							this.catErrata.add(this.codiceCategoria[i]);
						}
					}
				}
			}
	
			// se è visibile la richiesta del coordinatore di sicurezza
			// il campo è obbligatorio!!!
			if (iscrizioneHelper.isRichiestaCoordinatoreSicurezza() &&
				StringUtils.isEmpty(this.requisitiCoordinatoreSicurezza)) {
				this.addActionError(this.getText("Errors.requisitiCoordinatoreSicurezzaNotSet"));
			}
			
			if (this.getActionErrors().size() > 0
				&& !this.filtroCategorieNew.equals(this.filtroCategorie)) {
				this.addActionError(this.getText("Errors.filtroNotSet"));
			}
			
		} catch(Exception ex) {
			this.addActionError(this.getText("Errors.sessionExpired"));
			ApsSystemUtils.getLogger().error("ProcessPageCategorie.validate() " + ex.toString());
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "validate");
			ExceptionUtils.manageExceptionError(e, this);
			//target = CommonSystemConstants.PORTAL_ERROR;
		}
	}

	/**
	 * Next
	 */
	public String next() {
		String target = SUCCESS;

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;			
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			// se necessario si imposta "possesso requisiti coordinatore di sicurezza"
			// se null => non scrivere il tag nel doc XML
			iscrizioneHelper.setRequisitiCoordinatoreSicurezza(null);
			if(iscrizioneHelper.isRichiestaCoordinatoreSicurezza()) {
				if(StringUtils.isNotEmpty(this.requisitiCoordinatoreSicurezza)) {
					iscrizioneHelper.setRequisitiCoordinatoreSicurezza("1".equals(this.requisitiCoordinatoreSicurezza));
				}
			}
			
			// si crea l'insieme delle categorie selezionate
			Set<String> setCategorieSelezionate = new HashSet<String>();
			if (this.catSelezionata != null) {
				setCategorieSelezionate.addAll(Arrays.asList(this.catSelezionata));
			}

			// si controlla che ci siano categorie sia per l'iscrizione che ora
			// anche per l'aggiornamento (fino alla 1.2.0 il controllo andava
			// solo sull'iscrizione)
			if (setCategorieSelezionate.size() == 0) {
				// si controlla, nel caso non ci siano categorie selezionate,
				// che ci siano almeno in sessione escludendo quelle gestite
				// nella pagina (visto che nessuna risulta selezionata), questo
				// perche' si puo' applicare un filtro e non devo bloccare se
				// con il filtro non ho dati selezionabili
				@SuppressWarnings("unchecked")
				Set<String> setCategorieSelezionateInPrecedenza = (Set<String>) iscrizioneHelper.getCategorie().getCategorieSelezionate().clone();
				Set<String> categorieGestite = new HashSet<String>();
				if (this.codiceCategoria != null) {
					categorieGestite.addAll(Arrays.asList(this.codiceCategoria));
				}
				for (String catGestita : categorieGestite) {
					if (setCategorieSelezionateInPrecedenza.contains(catGestita)) {
						setCategorieSelezionateInPrecedenza.remove(catGestita);
					}
				}
				if (setCategorieSelezionateInPrecedenza.size() == 0) {
					this.addActionError(this.getText("Errors.categoriaNotSet"));
					target = INPUT;
				}
			}

			// solo se i controlli sono andati a buon fine ed ho categorie nella
			// pagina aggiorno i dati in sessione
			if (SUCCESS.equals(target)) {
				target = this.update(iscrizioneHelper);
			}
			
			this.nextResultAction = iscrizioneHelper.getNextAction(WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE);
		}

		return target;
	}

	/**
	 * ...
	 */
	private String update(WizardIscrizioneHelper iscrizioneHelper) {
		String target = SUCCESS;

		if (this.codiceCategoria != null) {
			// eseguo solo se non arrivo da una pagina con filtro e senza
			// categorie: in tal caso non posso aggiornare nulla

			// si crea l'insieme delle categorie selezionate
			Set<String> partialSetCategorieSelezionate = new HashSet<String>();
			if (this.catSelezionata != null) {
				partialSetCategorieSelezionate.addAll(Arrays.asList(this.catSelezionata));
			}

			// si aggiornano i dati in sessione, in particolare le classifiche
			for (int i = 0; i < this.codiceCategoria.length; i++) {
				if (partialSetCategorieSelezionate.contains(this.codiceCategoria[i])) {
					// ho selezionato la categoria, per cui aggiorno le classifiche
					iscrizioneHelper.getCategorie().getClasseDa().put(this.codiceCategoria[i], StringUtils.stripToNull(this.classeDa[i]));
					iscrizioneHelper.getCategorie().getClasseA().put(this.codiceCategoria[i], StringUtils.stripToNull(this.classeA[i]));
					iscrizioneHelper.getCategorie().getNota().put(this.codiceCategoria[i], StringUtils.stripToNull(this.nota[i]));
				} else {
					// la categoria non e' selezionata, potrebbe esserlo gia' da prima oppure l'ho appena deselezionata
					iscrizioneHelper.getCategorie().getClasseDa().remove(this.codiceCategoria[i]);
					iscrizioneHelper.getCategorie().getClasseA().remove(this.codiceCategoria[i]);
					iscrizioneHelper.getCategorie().getNota().remove(this.codiceCategoria[i]);
				}
			}

			// si aggiornano i dati in sessione, in particolare le categorie ordinandole e fondendole
			try {
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
					.getElencoCategorieBandoIscrizione(iscrizioneHelper.getIdBando(), null);
				Set<String> setCategorieFoglia = new HashSet<String>();
				for (CategoriaBandoIscrizioneType cat : categorie) {
					if (cat.isFoglia()) {
						setCategorieFoglia.add(cat.getCodice());
					}
				}

				Set<String> categorieGestite = new HashSet<String>();
				categorieGestite.addAll(Arrays.asList(this.codiceCategoria));

				// si costruisce un insieme con tutte le categorie selezionate (nell'albero visualizzato e non)
				Set<String> setNodiSelezionati = new HashSet<String>();
				for (CategoriaBandoIscrizioneType cat : categorie) {
					if (partialSetCategorieSelezionate.contains(cat.getCodice())
						|| (!categorieGestite.contains(cat.getCodice()) 
							&& iscrizioneHelper.getCategorie().getCategorieSelezionate().contains(cat.getCodice()))) {
						// va settata se presente nelle categorie selezionate
						// nella pagina (eventualmente filtrata) oppure nelle
						// categorie presenti in sessione ma non gestite nella
						// pagina
						setNodiSelezionati.add(cat.getCodice());
						if (StringUtils.isNotBlank(cat.getCodiceLivello1())) {
							setNodiSelezionati.add(cat.getCodiceLivello1());
						}
						if (StringUtils.isNotBlank(cat.getCodiceLivello2())) {
							setNodiSelezionati.add(cat.getCodiceLivello2());
						}
						if (StringUtils.isNotBlank(cat.getCodiceLivello3())) {
							setNodiSelezionati.add(cat.getCodiceLivello3());
						}
						if (StringUtils.isNotBlank(cat.getCodiceLivello4())) {
							setNodiSelezionati.add(cat.getCodiceLivello4());
						}
					}
				}
				// si costruisce l'elenco delle categorie selezionate includendo
				// anche i nodi fino alle foglie selezionate, e l'elenco delle
				// foglie aggiornate
				LinkedHashSet<String> setCategorieSelezionate = new LinkedHashSet<String>();
				HashSet<String> setFoglie = new HashSet<String>();
				for (CategoriaBandoIscrizioneType cat : categorie) {
					if (setNodiSelezionati.contains(cat.getCodice())) {
						setCategorieSelezionate.add(cat.getCodice());
						if (cat.isFoglia()) {
							setFoglie.add(cat.getCodice());
						}
					}
				}
				iscrizioneHelper.getCategorie().setCategorieSelezionate(setCategorieSelezionate);
				iscrizioneHelper.getCategorie().setFoglie(setFoglie);

			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "next");
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}

		return target;
	}

	/**
	 * Back 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		
		// nel caso di unica stazione appaltante si deve saltare la pagina di
		// selezione della SA e passare direttamente alla precedente
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if(!iscrizioneHelper.isRti() && !iscrizioneHelper.getImpresa().isConsorzio()){
				iscrizioneHelper.getStepNavigazione().remove(WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
			}
			
			this.nextResultAction = iscrizioneHelper.getPreviousAction(WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE);
			
			if((!iscrizioneHelper.getComponentiRTI().isEmpty() && iscrizioneHelper.isRti()) 
				|| (iscrizioneHelper.isRti()&&!iscrizioneHelper.getComponentiRTI().isEmpty()) 
				|| (iscrizioneHelper.getImpresa().isConsorzio())) {
				this.nextResultAction = this.nextResultAction + "Clear"; 
			}
		}
		
		return target;
	}

	/**
	 * Filter 
	 */
	public String filter() {
		String target = SUCCESS;

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			// la sessione non e' scaduta, per cui proseguo regolarmente
		} else {
			target = this.update(iscrizioneHelper);
			if (SUCCESS.equals(target)) {
				// se i dati sono stati controllati ed aggiornati con successo,
				// si procede a riaprire la pagina stessa con il nuovo filtro
				// impostato
				target = "filter";
			}
		}
		
		return target;
	}

}
