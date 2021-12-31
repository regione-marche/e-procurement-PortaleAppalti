package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.agiletec.aps.system.SystemConstants;

public class ProcessPageOffertaTecnicaAction extends AbstractProcessPageAction {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4470557106029574816L;

	private IEventManager eventManager;
	
	/** Memorizza la prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;
	
	private int tipoBusta;
	private String codice;
	private String codiceGara;
	private int operazione;
	
	// valori in input dei criteri di valutazione presenti sul form 
	private String[] criterioValutazione;
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getCodice() {
		return codice;
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

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String[] getCriterioValutazione() {
		return criterioValutazione;
	}

	public void setCriterioValutazione(String[] criterioValutazione) {
		this.criterioValutazione = criterioValutazione;
	}
		
	/**
	 * costruttore 
	 */
	public ProcessPageOffertaTecnicaAction() {
		super(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA,
			  WizardOffertaTecnicaHelper.STEP_OFFERTA); 
	} 

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = this.helperNext();
		
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		
		// verifica se action e dati in sessione sono sincronizzati...
		if(!helper.isSynchronizedToAction(this.codice, this)) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			
			if(isOffertaChanged(helper)) {
				// va rigenerato il pdf, quindi elimino l'eventuale file e resetto
				// il flag
				helper.deleteDocumentoOffertaTecnica(this, this.eventManager);
				helper.setRigenPdf(true);
				// sbianco lo UUID
				helper.setPdfUUID(null);
			}

			if(!this.validateAndSetCriteriValutazione(
					helper.getListaCriteriValutazione(), 
					this.criterioValutazione,
					true)) {
				target = INPUT;						
			}
				
			this.nextResultAction = helper.getNextAction(this.currentStep);
			
			// salva l'helper in sessione...
			this.session.put(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA, helper);			
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

//	@SkipValidation
//	public String back() {
//		String target = this.helperBack();
//		
//		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
//			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
//		
//		if (null != this.getCurrentUser()
//			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
//			
//			this.nextResultAction = InitOffertaTecnicaAction
//				.setNextResultAction(helper
//					.getPreviousStepNavigazione(this.currentStep));
//			
//		} else {
//			this.addActionError(this.getText("Errors.sessionExpired"));
//			target = CommonSystemConstants.PORTAL_ERROR;
//		}
//
//		return target;
//	}
	
//	public void validate() {	
//		super.validate();
//		
//		if (this.getFieldErrors().size() > 0) {
//			return;
//		}
//		
//		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
//			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);		
//		if(helper != null) {
//			// --- CRITERI DI VALUTAZIONE ---
//			if(this.validateCriteriValutazione(helper)) {
//			}
//		}			
//	}

	/**
	 * ... 
	 */
	private boolean isOffertaChanged(WizardOffertaTecnicaHelper helper) {
		boolean variazioneCriteriValidazione = isCriteriValidazioneVariati(helper);
	
		return (variazioneCriteriValidazione) || helper.isRigenPdf();
	}

