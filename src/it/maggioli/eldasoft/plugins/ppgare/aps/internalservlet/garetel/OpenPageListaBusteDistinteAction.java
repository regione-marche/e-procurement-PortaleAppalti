package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;


public class OpenPageListaBusteDistinteAction extends EncodedDataAction implements SessionAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -813415978191973920L;
	
	private static final String SESSION_ID_VISUALIZZA_LOTTO 		= "visualizzaLotto";
	private static final String SESSION_ID_DOCUMENTI_LOTTO_PRONTI 	= "documentiLottoPronti";
	private static final String SESSION_ID_APERTURA_LOTTO 			= "aperturaLotto";

	private HashMap<String, Boolean> visualizzaLotto;
	private HashMap<String, Boolean> documentiPronti;
	private HashMap<String, Boolean> aperturaLotto;

	private Map<String, Object> session;
	
	protected IComunicazioniManager comunicazioniManager;
	
	private int operazione;
	private int tipoBusta;
	private String codice;
	private String codiceGara;
	private String progressivoOfferta;
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public IComunicazioniManager getComunicazioniManager() {
		return comunicazioniManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
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

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public HashMap<String, Boolean> getDocumentiPronti() {
		return documentiPronti;
	}

	public void setDocumentiPronti(HashMap<String, Boolean> documentiPronti) {
		this.documentiPronti = documentiPronti;
	}

	public HashMap<String, Boolean> getVisualizzaLotto() {
		return visualizzaLotto;
	}
	
	public void setVisualizzaLotto(HashMap<String, Boolean> visualizzaLotto) {
		this.visualizzaLotto = visualizzaLotto;
	}
		
	public HashMap<String, Boolean> getAperturaLotto() {
		return aperturaLotto;
	}

	public void setAperturaLotto(HashMap<String, Boolean> aperturaLotto) {
		this.aperturaLotto = aperturaLotto;
	}

	/**
	 * ... 
	 */
	public String open() {
		this.setTarget(SUCCESS);

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
				boolean invioOfferta = !domandaPartecipazione; //(this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
				
				RiepilogoBusteHelper riepilogoBusta = (RiepilogoBusteHelper)this
					.session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				
				List<String> lottiAttivi = riepilogoBusta.getListaCompletaLotti();
				
				this.documentiPronti = new LinkedHashMap<String, Boolean>();
				this.visualizzaLotto = new LinkedHashMap<String, Boolean>();
				this.aperturaLotto = new LinkedHashMap<String, Boolean>();

				//if(domandaPartecipazione) {
				//}
				
				if(invioOfferta) {
					List<String> lottiDaProcessare = this.getLottiDaProcessare();
					
					if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
						for(int i = 0; i < lottiAttivi.size();i++) {
							this.visualizzaLotto.put(lottiAttivi.get(i), riepilogoBusta.getBusteEconomicheLotti().get(lottiAttivi.get(i)) != null);							
							this.documentiPronti.put(lottiAttivi.get(i), isDocumentazionePronta(lottiAttivi.get(i), PortGareSystemConstants.BUSTA_ECONOMICA));
							this.aperturaLotto.put(lottiAttivi.get(i), !lottiDaProcessare.contains(lottiAttivi.get(i)));
						}
					} 
					if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
						for(int i = 0; i < lottiAttivi.size();i++) {
							this.visualizzaLotto.put(lottiAttivi.get(i), riepilogoBusta.getBusteTecnicheLotti().get(lottiAttivi.get(i)) != null);
							this.documentiPronti.put(lottiAttivi.get(i), isDocumentazionePronta(lottiAttivi.get(i), PortGareSystemConstants.BUSTA_TECNICA));
							this.aperturaLotto.put(lottiAttivi.get(i), !lottiDaProcessare.contains(lottiAttivi.get(i)));
						}
					}
				}

				// salva in sessione i dati sui lotti
				this.session.put(SESSION_ID_VISUALIZZA_LOTTO, this.visualizzaLotto);
				this.session.put(SESSION_ID_DOCUMENTI_LOTTO_PRONTI, this.documentiPronti);
				this.session.put(SESSION_ID_APERTURA_LOTTO, this.aperturaLotto);

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
	 * ... 
	 */
	private boolean isDocumentazionePronta(String codiceLotto, int tipoBusta) {
		RiepilogoBusteHelper riepilogoBusta = (RiepilogoBusteHelper)this
			.session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
		
		boolean obbligatoriOk = false;
		
		if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA
		   && riepilogoBusta != null
		   && riepilogoBusta.getBusteEconomicheLotti() != null ) {
			
			if(riepilogoBusta.getBusteEconomicheLotti().get(codiceLotto) == null) {
				// in caso di OEPV a costo fisso, non c'è il lotto...
			} else {
				List<DocumentoMancanteBean> documentiMancantiEconomica = riepilogoBusta
					.getBusteEconomicheLotti().get(codiceLotto).getDocumentiMancanti();
			
				obbligatoriOk = true;
				for(int i = 0; i < documentiMancantiEconomica.size() && obbligatoriOk;i++) {
					if(documentiMancantiEconomica.get(i).isObbligatorio()) {
						obbligatoriOk = false;
					}
				}
				if(riepilogoBusta.getPrimoAccessoEconomicheEffettuato().get(codiceLotto) != null) {
					obbligatoriOk = obbligatoriOk && riepilogoBusta.getPrimoAccessoEconomicheEffettuato().get(codiceLotto);
				}
			}
			
			return obbligatoriOk;
		} 
		
		if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA 
		   && riepilogoBusta.getBusteTecnicheLotti() != null) {
			
			obbligatoriOk = true;
			if(riepilogoBusta.getBusteTecnicheLotti().get(codiceLotto) != null) {
				List<DocumentoMancanteBean> documentiMancantiTecnica = riepilogoBusta.getBusteTecnicheLotti().get(codiceLotto).getDocumentiMancanti();
				for(int i = 0; i < documentiMancantiTecnica.size() && obbligatoriOk; i++){
					if(documentiMancantiTecnica.get(i).isObbligatorio()){
						obbligatoriOk = false;
					}
				}
			}
			
			if(riepilogoBusta.getPrimoAccessoTecnicheEffettuato().get(codiceLotto) != null) {
				obbligatoriOk = obbligatoriOk && riepilogoBusta.getPrimoAccessoTecnicheEffettuato().get(codiceLotto);
			}
			
			return obbligatoriOk;
		}
		
		return obbligatoriOk;
	}
	
	/**
	 * ricava l'elenco dei lotti ancora da gestire che non sono da processare 
	 * @throws ApsException 
	 */
	private List<String> getLottiDaProcessare() throws ApsException {
		List<String> lista = new ArrayList<String>();
		
		String codice = (StringUtils.isNotEmpty(this.codiceGara) ? this.codiceGara : this.codice); 
		
		DettaglioComunicazioneType criteri = new DettaglioComunicazioneType(); 
		criteri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteri.setChiave1(this.getCurrentUser().getUsername());
		criteri.setChiave3(this.getProgressivoOfferta());
		criteri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		criteri.setTipoComunicazione(null);
		
		if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
			criteri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
		} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
			criteri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
		} else {
			// NON DOVREBBE MAI SUCCEDERE !!! 
			ApsSystemUtils.getLogger().error("getLottiDaProcessare() tipo busta non gestito (" + this.tipoBusta + ")");
		}
		
		if(StringUtils.isNotEmpty(criteri.getTipoComunicazione())) {
			List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(criteri);
			if (!comunicazioni.isEmpty()) {
				for(int i = 0; i < comunicazioni.size(); i++) {
					if(comunicazioni.get(i).getChiave2().indexOf(codice) >= 0) {
						lista.add(comunicazioni.get(i).getChiave2());
					}
				}
			}
		}
		
		return lista;
	}

}
