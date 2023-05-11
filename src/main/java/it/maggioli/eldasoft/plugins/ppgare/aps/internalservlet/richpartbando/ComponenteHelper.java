package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.io.Serializable;

/**
 * Helper per la memorizzazione delle componenti di un consorzio o di una RTI.
 *
 * @author Stefano.Sabbadin
 */
public class ComponenteHelper implements Serializable, IComponente {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3030138509069966848L;

	private String ragioneSociale;
	private String tipoImpresa;
	private String ambitoTerritoriale;
	private String nazione;
	private String codiceFiscale;
	private String partitaIVA;
	private String idFiscaleEstero;
	private Double quota;
	
	/**
	 * costruttore
	 */
	public ComponenteHelper() {
		this.ragioneSociale = null;
		this.tipoImpresa = null;
		this.ambitoTerritoriale = null;
		this.nazione = "Italia";
		this.codiceFiscale = null;
		this.partitaIVA = null;
		this.idFiscaleEstero = null;
		this.quota = null;
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
	public String getTipoImpresa() {
		return this.tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	@Override
	public String getAmbitoTerritoriale() {
		return this.ambitoTerritoriale;
	}

	@Override
	public void setAmbitoTerritoriale(String ambitoTerritoriale) {
		this.ambitoTerritoriale = ambitoTerritoriale;
	}

	@Override
	public String getNazione() {
		return nazione;
	}

	@Override
	public void setNazione(String nazione) {
		this.nazione = nazione;
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
	public String getIdFiscaleEstero() {
		return this.idFiscaleEstero;
	}

	@Override
	public void setIdFiscaleEstero(String idFiscaleEstero) {
		this.idFiscaleEstero = idFiscaleEstero;
	}
	
	@Override
	public Double getQuota() {
		return quota;
	}

	@Override
	public void setQuota(Double quota) {
		this.quota = quota;
	}

}
