package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper.FasiRettifica;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.RiepilogoBusteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 *  
 */
public class RichiesteRettificaList {
	
	private static final int STATO_NESSUNO 			= 0;
	private static final int STATO_IN_APPROVAZIONE 	= 1;
	private static final int STATO_RIFIUTATA 		= 2;
	private static final int STATO_ACCETTATA 		= 3;
	private static final int STATO_TRASMESSA 		= 4;
	private static final int STATO_ACQUISITA		= 5;
	
	/** 
     * RichiestaRettifica info relative allo stato della rettifica
     */
	public class RichiestaRettifica {
		private String codice = null;									// codice lotto/gara
		private int tipoBusta = 0;										// 0=nessuna (vedi BustaGara o PortGareSystemConstants.BUSTA_AMMINISTRATIVA, ...)
		private DettaglioComunicazioneType ultimaRichiesta = null;		// ultima richiesta di rettifica aperta
		private int statoRichiesta = 0; 								// 0=nessuno, 1=in approvazione, 2=rifiutata, 3=accettata, 4=trasmessa, 5=acquisita
		private boolean abilitaRichiesta = false;						// abilitazione della richiesta di rettifica 
		private boolean abilitaInvio = false;							// abilitazione dell'invio della rettifica
		private DettaglioComunicazioneType ultimaRettifica = null;		// ultima rettifica inviata
		private Date dataScadenza = null;
		
		public String getCodice() { return codice; }
		public int getTipoBusta() { return tipoBusta; }
		public DettaglioComunicazioneType getUltimaRichiesta() { return ultimaRichiesta; }
		public int getStatoRichiesta() { return statoRichiesta; }
		public boolean getAbilitaRichiesta() { return abilitaRichiesta; }
		public boolean getAbilitaInvio() { return abilitaInvio; }
		public DettaglioComunicazioneType getUltimaRettifica() { return ultimaRettifica; }
		public Date getDataScadenza() { return dataScadenza; }
		public Long getId() { return (ultimaRichiesta != null ? ultimaRichiesta.getId() : null); }
		public String getApplicativo() { return (ultimaRichiesta != null ? ultimaRichiesta.getApplicativo() : null); }
		
		public RichiestaRettifica(String codice, BustaGara busta) {
			this.codice = codice;
			this.tipoBusta = busta.getTipoBusta();
		}
	}  
	
	private IComunicazioniManager comunicazioniManager;
	private Map<String, List<DettaglioComunicazioneType>> richiesteRettificaAperte;
	private Map<String, RichiestaRettifica> abilitazioni;
	private Date now = new Date();
     
	/**
	 * costruttore 
	 */
	public RichiesteRettificaList(BustaGara busta) {
		initRichiesteRettifica(busta);
	}

	/**
     * restituisce le info relative ad una rettifica (richiesta, accettata, rifiutata, invio) 
     */
	public RichiestaRettifica get(String codice) {
		return (abilitazioni != null ? abilitazioni.get(codice) : null);
	}
 
    /**
     * recupera l'elenco delle richieste di rettifica ancora aperte (senza rettifiva (non rifiutate) o con rettifica non acquisita)  
     */
    private void initRichiesteRettifica(BustaGara busta) {
		richiesteRettificaAperte = null;
		abilitazioni = null;
		
		// se non c'e' una busta valida esci
		if(busta == null || (busta != null && StringUtils.isEmpty(busta.getCodiceGara()))) 
			return;
		
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());			
			comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
			
    		// STEP 1
			// recupera tutte le richieste di rettifica aperte...
			// cerca le richieste di rettifica ancora aperte
			// (richieste senza rettifica o con rettifica ancora aperta (W_INVCOM.COMKEYSESS != null))
			// NB: il filtro su "progressivo offerta" non serve, perche' si puo' inviare per busta/lotto UN'UNICA COMUNICAZIONE !!!
			List<DettaglioComunicazioneType> richieste = getComunicazioniRettifica(
					comunicazioniManager
		    		, FasiRettifica.RICHIESTA
		    		, busta.getCodiceGara()
		    		, null						// <== non usa il lotto per recuperare tutto con un'unica SQL !!! 
		    		, busta.getTipoBusta()
		    		, busta.getUsername()
		    );
			
