package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.utils.excel.DizionarioStili;
import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ArticoliSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
 * Classe Action per la gestione della ricerca e visualizzazione degli articoli
 *
 * @author Marco Perazzetta
 * @since 1.8.6
 */
public class ArticoliFinderAction extends EncodedDataAction
	implements SessionAware, ModelDriven<ArticoliSearchBean>, IDownloadAction 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8888482353549998441L;
	
	private static final int INDICE_RIGA_DA_ELIMINARE = 1;
	private static final int INDICE_RIGA_INIZIALE = 1;

	/**
	 * validi prima dell'eliminazione delle colonne
	 */
	private static final int INDICE_COLONNA_DATA_SCADENZA_OFFERTA = 25;
	private static final int INDICE_COLONNA_TEMPO_CONSEGNA = 22;
	private static final int INDICE_COLONNA_IVA = 18;
	private static final int INDICE_COLONNA_NUM_UN_ESPR_PREZZO = 16;
	private static final int INDICE_COLONNA_PREZZO = 14;
	private static final int INDICE_COLONNA_MESI_GARANZIA = 12;
	private static final int INDICE_COLONNA_DIMENSIONI = 11;
	private static final int INDICE_COLONNA_DESCRIZIONE_AGGIUNTIVA = 10;
	private static final int INDICE_COLONNA_NOME_COMMERCIALE = 9;
	private static final int INDICE_COLONNA_CODICE_PRODOTTO_PRODUTTORE = 8;
	private static final int INDICE_COLONNA_CODICE_MARCA_PRODOTTO_PRODUTTORE = 7;
	private static final int INDICE_COLONNA_CODICE_PRODOTTO_FORNITORE = 6;
	private static final int INDICE_ULTIMA_COLONNA = 13;

	private Map<String, Object> session;

	private ICataloghiManager cataloghiManager;

	@Validate
	private ArticoliSearchBean model = new ArticoliSearchBean();

	@Validate(EParamValidation.DIGIT)
	private String last;
	List<ArticoloType> listaArticoli = null;
	private InputStream inputStream;
	@Validate(EParamValidation.CODICE)
	private String codiceCatalogo;
	private Boolean impresaAbilitataAlCatalogo;

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

	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setCodiceCatalogo(String codiceCatalogo) {
		this.codiceCatalogo = codiceCatalogo;
	}

	public String getCodiceCatalogo() {
		return codiceCatalogo;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	@Override
	public ArticoliSearchBean getModel() {
		return this.model;
	}

	public List<ArticoloType> getListaArticoli() {
		return listaArticoli;
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

	public Boolean getImpresaAbilitataAlCatalogo() {
		return impresaAbilitataAlCatalogo;
	}

	public void setImpresaAbilitataAlCatalogo(Boolean impresaAbilitataAlCatalogo) {
		this.impresaAbilitataAlCatalogo = impresaAbilitataAlCatalogo;
	}

	@Override
	public String getUrlErrori() {
		return this.getUrlPage() + 
				"?actionPath=/ExtStr2/do/FrontEnd/Cataloghi/searchArticoli.action" +
				"&currentFrame=" + this.getCurrentFrame() + 
				"&codiceCatalogo=" + this.codiceCatalogo + 
				"&last=1";
	}

	/**
	 * ...
	 */
	private void doSearch() throws ApsException {

		if (this.model == null) {
			this.model = new ArticoliSearchBean();
		}
		
		boolean isSessionActive = (this.getCurrentUser() != null
								   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME));
		
		SearchResult<ArticoloType> risultato = this.cataloghiManager.searchArticoli(
						this.codiceCatalogo,
						this.model.getCodiceCategoria(),
						this.model.getCodice(),
						this.model.getTipo(),
						this.model.getDescrizione(),
						this.model.getColore(),
						(isSessionActive && this.model.isMieiArticoli() ? this.getCurrentUser().getUsername() : null),
						this.model.getStartIndexNumber(),
						this.model.getiDisplayLength());
		this.listaArticoli = risultato.getDati();
		this.model.processResult(risultato.getNumTotaleRecord(), risultato.getNumTotaleRecordFiltrati());
	}

	/**
	 * ...
	 */
	public String openSearch() {
		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download dell'excel articoli
		String errore = (String) this.session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
			this.setTarget(SUCCESS);
		}
		this.model = new ArticoliSearchBean();
		this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_ARTICOLI, this.model);
		try {
			if (this.getCurrentUser() != null
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				this.setImpresaAbilitataAlCatalogo(this.cataloghiManager.isImpresaAbilitataCatalogo(
						this.codiceCatalogo,
						this.getCurrentUser().getUsername()));
			} else {
				this.setImpresaAbilitataAlCatalogo(false);
			}
			this.doSearch();
			
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openSearch");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		this.setTarget(SUCCESS);
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di esiti in base ai filtri impostati.
	 *
	 * @return il target a cui andare
	 */
	public String search() {
		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download dell'excel articoli
		String errore = (String) this.session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
			this.setTarget(SUCCESS);
		}
		if ("1".equals(this.last)) {
			ArticoliSearchBean finder = (ArticoliSearchBean) this.session.get(PortGareSystemConstants.SESSION_ID_SEARCH_ARTICOLI);
			this.model = finder;
		}
		if (SUCCESS.equals(this.getTarget())) {
			try {
				if (this.getCurrentUser() != null
					&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
					this.setImpresaAbilitataAlCatalogo(this.cataloghiManager.isImpresaAbilitataCatalogo(
							this.codiceCatalogo,
							this.getCurrentUser().getUsername()));
				} else {
					this.setImpresaAbilitataAlCatalogo(false);
				}
				this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_ARTICOLI, this.model);
				this.doSearch();
				
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "search");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}
		return this.getTarget();
	}

	/**
	 * Restituisce lo stream contenente il documento excel con gli articoli
	 * filtrati
	 *
	 * @return il target a cui andare
	 */
	public String downloadArticoliFiltrati() {
		this.setTarget(SUCCESS);
		
		if (null == this.getCurrentUser() 
			|| this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			boolean procedi = true;
			List<ArticoloType> articoli = new ArrayList<ArticoloType>();
			try {
				if (this.model == null) {
					this.model = new ArticoliSearchBean();
				}
				SearchResult<ArticoloType> risultato = this.cataloghiManager.searchArticoli(
								this.codiceCatalogo,
								this.model.getCodiceCategoria(),
								this.model.getCodice(),
								this.model.getTipo(),
								this.model.getDescrizione(),
								this.model.getColore(),
								(this.model.isMieiArticoli() ? this.getCurrentUser().getUsername() : null),
								0,
								-1);
				if (risultato == null || risultato.getDati().isEmpty()) {
					session.put(ERROR_DOWNLOAD, this.getText("Errors.sheet.noResultToExport"));
					this.setTarget(INPUT);
					procedi = false;
				} else {
					articoli = risultato.getDati();
				}
				
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "downloadArticoliFiltrati");
				session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
				this.setTarget(INPUT);
				procedi = false;
			}

			if (procedi) {
				try {
					ServletContext ctx = this.getRequest().getSession().getServletContext();
					
					if (ctx.getResource(PortGareSystemConstants.MODELLO_EXCEL_IMPORT_PRODOTTI) == null) {
						session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.TEMPLATE_NOT_FOUND.name()));
						this.setTarget(INPUT);
					} else {
						HSSFWorkbook wb;
						try {
							String percorsoModelliExcel = ctx.getRealPath(PortGareSystemConstants.MODELLO_EXCEL_IMPORT_PRODOTTI);
							wb = UtilityExcel.leggiModelloExcel(percorsoModelliExcel);
							this.populateExcel(wb, articoli);
						} catch (ExcelException e) {
							ApsSystemUtils.logThrowable(e, this, "downloadArticoliFiltrati");
							session.put(ERROR_DOWNLOAD, this.getText(e.getCodiceErrore()));
							this.setTarget(INPUT);
						} catch (Throwable e) {
							ApsSystemUtils.logThrowable(e, this, "downloadArticoliFiltrati");
							session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
							this.setTarget(INPUT);
						}
					}
				} catch (Throwable e) {
					ApsSystemUtils.logThrowable(e, this, "downloadArticoliFiltrati");
					session.put(ERROR_DOWNLOAD, UtilityExcel.errore(ExcelException.Errore.EXPORT_UNEXPECTED_ERROR.name()));
					this.setTarget(INPUT);
				}
			}
		}
		return this.getTarget();
	}

	/**
	 * Genera fisicamente il file excel a partire dagli articoli e relative
	 * quantita' indicate nel mini-wizard
	 */
	private void populateExcel(HSSFWorkbook wb, List<ArticoloType> articoli) throws ApsException, ExcelException {
		// si determina il nome del file di destinazione nell'area
		// temporanea
		this.filename = PortGareSystemConstants.NOME_MODELLO_EXCEL_EXPORT_ARTICOLI
						+ FileUploadUtilities.generateFileName() + ".xls";

		DizionarioStili stili = new DizionarioStili(wb);
		HSSFSheet sheet = wb.getSheetAt(0);

		int numMergedRegions = sheet.getNumMergedRegions();
		for (int i = 0; i < numMergedRegions; i++) {
			sheet.removeMergedRegion(0);
		}
		
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_DATA_SCADENZA_OFFERTA, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_TEMPO_CONSEGNA, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_IVA, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_NUM_UN_ESPR_PREZZO, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_PREZZO, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_MESI_GARANZIA, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_DIMENSIONI, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_DESCRIZIONE_AGGIUNTIVA, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_NOME_COMMERCIALE, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_CODICE_PRODOTTO_PRODUTTORE, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_CODICE_MARCA_PRODOTTO_PRODUTTORE, stili.getStili());
		UtilityExcel.eliminaColonna(sheet, INDICE_COLONNA_CODICE_PRODOTTO_FORNITORE, stili.getStili());
		
		sheet.removeRow(sheet.getRow(INDICE_RIGA_DA_ELIMINARE));

		int indiceRigaCorrente = INDICE_RIGA_INIZIALE;

		//UtilityExcel.eliminaColonna
		for (ArticoloType articolo : articoli) {
			indiceRigaCorrente++;
			fillArticoloData(sheet, indiceRigaCorrente, articolo, stili);
		}
		
		// Auto size the column widths
		for (int columnIndex = 0; columnIndex < INDICE_ULTIMA_COLONNA; columnIndex++) {
			sheet.autoSizeColumn(columnIndex);
		}
		
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

	private void fillArticoloData(HSSFSheet sheet, int numRiga, ArticoloType articolo, DizionarioStili stili) {
		int numColonna = 1;

		//Tipo	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getTipo(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Codice articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getCodice(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Descrizione	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getDescrizione(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Descrizione tecnica	articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getDescrizioneTecnica(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Colore articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getColore(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Note articolo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getNote(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Unità di misura su cui è espresso il prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getUnitaMisuraDetermPrezzo(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Decimali prezzo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, (long) articolo.getNumDecimaliDetermPrezzo(), stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_UNLOCKED"));

		//Unità di misura per l'acquisto
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getUnitaMisuraAcquisto(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));

		//Lotto minimo per unità di misura
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getQuantitaUnitaAcquisto(), stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_UNLOCKED"));

		//Limite minimo lotto minimo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getQuantitaMinimaUnitaAcquisto(), stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_UNLOCKED"));

		//Limite massimo lotto minimo
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getQuantitaMassimaUnitaAcquisto(), stili.getStile("DECIMALE_1_CENTER_CALIBRI_NORMAL_UNLOCKED"));

		//Tempo massimo di consegna
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, (long) articolo.getTempoMaxConsegna(), stili.getStile("INTERO_CENTER_CALIBRI_NORMAL_UNLOCKED"));

		//Unita' di misura tempo di consegna
		UtilityExcel.scriviCella(sheet, numColonna++, numRiga, articolo.getUnitaMisuraTempoConsegna(), stili.getStile("STRINGA_LEFT_CALIBRI_NORMAL_UNLOCKED"));
	}
	
}
