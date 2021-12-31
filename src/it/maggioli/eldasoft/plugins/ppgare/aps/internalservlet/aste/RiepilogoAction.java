package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.eldasoft.www.sil.WSAste.DettaglioFaseAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Action per le operazioni sulle aste. 
 *
 * @version 1.0
 * @author ...
 */
public class RiepilogoAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IAsteManager asteManager;	
	private IComunicazioniManager comunicazioniManager;
	
	private String codice;
	private String codiceLotto;
	private DettaglioAstaType dettaglioAsta;		
	private List<DettaglioClassificaType> classifica;
	private List<DettaglioRilancioType> rilanci;
	private boolean abilitaConfermaOfferta; 

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
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

	public boolean isAbilitaConfermaOfferta() {
		return abilitaConfermaOfferta;
	}

	public void setAbilitaConfermaOfferta(boolean abilitaConfermaOfferta) {
		this.abilitaConfermaOfferta = abilitaConfermaOfferta;
	}

	/**
	 * Visualizzazione il riepilogo di un'asta
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
			// personali dell'utente (sia per la gara che per i singoli lotti)
			UserDetails userDetails = this.getCurrentUser();
			if(null != userDetails
			   && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty()
									 ? this.codiceLotto : this.codice);
								
				// metti in sessione i dati della gara...
				DettaglioGaraType gara = this.bandiManager.getDettaglioGara(codice);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_GARA, gara);
				
				// aggiorna i dati per la visualizzazione nella pagina...
				DettaglioAstaType asta = this.asteManager.getDettaglioAsta(
						codiceGara, 
						userDetails.getUsername());

				// trova il numero della fase d'asta in corso o l'ultima conclusa 
				// rispetto alla data corrente...								
				List<DettaglioFaseAstaType> fasi = this.asteManager.getFasiAsta(codiceGara);
				
				String numeroFase = this.getNumeroFase(
						this.asteManager, 
						asta,
						fasi,
						new Date());
				
				// reimposta i dati relativi alla fase da riepilogare...
				if(numeroFase != null) {										
					for(int i = 0; i < fasi.size(); i++) {
						if( numeroFase.equalsIgnoreCase(fasi.get(i).getNumeroFase().toString()) ) {
							asta.setFase(Integer.parseInt(numeroFase));
							asta.setDataOraApertura(fasi.get(i).getDataOraApertura());
							asta.setDataOraChiusura(fasi.get(i).getDataOraChiusura());
							asta.setDurataMinima(fasi.get(i).getDurataMinima());
							asta.setDurataMassima(fasi.get(i).getDurataMassima());
							asta.setTempoBase(fasi.get(i).getTempoBase());
							break;
						}
					}
				}
									
				// recupera la classifica...
				List<DettaglioClassificaType> classifica = null;
				if(asta.getFase() != null) {									
					classifica = this.asteManager.getClassifica(
						asta.getTipoClassifica(), 
						this.codice, 
						this.codiceLotto, 
						userDetails.getUsername(), 
						asta.getFase().toString());
				}
				
				// recupera i rilanci relativi all'asta...
				List<DettaglioRilancioType> rilanci = this.asteManager.getElencoRilanci(
					codice, 
					codiceLotto, 
					userDetails.getUsername(),
					null);				
				
				this.setDettaglioAsta(asta);				
				this.setClassifica(classifica);				
				this.setRilanci(rilanci);				

				// abilita il pulsante dell'invio conferma offerta				
				this.setAbilitaConfermaOfferta(
						(rilanci != null && rilanci.size() > 1) &&		// il I rilancio è sempre l'offerta base
						!ClassificaAction.isConfermaOffertaInviata(
								codiceGara,
								this.getCurrentUser().getUsername(),
								this.comunicazioniManager)
				);						

				// se l'asta è conclusa definitivamente e non ci sono altre fasi, 
				// avvia il wizard per l'invio dell'offerta d'asta...
				if(rilanci == null || (rilanci != null && rilanci.size() <= 0)) {
					// nessun rilancio od offerta iniziale per l'utente...
					// l'utente non è stato invitato all'asta ?!?
					//...
				} else if(this.dettaglioAsta.getTermineAsta() != null && 
						  this.dettaglioAsta.getTermineAsta() > 0) {	
					this.setTarget("successConfermaOfferta"); 
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
	 * Trova il numero della fase attiva o dell'ultima fase conclusa rispetto 
	 * alla data di riferimento  
	 */
	private String getNumeroFase(
			IAsteManager asteManager, 
			DettaglioAstaType asta, 
			List<DettaglioFaseAstaType> fasi, 
			Date dataRiferimeno) 
	{
		String numeroFase = null; 
		if(asta != null) {
			try {
				//String codice = (asta.getCodiceLotto() != null && !asta.getCodiceLotto().isEmpty()
				// 		 ? asta.getCodiceLotto() 
				// 		 : asta.getCodice());
		
				Date d3 = null;
				for(int i = 0; i < fasi.size(); i++) {
					Date d1 = (fasi.get(i).getDataOraApertura() != null ? fasi.get(i).getDataOraApertura().getTime() : null);
					//Date d2 = (fasi.get(i).getDataOraChiusura() != null ? fasi.get(i).getDataOraChiusura().getTime() : null);
					if(d1 != null && d1.compareTo(dataRiferimeno) <= 0) {
						if((d3 == null) ||												// caso iniziale
						   (d3 != null && d1.compareTo(d3) >= 0) ) {				 	// casi successivi 
							numeroFase = fasi.get(i).getNumeroFase().toString();
							d3 = d1;
						}																	
					} 
				}				
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "getNumeroFase");
			}
		}
		return numeroFase;
	}

}
