package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Action per le operazioni sulle aste. 
 *
 * @version 1.0
 * @author ...
 */
public class RilancioAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 130736715279109554L;

	public static final long MAX_NUMERO_DECIMALI = 5;
	
	private Map<String, Object> session;
	
	private IAsteManager asteManager;
	private INtpManager ntpManager;
	private IEventManager eventManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	
	private Integer tipoOfferta;					// 1=ribasso | 2=importo
	@Validate(EParamValidation.IMPORTO)
	private String importoOfferto;					// offerta ad importi
	@Validate(EParamValidation.PERCENTUALE)
	private String ribasso;							// offerta a ribasso percentuale
	@Validate(EParamValidation.PERCENTUALE)
	private String aumento;							// ...
	@Validate(EParamValidation.IMPORTO)
	private String confermaImporto;					// conferma di importo
	@Validate(EParamValidation.PERCENTUALE)
	private String confermaRibasso;					// conferma di ribasso
	private Double totalePrezziUnitari;
	
	private DettaglioAstaType dettaglioAsta;
	private List<DettaglioRilancioType> listaRilanci;
	private Long maxNumeroDecimali;	
	
	@Validate(EParamValidation.GENERIC)
	private String fromPage;						// classifica | riepilogo
	@Validate(EParamValidation.GENERIC)
	private String fase;							// nel caso di riepilogo indica la 
													// fase corrente o quella appena conclusa 
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setAsteManager(IAsteManager asteManager) {
		this.asteManager = asteManager;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}	

	public IComunicazioniManager getComunicazioniManager() {
		return comunicazioniManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
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
	
	public String getImportoOfferto() {
		return importoOfferto;
	}

	public void setImportoOfferto(String importoOfferto) {
		this.importoOfferto = importoOfferto;
	}

	public String getRibasso() {
		return ribasso;
	}

	public void setRibasso(String ribasso) {
		this.ribasso = ribasso;
	}

	public String getAumento() {
		return aumento;
	}

	public void setAumento(String aumento) {
		this.aumento = aumento;
	}
	
	public String getConfermaImporto() {
		return confermaImporto;
	}

	public void setConfermaImporto(String confermaImporto) {
		this.confermaImporto = confermaImporto;
	}

	public String getConfermaRibasso() {
		return confermaRibasso;
	}

	public void setConfermaRibasso(String confermaRibasso) {
		this.confermaRibasso = confermaRibasso;
	}
	
	public Integer getTipoOfferta() {
		return tipoOfferta;
	}

	public Double getTotalePrezziUnitari() {
		return totalePrezziUnitari;
	}

	public void setTotalePrezziUnitari(Double totalePrezziUnitari) {
		this.totalePrezziUnitari = totalePrezziUnitari;
	}

	public Double getValoreImporto() {
		return this.toDouble(this.importoOfferto);
	}
	
	public Double getValoreRibasso() {
		return this.toDouble(this.ribasso);
	}

	public Double getValoreAumento() {
		return this.toDouble(this.aumento);
	}

	public Double getValoreConfermaImporto() {
		return this.toDouble(this.confermaImporto);
	}
	
	public Double getValoreConfermaRibasso() {
		return this.toDouble(this.confermaRibasso);
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

	public Long getMaxNumeroDecimali() {
		return maxNumeroDecimali;
	}

	public void setMaxNumeroDecimali(Long maxNumeroDecimali) {
		this.maxNumeroDecimali = maxNumeroDecimali;
	}	
		
	/**
	 * ...
	 */
	public static boolean isUserLogged(EncodedDataAction action) { 
		UserDetails userDetails = action.getCurrentUser();
		return (null != userDetails
		        && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME));
	}
	
	/**
	 * Visualiza l'elenco dei rilanci effettuati in un'asta
	 *
	 * @return Il codice del risultato dell'azione
	 */
	@SkipValidation
	public String listRilanci() {
		this.setTarget(SUCCESS);
		
		try {
			if (isUserLogged(this)) {
				String codiceGara = (!StringUtils.isEmpty(this.codiceLotto) 
		 	 			 			 ? this.codiceLotto : this.codice);
				
				DettaglioAstaType asta = this.asteManager
					.getDettaglioAsta(codiceGara, this.getCurrentUser().getUsername());
				this.setDettaglioAsta(asta);
					
//				String numeroFase = 
//					(this.getFase() != null ? this.getFase() : asta.getFase().toString());
				
				List<DettaglioRilancioType> rilanci = this.asteManager
					.getElencoRilanci(this.codice, this.codiceLotto, this.getCurrentUser().getUsername(), null);				
				this.setListaRilanci(rilanci);
				
				this.setMaxNumeroDecimali(RilancioAction.getMaxNumeroDecimali(asta, bandiManager));
				
//				// se l'asta è ancora aperta e se esiste solo l'offerta iniziale 
//				// allora inserisci il primo rilancio...
//				if(asta.isAttiva()) {
//					if(rilanci != null && rilanci.size() == 1) {
//						this.setTarget("openRilanci");
//					}
//				}
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
	@SkipValidation
	public String view() {
		this.setTarget(SUCCESS);
		
		if(isUserLogged(this)) {
			try {
				boolean continua = true;
				
				String codiceGara = (!StringUtils.isEmpty(this.codiceLotto) 
		 	 			 			 ? this.codiceLotto : this.codice);
	
				DettaglioAstaType asta = this.asteManager
					.getDettaglioAsta(codiceGara, this.getCurrentUser().getUsername());
				this.setDettaglioAsta(asta);

				this.tipoOfferta = asta.getTipoOfferta();
					
				// verifica se il l'asta è ancora attiva...
				Date dataAttuale = this.ntpManager.getNtpDate();
				Date dataChiusura = (dettaglioAsta.getDataOraChiusura() != null 
						? dettaglioAsta.getDataOraChiusura().getTime() : null);

				if (dataAttuale.compareTo(dataChiusura) > 0) {
					if(dettaglioAsta.getTipoOfferta() == 1) {
						this.addActionError(this.getText("Errors.rilanci.ribasso.oltreDataChiusura"));
					} else if(dettaglioAsta.getTipoOfferta() == 2) {
						this.addActionError(this.getText("Errors.rilanci.importo.oltreDataChiusura"));
					}
					this.setTarget("annullaOpenRilancio");
					continua = false;
				}
				
				if(continua) {
					// recupera i rilanci dell'utente...
					// se non c'è almento il rilancio iniziale inserito dal BO
					// allora l'utente non può effettuare rilanci per l'asta
					List<DettaglioRilancioType> rilanci = this.asteManager
						.getElencoRilanci(this.codice, this.codiceLotto, this.getCurrentUser().getUsername(), asta.getFase().toString());				
					if(rilanci != null && rilanci.size() > 0) {
						this.setListaRilanci(rilanci);
					}
					
					// offerta a ribasso (in percentuale)
					// leggo il numero di decimali ammessi da tabella
					this.setMaxNumeroDecimali(RilancioAction.getMaxNumeroDecimali(asta, bandiManager));
	
					// recupera i prezzi unitari se esistono...
					// e prepara il rilancio
					List<VoceDettaglioAstaType> vociDettaglio = (List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);
					if(vociDettaglio != null && vociDettaglio.size() > 0) {
						//this.totalePrezziUnitari = RilancioPrezziUnitariAction.getTotalePrezziUnitari(vociDettaglio);
						if(asta.getTipoOfferta() == 2) {
							// importo
							this.totalePrezziUnitari = RilancioPrezziUnitariAction.getTotalePrezziUnitari(vociDettaglio);
							double offerta = RilancioPrezziUnitariAction.getTotaleOfferta(this.getCurrentUser().getUsername(), codiceGara, vociDettaglio);
							BigDecimal bdOfferta = BigDecimal.valueOf(new Double(offerta)).setScale(this.maxNumeroDecimali.intValue(), BigDecimal.ROUND_HALF_UP);
							this.importoOfferto = bdOfferta.toPlainString();
							this.confermaImporto = bdOfferta.toPlainString();
						}
					}
				}
				
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
	 * Esegue l'operazione di rilancio in un'asta
	 *
	 * @return Il codice del risultato dell'azione
	 * 
	 * @throws UnknownHostException 
	 * @throws SocketTimeoutException 
	 */
	public String rilancio() throws SocketTimeoutException, UnknownHostException {
		this.setTarget(SUCCESS);

		if (isUserLogged(this)) {
			boolean rilancioOk = false;
			String detailMessage = "";
			try {
				// valida l'importo del rilancio...
				//  - importo = importo di conferma
				//  - data del rilancio < data ora fine asta
				//  - scarto/rilancio minimo deve essere maggiore o uguale al limite minimo 
				//  - scarto/rilancio massimo deve essere minore o uguale al limite massimo
				Date dataAttuale = this.ntpManager.getNtpDate();
				
				boolean continua = true;
				
				String codiceGara = (!StringUtils.isEmpty(this.codiceLotto) 
			 		 	 			 ? this.codiceLotto : this.codice);
				
				DettaglioAstaType asta = this.asteManager
					.getDettaglioAsta(codiceGara, this.getCurrentUser().getUsername());
				if(asta == null) {
					this.addActionError(this.getText("Errors.rilanci.generico"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					detailMessage += "Dettaglio asta non trovato\n";
					continua = false;
				} 
				
				this.setMaxNumeroDecimali(RilancioAction.getMaxNumeroDecimali(asta, bandiManager));
				
				double rilancio = 0;
				double confermaRilancio = 0;
				if(asta.getTipoOfferta() == 1) {
					// ribasso
					rilancio = toDouble(this.ribasso);
					confermaRilancio = toDouble(this.confermaRibasso);
				} else {
					// importo
					rilancio = toDouble(this.importoOfferto);
					confermaRilancio = toDouble(this.confermaImporto);
				}
				
				// nel caso di rilancio a prezzi unitari, 
				// recupera dalla sessione l'elenco dei prezzi unitari...
				List<VoceDettaglioAstaType> voci = null;
				
				List<VoceDettaglioAstaType> soggette = 
					(List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);
				List<VoceDettaglioAstaType> nonSoggette = 
					(List<VoceDettaglioAstaType>) this.session
						.get(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE);
				
				if(soggette != null || nonSoggette != null) {
					// prepara un elenco di tutte le voci del rilancio...
					voci = new ArrayList<VoceDettaglioAstaType>();
					for(int i = 0; i < soggette.size(); i++) {
						voci.add(soggette.get(i));
					}
					for(int i = 0; i < nonSoggette.size(); i++) {
						voci.add(nonSoggette.get(i));
					}
				}
				if(voci != null && voci.size() <= 0) {
					voci = null;
				}
				
				// valida il rilancio...
				if(continua) {
					continua = this.validaRilancio(
							rilancio, 
							confermaRilancio,
							dataAttuale,
							this.maxNumeroDecimali,
							voci,
							asta);
					if(!continua) {
						this.getParameters().put("confermaImporto", null);
						this.getParameters().put("confermaRibasso", null);
						detailMessage += "Validazione campi non riuscita\n";
					} 
				}
				
				// invia il nuovo rilancio dell'utente...
				if(continua) {
					// predisponi il valore del "offerta" per l'inserimento 
					// del nuovo rilancio...
					if(asta.getTipoOfferta() == 1) {		// ribasso
						rilancio = -1 * rilancio;
					}
					if (asta.getTipoOfferta() == 2) {		// importo
					}

					// applica i decimali al rilancio
					BigDecimal bdRilancio = BigDecimal.valueOf(new Double(rilancio)).setScale(maxNumeroDecimali.intValue(), BigDecimal.ROUND_HALF_UP);

					Long idRilancio = this.asteManager.insertRilancioAsta(
							codice, 
							codiceLotto,
							this.getCurrentUser().getUsername(), 
							bdRilancio.doubleValue(),
							voci);
					
					rilancioOk = (idRilancio != null && idRilancio.longValue() > 0);
					if(rilancioOk) {
						this.setTarget(SUCCESS);
					} else {
						detailMessage += "Rilancio non inserito\n";
					}
				}
			} catch (Throwable t) {
				this.addActionError(t.getMessage());
				ApsSystemUtils.logThrowable(t, this, "rilancio");
				ExceptionUtils.manageExceptionError(t, this);
				detailMessage += t.getMessage();
				this.setTarget(INPUT);
			}
			
			// inserisco l'evento per il rilancio...
			this.writeEventoRilancio( 
					PortGareEventsConstants.RILANCIO_ASTA, 
					this.getCurrentUser().getUsername(), 
					this.getCurrentUser().getIpAddress(), 
					detailMessage, 
					rilancioOk, 
					this.codice, 
					this.codiceLotto);
			
			// pulisci la sessione...
			if(rilancioOk) {
				this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI);
			}
		}
		
		return this.getTarget();
	}

	/**
	 * Annulla la richiesta d'inserimento di un rilancio d'asta
	 *
	 * @return Il codice del risultato dell'azione
	 */
	public String cancel() {
		//this.setTarget(SUCCESS);
		this.setTarget("successCancel");
		this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI);
		return this.getTarget();
	}
	
	/**
	 * Ritorna alla gestione dei prezzi unitari
	 *
	 * @return Il codice del risultato dell'azione
	 */
	public String back() {
		//this.setTarget(SUCCESS);
		this.setTarget("successBack");
		return this.getTarget();
	}

	/**
	 * validazione del rilancio
	 * @throws ApsException 
	 */	
	private boolean validaRilancio(
			double offerta, 
			double confermaOfferta, 
			Date dataAttuale,
			Long maxNumeroDecimali,
			List<VoceDettaglioAstaType> voci,
			DettaglioAstaType dettaglioAsta) throws ApsException 
	{
		boolean continua = true;
				
		Date dataChiusura = (dettaglioAsta.getDataOraChiusura() != null 
							 ? dettaglioAsta.getDataOraChiusura().getTime() 
							 : null);
	
		boolean offertaRibasso = (dettaglioAsta.getTipoOfferta() == 1);
		boolean offertaImporto = (dettaglioAsta.getTipoOfferta() == 2);
		
		// prepara il prefisso per gli eventuali errori
		String errorPrefix = "";
		if(offertaRibasso) {
			errorPrefix = "Errors.rilanci.ribasso";
		} else if(offertaImporto) {
			errorPrefix = "Errors.rilanci.importo";
		}
		
		// converti in big decimal offerta e offerta ribasso
		BigDecimal bdOfferta = BigDecimal.valueOf(new Double(offerta)).setScale(maxNumeroDecimali.intValue(), BigDecimal.ROUND_HALF_UP);
		
		// verifica i decimali..
		if(offertaRibasso || offertaImporto) {
			int numeroDecimali = 0;
			try {
				String s = bdOfferta.toPlainString();
				numeroDecimali = s.substring(s.indexOf(".") + 1).length();
			} catch(Exception e) {
			}
			if (numeroDecimali > maxNumeroDecimali) {
				this.addActionError(
						this.getText(errorPrefix + ".tooManyDecimals", 
						new String[] {"Nuovo valore offerto", Long.toString(maxNumeroDecimali)}));
				this.setTarget(INPUT);
				continua = false;
			}
		}

		// verifica che il totale dei prezzi unitari sia uguale a quello
		// del rilancio...
		if(continua) {
			if(offertaImporto) {
				if(voci != null) {
					String codiceGara = (!StringUtils.isEmpty(this.codiceLotto) 
		 			 		 		     ? this.codiceLotto : this.codice);
						
					double tot = RilancioPrezziUnitariAction
						.getTotaleOfferta(this.getCurrentUser().getUsername(), codiceGara, voci);
					BigDecimal bdTot = BigDecimal.valueOf(new Double(tot)).setScale(maxNumeroDecimali.intValue(), BigDecimal.ROUND_HALF_UP);
					
					if(bdTot.doubleValue() != bdOfferta.doubleValue()) {
						continua = false;
						this.addActionError(this.getText("Errors.rilanci.importo.diversoTotalePrzUni",
														 new String[] {bdTot.toPlainString()}));
						this.setTarget(INPUT);
					}
				}
			}
		}
		
		// calcola lo scarto...
		if(continua) {
			double scarto = 0;
		
			if(offertaRibasso) {
				// ribasso percentuale 
				// a video sempre in valore assoluto quindi senza segno   
				if(dettaglioAsta.getRibassoUltimoRilancio() != null) {
					// -11.2, -12.5, -13.1 ... 
					scarto = Math.abs(offerta) - Math.abs(dettaglioAsta.getRibassoUltimoRilancio());
				}
				if(scarto <= 0) {
					this.addActionError(this.getText("Errors.rilanci.ribasso.invalid"));
					this.setTarget(INPUT);
					continua = false;
				}
			} else if(offertaImporto) {
				// importo
				if(dettaglioAsta.getImportoUltimoRilancio() != null) {
					// 15000.0, 12000.0, 11500.0, ...
					scarto = dettaglioAsta.getImportoUltimoRilancio() - offerta;
				}
				if(scarto <= 0 || offerta <= 0) {
					this.addActionError(this.getText("Errors.rilanci.offerta.invalid"));
					this.setTarget(INPUT);
					continua = false;
				}
			}
			
			if(offerta != confermaOfferta) {
				this.addActionError(this.getText(errorPrefix + ".offertaNonConfermata"));
				this.setTarget(INPUT);
				continua = false;
			}
			if (dataAttuale.compareTo(dataChiusura) > 0) {
				this.addActionError(this.getText(errorPrefix + ".oltreDataChiusura"));
				this.setTarget(INPUT);
				continua = false;
			}
			if (dettaglioAsta.getScartoRilancioMinimo() != null && scarto < dettaglioAsta.getScartoRilancioMinimo()) {
				this.addActionError(
						this.getText(errorPrefix + ".sottoScartoMinimo", 
						new String[] {dettaglioAsta.getScartoRilancioMinimo().toString()}));
				this.setTarget(INPUT);
				continua = false;
			}
			if (dettaglioAsta.getScartoRilancioMassimo() != null  && scarto > dettaglioAsta.getScartoRilancioMassimo()) {
				this.addActionError(
						this.getText(errorPrefix + ".sopraScartoMassimo", 
						new String[] {dettaglioAsta.getScartoRilancioMassimo().toString()}));
				this.setTarget(INPUT);
				continua = false;
			}
			
			if(INPUT.compareTo(this.getTarget()) == 0) {
				if(dettaglioAsta.getTipoOfferta() == 1) {
					this.ribasso = bdOfferta.toPlainString();
				} else { 
					this.importoOfferto = bdOfferta.toPlainString();
				}
			}
		}
		
		return continua;
	}

	/**
	 * registra un evento per il rilancio effettuato
	 */
	private void writeEventoRilancio(
			String tipoEvento, 
			String username, 
			String ipAddress, 
			String messaggioDettaglio,
			boolean rilancioBuonfine, 
			String codice, 
			String codiceLotto) 
	{
		try {
			Event evento = new Event();
			evento.setUsername(username);
			evento.setIpAddress(ipAddress);
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setDestination(codiceLotto != null && !codiceLotto .isEmpty() ? codiceLotto : codice);					
			evento.setLevel(rilancioBuonfine ? Event.Level.INFO : Event.Level.ERROR);
			evento.setEventType(tipoEvento);
			evento.setMessage("Rilancio asta elettronica");
			
			String msg = "";
			if(!rilancioBuonfine) {
				msg = "Rilancio non valido per la gara " + codice;
				if(codiceLotto != null && !codiceLotto .isEmpty()) {
					msg = msg + " e il lotto " + codiceLotto;
				}
			} else {
				msg = "Rilancio valido per la gara " + codice;
				if(codiceLotto != null && !codiceLotto .isEmpty()) {
					msg = msg + " e il lotto " + codiceLotto;
				}
			}
//			evento.setMessage(msg);
//			
//			if(!rilancioBuonfine && messaggioDettaglio != null) {
//				evento.setDetailMessage(messaggioDettaglio);
//			}
			
			if(messaggioDettaglio != null && !messaggioDettaglio.isEmpty()) {
				msg = msg + "\n\n" + messaggioDettaglio;
			}
			evento.setDetailMessage(msg);
			
			this.eventManager.insertEvent(evento);
		} catch(Throwable e) {
			// da tracciare ???
		}
	}

	/**
	 * restituisce il massimo numero di decimali da utilizzare per la 
	 * visualizzazione di importi/ribassi 
	 */
	public static Long getMaxNumeroDecimali(
			DettaglioAstaType asta, IBandiManager bandiManager) {
		Long n = null;
		try {
			if(asta.getNumDecimaliRibasso() != null) {
				n = asta.getNumDecimaliRibasso();
			} else {
				if(asta.getTipoOfferta() != null && asta.getTipoOfferta() == 1) {
					n = bandiManager.getNumeroDecimaliRibasso();
				} else {
					n = new Long(5);
				}
			}
		} catch (Throwable e) {
			// da tracciare ???
		}
		return n;
	} 
	
	/**
	 * converti una stringa in double  
	 */
	private double toDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch(Throwable e) {
			return 0;
		}
	}

}
