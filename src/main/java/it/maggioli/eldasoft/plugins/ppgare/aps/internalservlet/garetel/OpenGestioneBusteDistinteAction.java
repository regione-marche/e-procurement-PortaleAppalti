package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

/**
 * ... 
 */
public class OpenGestioneBusteDistinteAction extends OpenGestioneBusteAction implements SessionAware{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -1913545349959529280L;
	
	private static final String SESSION_ID_WIZARD_OFFERTA = "wizardOfferta";
	private static final String SESSION_ID_DETTAGLIO_GARA = "dettaglioGara";
	
	private boolean almenoUnaBustaTecnica;
	private boolean almenoUnaBustaEconomica;
	private HashMap<String, Boolean> wizardOfferta = new LinkedHashMap<String, Boolean>();
	private boolean nessunDocPerPreq;
	private boolean nessunDocPerAmm;
	private boolean nessunDocPerTec;
	private boolean nessunDocPerEco;
	private Long lottiSelezionati;
	
	public boolean isAlmenoUnaBustaTecnica() {
		return almenoUnaBustaTecnica;
	}

	public void setAlmenoUnaBustaTecnica(boolean almenoUnaBustaTecnica) {
		this.almenoUnaBustaTecnica = almenoUnaBustaTecnica;
	}

	public boolean isAlmenoUnaBustaEconomica() {
		return almenoUnaBustaEconomica;
	}

	public void setAlmenoUnaBustaEconomica(boolean almenoUnaBustaEconomica) {
		this.almenoUnaBustaEconomica = almenoUnaBustaEconomica;
	}

	public HashMap<String, Boolean> getWizardOfferta() {
		return wizardOfferta;
	}

	public void setWizardOfferta(HashMap<String, Boolean> wizardOfferta) {
		this.wizardOfferta = wizardOfferta;
	}

	public boolean isNessunDocPerPreq() {
		return nessunDocPerPreq;
	}

	public void setNessunDocPerPreq(boolean nessunDocPerPreq) {
		this.nessunDocPerPreq = nessunDocPerPreq;
	}

	public boolean isNessunDocPerAmm() {
		return nessunDocPerAmm;
	}

	public void setNessunDocPerAmm(boolean nessunDocPerAmm) {
		this.nessunDocPerAmm = nessunDocPerAmm;
	}

	public boolean isNessunDocPerTec() {
		return nessunDocPerTec;
	}

	public void setNessunDocPerTec(boolean nessunDocPerTec) {
		this.nessunDocPerTec = nessunDocPerTec;
	}

	public boolean isNessunDocPerEco() {
		return nessunDocPerEco;
	}

	public void setNessunDocPerEco(boolean nessunDocPerEco) {
		this.nessunDocPerEco = nessunDocPerEco;
	}
	
	public Long getLottiSelezionati() {
		return lottiSelezionati;
	}

