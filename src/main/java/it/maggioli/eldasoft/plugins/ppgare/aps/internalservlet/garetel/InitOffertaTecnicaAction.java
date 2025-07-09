package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Predispone l'apertura del wizard di inserimento di un'offerta tecnica.
 * 
 * @author ...
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class InitOffertaTecnicaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4010317624244436869L;

	private Map<String, Object> session;

	/** Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting. */
	private String multipartSaveDir;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;

	/** Codice del bando di gara per il quale gestire l'offerta telematica. */
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	@Validate(EParamValidation.CODICE)
	private String tipoBusta;
	@Validate(EParamValidation.DIGIT)
	private String operazione;
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

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}
	
	public String getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(String tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * Ritorna la directory per i file temporanei, prendendola da
	 * struts.multipart.saveDir (struts.properties) se valorizzata
	 * correttamente, altrimenti da javax.servlet.context.tempdir
	 * 
	 * @return path alla directory per i file temporanei
	 */
	private File getTempDir() {
		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir);
	}

	/**
	 * Carica in sessione l'helper di gestione dell'offerta tecnica
	 * opportunamente inizializzato, rilegge gli eventuali dati della bozza
	 * inserita, e decide lo step da visualizzare.
	 * 
	 * @return target struts da visualizzare
	 */
	public String initOffertaTecnica() {
		this.setTarget(SUCCESS);

		// "codice" e "codiceGara" vengono messi in sessione per facilitare
    	// la navigazione da pagina a pagina
		this.session.put("codice", this.codice);				// codice del lotto
		this.session.put("codiceGara", this.codiceGara);		// codice della gara

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// predispone il wizard in sessione caricato con tutti i dati previsti
    			GestioneBuste buste = GestioneBuste.getFromSession();
    			BustaTecnica bustaTec = buste.getBustaTecnica();
    			bustaTec.get(null, this.codice);

				// (3.2.0)
    			// verifica i documenti della busta di riepilogo 
    			// ed integra le informazioni mancanti 
    			BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
    			if(bustaRiepilogo != null) {
    				bustaRiepilogo.verificaIntegraDocumenti(bustaTec);
    			}
    			
				// verifica se e' un'offerta con OEPV ed imposto il wizard 
				// per la gestione della busta tecnica, se necessario...
    			boolean criteriVisibili = (bustaTec.getHelper() != null && bustaTec.getHelper().isCriteriValutazioneVisibili());
				if(criteriVisibili) {
					// busta tecnica con gestione OEPV...
					this.nextResultAction = bustaTec.getHelper().getNextAction("");	// vai al primo step del wizard
				} else {
					// busta tecnica standard...
					this.nextResultAction = "openPageDocumenti";
				}
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "initOfferta");
				this.addActionError(this.getText("Errors.offertaTelematica.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "initOfferta");
				this.addActionError(this.getText("Errors.offertaTelematica.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "initOfferta");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

}
