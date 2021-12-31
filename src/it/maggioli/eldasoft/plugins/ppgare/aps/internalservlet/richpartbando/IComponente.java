package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

/**
 * Interfaccia comune del componente RTI o consorzio.
 *
 * @author Stefano.Sabbadin
 */
public interface IComponente {

	String getRagioneSociale();
	
	void setRagioneSociale(String ragioneSociale);

	String getTipoImpresa();

	void setTipoImpresa(String tipoImpresa);

	String getNazione();

	void setNazione(String nazione);

	String getCodiceFiscale();

	void setCodiceFiscale(String codiceFiscale);

	String getPartitaIVA();

	void setPartitaIVA(String partitaIVA);

	Double getQuota();

	void setQuota(Double quota);

}
