package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Action di gestione dell'apertura della pagina con il riepilogo delle
 * categorie d'iscrizione selezionate nel wizard.
 * 
 * @author Stefano.Sabbadin
 */
public class OpenPageRiepilogoCategorieAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2424542969592935831L;

	private IBandiManager bandiManager;

	private List<CategoriaBandoIscrizioneType> categorieBando;

	private String[] codiceCategoria;
	private String[] classeDa;
	private String[] classeA;
	private String[] nota;

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
						.getElencoCategorieBandoIscrizione(
								iscrizioneHelper.getIdBando(), null);

				CategoriaBandoIscrizioneType[] tmpCategorie = null;
				String[] tmpCodiceCategoria = null;
				String[] tmpClasseDa = null;
				String[] tmpClasseA = null;
				String[] tmpNota = null;
				if (categorie != null) {
					// vanno ridefiniti gli array da assegnare effettivamente
					// agli attributi in modo da visualizzare la pagina in modo
					// coerente con la lista di categorie selezionate
					tmpCategorie = new CategoriaBandoIscrizioneType[iscrizioneHelper
							.getCategorie().getCategorieSelezionate().size()];
					tmpCodiceCategoria = new String[tmpCategorie.length];
					tmpClasseDa = new String[tmpCategorie.length];
					tmpClasseA = new String[tmpCategorie.length];
					tmpNota = new String[tmpCategorie.length];

					// si arriva da un'altra pagina e pertanto si presentano i
					// dati leggendoli dalla sessione
					int cont = 0;
					for (int i = 0; i < categorie.length; i++) {
						String codice = categorie[i].getCodice();
						if (iscrizioneHelper.getCategorie()
								.getCategorieSelezionate().contains(codice)) {
							tmpCategorie[cont] = categorie[i];
							tmpCodiceCategoria[cont] = codice;
							tmpClasseDa[cont] = iscrizioneHelper.getCategorie().getClasseDa().get(codice);
							tmpClasseA[cont] = iscrizioneHelper.getCategorie().getClasseA().get(codice);
							// si converte la stringa facendo l'escape dei
							// caratteri HTML ed inoltre si sostituiscono gli
							// acapo con un tag che rende la visione del dato
							// finale identico a come viene inserito
							tmpNota[cont] = StringUtils.replace(
									StringEscapeUtils.escapeHtml(
											iscrizioneHelper.getCategorie().getNota().get(codice)), 
											"\n",
											"<br/>");
							cont++;
						}
					}
				} else {
					tmpCategorie = new CategoriaBandoIscrizioneType[0];
					tmpCodiceCategoria = new String[0];
					tmpClasseDa = new String[0];
					tmpClasseA = new String[0];
					tmpNota = new String[0];
					this.setCategorieBando(new ArrayList<CategoriaBandoIscrizioneType>());
				}
				this.setCategorieBando(Arrays.asList(tmpCategorie));
				this.codiceCategoria = tmpCodiceCategoria;
				this.classeDa = tmpClasseDa;
				this.classeA = tmpClasseA;
				this.nota = tmpNota;

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
	
}
