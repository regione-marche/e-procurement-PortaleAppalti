package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageScaricaDomandaIscrizioneAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4924649951613088666L;

	public static final String NOT_VALID_STAMP = "NOT_VALID_STAMP";
	
	private ICodificheManager _codificheManager;
	
	private IComponente currentMandante;
	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate
	private ArrayList<FirmatarioBean> listaFirmatari = new ArrayList<>();

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
	@Validate(EParamValidation.SERIAL_NUMBER)
	private String serialNumberMarcaBollo; 

	// Hash temporanea per la visualizzazione degli errori senza inficiare la 
	// visualizzazione dei dati dei firmatari corretti
	protected HashMap<String, SoggettoImpresaHelper> tempHash = new HashMap <String,SoggettoImpresaHelper> (); 

	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return _codificheManager;
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

	public String getSerialNumberMarcaBollo() {
		return serialNumberMarcaBollo;
	}

	public void setSerialNumberMarcaBollo(String serialNumberMarcaBollo) {
		this.serialNumberMarcaBollo = serialNumberMarcaBollo;
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

	public void setListaFirmatariMandatariaVisible(
			boolean listaFirmatariMandatariaVisible) {
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

	public String getSTEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO() {
		return WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO;
	}

	public String getSTEP_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_QUESTIONARIO;
	}
	
	public String getSTEP_RIEPILOGO_QUESTIONARIO() {
		return WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO;
	}

	/**
	 * ... 
	 */	
	public String openPage() {
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null)
			this.addActionError(errore);
		errore = (String) session.remove(NOT_VALID_STAMP);
		if (StringUtils.isNotEmpty(errore))
			addFieldError("serialNumberMarcaBollo", errore);
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		helper.setIscrizioneDomandaVisible(true);
		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
		
		this.setListaFirmatariMandatariaVisible(false);
		this.setSerialNumberMarcaBollo(helper.getSerialNumberMarcaBollo());
		
		boolean rti = (helper.isAmmesseRTI() && helper.isRti()) || 
		              (helper.isRti() && helper.getComponentiRTI() != null && helper.getComponentiRTI().size() > 1); 
		
		if(rti) {
			if(helper.getComponentiRTI().isEmpty()) {
				this.setModificaFirmatarioVisible(true);
			}
			
			this.setListaImpreseVisible(true);
			if(helper.getComponentiRTI().size() > 0) {

				if(helper.getListaFirmatariMandataria().size() == 1) {
					FirmatarioBean firmatario = helper.getListaFirmatariMandataria().get(0);
				
					this.setModificaFirmatarioVisible(false);
					
					SoggettoFirmatarioImpresaHelper firmatarioMandataria = new SoggettoFirmatarioImpresaHelper();
					if(!helper.getImpresa().isLiberoProfessionista()) {
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
				String nominativo = (helper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() != null 
					? helper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() + " " 
					  + helper.getImpresa().getAltriDatiAnagraficiImpresa().getNome() 
					: helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
				firmatario.setNominativo(nominativo);
				this.getListaFirmatari().add(firmatario);
				helper.getListaFirmatariMandataria().clear();
				helper.getListaFirmatariMandataria().add(firmatario);
				this.setAllFirmatariInseriti(true);
			} else {
				if(helper.getIdFirmatarioSelezionatoInLista() < 0) {
					// caso cessazione precedente firmatario => seleziono di default il primo possibile
					helper.setIdFirmatarioSelezionatoInLista(0);
					helper.setFirmatarioSelezionato(helper.getListaFirmatariMandataria().get(0));
				}
				
				ArrayList<String> qualificaFirmatario = getQualificaFirmatario(helper, this);

				this.setTipoQualificaCodifica(qualificaFirmatario);
				this.setAllFirmatariInseriti(true);
			}
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);

		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String openPageEditFirmatarioMandataria(){
		String target = SUCCESS;

		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		helper.setIscrizioneDomandaVisible(false);
		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(true);
		this.setModificaFirmatarioVisible(true);
		this.setInputFirmatarioMandanteVisible(false);

		ArrayList<String> qualificaFirmatario = getQualificaFirmatario(helper, this);

		this.setTipoQualificaCodifica(qualificaFirmatario);
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);

		return target;
	}

	public String openPageEditFirmatarioMandante(){
		String target = SUCCESS;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		helper.setIscrizioneDomandaVisible(false);
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
			this.tempHash.put(helper.getComponentiRTI().getChiave(this.currentMandante), soggetto);
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
				         WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);

		return target;
	}

	public String openPageAfterErrorMandante() {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		helper.setIscrizioneDomandaVisible(false);
		if(helper.getComponentiRTI().size() > 0) {
			this.setListaImpreseVisible(true);
			this.setModificaFirmatarioVisible(true);
		}
		//BF
		Collection<String> err = this.getActionErrors();
		Map<String,List<String>> errFileds = this.getFieldErrors();

		if(err.isEmpty() || !errFileds.isEmpty() || !err.toArray()[0].toString().equals(this.getText("Errors.firmatarioNonSelezionato"))) {
			// ERRORE DA MASCHERA IN INPUT PER MANDANTE 
			this.setInputFirmatarioMandanteVisible(true);
			this.currentMandante = helper.getComponentiRTI().get(Integer.parseInt(this.id));
			this.setMandanteLiberoProfessionista( "6".equals(this.currentMandante.getTipoImpresa()) );
			this.setTipoImpresaCodifica(this.getCodificheManager().getCodifiche().get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO).get(this.currentMandante.getTipoImpresa()));

			IComponente componente = helper.getComponentiRTI().get(Integer.parseInt(this.id));

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

			this.tempHash.put(helper.getComponentiRTI().getChiave(componente), soggetto);
		} else {
			if(helper.getComponentiRTI().size() > 0) {
				this.setAllFirmatariInseriti(testPresenzaFirmatari(helper));
			}
		}
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);

		return this.getTarget();

	}

	/**
	 * verifica se tutti i componenti del RTI hanno il firmatario
	 */
	protected boolean testPresenzaFirmatari(WizardIscrizioneHelper helper) {
		boolean chiaveFound = true;

		for(int i = 0 ; i < helper.getComponentiRTI().size() && chiaveFound; i++) {
			SoggettoImpresaHelper firmatario = helper.getComponentiRTI().getFirmatario(helper.getComponentiRTI().get(i));
			if((firmatario == null) 
			   || (firmatario != null && StringUtils.isEmpty(firmatario.getNome()))) {
				chiaveFound = false;
			}
		}
		
		return chiaveFound;
	}
	
	/**
	 * Restituisce l'elenco delle qualifiche dei firmatari della mandataria 
	 */
	public static ArrayList<String> getQualificaFirmatario(
			WizardIscrizioneHelper helper, 
			EncodedDataAction action) {
		ArrayList<String> qualificaFirmatario = new ArrayList<String>();
		
		for(int i = 0; i < helper.getListaFirmatariMandataria().size(); i++) {
			FirmatarioBean firmatarioCorrente = helper.getListaFirmatariMandataria().get(i);
			
			if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioCorrente.getLista())) {
				qualificaFirmatario.add(action.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
							 .get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE + 
								  CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioCorrente.getLista())) {
				qualificaFirmatario.add(action.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
							 .get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO +
							      CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else {
				String codiceQualifica = helper.getImpresa().getAltreCaricheImpresa().get(firmatarioCorrente.getIndex()).getSoggettoQualifica();
				qualificaFirmatario.add(action.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
							 .get(codiceQualifica));
			}
		}	
		
		return qualificaFirmatario;
	}
	
	/**
	 * Restituisce il firmatario di un componente RTI
	 * Utilizzato nella pagina jsp in sostituzione a "tempHash"
	 */
	public SoggettoImpresaHelper getFirmatario(IComponente componenteRTI) {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper)this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		return (helper != null 
				? this.tempHash.get(helper.getComponentiRTI().getChiave(componenteRTI))
		        : null);
	}

}
