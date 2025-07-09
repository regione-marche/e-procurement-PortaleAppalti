package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Action di gestione delle operazioni nella pagina della selezione dei lotti
 * del wizard di partecipazione al bando di gara
 * 
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class OpenPageLottiAction extends BaseAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private Map<String, Object> session;
    private IBandiManager bandiManager;
    private IComunicazioniManager comunicazioniManager;

    /**
     * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
     * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
     * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
     * valorizzato diversamente, vuol dire che si proviene dalla normale
     * navigazione tra le pagine del wizard
     */
	@Validate(EParamValidation.GENERIC)
    private String page;

    private LottoGaraType[] lotti;

    // dati che, se popolati, dipendono dal fatto che si vuole ricaricare la
    // pagina in seguito a dei controlli falliti
    
    private String[] lottoSelezionato;

    /** Array di appoggio per la pagina JSP per indicare lo stato dei checkbox */
    private boolean[] checkLotto;

    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    public void setBandiManager(IBandiManager bandiManager) {
    	this.bandiManager = bandiManager;
    }

    public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public LottoGaraType[] getLotti() {
    	return lotti;
    }

    public String[] getLottoSelezionato() {
    	return lottoSelezionato;
    }

    public void setLottoSelezionato(String[] lottoSelezionato) {
    	this.lottoSelezionato = lottoSelezionato;
    }

    public boolean[] getCheckLotto() {
    	return checkLotto;
    }

    public void setPage(String page) {
    	this.page = page;
    }

    /**
     * ...
     */
	public String openPage() {
		String target = SUCCESS;
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
		WizardPartecipazioneHelper partecipazioneHelper = bustaPartecipazione.getHelper(); 

		if (partecipazioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PARTGARA_PAGINA_LOTTI);
			try {
				boolean domandaPartecipazione = buste.isDomandaPartecipazione();
				boolean ristretta = buste.isRistretta();
				
				LottoGaraType[] lottiGara = null;
				
				switch (partecipazioneHelper.getTipoEvento()) {
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
					lottiGara = this.bandiManager.getLottiGaraPerDomandePartecipazione(
							partecipazioneHelper.getIdBando(),
							null);
					break;
					
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
					if(!partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
						lottiGara = this.bandiManager.getLottiGaraPerRichiesteOfferta(
								this.getCurrentUser().getUsername(),
								partecipazioneHelper.getIdBando(),
								null);
					} else {
						lottiGara = this.bandiManager.getLottiGaraPlicoUnicoPerRichiesteOfferta(
								this.getCurrentUser().getUsername(),
								partecipazioneHelper.getIdBando(),
								null);
						if(lottiGara != null) {
							for(int i = 0; i < lottiGara.length; i++) {
								partecipazioneHelper.getOggettiLotti().add(lottiGara[i].getOggetto());
							}
						}
					}
					
					// se non e' mai stata aperta la compilazione dell'offerta,
					// verifica se e' stato presentata una partecipazione di prequalifica per questa gara...
					DettaglioComunicazioneType comunicazione = ComunicazioniUtilities.retrieveComunicazione(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(),
							partecipazioneHelper.getIdBando(), 
							partecipazioneHelper.getProgressivoOfferta(),
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT);

					DettaglioComunicazioneType comunicazionePreq = ComunicazioniUtilities.retrieveComunicazione(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(),
							partecipazioneHelper.getIdBando(), 
							partecipazioneHelper.getProgressivoOfferta(),
							CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA,
							PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
					
					if(comunicazione == null && comunicazionePreq != null) {
						// non e' ancora stata compilata l'offerta ed
						// e' presente la prequalifica (FS10 in stato 6)
						// quindi proponi tutti i lotti di gara selezionati
						for (int i = 0; i < lottiGara.length; i++) {
							if(!partecipazioneHelper.getLotti().contains(lottiGara[i].getCodiceLotto())) {
								partecipazioneHelper.getLotti().add(lottiGara[i].getCodiceLotto());
							}
						}
					}
					
					break;
					
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
					lottiGara = this.bandiManager.getLottiGaraPerRichiesteCheckDocumentazione(
							this.getCurrentUser().getUsername(),
							partecipazioneHelper.getIdBando());
					break;
				}
				this.lotti = lottiGara;
				
				// Gare ristrette a lotti (iter gara = 2 | 4 )
				// per la gestione della forma di partecipazione singola/rti per lotto
				// i lotti presentati in offerta dipendono dal tipo di partecipazioni  
				if(ristretta && !domandaPartecipazione) {
					this.getLottiPrequalificaOfferteMultiple(
							partecipazioneHelper,
							this.getCurrentUser().getUsername(),
							lottiGara);
				}

				// aggiorna lo stato dei check per i lotti presentati nella pagina
				if(this.lotti != null) {
					boolean[] tmpCheckLotto = new boolean[this.lotti.length];
					if (PortGareSystemConstants.WIZARD_PARTGARA_PAGINA_LOTTI.equals(this.page)) {
						// si arriva dalla pagina stessa, quindi se la si deve
						// ricaricare vuol dire che si e' verificato qualche
						// errore in fase di controllo e pertanto vanno ripresentati
						// i dati inseriti
						Set<String> setItemSelezionati = new HashSet<String>();
						if (this.lottoSelezionato != null)
							setItemSelezionati.addAll(Arrays.asList(this.lottoSelezionato));
	
						int cont = 0;
						for (int i = 0; i < this.lotti.length; i++) {
							tmpCheckLotto[cont++] = setItemSelezionati.contains(this.lotti[i].getCodiceLotto());
						}
					} else {
						// si arriva da un'altra pagina e pertanto si presentano
						// i dati leggendoli dalla sessione
						int cont = 0;
						for (int i = 0; i < this.lotti.length; i++) {
							tmpCheckLotto[cont++] = partecipazioneHelper.getLotti().contains(this.lotti[i].getCodiceLotto());
						}
					}
					this.checkLotto = tmpCheckLotto;
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}

		return target;
	}
	
	/**
	 * per gare a lotti con prequalifica gestisci i lotti utilizzabili in offerta 
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	private boolean getLottiPrequalificaOfferteMultiple(
			WizardPartecipazioneHelper helper,
			String username,
			LottoGaraType[] lottiPrequalifica) 
			throws ApsException, XmlException 
	{
		List<LottoGaraType> lottiAbilitati = new ArrayList<LottoGaraType>();
		
		// se la partecipazione e' di un'impresa singola,
		// e' possibile presentare in offerta solo i lotti della propria prequalifica
		// se la partecipazione e' una rti,
		// e' possibile presenzare in offerta tutti i lotti di tutte le offerte (singola+rti)
		boolean impresaSingola = !helper.isRti();
				
		// recupera le comunicazioni FS10 di prequalifica delle offerte...
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(helper.getIdBando());
		criteriRicerca.setChiave3(helper.getProgressivoOfferta());
		//criteriRicerca.setStato(*);
		
		// recupera la comunicazione di prequalifica FS10
		// e verifica il tipo di partecipazione (singola/rti) in modo da
		// predisporre l'elenco dei lotti disponibili per l'offerta...
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager
			.getElencoComunicazioni(criteriRicerca);
		
		String progOfferta = null;
		if(comunicazioni != null && comunicazioni.size() > 0) {
			progOfferta = comunicazioni.get(0).getChiave3();
		}
		
		if(progOfferta != null) {
			// recupera da BO solo i lotti ammessi in prequalifica per la stessa offerta...
			for(int i = 0; i < lottiPrequalifica.length; i++) {
				if( progOfferta.equals(lottiPrequalifica[i].getProgressivoOfferta()) ) {
					lottiAbilitati.add(lottiPrequalifica[i]);
				}
			}
		} else {
			// in caso di nuova offerta abilita tutti i lotti delle offerte di prequalifica
			for(int i = 0; i < lottiPrequalifica.length; i++) {
				lottiAbilitati.add(lottiPrequalifica[i]);
			}
		}
		
		// ricrea le strutture per helper ed action...
		List<String> lottiAmmessiOfferta = new ArrayList<String>();
		LottoGaraType[] lotti = new LottoGaraType[lottiAbilitati.size()]; 
		for (int i = 0; i < lottiAbilitati.size(); i++) {
			lotti[i] = lottiAbilitati.get(i);
			lottiAmmessiOfferta.add(lottiAbilitati.get(i).getCodiceLotto());
		}		
		this.lotti = lotti; 
		helper.setLottiAmmessiInOfferta(lottiAmmessiOfferta);
		
		return true;
	}
	
}
