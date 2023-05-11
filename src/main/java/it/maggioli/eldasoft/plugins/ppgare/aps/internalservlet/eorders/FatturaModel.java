package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import it.maggioli.eldasoft.nso.invoice.client.model.DatiCassaPrevidenzialeType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiCassaPrevidenzialeType.NaturaEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiCassaPrevidenzialeType.RitenutaEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiCassaPrevidenzialeType.TipoCassaEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiGeneraliDocumentoType.TipoDocumentoEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiPagamentoType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiPagamentoType.CondizioniPagamentoEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiRitenutaType.CausalePagamentoEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiRitenutaType.TipoRitenutaEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DettaglioPagamentoType.ModalitaPagamentoEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.IscrizioneREAType.StatoLiquidazioneEnum;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.util.Arrays;
import java.util.List;

public class FatturaModel {
	@Validate
	private String stabileOrganizzazione;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String numero;
	@Validate(EParamValidation.GENERIC)
	private String tcapre;
	private Long idOrdine;
	private List<DatiPagamentoType> datiPagamento;
	@Validate(EParamValidation.GENERIC)
	private String[] condPag;
	private StatoLiquidazioneEnum statoLiquidazione;
	@Validate(EParamValidation.GENERIC)
	private String bolloVirtuale;
	private TipoDocumentoEnum tipoDocumento;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String data;
	private TipoRitenutaEnum tipoRitenuta;
	private CausalePagamentoEnum causalePagamento;
	private Integer aliquotaRitenuta;
	private Integer riferimentoFase;
	@Validate(EParamValidation.GENERIC)
	private String ddtnum;
	@Validate(EParamValidation.GENERIC)
	private String ddtdata;
	@Validate(EParamValidation.GENERIC)
	private String ddtrifnum;
	private ModalitaPagamentoEnum[] modalitaPagamento;
	@Validate(EParamValidation.DATE_YYYYMMDD)
	private String dataScadenzaPagamento;	//Formato ISO 8601
	@Validate(EParamValidation.ISTITUTO_FINANZIARIO)
	private String istitutoFinanziario;
	@Validate(EParamValidation.IBAN)
	private String iban = null;
	@Validate(EParamValidation.ABI)
	private String abi = null;
	@Validate(EParamValidation.CAB)
	private String cab = null;
	@Validate(EParamValidation.BICC)
	private String bic = null;

	@Validate(EParamValidation.ALIQUOTA)
	private String datCassAliquota = null;
	@Validate(EParamValidation.IMPONIBILE)
	private String datCassImponibile = null;
	@Validate(EParamValidation.ALIQUOTA)
	private String datCassAliquotaIva = null;
	@Validate(EParamValidation.DAT_CASSA_RITENUTA)
	private String datCassRitenuta = null;
	@Validate(EParamValidation.DAT_CASSA_NATURA)
	private String datCassNatura = null;
	@Validate(EParamValidation.DAT_CASSA_TIPO_CASSA)
	private String datCassTipoCassa = null;

	public String getDatCassAliquota() {
		return datCassAliquota;
	}

	public void setDatCassAliquota(String datCassAliquota) {
		this.datCassAliquota = datCassAliquota;
	}

	public String getDatCassImponibile() {
		return datCassImponibile;
	}

	public void setDatCassImponibile(String datCassImponibile) {
		this.datCassImponibile = datCassImponibile;
	}

	public String getDatCassAliquotaIva() {
		return datCassAliquotaIva;
	}

	public void setDatCassAliquotaIva(String datCassAliquotaIva) {
		this.datCassAliquotaIva = datCassAliquotaIva;
	}

	public String getDatCassRitenuta() {
		return datCassRitenuta;
	}

	public void setDatCassRitenuta(String datCassRitenuta) {
		this.datCassRitenuta = datCassRitenuta;
	}

	public String getDatCassNatura() {
		return datCassNatura;
	}

