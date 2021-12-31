package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * Action di apertura della pagina di gestione ...
 *
 * @author ...
 */
public class OpenPageFirmatariAction extends AbstractOpenPageAction {
    /**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	private ICodificheManager codificheManager;
	private IBandiManager bandiManager;
	
	private String firmatarioSelezionato;
	private IComponente currentMandante;

	private ArrayList<FirmatarioBean> listaFirmatari = new ArrayList<FirmatarioBean>();

	private String id;

	private boolean listaImpreseVisible;
	private boolean modificaFirmatarioVisible;
	private boolean listaFirmatariMandatariaVisible;
	private boolean inputFirmatarioMandanteVisible;
	private boolean allFirmatariInseriti;
	private boolean mandanteLiberoProfessionista;
	private String tipoImpresaCodifica;
	private List<String> tipoQualificaCodifica;
	
	private String codiceFiscale;
	private String nome;
	private String cognome;
	private String dataNascita;
	private String comuneNascita;
	private String provinciaNascita;
	private String sesso;
	private String comune;
	private String indirizzo;
	private String numCivico;
	private String cap;
	private String provincia;
	private String nazione;
	private String soggettoQualifica; 

	// Hash temporanea per la visualizzazione degli errori senza inficiare la visualizzazione dei dati dei firmatari corretti
	private HashMap<String, SoggettoImpresaHelper> tempHash = new HashMap <String,SoggettoImpresaHelper> (); 
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	private String page;


	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public ICodificheManager getCodificheManager() {
		return codificheManager;
	}

	public void setCodificheManager(ICodificheManager manager) {
		this.codificheManager = manager;
	}
	
	public String getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(String firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public boolean isAllFirmatariInseriti() {
		return allFirmatariInseriti;
	}

	public void setAllFirmatariInseriti(boolean allFirmatariInseriti) {
		this.allFirmatariInseriti = allFirmatariInseriti;
	}

	public boolean isMandanteLiberoProfessionista() {
		return mandanteLiberoProfessionista;
	}

	public void setMandanteLiberoProfessionista(boolean mandanteLiberoProfessionista) {
		this.mandanteLiberoProfessionista = mandanteLiberoProfessionista;
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

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
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

	public String getPage() {
		return page;
	}

    public void setPage(String page) {
    	this.page = page;
    }    

    /**
     * ... 
     */
	public String openPage() {
		String target = SUCCESS;
		
		try {
			WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession();
			
			if (helperAsta == null) {
				// la sessione e' scaduta, occorre riconnettersi...
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// la sessione è attiva...
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 		 WizardOffertaAstaHelper.STEP_FIRMATARI);
				
				// metti in sessione i dati dell'offerta, visto che contiene
				// i dati dei firmatari...
				// nel caso di RTI sarà possibile editare il raggruppamento...
				this.session.put(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA, 
								 helperAsta.getOffertaEconomica());
				
				// recupera la decodifica dei tipi di firmatari...
				this.setTipoQualificaCodifica(
						helperAsta.getOffertaEconomica().getImpresa().getTipoQualificaCodifica(
								helperAsta.getOffertaEconomica().getListaFirmatariMandataria(),
								this.getMaps()));
				
				// recupera i dati dei firmatari...
				String codiceGara = (helperAsta.getAsta().getCodiceLotto() != null
					? helperAsta.getAsta().getCodiceLotto() 
					: helperAsta.getAsta().getCodice());
								
				// inizializza i parametri per la gestione della la pagina in 
				// caso di RTI...
				WizardOffertaEconomicaHelper helper = helperAsta.getOffertaEconomica();
				WizardPartecipazioneHelper partecipazione = helperAsta.getPartecipazione();
				WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
									
				this.setListaFirmatariMandatariaVisible(false);
				if(partecipazione.isRti()) {
					// è un RTI...
					// e la mandataria è sempre al I posto in componentiRTI(...)
					this.setListaImpreseVisible(true);
					
					if(helper.getListaFirmatariMandataria().size() == 1) {
						this.setModificaFirmatarioVisible(false);
						
						SoggettoFirmatarioImpresaHelper firmatarioMandataria = new SoggettoFirmatarioImpresaHelper();
						if(!helper.getImpresa().isLiberoProfessionista()) {
							firmatarioMandataria.setNominativo(helper.getListaFirmatariMandataria().get(0).getNominativo());
							if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(helper.getListaFirmatariMandataria().get(0).getLista())) {
								firmatarioMandataria.setNome(helper.getImpresa().getLegaliRappresentantiImpresa().get(helper.getListaFirmatariMandataria().get(0).getIndex()).getNome());
								firmatarioMandataria.setCognome(helper.getImpresa().getLegaliRappresentantiImpresa().get(helper.getListaFirmatariMandataria().get(0).getIndex()).getCognome());
							} else if(CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(helper.getListaFirmatariMandataria().get(0).getLista())) {
								firmatarioMandataria.setNome(helper.getImpresa().getDirettoriTecniciImpresa().get(helper.getListaFirmatariMandataria().get(0).getIndex()).getNome());
								firmatarioMandataria.setCognome(helper.getImpresa().getDirettoriTecniciImpresa().get(helper.getListaFirmatariMandataria().get(0).getIndex()).getCognome());
							} else {
								firmatarioMandataria.setNome(helper.getImpresa().getAltreCaricheImpresa().get(helper.getListaFirmatariMandataria().get(0).getIndex()).getNome());
								firmatarioMandataria.setCognome(helper.getImpresa().getAltreCaricheImpresa().get(helper.getListaFirmatariMandataria().get(0).getIndex()).getCognome());
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
					
					// se tutti i firmatari sono impostati, 
					// passa alla generazione del pdf...
					if(this.isAllFirmatariInseriti()) {
						this.setListaImpreseVisible(false);
					}
				} else {
					this.setListaImpreseVisible(false);
					if(helper.getImpresa().isLiberoProfessionista()) {
						FirmatarioBean firmatario = new FirmatarioBean();
						firmatario.setNominativo(datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale());
						this.getListaFirmatari().add(firmatario);
						this.setAllFirmatariInseriti(true);
					} else {
						ArrayList<String> qualificaFirmatario = getQualificaFirmatario(helper, this);
						this.setTipoQualificaCodifica(qualificaFirmatario);
						this.setAllFirmatariInseriti(true);
					}
				}

				// imposta il firmatario selezionato...
				// viene utilizzato da GenPDFOffertaAstaAction...
				// il valore è una stringa in formato
				//		"[descrizione firmatario] - [id firmatario]"
				String firmatarioSel = null;
				if(helper.getIdFirmatarioSelezionatoInLista() > 0 && 
				   helper.getListaFirmatariMandataria().size() > 0) {
					firmatarioSel = helper.getListaFirmatariMandataria().get(helper.getIdFirmatarioSelezionatoInLista()).getNominativo() + 
							        " - " + helper.getIdFirmatarioSelezionatoInLista(); 
				}				
				this.setFirmatarioSelezionato(firmatarioSel);
				
				// reinserisci l'helper asta in sessione... 
				helperAsta.putToSession();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return target;
	}

	/**
	 * ...
	 */
	public String openPageEditFirmatarioMandataria(){
		String target = SUCCESS;

		WizardOffertaEconomicaHelper helper = this.getFirmatariFromSession();

		this.setListaImpreseVisible(true);
		this.setListaFirmatariMandatariaVisible(true);
		this.setModificaFirmatarioVisible(true);	
		this.setInputFirmatarioMandanteVisible(false);

		ArrayList<String> qualificaFirmatario = getQualificaFirmatario(helper, this);
		this.setTipoQualificaCodifica(qualificaFirmatario);

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaAstaHelper.STEP_FIRMATARI);

		return target;
	}

	/**
	 * ...
	 */
	public String openPageEditFirmatarioMandante(){
		String target = SUCCESS;
		
		WizardOffertaEconomicaHelper helper = this.getFirmatariFromSession();

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
			if(!this.isMandanteLiberoProfessionista()){
				soggetto.setSoggettoQualifica(fromHelper.getSoggettoQualifica());
			}			
		}			
		this.tempHash.put(helper.getComponentiRTI().getChiave(this.currentMandante), soggetto);

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
						 WizardOffertaAstaHelper.STEP_FIRMATARI);

		return target;
	}

