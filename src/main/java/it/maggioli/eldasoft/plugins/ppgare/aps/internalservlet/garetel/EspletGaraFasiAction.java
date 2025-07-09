package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraLottoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class EspletGaraFasiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6707951860730141513L;

	public static final String SESSION_ID_DETT_ESPLETAMENTO_GARA = "dettaglioEspletamentoFasiGara";

	private Map<String, Object> session;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private Long faseGara;
	private Boolean lotti;
	private Boolean abilitaAperturaDocAmm;
	private Boolean abilitaValTec;
	private Boolean abilitaOffEco;
	private Boolean abilitaGraduatoria;
	private Boolean visibileValTec;
	private Boolean visibileOffEco;
	private Boolean visibileGraduatoria;
	private int tipologiaGara;
	private Integer tipoOffertaTelematica;
	private boolean costoFisso;
	private boolean isConcorsoPrimoGrado;
	private boolean with2Phase;
	private boolean canShowRankingFirstGradeCompetition;

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public Long getFaseGara() {
		return faseGara;
	}

	public void setFaseGara(Long faseGara) {
		this.faseGara = faseGara;
	}
	
	public Boolean getLotti() {
		return lotti;
	}

	public void setLotti(Boolean lotti) {
		this.lotti = lotti;
	}

	public Boolean getAbilitaAperturaDocAmm() {
		return abilitaAperturaDocAmm;
	}

	public void setAbilitaAperturaDocAmm(Boolean abilitaAperturaDocAmm) {
		this.abilitaAperturaDocAmm = abilitaAperturaDocAmm;
	}

	public Boolean getAbilitaValTec() {
		return abilitaValTec;
	}

	public void setAbilitaValTec(Boolean abilitaValTec) {
		this.abilitaValTec = abilitaValTec;
	}

	public Boolean getAbilitaOffEco() {
		return abilitaOffEco;
	}

	public void setAbilitaOffEco(Boolean abilitaOffEco) {
		this.abilitaOffEco = abilitaOffEco;
	}

	public Boolean getAbilitaGraduatoria() {
		return abilitaGraduatoria;
	}

	public void setAbilitaGraduatoria(Boolean abilitaGraduatoria) {
		this.abilitaGraduatoria = abilitaGraduatoria;
	}


	public Boolean getVisibileValTec() {
		return visibileValTec;
	}	
	
	public void setVisibileValTec(Boolean visibileValTec) {
		this.visibileValTec = visibileValTec;
	}

	public Boolean getVisibileOffEco() {
		return visibileOffEco;
	}

	public void setVisibileOffEco(Boolean visibileOffEco) {
		this.visibileOffEco = visibileOffEco;
	}

	public Boolean getVisibileGraduatoria() {
		return visibileGraduatoria;
	}

	public void setVisibileGraduatoria(Boolean visibileGraduatoria) {
		this.visibileGraduatoria = visibileGraduatoria;
	}
	

	public boolean isConcorsoPrimoGrado() {
		return isConcorsoPrimoGrado;
	}
	
	public boolean isWith2Phase() {
		return with2Phase;
	}

	public boolean isCanShowRankingFirstGradeCompetition() {
		return canShowRankingFirstGradeCompetition;
	}

	/**
	 * ... 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		Event evento = null;
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setLevel(Event.Level.INFO);
				evento.setDestination(this.codice);
				evento.setEventType(PortGareEventsConstants.ACCESSO_FASI_GARA);
				evento.setMessage("L'utente richiede l'accesso alla consultazione delle fasi di gara della procedura " + this.codice);

				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				
				this.lotti = false;
				this.tipologiaGara = 0;
				if(dettGara != null && dettGara.getDatiGeneraliGara() != null) {
					this.lotti = 
						dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE
						|| (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO);
					
					this.tipologiaGara = dettGara.getDatiGeneraliGara().getTipologia();
				}

				boolean visualizzaEspletamento = dettGara.getDatiGeneraliGara().isVisualizzaEspletamento();
				this.tipoOffertaTelematica = dettGara.getDatiGeneraliGara().getTipoOffertaTelematica();
				
				// concorsi di progettazione
				isConcorsoPrimoGrado = StringUtils.equals("9", dettGara.getDatiGeneraliGara().getIterGara());
				with2Phase = dettGara.getDatiGeneraliGara().getTipoConcorso() != null
							 && dettGara.getDatiGeneraliGara().getTipoConcorso() == 2;
				canShowRankingFirstGradeCompetition =  bandiManager.canShowRankingFirstGradeCompetition(codice);

				// una OEPV a costo fisso non ha offerta economica!!! 
				this.costoFisso = (boolean)(dettGara.getDatiGeneraliGara().getCostoFisso() == 1);
				int numLottiEco = getNumeroLottiBustaEconomica(dettGara); 
				
				boolean conOffertaTecnica = this.bandiManager.isGaraConOffertaTecnica(this.codice);
				
				long faseGara = -1;
				boolean proceduraTelematica = false;
				boolean abilitaRiepilogoOfferta = false;
				
				if(dettGara != null && dettGara.getDatiGeneraliGara() != null) {
					
					proceduraTelematica = dettGara.getDatiGeneraliGara().isProceduraTelematica();
					
					faseGara = calcolaFaseGara(dettGara);
					
					// RICHIESTA D'OFFERTA
					if (dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null
						&& dettGara.getDatiGeneraliGara().isProceduraTelematica()) {
						
						List<String> stati = new ArrayList<String>();
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_SCARTATA);

						List<DettaglioComunicazioneType> dettComunicazioni = 
							ComunicazioniUtilities.retrieveComunicazioniConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice,
										PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT, 
										stati);
					
						// per gare a lotti il riepilogo si abilita solo alla scadenza dei termini
						if(this.lotti) {
							// calcola se sono scaduti i termini...
							//abilitaRiepilogoOfferta = (BandiAction.countComunicazioniDaProcessare(dettComunicazioni) > 0);
							abilitaRiepilogoOfferta = (dettComunicazioni != null && dettComunicazioni.size() > 0);
						} else {
							abilitaRiepilogoOfferta = (dettComunicazioni != null);
						}
					}
				}
				
				this.setFaseGara(faseGara);
				this.setAbilitaAperturaDocAmm(false);
				this.setAbilitaValTec(false);
				this.setAbilitaOffEco(false);
				this.setAbilitaGraduatoria(false);
				this.setVisibileValTec(conOffertaTecnica);
				this.setVisibileOffEco((!this.lotti && !this.costoFisso) || (this.lotti && numLottiEco > 0));
				this.setVisibileGraduatoria(true);
				if("8".equals(dettGara.getDatiGeneraliGara().getIterGara())) {
					this.setVisibileGraduatoria(false);
				}
				
				if(proceduraTelematica && faseGara >= 2 && abilitaRiepilogoOfferta && visualizzaEspletamento) {
					// fase di gara
					//  - 2 amministrativa
					//  - 5 tecnica
					//  - 6 economica
					//  - 7 aggiudicazione
					
					this.setAbilitaAperturaDocAmm( faseGara >= 2 );
					
					this.setAbilitaValTec( faseGara >= 5 && conOffertaTecnica ); 
					
					this.setAbilitaOffEco( faseGara >= 6 );
					
					this.setAbilitaGraduatoria(
						(faseGara > 6 && !with2Phase)
							|| (with2Phase && canShowRankingFirstGradeCompetition)
					);
				} else {
					this.addActionError(this.getText("Errors.espletamento.nonDisponibile"));
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.espletamento.nonDisponibile"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
				
				// memorizza in sessione le abilitazioni della gara...
				this.saveAbilitazioniGara();
				
			} catch(Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "view");
				ExceptionUtils.manageExceptionError(e, this);
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage(e);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		// registra l'evento
		if(evento != null) {
			eventManager.insertEvent(evento);
		}
		
		return this.getTarget(); 
	}

	/**
	 * ...
	 */
	private int getNumeroLottiBustaEconomica(DettaglioGaraType dettaglioGara) {
		// NB: una OEPV a costo fisso non ha offerta economica!!!
		int numLottiEco = 0;
		
		for(int i = 0; i < dettaglioGara.getLotto().length; i++) {
			// una OEPV a costo fisso non ha offerta economica!!! ==> NO LOTTO !!!
			int costoFisso = (dettaglioGara.getLotto(i).getCostoFisso() != null 
				? dettaglioGara.getLotto(i).getCostoFisso().intValue() 
				: 0
			);
			if(costoFisso != 1)
				numLottiEco++; 
		}
		
		return numLottiEco;
	}
	
	/**
	 * @throws ApsException 
	 * 
	 */
	private long calcolaFaseGara(DettaglioGaraType dettaglioGara) throws ApsException {
		long fasgar = -1;
		
	 	if(dettaglioGara.getDatiGeneraliGara().getFaseGara() != null) {
	 		fasgar = dettaglioGara.getDatiGeneraliGara().getFaseGara().longValue();
		}
		
		// in caso di gara a LOTTI il fasgar e' presente su ogni lotto e va controllato per lotto...
		if(lotti) {
			List<EspletGaraOperatoreType> fasiLotti = bandiManager
					.getEspletamentoGaraValTecElencoOperatoriLotto(codice, getCurrentUser().getUsername());
			long faseGaraTec = getMaxFaseGara(fasiLotti);
			
			fasiLotti = bandiManager
					.getEspletamentoGaraOffEcoElencoOperatoriLotto(codice, getCurrentUser().getUsername());
			long faseGaraEco = getMaxFaseGara(fasiLotti);
		
			// basta 1 lotto della busta tec/eco per abilitare il relativo menu
			fasgar = Math.max(fasgar, Math.max(faseGaraTec, faseGaraEco));
		}
		
		return fasgar;
	}
		
	/**
	 * ...
	 */
	private long getMaxFaseGara(List<EspletGaraOperatoreType> fasiLotti) {
		long fasgar = -1;
		if(fasiLotti != null)
			fasgar = fasiLotti.stream()
				.filter(oper -> oper.getLotti() != null && oper.getLotti().length > 0)
				.map(oper -> Arrays.asList(oper.getLotti()).stream()
						.filter(l -> l.getFaseGara() != null)
						.map(l -> l.getFaseGara())
						.max(Long::compare)
						.orElse(-1L))
				.max(Long::compare)
				.orElse(-1L);
		return fasgar;
	}
		
	/**
	 * memorizza in sessione le abilitazioni della gara
	 */
	private void saveAbilitazioniGara() {
		this.session.remove(SESSION_ID_DETT_ESPLETAMENTO_GARA);
		
		HashMap<String, Object> abilitazioni = new HashMap<String, Object>();
		abilitazioni.put("codice", this.codice);
		abilitazioni.put("tipologiaGara", new Integer(this.tipologiaGara));
		abilitazioni.put("abilitaAperturaDocAmm", this.abilitaAperturaDocAmm);
		abilitazioni.put("abilitaValTec", this.abilitaValTec);
		abilitazioni.put("abilitaOffEco", this.abilitaOffEco);
		abilitazioni.put("abilitaGraduatoria", this.abilitaGraduatoria);
		abilitazioni.put("visibileValTec", this.visibileValTec);
		abilitazioni.put("visibileOffEco", this.visibileOffEco);
		abilitazioni.put("visibileOffEco", this.visibileOffEco);
		abilitazioni.put("tipoOffertaTelematica", new Integer(this.tipoOffertaTelematica));
		this.session.put(SESSION_ID_DETT_ESPLETAMENTO_GARA, abilitazioni);
	}

	/**
	 * recupera una abilitazione della gara 
	 */
	@SuppressWarnings("unchecked")
	private static Object getAbilitazioneGara(Map<String, Object> session, String codice, String key) {		
		Map<String, Object> abilitazioni = (Map<String, Object>) session.get(SESSION_ID_DETT_ESPLETAMENTO_GARA);
		Object value = null;
		if(abilitazioni != null) {
			String cod = (String) abilitazioni.get("codice");
			if(cod != null && codice != null && cod.equalsIgnoreCase(codice)) {
				value = abilitazioni.get(key);
			}
		}
		return value;
	}

	/**
	 * verifica se la documentazione amministrativa per la gara è abilitata
	 */
	public static boolean isAperturaDocAmministrativaAbilitata(Map<String, Object> session, String codice) {
		Boolean value = (Boolean) getAbilitazioneGara(session, codice, "abilitaAperturaDocAmm");
		return (value != null ? value : false);
	}
	
	/**
	 * verifica se la valutazione tecnica per la gara è abilitata
	 */
	public static boolean isValutazioneTecnicaAbilitata(Map<String, Object> session, String codice) {
		Boolean value = (Boolean) getAbilitazioneGara(session, codice, "abilitaValTec");
		return (value != null ? value : false);
	}
	
	/**
	 * verifica se l'offerta economica per la gara è abilitata
	 */
	public static boolean isOffertaEconomicaAbilitata(Map<String, Object> session, String codice) {
		Boolean value = (Boolean) getAbilitazioneGara(session, codice, "abilitaOffEco");
		return (value != null ? value : false);
	}
		
	/**
	 * verifica se la graduatoria per la gara è abilitata
	 */
	public static boolean isGraduatoriaAbilitata(Map<String, Object> session, String codice) {
		Boolean value = (Boolean) getAbilitazioneGara(session, codice, "abilitaGraduatoria");
		return (value != null ? value : false);	
	}
	
	/**
	 * tipologia della gara
	 */
	public static int getTipologiaGara(Map<String, Object> session, String codice) {
		// PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE
		// PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO
		// ...
		Integer value = (Integer) getAbilitazioneGara(session, codice, "tipologiaGara");
		return (value != null ? value.intValue() : 0);	
	}
	
	/**
	 * tipo offerta telematica 
	 * 	1 = solo upload
	 *  2 = ...
	 *  3 = solo upload + questionario
	 */
	public static int getTipoOffertaTelematica(Map<String, Object> session, String codice) {
		Integer value = (Integer) getAbilitazioneGara(session, codice, "tipoOffertaTelematica");
		return (value != null ? value.intValue() : 0);
	}
	
}
