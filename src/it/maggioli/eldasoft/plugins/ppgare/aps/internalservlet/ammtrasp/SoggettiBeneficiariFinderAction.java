package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.EsitoProspettoBeneficiariType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeTrasparenzaManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista esiti per
 * la trasparenza, amministrazione aperta.
 *
 * @version 1.7.1
 * @author Stefano.Sabbadin
 */
public class SoggettiBeneficiariFinderAction extends EncodedDataAction
				implements SessionAware, ModelDriven<SoggettiBeneficiariSearchBean> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 150557410489036667L;

	private Map<String, Object> session;

	private ILeggeTrasparenzaManager leggeTrasparenzaManager;

	/**
	 * Riferimento al manager per la gestione dei parametri applicativo.
	 */
	private IAppParamManager appParamManager;

	private CustomConfigManager customConfigManager;

	/**
	 * contenitore dei dati di ricerca.
	 */
	private SoggettiBeneficiariSearchBean model = new SoggettiBeneficiariSearchBean();

	private String last;

	List<EsitoProspettoBeneficiariType> listaEsiti = null;

	private Integer showCigColumn;

	private InputStream inputStream;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @param leggeTrasparenzaManager the leggeTrasparenzaManager to set
	 */
	public void setLeggeTrasparenzaManager(
					ILeggeTrasparenzaManager leggeTrasparenzaManager) {
		this.leggeTrasparenzaManager = leggeTrasparenzaManager;
	}

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @param customConfigManager the customConfigManager to set
	 */
	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	@Override
	public SoggettiBeneficiariSearchBean getModel() {
		return this.model;
	}

	/**
	 * @return the last
	 */
	public String getLast() {
		return last;
	}

	/**
	 * @param last the last to set
	 */
	public void setLast(String last) {
		this.last = last;
	}

	/**
	 * @return the showCigColumn
	 */
	public Integer getShowCigColumn() {
		return showCigColumn;
	}

	/**
	 * @param showCigColumn the showCigColumn to set
	 */
	public void setShowCigColumn(Integer showCigColumn) {
		this.showCigColumn = showCigColumn;
	}

	/**
	 * @return the esiti
	 */
	public List<EsitoProspettoBeneficiariType> getListaEsiti() {
		return listaEsiti;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Apre la pagina di ricerca senza risultati.
	 *
	 * @return
	 */
	public String init() {
		return this.getTarget();
	}

	/**
	 * Apre la form di ricerca degli esiti per la trasparenza
	 *
	 * @return
	 */
	public String openSearch() {
		this.setTarget(SUCCESS);
		return this.getTarget();
	}

	/**
	 * Restituisce la lista di esiti in base ai filtri impostati
	 */
	public String search() {
		
		//Imposto visualizzazione colonna CIG
		try {
			showCigColumn = this.customConfigManager.isVisible("AMMTRASP-SOGGETTIBENEFICIARI", "CIG") ? 1 : 0;
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "find");
			this.addActionError(this.getText("Errors.noCIGShowConfig"));
			showCigColumn = 0;
		}

		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}

		if ("1".equals(this.last)) {

			// se si richiede il rilancio dell'ultima estrazione effettuata,
			// allora si prendono dalla sessione i filtri applicati e si
			// caricano nel presente oggetto
			SoggettiBeneficiariSearchBean finder = (SoggettiBeneficiariSearchBean) this.session
							.get(PortGareSystemConstants.SESSION_ID_SEARCH_AMM_APERTA);
			this.model = finder;
		}

		// conversione delle date se valorizzate
		boolean dateOk = true;

		Date dtAffidamentoDa = null;
		try {
			dtAffidamentoDa = (Date) LocaleConvertUtils.convert(
							this.model.getDataAffidamentoDa(), java.sql.Date.class,
							"dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, "find");
			this.addActionErrorDateInvalid("LABEL_190_DATA_AFFIDAMENTO", DA_DATA, this.model.getDataAffidamentoDa());
			this.model.setDataAffidamentoDa(null);
			dateOk = false;
		}

		Date dtAffidamentoA = null;
		try {
			dtAffidamentoA = (Date) LocaleConvertUtils.convert(
							this.model.getDataAffidamentoA(), java.sql.Date.class,
							"dd/MM/yyyy");
		} catch (ConversionException e) {
			ApsSystemUtils.logThrowable(e, this, "find");
			this.addActionErrorDateInvalid("LABEL_190_DATA_AFFIDAMENTO", A_DATA, this.model.getDataAffidamentoA());
			this.model.setDataAffidamentoA(null);
			dateOk = false;
		}

		if (SUCCESS.equals(this.getTarget()) && dateOk) {
			try {
				String token = StringUtils.stripToNull((String) this.appParamManager
						.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN));

				// estrazione dell'elenco dei bandi
				listaEsiti = this.leggeTrasparenzaManager.getProspettoBeneficiariArt18DLgs83_2012(
						token,
						dtAffidamentoDa, 
						dtAffidamentoA);
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_AMM_APERTA,
								 this.model);
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE,
								 "searchAmmAperta");
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, 
								 true);
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "find");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}

		return this.getTarget();
	}

	public String export() {
		
		this.search();
		
		if (SUCCESS.equals(this.getTarget())) {
			// dopo aver estratto i dati si esegue l'export CSV
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream writer = new PrintStream(stream);

			writer.print(this.getI18nLabel("LABEL_190_DATA_AFFIDAMENTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			if (showCigColumn == 1) {
				writer.print(this.getI18nLabel("LABEL_CIG"));
				writer.print(StringUtilities.CSV_DELIMITER);
			}
			writer.print(this.getI18nLabel("LABEL_190_OGGETTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_SOGGETTO_BENEFICIARIO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_CODICE_FISCALE_ABBR") + " / " + 
					     this.getI18nLabel("LABEL_PARTITA_IVA_ABBR") + " " + 
					     this.getI18nLabel("LABEL_190_SOGG_BENEFICIARIO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_IMPORTO_CORRISPOSTO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_NORMA_BASE_ATTRIBUZIONE"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_UFFICIO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_RESPONSABILE_PROCEDIMENTO_AMM"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print(this.getI18nLabel("LABEL_190_MODALITA_INDIVIDUAZIONE_BENEFICIARIO"));
			writer.print(StringUtilities.CSV_DELIMITER);
			writer.print("URL");
			writer.println();

			for (EsitoProspettoBeneficiariType esito : this.listaEsiti) {
				writer.print(UtilityDate.convertiData(
								esito.getDataAffidamento(),
								UtilityDate.FORMATO_GG_MM_AAAA));
				writer.print(StringUtilities.CSV_DELIMITER);
				if (showCigColumn == 1) {
					writer.print(StringUtilities.escapeCsv(StringUtils.stripToEmpty(esito.getCig())));
					writer.print(StringUtilities.CSV_DELIMITER);
				}
				writer.print(StringUtilities.escapeCsv(StringUtils.stripToEmpty(esito.getOggetto())));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(esito.getBeneficiario()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtils.stripToEmpty(esito.getDatiFiscali()));
				writer.print(StringUtilities.CSV_DELIMITER);
				if (esito.getImporto() != null) {
					writer.print(BigDecimal.valueOf(esito.getImporto()).toPlainString());
				}
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(StringUtils.stripToEmpty(esito.getNorma())));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(esito.getUfficio()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(esito.getRup()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtilities.escapeCsv(esito.getModalita()));
				writer.print(StringUtilities.CSV_DELIMITER);
				writer.print(StringUtils.stripToEmpty(esito.getUrl()));
				writer.println();

			}
			this.inputStream = new ByteArrayInputStream(stream.toByteArray());
			this.setTarget("export");
		}
		return this.getTarget();
	}
}
