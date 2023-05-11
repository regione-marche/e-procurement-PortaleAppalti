package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.Action;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ImportDataAsyncAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ImportDataAsyncStatusBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.commons.lang.time.DateUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class ImportVariazionePrezziScadenzeAsyncAction extends ImportDataAsyncAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2643213125571566734L;

	private static final String SESSION_CARRELLO_PRODOTTI_ASYNCIMPORT = "carrelloProdottiImportAsync";	

	private IAppParamManager appParamManager;
	private ICataloghiManager cataloghiManager;

	@Validate(EParamValidation.CODICE)
	private String catalogo;
	@Validate(EParamValidation.FILE_NAME)
	private String allegatoFileName;
	private InputStream inputStream;

	private static final int INDICE_RIGA_INIZIALE 			= 2;  	//  3 riga (0-based)
	private static final int INDICE_COLONNA_ID_PRODOTTO 	= 11;	// 12 col (0-based)
	private static final int INDICE_COLONNA_PREZZO 			= 8;	//  9 col (0-based)
	private static final int INDICE_COLONNA_DATA_SCADENZA 	= 10;	// 11 col (0-based)

	
	//**************************************************************************
	// inizializza gli oggetti per la formattazione dei dati
	private static final DecimalFormatSymbols originalFormat = new DecimalFormatSymbols(Locale.ITALIAN);	
	static {
		originalFormat.setGroupingSeparator('.');
		originalFormat.setDecimalSeparator(',');
		//DecimalFormat dfOriginal = new DecimalFormat("#,##0.00000", originalFormat);
	}
	//**************************************************************************
	

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public ICataloghiManager getCataloghiManager() {
		return cataloghiManager;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public String getAllegatoFileName() {
		return allegatoFileName;
	}

	public void setAllegatoFileName(String allegatoFileName) {
		this.allegatoFileName = allegatoFileName;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	
	/**
	 * Funzione di prezzi e scadenze prodotti da file excel nei 3 formati .xls,
	 * .xlsx e .ots. Controlla che il formato del file sia corretto. Verifica ogni
	 * prodotto (singola riga del file) nei campi di interesse e lo inserisce nel
	 * carrello modifiche solo se non &egrave; stato gi&agrave;  modificato, &egrave; in stato 'a
	 * catalogo' e non presenta errori. In ogni caso porta ad una pagina di
	 * riepilogo con un dettaglio sull'importazione (numero di prodotti
	 * processati, prodotti inseriti in bozza, prodotti scartati e prodotti con
	 * errori (per quest'ultimo tipo ci sar&agrave; un elenco di dettaglio errori).
	 *
	 * @return the target to reach
	 */	
	public String load() {
		String target = Action.SUCCESS;
		
		this.inputStream = null;
				
		try {				
			// elabora una sottinsieme delle righe del file Excel...
			target = this.importDataAsync(
					SESSION_CARRELLO_PRODOTTI_ASYNCIMPORT,
					INDICE_RIGA_INIZIALE,	
					this.allegatoFileName, 
					PortGareSystemConstants.NOME_FOGLIO_EXCEL_VARIAZIONE_PREZZI_SCADENZE,
					CataloghiConstants.VERSIONE_MODELLO_EXCEL_IMPORT_VARIAZIONE_OFFERTA);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "load");
//				this.addActionError(e.getMessage());
			target = Action.INPUT;
		}					
		return target;
	}

	
//	@Override
//	public String importDataBegin() {
//		return Action.SUCCESS;
//	}
		
	/**
	 * ... 
	 */
	@Override
	public String importDataEnd() {
		if (this.getStatus().getSuccessRows() > 0) {
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) 
				this.getSession().get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		
			ProdottiCatalogoSessione prodotti = carrelloProdotti
				.getListaProdottiPerCatalogo().get(this.catalogo);

			prodotti.setVariazioneOfferta(true);			
		}		
		return Action.SUCCESS;
	}

	
