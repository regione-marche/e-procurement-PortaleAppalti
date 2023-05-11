package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaOperatoreIscrittoType;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.OperatoreIscrittoType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CatalogResultBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Action per le operazioni sui bandi d'iscrizione. Implementa le operazioni
 * CRUD sulle schede.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 * 
 */
/**
 * @author luca.sirri
 *
 */
public class BandiIscrizioneAction extends EncodedDataAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = 7302885346940171730L;

	private Map<String, Object> session;

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
	
	// usati in viewCategorieOperatore() 
	private List<CategoriaBandoIscrizioneType> categorieBando;
	@Validate(EParamValidation.CODICE)
	private String[] codiceCategoria;
	@Validate(EParamValidation.CLASSIFICAZIONI)
	private String[] classeDa;
	@Validate(EParamValidation.CLASSIFICAZIONI)
	private String[] classeA;
	private Integer tipoClassifica;
	
	// usati in viewOperatoriIscritti()
	private List<OperatoreIscrittoType> operatoriIscritti;
	private HashMap<String, List<CategoriaOperatoreIscrittoType>> categorieOperatoriIscritti;
	private HashMap<String, String> titoliCategorie;

	//Utilizzato dal ...
	private List<CatalogResultBean> processedResult;
	private List<CatalogResultBean> notProcessedResult;


	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager manager) {
		this.bandiManager = manager;
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

	public void setDettaglio(
			DettaglioBandoIscrizioneType dettaglioBandoIscrizione) {
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

	public Boolean getImpresaAbilitataAlRinnovo() {
		return impresaAbilitataAlRinnovo;
	}

	public void setImpresaAbilitataAlRinnovo(Boolean impresaAbilitataAlRinnovo) {
		this.impresaAbilitataAlRinnovo = impresaAbilitataAlRinnovo;
	}
	
	public int getTIPOLOGIA_ELENCO_STANDARD() {
		return PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD;
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

	public void setNumComunicazioniRicevuteDaLeggere(
			int numComunicazioniRicevuteDaLeggere) {
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

	public void setNumComunicazioniArchiviateDaLeggere(
			int numComunicazioniArchiviateDaLeggere) {
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

	public List<CategoriaBandoIscrizioneType> getCategorieBando() {
		return categorieBando;
	}

	public String[] getCodiceCategoria() {
		return codiceCategoria;
	}

	public String[] getClasseDa() {
		return classeDa;
	}

	public String[] getClasseA() {
		return classeA;
	}

	public Integer getTipoClassifica() {
		return tipoClassifica;
	}

	public void setTipoClassifica(Integer tipoClassifica) {
		this.tipoClassifica = tipoClassifica;
	}

	public List<OperatoreIscrittoType> getOperatoriIscritti() {
		return operatoriIscritti;
	}

	public void setOperatoriIscritti(List<OperatoreIscrittoType> operatoriIscritti) {
		this.operatoriIscritti = operatoriIscritti;
	}		

	public HashMap<String, List<CategoriaOperatoreIscrittoType>> getCategorieOperatoriIscritti() {
		return categorieOperatoriIscritti;
	}

	public void setCategorieOperatoriIscritti(
			HashMap<String, List<CategoriaOperatoreIscrittoType>> categorieOperatoriIscritti) {
		this.categorieOperatoriIscritti = categorieOperatoriIscritti;
	}	
	
	public HashMap<String, String> getTitoliCategorie() {
		return titoliCategorie;
	}

	public void setTitoliCategorie(HashMap<String, String> titoliCategorie) {
		this.titoliCategorie = titoliCategorie;
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

	public List<CatalogResultBean> getProcessedResult() {
		return processedResult;
	}

	public List<CatalogResultBean> getNotProcessedResult() {
		return notProcessedResult;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio bando
	 * iscrizione.
	 * 
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		this.setGenere(10L);

		// se si proviene dall'EncodedDataAction di InitIscrizione con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if ("block".equals(this.getTarget())) {
		    this.setTarget(SUCCESS);
		}
		try {
			this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_GARA);
			
			DettaglioBandoIscrizioneType bando = this.getBandiManager()
					.getDettaglioBandoIscrizione(this.codice);
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
							InitIscrizioneAction.calcolaFase(
									dataAttuale,
									bando.getDatiGeneraliBandoIscrizione()));
					
					this.setTipoClassifica(bando.getDatiGeneraliBandoIscrizione()
							.getTipoClassifica());
					
				} catch (Exception e) {
					// non si fa niente, si usano i dati ricevuti dal servizio e
					// quindi i test effettuati nel dbms server
				}
				
				StatisticheComunicazioniPersonaliType stat = this.getBandiManager()
					.getStatisticheComunicazioniPersonali(
							this.getCurrentUser().getUsername(), 
							this.codice, null,
							null);
				this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
				this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
				this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
				this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
				this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
				this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
				this.stato = this.getBandiManager()
						.getStatoIscrizioneABandoIscrizione(
								this.getCurrentUser().getUsername(), 
								this.codice);

				this.setImpresaAbilitataAlRinnovo(
						this.bandiManager.isImpresaAbilitataRinnovo(
								this.codice, 
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
				
				idComunicazioneBozza = InitIscrizioneAction.getComunicazioneStatoBozza(
						this.codice, 
						PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO,
						this.getCurrentUser().getUsername());
				this.setAggiornamentoInBozza(idComunicazioneBozza != null && idComunicazioneBozza.longValue() > 0);
				
				
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
				this.session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, true);				
			}
			
			this.session.put(PortGareSystemConstants.SESSION_ID_DETT_GARA, bando);
			
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}


	public String viewCategorie() {
		try {
			String filtro = StringUtils.stripToNull(this.filtroCategorie);
			CategoriaBandoIscrizioneType[] categorie = this.getBandiManager()
					.getElencoCategorieBandoIscrizione(this.codice, filtro);					
			this.setCategorie(categorie);
			
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewCategorie");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}


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

	public String viewOperatoriIscritti() {
		try {
			// metti in sessione il dettaglio della procedura...
			DettaglioBandoIscrizioneType bando = (DettaglioBandoIscrizioneType)this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_GARA);
			
			if((bando == null) ||
			   (bando != null && bando.getDatiGeneraliBandoIscrizione() != null && 
			    !this.codice.equalsIgnoreCase(bando.getDatiGeneraliBandoIscrizione().getCodice()))) 
			{
				bando = this.getBandiManager().getDettaglioBandoIscrizione(this.codice);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_GARA, bando);
			}
			this.setDettaglio(bando);
			this.setTipoClassifica(bando.getDatiGeneraliBandoIscrizione().getTipoClassifica());
			
			if( !bando.getDatiGeneraliBandoIscrizione().getPubblicaOperatori() ) {
				this.addActionError(this.getText("Errors.function.notEnabled"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// recupera l'elenco dei operatori iscritti e delle categorie 
				// da associare agli operatori iscritti... 
				this.setOperatoriIscritti( this.bandiManager.getOperatoriIscritti(this.codice) );
				
				// prepara una hash map contentente per ogni operatore le catagorie 
				// associate...
				this.categorieOperatoriIscritti = 
					new HashMap<String, List<CategoriaOperatoreIscrittoType>>();
				
				// recupera l'elenco di tutte le categorie e l'elenco delle 
				// categorie per operatore (ordinato per operatore,categoria)...
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
					.getElencoCategorieBandoIscrizione(this.codice, null);
	
				this.titoliCategorie = new HashMap<String, String>();
				if(categorie != null) {
					for(int i = 0; i < categorie.length; i++) {
						this.titoliCategorie.put(categorie[i].getCodice(), categorie[i].getTitolo());
					}
				}
	
				List<CategoriaOperatoreIscrittoType> categorieOperatori = this.bandiManager
					.getElencoCategorieOperatoriIscritti(codice);
				
				// per ogni categoria di un operatore iscritto, 
				// cerca il corrispondente nell'elenco delle categorie della procedura
				// e recupera il ramo dell'albero andando a ritroso finche' 
				// il livello e' > 1
				List<CategoriaOperatoreIscrittoType> tmpCategorie = null;
				String lastDitta = "";
				for(int i = 0; i < categorieOperatori.size(); i++) {
					
					if( !lastDitta.equals(categorieOperatori.get(i).getDitta()) ) {
						// se cambia l'operatore, crea una nuova lista di categorie
						// da associare al nuovo operatore...
						tmpCategorie = new ArrayList<CategoriaOperatoreIscrittoType>();
						this.categorieOperatoriIscritti
							.put(categorieOperatori.get(i).getDitta(), tmpCategorie);
						lastDitta = categorieOperatori.get(i).getDitta();
					} else {
						// recupera la lista di categorie relativa all'operatore
						// corrente...
						tmpCategorie = this.categorieOperatoriIscritti
							.get(categorieOperatori.get(i).getDitta());
					}				
					
					if(tmpCategorie != null) {
						// prima di aggiungere la categoria, verifica in che ramo 
						// dell'albero e' posizionata ed aggiungi i nodi del ramo...
						if(categorie != null) {
							List<CategoriaBandoIscrizioneType> ramo = 
								new ArrayList<CategoriaBandoIscrizioneType>();
							
							for(int j = 0; j < categorie.length; j++) {
								if(categorie[j].getCodice().equals(categorieOperatori.get(i).getCodice())) {
									// ...recupera tutti i nodi padre del ramo 
									// fino alla radice...
									int livello = categorie[j].getLivello();
									livello--;
									for(int k = j-1; k >= 0; k--) {
										boolean inserito = false;
										for(int t = 0; t < tmpCategorie.size(); t++) {
											// verifica se un nodo padre è già stato inserito...
											if(categorie[k].getCodice().equals(tmpCategorie.get(t).getCodice())){
												inserito = true;
												break;
											} 	
										}
										if(inserito || categorie[k].getLivello() <= 1) {
											break;
										}
										if(categorie[k].getLivello() == livello) {
											ramo.add(categorie[k]);
											livello--;
										}
									}
									break;
								}
							}
							
							// aggiungi i nodi del ramo...
							for(int j = ramo.size()-1; j >= 0; j--) {
								CategoriaOperatoreIscrittoType cat = new CategoriaOperatoreIscrittoType();
								cat.setTipoAppalto(ramo.get(j).getTipoAppalto());
								cat.setCodice(ramo.get(j).getCodice());
								cat.setDescrizione(ramo.get(j).getDescrizione());
								cat.setLivello(ramo.get(j).getLivello());
								cat.setFoglia(ramo.get(j).isFoglia());
								tmpCategorie.add( cat );
							}
						}
	
						// aggiungi la categoria dell'operatore iscritto...
						tmpCategorie.add( categorieOperatori.get(i) );
					}
				}
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewOperatoriIscritti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception t) {
			ApsSystemUtils.logThrowable(t, this, "viewOperatoriIscritti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * restituisce true se l'elenco e' ancora valido rispetto alla data odierna   
	 */
	public boolean isAttiva() {
		Date dataFineValidita = null; 
		if(dettaglio != null
		   && dettaglio.getDatiGeneraliBandoIscrizione() != null
		   && dettaglio.getDatiGeneraliBandoIscrizione().getDataFineValidita() != null) 
		{
			// dataFineValidita + "23:59:59"
			Calendar cal = Calendar.getInstance();
			cal.setTime(dettaglio.getDatiGeneraliBandoIscrizione().getDataFineValidita());
		    cal.set(Calendar.HOUR, 23);
		    cal.set(Calendar.MINUTE, 59);
		    cal.set(Calendar.SECOND, 59);
		    dataFineValidita = cal.getTime();
		}
		return !(dataFineValidita != null && dataFineValidita.before(new Date(System.currentTimeMillis())));
	}

}
