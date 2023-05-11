package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioFaseAstaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
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
public class OpenLottiDistintiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6721832775434415755L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IAsteManager asteManager;
	private INtpManager ntpManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;	
	@Validate(EParamValidation.ACTION)
	private String action;					// classifica | riepilogo
	private LinkedHashMap<LottoDettaglioGaraType, DettaglioAstaType> lotti;

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

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public LinkedHashMap<LottoDettaglioGaraType, DettaglioAstaType> getLotti() {
		return lotti;
	}

	public void setLotti(LinkedHashMap<LottoDettaglioGaraType, DettaglioAstaType> lotti) {
		this.lotti = lotti;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio asta
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String open() {
		this.setTarget(SUCCESS);
		try {
			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente
			UserDetails userDetails = this.getCurrentUser();
			if(null != userDetails
			   && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				DettaglioGaraType gara = this.bandiManager.getDettaglioGara(codice);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_GARA, gara);
				
				boolean lottiDistinti = ( 
					gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
					gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO
				);
				
				// recupera un elenco con l'asta della gara o delle aste 
				// relative ai lotti, se la ditta è invitata ed ammessa...
				List<DettaglioAstaType> aste = OpenLottiDistintiAction.getLottiInvitatiAsta(
						this.bandiManager,
						this.asteManager, 
						gara, 
						userDetails.getUsername());
				
				if(!lottiDistinti) {
					// --- GARA SEMPLICE ---
					//...
				} else {
					// --- GARA A LOTTI ---
					// carica le info d'asta per lotto...
					LinkedHashMap<LottoDettaglioGaraType, DettaglioAstaType> lotti = 
						new LinkedHashMap<LottoDettaglioGaraType, DettaglioAstaType>();
					
					for(int i = 0; i < aste.size(); i++) {
						if( aste.get(i) != null ) {
							for(int j = 0; j < gara.getLotto().length; j++) {
								if( gara.getLotto()[j].getCodiceLotto().equals(aste.get(i).getCodiceLotto()) ) {
									lotti.put(gara.getLotto()[j], aste.get(i));
									break;
								}
							}
						}
					}
					this.setLotti(lotti);
				}
				
				// prepara la destinazione...
				//  - gestioneLottiDistinti.jsp
				//  - classifica.jsp
				//  - riepilogo.jsp
				this.action = "";
				if(gara.getDatiGeneraliGara().isAstaElettronica()) {
					if(gara.getDatiGeneraliGara().isAstaAttiva()) {
						this.action = "classifica";
						if(!lottiDistinti) {
							this.setTarget("successClassifica");
						}
					} else {
						this.action = "riepilogo";
						if(!lottiDistinti) {
							this.setTarget("successRiepilogo");
						}
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "open");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	
	/**
	 * crea l'elenco delle aste attive per una gara/lotti   
	 */
	public static List<DettaglioAstaType> getLottiInvitatiAsta(
			IBandiManager bandiManager, 
			IAsteManager asteManager, 
			DettaglioGaraType gara, 
			String username) 
	{
		// verifica se l'asta associata al lotto ha una fase appena conclusa 
		// o in corso...
		// se non trovi una fase conclusa o in corso non visualizzare l'asta 
		// del lotto
		List<DettaglioAstaType> listaAste = new ArrayList<DettaglioAstaType>();
		DettaglioAstaType asta;
		try {
			Calendar dataAttuale = Calendar.getInstance();
			
			if(gara.getLotto() != null && gara.getLotto().length > 0) {
				// --- GARA A LOTTI ---
				for(int i = 0; i < gara.getLotto().length; i++) {
					int lottoAttivo = 0;
					asta = null;
					
					if( bandiManager.isInvitataAsta(gara.getLotto(i).getCodiceLotto(), username) ) {
						asta = asteManager.getDettaglioAsta(
								gara.getLotto(i).getCodiceLotto(), username);
						
						List<DettaglioFaseAstaType> fasi = asteManager.getFasiAsta(
								gara.getLotto(i).getCodiceLotto());
						
						if(fasi != null) {
							Calendar apertura = null;
							Calendar chiusura = null;
							for(int j = 0; j < fasi.size(); j++) {
								Calendar inizio = fasi.get(j).getDataOraApertura();
								Calendar fine = fasi.get(j).getDataOraChiusura();
							
								if((inizio != null && inizio.compareTo(dataAttuale) <= 0)) {
									if(fine != null && fine.compareTo(dataAttuale) < 0) {
										// fase conclusa
										lottoAttivo++;
										if((apertura == null) || (apertura != null && inizio.after(apertura))) {
											apertura = inizio;
											chiusura = fine;
										}
									}
									if((fine == null || (fine != null && dataAttuale.compareTo(fine) <= 0))) {
										// fase in corso
										lottoAttivo++;
										apertura = inizio;
										chiusura = fine;
										break;
									}
								}
							}
							asta.setDataOraApertura(apertura);
							asta.setDataOraChiusura(chiusura);
						}
					}
					
					if(lottoAttivo > 0) {
						listaAste.add(asta);
					}
				}
			} else {
				// --- GARA SEMPLICE ---
				boolean invitata = bandiManager.isInvitataAsta(
						gara.getDatiGeneraliGara().getCodice(), username);
				if(invitata) {
					asta = asteManager.getDettaglioAsta(
							gara.getDatiGeneraliGara().getCodice(), username);
					listaAste.add(asta);
				}
			}
		} catch(Throwable e) {
			ApsSystemUtils.logThrowable(e, null, "getLottiInvitatiAsta");
		}
		return listaAste;
	}

}