	/**
	 * ... 
	 */
	private boolean isCriteriValidazioneVariati(WizardOffertaTecnicaHelper helper){
		boolean result = false;
		if(this.getCriterioValutazione() != null) {			
			for(int i = 0; i < this.getCriterioValutazione().length; i++) {
				if(helper.getListaCriteriValutazione().get(i).getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
					if( !this.getCriterioValutazione()[i].equals(helper.getListaCriteriValutazione().get(i).getValore()) ) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Valida ed imposta i valori dei criteri di valutazione 
	 */
	private boolean validateAndSetCriteriValutazione(
			List<CriterioValutazioneOffertaType> listaCriteriValutazione,
			String[] valori,
			boolean validateAndSet) 
	{
		boolean validazioneOk = false;
		
		if(valori != null) {
			validazioneOk = true;
			for(int i = 0; i < valori.length; i++) {
				if(listaCriteriValutazione.get(i).getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
					if(StringUtils.isEmpty(valori[i])) {
						this.addActionError(this.getText("Errors.offertaTelematica.criteri.fieldRequired",
							     						 new String[] {listaCriteriValutazione.get(i).getDescrizione()}));
						validazioneOk = false;
					} else {
						switch (listaCriteriValutazione.get(i).getFormato()) {
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:				
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:	
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
								if(listaCriteriValutazione.get(i).getValoreMax() != null && Double.parseDouble(valori[i]) >= listaCriteriValutazione.get(i).getValoreMax()) {
									this.addActionError(this.getText("Errors.offertaTelematica.criteri.valoreMustBeLess",
																     new String[] {listaCriteriValutazione.get(i).getDescrizione(), listaCriteriValutazione.get(i).getValoreMax().toString()}));
									validazioneOk = false;
								}
								if(listaCriteriValutazione.get(i).getValoreMin() != null && Double.parseDouble(valori[i]) < listaCriteriValutazione.get(i).getValoreMin()) {
									this.addActionError(this.getText("Errors.offertaTelematica.criteri.valoreMustBeGreater",
																	 new String[] {listaCriteriValutazione.get(i).getDescrizione(), listaCriteriValutazione.get(i).getValoreMin().toString()}));
									validazioneOk = false;
								}
							break;
							
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:
								if(listaCriteriValutazione.get(i).getValoreMax() != null && Double.parseDouble(valori[i]) > listaCriteriValutazione.get(i).getValoreMax()) {
									this.addActionError(this.getText("Errors.offertaTelematica.criteri.valoreMustBeLess",
																	 new String[] {listaCriteriValutazione.get(i).getDescrizione(), listaCriteriValutazione.get(i).getValoreMax().toString()}));
									validazioneOk = false;
								}
								if(listaCriteriValutazione.get(i).getValoreMin() != null && Double.parseDouble(valori[i]) < listaCriteriValutazione.get(i).getValoreMin()) {
									this.addActionError(this.getText("Errors.offertaTelematica.criteri.valoreMustBeGreater",
																	 new String[] {listaCriteriValutazione.get(i).getDescrizione(), listaCriteriValutazione.get(i).getValoreMin().toString()}));
									validazioneOk = false;
								}
							break;
						}
						
						try {
							WizardOffertaTecnicaHelper.setValoreCriterioValutazione(
										listaCriteriValutazione.get(i), 
										valori[i],
										validateAndSet);
						} catch (NumberFormatException e) {
							this.addFieldError(listaCriteriValutazione.get(i).getDescrizione(), 
											   this.getText(e.getMessage(), new String[]{listaCriteriValutazione.get(i).getDescrizione(), 
																					     listaCriteriValutazione.get(i).getNumeroDecimali() + ""}));
							validazioneOk = false;
						} catch (ParseException e) {
							this.addFieldError(listaCriteriValutazione.get(i).getDescrizione(), 
											   this.getText(e.getMessage(), new String[]{listaCriteriValutazione.get(i).getDescrizione(), 
												   										 WizardOffertaTecnicaHelper.CRITERIO_VALUTAZIONE_TESTO_MAXLEN + ""}));
							validazioneOk = false;
						} catch (Throwable e) {
							this.addFieldError(listaCriteriValutazione.get(i).getDescrizione(), 
									   this.getText(e.getMessage(), new String[]{listaCriteriValutazione.get(i).getDescrizione()}));
							validazioneOk = false;
						}
					}

				}					
			}
		}							
		return validazioneOk; 
	}

	/**
	 * Valida i valori dei criteri di valutazione, senza impostarne il valore 
	 */
	private boolean validateCriteriValutazione(
			List<CriterioValutazioneOffertaType> listaCriteriValutazione,
			String[] valori) 
	{
		return this.validateAndSetCriteriValutazione(listaCriteriValutazione, valori, false); 
	}
	
	
//	public void validate() {	
//		super.validate();
//		if (this.getFieldErrors().size() > 0) {
//			return;
//		}
//		
//		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
//			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
//		
//		if (helper != null) {
//			//...
//		}
//	}
	
}







