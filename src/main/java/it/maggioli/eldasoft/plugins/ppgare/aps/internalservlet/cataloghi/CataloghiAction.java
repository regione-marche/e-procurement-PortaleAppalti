package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CatalogResultBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Action per le operazioni sui cataloghi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version 1.0
 * @author Stefano.Sabbadin
 *
 */
public class CataloghiAction extends EncodedDataAction implements SessionAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8145031459928348024L;

	private Map<String, Object> session;

	private ICataloghiManager cataloghiManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private INtpManager ntpManager;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.GENERIC)
	private String filtroCategorie;
	private DettaglioBandoIscrizioneType dettaglio;
	private Integer stato;
	private ComunicazioneType[] comunicazioniPersonali;
	private CategoriaBandoIscrizioneType[] categorie;
	private Boolean impresaAbilitataAlCatalogo;
	private Boolean impresaAbilitataAlRinnovo;
	private Boolean iscrizioneInBozza;
	private Boolean rinnovoInBozza;
	private Boolean aggiornamentoInBozza;
	
	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numSoccorsiIstruttori;
	private int numComunicazioniInviate;
	private Long genere;

	// viewCategorieOperatore() 
	private int tipoClassifica;


	//Utilizzato dal ...
	private List<CatalogResultBean> processedResult;
	private List<CatalogResultBean> notProcessedResult;


	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public List<CatalogResultBean> getProcessedResult() {
		return processedResult;
	}
	public List<CatalogResultBean> getNotProcessedResult() {
		return notProcessedResult;
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

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public String getFiltroCategorie() {
		return filtroCategorie;
	}

	public void setFiltroCategorie(String filtroCategorie) {
		this.filtroCategorie = filtroCategorie;
	}

	public DettaglioBandoIscrizioneType getDettaglio() {
		return dettaglio;
	}

	public void setDettaglio(DettaglioBandoIscrizioneType dettaglioBandoIscrizione) {
		this.dettaglio = dettaglioBandoIscrizione;
	}

	public Integer getStato() {
		return stato;
	}

	public ComunicazioneType[] getComunicazioniPersonali() {
		return comunicazioniPersonali;
	}

	public CategoriaBandoIscrizioneType[] getCategorie() {
		return categorie;
	}

	public void setCategorie(CategoriaBandoIscrizioneType[] categorie) {
		this.categorie = categorie;
	}

	public Boolean getImpresaAbilitataAlCatalogo() {
		return impresaAbilitataAlCatalogo;
	}

	public void setImpresaAbilitataAlCatalogo(Boolean impresaAbilitataAlCatalogo) {
		this.impresaAbilitataAlCatalogo = impresaAbilitataAlCatalogo;
	}
	
	public Boolean getImpresaAbilitataAlRinnovo() {
		return impresaAbilitataAlRinnovo;
	}

	public void setImpresaAbilitataAlRinnovo(Boolean impresaAbilitataAlRinnovo) {
		this.impresaAbilitataAlRinnovo = impresaAbilitataAlRinnovo;
	}

	public String getTAB_PRODOTTI_A_SISTEMA() {
		return CataloghiConstants.TAB_PRODOTTI_A_SISTEMA;
	}
	
	public int getTIPOLOGIA_ELENCO_CATALOGO() {
		return PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO;
	}
	
	public int getNumComunicazioniRicevute() {
		return numComunicazioniRicevute;
	}

	public void setNumComunicazioniRicevute(int numComunicazioniRicevute) {
		this.numComunicazioniRicevute = numComunicazioniRicevute;
	}

	public int getNumComunicazioniRicevuteDaLeggere() {
		return numComunicazioniRicevuteDaLeggere;
	}

	public void setNumComunicazioniRicevuteDaLeggere(int numComunicazioniRicevuteDaLeggere) {
		this.numComunicazioniRicevuteDaLeggere = numComunicazioniRicevuteDaLeggere;
	}

	public int getNumComunicazioniArchiviate() {
		return numComunicazioniArchiviate;
	}

	public void setNumComunicazioniArchiviate(int numComunicazioniArchiviate) {
		this.numComunicazioniArchiviate = numComunicazioniArchiviate;
	}

	public int getNumComunicazioniArchiviateDaLeggere() {
		return numComunicazioniArchiviateDaLeggere;
	}

	public void setNumComunicazioniArchiviateDaLeggere(int numComunicazioniArchiviateDaLeggere) {
		this.numComunicazioniArchiviateDaLeggere = numComunicazioniArchiviateDaLeggere;
	}

	public int getNumSoccorsiIstruttori() {
		return numSoccorsiIstruttori;
	}

	public void setNumSoccorsiIstruttori(int numSoccorsiIstruttori) {
		this.numSoccorsiIstruttori = numSoccorsiIstruttori;
	}

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}

	public void setNumComunicazioniInviate(int numComunicazioniInviate) {
		this.numComunicazioniInviate = numComunicazioniInviate;
	}

	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}

	public int getTipoClassifica() {
		return tipoClassifica;
	}

	public void setTipoClassifica(int tipoClassifica) {
		this.tipoClassifica = tipoClassifica;
	}
	
	public Boolean getIscrizioneInBozza() {
		return iscrizioneInBozza;
	}

	public void setIscrizioneInBozza(Boolean iscrizioneInBozza) {
		this.iscrizioneInBozza = iscrizioneInBozza;
	}

	public Boolean getRinnovoInBozza() {
		return rinnovoInBozza;
	}

	public void setRinnovoInBozza(Boolean rinnovoInBozza) {
		this.rinnovoInBozza = rinnovoInBozza;
	}

	public Boolean getAggiornamentoInBozza() {
		return aggiornamentoInBozza;
	}

	public void setAggiornamentoInBozza(Boolean aggiornamentoInBozza) {
		this.aggiornamentoInBozza = aggiornamentoInBozza;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio
	 * catalogo.
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		this.setGenere(20L);

		// se si proviene dall'EncodedDataAction di InitIscrizione con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if ("block".equals(this.getTarget())) {
			this.setTarget(SUCCESS);
		}
		try {
			DettaglioBandoIscrizioneType bando = this.cataloghiManager
							.getDettaglioCatalogo(this.codice);
			this.setDettaglio(bando);

			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente e si estrae lo stato di eventuali
			// iscrizioni al bando
			if (null != this.getCurrentUser()
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				try {
					// va rilevata l'ora ufficiale NTP per confrontarla con le
					// date in modo da abilitare eventuali comandi
					// dipendenti dal non superamento di tale data.
					// NB: se la data non viene rilevata, l'abilitazione dei
					// comandi dipende dal test sulla data effettuato nel dbms
					// server con la sua data di sistema
					Date dataAttuale = this.ntpManager.getNtpDate();
					bando.getDatiGeneraliBandoIscrizione().setFase(
							InitIscrizioneAction.calcolaFase(dataAttuale,
															 bando.getDatiGeneraliBandoIscrizione()));
					
					this.setTipoClassifica( bando.getDatiGeneraliBandoIscrizione()
							.getTipoClassifica());
					
				} catch (Exception e) {
					// non si fa niente, si usano i dati ricevuti dal servizio e
					// quindi i test effettuati nel dbms server
				}
								
				StatisticheComunicazioniPersonaliType stat = this.bandiManager
					.getStatisticheComunicazioniPersonali(this.getCurrentUser().getUsername(), 
														  this.codice,null,
														  null);	
				this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
				this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
				this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
				this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
				this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
				this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
				
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_GARA, bando);
				
				this.stato = this.bandiManager
					.getStatoIscrizioneABandoIscrizione(this.getCurrentUser().getUsername(), 
													    this.codice);

				this.setImpresaAbilitataAlCatalogo(this.cataloghiManager
						.isImpresaAbilitataCatalogo(this.codice, 
													this.getCurrentUser().getUsername()));
				this.setImpresaAbilitataAlRinnovo(this.bandiManager
						.isImpresaAbilitataRinnovo(this.codice, 
												   this.getCurrentUser().getUsername()));

				// verifica se esiste una comunicazione in stato BOZZA per 
				// la domanda di iscrizione
				Long idComunicazioneBozza = InitIscrizioneAction.getComunicazioneStatoBozza(
						this.codice, 
						PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO,
						this.getCurrentUser().getUsername());
				this.setIscrizioneInBozza(idComunicazioneBozza != null && idComunicazioneBozza.longValue() > 0);
								
				// verifica se esiste una comunicazione in stato BOZZA per 
				// la domanda di rinnovo
				idComunicazioneBozza = InitIscrizioneAction.getComunicazioneStatoBozza(
						this.codice, 
						PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE,
						this.getCurrentUser().getUsername());
				this.setRinnovoInBozza(idComunicazioneBozza != null && idComunicazioneBozza.longValue() > 0);

				// verifica se esiste una comunicazione in stato BOZZA per 
				// la domanda di rinnovo
				idComunicazioneBozza = InitIscrizioneAction.getComunicazioneStatoBozza(
						this.codice, 
						PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO,
						this.getCurrentUser().getUsername());
				this.setAggiornamentoInBozza(idComunicazioneBozza != null && idComunicazioneBozza.longValue() > 0);
				
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String viewCategorie() {
		try {
			String filtro = StringUtils.stripToNull(this.filtroCategorie);
			CategoriaBandoIscrizioneType[] categorieBando = this.bandiManager
							.getElencoCategorieBandoIscrizione(this.codice, filtro);
			this.setCategorie(categorieBando);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "viewCategorie");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	/**
	 * ...
	 */
	public String viewCategorieOperatore() {
		try {
			UserDetails userDetails = getCurrentUser();
			
			if (isSessionExpired()) {
				// la sessione e' scaduta, occorre riconnettersi
				addActionError(this.getText("Errors.sessionExpired"));
				setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// recupera tutte le categorie...
				CategoriaBandoIscrizioneType[] categorie =
						bandiManager.getElencoCategorieBandoIscrizione(codice, null);
				
				// ...recupera le categorie a cui l'operatore è iscritto
				// la lista contiene solo i codici non ordinati, quindi 
				// è necessario completare la lista utilizzando l'elenco completo...
				if (categorie != null) {
					WizardIscrizioneHelper processed = InitIscrizioneAction.retrieveProcessedCategories(
							userDetails.getUsername()
							, codice
							, null
							, comunicazioniManager
							, bandiManager
					);
					WizardIscrizioneHelper notProcessed = InitIscrizioneAction.retrieveNotProcessedCategories(
							userDetails.getUsername()
							, codice
							, null
							, comunicazioniManager
							, bandiManager
					);

					// ...cerca nella lista di tutte le categorie le info
					// mancanti per le categorie selezionate dall'operatore
					// e completa l'helper con descrizione, classeDa, classeA, ...
					processedResult = helperToResult(processed, categorie);
					notProcessedResult = helperToResult(notProcessed, categorie);

				} else {
					notProcessedResult = new ArrayList<>();
					processedResult = new ArrayList<>();
				}
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewCategorieOperatore");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "viewCategorieOperatore");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	private List<CatalogResultBean> helperToResult(WizardIscrizioneHelper helper, CategoriaBandoIscrizioneType[] categorie) {
		return helper == null
			   ? new ArrayList<>()
			   : Arrays.stream(categorie)
					   .filter(Objects::nonNull)
					   .filter(it -> helper.getCategorie() != null)
					   .filter(cat -> helper.getCategorie().getCategorieSelezionate().contains(cat.getCodice()))
					   .map(cat -> helperToResult(helper, cat))
				   .collect(Collectors.toList());
	}

	private CatalogResultBean helperToResult(WizardIscrizioneHelper helper, CategoriaBandoIscrizioneType categoria) {
		return CatalogResultBean.CatalogResultBeanBuilder.newCatalogResultBuilder()
				   .withCategory(categoria)
				   .withCodice(categoria.getCodice())
				   .withClassFrom(helper.getCategorie().getClasseDa().get(categoria.getCodice()))
				   .withClassTo(helper.getCategorie().getClasseA().get(categoria.getCodice()))
				.build();
	}

	public boolean isAttiva() {
		return !(dettaglio != null
				&& dettaglio.getDatiGeneraliBandoIscrizione() != null
				&& dettaglio.getDatiGeneraliBandoIscrizione().getDataFineValidita() != null
				&& dettaglio.getDatiGeneraliBandoIscrizione().getDataFineValidita().before(new Date(System.currentTimeMillis())));
	}

}