//	@Override
//	public String importDataUpdateStatus(JSONObject json) {
//		return Action.SUCCESS;
//	}

	/**
	 * ... 
	 */
	@Override
	public String importDataRow(Object sheet, ImportDataAsyncStatusBean status) throws ExcelException {		
		
		UserDetails currentUser = (UserDetails)this.getSession().get(SystemConstants.SESSIONPARAM_CURRENT_USER);				
		if(currentUser == null) {
			//return Action.ERROR;		
			return Action.SUCCESS;
		} 		
			
		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.getSession()
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		
		ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo()
			.get(this.catalogo);
					
		boolean goOn = true;
		WizardProdottoHelper prodottoHelper = null;
		Double prezzoUnitario = null;
		Date dataScadenzaOfferta = null;
		boolean prezzoNonAggiornato = false;
		boolean scadenzaNonAggiornata = false;		
		
		if (goOn) {
			
			status.setInputRows(status.getInputRows()+1);
		
			try {
				Number idProdottoNumber = UtilityExcel.leggiNumero(
						sheet, status.getRowIndex(), 
						INDICE_COLONNA_ID_PRODOTTO,	"ID prodotto", null, status.getErroriRiga());
				long idProdotto = idProdottoNumber.longValue();
				if (prodotti.prodottoGiaModificato(idProdotto)) {
					prodottoHelper = prodotti.getProdotto(prodotti.getProdottiModificati(),
									idProdotto);
				} else {
					prodottoHelper = WizardProdottoHelper.getInstance(
							this.getSession(),
							this.cataloghiManager,
							carrelloProdotti,
							currentUser.getUsername(),
							carrelloProdotti.getCurrentCatalogo(),
							CataloghiConstants.PRODOTTO_MODIFICATO,
							false,
							true,
							idProdotto,
							null,
							null);
				}
				if (prodottoHelper == null) {
					status.getErroriRiga().add(this.getText("Errors.sheet.cannotRetriveProdotto"));
					goOn = false;
				}
			} catch (Throwable ex) {
				if (ex instanceof ApsException) {
					ApsSystemUtils.logThrowable(ex, this, "importData");
				}
				status.getErroriRiga().add(this.getText("Errors.sheet.cannotRetriveProdotto"));
				goOn = false;
			}
		}
		
		if (goOn) {
		
			if (!prodottoHelper.getDettaglioProdotto().getStato().equals(CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO)) {
				String statoProdotto = "";
				if (prodottoHelper.getDettaglioProdotto().getStato().equals(CataloghiConstants.STATO_PRODOTTO_ARCHIVIATO)) {
					statoProdotto = CataloghiConstants.DESCRIZIONE_STATO_PRODOTTO_ARCHIVIATO;
				} else if (prodottoHelper.getDettaglioProdotto().getStato().equals(CataloghiConstants.STATO_PRODOTTO_IN_ATTESA)) {
					statoProdotto = CataloghiConstants.DESCRIZIONE_STATO_PRODOTTO_IN_ATTESA;
				} else if (prodottoHelper.getDettaglioProdotto().getStato().equals(CataloghiConstants.STATO_PRODOTTO_NON_CONFORME)) {
					statoProdotto = CataloghiConstants.DESCRIZIONE_STATO_PRODOTTO_NON_CONFORME;
				}
				status.getErroriRiga().add(this.getText("Errors.sheet.statoProdottoErrato", new String[] {statoProdotto}));
			}
		
			ArticoloType articolo = prodottoHelper.getArticolo().getDettaglioArticolo();
		
			// Prezzo
			Number prezzoUnitarioNumber = UtilityExcel.leggiNumero(
					sheet, status.getRowIndex(), INDICE_COLONNA_PREZZO, this.getTextFromDB("prezzoUnitario"), articolo.getNumDecimaliDetermPrezzo(), status.getErroriRiga());
		
			if (prezzoUnitarioNumber == null) {
				status.getErroriRiga().add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("prezzoUnitario")}));
			} else if (prezzoUnitarioNumber.doubleValue() <= 0) {
				status.getErroriRiga().add(this.getText("Errors.sheet.fieldGreatThan0", new String[] {this.getTextFromDB("prezzoUnitario")}));
			} else {
				prezzoUnitario = prezzoUnitarioNumber.doubleValue();
				BigDecimal bd =  new BigDecimal(prezzoUnitario).setScale(articolo.getNumDecimaliDetermPrezzo(), RoundingMode.HALF_UP);
				String[] prezzoUnitarioArray = bd.toString().split("\\.");//ORIGINALE 
				String decimalPartString = "";
				Integer decimalPart = 0;
				if (prezzoUnitarioArray.length > 1) {
					decimalPartString = prezzoUnitarioArray[1];
					decimalPart = new Integer(decimalPartString);
				}
				if (articolo.getNumDecimaliDetermPrezzo() == 0 && decimalPart > 0) {
					status.getErroriRiga().add(this.getText("Errors.noDecimalsNeeded", new String[] {this.getTextFromDB("prezzoUnitario")}));
				} else if (articolo.getNumDecimaliDetermPrezzo() > 0
							&& decimalPartString.length() > articolo.getNumDecimaliDetermPrezzo()) {
					status.getErroriRiga().add(this.getText("Errors.tooManyDecimals",
												new String[] {this.getTextFromDB("prezzoUnitario"), articolo.getNumDecimaliDetermPrezzo() + ""}));
				} else if (UtilityNumeri.confrontaDouble(
							prodottoHelper.getDettaglioProdotto().getPrezzoUnitario(),
							prezzoUnitario, 
							articolo.getNumDecimaliDetermPrezzo()) == 0) {
					prezzoNonAggiornato = true;
				}
			}
		
			// Data scadenza offerta
			dataScadenzaOfferta = UtilityExcel.leggiData(
					sheet, status.getRowIndex(), INDICE_COLONNA_DATA_SCADENZA, this.getTextFromDB("dataScadenzaOfferta"), status.getErroriRiga());
		
			if (dataScadenzaOfferta == null) {
				status.getErroriRiga().add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("dataScadenzaOfferta")}));
			} else {
				// Controllo che la data scadenza sia >= oggi e <=data oggi+1 anno.
				Date now = new Date();
				Calendar tomorrow = new GregorianCalendar();
				tomorrow.add(Calendar.YEAR, 1);
				if (dataScadenzaOfferta.before(now) || dataScadenzaOfferta.after(tomorrow.getTime())) {
					status.getErroriRiga().add(this.getText("Errors.invalidDataScadenza", new String[] {this.getTextFromDB("dataScadenzaOfferta")}));
				} else if (DateUtils.isSameDay(dataScadenzaOfferta, prodottoHelper.getDettaglioProdotto().getDataScadenzaOfferta())) {
					scadenzaNonAggiornata = true;
				}
			}
		
			// testo se sono stati inseriti valori per questa riga e sono diversi 
			// da quelli a sistema, altrimenti la scarto
			if (prezzoNonAggiornato && scadenzaNonAggiornata) {
				status.getSkippedRows().put(status.getRowIndex(), this.getText("Errors.sheet.nessunaVariazione"));				
				return Action.SUCCESS;
			}
		}
				
		if (!status.getErroriRiga().isEmpty()) {
			status.getErrorRows().put(status.getRowIndex(), status.getErroriRiga());
		} else if (goOn) {
			ProdottoType prodotto = prodottoHelper.getDettaglioProdotto();
			Double prezzoUnitarioPerMisuraAcq = prezzoUnitario * prodotto.getQuantitaUMPrezzo();
			BigDecimal prezzoUnitarioPerMisuraAcqRound = BigDecimal.valueOf(prezzoUnitarioPerMisuraAcq).setScale(
							prodottoHelper.getArticolo().getDettaglioArticolo().getNumDecimaliAcquisto(), BigDecimal.ROUND_HALF_UP);
			prodotto.setPrezzoUnitarioPerAcquisto(prezzoUnitarioPerMisuraAcqRound.doubleValue());
			BigDecimal prezzoUnitarioRound = BigDecimal.valueOf(prezzoUnitario).setScale(
							prodottoHelper.getArticolo().getDettaglioArticolo().getNumDecimaliDetermPrezzo(), BigDecimal.ROUND_HALF_UP);
			prodotto.setPrezzoUnitario(prezzoUnitarioRound.doubleValue());
			prodotto.setDataScadenzaOfferta(dataScadenzaOfferta);
			prodottoHelper.setDettaglioProdotto(prodotto);
			prodottoHelper.setDataOperazione(new Date());				
			carrelloProdotti.aggiungiAModificati(prodottoHelper);
			
			status.setSuccessRows(status.getSuccessRows() + 1);
		}
		
		return Action.SUCCESS;
	}
	
}
