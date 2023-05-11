package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.TokenInterceptor;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.www.sil.WSPagoPASoap.PagamentiOutType;
import it.eldasoft.www.sil.WSPagoPASoap.PagoPaException;
import it.eldasoft.www.sil.WSPagoPASoap.RiferimentoOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

/**
 * Questa classe gestisce le azioni per il pagamento tramite PagoPA
 * @author gabriele.nencini
 *
 */
public class PagoPaActionCrea extends EncodedDataAction implements SessionAware,ServletResponseAware,ModelDriven<PagoPaPagamentoModel> {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5113575431373320423L;
	
	private final Logger logger = ApsSystemUtils.getLogger();
	private Map<String, Object> session;
	private IBandiManager bandiManager;
	private PagoPaManager pagoPaManager;

	@Validate
	private PagoPaPagamentoModel model;
	private ConfigInterface configManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	
	private Map<Integer,String> tipiCausalePagamento;
	@Validate(EParamValidation.URL)
	private String followLink;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	
	private InputStream inputStream;
	private HttpServletResponse response;
	private String errorDownload;

	/**
	 * @param errorDownload the errorDownload to set
	 */
	public void setErrorDownload(String errorDownload) {
		this.errorDownload = errorDownload;
	}


	/**
	 * @param configManager the configManager to set
	 */
	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	
	/**
	 * @return the tipiCausalePagamento
	 */
	public Map<Integer, String> getTipiCausalePagamento() {
		return tipiCausalePagamento;
	}

	public PagoPaActionCrea() {
		logger.info("PagoPaActionCrea()");
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	@Override
	public PagoPaPagamentoModel getModel() {
		return this.model;
	}
	
	public void setModel(PagoPaPagamentoModel model) {
		this.model = model;
	}

	/**
	 * @param bandiManager the bandiManager to set
	 */
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	/**
	 * @param pagoPaManager the pagoPaManager to set
	 */
	public void setPagoPaManager(PagoPaManager pagoPaManager) {
		this.pagoPaManager = pagoPaManager;
	}

	/**
	 * @return the followLink
	 */
	public String getFollowLink() {
		return followLink;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}


	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}