			List<DettaglioComunicazioneType> rettifiche = getComunicazioniRettifica(
					comunicazioniManager
		    		, FasiRettifica.RETTIFICA
		    		, busta.getCodiceGara()
		    		, null						// <== non usa il lotto per recuperare tutto con un'unica SQL !!! 
		    		, busta.getTipoBusta()
		    		, busta.getUsername()
		    );
 			
			List<DettaglioComunicazioneType> accettate = getComunicazioniRisposta(
					comunicazioniManager
					, FasiRettifica.ACCETTAZIONE
					, busta.getCodiceGara()
		    		, null						// <== non usa il lotto per recuperare tutto con un'unica SQL !!! 
		    		, busta.getTipoBusta()
					, richieste
			);
			
			List<DettaglioComunicazioneType> rifiutate = getComunicazioniRisposta(
					comunicazioniManager
					, FasiRettifica.RIFIUTO
					, busta.getCodiceGara()
		    		, null						// <== non usa il lotto per recuperare tutto con un'unica SQL !!! 
		    		, busta.getTipoBusta()
					, richieste
			);
			
			// scarta tutte le richieste di rettifica, accettate, con rettifica acquisita da BO (w_docdig.digkey4 == null)
			if(richieste != null) {
				if(accettate != null && rettifiche != null) 
					richieste = richieste.stream()
						.filter(r -> !accettate.stream()
											.filter(ra -> isRispostaRichiesta(ra, r) && isLottoEquals(r.getChiave3(), ra.getChiave3()))
											.anyMatch(ra -> rettifiche.stream()
																.filter(re -> isRispostaRichiesta(re, ra) && isLottoEquals(r.getChiave3(), re.getChiave3()))
																.anyMatch(re -> isProcessata(re))
											)
						)
						.collect(Collectors.toList());
			}
			
			// INFO DEBUG
			if(richieste != null) {
				writeLog("getRichiesteRettificaAperte(...) busta=" + busta.getTipoBusta() + " richiesteAperte [");
				richieste.forEach(r -> writeLog("getRichiesteRettificaAperte(...) "
						+ r.getApplicativo() + "," + r.getId() + "," + r.getChiave1() + "," + r.getChiave2() + "," + r.getChiave3()
						+ " modello=" + r.getModello() + "," + r.getApplicativoRisposta() + "," + r.getIdRisposta())
				);
				writeLog("getRichiesteRettificaAperte(...) ]");
			}

			// STEP 2 
			// in caso di gara a lotti suddividi per lotto le richieste 
			// per gare senza lotti si inserisce un lotto fittizio con il codice gara 
			richiesteRettificaAperte = suddividiRichiestePerLotto(busta, richieste);
			
			// STEP 3 
			// inizializza le info per ogni gara/lotto 
			// per gare senza lotti si inserisce un lotto fittizio con il codice gara
			// verifica se buste (tecnica/economica) sono state acquisite (o aperte)
			// si cerca tra tutte le busta di gara processate tra tutti gli OE
			DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			//filtri.setChiave1(busta.getUsername());			// chiave1 non va usato
			filtri.setChiave2(busta.getCodiceGara());			// chiave2 e' un filtro in like!!!
			filtri.setChiave3(busta.getProgressivoOfferta());	// chiave3 e' un filtro in like!!!
			filtri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
			if(busta.getTipoBusta() == BustaGara.BUSTA_TECNICA)
				filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
			if(busta.getTipoBusta() == BustaGara.BUSTA_ECONOMICA)
				filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
			List<DettaglioComunicazioneType> acquisite = comunicazioniManager.searchElencoComunicazioni(filtri);
			
			// NB: searchElencoComunicazioni() ha i filtri sui campi chiave1,2,3,4,5 in like!!!
			// i risultati vanno filtrati per estrarre ESATTAMENTE lo username e il progressivo offerta
			if(acquisite != null)
				acquisite = acquisite.stream()
								.filter(acq-> acq.getChiave1().equalsIgnoreCase(busta.getUsername())
											  && acq.getChiave3().equalsIgnoreCase(busta.getProgressivoOfferta()))
								.collect(Collectors.toList());

