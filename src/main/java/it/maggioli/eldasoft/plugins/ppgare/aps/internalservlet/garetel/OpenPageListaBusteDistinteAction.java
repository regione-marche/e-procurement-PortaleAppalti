package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
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


@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
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
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	@Validate(EParamValidation.DIGIT)
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
				GestioneBuste buste = GestioneBuste.getFromSession();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
				RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper();
				
				this.codiceGara = buste.getCodiceGara();
				
				boolean domandaPartecipazione = buste.isDomandaPartecipazione();
				boolean invioOfferta = buste.isInvioOfferta();
				
				List<String> lottiAttivi = riepilogo.getListaCompletaLotti();
				
				this.documentiPronti = new LinkedHashMap<String, Boolean>();
				this.visualizzaLotto = new LinkedHashMap<String, Boolean>();
				this.aperturaLotto = new LinkedHashMap<String, Boolean>();

				//if(domandaPartecipazione) {
				//}
				
				if(invioOfferta) {
					List<String> lottiDaProcessare = this.getLottiDaProcessare();
					
					if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
						for(int i = 0; i < lottiAttivi.size();i++) {
							String codiceLotto = lottiAttivi.get(i);
							this.visualizzaLotto.put(codiceLotto, riepilogo.getBusteEconomicheLotti().get(codiceLotto) != null);							
							this.documentiPronti.put(codiceLotto, isDocumentazionePronta(codiceLotto, PortGareSystemConstants.BUSTA_ECONOMICA));
							this.aperturaLotto.put(codiceLotto, !lottiDaProcessare.contains(codiceLotto));
						}
					} 
					if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
						for(int i = 0; i < lottiAttivi.size();i++) {
							String codiceLotto = lottiAttivi.get(i);
							this.visualizzaLotto.put(codiceLotto, riepilogo.getBusteTecnicheLotti().get(codiceLotto) != null);
							this.documentiPronti.put(codiceLotto, isDocumentazionePronta(codiceLotto, PortGareSystemConstants.BUSTA_TECNICA));
							this.aperturaLotto.put(codiceLotto, !lottiDaProcessare.contains(codiceLotto));
						}
					}
				}

				// salva in sessione i dati sui lotti
				this.session.put(SESSION_ID_VISUALIZZA_LOTTO, this.visualizzaLotto);
				this.session.put(SESSION_ID_DOCUMENTI_LOTTO_PRONTI, this.documentiPronti);
				this.session.put(SESSION_ID_APERTURA_LOTTO, this.aperturaLotto);

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "open");
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
		BustaRiepilogo bustaRiepilogo = GestioneBuste.getBustaRiepilogoFromSession();
		RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper(); 
		
		boolean obbligatoriOk = false;
		
		if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA
		   && riepilogo != null
		   && riepilogo.getBusteEconomicheLotti() != null) 
		{	
			RiepilogoBustaBean riepilogoLotto = riepilogo.getBusteEconomicheLotti().get(codiceLotto);
			if(riepilogoLotto == null) {
				// in caso di OEPV a costo fisso, non c'è il lotto...
			} else {
				List<DocumentoMancanteBean> documentiMancantiEconomica = riepilogoLotto.getDocumentiMancanti();			
				obbligatoriOk = true;
				for(int i = 0; i < documentiMancantiEconomica.size() && obbligatoriOk;i++) {
					if(documentiMancantiEconomica.get(i).isObbligatorio()) {
						obbligatoriOk = false;
					}
				}
				if(riepilogo.getPrimoAccessoEconomicheEffettuato().get(codiceLotto) != null) {
					obbligatoriOk = obbligatoriOk && riepilogo.getPrimoAccessoEconomicheEffettuato().get(codiceLotto);
				}
				if (riepilogoLotto.isQuestionarioPresente()) {
					// nel caso di questionario, non ci sono documenti richiesti, 
					// pertanto va controllato se il questionario e' completato
					obbligatoriOk = obbligatoriOk && riepilogoLotto.isQuestionarioCompletato();
				}
			}
			return obbligatoriOk;
		} 
		
		if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA 
		   && riepilogo != null
		   && riepilogo.getBusteTecnicheLotti() != null) 
		{
			RiepilogoBustaBean riepilogoLotto = riepilogo.getBusteTecnicheLotti().get(codiceLotto);					
			if(riepilogoLotto != null) {
				List<DocumentoMancanteBean> documentiMancantiTecnica = riepilogoLotto.getDocumentiMancanti();
				obbligatoriOk = true;
				for(int i = 0; i < documentiMancantiTecnica.size() && obbligatoriOk; i++){
					if(documentiMancantiTecnica.get(i).isObbligatorio()){
						obbligatoriOk = false;
					}
				}
				if(riepilogo.getPrimoAccessoTecnicheEffettuato().get(codiceLotto) != null) {
					obbligatoriOk = obbligatoriOk && riepilogo.getPrimoAccessoTecnicheEffettuato().get(codiceLotto);
				}
	            if(riepilogoLotto.isQuestionarioPresente()) {
	            	// nel caso di questionario, non ci sono documenti richiesti, 
	            	// pertanto va controllato se il questionario e' completato
	            	obbligatoriOk = obbligatoriOk && riepilogoLotto.isQuestionarioCompletato();
	            }
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
