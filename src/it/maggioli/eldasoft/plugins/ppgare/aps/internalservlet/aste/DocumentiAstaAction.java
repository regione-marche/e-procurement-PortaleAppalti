package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioFaseAstaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;

public class DocumentiAstaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IAsteManager asteManager;
	
	private String codice;
	private String codiceLotto;
	private DettaglioAstaType dettaglioAsta;
	private Boolean lottiDistinti;
	private LinkedHashMap<LottoGaraType, DettaglioAstaType> lotti;
	private LinkedHashMap<String, List<DettaglioFaseAstaType>> fasiAsta;
	private LinkedHashMap<String, DocumentoAllegatoType[]> allegatiAsta;
	
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

	public Boolean getLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(Boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}

	public LinkedHashMap<LottoGaraType, DettaglioAstaType> getLotti() {
		return lotti;
	}

	public void setLotti(LinkedHashMap<LottoGaraType, DettaglioAstaType> lotti) {
		this.lotti = lotti;
	}
		
	public LinkedHashMap<String, List<DettaglioFaseAstaType>> getFasiAsta() {
		return fasiAsta;
	}

	public void setFasiAsta(LinkedHashMap<String, List<DettaglioFaseAstaType>> fasiAsta) {
		this.fasiAsta = fasiAsta;
	}	

	public LinkedHashMap<String, DocumentoAllegatoType[]> getAllegatiAsta() {
		return allegatiAsta;
	}

	public void setAllegatiAsta(LinkedHashMap<String, DocumentoAllegatoType[]> allegatiAsta) {
		this.allegatiAsta = allegatiAsta;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio asta
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		this.setTarget(SUCCESS);

		try {
			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente 
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {				
								
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty() 
 			             			 ? this.codiceLotto : this.codice);
				
				// per ogni lotto carica le info dell'asta e delle fasi...
				LottoGaraType[] lottiGara = this.bandiManager.getLottiGara(this.codice);

				boolean lottiDistinti = ( 
					lottiGara != null && 
					(lottiGara.length > 1 || (lottiGara.length == 1 && 
							                  lottiGara[0].getCodiceLotto().compareToIgnoreCase(this.codice) != 0))  
				);

				LinkedHashMap<LottoGaraType, DettaglioAstaType> lotti = 
					new LinkedHashMap<LottoGaraType, DettaglioAstaType>();
				
				LinkedHashMap<String, List<DettaglioFaseAstaType>> fasi = 
					new LinkedHashMap<String, List<DettaglioFaseAstaType>>();
				
				LinkedHashMap<String, DocumentoAllegatoType[]> allegati = 
					new LinkedHashMap<String, DocumentoAllegatoType[]>();
				
				if(!lottiDistinti) {
					// --- asta lotto unico ---
					
					DettaglioAstaType asta = this.asteManager
						.getDettaglioAsta(this.codice, userDetails.getUsername());
										
					List<DettaglioFaseAstaType> elencoFasi = this.asteManager
						.getFasiAsta(this.codice);						
					fasi.put(this.codice, elencoFasi);

					DocumentoAllegatoType[] documenti = this.bandiManager
						.getDocumentiInvitoAsta(this.codice, null);					
					allegati.put(this.codice, documenti);
					
				} else {	
					// --- asta a lotti distinti ---
					
					List<DettaglioAstaType> listaAste = 
						this.getLottiInvitatiAsta(codiceGara, userDetails.getUsername());
					
					for(int j = 0; j < listaAste.size(); j++) {
						DettaglioAstaType asta = listaAste.get(j);
						
						String codiceLotto = (asta.getCodiceLotto() != null && !asta.getCodiceLotto().isEmpty()
								? asta.getCodiceLotto() 
								: asta.getCodice() );
						
						for(int i = 0; i < lottiGara.length; i++) {
							if( codiceLotto.equalsIgnoreCase(lottiGara[i].getCodiceLotto()) ) {
						
								List<DettaglioFaseAstaType> elencoFasi = this.asteManager
									.getFasiAsta(lottiGara[i].getCodiceLotto());						
								fasi.put(lottiGara[i].getCodiceLotto(), elencoFasi);
							
								DocumentoAllegatoType[] documenti = this.bandiManager
									.getDocumentiInvitoAsta(this.codice, lottiGara[i].getCodiceLotto());					
								allegati.put(lottiGara[i].getCodiceLotto(), documenti);

								lotti.put(lottiGara[i], asta);
								
								break;
							}
						}
					}					
				}
				
				this.setLotti(lotti);
				this.setFasiAsta(fasi);
				this.setAllegatiAsta(allegati);				
				this.setLottiDistinti(lottiDistinti);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}
	
	/**
	 * recupera la lista dei lotti invitati all'asta
	 * @throws ApsException 
	 */
	private List<DettaglioAstaType> getLottiInvitatiAsta(String codiceGara, String username) 
		throws ApsException {
		
		DettaglioGaraType gara = this.bandiManager.getDettaglioGara(codiceGara);
		
		List<DettaglioAstaType> listaAste = OpenLottiDistintiAction.getLottiInvitatiAsta(
				this.bandiManager, 
				this.asteManager, 
				gara, 
				username);
		
		return listaAste;		
	}
	
}
