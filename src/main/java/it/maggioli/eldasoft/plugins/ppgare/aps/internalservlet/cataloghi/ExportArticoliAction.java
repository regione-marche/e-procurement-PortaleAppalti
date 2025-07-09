package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataValidation;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard
 * d'inserimento prodotto
 *
 * @author Marco.Perazzetta
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class ExportArticoliAction extends AbstractOpenPageAction implements IDownloadAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -999118947632964207L;
	
	private static final int INDICE_RIGA_INIZIALE 		= 2;
	private static final String DV_IVA 					= "'IVA'!$A$1:$A$4";
	private static final int MAX_QUANTITA_ESPORTABILE 	= 20;

	private ICataloghiManager cataloghiManager;

	@Validate(EParamValidation.CODICE)
	private String catalogo;
	private List<ArticoloType> articoli;
	@Validate(EParamValidation.DIGIT)
	private String[] articoliSelezionati;
	@Validate(EParamValidation.DIGIT)
	private String[] articoliDaEsportare;

	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	
	/**
	 * Riferimento alla url della pagina per visualizzare eventuali problemi
	 * emersi.
	 */
	private String urlPage;
	
	/**
	 * Riferimento al frame della pagina in cui visualizzare il contenuto della
	 * risposta.
	 */
	private String currentFrame;

	/**
	 * Per ogni articolo selezionato indica se e' possibile inserire prodotti
	 * oppure se si e' gia' raggiunto il limite.
	 */
	private boolean[] disponibili;

	/**
	 * Per ogni articolo selezionato indica la quantita' residua di prodotti
	 * inseribili
	 */
	private Long[] quantita;

	//Massimo numero di prodotti per articolo
	private Long massimaQuantita;

	//Quantita' di prodotti da esportare
	private Long quantitaProdotti;

	
	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public List<ArticoloType> getArticoli() {
		return articoli;
	}

	public void setArticoli(List<ArticoloType> articoli) {
		this.articoli = articoli;
	}

	public String[] getArticoliSelezionati() {
		return articoliSelezionati;
	}

	public void setArticoliSelezionati(String[] articoliSelezionati) {
		this.articoliSelezionati = articoliSelezionati;
	}

	public boolean[] getDisponibili() {
		return disponibili;
	}

	public void setDisponibili(boolean[] disponibili) {
		this.disponibili = disponibili;
	}

	public Long[] getQuantita() {
		return quantita;
	}

	public void setQuantita(Long[] quantita) {
		this.quantita = quantita;
	}

	public String[] getArticoliDaEsportare() {
		return articoliDaEsportare;
	}

	public void setArticoliDaEsportare(String[] articoliDaEsportare) {
		this.articoliDaEsportare = articoliDaEsportare;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	public String getUrlPage() {
		return urlPage;
	}

	public String getCurrentFrame() {
		return currentFrame;
	}

	@Override
	public String getUrlErrori() {
		return this.getUrlPage() + 
			"?actionPath=/ExtStr2/do/FrontEnd/Cataloghi/openPageExportArticles.action" +
			"&currentFrame=" + this.getCurrentFrame();
	}

	public Long getMassimaQuantita() {
		return massimaQuantita;
	}

	public void setMassimaQuantita(Long massimaQuantita) {
		this.massimaQuantita = massimaQuantita;
	}

	public Long getQuantitaProdotti() {
		return quantitaProdotti;
	}

	public void setQuantitaProdotti(Long quantitaProdotti) {
		this.quantitaProdotti = quantitaProdotti;
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {

		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
			this.articoliSelezionati = (String[]) this.session.remove(PortGareSystemConstants.ARTICOLI_X_EXPORT_IMPORT);
			this.catalogo = (String) this.session.remove(PortGareSystemConstants.CATALOGO);
			this.setTarget("open");
		}
		
		// se si proviene dall'EncodedDataAction di ProcessPageRiepilogo con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if (INPUT.equals(this.getTarget()) || "errorWS".equals(this.getTarget())) {
			this.setTarget("open");
		}
		CarrelloProdottiSessione carrello = (CarrelloProdottiSessione) this.session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);

		if (null == this.getCurrentUser()
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME) 
			|| carrello == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {

			// la sessione non e' scaduta, per cui proseguo regolarmente
			boolean processOK = false;
			try {
				if (this.quantitaProdotti == null || this.quantitaProdotti == 0) {
					this.quantitaProdotti = 1L;
				}
				this.articoli = new ArrayList<ArticoloType>();
				this.disponibili = new boolean[this.articoliSelezionati.length];
				this.quantita = new Long[this.articoliSelezionati.length];
				this.setMassimaQuantita(carrello.getListaProdottiPerCatalogo().get(this.catalogo)
						.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo().longValue());
				int i = 0;
				for (String articoloId : this.articoliSelezionati) {
					ArticoloType articolo = this.cataloghiManager.getArticolo(new Long(articoloId));
					Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(
							new Long(articoloId),
							this.getCurrentUser().getUsername());
					long numeroProdottiOECarrello = carrello.calculateProdottiCaricatiOE(new Long(articoloId));
					Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
					this.disponibili[i] = (prodottiCaricati < getMassimaQuantita());
					this.quantita[i] = prodottiCaricati;
					this.articoli.add(articolo);
					i++;
				}
				processOK = true;
				
			} catch (NumberFormatException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				this.addActionError(this.getText("Errors.gestioneProdotti.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				this.addActionError(this.getText("Errors.gestioneProdotti.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			if (processOK) {
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
								 PortGareSystemConstants.WIZARD_PAGINA_RIEPILOGO);
				this.setTarget("open");
			}
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String back() {
		String target = "back";
		return target;
	}

	/**
	 * ... 
	 */
	public String createExcel() {
		CarrelloProdottiSessione carrello = (CarrelloProdottiSessione) this.session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		
		if (null == this.getCurrentUser()
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			|| carrello == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {

			try {
				ServletContext ctx = this.getRequest().getSession().getServletContext();
				
				if (ctx.getResource(PortGareSystemConstants.MODELLO_EXCEL_IMPORT_PRODOTTI) == null) {
					session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.TEMPLATE_NOT_FOUND.name()));
					this.setTarget(INPUT);
				} else {
					boolean procedi = true;

					if (this.quantitaProdotti == null
						|| this.quantitaProdotti < 1
						|| this.quantitaProdotti > this.massimaQuantita
						|| this.quantitaProdotti > MAX_QUANTITA_ESPORTABILE) {
						session.put(ERROR_DOWNLOAD, 
								    this.getText("Errors.sheet.quantitaProdottiDaEsportareNonValida",
								    			 new String[] {Math.min(this.massimaQuantita, MAX_QUANTITA_ESPORTABILE) + ""}));
						this.setTarget(INPUT);
						procedi = false;
					}

					if (procedi) {
						HSSFWorkbook wb;
						try {
							String percorsoModelliExcel = ctx.getRealPath(PortGareSystemConstants.MODELLO_EXCEL_IMPORT_PRODOTTI);
							wb = UtilityExcel.leggiModelloExcel(percorsoModelliExcel);
							this.populateExcel(wb, carrello);
						} catch (ExcelException e) {
							ApsSystemUtils.logThrowable(e, this, "createExcel");
							session.put(ERROR_DOWNLOAD, this.getText(e.getCodiceErrore()));
							this.setTarget(INPUT);
						} catch (Throwable  e) {
							ApsSystemUtils.logThrowable(e, this, "createExcel");
							session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
							this.setTarget(INPUT);
						}
					}
				}
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "createExcel");
				session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
				this.setTarget(INPUT);
			}
		}
		
		this.session.put(PortGareSystemConstants.ARTICOLI_X_EXPORT_IMPORT, this.articoliSelezionati);
		if (INPUT.equals(this.getTarget())) {
			this.session.put(PortGareSystemConstants.CATALOGO, this.catalogo);
		}
		
		return this.getTarget();
	}

	/**
	 * Genera fisicamente il file excel a partire dagli articoli e relative
	 * quantita' indicate nel mini-wizard
	 */
	private void populateExcel(HSSFWorkbook wb, CarrelloProdottiSessione carrello) throws ApsException, ExcelException {
		// si determina il nome del file di destinazione nell'area temporanea
		this.filename = PortGareSystemConstants.NOME_MODELLO_EXCEL_IMPORT_PRODOTTI
						+ FileUploadUtilities.generateFileName() + ".xls";

		DizionarioStili stili = new DizionarioStili(wb);
		createCustomStyles(wb, stili);
		HSSFSheet sheet = wb.getSheet(CataloghiConstants.SHEET_PRODOTTI);

		int indiceRigaCorrente = INDICE_RIGA_INIZIALE;

		for (String articoloId : this.articoliDaEsportare) {
			try {
				ArticoloType articolo = this.cataloghiManager.getArticolo(new Long(articoloId));

				Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(
						new Long(articoloId),
						this.getCurrentUser().getUsername());
				long numeroProdottiOECarrello = carrello.calculateProdottiCaricatiOE(new Long(articoloId));
				Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
				long numeroRecordDaGenerare = Math.min(this.massimaQuantita - prodottiCaricati, this.quantitaProdotti);

				for (int i = 0; i < numeroRecordDaGenerare; i++) {
					if (indiceRigaCorrente > INDICE_RIGA_INIZIALE) {
						//Se sto inserendo pi� di una riga copio quella precedente per avere 
						//le formattazioni corrette
						UtilityExcel.copiaRiga(sheet, indiceRigaCorrente - 1, indiceRigaCorrente, stili.getStili());
					}
					indiceRigaCorrente++;
					fillArticoloData(sheet, indiceRigaCorrente, articolo, stili);
				}
			} catch (Throwable aps) {
				throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), aps);
			}
		}

		writeLegend(sheet, indiceRigaCorrente + 3, stili);
		UtilityExcel.protectSheet(wb, CataloghiConstants.SHEET_PRODOTTI);
		UtilityExcel.protectSheet(wb, CataloghiConstants.SHEET_IVA);
		UtilityExcel.protectVersionSheet(wb);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e) {
			throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), e);
		} catch (Throwable e) {
			throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), e);
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), ex);
			}
		}
		this.inputStream = new ByteArrayInputStream(os.toByteArray());
	}

	/**
	 * ... 
	 */
	private void createCustomStyles(HSSFWorkbook wb, DizionarioStili stili) {

		List<HSSFCellStyle> stiliRequired = new ArrayList<HSSFCellStyle>();
		
		for (String styleKey : stili.getStili().keySet()) {
			if (styleKey.contains(DizionarioStili.CellaBloccata.UNLOCKED.name())) {
				HSSFCellStyle style = stili.getStile(styleKey);
				style.setFillForegroundColor(HSSFColor.YELLOW.index);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				UtilityExcel.aggiungiBordoSuperiore(style, HSSFCellStyle.BORDER_THIN, HSSFColor.BLACK.index);
				UtilityExcel.aggiugiBordoInferiore(style, HSSFCellStyle.BORDER_THIN, HSSFColor.BLACK.index);
				UtilityExcel.aggiugiBordoSinistro(style, HSSFCellStyle.BORDER_THIN, HSSFColor.BLACK.index);
				UtilityExcel.aggiugiBordoDestro(style, HSSFCellStyle.BORDER_THIN, HSSFColor.BLACK.index);
				stili.aggiungiCustomStyle(style);
				stiliRequired.add(style);
			}
		}
		for (HSSFCellStyle stile : stiliRequired) {
			HSSFCellStyle newStileRequired = wb.createCellStyle();
			newStileRequired.cloneStyleFrom(stile);
			newStileRequired.setFillForegroundColor(HSSFColor.ORANGE.index);
			newStileRequired.setUserStyleName(stile.getUserStyleName() + DizionarioStili.SEPARATORE_CHIAVE + "REQUIRED");
			stili.aggiungiCustomStyle(newStileRequired);
		}
		HSSFCellStyle stileNascosto = wb.createCellStyle();
		stileNascosto.cloneStyleFrom(stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_LOCKED"));
		stileNascosto.setHidden(true);
		stileNascosto.setUserStyleName("STRINGA_CENTER_CALIBRI_NORMAL_LOCKED_HIDDEN");
		HSSFFont fontNascosto = wb.createFont();
		fontNascosto.setFontName("CALIBRI_NORMAL_TRANSPARENT");
		fontNascosto.setColor(HSSFColor.WHITE.index);
		stili.aggiungiCustomFont("CALIBRI_NORMAL_TRANSPARENT", fontNascosto);
		stileNascosto.setFont(fontNascosto);
		stili.aggiungiCustomStyle(stileNascosto);
	}

	/**
	 * ... 
	 */
	private void fillArticoloData(HSSFSheet sheet, int numRiga, ArticoloType articolo, DizionarioStili stili) {
		int numColonna = 1;

		//Tipo	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, 
				articolo.getTipo() != null ? this.getMaps().get("tipiArticolo").get(articolo.getTipo()) : "", 
				stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Codice articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getCodice(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Descrizione	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getDescrizione(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Descrizione tecnica	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getDescrizioneTecnica(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Colore articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, 
				(CataloghiConstants.TIPO_PRODOTTO_BENE.equals(Integer.valueOf(articolo.getTipo()))) ? articolo.getColore() : "", 
				stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Note articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getNote(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Codice prodotto fornitore
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		if (CataloghiConstants.TIPO_PRODOTTO_BENE.equals(Integer.valueOf(articolo.getTipo()))) {
			//Marca prodotto produttore
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

			//Codice prodotto produttore
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));
		} else {
			//Marca prodotto produttore
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

			//Codice prodotto produttore
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));
		}

		//Nome commerciale
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Descrizione aggiuntiva
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", 
				(articolo.isObbligoDescrizioneAggiuntiva()) ? stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED") : stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Dimensioni
		if (CataloghiConstants.TIPO_PRODOTTO_BENE.equals(Integer.valueOf(articolo.getTipo()))) {
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", 
					(articolo.isObbligoDimensioni()) ? stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED") : stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));
		} else {
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));
		}

		if (articolo.isObbligoGaranzia()) {
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));
		} else {
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_LOCKED"));
		}

		//Unità di misura su cui è espresso il prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, 
				articolo.getUnitaMisuraDetermPrezzo() != null ? this.getMaps().get("tipiUnitaMisura").get(articolo.getUnitaMisuraDetermPrezzo()) : "", 
				stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("DECIMALE_5_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Decimali prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, (long) articolo.getNumDecimaliDetermPrezzo(), stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_LOCKED"));

		if (CataloghiConstants.TIPO_PREZZO_UNITA_DI_MISURA.equals(articolo.getPrezzoUnitarioPer())) {
			//Num unità su cui è espresso il prezzo
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, null, stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

			//Unità di misura per l'acquisto
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, 
					articolo.getUnitaMisuraAcquisto() != null ? this.getMaps().get("tipiUnitaMisura").get(articolo.getUnitaMisuraAcquisto()) : "", 
					stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));
		} else {
			//Num unità su cui è espresso il prezzo
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, null, stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_LOCKED"));

			//Unità di misura per l'acquisto
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));
		}

		//Aliquota IVA
		UtilityExcel.aggiungiDataValidation(sheet, numRiga - 1, numRiga - 1, numColonna - 1, numColonna - 1, DV_IVA,
						"Errore", "Valore per il campo IVA non valido", DataValidation.ErrorStyle.STOP);
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, "", stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Lotto minimo per unità di misura
		if (!CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA.equals(articolo.getPrezzoUnitarioPer())
			&& !CataloghiConstants.TIPO_PREZZO_CONFEZIONE.equals(articolo.getPrezzoUnitarioPer())) {
			//Lotto minimo per unità di misura
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, null, stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));
		} else {
			//Lotto minimo per unità di misura
			UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getQuantitaUnitaAcquisto(), stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_LOCKED"));
		}

		//Limite minimo lotto minimo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getQuantitaMinimaUnitaAcquisto(), stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_LOCKED"));

		//Limite massimo lotto minimo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getQuantitaMassimaUnitaAcquisto(), stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_LOCKED"));

		//Tempo di consegna
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, null, stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Tempo massimo di consegna
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, (long) articolo.getTempoMaxConsegna(), stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_LOCKED"));

		//Unita' di misura tempo di consegna
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, 
				articolo.getUnitaMisuraTempoConsegna() != null ? this.getMaps().get("tipiUnitaMisuraTempiConsegna").get(articolo.getUnitaMisuraTempoConsegna()) : "", 
				stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Data scadenza offerta
		GregorianCalendar dataScadenza = new GregorianCalendar();
		dataScadenza.add(Calendar.YEAR, 1);
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, UtilityDate.convertiData(dataScadenza.getTime(), UtilityDate.FORMATO_GG_MM_AAAA), stili.getStile("DATA_LEFT_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Scrivo l'id dell'articolo per l'importazione successiva
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getId(), stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_LOCKED_HIDDEN"));
	}

	/**
	 * ... 
	 */
	private void writeLegend(HSSFSheet sheet, int numRiga, DizionarioStili stili) {

		UtilityExcel.scriviCella(sheet, 1, numRiga, "LEGENDA", stili.getStile("STRINGA_LEFT_CALIBRI_BOLD_LOCKED"));

		UtilityExcel.scriviCella(sheet, 2, numRiga++, "valore opzionale", stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_UNLOCKED"));

		UtilityExcel.scriviCella(sheet, 2, numRiga, "valore obbligatorio", stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));
	}
}
