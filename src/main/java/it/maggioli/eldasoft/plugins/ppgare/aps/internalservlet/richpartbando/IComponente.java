package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

/**
 * Interfaccia comune del componente RTI o consorzio.
 *
 * @author Stefano.Sabbadin
 */
public interface IComponente extends IImpresa {

	public Double getQuota();
	public void setQuota(Double quota);

}
