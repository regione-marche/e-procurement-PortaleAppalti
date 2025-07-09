package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.util.EspletamentoUtil;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.utils.OrderableField;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants.SESSION_ESPLET_ORDER;
import static java.lang.String.format;

public class EspletGaraViewOffEcoAction extends EncodedDataAction implements SessionAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5311787704219394626L;
	
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
	private Integer tipoOffertaTelematica;
	private Integer nascondiValoriEspletamento;
	private DettaglioGaraType dettaglioGara;
	private boolean hideFiscalCode = false;
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
	
	public String getLotto() {
		return lotto;
	}

	public void setLotto(String lotto) {
		this.lotto = lotto;
	}
	
	public Integer getTipoOffertaTelematica() {
		return tipoOffertaTelematica;
	}

	public void setTipoOffertaTelematica(Integer tipoOffertaTelematica) {
		this.tipoOffertaTelematica = tipoOffertaTelematica;
	}

	public Integer getNascondiValoriEspletamento() {
		return nascondiValoriEspletamento;
	}

	public void setNascondiValoriEspletamento(Integer nascondiValoriEspletamento) {
		this.nascondiValoriEspletamento = nascondiValoriEspletamento;
	}
	
	public DettaglioGaraType getDettaglioGara() {
		return dettaglioGara;
	}

	public void setDettaglioGara(DettaglioGaraType dettaglioGara) {
		this.dettaglioGara = dettaglioGara;
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

	/**
	 * ... 
	 */
	public String view() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)
			&& EspletGaraFasiAction.isOffertaEconomicaAbilitata(this.session, this.codice)) 
		{
			try {
				this.lottiDistinti = (!StringUtils.isEmpty(codiceLotto));
				String codiceGara = (this.lottiDistinti ? this.codiceLotto : this.codice);

				EspletamentoElencoOperatoriSearch search = new EspletamentoElencoOperatoriSearch();
				search.setCodice(codice);
				search.setCodiceLotto(codiceLotto);
				search.setCodiceOperatore(null);
				search.setUsername(getCurrentUser().getUsername());
				this.elencoOperatori = bandiManager.getEspletamentoGaraOffEcoElencoOperatori(search);

				// NB: 
				//	per le gare plico unico offerta unica
				//  l'informazione del soccorso istruttorio puo' essere 
				//  nella riga della lotto fittizio o nella riga del lotto
				List<EspletGaraOperatoreType> soccorsi = this.bandiManager
					.getEspletamentoGaraSoccorsiElencoOperatori(this.codice, this.codiceLotto, "6", null);
			
				EspletGaraViewDocAmmAction.updateSoccorsoIstruttorioList(this.elencoOperatori, soccorsi);
				
				int tipologiaGara = EspletGaraFasiAction.getTipologiaGara(this.session, this.codice);
				if(tipologiaGara == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO) {
					 codiceGara = this.codice;
				}
				
				// recupera le info sul lotto selezionato
				this.lotto = null;
				if(this.lottiDistinti) { 
					this.lotto = EspletGaraViewOffEcoAction.getCodiceInternoLotto(this.codiceLotto, this.bandiManager, this.codice); 					
				}
				
				this.dettaglioGara = this.bandiManager.getDettaglioGara(codiceGara);
				nascondiValoriEspletamento = this.dettaglioGara.getDatiGeneraliGara().getNascondiValoriEspletamento();
				hideFiscalCode = EspletamentoUtil.hasToHideFiscalCode(dettaglioGara);
				this.tipoOffertaTelematica = EspletGaraFasiAction.getTipoOffertaTelematica(this.session, codiceGara);

				this.faseGara = calcolaFaseGara(codiceGara, this.lotto, elencoOperatori);
				
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
	 * The changeOrder method is used to change the order of the elencoOperatori list based on the orderField value.<br/>
	 * It retrieves the orderField from the session and sorts the elencoOperatori list based on the orderField's identifier.<br/>
	 * The sorting is done using the genericComparator method of orderField.getOrderable().<br/>
	 *
	 * Note: This method modifies the elencoOperatori list in-place.<br/>
	 */
	private void changeOrder() {
		// Formattato così per differenziare le gare e le buste
		final String session_identifier = format("%s#%s#%s", SESSION_ESPLET_ORDER, "ECONOMICA", this.codice);
		// Se l'oggetto è nullo, arrivo da fuori, quindi utilizzo il field in sessione
		if (orderField == null) {
			Object session_order = session.get(session_identifier);
			if (session_order != null) orderField = (OrderableField) session_order;
		} else session.put(session_identifier, orderField);

		if (orderField != null) {
			switch (orderField.getIdentifier()) {
				case "PUNTEGGIO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getPunteggioEconomico));
					break;
				case "RIPARAMETRATO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getPunteggioEconomicoRiparametrato));
					break;
				case "RIBASSO":
				case "RIALZO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getRibassoOfferto));
					break;
				case "IMPORTO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getImportoOfferto));
					break;
				case "PLICO":
					elencoOperatori.sort(orderField.getOrderable().fieldComparator(EspletGaraOperatoreType::getNumeroPlico));
					break;
			}
		}
	}

	/**
	 * recupera il codice interno del lotto  
	 * @throws ApsException 
	 */
	public static String getCodiceInternoLotto(String codiceLotto, IBandiManager bandiManager, String codiceGara) throws ApsException {
		String lotto = null;
		DettaglioGaraType dettGara = bandiManager.getDettaglioGara(codiceGara);
		if(dettGara.getLotto() != null) {
			for(int i = 0; i < dettGara.getLotto().length; i++) {
				if(codiceLotto.equals(dettGara.getLotto()[i].getCodiceLotto())) {
					lotto = dettGara.getLotto()[i].getCodiceInterno();
					break;
				}
			}
		}
		return lotto;
	}
	
	/**
	 * @throws ApsException 
	 * 
	 */
	public static Long calcolaFaseGara(String codiceGara, String codiceInternoLotto, List<EspletGaraOperatoreType> elencoOperatori) throws ApsException {
		Long faseGara = -1L;
		
		boolean lottiDistinti = (!StringUtils.isEmpty(codiceInternoLotto));
		if( !lottiDistinti ) {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
			faseGara = bandiManager.getFaseGara(codiceGara);
		} else {
			if(elencoOperatori != null)
				faseGara = elencoOperatori.stream()
					.filter(oper -> oper.getFaseGara() != null)
					.map(oper -> oper.getFaseGara())
					.max(Long::compare)
					.orElse(-1L);
		}
		
		return faseGara;
	}

}
