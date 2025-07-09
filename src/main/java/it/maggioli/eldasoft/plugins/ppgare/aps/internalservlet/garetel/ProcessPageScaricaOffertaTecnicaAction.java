package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageScaricaOffertaTecnicaAction extends AbstractProcessPageAction { 
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7145413932901378087L;
	
	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;

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


	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
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

	@SkipValidation
	public String editFirmatarioMandataria() {
		return "modifyFirmatarioMandataria";
	}

	@SkipValidation
	public String editFirmatarioMandante() {
		return "modifyFirmatarioMandante";
	}

	/**
	 * costruttore 
	 */
	public ProcessPageScaricaOffertaTecnicaAction() {
		super(BustaGara.BUSTA_TECNICA, //PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA,
			  WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA); 
	} 

	/**
	 * ... 
	 */
	@SkipValidation
	public String saveFirmatarioMandataria() {
		String target = "saveFirmatarioMandataria";

		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
		
		// verifica se action e dati in sessione sono sincronizzati...
		if(!helper.isSynchronizedToAction(this.codice, this)) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
		
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		
		boolean mandatariaTrovata = false;
		for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !mandatariaTrovata; i++) {
			ISoggettoImpresa soggettoFromLista = null;
			
			String listaFirmatarioSelezionato = StringUtils.substring(
					this.firmatarioSelezionato, 0, this.firmatarioSelezionato.indexOf("-"));
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

		// CF,PIVA impresa 
		firmatario.setCodiceFiscaleImpresa(datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale());
		firmatario.setPartitaIvaImpresa(datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA());

		// aggiungi il firmatario all'helper...
		helper.getComponentiRTI().addFirmatario(datiImpresaHelper.getDatiPrincipaliImpresa(), firmatario);

		helper.setDatiModificati(true);
		helper.deleteDocumentoOffertaTecnica(this, this.eventManager);
		helper.setRigenPdf(true);
		helper.setPdfUUID(null);
		
		return target;
	}

	/**
	 * ... 
	 */
	public String saveFirmatarioMandante() {
		String target = "saveFirmatarioMandante";

		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
		
		// verifica se action e dati in sessione sono sincronizzati...
		if( !helper.isSynchronizedToAction(this.codice, this) ) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		firmatario.setNominativo(this.getCognome() + " " +this.getNome());

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
		
		helper.setDatiModificati(true);
		helper.deleteDocumentoOffertaTecnica(this, this.eventManager);
		helper.setRigenPdf(true);
		helper.setPdfUUID(null);
		
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String next() {
		String target = this.helperNext();

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{	
			WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();

			if ( !helper.isRigenPdf() ) {
				this.nextResultAction = helper.getNextAction(this.currentStep);
			} else {
				this.addActionError(this.getText("Errors.offertaTecnica.scaricaPDF"));
				target = INPUT;
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
	@SkipValidation
	public String back() {
		String target = SUCCESS;	//= this.helperBack();

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();

			this.nextResultAction = helper.getPreviousAction(this.currentStep);
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
			BustaTecnica bustaTec = GestioneBuste.getBustaTecnicaFromSession();
			WizardOffertaTecnicaHelper helper = bustaTec.getHelper();
			
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice, this)) {
				return CommonSystemConstants.PORTAL_ERROR;
			} 

			try {
				bustaTec.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
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

			this.nextResultAction = helper.getCurrentAction(this.currentStep);
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
