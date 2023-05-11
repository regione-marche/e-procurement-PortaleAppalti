package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaImpresaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.List;
import java.util.Map;

/**
 * Action per le operazioni sui cataloghi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version 1.0
 * @author Stefano.Sabbadin
 *
 */
public class ArticoliAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8145031459928348024L;

	private ICataloghiManager cataloghiManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private Long articoloId;
	@Validate(EParamValidation.CODICE)
	private String codiceCatalogo;
	private ArticoloType articolo;
	private Boolean impresaAbilitataAlCatalogo;
	private Boolean visualizzaPrezziOE;
	private Map<String, Object> session;
	private Long prodottiCaricatiOE;
	private Long prodottiCaricatiAOE;
	private Double migliorPrezzoAOE;
	private boolean impresaIscrittaACategoria;
	private boolean limiteInserimentoProdottiRaggiunto;
	private Integer maxProdottiArticolo;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setArticoloId(Long articoloId) {
		this.articoloId = articoloId;
	}

	public Long getArticoloId() {
		return articoloId;
	}

	public ArticoloType getArticolo() {
		return articolo;
	}

	public void setArticolo(ArticoloType articolo) {
		this.articolo = articolo;
	}

	public void setCodiceCatalogo(String codiceCatalogo) {
		this.codiceCatalogo = codiceCatalogo;
	}

	public String getCodiceCatalogo() {
		return codiceCatalogo;
	}

	public Boolean getImpresaAbilitataAlCatalogo() {
		return impresaAbilitataAlCatalogo;
	}

	public void setImpresaAbilitataAlCatalogo(Boolean impresaAbilitataAlCatalogo) {
		this.impresaAbilitataAlCatalogo = impresaAbilitataAlCatalogo;
	}

	public Boolean getVisualizzaPrezziOE() {
		return visualizzaPrezziOE;
	}

	public void setVisualizzaPrezziOE(Boolean visualizzaPrezziOE) {
		this.visualizzaPrezziOE = visualizzaPrezziOE;
	}

	public Long getProdottiCaricatiOE() {
		return prodottiCaricatiOE;
	}

	public void setProdottiCaricatiOE(Long prodottiCaricatiOE) {
		this.prodottiCaricatiOE = prodottiCaricatiOE;
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

	public boolean isImpresaIscrittaACategoria() {
		return impresaIscrittaACategoria;
	}

	public void setImpresaIscrittaACategoria(boolean impresaIscrittaACategoria) {
		this.impresaIscrittaACategoria = impresaIscrittaACategoria;
	}

	public String getPAGINA_DETTAGLIO_ARTICOLO() {
		return CataloghiConstants.PAGINA_DETTAGLIO_ARTICOLO;
	}

	public Integer getTIPO_PRODOTTO_BENE() {
		return CataloghiConstants.TIPO_PRODOTTO_BENE;
	}

	public boolean isLimiteInserimentoProdottiRaggiunto() {
		return limiteInserimentoProdottiRaggiunto;
	}

	public void setLimiteInserimentoProdottiRaggiunto(boolean limiteInserimentoProdottiRaggiunto) {
		this.limiteInserimentoProdottiRaggiunto = limiteInserimentoProdottiRaggiunto;
	}

	public Integer getMaxProdottiArticolo() {
		return maxProdottiArticolo;
	}

	public void setMaxProdottiArticolo(Integer maxProdottiArticolo) {
		this.maxProdottiArticolo = maxProdottiArticolo;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione del dettaglio di un
	 * articolo.
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {

		try {
			DettaglioBandoIscrizioneType catalogo = this.cataloghiManager.getDettaglioCatalogo(this.codiceCatalogo);
			this.setVisualizzaPrezziOE(catalogo.getDatiGeneraliBandoIscrizione().getPrezziVisibili());
			this.setArticolo(this.cataloghiManager.getArticolo(this.articoloId));
			
			if (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				this.setImpresaAbilitataAlCatalogo(this.cataloghiManager.isImpresaAbilitataCatalogo(
						this.codiceCatalogo, 
						this.getCurrentUser().getUsername()));
				CarrelloProdottiSessione carrelloProdotti = CarrelloProdottiSessione.getInstance(
						this.session, 
						this.codiceCatalogo, 
						this.cataloghiManager);
				long numeroProdottiOESistema = cataloghiManager.getNumProdottiOEInArticolo(
						this.articoloId, 
						this.getCurrentUser().getUsername());
				long numeroProdottiOECarrello = carrelloProdotti.calculateProdottiCaricatiOE(this.articoloId);
				this.setProdottiCaricatiOE(numeroProdottiOESistema + numeroProdottiOECarrello);
				this.setLimiteInserimentoProdottiRaggiunto(this.getProdottiCaricatiOE() >= catalogo.getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo());
				this.setMaxProdottiArticolo(catalogo.getDatiGeneraliBandoIscrizione().getMaxNumProdottiPerArticolo());
				this.setProdottiCaricatiAOE(this.cataloghiManager.getNumProdottiAltriOEInArticolo(
						this.articoloId, 
						this.getCurrentUser().getUsername()));
				List<CategoriaImpresaType> listaCategorie = this.bandiManager.getCategorieImpresaPerIscrizione(
						this.getCurrentUser().getUsername(), 
						this.codiceCatalogo);
				for (CategoriaImpresaType categoria : listaCategorie) {
					if (this.getArticolo().getCodiceCategoria().equals(categoria.getCategoria())) {
						this.impresaIscrittaACategoria = true;
					}
				}
			} else {
				this.setImpresaAbilitataAlCatalogo(false);
				this.setProdottiCaricatiOE(0L);
				this.setProdottiCaricatiAOE(0L);
			}
			Double migliorPrezzo = this.cataloghiManager.getPrezzoMiglioreArticolo(this.articoloId);
			this.setMigliorPrezzoAOE(migliorPrezzo != null ? migliorPrezzo : 0);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
