package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraLottoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewOffEcoLottiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8485778164267646441L;

	private IBandiManager bandiManager;	
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	private List<EspletGaraLottoType> lotti;
		
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public List<EspletGaraLottoType> getLotti() {
		return lotti;
	}

	public void setLotti(List<EspletGaraLottoType> lotti) {
		this.lotti = lotti;
	}
	
	/**
	 * ... 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				
				List<EspletGaraLottoType> lotti = getLottiAbilitatiGara(dettGara);
				this.setLotti(lotti);
				
			} catch(Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "view");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);	
		}
		
		return this.getTarget();
	}

	/**
	 * recupera l'elenco dei lotti abilitati per la fase di offerta economica 
	 */
	public List<EspletGaraLottoType> getLottiAbilitatiGara(DettaglioGaraType dettaglio) throws ApsException {
		List<EspletGaraLottoType> lotti = null;
		
		if(dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
		   dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {						
			lotti = EspletGaraViewOffEcoLottiAction.getListaLottiAbilitati(
					BustaGara.BUSTA_ECONOMICA
					, dettaglio
					, getCurrentUser().getUsername()
			);
		}
		
		return lotti;
	}
	
	/**
	 * prepara la lista dei lotti abilitati per la gara
	 * @throws ApsException 
	 */
	public static List<EspletGaraLottoType> getListaLottiAbilitati(
			int tipoBusta
			, DettaglioGaraType dettaglioGara
			, String username
	) throws ApsException {	
		List<EspletGaraLottoType> lotti = new ArrayList<EspletGaraLottoType>();
		
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
		
		// estrai i lotti abilitati per la gara...
		List<LottoGaraType> lottiGara = new ArrayList<LottoGaraType>();
		for(int i = 0; i < dettaglioGara.getLotto().length; i++) {
			boolean addLotto = true;
		
			// per le buste economiche si verifica se il lotto va aggiunto alla lista...
			if(tipoBusta == BustaGara.BUSTA_ECONOMICA) {
				if(dettaglioGara.getLotto(i).getCostoFisso() != null 
				   && dettaglioGara.getLotto(i).getCostoFisso() == 1) 
				{
					// una OEPV a costo fisso non ha offerta economica!!!
					addLotto = false;
				}
			}
			
			if(addLotto) 
				// verifica se il lotto in esame e' fittizio ed in tal caso ignoralo
				if(StringUtils.isNotEmpty(dettaglioGara.getLotto(i).getCodiceInterno())) {
					LottoGaraType lotto = new LottoGaraType();
					lotto.setCodiceLotto( dettaglioGara.getLotto(i).getCodiceLotto() );
					lotto.setCodiceInterno( dettaglioGara.getLotto(i).getCodiceInterno() );
					lotto.setOggetto( dettaglioGara.getLotto(i).getOggetto() );
					lottiGara.add(lotto);
				}
		}
		
		// recupera l'elenco degli operatori per tutti i lotti...
		long faseEspletamentoCorrente = -1;
		List<EspletGaraOperatoreType> operatori = null;
		if(tipoBusta == BustaGara.BUSTA_TECNICA) { 
			faseEspletamentoCorrente = 5; 
			operatori = bandiManager.getEspletamentoGaraValTecElencoOperatoriLotto(
					dettaglioGara.getDatiGeneraliGara().getCodice()
					, username
			);
		} else if(tipoBusta == BustaGara.BUSTA_ECONOMICA) {
			faseEspletamentoCorrente = 6;
			operatori = bandiManager.getEspletamentoGaraOffEcoElencoOperatoriLotto(
					dettaglioGara.getDatiGeneraliGara().getCodice()
					, username
			);
		}
		
		if(operatori != null && operatori.size() > 0) {
			for(LottoGaraType lotto : lottiGara) {
				if(lotto.getCodiceLotto() != null) {
					// verifica se tra i lotti associati agli operatori e' presente il "CodiceLotto"
//					int numOperatori = 0;
					for(EspletGaraOperatoreType oper : operatori) {
						if(oper.getLotti() != null && oper.getLotti().length > 0) {
							//boolean trovato = false;
							//int k = 0;
							//while (k < oper.getLotti().length && !trovato) {
							//	trovato = (lotto.getCodiceLotto().equalsIgnoreCase( oper.getLotti(k).getLotto() ));
							//	k++;
							//}
							EspletGaraLottoType lottoTrovato = Arrays.asList(oper.getLotti()).stream()
									.filter(l -> lotto.getCodiceLotto().equalsIgnoreCase(l.getLotto()) 
												 || lotto.getCodiceLotto().equalsIgnoreCase(l.getCodiceInterno()))
									.findFirst()
									.orElse(null);
							
							if(lottoTrovato != null) {
								// lotto trovato...
								// se esiste almeno 1 operatore per questo lotto
								// con fase gara >= sbustamento busta tecnica/economica"
								// non e' necessario verificare anche i lotti degli altri operatori...
								if(lottoTrovato.getFaseGara() != null && lottoTrovato.getFaseGara().longValue() >= faseEspletamentoCorrente) {
									//numOperatori++;
									lotti.add(new EspletGaraLottoType(
									           lotto.getCodiceLotto()		// lotto
									           , lotto.getCodiceInterno()	// codice interno
									           , lotto.getOggetto()			// oggetto 
									           , null						// ammissione
									           , lottoTrovato.getFaseGara()	// fase gara
									));
								}
								break;
							}
						}
					}
					
//					if(numOperatori > 0) {
//						lotti.add(new EspletGaraLottoType(
//					           lotto.getCodiceLotto()		// lotto
//					           , lotto.getCodiceInterno()	// codice interno
//					           , lotto.getOggetto()			// oggetto 
//					           , null						// ammissione
//					           , null						// fase gara
//						));
//					}
				}
			}
		}
		
		return lotti;
	}

}
