package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Action per le operazioni di invio buste di gare telematiche
 *
 * @version 1.0
 * @author Marco.Perazzetta
 *
 */
public class OpenGestioneBusteAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1693287506907078618L;

	protected IComunicazioniManager comunicazioniManager;
	protected IBandiManager bandiManager;
	protected IAppParamManager appParamManager;

	protected Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
	protected String codiceGara;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	protected int operazione;
	@Validate(EParamValidation.DIGIT)
	protected String progressivoOfferta;
	@Validate(EParamValidation.DIGIT)
	protected String nuovaOfferta;
	private DettaglioComunicazioneType comunicazione;
	private DettaglioComunicazioneType comunicazioneBustaPreq;
	private DettaglioComunicazioneType comunicazioneBustaAmm;
	private DettaglioComunicazioneType comunicazioneBustaTec;
	private DettaglioComunicazioneType comunicazioneBustaEco;
	private DettaglioComunicazioneType comunicazioneBustaRie;
	private DettaglioComunicazioneType comunicazioneDomandaPartecipazione;
	private boolean bustaPreqAlreadySent;
	private boolean bustaAmmAlreadySent;
	private boolean bustaTecAlreadySent;
	private boolean bustaEcoAlreadySent;
	private Integer tipoOffertaTelematica;
	private boolean offertaTelematica;
	private boolean offertaTecnica;	
	private boolean protocollazioneMailFallita;
	private boolean costoFisso;
	private boolean noBustaAmministrativa;

	@Validate(EParamValidation.FILE_NAME)
	private String multipartSaveDir;
	
	public Map<String, Object> getSession() {
		return session;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return comunicazioniManager;
	}

	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public IAppParamManager getAppParamManager(){
		return this.appParamManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

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
	
	public String getNuovaOfferta() {
		return nuovaOfferta;
	}

	public void setNuovaOfferta(String nuovaOfferta) {
		this.nuovaOfferta = nuovaOfferta;
	}

	public DettaglioComunicazioneType getComunicazione() {
		return comunicazione;
	}

	public void setComunicazione(DettaglioComunicazioneType comunicazione) {
		this.comunicazione = comunicazione;
	}

	public Integer getTipoOffertaTelematica() {
		return tipoOffertaTelematica;
	}

	public void setTipoOffertaTelematica(Integer tipoOffertaTelematica) {
		this.tipoOffertaTelematica = tipoOffertaTelematica;
	}

	public boolean isOffertaTelematica() {
		return offertaTelematica;
	}

	public void setOffertaTelematica(boolean offertaTelematica) {
		this.offertaTelematica = offertaTelematica;
	}

	public boolean isOffertaTecnica() {
		return offertaTecnica;
	}

	public void setOffertaTecnica(boolean offertaTecnica) {
		this.offertaTecnica = offertaTecnica;
	}

	public DettaglioComunicazioneType getComunicazioneBustaAmm() {
		return comunicazioneBustaAmm;
	}

	public void setComunicazioneBustaAmm(DettaglioComunicazioneType comunicazioneBustaAmm) {
		this.comunicazioneBustaAmm = comunicazioneBustaAmm;
	}

	public DettaglioComunicazioneType getComunicazioneBustaTec() {
		return comunicazioneBustaTec;
	}

	public void setComunicazioneBustaTec(DettaglioComunicazioneType comunicazioneBustaTec) {
		this.comunicazioneBustaTec = comunicazioneBustaTec;
	}

	public DettaglioComunicazioneType getComunicazioneBustaEco() {
		return comunicazioneBustaEco;
	}

	public void setComunicazioneBustaEco(DettaglioComunicazioneType comunicazioneBustaEco) {
		this.comunicazioneBustaEco = comunicazioneBustaEco;
	}

	public DettaglioComunicazioneType getComunicazioneBustaPreq() {
		return comunicazioneBustaPreq;
	}

	public void setComunicazioneBustaPreq(DettaglioComunicazioneType comunicazioneBustaPreq) {
		this.comunicazioneBustaPreq = comunicazioneBustaPreq;
	}

	public DettaglioComunicazioneType getComunicazioneDomandaPartecipazione() {
		return comunicazioneDomandaPartecipazione;
	}

	public void setComunicazioneDomandaPartecipazione(DettaglioComunicazioneType comunicazioneDomandaPartecipazione) {
		this.comunicazioneDomandaPartecipazione = comunicazioneDomandaPartecipazione;
	}

	public boolean isBustaAmmAlreadySent() {
		return bustaAmmAlreadySent;
	}

	public void setBustaAmmAlreadySent(boolean bustaAmmAlreadySent) {
		this.bustaAmmAlreadySent = bustaAmmAlreadySent;
	}

	public boolean isBustaTecAlreadySent() {
		return bustaTecAlreadySent;
	}

	public void setBustaTecAlreadySent(boolean bustaTecAlreadySent) {
		this.bustaTecAlreadySent = bustaTecAlreadySent;
	}

	public boolean isBustaEcoAlreadySent() {
		return bustaEcoAlreadySent;
	}

	public void setBustaEcoAlreadySent(boolean bustaEcoAlreadySent) {
		this.bustaEcoAlreadySent = bustaEcoAlreadySent;
	}

	public boolean isBustaPreqAlreadySent() {
		return bustaPreqAlreadySent;
	}

	public void setBustaPreqAlreadySent(boolean bustaPreqAlreadySent) {
		this.bustaPreqAlreadySent = bustaPreqAlreadySent;
	}

	public DettaglioComunicazioneType getComunicazioneBustaRie() {
		return comunicazioneBustaRie;
	}

	public void setComunicazioneBustaRie(DettaglioComunicazioneType comunicazioneBustaRie) {
		this.comunicazioneBustaRie = comunicazioneBustaRie;
	}
		
	public boolean isCostoFisso() {
		return costoFisso;
	}

	public void setCostoFisso(boolean costoFisso) {
		this.costoFisso = costoFisso;
	}
	
	public boolean isProtocollazioneMailFallita() {
		return protocollazioneMailFallita;
	}

	public void setProtocollazioneMailFallita(boolean protocollazioneMailFallita) {
		this.protocollazioneMailFallita = protocollazioneMailFallita;
	}
	
	public boolean isNoBustaAmministrativa() {
		return noBustaAmministrativa;
	}

	public void setNoBustaAmministrativa(boolean noBustaAmministrativa) {
		this.noBustaAmministrativa = noBustaAmministrativa;
	}

	/**
	 * costanti esposte da utilizzare nella jsp 
	 */
	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}

	public int getBUSTA_AMMINISTRATIVA() {
		return PortGareSystemConstants.BUSTA_AMMINISTRATIVA;
	}

	public int getBUSTA_TECNICA() {
		return PortGareSystemConstants.BUSTA_TECNICA;
	}

	public int getBUSTA_ECONOMICA() {
		return PortGareSystemConstants.BUSTA_ECONOMICA;
	}

	public int getBUSTA_PRE_QUALIFICA() {
		return PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
	}


	/**
	 * Apertura pagina di gestione dei prodotti
	 *
	 * @return
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		if(StringUtils.isEmpty(this.progressivoOfferta)) {
			this.progressivoOfferta = "1";
		}
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				String codGara = (StringUtils.isNotEmpty(this.codiceGara) 
							   	  ? this.codiceGara 
							  	  : this.codice);
				
				// inizializza la gestione delle buste 
				// NB: save e restore in sessione e' gestito dalla classe  
				GestioneBuste gestioneBuste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						codGara, 
						this.progressivoOfferta,
						this.operazione);
				
				// carica i dati dal servizio 
				// in base al tipo di presentazione (domanda di partecipazione o invio offerta)
				// carica le buste relative
				gestioneBuste.get();
				
				if( !gestioneBuste.isModalitaAggiudicazioneValida() && !gestioneBuste.isIndagineMercato() ) {
					// non c'e' modalita' di aggiudicazione e non e' un'indagine di mercato
					this.addActionError(this.getText("Errors.invioBuste.modalitaAggiudicazioneNonDefinita"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
				
				BustaPartecipazione bustaPartecipazione = gestioneBuste.getBustaPartecipazione();
				BustaPrequalifica bustaPrequalifica = gestioneBuste.getBustaPrequalifica();
				BustaAmministrativa bustaAmministrativa = gestioneBuste.getBustaAmministrativa(); 
				BustaTecnica bustaTecnica = gestioneBuste.getBustaTecnica();
				BustaEconomica bustaEconomica = gestioneBuste.getBustaEconomica();
				BustaRiepilogo bustaRiepilogo = gestioneBuste.getBustaRiepilogo();

				DettaglioGaraType gara = gestioneBuste.getDettaglioGara();
				this.tipoOffertaTelematica = gara.getDatiGeneraliGara().getTipoOffertaTelematica();
				this.offertaTelematica = gara.getDatiGeneraliGara().isOffertaTelematica();
				this.offertaTecnica = (boolean) this.bandiManager.isGaraConOffertaTecnica(this.codice);
				this.costoFisso = (boolean) (gara.getDatiGeneraliGara().getCostoFisso() == 1);
				this.noBustaAmministrativa = gara.getDatiGeneraliGara().isNoBustaAmministrativa();
				this.comunicazione = (bustaPartecipazione.getId() > 0 ? bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione() : null);
				this.comunicazioneBustaRie = (bustaRiepilogo.getId() > 0 ? bustaRiepilogo.getComunicazioneFlusso().getDettaglioComunicazione() : null);
				
				// nel caso di una gara RISTRETTA
				// recupero la comunicazione della domanda di partecipazione
				this.comunicazioneDomandaPartecipazione = null;
				if(bustaPartecipazione.getDomandaPartecipazione() != null && 
				   bustaPartecipazione.getDomandaPartecipazione().getId() > 0) 
				{
					this.comunicazioneDomandaPartecipazione = bustaPartecipazione.getDomandaPartecipazione().getDettaglioComunicazione();
				}
				
				// verifica se esiste la comunicazione FS11/FS10 in stato DA PROTOCOLLARE... 
				this.protocollazioneMailFallita = false;
				DettaglioComunicazioneType comunicazioneDaProtocollare = bustaPartecipazione
					.find(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
				if(comunicazioneDaProtocollare != null) {
					this.protocollazioneMailFallita = true;
				}
				
				// verifica se le buste sono gia' state inviate...
				if(bustaPrequalifica != null) {
					this.bustaPreqAlreadySent = bustaPrequalifica.isBustaInviata();
					if(bustaPrequalifica.getId() > 0) {
						this.comunicazioneBustaPreq = bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione();
					}
				}
				if(bustaAmministrativa != null) {
					this.bustaAmmAlreadySent = bustaAmministrativa.isBustaInviata();
					if(bustaAmministrativa.getId() > 0) {
						this.comunicazioneBustaAmm = bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione();
					}
				}
				if(bustaTecnica != null) {
					this.bustaTecAlreadySent = bustaTecnica.isBustaInviata();
					if(bustaTecnica.getId() > 0) {
						this.comunicazioneBustaTec = bustaTecnica.getComunicazioneFlusso().getDettaglioComunicazione();
					}
				}
				if(bustaEconomica != null) {
					this.bustaEcoAlreadySent = bustaEconomica.isBustaInviata();
					if(bustaEconomica.getId() > 0) {
						this.comunicazioneBustaEco = bustaEconomica.getComunicazioneFlusso().getDettaglioComunicazione();
					}
				}
			
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "open");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "open");
				this.addActionError(this.getText("Errors.invioBuste.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * @param multipartSaveDir the multipartSaveDir to set
	 */
	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

//	/**
//	 * Ritorna la directory per i file temporanei, prendendola da
//	 * struts.multipart.saveDir (struts.properties) se valorizzata correttamente,
//	 * altrimenti da javax.servlet.context.tempdir
//	 *
//	 * @return path alla directory per i file temporanei
//	 */
//	private File getTempDir() {
//		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), this.multipartSaveDir);
//	}

}
