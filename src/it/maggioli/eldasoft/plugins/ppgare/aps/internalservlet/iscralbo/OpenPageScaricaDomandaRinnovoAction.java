package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.Collection;

public class OpenPageScaricaDomandaRinnovoAction extends OpenPageScaricaDomandaIscrizioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4986431262361969763L;

	
	/**
	 * ... 
	 */
	public String openPage() {
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		helper.setIscrizioneDomandaVisible(true);
		
		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
					
		this.setListaFirmatariMandatariaVisible(false);
		if(helper.isAmmesseRTI() && helper.isRti()) {
			if(helper.getComponentiRTI().size() <= 0) {
				
				IDatiPrincipaliImpresa datiPrincipaliImpresaHelper = helper.getImpresa().getDatiPrincipaliImpresa();
				ComponentiRTIList componentiRTI = new ComponentiRTIList();
								
				// crea la lista della composizione RTI come mandataria + lista mandanti
				// ...aggiungi la mandataria... 
				IComponente mandataria = new ComponenteHelper();
				mandataria.setCodiceFiscale(datiPrincipaliImpresaHelper.getCodiceFiscale());
				mandataria.setNazione(datiPrincipaliImpresaHelper.getNazioneSedeLegale());
				mandataria.setPartitaIVA(datiPrincipaliImpresaHelper.getPartitaIVA());
				mandataria.setRagioneSociale(datiPrincipaliImpresaHelper.getRagioneSociale());
				mandataria.setTipoImpresa(datiPrincipaliImpresaHelper.getTipoImpresa());				
				componentiRTI.add(mandataria);
				
				// ...aggiungi le mandatanti...
				for(int i = 0; i < helper.getComponenti().size(); i++) {
					IComponente mandante = new ComponenteHelper();
					mandante.setCodiceFiscale(helper.getComponenti().get(i).getCodiceFiscale());
					mandante.setNazione(helper.getComponenti().get(i).getNazione());
					mandante.setPartitaIVA(helper.getComponenti().get(i).getPartitaIVA());
					mandante.setRagioneSociale(helper.getComponenti().get(i).getRagioneSociale());
					mandante.setTipoImpresa(helper.getComponenti().get(i).getTipoImpresa());
					componentiRTI.add(mandante);
				}
				helper.setComponentiRTI(componentiRTI);
				this.setModificaFirmatarioVisible(true);
			}
			
			this.setListaImpreseVisible(true);
			if(helper.getComponentiRTI().size() > 0) {
		
				FirmatarioBean firmatario = helper.getListaFirmatariMandataria().get(0);
				
				if(helper.getListaFirmatariMandataria().size() == 1) {
					this.setModificaFirmatarioVisible(false);
					
					SoggettoFirmatarioImpresaHelper firmatarioMandataria = new SoggettoFirmatarioImpresaHelper();
					if(!helper.getImpresa().isLiberoProfessionista()){
						firmatarioMandataria.setNominativo(firmatario.getNominativo());
						if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatario.getLista())) {
							firmatarioMandataria.setNome(helper.getImpresa().getLegaliRappresentantiImpresa().get(firmatario.getIndex()).getNome());
							firmatarioMandataria.setCognome(helper.getImpresa().getLegaliRappresentantiImpresa().get(firmatario.getIndex()).getCognome());
						} else if(CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatario.getLista())) {
							firmatarioMandataria.setNome(helper.getImpresa().getDirettoriTecniciImpresa().get(firmatario.getIndex()).getNome());
							firmatarioMandataria.setCognome(helper.getImpresa().getDirettoriTecniciImpresa().get(firmatario.getIndex()).getCognome());
						} else {
							firmatarioMandataria.setNome(helper.getImpresa().getAltreCaricheImpresa().get(firmatario.getIndex()).getNome());
							firmatarioMandataria.setCognome(helper.getImpresa().getAltreCaricheImpresa().get(firmatario.getIndex()).getCognome());
						}
					} else {
						firmatarioMandataria.setNome(helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
						firmatarioMandataria.setNominativo(helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
					}
					
					helper.getComponentiRTI().addFirmatario(helper.getComponentiRTI().get(0), firmatarioMandataria);
				} else {
					this.setModificaFirmatarioVisible(true);	
				}
				
				this.setAllFirmatariInseriti(testPresenzaFirmatari(helper));
			}
		} else {
			this.setListaImpreseVisible(false);
			if(helper.getImpresa().isLiberoProfessionista()) {
				FirmatarioBean firmatario = new FirmatarioBean();
				firmatario.setNominativo(datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale());
				this.getListaFirmatari().add(firmatario);
				this.setAllFirmatariInseriti(true);
			} else {
				ArrayList<String> qualificaFirmatario = getQualificaFirmatario(helper);
				this.setTipoQualificaCodifica(qualificaFirmatario);
				this.setAllFirmatariInseriti(true);
			}
		}
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE);
		
		return this.getTarget();
	}
	
	/**
	 * ... 
	 */
	public String openPageEditRinnovoFirmatarioMandante() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		helper.setIscrizioneDomandaVisible(false);
		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(false);
		this.setInputFirmatarioMandanteVisible(true);
		if(helper.getListaFirmatariMandataria().size() > 1) {
			this.setModificaFirmatarioVisible(true);
		}
		
		this.setCurrentMandante(helper.getComponentiRTI().get(Integer.parseInt(this.getId())));
		this.setMandanteLiberoProfessionista( "6".equals(this.getCurrentMandante().getTipoImpresa()) );		
		this.setTipoImpresaCodifica(this.getCodificheManager().getCodifiche().get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO).get(this.getCurrentMandante().getTipoImpresa()));
				
		int id = Integer.parseInt(this.getId());
		if(id < helper.getComponentiRTI().size()) {
			IComponente componente = helper.getComponentiRTI().get(id);
			
			SoggettoImpresaHelper fromHelper = helper.getComponentiRTI().getFirmatario(componente);
			SoggettoImpresaHelper soggetto = new SoggettoImpresaHelper();
			
			if(fromHelper != null) {
				soggetto.setCognome(fromHelper.getCognome());
				soggetto.setNome(fromHelper.getNome());
				soggetto.setNominativo(fromHelper.getNominativo());
				soggetto.setCap(fromHelper.getCap());
				soggetto.setCodiceFiscale(fromHelper.getCodiceFiscale());
				soggetto.setComune(fromHelper.getComune());
				soggetto.setComuneNascita(fromHelper.getComuneNascita());
				soggetto.setDataNascita(fromHelper.getDataNascita());
				soggetto.setIndirizzo(fromHelper.getIndirizzo());
				soggetto.setNazione(fromHelper.getNazione());
				soggetto.setNumCivico(fromHelper.getNumCivico());
				soggetto.setProvincia(fromHelper.getProvincia());
				soggetto.setProvinciaNascita(fromHelper.getProvinciaNascita());
				soggetto.setSesso(fromHelper.getSesso());
				if(!this.isMandanteLiberoProfessionista()){
					soggetto.setSoggettoQualifica(fromHelper.getSoggettoQualifica());
				}
			}
			this.tempHash.put(helper.getComponentiRTI().getChiave(componente), soggetto);
		} 
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
					 	 WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE);

		return target;
	}
	
	/**
	 * ... 
	 */
	public String openPageEditRinnovoFirmatarioMandataria() {
		String target = SUCCESS;
		
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		helper.setIscrizioneDomandaVisible(false);
		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(true);
		this.setModificaFirmatarioVisible(true);	
		this.setInputFirmatarioMandanteVisible(false);
		
		ArrayList<String> qualificaFirmatario = getQualificaFirmatario(helper);
		
		if(helper.getListaFirmatariMandataria().size() > 0) {		
			if(helper.getIdFirmatarioSelezionatoInLista() < 0) {
				helper.setIdFirmatarioSelezionatoInLista(0);
				helper.setFirmatarioSelezionato(helper.getListaFirmatariMandataria().get(0));
			}
		}
		
		this.setTipoQualificaCodifica(qualificaFirmatario);
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE);
		
		return target;
	}
		
	/**
	 * ... 
	 */
	public String openPageRinnovoAfterErrorMandante() {
		WizardRinnovoHelper helper = (WizardRinnovoHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		
		helper.setIscrizioneDomandaVisible(false);
		if(helper.getComponentiRTI().size() > 0) {
			this.setListaImpreseVisible(true);
			this.setModificaFirmatarioVisible(true);
		}
		//BF
		Collection<String> err = this.getActionErrors();

		if(err.isEmpty() || !err.toArray()[0].toString().equals(this.getText("Errors.offertaTelematica.scaricaPDF"))) {
			//ERRORE DA MASCHERA IN INPUT PER MANDANTE 
			this.setInputFirmatarioMandanteVisible(true);
			this.setCurrentMandante(helper.getComponentiRTI().get(Integer.parseInt(this.getId())));			
			this.setMandanteLiberoProfessionista( "6".equals(this.getCurrentMandante().getTipoImpresa()) );
			this.setTipoImpresaCodifica(this.getCodificheManager().getCodifiche().get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO).get(this.getCurrentMandante().getTipoImpresa()));
			
			IComponente componente = helper.getComponentiRTI().get(Integer.parseInt(this.getId()));
			int i = helper.getComponentiRTI().getMandataria(componente);
			String chiaveMandante = (i >= 0 ? helper.getComponentiRTI().getChiave(componente) : null);
			
			SoggettoImpresaHelper soggetto = new SoggettoImpresaHelper();
			soggetto.setCognome(this.getCognome());
			soggetto.setNome(this.getNome());
			soggetto.setNominativo(this.getCognome() + " " + this.getNome());
			soggetto.setCap(this.getCap());
			soggetto.setCodiceFiscale(this.getCodiceFiscale());
			soggetto.setComune(this.getComune());
			soggetto.setComuneNascita(this.getComuneNascita());
			soggetto.setDataNascita(this.getDataNascita());
			soggetto.setIndirizzo(this.getIndirizzo());
			soggetto.setNazione(this.getNazione());
			soggetto.setNumCivico(this.getNumCivico());
			soggetto.setProvincia(this.getProvincia());
			soggetto.setProvinciaNascita(this.getProvinciaNascita());
			soggetto.setSesso(this.getSesso());
			soggetto.setSoggettoQualifica(this.getSoggettoQualifica());
			
			this.tempHash.put(chiaveMandante, soggetto);
		} else {
			if(helper.getComponentiRTI().size() > 0) {
				this.setAllFirmatariInseriti(testPresenzaFirmatari(helper));
			} 
		}
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardRinnovoHelper.STEP_SCARICA_ISCRIZIONE);
		
		return this.getTarget();
	}
	
	/**
	 * Restituisce il firmatario di un componente RTI
	 * Utilizzato nella pagina jsp in sostituzione a "tempHash"
	 */
	public SoggettoImpresaHelper getFirmatario(IComponente componenteRTI) {
		WizardRinnovoHelper helper = (WizardRinnovoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		return (helper != null 
				? this.tempHash.get(helper.getComponentiRTI().getChiave(componenteRTI))
		        : null);
	}
	
	/**
	 * Restituisce l'elenco delle qualifiche dei firmatari della mandataria 
	 */
	private ArrayList<String> getQualificaFirmatario(WizardRinnovoHelper helper) {		
		return OpenPageScaricaDomandaIscrizioneAction.getQualificaFirmatario(helper, this);
	}
			
}
