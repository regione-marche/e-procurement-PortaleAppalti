package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.util.EspletamentoUtil;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.utils.OrderableField;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.List;
import java.util.Map;

import static it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants.SESSION_ESPLET_ORDER;
import static java.lang.String.format;

public class EspletGaraViewValTecAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 7497263297686769639L;
	
	private Map<String, Object> session;
	private IBandiManager bandiManager;	
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;	
	private Long faseGara;
	private Boolean lottiDistinti;
	private List<EspletGaraOperatoreType> elencoOperatori;
	@Validate(EParamValidation.CODICE)
	private String lotto;
	private boolean hideFiscalCode;
	private boolean isConcorsoPrimoGrado;
	private boolean with2Phase;
	/**
	 * The orderField variable is of type OrderableField and is annotated with @Validate because it needs validation.<br/>
	 * It represents an orderable field used for sorting objects.<br/>
	 * Use this variable on an instance of the EspletGaraViewValTecAction class to integrate the sortable columns in a table.<br/>
	 * <br/>
	 * Example in jsp:<br/>
	 * <pre>
	 * &lt;input type="hidden" name="orderField.ordered" value="ASC" /&gt;
	 * &lt;input type="hidden" name="orderField.identifier" value="MY_COLUMN_IDENTIFIER" /&gt;
	 * </pre>
	 * See: espletOrderableColumn.jsp
	 */
	@Validate
	private OrderableField orderField;

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
	
	public Boolean getLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(Boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}

	public List<EspletGaraOperatoreType> getElencoOperatori() {
		return elencoOperatori;
	}

	public void setElencoOperatori(List<EspletGaraOperatoreType> elencoOperatori) {
		this.elencoOperatori = elencoOperatori;
	}

	public OrderableField getOrderField() {
		return orderField;
	}
	public void setOrderField(OrderableField orderField) {
		this.orderField = orderField;
	}
	public boolean getHideFiscalCode() {
		return hideFiscalCode;
	}
	public boolean isConcorsoPrimoGrado() {
		return isConcorsoPrimoGrado;
	}
	public boolean isWith2Phase() {
		return with2Phase;
	}

	/**
	 * ... 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& EspletGaraFasiAction.isValutazioneTecnicaAbilitata(this.session, this.codice)) 
		{
			try {
				DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				hideFiscalCode = EspletamentoUtil.hasToHideFiscalCode(dettGara);
				isConcorsoPrimoGrado = StringUtils.equals("9", dettGara.getDatiGeneraliGara().getIterGara());
				with2Phase = dettGara.getDatiGeneraliGara().getTipoConcorso() != null
						&& dettGara.getDatiGeneraliGara().getTipoConcorso() == 2;


				this.lottiDistinti = (!StringUtils.isEmpty(this.codiceLotto));
				String codiceGara = (this.lottiDistinti ? this.codiceLotto : this.codice);

				EspletamentoElencoOperatoriSearch search = new EspletamentoElencoOperatoriSearch();
				search.setCodice(codice);
				search.setCodiceLotto(codiceLotto);
				search.setCodiceOperatore(null);
				search.setUsername(getCurrentUser().getUsername());
				this.elencoOperatori = bandiManager.getEspletamentoGaraValTecElencoOperatori(search);
				
				// NB: 
				//	per le gare plico unico offerta unica
				//  l'informazione del soccorso istruttorio puo' essere 
				//  nella riga della lotto fittizio o nella riga del lotto
				List<EspletGaraOperatoreType> soccorsi = this.bandiManager
					.getEspletamentoGaraSoccorsiElencoOperatori(this.codice, this.codiceLotto, "5", null);
				
				EspletGaraViewDocAmmAction.updateSoccorsoIstruttorioList(this.elencoOperatori, soccorsi);
				
				int tipologiaGara = EspletGaraFasiAction.getTipologiaGara(this.session, this.codice);
				if(tipologiaGara == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
					 codiceGara = this.codice;
				}
				
				this.lotto = null;
				if(this.lottiDistinti) { 
					this.lotto = EspletGaraViewOffEcoAction.getCodiceInternoLotto(this.codiceLotto, this.bandiManager, this.codice); 					
				}
				
				this.faseGara = EspletGaraViewOffEcoAction.calcolaFaseGara(codiceGara, this.lotto, elencoOperatori);

				changeOrder();
				
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
	 * Changes the order of the list of operators based on the order field.<br/>
	 * If the order field is null, it checks if there is a saved order field in the session and assigns it to orderField.<br/>
	 * Otherwise, it stores the current order field in the session.<br/>
	 *
	 * If the order field is not null, it performs the sorting based on the identifier of the order field.<br/>
	 *
	 * The sorting is performed using the genericComparator method of the orderable field.<br/>
	 * If the identifier is "PUNTEGGIO", it uses the punteggioTecnicoOrder method as the condition for sorting.<br/>
	 * If the identifier is "RIPARAMETRATO", it uses the riparametratoTecnicoOrder method as the condition for sorting.<br/>
	 */
	private void changeOrder() {
		final String session_identifier = format("%s#%s#%s", SESSION_ESPLET_ORDER, "TECNICA", this.codice);
		if (orderField == null) {
			Object session_order = session.get(session_identifier);
			if (session_order != null) orderField = (OrderableField) session_order;
		} else session.put(session_identifier, orderField);

		if (elencoOperatori != null && !elencoOperatori.isEmpty() && orderField != null) {
			switch (orderField.getIdentifier()) {
				case "PUNTEGGIO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getPunteggioTecnico));
					break;
				case "RIPARAMETRATO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getPunteggioTecnicoRiparametrato));
					break;
				case "PLICO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getNumeroPlico));
					break;
			}
		}
	}

}
