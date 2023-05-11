package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * Action di gestione delle operazioni nella pagina di assegnazione dei firmatari 
 * per l'offerta dell'asta.
 *
 * @author ...
 */
public class ProcessPageFirmatariAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5575179718218487215L;
	
	private ICodificheManager codificheManager;
	private IEventManager eventManager;
	
	@Validate(EParamValidation.FIRMATARIO)
	private String firmatarioSelezionato;
	@Validate(EParamValidation.DIGIT)
	private String id;
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
	@Validate(EParamValidation.PROGRESSIVO_INVIO)
	private String provincia;
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.GENERIC)
	private String soggettoQualifica; 
	private boolean obbQualifica;
	
	
	public ICodificheManager getCodificheManager() {
		return codificheManager;
	}

	public void setCodificheManager(ICodificheManager codificheManager) {
		this.codificheManager = codificheManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(String firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public boolean isObbQualifica() {
		return obbQualifica;
	}

	public void setObbQualifica(boolean obbQualifica) {
		this.obbQualifica = obbQualifica;
	}

	/**
	 * ... 
	 */
	public ProcessPageFirmatariAction() {
		super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA, 
		 	  WizardOffertaAstaHelper.STEP_FIRMATARI,
		 	  false); 
	} 

	/**
	 * ... 
	 */
	@Override
	@SkipValidation
	public String next() {
		String target = this.helperNext();
		
		try {
			WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
			
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi...
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				WizardOffertaEconomicaHelper offerta = helper.getOffertaEconomica();
				
				// [LISTA] - [id lista]
				if(this.firmatarioSelezionato != null) {  
					String[] s = this.firmatarioSelezionato.split("-");
					helper.getOffertaEconomica().setIdFirmatarioSelezionatoInLista(
							s.length > 1 ? Integer.parseInt(s[1]) : -1);
				}
				
				// verifica se i tutti firmatari sono stati selezionati/inseriti...
				// - caso di RTI con più firmatari
				// - caso singola impresa con 1 firmatario
				if(helper.getPartecipazione().isRti()) {
					// caso di RTI con mandataria e mandati...
					boolean chiaveFound = true;
					for(int i = 0 ; i < offerta.getComponentiRTI().size() && chiaveFound; i++){
						if(offerta.getComponentiRTI().getFirmatario(offerta.getComponentiRTI().get(i)) == null) {
							chiaveFound = false;
						}
					}
					if(!chiaveFound) {
						this.addActionError(this.getText("Errors.firmatarioRtiNotSet"));
						target = ERROR;
					}
				} else {
					// ditta singola...
					if(this.firmatarioSelezionato == null 
					   && helper.getOffertaEconomica().getFirmatarioSelezionato() == null) {
						this.addActionError(this.getText("Errors.firmatarioNotSet"));
						target = ERROR;
					}
				}
				
				// verifica se è stato generato il pdf...
				if (!helper.isRigenPdf()) {
					target = SUCCESS;
				} else {
					this.addActionError(this.getText("Errors.offertaAsta.scaricaPDF"));
					target = ERROR;
				}
				
				helper.putToSession();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	@SkipValidation
	public String back() {
		String target = this.helperBack();
		return (SUCCESS.equalsIgnoreCase(target) ? "back" : target);
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String editFirmatarioMandataria() {
		return "modifyFirmatarioMandataria";
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String editFirmatarioMandante() {
		return "modifyFirmatarioMandante";
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String saveFirmatarioMandataria() {
		String target = "saveFirmatarioMandataria";
		
		boolean mandatariaTrovata = false;

		WizardOffertaEconomicaHelper helper = this.getFirmatariFromSession();
		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
		
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		
		for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !mandatariaTrovata; i++){
			ISoggettoImpresa soggettoFromLista = null;
			String listaFirmatarioSelezionato = StringUtils.substring(this.firmatarioSelezionato, 0, this.firmatarioSelezionato.indexOf("-"));
			int indiceFirmatarioSelezionato = Integer.parseInt(StringUtils.substring(this.getFirmatarioSelezionato(), this.getFirmatarioSelezionato().indexOf("-")+1, this.getFirmatarioSelezionato().length()));			
			if(listaFirmatarioSelezionato.equals(CataloghiConstants.LISTA_ALTRE_CARICHE)){
				soggettoFromLista = datiImpresaHelper.getAltreCaricheImpresa().get(indiceFirmatarioSelezionato);
			} else if(listaFirmatarioSelezionato.equals(CataloghiConstants.LISTA_DIRETTORI_TECNICI)){
				soggettoFromLista = datiImpresaHelper.getDirettoriTecniciImpresa().get(indiceFirmatarioSelezionato);
			} else {
				soggettoFromLista = datiImpresaHelper.getLegaliRappresentantiImpresa().get(indiceFirmatarioSelezionato);
			}

			if(helper.getListaFirmatariMandataria().get(i).getNominativo().equalsIgnoreCase(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome()))
				mandatariaTrovata = true;

			firmatario = (SoggettoFirmatarioImpresaHelper) soggettoFromLista;
			firmatario.setNominativo(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome());

			helper.setIdFirmatarioSelezionatoInLista(i);
		}

		helper.getComponentiRTI().addFirmatario(datiImpresaHelper.getDatiPrincipaliImpresa(), firmatario);
		
		helper.setDatiModificati(true);
		helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
		helper.setRigenPdf(true);
		helper.setPdfUUID(null);
		
		return target;
	}

	/**
	 * ... 
	 */
	public String saveFirmatarioMandante() {
		String target = "saveFirmatarioMandante";
		
		WizardOffertaEconomicaHelper helper = this.getFirmatariFromSession();
		
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

		IComponente componente = helper.getComponentiRTI().get(Integer.parseInt(this.id));
		String cf = ("2".equals(componente.getAmbitoTerritoriale()) 
				 ? componente.getIdFiscaleEstero() 
				 : componente.getCodiceFiscale());
		firmatario.setCodiceFiscaleImpresa(cf);
		firmatario.setPartitaIvaImpresa(componente.getPartitaIVA());
		
		helper.getComponentiRTI().addFirmatario(componente, firmatario);
		
		helper.setDatiModificati(true);
		helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
		helper.setRigenPdf(true);
		helper.setPdfUUID(null);
		
		return target;
	}

	/**
	 * ... 
	 */
	public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		if (this.getCodiceFiscale() != null 
			&& !"".equals(this.getCodiceFiscale())
			&& !UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), "ITALIA".equalsIgnoreCase(this.getNazione()))) {
			this.addFieldError(
					"codiceFiscale",
					this.getText("Errors.wrongField", new String[] {this.getTextFromDB("codiceFiscale")}));
		}
	}

	/**
	 * ... 
	 */
	private WizardOffertaEconomicaHelper getFirmatariFromSession() { 
		WizardOffertaEconomicaHelper helper = null;
		WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession(); 
		if(helperAsta != null) {
			helper = helperAsta.getOffertaEconomica();
		}
		return helper;
	}

}
