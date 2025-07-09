package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.User;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.WithError;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import org.apache.commons.lang.StringUtils;

/**
 * Action di gestione dell'apertura della pagina dei dati principali
 * dell'impresa nel wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.MODIFICA_IMPRESA, EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA,
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class OpenPageDatiPrincipaliImpresaAction extends AbstractOpenPageAction
				implements IDatiPrincipaliImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4560570634863203786L;

	protected ICodificheManager _codificheManager;
	protected ICustomConfigManager customConfigManager;
	protected DatiImpresaChecker datiImpresaChecker;

	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.NATURA_GIURIDICA)
	private String naturaGiuridica;
	@Validate(EParamValidation.TIPO_IMPRESA)
	private String tipoImpresa;
	@Validate(EParamValidation.AMBITO_TERRITORIALE)
	private String ambitoTerritoriale;		// 1=operatore italiano, 2=operaore UE, 3=operatore extra UE
	@Validate(value = EParamValidation.CODICE_FISCALE_O_IDENTIFICATIVO, error = @WithError(fieldLabel = "CODICE_FISCALE_O_IDENTIFICATIVO"))
	private String codiceFiscale;
	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIVA;
	@Validate(EParamValidation.SI_NO)
	private String microPiccolaMediaImpresa;
	@Validate(EParamValidation.INDIRIZZO)
	private String indirizzoSedeLegale;
	@Validate(EParamValidation.NUM_CIVICO)
	private String numCivicoSedeLegale;
	@Validate(EParamValidation.CAP)
	private String capSedeLegale;
	@Validate(EParamValidation.COMUNE)
	private String comuneSedeLegale;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaSedeLegale;
	@Validate(EParamValidation.NAZIONE)
	private String nazioneSedeLegale;
	@Validate(EParamValidation.SITO_WEB)
	private String sitoWeb;
	private boolean liberoProfessionista;
	@Validate(EParamValidation.TELEFONO)
	private String telefonoRecapito;
	@Validate(EParamValidation.FAX)
	private String faxRecapito;
	@Validate(EParamValidation.TELEFONO)
	private String cellulareRecapito;
	@Validate(EParamValidation.EMAIL)
	private String emailRecapito;
	@Validate(EParamValidation.EMAIL)
	private String emailPECRecapito;
	@Validate(EParamValidation.EMAIL)
	private String emailRecapitoConferma;
	@Validate(EParamValidation.EMAIL)
	private String emailPECRecapitoConferma;
	@Validate(EParamValidation.SI_NO)
	private String vatGroup;
	@Validate(EParamValidation.TIPO_SOCIETA_COOPERATIVA)
	private String tipoSocietaCooperativa;

	//
	private boolean readonlyIsVatGroup;
	private boolean readonlyPartitaIva;
	private boolean readonlyRagioneSociale;
	private boolean readonlyCf;
	private boolean gruppoIvaAbilitato;
	
	// campi di controllo per informazioni teoricamente obbligatorie ma che nel
	// caso di backoffice con dati parziali, almeno la prima volta potrebbero
	// arrivare incompleti
	private boolean readonlyNaturaGiuridica;
	private boolean readonlyTipoImpresa;
	private boolean isItalianOnly;

	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setDatiImpresaChecker(DatiImpresaChecker datiImpresaChecker) {
		this.datiImpresaChecker = datiImpresaChecker;
	}

	@Override
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	@Override
	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	@Override
	public String getNaturaGiuridica() {
		return naturaGiuridica;
	}

	@Override
	public void setNaturaGiuridica(String naturaGiuridica) {
		this.naturaGiuridica = naturaGiuridica;
	}

	@Override
	public String getTipoImpresa() {
		return tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}
	
	@Override
	public String getAmbitoTerritoriale() {
		return ambitoTerritoriale;
	}

	@Override
	public void setAmbitoTerritoriale(String ambitoTerritoriale) {
		this.ambitoTerritoriale = ambitoTerritoriale;
	}

	@Override
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	@Override
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Override
	public String getPartitaIVA() {
		return partitaIVA;
	}

	@Override
	public void setPartitaIVA(String partitaIVA) {
		this.partitaIVA = partitaIVA;
	}
	
	@Override
	public String getIdAnagraficaEsterna() {
		WizardDatiImpresaHelper helper = getSessionHelper();
		return (helper != null ? helper.getDatiPrincipaliImpresa().getIdAnagraficaEsterna() : null);
	}

	@Override
	public void setIdAnagraficaEsterna(String idAnagraficaEsterna) {
		// NON VIENE GESTITO COME INPUT A VIDEO
	}

	@Override
	public String getMicroPiccolaMediaImpresa() {
		return this.microPiccolaMediaImpresa;
	}

	@Override
	public void setMicroPiccolaMediaImpresa(String microPiccolaMediaImpresa) {
		this.microPiccolaMediaImpresa = microPiccolaMediaImpresa;		
	}

	@Override
	public String getIndirizzoSedeLegale() {
		return indirizzoSedeLegale;
	}

	@Override
	public void setIndirizzoSedeLegale(String indirizzoSedeLegale) {
		this.indirizzoSedeLegale = indirizzoSedeLegale;
	}

	@Override
	public String getNumCivicoSedeLegale() {
		return numCivicoSedeLegale;
	}

	@Override
	public void setNumCivicoSedeLegale(String numCivicoSedeLegale) {
		this.numCivicoSedeLegale = numCivicoSedeLegale;
	}

	@Override
	public String getCapSedeLegale() {
		return capSedeLegale;
	}

	@Override
	public void setCapSedeLegale(String capSedeLegale) {
		this.capSedeLegale = capSedeLegale;
	}

	@Override
	public String getComuneSedeLegale() {
		return comuneSedeLegale;
	}

	@Override
	public void setComuneSedeLegale(String comuneSedeLegale) {
		this.comuneSedeLegale = comuneSedeLegale;
	}

	@Override
	public String getProvinciaSedeLegale() {
		return provinciaSedeLegale;
	}
	
	@Override
	public void setProvinciaSedeLegale(String provinciaSedeLegale) {
		this.provinciaSedeLegale = provinciaSedeLegale;
	}

	@Override
	public String getNazioneSedeLegale() {
		return nazioneSedeLegale;
	}

	@Override
	public void setNazioneSedeLegale(String nazioneSedeLegale) {
		this.nazioneSedeLegale = nazioneSedeLegale;
	}

	@Override
	public String getSitoWeb() {
		return sitoWeb;
	}

	@Override
	public void setSitoWeb(String sitoWeb) {
		this.sitoWeb = sitoWeb;
	}

	@Override
	public String getTelefonoRecapito() {
		return telefonoRecapito;
	}

	@Override
	public void setTelefonoRecapito(String telefonoRecapito) {
		this.telefonoRecapito = telefonoRecapito;
	}

	@Override
	public String getFaxRecapito() {
		return faxRecapito;
	}

	@Override
	public void setFaxRecapito(String faxRecapito) {
		this.faxRecapito = faxRecapito;
	}

	@Override
	public String getCellulareRecapito() {
		return cellulareRecapito;
	}

	@Override
	public void setCellulareRecapito(String cellulareRecapito) {
		this.cellulareRecapito = cellulareRecapito;
	}

	@Override
	public String getEmailRecapito() {
		return emailRecapito;
	}

	@Override
	public void setEmailRecapito(String emailRecapito) {
		this.emailRecapito = emailRecapito;
	}

	@Override
	public String getEmailPECRecapito() {
		return this.emailPECRecapito;
	}

	@Override
	public void setEmailPECRecapito(String emailPECRecapito) {
		this.emailPECRecapito = emailPECRecapito;
	}

	public String getEmailRecapitoConferma() {
		return emailRecapitoConferma;
	}

	public void setEmailRecapitoConferma(String emailRecapitoConferma) {
		this.emailRecapitoConferma = emailRecapitoConferma;
	}

	public String getEmailPECRecapitoConferma() {
		return emailPECRecapitoConferma;
	}

	public void setEmailPECRecapitoConferma(String emailPECRecapitoConferma) {
		this.emailPECRecapitoConferma = emailPECRecapitoConferma;
	}

	public boolean isReadonlyNaturaGiuridica() {
		return readonlyNaturaGiuridica;
	}

	public boolean isReadonlyTipoImpresa() {
		return readonlyTipoImpresa;
	}

	public boolean isReadonlyPartitaIva() {
		return readonlyPartitaIva;
	}

	public void setReadonlyPartitaIva(boolean readonlyPartitaIva) {
		this.readonlyPartitaIva = readonlyPartitaIva;
	}

	public boolean isReadonlyCf() {
		return readonlyCf;
	}

	public void setReadonlyCf(boolean readonlyCf) {
		this.readonlyCf = readonlyCf;
	}

	public boolean isReadonlyRagioneSociale() {
		return readonlyRagioneSociale;
	}

	public void setReadonlyRagioneSociale(boolean readonlyRagioneSociale) {
		this.readonlyRagioneSociale = readonlyRagioneSociale;
	}

	public boolean isLiberoProfessionista() {
		return liberoProfessionista;
	}

	public void setLiberoProfessionista(boolean liberoProfessionista) {
		this.liberoProfessionista = liberoProfessionista;
	}

	@Override
	public String getVatGroup() {
		return vatGroup;
	}
	@Override
	public void setVatGroup(String vatGroup) {
		this.vatGroup = vatGroup;
	}

	public boolean isReadonlyIsVatGroup() {
		return readonlyIsVatGroup;
	}

	public void setReadonlyIsVatGroup(boolean readonlyIsVataGroup) {
		this.readonlyIsVatGroup = readonlyIsVataGroup;
	}
	
	
	public boolean isGruppoIvaAbilitato() {
		return gruppoIvaAbilitato;
	}

	public void setGruppoIvaAbilitato(boolean gruppoIvaAbilitato) {
		this.gruppoIvaAbilitato = gruppoIvaAbilitato;
	}

	@Override
	public String getTipoSocietaCooperativa() {
		return tipoSocietaCooperativa;
	}
	@Override
	public void setTipoSocietaCooperativa(String tipoSocietaCooperativa) {
		this.tipoSocietaCooperativa = tipoSocietaCooperativa;
	}

	public boolean isItalianOnly() {
		return isItalianOnly;
	}

	public void setItalianOnly(boolean isItalianOnly) {
		this.isItalianOnly = isItalianOnly;
	}
	/**
	 * ...
	 */
	@Override
	public String openPage() {
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
//			if(this.getCurrentUser().getUsername().equals(session.get(SystemConstants.SESSION_DATI_IMPRESA))) {
//				this.addActionError(this.getText("Errors.datiImpresaIncompleti"));
//			}

			// registrazione online (standard)
			
			// aggiorna i dati nel bean a partire da quelli presenti in sessione
			OpenPageDatiPrincipaliImpresaAction.synchronizeDatiPrincipaliImpresa(
					helper.getDatiPrincipaliImpresa(), 
					this);
			this.setReadonlyFields(helper.getDatiPrincipaliImpresa());
			try {
				User currentUser = (User) session.get(SystemConstants.SESSIONPARAM_CURRENT_USER);
				if (customConfigManager.isActiveFunction("REG-IMPRESA", "SOLOESTERE")
					&& session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO) == null
					&& (currentUser == null
						||StringUtils.equals(currentUser.getUsername(), SystemConstants.GUEST_USER_NAME))) {
					//Se sono loggato come spid o come utente normale, devo visualizzare il valore effettivo, quindi, non entro.
					//Chi effettua l'accesso con SSO pu\u00F2 essere operatore italiano
					enableOnlyForeigners();
				}
			} catch (Exception e) {
				ApsSystemUtils.getLogger().error("openPage", e);
			}
			
			// verifica se in BO e' stata abilitata la gestione del gruppo IVA
			this.gruppoIvaAbilitato = this.getMaps().get(InterceptorEncodedData.CHECK_GRUPPO_IVA).containsValue("1");
			
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PAGINA_DATI_IMPRESA);
		}
		return this.getTarget();
	}

	private void enableOnlyForeigners() {
		ambitoTerritoriale = "2";	//La chiave corrispondente al'estero sull'interceptor � definita con 2.
	}

	/**
	 * ... 
	 */
	public String openPageAfterError() {
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.setReadonlyFields(helper.getDatiPrincipaliImpresa());

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PAGINA_DATI_IMPRESA);
		}

		return this.getTarget();
	}
	
	private void setReadonlyFields(IDatiPrincipaliImpresa impresa) {

		WizardDatiPrincipaliImpresaHelper impresaHelper = (WizardDatiPrincipaliImpresaHelper) impresa;
		this.readonlyNaturaGiuridica = impresaHelper.isReadonlyNaturaGiuridica();
		this.readonlyTipoImpresa = impresaHelper.isReadonlyTipoImpresa();
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		if(registrazioneHelper != null && 
				PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(registrazioneHelper.getTipologiaRegistrazione())){
			this.addActionMessage(this.getText("Warning.noSpidBusinessAccountForceReg"));
			
			this.readonlyPartitaIva = true;
			this.readonlyRagioneSociale = true;
			this.readonlyCf = true;
//			this.readonlyIsVatGroup = true;
			isItalianOnly = true;
			ambitoTerritoriale = "1";

		} else {
			this.readonlyPartitaIva = false;
			this.readonlyRagioneSociale = false;
			this.readonlyCf = false;
			this.readonlyIsVatGroup = false;
		}
		
		if (StringUtils.isNotBlank(this.getTipoImpresa())) {
			this.setLiberoProfessionista(this._codificheManager
							.getCodifiche()
							.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
							.containsKey(this.getTipoImpresa()));
		}
	}

	/**
	 * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
	 *
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		return helper;
	}

	/**
	 * Sincronizza i dati tra l'oggetto sorgente e l'oggetto di destinazione
	 *
	 * @param from oggetto sorgente
	 * @param to oggetto destinazione
	 */
	public static void synchronizeDatiPrincipaliImpresa(IDatiPrincipaliImpresa from, IDatiPrincipaliImpresa to) {

		to.setRagioneSociale(from.getRagioneSociale());
		to.setNaturaGiuridica(from.getNaturaGiuridica());
		to.setTipoImpresa(from.getTipoImpresa());
		to.setAmbitoTerritoriale(from.getAmbitoTerritoriale());
		to.setCodiceFiscale(from.getCodiceFiscale());
		to.setPartitaIVA(StringUtils.stripToNull(from.getPartitaIVA()));
		to.setMicroPiccolaMediaImpresa(from.getMicroPiccolaMediaImpresa());

		to.setIndirizzoSedeLegale(from.getIndirizzoSedeLegale());
		to.setNumCivicoSedeLegale(from.getNumCivicoSedeLegale());
		to.setCapSedeLegale(from.getCapSedeLegale());
		to.setComuneSedeLegale(from.getComuneSedeLegale());
		to.setProvinciaSedeLegale(StringUtils.stripToNull(from.getProvinciaSedeLegale()));
		to.setNazioneSedeLegale(from.getNazioneSedeLegale());
		to.setSitoWeb(from.getSitoWeb());
		to.setTelefonoRecapito(from.getTelefonoRecapito());
		to.setFaxRecapito(from.getFaxRecapito());
		to.setCellulareRecapito(StringUtils.stripToNull(from.getCellulareRecapito()));
		to.setEmailRecapito(from.getEmailRecapito());
		to.setEmailPECRecapito(from.getEmailPECRecapito());
		to.setEmailRecapitoConferma(from.getEmailRecapitoConferma());
		to.setEmailPECRecapitoConferma(from.getEmailPECRecapitoConferma());
		to.setVatGroup(from.getVatGroup());
		to.setTipoSocietaCooperativa(from.getTipoSocietaCooperativa());
	}

}