	/**
	 * ...
	 */
	public String openPageAfterErrorMandante() {
		WizardOffertaEconomicaHelper helper = this.getFirmatariFromSession();

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

		if(err.isEmpty() || !err.toArray()[0].toString().equals(this.getText("Errors.offertaTelematica.scaricaPDF"))) {
			// ERRORE DA MASCHERA IN INPUT PER MANDANTE
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
			if(helper.getComponentiRTI().size() > 0){
				this.setAllFirmatariInseriti(testPresenzaFirmatari(helper));
			}
		}
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
					     WizardOffertaAstaHelper.STEP_FIRMATARI);

		return this.getTarget();
	}

	/**
	 * ...
	 */
	private boolean testPresenzaFirmatari(WizardOffertaEconomicaHelper helper){
		// NB: in sessione non c'è una bustaRiepilogativa da poter utilizzare...
		boolean chiaveFound = true;
		for(int i = 0 ; i < helper.getComponentiRTI().size() && chiaveFound; i++){
			if(helper.getComponentiRTI().getFirmatario(helper.getComponentiRTI().get(i)) == null) {
				chiaveFound = false;
			}
		}

		return chiaveFound; // && !helper.isDatiModificati();
	}

	/**
	 * ...
	 */
	private WizardOffertaEconomicaHelper getFirmatariFromSession() { 		
		WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession();
		WizardOffertaEconomicaHelper helper = null;
		if(helperAsta != null) {
			helper = helperAsta.getOffertaEconomica();
		}
		return helper;
	}
	
	/**
	 * ...
	 */
	private void putFirmatariToSession(WizardOffertaEconomicaHelper helper) {
		WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession(); 
		if(helperAsta != null) {
			helperAsta.setOffertaEconomica(helper);
		}
		helperAsta.putToSession();
	}
	
	/**
	 * ...
	 */
	private static ArrayList<String> getQualificaFirmatario(
			WizardOffertaEconomicaHelper helper, 
			EncodedDataAction action) 
	{
		ArrayList<String> qualificaFirmatario = new ArrayList<String>();
	
		for(int i = 0; i < helper.getListaFirmatariMandataria().size(); i++) {
			FirmatarioBean firmatarioCorrente = helper.getListaFirmatariMandataria().get(i);
	
			if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioCorrente.getLista())) {
				qualificaFirmatario.add(action.getMaps()
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
							 .get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE +
								  CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			}else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioCorrente.getLista())) {
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
		WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		return (helper != null 
				? this.tempHash.get(helper.getComponentiRTI().getChiave(componenteRTI))
		        : null);
	}

}
