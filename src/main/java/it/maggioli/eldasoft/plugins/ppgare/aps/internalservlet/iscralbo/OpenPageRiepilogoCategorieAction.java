package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CatalogResultBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Action di gestione dell'apertura della pagina con il riepilogo delle
 * categorie d'iscrizione selezionate nel wizard.
 * 
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageRiepilogoCategorieAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2424542969592935831L;

	private IBandiManager bandiManager;

	private List<CategoriaBandoIscrizioneType> categorieBando;

	@Validate(EParamValidation.CODICE_CATEGORIA)
	private String[] codiceCategoria;
	@Validate(EParamValidation.CLASSIFICAZIONI)
	private String[] classeDa;
	@Validate(EParamValidation.CLASSIFICAZIONI)
	private String[] classeA;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String[] nota;

	//Utilizzato dal ...
	private List<CatalogResultBean> processedResult;


	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public List<CategoriaBandoIscrizioneType> getCategorieBando() {
		return categorieBando;
	}

	public void setCategorieBando(
			List<CategoriaBandoIscrizioneType> categorieBando) {
		this.categorieBando = categorieBando;
	}

	public String[] getCodiceCategoria() {
		return codiceCategoria;
	}

	public void setCodiceCategoria(String[] codiceCategoria) {
		this.codiceCategoria = codiceCategoria;
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
	
	public String getSTEP_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_QUESTIONARIO;
	}
	
	public String getSTEP_RIEPILOGO_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO;
	}

	public List<CatalogResultBean> getProcessedResult() {
		return processedResult;
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
							 PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_RIEPILOGO_CATEGORIE);

			try {
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(iscrizioneHelper.getIdBando(), null);

				if (categorie != null) {
					// si arriva da un'altra pagina e pertanto si presentano i
					// dati leggendoli dalla sessione
					processedResult = helperToResult(iscrizioneHelper, categorie);
				} else
					processedResult = new ArrayList<>();

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
							 WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE);
		}

		return this.getTarget();
	}

	private List<CatalogResultBean> helperToResult(WizardIscrizioneHelper helper, CategoriaBandoIscrizioneType[] categorie) {
		return helper == null
			   ? new ArrayList<>()
			   : Arrays.stream(categorie)
					   .filter(Objects::nonNull)
					   .filter(it -> helper.getCategorie() != null)
					   .filter(cat -> helper.getCategorie().getCategorieSelezionate().contains(cat.getCodice()))
					   .map(cat -> helperToResult(helper, cat))
				   .collect(Collectors.toList());
	}

	private CatalogResultBean helperToResult(WizardIscrizioneHelper helper, CategoriaBandoIscrizioneType categoria) {
		return CatalogResultBean.CatalogResultBeanBuilder.newCatalogResultBuilder()
				.withCategory(categoria)
				.withCodice(categoria.getCodice())
				.withClassFrom(helper.getCategorie().getClasseDa().get(categoria.getCodice()))
				.withClassTo(helper.getCategorie().getClasseA().get(categoria.getCodice()))
				// si converte la stringa facendo l'escape dei
				// caratteri HTML ed inoltre si sostituiscono gli
				// acapo con un tag che rende la visione del dato
				// finale identico a come viene inserito
				.withNota(
						StringUtils.replace(
								StringEscapeUtils.escapeHtml(
										helper.getCategorie().getNota().get(categoria.getCodice())
								)
								,"\n"
								,"<br/>"
						)
				)
			.build();
	}
	
}
