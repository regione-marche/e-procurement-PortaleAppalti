package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.dgue.dto;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Oggetto rappresentante l'impresa, inviato in json al dgue.
 */
public class AnagraficaOE implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2391415230108960113L;

	private String denominazioneRTI;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:EconomicOperatorRole
	 * 
	 * Code	Name
		SCLE	Sole contractor / Lead entity
		GM	Group member
		OERON	Other entity (relied upon)
		OENRON	Other entity (not relied upon)
		SC	Sole contractor
		LE	Lead Entity
	 */
	private String ruolo;
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:PartyIdentification/cbc:ID
	 * 
	 * PIVIMP - ita 
	 * CFIMP - est 
	 * IMPR
	 * */
	private String partitaIva;
	/**
	 *cac:EconomicOperatorParty/cac:Party/cac:PartyName/cbc:Name
	 */
	private String ragioneSociale;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cbc:IndustryClassificationCode
	 * CLADIMP	IMPR
	 * 3	Micro impresa
	 * 2	Piccola impresa
	 * 1	Media impresa
	 * 4	Grande impresa
	 */
	private String codiceClassifica;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:PostalAddress/cac:AddressLine /cbc:Line
	 * INDIMP+NCIIMP	IMPR
	 */
	private String indirizzo;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:PostalAddress/cbc:CityName
	 * LOCIMP	IMPR
	 */
	private String citta;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:PostalAddress/cbc:PostalZone
	 * CAPIMP	IMPR
	 */
	private String cap;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode
	 * NAZIMP	IMPR
	 * da rimappare nel DGUE con apposito CountryCodeIdentifier
	 */
	private String nazione;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cbc:EndPointID
	 */
	private String email;

	private String webSite;
	private String telefono;
	private String codiceFiscale;
	
	/**
	 * cac:EconomicOperatorParty/cac:Party/cac:PowerOfAttorney
	 */
	private List<ISoggettoImpresa> legaleRappresentanti;
	
	/**
	 * cac:TenderingCriterion
	 */
	private List<IComponente> componentiRti;
	
	
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getCodiceClassifica() {
		return codiceClassifica;
	}

	public void setCodiceClassifica(String codiceClassifica) {
		this.codiceClassifica = codiceClassifica;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
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

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
    
    public String getWebSite() {
    	return this.webSite;
    }

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public List<ISoggettoImpresa> getLegaleRappresentanti() {
		return legaleRappresentanti;
	}

	public void setLegaleRappresentanti(ArrayList<ISoggettoImpresa> legaleRappresentanti) {
		this.legaleRappresentanti = legaleRappresentanti;
	}

	public List<IComponente> getComponentiRti() {
		return componentiRti;
	}

	public void setComponentiRti(List<IComponente> componentiRti) {
		this.componentiRti = componentiRti;
	}

	/**
	 * @return the codiceFiscale
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * @param codiceFiscale the codiceFiscale to set
	 */
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getDenominazioneRTI() {
		return denominazioneRTI;
	}

	public void setDenominazioneRTI(String denominazioneRTI) {
		this.denominazioneRTI = denominazioneRTI;
	}

}
