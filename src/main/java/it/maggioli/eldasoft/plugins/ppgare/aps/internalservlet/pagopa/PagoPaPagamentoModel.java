package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;
import java.util.Date;

public class PagoPaPagamentoModel implements Serializable {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 6159923457627871830L;

	private Long id;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	@Validate(EParamValidation.CODICE_IMPRESA)
	private String codiceImpresa;
	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.TITOLO_GARA)
	private String titoloGara;
	private Integer causale;
	private Integer stato;
	@Validate(EParamValidation.GENERIC)
	private String idDebito;
	@Validate(EParamValidation.GENERIC)
	private String idRata;
	@Validate(EParamValidation.IUV)
	private String iuv;
	private Double importo;
	private Date dataScadenza;
	private Date dataInizioValidita;
	private Date dataFineValidita;
	private Date dataPagamento;
	@Validate(EParamValidation.GENERIC)
	private String idFiscaleDebitore;
	@Validate(EParamValidation.GENERIC)
	private String tipoIdFiscaleDebitore;
	private boolean pagamentoAbilitato = Boolean.TRUE;
	private boolean pagamentoEffettuato;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	/**
	 * @return the codiceImpresa
	 */
	public String getCodiceImpresa() {
		return codiceImpresa;
	}
	/**
	 * @param codiceImpresa the codiceImpresa to set
	 */
	public void setCodiceImpresa(String codiceImpresa) {
		this.codiceImpresa = codiceImpresa;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the titoloGara
	 */
	public String getTitoloGara() {
		return titoloGara;
	}
	/**
	 * @param titoloGara the titoloGara to set
	 */
	public void setTitoloGara(String titoloGara) {
		this.titoloGara = titoloGara;
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
	 * @return the idRata
	 */
	public String getIdRata() {
		return idRata;
	}
	/**
	 * @param idRata the idRata to set
	 */
	public void setIdRata(String idRata) {
		this.idRata = idRata;
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
	 * @return the importo
	 */
	public Double getImporto() {
		return importo;
	}
	/**
	 * @param importo the importo to set
	 */
	public void setImporto(Double importo) {
		this.importo = importo;
	}
	/**
	 * @return the dataScadenza
	 */
	public Date getDataScadenza() {
		return dataScadenza;
	}
	/**
	 * @param dataScadenza the dataScadenza to set
	 */
	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}
	/**
	 * @return the dataInizioValidita
	 */
	public Date getDataInizioValidita() {
		return dataInizioValidita;
	}
	/**
	 * @param dataInizioValidita the dataInizioValidita to set
	 */
	public void setDataInizioValidita(Date dataInizioValidita) {
		this.dataInizioValidita = dataInizioValidita;
	}
	/**
	 * @return the dataFineValidita
	 */
	public Date getDataFineValidita() {
		return dataFineValidita;
	}
	/**
	 * @param dataFineValidita the dataFineValidita to set
	 */
	public void setDataFineValidita(Date dataFineValidita) {
		this.dataFineValidita = dataFineValidita;
	}
	/**
	 * @return the dataPagamento
	 */
	public Date getDataPagamento() {
		return dataPagamento;
	}
	/**
	 * @param dataPagamento the dataPagamento to set
	 */
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	/**
	 * @return the idFiscaleDebitore
	 */
	public String getIdFiscaleDebitore() {
		return idFiscaleDebitore;
	}
	/**
	 * @param idFiscaleDebitore the idFiscaleDebitore to set
	 */
	public void setIdFiscaleDebitore(String idFiscaleDebitore) {
		this.idFiscaleDebitore = idFiscaleDebitore;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idDebito == null) ? 0 : idDebito.hashCode());
		result = prime * result + ((idRata == null) ? 0 : idRata.hashCode());
		result = prime * result + ((iuv == null) ? 0 : iuv.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PagoPaPagamentoModel other = (PagoPaPagamentoModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idDebito == null) {
			if (other.idDebito != null)
				return false;
		} else if (!idDebito.equals(other.idDebito))
			return false;
		if (idRata == null) {
			if (other.idRata != null)
				return false;
		} else if (!idRata.equals(other.idRata))
			return false;
		if (iuv == null) {
			if (other.iuv != null)
				return false;
		} else if (!iuv.equals(other.iuv))
			return false;
		return true;
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
	 * @return the tipoIdFiscaleDebitore
	 */
	public String getTipoIdFiscaleDebitore() {
		return tipoIdFiscaleDebitore;
	}
	/**
	 * @param tipoIdFiscaleDebitore the tipoIdFiscaleDebitore to set
	 */
	public void setTipoIdFiscaleDebitore(String tipoIdFiscaleDebitore) {
		this.tipoIdFiscaleDebitore = tipoIdFiscaleDebitore;
	}
	
	/**
	 * @return the pagamentoAbilitato
	 */
	public boolean isPagamentoAbilitato() {
		return pagamentoAbilitato;
	}
	/**
	 * @param pagamentoAbilitato the pagamentoAbilitato to set
	 */
	public void setPagamentoAbilitato(boolean pagamentoAbilitato) {
		this.pagamentoAbilitato = pagamentoAbilitato;
	}
	/**
	 * @return the pagamentoEffettuato
	 */
	public boolean isPagamentoEffettuato() {
		return pagamentoEffettuato;
	}
	/**
	 * @param pagamentoEffettuato the pagamentoEffettuato to set
	 */
	public void setPagamentoEffettuato(boolean pagamentoEffettuato) {
		this.pagamentoEffettuato = pagamentoEffettuato;
	}
	@Override
	public String toString() {
		return "PagoPaPagamentoModel [" + (id != null ? "id=" + id + ", " : "")
				+ (codiceGara != null ? "codiceGara=" + codiceGara + ", " : "")
				+ (codiceImpresa != null ? "codiceImpresa=" + codiceImpresa + ", " : "")
				+ (username != null ? "username=" + username + ", " : "")
				+ (titoloGara != null ? "titoloGara=" + titoloGara + ", " : "")
				+ (causale != null ? "causale=" + causale + ", " : "") + (stato != null ? "stato=" + stato + ", " : "")
				+ (idDebito != null ? "idDebito=" + idDebito + ", " : "")
				+ (idRata != null ? "idRata=" + idRata + ", " : "") + (iuv != null ? "iuv=" + iuv + ", " : "")
				+ (importo != null ? "importo=" + importo + ", " : "")
				+ (dataScadenza != null ? "dataScadenza=" + dataScadenza + ", " : "")
				+ (dataInizioValidita != null ? "dataInizioValidita=" + dataInizioValidita + ", " : "")
				+ (dataFineValidita != null ? "dataFineValidita=" + dataFineValidita + ", " : "")
				+ (idFiscaleDebitore != null ? "idFiscaleDebitore=" + idFiscaleDebitore + ", " : "")
				+ (tipoIdFiscaleDebitore != null ? "tipoIdFiscaleDebitore=" + tipoIdFiscaleDebitore : "") + "]";
	}
	
	
	
}
