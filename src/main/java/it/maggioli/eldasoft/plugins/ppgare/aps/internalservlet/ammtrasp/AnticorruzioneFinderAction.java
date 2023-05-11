package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AdempimentoAnticorruzioneType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.AppaltoAggiudicatoAnticorruzioneType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.PartecipanteType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeAnticorruzioneManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista esiti per
 * il prospetto procedure gare e contratti (anticorruzione Legge 190/2012).
 * 
 * @version 1.8.0
 * @author Stefano.Sabbadin
 */
public class AnticorruzioneFinderAction extends EncodedDataAction
		implements SessionAware, ModelDriven<AnticorruzioneSearchBean> {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 8888482353549998441L;
	
	private Map<String, Object> session;

	private ILeggeAnticorruzioneManager leggeAnticorruzioneManager;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/** contenitore dei dati di ricerca. */
	@Validate
	private AnticorruzioneSearchBean model = new AnticorruzioneSearchBean();

	@Validate(EParamValidation.GENERIC)
	private String last;

	List<AppaltoAggiudicatoAnticorruzioneType> listaAppalti = null;
	List<AdempimentoAnticorruzioneType> listaAdempimenti = null;

	private InputStream inputStream;
	
	private Date dataUltimoAggiornamento;

	@Validate(EParamValidation.CODICE_FISCALE)
	private String stazioneAppaltante;
	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @param leggeAnticorruzioneManager
	 *            the leggeAnticorruzioneManager to set
	 */
	public void setLeggeAnticorruzioneManager(
			ILeggeAnticorruzioneManager leggeAnticorruzioneManager) {
		this.leggeAnticorruzioneManager = leggeAnticorruzioneManager;
	}

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @return the last
	 */
	public String getLast() {
		return last;
	}

	/**
	 * @param last
	 *            the last to set
	 */
	public void setLast(String last) {
		this.last = last;
	}
	
	@Override
	public AnticorruzioneSearchBean getModel() {
		return this.model;
	}

	/**
	 * @return the listaAppalti
	 */
	public List<AppaltoAggiudicatoAnticorruzioneType> getListaAppalti() {
		return listaAppalti;
	}
	
	/**
	 * @return the listaAdempimenti
	 */
	public List<AdempimentoAnticorruzioneType> getListaAdempimenti() {
		return listaAdempimenti;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * @return the dataUltimoAggiornamento
	 */
	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}
	
	/**
	 * @return the stazioneAppaltante
	 */
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	/**
	 * calcola la MAX data di ultimo aggiormento nell'elenco adempimenti anticorruzione  
	 */
	private Date getMaxDataUltimoAggiornamento(List<AdempimentoAnticorruzioneType> lista) {
		Date dta = null;
		if(lista != null) {			
			for(int i = 0; i < lista.size(); i++) {
				if( dta == null ) {
					dta = lista.get(i).getDataAggiornamento();
				} 
				if( lista.get(i).getDataAggiornamento() != null && 
					lista.get(i).getDataAggiornamento().compareTo(dta) > 0 ) {
					dta = lista.get(i).getDataAggiornamento();
				}				
			}
		}		
		return dta;		
	}

	/**
	 * Apre la pagina di ricerca senza risultati.
	 */
	public String init() {
		return this.getTarget();
	}

	/**
	 * Apre la form di ricerca degli esiti per la trasparenza
	 */
	public String openSearch() {
		this.setTarget(SUCCESS);
		
		this.session
			.put(PortGareSystemConstants.SESSION_ID_SEARCH_ANTICORRUZIONE,
				 this.model);

		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		//this.stazioneAppaltante = this.getCodiceStazioneAppaltante();
		this.stazioneAppaltante = this.getCodiceFiscaleStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setProponente(this.stazioneAppaltante);
			this.stazioneAppaltante += " - " + this.getDescStazioneAppaltante();
		}

		/*--- LISTA LINK ADEMPIMENTI --- */		
		try {
			listaAdempimenti = this.leggeAnticorruzioneManager.getAdempimentiAnticorruzione(null, Integer.parseInt(this.model.getAnno()));			
			this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(this.listaAdempimenti);
			this.session.put("listaAdempimenti", listaAdempimenti);
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "openSearch");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di esiti in base ai filtri impostati.
	 */
	@SuppressWarnings("unchecked")
	public String search() {
		listaAdempimenti = (List<AdempimentoAnticorruzioneType>) this.session.get("listaAdempimenti");
		
		if ("1".equals(this.last)) {
			// se si richiede il rilancio dell'ultima estrazione effettuata,
			// allora si prendono dalla sessione i filtri applicati e si
			// caricano nel presente oggetto
			AnticorruzioneSearchBean finder = (AnticorruzioneSearchBean) this.session
					.get(PortGareSystemConstants.SESSION_ID_SEARCH_ANTICORRUZIONE);
			this.model = finder;
		}

		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		//this.stazioneAppaltante = this.getCodiceStazioneAppaltante();
		this.stazioneAppaltante = this.getCodiceFiscaleStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setProponente(this.stazioneAppaltante);
			this.stazioneAppaltante += " - " + this.getDescStazioneAppaltante();
		}
		
		boolean paramOk = true;
		
		int anno = 0;
		if (this.model.getAnno() == null) {
			this.addActionError("LABEL_190_ANNO_RIFERIMENTO", IS_REQUIRED, this.model.getAnno());
			paramOk = false;
		} else {
			try {
				anno = Integer.parseInt(this.model.getAnno());
			} catch (NumberFormatException e) {
				ApsSystemUtils.logThrowable(e, this, "find");
				this.addActionError("LABEL_190_ANNO_RIFERIMENTO", IS_INVALID, this.model.getAnno());
				this.model.setAnno(null);
				paramOk = false;
			}
		}

		if (SUCCESS.equals(this.getTarget()) && paramOk) {
			try {
				String token = StringUtils
						.stripToNull((String) this.appParamManager
								.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN));

				// estrazione dell'elenco dei bandi
				listaAppalti = this.leggeAnticorruzioneManager
						.getProspettoGareContrattiAnticorruzione(token, anno,
								this.model.getCig(),
								this.model.getProponente(),
								this.model.getOggetto(),
								this.model.getPartecipante(),
								this.model.getAggiudicatario());
				this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(this.listaAdempimenti);
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				this.session
						.put(PortGareSystemConstants.SESSION_ID_SEARCH_ANTICORRUZIONE,
								this.model);
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "find");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}

		return this.getTarget();
	}

	/**
	 * Esporta in formato CSV i dati estratti.
	 */
	public String export() {		
		this.search();
		if (SUCCESS.equals(this.getTarget())) {
			// dopo aver estratto i dati si esegue l'export CSV
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream writer = new PrintStream(stream);

			writer.print(this.getI18nLabel("LABEL_CIG"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_STRUTTURA_PROPONENTE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_OGGETTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_SCELTA_CONTRAENTE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_ELENCO_OPERATORI_INVITATI"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_AGGIUDICATARIO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_IMPORTO_AGGIUDICAZIONE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_TEMPI"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_IMPORTO_SOMME_LIQUIDATE"));
			writer.println();
			
			for (AppaltoAggiudicatoAnticorruzioneType appalto : this.listaAppalti) {
				//WE870 : il cig fittizio viene passato come ="0000000000" altrimenti
				//        excel lo presenta come 0
				writer.print("=\""+ appalto.getCig()+"\"");
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(appalto.getCodiceFiscaleProponente() + "-"
						+ appalto.getDenominazioneProponente()));
				writer.print(StringUtilities.CSV_DELIMITER);
				//WE1181
				writer.print(StringUtilities.escapeCsv(appalto.getOggetto()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(appalto.getSceltaContraente());
				writer.print(StringUtilities.CSV_DELIMITER);

				printDatiOE(writer, appalto.getPartecipante());
				
				writer.print(StringUtilities.CSV_DELIMITER);
				printDatiOE(writer, appalto.getAggiudicatario());
				writer.print(StringUtilities.CSV_DELIMITER);
				if (appalto.getImportoAggiudicazione() != null)
					writer.print(BigDecimal.valueOf(appalto.getImportoAggiudicazione()).toPlainString());
				writer.print(StringUtilities.CSV_DELIMITER);
				if (appalto.getDataInizio() != null)
					writer.print(this.getI18nLabel("LABEL_190_INIZIO") + " "
							+ UtilityDate.convertiData(appalto.getDataInizio(),
									UtilityDate.FORMATO_GG_MM_AAAA));
				if (appalto.getDataUltimazione() != null)
					writer.print(", " + this.getI18nLabel("LABEL_190_ULTIMAZIONE") + " "
							+ UtilityDate.convertiData(
									appalto.getDataUltimazione(),
									UtilityDate.FORMATO_GG_MM_AAAA));
				writer.print(StringUtilities.CSV_DELIMITER);
				if (appalto.getImportoSommeLiquidate() != null)
					writer.print(BigDecimal.valueOf(appalto.getImportoSommeLiquidate()).toPlainString());
				writer.println();

			}
			this.inputStream = new ByteArrayInputStream(stream.toByteArray());
			this.setTarget("export");
		}
		return this.getTarget();
	}

	/**
	 * Uniforma la modalità di stampa dei dati dell'operatore economico
	 * indipendentemente che sia impresa partecipante o aggiudicataria
	 * 
	 * @param writer
	 *            writer dello stream csv
	 * @param partecipanti
	 *            elenco degli operatori economici da inserire nel campo del csv
	 */
	private void printDatiOE(PrintStream writer, PartecipanteType[] partecipanti) {
		if (partecipanti != null) {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < partecipanti.length; i++) {
				PartecipanteType partecipante = partecipanti[i];
				if (i > 0)
					s.append(",");
				if (StringUtils.isNotEmpty(partecipante.getCodiceFiscale()))
					s.append(partecipante.getCodiceFiscale());
				else
					s.append(partecipante.getIdFiscaleEstero());
				//WE1181
				s.append("-"+partecipante.getRagioneSociale());
				
				if (StringUtils.isNotEmpty(partecipante.getRuolo()))
					s.append(" con ruolo " + partecipante.getRuolo());
			}
			writer.print(StringUtilities.escapeCsv(s.toString()));
		}
	}
}