	public void setDatCassNatura(String datCassNatura) {
		this.datCassNatura = datCassNatura;
	}

	public String getDataScadenzaPagamento() {
		return dataScadenzaPagamento;
	}

	public void setDataScadenzaPagamento(String dataScadenzaPagamento) {
		this.dataScadenzaPagamento = dataScadenzaPagamento;
	}

	public String getIstitutoFinanziario() {
		return istitutoFinanziario;
	}

	public void setIstitutoFinanziario(String istitutoFinanziario) {
		this.istitutoFinanziario = istitutoFinanziario;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getAbi() {
		return abi;
	}

	public void setAbi(String abi) {
		this.abi = abi;
	}

	public String getCab() {
		return cab;
	}

	public void setCab(String cab) {
		this.cab = cab;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getDdtnum() {
		return ddtnum;
	}

	public void setDdtnum(String ddtnum) {
		this.ddtnum = ddtnum;
	}

	public String getDdtdata() {
		return ddtdata;
	}

	public void setDdtdata(String ddtdata) {
		this.ddtdata = ddtdata;
	}

	public String getDdtrifnum() {
		return ddtrifnum;
	}

	public void setDdtrifnum(String ddtrifnum) {
		this.ddtrifnum = ddtrifnum;
	}

	public String getStabileOrganizzazione() {
		return stabileOrganizzazione;
	}

	public void setStabileOrganizzazione(String stabileOrganizzazione) {
		this.stabileOrganizzazione = stabileOrganizzazione;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Long getIdOrdine() {
		return idOrdine;
	}

	public void setIdOrdine(Long idOrdine) {
		this.idOrdine = idOrdine;
	}

	@Override
	public String toString() {
		return "FatturaModelHeader ["
				+ (stabileOrganizzazione != null ? "stabileOrganizzazione=" + stabileOrganizzazione + ", " : "")
				+ (numero != null ? "numero=" + numero + ", " : "")
				+ (tcapre != null ? "tcapre=" + tcapre + ", " : "")
				+ (idOrdine != null ? "idOrdine=" + idOrdine + ", " : "")
				+ (datiPagamento != null ? "datiPagamento=" + datiPagamento + ", " : "")
				+ (condPag != null ? "condPag=" + Arrays.toString(condPag) + ", " : "")
				+ (statoLiquidazione != null ? "statoLiquidazione=" + statoLiquidazione + ", " : "")
				+ (bolloVirtuale != null ? "bolloVirtuale=" + bolloVirtuale + ", " : "")
				+ (tipoDocumento != null ? "tipoDocumento=" + tipoDocumento + ", " : "")
				+ (data != null ? "data=" + data + ", " : "")
				+ (tipoRitenuta != null ? "tipoRitenuta=" + tipoRitenuta + ", " : "")
				+ (causalePagamento != null ? "causalePagamento=" + causalePagamento + ", " : "")
				+ (aliquotaRitenuta != null ? "aliquotaRitenuta=" + aliquotaRitenuta + ", " : "")
				+ (riferimentoFase != null ? "riferimentoFase=" + riferimentoFase + ", " : "")
				+ (ddtnum != null ? "ddtnum=" + ddtnum + ", " : "")
				+ (ddtdata != null ? "ddtdata=" + ddtdata + ", " : "")
				+ (ddtrifnum != null ? "ddtrifnum=" + ddtrifnum + ", " : "")
				+ (modalitaPagamento != null ? "modalitaPagamento=" + Arrays.toString(modalitaPagamento) + ", " : "")
				+ (dataScadenzaPagamento != null ? "dataScadenzaPagamento=" + dataScadenzaPagamento + ", " : "")
				+ (istitutoFinanziario != null ? "istitutoFinanziario=" + istitutoFinanziario + ", " : "")
				+ (iban != null ? "iban=" + iban + ", " : "") + (abi != null ? "abi=" + abi + ", " : "")
				+ (cab != null ? "cab=" + cab + ", " : "") + (bic != null ? "bic=" + bic : "") + "]";
	}

	public String getTcapre() {
		return tcapre;
	}

	public void setTcapre(String tcapre) {
		this.tcapre = tcapre;
	}

	public List<DatiPagamentoType> getDatiPagamento() {
		return datiPagamento;
	}

	public void setDatiPagamento(List<DatiPagamentoType> datiPagamento) {
		this.datiPagamento = datiPagamento;
	}

	public CondizioniPagamentoEnum[] getEnumCondPag() {
		return CondizioniPagamentoEnum.values();
	}

	public String[] getCondPag() {
		return condPag;
	}

	public void setCondPag(String[] condPag) {
		this.condPag = condPag;
	}

	public StatoLiquidazioneEnum getStatoLiquidazione() {
		return statoLiquidazione;
	}

	public void setStatoLiquidazione(StatoLiquidazioneEnum statoLiquidazione) {
		this.statoLiquidazione = statoLiquidazione;
	}
	public void setStatoLiquidazione(String statoLiquidazione) {
		this.statoLiquidazione = StatoLiquidazioneEnum.fromValue(statoLiquidazione);
	}

	public StatoLiquidazioneEnum[] getStatiLiquidazione() {
		return StatoLiquidazioneEnum.values();
	}

	public String getBolloVirtuale() {
		return bolloVirtuale;
	}

	public void setBolloVirtuale(String bolloVirtuale) {
		this.bolloVirtuale = bolloVirtuale;
	}
	
	public TipoDocumentoEnum[] getTipiDocumento() {
		return TipoDocumentoEnum.values();
	}

	public TipoDocumentoEnum getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoEnum tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public TipoRitenutaEnum[] getTipiRitenuta() {
		return TipoRitenutaEnum.values();
	}

	public TipoRitenutaEnum getTipoRitenuta() {
		return tipoRitenuta;
	}

	public void setTipoRitenuta(TipoRitenutaEnum tipoRitenuta) {
		this.tipoRitenuta = tipoRitenuta;
	}

	public CausalePagamentoEnum[] getCausaliPagamento() {
		return CausalePagamentoEnum.values();
	}

	public CausalePagamentoEnum getCausalePagamento() {
		return causalePagamento;
	}

	public void setCausalePagamento(CausalePagamentoEnum causalePagamento) {
		this.causalePagamento = causalePagamento;
	}

	public Integer getAliquotaRitenuta() {
		return aliquotaRitenuta;
	}

	public void setAliquotaRitenuta(Integer aliquotaRitenuta) {
		this.aliquotaRitenuta = aliquotaRitenuta;
	}
	public ModalitaPagamentoEnum[] getModalitaPagamentoEnum() {
		return ModalitaPagamentoEnum.values();
	}

	public ModalitaPagamentoEnum[] getModalitaPagamento() {
		return modalitaPagamento;
	}

	public void setModalitaPagamento(ModalitaPagamentoEnum[] modalitaPagamento) {
		this.modalitaPagamento = modalitaPagamento;
	}

	public Integer getRiferimentoFase() {
		return riferimentoFase;
	}

	public void setRiferimentoFase(Integer riferimentoFase) {
		this.riferimentoFase = riferimentoFase;
	}

	public String getDatCassTipoCassa() {
		return this.datCassTipoCassa;
	}

	public void setDatCassTipoCassa(String datCassTipoCassa) {
		this.datCassTipoCassa = datCassTipoCassa;
	}
	
	public RitenutaEnum[] getDatCassRitenutaEnum() {
		return DatiCassaPrevidenzialeType.RitenutaEnum.values();
	}
	public TipoCassaEnum[] getDatCassTipoCassaEnum() {
		return DatiCassaPrevidenzialeType.TipoCassaEnum.values();
	}
	public NaturaEnum[] getDatCassNaturaEnum() {
		return DatiCassaPrevidenzialeType.NaturaEnum.values();
	}
}
