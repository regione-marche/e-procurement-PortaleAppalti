package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * Action di gestione dell'apertura delle pagine del wizard
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageSAAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private String[] saSelezionata;

	public String[] getSaSelezionata() {
		return saSelezionata;
	}

	public void setSaSelezionata(String[] saSelezionata) {
		this.saSelezionata = saSelezionata;
	}

	/**
	 * ... 
	 */
	@Override
	public void validate() {
		super.validate();
		
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper != null && !iscrizioneHelper.isAggiornamentoIscrizione()) {
			// il controllo lo si effettua solo se la sessione e' ancora valida
			// e si tratta di iscrizione, tanto poi nel metodo richiamato
			// dall'azione si effettua il controllo della sessione e l'eventuale
			// forward corretto
			if (this.saSelezionata == null) {
				this.addFieldError("saSelezionata", this.getText("Errors.stazioneAppaltanteNotSet"));
			}
		}
	}

	/**
	 * ... 
	 */
	@Override
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
			TreeSet<String> setSA = new TreeSet<String>();
			if (this.saSelezionata != null) {
				setSA = new TreeSet<String>(Arrays.asList(this.saSelezionata));
			}
			iscrizioneHelper.setStazioniAppaltanti(setSA);

			// nel caso di categorie non presenti, si salta la pagina di
			// selezione e si passa a quella successiva
			if (!iscrizioneHelper.isEditRTI()) {
				target = "successSkipRTI";
				if (!iscrizioneHelper.getImpresa().isConsorzio()) {
					target = "successSkipComponenti";
					if (iscrizioneHelper.isCategorieAssenti()) {
						target = "successSkipCategorie";
					}
				}
			}
		}
		return target;
	}
	
}
