package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Estensione per l'offerta economica delle funzioni per la gestione dei documenti.
 * 
 * @author Stefano.Sabbadin
 */
public class DocumentoOffertaAction extends DocumentoBustaAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3738894712508695688L;
	
	
	/**
	 * restituisce l'helper relativo al tipo busta 
	 */
	protected WizardOffertaHelper getHelper() throws ApsException {
		WizardOffertaHelper helper = null;
		if(this.getTipoBusta() == PortGareSystemConstants.BUSTA_ECONOMICA) {
			// BUSTA ECONOMICA
			helper = (WizardOffertaEconomicaHelper) this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
			
		} else if(this.getTipoBusta() == PortGareSystemConstants.BUSTA_TECNICA) {
			// BUSTA TECNICA
			helper = (WizardOffertaTecnicaHelper) this.getSession()
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		} 
		return helper;
	}

	/**
	 * utilizzato dalla classe padre
	 */
	protected WizardDocumentiBustaHelper getWizardDocumentiBustaHelper() throws ApsException {
		WizardOffertaHelper helper = this.getHelper();
		return (helper != null ? helper.getDocumenti() : null);
	}
	
	/**
	 * elimina un allegato richiesto
	 */
	public String deleteAllegatoRichiesto() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// recupera l'helper della busta corretto (tecnica, economica)
				long idOfferta = -1;
				String codice = null;
				
				WizardOffertaHelper helper = this.getHelper();
				if(helper != null) {
					idOfferta = helper.getIdOfferta().longValue();
					codice = helper.getGara().getCodice();
				}
				
				if(this.id < documentiBustaHelper.getDocRichiestiId().size()) {
					// recupera il codice della gara
					codice = (documentiBustaHelper.getCodice() != null
							  ? documentiBustaHelper.getCodice()
							  : codice);
					
					// verifica solo per il documento "offerta economica"!!!
					if(idOfferta >= 0 && idOfferta == documentiBustaHelper.getDocRichiestiId().get(this.id).longValue()) {
						documentiBustaHelper.setDocOffertaPresente(false);
					}
				
					// traccia l'evento di eliminazione di un file...	 
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.DELETE_FILE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage(documentiBustaHelper.getDescTipoBusta() + ": cancellazione documento richiesto" 
							+ ", file="+ documentiBustaHelper.getDocRichiestiFileName().get(this.id)
							+ ", dimensione=" + documentiBustaHelper.getDocRichiestiSize().get(this.id) + "KB");
	
					try {
						documentiBustaHelper.removeDocRichiesto(this.id);
						documentiBustaHelper.setDatiModificati(true);
					} catch (Exception ex) {
						//ho rilanciato di nuovo la stessa azione con un refresh di pagina
					} finally {
						if(evento != null) {
							this.getEventManager().insertEvent(evento);
						}
					}									
						
					if( !this.save() ) {
						target = CommonSystemConstants.PORTAL_ERROR;
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteAllegatoRichiesto");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}
	
	
	/**
	 * elimina un allegato ulteriore
	 */
	public String deleteAllegatoUlteriore() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// recupera l'helper della busta corretto (tecnica, economica)
				long idOfferta = -1;
				String codice = null;
				
				WizardOffertaHelper helper = this.getHelper();
				if(helper != null) {
					idOfferta = helper.getIdOfferta().longValue();
					codice = helper.getGara().getCodice();
				}
				
				if(this.id < documentiBustaHelper.getDocUlterioriDesc().size()) {
					// recupera il codice della gara
					codice = (documentiBustaHelper.getCodice() != null
							  ? documentiBustaHelper.getCodice()
							  : codice);
					
					// traccia l'evento di eliminazione di un file...
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.DELETE_FILE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage(documentiBustaHelper.getDescTipoBusta() + ": cancellazione documento ulteriore" 
							+ ", file="+ documentiBustaHelper.getDocUlterioriFileName().get(this.id)
							+ ", dimensione=" + documentiBustaHelper.getDocUlterioriSize().get(this.id) + "KB");
	
					try {
						documentiBustaHelper.removeDocUlteriore(this.id);
						documentiBustaHelper.setDatiModificati(true);
					} catch (Exception ex) {
						//ho rilanciato di nuovo la stessa azione con un refresh di pagina
					} finally {
						if(evento != null) {
							this.getEventManager().insertEvent(evento);
						}
					}
					
					if( !this.save() ) {
						target = CommonSystemConstants.PORTAL_ERROR;
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteAllegatoUlteriore");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * aggiorna un allegato 	 
	 */
	protected boolean save() throws ApsException {
		String target = SUCCESS;
		
		WizardDocumentiBustaHelper documenti = getWizardDocumentiBustaHelper();
		if (documenti == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {	
			WizardOffertaHelper helperBusta = this.getHelper();
			if(helperBusta == null) {
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else { 
				target = ProcessPageDocumentiOffertaAction.saveDocumenti(
						documenti.getCodice(),
						helperBusta,
						this.getSession(), 
						this);
			}
		}
		
		return (SUCCESS.equalsIgnoreCase(target));
	}
	
}
