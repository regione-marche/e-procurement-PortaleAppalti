package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioBandoOutType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioBandoType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IDPCM26042011Manager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IEsitiManager;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Action per la gestione della generazione della tabella di indicizzazione con
 * i dati di dettaglio di un bando/esito/avviso nel rispetto del D.P.C.M. del
 * 26/04/2011.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class TabellaIndicizzazioneDPCMAction extends BaseAction {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 5614236889512341640L;

	private IDPCM26042011Manager dpcmManager;
	private IBandiManager bandiManager;
	private IAvvisiManager avvisiManager;
	private IEsitiManager esitiManager;
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.GENERIC)
	private String tipo;

	private DettaglioBandoType[] lotti;

	private DocumentoAllegatoType[] documenti;

	@Validate(EParamValidation.TITOLO_GARA)
	private String titolo;
	
	private Date dataPubblicazione;
	
	private boolean isAmountVisible;

	/**
	 * @param dpcmManager
	 *            the dpcmManager to set
	 */
	public void setDpcmManager(IDPCM26042011Manager dpcmManager) {
		this.dpcmManager = dpcmManager;
	}

	/**
	 * @param bandiManager
	 *            the bandiManager to set
	 */
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	/**
	 * @param avvisiManager
	 *            the avvisiManager to set
	 */
	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}

	/**
	 * @param esitiManager
	 *            the esitiManager to set
	 */
	public void setEsitiManager(IEsitiManager esitiManager) {
		this.esitiManager = esitiManager;
	}
	
	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @return the codice
	 */
	public String getCodice() {
		return codice;
	}

	/**
	 * @param codice
	 *            the codice to set
	 */
	public void setCodice(String codice) {
		this.codice = codice;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the lotti
	 */
	public DettaglioBandoType[] getLotti() {
		return lotti;
	}

	/**
	 * @return the documenti
	 */
	public DocumentoAllegatoType[] getDocumenti() {
		return documenti;
	}

	/**
	 * @return the titolo
	 */
	public String getTitolo() {
		return titolo;
	}
	
	/**
	 * @return the dataPubblicazione
	 */
	public Date getDataPubblicazione() {
		return dataPubblicazione;
	}
	
	public boolean getIsAmountVisible() {
		return isAmountVisible;
	}

	public void setIsAmountVisible(boolean isAmountVisible) {
		this.isAmountVisible = isAmountVisible;
	}

	/**
	 * Estrae i dati di lotti richiesti dal DPCM per la generazione della
	 * tabella di indicizzazione.
	 */
	public String viewTabIndicizzazione() {
		String target = SUCCESS;
		try {
			String token = StringUtils
					.stripToNull((String) this.appParamManager
							.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN));
			DettaglioBandoOutType dati = this.dpcmManager.getDettaglioBando(
					token, this.codice, this.tipo);
			this.lotti = dati.getBando();
			this.documenti = dati.getDocumento();
			isAmountVisible = true;
			if ("Bando".equals(this.tipo)) {
				DettaglioGaraType dettBando = this.bandiManager
						.getDettaglioGara(this.codice);
				this.titolo = dettBando.getDatiGeneraliGara().getOggetto();
				this.dataPubblicazione = dettBando.getDatiGeneraliGara().getDataPubblicazione();
				isAmountVisible = dettBando.getDatiGeneraliGara().getNascondiImportoBaseGara() != 1;
			} else if ("Avviso".equals(this.tipo)) {
				DettaglioAvvisoType dettAvviso = this.avvisiManager
						.getDettaglioAvviso(this.codice);
				this.titolo = dettAvviso.getDatiGenerali().getOggetto();
				this.dataPubblicazione = dettAvviso.getDatiGenerali().getDataPubblicazione();
			} else if ("Esito".equals(this.tipo)) {
				DettaglioEsitoType dettEsito = this.esitiManager
						.getDettaglioEsito(this.codice);
				this.titolo = dettEsito.getDatiGenerali().getOggetto();
				this.dataPubblicazione = dettEsito.getDatiGenerali().getDataPubblicazione();
			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "viewTabIndicizzazione");
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
}
