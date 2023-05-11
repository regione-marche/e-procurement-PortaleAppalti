package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * Action di gestione dell'apertura delle pagine del wizard
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageRiepilogoAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;
	
//	private static final String ID_LISTA_COMPLETA_LOTTI = "listaCompletaLotti";

	private Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	private Date dataPresentazione;	

	private InputStream inputStream;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCodice() {
		return codice;
	}
	
	public void setCodice(String codice) {
		this.codice = codice;
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

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * ... 
	 */
	public String send() {
		this.setTarget(SUCCESS);
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
		WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper(); 

		if (partecipazione == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.setCodice(partecipazione.getIdBando());
			this.setOperazione(partecipazione.getTipoEvento());
			this.setProgressivoOfferta(partecipazione.getProgressivoOfferta());
			
			boolean domandaPartecipazione = buste.isDomandaPartecipazione();
			boolean inviaOfferta = buste.isInvioOfferta();
			
			if (partecipazione.isDatiInviati()) {
				// l'invio della comunicazione e' gia' stato completato,
				// pertanto si segnala l'errore e non si ripete l'invio
				this.addActionError(this.getText("Errors.invioComunicazioneCompleted"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
//				// si elimina pertanto l'oggetto dalla sessione in modo da
//				// evitare il ripetersi
//				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			} else {
				if(buste.isGaraLotti() &&  partecipazione.getLotti().isEmpty() && partecipazione.isGaraTelematica() && partecipazione.isLottiDistinti()) {
					this.addActionError(this.getText("Errors.checkDatiObbligatori"));
					this.setTarget(INPUT);
				} else {
					try {	
						boolean continua = true;
						
						// si prosegue solo se il timestamp ntp e' stato calcolato
						// oppure non serve calcolarlo
						Date dataPresentazione = buste.getNTPDate();
						if (dataPresentazione != null) {
							if (partecipazione.getDataScadenza() != null
								&& dataPresentazione.compareTo(partecipazione.getDataScadenza()) > 0) 
							{
								continua = false; 
								this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
								this.setTarget(CommonSystemConstants.PORTAL_ERROR);
							} else {
								partecipazione.setDataPresentazione(dataPresentazione);
							}
						}
						
						// invia la richiesta di partecipazione (FS11/FS10)
						if(continua) {
							continua = continua && bustaPartecipazione.send();
						}
						
						if( !continua ) {
							this.addActionError("Errore generico");
							this.setTarget(CommonSystemConstants.PORTAL_ERROR);
						} else {
							// this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
							// ...e si imposta il codice del bando per la riapertura del
							// dettaglio
							this.setCodice(partecipazione.getIdBando());

							if (partecipazione.isGaraTelematica()) {
								if(domandaPartecipazione) {
									this.setTarget("successGT");
								} else {
									if(partecipazione.isPlicoUnicoOfferteDistinte()) {
										this.setTarget("successGTOfferteDistinte");
									} else {
										this.setTarget("successGT");
									}
								}
							}
						}
					} catch (ApsException t) {
						ApsSystemUtils.logThrowable(t, this, "send");
						ExceptionUtils.manageExceptionError(t, this);
						this.setTarget("errorWS");
					} catch (IOException t) {
						ApsSystemUtils.logThrowable(t, this, "send");
						this.addActionError(this.getText("Errors.cannotLoadAttachments"));
						this.setTarget("errorWS");
					} catch (Throwable t) {
						ApsSystemUtils.logThrowable(t, this, "send");
						ExceptionUtils.manageExceptionError(t, this);
						this.setTarget("errorWS");
					}
				}
			}
		}
		
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	public String back() {
		String target = "back";

		GestioneBuste buste = GestioneBuste.getFromSession();
		WizardPartecipazioneHelper partecipazioneHelper = buste.getBustaPartecipazione().getHelper();
		
		if (partecipazioneHelper != null &&
			(null != this.getCurrentUser()
			 && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{
			String newTarget = partecipazioneHelper.getPreviousStepTarget(WizardPartecipazioneHelper.STEP_RIEPILOGO);
			target = (newTarget != null ? newTarget : target);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

}
