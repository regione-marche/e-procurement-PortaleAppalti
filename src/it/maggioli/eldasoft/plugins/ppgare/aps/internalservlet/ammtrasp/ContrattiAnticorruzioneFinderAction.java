package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.ProspettoContrattoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeTrasparenzaManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;

public class ContrattiAnticorruzioneFinderAction extends EncodedDataAction
	implements SessionAware, ModelDriven<ContrattiAnticorruzioneSearchBean> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4664296624788683568L;

	private ILeggeTrasparenzaManager leggeTrasparenzaManager;
	private ICustomConfigManager customConfigManager;

	/** contenitore dei dati di sessione. */
	private Map<String, Object> session;

	/** contenitore dei dati di ricerca. */
	private ContrattiAnticorruzioneSearchBean model = new ContrattiAnticorruzioneSearchBean();

	/** elenco delle procedure estratte */	
	private List<ProspettoContrattoType> listaContratti;

	private String last;
	
	private String stazioneAppaltante;

	private InputStream inputStream;

	public void setLeggeTrasparenzaManager(ILeggeTrasparenzaManager leggeTrasparenzaManager) {
		this.leggeTrasparenzaManager = leggeTrasparenzaManager;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public ContrattiAnticorruzioneSearchBean getModel() {
		return this.model;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public List<ProspettoContrattoType> getListaContratti() {
		return listaContratti;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Apre la form di ricerca degli esiti per la trasparenza
	 */
	public String openSearchContratti() {
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
	 * Restituisce la lista di esiti in base ai filtri impostati.
	 */
	public String search() {
		
		if ("1".equals(this.last)) {
			// se si richiede il rilancio dell'ultima estrazione effettuata,
			// allora si prendono dalla sessione i filtri applicati e si
			// caricano nel presente oggetto
			ContrattiAnticorruzioneSearchBean finder = (ContrattiAnticorruzioneSearchBean) this.session
					.get(AmmTraspConstants.SESSION_ID_SEARCH_AMMTRASP);
			this.model = finder;			
		}
		
		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}		

		// conversione delle date se valorizzate
		boolean dateOk = true;

		Date dtPubblicazioneEsitoDa = null;
		try {
			if(!StringUtils.isEmpty(this.model.getDataDa())){
				dtPubblicazioneEsitoDa = (Date) LocaleConvertUtils.convert(
						this.model.getDataDa(), java.sql.Date.class,
						"dd/MM/yyyy");
			}
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, "find");
			this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_ESITO", DA_DATA, this.model.getDataDa());
			this.model.setDataDa(null);
			dateOk = false;
		}


		Date dtPubblicazioneEsitoA = null;
		try {
			if(!StringUtils.isEmpty(this.model.getDataA())){
				dtPubblicazioneEsitoA = (Date) LocaleConvertUtils.convert(
						this.model.getDataA(), java.sql.Date.class,
						"dd/MM/yyyy");
			}
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, "find");
			this.addActionErrorDateInvalid("LABEL_DATA_PUBBLICAZIONE_ESITO", A_DATA, this.model.getDataA());
			this.model.setDataA(null);
			dateOk = false;
		}

		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			try {
				listaContratti = this.leggeTrasparenzaManager.getProspettoContratti(
						null, 
						model.getCig(), 
						model.getStazioneAppaltante(), 
						model.getOggetto(), 
						null,
						model.getPartecipante(),
						model.getAggiudicatario(),
						dtPubblicazioneEsitoDa, dtPubblicazioneEsitoA);

				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				this.session.put(AmmTraspConstants.SESSION_ID_SEARCH_AMMTRASP, this.model);			
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "search");
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
			boolean visImportoContratto = false;
			try {
				visImportoContratto = this.customConfigManager.isVisible("AMMTRASP-RIEPCONTRATTI", "IMPCONTRATTO");
			} catch (Exception e) {
				// server tracciare ?!?
			}
			
			// dopo aver estratto i dati si esegue l'export CSV
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream writer = new PrintStream(stream);

			writer.print(this.getI18nLabel("LABEL_CIG"));	
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_STAZIONE_APPALTANTE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_TITOLO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_SCELTA_CONTRAENTE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_AGGIUDICATARIO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_IMPORTO_AGGIUDICAZIONE"));
			if (visImportoContratto) {
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(this.getI18nLabel("LABEL_190_IMPORTO_CONTRATTO"));				
			}
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_TEMPI"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_IMPORTO_SOMME_LIQUIDATE"));
			writer.println();
			
			for (ProspettoContrattoType contratto : this.listaContratti) {
				writer.print("=\""+ contratto.getCig()+"\"");
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(contratto.getStrutturaProponenteCF() + " - " + contratto.getStrutturaProponenteDenominazione()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(contratto.getOggetto()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(contratto.getSceltaContraente());
				writer.print(StringUtilities.CSV_DELIMITER);
				if (contratto.getAggiudicatario() != null) {
					for (int i = 0; i < contratto.getAggiudicatario().length; i++) {
						if (contratto.getAggiudicatario(i) != null) {
							if (i>0) {
								writer.print(",");
							}
							writer.print(StringUtilities.escapeCsv(contratto.getAggiudicatario(i)));
						}
					}
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if (contratto.getImportoAggiudicazione() != null)
					writer.print(BigDecimal.valueOf(contratto.getImportoAggiudicazione()).toPlainString());
				if (visImportoContratto) {
					writer.print(StringUtilities.CSV_DELIMITER);
					if (contratto.getImportoContratto() != null)
						writer.print(BigDecimal.valueOf(contratto.getImportoContratto()).toPlainString());
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				if (contratto.getDataInizio() != null)
					writer.print(this.getI18nLabel("LABEL_190_INIZIO") + " "
							+ UtilityDate.convertiData(contratto.getDataInizio(),
									UtilityDate.FORMATO_GG_MM_AAAA));
				if (contratto.getDataUltimazione() != null)
					writer.print(", " + this.getI18nLabel("LABEL_190_ULTIMAZIONE") + " "  
							+ UtilityDate.convertiData(
									contratto.getDataUltimazione(),
									UtilityDate.FORMATO_GG_MM_AAAA));
				writer.print(StringUtilities.CSV_DELIMITER);
				if (contratto.getImportoSommeLiquidate() != null)
					writer.print(BigDecimal.valueOf(contratto.getImportoSommeLiquidate()).toPlainString());
				writer.println();
			}
			this.inputStream = new ByteArrayInputStream(stream.toByteArray());
			this.setTarget("export");
		}
		return this.getTarget();
	}
}
