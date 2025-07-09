package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.RichiesteRettificaList;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper.FasiRettifica;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IStipuleManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DettaglioComunicazioniAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 8405860693208849965L;
	
	private Map<String, Object> session;
	private INtpManager ntpManager;
	private IAppParamManager appParamManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IContrattiManager contrattiManager;
	private IAvvisiManager avvisiManager;
	private IStipuleManager stipuleManager;
	
	@Validate(EParamValidation.ENTITA)
	private String entita;
	private Long idComunicazione;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codice2;
	private ComunicazioneType comunicazione;
	private Long genere;
	private boolean inviata;
	@Validate(EParamValidation.GENERIC)
	private String tipo;
	private int pagina;
	private boolean dettaglioPresente;
	@Validate(EParamValidation.GENERIC)
	private String fromBando;
	@Validate(EParamValidation.GENERIC)
	private String fromAvviso;
	@Validate(EParamValidation.GENERIC)
	private String fromStipula;
	private boolean abilitaProcedura;
	private Long idDestinatario;
	@Validate(EParamValidation.GENERIC)
	private String mittente;
	private Boolean soccorsoIstruttorio;			// TRUE in caso di soccorso istruttorio
	private boolean soccorsoScaduto;				// TRUE in caso di soccorso istruttorio scaduto 
	private boolean rettifica;						// TRUE in di richiesta di rettifica	
	@Validate(EParamValidation.GENERIC)
	private String applicativo;

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setContrattiManager(IContrattiManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}
	
	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}

	public void setStipuleManager(IStipuleManager stipuleManager) {
		this.stipuleManager = stipuleManager;
	}

	public boolean isDettaglioPresente() {
		return dettaglioPresente;
	}

	public void setDettaglioPresente(boolean dettaglioPresente) {
		this.dettaglioPresente = dettaglioPresente;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
	
	public long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = StringUtils.stripToNull(codice);
	}

	public String getCodice2() {
		return codice2;
	}

	public void setCodice2(String codice2) {
		this.codice2 = codice2;
	}

	public String getEntita() {
		return entita;
	}

	public void setEntita(String entita) {
		this.entita = entita;
	}

	public ComunicazioneType getComunicazione() {
		return comunicazione;
	}

	public Long getGenere() {
		return genere;
	}
	
	public boolean isInviata() {
		return inviata;
	}

	public void setInviata(boolean inviata) {
		this.inviata = inviata;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
		
	public String getFromBando() {
		return fromBando;
	}

	public void setFromBando(String fromBando) {
		this.fromBando = fromBando;
	}		
	
	public String getFromAvviso() {
		return fromAvviso;
	}

	public void setFromAvviso(String fromAvviso) {
		this.fromAvviso = fromAvviso;
	}

	public String getFromStipula() {
		return fromStipula;
	}

	public void setFromStipula(String fromStipula) {
		this.fromStipula = fromStipula;
	}

	public boolean isAbilitaProcedura() {
		return abilitaProcedura;
	}

	public void setAbilitaProcedura(boolean abilitaProcedura) {
		this.abilitaProcedura = abilitaProcedura;
	}
	
	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	
	public Boolean getSoccorsoIstruttorio() {
		return soccorsoIstruttorio;
	}

	public void setSoccorsoIstruttorio(Boolean soccorsoIstruttorio) {
		this.soccorsoIstruttorio = soccorsoIstruttorio;
	}
	
	public boolean isSoccorsoScaduto() {
		return soccorsoScaduto;
	}

	public void setSoccorsoScaduto(boolean soccorsoScaduto) {
		this.soccorsoScaduto = soccorsoScaduto;
	}

	public boolean isRettifica() {
		return rettifica;
	}

	public void setRettifica(boolean rettifica) {
		this.rettifica = rettifica;
	}

	public String getApplicativo() {
		return applicativo;
	}

	public void setApplicativo(String applicativo) {
		this.applicativo = applicativo;
	}

	/**
	 * espone alla jsp le costanti BUSTA_ECONOMICA, BUSTA_TECNICA  
	 */
	public int getBUSTA_TECNICA() { 
		return PortGareSystemConstants.BUSTA_TECNICA; 
	}
	
	public int getBUSTA_ECONOMICA() { 
		return PortGareSystemConstants.BUSTA_ECONOMICA; 
	}
	
	
	/**
	 * Inizializza il dettaglio di una comunicazione inviata/ricevuta
	 */
	private void initDettaglioComunicazione() {
		if("1".equals(this.fromStipula)){
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
			this.session.remove("dettaglioPresente");
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			this.entita = null;
		} else {
			this.entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
		}
		
		if(("1".equals(this.fromBando) || "1".equals(this.fromAvviso)) && this.entita == null) {
			/// fromBando = "1" se si arriva da dettaglio bando
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
			this.session.remove("dettaglioPresente");
		} 
		
		// inizializza lo stato per la visualizzazione del tipo dettaglio
		this.setInviata(false);
		this.setSoccorsoIstruttorio(false);
		this.setSoccorsoScaduto(false);
		this.setRettifica(false);
	}
	
	/**
	 * Recupera il dettaglio della comunicazione ricevuta
	 */
	public String openPageRicevute() {
		try {
			this.initDettaglioComunicazione();
			
			this.setInviata(false);
			
			this.codice = (String)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.pagina = (Integer) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA);
			this.tipo = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO);
			this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA); //per discriminare se tornare ad area personale o a procedura			
			this.entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);

			this.comunicazione = this.bandiManager.getComunicazioneRicevuta(
					this.getCurrentUser().getUsername(), 
					this.idComunicazione,
					this.idDestinatario,
					this.applicativo);
			
			this.codice2 = this.comunicazione.getCodice2();
			if(this.entita == null && comunicazione.getEntita() != null){
				this.entita = comunicazione.getEntita();
			}
			
			//In caso di comunicazione ricevuta o archiviata aggiorno la data di lettura del messaggio
			if (this.comunicazione.getDataLettura() == null) {//this.comunicazione.setDataLettura(Calendar.getInstance())
				Long idDestinatario = this.comunicazione.getIdDestinatario();
				
				this.comunicazioniManager.updateDataLetturaDestinatarioComunicazione(
						this.applicativo, 
						this.idComunicazione, 
						idDestinatario);
				this.comunicazione = this.bandiManager.getComunicazioneRicevuta(
						this.getCurrentUser().getUsername(), 
						this.idComunicazione,
						idDestinatario,
						this.applicativo);
			}

			//controllo se il dettaglio e' consultabile per la procedura
			this.existsDettaglio();
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageRicevute");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Recupera il dettaglio della comunicazione inviata
	 */
	public String openPageInviate() {
		try {
			this.initDettaglioComunicazione();
			
			this.setInviata(true);
			String idprg = "PA";
			this.comunicazione = this.bandiManager.getComunicazioneInviata(this.getCurrentUser().getUsername(), this.idComunicazione, idprg);
			if(this.entita == null && comunicazione != null){
				this.entita = comunicazione.getEntita();//comunicazione = new ComunicazioneType();
			}
			this.codice = this.comunicazione.getCodice();
			this.codice2 = this.comunicazione.getCodice2();
			this.pagina = ((Integer) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA)).intValue();
			this.tipo = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO);
			//this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA); //per discriminare se tornare ad area personale o a procedura
			this.mittente = this.comunicazione.getDestinatario();	//this.getMittente(this.comunicazione);
			
			//controllo se il dettaglio e' consultabile per la procedura
			this.existsDettaglio();
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageInviate");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Recupera il dettaglio del soccorso istruttorio
	 */
	public String openPageSoccorsoIstruttorio() {
		String nomeOperazioneLog = this.getI18nLabelFromDefaultLocale("LABEL_COMUNICAZIONI_INVIA_COMUNICAZIONE").toLowerCase();
		String nomeOperazione = this.getI18nLabel("LABEL_COMUNICAZIONI_INVIA_COMUNICAZIONE").toLowerCase();		
		try {
			this.initDettaglioComunicazione();
			
			this.setSoccorsoIstruttorio(true);
			this.setSoccorsoScaduto(true);
			
			this.comunicazione = this.bandiManager.getComunicazioneRicevuta(
					this.getCurrentUser().getUsername(), 
					this.idComunicazione,
					this.idDestinatario,
					this.applicativo);
			
			Date dataOra = this.ntpManager.getNtpDate();
			Date dataScadenza = this.calcolaDataOra(this.comunicazione.getDataScadenza(), this.comunicazione.getOraScadenza());
			if(dataOra != null && dataScadenza != null) {
				this.setSoccorsoScaduto( dataOra.after(dataScadenza) );
			}
			
			this.codice = (String)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.pagina = ((Integer) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA)).intValue();
			this.tipo = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO);
			this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA); //per discriminare se tornare ad area personale o a procedura
			this.mittente = this.comunicazione.getDestinatario();	//this.getMittente(this.comunicazione);
			
			// In caso di comunicazione ricevuta o archiviata aggiorno la data di lettura del messaggio
			if (this.comunicazione.getDataLettura() == null) {
				Long idDestinatario = this.comunicazione.getIdDestinatario();
				this.comunicazioniManager.updateDataLetturaDestinatarioComunicazione(
						CommonSystemConstants.ID_APPLICATIVO_GARE, 
						this.idComunicazione, 
						idDestinatario);
				this.comunicazione = this.bandiManager.getComunicazioneRicevuta(
						this.getCurrentUser().getUsername(), 
						this.idComunicazione,
						idDestinatario, 
						this.applicativo);
			}

			// controllo se il dettaglio e' consultabile per la procedura
			this.existsDettaglio();
			
		} catch (SocketTimeoutException e) {
			ApsSystemUtils.logThrowable(e, this, "openPageSoccorsoIstruttorio", this
					.getTextFromDefaultLocale("Errors.ntpTimeout", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpTimeout", new String[] { nomeOperazione }));
		} catch (UnknownHostException e) {
			ApsSystemUtils.logThrowable(e, this, "openPageSoccorsoIstruttorio", this
					.getTextFromDefaultLocale("Errors.ntpUnknownHost", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione }));
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "openPageSoccorsoIstruttorio", this
					.getTextFromDefaultLocale("Errors.ntpUnexpectedError", nomeOperazioneLog));
			this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPageSoccorsoIstruttorio");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Ritorna l'indicazione se il portale risulta configurato per la
	 * protocollazione via servizio con attribuzione di data e numero
	 * protocollo.
	 * 
	 * @return true se la protocollazione mediante servizio risulta attiva,
	 *         false altrimenti
	 */
	public boolean isServizioProtocollazione() {
		Integer tipoProtocollazione = (Integer) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_TIPO);
		if (tipoProtocollazione == null) {
			tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
		}
		return tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM;
	}

	/**
	 * Controllo se il dettaglio e' consultabile per la procedura
	 */
	private void existsDettaglio() throws ApsException {
		DettaglioGaraType dettaglioGara = null; 
		this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
		if (this.genere == null) {
			//codice null se si arriva da area personale
			if(this.codice == null) {
				this.codice = this.comunicazione.getCodice();
			}
			this.genere = this.bandiManager.getGenere(this.codice);
		}
		if(this.genere == null && "G1STIPULA".equals(this.comunicazione.getEntita())) {
			boolean dettPresente = (this.stipuleManager.getDettaglioStipulaContratto(this.codice, this.getCurrentUser().getUsername(), true) != null);
			this.setDettaglioPresente(dettPresente);
		} else if(this.genere == null && "APPA".equals(this.comunicazione.getEntita())) {
			this.setDettaglioPresente(true);
		}
		else if(this.genere != null) {
			if(this.genere == 10) {
				// ELENCO
				this.setDettaglioPresente(this.bandiManager.getDettaglioBandoIscrizione(this.codice) != null);
			} else if(this.genere == 20) {
				// CATALOGO
				this.setDettaglioPresente(this.bandiManager.getDettaglioBandoIscrizione(this.codice) != null);
			} else if(this.genere == 4) {
				// ODA
				this.setDettaglioPresente(this.contrattiManager.getDettaglioContratto(this.codice) != null);
			} else if(this.genere == 11) {
				// AVVISI
				this.setDettaglioPresente(this.avvisiManager.getDettaglioAvviso(this.codice) != null);
			} else if(genere == 100) {
				// LOTTO DI GARA
				dettaglioGara = this.bandiManager.getDettaglioGaraFromLotto(this.comunicazione.getCodice());
				this.setDettaglioPresente(dettaglioGara != null);
				if (this.dettaglioPresente) {
					this.codice = dettaglioGara.getDatiGeneraliGara().getCodice();
				}
			} else {
				// GARA
				dettaglioGara = this.bandiManager.getDettaglioGara(this.comunicazione.getCodice());
				this.setDettaglioPresente(dettaglioGara != null);
			}
		}
		// abilita il pulsante "vai a procedura"...
		this.abilitaProcedura = ( this.dettaglioPresente && 
				   			  	  this.codice != null && 
				   			  	  this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA) == null );

		// per le gare verifica se ci sono delle richieste di rettifica in corso...
		this.rettifica = false;
		if(dettaglioGara != null && WizardRettificaHelper.isModelloRettifica(comunicazione.getModello())) {
			DettaglioComunicazioneType richiesta = RichiesteRettificaList.getRichiestaRettificaInCorso(getCurrentUser().getUsername(), comunicazione);
			this.rettifica = (richiesta != null);  
		}
		
		// aggiorna i parametri in sessione...
		//this.session.remove(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
		if(this.abilitaProcedura) {
			if(this.soccorsoIstruttorio) {
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, "openPageDettaglioSoccorsoIstruttorio");
			} if(this.inviata) {
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, "openPageDettaglioComunicazioneInviata");
			} else {
				this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, "openPageDettaglioComunicazioneRicevuta");
			}
		}
		if(this.inviata) {
			// comunicazione inviata...
			//??? anche in questo caso di fa => this.session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, this.dettaglioPresente);
		} else {
			// comunicazione ricevuta...
			this.session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, this.dettaglioPresente);
		}
	}

	/**
	 * ... 
	 */
	private Date calcolaDataOra(Calendar data, String ora) {
		Date risultato = null;
		if (data != null && data.getTime() != null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(data.getTime());
			int ore = 24;
			int minuti = 0;
			if (ora != null && !"".equals(ora)) {
				ore = Integer.parseInt(ora.substring(0, ora.indexOf(':')));
				minuti = Integer.parseInt(ora.substring(ora.indexOf(':') + 1));
			}
			gc.set(Calendar.HOUR_OF_DAY, ore);
			gc.set(Calendar.MINUTE, minuti);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			risultato = gc.getTime();
		}
		return risultato;
	}
	
	/**
	 * restituisce il nome del mittente di una comunicazione o dal profilo utente 
	 */
	private String getMittente(ComunicazioneType comunicazione) {
		String mittente = null;
		if(StringUtils.isNotEmpty(comunicazione.getMittente())) {
			mittente = comunicazione.getMittente();
		} else {
			UserProfile profilo = (UserProfile) this.getCurrentUser().getProfile();
			if(profilo != null) {
				mittente = (String) profilo.getValue(profilo.getFirstNameAttributeName());
			}
		}
		return mittente;
	}

}
