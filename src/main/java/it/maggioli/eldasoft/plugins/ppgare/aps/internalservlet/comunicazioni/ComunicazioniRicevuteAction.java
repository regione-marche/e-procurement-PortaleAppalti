package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;


import com.agiletec.aps.system.ApsSystemUtils;
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
	
	private static final int MAX_NUM_RECORD = 10;
	private static final String COMUNICAZIONI_ENTITA_LFS	= "APPA";
	
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
	 * ... 
	 */
	public String openPageComunicazioniRicevute() {
		
		UserDetails userDetails = this.getCurrentUser();
		int startIndex = 0;
		
		//gestione del ritorno alla pagina aperta partendo dal dettaglio
		if(this.getPagina() > 0) {
			this.model.setCurrentPage(this.getPagina());
		}

		try {
			// in caso si torni dal Dettaglio Comunicazioni premendo il link "Torna alla lista"...
			// i seguenti parametri non vengono passati tra i parametri delle request e response...
			if(StringUtils.isEmpty(this.actionName) && StringUtils.isEmpty(this.namespace)) {
				this.actionName = (String) this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
				this.namespace = (String) this.session.get(ComunicazioniConstants.SESSION_ID_NAMESPACE);
			}
			this.codice2 = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA);
			if(this.model.getCurrentPage() > 0) {
				startIndex = MAX_NUM_RECORD * (this.model.getCurrentPage() - 1);
			}

			String e = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			if(COMUNICAZIONI_ENTITA_LFS.equalsIgnoreCase(e)) {
				this.entita = COMUNICAZIONI_ENTITA_LFS;
			}
			//this.entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);			
			String codiceProcedura = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			Long genereProcedura = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);						
			if(StringUtils.isNotEmpty(this.comunicazioniCodiceProcedura)) {
				codiceProcedura = this.comunicazioniCodiceProcedura;
			}
			
			if(this.comunicazioniGenere != null && this.comunicazioniGenere.longValue() > 0) {
				genereProcedura = this.comunicazioniGenere;
			}

			this.comunicazioniCodiceProcedura = codiceProcedura;
			this.genere = genereProcedura;
			
			
			// genere = 1 quando area personale -> ricevute -> comunicazione -> vai a procedura -> ricevute 
			if(this.genere != null && (this.genere == 100 || this.genere == 1)) {
				this.setComunicazioni(this.bandiManager
						.getComunicazioniPersonaliRicevuteGaraLotti(
								userDetails.getUsername(), 
								StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
								false,
								this.entita, 
								startIndex, MAX_NUM_RECORD));
			} else {
				this.setComunicazioni(this.bandiManager
						.getComunicazioniPersonaliRicevute(
								userDetails.getUsername(),
								StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
								null,
								false,
								this.entita, 
								startIndex, MAX_NUM_RECORD));
			}

			this.model.processResult(this.getComunicazioni().getNumTotaleRecord(), this.getComunicazioni().getNumTotaleRecordFiltrati());
			this.setTipoComunicazione("ricevute");
			
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO, this.tipoComunicazione);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.model.getCurrentPage());
			this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
			this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
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
	 * ... 
	 */
	public String openPageComunicazioniArchiviate() {

		UserDetails userDetails = this.getCurrentUser();
		int startIndex = 0;
		
		//gestione del ritorno alla pagina aperta partendo dal dettaglio
		if(this.getPagina() > 0) {
			this.model.setCurrentPage(this.getPagina());
		}
		
		try {
			if(this.model.getCurrentPage() > 0 ) {
				startIndex = MAX_NUM_RECORD * (this.model.getCurrentPage() - 1);
			}
			
			String codiceProcedura = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			this.codice2 = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA);
			Long genereProcedura = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);						
			if(StringUtils.isNotEmpty(this.comunicazioniCodiceProcedura)) {
				codiceProcedura = this.comunicazioniCodiceProcedura;
			}
			if(this.comunicazioniGenere != null && this.comunicazioniGenere.longValue() > 0) {
				genereProcedura = this.comunicazioniGenere;
			}
			this.comunicazioniCodiceProcedura = codiceProcedura;
			this.genere = genereProcedura;
			this.entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);
			if(genere != null && (genere == 100 || genere == 1)) {
				this.setComunicazioni(this.bandiManager
						.getComunicazioniPersonaliArchiviateGaraLotti(
								userDetails.getUsername(),  
								StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
								false,
								this.entita,
								startIndex, MAX_NUM_RECORD));
			} else {
				this.setComunicazioni(this.bandiManager
						.getComunicazioniPersonaliArchiviate(
								userDetails.getUsername(),  
								StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
								null,
								false,
								this.entita,
								startIndex,	MAX_NUM_RECORD));
			}

			this.model.processResult(this.getComunicazioni().getNumTotaleRecord(), this.getComunicazioni().getNumTotaleRecordFiltrati());
			this.setTipoComunicazione("archiviate");
			
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO, this.tipoComunicazione);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.model.getCurrentPage());
			this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
			this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageComunicazioniArchiviate");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}	

	/**
	 * ... 
	 */
	public String openPageSoccorsiIstruttori() {
	
		UserDetails userDetails = this.getCurrentUser();
		int startIndex = 0;
		
		// gestione del ritorno alla pagina aperta partendo dal dettaglio
		if(this.getPagina() > 0) {
			this.model.setCurrentPage(this.getPagina());
		}

		try {
			// in caso si torni dal Dettaglio Comunicazioni premendo il link "Torna alla lista"...
			// i seguenti parametri non vengono passati tra i parametri delle request e response...
			if(StringUtils.isEmpty(this.actionName) && StringUtils.isEmpty(this.namespace)) {
				this.actionName = (String) this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
				this.namespace = (String) this.session.get(ComunicazioniConstants.SESSION_ID_NAMESPACE);
			}
			
			if(this.model.getCurrentPage() > 0) {
				startIndex = MAX_NUM_RECORD * (this.model.getCurrentPage() - 1);
			}

			String codiceProcedura = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA);
			Long genereProcedura = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);						
			if(StringUtils.isNotEmpty(this.comunicazioniCodiceProcedura)) {
				codiceProcedura = this.comunicazioniCodiceProcedura;
			}
			if(this.comunicazioniGenere != null && this.comunicazioniGenere.longValue() > 0) {
				genereProcedura = this.comunicazioniGenere;
			}
			this.comunicazioniCodiceProcedura = codiceProcedura;
			this.genere = genereProcedura;
			
			
			// genere = 1 quando area personaele -> ricevute -> comunicazione -> vai a procedura -> ricevute 
			if(this.genere != null && (this.genere == 100 || this.genere == 1)) {
				this.setComunicazioni(this.bandiManager
						.getComunicazioniPersonaliRicevuteGaraLotti(
								userDetails.getUsername(), 
								StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
								true,
								this.entita,
								startIndex, MAX_NUM_RECORD));
			} else {
				this.setComunicazioni(this.bandiManager
						.getComunicazioniPersonaliRicevute(
								userDetails.getUsername(),
								StringUtils.stripToNull(this.comunicazioniCodiceProcedura),
								null,
								true,
								this.entita,
								startIndex, MAX_NUM_RECORD));
			}

			this.model.processResult(this.getComunicazioni().getNumTotaleRecord(), this.getComunicazioni().getNumTotaleRecordFiltrati());
			this.setTipoComunicazione("soccorsiIstruttori");
			
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_TIPO, this.tipoComunicazione);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.model.getCurrentPage());
			this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
			this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPageComunicazioniRicevute");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}


}
