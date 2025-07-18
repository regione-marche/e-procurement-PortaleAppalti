package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageScaricaOffertaAction extends AbstractProcessPageAction { 
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8947382712238231089L;

	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;
	private ICustomConfigManager customConfigManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	protected String nextResultAction;
	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;

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
	@Validate(EParamValidation.PROVINCIA)
	private String provincia;
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.GENERIC)
	private String soggettoQualifica; 
	private boolean obbQualifica;
	@Validate(EParamValidation.PASS_OE)
	private String passoe;

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	@SkipValidation
	public String getNextResultAction() {
		return nextResultAction;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
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
	
	public String getPassoe() {
		return passoe;
	}

	public void setPassoe(String passoe) {
		this.passoe = passoe;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String editFirmatarioMandataria(){
		return "modifyFirmatarioMandataria";
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String editFirmatarioMandante(){
		return "modifyFirmatarioMandante";
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String saveFirmatarioMandataria(){
		String target = "saveFirmatarioMandataria";

		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
		
		// verifica se action e dati in sessione sono sincronizzati...
		if(!helper.isSynchronizedToAction(this.codice, this)) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		helper.saveFirmatarioMandataria(this.firmatarioSelezionato);
		
		helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
		
		return target;
	}

	/**
	 * ... 
	 */
	public String saveFirmatarioMandante() {
		String target = "saveFirmatarioMandante";

		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
		
		// verifica se action e dati in sessione sono sincronizzati...
		if( !helper.isSynchronizedToAction(this.codice, this) ) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
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
	
		// CF,PIVA impresa 
		IComponente componente = helper.getComponentiRTI().get(Integer.parseInt(this.id));
		String cf = ("2".equals(componente.getAmbitoTerritoriale()) 
					 ? componente.getIdFiscaleEstero() 
					 : componente.getCodiceFiscale());
		firmatario.setCodiceFiscaleImpresa(cf);
		firmatario.setPartitaIvaImpresa(componente.getPartitaIVA());

		helper.getComponentiRTI().addFirmatario(componente, firmatario);
		
		helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
		
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	@SkipValidation
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
				WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
	
				// verifica se action e dati in sessione sono sincronizzati...
				if(!helper.isSynchronizedToAction(this.codice, this)) {
					return CommonSystemConstants.PORTAL_ERROR;
				} 

				// verifica la validit� del "PassOE", se � utilizzato
				// Il PASSOE deve essere inserito e composto al massimo da 30 caratteri
				if (this.customConfigManager.isVisible("GARE-DOCUMOFFERTA", "PASSOE")) {
					helper.setPassoe(this.passoe);
					if(StringUtils.isEmpty(this.passoe)) {
						this.addActionError(this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("passoe") }));
						target = ERROR;
					}
				}
				
				// verifica se va rigenerato il PDF
				if (helper.isRigenPdf()) {
					this.addActionError(this.getText("Errors.offertaTelematica.scaricaPDF"));
					target = ERROR;
				}
				
				if(SUCCESS.equals(target)) {
					this.nextResultAction = helper.getNextAction(WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);
				}
				
//				this.session.put(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA, helper);
				bustaEco.putToSession();
				
			} catch (Throwable e) {
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
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
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
			WizardOffertaEconomicaHelper helper = bustaEco.getHelper();

			this.nextResultAction = helper.getPreviousAction(WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	@SkipValidation
	public String save() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
			WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
			
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice, this)) {
				return CommonSystemConstants.PORTAL_ERROR;
			} 

			try {
				bustaEco.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				helper.setDatiModificati(false);
				
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				ExceptionUtils.manageExceptionError(t, this);
				target = ERROR;
			} catch (GeneralSecurityException e) {
				ApsSystemUtils.logThrowable(e, this, "save");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			} catch (IOException t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
				target = ERROR;
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "save");
				this.addActionError(this.getText("Errors.save.outOfMemory"));
				target = INPUT;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				ExceptionUtils.manageExceptionError(t, this);
				target = ERROR;
			}

			this.nextResultAction = helper.getCurrentAction(WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

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
		if (!"".equals(this.getCodiceFiscale())
			&& !UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), "ITALIA".equalsIgnoreCase(this.getNazione()))) 
		{
			this.addFieldError("codiceFiscale",
					this.getText("Errors.wrongField", new String[] {this.getTextFromDB("codiceFiscale")}));
		}
	}
	
}
