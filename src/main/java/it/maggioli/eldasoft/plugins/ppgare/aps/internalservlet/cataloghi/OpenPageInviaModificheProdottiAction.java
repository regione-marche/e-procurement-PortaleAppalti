package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard
 * d'iscrizione all'albo
 *
 * @author Marco.Perazzetta
 */
public class OpenPageInviaModificheProdottiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4634220165132894824L;

	private IAppParamManager appParamManager;

	private Map<String, Object> session;

	@Validate
	private List<FirmatarioBean> listaFirmatari = new ArrayList<>();
	@Validate(EParamValidation.GENERIC)
	private ArrayList<String> tipoQualificaCodifica;
	
	private File riepilogo;
	@Validate(EParamValidation.FILE_NAME)
	private String riepilogoFileName;
	private boolean riepilogoAlreadyUploaded;

	@Validate(EParamValidation.CODICE)
	private String catalogo;

	private boolean variazioneOfferta;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public File getRiepilogo() {
		return riepilogo;
	}

	public List<FirmatarioBean> getListaFirmatari() {
		return listaFirmatari;
	}

	public void setListaFirmatari(List<FirmatarioBean> listaFirmatari) {
		this.listaFirmatari = listaFirmatari;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public boolean isRiepilogoAlreadyUploaded() {
		return riepilogoAlreadyUploaded;
	}

	public void setRiepilogoAlreadyUploaded(boolean riepilogoAlreadyUploaded) {
		this.riepilogoAlreadyUploaded = riepilogoAlreadyUploaded;
	}

	public String getRiepilogoFileName() {
		return riepilogoFileName;
	}

	public void setRiepilogoFileName(String riepilogoFileName) {
		this.riepilogoFileName = riepilogoFileName;
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}

	public boolean isVariazioneOfferta() {
		return variazioneOfferta;
	}

	public void setVariazioneOfferta(boolean variazioneOfferta) {
		this.variazioneOfferta = variazioneOfferta;
	}
	
	public ArrayList<String> getTipoQualificaCodifica() {
		return tipoQualificaCodifica;
	}

	public void setTipoQualificaCodifica(ArrayList<String> tipoQualificaCodifica) {
		this.tipoQualificaCodifica = tipoQualificaCodifica;
	}
	
	/**
	 * ... 
	 */
	public String openPage() {

		// si carica un eventuale errore parcheggiato in sessione, ad esempio in
		// caso di errori durante il download
		String errore = (String) this.session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}

		// se si proviene dall'EncodedDataAction di ProcessPageRiepilogo con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if (INPUT.equals(this.getTarget()) || "errorWS".equals(this.getTarget())) {
			this.setTarget(SUCCESS);
		}

		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			try {
				CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				ProdottiCatalogoSessione prodottiDaInviare = carrelloProdotti.getListaProdottiPerCatalogo()
					.get(carrelloProdotti.getCurrentCatalogo());
				
				this.riepilogoAlreadyUploaded = prodottiDaInviare.getRiepilogo() != null;
				if (riepilogoAlreadyUploaded) {
					this.riepilogoFileName = prodottiDaInviare.getRiepilogoFileName();
				}
				// --- CESSATI --- 
				WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(), 
						this);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, datiImpresaHelper);
				if (datiImpresaHelper.isLiberoProfessionista()) {
					FirmatarioBean firmatario = new FirmatarioBean();
					String nominativo = datiImpresaHelper.getAltriDatiAnagraficiImpresa().getNome() != null 
								? datiImpresaHelper.getAltriDatiAnagraficiImpresa().getCognome() + " " + datiImpresaHelper.getAltriDatiAnagraficiImpresa().getNome() 
								: datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
					firmatario.setNominativo(nominativo);
					this.getListaFirmatari().add(firmatario);
				} else {
					for (int i = 0; i < datiImpresaHelper.getLegaliRappresentantiImpresa().size(); i++) {
						ISoggettoImpresa soggetto = datiImpresaHelper.getLegaliRappresentantiImpresa().get(i);
						addSoggetto(soggetto, i, CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI);
					}
					for (int i = 0; i < datiImpresaHelper.getDirettoriTecniciImpresa().size(); i++) {
						ISoggettoImpresa soggetto = datiImpresaHelper.getDirettoriTecniciImpresa().get(i);
						addSoggetto(soggetto, i, CataloghiConstants.LISTA_DIRETTORI_TECNICI);
					}
					for (int i = 0; i < datiImpresaHelper.getAltreCaricheImpresa().size(); i++) {
						ISoggettoImpresa soggetto = datiImpresaHelper.getAltreCaricheImpresa().get(i);
						addSoggetto(soggetto, i, CataloghiConstants.LISTA_ALTRE_CARICHE);
					}
					
					ArrayList<String> qualificaFirmatario = new ArrayList<String>();
					
					for(int i = 0; i < this.getListaFirmatari().size(); i++) {
						FirmatarioBean firmatarioCorrente = this.getListaFirmatari().get(i);
						
						if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioCorrente.getLista())){
							qualificaFirmatario.add(this.getMaps()
									.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
									.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
											+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
						}else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioCorrente.getLista())){
							qualificaFirmatario.add(this.getMaps()
									.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
									.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
											+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
						}else{
							String codiceQualifica = datiImpresaHelper.getAltreCaricheImpresa().get(firmatarioCorrente.getIndex()).getSoggettoQualifica();
							qualificaFirmatario.add(this.getMaps()
									.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
									.get(codiceQualifica));
						}
					}
					
					this.setTipoQualificaCodifica(qualificaFirmatario);
				}
				this.variazioneOfferta = prodottiDaInviare.isVariazioneOfferta();
				
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	private void addSoggetto(ISoggettoImpresa soggetto, int index, String lista) {

		if (soggetto.getDataFineIncarico() == null && "1".equals(soggetto.getResponsabileDichiarazioni())) {
			FirmatarioBean firmatario = new FirmatarioBean();
			String cognome = StringUtils.capitalize(soggetto.getCognome().substring(0, 1)) + soggetto.getCognome().substring(1);
			String nome = StringUtils.capitalize(soggetto.getNome().substring(0, 1)) + soggetto.getNome().substring(1);
			firmatario.setNominativo(new StringBuilder().append(cognome).append(" ").append(nome).toString());
			firmatario.setIndex(index);
			firmatario.setLista(lista);
			this.getListaFirmatari().add(firmatario);
		}
	}

}
