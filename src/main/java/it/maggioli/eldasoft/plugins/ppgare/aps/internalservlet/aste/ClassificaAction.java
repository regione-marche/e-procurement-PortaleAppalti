package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Action per le operazioni sulle aste. 
 *
 * @version 1.0
 * @author ...
 */
public class ClassificaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 2588046293805499712L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IAsteManager asteManager;
	private INtpManager ntpManager;
	private IComunicazioniManager comunicazioniManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	private DettaglioAstaType dettaglioAsta;
	private List<DettaglioClassificaType> classifica;
	private List<DettaglioRilancioType> rilanci;
	private boolean prezziUnitari;
	private boolean lottiDistinti;
	private boolean abilitaRilancio; 
	private boolean abilitaConfermaOfferta;
	
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
	
	public IAsteManager getAsteManager() {
		return asteManager;
	}

	public void setAsteManager(IAsteManager asteManager) {
		this.asteManager = asteManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}
	
	public DettaglioAstaType getDettaglioAsta() {
		return dettaglioAsta;
	}

	public void setDettaglioAsta(DettaglioAstaType dettaglioAsta) {
		this.dettaglioAsta = dettaglioAsta;
	}

	public List<DettaglioClassificaType> getClassifica() {
		return classifica;
	}

	public void setClassifica(List<DettaglioClassificaType> classifica) {
		this.classifica = classifica;
	}
	
	public List<DettaglioRilancioType> getRilanci() {
		return rilanci;
	}

	public void setRilanci(List<DettaglioRilancioType> rilanci) {
		this.rilanci = rilanci;
	}

	public boolean isPrezziUnitari() {
		return prezziUnitari;
	}

	public void setPrezziUnitari(boolean prezziUnitari) {
		this.prezziUnitari = prezziUnitari;
	}
	
	public boolean isLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}
	
	public boolean isAbilitaRilancio() {
		return abilitaRilancio;
	}

	public void setAbilitaRilancio(boolean abilitaRilancio) {
		this.abilitaRilancio = abilitaRilancio;
	}
	
	public boolean isAbilitaConfermaOfferta() {
		return abilitaConfermaOfferta;
	}

	public void setAbilitaConfermaOfferta(boolean abilitaConfermaOfferta) {
		this.abilitaConfermaOfferta = abilitaConfermaOfferta;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio asta
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		this.setTarget(SUCCESS);

		try {
			this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI);
			this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_NON_SOGGETTE);
			this.session.remove(PortGareSystemConstants.SESSION_ID_RILANCIO_PREZZI_UNITARI_SOGGETTE);
			
			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente 
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty() 
 			             			 ? this.codiceLotto : this.codice);

				// metti in sessione i dati della gara...
				DettaglioGaraType gara = this.bandiManager.getDettaglioGara(codice);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_GARA, gara);
				
				this.setLottiDistinti(this.codiceLotto != null && !this.codiceLotto.isEmpty() && 
						              !this.codiceLotto.equalsIgnoreCase(this.codice));
				
				DettaglioAstaType asta = this.asteManager
						.getDettaglioAsta(codiceGara, userDetails.getUsername());
				this.setDettaglioAsta(asta);
				
				List<DettaglioClassificaType> classifica = null;
				if(asta != null && asta.getTipoClassifica() != null && asta.getFase() != null ) { 
					classifica = this.asteManager.getClassifica(
						asta.getTipoClassifica().intValue(), 
						this.codice, 
						this.codiceLotto, 
						userDetails.getUsername(), 
						asta.getFase().toString());
				}
				this.setClassifica(classifica);
				

				// recupera l'elenco dei rilanci
				List<DettaglioRilancioType> rilanci = this.asteManager.getElencoRilanci(
						this.codice, 
						this.codiceLotto, 
						null, 
						asta.getFase().toString());

				this.setRilanci(rilanci);
				
				// abilita il pulsante dei rilanci
				// verifica se esiste almeno un rilancio per la ditta...
				boolean abilitaRilancio = false;
				if(rilanci != null) {
					for(int i = 0; i < rilanci.size(); i++) {
						if(userDetails.getUsername().equalsIgnoreCase(rilanci.get(i).getUsername())) {
							abilitaRilancio = true;
							break;
						}
					}
				}
				this.setAbilitaRilancio(abilitaRilancio);
				
				// abilita il pulsante dell'invio conferma offerta
				// NB: il I rilancio è sempre l'offerta base
				this.setAbilitaConfermaOfferta(
						(rilanci != null && rilanci.size() > 1) &&
						!ClassificaAction.isConfermaOffertaInviata(
								codiceGara,
								this.getCurrentUser().getUsername(),
								this.comunicazioniManager)
				);
				
				// rimuovi dalla sessione eventuali dati vecchi
				// e verifica se esistono dei dati sui prezzi unitari...
				this.prezziUnitari = false;
				List<VoceDettaglioAstaType> voci = this.asteManager.getPrezziUnitariPrimoRilancio(
						this.codice, 
						this.codiceLotto, 
						userDetails.getUsername());
				if(voci != null && voci.size() > 0) {
					this.prezziUnitari = true;
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}
	
	/**
	 * ...
	 */
	public static boolean isConfermaOffertaInviata(
			String codiceGara,
			String username,
			IComunicazioniManager comunicazioniManager) 
	{
		boolean offertaInviata = false;
		try {
			// verifica se esiste la comunicazione di supporto 
			// per le offerte d'asta FS13 in stato BOZZA (o PROCESSATA)...
			List<String> stati = new ArrayList<String>(); 
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		
			DettaglioComunicazioneType dettComunicazioneFS13 = ComunicazioniUtilities
				.retrieveComunicazioneConStati(
					comunicazioniManager,
					username,
					codiceGara,
					null, 
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA,
					stati);
			if(dettComunicazioneFS13 != null) {
				offertaInviata = (! CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(dettComunicazioneFS13.getStato()));
			}
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, null, "isConfermaOffertaInviata");
		}
		return offertaInviata; 
	}
	
}
