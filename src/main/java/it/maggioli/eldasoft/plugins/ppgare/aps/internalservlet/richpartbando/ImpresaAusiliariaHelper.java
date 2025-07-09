package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import it.eldasoft.www.sil.WSGareAppalto.ImpresaAusiliariaType;

/**
 * Helper per la memorizzazione delle imprese ausiliarie di un avvalimento.
 *
 * @author ... 
 */
public class ImpresaAusiliariaHelper implements Serializable, IImpresaAusiliaria {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7772441573023961702L;

	private String ragioneSociale;
	private String tipoImpresa;
	private String ambitoTerritoriale;
	private String nazione;
	private String codiceFiscale;
	private String partitaIVA;
	private String idFiscaleEstero;
	private String avvalimentoPer;
	
	/**
	 * costruttore
	 */
	public ImpresaAusiliariaHelper() {
		this.ragioneSociale = null;
		this.tipoImpresa = null;
		this.ambitoTerritoriale = null;
		this.nazione = "Italia";
		this.codiceFiscale = null;
		this.partitaIVA = null;
		this.idFiscaleEstero = null;
		this.avvalimentoPer = null;
	}

	public ImpresaAusiliariaHelper(IImpresaAusiliaria from) {
		copyTo(from, this);
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
	public String getAvvalimentoPer() {
		return avvalimentoPer;
	}

	@Override
	public void setAvvalimentoPer(String avvalimentoPer) {
		this.avvalimentoPer = avvalimentoPer;
	}

	public static void reset(IImpresaAusiliaria impresaAusiliaria) {
		impresaAusiliaria.setRagioneSociale(null);
		impresaAusiliaria.setTipoImpresa(null);
		impresaAusiliaria.setAmbitoTerritoriale(null);
		impresaAusiliaria.setNazione("Italia");
		impresaAusiliaria.setCodiceFiscale(null);
		impresaAusiliaria.setPartitaIVA(null);
		impresaAusiliaria.setIdFiscaleEstero(null);
		impresaAusiliaria.setAvvalimentoPer(null);
	}

	public static void copyTo(IImpresaAusiliaria from, IImpresaAusiliaria to) {
		to.setRagioneSociale(from.getRagioneSociale());
		to.setTipoImpresa(from.getTipoImpresa());
		to.setAmbitoTerritoriale(from.getAmbitoTerritoriale());
		to.setNazione(from.getNazione());
		to.setCodiceFiscale( StringUtils.isNotEmpty(from.getCodiceFiscale()) ? from.getCodiceFiscale().toUpperCase() : from.getCodiceFiscale() );
		to.setPartitaIVA( StringUtils.isNotEmpty(from.getPartitaIVA()) ? from.getPartitaIVA().toUpperCase() : from.getPartitaIVA() );
		to.setIdFiscaleEstero(  StringUtils.isNotEmpty(from.getIdFiscaleEstero()) ? from.getIdFiscaleEstero().toUpperCase() : from.getIdFiscaleEstero() );
		to.setAvvalimentoPer(from.getAvvalimentoPer());
	}
	
	public static void copyTo(ImpresaAusiliariaType from, IImpresaAusiliaria to) {
		to.setRagioneSociale(from.getRagioneSociale());
		to.setTipoImpresa(from.getTipoImpresa());
		to.setAmbitoTerritoriale(from.getAmbitoTerritoriale());
		to.setNazione(from.getNazione());
		to.setCodiceFiscale(from.getCodiceFiscale());
		to.setPartitaIVA(from.getPartitaIVA());
		to.setIdFiscaleEstero(from.getIdFiscaleEstero());
		to.setAvvalimentoPer(from.getAvvalimentoPer());
	}

}
