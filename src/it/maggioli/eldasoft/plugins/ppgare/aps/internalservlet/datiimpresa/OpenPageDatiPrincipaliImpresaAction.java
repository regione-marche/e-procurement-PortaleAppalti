package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import org.apache.commons.lang.StringUtils;

/**
 * Action di gestione dell'apertura della pagina dei dati principali
 * dell'impresa nel wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class OpenPageDatiPrincipaliImpresaAction extends AbstractOpenPageAction
				implements IDatiPrincipaliImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4560570634863203786L;

	private String ragioneSociale;
	private String naturaGiuridica;
	private String tipoImpresa;
	private String ambitoTerritoriale;		// 1=operatore italiano, 2=operaore UE, 3=operatore extra UE
	private String codiceFiscale;
	private String partitaIVA;

	private String microPiccolaMediaImpresa;

	private String indirizzoSedeLegale;
	private String numCivicoSedeLegale;
	private String capSedeLegale;
	private String comuneSedeLegale;
	private String provinciaSedeLegale;
	private String nazioneSedeLegale;
	private String sitoWeb;
	private boolean liberoProfessionista;
	private String telefonoRecapito;
	private String faxRecapito;
	private String cellulareRecapito;
	private String emailRecapito;
	private String emailPECRecapito;
	private String emailRecapitoConferma;
	private String emailPECRecapitoConferma;

	// campi di controllo per informazioni teoricamente obbligatorie ma che nel
	// caso di backoffice con dati parziali, almeno la prima volta potrebbero
	// arrivare incompleti
	private boolean readonlyNaturaGiuridica;
	private boolean readonlyTipoImpresa;
	
	private ICodificheManager _codificheManager;

	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
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

	public boolean isLiberoProfessionista() {
		return liberoProfessionista;
	}

	public void setLiberoProfessionista(boolean liberoProfessionista) {
		this.liberoProfessionista = liberoProfessionista;
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

			// registrazione online (standard)
			
			// aggiorna i dati nel bean a partire da quelli presenti in sessione
			OpenPageDatiPrincipaliImpresaAction
							.synchronizeDatiPrincipaliImpresa(helper
											.getDatiPrincipaliImpresa(), this);
			this.setReadonlyFields(helper.getDatiPrincipaliImpresa());

			this.session.put(
					PortGareSystemConstants.SESSION_ID_PAGINA,
					PortGareSystemConstants.WIZARD_PAGINA_DATI_IMPRESA);
		}
		return this.getTarget();
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

			this.session.put(
					PortGareSystemConstants.SESSION_ID_PAGINA,
					PortGareSystemConstants.WIZARD_PAGINA_DATI_IMPRESA);
		}

		return this.getTarget();
	}
	
	private void setReadonlyFields(IDatiPrincipaliImpresa impresa) {

		WizardDatiPrincipaliImpresaHelper impresaHelper = (WizardDatiPrincipaliImpresaHelper) impresa;
		this.readonlyNaturaGiuridica = impresaHelper.isReadonlyNaturaGiuridica();
		this.readonlyTipoImpresa = impresaHelper.isReadonlyTipoImpresa();
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
	}
	
}
