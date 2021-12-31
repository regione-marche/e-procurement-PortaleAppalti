package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ImportProdottiResultBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardArticoloHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Consente la generazione del PDF di riepilogo delle modifiche ai prodotti.
 *
 * @author Marco.Perazzetta
 */
public class ImportProductsAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7877095906255185322L;

	private Map<String, Object> session;

	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;
	private ICataloghiManager cataloghiManager;
	private IEventManager eventManager;

	private String catalogo;

	private File allegato;
	private String allegatoContentType;
	private String allegatoFileName;

	private ImportProdottiResultBean result = new ImportProdottiResultBean();
	private static final int INDICE_RIGA_INIZIALE = 2;
	private static final int INDICE_COLONNA_ID_ARTICOLO = 26;
	private static final int INDICE_COLONNA_CODICE_PRODOTTO = 6;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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
	 * Funzione di caricamento prodotti da file excel nei 3 formati .xls, .xlsx e
	 * .ots. Controlla che il formato del file sia corretto. Verifica ogni
	 * prodotto (singola riga del file) nei campi di interesse e lo inserisce nel
	 * carrello in stato bozza solo se non &egrave; stata processata in precedenza
	 * (ovvero &egrave; gi&agrave;  presente un prodotto da inserire, in bozza, nel catalogo
	 * prodotti con medesimo codice prodotto del fornitore) e se non presenta
	 * errori. In ogni caso porta ad una pagina di riepilogo con un dettaglio
	 * sull'importazione (numero di prodotti processati, prodotti inseriti in
	 * bozza, prodotti scartati perch&egrave; gi&agrave;  esistenti e prodotti con errori (per
	 * quest'ultimo tipo ci sar&agrave;  un elenco di dettaglio errori).
	 *
	 * @return the target to reach
	 */
	public String load() {
		try {
			if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				int allegatoSize = FileUploadUtilities.getFileSize(this.allegato);
				
				// traccia l'evento di upload di un file...
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(this.catalogo);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Import prodotti catalogo:" 
								  + " file="+this.allegatoFileName 
								  + ", dimensione=" + allegatoSize + "KB");
	
				boolean controlliOk = true;
	//			controlliOk = controlliOk && this.checkFileSize(this.allegato, 0, this.appParamManager);
	//			controlliOk = controlliOk && this.checkFileName(this.allegatoFileName);
	//			controlliOk = controlliOk && this.checkFileFormat(this.allegato, this.allegatoFileName, PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL);
				controlliOk = controlliOk && this.checkFileSize(this.allegato, this.allegatoFileName, allegatoSize, this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.allegatoFileName, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.allegato, this.allegatoFileName, PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL, evento, false);
	
				if (!controlliOk) {
					this.setTarget(INPUT);
				} else {
	
					CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
						.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
					try {
						Object sheet = UtilityExcel.leggiFoglioExcelPerImportazione(
								this.allegatoFileName,
								PortGareSystemConstants.NOME_MODELLO_EXCEL_IMPORT_PRODOTTI,
								this.allegato, 
								CataloghiConstants.VERSIONE_MODELLO_EXCEL_IMPORT_PRODOTTI);
						DettaglioBandoIscrizioneType dettaglioCatalogo = this.bandiManager.getDettaglioBandoIscrizione(carrelloProdotti.getCurrentCatalogo());
						ProdottiCatalogoSessione prodotti = importData(sheet, carrelloProdotti, dettaglioCatalogo);
						if (this.getResult().getSuccessRows() > 0) {
							ProdottiAction.saveProdotti(
									this.comunicazioniManager, 
									this.eventManager, 
									this.appParamManager, 
									prodotti, 
									this);
						}
					} catch (ExcelException e) {
						ApsSystemUtils.logThrowable(e, this, "load");
						this.addActionError(UtilityExcel.errore(e));
						this.setTarget(INPUT);
					} catch (Throwable e) {
						ApsSystemUtils.logThrowable(e, this, "load");
						this.addActionError(UtilityExcel.errore(ExcelException.Errore.IMPORT_UNEXPECTED_ERROR.name()));
						this.setTarget(INPUT);
					}
				}
				this.eventManager.insertEvent(evento);
			}
		
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "load");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(INPUT);
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
	public ProdottiCatalogoSessione importData(
			Object sheet, CarrelloProdottiSessione carrelloProdotti,
			DettaglioBandoIscrizioneType dettaglioCatalogo) throws ExcelException 
	{
		int numeroRigheProcessate = 0;
		int numeroRigheSuccesso = 0;
		Object row;
		List<String> erroriRiga;
		ProdottiCatalogoSessione prodotti = carrelloProdotti.getListaProdottiPerCatalogo().get(this.catalogo);
		boolean goOn;
		int colIndex;
		WizardProdottoHelper prodottoHelper;
		ProdottoType prodottoType;
		ArticoloType articolo;
		String codiceProdottoFornitore;
		String marcaProdottoProduttore;
		String codiceProdottoProduttore;
		String nomeCommerciale;
		String descrizioneAggiuntiva;
		String dimensioni;
		Integer garanzia;
		Double prezzoUnitario;
		Double quantitaUMPrezzo;
		Integer aliquotaIVA;
		Double quantitaUMAcquisto;
		Integer tempoConsegna;
		Date dataScadenzaOfferta;

		for (int rowIndex = INDICE_RIGA_INIZIALE; rowIndex <= UtilityExcel.ricavaNumeroRigheFoglio(sheet); rowIndex++) {
			erroriRiga = new ArrayList<String>();
			prodottoHelper = new WizardProdottoHelper();
			articolo = null;
			codiceProdottoFornitore = null;
			marcaProdottoProduttore = null;
			codiceProdottoProduttore = null;
			nomeCommerciale = null;
			descrizioneAggiuntiva = null;
			dimensioni = null;
			garanzia = null;
			prezzoUnitario = null;
			quantitaUMPrezzo = null;
			aliquotaIVA = null;
			quantitaUMAcquisto = null;
			tempoConsegna = null;
			dataScadenzaOfferta = null;
			goOn = true;

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
				prodottoHelper.setCodiceCatalogo(this.catalogo);

				try {
					Number idArticoloNumber = UtilityExcel.leggiNumero(
							sheet, 
							rowIndex, 
							INDICE_COLONNA_ID_ARTICOLO,
							"ID articolo", 
							null, 
							erroriRiga);
					long idArticolo = idArticoloNumber.longValue();
					articolo = this.cataloghiManager.getArticolo(idArticolo);
					if (articolo == null) {
						erroriRiga.add(this.getText("Errors.sheet.cannotRetriveArticle"));
						goOn = false;
					}
				} catch (Throwable ex) {
					if (ex instanceof ApsException) {
						ApsSystemUtils.logThrowable(ex, this, "importData");
					}
					erroriRiga.add(this.getText("Errors.sheet.cannotRetriveArticle"));
					goOn = false;
				}
			}

			if (goOn) {
				prodottoHelper.setArticolo(new WizardArticoloHelper(articolo.getId(), articolo));
				prodottoHelper.setCatalogo(dettaglioCatalogo);
				colIndex = INDICE_COLONNA_CODICE_PRODOTTO;

				//Codice prodotto fornitore	colonna 6
				codiceProdottoFornitore = UtilityStringhe.trim(UtilityExcel.leggiStringa(
						sheet, 
						rowIndex,
						colIndex++, 
						this.getTextFromDB("codiceProdottoFornitore"), 
						erroriRiga));
				if (StringUtils.isBlank(codiceProdottoFornitore)) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[]{this.getTextFromDB("codiceProdottoFornitore")}));
				} else {
					if (prodotti.prodottoGiaBozza(codiceProdottoFornitore)) {
						this.getResult().getSkippedRows().put(rowIndex, this.getText("Errors.prodottoAlreadyBozza", new String[] {codiceProdottoFornitore}));
						continue;
					} else if (prodotti.prodottoGiaInserito(codiceProdottoFornitore)) {
						this.getResult().getSkippedRows().put(rowIndex, this.getText("Errors.prodottoAlreadyToInsert", new String[] {codiceProdottoFornitore}));
						continue;
					} else {
						try {
							SearchResult<ProdottoType> prodottiDaSistema = this.cataloghiManager.searchProdotti(
									this.catalogo, articolo.getId(),
									this.getCurrentUser().getUsername(), 
									new ProdottiSearchBean(codiceProdottoFornitore));
							if (prodottiDaSistema != null && prodottiDaSistema.getDati() != null && !prodottiDaSistema.getDati().isEmpty()) {
								this.getResult().getSkippedRows().put(rowIndex, 
																	  this.getText("Errors.prodottoAlreadyAdded", new String[] {codiceProdottoFornitore}));
								continue;
							}
						} catch (Throwable ex) {
							if (ex instanceof ApsException) {
								ApsSystemUtils.logThrowable(ex, this, "importData");
							}
							erroriRiga.add(this.getText("Errors.sheet.cannotRetriveProduct"));
							goOn = false;
						}
						try {
							Long numeroProdottiOESistema = this.cataloghiManager.getNumProdottiOEInArticolo(
									prodottoHelper.getArticolo().getIdArticolo(),
									this.getCurrentUser().getUsername());
							long numeroProdottiOECarrello = carrelloProdotti.calculateProdottiCaricatiOE(prodottoHelper.getArticolo().getIdArticolo());
							Long prodottiCaricati = numeroProdottiOESistema + numeroProdottiOECarrello;
							if (prodottiCaricati >= prodottoHelper.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo()) {
								this.getResult().getSkippedRows().put(rowIndex, 
																	  this.getText("Errors.limiteInserimentoProdottiPerArticolo",
																			  	   new String[] {prodottoHelper.getCatalogo().getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo() + ""}));
								continue;
							}
						} catch (Throwable ex) {
							if (ex instanceof ApsException) {
								ApsSystemUtils.logThrowable(ex, this, "importData");
							}
							erroriRiga.add(this.getText("Errors.sheet.cannotCheckLimiteProdotti", new String[] {articolo.getCodice()}));
							goOn = false;
						}
					}
				}

				//Marca prodotto produttore
				marcaProdottoProduttore = UtilityStringhe.trim(UtilityExcel.leggiStringa(
						sheet, rowIndex, colIndex++, this.getTextFromDB("marcaProdottoProduttore"), erroriRiga));
				if (CataloghiConstants.TIPO_PRODOTTO_BENE.equals(Integer.valueOf(articolo.getTipo())) && StringUtils.isBlank(marcaProdottoProduttore)) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[]{this.getTextFromDB("marcaProdottoProduttore")}));
				}

				//Codice prodotto produttore
				codiceProdottoProduttore = UtilityStringhe.trim(UtilityExcel.leggiStringa(
						sheet, rowIndex, colIndex++, this.getTextFromDB("codiceProdottoProduttore"), erroriRiga));
				if (CataloghiConstants.TIPO_PRODOTTO_BENE.equals(Integer.valueOf(articolo.getTipo())) && StringUtils.isBlank(codiceProdottoProduttore)) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("codiceProdottoProduttore")}));
				}

				//Nome commerciale
				nomeCommerciale = UtilityStringhe.trim(UtilityExcel.leggiStringa(
						sheet, rowIndex, colIndex++, this.getTextFromDB("nomeCommerciale"), erroriRiga));
				if (StringUtils.isBlank(nomeCommerciale)) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("nomeCommerciale")}));
				}

				//Descrizione aggiuntiva
				descrizioneAggiuntiva = UtilityStringhe.trim(UtilityExcel.leggiStringa(
						sheet, rowIndex, colIndex++, this.getTextFromDB("descrizioneAggiuntiva"), erroriRiga));
				if (articolo.isObbligoDescrizioneAggiuntiva() && StringUtils.isBlank(descrizioneAggiuntiva)) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("descrizioneAggiuntiva")}));
				}

				//Dimensioni
				dimensioni = UtilityStringhe.trim(UtilityExcel.leggiStringa(
						sheet, rowIndex, colIndex++, this.getTextFromDB("dimensioni"), erroriRiga));
				if (articolo.isObbligoDimensioni() && StringUtils.isBlank(dimensioni)) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("dimensioni")}));
				}

				//garanzia
				Number garanziaNumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, colIndex++, this.getTextFromDB("garanzia"), 0, erroriRiga);
				if (articolo.isObbligoGaranzia() && garanziaNumber == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("garanzia")}));
				} else if (articolo.isObbligoGaranzia() && garanziaNumber != null) {
					garanzia = garanziaNumber.intValue();
					if (garanzia < CataloghiConstants.DEFAULT_NUM_MESI_GARANZIA) {
						erroriRiga.add(this.getText("Errors.mustBeAbove",
										new String[]{this.getTextFromDB("garanzia"), CataloghiConstants.DEFAULT_NUM_MESI_GARANZIA + ""}));
					}
				}

				//Unita'  di misura su cui e' espresso il prezzo
				colIndex++;

				//Prezzo
				Number prezzoUnitarioNumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, colIndex++, this.getTextFromDB("prezzoUnitario"), articolo.getNumDecimaliDetermPrezzo(), erroriRiga);
				if (prezzoUnitarioNumber == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("prezzoUnitario")}));
				} else if (prezzoUnitarioNumber.doubleValue() <= 0) {
					erroriRiga.add(this.getText("Errors.sheet.fieldGreatThan0", new String[] {this.getTextFromDB("prezzoUnitario")}));
				} else {
					prezzoUnitario = prezzoUnitarioNumber.doubleValue();
					String[] prezzoUnitarioArray = prezzoUnitario.toString().split("\\.");
					String decimalPartString = "";
					Integer decimalPart = 0;
					if (prezzoUnitarioArray.length > 1) {
						decimalPartString = prezzoUnitarioArray[1];
						decimalPart = new Integer(decimalPartString);
					}
					if (articolo.getNumDecimaliDetermPrezzo() == 0 && decimalPart > 0) {
						erroriRiga.add(this.getText("Errors.noDecimalsNeeded", new String[] {this.getTextFromDB("prezzoUnitario")}));
					} else if (articolo.getNumDecimaliDetermPrezzo() > 0 && decimalPartString.length() > articolo.getNumDecimaliDetermPrezzo()) {
						erroriRiga.add(this.getText("Errors.tooManyDecimals", new String[] {this.getTextFromDB("prezzoUnitario"), articolo.getNumDecimaliDetermPrezzo() + ""}));
					}
				}

				//Decimali prezzo
				colIndex++;

				//Num unita' su cui e' espresso il prezzo
				Number quantitaUMPrezzoNumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, colIndex++, this.getTextFromDB("quantitaUMPrezzo"), 5, erroriRiga);
				if (CataloghiConstants.TIPO_PREZZO_UNITA_DI_MISURA.equals(articolo.getPrezzoUnitarioPer())) {
					if (quantitaUMPrezzoNumber == null) {
						erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("quantitaUMPrezzo")}));
					} else if (quantitaUMPrezzoNumber.doubleValue() <= 0) {
						erroriRiga.add(this.getText("Errors.sheet.fieldGreatThan0", new String[] {this.getTextFromDB("quantitaUMPrezzo")}));
					} else if (quantitaUMPrezzoNumber != null) {
						quantitaUMPrezzo = quantitaUMPrezzoNumber.doubleValue();
					}
				}

				//Unita' di misura per l'acquisto
				colIndex++;

				//Aliquota IVA
				Number aliquotaIVANumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, colIndex++, this.getTextFromDB("aliquotaIVA"), 0, erroriRiga);
				if (aliquotaIVANumber == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("aliquotaIVA")}));
				} else {
					aliquotaIVA = aliquotaIVANumber.intValue();
				}

				//Lotto minimo per unita' di misura
				Number quantitaUMAcquistoNumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, colIndex++, this.getTextFromDB("quantitaUMAcquisto"), 1, erroriRiga);
				if (!CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA.equals(articolo.getPrezzoUnitarioPer())
					&& !CataloghiConstants.TIPO_PREZZO_CONFEZIONE.equals(articolo.getPrezzoUnitarioPer())) {
					if (quantitaUMAcquistoNumber == null) {
						erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("quantitaUMAcquisto")}));
					} else {
						if (articolo.getQuantitaMinimaUnitaAcquisto() != null && quantitaUMAcquistoNumber.doubleValue() < articolo.getQuantitaMinimaUnitaAcquisto()) {
							erroriRiga.add(this.getText("Errors.mustBeAbove",
											new String[] {this.getTextFromDB("quantitaUMAcquisto"), UtilityNumeri.convertiDouble(articolo.getQuantitaMinimaUnitaAcquisto())}));
						}
						if (articolo.getQuantitaMassimaUnitaAcquisto() != null && quantitaUMAcquistoNumber.doubleValue() > articolo.getQuantitaMassimaUnitaAcquisto()) {
							erroriRiga.add(this.getText("Errors.mustBeUnder",
											new String[] {this.getTextFromDB("quantitaUMAcquisto"), UtilityNumeri.convertiDouble(articolo.getQuantitaMassimaUnitaAcquisto())}));
						}
						quantitaUMAcquisto = quantitaUMAcquistoNumber.doubleValue();
					}
				}

				//Limite minimo lotto minimo
				colIndex++;

				//Limite massimo lotto minimo
				colIndex++;

				//Tempo di consegna
				Number tempoConsegnaNumber = UtilityExcel.leggiNumero(
						sheet, rowIndex, colIndex++, this.getTextFromDB("tempoConsegna"), 0, erroriRiga);
				if (tempoConsegnaNumber == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("tempoConsegna")}));
				} else {
					if (articolo.getTempoMaxConsegna() > 0 && tempoConsegnaNumber.intValue() > articolo.getTempoMaxConsegna()) {
						erroriRiga.add(this.getText("Errors.mustBeUnder",
										new String[] {this.getTextFromDB("tempoConsegna"), UtilityNumeri.convertiIntero(articolo.getTempoMaxConsegna())}));
					}
					tempoConsegna = tempoConsegnaNumber.intValue();
				}

				//Tempo massimo di consegna
				colIndex++;

				//Unita' di misura tempo di consegna
				colIndex++;

				//Data scadenza offerta
				dataScadenzaOfferta = UtilityExcel.leggiData(sheet, rowIndex, colIndex++, this.getTextFromDB("dataScadenzaOfferta"), erroriRiga);
				if (dataScadenzaOfferta == null) {
					erroriRiga.add(this.getText("Errors.sheet.requiredFiled", new String[] {this.getTextFromDB("dataScadenzaOfferta")}));
				} else {
					//Controllo che la data scadenza sia >= oggi e <=data oggi+1 anno.
					Date now = new Date();
					Calendar tomorrow = new GregorianCalendar();
					tomorrow.add(Calendar.YEAR, 1);
					if (dataScadenzaOfferta.before(now) || dataScadenzaOfferta.after(tomorrow.getTime())) {
						erroriRiga.add(this.getText("Errors.invalidDataScadenza", new String[] {this.getTextFromDB("dataScadenzaOfferta")}));
					}
				}

				//testo se sono stati inseriti valori per questa riga, altrimenti la scarto
				if (StringUtils.isBlank(codiceProdottoFornitore)
					&& StringUtils.isBlank(marcaProdottoProduttore)
					&& StringUtils.isBlank(codiceProdottoProduttore)
					&& StringUtils.isBlank(nomeCommerciale)
					&& StringUtils.isBlank(descrizioneAggiuntiva)
					&& StringUtils.isBlank(dimensioni)
					&& prezzoUnitario == null
					&& quantitaUMPrezzo == null
					&& aliquotaIVA == null) {
					this.getResult().getSkippedRows().put(rowIndex, this.getText("Errors.sheet.prodottoNonCompilato"));
					continue;
				}
			}

			if (!erroriRiga.isEmpty()) {
				this.getResult().getErrorRows().put(rowIndex, erroriRiga);
			} else if (goOn) {
				prodottoType = new ProdottoType();
				prodottoType.setCodiceProdottoFornitore(codiceProdottoFornitore);
				prodottoType.setMarcaProdottoProduttore(marcaProdottoProduttore);
				prodottoType.setCodiceProdottoProduttore(codiceProdottoProduttore);
				prodottoType.setNomeCommerciale(nomeCommerciale);
				prodottoType.setDescrizioneAggiuntiva(descrizioneAggiuntiva);
				prodottoType.setDimensioni(dimensioni);
				prodottoType.setGaranzia(garanzia);
				prodottoType.setQuantitaUMPrezzo(
								!CataloghiConstants.TIPO_PREZZO_UNITA_DI_MISURA.equals(prodottoHelper.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer())
												? 1 : quantitaUMPrezzo);
				prodottoType.setPrezzoUnitario(prezzoUnitario);
				Double prezzoUnitarioPerMisuraAcq = prodottoType.getPrezzoUnitario() * prodottoType.getQuantitaUMPrezzo();
				BigDecimal prezzoUnitarioPerMisuraAcqRound = BigDecimal.valueOf(prezzoUnitarioPerMisuraAcq).setScale(
								prodottoHelper.getArticolo().getDettaglioArticolo().getNumDecimaliAcquisto(), BigDecimal.ROUND_HALF_UP);
				prodottoType.setPrezzoUnitarioPerAcquisto(prezzoUnitarioPerMisuraAcqRound.doubleValue());

				if (CataloghiConstants.TIPO_PREZZO_CONFEZIONE.equals(prodottoHelper.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer())
					|| CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA.equals(prodottoHelper.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer())) {
					prodottoType.setQuantitaUMAcquisto(prodottoHelper.getArticolo().getDettaglioArticolo().getQuantitaUnitaAcquisto());
				} else {
					prodottoType.setQuantitaUMAcquisto(quantitaUMAcquisto);
				}
				if (aliquotaIVA != null) {
					for (String aliquotaKey : this.getMaps().get(InterceptorEncodedData.LISTA_ALIQUOTE_IVA).keySet()) {
						String aliquota = this.getMaps().get(InterceptorEncodedData.LISTA_ALIQUOTE_IVA).get(aliquotaKey);
						if (aliquotaIVA.toString().equals(aliquota)) {
							prodottoType.setAliquotaIVA(aliquotaKey);
						}
					}
				}
				prodottoType.setTempoConsegna(tempoConsegna != null ? tempoConsegna : 0);
				prodottoType.setDataScadenzaOfferta(dataScadenzaOfferta);
				prodottoHelper.setDettaglioProdotto(prodottoType);
				prodottoHelper.setDataOperazione(new Date());
				if (prodottoHelper.getArticolo().getDettaglioArticolo().isProdottoDaVerificare()) {
					prodottoType.setStato(CataloghiConstants.STATO_PRODOTTO_IN_ATTESA);
				} else {
					prodottoType.setStato(CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO);
				}
				
				if (articolo.isObbligoImmagine()
					|| articolo.isObbligoCertificazioni()
					|| articolo.isObbligoSchedaTecnica()) {
					prodottoHelper.setStatoProdotto(CataloghiConstants.BOZZA);
					carrelloProdotti.aggiungiABozze(prodottoHelper);
				} else {
					prodottoHelper.setStatoProdotto(CataloghiConstants.PRODOTTO_INSERITO);
					carrelloProdotti.aggiungiAInseriti(prodottoHelper);
				}
				numeroRigheSuccesso++;
			}
		}

		this.getResult().setInputRows(numeroRigheProcessate);
		this.getResult().setSuccessRows(numeroRigheSuccesso);
		return prodotti;
	}

}