	/**
	 * @param urlManager the urlManager to set
	 */
	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}


	/**
	 * @param pageManager the pageManager to set
	 */
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public String nuovo() {
		logger.info("PagoPaAction.nuovo");
		this.model = new PagoPaPagamentoModel();
		try {
			
			this.tipiCausalePagamento = this.pagoPaManager.getElencoTipiCausalePagamento();
			this.session.put("tipiCausalePagamento",this.tipiCausalePagamento);
			this.session.put("codimp", this.bandiManager.getIdImpresa(this.getCurrentUser().getUsername()));
//			this.model.setCodiceGara(RandomStringUtils.random(8,true,true));
//			this.model.setCausale(RandomStringUtils.random(8,true,true));//TODO menu a tendina
//			this.model.setImporto(RandomUtils.nextDouble());
//			this.model.setIdRata(UUID.randomUUID().toString());
			
			
			
			logger.info("model: {}",model);
			this.session.put("model", model);
		} catch (Exception e) {
			logger.error("Errorre in nuovo",e);
			
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String crea() {
		logger.info("crea");
		logger.info("model: {}",model);
		//TODO chiamare il dettaglio gara
		//alla conferma si apre la nuova finestra??
		//azione successiva ??
		this.tipiCausalePagamento = (Map<Integer, String>) this.session.get("tipiCausalePagamento");
		if(validate(this.model)) {
			logger.info("Modello valido facciamo quello che dobbiamo");
			// richiedere dettaglio della gara per "verificare che la gara esista"
			try {
				UserDetails currentUser = (UserDetails)this.session.get(SystemConstants.SESSIONPARAM_CURRENT_USER);
				RiferimentoOutType rot = pagoPaManager.getRiferimento(new PagoPaRiferimentoFilterModel().codice(model.getCodiceGara()).usernome(currentUser.getUsername()));
				if(rot == null) {
					this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_RIFPROC"));
					return SUCCESS;
				}
//				DettaglioGaraType dgt = bandiManager.getDettaglioGara(model.getCodiceGara());
//				if(dgt == null) {
//					this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_RIFPROC"));
//					return SUCCESS;
//				}
//				this.model.setTitoloGara(dgt.getDatiGeneraliGara().getOggetto());
				this.model.setTitoloGara(rot.getOggetto());
//				Date dt = InitIscrizioneAction.calcolaDataOra(
//						dgt.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
//						dgt.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),
//						true);
//				if(dt == null) {
//					dt = InitIscrizioneAction.calcolaDataOra(
//							dgt.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
//							dgt.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(),
//							true);
//				}
				Date dt =Date.from(ZonedDateTime.now().plusDays(30).toInstant());
				logger.info("Data scadenza fixed sysDate + 30d: {}",dt);
				this.model.setDataScadenza(dt);//TODO verificare in base alla tipologia
				this.model.setDataInizioValidita(new Date());
				this.model.setDataFineValidita(this.model.getDataScadenza());
				DatiImpresaDocument datiImpresa = bandiManager.getDatiImpresa(this.getCurrentUser().getUsername(),null);
				
				this.model.setIdFiscaleDebitore(StringUtils.trimToNull(datiImpresa.getDatiImpresa().getImpresa().getPartitaIVA()));
				if(StringUtils.isBlank(this.model.getIdFiscaleDebitore())){
					this.model.setIdFiscaleDebitore(StringUtils.trimToNull(datiImpresa.getDatiImpresa().getImpresa().getCodiceFiscale()));
				}
				this.model.setCodiceImpresa(this.session.get("codimp").toString());
				this.model.setUsername(this.getCurrentUser().getUsername());
				//TODO crea a DB il record in stato bozza
				pagoPaManager.creaBozzaPagamento(this.model);
				this.model.setStato(1);
				return "creato";
				
			} catch (ApsException e) {
				logger.error("Errore nella comunicazione con WSAppalti.",e);
				this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_GEN"));
				return SUCCESS;
			} catch (PagoPaException e) {
				logger.error("Errore nella invocazione del servizio.",e);
				this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
				return SUCCESS;
			} catch (RemoteException e) {
				logger.error("Errore nella invocazione del servizio.",e);
				this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
				return SUCCESS;
			} catch (Exception e) {
				logger.error("Errore nella invocazione del servizio.",e);
				this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
				return SUCCESS;
			}
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String eseguipagamento() {
		logger.info("Richiesto pagamento per id {}",this.getModel().getId());
		try {
			this.followLink =  pagoPaManager.eseguiPagamento(this.model,configManager.getParam(SystemConstants.PAR_APPL_BASE_URL));
			logger.info("Salvo in sessione id: {}",this.getModel().getId());
			this.session.put("id", this.getModel().getId());
			return "follow";
		} catch (Exception e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			this.tipiCausalePagamento = (Map<Integer, String>) this.session.get("tipiCausalePagamento");
			return SUCCESS;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		this.model = new PagoPaPagamentoModel();
		this.tipiCausalePagamento = (Map<Integer, String>) this.session.get("tipiCausalePagamento");
		return SUCCESS;
	}
	
	private boolean validate(PagoPaPagamentoModel model) {
		boolean valid = true;
		if(StringUtils.isBlank(model.getCodiceGara())) {
			this.addActionError(this.getText("column.select")+" "+this.getI18nLabel("LABEL_PAGOPA_GARA_CODICE").toLowerCase());
			valid = false;
		}
		if(model.getCausale()==null || model.getCausale().intValue()==-1) {
			this.addActionError(this.getText("column.select")+" "+this.getI18nLabel("LABEL_PAGOPA_CAUSALE").toLowerCase());
			valid = false;
		}
		if(model.getImporto()==null || model.getImporto()<=0.0) {
			this.addActionError(this.getText("column.select")+" "+this.getI18nLabel("LABEL_PAGOPA_IMPORTO").toLowerCase()+" "+this.getI18nLabel("LABEL_PAGOPA_ERR_VAL_IMP").toLowerCase());
			valid = false;
		}
		return valid;
	}
	
	public String dettaglio() {
		try {
			logger.debug("this.model: {}",this.model);
			this.tipiCausalePagamento = this.pagoPaManager.getElencoTipiCausalePagamento();
			PagamentiOutType out = pagoPaManager.getPagamentoById(this.model.getId());
			this.model.setCausale(out.getCausaletip());
			this.model.setCodiceGara(out.getCodicegara());
//			DettaglioGaraType dgt = bandiManager.getDettaglioGara(model.getCodiceGara());
//			this.model.setTitoloGara(dgt.getDatiGeneraliGara().getOggetto());
			UserDetails currentUser = (UserDetails)this.session.get(SystemConstants.SESSIONPARAM_CURRENT_USER);
			try {
				RiferimentoOutType rot = pagoPaManager.getRiferimento(new PagoPaRiferimentoFilterModel().codice(model.getCodiceGara()).usernome(currentUser.getUsername()));
				this.model.setTitoloGara(rot.getOggetto());
			} catch (Exception e) {
				logger.warn("Oggetto non trovato per procedura {}",model.getCodiceGara());
				this.model.setTitoloGara(" - ");
			}
//			this.model.setCodiceImpresa(this.bandiManager.getIdImpresa(this.getCurrentUser().getUsername()));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			if(out.getDatafinevalidita()!=null)
				this.model.setDataFineValidita(sdf.parse(out.getDatafinevalidita()));
			if(out.getDatainiziovalidita()!=null)
				this.model.setDataInizioValidita(sdf.parse(out.getDatainiziovalidita()));
			if(out.getDatascadenza()!=null)
				this.model.setDataScadenza(sdf.parse(out.getDatascadenza()));
			if(out.getDatapagamento()!=null)
				this.model.setDataPagamento(sdf.parse(out.getDatapagamento()));
			this.model.setId(out.getId());
			this.model.setIdDebito(out.getIddebito());
//			this.model.setIdFiscaleDebitore(ERROR);
			this.model.setIdRata(out.getIdrata());
			this.model.setImporto(out.getImporto().doubleValue());
			this.model.setIuv(out.getIuv());
			this.model.setUsername(this.getCurrentUser().getUsername());
			this.model.setStato(out.getStatotip());
			if(this.model.getStato()!=3 && Date.from(Instant.now()).after(this.model.getDataScadenza())) {
				this.addActionMessage(this.getI18nLabel("LABEL_PAGOPA_MSG_NOPAG"));
				this.model.setPagamentoAbilitato(false);
			}
			if(this.model.isPagamentoEffettuato()) {
				this.addActionMessage(this.getI18nLabel("LABEL_PAGOPA_MSG_PAGNEW"));				
			}
			if(StringUtils.isNotBlank(errorDownload)) {
				this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_DOWNLOAD"));
			}
		} catch (PagoPaException e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			return SUCCESS;
		} catch (RemoteException e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			return SUCCESS;
		} catch (ApsException e) {
			logger.error("Errore nella comunicazione con WSAppalti.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_GEN"));
			return SUCCESS;
		} catch (Exception e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			return SUCCESS;
		}
		return SUCCESS;
	}
	
	public String scaricaRicevuta() {
		logger.info("Invoked scaricaRicevuta with id: {}",this.model.getId());
		PagamentiOutType out;
		try {
			out = pagoPaManager.getPagamentoById(this.model.getId());
			logger.info("Found pagamento: {}",out);
			this.inputStream = pagoPaManager.getRicevuta(out);
			this.filename = out.getIuv()+".xml";
		} catch (PagoPaException e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			return "error";
		} catch (RemoteException e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			return "error";
		} catch (Exception e) {
			logger.error("Errore nella invocazione del servizio.",e);
			this.addActionError(this.getI18nLabel("LABEL_PAGOPA_ERR_SRV"));
			return "error";
		}
		logger.info("Return success -> {}",this.filename);
		return SUCCESS;
	}
	
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest(); 
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);
		reqCtx.setResponse(this.response);
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPage pageDest = this.pageManager.getPage("ppgare_pagopa_effettuati");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		String token = TokenInterceptor.saveSessionToken(request);
		PageURL url = this.urlManager.createURL(reqCtx);
		url.addParam("actionPath", "/ExtStr2/do/FrontEnd/PagoPA/dettaglioPagamento.action");
		url.addParam("currentFrame", "7");
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		url.addParam("errorDownload", "1");
		url.addParam("model.id", String.valueOf(this.model.getId()));
		url.addParam(TokenInterceptor.getStrutsTokenName(), token);
		String correctedUrl = StringUtils.replace(url.getURL(),"&amp;","&");
		return correctedUrl;
	}
	
	

}
