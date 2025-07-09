package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

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
	
	public ComponenteHelper(IComponente from) {
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
	public Double getQuota() {
		return quota;
	}

	@Override
	public void setQuota(Double quota) {
		this.quota = quota;
	}

	public static void reset(IComponente componente) {
		componente.setRagioneSociale(null);
		componente.setTipoImpresa(null);
		componente.setAmbitoTerritoriale(null);
		componente.setNazione("Italia");
		componente.setCodiceFiscale(null);
		componente.setPartitaIVA(null);
		componente.setIdFiscaleEstero(null);
		componente.setQuota(null);
	}

	private static String toUpper(String value) {
		return StringUtils.isNotEmpty(value) ? value.toUpperCase() : value; 
	}

	public static void copyTo(IComponente from, IComponente to) {
		to.setRagioneSociale(from.getRagioneSociale());
		to.setTipoImpresa(from.getTipoImpresa());
		to.setAmbitoTerritoriale(from.getAmbitoTerritoriale());
		to.setNazione(from.getNazione());
		to.setCodiceFiscale(toUpper(from.getCodiceFiscale()));
		to.setPartitaIVA(toUpper(from.getPartitaIVA()));
		to.setIdFiscaleEstero(toUpper(from.getIdFiscaleEstero()));
		to.setQuota(from.getQuota());
	}

	public static IComponente fromPartecipantiRaggruppamentoType(PartecipanteRaggruppamentoType partecipante) {
		ComponenteHelper toReturn = new ComponenteHelper();

		toReturn.ragioneSociale = partecipante.getRagioneSociale();
		toReturn.tipoImpresa = partecipante.getTipoImpresa();
		toReturn.nazione = partecipante.getNazione();
		toReturn.ambitoTerritoriale = partecipante.getAmbitoTerritoriale();
		toReturn.codiceFiscale = toUpper(partecipante.getCodiceFiscale()); 
		toReturn.partitaIVA = toUpper(partecipante.getPartitaIVA());
		toReturn.idFiscaleEstero = toUpper(partecipante.getIdFiscaleEstero());
		toReturn.quota = partecipante.getQuota();
		return toReturn;
	}

	public static IComponente fromDatiImpresa(WizardDatiImpresaHelper impresa) {
		ComponenteHelper toReturn = new ComponenteHelper();

		if(impresa.isLiberoProfessionista()) {
//			toReturn.ragioneSociale = ((StringUtils.isNotEmpty(impresa.getAltriDatiAnagraficiImpresa().getCognome()) ? impresa.getAltriDatiAnagraficiImpresa().getCognome() : "") + 
//									   " " + 
//									   (StringUtils.isNotEmpty(impresa.getAltriDatiAnagraficiImpresa().getNome()) ? impresa.getAltriDatiAnagraficiImpresa().getNome() : ""))
//									  .trim();
			toReturn.ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			toReturn.tipoImpresa = impresa.getDatiPrincipaliImpresa().getTipoImpresa();
			toReturn.ambitoTerritoriale = impresa.getDatiPrincipaliImpresa().getAmbitoTerritoriale();
			toReturn.nazione = impresa.getDatiPrincipaliImpresa().getNaturaGiuridica();
			toReturn.codiceFiscale = toUpper(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
			toReturn.partitaIVA = toUpper(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
			toReturn.idFiscaleEstero = null;
			toReturn.quota = 100.0;
		} else {
			toReturn.ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			toReturn.tipoImpresa = impresa.getDatiPrincipaliImpresa().getTipoImpresa();
			toReturn.ambitoTerritoriale = impresa.getDatiPrincipaliImpresa().getAmbitoTerritoriale();
			toReturn.nazione = impresa.getDatiPrincipaliImpresa().getNaturaGiuridica();
			toReturn.codiceFiscale = toUpper(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
			toReturn.partitaIVA = toUpper(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
			toReturn.idFiscaleEstero = null;
			toReturn.quota = 100.0;
		}
		
		return toReturn;
	}

}
