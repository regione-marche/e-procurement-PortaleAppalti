package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class ProcessPageScaricaDomandaRinnovoAction extends ProcessPageScaricaDomandaIscrizioneAction {
	/**
	 * UI
	 */
	private static final long serialVersionUID = -4179598481560984730L;

	/**
	 * ... 
	 */
	@SkipValidation
	public String editRinnovoFirmatarioMandataria(){
		return "modifyFirmatarioMandataria";
	}
	
	/**
	 * ... 
	 */
	@SkipValidation
	public String editRinnovoFirmatarioMandante(){
		return "modifyFirmatarioMandante";
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		if( !this.testPresenzaFirmatari(helper) ) {
			target = ERROR;
		}

		this.nextResultAction = helper.getNextAction(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = SUCCESS;
		// nel caso di assenza di categorie si deve saltare alla pagina
		// precedente (a sua volta, se esiste un'unica stazione appaltante
		// si deve saltare direttamente alla pagina precedente)
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		this.nextResultAction = helper.getPreviousAction(WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE);
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String save() {
		String target = INPUT;
		
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);

		this.addActionError(this.getText("Errors.function.notSupported"));
		target = CommonSystemConstants.PORTAL_ERROR;
		
		this.nextResultAction = rinnovoHelper.getPreviousAction(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);		
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String saveRinnovoFirmatarioMandataria() {
		String target = "saveFirmatarioMandataria";
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		helper.saveFirmatarioMandataria(this.getFirmatarioSelezionato());
		
		helper.setDatiModificati(true);
	
		return target;
	}
	
	/**
	 * ... 
	 */
	public String saveRinnovoFirmatarioMandante(){
		String target = "saveFirmatarioMandante";
	
		WizardRinnovoHelper helper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		firmatario.setNominativo(this.getCognome() + " " +this.getNome());

		firmatario.setCodiceFiscale(this.getCodiceFiscale());
		
		// Dati anagrafici
		firmatario.setCognome(this.getCognome());
		firmatario.setNome(this.getNome());
		firmatario.setDataNascita(this.getDataNascita());
		firmatario.setComuneNascita(this.getComuneNascita());
		firmatario.setProvinciaNascita(this.getProvinciaNascita());
		firmatario.setSesso(this.getSesso());
		
		// Residenza
		firmatario.setComune(this.getComune());
		firmatario.setIndirizzo(this.getIndirizzo());
		firmatario.setNumCivico(this.getNumCivico());
		firmatario.setCap(this.getCap());
		firmatario.setProvincia(this.getProvincia());
		firmatario.setNazione(this.getNazione());
		firmatario.setSoggettoQualifica(this.getSoggettoQualifica());
		
		helper.getComponentiRTI().addFirmatario(helper.getComponentiRTI().get(Integer.parseInt(this.getId())), firmatario);
		helper.setDatiModificati(true);

		return target;
	}
	
}
