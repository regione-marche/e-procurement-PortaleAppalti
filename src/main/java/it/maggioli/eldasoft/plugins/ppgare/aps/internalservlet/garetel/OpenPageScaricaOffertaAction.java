package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class OpenPageScaricaOffertaAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5057040244414162025L;

	private ICodificheManager codificheManager;
	
	private IComponente currentMandante;

	private ArrayList<FirmatarioBean> listaFirmatari = new ArrayList<FirmatarioBean>();

	@Validate(EParamValidation.DIGIT)
	private String id;

	private boolean listaImpreseVisible;
	private boolean modificaFirmatarioVisible;
	private boolean listaFirmatariMandatariaVisible;
	private boolean inputFirmatarioMandanteVisible;
	private boolean allFirmatariInseriti;
	private boolean mandanteLiberoProfessionista;
	@Validate(EParamValidation.GENERIC)
	private String tipoImpresaCodifica;
	private List<String> tipoQualificaCodifica;

	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscale;
	@Validate(EParamValidation.NOME)
	private String nome;
	@Validate(EParamValidation.COGNOME)
	private String cognome;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataNascita;
	@Validate(EParamValidation.COMUNE)
	private String comuneNascita;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaNascita;
	@Validate(EParamValidation.GENDER)
	private String sesso;
	@Validate(EParamValidation.COMUNE)
	private String comune;
	@Validate(EParamValidation.INDIRIZZO)
	private String indirizzo;
	@Validate(EParamValidation.NUM_CIVICO)
	private String numCivico;
	@Validate(EParamValidation.CAP)
	private String cap;
	@Validate(EParamValidation.PROVINCIA)
	private String provincia;
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.GENERIC)
	private String soggettoQualifica; 
	@Validate(EParamValidation.PASS_OE)
	private String passoe;
	
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	
	// Hash temporanea per la visualizzazione degli errori senza inficiare la visualizzazione dei dati dei firmatari corretti
	private HashMap<String, SoggettoImpresaHelper> tempHash = new HashMap <String,SoggettoImpresaHelper> (); 

	
	public void setCodificheManager(ICodificheManager manager) {
		this.codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return codificheManager;
	}

	public boolean isListaImpreseVisible() {
		return listaImpreseVisible;
	}

	public void setListaImpreseVisible(boolean listaImpreseVisible) {
		this.listaImpreseVisible = listaImpreseVisible;
	}

	public boolean isModificaFirmatarioVisible() {
		return modificaFirmatarioVisible;
	}

	public void setModificaFirmatarioVisible(boolean modificaFirmatarioVisible) {
		this.modificaFirmatarioVisible = modificaFirmatarioVisible;
	}

	public boolean isListaFirmatariMandatariaVisible() {
		return listaFirmatariMandatariaVisible;
	}

	public void setListaFirmatariMandatariaVisible(boolean listaFirmatariMandatariaVisible) {
		this.listaFirmatariMandatariaVisible = listaFirmatariMandatariaVisible;
	}

	public boolean isInputFirmatarioMandanteVisible() {
		return inputFirmatarioMandanteVisible;
	}

	public void setInputFirmatarioMandanteVisible(boolean inputFirmatarioMandanteVisible) {
		this.inputFirmatarioMandanteVisible = inputFirmatarioMandanteVisible;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IComponente getCurrentMandante() {
		return currentMandante;
	}

	public void setCurrentMandante(IComponente currentMandante) {
		this.currentMandante = currentMandante;
	}

	public ArrayList<FirmatarioBean> getListaFirmatari() {
		return listaFirmatari;
	}

	public void setListaFirmatari(ArrayList<FirmatarioBean> listaFirmatari) {
		this.listaFirmatari = listaFirmatari;
	}

	public boolean isAllFirmatariInseriti() {
		return allFirmatariInseriti;
	}

	public void setAllFirmatariInseriti(boolean allFirmatariInseriti) {
		this.allFirmatariInseriti = allFirmatariInseriti;
	}

	public String getTipoImpresaCodifica() {
		return tipoImpresaCodifica;
	}

	public void setTipoImpresaCodifica(String tipoImpresaCodifica) {
		this.tipoImpresaCodifica = tipoImpresaCodifica;
	}

	public List<String> getTipoQualificaCodifica() {
		return tipoQualificaCodifica;
	}

	public void setTipoQualificaCodifica(List<String> tipoQualificaCodifica) {
		this.tipoQualificaCodifica = tipoQualificaCodifica;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public String getProvinciaNascita() {
		return provinciaNascita;
	}

	public void setProvinciaNascita(String provinciaNascita) {
		this.provinciaNascita = provinciaNascita;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getNumCivico() {
		return numCivico;
	}

	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}
	
	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	public String getSoggettoQualifica() {
		return soggettoQualifica;
	}

	public void setSoggettoQualifica(String soggettoQualifica) {
		this.soggettoQualifica = soggettoQualifica;
	}

	public boolean isMandanteLiberoProfessionista() {
		return mandanteLiberoProfessionista;
	}

	public void setMandanteLiberoProfessionista(boolean mandanteLiberoProfessionista) {
		this.mandanteLiberoProfessionista = mandanteLiberoProfessionista;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public String getPassoe() {
		return passoe;
	}

	public void setPassoe(String passoe) {
		this.passoe = passoe;
	}

	
	public String getSTEP_PREZZI_UNITARI() { return WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI; }

	public String getSTEP_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_OFFERTA; }

	public String getSTEP_SCARICA_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA; }

	public String getSTEP_DOCUMENTI() { return WizardOffertaEconomicaHelper.STEP_DOCUMENTI; }

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		WizardOffertaEconomicaHelper helper = buste.getBustaEconomica().getHelper();
		RiepilogoBusteHelper riepilogo = buste.getBustaRiepilogo().getHelper();
		WizardPartecipazioneHelper partecipazione = buste.getBustaPartecipazione().getHelper();
		WizardDatiImpresaHelper impresa = buste.getImpresa();

		this.setListaFirmatariMandatariaVisible(false);
		if(partecipazione.isRti()) {
			this.setListaImpreseVisible(true);
			
			helper.verificaFirmatarioMandataria();
			
			this.setModificaFirmatarioVisible(helper.getListaFirmatariMandataria().size() > 1);

			this.setAllFirmatariInseriti(helper.testPresenzaFirmatari(riepilogo));
			
		} else {
			this.setListaImpreseVisible(false);
			
			if(helper.getImpresa().isLiberoProfessionista()) {
				FirmatarioBean firmatario = new FirmatarioBean();
				firmatario.setNominativo(impresa.getAltriDatiAnagraficiImpresa().getCognome() + " " + 
										 impresa.getAltriDatiAnagraficiImpresa().getNome());
				this.getListaFirmatari().add(firmatario);
			} else {
				List<String> qualifiche = helper.getQualificheFirmatarioMandataria();
				this.setTipoQualificaCodifica(qualifiche);
			}
			
			this.setAllFirmatariInseriti(true);
		}
		
		// passoe
		this.setPassoe(helper.getPassoe());

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);

		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String openPageEditFirmatarioMandataria() {
		String target = SUCCESS;

		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();

		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(true);
		this.setModificaFirmatarioVisible(true);	
		this.setInputFirmatarioMandanteVisible(false);

		List<String> qualifiche = helper.getQualificheFirmatarioMandataria();
		this.setTipoQualificaCodifica(qualifiche);

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);

		return target;
	}

	/**
	 * ... 
	 */
	public String openPageEditFirmatarioMandante() {
		String target = SUCCESS;
		
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();

		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(false);
		this.setInputFirmatarioMandanteVisible(true);
		if(helper.getListaFirmatariMandataria().size() > 1) {
			this.setModificaFirmatarioVisible(true);
		}

		this.currentMandante = helper.getComponentiRTI().get(Integer.parseInt(this.id));
		this.setMandanteLiberoProfessionista( "6".equals(this.currentMandante.getTipoImpresa()) );
		this.setTipoImpresaCodifica(this.getCodificheManager().getCodifiche().get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO).get(this.currentMandante.getTipoImpresa()));

		SoggettoImpresaHelper fromHelper = helper.getComponentiRTI().getFirmatario(this.currentMandante);
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
			if(!this.isMandanteLiberoProfessionista()) {
				soggetto.setSoggettoQualifica(fromHelper.getSoggettoQualifica());
			}
		}
		this.tempHash.put(helper.getComponentiRTI().getChiave(this.currentMandante), soggetto);

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);

		return target;
	}

	/**
	 * ... 
	 */
	public String openPageAfterErrorMandante() {
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		RiepilogoBusteHelper riepilogo = GestioneBuste.getBustaRiepilogoFromSession().getHelper();

		if(helper.getComponentiRTI().size() > 0) {
			this.setListaImpreseVisible(true);
			this.setModificaFirmatarioVisible( helper.getListaFirmatariMandataria().size() > 1);
		}
		
		//BF
		Collection<String> err = this.getActionErrors();

		if(err.isEmpty() || !err.toArray()[0].toString().equals(this.getText("Errors.offertaTelematica.scaricaPDF"))) {
			//ERRORE DA MASCHERA IN INPUT PER MANDANTE 
			this.setInputFirmatarioMandanteVisible(true);
			this.currentMandante = helper.getComponentiRTI().get(Integer.parseInt(this.id));
			this.setMandanteLiberoProfessionista( "6".equals(this.currentMandante.getTipoImpresa()) );
			this.setTipoImpresaCodifica(this.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO).get(this.currentMandante.getTipoImpresa()));

			SoggettoImpresaHelper soggetto = new SoggettoImpresaHelper();
			soggetto.setCognome(this.cognome);
			soggetto.setNome(this.nome);
			soggetto.setNominativo(this.cognome + " " + this.nome);
			soggetto.setCap(this.cap);
			soggetto.setCodiceFiscale(this.codiceFiscale);
			soggetto.setComune(this.comune);
			soggetto.setComuneNascita(this.comuneNascita);
			soggetto.setDataNascita(this.dataNascita);
			soggetto.setIndirizzo(this.indirizzo);
			soggetto.setNazione(this.nazione);
			soggetto.setNumCivico(this.numCivico);
			soggetto.setProvincia(this.provincia);
			soggetto.setProvinciaNascita(this.provinciaNascita);
			soggetto.setSesso(this.sesso);
			soggetto.setSoggettoQualifica(this.soggettoQualifica);

			this.tempHash.put(helper.getComponentiRTI().getChiave(this.currentMandante), soggetto);
		} else {
			if(helper.getComponentiRTI().size() > 0) {
				this.setAllFirmatariInseriti(helper.testPresenzaFirmatari(riepilogo));
			}
		}
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);

		return this.getTarget();

	}
	
	/**
	 * Restituisce il firmatario di un componente RTI
	 * Utilizzato nella pagina jsp in sostituzione a "tempHash"
	 */
	public SoggettoImpresaHelper getFirmatario(IComponente componenteRTI) {
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		return (helper != null 
				? this.tempHash.get(helper.getComponentiRTI().getChiave(componenteRTI))
		        : null);
	}
		
}


