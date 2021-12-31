package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

public class ProcessPageScaricaOffertaAction extends AbstractProcessPageAction { 
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8947382712238231089L;

	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;
	private ICustomConfigManager customConfigManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	protected String nextResultAction;
	private String codice;
	private int operazione;
	
	private String firmatarioSelezionato;
	private String id;
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
	private boolean obbQualifica;
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

	@SkipValidation
	public String editFirmatarioMandataria(){
		return "modifyFirmatarioMandataria";
	}

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

		WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) session
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		
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

		WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) session
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		
		// verifica se action e dati in sessione sono sincronizzati...
		if(!helper.isSynchronizedToAction(this.codice, this)) {
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
		firmatario.setCodiceFiscaleImpresa(componente.getCodiceFiscale());
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
	@Override
	@SkipValidation
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
	
				// verifica se action e dati in sessione sono sincronizzati...
				if(!helper.isSynchronizedToAction(this.codice, this)) {
					return CommonSystemConstants.PORTAL_ERROR;
				} 

				// verifica la validità del "PassOE", se è utilizzato
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
				
				this.session.put(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA, helper);
				
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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) session
					.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);

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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
			
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice, this)) {
				return CommonSystemConstants.PORTAL_ERROR;
			} 

			try {
				// ----- INVIO BUSTA ECONOMICA/TECNICA -----
				ProcessPageDocumentiOffertaAction.sendComunicazioneBusta(
						this, 
						this.comunicazioniManager,
						this.eventManager,
						helper,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				
				// ----- RIALLINEAMENTO BUSTA RIEPILOGATIVA -----
				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				
				if(bustaRiepilogativa.getBustaEconomica() != null) {
					// --- LOTTO UNICO ---
					bustaRiepilogativa.getBustaEconomica().riallineaDocumenti(helper.getDocumenti());					
				}else{
					// --- LOTTI DISTINTI --- 
					bustaRiepilogativa.getBusteEconomicheLotti().get(helper.getCodice()).riallineaDocumenti(helper.getDocumenti());
				}				
				// copia i firmatari della busta nella busta di riepilogo...
				helper.copiaUltimiFirmatariInseriti2Busta(bustaRiepilogativa);				
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA, helper.getDocumenti());

				// ----- INVIO COMUNICAZIONE BUSTA RIEPILOGATIVA -----
				// Preparazione per invio busta riepilogativa
				// ed invia la nuova comunicazione con gli allineamenti del caso
				bustaRiepilogativa.sendComunicazioneBusta(
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA, 
						helper.getImpresa(), 
						this.getCurrentUser().getUsername(), 
						helper.getGara().getCodice(), 
						helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale(), 
						this.comunicazioniManager, 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO,
						this);

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
			&& !UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), "ITALIA".equalsIgnoreCase(this.getNazione()))) {
			this.addFieldError("codiceFiscale",
					this.getText("Errors.wrongField", new String[] {this.getTextFromDB("codiceFiscale")}));
		}
	}
	
}
