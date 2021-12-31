package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.RichiestaPartecipazione;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;

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

	private INtpManager ntpManager;
	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;
	private IEventManager eventManager;
	
	private String codice;
	private int operazione;
	private String progressivoOfferta;
	private Date dataPresentazione;	

	private InputStream inputStream;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
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
		
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

		if (partecipazioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.setCodice(partecipazioneHelper.getIdBando());
			this.setOperazione(partecipazioneHelper.getTipoEvento());
			this.setProgressivoOfferta(partecipazioneHelper.getProgressivoOfferta());
			
			boolean domandaPartecipazione = ( partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA );
			boolean inviaOfferta = ( partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA );
			
			if (partecipazioneHelper.isDatiInviati()) {
				// l'invio della comunicazione e' gia' stato completato,
				// pertanto si segnala l'errore e non si ripete l'invio
				this.addActionError(this.getText("Errors.invioComunicazioneCompleted"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				// si elimina pertanto l'oggetto dalla sessione in modo da
				// evitare il ripetersi
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			} else {
				if (partecipazioneHelper.getLotti().isEmpty() && partecipazioneHelper.isGaraTelematica()) {
					this.addActionError(this.getText("Errors.checkDatiObbligatori"));
					this.setTarget(INPUT);
				} else {
					try {
						RichiestaPartecipazione richiesta = new RichiestaPartecipazione(this, true);
						
						boolean inviata = richiesta.send(
								this.codice,
								partecipazioneHelper, 
								null, 
								null);
						
						if(!inviata) {
							this.addActionError(richiesta.getActionError());
							this.setTarget(richiesta.getActionTarget());
						} else {
							// this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
							// this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
							// ...e si imposta il codice del bando per la riapertura del
							// dettaglio
							this.setCodice(partecipazioneHelper.getIdBando());

							if (partecipazioneHelper.isGaraTelematica()) {
								if(domandaPartecipazione) {
									this.setTarget("successGT");
								} else {
									if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
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
		
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		
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
