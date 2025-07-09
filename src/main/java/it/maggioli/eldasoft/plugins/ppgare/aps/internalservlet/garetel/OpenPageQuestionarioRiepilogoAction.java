package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import org.apache.commons.lang3.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di apertura del riepilogo di un questionario.
 *
 * @author 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageQuestionarioRiepilogoAction extends OpenPageDocumentiBustaAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7614046361855883461L;

	private Boolean rettifica;
	
	
	public Boolean getRettifica() {
		return rettifica;
	}
	
	public void setRettifica(Boolean rettifica) {
		this.rettifica = rettifica;
	}

	/**
	 * (QCompiler) apri la pagina deil riepilogo (documenti allegati)  
	 */
	@Override
	public String openPage() {
		String target = super.openPage();
		
		if(SUCCESS_QUESTIONARIO.equals(target)) {
			try {
				this.rettifica = false;
				
				GestioneBuste buste = GestioneBuste.getFromSession();
				WizardDocumentiBustaHelper helper = buste.getBusta(this.getTipoBusta()).getHelperDocumenti();
				RiepilogoBusteHelper riepilogo = buste.getBustaRiepilogo().getHelper();
								
				RiepilogoBustaBean busta = this.getBustaRiepilogo(helper, riepilogo);
				
				// verifica se la modulistica del questionario e' cambiata in BO
				long idModello = (helper.getQuestionarioAssociato() != null ? helper.getQuestionarioAssociato().getId() : 0);
				long idBusta = (busta != null ? busta.getQuestionarioId() : 0);
				if(idModello != idBusta) {
					this.rettifica = true;
				}
				
				this.setTarget(SUCCESS);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.setTarget(target);
		}
		
		return this.getTarget();
	}

	/**
	 * recupera la busta di riepilogo relativa al wizard dei documenti 
	 */
	private RiepilogoBustaBean getBustaRiepilogo(
			WizardDocumentiBustaHelper helper, 
			RiepilogoBusteHelper riepilogo) 
	{
		RiepilogoBustaBean busta = null;
		
		boolean garaLotti = false;
		if(StringUtils.isNotEmpty(helper.getCodice()) && StringUtils.isNotEmpty(helper.getCodiceGara())) {
			garaLotti = !helper.getCodice().equals(helper.getCodiceGara());
		}
		
		if(helper.getTipoBusta() == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
			busta = riepilogo.getBustaPrequalifica();
		} else if(helper.getTipoBusta() == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			busta = riepilogo.getBustaAmministrativa();
		} else if(helper.getTipoBusta() == PortGareSystemConstants.BUSTA_TECNICA) {
			if( !garaLotti ) {
				busta = riepilogo.getBustaTecnica();
			} else {
				// gara a lotti
				busta = riepilogo.getBusteTecnicheLotti().get(this.getCodice());
			}
		} else if(helper.getTipoBusta() == PortGareSystemConstants.BUSTA_ECONOMICA) {
			if( !garaLotti ) {
				busta = riepilogo.getBustaEconomica();
			} else {
				// gara a lotti
				busta = riepilogo.getBusteEconomicheLotti().get(this.getCodice());
			}
		}
		return busta; 
	}
	
}
