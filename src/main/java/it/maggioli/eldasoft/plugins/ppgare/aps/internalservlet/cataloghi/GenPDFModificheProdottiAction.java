package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ProdottoType;
import it.eldasoft.sil.portgare.datatypes.ReportGestioneProdottiDocument;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlObject;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consente la generazione del PDF per la domanda di iscrizione.
 *
 * @author stefano.sabbadin
 * @since 1.8.2
 */
public class GenPDFModificheProdottiAction extends GenPDFAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6726785870328864846L;

	private IBandiManager bandiManager;

	@Validate(EParamValidation.GENERIC)
	private String firmatarioSelezionato;
	@Validate(EParamValidation.CODICE)
	private String catalogo;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	@Override
	public String getUrlErrori() {
		return this.urlPage + 
				"?actionPath=" + "/ExtStr2/do/FrontEnd/Cataloghi/openPageInviaModificheProdotti.action" +
				"&currentFrame=" + this.currentFrame;
	}

	public String getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(String firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	/**
	 * costruttore 
	 */
	public GenPDFModificheProdottiAction() {
		super("",	// { DatiVariazioniPrezzoScadenza(.jasper) | DatiModificheProdotti(.jasper) }
		      "ModificheProdottiHeader",
		      "ModificheProdottiFooter",
			  "/ReportGestioneProdotti");
	}

	/**
	 * ... 
	 */
	@Override
	protected boolean reportInit() {
		boolean continua = true;

		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
			this.setTarget(ERROR);
			continua = false;
		} else if (this.firmatarioSelezionato == null) {
			session.put(ERROR_DOWNLOAD, this.getText("Errors.firmatarioNotSet"));
			this.setTarget(INPUT);
			continua = false;
		} else {
			try {
				ServletContext ctx = this.getRequest().getSession().getServletContext();
					
				if (ctx.getResource(PortGareSystemConstants.GARE_JASPER_FOLDER
						+ "/DatiModificheProdotti.jasper") == null) {
					session.put(ERROR_DOWNLOAD, this.getText("Errors.pdf.templateModificaProdotti.notFound"));
					this.setTarget(INPUT);
					continua = false;
				} else if (ctx.getResource(PortGareSystemConstants.GARE_JASPER__SUBREPORT_FOLDER
						+ "/ModificheProdottiHeader.jasper") == null) {
					session.put(ERROR_DOWNLOAD, this.getText("Errors.pdf.subTemplateModificaProdotti.notFound"));
					this.setTarget(INPUT);
					continua = false;
				} else if(ctx.getResource(PortGareSystemConstants.GARE_JASPER__SUBREPORT_FOLDER
						+ "/ModificheProdottiFooter.jasper") == null) {
					session.put(ERROR_DOWNLOAD, this.getText("Errors.pdf.subTemplateModificaProdotti.notFound"));
					this.setTarget(INPUT);
					continua = false;
				} else {
					// nessun errore, si può generare il report...
		
					CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
						.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
					ProdottiCatalogoSessione prodottiModificati = carrelloProdotti.getListaProdottiPerCatalogo()
						.get(carrelloProdotti.getCurrentCatalogo());

					this.xmlRootNode = "/ReportGestioneProdotti";
					
					if (prodottiModificati.isVariazioneOfferta()) {
						this.reportName = "DatiVariazioniPrezzoScadenza";
						this.filename = "RichiestaAggiornamentoOffertaProdotti";						
					} else {
						this.reportName = "DatiModificheProdotti";
						this.filename = "RiepilogoModificheProdotti";	
					}
				}
				
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "createPdf");
				session.put(ERROR_DOWNLOAD, 
							"Si e' verificato un errore inaspettato durante l'elaborazione dell'operazione: "
							+ e.getMessage());
				this.setTarget(INPUT);
				continua = false;
			}
		}
		
		return continua;
	}

	/**
	 * ... 
	 */
	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
		ProdottiCatalogoSessione prodottiModificati = carrelloProdotti.getListaProdottiPerCatalogo()
			.get(carrelloProdotti.getCurrentCatalogo());

		params.put("CATALOGO", prodottiModificati.getCatalogo().getDatiGeneraliBandoIscrizione().getOggetto());
	}

	/**
	 * ... 
	 */
	@Override
	protected void reportCompleted() {		
		CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
			.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);

		if (SUCCESS.equals(this.getTarget()) && carrelloProdotti != null) {
			//carrelloProdotti.getPdfGenerati().add(pdf);
		}				
	}

	/**
	 * ... 
	 */
	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws Exception {
		XmlObject document = null; 
	
		try {
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			
			ProdottiCatalogoSessione prodottiModificati = carrelloProdotti.getListaProdottiPerCatalogo()
				.get(carrelloProdotti.getCurrentCatalogo());
			// Aggiorno i dati dell'impresa aggiungendo il firmatario del documento
			// ricavo il firmatario selezionato e aggiorno l'oggetto in sessione 
			// relativo all'impresa
			WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
			FirmatarioBean firmatario = new FirmatarioBean(this.firmatarioSelezionato);
			
			String tipoOperazione = (prodottiModificati.isVariazioneOfferta()
					? CataloghiConstants.COMUNICAZIONE_INVIO_VARIAZIONE_OFFERTA
					: CataloghiConstants.COMUNICAZIONE_INVIO_PRODOTTI);

			
			// FASE 1: si genera il documento XML puro
			ReportGestioneProdottiDocument doc = ReportGestioneProdottiDocument.Factory.newInstance();
			prodottiModificati.getXmlDocument(doc, datiImpresaHelper, tipoOperazione);
			
			addFirmatario(firmatario, doc.getReportGestioneProdotti().addNewFirmatario(), datiImpresaHelper);
			
			// FASE 2: si sostituiscono tutti i dati tabellati con le
			// corrispondenti descrizioni e si eliminano i dati non previsti in configurazione
			if (StringUtils.isNotBlank(datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale())) {
				doc.getReportGestioneProdotti().getDatiImpresa().getImpresa().getSedeLegale().setProvincia(
						getMaps().get(InterceptorEncodedData.LISTA_PROVINCE)
								.get(datiImpresaHelper.getDatiPrincipaliImpresa().getProvinciaSedeLegale()));
			}
			if (StringUtils.isNotBlank(doc.getReportGestioneProdotti().getFirmatario().getProvinciaNascita())) {
				doc.getReportGestioneProdotti().getFirmatario().setProvinciaNascita(
						getMaps().get(InterceptorEncodedData.LISTA_PROVINCE)
								.get(doc.getReportGestioneProdotti().getFirmatario().getProvinciaNascita()));
			}
			if (!datiImpresaHelper.isLiberoProfessionista()) {
				if (firmatario.getLista().equals(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI)) {
					doc.getReportGestioneProdotti().getFirmatario().setQualifica(
							getMaps().get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO).get(
									CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE + 
									CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
				} else if (firmatario.getLista().equals(CataloghiConstants.LISTA_DIRETTORI_TECNICI)) {
					doc.getReportGestioneProdotti().getFirmatario().setQualifica(
							getMaps().get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO).get(
									CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO + 
									CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
				} else {
					doc.getReportGestioneProdotti().getFirmatario().setQualifica(
							getMaps().get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA).get(
									CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA + 
									CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI + 
									doc.getReportGestioneProdotti().getFirmatario().getQualifica()));
				}
			}
			for (ProdottoType prodottoArray : doc.getReportGestioneProdotti().getInserimenti().getProdottoArray()) {
				updateInfoProdottoType(prodottoArray, getMaps());
			}

			for (ProdottoType prodottoArray : doc.getReportGestioneProdotti().getAggiornamenti().getProdottoArray()) {
				updateInfoProdottoType(prodottoArray, getMaps());
			}

			for (ProdottoType prodottoArray : doc.getReportGestioneProdotti().getArchiviazioni().getProdottoArray()) {
				updateInfoProdottoType(prodottoArray, getMaps());
			}

			document = doc;
	
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "generaXmlConDatiTipizzati");
			session.put(ERROR_DOWNLOAD, 
						"Si e' verificato un errore inaspettato durante l'elaborazione dell'operazione: "
						+ e.getMessage());
			this.setTarget(INPUT);
		}
		
		return document;
	}

	/**
	 * generazione XML 
	 */
	private void addFirmatario(
			FirmatarioBean firmatario, 
			FirmatarioType referente, 
			WizardDatiImpresaHelper datiImpresaHelper) 
	{
		if (datiImpresaHelper.isLiberoProfessionista()) {
			//il libero professionista ha come nominativo la ragione sociale che 
			//metto direttamente dentro al campo nome
			referente.setNome(StringUtils.capitalize(datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale()));
			referente.setCognome(null);
			referente.setComuneNascita(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getComuneNascita());
			referente.setDataNascita(CalendarValidator.getInstance()
							.validate(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getDataNascita(), "dd/MM/yyyy"));
			referente.setProvinciaNascita(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getProvinciaNascita());
			referente.setQualifica("Libero Professionista");
			referente.setCodiceFiscaleFirmatario(datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale());
		} else {
			//recupero il soggetto firmatario dalla lista referenti dell'azienda
			ISoggettoImpresa soggetto;
			if (firmatario.getLista().equals(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI)) {
				soggetto = datiImpresaHelper.getLegaliRappresentantiImpresa().get(firmatario.getIndex());
			} else if (firmatario.getLista().equals(CataloghiConstants.LISTA_DIRETTORI_TECNICI)) {
				soggetto = datiImpresaHelper.getDirettoriTecniciImpresa().get(firmatario.getIndex());
			} else {
				soggetto = datiImpresaHelper.getAltreCaricheImpresa().get(firmatario.getIndex());
			}
			WizardDatiImpresaHelper.fillFirmatarioFields(referente, soggetto);
		}
	}

	/**
	 * Sostituisco i codici con le vere descrizioni nel prodotto
	 *
	 * @param prodotto prodotto da processare
	 * @param maps la mappa con tutte le descrizioni
	 */
	private void updateInfoProdottoType(
			ProdottoType prodotto, 
			Map<String, LinkedHashMap<String, String>> maps) 
	{
		if (StringUtils.isNotBlank(prodotto.getAliquotaIVA())) {
			prodotto.setAliquotaIVA(maps.get(InterceptorEncodedData.LISTA_ALIQUOTE_IVA).get(prodotto.getAliquotaIVA()));
		}
		if (prodotto.getImmagine() != null) {
			prodotto.getImmagine().setFile(null);
		}
		if (prodotto.getCertificazioniRichieste() != null && prodotto.getCertificazioniRichieste().getDocumentoArray() != null) {
			for (DocumentoType documento : prodotto.getCertificazioniRichieste().getDocumentoArray()) {
				documento.setFile(null);
			}
		}
		if (prodotto.getSchedeTecniche() != null && prodotto.getSchedeTecniche().getDocumentoArray() != null) {
			for (DocumentoType documento : prodotto.getSchedeTecniche().getDocumentoArray()) {
				documento.setFile(null);
			}
		}
		if (StringUtils.isNotBlank(prodotto.getStato())) {
			prodotto.setStato(maps.get(InterceptorEncodedData.LISTA_STATI_PRODOTTO)
					.get(prodotto.getStato()));
		}
		if (StringUtils.isNotBlank(prodotto.getUnitaMisuraAcquisto())) {
			prodotto.setUnitaMisuraAcquisto(maps.get(InterceptorEncodedData.LISTA_TIPI_UNITA_MISURA)
					.get(prodotto.getUnitaMisuraAcquisto()).toLowerCase());
		}
		if (StringUtils.isNotBlank(prodotto.getUnitaMisuraDetermPrezzo())) {
			prodotto.setUnitaMisuraDetermPrezzo(maps.get(InterceptorEncodedData.LISTA_TIPI_UNITA_MISURA)
					.get(prodotto.getUnitaMisuraDetermPrezzo()).toLowerCase());
		}
		if (StringUtils.isNotBlank(prodotto.getUnitaMisuraTempoConsegna())) {
			prodotto.setUnitaMisuraTempoConsegna(maps.get(InterceptorEncodedData.LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA)
					.get(prodotto.getUnitaMisuraTempoConsegna()).toLowerCase());
		}
		if (StringUtils.isNotBlank(prodotto.getTipoArticolo())) {
			prodotto.setTipoArticolo(maps.get(InterceptorEncodedData.LISTA_TIPI_ARTICOLO)
					.get(prodotto.getTipoArticolo()));
		}
	}

}
