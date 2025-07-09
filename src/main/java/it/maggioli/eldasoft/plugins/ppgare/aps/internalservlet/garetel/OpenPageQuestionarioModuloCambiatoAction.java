package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

/**
 * ... 
 *
 * @author ...
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageQuestionarioModuloCambiatoAction extends AbstractOpenPageAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7409783449393966071L;
	
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	@Validate(EParamValidation.DIGIT)
	private String operazione;
	private int tipoBusta;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	private Long idQuestionario;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String descrizioneBusta;

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
	
	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}
	
	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}	

	public Long getIdQuestionario() {
		return idQuestionario;
	}

	public void setIdQuestionario(Long idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getDescrizioneBusta() {
		return this.descrizioneBusta;
	}

	/**
	 * ...
	 */
	@Override
	public String openPage() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
//System.out.println(CollegaUtenzaSSOAction.generaToken("URBANS"));
//System.out.println(CollegaUtenzaSSOAction.generaToken("00205010507"));
				boolean garaLotti = false;
				this.idQuestionario = null;
				this.descrizioneBusta = null;
				RiepilogoBustaBean busta = null;

				RiepilogoBusteHelper bustaRiepilogativa = GestioneBuste.getBustaRiepilogoFromSession().getHelper();

				if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
					this.descrizioneBusta = PortGareSystemConstants.BUSTA_PRE;
					busta = bustaRiepilogativa.getBustaPrequalifica();
				} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
					this.descrizioneBusta = PortGareSystemConstants.BUSTA_AMM;
					busta = bustaRiepilogativa.getBustaAmministrativa();
				} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
					this.descrizioneBusta = PortGareSystemConstants.BUSTA_TEC;
					if( !garaLotti ) {
						busta = bustaRiepilogativa.getBustaTecnica();
					} else {
						// ...
					}
				} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
					this.descrizioneBusta = PortGareSystemConstants.BUSTA_ECO;
					if( !garaLotti ) {
						busta = bustaRiepilogativa.getBustaEconomica();
					} else {
						// ...
					}
				}
				
				if(busta != null) {
					this.idQuestionario = busta.getQuestionarioId();
				}
					
				String cod = (StringUtils.isNotEmpty(bustaRiepilogativa.getCodice())
							  ? bustaRiepilogativa.getCodice() 
							  : bustaRiepilogativa.getCodiceGara());
				this.setCodice(cod);
				
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
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