			abilitazioni = elaboraAbilitazioni(
					busta
					, accettate
					, rifiutate
					, rettifiche
					, acquisite
			);
			
		} catch (Exception ex) {
			// non ci sono comunicazioni di rettifica
			ApsSystemUtils.getLogger().error("WizardRettificaHelper.getRichiesteRettificaAperte()", ex);
		}
    }
    
    /**
     * ... 
     * @throws ApsException 
     */
    public static List<DettaglioComunicazioneType> getComunicazioniRettifica(
    		IComunicazioniManager comunicazioniManager
    		, FasiRettifica fase
    		, String codiceGara
    		, String codiceLotto
    		, int tipoBusta
    		, String username
    ) throws ApsException {
		DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
		filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE);
		filtri.setChiave1(username);
		filtri.setChiave2(codiceGara);
		if(StringUtils.isNotEmpty(codiceLotto))
			filtri.setChiave3(codiceLotto);
		filtri.setModello(WizardRettificaHelper.getFaseRettifica(tipoBusta, fase));
		List<DettaglioComunicazioneType> lista = comunicazioniManager.getElencoComunicazioni(filtri);
		
		// ordina per idcom
		if(lista != null)
			lista.sort((a, b) -> (a.getId() < b.getId() ? -1 : 1));
		
		return lista;
    }
    
    /**
     * restituisce l'elenco delle comunicazioni di risposta 
     * @throws ApsException 
     */
    public static List<DettaglioComunicazioneType> getComunicazioniRisposta(
    		IComunicazioniManager comunicazioniManager
    		, FasiRettifica fase
    		, String codiceGara
    		, String codiceLotto
    		, int tipoBusta
    		, List<DettaglioComunicazioneType> richiesteRettifica
    ) throws ApsException {
    	List<DettaglioComunicazioneType> lista = null;
    	if(richiesteRettifica != null) {
	    	DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO_GARE);
			filtri.setChiave1(codiceGara);
			if(StringUtils.isNotEmpty(codiceLotto))
				filtri.setChiave2(codiceLotto);
			filtri.setModello(WizardRettificaHelper.getFaseRettifica(tipoBusta, fase));
			lista = comunicazioniManager.getElencoComunicazioni(filtri);
			
			// filtra solo le risposte relative alle richieste in stato 3=inviata, 4=inviata con messaggi, 10,11=sono inviate ma prese incarico dal documentale  
			if(lista != null)
				lista = lista.stream()
							.filter(a -> "3".equals(a.getStato()) || "4".equals(a.getStato()) || "10".equals(a.getStato()) || "11".equals(a.getStato()))
							.filter(a -> richiesteRettifica.stream().anyMatch(r -> isRispostaRichiesta(a, r)))
							.collect(Collectors.toList());
    	}
		return lista;
    }    
    
    /**
     * restituisce una mappa di liste di richieste suddividisa per lotto
     * in caso di gara non a lotti si inserisce un lotto fittizio con il codice gara !!!
     */
    private Map<String, List<DettaglioComunicazioneType>> suddividiRichiestePerLotto(
    		BustaGara busta
    		, List<DettaglioComunicazioneType> richiesteAperte
    ) {
    	Map<String, List<DettaglioComunicazioneType>> richieste = null;
    	
    	if(richiesteAperte != null) {
    		richieste = new HashMap<String, List<DettaglioComunicazioneType>>();
    		if(busta.getGestioneBuste().isGaraLottiDistinti()) {
    			// gara a lotti
				for (DettaglioComunicazioneType dettCom : richiesteAperte) {
					String lotto = dettCom.getChiave3();
					List<DettaglioComunicazioneType> lista = richieste.get(lotto);
    				if(lista == null) {
    					lista = new ArrayList<DettaglioComunicazioneType>(); 
    					richieste.put(lotto, lista);
    				}
    				lista.add(dettCom);
				}
    		} else {
    			// gara NO lotti
				richieste.put(busta.getCodiceGara(), richiesteAperte);
    		}
    	}
		
		return richieste;
    }
    
    /**
     * inizializza le info per ogni gara/lotto 
     * per gare senza lotti si inserisce un lotto fittizio con il codice gara
     */
    private Map<String, RichiestaRettifica> elaboraAbilitazioni(
    		BustaGara busta
    		, List<DettaglioComunicazioneType> accettate
    		, List<DettaglioComunicazioneType> rifiutate
    		, List<DettaglioComunicazioneType> rettifiche
    		, List<DettaglioComunicazioneType> acquisite
    ) { 
    	Map<String, RichiestaRettifica> abilitazioni = new HashMap<String, RichiestaRettifica>();
    	
		// inizializza la lista delle info per tutti i lotti (o per la gara)
		if(busta.getGestioneBuste().isGaraLottiDistinti()) {
			// gara a lotti distinti
			BustaRiepilogo bustaRie = busta.getGestioneBuste().getBustaRiepilogo();
			RiepilogoBusteHelper riepilogo = bustaRie.getHelper();
			riepilogo.getListaCompletaLotti().stream()
					.forEach(lotto -> abilitazioni.put(lotto, elaboraAbilitazioniLotto(
																lotto
																, busta
																, (richiesteRettificaAperte != null ? richiesteRettificaAperte.get(lotto) : null)
																, accettate
																, rifiutate
																, rettifiche
																, acquisite
															  )
									  )
					);
		} else {
			// gara NO lotti o plico unico offerta unica
			abilitazioni.put(busta.getCodiceGara(), elaboraAbilitazioniLotto(
														busta.getCodiceGara()
														, busta
														, (richiesteRettificaAperte != null ? richiesteRettificaAperte.get(busta.getCodiceGara()) : null)
														, accettate
														, rifiutate
														, rettifiche
														, acquisite
													 )
			);
		}
		
		return abilitazioni;
    }
    
    /**
     * calcola stato e abilitazioni della richiesta di rettifica (per lotto/gara)
     */
	private RichiestaRettifica elaboraAbilitazioniLotto(
			String codice
			, BustaGara busta
			, List<DettaglioComunicazioneType> richiesteLotto
			, List<DettaglioComunicazioneType> accettate
			, List<DettaglioComunicazioneType> rifiutate
    		, List<DettaglioComunicazioneType> rettifiche
    		, List<DettaglioComunicazioneType> acquisite
	) {		
		RichiestaRettifica info = new RichiestaRettifica(codice, busta);
		
		// per le gare a lotti recupera il codice del lotto
		String lotto = (busta.getCodiceGara().equalsIgnoreCase(codice) ? null : codice);
		
		// se esiste, recupera la richiesta di rettifica aperta piu' recente (ordine di idCom)
		info.ultimaRichiesta = null;
		int richiesteLottoSize = 0;
		if(richiesteLotto != null && richiesteLotto.size() > 0) {
			info.ultimaRichiesta = richiesteLotto.get(richiesteLotto.size() - 1);
			richiesteLottoSize = richiesteLotto.size();
		}
		
		// se esiste, recupera la rettifica piu' recente (ordine di idCom)
		info.ultimaRettifica = null;
		if(rettifiche != null && rettifiche.size() > 0) {
			List<DettaglioComunicazioneType> rettLotto =  rettifiche.stream()
					.filter(r -> isLottoEquals(lotto, r.getChiave3()))
					.collect(Collectors.toList());
			if(rettLotto != null && rettLotto.size() > 0)
				info.ultimaRettifica = rettLotto.get(rettLotto.size() - 1); 
		}
		
		// verifica se la richiesta e' stata rifiutata
		boolean rifiutata = (rifiutate != null 
			? rifiutate.stream().anyMatch(r -> isRispostaRichiesta(r, info.ultimaRichiesta))
			: false
		);
		
		// verifica se la richiesta e' stata accettata
		DettaglioComunicazioneType ultimaAccettazione = (!rifiutata && accettate != null
				? accettate.stream()
					.filter(r -> isRispostaRichiesta(r, info.ultimaRichiesta) && isLottoEquals(lotto, r.getChiave2()))
					.findFirst()
					.orElse(null)
				: null
		);
		boolean accettata = (!rifiutata && ultimaAccettazione != null);
		
		// verifica se l'invio della rettifica associato alla risposta di accettazione, e' stata trasmessa
		boolean trasmessa = false;
		if(accettata)
			if(rettifiche != null && rettifiche.size() > 0) {
				DettaglioComunicazioneType ultimaTrasmessa = rettifiche.stream()
						.filter(r -> isRispostaRichiesta(r, ultimaAccettazione) && isLottoEquals(lotto, r.getChiave3()))
						.findFirst()
						.orElse(null);
				trasmessa = (ultimaTrasmessa != null && !isProcessata(ultimaTrasmessa));	// trasmessa ma non processata da BO !!!
			}
		
		// verifica esiste 1 busta gia' acquisita/aperta da BO (anche di un altro OE)
		boolean acquisitaBO = false;
		if(acquisite != null && acquisite.size() > 0)
			acquisitaBO = acquisite.stream()
				.anyMatch(acq -> info.codice.equalsIgnoreCase(acq.getChiave2()));		// chiave2 = codice gara/lotto
		
		// stato della richiesta
		info.statoRichiesta = STATO_NESSUNO;
		if(info.ultimaRichiesta != null) 
			info.statoRichiesta = STATO_IN_APPROVAZIONE;
		if(rifiutata)
			info.statoRichiesta = STATO_RIFIUTATA;
		if(accettata)
			info.statoRichiesta = STATO_ACCETTATA;
		if(trasmessa)
			info.statoRichiesta = STATO_TRASMESSA;
		if(acquisitaBO) 
			info.statoRichiesta = STATO_ACQUISITA;
		
		info.dataScadenza = calcolaDataScadenza(ultimaAccettazione);
		boolean rettificaScaduta = (info.dataScadenza != null && now.after(info.dataScadenza));
		
		info.abilitaRichiesta = !acquisitaBO &&
								(info.statoRichiesta == STATO_NESSUNO 
								 || info.statoRichiesta == STATO_RIFIUTATA 
								 || richiesteLottoSize <= 0 
								 || rettificaScaduta);
		
		info.abilitaInvio = !acquisitaBO &&
							(info.statoRichiesta > STATO_NESSUNO 
							 && info.statoRichiesta == STATO_ACCETTATA
							 && !rettificaScaduta);
		
		writeLog("elaboraAbilitazioni(...) (" 
				+ "id=" + (info.ultimaRichiesta != null ? info.ultimaRichiesta.getId() : "") + ", " 
				+ "idprg=" + (info.ultimaRichiesta != null ? info.ultimaRichiesta.getApplicativo() : "") + ", "
				+ "stato=" + info.statoRichiesta + ", "
				+ "abilitaRichiesta=" + info.abilitaRichiesta + ", "
				+ "abilitaInvio=" + info.abilitaInvio
				+ ")");
		return info;
	}
    
    /**
     * restituisce true se la comunicazione di risposta e' quelle relativa alla richiesta  
     */
    public static boolean isRispostaRichiesta(DettaglioComunicazioneType risposta, DettaglioComunicazioneType richiesta) {
    	boolean isEqual = false;
    	if(richiesta != null && risposta != null 
    	   && richiesta.getApplicativo() != null && richiesta.getId() != null   
    	   && risposta.getApplicativoRisposta() != null && risposta.getIdRisposta() != null)
    	{
    		isEqual = richiesta.getApplicativo().equals(risposta.getApplicativoRisposta()) 
    				  && richiesta.getId().longValue() == risposta.getIdRisposta();
    	}
    	return isEqual; 
    }
    
    /**
     * restiruisce true se due lotti hanno lo stesso codice (null e "" sono equivalenti)
     */
    public static boolean isLottoEquals(String a, String b) {
    	if(a == null) a = "";
    	if(b == null) b = "";
    	return a.equalsIgnoreCase(b);
    }
    
    /**
     * restiruisce true se la comunicazione di invio rettifica e' stata acquisita/processata da BO (digkey4 viene resettato a NULL)
     */
    private boolean isProcessata(DettaglioComunicazioneType comunicazione) {
    	boolean processata = false;
    	try {
			it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType c = comunicazioniManager
					.getComunicazione(comunicazione.getApplicativo(), comunicazione.getId());
			if(c != null) {
				processata = true;
				for(AllegatoComunicazioneType a : c.getAllegato()) 
					processata = processata && (StringUtils.isEmpty(a.getCifrato()));  // => W_DOCDIG.DIGKEY4 != "crypt" o == NULL
			} else {
				ApsSystemUtils.getLogger().warn("isProcessata() comunicazione non trovata idcom=" + comunicazione.getId());
			}
		} catch (ApsException ex) {
			ApsSystemUtils.getLogger().error("isProcessata()", ex);
		}
    	return processata;
    }
    
	/**
	 * restituisce True se esiste una richiesta di rettifica accettata, in corso, senza rettifica 
	 * @throws ApsException 
	 */
	public static DettaglioComunicazioneType getRichiestaRettificaInCorso(String username, ComunicazioneType comunicazione) throws ApsException {
		DettaglioComunicazioneType richiestaRettificaAccettata = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
		
			// verifica se ci sono richieste di rettifica accettate...
			DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE);
			filtri.setChiave1(username);
			filtri.setChiave2(comunicazione.getCodice());
			filtri.setChiave3(comunicazione.getCodice2());
			filtri.setModello(WizardRettificaHelper.getFaseRettifica(comunicazione.getTipoBusta().intValue(), FasiRettifica.RICHIESTA));
			List<DettaglioComunicazioneType> richieste = comunicazioniManager.getElencoComunicazioni(filtri);
			
			// recupera le risposte di accettazione
			filtri = new DettaglioComunicazioneType();
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO_GARE);
			filtri.setChiave1(comunicazione.getCodice());
			filtri.setChiave2(comunicazione.getCodice2());
			filtri.setModello(WizardRettificaHelper.getFaseRettifica(comunicazione.getTipoBusta().intValue(), FasiRettifica.ACCETTAZIONE));
			List<DettaglioComunicazioneType> accettate = comunicazioniManager.getElencoComunicazioni(filtri);
			
			// recupera gli invii di rettifica
			filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_INVIO_COMUNICAZIONE);
			filtri.setChiave1(username);
			filtri.setChiave2(comunicazione.getCodice());
			filtri.setChiave3(comunicazione.getCodice2());
			filtri.setModello(WizardRettificaHelper.getFaseRettifica(comunicazione.getTipoBusta().intValue(), FasiRettifica.RETTIFICA));
			List<DettaglioComunicazioneType> rettifiche = comunicazioniManager.getElencoComunicazioni(filtri);
			
			// verifica se ci sono rettifiche ancora in corso 
			Date now = new Date();
			if(richieste != null && accettate != null) {
				// recupera tutte la prima richiesta di rettifica, accettata, non scaduta, senza invio rettifica
				richiestaRettificaAccettata = richieste.stream()
								.filter(r -> accettate.stream()
												.anyMatch(a -> isRispostaRichiesta(a, r) && now.before(calcolaDataScadenza(a))
														       && (rettifiche == null 
														           || (rettifiche != null && rettifiche.stream()
														        		   						.noneMatch(re -> isRispostaRichiesta(re, a))))))
								.findFirst()
								.orElse(null);				
			}
		} catch (Exception ex) {
			ApsSystemUtils.getLogger().error("getRichiestaRettificaInCorso()", ex);
		}		
		return richiestaRettificaAccettata;
	}	

	/**
	 * calcola la data e ora della data di scadenza di una comunicazione  
	 */
	private static Date calcolaDataScadenza(DettaglioComunicazioneType dettComunicazione) {
		Date scadenza = null;
		try {
			GregorianCalendar cal = new GregorianCalendar();
			
			cal.setTime(dettComunicazione.getDataScadenza().getTime());
			
			int hh = 24;
			int mm = 0;
			if(StringUtils.isNotEmpty(dettComunicazione.getOraScadenza())) {
				int i = dettComunicazione.getOraScadenza().indexOf(':');
				hh = Integer.parseInt(dettComunicazione.getOraScadenza().substring(0, i));
				mm = Integer.parseInt(dettComunicazione.getOraScadenza().substring(i + 1));
			}
			cal.set(Calendar.HOUR_OF_DAY, hh);
			cal.set(Calendar.MINUTE, mm);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			scadenza = cal.getTime();
		} catch (Exception e) {
			scadenza = null;
		}
		return scadenza;
	}
	
	
    /**
     * tracciature di DEBUG 
     */
    private static void writeLog(String msg) {
    	ApsSystemUtils.getLogger().debug(msg);
//System.out.println(msg);
    }

}
