package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.consulenti;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ConsulenteCollaboratoreType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.Allegato;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ZipUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeTrasparenzaManager;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione dei consulenti
 * e collaboratori.
 *
 * @author ...
 */
public class ConsulentiCollaboratoriFinderAction extends EncodedDataAction implements SessionAware, ModelDriven<ConsulentiCollaboratoriResultBean> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3421685615685739256L;

	private static final SimpleDateFormat YYYYMMDD_HHMMSS 	= new SimpleDateFormat("yyyyMMdd-HHmmss");
	private static final SimpleDateFormat DDMMYYYY 			= new SimpleDateFormat("dd/MM/yyyy");
	
	private Map<String, Object> session;	
	private ILeggeTrasparenzaManager leggeTrasparenzaManager;
	private ConfigInterface configManager;
	private IAppParamManager appParamManager;

	@Validate
	private ConsulentiCollaboratoriResultBean model = new ConsulentiCollaboratoriResultBean();
	private List<ConsulenteCollaboratoreType> lista;
	@Validate(EParamValidation.DIGIT)
	private String last;
	private int id;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceSoggetto;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String stazioneAppaltante;
	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;	

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}	

	public void setLeggeTrasparenzaManager(ILeggeTrasparenzaManager leggeTrasparenzaManager) {
		this.leggeTrasparenzaManager = leggeTrasparenzaManager;
	}
	
	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	@Override
	public ConsulentiCollaboratoriResultBean getModel() {
		return this.model;
	}

	public List<ConsulenteCollaboratoreType> getLista() {
		return lista;
	}
	
	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceSoggetto() {
		return codiceSoggetto;
	}

	public void setCodiceSoggetto(String codiceSoggetto) {
		this.codiceSoggetto = codiceSoggetto;
	}
	
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}

	/**
	 * Restituisce la lista di consulenti collaboratori in base ai filtri impostati.
	 *
	 * @return target to reach
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}	
		
		return this.getTarget();
	}
	
	/**
	 * Esegue una ricerca e restituisce la lista di consulenti collaboratori 
	 * in base ai filtri impostati. 
	 */
	public String find() {
		this.setTarget(SUCCESS);
		
		try {
			if ("1".equals(this.last)) {
				// se si richiede il rilancio dell'ultima estrazione effettuata,
				// allora si prendono dalla sessione i filtri applicati e si
				// caricano nel presente oggetto
				ConsulentiCollaboratoriResultBean finder = (ConsulentiCollaboratoriResultBean) this.session
						.get(PortGareSystemConstants.SESSION_ID_SEARCH_CONSULENTI_COLLABORATORI);
				this.model = finder;
			}

			// se è stata impostata una stazione appaltante nei parametri del portale
			// allora i dati vanno sempre filtrati per questa stazione appaltante...
			this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
			if(this.stazioneAppaltante != null) {
				this.model.setStazioneAppaltante(this.stazioneAppaltante);
			}
			
			// --- validazione campi di ricerca ---
			boolean paramOk = true;
						
			Date dtDataDa = null;
			if (StringUtils.isNotEmpty(this.model.getDataDa())) {
				try {
					dtDataDa = (Date) LocaleConvertUtils.convert(this.model.getDataDa(), java.sql.Date.class, "dd/MM/yyyy");
				} catch (ConversionException e) {
					ApsSystemUtils.logThrowable(e, this, "find");
					this.addActionErrorDateInvalid("LABEL_DATA_PROVVEDIMENTO", DA_DATA, this.model.getDataDa());
					this.model.setDataDa(null);
					paramOk = false;
				}
			}

			Date dtDataA = null;
			if (StringUtils.isNotEmpty(this.model.getDataA())) {
				try {
					dtDataA = (Date) LocaleConvertUtils.convert(this.model.getDataA(), java.sql.Date.class, "dd/MM/yyyy");
				} catch (ConversionException e) {
					ApsSystemUtils.logThrowable(e, this, "find");
					this.addActionErrorDateInvalid("LABEL_DATA_PROVVEDIMENTO", A_DATA, this.model.getDataA());
					this.model.setDataA(null);
					paramOk = false;
				}
			}
			
		
			Double compensoDa = null;
			try {
				if(StringUtils.isNotEmpty(this.model.getCompensoPrevistoDa())) {
					compensoDa = Double.parseDouble(this.model.getCompensoPrevistoDa());
				}
			} catch (NumberFormatException e) {
				ApsSystemUtils.logThrowable(e, this, "find");
				this.addActionErrorDoubleInvalid("LABEL_COMPENSO_PREVISTO", A_PARTIRE_DA, this.model.getCompensoPrevistoDa());
				this.model.setCompensoPrevistoDa(null);
				paramOk = paramOk && false;
			}
			
			Double compensoA = null;
			try {
				if(StringUtils.isNotEmpty(this.model.getCompensoPrevistoA())) {
					compensoA = Double.parseDouble(this.model.getCompensoPrevistoA());
				}
			} catch (NumberFormatException e) {
				ApsSystemUtils.logThrowable(e, this, "find");
				this.addActionErrorDoubleInvalid("LABEL_COMPENSO_PREVISTO", FINO_A, this.model.getCompensoPrevistoA());
				this.model.setCompensoPrevistoA(null);
				paramOk = paramOk && false;
			}
			
			this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_CONSULENTI_COLLABORATORI,
							 this.model);
			
			// estrazione dell'elenco dei consulenti e collaboratori
			if(paramOk) {
				this.lista = this.leggeTrasparenzaManager.getConsulentiCollaboratori(
						null, 
						this.model.getStazioneAppaltante(),
						StringUtils.isNotEmpty(model.getSoggettoPercettore()) ? model.getSoggettoPercettore().toUpperCase() : model.getSoggettoPercettore(),
						dtDataDa, 
						dtDataA,
						StringUtils.isNotEmpty(model.getRagioneIncarico()) ? model.getRagioneIncarico().toUpperCase() : model.getRagioneIncarico(),
						compensoDa,
						compensoA);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "find");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "find");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}
	
	/**
	 * esegue il download di un archivio zip contenente i documenti relativi ad un consulente/collaboratore
	 */
	public String downloadDocumenti() {
		this.setTarget(SUCCESS);
		
		try {
			// recupera l'elenco degli allegati relativi al consulente/collaboratore... 
			List<FileType> documenti = this.leggeTrasparenzaManager
				.getDocumentiConsulentiCollaboratori(null, this.codice, this.codiceSoggetto);
			
			List<Allegato> files = new ArrayList<Allegato>();
			byte[] contenuto = null;
			for(int i = 0; i < documenti.size(); i++) {
				contenuto = documenti.get(i).getFile();
				if(contenuto != null) {
					String filename = documenti.get(i).getNome();
					
					// verifica se il nome del file esiste gia'
					boolean filenameUnivoco;
					int n = 0;
					do {
						filenameUnivoco = true;
						for(int j = 0; j < files.size(); j++) {
							if(filename.equalsIgnoreCase(files.get(j).getNome())) {
								n++;
								String ext = FilenameUtils.getExtension(documenti.get(i).getNome()); 
								filename = StringUtils.left(documenti.get(i).getNome(), 
															documenti.get(i).getNome().length() - ext.length() - 1) +
										   "(" + n + ")" + "." + ext;
								filenameUnivoco = false;
								break;
							}
						}
					} while (!filenameUnivoco);
					
					// aggiungi il file allo zip
					files.add( new Allegato(filename, contenuto) );
				}
			}

			// comprimi gli allegati e rendili disponibili per il download utente...
			byte[] zipFile = ZipUtilities.getZip(files);
			this.inputStream = new ByteArrayInputStream(zipFile);
			//this.filename = this.codice + "_" + this.codiceSoggetto + "_documents.zip";
			//this.filename = "documenti_consulente_collaboratore_" + YYYYMMDD_HHMMSS.format(new Date()) + ".zip";
			this.filename = YYYYMMDD_HHMMSS.format(new Date()) + ".zip";
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumenti");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} 
		
		return this.getTarget(); 
	}
	
	/**
	 * Esporta in formato CSV i dati estratti.
	 */
	public String export() {
		this.find();
		if (SUCCESS.equals(this.getTarget())) {
			// dopo aver estratto i dati si esegue l'export CSV
			//StringBuffer url = new StringBuffer();
			//url.append(this.configManager.getParam(SystemConstants.PAR_APPL_BASE_URL));
				
			String stazAppUnico = (String) appParamManager.getConfigurationValue(AppParamManager.DENOMINAZIONE_STAZIONE_APPALTANTE_UNICA);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream writer = new PrintStream(stream);
			
			writer.print(this.getI18nLabel("LABEL_SOGGETTO_PERCETTORE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_NUMERO_PROVVEDIMENTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_DATA_PROVVEDIMENTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_RAGIONE_INCARICO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_OGGETTO_PRESTAZIONE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_COMPENSO_PREVISTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_PARTE_VARIABILE_COMPENSO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_INIZIO_INCARICO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_FINE_INCARICO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_TIPO_PROCEDURA"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_NUM_PARTECIPANTI"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_STAZIONE_APPALTANTE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_FILE_ALLEGATI"));
			writer.println();

			
			for (ConsulenteCollaboratoreType elemento : this.lista) {
				if(elemento.getSoggettoPercettore() != null) {
					writer.print(StringUtilities.escapeCsv(elemento.getSoggettoPercettore()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getProtocollo() != null) {
					writer.print(StringUtilities.escapeCsv(elemento.getProtocollo()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if (elemento.getData() != null) {
					writer.print(DDMMYYYY.format(elemento.getData()));
				} 
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getRagioneIncarico() != null) {
					writer.print(StringUtilities.escapeCsv(elemento.getRagioneIncarico()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getOggettoPrestazione() != null) {
					writer.print(StringUtilities.escapeCsv(elemento.getOggettoPrestazione()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getCompensoPrevisto() != null) {
					writer.print(elemento.getCompensoPrevisto());
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getParteVariabile() != null) {
					writer.print(StringUtilities.escapeCsv(StringUtils.stripToEmpty(elemento.getParteVariabile())));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getDataA() != null) {
					writer.print(DDMMYYYY.format(elemento.getDataA()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getDataDa() != null) {
					writer.print(DDMMYYYY.format(elemento.getDataDa()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getTipoProcedura() != null) {
					writer.print(StringUtilities.escapeCsv(elemento.getTipoProcedura()));
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if(elemento.getNumeroPartecipanti() != null) {
					writer.print(elemento.getNumeroPartecipanti());
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				
				printStazioneAppaltante(writer, stazAppUnico, elemento);

				// documenti associati al consulente/collaboratore
				if(StringUtils.isNotEmpty(elemento.getUrl())) {
					writer.print(elemento.getUrl());
					writer.print(StringUtilities.CSV_DELIMITER);
				}
								
				writer.println();
			}
			this.inputStream = new ByteArrayInputStream(stream.toByteArray());
			this.setTarget("export");
		}
		return this.getTarget();
	}

	private void printStazioneAppaltante(PrintStream writer, String stazAppUnico,
			ConsulenteCollaboratoreType elemento) {
		if (StringUtils.isNotEmpty(stazAppUnico))
			writer.print(stazAppUnico);
		else if(elemento.getStazioneAppaltante() != null)
			writer.print(elemento.getStazioneAppaltante());
		writer.print(StringUtilities.CSV_DELIMITER);
	}

}