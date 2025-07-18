package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei documenti delle buste
 * economica, tecnica, amministrativa
 *
 * @author 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageQuestionarioRiepilogoAction extends ProcessPageDocumentiBustaAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2128646566474413623L;
	
	private static final String CANCEL 							= "cancel";
	private static final String BACK							= "back";

	/**
	 * Riferimento al manager per la gestione dei parametri applicativo.
	 */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;		// openGestioneBuste | openGestioneBusteDistinte
	
	private Long idComunicazione;			// id della comunicazione
	@Validate(EParamValidation.UUID)
	private String uuid;					// UUID dell'allegato	
//	private String descrizione;				// descrizione del file
//	private File allegato;					// file  
//	private String filename;				// nomefile per "allegato"
//	private String contentType;				// content type per "allegato"
	

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getNextResultAction() {
		return nextResultAction;
	}

	public void setNextResultAction(String nextResultAction) {
		this.nextResultAction = nextResultAction;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String cancel() {
		String target = CANCEL;

		WizardPartecipazioneHelper partecipazione = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if (partecipazione.isPlicoUnicoOfferteDistinte()) {
			if (this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
				this.nextResultAction = "openPageListaBusteTecnicheDistinte";
			} else if (this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
				this.nextResultAction = "openPageListaBusteEconomicheDistinte";
			} else {
				this.nextResultAction = "openGestioneBusteDistinte";
			}
		} else {
			this.nextResultAction = "openGestioneBuste";
		}

		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		return BACK;
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		// NON ESISTE
		return null;
	}		
	
	/**
	 * verifica l'integrita' della busta 
	 */
	public String check() {
		String target = SUCCESS;
		this.nextResultAction = "openQCSummary";
		
		Event evento = null;
		try {
			GestioneBuste buste = GestioneBuste.getFromSession();
			BustaDocumenti busta = buste.getBusta(tipoBusta);
			String codiceLotto = (StringUtils.isNotEmpty(codice) ? codice : codiceGara);
			
			evento = VerificaDocumentiCorrotti.createNewEvent(busta);
			
			VerificaDocumentiCorrotti validazione = new VerificaDocumentiCorrotti(evento);
			validazione.validate(busta, codiceLotto);
			if( !validazione.isErroriPresenti() ) {
				this.addActionMessage(this.getText("Envelope.prontaInvio"));
			} else {
				validazione.addActionErrors(this, evento);
				target = INPUT;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "check");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(t);
			}
		} finally {
			if(evento != null) {
				eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}

}

