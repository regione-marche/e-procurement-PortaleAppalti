package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.TokenInterceptor;
import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Consente la generazione del PDF per la domanda di iscrizione.
 *
 * @author stefano.sabbadin
 * @since 1.8.2
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class GenExcelPrezziScadenzeAction extends EncodedDataAction implements SessionAware, IDownloadAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6766785870217564846L;

	private static final int INDICE_RIGA_INIZIALE = 2;

	private Map<String, Object> session;

	private IAppParamManager appParamManager;
	private IBandiManager bandiManager;
	private ICataloghiManager cataloghiManager;
	
	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;

	/**
	 * Riferimento alla url della pagina per visualizzare eventuali problemi
	 * emersi.
	 */
	@Validate(EParamValidation.URL)
	private String urlPage;

	/**
	 * Riferimento al frame della pagina in cui visualizzare il contenuto della
	 * risposta.
	 */
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;
	@Validate(EParamValidation.CODICE)
	private String catalogo;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
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
			"?actionPath=/ExtStr2/do/FrontEnd/Cataloghi/variazionePrezziScadenzeChoices.action" +
			"&currentFrame=" + this.getCurrentFrame() + 
			"&catalogo=" + this.catalogo;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
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
			
			SearchResult<ProdottoType> prodotti = null;
			boolean procedi = true;
			try {
				ProdottiSearchBean parametri = new ProdottiSearchBean();
				parametri.setiDisplayLength(0);
				parametri.setStato(CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO);
				prodotti = this.cataloghiManager.searchProdotti(
						this.catalogo, 
						null, 
						this.getCurrentUser().getUsername(), 
						parametri);
				if (prodotti == null || prodotti.getDati().isEmpty()) {
					session.put(ERROR_DOWNLOAD, this.getText("Errors.sheet.noResultToExport"));
					this.setTarget(INPUT);
					procedi = false;
				}
			} catch (NumberFormatException e) {
				ApsSystemUtils.logThrowable(e, this, "createExcel");
				session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
				this.setTarget(INPUT);
				procedi = false;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "createExcel");
				session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
				this.setTarget(INPUT);
				procedi = false;
			}

			if (procedi) {
				try {
					ServletContext ctx = this.getRequest().getSession().getServletContext();
					
					if (ctx.getResource(PortGareSystemConstants.MODELLO_EXCEL_VARIAZIONE_PREZZI_SCADENZE) == null) {
						session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.TEMPLATE_NOT_FOUND.name()));
						this.setTarget(INPUT);
					} else {
						HSSFWorkbook wb;
						try {
							String percorsoModelliExcel = ctx.getRealPath(PortGareSystemConstants.MODELLO_EXCEL_VARIAZIONE_PREZZI_SCADENZE);
							wb = UtilityExcel.leggiModelloExcel(percorsoModelliExcel);
							this.populateExcel(wb, prodotti.getDati());
						} catch (ExcelException e) {
							ApsSystemUtils.logThrowable(e, this, "createExcel");
							session.put(ERROR_DOWNLOAD, this.getText(e.getCodiceErrore()));
							this.setTarget(INPUT);
						} catch (Throwable e) {
							ApsSystemUtils.logThrowable(e, this, "createExcel");
							session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
							this.setTarget(INPUT);
						}
					}
				} catch (Throwable e) {
					ApsSystemUtils.logThrowable(e, this, "createExcel");
					session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
					this.setTarget(INPUT);
				}
			}
		}
		
		if( !SUCCESS.equals(this.getTarget()) ) {
			TokenInterceptor.redirectToken();
		}
		
		return this.getTarget();
	}

	/**
	 * Genera fisicamente il file excel a partire dagli articoli e relative
	 * quantita' indicate nel mini-wizard
	 */
	private void populateExcel(
			HSSFWorkbook wb, 
			List<ProdottoType> prodotti) throws ApsException, ExcelException 
	{
		// si determina il nome del file di destinazione nell'area
		// temporanea
		this.filename = PortGareSystemConstants.NOME_MODELLO_EXCEL_VARIAZIONE_PREZZI_SCADENZE
						+ FileUploadUtilities.generateFileName() + ".xls";

		DizionarioStili stili = new DizionarioStili(wb);
		createCustomStyles(wb, stili);
		HSSFSheet sheet = wb.getSheetAt(0);

		int indiceRigaCorrente = INDICE_RIGA_INIZIALE;
		for (ProdottoType prodotto : prodotti) {

			ArticoloType articolo = null;

			//Se sto inserendo più di una riga copio quella precedente per avere 
			//le formattazioni corrette
			if (indiceRigaCorrente > INDICE_RIGA_INIZIALE) {
				UtilityExcel.copiaRiga(sheet, indiceRigaCorrente - 1, indiceRigaCorrente, stili.getStili());
			}
			try {
				articolo = this.cataloghiManager.getArticolo(prodotto.getIdArticolo());
				indiceRigaCorrente++;
				fillArticoloData(sheet, indiceRigaCorrente, prodotto, articolo, stili);
			} catch (Throwable aps) {
				throw new ExcelException(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name(), aps);
			}
		}
		writeLegend(sheet, indiceRigaCorrente + 3, stili);
		UtilityExcel.protectSheet(wb, CataloghiConstants.SHEET_PRODOTTI);
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
				stili.aggiungiCustomStyle(style);
				stiliRequired.add(style);
			}
		}
		for (HSSFCellStyle stile : stiliRequired) {
			HSSFCellStyle newStileRequired = wb.createCellStyle();
			newStileRequired.cloneStyleFrom(stile);
			newStileRequired.setFillForegroundColor(HSSFColor.ORANGE.index);
			newStileRequired.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
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
	private void fillArticoloData(
			HSSFSheet sheet, 
			int numRiga, 
			ProdottoType prodotto, 
			ArticoloType articolo, 
			DizionarioStili stili) 
	{
		int numColonna = 1;

		//Codice articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getCodice(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Descrizione	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getDescrizione(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Colore articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, (CataloghiConstants.TIPO_PRODOTTO_BENE.equals(Integer.valueOf(articolo.getTipo())))
						? articolo.getColore() : "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Codice prodotto fornitore
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, prodotto.getCodiceProdottoFornitore(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Marca prodotto produttore
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, prodotto.getMarcaProdottoProduttore(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Codice prodotto produttore
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, prodotto.getCodiceProdottoProduttore(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Nome commerciale
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, prodotto.getNomeCommerciale(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Unità di misura su cui è espresso il prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getUnitaMisuraDetermPrezzo() != null
						? this.getMaps().get("tipiUnitaMisura").get(articolo.getUnitaMisuraDetermPrezzo()) : "", stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_LOCKED"));

		//Prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, prodotto.getPrezzoUnitario(), stili.getStile("DECIMALE_5_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Decimali prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, (long) articolo.getNumDecimaliDetermPrezzo(), stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_LOCKED"));

		//Data scadenza offerta
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, UtilityDate.convertiData(prodotto.getDataScadenzaOfferta(), UtilityDate.FORMATO_GG_MM_AAAA), stili.getStile("DATA_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));

		//Scrivo l'id del prodotto per l'importazione successiva
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, prodotto.getId(), stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_LOCKED_HIDDEN"));
	}

	/**
	 * ...
	 */
	private void writeLegend(HSSFSheet sheet, int numRiga, DizionarioStili stili) {

		UtilityExcel.scriviCella(sheet, 1, numRiga, "LEGENDA", stili.getStile("STRINGA_LEFT_CALIBRI_BOLD_LOCKED"));

		UtilityExcel.scriviCella(sheet, 2, numRiga++, "cella editabile", stili.getStile("STRINGA_CENTER_CALIBRI_NORMAL_UNLOCKED_REQUIRED"));
	}
}
