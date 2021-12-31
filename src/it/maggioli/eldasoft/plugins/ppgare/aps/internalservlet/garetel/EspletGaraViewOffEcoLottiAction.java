package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraLottoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

public class EspletGaraViewOffEcoLottiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private IBandiManager bandiManager;	
	
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
				
				List<EspletGaraLottoType> lotti = EspletGaraViewOffEcoLottiAction
					.getLottiAbilitatiGara(dettGara, this.bandiManager);				
				
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
	public static List<EspletGaraLottoType> getLottiAbilitatiGara(
			DettaglioGaraType dettaglio, 
			IBandiManager bandiManager) throws ApsException 
	{
		List<EspletGaraLottoType> lotti = null;
		
		if(dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
		   dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
			
//			LottoGaraType[] lottiGara = null;
//			if(dettaglio.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {		
//				lottiGara = this.bandiManager.getLottiGaraPlicoUnicoPerRichiesteOfferta(
//						this.getCurrentUser().getUsername(),
//						dettaglio.getDatiGeneraliGara().getCodice());
//			} else {
//				lottiGara = this.bandiManager.getLottiGaraPerRichiesteOfferta(				
//						this.getCurrentUser().getUsername(),
//						dettaglio.getDatiGeneraliGara().getCodice());
//			}
			
			List<LottoGaraType> lottiGara = new ArrayList<LottoGaraType>();			
			for(int i = 0; i < dettaglio.getLotto().length; i++) {
				if(dettaglio.getLotto(i).getCostoFisso() != null 
				   && dettaglio.getLotto(i).getCostoFisso() == 1) {
					// una OEPV a costo fisso non ha offerta economica!!! 
				} else {
					// verifica se il lotto in esame e' fittizio ed in tal caso ignoralo
					if(StringUtils.isNotEmpty(dettaglio.getLotto(i).getCodiceInterno())) {
						LottoGaraType lotto = new LottoGaraType();
						lotto.setCodiceLotto( dettaglio.getLotto(i).getCodiceLotto() );
						lotto.setCodiceInterno( dettaglio.getLotto(i).getCodiceInterno() );
						lotto.setOggetto( dettaglio.getLotto(i).getOggetto() );
						lottiGara.add(lotto);
					}
				}
			}

			// recupera l'elenco degli operatori per tutti i lotti...
			List<EspletGaraOperatoreType> operatori = bandiManager
				.getEspletamentoGaraOffEcoElencoOperatoriLotto(
						dettaglio.getDatiGeneraliGara().getCodice(), 
						null);
			
			// prepara la lista dei lotti abilitati per la gara...
			lotti = new ArrayList<EspletGaraLottoType>();			
			if(operatori != null && operatori.size() > 0) {
				for(int i = 0; i < lottiGara.size(); i++) {				
					if(lottiGara.get(i).getCodiceLotto() != null) {						
	
						// verifica se tra i lotti associati agli operatori è presente il "CodiceLotto"
						int numOperatori = 0;
						for(int j = 0; j < operatori.size(); j++) {
							if(operatori.get(j).getLotti() != null && operatori.get(j).getLotti().length > 0) {
								boolean trovato = false;
								int k = 0;
								while (k < operatori.get(j).getLotti().length && !trovato) {
									trovato = (lottiGara.get(i).getCodiceLotto().equalsIgnoreCase( operatori.get(j).getLotti(k).getLotto() ));
									k++;
								}
								if(trovato) {
									// lotto trovato...
									// se esiste almeno 1 operatore per questo lotto
									// non è necessario verificare anche i lotti degli altri operatori...
									numOperatori++;
									break;
								}
							}
						}
						
						if(numOperatori > 0) {
							EspletGaraLottoType lotto = new EspletGaraLottoType();
							lotto.setLotto(lottiGara.get(i).getCodiceLotto());
							lotto.setCodiceInterno(lottiGara.get(i).getCodiceInterno());
							lotto.setOggetto(lottiGara.get(i).getOggetto());					
							lotti.add(lotto);		
						}					
					}
				}			
			}
		}
		
		return lotti;
	}

}
