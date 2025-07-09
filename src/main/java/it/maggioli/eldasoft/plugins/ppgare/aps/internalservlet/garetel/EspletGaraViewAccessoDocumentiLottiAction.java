package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraLottoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;


public class EspletGaraViewAccessoDocumentiLottiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3145861823147132574L;

	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	private List<EspletGaraLottoType> lotti;
		
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(codice);
				
				// recupera solo i lotti per i quali l'OE ha presentato offerta...
				List<String> lottiOfferta = getLottiPubblicazioneAtti(
						getCurrentUser().getUsername(),
						codice
				);
				
				List<EspletGaraLottoType> lotti = new ArrayList<EspletGaraLottoType>();
				for(int i = 0; i < dettGara.getLotto().length; i++) {
					if(dettGara.getLotto(i).getCodiceInterno() != null 
					    && lottiOfferta.indexOf(dettGara.getLotto(i).getCodiceLotto()) >= 0) 
					{
						EspletGaraLottoType lotto = new EspletGaraLottoType();
						lotto.setLotto(dettGara.getLotto(i).getCodiceLotto());
						lotto.setCodiceInterno(dettGara.getLotto(i).getCodiceInterno());
						lotto.setOggetto(dettGara.getLotto(i).getOggetto());
						lotti.add(lotto);
					}
				}
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
	 * restituisce l'elenco dei lotti presentati in offerta da un OE 
	 */
	private static List<String> getInfoPubblicazioneAtti(
		String username,
		String codiceGara,
		List<EspletGaraOperatoreType> classificate
	) {
		List<String> lottiGara = new ArrayList<String>();
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
			
			EspletamentoElencoOperatoriSearch search = new EspletamentoElencoOperatoriSearch();
			search.setCodice(codiceGara);
			search.setUsername(username);
			List<EspletGaraOperatoreType> imprese = bandiManager.getEspletamentoGaraAccessoDocumentiElencoOperatori(search);			
			if(imprese != null) {
				imprese.stream()
					.filter(i -> i.getLotti() != null) 
					.forEach(i-> Arrays.asList(i.getLotti())
									.stream()
										.map(EspletGaraLottoType::getLotto)
										.filter(lotto -> lottiGara.indexOf(lotto) < 0)
										.forEach(lotto -> lottiGara.add(lotto))
					);
			}
			
			// recupera le classificate per ogni lotto
			if(lottiGara != null && imprese != null && classificate != null) {
				imprese.stream()
					.forEach(i -> classificate.add(i));
			}

		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, null, "getInfoPubblicazioneAtti");
		}
		return lottiGara;
	}
	
	/**
	 * restituisce l'elenco dei lotti della pubblicazione atti 
	 */
	public static List<String> getLottiPubblicazioneAtti(
		String username,
		String codiceGara
	) {
		return getInfoPubblicazioneAtti(username, codiceGara, null); 
	}

	/**
	 * restituisce l'elenco delle classificate+aggiudicataria per la pubblicazione atti (art 36)
	 */
	public static List<EspletGaraOperatoreType> getAmmessePubblicazioneAtti(
			String username, 
			String codiceGara
	) {
		List<EspletGaraOperatoreType> classificate = new ArrayList<EspletGaraOperatoreType>();
		getInfoPubblicazioneAtti(username, codiceGara, classificate);
		return classificate;
	}
	
}
