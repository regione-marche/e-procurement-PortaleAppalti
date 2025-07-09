package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ImportProdottiResultBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Consente la generazione del PDF di riepilogo delle modifiche ai prodotti.
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class ImportVariazionePrezziScadenzeAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7877095906255185322L;

	private Map<String, Object> session;

	private IAppParamManager appParamManager;
	private ICataloghiManager cataloghiManager;
	private IEventManager eventManager;

	@Validate(EParamValidation.CODICE)
	private String catalogo;

	private File allegato;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String allegatoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String allegatoFileName;

	private ImportProdottiResultBean result = new ImportProdottiResultBean();
	private static final int INDICE_RIGA_INIZIALE = 2;
	private static final int INDICE_COLONNA_ID_PRODOTTO = 11;
	private static final int INDICE_COLONNA_PREZZO = 8;
	private static final int INDICE_COLONNA_DATA_SCADENZA = 10;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public File getAllegato() {
		return allegato;
	}

	public void setAllegato(File allegato) {
		this.allegato = allegato;
	}

	public String getAllegatoContentType() {
		return allegatoContentType;
	}

	public void setAllegatoContentType(String allegatoContentType) {
		this.allegatoContentType = allegatoContentType;
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

	public ImportProdottiResultBean getResult() {
		return result;
	}

	public void setResult(ImportProdottiResultBean result) {
		this.result = result;
	}

	/**
	 * Funzione di prezzi e scadenze prodotti da file excel nei 3 formati .xls,
	 * .xlsx e .ots. Controlla che il formato del file sia corretto. Verifica ogni
	 * prodotto (singola riga del file) nei campi di interesse e lo inserisce nel
	 * carrello modifiche solo se non &egrave; stato gi&agrave; modificato, &egrave; in stato 'a
	 * catalogo' e non presenta errori. In ogni caso porta ad una pagina di
	 * riepilogo con un dettaglio sull'importazione (numero di prodotti
	 * processati, prodotti inseriti in bozza, prodotti scartati e prodotti con
	 * errori (per quest'ultimo tipo ci sar&agrave; un elenco di dettaglio errori).
	 *
	 * @return the target to reach
	 */
	public String load() {
		try{
			if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				int allegatoSize = FileUploadUtilities.getFileSize(this.allegato);
				
				// valida l'upload del documento...
				getUploadValidator()
						.setActualTotalSize(allegatoSize)
						.setDocumento(allegato)
						.setDocumentoFileName(allegatoFileName)
						.setDocumentoFormato(PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL)
						.setOnlyP7m(false)
						.setEventoDestinazione(catalogo)
						.setEventoMessaggio("Import variazione prezzi scadenze:"
								  			+ " file=" + this.allegatoFileName
								  			+ ", dimensione=" + allegatoSize + "KB");

				if ( !getUploadValidator().validate() ) {
					actionErrorToFieldError();
					this.setTarget(INPUT);
				} else {
	//				// import SINCRONO...
	//				CarrelloProdottiSessione carrelloProdotti
	//								= (CarrelloProdottiSessione) this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
	//				try {
	//					Object sheet = UtilityExcel.leggiFoglioExcelPerImportazione(this.allegatoFileName,
	//									PortGareSystemConstants.NOME_FOGLIO_EXCEL_VARIAZIONE_PREZZI_SCADENZE,
	//									this.allegato, CataloghiConstants.VERSIONE_MODELLO_EXCEL_IMPORT_VARIAZIONE_OFFERTA);
	//					ProdottiCatalogoSessione prodotti = importData(sheet, carrelloProdotti);
	//					if (this.getResult().getSuccessRows() > 0) {
	//						prodotti.setVariazioneOfferta(true);
	//					}
	//				} catch (ExcelException e) {
	//					ApsSystemUtils.logThrowable(e, this, "createExcel");
	//					this.addActionError(UtilityExcel.errore(e));
	//					this.setTarget(INPUT);
	//				}
					
					// import ASINCRONO...
					// reindirizza l'action alla pagina dei risultati
					// nella quale viene richiamata la action di "import asincrono"
					// Vedi: ImportVariazionePrezziScadenzeAsyncAction
					try {
						String dstFileName = this.allegato.getAbsolutePath() + "." + 
											 FilenameUtils.getExtension(this.allegatoFileName);
						FileUtils.copyFile(this.allegato, new File(dstFileName));
			            
						this.allegatoFileName = dstFileName;
						
						this.setTarget(SUCCESS);
					} catch(Throwable e) {
						//this.addActionError(e.getMessage());
						this.setTarget(INPUT);
					}
				}
				
				this.eventManager.insertEvent(getUploadValidator().getEvento());
			}
			
			}catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "importData");
				ExceptionUtils.manageExceptionError(t, this);
				
			}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	public ProdottiCatalogoSessione importData(Object sheet, CarrelloProdottiSessione carrelloProdotti) throws ExcelException {

		int numeroRigheProcessate = 0;
		int numeroRigheSuccesso = 0;
		Object row;
		List<String> erroriRiga;
		ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(this.catalogo);
		boolean goOn = true;
		WizardProdottoHelper prodottoHelper = null;
		Double prezzoUnitario;
		Date dataScadenzaOfferta;
		boolean prezzoNonAggiornato = false;
		boolean scadenzaNonAggiornata = false;

		for (int rowIndex = INDICE_RIGA_INIZIALE; rowIndex <= UtilityExcel.ricavaNumeroRigheFoglio(sheet); rowIndex++) {

			erroriRiga = new ArrayList<String>();
			prezzoUnitario = null;
			dataScadenzaOfferta = null;
			DecimalFormatSymbols originalFormat = new DecimalFormatSymbols(Locale.ITALIAN);
			originalFormat.setGroupingSeparator('.');
			originalFormat.setDecimalSeparator(',');
			//DecimalFormat dfOriginal = new DecimalFormat("#,##0.00000", originalFormat);
			prezzoNonAggiornato = false;
			scadenzaNonAggiornata = false;
			
			try {
				row = UtilityExcel.leggiRiga(sheet, rowIndex);
				if (row == null || UtilityExcel.rigaVuota(row)) {
					//fine dati da importare, che devono essere contigui
					break;
				}
			} catch (ExcelException ex) {
				erroriRiga.add(UtilityExcel.errore(ex));
				goOn = false;
			}

			if (goOn) {

				numeroRigheProcessate++;

				try {
					Number idProdottoNumber = UtilityExcel.leggiNumero(
							sheet, rowIndex, INDICE_COLONNA_ID_PRODOTTO, "ID prodotto", null, erroriRiga);
					long idProdotto = idProdottoNumber.longValue();
					if (prodotti.prodottoGiaModificato(idProdotto)) {
						prodottoHelper = prodotti.getProdotto(
								prodotti.getProdottiModificati(),
								idProdotto);
					} else {
						prodottoHelper = WizardProdottoHelper.getInstance(
								this.session,
								this.cataloghiManager,
								carrelloProdotti,
								this.getCurrentUser().getUsername(),
								carrelloProdotti.getCurrentCatalogo(),
								CataloghiConstants.PRODOTTO_MODIFICATO,
								false,
								true,
								idProdotto,
								null,
								null);
					}
					if (prodottoHelper == null) {
						erroriRiga.add(this.getText("Errors.sheet.cannotRetriveProdotto"));
						goOn = false;
					}
				} catch (Throwable ex) {
					if (ex instanceof ApsException) {
						ApsSystemUtils.logThrowable(ex, this, "importData");
					}
					erroriRiga.add(this.getText("Errors.sheet.cannotRetriveProdotto"));
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
					erroriRiga.add(this.getText("Errors.sheet.statoProdottoErrato", new String[]{statoProdotto}));
				}

				ArticoloType articolo = prodottoHelper.getArticolo().getDettaglioArticolo();

				//Prezzo
				Number prezzoUnitarioNumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, INDICE_COLONNA_PREZZO, this.getTextFromDB("prezzoUnitario"), articolo.getNumDecimaliDetermPrezzo(), erroriRiga);

				if (prezzoUnitarioNumber == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("prezzoUnitario")}));
				} else if (prezzoUnitarioNumber.doubleValue() <= 0) {
					erroriRiga.add(this.getText("Errors.sheet.fieldGreatThan0", new String[] {this.getTextFromDB("prezzoUnitario")}));
				} else {
					prezzoUnitario = prezzoUnitarioNumber.doubleValue();
					BigDecimal bd =  new BigDecimal(prezzoUnitario).setScale(articolo.getNumDecimaliDetermPrezzo(), RoundingMode.HALF_UP);
					String[] prezzoUnitarioArray =bd.toString().split("\\.");//ORIGINALE 
					String decimalPartString = "";
					Integer decimalPart = 0;
					if (prezzoUnitarioArray.length > 1) {
						decimalPartString = prezzoUnitarioArray[1];
						decimalPart = new Integer(decimalPartString);
					}
					if (articolo.getNumDecimaliDetermPrezzo() == 0 && decimalPart > 0) {
						erroriRiga.add(this.getText("Errors.noDecimalsNeeded", new String[] {this.getTextFromDB("prezzoUnitario")}));
					} else if (articolo.getNumDecimaliDetermPrezzo() > 0
							   && decimalPartString.length() > articolo.getNumDecimaliDetermPrezzo()) {
						erroriRiga.add(this.getText("Errors.tooManyDecimals",
										new String[] {this.getTextFromDB("prezzoUnitario"), articolo.getNumDecimaliDetermPrezzo() + ""}));
					} else if (UtilityNumeri.confrontaDouble(
								prodottoHelper.getDettaglioProdotto().getPrezzoUnitario(),
								prezzoUnitario, 
								articolo.getNumDecimaliDetermPrezzo()) == 0) {
						prezzoNonAggiornato = true;
					}
				}

				//Data scadenza offerta
				dataScadenzaOfferta = UtilityExcel.leggiData(
						sheet, rowIndex, INDICE_COLONNA_DATA_SCADENZA, this.getTextFromDB("dataScadenzaOfferta"), erroriRiga);

				if (dataScadenzaOfferta == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("dataScadenzaOfferta")}));
				} else {
					//Controllo che la data scadenza sia >= oggi e <=data oggi+1 anno.
					Date now = new Date();
					Calendar tomorrow = new GregorianCalendar();
					tomorrow.add(Calendar.YEAR, 1);
					if (dataScadenzaOfferta.before(now) || dataScadenzaOfferta.after(tomorrow.getTime())) {
						erroriRiga.add(this.getText("Errors.invalidDataScadenza", new String[] {this.getTextFromDB("dataScadenzaOfferta")}));
					} else if (DateUtils.isSameDay(dataScadenzaOfferta, prodottoHelper.getDettaglioProdotto().getDataScadenzaOfferta())) {
						scadenzaNonAggiornata = true;
					}
				}

				//testo se sono stati inseriti valori per questa riga e sono diversi da quelli a sistema, altrimenti la scarto
				if (prezzoNonAggiornato && scadenzaNonAggiornata) {
					this.getResult().getSkippedRows().put(rowIndex, this.getText("Errors.sheet.nessunaVariazione"));
					continue;
				}
			}

			if (!erroriRiga.isEmpty()) {
				this.getResult().getErrorRows().put(rowIndex, erroriRiga);
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
				numeroRigheSuccesso++;
			}
		}

		this.getResult().setInputRows(numeroRigheProcessate);
		this.getResult().setSuccessRows(numeroRigheSuccesso);
		return prodotti;
	}
	
	@Override
	protected Map<String, String> getTextAndElementAndHtmlID() {
		Map<String, String> toReturn = new HashMap<>();
		
		toReturn.put(getText("Errors.fileNotSet"), "allegato");
		toReturn.put(getText("Errors.emptyFile"), "allegato");
		toReturn.put(getText("Errors.overflowFileSize"), "allegato");
		toReturn.put(getText("Errors.overflowTotalFileSize"), "allegato");
		
		return toReturn;
	}
	
}
