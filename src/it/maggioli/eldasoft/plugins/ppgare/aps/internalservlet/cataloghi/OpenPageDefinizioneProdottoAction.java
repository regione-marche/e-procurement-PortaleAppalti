package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardArticoloHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;

/**
 * Action di gestione dell'apertura della pagina del dettaglio prodotto del
 * wizard d'inserimento prodotto
 *
 * @author Marco.Perazzetta
 */
public class OpenPageDefinizioneProdottoAction extends AbstractOpenPageAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4634220130132894824L;

	private Map<String, Object> session;

	private Long prodottiCaricatiAOE;

	private Double migliorPrezzoAOE;

	private ICataloghiManager cataloghiManager;
	private IDocumentiDigitaliManager documentiDigitaliManager;

	private String marcaProdottoProduttore;
	private String codiceProdottoProduttore;
	private String nomeCommerciale;
	private String codiceProdottoFornitore;
	private String descrizioneAggiuntiva;
	private String dimensioni;
	private String aliquotaIVA;
	private String prezzoUnitario;
	private String garanzia;
	private String tempoConsegna;
	private String dataScadenzaOfferta;
	private String quantitaUMPrezzo;
	private String quantitaUMAcquisto;

	private Long prodottoId;
	private Long articoloId;
	private Boolean aggiornamento;
	private Boolean inCarrello;
	private String catalogo;
	private String statoProdotto;
	private String wizardSourcePage;

	/**
	 * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
	 */
	private String multipartSaveDir;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	public Long getProdottiCaricatiAOE() {
		return prodottiCaricatiAOE;
	}

	public void setProdottiCaricatiAOE(Long prodottiCaricatiAOE) {
		this.prodottiCaricatiAOE = prodottiCaricatiAOE;
	}

	public Double getMigliorPrezzoAOE() {
		return migliorPrezzoAOE;
	}

	public void setMigliorPrezzoAOE(Double migliorPrezzoAOE) {
		this.migliorPrezzoAOE = migliorPrezzoAOE;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setDocumentiDigitaliManager(IDocumentiDigitaliManager documentiDigitaliManager) {
		this.documentiDigitaliManager = documentiDigitaliManager;
	}

	public String getMarcaProdottoProduttore() {
		return marcaProdottoProduttore;
	}

	public void setMarcaProdottoProduttore(String marcaProdottoProduttore) {
		this.marcaProdottoProduttore = marcaProdottoProduttore;
	}

	public String getCodiceProdottoProduttore() {
		return codiceProdottoProduttore;
	}

	public void setCodiceProdottoProduttore(String codiceProdottoProduttore) {
		this.codiceProdottoProduttore = codiceProdottoProduttore;
	}

	public String getNomeCommerciale() {
		return nomeCommerciale;
	}

	public void setNomeCommerciale(String nomeCommerciale) {
		this.nomeCommerciale = nomeCommerciale;
	}

	public String getCodiceProdottoFornitore() {
		return codiceProdottoFornitore;
	}

	public void setCodiceProdottoFornitore(String codiceProdottoFornitore) {
		this.codiceProdottoFornitore = codiceProdottoFornitore;
	}

	public String getDescrizioneAggiuntiva() {
		return descrizioneAggiuntiva;
	}

	public void setDescrizioneAggiuntiva(String descrizioneAggiuntiva) {
		this.descrizioneAggiuntiva = descrizioneAggiuntiva;
	}

	public String getDimensioni() {
		return dimensioni;
	}

	public void setDimensioni(String dimensioni) {
		this.dimensioni = dimensioni;
	}

	public String getAliquotaIVA() {
		return aliquotaIVA;
	}

	public void setAliquotaIVA(String aliquotaIVA) {
		this.aliquotaIVA = aliquotaIVA;
	}

	public String getGaranzia() {
		return garanzia;
	}

	public void setGaranzia(String garanzia) {
		this.garanzia = garanzia;
	}

	public String getTempoConsegna() {
		return tempoConsegna;
	}

	public void setTempoConsegna(String tempoConsegna) {
		this.tempoConsegna = tempoConsegna;
	}

	public String getDataScadenzaOfferta() {
		return dataScadenzaOfferta;
	}

	public void setDataScadenzaOfferta(String dataScadenzaOfferta) {
		this.dataScadenzaOfferta = dataScadenzaOfferta;
	}

	public String getQuantitaUMPrezzo() {
		return quantitaUMPrezzo;
	}

	public void setQuantitaUMPrezzo(String quantitaUMPrezzo) {
		this.quantitaUMPrezzo = quantitaUMPrezzo;
	}

	public String getQuantitaUMAcquisto() {
		return quantitaUMAcquisto;
	}

	public void setQuantitaUMAcquisto(String quantitaUMAcquisto) {
		this.quantitaUMAcquisto = quantitaUMAcquisto;
	}

	public String getPrezzoUnitario() {
		return prezzoUnitario;
	}

	public void setPrezzoUnitario(String prezzoUnitario) {
		this.prezzoUnitario = prezzoUnitario;
	}

	public Long getProdottoId() {
		return prodottoId;
	}

	public void setProdottoId(Long prodottoId) {
		this.prodottoId = prodottoId;
	}

	public Boolean isAggiornamento() {
		return aggiornamento;
	}

	public void setAggiornamento(Boolean aggiornamento) {
		this.aggiornamento = aggiornamento;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public Long getArticoloId() {
		return articoloId;
	}

	public void setArticoloId(Long articoloId) {
		this.articoloId = articoloId;
	}

	public Boolean isInCarrello() {
		return inCarrello;
	}

	public void setInCarrello(Boolean inCarrello) {
		this.inCarrello = inCarrello;
	}

	public String getStatoProdotto() {
		return statoProdotto;
	}

	public void setStatoProdotto(String statoProdotto) {
		this.statoProdotto = statoProdotto;
	}

	public String getWizardSourcePage() {
		return wizardSourcePage;
	}

	public void setWizardSourcePage(String wizardSourcePage) {
		this.wizardSourcePage = wizardSourcePage;
	}

	
	public Integer getDEFAULT_NUM_MESI_GARANZIA() {
		return CataloghiConstants.DEFAULT_NUM_MESI_GARANZIA;
	}

	public Integer getTIPO_PRODOTTO_BENE() {
		return CataloghiConstants.TIPO_PRODOTTO_BENE;
	}

	public String getTIPO_PREZZO_UNITA_DI_MISURA() {
		return CataloghiConstants.TIPO_PREZZO_UNITA_DI_MISURA;
	}

	public String getTIPO_PREZZO_CONFEZIONE() {
		return CataloghiConstants.TIPO_PREZZO_CONFEZIONE;
	}

	public String getTIPO_PREZZO_PRODOTTO_SERVIZIO() {
		return CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO;
	}

	public String getTIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UM() {
		return CataloghiConstants.TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UNITA_DI_MISURA;
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DEFINIZIONE_PRODOTTO);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.catalogo, 
						this.cataloghiManager);
				WizardProdottoHelper oldProdottoHelper = WizardProdottoHelper.getInstance(
						this.session, 
						this.cataloghiManager, 
						carrelloProdotti,
						this.getCurrentUser().getUsername(), 
						carrelloProdotti.getCurrentCatalogo(), 
						this.statoProdotto, 
						this.inCarrello, 
						this.aggiornamento,
						this.prodottoId, 
						this.articoloId, 
						this.wizardSourcePage);
				String tempDir = StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir).getAbsolutePath();
				WizardProdottoHelper prodottoHelper = oldProdottoHelper.clone(
						this.documentiDigitaliManager, 
						tempDir, 
						true, 
						carrelloProdotti,
						this.getCurrentUser().getUsername());
				if (this.prodottoId != null 
					&& carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo()).prodottoGiaModificato(this.prodottoId)) {
					this.addActionError(this.getText("Errors.prodottoAlreadyModified"));
					this.setTarget(INPUT);
				}
				if (prodottoHelper.getDettaglioProdotto() != null) {
					fillFieldsProduct(prodottoHelper.getDettaglioProdotto(), oldProdottoHelper.getArticolo());
				}
				// inizializzo la data di scadenza
				if (this.dataScadenzaOfferta == null) {
					Calendar calendar = new GregorianCalendar();
					calendar.add(Calendar.YEAR, 1);
					this.dataScadenzaOfferta = CalendarValidator.getInstance().format(calendar.getTime(), "dd/MM/yyyy");
				}
				this.setProdottiCaricatiAOE(this.cataloghiManager.getNumProdottiAltriOEInArticolo(
						prodottoHelper.getArticolo().getIdArticolo(),
						this.getCurrentUser().getUsername()));
				Double migliorPrezzo = this.cataloghiManager.getPrezzoMiglioreArticolo(prodottoHelper.getArticolo().getIdArticolo());
				this.setMigliorPrezzoAOE(migliorPrezzo != null ? migliorPrezzo : 0);
				session.put(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO, prodottoHelper);
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (CloneNotSupportedException ex) {
				ApsSystemUtils.logThrowable(ex, this, "openPage");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String openPageAfterError() {

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_PAGINA_DEFINIZIONE_PRODOTTO);
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// inizializzo la data di scadenza
				WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				
				if (this.dataScadenzaOfferta == null) {
					Calendar calendar = new GregorianCalendar();
					calendar.add(Calendar.YEAR, 1);
					this.dataScadenzaOfferta = CalendarValidator.getInstance().format(calendar.getTime(), "dd/MM/yyyy");
				}
				this.setProdottiCaricatiAOE(this.cataloghiManager.getNumProdottiAltriOEInArticolo(
						prodottoHelper.getArticolo().getIdArticolo(),
						this.getCurrentUser().getUsername()));
				Double migliorPrezzo = this.cataloghiManager.getPrezzoMiglioreArticolo(prodottoHelper.getArticolo().getIdArticolo());
				this.setMigliorPrezzoAOE(migliorPrezzo != null ? migliorPrezzo : 0);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	private void fillFieldsProduct(ProdottoType prodotto, WizardArticoloHelper articolo) {
		this.marcaProdottoProduttore = prodotto.getMarcaProdottoProduttore();
		this.codiceProdottoProduttore = prodotto.getCodiceProdottoProduttore();
		this.nomeCommerciale = prodotto.getNomeCommerciale();
		this.codiceProdottoFornitore = prodotto.getCodiceProdottoFornitore();
		this.descrizioneAggiuntiva = prodotto.getDescrizioneAggiuntiva();
		this.dimensioni = prodotto.getDimensioni();
		this.aliquotaIVA = prodotto.getAliquotaIVA();
		this.garanzia = prodotto.getGaranzia() != null ? prodotto.getGaranzia() + "" : null;
		this.tempoConsegna = prodotto.getTempoConsegna() + "";
		this.dataScadenzaOfferta = CalendarValidator.getInstance().format(prodotto.getDataScadenzaOfferta(), "dd/MM/yyyy");
		this.quantitaUMPrezzo = prodotto.getQuantitaUMPrezzo() + "";
		this.quantitaUMAcquisto = prodotto.getQuantitaUMAcquisto() + "";
		//this.prezzoUnitario = prodotto.getPrezzoUnitario() + "";
		this.prezzoUnitario = BigDecimal.valueOf(new Double(prodotto.getPrezzoUnitario())).setScale(articolo.getDettaglioArticolo().getNumDecimaliDetermPrezzo(), BigDecimal.ROUND_HALF_UP).toPlainString();  
	}
	
}
