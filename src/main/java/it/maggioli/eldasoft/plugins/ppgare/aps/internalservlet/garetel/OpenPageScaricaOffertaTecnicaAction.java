package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageScaricaOffertaTecnicaAction extends AbstractOpenPageAction{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3244799491605081641L;

	private ICodificheManager _codificheManager;
	
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
	@Validate(EParamValidation.GENERIC)
	private ArrayList<String> tipoQualificaCodifica;

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

	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	
	// Hash temporanea per la visualizzazione degli errori senza inficiare la 
	// visualizzazione dei dati dei firmatari corretti
	private HashMap<String, SoggettoImpresaHelper> tempHash = new HashMap <String,SoggettoImpresaHelper> (); 

	
	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return _codificheManager;
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

	public ArrayList<String> getTipoQualificaCodifica() {
		return tipoQualificaCodifica;
	}

	public void setTipoQualificaCodifica(ArrayList<String> tipoQualificaCodifica) {
		this.tipoQualificaCodifica = tipoQualificaCodifica;
	}

	public HashMap<String, SoggettoImpresaHelper> getTempHash() {
		return tempHash;
	}

	public void setTempHash(HashMap<String, SoggettoImpresaHelper> tempHash) {
		this.tempHash = tempHash;
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

	
	public String getSTEP_OFFERTA() { return WizardOffertaTecnicaHelper.STEP_OFFERTA; }
	
	public String getSTEP_SCARICA_OFFERTA() { return WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA; }

	public String getSTEP_DOCUMENTI() { return WizardOffertaTecnicaHelper.STEP_DOCUMENTI; }

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
		BustaTecnica bustaTec = buste.getBustaTecnica();		
		WizardOffertaTecnicaHelper helper = bustaTec.getHelper();
		WizardPartecipazioneHelper partecipazioneHelper = buste.getBustaPartecipazione().getHelper();
		RiepilogoBusteHelper riepilogo = buste.getBustaRiepilogo().getHelper();
		
		this.setListaFirmatariMandatariaVisible(false);
		if(partecipazioneHelper.isRti()) {
			this.setListaImpreseVisible(true);
			
			if(helper.getListaFirmatariMandataria().size() == 1) {
				this.setModificaFirmatarioVisible(false);
				
				SoggettoFirmatarioImpresaHelper firmatarioMandataria = new SoggettoFirmatarioImpresaHelper();
				if(!helper.getImpresa().isLiberoProfessionista()) {
					// inizializza i dati del firmatario con i dati relativi al libero professionista
					FirmatarioBean firmatario = helper.getListaFirmatariMandataria().get(0);
					ISoggettoImpresa soggetto = null;
					if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatario.getLista())) {
						soggetto = helper.getImpresa().getLegaliRappresentantiImpresa().get(firmatario.getIndex());
					} else if(CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatario.getLista())) {
						soggetto = helper.getImpresa().getDirettoriTecniciImpresa().get(firmatario.getIndex());
					} else {
						soggetto = helper.getImpresa().getAltreCaricheImpresa().get(firmatario.getIndex());
					}
					firmatarioMandataria.copyFrom(soggetto);
					firmatarioMandataria.setNominativo(firmatario.getNominativo());
				} else {
					// inizializza i dati del firmatario con i dati relativi all'impresa
					String nominativo = helper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() != null 
										? helper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() + " " + 
										  helper.getImpresa().getAltriDatiAnagraficiImpresa().getNome() 
										: helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
					firmatarioMandataria.setNome(helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());										
					firmatarioMandataria.setNominativo(nominativo);
				}
				helper.getComponentiRTI().addFirmatario(helper.getComponentiRTI().get(0), firmatarioMandataria);
			} else {
				this.setModificaFirmatarioVisible(true);	
			}
			this.setAllFirmatariInseriti(helper.testPresenzaFirmatari(riepilogo));
		} else {
			this.setListaImpreseVisible(false);
			if(helper.getImpresa().isLiberoProfessionista()) {
				FirmatarioBean firmatario = new FirmatarioBean();
				firmatario.setNominativo(buste.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
				this.getListaFirmatari().add(firmatario);
				this.setAllFirmatariInseriti(true);
			} else {
				ArrayList<String> qualificaFirmatario = new ArrayList<String>();

				for(int i = 0; i < helper.getListaFirmatariMandataria().size(); i++) {
					FirmatarioBean firmatarioCorrente = helper.getListaFirmatariMandataria().get(i);

					if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioCorrente.getLista())) {
						qualificaFirmatario.add(this.getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
								.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
										+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
					} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioCorrente.getLista())) {
						qualificaFirmatario.add(this.getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
								.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
										+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
					} else {
						String codiceQualifica = helper.getImpresa().getAltreCaricheImpresa().get(firmatarioCorrente.getIndex()).getSoggettoQualifica();
						qualificaFirmatario.add(this.getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
								.get(codiceQualifica));
					}
				}

				this.setTipoQualificaCodifica(qualificaFirmatario);
				this.setAllFirmatariInseriti(true);
			}
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA);

		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String openPageEditFirmatarioMandataria() {
		String target = SUCCESS;

		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();

		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(true);
		this.setModificaFirmatarioVisible(true);	
		this.setInputFirmatarioMandanteVisible(false);

		ArrayList<String> qualificaFirmatario = new ArrayList<String>();

		for(int i = 0; i < helper.getListaFirmatariMandataria().size(); i++) {
			FirmatarioBean firmatarioCorrente = helper.getListaFirmatariMandataria().get(i);


			if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioCorrente.getLista())) {
				qualificaFirmatario.add(this.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
								+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioCorrente.getLista())) {
				qualificaFirmatario.add(this.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
								+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else {
				String codiceQualifica = helper.getImpresa().getAltreCaricheImpresa().get(firmatarioCorrente.getIndex()).getSoggettoQualifica();
				qualificaFirmatario.add(this.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
						.get(codiceQualifica));
			}
		}

		this.setTipoQualificaCodifica(qualificaFirmatario);

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA);

		return target;
	}

	/**
	 * ...
	 */
	public String openPageEditFirmatarioMandante() {
		String target = SUCCESS;
		
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();

		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(false);
		this.setInputFirmatarioMandanteVisible(true);
		if(helper.getListaFirmatariMandataria().size() > 1) {
			this.setModificaFirmatarioVisible(true);
		}

		this.currentMandante = helper.getComponentiRTI().get(Integer.parseInt(this.id));
		this.setMandanteLiberoProfessionista( "6".equals(this.currentMandante.getTipoImpresa()) );
		this.setTipoImpresaCodifica(this.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO).get(this.currentMandante.getTipoImpresa()));

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
						 WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA);

		return target;
	}

	/**
	 * ... 
	 */
	public String openPageAfterErrorMandante() {
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
		RiepilogoBusteHelper riepilogo = GestioneBuste.getBustaRiepilogoFromSession().getHelper();

		if(helper.getComponentiRTI().size() > 0) {
			this.setListaImpreseVisible(true); 
			if(helper.getListaFirmatariMandataria().size() > 1) {
				this.setModificaFirmatarioVisible(true);
			} else {
				this.setModificaFirmatarioVisible(false);
			}
		}
		
		//BF
		Collection<String> err = this.getActionErrors();

		if(err.isEmpty() || !err.toArray()[0].toString().equals(this.getText("Errors.offertaTecnica.scaricaPDF"))) {
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
						 WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA);

		return this.getTarget();
	}

	/**
	 * Restituisce il firmatario di un componente RTI
	 * Utilizzato nella pagina jsp in sostituzione a "tempHash"
	 */
	public SoggettoImpresaHelper getFirmatario(IComponente componenteRTI) {
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
		return (helper != null 
				? this.tempHash.get(helper.getComponentiRTI().getChiave(componenteRTI))
		        : null);
	}

}