	public void setLottiSelezionati(Long lottiSelezionati) {
		this.lottiSelezionati = lottiSelezionati;
	}

	
	public int getBUSTA_PRE_QUALIFICA() {
		return PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
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
	
	
	/**
	 * Apertura pagina di gestione delle offerte a buste distinte
	 *
	 * @return
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				String codGara = (StringUtils.isNotEmpty(this.codiceGara) ? this.codiceGara : this.codice );
				
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
				
				// SERVE ANCORA QUESTO ???
//				if(StringUtils.stripToNull(this.codice) != null 
//					&& StringUtils.stripToNull(this.codiceGara) == null) 
//				{
//					// fatto cosi' per gestire il ritorno all'openGestioneBusteDistinte 
//					// da wizard inizio compilazione offerta...
//					// e nel caso in cui codiceGara non venga passato dall'ultimo helper\wizard!!! 
//					this.codiceGara = this.getCodice();
//				}
				this.codiceGara = gestioneBuste.getCodiceGara();
				
				// pulizia della sessione dall'accesso ad una gara precedente 
				// che potrebbe essere stata a offerta unica
				gestioneBuste.resetSession();
				gestioneBuste.putToSession();
				
				// inizializza i dati per la gestione della pagina del menu
				DettaglioGaraType gara = gestioneBuste.getDettaglioGara();
				bustaPartecipazione.getHelper().setPlicoUnicoOfferteDistinte(true);
				this.setOffertaTelematica(gara.getDatiGeneraliGara().isOffertaTelematica());
				this.setOffertaTecnica((boolean) this.bandiManager.isGaraConOffertaTecnica(this.codiceGara));
				this.setNoBustaAmministrativa(gara.getDatiGeneraliGara().isNoBustaAmministrativa());
				
				// aggiorna il numero dei lotti selezionati nello step di partecipazione
				this.lottiSelezionati = 0L;
				List<String> lotti = bustaPartecipazione.getLottiAttivi();
				if(lotti != null) {
					this.lottiSelezionati = new Long(lotti.size());
				}

				// recupera la comunicazione di partecipazione FS11/FS10
				this.setComunicazione(null);
				if(bustaPartecipazione.getId() > 0 ) { 
					this.setComunicazione(bustaPartecipazione.getComunicazioneFlusso().getDettaglioComunicazione());
				}

				// verifica se le buste amministrativa, tecnica, economica sono già state inviate
				// ed imposta i flag bustaAmmAlreadySent, bustaTecAlreadySent, BustaEcoAlreadySent
				// per l'abilitazione nel menu dei pulsanti delle buste
				this.setBustaPreqAlreadySent(false);
				this.setBustaAmmAlreadySent(false);
				this.setBustaTecAlreadySent(false);
				this.setBustaEcoAlreadySent(false);
				this.setAlmenoUnaBustaTecnica(false);
				this.setAlmenoUnaBustaEconomica(false);
				
				// verifica se le buste sono gia' state inviate...
				if(bustaPrequalifica != null) {
					this.setBustaPreqAlreadySent( bustaPrequalifica.isBustaInviata() );
				}
				if(bustaAmministrativa != null) {
					this.setBustaAmmAlreadySent( bustaAmministrativa.isBustaInviata() );
				}
				if(bustaTecnica != null) {
					this.setBustaTecAlreadySent( bustaTecnica.isBusteLottiInviate() );
				}
				if(bustaEconomica != null) {
					this.setBustaEcoAlreadySent( bustaEconomica.isBusteLottiInviate() );
				}
				
				// verifica se esiste la comunicazione per la busta di prequalifica FS10A
				this.setComunicazioneBustaPreq(null);
				if(bustaPrequalifica != null && bustaPrequalifica.getId() > 0) {
					this.setComunicazioneBustaPreq( bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione() );
				}
				
				// verifica se esiste la comunicazione per la busta amministrativa FS11A
				this.setComunicazioneBustaAmm(null);
				if(bustaAmministrativa != null && bustaAmministrativa.getId() > 0) {
					this.setComunicazioneBustaAmm( bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione() );
				}

				// se non esiste la comunicazione FS11R/FS10R allora non si 
				// e' ancora confermata lo step di "Inizia compilazione offerta"
				// e il pulsante "Busta economica" non va nascosto, 
				// finche' non sono stati confermati i lotti
				if(gara.getLotto().length > 1 && bustaRiepilogo.getId() <= 0) {
					this.setAlmenoUnaBustaEconomica(true);
				}

				if(this.getComunicazione() != null) {
					this.setAlmenoUnaBustaTecnica(false);
					this.setAlmenoUnaBustaEconomica(false);
					
					List<String> lottiAttivi = bustaPartecipazione.getLottiAttivi();
					for(int i = 0; i < lottiAttivi.size(); i++) {
						this.wizardOfferta.put(lottiAttivi.get(i), gara.getDatiGeneraliGara().isOffertaTelematica());
					}
					this.session.put(SESSION_ID_WIZARD_OFFERTA, this.wizardOfferta);
					
					this.setAlmenoUnaBustaTecnica(bustaRiepilogo.isAlmenoUnaBustaTecnica());
					this.setAlmenoUnaBustaEconomica(bustaRiepilogo.isAlmenoUnaBustaEconomica());
				}
				
				// verifica lo stato stato sulla presenza dei documenti per le varie buste 
				this.nessunDocPerPreq = true;
				this.nessunDocPerAmm = true;
				this.nessunDocPerTec = true;
				this.nessunDocPerEco = true;
                if (bustaRiepilogo.getHelper().getBustaPrequalifica() != null) {
                    this.nessunDocPerPreq = bustaRiepilogo.getHelper().getBustaPrequalifica().getDocumentiInseriti().isEmpty();
                }
                if (bustaRiepilogo.getHelper().getBustaAmministrativa() != null) {
                    this.nessunDocPerAmm = bustaRiepilogo.getHelper().getBustaAmministrativa().getDocumentiInseriti().isEmpty();
                }
                for (RiepilogoBustaBean lotto : bustaRiepilogo.getHelper().getBusteTecnicheLotti().values()) {
                	if(lotto != null) {
                		this.nessunDocPerTec = this.nessunDocPerTec && lotto.getDocumentiInseriti().isEmpty();
                	}
                }
                for (RiepilogoBustaBean lotto : bustaRiepilogo.getHelper().getBusteEconomicheLotti().values()) {
                	if(lotto != null) {
                		this.nessunDocPerEco = this.nessunDocPerEco && lotto.getDocumentiInseriti().isEmpty();
                	}
                }

				// verifica se sono stati tentati degli invii con protocollazione
				this.setProtocollazioneMailFallita(false);
				DettaglioComunicazioneType comunicazioneDaProtocollare = bustaPartecipazione
					.find(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
				if(comunicazioneDaProtocollare != null) {
					this.setProtocollazioneMailFallita(true);
				}

			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "open");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException ex) {
				ApsSystemUtils.logThrowable(ex, this, "open");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}catch (Throwable e) {
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

}
