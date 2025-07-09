package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSGareAppalto.AbilitazioniGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.OpenLottiDistintiAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.EspletGaraViewAccessoDocumentiLottiAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Action per le operazioni sui bandi. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class BandiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6369494589081286064L;
	
	private static final String STATO_GARA_IN_CORSO = "1";
	private static final String STATO_GARA_SOSPESA 	= "4";

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private INtpManager ntpManager;
	private IAsteManager asteManager;
	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private DettaglioGaraType dettaglioGara;
	private DettaglioBandoIscrizioneType dettaglioIscrizione;
	private AbilitazioniGaraType abilitazioniGara;
	private boolean invitoPresentato;
	private boolean abilitaRiepilogoOfferta;
	private boolean abilitaRiepilogoRichiesta;
	private boolean garaLotti;
	private LottoGaraType[] lotti;
	@Validate(EParamValidation.GENERIC)
	private String BDNCPUrl;
	private DocumentoAllegatoType[] attiDocumenti;
	@Validate(EParamValidation.DIGIT)
	private String numeroOrdineInvito;
	
	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numSoccorsiIstruttori;
	private int numComunicazioniInviate;
	private Long genere;
	@Validate(EParamValidation.ENTITA)
	private String entita;		// mepa | elenco
	
	private boolean abilitaRiepilogoAsta; 
	private boolean abilitaPartecipaAsta;
	private boolean abilitaRinunciaOfferta;
	private boolean abilitaRiepilogoRinunciaOfferta;
	private boolean abilitaAccessoDocumenti;

	@Validate(EParamValidation.CODICE)
	private String codiceFromLotto;
	
	// passati da DettaglioComunicazioneAction per tornare al dettaglio della comunicazione
	private Long idComunicazione;
	private Long idDestinatario;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	protected IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public IAsteManager getAsteManager() {
		return asteManager;
	}

	public void setAsteManager(IAsteManager asteManager) {
		this.asteManager = asteManager;
	}
	
	public ICustomConfigManager getCustomConfigManager() {
		return customConfigManager;
	}

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public DettaglioGaraType getDettaglioGara() {
		return dettaglioGara;
	}

	public void setDettaglioGara(DettaglioGaraType dettaglioGara) {
		this.dettaglioGara = dettaglioGara;
	}

	public AbilitazioniGaraType getAbilitazioniGara() {
		return abilitazioniGara;
	}

	public void setAbilitazioniGara(AbilitazioniGaraType abilitazioniGara) {
		this.abilitazioniGara = abilitazioniGara;
	}
	
	public boolean isGaraLotti() {
		return garaLotti;
	}

	public void setGaraLotti(boolean garaLotti) {
		this.garaLotti = garaLotti;
	}

	public LottoGaraType[] getLotti() {
		return lotti;
	}

	public void setLotti(LottoGaraType[] lotti) {
		this.lotti = lotti;
	}

	public String getBDNCPUrl() {
		return BDNCPUrl;
	}

	public void setBDNCPUrl(String bDNCPUrl) {
		BDNCPUrl = bDNCPUrl;
	}

	public DocumentoAllegatoType[] getAttiDocumenti() {
		return attiDocumenti;
	}

	public void setAttiDocumenti(DocumentoAllegatoType[] attiDocumenti) {
		this.attiDocumenti = attiDocumenti;
	}
	
	public boolean isInvitoPresentato() {
		return invitoPresentato;
	}

	public boolean isAbilitaRiepilogoOfferta() {
		return abilitaRiepilogoOfferta;
	}

	public void setAbilitaRiepilogoOfferta(boolean abilitaRiepilogoOfferta) {
		this.abilitaRiepilogoOfferta = abilitaRiepilogoOfferta;
	}

	public boolean isAbilitaRiepilogoRichiesta() {
		return abilitaRiepilogoRichiesta;
	}

	public void setAbilitaRiepilogoRichiesta(boolean abilitaRiepilogoRichiesta) {
		this.abilitaRiepilogoRichiesta = abilitaRiepilogoRichiesta;
	}

	public int getNumComunicazioniRicevute() {
		return numComunicazioniRicevute;
	}

	public void setNumComunicazioniRicevute(int numComunicazioniRicevute) {
		this.numComunicazioniRicevute = numComunicazioniRicevute;
	}

	public int getNumComunicazioniRicevuteDaLeggere() {
		return numComunicazioniRicevuteDaLeggere;
	}

	public void setNumComunicazioniRicevuteDaLeggere(int numComunicazioniRicevuteDaLeggere) {
		this.numComunicazioniRicevuteDaLeggere = numComunicazioniRicevuteDaLeggere;
	}

	public int getNumComunicazioniArchiviate() {
		return numComunicazioniArchiviate;
	}

	public void setNumComunicazioniArchiviate(int numComunicazioniArchiviate) {
		this.numComunicazioniArchiviate = numComunicazioniArchiviate;
	}

	public int getNumComunicazioniArchiviateDaLeggere() {
		return numComunicazioniArchiviateDaLeggere;
	}

	public void setNumComunicazioniArchiviateDaLeggere(int numComunicazioniArchiviateDaLeggere) {
		this.numComunicazioniArchiviateDaLeggere = numComunicazioniArchiviateDaLeggere;
	}
	
	public int getNumSoccorsiIstruttori() {
		return numSoccorsiIstruttori;
	}

	public void setNumSoccorsiIstruttori(int numSoccorsiIstruttori) {
		this.numSoccorsiIstruttori = numSoccorsiIstruttori;
	}

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}

	public void setNumComunicazioniInviate(int numComunicazioniInviate) {
		this.numComunicazioniInviate = numComunicazioniInviate;
	}
	
	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}
	
	public String getEntita() {
		return entita;
	}
	
	public void setEntita(String entita) {
		this.entita = entita;
	}

	public boolean isAbilitaRiepilogoAsta() {
		return abilitaRiepilogoAsta;
	}

	public void setAbilitaRiepilogoAsta(boolean abilitaRiepilogoAsta) {
		this.abilitaRiepilogoAsta = abilitaRiepilogoAsta;
	}

	public boolean isAbilitaPartecipaAsta() {
		return abilitaPartecipaAsta;
	}

	public void setAbilitaPartecipaAsta(boolean abilitaPartecipaAsta) {
		this.abilitaPartecipaAsta = abilitaPartecipaAsta;
	}

	public String getCodiceFromLotto() {
		return codiceFromLotto;
	}

	public void setCodiceFromLotto(String codiceFromLotto) {
		this.codiceFromLotto = codiceFromLotto;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}
	
	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public DettaglioBandoIscrizioneType getDettaglioIscrizione() {
		return dettaglioIscrizione;
	}

	public void setDettaglioIscrizione(DettaglioBandoIscrizioneType dettaglioIscrizione) {
		this.dettaglioIscrizione = dettaglioIscrizione;
	}

	public String getNumeroOrdineInvito() {
		return numeroOrdineInvito;
	}

	public void setNumeroOrdineInvito(String numeroOrdineInvito) {
		this.numeroOrdineInvito = numeroOrdineInvito;
	}
	
	public boolean isAbilitaRinunciaOfferta() {
		return abilitaRinunciaOfferta;
	}

	public void setAbilitaRinunciaOfferta(boolean abilitaRinunciaOfferta) {
		this.abilitaRinunciaOfferta = abilitaRinunciaOfferta;
	}
	
	public boolean isAbilitaRiepilogoRinunciaOfferta() {
		return abilitaRiepilogoRinunciaOfferta;
	}

	public void setAbilitaRiepilogoRinunciaOfferta(boolean abilitaRiepilogoRinunciaOfferta) {
		this.abilitaRiepilogoRinunciaOfferta = abilitaRiepilogoRinunciaOfferta;
	}
	
	public boolean isAbilitaAccessoDocumenti() {
		return abilitaAccessoDocumenti;
	}

	public void setAbilitaAccessoDocumenti(boolean abilitaAccessoDocumenti) {
		this.abilitaAccessoDocumenti = abilitaAccessoDocumenti;
	}

	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}
	

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio bando.
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		// NB: 
		// se si proviene dall'EncodedDataAction di InitIscrizione o dell'OpenGestioneBuste con un errore, 
		// devo resettare il target tanto va riaperta la pagina del bando
		resetTargetAfterActionChain();
		
		// apri il dettaglio del bando
		try {
			if(!StringUtils.isEmpty(this.codiceFromLotto)) {
				this.codice = this.codiceFromLotto;
			}
			
			DettaglioGaraType gara = this.getBandiManager().getDettaglioGara(this.codice);
			
			if(StringUtils.isNotEmpty(this.codice) && gara == null) {
				// se non si trova esito, gara, delibera o avviso si segnala un errore
				addActionMessage(this.getText("appalto.inDefinizione"));
				setTarget(CommonSystemConstants.PORTAL_ERROR);
				return this.getTarget();
			}
			
			if (isAccessoNonConsentitoDatiProcedura(gara, this.getCurrentUser())) {
				// blocco l'accesso ai dati quando si forza la url di accesso senza avere l'autorizzazione a consultare il dato
				this.addActionError(this.getText("Errors.notAllowed"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
//				boolean invitata = this.isInvitataAsta(gara);
//				gara.getDatiGeneraliGara().setInvitataAsta(invitata);
				gara.getDatiGeneraliGara().setInvitataAsta(false);
				
				boolean ristretta = WizardPartecipazioneHelper.isGaraRistretta(gara.getDatiGeneraliGara().getIterGara());
				boolean negoziata = WizardPartecipazioneHelper.isGaraNegoziata(gara.getDatiGeneraliGara().getIterGara());
				boolean rdo = WizardPartecipazioneHelper.isGaraRdo(gara.getDatiGeneraliGara().getIterGara());
				boolean sospesa = STATO_GARA_SOSPESA.equals(gara.getDatiGeneraliGara().getStato());
				
				this.setDettaglioGara(gara);
				
				garaLotti = false;
				if(gara != null && gara.getDatiGeneraliGara() != null) {
					garaLotti = 
						gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE
						|| (gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO);
				}
				
				// nel caso di utente loggato si estraggono le comunicazioni
				// personali dell'utente (sia per la gara che per i singoli lotti)
				UserDetails userDetails = this.getCurrentUser();
				if (null != userDetails
					&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

					// imposta il flag invitata ad asta
					boolean invitataAsta = this.isInvitataAsta(gara);
					gara.getDatiGeneraliGara().setInvitataAsta(invitataAsta);
					
					boolean busteDistinte = false;
					if(gara.getDatiGeneraliGara().getBusteDistinte() != null) {
						busteDistinte = gara.getDatiGeneraliGara().getBusteDistinte();
					}

//					boolean presentaPartecipazioneScaduta = false;
//					boolean presentaOffertaScaduta = false;

					// imposta le abilitazioni di gara
					AbilitazioniGaraType abilitazioni = this.getBandiManager()
									.getAbilitazioniGara(userDetails.getUsername(), codice);
					this.setAbilitazioniGara(abilitazioni);
					// serve a capire se, indipendentemente dalla data in cui si richiede la visualizzazione del dettaglio, sono stato invitato
					this.invitoPresentato = this.getAbilitazioniGara().isRichInvioOfferta();
					
					Date dataAttuale = null;
					try {
						// va rilevata l'ora ufficiale NTP per confrontarla con
						// la data termine in modo da abilitare eventuali
						// comandi dipendenti dal non superamento di tale data.
						// NB: se la data non viene rilevata, l'abilitazione dei
						// comandi dipende dal test sulla data effettuato nel
						// dbms server con la sua data di sistema
						dataAttuale = this.ntpManager.getNtpDate();
					} catch (Exception e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}

					if (dataAttuale != null && 
						gara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda() != null) 
					{
						try {
							if (dataAttuale.compareTo(
									InitIscrizioneAction.calcolaDataOra(
										gara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
										gara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda(), true)) > 0) {
								this.getAbilitazioniGara().setRichPartecipazione(false);
							}
						} catch (Exception e) {
							// non si fa niente, si usano i dati ricevuti dal
							// servizio e quindi i test effettuati nel dbms server
						}
					}
					
					if (dataAttuale != null && 
						gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null) 
					{
						try {
							if (dataAttuale.compareTo(
									InitIscrizioneAction.calcolaDataOra(
										gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
										gara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),	true)) > 0) {
								this.getAbilitazioniGara().setRichInvioOfferta(false);
							}
						} catch (Exception e) {
							// non si fa niente, si usano i dati ricevuti dal
							// servizio e quindi i test effettuati nel dbms server
						}
					}
					
					
					List<DettaglioComunicazioneType> dettComunicazioni;
					List<String> stati = new ArrayList<String>();
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_SCARTATA);
					
					// controllo comunicazione in stato 5 e setto
					// DOMANDA PARTECIPAZIONE
					boolean abilitaRiepilogo = false;
					dettComunicazioni = null;
					if (gara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda() != null
						&& gara.getDatiGeneraliGara().isProceduraTelematica()) 
					{
						dettComunicazioni = ComunicazioniUtilities.retrieveComunicazioniConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice, 
										PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT, 
										stati);
						
						// per gare a lotti il riepilogo si abilita solo alla scadenza dei termini
						if(garaLotti && busteDistinte) {
							if(!this.getAbilitazioniGara().isRichPartecipazione()) {
								abilitaRiepilogo = (dettComunicazioni != null && dettComunicazioni.size() > 0);
							}
						} else {
							abilitaRiepilogo = (dettComunicazioni != null);
						}

						this.getAbilitazioniGara().setRichPartecipazione(this.getAbilitazioniGara().isRichPartecipazione() && !abilitaRiepilogo);					
					}
					this.abilitaRiepilogoRichiesta = (abilitaRiepilogo); 
					
					// RICHIESTA OFFERTA
					abilitaRiepilogo = false;
					dettComunicazioni = null;
					if (gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null
						&& gara.getDatiGeneraliGara().isProceduraTelematica()) 
					{
						dettComunicazioni = ComunicazioniUtilities.retrieveComunicazioniConStati(
										this.comunicazioniManager,
										this.getCurrentUser().getUsername(),
										this.codice, 
										PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT, 
										stati);
						
						// per gare a lotti il riepilogo si abilita solo alla scadenza dei termini
						if(garaLotti && busteDistinte) {
							if(!this.getAbilitazioniGara().isRichInvioOfferta()) {
								abilitaRiepilogo = (dettComunicazioni != null && dettComunicazioni.size() > 0);
							}
						} else {
							abilitaRiepilogo = (dettComunicazioni != null);
						}

						this.getAbilitazioniGara().setRichInvioOfferta(this.getAbilitazioniGara().isRichInvioOfferta() && !abilitaRiepilogo);
					}
					this.abilitaRiepilogoOfferta = (abilitaRiepilogo);

					// In base al genere scelgo come richiamare le statistiche sulle comunicazioni
					StatisticheComunicazioniPersonaliType stat = null;
					
					if(gara.getDatiGeneraliGara().getTipologia() ==  1) {
						stat = this.getBandiManager().getStatisticheComunicazioniPersonaliGaraLotti(
								this.getCurrentUser().getUsername(), 
								this.codice,
								null);
					} else {
						stat = this.getBandiManager().getStatisticheComunicazioniPersonali(
								this.getCurrentUser().getUsername(), 
								this.codice, null,
								null);	
					}
					 
					this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
					this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
					this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
					this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
					this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
					this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
					
					this.setGenere(this.getBandiManager().getGenere(this.codice));
					
					// verifica se l'utente è invitato e ammesso all'asta...
					// in caso di gara a lotti verifica se l'utente è invitato/ammesso 
					// ad almento un'asta di un lotto
					boolean abilitataAsta = this.isAbilitataAsta(gara);
					
					this.abilitaPartecipaAsta = abilitataAsta;
					
					this.abilitaRiepilogoAsta = false;
					if(abilitataAsta
					   && dataAttuale != null && gara.getDatiGeneraliGara().getDataInizioAsta() != null) {
						try {
							this.abilitaRiepilogoAsta = true;
							if (dataAttuale.compareTo(gara.getDatiGeneraliGara().getDataInizioAsta().getTime()) < 0) {
								this.abilitaRiepilogoAsta = false;
							}
						} catch (Exception e) {
							// non si fa niente, si usano i dati ricevuti dal
							// servizio e quindi i test effettuati nel dbms server
						}
					}
					
					// numero ordine invito per gare negoziate
					this.numeroOrdineInvito = null;
					if (ristretta && 
						this.customConfigManager.isVisible("GARE", "NUMORDINEINVITO")) 
					{
						Long n = this.bandiManager.getNumeroOrdineInvito(
								this.getCurrentUser().getUsername(),
								this.codice);
						this.numeroOrdineInvito = (n != null && n >= 0 ? n.toString() : null);
					}
					
					// rinuncia partecipazione offerta (FS14)
					// se l'offerta non e' stata inviata allora verifica se visualizzare la rinuncia
					this.abilitaRinunciaOfferta = false;
					this.abilitaRiepilogoRinunciaOfferta = false;
					if(this.getAbilitazioniGara().isRichInvioOfferta() && !this.abilitaRiepilogoOfferta) {
						if(this.customConfigManager.isActiveFunction("GARE", "RINUNCIA")
						   && ((ristretta || negoziata || rdo) && !sospesa))
						{
							DettaglioComunicazioneType dettCom = ComunicazioniUtilities.retrieveComunicazione(
									this.comunicazioniManager,
									this.getCurrentUser().getUsername(),
									this.codice, 
									null,
									null,
									PortGareSystemConstants.RICHIESTA_RINUNCIA_PARTECIPAZIONE_OFFERTA);
							boolean rinunciaPresente = (dettCom != null);

							if(garaLotti) {
								// 1 offerta per plico con N plichi
								// se e' presente anche solo 1 offerta allora si disabilita la rinuncia...
								if(rinunciaPresente) {
									this.abilitaRinunciaOfferta = false;
									this.abilitaRiepilogoRinunciaOfferta = true;
								} else {
									// se esiste almeno un'offerta allora si disabilitano rinuncia e riepilogo rinuncia
									int offerte = (busteDistinte && dettComunicazioni != null ? dettComunicazioni.size() : 0);
									this.abilitaRinunciaOfferta = (offerte <= 0);
									this.abilitaRiepilogoRinunciaOfferta = false;
								}
							} else {
								// gara NON a lotti
								this.abilitaRinunciaOfferta = (dettCom == null);
								this.abilitaRiepilogoRinunciaOfferta = !this.abilitaRinunciaOfferta;
							}
							
							if(rinunciaPresente) {
								// esiste la rinucia di offerta per la gara
								this.abilitazioniGara.setRichInvioOfferta(false);
								//this.abilitazioniGara.setRichComprovaRequisiti(false);	???
								//this.abilitazioniGara.setRichPartecipazione(false);		???
							}
						}
					}
					
					// abilita il link per l'accesso alla documentazione ART.36
					abilitaAccessoDocumenti = false;
					List<EspletGaraOperatoreType> ammessePubblicazione = getAmmessePubblicazioneGara(
							codice, 
							garaLotti, 
							getCurrentUser().getUsername()
					);
					if(ammessePubblicazione != null && ammessePubblicazione.size() > 0) {
						// abilita l'accesso alla pubblicazione solo se l'utente e' tra le classificate
						// NB: dovrebbe restituire solo l'impresa relativa all'utente corrente
						abilitaAccessoDocumenti = true;
					}
					
					// nel caso il bando venga aperto da una comunicazione (inviata/ricevuta)
					// in sessione vengono memorizzati idComunicazione, idDestinatario per 
					// poter tornare al dettaglio della comunicazione
					if(this.idComunicazione == null) {
						this.idComunicazione = (Long)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDCOMUNICAZIONE);
						this.idDestinatario = (Long)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDDESTINATARIO);
					}
					this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDCOMUNICAZIONE);
					this.session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDDESTINATARIO);
					
					// metti in sessione i dati per la gestione delle comunicazioni (DettaglioComunicazioneAction)
					if(this.idComunicazione != null) {
						this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDCOMUNICAZIONE, this.idComunicazione);
						this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDDESTINATARIO, this.idDestinatario);
					}
					this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
					this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
					this.session.put("dettaglioPresente", true);
				}
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "view");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String viewLotti() {
		try {
			DettaglioGaraType gara = this.getBandiManager().getDettaglioGara(this.codice);

			if (isAccessoNonConsentitoDatiProcedura(gara, getCurrentUser())) {
				// blocco l'accesso ai dati quando si forza la url di accesso senza avere l'autorizzazione a consultare il dato
				this.addActionError(this.getText("Errors.notAllowed"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// popolo i dati da visualizzare
				this.setDettaglioGara(gara);
				LottoGaraType[] lottigara = this.getBandiManager().getLottiGara(
								this.codice);
				for(int i = 0; i < lottigara.length; i++) {
					for(int j = 0; j < gara.getLotto().length; j++) {
						if( gara.getLotto(j).getCodiceLotto().equals(lottigara[i].getCodiceLotto()) ) {
							lottigara[i].setSoggettiAderenti(gara.getLotto(j).getSoggettiAderenti());
							break;
						}
					}	
				}
				this.setLotti(lottigara);
			}

		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewLotti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Verifica se esistono o meno tutte le condizioni per poter accedere ad un
	 * dettaglio procedura ed ai dati in esso contenuti. Lo scopo &egrave;
	 * bloccare l'accesso alla consultazione delle procedure negoziate in corso
	 * di ricezione offerte nel caso di utenti non autenticati oppure non
	 * invitati.
	 * 
	 * @param gara
	 *            procedura di gara
	 * @param username
	 *            procedura di gara
	 * @return true se l'accesso non &egrave; consentito, false altrimenti
	 * @throws ApsException
	 */
	public static boolean isAccessoNonConsentitoDatiProcedura(DettaglioGaraType gara, UserDetails user)
		throws ApsException 
	{
		// 14/03/2018: sicurezza accesso al dettaglio. Si puo' accedere al
		// dettaglio solo se autorizzati, pertanto si recuperano e si
		// impostano delle variabili per poter controllare l'accesso o il
		// blocco alla scheda di dettaglio
		//
		// il set di inizializzazioni qui sotto riportate servono a far
		// fallire i controlli finali e a non aprire il dettaglio se non
		// vengono variati i valori nella logica intermedia del metodo
		boolean isNegoziata = false;
		boolean isInCorso = false;
		boolean isSospesa = false;
		boolean isAccessoAnonimo = true;
		boolean isInvitata = false;
		if(gara != null &&  gara.getDatiGeneraliGara() != null) {
			isNegoziata = WizardPartecipazioneHelper.isGaraNegoziata(gara.getDatiGeneraliGara().getIterGara());
			isInCorso = STATO_GARA_IN_CORSO.equals(gara.getDatiGeneraliGara().getStato());
			isSospesa = STATO_GARA_SOSPESA.equals(gara.getDatiGeneraliGara().getStato());
			String codice = gara.getDatiGeneraliGara().getCodice();
		
			if (StringUtils.isNotEmpty(codice)
				&& user != null 
				&& !user.getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
			{
				// imposto il flag
				isAccessoAnonimo = false;
				
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
				IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
				INtpManager ntpManager = (INtpManager) ctx.getBean(CommonSystemConstants.NTP_MANAGER);
	
				// verifica le abilitazioni di gara per l'utenza
				AbilitazioniGaraType abilitazioni = bandiManager.getAbilitazioniGara(user.getUsername(), codice);
				isInvitata = abilitazioni.isRichInvioOfferta();
				
				Date dataAttuale = null;
				try {
					// va rilevata l'ora ufficiale NTP per confrontarla con
					// la data termine in modo da abilitare eventuali
					// comandi dipendenti dal non superamento di tale data.
					// NB: se la data non viene rilevata, l'abilitazione dei
					// comandi dipende dal test sulla data effettuato nel
					// dbms server con la sua data di sistema
					dataAttuale = ntpManager.getNtpDate();
				} catch (Exception e) {
					// non si fa niente, si usano i dati ricevuti dal
					// servizio e quindi i test effettuati nel dbms server
				}
	
				if (dataAttuale != null && 
					gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null) 
				{
					try {
						if (dataAttuale.compareTo(
								InitIscrizioneAction.calcolaDataOra(
									gara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
									gara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),	true)) > 0) {
							isInCorso = false;
						} else {
							isInCorso = true;
						}
					} catch (Exception e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}
			}
		}
		
		return isNegoziata && isInCorso && (isAccessoAnonimo || !isInvitata);
	}
		
	/**
	 * ... 
	 */
	public String viewFromLotto() {
		try {
			// prima recupera il codice della gara dal dettaglio estratto dal
			// lotto...
			// ...poi con il codice della gara ricavato si può riutilizzare 
			// la gestione standard per visualizzazione della gara
			DettaglioGaraType gara = this.getBandiManager()
				.getDettaglioGaraFromLotto(this.codice);
			if(gara != null && gara.getDatiGeneraliGara() != null) {
//				this.codice = gara.getDatiGeneraliGara().getCodice();
//				this.setTarget( this.view() );
				this.codiceFromLotto = gara.getDatiGeneraliGara().getCodice();
				this.setTarget(SUCCESS);
			} else {
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewFromLotto");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * visualizza schefa ANAC 
	 */
	public String viewBDNCP() {
		try {
			DettaglioGaraType gara = this.getBandiManager().getDettaglioGara(this.codice);
			if (isAccessoNonConsentitoDatiProcedura(gara, getCurrentUser())) {
				// blocco l'accesso ai dati quando si forza la url di accesso senza avere l'autorizzazione a consultare il dato
				this.addActionError(this.getText("Errors.notAllowed"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// popolo i dati da visualizzare
				this.setDettaglioGara(gara);
				this.setLotti(this.getBandiManager().getLottiGara(this.codice));
				this.setBDNCPUrl( (String)appParamManager.getConfigurationValue(AppParamManager.BDNCP_TEMPLATE_URL) );				
			}

		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewDatiApertiBDNCP");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * visualizza atti e documenti di un bando 
	 */
	public String viewAttiDocumenti() {
		try {
			DettaglioGaraType gara = this.getBandiManager().getDettaglioGara(this.codice);
			if (isAccessoNonConsentitoDatiProcedura(gara, getCurrentUser())) {
				// blocco l'accesso ai dati quando si forza la url di accesso senza avere l'autorizzazione a consultare il dato
				this.addActionError(this.getText("Errors.notAllowed"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// popolo i dati da visualizzare
				this.setDettaglioGara(gara);
				DocumentoAllegatoType[] attiDocs = this.getBandiManager().getAttiDocumentiBandoGara(this.codice);
				this.setAttiDocumenti(attiDocs);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewAttiDocumenti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * visualizza altri documenti per un elenco
	 */
	public String viewAltriDocumenti() {
		try {
			DettaglioBandoIscrizioneType bando = this.getBandiManager().getDettaglioBandoIscrizione(this.codice);
//			if (isAccessoNonConsentitoDatiProcedura(gara)) {
//				// blocco l'accesso ai dati quando si forza la url di accesso senza avere l'autorizzazione a consultare il dato
//				this.addActionError(this.getText("Errors.notAllowed"));
//				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
//			} else {
				// popolo i dati da visualizzare
				this.setDettaglioIscrizione(bando);
				DocumentoAllegatoType[] attiDocs = this.getBandiManager().getAttiDocumentiBandoGara(this.codice);
				this.setAttiDocumenti(attiDocs);
//			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewAltriDocumenti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Verifica se esiste l'invito ad un'asta per una data gara per un dato utente  
	 */
	private boolean isInvitataAsta(DettaglioGaraType gara) {
		boolean invitata = false;
		try {	
			// verifica l'invito solo se l'utente e' loggato...
			UserDetails userDetails = this.getCurrentUser();
			if(null != userDetails && 
			   !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				// ...l'utente e' loggato...
				invitata = this.getBandiManager().isInvitataAsta(
						gara.getDatiGeneraliGara().getCodice(), 
						userDetails.getUsername());
			}
		} catch(Exception t) {
			ApsSystemUtils.logThrowable(t, this, "isInvitataAsta");
			ExceptionUtils.manageExceptionError(t, this);
		}			
		return invitata;
	}
	
	private boolean isAbilitataAsta(DettaglioGaraType gara) {
		boolean invitata = false;
		try {
			List<DettaglioAstaType> listaAste =  OpenLottiDistintiAction.getLottiInvitatiAsta(
					this.bandiManager, 
					this.asteManager, 
					gara, 
					this.getCurrentUser().getUsername());
			
			if(gara.getLotto() != null && gara.getLotto().length > 0) {
				// --- GARA A LOTTI ---
				invitata = (listaAste.size() > 0);
			} else {
				// --- GARA SEMPLICE ---
				invitata = (listaAste.size() > 0);
			}
		} catch (Exception t) {
			ApsSystemUtils.logThrowable(t, this, "isAbilitataAsta");
			ExceptionUtils.manageExceptionError(t, this);
		}
		return invitata;
	}	
	
	/**
	 * restituisce l'elenco delle ammesse alla pubblicazione atti (art 36)  
	 */
	private List<EspletGaraOperatoreType> getAmmessePubblicazioneGara(String codiceGara, boolean garaLotti, String username) {
		List<EspletGaraOperatoreType> ammesse = null;
		try {
			if( !garaLotti ) {
				// gara NO lotti
				// recupera le classificate della la gara
				if(dettaglioGara.getDatiGeneraliGara().getDataPubblicazioneAtti() != null) {
					EspletamentoElencoOperatoriSearch search = new EspletamentoElencoOperatoriSearch();
					search.setCodice(codice);
					search.setUsername(username);
					List<EspletGaraOperatoreType> imprese = bandiManager.getEspletamentoGaraAccessoDocumentiElencoOperatori(search);							
					if(imprese != null)	{
						ammesse = new ArrayList<EspletGaraOperatoreType>();
						ammesse = imprese;
					}
				}
			} else {
				// gare a lotti
				ammesse = EspletGaraViewAccessoDocumentiLottiAction
						.getAmmessePubblicazioneAtti(getCurrentUser().getUsername(), codiceGara);
			}			
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getAmmessePubblicazioneGara");
		}
		return ammesse;
	}
	
	/**
	 * se si proviene dall'EncodedDataAction di InitIscrizione o dell'OpenGestioneBuste con un errore, 
	 * devo resettare il target tanto va riaperta la pagina del bando
	 */
	private void resetTargetAfterActionChain() {
		if ("block".equals(getTarget()) 
			|| "successBando".equalsIgnoreCase(getTarget())
			|| "backBando".equalsIgnoreCase(getTarget()))
			setTarget(SUCCESS);
	}
	
}
