package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import java.io.Serializable;

/**
 * Bean per la memorizzazione dei criteri di ricerca per i prospetti trasparenza
 * amministrazione (soggetti beneficiari).
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.0
 */
public class SoggettiBeneficiariSearchBean implements Serializable {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 8287042419165904615L;

	private String dataAffidamentoDa;
	private String dataAffidamentoA;

	/**
	 * @return the dataAffidamentoDa
	 */
	public String getDataAffidamentoDa() {
		return dataAffidamentoDa;
	}

	/**
	 * @param dataAffidamentoDa
	 *            the dataAffidamentoDa to set
	 */
	public void setDataAffidamentoDa(String dataAffidamentoDa) {
		if (!"".equals(dataAffidamentoDa))
			this.dataAffidamentoDa = dataAffidamentoDa;
	}

	/**
	 * @return the dataAffidamentoA
	 */
	public String getDataAffidamentoA() {
		return dataAffidamentoA;
	}

	/**
	 * @param dataAffidamentoA
	 *            the dataAffidamentoA to set
	 */
	public void setDataAffidamentoA(String dataAffidamentoA) {
		if (!"".equals(dataAffidamentoA))
			this.dataAffidamentoA = dataAffidamentoA;
	}
}
