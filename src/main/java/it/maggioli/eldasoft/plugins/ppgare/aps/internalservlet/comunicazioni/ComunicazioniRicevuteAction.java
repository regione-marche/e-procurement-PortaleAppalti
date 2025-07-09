package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;


public class ComunicazioniRicevuteAction extends EncodedDataAction implements ModelDriven<ComunicazioniSearchBean>, SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1479643031996378000L;

	// tipi di comunicaizioni (ricevuta, archiviate, soccorsi) 
	private static final String COMUNICAZIONI_RICEVUTE 		= "ricevute";
	private static final String COMUNICAZIONI_ARCHIVIATE	= "archiviate";
	private static final String COMUNICAZIONI_SOCCORSI 		= "soccorsiIstruttori";
	
	private static final int MAX_NUM_RECORD = 10;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;
	@Validate(EParamValidation.CODICE)
	private String comunicazioniCodiceProcedura;
	@Validate(EParamValidation.CODICE)
	private String codice2;
	private Long comunicazioniGenere;
	private SearchResult<ComunicazioneType> comunicazioni;
	@Validate(EParamValidation.TIPO_COMUNICAZIONE)
	private String tipoComunicazione;
	@Validate
	private ComunicazioniSearchBean model = new ComunicazioniSearchBean();
	private int pagina;
	@Validate(EParamValidation.ACTION)
	private String actionName;
	@Validate(EParamValidation.ACTION)
	private String namespace;
	private Long genere;
	@Validate(EParamValidation.ENTITA)
	private String entita;	
	
	// attributi per la navigaione
	private Long idComunicazione;
	private Long idDestinatario;
	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	@Override
	public ComunicazioniSearchBean getModel() {
		return this.model;
	}
	
	public String getEntita() {
		return entita;
	}

	public void setEntita(String entita) {
		this.entita = entita;
	}

	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}

	public String getComunicazioniCodiceProcedura() {
		return comunicazioniCodiceProcedura;
	}

	public void setComunicazioniCodiceProcedura(String comunicazioneCodiceProcedura) {
		this.comunicazioniCodiceProcedura = comunicazioneCodiceProcedura;
	}
	
	public String getCodice2() {
		return codice2;
	}

	public void setCodice2(String codice2) {
		this.codice2 = codice2;
	}

	public Long getComunicazioniGenere() {
		return comunicazioniGenere;
	}

	public void setComunicazioniGenere(Long comunicazioniGenere) {
		this.comunicazioniGenere = comunicazioniGenere;
	}

	public SearchResult<ComunicazioneType> getComunicazioni() {
		return comunicazioni;
	}

	public void setComunicazioni(SearchResult<ComunicazioneType> comunicazioni) {
		this.comunicazioni = comunicazioni;
	}

	public String getTipoComunicazione() {
		return tipoComunicazione;
	}

	public void setTipoComunicazione(String tipoComunicazione) {
		this.tipoComunicazione = tipoComunicazione;
	}
	
	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}
	
	
	/**
	 * restituisce la lista delle comunicazioni ricevute/archiviate/soccorsi isrtuttori
	 * 
	 * @throws ApsException 
	 */
	private SearchResult<ComunicazioneType> getListaComunicazioni(String tipoComunicazione) 
			throws ApsException 
	{
		this.setTipoComunicazione(tipoComunicazione);
		
		boolean ricevute = COMUNICAZIONI_RICEVUTE.equalsIgnoreCase(tipoComunicazione);
		boolean archiviate = COMUNICAZIONI_ARCHIVIATE.equalsIgnoreCase(tipoComunicazione);
		boolean soccorsiIstruttori = COMUNICAZIONI_SOCCORSI.equalsIgnoreCase(tipoComunicazione);
		boolean riservata =  PortGareSystemConstants.ENTITA_GENERICA_RISERVATA.equalsIgnoreCase(
					(String)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA));
		
		// imposta la pagina corrente da visualizzare
		if(this.getPagina() > 0) {
			this.model.setCurrentPage(this.getPagina());
		}
		
		// imposta l'indice relativo alla prima comunicazione per il gruppo di comunicazioni visualizzabili nella pagina corrente
		int startIndex = (this.model.getCurrentPage() > 0
						  ? MAX_NUM_RECORD * (this.model.getCurrentPage() - 1)
						  : 0);
		
		// recupera dalla sessione "codiceProcedura" (se esiste) 
		String codiceProcedura = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
		if(StringUtils.isNotEmpty(this.comunicazioniCodiceProcedura)) {
			codiceProcedura = this.comunicazioniCodiceProcedura;
		}
		this.comunicazioniCodiceProcedura = codiceProcedura;
		
		// recupera dalla sessione "codice2"
		this.codice2 = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA);
		
		// recupera dalla sessione "genereProcedura" (se esiste)
		Long genereProcedura = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
		if(this.comunicazioniGenere != null && this.comunicazioniGenere.longValue() > 0) {
			genereProcedura = this.comunicazioniGenere;
		}
		this.genere = genereProcedura;

		// recupera la lista delle comunicazioni...
		// (genere = 1 quando area personale -> ricevute -> comunicazione -> vai a procedura -> ricevute)
		boolean garaLotti = (this.genere != null && (this.genere == 100 || this.genere == 1));
		
		SearchResult<ComunicazioneType> comunicazioni = null;
		if( ricevute || soccorsiIstruttori ) {
			// comunicazioni ricevute / soccorsi istruttori
			if(garaLotti) {
				comunicazioni = this.bandiManager.getComunicazioniPersonaliRicevuteGaraLotti(
						this.getCurrentUser().getUsername(),  
						StringUtils.stripToNull(comunicazioniCodiceProcedura),
						soccorsiIstruttori,
						this.entita,
						startIndex, MAX_NUM_RECORD);
			} else {
				comunicazioni = this.bandiManager.getComunicazioniPersonaliRicevute(
						this.getCurrentUser().getUsername(),  
						StringUtils.stripToNull(comunicazioniCodiceProcedura),
						null,
						soccorsiIstruttori,
						this.entita,
						startIndex, MAX_NUM_RECORD);
			}	
		} else if( archiviate ) {
			// comunicazioni archiviate
			if(garaLotti) {
				comunicazioni = this.bandiManager.getComunicazioniPersonaliArchiviateGaraLotti(
						this.getCurrentUser().getUsername(),  
						StringUtils.stripToNull(comunicazioniCodiceProcedura),
						false,
						this.entita,
						startIndex, MAX_NUM_RECORD);
			} else {
				comunicazioni = this.bandiManager.getComunicazioniPersonaliArchiviate(
						this.getCurrentUser().getUsername(),  
						StringUtils.stripToNull(comunicazioniCodiceProcedura),
						null,
						false,
						this.entita,
						startIndex, MAX_NUM_RECORD);
			}
		}
		
		// aggiorna il modello della pagina 
		this.model.processResult(comunicazioni.getNumTotaleRecord(), comunicazioni.getNumTotaleRecordFiltrati());
		// aggiorna la sessione
		this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO, this.tipoComunicazione);
		this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.model.getCurrentPage());
		this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
		this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
		this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
			
		return comunicazioni;
	}
	
	/**
	 * restituisce la lista delle comunicazioni ricevute 
	 */
	public String openPageComunicazioniRicevute() {
		try {
			// in caso si torni dal Dettaglio Comunicazioni premendo il link "Torna alla lista"...
			// i seguenti parametri non vengono passati tra i parametri delle request e response...
			if(StringUtils.isEmpty(this.actionName) && StringUtils.isEmpty(this.namespace)) {
				this.actionName = (String) this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
				this.namespace = (String) this.session.get(ComunicazioniConstants.SESSION_ID_NAMESPACE);
			}
			
			String e = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			this.entita = (PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(e) ? PortGareSystemConstants.ENTITA_CONTRATTO_LFS : null);

			// recupera l'elenco delle comunicazioni
			this.setComunicazioni(getListaComunicazioni(COMUNICAZIONI_RICEVUTE));
			
			if(this.genere != null) {
				this.entita = null;
				this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			}
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageComunicazioniRicevute");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}
	
	/**
	 * restituisce la lista delle comunicazion archivitate
	 */
	public String openPageComunicazioniArchiviate() {
		try {
			// recupera l'elenco delle comunicazioni
			this.setComunicazioni(getListaComunicazioni(COMUNICAZIONI_ARCHIVIATE));
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageComunicazioniArchiviate");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}	

	/**
	 * restiruisce la lista delle comunicazioni di soccorso istruttorio
	 */
	public String openPageSoccorsiIstruttori() {
		try {
			// in caso si torni dal Dettaglio Comunicazioni premendo il link "Torna alla lista"...
			// i seguenti parametri non vengono passati tra i parametri delle request e response...
			if(StringUtils.isEmpty(this.actionName) && StringUtils.isEmpty(this.namespace)) {
				this.actionName = (String) this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
				this.namespace = (String) this.session.get(ComunicazioniConstants.SESSION_ID_NAMESPACE);
			}
			
			this.entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);

			// recupera l'elenco delle comunicazioni
			this.setComunicazioni(getListaComunicazioni(COMUNICAZIONI_SOCCORSI));
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageComunicazioniRicevute");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}


}
