package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei documenti dell'elenco
 *
 * @author 
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class ProcessPageQuestionarioRiepilogoAction extends ProcessPageDocumentiAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -386723856302260556L;
	
	
	private static final String BACK							= "back";
	private static final String NEXT							= "next";

	/**
	 * Riferimento al manager per la gestione dei parametri applicativo.
	 */
	
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
	
	/**
	 * ... 
	 */
	@Override
	public String cancel() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = BACK;
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);			
		this.nextResultAction = helper.getPreviousAction(WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO);	
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = NEXT; 
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);			
		this.nextResultAction = helper.getNextAction(WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO);	
		return target;
	}		
	
}
