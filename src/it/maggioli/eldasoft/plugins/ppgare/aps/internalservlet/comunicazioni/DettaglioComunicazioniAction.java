package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile;

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
	
	private Long idComunicazione;
	private String codice;
	private ComunicazioneType comunicazione;
	private Long genere;
	private boolean inviata;
	private String tipo;
	private int pagina;
	private boolean dettaglioPresente;
	private String fromBando;
	private String fromAvviso;
	private boolean abilitaProcedura;
	private Long idDestinatario;
	private String mittente;
	private Boolean soccorsoIstruttorio;
	private boolean soccorsoScaduto;		// TRUE in caso di soccorso istruttorio scaduto 
	

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

	/**
	 * Inizializza il dettaglio di una comunicazione inviata/ricevuta
	 */
	private void initDettaglioComunicazione() {
		if("1".equals(this.fromBando) || "1".equals(this.fromAvviso)) {
			/// fromBando = "1" se si arriva da dettaglio bando
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
			this.session.remove("dettaglioPresente");
		} 
		
		// inizializza lo stato per la visualizzazione del tipo dettaglio
		this.setInviata(false);
		this.setSoccorsoIstruttorio(false);
		this.setSoccorsoScaduto(false);
	}
	
	/**
	 * Recupera il dettaglio della comunicazione ricevuta
	 */
	public String openPageRicevute() {
		try {
			this.initDettaglioComunicazione();
			
			this.setInviata(false);
			
			this.comunicazione = this.bandiManager.getComunicazioneRicevuta(
					this.getCurrentUser().getUsername(), 
					this.idComunicazione,
					this.idDestinatario);
			
			this.codice = (String)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.pagina = ((Integer) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA)).intValue();
			this.tipo = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO);
			this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA); //per discriminare se tornare ad area personale o a procedura			
			
			//In caso di comunicazione ricevuta o archiviata aggiorno la data di lettura del messaggio
			if (this.comunicazione.getDataLettura() == null) {
				Long idDestinatario = this.comunicazione.getIdDestinatario();
				this.comunicazioniManager.updateDataLetturaDestinatarioComunicazione(
						CommonSystemConstants.ID_APPLICATIVO_GARE, 
						this.idComunicazione, 
						idDestinatario);
				this.comunicazione = this.bandiManager.getComunicazioneRicevuta(
						this.getCurrentUser().getUsername(), 
						this.idComunicazione,
						idDestinatario);
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
			
			this.comunicazione = this.bandiManager.getComunicazioneInviata(this.getCurrentUser().getUsername(), this.idComunicazione);
			this.codice = this.comunicazione.getCodice();
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
					this.idDestinatario);
			
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
						idDestinatario);
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
			
		this.genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
		if (this.genere == null) {
			//codice null se si arriva da area personale
			if(this.codice == null){
				this.codice = this.comunicazione.getCodice();
			}
			this.genere = this.bandiManager.getGenere(this.codice);
		}
		
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
			DettaglioGaraType dettaglioGaraFromLotto = this.bandiManager.getDettaglioGaraFromLotto(this.comunicazione.getCodice());
			this.codice = dettaglioGaraFromLotto.getDatiGeneraliGara().getCodice();
			this.setDettaglioPresente(dettaglioGaraFromLotto != null);
		} else {
			// GARA
			this.setDettaglioPresente(this.bandiManager.getDettaglioGara(this.comunicazione.getCodice()) != null);
		}
		
		// abilita il pulsante "vai a procedura"...
		this.abilitaProcedura = ( this.dettaglioPresente && 
				   			  	  this.codice != null && 
				   			  	  this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA) == null );

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
