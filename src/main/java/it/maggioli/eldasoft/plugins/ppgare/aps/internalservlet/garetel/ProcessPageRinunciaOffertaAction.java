package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;


/**
 * ...
 * 
 * ...
 */
public class ProcessPageRinunciaOffertaAction extends ProcessPageProtocollazioneAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5074535766391058897L;
	
//	private String codice;				// ereditato da ProcessPageProtocollazioneAction
	@Validate(EParamValidation.DIGIT)
	private String operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	@Validate(EParamValidation.MOTIVAZIONE)
	private String motivazione;
	private boolean riepilogo;	
	private Date dataPubblicazione;
	
    private DettaglioGaraType dettaglioGara;  
		
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}
	
	public boolean isRiepilogo() {
		return riepilogo;
	}

	public void setRiepilogo(boolean riepilogo) {
		this.riepilogo = riepilogo;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public Date getDataPubblicazione() {
		return dataPubblicazione;
	}

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	/**
	 * ... 
	 */
	@Override
	public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
	}

	/**
	 * invia la comunicazioni di rinuncia partecipazione 
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */	
	public String rinunciaOfferta() throws GeneralSecurityException, IOException {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
			    this.dettaglioGara = this.bandiManager.getDettaglioGara(this.codice);
			  
				// carica i dati aggiornati dell'impresa
				WizardRegistrazioneImpresaHelper datiImpresa = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(), 
						this);
			
				String target = super.send(	
						PortGareSystemConstants.RICHIESTA_RINUNCIA_PARTECIPAZIONE_OFFERTA,
						null,
						datiImpresa, 
						null,
						null,
						this.codice, 
						null,
						null,
						null);
				
				if( !SUCCESS.equalsIgnoreCase(target) ) {
					this.addActionError(this.getText("Errors.unexpected"));
					this.setTarget(target);
				}
			} catch (Exception e) {
				this.addActionError(this.getText("Errors.unexpected"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "rinunciaOfferta");
				this.addActionError(this.getText("Errors.unexpected"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}	

	/**
	 * ... 
	 */
	@SkipValidation
	public String confirmAnnulla() {
		this.setTarget("confirmAnnulla");
		return this.getTarget();
	}

    /**
     * annulla la rinuncia all'offerta
     */
    @SkipValidation
    public String annullaRinuncia() {
      this.setTarget(SUCCESS);

      boolean comunicazioneEliminata = false;

      if (null != this.getCurrentUser() && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
        Event evento = new Event();
        evento.setUsername(this.getCurrentUser().getUsername());
        evento.setDestination(this.getCodice());
        evento.setLevel(Event.Level.INFO);
        evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
        evento.setMessage("Annullamento comunicazione FS14");
        evento.setIpAddress(this.getCurrentUser().getIpAddress());
        evento.setSessionId(this.getRequest().getSession().getId());

        try {
          DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities.retrieveComunicazione(this.comunicazioniManager,
              this.getCurrentUser().getUsername(), this.codice, null, null,
              PortGareSystemConstants.RICHIESTA_RINUNCIA_PARTECIPAZIONE_OFFERTA);

          if (dettComunicazione != null) {
            evento.setMessage(evento.getMessage() + " con id " + dettComunicazione.getId());
            this.dataInvio = this.retrieveDataNTP();
            this.dataPubblicazione = dettComunicazione.getDataPubblicazione();

            // elimina la comunicazione FS14
            this.comunicazioniManager.deleteComunicazione(dettComunicazione.getApplicativo(), dettComunicazione.getId());

            comunicazioneEliminata = true;
          } else {
            // this.addActionError(this.getText("Errors.comunicazione non trovata"));
            // this.target = CommonSystemConstants.PORTAL_ERROR;
          }

        } catch (ApsException e) {
          ApsSystemUtils.logThrowable(e, this, "annullaRinuncia");
          this.addActionError(this.getText("Errors.unexpected"));
          this.setTarget(CommonSystemConstants.PORTAL_ERROR);
          evento.setError(e);
        } finally {
          this.eventManager.insertEvent(evento);
        }

        if (comunicazioneEliminata) {
          evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
          evento.setMessage("Invio mail ricevuta di conferma annullamento comunicazioni a "
              + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
          evento.setLevel(Event.Level.INFO);
          evento.setDetailMessage((String) null);
          try {
            this.sendMailAnnullaRinuncia();
          } catch (ApsException e) {
            this.msgErrore = this.getText("Errors.sendMailError");
            ApsSystemUtils.logThrowable(e, this, "invio",
                "Per errori durante la connessione al server di posta, "
                    + "non e' stato possibile inviare all'impresa con utenza "
                    + this.getCurrentUser().getUsername()
                    + " la ricevuta della richiesta di rinuncia per la procedura con codice "
                    + this.codice
                    + ".");
            ExceptionUtils.manageExceptionError(e, this);
            evento.setError(e);
          } finally {
            this.eventManager.insertEvent(evento);
          }
        }

      } else {
        this.addActionError(this.getText("Errors.sessionExpired"));
        this.setTarget(CommonSystemConstants.PORTAL_ERROR);
      }

      return this.getTarget();
    }
	
	/**
	 * Invia la mail di ricevuta all'operatore economico, indicando
	 * eventualmente i riferimenti della protocollazione ove prevista.
	 * 
	 * @throws ApsSystemException
	 */
	private void sendMailAnnullaRinuncia() throws ApsSystemException {
		String data = UtilityDate.convertiData(dataPubblicazione, UtilityDate.FORMATO_GG_MM_AAAA);
		
		String text = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_ANNULLA_RINUNCIA_TESTO"), 
				new Object[] {data});
		
		String subject = MessageFormat.format(this.getI18nLabel("MAIL_GARETEL_RICEVUTA_ANNULLA_RINUNCIA_OGGETTO"), 
				new Object[] {this.codice});
		
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { (String)((IUserProfile) this.getCurrentUser().getProfile()).getValue("email") }, 
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}

	
//	@Override
//	protected byte[] addComunicazionePdf(AllegatoComunicazioneType[] allegati) {
//		// metodo personalizzabile
//		byte[] content = super.addComunicazionePdf(allegati);
//		// ...
//		return content;
//	}
	
//	@Override
//	protected String getComunicazionePdf() {
//		// metodo personalizzabile 
//		DA MODIFICARE SE NON VIENE UTILIZZATO "getComunicazionePdfDefault()" PER GENERARE IL PDF!
//	}

	@Override
	protected String getDescrizioneFunzione() {
		return "rinuncia partecipazione";
	}

	@Override
	protected String getOggettoNuovaComunicazione() {
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_RINUNCIA_OGGETTO"), 
				new Object[] { this.dettaglioGara.getDatiGeneraliGara().getCodice() });
		return oggetto;
	}

	@Override
	protected String getTestoNuovaComunicazione() {
		return this.motivazione;
	}

	@Override
	protected String getOggettoMailUfficioProtocollo() {
		String oggetto = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RINUNCIA_RICEVUTA_OGGETTO"), 
				new Object[] { this.getOggettoNuovaComunicazione(), this.codice });
		return oggetto;
	}

	@Override
	protected String getTestoMailUfficioProtocollo() {
		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String oggetto = this.dettaglioGara.getDatiGeneraliGara().getOggetto();
		
		String testo = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RINUNCIA_RICEVUTA_TESTO"),
				new Object[] { ragioneSociale, data, this.codice, oggetto });
		
		if (this.isPresentiDatiProtocollazione()) {
			testo = MessageFormat.format(
					this.getI18nLabelFromDefaultLocale("MAIL_GARETEL_RINUNCIA_RICEVUTA_TESTOCONPROTOCOLLO"),
					new Object[] {ragioneSociale, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo(), 
								  this.codice, oggetto});
		}
		
		return testo;
	}
	
	@Override
	protected void addAllegatiMailUfficioProtocollo(Map<String, byte[]> allegatiMail) {
		// NON CI DOVREBBERO ESSERE ALLEGATI DA ALLEGARE ALLA MAIL!!! 
	}
		
	@Override
	protected String getOggettoMailConfermaImpresa() {
		String oggetto = MessageFormat.format(
				this.getI18nLabel("MAIL_GARETEL_RINUNCIA_RICEVUTA_OGGETTO"), 
				new Object[] { this.getOggettoNuovaComunicazione(), this.codice });
		return oggetto;
	}

	@Override
	protected String getTestoMailConfermaImpresa() {
		String data = UtilityDate.convertiData(this.dataInvio, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String ragioneSociale = this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
		String oggetto = this.dettaglioGara.getDatiGeneraliGara().getOggetto();

		String testo = MessageFormat.format(
				this.getI18nLabel("MAIL_GARETEL_RINUNCIA_RICEVUTA_TESTO"),
				new Object[] { ragioneSociale, data, this.codice, oggetto });

		if (this.isPresentiDatiProtocollazione()) {
			testo = MessageFormat.format(
					this.getI18nLabel("MAIL_GARETEL_RINUNCIA_RICEVUTA_TESTOCONPROTOCOLLO"),
					new Object[] {ragioneSociale, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo(),
								  this.codice, oggetto});
		}

		return testo;
	}

	@Override
	protected String getWSDMClassificaFascicolo() {
		String classificaFascicolo = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CLASSIFICA);
		return classificaFascicolo;
	}

	@Override
	protected String getWSDMTipoDocumento() {
		String tipoDocumento = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TIPO_DOCUMENTO);
		return tipoDocumento;
	}

	@Override
	protected String getWSDMCodiceRegistro() {
		String codiceRegistro = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_CODICE_REGISTRO);
		return codiceRegistro;
	}

	@Override
	protected String getWSDMIdIndice() {
		String idIndice = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_INDICE);
		return idIndice;
	}

	@Override
	protected String getWSDMIdTitolazione() {
		String idTitolazione = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_GARE_TITOLAZIONE);
		return idTitolazione;
	}

}
