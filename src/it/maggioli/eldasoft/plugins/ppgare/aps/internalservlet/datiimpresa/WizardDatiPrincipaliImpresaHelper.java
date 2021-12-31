package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.sil.portgare.datatypes.ImpresaAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati principali dell'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class WizardDatiPrincipaliImpresaHelper implements IDatiPrincipaliImpresa {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -7332889117241790543L;

	/* dati impresa */
    private String ragioneSociale;
    private String naturaGiuridica;
    private String tipoImpresa;
    private String ambitoTerritoriale;
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

    public WizardDatiPrincipaliImpresaHelper() {
		this.ragioneSociale = null;
		this.naturaGiuridica = null;
		this.tipoImpresa = null;
		this.ambitoTerritoriale = null;		// 1=operatore eco italiano (default)
		this.codiceFiscale = null;
		this.partitaIVA = null;

		this.microPiccolaMediaImpresa = null;
	
		this.indirizzoSedeLegale = null;
		this.numCivicoSedeLegale = null;
		this.capSedeLegale = null;
		this.comuneSedeLegale = null;
		this.provinciaSedeLegale = null;
		this.nazioneSedeLegale = "Italia";
		
		this.sitoWeb = null;
	
		this.telefonoRecapito = null;
		this.faxRecapito = null;
		this.cellulareRecapito = null;
		this.emailRecapito = null;
		this.emailPECRecapito = null;
		this.emailRecapitoConferma = null;
		this.emailPECRecapitoConferma = null;
	
		this.readonlyNaturaGiuridica = false;
		this.readonlyTipoImpresa = false;
    }

    public WizardDatiPrincipaliImpresaHelper(ImpresaAggiornabileType impresa) {
		this.ragioneSociale = impresa.getRagioneSociale();
		this.naturaGiuridica = Integer.toString(impresa.getNaturaGiuridica());
		this.tipoImpresa = impresa.getTipoImpresa();
		this.ambitoTerritoriale = (impresa.getSedeLegale().getNazione() != null && 
				                   "ITALIA".equals(impresa.getSedeLegale().getNazione().toUpperCase()) ? "1" : "2");
		this.codiceFiscale = impresa.getCodiceFiscale();
		this.partitaIVA = impresa.getPartitaIVA();

		this.microPiccolaMediaImpresa = impresa.getMicroPiccolaMediaImpresa();
	
		this.indirizzoSedeLegale = impresa.getSedeLegale().getIndirizzo();
		this.numCivicoSedeLegale = impresa.getSedeLegale().getNumCivico();
		this.capSedeLegale = impresa.getSedeLegale().getCap();
		this.comuneSedeLegale = impresa.getSedeLegale().getComune();
		this.provinciaSedeLegale = impresa.getSedeLegale().getProvincia();
		this.nazioneSedeLegale = impresa.getSedeLegale().getNazione();
		
		this.sitoWeb = impresa.getSitoWeb();
	
		this.telefonoRecapito = impresa.getRecapiti().getTelefono();
		this.faxRecapito = impresa.getRecapiti().getFax();
		this.cellulareRecapito = impresa.getRecapiti().getCellulare();
		this.emailRecapito = impresa.getRecapiti().getEmail();
		this.emailPECRecapito = impresa.getRecapiti().getPec();
		this.emailRecapitoConferma = this.emailRecapito;
		this.emailPECRecapitoConferma = this.emailPECRecapito;
	
//		this.readonlyNaturaGiuridica = (StringUtils.stripToNull(impresa
//			.getNaturaGiuridica()) != null);
		this.readonlyNaturaGiuridica = (impresa.getNaturaGiuridica() > 0);
		this.readonlyTipoImpresa = (StringUtils.stripToNull(impresa.getTipoImpresa()) != null);
    }

    public WizardDatiPrincipaliImpresaHelper(ImpresaType impresa) {
		this.ragioneSociale = impresa.getRagioneSociale();
		this.naturaGiuridica = impresa.getNaturaGiuridica();
		this.tipoImpresa = impresa.getTipoImpresa();
		this.ambitoTerritoriale = (impresa.getSedeLegale().getNazione() != null && 
								   "ITALIA".equals(impresa.getSedeLegale().getNazione().toUpperCase()) ? "1" : "2");
		this.codiceFiscale = impresa.getCodiceFiscale();
		this.partitaIVA = impresa.getPartitaIVA();

		this.microPiccolaMediaImpresa = impresa.getMicroPiccolaMediaImpresa();
	
		this.indirizzoSedeLegale = impresa.getSedeLegale().getIndirizzo();
		this.numCivicoSedeLegale = impresa.getSedeLegale().getNumCivico();
		this.capSedeLegale = impresa.getSedeLegale().getCap();
		this.comuneSedeLegale = impresa.getSedeLegale().getComune();
		this.provinciaSedeLegale = impresa.getSedeLegale().getProvincia();
		this.nazioneSedeLegale = impresa.getSedeLegale().getNazione();
	
		this.sitoWeb = impresa.getSitoWeb();
	
		this.telefonoRecapito = impresa.getRecapiti().getTelefono();
		this.faxRecapito = impresa.getRecapiti().getFax();
		this.cellulareRecapito = impresa.getRecapiti().getCellulare();
		this.emailRecapito = impresa.getRecapiti().getEmail();
		this.emailPECRecapito = impresa.getRecapiti().getPec();
		this.emailRecapitoConferma = this.emailRecapito;
		this.emailPECRecapitoConferma = this.emailPECRecapito;
	
		this.readonlyNaturaGiuridica = (StringUtils.stripToNull(impresa.getNaturaGiuridica()) != null);
		this.readonlyTipoImpresa = (StringUtils.stripToNull(impresa.getTipoImpresa()) != null);
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

	@Override
	public String getEmailRecapitoConferma() {
		return this.emailRecapitoConferma;
	}

	@Override
	public void setEmailRecapitoConferma(String value) {
		this.emailRecapitoConferma = value;	
	}

	@Override
	public String getEmailPECRecapitoConferma() {
		return this.emailPECRecapitoConferma;	
	}

	@Override
	public void setEmailPECRecapitoConferma(String value) {
		this.emailPECRecapitoConferma = value;
	}  
	
    /**
     * @return the readonlyNaturaGiuridica
     */
    public boolean isReadonlyNaturaGiuridica() {
        return readonlyNaturaGiuridica;
    }

    /**
     * @return the readonlyTipoImpresa
     */
    public boolean isReadonlyTipoImpresa() {
        return readonlyTipoImpresa;
    }

}
