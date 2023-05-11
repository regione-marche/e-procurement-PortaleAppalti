package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;

public class RilancioPrezziUnitariAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3121584664242877001L;

	private Map<String, Object> session;
	
	private IAsteManager asteManager;
	private IBandiManager bandiManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	@Validate(EParamValidation.IMPORTO)
	private String importoOfferto;
	private DettaglioAstaType dettaglioAsta;
	private List<DettaglioRilancioType> listaRilanci;
	private Long maxNumeroDecimali;
	private Integer discriminante;			// 1 = VOCI SOGGETTE RIBASSO,  2 = VOCI NON SOGGETTE RIBASSO
	private Double totalePrezziUnitari;
	
	// visualizza i dettagli dell'offerta
	private List<VoceDettaglioAstaType> vociDettaglio;
	private List<VoceDettaglioAstaType> vociNonSoggette;
	
	// proprità usate per recuperare i valori degli input del dialog
	@Validate(EParamValidation.QUANTITA)
	private String[] quantita;
	@Validate(EParamValidation.IMPORTO)
	private String[] prezzoUnitario;
	@Validate(EParamValidation.DESCRIZIONE)
	private String[] descrizione;
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setAsteManager(IAsteManager asteManager) {
		this.asteManager = asteManager;
	}

	public void setNtpManager(INtpManager ntpManager) {
	}

	public void setEventManager(IEventManager eventManager) {
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}

	public DettaglioAstaType getDettaglioAsta() {
		return dettaglioAsta;
	}

	public void setDettaglioAsta(DettaglioAstaType dettaglioAsta) {
		this.dettaglioAsta = dettaglioAsta;
	}

	public List<DettaglioRilancioType> getListaRilanci() {
		return listaRilanci;
	}

	public void setListaRilanci(List<DettaglioRilancioType> listaRilanci) {
		this.listaRilanci = listaRilanci;
	}

	public List<VoceDettaglioAstaType> getVociDettaglio() {
		return vociDettaglio;
	}

	public void setVociDettaglio(List<VoceDettaglioAstaType> vociDettaglio) {
		this.vociDettaglio = vociDettaglio;
	}

	public List<VoceDettaglioAstaType> getVociNonSoggette() {
		return vociNonSoggette;
	}

	public void setVociNonSoggette(List<VoceDettaglioAstaType> vociNonSoggette) {
		this.vociNonSoggette = vociNonSoggette;
	}

	public String[] getQuantita() {
		return quantita;
	}

	public void setQuantita(String[] quantita) {
		this.quantita = quantita;
	}

	public String[] getPrezzoUnitario() {
		return prezzoUnitario;
	}

	public void setPrezzoUnitario(String[] prezzoUnitario) {
		this.prezzoUnitario = prezzoUnitario;
	}

	public String[] getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String[] descrizione) {
		this.descrizione = descrizione;
	}
	
	public Long getMaxNumeroDecimali() {
		return maxNumeroDecimali;
	}

	public void setMaxNumeroDecimali(Long maxNumeroDecimali) {
		this.maxNumeroDecimali = maxNumeroDecimali;
	}	
	
	public String getImportoOfferto() {
		return importoOfferto;
	}

	public void setImportoOfferto(String importoOfferto) {
		this.importoOfferto = importoOfferto;
	}

	public Double getTotalePrezziUnitari() {
		return totalePrezziUnitari;
	}

	public void setTotalePrezziUnitari(Double totalePrezziUnitari) {
		this.totalePrezziUnitari = totalePrezziUnitari;
	}

	public Integer getDiscriminante() {
		return discriminante;
	}

	public void setDiscriminante(Integer discriminante) {
		this.discriminante = discriminante;
	}

	
	public int getVOCI_SOGGETTE_RIBASSO() { 
		return 1; 
	}
	
	public int getVOCI_NON_SOGGETTE_RIBASSO() { 
		return 2; 
	}
	
	
	/**
	 * ...	
	 */
	private boolean isUserLogged() {
		return RilancioAction.isUserLogged(this); 
	}

	/**
	 * Visualiza l'elenco dei rilanci effettuati in un'asta
	 *
	 * @return Il codice del risultato dell'azione
	 */
	public String listRilanci() {
		this.setTarget(SUCCESS);
		
		try {
			if (isUserLogged()) {
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty() 
			             			 ? this.codiceLotto : this.codice);
				
				DettaglioAstaType asta = this.asteManager
					.getDettaglioAsta(codiceGara, this.getCurrentUser().getUsername());
				this.setDettaglioAsta(asta);
				
				List<DettaglioRilancioType> rilanci = this.asteManager
					.getElencoRilanci(
							this.codice, 
							this.codiceLotto, 
							this.getCurrentUser().getUsername(), 
							asta.getFase().toString());
				this.setListaRilanci(rilanci);
				
				if(rilanci == null || (rilanci != null && rilanci.size() <= 0)) {
					this.setTarget(INPUT);
				}
				
				this.setMaxNumeroDecimali(RilancioAction.getMaxNumeroDecimali(asta, this.bandiManager));
				
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "listRilanci");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	
	/**
	 * Apre la richiesta di rilancio in un'asta
	 *
	 * @return Il codice del risultato dell'azione
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if(isUserLogged()) {
			try {
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty() 
            			 		 	 ? this.codiceLotto : this.codice);
				
				// recupera i dettagli dell'asta...
				DettaglioAstaType asta = this.asteManager
					.getDettaglioAsta(codiceGara, this.getCurrentUser().getUsername());
				this.setDettaglioAsta(asta);
				
				this.setMaxNumeroDecimali(RilancioAction.getMaxNumeroDecimali(asta, this.bandiManager));
				
				// recupera l'elenco dei rilanci...
				List<DettaglioRilancioType> rilanci = this.asteManager
					.getElencoRilanci(
							this.codice, 
							this.codiceLotto, 
							this.getCurrentUser().getUsername(), 
							asta.getFase().toString());
				this.setListaRilanci(rilanci);
				
				// recupera l'elenco dell'ultima offerta a prezzi unitari...
				// voci soggette e non soggette a ribasso...
				this.vociNonSoggette = (List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE);
				List<VoceDettaglioAstaType> soggette = (List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);
				if(this.vociNonSoggette == null 
				   && soggette == null  
				   && this.listaRilanci != null && this.listaRilanci.size() > 0) {
					// recupera l'elenco dei prezzi unitari e suddividilo in
					// soggette a ribasso e non soggette a ribasso...
					List<VoceDettaglioAstaType> voci = this.asteManager.getPrezziUnitariRilancio(
							codice, 
							codiceLotto, 
							this.getCurrentUser().getUsername(),
							this.listaRilanci.get(this.listaRilanci.size() - 1).getId());
					
					if(voci != null) {
						this.vociNonSoggette = new ArrayList<VoceDettaglioAstaType>();
						soggette = new ArrayList<VoceDettaglioAstaType>();
						for(int i = 0; i < voci.size(); i++) {
							if( voci.get(i).isNonSoggettoRibasso() ) {
								this.vociNonSoggette.add(voci.get(i));
							} else {
								soggette.add(voci.get(i));
							}
						}
						
						// salva in sessione le tutte le voci (soggette e non soggette a ribasso)...
						this.session.put(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE, 
										 this.vociNonSoggette);
						this.session.put(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE, 
										 soggette);
					}
				}
				
				// inizializza le vociDettaglio per la pagina jsp...
				if (this.discriminante == null) {
					this.discriminante = getVOCI_SOGGETTE_RIBASSO();
				}
			    if(this.discriminante == getVOCI_SOGGETTE_RIBASSO()) {
			    	this.vociDettaglio = soggette;
			    } else {
			    	this.vociDettaglio = this.vociNonSoggette;
			    } 
				
				// salva in sessione la tabella dei prezzi unitari...
				this.session.put(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI, 
								 this.vociDettaglio);

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "view");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * Vai al passo successivo della richiesta d'inserimento di un rilancio d'asta
	 *
	 * @return Il codice del risultato dell'azione
	 */
	public String next() {
		this.setTarget(SUCCESS);
		
		try {
			this.calcolaTotalePrezziUnitari();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "next");
			ExceptionUtils.manageExceptionError(t, this);
			this.addActionError(t.getMessage());
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * Annulla la richiesta d'inserimento di un rilancio d'asta
	 *
	 * @return Il codice del risultato dell'azione
	 */
	public String cancel() {
		this.setTarget("cancel");
		this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI);
		this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE);
		this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);
		return this.getTarget();
	}

	/**
	 * Annulla l'operazione di editing della finestra e si ricaricano i dati.
	 */
	public String undo() {
		this.setTarget("undo");
		return this.getTarget();
	}

	/**
	 * Salva i dati inseriti nella form, invia il nuovo rilancio e 
	 * torna alla pagina della classifica
	 */
	public String save() {
		this.setTarget("save");
		
		if (isUserLogged()) {
			boolean continua = true;
			try {
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty() 
			             ? this.codiceLotto : this.codice);
				
				this.vociDettaglio = (List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI);
				this.vociNonSoggette = (List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE);
				List<VoceDettaglioAstaType> soggette = (List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);

				// recupera il dettaglio dell'asta
				DettaglioAstaType asta = this.asteManager.getDettaglioAsta(
							codiceGara, 
							this.getCurrentUser().getUsername());
				if(asta == null) {
					this.addActionError(this.getText("Errors.rilanci.generico"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					continua = false;
				}
	
				// sincronizza input e sessione... 
				if(continua) {
					// la quantità non è un campo editabile,
					// quindi viene inizializzata dai dati in sessione...
					if(this.vociDettaglio == soggette) {
						this.quantita = new String[this.vociDettaglio.size()];
						for(int i = 0; i < this.vociDettaglio.size(); i++) {
							this.quantita[i] = this.vociDettaglio.get(i).getQuantita().toString(); 
						}
							
						// solo le voci soggette a ribasso sono editabili...
						// cerca nell'elenco di tutte le voci solo quelle non soggette
						// ed aggiornale...
						for(int i = 0; i < this.prezzoUnitario.length; i++) {
							double prz = this.toDouble(this.prezzoUnitario[i]);
							double qta = this.toDouble(this.quantita[i]);
							this.vociDettaglio.get(i).setPrezzoUnitario(prz);
							this.vociDettaglio.get(i).setQuantita(qta);
							this.vociDettaglio.get(i).setAstePrezzoUnitario(prz);
							this.vociDettaglio.get(i).setAsteImportoUnitario(prz * qta);
						}
					}
					
					//this.calcolaTotalePrezziUnitari();
					
					// salva in sessione la tabella dei prezzi unitari...
					this.session.put(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI, 
									 this.vociDettaglio);
					this.session.put(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE, 
									 this.vociNonSoggette);
					this.session.put(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE, 
									 soggette);
					
					this.setTarget("save");
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				ExceptionUtils.manageExceptionError(t, this);
				this.addActionError(t.getMessage());
				this.setTarget(INPUT);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

	/**
	 * converte da testo a double
	 */
	private double toDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch(Exception e) {
			return 0;
		}	
	}
	
	/**
	 * 
	 */
	private void calcolaTotalePrezziUnitari() throws ApsException {
		String codiceGara = (!StringUtils.isEmpty(this.codiceLotto) 
		 		 ? this.codiceLotto : this.codice);
		
		List<VoceDettaglioAstaType> soggette = (List<VoceDettaglioAstaType>) this.session
			.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);

		List<VoceDettaglioAstaType> nonSoggette = (List<VoceDettaglioAstaType>) this.session
			.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE);
		
		this.totalePrezziUnitari = new Double(RilancioPrezziUnitariAction.getTotalePrezziUnitari(soggette));
		
		double importoOff = RilancioPrezziUnitariAction.getTotaleOfferta(this.getCurrentUser().getUsername(), codiceGara, soggette);
		this.importoOfferto = BigDecimal.valueOf(new Double(importoOff)).setScale(5, BigDecimal.ROUND_HALF_UP).toPlainString(); 
	}

	/**
	 * calcola il totale delle voci soggette a ribasso
	 */
	public static double getTotalePrezziUnitari(List<VoceDettaglioAstaType> voci) {
		double totPrezziUnitari = 0.0;
		if(voci != null) {
			for(int i = 0; i < voci.size(); i++) {
				if(voci.get(i).isNonSoggettoRibasso() || voci.get(i).isSoloSicurezza()) {
					// sicurezza o voce non soggetta a ribasso
				} else {
					// voce soggetta a ribasso
					double prz = voci.get(i).getAstePrezzoUnitario();
					double qta = voci.get(i).getQuantita();
					totPrezziUnitari += prz * qta;
				}
			}
		}
		return totPrezziUnitari;
	}
	
	/**
	 * calcola il totale dell'offerta, basato su
	 * 
	 * 		importo offerto = Sum( voci soggette a ribasso )
	 *						  + importo sicurezza della gara 
	 *						  + importo voci non soggetto a ribasso della gara
     *						  + importo oneri progettazione della gara
	 * @throws ApsException 
	 */
	public static double getTotaleOfferta(
			String username,
			String codiceGara,
			List<VoceDettaglioAstaType> voci) throws ApsException 
	{
		// calcolo il totale offerto come somma dei prezzi unitari 
		// e dei totali di gara per voci non soggette, sicurezza e 
		// oneri di progettazione
		// NB: se sono presenti nei prezzi unitari delle lavorazioni per
		//     sicurezza o non soggette queste righe vengono ignorate   
		//
		// Calcolato del totale offerta presente in ProcessPageOffertaAction.save()...
 
	 	// calcolo il totale offerto come somma dei prezzi unitari ed altre
	 	// componenti dell'importo a base di gara
		
		BigDecimal bdOfferta = new BigDecimal(0.0);
		try {
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());
			IAsteManager asteManager = (IAsteManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.ASTE_MANAGER, ServletActionContext.getRequest());
	
			DettaglioGaraType dettGara = bandiManager.getDettaglioGara(codiceGara);
			GaraType gara = dettGara.getDatiGeneraliGara();
			
			DettaglioAstaType asta = asteManager.getDettaglioAsta(codiceGara, username);
			
			Long decimali = RilancioAction.getMaxNumeroDecimali(asta, bandiManager);
			
			double importoSicurezza = 0;
			if (gara.isOffertaComprensivaSicurezza() && gara.getImportoSicurezza() != null) {
				importoSicurezza = gara.getImportoSicurezza();
			}
			double importoNonSoggettoRibasso = 0;
			if (gara.getImportoNonSoggettoRibasso() != null) {
				importoNonSoggettoRibasso = gara.getImportoNonSoggettoRibasso();
			}
			double importoOneriProgettazione = 0;
			if (!gara.isOneriSoggettiRibasso() && gara.getImportoOneriProgettazione() != null) {
				importoOneriProgettazione = gara.getImportoOneriProgettazione();
			}

			double importoOfferto = getTotalePrezziUnitari(voci)
									+ importoSicurezza 
									+ importoNonSoggettoRibasso
									+ importoOneriProgettazione;
			bdOfferta = BigDecimal.valueOf(new Double(importoOfferto)).setScale(decimali.intValue(), BigDecimal.ROUND_HALF_UP);
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("getTotaleOfferta", t);
		}
		return bdOfferta.doubleValue(); 
	}

}