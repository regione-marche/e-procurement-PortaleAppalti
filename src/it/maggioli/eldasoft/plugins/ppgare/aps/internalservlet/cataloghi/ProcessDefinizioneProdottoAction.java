package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

/**
 * Action di gestione della pagina di dettaglio prodotto
 *
 * @author Marco.Perazzetta
 */
public class ProcessDefinizioneProdottoAction extends AbstractProcessPageAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1852462084751813257L;

	private String marcaProdottoProduttore;
	private String codiceProdottoProduttore;
	private String nomeCommerciale;
	private String codiceProdottoFornitore;
	private String descrizioneAggiuntiva;
	private boolean obbligoDescrizioneAggiuntiva;
	private boolean obbligoDimensioni;
	private boolean obbligoGaranzia;
	private String dimensioni;
	private String aliquotaIVA;
	private String prezzoUnitario;
	private String garanzia;
	private String tempoConsegna;
	private String dataScadenzaOfferta;
	private Long tipoArticolo;
	private String quantitaUMPrezzo;
	private String quantitaUMAcquisto;
	private String prezzoUnitarioPer;
	private String qtaMinDetPz;
	private String qtaMaxDetPz;
	private Integer tempoMaxConsegna;
	private Integer numDecPrezzo;

	public String getMarcaProdottoProduttore() {
		return marcaProdottoProduttore;
	}

	public void setMarcaProdottoProduttore(String marcaProdottoProduttore) {
		this.marcaProdottoProduttore = marcaProdottoProduttore;
	}

	public String getCodiceProdottoProduttore() {
		return codiceProdottoProduttore;
	}

	public void setCodiceProdottoProduttore(String codiceProdottoProduttore) {
		this.codiceProdottoProduttore = codiceProdottoProduttore;
	}

	public String getNomeCommerciale() {
		return nomeCommerciale;
	}

	public void setNomeCommerciale(String nomeCommerciale) {
		this.nomeCommerciale = nomeCommerciale;
	}

	public String getCodiceProdottoFornitore() {
		return codiceProdottoFornitore;
	}

	public void setCodiceProdottoFornitore(String codiceProdottoFornitore) {
		this.codiceProdottoFornitore = codiceProdottoFornitore;
	}

	public String getDescrizioneAggiuntiva() {
		return descrizioneAggiuntiva;
	}

	public void setDescrizioneAggiuntiva(String descrizioneAggiuntiva) {
		this.descrizioneAggiuntiva = descrizioneAggiuntiva;
	}

	public boolean getObbligoDescrizioneAggiuntiva() {
		return obbligoDescrizioneAggiuntiva;
	}

	public boolean getObbligoDimensioni() {
		return obbligoDimensioni;
	}

	public String getDimensioni() {
		return dimensioni;
	}

	public void setDimensioni(String dimensioni) {
		this.dimensioni = dimensioni;
	}

	public String getAliquotaIVA() {
		return aliquotaIVA;
	}

	public void setAliquotaIVA(String aliquotaIVA) {
		this.aliquotaIVA = aliquotaIVA;
	}

	public String getPrezzoUnitario() {
		return prezzoUnitario;
	}

	public void setPrezzoUnitario(String prezzoUnitario) {
		this.prezzoUnitario = prezzoUnitario;
	}

	public String getGaranzia() {
		return garanzia;
	}

	public void setGaranzia(String garanzia) {
		this.garanzia = garanzia;
	}

	public String getTempoConsegna() {
		return tempoConsegna;
	}

	public void setTempoConsegna(String tempoConsegna) {
		this.tempoConsegna = tempoConsegna;
	}

	public String getDataScadenzaOfferta() {
		return dataScadenzaOfferta;
	}

	public void setDataScadenzaOfferta(String dataScadenzaOfferta) {
		this.dataScadenzaOfferta = dataScadenzaOfferta;
	}

	public Long getTipoArticolo() {
		return tipoArticolo;
	}

	public void setTipoArticolo(Long tipoArticolo) {
		this.tipoArticolo = tipoArticolo;
	}

	public String getQuantitaUMPrezzo() {
		return quantitaUMPrezzo;
	}

	public void setQuantitaUMPrezzo(String quantitaUMPrezzo) {
		this.quantitaUMPrezzo = quantitaUMPrezzo;
	}

	public String getQuantitaUMAcquisto() {
		return quantitaUMAcquisto;
	}

	public void setQuantitaUMAcquisto(String quantitaUMAcquisto) {
		this.quantitaUMAcquisto = quantitaUMAcquisto;
	}

	public String getPrezzoUnitarioPer() {
		return prezzoUnitarioPer;
	}

	public void setPrezzoUnitarioPer(String prezzoUnitarioPer) {
		this.prezzoUnitarioPer = prezzoUnitarioPer;
	}

	public String getQtaMinDetPz() {
		return qtaMinDetPz;
	}

	public void setQtaMinDetPz(String qtaMinDetPz) {
		this.qtaMinDetPz = qtaMinDetPz;
	}

	public String getQtaMaxDetPz() {
		return qtaMaxDetPz;
	}

	public void setQtaMaxDetPz(String qtaMaxDetPz) {
		this.qtaMaxDetPz = qtaMaxDetPz;
	}

	public Integer getTempoMaxConsegna() {
		return tempoMaxConsegna;
	}

	public void setTempoMaxConsegna(Integer tempoMaxConsegna) {
		this.tempoMaxConsegna = tempoMaxConsegna;
	}

	public boolean getObbligoGaranzia() {
		return obbligoGaranzia;
	}

	public void setObbligoDescrizioneAggiuntiva(boolean obbligoDescrizioneAggiuntiva) {
		this.obbligoDescrizioneAggiuntiva = obbligoDescrizioneAggiuntiva;
	}

	public void setObbligoDimensioni(boolean obbligoDimensioni) {
		this.obbligoDimensioni = obbligoDimensioni;
	}

	public void setObbligoGaranzia(boolean obbligoGaranzia) {
		this.obbligoGaranzia = obbligoGaranzia;
	}

	public Integer getNumDecPrezzo() {
		return numDecPrezzo;
	}

	public void setNumDecPrezzo(Integer numDecPrezzo) {
		this.numDecPrezzo = numDecPrezzo;
	}

	/**
	 * Funzione di controllo sui dati inseriti
	 */
	@Override
	public void validate() {

		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		try {
			Double _qtaUMAcquisto = new Double(StringUtils.isNotBlank(this.quantitaUMAcquisto) ? this.quantitaUMAcquisto : "0");
			Double _qtaMinDetPz = new Double(StringUtils.isNotBlank(this.qtaMinDetPz) ? this.qtaMinDetPz : "0");
			Double _qtaMaxDetPz = new Double(StringUtils.isNotBlank(this.qtaMaxDetPz) ? this.qtaMaxDetPz : "0"); 
			Integer _garanzia = new Integer(StringUtils.isNotBlank(this.garanzia) ? this.garanzia : "0");
			Integer _tempoConsegna = new Integer(StringUtils.isNotBlank(this.tempoConsegna) ? this.tempoConsegna : "0");
					
			if (StringUtils.isNotBlank(this.quantitaUMAcquisto) && !CataloghiConstants.TIPO_PREZZO_CONFEZIONE.equals(this.prezzoUnitarioPer) && !CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA.equals(this.prezzoUnitarioPer)) {
				if (StringUtils.isNotBlank(this.qtaMinDetPz) && _qtaUMAcquisto < _qtaMinDetPz) {
					this.addFieldError("quantitaUMAcquisto", this.getText("Errors.mustBeAbove", new String[] {this.getTextFromDB("quantitaUMAcquisto"), this.qtaMinDetPz}));
				}
				if (StringUtils.isNotBlank(this.qtaMaxDetPz) && _qtaUMAcquisto > _qtaMaxDetPz) {
					this.addFieldError("quantitaUMAcquisto", this.getText("Errors.mustBeUnder", new String[] {this.getTextFromDB("quantitaUMAcquisto"), this.qtaMaxDetPz}));
				}
			}
			
			if (this.obbligoGaranzia && StringUtils.isNotBlank(this.garanzia) && _garanzia < CataloghiConstants.DEFAULT_NUM_MESI_GARANZIA) {
				this.addFieldError("garanzia", this.getText("Errors.mustBeAbove", new String[] {this.getTextFromDB("garanzia"), CataloghiConstants.DEFAULT_NUM_MESI_GARANZIA + ""}));
			}
			
			if (this.tempoMaxConsegna != null && StringUtils.isNotBlank(this.tempoConsegna) && _tempoConsegna > this.tempoMaxConsegna) {
				this.addFieldError("tempoConsegna", this.getText("Errors.mustBeUnder", new String[]{this.getTextFromDB("tempoConsegna"), this.tempoMaxConsegna + ""}));
			}
			
			//forzo comunque il numero decimali a 1 per la conversione in scala altrimenti poi il controllo sbaglia.
			Integer numeroMassimoDecimali = (this.numDecPrezzo == null || this.numDecPrezzo == 0) ? 1 : this.numDecPrezzo;
			BigDecimal bd = BigDecimal.valueOf(new Double(this.prezzoUnitario)).setScale(numeroMassimoDecimali, BigDecimal.ROUND_HALF_UP);
			if (this.numDecPrezzo != null) {
				String[] prezzoUnitarioArray = bd.toPlainString().split("\\.");
				String decimalPartString = "";
				Integer decimalPart = 0;
				if (prezzoUnitarioArray.length > 1) {
					decimalPartString = prezzoUnitarioArray[1];
					decimalPart = new Integer(decimalPartString);
				}
				if (this.numDecPrezzo == 0 && decimalPart > 0) {
					this.addFieldError("prezzoUnitario", this.getText("Errors.noDecimalsNeeded", new String[] {this.getTextFromDB("prezzoUnitario")}));
				} else if (this.numDecPrezzo > 0 && decimalPartString.length() > this.numDecPrezzo) {
					this.addFieldError("prezzoUnitario", this.getText("Errors.tooManyDecimals", new String[] {this.getTextFromDB("prezzoUnitario"), this.numDecPrezzo + ""}));
				}
			}
			//Controllo che la data scadenza sia >= oggi e <=data oggi+1 anno.
			Date now = new Date();
			Calendar tomorrow = new GregorianCalendar();
			tomorrow.add(Calendar.YEAR, 1);
			Date scadenzaOfferta = CalendarValidator.getInstance().validate(this.dataScadenzaOfferta, "dd/MM/yyyy").getTime();
			if (scadenzaOfferta.before(now) || scadenzaOfferta.after(tomorrow.getTime())) {
				this.addFieldError("prezzoUnitario", this.getText("Errors.invalidDataScadenza"));
			}
		} catch (NumberFormatException t) {
			throw new RuntimeException("Errore durante la verifica dei dati del prodotto", t);
		} catch (Throwable t) {
			throw new RuntimeException("Errore durante la verifica dei dati del prodotto", t);
		}
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				WizardProdottoHelper prodottoHelper = getSessionHelper();
				ProdottoType prodotto = prodottoHelper.getDettaglioProdotto();
				if (prodotto == null) {
					prodotto = new ProdottoType();
				}
				prodotto.setCodiceCatalogo(prodottoHelper.getCodiceCatalogo());
				if (this.quantitaUMPrezzo != null) {
					prodotto.setQuantitaUMPrezzo(Double.parseDouble(this.quantitaUMPrezzo));
				}
				//calcolo la quantita', riferita all'unita' di misura per la determinazione del prezzo, che costituisce l'unita' di misura per acquisto
				if (!CataloghiConstants.TIPO_PREZZO_UNITA_DI_MISURA.equals(prodottoHelper.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer())) {
					prodotto.setQuantitaUMPrezzo(1);
				}
				//calcolo il prezzo unitario del prodotto riferito all'unita' di misura per l'acquisto
				Double prezzoUnitarioPerMisuraAcq = new Double(this.prezzoUnitario) * prodotto.getQuantitaUMPrezzo();
				BigDecimal prezzoUnitarioPerMisuraAcqRound = BigDecimal.valueOf(prezzoUnitarioPerMisuraAcq).setScale(
								prodottoHelper.getArticolo().getDettaglioArticolo().getNumDecimaliAcquisto(), BigDecimal.ROUND_HALF_UP);
				prodotto.setPrezzoUnitarioPerAcquisto(prezzoUnitarioPerMisuraAcqRound.doubleValue());
				BigDecimal prezzoUnitarioRound = BigDecimal.valueOf(new Double(this.prezzoUnitario)).setScale(
								prodottoHelper.getArticolo().getDettaglioArticolo().getNumDecimaliDetermPrezzo(), BigDecimal.ROUND_HALF_UP);
				prodotto.setPrezzoUnitario(prezzoUnitarioRound.doubleValue());
				//calcolo la quantita' di unita' di misura per l'acquisto
				if (CataloghiConstants.TIPO_PREZZO_CONFEZIONE.equals(prodottoHelper.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer())
					|| CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA.equals(prodottoHelper.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer())) {
					prodotto.setQuantitaUMAcquisto(prodottoHelper.getArticolo().getDettaglioArticolo().getQuantitaUnitaAcquisto());
				} else {
					prodotto.setQuantitaUMAcquisto(new Double(this.quantitaUMAcquisto));
				}
				prodotto.setMarcaProdottoProduttore(this.marcaProdottoProduttore);
				prodotto.setCodiceProdottoProduttore(this.codiceProdottoProduttore);
				prodotto.setNomeCommerciale(this.nomeCommerciale);
				prodotto.setCodiceProdottoFornitore(this.codiceProdottoFornitore);
				prodotto.setDescrizioneAggiuntiva(this.descrizioneAggiuntiva);
				prodotto.setDimensioni(this.dimensioni);
				prodotto.setAliquotaIVA(this.aliquotaIVA);
				if (prodottoHelper.getArticolo().getDettaglioArticolo().isObbligoGaranzia()) {
					prodotto.setGaranzia(new Integer(this.garanzia));
				}
				prodotto.setTempoConsegna(new Integer(this.tempoConsegna));
				prodotto.setDataScadenzaOfferta(CalendarValidator.getInstance().validate(
								this.dataScadenzaOfferta, "dd/MM/yyyy").getTime());
				prodottoHelper.setDettaglioProdotto(prodotto);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO, prodottoHelper);
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "next");
				ExceptionUtils.manageExceptionError(ex, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * Estrae l'helper del wizard prodotto da utilizzare nei controlli
	 *
	 * @return helper contenente i dati del prodotto
	 */
	protected WizardProdottoHelper getSessionHelper() {
		WizardProdottoHelper helper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		return helper;
	}
	
}
