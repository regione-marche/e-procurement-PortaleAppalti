package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraAccessoDocumentiDocumentoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ... 
 */
public class EspletGaraViewAccessoDocumentiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7562959600762272871L;
		
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	private DettaglioGaraType dettaglioGara;
	private List<EspletGaraAccessoDocumentiDocumentoType> documentiGara; 
	private Long faseGara;	
	private List<EspletGaraOperatoreType> aggiudicatarie;
	private List<EspletGaraOperatoreType> classificate;
	private Boolean lottiDistinti;
	
	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public Long getFaseGara() {
		return faseGara;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}
	
	public DettaglioGaraType getDettaglioGara() {
		return dettaglioGara;
	}

	public void setDettaglioGara(DettaglioGaraType dettaglioGara) {
		this.dettaglioGara = dettaglioGara;
	}

	public List<EspletGaraAccessoDocumentiDocumentoType> getDocumentiGara() {
		return documentiGara;
	}

	public void setDocumentiGara(List<EspletGaraAccessoDocumentiDocumentoType> documentiGara) {
		this.documentiGara = documentiGara;
	}

	public List<EspletGaraOperatoreType> getAggiudicatarie() {
		return aggiudicatarie;
	}

	public void setAggiudicatarie(List<EspletGaraOperatoreType> aggiudicatarie) {
		this.aggiudicatarie = aggiudicatarie;
	}

	public List<EspletGaraOperatoreType> getClassificate() {
		return classificate;
	}

	public void setClassificate(List<EspletGaraOperatoreType> classificate) {
		this.classificate = classificate;
	}
	
	public Boolean getLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(Boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}

	/**
	 * apre la pagina dell'accesso ai documenti art 36 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))
		{
			try {
				lottiDistinti = (StringUtils.isNotEmpty(this.codiceLotto) && !this.codiceLotto.equalsIgnoreCase(codice));
				
				String cod = (lottiDistinti ? this.codiceLotto : this.codice);
				
				// verifica se l'utente ha accesso alla pubblicazione
				EspletamentoElencoOperatoriSearch search = new EspletamentoElencoOperatoriSearch();
				search.setCodice(cod);
				search.setUsername(getCurrentUser().getUsername());
				List<EspletGaraOperatoreType> imprese = bandiManager.getEspletamentoGaraAccessoDocumentiElencoOperatori(search);
				
				if(imprese != null && imprese.size() > 0) {
					// recupera la documentazione di gara
					documentiGara = bandiManager.getEspletamentoGaraAccessoDocumentiAtti(cod, null);
				
					// recupera le aggiudicatarie (aggiudicataria graduatoria=1)
					aggiudicatarie = imprese.stream()
							.filter(i -> "1".equals(i.getGraduatoria()))
							.collect(Collectors.toList());
					
					// recupera le classificate (graduatoria=2)
					classificate = imprese.stream()
							.filter(i -> !"1".equals(i.getGraduatoria()))
							.collect(Collectors.toList());
				}
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

}
