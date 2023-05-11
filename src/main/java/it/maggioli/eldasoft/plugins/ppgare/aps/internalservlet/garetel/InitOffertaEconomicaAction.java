package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import java.util.Map;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Predispone l'apertura del wizard di inserimento di un'offerta economica,
 * caricando l'eventuale comunicazione in stato bozza e predisponendo i dati in
 * sessione. Viene effettuato anche il calcolo degli step della navigazione del
 * wizard, dato che lo step dei prezzi unitari &egrave; disponibile solo per
 * determinate tipologie di gara.
 * 
 * @author Stefano.Sabbadin
 */
public class InitOffertaEconomicaAction extends EncodedDataAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -1862931437661854979L;

    private Map<String, Object> session;

    private String multipartSaveDir;
    private IBandiManager bandiManager;
    private IComunicazioniManager comunicazioniManager;

    /** Memorizza lal prossima dispatchAction da eseguire nel wizard */
    @Validate(EParamValidation.ACTION)
    private String nextResultAction;

    /** Codice del bando di gara per il quale gestire l'offerta telematica */
    @Validate(EParamValidation.CODICE)
    private String codice;
    @Validate(EParamValidation.CODICE)
    private String codiceGara;
    @Validate(EParamValidation.DIGIT)
    private String tipoBusta;
    private int operazione;
    @Validate(EParamValidation.DIGIT)
    private String progressivoOfferta;

    @Override
    public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
    }

    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
    }

    public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
    }

    public IBandiManager getBandiManager() {
		return bandiManager;
    }

    public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
    }

    public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
    }
    
    public String getNextResultAction() {
		return nextResultAction;
    }

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}
	
    public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(String tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	/**
     * Carica in sessione l'helper di gestione dell'offerta economica
     * opportunamente inizializzato, rilegge gli eventuali dati della bozza
     * inserita, e decide lo step da visualizzare.
     * 
     * @return target struts da visualizzare
     */
    public String initOfferta() {
    	this.setTarget(SUCCESS);

    	// "codice" e "codiceGara" vengono messi in sessione per facilitare
    	// la navigazione da pagina a pagina
    	this.session.put("codice", this.codice);
    	this.session.put("codiceGara", this.codiceGara);

    	if (null != this.getCurrentUser()
    		&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
    	{
    		try {
    			// predispone il wizard in sessione caricato con tutti i dati
    			// previsti
//    			String codGara = (StringUtils.isEmpty(this.codiceGara) ? this.codice : this.codiceGara);
//    			
//    			// ATTENZIONE:
//    			// 	 codiceGara == null ==> gara a lotto unico  
//    			//   codiceGara != null ==> gara a lotti distinti
//    			boolean isLottoUnico = (this.codiceGara == null);
    			
    			GestioneBuste buste = GestioneBuste.getFromSession();
    			BustaEconomica bustaEco = buste.getBustaEconomica();
    			bustaEco.get(null, this.codice);
    			
    			// (3.2.0)
    			// verifica i documenti della busta di riepilogo 
    			// ed integra le informazioni mancanti 
    			BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
    			if(bustaRiepilogo != null) {
    				bustaRiepilogo.verificaIntegraDocumenti(bustaEco);
    			}
    			
    			// vai alla prima pagina del wizard
    			this.nextResultAction = bustaEco.getHelper().getNextAction("");
    			
    		} catch (ApsException e) {
    			ApsSystemUtils.logThrowable(e, this, "initOfferta");
    			ExceptionUtils.manageExceptionError(e, this);
    			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    		} catch (Throwable t) {
    			ApsSystemUtils.logThrowable(t, this, "initOfferta");
    			ExceptionUtils.manageExceptionError(t, this);
    			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    		}
    	} else {
    		this.addActionError(this.getText("Errors.sessionExpired"));
    		this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    	}

    	return this.getTarget();
    }
	
	/**
	 * conferma per elimina e ricrea della busta economica (SEPR-173) 
	 */
	public String confermaRicreaBusta() {
		this.setTarget(SUCCESS);
		return this.getTarget();
	}
	
	/**
	 * elimina e ricrea la busta economica (SEPR-173) 
	 */
	public String ricreaBusta() {
		this.setTarget(SUCCESS);
		
		try {
			BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
			bustaEco.delete(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			
		} catch (Throwable e) {
			ApsSystemUtils.getLogger().error("ricreaBusta", e);
		}
		
		return this.getTarget();
	}

}
