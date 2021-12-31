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
	private String nazione;
	private String codiceFiscale;
	private String partitaIVA;
	private Double quota;
	private String tipoImpresa;

	public ComponenteHelper() {
		this.ragioneSociale = null;
		this.nazione = "Italia";
		this.codiceFiscale = null;
		this.partitaIVA = null;
		this.quota = null;
		this.tipoImpresa = null;
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
	public Double getQuota() {
		return quota;
	}

	@Override
	public void setQuota(Double quota) {
		this.quota = quota;
	}

	@Override
	public String getTipoImpresa() {
		return this.tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}
	
}
