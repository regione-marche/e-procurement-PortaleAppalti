package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

/**
 * Interfaccia comune del componente RTI o consorzio.
 *
 * @author Stefano.Sabbadin
 */
public interface IComponente {

	public String getRagioneSociale();
	
	public void setRagioneSociale(String ragioneSociale);

	public String getTipoImpresa();

	public void setTipoImpresa(String tipoImpresa);

	public String getAmbitoTerritoriale();

	public void setAmbitoTerritoriale(String ambitoTerritoriale);
	
	public String getNazione();

	public void setNazione(String nazione);

	public String getCodiceFiscale();

	public void setCodiceFiscale(String codiceFiscale);

	public String getPartitaIVA();

	public void setPartitaIVA(String partitaIVA);
	
	public String getIdFiscaleEstero();

	public void setIdFiscaleEstero(String idFiscaleEstero);

	public Double getQuota();

	public void setQuota(Double quota);

}
