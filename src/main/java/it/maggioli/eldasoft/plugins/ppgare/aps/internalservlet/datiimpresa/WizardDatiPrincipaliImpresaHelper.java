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
	 * UID
	 */
	private static final long serialVersionUID = -7332889117241790543L;

	/* dati impresa */
    private String ragioneSociale;
    private String naturaGiuridica;
    private String tipoImpresa;
    private String ambitoTerritoriale;
    private String codiceFiscale;
    private String partitaIVA;
    private String vatGroup;
    
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
	private String tipoSocietaCooperativa;
	
	private String idAnagraficaEsterna; 		// Michelangelo (SACE)
        
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
		this.vatGroup = null;

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
		
		this.idAnagraficaEsterna = null;
	
		this.readonlyNaturaGiuridica = false;
		this.readonlyTipoImpresa = false;
    }

    public WizardDatiPrincipaliImpresaHelper(ImpresaAggiornabileType impresa) {
		ragioneSociale = impresa.getRagioneSociale();
		naturaGiuridica = Integer.toString(impresa.getNaturaGiuridica());
		tipoImpresa = impresa.getTipoImpresa();
		ambitoTerritoriale = (impresa.getSedeLegale().getNazione() != null &&
			                   "ITALIA".equals(impresa.getSedeLegale().getNazione().toUpperCase()) ? "1" : "2");
		codiceFiscale = impresa.getCodiceFiscale();
		partitaIVA = impresa.getPartitaIVA();
		vatGroup = impresa.getGruppoIva();

		microPiccolaMediaImpresa = impresa.getMicroPiccolaMediaImpresa();
	
		indirizzoSedeLegale = impresa.getSedeLegale().getIndirizzo();
		numCivicoSedeLegale = impresa.getSedeLegale().getNumCivico();
		capSedeLegale = impresa.getSedeLegale().getCap();
		comuneSedeLegale = impresa.getSedeLegale().getComune();
		provinciaSedeLegale = impresa.getSedeLegale().getProvincia();
		nazioneSedeLegale = impresa.getSedeLegale().getNazione();
		
		sitoWeb = impresa.getSitoWeb();
	
		telefonoRecapito = impresa.getRecapiti().getTelefono();
		faxRecapito = impresa.getRecapiti().getFax();
		cellulareRecapito = impresa.getRecapiti().getCellulare();
		emailRecapito = impresa.getRecapiti().getEmail();
		emailPECRecapito = impresa.getRecapiti().getPec();
		emailRecapitoConferma = emailRecapito;
		emailPECRecapitoConferma = emailPECRecapito;
		tipoSocietaCooperativa = impresa.getTipoSocietaCooperativa();
	
		idAnagraficaEsterna = (impresa.isSetIdAnagraficaEsterna() ? impresa.getIdAnagraficaEsterna() : null);
		
//		this.readonlyNaturaGiuridica = (StringUtils.stripToNull(impresa
//			.getNaturaGiuridica()) != null);
		readonlyNaturaGiuridica = (impresa.getNaturaGiuridica() > 0);
		readonlyTipoImpresa = (StringUtils.stripToNull(impresa.getTipoImpresa()) != null);
    }

    public WizardDatiPrincipaliImpresaHelper(ImpresaType impresa) {
		ragioneSociale = impresa.getRagioneSociale();
		naturaGiuridica = impresa.getNaturaGiuridica();
		tipoImpresa = impresa.getTipoImpresa();
		ambitoTerritoriale = (impresa.getSedeLegale().getNazione() != null &&
							   "ITALIA".equals(impresa.getSedeLegale().getNazione().toUpperCase()) ? "1" : "2");
		codiceFiscale = impresa.getCodiceFiscale();
		partitaIVA = impresa.getPartitaIVA();
		vatGroup = impresa.getGruppoIva();

		microPiccolaMediaImpresa = impresa.getMicroPiccolaMediaImpresa();
	
		indirizzoSedeLegale = impresa.getSedeLegale().getIndirizzo();
		numCivicoSedeLegale = impresa.getSedeLegale().getNumCivico();
		capSedeLegale = impresa.getSedeLegale().getCap();
		comuneSedeLegale = impresa.getSedeLegale().getComune();
		provinciaSedeLegale = impresa.getSedeLegale().getProvincia();
		nazioneSedeLegale = impresa.getSedeLegale().getNazione();
	
		sitoWeb = impresa.getSitoWeb();
	
		telefonoRecapito = impresa.getRecapiti().getTelefono();
		faxRecapito = impresa.getRecapiti().getFax();
		cellulareRecapito = impresa.getRecapiti().getCellulare();
		emailRecapito = impresa.getRecapiti().getEmail();
		emailPECRecapito = impresa.getRecapiti().getPec();
		emailRecapitoConferma = emailRecapito;
		emailPECRecapitoConferma = emailPECRecapito;
		tipoSocietaCooperativa = impresa.getTipoSocietaCooperativa();
	
		idAnagraficaEsterna = (impresa.isSetIdAnagraficaEsterna() ? impresa.getIdAnagraficaEsterna() : null); 
		
		readonlyNaturaGiuridica = (StringUtils.stripToNull(impresa.getNaturaGiuridica()) != null);
		readonlyTipoImpresa = (StringUtils.stripToNull(impresa.getTipoImpresa()) != null);
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
		return idAnagraficaEsterna;
	}

	@Override
	public void setIdAnagraficaEsterna(String idAnagraficaEsterna) {
		this.idAnagraficaEsterna = idAnagraficaEsterna;
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
	
    public boolean isReadonlyNaturaGiuridica() {
        return readonlyNaturaGiuridica;
    }

    public boolean isReadonlyTipoImpresa() {
        return readonlyTipoImpresa;
    }

	@Override
	public void setVatGroup(String vatGroup) {
		this.vatGroup = vatGroup;
	}

	@Override
	public String getVatGroup() {
		return vatGroup;
	}

	@Override
	public String getTipoSocietaCooperativa() {
		return tipoSocietaCooperativa;
	}

	@Override
	public void setTipoSocietaCooperativa(String tipoSocietaCooperativa) {
		this.tipoSocietaCooperativa = tipoSocietaCooperativa;
	}

}
