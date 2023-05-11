package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

public class PagoPASearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -4623566150806895838L;

	@Validate(EParamValidation.CODICE)
	private String codiceimpresa;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	@Validate(EParamValidation.IUV)
	private String iuv;
	@Validate(EParamValidation.GENERIC)
	private String idDebito;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataScadenzaDa;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataScadenzaA;
	private Integer causale;
	private Integer stato;
	/**
	 * @return the codiceimpresa
	 */
	public String getCodiceimpresa() {
		return codiceimpresa;
	}
	/**
	 * @param codiceimpresa the codiceimpresa to set
	 */
	public void setCodiceimpresa(String codiceimpresa) {
		this.codiceimpresa = codiceimpresa;
	}
	/**
	 * @return the iuv
	 */
	public String getIuv() {
		return iuv;
	}
	/**
	 * @param iuv the iuv to set
	 */
	public void setIuv(String iuv) {
		this.iuv = iuv;
	}
	/**
	 * @return the idDebito
	 */
	public String getIdDebito() {
		return idDebito;
	}
	/**
	 * @param idDebito the idDebito to set
	 */
	public void setIdDebito(String idDebito) {
		this.idDebito = idDebito;
	}
	/**
	 * @return the dataScadenzaDa
	 */
	public String getDataScadenzaDa() {
		return dataScadenzaDa;
	}
	/**
	 * @param dataScadenzaDa the dataScadenzaDa to set
	 */
	public void setDataScadenzaDa(String dataScadenzaDa) {
		this.dataScadenzaDa = dataScadenzaDa;
	}
	/**
	 * @return the dataScadenzaA
	 */
	public String getDataScadenzaA() {
		return dataScadenzaA;
	}
	/**
	 * @param dataScadenzaA the dataScadenzaA to set
	 */
	public void setDataScadenzaA(String dataScadenzaA) {
		this.dataScadenzaA = dataScadenzaA;
	}
	/**
	 * @return the causale
	 */
	public Integer getCausale() {
		return causale;
	}
	/**
	 * @param causale the causale to set
	 */
	public void setCausale(Integer causale) {
		this.causale = causale;
	}
	/**
	 * @return the stato
	 */
	public Integer getStato() {
		return stato;
	}
	/**
	 * @param stato the stato to set
	 */
	public void setStato(Integer stato) {
		this.stato = stato;
	}
	/**
	 * @return the codiceGara
	 */
	public String getCodiceGara() {
		return codiceGara;
	}
	/**
	 * @param codiceGara the codiceGara to set
	 */
	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}
	
	@Override
	public String toString() {
		return "PagoPASearchBean [" + (codiceimpresa != null ? "codiceimpresa=" + codiceimpresa + ", " : "")
				+ (codiceGara != null ? "codiceGara=" + codiceGara + ", " : "")
				+ (iuv != null ? "iuv=" + iuv + ", " : "") + (idDebito != null ? "idDebito=" + idDebito + ", " : "")
				+ (dataScadenzaDa != null ? "dataScadenzaDa=" + dataScadenzaDa + ", " : "")
				+ (dataScadenzaA != null ? "dataScadenzaA=" + dataScadenzaA + ", " : "")
				+ (causale != null ? "causale=" + causale + ", " : "") + (stato != null ? "stato=" + stato + ", " : "")
				+ "getCurrentPage()=" + getCurrentPage() + ", getiTotalDisplayRecords()=" + getiTotalDisplayRecords()
				+ ", getPagesToShow()=" + getPagesToShow() + ", getiDisplayLength()=" + getiDisplayLength() +"]";
	}
}
