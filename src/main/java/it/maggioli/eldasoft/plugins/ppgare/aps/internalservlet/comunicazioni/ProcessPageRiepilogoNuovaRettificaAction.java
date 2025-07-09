package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.DelegateUser;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.COMUNICAZIONE })
public class ProcessPageRiepilogoNuovaRettificaAction extends ProcessPageRiepilogoNuovaComunicazioneAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 5136809295825019975L;
	
	// parametri restituiti alla configurazione della action presente in comunicazioni.xml
	@Validate(EParamValidation.ACTION)
	protected String actionName;
	@Validate(EParamValidation.ACTION)
	protected String namespace;
	
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

	/**
	 * costruttore
	 */
	public ProcessPageRiepilogoNuovaRettificaAction() {
		super(new WizardRettificaHelper());
	}
	
	/**
	 * annulla l'invio della richiesta di rettifica  
	 */
	public String cancelRichiesta() {
		// NB: imposta i parametri actionname, namespace da passare alla configurazione della action presente in comunicazioni.xml		
		actionName = (StringUtils.isEmpty(getCodice2()) ? "openRiepilogoOfferta" : "riepilogoOfferteDistinte");	    	
		namespace = "/do/FrontEnd/GareTel";
        return "cancel";
	}
	
	/**
	 * chiedi la conferma per inviare la richiesta di rettifica  
	 */
	public String confirmRichiesta() {
		this.setTarget(TARGET_REOPEN);
		
		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return this.getTarget();
		}

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			this.setTarget(SUCCESS);
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	/**
	 * invia la comunicazione di "richiesta rettifica"
	 */
	public String sendRichiesta() throws ApsException {
		this.setTarget(TARGET_SUCCESS_PAGE);
		
		if( !hasPermessiInvioFlusso() ) {
			addActionErrorSoggettoImpresaPermessiAccessoInsufficienti();
			return this.getTarget();
		}
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// recupera i dati impresa 
		        WizardRegistrazioneImpresaHelper impresa = ImpresaAction.getLatestDatiImpresa(
		        		this.getCurrentUser().getUsername(),
		                this
		        );
		        
		        // recupera il dettaglio di gara
		        DettaglioGaraType dettaglioGara = getBandiManager().getDettaglioGara(codice);
		        
		        // recupera mittente, concorsoDiProgettazione, fineConc, concorsoCrypted
		    	String mittente = (impresa != null && impresa.getDatiPrincipaliImpresa() != null 
		    			? impresa.getDatiPrincipaliImpresa().getRagioneSociale() 
		    			: null
		    	);
		    	
		    	// in caso di concorso di progettazione recupera i dati del mittente
		    	boolean concorsoCrypted = getBandiManager().isConcorsoCrypted(getCurrentUser().getUsername(), codice);
		    	if(concorsoCrypted) 
		    		mittente = getBandiManager().getRagioneSocialeAnonima(getCurrentUser().getUsername(), codice);
		
		        // recupera i dati dei concorsi di progettazione
		    	boolean concorsoDiProgettazione = false;
		    	boolean fineConc = false;
		        if (dettaglioGara != null && dettaglioGara.getDatiGeneraliGara() != null) {
		        	concorsoDiProgettazione = "9".equals(dettaglioGara.getDatiGeneraliGara().getIterGara())
		             						  || "10".equals(dettaglioGara.getDatiGeneraliGara().getIterGara());
		            if (concorsoDiProgettazione)
		            	fineConc = dettaglioGara.getDatiGeneraliGara().isFineConc();
		        }
				
				// prepara la richiesta di rettifica
		        helper = WizardRettificaHelper.creaRichiestaRettifica(
						getCurrentUser().getUsername()
						, codice
						, codice2
						, progressivoOfferta
						, tipoBusta
						, operazione
				);
				
		        helper.setComunicazioneId(idComunicazione);
		        helper.setComunicazioneApplicativo(applicativo);
		        helper.setIdDestinatario(idDestinatario);    		// progressivo del destinatario relativo alla comunicazione !!!
		        //helper.setDestinatario(destinatario);	???
		        helper.setMittente(mittente);
		        helper.setImpresa(impresa);
		        helper.setConcorsoDiProgettazioneCrypted(concorsoCrypted);
		        helper.setConcorsoDiProgettazione(concorsoDiProgettazione);
		        helper.setConcEnded(fineConc);
		        helper.setTipoBusta(tipoBusta);
				
				// inizializza i dati in sessione necessari per chiamare "send()" del padre !!!
		        putWizardToSession();	//this.session.put(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE, helper);
			    this.session.put(ComunicazioniConstants.SESSION_ID_FROM, from);
			    this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, pagina);
			    this.session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, false);
			    this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA, codice2);
			    
				// invia la comunicazione
			    this.setTarget( super.send() );
			    
			} catch (Exception ex) {
				ApsSystemUtils.logThrowable(ex, this, "sendRichiestaRettifica");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
