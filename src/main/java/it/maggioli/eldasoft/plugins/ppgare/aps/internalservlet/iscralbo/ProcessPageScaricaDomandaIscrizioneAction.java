package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

public class ProcessPageScaricaDomandaIscrizioneAction extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.ProcessPageScaricaOffertaAction{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4179598481560984730L;

	/**
	 * ... 
	 */
	@Override
	@SkipValidation	
	public String saveFirmatarioMandataria(){
		String target = "saveFirmatarioMandataria";
		
		boolean mandatariaTrovata = false;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
		
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		
		for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !mandatariaTrovata; i++) {
			ISoggettoImpresa soggettoFromLista = null;
			String listaFirmatarioSelezionato = StringUtils.substring(
					this.getFirmatarioSelezionato(), 0, this.getFirmatarioSelezionato().indexOf("-"));
			int indiceFirmatarioSelezionato = Integer.parseInt(StringUtils.substring(
					this.getFirmatarioSelezionato(), 
					this.getFirmatarioSelezionato().indexOf("-") + 1, 
					this.getFirmatarioSelezionato().length()));
			if(listaFirmatarioSelezionato.equals(CataloghiConstants.LISTA_ALTRE_CARICHE)) {
				soggettoFromLista = datiImpresaHelper.getAltreCaricheImpresa().get(indiceFirmatarioSelezionato);
			} else if(listaFirmatarioSelezionato.equals(CataloghiConstants.LISTA_DIRETTORI_TECNICI)) {
				soggettoFromLista = datiImpresaHelper.getDirettoriTecniciImpresa().get(indiceFirmatarioSelezionato);
			} else {
				soggettoFromLista = datiImpresaHelper.getLegaliRappresentantiImpresa().get(indiceFirmatarioSelezionato);
			}
			
			if(helper.getListaFirmatariMandataria().get(i).getNominativo()
					.equalsIgnoreCase(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome())) {
				mandatariaTrovata = true;
			}
			
			firmatario.copyFrom(soggettoFromLista);
			firmatario.setNominativo(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome());
			
			helper.setIdFirmatarioSelezionatoInLista(i);
		}
		
		helper.getComponentiRTI().addFirmatario(datiImpresaHelper.getDatiPrincipaliImpresa(), firmatario);
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	@SkipValidation
	public String saveFirmatarioMandante(){
		String target = "saveFirmatarioMandante";
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		
		IComponente componente = helper.getComponentiRTI().get(Integer.parseInt(this.getId()));
		
		firmatario.setNominativo(this.getCognome() + " " + this.getNome());
		firmatario.setCodiceFiscale(this.getCodiceFiscale());
		
		//Dati anagrafici
		firmatario.setCognome(this.getCognome());
		firmatario.setNome(this.getNome());
		firmatario.setDataNascita(this.getDataNascita());
		firmatario.setComuneNascita(this.getComuneNascita());
		firmatario.setProvinciaNascita(this.getProvinciaNascita());
		firmatario.setSesso(this.getSesso());
		
		//Residenza
		firmatario.setComune(this.getComune());
		firmatario.setIndirizzo(this.getIndirizzo());
		firmatario.setNumCivico(this.getNumCivico());
		firmatario.setCap(this.getCap());
		firmatario.setProvincia(this.getProvincia());
		firmatario.setNazione(this.getNazione());
		firmatario.setSoggettoQualifica(this.getSoggettoQualifica());

		helper.getComponentiRTI().addFirmatario(componente, firmatario);
		
		//helper.setDatiModificati(true);  // <= manca e va messo ???
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
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
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		this.nextResultAction = iscrizioneHelper.getPreviousAction(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String save() {
		String target = INPUT;
		
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		// si restituisce sempre "funzione non supportata" => ???
		this.addActionError(this.getText("Errors.function.notSupported"));
		target = CommonSystemConstants.PORTAL_ERROR;
		
		this.nextResultAction = iscrizioneHelper.getPreviousAction(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
		
		return target;
	}

	/**
	 * verifica se tutti i firmatari sono stati inseriti 
	 */
	protected boolean testPresenzaFirmatari(WizardIscrizioneHelper helper) {
		// NB: il wizard del rinnovo e' un'estensione del wizard dell'iscrizione
		boolean firmatariPresenti = true;
		
		if(helper.isRti()) {
			// controlla se sono presenti tutti i firmatari per ogni componente della rti...
			for(int i = 0; i < helper.getComponentiRTI().size() && firmatariPresenti; i++) {
				if(helper.getComponentiRTI().getFirmatario(helper.getComponentiRTI().get(i)) == null) {
					firmatariPresenti = false;
				}
			}
			if( !firmatariPresenti ) {
				this.addActionError(this.getText("Errors.firmatarioNonSelezionato"));
			}
		}
		return firmatariPresenti;
	}

}
